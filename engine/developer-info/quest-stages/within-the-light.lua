local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local gnome = Model.new(3621, {
  [90] = Vertex.new(-47, 483, 19, 96, 84, 50),
  [170] = Vertex.new(47, 483, 19, 96, 84, 50),
  [3346] = Vertex.new(0, 440, -10, 15, 167, 42),
  [3347] = Vertex.new(2, 440, -12, 15, 167, 42),
  [3348] = Vertex.new(-2, 440, -12, 15, 167, 42),
})
local arianwynMourner = Model.new(5505, {
  [3985] = Vertex.new(0, 735, -7, 28, 140, 127),
  [3987] = Vertex.new(0, 732, -8, 28, 140, 127),
  [4854] = Vertex.new(-65, 670, 50, 14, 14, 22),
  [4885] = Vertex.new(65, 670, 50, 14, 14, 22),
  [5500] = Vertex.new(132, 377, -18, 132, 97, 68),
})
local arianwynMournerNoMask = Model.new(5133, {
  [37] = Vertex.new(0, 732, 1, 28, 140, 127),
  [38] = Vertex.new(0, 729, 1, 28, 140, 127),
  [4996] = Vertex.new(65, 670, 50, 14, 14, 22),
  [5031] = Vertex.new(-65, 670, 50, 14, 14, 22),
  [5128] = Vertex.new(-132, 377, -18, 132, 97, 68),
})
--#endregion
--#region Objects
local openLightDoor = Model.new(1212, {
  [36] = Vertex.new(200, 624, -16, 254, 254, 254, 0.4902),
  [493] = Vertex.new(-256, 452, 0, 254, 254, 254, 0.05882),
  [496] = Vertex.new(-256, 452, 0, 254, 254, 254, 0.05882),
  [512] = Vertex.new(256, 452, 0, 254, 254, 254, 0.05882),
  [573] = Vertex.new(-200, 612, 32, 254, 254, 254, 0.3725),
})
local lightPillar = Model.new(630, {
  [512] = Vertex.new(-260, 600, -20, 14, 13, 13),
  [517] = Vertex.new(-260, 600, -20, 14, 13, 13),
  [524] = Vertex.new(-260, 600, 20, 14, 13, 13),
  [538] = Vertex.new(-20, 600, -260, 14, 13, 13),
  [548] = Vertex.new(20, 600, -260, 14, 13, 13),
})
local redLightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 250, 56, 41, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 250, 56, 41, 0.2157),
  [45] = Vertex.new(16, 576, -256, 250, 56, 41, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 250, 56, 41, 0.2157),
  [69] = Vertex.new(8, 592, -256, 250, 56, 41, 0.2157),
})
local blueLightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 110, 113, 251, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 110, 113, 251, 0.2157),
  [45] = Vertex.new(16, 576, -256, 110, 113, 251, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 110, 113, 251, 0.2157),
  [69] = Vertex.new(8, 592, -256, 110, 113, 251, 0.2157),
})
local energizedPillar = Model.new(5316, {
  [4499] = Vertex.new(-4, 472, -184, 30, 162, 168),
  [4500] = Vertex.new(-4, 528, -184, 30, 162, 168),
  [4503] = Vertex.new(8, 528, -184, 30, 162, 168),
  [4515] = Vertex.new(180, 528, 8, 173, 171, 30),
  [4521] = Vertex.new(-184, 528, -8, 123, 30, 161),
})
local deathRuins = Model.new(885, {
  [310] = Vertex.new(2392, 570, 7551, 137, 126, 125),
  [347] = Vertex.new(2471, 669, 7707, 137, 126, 125),
  [422] = Vertex.new(2367, 345, 8036, 137, 126, 125),
  [788] = Vertex.new(3103, 689, 8394, 137, 126, 125),
  [826] = Vertex.new(2859, 390, 8342, 137, 126, 125),
})
local deathAltarPortal = Model.new(1572, {
  [11] = Vertex.new(-192, 152, 160, 178, 63, 55, 0.1765),
  [14] = Vertex.new(168, 152, 176, 178, 63, 55, 0.1765),
  [359] = Vertex.new(-228, 152, -108, 178, 63, 55, 0.1765),
  [389] = Vertex.new(-140, 140, -208, 178, 63, 55, 0.1765),
  [1463] = Vertex.new(136, 144, -212, 178, 63, 55, 0.1765),
})
local crystalObj = Model.new(576, {
  [125] = Vertex.new(-440, 556, 100, 170, 157, 170, 0.8745),
  [251] = Vertex.new(640, 356, -100, 170, 157, 170, 0.8745),
  [269] = Vertex.new(604, 400, 100, 170, 157, 170, 0.8745),
  [278] = Vertex.new(604, 400, 100, 170, 157, 170, 0.8745),
  [282] = Vertex.new(604, 400, 100, 170, 157, 170, 0.8745),
})
local deadSlave = Model.new(3111, {
  [2373] = Vertex.new(328, 1036, 3276, 89, 83, 68),
  [2376] = Vertex.new(340, 1016, 3308, 89, 83, 68),
  [2388] = Vertex.new(344, 988, 3308, 89, 83, 68),
  [2616] = Vertex.new(232, 1080, 3332, 89, 83, 68),
  [2660] = Vertex.new(216, 1092, 3332, 89, 83, 68),
})
local choppableTree = Model.new(531, {
  [82] = Vertex.new(68, 904, -44, 88, 95, 49),
  [86] = Vertex.new(96, 892, -52, 88, 95, 49),
  [119] = Vertex.new(136, 828, -28, 94, 101, 52),
  [252] = Vertex.new(76, 796, 152, 89, 83, 68),
  [321] = Vertex.new(76, 796, 152, 81, 75, 62),
})
local crystalTree = Model.new(429, {
  [12] = Vertex.new(76, 420, -12, 60, 56, 46),
  [36] = Vertex.new(68, 668, -8, 69, 65, 53),
  [46] = Vertex.new(8, 664, 0, 157, 157, 170),
  [54] = Vertex.new(-44, 416, -104, 157, 157, 170),
  [117] = Vertex.new(28, 536, -256, 81, 75, 62),
})
local ladder = Model.new(684, {
  [383] = Vertex.new(112, 700, -12, 72, 67, 55),
  [423] = Vertex.new(-112, 808, -12, 72, 67, 55),
  [437] = Vertex.new(112, 808, -12, 72, 67, 55),
  [477] = Vertex.new(-112, 932, -12, 72, 67, 55),
  [491] = Vertex.new(112, 932, -12, 72, 67, 55),
})
local ballista = Model.new(1128, {
  [11] = Vertex.new(3236, 7600, 2452, 138, 132, 106),
  [936] = Vertex.new(3532, 7720, 2348, 136, 127, 107),
  [941] = Vertex.new(3528, 7720, 2264, 136, 127, 107),
  [987] = Vertex.new(3520, 7536, 2340, 108, 101, 86),
  [1098] = Vertex.new(3516, 7536, 2272, 108, 101, 86),
})
local target = Model.new(2142, {
  [17] = Vertex.new(-24, 616, -220, 157, 54, 48),
  [23] = Vertex.new(24, 616, -220, 157, 54, 48),
  [45] = Vertex.new(-20, 616, -28, 61, 21, 18),
  [611] = Vertex.new(-20, 616, -28, 98, 91, 62),
  [791] = Vertex.new(16, 616, -28, 98, 91, 62),
})
local largeStoneDoor = Model.new(2583, {
  [1075] = Vertex.new(-460, 884, -212, 116, 105, 74),
  [1076] = Vertex.new(-392, 888, -248, 116, 105, 74),
  [1077] = Vertex.new(-412, 884, -212, 116, 105, 74),
  [1079] = Vertex.new(-472, 888, -248, 116, 105, 74),
  [1080] = Vertex.new(-392, 888, -248, 116, 105, 74),
})
--#endregion
--#region Items
local toadCrunchies = Model.new(243, {
  [214] = Vertex.new(72, 16, 44, 9, 96, 11),
  [217] = Vertex.new(52, 16, 76, 9, 96, 11),
  [218] = Vertex.new(68, 16, 60, 9, 96, 11),
  [219] = Vertex.new(52, 16, 60, 9, 96, 11),
  [224] = Vertex.new(40, 16, 72, 9, 96, 11),
})
local deathTalisman = Model.new(585, {
  [353] = Vertex.new(-84, 24, 28, 31, 28, 28),
  [485] = Vertex.new(-84, 0, 28, 31, 28, 28),
  [558] = Vertex.new(-24, 48, 36, 162, 149, 148),
  [569] = Vertex.new(-12, 48, 60, 162, 149, 148),
  [578] = Vertex.new(-8, 48, 52, 0, 0, 0),
})
--#endregion
--#region Quest Items
local tarnishedKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 69, 65, 53),
  [104] = Vertex.new(68, 16, 68, 69, 65, 53),
  [397] = Vertex.new(68, 16, 68, 69, 65, 53),
  [400] = Vertex.new(68, 16, 68, 69, 65, 53),
  [408] = Vertex.new(68, 16, 68, 69, 65, 53),
})
local newKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 153, 121, 13),
  [104] = Vertex.new(68, 16, 68, 153, 121, 13),
  [397] = Vertex.new(68, 16, 68, 153, 121, 13),
  [400] = Vertex.new(68, 16, 68, 153, 121, 13),
  [408] = Vertex.new(68, 16, 68, 153, 121, 13),
})
local crystalTrinket = Model.new(192, {
  [101] = Vertex.new(28, 60, 28, 222, 217, 217, 0.3686),
  [108] = Vertex.new(28, 60, -28, 222, 217, 217, 0.3686),
  [113] = Vertex.new(28, 60, -28, 222, 217, 217, 0.3686),
  [120] = Vertex.new(-28, 60, -28, 222, 217, 217, 0.3686),
  [125] = Vertex.new(-28, 60, -28, 222, 217, 217, 0.3686),
})
local colourWheel = Model.new(54, {
  [5] = Vertex.new(-20, 0, 40, 198, 196, 17),
  [9] = Vertex.new(-20, 0, 40, 198, 196, 17),
  [26] = Vertex.new(20, 0, 40, 21, 228, 236),
  [41] = Vertex.new(-20, 0, -40, 132, 11, 104),
  [44] = Vertex.new(20, 0, -40, 132, 11, 104),
})
local notes = Model.new(324, {
  [249] = Vertex.new(24, 4, 44, 69, 55, 14),
  [251] = Vertex.new(-16, 4, 60, 69, 55, 14),
  [255] = Vertex.new(60, 4, 20, 69, 55, 14),
  [263] = Vertex.new(-24, 8, 48, 69, 55, 14),
  [281] = Vertex.new(-36, 12, 36, 69, 55, 14),
})
local teleportCrystal = Model.new(60, {
  [46] = Vertex.new(-80, 44, -80, 140, 140, 153, 0.6863),
  [49] = Vertex.new(-80, 44, -80, 140, 140, 153, 0.6863),
  [52] = Vertex.new(-80, 44, -80, 140, 140, 153, 0.6863),
  [55] = Vertex.new(-80, 44, -80, 140, 140, 153, 0.6863),
  [58] = Vertex.new(-80, 44, -80, 140, 140, 153, 0.6863),
})
local report = Model.new(564, {
  [204] = Vertex.new(68, 52, -80, 103, 89, 65),
  [268] = Vertex.new(80, 68, 80, 103, 89, 65),
  [273] = Vertex.new(80, 68, 80, 103, 89, 65),
  [456] = Vertex.new(68, 52, -80, 162, 140, 102),
  [471] = Vertex.new(68, 52, 80, 162, 140, 102),
})
--#endregion

local bookTexture =
  "\x39\x1c\x0b\x00\x53\x25\x09\x25\x80\x4b\x23\xff\x6b\x2f\x0a\xff\x6b\x2f\x0a\xff\x73\x34\x0c\xff\x53\x20\x02\xff\x74\x3a\x12\xff\x63\x2d\x0a\xff\x36\x14\x02\xff\x49\x2a\x13\xff\xd6\x9b\x4c\xff\xd4\x8b\x2e\xff\xd6\x93\x3b\xff\xdb\x9b\x3c\xff\xd6\x93\x3b\xff\xdc\x9b\x43\xff\xdc\x9b\x43\xff\xda\xa3\x4b\xff\xdf\xab\x5a\xff\xea\xbc\x6b\xff\xec\xc4\x7b\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xeb\xc4\x83\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe3\xc3\x8b\xff\xe4\xbd\x8b\xff\xe4\xbd\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe6\xc3\x90\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xee\xd3\xa0\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xee\xd3\xa0\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf6\xdf\xb8\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe6\xca\x90\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xeb\xca\x8c\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xc3\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe3\xc3\x8b\xff\xe2\xc3\x84\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd2\xad\x6a\xff\xcb\xa4\x62\xff\xcb\xa4\x62\xff\xc7\x9c\x5a\xff\xc7\x9c\x5a\xff\xc3\x94\x52\xff\xc2\x8d\x4a\xff\xbc\x8b\x4b\xff\xb4\x81\x43\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xbb\x73\x34\xff\xbc\x82\x3c\xff\xc2\x8d\x4a\xff\xc2\x8d\x4a\xff\x99\x6a\x3b\xff\x7b\x43\x1a\xff\x2c\x1a\x0d\xff\x2c\x1a\x0d\xff\x7b\x43\x1a\xff\x99\x6a\x3b\xff\xc2\x8d\x4a\xff\xc2\x8d\x4a\xff\xbc\x82\x3c\xff\xbb\x73\x34\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb4\x81\x43\xff\xbc\x8b\x4b\xff\xc2\x8d\x4a\xff\xc3\x94\x52\xff\xc7\x9c\x5a\xff\xc7\x9c\x5a\xff\xcb\xa4\x62\xff\xcb\xa4\x62\xff\xd2\xad\x6a\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xe2\xc3\x84\xff\xe3\xc3\x8b\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xc3\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xca\x8c\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xe6\xca\x90\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf6\xdf\xb8\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xee\xd3\xa0\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xee\xd3\xa0\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xe6\xc3\x90\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe4\xbd\x8b\xff\xe4\xbd\x8b\xff\xe3\xc3\x8b\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xeb\xc4\x83\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xec\xc4\x7b\xff\xea\xbc\x6b\xff\xdf\xab\x5a\xff\xda\xa3\x4b\xff\xdc\x9b\x43\xff\xdc\x9b\x43\xff\xd6\x93\x3b\xff\xdb\x9b\x3c\xff\xd6\x93\x3b\xff\xd4\x8b\x2e\xff\xd6\x9b\x4c\xff\x49\x2a\x13\xff\x36\x14\x02\xff\x63\x2d\x0a\xff\x74\x3a\x12\xff\x53\x20\x02\xff\x73\x34\x0c\xff\x6b\x2f\x0a\xff\x6b\x2f\x0a\xff\x80\x4b\x23\xff\x53\x25\x09\x25\x39\x1c\x0b\x00"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Arianwyn in Lletya.",
    title = "Starting out",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2352, 2069, 3172) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["arianwyn"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["arianwyn"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Arianwyn.",
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    postconditions = { Condition.ConversationText:new("secure in my absence") },
  },
  {
    text = "Enter the Mourners' headquarters in the north-eastern corner of West Ardougne.",
    title = "Trouble in the temple",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {
      ["Mourner outfit"] = { quantity = 1 },
      ["New key"] = { quantity = 1 },
      ["Tarnished key"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2551, 1785, 3320.4) },
    postconditions = {
      Condition.DistanceTo:new(2551, 1285, 3322, 1),
      Condition.DistanceTo:new(2045, 7973, 4629, 20),
    },
  },
  {
    text = "Descend the trapdoor.",
    actions = { Action.Direction:new(2542, 1285, 3327, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2045, 7973, 4629, 20) },
  },
  {
    text = "Search the Head Mourner's desk for the tarnished and new key.",
    actions = {
      Action.Direction:new(2043, 8373, 4629.5),
      Action.ConversationHighlight:new("Take a tarnished key."),
    },
    postconditions = { Condition.InventoryContains:new(tarnishedKey) },
  },
  {
    actions = {
      Action.Direction:new(2043, 8373, 4629.5),
      Action.ConversationHighlight:new("Take a new key."),
    },
    postconditions = { Condition.InventoryContains:new(newKey) },
  },
  {
    text = "Enter the tunnels.",
    actions = { Action.Direction:new(2033.5, 8473, 4636) },
    postconditions = { Condition.DistanceTo:new(2031, 7973, 4636, 2) },
  },
  {
    text = "Remove your gas mask.",
    warning = "No tracking for this step.",
  },
  {
    text = "Chant-to the blank teleport crystal on the marked tile.",
    actions = {
      Action.PathGuide:new({
        Location:new(2032, 7973, 4636),
        Location:new(2026, 7135, 4636),
        Location:new(2018, 6319, 4636),
        Location:new(2012, 5895, 4638),
        Location:new(2003, 6021, 4638),
        Location:new(1997, 5764, 4632),
        Location:new(1991, 5125, 4626),
        Location:new(1985, 4805, 4626),
        Location:new(1974, 3589, 4627),
        Location:new(1969, 3613, 4631),
        Location:new(1962, 3229, 4634),
        Location:new(1953, 3373, 4634),
        Location:new(1946, 3197, 4636),
        Location:new(1946, 3197, 4636),
        Location:new(1941, 2925, 4638),
        Location:new(1937, 2197, 4641),
      }),
      Action.Direction:new(1937, 2197, 4641, { tile = true }),
      Action.InventoryHighlight:new(teleportCrystal),
    },
    postconditions = { Condition.ConversationText:new("you hear that") },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("enchanting") } },
  {
    text = "Equip the gas mask.",
    warning = "No tracking for this step.",
    actions = { Action.InventoryHighlight:new(Models.items["gas mask"]) },
  },
  {
    text = "Return to the Mourners' headquarters basement.",
    actions = {
      Action.PathGuide:new({
        Location:new(2032, 7973, 4636),
        Location:new(2026, 7135, 4636),
        Location:new(2018, 6319, 4636),
        Location:new(2012, 5895, 4638),
        Location:new(2003, 6021, 4638),
        Location:new(1997, 5764, 4632),
        Location:new(1991, 5125, 4626),
        Location:new(1985, 4805, 4626),
        Location:new(1974, 3589, 4627),
        Location:new(1969, 3613, 4631),
        Location:new(1962, 3229, 4634),
        Location:new(1953, 3373, 4634),
        Location:new(1946, 3197, 4636),
        Location:new(1946, 3197, 4636),
        Location:new(1941, 2925, 4638),
        Location:new(1937, 2197, 4641),
        Location:new(2035, 7973, 4636),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2035, 7973, 4636, 1) },
  },
  {
    text = "Talk to the gnome.",
    actions = {
      Action.ModelHighlight:new(gnome),
      Action.ConversationHighlight:new("Talk about Within the Light."),
      Action.ConversationHighlight:new("Fine, have them."),
      Action.ConversationHighlight:new("Stay undercover."),
    },
    postconditions = { Condition.ConversationText:new("your word") }, --not tested
  },
  {
    text = "Kill a mourner nearby for a second mourner outfit.",
    warning = "No tracking for this step.",
    actions = { Action.ModelHighlight:new(Models.npcs["mourner"], { highlightPriority = "all" }) },
  },
  {
    text = "Use a piece of mourner's outfit on Arianwyn in Lletya.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    actions = { Action.Direction:new(2352, 2069, 3172) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["arianwyn"]),
      Condition.ConversationText:new("the part"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.InventoryHighlight:new(Models.items["gas mask"]),
      Action.ConversationHighlight:new("take it now"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["arianwyn"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("the part") },
  },
  {
    text = "Use the enchanted crystal on Arianwyn.",
    actions = {
      Action.ModelHighlight:new(arianwynMourner),
      Action.InventoryHighlight:new(teleportCrystal),
    },
    postconditions = { Condition.ConversationText:new("Nice job") },
  },
  {
    text = "Talk to Arianwyn again.<ul><li>Dismiss your pet/familiar.</li><li>Make sure to have your second set of mourner gear equipped or in your inventory.</li></ul>",
    actions = {
      Action.ModelHighlight:new(arianwynMourner),
      Action.ConversationHighlight:new("About those tasks I need to do..."),
      Action.ConversationHighlight:new("I'm ready, let's go."),
    },
    postconditions = { Condition.ConversationText:new("with you") },
  },
  {
    text = "Climb up the staircase.<ul><li>Equip your tank gear/shield.</li></ul>", --entrance
    title = "The death altar",
    neededItems = {
      ["Crystal trinket"] = { quantity = 1 },
      ["Death talisman/wicked hood"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(1903.5, 405, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1901, 997, 4638, 4) },
  },
  {
    text = "Climb up the staircase to the south.",
    actions = { Action.Direction:new(1894, 1497, 4620) },
    postconditions = { Condition.DistanceToWithHeight:new(1892, 1989, 4620, 4) },
  },
  {
    text = "Climb down the staircase to the north.",
    actions = { Action.Direction:new(1891, 1989, 4636) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 997, 4638, 4) },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(1888, 997, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1886, 5, 4639, 4) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1883, 5, 4639, 1) },
  },
  {
    text = "Rotate the mirror west.",
    actions = {
      Action.ModelHighlight:new(lightPillar),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(redLightBeam, { atLocation = Location:new(1875, 605, 4639) }) },
  },
  {
    text = "Pass through the light door to the death altar.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.DistanceTo:new(1863, 125, 4639, 1) },
  },
  {
    text = "Talk to Thorgel.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["thorgel"]),
      Action.ConversationHighlight:new("Within the Light."),
    },
    postconditions = { Condition.ConversationText:new("with him first") },
  },
  {
    text = "Talk to Arianwyn.",
    actions = { Action.ModelHighlight:new(arianwynMournerNoMask) },
    postconditions = { Condition.ConversationText:new("get right on it") },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.DistanceTo:new(1867, 5, 4639, 1) },
  },
  {
    text = "Investigate the energised pillar.",
    actions = { Action.ModelHighlight:new(energizedPillar) },
    postconditions = { Condition.ConversationText:new("Temple of Light") },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.DistanceTo:new(1863, 125, 4639, 1) },
  },
  {
    text = "Talk to Arianwyn.",
    actions = { Action.ModelHighlight:new(arianwynMournerNoMask) },
    postconditions = { Condition.ConversationText:new("really capable of") },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.DistanceTo:new(1867, 5, 4639, 1) },
  },
  {
    text = "Use the crystal trinket on the energised pillar.",
    warning = "Epilepsy warning.",
    actions = {
      Action.ModelHighlight:new(energizedPillar),
      Action.InventoryHighlight:new(crystalTrinket),
    },
    postconditions = { Condition.ConversationText:new("what happened") },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.DistanceTo:new(1863, 125, 4639, 1) },
  },
  {
    text = "Talk to Arianwyn.<ul><li>Speak to Arianwyn again if you need a death talisman.</li></ul>",
    actions = {
      Action.ModelHighlight:new(arianwynMournerNoMask),
      Action.ConversationHighlight:new("I'm not even going to argue."),
    },
    postconditions = { Condition.ConversationText:new("you at the altar") },
  },
  {
    actions = { Action.ModelHighlight:new(arianwynMournerNoMask) },
    postconditions = { Condition.InventoryContains:new(deathTalisman) },
  },
  {
    text = "Enter the death ruins.",
    actions = { Action.ModelHighlight:new(deathRuins) },
    postconditions = { Condition.DistanceTo:new(2208, 2373, 4829, 4) },
  },
  {
    text = "Talk to Thorgel. <b>Do the next steps quickly</b>.",
    title = "Energized",
    actions = {
      Action.ModelHighlight:new(Models.npcs["thorgel"]),
      Action.ConversationHighlight:new("Within the Light."),
      Action.ConversationHighlight:new("Okay, let's do this."),
    },
    postconditions = { Condition.ConversationText:new("before it wears off") },
  },
  {
    text = "Exit the altar.",
    actions = { Action.ModelHighlight:new(deathAltarPortal) },
    postconditions = { Condition.DistanceTo:new(1863, 125, 4639, 4) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.DistanceTo:new(1867, 5, 4639, 1) },
  },
  {
    text = "Rotate the mirror east.",
    actions = {
      Action.ModelHighlight:new(lightPillar),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(openLightDoor, { atLocation = Location:new(1885, 5, 4639) }) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1887, 5, 4639, 1) },
  },
  {
    text = "Go up the stairs.",
    actions = { Action.Direction:new(1888, 605, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1890, 997, 4639, 4) },
  },
  {
    text = "Go up the northern stairs.",
    actions = { Action.Direction:new(1891, 1697, 4643) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 1989, 4644, 4) },
  },
  {
    text = "Investigate the crystal to the south-east.<ul><li>If your character speaks, you have to start from the beginning.</li></ul>",
    actions = { Action.Direction:new(1908.5, 2489, 4639) },
    postconditions = { Condition.ModelVisible:new(crystalObj) },
  },
  {
    actions = { Action.ModelHighlight:new(crystalObj) },
    jumpconditions = { Condition.ModelNotVisible:new(crystalObj) },
    jumpOffset = -1,
    postconditions = { Condition.ChatText:new("the energy within you") },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(1891, 1989, 4642) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 997, 4640, 4) },
  },
  {
    text = "Climb down the stairs again.",
    actions = { Action.Direction:new(1888, 997, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1886, 5, 4639, 4) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1883, 5, 4639, 1) },
  },
  {
    text = "Touch the energized pillar.<ul><li>If you don't get transported, you were not fast enough.</li></ul>",
    actions = { Action.ModelHighlight:new(energizedPillar) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene. Don't click anywhere in the cutscene.",
    postconditions = { Condition.ChatText:new("Elven Seed") },
  },
  {
    text = "Search the dead slave.",
    title = "Within the light itself",
    actions = { Action.ModelHighlight:new(deadSlave, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(colourWheel) },
  },
  {
    actions = { Action.ModelHighlight:new(deadSlave, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(notes) },
  },
  {
    text = "Chop the tree.",
    actions = { Action.ModelHighlight:new(choppableTree, { instance = true }) },
    postconditions = { Condition.ConversationText:new("it like the others") },
  },
  {
    text = "Fix the crystal tree.",
    actions = { Action.ModelHighlight:new(crystalTree, { instanced = true, atLocation = Location:new(9, 500, 6) }) },
    postconditions = {
      Condition.ModelVisible:new(blueLightBeam, { instance = true, atLocation = Location:new(7, 600, 6) }),
    },
  },
  {
    text = "The solution to this puzzle differs player to player. Refer to the wiki for an explanation on how to solve this. Sorry.",
    postconditions = { Condition.ModelVisible:new(ladder, { instance = true }) },
  },
  {
    text = "Climb the ladder to the south-east.",
    actions = { Action.ModelHighlight:new(ladder, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  { -- Not tested. Can't reach 0,0,0 again.
    text = "Follow the path while avoiding the spikes.",
    title = "Sharpshooter",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 3),
        Location:new(0, 0, 5),
        Location:new(0, 0, 5),
        Location:new(0, 0, 7),
        Location:new(0, 0, 9),
        Location:new(0, 0, 11),
        Location:new(0, 0, 13),
        Location:new(0, 0, 17),
        Location:new(-2, 0, 17),
        Location:new(-4, 0, 17),
        Location:new(-6, 0, 17),
        Location:new(-8, 0, 17),
        Location:new(-10, 0, 17),
        Location:new(-10, 0, 15),
        Location:new(-8, 0, 15),
        Location:new(-6, 0, 15),
        Location:new(-4, 0, 15),
        Location:new(-4, 0, 13),
        Location:new(-4, 0, 11),
        Location:new(-4, 0, 9),
        Location:new(-4, 0, 7),
        Location:new(-4, 0, 5),
        Location:new(-4, 0, 2),
        Location:new(-6, 0, 2),
        Location:new(-6, 0, 3),
        Location:new(-8, 0, 3),
        Location:new(-8, 0, 2),
        Location:new(-10, 0, 2),
        Location:new(-10, 0, 3),
        Location:new(-11, 0, 3),
        Location:new(-11, 0, 5),
        Location:new(-10, 0, 5),
        Location:new(-8, 0, 5),
        Location:new(-8, 0, 7),
        Location:new(-8, 0, 9),
        Location:new(-10, 0, 9),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-11, 0, 10, 2, true) },
  },
  {
    text = "Shoot the ballista to hit the target.",
    actions = {
      Action.ModelHighlight:new(ballista),
      Action.ModelHighlight:new(target),
    },
    postconditions = { Condition.ChatText:new("hit the target") },
  },
  {
    text = "Jump to the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 1),
        Location:new(0, 0, 3),
        Location:new(0, 0, 5),
        Location:new(0, 0, 5),
        Location:new(0, 0, 7),
        Location:new(0, 0, 9),
        Location:new(0, 0, 11),
        Location:new(0, 0, 13),
        Location:new(0, 0, 17),
        Location:new(-2, 0, 17),
        Location:new(-4, 0, 17),
        Location:new(-6, 0, 17),
        Location:new(-8, 0, 17),
        Location:new(-10, 0, 17),
        Location:new(-10, 0, 15),
        Location:new(-8, 0, 15),
        Location:new(-6, 0, 15),
        Location:new(-4, 0, 15),
        Location:new(-4, 0, 13),
        Location:new(-4, 0, 11),
        Location:new(-4, 0, 9),
        Location:new(-4, 0, 7),
        Location:new(-4, 0, 5),
        Location:new(-4, 0, 2),
        Location:new(-6, 0, 2),
        Location:new(-6, 0, 3),
        Location:new(-8, 0, 3),
        Location:new(-8, 0, 2),
        Location:new(-10, 0, 2),
        Location:new(-10, 0, 3),
        Location:new(-11, 0, 3),
        Location:new(-11, 0, 5),
        Location:new(-10, 0, 5),
        Location:new(-8, 0, 5),
        Location:new(-8, 0, 7),
        Location:new(-8, 0, 9),
        Location:new(-10, 0, 9),
      }, { instance = true }),
      Action.Direction:new(0, 400, -3, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Go-through the large stone door.",
    title = "The passage to Prifddinas",
    actions = { Action.ModelHighlight:new(largeStoneDoor) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the Elf messenger.",
    actions = { Action.Direction:new(-12, 100, -0.5, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(report) },
  },
  {
    text = "Read the report.",
    actions = { Action.InventoryHighlight:new(report) },
    postconditions = { Condition.Generic2DVisible:new(482, 290, 135, bookTexture) },
  },
  {
    text = "Return to Arianwyn in Lletya.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2352, 2069, 3172) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["arianwyn"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["arianwyn"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Within the Light",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.long,
  releaseDate = 1255392000,
  prereqQuests = { "Mourning's End Part II" },
  questReqs = {
    Types.QuestReq.skill("Agility", 69),
    Types.QuestReq.skill("Fletching", 70),
    Types.QuestReq.skill("Ranged", 75),
    Types.QuestReq.skill("Woodcutting", 75),
  },
  neededItems = {
    ["Mourner gear"] = { quantity = 2, model = Models.items["gas mask"], duringQuest = true },
    ["Death talisman"] = { quantity = 1, model = deathTalisman, duringQuest = true },
    ["New key"] = { quantity = 1, model = newKey, duringQuest = true },
    ["Tarnished key"] = { quantity = 1, model = tarnishedKey, duringQuest = true },
    ["Notes"] = { quantity = 1, model = notes, duringQuest = true },
    ["Colour wheel"] = { quantity = 1, model = colourWheel, duringQuest = true },
    ["Crystal trinket"] = { quantity = 1, model = crystalTrinket, duringQuest = true },
    ["Toad crunchies"] = { quantity = 1, model = toadCrunchies },
  },
  recommendedItems = {
    ["Food"] = { quantity = 1 },
    ["Prayer potions"] = { quantity = 1 },
    ["Crystal teleport seed"] = { quantity = 1 },
    ["Dave's spellbook"] = { quantity = 1 },
    ["Weapon"] = { quantity = 1 },
    ["Tank gear"] = { quantity = 1 },
    ["Shield"] = { quantity = 1 },
    ["Wicked hood"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Shadows"] = { level = "95", optional = true, quantity = 1 },
    ["Mourner"] = { level = "79, 84, 86", optional = true, quantity = 1 },
  },
})
