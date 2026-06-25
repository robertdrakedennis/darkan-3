local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local sliskeClone = Model.new(5544, {
  [1122] = Vertex.new(-31, 663, -108, 140, 139, 139),
  [1134] = Vertex.new(-23, 675, -113, 140, 139, 139),
  [1455] = Vertex.new(31, 663, -108, 140, 139, 139),
  [1461] = Vertex.new(22, 675, -113, 140, 139, 139),
  [3087] = Vertex.new(41, 650, -103, 140, 140, 140),
})
-- local seren
-- local armadyl
local zamorak = Model.new(8583, {
  [2850] = Vertex.new(-124, 834, 58, 128, 127, 127),
  [2853] = Vertex.new(-144, 812, 58, 128, 127, 127),
  [7713] = Vertex.new(19, 966, -40, 128, 127, 127),
  [7866] = Vertex.new(-23, 966, -40, 128, 127, 127),
  [7887] = Vertex.new(-18, 960, -40, 128, 127, 127),
})
local bandos = Model.new(15516, {
  [525] = Vertex.new(287, 999, 129, 179, 113, 53),
  [1166] = Vertex.new(327, 1010, 77, 190, 120, 57),
  [1911] = Vertex.new(330, 1019, 63, 213, 135, 64),
  [4565] = Vertex.new(311, 1078, 75, 176, 123, 52),
  [5816] = Vertex.new(-86, 1247, -130, 55, 35, 16),
})
local saradomin = Model.new(17868, {
  [3527] = Vertex.new(80, 597, -63, 127, 127, 127),
  [13812] = Vertex.new(46, 593, -90, 127, 127, 127),
  [17851] = Vertex.new(0, 914, 9, 52, 138, 27),
  [17852] = Vertex.new(0, 911, 9, 52, 138, 27),
  [17853] = Vertex.new(0, 911, 7, 52, 138, 27),
})
local guthix = Model.new(6663, {
  [1068] = Vertex.new(3, 817, -37, 56, 47, 43),
  [1070] = Vertex.new(5, 813, -33, 56, 47, 43),
  [1074] = Vertex.new(-3, 817, -37, 56, 47, 43),
  [1076] = Vertex.new(-5, 813, -33, 56, 47, 43),
  [5313] = Vertex.new(44, 744, 44, 71, 63, 54),
})
local vicendithas = Model.new(59307, {
  [45677] = Vertex.new(33, 914, 59, 127, 127, 127),
  [45716] = Vertex.new(13, 912, 72, 127, 127, 127),
  [46349] = Vertex.new(-21, 908, 68, 127, 127, 127),
  [46357] = Vertex.new(-6, 921, 84, 127, 127, 127),
  [46832] = Vertex.new(-26, 901, 88, 127, 127, 127),
})
--#endregion
--#region Objects
local temporalInstability = Model.any({
  Model.new(1974, {
    [109] = Vertex.new(194, 427, 200, 183, 240, 243),
    [113] = Vertex.new(193, 429, 195, 183, 240, 243),
    [115] = Vertex.new(190, 426, 205, 183, 240, 243),
    [118] = Vertex.new(192, 429, 203, 183, 240, 243),
    [119] = Vertex.new(195, 427, 199, 183, 240, 243),
  }),
  Model.new(1974, {
    [109] = Vertex.new(194, 444, 200, 183, 240, 243),
    [113] = Vertex.new(193, 446, 195, 183, 240, 243),
    [115] = Vertex.new(190, 444, 205, 183, 240, 243),
    [118] = Vertex.new(192, 447, 203, 183, 240, 243),
    [119] = Vertex.new(195, 444, 199, 183, 240, 243),
  }),
  Model.new(1974, {
    [109] = Vertex.new(194, 440, 200, 183, 240, 243),
    [113] = Vertex.new(193, 442, 195, 183, 240, 243),
    [115] = Vertex.new(190, 438, 205, 183, 240, 243),
    [118] = Vertex.new(192, 441, 203, 183, 240, 243),
    [119] = Vertex.new(195, 440, 199, 183, 240, 243),
  }),
})
local strangePortal = Model.new(810, {
  [132] = Vertex.new(-541, 50, -91, 185, 228, 20, 0.2000),
  [145] = Vertex.new(-353, 50, 411, 185, 228, 20, 0.2000),
  [174] = Vertex.new(360, 50, 411, 185, 228, 20, 0.2000),
  [186] = Vertex.new(550, 50, 95, 185, 228, 20, 0.2000),
  [189] = Vertex.new(550, 50, 95, 185, 228, 20, 0.2000),
})
local exitPortal = Model.new(810, {
  [132] = Vertex.new(-541, 50, -91, 184, 227, 19, 0.2000),
  [145] = Vertex.new(-353, 50, 411, 184, 227, 19, 0.2000),
  [174] = Vertex.new(360, 50, 411, 184, 227, 19, 0.2000),
  [186] = Vertex.new(550, 50, 95, 184, 227, 19, 0.2000),
  [189] = Vertex.new(550, 50, 95, 184, 227, 19, 0.2000),
})
local memory = Model.new(1584, {
  [15] = Vertex.new(4792188, 3321, 1714030, 249, 132, 132, 0.2431),
  [27] = Vertex.new(4792150, 3355, 1714024, 249, 132, 132, 0.09412),
  [56] = Vertex.new(4792182, 3336, 1714035, 255, 104, 104, 0.1882),
  [57] = Vertex.new(4792182, 3336, 1714035, 255, 104, 104, 0.1882),
  [60] = Vertex.new(4792182, 3336, 1714035, 255, 104, 104, 0.1882),
})
local mutableAnima = Model.new(1962, {
  [470] = Vertex.new(44, 611, 300, 82, 205, 98),
  [472] = Vertex.new(44, 611, 300, 82, 205, 98),
  [476] = Vertex.new(37, 617, 300, 82, 205, 98),
  [477] = Vertex.new(44, 611, 300, 82, 205, 98),
  [1009] = Vertex.new(43, 610, 300, 33, 112, 53),
})
--#endregion
--#region Items
local skullOfRemembrance = Model.new(1239, {
  [176] = Vertex.new(-29, 21, -30, 92, 84, 70),
  [632] = Vertex.new(-3, 21, -45, 92, 84, 70),
  [870] = Vertex.new(18, 30, -11, 43, 39, 33),
  [873] = Vertex.new(5, 41, -33, 46, 42, 35),
  [1028] = Vertex.new(-24, 41, -17, 46, 42, 35),
})
--#endregion
--#region Quest Items
local letterFromCharos = Model.new(138, {
  [7] = Vertex.new(-100, 16, -56, 132, 121, 101),
  [10] = Vertex.new(-100, 16, -56, 132, 121, 101),
  [16] = Vertex.new(100, 24, -56, 132, 121, 101),
  [46] = Vertex.new(36, 36, 4, 141, 129, 108),
  [67] = Vertex.new(-100, 16, 56, 141, 129, 108),
})
local brokenSliskeMaskShard = Model.new(156, {
  [30] = Vertex.new(-48, 1, 140, 161, 148, 147, 0.4118),
  [86] = Vertex.new(-48, 17, 140, 54, 64, 19, 0.4078),
  [90] = Vertex.new(-48, 17, 140, 54, 64, 19, 0.4078),
  [107] = Vertex.new(-48, 17, 140, 54, 64, 19, 0.5294),
  [131] = Vertex.new(-48, 17, 140, 161, 148, 147, 0.2941),
})
local sliskeMask = Model.new(1188, {
  [1092] = Vertex.new(13, 6, 35, 71, 67, 65),
  [1097] = Vertex.new(13, 6, 35, 71, 67, 65),
  [1139] = Vertex.new(-26, 13, 36, 71, 67, 65),
  [1163] = Vertex.new(-36, 6, 18, 71, 67, 65),
  [1169] = Vertex.new(-36, 6, 18, 71, 67, 65),
})
--#endregion

local bookTexture =
  "\x39\x1c\x0b\x00\x53\x29\x0c\x25\x8b\x49\x1b\xff\x73\x34\x0c\xff\x6b\x2f\x0a\xff\x7b\x3b\x12\xff\x5b\x24\x02\xff\x7b\x3b\x12\xff\x6b\x2f\x0a\xff\x2b\x11\x04\xff\x6b\x3b\x17\xff\xe5\xb4\x63\xff\xe3\xab\x4b\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa5\x4a\xff\xda\xa3\x4b\xff\xdf\xab\x5a\xff\xe9\xbd\x73\xff\xeb\xc4\x83\xff\xec\xc4\x7b\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xee\xd3\xa0\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf1\xd6\xa8\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd6\xa8\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xe6\xca\x90\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xdc\xbf\x89\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xde\xc0\x84\xff\xd5\xb9\x85\xff\xd5\xb9\x85\xff\xdb\xbc\x83\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd2\xad\x6a\xff\xc6\xa3\x6b\xff\xcb\xa4\x62\xff\xc7\x9c\x5a\xff\xc3\x94\x52\xff\xc3\x94\x52\xff\xbc\x84\x42\xff\xba\x7c\x3a\xff\xb9\x7a\x34\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb5\x7a\x3a\xff\xbc\x84\x42\xff\xc2\x8d\x4a\xff\x99\x6a\x3b\xff\x94\x52\x22\xff\x3c\x22\x10\xff\x3c\x22\x10\xff\x94\x52\x22\xff\x99\x6a\x3b\xff\xc2\x8d\x4a\xff\xbc\x84\x42\xff\xb5\x7a\x3a\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb9\x7a\x34\xff\xba\x7c\x3a\xff\xbc\x84\x42\xff\xc3\x94\x52\xff\xc3\x94\x52\xff\xc7\x9c\x5a\xff\xcb\xa4\x62\xff\xc6\xa3\x6b\xff\xd2\xad\x6a\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbc\x83\xff\xd5\xb9\x85\xff\xd5\xb9\x85\xff\xde\xc0\x84\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xdc\xbf\x89\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe6\xca\x90\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd6\xa8\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd6\xa8\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xee\xd3\xa0\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xec\xc4\x7b\xff\xeb\xc4\x83\xff\xe9\xbd\x73\xff\xdf\xab\x5a\xff\xda\xa3\x4b\xff\xe1\xa5\x4a\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe3\xab\x4b\xff\xe5\xb4\x63\xff\x6b\x3b\x17\xff\x2b\x11\x04\xff\x6b\x2f\x0a\xff\x7b\x3b\x12\xff\x5b\x24\x02\xff\x7b\x3b\x12\xff\x6b\x2f\x0a\xff\x73\x34\x0c\xff\x8b\x49\x1b\xff\x53\x29\x0c\x25\x39\x1c\x0b\x00"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Seren on the 1st floor (2nd floor[US]) of Burthorpe Castle.",
    title = "Attending the council",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(2901.5, 2965, 3570) },
    postconditions = { Condition.DistanceToWithHeight:new(2899, 3877, 3567, 24) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["seren"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Watch the cutscene or hit escape.", postconditions = { Condition.ConversationText:new("Ridiculous") } },
  { text = "Continue the conversation.", postconditions = { Condition.ConversationActive:new() } },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Osman.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["osman"]),
      Action.ConversationHighlight:new("Do you have anywhere we could plant the garden?"),
    },
    postconditions = { Condition.ChatText:new("for the garden") },
  },
  {
    text = "Talk to Sir Amik Varze.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["sir amik varze"]),
      Action.ConversationHighlight:new("Do you have a workforce to plant it?"),
    },
    postconditions = { Condition.ChatText:new("obtained a workforce") },
  },
  {
    text = "Talk to Azzanadra. Propose the plan.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["azzanadra"]),
      Action.ConversationHighlight:new("Desperate Times"),
      Action.ConversationHighlight:new("Do you have any seeds we could use?"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("secured some seeds") },
  },
  {
    text = "Talk to Vanescula Drakan. Propose the plan.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("Do you have anywhere we could plant the garden?"),
      Action.ConversationHighlight:new("Change to Morytania."),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ChatText:new("location to Morytania") },
  },
  {
    text = "Talk to Zarador. Propose the plan.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["zarador"]),
      Action.ConversationHighlight:new("Do you have anywhere we could plant the garden?"),
      Action.ConversationHighlight:new("Change to Feldip."),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ModelVisible:new(Models.npcs["kerapac"]) },
  },
  {
    text = "Kerapac will appear. Complete the dialogue.<ul><li>If the dialogue containing Kerapac does not start, talk to Seren.</li></ul>",
    postconditions = { Condition.ConversationText:new("head to the Needle") },
  },
  {
    text = "Talk to Kerapac next to the Needle.<ul><li>It is located west of Piscatoris Fishing Colony (Phoenix Lair Teleport, fairy ring AKQ, Sixth-Age circuit teleports to Guthix memorial, or via memory strands within the currency pouch).</li></ul>",
    title = "The Needle",
    actions = {
      Action.Direction:new(2225, 3909, 3657, { distance = 10 }),
      Action.ModelHighlight:new(Models.npcs["kerapac"], { distance = 10 }),
      Action.ConversationHighlight:new("Let's get on with it."),
      Action.ConversationHighlight:new("Say nothing."),
      Action.ConversationHighlight:new("Yes, I'm ready."),
    },
    postconditions = { Condition.ConversationText:new("all is lost") },
  },
  {
    text = "Close the temporal instability near the needle.",
    actions = {
      Action.Direction:new(2232, 3369, 3670, { distance = 8 }),
      Action.ModelHighlight:new(temporalInstability, { distance = 8 }),
    },
    postconditions = { Condition.ChatText:new("closed 1/5") },
  },
  {
    text = "Close the temporal instability near the stream.",
    actions = {
      Action.Direction:new(2209, 785, 3678, { distance = 8 }),
      Action.ModelHighlight:new(temporalInstability, { distance = 8 }),
    },
    postconditions = { Condition.ChatText:new("closed 2/5") },
  },
  {
    text = "Close the temporal instability near the cart.",
    actions = {
      Action.Direction:new(2189, 1305, 3685, { distance = 8 }),
      Action.ModelHighlight:new(temporalInstability, { distance = 8 }),
    },
    postconditions = { Condition.ChatText:new("closed 3/5") },
  },
  {
    text = "Close the temporal instability in the room upstairs.",
    actions = { Action.Direction:new(2204.5, 1305, 3691) },
    postconditions = { Condition.DistanceToWithHeight:new(2204, 2469, 3695, 4) },
  },
  {
    actions = {
      Action.Direction:new(2197, 3069, 3694),
      Action.ModelHighlight:new(temporalInstability, { highlightPriority = "closest" }),
    },
    postconditions = { Condition.ChatText:new("closed 4/5") },
  },
  {
    text = "Close the temporal instability.",
    actions = {
      Action.Direction:new(2191, 3069, 3692),
      Action.ModelHighlight:new(temporalInstability, { highlightPriority = "closest" }),
    },
    postconditions = { Condition.ChatText:new("closed 5/5") },
  },
  {
    text = "Talk to Kerapac again.",
    actions = { Action.Direction:new(2204.5, 2369, 3694) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2204, 805, 3690, 4),
      Condition.DistanceTo:new(2225, 3837, 3659, 4),
    },
  },
  {
    actions = {
      Action.Direction:new(2225, 3909, 3657, { distance = 10 }),
      Action.ModelHighlight:new(Models.npcs["kerapac"], { distance = 10 }),
    },
    postconditions = { Condition.ConversationText:new("someone called McGrubor") },
  },
  {
    text = "Descend into the mysterious hole inside McGrubor's Wood.<ul><li>ALS.</li><li>Seers' Village lodestone.</li></ul>",
    actions = { Action.Direction:new(2661.5, 1693, 3500) },
    postconditions = {
      Condition.DistanceTo:new(2663, 1101, 3500, 1), -- Loose railing
      Condition.DistanceTo:new(2644, 885, 3494, 4), --Fairy ring
      Condition.DistanceTo:new(2659, 1389, 3486, 4), --Next to hole
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.objects["mysterious hole"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Investigate the coffin.",
    actions = {
      Action.Direction:new(32, 3425, 15, { instance = true, distance = 20 }),
      Action.ModelHighlight:new(Models.objects["mysterious hole coffin"], { instance = true, distance = 20 }),
    },
    postconditions = { Condition.InventoryContains:new(letterFromCharos) },
  },
  {
    text = "Read the letter from Charos.",
    actions = { Action.InventoryHighlight:new(letterFromCharos) },
    postconditions = { Condition.Generic2DVisible:new(482, 290, 140, bookTexture) },
  },
  {
    text = "Step on the strange portal near the entrance to the Empty Throne Room in the Varrock Dig Site.",
    title = "First key",
    warning = "This section does not have tracking as of now.",
    neededItems = {
      ["Old necklace"] = { quantity = 1 },
      ["Necklace of Charos"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3378, 445, 3405, { tile = true }) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Step on the portals until a room with a chest unlocks. Once in a room with a chest, read the section below for the solution.",
    postconditions = { Condition.ChatText:new("feels more powerful somehow") },
  },
  {
    text = "<ul><li>If the room has a note on a recipe: If the amount of each & every ingredient can be represented with a single Roman numeral (e.g. 1000g of Bacon = 'M'), those are the code.<br/>If not, the code is the first letter of each ingredient.</li><li>Note: Roman numerals for some of the steps below: 1=I, 5=V, 10=X, 50=L, 100=C, 500=D, 1,000=M.</li><li>If the room has items on the ground that can't be picked up, the code is the first letter of each item (e.g. Plank, Jute Fibres, Catfish and Diamond Bolt). Start from the furthest item away from the chest, with the next item the next closest. If this doesn't work, try the letters in the opposite order.</li><li>If the room has coins on the ground examine each pile, the code is the number of coins in each pile. Go clockwise, as represented in Roman numerals (e.g. 500 coins = 'D').</li><li>If there is a plaque next to the room's door, the code is the first letter of each lodestone name depicted (e.g. Lumbridge, Falador, Draynor Village and Varrock).</li><li>If the room has only one item on the ground with a four-letter word in its name, the combination is those four letters. (e.g. Beer = BEER or Swordfish = FISH).</li><li>If the room has a clean herb on the ground, the combination is HERB.</li></ul>",
    postconditions = { Condition.ChatText:new("feels more powerful somehow") },
  },
  {
    text = "Note the order of the icons.",
    postconditions = { Condition.ChatText:new("feels more powerful somehow") },
  },
  {
    text = "Repeat for all chests in the room.",
    postconditions = { Condition.ChatText:new("feels more powerful somehow") },
  },
  {
    text = "Rearrange the slide puzzle in the middle to match the icons seen from the chests. (North side is up. If you accidentally did the puzzle upside down, just rotate the outer pieces until they are in place.)",
    postconditions = { Condition.ChatText:new("feels more powerful somehow") },
  },
  {
    text = "Step on the exit portal to the west.",
    actions = { Action.ModelHighlight:new(exitPortal) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Teleport with the skull of remembrance.",
    title = "Second key",
    neededItems = { ["Skull of remembrance"] = { quantity = 1 } },
    actions = {
      Action.InventoryHighlight:new(Models.items["skull of remembrance"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3035, 10213, 3577, 4) },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(3031.5, 9273, 3577.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3030, 7077, 3576, 4) },
  },
  {
    text = "Climb down the stairs again.",
    actions = { Action.Direction:new(3031, 7477, 3540) },
    postconditions = { Condition.DistanceToWithHeight:new(3030, 4921, 3541, 4) },
  },
  {
    text = "Climb down the stairs yet again.",
    actions = { Action.Direction:new(3003, 5321, 3577) },
    postconditions = { Condition.DistanceToWithHeight:new(3004, 2745, 3576, 4) },
  },
  {
    text = "Step onto the portal.",
    actions = { Action.Direction:new(3030, 2725, 3545, { tile = true }) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Follow the same guide as the first key.",
    postconditions = { Condition.ChatText:new("feels more powerful somehow") },
  },
  {
    text = "Step on the exit portal to the west.",
    actions = { Action.ModelHighlight:new(exitPortal) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Climb the staircase in the Varrock Palace.",
    title = "Third key",
    actions = { Action.Direction:new(3212.5, 1653, 3473) },
    postconditions = { Condition.DistanceToWithHeight:new(3212, 2469, 3476, 4) },
  },
  {
    text = "Step on the portal.",
    actions = { Action.Direction:new(3224, 2469, 3470, { tile = true }) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Follow the key rules above for this part.",
    postconditions = { Condition.ChatText:new("feels more powerful somehow") },
  },
  {
    text = "Step on the exit portal to the west.",
    actions = { Action.ModelHighlight:new(exitPortal) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Reldo in the Varrock Palace library.",
    title = "The big reveal",
    neededItems = {
      ["Rune bars"] = { quantity = 3 },
      ["Runite stone spirits"] = { quantity = 2 },
      ["Gleaming energy"] = { quantity = 10 },
      ["Mind runes"] = { quantity = 10 },
    },
    actions = { Action.Direction:new(3212.5, 2469, 3475) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3213, 1253, 3472, 4),
      Condition.DistanceTo:new(3210, 1253, 3494, 8),
    },
  },
  {
    actions = {
      Action.Direction:new(3210, 1253, 3494, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["reldo"], { distance = 8 }),
      Action.ConversationHighlight:new("[Use Charos's Necklace.]"),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("head to the Needle") }, --not correct
  },
  {
    text = "Gear for combat. Deaths here are non-safe. Move to the next step when you're ready.",
    title = "The device",
  },
  {
    text = "Talk to Kerapac at The Needle.",
    actions = {
      Action.Direction:new(2225, 3909, 3657, { distance = 10 }),
      Action.ModelHighlight:new(Models.npcs["kerapac"], { distance = 10 }),
    },
    postconditions = { Condition.ConversationText:new("What's this") },
  },
  {
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ConversationText:new("should look around") },
  },
  {
    text = "Kill the 5 Sliske clones and pick up the fragments.<ul><li>Make sure lootshare is turned off.</li></ul>",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(7, -904, 5, { instance = true }),
      Action.Direction:new(8, -488, -5, { instance = true }),
      Action.Direction:new(19, -1080, -3, { instance = true }),
      Action.Direction:new(29, -2064, 3, { instance = true }),
      Action.Direction:new(29, -1560, 10, { instance = true }),
      Action.ModelHighlight:new(sliskeClone),
      Action.ModelHighlight:new(brokenSliskeMaskShard),
    },
    postconditions = { Condition.InventoryContains:new(brokenSliskeMaskShard, 5) },
  },
  {
    text = "Combine the fragments into Sliske's mask.",
    actions = { Action.InventoryHighlight:new(brokenSliskeMaskShard) },
    postconditions = { Condition.InventoryContains:new(sliskeMask) },
  },
  {
    text = "Walk to the bottom of the platform, wear the mask, and kill Guthix.",
    actions = {
      Action.Direction:new(61, -6280, 1, { instance = true }),
      Action.InventoryHighlight:new(sliskeMask),
      Action.ConversationHighlight:new("Yes - Kill him"),
    },
    postconditions = { Condition.ConversationText:new("Guthix...") },
  },
  {
    text = "Harvest the five memories on Naragun.",
    warning = "Complete each dialogue.",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(12, -576, -2, { instance = true, tile = true }),
    },
    postconditions = { Condition.ConversationText:new("they would live") }, --not tested
  },
  {
    -- text = "debug1",
    actions = { Action.Direction:new(-6, 768, 12, { instance = true, tile = true }) },
    postconditions = { Condition.ConversationText:new("too high a price") }, --not tested
  },
  {
    -- text = "debug2",
    actions = { Action.Direction:new(12, 2208, 33, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(zamorak, { instance = true }) },
  },
  { postconditions = { Condition.ModelNotVisible:new(zamorak) } },
  {
    -- text = "debug3",
    actions = { Action.Direction:new(29, 3152, 47, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(bandos, { instance = true }) },
  },
  { postconditions = { Condition.ModelNotVisible:new(bandos) } },
  {
    -- text = "debug4",
    actions = { Action.Direction:new(-3, 6688, 46, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(saradomin, { instance = true }) },
  },
  { postconditions = { Condition.ModelNotVisible:new(saradomin) } },
  {
    text = "Talk to Guthix near the empty fairy ring.",
    actions = {
      Action.Direction:new(25, 2256, 23, { instance = true, distance = 20 }),
      Action.ModelHighlight:new(guthix, { instanced = true, distance = 20 }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("with my death the Edicts fell") }, --not tested
  },
  {
    text = "Click each mutable anima once so both the green particles will end up on Kerapac.",
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = { Action.ModelHighlight:new(mutableAnima, { instanced = true, highlightPriority = "all" }) },
    postconditions = { Condition.DistanceFrom:new(0, 0, 0, 200, true) },
  },
  {
    text = "Open the door at the end of the path.",
    actions = {
      Action.PathGuide:new({ --not tested
        Location:new(14987, 579, 4105),
        Location:new(14987, 579, 4109),
        Location:new(14987, 1529, 4116),
        Location:new(14977, 1529, 4129),
        Location:new(14966, 2989, 4129),
        Location:new(14966, 2989, 4138),
        Location:new(14963, 2989, 4138),
        Location:new(14962, 2989, 4135),
        Location:new(14961, 2989, 4128),
        Location:new(14959, 2989, 4125),
        Location:new(14962, 2949, 4122),
        Location:new(14962, 2949, 4121),
        Location:new(14957, 2949, 4121),
        Location:new(14956, 2949, 4121),
        Location:new(14950, 2949, 4121),
        Location:new(14944, 1477, 4119),
        Location:new(14944, 1477, 4127),
        Location:new(14947, 2949, 4127),
        Location:new(14948, 2989, 4127),
        Location:new(14950, 2989, 4130),
        Location:new(14950, 2989, 4138),
        Location:new(14950, 2989, 4139),
        Location:new(14956, 2989, 4139),
        Location:new(14956, 4549, 4151),
        Location:new(14956, 4549, 4164),
        Location:new(14958, 4549, 4172),
        Location:new(14978, 5829, 4173),
        Location:new(14998, 5829, 4173),
        Location:new(15007, 5829, 4173),
        Location:new(15007, 4389, 4154),
        Location:new(15010, 4429, 4141),
      }),
    },
    postconditions = { Condition.ConversationText:new("you are doing is wrong") },
  },
  -- {
  --   text = "Jump the gap outside the window on the south-east side.",
  --   actions = {
  --     Action.ResetInstance:new(),
  --     Action.Direction:new(-26, 2370, 16, { instance = true }),
  --   },
  --   postconditions = { Condition.DistanceTo:new(-30, 2370, 16, 1, true) },
  -- },
  -- {
  --   text = "Run across the planks.",
  --   actions = { Action.Direction:new(-31, 2370, 16, { instance = true }) },
  --   postconditions = { Condition.DistanceTo:new(-37, 2370, 16, 1, true) },
  -- },
  -- {
  --   text = "Drop down the rock face.",
  --   actions = { Action.Direction:new(-41, 2370, 14, { instance = true }) },
  --   postconditions = { Condition.DistanceToWithHeight:new(-43, 898, 14, 1, true) },
  -- },
  -- {
  --   text = "Climb the rock face.",
  --   actions = { Action.Direction:new(-42, 1298, 22, { instance = true }) },
  --   postconditions = { Condition.DistanceToWithHeight:new(-40, 2370, 22, 1, true) },
  -- },
  -- {
  --   text = "Climb through the obstacle.",
  --   actions = { Action.Direction:new(-37, 2810, 27, { instance = true }) },
  --   postconditions = { Condition.DistanceTo:new(-37, 2410, 29, 1, true) },
  -- },
  {
    text = "Switch all the green mutable anima.<ul><li>Use temporal rifts to teleport to the top floor.</li></ul>",
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = { Action.ModelHighlight:new(mutableAnima, { instance = true, highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("Losing the song") },
  },
  {
    text = "Redirect the green mutable anima to Kerapac.<ul><li>Harvest all three unrefined anima (red dots on the image) to unlock the three frozen anima. Make sure to finish the dialogue with each NPC.</li><li>Switch the green mutable anima according to the direction on the image. All of these can be clicked while standing on the main floor.</li><li>Disable bloom in graphics settings if it is difficult to see the direction the green mutable anima is facing.</li></ul>",
    warning = "Don't click on the mutable anima until directed to.",
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    -- text = "debug1",
    actions = { Action.Direction:new(11, 1920, 25, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(vicendithas) },
  },
  { postconditions = { Condition.ModelNotVisible:new(vicendithas) } },
  {
    -- text = "debug2",
    actions = { Action.Direction:new(12, 1920, -60, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(vicendithas) },
  },
  { postconditions = { Condition.ModelNotVisible:new(vicendithas) } },
  {
    -- text = "debug3",
    actions = { Action.Direction:new(-10, 2880, -42, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(vicendithas) },
  },
  { postconditions = { Condition.ModelNotVisible:new(vicendithas) } },
  {
    -- text = "debug4",
    actions = { Action.ModelHighlight:new(mutableAnima, { instance = true, highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("I found your notes father") },
  },
  {
    text = "Watch the cutscene.<ul><li>If cutscene doesn't start, talk to Kerapac.</li></ul>",
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Seren in Burthorpe.",
    actions = { Action.Direction:new(2901.5, 2965, 3570) },
    postconditions = { Condition.DistanceToWithHeight:new(2899, 3877, 3567, 24) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["seren"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Desperate Times",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1557705600,
  prereqQuests = { "The Needle Skips", "You Are It" },
  questReqs = {
    Types.QuestReq.skill("Divination", 50),
    Types.QuestReq.skill("Mining", 50),
    Types.QuestReq.skill("Smithing", 50),
  },
  neededItems = {
    ["Rune bars"] = { quantity = 3, model = Models.items["rune bar"], duringQuest = true },
    ["Runite stone spirits"] = { quantity = 2, model = Models.items["runite stone spirit"], duringQuest = true },
    ["Mind runes"] = { quantity = 10, model = Models.items["mind rune"], duringQuest = true },
    ["Gleaming energy"] = { quantity = 10, model = Models.items["gleaming energy"], duringQuest = true },
    ["Old necklace"] = { quantity = 1, model = Models.items["old necklace"], duringQuest = true },
    ["Skull of remembrance"] = { quantity = 1, model = Models.items["skull of remembrance"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = { ["Sliske clone"] = { level = "50", quantity = 5 } },
})
