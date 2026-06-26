# Session flow / timeline / state-machine (from build/undercut-flow-session-production-isaac.jsonl)

A SECOND production capture (34KB) — a high-level FLOW view (not raw packets). Gives the timeline + state machine + validation
counts that the 1MB socket-session lacked. (Corrects iter32 "no timestamps" — timing was in THIS capture.)

## Session metadata
- session_id: session-20260622-152729-production-isaac (2026-06-22 15:27:29). duration **139,835 ms (~140s)**. 12,486 events, session_stop=1.

## Client MAIN-STATE machine (the lobby->world handoff)
1. **LOGIN_SCREEN  (state 10)** @ cycle 9567,  epoch 1782156449894  [phase LOGIN]
2. **LOBBY_SCREEN  (state 20)** @ cycle 14424, epoch 1782156547027  [phase WORLD_SELECT]
3. **LOGGED_IN     (state 30)** @ cycle 14927, epoch 1782156557082  [phase IN_GAME]

## Phase timeline (durations)
- **LOGIN**: cycles 9567-14423, **97,072 ms (~97s)**, 4857 ticks (the long JS5/login phase).
- **WORLD_SELECT (lobby)**: cycles 14424-14926, **9,995 ms (~10s)**, **1670 server packets** (world list + account state).
- **IN_GAME (world)**: cycles 14927-16546, **32,330 ms (~32s)**, **3795 server + 31 client packets** (world entry + render).
- State deltas: LOGIN->LOBBY = 97,133 ms; LOBBY->IN_GAME = 10,055 ms.

## world_traffic — independent counts (CROSS-VALIDATE the socket-session decode)
- anti_cheat_challenge: **5** (= my op174 = 5 EXACT). client_keepalive: **31** (= my op51 Ping = 31 EXACT).
- player_info: 55 (~ my op22 = 54). npc_info: 54 (~ my op52 = 53). zone_update: 66 (counts zones, not the 680 sub-packets).
- has_required_traffic: TRUE. label_summary: 0 unknown / 0 unresolved packet kinds (deframer fully resolved everything).

=> The session: ~97s login -> ~10s lobby (world list) -> click into world -> ~32s in-game (HUD+player+scene) -> stop. ~140s total.
   The state machine (10 LOGIN -> 20 LOBBY -> 30 LOGGED_IN) is the lobby->world handoff at the client-state level.

## Data-source audit + cross-validation (iter56)
- CONFIRMED target: undercut-socket-session-production-isaac (real peer 8.42.17.253) = THE production session. Correctly analyzed.
- Other build/ captures are LOCALHOST dev-server tests (capture-65205/28871: peer ::1, roles js5+lobby, 21 desyncs) - NOT production.
  (These are the user's local server under test; the desyncs = where their server diverges from client expectations - project-relevant but out of this loop's "production" scope.)
- flow-session packet_count CROSS-VALIDATES the socket decode: op61=1176+1132, op35=871, op28=491+438, op82=56, op22=55, op52=54,
  op110=86, op47=61, op92=35 ... all match (±1 at phase boundaries). Adds metadata: per-opcode category, codec_size, payload bytes, cycle range.
