# World login server-client-var block 2026-06-24

Capture: `/Users/robert/.undercut/recordings/session-20260624-191139-prod-transition-20260624-191126`

Decoded transcript: `/Users/robert/projects/darkan3-server/build/undercut-socket-session-prod-transition-20260624-191139-varcs.jsonl`

This page documents the raw pre-ISAAC server-client-var block sent by production immediately after world login result `2`.
It is part A of the world-login response, before the players byte and world-login-data block.

## Wire shape

| Field | Value | Evidence |
|---|---:|---|
| login result | `2` | decoded socket login event |
| block length | `1321` | decoded socket login event |
| ack flag | `1` | first byte of block body |
| entries | `220` | deframer typed entry decode |
| entry type mix | `220 int`, `0 long`, `0 string` | cache-backed deframer type lookup |
| entry body bytes | `1320` | `220 * (u16 id + s32 value)` |
| decode errors | `0` | no `server_client_var_decode_error` rows |

The total block body is `1 + 220 * 6 = 1321` bytes.

## 948 client parser

Ghidra functions in the 948 client:

| Function | Address | Finding |
|---|---:|---|
| `LoginStepWaitingServerClientVarLength` | `00180d00` | State `0xfa` reads a big-endian 2-byte block length into `loginMgr+0x1c8`, then advances to `0x104`. |
| `LoginStepWaitingServerClientVarConfigData` | `00180bf0` | State `0x104` waits for exactly that many bytes, copies the block, then advances to `0x10e`. |
| `LoginStepWaitingServerClientVar` | `0018f280` | State `0x10e` reads byte 0 as ack, then loops from offset 1. Each entry begins with big-endian u16 varc id, then dispatches type-specific value decode through the client config type. If ack is `1`, the login state advances to players-byte state `0x82`; otherwise it loops back to length state `0xfa`. |

Important parser detail: entry values are not self-describing on the wire. The client looks up the varc id in its cache config and then decodes the typed value. A wrong id or wrong value type can break the login parser even if the length is correct.

## Entry table

| # | Varc id | Type | Value |
|---:|---:|---|---:|
| 0 | 4109 | int | 536937475 |
| 1 | 4110 | int | 134678021 |
| 2 | 4111 | int | -2088401878 |
| 3 | 4112 | int | 35464 |
| 4 | 5139 | int | -2146664148 |
| 5 | 5140 | int | 2048 |
| 6 | 4116 | int | 1 |
| 7 | 4120 | int | 620756991 |
| 8 | 5155 | int | 82560 |
| 9 | 4646 | int | -2129022716 |
| 10 | 4647 | int | 16777216 |
| 11 | 5160 | int | 1597647 |
| 12 | 5161 | int | 16777215 |
| 13 | 4154 | int | 377032 |
| 14 | 4155 | int | 10186752 |
| 15 | 5192 | int | -2147176332 |
| 16 | 5193 | int | 2048 |
| 17 | 8267 | int | 2992 |
| 18 | 4684 | int | -2147360683 |
| 19 | 8268 | int | 1812 |
| 20 | 4685 | int | 4853759 |
| 21 | 4705 | int | -2147237488 |
| 22 | 4706 | int | 2048 |
| 23 | 4723 | int | 1597647 |
| 24 | 4724 | int | 16777215 |
| 25 | 6277 | int | 1474767 |
| 26 | 3721 | int | 100992003 |
| 27 | 3722 | int | -13304057 |
| 28 | 3723 | int | 4198400 |
| 29 | 6296 | int | 1344405503 |
| 30 | 4764 | int | -2146090718 |
| 31 | 4765 | int | 8392703 |
| 32 | 4254 | int | -2147352464 |
| 33 | 6302 | int | -1 |
| 34 | 4255 | int | 1075 |
| 35 | 6304 | int | -2146041344 |
| 36 | 6305 | int | 8390656 |
| 37 | 6323 | int | -2146336488 |
| 38 | 6324 | int | 200 |
| 39 | 3769 | int | 385875968 |
| 40 | 3770 | int | 436207616 |
| 41 | 4794 | int | 369098519 |
| 42 | 4798 | int | -2146377458 |
| 43 | 4813 | int | -2146897680 |
| 44 | 4814 | int | 1805193 |
| 45 | 5840 | int | 1597647 |
| 46 | 5841 | int | 16777215 |
| 47 | 3295 | int | -1610612736 |
| 48 | 4322 | int | 1597647 |
| 49 | 4323 | int | 16777215 |
| 50 | 4324 | int | 1597647 |
| 51 | 4325 | int | 16777215 |
| 52 | 6417 | int | -2147098374 |
| 53 | 6418 | int | 13493424 |
| 54 | 2852 | int | 319951120 |
| 55 | 2853 | int | 387323156 |
| 56 | 2854 | int | 454695192 |
| 57 | 6439 | int | -2146254592 |
| 58 | 2855 | int | 572588031 |
| 59 | 2856 | int | 319951120 |
| 60 | 2857 | int | -236 |
| 61 | 2858 | int | 385833471 |
| 62 | 2859 | int | -268435456 |
| 63 | 2860 | int | -1010826241 |
| 64 | 2862 | int | -1 |
| 65 | 2863 | int | 704643071 |
| 66 | 2864 | int | 642008373 |
| 67 | 2865 | int | -13162457 |
| 68 | 2866 | int | -1 |
| 69 | 2867 | int | 589579832 |
| 70 | 2868 | int | 842019105 |
| 71 | 6964 | int | -1 |
| 72 | 2869 | int | 33646952 |
| 73 | 6457 | int | -2145025248 |
| 74 | 6458 | int | 8390656 |
| 75 | 5947 | int | -2147098374 |
| 76 | 5948 | int | 16775168 |
| 77 | 4939 | int | -2146135748 |
| 78 | 4954 | int | -2130378572 |
| 79 | 4955 | int | 16780124 |
| 80 | 2912 | int | 32 |
| 81 | 2913 | int | -2130706433 |
| 82 | 2914 | int | 8390656 |
| 83 | 2915 | int | -2147155691 |
| 84 | 2916 | int | 16777215 |
| 85 | 2917 | int | -2146635569 |
| 86 | 2918 | int | 4095 |
| 87 | 2919 | int | -2130394606 |
| 88 | 2920 | int | 29200384 |
| 89 | 2921 | int | -2146520558 |
| 90 | 2922 | int | 352317440 |
| 91 | 2923 | int | -2145849067 |
| 92 | 2924 | int | 79429631 |
| 93 | 2925 | int | -2144451968 |
| 94 | 2926 | int | 2048 |
| 95 | 2927 | int | 318767104 |
| 96 | 2928 | int | 352321536 |
| 97 | 2929 | int | 335544320 |
| 98 | 2930 | int | 369098752 |
| 99 | 2931 | int | 352321536 |
| 100 | 2932 | int | 385875968 |
| 101 | 6005 | int | 1474767 |
| 102 | 2933 | int | 51806415 |
| 103 | 2934 | int | 27865087 |
| 104 | 6006 | int | 704643072 |
| 105 | 2935 | int | -2146885110 |
| 106 | 2936 | int | 15155700 |
| 107 | 2937 | int | -2146041344 |
| 108 | 2938 | int | 8390656 |
| 109 | 2939 | int | 1597647 |
| 110 | 2940 | int | 16777215 |
| 111 | 2941 | int | 84623567 |
| 112 | 2942 | int | 8392703 |
| 113 | 2943 | int | 287005 |
| 114 | 2944 | int | 8388608 |
| 115 | 2945 | int | 737487 |
| 116 | 2946 | int | 142610431 |
| 117 | 2947 | int | 118177999 |
| 118 | 2948 | int | 109055999 |
| 119 | 2949 | int | 134955215 |
| 120 | 4997 | int | -1 |
| 121 | 2950 | int | 159387647 |
| 122 | 4998 | int | -1 |
| 123 | 2951 | int | 101400783 |
| 124 | 4999 | int | -1 |
| 125 | 2952 | int | 92278783 |
| 126 | 5000 | int | -1 |
| 127 | 5001 | int | -1 |
| 128 | 3465 | int | 8388608 |
| 129 | 5002 | int | -1 |
| 130 | 2955 | int | 1102022 |
| 131 | 5003 | int | -1 |
| 132 | 2956 | int | 16777215 |
| 133 | 5004 | int | -1 |
| 134 | 2957 | int | 436207616 |
| 135 | 5005 | int | -1 |
| 136 | 5006 | int | -1 |
| 137 | 2959 | int | 68743445 |
| 138 | 5007 | int | -1 |
| 139 | 2960 | int | 263979007 |
| 140 | 5008 | int | -1 |
| 141 | 2961 | int | 151732431 |
| 142 | 5009 | int | -1 |
| 143 | 2962 | int | 226496511 |
| 144 | 5010 | int | -1 |
| 145 | 2963 | int | 369098752 |
| 146 | 2964 | int | 402653184 |
| 147 | 2965 | int | 1597647 |
| 148 | 5014 | int | -2130550254 |
| 149 | 2966 | int | 16777215 |
| 150 | 5015 | int | 26345472 |
| 151 | 2967 | int | 1597647 |
| 152 | 5016 | int | -2130550254 |
| 153 | 2968 | int | 16777215 |
| 154 | 5017 | int | 25513984 |
| 155 | 2969 | int | 18411797 |
| 156 | 5018 | int | -2112843700 |
| 157 | 2970 | int | 12320767 |
| 158 | 5019 | int | 46607404 |
| 159 | 2971 | int | 1597647 |
| 160 | 5020 | int | -2112843700 |
| 161 | 2972 | int | 16777215 |
| 162 | 5021 | int | 46607100 |
| 163 | 2973 | int | -2146868998 |
| 164 | 6046 | int | -2147110729 |
| 165 | 2974 | int | 12953600 |
| 166 | 4510 | int | -51969 |
| 167 | 6047 | int | 4196352 |
| 168 | 4511 | int | 402653184 |
| 169 | 2975 | int | -2147348352 |
| 170 | 4512 | int | 167772160 |
| 171 | 2976 | int | 5607423 |
| 172 | 4513 | int | -2146827988 |
| 173 | 4514 | int | 8390656 |
| 174 | 4515 | int | 1597647 |
| 175 | 2979 | int | -2146664148 |
| 176 | 4516 | int | 16777215 |
| 177 | 2980 | int | 4196352 |
| 178 | 2981 | int | -2146303776 |
| 179 | 2982 | int | 8392703 |
| 180 | 2983 | int | -2147348326 |
| 181 | 2984 | int | 3073 |
| 182 | 2985 | int | 1597647 |
| 183 | 2986 | int | 16777215 |
| 184 | 2987 | int | 307380 |
| 185 | 2988 | int | 1805193 |
| 186 | 2989 | int | -2147299198 |
| 187 | 2990 | int | 1340 |
| 188 | 2991 | int | -2144820534 |
| 189 | 2992 | int | 8390656 |
| 190 | 2993 | int | -2146807458 |
| 191 | 2994 | int | 4196352 |
| 192 | 2995 | int | 2360096 |
| 193 | 2996 | int | 8390656 |
| 194 | 6102 | int | -1 |
| 195 | 5079 | int | -1 |
| 196 | 6103 | int | -1 |
| 197 | 5080 | int | 268435456 |
| 198 | 5081 | int | 1597647 |
| 199 | 5082 | int | 16777215 |
| 200 | 6108 | int | 1597647 |
| 201 | 6620 | int | -1 |
| 202 | 6621 | int | -2147246022 |
| 203 | 6109 | int | 16777215 |
| 204 | 6622 | int | 13276630 |
| 205 | 6110 | int | 1597647 |
| 206 | 6111 | int | 16777215 |
| 207 | 6112 | int | 1597647 |
| 208 | 6113 | int | 16777215 |
| 209 | 6114 | int | 1597647 |
| 210 | 6115 | int | 16777215 |
| 211 | 6116 | int | 1597647 |
| 212 | 6117 | int | 16777215 |
| 213 | 6118 | int | 1597647 |
| 214 | 6119 | int | 16777215 |
| 215 | 8181 | int | 16777215 |
| 216 | 7159 | int | 255 |
| 217 | 8184 | int | 963090 |
| 218 | 7160 | int | -1 |
| 219 | 8185 | int | 335540224 |

## Adversary review

- This is not an ISAAC-framed `ServerProt`. It is raw login response data before outbound ISAAC packet framing starts.
- Length correctness is not enough. The client resolves each varc id through cache config and then consumes the type-specific value; unknown ids or wrong value widths can corrupt the cursor or crash.
- The block is a production observation, not a proof that every value is required for first light. Treat it as the current high-confidence baseline until controlled removal experiments or Ghidra-backed CS2/UI analysis prove a smaller subset.
- The local server should build this through typed entries, not opaque byte replay, so future work can name and own each var.
- A one-byte ack-only block is parser-valid, but it skips 220 production config values and no longer matches the latest successful production transition.
