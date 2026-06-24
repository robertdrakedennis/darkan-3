//! dyld image enumeration + `LC_SEGMENT_64` walk.
//!
//! Mirrors `client/launcher/patcher-mac/src/lib.rs` verbatim (the proven
//! mechanic): match the rs2client main image by name, compute its ASLR slide,
//! and record each segment's runtime-slid bounds + protection. The recorder
//! needs this ONLY for the inline seed hook (pattern-scanning `__TEXT` and
//! `__const`). The socket interposes need none of this — dyld resolves them by
//! name with no address knowledge.

use std::ffi::CStr;
use std::os::raw::c_char;

const LC_SEGMENT_64: u32 = 0x19;

/// `struct mach_header_64` — we only read `ncmds`; the rest is laid out for ABI
/// fidelity so `size_of` steps correctly to the load commands.
#[repr(C)]
pub struct MachHeader64 {
    pub magic: u32,
    pub cputype: i32,
    pub cpusubtype: i32,
    pub filetype: u32,
    pub ncmds: u32,
    pub sizeofcmds: u32,
    pub flags: u32,
    pub reserved: u32,
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
    segname: [c_char; 16],
    vmaddr: u64,
    vmsize: u64,
    fileoff: u64,
    filesize: u64,
    maxprot: i32,
    initprot: i32,
    nsects: u32,
    flags: u32,
}

extern "C" {
    fn _dyld_image_count() -> u32;
    fn _dyld_get_image_name(image_index: u32) -> *const c_char;
    fn _dyld_get_image_header(image_index: u32) -> *const MachHeader64;
    fn _dyld_get_image_vmaddr_slide(image_index: u32) -> isize;
}

/// A loaded segment of the rs2client image (runtime-slid bounds + protection).
pub struct Segment {
    pub name: String,
    pub start: usize, // vmaddr + slide
    pub end: usize,   // vmaddr + slide + vmsize
    pub initprot: i32,
}

/// Find the rs2client main image among loaded dyld images, returning its
/// segments + matched image name. Matches by `strstr(name, "rs2client")` so it
/// is robust regardless of image index. `None` if no rs2client image is loaded
/// (e.g. injected into a wrapper process — the recorder then runs
/// interpose-only).
pub fn find_rs2client_segments() -> Option<(Vec<Segment>, String)> {
    unsafe {
        let count = _dyld_image_count();
        for i in 0..count {
            let name_ptr = _dyld_get_image_name(i);
            if name_ptr.is_null() {
                continue;
            }
            let name = CStr::from_ptr(name_ptr).to_string_lossy();
            if !name.contains("rs2client") {
                continue;
            }
            let hdr = _dyld_get_image_header(i);
            if hdr.is_null() {
                continue;
            }
            let slide = _dyld_get_image_vmaddr_slide(i);
            let segments = parse_segments(hdr, slide);
            if segments.is_empty() {
                continue;
            }
            return Some((segments, name.into_owned()));
        }
    }
    None
}

/// Walk the load commands, recording each `LC_SEGMENT_64`'s runtime-slid range,
/// name, and initprot.
///
/// # Safety
/// `hdr` must point at a valid, mapped `mach_header_64` followed by `ncmds`
/// well-formed load commands (guaranteed by dyld for a loaded image).
fn parse_segments(hdr: *const MachHeader64, slide: isize) -> Vec<Segment> {
    let mut out = Vec::new();
    unsafe {
        let ncmds = (*hdr).ncmds;
        let mut lc =
            (hdr as *const u8).add(std::mem::size_of::<MachHeader64>()) as *const LoadCommand;
        for _ in 0..ncmds {
            let cmd = (*lc).cmd;
            let cmdsize = (*lc).cmdsize as usize;
            if cmdsize == 0 {
                break;
            }
            if cmd == LC_SEGMENT_64 {
                let sg = lc as *const SegmentCommand64;
                let name = cstr_field_to_string(&(*sg).segname);
                let start = ((*sg).vmaddr as isize).wrapping_add(slide) as usize;
                let end = start.wrapping_add((*sg).vmsize as usize);
                out.push(Segment {
                    name,
                    start,
                    end,
                    initprot: (*sg).initprot,
                });
            }
            lc = (lc as *const u8).add(cmdsize) as *const LoadCommand;
        }
    }
    out
}

fn cstr_field_to_string(field: &[c_char]) -> String {
    let bytes: Vec<u8> = field
        .iter()
        .take_while(|&&c| c != 0)
        .map(|&c| c as u8)
        .collect();
    String::from_utf8_lossy(&bytes).into_owned()
}
