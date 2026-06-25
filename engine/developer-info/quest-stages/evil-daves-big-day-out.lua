local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local kara = Model.new(4665, {
  [1695] = Vertex.new(24, 738, -38, 31, 29, 28),
  [1719] = Vertex.new(-24, 738, -38, 31, 29, 28),
  [1741] = Vertex.new(-2, 716, -57, 107, 79, 55),
  [1747] = Vertex.new(2, 716, -57, 107, 79, 55),
  [1751] = Vertex.new(6, 716, -52, 107, 79, 55),
})
local clark = Model.new(2916, {
  [2333] = Vertex.new(31, 764, -34, 138, 101, 71),
  [2513] = Vertex.new(-19, 755, -48, 60, 55, 46),
  [2530] = Vertex.new(19, 762, -43, 135, 131, 124),
  [2534] = Vertex.new(19, 762, -43, 135, 131, 124),
  [2542] = Vertex.new(-19, 762, -43, 135, 131, 124),
})
local shopkeeper = Model.new(3807, {
  [2680] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [2685] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [2688] = Vertex.new(2, 725, -59, 107, 79, 55),
  [2690] = Vertex.new(7, 724, -51, 107, 79, 55),
  [3235] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local hellrat = Model.new(996, {
  [23] = Vertex.new(-28, 100, -180, 0, 0, 0),
  [26] = Vertex.new(28, 100, -180, 0, 0, 0),
  [35] = Vertex.new(-28, 100, -180, 0, 0, 0),
  [327] = Vertex.new(-24, 24, 208, 0, 0, 0),
  [351] = Vertex.new(24, 24, 208, 0, 0, 0),
})
local shadyMan = Model.new(4299, {
  [1324] = Vertex.new(-2, 725, -59, 70, 51, 28),
  [1329] = Vertex.new(-7, 724, -51, 70, 51, 28),
  [1332] = Vertex.new(2, 725, -59, 70, 51, 28),
  [1334] = Vertex.new(7, 724, -51, 70, 51, 28),
  [3949] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local guard = Model.new(3999, {
  [1031] = Vertex.new(-36, 831, -38, 83, 75, 42),
  [1851] = Vertex.new(40, 831, -40, 100, 94, 64),
  [1866] = Vertex.new(0, 834, 48, 100, 94, 64),
  [2039] = Vertex.new(40, 831, -40, 83, 75, 42),
  [2586] = Vertex.new(-4, 809, -67, 127, 116, 65),
})
--#endregion
--#region Objects
local mirror = Model.new(1464, {
  [19] = Vertex.new(28, 908, 48, 47, 37, 4),
  [21] = Vertex.new(108, 724, 124, 47, 37, 4),
  [22] = Vertex.new(28, 908, 48, 47, 37, 4),
  [51] = Vertex.new(-40, 908, -20, 47, 37, 4),
  [66] = Vertex.new(28, 908, 48, 47, 37, 4),
})
local wardrobe = Model.new(1020, {
  [131] = Vertex.new(222, 839, 154, 127, 127, 127),
  [145] = Vertex.new(190, 839, 130, 127, 127, 127),
  [155] = Vertex.new(222, 839, 154, 127, 127, 127),
  [167] = Vertex.new(-189, 839, 130, 127, 127, 127),
  [367] = Vertex.new(-220, 839, 154, 127, 127, 127),
})
local sink = Model.new(2790, {
  [557] = Vertex.new(-414, 307, 188, 177, 177, 177),
  [2041] = Vertex.new(408, 312, -194, 177, 177, 177),
  [2494] = Vertex.new(-423, 312, 188, 177, 177, 177),
  [2500] = Vertex.new(-423, 312, 188, 177, 177, 177),
  [2548] = Vertex.new(-408, 312, -194, 177, 177, 177),
})
local tableOfVegetables = Model.new(9801, {
  [1711] = Vertex.new(-421, 336, 100, 127, 127, 127),
  [5302] = Vertex.new(-419, 336, 81, 127, 127, 127),
  [5329] = Vertex.new(-412, 336, 105, 127, 127, 127),
  [5338] = Vertex.new(-402, 336, 102, 127, 127, 127),
  [5347] = Vertex.new(-424, 336, 90, 127, 127, 127),
})
local bed = Model.new(3048, {
  [281] = Vertex.new(-223, 162, 462, 127, 127, 127),
  [337] = Vertex.new(-238, 162, -449, 127, 127, 127),
  [563] = Vertex.new(227, 162, 462, 127, 127, 127),
  [613] = Vertex.new(241, 162, -492, 127, 127, 127),
  [799] = Vertex.new(242, 162, -453, 127, 127, 127),
})
local bookcase = Model.new(3132, {
  [1130] = Vertex.new(-212, 969, 199, 127, 127, 127),
  [1151] = Vertex.new(212, 969, 199, 127, 127, 127),
  [1814] = Vertex.new(212, 989, 199, 127, 127, 127),
  [1835] = Vertex.new(-212, 989, 68, 127, 127, 127),
  [2471] = Vertex.new(93, 912, 194, 127, 127, 127),
})
local boiler = Model.new(2424, {
  [1544] = Vertex.new(29, 608, -119, 66, 57, 34),
  [1585] = Vertex.new(-142, 594, -21, 66, 57, 34),
  [1588] = Vertex.new(-142, 594, -21, 66, 57, 34),
  [1590] = Vertex.new(-113, 594, -49, 66, 57, 34),
  [1632] = Vertex.new(-142, 594, -21, 66, 57, 34),
})
local drapes = Model.new(462, {
  [10] = Vertex.new(-236, 932, -252, 89, 70, 46),
  [35] = Vertex.new(-252, 912, -180, 86, 73, 66),
  [39] = Vertex.new(-212, 912, 176, 86, 73, 66),
  [277] = Vertex.new(-256, 944, -160, 125, 20, 11),
  [288] = Vertex.new(-256, 944, 108, 114, 18, 10),
})
local fountain = Model.new(14934, {
  [3468] = Vertex.new(2715, 2300, 676, 90, 95, 98),
  [8560] = Vertex.new(2724, 2327, 679, 90, 95, 98),
  [14365] = Vertex.new(2733, 2324, 691, 90, 95, 98),
  [14368] = Vertex.new(2733, 2324, 691, 90, 95, 98),
  [14679] = Vertex.new(2724, 2318, 681, 90, 95, 98),
})
local keg = Model.new(1512, {
  [15] = Vertex.new(-86, 488, -176, 177, 177, 177),
  [59] = Vertex.new(-48, 283, -229, 177, 177, 177),
  [71] = Vertex.new(-56, 263, -229, 177, 177, 177),
  [1368] = Vertex.new(-86, 488, -176, 177, 177, 177),
  [1496] = Vertex.new(-86, 488, -176, 177, 177, 177),
})
local coconutTree = Model.new(1188, {
  [24] = Vertex.new(30, 1683, 120, 177, 177, 177),
  [321] = Vertex.new(112, 1684, 83, 177, 177, 177),
  [363] = Vertex.new(68, 1687, 144, 177, 177, 177),
  [549] = Vertex.new(73, 1674, 148, 177, 177, 177),
  [591] = Vertex.new(116, 1669, 75, 177, 177, 177),
})
local jailBarrel = Model.new(909, {
  [591] = Vertex.new(164, 156, -132, 114, 95, 72),
  [594] = Vertex.new(130, 224, -153, 114, 95, 72),
  [597] = Vertex.new(64, 224, -183, 114, 95, 72),
  [833] = Vertex.new(130, 224, -153, 74, 62, 47),
  [835] = Vertex.new(130, 224, -153, 74, 62, 47),
})
local mysteriousEntrance = Model.new(2835, {
  [225] = Vertex.new(-244, 109, -456, 0, 0, 0, 0.9922),
  [228] = Vertex.new(-362, 0, -456, 0, 0, 0, 0.9922),
  [263] = Vertex.new(342, 450, -188, 50, 46, 46, 0.9922),
  [792] = Vertex.new(-463, 0, 456, 71, 72, 66, 0.9922),
  [2438] = Vertex.new(252, 430, -284, 76, 77, 70, 0.9922),
})
local cookingRange = Model.new(3987, {
  [1443] = Vertex.new(-392, 420, -444, 50, 46, 47),
  [2009] = Vertex.new(436, 840, 512, 92, 77, 58),
  [2180] = Vertex.new(436, 840, 512, 77, 64, 49),
  [2183] = Vertex.new(436, 720, 512, 74, 62, 47),
  [2196] = Vertex.new(300, 776, 512, 77, 64, 49),
})
--#endregion
--#region Items
local crystalAcorn = Model.new(312, {
  [244] = Vertex.new(-10, 41, -69, 62, 133, 151),
  [249] = Vertex.new(-10, 41, -69, 62, 133, 151),
  [266] = Vertex.new(10, 41, -69, 62, 133, 151),
  [269] = Vertex.new(10, 41, -69, 62, 133, 151),
  [273] = Vertex.new(10, 41, -69, 62, 133, 151),
})
--#endregion
--#region Quest Items
local broom = Model.new(348, {
  [3] = Vertex.new(252, 4, -228, 91, 77, 37),
  [33] = Vertex.new(252, 4, -228, 91, 77, 37),
  [282] = Vertex.new(-224, 68, 220, 89, 73, 46),
  [296] = Vertex.new(-224, 68, 220, 89, 73, 46),
  [317] = Vertex.new(-224, 68, 220, 89, 73, 46),
})
local bedBugCleaner = Model.new(402, {
  [188] = Vertex.new(4, 288, -4, 82, 75, 75),
  [191] = Vertex.new(-4, 288, -4, 82, 75, 75),
  [192] = Vertex.new(4, 288, -4, 82, 75, 75),
  [194] = Vertex.new(-4, 288, -4, 82, 75, 75),
  [197] = Vertex.new(4, 288, -4, 82, 75, 75),
})
local soap = Model.new(180, {
  [3] = Vertex.new(-52, 40, 24, 146, 149, 136),
  [5] = Vertex.new(48, 40, -28, 146, 149, 136),
  [9] = Vertex.new(-52, 40, 24, 146, 149, 136),
  [12] = Vertex.new(-52, 40, 24, 146, 149, 136),
  [14] = Vertex.new(-52, 40, 24, 146, 149, 136),
})
local knife = Model.new(198, {
  [131] = Vertex.new(8, 24, -148, 115, 106, 106),
  [136] = Vertex.new(8, 24, -148, 115, 106, 106),
  [143] = Vertex.new(8, 24, -148, 81, 57, 24),
  [146] = Vertex.new(8, 24, -148, 81, 57, 24),
  [150] = Vertex.new(8, 24, -148, 81, 57, 24),
})
local davesSpellbook = Model.new(744, {
  [225] = Vertex.new(30, 48, -65, 110, 109, 9),
  [227] = Vertex.new(-28, 49, -59, 110, 109, 9),
  [231] = Vertex.new(30, 48, 59, 110, 109, 9),
  [233] = Vertex.new(-28, 49, 64, 110, 109, 9),
  [237] = Vertex.new(-48, 49, -44, 110, 109, 9),
})
local emergencyStewFlask = Model.multi({
  Model.new(789, {
    [89] = Vertex.new(74, 223, 12, 98, 107, 101),
    [408] = Vertex.new(-11, 257, 78, 141, 154, 147),
    [450] = Vertex.new(-11, 257, -78, 141, 154, 147),
    [660] = Vertex.new(-11, 257, 78, 98, 107, 101),
    [689] = Vertex.new(-11, 257, -78, 98, 107, 101),
  }),
  Model.new(534, {
    [385] = Vertex.new(-57, 223, -57, 88, 93, 96, 0.6078),
    [388] = Vertex.new(-57, 223, -57, 88, 93, 96, 0.6078),
    [396] = Vertex.new(-57, 223, 57, 88, 93, 96, 0.6078),
    [411] = Vertex.new(-57, 223, -57, 88, 93, 96, 0.6078),
    [415] = Vertex.new(57, 223, -57, 88, 93, 96, 0.6078),
  }),
})
local oldRug = Model.multi({
  Model.new(2712, {
    [12] = Vertex.new(-151, 177, -157, 192, 192, 192),
    [27] = Vertex.new(-101, 177, 226, 192, 192, 192),
    [42] = Vertex.new(101, 177, 226, 192, 192, 192),
    [114] = Vertex.new(-101, 161, 226, 192, 192, 192),
    [123] = Vertex.new(101, 161, 226, 192, 192, 192),
  }),
  Model.new(2712, {
    [477] = Vertex.new(150, 177, 78, 192, 192, 192),
    [699] = Vertex.new(163, 161, 382, 192, 192, 192),
    [872] = Vertex.new(156, 177, 296, 192, 192, 192),
    [888] = Vertex.new(163, 161, 382, 192, 192, 192),
    [990] = Vertex.new(150, 161, 78, 192, 192, 192),
  }),
})
local emptyFlask = Model.multi({
  Model.new(198, {
    [18] = Vertex.new(31, 258, 25, 111, 92, 70),
    [53] = Vertex.new(31, 258, 25, 111, 92, 70),
    [152] = Vertex.new(31, 258, 25, 139, 115, 88),
    [153] = Vertex.new(36, 258, 5, 139, 115, 88),
    [156] = Vertex.new(31, 258, 25, 139, 115, 88),
  }),
  Model.new(606, {
    [59] = Vertex.new(-22, 203, -22, 86, 91, 93, 0.7059),
    [62] = Vertex.new(22, 203, -22, 86, 91, 93, 0.7059),
    [66] = Vertex.new(22, 203, -22, 86, 91, 93, 0.7059),
    [68] = Vertex.new(-22, 203, 22, 86, 91, 93, 0.7059),
    [72] = Vertex.new(-22, 203, 22, 86, 91, 93, 0.7059),
  }),
})
local waterFlask = Model.multi({
  Model.new(198, {
    [18] = Vertex.new(31, 258, 25, 103, 86, 65),
    [53] = Vertex.new(31, 258, 25, 103, 86, 65),
    [152] = Vertex.new(31, 258, 25, 131, 109, 83),
    [153] = Vertex.new(36, 258, 5, 131, 109, 83),
    [156] = Vertex.new(31, 258, 25, 131, 109, 83),
  }),
  Model.new(678, {
    [131] = Vertex.new(-22, 203, -22, 79, 84, 87, 0.7059),
    [134] = Vertex.new(22, 203, -22, 79, 84, 87, 0.7059),
    [138] = Vertex.new(22, 203, -22, 79, 84, 87, 0.7059),
    [140] = Vertex.new(-22, 203, 22, 79, 84, 87, 0.7059),
    [144] = Vertex.new(-22, 203, 22, 79, 84, 87, 0.7059),
  }),
})
local brokenBroom = Model.new(417, {
  [15] = Vertex.new(333, 4, -166, 91, 77, 37),
  [33] = Vertex.new(333, 4, -166, 91, 77, 37),
  [309] = Vertex.new(-274, 68, 178, 89, 73, 46),
  [335] = Vertex.new(-274, 68, 178, 89, 73, 46),
  [350] = Vertex.new(-274, 68, 178, 89, 73, 46),
})
local brokenDishes = Model.new(222, {
  [41] = Vertex.new(-40, 13, 154, 162, 155, 148),
  [57] = Vertex.new(-43, 13, -141, 162, 155, 148),
  [119] = Vertex.new(-40, 13, 154, 113, 109, 104),
  [194] = Vertex.new(-40, 13, 154, 72, 69, 66),
  [198] = Vertex.new(-40, 13, 154, 72, 69, 66),
})
local brokenKnife = Model.new(222, {
  [36] = Vertex.new(6, 12, 119, 115, 106, 106),
  [115] = Vertex.new(6, 12, 119, 115, 106, 106),
  [119] = Vertex.new(6, 12, 119, 115, 106, 106),
  [140] = Vertex.new(72, 24, -107, 115, 106, 106),
  [142] = Vertex.new(72, 24, -107, 115, 106, 106),
})
local handWrittenNote = Model.new(399, {
  [50] = Vertex.new(-28, 16, 68, 0, 0, 0),
  [156] = Vertex.new(16, 16, 72, 0, 0, 0),
  [188] = Vertex.new(-44, 16, -44, 29, 4, 2),
  [258] = Vertex.new(12, 16, -72, 35, 5, 3),
  [323] = Vertex.new(-12, 16, -72, 35, 5, 3),
})
local knightsHelmet = Model.new(984, {
  [318] = Vertex.new(47, 77, 55, 117, 108, 107),
  [402] = Vertex.new(-48, 77, 55, 117, 108, 107),
  [543] = Vertex.new(48, 77, 55, 117, 108, 107),
  [642] = Vertex.new(-47, 77, 55, 117, 108, 107),
  [793] = Vertex.new(-57, 146, 92, 147, 135, 135),
})
local gunpowder = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 119, 110, 109),
  [34] = Vertex.new(100, 0, 60, 119, 110, 109),
  [42] = Vertex.new(100, 0, 60, 119, 110, 109),
  [64] = Vertex.new(132, 0, -24, 119, 110, 109),
  [72] = Vertex.new(132, 0, -24, 119, 110, 109),
})
local charredBoots = Model.new(564, {
  [426] = Vertex.new(-54, 0, -39, 107, 98, 98),
  [457] = Vertex.new(68, 0, 38, 117, 108, 107),
  [462] = Vertex.new(68, 0, 38, 117, 108, 107),
  [469] = Vertex.new(-21, 0, 75, 117, 108, 107),
  [473] = Vertex.new(-21, 0, 75, 117, 108, 107),
})
local coconut = Model.new(240, {
  [226] = Vertex.new(-64, 136, 48, 90, 60, 18),
  [229] = Vertex.new(-64, 136, 48, 90, 60, 18),
  [232] = Vertex.new(-64, 136, 48, 90, 60, 18),
  [235] = Vertex.new(-64, 136, 48, 90, 60, 18),
  [238] = Vertex.new(-64, 136, 48, 90, 60, 18),
})
local emptyCoconutShell = Model.new(450, {
  [1] = Vertex.new(-76, 128, -4, 81, 52, 7),
  [2] = Vertex.new(-56, 128, -36, 81, 52, 7),
  [3] = Vertex.new(-64, 128, -44, 81, 52, 7),
  [6] = Vertex.new(-68, 128, -4, 81, 52, 7),
  [7] = Vertex.new(-12, 124, -64, 81, 52, 7),
  [61] = Vertex.new(0, 88, -88, 90, 60, 18),
  [62] = Vertex.new(-12, 124, -72, 90, 60, 18),
  [63] = Vertex.new(28, 124, -68, 90, 60, 18),
  [65] = Vertex.new(52, 88, -68, 90, 60, 18),
  [69] = Vertex.new(64, 124, -32, 90, 60, 18),
  [241] = Vertex.new(-8, 124, -48, 52, 15, 4),
  [242] = Vertex.new(20, 76, -52, 52, 15, 4),
  [243] = Vertex.new(20, 124, -48, 52, 15, 4),
  [246] = Vertex.new(-8, 80, -56, 52, 15, 4),
  [247] = Vertex.new(-12, 40, 36, 52, 15, 4),
  [248] = Vertex.new(12, 76, 60, 52, 15, 4),
  [249] = Vertex.new(8, 40, 40, 52, 15, 4),
  [252] = Vertex.new(-20, 80, 56, 52, 15, 4),
  [253] = Vertex.new(-28, 40, 20, 52, 15, 4),
  [256] = Vertex.new(12, 124, 56, 52, 15, 4),
})
local coconutDrink = Model.new(450, {
  [1] = Vertex.new(-76, 128, -4, 81, 52, 7),
  [2] = Vertex.new(-56, 128, -36, 81, 52, 7),
  [3] = Vertex.new(-64, 128, -44, 81, 52, 7),
  [6] = Vertex.new(-68, 128, -4, 81, 52, 7),
  [7] = Vertex.new(-12, 124, -64, 81, 52, 7),
  [61] = Vertex.new(0, 88, -88, 90, 60, 18),
  [62] = Vertex.new(-12, 124, -72, 90, 60, 18),
  [63] = Vertex.new(28, 124, -68, 90, 60, 18),
  [65] = Vertex.new(52, 88, -68, 90, 60, 18),
  [69] = Vertex.new(64, 124, -32, 90, 60, 18),
  [241] = Vertex.new(-8, 124, -48, 161, 167, 173),
  [242] = Vertex.new(20, 76, -52, 161, 167, 173),
  [243] = Vertex.new(20, 124, -48, 161, 167, 173),
  [246] = Vertex.new(-8, 80, -56, 161, 167, 173),
  [247] = Vertex.new(-12, 40, 36, 161, 167, 173),
  [248] = Vertex.new(12, 76, 60, 161, 167, 173),
  [249] = Vertex.new(8, 40, 40, 161, 167, 173),
  [252] = Vertex.new(-20, 80, 56, 161, 167, 173),
  [253] = Vertex.new(-28, 40, 20, 161, 167, 173),
  [256] = Vertex.new(12, 124, 56, 161, 167, 173),
})
local shellRat = Model.new(1083, {
  [101] = Vertex.new(7, 244, 15, 0, 0, 0),
  [107] = Vertex.new(56, 234, -10, 0, 0, 0),
  [110] = Vertex.new(56, 234, -10, 0, 0, 0),
  [113] = Vertex.new(56, 234, -10, 0, 0, 0),
  [489] = Vertex.new(33, 46, 16, 118, 19, 10),
})
local barrel = Model.new(909, {
  [594] = Vertex.new(130, 224, -153, 114, 95, 72),
  [597] = Vertex.new(64, 224, -183, 114, 95, 72),
  [711] = Vertex.new(130, 224, -153, 55, 45, 34),
  [833] = Vertex.new(130, 224, -153, 74, 62, 47),
  [835] = Vertex.new(130, 224, -153, 74, 62, 47),
})
local carpetBarrel = Model.new(1464, {
  [917] = Vertex.new(86, 168, -226, 156, 115, 31),
  [945] = Vertex.new(109, 241, -174, 156, 115, 31),
  [947] = Vertex.new(133, 220, -180, 156, 115, 31),
  [951] = Vertex.new(113, 210, -189, 136, 101, 27),
  [953] = Vertex.new(133, 215, -160, 136, 101, 27),
})
--#endregion

local bankXButtonTexture =
  "\x6b\x2d\x28\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6d\x31\x2a\xff\x6d\x31\x2a\xff\x6b\x2d\x28\xff\xb3\x8c\x5d\xff\xcb\xab\x6f\xff\xb3\x8c\x5d\xff\x6b\x2d\x28\xff\x6b\x2d\x28\xff\x6d\x31\x2a\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6b\x2d\x28\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Go to the Basement of Doom, located under a house west of Edgeville bank.",
    title = "Freaky Friday",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(3077, 1669, 3493, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3077, 3045, 9893, 4) },
  },
  {
    text = "Talk to Evil Dave.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["evil dave"]),
      Action.ConversationHighlight:new("'Evil Dave's Big Day Out'."),
      Action.ConversationHighlight:new("I was looking for a quest."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Evil Dave.",
    actions = { Action.ModelHighlight:new(Models.npcs["evil dave"]) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("mirror I can use") },
  },
  {
    text = "Admire the mirror.",
    actions = { Action.ModelHighlight:new(mirror) },
    postconditions = { Condition.ConversationText:new("going on") },
  },
  {
    text = "Climb the cellar stairs.",
    actions = { Action.Direction:new(-7, 600, -4, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Doris.",
    actions = { Action.ModelHighlight:new(Models.npcs["doris"]) },
    postconditions = { Condition.ConversationText:new("get on with it") },
  },
  {
    text = "Search the wardrobe for a broom and bed bug cleaner.",
    title = "Chores of doom",
    actions = {
      Action.ModelHighlight:new(wardrobe),
      Action.ConversationHighlight:new("Broom"),
    },
    postconditions = { Condition.InventoryContains:new(broom) },
  },
  {
    actions = {
      Action.ModelHighlight:new(wardrobe),
      Action.ConversationHighlight:new("Bed bug cleaner"),
    },
    postconditions = { Condition.InventoryContains:new(bedBugCleaner) },
  },
  {
    text = "Sweep the rug.",
    actions = { Action.Direction:new(1, 0, 0, { instance = true, tile = true }) },
    postconditions = { Condition.ChatText:new("1/4 chores") },
  },
  {
    text = "Search the shelf for a bar of soap.",
    actions = { Action.Direction:new(-1.15, 900, -1.05, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(soap) },
  },
  {
    text = "Wash the dishes in the sink.",
    actions = { Action.ModelHighlight:new(sink) },
    postconditions = { Condition.ChatText:new("2/4 chores") },
  },
  {
    text = "Take the knife on the sink.",
    actions = { Action.ModelHighlight:new(sink) },
    postconditions = { Condition.InventoryContains:new(knife) },
  },
  {
    text = "Chop the vegetables.",
    actions = { Action.ModelHighlight:new(tableOfVegetables) },
    postconditions = { Condition.ChatText:new("3/4 chores") },
  },
  {
    text = "Go downstairs.",
    actions = { Action.Direction:new(-1, 0, 0, { instance = true, tile = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Clean your bed.",
    actions = { Action.ModelHighlight:new(bed) },
    postconditions = { Condition.ChatText:new("4/4 chores") },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("check in with Doris") },
  },
  {
    text = "Go back upstairs.",
    actions = { Action.Direction:new(-6, 600, -4, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Doris.",
    actions = { Action.ModelHighlight:new(Models.npcs["doris"]) },
    postconditions = { Condition.ConversationText:new("for his spellbook") },
  },
  {
    text = "Go back downstairs.",
    actions = { Action.Direction:new(-1, 0, 0, { instance = true, tile = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the bookcase by the stairs.",
    actions = {
      Action.ModelHighlight:new(bookcase),
      Action.ConversationHighlight:new("No, I need to find the spellbook."),
    },
    postconditions = { Condition.ChatText:new("1/3 important") },
  },
  {
    text = "Search behind the boiler in the southeast corner.",
    actions = { Action.ModelHighlight:new(boiler) },
    postconditions = { Condition.ChatText:new("2/3 important") },
  },
  {
    text = "Search the drapes by the bed.",
    actions = { Action.ModelHighlight:new(drapes) },
    postconditions = { Condition.ChatText:new("finished searching") },
  },
  {
    text = "Investigate Dave's spellbook.<ul><li>If you interact with Dave's spellbook again and get the message 'I don't need to use the spellbook right now', interact with the drapes once more and the option to teleport to Makeover Mage will appear.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(davesSpellbook),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("better check") },
  },
  {
    text = "Talk to Kara.",
    title = "Making a withdrawal",
    actions = { Action.ModelHighlight:new(kara) },
    postconditions = { Condition.Generic2DVisible:new(15, 15, 7, bankXButtonTexture) },
  },
  {
    text = "Withdraw the law rune, stew, and old rug from Dave's bank",
    postconditions = { Condition.InventoryContains:new(Models.items["law rune"]) },
  },
  { postconditions = { Condition.InventoryContains:new(emergencyStewFlask) } },
  { postconditions = { Condition.InventoryContains:new(oldRug) } },
  {
    text = "Talk to Clark.",
    actions = {
      Action.ModelHighlight:new(clark),
      Action.ConversationHighlight:new("Air Runes."),
    },
    postconditions = { Condition.ConversationText:new("would you") },
  },
  {
    text = "Drink the stew.",
    actions = { Action.InventoryHighlight:new(emergencyStewFlask) },
    postconditions = { Condition.InventoryContains:new(emptyFlask) },
  },
  {
    text = "Refill the flask from any of the water fountains.",
    actions = {
      Action.ModelHighlight:new(fountain),
      Action.InventoryHighlight:new(emptyFlask),
    },
    postconditions = { Condition.InventoryContains:new(waterFlask) },
  },
  {
    text = "Talk to Clark again.",
    actions = { Action.ModelHighlight:new(clark) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to the makeover mage.",
    title = "Double trouble",
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Doris.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["doris"]),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("Do them again") },
  },
  {
    text = "Sweep the rug again.",
    actions = { Action.Direction:new(0, 0, -3, { instance = true, tile = true }) },
    postconditions = { Condition.InventoryContains:new(brokenBroom) },
  },
  {
    text = "Wash the dishes again.",
    actions = { Action.ModelHighlight:new(sink) },
    postconditions = { Condition.InventoryContains:new(brokenDishes) },
  },
  {
    text = "Chop the vegetables again.",
    actions = { Action.ModelHighlight:new(tableOfVegetables) },
    postconditions = { Condition.InventoryContains:new(brokenKnife) },
  },
  {
    text = "Talk to the shopkeeper at the general store to the north.",
    actions = {
      Action.ModelHighlight:new(shopkeeper),
      Action.ConversationHighlight:new("Go to the shops"),
    },
    postconditions = { Condition.ConversationText:new("I... sure") }, --not tested
  },
  {
    text = "Sweep the rug again.",
    actions = { Action.Direction:new(0, 0, -3, { instance = true, tile = true }) },
    postconditions = { Condition.ChatText:new("1/4 chores") },
  },
  {
    text = "Wash the dishes again.",
    actions = { Action.ModelHighlight:new(sink) },
    postconditions = { Condition.ChatText:new("2/4 chores") },
  },
  {
    text = "Chop the vegetables again.",
    actions = { Action.ModelHighlight:new(tableOfVegetables) },
    postconditions = { Condition.ChatText:new("3/4 chores") },
  },
  {
    text = "Go downstairs.",
    actions = { Action.Direction:new(-2, 0, -3, { instance = true, tile = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Clean your bed again.",
    actions = { Action.ModelHighlight:new(bed) },
    postconditions = { Condition.ChatText:new("4/4 chores") }, --not tested
  },
  { postconditions = { Condition.ChangedInstance:new() } },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("chores are done") },
  },
  {
    text = "Go upstairs.",
    actions = { Action.Direction:new(-6, 600, -4, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Doris",
    actions = { Action.ModelHighlight:new(Models.npcs["doris"]) },
    postconditions = { Condition.ConversationText:new("head to Falador") },
  },
  {
    text = "Open the door.",
    actions = {
      Action.Direction:new(1, 600, 3.5, { instance = true }),
      Action.ConversationHighlight:new("Carry on with the quest."),
      Action.ConversationHighlight:new("I'm ready to sneak in."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Pickup the hand-written note in front of you.",
    title = "Sneaking in",
    actions = { Action.ModelHighlight:new(handWrittenNote) },
    postconditions = { Condition.InventoryContains:new(handWrittenNote) },
  },
  {
    text = "Search the suit of armour to the west for a knight's helmet.",
    actions = { Action.Direction:new(-9, 900, 0, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(knightsHelmet) },
  },
  {
    text = "Search the crates in the west for some gunpowder.",
    actions = { Action.Direction:new(-17, 400, -1, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(gunpowder) },
  },
  {
    text = "Use the gunpowder on the helmet.",
    actions = {
      Action.InventoryHighlight:new(gunpowder),
      Action.InventoryHighlight:new(knightsHelmet),
    },
    postconditions = { Condition.ConversationText:new("blow up in") },
  },
  {
    text = "Use the hand-written note on the helmet.",
    actions = {
      Action.InventoryHighlight:new(handWrittenNote),
      Action.InventoryHighlight:new(knightsHelmet),
    },
    postconditions = { Condition.ConversationText:new("shoddy and dangerous") },
  },
  {
    text = "Use the helmet with fuse on the torch on the wall.",
    actions = { Action.Direction:new(-15, 950, -2.25, { instance = true }) },
    postconditions = { Condition.ConversationText:new("quickly") },
  },
  {
    text = "Get close to the guard and throw the lit decoy bomb.",
    actions = {
      Action.Direction:new(2, 0, 4, { instance = true, tile = true }),
      Action.InventoryHighlight:new(knightsHelmet),
    },
    postconditions = { Condition.ModelVisible:new(charredBoots) },
  },
  {
    text = "Follow the path and open the door.",
    actions = {
      Action.PathGuide:new({
        Location:new(2, 0, 4),
        Location:new(2, 0, 13),
        Location:new(-3, 0, 17),
        Location:new(-10, 0, 19),
        Location:new(-26, 0, 19),
        Location:new(-26, 0, 13),
      }, { instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(Models.npcs["sir amik varze"]) },
  },
  {
    text = "Talk to Sir Amik to determine you need to give him more to drink.",
    actions = { Action.ModelHighlight:new(Models.npcs["sir amik varze"]) },
    postconditions = { Condition.ConversationText:new("even more drunk") },
  },
  {
    text = "Follow these steps quickly:<ul><li>Click the keg and wait for the drink callout.</li><li>Click the correct option.</li><li>Immediately click Sir Amik Varze.</li></ul>",
    actions = { Action.ModelHighlight:new(keg) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Doris.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["doris"]),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("test") },
  },
  {
    text = "Sweep the rug again.",
    actions = { Action.Direction:new(0, 0, -3, { instance = true, tile = true }) },
    postconditions = { Condition.ChatText:new("1/4 chores") },
  },
  {
    text = "Wash the dishes again.",
    actions = { Action.ModelHighlight:new(sink) },
    postconditions = { Condition.ChatText:new("2/4 chores") },
  },
  {
    text = "Chop the vegetables again.",
    actions = { Action.ModelHighlight:new(tableOfVegetables) },
    postconditions = { Condition.ChatText:new("3/4 chores") },
  },
  {
    text = "Go downstairs.",
    actions = { Action.Direction:new(-2, 0, -3, { instance = true, tile = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Clean your bed again.",
    actions = { Action.ModelHighlight:new(bed) },
    postconditions = { Condition.ConversationText:new("shall pass") },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("wake up Dave") },
  },
  {
    text = "Talk to Felicity or Margaret the hell-rats.<ul><li>If they appear as normal hell-rats: go up the stairs for a dialogue and go back down.</li></ul>",
    title = "Recruitment",
    actions = {
      Action.Direction:new(-7, 0, -11, { instance = true, tile = true }),
      Action.Direction:new(-7, 0, -12, { instance = true, tile = true }),
    },
    postconditions = {
      Condition.ChangedInstance:new(),
      Condition.ConversationText:new("Ciao"),
    },
  },
  {
    actions = { Action.Direction:new(-1, 0, 0, { instance = true, tile = true }) },
    postconditions = {
      Condition.ChangedInstance:new(),
      Condition.ConversationText:new("Ciao"),
    },
  },
  {
    actions = {
      Action.Direction:new(-2, 0, -7, { instance = true, tile = true }),
      Action.Direction:new(-2, 0, -8, { instance = true, tile = true }),
    },
    postconditions = {
      Condition.ConversationText:new("Ciao"),
    },
  },
  {
    text = "Recruit any 3 of the other hell-rats.",
    actions = {
      Action.ModelHighlight:new(hellrat, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("going on a mission") }, --not tested
  },
  {
    text = "Climb up the stairs to get to Shantay Pass.",
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Shantay.",
    title = "HELL-RAT DOOM SQUAD",
    actions = {
      Action.ModelHighlight:new(Models.npcs["shantay"]),
    },
    postconditions = { Condition.ConversationText:new("Oh, nothing, nothing") },
  },
  {
    text = "Shake the coconut tree.",
    actions = { Action.ModelHighlight:new(coconutTree) },
    postconditions = {},
  },
  {
    text = "Pick up the coconut.",
    actions = { Action.ModelHighlight:new(coconut) },
    postconditions = { Condition.InventoryContains:new(coconut) },
  },
  {
    text = "Use the coconut on Shantay.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["shantay"]),
      Action.InventoryHighlight:new(coconut),
    },
    postconditions = { Condition.InventoryContains:new(emptyCoconutShell) },
  },
  {
    text = "Click the empty shell to add a hell-rat to it.",
    actions = { Action.InventoryHighlight:new(emptyCoconutShell) },
    postconditions = { Condition.ConversationText:new("high enough") },
  },
  {
    text = "Climb up the rope on the northeastern wall.",
    actions = { Action.Direction:new(4.45, 600, -0.7, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Throw the Shell-rat.",
    actions = { Action.InventoryHighlight:new(shellRat) },
    postconditions = { Condition.ChatText:new("smuggled 1/3") },
  },
  {
    text = "Climb back down.",
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Try to open the Jail door.",
    actions = { Action.Direction:new(-8.5, 604, -6, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(-10, -64, -6, 1, true) },
  },
  {
    text = "Talk to the shady man to receive a jewel.",
    actions = { Action.ModelHighlight:new(shadyMan) },
    postconditions = { Condition.ConversationText:new("no problem") },
  },
  {
    text = "Search the barrel inside of the jail cell to take one.",
    actions = { Action.ModelHighlight:new(jailBarrel) },
    postconditions = { Condition.InventoryContains:new(barrel) },
  },
  {
    text = "Talk to the border guards to exchange the jewel for a rope.",
    actions = { Action.ModelHighlight:new(guard) },
    postconditions = { Condition.InventoryContains:new(Models.items["rope"]) },
  },
  {
    text = "Use the rope on the coconut tree.",
    actions = {
      Action.ModelHighlight:new(coconutTree),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.ChatText:new("smuggled 2/3") },
  },
  {
    text = "Click the barrel in your inventory to load the last hell-rat into it.",
    actions = { Action.InventoryHighlight:new(barrel) },
    postconditions = { Condition.ConversationText:new("echo-echo-echo") },
  },
  {
    text = "Use the old rug (you got from the bank earlier) on the barrel.",
    actions = {
      Action.InventoryHighlight:new(oldRug),
      Action.InventoryHighlight:new(barrel),
    },
    postconditions = { Condition.ConversationText:new("test") },
  },
  {
    text = "Roll the barrel past the Shantay pass.",
    actions = {
      Action.Direction:new(-3, -64, -10, { instance = true, tile = true }),
      Action.InventoryHighlight:new(carpetBarrel),
    },
    postconditions = { Condition.ChatText:new("smuggled 3/3") },
  },
  {
    text = "Talk to Shantay.",
    actions = { Action.ModelHighlight:new(Models.npcs["shantay"]) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the hole that has a slight twinkle to it.",
    title = "SECRET TUNNEL",
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Enter the sparkling holes 4 times.<ul><li>You can also hover your mouse over the holes until you spot the hole with a little rat icon next to it.</li></ul>",
    postconditions = { Condition.ConversationText:new("this is it") },
  },
  {
    text = "Attempt to enter the mysterious entrance as a hell-rat.",
    actions = { Action.ModelHighlight:new(mysteriousEntrance) },
    postconditions = { Condition.ConversationText:new("secret meeting must") },
  },
  {
    text = "Enter the mysterious entrance.",
    actions = { Action.ModelHighlight:new(mysteriousEntrance) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("Magical support") },
  },
  {
    text = "Grab Dave from the empty cooking range south-east.",
    actions = { Action.ModelHighlight:new(cookingRange) },
    postconditions = { Condition.ConversationText:new("talk about it") },
  },
  {
    text = "Select the Clean Bed option.",
    actions = { Action.ModelHighlight:new(bed) },
    postconditions = { Condition.ConversationText:new("Doom Squad") },
  },
  {
    text = "Gather 5 hell-rats.<ul><li>If cutscene does not begin, gather the sixth hell-rat.</li></ul>",
    actions = { Action.ModelHighlight:new(hellrat) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("BLOOD") } },
  { postconditions = { Condition.ChangedInstance:new() } },
  {
    text = "Talk to Doris.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(3079, 1669, 3493, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["doris"], { distance = 6 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Evil Dave's Big Day Out",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1511740800,
  prereqQuests = { "Recipe for Disaster: Freeing Evil Dave" },
  questReqs = {
    Types.QuestReq.skill("Agility", 30),
    Types.QuestReq.skill("Cooking", 30),
    Types.QuestReq.skill("Herblore", 30),
    Types.QuestReq.skill("Magic", 30),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
