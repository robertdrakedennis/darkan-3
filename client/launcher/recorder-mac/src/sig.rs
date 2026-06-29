//! Mach-O main-image discovery + wildcard byte-pattern sig-scanning.
//!
//! Ported from `client/launcher/patcher-mac/src/lib.rs` (the dyld image walk +
//! readable-segment enumeration), extended with `??` full-byte wildcard support
//! so the recorder can resolve function ENTRIES (not just contiguous ASCII
//! strings) the way the RE doc's signatures are written.
//!
//! Resolving a runtime address from a match:
//!   `runtime_addr = match_file_offset + 0x100000000 + dyld_slide`  (== match_vmaddr + slide)
//! We compute that directly: the scan operates on the already-slid in-memory
//! bytes, so a hit's runtime address is just the slice address of the match.

use memchr::memmem;
use std::ffi::CStr;
use std::os::raw::c_char;

pub const MACHO_IMAGE_BASE: u64 = 0x1_0000_0000;

// -- dyld + Mach-O FFI (same surface as patcher-mac) --------------------------

extern "C" {
    fn _dyld_image_count() -> u32;
    fn _dyld_get_image_header(image_index: u32) -> *const MachHeader64;
    fn _dyld_get_image_name(image_index: u32) -> *const c_char;
    fn _dyld_get_image_vmaddr_slide(image_index: u32) -> isize;
}

const MH_MAGIC_64: u32 = 0xfeed_facf;
const MH_EXECUTE: u32 = 0x2;
const LC_SEGMENT_64: u32 = 0x19;

#[repr(C)]
struct MachHeader64 {
    magic: u32,
    cputype: i32,
    cpusubtype: i32,
    filetype: u32,
    ncmds: u32,
    sizeofcmds: u32,
    flags: u32,
    reserved: u32,
}

#[repr(C)]
struct LoadCommand {
    cmd: u32,
    cmdsize: u32,
}

#[repr(C)]
struct SegmentCommand64 {
    cmd: u32,
    cmdsize: u32,
    segname: [u8; 16],
    vmaddr: u64,
    vmsize: u64,
    fileoff: u64,
    filesize: u64,
    maxprot: i32,
    initprot: i32,
    nsects: u32,
    flags: u32,
}

/// A mapped, readable, executable region of an image (we scan `__TEXT`).
#[derive(Clone, Copy)]
pub struct Region {
    pub start: usize,
    pub end: usize,
}

/// The resolved main image: its slid base, name, and executable scan regions.
/// `header` is retained so we can re-walk for readable-only segments (the build
/// string lives in `__TEXT,__const`).
pub struct MainImage {
    pub name: String,
    pub slide: isize,
    pub regions: Vec<Region>,
    header: usize,
}

/// All READABLE segments of the image (superset of the executable `regions`),
/// for string scans like the build identifier. Re-walks the load commands.
pub fn readable_regions(image: &MainImage) -> Vec<Region> {
    let mut regions = Vec::new();
    let header = image.header as *const MachHeader64;
    if header.is_null() {
        return regions;
    }
    let hdr = unsafe { &*header };
    if hdr.magic != MH_MAGIC_64 {
        return regions;
    }
    let mut cmd_ptr = unsafe { (header as *const u8).add(std::mem::size_of::<MachHeader64>()) };
    for _ in 0..hdr.ncmds {
        let lc = unsafe { &*(cmd_ptr as *const LoadCommand) };
        if lc.cmdsize == 0 {
            break;
        }
        if lc.cmd == LC_SEGMENT_64 {
            let seg = unsafe { &*(cmd_ptr as *const SegmentCommand64) };
            let readable = (seg.initprot & 0x1) != 0;
            if readable && seg.vmsize > 0 {
                let start = (seg.vmaddr as isize + image.slide) as usize;
                let end = start.wrapping_add(seg.vmsize as usize);
                if end > start {
                    regions.push(Region { start, end });
                }
            }
        }
        cmd_ptr = unsafe { cmd_ptr.add(lc.cmdsize as usize) };
    }
    regions
}

/// Resolve the MAIN EXECUTABLE image. We CANNOT assume dyld index 0 — when this
/// dylib is loaded via `DYLD_INSERT_LIBRARIES`, the inserted dylib is placed
/// BEFORE the main executable in the image list (verified: index 0 becomes the
/// inserted dylib, the executable shifts to index 1). We therefore scan for the
/// image whose Mach-O `filetype == MH_EXECUTE`. Returns `None` if none is found
/// or it has no readable segment — callers degrade to socket-only capture.
pub fn resolve_main_image() -> Option<MainImage> {
    let count = unsafe { _dyld_image_count() };
    let mut idx = None;
    for i in 0..count {
        let header = unsafe { _dyld_get_image_header(i) };
        if header.is_null() {
            continue;
        }
        let hdr = unsafe { &*header };
        if hdr.magic == MH_MAGIC_64 && hdr.filetype == MH_EXECUTE {
            idx = Some(i);
            break;
        }
    }
    let index = idx?;

    let header = unsafe { _dyld_get_image_header(index) };
    if header.is_null() {
        return None;
    }
    let hdr = unsafe { &*header };
    if hdr.magic != MH_MAGIC_64 {
        return None;
    }
    let slide = unsafe { _dyld_get_image_vmaddr_slide(index) };
    let name = unsafe {
        let np = _dyld_get_image_name(index);
        if np.is_null() {
            "<unknown>".to_string()
        } else {
            CStr::from_ptr(np).to_string_lossy().to_string()
        }
    };

    let mut regions = Vec::new();
    let mut cmd_ptr = unsafe { (header as *const u8).add(std::mem::size_of::<MachHeader64>()) };
    for _ in 0..hdr.ncmds {
        let lc = unsafe { &*(cmd_ptr as *const LoadCommand) };
        if lc.cmdsize == 0 {
            break;
        }
        if lc.cmd == LC_SEGMENT_64 {
            let seg = unsafe { &*(cmd_ptr as *const SegmentCommand64) };
            // initprot bit 0 = VM_PROT_READ, bit 2 = VM_PROT_EXECUTE. We only
            // need executable __TEXT for function-entry signatures, but include
            // any readable+executable segment for safety.
            let readable = (seg.initprot & 0x1) != 0;
            let executable = (seg.initprot & 0x4) != 0;
            if readable && executable && seg.vmsize > 0 {
                let start = (seg.vmaddr as isize + slide) as usize;
                let end = start.wrapping_add(seg.vmsize as usize);
                if end > start {
                    regions.push(Region { start, end });
                }
            }
        }
        cmd_ptr = unsafe { cmd_ptr.add(lc.cmdsize as usize) };
    }

    if regions.is_empty() {
        return None;
    }
    Some(MainImage {
        name,
        slide,
        regions,
        header: header as usize,
    })
}

// -- Wildcard pattern -------------------------------------------------------

/// A compile-time-described byte pattern. `bytes`/`mask` are derived lazily on
/// first scan from the human-readable `text` (IDA-style, space-separated, `??`
/// = wildcard) so the `const` form stays declarative in `offsets.rs`.
pub struct Pattern {
    pub name: &'static str,
    /// Expected `vmaddr - 0x100000000` (sanity check after the scan).
    pub expected_file: usize,
    pub text: &'static str,
}

impl Pattern {
    pub const fn new(name: &'static str, expected_file: usize, text: &'static str) -> Self {
        Pattern {
            name,
            expected_file,
            text,
        }
    }

    /// Parse the textual pattern into (bytes, mask). `mask[i] == true` means
    /// `bytes[i]` is significant; `false` means wildcard.
    fn compile(&self) -> (Vec<u8>, Vec<bool>) {
        let mut bytes = Vec::new();
        let mut mask = Vec::new();
        for tok in self.text.split_whitespace() {
            if tok == "??" {
                bytes.push(0);
                mask.push(false);
            } else {
                match u8::from_str_radix(tok, 16) {
                    Ok(b) => {
                        bytes.push(b);
                        mask.push(true);
                    }
                    Err(_) => {
                        // Malformed pattern => treat as wildcard so we never
                        // panic in a ctor; uniqueness check below will likely
                        // fail loudly instead.
                        bytes.push(0);
                        mask.push(false);
                    }
                }
            }
        }
        (bytes, mask)
    }
}

/// Find every match of `pattern` across the image regions. Anchored on the
/// pattern's first non-wildcard byte via `memmem` for speed, then verified
/// byte-by-byte against the mask.
fn find_all(image: &MainImage, pattern: &Pattern) -> Vec<usize> {
    let (bytes, mask) = pattern.compile();
    let mut out = Vec::new();
    if bytes.is_empty() {
        return out;
    }
    // Anchor index = first significant byte (patterns here always start with a
    // significant byte, but be defensive).
    let anchor = mask.iter().position(|&m| m).unwrap_or(0);
    let anchor_needle = [bytes[anchor]];
    let n = bytes.len();
    let finder = memmem::Finder::new(&anchor_needle);

    for region in &image.regions {
        let len = region.end - region.start;
        if len < n {
            continue;
        }
        let slice = unsafe { std::slice::from_raw_parts(region.start as *const u8, len) };
        for hit in finder.find_iter(slice) {
            // hit is the position of the anchor byte; the pattern starts
            // `anchor` bytes earlier.
            if hit < anchor {
                continue;
            }
            let start = hit - anchor;
            if start + n > len {
                continue;
            }
            let candidate = &slice[start..start + n];
            if matches_mask(candidate, &bytes, &mask) {
                out.push(region.start + start);
            }
        }
    }
    out
}

#[inline]
fn matches_mask(haystack: &[u8], bytes: &[u8], mask: &[bool]) -> bool {
    for i in 0..bytes.len() {
        if mask[i] && haystack[i] != bytes[i] {
            return false;
        }
    }
    true
}

/// Resolve a pattern to its single runtime address.
///
/// Returns `None` (with a logged reason) when the pattern matches zero or more
/// than one site, or when the single match's file offset disagrees with the
/// documented `expected_file` — refusing to hook a wrong/ambiguous address is
/// the defensive choice; a missing hook degrades capture, a wrong hook can
/// crash the client.
pub fn resolve(image: &MainImage, pattern: &Pattern) -> Option<usize> {
    let hits = find_all(image, pattern);
    match hits.len() {
        1 => {
            let addr = hits[0];
            let file_off = addr
                .wrapping_sub(image.slide as usize)
                .wrapping_sub(MACHO_IMAGE_BASE as usize);
            if file_off != pattern.expected_file {
                crate::log(&format!(
                    "sig '{}' matched at file 0x{:x} but expected 0x{:x} — refusing (binary drift?)",
                    pattern.name, file_off, pattern.expected_file
                ));
                return None;
            }
            Some(addr)
        }
        0 => {
            crate::log(&format!(
                "sig '{}' matched 0 sites — capture point disabled",
                pattern.name
            ));
            None
        }
        n => {
            crate::log(&format!(
                "sig '{}' matched {} sites (expected 1) — refusing to hook",
                pattern.name, n
            ));
            None
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn pattern_compiles_with_wildcards() {
        let p = Pattern::new("t", 0, "55 48 ?? E5 ??");
        let (bytes, mask) = p.compile();
        assert_eq!(bytes, vec![0x55, 0x48, 0x00, 0xE5, 0x00]);
        assert_eq!(mask, vec![true, true, false, true, false]);
    }

    #[test]
    fn mask_match() {
        let bytes = vec![0x55, 0x00, 0xE5];
        let mask = vec![true, false, true];
        assert!(matches_mask(&[0x55, 0xAB, 0xE5], &bytes, &mask));
        assert!(matches_mask(&[0x55, 0x00, 0xE5], &bytes, &mask));
        assert!(!matches_mask(&[0x55, 0xAB, 0xE6], &bytes, &mask));
        assert!(!matches_mask(&[0x54, 0xAB, 0xE5], &bytes, &mask));
    }
}
