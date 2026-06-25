# Login handshake flow — both connections (from login_event + connection records)

The capture is a LIVE RS3 production login. Two TCP connections, both :443, ISAAC-ciphered (isaac_delta 50).

## Connections
- **Lobby**: peer 8.42.17.253:443. C2S 797B, S2C 18339B. ISAAC seeds (c2s): e7cf8696,be634eb7,0ce30010,e921a349.
- **World**: peer 8.26.16.145:443. C2S 2799B, S2C 47350B. ISAAC seeds (c2s): f37aa346,56d41356,7e614a1c,c7e50b9b.

## Lobby login sequence
1. C2S **connection_type 14** (login).
2. S2C **first_response code 0** + **session_key 0x003ca49c2a0723bf** (server challenge for RSA/ISAAC seeding).
3. C2S **login_packet opcode 19**, RSA **block_size 644** (encrypted credentials/seeds block).
4. S2C **login_result code 2** (= success).
5. S2C **login_data_len 111** (player data block: rights/member/displayname etc.).
   -> then the lobby UI + world list (op216) + account state (varps/skills) stream.

## World login sequence (after Play Now)
1. C2S **connection_type 14**.
2. S2C **first_response code 0** + **session_key 0x007ef65aec58dfe7**.
3. C2S **login_packet opcode 16**, RSA **block_size 664**.
4. S2C **login_result code 2** (success).
5. S2C **server_client_var_block_len 1321** + **server_client_var_ack flag 1** (server client vars).
6. S2C **players_byte value 2** (local player world index / GPI seed).
7. S2C **world_login_data_len 37** (world login data).
   -> then op81 RebuildNormal + world-entry stream (HUD, player, npcs, inv, zone).

## Key protocol constants
- Connection type byte = **14** (login) for both lobby and world.
- Login packet opcode: **19 = lobby**, **16 = world**. RSA block: 644B (lobby) / 664B (world).
- login_result success code = **2**.
- Server session key = 8 bytes (u64), sent in first_response, seeds the ISAAC/RSA.
- World list (op216): **133 worlds** (world1..world141ish .runescape.com), 13 regions.
