//! DYLD interpose taps for the game TCP stream — the zero-code-patch capture
//! mechanism that the adversarial review proved fires under Rosetta on this
//! binary.
//!
//! ## How interposing works (and why it is Rosetta-safe)
//!
//! A `__DATA,__interpose` section is an array of `{replacement, original}`
//! function-pointer pairs. For an *inserted* dylib, dyld rewrites the two-level
//! symbol bindings so every call the client makes to e.g. `recv` lands on our
//! `my_recv` instead. The CPU never executes a hand-written trampoline — dyld
//! just changed a pointer — so Rosetta translates whatever the call lands on,
//! exactly as it would natively. There is no "patch a live instruction stream
//! mid-translation" hazard. This is why the entire game-stream capture sits on
//! the safe side of the Rosetta line.
//!
//! The review confirmed both candidate clients import only
//! `recv/recvfrom/send/sendto/read/write/connect/close` (no `recvmsg`/`readv`/
//! vector or msg variants), so this set captures the full stream.
//!
//! ## Section name
//!
//! The review noted modern clang emits `__DATA_CONST,__interpose`, but its live
//! test used the classic `__DATA,__interpose` and dyld honored it for an
//! inserted dylib (it applies interposing before sealing `__DATA_CONST`). We use
//! the classic name here because it is the empirically-proven-to-fire form on
//! this exact setup; `build-mac.sh` verifies with `otool -l` which section the
//! Rust toolchain actually placed it in and the report records that.
//!
//! ## Hot-path discipline
//!
//! Each replacement: call the real libc fn, and only record bytes the kernel
//! accepted for I/O. For `connect`, record the peer before the syscall so
//! immediate failures still preserve the target address. The `capture::record_*`
//! fns take the writer lock once and bail instantly if recording is disabled, so
//! when `DARKAN_RECORD` is unset the overhead is one uncontended lock + an
//! `is_none` check per call. No allocation on the hot path.

use crate::capture::{self, is_armed, SyscallKind, DIR_IN, DIR_OUT};
use libc::{c_int, c_void, size_t, sockaddr, socklen_t, ssize_t};

// ---------------------------------------------------------------------------
// The interpose table entry type + section.
// ---------------------------------------------------------------------------

/// One `{replacement, original}` pair, matching dyld's `dyld_interpose_t`
/// (two `const void*` in C). We store raw `*const c_void` so the array can be a
/// `const`-initialized `static` — casting a function item to a raw pointer with
/// `as *const c_void` is permitted in const eval (unlike `fn as usize`, which is
/// not). Raw pointers are not `Sync`, so we wrap the whole table in a `Sync`
/// newtype below rather than impl-ing `Sync` on individual entries.
#[repr(C)]
struct Interpose {
    replacement: *const c_void,
    original: *const c_void,
}

/// Wrapper so the `static` interpose table is `Sync` (raw pointers aren't).
/// Safe: the table is immutable after load and only read by dyld.
#[repr(transparent)]
struct InterposeTable<const N: usize>([Interpose; N]);
unsafe impl<const N: usize> Sync for InterposeTable<N> {}

// ---------------------------------------------------------------------------
// Real libc declarations. We call these directly (the dylib's OWN binding to
// libSystem is NOT interposed — interposing only rewrites the MAIN image's
// bindings — so `recv` here is the genuine libc `recv`).
// ---------------------------------------------------------------------------

extern "C" {
    fn recv(fd: c_int, buf: *mut c_void, len: size_t, flags: c_int) -> ssize_t;
    fn read(fd: c_int, buf: *mut c_void, len: size_t) -> ssize_t;
    fn recvfrom(
        fd: c_int,
        buf: *mut c_void,
        len: size_t,
        flags: c_int,
        from: *mut sockaddr,
        fromlen: *mut socklen_t,
    ) -> ssize_t;
    fn send(fd: c_int, buf: *const c_void, len: size_t, flags: c_int) -> ssize_t;
    fn write(fd: c_int, buf: *const c_void, len: size_t) -> ssize_t;
    fn sendto(
        fd: c_int,
        buf: *const c_void,
        len: size_t,
        flags: c_int,
        to: *const sockaddr,
        tolen: socklen_t,
    ) -> ssize_t;
    fn connect(fd: c_int, addr: *const sockaddr, len: socklen_t) -> c_int;
    fn close(fd: c_int) -> c_int;
}

// ---------------------------------------------------------------------------
// Replacement functions. Each preserves errno semantics by calling the real fn
// and returning its result verbatim; capture is a pure side effect.
// ---------------------------------------------------------------------------

#[no_mangle]
unsafe extern "C" fn darkan_recv(
    fd: c_int,
    buf: *mut c_void,
    len: size_t,
    flags: c_int,
) -> ssize_t {
    let n = recv(fd, buf, len, flags);
    // Fast pre-arm passthrough: dyld may route the client's earliest reads here
    // before the ctor armed us; do nothing but forward until then.
    if !is_armed() {
        return n;
    }
    if n > 0 && !buf.is_null() {
        let slice = std::slice::from_raw_parts(buf as *const u8, n as usize);
        capture::record_io(fd, DIR_IN, SyscallKind::Recv, slice);
    }
    n
}

#[no_mangle]
unsafe extern "C" fn darkan_read(fd: c_int, buf: *mut c_void, len: size_t) -> ssize_t {
    let n = read(fd, buf, len);
    if !is_armed() {
        return n;
    }
    if n > 0 && !buf.is_null() {
        let slice = std::slice::from_raw_parts(buf as *const u8, n as usize);
        capture::record_io(fd, DIR_IN, SyscallKind::Read, slice);
    }
    n
}

#[no_mangle]
unsafe extern "C" fn darkan_recvfrom(
    fd: c_int,
    buf: *mut c_void,
    len: size_t,
    flags: c_int,
    from: *mut sockaddr,
    fromlen: *mut socklen_t,
) -> ssize_t {
    let n = recvfrom(fd, buf, len, flags, from, fromlen);
    if !is_armed() {
        return n;
    }
    if n > 0 && !buf.is_null() {
        let slice = std::slice::from_raw_parts(buf as *const u8, n as usize);
        capture::record_io(fd, DIR_IN, SyscallKind::Recvfrom, slice);
    }
    n
}

#[no_mangle]
unsafe extern "C" fn darkan_send(
    fd: c_int,
    buf: *const c_void,
    len: size_t,
    flags: c_int,
) -> ssize_t {
    let n = send(fd, buf, len, flags);
    if !is_armed() {
        return n;
    }
    if n > 0 && !buf.is_null() {
        // Record only the bytes actually accepted by the kernel (n), which may
        // be < len on a short write — matching what really went on the wire.
        let slice = std::slice::from_raw_parts(buf as *const u8, n as usize);
        capture::record_io(fd, DIR_OUT, SyscallKind::Send, slice);
    }
    n
}

#[no_mangle]
unsafe extern "C" fn darkan_write(fd: c_int, buf: *const c_void, len: size_t) -> ssize_t {
    let n = write(fd, buf, len);
    if !is_armed() {
        return n;
    }
    if n > 0 && !buf.is_null() {
        let slice = std::slice::from_raw_parts(buf as *const u8, n as usize);
        capture::record_io(fd, DIR_OUT, SyscallKind::Write, slice);
    }
    n
}

#[no_mangle]
unsafe extern "C" fn darkan_sendto(
    fd: c_int,
    buf: *const c_void,
    len: size_t,
    flags: c_int,
    to: *const sockaddr,
    tolen: socklen_t,
) -> ssize_t {
    let n = sendto(fd, buf, len, flags, to, tolen);
    if !is_armed() {
        return n;
    }
    if n > 0 && !buf.is_null() {
        let slice = std::slice::from_raw_parts(buf as *const u8, n as usize);
        capture::record_io(fd, DIR_OUT, SyscallKind::Sendto, slice);
    }
    n
}

#[no_mangle]
unsafe extern "C" fn darkan_connect(fd: c_int, addr: *const sockaddr, len: socklen_t) -> c_int {
    if is_armed() && !addr.is_null() {
        record_peer(fd, addr, len);
    }
    let ret = connect(fd, addr, len);
    ret
}

#[no_mangle]
unsafe extern "C" fn darkan_close(fd: c_int) -> c_int {
    if !is_armed() {
        return close(fd);
    }
    // Record the close BEFORE the real call so the fd is still valid context.
    capture::record_close(fd);
    close(fd)
}

// ---------------------------------------------------------------------------
// Peer-address extraction for connect().
// ---------------------------------------------------------------------------

/// Decode an AF_INET / AF_INET6 sockaddr and emit a Connect record. Non-INET
/// families (AF_UNIX, etc.) are ignored — they are not game connections.
unsafe fn record_peer(fd: c_int, addr: *const sockaddr, len: socklen_t) {
    let family = (*addr).sa_family as c_int;
    match family {
        libc::AF_INET => {
            if (len as usize) < std::mem::size_of::<libc::sockaddr_in>() {
                return;
            }
            let sin = addr as *const libc::sockaddr_in;
            // sin_port is network byte order (big-endian); convert to host.
            let port = u16::from_be((*sin).sin_port);
            // s_addr is network byte order; expose the 4 raw bytes in network
            // order (a.b.c.d) so the deframer prints them directly.
            let raw = (*sin).sin_addr.s_addr.to_ne_bytes();
            capture::record_connect(fd, libc::AF_INET as u8, port, &raw);
        }
        libc::AF_INET6 => {
            if (len as usize) < std::mem::size_of::<libc::sockaddr_in6>() {
                return;
            }
            let sin6 = addr as *const libc::sockaddr_in6;
            let port = u16::from_be((*sin6).sin6_port);
            let raw = (*sin6).sin6_addr.s6_addr;
            capture::record_connect(fd, libc::AF_INET6 as u8, port, &raw);
        }
        _ => {}
    }
}

// ---------------------------------------------------------------------------
// The interpose table. dyld reads this section at load and rewrites the main
// image's bindings for each `original` symbol to the matching `replacement`.
// ---------------------------------------------------------------------------

#[used]
#[link_section = "__DATA,__interpose"]
static INTERPOSES: InterposeTable<8> = InterposeTable([
    Interpose {
        replacement: darkan_recv as *const c_void,
        original: recv as *const c_void,
    },
    Interpose {
        replacement: darkan_read as *const c_void,
        original: read as *const c_void,
    },
    Interpose {
        replacement: darkan_recvfrom as *const c_void,
        original: recvfrom as *const c_void,
    },
    Interpose {
        replacement: darkan_send as *const c_void,
        original: send as *const c_void,
    },
    Interpose {
        replacement: darkan_write as *const c_void,
        original: write as *const c_void,
    },
    Interpose {
        replacement: darkan_sendto as *const c_void,
        original: sendto as *const c_void,
    },
    Interpose {
        replacement: darkan_connect as *const c_void,
        original: connect as *const c_void,
    },
    Interpose {
        replacement: darkan_close as *const c_void,
        original: close as *const c_void,
    },
]);
