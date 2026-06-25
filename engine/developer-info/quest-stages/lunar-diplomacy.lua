local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local lokarSearunner = Model.new(3405, {
  [30] = Vertex.new(5, 754, -60, 133, 98, 69),
  [1395] = Vertex.new(-28, 720, 12, 99, 90, 51),
  [1430] = Vertex.new(28, 720, 12, 99, 90, 51),
  [2406] = Vertex.new(0, 738, 32, 133, 98, 69),
  [2409] = Vertex.new(0, 738, 32, 133, 98, 69),
})
local captainBentley = Model.new(4128, {
  [353] = Vertex.new(-37, 776, 1, 133, 99, 69),
  [510] = Vertex.new(37, 776, 1, 133, 99, 69),
  [3772] = Vertex.new(100, 672, 68, 134, 149, 46),
  [3774] = Vertex.new(72, 672, 68, 134, 149, 46),
  [3775] = Vertex.new(100, 672, 68, 134, 149, 46),
})
local birdsEyeJack = Model.new(3876, {
  [2150] = Vertex.new(-60, 760, 16, 39, 35, 35),
  [2798] = Vertex.new(-52, 800, 16, 51, 47, 47),
})
local beefyBurns = Model.new(3582, {
  [550] = Vertex.new(46, 780, 20, 133, 99, 69),
  [553] = Vertex.new(46, 780, 20, 133, 99, 69),
  [558] = Vertex.new(46, 780, 20, 133, 99, 69),
  [586] = Vertex.new(-46, 780, 20, 133, 99, 69),
  [3290] = Vertex.new(-43, 775, -12, 73, 68, 46),
})
local eagleEyeShultz = Model.new(3387, {
  [486] = Vertex.new(0, 734, 32, 133, 99, 69),
  [489] = Vertex.new(0, 734, 32, 133, 99, 69),
  [3095] = Vertex.new(-136, 384, 112, 59, 55, 38),
  [3098] = Vertex.new(-116, 392, -48, 56, 51, 29),
  [3101] = Vertex.new(-124, 384, -48, 56, 51, 29),
})
local lecherousLee = Model.new(2880, {
  [1303] = Vertex.new(46, 780, 20, 133, 99, 69),
  [1306] = Vertex.new(46, 780, 20, 133, 99, 69),
  [1311] = Vertex.new(46, 780, 20, 133, 99, 69),
  [1405] = Vertex.new(-46, 780, 20, 133, 99, 69),
  [1815] = Vertex.new(5, 759, -55, 133, 99, 69),
})
local daveyBoy = Model.new(3783, {
  [510] = Vertex.new(0, 738, 32, 133, 99, 69),
  [513] = Vertex.new(0, 738, 32, 133, 99, 69),
  [3021] = Vertex.new(-28, 720, 12, 60, 49, 31),
  [3065] = Vertex.new(28, 720, 12, 60, 49, 31),
  [3738] = Vertex.new(5, 754, -60, 133, 99, 69),
})
local cabinBoy = Model.new(2979, {
  [301] = Vertex.new(-220, 288, -4, 140, 112, 89),
  [2720] = Vertex.new(-52, 496, -28, 94, 54, 29),
  [2723] = Vertex.new(-52, 496, -28, 85, 49, 35),
  [2726] = Vertex.new(-48, 500, -12, 94, 54, 29),
  [2731] = Vertex.new(-52, 496, -28, 85, 49, 35),
})
local suqah = Model.any({
  Model.new(3084, { --shield
    [327] = Vertex.new(138, 921, -46, 106, 106, 97),
    [600] = Vertex.new(-138, 921, -46, 106, 106, 97),
    [630] = Vertex.new(138, 921, -46, 106, 106, 97),
    [2763] = Vertex.new(260, 500, -332, 65, 60, 60),
    [2874] = Vertex.new(241, 405, -371, 65, 60, 60),
  }),
  Model.new(3387, { --dual wield
    [557] = Vertex.new(0, 959, -197, 45, 41, 41),
    [1257] = Vertex.new(154, 1059, -52, 106, 106, 97),
    [1518] = Vertex.new(-154, 1059, -52, 106, 106, 97),
    [2348] = Vertex.new(-63, 921, -86, 76, 76, 69),
    [2352] = Vertex.new(-63, 921, -86, 76, 76, 69),
  }),
})
local selene = Model.new(6288, {
  [5122] = Vertex.new(0, 735, -7, 63, 163, 33),
  [5124] = Vertex.new(0, 732, -8, 63, 163, 33),
  [5580] = Vertex.new(29, 710, -6, 53, 41, 16),
  [5582] = Vertex.new(30, 698, -11, 53, 41, 16),
  [5600] = Vertex.new(-29, 710, -6, 53, 41, 16),
})
local paulinePolaris = Model.new(4869, {
  [2835] = Vertex.new(24, 738, -38, 35, 34, 32),
  [2859] = Vertex.new(-24, 738, -38, 35, 34, 32),
  [2881] = Vertex.new(-2, 716, -57, 123, 91, 64),
  [2887] = Vertex.new(2, 716, -57, 123, 91, 64),
  [3730] = Vertex.new(-25, 756, -35, 169, 158, 108),
})
local meteora = Model.new(5190, {
  [2619] = Vertex.new(24, 738, -38, 35, 34, 32),
  [2643] = Vertex.new(-24, 738, -38, 35, 34, 32),
  [2671] = Vertex.new(2, 716, -57, 123, 91, 64),
  [5112] = Vertex.new(12, 784, -36, 108, 90, 56),
  [5118] = Vertex.new(-4, 776, -36, 108, 90, 56),
})
local rimaeSirsalis = Model.new(3639, {
  [1824] = Vertex.new(24, 738, -38, 35, 34, 32),
  [1848] = Vertex.new(-24, 738, -38, 35, 34, 32),
  [1870] = Vertex.new(-2, 716, -57, 123, 91, 64),
  [1876] = Vertex.new(2, 716, -57, 123, 91, 64),
  [1880] = Vertex.new(6, 716, -52, 123, 91, 64),
})
local etherealBeing = Model.any({
  Model.new(5616, { --man
    [4408] = Vertex.new(0, 735, -7, 40, 195, 178),
    [4410] = Vertex.new(0, 732, -8, 40, 195, 178),
    [4965] = Vertex.new(-62, 670, 50, 135, 176, 167),
    [4996] = Vertex.new(62, 670, 50, 135, 176, 167),
    [5611] = Vertex.new(132, 377, -18, 185, 137, 96),
  }),
  Model.new(5640, { --lady
    [24] = Vertex.new(18, 706, 30, 140, 120, 156),
    [46] = Vertex.new(-18, 706, 30, 140, 120, 156),
    [2748] = Vertex.new(-34, 612, -73, 135, 176, 167),
    [4852] = Vertex.new(0, 735, -7, 79, 204, 42),
    [4854] = Vertex.new(0, 732, -8, 79, 204, 42),
  }),
})
local etherealFluke = Model.new(3840, {
  [3316] = Vertex.new(0, 735, -7, 40, 195, 178),
  [3317] = Vertex.new(0, 732, -7, 40, 195, 178),
  [3318] = Vertex.new(0, 732, -8, 40, 195, 178),
  [3835] = Vertex.new(132, 377, -18, 185, 137, 96),
  [3838] = Vertex.new(-132, 377, -18, 185, 137, 96),
})
local etherealGuide = Model.new(3840, {
  [3316] = Vertex.new(0, 735, -7, 40, 195, 178),
  [3317] = Vertex.new(0, 732, -7, 40, 195, 178),
  [3318] = Vertex.new(0, 732, -8, 40, 195, 178),
  [3835] = Vertex.new(132, 377, -18, 185, 137, 96),
  [3838] = Vertex.new(-132, 377, -18, 185, 137, 96),
})
local etherealPerceptive = Model.new(4698, {
  [4174] = Vertex.new(0, 735, -7, 40, 195, 178),
  [4175] = Vertex.new(0, 732, -7, 40, 195, 178),
  [4176] = Vertex.new(0, 732, -8, 40, 195, 178),
  [4693] = Vertex.new(132, 377, -18, 185, 137, 96),
  [4696] = Vertex.new(-132, 377, -18, 185, 137, 96),
})
local etherealExpert = Model.new(3840, {
  [3316] = Vertex.new(0, 735, -7, 40, 195, 178),
  [3317] = Vertex.new(0, 732, -7, 40, 195, 178),
  [3318] = Vertex.new(0, 732, -8, 40, 195, 178),
  [3835] = Vertex.new(132, 377, -18, 185, 137, 96),
  [3838] = Vertex.new(-132, 377, -18, 185, 137, 96),
})
local etherealNumerator = Model.new(3840, {
  [3316] = Vertex.new(0, 735, -7, 40, 195, 178),
  [3317] = Vertex.new(0, 732, -7, 40, 195, 178),
  [3318] = Vertex.new(0, 732, -8, 40, 195, 178),
  [3835] = Vertex.new(132, 377, -18, 185, 137, 96),
  [3838] = Vertex.new(-132, 377, -18, 185, 137, 96),
})
local etherealMimic = Model.new(3912, {
  [24] = Vertex.new(18, 706, 30, 124, 95, 107),
  [46] = Vertex.new(-18, 706, 30, 124, 95, 107),
  [1638] = Vertex.new(-34, 612, -73, 89, 140, 133),
  [2302] = Vertex.new(0, 735, -7, 56, 144, 29),
  [2304] = Vertex.new(0, 732, -8, 56, 144, 29),
})
--#endregion
--#region Objects
local piratesCoveLadder = Model.new(4632, {
  [686] = Vertex.new(636, 2722, 6378, 167, 167, 167),
  [712] = Vertex.new(655, 2717, 6380, 167, 167, 167),
  [1310] = Vertex.new(940, 2722, 6378, 167, 167, 167),
  [1336] = Vertex.new(955, 2717, 6380, 167, 167, 167),
  [1378] = Vertex.new(955, 2717, 6380, 167, 167, 167),
})
local markedWallchart = Model.new(33, {
  [1] = Vertex.new(-247, 666, -241, 247, 247, 247),
  [3] = Vertex.new(-221, 517, -250, 247, 247, 247),
  [8] = Vertex.new(-247, 515, 224, 247, 247, 247),
  [15] = Vertex.new(-202, 938, -235, 247, 247, 247),
  [33] = Vertex.new(-202, 915, 234, 247, 247, 247),
})
local markedCannon = Model.new(1128, {
  [875] = Vertex.new(-681, 100, 93, 65, 61, 60),
  [1117] = Vertex.new(-634, 107, 236, 82, 66, 63),
  [1122] = Vertex.new(-654, 58, 236, 82, 66, 63),
  [1124] = Vertex.new(-647, 107, -237, 82, 66, 63),
  [1128] = Vertex.new(-667, 58, -237, 82, 66, 63),
})
local markedChest = Model.new(1110, {
  [69] = Vertex.new(-192, 296, -152, 56, 44, 29),
  [393] = Vertex.new(192, 296, 152, 56, 44, 29),
  [527] = Vertex.new(188, 296, 152, 54, 54, 50),
  [767] = Vertex.new(192, 296, 152, 66, 55, 42),
  [921] = Vertex.new(-184, 296, 152, 54, 54, 50),
})
local markedMast = Model.new(384, {
  [245] = Vertex.new(290, 1444, 0, 168, 131, 111),
  [248] = Vertex.new(290, 1444, 0, 168, 131, 111),
  [250] = Vertex.new(290, 1444, 0, 168, 131, 111),
  [258] = Vertex.new(143, 394, 249, 168, 131, 111),
  [317] = Vertex.new(-290, 1444, 0, 168, 131, 111),
})
local markedBarrels = Model.new(4824, {
  [310] = Vertex.new(-135, 350, -118, 127, 127, 127),
  [328] = Vertex.new(-135, 380, 83, 127, 127, 127),
  [334] = Vertex.new(131, 417, 84, 127, 127, 127),
  [376] = Vertex.new(131, 346, -128, 127, 127, 127),
  [1504] = Vertex.new(131, 256, -199, 127, 127, 127),
})
local mineLadder = Model.new(888, {
  [287] = Vertex.new(7516, 3164, 4452, 70, 61, 36),
  [309] = Vertex.new(7564, 3308, 4276, 70, 61, 36),
  [341] = Vertex.new(7536, 3300, 4452, 70, 61, 36),
  [363] = Vertex.new(7584, 3448, 4276, 70, 61, 36),
  [395] = Vertex.new(7556, 3440, 4452, 70, 61, 36),
})
local babaYagasHouse = Model.new(14139, {
  [2725] = Vertex.new(-60, 2742, 974, 147, 147, 147),
  [2746] = Vertex.new(-60, 2742, -836, 147, 147, 147),
  [2800] = Vertex.new(60, 2742, -836, 147, 147, 147),
  [14092] = Vertex.new(594, 2013, -703, 147, 147, 147),
  [14096] = Vertex.new(-594, 1998, 865, 147, 147, 147),
})
local brazier = Model.new(7890, {
  [73] = Vertex.new(-680, 1322, -378, 127, 127, 127),
  [109] = Vertex.new(-284, 1322, -594, 127, 127, 127),
  [121] = Vertex.new(-862, 1322, 54, 127, 127, 127),
  [373] = Vertex.new(-680, 1322, -378, 127, 127, 127),
  [1234] = Vertex.new(-680, 1322, -378, 127, 127, 127),
})
local litBrazier = Model.new(789, {
  [781] = Vertex.new(-278, 663, 354, 163, 149, 151, 0.000),
  [784] = Vertex.new(-426, 720, 32, 163, 149, 151, 0.000),
  [786] = Vertex.new(-191, 720, -203, 163, 149, 151, 0.000),
  [787] = Vertex.new(-511, 735, -194, 163, 149, 151, 0.000),
  [788] = Vertex.new(-228, 735, 374, 163, 149, 151, 0.000),
})
local dreamTree = Model.new(1062, {
  [77] = Vertex.new(-92, 1028, -140, 209, 176, 226),
  [89] = Vertex.new(-84, 1028, 164, 209, 176, 226),
  [95] = Vertex.new(92, 1028, 164, 209, 176, 226),
  [98] = Vertex.new(92, 1028, 164, 209, 176, 226),
  [102] = Vertex.new(92, 1028, 164, 209, 176, 226),
})
local lectern = Model.new(756, {
  [98] = Vertex.new(180, 0, 368, 203, 219, 157),
  [570] = Vertex.new(76, 888, -144, 92, 108, 216),
  [599] = Vertex.new(64, 900, 164, 92, 108, 216),
  [636] = Vertex.new(64, 900, 164, 92, 108, 216),
  [756] = Vertex.new(64, 900, 164, 92, 108, 216),
})
--#endregion
--#region Items
local sealOfPassage = Model.new(270, {
  [230] = Vertex.new(-52, 0, 24, 42, 42, 38),
  [233] = Vertex.new(-52, 0, -20, 42, 42, 38),
  [234] = Vertex.new(-52, 0, 24, 42, 42, 38),
  [249] = Vertex.new(-52, 0, -20, 42, 42, 38),
  [254] = Vertex.new(-52, 0, 24, 42, 42, 38),
})
local emptyBullseyeLantern = Model.new(402, {
  [188] = Vertex.new(-48, 148, 48, 57, 52, 52),
  [241] = Vertex.new(60, 156, 36, 57, 52, 52),
  [244] = Vertex.new(60, 148, 48, 57, 52, 52),
  [246] = Vertex.new(60, 156, 36, 57, 52, 52),
  [252] = Vertex.new(60, 148, 48, 57, 52, 52),
})
local suqahTooth = Model.new(72, {
  [1] = Vertex.new(40, 4, -60, 141, 140, 129),
  [4] = Vertex.new(40, 4, -60, 141, 140, 129),
  [7] = Vertex.new(40, 4, -60, 141, 140, 129),
  [10] = Vertex.new(40, 4, -60, 141, 140, 129),
  [13] = Vertex.new(-32, 20, 52, 91, 23, 18),
})
local suqahHide = Model.new(156, {
  [1] = Vertex.new(36, 16, 80, 81, 81, 74),
  [2] = Vertex.new(48, 0, 148, 81, 81, 74),
  [3] = Vertex.new(76, 0, 108, 81, 81, 74),
  [5] = Vertex.new(32, 4, 140, 81, 81, 74),
  [7] = Vertex.new(0, 20, 64, 81, 81, 74),
  [11] = Vertex.new(0, 8, 136, 81, 81, 74),
  [15] = Vertex.new(-44, 16, 80, 81, 81, 74),
  [18] = Vertex.new(-32, 4, 140, 81, 81, 74),
  [21] = Vertex.new(-72, 0, 116, 81, 81, 74),
  [24] = Vertex.new(-44, 0, 152, 81, 81, 74),
  [26] = Vertex.new(-100, -4, 52, 76, 76, 69),
  [29] = Vertex.new(-64, 12, 36, 76, 76, 69),
  [35] = Vertex.new(-96, 4, -4, 76, 76, 69),
  [38] = Vertex.new(0, 16, 16, 76, 76, 69),
  [44] = Vertex.new(-80, 16, -12, 76, 76, 69),
  [50] = Vertex.new(-120, 0, -52, 76, 76, 69),
  [54] = Vertex.new(48, 12, 32, 76, 76, 69),
  [60] = Vertex.new(96, -4, 36, 76, 76, 69),
  [62] = Vertex.new(0, 24, -36, 76, 76, 69),
  [68] = Vertex.new(-92, 20, -72, 76, 76, 69),
})
local tannedSuqahHide = Model.new(156, {
  [1] = Vertex.new(36, 16, 80, 48, 48, 44),
  [2] = Vertex.new(48, 0, 148, 48, 48, 44),
  [3] = Vertex.new(76, 0, 108, 48, 48, 44),
  [5] = Vertex.new(32, 4, 140, 48, 48, 44),
  [7] = Vertex.new(0, 20, 64, 48, 48, 44),
  [11] = Vertex.new(0, 8, 136, 48, 48, 44),
  [15] = Vertex.new(-44, 16, 80, 48, 48, 44),
  [18] = Vertex.new(-32, 4, 140, 48, 48, 44),
  [21] = Vertex.new(-72, 0, 116, 48, 48, 44),
  [24] = Vertex.new(-44, 0, 152, 48, 48, 44),
  [26] = Vertex.new(-100, -4, 52, 48, 48, 44),
  [29] = Vertex.new(-64, 12, 36, 48, 48, 44),
  [35] = Vertex.new(-96, 4, -4, 48, 48, 44),
  [38] = Vertex.new(0, 16, 16, 48, 48, 44),
  [44] = Vertex.new(-80, 16, -12, 48, 48, 44),
  [50] = Vertex.new(-120, 0, -52, 48, 48, 44),
  [54] = Vertex.new(48, 12, 32, 48, 48, 44),
  [60] = Vertex.new(96, -4, 36, 48, 48, 44),
  [62] = Vertex.new(0, 24, -36, 48, 48, 44),
  [68] = Vertex.new(-92, 20, -72, 48, 48, 44),
})
--#endregion
--#region Quest Items
local emeraldLens = Model.new(66, {
  [2] = Vertex.new(-32, 4, 56, 98, 139, 72, 0.4353),
  [11] = Vertex.new(-32, 4, 56, 98, 139, 72, 0.4353),
  [17] = Vertex.new(-60, 4, 32, 98, 139, 72, 0.4353),
  [29] = Vertex.new(-60, 4, 32, 98, 139, 72, 0.4353),
  [33] = Vertex.new(-60, 4, 32, 98, 139, 72, 0.4353),
})
local emeraldBullseyeLantern = Model.multi({
  Model.new(384, {
    [25] = Vertex.new(-48, 196, 28, 45, 41, 41),
    [38] = Vertex.new(-48, 0, 60, 45, 41, 41),
    [39] = Vertex.new(60, 0, 60, 45, 41, 41),
    [42] = Vertex.new(60, 12, 60, 45, 41, 41),
    [68] = Vertex.new(60, 0, -48, 45, 41, 41),
    [69] = Vertex.new(60, 12, -48, 45, 41, 41),
    [75] = Vertex.new(-48, 0, -48, 45, 41, 41),
    [81] = Vertex.new(-48, 12, -48, 45, 41, 41),
    [95] = Vertex.new(56, 128, -48, 45, 41, 41),
    [103] = Vertex.new(-44, 128, -48, 45, 41, 41),
    [109] = Vertex.new(-48, 164, -48, 45, 41, 41),
    [367] = Vertex.new(60, 140, 32, 57, 52, 52),
    [368] = Vertex.new(68, 148, 40, 57, 52, 52),
    [369] = Vertex.new(60, 140, 44, 57, 52, 52),
    [370] = Vertex.new(60, 156, -24, 57, 52, 52),
    [371] = Vertex.new(68, 148, -24, 57, 52, 52),
    [372] = Vertex.new(60, 148, -32, 57, 52, 52),
    [373] = Vertex.new(60, 148, -12, 57, 52, 52),
    [378] = Vertex.new(60, 140, -28, 57, 52, 52),
    [379] = Vertex.new(60, 140, -16, 57, 52, 52),
  }),
  Model.new(204, {
    [67] = Vertex.new(4, 148, -80, 148, 141, 113, 0.3098),
    [68] = Vertex.new(4, 112, -52, 148, 141, 113, 0.3098),
    [69] = Vertex.new(4, 140, -88, 148, 141, 113, 0.3098),
    [70] = Vertex.new(4, 120, -44, 148, 141, 113, 0.3098),
    [73] = Vertex.new(32, 112, -80, 85, 120, 62, 0.4980),
    [74] = Vertex.new(56, 128, -48, 85, 120, 62, 0.4980),
    [75] = Vertex.new(56, 80, -48, 85, 120, 62, 0.4980),
    [76] = Vertex.new(16, 128, -80, 85, 120, 62, 0.4980),
    [80] = Vertex.new(28, 152, -48, 85, 120, 62, 0.4980),
    [84] = Vertex.new(32, 92, -80, 85, 120, 62, 0.4980),
    [90] = Vertex.new(28, 56, -48, 85, 120, 62, 0.4980),
    [91] = Vertex.new(-4, 128, -80, 85, 120, 62, 0.4980),
    [98] = Vertex.new(-20, 152, -48, 85, 120, 62, 0.4980),
    [102] = Vertex.new(16, 76, -80, 85, 120, 62, 0.4980),
    [105] = Vertex.new(-20, 56, -48, 85, 120, 62, 0.4980),
    [106] = Vertex.new(-20, 112, -80, 85, 120, 62, 0.4980),
    [113] = Vertex.new(-44, 128, -48, 85, 120, 62, 0.4980),
    [117] = Vertex.new(-4, 76, -80, 85, 120, 62, 0.4980),
    [123] = Vertex.new(-44, 80, -48, 85, 120, 62, 0.4980),
    [125] = Vertex.new(-20, 92, -80, 85, 120, 62, 0.4980),
  }),
})
local litEmeraldBullseyeLantern = Model.multi({
  Model.new(384, {
    [25] = Vertex.new(-48, 196, 28, 45, 41, 41),
    [38] = Vertex.new(-48, 0, 60, 45, 41, 41),
    [39] = Vertex.new(60, 0, 60, 45, 41, 41),
    [42] = Vertex.new(60, 12, 60, 45, 41, 41),
    [68] = Vertex.new(60, 0, -48, 45, 41, 41),
    [69] = Vertex.new(60, 12, -48, 45, 41, 41),
    [75] = Vertex.new(-48, 0, -48, 45, 41, 41),
    [81] = Vertex.new(-48, 12, -48, 45, 41, 41),
    [95] = Vertex.new(56, 128, -48, 45, 41, 41),
    [103] = Vertex.new(-44, 128, -48, 45, 41, 41),
    [109] = Vertex.new(-48, 164, -48, 45, 41, 41),
    [367] = Vertex.new(60, 140, 32, 57, 52, 52),
    [368] = Vertex.new(68, 148, 40, 57, 52, 52),
    [369] = Vertex.new(60, 140, 44, 57, 52, 52),
    [370] = Vertex.new(60, 156, -24, 57, 52, 52),
    [371] = Vertex.new(68, 148, -24, 57, 52, 52),
    [372] = Vertex.new(60, 148, -32, 57, 52, 52),
    [373] = Vertex.new(60, 148, -12, 57, 52, 52),
    [378] = Vertex.new(60, 140, -28, 57, 52, 52),
    [379] = Vertex.new(60, 140, -16, 57, 52, 52),
  }),
  Model.new(204, {
    [67] = Vertex.new(4, 148, -80, 148, 141, 113, 0.3098),
    [68] = Vertex.new(4, 112, -52, 148, 141, 113, 0.3098),
    [69] = Vertex.new(4, 140, -88, 148, 141, 113, 0.3098),
    [70] = Vertex.new(4, 120, -44, 148, 141, 113, 0.3098),
    [73] = Vertex.new(32, 112, -80, 85, 120, 62, 0.4980),
    [74] = Vertex.new(56, 128, -48, 85, 120, 62, 0.4980),
    [75] = Vertex.new(56, 80, -48, 85, 120, 62, 0.4980),
    [76] = Vertex.new(16, 128, -80, 85, 120, 62, 0.4980),
    [80] = Vertex.new(28, 152, -48, 85, 120, 62, 0.4980),
    [84] = Vertex.new(32, 92, -80, 85, 120, 62, 0.4980),
    [90] = Vertex.new(28, 56, -48, 85, 120, 62, 0.4980),
    [91] = Vertex.new(-4, 128, -80, 85, 120, 62, 0.4980),
    [98] = Vertex.new(-20, 152, -48, 85, 120, 62, 0.4980),
    [102] = Vertex.new(16, 76, -80, 85, 120, 62, 0.4980),
    [105] = Vertex.new(-20, 56, -48, 85, 120, 62, 0.4980),
    [106] = Vertex.new(-20, 112, -80, 85, 120, 62, 0.4980),
    [113] = Vertex.new(-44, 128, -48, 85, 120, 62, 0.4980),
    [117] = Vertex.new(-4, 76, -80, 85, 120, 62, 0.4980),
    [123] = Vertex.new(-44, 80, -48, 85, 120, 62, 0.4980),
    [125] = Vertex.new(-20, 92, -80, 85, 120, 62, 0.4980),
  }),
})
local lunarOre = Model.new(336, {
  [14] = Vertex.new(80, 80, -132, 97, 89, 89),
  [16] = Vertex.new(-132, 0, 144, 97, 89, 89),
  [131] = Vertex.new(80, 80, -132, 97, 89, 89),
  [237] = Vertex.new(80, 80, -132, 97, 89, 89),
  [239] = Vertex.new(80, 80, -132, 97, 89, 89),
})
local emptySpecialVial = Model.multi({
  Model.new(426, {
    [164] = Vertex.new(-20, 212, 64, 97, 80, 50),
    [169] = Vertex.new(-20, 196, 76, 97, 80, 50),
    [170] = Vertex.new(-12, 196, 76, 97, 80, 50),
    [171] = Vertex.new(-16, 176, 80, 97, 80, 50),
    [182] = Vertex.new(44, 212, 44, 97, 80, 50),
    [185] = Vertex.new(60, 196, 40, 97, 80, 50),
    [186] = Vertex.new(64, 200, 40, 97, 80, 50),
    [189] = Vertex.new(60, 176, 36, 97, 80, 50),
    [200] = Vertex.new(16, 212, -32, 97, 80, 50),
    [214] = Vertex.new(24, 196, -48, 97, 80, 50),
    [216] = Vertex.new(20, 176, -44, 97, 80, 50),
    [217] = Vertex.new(24, 200, -52, 97, 80, 50),
    [219] = Vertex.new(20, 176, -44, 97, 80, 50),
    [222] = Vertex.new(20, 176, -44, 24, 58, 59),
    [233] = Vertex.new(-60, 196, -12, 97, 80, 50),
    [234] = Vertex.new(-64, 200, -12, 97, 80, 50),
    [237] = Vertex.new(-56, 176, -8, 97, 80, 50),
    [239] = Vertex.new(-60, 200, -8, 97, 80, 50),
    [332] = Vertex.new(-20, 200, 80, 97, 80, 50),
    [404] = Vertex.new(-12, 200, 80, 97, 80, 50),
  }),
  Model.new(96, {
    [2] = Vertex.new(-16, 44, 56, 24, 58, 59, 0.2980),
    [6] = Vertex.new(40, 44, 32, 24, 58, 59, 0.2980),
    [7] = Vertex.new(-40, 44, 0, 24, 58, 59, 0.2980),
    [13] = Vertex.new(20, 84, -36, 24, 58, 59, 0.2980),
    [27] = Vertex.new(-20, 136, 68, 24, 58, 59, 0.2980),
    [29] = Vertex.new(-20, 84, 68, 24, 58, 59, 0.2980),
    [30] = Vertex.new(52, 84, 36, 24, 58, 59, 0.2980),
    [36] = Vertex.new(-52, 136, -8, 24, 58, 59, 0.2980),
    [41] = Vertex.new(20, 136, -44, 24, 58, 59, 0.2980),
    [45] = Vertex.new(52, 136, 36, 24, 58, 59, 0.2980),
    [52] = Vertex.new(-20, 164, -20, 24, 58, 59, 0.2980),
    [58] = Vertex.new(40, 164, 4, 24, 58, 59, 0.2980),
    [64] = Vertex.new(16, 164, 52, 24, 58, 59, 0.2980),
    [71] = Vertex.new(-36, 164, 32, 24, 58, 59, 0.2980),
    [78] = Vertex.new(-8, 180, -8, 24, 58, 59, 0.2980),
  }),
})
local guamVial = Model.multi({
  Model.new(426, {
    [164] = Vertex.new(-20, 212, 64, 97, 80, 50),
    [169] = Vertex.new(-20, 196, 76, 97, 80, 50),
    [170] = Vertex.new(-12, 196, 76, 97, 80, 50),
    [171] = Vertex.new(-16, 176, 80, 97, 80, 50),
    [182] = Vertex.new(44, 212, 44, 97, 80, 50),
    [185] = Vertex.new(60, 196, 40, 97, 80, 50),
    [186] = Vertex.new(64, 200, 40, 97, 80, 50),
    [189] = Vertex.new(60, 176, 36, 97, 80, 50),
    [200] = Vertex.new(16, 212, -32, 97, 80, 50),
    [214] = Vertex.new(24, 196, -48, 97, 80, 50),
    [216] = Vertex.new(20, 176, -44, 97, 80, 50),
    [217] = Vertex.new(24, 200, -52, 97, 80, 50),
    [219] = Vertex.new(20, 176, -44, 97, 80, 50),
    [222] = Vertex.new(20, 176, -44, 24, 58, 59),
    [233] = Vertex.new(-60, 196, -12, 97, 80, 50),
    [234] = Vertex.new(-64, 200, -12, 97, 80, 50),
    [237] = Vertex.new(-56, 176, -8, 97, 80, 50),
    [239] = Vertex.new(-60, 200, -8, 97, 80, 50),
    [332] = Vertex.new(-20, 200, 80, 97, 80, 50),
    [404] = Vertex.new(-12, 200, 80, 97, 80, 50),
  }),
  Model.new(150, {
    [2] = Vertex.new(12, 116, -24, 24, 58, 59, 0.8471),
    [27] = Vertex.new(12, 84, -24, 23, 36, 36, 0.8471),
    [35] = Vertex.new(36, 84, 24, 23, 36, 36, 0.8471),
    [36] = Vertex.new(20, 44, 24, 23, 36, 36, 0.8471),
    [41] = Vertex.new(-12, 44, 40, 23, 36, 36, 0.8471),
    [46] = Vertex.new(-28, 44, 8, 23, 36, 36, 0.8471),
    [49] = Vertex.new(40, 116, 28, 67, 36, 35, 0.8471),
    [97] = Vertex.new(-20, 136, 68, 24, 58, 59, 0.2941),
    [98] = Vertex.new(52, 84, 36, 24, 58, 59, 0.2941),
    [99] = Vertex.new(52, 136, 36, 24, 58, 59, 0.2941),
    [100] = Vertex.new(20, 136, -44, 24, 58, 59, 0.2941),
    [103] = Vertex.new(-52, 136, -8, 24, 58, 59, 0.2941),
    [105] = Vertex.new(-36, 164, 32, 24, 58, 59, 0.2941),
    [106] = Vertex.new(-20, 164, -20, 24, 58, 59, 0.2941),
    [112] = Vertex.new(40, 164, 4, 24, 58, 59, 0.2941),
    [118] = Vertex.new(16, 164, 52, 24, 58, 59, 0.2941),
    [127] = Vertex.new(24, 180, 8, 24, 58, 59, 0.2941),
    [132] = Vertex.new(-8, 180, -8, 24, 58, 59, 0.2941),
    [136] = Vertex.new(8, 180, 36, 24, 58, 59, 0.2941),
    [143] = Vertex.new(-24, 180, 24, 24, 58, 59, 0.2941),
  }),
})
local groundSuqahTooth = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 127, 107, 52),
  [34] = Vertex.new(100, 0, 60, 127, 107, 52),
  [42] = Vertex.new(100, 0, 60, 127, 107, 52),
  [64] = Vertex.new(132, 0, -24, 127, 107, 52),
  [72] = Vertex.new(132, 0, -24, 127, 107, 52),
})
local lunarStaff = Model.new(555, {
  [27] = Vertex.new(-116, 0, 180, 23, 36, 36),
  [29] = Vertex.new(-132, 0, 192, 23, 36, 36),
  [96] = Vertex.new(100, 0, -148, 97, 80, 50),
  [203] = Vertex.new(-132, 0, 192, 4, 46, 48),
  [207] = Vertex.new(-116, 0, 180, 4, 46, 48),
})
local lunarBar = Model.new(84, {
  [1] = Vertex.new(-56, 0, 80, 74, 81, 74),
  [4] = Vertex.new(-56, 0, 80, 74, 81, 74),
  [9] = Vertex.new(56, 0, -80, 74, 81, 74),
  [13] = Vertex.new(56, 0, -80, 74, 81, 74),
  [16] = Vertex.new(56, 0, -80, 74, 81, 74),
})
local lunarHelm = Model.new(324, {
  [20] = Vertex.new(20, 124, -68, 31, 36, 49),
  [146] = Vertex.new(0, 144, -88, 4, 46, 48),
  [148] = Vertex.new(0, 144, -88, 4, 46, 48),
  [299] = Vertex.new(0, 144, -88, 45, 41, 41),
  [301] = Vertex.new(0, 144, -88, 45, 41, 41),
})
local lunarCape = Model.new(168, {
  [1] = Vertex.new(-76, -8, -240, 30, 36, 23),
  [65] = Vertex.new(-236, 0, -96, 30, 36, 23),
  [111] = Vertex.new(144, 16, 56, 30, 36, 23),
  [137] = Vertex.new(192, 8, 248, 30, 36, 23),
  [141] = Vertex.new(192, 8, 248, 30, 36, 23),
})
local tiara = Model.new(123, {
  [115] = Vertex.new(4, 24, -40, 23, 36, 36),
  [117] = Vertex.new(-4, 24, -40, 23, 36, 36),
  [118] = Vertex.new(8, 0, -40, 24, 58, 59),
  [120] = Vertex.new(12, 8, -44, 24, 58, 59),
  [121] = Vertex.new(-12, 8, -44, 67, 36, 35),
})
local lunarAmulet = Model.new(387, {
  [195] = Vertex.new(0, 0, -128, 97, 80, 50),
  [211] = Vertex.new(-8, 0, -136, 97, 80, 50),
  [231] = Vertex.new(-16, 0, -140, 67, 36, 35),
  [236] = Vertex.new(-24, 0, -140, 67, 36, 35),
  [240] = Vertex.new(-24, 0, -140, 67, 36, 35),
})
local lunarTorso = Model.new(531, {
  [430] = Vertex.new(128, 16, -92, 49, 31, 37),
  [434] = Vertex.new(128, 16, -92, 49, 31, 37),
  [451] = Vertex.new(-128, 16, -92, 49, 31, 37),
  [468] = Vertex.new(-120, 36, 44, 49, 31, 37),
  [498] = Vertex.new(120, 36, 44, 49, 31, 37),
})
local lunarTrousers = Model.new(375, {
  [15] = Vertex.new(-60, -8, 72, 31, 36, 49),
  [19] = Vertex.new(60, -8, 96, 4, 46, 48),
  [23] = Vertex.new(-60, -8, 96, 4, 46, 48),
  [108] = Vertex.new(-48, 0, -200, 4, 46, 48),
  [225] = Vertex.new(48, 0, -200, 4, 46, 48),
})
local lunarGloves = Model.new(156, {
  [5] = Vertex.new(-60, 4, 56, 49, 31, 37),
  [14] = Vertex.new(104, 16, -64, 30, 36, 23),
  [19] = Vertex.new(56, 4, 56, 49, 31, 37),
  [67] = Vertex.new(56, 4, 56, 49, 31, 37),
  [122] = Vertex.new(-60, 4, 56, 49, 31, 37),
})
local lunarBoots = Model.new(210, {
  [90] = Vertex.new(60, 92, -12, 30, 36, 23),
  [114] = Vertex.new(88, 0, -60, 31, 36, 49),
  [197] = Vertex.new(60, 92, -12, 30, 36, 23),
  [200] = Vertex.new(60, 92, -12, 38, 42, 38),
  [210] = Vertex.new(28, 96, 24, 38, 42, 38),
})
local lunarRing = Model.new(534, {
  [311] = Vertex.new(-68, 16, 80, 24, 58, 59),
  [315] = Vertex.new(-68, 16, 80, 24, 58, 59),
  [319] = Vertex.new(-44, 16, 104, 24, 58, 59),
  [323] = Vertex.new(-44, 16, 104, 24, 58, 59),
  [325] = Vertex.new(-44, 16, 104, 24, 58, 59),
})
local kindling = Model.new(342, {
  [174] = Vertex.new(136, 0, -160, 50, 37, 21),
  [176] = Vertex.new(136, 0, -160, 50, 37, 21),
  [185] = Vertex.new(140, 0, -192, 50, 37, 21),
  [192] = Vertex.new(140, 0, -192, 50, 37, 21),
  [197] = Vertex.new(140, 0, -192, 50, 37, 21),
})
local dreamLog = Model.new(126, {
  [3] = Vertex.new(-72, 104, 120, 144, 171, 89),
  [23] = Vertex.new(68, 104, -116, 144, 171, 89),
  [45] = Vertex.new(-72, 104, 120, 89, 171, 139),
  [50] = Vertex.new(-72, 104, 120, 89, 171, 139),
  [56] = Vertex.new(-72, 104, 120, 89, 171, 139),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Lokar Searunner on Rellekka's westernmost dock.",
    title = "Starting out",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fremennik Province lodestone",
      url = "Fremennik_Province_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2621, 925, 3688, { distance = 20 }),
      Action.ModelHighlight:new(lokarSearunner, { distance = 20 }),
      Action.ConversationHighlight:new("You've been away from these parts a while?"),
      Action.ConversationHighlight:new("Why did you leave?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Lokar Searunner.",
    actions = { Action.ModelHighlight:new(lokarSearunner) },
    postconditions = { Condition.ConversationText:new("be right back") },
  },
  {
    text = "Buy a spade from Sigmund the Merchant.",
    actions = { Action.ModelHighlight:new(Models.npcs["sigmund the merchant"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["spade"]) },
  },
  {
    text = "Speak to Brundt the Chieftain in the Longhall for a seal of passage.",
    title = "Getting to the isle",
    warning = "Keep the seal on you throughout the quest.",
    actions = {
      Action.Direction:new(2658.5, 2685, 3668, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["brundt the chieftain"], { distance = 6 }),
      Action.ConversationHighlight:new("Ask about a seal of passage."),
    },
    postconditions = { Condition.ConversationText:new("best that you do") },
  },
  {
    text = "Return and talk to Lokar.",
    actions = {
      Action.Direction:new(2621, 925, 3688, { distance = 20 }),
      Action.ModelHighlight:new(lokarSearunner, { distance = 20 }),
      Action.ConversationHighlight:new("Arrr! Yar! Let's be on our way, yar!"),
    },
    postconditions = { Condition.ConversationText:new("already on board") },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.ModelHighlight:new(piratesCoveLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(2209, 2821, 3803, 4) },
  },
  {
    text = "Talk to Captain Bentley.",
    actions = {
      Action.ModelHighlight:new(captainBentley),
      Action.ConversationHighlight:new("Can we sail to Lunar Isle now?"),
    },
    postconditions = { Condition.ConversationText:new("VERY strange") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2222, 3965, 3797.6) }, --above deck -> below deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 2021, 3797, 4) },
  },
  {
    text = "Talk to 'Bird's-Eye' Jack to the south.",
    actions = { Action.ModelHighlight:new(birdsEyeJack) },
    postconditions = { Condition.ConversationText:new("I'm gone") },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2222, 2621, 3797.6) }, --below deck -> above deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 3365, 3796, 4) },
  },
  {
    text = "Talk to Captain Bentley again.",
    actions = {
      Action.ModelHighlight:new(captainBentley),
      Action.ConversationHighlight:new("Perhaps it's the Navigator's fault?"),
    },
    postconditions = { Condition.ConversationText:new("GAH") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2222, 3965, 3797.6) }, --above deck -> below deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 2021, 3797, 4) },
  },
  {
    text = "Talk to 'Birds-Eye' Jack again.",
    actions = { Action.ModelHighlight:new(birdsEyeJack) },
    postconditions = { Condition.ConversationText:new("being my fault") },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2222, 2621, 3797.6) }, --below deck -> above deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 3365, 3796, 4) },
  },
  {
    text = "Talk to 'Eagle-eye' Shultz at the very front of the ship.",
    actions = { Action.ModelHighlight:new(eagleEyeShultz) },
    postconditions = { Condition.ConversationText:new("been extremely helpful") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2222, 3965, 3797.6) }, --above deck -> below deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 2021, 3797, 4) },
  },
  {
    text = "Climb down the ladder again.",
    actions = { Action.Direction:new(2222, 2021, 3804) }, --below deck -> lowest deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 485, 3806, 4) },
  },
  {
    text = "Talk to 'Beefy' Burns in the kitchen.",
    actions = { Action.ModelHighlight:new(beefyBurns) },
    postconditions = { Condition.ConversationText:new("Thanks for the help") },
  },
  {
    text = "Climb up the ladder.", --lowest deck -> below deck
    actions = { Action.Direction:new(2222, 1085, 3805) },
    postconditions = { Condition.DistanceToWithHeight:new(2222, 2021, 3803, 4) },
  },
  {
    text = "Climb up the ladder again.",
    actions = { Action.Direction:new(2222, 2621, 3797.6) }, --below deck -> above deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 3365, 3796, 4) },
  },
  {
    text = "Climb up the ladder yet again.",
    actions = { Action.Direction:new(2218, 4941, 3791) }, --above deck -> top level
    postconditions = { Condition.DistanceToWithHeight:new(2218, 5381, 3789, 4) },
  },
  {
    text = "Talk to 'Lecherous' Lee.",
    actions = { Action.ModelHighlight:new(lecherousLee) },
    postconditions = { Condition.ConversationText:new("No problem") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2218, 5381, 3790) }, --top level -> above dec
    postconditions = { Condition.DistanceToWithHeight:new(2218, 4341, 3792, 4) },
  },
  {
    text = "Talk to First mate 'Davey-boy'.",
    actions = { Action.ModelHighlight:new(daveyBoy) },
    postconditions = { Condition.ConversationText:new("have a word with him") },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2218, 4941, 3791) }, --above deck -> top level
    postconditions = { Condition.DistanceToWithHeight:new(2218, 5381, 3789, 4) },
  },
  {
    text = "Talk to the cabin boy (2 inventory spaces required).",
    actions = { Action.ModelHighlight:new(cabinBoy) },
    postconditions = { Condition.InventoryContains:new(emeraldLens) },
  },
  {
    text = "Use the lens on the lantern. Light it.",
    actions = {
      Action.InventoryHighlight:new(emeraldLens),
      Action.InventoryHighlight:new(emptyBullseyeLantern),
      Action.InventoryHighlight:new(emeraldBullseyeLantern),
    },
    postconditions = { Condition.InventoryContains:new(litEmeraldBullseyeLantern) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2218, 5381, 3790) }, --top level -> above dec
    postconditions = { Condition.DistanceToWithHeight:new(2218, 4341, 3792, 4) },
  },
  {
    text = "Use the lantern on the wallchart in the captains quarters.",
    actions = {
      Action.ModelHighlight:new(markedWallchart),
      Action.InventoryHighlight:new(litEmeraldBullseyeLantern),
      Action.ConversationHighlight:new("Rub away!"),
    },
    postconditions = { Condition.ConversationText:new("One down") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2222, 3965, 3797.6) }, --above deck -> below deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 2021, 3797, 4) },
  },
  {
    text = "Use the lantern on the cannon.",
    actions = {
      Action.ModelHighlight:new(markedCannon),
      Action.InventoryHighlight:new(litEmeraldBullseyeLantern),
      Action.ConversationHighlight:new("Rub away!"),
    },
    postconditions = { Condition.ConversationText:new("Two down") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2222, 2021, 3804) }, --below deck -> lowest deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 485, 3806, 4) },
  },
  {
    text = "Use the lantern on the chest.",
    actions = {
      Action.ModelHighlight:new(markedChest),
      Action.InventoryHighlight:new(litEmeraldBullseyeLantern),
      Action.ConversationHighlight:new("Rub away!"),
    },
    postconditions = { Condition.ConversationText:new("Three down") },
  },
  {
    text = "Use the lantern on the support.",
    actions = {
      Action.ModelHighlight:new(markedMast),
      Action.InventoryHighlight:new(litEmeraldBullseyeLantern),
      Action.ConversationHighlight:new("Rub away!"),
    },
    postconditions = { Condition.ConversationText:new("Four down") },
  },
  {
    text = "Use the lantern on the barrel.",
    actions = {
      Action.ModelHighlight:new(markedBarrels),
      Action.InventoryHighlight:new(litEmeraldBullseyeLantern),
      Action.ConversationHighlight:new("Rub away!"),
    },
    postconditions = { Condition.ConversationText:new("hopefully the jinx") },
  },
  {
    text = "Climb up the ladder.", --lowest deck -> below deck
    actions = { Action.Direction:new(2222, 1085, 3805) },
    postconditions = { Condition.DistanceToWithHeight:new(2222, 2021, 3803, 4) },
  },
  {
    text = "Climb up the ladder again.",
    actions = { Action.Direction:new(2222, 2621, 3797.6) }, --below deck -> above deck
    postconditions = { Condition.DistanceToWithHeight:new(2222, 3365, 3796, 4) },
  },
  {
    text = "Talk to Captain Bentley.",
    actions = { Action.ModelHighlight:new(captainBentley) },
    postconditions = {
      Condition.DistanceTo:new(2130, 2469, 3899, 4),
      Condition.ConversationText:new("Thanks Cap'n"),
    },
  },
  {
    text = "Climb down the ladders and walk into the city (there will be a small cutscene).<ul><li>You can use the bank if you have seal of passage.</li></ul>",
    title = "Lunar Isle",
    neededItems = {
      ["Clean guam"] = { quantity = 1 },
      ["Clean marrentill"] = { quantity = 1 },
      ["Suqah tooth"] = { quantity = 1, model = suqahTooth },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2127.5, 2373, 3893) },
    postconditions = { Condition.DistanceToWithHeight:new(2126, 965, 3893, 4) },
  },
  {
    actions = { Action.Direction:new(2109, 2885, 3911.5) },
    postconditions = { Condition.DistanceTo:new(2110, 2885, 3911, 4) },
  },
  { postconditions = { Condition.ChatText:new("goes nothing") } }, --not tested
  {
    text = "Go north-east outside the city and kill suqahs until these items are obtained:<ul><li>One suqah tooth.</li><li>4 suqah hides.</li><li>If not already in the backpack, one grimy guam and one grimy marrentill. Clean the herbs.</li></ul>",
    actions = { Action.ModelHighlight:new(suqah, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(suqahTooth) },
  },
  {
    actions = { Action.ModelHighlight:new(suqah, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(suqahHide, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(suqah, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(Models.items["clean guam"]) },
  },
  {
    actions = { Action.ModelHighlight:new(suqah, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(Models.items["clean marrentill"]) },
  },
  {
    text = "Climb down the ladder into the mine.",
    actions = {
      Action.Direction:new(2142, 3109, 3944, { distance = 15 }),
      Action.ModelHighlight:new(mineLadder, { distance = 16 }),
    },
    postconditions = { Condition.DistanceTo:new(2329, 3101, 10353, 4) },
  },
  {
    text = "Mine a stalagmite.<ul><li>Optional: Mine the Rune Essence rcok to complete the Hard Fremennik achievement 'Runes on the Moon'.</li></ul>",
    actions = { Action.Direction:new(2321, 2709, 10343) },
    postconditions = { Condition.InventoryContains:new(lunarOre) },
  },
  {
    text = "Exit the mine.",
    actions = { Action.Direction:new(2330, 3701, 10353) },
    postconditions = { Condition.DistanceTo:new(2142, 3109, 3944, 4) },
  },
  {
    text = "Talk to the Oneiromancer near the astral altar at the south-east part of the island.",
    actions = {
      Action.Direction:new(2150, 277, 3867, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["oneiromancer"], { distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("north of the town") },
  },
  {
    text = "Go inside the walking house in the northern section of the city.",
    actions = {
      Action.Direction:new(2091, 2725, 3930, { distance = 16 }),
      Action.ModelHighlight:new(babaYagasHouse, { distance = 20 }),
    },
    postconditions = { Condition.ModelVisible:new(Models.npcs["baba yaga"]) },
  },
  {
    text = "Talk to Baba Yaga (1 inventory space required).",
    actions = {
      Action.ModelHighlight:new(Models.npcs["baba yaga"]),
      Action.ConversationHighlight:new("The Oneiromancer told me you may be able to help..."),
    },
    postconditions = { Condition.ConversationText:new("Thanks") },
  },
  {
    text = "Use the vial on the sink.",
    actions = {
      Action.Direction:new(3105.225, 1781, 4450),
      Action.InventoryHighlight:new(emptySpecialVial),
    },
    postconditions = { Condition.ChatText:new("fill the Vial") },
  },
  {
    text = "Use a clean guam on the vial of water.",
    actions = {
      Action.InventoryHighlight:new(Models.items["clean guam"]),
      Action.InventoryHighlight:new(emptySpecialVial),
    },
    postconditions = { Condition.InventoryContains:new(guamVial) },
  },
  {
    text = "Use a clean marrentill on the guam vial.",
    warning = "No tracking for this step.",
    actions = {
      Action.InventoryHighlight:new(Models.items["clean marrentill"]),
      Action.InventoryHighlight:new(guamVial),
    },
  },
  {
    text = "Grind the suqah tooth.",
    actions = { Action.ModelHighlight:new(suqahTooth) },
    postconditions = { Condition.InventoryContains:new(groundSuqahTooth) },
  },
  {
    text = "Use the ground suqah tooth on the guam-marr vial.",
    warning = "No tracking for this step.",
    actions = {
      Action.InventoryHighlight:new(groundSuqahTooth),
      Action.InventoryHighlight:new(guamVial),
    },
  },
  {
    text = "Leave the house.",
    actions = { Action.Direction:new(3103, 1981, 4446.5) },
    postconditions = { Condition.DistanceTo:new(2083, 2885, 3927, 20) },
  },
  {
    text = "Return to the Oneiromancer with the potion.",
    actions = {
      Action.Direction:new(2150, 277, 3867, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["oneiromancer"], { distance = 20 }),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(guamVial) },
  },
  {
    text = "Order matters. Use your dramen staff/staves on the air altar.",
    title = "Lunar staff",
    warning = "No tracking for this step.",
    neededItems = { ["Dramen staff"] = { quantity = 1 } },
    recommendedItems = {
      ["Wicked hood"] = { quantity = 1 },
      ["Skull sceptre"] = { quantity = 1 },
      ["Ring of duelling"] = { quantity = 1 },
      ["Enlightened amulet"] = { quantity = 1 },
      ["Archaeology journal"] = { quantity = 1 },
    },
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3127, 2421, 3405) },
    postconditions = { Condition.DistanceTo:new(2842, 4973, 4832, 20) },
  },
  {
    actions = {
      Action.Direction:new(2844, 5645, 4834),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
  },
  {
    text = "Use your dramen staff/staves on the fire altar.",
    warning = "No tracking for this step.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Al Kharid lodestone",
      url = "Al_Kharid_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3313, 2029, 3255) },
    postconditions = { Condition.DistanceTo:new(2581, 2181, 4842, 20) },
  },
  {
    actions = {
      Action.Direction:new(2585, 2781, 4838),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
  },
  {
    text = "Use your dramen staff/staves on the water altar.",
    warning = "No tracking for this step.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Lumbridge lodestone",
      url = "Lumbridge_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3183, 2093, 3158) },
    postconditions = { Condition.DistanceTo:new(3489, 941, 4833, 20) },
  },
  {
    actions = {
      Action.Direction:new(3484, 1725, 4836),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
  },
  {
    text = "Use your dramen staff/staves on the earth altar.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fort Forinthry lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3306, 997, 3474) },
    postconditions = { Condition.DistanceTo:new(2658, 1981, 4833, 20) },
  },
  {
    actions = {
      Action.Direction:new(2658, 2397, 4841),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
    postconditions = { Condition.InventoryContains:new(lunarStaff) },
  },
  {
    text = "Smelt the lunar ore at any regular furnace.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.InventoryContains:new(lunarBar) },
  },
  {
    text = "Make a lunar helmet at a regular anvil.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.InventoryContains:new(lunarHelm) },
  },
  {
    text = "'Travel' from Lokar Searunner in Rellekka at the westernmost dock",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fremennik Province lodestone",
      url = "Fremennik_Province_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2621, 925, 3688, { distance = 20 }),
      Action.ModelHighlight:new(lokarSearunner, { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2209, 273, 3792, 4) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.ModelHighlight:new(piratesCoveLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(2209, 2821, 3803, 4) },
  },
  {
    text = "Travel with Captain Bentley.",
    actions = { Action.ModelHighlight:new(captainBentley) },
    postconditions = { Condition.DistanceTo:new(2130, 2469, 3899, 4) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2127.5, 2373, 3893) },
    postconditions = { Condition.DistanceToWithHeight:new(2126, 965, 3893, 4) },
  },
  {
    text = "Talk to the Oneiromancer to give her a lunar staff.",
    actions = {
      Action.Direction:new(2150, 277, 3867, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["oneiromancer"], { distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("hear about") },
  },
  {
    text = "Talk to Pauline Polaris in the north-western-most building in the city.",
    title = "Lunar gear",
    neededItems = {
      ["Thread"] = { quantity = 2 },
      ["Spade (tool belt doesn't work)"] = { quantity = 1 },
      ["Suqah hide"] = { quantity = 4, model = suqahHide },
    },
    actions = {
      Action.Direction:new(2072.5, 2885, 3922, { distance = 4 }),
      Action.ModelHighlight:new(paulinePolaris, { distance = 5 }),
      Action.ConversationHighlight:new("Pauline?"),
      Action.ConversationHighlight:new("Jane Blud-Hagic-Maid"),
    },
    postconditions = { Condition.InventoryContains:new(lunarCape) },
  },
  {
    text = "Talk to Meteora south in the city.",
    actions = {
      Action.Direction:new(2080, 2885, 3895, { distance = 4 }),
      Action.ModelHighlight:new(meteora, { distance = 5 }),
    },
    postconditions = { Condition.ConversationText:new("Gud") },
  },
  {
    text = "Kill Suqahs until one drops a special tiara.",
    actions = {
      Action.ModelHighlight:new(suqah, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(tiara),
    },
    postconditions = { Condition.InventoryContains:new(tiara) },
  },
  {
    text = "Give Meteora the tiara.",
    actions = {
      Action.Direction:new(2080, 2885, 3895, { distance = 4 }),
      Action.ModelHighlight:new(meteora, { distance = 5 }),
    },
    postconditions = { Condition.InventoryContains:new(lunarAmulet) },
  },
  {
    text = "Talk to Rimae Sirsalis in the clothing store south of the bank about ceremonial clothes.",
    actions = {
      Action.Direction:new(2104, 2885, 3903, { distance = 4 }),
      Action.ModelHighlight:new(rimaeSirsalis, { distance = 5 }),
      Action.ConversationHighlight:new("You know the ceremonial clothes?"),
    },
    postconditions = { Condition.ConversationText:new("Interesting") },
  },
  {
    text = "Talk to Rimae Sirsalis again, she will tan your hides.",
    actions = {
      Action.ConversationHighlight:new("You know the ceremonial clothes?"),
      Action.ConversationHighlight:new("That seems like a fair deal."),
    },
    postconditions = { Condition.ConversationText:new("test") },
  },
  {
    text = "Craft a lunar torso, trousers, gloves, and boots from your hides.<ul><li>You can buy thread from Rimae's shop.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(tannedSuqahHide),
      Action.ConversationHighlight:new("Lunar Torso"),
    },
    postconditions = { Condition.InventoryContains:new(lunarTorso) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(tannedSuqahHide),
      Action.ConversationHighlight:new("Lunar Trousers"),
    },
    postconditions = { Condition.InventoryContains:new(lunarTrousers) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(tannedSuqahHide),
      Action.ConversationHighlight:new("Lunar Gloves"),
    },
    postconditions = { Condition.InventoryContains:new(lunarGloves) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(tannedSuqahHide),
      Action.ConversationHighlight:new("Lunar Boots"),
    },
    postconditions = { Condition.InventoryContains:new(lunarBoots) },
  },
  {
    text = "Talk to Selene next to the lodestone.",
    actions = {
      Action.ModelHighlight:new(selene),
      Action.ConversationHighlight:new("I'm looking for a ring."),
    },
    postconditions = { Condition.ConversationText:new("I hope") },
  },
  {
    text = "Dig on the marked tile south of the city.",
    actions = { Action.Direction:new(2078, 149, 3863, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(lunarRing) },
  },
  {
    text = "Talk to the Oneiromancer.",
    actions = {
      Action.Direction:new(2150, 277, 3867, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["oneiromancer"], { distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("Thanks") },
  },
  {
    actions = {
      Action.Direction:new(2150, 277, 3867, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["oneiromancer"], { distance = 20 }),
    },
    postconditions = { Condition.InventoryContains:new(lunarTorso) },
  },
  {
    text = "Use the vial on the kindling.",
    title = "Getting ready for bed",
    actions = {
      Action.InventoryHighlight:new(kindling),
      Action.InventoryHighlight:new(guamVial),
    },
    postconditions = { Condition.ChatText:new("soak the kindling") },
  },
  {
    text = "Get ready for the next sections. Manually move to the next step when you're ready.<ul><li>Equip <b>ALL</b> lunar gear.</li><li>Get your combat runes (prioritize fire spells) and supplies.</li><li>Dismiss your follower</li><li>Wear all your lunar items.</li><li>Four backpack spaces are required.</li><li>Do <b>not</b> carry an augmented crystal hatchet in the backpack to avoid a bug later in the quest.</li></ul>",
  },
  {
    text = "Light the ceremonial brazier in the building west of the lodestone.",
    actions = {
      Action.Direction:new(2073, 3485, 3912, { distance = 15 }),
      Action.ModelHighlight:new(brazier, { distance = 16 }),
    },
    postconditions = { Condition.ModelVisible:new(litBrazier) },
  },
  {
    text = "Use the soaked kindling on the brazier.<ul><li>If you exit the Dreamworld early, you can get a new kindling from the Oneiromancer.</li></ul>",
    actions = {
      Action.ModelHighlight:new(brazier),
      Action.InventoryHighlight:new(kindling),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to the Ethereal Man/Lady.",
    title = "Dicing",
    actions = { Action.ModelHighlight:new(etherealBeing, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("Good point") },
  },
  {
    text = "Step on the yellow/green platform south-west of the island.",
    actions = { Action.Direction:new(-12, -32, -7, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-28, -16, -18, 4, true) },
  },
  {
    text = "Talk to Ethereal Fluke.",
    actions = { Action.ModelHighlight:new(etherealFluke, { instanced = true, highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationText:new("number you want") },
  },
  {
    text = "He will call out a number. You need to make the dice add up to the number he called. When you 'roll' one of the dice, it will turn the die upside down, so each die only has two possible numbers (1 and 6, 2 and 5, 3 and 4).",
    postconditions = { Condition.ConversationText:new("You've done it! Good work") },
  },
  {
    text = '<table cellspacing="13"><caption>Answers</caption><tbody><tr><td colspan="7" style="text-align: center; ;" class="table-na nohighlight" data-sort-value="0">N/A</td><th rowspan="10"></th><th>21</th><td>1</td><td>6</td><td>2</td><td>5</td><td>3</td><td>4</td></tr><tr><th>12</th><td>1</td><td>1</td><td>2</td><td>2</td><td>3</td><td>3</td><th>22</th><td>1</td><td>6</td><td>2</td><td>5</td><td>4</td><td>4</td></tr><tr><th>13</th><td>1</td><td>1</td><td>2</td><td>2</td><td>3</td><td>4</td><th>23</th><td>1</td><td>6</td><td>5</td><td>5</td><td>3</td><td>3</td></tr><tr><th>14</th><td>1</td><td>1</td><td>2</td><td>2</td><td>4</td><td>4</td><th>24</th><td>1</td><td>6</td><td>5</td><td>5</td><td>3</td><td>4</td></tr><tr><th>15</th><td>1</td><td>1</td><td>2</td><td>5</td><td>3</td><td>3</td><th>25</th><td>1</td><td>6</td><td>5</td><td>5</td><td>4</td><td>4</td></tr><tr><th>16</th><td>1</td><td>1</td><td>2</td><td>5</td><td>3</td><td>4</td><th>26</th><td>6</td><td>6</td><td>2</td><td>5</td><td>3</td><td>4</td></tr><tr><th>17</th><td>1</td><td>1</td><td>2</td><td>5</td><td>4</td><td>4</td><th>27</th><td>6</td><td>6</td><td>2</td><td>5</td><td>4</td><td>4</td></tr><tr><th>18</th><td>1</td><td>1</td><td>5</td><td>5</td><td>3</td><td>3</td><th>28</th><td>6</td><td>6</td><td>5</td><td>5</td><td>3</td><td>3</td></tr><tr><th>19</th><td>1</td><td>1</td><td>5</td><td>5</td><td>3</td><td>4</td><th>29</th><td>6</td><td>6</td><td>5</td><td>5</td><td>3</td><td>4</td></tr><tr><th>20</th><td>1</td><td>1</td><td>5</td><td>5</td><td>4</td><td>4</td><th>30</th><td>6</td><td>6</td><td>5</td><td>5</td><td>4</td><td>4</td></tr></tbody></table>',
    postconditions = { Condition.ConversationText:new("You've done it! Good work") },
  },
  {
    text = "Wait to be teleported back to the middle plaform.",
    postconditions = { Condition.DistanceTo:new(-12, -32, -7, 4, true) },
  },
  {
    text = "Talk to the Ethereal Man/Lady.",
    actions = { Action.ModelHighlight:new(etherealBeing, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("Spot on") },
  },
  {
    text = "Head south-east and step on the pink/purple platform.",
    title = "Number sequence",
    actions = { Action.Direction:new(5, -32, -7, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(24, -32, -20, 4, true) },
  },
  {
    text = "Talk to Ethereal Numerator.",
    actions = { Action.ModelHighlight:new(etherealNumerator, { instanced = true, highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ModelHighlight:new(etherealNumerator, { instanced = true, highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  { --should highlight last two numbers of each row like the wiki
    text = "Click on the numbers to complete the patterns for four times.<br/><br/><table><tbody><tr><td>0</td><td>1</td><td>3</td><td>4</td><td>6</td><td>7</td></tr><tr><td>1</td><td>1</td><td>1</td><td>2</td><td>1</td><td>3</td><td>1</td><td>4</td><td>1</td><td>5</td></tr><tr><td>1</td><td>1</td><td>2</td><td>2</td><td>3</td><td>3</td><td>4</td></tr><tr><td>1</td><td>1</td><td>2</td><td>3</td><td>1</td><td>1</td><td>4</td><td>5</td><td>1</td></tr><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td></tr><tr><td>1</td><td>3</td><td>5</td><td>7</td><td>9</td></tr><tr><td>1</td><td>4</td><td>2</td><td>5</td><td>3</td><td>6</td></tr><tr><td>1</td><td>6</td><td>2</td><td>5</td><td>3</td><td>4</td></tr><tr><td>1</td><td>9</td><td>2</td><td>8</td><td>3</td><td>7</td></tr><tr><td>2</td><td>3</td><td>5</td><td>6</td><td>8</td><td>9</td></tr><tr><td>2</td><td>6</td><td>3</td><td>7</td><td>4</td><td>8</td></tr><tr><td>3</td><td>4</td><td>2</td><td>5</td><td>1</td><td>6</td></tr><tr><td>7</td><td>3</td><td>6</td><td>2</td><td>5</td><td>1</td></tr><tr><td>8</td><td>6</td><td>4</td><td>2</td><td>0</td></tr><tr><td>9</td><td>7</td><td>5</td><td>3</td><td>1</td></tr><tr><td>9</td><td>8</td><td>7</td><td>6</td><td>5</td><td >4</td></tr></tbody></table>",
    postconditions = { Condition.ConversationText:new("hang of 124151 this now") }, --not tested
  },
  {
    text = "Wait to be be teleported back to the middle plaform.",
    postconditions = { Condition.DistanceTo:new(5, -32, -7, 4, true) },
  },
  {
    text = "Talk to the Ethereal Man/Lady.",
    actions = { Action.ModelHighlight:new(etherealBeing, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("great deal") },
  },
  {
    text = "Head north-east and step on the blue platform.",
    title = "Logging race",
    actions = { Action.Direction:new(1, -16, 11, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(5, -32, 25, 4, true) },
  },
  {
    text = "Talk to Ethereal Perceptive.<ul><li>Bug: Do not carry an augmented crystal hatchet or you will have to leave to bank it.</li></ul>",
    actions = {
      Action.ModelHighlight:new(etherealPerceptive, { instanced = true }),
      Action.ConversationHighlight:new("Ok, let's go!"),
    },
    postconditions = { Condition.ChatText:new("Go go") },
  },
  {
    text = "Run west.",
    actions = { Action.Direction:new(-12, -16, 25, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(-12, -16, 25, 4, true) },
  },
  {
    text = "Run west and chop 20 dream logs, depositing them on your log pile.",
    actions = { Action.ModelHighlight:new(dreamTree, { instanced = true, highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(dreamLog, 20) },
  },
  {
    text = "Deposit your logs.",
    actions = { Action.Direction:new(-4, 0, 24, { instance = true, tile = true }) },
    postconditions = { Condition.ConversationText:new("Wew done") },
  },
  {
    text = "Wait to be be teleported back to the middle plaform.",
    postconditions = { Condition.DistanceTo:new(1, -16, 11, 4, true) },
  },
  {
    text = "Talk to the Ethereal Man/Lady.",
    actions = { Action.ModelHighlight:new(etherealBeing, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("very impressed") },
  },
  {
    text = "Head north-west and step on the grey platform.",
    title = "Memorisation",
    actions = { Action.Direction:new(-12, -8, 8, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-27, -32, 25, 4, true) },
  },
  {
    text = "Talk to Ethereal Guide.",
    actions = { Action.ModelHighlight:new(etherealGuide, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("Very well") },
  },
  {
    text = "Cross the chasm by jumping on the correct combination of Dream Puffs. <b>Keep a note of the correct steps!</b> You can use the table on the wiki to track them.",
    postconditions = { Condition.ConversationText:new("have you learned") },
  },
  {
    text = "Wait to be be teleported back to the middle plaform.",
    postconditions = { Condition.DistanceTo:new(-12, -8, 8, 4, true) },
  },
  {
    text = "Talk to the Ethereal Man/Lady.",
    actions = { Action.ModelHighlight:new(etherealBeing, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("Bingo") },
  },
  {
    text = "Head east and step on the yellow platform.",
    title = "The hurdles",
    actions = { Action.Direction:new(7, -32, 1, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(22, -32, -9, 4, true) },
  },
  {
    text = "Talk to Ethereal Expert and start the race.",
    actions = { Action.ModelHighlight:new(etherealExpert, { instanced = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(etherealExpert, { instanced = true }),
      Action.ConversationHighlight:new("Ok."),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Jump over the hurdles. If you fail three times, you will lose the race. Run and stop before each hurdle, before clicking the orb to jump over.",
    actions = { Action.ConversationHighlight:new("Eat my dust!") },
    postconditions = { Condition.ConversationText:new("I won") },
  },
  {
    text = "Wait to be be teleported back to the middle plaform.",
    postconditions = { Condition.DistanceTo:new(7, -32, 1, 4, true) },
  },
  {
    text = "Talk to the Ethereal Man/Lady.",
    actions = { Action.ModelHighlight:new(etherealBeing, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("Very, very good") },
  },
  {
    text = "Head south and step on the green platform.",
    title = "Copy cat",
    actions = { Action.Direction:new(2, 0, -8, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(10, -32, -17, 4, true) },
  },
  {
    text = "Talk to Ethereal Mimic.",
    actions = {
      Action.ModelHighlight:new(etherealMimic, { instance = true, highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Suppose I may as well have a go."),
    },
    postconditions = { Condition.ConversationText:new("Follow!") },
  },
  {
    text = "Click on them and then copy the emote that they perform. They teleport after each correct emote.",
    actions = { Action.ModelHighlight:new(etherealMimic, { instance = true, highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationText:new("Yep") },
  },
  {
    text = "Wait to be be teleported back to the middle plaform.",
    postconditions = { Condition.DistanceTo:new(2, 0, -8, 4, true) },
  },
  {
    text = "Talk to the Ethereal Man/Lady.",
    actions = {
      Action.ModelHighlight:new(etherealBeing, { instanced = true }),
      Action.ConversationHighlight:new("Of course. I'm ready."),
    },
    postconditions = { Condition.ConversationText:new("about right to me") },
  },
  {
    text = "Talk to the Ethereal Man/Lady to begin the final fight.",
    title = "Fight Me",
    actions = {
      Action.ModelHighlight:new(etherealBeing, { instanced = true }),
      Action.ConversationHighlight:new("Of course. I'm ready."),
    },
    postconditions = { Condition.ConversationText:new("I'm ready") },
  },
  {
    text = "Kill Me. You can safe-spot them behind the dream tree.<ul><li>Should you forget your runes after entering the fight, you will not be able to leave the fight in any way unless you log out and back in. This will take you back to Lunar Isle where you can bank to get the runes.</li><li>Be sure to equip your Seal of passage before speaking to a banker.</li><li>Go back to the Oneiromancer again to get the kindling and potion to return back to the dream.</li><li>Re-equip your Lunar amulet before attempting to enter the dream.</li></ul>",
    warning = "No tracking for this step.",
  },
  {
    text = "Read the lectern to return to Lunar Isle.",
    title = "Finishing up",
    actions = {
      Action.ModelHighlight:new(lectern, { instanced = true }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to the Oneiromancer.",
    actions = {
      Action.Direction:new(2150, 277, 3867, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["oneiromancer"], { distance = 20 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Lunar Diplomacy",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1153699200,
  prereqQuests = { "The Fremennik Trials", "Lost City" },
  questReqs = {
    Types.QuestReq.skill("Crafting", 61),
    Types.QuestReq.skill("Defence", 40),
    Types.QuestReq.skill("Firemaking", 49),
    Types.QuestReq.skill("Herblore", 5),
    Types.QuestReq.skill("Magic", 65),
    Types.QuestReq.skill("Mining", 60),
    Types.QuestReq.skill("Woodcutting", 55),
  },
  neededItems = {
    ["Coins"] = { quantity = 1000 },
    ["Runes for combat spells"] = { quantity = 1 },
    ["Dramen staff"] = { quantity = 1, model = Models.items["dramen staff"] },
    ["Spade (tool belt doesn't work)"] = { quantity = 1, model = Models.items["spade"] },
    ["Access to the air, water, earth, and fire altars"] = { quantity = 1 },
    ["Thread"] = { quantity = 2, model = Models.items["thread"] },
    ["Clean guam"] = { quantity = 1, model = Models.items["clean guam"], duringQuest = true },
    ["Clean marrentill"] = { quantity = 1, model = Models.items["clean marrentill"], duringQuest = true },
  },
  recommendedItems = {
    ["Coins"] = { quantity = 2000 },
    ["Wicked hood"] = { quantity = 1 },
    ["Skull sceptre"] = { quantity = 1 },
    ["Ring of duelling"] = { quantity = 1 },
    ["Enlightened amulet"] = { quantity = 1 },
    ["Archaeology journal"] = { quantity = 1 },
    ["Agility boost (Summer pie, Surefooted aura)"] = { quantity = 1 },
    ["Extra dramen staves (for more lunar staves)"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Suqah"] = { level = "73, 74, 79", quantity = 5 },
    ["Me"] = { level = "84", quantity = 1 },
  },
})
