local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local koftik = Model.new(4449, {
  [2815] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [2820] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [2823] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2825] = Vertex.new(7, 724, -51, 108, 80, 56),
  [3175] = Vertex.new(0, 735, -7, 29, 141, 128),
})
local elfTracker = Model.new(4053, {
  [2161] = Vertex.new(-31, 724, 3, 128, 127, 127),
  [2221] = Vertex.new(31, 724, 3, 128, 127, 127),
  [3516] = Vertex.new(26, 688, -33, 127, 127, 127),
  [3645] = Vertex.new(-26, 688, -33, 127, 127, 127),
  [4045] = Vertex.new(0, 735, -7, 27, 139, 126),
})
--#endregion
--#region Objects
local skybeamWell = Model.new(2028, {
  [869] = Vertex.new(-416, 948, 32, 192, 200, 193),
  [881] = Vertex.new(436, 948, 24, 192, 200, 193),
  [887] = Vertex.new(436, 948, 24, 192, 200, 193),
  [903] = Vertex.new(424, 948, 24, 192, 200, 193),
  [915] = Vertex.new(436, 948, 24, 192, 200, 193),
})
local caveEntranceLightBeam = Model.new(60, {
  [14] = Vertex.new(3072, 2080, 4736, 131, 121, 82, 0.1373),
  [15] = Vertex.new(4762, 862, 4827, 131, 121, 82, 0.1373),
  [37] = Vertex.new(3072, 2216, 3456, 131, 121, 82, 0.1373),
  [39] = Vertex.new(4762, 963, 3365, 131, 121, 82, 0.1373),
  [54] = Vertex.new(3072, 2176, 4096, 131, 121, 82, 0.6078),
})
local catapult = Model.any({
  Model.new(3639, { --right
    [1253] = Vertex.new(12, 1576, 1228, 132, 94, 11),
    [1263] = Vertex.new(24, 1568, 1232, 124, 98, 10),
    [1265] = Vertex.new(0, 1576, 1228, 132, 130, 11),
    [1274] = Vertex.new(28, 1576, 1204, 124, 98, 10),
    [1277] = Vertex.new(24, 1568, 1232, 124, 98, 10),
  }),
  Model.new(3639, { --middle
    [1253] = Vertex.new(12, 1576, 1228, 132, 94, 11),
    [1263] = Vertex.new(24, 1568, 1232, 124, 98, 10),
    [1265] = Vertex.new(0, 1576, 1228, 132, 130, 11),
    [1274] = Vertex.new(28, 1576, 1204, 124, 98, 10),
    [1277] = Vertex.new(24, 1568, 1232, 124, 98, 10),
  }),
  Model.new(2916, { --left
    [27] = Vertex.new(-88, 276, 568, 118, 102, 74),
    [339] = Vertex.new(-256, 1284, -520, 93, 90, 85),
    [340] = Vertex.new(-256, 1284, -520, 93, 90, 85),
    [396] = Vertex.new(-256, 284, -388, 105, 91, 66),
    [421] = Vertex.new(-256, 356, -396, 94, 81, 59),
  }),
})
--#endregion
--#region Items
local shortbow = Model.new(144, { --all shortbows
  [3] = Vertex.new(-128, 16, -120, 97, 89, 89),
  [11] = Vertex.new(-128, 16, -120, 97, 89, 89),
})
local metalArrows = Model.new(537, { --all metal arrows I think?
  [54] = Vertex.new(-88, 0, -200, 69, 39, 6),
  [140] = Vertex.new(20, 0, -224, 91, 15, 8),
  [212] = Vertex.new(48, 0, -168, 91, 15, 8),
  [284] = Vertex.new(-12, 0, -148, 91, 15, 8),
  [356] = Vertex.new(-48, 0, -188, 91, 15, 8),
})
local stripOfCloth = Model.new(96, {
  [3] = Vertex.new(48, 0, -84, 130, 130, 142),
  [47] = Vertex.new(-32, 0, 168, 130, 130, 142),
  [49] = Vertex.new(-32, -4, 168, 143, 128, 90),
  [51] = Vertex.new(120, -4, 132, 143, 128, 90),
  [96] = Vertex.new(48, -4, -84, 143, 128, 90),
})
local limestone = Model.new(264, {
  [169] = Vertex.new(216, 0, -100, 88, 88, 80),
  [181] = Vertex.new(216, 0, -100, 88, 88, 80),
  [184] = Vertex.new(216, 0, -100, 88, 88, 80),
  [186] = Vertex.new(252, -4, -52, 88, 88, 80),
  [233] = Vertex.new(252, -4, -52, 88, 88, 80),
})
local cookedRabbit = Model.new(591, {
  [404] = Vertex.new(-36, 36, 4, 113, 64, 45),
  [570] = Vertex.new(-12, 68, -16, 113, 64, 45),
  [573] = Vertex.new(-12, 68, -16, 113, 64, 45),
  [585] = Vertex.new(-12, 68, 16, 113, 64, 45),
  [591] = Vertex.new(-12, 68, 16, 113, 64, 45),
})
--#endregion
--#region Quest Items
local kingsMessage = Model.new(372, {
  [3] = Vertex.new(20, 0, -72, 146, 146, 134),
  [5] = Vertex.new(-72, 0, -56, 146, 146, 134),
  [187] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [190] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [195] = Vertex.new(-84, 28, -84, 89, 63, 7),
})
local dampCloth = Model.new(96, {
  [3] = Vertex.new(48, 0, -84, 59, 49, 38),
  [47] = Vertex.new(-32, 0, 168, 59, 49, 38),
  [49] = Vertex.new(-32, -4, 168, 145, 130, 92),
  [51] = Vertex.new(120, -4, 132, 145, 130, 92),
  [96] = Vertex.new(48, -4, -84, 145, 130, 92),
})
local fireArrow = Model.new(96, {
  [3] = Vertex.new(24, 0, -160, 91, 15, 8),
  [9] = Vertex.new(-40, 0, -120, 91, 15, 8),
  [15] = Vertex.new(-16, 0, 148, 69, 39, 6),
  [26] = Vertex.new(-12, 0, 208, 106, 98, 97),
  [96] = Vertex.new(4, 0, 112, 106, 98, 97),
})
local litFireArrow = Model.multi({
  Model.new(39, {
    [18] = Vertex.new(-32, 0, -120, 91, 15, 8),
    [24] = Vertex.new(16, 0, -160, 91, 15, 8),
  }),
  Model.new(174, {
    [35] = Vertex.new(-24, 0, 192, 111, 80, 10, 0.5608),
    [38] = Vertex.new(-24, 0, 192, 111, 80, 10, 0.5608),
  }),
})
local crystalPendant = Model.new(198, {
  [182] = Vertex.new(-4, 12, -108, 11, 128, 14),
  [183] = Vertex.new(-8, 12, -100, 11, 128, 14),
  [187] = Vertex.new(4, 12, -108, 11, 128, 14),
  [188] = Vertex.new(-4, 12, -108, 11, 128, 14),
  [195] = Vertex.new(4, 12, -108, 11, 128, 14),
})
local barrel = Model.new(354, {
  [1] = Vertex.new(-76, 288, 80, 102, 80, 37),
  [3] = Vertex.new(-64, 288, 72, 102, 80, 37),
  [4] = Vertex.new(-76, 288, 80, 102, 80, 37),
  [11] = Vertex.new(-76, 288, 80, 102, 80, 37),
  [45] = Vertex.new(76, 288, 80, 102, 80, 37),
})
local theBigBookOfBangs = Model.new(204, {
  [87] = Vertex.new(36, 36, -84, 80, 45, 7),
  [159] = Vertex.new(36, 36, -84, 109, 108, 9),
  [171] = Vertex.new(64, 36, 56, 109, 108, 9),
  [201] = Vertex.new(-32, 52, 32, 128, 98, 128),
  [203] = Vertex.new(32, 52, 52, 128, 98, 128),
})
local barrelOfTar = Model.new(414, {
  [237] = Vertex.new(76, 288, 60, 22, 22, 28),
  [282] = Vertex.new(80, 316, 32, 22, 22, 28),
  [287] = Vertex.new(80, 316, 32, 22, 22, 28),
  [289] = Vertex.new(80, 316, 32, 22, 22, 28),
  [292] = Vertex.new(80, 316, 32, 22, 22, 28),
})
local sulphur = Model.new(243, {
  [21] = Vertex.new(-104, 0, -168, 106, 106, 54),
  [26] = Vertex.new(-104, 0, -168, 106, 106, 54),
  [32] = Vertex.new(-104, 0, -168, 106, 106, 54),
  [33] = Vertex.new(-132, 0, -96, 106, 106, 54),
  [38] = Vertex.new(-132, 0, -96, 106, 106, 54),
})
local groundSulphur = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 152, 151, 13),
  [34] = Vertex.new(100, 0, 60, 152, 151, 13),
  [42] = Vertex.new(100, 0, 60, 152, 151, 13),
  [64] = Vertex.new(132, 0, -24, 152, 151, 13),
  [72] = Vertex.new(132, 0, -24, 152, 151, 13),
})
local quicklime = Model.new(264, {
  [169] = Vertex.new(216, 0, -100, 159, 146, 159),
  [181] = Vertex.new(216, 0, -100, 159, 146, 159),
  [184] = Vertex.new(216, 0, -100, 159, 146, 159),
  [186] = Vertex.new(252, -4, -52, 159, 146, 159),
  [233] = Vertex.new(252, -4, -52, 159, 146, 159),
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
local potOfQuicklime = Model.new(378, {
  [152] = Vertex.new(-28, 176, -68, 95, 60, 28),
  [156] = Vertex.new(-28, 176, -68, 95, 60, 28),
  [185] = Vertex.new(-28, 176, -68, 95, 60, 28),
  [192] = Vertex.new(-28, 176, -68, 95, 60, 28),
  [194] = Vertex.new(-28, 176, -68, 95, 60, 28),
})
local barrelOfNaphthaMix = Model.multi({
  Model.new(354, {
    [122] = Vertex.new(96, 128, 100, 84, 64, 25),
    [147] = Vertex.new(-96, 128, 100, 84, 64, 25),
    [163] = Vertex.new(-76, 288, 80, 84, 64, 25),
    [164] = Vertex.new(0, 288, 108, 84, 64, 25),
    [166] = Vertex.new(-76, 288, 80, 84, 64, 25),
  }),
  Model.new(18, {
    [1] = Vertex.new(0, 232, -104, 152, 151, 13, 0.5608),
    [2] = Vertex.new(108, 232, 4, 152, 151, 13, 0.5608),
    [3] = Vertex.new(80, 232, -72, 152, 151, 13, 0.5608),
    [5] = Vertex.new(76, 232, 84, 152, 151, 13, 0.5608),
    [8] = Vertex.new(0, 232, 112, 152, 151, 13, 0.5608),
  }),
})
local barrelBomb = Model.new(210, {
  [145] = Vertex.new(-76, 308, -72, 61, 56, 55),
  [148] = Vertex.new(-76, 308, -72, 61, 56, 55),
  [156] = Vertex.new(-76, 308, -72, 61, 56, 55),
  [158] = Vertex.new(-76, 308, -72, 61, 56, 55),
  [172] = Vertex.new(76, 308, -72, 61, 56, 55),
})
--#endregion

local lodestoneToIowerth1 = Action.PathGuide:new({
  Location:new(2252, 1173, 3151),
  Location:new(2241, 1117, 3162),
  Location:new(2238, 885, 3168),
  Location:new(2233, 1093, 3173),
  Location:new(2231, 901, 3178),
  Location:new(2223, 621, 3181),
  Location:new(2220, 885, 3185),
  Location:new(2218, 1029, 3189),
  Location:new(2211, 725, 3197),
  Location:new(2209, 485, 3201),
})
local lodestoneToIowerth2 = Action.PathGuide:new({
  Location:new(2209, 485, 3205),
  Location:new(2209, 533, 3209),
  Location:new(2206, 717, 3213),
  Location:new(2204, 805, 3219),
  Location:new(2204, 533, 3225),
  Location:new(2203, 373, 3230),
  Location:new(2202, 261, 3237),
})
local lodestoneToIowerth3 = Action.PathGuide:new({
  Location:new(2196, 317, 3237),
  Location:new(2192, 741, 3241),
  Location:new(2194, 979, 3245),
  Location:new(2196, 1221, 3251),
  Location:new(2204, 1109, 3252),
})
local lodestoneToMushroomPatch = Action.PathGuide:new({
  Location:new(2252, 1189, 3149),
  Location:new(2243, 1165, 3150),
  Location:new(2240, 981, 3150),
  Location:new(2240, 973, 3149),
  Location:new(2231, 861, 3149),
})
local mushroomToTyrasCamp1 = Action.PathGuide:new({
  Location:new(2222, 901, 3146),
  Location:new(2220, 1061, 3149),
  Location:new(2220, 965, 3152),
})
local mushroomToTyrasCamp2 = Action.PathGuide:new({
  Location:new(2220, 901, 3155),
  Location:new(2217, 869, 3158),
  Location:new(2217, 869, 3160),
  Location:new(2217, 869, 3169),
  Location:new(2213, 909, 3175),
  Location:new(2209, 1037, 3177),
  Location:new(2204, 1293, 3178),
  Location:new(2194, 909, 3178),
  Location:new(2189, 965, 3175),
  Location:new(2188, 1173, 3171),
  Location:new(2188, 1333, 3162),
  Location:new(2188, 2365, 3158),
  Location:new(2188, 2565, 3154),
  Location:new(2188, 2565, 3151),
})

local parchmentTexture =
  "\x03\x0a\x0d\xff\x0a\x14\x18\xff\x1a\x20\x20\xff\x78\x59\x3b\xff\x78\x55\x37\xff\x7d\x5b\x3b\xff\x99\x77\x52\xff\xac\x89\x60\xff\xb1\x8b\x61\xff\xb6\x93\x66\xff\xac\x85\x5c\xff\x79\x5d\x3c\xff\x6f\x57\x38\xff\x7b\x63\x40\xff\x85\x69\x47\xff\x8d\x70\x49\xff\x9f\x81\x57\xff\xa4\x83\x58\xff\xa5\x80\x54\xff\xac\x88\x5c\xff\xb1\x8d\x60\xff\xb5\x94\x68\xff\xb9\x98\x6b\xff\xbc\x9b\x6c\xff\xbd\x9b\x6c\xff\xbe\x9d\x6e\xff\xbf\x9b\x6b\xff\xc1\x9d\x6e\xff\xc2\x9e\x70\xff\xc3\x9d\x6d\xff\xc3\x9d\x6f\xff\xc6\xa3\x75\xff\xc8\xa6\x78\xff\xc9\xa6\x79\xff\xcb\xa9\x7a\xff\xcc\xa9\x7b\xff\xcf\xab\x7d\xff\xcf\xaa\x79\xff\xce\xaa\x7a\xff\xd1\xaf\x81\xff\xd2\xb0\x80\xff\xd6\xb6\x87\xff\xd7\xb8\x89\xff\xd7\xb6\x86\xff\xd8\xb7\x86\xff\xd7\xb9\x8a\xff\xd6\xb5\x86\xff\xd6\xb7\x87\xff\xd9\xbd\x8f\xff\xda\xbd\x90\xff\xdc\xbe\x91\xff\xdd\xbf\x90\xff\xdd\xbe\x8d\xff\xdb\xbb\x8b\xff\xdb\xbc\x8a\xff\xd7\xb6\x84\xff\xda\xb8\x86\xff\xd9\xb6\x85\xff\xd9\xb6\x83\xff\xdb\xba\x8c\xff\xdb\xb9\x87\xff\xd8\xb6\x84\xff\xda\xb9\x87\xff\xdd\xbc\x8a\xff\xdd\xbb\x89\xff\xdc\xbb\x88\xff\xdd\xbb\x88\xff\xdc\xb9\x87\xff\xda\xb6\x81\xff\xda\xb7\x80\xff\xda\xb7\x82\xff\xdb\xba\x85\xff\xdd\xbb\x88\xff\xde\xbd\x89\xff\xde\xbe\x8a\xff\xdf\xbf\x8b\xff\xe0\xbf\x8c\xff\xdf\xbe\x8c\xff\xdf\xbf\x8d\xff\xdd\xbe\x8a\xff\xdf\xc0\x8d\xff\xdf\xc0\x8e\xff\xde\xbe\x8e\xff\xdc\xbc\x8b\xff\xdc\xbd\x8c\xff\xdd\xbf\x8d\xff\xdb\xbc\x8a\xff\xdd\xbe\x8e\xff\xdc\xbc\x89\xff\xdb\xbc\x87\xff\xde\xc0\x8e\xff\xde\xbf\x8e\xff\xdd\xbb\x89\xff\xdd\xbb\x88\xff\xde\xbd\x8c\xff\xe1\xbf\x8e\xff\xe3\xc2\x91\xff\xe5\xc7\x97\xff\xe6\xc8\x99\xff\xe6\xc9\x99\xff\xe3\xc5\x92\xff\xe4\xc6\x94\xff\xe5\xc8\x98\xff\xe8\xcc\x9d\xff\xea\xce\xa0\xff\xe9\xce\xa1\xff\xe9\xcd\x9e\xff\xe7\xca\x9c\xff\xe9\xcd\x9e\xff\xe8\xcb\x9b\xff\xe8\xcb\x9b\xff\xea\xcf\x9f\xff\xe9\xce\x9e\xff\xe9\xcd\x9d\xff\xe9\xcd\x9d\xff\xea\xd0\x9d\xff\xeb\xcf\x9e\xff\xea\xcf\x9d\xff\xea\xce\x9c\xff\xeb\xcf\x9d\xff\xea\xce\x9a\xff\xec\xcf\x9b\xff\xec\xcf\x9d\xff\xec\xd0\x9d\xff\xeb\xd2\xa7\xff\xeb\xd2\xab\xff\xeb\xd4\xae\xff\xeb\xce\xa0\xff\xeb\xcd\x9e\xff\xee\xcf\xa0\xff\xee\xcf\xa0\xff\xee\xcf\xa1\xff\xea\xcc\x9b\xff\xe8\xc9\x97\xff\xe9\xca\x98\xff\xe7\xc7\x95\xff\xe6\xc4\x92\xff\xe7\xc4\x93\xff\xe8\xc5\x97\xff\xe7\xc5\x96\xff\xe8\xc6\x94\xff\xe7\xc7\x96\xff\xe8\xc6\x96\xff\xe6\xc7\x95\xff\xe8\xc9\x99\xff\xe5\xc2\x92\xff\xe6\xc5\x96\xff\xe6\xc7\x94\xff\xe6\xc6\x93\xff\xe7\xc8\x96\xff\xe6\xc7\x93\xff\xe6\xc7\x92\xff\xe6\xc9\x94\xff\xe6\xc9\x96\xff\xe4\xc7\x95\xff\xe3\xc7\x94\xff\xe4\xc8\x96\xff\xe2\xc4\x90\xff\xe3\xc5\x92\xff\xe4\xc5\x92\xff\xe3\xc2\x91\xff\xe4\xc5\x96\xff\xe3\xc5\x94\xff\xe5\xc7\x99\xff\xe2\xc4\x93\xff\xe3\xc6\x97\xff\xe5\xc6\x99\xff\xe4\xc4\x97\xff\xe6\xc6\x99\xff\xe6\xc9\x99\xff\xe7\xcb\x9c\xff\xe6\xc9\x99\xff\xe6\xc8\x98\xff\xe5\xc7\x97\xff\xe6\xc7\x9a\xff\xe6\xc7\x9b\xff\xe5\xc5\x99\xff\xe4\xc4\x97\xff\xe4\xc5\x97\xff\xe4\xc5\x96\xff\xe3\xc6\x95\xff\xe4\xc7\x96\xff\xe3\xc5\x95\xff\xe1\xc2\x90\xff\xdf\xbe\x8d\xff\xe0\xc3\x91\xff\xde\xbf\x90\xff\xe0\xbf\x91\xff\xe0\xbb\x8b\xff\xe0\xbc\x8a\xff\xe1\xbc\x8e\xff\xe3\xc1\x94\xff\xe4\xc1\x94\xff\xe5\xc4\x97\xff\xe2\xc5\x97\xff\xe1\xc3\x95\xff\xe3\xc5\x95\xff\xe6\xc8\x97\xff\xe7\xcd\xa1\xff\xe8\xcf\xa3\xff\xe8\xd2\xab\xff\xe7\xd2\xad\xff\xe9\xd1\xa8\xff\xe9\xcd\x9b\xff\xe8\xcd\x9a\xff\xe9\xce\x9d\xff\xe9\xcc\x9b\xff\xe9\xcc\x9b\xff\xe8\xcb\x9a\xff\xe8\xca\x99\xff\xe9\xcd\x9e\xff\xe8\xca\x97\xff\xe7\xc9\x93\xff\xe8\xc9\x97\xff\xea\xcb\x9d\xff\xec\xce\xa0\xff\xec\xcc\x9e\xff\xea\xcc\x9f\xff\xe9\xc9\x9b\xff\xe8\xc8\x9b\xff\xe9\xc8\x9b\xff\xe8\xc6\x98\xff\xe9\xc8\x98\xff\xe9\xca\x98\xff\xe8\xc8\x98\xff\xe7\xc6\x96\xff\xe9\xc9\x9a\xff\xe8\xc6\x96\xff\xe5\xc2\x92\xff\xe4\xc3\x92\xff\xe5\xc5\x95\xff\xe5\xc4\x93\xff\xe6\xc5\x95\xff\xe5\xc5\x96\xff\xe5\xc6\x99\xff\xe6\xc7\x9b\xff\xe5\xc7\x99\xff\xe5\xc8\x9d\xff\xe4\xc7\x9c\xff\xe2\xc4\x98\xff\xe3\xc4\x9a\xff\xe2\xc3\x98\xff\xe0\xbf\x92\xff\xe3\xc4\x99\xff\xe4\xc5\x9a\xff\xe4\xc5\x9a\xff\xe4\xc5\x97\xff\xe6\xc6\x9a\xff\xe5\xc2\x92\xff\xe5\xc3\x96\xff\xe5\xc3\x96\xff\xe2\xbe\x91\xff\xe3\xc2\x93\xff\xe1\xc2\x92\xff\xe1\xc5\x98\xff\xe3\xc6\x97\xff\xe2\xc5\x94\xff\xde\xbf\x8c\xff\xdf\xbf\x8f\xff\xdf\xbd\x8d\xff\xdd\xb7\x8c\xff\xdf\xbc\x8c\xff\xe2\xc0\x91\xff\xe3\xc1\x93\xff\xe2\xc0\x93\xff\xe2\xc0\x92\xff\xe2\xbf\x92\xff\xe2\xbf\x91\xff\xe1\xbf\x91\xff\xe2\xbf\x91\xff\xe3\xc1\x93\xff\xe3\xc0\x92\xff\xe1\xbc\x8f\xff\xe4\xc0\x91\xff\xe4\xc2\x92\xff\xe2\xc2\x8e\xff\xe1\xc1\x8b\xff\xe3\xc4\x92\xff\xe1\xc2\x90\xff\xe0\xc0\x8e\xff\xdf\xbf\x8c\xff\xdf\xbe\x8b\xff\xe0\xbe\x8e\xff\xde\xba\x8a\xff\xdc\xb6\x87\xff\xdd\xba\x8d\xff\xde\xbc\x8e\xff\xdc\xba\x8a\xff\xd8\xb5\x83\xff\xdc\xb8\x88\xff\xde\xbc\x8d\xff\xdf\xc0\x90\xff\xe1\xc4\x93\xff\xe0\xc4\x91\xff\xde\xc2\x8d\xff\xe1\xc5\x91\xff\xdf\xc4\x91\xff\xe3\xc7\x96\xff\xe2\xc7\x95\xff\xe1\xc5\x96\xff\xe0\xc5\x95\xff\xdf\xc3\x95\xff\xdc\xc1\x8e\xff\xdc\xc0\x90\xff\xde\xc2\x93\xff\xdc\xbf\x8d\xff\xdc\xbe\x8d\xff\xdc\xbb\x8a\xff\xdc\xb8\x88\xff\xdb\xb6\x84\xff\xdd\xb8\x89\xff\xdb\xb8\x87\xff\xdb\xb9\x85\xff\xdc\xba\x88\xff\xde\xbe\x8d\xff\xde\xbe\x8d\xff\xdb\xbb\x88\xff\xdd\xbc\x8a\xff\xdd\xbe\x8e\xff\xdc\xbb\x8a\xff\xdd\xbd\x8b\xff\xdd\xbc\x8b\xff\xdd\xbb\x88\xff\xdf\xbf\x8d\xff\xe0\xc0\x8e\xff\xe1\xc0\x8d\xff\xe0\xbf\x8d\xff\xe1\xc0\x8f\xff\xe1\xbf\x8d\xff\xe1\xbf\x8c\xff\xe0\xbd\x88\xff\xe1\xbe\x8b\xff\xe0\xbe\x8a\xff\xde\xbc\x8a\xff\xdd\xc1\x95\xff\xdd\xbf\x93\xff\xdd\xbd\x8b\xff\xdc\xba\x87\xff\xde\xbc\x89\xff\xde\xb9\x86\xff\xe0\xba\x86\xff\xe1\xbc\x8a\xff\xe1\xbc\x8b\xff\xe2\xbe\x8c\xff\xe2\xbe\x8d\xff\xe2\xc0\x8f\xff\xe1\xbe\x8c\xff\xe3\xc0\x8f\xff\xe3\xbf\x8e\xff\xe3\xbf\x8e\xff\xe3\xbe\x8e\xff\xe2\xbd\x8e\xff\xe1\xbe\x8d\xff\xe1\xbf\x8d\xff\xe3\xc1\x90\xff\xe4\xc2\x92\xff\xe3\xc2\x92\xff\xe4\xc4\x94\xff\xe5\xc3\x93\xff\xe7\xc5\x94\xff\xe7\xc7\x96\xff\xe8\xc9\x98\xff\xe8\xc8\x99\xff\xe7\xc5\x96\xff\xe7\xc5\x95\xff\xe5\xc5\x97\xff\xe7\xc7\x95\xff\xe8\xc8\x98\xff\xea\xc9\x99\xff\xea\xc9\x9a\xff\xe9\xc9\x9a\xff\xe9\xc8\x9a\xff\xe9\xc8\x9a\xff\xe9\xc8\x98\xff\xe9\xc8\x9a\xff\xe8\xc7\x98\xff\xe8\xc9\x98\xff\xea\xca\x9a\xff\xe8\xc9\x98\xff\xe9\xc9\x9a\xff\xe8\xc7\x97\xff\xe4\xc3\x91\xff\xe4\xc4\x92\xff\xe6\xc6\x95\xff\xe5\xc5\x94\xff\xe6\xc5\x96\xff\xe5\xc6\x98\xff\xe5\xc6\x98\xff\xe7\xc8\x9d\xff\xe4\xc6\x98\xff\xe4\xc7\x9b\xff\xe5\xc7\x9d\xff\xe2\xc4\x98\xff\xe2\xc3\x98\xff\xe3\xc3\x97\xff\xe1\xc1\x94\xff\xe2\xc3\x98\xff\xe3\xc3\x98\xff\xe3\xc4\x99\xff\xe3\xc4\x97\xff\xe4\xc4\x97\xff\xe4\xc1\x91\xff\xe4\xc2\x95\xff\xe3\xc1\x93\xff\xe1\xbd\x90\xff\xe2\xc1\x91\xff\xe1\xc1\x93\xff\xe2\xc6\x98\xff\xe3\xc7\x98\xff\xe3\xc6\x96\xff\xe0\xc1\x90\xff\xe1\xc1\x92\xff\xe1\xbf\x90\xff\xe0\xbe\x91\xff\xe0\xbd\x8d\xff\xe2\xc1\x93\xff\xe3\xc2\x94\xff\xe3\xc2\x94\xff\xe2\xc0\x92\xff\xe2\xc0\x93\xff\xe2\xc0\x91\xff\xe2\xc0\x91\xff\xe2\xc0\x92\xff\xe3\xc0\x92\xff\xe5\xc2\x94\xff\xe2\xbd\x90\xff\xe5\xc1\x93\xff\xe3\xc1\x92\xff\xe3\xc3\x92\xff\xe2\xc2\x8e\xff\xe2\xc2\x8f\xff\xe1\xc0\x8e\xff\xe1\xc1\x90\xff\xe1\xc1\x90\xff\xe2\xc2\x91\xff\xe4\xc3\x95\xff\xe3\xc2\x93\xff\xe3\xc2\x92\xff\xe4\xc4\x98\xff\xe5\xc4\x97\xff\xe5\xc4\x96\xff\xe2\xc0\x91\xff\xe3\xc3\x94\xff\xe3\xc5\x95\xff\xe3\xc5\x96\xff\xe4\xc6\x98\xff\xe4\xc8\x99\xff\xe3\xc7\x97\xff\xe2\xc6\x96\xff\xe1\xc5\x96\xff\xe0\xc2\x93\xff\xdf\xbf\x91\xff\xde\xbe\x8f\xff\xde\xbe\x8f\xff\xdd\xbe\x8f\xff\xdc\xbd\x8d\xff\xda\xbb\x8d\xff\xd9\xbb\x8b\xff\xd7\xb8\x88\xff\xd7\xb9\x8b\xff\xd6\xb8\x8a\xff\xd3\xb5\x86\xff\xd0\xb1\x7f\xff\xce\xac\x7b\xff\xcc\xaa\x7a\xff\xcb\xab\x77\xff\xcb\xaa\x77\xff\xc8\xa7\x76\xff\xc7\xa5\x76\xff\xc7\xa6\x76\xff\xc3\xa2\x73\xff\xc2\xa0\x73\xff\xbd\x9a\x6d\xff\xba\x97\x69\xff\xb8\x95\x68\xff\xb5\x93\x67\xff\xb3\x94\x69\xff\xa5\x88\x5e\xff\x90\x7a\x50\xff\x8e\x77\x4f\xff\x88\x73\x4d\xff\x82\x6c\x4a\xff\x7a\x63\x41\xff\x73\x5c\x3b\xff\xad\x85\x57\xff\xc6\xa1\x74\xff\xca\xa6\x7a\xff\xbb\x94\x67\xff\xa6\x7f\x56\xff\x90\x6d\x48\xff\x7d\x60\x3f\xff\x72\x56\x36\xff\x6f\x55\x39\xff\x0c\x14\x1c\xff\x07\x16\x1e\xff\x07\x10\x14\xff"
local bookTexture =
  "\x39\x1c\x0b\x00\x53\x25\x09\x25\x80\x4b\x23\xff\x6b\x2f\x0a\xff\x6b\x2f\x0a\xff\x73\x34\x0c\xff\x53\x20\x02\xff\x74\x3a\x12\xff\x63\x2d\x0a\xff\x36\x14\x02\xff\x49\x2a\x13\xff\xd6\x9b\x4c\xff\xd4\x8b\x2e\xff\xd6\x93\x3b\xff\xdb\x9b\x3c\xff\xd6\x93\x3b\xff\xdc\x9b\x43\xff\xdc\x9b\x43\xff\xda\xa3\x4b\xff\xdf\xab\x5a\xff\xea\xbc\x6b\xff\xec\xc4\x7b\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xeb\xc4\x83\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe3\xc3\x8b\xff\xe4\xbd\x8b\xff\xe4\xbd\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe6\xc3\x90\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xee\xd3\xa0\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xee\xd3\xa0\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf6\xdf\xb8\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe6\xca\x90\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xeb\xca\x8c\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xc3\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe3\xc3\x8b\xff\xe2\xc3\x84\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd2\xad\x6a\xff\xcb\xa4\x62\xff\xcb\xa4\x62\xff\xc7\x9c\x5a\xff\xc7\x9c\x5a\xff\xc3\x94\x52\xff\xc2\x8d\x4a\xff\xbc\x8b\x4b\xff\xb4\x81\x43\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xbb\x73\x34\xff\xbc\x82\x3c\xff\xc2\x8d\x4a\xff\xc2\x8d\x4a\xff\x99\x6a\x3b\xff\x7b\x43\x1a\xff\x2c\x1a\x0d\xff\x2c\x1a\x0d\xff\x7b\x43\x1a\xff\x99\x6a\x3b\xff\xc2\x8d\x4a\xff\xc2\x8d\x4a\xff\xbc\x82\x3c\xff\xbb\x73\x34\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb4\x81\x43\xff\xbc\x8b\x4b\xff\xc2\x8d\x4a\xff\xc3\x94\x52\xff\xc7\x9c\x5a\xff\xc7\x9c\x5a\xff\xcb\xa4\x62\xff\xcb\xa4\x62\xff\xd2\xad\x6a\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xdb\xbc\x83\xff\xe2\xc3\x84\xff\xe3\xc3\x8b\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe6\xc3\x90\xff\xe6\xca\x90\xff\xe6\xca\x90\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xca\x8c\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xe6\xca\x90\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf6\xdf\xb8\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xee\xd3\xa0\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xee\xd3\xa0\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xe6\xc3\x90\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe4\xbd\x8b\xff\xe4\xbd\x8b\xff\xe3\xc3\x8b\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xeb\xc4\x83\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xec\xc4\x7b\xff\xea\xbc\x6b\xff\xdf\xab\x5a\xff\xda\xa3\x4b\xff\xdc\x9b\x43\xff\xdc\x9b\x43\xff\xd6\x93\x3b\xff\xdb\x9b\x3c\xff\xd6\x93\x3b\xff\xd4\x8b\x2e\xff\xd6\x9b\x4c\xff\x49\x2a\x13\xff\x36\x14\x02\xff\x63\x2d\x0a\xff\x74\x3a\x12\xff\x53\x20\x02\xff\x73\x34\x0c\xff\x6b\x2f\x0a\xff\x6b\x2f\x0a\xff\x80\x4b\x23\xff\x53\x25\x09\x25\x39\x1c\x0b\x00"
local bankXButtonTexture =
  "\x6b\x2d\x28\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6d\x31\x2a\xff\x6d\x31\x2a\xff\x6b\x2d\x28\xff\xb3\x8c\x5d\xff\xcb\xab\x6f\xff\xb3\x8c\x5d\xff\x6b\x2d\x28\xff\x6b\x2d\x28\xff\x6d\x31\x2a\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6b\x2d\x28\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Read the King's message.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.InventoryHighlight:new(kingsMessage) },
    postconditions = { Condition.Generic2DVisible:new(496, 293, 160, parchmentTexture) },
  },
  {
    text = "Go to 1st floor (2nd floor[US]) of Ardougne Castle in East Ardougne.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2571, 2525, 3307) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2571, 2917, 3306, 4),
      Condition.DistanceToWithHeight:new(2576, 2917, 3297, 5),
    },
  },
  {
    text = "Talk to King Lathas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king lathas"]) },
    postconditions = { Condition.ConversationText:new("will be well rewarded") }, --not tested
  },
  {
    text = "Enter the Underground Pass.",
    title = "Underground Pass",
    neededItems = {
      ["Ropes"] = { quantity = 3 },
      ["Any shortbow"] = { quantity = 1 },
      ["Any metal arrows (bronze, iron, etc.)"] = { quantity = 10 },
    },
    actions = { Action.Direction:new(2437, 1153, 3315) },
    postconditions = { Condition.DistanceTo:new(2496, 2693, 9715, 4) },
  },
  {
    text = "Talk to Koftik at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2496, 2693, 9715),
        Location:new(2490, 2109, 9717),
        Location:new(2484, 1277, 9716),
        Location:new(2480, 997, 9716),
        Location:new(2480, 996, 9712),
        Location:new(2479, 893, 9709),
        Location:new(2472, 1002, 9706),
        Location:new(2470, 1213, 9706),
        Location:new(2463, 831, 9706),
        Location:new(2457, 957, 9711),
        Location:new(2457, 1205, 9713),
        Location:new(2452, 837, 9716),
      }),
      Action.ModelHighlight:new(koftik, { distance = 16 }),
    },
    postconditions = { Condition.InventoryContains:new(dampCloth) },
  },
  {
    text = "Use the damp cloth on whichever arrow you brought.",
    actions = {
      Action.InventoryHighlight:new(dampCloth),
      Action.InventoryHighlight:new(metalArrows),
    },
    postconditions = { Condition.InventoryContains:new(fireArrow) },
  },
  {
    text = "Light your fire arrows.",
    actions = { Action.InventoryHighlight:new(fireArrow, true) },
    postconditions = { Condition.ChatText:new("light the cloth wrapped") },
  },
  {
    text = "Equip your bow/lit fire arrows, then stand on the marked tile.",
    actions = {
      Action.Direction:new(2448, 1125, 9721, { tile = true }),
      Action.InventoryHighlight:new(litFireArrow),
      Action.InventoryHighlight:new(shortbow),
    },
    postconditions = { Condition.DistanceTo:new(2448, 1125, 9721, 0) },
  },
  {
    text = "Fire at the guide rope attached to the bridge.",
    actions = { Action.Direction:new(2443.25, 909, 9718) },
    postconditions = { Condition.ChatText:new("arrow sets the rope") },
  },
  {
    text = "Wait for your character to run across the bridge.",
    postconditions = { Condition.DistanceTo:new(2442, 501, 9716, 1) },
  },
  {
    text = "Pick up the plank.",
    actions = { Action.ModelHighlight:new(Models.items["plank"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["plank"]) },
  },
  {
    text = "Use a rope on the old spike at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2435, 1085, 9714),
        Location:new(2434, 1052, 9708),
        Location:new(2436, 933, 9702),
        Location:new(2441, 869, 9699),
        Location:new(2448, 803, 9698),
        Location:new(2455, 613, 9698),
        Location:new(2461, 1189, 9699),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2459, 957, 9699, 4) },
  },
  {
    actions = {
      Action.Direction:new(2462.4, 1437, 9699),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.DistanceTo:new(2467, 1285, 9699, 1) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2466, 1285, 9699),
        Location:new(2468, 1101, 9696),
        Location:new(2474, 973, 9697),
        Location:new(2477, 822, 9699),
        Location:new(2482, 1197, 9701),
        Location:new(2486, 1133, 9703),
        Location:new(2490, 981, 9700),
        Location:new(2491, 1211, 9696),
        Location:new(2491, 717, 9690),
        Location:new(2490, 845, 9687),
        Location:new(2487, 1061, 9679),
        Location:new(2481, 837, 9679),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2480, 837, 9679, 1) },
  },
  {
    text = "Find your path across the grid. Keep a record of the path over the grid for Regicide quest.<ul><li>You can use the table on the wiki to save your path.</li></ul>",
    postconditions = {
      Condition.DistanceTo:new(2466, 837, 9674, 1),
      Condition.DistanceTo:new(2466, 837, 9677, 1),
      Condition.DistanceTo:new(2466, 837, 9680, 1),
      Condition.DistanceTo:new(2466, 837, 9681, 1),
    },
  },
  {
    text = "Pull the lever to the south at the end to continue.",
    actions = { Action.Direction:new(2466, 837, 9672) },
    postconditions = { Condition.ChatText:new("portcullis opens") },
  },
  {
    text = "Follow the path. Step on the breaks in the line to avoid traps.",
    actions = {
      Action.PathGuide:new({
        Location:new(2464, 885, 9675),
        Location:new(2459, 852, 9674),
        Location:new(2454, 821, 9675),
        Location:new(2451, 541, 9678),
        Location:new(2446, 1004, 9678),
        Location:new(2444, 1069, 9677),
        Location:new(2442, 989, 9677),
        Location:new(2441, 989, 9677),
        Location:new(2439, 885, 9677),
        Location:new(2436, 714, 9676),
        Location:new(2434, 941, 9676),
        Location:new(2433, 981, 9676),
        Location:new(2431, 1053, 9676),
        Location:new(2429, 997, 9676),
        Location:new(2426, 965, 9675),
        Location:new(2422, 1253, 9674),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2423, 1125, 9674, 4) },
  },
  {
    text = "Climb down the well.",
    actions = { Action.Direction:new(2416.5, 1609, 9674.5) },
    postconditions = { Condition.DistanceTo:new(2423, 1205, 9660, 4) },
  },
  {
    text = "Pick the lock to the cell.",
    actions = { Action.Direction:new(2393, 1497, 9654.5) },
    postconditions = { Condition.DistanceTo:new(2393, 997, 9653, 1) },
  },
  {
    text = "Dig the mud.",
    actions = { Action.Direction:new(2393, 997, 9650, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2392, 845, 9646, 1) },
  },
  {
    text = "Cross the ledge.",
    actions = { Action.Direction:new(2374, 861, 9644) },
    postconditions = { Condition.DistanceTo:new(2374, 901, 9638, 1) },
    jumpconditions = { Condition.DistanceTo:new(2372, 237, 9642, 1) },
    jumpOffset = -2,
  },
  {
    text = "Reaching the eastern side of the wooden bridge maze.<ul><li>Optional: With 50 Thieving, you can take the shortcut. Avoid the bubbles.</li></ul>",
    actions = {
      Action.PathGuide:new({ --wooden bridge maze
        Location:new(2373, 989, 9634),
        Location:new(2379, 869, 9634),
        Location:new(2381, 869, 9634),
        Location:new(2384, 869, 9634),
        Location:new(2384, 869, 9631),
        Location:new(2386, 869, 9631),
        Location:new(2389, 869, 9631),
        Location:new(2389, 869, 9627),
        Location:new(2391, 869, 9627),
        Location:new(2395, 869, 9627),
        Location:new(2395, 869, 9632),
        Location:new(2398, 869, 9632),
        Location:new(2403, 869, 9632),
        Location:new(2403, 869, 9637),
        Location:new(2405, 869, 9637),
        Location:new(2407, 869, 9637),
        Location:new(2411, 869, 9637),
        Location:new(2415, 229, 9637),
      }),

      Action.PathGuide:new({ --shortcut
        Location:new(2373, 989, 9634),
        Location:new(2372, 1581, 9627),
        Location:new(2374, 1509, 9624),
        Location:new(2376, 1189, 9620),
        Location:new(2380, 981, 9619),
        Location:new(2384, 885, 9619),
        Location:new(2391, 933, 9619),
        Location:new(2392, 933, 9619),
        Location:new(2393, 925, 9618),
        Location:new(2395, 957, 9618),
        Location:new(2396, 901, 9619),
        Location:new(2399, 973, 9619),
        Location:new(2400, 933, 9620),
        Location:new(2403, 941, 9620),
        Location:new(2404, 933, 9620),
      }),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2417, 181, 9637, 1), --wooden bridge maze
      Condition.DistanceTo:new(2405, 1045, 9620, 1), --shortcut
    },
  },
  {
    text = "Enter the obstacle pipe to the south.",
    actions = { Action.Direction:new(2418, 1489, 9605) },
    postconditions = { Condition.DistanceTo:new(2412, 1245, 9605, 1) },
  },
  {
    text = "Pass through the tunnel north of the cage.",
    actions = { Action.Direction:new(2375.5, 1589, 9611) },
    postconditions = { Condition.DistanceTo:new(2371, 2349, 9666, 4) },
  },
  {
    text = "Enter the door at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2371, 2349, 9666),
        Location:new(2373, 1893, 9673),
        Location:new(2373, 894, 9682),
        Location:new(2373, 1076, 9690),
        Location:new(2378, 1037, 9696),
        Location:new(2386, 789, 9701),
        Location:new(2389, 725, 9706),
        Location:new(2397, 213, 9706),
        Location:new(2400, 349, 9703),
        Location:new(2407, 213, 9703),
        Location:new(2411, 197, 9706),
        Location:new(2417, 821, 9707),
        Location:new(2419, 965, 9706),
        Location:new(2421, 1029, 9708),
        Location:new(2423, 1141, 9709),
        Location:new(2423, 1157, 9722),
        Location:new(2414, 725, 9722),
        Location:new(2407, 837, 9719),
      }),
      Action.PathGuide:new({
        Location:new(2405, 901, 9719),
        Location:new(2401, 1093, 9722),
        Location:new(2394, 1277, 9722),
        Location:new(2391, 1453, 9720),
        Location:new(2385, 1269, 9719),
        Location:new(2378, 775, 9718),
        Location:new(2373, 613, 9716),
        Location:new(2369, 677, 9718),
      }),
      Action.InventoryHighlight:new(Models.items["plank"]),
    },
    postconditions = { Condition.DistanceTo:new(2173, 6413, 4725, 4) },
  },
  {
    text = "Open the temple doors at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2173, 6413, 4725),
        Location:new(2171, 6461, 4722),
        Location:new(2171, 6677, 4700),
        Location:new(2172, 6433, 4696),
        Location:new(2172, 6725, 4686),
        Location:new(2167, 7653, 4686),
        Location:new(2162, 7749, 4686),
        Location:new(2161, 7749, 4688),
        Location:new(2161, 8197, 4694),
        Location:new(2161, 7909, 4699),
        Location:new(2156, 7717, 4699),
        Location:new(2156, 7717, 4698),
        Location:new(2154, 7717, 4698),
        Location:new(2154, 7285, 4693),
        Location:new(2154, 7237, 4690),
        Location:new(2154, 7069, 4686),
        Location:new(2155, 7045, 4686),
        Location:new(2155, 7045, 4683),
        Location:new(2153, 7069, 4683),
        Location:new(2153, 6373, 4678),
        Location:new(2154, 6373, 4678),
        Location:new(2154, 6373, 4676),
        Location:new(2156, 6053, 4676),
        Location:new(2160, 5893, 4676),
        Location:new(2160, 5893, 4674),
        Location:new(2160, 5381, 4671),
        Location:new(2160, 5381, 4670),
        Location:new(2165, 5381, 4670),
        Location:new(2165, 5381, 4667),
        Location:new(2162, 5381, 4667),
        Location:new(2162, 5381, 4666),
        Location:new(2162, 5381, 4663),
        Location:new(2161, 5381, 4659),
        Location:new(2161, 5093, 4656),
        Location:new(2161, 5093, 4653),
        Location:new(2159, 5093, 4653),
        Location:new(2159, 5093, 4649),
        Location:new(2154, 5093, 4649),
        Location:new(2150, 5733, 4648),
        Location:new(2146, 6373, 4648),
        Location:new(2145, 6373, 4648),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2014, 6373, 4712, 4) },
  },
  {
    text = "Climb down the well.",
    actions = { Action.ModelHighlight:new(skybeamWell) },
    postconditions = { Condition.DistanceTo:new(2343, 869, 9622, 4) },
  },
  {
    text = "Exit the Underground Pass.",
    actions = { Action.ModelHighlight:new(caveEntranceLightBeam) },
    postconditions = { Condition.DistanceTo:new(2312, 1341, 3217, 4) },
  },
  {
    text = "Wait for Idris to talk to you.<ul><li>If Idris does not spawn, log out and back in to trigger the scene.</li></ul>",
    title = "Tirannwn",
    neededItems = { ["Limestone"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.ConversationText:new("Halt human") },
  },
  {
    text = "Talk to Idris.",
    postconditions = { Condition.ConversationText:new("human by the name") },
  },
  {
    text = "Talk to Morvran.",
    postconditions = { Condition.ConversationText:new("north west of the forest") },
  },
  {
    text = "Activate the lodestone at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2311, 1277, 3217),
        Location:new(2307, 1189, 3213),
        Location:new(2302, 1085, 3211),
        Location:new(2297, 1029, 3208),
        Location:new(2294, 1093, 3207),
        Location:new(2284, 885, 3212),
        Location:new(2272, 1189, 3209),
        Location:new(2267, 1269, 3205),
      }),
      Action.PathGuide:new({
        Location:new(2267, 1277, 3201),
        Location:new(2263, 1189, 3195),
        Location:new(2260, 1101, 3185),
        Location:new(2253, 1109, 3183),
        Location:new(2247, 1373, 3182),
        Location:new(2242, 1229, 3182),
        Location:new(2238, 981, 3181),
      }),
      Action.PathGuide:new({
        Location:new(2234, 1029, 3181),
        Location:new(2232, 917, 3179),
        Location:new(2233, 1093, 3173),
        Location:new(2238, 885, 3168),
        Location:new(2241, 1117, 3162),
        Location:new(2252, 1173, 3151),
      }),
    },
    postconditions = { Condition.ChatText:new("lodestone in Tirannwn") },
  },
  {
    text = "Talk to Lord Iorwerth at the end of the path.",
    actions = {
      lodestoneToIowerth1,
      lodestoneToIowerth2,
      lodestoneToIowerth3,
      Action.ModelHighlight:new(Models.npcs["lord iorwerth"], { distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("task is complete") },
  },
  {
    text = "Talk to the elf tracker.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = { Action.ModelHighlight:new(elfTracker) },
    postconditions = { Condition.ConversationText:new("brigands or outlaws") },
  },
  {
    text = "Talk to Lord Iorwerth.",
    actions = {
      lodestoneToIowerth1,
      lodestoneToIowerth2,
      lodestoneToIowerth3,
      Action.ModelHighlight:new(Models.npcs["lord iorwerth"], { distance = 20 }),
    },
    postconditions = { Condition.InventoryContains:new(crystalPendant) },
  },
  {
    text = "Talk to the elf tracker.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = { Action.ModelHighlight:new(elfTracker) },
    postconditions = { Condition.ConversationText:new("me if you find anything") },
  },
  {
    text = "Right-click and 'follow' the tracks.", -- about 10 steps west of the lodestone in front of the dense forest entrance.",
    actions = { Action.Direction:new(2241, 1085, 3150, { tile = true }) },
    postconditions = { Condition.ChatText:new("impassable woodland") },
  },
  {
    text = "Talk to the elf tracker.",
    actions = { Action.ModelHighlight:new(elfTracker) },
    postconditions = { Condition.ConversationText:new("what I can find") },
  },
  {
    text = "Kill the guard that appears past the dense forest.",
    actions = { lodestoneToMushroomPatch },
    postconditions = { Condition.ModelVisible:new(Models.npcs["tyras guard"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["tyras guard"]) },
    postconditions = { Condition.ModelNotVisible:new(Models.npcs["tyras guard"]) },
  },
  {
    text = "Talk to General Hining at the end of the path.",
    actions = {
      mushroomToTyrasCamp1,
      mushroomToTyrasCamp2,
      Action.ModelHighlight:new(Models.npcs["general hining"]),
    },
    postconditions = { Condition.ConversationText:new("what I had in mind") },
  },
  {
    text = "Take 2 barrels.<ul><li>Extra is for Mourning's End Part I.</li></ul>",
    actions = { Action.ModelHighlight:new(barrel, { highlightPriority = "all" }) },
    postconditions = { Condition.InventoryContains:new(barrel, 2) },
  },
  {
    text = "Talk to Lord Iorwerth.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = {
      lodestoneToIowerth1,
      lodestoneToIowerth2,
      lodestoneToIowerth3,
      Action.ModelHighlight:new(Models.npcs["lord iorwerth"], { distance = 20 }),
    },
    postconditions = { Condition.InventoryContains:new(theBigBookOfBangs) },
  },
  {
    text = "Read the book.",
    actions = { Action.InventoryHighlight:new(theBigBookOfBangs) },
    postconditions = { Condition.Generic2DVisible:new(482, 290, 135, bookTexture) },
  },
  {
    text = "Pick up the pot to the north.",
    actions = { Action.ModelHighlight:new(Models.items["pot"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["pot"]) },
  },
  {
    text = "Use both barrels on the tar south of the elf tracker.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2263, 325, 3127, { tile = true }),
      Action.InventoryHighlight:new(barrel),
    },
    postconditions = { Condition.InventoryContains:new(barrelOfTar, 2) },
  },
  {
    text = "Take some sulphur.",
    actions = { Action.Direction:new(2261, 493, 3130, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(sulphur) },
  },
  {
    text = "Grind the sulphur.",
    actions = { Action.InventoryHighlight:new(sulphur) },
    postconditions = { Condition.InventoryContains:new(groundSulphur) },
  },
  {
    text = "Use the limestone on any furnace.",
    actions = {
      Action.InventoryHighlight:new(limestone),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InventoryContains:new(quicklime) },
  },
  {
    text = "Talk to the Chemist in Rimmington.",
    title = "Sabotage",
    warning = "The The Big Book of Bangs must be in your inventory.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Port Sarim lodestone",
      url = "Port_Sarim_lodestone_icon.png",
    },
    neededItems = {
      ["The Big Book of Bangs"] = { quantity = 1 },
      ["Empty pot"] = { quantity = 1 },
      ["Quicklime"] = { quantity = 1 },
      ["Strip of cloth"] = { quantity = 1 },
      ["Cooked rabbit"] = { quantity = 1 },
      ["Barrel of coal-tar"] = { quantity = 2 },
      ["Ground sulphur"] = { quantity = 1 },
      ["Coal"] = { quantity = 20 },
    },
    recommendedItems = {},
    actions = {
      Action.Direction:new(2932, 981, 3210, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["chemist"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about Regicide."),
      Action.ConversationHighlight:new("What is naphtha and how do I make it?"),
    },
    postconditions = { Condition.ConversationText:new("keep the still hot") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["chemist"]),
      Action.ConversationHighlight:new("What's quicklime?"),
    },
    postconditions = { Condition.ConversationText:new("extremely caustic") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["chemist"]),
      Action.ConversationHighlight:new("What's brimstone?"),
    },
    postconditions = { Condition.ConversationText:new("that I don't know") },
  },
  {
    text = "Use your barrel of tar on the fractionation still outside.",
    actions = {
      Action.Direction:new(2927, 2185, 3212),
      Action.InventoryHighlight:new(barrelOfTar),
    },
    postconditions = { Condition.Generic2DVisible:new(15, 15, 7, bankXButtonTexture) },
  },
  {
    text = "Create 2 barrels of naphtha.<ul><li>Increase the tar regulator twice.</li><li>Wait until the pressure valve is in the green.</li><li>Increase the pressure valve once.</li><li>'Add coal' in 1-2 increments to get the heat valve in the green.</li><li>When the distillation bar is full, exit the interface.</li></ul>",
    title = "Operating the still",
    warning = "No tracking for this step.",
    postconditions = { Condition.InventoryContains:new(barrelOfNaphtha) },
  },
  {
    actions = { Action.Direction:new(2927, 2185, 3212), Action.InventoryHighlight:new(barrelOfTar) },
    postconditions = {
      Condition.Generic2DVisible:new(15, 15, 7, bankXButtonTexture),
      Condition.InventoryContains:new(barrelOfNaphtha, 2),
    },
  },
  { postconditions = { Condition.InventoryContains:new(barrelOfNaphtha, 2) } },
  {
    text = "Grind the quicklime into your pot.",
    title = "Preparing barrel bomb",
    actions = { Action.InventoryHighlight:new(quicklime) },
    postconditions = { Condition.InventoryContains:new(potOfQuicklime) },
  },
  {
    text = "Use ground sulphur on the barrel of naphtha.",
    actions = {
      Action.InventoryHighlight:new(groundSulphur),
      Action.InventoryHighlight:new(barrelOfNaphtha),
    },
    postconditions = { Condition.ChatText:new("sulphur dust into the naphtha") },
  },
  {
    text = "Use the pot of quicklime on the naphtha mix.",
    actions = {
      Action.InventoryHighlight:new(potOfQuicklime),
      Action.InventoryHighlight:new(barrelOfNaphthaMix),
    },
    postconditions = { Condition.ChatText:new("naphtha and seal the barrel") },
  },
  {
    text = "Use a strip of cloth on the barrel bomb.",
    actions = {
      Action.InventoryHighlight:new(stripOfCloth, true),
      Action.InventoryHighlight:new(barrelBomb),
    },
    postconditions = { Condition.ConversationText:new("stuff cloth through") },
  },
  {
    text = "Kill and cook a rabbit if you don't have one.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    postconditions = { Condition.InventoryContains:new(cookedRabbit) },
  },
  {
    text = "Use a cooked rabbit on the Tyras guard.",
    actions = {
      lodestoneToMushroomPatch,
      Action.PathGuide:new({
        Location:new(2231, 861, 3149),
        Location:new(2227, 885, 3146),
        Location:new(2222, 901, 3146),
      }),
      mushroomToTyrasCamp1,
      Action.PathGuide:new({
        Location:new(2220, 901, 3155),
        Location:new(2217, 869, 3158),
        Location:new(2217, 869, 3169),
        Location:new(2209, 1037, 3177),
        Location:new(2204, 1237, 3179),
        Location:new(2185, 1093, 3181),
      }),
      Action.ModelHighlight:new(Models.npcs["shirtless tyras guard"], { distance = 16 }),
      Action.InventoryHighlight:new(cookedRabbit),
    },
    postconditions = { Condition.ConversationText:new("No problem") },
  },
  {
    text = "Use the bomb on the catapult (not the catapult lever).",
    actions = {
      Action.ModelHighlight:new(catapult),
      Action.InventoryHighlight:new(barrelBomb),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to Lord Iorwerth at the Elf Camp.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = {
      lodestoneToIowerth1,
      lodestoneToIowerth2,
      lodestoneToIowerth3,
      Action.ModelHighlight:new(Models.npcs["lord iorwerth"], { distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("my lord") },
  },
  {
    text = "Attempt to enter Ardougne Castle.",
    title = "The lord's message",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2588.5, 2089, 3296.5) },
    postconditions = { Condition.ConversationText:new("by the name") },
  },
  {
    text = "Talk to Arianwyn.",
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    postconditions = { Condition.ConversationText:new("be in touch") },
  },
  {
    text = "Go to 1st floor (2nd floor[US]) of Ardougne Castle in East Ardougne.",
    actions = { Action.Direction:new(2571, 2525, 3307) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2571, 2917, 3306, 4),
      Condition.DistanceToWithHeight:new(2576, 2917, 3297, 5),
    },
  },
  {
    text = "Talk to King Lathas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king lathas"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Regicide",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.long,
  releaseDate = 1095638400,
  prereqQuests = { "Underground Pass" },
  questReqs = {
    Types.QuestReq.skill("Agility", 56),
  },
  neededItems = {
    ["Ropes"] = { quantity = 3, model = Models.items["rope"] },
    ["Any shortbow"] = { quantity = 1, model = shortbow },
    ["Any metal arrows (bronze, iron, etc.)"] = { quantity = 10, model = metalArrows },
    ["Plank"] = { quantity = 1, model = Models.items["plank"], duringQuest = true },
    ["Limestone"] = { quantity = 1, model = limestone },
    ["Coal (not in orebox)"] = { quantity = 20, model = Models.items["coal"] },
    ["An empty pot"] = { quantity = 1, model = Models.items["pot"], duringQuest = true },
    ["Strip of cloth"] = { quantity = 1, model = stripOfCloth },
    ["Cooked rabbit"] = { quantity = 1, model = cookedRabbit, duringQuest = true },
  },
  recommendedItems = {
    ["Any agility boost"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
    ["Antipoison"] = { quantity = 1 },
  },
  combatNPCs = { ["Tyras guard"] = { level = "58", quantity = 1 } },
})
