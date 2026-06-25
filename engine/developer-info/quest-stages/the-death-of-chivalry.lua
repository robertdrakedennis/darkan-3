local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local sirOwen = Model.new(3729, {
  [1852] = Vertex.new(88, 696, 82, 94, 102, 102),
  [1856] = Vertex.new(106, 733, 22, 94, 102, 102),
  [1964] = Vertex.new(-108, 702, 33, 140, 152, 152),
  [1972] = Vertex.new(-89, 696, 82, 94, 102, 102),
  [1977] = Vertex.new(-107, 733, 22, 94, 102, 102),
})
local saradomin = Model.new(17868, {
  [3527] = Vertex.new(80, 597, -63, 127, 127, 127),
  [13812] = Vertex.new(46, 593, -90, 127, 127, 127),
  [17851] = Vertex.new(0, 914, 9, 52, 139, 27),
  [17852] = Vertex.new(0, 911, 9, 52, 139, 27),
  [17853] = Vertex.new(0, 911, 7, 52, 139, 27),
})
local lieutenantYork = Model.new(5463, {
  [1944] = Vertex.new(39, 608, -68, 83, 76, 83),
  [1946] = Vertex.new(-39, 608, -68, 83, 76, 83),
  [3799] = Vertex.new(0, 735, -7, 27, 138, 126),
  [3800] = Vertex.new(0, 732, -7, 27, 138, 126),
  [3801] = Vertex.new(0, 732, -8, 27, 138, 126),
})
local fortressGuard = Model.new(6756, {
  [826] = Vertex.new(-2, 725, -59, 106, 77, 54),
  [834] = Vertex.new(2, 725, -59, 106, 77, 54),
  [3387] = Vertex.new(170, 515, -181, 103, 80, 52),
  [6240] = Vertex.new(-65, 670, 50, 0, 0, 0),
  [6271] = Vertex.new(65, 670, 50, 0, 0, 0),
})
local fortressGuard2 = Model.new(6408, {
  [4865] = Vertex.new(-131, 354, -382, 147, 114, 75),
  [4889] = Vertex.new(-131, 354, -370, 147, 114, 75),
  [4955] = Vertex.new(-113, 354, -382, 147, 114, 75),
  [5079] = Vertex.new(-127, 367, -324, 112, 87, 57),
  [5109] = Vertex.new(-128, 366, -324, 112, 87, 57),
})
local hierophantMarius = Model.new(4812, {
  [2356] = Vertex.new(-2, 725, -59, 93, 71, 59),
  [2361] = Vertex.new(-7, 724, -51, 93, 71, 59),
  [2392] = Vertex.new(2, 725, -59, 93, 71, 59),
  [2396] = Vertex.new(7, 724, -51, 93, 71, 59),
  [3973] = Vertex.new(0, 735, -7, 165, 26, 14),
})
local lieutenantGraves = Model.new(6330, {
  [2447] = Vertex.new(-153, 641, 29, 179, 125, 53),
  [2487] = Vertex.new(153, 641, 29, 179, 125, 53),
  [4586] = Vertex.new(31, 764, -30, 147, 102, 44),
  [4605] = Vertex.new(-31, 764, -30, 147, 102, 44),
  [4609] = Vertex.new(-32, 754, -31, 159, 111, 47),
})
local blackKnight = Model.any({
  Model.new(6459, {
    [827] = Vertex.new(-132, 379, -324, 13, 12, 13),
    [836] = Vertex.new(-132, 379, -326, 13, 12, 13),
    [860] = Vertex.new(-118, 379, -326, 13, 12, 13),
    [884] = Vertex.new(-132, 341, -326, 13, 12, 13),
    [908] = Vertex.new(-118, 341, -326, 13, 12, 13),
  }),
  Model.new(5463, {
    [1944] = Vertex.new(39, 608, -68, 83, 76, 83),
    [1946] = Vertex.new(-39, 608, -68, 83, 76, 83),
    [3799] = Vertex.new(0, 735, -7, 27, 139, 126),
    [3800] = Vertex.new(0, 732, -7, 27, 139, 126),
    [3801] = Vertex.new(0, 732, -8, 27, 139, 126),
  }),
})
local dawn = Model.new(6873, {
  [5776] = Vertex.new(-8, 788, 66, 9, 6, 6),
  [5781] = Vertex.new(0, 789, 69, 9, 6, 6),
  [5884] = Vertex.new(10, 777, 72, 9, 6, 6),
  [5947] = Vertex.new(-10, 777, 72, 9, 6, 6),
  [6689] = Vertex.new(-27, 772, 56, 9, 6, 6),
})
local fern = Model.new(1659, {
  [8] = Vertex.new(58, 846, -211, 132, 171, 172),
  [33] = Vertex.new(-57, 844, -212, 132, 171, 172),
  [111] = Vertex.new(45, 1021, -113, 134, 146, 146),
  [147] = Vertex.new(-45, 1021, -113, 134, 146, 146),
  [395] = Vertex.new(36, 876, -180, 134, 146, 146),
})
local dawnArmour = Model.new(7509, {
  [3257] = Vertex.new(127, 415, -19, 26, 24, 26),
  [3266] = Vertex.new(127, 415, -19, 26, 24, 26),
  [3311] = Vertex.new(-127, 415, -19, 26, 24, 26),
  [7472] = Vertex.new(128, 388, -25, 162, 113, 49),
  [7491] = Vertex.new(-128, 388, -25, 162, 113, 49),
})
--#endregion
--#region Objects
local suitOfArmour = Model.new(5160, {
  [1928] = Vertex.new(-32, 822, -36, 46, 32, 14),
  [1940] = Vertex.new(32, 822, -36, 46, 32, 14),
  [2669] = Vertex.new(128, 706, 51, 26, 24, 26),
  [3326] = Vertex.new(144, 686, 46, 43, 39, 43),
  [3332] = Vertex.new(162, 675, 40, 43, 39, 43),
})
local grimoireStand = Model.new(1005, {
  [19] = Vertex.new(-155, 653, 63, 32, 17, 16),
  [20] = Vertex.new(-144, 643, 56, 32, 17, 16),
  [21] = Vertex.new(-144, 487, -129, 32, 17, 16),
  [462] = Vertex.new(32, 584, 156, 21, 22, 20),
  [474] = Vertex.new(-32, 360, -175, 21, 22, 20),
})
local candleStand = Model.new(1872, {
  [1593] = Vertex.new(4, 570, -3, 0, 0, 0),
  [1595] = Vertex.new(4, 557, 4, 0, 0, 0),
  [1599] = Vertex.new(3, 557, -4, 0, 0, 0),
  [1601] = Vertex.new(3, 570, 2, 0, 0, 0),
  [1851] = Vertex.new(-24, 508, -1, 29, 12, 11),
})
local candleStandWithCandle = Model.new(1602, {
  [5] = Vertex.new(2, 639, 2, 36, 34, 33),
  [8] = Vertex.new(0, 646, 0, 36, 34, 33),
  [13] = Vertex.new(-1, 646, 1, 36, 34, 33),
  [19] = Vertex.new(-1, 646, 1, 36, 34, 33),
  [22] = Vertex.new(1, 639, 3, 36, 34, 33),
})
local chalkCircle = Model.new(1029, {
  [14] = Vertex.new(476, 55, 535, 73, 67, 67, 0.8039),
  [57] = Vertex.new(498, 55, -515, 73, 67, 67, 0.8039),
  [78] = Vertex.new(-467, 55, -586, 73, 67, 67, 0.8039),
  [81] = Vertex.new(-671, 55, -379, 73, 67, 67, 0.8039),
  [340] = Vertex.new(658, 55, -379, 73, 67, 67, 0.8039),
})
local litCandleStand = Model.new(1590, {
  [2] = Vertex.new(0, 646, 0, 36, 34, 33),
  [5] = Vertex.new(2, 639, 2, 36, 34, 33),
  [8] = Vertex.new(0, 646, 0, 36, 34, 33),
  [1316] = Vertex.new(-16, 508, 0, 64, 47, 26),
  [1400] = Vertex.new(16, 508, 0, 64, 47, 26),
})
local repairedCircle = Model.new(1161, {
  [14] = Vertex.new(476, 55, 535, 73, 67, 67, 0.8039),
  [309] = Vertex.new(498, 55, -515, 73, 67, 67, 0.8039),
  [330] = Vertex.new(-467, 55, -586, 73, 67, 67, 0.8039),
  [333] = Vertex.new(-671, 55, -379, 73, 67, 67, 0.8039),
  [652] = Vertex.new(658, 55, -379, 73, 67, 67, 0.8039),
})
local doorway = Model.new(90, {
  [14] = Vertex.new(2594, 2379, 3999, 34, 36, 37, 0.8039),
  [69] = Vertex.new(2903, 755, 4278, 112, 121, 123, 0.6078),
  [71] = Vertex.new(2903, 1026, 4008, 112, 121, 123, 0.6078),
  [75] = Vertex.new(2903, 1041, 4009, 112, 121, 123, 0.6078),
  [77] = Vertex.new(2903, 1311, 4280, 112, 121, 123, 0.6078),
})
local pileOfBones = Model.new(354, {
  [1] = Vertex.new(622, 7, -622, 158, 152, 144),
  [3] = Vertex.new(622, 7, 620, 158, 152, 144),
  [5] = Vertex.new(-621, 7, -621, 158, 152, 144),
})
local lootedCoffin = Model.new(5556, {
  [3200] = Vertex.new(2926, 2736, 1070, 71, 76, 70),
  [3203] = Vertex.new(2934, 2775, 1066, 101, 108, 99),
  [3206] = Vertex.new(2938, 2788, 1035, 62, 66, 60),
  [3209] = Vertex.new(2938, 2786, 1009, 62, 66, 60),
  [3260] = Vertex.new(3424, 2359, 1009, 97, 104, 95),
})
local graveyardPortcullis = Model.new(23361, {
  [5004] = Vertex.new(4877, 1240, 3998, 78, 86, 79),
  [5156] = Vertex.new(4877, 757, 3998, 60, 66, 61),
  [7853] = Vertex.new(4436, 1566, 3584, 40, 43, 40),
  [11951] = Vertex.new(4687, 1619, 3688, 68, 44, 35),
  [11967] = Vertex.new(4928, 1307, 3933, 82, 52, 42),
})
local graveyardPortcullisOpen = Model.new(8622, {
  [2147] = Vertex.new(-670, 1176, -525, 78, 86, 79),
  [8459] = Vertex.new(-330, 1605, 245, 66, 74, 129),
  [8579] = Vertex.new(-360, 1555, 335, 68, 44, 35),
  [8583] = Vertex.new(-605, 1282, -576, 82, 52, 42),
  [8595] = Vertex.new(-605, 1243, 576, 82, 52, 42),
})
local bunnyTile = Model.new(276, {
  [13] = Vertex.new(988, 269, 1064, 131, 140, 128),
  [37] = Vertex.new(982, 269, 1495, 131, 140, 128),
  [40] = Vertex.new(982, 269, 1495, 131, 140, 128),
  [80] = Vertex.new(982, 215, 1495, 131, 140, 128),
  [228] = Vertex.new(982, 269, 1495, 167, 167, 167),
})
local bunnyTile2 = Model.new(276, {
  [13] = Vertex.new(1500, 269, 3112, 131, 140, 128),
  [37] = Vertex.new(1494, 269, 3543, 131, 140, 128),
  [40] = Vertex.new(1494, 269, 3543, 131, 140, 128),
  [80] = Vertex.new(1494, 215, 3543, 131, 140, 128),
  [228] = Vertex.new(1494, 269, 3543, 167, 167, 167),
})
local wandBarrier = Model.new(591, {
  [529] = Vertex.new(4, 1006, -9, 118, 203, 206, 0.000),
  [530] = Vertex.new(12, 1006, 7, 118, 203, 206, 0.000),
  [531] = Vertex.new(-3, 1006, 4, 118, 203, 206, 0.000),
})
local wandBarrier2 = Model.new(543, {
  [505] = Vertex.new(4, 1006, -9, 117, 202, 206, 0.000),
  [506] = Vertex.new(12, 1006, 7, 117, 202, 206, 0.000),
  [507] = Vertex.new(-3, 1006, 4, 117, 202, 206, 0.000),
})
local wandChamberPortcullis = Model.new(8562, {
  [2147] = Vertex.new(-670, 1176, -525, 78, 86, 79),
  [8471] = Vertex.new(-152, 1555, -335, 68, 44, 35),
  [8519] = Vertex.new(-360, 1555, 335, 68, 44, 35),
  [8523] = Vertex.new(-605, 1282, -576, 82, 52, 42),
  [8535] = Vertex.new(-605, 1243, 576, 82, 52, 42),
})
local wandChamberPortcullisOpen = Model.new(8622, {
  [2766] = Vertex.new(4877, 4824, 3998, 78, 85, 79),
  [8453] = Vertex.new(4597, 5091, 3658, 65, 73, 128),
  [8459] = Vertex.new(4597, 5253, 3658, 65, 73, 128),
  [8579] = Vertex.new(4687, 5203, 3688, 68, 43, 35),
  [8595] = Vertex.new(4928, 4891, 3933, 81, 52, 41),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local captainsHelm = Model.new(1554, {
  [578] = Vertex.new(91, 68, 57, 67, 13, 17),
  [992] = Vertex.new(-29, 52, -31, 68, 47, 20),
  [1004] = Vertex.new(29, 52, -31, 68, 47, 20),
  [1125] = Vertex.new(-20, 10, -44, 102, 71, 30),
  [1151] = Vertex.new(20, 10, -44, 102, 71, 30),
})
local captainsCuriass = Model.multi({
  Model.new(1341, {
    [720] = Vertex.new(115, 19, 100, 40, 36, 40),
    [850] = Vertex.new(155, 11, 66, 65, 60, 65),
    [855] = Vertex.new(147, 15, 71, 65, 60, 65),
    [856] = Vertex.new(170, 0, 71, 65, 60, 65),
    [888] = Vertex.new(-134, 0, 116, 85, 78, 83),
  }),
  Model.new(36, {
    [9] = Vertex.new(48, 35, 107, 185, 136, 36),
    [11] = Vertex.new(52, 41, 90, 148, 109, 29),
    [21] = Vertex.new(30, 51, 92, 188, 16, 30),
    [27] = Vertex.new(48, 36, 108, 188, 16, 30),
    [29] = Vertex.new(52, 42, 91, 188, 16, 30),
  }),
})
local captainsGown = Model.multi({
  Model.new(900, {
    [164] = Vertex.new(65, 1, 213, 26, 5, 6),
    [401] = Vertex.new(35, 33, 205, 152, 13, 24),
    [407] = Vertex.new(34, 41, 131, 178, 15, 28),
    [449] = Vertex.new(-34, 33, 205, 152, 13, 24),
    [453] = Vertex.new(-33, 41, 131, 178, 15, 28),
  }),
  Model.new(12, {
    [3] = Vertex.new(51, 29, 209, 164, 115, 49),
    [5] = Vertex.new(64, 24, 184, 164, 115, 49),
    [7] = Vertex.new(-51, 29, 209, 164, 115, 49),
    [9] = Vertex.new(-62, 21, 205, 164, 115, 49),
    [11] = Vertex.new(-53, 32, 188, 164, 115, 49),
  }),
})
local captainsGauntlets = Model.new(708, {
  [14] = Vertex.new(-22, 11, 71, 26, 24, 26),
  [30] = Vertex.new(-22, 11, -69, 26, 24, 26),
  [31] = Vertex.new(-22, 11, 71, 26, 24, 26),
  [34] = Vertex.new(-22, 11, 71, 26, 24, 26),
  [44] = Vertex.new(-22, 11, -69, 26, 24, 26),
})
local captainsBoots = Model.new(570, {
  [3] = Vertex.new(46, 26, -50, 26, 24, 26),
  [4] = Vertex.new(55, 31, -35, 26, 24, 26),
  [7] = Vertex.new(46, 26, -50, 26, 24, 26),
  [29] = Vertex.new(-46, 26, -50, 26, 24, 26),
  [34] = Vertex.new(-46, 26, -50, 26, 24, 26),
})
local letterFromDaquarius = Model.new(138, {
  [7] = Vertex.new(-100, 16, -56, 130, 118, 99),
  [10] = Vertex.new(-100, 16, -56, 130, 118, 99),
  [16] = Vertex.new(100, 24, -56, 130, 118, 99),
  [46] = Vertex.new(36, 36, 4, 139, 126, 106),
  [67] = Vertex.new(-100, 16, 56, 139, 126, 106),
})
local redCandle = Model.new(192, {
  [22] = Vertex.new(107, 26, 7, 144, 140, 131),
  [96] = Vertex.new(-116, 20, 30, 34, 15, 13),
  [120] = Vertex.new(-116, 20, 30, 44, 19, 17),
  [121] = Vertex.new(-116, 20, 30, 44, 19, 17),
  [124] = Vertex.new(-116, 20, 30, 44, 19, 17),
})
local chalk = Model.new(42, {
  [8] = Vertex.new(28, 7, 41, 118, 116, 108),
  [22] = Vertex.new(28, 7, 41, 114, 112, 104),
  [25] = Vertex.new(-20, 14, -39, 142, 139, 130),
  [31] = Vertex.new(28, 7, 41, 142, 139, 130),
  [41] = Vertex.new(28, 7, 41, 130, 128, 119),
})
local grimoire = Model.multi({
  Model.new(318, {
    [243] = Vertex.new(63, 42, 80, 128, 101, 80),
    [299] = Vertex.new(68, 24, -94, 128, 101, 80),
    [300] = Vertex.new(-16, 29, -92, 128, 101, 80),
    [305] = Vertex.new(68, 24, -94, 149, 118, 94),
    [318] = Vertex.new(28, 26, 91, 149, 118, 94),
  }),
  Model.new(6, {
    [1] = Vertex.new(56, 62, -24, 14, 3, 1),
    [2] = Vertex.new(-44, 62, -84, 14, 3, 1),
    [3] = Vertex.new(-44, 62, -24, 14, 3, 1),
    [5] = Vertex.new(56, 62, -84, 14, 3, 1),
    [6] = Vertex.new(-44, 62, -84, 14, 3, 1),
  }),
})
local smallMetalKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 107, 79, 43),
  [104] = Vertex.new(68, 16, 68, 107, 79, 43),
  [397] = Vertex.new(68, 16, 68, 107, 79, 43),
  [400] = Vertex.new(68, 16, 68, 107, 79, 43),
  [408] = Vertex.new(68, 16, 68, 107, 79, 43),
})
local wandOfRes = Model.new(684, {
  [65] = Vertex.new(18, 1, 175, 61, 42, 31),
  [340] = Vertex.new(-8, 26, 204, 61, 42, 31),
  [367] = Vertex.new(18, 1, 175, 82, 56, 42),
  [587] = Vertex.new(-8, 26, 204, 82, 56, 42),
  [614] = Vertex.new(-8, 26, 204, 54, 37, 27),
})
--#endregion

local bookLeftPage =
  "\x1b\x0b\x02\xff\x43\x2b\x12\xff\x4a\x2d\x10\xff\x4c\x32\x16\xff\x4c\x32\x16\xff\x52\x34\x15\xff\x5c\x3c\x1a\xff\x61\x41\x1c\xff\x6b\x43\x1b\xff\x74\x4c\x22\xff\x74\x4c\x22\xff\x74\x4c\x22\xff\x7b\x53\x27\xff\x83\x5b\x2c\xff\x8b\x63\x31\xff\x93\x65\x31\xff\x94\x6d\x3a\xff\x94\x6d\x3a\xff\x9c\x73\x3c\xff\xa2\x7b\x43\xff\x9f\x7c\x4b\xff\xab\x83\x49\xff\xb1\x84\x48\xff\xb4\x8e\x52\xff\xbb\x92\x54\xff\xbb\x92\x54\xff\xbb\x95\x5a\xff\xc3\x9b\x5c\xff\xc3\x9b\x5c\xff\xc3\x9b\x5c\xff\xcb\xa3\x63\xff\xcb\xa3\x63\xff\xcb\xa3\x63\xff\xcb\xa3\x63\xff\xcb\xa3\x63\xff\xcb\xa3\x63\xff\xcb\xa5\x69\xff\xd3\xab\x6a\xff\xd3\xab\x6a\xff\xd9\xae\x71\xff\xdc\xb2\x6c\xff\xdc\xb2\x6c\xff\xdc\xb4\x73\xff\xdc\xb4\x73\xff\xdc\xb4\x73\xff\xdc\xb4\x73\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xba\x74\xff\xe2\xb5\x72\xff\xda\xad\x6a\xff\xe3\xba\x74\xff\xe5\xc0\x7d\xff\xe2\xb5\x72\xff\xe2\xb4\x6b\xff\xe3\xbc\x7b\xff\xe9\xc3\x83\xff\xe2\xb5\x72\xff\xdc\xb4\x73\xff\xdc\xb2\x6c\xff\xe2\xb5\x72\xff\xe3\xba\x74\xff\xe3\xbc\x7b\xff\xe9\xc3\x83\xff\xec\xca\x8b\xff\xe9\xc3\x83\xff\xe3\xba\x74\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe5\xc0\x7d\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe8\xbd\x7b\xff\xe3\xba\x74\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe2\xb5\x72\xff\xe2\xb5\x72\xff\xe3\xbc\x7b\xff\xe5\xc0\x7d\xff\xe5\xc1\x83\xff\xe5\xc1\x83\xff\xe3\xbc\x7b\xff\xd3\xab\x6a\xff\xd2\xa5\x63\xff\xd3\xab\x6a\xff\xdc\xb5\x7a\xff\xe3\xbc\x7b\xff\xe3\xbd\x81\xff\xe5\xc1\x83\xff\xe3\xbd\x81\xff\xe3\xbc\x7b\xff\xe9\xc2\x7c\xff\xe3\xba\x74\xff\xe9\xc3\x83\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xba\x74\xff\xe5\xc1\x83\xff\xe5\xc1\x83\xff\xe3\xbc\x7b\xff\xe3\xbd\x81\xff\xe5\xc1\x83\xff\xe8\xbc\x73\xff\xe3\xbc\x7b\xff\xe5\xc1\x83\xff\xea\xc5\x8a\xff\xec\xca\x8b\xff\xec\xca\x8b\xff\xe9\xc3\x83\xff\xe8\xbd\x7b\xff\xe9\xc2\x7c\xff\xe9\xc2\x7c\xff\xe9\xc3\x83\xff\xec\xca\x8b\xff\xe9\xc3\x83\xff\xe5\xc1\x83\xff\xe9\xc3\x83\xff\xe3\xbc\x7b\xff\xdc\xb4\x73\xff\xe1\xb6\x79\xff\xe9\xc3\x83\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xbd\x81\xff\xe3\xbc\x7b\xff\xe9\xc3\x83\xff\xe5\xc0\x7d\xff\xe3\xbc\x7b\xff\xe5\xc1\x83\xff\xe5\xc1\x83\xff\xe3\xbd\x81\xff\xe3\xbc\x7b\xff\xe3\xbd\x81\xff\xe5\xc0\x7d\xff\xe5\xc1\x83\xff\xe9\xc3\x83\xff\xe3\xbc\x7b\xff\xe3\xbd\x81\xff\xe3\xbd\x81\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xba\x74\xff\xe2\xb5\x72\xff\xdc\xb4\x73\xff\xdc\xb4\x73\xff\xdc\xb4\x73\xff\xdc\xb5\x7a\xff\xe2\xb5\x72\xff\xdc\xb5\x7a\xff\xe3\xbc\x7b\xff\xe3\xbc\x7b\xff\xe3\xba\x74\xff\xe3\xbc\x7b\xff\xe5\xc1\x83\xff\xe5\xc0\x7d\xff\xe3\xbc\x7b\xff\xe8\xbd\x7b\xff\xe3\xbc\x7b\xff\xe3\xba\x74\xff\xe3\xbc\x7b\xff\xe8\xbd\x7b\xff\xe2\xb5\x72\xff\xe2\xb5\x72\xff\xe8\xbc\x73\xff\xe8\xb6\x72\xff\xe2\xb5\x72\xff\xe8\xbc\x73\xff\xe3\xba\x74\xff\xe3\xba\x74\xff\xe4\xb9\x6c\xff\xe8\xbc\x73\xff\xe3\xba\x74\xff\xe2\xb5\x72\xff\xe2\xb5\x72\xff\xe3\xba\x74\xff\xe3\xba\x74\xff\xe2\xb4\x6b\xff\xe3\xbc\x7b\xff\xe3\xba\x74\xff\xe2\xb4\x6b\xff\xe8\xbc\x73\xff\xe8\xbd\x7b\xff\xe8\xbd\x7b\xff\xe9\xc3\x83\xff\xe9\xc3\x83\xff\xe9\xc3\x83\xff\xe9\xc3\x83\xff\xe9\xc3\x83\xff\xec\xca\x8b\xff\xea\xc5\x8a\xff\xec\xcb\x93\xff\xf0\xce\x93\xff\xe9\xc6\x91\xff\xec\xca\x8b\xff\xf0\xd3\x9c\xff\xec\xcd\x9a\xff\xed\xd1\x9c\xff\xec\xcd\x9a\xff\xec\xcd\x9a\xff\xec\xcb\x93\xff\xec\xcb\x93\xff\xed\xd1\x9c\xff\xec\xcd\x9a\xff\xec\xcd\x9a\xff\xec\xcd\x9a\xff\xec\xcd\x9a\xff\xe9\xc6\x91\xff\xec\xcd\x9a\xff\xed\xd3\xa3\xff\xed\xd3\xa3\xff\xed\xd3\xa3\xff\xed\xd3\xa3\xff\xe8\xcd\xa1\xff\xed\xd5\xaa\xff\xf1\xdc\xb3\xff\xf1\xdc\xb3\xff\xed\xd5\xaa\xff\xed\xd3\xa3\xff\xed\xd3\xa3\xff\xed\xd5\xaa\xff\xf1\xda\xac\xff\xf0\xd6\xa3\xff\xed\xd3\xa3\xff\xe8\xcd\xa1\xff\xed\xd3\xa3\xff\xec\xcb\x93\xff\xed\xd1\x9c\xff\xec\xcd\x9a\xff\xed\xd3\xa3\xff\xed\xd3\xa3\xff\xed\xd1\x9c\xff\xf0\xd3\x9c\xff\xf0\xd3\x9c\xff\xef\xd2\x8c\xff\xf0\xd2\x83\xff\xec\xcb\x7c\xff\xea\xc4\x6c\xff\xe6\xba\x5a\xff\xc8\x8f\x40\xff\xae\x63\x11\xff\x73\x3c\x0a\xff\x13\x07\x01\xff\x00\x00\x01\xcd\x00\x00\x01\x00\x00\x00\x01\x00"

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Sir Owen in the garden north of the Edgeville Monastery.",
    title = "Meeting Saradomin",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(3051, 1605, 3502, { distance = 12 }),
      Action.ModelHighlight:new(sirOwen, { distance = 12 }),
      Action.ConversationHighlight:new("The who with the what now?"),
      Action.ConversationHighlight:new("I guess I have one of those faces."),
      Action.ConversationHighlight:new("Saradomin must have had a good reason..."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { postconditions = { Condition.ConversationText:new("Mighty Saradomin!") } },
  {
    text = "Talk to Saradomin after the cutscene.",
    actions = {
      Action.Direction:new(3051, 1605, 3503, { distance = 12 }),
      Action.ModelHighlight:new(saradomin, { distance = 12 }),
      Action.ConversationHighlight:new("(Kneel before Saradomin.)"),
      Action.ConversationHighlight:new("How can I help?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Pick up the suit of armour just south-west of you.",
    actions = { Action.ModelHighlight:new(suitOfArmour) },
    postconditions = { Condition.InventoryContains:new(captainsCuriass) },
  },
  {
    text = "Equip the armor.",
    actions = {
      Action.InventoryHighlight:new(captainsHelm),
      Action.InventoryHighlight:new(captainsCuriass),
      Action.InventoryHighlight:new(captainsGown),
      Action.InventoryHighlight:new(captainsGauntlets),
      Action.InventoryHighlight:new(captainsBoots),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(captainsHelm) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(captainsHelm),
      Action.InventoryHighlight:new(captainsCuriass),
      Action.InventoryHighlight:new(captainsGown),
      Action.InventoryHighlight:new(captainsGauntlets),
      Action.InventoryHighlight:new(captainsBoots),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(captainsCuriass) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(captainsHelm),
      Action.InventoryHighlight:new(captainsCuriass),
      Action.InventoryHighlight:new(captainsGown),
      Action.InventoryHighlight:new(captainsGauntlets),
      Action.InventoryHighlight:new(captainsBoots),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(captainsGown) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(captainsHelm),
      Action.InventoryHighlight:new(captainsCuriass),
      Action.InventoryHighlight:new(captainsGown),
      Action.InventoryHighlight:new(captainsGauntlets),
      Action.InventoryHighlight:new(captainsBoots),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(captainsGauntlets) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(captainsHelm),
      Action.InventoryHighlight:new(captainsCuriass),
      Action.InventoryHighlight:new(captainsGown),
      Action.InventoryHighlight:new(captainsGauntlets),
      Action.InventoryHighlight:new(captainsBoots),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(captainsBoots) },
  },
  {
    text = "Talk to Saradomin.",
    actions = {
      Action.Direction:new(3051, 1605, 3503, { distance = 12 }),
      Action.ModelHighlight:new(saradomin, { distance = 12 }),
      Action.ConversationHighlight:new("Yes, teleport us to the Black Knights' Fortress."),
    },
    postconditions = { Condition.ConversationText:new("recover the weapon at any cost") },
  },
  {
    text = "Open the Portcullis.",
    title = "Infiltrating the fortress",
    actions = { Action.Direction:new(3023, 2725, 3538.25) },
    postconditions = { Condition.ConversationText:new("Captain Dulcin!") },
  },
  {
    text = "Fortress guard:<ul><li>Dismiss pets & familiars before proceeding.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Who?"),
      Action.ConversationHighlight:new("He is my prisoner."),
      Action.ConversationHighlight:new("No, I have something... special planned for him."),
    },
    postconditions = { Condition.ConversationText:new("Now let's find a way up to the next level.") },
  },
  {
    text = "Talk to Lieutenant York to the north.",
    actions = {
      Action.Direction:new(3023, 2760, 3557, { distance = 8 }),
      Action.ModelHighlight:new(lieutenantYork, { distance = 8 }),
      Action.ConversationHighlight:new("Are you suggesting I cannot handle a mere knight of Saradomin?"),
    },
    postconditions = { Condition.ConversationText:new("Please continue, Captain Dulcin.") },
  },
  {
    text = "Climb up staircase to the north-west. The fortress guard will stop you.",
    actions = { Action.Direction:new(3003, 3345, 3577) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(fortressGuard),
      Action.ConversationHighlight:new("What do you want, maggot?"),
      Action.ConversationHighlight:new("This prisoner is to be sacrificed."),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = { Action.Direction:new(3003, 3345, 3577) },
    postconditions = { Condition.DistanceToWithHeight:new(3004, 4921, 3576, 4) },
  },
  {
    text = "Head to the 1st floor (2nd floor[US]) to the south, talk to Hierophant Marius.",
    actions = {
      Action.Direction:new(3023, 4901, 3547, { distance = 8 }),
      Action.ModelHighlight:new(hierophantMarius, { distance = 8 }),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("I shall return after I deal with this Temple Knight."),
    },
    postconditions = { Condition.ConversationText:new("may proceed up the south-east stairs") },
  },
  {
    text = "Climb up staircase. The fortress guard will stop you.",
    actions = { Action.Direction:new(3031, 5521, 3540) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(fortressGuard2),
      Action.ConversationHighlight:new("I am bringing him to the tallest tower."),
      Action.ConversationHighlight:new("We need him to recover the weapon of power, as you know."),
    },
    postconditions = {
      Condition.ConversationText:new("Strength through chaos."),
      Condition.DistanceToWithHeight:new(3030, 7077, 3541, 4),
    },
  },
  {
    actions = { Action.Direction:new(3031, 5521, 3540) },
    postconditions = { Condition.DistanceTo:new(3030, 7077, 3541, 4) },
  },
  {
    text = "Talk to Lieutenant Graves.",
    actions = {
      Action.Direction:new(3023, 7712, 3553, { distance = 8 }),
      Action.ModelHighlight:new(lieutenantGraves, { distance = 8, atLocation = Location:new(3023, 7112, 3552) }),
      Action.ConversationHighlight:new("Stand aside. I will handle this."),
      Action.ConversationHighlight:new("The prisoner stays with me."),
    },
    postconditions = { Condition.ConversationText:new("Thanks for the distraction") },
  },
  {
    text = "Search the Lieutenant's body.",
    actions = {
      Action.ModelHighlight:new(lieutenantGraves, { distance = 8, atLocation = Location:new(3023, 7112, 3552) }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      Condition.ConversationText:new("As you wish"),
      Condition.ConversationText:new("feel a thing"),
      Condition.ConversationText:new("investigate the attic"),
    },
  },
  {
    text = "Climb the stairs to the north-east.",
    title = "The ritual",
    actions = { Action.Direction:new(3031.5, 7677, 3577.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3031, 9273, 3576, 5) },
  },
  {
    text = "Exit the dialogue. Search the wardrobe.",
    actions = { Action.Direction:new(3026, 9853, 3574) },
    postconditions = { Condition.InventoryContains:new(letterFromDaquarius) },
  },
  {
    text = "Search the box of candles.",
    actions = { Action.Direction:new(3027, 9273, 3571) },
    postconditions = { Condition.InventoryContains:new(redCandle, 3) },
  },
  {
    text = "Search the shelf.",
    actions = { Action.Direction:new(3025.7, 9753, 3576) },
    postconditions = { Condition.InventoryContains:new(chalk) },
  },
  {
    text = "Pick up the Grimoire and read it.",
    actions = {
      Action.ModelHighlight:new(grimoireStand),
      Action.ConversationHighlight:new("Pick up the grimoire."),
      Action.ConversationHighlight:new("Pick-up the grimoire."),
    },
    postconditions = { Condition.InventoryContains:new(grimoire) },
  },
  {
    text = "Read the Grimoire.",
    actions = { Action.InventoryHighlight:new(grimoire) },
    postconditions = { Condition.Generic2DVisible:new(251, 334, 100, bookLeftPage) },
  },
  {
    text = "Fix each candle stand.",
    actions = {
      Action.InventoryHighlight:new(redCandle),
      Action.ModelHighlight:new(candleStand, { highlightPriority = "all" }),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(redCandle) },
  },
  {
    text = "Light the candles.",
    actions = { Action.ModelHighlight:new(candleStandWithCandle, { highlightPriority = "all" }) },
    postconditions = { Condition.ModelNotVisible:new(candleStandWithCandle) },
  },
  {
    text = "Repair the Ritual circle.",
    actions = {
      Action.InventoryHighlight:new(chalk),
      Action.ModelHighlight:new(chalkCircle),
      Action.ConversationHighlight:new("Use the chalk to repair the magic circle."),
    },
    postconditions = { Condition.ModelVisible:new(repairedCircle) },
  },
  {
    text = "Chant over the north-west candle.<ul><li>Arom</li><li>Nahrea</li><li>Imperium</li></ul>",
    warning = "Make sure you choose the correct order.",
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3032, 10213, 3581) }),
      Action.ConversationHighlight:new("Arom."),
      Action.ConversationHighlight:new("Nahrea."),
      Action.ConversationHighlight:new("Imperium."),
    },
    postconditions = { Condition.ConversationText:new("Arom") },
  },
  {
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3032, 10213, 3581) }),
      Action.ConversationHighlight:new("Arom."),
      Action.ConversationHighlight:new("Nahrea."),
      Action.ConversationHighlight:new("Imperium."),
    },
    postconditions = { Condition.ConversationText:new("Nahrea") },
  },
  {
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3032, 10213, 3581) }),
      Action.ConversationHighlight:new("Arom."),
      Action.ConversationHighlight:new("Nahrea."),
      Action.ConversationHighlight:new("Imperium."),
    },
    postconditions = { Condition.ConversationText:new("Imperium") },
  },
  {
    text = "Chant over the north-east candle.<ul><li>Feritas</li><li>Silenti</li><li>Sepulchrum</li></ul>",
    warning = "Make sure you choose the correct order.",
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3035, 10213, 3581) }),
      Action.ConversationHighlight:new("Feritas."),
      Action.ConversationHighlight:new("Silenti."),
      Action.ConversationHighlight:new("Sepulchrum."),
    },
    postconditions = { Condition.ConversationText:new("Feritas") },
  },
  {
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3035, 10213, 3581) }),
      Action.ConversationHighlight:new("Feritas."),
      Action.ConversationHighlight:new("Silenti."),
      Action.ConversationHighlight:new("Sepulchrum."),
    },
    postconditions = { Condition.ConversationText:new("Silenti") },
  },
  {
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3035, 10213, 3581) }),
      Action.ConversationHighlight:new("Feritas."),
      Action.ConversationHighlight:new("Silenti."),
      Action.ConversationHighlight:new("Sepulchrum."),
    },
    postconditions = { Condition.ConversationText:new("Sepulchrum") },
  },
  {
    text = "Chant over the south-east candle.<ul><li>Igasac</li><li>Perdimit</li><li>Ebulam</li></ul>",
    warning = "Make sure you choose the correct order.",
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3035, 10213, 3578) }),
      Action.ConversationHighlight:new("Igasac."),
      Action.ConversationHighlight:new("Perdimit."),
      Action.ConversationHighlight:new("Ebulam."),
    },
    postconditions = { Condition.ConversationText:new("Igasac") },
  },
  {
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3035, 10213, 3578) }),
      Action.ConversationHighlight:new("Igasac."),
      Action.ConversationHighlight:new("Perdimit."),
      Action.ConversationHighlight:new("Ebulam."),
    },
    postconditions = { Condition.ConversationText:new("Perdimit") },
  },
  {
    actions = {
      Action.ModelHighlight:new(litCandleStand, { atLocation = Location:new(3035, 10213, 3578) }),
      Action.ConversationHighlight:new("Igasac."),
      Action.ConversationHighlight:new("Perdimit."),
      Action.ConversationHighlight:new("Ebulam."),
    },
    postconditions = { Condition.ConversationText:new("Ebulam") },
  },
  {
    text = "Enter the portal.<ul><li>You must remove the suit of armour that was equipped at the beginning of the quest.</li></ul>",
    actions = {
      Action.Direction:new(3033.5, 10813, 3579.5),
      Action.ConversationHighlight:new("Yes, enter the portal."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Defeat the three Black Knights.",
    title = "The Saradominist crypt",
    neededItems = {
      ["Combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(blackKnight),
    },
    postconditions = { Condition.ConversationText:new("Thank you") },
  },
  {
    text = "Search the bodies.",
    actions = { Action.ModelHighlight:new(blackKnight, { highlightPriority = "all" }) },
    postconditions = {
      Condition.InventoryContains:new(smallMetalKey),
      Condition.ConversationText:new("You recover a small metal key"),
    },
  },
  {
    text = "Unlock the hanging cage.",
    actions = { Action.ModelHighlight:new(dawn) },
    postconditions = { Condition.ConversationText:new("Thank you") },
  },
  {
    text = "Talk to Dawn and learn all she knows.",
    actions = {
      Action.ModelHighlight:new(dawn),
      Action.ConversationHighlight:new("Where are we?"),
    },
    postconditions = { Condition.ConversationText:new("There's snow at the surface entrance") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dawn),
      Action.ConversationHighlight:new("Who's Saint Elspeth?"),
    },
    postconditions = { Condition.ConversationText:new("We devote our time to music") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dawn),
      Action.ConversationHighlight:new("How did the Kinshra capture you?"),
    },
    postconditions = { Condition.ConversationText:new("If you hadn't arrived when you") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dawn),
      Action.ConversationHighlight:new("Continue."),
    },
    postconditions = { Condition.ConversationText:new("I'm right here") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dawn),
      Action.ConversationHighlight:new("Let's take her with us."),
    },
    postconditions = {
      Condition.ConversationText:new("investigate the tomb"),
      Condition.ConversationInactive:new(),
    },
  },
  {
    text = "Enter the doorway to the east.<ul><li>Optional: Search the supply crate south of the portal first.</li></ul>",
    actions = { Action.ModelHighlight:new(doorway, { instanced = true }) },
    postconditions = {
      Condition.ModelVisible:new(pileOfBones),
      Condition.ModelVisible:new(lootedCoffin),
    },
  },
  {
    text = "Inspect the looted coffin.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(lootedCoffin),
    },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Open the portcullis to the south.",
    actions = { Action.ModelHighlight:new(graveyardPortcullis) }, --huge, but acceptable imo
    postconditions = { Condition.ModelVisible:new(graveyardPortcullisOpen) },
  },
  {
    actions = { Action.ModelHighlight:new(graveyardPortcullisOpen) },
    postconditions = { Condition.ModelVisible:new(blackKnight) },
  },
  {
    text = "Talk to Fern.",
    title = "Fern",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(0, -1682, -19, { instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(fern, { animated = true }) },
  },
  {
    -- text = "debug0",
    actions = {
      Action.ModelHighlight:new(fern),
      Action.ConversationHighlight:new("I am ready to be judged."),
    },
    postconditions = {
      Condition.ConversationText:new("I must ensure you have the strength"),
      Condition.ConversationText:new("You must kill me"),
    },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(fern),
      Action.ConversationHighlight:new("I fight to protect the innocent."),
    },
    postconditions = { Condition.ConversationText:new("You must kill me") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(fern),
      Action.ConversationHighlight:new("I'm ready."),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Defeat Fern.<ul><li>Dodge her charge attack.</li><li>Optional: Loot the dead black knights for some coins.</li></ul>",
    postconditions = { Condition.ConversationText:new("grieves me to have") },
  },
  {
    text = "Click on the wand.",
    title = "The Wand of Resurrection",
    actions = {
      Action.Direction:new(0, -3540, -46, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(wandBarrier, { instance = true, distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("disable the protective wards") },
  },
  --After flipping two tiles next to each other, the models get merged. So, I just highlight the tile.
  { --bunny 1+2
    text = "Matching two tiles at a time to solve the puzzle.<ul><li>There's an image on the wiki you can refer to. This will be tracked in a later version.</li></ul>",
    warning = "Tracking is based on position. It won't be accurate.",
    actions = {
      Action.Direction:new(1, -3540, -43, { instance = true, tile = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(bunnyTile, { instance = true, atLocation = Location:new(1, -3540, -42) }),
    },
  },
  {
    actions = { Action.Direction:new(2, -3540, -47, { instance = true, tile = true }) },
    postconditions = {
      Condition.ModelVisible:new(bunnyTile2, { instance = true, atLocation = Location:new(2, -3540, -47) }),
    },
  },
  { --star 1
    actions = { Action.Direction:new(2, -3540, -46, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(3, -3540, -46, 0, true) },
  },
  { --star 2
    actions = { Action.Direction:new(1, -3540, -47, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(1, -3540, -48, 0, true) },
  },
  { --centaur 1
    actions = { Action.Direction:new(0, -3540, -47, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(0, -3540, -48, 0, true) },
  },
  { --centaur 2
    actions = { Action.Direction:new(-2, -3540, -43, { instance = true, tile = true }) },
    postconditions = {
      Condition.DistanceTo:new(-2, -3540, -42, 0, true),
      Condition.DistanceTo:new(-3, -3540, -43, 0, true),
    },
  },
  { --wing 1
    actions = { Action.Direction:new(-2, -3540, -44, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-3, -3540, -44, 0, true) },
  },
  { --wing 2
    actions = { Action.Direction:new(-2, -3540, -47, { instance = true, tile = true }) },
    postconditions = {
      Condition.DistanceTo:new(-3, -3540, -47, 0, true),
      Condition.DistanceTo:new(-2, -3540, -48, 0, true),
    },
  },
  { --unicorn 1
    actions = { Action.Direction:new(-1, -3540, -47, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-1, -3540, -48, 0, true) },
  },
  { --unicorn 2
    actions = { Action.Direction:new(2, -3540, -43, { instance = true, tile = true }) },
    postconditions = {
      Condition.DistanceTo:new(2, -3540, -42, 0, true),
      Condition.DistanceTo:new(3, -3540, -43, 0, true),
    },
  },
  { --helm 1
    actions = { Action.Direction:new(2, -3540, -44, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(3, -3540, -44, 0, true) },
  },
  { --helm 2
    actions = { Action.Direction:new(-2, -3540, -45, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-3, -3540, -45, 0, true) },
  },
  { --owl 1
    actions = { Action.Direction:new(-2, -3540, -46, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-3, -3540, -46, 0, true) },
  },
  { --owl 2
    actions = { Action.Direction:new(0, -3540, -43, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(0, -3540, -42, 0, true) },
  },
  { --lion 1
    actions = { Action.Direction:new(-1, -3540, -43, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-1, -3540, -42, 0, true) },
  },
  { --lion 2
    actions = { Action.Direction:new(2, -3540, -45, { instance = true, tile = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Try to take the wand.",
    actions = {
      Action.ModelHighlight:new(wandBarrier2, { instanced = true }),
      Action.ConversationHighlight:new("I shall sacrifice my own blood."),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("Dawn!") },
  },
  {
    text = "Talk to Dawn to the north.",
    title = "Fighting for the wand",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(0, 1658, 23, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(dawnArmour, { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Give me the wand or you won't live to regret it."),
      Action.ConversationHighlight:new("Continue"),
    },
    postconditions = { Condition.ConversationText:new("Now you die.") },
  },
  {
    text = "Defeat the Black Knights and Sir Owen.",
    warning = "There's no tracking for this step.",
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ChatText:new("Zombiedefeated(3/3).") },
  },
  {
    text = "Go north and open the portcullis.",
    actions = { Action.ModelHighlight:new(wandChamberPortcullis, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(wandChamberPortcullisOpen, { instance = true }) },
  },
  {
    actions = { Action.ModelHighlight:new(wandChamberPortcullisOpen, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(dawnArmour, { instance = true }) },
  },
  {
    text = "Kill Dawn.<ul><li>Refer to the wiki guide if you're having trouble.</li></ul>",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(dawnArmour, { instanced = true }),
    },
    postconditions = { Condition.ConversationText:new("how well you fare") },
  },
  { postconditions = { Condition.ConversationInactive:new() } }, --fight starts
  { postconditions = { Condition.ConversationActive:new() } }, --dawn starts to die
  { postconditions = { Condition.ConversationInactive:new() } }, --dawn dies
  {
    text = "Search Dawn's body.",
    actions = { Action.ModelHighlight:new(dawnArmour, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(wandOfRes) },
  },
  {
    text = "Enter the Portcullis to the south.",
    title = "Saradomin and the wand",
    actions = { Action.ModelHighlight:new(graveyardPortcullisOpen, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(blackKnight) },
  },
  {
    text = "Speak with Saradomin.<ul><li>Optional: When asked to be teleported back to Edgeville, it is recommended to stay in the tombs to claim the extra post-quest rewards which require level 40 Strength and two other coffins requiring level 60 and 80 Strength.</li></ul>",
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ModelVisible:new(saradomin, { instance = true }) },
  },
  {
    actions = {
      Action.ModelHighlight:new(saradomin, { instanced = true }),
      Action.ConversationHighlight:new("I've made my decision."),
      Action.ConversationHighlight:new("I will give you the wand."),
      Action.ConversationHighlight:new("Were we too late?"),
      Action.ConversationHighlight:new("I stand by you, Saradomin."),
      Action.ConversationHighlight:new("Why would you help us?"),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Death of Chivalry",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = false,
  length = Enums.length.long,
  releaseDate = 1376352000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Combat gear"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Black knights"] = { level = "Scaled", quantity = 5 },
    ["Fern"] = { level = "Scaled" },
    ["Dawn"] = { level = "Scaled" },
    ["Skeletons"] = { level = "Scaled" },
    ["Sir Owen"] = { level = "Scaled" },
  },
})
