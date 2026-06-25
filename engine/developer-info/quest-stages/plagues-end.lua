local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local mayorOfPrif = Model.new(4197, {
  [355] = Vertex.new(27, 717, -1, 127, 127, 127),
  [373] = Vertex.new(-27, 717, -1, 127, 127, 127),
  [1239] = Vertex.new(30, 700, -9, 127, 127, 127),
  [1362] = Vertex.new(-30, 700, -9, 127, 127, 127),
  [2761] = Vertex.new(0, 739, -1, 57, 145, 30),
})
local carla = Model.new(3615, {
  [1899] = Vertex.new(24, 738, -38, 32, 31, 29),
  [1923] = Vertex.new(-24, 738, -38, 32, 31, 29),
  [1945] = Vertex.new(-2, 716, -57, 109, 81, 57),
  [1951] = Vertex.new(2, 716, -57, 109, 81, 57),
  [1955] = Vertex.new(6, 716, -52, 109, 81, 57),
})
local sirEdmond = Model.new(5223, {
  [339] = Vertex.new(2, 725, -59, 103, 82, 66),
  [711] = Vertex.new(52, 732, 17, 24, 22, 22),
  [749] = Vertex.new(-52, 732, 17, 24, 22, 22),
  [1830] = Vertex.new(-108, 612, 72, 24, 22, 22),
  [1844] = Vertex.new(108, 612, 72, 24, 22, 22),
})
local sirHugo = Model.new(5958, {
  [1317] = Vertex.new(-49, 695, 74, 164, 132, 34),
  [2046] = Vertex.new(52, 732, 17, 24, 22, 22),
  [2084] = Vertex.new(-52, 732, 17, 24, 22, 22),
  [3042] = Vertex.new(-108, 612, 72, 24, 22, 22),
  [3056] = Vertex.new(108, 612, 72, 24, 22, 22),
})
local gwir = Model.new(4089, {
  [771] = Vertex.new(96, 590, -11, 128, 128, 128),
  [1740] = Vertex.new(-13, 673, -1, 127, 127, 127),
  [2617] = Vertex.new(0, 739, -1, 57, 145, 30),
  [2618] = Vertex.new(0, 736, -1, 57, 145, 30),
  [2619] = Vertex.new(0, 736, -2, 57, 145, 30),
})
local kelyn = Model.new(6240, {
  [1296] = Vertex.new(-4, 770, -44, 127, 127, 127),
  [1323] = Vertex.new(-5, 773, -43, 127, 127, 127),
  [1332] = Vertex.new(-26, 720, -39, 127, 127, 127),
  [1734] = Vertex.new(-5, 769, -41, 127, 127, 127),
  [1845] = Vertex.new(-7, 770, -40, 127, 127, 127),
})
local trahaearnAutomaton = Model.any({
  Model.new(5763, { --hally
    [699] = Vertex.new(-128, 366, -210, 127, 127, 127),
    [732] = Vertex.new(-128, 365, -210, 127, 127, 127),
    [1171] = Vertex.new(0, 735, -7, 29, 142, 129),
    [1172] = Vertex.new(0, 732, -7, 29, 142, 129),
    [1173] = Vertex.new(0, 732, -8, 29, 142, 129),
  }),
  Model.new(4443, { --staff
    [1] = Vertex.new(0, 735, -7, 29, 142, 129),
    [2] = Vertex.new(0, 732, -7, 29, 142, 129),
    [3] = Vertex.new(0, 732, -8, 29, 142, 129),
    [4438] = Vertex.new(-132, 377, -18, 134, 99, 70),
    [4441] = Vertex.new(132, 377, -18, 134, 99, 70),
  }),
  Model.new(6081, { --bow
    [1489] = Vertex.new(0, 735, -7, 29, 142, 129),
    [1490] = Vertex.new(0, 732, -7, 29, 142, 129),
    [1491] = Vertex.new(0, 732, -8, 29, 142, 129),
    [6076] = Vertex.new(-132, 377, -18, 134, 99, 70),
    [6079] = Vertex.new(132, 377, -18, 134, 99, 70),
  }),
})
local elfHermit = Model.new(4647, {
  [2685] = Vertex.new(-27, 753, -30, 128, 127, 127),
  [2686] = Vertex.new(-29, 726, -25, 128, 127, 127),
  [2750] = Vertex.new(27, 753, -30, 128, 127, 127),
  [3040] = Vertex.new(-30, 741, -23, 128, 127, 127),
  [3226] = Vertex.new(30, 741, -23, 128, 127, 127),
})
local shadows = Model.any({
  Model.new(6276, {
    [4] = Vertex.new(181, 648, 32, 128, 128, 128, 0.000),
    [5] = Vertex.new(184, 632, 32, 128, 128, 128, 0.000),
    [6] = Vertex.new(171, 631, 32, 128, 128, 128, 0.000),
    [13] = Vertex.new(-180, 650, 32, 128, 128, 128, 0.000),
    [15] = Vertex.new(-187, 630, 32, 128, 128, 128, 0.000),
  }),
  Model.new(6276, {
    [4] = Vertex.new(317, 1161, 57, 178, 178, 178, 0.000),
    [5] = Vertex.new(322, 1133, 57, 178, 178, 178, 0.000),
    [13] = Vertex.new(-314, 1164, 57, 178, 178, 178, 0.000),
    [14] = Vertex.new(-300, 1130, 57, 178, 178, 178, 0.000),
    [15] = Vertex.new(-327, 1130, 57, 178, 178, 178, 0.000),
  }),
})
local darkLord = Model.new(9615, {
  [4] = Vertex.new(0, 1337, -10, 36, 176, 160),
  [5] = Vertex.new(0, 1327, -10, 36, 176, 160),
  [6] = Vertex.new(0, 1327, -13, 36, 176, 160),
  [7] = Vertex.new(0, 1181, 24, 36, 176, 160),
  [8] = Vertex.new(0, 1171, 24, 36, 176, 160),
})
--#endregion
--#region Objects
local mournersStandard = Model.new(1437, {
  [1152] = Vertex.new(244, 1408, -28, 56, 51, 36),
  [1229] = Vertex.new(-92, 1380, -52, 91, 82, 58),
  [1343] = Vertex.new(244, 1408, -28, 91, 82, 58),
  [1404] = Vertex.new(128, 1384, -52, 113, 102, 72),
  [1436] = Vertex.new(244, 1408, -28, 113, 102, 72),
})
local blueSymbolRangeShop = Model.new(36, {
  [3] = Vertex.new(-189, 438, -162, 127, 128, 127),
  [18] = Vertex.new(-189, 314, -277, 127, 128, 127),
  [21] = Vertex.new(-189, 295, -278, 127, 128, 127),
  [27] = Vertex.new(-189, 394, -130, 127, 128, 127),
  [36] = Vertex.new(-189, 261, -185, 127, 128, 127),
})
local blueSymbolSouthernBuilding = Model.new(36, {
  [3] = Vertex.new(-247, 499, -140, 127, 128, 127),
  [18] = Vertex.new(-247, 375, -255, 127, 128, 127),
  [21] = Vertex.new(-247, 356, -256, 127, 128, 127),
  [27] = Vertex.new(-247, 445, -115, 127, 128, 127),
  [36] = Vertex.new(-247, 325, -156, 127, 128, 127),
})
local blueSymbolBank = Model.new(36, {
  [6] = Vertex.new(-247, 278, 219, 127, 128, 127),
  [9] = Vertex.new(-247, 297, 220, 127, 128, 127),
  [18] = Vertex.new(-247, 307, -53, 127, 128, 127),
  [24] = Vertex.new(-247, 431, 99, 127, 128, 127),
  [27] = Vertex.new(-247, 387, 94, 127, 128, 127),
})
local blueSymbolAltar = Model.new(36, {
  [3] = Vertex.new(-185, 300, -104, 127, 128, 127),
  [18] = Vertex.new(-185, 176, -219, 127, 128, 127),
  [21] = Vertex.new(-185, 157, -220, 127, 128, 127),
  [27] = Vertex.new(-185, 253, -76, 127, 128, 127),
  [36] = Vertex.new(-185, 126, -125, 127, 128, 127),
})
local blueSymbolClothingStore = Model.new(36, {
  [3] = Vertex.new(-247, 445, -134, 127, 128, 127),
  [18] = Vertex.new(-247, 321, -249, 127, 128, 127),
  [21] = Vertex.new(-247, 302, -250, 127, 128, 127),
  [27] = Vertex.new(-247, 401, -102, 127, 128, 127),
  [36] = Vertex.new(-247, 268, -157, 127, 128, 127),
})
local statueHotspot = Model.new(10566, {
  [311] = Vertex.new(-443, 1645, -208, 201, 194, 193, 0.5098),
  [330] = Vertex.new(-591, 1638, 118, 201, 194, 193, 0.5098),
  [332] = Vertex.new(-591, 1641, 118, 201, 194, 193, 0.5098),
  [357] = Vertex.new(-443, 1649, -208, 201, 194, 193, 0.5098),
  [2641] = Vertex.new(-951, 2408, 841, 201, 194, 193, 0.5098),
})
local ithellStatue = Model.new(9978, {
  [275] = Vertex.new(-591, 1641, 118, 177, 177, 177),
  [297] = Vertex.new(-443, 1649, -208, 178, 177, 177),
  [7596] = Vertex.new(-6, 1815, -24, 177, 177, 177),
  [9153] = Vertex.new(-591, 1638, 118, 177, 177, 177),
  [9176] = Vertex.new(-443, 1645, -208, 177, 177, 177),
})
local crackedWall = Model.new(3054, {
  [14] = Vertex.new(768, -32, -768, 0, 0, 0),
  [18] = Vertex.new(768, -32, -768, 0, 0, 0),
  [23] = Vertex.new(768, 31, 768, 0, 0, 0),
})
local crackedWallTunnel = Model.new(3720, {
  [2] = Vertex.new(768, -32, -768, 0, 0, 0),
  [6] = Vertex.new(768, -32, -768, 0, 0, 0),
  [11] = Vertex.new(768, 31, 768, 0, 0, 0),
})
local obelisk = Model.new(4752, {
  [530] = Vertex.new(96, 720, -128, 166, 177, 177),
  [916] = Vertex.new(20, 748, 148, 166, 177, 177),
  [919] = Vertex.new(40, 820, 88, 166, 177, 177),
  [1537] = Vertex.new(-116, 1124, 24, 166, 177, 177),
  [1543] = Vertex.new(-64, 1228, 132, 166, 177, 177),
})
local mushroomObj = Model.new(1392, {
  [1189] = Vertex.new(166, 0, -182, 131, 129, 121),
  [1191] = Vertex.new(171, 0, -171, 131, 129, 121),
  [1197] = Vertex.new(165, 0, -160, 131, 129, 121),
  [1203] = Vertex.new(153, 0, -161, 131, 129, 121),
  [1218] = Vertex.new(166, 0, -182, 131, 129, 121),
})
local treeOrb = Model.new(615, {
  [1] = Vertex.new(-5, 18, -4, 158, 146, 14),
  [2] = Vertex.new(-8, -13, -4, 158, 146, 14),
  [3] = Vertex.new(18, -1, -2, 158, 146, 14),
  [4] = Vertex.new(-5, 17, -1, 158, 146, 14),
  [5] = Vertex.new(-8, -14, -1, 158, 146, 14),
})
local inspectTree = Model.new(1224, {
  [76] = Vertex.new(-210, 1521, -59, 91, 76, 58),
  [79] = Vertex.new(-210, 1521, -59, 86, 71, 55),
  [370] = Vertex.new(-49, 1423, -133, 94, 79, 60),
  [682] = Vertex.new(-210, 1521, -59, 91, 76, 58),
  [866] = Vertex.new(-59, 1496, 71, 86, 71, 55),
})
local meilyrPortal = Model.new(540, {
  [300] = Vertex.new(334, 43, -146, 61, 194, 189),
  [354] = Vertex.new(-138, 43, -326, 61, 194, 189),
  [363] = Vertex.new(-324, 43, 153, 61, 194, 189),
  [372] = Vertex.new(144, 43, 338, 61, 194, 189),
  [486] = Vertex.new(82, 43, 320, 69, 166, 162),
})
local grandLibraryDoorSymbols = Model.new(84, {
  [21] = Vertex.new(1934, 1560, 3281, 177, 177, 177),
  [69] = Vertex.new(1933, 1771, 3099, 177, 177, 177),
  [78] = Vertex.new(1934, 2036, 2869, 177, 177, 177),
  [81] = Vertex.new(4270, 1445, 2068, 0, 0, 0),
  [83] = Vertex.new(3879, 1445, 1701, 0, 0, 0),
})
local mirror = Model.new(1035, {
  [80] = Vertex.new(-203, -156, -78, 38, 145, 184),
  [410] = Vertex.new(122, -261, -166, 36, 138, 176),
  [606] = Vertex.new(-66, 382, 132, 26, 98, 125),
  [621] = Vertex.new(168, 382, -96, 24, 93, 118),
  [636] = Vertex.new(101, 382, -165, 24, 93, 118),
})
--#endregion
--#region Items
local spiritShard = Model.new(108, {
  [41] = Vertex.new(28, 24, -124, 194, 185, 162),
  [50] = Vertex.new(-28, 24, -124, 194, 185, 162),
  [54] = Vertex.new(-28, 24, -124, 194, 185, 162),
  [59] = Vertex.new(-28, 24, -124, 194, 185, 162),
  [68] = Vertex.new(28, 24, -124, 194, 185, 162),
})
local spiritPouch = Model.new(99, {
  [28] = Vertex.new(108, 0, 88, 152, 146, 117),
  [33] = Vertex.new(108, 0, 88, 152, 146, 117),
  [66] = Vertex.new(-80, 0, -104, 165, 154, 127),
  [68] = Vertex.new(-88, 0, -100, 165, 154, 127),
  [70] = Vertex.new(-88, 0, -100, 165, 154, 127),
})
local goldCharm = Model.new(291, {
  [255] = Vertex.new(-16, 20, -32, 0, 0, 0),
  [261] = Vertex.new(32, 20, -4, 0, 0, 0),
  [267] = Vertex.new(4, 20, -40, 0, 0, 0),
  [269] = Vertex.new(-36, 20, -12, 0, 0, 0),
  [290] = Vertex.new(24, 20, 28, 0, 0, 0),
})
local greenCharm = Model.new(294, {
  [254] = Vertex.new(-4, 20, 44, 0, 0, 0),
  [258] = Vertex.new(4, 20, 44, 0, 0, 0),
  [264] = Vertex.new(-40, 20, -12, 0, 0, 0),
  [269] = Vertex.new(40, 20, -12, 0, 0, 0),
  [281] = Vertex.new(-4, 20, -36, 0, 0, 0),
})
local crimsonCharm = Model.new(285, {
  [253] = Vertex.new(-8, 20, 12, 0, 0, 0),
  [255] = Vertex.new(-40, 20, -12, 0, 0, 0),
  [258] = Vertex.new(-40, 20, 12, 0, 0, 0),
  [272] = Vertex.new(-28, 20, -28, 0, 0, 0),
  [284] = Vertex.new(36, 20, 12, 0, 0, 0),
})
local blueCharm = Model.new(261, {
  [254] = Vertex.new(-28, 20, 28, 0, 0, 0),
  [255] = Vertex.new(24, 20, 28, 0, 0, 0),
  [256] = Vertex.new(-12, 20, -36, 0, 0, 0),
  [257] = Vertex.new(-40, 20, -4, 0, 0, 0),
  [259] = Vertex.new(12, 20, -36, 0, 0, 0),
})
local plantCure = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 116, 104, 48),
    [3] = Vertex.new(4, 96, -12, 116, 104, 48),
    [4] = Vertex.new(-4, 96, -12, 116, 104, 48),
    [7] = Vertex.new(4, 96, 12, 116, 104, 48),
    [9] = Vertex.new(-4, 96, 12, 116, 104, 48),
  }),
  Model.new(240, {
    [194] = Vertex.new(12, 96, 4, 136, 138, 149, 0.4980),
    [221] = Vertex.new(-12, 96, -4, 136, 138, 149, 0.4980),
    [225] = Vertex.new(-12, 96, -4, 136, 138, 149, 0.4980),
    [234] = Vertex.new(-4, 96, 12, 136, 138, 149, 0.4980),
    [237] = Vertex.new(-4, 96, 12, 136, 138, 149, 0.4980),
  }),
})
local superDefence3 = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 160, 130, 33),
    [2] = Vertex.new(-20, 52, 8, 160, 130, 33),
    [3] = Vertex.new(-12, 52, 20, 160, 130, 33),
    [6] = Vertex.new(-20, 52, -12, 160, 130, 33),
    [9] = Vertex.new(12, 52, 20, 160, 130, 33),
    [11] = Vertex.new(-12, 52, -20, 160, 130, 33),
    [15] = Vertex.new(20, 52, 8, 160, 130, 33),
    [17] = Vertex.new(12, 52, -20, 160, 130, 33),
    [21] = Vertex.new(20, 52, -16, 160, 130, 33),
    [25] = Vertex.new(-12, 96, 4, 116, 104, 48),
    [26] = Vertex.new(-12, 112, 4, 116, 104, 48),
    [27] = Vertex.new(-12, 112, -4, 116, 104, 48),
    [30] = Vertex.new(-12, 96, -4, 116, 104, 48),
    [33] = Vertex.new(-4, 112, -12, 116, 104, 48),
  }),
  Model.new(288, {
    [49] = Vertex.new(4, 0, -36, 160, 130, 33, 0.8745),
    [50] = Vertex.new(40, 12, -12, 160, 130, 33, 0.8745),
    [51] = Vertex.new(32, 0, -4, 160, 130, 33, 0.8745),
    [53] = Vertex.new(12, 12, -40, 160, 130, 33, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 160, 130, 33, 0.8745),
    [60] = Vertex.new(40, 12, 12, 160, 130, 33, 0.8745),
    [63] = Vertex.new(32, 0, 4, 160, 130, 33, 0.8745),
    [65] = Vertex.new(20, 52, -16, 160, 130, 33, 0.8745),
    [69] = Vertex.new(20, 52, 8, 160, 130, 33, 0.8745),
    [74] = Vertex.new(12, 52, -20, 160, 130, 33, 0.8745),
    [78] = Vertex.new(12, 52, 20, 160, 130, 33, 0.8745),
    [79] = Vertex.new(-12, 12, -40, 160, 130, 33, 0.8745),
    [86] = Vertex.new(-12, 52, -20, 160, 130, 33, 0.8745),
    [88] = Vertex.new(-32, 0, -4, 160, 130, 33, 0.8745),
    [93] = Vertex.new(12, 12, 40, 160, 130, 33, 0.8745),
    [99] = Vertex.new(-12, 52, 20, 160, 130, 33, 0.8745),
    [102] = Vertex.new(4, 0, 36, 160, 130, 33, 0.8745),
    [104] = Vertex.new(-40, 12, -12, 160, 130, 33, 0.8745),
    [109] = Vertex.new(-32, 0, 4, 160, 130, 33, 0.8745),
    [113] = Vertex.new(-20, 52, -12, 160, 130, 33, 0.8745),
  }),
})
--#endregion
--#region Quest Items
local iorwerthMasterPlan = Model.new(36, {
  [21] = Vertex.new(-44, 3, 52, 114, 108, 106),
  [27] = Vertex.new(-37, 5, 57, 131, 124, 122),
  [29] = Vertex.new(38, 5, -56, 131, 124, 122),
  [33] = Vertex.new(-47, 6, 49, 147, 139, 136),
  [35] = Vertex.new(48, 6, -49, 147, 139, 136),
})
local halgrivesProclamation = Model.multi({
  Model.new(60, {
    [3] = Vertex.new(-20, 27, -95, 84, 22, 17),
    [5] = Vertex.new(-20, 27, -95, 84, 22, 17),
    [32] = Vertex.new(44, 31, -84, 103, 27, 21),
    [47] = Vertex.new(20, 60, 6, 103, 27, 21),
    [59] = Vertex.new(18, 16, 35, 103, 27, 21),
  }),
  Model.new(456, {
    [297] = Vertex.new(-121, 52, -10, 148, 133, 118),
    [345] = Vertex.new(-117, 56, -14, 165, 149, 131),
    [368] = Vertex.new(111, 56, -14, 165, 149, 131),
    [423] = Vertex.new(-105, 36, -42, 180, 163, 143),
    [432] = Vertex.new(98, 36, -48, 180, 163, 143),
  }),
  Model.new(12, {
    [1] = Vertex.new(-29, 72, 21, 205, 181, 64),
    [3] = Vertex.new(-29, 49, -34, 205, 181, 64),
    [5] = Vertex.new(31, 72, 21, 205, 181, 64),
    [9] = Vertex.new(-21, 57, -26, 108, 140, 130),
    [11] = Vertex.new(23, 73, 13, 108, 140, 130),
  }),
})
local braveksList = Model.multi({
  Model.new(60, {
    [3] = Vertex.new(1, 27, -94, 79, 28, 25),
    [5] = Vertex.new(1, 27, -94, 79, 28, 25),
    [32] = Vertex.new(36, 31, -83, 98, 35, 30),
    [33] = Vertex.new(17, 35, -82, 98, 35, 30),
    [59] = Vertex.new(18, 16, 35, 98, 35, 30),
  }),
  Model.new(456, {
    [297] = Vertex.new(-121, 52, -10, 138, 133, 128),
    [345] = Vertex.new(-117, 56, -14, 154, 149, 142),
    [368] = Vertex.new(111, 56, -14, 154, 149, 142),
    [423] = Vertex.new(-105, 36, -42, 169, 162, 156),
    [432] = Vertex.new(98, 36, -48, 169, 162, 156),
  }),
  Model.new(6, {
    [1] = Vertex.new(-23, 70, 16, 50, 67, 122),
    [2] = Vertex.new(25, 51, -29, 50, 67, 122),
    [3] = Vertex.new(-23, 51, -29, 50, 67, 122),
    [4] = Vertex.new(-23, 70, 16, 50, 67, 122),
    [5] = Vertex.new(25, 70, 16, 50, 67, 122),
  }),
})
local revolutionaryFlag = Model.multi({
  Model.new(225, {
    [13] = Vertex.new(-122, 5, 270, 145, 93, 13),
    [35] = Vertex.new(30, 5, -290, 63, 32, 19),
    [39] = Vertex.new(-74, 5, -230, 63, 32, 19),
    [50] = Vertex.new(-94, 5, -218, 63, 32, 19),
    [215] = Vertex.new(98, 5, -222, 63, 32, 19),
  }),
  Model.new(36, {
    [3] = Vertex.new(90, 9, 76, 187, 187, 187),
    [14] = Vertex.new(38, 9, -166, 187, 187, 187),
    [18] = Vertex.new(-46, 9, -166, 187, 187, 187),
    [20] = Vertex.new(90, 9, 76, 187, 187, 187),
    [35] = Vertex.new(-46, 9, -166, 187, 187, 187),
  }),
})
local theLostElders = Model.new(432, {
  [107] = Vertex.new(-103, 1, 71, 61, 80, 79),
  [155] = Vertex.new(-44, 37, -96, 48, 62, 62),
  [284] = Vertex.new(-44, 37, -96, 98, 97, 90),
  [357] = Vertex.new(93, 37, -70, 57, 74, 73),
  [376] = Vertex.new(93, 37, -70, 57, 74, 73),
})
local amloddCharm = Model.new(309, {
  [254] = Vertex.new(0, 18, 42, 28, 26, 26),
  [258] = Vertex.new(29, 18, -28, 28, 26, 26),
  [272] = Vertex.new(13, 18, 17, 28, 26, 26),
  [276] = Vertex.new(-13, 18, 17, 28, 26, 26),
  [290] = Vertex.new(-29, 18, -28, 28, 26, 26),
})
local meilyrPotionRecipe = Model.new(6, {
  [1] = Vertex.new(35, 1, 54, 188, 159, 157),
  [2] = Vertex.new(-43, 1, -58, 188, 159, 157),
  [3] = Vertex.new(-51, 1, 47, 188, 159, 157),
  [5] = Vertex.new(44, 1, -51, 188, 159, 157),
  [6] = Vertex.new(-43, 1, -58, 188, 159, 157),
})
local amloddPouch = Model.new(429, {
  [28] = Vertex.new(108, 0, 88, 159, 151, 122),
  [33] = Vertex.new(108, 0, 88, 159, 151, 122),
  [68] = Vertex.new(-88, 0, -100, 171, 159, 131),
  [153] = Vertex.new(49, 39, -57, 142, 154, 150),
  [207] = Vertex.new(-53, 39, -57, 142, 154, 150),
})
local blueMushroom = Model.new(234, {
  [39] = Vertex.new(-45, 55, 25, 187, 187, 187),
  [43] = Vertex.new(-45, 55, 25, 187, 187, 187),
  [49] = Vertex.new(-45, 55, 25, 187, 187, 187),
  [118] = Vertex.new(-45, 55, 25, 133, 131, 122),
  [123] = Vertex.new(-45, 55, 25, 133, 131, 122),
})
local blueMushroomPowder = Model.new(72, {
  [1] = Vertex.new(-44, 0, 36, 69, 121, 166),
  [2] = Vertex.new(0, 40, -8, 69, 121, 166),
  [3] = Vertex.new(-68, 0, 8, 69, 121, 166),
  [4] = Vertex.new(8, 0, 40, 69, 121, 166),
  [9] = Vertex.new(-60, 0, -32, 69, 121, 166),
  [10] = Vertex.new(52, 0, 4, 69, 121, 166),
  [15] = Vertex.new(-24, 0, -72, 69, 121, 166),
  [16] = Vertex.new(60, 0, -48, 69, 121, 166),
  [21] = Vertex.new(12, 0, -68, 69, 121, 166),
  [25] = Vertex.new(48, 0, 76, 69, 121, 166),
  [26] = Vertex.new(68, 24, 56, 69, 121, 166),
})
local redMushroomPowder = Model.new(72, {
  [1] = Vertex.new(-44, 0, 36, 175, 62, 55),
  [2] = Vertex.new(0, 40, -8, 175, 62, 55),
  [3] = Vertex.new(-68, 0, 8, 175, 62, 55),
  [4] = Vertex.new(8, 0, 40, 175, 62, 55),
  [9] = Vertex.new(-60, 0, -32, 175, 62, 55),
  [10] = Vertex.new(52, 0, 4, 175, 62, 55),
  [15] = Vertex.new(-24, 0, -72, 175, 62, 55),
  [16] = Vertex.new(60, 0, -48, 175, 62, 55),
  [21] = Vertex.new(12, 0, -68, 175, 62, 55),
  [25] = Vertex.new(48, 0, 76, 175, 62, 55),
  [26] = Vertex.new(68, 24, 56, 175, 62, 55),
})
local meilyrPotion = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 132, 43, 138),
    [2] = Vertex.new(-20, 52, 8, 132, 43, 138),
    [3] = Vertex.new(-12, 52, 20, 132, 43, 138),
    [6] = Vertex.new(-20, 52, -12, 132, 43, 138),
    [9] = Vertex.new(12, 52, 20, 132, 43, 138),
    [11] = Vertex.new(-12, 52, -20, 132, 43, 138),
    [15] = Vertex.new(20, 52, 8, 132, 43, 138),
    [17] = Vertex.new(12, 52, -20, 132, 43, 138),
    [21] = Vertex.new(20, 52, -16, 132, 43, 138),
    [25] = Vertex.new(-12, 96, 4, 150, 135, 62),
    [26] = Vertex.new(-12, 112, 4, 150, 135, 62),
    [27] = Vertex.new(-12, 112, -4, 150, 135, 62),
    [30] = Vertex.new(-12, 96, -4, 150, 135, 62),
    [33] = Vertex.new(-4, 112, -12, 150, 135, 62),
  }),
  Model.new(288, {
    [49] = Vertex.new(4, 0, -36, 132, 43, 138, 0.8745),
    [50] = Vertex.new(40, 12, -12, 132, 43, 138, 0.8745),
    [51] = Vertex.new(32, 0, -4, 132, 43, 138, 0.8745),
    [53] = Vertex.new(12, 12, -40, 132, 43, 138, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 132, 43, 138, 0.8745),
    [60] = Vertex.new(40, 12, 12, 132, 43, 138, 0.8745),
    [63] = Vertex.new(32, 0, 4, 132, 43, 138, 0.8745),
    [65] = Vertex.new(20, 52, -16, 132, 43, 138, 0.8745),
    [69] = Vertex.new(20, 52, 8, 132, 43, 138, 0.8745),
    [74] = Vertex.new(12, 52, -20, 132, 43, 138, 0.8745),
    [78] = Vertex.new(12, 52, 20, 132, 43, 138, 0.8745),
    [79] = Vertex.new(-12, 12, -40, 132, 43, 138, 0.8745),
    [86] = Vertex.new(-12, 52, -20, 132, 43, 138, 0.8745),
    [88] = Vertex.new(-32, 0, -4, 132, 43, 138, 0.8745),
    [93] = Vertex.new(12, 12, 40, 132, 43, 138, 0.8745),
    [99] = Vertex.new(-12, 52, 20, 132, 43, 138, 0.8745),
    [102] = Vertex.new(4, 0, 36, 132, 43, 138, 0.8745),
    [104] = Vertex.new(-40, 12, -12, 132, 43, 138, 0.8745),
    [109] = Vertex.new(-32, 0, 4, 132, 43, 138, 0.8745),
    [113] = Vertex.new(-20, 52, -12, 132, 43, 138, 0.8745),
  }),
})
--#endregion

local bookTexture =
  "\x39\x1c\x0b\x00\x53\x29\x0c\x25\x8b\x49\x1b\xff\x73\x34\x0c\xff\x6b\x2f\x0a\xff\x7b\x3b\x12\xff\x5b\x24\x02\xff\x7b\x3b\x12\xff\x6b\x2f\x0a\xff\x2b\x11\x04\xff\x6b\x3b\x17\xff\xe5\xb4\x63\xff\xe3\xab\x4b\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa5\x4a\xff\xda\xa3\x4b\xff\xdf\xab\x5a\xff\xe9\xbd\x73\xff\xeb\xc4\x83\xff\xec\xc4\x7b\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xee\xd3\xa0\xff\xed\xcd\x9a\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xf4\xdb\xab\xff\xf1\xd6\xa8\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd6\xa8\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xe6\xca\x90\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xdc\xbf\x89\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xde\xc0\x84\xff\xd5\xb9\x85\xff\xd5\xb9\x85\xff\xdb\xbc\x83\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xd4\xb4\x7b\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd2\xad\x6a\xff\xc6\xa3\x6b\xff\xcb\xa4\x62\xff\xc7\x9c\x5a\xff\xc3\x94\x52\xff\xc3\x94\x52\xff\xbc\x84\x42\xff\xba\x7c\x3a\xff\xb9\x7a\x34\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb5\x7a\x3a\xff\xbc\x84\x42\xff\xc2\x8d\x4a\xff\x99\x6a\x3b\xff\x94\x52\x22\xff\x3c\x22\x10\xff\x3c\x22\x10\xff\x94\x52\x22\xff\x99\x6a\x3b\xff\xc2\x8d\x4a\xff\xbc\x84\x42\xff\xb5\x7a\x3a\xff\xb4\x73\x33\xff\xb4\x73\x33\xff\xb9\x7a\x34\xff\xba\x7c\x3a\xff\xbc\x84\x42\xff\xc3\x94\x52\xff\xc3\x94\x52\xff\xc7\x9c\x5a\xff\xcb\xa4\x62\xff\xc6\xa3\x6b\xff\xd2\xad\x6a\xff\xd4\xb3\x73\xff\xd4\xb3\x73\xff\xd4\xb4\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbb\x7b\xff\xdb\xbc\x83\xff\xd5\xb9\x85\xff\xd5\xb9\x85\xff\xde\xc0\x84\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xdc\xbf\x89\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe3\xc3\x8b\xff\xe5\xc9\x8d\xff\xe6\xca\x90\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe5\xc9\x8d\xff\xe5\xc9\x8d\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xec\xd0\x95\xff\xec\xd0\x95\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xee\xd2\x9b\xff\xf0\xd4\x9c\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf1\xd5\xa2\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf5\xdd\xb1\xff\xf5\xdd\xb1\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf3\xda\xa4\xff\xf3\xda\xa4\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf4\xdb\xab\xff\xf1\xd6\xa8\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xf1\xd6\xa8\xff\xf4\xdb\xab\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xf0\xd4\x9c\xff\xf1\xd5\xa2\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xee\xd3\xa0\xff\xf1\xd5\xa2\xff\xed\xcd\x9a\xff\xee\xd2\x9b\xff\xf1\xd5\xa2\xff\xee\xd2\x9b\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xed\xcd\x9a\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xe9\xc5\x91\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xcb\x93\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc5\x91\xff\xeb\xcb\x93\xff\xec\xd0\x95\xff\xeb\xcb\x93\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xeb\xca\x8c\xff\xe9\xc3\x8c\xff\xe9\xc3\x8c\xff\xed\xcb\x83\xff\xed\xcb\x83\xff\xec\xc4\x7b\xff\xeb\xc4\x83\xff\xe9\xbd\x73\xff\xdf\xab\x5a\xff\xda\xa3\x4b\xff\xe1\xa5\x4a\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe1\xa4\x44\xff\xe3\xab\x4b\xff\xe5\xb4\x63\xff\x6b\x3b\x17\xff\x2b\x11\x04\xff\x6b\x2f\x0a\xff\x7b\x3b\x12\xff\x5b\x24\x02\xff\x7b\x3b\x12\xff\x6b\x2f\x0a\xff\x73\x34\x0c\xff\x8b\x49\x1b\xff\x53\x29\x0c\x25\x39\x1c\x0b\x00"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Arianwyn in Lletya.",
    title = "Starting out",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    neededItems = { ["Mourner gear"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2352, 2069, 3172) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["arianwyn"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.ConversationHighlight:new("Let's go!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["arianwyn"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Arianwyn.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.ConversationHighlight:new("I have the Mourner disguise with me."),
      Action.ConversationHighlight:new("I'm ready to teleport."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Equip your mourner's outfit.",
    title = "Infiltration",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(Models.items["mourner gear"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["mourner gear"]) },
  },
  {
    text = "Enter the door to the west.",
    actions = { Action.Direction:new(-14, 550, 0, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open the door to the north.",
    actions = {
      Action.Direction:new(0, 550, 10.5, { instance = true }),
      Action.ConversationHighlight:new("We thought we saw a mouse."),
      Action.ConversationHighlight:new("It might get into the food stores."),
      Action.ConversationHighlight:new("We knew you were busy, so we dealt with it."),
    },
    postconditions = { Condition.ConversationText:new("waiting for him") },
  },
  {
    text = "Talk to a mourner in the north-western room.",
    actions = { Action.Direction:new(-14, 600, 17, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["mourner"], { instance = true, atLocation = Location:new(-14, 600, 17) }),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["mourner"], { instanced = true, atLocation = Location:new(-14, 600, 17) }),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["mourner"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("death") },
  },
  {
    text = "Talk to Iestin Edern.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["iestin edern"]),
      Action.ConversationHighlight:new("The captain wants you to hurry."),
    },
    postconditions = { Condition.ConversationText:new("room lead to Prifddinas") },
  },
  {
    text = "Climb up the nearby stairs (north wall).",
    actions = { Action.Direction:new(-15, 550, 24.5, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to the Mayor of Prifddinas to the north-west.",
    title = "Iorwerth master plan",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(-23, 1972, 35, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(mayorOfPrif) },
  },
  {
    actions = {
      Action.ModelHighlight:new(mayorOfPrif),
      Action.ConversationHighlight:new("I've got a report from Iestin Edern."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(mayorOfPrif) },
    jumpOffset = -1,
    postconditions = {
      Condition.ConversationText:new("mayor takes the report"),
      -- Condition.InventoryDoesNotContain:new(), --didn't get report data
    },
  },
  {
    text = "Talk to the mourner to the south.",
    actions = { Action.Direction:new(-25, 500, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["mourner"], { instance = true, atLocation = Location:new(-25, -88, -9) }),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["mourner"], { instanced = true, atLocation = Location:new(-25, -88, -9) }),
      Action.ConversationHighlight:new("The mayor wants you to distribute the food to the crowd."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["mourner"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("something to eat") },
  },
  {
    text = "Return to the Mayor.",
    actions = { Action.Direction:new(-23, 1972, 35, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(mayorOfPrif) },
  },
  {
    actions = {
      Action.ModelHighlight:new(mayorOfPrif),
      Action.ConversationHighlight:new("The guards are distributing it to the crowd!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(mayorOfPrif) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("this outrage") },
  },
  {
    text = "Take Iorwerth master plan from the desk.",
    actions = { Action.Direction:new(-24, 2472, 35, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(iorwerthMasterPlan) },
  },
  {
    text = "Climb down the stairs to the south-east.",
    actions = { Action.Direction:new(2.5, 0, 0, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Iestin Edern.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["iestin edern"]),
      Action.ConversationHighlight:new("plans here"),
      Action.ConversationHighlight:new("How can we stop him?"),
    },
    postconditions = { Condition.ConversationText:new("start a revolution") },
  },
  {
    text = "Talk to Councillor Halgrive at the East Ardougne church.",
    title = "Gathering forces",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2616, 1189, 3300) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["councillor halgrive"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["councillor halgrive"]),
      Action.ConversationHighlight:new("I need to show you this document."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["councillor halgrive"]) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(halgrivesProclamation) },
  },
  {
    text = "Climb the stairs in the Civic Office of West Ardougne.",
    actions = { Action.Direction:new(2528.5, 1689, 3316.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2530, 2149, 3316, 4) },
  },
  {
    text = "Talk to Bravek.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["bravek"]),
      Action.ConversationHighlight:new("I need to show you this document."),
      Action.ConversationHighlight:new("If you suspected, why didn't you do anything?"),
    },
    postconditions = {
      Condition.ConversationText:new("us all better."),
      Condition.InventoryContains:new(braveksList),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["bravek"]),
      Action.ConversationHighlight:new("We need to overthrow the Mourners."),
    },
    postconditions = { Condition.InventoryContains:new(braveksList) },
  },
  {
    text = "Unequip 1 mourner piece. The people won't speak to you with a full set of mourner outfit equipped.",
    postconditions = { Condition.InventoryContains:new(Models.items["mourner gear"]) },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(2528.5, 2149, 3316.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2527, 1189, 3316, 4) },
  },
  {
    text = "Talk to Ted Rehnison.",
    actions = { Action.Direction:new(2531, 1285, 3331) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["ted rehnison"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["ted rehnison"]),
      Action.ConversationHighlight:new("I need your help to overthrow the Mourners."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["ted rehnison"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("the church") }, --not tested
  },
  {
    text = "Talk to Jethick.",
    actions = { Action.Direction:new(2540, 1197, 3305) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["jethick"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["jethick"]),
      Action.ConversationHighlight:new("We would if we tore down the city's walls. Join my revolution!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["jethick"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("long time") },
  },
  {
    text = "Talk to Nurse Sarah.",
    actions = { Action.Direction:new(2517, 1029, 3274) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["nurse sarah"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["nurse sarah"]),
      Action.ConversationHighlight:new("I'd feel better if you joined my revolution."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["nurse sarah"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("eradicate them") },
  },
  {
    text = "Talk to Carla.",
    actions = { Action.Direction:new(2495, 1349, 3311) },
    postconditions = { Condition.ModelVisible:new(carla) },
  },
  {
    actions = {
      Action.ModelHighlight:new(carla),
      Action.ConversationHighlight:new("Join my revolution and avenge your son."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(carla) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("the church") },
  },
  {
    text = "Talk to Koftik.",
    actions = { Action.Direction:new(2440, 653, 3315) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["koftik"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["koftik"]),
      Action.ConversationHighlight:new("overthrow the Mourners"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["koftik"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("count me in") },
  },
  {
    text = "Talk to Elena.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2592, 1157, 3336) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["elena"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["elena"]),
      Action.ConversationHighlight:new("Yes - and it's time to stop them."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["elena"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("years ago") },
  },
  {
    text = "Talk to Elena at the West Ardougne church.",
    title = "Revolution",
    neededItems = {
      ["Mourner gear"] = { quantity = 1 },
      ["Combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2528, 1157, 3291) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["elena"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["elena"]),
      Action.ConversationHighlight:new("Yes - I'll explain everything, and then we'll strike!"),
      Action.ConversationHighlight:new("Enough talk - it's time to strike!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["elena"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Mourner HQ") },
  },
  {
    text = "Kick down door at the Mourner HQ.",
    warning = "If door is not closed, teleport away and come back.",
    actions = { Action.Direction:new(2551, 1785, 3320.4) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill the mourners.",
    warning = "No tracking for this step.",
    actions = { Action.ModelHighlight:new(Models.npcs["mourner"], { highlightPriority = "all" }) },
    -- postconditions = {},
  },
  {
    text = "Open the trapdoor.",
    actions = { Action.Direction:new(-9, 0, 4, { instance = true, tile = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Kill the mourners.",
    -- warning = "No tracking for this step.",
    actions = { Action.ModelHighlight:new(Models.npcs["mourner"], { highlightPriority = "all" }) },
    postconditions = { Condition.ModelNotVisible:new(Models.npcs["mourner"]) },
  },
  {
    text = "Attack the head mourner.",
    actions = { Action.ModelHighlight:new(Models.npcs["head mourner"]) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Talk to the head mourner.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["head mourner"]),
      Action.ConversationHighlight:new("I have some questions."),
      Action.ConversationHighlight:new("How were you going to summon the Dark Lord?"),
    },
    postconditions = {
      Condition.ConversationText:new("following orders"),
      Condition.ConversationText:new("see me again"),
      Condition.ConversationText:new("Nooo"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["head mourner"]),
      Action.ConversationHighlight:new("Enough questions."),
      Action.ConversationHighlight:new("coming for him"),
      Action.ConversationHighlight:new("Now die"),
    },
    postconditions = {
      Condition.ConversationText:new("see me again"),
      Condition.ConversationText:new("Nooo"),
    },
  },
  {
    text = "Talk to Elena.",
    actions = { Action.ModelHighlight:new(Models.npcs["elena"]) },
    postconditions = { Condition.InventoryContains:new(revolutionaryFlag) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(0, 600, 1, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(0.5, 450, -1.5, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(2, 960, -2, 4, true) },
  },
  {
    text = "Replace the mourners standard.",
    actions = { Action.ModelHighlight:new(mournersStandard) },
    postconditions = { Condition.ConversationText:new("defend the king") },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(0.5, 960, -1.5, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-1, 0, -2, 4, true) },
  },
  {
    text = "Exit the HQ.",
    actions = { Action.Direction:new(8, 400, -6.5, { instance = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Sir Edmond at the Ardougne Castle.",
    actions = { Action.Direction:new(2589, 1925, 3296) },
    postconditions = {
      Condition.ModelVisible:new(sirEdmond),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(sirEdmond),
      Action.ConversationHighlight:new("Yes"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(sirEdmond) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(-16.5, 400, 11.5, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-16, 992, 10, 4, true) },
  },
  {
    text = "Talk to King Lathas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king lathas"]) },
    postconditions = { Condition.ConversationText:new("head from your") },
  },
  {
    text = "Kill Sir Hugo.",
    postconditions = { Condition.ModelNotVisible:new(sirHugo) },
  },
  {
    text = "Talk to King Lathas.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["king lathas"]),
      Action.ConversationHighlight:new("You need to pay with your death."),
    },
    postconditions = { Condition.ConversationText:new("killed him") },
  },
  {
    text = "Talk to Sir Edmond.",
    actions = { Action.ModelHighlight:new(sirEdmond) },
    postconditions = { Condition.ConversationText:new("last saw them") },
  },
  {
    text = "Talk to Arianwyn in Lletya.",
    title = "Finding the leaders",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    neededItems = { ["Mourner gear"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2352, 2069, 3172) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["arianwyn"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.ConversationHighlight:new("I'm ready to teleport."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["arianwyn"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Equip your mourner's outfit.",
    actions = { Action.InventoryHighlight:new(Models.items["mourner gear"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["mourner gear"]) },
  },
  {
    text = "Talk to Iestin Edern.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["iestin edern"]),
      Action.ConversationHighlight:new("Okay - what do I do now?"),
      Action.ConversationHighlight:new("Go on..."),
      Action.ConversationHighlight:new("Yes - teleport me to Lletya now."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Gwir.",
    actions = { Action.Direction:new(2353, 1669, 3178) },
    postconditions = { Condition.ModelVisible:new(gwir) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gwir),
      Action.ConversationHighlight:new("I need to find the Elders of Prifddinas."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(gwir) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("you'll need") },
  },
  {
    text = "Search a bookcase.",
    actions = {
      Action.Direction:new(2354, 2069, 3180.5),
      Action.ConversationHighlight:new("The Lost Elders"),
    },
    postconditions = { Condition.InventoryContains:new(theLostElders) },
  },
  {
    text = "Read the book.",
    actions = { Action.InventoryHighlight:new(theLostElders) },
    postconditions = { Condition.Generic2DVisible:new(482, 290, 140, bookTexture) },
  },
  {
    text = "Talk to Gwir.",
    actions = {
      Action.ModelHighlight:new(gwir),
      Action.ConversationHighlight:new("I need to find the Elders of Prifddinas."),
      Action.ConversationHighlight:new("Lord Amlodd."),
    },
    postconditions = { Condition.InventoryContains:new(amloddCharm) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gwir),
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("Lady Meilyr."),
    },
    postconditions = { Condition.InventoryContains:new(meilyrPotionRecipe) },
  },
  {
    text = "Inspect the symbol in the bank.",
    title = "Finding Lady Ithell",
    neededItems = { ["Magic logs"] = { quantity = 10 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2357, 1669, 3164) },
    postconditions = {
      Condition.ModelVisible:new(blueSymbolBank),
      Condition.ChatText:new("1/5"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(blueSymbolBank) },
    jumpconditions = { Condition.ModelNotVisible:new(blueSymbolBank) },
    jumpOffset = -1,
    postconditions = { Condition.ChatText:new("1/5") },
  },
  {
    text = "Inspect the symbol in the southern building.",
    actions = { Action.Direction:new(2339, 1669, 3155) },
    postconditions = {
      Condition.ModelVisible:new(blueSymbolSouthernBuilding),
      Condition.ChatText:new("2/5"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(blueSymbolSouthernBuilding) },
    jumpconditions = { Condition.ModelNotVisible:new(blueSymbolSouthernBuilding) },
    jumpOffset = -1,
    postconditions = { Condition.ChatText:new("2/5") },
  },
  {
    text = "Inspect the symbol in the ranging shop.",
    actions = { Action.Direction:new(2323.9, 1669, 3165) },
    postconditions = {
      Condition.ModelVisible:new(blueSymbolRangeShop),
      Condition.ChatText:new("3/5"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(blueSymbolRangeShop) },
    jumpconditions = { Condition.ModelNotVisible:new(blueSymbolRangeShop) },
    jumpOffset = -1,
    postconditions = { Condition.ChatText:new("3/5") },
  },
  {
    text = "Climb up the stairs in the clothing store.",
    actions = { Action.Direction:new(2328.5, 1969, 3176.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2327, 2821, 3176, 4) },
  },
  {
    text = "Inspect the symbol.",
    actions = { Action.ModelHighlight:new(blueSymbolClothingStore) },
    postconditions = { Condition.ChatText:new("4/5") },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(2328.5, 2821, 3176.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2328, 1669, 3175, 4) },
  },
  {
    text = "Climb up the stairs in the center of town.",
    actions = {
      Action.Direction:new(2339, 1685, 3167.5),
      Action.Direction:new(2339, 1685, 3175.5),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2343, 2789, 3168, 4),
      Condition.DistanceToWithHeight:new(2343, 2789, 3175, 4),
    },
  },
  {
    text = "Inspect the symbol.",
    actions = { Action.Direction:new(2357, 2789, 3177) },
    postconditions = { Condition.ModelVisible:new(blueSymbolAltar) },
  },
  {
    actions = { Action.ModelHighlight:new(blueSymbolAltar) },
    jumpconditions = { Condition.ModelNotVisible:new(blueSymbolAltar) },
    jumpOffset = -1,
    postconditions = { Condition.ChatText:new("5/5") },
  },
  {
    text = "Build the statue hotspot.",
    actions = { Action.Direction:new(2339.5, 1485, 3171.5) },
    postconditions = {
      Condition.ModelVisible:new(statueHotspot),
      Condition.ModelVisible:new(ithellStatue),
    },
  },
  {
    actions = { Action.ModelHighlight:new(statueHotspot) },
    jumpconditions = { Condition.ModelNotVisible:new(statueHotspot) },
    jumpOffset = -1,
    postconditions = { Condition.ModelVisible:new(ithellStatue) },
  },
  {
    text = "Climb up the stairs.",
    actions = {
      Action.Direction:new(2339, 1685, 3167.5),
      Action.Direction:new(2339, 1685, 3175.5),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2343, 2789, 3168, 4),
      Condition.DistanceToWithHeight:new(2343, 2789, 3175, 4),
    },
  },
  {
    text = "Talk to Kelyn.",
    actions = {
      Action.ModelHighlight:new(kelyn),
      Action.ConversationHighlight:new("It's time. I'm gathering the clan leaders."),
    },
    postconditions = { Condition.ConversationText:new("meet you there") },
  },
  {
    text = "Follow the path to the Underground Pass cave entrance.",
    title = "Finding Lady Trahaearn",
    neededItems = {
      ["Rune bar"] = { quantity = 1 },
      ["Pouch"] = { quantity = 1 },
      ["Spirit shard"] = { quantity = 1 },
      ["Gold charm"] = { quantity = 1 },
      ["Green charm"] = { quantity = 1 },
      ["Crimson charm"] = { quantity = 1 },
      ["Blue charm"] = { quantity = 1 },
      ["Amlodd charm"] = { quantity = 1, model = amloddCharm },
      ["Combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    recommendedItems = {},
    actions = {
      Action.PathGuide:new({
        Location:new(2254, 1301, 3149),
        Location:new(2248, 973, 3151),
        Location:new(2242, 1109, 3160),
        Location:new(2237, 909, 3169),
        Location:new(2233, 1093, 3173),
        Location:new(2233, 1013, 3181),
        Location:new(2238, 981, 3181),
        Location:new(2243, 1357, 3182),
        Location:new(2251, 989, 3182),
        Location:new(2259, 1141, 3183),
        Location:new(2263, 981, 3192),
        Location:new(2267, 1276, 3201),
        Location:new(2267, 1269, 3205),
        Location:new(2269, 1269, 3206),
        Location:new(2270, 1229, 3209),
        Location:new(2279, 1013, 3212),
        Location:new(2285, 893, 3213),
        Location:new(2290, 1157, 3208),
        Location:new(2295, 1061, 3207),
        Location:new(2300, 1021, 3210),
        Location:new(2305, 1165, 3213),
        Location:new(2310, 1300, 3217),
        Location:new(2314, 1341, 3217),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2314, 901, 9624, 4) },
  },
  {
    text = "Mine the cracked wall.",
    actions = { Action.ModelHighlight:new(crackedWall) },
    postconditions = { Condition.ModelVisible:new(crackedWallTunnel) },
  },
  {
    text = "Enter tunnel.",
    actions = { Action.ModelHighlight:new(crackedWallTunnel) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill the automatons.",
    warning = "No tracking for this step.",
    actions = { Action.ModelHighlight:new(trahaearnAutomaton, { highlightPriority = "all" }) },
  },
  {
    text = "Wake Lady Trahaearn.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["lady trahaearn"]),
      Action.ConversationHighlight:new("Repair the exoskeleton."),
      Action.ConversationHighlight:new("It's time. I'm gathering the clan leaders."),
    },
    postconditions = { Condition.ConversationText:new("meet you there") },
  },
  {
    text = "Exit the tunnel.",
    title = "Finding Lord Amlodd",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(0, 350, 2, { instance = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Use the Amlodd Charm on the obelisk.",
    actions = {
      Action.ModelHighlight:new(obelisk),
      Action.InventoryHighlight:new(amloddCharm),
      Action.ConversationHighlight:new("Create the Amlodd pouch."),
    },
    postconditions = { Condition.InventoryContains:new(amloddPouch) },
  },
  {
    text = "Summon Amlodd pouch.",
    actions = {
      Action.InventoryHighlight:new(amloddPouch),
      Action.ConversationHighlight:new("Are you my summoned creature now? Can I command you?"),
    },
    postconditions = { Condition.ConversationText:new("clever there") },
  },
  {
    actions = {
      Action.InventoryHighlight:new(amloddPouch),
      Action.ConversationHighlight:new("It's time. I'm gathering the clan leaders."),
    },
    postconditions = { Condition.ConversationText:new("Undercity") },
  },
  {
    text = "Pick a blue mushroom.",
    title = "Finding Lord Crwys",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    neededItems = {
      ["Plant cure"] = { quantity = 1 },
      ["Meilyr potion recipe"] = { quantity = 1, model = meilyrPotionRecipe },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(mushroomObj, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(blueMushroom) },
  },
  {
    text = "Pick a red mushroom.",
    actions = { Action.ModelHighlight:new(mushroomObj, { atLocation = Location:new(2236, 661, 3142) }) },
    postconditions = { Condition.InventoryContains:new(blueMushroom, 2) },
  },
  {
    text = "Follow the path to the marked tile.",
    actions = {
      Action.Direction:new(2217, 869, 3166, { tile = true }),
      Action.PathGuide:new({
        Location:new(2240, 973, 3149),
        Location:new(2237, 853, 3149),
        Location:new(2234, 869, 3149),
        Location:new(2231, 861, 3149),
        Location:new(2229, 869, 3147),
        Location:new(2225, 869, 3146),
        Location:new(2220, 1045, 3148),
        Location:new(2220, 965, 3152),
        Location:new(2220, 901, 3155),
        Location:new(2217, 869, 3158),
        Location:new(2217, 869, 3163),
        Location:new(2217, 869, 3166),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2217, 869, 3166, 0) },
  },
  {
    text = "Chop down tree.",
    actions = { Action.Direction:new(2218.5, 1269, 3166) },
    postconditions = { Condition.ModelVisible:new(treeOrb) },
  },
  {
    text = "Inspect tree.",
    actions = { Action.ModelHighlight:new(inspectTree) },
    postconditions = { Condition.ConversationText:new("diseased") },
  },
  {
    text = "Use plant cure on the tree.",
    actions = {
      Action.ModelHighlight:new(inspectTree),
      Action.InventoryHighlight:new(plantCure),
    },
    postconditions = { Condition.ModelVisible:new(Models.npcs["lord crwys"]) },
  },
  {
    text = "Talk to Lord Crwys.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["lord crwys"]),
      Action.ConversationHighlight:new("It's time. I'm gathering the clan leaders."),
    },
    postconditions = { Condition.ConversationText:new("Undercity") },
  },
  {
    text = "Follow the path across the rocks.",
    title = "Finding Lady Hefin",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "DLR",
    },
    neededItems = {},
    recommendedItems = { ["Dramen staff"] = { quantity = 1 } },
    actions = {
      Action.PathGuide:new({
        Location:new(2210, 341, 3101),
        Location:new(2208, 325, 3103),
        Location:new(2208, 349, 3105),
        Location:new(2207, 430, 3106),
        Location:new(2205, 357, 3107),
        Location:new(2203, 360, 3107),
        Location:new(2203, 360, 3109),
        Location:new(2203, 325, 3111),
        Location:new(2200, 357, 3111),
        Location:new(2198, 360, 3111),
        Location:new(2198, 360, 3109),
        Location:new(2198, 360, 3107),
        Location:new(2198, 357, 3105),
        Location:new(2195, 589, 3102),
        Location:new(2193, 325, 3100),
        Location:new(2193, 360, 3098),
        Location:new(2193, 360, 3096),
        Location:new(2193, 349, 3094),
        Location:new(2190, 357, 3093),
        Location:new(2188, 360, 3093),
        Location:new(2188, 325, 3095),
        Location:new(2185, 413, 3096),
        Location:new(2183, 360, 3096),
        Location:new(2181, 360, 3096),
        Location:new(2179, 333, 3096),
        Location:new(2176, 325, 3097),
        Location:new(2174, 360, 3097),
        Location:new(2174, 360, 3095),
        Location:new(2174, 360, 3093),
        Location:new(2174, 360, 3091),
        Location:new(2172, 349, 3091),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2172, 349, 3091, 0) },
  },
  {
    text = "Talk to Elf Hermit.",
    actions = {
      Action.ModelHighlight:new(elfHermit),
      Action.ConversationHighlight:new("I seek the wisdom of Seren."),
      Action.ConversationHighlight:new("Seren's faithfulness to the elves."),
      Action.ConversationHighlight:new("The symmetry of crystals."),
    },
    postconditions = {
      Condition.ConversationText:new("humility"),
      Condition.ConversationText:new("Undercity"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(elfHermit),
      Action.ConversationHighlight:new("I don't know."),
      Action.ConversationHighlight:new("It's time. I'm gathering the clan leaders."),
    },
    postconditions = { Condition.ConversationText:new("Undercity") },
  },
  {
    text = "Grind the blue Isafdar mushroom.",
    title = "Finding Lady Meilyr",
    neededItems = {
      ["Blue Isafdar mushroom"] = { quantity = 1 },
      ["Red Isafdar mushroom"] = { quantity = 1 },
      ["Super defence (3)"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(blueMushroom) },
    postconditions = { Condition.InventoryContains:new(blueMushroomPowder) },
  },
  {
    text = "Grind the red Isafdar mushroom.",
    actions = { Action.InventoryHighlight:new(blueMushroom) },
    postconditions = { Condition.InventoryContains:new(redMushroomPowder) },
  },
  {
    text = "Use the ground blue Isafdar mushroom on a super defence (3) potion.",
    actions = {
      Action.InventoryHighlight:new(blueMushroomPowder),
      Action.InventoryHighlight:new(superDefence3),
    },
    postconditions = { Condition.InventoryContains:new(meilyrPotion) },
  },
  {
    text = "Drink Meilyr potion in the Dungeoneering lobby at Daemonheim.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Teleport to Daemonheim",
      url = "Ring_of_kinship.png",
    },
    actions = {
      Action.Direction:new(3449, 13189, 3726),
      Action.InventoryHighlight:new(meilyrPotion),
    },
    postconditions = { Condition.ModelVisible:new(meilyrPortal) },
  },
  {
    text = "Enter the portal that appears on the west side.",
    actions = { Action.ModelHighlight:new(meilyrPortal) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Lady Meilyr.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["lady meilyr"]),
      Action.ConversationHighlight:new("It's time. I'm gathering the clan leaders."),
    },
    postconditions = { Condition.ConversationText:new("Undercity") },
  },
  {
    text = "Talk to Arianwyn in Lletya.",
    title = "Distraction",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    neededItems = {
      ["Combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    recommendedItems = { ["Coins"] = { quantity = 3200 } },
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.ConversationHighlight:new("I'm ready to teleport."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Iestin Edern.",
    actions = { Action.ModelHighlight:new(Models.npcs["iestin edern"]) },
    postconditions = { Condition.ConversationText:new("Lord Iorwerth for good") },
  },
  {
    text = "Talk to General Hining in the Tyras Camp.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Charter to Port Tyras",
      url = "Transportation_map_icon.png",
    },
    actions = { Action.Direction:new(2186, 2565, 3148) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["general hining"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["general hining"]),
      Action.ConversationHighlight:new("The time has come to face the darkness."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["general hining"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("our support") },
  },
  {
    text = "Return to Arianwyn in Lletya.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.ConversationHighlight:new("I'm ready to teleport."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Iestin Edern.",
    actions = { Action.ModelHighlight:new(Models.npcs["iestin edern"]) },
    postconditions = { Condition.ConversationText:new("finish this") },
  },
  {
    text = "Enter the Grand Library.",
    actions = { Action.ModelHighlight:new(grandLibraryDoorSymbols) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Lord Crwys.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["lord crwys"]),
      Action.ConversationHighlight:new("Okay, what do I do now?"),
    },
    postconditions = { Condition.ChatText:new("Stage 1/8:") },
  },
  {
    text = "Put a mirror on the plinth. Orientate the mirror so the beam will go north.",
    title = "Cadarn",
    actions = { Action.Direction:new(-17, 392, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, -3) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-17, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 3) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-23, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-23, 392, 3) }),
    },
  },
  {
    text = "Activate the Seal of Cadarn.",
    actions = {
      Action.Direction:new(-12, 492, -3, { instance = true }),
      Action.ConversationHighlight:new("what do I do"),
    },
    postconditions = { Condition.ConversationText:new("Seal of Seren") },
  },
  {
    text = "Kill the shadows.",
    actions = { Action.ModelHighlight:new(shadows, { highlightPriority = "all" }) },
    postconditions = { Condition.ModelNotVisible:new(shadows) },
  },
  {
    text = "East.",
    title = "Trahaearn",
    actions = { Action.Direction:new(-20, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, -6) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-17, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-16, 392, -6) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-17, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 6) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-23, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-24, 392, 6) }),
    },
  },
  {
    text = "Activate the Seal of Trahaearn.",
    actions = { Action.Direction:new(-20, 492, -11, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Seal of Seren") },
  },
  {
    text = "Kill the shadows.",
    actions = { Action.ModelHighlight:new(shadows, { highlightPriority = "all" }) },
    postconditions = { Condition.ModelNotVisible:new(shadows) },
  },
  {
    text = "West.",
    title = "Iorwerth",
    actions = { Action.Direction:new(-26, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, -9) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-29, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-30, 392, -9) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-29, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-30, 392, 9) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-17, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 10) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-17, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 5) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-23, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-24, 392, 6) }),
    },
  },
  {
    text = "Activate the Seal of Iorwerth.",
    actions = { Action.Direction:new(-26, 492, -11, { instance = true }) },
    postconditions = { Condition.ConversationText:new("is that") },
  },
  {
    text = "Talk to the Dark Lord.",
    actions = { Action.ConversationHighlight:new("want to talk") },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Fight the Dark Lord and minion.<ul><li>Avoid his AoEs.</li></ul>",
    warning = "No tracking for this step.",
  },
  {
    text = "North.",
    title = "Ithell",
    actions = { Action.Direction:new(-32, 392, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, -3) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-32, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, 3) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-26, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, 3) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-26, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, 9) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-20, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, 9) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-20, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, 3) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-17, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 3) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-17, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 6) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-14, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, 6) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-14, 392, 0, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, 0) }),
    },
  },
  {
    text = "Activate the Seal of Ithell.",
    actions = { Action.Direction:new(-34, 492, -3, { instance = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ConversationHighlight:new("fight") },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Fight the Dark Lord and minions.",
    warning = "No tracking for this step.",
  },
  {
    text = "North.",
    title = "Amlodd",
    actions = { Action.Direction:new(-32, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, 3) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-32, -192, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, 6) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-29, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-29, 392, 6) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-29, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-29, 392, -6) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-23, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-23, 392, -6) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-23, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-23, 392, -9) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-17, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, -9) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-17, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, -6) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-14, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, -6) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-14, 392, 0, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, 0) }),
    },
  },
  {
    text = "Activate the Seal of Amlodd.",
    actions = { Action.Direction:new(-34, 492, 3, { instance = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ConversationHighlight:new("fight") },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Fight the Dark Lord and minions.",
    warning = "No tracking for this step.",
  },
  {
    text = "South.",
    title = "Hefin",
    actions = { Action.Direction:new(-14, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, 9) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-14, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, -9) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-32, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, -9) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-32, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, 9) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-29, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-29, 392, -6) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-17, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, -6) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-17, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 6) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-26, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, 6) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-26, 392, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, -3) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-20, 392, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, -3) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-20, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, 3) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-23, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-23, 392, 3) }),
    },
  },
  {
    text = "Activate the Seal of Hefin.",
    actions = { Action.Direction:new(-26, 492, 11, { instance = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ConversationHighlight:new("fight") },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Fight the Dark Lord and minions.",
    warning = "No tracking for this step.",
  },
  {
    text = "South.",
    title = "Meilyr",
    actions = { Action.Direction:new(-17, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 9) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-17, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 6) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-26, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, 6) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-26, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, 3) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-32, 392, 3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, 3) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-32, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, -6) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-29, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-29, 392, -6) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-29, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-29, 392, -9) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-17, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, -9) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-17, 392, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, -3) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-20, 392, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, -3) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-20, 392, 0, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, 0) }),
    },
  },
  {
    text = "Activate the Seal of Meilyr.",
    actions = { Action.Direction:new(-20, 492, 11, { instance = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ConversationHighlight:new("fight") },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Fight the Dark Lord and minions.",
    warning = "No tracking for this step.",
  },
  {
    text = "East",
    title = "Crwys",
    actions = { Action.Direction:new(-17, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-17, 392, 6) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-14, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, 6) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-14, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, 9) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-14, 392, 0, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, 0) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-14, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-14, 392, -9) }),
    },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(-20, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-20, 392, -9) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-32, 392, -9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, -9) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-26, 392, -6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, -6) }),
    },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(-32, 392, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, -3) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-32, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-32, 392, 9) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-29, 392, 6, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-29, 392, 6) }),
    },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(-26, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-26, 392, 9) }),
    },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(-23, 392, 9, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(mirror, { instance = true, atLocation = Location:new(-23, 392, 9) }),
    },
  },
  {
    text = "Activate the Seal of Crwys.",
    actions = { Action.Direction:new(-20, 492, 11, { instance = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Talk to Arianwyn.<ul><li>Recommended: Talk to Arianwyn to teleport to the entrance of Prifddinas, or walk there yourself. After a skippable cutscene, the city of Prifddinas will be unlocked.</li></ul>",
    title = "Finishing up",
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Plague's End",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1407715200,
  prereqQuests = { "Within the Light", "Catapult Construction", "Making History" },
  questReqs = {
    Types.QuestReq.skill("Agility", 75),
    Types.QuestReq.skill("Construction", 75),
    Types.QuestReq.skill("Crafting", 75),
    Types.QuestReq.skill("Dungeoneering", 75),
    Types.QuestReq.skill("Herblore", 75),
    Types.QuestReq.skill("Mining", 75),
    Types.QuestReq.skill("Prayer", 75),
    Types.QuestReq.skill("Ranged", 75),
    Types.QuestReq.skill("Summoning", 75),
    Types.QuestReq.skill("Woodcutting", 75),
  },
  neededItems = {
    ["Super defence (3)"] = { quantity = 1, model = superDefence3 },
    ["Mourner gear"] = { quantity = 1, model = Models.items["mourner gear"] },
    ["Rune bar"] = { quantity = 1, model = Models.items["rune bar"] },
    ["Plant cure"] = { quantity = 1, model = plantCure },
    ["Magic logs"] = { quantity = 10, model = Models.items["magic logs"] },
    ["Spirit shard"] = { quantity = 200, model = spiritShard },
    ["Gold charm"] = { quantity = 1, model = goldCharm },
    ["Green charm"] = { quantity = 1, model = greenCharm },
    ["Crimson charm"] = { quantity = 1, model = crimsonCharm },
    ["Blue charm"] = { quantity = 1, model = blueCharm },
    ["Pouch"] = { quantity = 1, model = spiritPouch },
    ["Combat gear"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  recommendedItems = {
    ["Coins"] = { quantity = 3200 },
    ["Tirannwn quiver"] = { quantity = 1 },
    ["Ring of kinship"] = { quantity = 1 },
    ["Dramen staff"] = { quantity = 1 },
    ["Crystal teleport seed"] = { quantity = 1 },
    ["Antipoison"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Sir Hugo"] = { level = "107", quantity = 1 },
    ["Trahaearn automatons"] = { level = "70", quantity = 3 },
    ["shadows"] = { level = "107", quantity = 1 },
    ["Dark Lord"] = { level = "107", quantity = 1 },
    ["Mourner"] = { level = "83", quantity = 1 },
    ["Head Mourner"] = { level = "100", quantity = 1 },
  },
})
