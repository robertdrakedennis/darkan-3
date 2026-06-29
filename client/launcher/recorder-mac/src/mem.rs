//! Defensive native memory reads.
//!
//! Mirrors the engine's `NativeAccess.deref` / `getOrNull` discipline: every
//! pointer is null-checked exactly the way the binary itself reads it, and we
//! never crash the client. A null or obviously-bogus pointer yields `None`
//! rather than dereferencing into a fault.
//!
//! NOTE: there is no portable userspace way to *prove* an address is mapped
//! before reading it. We follow the engine's model — null-check every hop and
//! bound the copy length — which is sufficient because we only ever walk
//! pointer chains the client just produced (a live ServerConnection, a buffer
//! the client allocated). We additionally reject the low page and absurd
//! lengths so a partially-initialised struct can't make us copy garbage.

/// Smallest address we will dereference (reject NULL + the zero page).
const MIN_VALID_ADDR: usize = 0x1000;
/// Hard ceiling on any single body copy (defends against a corrupt length
/// field). Matches the engine's `MAX_PAYLOAD` order of magnitude.
pub const MAX_COPY: usize = 0x20000;

#[inline]
pub fn is_plausible(addr: usize) -> bool {
    addr >= MIN_VALID_ADDR
}

/// Read a `u32` (little-endian native) at `base + off`, or `None` if `base` is
/// implausible.
#[inline]
pub fn read_i32(base: usize, off: usize) -> Option<i32> {
    if !is_plausible(base) {
        return None;
    }
    let p = (base + off) as *const i32;
    Some(unsafe { p.read_unaligned() })
}

/// Read a byte at `base + off`, or `None` if `base` is implausible.
#[inline]
pub fn read_u8(base: usize, off: usize) -> Option<u8> {
    if !is_plausible(base) {
        return None;
    }
    let p = (base + off) as *const u8;
    Some(unsafe { p.read_unaligned() })
}

/// Read a pointer-sized value at `base + off`. Returns the raw value (caller
/// decides whether 0 is acceptable).
#[inline]
pub fn read_ptr(base: usize, off: usize) -> Option<usize> {
    if !is_plausible(base) {
        return None;
    }
    let p = (base + off) as *const usize;
    Some(unsafe { p.read_unaligned() })
}

/// Follow a pointer field: read `*(base + off)` and return it only if the
/// result is itself plausible (non-null, above the zero page).
#[inline]
pub fn deref(base: usize, off: usize) -> Option<usize> {
    let v = read_ptr(base, off)?;
    if is_plausible(v) {
        Some(v)
    } else {
        None
    }
}

/// Copy `len` bytes from `addr` into a fresh `Vec`, clamping to [`MAX_COPY`].
/// Returns `None` for an implausible address or non-positive length.
pub fn copy_bytes(addr: usize, len: usize) -> Option<Vec<u8>> {
    if !is_plausible(addr) || len == 0 {
        return None;
    }
    let len = len.min(MAX_COPY);
    let src = addr as *const u8;
    let mut out = vec![0u8; len];
    unsafe {
        std::ptr::copy_nonoverlapping(src, out.as_mut_ptr(), len);
    }
    Some(out)
}
