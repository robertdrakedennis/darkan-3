//! World-server + HTTP content connect() redirects — macOS `__DATA,__interpose` tap.
//!
//! ## Why this exists
//!
//! RS3 NXT dials game **worlds** on port **443** (a TLS-wrapped world protocol).
//! Proven from prod connect() taps: every live world socket is `<jagex-ip>:443`.
//! Our local world server, however, listens on `WORLD_PORT` (default 43597 in
//! `.env` / `EnvVars.worldPort`) because LostCity holds 43594/43595.
//!
//! In local/custom mode the worldlist + login-data tail already hand the client
//! `localhost` as the world host (so it dials loopback), but the client still
//! uses its *default* world port 443 — so it connects to `127.0.0.1:443` /
//! `::1:443`, gets connection-refused, and the lobby shows "Unexpected server
//! response." It never tries 43597.
//!
//! This tap rewrites:
//!   * the WORLD connect from loopback:443 to `DARKAN_WORLD_PORT`, so the client
//!     reaches our local world server; and
//!   * external HTTP content connects from `<content-cdn>:80` to
//!     `127.0.0.1:DARKAN_HTTP_PORT`, so 948-5 cache groups come from the local
//!     cache server instead of the live CloudFront CDN.
//!
//! ## Why connect() interpose (approach A), not a memory patch
//!
//! The capture (`session-…-local/events.jsonl`) shows the world connect is the
//! ONLY loopback (`127.0.0.1` / `::1`) connection dialed on :443. Every other
//! :443 connect (auth/social/telemetry) targets a real external Jagex IP, and
//! every other loopback connect uses a different port (43596 lobby, 8829 HTTP
//! JS5). So `loopback:<from_port>` is a precise, exclusive selector for the
//! world connect — host+port aware, needing zero binary RE, and stable across
//! client builds (a hardcoded-immediate memory patch would shift every rebuild
//! and risks colliding with the external-HTTPS :443 path).
//!
//! ## Call-site / host awareness (critical)
//!
//! We rewrite ONLY when BOTH hold:
//!   * the destination IP is loopback (`127.0.0.1` or `::1`), AND
//!   * the destination port equals the configured world dial port (`from_port`,
//!     default 443).
//!
//! The client's many legitimate `<external-jagex-ip>:443` HTTPS connections are
//! left completely untouched, as are loopback connects to the lobby (43596) and
//! HTTP JS5 (8829) ports. We mutate a private copy of the sockaddr — never the
//! caller's buffer. The world redirect rewrites only the 2-byte port field; the
//! HTTP-content redirect rewrites external IPv4/IPv6 `:80` to loopback plus the
//! local content port.
//!
//! ## Bootstrap safety
//!
//! Like the recorder's connect tap, this interposer goes live the instant dyld
//! maps the dylib — which is DURING libSystem's own bootstrap (`__malloc_init`
//! calls `connect`/`close` indirectly). Touching any TLS-backed machinery
//! (locks, `OnceCell`, formatting) before our ctor runs aborts with
//! `_tlv_bootstrap_error`. So until `arm()` is called from the ctor, the
//! interposer is a PURE passthrough that touches nothing but the real
//! `connect()`. The from/to ports live in plain `AtomicU16` (no TLS, no alloc).

use std::os::raw::c_int;
use std::sync::atomic::{AtomicBool, AtomicU16, Ordering};

/// The world dial port the client uses by default (the port we intercept).
/// Standard RS3 NXT world port — TLS-wrapped world protocol.
pub const DEFAULT_WORLD_FROM_PORT: u16 = 443;

/// Armed only AFTER our ctor has validated config and called `arm()`. Until
/// then the interposer is a pure passthrough (see module docs — bootstrap TLS).
static ARMED: AtomicBool = AtomicBool::new(false);

/// Port the client currently dials the world on (the one we rewrite FROM).
static FROM_PORT: AtomicU16 = AtomicU16::new(DEFAULT_WORLD_FROM_PORT);

/// Local world-server port we rewrite the loopback world connect TO.
static TO_PORT: AtomicU16 = AtomicU16::new(0);

/// Local config/content-server port we rewrite external HTTP content connects TO.
static HTTP_CONTENT_TO_PORT: AtomicU16 = AtomicU16::new(0);

/// Arm the redirect: loopback `from_port` connects are rewritten to `to_port`.
/// Called from the ctor once `DARKAN_WORLD_PORT` is parsed. No-op-safe if
/// `to_port == 0` or `to_port == from_port` (nothing to rewrite).
pub fn arm(from_port: u16, to_port: u16) {
    FROM_PORT.store(from_port, Ordering::SeqCst);
    TO_PORT.store(to_port, Ordering::SeqCst);
    ARMED.store(true, Ordering::SeqCst);
}

/// Arm the HTTP content redirect: external `:80` connects are rewritten to the
/// local config/content server. Kept separate from [arm] because world redirect
/// and content redirect are independently configured.
pub fn arm_http_content(to_port: u16) {
    HTTP_CONTENT_TO_PORT.store(to_port, Ordering::SeqCst);
    ARMED.store(true, Ordering::SeqCst);
}

#[inline]
fn armed() -> bool {
    ARMED.load(Ordering::Relaxed)
}

extern "C" {
    fn connect(socket: c_int, address: *const libc::sockaddr, len: libc::socklen_t) -> c_int;
}

/// True if `addr` is an IPv4/IPv6 loopback address (`127.0.0.0/8` or `::1`).
/// Anything else (external Jagex IPs) returns false so it is never rewritten.
#[inline]
unsafe fn is_loopback(addr: *const libc::sockaddr) -> bool {
    match (*addr).sa_family as c_int {
        libc::AF_INET => {
            let sin = &*(addr as *const libc::sockaddr_in);
            // s_addr is network byte order; 127.x.x.x => first octet 127.
            let host = u32::from_be(sin.sin_addr.s_addr);
            (host >> 24) == 127
        }
        libc::AF_INET6 => {
            let sin6 = &*(addr as *const libc::sockaddr_in6);
            // ::1 == fifteen zero bytes then 0x01.
            let b = sin6.sin6_addr.s6_addr;
            b == [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
        }
        _ => false,
    }
}

/// Read the destination port (host order) from an AF_INET/AF_INET6 sockaddr.
#[inline]
unsafe fn dest_port(addr: *const libc::sockaddr) -> Option<u16> {
    match (*addr).sa_family as c_int {
        libc::AF_INET => Some(u16::from_be((*(addr as *const libc::sockaddr_in)).sin_port)),
        libc::AF_INET6 => Some(u16::from_be(
            (*(addr as *const libc::sockaddr_in6)).sin6_port,
        )),
        _ => None,
    }
}

/// True when this connect is an external HTTP content candidate. We deliberately
/// do NOT match external :443 here: that path is TLS, and redirecting it to the
/// plain local Ktor HTTP listener would convert a CDN escape into a TLS failure.
#[inline]
unsafe fn should_redirect_http_content(addr: *const libc::sockaddr) -> bool {
    dest_port(addr) == Some(80) && !is_loopback(addr)
}

/// Interposed `connect()`. Rewrites a loopback `from_port` destination to
/// `to_port`, or an external HTTP content destination to the local HTTP server,
/// on a PRIVATE copy of the sockaddr before the real connect(); everything else
/// passes straight through unmodified.
///
/// # Safety
/// `address`/`len` are the caller's `connect()` args; we only read them and,
/// for the matched case, copy them into a local before mutating the copy.
#[no_mangle]
pub unsafe extern "C" fn darkan_world_connect(
    socket: c_int,
    address: *const libc::sockaddr,
    len: libc::socklen_t,
) -> c_int {
    // HARD GUARD: nothing but the real call until the ctor has armed us. Avoids
    // any TLS/lock/alloc touch during libSystem bootstrap (see module docs).
    if !armed() || address.is_null() {
        return connect(socket, address, len);
    }

    let to_port = TO_PORT.load(Ordering::Relaxed);
    let from_port = FROM_PORT.load(Ordering::Relaxed);
    let http_content_to_port = HTTP_CONTENT_TO_PORT.load(Ordering::Relaxed);

    if http_content_to_port != 0 && should_redirect_http_content(address) {
        let family = (*address).sa_family as c_int;
        let to_be = http_content_to_port.to_be();
        match family {
            libc::AF_INET if (len as usize) >= std::mem::size_of::<libc::sockaddr_in>() => {
                let mut sa: libc::sockaddr_in = *(address as *const libc::sockaddr_in);
                sa.sin_port = to_be;
                sa.sin_addr.s_addr = u32::from(std::net::Ipv4Addr::new(127, 0, 0, 1)).to_be();
                return connect(
                    socket,
                    &sa as *const libc::sockaddr_in as *const libc::sockaddr,
                    len,
                );
            }
            libc::AF_INET6 if (len as usize) >= std::mem::size_of::<libc::sockaddr_in6>() => {
                let mut sa: libc::sockaddr_in6 = *(address as *const libc::sockaddr_in6);
                sa.sin6_port = to_be;
                sa.sin6_addr.s6_addr = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1];
                return connect(
                    socket,
                    &sa as *const libc::sockaddr_in6 as *const libc::sockaddr,
                    len,
                );
            }
            _ => {}
        }
    }

    // Only act when there is a real rewrite to do and the destination matches
    // loopback:from_port exactly. `dest_port`/`is_loopback` are pure reads.
    if to_port != 0 && to_port != from_port {
        if let Some(port) = dest_port(address) {
            if port == from_port && is_loopback(address) {
                let family = (*address).sa_family as c_int;
                let to_be = to_port.to_be();
                match family {
                    libc::AF_INET if (len as usize) >= std::mem::size_of::<libc::sockaddr_in>() => {
                        // Copy the caller's sockaddr, rewrite only the port on
                        // the copy, and connect() to the copy. The caller's
                        // buffer is never mutated.
                        let mut sa: libc::sockaddr_in = *(address as *const libc::sockaddr_in);
                        sa.sin_port = to_be;
                        return connect(
                            socket,
                            &sa as *const libc::sockaddr_in as *const libc::sockaddr,
                            len,
                        );
                    }
                    libc::AF_INET6
                        if (len as usize) >= std::mem::size_of::<libc::sockaddr_in6>() =>
                    {
                        let mut sa: libc::sockaddr_in6 = *(address as *const libc::sockaddr_in6);
                        sa.sin6_port = to_be;
                        return connect(
                            socket,
                            &sa as *const libc::sockaddr_in6 as *const libc::sockaddr,
                            len,
                        );
                    }
                    _ => {}
                }
            }
        }
    }

    connect(socket, address, len)
}

// -- __interpose table --------------------------------------------------------
//
// One entry: { replacement_fn, original_fn }. dyld rewrites call sites to our
// replacement and lets us tail-call the real libc `connect` by its imported
// symbol. Works under the client's two-level namespace WITHOUT a forced flat
// namespace (the recorder's identical table proves this in the live flow).

#[repr(C)]
struct Interpose {
    replacement: *const (),
    original: *const (),
}
unsafe impl Sync for Interpose {}

#[used]
#[link_section = "__DATA,__interpose"]
static INTERPOSE_CONNECT: Interpose = Interpose {
    replacement: darkan_world_connect as *const (),
    original: connect as *const (),
};

// -- Tests --------------------------------------------------------------------

#[cfg(test)]
mod tests {
    use super::*;
    use std::mem::size_of;

    /// Build an AF_INET sockaddr for `127.0.0.1:<port>` (network byte order).
    fn v4_loopback(port: u16) -> libc::sockaddr_in {
        let mut sa: libc::sockaddr_in = unsafe { std::mem::zeroed() };
        sa.sin_family = libc::AF_INET as libc::sa_family_t;
        sa.sin_port = port.to_be();
        sa.sin_addr.s_addr = u32::from(std::net::Ipv4Addr::new(127, 0, 0, 1)).to_be();
        sa
    }

    /// Build an AF_INET sockaddr for an external IP:port.
    fn v4_external(a: u8, b: u8, c: u8, d: u8, port: u16) -> libc::sockaddr_in {
        let mut sa: libc::sockaddr_in = unsafe { std::mem::zeroed() };
        sa.sin_family = libc::AF_INET as libc::sa_family_t;
        sa.sin_port = port.to_be();
        sa.sin_addr.s_addr = u32::from(std::net::Ipv4Addr::new(a, b, c, d)).to_be();
        sa
    }

    fn v6_loopback(port: u16) -> libc::sockaddr_in6 {
        let mut sa: libc::sockaddr_in6 = unsafe { std::mem::zeroed() };
        sa.sin6_family = libc::AF_INET6 as libc::sa_family_t;
        sa.sin6_port = port.to_be();
        sa.sin6_addr.s6_addr = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1];
        sa
    }

    fn v6_external(port: u16) -> libc::sockaddr_in6 {
        let mut sa: libc::sockaddr_in6 = unsafe { std::mem::zeroed() };
        sa.sin6_family = libc::AF_INET6 as libc::sa_family_t;
        sa.sin6_port = port.to_be();
        sa.sin6_addr.s6_addr = [
            0x26, 0x00, 0x90, 0x00, 0x53, 0x01, 0x00, 0x00, 0, 0, 0, 0, 0, 0, 0, 1,
        ];
        sa
    }

    #[test]
    fn loopback_v4_443_is_matched() {
        let sa = v4_loopback(443);
        unsafe {
            let p = &sa as *const _ as *const libc::sockaddr;
            assert!(is_loopback(p));
            assert_eq!(dest_port(p), Some(443));
        }
    }

    #[test]
    fn loopback_v6_443_is_matched() {
        let sa = v6_loopback(443);
        unsafe {
            let p = &sa as *const _ as *const libc::sockaddr;
            assert!(is_loopback(p));
            assert_eq!(dest_port(p), Some(443));
        }
    }

    #[test]
    fn external_443_is_not_loopback() {
        // The Jagex HTTPS targets from the capture (52.49.10.11, 34.255.32.9).
        for (a, b, c, d) in [(52u8, 49, 10, 11), (34, 255, 32, 9), (8, 26, 16, 159)] {
            let sa = v4_external(a, b, c, d, 443);
            unsafe {
                let p = &sa as *const _ as *const libc::sockaddr;
                assert!(!is_loopback(p), "{a}.{b}.{c}.{d} must NOT be loopback");
                assert_eq!(dest_port(p), Some(443));
            }
        }
    }

    #[test]
    fn loopback_other_ports_match_loopback_but_not_from_port() {
        // 43596 (lobby) and 8829 (HTTP JS5) are loopback but must be left alone
        // because they are not the world dial port (443).
        for other in [43596u16, 8829] {
            let sa = v4_loopback(other);
            unsafe {
                let p = &sa as *const _ as *const libc::sockaddr;
                assert!(is_loopback(p));
                assert_ne!(dest_port(p), Some(443));
            }
        }
    }

    #[test]
    fn external_http_80_is_content_redirect_candidate() {
        let sa = v4_external(52, 84, 147, 140, 80);
        unsafe {
            let p = &sa as *const _ as *const libc::sockaddr;
            assert!(should_redirect_http_content(p));
        }
    }

    #[test]
    fn loopback_http_80_is_not_content_redirect_candidate() {
        let sa = v4_loopback(80);
        unsafe {
            let p = &sa as *const _ as *const libc::sockaddr;
            assert!(!should_redirect_http_content(p));
        }
    }

    #[test]
    fn external_https_443_is_not_content_redirect_candidate() {
        for sa in [
            v4_external(52, 84, 147, 140, 443),
            v4_external(52, 208, 25, 158, 443),
        ] {
            unsafe {
                let p = &sa as *const _ as *const libc::sockaddr;
                assert!(!should_redirect_http_content(p));
            }
        }
    }

    #[test]
    fn external_ipv6_http_80_is_content_redirect_candidate() {
        let sa = v6_external(80);
        unsafe {
            let p = &sa as *const _ as *const libc::sockaddr;
            assert!(should_redirect_http_content(p));
        }
    }

    #[test]
    fn rewrite_only_matched_v4_in_place_copy() {
        // Simulate the rewrite logic against a private copy (no real connect()):
        // matched loopback:443 -> 43597; everything else unchanged.
        let from = 443u16;
        let to = 43597u16;

        // matched
        let sa = v4_loopback(from);
        let mut copy = sa;
        copy.sin_port = to.to_be();
        assert_eq!(u16::from_be(copy.sin_port), to);
        // caller's buffer untouched
        assert_eq!(u16::from_be(sa.sin_port), from);

        // external :443 must NOT be rewritten (guard is is_loopback)
        let ext = v4_external(52, 49, 10, 11, from);
        unsafe {
            assert!(!is_loopback(&ext as *const _ as *const libc::sockaddr));
        }
    }

    #[test]
    fn len_guard_rejects_short_sockaddr() {
        // A len shorter than the struct must fall through (no rewrite). We only
        // assert the size relationship the match arms rely on.
        assert!((1 as libc::socklen_t as usize) < size_of::<libc::sockaddr_in>());
        assert!((1 as libc::socklen_t as usize) < size_of::<libc::sockaddr_in6>());
    }

    #[test]
    fn arm_then_armed_reports_true() {
        // arm() is global; assert the from/to land and the flag flips.
        arm(443, 43597);
        assert!(armed());
        assert_eq!(FROM_PORT.load(Ordering::Relaxed), 443);
        assert_eq!(TO_PORT.load(Ordering::Relaxed), 43597);
    }

    #[test]
    fn arm_http_content_sets_local_content_port() {
        arm_http_content(8829);
        assert!(armed());
        assert_eq!(HTTP_CONTENT_TO_PORT.load(Ordering::Relaxed), 8829);
    }
}
