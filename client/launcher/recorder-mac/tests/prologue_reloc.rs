//! Offline validation that the inline-detour prologue relocation is SAFE for
//! every recorder hook target, using the real RE binary's bytes.
//!
//! This guards the load-bearing "never crash the client" requirement: it
//! decodes each documented function's prologue, confirms at least a 14-byte
//! absolute-jump's worth of whole instructions can be stolen, and re-encodes
//! them at a synthetic trampoline address via the SAME `iced-x86` BlockEncoder
//! the dylib uses — proving the stolen prologue (including the RIP-relative
//! `inc [rip+...]` in ClientStream::Read and the `cmp; jz` in SetMainState)
//! relocates cleanly.
//!
//! Ignored by default (needs the RE binary checked out). Run with:
//!   cargo test --release --test prologue_reloc -- --ignored --nocapture

#![cfg(target_os = "macos")]

use iced_x86::{
    BlockEncoder, BlockEncoderOptions, Decoder, DecoderOptions, Instruction, InstructionBlock,
};
use std::path::PathBuf;

const ABS_JMP_LEN: usize = 14;

// (name, file offset == vmaddr - 0x100000000) — the exact hook targets. The
// s2c dispatch site is a MID-FUNCTION inline observer target (file 0x6d8ea); its
// stolen window must relocate just like the entry targets (it has no RIP-relative
// instruction in the first 14 bytes — verified — so it relocates trivially).
const TARGETS: &[(&str, usize)] = &[
    ("ReadPacket", 0x6c920),
    ("ReadPacket::s2cDispatch(inline)", 0x6d8ea),
    ("TcpConnectionMessage::Init", 0x57d20),
    ("FlushOutgoingQueue", 0x41d120),
    ("ClientStream::Read", 0x8dbf60),
    ("ClientStream::Write", 0x8dc340),
    ("Client::SetMainState", 0x27ff50),
    ("Isaac::Init", 0x71a10),
    ("ClientStream::Fill", 0x8dc070),
    // New lobby/login hooks (RE §8a). OpenLoginStream's first 14 bytes include a
    // rel8 `jne` whose target is OUTSIDE the stolen window — the BlockEncoder
    // widens it to rel32 in the trampoline, so it must still relocate cleanly.
    ("LoginStateMachine::OpenLoginStream", 0xc96a0),
    ("ConnectionManager::SetupLoginCiphers", 0xca170),
];

fn re_binary() -> Option<PathBuf> {
    // tests run with CWD = crate dir (client/launcher/recorder-mac).
    let p = PathBuf::from(env!("CARGO_MANIFEST_DIR"))
        .join("../../../re-resources/948-5/rs2client.948-5-mac");
    if p.exists() {
        Some(p)
    } else {
        None
    }
}

fn steal(code: &[u8], ip: u64, min: usize) -> (Vec<Instruction>, usize) {
    let mut dec = Decoder::with_ip(64, code, ip, DecoderOptions::NONE);
    let mut instrs = Vec::new();
    let mut total = 0usize;
    while total < min {
        assert!(dec.can_decode(), "ran out of bytes");
        let insn = dec.decode();
        assert!(!insn.is_invalid(), "invalid instruction at +0x{total:x}");
        total += insn.len();
        instrs.push(insn);
    }
    (instrs, total)
}

#[test]
#[ignore]
fn all_targets_relocate_cleanly() {
    let Some(bin) = re_binary() else {
        eprintln!("RE binary not present — skipping (this test needs re-resources checked out)");
        return;
    };
    let data = std::fs::read(&bin).expect("read RE binary");
    let image_base = 0x1_0000_0000u64;

    for &(name, off) in TARGETS {
        let entry_vmaddr = image_base + off as u64;
        let code = &data[off..off + ABS_JMP_LEN + 15];

        let (instrs, stolen) = steal(code, entry_vmaddr, ABS_JMP_LEN);
        assert!(
            stolen >= ABS_JMP_LEN,
            "{name}: stole {stolen} < {ABS_JMP_LEN}"
        );

        // Re-encode at a synthetic trampoline address that is DIFFERENT from the
        // original (so relative/RIP-relative operands must be rewritten) but
        // still within ±2GB — exactly the invariant `reserve_near` enforces at
        // runtime. A trampoline >2GB away cannot relocate the RIP-relative
        // `inc [rip+...]` in ClientStream::Read, which is the whole reason the
        // dylib allocates the trampoline near the target.
        let tramp_rip = entry_vmaddr + 0x10_0000; // +1 MiB, well inside ±2GB
        let block = InstructionBlock::new(&instrs, tramp_rip);
        let res = BlockEncoder::encode(64, block, BlockEncoderOptions::NONE)
            .unwrap_or_else(|e| panic!("{name}: BlockEncoder failed: {e}"));

        eprintln!(
            "{name:28} stole {stolen} bytes ({} insns) -> trampoline {} bytes  OK",
            instrs.len(),
            res.code_buffer.len()
        );
    }
}
