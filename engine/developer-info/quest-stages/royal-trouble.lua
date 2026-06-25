local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local gardenerGunnhild = Model.new(4419, {
  [2739] = Vertex.new(24, 738, -38, 32, 30, 29),
  [2763] = Vertex.new(-24, 738, -38, 32, 30, 29),
  [2785] = Vertex.new(-2, 716, -57, 152, 98, 79),
  [2791] = Vertex.new(2, 716, -57, 152, 98, 79),
  [2795] = Vertex.new(6, 716, -52, 152, 98, 79),
})
local matilda = Model.new(3858, {
  [1884] = Vertex.new(24, 738, -38, 32, 30, 29),
  [1908] = Vertex.new(-24, 738, -38, 32, 30, 29),
  [1930] = Vertex.new(-2, 716, -57, 109, 80, 57),
  [1936] = Vertex.new(2, 716, -57, 109, 80, 57),
  [1940] = Vertex.new(6, 716, -52, 109, 80, 57),
})
local sailor = Model.new(4953, {
  [2899] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2907] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2909] = Vertex.new(7, 724, -51, 109, 80, 57),
  [4416] = Vertex.new(-65, 670, 50, 31, 37, 49),
  [4447] = Vertex.new(65, 670, 50, 31, 37, 49),
})
local donal = Model.new(6285, {
  [4533] = Vertex.new(14, 205, 101, 17, 9, 8),
  [4541] = Vertex.new(14, 258, 60, 17, 9, 8),
  [4544] = Vertex.new(-14, 258, 60, 17, 9, 8),
  [4554] = Vertex.new(-14, 205, 101, 17, 9, 8),
  [4556] = Vertex.new(32, 204, 84, 17, 9, 8),
})
local thorodin = Model.new(6459, {
  [888] = Vertex.new(-237, 204, -157, 43, 41, 39),
  [890] = Vertex.new(-237, 254, -188, 43, 41, 39),
  [896] = Vertex.new(-175, 206, -202, 43, 41, 39),
  [4449] = Vertex.new(-103, 486, 45, 60, 58, 76),
  [4461] = Vertex.new(-103, 487, 45, 72, 70, 92),
})
--#endregion
--#region Objects
local ladder = Model.new(276, {
  [233] = Vertex.new(7040, 2752, 3168, 91, 82, 58),
  [237] = Vertex.new(6784, 2752, 3072, 91, 82, 58),
  [245] = Vertex.new(6784, 1824, 3168, 81, 70, 42),
  [249] = Vertex.new(7040, 2752, 3168, 81, 70, 42),
  [257] = Vertex.new(7040, 1824, 3072, 81, 70, 42),
})
local coalRock = Model.any({
  Model.new(2808, {
    [841] = Vertex.new(361, -60, -335, 127, 127, 127),
    [885] = Vertex.new(-457, -61, -256, 127, 127, 127),
    [887] = Vertex.new(-409, -65, -271, 127, 127, 127),
    [891] = Vertex.new(-457, -61, -256, 127, 127, 127),
    [912] = Vertex.new(-457, -61, -256, 127, 127, 127),
  }),
  Model.new(3252, {
    [207] = Vertex.new(-108, 521, 77, 127, 127, 127),
    [2751] = Vertex.new(120, -299, -234, 127, 127, 127),
    [2752] = Vertex.new(120, -299, -234, 127, 127, 127),
    [2844] = Vertex.new(-226, 383, -124, 127, 127, 127),
    [2932] = Vertex.new(221, -66, 252, 127, 127, 127),
  }),
})
local brokenScaffold = Model.new(228, {
  [79] = Vertex.new(232, 1876, -48, 79, 71, 50),
  [84] = Vertex.new(236, 1876, 44, 79, 71, 50),
  [96] = Vertex.new(232, 1876, -48, 71, 62, 36),
  [108] = Vertex.new(136, 1876, -44, 79, 71, 50),
  [120] = Vertex.new(140, 1876, 48, 71, 62, 36),
})
local partiallyRepairedScaffold = Model.new(468, {
  [139] = Vertex.new(232, 1876, -48, 79, 71, 50),
  [144] = Vertex.new(236, 1876, 44, 79, 71, 50),
  [156] = Vertex.new(232, 1876, -48, 71, 62, 36),
  [168] = Vertex.new(136, 1876, -44, 79, 71, 50),
  [180] = Vertex.new(140, 1876, 48, 71, 62, 36),
})
local attachedLongPullyBeam = Model.new(330, {
  [21] = Vertex.new(-508, 1180, 0, 46, 36, 3),
  [129] = Vertex.new(748, 916, 44, 71, 62, 36),
  [282] = Vertex.new(-600, 1224, 48, 85, 76, 53),
  [322] = Vertex.new(552, 1316, 48, 71, 62, 36),
  [327] = Vertex.new(552, 1316, 48, 71, 62, 36),
})
local twoPullyOverhang = Model.new(522, {
  [171] = Vertex.new(748, 916, 44, 71, 62, 36),
  [183] = Vertex.new(652, 1024, -36, 71, 62, 36),
  [474] = Vertex.new(-600, 1224, 48, 85, 76, 53),
  [514] = Vertex.new(552, 1316, 48, 71, 62, 36),
  [519] = Vertex.new(552, 1316, 48, 71, 62, 36),
})
local scaffoldingWithRope = Model.new(552, {
  [223] = Vertex.new(232, 1876, -48, 79, 71, 50),
  [228] = Vertex.new(236, 1876, 44, 79, 71, 50),
  [240] = Vertex.new(232, 1876, -48, 71, 62, 36),
  [252] = Vertex.new(136, 1876, -44, 79, 71, 50),
  [264] = Vertex.new(140, 1876, 48, 71, 62, 36),
})
local brokenPlatform = Model.new(333, {
  [9] = Vertex.new(220, 0, 212, 85, 76, 53),
  [144] = Vertex.new(-212, 0, 212, 71, 62, 36),
  [207] = Vertex.new(-200, 20, -200, 64, 53, 19),
  [258] = Vertex.new(-200, 20, 168, 64, 53, 19),
  [276] = Vertex.new(-200, 20, -200, 79, 71, 50),
})
local fixedPlatform = Model.new(540, {
  [501] = Vertex.new(-228, 432, 4, 85, 76, 53),
  [513] = Vertex.new(-232, 432, 4, 85, 76, 53),
  [525] = Vertex.new(244, 428, 4, 85, 76, 53),
  [537] = Vertex.new(236, 432, 4, 85, 76, 53),
  [540] = Vertex.new(236, 432, 4, 85, 76, 53),
})
local enginePlatform = Model.new(243, {
  [45] = Vertex.new(256, 28, 244, 71, 62, 36),
  [63] = Vertex.new(-232, 28, -236, 71, 62, 36),
  [120] = Vertex.new(256, 0, 160, 71, 62, 36),
  [147] = Vertex.new(256, 0, -256, 71, 62, 36),
  [225] = Vertex.new(260, 236, -164, 46, 36, 3),
})
local attachedEngine = Model.new(1746, {
  [147] = Vertex.new(256, 0, -256, 71, 62, 36),
  [204] = Vertex.new(60, 136, -204, 59, 53, 45),
  [207] = Vertex.new(-116, 172, -212, 59, 53, 45),
  [213] = Vertex.new(-132, 248, -212, 59, 53, 45),
  [222] = Vertex.new(60, 316, -204, 59, 53, 45),
})
local lift = Model.new(594, {
  [23] = Vertex.new(8, 2152, 4, 46, 36, 3),
  [45] = Vertex.new(0, 2156, 0, 46, 36, 3),
  [48] = Vertex.new(0, 2156, 0, 46, 36, 3),
})
local ropeRock = Model.new(189, {
  [9] = Vertex.new(256, 1716, 68, 62, 62, 47),
  [75] = Vertex.new(-256, 1880, 108, 62, 62, 47),
  [94] = Vertex.new(-256, 1940, 68, 62, 62, 47),
  [146] = Vertex.new(256, 1876, 136, 62, 62, 47),
  [189] = Vertex.new(256, 1716, 68, 62, 62, 47),
})
local ropeSwing = Model.new(519, {
  [126] = Vertex.new(256, 1876, 136, 62, 62, 47),
  [202] = Vertex.new(-256, 1940, 68, 62, 62, 47),
  [214] = Vertex.new(-256, 1880, 108, 62, 62, 47),
  [246] = Vertex.new(256, 1716, 68, 62, 62, 47),
  [252] = Vertex.new(256, 1716, 68, 62, 62, 47),
})
local campfire = Model.new(888, {
  [17] = Vertex.new(-172, 0, -192, 62, 62, 47),
  [716] = Vertex.new(-60, 88, -180, 29, 21, 11),
  [829] = Vertex.new(-120, 40, -60, 29, 21, 11),
  [847] = Vertex.new(-112, 92, -232, 29, 21, 11),
  [855] = Vertex.new(-124, 96, -244, 29, 21, 11),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local scroll = Model.new(366, {
  [21] = Vertex.new(84, 56, 84, 123, 102, 64),
  [38] = Vertex.new(-76, 56, 84, 123, 102, 64),
  [59] = Vertex.new(84, 56, 84, 154, 128, 80),
  [63] = Vertex.new(84, 56, 84, 154, 128, 80),
  [339] = Vertex.new(-76, 56, 84, 154, 128, 80),
})
local miningProp = Model.new(282, {
  [9] = Vertex.new(-148, 520, -16, 79, 68, 50),
  [11] = Vertex.new(-136, 532, 32, 79, 68, 50),
  [69] = Vertex.new(-136, 532, 32, 71, 59, 36),
  [241] = Vertex.new(-136, 532, 32, 64, 49, 19),
  [244] = Vertex.new(-136, 532, 32, 64, 49, 19),
})
local engine = Model.new(1470, {
  [102] = Vertex.new(260, 236, -164, 56, 50, 43),
  [105] = Vertex.new(-132, 248, -212, 56, 50, 43),
  [114] = Vertex.new(60, 316, -204, 56, 50, 43),
  [120] = Vertex.new(-116, 172, -212, 56, 50, 43),
  [126] = Vertex.new(60, 136, -204, 56, 50, 43),
})
local pullyBeam = Model.new(240, {
  [189] = Vertex.new(-236, 192, -40, 88, 76, 55),
  [191] = Vertex.new(-188, 308, 24, 88, 76, 55),
  [193] = Vertex.new(-188, 308, 24, 68, 52, 20),
  [196] = Vertex.new(-188, 308, 24, 68, 52, 20),
  [227] = Vertex.new(-188, 308, 24, 78, 64, 40),
})
local beam = Model.new(60, {
  [3] = Vertex.new(240, 128, -40, 94, 81, 59),
  [11] = Vertex.new(-244, 136, 24, 94, 81, 59),
  [20] = Vertex.new(240, 128, -40, 72, 55, 21),
  [24] = Vertex.new(240, 128, -40, 72, 55, 21),
  [35] = Vertex.new(240, 128, -40, 81, 67, 41),
})
local longPullyBeam = Model.new(288, {
  [189] = Vertex.new(88, 964, -40, 88, 76, 55),
  [191] = Vertex.new(208, 928, 24, 88, 76, 55),
  [239] = Vertex.new(88, 964, -40, 78, 64, 40),
  [243] = Vertex.new(208, 928, 24, 78, 64, 40),
  [288] = Vertex.new(208, 928, 24, 68, 52, 20),
})
local longLongMan = Model.new(336, {
  [189] = Vertex.new(-52, 1456, -40, 88, 76, 55),
  [191] = Vertex.new(72, 1448, 24, 88, 76, 55),
  [263] = Vertex.new(-52, 1456, -40, 78, 64, 40),
  [267] = Vertex.new(72, 1448, 24, 78, 64, 40),
  [336] = Vertex.new(72, 1448, 24, 68, 52, 20),
})
local diary1 = Model.new(234, {
  [1] = Vertex.new(56, 24, 68, 29, 21, 11),
  [2] = Vertex.new(-68, 12, 76, 29, 21, 11),
  [3] = Vertex.new(60, 12, 64, 29, 21, 11),
  [5] = Vertex.new(-68, 24, 76, 29, 21, 11),
  [7] = Vertex.new(-12, 24, 56, 29, 46, 57),
  [8] = Vertex.new(-12, 32, 56, 29, 46, 57),
  [9] = Vertex.new(-80, 40, 64, 29, 46, 57),
  [10] = Vertex.new(64, 28, 64, 29, 46, 57),
  [14] = Vertex.new(64, 32, 64, 29, 46, 57),
  [16] = Vertex.new(-8, 0, 56, 29, 46, 57),
  [17] = Vertex.new(-8, 8, 56, 29, 46, 57),
  [25] = Vertex.new(-8, 8, -84, 57, 71, 91),
  [26] = Vertex.new(-76, 12, -88, 57, 71, 91),
  [27] = Vertex.new(-72, 12, -16, 57, 71, 91),
  [100] = Vertex.new(48, 24, 64, 97, 75, 49),
  [101] = Vertex.new(-56, 24, -96, 97, 75, 49),
  [102] = Vertex.new(-56, 24, 68, 97, 75, 49),
  [104] = Vertex.new(52, 24, -4, 97, 75, 49),
  [217] = Vertex.new(-76, 40, -88, 67, 88, 107),
  [224] = Vertex.new(-72, 40, -16, 67, 88, 107),
})
-- local diary2 --missing
-- local diary3 --missing
local dairy4 = Model.new(234, {
  [1] = Vertex.new(56, 36, 68, 29, 21, 11),
  [2] = Vertex.new(-68, 12, 76, 29, 21, 11),
  [3] = Vertex.new(60, 12, 64, 29, 21, 11),
  [5] = Vertex.new(-68, 36, 76, 29, 21, 11),
  [7] = Vertex.new(-12, 36, 56, 29, 46, 57),
  [8] = Vertex.new(-12, 44, 56, 29, 46, 57),
  [9] = Vertex.new(-80, 40, 64, 29, 46, 57),
  [10] = Vertex.new(64, 40, 64, 29, 46, 57),
  [14] = Vertex.new(64, 44, 64, 29, 46, 57),
  [16] = Vertex.new(-8, 0, 56, 29, 46, 57),
  [17] = Vertex.new(-8, 8, 56, 29, 46, 57),
  [25] = Vertex.new(-8, 8, -84, 57, 71, 91),
  [26] = Vertex.new(-76, 12, -88, 57, 71, 91),
  [27] = Vertex.new(-72, 12, -16, 57, 71, 91),
  [100] = Vertex.new(48, 36, 64, 97, 75, 49),
  [101] = Vertex.new(-56, 36, -96, 97, 75, 49),
  [102] = Vertex.new(-56, 36, 68, 97, 75, 49),
  [104] = Vertex.new(52, 36, -4, 97, 75, 49),
  [217] = Vertex.new(-76, 40, -88, 67, 88, 107),
  [224] = Vertex.new(-72, 40, -16, 67, 88, 107),
})
local diary5 = Model.new(234, { --incomplete
  [118] = Vertex.new(64, 12, -64, 29, 21, 11),
  [121] = Vertex.new(64, 12, -64, 29, 21, 11),
  [197] = Vertex.new(68, 44, -88, 29, 46, 57),
  [199] = Vertex.new(68, 44, -88, 29, 46, 57),
  [202] = Vertex.new(68, 44, -88, 29, 46, 57),
})
local heavyBox = Model.new(444, {
  [149] = Vertex.new(20, 332, -176, 144, 112, 74),
  [199] = Vertex.new(20, 328, 208, 144, 112, 74),
  [205] = Vertex.new(20, 328, 208, 144, 112, 74),
  [221] = Vertex.new(8, 328, 208, 144, 112, 74),
  [227] = Vertex.new(8, 328, 208, 144, 112, 74),
})
local rope = Model.new(1329, {
  [1300] = Vertex.new(-60, 2268, 24, 147, 132, 92),
  [1302] = Vertex.new(-72, 2252, 36, 147, 132, 92),
  [1316] = Vertex.new(-52, 2276, 52, 147, 132, 92),
  [1322] = Vertex.new(-68, 2256, 52, 147, 132, 92),
  [1326] = Vertex.new(-68, 2256, 52, 147, 132, 92),
})
--#endregion

local bookTexture =
  "\x39\x1c\x0b\x00\x53\x29\x0c\x25\x8b\x49\x1b\xff\x73\x34\x0c\xff\x6b\x2f\x0a\xff\x7b\x3b\x12\xff\x5b\x24\x02\xff\x7b\x3b\x12\xff\x6b\x2f\x0a\xff\x2b\x11\x04\xff\x6b\x3b\x17\xff\xe5\xb4\x63\xff\xe3\xab\x4b\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa5\x4a\xff\xda\xa3\x4b\xff\xdf\xab\x5a\xff\xe9\xbd\x73\xff\xeb\xc4\x83\xff\xec\xc4\x7b\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xee\xd3\xa0\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf1\xd6\xa8\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd6\xa8\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xe6\xca\x90\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xdc\xbf\x89\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xde\xc0\x84\xff\xd5\xb9\x85\xff\xd5\xb9\x85\xff\xdb\xbc\x83\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd2\xad\x6a\xff\xc6\xa3\x6b\xff\xcb\xa4\x62\xff\xc7\x9c\x5a\xff\xc3\x94\x52\xff\xc3\x94\x52\xff\xbc\x84\x42\xff\xba\x7c\x3a\xff\xb9\x7a\x34\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb5\x7a\x3a\xff\xbc\x84\x42\xff\xc2\x8d\x4a\xff\x99\x6a\x3b\xff\x94\x52\x22\xff\x3c\x22\x10\xff\x3c\x22\x10\xff\x94\x52\x22\xff\x99\x6a\x3b\xff\xc2\x8d\x4a\xff\xbc\x84\x42\xff\xb5\x7a\x3a\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb9\x7a\x34\xff\xba\x7c\x3a\xff\xbc\x84\x42\xff\xc3\x94\x52\xff\xc3\x94\x52\xff\xc7\x9c\x5a\xff\xcb\xa4\x62\xff\xc6\xa3\x6b\xff\xd2\xad\x6a\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbc\x83\xff\xd5\xb9\x85\xff\xd5\xb9\x85\xff\xde\xc0\x84\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xdc\xbf\x89\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe6\xca\x90\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd6\xa8\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd6\xa8\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xee\xd3\xa0\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xec\xc4\x7b\xff\xeb\xc4\x83\xff\xe9\xbd\x73\xff\xdf\xab\x5a\xff\xda\xa3\x4b\xff\xe1\xa5\x4a\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe3\xab\x4b\xff\xe5\xb4\x63\xff\x6b\x3b\x17\xff\x2b\x11\x04\xff\x6b\x2f\x0a\xff\x7b\x3b\x12\xff\x5b\x24\x02\xff\x7b\x3b\x12\xff\x6b\x2f\x0a\xff\x73\x34\x0c\xff\x8b\x49\x1b\xff\x53\x29\x0c\x25\x39\x1c\x0b\x00"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Advisor Ghrim in Miscellania.",
    title = "Getting started",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20),
      Condition.DistanceToWithHeight:new(2504, 3321, 3860, 8),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["advisor ghrim"]),
      Action.ConversationHighlight:new("Has anything been happening in the kingdom recently?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Advisor Ghrim.",
    actions = { Action.ModelHighlight:new(Models.npcs["advisor ghrim"]) },
    postconditions = { Condition.ConversationText:new("like to see you once again") },
  },
  {
    text = "Speak to your spouse.",
    title = "Recent events",
    actions = {
      Action.ModelHighlight:new(Models.npcs["prince brand"]),
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Wait for the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to King Vargas.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["king vargas"]),
      Action.ConversationHighlight:new("Right away, Your Majesty"),
    },
    postconditions = { Condition.ConversationText:new("in such capable hands") }, --not correct
  },
  {
    text = "Talk to Gardener Gunnhild at the flax patch.",
    actions = {
      Action.Direction:new(2527, 2229, 3855, { distance = 16 }),
      Action.ModelHighlight:new(gardenerGunnhild, { distance = 16 }),
      Action.ConversationHighlight:new("Royal Trouble."),
    },
    postconditions = { Condition.ConversationText:new("had been stolen") },
  },
  {
    text = "Talk to King Vargas.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20),
      Condition.DistanceToWithHeight:new(2504, 3363, 3860, 8),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("talk to her right away") }, --not correct
  },
  {
    text = "Talk to Queen Sigrid.",
    actions = { Action.Direction:new(2614, 1201, 3867) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2615, 1869, 3867, 20),
      Condition.DistanceToWithHeight:new(2612, 1893, 3874, 6),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["queen sigrid"]),
      Action.ConversationHighlight:new("Of course, it's my duty."),
    },
    postconditions = { Condition.ConversationText:new("have the time free") },
  },
  {
    text = "Talk to Matilda in the Etceteria castle courtyard.",
    actions = {
      Action.Direction:new(2603.5, 901, 3871.5, { distance = 8 }),
      Action.ModelHighlight:new(matilda, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Talk to Queen Sigrid again.",
    actions = { Action.Direction:new(2614, 1201, 3867) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2615, 1869, 3867, 20),
      Condition.DistanceToWithHeight:new(2612, 1893, 3874, 6),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["queen sigrid"]),
      Action.ConversationHighlight:new("I suppose so..."),
    },
    postconditions = { Condition.ConversationText:new("get used to it") }, --not correct
  },
  {
    text = "Talk to King Vargas.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20),
      Condition.DistanceToWithHeight:new(2504, 3363, 3860, 8),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("I'll do that") },
  },
  {
    text = "Talk to Advisor Ghrim.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["advisor ghrim"]),
      Action.ConversationHighlight:new("King Vargas asked me to talk to you."),
    },
    postconditions = { Condition.ConversationText:new("recent arrivals to the island") },
  },
  {
    text = "Talk to the Sailor on the docks in Miscellania.",
    actions = {
      Action.Direction:new(2580, 325, 3845, { distance = 20 }),
      Action.ModelHighlight:new(sailor, { distance = 20 }),
      Action.ConversationHighlight:new("I'm looking for a sailor..."),
    },
    postconditions = { Condition.ConversationText:new("Gielinor") }, --not correct
  },
  {
    text = "Talk to King Vargas again with at least 1 inventory space free.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20),
      Condition.DistanceToWithHeight:new(2504, 3363, 3860, 8),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.InventoryContains:new(scroll) },
  },
  {
    text = "Gear up for the final fight now. Having 8 free inventory spaces is recommended.",
    title = "Miscellania dungeons",
    warning = "Manually move to the next step when you're ready.",
  },
  {
    text = "Climb down the dungeon ladder in the southern portion of the Miscellania courtyard.",
    actions = { Action.Direction:new(2509, 2245, 3846) },
    postconditions = { Condition.DistanceTo:new(2509, 1189, 10245, 4) },
  },
  {
    text = "Talk to Donal.",
    actions = {
      Action.Direction:new(2529, 1189, 10256, { distance = 4 }),
      Action.ModelHighlight:new(donal, { distance = 5 }),
      Action.ConversationHighlight:new("Of course. Dealing with monsters is what I do best!"),
    },
    postconditions = { Condition.ConversationText:new("fighting that monster") },
  },
  {
    text = "Use the mining prop on the crevice to the north.",
    actions = {
      Action.Direction:new(2505, 1765, 10281.5),
      Action.InventoryHighlight:new(miningProp),
    },
    postconditions = { Condition.ChatText:new("wedge the prop in the wall") },
  },
  {
    text = "Squeeze through the crevice to the north.",
    actions = { Action.Direction:new(2505, 1765, 10281.5) },
    postconditions = { Condition.DistanceTo:new(2505, 1165, 10283, 1) },
  },
  {
    text = "Pick up 3 beams.",
    title = "Fixing the lift",
    warning = "There is a bug where sometimes you won't be able to pick up the engine. Leave the dungeon and come back, or try to use area loot.",
    actions = { Action.Direction:new(2504, 1437, 10284) },
    postconditions = { Condition.InventoryContains:new(beam, 3) },
  },
  {
    text = "Pick up 3 pully beams.",
    actions = { Action.Direction:new(2504, 1437, 10285) },
    postconditions = { Condition.InventoryContains:new(pullyBeam, 3) },
  },
  {
    text = "Pick up an engine.",
    actions = { Action.Direction:new(2504, 1437, 10287) },
    postconditions = { Condition.InventoryContains:new(engine) },
  },
  {
    text = "Pick up 2 ropes.",
    actions = { Action.Direction:new(2506, 1437, 10289) },
    postconditions = { Condition.InventoryContains:new(Models.items["rope"], 2) },
  },
  {
    text = "Mine 5 coal.",
    actions = { Action.ModelHighlight:new(coalRock, { highlightPriority = "all" }) },
    postconditions = { Condition.InventoryContains:new(Models.items["coal"], 5) },
  },
  {
    text = "Use a pulley beam on the broken scaffolding.",
    actions = {
      Action.ModelHighlight:new(brokenScaffold),
      Action.InventoryHighlight:new(pullyBeam),
    },
    postconditions = { Condition.ModelNotVisible:new(brokenScaffold) },
  },
  {
    text = "Use a beam on a pulley beam.",
    actions = {
      Action.InventoryHighlight:new(beam),
      Action.InventoryHighlight:new(pullyBeam),
    },
    postconditions = { Condition.InventoryContains:new(longPullyBeam) },
  },
  {
    text = "Attach another beam to get a longer pulley beam.",
    actions = {
      Action.InventoryHighlight:new(longPullyBeam),
      Action.InventoryHighlight:new(beam),
    },
    postconditions = { Condition.InventoryContains:new(longLongMan) }, --google it
  },
  {
    text = "Use the longer pully beam on the scaffolding.",
    actions = {
      Action.ModelHighlight:new(partiallyRepairedScaffold),
      Action.InventoryHighlight:new(longLongMan),
    },
    postconditions = { Condition.ModelVisible:new(attachedLongPullyBeam) },
  },
  {
    text = "Use your last pulley beam on the scaffolding.",
    actions = {
      Action.ModelHighlight:new(partiallyRepairedScaffold),
      Action.InventoryHighlight:new(pullyBeam),
    },
    postconditions = { Condition.ModelVisible:new(twoPullyOverhang) },
  },
  {
    text = "Use your rope on the scaffolding.",
    actions = {
      Action.ModelHighlight:new(partiallyRepairedScaffold),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.ModelVisible:new(scaffoldingWithRope) },
  },
  {
    text = "Use the third beam on the platform (north side).",
    actions = {
      Action.ModelHighlight:new(brokenPlatform),
      Action.InventoryHighlight:new(beam),
    },
    postconditions = { Condition.ModelVisible:new(fixedPlatform) },
  },
  {
    text = "Add 5 coal to the engine, then use it on the engine platform.",
    actions = {
      Action.InventoryHighlight:new(engine),
      Action.InventoryHighlight:new(Models.items["coal"]),
      Action.ModelHighlight:new(enginePlatform),
    },
    postconditions = { Condition.ModelVisible:new(attachedEngine) },
  },
  {
    text = "Use the lift.",
    actions = { Action.ModelHighlight:new(lift) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2509, 2005, 10288, 4),
      Condition.DistanceToWithHeight:new(2510, 2021, 10287, 4),
    },
  },
  {
    text = "Pick up the plank off the ground, and enter the tunnel.",
    title = "Exploring the tunnel",
    actions = { Action.ModelHighlight:new(Models.items["plank"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["plank"]) },
  },
  {
    text = "Enter the tunnel",
    actions = { Action.Direction:new(2511, 2321, 10287) },
    postconditions = { Condition.DistanceTo:new(2514, 997, 10291, 4) },
  },
  {
    text = "Use a rope in your inventory on the northern end of the rock.",
    actions = {
      Action.ModelHighlight:new(ropeRock),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.ModelVisible:new(ropeSwing) },
  },
  {
    text = "Swing across the river.",
    actions = { Action.ModelHighlight:new(ropeSwing) },
    postconditions = { Condition.DistanceTo:new(2543, 429, 10299, 1) },
  },
  {
    text = "Search the fire remains to obtain a burnt diary.",
    actions = { Action.ModelHighlight:new(campfire, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(diary1) },
  },
  {
    text = "Use the plank to cross the small rocks.",
    actions = {
      Action.InventoryHighlight:new(Models.items["plank"]),
      Action.PathGuide:new({
        Location:new(2554, 1189, 10294),
        Location:new(2548, 397, 10288),
        Location:new(2545, 389, 10287),
        Location:new(2542, 389, 10287),
        Location:new(2539, 357, 10286),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2537, 661, 10285, 1) },
  },
  {
    text = "Search the fire remains.",
    actions = { Action.ModelHighlight:new(campfire, { highlightPriority = "closest" }) },
    postconditions = { Condition.ChatText:new("add them to the diary you have") },
  },
  {
    text = "Search the fire remains to the east.",
    actions = { Action.ModelHighlight:new(campfire, { atLocation = Location:new(2554, 1149, 10279) }) },
    postconditions = { Condition.DistanceTo:new(2554, 1149, 10279, 1) },
  },
  {
    text = "Search the fire remains to the south.<ul><li>Surge across the vents to avoid any damage.</li></ul>",
    actions = { Action.ModelHighlight:new(campfire, { atLocation = Location:new(2549, 837, 10261) }) },
    postconditions = {
      -- Condition.ModelVisible:new(diary4),
      Condition.DistanceTo:new(2549, 837, 10261, 1),
    },
  },
  {
    text = "Search the last fire remains to the east.",
    actions = { Action.ModelHighlight:new(campfire, { atLocation = Location:new(2573, 869, 10246) }) },
    postconditions = { Condition.DistanceTo:new(2573, 869, 10247, 1) },
  },
  {
    text = "Read the burnt diary.",
    actions = { Action.InventoryHighlight:new(diary5) },
    postconditions = { Condition.Generic2DVisible:new(482, 290, 140, bookTexture) },
  },
  {
    text = "Squeeze through the crevice at the end of the path.",
    actions = { Action.Direction:new(2585, 1525, 10260.5) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the custcene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Finish the dialogue with the teenagers.",
    title = "Final fight",
    postconditions = { Condition.ConversationText:new("tell my father how badly") },
  },
  {
    text = "Squeeze through the crevice at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2572, 1189, 10276),
        Location:new(2571, 1213, 10283),
        Location:new(2571, 1016, 10291),
        Location:new(2576, 1317, 10296),
        Location:new(2584, 1021, 10295),
        Location:new(2593, 1133, 10286),
        Location:new(2596, 981, 10275),
        Location:new(2598, 1516, 10257),
        Location:new(2605, 1253, 10252),
        Location:new(2614, 1205, 10258),
        Location:new(2617, 1077, 10265),
        Location:new(2617, 1157, 10271),
      }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill the Giant Sea Snake, and pick up the heavy box.",
    actions = { Action.ModelHighlight:new(heavyBox, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(heavyBox) },
  },
  {
    text = "Go back through the crevice.",
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Climb up the rope.",
    actions = { Action.ModelHighlight:new(rope) },
    postconditions = { Condition.DistanceTo:new(2620, 797, 3864, 4) },
  },
  {
    text = "Talk to Queen Sigrid with at least 1 free inventory space.<ul><li>If you forgot to pick up the heavy box, it can be obtained by speaking with the guard by the escape hole.</li></ul>",
    actions = { Action.Direction:new(2614, 1201, 3867) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2615, 1869, 3867, 20),
      Condition.DistanceToWithHeight:new(2612, 1893, 3874, 6),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["queen sigrid"]) },
    postconditions = { Condition.ConversationText:new("please give my letter to Vargas") },
  },
  {
    text = "Talk to King Vargas.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20),
      Condition.DistanceToWithHeight:new(2504, 3363, 3860, 8),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Royal Trouble",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1148256000,
  prereqQuests = { "Throne of Miscellania" },
  questReqs = {
    Types.QuestReq.skill("Agility", 40),
    Types.QuestReq.skill("Slayer", 40),
  },
  neededItems = {
    ["Coal"] = { quantity = 5, model = Models.items["coal"], duringQuest = true },
    ["Rope"] = { quantity = 2, model = Models.items["rope"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = { ["Giant Sea Snake"] = { quantity = 1, level = "63" } },
})
