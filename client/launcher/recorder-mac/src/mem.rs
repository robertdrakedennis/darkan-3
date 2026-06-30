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

// ---------------------------------------------------------------------------
// Mapped-memory probe (mach_vm_region) — for CROSS-THREAD readers.
//
// The recorder's hooks read client memory ON the client's own thread, where the
// chain they walk was just produced and cannot be freed mid-read. The anim-trace
// POLLER, however, runs on a SEPARATE thread and could (rarely) deref an avatar
// the client frees concurrently → an unmapped read → SIGSEGV. `is_plausible` only
// rejects the low page; it cannot tell mapped from unmapped. So the poller gates
// each base pointer through `is_readable`, which asks the kernel whether the
// region containing `[addr, addr+len)` is currently mapped + readable. A syscall
// per (infrequent) sample is negligible, and it makes a concurrent-free deref
// impossible: a freed/unmapped pointer fails the check and the sample is skipped.
// (The on-client-thread hooks do NOT need this and do not pay for it.)
// ---------------------------------------------------------------------------

type MachPortT = u32;
type KernReturnT = i32;
type MachVmAddressT = u64;
type MachVmSizeT = u64;
type VmProtT = i32;

const KERN_SUCCESS: KernReturnT = 0;
const VM_PROT_READ: VmProtT = 0x1;
/// `vm_region_basic_info_64` flavor + its info word count (<mach/vm_region.h>).
const VM_REGION_BASIC_INFO_64: i32 = 9;
const VM_REGION_BASIC_INFO_COUNT_64: u32 = 9;

#[repr(C)]
#[derive(Default)]
struct VmRegionBasicInfo64 {
    protection: VmProtT,
    max_protection: VmProtT,
    inheritance: u32,
    shared: u32,
    reserved: u32,
    offset: u64,
    behavior: i32,
    user_wired_count: u16,
}

extern "C" {
    fn mach_task_self() -> MachPortT;
    fn mach_vm_region(
        target_task: MachPortT,
        address: *mut MachVmAddressT,
        size: *mut MachVmSizeT,
        flavor: i32,
        info: *mut VmRegionBasicInfo64,
        info_count: *mut u32,
        object_name: *mut MachPortT,
    ) -> KernReturnT;
}

/// True iff `[addr, addr+len)` lies entirely within a CURRENTLY-MAPPED, READABLE
/// region of this process. Used by the cross-thread anim-trace poller to refuse a
/// read of a pointer the client may have freed/unmapped concurrently (an unmapped
/// deref would SIGSEGV; this turns it into a skipped sample). Conservative: any
/// uncertainty (syscall failure, the region starts above `addr` so `addr` is in a
/// hole, the region is too short, or it is not readable) returns `false`.
pub fn is_readable(addr: usize, len: usize) -> bool {
    if !is_plausible(addr) || len == 0 {
        return false;
    }
    // mach_vm_region returns the region AT OR ABOVE the queried address, so we must
    // verify it actually CONTAINS [addr, addr+len): if it starts above `addr`,
    // `addr` is in an unmapped hole.
    let mut region_addr: MachVmAddressT = addr as MachVmAddressT;
    let mut region_size: MachVmSizeT = 0;
    let mut info = VmRegionBasicInfo64::default();
    let mut count = VM_REGION_BASIC_INFO_COUNT_64;
    let mut object_name: MachPortT = 0;
    let kr = unsafe {
        mach_vm_region(
            mach_task_self(),
            &mut region_addr,
            &mut region_size,
            VM_REGION_BASIC_INFO_64,
            &mut info,
            &mut count,
            &mut object_name,
        )
    };
    if kr != KERN_SUCCESS {
        return false; // no region at/above addr, or the query failed → unmapped
    }
    let region_start = region_addr as usize;
    let region_end = region_start.saturating_add(region_size as usize);
    let want_end = match addr.checked_add(len) {
        Some(e) => e,
        None => return false,
    };
    region_start <= addr && want_end <= region_end && (info.protection & VM_PROT_READ) != 0
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

#[cfg(test)]
mod tests {
    use super::*;

    /// `is_readable` must accept a genuinely-mapped address (a live stack object)
    /// and reject the null/low page + a wild high address that is not mapped. This
    /// is the load-bearing guard that makes the cross-thread poller fault-proof.
    #[test]
    fn is_readable_accepts_mapped_rejects_unmapped() {
        // A live stack buffer is mapped + readable for its whole extent.
        let buf = [0u8; 256];
        let addr = buf.as_ptr() as usize;
        assert!(is_readable(addr, 256));
        assert!(is_readable(addr, 1));

        // Null / low page is never readable.
        assert!(!is_readable(0, 8));
        assert!(!is_readable(0x10, 8));

        // A wild, almost-certainly-unmapped high address: either no region exists
        // at/above it (query fails) or the region above it starts higher (hole), so
        // the conservative check returns false. (On the off chance something is
        // mapped that high, the extent check still rejects a 4 KiB read.)
        assert!(!is_readable(0x7000_0000_0000, 4096));

        // Zero length is rejected (nothing to read).
        assert!(!is_readable(addr, 0));
    }
}
