# Audio ServerProt Handlers

> **Rev 947-1**: Opcodes reshuffled from 946. See `serverprot-table.md` for current opcode/size table.

Category: `jag::packethandlers::Audio`
Symbol dump: 16 lambdas (E_ through E14_) in `jag::packethandlers::Audio::Audio(jag::Client &)`
Client subsystem reference: `__DT_SYMTAB[0x4a2]` (SoundManager object)

All 16 audio handlers are registered inline in `BindHandlers` (0x0011852a), not via a separate constructor call.

## Handler Summary

| # | Name | Address | Payload Size | Description |
|---|------|---------|-------------|-------------|
| 1 | SYNTH_SOUND | `0x00191850` | 12 | Play synthesized sound effect |
| 2 | MIDI_SONG | `0x00191990` | 10 | Play MIDI music track |
| 3 | MIDI_JINGLE | `0x00191af0` | 10 | Play MIDI jingle (quest/level up) |
| 4 | MIDI_STOP | `0x00191c50` | 0 | Stop MIDI playback |
| 5 | MIDI_SWAP | `0x001e4180` | 5 | Cross-fade to new MIDI song |
| 6 | SOUND_GROUP | `0x00191c90` | 11+ | Play 3D positioned sound |
| 7 | SOUND_STOP | `0x00191f10` | 2 | Stop specific sound by ID |
| 8 | SOUND_STOP_ALL | `0x00192870` | 0 | Stop all sounds on buss 8 |
| 9 | SOUND_GROUP_SPEED | `0x00192470` | 6 | Set sound group playback speed |
| 10 | SOUND_GROUP_STOP | `0x0018d450` | 2 | Stop all instances of a sound group |
| 11 | SOUND_MODIFY | `0x0018d0e0` | 4 | Modify active sound speed/volume |
| 12 | SOUND_AREA | `0x001c1d20` | 4 | Trigger area sound via SoundManager |
| 13 | SOUND_AREA_SYNTH | `0x0018da50` | 8 | Create area synth sound |
| 14 | SOUND_MIXBUSS_SETLEVEL | `0x001917e0` | 5 | Set mix buss volume level |
| 15 | VORBIS_SONG | `0x00192360` | 6 | Play Vorbis-encoded music |
| 16 | VORBIS_PRELOAD | `0x001e4110` | 4 | Preload Vorbis audio resource |

## Detailed Handler Analysis

### SYNTH_SOUND (0x00191850)

**Packet format:** 12 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 4 | uint | soundId | Sound resource ID (big-endian swapped) |
| 4 | 1 | byte | volume | Volume level |
| 5 | 2 | ushort | loopDelay | Delay between loops (big-endian swapped) |
| 7 | 1 | byte | loopCount | Number of times to loop |
| 8 | 2 | ushort | delay | Initial delay before playback (big-endian swapped) |
| 10 | 2 | ushort | attenuation | Distance attenuation (big-endian swapped) |

**Behavior:** Creates a synthesized sound instance via `jag::game::SoundManager::ClaimVorbis` (FUN_00ce83b0). The core sound creation function used by most audio handlers.

### MIDI_SONG (0x00191990)

**Packet format:** 10 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 4 | uint | songId | MIDI song resource ID |
| 4 | 1 | byte | volume | Volume level |
| 5 | 2 | ushort | fadeIn | Fade-in duration |
| 7 | 1 | byte | loop | Loop control |
| 8 | 2 | ushort | delay | Start delay |

**Behavior:** Starts MIDI music playback. Used for background music in different game areas.

### MIDI_JINGLE (0x00191af0)

**Packet format:** 10 bytes (same structure as MIDI_SONG)
**Behavior:** Plays a short MIDI jingle (quest complete fanfare, level up sound, etc.). Same packet structure as MIDI_SONG but treated as a one-shot priority sound.

### MIDI_STOP (0x00191c50)

**Packet format:** 0 bytes
**Behavior:** Stops all MIDI playback via `FUN_00cb4110(soundMgr, 1)`. The parameter `1` indicates a graceful stop.

### MIDI_SWAP (0x001e4180)

**Packet format:** 5 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 1 | byte | volume | Target volume for new song |
| 1 | 4 | int | songId | New song ID (g4s_alt3 encoded) |

**Behavior:** Cross-fades between the currently playing MIDI song and a new one. If songId is negative, stops current song. Otherwise, claims a new sound instance, sets cross-fade parameters (duration at +0x94 = 25 ticks, volume curve at +0x34..+0x44), and stores the new song reference at SoundManager+0x698/+0x6a8.

### SOUND_GROUP (0x00191c90)

**Packet format:** 11+ bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 4 | uint | soundId | Sound resource ID |
| 4 | 1 | byte | volume | Volume level |
| 5 | 2 | ushort | x | X coordinate |
| 7 | 2 | ushort | y | Y coordinate |
| 9 | 1 | byte | boneId | Bone attachment ID |
| 10 | 1 | byte | type | Sound shape type |

**Behavior:** Creates a 3D positioned sound via `jag::game::SoundManager::ClaimVorbis` (FUN_00ce83b0). The sound is spatialized at the given world coordinates.

### SOUND_STOP (0x00191f10)

**Packet format:** 2 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 2 | ushort | soundId | Sound instance ID to stop |

**Behavior:** Iterates all active sound instances searching by ID. When found, marks the instance for fade-out and stop. Uses an unrolled loop (8x unroll factor) for performance.

### SOUND_STOP_ALL (0x00192870)

**Packet format:** 0 bytes
**Behavior:** Stops all sounds on audio buss 8 via `jag::game::SoundManager::StopAllSoundsOnBuss(soundMgr, 8)` (FUN_007c9b40).

### SOUND_GROUP_SPEED (0x00192470)

**Packet format:** 6 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 4 | uint | soundId | Sound group ID (big-endian swapped) |
| 4 | 2 | ushort | speed | Playback speed parameter |

**Behavior:** Adjusts the playback speed of an active sound group.

### SOUND_GROUP_STOP (0x0018d450)

**Packet format:** 2 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 2 | ushort | groupId | Sound group ID to stop |

**Behavior:** Iterates all active sound instances. For each instance matching the group ID at +0x9c, clears the active flag (+0x98), checks state (+0x94 == 0 && +0x14 < 4), and initiates graceful shutdown. Uses an unrolled loop (4x unroll factor).

### SOUND_MODIFY (0x0018d0e0)

**Packet format:** 4 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 2 | ushort | soundId | Sound instance ID |
| 2 | 2 | ushort | speed | Speed/volume modifier |

**Behavior:** Searches active sound instances by ID at +0x34 using an 8-way unrolled loop. When found, applies a normalized float speed/volume value at +0x1c (clamped to [0.0, max]).

### SOUND_AREA (0x001c1d20)

**Packet format:** 4 bytes (gT_uint encoded)
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 4 | uint | soundId | Area sound ID (transform-encoded) |

**Behavior:** Triggers an area sound via a SoundManager vtable function at +0x670/+0x680. Uses an indirect call through the SoundManager's vtable at offset +0x38.

### SOUND_AREA_SYNTH (0x0018da50)

**Packet format:** 8 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 4 | uint | soundId | Sound resource ID (big-endian swapped) |
| 4 | 1 | byte | param1 | First parameter |
| 5 | 1 | byte | param2 | Second parameter |
| 6 | 2 | ushort | param3 | Third parameter (big-endian swapped) |

**Behavior:** Creates an area synth sound via FUN_00bc1410. Uses a different creation path than SOUND_GROUP.

### SOUND_MIXBUSS_SETLEVEL (0x001917e0)

**Packet format:** 5 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 1 | byte | level | Volume level (negated before use) |
| 1 | 4 | uint | bussId | Mix buss ID (big-endian swapped) |

**Behavior:** Sets the volume level of a specific mix buss via `FUN_007c9950(soundMgr, bussId, -level)`.

### VORBIS_SONG (0x00192360)

**Packet format:** 6 bytes
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 2 | ushort | songId | Vorbis song resource ID |
| 2 | 2 | ushort | loopCount | Number of loops (0 = infinite) |
| 4 | 2 | ushort | duration | Playback duration |

**Behavior:** Starts Vorbis (OGG) encoded music playback. Used for higher-quality streamed audio tracks.

### VORBIS_PRELOAD (0x001e4110)

**Packet format:** 4 bytes (g4s_alt3 encoded)
| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0 | 4 | int | resourceId | Vorbis resource to preload |

**Behavior:** Preloads a Vorbis audio resource into memory. Only acts if streaming audio is enabled (SoundManager+0x718 == 1). Calls FUN_007ca020 to initiate the preload, then triggers a vtable callback.

## Key Subsystem Functions

| Address | Name | Description |
|---------|------|-------------|
| `0x00ce83b0` | `jag::game::SoundManager::ClaimVorbis` | Core sound creation function |
| `0x00cb4110` | `jag::game::SoundManager::StopAllSoundsOnBuss` | Stop sounds on specific buss |
| `0x007c9950` | SoundManager::SetMixBussLevel | Set mix buss volume |
| `0x007c9b40` | SoundManager::StopSoundsOnBuss | Stop sounds on buss by type |
| `0x00bc1410` | SoundManager::CreateAreaSynth | Create area synth sound |
| `0x007ca020` | SoundManager::PreloadResource | Preload audio resource |

## Client Subsystem References

- `__DT_SYMTAB[0x4a2]` - SoundManager object
- SoundManager+0x698 - Current MIDI song ID
- SoundManager+0x6a8 - Current MIDI song instance pointer
- SoundManager+0x718 - Streaming audio enabled flag
- SoundManager+0x670 - Area sound function pointer
- Return values: `DAT_016fb240` = success, `DAT_016fb220` = yield
