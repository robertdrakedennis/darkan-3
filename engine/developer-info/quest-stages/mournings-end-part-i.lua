local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local oronwen = Model.new(5985, {
  [109] = Vertex.new(-27, 717, -1, 127, 127, 127),
  [121] = Vertex.new(27, 717, -1, 127, 127, 127),
  [1125] = Vertex.new(-30, 695, -24, 127, 127, 127),
  [1161] = Vertex.new(30, 695, -24, 127, 127, 127),
  [3607] = Vertex.new(0, 735, -7, 29, 142, 129),
})
local gnome = Model.new(3621, {
  [90] = Vertex.new(-47, 483, 19, 96, 84, 50),
  [170] = Vertex.new(47, 483, 19, 96, 84, 50),
  [3346] = Vertex.new(0, 440, -10, 15, 167, 42),
  [3347] = Vertex.new(2, 440, -12, 15, 167, 42),
  [3348] = Vertex.new(-2, 440, -12, 15, 167, 42),
})
local freshSickLookingSheep1 = Model.new(9216, {
  [3250] = Vertex.new(-38, 410, -148, 127, 127, 127),
  [5555] = Vertex.new(-31, 413, -175, 128, 128, 127),
  [5557] = Vertex.new(-31, 413, -175, 128, 128, 127),
  [5930] = Vertex.new(31, 413, -175, 128, 128, 127),
  [6103] = Vertex.new(38, 410, -148, 127, 127, 127),
})
local freshSickLookingSheep2 = Model.new(9216, {
  [1006] = Vertex.new(38, 410, -148, 127, 127, 127),
  [3332] = Vertex.new(-31, 413, -175, 128, 128, 127),
  [3334] = Vertex.new(-31, 413, -175, 128, 128, 127),
  [3497] = Vertex.new(31, 413, -175, 128, 128, 127),
  [6103] = Vertex.new(-38, 410, -148, 127, 127, 127),
})
local freshSickLookingSheep4 = Model.new(9216, {
  [1717] = Vertex.new(37, 409, -147, 127, 127, 127),
  [4063] = Vertex.new(-37, 409, -147, 127, 127, 127),
  [4124] = Vertex.new(30, 412, -174, 128, 127, 127),
  [4130] = Vertex.new(30, 412, -174, 128, 127, 127),
  [4162] = Vertex.new(-30, 412, -174, 128, 127, 127),
})
local freshSickLookingSheep3 = Model.new(9216, {
  [2986] = Vertex.new(-38, 410, -148, 127, 127, 127),
  [3500] = Vertex.new(-31, 413, -175, 128, 128, 127),
  [3502] = Vertex.new(-31, 413, -175, 128, 128, 127),
  [3923] = Vertex.new(31, 413, -175, 128, 128, 127),
  [4198] = Vertex.new(38, 410, -148, 127, 127, 127),
})
--#endregion
--#region Objects
local gnomeOnARack = Model.new(4563, {
  [157] = Vertex.new(520, 416, -192, 97, 89, 88),
  [159] = Vertex.new(508, 416, -184, 97, 89, 88),
  [953] = Vertex.new(-628, 312, -188, 59, 49, 30),
  [963] = Vertex.new(600, 460, -168, 59, 49, 30),
  [975] = Vertex.new(600, 460, 192, 59, 49, 30),
})
--#endregion
--#region Items
local bearFur = Model.new(120, {
  [2] = Vertex.new(108, 0, 140, 108, 86, 10),
  [31] = Vertex.new(-80, 0, 128, 108, 86, 10),
  [75] = Vertex.new(108, 0, -116, 108, 86, 10),
  [77] = Vertex.new(108, 0, -116, 108, 86, 10),
  [120] = Vertex.new(-112, 0, -88, 108, 86, 10),
})
local toadCrunchies = Model.new(243, {
  [214] = Vertex.new(72, 16, 44, 9, 96, 11),
  [217] = Vertex.new(52, 16, 76, 9, 96, 11),
  [218] = Vertex.new(68, 16, 60, 9, 96, 11),
  [219] = Vertex.new(52, 16, 60, 9, 96, 11),
  [224] = Vertex.new(40, 16, 72, 9, 96, 11),
})
--#endregion
--#region Quest Items
local rottenApple = Model.new(186, {
  [2] = Vertex.new(0, 80, -52, 100, 96, 52),
  [3] = Vertex.new(-8, 92, -28, 100, 96, 52),
  [4] = Vertex.new(-8, 92, -28, 100, 96, 52),
  [11] = Vertex.new(44, 80, 28, 100, 96, 52),
  [17] = Vertex.new(-48, 80, 16, 100, 96, 52),
})
local mournerLetter = Model.new(372, {
  [3] = Vertex.new(20, 0, -72, 149, 149, 137),
  [5] = Vertex.new(-72, 0, -56, 149, 149, 137),
  [187] = Vertex.new(-84, 28, -84, 92, 66, 8),
  [190] = Vertex.new(-84, 28, -84, 92, 66, 8),
  [195] = Vertex.new(-84, 28, -84, 92, 66, 8),
})
local gasMask = Model.new(1362, {
  [1026] = Vertex.new(-45, 22, 76, 111, 107, 71),
  [1076] = Vertex.new(45, 22, 76, 111, 107, 71),
  [1131] = Vertex.new(-45, 22, 76, 49, 47, 31),
  [1208] = Vertex.new(45, 22, 76, 49, 47, 31),
  [1303] = Vertex.new(-45, 22, 76, 23, 22, 14),
})
local mournerCape = Model.new(492, {
  [306] = Vertex.new(-103, -1, -233, 59, 38, 5),
  [332] = Vertex.new(105, -1, -277, 14, 15, 23),
  [373] = Vertex.new(-113, -1, -277, 14, 15, 23),
  [376] = Vertex.new(-113, -1, -277, 14, 15, 23),
  [377] = Vertex.new(-103, -1, -233, 14, 15, 23),
})
local bloodyMournerTop = Model.multi({
  Model.new(456, {
    [361] = Vertex.new(-143, 0, -145, 63, 61, 58),
    [364] = Vertex.new(-143, 0, -145, 63, 61, 58),
    [367] = Vertex.new(143, 0, -145, 63, 61, 58),
    [379] = Vertex.new(-119, 0, -145, 9, 9, 8),
    [385] = Vertex.new(119, 0, -145, 9, 9, 8),
  }),
  Model.new(72, {
    [35] = Vertex.new(-19, 34, -67, 29, 7, 6),
    [39] = Vertex.new(19, 33, -85, 29, 7, 6),
    [47] = Vertex.new(-19, 32, -101, 29, 7, 6),
    [63] = Vertex.new(19, 25, 85, 29, 7, 6),
    [71] = Vertex.new(-19, 32, 74, 29, 7, 6),
  }),
})
local tornMournerTrousers = Model.new(666, {
  [54] = Vertex.new(-93, 0, 143, 57, 41, 36),
  [357] = Vertex.new(-61, 0, -196, 36, 35, 33),
  [359] = Vertex.new(61, 0, -196, 36, 35, 33),
  [370] = Vertex.new(-61, 0, -196, 36, 35, 33),
  [394] = Vertex.new(61, 0, -196, 36, 35, 33),
})
local mournerGloves = Model.new(615, {
  [4] = Vertex.new(-58, 27, -46, 20, 19, 18),
  [47] = Vertex.new(-17, 27, 68, 24, 23, 22),
  [77] = Vertex.new(-58, 27, -46, 24, 23, 22),
  [81] = Vertex.new(-58, 27, -46, 24, 23, 22),
  [84] = Vertex.new(-58, 27, -46, 24, 23, 22),
})
local mournerBoots = Model.new(420, {
  [3] = Vertex.new(-29, 67, 49, 15, 14, 14),
  [344] = Vertex.new(-29, 67, 49, 15, 14, 14),
  [353] = Vertex.new(-29, 67, 49, 24, 23, 22),
  [389] = Vertex.new(85, 16, -64, 28, 27, 26),
  [393] = Vertex.new(85, 16, -64, 28, 27, 26),
})
local soap = Model.new(180, {
  [3] = Vertex.new(-52, 40, 24, 147, 151, 138),
  [5] = Vertex.new(48, 40, -28, 147, 151, 138),
  [9] = Vertex.new(-52, 40, 24, 147, 151, 138),
  [12] = Vertex.new(-52, 40, 24, 147, 151, 138),
  [14] = Vertex.new(-52, 40, 24, 147, 151, 138),
})
local cleanMournerTop = Model.multi({
  Model.new(444, {
    [349] = Vertex.new(-143, 0, -145, 63, 61, 58),
    [352] = Vertex.new(-143, 0, -145, 63, 61, 58),
    [355] = Vertex.new(143, 0, -145, 63, 61, 58),
    [367] = Vertex.new(-119, 0, -145, 9, 9, 8),
    [373] = Vertex.new(119, 0, -145, 9, 9, 8),
  }),
  Model.new(72, {
    [35] = Vertex.new(-19, 34, -67, 29, 7, 6),
    [39] = Vertex.new(19, 33, -85, 29, 7, 6),
    [47] = Vertex.new(-19, 32, -101, 29, 7, 6),
    [63] = Vertex.new(19, 25, 85, 29, 7, 6),
    [71] = Vertex.new(-19, 32, 74, 29, 7, 6),
  }),
})
local mendedMournerTrousers = Model.new(666, {
  [357] = Vertex.new(-61, 0, -196, 36, 35, 33),
  [359] = Vertex.new(61, 0, -196, 36, 35, 33),
  [370] = Vertex.new(-61, 0, -196, 36, 35, 33),
  [394] = Vertex.new(61, 0, -196, 36, 35, 33),
  [421] = Vertex.new(-30, 0, -196, 20, 19, 18),
})
local fixedDevice = Model.new(597, {
  [65] = Vertex.new(-92, 84, -64, 98, 84, 8),
  [69] = Vertex.new(100, 96, -44, 98, 84, 8),
  [71] = Vertex.new(88, 100, -64, 98, 84, 8),
  [297] = Vertex.new(88, 100, 24, 98, 84, 8),
  [323] = Vertex.new(96, 100, -140, 98, 84, 8),
})
local blueToad = Model.new(477, {
  [13] = Vertex.new(-64, 60, -56, 51, 69, 80),
  [14] = Vertex.new(-64, 12, -80, 51, 69, 80),
  [15] = Vertex.new(-80, 12, -68, 51, 69, 80),
  [16] = Vertex.new(56, 60, -56, 51, 69, 80),
  [17] = Vertex.new(96, 88, -8, 51, 69, 80),
  [18] = Vertex.new(80, 0, -8, 51, 69, 80),
  [19] = Vertex.new(4, 16, -140, 51, 69, 80),
  [20] = Vertex.new(-12, 16, -140, 51, 69, 80),
  [21] = Vertex.new(-4, 24, -120, 51, 69, 80),
  [22] = Vertex.new(44, 4, -108, 51, 69, 80),
})
local redToad = Model.new(477, {
  [13] = Vertex.new(-64, 60, -56, 103, 52, 31),
  [14] = Vertex.new(-64, 12, -80, 103, 52, 31),
  [15] = Vertex.new(-80, 12, -68, 103, 52, 31),
  [16] = Vertex.new(56, 60, -56, 103, 52, 31),
  [17] = Vertex.new(96, 88, -8, 103, 52, 31),
  [18] = Vertex.new(80, 0, -8, 103, 52, 31),
  [19] = Vertex.new(4, 16, -140, 103, 52, 31),
  [20] = Vertex.new(-12, 16, -140, 103, 52, 31),
  [21] = Vertex.new(-4, 24, -120, 103, 52, 31),
  [22] = Vertex.new(44, 4, -108, 103, 52, 31),
})
local yellowToad = Model.new(477, {
  [13] = Vertex.new(-64, 60, -56, 126, 110, 25),
  [14] = Vertex.new(-64, 12, -80, 126, 110, 25),
  [15] = Vertex.new(-80, 12, -68, 126, 110, 25),
  [16] = Vertex.new(56, 60, -56, 126, 110, 25),
  [17] = Vertex.new(96, 88, -8, 126, 110, 25),
  [18] = Vertex.new(80, 0, -8, 126, 110, 25),
  [19] = Vertex.new(4, 16, -140, 126, 110, 25),
  [20] = Vertex.new(-12, 16, -140, 126, 110, 25),
  [21] = Vertex.new(-4, 24, -120, 126, 110, 25),
  [22] = Vertex.new(44, 4, -108, 126, 110, 25),
})
local greenToad = Model.new(477, {
  [13] = Vertex.new(-64, 60, -56, 19, 213, 24),
  [14] = Vertex.new(-64, 12, -80, 19, 213, 24),
  [15] = Vertex.new(-80, 12, -68, 19, 213, 24),
  [16] = Vertex.new(56, 60, -56, 19, 213, 24),
  [17] = Vertex.new(96, 88, -8, 19, 213, 24),
  [18] = Vertex.new(80, 0, -8, 19, 213, 24),
  [19] = Vertex.new(4, 16, -140, 19, 213, 24),
  [20] = Vertex.new(-12, 16, -140, 19, 213, 24),
  [21] = Vertex.new(-4, 24, -120, 19, 213, 24),
  [22] = Vertex.new(44, 4, -108, 19, 213, 24),
  [24] = Vertex.new(20, 24, -128, 19, 213, 24),
  [25] = Vertex.new(4, 28, -116, 19, 213, 24),
  [26] = Vertex.new(40, 28, -80, 19, 213, 24),
  [46] = Vertex.new(96, 4, -56, 19, 213, 24),
  [47] = Vertex.new(72, 0, -68, 19, 213, 24),
  [48] = Vertex.new(96, 0, -56, 19, 213, 24),
  [50] = Vertex.new(72, 12, -68, 19, 213, 24),
  [52] = Vertex.new(96, 0, -88, 19, 213, 24),
  [53] = Vertex.new(72, 12, -80, 19, 213, 24),
  [54] = Vertex.new(96, 4, -88, 19, 213, 24),
})
local barrel = Model.new(354, {
  [1] = Vertex.new(-76, 288, 80, 103, 81, 37),
  [3] = Vertex.new(-64, 288, 72, 103, 81, 37),
  [4] = Vertex.new(-76, 288, 80, 103, 81, 37),
  [11] = Vertex.new(-76, 288, 80, 103, 81, 37),
  [45] = Vertex.new(76, 288, 80, 103, 81, 37),
})
local barrelOfRottenApples = Model.new(726, {
  [11] = Vertex.new(40, 330, 64, 98, 94, 51),
  [17] = Vertex.new(-44, 330, 56, 98, 94, 51),
  [20] = Vertex.new(40, 310, -60, 98, 94, 51),
  [38] = Vertex.new(-28, 318, -68, 98, 94, 51),
  [53] = Vertex.new(-60, 318, -24, 98, 94, 51),
})
local barrelOfCrushedRottenApples = Model.new(372, {
  [5] = Vertex.new(-96, 160, -80, 81, 46, 24),
  [8] = Vertex.new(0, 160, -112, 81, 46, 24),
  [9] = Vertex.new(-96, 160, -80, 81, 46, 24),
  [11] = Vertex.new(96, 160, -80, 81, 46, 24),
  [15] = Vertex.new(96, 160, -80, 81, 46, 24),
})
local barrelOfTar = Model.new(414, {
  [237] = Vertex.new(76, 288, 60, 22, 22, 28),
  [282] = Vertex.new(80, 316, 32, 22, 22, 28),
  [287] = Vertex.new(80, 316, 32, 22, 22, 28),
  [289] = Vertex.new(80, 316, 32, 22, 22, 28),
  [292] = Vertex.new(80, 316, 32, 22, 22, 28),
})
local barrelOfNaphtha = Model.multi({
  Model.new(354, {
    [122] = Vertex.new(96, 128, 100, 84, 64, 25),
    [147] = Vertex.new(-96, 128, 100, 84, 64, 25),
    [163] = Vertex.new(-76, 288, 80, 84, 64, 25),
    [164] = Vertex.new(0, 288, 108, 84, 64, 25),
    [166] = Vertex.new(-76, 288, 80, 84, 64, 25),
  }),
  Model.new(18, {
    [1] = Vertex.new(0, 232, -104, 95, 87, 87, 0.5608),
    [2] = Vertex.new(108, 232, 4, 95, 87, 87, 0.5608),
    [3] = Vertex.new(80, 232, -72, 95, 87, 87, 0.5608),
    [5] = Vertex.new(76, 232, 84, 95, 87, 87, 0.5608),
    [8] = Vertex.new(0, 232, 112, 95, 87, 87, 0.5608),
  }),
})
local sieve = Model.multi({
  Model.new(312, {
    [93] = Vertex.new(-80, 72, 28, 65, 54, 41),
    [95] = Vertex.new(-80, 72, 28, 65, 54, 41),
    [129] = Vertex.new(-72, 96, 52, 65, 54, 41),
    [132] = Vertex.new(-72, 96, 52, 65, 54, 41),
    [138] = Vertex.new(-24, 72, 84, 65, 54, 41),
  }),
  Model.new(144, {
    [81] = Vertex.new(-48, 96, 76, 128, 128, 128),
    [85] = Vertex.new(-48, 96, 76, 128, 128, 128),
    [87] = Vertex.new(-72, 96, 52, 128, 128, 128),
    [88] = Vertex.new(-48, 96, 76, 128, 128, 128),
    [91] = Vertex.new(-72, 96, 52, 128, 128, 128),
  }),
})
local toxicNaphtha = Model.multi({
  Model.new(354, {
    [163] = Vertex.new(-76, 288, 80, 85, 65, 26),
    [166] = Vertex.new(-76, 288, 80, 85, 65, 26),
    [170] = Vertex.new(-76, 288, 80, 85, 65, 26),
    [177] = Vertex.new(76, 288, 80, 85, 65, 26),
    [210] = Vertex.new(76, 288, 80, 85, 65, 26),
  }),
  Model.new(18, {
    [3] = Vertex.new(80, 232, -72, 35, 177, 39, 0.5608),
    [5] = Vertex.new(76, 232, 84, 35, 177, 39, 0.5608),
    [9] = Vertex.new(76, 232, 84, 35, 177, 39, 0.5608),
    [11] = Vertex.new(-76, 232, 84, 35, 177, 39, 0.5608),
    [17] = Vertex.new(-76, 232, -72, 35, 177, 39, 0.5608),
  }),
})
local toxicPowder = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 154, 142, 141),
  [34] = Vertex.new(100, 0, 60, 154, 142, 141),
  [42] = Vertex.new(100, 0, 60, 154, 142, 141),
  [64] = Vertex.new(132, 0, -24, 154, 142, 141),
  [72] = Vertex.new(132, 0, -24, 154, 142, 141),
})
--#endregion

local scrollTexture =
  "\x04\x0b\x0c\xff\x07\x0e\x13\xff\x14\x18\x17\xff\x76\x59\x3c\xff\x79\x59\x3a\xff\x87\x67\x45\xff\x96\x74\x50\xff\xa7\x84\x5c\xff\xb4\x90\x64\xff\xb7\x90\x64\xff\x9f\x7a\x53\xff\x71\x57\x38\xff\x6d\x53\x36\xff\x76\x5b\x3c\xff\x86\x6a\x42\xff\x99\x7c\x53\xff\xa2\x85\x5a\xff\xa9\x89\x61\xff\xaf\x8f\x66\xff\xb3\x94\x6b\xff\xb6\x98\x6e\xff\xb6\x98\x6c\xff\xb9\x9a\x6e\xff\xbb\x9c\x6f\xff\xbd\x9b\x6d\xff\xc0\xa0\x71\xff\xc0\xa1\x72\xff\xc3\xa4\x75\xff\xc4\xa3\x76\xff\xc4\xa2\x73\xff\xc3\xa2\x72\xff\xc7\xa5\x76\xff\xc9\xa7\x76\xff\xcb\xa9\x7c\xff\xcc\xab\x7e\xff\xcf\xaf\x82\xff\xd1\xb0\x83\xff\xd2\xb1\x80\xff\xd2\xb0\x7d\xff\xd1\xb0\x7d\xff\xd2\xb0\x7f\xff\xd5\xb2\x81\xff\xd6\xb2\x83\xff\xd8\xb5\x86\xff\xd8\xb3\x83\xff\xd9\xb4\x84\xff\xd9\xb6\x86\xff\xd9\xb5\x82\xff\xdb\xb8\x87\xff\xda\xba\x87\xff\xdb\xbc\x89\xff\xdd\xbf\x8e\xff\xdd\xc0\x90\xff\xdd\xbe\x8f\xff\xdc\xbf\x8f\xff\xdd\xbe\x90\xff\xdd\xc0\x90\xff\xdc\xbe\x8f\xff\xdd\xbe\x90\xff\xdd\xbf\x90\xff\xdc\xbd\x8e\xff\xde\xbc\x8e\xff\xdd\xbc\x8b\xff\xdc\xbc\x8c\xff\xdc\xba\x8a\xff\xda\xb6\x84\xff\xd6\xb1\x7e\xff\xd6\xb2\x7f\xff\xd9\xb6\x84\xff\xdb\xbb\x88\xff\xdd\xbd\x8c\xff\xde\xbf\x8e\xff\xdf\xc0\x91\xff\xdf\xbe\x8f\xff\xe0\xbf\x8e\xff\xe0\xbe\x8e\xff\xe0\xbe\x8e\xff\xde\xbd\x8b\xff\xde\xbf\x90\xff\xde\xbf\x8f\xff\xdf\xc0\x91\xff\xdf\xc1\x91\xff\xde\xc0\x91\xff\xde\xc1\x90\xff\xde\xc0\x90\xff\xdc\xbd\x8c\xff\xde\xbf\x8f\xff\xdd\xbe\x8c\xff\xdd\xbd\x8b\xff\xe0\xc0\x8e\xff\xe1\xc2\x90\xff\xe0\xc2\x91\xff\xde\xbf\x8e\xff\xdd\xbe\x8d\xff\xde\xbe\x8f\xff\xde\xbd\x8e\xff\xdd\xbc\x8d\xff\xdf\xc0\x8f\xff\xe2\xc3\x93\xff\xe1\xbe\x8d\xff\xdf\xbd\x8e\xff\xe3\xc3\x95\xff\xe5\xc4\x97\xff\xe7\xc8\x9a\xff\xe6\xc7\x99\xff\xe8\xc8\x9a\xff\xeb\xcc\x9f\xff\xea\xcd\x9f\xff\xea\xcd\x9f\xff\xea\xce\xa0\xff\xeb\xce\xa3\xff\xeb\xce\xa2\xff\xe8\xc8\x9c\xff\xe6\xc6\x9c\xff\xeb\xcf\xa2\xff\xea\xcc\x9f\xff\xea\xcf\x9f\xff\xe9\xcd\x9d\xff\xea\xcf\x9d\xff\xe9\xcd\x9a\xff\xea\xcd\x9b\xff\xea\xcd\x9a\xff\xe9\xcc\x9b\xff\xea\xcc\x9c\xff\xeb\xcd\x9f\xff\xe9\xcb\x9d\xff\xea\xcc\x9f\xff\xe9\xcb\x9e\xff\xeb\xcc\x9d\xff\xeb\xcb\x9c\xff\xeb\xcc\x9b\xff\xea\xcb\x9c\xff\xe8\xc9\x9a\xff\xe6\xc6\x95\xff\xe7\xc8\x96\xff\xe5\xc5\x91\xff\xe5\xc5\x92\xff\xe7\xc6\x95\xff\xe6\xc8\x96\xff\xe6\xc5\x96\xff\xe6\xc6\x99\xff\xe7\xc7\x99\xff\xe7\xc8\x9b\xff\xe6\xc6\x98\xff\xe5\xc5\x9a\xff\xe5\xc5\x98\xff\xe7\xc7\x9b\xff\xe4\xc5\x9a\xff\xe5\xca\xa2\xff\xe6\xce\xac\xff\xe5\xcd\xa9\xff\xe5\xcc\xa3\xff\xe4\xc8\x99\xff\xe5\xc8\x97\xff\xe3\xc6\x92\xff\xe4\xc7\x96\xff\xe5\xc6\x95\xff\xe5\xc6\x99\xff\xe4\xc6\x97\xff\xe4\xc5\x94\xff\xe6\xc7\x96\xff\xe5\xc6\x97\xff\xe4\xc5\x96\xff\xe3\xc4\x93\xff\xe3\xc4\x92\xff\xe4\xc6\x95\xff\xe3\xc6\x94\xff\xe3\xc6\x93\xff\xe3\xc6\x93\xff\xe3\xc6\x96\xff\xe3\xc6\x96\xff\xe3\xc6\x96\xff\xe3\xc5\x95\xff\xe5\xc5\x96\xff\xe5\xc6\x98\xff\xe5\xc6\x98\xff\xe5\xc6\x99\xff\xe5\xc5\x98\xff\xe4\xc5\x98\xff\xe5\xc5\x97\xff\xe6\xc9\x9c\xff\xe6\xca\x9e\xff\xe6\xcb\x9b\xff\xe7\xcb\x9c\xff\xe7\xca\x9b\xff\xe7\xca\x9b\xff\xe7\xca\x9b\xff\xe5\xc8\x97\xff\xe6\xc8\x97\xff\xe4\xc4\x8e\xff\xe4\xc3\x90\xff\xe5\xc5\x94\xff\xe6\xc8\x98\xff\xe7\xc9\x9b\xff\xe5\xc6\x98\xff\xe4\xc9\x9a\xff\xe5\xc8\x9c\xff\xe3\xc5\x97\xff\xe5\xc8\x97\xff\xe6\xca\x9a\xff\xe7\xcc\x9c\xff\xe7\xce\x9d\xff\xe9\xcf\x9e\xff\xe9\xcf\x9f\xff\xea\xce\x9e\xff\xea\xce\x9a\xff\xea\xcd\x9d\xff\xea\xcd\x9d\xff\xea\xcf\x9e\xff\xea\xd0\xa1\xff\xe9\xcf\xa1\xff\xea\xd0\xa4\xff\xeb\xd0\xa3\xff\xeb\xce\x9f\xff\xe9\xcd\x9e\xff\xe9\xcc\x9c\xff\xea\xce\x9e\xff\xea\xcc\x9d\xff\xe8\xca\x99\xff\xe9\xca\x98\xff\xe9\xca\x97\xff\xe8\xc7\x93\xff\xe7\xc7\x94\xff\xe7\xc6\x8e\xff\xe7\xc7\x93\xff\xe7\xc6\x95\xff\xe6\xc5\x95\xff\xe6\xc5\x94\xff\xe4\xc4\x94\xff\xe4\xc4\x94\xff\xe3\xc2\x95\xff\xe5\xc4\x94\xff\xe7\xc7\x99\xff\xe6\xc8\x98\xff\xe6\xca\x9a\xff\xe8\xcc\x9c\xff\xe9\xcd\x9e\xff\xe8\xcd\x9d\xff\xe6\xc9\x9b\xff\xe5\xc9\x9c\xff\xe5\xc7\x9c\xff\xe3\xc3\x94\xff\xe3\xc3\x97\xff\xe4\xc5\x98\xff\xe1\xc2\x95\xff\xe4\xc4\x98\xff\xe4\xc3\x97\xff\xe4\xc1\x95\xff\xe3\xc0\x95\xff\xe2\xbe\x92\xff\xe0\xbd\x8f\xff\xe1\xbe\x91\xff\xe2\xbe\x90\xff\xe0\xc0\x94\xff\xdf\xc7\xa2\xff\xe0\xc7\xa1\xff\xe1\xc1\x94\xff\xe0\xc0\x91\xff\xdf\xc0\x91\xff\xde\xbf\x92\xff\xdf\xc1\x92\xff\xde\xc0\x8d\xff\xe0\xc4\x92\xff\xe2\xc5\x94\xff\xe2\xc3\x90\xff\xe2\xc4\x92\xff\xe0\xc1\x8c\xff\xe0\xc1\x8c\xff\xe1\xc3\x8f\xff\xe1\xc2\x8f\xff\xe1\xc1\x8d\xff\xe0\xc1\x8d\xff\xe0\xc2\x8f\xff\xe1\xc3\x8f\xff\xe3\xc4\x91\xff\xe5\xc6\x93\xff\xe4\xc4\x93\xff\xe3\xc5\x95\xff\xe1\xc4\x94\xff\xe3\xc6\x97\xff\xe1\xc0\x92\xff\xde\xb8\x87\xff\xdd\xb6\x82\xff\xde\xb9\x88\xff\xdd\xb7\x89\xff\xdd\xb9\x8b\xff\xdc\xb8\x8b\xff\xe0\xbe\x92\xff\xe1\xc0\x92\xff\xdf\xc1\x90\xff\xe2\xc6\x96\xff\xe3\xc7\x97\xff\xe2\xc4\x93\xff\xe1\xc3\x92\xff\xe1\xc6\x95\xff\xe0\xc4\x92\xff\xe0\xc1\x90\xff\xe1\xc2\x93\xff\xe2\xc2\x92\xff\xe2\xc1\x94\xff\xe1\xbf\x90\xff\xe1\xbf\x90\xff\xe1\xbf\x90\xff\xdf\xbe\x8e\xff\xdf\xbe\x8f\xff\xde\xbd\x8c\xff\xdd\xba\x85\xff\xdf\xc0\x8e\xff\xdd\xbd\x8b\xff\xdc\xbb\x8b\xff\xdd\xbc\x8d\xff\xde\xbe\x8e\xff\xdd\xbd\x8c\xff\xde\xbf\x8e\xff\xe0\xc2\x91\xff\xe1\xc2\x91\xff\xdf\xc0\x8e\xff\xdd\xbc\x8b\xff\xde\xbd\x8c\xff\xde\xbf\x90\xff\xdc\xbc\x8c\xff\xde\xbf\x8f\xff\xdf\xc0\x8f\xff\xdf\xc0\x8f\xff\xe0\xc1\x90\xff\xe0\xbf\x90\xff\xdf\xbf\x8d\xff\xdf\xbe\x8c\xff\xdf\xbc\x89\xff\xde\xba\x88\xff\xdf\xbd\x8c\xff\xdf\xbd\x8b\xff\xde\xbc\x8a\xff\xde\xbe\x8c\xff\xde\xbd\x8a\xff\xdc\xbc\x88\xff\xdd\xbd\x8b\xff\xdb\xba\x87\xff\xdb\xba\x87\xff\xdc\xbb\x8a\xff\xe0\xc1\x8e\xff\xe0\xbf\x8d\xff\xe0\xbd\x8d\xff\xe2\xbf\x8f\xff\xe4\xc2\x93\xff\xe3\xc0\x90\xff\xe4\xc2\x92\xff\xe3\xc0\x8f\xff\xe2\xbe\x8f\xff\xe3\xbf\x8f\xff\xe3\xc0\x8e\xff\xe2\xc0\x8e\xff\xe4\xc2\x91\xff\xe5\xc4\x93\xff\xe6\xc4\x93\xff\xe5\xc3\x92\xff\xe6\xc5\x93\xff\xe7\xc7\x96\xff\xe7\xc5\x95\xff\xe8\xc6\x95\xff\xe9\xc9\x96\xff\xeb\xcd\x9d\xff\xea\xce\xa0\xff\xea\xce\xa1\xff\xeb\xd0\xa6\xff\xea\xd0\xa4\xff\xea\xce\x9f\xff\xe9\xcc\x9c\xff\xe8\xca\x9c\xff\xea\xcd\xa0\xff\xea\xcd\xa0\xff\xe8\xcc\x9e\xff\xe8\xcd\x9d\xff\xe8\xcb\x9c\xff\xe7\xc9\x98\xff\xe6\xc6\x91\xff\xe6\xc5\x8f\xff\xe8\xc8\x96\xff\xe7\xc6\x94\xff\xe6\xc5\x94\xff\xe6\xc5\x95\xff\xe4\xc3\x92\xff\xe4\xc3\x94\xff\xe4\xc3\x94\xff\xe2\xc0\x90\xff\xe5\xc6\x99\xff\xe7\xc9\x99\xff\xe6\xc9\x99\xff\xe8\xcc\x9d\xff\xe8\xcd\x9d\xff\xe7\xcb\x9b\xff\xe5\xc8\x9b\xff\xe4\xc8\x9b\xff\xe4\xc7\x9b\xff\xe3\xc6\x97\xff\xe4\xc5\x9a\xff\xe3\xc5\x99\xff\xe1\xc2\x96\xff\xe4\xc5\x99\xff\xe3\xc2\x97\xff\xe3\xc1\x94\xff\xe4\xc1\x96\xff\xe1\xbf\x92\xff\xe1\xbe\x91\xff\xe0\xbd\x8f\xff\xe1\xbd\x8f\xff\xdf\xc2\x97\xff\xe0\xc7\xa1\xff\xe0\xc7\xa0\xff\xe1\xc3\x95\xff\xe0\xc0\x91\xff\xdf\xbf\x90\xff\xde\xbe\x90\xff\xe0\xc0\x92\xff\xe0\xc1\x90\xff\xe1\xc3\x93\xff\xe1\xc2\x92\xff\xe1\xc3\x91\xff\xe1\xc3\x90\xff\xe0\xc2\x8d\xff\xdf\xc1\x8d\xff\xdf\xc0\x8d\xff\xe0\xc3\x90\xff\xe2\xc3\x8f\xff\xe1\xc3\x8f\xff\xe1\xc3\x90\xff\xe1\xc2\x8e\xff\xe4\xc5\x92\xff\xe4\xc5\x93\xff\xe4\xc5\x93\xff\xe3\xc5\x94\xff\xe1\xc4\x95\xff\xe2\xc4\x95\xff\xe1\xc1\x92\xff\xe0\xbe\x8c\xff\xe1\xbf\x8d\xff\xe2\xc1\x8f\xff\xe4\xc2\x91\xff\xe5\xc4\x94\xff\xe3\xc5\x95\xff\xe4\xc5\x95\xff\xe3\xc5\x95\xff\xe2\xc3\x94\xff\xe2\xc3\x94\xff\xe2\xc4\x96\xff\xe4\xc6\x96\xff\xe2\xc2\x90\xff\xe3\xc4\x95\xff\xe2\xc6\x96\xff\xe1\xc4\x96\xff\xe1\xc5\x96\xff\xe1\xc2\x93\xff\xe1\xc2\x93\xff\xdf\xc0\x91\xff\xde\xc0\x91\xff\xde\xbe\x8e\xff\xdb\xbe\x8d\xff\xdb\xbf\x8f\xff\xd9\xbe\x90\xff\xd8\xbc\x8e\xff\xd7\xba\x8c\xff\xd3\xb7\x8a\xff\xd2\xb6\x88\xff\xd1\xb7\x88\xff\xd1\xb7\x88\xff\xcf\xb5\x86\xff\xce\xb3\x83\xff\xcd\xb2\x83\xff\xca\xaf\x80\xff\xc9\xae\x80\xff\xc4\xa4\x78\xff\xc1\x9f\x73\xff\xbf\x9d\x73\xff\xbc\x9b\x6e\xff\xba\x98\x6c\xff\xb7\x95\x6a\xff\xb4\x92\x67\xff\xa7\x8b\x62\xff\x96\x7d\x56\xff\x8e\x77\x50\xff\x85\x6f\x49\xff\x80\x6a\x45\xff\x77\x5c\x3c\xff\x70\x57\x37\xff\x9f\x7c\x50\xff\xc1\x9b\x69\xff\xc1\x9b\x6a\xff\xbd\x94\x64\xff\xad\x85\x5a\xff\x96\x75\x4c\xff\x7f\x61\x3c\xff\x72\x55\x32\xff\x6f\x55\x39\xff\x07\x0d\x10\xff\x06\x0c\x12\xff\x07\x11\x17\xff"
local bankXButtonTexture =
  "\x6b\x2d\x28\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6d\x31\x2a\xff\x6d\x31\x2a\xff\x6b\x2d\x28\xff\xb3\x8c\x5d\xff\xcb\xab\x6f\xff\xb3\x8c\x5d\xff\x6b\x2d\x28\xff\x6b\x2d\x28\xff\x6d\x31\x2a\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6b\x2d\x28\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Eluned in Isafdar.<ul><li>Chop magic logs nearby.</li><li>Kill a grizzly bear just west of Lletya if you need bear fur.</li></ul>",
    title = "Meeting the rebels",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2290, 1005, 3149) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["eluned"]),
      Condition.QuestInterfaceOpen:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["eluned"]),
      Action.ConversationHighlight:new("Talk about Mourning's End"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["eluned"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Eluned.",
    actions = { Action.ModelHighlight:new(Models.npcs["eluned"]) },
    postconditions = { Condition.DistanceTo:new(2351, 1669, 3172, 4) },
  },
  {
    text = "Talk to Arianwyn.",
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Kill a mourner at Arandar.",
    title = "Mourners in the mountains",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Eagles' Peak lodestone",
      url = "Eagles'_Peak_lodestone_icon.png",
    },
    neededItems = {
      ["Silk"] = { quantity = 2 },
      ["Crystal teleport seed"] = { quantity = 1 },
      ["Bear fur"] = { quantity = 1 },
      ["Bucket of water"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2385.5, 5213, 3334.5) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["mourner"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["mourner"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["mourner"]) },
    jumpOffset = -1,
    postconditions = { Condition.ModelVisible:new(bloodyMournerTop) },
  },
  {
    text = "Pick up all items the mourner drops.<ul><li>Get 2 sets for Within the Light.</li></ul>",
    actions = {
      Action.ModelHighlight:new(gasMask),
      Action.ModelHighlight:new(mournerCape),
      Action.ModelHighlight:new(mournerBoots),
      Action.ModelHighlight:new(mournerGloves),
      Action.ModelHighlight:new(bloodyMournerTop),
      Action.ModelHighlight:new(mournerLetter),
      Action.ModelHighlight:new(tornMournerTrousers),
    },
    postconditions = { Condition.InventoryContains:new(gasMask) },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(gasMask),
      Action.ModelHighlight:new(mournerCape),
      Action.ModelHighlight:new(mournerBoots),
      Action.ModelHighlight:new(mournerGloves),
      Action.ModelHighlight:new(bloodyMournerTop),
      Action.ModelHighlight:new(mournerLetter),
      Action.ModelHighlight:new(tornMournerTrousers),
    },
    postconditions = { Condition.InventoryContains:new(mournerCape) },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(gasMask),
      Action.ModelHighlight:new(mournerCape),
      Action.ModelHighlight:new(mournerBoots),
      Action.ModelHighlight:new(mournerGloves),
      Action.ModelHighlight:new(bloodyMournerTop),
      Action.ModelHighlight:new(mournerLetter),
      Action.ModelHighlight:new(tornMournerTrousers),
    },
    postconditions = { Condition.InventoryContains:new(mournerBoots) },
  },
  {
    -- text = "debug3",
    actions = {
      Action.ModelHighlight:new(gasMask),
      Action.ModelHighlight:new(mournerCape),
      Action.ModelHighlight:new(mournerBoots),
      Action.ModelHighlight:new(mournerGloves),
      Action.ModelHighlight:new(bloodyMournerTop),
      Action.ModelHighlight:new(mournerLetter),
      Action.ModelHighlight:new(tornMournerTrousers),
    },
    postconditions = { Condition.InventoryContains:new(mournerGloves) },
  },
  {
    -- text = "debug4",
    actions = {
      Action.ModelHighlight:new(gasMask),
      Action.ModelHighlight:new(mournerCape),
      Action.ModelHighlight:new(mournerBoots),
      Action.ModelHighlight:new(mournerGloves),
      Action.ModelHighlight:new(bloodyMournerTop),
      Action.ModelHighlight:new(mournerLetter),
      Action.ModelHighlight:new(tornMournerTrousers),
    },
    postconditions = { Condition.InventoryContains:new(bloodyMournerTop) },
  },
  {
    -- text = "debug5",
    actions = {
      Action.ModelHighlight:new(gasMask),
      Action.ModelHighlight:new(mournerCape),
      Action.ModelHighlight:new(mournerBoots),
      Action.ModelHighlight:new(mournerGloves),
      Action.ModelHighlight:new(bloodyMournerTop),
      Action.ModelHighlight:new(mournerLetter),
      Action.ModelHighlight:new(tornMournerTrousers),
    },
    postconditions = { Condition.InventoryContains:new(mournerLetter) },
  },
  {
    -- text = "debug6",
    actions = {
      Action.ModelHighlight:new(gasMask),
      Action.ModelHighlight:new(mournerCape),
      Action.ModelHighlight:new(mournerBoots),
      Action.ModelHighlight:new(mournerGloves),
      Action.ModelHighlight:new(bloodyMournerTop),
      Action.ModelHighlight:new(mournerLetter),
      Action.ModelHighlight:new(tornMournerTrousers),
    },
    postconditions = { Condition.InventoryContains:new(tornMournerTrousers) },
  },
  {
    text = "Read the letter.",
    actions = { Action.InventoryHighlight:new(mournerLetter) },
    postconditions = { Condition.Generic2DVisible:new(496, 293, 165, scrollTexture) },
  },
  {
    text = "Talk to Tegid at the Taverly lake.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Taverley lodestone",
      url = "Taverley_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2920.5, 77, 3418) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["tegid"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["tegid"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["tegid"]) },
    jumpOffset = -1,
    postconditions = {
      Condition.ConversationText:new("I'll try it"), --completed Eadgar's Ruse
      Condition.ConversationText:new("give it a try"), --else
    },
  },
  {
    text = "Search the laundry basket.",
    actions = {
      Action.Direction:new(2917, 385, 3419),
      Action.ConversationHighlight:new("Steal the soap"),
    },
    postconditions = { Condition.InventoryContains:new(soap) },
  },
  {
    text = "Use the soap on the bloody mourner top with a bucket of water.",
    actions = {
      Action.InventoryHighlight:new(soap),
      Action.InventoryHighlight:new(bloodyMournerTop),
    },
    postconditions = { Condition.InventoryContains:new(cleanMournerTop) },
  },
  {
    text = "Talk to Oronwen in the Lletya clothing shop.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    actions = { Action.Direction:new(2326, 1669, 3176) },
    postconditions = { Condition.ModelVisible:new(oronwen) },
  },
  {
    actions = {
      Action.ModelHighlight:new(oronwen),
      Action.ConversationHighlight:new("Do you mend clothes?"),
      Action.ConversationHighlight:new("I have all I need to mend my trousers."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(oronwen) },
    jumpOffset = -1,
    postconditions = {
      Condition.ConversationText:new("have them done"),
    },
  },
  {
    text = "Wait for the trousers to be mended.<ul><li>Buy 3 of each dye from her.</li></ul>",
    actions = {
      Action.ModelHighlight:new(oronwen),
      Action.ConversationHighlight:new("How are you doing with my trousers?"),
    },
    postconditions = { Condition.InventoryContains:new(mendedMournerTrousers) },
  },
  {
    text = "Equip the mourner gear.",
    title = "Amongst the Death Guard",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {
      ["Feather"] = { quantity = 1 },
      ["Toad crunchies"] = { quantity = 1 },
      ["Soft leather"] = { quantity = 1 },
      ["Magic log"] = { quantity = 1 },
      ["Mourner gear"] = { quantity = 1, model = Models.items["mourner gear"] },
      ["Mourner letter"] = { quantity = 1, model = mournerLetter },
    },
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(Models.items["mourner gear"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["mourner gear"]) },
  },
  {
    text = "Enter the Mourners' headquarters in the north-eastern corner of West Ardougne.",
    actions = { Action.Direction:new(2551, 1785, 3320.4) },
    postconditions = { Condition.DistanceTo:new(2551, 1285, 3322, 1) },
  },
  {
    text = "Descend the trapdoor.",
    actions = { Action.Direction:new(2542, 1285, 3327, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2044, 7973, 4649, 4) },
  },
  {
    text = "Talk to the Head Mourner.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["head mourner"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("to explain it all") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["head mourner"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("you sure") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["head mourner"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("need of an explanation") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["head mourner"]),
      Action.ConversationHighlight:new("No"),
    },
    postconditions = { Condition.ConversationText:new("tarnished key") },
  },
  {
    text = "Talk to the gnome on a rack in the west room.",
    actions = {
      Action.ModelHighlight:new(gnomeOnARack),
      Action.ConversationHighlight:new("You said about toad crunchies and being tickled."),
    },
    postconditions = { Condition.ConversationText:new("sooner or later") },
  },
  {
    text = "Use a feather on the gnome.",
    actions = {
      Action.ModelHighlight:new(gnomeOnARack),
      Action.InventoryHighlight:new(Models.items["feather"]),
    },
    postconditions = { Condition.ConversationText:new("quite working") },
  },
  {
    text = "Use a feather on the gnome again.",
    actions = {
      Action.ModelHighlight:new(gnomeOnARack),
      Action.InventoryHighlight:new(Models.items["feather"]),
    },
    postconditions = { Condition.ConversationText:new("little blighter") },
  },
  {
    text = "Talk to the gnome again.",
    actions = { Action.ModelHighlight:new(gnomeOnARack) },
    postconditions = { Condition.ConversationText:new("all tied up") },
  },
  {
    text = "Release the gnome.",
    actions = { Action.ModelHighlight:new(gnomeOnARack) },
    postconditions = { Condition.ConversationText:new("toad crunchies") },
  },
  {
    text = "Talk to the gnome after a minute passes.<ul><li>Lobby if he disappears.</li></ul>",
    actions = { Action.ModelHighlight:new(gnome) },
    postconditions = { Condition.ConversationText:new("take it") },
  },
  {
    text = "Enter Rantz's cave in Feldip Hills.",
    title = "Dyeing the non-dying",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "AKS",
    },
    neededItems = {
      ["Ogre bellows"] = { quantity = 1 },
      ["Fixed device"] = { quantity = 1, model = fixedDevice },
      ["Mourner gear"] = { quantity = 1 },
      ["Red dye"] = { quantity = 3 },
      ["Blue dye"] = { quantity = 3 },
      ["Yellow dye"] = { quantity = 3 },
      ["Green dye"] = { quantity = 3 },
      ["red toad"] = { quantity = 3, model = redToad },
      ["blue toad"] = { quantity = 3, model = blueToad },
      ["yellow toad"] = { quantity = 3, model = yellowToad },
      ["green toad"] = { quantity = 3, model = greenToad },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2631, 1317, 2997) },
    jumpconditions = { Condition.InventoryContains:new(Models.items["ogre bellows"]) },
    jumpOffset = 5,
    postconditions = { Condition.ModelVisible:new(Models.objects["rantz cave entrance"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["rantz cave entrance"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.objects["rantz cave entrance"]) },
    jumpOffset = -1,
    postconditions = { Condition.DistanceTo:new(2647, 1133, 9378, 4) },
  },
  {
    text = "'Unlock' the locked ogre chest.",
    actions = { Action.ModelHighlight:new(Models.objects["locked ogre chest"]) },
    postconditions = {
      Condition.ChatText:new("rock off the chest"),
      Condition.ModelVisible:new(Models.objects["unlocked ogre chest"]),
    },
  },
  {
    text = "Search the chest.",
    actions = { Action.ModelHighlight:new(Models.objects["unlocked ogre chest"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["ogre bellows"]) },
  },
  {
    text = "Exit the cave.",
    actions = { Action.Direction:new(2646, 1849, 9376) },
    postconditions = { Condition.DistanceTo:new(2629, 1453, 2997, 4) },
  },
  {
    text = "Use the bottle of dye on your empty ogre bellows. Use the blue dye bellows on the swamp toad.",
    warning = "If your ogre bellows is filled with swamp gas, use it on swamp toads until empty.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["swamp toad"], { highlightPriority = "closest" }),
      Action.InventoryHighlight:new(Models.items["empty ogre bellows"]),
      Action.InventoryHighlight:new(Models.items["blue dye"]),
    },
    postconditions = { Condition.InventoryContains:new(blueToad, 3) },
  },
  {
    text = "Repeat the same steps with red dye.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["swamp toad"], { highlightPriority = "closest" }),
      Action.InventoryHighlight:new(Models.items["empty ogre bellows"]),
      Action.InventoryHighlight:new(Models.items["red dye"]),
    },
    postconditions = { Condition.InventoryContains:new(redToad, 3) },
  },
  {
    text = "Repeat the same steps with yellow dye.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["swamp toad"], { highlightPriority = "closest" }),
      Action.InventoryHighlight:new(Models.items["empty ogre bellows"]),
      Action.InventoryHighlight:new(Models.items["yellow dye"]),
    },
    postconditions = { Condition.InventoryContains:new(yellowToad, 3) },
  },
  {
    text = "Repeat the same steps with green dye.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["swamp toad"], { highlightPriority = "closest" }),
      Action.InventoryHighlight:new(Models.items["empty ogre bellows"]),
      Action.InventoryHighlight:new(Models.items["green dye"]),
    },
    postconditions = { Condition.InventoryContains:new(greenToad, 3) },
  },
  {
    text = "Use a red toad on the fixed device.",
    actions = {
      Action.InventoryHighlight:new(redToad),
      Action.InventoryHighlight:new(fixedDevice),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(redToad, 3) },
  },
  {
    text = "Equip the fixed device.",
    actions = { Action.InventoryHighlight:new(fixedDevice, true) },
    postconditions = { Condition.InventoryDoesNotContain:new(fixedDevice) },
  },
  {
    text = "Fire the toad at the sick-looking sheep (1) from the equipment interface.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2613, 613, 3346, { tile = true }),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (1)"], { highlightPriority = "all" }),
      Action.InventoryHighlight:new(fixedDevice, true),
    },
    postconditions = { Condition.ModelVisible:new(freshSickLookingSheep1) },
  },
  {
    text = "Fire a green toad at the sick-looking sheep (2) from the equipment interface.",
    actions = {
      Action.Direction:new(2612, 789, 3368, { tile = true }),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (2)"], { highlightPriority = "all" }),
      Action.InventoryHighlight:new(greenToad),
      Action.InventoryHighlight:new(fixedDevice),
    },
    postconditions = { Condition.ModelVisible:new(freshSickLookingSheep2) },
  },
  {
    text = "Fire a yellow toad at the sick-looking sheep (4) from the equipment interface.",
    warning = "No tracking for this step.",
    actions = {
      Action.Direction:new(2612, 349, 3376, { tile = true }),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (4)"], { highlightPriority = "all" }),
      Action.InventoryHighlight:new(yellowToad),
      Action.InventoryHighlight:new(fixedDevice),
    },
    -- postconditions = { Condition.ModelVisible:new(freshSickLookingSheep4) },
  },
  {
    text = "Fire a blue toad at the sick-looking sheep (3) from the equipment interface.",
    actions = {
      Action.Direction:new(2565, 1037, 3389, { tile = true }),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (3)"], { highlightPriority = "all" }),
      Action.InventoryHighlight:new(blueToad),
      Action.InventoryHighlight:new(fixedDevice),
    },
    postconditions = { Condition.ModelVisible:new(freshSickLookingSheep3) },
  },
  {
    text = "Unequip the fixed device.",
    postconditions = { Condition.InventoryContains:new(fixedDevice) },
  },
  {
    text = "Return to the Head Mourner at the Mourners' Headquarters.",
    actions = { Action.Direction:new(2551, 1785, 3320.4) },
    postconditions = {
      Condition.DistanceTo:new(2551, 1285, 3322, 1),
      Condition.DistanceTo:new(2045, 7973, 4629, 20),
    },
  },
  {
    actions = { Action.Direction:new(2542, 1285, 3327, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2045, 7973, 4629, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["head mourner"]) },
    postconditions = { Condition.ConversationText:new("Yes Sir") },
  },
  {
    text = "Climb the ladder.",
    title = "Elena's advice",
    neededItems = { ["Rotten apple"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2044, 8473, 4650) },
    postconditions = { Condition.DistanceTo:new(2543, 1285, 3327, 4) },
  },
  {
    text = "Take a rotten apple from the ground west of the headquarters.",
    actions = { Action.Direction:new(2535, 1253, 3333, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(rottenApple) },
  },
  {
    text = "Talk to Elena, north-east of Ardougne Castle.",
    actions = { Action.Direction:new(2592, 1157, 3336) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["elena"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["elena"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["elena"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("rotten apple for you") },
  },
  {
    text = "Talk to Elena again.",
    actions = { Action.ModelHighlight:new(Models.npcs["elena"]) },
    postconditions = { Condition.ConversationText:new("leave you to it") },
  },
  {
    text = "Wait a minute or world-hop, then talk to her again.",
    actions = { Action.ModelHighlight:new(Models.npcs["elena"]) },
    postconditions = { Condition.ConversationText:new("infected the trees") },
  },
  {
    text = "Pick up an empty barrel in the fenced-in apple orchard north-west of Ardougne.<ul><li>Grab an extra if you don't have a barrel of naphtha.</li></ul>",
    title = "Preparing the poison",
    neededItems = {
      ["Coal"] = { quantity = 10 },
      ["Barrel of naphtha"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2487, 901, 3371) },
    postconditions = { Condition.ModelVisible:new(barrel) },
  },
  {
    actions = { Action.ModelHighlight:new(barrel) },
    jumpconditions = { Condition.ModelNotVisible:new(barrel) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(barrel) },
  },
  {
    text = "Use the empty barrel on the rotten apple pile.",
    actions = {
      Action.Direction:new(2487, 949, 3374, { tile = true }),
      Action.InventoryHighlight:new(barrel),
    },
    postconditions = { Condition.InventoryContains:new(barrelOfRottenApples) },
  },
  {
    text = "Use the apple barrel.",
    actions = { Action.Direction:new(2484, 1597, 3374) },
    postconditions = { Condition.InventoryContains:new(barrelOfCrushedRottenApples) },
  },
  {
    text = "Use your barrel on the tar south of the Tirannwn lodestone.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2263, 325, 3127, { tile = true }),
      Action.InventoryHighlight:new(barrel),
    },
    jumpconditions = { Condition.InventoryContains:new(barrelOfNaphtha) },
    jumpOffset = 5,
    postconditions = { Condition.InventoryContains:new(barrelOfTar) },
  },
  {
    text = "Use your barrel of tar on the fractionation still outside of the Chemist's house.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Port Sarim lodestone",
      url = "Port_Sarim_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2927, 2185, 3212),
      Action.InventoryHighlight:new(barrelOfTar),
    },
    postconditions = { Condition.Generic2DVisible:new(15, 15, 7, bankXButtonTexture) },
  },
  {
    text = "Create a barrel of naphtha.<ul><li>Increase the tar regulator twice.</li><li>Wait until the pressure valve is in the green.</li><li>Increase the pressure valve once.</li><li>'Add coal' in 1-2 increments to get the heat valve in the green.</li><li>When the distillation bar is full, exit the interface.</li></ul>",
    warning = "No tracking for this step.",
    postconditions = { Condition.InventoryContains:new(barrelOfNaphtha) },
  },
  {
    actions = {
      Action.Direction:new(2927, 2185, 3212),
      Action.InventoryHighlight:new(barrelOfTar),
    },
    postconditions = { Condition.Generic2DVisible:new(15, 15, 7, bankXButtonTexture) },
  },
  { postconditions = { Condition.InventoryContains:new(barrelOfNaphtha) } },
  {
    text = "Use the barrel of naphtha on the apple barrel, then use the apple mix on your sieve.",
    actions = {
      Action.InventoryHighlight:new(sieve),
      Action.InventoryHighlight:new(barrelOfNaphtha),
      Action.InventoryHighlight:new(barrelOfCrushedRottenApples),
    },
    postconditions = { Condition.InventoryContains:new(toxicNaphtha) },
  },
  {
    text = "Use the toxic naphtha on any cooking range.",
    warning = "Do NOT use the toxic naphtha on an open fire or it'll explode.",
    actions = { Action.InventoryHighlight:new(toxicPowder) },
    postconditions = { Condition.InventoryContains:new(toxicPowder) },
  },
  {
    text = "Enter the largest building in the centre of West Ardougne.",
    title = "West Ardougne's woe",
    actions = { Action.Direction:new(2525.5, 1789, 3310.5) },
    postconditions = { Condition.DistanceTo:new(2526, 1189, 3313, 2) },
  },
  {
    text = "Use one of the toxic powder on one of the grain sacks.",
    actions = {
      Action.Direction:new(2521, 1189, 3316, { tile = true }),
      Action.InventoryHighlight:new(toxicPowder),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(toxicPowder, 2) },
  },
  {
    text = "Climb up the stairs in the church to the south.",
    actions = { Action.Direction:new(2531, 1557, 3294) },
    postconditions = { Condition.DistanceToWithHeight:new(2530, 2117, 3294, 4) },
  },
  {
    text = "Use the toxic powder on the grain.",
    actions = {
      Action.Direction:new(2531, 2117, 3291, { tile = true }),
      Action.InventoryHighlight:new(toxicPowder),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(toxicPowder) },
  },
  {
    text = "Return to the Head Mourner at the Mourners' Headquarters.",
    actions = { Action.Direction:new(2551, 1785, 3320.4) },
    postconditions = {
      Condition.DistanceTo:new(2551, 1285, 3322, 1),
      Condition.DistanceTo:new(2045, 7973, 4629, 20),
    },
  },
  {
    actions = { Action.Direction:new(2542, 1285, 3327, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2045, 7973, 4629, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["head mourner"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Mourning's End Part I",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1121731200,
  prereqQuests = { "Roving Elves", "Sheep Herder", "Big Chompy Bird Hunting" },
  questReqs = {
    Types.QuestReq.skill("Ranged", 60),
    Types.QuestReq.skill("Thieving", 50),
  },
  neededItems = {
    ["Silk"] = { quantity = 2, model = Models.items["silk"] },
    ["Bear fur"] = { quantity = 1, model = bearFur, duringQuest = true },
    ["Bucket of water"] = { quantity = 1, model = Models.items["bucket of water"] },
    ["Ogre bellows"] = { quantity = 1, model = Models.items["ogre bellows"], duringQuest = true },
    ["Red dye"] = { quantity = 3, model = Models.items["red dye"], duringQuest = true },
    ["Blue dye"] = { quantity = 3, model = Models.items["blue dye"], duringQuest = true },
    ["Yellow dye"] = { quantity = 3, model = Models.items["yellow dye"], duringQuest = true },
    ["Green dye"] = { quantity = 3, model = Models.items["green dye"], duringQuest = true },
    ["Rotten apple"] = { quantity = 1, model = rottenApple, duringQuest = true },
    ["Coal"] = { quantity = 10, model = Models.items["coal"] },
    ["Barrel of naphtha"] = { quantity = 1, model = Models.items["barrel of naphtha"] },
    ["Feather"] = { quantity = 1, model = Models.items["feather"] },
    ["Toad crunchies"] = { quantity = 1, model = toadCrunchies },
    ["Soft leather"] = { quantity = 1, model = Models.items["leather"] },
    ["Magic log"] = { quantity = 1, model = Models.items["magic logs"], duringQuest = true },
  },
  recommendedItems = {
    ["Ring of dueling"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  combatNPCs = { ["Mourner"] = { level = "79-86", quantity = 1 } },
})
