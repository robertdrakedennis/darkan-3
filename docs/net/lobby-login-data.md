# Lobby Login Data Format (rev 946)

**Source:** `jag::LoginManager::LoginStepHandleLoginData` in rs2client (Ghidra RE)
**Sent:** Server → Client after login response code 2 (SUCCESS)
**Wire format:** `[1B response_code=0x02] [1B data_length] [NB login_data]`

## Field Layout

| # | Offset | Read | Bytes | Field | Sample | Notes |
|---|--------|------|-------|-------|--------|-------|
| 1 | 0 | g1 | 1 | hasTotpUpdate | 0 | If 1: ISAAC re-seed (4 cipher bytes + g4 TOTP code) follows |
| 2 | 1 | g1 | 1 | membershipType | 0 | LoggedInPlayer+0x08 |
| 3 | 2 | g1 | 1 | membershipDays | 0 | LoggedInPlayer+0x0C |
| 4 | 3 | g1 | 1 | emailValidated | 0 | Bool (`== 1`); +0x10 |
| 5 | 4-6 | g3s | 3 | recoveryDelay | -8388606 | Signed medium: if > 0x7FFFFF then subtract 0x1000000 |
| 6 | 7 | g1s | 1 | staffModLevel | 1 | Signed byte cast to int; +0x88 |
| 7 | 8 | g1 | 1 | unknownFlag1 | 0 | Bool; +0x19 |
| 8 | 9 | g1 | 1 | unknownFlag2 | 1 | Bool; +0x1A |
| 9 | 10-17 | g8 | 8 | membershipTimestamp | 1631993231463 | Unix millis; +0x30 |
| 10 | 18 | g1 | 1 | timeDaysByte | 223 | Used in: `(membershipTs - now) - timeMillisInt - (timeDaysByte << 32)` |
| 11 | 19-22 | g4 | 4 | timeMillisInt | 128664087 | See above |
| 12 | 23 | g1 | 1 | flagsByte | 0x00 | bit0 = quickChatOnly (+0x28), bit1 = unknown (+0x29) |
| 13 | 24-27 | g4 | 4 | lastLoginIP | 0 | +0x40 |
| 14 | 28-31 | g4 | 4 | lastLoginDays | 5000 | +0x44 |
| 15 | 32-33 | g2 | 2 | playerIndex | 0 | +0x1C |
| 16 | 34-35 | g2 | 2 | unknown3 | 0 | +0x20 |
| 17 | 36-37 | g2 | 2 | unknown4 | 8573 | +0x60 |
| 18 | 38-41 | g4 | 4 | unknown5 | 0x327B4E0E | +0x64 |
| 19 | 42 | g1 | 1 | unknown6 | 3 | +0x24 |
| 20 | 43-44 | g2 | 2 | unknown7 | 53791 | +0x38 |
| 21 | 45-46 | g2 | 2 | unknown8 | 53791 | +0x3C |
| 22 | 47 | g1 | 1 | isMembersWorld | 0 | Bool; +0x18 |
| 23 | 48-56 | gjStr | 9 | displayName | "Mememom" | Versioned: [1B ver=0x00][string][NUL] |
| 24 | 57 | g1 | 1 | unknown9 | 2 | +0x84 |
| 25 | 58-61 | g4 | 4 | unknown10 | 0x00090485 | +0x80 |
| 26 | 62-63 | g2 | 2 | worldId | 3 | 0xFFFF = -1 |
| 27 | 64-85 | gjStr | 22 | serverHostname | "world3.runescape.com" | Versioned string |
| 28 | 86-87 | g2 | 2 | gamePort | 43594 | TCP game port |
| 29 | 88-89 | g2 | 2 | httpsPort | 443 | HTTPS port |
| 30 | 90-97 | g8 | 8 | sessionToken1 | 0x0000000013154028 | LoginManager session state |
| 31 | 98-105 | g8 | 8 | sessionToken2 | 0x06F29A580E4004A4 | LoginManager session state |

**Total: 106 bytes** (sample data from live Jagex lobby login, March 2026)

## gjStr (Versioned String)

```
version = g1()
if version != 0: return ""  // invalid version → empty string
return gStr()               // null-terminated CP1252 string
```

## Notes

- The `hasTotpUpdate` field at offset 0 gates an ISAAC re-seed path. When 1, the client reads 4 ISAAC cipher values then a g4 TOTP code before proceeding.
- `recoveryDelay` uses signed 24-bit (medium) encoding. Value -8388606 likely means "recovery not set".
- `lastLoginDays` = 5000 appears to mean "never logged in before" or "very long ago".
- `unknown5` (0x327B4E0E) at +0x64 may be a CRC or hash.
- `unknown7` and `unknown8` are identical (53791 = 0xD21F) in sample — possibly related build/version numbers.
