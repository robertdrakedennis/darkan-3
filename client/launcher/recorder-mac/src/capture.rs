//! Binary capture format + thread-safe writer.
//!
//! The client is multithreaded (the game socket I/O, the JS5 worker, and the
//! login thread run concurrently), so every append must be guarded. We hold a
//! single global `Mutex<Option<Writer>>` and serialize whole records under it.
//! The hot path is: format a small header on the stack, take the lock, write
//! the header + payload, drop the lock. No allocation on the hot path.
//!
//! ## On-disk format
//!
//! A capture file is a sequence of length-prefixed framed records. Every record
//! is self-describing so the offline deframer can walk the file without any
//! external schema:
//!
//! ```text
//! magic        u32  LE  0x444B5243  ("DKRC")  — file header, once
//! version      u16  LE  1
//! pid          u32  LE  the recorded process pid
//! (then a stream of records:)
//!   rec_type   u8       one of RecType below
//!   ts_nanos   u64  LE  monotonic timestamp (mach_absolute_time, ns)
//!   ... type-specific fields ...
//! ```
//!
//! ### RecType::Io (0x01) — a recv/read/send/write/recvfrom/sendto result
//! ```text
//!   fd         i32  LE
//!   dir        u8       0 = inbound (recv/read/recvfrom), 1 = outbound (send/write/sendto)
//!   syscall    u8       SyscallKind: which libc fn produced this
//!   len        u32  LE  number of payload bytes that follow
//!   bytes      [u8; len]
//! ```
//!
//! ### RecType::Connect (0x02) — a connect() with an AF_INET/AF_INET6 peer
//! ```text
//!   fd         i32  LE
//!   family     u8       AF_INET (2) or AF_INET6 (30)
//!   port       u16  LE  host byte order
//!   addr_len   u8       4 (v4) or 16 (v6)
//!   addr       [u8; addr_len]
//! ```
//!
//! ### RecType::Close (0x03) — a close()
//! ```text
//!   fd         i32  LE
//! ```
//!
//! ### RecType::Seeds (0x04) — the 4 C2S ISAAC seeds captured at the seed hook
//! ```text
//!   fd_hint    i32  LE  -1 (we don't always know the fd at hook time)
//!   seed0..3   i32  LE  ×4 — the RAW client seeds (S2C = each + 50)
//! ```
//!
//! ### RecType::Note (0x05) — a freeform ASCII note (hook status, etc.)
//! ```text
//!   len        u16  LE
//!   text       [u8; len]
//! ```

use std::fs::{File, OpenOptions};
use std::io::Write;
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::Mutex;

const FILE_MAGIC: u32 = 0x444B_5243; // "DKRC" (Darkan ReCorder)
const FILE_VERSION: u16 = 1;

#[repr(u8)]
#[derive(Clone, Copy)]
pub enum RecType {
    Io = 0x01,
    Connect = 0x02,
    Close = 0x03,
    Seeds = 0x04,
    Note = 0x05,
}

/// Which libc function produced an `Io` record. Lets the deframer distinguish
/// stream sockets from anything unexpected without guessing.
#[repr(u8)]
#[derive(Clone, Copy)]
pub enum SyscallKind {
    Recv = 0,
    Read = 1,
    Recvfrom = 2,
    Send = 3,
    Write = 4,
    Sendto = 5,
}

/// Inbound (client receives) vs outbound (client sends).
pub const DIR_IN: u8 = 0;
pub const DIR_OUT: u8 = 1;

struct Writer {
    file: File,
}

static WRITER: Mutex<Option<Writer>> = Mutex::new(None);

/// Gate checked by the interpose hot path BEFORE doing any capture work. dyld
/// wires the interpose section during fixups — *before* the ctor runs — so the
/// client's earliest `read`/`close`/`recv` calls can land in our replacements
/// before the writer (or even the Rust runtime backing the writer) is ready.
/// We therefore keep every replacement a pure passthrough until the ctor calls
/// [arm], guaranteeing the capture path is never entered prematurely.
static ARMED: AtomicBool = AtomicBool::new(false);

/// Arm the capture path. Called at the very end of the ctor, once the writer is
/// installed. Until this is set, all `record_*` fns no-op.
pub fn arm() {
    ARMED.store(true, Ordering::Release);
}

/// True once [arm] has run. The interposes check this first (a single relaxed
/// atomic load) so the pre-arm hot path is as cheap as possible.
#[inline(always)]
pub fn is_armed() -> bool {
    ARMED.load(Ordering::Acquire)
}

/// Monotonic nanosecond timestamp via `mach_absolute_time` scaled by the
/// timebase. Used so the deframer can order records and measure inter-arrival
/// gaps without wall-clock skew.
fn now_nanos() -> u64 {
    use std::sync::atomic::{AtomicU64, Ordering};
    // Cache the timebase numer/denom (mach_timebase_info) — it never changes.
    static NUMER: AtomicU64 = AtomicU64::new(0);
    static DENOM: AtomicU64 = AtomicU64::new(0);

    #[repr(C)]
    struct MachTimebaseInfo {
        numer: u32,
        denom: u32,
    }
    extern "C" {
        fn mach_absolute_time() -> u64;
        fn mach_timebase_info(info: *mut MachTimebaseInfo) -> libc::c_int;
    }

    let mut numer = NUMER.load(Ordering::Relaxed);
    let mut denom = DENOM.load(Ordering::Relaxed);
    if denom == 0 {
        let mut tb = MachTimebaseInfo { numer: 0, denom: 0 };
        unsafe {
            mach_timebase_info(&mut tb);
        }
        numer = tb.numer.max(1) as u64;
        denom = tb.denom.max(1) as u64;
        NUMER.store(numer, Ordering::Relaxed);
        DENOM.store(denom, Ordering::Relaxed);
    }
    let t = unsafe { mach_absolute_time() };
    // ns = ticks * numer / denom
    (t as u128 * numer as u128 / denom as u128) as u64
}

/// Open the capture file and write the file header. Called once from the ctor.
/// Returns the resolved path on success (for the load note), or an error
/// string. Never panics — the recorder must not destabilize the client.
pub fn init(path: &str) -> Result<String, String> {
    let mut file = OpenOptions::new()
        .create(true)
        .write(true)
        .truncate(true)
        .open(path)
        .map_err(|e| format!("open capture file {path}: {e}"))?;

    let mut hdr = [0u8; 4 + 2 + 4];
    hdr[0..4].copy_from_slice(&FILE_MAGIC.to_le_bytes());
    hdr[4..6].copy_from_slice(&FILE_VERSION.to_le_bytes());
    let pid = std::process::id();
    hdr[6..10].copy_from_slice(&pid.to_le_bytes());
    file.write_all(&hdr)
        .map_err(|e| format!("write capture header: {e}"))?;
    file.flush().ok();

    let mut guard = WRITER.lock().unwrap_or_else(|p| p.into_inner());
    *guard = Some(Writer { file });
    Ok(path.to_string())
}

/// Append a framed record. `header` is the per-type fixed header (already
/// including rec_type + ts is prepended here), `payload` the variable tail.
/// All writes for one record happen under a single lock so concurrent client
/// threads never interleave a record. Errors are swallowed — a failed write
/// must not crash the client; at worst the capture is truncated.
#[inline]
fn append(rec_type: RecType, body: &[u8], payload: &[u8]) {
    // Hard gate: never touch the writer/lock before the ctor armed us. This is
    // load-bearing — dyld may route the client's pre-ctor syscalls through our
    // interposes, and entering the capture path then can fault.
    if !is_armed() {
        return;
    }
    let mut guard = match WRITER.lock() {
        Ok(g) => g,
        Err(p) => p.into_inner(), // a poisoned lock still lets us keep recording
    };
    let w = match guard.as_mut() {
        Some(w) => w,
        None => return, // recording disabled / not initialized → no-op
    };
    // [rec_type u8][ts u64][body][payload]
    let mut prefix = [0u8; 1 + 8];
    prefix[0] = rec_type as u8;
    prefix[1..9].copy_from_slice(&now_nanos().to_le_bytes());
    // Best-effort: ignore I/O errors (the client must not die over a capture write).
    let _ = w.file.write_all(&prefix);
    if !body.is_empty() {
        let _ = w.file.write_all(body);
    }
    if !payload.is_empty() {
        let _ = w.file.write_all(payload);
    }
}

/// Record an inbound/outbound I/O result. `bytes` is the actual transferred
/// slice (length == the syscall's return value). Called from the interposes.
#[inline]
pub fn record_io(fd: i32, dir: u8, syscall: SyscallKind, bytes: &[u8]) {
    let mut body = [0u8; 4 + 1 + 1 + 4];
    body[0..4].copy_from_slice(&fd.to_le_bytes());
    body[4] = dir;
    body[5] = syscall as u8;
    body[6..10].copy_from_slice(&(bytes.len() as u32).to_le_bytes());
    append(RecType::Io, &body, bytes);
}

/// Record a connect() to an IPv4/IPv6 peer (so the deframer can attribute fds
/// to JS5/lobby/world by port). Non-INET families are ignored by the caller.
#[inline]
pub fn record_connect(fd: i32, family: u8, port: u16, addr: &[u8]) {
    let mut body = [0u8; 4 + 1 + 2 + 1];
    body[0..4].copy_from_slice(&fd.to_le_bytes());
    body[4] = family;
    body[5..7].copy_from_slice(&port.to_le_bytes());
    body[7] = addr.len() as u8;
    append(RecType::Connect, &body, addr);
}

/// Record a close() so per-fd state can be flushed/reset by the deframer.
#[inline]
pub fn record_close(fd: i32) {
    let body = fd.to_le_bytes();
    append(RecType::Close, &body, &[]);
}

/// Record the 4 RAW C2S ISAAC seeds captured at the seed hook (S2C = each + 50).
pub fn record_seeds(fd_hint: i32, seeds: [i32; 4]) {
    let mut body = [0u8; 4 + 16];
    body[0..4].copy_from_slice(&fd_hint.to_le_bytes());
    for (i, s) in seeds.iter().enumerate() {
        let off = 4 + i * 4;
        body[off..off + 4].copy_from_slice(&s.to_le_bytes());
    }
    append(RecType::Seeds, &body, &[]);
}

/// Record a freeform ASCII note (hook-install status, locator results, etc.).
pub fn record_note(text: &str) {
    let bytes = text.as_bytes();
    let len = bytes.len().min(u16::MAX as usize);
    let body = (len as u16).to_le_bytes();
    append(RecType::Note, &body, &bytes[..len]);
}
