local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local mysteriousBody = Model.new(8457, {
  [1] = Vertex.new(0, 789, -7, 28, 140, 128),
  [2] = Vertex.new(0, 786, -8, 28, 140, 128),
  [3] = Vertex.new(0, 786, -7, 28, 140, 128),
  [1034] = Vertex.new(130, 462, 9, 127, 127, 128),
  [1040] = Vertex.new(108, 444, -34, 127, 127, 128),
})
local primrose = Model.new(5346, {
  [475] = Vertex.new(0, 673, -7, 28, 140, 128),
  [477] = Vertex.new(0, 670, -8, 28, 140, 128),
  [696] = Vertex.new(-15, 706, -42, 127, 127, 127),
  [834] = Vertex.new(-21, 564, -54, 127, 127, 127),
  [2217] = Vertex.new(21, 564, -54, 127, 127, 127),
})
local megan = Model.new(5709, {
  [589] = Vertex.new(0, 731, -7, 28, 140, 128),
  [590] = Vertex.new(0, 728, -7, 28, 140, 128),
  [591] = Vertex.new(0, 728, -8, 28, 140, 128),
  [5704] = Vertex.new(-120, 386, -10, 28, 140, 31),
  [5705] = Vertex.new(-118, 386, -12, 28, 140, 31),
})
--#endregion
--#region Objects
local fireplace = Model.new(3162, {
  [2637] = Vertex.new(-248, 518, -925, 127, 127, 127),
  [2670] = Vertex.new(-317, 497, -933, 127, 127, 127),
  [2889] = Vertex.new(317, 497, -933, 127, 127, 127),
  [2925] = Vertex.new(248, 518, -925, 127, 127, 127),
  [3111] = Vertex.new(72, 541, -941, 127, 127, 127),
})
local portal = Model.new(4074, {
  [279] = Vertex.new(289, 1580, 465, 254, 254, 254),
  [2958] = Vertex.new(266, 1934, 554, 254, 254, 254),
  [3680] = Vertex.new(490, 1983, 367, 254, 254, 254, 0.3216),
  [3681] = Vertex.new(468, 1977, 392, 254, 254, 254, 0.3216),
  [3738] = Vertex.new(266, 1934, 554, 254, 254, 254, 0.9608),
})
local buckthornBush = Model.new(1032, {
  [96] = Vertex.new(660, 238, 185, 246, 246, 246),
  [267] = Vertex.new(-135, 337, 607, 246, 246, 246),
  [552] = Vertex.new(160, 411, 520, 246, 246, 246),
  [744] = Vertex.new(565, 416, 108, 246, 246, 246),
  [867] = Vertex.new(230, 562, 382, 246, 246, 246),
})
local bookcase = Model.new(2730, {
  [1785] = Vertex.new(-426, 1186, -253, 127, 127, 127),
  [1866] = Vertex.new(-385, 1096, -253, 127, 127, 127),
  [1878] = Vertex.new(-415, 1096, -253, 127, 127, 127),
  [2094] = Vertex.new(-396, 1131, -253, 127, 127, 127),
  [2181] = Vertex.new(400, 1186, -253, 127, 127, 127),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local shardOfTheNeedle = Model.new(78, {
  [19] = Vertex.new(64, 37, -47, 127, 127, 127),
  [52] = Vertex.new(-79, 62, 12, 127, 127, 127),
  [55] = Vertex.new(-79, 62, 12, 127, 127, 127),
  [73] = Vertex.new(-79, 62, 12, 127, 127, 127),
  [76] = Vertex.new(-79, 62, 12, 127, 127, 127),
})
local buckthornLeaf = Model.new(402, {
  [34] = Vertex.new(-76, 4, 64, 99, 54, 51),
  [106] = Vertex.new(-72, 4, 76, 99, 54, 51),
  [128] = Vertex.new(-72, 4, 76, 92, 41, 37),
  [383] = Vertex.new(-56, 4, -24, 99, 54, 51),
  [387] = Vertex.new(-32, -4, -40, 99, 54, 51),
})
local buckthornBerry = Model.new(480, {
  [60] = Vertex.new(-88, 4, -8, 120, 63, 24),
  [62] = Vertex.new(-88, 4, -8, 120, 63, 24),
  [144] = Vertex.new(-80, 4, -60, 120, 63, 24),
  [146] = Vertex.new(-80, 4, -60, 120, 63, 24),
  [396] = Vertex.new(-76, 4, -60, 94, 49, 19),
})
local buckthornSmellingSalts = Model.new(525, {
  [383] = Vertex.new(92, 76, 52, 72, 57, 46),
  [386] = Vertex.new(52, 76, 92, 72, 57, 46),
  [387] = Vertex.new(92, 76, 52, 72, 57, 46),
  [389] = Vertex.new(92, 76, 52, 72, 57, 46),
  [392] = Vertex.new(52, 76, 92, 72, 57, 46),
})
local bottleOfCrushedBerries = Model.multi({
  Model.new(582, {
    [1] = Vertex.new(15, 129, 41, 73, 80, 80),
    [4] = Vertex.new(22, 129, 20, 69, 75, 75),
    [7] = Vertex.new(23, 117, 34, 64, 70, 70),
    [245] = Vertex.new(38, 61, 14, 124, 38, 43),
    [248] = Vertex.new(59, 36, 28, 124, 38, 43),
    [260] = Vertex.new(36, 36, 65, 124, 38, 43),
  }),
  Model.new(108, {
    [13] = Vertex.new(6, 90, 30, 93, 100, 101, 0.8039),
    [21] = Vertex.new(11, 90, 39, 93, 100, 101, 0.8039),
    [22] = Vertex.new(15, 90, 41, 93, 100, 101, 0.8039),
    [24] = Vertex.new(11, 90, 39, 93, 100, 101, 0.8039),
    [30] = Vertex.new(20, 90, 39, 93, 100, 101, 0.8039),
  }),
})
local buckthronSalve = Model.multi({
  Model.new(582, {
    [251] = Vertex.new(45, 61, 27, 137, 82, 28),
    [254] = Vertex.new(50, 36, 51, 137, 82, 28),
    [257] = Vertex.new(38, 61, 42, 137, 82, 28),
    [260] = Vertex.new(36, 36, 65, 137, 82, 28),
    [263] = Vertex.new(26, 61, 50, 137, 82, 28),
  }),
  Model.new(108, {
    [13] = Vertex.new(6, 90, 30, 93, 100, 101, 0.8039),
    [21] = Vertex.new(11, 90, 39, 93, 100, 101, 0.8039),
    [22] = Vertex.new(15, 90, 41, 93, 100, 101, 0.8039),
    [24] = Vertex.new(11, 90, 39, 93, 100, 101, 0.8039),
    [30] = Vertex.new(20, 90, 39, 93, 100, 101, 0.8039),
  }),
})
local noteFromGail = Model.new(219, {
  [3] = Vertex.new(-66, 7, 66, 102, 93, 78),
  [21] = Vertex.new(-74, 7, 54, 102, 93, 78),
  [36] = Vertex.new(-108, 7, -90, 102, 93, 78),
  [108] = Vertex.new(-54, 8, 69, 51, 47, 46),
  [209] = Vertex.new(-74, 8, -49, 51, 47, 46),
})
--#endregion

local bookTexture =
  "\x39\x1c\x0b\x00\x6b\x34\x12\x25\x94\x52\x22\xff\x93\x4b\x1b\xff\x8b\x49\x1b\xff\x94\x52\x22\xff\x83\x41\x14\xff\xa2\x5a\x25\xff\x9a\x54\x21\xff\x4b\x23\x0a\xff\x53\x29\x0c\xff\xbc\x8b\x4b\xff\xde\xac\x62\xff\xe5\xb3\x5b\xff\xe3\xb2\x53\xff\xe1\xa4\x44\xff\xdc\xa3\x44\xff\xda\xa3\x4b\xff\xe3\xab\x52\xff\xe5\xb4\x63\xff\xec\xc4\x7b\xff\xeb\xc4\x83\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xe4\xbd\x8b\xff\xe4\xbd\x8b\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xe3\xc3\x8b\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd3\xa0\xff\xf3\xda\xa4\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe6\xca\x90\xff\xe3\xc3\x8b\xff\xe6\xca\x90\xff\xeb\xcb\x93\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe3\xc3\x8b\xff\xdc\xbf\x89\xff\xdc\xbf\x89\xff\xdc\xbf\x89\xff\xd2\xb4\x82\xff\xd5\xb9\x85\xff\xdc\xbf\x89\xff\xdc\xbf\x89\xff\xd5\xb9\x85\xff\xd2\xb4\x82\xff\xd2\xb4\x82\xff\xd2\xb4\x82\xff\xd4\xb4\x7b\xff\xcd\xb1\x7c\xff\xcb\xac\x7a\xff\xc4\xa3\x73\xff\xc4\xa3\x73\xff\xc4\xa3\x73\xff\xc5\x9c\x62\xff\xc3\x94\x52\xff\xc2\x8d\x4a\xff\xbc\x8b\x4b\xff\xbc\x8b\x4b\xff\xbc\x84\x42\xff\xb2\x74\x3a\xff\xb4\x73\x33\xff\xba\x7c\x3a\xff\xbc\x84\x42\xff\xbf\x93\x5b\xff\x9f\x76\x46\xff\x63\x3a\x1b\xff\x24\x14\x09\xff\x24\x14\x09\xff\x63\x3a\x1b\xff\x9f\x76\x46\xff\xbf\x93\x5b\xff\xbc\x84\x42\xff\xba\x7c\x3a\xff\xb4\x73\x33\xff\xb2\x74\x3a\xff\xbc\x84\x42\xff\xbc\x8b\x4b\xff\xbc\x8b\x4b\xff\xc2\x8d\x4a\xff\xc3\x94\x52\xff\xc5\x9c\x62\xff\xc4\xa3\x73\xff\xc4\xa3\x73\xff\xc4\xa3\x73\xff\xcb\xac\x7a\xff\xcd\xb1\x7c\xff\xd4\xb4\x7b\xff\xd2\xb4\x82\xff\xd2\xb4\x82\xff\xd2\xb4\x82\xff\xd5\xb9\x85\xff\xdc\xbf\x89\xff\xdc\xbf\x89\xff\xd5\xb9\x85\xff\xd2\xb4\x82\xff\xdc\xbf\x89\xff\xdc\xbf\x89\xff\xdc\xbf\x89\xff\xe3\xc3\x8b\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xeb\xcb\x93\xff\xe6\xca\x90\xff\xe3\xc3\x8b\xff\xe6\xca\x90\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf3\xda\xa4\xff\xee\xd3\xa0\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe3\xc3\x8b\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe4\xbd\x8b\xff\xe4\xbd\x8b\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xc4\x83\xff\xec\xc4\x7b\xff\xe5\xb4\x63\xff\xe3\xab\x52\xff\xda\xa3\x4b\xff\xdc\xa3\x44\xff\xe1\xa4\x44\xff\xe3\xb2\x53\xff\xe5\xb3\x5b\xff\xde\xac\x62\xff\xbc\x8b\x4b\xff\x53\x29\x0c\xff\x4b\x23\x0a\xff\x9a\x54\x21\xff\xa2\x5a\x25\xff\x83\x41\x14\xff\x94\x52\x22\xff\x8b\x49\x1b\xff\x93\x4b\x1b\xff\x94\x52\x22\xff\x6b\x34\x12\x25\x39\x1c\x0b\x00"

---@type QuestStep[]
local steps = {
  {
    text = "Search the needle on the hill west of Piscatoris.<ul><li>Phoenix Lair Teleport scroll.</li><li>Teleport from the memory strand in the currency pouch.</li><li>Fairy ring code AKQ.</li><li>Eagles' Peak lodestone.</li></ul>",
    title = "Starting out",
    actions = {
      Action.Direction:new(2223, 4893, 3657, { distance = 16 }),
      Action.ModelHighlight:new(Models.objects["the needle"], { distance = 16 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("get the most out of it") } },
  {
    text = "Investigate the body on the ground.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(mysteriousBody),
    },
    postconditions = { Condition.ConversationText:new("Megan") },
  },
  {
    text = "Search the needle.",
    title = "Chapter 1",
    actions = {
      Action.Direction:new(2223, 4893, 3657, { distance = 16 }),
      Action.ModelHighlight:new(Models.objects["the needle"], { distance = 16 }),
    },
    postconditions = { Condition.InventoryContains:new(shardOfTheNeedle) },
  },
  {
    text = "Commune with the shard. Word: <b>fire</b>.",
    actions = { Action.InventoryHighlight:new(shardOfTheNeedle) },
    postconditions = { Condition.ChatText:new("fireplace has been") },
  },
  {
    text = "Go into the house to the north-west and interact/explore the fireplace.<ul><li>Optional: For full completion, find all of the other words listed on The Needle Skips#Chapter 1.</li></ul>",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(-27, -3088, 39, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(fireplace, { instance = true, distance = 12 }),
    },
    postconditions = { Condition.ChatText:new("start the next chapter") },
  },
  {
    text = "Enter the temporal portal next to the needle.",
    actions = {
      Action.Direction:new(4, -312, 4, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(portal, { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Continue to chapter 2"),
    },
    postconditions = { Condition.ConversationText:new("jiggle") },
  },
  {
    text = "Commune with the shard. Word: <b>birthday</b>.",
    title = "Chapter 2",
    actions = {
      Action.ResetInstance:new(),
      Action.InventoryHighlight:new(shardOfTheNeedle),
    },
    postconditions = { Condition.ChatText:new("What have you done") },
  },
  {
    text = "Descend the stairs to the east and go back to the Needle.",
    actions = { Action.Direction:new(7.5, -50, 0, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(7, -1664, -4, 1, true) },
  },
  {
    text = "Enter the temporal portal.",
    actions = {
      Action.Direction:new(31, 1112, -33, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(portal, { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Continue to chapter 3"),
    },
    postconditions = { Condition.ConversationText:new("someone") },
  },
  {
    text = "Commune with the shard. Word: <b>wenla</b>.<ul><li>For full completion, find all of the other words listed on The Needle Skips#Chapter 3.</li></ul>",
    title = "Chapter 3",
    actions = {
      Action.ResetInstance:new(),
      Action.InventoryHighlight:new(shardOfTheNeedle),
    },
    postconditions = { Condition.ChatText:new("discovered the word wenla") },
  },
  {
    text = "Go back to the Needle and search it.",
    actions = {
      Action.Direction:new(26, 3788, -19.8, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(Models.objects["the needle"], { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("area snaps back") },
  },
  {
    text = "Run back towards the house and harvest the buckthorn bush near the river.",
    title = "The finale",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(buckthornBush, { instance = true }),
    },
    postconditions = {
      Condition.InventoryContains:new(buckthornLeaf),
      Condition.InventoryContains:new(buckthornBerry),
    },
  },
  {
    text = "Grind the leaf.",
    actions = { Action.InventoryHighlight:new(buckthornLeaf) },
    postconditions = { Condition.InventoryContains:new(buckthornSmellingSalts) },
  },
  {
    text = "Crush the berries.",
    actions = { Action.InventoryHighlight:new(buckthornBerry) },
    postconditions = { Condition.InventoryContains:new(bottleOfCrushedBerries) },
  },
  {
    text = "Go upstairs in the house.",
    actions = { Action.Direction:new(-19.5, -2688, 34, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-20, -1424, 38, 4, true) },
  },
  {
    text = "Give Primrose the smelling salts.",
    actions = {
      Action.ModelHighlight:new(primrose, { instanced = true }),
      Action.InventoryHighlight:new(buckthornSmellingSalts),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("not safe by herself") },
  },
  {
    text = "Go back downstairs.",
    actions = { Action.Direction:new(-19.5, -2024, 37, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-20, -3088, 33, 4, true) },
  },
  {
    text = "Search the bookcase to the north-east.",
    actions = { Action.ModelHighlight:new(bookcase, { instanced = true }) },
    postconditions = { Condition.Generic2DVisible:new(482, 290, 100, bookTexture) },
  },
  {
    text = "Talk to Megan and name the demon 'Metum'.",
    actions = {
      Action.ModelHighlight:new(megan, { instanced = true }),
      Action.ConversationHighlight:new("[Name the demon]"),
      Action.ConversationHighlight:new("Metum"),
    },
    postconditions = { Condition.ConversationText:new("And Gail") },
  },
  {
    text = "Use the bottle of crushed berries on the stove on the west side of the house.",
    actions = {
      Action.Direction:new(-34, -2688, 35, { instance = true }),
      Action.InventoryHighlight:new(bottleOfCrushedBerries),
    },
    postconditions = { Condition.InventoryContains:new(buckthronSalve) },
  },
  {
    text = "Use the buckthorn salve on Gail.",
    actions = {
      Action.Direction:new(1, 16, 0, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["gail"], { instance = true, distance = 12 }),
      Action.InventoryHighlight:new(buckthronSalve),
    },
    postconditions = { Condition.DistanceTo:new(-27, -3088, 35, 4, true) },
  },
  { text = "Finish the dialogue.", postconditions = { Condition.ConversationText:new("you both wait in the house") } },
  {
    text = "Talk to Megan.",
    actions = { Action.ModelHighlight:new(megan, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("needle") },
  },
  {
    text = "Pick up the note from gail near the Needle.",
    actions = {
      Action.Direction:new(0, 0, 1, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(noteFromGail, { instanced = true, distance = 12 }),
    },
    postconditions = { Condition.InventoryContains:new(noteFromGail) },
  },
  {
    text = "Talk to Megan inside the house.",
    actions = {
      Action.Direction:new(-28, -3088, 36, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(megan, { instanced = true, distance = 12 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Needle Skips",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1542585600,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
