local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local legendsGuildGuard = Model.new(4593, {
  [2587] = Vertex.new(-2, 725, -59, 108, 79, 56),
  [2595] = Vertex.new(2, 725, -59, 108, 79, 56),
  [3914] = Vertex.new(-25, 758, -32, 99, 100, 130),
  [3927] = Vertex.new(4, 726, -67, 99, 100, 130),
  [3932] = Vertex.new(-4, 726, -67, 99, 100, 130),
})
local radimusErkle = Model.new(3804, {
  [175] = Vertex.new(60, 684, 32, 140, 129, 128),
  [178] = Vertex.new(60, 684, 32, 140, 129, 128),
  [181] = Vertex.new(-60, 684, 32, 140, 129, 128),
  [561] = Vertex.new(0, 692, -36, 138, 102, 71),
  [921] = Vertex.new(0, 628, -84, 147, 136, 135),
})
local jungleForester = Model.any({
  Model.new(5433, { --male
    [2791] = Vertex.new(-2, 725, -59, 108, 79, 56),
    [2799] = Vertex.new(2, 725, -59, 108, 79, 56),
    [2801] = Vertex.new(7, 724, -51, 108, 79, 56),
    [4842] = Vertex.new(-65, 670, 50, 55, 44, 11),
    [4873] = Vertex.new(65, 670, 50, 55, 44, 11),
  }),
  Model.new(4725, { --female
    [2565] = Vertex.new(24, 738, -38, 31, 30, 28),
    [2589] = Vertex.new(-24, 738, -38, 31, 30, 28),
    [2611] = Vertex.new(-2, 716, -57, 108, 79, 56),
    [2617] = Vertex.new(2, 716, -57, 108, 79, 56),
    [2621] = Vertex.new(6, 716, -52, 108, 79, 56),
  }),
})
local gujuo = Model.new(3567, {
  [2272] = Vertex.new(-2, 725, -59, 53, 38, 4),
  [2277] = Vertex.new(-7, 724, -51, 53, 38, 4),
  [2280] = Vertex.new(2, 725, -59, 53, 38, 4),
  [2282] = Vertex.new(7, 724, -51, 53, 38, 4),
  [3001] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local ungadulu = Model.new(4005, {
  [2335] = Vertex.new(2, 725, -59, 53, 38, 4),
  [2339] = Vertex.new(7, 724, -51, 53, 38, 4),
  [2353] = Vertex.new(-2, 725, -59, 53, 38, 4),
  [2358] = Vertex.new(-7, 724, -51, 53, 38, 4),
  [3545] = Vertex.new(40, 711, -12, 124, 114, 114),
})
local nezikchened = Model.new(5247, {
  [5106] = Vertex.new(964, 1534, 120, 77, 71, 71),
  [5108] = Vertex.new(964, 1534, 120, 77, 71, 71),
  [5124] = Vertex.new(-964, 1534, 120, 77, 71, 71),
  [5154] = Vertex.new(875, 1528, 120, 132, 121, 121),
  [5168] = Vertex.new(-875, 1528, 120, 132, 121, 121),
})
local sanTojalon = Model.new(4107, {
  [2245] = Vertex.new(161, 587, 56, 102, 93, 78, 0.2941),
  [4086] = Vertex.new(224, 578, -18, 144, 137, 132),
  [4088] = Vertex.new(220, 589, -30, 144, 137, 132),
  [4098] = Vertex.new(226, 571, 57, 144, 137, 132),
  [4100] = Vertex.new(221, 582, 46, 144, 137, 132),
})
local ranalphDevere = Model.new(4245, {
  [230] = Vertex.new(3, 729, 62, 118, 108, 91, 0.2941),
  [252] = Vertex.new(-2, 729, 62, 118, 108, 91, 0.2941),
  [923] = Vertex.new(2, 729, 61, 51, 47, 39, 0.2941),
  [2365] = Vertex.new(161, 587, 56, 102, 93, 78, 0.2941),
  [2368] = Vertex.new(-161, 587, 56, 102, 93, 78, 0.2941),
})
local irvigSenay = Model.new(4023, {
  [2026] = Vertex.new(0, 862, -27, 94, 86, 72, 0.2941),
  [4002] = Vertex.new(224, 578, -18, 144, 137, 132),
  [4004] = Vertex.new(220, 589, -30, 144, 137, 132),
  [4014] = Vertex.new(226, 571, 57, 144, 137, 132),
  [4016] = Vertex.new(221, 582, 46, 144, 137, 132),
})
local viyeldi = Model.new(2820, {
  [121] = Vertex.new(32, 734, -18, 89, 81, 68),
  [148] = Vertex.new(-31, 734, -18, 89, 81, 68),
  [551] = Vertex.new(32, 734, -18, 89, 81, 68),
  [1682] = Vertex.new(-12, 678, 5, 8, 55, 90),
  [2213] = Vertex.new(144, 416, 4, 8, 55, 90),
})
--#endregion
--#region Objects
local mossyRock = Model.new(504, {
  [105] = Vertex.new(7448, 2592, 4732, 59, 62, 57),
  [107] = Vertex.new(7364, 2592, 4836, 59, 62, 57),
  [108] = Vertex.new(7448, 2592, 4732, 59, 62, 57),
  [186] = Vertex.new(6900, 2592, 4620, 31, 66, 27),
  [341] = Vertex.new(7052, 2592, 4396, 31, 66, 27),
})
local caveExit = Model.new(1179, {
  [103] = Vertex.new(1536, 2398, 7412, 63, 45, 5),
  [113] = Vertex.new(1536, 960, 8192, 63, 45, 5),
  [153] = Vertex.new(3072, 960, 8192, 63, 45, 5),
  [431] = Vertex.new(3072, 1485, 7272, 63, 45, 5),
  [485] = Vertex.new(3072, 1719, 7420, 88, 70, 36),
})
local ancientGate = Model.new(1476, {
  [54] = Vertex.new(5392, 1980, 1196, 85, 81, 78),
  [96] = Vertex.new(5288, 1980, 1196, 85, 81, 78),
  [138] = Vertex.new(5180, 1980, 1196, 85, 81, 78),
  [794] = Vertex.new(4952, 1980, 1196, 85, 81, 78),
  [836] = Vertex.new(5060, 1980, 1196, 85, 81, 78),
})
local reeds = Model.new(4920, {
  [2857] = Vertex.new(3201, 636, 2901, 127, 127, 127),
  [3170] = Vertex.new(3201, 633, 2902, 127, 127, 127),
  [3173] = Vertex.new(2812, 918, 3002, 127, 127, 127),
  [3196] = Vertex.new(2812, 917, 3004, 127, 127, 127),
  [4510] = Vertex.new(2894, 933, 2863, 127, 127, 127),
})
local filledPond = Model.new(486, {
  [3] = Vertex.new(2807, 458, 1823, 128, 128, 128),
  [15] = Vertex.new(3614, 458, 2068, 128, 128, 128),
  [18] = Vertex.new(3606, 458, 1674, 128, 128, 128),
  [153] = Vertex.new(3576, 448, 1980, 72, 67, 66),
  [450] = Vertex.new(3216, 448, 2484, 72, 67, 66),
})
local winchWithRope = Model.new(5790, {
  [754] = Vertex.new(-1024, 180, -1280, 62, 49, 25),
  [755] = Vertex.new(-1024, 116, -768, 62, 49, 25),
  [759] = Vertex.new(-1024, 180, -1280, 62, 49, 25),
  [2349] = Vertex.new(-32, 1254, -708, 90, 74, 46),
  [2391] = Vertex.new(64, 1136, 932, 90, 74, 46),
})
local heartInWall = Model.new(288, {
  [45] = Vertex.new(-84, 480, 96, 154, 13, 59, 0.9373),
  [74] = Vertex.new(-100, 481, 80, 154, 13, 59, 0.9373),
  [77] = Vertex.new(-100, 481, 80, 154, 13, 59, 0.9373),
  [141] = Vertex.new(-100, 481, 80, 154, 13, 59, 0.9373),
  [228] = Vertex.new(-44, 468, 24, 42, 30, 3, 0.9373),
})
local blueWizardHat = Model.new(159, {
  [3] = Vertex.new(-44, 12, 44, 8, 55, 90),
  [11] = Vertex.new(-76, 16, -88, 8, 55, 90),
  [14] = Vertex.new(-56, 24, -104, 8, 55, 90),
  [26] = Vertex.new(80, 28, -64, 8, 55, 90),
  [38] = Vertex.new(40, 12, 48, 8, 55, 90),
})
local sacredSpring = Model.new(1842, {
  [165] = Vertex.new(1221924, 271, 2401034, 183, 219, 238, 0.1882),
  [201] = Vertex.new(1221896, 293, 2401051, 224, 239, 247, 0.4157),
  [212] = Vertex.new(1221796, 370, 2401085, 184, 220, 238, 0.2039),
  [213] = Vertex.new(1221796, 370, 2401085, 184, 220, 238, 0.2039),
  [216] = Vertex.new(1221796, 370, 2401085, 184, 220, 238, 0.2039),
})
local sproutTree = Model.new(162, {
  [1] = Vertex.new(-180, 32, -180, 54, 66, 27),
  [13] = Vertex.new(112, 32, 220, 54, 66, 27),
  [16] = Vertex.new(132, 32, -212, 54, 66, 27),
  [145] = Vertex.new(132, 160, 64, 54, 66, 27),
  [154] = Vertex.new(-128, 160, 88, 54, 66, 27),
})
local partiallyGrownTree = Model.new(222, {
  [17] = Vertex.new(304, 578, 0, 128, 128, 128),
  [131] = Vertex.new(284, 558, -198, 128, 128, 128),
  [161] = Vertex.new(-244, 558, -234, 128, 128, 128),
  [191] = Vertex.new(-288, 558, 198, 128, 128, 128),
  [221] = Vertex.new(240, 558, 234, 128, 128, 128),
})
local yommiTree = Model.new(222, {
  [17] = Vertex.new(608, 1156, 0, 128, 128, 128),
  [131] = Vertex.new(568, 1116, -396, 128, 128, 128),
  [161] = Vertex.new(-488, 1116, -468, 128, 128, 128),
  [191] = Vertex.new(-576, 1116, 396, 128, 128, 128),
  [221] = Vertex.new(480, 1116, 468, 128, 128, 128),
})
local fallenYommiTree = Model.new(1236, {
  [102] = Vertex.new(-612, 12, 44, 81, 74, 62),
  [542] = Vertex.new(-408, 12, 192, 81, 74, 62),
  [623] = Vertex.new(596, 12, 52, 81, 74, 62),
  [1095] = Vertex.new(152, 200, 316, 81, 74, 62),
  [1152] = Vertex.new(168, 188, 316, 81, 74, 62),
})
local partiallyCarvedYommiTree = Model.new(324, {
  [29] = Vertex.new(-340, 44, 312, 81, 74, 62),
  [32] = Vertex.new(-340, 44, 312, 81, 74, 62),
  [79] = Vertex.new(332, 60, 296, 81, 74, 62),
  [273] = Vertex.new(-340, 44, 312, 95, 89, 61),
  [287] = Vertex.new(-344, 60, 264, 95, 89, 61),
})
local carvedYommiTree = Model.new(1392, {
  [360] = Vertex.new(-60, 52, -384, 55, 50, 28),
  [431] = Vertex.new(-24, 92, -316, 162, 149, 148),
  [792] = Vertex.new(-40, 56, -336, 55, 50, 28),
  [803] = Vertex.new(-24, 72, -384, 55, 50, 28),
  [935] = Vertex.new(-44, 60, -336, 55, 50, 28),
})
local oldTotem = Model.new(1494, {
  [73] = Vertex.new(-44, 1088, 24, 66, 52, 27),
  [224] = Vertex.new(212, 1088, -32, 47, 22, 4),
  [795] = Vertex.new(212, 1088, -32, 66, 52, 27),
  [849] = Vertex.new(-200, 1044, -44, 66, 52, 27),
  [999] = Vertex.new(212, 1088, -32, 66, 52, 27),
})
local newTotem = Model.new(1494, {
  [11] = Vertex.new(-200, 1044, -44, 128, 127, 117),
  [73] = Vertex.new(-44, 1088, 24, 85, 67, 34),
  [224] = Vertex.new(212, 1088, -32, 128, 127, 117),
  [795] = Vertex.new(212, 1088, -32, 85, 67, 34),
  [999] = Vertex.new(212, 1088, -32, 85, 67, 34),
})
--#endregion
--#region Items
local enchantedVial = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [2] = Vertex.new(4, 112, -12, 114, 102, 47),
    [3] = Vertex.new(4, 96, -12, 114, 102, 47),
    [4] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [7] = Vertex.new(-12, 96, -4, 114, 102, 47),
    [9] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [11] = Vertex.new(-12, 112, -4, 114, 102, 47),
    [23] = Vertex.new(-4, 112, 12, 114, 102, 47),
    [31] = Vertex.new(12, 96, 4, 114, 102, 47),
    [35] = Vertex.new(12, 112, 4, 114, 102, 47),
  }),
  Model.new(1266, {
    [1] = Vertex.new(32, -8, 4, 96, 89, 88, 0.6863),
    [2] = Vertex.new(4, -8, -36, 96, 89, 88, 0.6863),
    [3] = Vertex.new(32, -8, -4, 96, 89, 88, 0.6863),
    [5] = Vertex.new(-4, -8, -36, 96, 89, 88, 0.6863),
    [8] = Vertex.new(-32, -8, -4, 96, 89, 88, 0.6863),
    [11] = Vertex.new(-32, -8, 4, 96, 89, 88, 0.6863),
    [14] = Vertex.new(-4, -8, 36, 96, 89, 88, 0.6863),
    [17] = Vertex.new(4, -8, 36, 96, 89, 88, 0.6863),
    [19] = Vertex.new(60, 128, -8, 84, 102, 163, 0.4980),
    [20] = Vertex.new(56, 124, -8, 84, 102, 163, 0.4980),
    [21] = Vertex.new(44, 128, -8, 84, 102, 163, 0.4980),
    [24] = Vertex.new(56, 132, -8, 84, 102, 163, 0.4980),
    [27] = Vertex.new(60, 112, -8, 84, 102, 163, 0.4980),
    [30] = Vertex.new(60, 144, -8, 84, 102, 163, 0.4980),
    [1028] = Vertex.new(-12, 84, -4, 135, 136, 147, 0.4980),
    [1029] = Vertex.new(-16, 68, -4, 135, 136, 147, 0.4980),
    [1031] = Vertex.new(-12, 84, 4, 135, 136, 147, 0.4980),
    [1035] = Vertex.new(-4, 84, -12, 135, 136, 147, 0.4980),
    [1171] = Vertex.new(-40, 12, 12, 96, 89, 88, 0.8745),
    [1172] = Vertex.new(-16, 68, 4, 96, 89, 88, 0.8745),
  }),
})
local holyWater = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [2] = Vertex.new(4, 112, -12, 114, 102, 47),
    [3] = Vertex.new(4, 96, -12, 114, 102, 47),
    [4] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [7] = Vertex.new(-12, 96, -4, 114, 102, 47),
    [9] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [11] = Vertex.new(-12, 112, -4, 114, 102, 47),
    [23] = Vertex.new(-4, 112, 12, 114, 102, 47),
    [31] = Vertex.new(12, 96, 4, 114, 102, 47),
    [35] = Vertex.new(12, 112, 4, 114, 102, 47),
  }),
  Model.new(1266, {
    [1] = Vertex.new(32, -8, 4, 12, 16, 136, 0.6863),
    [2] = Vertex.new(4, -8, -36, 12, 16, 136, 0.6863),
    [3] = Vertex.new(32, -8, -4, 12, 16, 136, 0.6863),
    [5] = Vertex.new(-4, -8, -36, 12, 16, 136, 0.6863),
    [8] = Vertex.new(-32, -8, -4, 12, 16, 136, 0.6863),
    [11] = Vertex.new(-32, -8, 4, 12, 16, 136, 0.6863),
    [14] = Vertex.new(-4, -8, 36, 12, 16, 136, 0.6863),
    [17] = Vertex.new(4, -8, 36, 12, 16, 136, 0.6863),
    [19] = Vertex.new(60, 128, -8, 84, 102, 163, 0.4980),
    [20] = Vertex.new(56, 124, -8, 84, 102, 163, 0.4980),
    [21] = Vertex.new(44, 128, -8, 84, 102, 163, 0.4980),
    [24] = Vertex.new(56, 132, -8, 84, 102, 163, 0.4980),
    [27] = Vertex.new(60, 112, -8, 84, 102, 163, 0.4980),
    [30] = Vertex.new(60, 144, -8, 84, 102, 163, 0.4980),
    [1028] = Vertex.new(-12, 84, -4, 135, 136, 147, 0.4980),
    [1029] = Vertex.new(-16, 68, -4, 135, 136, 147, 0.4980),
    [1031] = Vertex.new(-12, 84, 4, 135, 136, 147, 0.4980),
    [1035] = Vertex.new(-4, 84, -12, 135, 136, 147, 0.4980),
    [1171] = Vertex.new(-40, 12, 12, 12, 16, 136, 0.8745),
    [1172] = Vertex.new(-16, 68, 4, 12, 16, 136, 0.8745),
  }),
})
--#endregion
--#region Quest Items
local radimusNotes = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 119, 130, 155),
  [5] = Vertex.new(-72, 0, -56, 119, 130, 155),
  [187] = Vertex.new(-84, 28, -84, 103, 17, 9),
  [190] = Vertex.new(-84, 28, -84, 103, 17, 9),
  [195] = Vertex.new(-84, 28, -84, 103, 17, 9),
})
local bullRoarer = Model.new(264, {
  [2] = Vertex.new(96, 0, 100, 129, 112, 82),
  [66] = Vertex.new(104, 0, 96, 129, 112, 82),
  [68] = Vertex.new(104, 0, 96, 129, 112, 82),
  [90] = Vertex.new(-72, 16, -36, 129, 112, 82),
  [129] = Vertex.new(128, 0, 144, 96, 89, 88),
})
local bindingBook = Model.new(312, {
  [207] = Vertex.new(-48, 56, 88, 116, 123, 151),
  [209] = Vertex.new(-36, 56, 100, 116, 123, 151),
  [215] = Vertex.new(48, 56, 100, 116, 123, 151),
  [279] = Vertex.new(-48, 56, -96, 116, 123, 151),
  [309] = Vertex.new(76, 56, -60, 116, 123, 151),
})
local goldenBowl = Model.new(288, {
  [10] = Vertex.new(-16, 4, 40, 177, 151, 16),
  [19] = Vertex.new(52, 4, 4, 177, 151, 16),
  [21] = Vertex.new(84, 100, -48, 177, 151, 16),
  [25] = Vertex.new(40, 4, -28, 177, 151, 16),
  [27] = Vertex.new(48, 100, -84, 177, 151, 16),
  [43] = Vertex.new(12, 4, -44, 177, 151, 16),
  [46] = Vertex.new(24, 4, 36, 177, 151, 16),
  [99] = Vertex.new(108, 68, 0, 103, 73, 9),
  [105] = Vertex.new(92, 68, -52, 103, 73, 9),
  [171] = Vertex.new(92, 100, -52, 177, 151, 16),
  [223] = Vertex.new(84, 100, 48, 111, 94, 45),
  [224] = Vertex.new(92, 100, 52, 111, 94, 45),
  [227] = Vertex.new(52, 100, 92, 111, 94, 45),
  [228] = Vertex.new(48, 100, 84, 111, 94, 45),
  [230] = Vertex.new(108, 100, 0, 111, 94, 45),
  [233] = Vertex.new(0, 100, 108, 111, 94, 45),
  [240] = Vertex.new(0, 100, 100, 111, 94, 45),
  [245] = Vertex.new(-52, 100, 92, 111, 94, 45),
  [250] = Vertex.new(-84, 100, 48, 111, 94, 45),
  [252] = Vertex.new(-48, 100, 84, 111, 94, 45),
})
local filledGoldenBowl = Model.new(324, {
  [21] = Vertex.new(-80, 72, 44, 32, 156, 151),
  [27] = Vertex.new(-96, 72, 0, 32, 156, 151),
  [171] = Vertex.new(-16, 0, 40, 103, 73, 9),
  [227] = Vertex.new(-92, 64, -52, 177, 151, 16),
  [245] = Vertex.new(52, 64, -92, 177, 151, 16),
  [251] = Vertex.new(92, 64, -52, 177, 151, 16),
  [289] = Vertex.new(84, 96, -48, 111, 94, 45),
  [290] = Vertex.new(52, 96, -92, 111, 94, 45),
  [291] = Vertex.new(48, 96, -84, 111, 94, 45),
  [292] = Vertex.new(-84, 96, 48, 111, 94, 45),
  [293] = Vertex.new(-92, 96, 52, 111, 94, 45),
  [294] = Vertex.new(-52, 96, 92, 111, 94, 45),
  [297] = Vertex.new(0, 96, -108, 111, 94, 45),
  [298] = Vertex.new(-100, 96, 0, 111, 94, 45),
  [303] = Vertex.new(0, 96, -100, 111, 94, 45),
  [305] = Vertex.new(-108, 96, 0, 111, 94, 45),
  [309] = Vertex.new(-52, 96, -92, 111, 94, 45),
  [310] = Vertex.new(-84, 96, -48, 111, 94, 45),
  [315] = Vertex.new(-48, 96, -84, 111, 94, 45),
  [317] = Vertex.new(-92, 96, -52, 111, 94, 45),
})
local hollowReed = Model.new(540, {
  [259] = Vertex.new(-232, 32, -232, 30, 74, 31),
  [430] = Vertex.new(-232, 32, -232, 30, 74, 31),
  [434] = Vertex.new(-216, 32, -248, 30, 74, 31),
  [435] = Vertex.new(-232, 32, -232, 30, 74, 31),
  [444] = Vertex.new(-216, 32, -248, 30, 74, 31),
})
local yommiTreeSeeds = Model.new(528, {
  [52] = Vertex.new(-120, 84, -88, 98, 84, 8),
  [55] = Vertex.new(-120, 84, -88, 98, 84, 8),
  [345] = Vertex.new(-68, 16, -92, 8, 94, 11),
  [355] = Vertex.new(-68, 16, -92, 8, 94, 11),
  [361] = Vertex.new(-68, 16, -92, 8, 94, 11),
})
local germinatedYommiTreeSeeds = Model.new(528, {
  [52] = Vertex.new(-120, 84, -88, 98, 84, 8),
  [55] = Vertex.new(-120, 84, -88, 98, 84, 8),
  [345] = Vertex.new(-68, 16, -92, 90, 154, 13),
  [355] = Vertex.new(-68, 16, -92, 90, 154, 13),
  [361] = Vertex.new(-68, 16, -92, 90, 154, 13),
})
local braveryPotion = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [2] = Vertex.new(4, 112, -12, 114, 102, 47),
    [3] = Vertex.new(4, 96, -12, 114, 102, 47),
    [4] = Vertex.new(-4, 96, -12, 114, 102, 47),
    [7] = Vertex.new(4, 96, 12, 114, 102, 47),
    [8] = Vertex.new(-4, 112, 12, 114, 102, 47),
    [9] = Vertex.new(-4, 96, 12, 114, 102, 47),
    [13] = Vertex.new(-12, 96, -4, 114, 102, 47),
    [17] = Vertex.new(-12, 112, -4, 114, 102, 47),
    [36] = Vertex.new(12, 96, -4, 114, 102, 47),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 135, 136, 147, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 135, 136, 147, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 135, 136, 147, 0.4980),
    [5] = Vertex.new(4, 96, 20, 135, 136, 147, 0.4980),
    [7] = Vertex.new(20, 84, 4, 135, 136, 147, 0.4980),
    [11] = Vertex.new(20, 96, 4, 135, 136, 147, 0.4980),
    [13] = Vertex.new(20, 84, -4, 135, 136, 147, 0.4980),
    [17] = Vertex.new(20, 96, -4, 135, 136, 147, 0.4980),
    [19] = Vertex.new(4, 84, -20, 135, 136, 147, 0.4980),
    [23] = Vertex.new(4, 96, -20, 135, 136, 147, 0.4980),
    [49] = Vertex.new(-4, 68, 16, 82, 46, 7, 0.8745),
    [50] = Vertex.new(-4, 84, 12, 82, 46, 7, 0.8745),
    [51] = Vertex.new(-12, 84, 4, 82, 46, 7, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 82, 46, 7, 0.8745),
    [55] = Vertex.new(-12, 12, 40, 82, 46, 7, 0.8745),
    [60] = Vertex.new(-12, 84, -4, 82, 46, 7, 0.8745),
    [63] = Vertex.new(-40, 12, 12, 82, 46, 7, 0.8745),
    [64] = Vertex.new(-4, 0, 36, 82, 46, 7, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 82, 46, 7, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 82, 46, 7, 0.8745),
  }),
})
local crystalChunk = Model.new(144, {
  [23] = Vertex.new(-36, 92, 12, 103, 17, 9, 0.7490),
  [27] = Vertex.new(-36, 92, 12, 103, 17, 9, 0.7490),
  [56] = Vertex.new(-12, 60, 36, 103, 17, 9, 0.7490),
  [69] = Vertex.new(-12, 60, 36, 103, 17, 9, 0.7490),
  [74] = Vertex.new(-12, 60, 36, 103, 17, 9, 0.7490),
})
local crystalHunk = Model.new(144, {
  [24] = Vertex.new(44, 100, 48, 103, 17, 9, 0.7490),
  [25] = Vertex.new(44, 100, 48, 103, 17, 9, 0.7490),
  [43] = Vertex.new(44, 60, 48, 103, 17, 9, 0.7490),
  [63] = Vertex.new(44, 60, 48, 103, 17, 9, 0.7490),
  [67] = Vertex.new(44, 60, 48, 103, 17, 9, 0.7490),
})
local crystalLump = Model.new(108, {
  [2] = Vertex.new(-8, 72, 40, 103, 17, 9, 0.7490),
  [9] = Vertex.new(-36, 76, 44, 103, 17, 9, 0.7490),
  [11] = Vertex.new(-36, 76, 44, 103, 17, 9, 0.7490),
  [17] = Vertex.new(-36, 76, 44, 103, 17, 9, 0.7490),
  [20] = Vertex.new(-36, 76, 44, 103, 17, 9, 0.7490),
})
local heartCrystal = Model.new(288, {
  [1] = Vertex.new(4, 100, -16, 103, 17, 9, 0.7490),
  [2] = Vertex.new(16, 76, -36, 103, 17, 9, 0.7490),
  [3] = Vertex.new(4, 76, -36, 103, 17, 9, 0.7490),
  [6] = Vertex.new(-8, 72, -16, 103, 17, 9, 0.7490),
  [8] = Vertex.new(-8, 56, -32, 103, 17, 9, 0.7490),
  [13] = Vertex.new(24, 100, -16, 103, 17, 9, 0.7490),
  [16] = Vertex.new(36, 72, -28, 103, 17, 9, 0.7490),
  [22] = Vertex.new(-8, 80, 4, 103, 17, 9, 0.7490),
  [25] = Vertex.new(4, 108, 4, 103, 17, 9, 0.7490),
  [31] = Vertex.new(44, 72, -12, 103, 17, 9, 0.7490),
  [34] = Vertex.new(24, 108, 4, 103, 17, 9, 0.7490),
  [41] = Vertex.new(28, 36, -8, 103, 17, 9, 0.7490),
  [46] = Vertex.new(44, 72, 4, 103, 17, 9, 0.7490),
  [49] = Vertex.new(28, 36, 0, 103, 17, 9, 0.7490),
  [55] = Vertex.new(4, 104, 24, 103, 17, 9, 0.7490),
  [59] = Vertex.new(-8, 0, -4, 103, 17, 9, 0.7490),
  [64] = Vertex.new(24, 104, 24, 103, 17, 9, 0.7490),
  [70] = Vertex.new(28, 36, 4, 103, 17, 9, 0.7490),
  [75] = Vertex.new(-8, 76, 24, 103, 17, 9, 0.7490),
  [79] = Vertex.new(44, 72, 20, 103, 17, 9, 0.7490),
})
local chargedHeartCrystal = Model.new(288, {
  [1] = Vertex.new(4, 100, -16, 154, 13, 59, 0.9373),
  [2] = Vertex.new(16, 76, -36, 154, 13, 59, 0.9373),
  [3] = Vertex.new(4, 76, -36, 154, 13, 59, 0.9373),
  [6] = Vertex.new(-8, 72, -16, 154, 13, 59, 0.9373),
  [8] = Vertex.new(-8, 56, -32, 154, 13, 59, 0.9373),
  [13] = Vertex.new(24, 100, -16, 154, 13, 59, 0.9373),
  [16] = Vertex.new(36, 72, -28, 154, 13, 59, 0.9373),
  [22] = Vertex.new(-8, 80, 4, 154, 13, 59, 0.9373),
  [25] = Vertex.new(4, 108, 4, 154, 13, 59, 0.9373),
  [31] = Vertex.new(44, 72, -12, 154, 13, 59, 0.9373),
  [34] = Vertex.new(24, 108, 4, 154, 13, 59, 0.9373),
  [41] = Vertex.new(28, 36, -8, 154, 13, 59, 0.9373),
  [46] = Vertex.new(44, 72, 4, 154, 13, 59, 0.9373),
  [49] = Vertex.new(28, 36, 0, 154, 13, 59, 0.9373),
  [55] = Vertex.new(4, 104, 24, 154, 13, 59, 0.9373),
  [59] = Vertex.new(-8, 0, -4, 154, 13, 59, 0.9373),
  [64] = Vertex.new(24, 104, 24, 154, 13, 59, 0.9373),
  [70] = Vertex.new(28, 36, 4, 154, 13, 59, 0.9373),
  [75] = Vertex.new(-8, 76, 24, 154, 13, 59, 0.9373),
  [79] = Vertex.new(44, 72, 20, 154, 13, 59, 0.9373),
})
local darkDagger = Models.items["beer"] --whoops. we needed this
local yommiTotem = Model.new(1398, {
  [91] = Vertex.new(4, 828, -72, 162, 149, 148),
  [1026] = Vertex.new(52, 844, 4, 53, 49, 4),
  [1056] = Vertex.new(-56, 840, 12, 53, 49, 4),
  [1067] = Vertex.new(168, 888, 12, 81, 74, 62),
  [1340] = Vertex.new(-8, 824, -72, 162, 149, 148),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to a Legends' guard at the Legends' Guild east of the Ardougne lodestone.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(2728, 2245, 3349, { distance = 20 }),
      Action.ModelHighlight:new(legendsGuildGuard, { distance = 20 }),
      Action.ConversationHighlight:new("Can I speak to someone in charge?"),
      Action.ConversationHighlight:new("Can I go on the quest?"),
      Action.ConversationHighlight:new("Yes, I'd like to talk to Grand Vizier Erkle."),
    },
    postconditions = { Condition.ConversationText:new("Good luck") },
  },
  {
    text = "Talk to Radimus Erkle.",
    actions = {
      Action.Direction:new(2725, 2245, 3368, { distance = 20 }),
      Action.ModelHighlight:new(radimusErkle, { distance = 20 }),
      Action.ConversationHighlight:new("Yes actually, what's involved?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Radimus Erkle.",
    actions = { Action.ModelHighlight:new(radimusErkle) },
    postconditions = { Condition.ConversationText:new("very good luck to you") },
  },
  {
    text = "Take 3 papyrus from Radimus' table.<ul><li>You can buy papyrus from the general store next to the Karamja lodestone too.</li></ul>",
    actions = { Action.ModelHighlight:new(Models.items["papyrus"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["papyrus"], 3) },
  },
  {
    text = "Chop-down the Jungle Trees/Bushes south of Shilo Village (fairy ring CKR) to access the Kharazi Jungle.",
    title = "Kharazi Jungle",
    neededItems = {
      ["Radimus notes"] = { quantity = 1 },
      ["Papyrus"] = { quantity = 3 },
      ["Charcoal"] = { quantity = 3 },
    },
    actions = { Action.Direction:new(2796, 853, 2940) },
    postconditions = {
      Condition.DistanceTo:new(2796, 1157, 2937, 2), -- west
      Condition.DistanceTo:new(2866, 1093, 2930, 4), -- center
      Condition.DistanceTo:new(2936, 589, 2933, 4), -- east
      Condition.DistanceTo:new(2902, 1445, 2931, 2), -- fairy ring
    },
  },
  {
    text = "Stand on the marked tile and right click complete the Radimus notes.",
    actions = {
      Action.Direction:new(2904, 1477, 2931, { tile = true }),
      Action.InventoryHighlight:new(radimusNotes, true),
    },
    postconditions = { Condition.ConversationText:new("Eastern Kharazi Jungle - *** Com") }, --doesn't work
  },
  {
    text = "Repeat in the centre of the jungle.<ul><li>Optional: Repair fairy ring CJS with five bittercap mushrooms to make returning to the jungle easier.</li></ul>",
    actions = {
      Action.Direction:new(2862, 1629, 2932, { tile = true }),
      Action.InventoryHighlight:new(radimusNotes, true),
    },
    postconditions = { Condition.ConversationText:new("Middle Kharazi Jungle - *** Com") }, --doesn't work
  },
  {
    text = "Repeat again on the eastern edge of the jungle.",
    actions = {
      Action.Direction:new(2787, 1149, 2924, { tile = true }),
      Action.InventoryHighlight:new(radimusNotes, true),
    },
    postconditions = {
      Condition.ConversationText:new("Western Kharazi Jungle - *** Com"), --doesn't work
      Condition.ConversationText:new("Well done"),
    },
  },
  {
    text = "Use the notes on a jungle forester, south of Shilo Village (fairy ring CKR).",
    actions = {
      Action.Direction:new(2794, 773, 2945, { distance = 20 }),
      Action.ModelHighlight:new(jungleForester, { distance = 20 }),
      Action.InventoryHighlight:new(radimusNotes),
      Action.ConversationHighlight:new("Yes, go ahead make a copy!"),
    },
    postconditions = { Condition.InventoryContains:new(bullRoarer) },
  },
  {
    text = "Return to Kharazi Jungle (fairy ring CJS).",
    title = "Cave",
    neededItems = {
      ["Radimus notes (complete)"] = { quantity = 1 },
      ["Bull roarer"] = { quantity = 1 },
      ["Lockpick"] = { quantity = 1 },
      ["Cut opal"] = { quantity = 1 },
      ["Cut jade"] = { quantity = 1 },
      ["Cut red topaz"] = { quantity = 1 },
      ["Cut sapphire"] = { quantity = 1 },
      ["Cut emerald"] = { quantity = 1 },
      ["Cut ruby"] = { quantity = 1 },
      ["Cut diamond"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2796, 853, 2940) },
    postconditions = {
      Condition.DistanceTo:new(2796, 1157, 2937, 2), -- west
      Condition.DistanceTo:new(2866, 1093, 2930, 4), -- center
      Condition.DistanceTo:new(2936, 589, 2933, 4), -- east
      Condition.DistanceTo:new(2902, 1445, 2931, 2), -- fairy ring
    },
  },
  {
    text = "Swing the bull roarer until Gujuo appears.",
    actions = { Action.InventoryHighlight:new(bullRoarer) },
    postconditions = { Condition.ModelVisible:new(gujuo) },
  },
  {
    text = "Talk to Gujuo.",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("I was hoping to attract the attention of a native."),
    },
    postconditions = { Condition.ConversationText:new("want to talk about Bwana") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("I want to develop friendly relations with your people."),
    },
    postconditions = { Condition.ConversationText:new("very distributed throughout") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("Can you get your people together?"),
    },
    postconditions = { Condition.ConversationText:new("it does not seem to work") },
  },
  {
    -- text = "debug3",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("What can we do instead then?"),
    },
    postconditions = { Condition.ConversationText:new("sacred Yommi tree") },
  },
  {
    -- text = "debug4",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("How do we make the totem pole?"),
    },
    postconditions = { Condition.ConversationText:new("Kharazi Jungle") },
  },
  {
    -- text = "debug5",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("I will release Ungadulu..."),
    },
    postconditions = { Condition.ConversationText:new("go rest") }, --untested
  },
  {
    text = "Search the mossy rocks in the north-west of the jungle.",
    actions = {
      Action.Direction:new(2781, 2597, 2936.5, { distance = 20 }),
      Action.ModelHighlight:new(mossyRock, { distance = 20 }),
      Action.ConversationHighlight:new("Yes, I'll crawl through, I'm very athletic."),
    },
    postconditions = { Condition.DistanceTo:new(2772, 1461, 9341, 4) },
  },
  {
    text = "<i>Investigate</i> the fire wall.",
    actions = {
      Action.Direction:new(2788.35, 1457, 9332),
      Action.ConversationHighlight:new("How can I extinguish the flames?"),
      Action.ConversationHighlight:new("Where do I get pure water from?"),
    },
    postconditions = { Condition.ConversationText:new("starts convulsing") },
  },
  {
    text = "Exit the cave.",
    actions = { Action.ModelHighlight:new(caveExit) },
    postconditions = { Condition.DistanceTo:new(2781, 2597, 2936.5, 4) },
  },
  {
    text = "Swing the bull roarer until Gujuo appears.",
    actions = { Action.InventoryHighlight:new(bullRoarer) },
    postconditions = { Condition.ModelVisible:new(gujuo) },
  },
  {
    text = "Talk to Gujuo.",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("I need some pure water to douse some magic flames."),
    },
    postconditions = { Condition.ConversationText:new("will manage to claim some") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("Where is the pool of sacred water?"),
    },
    postconditions = { Condition.ConversationText:new("very dangerous") }, --wrong
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("What kind of a vessel?"),
    },
    postconditions = { Condition.ConversationText:new("that it will help you") },
  },
  {
    -- text = "debug3",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("Ok thanks for your help."),
    },
    postconditions = { Condition.ConversationText:new("collect herbs") },
  },
  {
    text = "Search the mossy rocks in the north-west of the jungle.",
    actions = {
      Action.Direction:new(2781, 2597, 2936.5, { distance = 20 }),
      Action.ModelHighlight:new(mossyRock, { distance = 20 }),
      Action.ConversationHighlight:new("Yes, I'll crawl through, I'm very athletic."),
    },
    postconditions = { Condition.DistanceTo:new(2772, 1461, 9341, 4) },
  },
  {
    text = "Search the eastern bookcase.",
    actions = {
      Action.Direction:new(2795.5, 1209, 9339),
      Action.ConversationHighlight:new("Yes please!"),
    },
    postconditions = { Condition.DistanceTo:new(2800, 701, 9340, 1) },
  },
  {
    text = "<i>Search</i> the ancient gate.",
    actions = { Action.Direction:new(2809.5, 1341, 9331.75) },
    postconditions = { Condition.DistanceTo:new(2809, 749, 9330, 1) },
  },
  {
    text = "Smash-to-bits the three boulders (drop the rocks).",
    actions = { Action.Direction:new(2809.5, 1373, 9327.5) },
    postconditions = { Condition.DistanceTo:new(2809, 789, 9325, 1) },
  },
  {
    -- text = "debug1",
    actions = { Action.Direction:new(2809.5, 1373, 9323.5) },
    postconditions = { Condition.DistanceTo:new(2809, 725, 9321, 1) },
  },
  {
    -- text = "debug2",
    actions = { Action.Direction:new(2809.5, 1373, 9319.5) },
    postconditions = { Condition.DistanceTo:new(2809, 981, 9317, 1) },
  },
  {
    text = "Open the Ancient Gate.",
    actions = {
      Action.ModelHighlight:new(ancientGate, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Yes, I'm very strong, I'll force them open."),
    },
    postconditions = { Condition.DistanceTo:new(2809, 1301, 9312, 1) },
  },
  {
    text = "Jump-over the jagged wall.<ul><li>Optional: Kill a death wing for a hard Karamja achievement.</li></ul>",
    actions = { Action.Direction:new(2791, 913, 9294.45) },
    postconditions = { Condition.DistanceTo:new(2790, 685, 9296, 1) },
  },
  {
    text = "Search the marked wall.",
    actions = {
      Action.Direction:new(2779, 1533, 9305),
      Action.ConversationHighlight:new("Yes, I'll read it."),
    },
    postconditions = { Condition.ConversationText:new("make maps from indifference") },
  },
  {
    text = "Use your runes on it in order according to your version:<ul><li>English: Soul, Mind, Earth, Law, Law.</li><li>German: Air, Earth, Blood, Earth, Nature.</li><li>French: Law, Air, Death, Mind, Blood.</li><li>Portuguese: Fire, Water, Earth, Air, Law.</li></ul>",
    warning = "Tracking is designed for en only.",
    actions = {
      Action.Direction:new(2779, 1533, 9305),
      Action.InventoryHighlight:new(Models.items["soul rune"]),
    },
    postconditions = { Condition.ConversationText:new("first depression") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.Direction:new(2779, 1533, 9305),
      Action.InventoryHighlight:new(Models.items["mind rune"]),
    },
    postconditions = { Condition.ConversationText:new("second depression") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.Direction:new(2779, 1533, 9305),
      Action.InventoryHighlight:new(Models.items["earth rune"]),
    },
    postconditions = { Condition.ConversationText:new("third depression") },
  },
  {
    -- text = "debug3",
    actions = {
      Action.Direction:new(2779, 1533, 9305),
      Action.InventoryHighlight:new(Models.items["law rune"]),
      Action.ConversationHighlight:new("Yes, I'll go through!"),
    },
    postconditions = { Condition.DistanceTo:new(2774, 805, 9301, 2) },
  },
  {
    text = "Use the cut sapphire on the carved rock.",
    actions = {
      Action.Direction:new(2781, 961, 9291),
      Action.InventoryHighlight:new(Models.items["sapphire"], true),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["sapphire"]) },
  },
  {
    text = "Use the cut diamond on the carved rock.",
    actions = {
      Action.Direction:new(2774, 1041, 9287),
      Action.InventoryHighlight:new(Models.items["diamond"], true),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["diamond"]) },
  },
  {
    text = "Use the cut ruby on the carved rock.",
    actions = {
      Action.Direction:new(2767, 1057, 9289),
      Action.InventoryHighlight:new(Models.items["ruby"], true),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["ruby"]) },
  },
  {
    text = "Use the cut red topaz on the carved rock.",
    actions = {
      Action.Direction:new(2772, 1157, 9295),
      Action.InventoryHighlight:new(Models.items["red topaz"], true),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["red topaz"]) },
  },
  {
    text = "Use the cut jade on the carved rock.",
    actions = {
      Action.Direction:new(2771, 1029, 9303),
      Action.InventoryHighlight:new(Models.items["jade"], true),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["jade"]) },
  },
  {
    text = "Use the cut emerald on the carved rock.",
    actions = {
      Action.Direction:new(2757, 1009, 9297),
      Action.InventoryHighlight:new(Models.items["emerald"], true),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["emerald"]) },
  },
  {
    text = "Use the cut opal on the carved rock.",
    actions = {
      Action.Direction:new(2764, 957, 9309),
      Action.InventoryHighlight:new(Models.items["opal"], true),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["opal"]) },
  },
  { text = "Wait for the cutscene.", postconditions = { Condition.ModelVisible:new(bindingBook) } },
  {
    text = "Take the Binding book.",
    actions = { Action.ModelHighlight:new(bindingBook) },
    postconditions = { Condition.InventoryContains:new(bindingBook) },
  },
  {
    text = "Use 2 gold bars on any anvil with the sketch in your inventory.",
    title = "Sacred pool",
    warning = "Bring extra bars in case it fails.",
    neededItems = {
      ["Radimus notes"] = { quantity = 1 },
      ["Vial"] = { quantity = 1 },
      ["Gold bar"] = { quantity = 1 },
      ["Binding book"] = { quantity = 1 },
      ["Sketch"] = { quantity = 1 },
      ["Bull roarer"] = { quantity = 1 },
    },
    actions = {
      Action.InventoryHighlight:new(Models.items["gold bar"]),
      Action.ConversationHighlight:new("Golden bowl"),
    },
    postconditions = { Condition.InventoryContains:new(goldenBowl) },
  },
  {
    text = "Enter the Kharazi Jungle.",
    actions = { Action.Direction:new(2796, 853, 2940) },
    postconditions = {
      Condition.DistanceTo:new(2796, 1157, 2937, 2), -- west
      Condition.DistanceTo:new(2866, 1093, 2930, 4), -- center
      Condition.DistanceTo:new(2936, 589, 2933, 4), -- east
      Condition.DistanceTo:new(2902, 1445, 2931, 2), -- fairy ring
    },
  },
  {
    text = "Swing the bull roarer until Gujuo appears.",
    actions = { Action.InventoryHighlight:new(bullRoarer) },
    postconditions = { Condition.ModelVisible:new(gujuo) },
  },
  {
    text = "Talk to Gujuo.",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("Yes, I'd like you to bless my gold bowl."),
    },
    postconditions = { Condition.ConversationText:new("The bowl is blessed") },
  },
  {
    text = "'Cut-Reed' on the tall reeds next to the water pool.",
    actions = {
      Action.Direction:new(2836, 453, 2915, { distance = 20 }),
      Action.ModelHighlight:new(reeds, { distance = 20 }),
    },
    postconditions = { Condition.InventoryContains:new(hollowReed) },
  },
  {
    text = "Use the hollow reed on the water pool.",
    actions = {
      Action.ModelHighlight:new(filledPond),
      Action.InventoryHighlight:new(hollowReed),
    },
    postconditions = { Condition.InventoryContains:new(filledGoldenBowl) },
  },
  {
    text = "Enchant-Vials with the binding book to create an enchanted vial.",
    actions = { Action.InventoryHighlight:new(bindingBook, true) },
    postconditions = { Condition.InventoryContains:new(enchantedVial) },
  },
  {
    text = "Use the hollow reed on the Water Pool again.",
    actions = {
      Action.ModelHighlight:new(filledPond),
      Action.InventoryHighlight:new(hollowReed),
    },
    postconditions = { Condition.InventoryContains:new(holyWater) },
  },
  {
    text = "Search the mossy rocks in the north-west of the jungle.",
    actions = {
      Action.Direction:new(2781, 2597, 2936.5, { distance = 20 }),
      Action.ModelHighlight:new(mossyRock, { distance = 20 }),
      Action.ConversationHighlight:new("Yes, I'll crawl through, I'm very athletic."),
    },
    postconditions = { Condition.DistanceTo:new(2772, 1461, 9341, 4) },
  },
  {
    text = "Use the golden bowl on the fire wall.",
    actions = {
      Action.Direction:new(2788.35, 1457, 9332),
      Action.InventoryHighlight:new(filledGoldenBowl),
    },
    postconditions = { Condition.DistanceTo:new(2790, 1053, 9331, 1) },
  },
  {
    text = "Use the binding book on Ungadulu.",
    actions = {
      Action.ModelHighlight:new(ungadulu),
      Action.InventoryHighlight:new(bindingBook),
    },
    postconditions = { Condition.ModelVisible:new(nezikchened) },
  },
  {
    text = "Kill Nezikchened. He will blast the player for up to 2500 typeless damage when he dies.",
    postconditions = { Condition.ModelNotVisible:new(nezikchened) },
  },
  {
    text = "Talk to Ungadulu.",
    actions = {
      Action.ModelHighlight:new(ungadulu),
      Action.ConversationHighlight:new("I need to collect some Yommi tree seeds for Gujuo."),
    },
    postconditions = { Condition.ConversationText:new("passed into the flaming") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(ungadulu),
      Action.ConversationHighlight:new("How do I grow the Yommi tree?"),
    },
    postconditions = { Condition.ConversationText:new("be difficult to find it") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(ungadulu),
      Action.ConversationHighlight:new("What will you do now?"),
    },
    postconditions = { Condition.ConversationText:new("that terrible Demon") },
  },
  {
    -- text = "debug3",
    actions = {
      Action.ModelHighlight:new(ungadulu),
      Action.ConversationHighlight:new("Ok, thanks..."),
    },
    postconditions = { Condition.ConversationText:new("pleasure Bwana") },
  },
  {
    text = "Touch the fire wall.",
    actions = { Action.Direction:new(2788.35, 1457, 9332) },
    postconditions = { Condition.DistanceTo:new(2786, 901, 9332, 2) },
  },
  {
    text = "Exit the cave.",
    actions = { Action.ModelHighlight:new(caveExit) },
    postconditions = { Condition.DistanceTo:new(2781, 2597, 2936.5, 4) },
  },
  {
    text = "Use the golden bowl on the seeds.",
    actions = {
      Action.InventoryHighlight:new(filledGoldenBowl),
      Action.InventoryHighlight:new(yommiTreeSeeds),
    },
    postconditions = { Condition.ConversationActive:new() }, --easy to break
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Use the seeds on a patch of fertile soil.",
    actions = {
      Action.Direction:new(2832, 1157, 2922, { tile = true }),
      Action.InventoryHighlight:new(germinatedYommiTreeSeeds),
    },
    postconditions = { Condition.DistanceTo:new(2832, 1157, 2922, 3) },
  },
  {
    actions = {
      Action.Direction:new(2832, 1157, 2922, { tile = true }),
      Action.InventoryHighlight:new(germinatedYommiTreeSeeds),
    },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Use the hollow reed on the pool.",
    actions = {
      Action.ModelHighlight:new(filledPond),
      Action.InventoryHighlight:new(hollowReed),
    },
    postconditions = { Condition.ConversationText:new("looks as if") },
  },
  {
    text = "Swing the bull roarer until Gujuo appears.",
    actions = { Action.InventoryHighlight:new(bullRoarer) },
    postconditions = { Condition.ModelVisible:new(gujuo) },
  },
  {
    text = "Talk to Gujuo.",
    warning = "Switch to the standard spellbook if you are not already on it after talking to him.",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("The water pool has dried up and I need more water."),
    },
    postconditions = { Condition.ConversationText:new("evil is at work here") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("Where is the source of the spring of pure water?"),
    },
    postconditions = { Condition.ConversationText:new("never go near such a place") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("If I went, could you help me?"),
    },
    postconditions = { Condition.ConversationText:new("Kharazi lion") },
  },
  {
    -- text = "debug3",
    actions = {
      Action.ModelHighlight:new(gujuo),
      Action.ConversationHighlight:new("Ok thanks for your help."),
    },
    postconditions = { Condition.ConversationText:new("collect herbs now") },
  },
  {
    text = "Search palm trees for grimy ardrigal and clean it.",
    title = "Restoring the pool",
    neededItems = {
      ["Radimus notes"] = { quantity = 1 },
      ["Clean ardrigal"] = { quantity = 1 },
      ["Clean snake weed"] = { quantity = 1 },
      ["Vial of water"] = { quantity = 1 },
      ["Unpowered orb"] = { quantity = 1 },
      ["Runes for any Charge Orb Spell"] = { quantity = 1 },
      ["Blessed gold bowl"] = { quantity = 1 },
      ["Yommi tree seeds"] = { quantity = 1 },
      ["Lockpick"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(2864.5, 661, 3118.5),
      Action.InventoryHighlight:new(Models.items["grimy ardrigal"]),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["ardrigal"]) },
  },
  {
    text = "Search some marshy jungle vines for a grimy snake weed.",
    actions = {
      Action.Direction:new(2759, 143, 3043, { tile = true }),
      Action.InventoryHighlight:new(Models.items["grimy snake weed"]),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["snake weed"]) },
  },
  {
    text = "Use the ardrigal and snake weed on a vial of water for a bravery potion.",
    actions = {
      Action.InventoryHighlight:new(Models.items["ardrigal"]),
      Action.InventoryHighlight:new(Models.items["snake weed"]),
      Action.InventoryHighlight:new(Models.items["vial of water"]),
    },
    postconditions = { Condition.InventoryContains:new(braveryPotion) },
  },
  {
    text = "Enter the Kharazi Jungle.",
    actions = { Action.Direction:new(2796, 853, 2940) },
    postconditions = {
      Condition.DistanceTo:new(2796, 1157, 2937, 2), -- west
      Condition.DistanceTo:new(2866, 1093, 2930, 4), -- center
      Condition.DistanceTo:new(2936, 589, 2933, 4), -- east
      Condition.DistanceTo:new(2902, 1445, 2931, 2), -- fairy ring
    },
  },
  {
    text = "Search the mossy rocks in the north-west of the jungle.",
    actions = {
      Action.Direction:new(2781, 2597, 2936.5, { distance = 20 }),
      Action.ModelHighlight:new(mossyRock, { distance = 20 }),
      Action.ConversationHighlight:new("Yes, I'll crawl through, I'm very athletic."),
    },
    postconditions = { Condition.DistanceTo:new(2772, 1461, 9341, 4) },
  },
  {
    text = "Traverse the dungeon as before to where you found the Binding book:<ul><li>Search the eastern Bookcase.</li><li>Choose the Search option on the Ancient Gate.</li><li>Smash-to-bits the three Boulders.</li><li>Open the Ancient Gate.</li><li>Move past the Death wings.</li><li>Choose the Jump-over option on Jagged wall.</li><li>Use the Marked wall.</li></ul>",
    actions = {
      Action.PathGuide:new({
        Location:new(2772, 1461, 9341),
        Location:new(2774, 1405, 9338),
        Location:new(2795, 709, 9338),
        Location:new(2795, 757, 9340),
        Location:new(2799, 757, 9341),
        Location:new(2800, 701, 9340),
        Location:new(2809, 717, 9335),
        Location:new(2809, 741, 9331),
        Location:new(2809, 805, 9326),
        Location:new(2809, 765, 9322),
        Location:new(2809, 925, 9318),
        Location:new(2809, 1189, 9313),
        Location:new(2812, 1117, 9306),
        Location:new(2812, 1013, 9296),
        Location:new(2806, 1155, 9290),
        Location:new(2805, 1381, 9284),
        Location:new(2795, 757, 9284),
        Location:new(2791, 613, 9294),
        Location:new(2788, 877, 9298),
        Location:new(2787, 877, 9305),
        Location:new(2780, 933, 9305),
        Location:new(2774, 805, 9301),
        Location:new(2763, 1053, 9313),
      }),

      Action.ConversationHighlight:new("Yes, I'm very strong, I'll force them open."),
    },
    postconditions = { Condition.DistanceTo:new(2762, 1085, 9315, 2) },
  },
  {
    text = "Cast a charge orb spell on the north Ancient Gate.",
    actions = { Action.Direction:new(2762.5, 1685, 9315.5) },
    postconditions = { Condition.DistanceTo:new(2762, 1125, 9317, 1) },
  },
  {
    text = "Smash the barrels for a rope.",
    postconditions = { Condition.InventoryContains:new(Models.items["rope"]) },
  },
  {
    text = "Use the Rope on the Winch.<ul><li>If you forgot your rope, you can smash the barrels in the room for one.</li></ul>",
    actions = {
      Action.Direction:new(2762, 1085, 9330),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.ModelVisible:new(winchWithRope) },
  },
  {
    text = "Drink the bravery potion.",
    actions = {
      Action.InventoryHighlight:new(braveryPotion),
      Action.ConversationHighlight:new("Yes, I'll bravely drink the bravery potion."),
    },
    postconditions = { Condition.ConversationText:new("feel quite") }, --not tested
  },
  {
    text = "Search and then climb-down the Winch.",
    actions = {
      Action.Direction:new(2762, 1085, 9330),
      Action.ConversationHighlight:new("Yes, I'll shimmy down the rope into possible doom."),
    },
    postconditions = { Condition.DistanceTo:new(2377, 5573, 4712, 4) },
  },
  { --not doing direction arrows/path since chances are users will fall (like I did)
    text = "Climb-over the rocky ledges and rocks to reach the bottom. If you fall, skip to the next step.",
    title = "Restoring the pool (part 2)",
    actions = {
      Action.ConversationHighlight:new("Yes, I can think of nothing more exciting!"),
      Action.ConversationHighlight:new("Yes, I want to climb over the rocks."),
    },
    postconditions = { Condition.DistanceTo:new(2390, 1989, 4716, 1) },
  },
  {
    text = "Kill San Tojalon.",
    actions = { Action.ModelHighlight:new(sanTojalon) },
    postconditions = { Condition.InventoryContains:new(crystalChunk) },
  },
  {
    text = "Kill Irvig Senay.",
    actions = { Action.ModelHighlight:new(irvigSenay) },
    postconditions = { Condition.InventoryContains:new(crystalHunk) },
  },
  {
    text = "Kill Ranalph Devere.",
    actions = { Action.ModelHighlight:new(ranalphDevere) },
    postconditions = { Condition.InventoryContains:new(crystalLump) },
  },
  {
    text = "Use each crystal on the north-east furnace.",
    actions = {
      Action.Direction:new(2427, 897, 4727),
      Action.InventoryHighlight:new(crystalChunk),
      Action.InventoryHighlight:new(crystalHunk),
      Action.InventoryHighlight:new(crystalLump),
    },
    postconditions = { Condition.InventoryContains:new(heartCrystal) },
  },
  {
    text = "Use the heart crystal on the searchable mossy rock in the middle of the room.",
    actions = {
      Action.Direction:new(2410.5, 1517, 4715.5),
      Action.InventoryHighlight:new(heartCrystal, true),
    },
    postconditions = { Condition.InventoryContains:new(chargedHeartCrystal) },
  },
  {
    text = "Run south and use the heart crystal on the recess next to the shimmering field.",
    actions = {
      Action.Direction:new(2421.85, 1437, 4691.1),
      Action.InventoryHighlight:new(chargedHeartCrystal),
    },
    postconditions = { Condition.ModelVisible:new(heartInWall) },
  },
  {
    text = "Walk-through the shimmering field.",
    actions = { Action.Direction:new(2420.5, 1677, 4690.5) },
    postconditions = { Condition.DistanceTo:new(2421, 1069, 4689, 1) },
  },
  {
    text = "Push the boulder.",
    actions = {
      Action.Direction:new(2392.5, 893, 4678.5),
      Action.ConversationHighlight:new("Who's asking?"),
      Action.ConversationHighlight:new("What can I do about that?"),
      Action.ConversationHighlight:new("I'll do what I must to get the water."),
      Action.ConversationHighlight:new("Ok, I'll do it."),
    },
    postconditions = { Condition.ConversationText:new("the dagger back to me") },
  },
  {
    text = "Walk-through the shimmering field.",
    actions = {
      Action.Direction:new(2420.5, 1677, 4690.5),
      Action.InventoryHighlight:new(darkDagger),
    },
    postconditions = { Condition.DistanceTo:new(2420, 1101, 4692, 1) },
  },
  {
    text = "Climb back up the cliff. Good luck!",
    actions = {
      Action.InventoryHighlight:new(darkDagger),
      Action.Direction:new(2390, 2393, 4718),
      Action.Direction:new(2390, 2485, 4724),
      Action.Direction:new(2387, 4361, 4728),
      Action.Direction:new(2382, 4269, 4729),
      Action.Direction:new(2377, 5077, 4728),
      Action.Direction:new(2377, 5085, 4717),
      Action.ConversationHighlight:new("Yes, I can think of nothing more exciting!"),
      Action.ConversationHighlight:new("Yes, I want to climb over the rocks."),
    },
    postconditions = { Condition.DistanceTo:new(2378, 5085, 4716, 1) },
  },
  {
    text = "Equip the Dark dagger then take the Blue hat.",
    actions = {
      Action.ModelHighlight:new(blueWizardHat),
      Action.InventoryHighlight:new(darkDagger),
    },
    postconditions = { Condition.ModelVisible:new(viyeldi) },
  },
  {
    text = "Kill Viyeldi. <b>Attack him during the dialogue.</b> He will disappear after completing the dialogue.",
    actions = { Action.ModelHighlight:new(blueWizardHat) },
    postconditions = { Condition.ChatText:new("seems to glow as Viyeldi") },
  },
  {
    text = "Return to the boulders and push one.<ul><li>You can unequip the dagger.</li></ul>",
    actions = {
      Action.Direction:new(2392.5, 893, 4678.5),
      Action.ConversationHighlight:new("Yes, I can think of nothing more exciting!"),
      Action.ConversationHighlight:new("Yes, I want to climb over the rocks."),
    },
    postconditions = { Condition.ModelVisible:new(nezikchened) },
  },
  {
    text = "Kill Nezikchened.",
    postconditions = { Condition.ModelNotVisible:new(nezikchened) },
  },
  {
    text = "Push all three boulders.<ul><li>All three must be pushed while standing on the east side of them.</li><li>If one does not move, Push it from a different angle.</li></ul>",
    actions = {
      Action.Direction:new(2394, 405, 4679, { tile = true }),
      Action.Direction:new(2388, 541, 4689, { tile = true }),
      Action.Direction:new(2375, 141, 4693, { tile = true }),
    },
    postconditions = { Condition.ModelVisible:new(sacredSpring) },
  },
  {
    text = "Return to the Kharazi Jungle (fairy ring CJS).",
    actions = { Action.Direction:new(2796, 853, 2940) },
    postconditions = {
      Condition.DistanceTo:new(2796, 1157, 2937, 2), -- west
      Condition.DistanceTo:new(2866, 1093, 2930, 4), -- center
      Condition.DistanceTo:new(2936, 589, 2933, 4), -- east
      Condition.DistanceTo:new(2902, 1445, 2931, 2), -- fairy ring
    },
  },
  {
    text = "Use the hollow reed on the water pool.",
    actions = {
      Action.ModelHighlight:new(filledPond),
      Action.InventoryHighlight:new(hollowReed),
    },
    postconditions = { Condition.InventoryContains:new(filledGoldenBowl) },
  },
  {
    text = "Use the yommi tree seeds on the fertile soil.",
    actions = {
      Action.Direction:new(2832, 1157, 2922, { tile = true }),
      Action.InventoryHighlight:new(germinatedYommiTreeSeeds, true),
    },
    postconditions = { Condition.ModelVisible:new(sproutTree) },
  },
  { text = "Wait for the tree to grow a bit.", postconditions = { Condition.ModelVisible:new(partiallyGrownTree) } },
  {
    text = "Use the Golden bowl on the partially grown tree.",
    actions = {
      Action.ModelHighlight:new(partiallyGrownTree),
      Action.InventoryHighlight:new(filledGoldenBowl),
    },
    postconditions = { Condition.InventoryContains:new(yommiTree) },
  },
  {
    text = "Chop the Adult Yommi tree.",
    actions = { Action.ModelHighlight:new(yommiTree) },
    postconditions = { Condition.ModelVisible:new(fallenYommiTree) },
  },
  {
    text = "Trim the Felled Yommi tree.",
    actions = { Action.ModelHighlight:new(fallenYommiTree) },
    postconditions = { Condition.ModelVisible:new(partiallyCarvedYommiTree) },
  },
  {
    text = "Carve the Trimmed Yommi.",
    actions = { Action.ModelHighlight:new(partiallyCarvedYommiTree) },
    postconditions = { Condition.ModelVisible:new(carvedYommiTree) },
  },
  {
    text = "Lift the Totem pole.",
    actions = { Action.ModelHighlight:new(carvedYommiTree) },
    postconditions = { Condition.InventoryContains:new(yommiTotem) },
  },
  {
    text = "Use your Yommi totem on the Totem Pole east of the pool.",
    title = "The final fight",
    warning = "Heal up before using the totem.",
    neededItems = {
      ["Yommi totem"] = { quantity = 1 },
      ["Radimus notes"] = { quantity = 1 },
      ["Bull roarer"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(2876, 1717, 2913, { distance = 16 }),
      Action.ModelHighlight:new(oldTotem, { distance = 16 }),
      Action.InventoryHighlight:new(yommiTotem),
    },
    postconditions = { Condition.ModelVisible:new(nezikchened) },
  },
  {
    text = "Defeat Nezikchened and the three skeletons.<ul><li>He will appear four times, with a skeleton spawning between each kill.</li></ul>",
    postconditions = { Condition.ChatText:new("the final killing blow") },
  },
  {
    text = "Use the Yommi totem on the Totem Pole again.",
    actions = {
      Action.ModelHighlight:new(oldTotem),
      Action.InventoryHighlight:new(yommiTotem),
    },
    postconditions = { Condition.ModelVisible:new(newTotem) },
  },
  {
    text = "Talk to Gujuo to receive the gilded totem.",
    actions = { Action.ModelHighlight:new(gujuo) },
    postconditions = { Condition.ConversationText:new("am tired Bwana") },
  },
  {
    text = "Enter the Legends' Guild courtyard.",
    title = "Finishing up",
    actions = { Action.Direction:new(2728.5, 2245, 3349.5) },
    postconditions = { Condition.DistanceTo:new(2728, 2245, 3351, 1) },
  },
  {
    text = "Talk to Radimus at the Legends' Guild.",
    actions = {
      Action.Direction:new(2725, 2245, 3368, { distance = 18 }),
      Action.ModelHighlight:new(radimusErkle, { distance = 18 }),
    },
    postconditions = { Condition.ChatText:new("meet you in the main hall") },
  },
  {
    text = "Talk to Radimus again inside the guild.",
    actions = { Action.Direction:new(2728.5, 2845, 3373.5) },
    postconditions = { Condition.DistanceTo:new(2728, 2245, 3375, 1) },
  },
  {
    actions = {
      Action.ModelHighlight:new(radimusErkle),
      Action.ConversationHighlight:new("Yes, I'll train now."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Legends' Quest",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1061337600,
  prereqQuests = { "Heroes' Quest", "Jungle Potion" },
  questReqs = {
    Types.QuestReq.skill("Agility", 50),
    Types.QuestReq.skill("Crafting", 50),
    Types.QuestReq.skill("Herblore", 45),
    Types.QuestReq.skill("Magic", 56),
    Types.QuestReq.skill("Mining", 52),
    Types.QuestReq.skill("Prayer", 42),
    Types.QuestReq.skill("Smithing", 50),
    Types.QuestReq.skill("Strength", 50),
    Types.QuestReq.skill("Thieving", 50),
    Types.QuestReq.skill("Woodcutting", 50),
    Types.QuestReq.questpoints(107),
  },
  neededItems = {
    ["Food"] = { quantity = 1 },
    ["Rope"] = { quantity = 1, model = Models.items["rope"], duringQuest = true },
    ["Empty vial"] = { quantity = 1, model = Models.items["vial"] },
    ["Vial of water"] = { quantity = 1, model = Models.items["vial of water"] },
    ["Papyrus"] = { quantity = 5, model = Models.items["papyrus"], duringQuest = true },
    ["Charcoal"] = { quantity = 3, model = Models.items["charcoal"] },
    ["Unpowered orb"] = { quantity = 2, model = Models.items["unpowered orb"] },
    ["Gold bars"] = { quantity = 4, model = Models.items["gold bar"] },
    ["Clean ardrigal"] = { quantity = 1, model = Models.items["ardrigal"], duringQuest = true },
    ["Clean snake weed"] = { quantity = 1, model = Models.items["snake weed"], duringQuest = true },
    ["Cut opal"] = { quantity = 1, model = Models.items["opal"] },
    ["Cut jade"] = { quantity = 1, model = Models.items["jade"] },
    ["Cut red topaz"] = { quantity = 1, model = Models.items["red topaz"] },
    ["Cut sapphire"] = { quantity = 1, model = Models.items["sapphire"] },
    ["Cut emerald"] = { quantity = 1, model = Models.items["emerald"] },
    ["Cut ruby"] = { quantity = 1, model = Models.items["ruby"] },
    ["Cut diamond"] = { quantity = 1, model = Models.items["diamond"] },
    ["Runes for any Charge Orb Spell"] = { quantity = 5 },
    ["Lockpick, hair clip or a master thief's lockpick"] = { quantity = 1, model = Models.items["lockpick"] },
    ["Unaugmented rune hatchet or better (tool belt works)"] = { quantity = 1 },
    ["(en) 1 Soul, mind, earth, 2 law runes"] = { quantity = 1 },
    ["(de) 1 air, blood, nature, 2 earth runes"] = { quantity = 1 },
    ["(fr) 1 law, air, death, mind, blood rune"] = { quantity = 1 },
    ["(pt) 1 fire, water, earth, air, law rune"] = { quantity = 1 },
  },
  recommendedItems = {
    ["Bittercap mushrooms to activate fairy ring CJS"] = { quantity = 5 },
  },
  combatNPCs = {
    ["Nezikchened"] = { level = "84", quantity = 3 },
    ["Ranalph Devere"] = { level = "63", quantity = 2 },
    ["Irvig Senay"] = { level = "70", quantity = 2 },
    ["San Tojalon"] = { level = "70", quantity = 2 },
  },
})
