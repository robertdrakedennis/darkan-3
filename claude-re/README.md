# claude-re — Production packet session, reverse-engineered

Headless Ghidra RE of the last production packet session
(`build/undercut-socket-session-production-isaac.jsonl`) against the RS3 NXT **948-5** client.
Isolated workspace — no MCP (another agent held it), no reliance on prior `docs/`, every claim validated or marked.

## Start here
- **`findings/00-SESSION-SUMMARY.md`** — the full world-entry story + decoded wire formats + completeness proof.
- **`PROGRESS.md`** — iteration-by-iteration log.

## Decoded + validated
- **Connections**: lobby 8.42.17.253 + world 8.26.16.145, both :443, ISAAC (delta 50).
- **Login handshake**: conn-type 14; lobby opc 19 / RSA 644B; world opc 16 / RSA 664B; result code 2; session keys. (`34-login-flow`)
- **World list (full)**: 20 regions (13 standard + 7 custom), per-world flags, 133 hosts, **LIVE counts (18,127 online)**, 54 news headlines. (`33-lobby`, `36-worldlist-complete`)
- **Protocol**: 218 opcodes w/ definitive sizes (37/37 validated), 192 handlers decompiled, opcode→handler map. (`11`,`12`,`14`,`20-24`)
- **Player**: skills (fresh lvl1, Constitution 10), right-click menu, **inventory byte-exact**, equipment, **1617 varps**, name, MOTD. (`30`,`31`,`32`)
- **UI**: HUD root 1477 (197 comps), 53 sub-interfaces positioned, 42 event-wired (op35), 28 CS2 build scripts. (`30`)
- **Scene**: op81 XTEA build, ground items, ~95 NPCs (count validated), zone footprint. (`30`)
- **Anti-cheat algorithm** + full C2S telemetry. (`35`)
- **Completeness PROVEN** (byte accounting = 100% of TCP bytes). **Correctness CROSS-VALIDATED** (lobby vs world account state ~99.7% identical).

## Characterized frontiers (blocked by tested mechanism, never faked)
- **Appearance pixels** — cipher-scrambled (`gScrambled*` transform derived from code; keystream entangled w/ session cipher).
- **GPI/NPC per-entity positions** — runtime-dispatched (.bss handler table; handler fns reachable by name, bit-loop not reconstructed).
- **op82 pixel coords** — cache-defined (x/y all-zero on the wire).
- **Item names / CS2 script logic** — live in the cache, not this capture.

## Integrity record (over-reaches caught + corrected)
op78 absolute→relative · op216 counts 855k→18,127 · world-def drift→gap-validated · op22 handler false-match flagged ·
op98 "CRC"→render report · op82 coords→cache-defined.

## Findings index
`00-OVERVIEW` `00-SESSION-SUMMARY` `01-opcodes-all` `02-world-s2c-sequence` `10-prot-registration` `11-handlers-all`
`12-opcode-map` `13-dispatch` `14-serverprot-table` `15-bitparse` `16-gpi-decoders` `17-coord-reader` `18-appearance`
`19-scramble` `20-interfaces` `21-player` `22-inventory` `23-world-state` `24-other` `29-session-payloads`
`30-session-decoded` `31-player-complete` `32-player-varps` `33-lobby` `34-login-flow` `35-c2s` `36-worldlist-complete`
(`24-js5` is the other agent's JS5 work, left untouched.)
