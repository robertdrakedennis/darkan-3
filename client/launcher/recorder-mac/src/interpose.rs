//! libc `connect()` / `close()` interposition for connect/disconnect events
//! and per-fd peer (ip:port) tracking.
//!
//! We use the canonical macOS `__DATA,__interpose` mechanism (a table of
//! {replacement, original} function-pointer pairs) rather than symbol
//! shadowing. dyld rewrites call sites to our replacement and lets us call the
//! real libc function directly by its imported symbol — this works for the
//! TWOLEVEL-namespace rs2client even without `DYLD_FORCE_FLAT_NAMESPACE`
//! (though the launcher sets that too).
//!
//! Both replacements are pure pass-throughs that record a side event and then
//! tail-call the real libc routine — they NEVER alter behaviour or return
//! values, so leaving the dylib inserted is invisible to the client.

use crate::state;
use parking_lot::Mutex;
use std::collections::HashMap;
use std::net::{Ipv4Addr, Ipv6Addr};
use std::os::raw::c_int;
use std::sync::atomic::{AtomicBool, Ordering};

/// Armed only AFTER our ctor has fully initialised. CRITICAL: the libc
/// interposers below become live the instant dyld maps this dylib — which is
/// DURING libSystem's own bootstrap (`__malloc_init` calls `close()` etc.).
/// Touching our `parking_lot` Mutex / `OnceCell` / formatting machinery before
/// our ctor runs hits thread-local storage that isn't bootstrapped yet and
/// aborts (`_tlv_bootstrap_error`). Until armed, both interposers are PURE
/// passthroughs that touch nothing but the real libc call.
static ARMED: AtomicBool = AtomicBool::new(false);

pub fn arm() {
    ARMED.store(true, Ordering::SeqCst);
}

#[inline]
fn armed() -> bool {
    ARMED.load(Ordering::Relaxed)
}

/// fd -> "ip:port" formatted peer, captured at connect() for use at close().
static PEERS: Mutex<Option<HashMap<c_int, (String, u16)>>> = Mutex::new(None);

fn peers() -> parking_lot::MutexGuard<'static, Option<HashMap<c_int, (String, u16)>>> {
    let mut g = PEERS.lock();
    if g.is_none() {
        *g = Some(HashMap::new());
    }
    g
}

/// Parse a `sockaddr*` into (ip, port) for AF_INET / AF_INET6. Returns None for
/// AF_UNIX and anything we don't format (those connects are ignored).
unsafe fn parse_sockaddr(addr: *const libc::sockaddr, len: libc::socklen_t) -> Option<(String, u16)> {
    if addr.is_null() {
        return None;
    }
    let family = (*addr).sa_family as c_int;
    match family {
        libc::AF_INET => {
            if (len as usize) < std::mem::size_of::<libc::sockaddr_in>() {
                return None;
            }
            let sin = &*(addr as *const libc::sockaddr_in);
            let ip = Ipv4Addr::from(u32::from_be(sin.sin_addr.s_addr));
            let port = u16::from_be(sin.sin_port);
            Some((ip.to_string(), port))
        }
        libc::AF_INET6 => {
            if (len as usize) < std::mem::size_of::<libc::sockaddr_in6>() {
                return None;
            }
            let sin6 = &*(addr as *const libc::sockaddr_in6);
            let ip = Ipv6Addr::from(sin6.sin6_addr.s6_addr);
            let port = u16::from_be(sin6.sin6_port);
            Some((ip.to_string(), port))
        }
        _ => None,
    }
}

extern "C" {
    fn connect(socket: c_int, address: *const libc::sockaddr, len: libc::socklen_t) -> c_int;
    fn close(fd: c_int) -> c_int;
}

#[no_mangle]
pub unsafe extern "C" fn darkan_connect(
    socket: c_int,
    address: *const libc::sockaddr,
    len: libc::socklen_t,
) -> c_int {
    // HARD GUARD: do nothing but the real call until our ctor has armed us (see
    // ARMED). This avoids touching TLS-backed locks during libSystem bootstrap.
    if !armed() {
        return connect(socket, address, len);
    }
    // Record the intended peer BEFORE the real call (the address is valid now;
    // for non-blocking sockets connect may return EINPROGRESS but the peer is
    // still what we want to log).
    if let Some((ip, port)) = parse_sockaddr(address, len) {
        let peer = format!("{ip}:{port}");
        peers().as_mut().unwrap().insert(socket, (ip.clone(), port));
        if let Some(s) = state::session() {
            s.event_connect(socket, &peer, port);
        }
        state::note_connection(&ip, port);
    }
    connect(socket, address, len)
}

#[no_mangle]
pub unsafe extern "C" fn darkan_close(fd: c_int) -> c_int {
    if !armed() {
        return close(fd);
    }
    if let Some((ip, port)) = peers().as_mut().and_then(|m| m.remove(&fd)) {
        if let Some(s) = state::session() {
            s.event_disconnect(fd, &format!("{ip}:{port}"), port);
        }
    }
    close(fd)
}

// -- __interpose table --------------------------------------------------------
//
// Each entry is { replacement_fn, original_fn }. The section name is the dyld
// contract for interposition. `#[used]` + `link_section` keep it in the binary.

#[repr(C)]
struct Interpose {
    replacement: *const (),
    original: *const (),
}
unsafe impl Sync for Interpose {}

#[used]
#[link_section = "__DATA,__interpose"]
static INTERPOSE_CONNECT: Interpose = Interpose {
    replacement: darkan_connect as *const (),
    original: connect as *const (),
};

#[used]
#[link_section = "__DATA,__interpose"]
static INTERPOSE_CLOSE: Interpose = Interpose {
    replacement: darkan_close as *const (),
    original: close as *const (),
};
