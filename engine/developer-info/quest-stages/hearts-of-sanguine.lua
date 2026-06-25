local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local liat = Model.new(5757, {
  [164] = Vertex.new(-44, 433, -57, 26, 44, 51),
  [167] = Vertex.new(-48, 434, -49, 26, 44, 51),
  [243] = Vertex.new(-38, 454, -99, 105, 91, 54),
  [1032] = Vertex.new(28, 269, -111, 159, 154, 145),
  [2158] = Vertex.new(-39, 450, -97, 105, 91, 54),
})
local gidon = Model.new(7443, {
  [42] = Vertex.new(-206, 264, -189, 83, 79, 76),
  [216] = Vertex.new(-237, 204, -157, 45, 42, 41),
  [218] = Vertex.new(-237, 254, -188, 45, 42, 41),
  [222] = Vertex.new(-175, 230, -148, 45, 42, 41),
  [224] = Vertex.new(-175, 206, -202, 45, 42, 41),
})
local matthew = Model.new(7623, {
  [2155] = Vertex.new(0, 735, -7, 29, 141, 128),
  [2156] = Vertex.new(0, 732, -7, 29, 141, 128),
  [2157] = Vertex.new(0, 732, -8, 29, 141, 128),
  [2971] = Vertex.new(-132, 377, -18, 127, 127, 127),
  [2974] = Vertex.new(132, 377, -18, 127, 127, 127),
})
local miriam = Model.new(13791, {
  [8360] = Vertex.new(23, 675, 18, 127, 127, 127),
  [11447] = Vertex.new(26, 767, -27, 39, 36, 35),
  [11663] = Vertex.new(-11, 736, 21, 39, 36, 35),
  [11666] = Vertex.new(-11, 736, 21, 39, 36, 35),
  [11677] = Vertex.new(20, 735, 12, 39, 36, 35),
})
local jacob = Model.new(15276, {
  [5344] = Vertex.new(3, 473, -69, 128, 128, 128),
  [5369] = Vertex.new(-4, 464, -67, 128, 128, 128),
  [5372] = Vertex.new(-38, 665, -9, 128, 128, 128),
  [5440] = Vertex.new(38, 665, -9, 128, 128, 128),
  [7837] = Vertex.new(-126, 583, 20, 128, 128, 128),
})
local farmerRachel = Model.new(10650, {
  [32] = Vertex.new(23, 767, -45, 154, 141, 118),
  [38] = Vertex.new(0, 788, -49, 154, 141, 118),
  [40] = Vertex.new(0, 759, 55, 154, 141, 118),
  [71] = Vertex.new(-23, 767, -45, 154, 141, 118),
  [76] = Vertex.new(0, 788, -49, 154, 141, 118),
})
local hedgehog = Model.new(15000, {
  [6629] = Vertex.new(10, 39, -68, 127, 127, 127),
  [6665] = Vertex.new(7, 35, -71, 127, 127, 127),
  [7412] = Vertex.new(-10, 39, -68, 127, 127, 127),
  [7414] = Vertex.new(-10, 39, -68, 127, 127, 127),
  [7447] = Vertex.new(-7, 35, -71, 127, 127, 127),
})
local duck = Model.new(27162, {
  [35] = Vertex.new(-164, 103, -38, 128, 128, 128),
  [37] = Vertex.new(164, 103, -38, 128, 128, 128),
  [1097] = Vertex.new(-159, 109, -28, 128, 128, 128),
  [1445] = Vertex.new(159, 109, -28, 128, 128, 128),
  [9202] = Vertex.new(-164, 103, -38, 128, 128, 128),
})
local badger = Model.new(40374, {
  [3119] = Vertex.new(6, 95, -199, 127, 127, 127),
  [3662] = Vertex.new(-6, 95, -199, 127, 127, 127),
  [12743] = Vertex.new(12, 121, -199, 127, 127, 127),
  [12814] = Vertex.new(12, 114, -199, 127, 127, 127),
  [13219] = Vertex.new(-12, 121, -199, 127, 127, 127),
})
local sanguineTumor = Model.new(2946, {
  [205] = Vertex.new(-52, 296, 2, 127, 127, 127),
  [259] = Vertex.new(0, 301, 51, 127, 127, 127),
  [262] = Vertex.new(0, 301, 51, 127, 127, 127),
  [983] = Vertex.new(52, 296, 2, 127, 127, 127),
  [1037] = Vertex.new(0, 301, 51, 127, 127, 127),
})
--#endregion
--#region Objects
local deadBear = Model.new(2802, {
  [2] = Vertex.new(-24, 357, -522, 128, 127, 127),
  [4] = Vertex.new(-25, 358, -510, 128, 127, 127),
  [5] = Vertex.new(-24, 357, -522, 128, 127, 127),
  [27] = Vertex.new(24, 357, -522, 128, 127, 127),
  [28] = Vertex.new(25, 358, -510, 128, 127, 127),
})
local bloodsplatter = Model.new(54, {
  [1] = Vertex.new(-510, -8, -510, 127, 127, 127),
  [30] = Vertex.new(510, 46, -510, 127, 127, 127),
  [53] = Vertex.new(510, 189, 510, 127, 127, 127),
})
local caveEntrance = Model.new(19302, {
  [3189] = Vertex.new(1361, -572, 645, 127, 127, 127),
  [3898] = Vertex.new(928, 697, -320, 127, 127, 127),
  [3901] = Vertex.new(928, 697, -320, 127, 127, 127),
  [3905] = Vertex.new(928, 697, -320, 127, 127, 127),
  [13388] = Vertex.new(-725, 1221, -427, 127, 127, 127),
})
local poppiesObj = Model.new(372, {
  [92] = Vertex.new(-120, 392, 312, 128, 127, 127),
  [185] = Vertex.new(318, 452, -340, 128, 127, 127),
  [218] = Vertex.new(67, 423, 348, 128, 127, 127),
  [302] = Vertex.new(-200, 452, -386, 128, 127, 127),
  [304] = Vertex.new(318, 452, -341, 128, 127, 127),
})
local wolfsTongueMushroomObj = Model.new(1242, {
  [674] = Vertex.new(13, 153, -32, 128, 128, 127),
  [724] = Vertex.new(-24, 149, 24, 128, 128, 127),
  [808] = Vertex.new(-24, 157, -24, 128, 128, 127),
  [839] = Vertex.new(105, 75, -16, 128, 128, 127),
  [868] = Vertex.new(114, 69, 29, 128, 128, 127),
})
local vialCabinet = Model.new(1392, {
  [673] = Vertex.new(-208, 588, 193, 127, 127, 127),
  [1325] = Vertex.new(172, 829, 152, 127, 127, 127),
  [1327] = Vertex.new(151, 832, 164, 127, 127, 127),
  [1385] = Vertex.new(69, 832, 92, 127, 127, 127),
  [1387] = Vertex.new(47, 835, 104, 127, 127, 127),
})
local vatOfOil = Model.new(900, {
  [42] = Vertex.new(-53, 740, 33, 127, 127, 127),
  [45] = Vertex.new(-12, 740, -58, 127, 127, 127),
  [584] = Vertex.new(58, 740, 6, 127, 127, 127),
  [793] = Vertex.new(-53, 740, 33, 127, 127, 127),
  [884] = Vertex.new(-53, 740, 33, 127, 127, 127),
})
local bucketOfSpines = Model.new(1404, {
  [622] = Vertex.new(70, 381, 55, 127, 127, 127),
  [625] = Vertex.new(70, 381, 55, 127, 127, 127),
  [634] = Vertex.new(72, 372, 49, 127, 127, 127),
  [1048] = Vertex.new(-74, 348, 45, 127, 127, 127),
  [1066] = Vertex.new(-75, 353, 34, 127, 127, 127),
})
local sackOfFlowers = Model.new(1008, {
  [491] = Vertex.new(-62, 455, -145, 127, 127, 127),
  [494] = Vertex.new(-62, 455, -145, 127, 127, 127),
  [571] = Vertex.new(282, 258, 183, 127, 127, 127),
  [769] = Vertex.new(-62, 455, -145, 127, 127, 127),
  [826] = Vertex.new(80, 504, 48, 127, 127, 127),
})
local roundTable = Model.new(3948, {
  [221] = Vertex.new(-347, 66, 319, 127, 127, 127),
  [295] = Vertex.new(-331, 58, 333, 127, 127, 127),
  [637] = Vertex.new(-347, 66, 319, 127, 127, 127),
  [1180] = Vertex.new(-303, 315, 431, 127, 127, 127),
  [1283] = Vertex.new(-331, 66, 335, 127, 127, 127),
})
local sackOfMushrooms = Model.new(4929, {
  [3650] = Vertex.new(-187, 457, 166, 128, 128, 127),
  [3902] = Vertex.new(-177, 472, -138, 128, 128, 127),
  [3905] = Vertex.new(-163, 462, -171, 128, 128, 127),
  [4246] = Vertex.new(188, 418, -164, 128, 128, 127),
  [4254] = Vertex.new(185, 418, -169, 128, 128, 127),
})
local desk = Model.new(8223, {
  [6777] = Vertex.new(366, 477, 155, 128, 128, 127),
  [7036] = Vertex.new(349, 531, -144, 128, 128, 127),
  [7047] = Vertex.new(414, 532, -78, 128, 128, 127),
  [7841] = Vertex.new(361, 588, -71, 127, 127, 127),
  [8016] = Vertex.new(360, 604, -69, 127, 127, 127),
})
local bloodsplatter2 = Model.new(54, {
  [1] = Vertex.new(-1226, -37, -1226, 127, 127, 127),
  [17] = Vertex.new(-1226, 57, 1226, 127, 127, 127),
  [30] = Vertex.new(1226, -37, -1226, 127, 127, 127),
  [53] = Vertex.new(1226, -69, 1226, 127, 127, 127),
})
local caveEntrance2 = Model.new(3708, {
  [23] = Vertex.new(836, 1442, -1369, 127, 127, 127),
  [419] = Vertex.new(-826, 1442, -1339, 127, 127, 127),
  [1868] = Vertex.new(1059, 1823, -1384, 127, 127, 127),
  [2132] = Vertex.new(1059, 1823, -1384, 127, 127, 127),
  [2134] = Vertex.new(1059, 1823, -1384, 127, 127, 127),
})
local furnace = Model.new(7029, {
  [1533] = Vertex.new(5500, 8661, 2895, 128, 127, 127),
  [2049] = Vertex.new(5723, 8976, 2645, 127, 127, 127),
  [2052] = Vertex.new(5729, 8989, 2691, 127, 127, 127),
  [3254] = Vertex.new(5687, 8689, 2663, 127, 127, 127),
  [3977] = Vertex.new(5365, 8689, 2981, 127, 127, 127),
})
--#endregion
--#region Items
local wendlewickAle = Model.new(306, {
  [237] = Vertex.new(48, 80, 0, 149, 139, 95),
  [266] = Vertex.new(48, 72, 16, 149, 139, 95),
  [270] = Vertex.new(48, 72, 16, 149, 139, 95),
  [287] = Vertex.new(48, 72, 16, 149, 139, 95),
  [305] = Vertex.new(48, 80, 0, 149, 139, 95),
})
local havensilverGreatsword = Model.new(1518, {
  [125] = Vertex.new(223, -3, 243, 127, 127, 127),
  [129] = Vertex.new(223, -3, 243, 127, 127, 127),
  [275] = Vertex.new(244, -1, 222, 127, 127, 127),
  [471] = Vertex.new(-254, -2, -251, 127, 127, 128),
  [593] = Vertex.new(-254, -2, -251, 127, 127, 128),
})
--#endregion
--#region Quest Items
local wendlewickPoppy = Model.new(264, {
  [196] = Vertex.new(26, 32, 56, 168, 28, 15),
  [201] = Vertex.new(26, 32, 56, 168, 28, 15),
  [208] = Vertex.new(56, 32, -26, 168, 28, 15),
  [213] = Vertex.new(56, 32, -26, 168, 28, 15),
  [220] = Vertex.new(-26, 32, -56, 168, 28, 15),
})
local wolfsTongue = Model.new(234, {
  [39] = Vertex.new(-45, 55, 25, 187, 187, 187),
  [43] = Vertex.new(-45, 55, 25, 187, 187, 187),
  [49] = Vertex.new(-45, 55, 25, 187, 187, 187),
  [118] = Vertex.new(-45, 55, 25, 133, 131, 122),
  [123] = Vertex.new(-45, 55, 25, 133, 131, 122),
})
local hedgehogSpines = Model.new(510, {
  [36] = Vertex.new(-54, 16, -97, 126, 107, 97),
  [51] = Vertex.new(-54, 16, -97, 74, 62, 57),
  [56] = Vertex.new(-54, 16, -97, 74, 62, 57),
  [223] = Vertex.new(-66, 21, -95, 110, 84, 70),
  [411] = Vertex.new(-54, 16, -97, 56, 47, 43),
})
local vialOfOil = Model.multi({
  Model.new(90, {
    [2] = Vertex.new(-20, 52, 8, 126, 154, 14),
    [11] = Vertex.new(-12, 52, -20, 126, 154, 14),
    [14] = Vertex.new(12, 52, 20, 126, 154, 14),
    [23] = Vertex.new(20, 52, -16, 126, 154, 14),
    [67] = Vertex.new(-4, 96, 12, 115, 103, 47),
    [68] = Vertex.new(-4, 112, 12, 115, 103, 47),
    [69] = Vertex.new(-12, 112, 4, 115, 103, 47),
    [72] = Vertex.new(-12, 96, 4, 115, 103, 47),
    [74] = Vertex.new(-4, 112, -12, 115, 103, 47),
    [75] = Vertex.new(-12, 112, -4, 115, 103, 47),
    [77] = Vertex.new(4, 112, -12, 115, 103, 47),
    [80] = Vertex.new(12, 112, -4, 115, 103, 47),
    [83] = Vertex.new(12, 112, 4, 115, 103, 47),
    [86] = Vertex.new(4, 112, 12, 115, 103, 47),
  }),
  Model.new(288, {
    [1] = Vertex.new(-20, 84, 4, 136, 137, 148, 0.4980),
    [3] = Vertex.new(-20, 84, -4, 136, 137, 148, 0.4980),
    [10] = Vertex.new(-4, 84, 20, 136, 137, 148, 0.4980),
    [15] = Vertex.new(-4, 84, -20, 136, 137, 148, 0.4980),
    [22] = Vertex.new(4, 84, 20, 136, 137, 148, 0.4980),
    [27] = Vertex.new(4, 84, -20, 136, 137, 148, 0.4980),
    [34] = Vertex.new(20, 84, 4, 136, 137, 148, 0.4980),
    [39] = Vertex.new(20, 84, -4, 136, 137, 148, 0.4980),
    [241] = Vertex.new(-4, 96, 20, 136, 137, 148, 0.4980),
    [242] = Vertex.new(4, 96, 12, 136, 137, 148, 0.4980),
    [243] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
    [244] = Vertex.new(-12, 96, 4, 136, 137, 148, 0.4980),
    [248] = Vertex.new(4, 96, 20, 136, 137, 148, 0.4980),
    [251] = Vertex.new(-20, 96, 4, 136, 137, 148, 0.4980),
    [254] = Vertex.new(12, 96, 4, 136, 137, 148, 0.4980),
    [256] = Vertex.new(-20, 96, -4, 136, 137, 148, 0.4980),
    [260] = Vertex.new(20, 96, 4, 136, 137, 148, 0.4980),
    [264] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [265] = Vertex.new(12, 96, -4, 136, 137, 148, 0.4980),
    [270] = Vertex.new(-4, 96, -12, 136, 137, 148, 0.4980),
  }),
})
local vialOfOilS = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 107, 132, 11),
    [2] = Vertex.new(-20, 52, 8, 107, 132, 11),
    [3] = Vertex.new(-12, 52, 20, 107, 132, 11),
    [6] = Vertex.new(-20, 52, -12, 107, 132, 11),
    [9] = Vertex.new(12, 52, 20, 107, 132, 11),
    [11] = Vertex.new(-12, 52, -20, 107, 132, 11),
    [15] = Vertex.new(20, 52, 8, 107, 132, 11),
    [17] = Vertex.new(12, 52, -20, 107, 132, 11),
    [21] = Vertex.new(20, 52, -16, 107, 132, 11),
    [25] = Vertex.new(-12, 96, 4, 113, 101, 46),
    [26] = Vertex.new(-12, 112, 4, 113, 101, 46),
    [27] = Vertex.new(-12, 112, -4, 113, 101, 46),
    [30] = Vertex.new(-12, 96, -4, 113, 101, 46),
    [33] = Vertex.new(-4, 112, -12, 113, 101, 46),
  }),
  Model.new(288, {
    [49] = Vertex.new(4, 0, -36, 107, 132, 11, 0.8745),
    [50] = Vertex.new(40, 12, -12, 107, 132, 11, 0.8745),
    [51] = Vertex.new(32, 0, -4, 107, 132, 11, 0.8745),
    [53] = Vertex.new(12, 12, -40, 107, 132, 11, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 107, 132, 11, 0.8745),
    [60] = Vertex.new(40, 12, 12, 107, 132, 11, 0.8745),
    [63] = Vertex.new(32, 0, 4, 107, 132, 11, 0.8745),
    [65] = Vertex.new(20, 52, -16, 107, 132, 11, 0.8745),
    [69] = Vertex.new(20, 52, 8, 107, 132, 11, 0.8745),
    [74] = Vertex.new(12, 52, -20, 107, 132, 11, 0.8745),
    [78] = Vertex.new(12, 52, 20, 107, 132, 11, 0.8745),
    [79] = Vertex.new(-12, 12, -40, 107, 132, 11, 0.8745),
    [86] = Vertex.new(-12, 52, -20, 107, 132, 11, 0.8745),
    [88] = Vertex.new(-32, 0, -4, 107, 132, 11, 0.8745),
    [93] = Vertex.new(12, 12, 40, 107, 132, 11, 0.8745),
    [99] = Vertex.new(-12, 52, 20, 107, 132, 11, 0.8745),
    [102] = Vertex.new(4, 0, 36, 107, 132, 11, 0.8745),
    [104] = Vertex.new(-40, 12, -12, 107, 132, 11, 0.8745),
    [109] = Vertex.new(-32, 0, 4, 107, 132, 11, 0.8745),
    [113] = Vertex.new(-20, 52, -12, 107, 132, 11, 0.8745),
  }),
})
local crushedHedgehogSpines = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 126, 87, 65),
  [34] = Vertex.new(100, 0, 60, 126, 87, 65),
  [42] = Vertex.new(100, 0, 60, 126, 87, 65),
  [64] = Vertex.new(132, 0, -24, 126, 87, 65),
  [72] = Vertex.new(132, 0, -24, 126, 87, 65),
})
local vialOfOilSH = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 126, 87, 65),
    [2] = Vertex.new(-20, 52, 8, 126, 87, 65),
    [3] = Vertex.new(-12, 52, 20, 126, 87, 65),
    [6] = Vertex.new(-20, 52, -12, 126, 87, 65),
    [9] = Vertex.new(12, 52, 20, 126, 87, 65),
    [11] = Vertex.new(-12, 52, -20, 126, 87, 65),
    [15] = Vertex.new(20, 52, 8, 126, 87, 65),
    [17] = Vertex.new(12, 52, -20, 126, 87, 65),
    [21] = Vertex.new(20, 52, -16, 126, 87, 65),
    [25] = Vertex.new(-12, 96, 4, 113, 101, 46),
    [26] = Vertex.new(-12, 112, 4, 113, 101, 46),
    [27] = Vertex.new(-12, 112, -4, 113, 101, 46),
    [30] = Vertex.new(-12, 96, -4, 113, 101, 46),
    [33] = Vertex.new(-4, 112, -12, 113, 101, 46),
  }),
  Model.new(288, {
    [49] = Vertex.new(4, 0, -36, 126, 87, 65, 0.8745),
    [50] = Vertex.new(40, 12, -12, 126, 87, 65, 0.8745),
    [51] = Vertex.new(32, 0, -4, 126, 87, 65, 0.8745),
    [53] = Vertex.new(12, 12, -40, 126, 87, 65, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 126, 87, 65, 0.8745),
    [60] = Vertex.new(40, 12, 12, 126, 87, 65, 0.8745),
    [63] = Vertex.new(32, 0, 4, 126, 87, 65, 0.8745),
    [65] = Vertex.new(20, 52, -16, 126, 87, 65, 0.8745),
    [69] = Vertex.new(20, 52, 8, 126, 87, 65, 0.8745),
    [74] = Vertex.new(12, 52, -20, 126, 87, 65, 0.8745),
    [78] = Vertex.new(12, 52, 20, 126, 87, 65, 0.8745),
    [79] = Vertex.new(-12, 12, -40, 126, 87, 65, 0.8745),
    [86] = Vertex.new(-12, 52, -20, 126, 87, 65, 0.8745),
    [88] = Vertex.new(-32, 0, -4, 126, 87, 65, 0.8745),
    [93] = Vertex.new(12, 12, 40, 126, 87, 65, 0.8745),
    [99] = Vertex.new(-12, 52, 20, 126, 87, 65, 0.8745),
    [102] = Vertex.new(4, 0, 36, 126, 87, 65, 0.8745),
    [104] = Vertex.new(-40, 12, -12, 126, 87, 65, 0.8745),
    [109] = Vertex.new(-32, 0, 4, 126, 87, 65, 0.8745),
    [113] = Vertex.new(-20, 52, -12, 126, 87, 65, 0.8745),
  }),
})
local vialOfOilSHP = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 108, 74, 68),
    [2] = Vertex.new(-20, 52, 8, 108, 74, 68),
    [3] = Vertex.new(-12, 52, 20, 108, 74, 68),
    [6] = Vertex.new(-20, 52, -12, 108, 74, 68),
    [9] = Vertex.new(12, 52, 20, 108, 74, 68),
    [11] = Vertex.new(-12, 52, -20, 108, 74, 68),
    [15] = Vertex.new(20, 52, 8, 108, 74, 68),
    [25] = Vertex.new(-12, 96, 4, 113, 101, 46),
    [26] = Vertex.new(-12, 112, 4, 113, 101, 46),
    [27] = Vertex.new(-12, 112, -4, 113, 101, 46),
    [30] = Vertex.new(-12, 96, -4, 113, 101, 46),
    [33] = Vertex.new(-4, 112, -12, 113, 101, 46),
    [36] = Vertex.new(-4, 96, -12, 113, 101, 46),
    [39] = Vertex.new(4, 112, -12, 113, 101, 46),
  }),
  Model.new(288, {
    [1] = Vertex.new(-20, 84, 4, 134, 135, 147, 0.4980),
    [2] = Vertex.new(-20, 96, -4, 134, 135, 147, 0.4980),
    [3] = Vertex.new(-20, 84, -4, 134, 135, 147, 0.4980),
    [5] = Vertex.new(-20, 96, 4, 134, 135, 147, 0.4980),
    [9] = Vertex.new(-4, 96, -20, 134, 135, 147, 0.4980),
    [10] = Vertex.new(-4, 84, 20, 134, 135, 147, 0.4980),
    [15] = Vertex.new(-4, 84, -20, 134, 135, 147, 0.4980),
    [17] = Vertex.new(-4, 96, 20, 134, 135, 147, 0.4980),
    [21] = Vertex.new(4, 96, -20, 134, 135, 147, 0.4980),
    [22] = Vertex.new(4, 84, 20, 134, 135, 147, 0.4980),
    [49] = Vertex.new(4, 0, -36, 108, 74, 68, 0.8745),
    [50] = Vertex.new(40, 12, -12, 108, 74, 68, 0.8745),
    [51] = Vertex.new(32, 0, -4, 108, 74, 68, 0.8745),
    [53] = Vertex.new(12, 12, -40, 108, 74, 68, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 108, 74, 68, 0.8745),
    [60] = Vertex.new(40, 12, 12, 108, 74, 68, 0.8745),
    [63] = Vertex.new(32, 0, 4, 108, 74, 68, 0.8745),
    [65] = Vertex.new(20, 52, -16, 108, 74, 68, 0.8745),
    [69] = Vertex.new(20, 52, 8, 108, 74, 68, 0.8745),
    [74] = Vertex.new(12, 52, -20, 108, 74, 68, 0.8745),
  }),
})
local antisanguinePotion = Model.multi({
  Model.new(66, {
    [2] = Vertex.new(4, 112, -12, 113, 101, 46),
    [3] = Vertex.new(4, 96, -12, 113, 101, 46),
    [9] = Vertex.new(-4, 96, 12, 113, 101, 46),
    [11] = Vertex.new(4, 112, 12, 113, 101, 46),
    [12] = Vertex.new(-4, 112, 12, 113, 101, 46),
    [14] = Vertex.new(-4, 112, -12, 113, 101, 46),
    [15] = Vertex.new(-4, 96, -12, 113, 101, 46),
    [20] = Vertex.new(-12, 112, -4, 113, 101, 46),
    [21] = Vertex.new(-12, 96, -4, 113, 101, 46),
    [23] = Vertex.new(-12, 112, 4, 113, 101, 46),
  }),
  Model.new(240, {
    [50] = Vertex.new(-4, 84, 12, 107, 47, 43, 0.8745),
    [53] = Vertex.new(-12, 84, 4, 107, 47, 43, 0.8745),
    [60] = Vertex.new(-12, 84, -4, 107, 47, 43, 0.8745),
    [95] = Vertex.new(-4, 84, -12, 107, 47, 43, 0.8745),
    [96] = Vertex.new(4, 84, -12, 107, 47, 43, 0.8745),
    [131] = Vertex.new(12, 84, -4, 107, 47, 43, 0.8745),
    [132] = Vertex.new(12, 84, 4, 107, 47, 43, 0.8745),
    [146] = Vertex.new(16, 68, -4, 107, 47, 43, 0.8745),
    [150] = Vertex.new(4, 84, 12, 107, 47, 43, 0.8745),
    [152] = Vertex.new(40, 12, -12, 107, 47, 43, 0.8745),
    [153] = Vertex.new(40, 12, 12, 107, 47, 43, 0.8745),
    [157] = Vertex.new(32, 0, -4, 107, 47, 43, 0.8745),
    [159] = Vertex.new(32, 0, 4, 107, 47, 43, 0.8745),
    [160] = Vertex.new(16, 68, 4, 107, 47, 43, 0.8745),
    [171] = Vertex.new(-4, 68, 16, 107, 47, 43, 0.8745),
    [177] = Vertex.new(12, 12, 40, 107, 47, 43, 0.8745),
    [179] = Vertex.new(4, 68, 16, 107, 47, 43, 0.8745),
    [183] = Vertex.new(4, 0, 36, 107, 47, 43, 0.8745),
    [186] = Vertex.new(-12, 12, 40, 107, 47, 43, 0.8745),
    [192] = Vertex.new(-4, 0, 36, 107, 47, 43, 0.8745),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Adam north of the Wendlewick market.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3482, 8613, 1571) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["adam"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adam"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["adam"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Adam.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adam"]),
    },
    postconditions = { Condition.ConversationText:new("speak to both of them") },
  },
  {
    text = "Talk to Raz in her house between Highweald Forest and Hollow Hill.",
    title = "Rumors",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3574, 19621, 1641) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["raz"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["raz"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["raz"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("see if I can find it") },
  },
  {
    text = "Inspect the dead bear to the north.",
    actions = { Action.Direction:new(3572, 23485, 1675) },
    postconditions = { Condition.ModelVisible:new(deadBear) },
  },
  {
    actions = { Action.ModelHighlight:new(deadBear) },
    jumpconditions = { Condition.ModelNotVisible:new(deadBear) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Return to Raz.",
    actions = { Action.Direction:new(3574, 19621, 1641) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["raz"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["raz"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["raz"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("bottom of this") },
  },
  {
    text = "Talk to Bartender Gefen in the Wendlewick tavern.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3499, 8317, 1498) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["gefen"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["gefen"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["gefen"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("keep you updated") },
  },
  {
    text = "Buy a Wendlewick ale.",
    actions = { Action.ModelHighlight:new(Models.npcs["gefen"]) },
    postconditions = { Condition.InventoryContains:new(wendlewickAle) },
  },
  {
    text = "Talk to Matthew.",
    actions = {
      Action.ModelHighlight:new(matthew),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("appreciate the information") },
  },
  {
    text = "Talk to Miriam outside of the tavern.",
    actions = { Action.ModelHighlight:new(miriam) },
    postconditions = { Condition.ConversationText:new("I see") },
  },
  {
    text = "Talk to Jacob",
    actions = {
      Action.ModelHighlight:new(jacob),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    postconditions = { Condition.ConversationText:new("will want to hear") },
  },
  {
    text = "Talk to Bartender Gefen.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["gefen"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    postconditions = { Condition.ConversationText:new("what we uncovered") },
  },
  {
    text = "Return to Adam.",
    actions = {
      Action.Direction:new(3482, 8613, 1571),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    postconditions = { Condition.ModelVisible:new(Models.npcs["adam"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adam"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["adam"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("owns the Eastfold Farm") },
  },
  {
    text = "Talk to Farmer Rachel at the Eastfold Farm.",
    title = "To the farm",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "BKS",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3612, 7333, 1436) },
    postconditions = { Condition.ModelVisible:new(farmerRachel) },
  },
  {
    actions = {
      Action.ModelHighlight:new(farmerRachel),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(farmerRachel) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("track them down for") },
  },
  {
    text = "Investigate the bloodsplatter to the north at the broken fence",
    actions = { Action.ModelHighlight:new(bloodsplatter) },
    postconditions = { Condition.ConversationText:new("see where it goes") },
  },
  {
    text = "Enter the cave entrance to the north-east.",
    actions = { Action.ModelHighlight:new(caveEntrance) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Speak to Anya",
    actions = {
      Action.ModelHighlight:new(Models.npcs["anya"]),
      Action.ConversationHighlight:new("What are we dealing with?"),
      Action.ConversationHighlight:new("What do I need to do?"),
    },
    postconditions = { Condition.ConversationText:new("and we may begin") },
  },
  {
    text = "Climb to the top of the Wendlewick lighthouse.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3450, 7837, 1495.4) },
    postconditions = { Condition.DistanceToWithHeight:new(3454, 11509, 1495, 4) },
  },
  {
    text = "Talk to Esther.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["esther"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
      Action.ConversationHighlight:new("(Continue Quest)"),
    },
    postconditions = {
      Condition.ConversationText:new("Lets get to it"), --not tested
      Condition.ConversationText:new("Let's get to it"), --not tested
    },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(3452.5, 11517, 1495.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3452, 7237, 1494, 4) },
  },
  {
    text = "Pick the Wendlewick poppy at the top of the hill.",
    title = "Gathering ingredients",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3454, 13125, 1454) },
    postconditions = { Condition.ModelVisible:new(poppiesObj) },
  },
  {
    actions = { Action.ModelHighlight:new(poppiesObj, { highlightPriority = "closest" }) },
    jumpconditions = { Condition.ModelNotVisible:new(poppiesObj) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(wendlewickPoppy) },
  },
  {
    text = "Wolf's tongue mushrooms next to fairy ring.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "BKS",
    },
    actions = { Action.Direction:new(3596, 6349, 1413) },
    postconditions = { Condition.ModelVisible:new(wolfsTongueMushroomObj) },
  },
  {
    actions = { Action.ModelHighlight:new(wolfsTongueMushroomObj, { highlightPriority = "closest" }) },
    jumpconditions = { Condition.ModelNotVisible:new(wolfsTongueMushroomObj) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(wolfsTongue) },
  },
  {
    text = "Kill a hedgehog west of the Shrine of Inanna.",
    actions = { Action.Direction:new(3531, 6821, 1402) },
    postconditions = { Condition.ModelVisible:new(hedgehog) },
  },
  {
    actions = { Action.ModelHighlight:new(hedgehog, { highlightPriority = "closest" }) },
    jumpconditions = { Condition.ModelNotVisible:new(hedgehog) },
    jumpOffset = -1,
    postconditions = { Condition.ModelVisible:new(hedgehogSpines) },
  },
  {
    text = "Pick up the hedgehog spines.",
    actions = { Action.ModelHighlight:new(hedgehogSpines) },
    postconditions = { Condition.InventoryContains:new(hedgehogSpines) },
  },
  {
    text = "Climb to the top of the Wendlewick lighthouse.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3450, 7837, 1495.4) },
    postconditions = { Condition.DistanceToWithHeight:new(3454, 11509, 1495, 4) },
  },
  {
    text = "Talk to Esther.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["esther"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    postconditions = { Condition.ConversationText:new("goes nothing") },
  },
  {
    text = "Take a vial from the vial cabinet.",
    title = "Antisanguine potion",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(vialCabinet) },
    postconditions = { Condition.InventoryContains:new(Models.items["vial"]) },
  },
  {
    text = "Fill the vial with oil.",
    actions = {
      Action.ModelHighlight:new(vatOfOil),
      Action.InventoryHighlight:new(Models.items["vial"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InventoryContains:new(vialOfOil) },
  },
  {
    text = "Use the vial of oil on the fireplace.",
    actions = {
      Action.Direction:new(3457, 11921, 1501.5),
      Action.InventoryHighlight:new(vialOfOil, true),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InventoryContains:new(vialOfOilS) },
  },
  {
    text = "Take some hedgehog spines.",
    actions = { Action.ModelHighlight:new(bucketOfSpines) },
    postconditions = { Condition.InventoryContains:new(hedgehogSpines) },
  },
  {
    text = "Grind the hedgehog spines.",
    actions = { Action.InventoryHighlight:new(hedgehogSpines) },
    postconditions = { Condition.InventoryContains:new(crushedHedgehogSpines) },
  },
  {
    text = "Mix the curshed hedgehog spine with the vial of oil.",
    actions = {
      Action.InventoryHighlight:new(vialOfOilS),
      Action.InventoryHighlight:new(crushedHedgehogSpines),
    },
    postconditions = { Condition.InventoryContains:new(vialOfOilSH) },
  },
  {
    text = "Take from the sack of flowers.",
    actions = { Action.ModelHighlight:new(sackOfFlowers) },
    postconditions = { Condition.InventoryContains:new(wendlewickPoppy) },
  },
  {
    text = "Press the poppy on the table.",
    warning = "No tracking for this step.",
    actions = {
      Action.ModelHighlight:new(roundTable),
      Action.ConversationHighlight:new("Press Wendlewick poppy"),
    },
  },
  {
    text = "Mix the pressed poppy with the vial of oil.",
    actions = {
      Action.InventoryHighlight:new(vialOfOilSH),
      Action.InventoryHighlight:new(wendlewickPoppy),
    },
    postconditions = { Condition.InventoryContains:new(vialOfOilSHP) },
  },
  {
    text = "Take a wolf's tongue from the sack of mushrooms.",
    actions = { Action.ModelHighlight:new(sackOfMushrooms) },
    postconditions = { Condition.InventoryContains:new(wolfsTongue) },
  },
  {
    text = "Weight out 30g on the desk.",
    warning = "No tracking for this step.",
    actions = {
      Action.ModelHighlight:new(desk),
      Action.ConversationHighlight:new("Weigh wolf's tongue"),
      Action.ConversationHighlight:new("Weigh out 30g"),
    },
  },
  {
    text = "Mix the wolf's tongue with the vial of oil.",
    actions = {
      Action.InventoryHighlight:new(vialOfOilSHP),
      Action.InventoryHighlight:new(wolfsTongue),
    },
    postconditions = { Condition.InventoryContains:new(antisanguinePotion) },
  },
  {
    text = "Talk to Esther.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["esther"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    postconditions = { Condition.ConversationText:new("Lead the way") },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(3452.5, 11517, 1495.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3452, 7237, 1494, 4) },
  },
  {
    text = "Test the antisanguine potion on a hedgehog south of the Shrine of Inanna.",
    title = "Field testing",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "BKS",
    },
    neededItems = { ["Antisanguine potion"] = { quantity = 1, model = antisanguinePotion } },
    recommendedItems = {},
    actions = { Action.Direction:new(3573, 3301, 1376) },
    postconditions = { Condition.ModelVisible:new(hedgehog) },
  },
  {
    actions = { Action.ModelHighlight:new(hedgehog) },
    jumpconditions = { Condition.ModelNotVisible:new(hedgehog) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("relatively small") },
  },
  {
    text = "Kill the hedgehog.",
    postconditions = { Condition.ConversationText:new("different dosage on") },
  },
  {
    text = "Cross the stepping stones.",
    actions = { Action.Direction:new(3584, 2965, 1359, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3588, 2925, 1359, 1) },
  },
  {
    text = "Test the antisanguine potion on the duck.",
    actions = { Action.ModelHighlight:new(duck, { atLocation = Location:new(3590, 3133, 1363) }) },
    postconditions = { Condition.ModelVisible:new(bloodsplatter2, { atLocation = Location:new(3590, 3133, 1363) }) },
  },
  {
    text = "Use the antisanguine potion on a badger to the north-east.",
    actions = { Action.Direction:new(3608, 4557, 1383) },
    postconditions = { Condition.ModelVisible:new(badger) },
  },
  {
    actions = { Action.ModelHighlight:new(badger) },
    jumpconditions = { Condition.ModelNotVisible:new(badger) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("forging this weapon") },
  },
  {
    text = "Talk to Liat at the Wendlewick smithy.",
    title = "Getting a silver weapon",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    neededItems = { ["Backpack spaces"] = { quantity = 20 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3511, 8101, 1539) },
    postconditions = { Condition.ModelVisible:new(liat) },
  },
  {
    actions = {
      Action.ModelHighlight:new(liat),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(liat) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("rump kick coming") },
  },
  {
    text = "Talk to Gidon at the Highweald Forest mine to the north.",
    actions = { Action.Direction:new(3517, 19553, 1691) },
    postconditions = { Condition.ModelVisible:new(gidon) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gidon),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(gidon) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("what I can do") },
  },
  {
    text = "Mine 10 copper ore.",
    actions = { Action.ModelHighlight:new(Models.objects["copper rock"], { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(Models.items["copper ore"], 10) },
  },
  {
    text = "Mine 10 tin ore.",
    actions = { Action.ModelHighlight:new(Models.objects["tin rock"], { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(Models.items["tin ore"], 10) },
  },
  {
    text = "Return to Liat.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3511, 8101, 1539) },
    postconditions = { Condition.ModelVisible:new(liat) },
  },
  {
    actions = {
      Action.ModelHighlight:new(liat),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(liat) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("he'll surprise you") },
  },
  {
    text = "Enter the new cave entrance in the Highweald Forest mine.",
    actions = { Action.Direction:new(3517, 19061, 1695) },
    postconditions = {
      Condition.ModelVisible:new(caveEntrance2),
      Condition.DistanceTo:new(3487, 2845, 8071, 4),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(caveEntrance2),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(caveEntrance2) },
    jumpOffset = -1,
    postconditions = { Condition.DistanceTo:new(3487, 2845, 8071, 4) },
  },
  {
    text = "Talk to Gidon.",
    actions = { Action.ModelHighlight:new(gidon) },
    postconditions = { Condition.ConversationText:new("All yours") },
  },
  {
    text = "Mine 4 havensilver ore.",
    actions = {
      Action.ModelHighlight:new(Models.objects["havensilver rock"], { atLocation = Location:new(3483, 2245, 8089) }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["havensilver ore"], 4) },
  },
  {
    text = "Return to Liat again.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3511, 8101, 1539) },
    postconditions = { Condition.ModelVisible:new(liat) },
  },
  {
    actions = {
      Action.ModelHighlight:new(liat),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(liat) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("get in my way") },
  },
  {
    text = "Smelt the ores into havensilver bars.",
    actions = { Action.ModelHighlight:new(furnace) },
    postconditions = { Condition.InventoryContains:new(Models.items["havensilver bar"], 4) },
  },
  {
    text = "Smith a Havensilver greatsword.",
    actions = { Action.Direction:new(3517, 8701, 1538) },
    postconditions = { Condition.InventoryContains:new(havensilverGreatsword) },
  },
  {
    text = "Talk to Anya at the cave east of Marigold Farm.",
    title = "The heart",
    warning = "Make sure you have your antisanguine potion.",
    neededItems = {
      ["Antisanguine potion"] = { quantity = 1 },
      ["Food"] = { quantity = 20 },
    },
    recommendedItems = {},
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "BKS",
    },
    actions = { Action.Direction:new(3633, 8749, 1475) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["anya"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["anya"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
      Action.ConversationHighlight:new("I have no reason not to trust her"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["anya"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("you require more") },
  },
  {
    text = "Drink the antisanguine potion.",
    actions = { Action.InventoryHighlight:new(antisanguinePotion) },
    postconditions = { Condition.ChatText:new("of your antisanguine potion") },
  },
  {
    text = "Enter the cave.",
    actions = { Action.ModelHighlight:new(caveEntrance) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Fight the Sanguine heart.<ul><li>The heart is invulnerable when tumors spawn.</li><li>Avoid the exploding pustules.</li></ul>",
    actions = { Action.ModelHighlight:new(sanguineTumor) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Anya.",
    actions = { Action.ModelHighlight:new(Models.npcs["anya"]) },
    postconditions = { Condition.ConversationText:new("good news") },
  },
  {
    text = "Leave the cave.",
    actions = { Action.Direction:new(3424, 1989, 7877) },
    postconditions = { Condition.DistanceTo:new(3638, 8037, 1475, 4) },
  },
  {
    text = "Talk to Zeke.",
    title = "Finishing up",
    actions = {
      Action.ModelHighlight:new(Models.npcs["zeke"]),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("we were victorious") },
  },
  {
    text = "Return to Adam.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3482, 8613, 1571) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["adam"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adam"]),
      Action.ConversationHighlight:new("Talk about 'Hearts of Sanguine'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["adam"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Hearts of Sanguine",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1774278419,
  prereqQuests = { "Visions of Havenhythe" },
  questReqs = {},
  neededItems = {
    ["Coins"] = { quantity = 1 },
    ["Copper ore"] = { quantity = 10, model = Models.items["copper ore"], duringQuest = true },
    ["Tin ore"] = { quantity = 10, model = Models.items["tin ore"], duringQuest = true },
    ["Wendlewick ale"] = { quantity = 1, model = Models.items["beer"], duringQuest = true },
  },
  recommendedItems = { ["Melee armour"] = { quantity = 1 } },
  combatNPCs = {
    ["Hedgehog"] = { level = "None", quantity = 2 },
    ["Sanguine heart"] = { level = "500", quantity = 1 },
  },
})
