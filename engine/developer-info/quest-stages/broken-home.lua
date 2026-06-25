local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

-- TODO: If we can ever highlight particles, use this
local doorFogParticles = Model.new(816, {
  [507] = Vertex.new(5380623, 2367, 1213488, 82, 82, 82, 0.07059),
  [513] = Vertex.new(5380609, 2381, 1213488, 81, 81, 81, 0.03529),
  [782] = Vertex.new(5380627, 2479, 1213488, 44, 44, 44, 0.04706),
  [783] = Vertex.new(5380627, 2479, 1213488, 44, 44, 44, 0.04706),
  [786] = Vertex.new(5380627, 2479, 1213488, 44, 44, 44, 0.04706),
})

--#region NPCs
local maria = Model.new(4062, {
  [978] = Vertex.new(-24, 738, -38, 49, 47, 45),
  [982] = Vertex.new(-2, 716, -57, 177, 130, 90),
  [987] = Vertex.new(-6, 716, -52, 177, 130, 90),
  [988] = Vertex.new(2, 716, -57, 177, 130, 90),
  [1002] = Vertex.new(24, 738, -38, 49, 47, 45),
})
local lenian = Model.new(6099, {
  [5961] = Vertex.new(17, 517, -27, 254, 254, 254),
  [5966] = Vertex.new(17, 517, -27, 254, 254, 254),
  [5967] = Vertex.new(29, 509, -21, 254, 254, 254),
  [6056] = Vertex.new(22, 505, -28, 254, 254, 254),
  [6084] = Vertex.new(15, 506, -28, 254, 254, 254),
})
local dukeSucc = Model.new(19824, {
  [1661] = Vertex.new(589, 934, -846, 254, 254, 254),
  [3474] = Vertex.new(-446, 1031, -904, 254, 254, 254),
  [3477] = Vertex.new(-446, 1031, -904, 254, 254, 254),
  [3825] = Vertex.new(-599, 876, -884, 254, 254, 254),
  [3826] = Vertex.new(-521, 886, -972, 254, 254, 254),
})
--#endRegion
--#region Objects
local openedChest = Model.any({
  Model.new(1494, {
    [1183] = Vertex.new(176, 158, -244, 133, 76, 40),
    [1185] = Vertex.new(211, 195, -244, 133, 76, 40),
    [1187] = Vertex.new(137, 158, -244, 133, 76, 40),
    [1190] = Vertex.new(100, 182, -244, 133, 76, 40),
    [1194] = Vertex.new(211, 182, -244, 133, 76, 40),
  }),
  Model.new(1494, {
    [1183] = Vertex.new(169, 158, -255, 135, 78, 42),
    [1185] = Vertex.new(201, 195, -255, 135, 78, 42),
    [1187] = Vertex.new(131, 158, -255, 135, 78, 42),
    [1190] = Vertex.new(96, 182, -255, 135, 78, 42),
    [1194] = Vertex.new(201, 182, -255, 135, 78, 42),
  }),
})
local genericKeyObj = Model.new(468, {
  [144] = Vertex.new(-30, 3, -127, 188, 156, 98),
  [150] = Vertex.new(-17, 3, -125, 188, 156, 98),
  [201] = Vertex.new(-35, 3, -79, 166, 132, 69),
  [282] = Vertex.new(-62, 3, -101, 166, 132, 69),
  [288] = Vertex.new(-62, 3, -101, 166, 132, 69),
})
local scytheKeyObj = Model.new(468, {
  [144] = Vertex.new(-30, 3, -127, 115, 95, 60),
  [150] = Vertex.new(-17, 3, -125, 115, 95, 60),
  [201] = Vertex.new(-35, 3, -79, 103, 82, 43),
  [282] = Vertex.new(-62, 3, -101, 103, 82, 43),
  [288] = Vertex.new(-62, 3, -101, 103, 82, 43),
})
local tusksKeyObj = Model.new(468, {
  [144] = Vertex.new(-30, 3, -127, 113, 93, 58),
  [150] = Vertex.new(-17, 3, -125, 113, 93, 58),
  [201] = Vertex.new(-35, 3, -79, 101, 80, 41),
  [282] = Vertex.new(-62, 3, -101, 101, 80, 41),
  [288] = Vertex.new(-62, 3, -101, 101, 80, 41),
})
local daggerKeyObj = Model.new(468, {
  [144] = Vertex.new(-30, 3, -127, 113, 93, 58),
  [150] = Vertex.new(-17, 3, -125, 113, 93, 58),
  [201] = Vertex.new(-35, 3, -79, 101, 80, 41),
  [282] = Vertex.new(-62, 3, -101, 101, 80, 41),
  [288] = Vertex.new(-62, 3, -101, 101, 80, 41),
})
local clockHandObj = Model.new(276, {
  [26] = Vertex.new(126, 7, 19, 254, 254, 254),
  [28] = Vertex.new(126, 7, 19, 254, 254, 254),
  [30] = Vertex.new(126, 12, 19, 254, 254, 254),
  [48] = Vertex.new(126, 7, 19, 254, 254, 254),
  [168] = Vertex.new(-73, 7, 37, 254, 254, 254),
})
local clock = Model.new(1800, {
  [90] = Vertex.new(113, 992, 68, 214, 208, 208),
  [102] = Vertex.new(113, 1021, 39, 214, 208, 208),
  [242] = Vertex.new(203, 1110, 114, 98, 95, 93),
  [675] = Vertex.new(203, 1110, 114, 152, 125, 109),
  [681] = Vertex.new(203, 1110, 114, 143, 119, 105),
})
local foodChest = Model.new(1401, {
  [177] = Vertex.new(-250, 208, -171, 70, 76, 76),
  [608] = Vertex.new(-238, 232, -196, 70, 76, 76),
  [710] = Vertex.new(238, 232, -196, 70, 76, 76),
  [714] = Vertex.new(242, 294, -98, 70, 76, 76),
  [1128] = Vertex.new(-242, 294, -98, 70, 76, 76),
})
local bookPile = Model.new(1182, {
  [311] = Vertex.new(136, 12, -208, 200, 192, 192),
  [314] = Vertex.new(136, 12, -208, 200, 192, 192),
  [494] = Vertex.new(244, 12, 76, 86, 73, 36),
  [503] = Vertex.new(144, 132, 184, 10, 69, 112),
  [981] = Vertex.new(168, 96, 156, 86, 73, 36),
})
local eyeGemObj = Model.new(162, {
  [157] = Vertex.new(60, 359, 62, 15, 183, 81),
  [159] = Vertex.new(-59, 359, 62, 15, 183, 81),
  [162] = Vertex.new(60, 359, -60, 15, 183, 81),
})
local eyeGemObj2 = Model.new(156, {
  [6] = Vertex.new(-30, 54, -8, 11, 119, 54, 0.7490),
  [8] = Vertex.new(-30, 54, -8, 12, 131, 59, 0.7490),
  [105] = Vertex.new(-30, 54, -8, 15, 168, 76, 0.7490),
  [115] = Vertex.new(38, 41, -5, 15, 168, 76, 0.7490),
  [116] = Vertex.new(25, 59, 3, 15, 168, 76, 0.7490),
})
local temporalChest = Model.new(1290, {
  [51] = Vertex.new(-186, 194, 56, 58, 34, 18),
  [510] = Vertex.new(-194, 183, 67, 58, 34, 18),
  [669] = Vertex.new(-250, 134, -206, 72, 75, 79),
  [681] = Vertex.new(-250, 134, -185, 72, 75, 79),
  [705] = Vertex.new(250, 134, -206, 72, 75, 79),
})
local temporalChest2 = Model.new(1290, {
  [51] = Vertex.new(-178, 194, 59, 56, 32, 17),
  [510] = Vertex.new(-185, 183, 70, 56, 32, 17),
  [669] = Vertex.new(-239, 134, -215, 70, 73, 77),
  [681] = Vertex.new(-239, 134, -194, 70, 73, 77),
  [705] = Vertex.new(239, 134, -215, 70, 73, 77),
})
local curtain = Model.new(2982, {
  [2028] = Vertex.new(-1525, 1091, 548, 232, 230, 229),
  [2847] = Vertex.new(1536, 1093, 464, 163, 143, 105),
  [2862] = Vertex.new(-1520, 1112, 460, 180, 156, 111),
  [2871] = Vertex.new(1536, 1112, 468, 140, 117, 87),
  [2892] = Vertex.new(1536, 1091, 552, 117, 101, 80),
})
local facelessServant = Model.new(3813, {
  [864] = Vertex.new(-49, 672, 16, 195, 187, 187),
  [867] = Vertex.new(47, 670, 16, 228, 224, 224),
  [1432] = Vertex.new(0, 733, -5, 92, 236, 49),
  [1434] = Vertex.new(0, 730, -6, 92, 236, 49),
  [3292] = Vertex.new(126, 380, -15, 92, 236, 49),
})
local frozenServant = Model.new(3825, {
  [1275] = Vertex.new(24, 738, -38, 32, 31, 29),
  [1299] = Vertex.new(-24, 738, -38, 32, 31, 29),
  [1321] = Vertex.new(-2, 716, -57, 109, 81, 57),
  [1327] = Vertex.new(2, 716, -57, 109, 81, 57),
  [1331] = Vertex.new(6, 716, -52, 109, 81, 57),
})
local eyeStatue = Model.new(1905, {
  [768] = Vertex.new(188, 859, 15, 127, 122, 117),
  [1493] = Vertex.new(69, 1222, 0, 71, 68, 65),
  [1586] = Vertex.new(188, 859, 15, 131, 126, 120),
  [1743] = Vertex.new(165, 980, -93, 139, 134, 128),
  [1755] = Vertex.new(185, 980, 93, 107, 102, 98),
})
local slumpedButler = Model.new(3996, {
  [1567] = Vertex.new(-511, 49, 113, 30, 142, 129),
  [1568] = Vertex.new(-509, 50, 114, 30, 142, 129),
  [1569] = Vertex.new(-508, 51, 113, 30, 142, 129),
  [2574] = Vertex.new(-464, 85, 87, 132, 121, 121),
  [2577] = Vertex.new(-474, 115, 91, 132, 121, 121),
})
local ormodsScribblings = Model.new(30, {
  [3] = Vertex.new(1855, 25, 1949, 0, 0, 0),
  [5] = Vertex.new(1887, 28, 1568, 0, 0, 0),
  [9] = Vertex.new(1760, 14, 1625, 81, 67, 42),
  [15] = Vertex.new(1553, 12, 1793, 78, 65, 41),
  [21] = Vertex.new(1764, 9, 1619, 0, 0, 0, 0.4118),
})
local finalNotes = Model.new(102, {
  [71] = Vertex.new(2386, 1963, 2726, 0, 0, 0),
  [75] = Vertex.new(2358, 1977, 3092, 0, 0, 0),
  [77] = Vertex.new(2390, 1980, 2711, 0, 0, 0),
  [81] = Vertex.new(2263, 1966, 2768, 58, 45, 23),
  [93] = Vertex.new(2267, 1961, 2762, 0, 0, 0, 0.4118),
})
local scorchedSkeleton = Model.new(3615, {
  [483] = Vertex.new(-40, 267, 45, 83, 91, 58),
  [612] = Vertex.new(40, 218, 138, 124, 127, 97),
  [648] = Vertex.new(-87, 231, 116, 141, 144, 111),
  [1305] = Vertex.new(-40, 267, 45, 133, 135, 104),
  [3117] = Vertex.new(-9, 212, 53, 150, 152, 117),
})
local pipetteDrawer = Model.new(1074, {
  [425] = Vertex.new(-256, 560, 236, 74, 55, 39),
  [449] = Vertex.new(252, 560, 252, 74, 55, 39),
  [452] = Vertex.new(252, 560, 252, 74, 55, 39),
  [751] = Vertex.new(-252, 560, -108, 64, 47, 33),
  [762] = Vertex.new(252, 560, 252, 64, 47, 33),
})
local cauldron = Model.new(36, {
  [3] = Vertex.new(-329, 14, -191, 0, 0, 0),
  [5] = Vertex.new(322, 14, 191, 0, 0, 0),
  [12] = Vertex.new(151, 488, -95, 79, 130, 195),
  [18] = Vertex.new(-135, 488, -116, 79, 130, 195),
  [33] = Vertex.new(137, 488, 110, 79, 130, 195),
})
local cauldronGreen = Model.new(36, {
  [3] = Vertex.new(-329, 14, -191, 0, 0, 0),
  [5] = Vertex.new(322, 14, 191, 0, 0, 0),
  [12] = Vertex.new(151, 488, -95, 158, 195, 79),
  [18] = Vertex.new(-135, 488, -116, 158, 195, 79),
  [33] = Vertex.new(137, 488, 110, 158, 195, 79),
})
local slimyDoor = Model.new(1029, {
  [177] = Vertex.new(-235, 575, 226, 220, 215, 215, 0.6000),
  [189] = Vertex.new(-233, 721, -245, 220, 215, 215, 0.6000),
  [313] = Vertex.new(-233, 672, -248, 220, 215, 215, 0.6000),
  [549] = Vertex.new(-237, 969, -211, 220, 215, 215, 0.6000),
  [702] = Vertex.new(-237, 969, -211, 220, 215, 215, 0.6000),
})
local spiralStaircase = Model.new(2394, {
  [336] = Vertex.new(418, 2035, -555, 58, 54, 53),
  [360] = Vertex.new(685, 1874, -278, 58, 54, 53),
  [516] = Vertex.new(418, 2035, -555, 64, 59, 58),
  [519] = Vertex.new(385, 2080, -499, 64, 59, 58),
  [564] = Vertex.new(628, 1919, -248, 64, 59, 58),
})
local bust = Model.new(2304, {
  [421] = Vertex.new(-1, 638, -57, 113, 116, 123),
  [426] = Vertex.new(-6, 637, -49, 113, 116, 123),
  [429] = Vertex.new(3, 638, -58, 113, 116, 123),
  [431] = Vertex.new(8, 637, -49, 113, 116, 123),
  [1409] = Vertex.new(46, 517, -107, 129, 128, 81),
})
local trapdoor = Model.new(4122, {
  [144] = Vertex.new(-17, 846, -219, 42, 36, 21),
  [183] = Vertex.new(49, 947, -95, 127, 126, 97),
  [186] = Vertex.new(46, 931, -108, 127, 126, 97),
  [454] = Vertex.new(-27, 825, -250, 42, 36, 21),
  [577] = Vertex.new(4, 825, -259, 42, 36, 21),
})
local cupboard = Model.new(2091, {
  [987] = Vertex.new(415, 969, -133, 123, 105, 87),
  [993] = Vertex.new(-413, 969, -133, 123, 105, 87),
  [1269] = Vertex.new(-397, 1082, -133, 254, 254, 254),
  [1293] = Vertex.new(399, 1082, -133, 254, 254, 254),
  [1578] = Vertex.new(-413, 941, -133, 254, 254, 254),
})
local openCupboard = Model.new(3636, {
  [243] = Vertex.new(415, 969, -207, 123, 105, 87),
  [381] = Vertex.new(-413, 969, -207, 123, 105, 87),
  [981] = Vertex.new(399, 1082, -133, 254, 254, 254),
  [1125] = Vertex.new(-425, 941, -207, 254, 254, 254),
  [1218] = Vertex.new(-397, 1082, -207, 254, 254, 254),
})
local gloriousHole = Model.new(18, {
  [3] = Vertex.new(2313, 3113, 2064, 140, 111, 57),
  [6] = Vertex.new(2287, 3113, 1940, 140, 111, 57),
  [9] = Vertex.new(2380, 3175, 2064, 140, 111, 57),
  [12] = Vertex.new(2297, 3175, 1834, 140, 111, 57),
  [15] = Vertex.new(2464, 3051, 2064, 140, 111, 57),
})
local basementStatue = Model.new(3516, {
  [47] = Vertex.new(48, 916, 72, 89, 83, 91),
  [227] = Vertex.new(180, 384, -172, 72, 70, 77),
  [266] = Vertex.new(-128, 780, -64, 79, 77, 84),
  [492] = Vertex.new(92, 916, -24, 79, 77, 84),
  [641] = Vertex.new(180, 384, -172, 72, 70, 77),
})
local danglingRope = Model.new(600, {
  [227] = Vertex.new(1328, 196, 1244, 52, 41, 4),
  [516] = Vertex.new(1352, 1284, 1244, 52, 41, 4),
  [522] = Vertex.new(1348, 1288, 1228, 52, 41, 4),
  [524] = Vertex.new(1348, 1288, 1228, 52, 41, 4),
  [540] = Vertex.new(1340, 1300, 1256, 52, 41, 4),
})
local openDoors = Model.new(3552, {
  [51] = Vertex.new(-425, 1080, 270, 135, 123, 108),
  [696] = Vertex.new(-496, 1666, 211, 117, 108, 97),
  [2196] = Vertex.new(-435, 1061, 262, 87, 82, 78),
  [2730] = Vertex.new(-514, 1092, -196, 96, 88, 88),
  [3087] = Vertex.new(-295, 880, 589, 96, 88, 88),
})
local drawnShowerCurtain = Model.new(2952, {
  [2058] = Vertex.new(-1525, 1091, 548, 232, 230, 230),
  [2823] = Vertex.new(1536, 1093, 464, 164, 144, 105),
  [2838] = Vertex.new(-1520, 1112, 460, 180, 156, 112),
  [2847] = Vertex.new(1536, 1112, 468, 140, 117, 87),
  [2868] = Vertex.new(1536, 1091, 552, 117, 102, 80),
})
--#endRegion
--#region Quest Items
local clockHand = Model.new(276, {
  [26] = Vertex.new(126, 7, 19, 254, 254, 254),
  [28] = Vertex.new(126, 7, 19, 254, 254, 254),
  [30] = Vertex.new(126, 12, 19, 254, 254, 254),
  [44] = Vertex.new(126, 12, 19, 254, 254, 254),
  [48] = Vertex.new(126, 7, 19, 254, 254, 254),
})
local genericKey = Model.new(468, {
  [18] = Vertex.new(-17, 3, -125, 166, 132, 69),
  [150] = Vertex.new(-17, 3, -125, 188, 156, 98),
  [201] = Vertex.new(-35, 3, -79, 166, 132, 69),
  [282] = Vertex.new(-62, 3, -101, 166, 132, 69),
  [288] = Vertex.new(-62, 3, -101, 166, 132, 69),
})
local meat = Model.new(375, {
  [117] = Vertex.new(-57, 0, 67, 110, 104, 90),
  [141] = Vertex.new(-63, 0, -57, 107, 62, 59),
  [143] = Vertex.new(-63, 0, -57, 107, 62, 59),
  [149] = Vertex.new(-57, 0, 67, 107, 62, 59),
  [151] = Vertex.new(-57, 0, 67, 107, 62, 59),
})
local eyeGem = Model.new(156, {
  [6] = Vertex.new(-30, 54, -8, 14, 158, 72, 0.7490),
  [8] = Vertex.new(-30, 54, -8, 15, 168, 76, 0.7490),
  [105] = Vertex.new(-30, 54, -8, 20, 217, 98, 0.7490),
  [108] = Vertex.new(-30, 54, -8, 20, 217, 98, 0.7490),
  [116] = Vertex.new(25, 59, 3, 20, 217, 98, 0.7490),
})
local scrollFrag1 = Model.new(135, {
  [35] = Vertex.new(76, 0, -75, 192, 159, 100),
  [50] = Vertex.new(-88, 56, -83, 214, 186, 139),
  [132] = Vertex.new(-84, 51, -17, 214, 186, 139),
  [133] = Vertex.new(-84, 51, -17, 214, 186, 139),
  [134] = Vertex.new(-80, 52, -18, 214, 186, 139),
})
local scrollFrag2 = Model.new(198, {
  [26] = Vertex.new(84, 56, 84, 214, 186, 139),
  [28] = Vertex.new(84, 56, 84, 214, 186, 139),
  [42] = Vertex.new(84, 56, 84, 183, 152, 96),
  [141] = Vertex.new(94, 48, -51, 183, 152, 96),
  [143] = Vertex.new(94, 48, -51, 183, 152, 96),
})
local scrollFrag3 = Model.new(141, {
  [120] = Vertex.new(-84, 56, 96, 213, 185, 137),
  [138] = Vertex.new(-76, 52, 5, 213, 185, 137),
})
local breakCurse = Model.new(474, {
  [35] = Vertex.new(82, 0, -58, 191, 158, 98),
  [471] = Vertex.new(-72, 52, 5, 213, 185, 137),
})
local pipette = Model.multi({
  Model.new(108, {
    [12] = Vertex.new(24, 12, 48, 0, 0, 0),
    [81] = Vertex.new(13, 19, 51, 4, 5, 48),
    [100] = Vertex.new(24, 12, 48, 4, 5, 48),
    [105] = Vertex.new(24, 12, 48, 4, 5, 48),
    [107] = Vertex.new(24, 12, 48, 4, 5, 48),
  }),
  Model.new(108, {
    [74] = Vertex.new(-21, 4, -57, 147, 144, 135, 0.4000),
    [76] = Vertex.new(-21, 4, -57, 147, 144, 135, 0.4000),
    [81] = Vertex.new(-21, 4, -57, 147, 144, 135, 0.4000),
    [84] = Vertex.new(-21, 4, -57, 147, 144, 135, 0.4000),
    [97] = Vertex.new(-17, 6, -58, 147, 144, 135, 0.4000),
  }),
})
local pigPipette = Model.multi({
  Model.new(156, {
    [58] = Vertex.new(24, 12, 48, 4, 5, 47),
    [62] = Vertex.new(24, 12, 48, 4, 5, 47),
  }),
  Model.new(108, {
    [1] = Vertex.new(-21, 4, -57, 145, 142, 133, 0.4000),
    [6] = Vertex.new(-21, 4, -57, 145, 142, 133, 0.4000),
  }),
})
local alkalinePipe = Model.multi({
  Model.new(156, {
    [109] = Vertex.new(24, 12, 48, 4, 5, 47),
    [114] = Vertex.new(24, 12, 48, 4, 5, 47),
  }),
  Model.new(108, {
    [74] = Vertex.new(-21, 4, -57, 145, 142, 133, 0.4000),
    [76] = Vertex.new(-21, 4, -57, 145, 142, 133, 0.4000),
  }),
})
local hairbrush = Model.new(324, {
  [18] = Vertex.new(-12, 27, -123, 106, 75, 9),
  [228] = Vertex.new(-12, 27, -123, 106, 75, 9),
})
local notesOnInsanity = Model.new(489, {
  [9] = Vertex.new(24, 11, 77, 219, 200, 184),
  [141] = Vertex.new(60, 0, 21, 181, 138, 115),
})
--#endRegion

local upArrowTexture =
  "\x91\x91\x92\xf9\x2b\x22\x1c\xff\x3b\x33\x2c\xff\x35\x1c\x15\xff\x23\x0d\x07\xff\x34\x1c\x08\xff\xef\x9a\x2d\xff\xee\x8e\x21\xff\xee\x8e\x21\xff\x4b\x24\x0a\xff\xef\x9a\x2d\xff\xb2\x62\x18\xff\xea\x82\x18\xff\x36\x13\x06\xff\x36\x13\x06\xff\x36\x13\x06\xff\x68\x30\x0d\xff\x57\x26\x0a\xff\xbc\x67\x17\xff\xee\x8e\x21\xff\xee\x8e\x21\xff\xef\x9a\x2d\xff\x34\x1c\x08\xff\x23\x13\x0a\xff\x43\x33\x21\xff\x3b\x2d\x24\xff\x32\x2a\x24\xff\x91\x91\x92\xf9"
local middleCheckButtonTexture =
  "\xec\xaa\x26\xff\xec\xa9\x22\xff\xec\xa9\x24\xff\xec\xaa\x26\xff\xec\xaa\x26\xff\xed\xab\x28\xff\xed\xab\x28\xff\xec\xa9\x24\xff\xec\xa9\x24\xff\xec\xa8\x21\xff\xec\xa9\x22\xff\xed\xae\x2f\xff\xed\xae\x31\xff\xed\xab\x28\xff\xed\xab\x28\xff\xed\xad\x2c\xff\xed\xac\x2a\xff\xed\xab\x28\xff\xed\xab\x28\xff\xed\xab\x28\xff\xed\xac\x2a\xff\xec\xaa\x26\xff\xed\xab\x28\xff\xed\xab\x28\xff\xec\xaa\x26\xff"

local steps = {
  {
    text = "Talk to Maria north of the Archaeology Campus.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fort Forinthry lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3360, 1601, 3481) },
    postconditions = { Condition.ModelVisible:new(maria) },
  },
  {
    actions = { Action.ModelHighlight:new(maria) },
    jumpconditions = { Condition.ModelNotVisible:new(maria) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Maria.",
    actions = { Action.ModelHighlight:new(maria) },
    postconditions = {
      Condition.ConversationText:new("Be careful"),
      Condition.ConversationText:new("house is safe"),
    },
  },
  {
    text = "Enter the mansion.<ul><li>IMPORTANT: If you go in and out of a room to dodge the ghost, you have to manually go back a step.</li></ul>",
    actions = {
      Action.Direction:new(3363.4, 6685, 3469.5),
      Action.ConversationHighlight:new("Enter."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Open WEST door (safe).",
    title = "Getting raven key",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-9, 0, 11),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-10, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 30, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  { -- Coords in this room don't change
    text = "Pick up the raven key and clock hands.",
    actions = {
      Action.ModelHighlight:new(clockHandObj, { instanced = true }),
      Action.ModelHighlight:new(genericKeyObj, { instanced = true }),
    },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    actions = {
      Action.ModelHighlight:new(clockHandObj, { instanced = true }),
      Action.ModelHighlight:new(genericKeyObj, { instanced = true }),
    },
    postconditions = { Condition.InventoryContains:new(clockHand) },
  },
  {
    text = "Open EAST door.",
    actions = { Action.Direction:new(0.5, 200, 0, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door (safe).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, -30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Use the clock hands on the grandfather clock.",
    title = "Repairing grandfather clock and organising books",
    actions = {
      Action.ModelHighlight:new(clock, { instanced = true }),
      Action.InventoryHighlight:new(clockHand),
    },
    postconditions = { Condition.InventoryContains:new(eyeGem) },
  },
  {
    text = "Search the chest.",
    actions = { Action.ModelHighlight:new(foodChest, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(meat) },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 1, -3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door. (unbolt, then enter)",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(10, 30, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door (raven key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(18, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, -30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(4, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Repeat only: Open and search the chest (10).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(5, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    text = "Sort the pile of books.<ul><li>First pick any random order of books.</li><li>The order is from bottom to top.</li><li>Use the hints to find the correct order.</li></ul>",
    actions = { Action.ModelHighlight:new(bookPile, { instanced = true }) },
    postconditions = { Condition.Generic2DVisible:new(25, 27, 12, middleCheckButtonTexture) },
  },
  { postconditions = { Condition.InventoryContains:new(eyeGem, 3) } },
  {
    text = "Open WEST door.",
    title = "Heading to statue puzzle",
    actions = {
      Action.PathGuide:new({
        Location:new(3, 0, 1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-4, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(8, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 9),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH MIDDLE door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-3, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Push the statue WEST.",
    title = "Solving the statue puzzle",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-1, 0, 0, 0, true) },
  },
  {
    text = "Push the statue EAST.",
    actions = {
      Action.PathGuide:new({
        Location:new(-1, 0, 0),
        Location:new(-1, 0, -2),
        Location:new(0, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(0, 0, -2, 0, true) },
  },
  {
    text = "Push the statue WEST.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, -2),
        Location:new(0, 0, -3),
        Location:new(-1, 0, -3),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-1, 0, -3, 0, true) },
  },
  {
    text = "Push the statue SOUTH.",
    actions = {
      Action.PathGuide:new({
        Location:new(-1, 0, -3),
        Location:new(-1, 0, -5),
        Location:new(-2, 0, -5),
        Location:new(-2, 0, -6),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-2, 0, -6, 0, true) },
  },
  {
    text = "Push the statue EAST.",
    actions = {
      Action.PathGuide:new({
        Location:new(-2, 0, -6),
        Location:new(-2, 0, -5),
        Location:new(0, 0, -5),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(0, 0, -5, 0, true) },
  },
  {
    text = "Push the statue SOUTH.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, -5),
        Location:new(0, 0, -6),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(0, 0, -6, 0, true) },
  },
  {
    text = "Push the statue WEST.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, -6),
        Location:new(-1, 0, -6),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-1, 0, -6, 0, true) },
  },
  {
    text = "Take the fourth eye gem from the table.",
    actions = { Action.ModelHighlight:new(eyeGemObj, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(eyeGem, 4) },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 1, -6),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Repeat only: Open and search the chest (20).",
    title = "Getting spider key",
    actions = { Action.ModelHighlight:new(temporalChest, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    text = "Ascend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-8, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 30, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(9, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, -8),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search Ingram's research notes.",
    actions = { Action.Direction:new(-1, 40, -1, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(scrollFrag1) },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(-1, 40, -1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-WEST door.",
    title = "Getting statue key",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 8),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-9, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door to the upstairs lobby (unbolt).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, -7),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door (spider key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-17, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, -30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the dead servant.",
    actions = {
      Action.Direction:new(-1.5, 600, -3, { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InventoryContains:new(eyeGem, 5) },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(-3, 0, -3),
        Location:new(-4, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Draw back the curtain.",
    actions = {
      Action.ModelHighlight:new(curtain, { instanced = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ModelVisible:new(drawnShowerCurtain, { instance = true }) },
  },
  {
    text = "Talk to the faceless servant.",
    actions = {
      Action.ModelHighlight:new(facelessServant, { instanced = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Open EAST door.",
    title = "Heading to four statues",
    actions = {
      Action.PathGuide:new({
        Location:new(-3, 0, -1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(4, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-9, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 9),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-WEST door (statue key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(6, -30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Take the eye gem on the floor.",
    title = "Getting scythe key",
    actions = { Action.ModelHighlight:new(eyeGemObj2, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(eyeGem, 6) },
  },
  {
    text = "Investigate the statue twice. Progress all dialogue.",
    actions = { Action.ModelHighlight:new(eyeStatue, { instanced = true }) },
    postconditions = {
      Condition.ConversationText:new("unlocking"),
      Condition.ConversationText:new("both eyes"),
    },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the statue twice. Progress all dialogue.",
    actions = { Action.ModelHighlight:new(eyeStatue, { instanced = true }) },
    postconditions = {
      Condition.ConversationText:new("unlocking"),
      Condition.ConversationText:new("both eyes"),
    },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the statue twice. Progress all dialogue.",
    actions = { Action.ModelHighlight:new(eyeStatue, { instanced = true }) },
    postconditions = {
      Condition.ConversationText:new("unlocking"),
      Condition.ConversationText:new("both eyes"),
    },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Take the scythe key on the floor.",
    actions = { Action.ModelHighlight:new(scytheKeyObj, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(2, 0, 1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    title = "Heading to Saradomin shrine",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-6, 30, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, -9),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(10, 30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door (scythe key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(18, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-EAST door (safe).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(11, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the shrine.<ul><li>Search the chest for food.</li></ul>",
    title = "Getting skull key",
    actions = { Action.Direction:new(-1, 600, -4, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(-1, 0, -3),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "<b>Peek</b> WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-11, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.Generic2DVisible:new(28, 28, 14, upArrowTexture) },
  },
  { text = "Click 'Go Back'.", postconditions = { Condition.ChangedInstance:new() } },
  {
    text = "Open NORTH-EAST door (unbolt).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(11, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, 9),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-9, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Descend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, -30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-EAST door.",
    title = "Shattered windows",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(11, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, -9),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-9, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-WEST door.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-18, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door (skull key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-5, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-EAST door.",
    title = "Searching Ormod's scribblings",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search Ormod's scribblings.",
    actions = { Action.ModelHighlight:new(ormodsScribblings, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(scrollFrag2) },
  },
  {
    text = "Open WEST door.",
    actions = { Action.Direction:new(-0.5, 600, 0, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, 6),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    title = "Searching scorched skeleton",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, -30, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Play the grand piano.",
    actions = { Action.Direction:new(-2, 800, -0.5, { instance = true }) },
    postconditions = { Condition.ConversationText:new("hear a clunk nearby") },
  },
  {
    text = "Open SOUTH hidden door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-3, 0, -3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the scorched skeleton in SOUTH-EAST corner for an eye gem.",
    warning = "Dangerous area.",
    actions = { Action.ModelHighlight:new(scorchedSkeleton, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(eyeGem) },
  },
  {
    text = "Open NORTH hidden door.",
    actions = {
      Action.PathGuide:new({
        Location:new(3, 1, -4),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(3, 0, 3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(10, 30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Descend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, -4),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-WEST door.",
    title = "Walking through knives maze",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, 5),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the butler.",
    actions = { Action.Direction:new(-2, 200, 1, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(-1, 0, 1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-EAST door (cleaver key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(5, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Turn run <b>OFF</b>. Follow the path while dodging the flying knives.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 8, 1),
        Location:new(-1, 0, 1),
        Location:new(-1, 0, 3),
        Location:new(0, 0, 3),
        Location:new(0, 0, 4),
        Location:new(4, 0, 4),
        Location:new(4, 8, 2),
        Location:new(2, 8, 2),
        Location:new(2, 0, 0),
        Location:new(4, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH MIDDLE door.<ul><li>Turn run <b>ON</b>.</li></ul>",
    title = "Making alkaline concoction",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(4, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the chest of drawers.",
    actions = { Action.ModelHighlight:new(pipetteDrawer, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(pipette) },
  },
  {
    text = "Open SOUTH door.",
    actions = { Action.Direction:new(0, 600, -0.5, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(5, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Use the large pipette on the pig.",
    actions = {
      Action.Direction:new(-1, 600, 3, { instance = true }),
      Action.InventoryHighlight:new(pipette),
    },
    postconditions = {
      Condition.InventoryContains:new(pigPipette),
      Condition.ConversationText:new("You received the Large pipette of pig bile!"),
    },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 3),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, -3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the furnace.",
    actions = { Action.Direction:new(3.5, 600, 1, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["ashes"]) },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(2, 0, 0),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Use the pipette of pig bile on the cauldron.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-11, 0, -2),
      }, { instance = true }),
      Action.ModelHighlight:new(cauldron, { instanced = true }),
      Action.InventoryHighlight:new(pigPipette),
    },
    postconditions = { Condition.InventoryContains:new(pipette) },
  },
  {
    text = "Use the human ashes on the cauldron.",
    actions = {
      Action.ModelHighlight:new(cauldronGreen, { instanced = true }),
      Action.InventoryHighlight:new(Models.items["ashes"]),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["ashes"]) },
  },
  {
    text = "Use the large pipette on the cauldron.",
    actions = {
      Action.ModelHighlight:new(cauldronGreen, { instanced = true }),
      Action.InventoryHighlight:new(pipette),
    },
    postconditions = { Condition.InventoryContains:new(alkalinePipe) },
  },
  {
    text = "Use the alkaline concoction on the SOUTH door.",
    actions = {
      Action.InventoryHighlight:new(alkalinePipe),
      Action.ModelHighlight:new(slimyDoor, { instanced = true }),
    },
    postconditions = {
      Condition.ConversationText:new("You dissolved the ectoplasm with the alkaline concoction!"),
      Condition.InventoryDoesNotContain:new(alkalinePipe),
    },
  },
  {
    text = "Open SOUTH door.",
    title = "Getting doll key",
    actions = { Action.Direction:new(-9, 600, -4, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, -2),
      }, { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the frozen servant.",
    actions = {
      Action.ModelHighlight:new(frozenServant, { instanced = true }),
      Action.ConversationHighlight:new("Take the key?"),
      Action.ConversationHighlight:new("Pull the key free?"),
    },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(-4, 0, -1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  { --TODO: Fix when jumpconditions work
    text = "Repeat only:<ul><li>Open SOUTH-EAST door.</li><li>Open and search the chest (40).</li><li>Open WEST door.</li></ul>",
    title = "Getting tusks key",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, -5),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = { Action.ModelHighlight:new(temporalChest2, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(2, 0, -1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 5),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(0, 0, 5, 0, true) }, --TODO: kinda janky
  },
  {
    text = "Open NORTH-EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Take the snake key.",
    actions = { Action.Direction:new(5, 1000, 2.5, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(genericKey, 2) },
  },
  {
    text = "Climb the ladder (snake key).",
    actions = {
      Action.Direction:new(6, 800, 2, { instance = true }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Use CENTRE stairs.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({ --not tested
        Location:new(0, 0, 0),
        Location:new(-3, 0, 0),
        Location:new(-3, 0, -5),
        Location:new(-3, 0, -8),
        Location:new(-9, 0, -8),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(-9, 1952, -2, 20, true) },
  },
  {
    text = "Open WEST door.",
    warning = "Dangerous area.",
    actions = {
      Action.ResetInstance:new(),
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-9, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door (doll key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-11, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Ascend the stairs.",
    actions = { Action.ModelHighlight:new(spiralStaircase, { instanced = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Take the tusks key.",
    actions = { Action.ModelHighlight:new(tusksKeyObj, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Descend the stairs.",
    actions = { Action.Direction:new(-1, 0, -1, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    title = "Searching the thief",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, -30, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 9),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-EAST door (tusks key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(12, -30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the bust.",
    actions = { Action.ModelHighlight:new(bust, { instanced = true }) },
    postconditions = { Condition.DistanceTo:new(0, 0, -3, 0, true) },
  },
  {
    text = "Enter SOUTH-WEST trap door.",
    actions = { Action.ModelHighlight:new(trapdoor, { instanced = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the thief.",
    actions = { Action.Direction:new(0, 200, 1, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(eyeGem, 2) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(0, 600, 0, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0.5),
        Location:new(4, 0, 0.5),
        Location:new(4, 0, 6),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-WEST door.",
    title = "Heading to shrine of the son",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-6, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the statue twice. Progress all dialogue.",
    actions = { Action.ModelHighlight:new(eyeStatue, { instanced = true }) },
    postconditions = {
      Condition.ConversationText:new("unlocking"),
      Condition.ConversationText:new("both eyes"),
    },
  },
  {
    text = "Open EAST secret door (safe).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(3, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Take from the shrine of the son.",
    title = "Taking back mother's hairbrush",
    actions = { Action.Direction:new(1.5, 600, 1, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(hairbrush) },
  },
  {
    text = "Open WEST secret door.",
    actions = { Action.Direction:new(-0.5, 600, 0.15, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-3, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-6, 30, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, -9),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Ascend the stairs.",
    actions = { Action.ModelHighlight:new(spiralStaircase, { instanced = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to the emaciated spirit.",
    actions = { Action.ModelHighlight:new(lenian, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Open EAST door.",
    title = "Heading to hidden basement",
    actions = { Action.Direction:new(1.5, 600, 0, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(11, 30, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Move the body.<ul><li>Eat food if need be.</li></ul>",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(18, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(slumpedButler) },
  },
  {
    text = "Open EAST door.",
    warning = "Dangerous area.",
    actions = { Action.Direction:new(18.5, 0, -1, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-EAST door.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(11, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH-WEST door.",
    warning = "Dangerous area.",
    actions = { Action.Direction:new(-1, 30, 9, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door (noose key).",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-8, -30, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open and climb into the cupboard.",
    title = "Going through long corridor",
    actions = {
      Action.ModelHighlight:new(cupboard, { instanced = true }),
      Action.ModelHighlight:new(openCupboard, { instanced = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Peek through peephole along the wall.",
    actions = { Action.ModelHighlight:new(gloriousHole, { instanced = true }) },
    postconditions = { Condition.Generic2DVisible:new(28, 28, 14, upArrowTexture) },
  },
  { text = "Click 'Go Back'.", postconditions = { Condition.ChangedInstance:new() } },
  {
    text = "Open WEST door (unbolt).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-8, 0, 0),
        Location:new(-8, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  { --TODO: Fix when jumpconditions work
    text = "Repeat only:<ul><li>Open SOUTH-EAST door.</li><li>Open and search the chest (30).</li><li>Open WEST door.</li></ul>",
    title = "Getting to the ancient door puzzle",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    -- text = "debug",
    actions = { Action.ModelHighlight:new(temporalChest2, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    -- text = "debug",
    actions = {
      Action.PathGuide:new({
        Location:new(1, 0, 1),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    -- text = "debug",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 9),
      }, { instance = true }),
    },
    -- jumpconditions = { Condition.DistanceFrom:new(0, 0, 0, 20, true) },
    -- jumpOffset = 2,
    postconditions = { Condition.DistanceTo:new(0, 0, 9, 1, true) },
  },
  {
    text = "Open NORTH-EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 10),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Optional safe room:<ul><li>Open NORTH-WEST door.</li><li>Search the chest for food.</li><li>Open SOUTH door.</li></ul>",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, 5),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = { Action.Direction:new(0, 600, 0, { instance = true }) }, --TODO: fix coords
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = { Action.Direction:new(4, 0, -3, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(4, 0, -3, 1) },
  },
  {
    text = "Descend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(1, 0, 0),
        Location:new(1, 0, 4),
        Location:new(6, 0, 4),
        Location:new(6, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Descend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Move all statues EXCEPT the north statue.",
    title = "Solving ancient door puzzle",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 6),
        Location:new(5, 0, 6),
        Location:new(5, 0, 8),
        Location:new(7, 0, 8),
      }, { instance = true }),
      Action.PathGuide:new({
        Location:new(5, 0, 4),
        Location:new(7, 0, 4),
      }, { instance = true }),
      Action.PathGuide:new({
        Location:new(5, 0, 6),
        Location:new(5, 0, 2),
        Location:new(7, 0, 2),
      }, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(basementStatue, { instance = true, atLocation = Location:new(9, 0, 2) }),
    },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 6),
        Location:new(5, 0, 6),
        Location:new(5, 0, 8),
        Location:new(7, 0, 8),
      }, { instance = true }),
      Action.PathGuide:new({
        Location:new(5, 0, 4),
        Location:new(7, 0, 4),
      }, { instance = true }),
      Action.PathGuide:new({
        Location:new(5, 0, 6),
        Location:new(5, 0, 2),
        Location:new(7, 0, 2),
      }, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(basementStatue, { instance = true, atLocation = Location:new(9, 0, 4) }),
    },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 6),
        Location:new(5, 0, 6),
        Location:new(5, 0, 8),
        Location:new(7, 0, 8),
      }, { instance = true }),
      Action.PathGuide:new({
        Location:new(5, 0, 4),
        Location:new(7, 0, 4),
      }, { instance = true }),
      Action.PathGuide:new({
        Location:new(5, 0, 6),
        Location:new(5, 0, 2),
        Location:new(7, 0, 2),
      }, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(basementStatue, { instance = true, atLocation = Location:new(9, 0, 8) }),
    },
  },
  {
    text = "Stand on the MIDDLE yellow pressure plate.",
    actions = { Action.Direction:new(8, 0, 6, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(openDoors, { instance = true }) },
  },
  {
    text = "Descend the stairs behind the ancient door.",
    actions = {
      Action.PathGuide:new({
        Location:new(8, 0, 6),
        Location:new(16, 0, 6),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Repeat only: Open and search the chest (50).",
    title = "Climbing down the well",
    actions = { Action.ModelHighlight:new(temporalChest, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    text = "Descend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 10),
        Location:new(-2, 0, 10),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Descend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, -10),
        Location:new(2, 0, -10),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Climb down the mountaineering gear (safe).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(4, 0, 0),
        Location:new(5, 0, 2),
      }, { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Repeat only: Open and search the chest (60).",
    title = "Getting dagger key",
    actions = { Action.ModelHighlight:new(temporalChest2, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    text = "Open EAST door.<ul><li>Search NORTH chest for food.</li></ul>",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(13, 600, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search the reception desk.",
    actions = { Action.Direction:new(7, 500, 0, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(6, 0, 0),
        Location:new(6, 0, 3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 7),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Repeat only: Open and search the chest (70).",
    actions = { Action.ModelHighlight:new(temporalChest, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    text = "Open EAST door (chains key).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(15, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, -11),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(11, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Take the dagger key.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(4, 0, 0),
        Location:new(4, 0, -8),
      }, { instance = true }),
      Action.ModelHighlight:new(daggerKeyObj, { instance = true }),
    },
    postconditions = { Condition.InventoryContains:new(genericKey) },
  },
  {
    text = "Repeat only: Open and search the chest (80).",
    actions = { Action.ModelHighlight:new(temporalChest, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(4, 0, -8),
        Location:new(4, 0, 0),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-11, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, 11),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-15, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, -7),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door (safe).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, -3),
        Location:new(-6, 0, -3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Climb the rope.",
    title = "Leaving hidden basement",

    actions = { Action.ModelHighlight:new(danglingRope, { instanced = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Ascend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, -2),
        Location:new(-4, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Ascend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 10),
        Location:new(1, 0, 10),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Ascend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, -10),
        Location:new(-1, 0, -10),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Ascend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-15, 0, 0),
        Location:new(-15, 0, -7),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Ascend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, 3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  { --TODO: Fix when jumpconditions works
    text = "Optional safe room:<ul><li>Open NORTH-WEST door.</li><li>Search the chest for food.</li><li>Open SOUTH door.</li></ul>",
    title = "Back to grandfather clock",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 2),
        Location:new(-4, 0, 2),
        Location:new(-4, 0, 3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = { Action.Direction:new(0, 600, 0, { instance = true }) }, --TODO: fix coords
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, -6),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-1, 0, -6, 1, true) },
  },
  {
    text = "Ascend the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 2),
        Location:new(-5, 0, 2),
        Location:new(-5, 0, -3),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, 4),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH-WEST door.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-9, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, -7),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open SOUTH door (safe).",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-5, -30, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Open EAST door.",
    title = "Breaking the curse",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(10, 0, 0),
        Location:new(10, 0, -2),
        Location:new(10.5, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Use the CENTRE stairs.",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(2, 0, 0),
        Location:new(2, 0, -2),
        Location:new(9, 0, -2),
        Location:new(9, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(9, 1952, 4, 10, true) },
  },
  {
    text = "Open EAST door.",
    warning = "Dangerous area.",
    actions = {
      Action.ResetInstance:new(),
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(8, 0, 0),
        Location:new(8, 0, -1),
        Location:new(9.5, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "<b>Quickly</b> open SOUTH-WEST door (dagger key).",
    warning = "Dangerous area.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Search Ingram's rantings.",
    actions = { Action.ModelHighlight:new(finalNotes, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(scrollFrag3) },
  },
  {
    text = "Combine the scroll fragments.",
    actions = { Action.InventoryHighlight:new(notesOnInsanity) },
    postconditions = { Condition.InventoryContains:new(breakCurse) },
  },
  { --TODO: Fix when jumpconditions work
    text = "Repeat only:<ul><li>Open EAST door.</li><li>Open and search the chest (90).</li><li>Open WEST door.</li></ul>",
    title = "Dialogue with Ormod",
    actions = {
      Action.PathGuide:new({
        Location:new(1, 0, 0),
        Location:new(3, 0, -2),
        Location:new(3.5, 0, -2),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = { Action.ModelHighlight:new(temporalChest2, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(openedChest, { instance = true }) },
  },
  {
    actions = { Action.Direction:new(0, 600, 0, { instance = true }) }, --TODO: fix coords
    postconditions = { Condition.DistanceFrom:new(0, 0, 0, 10, true) },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-3, 0, 2),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-3, 0, 2, 1, true) },
  },
  {
    text = "Open NORTH door.",
    actions = {
      Action.PathGuide:new({
        Location:new(1, 0, 0),
        Location:new(0, 0, 0),
        Location:new(0, 0, 0.5),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Run EAST down the hall.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(10, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(10, 0, 1, 1, true) },
  },
  {
    text = "Use the scroll on the monster.",
    actions = {
      Action.ModelHighlight:new(dukeSucc, { instanced = true }),
      Action.InventoryHighlight:new(breakCurse),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(breakCurse) },
  },
  {
    text = "Talk to the monster.",
    actions = {
      Action.ModelHighlight:new(dukeSucc, { instanced = true }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("recall the layout") },
  },
  {
    text = "Open WEST door.",
    actions = {
      Action.PathGuide:new({
        Location:new(5, 0, 1),
        Location:new(-1.5, 0, 1),
      }, { instance = true }),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Use the CENTRE stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-1, 0, 0),
        Location:new(-1, 0, 1),
        Location:new(-9, 0, 1),
        Location:new(-9, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(-9, -1952, -5, 20, true) },
  },
  {
    text = "Talk to Lenian.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(lenian, { instanced = true }),
    },
    postconditions = { Condition.ConversationText:new("finishes") },
  },
  {
    text = "Exit the mansion.",
    title = "Finishing up",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, -3),
        Location:new(0, 0, -9.5),
      }, { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Maria.",
    actions = { Action.ModelHighlight:new(maria) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Broken Home",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.mediumlong,
  releaseDate = 1414368000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
