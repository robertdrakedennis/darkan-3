//! Minimal x86_64 inline trampoline detour engine.
//!
//! Why hand-rolled: every off-the-shelf Rust detour crate (retour, detour2,
//! minhook, dobby) either requires nightly or fails to build/link for
//! `x86_64-apple-darwin`. The hook sits on the client's hot networking path and
//! a wrong relocation crashes the client, so correctness is non-negotiable —
//! we therefore use the pure-Rust `iced-x86` decoder + `BlockEncoder`, which is
//! exactly the machinery minhook/retour use internally (decode the stolen
//! prologue, re-encode it at the trampoline's address so all RIP-relative and
//! relative-branch operands are fixed up).
//!
//! Mechanism (standard 64-bit inline hook):
//!   1. Decode whole instructions at `target` until >= 14 bytes are covered
//!      (14 = the size of an x86_64 absolute `JMP [rip+0]; <abs64>`).
//!   2. Allocate a trampoline page. Re-encode the stolen instructions there via
//!      `BlockEncoder` at the trampoline's runtime address, then append a 14-byte
//!      absolute jump back to `target + stolen_len`.
//!   3. Overwrite `target`'s first 14 bytes with an absolute jump to `detour`.
//!
//! Calling the trampoline runs the original prologue then resumes the original
//! function body — i.e. it behaves exactly like calling the un-hooked target.
//!
//! THREAD-SAFETY of the entry patch: overwriting a live function entry is only
//! safe if no thread is currently executing inside the stolen bytes. We rely on
//! the same invariant as the RSA patcher — installation happens in the dylib's
//! `#[ctor]`, which dyld runs BEFORE the host's `main()`. At that point the
//! client has not yet spawned its networking/render threads, so none of the
//! target functions can be mid-execution. We therefore do not suspend threads.
//! (Verified against all 6 targets in `tests/prologue_reloc.rs`: every prologue
//! steals >=14 whole bytes and re-encodes cleanly within +/-2GB.)

use iced_x86::{
    BlockEncoder, BlockEncoderOptions, Decoder, DecoderOptions, Instruction, InstructionBlock,
};
use std::os::raw::c_void;

const ABS_JMP_LEN: usize = 14; // FF 25 00000000  +  imm64
const PAGE_SIZE: usize = 4096;

// -- mach_vm code-patching FFI ------------------------------------------------
//
// CRITICAL (Rosetta): the rs2client mac build is x86_64-only, so on Apple
// Silicon it runs under Rosetta 2, where `mprotect(PROT_WRITE|PROT_EXEC)` on the
// main image's `__text` is REFUSED (EPERM) — verified. The working primitive is
// `mach_vm_protect` with `VM_PROT_COPY`, which forces a private writable copy of
// the page; it succeeds under Rosetta AND on native Intel. We patch in the ctor
// (pre-main), before Rosetta has translated the target, so the first execution
// translates our patched bytes.

type MachPortT = u32;
type KernReturnT = i32;
type MachVmAddressT = u64;
type MachVmSizeT = u64;
type VmProtT = i32;

const KERN_SUCCESS: KernReturnT = 0;
const VM_PROT_READ: VmProtT = 0x1;
const VM_PROT_WRITE: VmProtT = 0x2;
const VM_PROT_EXECUTE: VmProtT = 0x4;
const VM_PROT_COPY: VmProtT = 0x10;

extern "C" {
    fn mach_task_self() -> MachPortT;
    fn mach_vm_protect(
        target_task: MachPortT,
        address: MachVmAddressT,
        size: MachVmSizeT,
        set_maximum: u32, // boolean_t
        new_protection: VmProtT,
    ) -> KernReturnT;
}

/// An installed detour. Holds the executable trampoline so it lives as long as
/// the hook is active (we never uninstall — the process exits with the client).
pub struct Detour {
    trampoline_ptr: *mut u8,
    trampoline_len: usize,
}

unsafe impl Send for Detour {}
unsafe impl Sync for Detour {}

impl Detour {
    /// Pointer to call the ORIGINAL function (the trampoline).
    pub fn trampoline(&self) -> *const c_void {
        self.trampoline_ptr as *const c_void
    }
}

impl Drop for Detour {
    fn drop(&mut self) {
        // In practice every Detour is parked for the process lifetime, but if
        // one is ever dropped, release its trampoline page. We do NOT restore
        // the patched entry (the target's original bytes are inside the
        // trampoline, not retained here) — dropping a live hook is a logic bug,
        // so we only free our own allocation.
        if !self.trampoline_ptr.is_null() && self.trampoline_len > 0 {
            unsafe {
                libc::munmap(self.trampoline_ptr as *mut c_void, self.trampoline_len);
            }
        }
    }
}

#[derive(Debug)]
pub enum DetourError {
    Decode(String),
    Encode(String),
    Alloc,
    Protect,
}

impl std::fmt::Display for DetourError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            DetourError::Decode(s) => write!(f, "decode: {s}"),
            DetourError::Encode(s) => write!(f, "encode: {s}"),
            DetourError::Alloc => write!(f, "trampoline alloc failed"),
            DetourError::Protect => write!(f, "mprotect failed"),
        }
    }
}

/// Build the 14-byte absolute jump `JMP qword ptr [rip+0]; <dest as u64>`.
fn abs_jmp(dest: usize) -> [u8; ABS_JMP_LEN] {
    let mut b = [0u8; ABS_JMP_LEN];
    b[0] = 0xFF;
    b[1] = 0x25;
    // rip+0 displacement
    b[2..6].copy_from_slice(&0u32.to_le_bytes());
    b[6..14].copy_from_slice(&(dest as u64).to_le_bytes());
    b
}

/// Decode whole instructions at `code` (runtime address `ip`) until at least
/// `min` bytes are covered. Returns (instructions, total_bytes).
fn steal_prologue(
    code: &[u8],
    ip: u64,
    min: usize,
) -> Result<(Vec<Instruction>, usize), DetourError> {
    let mut dec = Decoder::with_ip(64, code, ip, DecoderOptions::NONE);
    let mut instrs = Vec::new();
    let mut total = 0usize;
    while total < min {
        if !dec.can_decode() {
            return Err(DetourError::Decode("ran out of bytes before min".into()));
        }
        let insn = dec.decode();
        if insn.is_invalid() {
            return Err(DetourError::Decode(format!(
                "invalid instruction at +0x{total:x}"
            )));
        }
        total += insn.len();
        instrs.push(insn);
    }
    Ok((instrs, total))
}

/// Reserve `len` bytes of RW memory within ±2GB of `near` (required so the
/// BlockEncoder can keep RIP-relative operands in the stolen prologue — e.g.
/// `ClientStream::Read`'s `inc [rip+0x8487af]` — within reach).
///
/// We probe page-aligned hint addresses stepping outward from `near` (both
/// directions) and accept the first `mmap` (no MAP_FIXED, so the kernel may
/// move it) whose result actually lands within ±2GB. Returns the mapping base
/// and its rounded length.
unsafe fn reserve_near(near: usize, len: usize) -> Option<(*mut c_void, usize)> {
    let rounded = (len + PAGE_SIZE - 1) & !(PAGE_SIZE - 1);
    const TWO_GB: usize = 0x7000_0000; // stay comfortably inside ±2GB
    const STEP: usize = 0x10_0000; // 1 MiB probe stride

    // Try increasing offsets above and below the target.
    let mut delta = STEP;
    while delta <= TWO_GB {
        for &signed in &[delta as isize, -(delta as isize)] {
            let hint = (near as isize).wrapping_add(signed) as usize & !(PAGE_SIZE - 1);
            if hint == 0 {
                continue;
            }
            let p = libc::mmap(
                hint as *mut c_void,
                rounded,
                libc::PROT_READ | libc::PROT_WRITE,
                libc::MAP_PRIVATE | libc::MAP_ANON,
                -1,
                0,
            );
            if p == libc::MAP_FAILED {
                continue;
            }
            let got = p as usize;
            let within = got.abs_diff(near) <= TWO_GB;
            if within {
                return Some((p, rounded));
            }
            // Too far — release and keep probing.
            libc::munmap(p, rounded);
        }
        delta += STEP;
    }
    None
}

/// Overwrite `addr..addr+bytes.len()` with `bytes` in the MAIN IMAGE's code.
///
/// Uses `mach_vm_protect(VM_PROT_READ|WRITE|COPY)` — the only mechanism that can
/// make the main binary's `__text` writable under Rosetta (mprotter RWX is
/// EPERM there). Falls back to `mprotect` if the mach call is unavailable (no
/// downside on native Intel where both work). Restores `R|X` after the write.
unsafe fn patch_code(addr: usize, bytes: &[u8]) -> bool {
    let page_start = addr & !(PAGE_SIZE - 1);
    let page_end = (addr + bytes.len() + PAGE_SIZE - 1) & !(PAGE_SIZE - 1);
    let total = (page_end - page_start) as MachVmSizeT;

    let task = mach_task_self();
    let kr = mach_vm_protect(
        task,
        page_start as MachVmAddressT,
        total,
        0,
        VM_PROT_READ | VM_PROT_WRITE | VM_PROT_COPY,
    );
    if kr != KERN_SUCCESS {
        // Fallback: plain mprotect (works on native Intel; EPERM under Rosetta).
        if libc::mprotect(
            page_start as *mut c_void,
            total as usize,
            libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
        ) != 0
        {
            return false;
        }
    }

    std::ptr::copy_nonoverlapping(bytes.as_ptr(), addr as *mut u8, bytes.len());

    // Restore R|X (best effort — a failed restore still leaves a working hook).
    let kr = mach_vm_protect(
        task,
        page_start as MachVmAddressT,
        total,
        0,
        VM_PROT_READ | VM_PROT_EXECUTE,
    );
    if kr != KERN_SUCCESS {
        let _ = libc::mprotect(
            page_start as *mut c_void,
            total as usize,
            libc::PROT_READ | libc::PROT_EXEC,
        );
    }
    true
}

/// Install an inline detour at `target` redirecting to `detour`. On success the
/// returned `Detour::trampoline()` calls the original function.
///
/// # Safety
/// `target` must be a valid, executable function entry with at least
/// `ABS_JMP_LEN` bytes of decodable prologue and no jump landing inside those
/// bytes. All recorder targets are documented function entries that satisfy
/// this; we still validate the decode and refuse on any anomaly.
pub unsafe fn install(target: usize, detour: usize) -> Result<Detour, DetourError> {
    // Read enough prologue bytes to cover the abs-jmp plus the largest single
    // instruction (15 bytes) so the last stolen instruction is never truncated.
    let read_len = ABS_JMP_LEN + 15;
    let code = std::slice::from_raw_parts(target as *const u8, read_len);

    let (instrs, stolen) = steal_prologue(code, target as u64, ABS_JMP_LEN)?;

    // Allocate the trampoline WITHIN ±2GB of the target. This is mandatory: a
    // stolen RIP-relative instruction (e.g. ClientStream::Read's
    // `inc [rip+0x8487af]`) can only be re-encoded if the trampoline is within
    // rel32 reach of the data it references. A generous cap (stolen*4 + tail)
    // covers any branch-widening the encoder performs.
    let tramp_cap = ((stolen * 4) + ABS_JMP_LEN + PAGE_SIZE) & !(PAGE_SIZE - 1);
    let (raw, tramp_cap) = match reserve_near(target, tramp_cap) {
        Some(v) => v,
        None => return Err(DetourError::Alloc),
    };
    let tramp_addr = raw as usize;

    // Re-encode stolen instructions at the trampoline's real address.
    let block = InstructionBlock::new(&instrs, tramp_addr as u64);
    let encoded = BlockEncoder::encode(64, block, BlockEncoderOptions::NONE)
        .map_err(|e| DetourError::Encode(e.to_string()))?;
    let mut tramp_bytes = encoded.code_buffer;

    // Append absolute jump back to (target + stolen).
    let back = abs_jmp(target + stolen);
    tramp_bytes.extend_from_slice(&back);

    if tramp_bytes.len() > tramp_cap {
        libc::munmap(raw, tramp_cap);
        return Err(DetourError::Encode("trampoline overflow".into()));
    }

    std::ptr::copy_nonoverlapping(
        tramp_bytes.as_ptr(),
        tramp_addr as *mut u8,
        tramp_bytes.len(),
    );
    if libc::mprotect(raw, tramp_cap, libc::PROT_READ | libc::PROT_EXEC) != 0 {
        libc::munmap(raw, tramp_cap);
        return Err(DetourError::Protect);
    }

    // Finally, redirect the target entry to the detour.
    let jmp = abs_jmp(detour);
    if !patch_code(target, &jmp) {
        libc::munmap(raw, tramp_cap);
        return Err(DetourError::Protect);
    }

    Ok(Detour {
        trampoline_ptr: tramp_addr as *mut u8,
        trampoline_len: tramp_cap,
    })
}

// ---------------------------------------------------------------------------
// Inline (mid-function) observer detour
// ---------------------------------------------------------------------------
//
// `install()` above is an ENTRY-POINT hook: the detour runs at the SysV ABI
// boundary, so it may freely clobber caller-saved registers. That is unsafe at a
// MID-FUNCTION site (e.g. ConnectionManager::ReadPacket's inline s2c dispatch at
// file 0x6d8ea) where the client's live registers (R15/RSI/RDI/R8/…) must be
// intact when execution resumes.
//
// `install_inline()` therefore installs a register-PRESERVING observer: the
// target site is redirected to a trampoline that (1) pushes the full GPR set +
// FLAGS to a stack frame, (2) calls a read-only `extern "C"` observer with a
// pointer to that frame, (3) restores every register + FLAGS exactly, then (4)
// runs the relocated stolen prologue and jumps back. The observer can read the
// client's registers and memory but cannot perturb control flow — it is a pure
// tap, the inline analogue of the entry-hook capture.
//
// The save/restore is fixed, well-known machine code (push/pop sequences); only
// the stolen prologue is relocated, via the SAME trusted iced-x86 BlockEncoder
// used by `install()`. The push order below defines the `Regs` frame layout that
// `hooks.rs` reads.

/// The saved register frame an inline observer receives (a pointer to this).
/// Field order MUST match the `push` order emitted in `install_inline` (the
/// observer trampoline pushes r15..rax, so `rax` is at the LOWEST address — i.e.
/// `Regs` field order top-to-bottom is rax, rbx, …, r15).
#[repr(C)]
#[derive(Clone, Copy, Debug)]
pub struct Regs {
    pub rax: u64,
    pub rbx: u64,
    pub rcx: u64,
    pub rdx: u64,
    pub rsi: u64,
    pub rdi: u64,
    pub rbp: u64,
    pub r8: u64,
    pub r9: u64,
    pub r10: u64,
    pub r11: u64,
    pub r12: u64,
    pub r13: u64,
    pub r14: u64,
    pub r15: u64,
}

/// An `extern "C"` observer for an inline detour. Receives a pointer to the live
/// register frame; MUST NOT mutate it (control flow + register state are
/// restored verbatim afterwards) and MUST NOT unwind.
pub type InlineObserver = unsafe extern "C" fn(regs: *const Regs);

/// Emit the fixed save-frame prologue. Pushes FLAGS then r15..rax so that on
/// entry to the observer `RSP` points at a `Regs` (rax lowest). Returns the
/// bytes. The order is: pushfq; push r15; r14; …; rbx; push rax.
fn emit_save_frame() -> Vec<u8> {
    let mut b = Vec::new();
    b.push(0x9C); // pushfq
                  // push r15..r8  (REX.B + 0x50+reg)
    for r in [0x57u8, 0x56, 0x55, 0x54, 0x53, 0x52, 0x51, 0x50] {
        b.push(0x41); // REX.B
        b.push(r);
    }
    // push rbp, rdi, rsi, rdx, rcx, rbx, rax  (0x50+reg, no REX)
    for r in [0x55u8, 0x57, 0x56, 0x52, 0x51, 0x53, 0x50] {
        b.push(r);
    }
    b
}

/// Emit the matching restore epilogue: pop rax..r15 then popfq (exact inverse of
/// `emit_save_frame`, so every register + FLAGS is byte-identical afterwards).
fn emit_restore_frame() -> Vec<u8> {
    let mut b = Vec::new();
    // pop rax, rbx, rcx, rdx, rsi, rdi, rbp  (0x58+reg)
    for r in [0x58u8, 0x5B, 0x59, 0x5A, 0x5E, 0x5F, 0x5D] {
        b.push(r);
    }
    // pop r8..r15  (REX.B + 0x58+reg)
    for r in [0x58u8, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x5F] {
        b.push(0x41); // REX.B
        b.push(r);
    }
    b.push(0x9D); // popfq
    b
}

/// Build the call sequence to the observer with the saved frame as arg0.
/// At this point RSP points at the `Regs` frame; we set `RDI = RSP` (arg0) and
/// keep a 16-byte-aligned RSP at the `call`.
///
/// Alignment: the inline sites we hook are mid-function, where (by SysV prologue
/// accounting) RSP is congruent to 8 mod 16 on entry to the patched site. After
/// `pushfq` (8) + 15 pushes (120) = 128 bytes, RSP is again 8 mod 16. SysV
/// requires RSP ≡ 0 mod 16 at the `call` instruction, so we `sub rsp,8` first
/// (and `add rsp,8` after). We pass the frame pointer as the PRE-adjustment RSP
/// so the observer's `Regs*` is correct regardless of the alignment padding.
fn emit_observer_call(observer: usize) -> Vec<u8> {
    let mut b = Vec::new();
    // mov rdi, rsp           (48 89 E7)  — arg0 = &Regs (frame top == current RSP)
    b.extend_from_slice(&[0x48, 0x89, 0xE7]);
    // sub rsp, 8             (48 83 EC 08)  — 16-byte align for the call
    b.extend_from_slice(&[0x48, 0x83, 0xEC, 0x08]);
    // mov rax, imm64         (48 B8 <observer>)
    b.extend_from_slice(&[0x48, 0xB8]);
    b.extend_from_slice(&(observer as u64).to_le_bytes());
    // call rax               (FF D0)
    b.extend_from_slice(&[0xFF, 0xD0]);
    // add rsp, 8             (48 83 C4 08)  — undo the alignment pad
    b.extend_from_slice(&[0x48, 0x83, 0xC4, 0x08]);
    b
}

/// Install a register-preserving INLINE observer at `site` (a mid-function
/// address). `site` is redirected to a trampoline that saves all registers +
/// FLAGS, calls `observer(&Regs)` (read-only), restores everything, runs the
/// relocated stolen prologue, and jumps back to `site + stolen`. The returned
/// `Detour` owns the trampoline page (parked for the process lifetime).
///
/// # Safety
/// `site` must be a valid, executable instruction boundary with at least
/// `ABS_JMP_LEN` bytes of decodable instructions and NO branch target landing
/// inside those bytes (verified for the s2c dispatch site in
/// `tests/prologue_reloc.rs` and against the function disassembly). The observer
/// must not mutate `*regs` or unwind.
pub unsafe fn install_inline(site: usize, observer: InlineObserver) -> Result<Detour, DetourError> {
    let read_len = ABS_JMP_LEN + 15;
    let code = std::slice::from_raw_parts(site as *const u8, read_len);
    let (instrs, stolen) = steal_prologue(code, site as u64, ABS_JMP_LEN)?;

    // Trampoline budget: save frame + observer call + restore frame (all fixed,
    // small) + relocated stolen prologue (×4 for any branch widening) + the
    // 14-byte tail jump, page-rounded. Generous so we never overflow.
    let fixed = emit_save_frame().len()
        + emit_observer_call(observer as usize).len()
        + emit_restore_frame().len();
    let tramp_cap = (fixed + (stolen * 4) + ABS_JMP_LEN + PAGE_SIZE) & !(PAGE_SIZE - 1);
    let (raw, tramp_cap) = match reserve_near(site, tramp_cap) {
        Some(v) => v,
        None => return Err(DetourError::Alloc),
    };
    let tramp_addr = raw as usize;

    // Assemble the trampoline body. The stolen prologue must be relocated to run
    // AT its position inside the trampoline (after the save/call/restore), so we
    // compute that address before encoding so RIP-relative operands resolve.
    let save = emit_save_frame();
    let call = emit_observer_call(observer as usize);
    let restore = emit_restore_frame();
    let stolen_at = tramp_addr + save.len() + call.len() + restore.len();

    let block = InstructionBlock::new(&instrs, stolen_at as u64);
    let encoded = BlockEncoder::encode(64, block, BlockEncoderOptions::NONE)
        .map_err(|e| DetourError::Encode(e.to_string()))?;

    let mut tramp_bytes = Vec::with_capacity(tramp_cap);
    tramp_bytes.extend_from_slice(&save);
    tramp_bytes.extend_from_slice(&call);
    tramp_bytes.extend_from_slice(&restore);
    tramp_bytes.extend_from_slice(&encoded.code_buffer);
    tramp_bytes.extend_from_slice(&abs_jmp(site + stolen)); // back to after the stolen bytes

    if tramp_bytes.len() > tramp_cap {
        libc::munmap(raw, tramp_cap);
        return Err(DetourError::Encode("inline trampoline overflow".into()));
    }

    std::ptr::copy_nonoverlapping(
        tramp_bytes.as_ptr(),
        tramp_addr as *mut u8,
        tramp_bytes.len(),
    );
    if libc::mprotect(raw, tramp_cap, libc::PROT_READ | libc::PROT_EXEC) != 0 {
        libc::munmap(raw, tramp_cap);
        return Err(DetourError::Protect);
    }

    // Redirect the inline site to the observer trampoline.
    let jmp = abs_jmp(tramp_addr);
    if !patch_code(site, &jmp) {
        libc::munmap(raw, tramp_cap);
        return Err(DetourError::Protect);
    }

    Ok(Detour {
        trampoline_ptr: tramp_addr as *mut u8,
        trampoline_len: tramp_cap,
    })
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn abs_jmp_encoding() {
        let j = abs_jmp(0x1122_3344_5566_7788);
        assert_eq!(&j[0..6], &[0xFF, 0x25, 0x00, 0x00, 0x00, 0x00]);
        assert_eq!(&j[6..14], &0x1122_3344_5566_7788u64.to_le_bytes());
    }

    #[test]
    fn steal_covers_min() {
        // push rbp; mov rbp,rsp; push r15; push r14; push r13; sub rsp,0x28
        let code = [
            0x55, 0x48, 0x89, 0xE5, 0x41, 0x57, 0x41, 0x56, 0x41, 0x55, 0x48, 0x83, 0xEC, 0x28,
            0x90, 0x90,
        ];
        let (instrs, total) = steal_prologue(&code, 0x1000, ABS_JMP_LEN).unwrap();
        assert!(total >= ABS_JMP_LEN);
        assert!(!instrs.is_empty());
    }

    // -- Inline observer frame encoding -------------------------------------

    use iced_x86::{Code, Decoder, DecoderOptions, Mnemonic, Register};

    /// Decode a byte buffer into a flat list of (mnemonic, op0_register).
    fn decode_ops(code: &[u8]) -> Vec<(Mnemonic, Register)> {
        let mut dec = Decoder::with_ip(64, code, 0, DecoderOptions::NONE);
        let mut out = Vec::new();
        while dec.can_decode() {
            let insn = dec.decode();
            out.push((insn.mnemonic(), insn.op0_register()));
        }
        out
    }

    #[test]
    fn save_frame_pushes_flags_then_all_gprs_rax_lowest() {
        let ops = decode_ops(&emit_save_frame());
        // pushfq first, then 15 pushes (r15..rax). rax is pushed LAST so it ends
        // up at the LOWEST address — matching Regs.rax being the first field.
        let expected = [
            (Mnemonic::Pushfq, Register::None),
            (Mnemonic::Push, Register::R15),
            (Mnemonic::Push, Register::R14),
            (Mnemonic::Push, Register::R13),
            (Mnemonic::Push, Register::R12),
            (Mnemonic::Push, Register::R11),
            (Mnemonic::Push, Register::R10),
            (Mnemonic::Push, Register::R9),
            (Mnemonic::Push, Register::R8),
            (Mnemonic::Push, Register::RBP),
            (Mnemonic::Push, Register::RDI),
            (Mnemonic::Push, Register::RSI),
            (Mnemonic::Push, Register::RDX),
            (Mnemonic::Push, Register::RCX),
            (Mnemonic::Push, Register::RBX),
            (Mnemonic::Push, Register::RAX),
        ];
        assert_eq!(ops, expected);
    }

    #[test]
    fn restore_frame_is_exact_inverse_of_save() {
        let ops = decode_ops(&emit_restore_frame());
        // pops in reverse push order (rax..r15) then popfq.
        let expected = [
            (Mnemonic::Pop, Register::RAX),
            (Mnemonic::Pop, Register::RBX),
            (Mnemonic::Pop, Register::RCX),
            (Mnemonic::Pop, Register::RDX),
            (Mnemonic::Pop, Register::RSI),
            (Mnemonic::Pop, Register::RDI),
            (Mnemonic::Pop, Register::RBP),
            (Mnemonic::Pop, Register::R8),
            (Mnemonic::Pop, Register::R9),
            (Mnemonic::Pop, Register::R10),
            (Mnemonic::Pop, Register::R11),
            (Mnemonic::Pop, Register::R12),
            (Mnemonic::Pop, Register::R13),
            (Mnemonic::Pop, Register::R14),
            (Mnemonic::Pop, Register::R15),
            (Mnemonic::Popfq, Register::None),
        ];
        assert_eq!(ops, expected);
    }

    #[test]
    fn regs_layout_matches_push_order() {
        // The Regs frame is read off RSP after the pushes; offset of each field
        // must equal its push slot (rax @ 0x00 … r15 @ 0x70). 15 GPRs * 8 bytes.
        use std::mem::offset_of;
        assert_eq!(offset_of!(Regs, rax), 0x00);
        assert_eq!(offset_of!(Regs, rbx), 0x08);
        assert_eq!(offset_of!(Regs, rcx), 0x10);
        assert_eq!(offset_of!(Regs, rdx), 0x18);
        assert_eq!(offset_of!(Regs, rsi), 0x20);
        assert_eq!(offset_of!(Regs, rdi), 0x28);
        assert_eq!(offset_of!(Regs, rbp), 0x30);
        assert_eq!(offset_of!(Regs, r8), 0x38);
        assert_eq!(offset_of!(Regs, r15), 0x70);
        assert_eq!(std::mem::size_of::<Regs>(), 15 * 8);
    }

    #[test]
    fn observer_call_sets_rdi_aligns_and_embeds_target() {
        let target = 0x1234_5678_9abc_def0usize;
        let bytes = emit_observer_call(target);
        let ops = decode_ops(&bytes);
        // mov rdi,rsp ; sub rsp,8 ; mov rax,imm64 ; call rax ; add rsp,8
        assert_eq!(ops[0].0, Mnemonic::Mov);
        assert_eq!(ops[0].1, Register::RDI);
        assert_eq!(ops[1].0, Mnemonic::Sub);
        assert_eq!(ops[1].1, Register::RSP);
        assert_eq!(ops[2].0, Mnemonic::Mov);
        assert_eq!(ops[2].1, Register::RAX);
        assert_eq!(ops[3].0, Mnemonic::Call);
        assert_eq!(ops[4].0, Mnemonic::Add);
        assert_eq!(ops[4].1, Register::RSP);
        // The imm64 must be the observer address (mov rax, imm64 = 48 B8 ...).
        let mut dec = Decoder::with_ip(64, &bytes, 0, DecoderOptions::NONE);
        let _ = dec.decode(); // mov rdi,rsp
        let _ = dec.decode(); // sub rsp,8
        let mov = dec.decode();
        assert_eq!(mov.code(), Code::Mov_r64_imm64);
        assert_eq!(mov.immediate64(), target as u64);
    }
}
