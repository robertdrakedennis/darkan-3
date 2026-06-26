# Production capture — overview (built fresh from the jsonl, no external docs)

Source: `build/undercut-socket-session-production-isaac.jsonl` — 5568 records (5553 packets, 2 connections, 13 login events)

## Connections

| role | peer | port | C2S bytes | S2C bytes |
|---|---|---|---|---|
| lobby | 8.42.17.253:443 | 443 | 797 | 18339 |
| world | 8.26.16.145:443 | 443 | 2799 | 47350 |

## Login events (in order)

- lobby S2C **first_response** code=0 len=None key=0x003ca49c2a0723bf
- lobby S2C **login_result** code=2 len=None key=None
- lobby S2C **login_data_len** code=None len=111 key=None
- lobby C2S **connection_type** code=None len=None key=None
- lobby C2S **login_packet** code=None len=None key=None
- world S2C **first_response** code=0 len=None key=0x007ef65aec58dfe7
- world S2C **login_result** code=2 len=None key=None
- world S2C **server_client_var_block_len** code=None len=1321 key=None
- world S2C **server_client_var_ack** code=None len=None key=None
- world S2C **players_byte** code=None len=None key=None
- world S2C **world_login_data_len** code=None len=37 key=None
- world C2S **connection_type** code=None len=None key=None
- world C2S **login_packet** code=None len=None key=None

## Packet counts by conn/dir

- lobby S2C: 1670
- lobby C2S: 13
- world S2C: 3790
- world C2S: 80
