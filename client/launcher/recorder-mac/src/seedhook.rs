//! Inline hook for the ISAAC seed source — the ONE Rosetta-risk surface.
//!
//! The offline deframer cannot de-obfuscate opcodes without the 4 C2S ISAAC
//! seeds (S2C = each + 50). There is **no interpose escape hatch**: the review
//! (B1) proved the client has no exported `RSA_*`/`SSL_*`/`BN_*` symbol — RSA is
//! Jagex's own inlined `jag::math::BigInteger`. So the seeds MUST come from an
//! inline hook (or, on our server, the server log — see the validation path).
//!
//! ## What we hook and why this point
//!
//! Per `docs/binary/patch-targets-948-mac.md` §P4, `SendLoginPacket_xplat948`
//! derives the server seeds from the client seeds with a single SSE2 `PADDD`:
//! ```text
//!   1000cb88e  f3 41 0f 6f 46 48      MOVDQU XMM0,[R14+0x48]   ; 4× client ISAAC seeds
//!   1000cb894  66 0f fe 05 <disp32>   PADDD  XMM0,[+50,+50,+50,+50]
//!   1000cb89c  66 0f 7f 45 b0         MOVDQA [RBP-0x50],XMM0   ; server seeds = client+50
//! ```
//! We hook the **`MOVDQU XMM0,[R14+0x48]`** instruction (the one that loads the
//! client seeds). At that instant R14 points at the login-block builder's state
//! struct and the 4 RAW client seeds live at `[R14+0x48]`. This is the ideal
//! hook point for two reasons:
//!   1. It yields the seeds we want (the RAW C2S seeds — the deframer adds 50
//!      itself for S2C, mirroring the proxy).
//!   2. The instruction has **no RIP-relative operand** (it is R14-relative), so
//!      relocating it into our trampoline is a verbatim copy — the single
//!      biggest correctness risk in an x86_64 inline hook (RIP-relative operand
//!      fixups) does not apply here. §11 of the plan calls for exactly this:
//!      "prefer a hook point whose first instructions are simple (no RIP-relative
//!      operands to relocate)."
//!
//! ## Locating it in any build (pattern-scan, not a hardcoded address)
//!
//! The prod build has shifted addresses, so we scan rather than hardcode:
//!   1. Find the 16-byte `[50,50,50,50]` constant (`32 00 00 00` ×4) in the
//!      slid `__const`/`__TEXT` range. Revision-stable (the +50 contract never
//!      changes — confirmed on every platform).
//!   2. Scan `__text` for `66 0f fe 05 <disp32>` (`PADDD XMM0,[rip+disp32]`)
//!      whose RIP-relative target (`next_instr_addr + disp32`) equals that
//!      constant's address. That uniquely identifies the PADDD inside
//!      SendLoginPacket.
//!   3. The `MOVDQU XMM0,[R14+0x48]` (`f3 41 0f 6f 46 48`) sits immediately
//!      before the PADDD. We confirm those 6 bytes are right there and hook them.
//!
//! ## The trampoline (and the Rosetta hazard)
//!
//! We overwrite the 6-byte MOVDQU with a 5-byte `JMP rel32` to our stub (+1 pad
//! byte). Our stub: snapshot `[R14+0x48]` (4 ints) into a static, call the Rust
//! recorder, re-execute the displaced MOVDQU verbatim, then `JMP` back to the
//! instruction after it. The stub is mmap'd RWX.
//!
//! **This is the genuine Rosetta-AOT hazard** (review B8): writing a control-flow
//! JMP into `__TEXT` of a Rosetta-translated process is materially different
//! from the patcher's static-immediate patch, and nobody has proven it on this
//! client. Mitigations:
//!   * Install from the `#[ctor]`, long before SendLoginPacket is first called
//!     (login time), so first translation of that function sees patched bytes.
//!   * `sys_icache_invalidate` after writing (cheap insurance).
//!   * A **no-op-trampoline self-test** (`selftest_noop_trampoline`) the ctor
//!     runs first: it builds a tiny RWX stub `ret`, calls it, and confirms we
//!     survive — proving RWX-exec works under Rosetta before we touch `__TEXT`.
//!   * If anything fails to locate or install, we emit a clear Note and degrade
//!     to interpose-only (the raw streams are still captured; opcodes just can't
//!     be decoded for prod — on our server the seeds come from the server log).

use crate::capture;
use crate::image::Segment;
use mach2::kern_return::KERN_SUCCESS;
use mach2::traps::mach_task_self;
use mach2::vm::mach_vm_protect;
use mach2::vm_prot::{VM_PROT_COPY, VM_PROT_EXECUTE, VM_PROT_READ, VM_PROT_WRITE};
use memchr::memmem::Finder;
use std::sync::atomic::{AtomicI32, Ordering};

extern "C" {
    fn sys_icache_invalidate(start: *mut libc::c_void, len: libc::size_t);
}

/// The packed `[50,50,50,50]` ISAAC delta constant (4× little-endian int32 50).
const ISAAC_DELTA_CONST: &[u8] = &[
    0x32, 0x00, 0x00, 0x00, 0x32, 0x00, 0x00, 0x00, 0x32, 0x00, 0x00, 0x00, 0x32, 0x00, 0x00, 0x00,
];

/// `PADDD XMM0, [rip+disp32]` opcode prefix (`66 0f fe 05`); the next 4 bytes
/// are the signed disp32 we resolve against the delta constant.
const PADDD_RIP_PREFIX: &[u8] = &[0x66, 0x0f, 0xfe, 0x05];

/// `MOVDQU XMM0, [R14+0x48]` (`f3 41 0f 6f 46 48`) — the client-seed load that
/// immediately precedes the PADDD. This is the 6-byte instruction we hook.
const MOVDQU_R14_48: &[u8] = &[0xf3, 0x41, 0x0f, 0x6f, 0x46, 0x48];
const HOOK_INSN_LEN: usize = 6;

/// Captured RAW C2S seeds, published by the stub. -1 = not yet captured.
/// The stub writes these via a plain store (it runs on the login thread); the
/// Rust side reads them once and forwards to the capture file.
static SEED0: AtomicI32 = AtomicI32::new(0);
static SEED1: AtomicI32 = AtomicI32::new(0);
static SEED2: AtomicI32 = AtomicI32::new(0);
static SEED3: AtomicI32 = AtomicI32::new(0);
static SEEDS_READY: AtomicI32 = AtomicI32::new(0);

/// Outcome of attempting to install the seed hook, for honest reporting.
pub enum HookResult {
    /// Hook located and trampoline installed. Seeds will be captured at login.
    Installed { hook_addr: usize },
    /// Could not locate the pattern in this build. Degrade to interpose-only.
    NotLocated(String),
    /// Located but the trampoline could not be installed (RWX mmap or
    /// `__TEXT` write failed). Degrade to interpose-only.
    InstallFailed(String),
    /// The no-op RWX self-test failed → the trampoline approach is unsafe on
    /// this host. Degrade to interpose-only without touching `__TEXT`.
    SelftestFailed(String),
}

// ---------------------------------------------------------------------------
// Public entry: locate + install, called from the ctor.
// ---------------------------------------------------------------------------

/// Attempt to install the seed hook. `segments` are the rs2client image's
/// slid segments (from `image::find_rs2client_segments`). Never panics.
pub fn install(segments: &[Segment]) -> HookResult {
    // 0) Prove RWX-exec works under Rosetta before we write into __TEXT.
    if let Err(e) = selftest_noop_trampoline() {
        return HookResult::SelftestFailed(e);
    }

    // 1) Locate the hook instruction by pattern-scan.
    let text = match segments.iter().find(|s| s.name == "__TEXT") {
        Some(t) => t,
        None => return HookResult::NotLocated("no __TEXT segment".into()),
    };

    let hook_addr = match locate_hook(text) {
        Ok(a) => a,
        Err(e) => return HookResult::NotLocated(e),
    };

    // 2) Build the relocating trampoline + write the JMP into __TEXT.
    match install_trampoline(hook_addr, text.initprot) {
        Ok(()) => HookResult::Installed { hook_addr },
        Err(e) => HookResult::InstallFailed(e),
    }
}

/// Poll for captured seeds and, if ready, write a Seeds record. Called from a
/// short-lived watcher thread the ctor spawns (the stub itself only does the
/// minimum: snapshot + flag, to keep the hooked path tiny and signal-safe).
/// Returns true once the seeds have been recorded.
pub fn try_flush_seeds() -> bool {
    if SEEDS_READY.load(Ordering::Acquire) == 0 {
        return false;
    }
    let seeds = [
        SEED0.load(Ordering::Relaxed),
        SEED1.load(Ordering::Relaxed),
        SEED2.load(Ordering::Relaxed),
        SEED3.load(Ordering::Relaxed),
    ];
    capture::record_seeds(-1, seeds);
    capture::record_note(&format!(
        "seeds captured: [{:#010x}, {:#010x}, {:#010x}, {:#010x}] (raw C2S; S2C=+50)",
        seeds[0] as u32, seeds[1] as u32, seeds[2] as u32, seeds[3] as u32
    ));
    true
}

// ---------------------------------------------------------------------------
// Locator.
// ---------------------------------------------------------------------------

fn locate_hook(text: &Segment) -> Result<usize, String> {
    let len = text
        .end
        .checked_sub(text.start)
        .ok_or_else(|| "bad __TEXT bounds".to_string())?;
    let text_slice = unsafe { std::slice::from_raw_parts(text.start as *const u8, len) };

    // (a) Find the [50,50,50,50] constant address (must be unique-ish; take first).
    let const_finder = Finder::new(ISAAC_DELTA_CONST);
    let const_off = const_finder
        .find(text_slice)
        .ok_or_else(|| "ISAAC delta const [50,50,50,50] not found in __TEXT".to_string())?;
    let const_addr = text.start + const_off;

    // (b) Scan for ALL `PADDD XMM,[rip+disp32]` whose RIP-relative target ==
    //     const_addr. The doc (§P4) notes there are at least TWO readers of the
    //     delta const in the SendLoginPacket region (`0x1000cb894` in
    //     SendLoginPacket and `0x1000c67c8` in FUN_1000c6630). Only the
    //     SendLoginPacket one is preceded by `MOVDQU XMM0,[R14+0x48]` loading
    //     the client seeds — so we iterate every candidate and pick the one with
    //     the exact preceding hook instruction, rather than the first match.
    let paddd_finder = Finder::new(PADDD_RIP_PREFIX);
    let mut candidates: Vec<usize> = Vec::new();
    for off in paddd_finder.find_iter(text_slice) {
        if off + 8 > text_slice.len() {
            continue;
        }
        let disp = i32::from_le_bytes([
            text_slice[off + 4],
            text_slice[off + 5],
            text_slice[off + 6],
            text_slice[off + 7],
        ]);
        let insn_end = text.start + off + 8;
        let target = (insn_end as isize).wrapping_add(disp as isize) as usize;
        if target == const_addr {
            candidates.push(text.start + off);
        }
    }
    if candidates.is_empty() {
        return Err("no PADDD XMM,[rip] referencing the delta const".into());
    }

    // (c) Among the candidates, find the one immediately preceded by
    //     `MOVDQU XMM0,[R14+0x48]` (`f3 41 0f 6f 46 48`) — the client-seed load
    //     inside SendLoginPacket. That MOVDQU is our hook point.
    let mut found: Option<(usize, usize)> = None; // (movdqu_addr, paddd_addr)
    let mut seen = String::new();
    for &paddd_addr in &candidates {
        let movdqu_addr = match paddd_addr.checked_sub(HOOK_INSN_LEN) {
            Some(a) if a >= text.start => a,
            _ => continue,
        };
        let movdqu_off = movdqu_addr - text.start;
        let bytes = &text_slice[movdqu_off..movdqu_off + HOOK_INSN_LEN];
        seen.push_str(&format!(" [PADDD@{:#x} prev={:02x?}]", paddd_addr, bytes));
        if bytes == MOVDQU_R14_48 {
            found = Some((movdqu_addr, paddd_addr));
            break;
        }
    }

    let (movdqu_addr, paddd_addr) = found.ok_or_else(|| {
        format!(
            "found {} PADDD-from-delta site(s) but none preceded by MOVDQU XMM0,[R14+0x48];\
             candidates:{}",
            candidates.len(),
            seen
        )
    })?;

    capture::record_note(&format!(
        "seed hook located: MOVDQU@{:#x} PADDD@{:#x} delta_const@{:#x} (of {} candidates)",
        movdqu_addr,
        paddd_addr,
        const_addr,
        candidates.len()
    ));
    Ok(movdqu_addr)
}

// ---------------------------------------------------------------------------
// Trampoline construction.
//
// Stub layout (hand-assembled x86_64), allocated RWX:
//   ; snapshot the 4 client seeds from [R14+0x48] into our static
//   push rax
//   movabs rax, &SEED0           ; absolute addr of the seed static block
//   ; copy 16 bytes [R14+0x48] -> [rax]   (4× mov via xmm-free GPR path to keep it simple)
//   push rcx
//   mov ecx, [r14+0x48]; mov [rax+0],  ecx
//   mov ecx, [r14+0x4c]; mov [rax+4],  ecx
//   mov ecx, [r14+0x50]; mov [rax+8],  ecx
//   mov ecx, [r14+0x54]; mov [rax+12], ecx
//   mov dword [rax+16], 1        ; SEEDS_READY = 1   (relies on SEEDS_READY laid out right after SEED3)
//   pop rcx
//   pop rax
//   ; re-execute the displaced instruction verbatim (no RIP-relative operand)
//   <6 bytes: f3 41 0f 6f 46 48>  ; MOVDQU XMM0,[R14+0x48]
//   ; jump back to the instruction after the hook without touching GPRs
//   jmp qword ptr [rip+0] ; .quad return_addr
//
// NOTE: we clobber RAX/RCX but save/restore them; XMM0 is loaded by the
// displaced MOVDQU exactly as the original code intended, so the client sees
// identical XMM0 on return. The hooked instruction is mid-function so the ABI
// only requires we not corrupt the registers the original instruction stream
// relies on across this point — RAX/RCX are restored, RFLAGS is untouched by
// our movs (mov does not affect flags), XMM0 is set by the displaced insn, and
// the final indirect jump does not clobber a scratch register. This keeps the
// stub correct without a full register save.
// ---------------------------------------------------------------------------

/// Address of the contiguous seed static block. We rely on the five statics
/// (SEED0..3 + SEEDS_READY) being independent globals; the stub computes each
/// address from `&SEED0` only if they are contiguous, which is NOT guaranteed
/// across the four AtomicI32s. To be safe we instead pass the stub a small
/// dedicated 20-byte scratch buffer and copy out of it on the Rust side.
#[repr(C, align(16))]
struct SeedScratch {
    seeds: [i32; 4],
    ready: i32,
}
static mut SEED_SCRATCH: SeedScratch = SeedScratch {
    seeds: [0; 4],
    ready: 0,
};

fn install_trampoline(hook_addr: usize, text_initprot: i32) -> Result<(), String> {
    // 1) Allocate an RWX page for the stub.
    let stub = alloc_rwx(256).ok_or_else(|| "mmap RWX stub failed".to_string())?;

    // 2) Emit the stub machine code.
    let scratch_ptr = std::ptr::addr_of_mut!(SEED_SCRATCH) as usize;
    let return_addr = hook_addr + HOOK_INSN_LEN; // instruction after the hooked MOVDQU
    let code = build_stub(scratch_ptr, return_addr);
    if code.len() > 256 {
        unsafe { free_rwx(stub, 256) };
        return Err(format!("stub too large ({} bytes)", code.len()));
    }
    unsafe {
        std::ptr::copy_nonoverlapping(code.as_ptr(), stub as *mut u8, code.len());
        sys_icache_invalidate(stub as *mut libc::c_void, code.len());
    }

    // 3) Overwrite the 6-byte MOVDQU at hook_addr with `JMP rel32` (5 bytes) +
    //    one NOP pad, so the next instruction (PADDD) starts at the right place.
    let rel = (stub as isize) - (hook_addr as isize + 5); // rel32 from end of JMP
    if rel < i32::MIN as isize || rel > i32::MAX as isize {
        unsafe { free_rwx(stub, 256) };
        return Err("stub out of JMP rel32 range".into());
    }
    let rel32 = (rel as i32).to_le_bytes();
    let mut patch = [0x90u8; HOOK_INSN_LEN]; // pre-fill with NOPs
    patch[0] = 0xe9; // JMP rel32
    patch[1..5].copy_from_slice(&rel32);
    // patch[5] stays 0x90 (NOP) — pads the 6th byte of the displaced insn.

    if !write_text(hook_addr, &patch, text_initprot) {
        unsafe { free_rwx(stub, 256) };
        return Err(format!(
            "failed to write JMP into __TEXT at {:#x}",
            hook_addr
        ));
    }

    // 4) Spawn a watcher to copy seeds out of the scratch into the capture file
    //    once the stub flags them ready. (The stub keeps to the bare minimum.)
    spawn_seed_watcher();

    capture::record_note(&format!(
        "seed hook installed: JMP at {:#x} -> stub {:#x} (return {:#x})",
        hook_addr, stub, return_addr
    ));
    Ok(())
}

/// Build the hand-assembled stub. See the module comment for the layout.
fn build_stub(scratch_ptr: usize, return_addr: usize) -> Vec<u8> {
    let mut c: Vec<u8> = Vec::with_capacity(128);
    // push rax ; push rcx
    c.push(0x50);
    c.push(0x51);
    // movabs rax, scratch_ptr           48 b8 <imm64>
    c.extend_from_slice(&[0x48, 0xb8]);
    c.extend_from_slice(&(scratch_ptr as u64).to_le_bytes());
    // For i in 0..4: mov ecx,[r14+0x48+4i] ; mov [rax+4i],ecx
    //   mov ecx,[r14+disp8]   41 8b 4e <disp8>
    //   mov [rax+disp8],ecx   89 48 <disp8>
    for i in 0..4u8 {
        let src_disp = 0x48u8 + 4 * i;
        let dst_disp = 4 * i;
        c.extend_from_slice(&[0x41, 0x8b, 0x4e, src_disp]); // mov ecx,[r14+src_disp]
        c.extend_from_slice(&[0x89, 0x48, dst_disp]); // mov [rax+dst_disp],ecx
    }
    // mov dword [rax+16], 1             c7 40 10 01 00 00 00   (ready flag)
    c.extend_from_slice(&[0xc7, 0x40, 0x10, 0x01, 0x00, 0x00, 0x00]);
    // pop rcx ; pop rax
    c.push(0x59);
    c.push(0x58);
    // re-execute the displaced MOVDQU XMM0,[R14+0x48] verbatim
    c.extend_from_slice(MOVDQU_R14_48);
    // jump back without touching any GPR:
    //   ff 25 00 00 00 00      jmp qword ptr [rip+0]
    //   <return_addr u64>
    c.extend_from_slice(&[0xff, 0x25, 0x00, 0x00, 0x00, 0x00]);
    c.extend_from_slice(&(return_addr as u64).to_le_bytes());
    c
}

/// Background watcher: once the stub flags seeds ready, copy them into the
/// AtomicI32 publishers + flush to the capture, then exit. Bounded so it never
/// spins forever if no login happens (the client may sit at the GUI).
fn spawn_seed_watcher() {
    std::thread::spawn(|| {
        // Poll for up to ~10 minutes (login is user-driven), then give up.
        for _ in 0..6000 {
            let ready = unsafe { std::ptr::read_volatile(std::ptr::addr_of!(SEED_SCRATCH.ready)) };
            if ready != 0 {
                let s = unsafe { std::ptr::read_volatile(std::ptr::addr_of!(SEED_SCRATCH.seeds)) };
                SEED0.store(s[0], Ordering::Relaxed);
                SEED1.store(s[1], Ordering::Relaxed);
                SEED2.store(s[2], Ordering::Relaxed);
                SEED3.store(s[3], Ordering::Relaxed);
                SEEDS_READY.store(1, Ordering::Release);
                try_flush_seeds();
                return;
            }
            std::thread::sleep(std::time::Duration::from_millis(100));
        }
        capture::record_note("seed watcher timed out (no login observed in 10min)");
    });
}

// ---------------------------------------------------------------------------
// Low-level memory ops.
// ---------------------------------------------------------------------------

/// mmap an RWX region for the stub. Returns the base address or None.
fn alloc_rwx(len: usize) -> Option<usize> {
    let p = unsafe {
        libc::mmap(
            std::ptr::null_mut(),
            len,
            libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
            libc::MAP_PRIVATE | libc::MAP_ANON,
            -1,
            0,
        )
    };
    if p == libc::MAP_FAILED {
        None
    } else {
        Some(p as usize)
    }
}

unsafe fn free_rwx(addr: usize, len: usize) {
    libc::munmap(addr as *mut libc::c_void, len);
}

/// Write `bytes` into the r-x `__TEXT` at `addr`, breaking COW via VM_PROT_COPY,
/// then restoring the original protection. Mirrors the patcher's `patch_memory`.
fn write_text(addr: usize, bytes: &[u8], original_prot: i32) -> bool {
    let page_size = unsafe { libc::sysconf(libc::_SC_PAGESIZE) }.max(4096) as usize;
    let page_start = addr & !(page_size - 1);
    let page_end = (addr + bytes.len() + page_size - 1) & !(page_size - 1);
    let total_len = (page_end - page_start) as u64;
    let task = unsafe { mach_task_self() };

    unsafe {
        let kr = mach_vm_protect(
            task,
            page_start as u64,
            total_len,
            0,
            VM_PROT_READ | VM_PROT_WRITE | VM_PROT_COPY,
        );
        if kr != KERN_SUCCESS {
            return false;
        }
        std::ptr::copy_nonoverlapping(bytes.as_ptr(), addr as *mut u8, bytes.len());
        let restore = if original_prot & (VM_PROT_READ | VM_PROT_EXECUTE) != 0 {
            original_prot
        } else {
            VM_PROT_READ | VM_PROT_EXECUTE
        };
        let _ = mach_vm_protect(task, page_start as u64, total_len, 0, restore);
        sys_icache_invalidate(addr as *mut libc::c_void, bytes.len());
    }
    true
}

// ---------------------------------------------------------------------------
// No-op trampoline self-test (review B8): prove RWX-exec works under Rosetta
// BEFORE we write any control-flow into the client's __TEXT.
// ---------------------------------------------------------------------------

/// Build a tiny RWX stub that is just `ret`, call it through a fn pointer, and
/// confirm we return. If this faults/SIGILLs the host can't run our generated
/// code under Rosetta and we must not attempt the real hook. We can't catch a
/// fault here without a signal handler, so "success" means simply that the call
/// returns — which on a broken host would instead crash. We therefore run it
/// and, if we're still executing afterward, declare success. (A pre-emptive
/// crash would take the process down before any __TEXT write — strictly safer
/// than discovering the problem after patching client code.)
fn selftest_noop_trampoline() -> Result<(), String> {
    let stub = alloc_rwx(16).ok_or_else(|| "selftest: mmap RWX failed".to_string())?;
    unsafe {
        // ret
        std::ptr::write(stub as *mut u8, 0xc3u8);
        sys_icache_invalidate(stub as *mut libc::c_void, 1);
        let f: extern "C" fn() = std::mem::transmute(stub);
        f();
        free_rwx(stub, 16);
    }
    Ok(())
}
