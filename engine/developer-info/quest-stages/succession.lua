local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local khazardGuard = Model.new(4923, {
  [2479] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [2487] = Vertex.new(2, 725, -59, 109, 81, 57),
  [2489] = Vertex.new(7, 724, -51, 109, 81, 57),
  [3612] = Vertex.new(-31, 764, -6, 57, 53, 53),
  [3615] = Vertex.new(29, 764, -22, 57, 53, 53),
})
local ka = Model.new(3822, {
  [63] = Vertex.new(-25, 158, -134, 255, 255, 255),
  [87] = Vertex.new(25, 158, -134, 255, 255, 255),
  [624] = Vertex.new(0, 162, -156, 255, 255, 255),
  [957] = Vertex.new(0, 160, -162, 255, 255, 255),
  [1056] = Vertex.new(0, 160, -162, 255, 255, 255),
})
local theMagister = Model.new(7626, {
  [466] = Vertex.new(-160, 435, -29, 127, 127, 127),
  [811] = Vertex.new(17, 875, -45, 127, 128, 128),
  [2312] = Vertex.new(-15, 892, -52, 127, 127, 127),
  [2398] = Vertex.new(-146, 419, -21, 127, 127, 127),
  [2399] = Vertex.new(-149, 435, 18, 127, 127, 127),
})
local adrasteiaTheForgotten = Model.new(23166, {
  [5998] = Vertex.new(52, 451, -52, 127, 127, 127),
  [7603] = Vertex.new(127, 415, -19, 128, 127, 128),
  [7613] = Vertex.new(-127, 415, -19, 128, 127, 128),
  [22339] = Vertex.new(56, 612, -49, 127, 127, 127),
  [22432] = Vertex.new(-56, 612, -49, 127, 127, 127),
})
local vengeance = Model.new(17802, {
  [4807] = Vertex.new(127, 415, -19, 128, 127, 128),
  [4811] = Vertex.new(127, 415, 18, 128, 127, 128),
  [4817] = Vertex.new(-127, 415, -19, 128, 127, 128),
  [12015] = Vertex.new(34, 452, -52, 127, 127, 127),
  [12023] = Vertex.new(-34, 452, -52, 127, 127, 127),
})
local forgottenArcher = Model.new(4362, {
  [2595] = Vertex.new(24, 738, -38, 30, 28, 27),
  [2619] = Vertex.new(-24, 738, -38, 30, 28, 27),
  [3226] = Vertex.new(0, 739, -3, 27, 139, 126),
  [3228] = Vertex.new(0, 736, -4, 27, 139, 126),
  [4357] = Vertex.new(132, 381, -14, 131, 96, 67),
})
local necrolord = Model.new(9351, {
  [8827] = Vertex.new(0, 739, -1, 54, 142, 28),
  [8828] = Vertex.new(0, 736, -1, 54, 142, 28),
  [8829] = Vertex.new(0, 736, -2, 54, 142, 28),
  [8830] = Vertex.new(126, 380, -15, 54, 142, 28),
  [8832] = Vertex.new(124, 380, -17, 54, 142, 28),
})
local kolodion = Model.new(2961, {
  [2768] = Vertex.new(-8, 812, -76, 0, 0, 0),
  [2799] = Vertex.new(32, 832, -84, 0, 0, 0),
  [2959] = Vertex.new(0, 789, -7, 27, 139, 126),
  [2960] = Vertex.new(0, 786, -7, 27, 139, 126),
  [2961] = Vertex.new(0, 786, -8, 27, 139, 126),
})
local adrasteiaWhiteOutfit = Model.new(55662, {
  [5723] = Vertex.new(14, 710, -25, 127, 128, 128),
  [5749] = Vertex.new(10, 710, -25, 127, 128, 128),
  [5788] = Vertex.new(-14, 710, -25, 127, 128, 128),
  [5816] = Vertex.new(-10, 710, -25, 127, 128, 128),
  [55369] = Vertex.new(202, 400, -30, 128, 128, 128),
})
local adrasteiaWhiteOutfitCrown = Model.new(57774, {
  [7883] = Vertex.new(14, 710, -25, 127, 128, 128),
  [7909] = Vertex.new(10, 710, -25, 127, 128, 128),
  [7948] = Vertex.new(-14, 710, -25, 127, 128, 128),
  [7976] = Vertex.new(-10, 710, -25, 127, 128, 128),
  [57481] = Vertex.new(202, 400, -30, 128, 128, 128),
})
--#endregion
--#region Objects
local largeFootprint = Model.new(48, {
  [2] = Vertex.new(162, 2, 80, 127, 127, 127),
  [4] = Vertex.new(162, 2, 80, 127, 127, 127),
  [19] = Vertex.new(-158, 2, 80, 127, 127, 127),
})
local bodyOfDemonCultist = Model.new(9774, {
  [2755] = Vertex.new(-76, 643, 19, 127, 127, 127),
  [2764] = Vertex.new(76, 643, 19, 127, 127, 127),
  [2767] = Vertex.new(0, 735, -7, 127, 127, 127),
  [2768] = Vertex.new(0, 732, -7, 127, 127, 127),
  [2769] = Vertex.new(0, 732, -8, 127, 127, 127),
})
local scratchMarks = Model.new(528, {
  [1] = Vertex.new(858, 23, 344, 76, 70, 70),
  [133] = Vertex.new(-827, 23, 215, 76, 70, 70),
  [199] = Vertex.new(927, 23, 303, 76, 70, 70, 0.05882),
  [218] = Vertex.new(-1045, 23, 278, 76, 70, 70, 0.05882),
  [221] = Vertex.new(-1045, 23, 278, 76, 70, 70, 0.05882),
})
local crateWithNote = Model.new(735, {
  [559] = Vertex.new(252, 139, -128, 19, 16, 15),
  [565] = Vertex.new(233, 145, -176, 19, 16, 15),
  [569] = Vertex.new(233, 145, -176, 19, 16, 15),
  [577] = Vertex.new(82, 146, 256, 19, 16, 15),
  [589] = Vertex.new(-158, 120, 125, 19, 16, 15),
})
local longChest = Model.new(2715, {
  [884] = Vertex.new(468, 217, -158, 254, 254, 254),
  [890] = Vertex.new(535, 218, -110, 254, 254, 254),
  [1174] = Vertex.new(536, 192, 125, 254, 254, 254),
  [1342] = Vertex.new(536, 240, 125, 254, 254, 254),
  [1426] = Vertex.new(536, 183, 125, 254, 254, 254),
})
local wideCrate = Model.new(3405, {
  [623] = Vertex.new(546, 230, -159, 254, 254, 254),
  [784] = Vertex.new(522, 285, -164, 254, 254, 254),
  [1346] = Vertex.new(551, 230, 147, 254, 254, 254),
  [2212] = Vertex.new(521, 289, 159, 254, 254, 254),
  [3113] = Vertex.new(527, 341, -151, 254, 254, 254),
})
local bookcase = Model.new(2181, {
  [158] = Vertex.new(458, -816, 116, 141, 50, 44),
  [179] = Vertex.new(-452, -1165, 116, 109, 75, 45),
  [356] = Vertex.new(-512, -1280, 286, 163, 150, 150),
  [374] = Vertex.new(512, -1280, 286, 163, 150, 150),
  [617] = Vertex.new(-512, 1274, -335, 186, 186, 186),
})
local imbuedBones = Model.new(3714, {
  [864] = Vertex.new(-65, 72, 92, 187, 187, 187),
  [979] = Vertex.new(-65, 72, 92, 187, 187, 187),
  [1061] = Vertex.new(-76, 69, 59, 187, 187, 187),
  [1637] = Vertex.new(-65, 72, 92, 187, 187, 187),
  [1955] = Vertex.new(-69, 15, 135, 187, 187, 187),
})
local fragmentsOfAnObelisk = Model.new(258, {
  [48] = Vertex.new(-20, 204, -62, 128, 128, 128),
  [61] = Vertex.new(86, 137, -112, 128, 128, 128),
  [72] = Vertex.new(86, 137, -112, 128, 128, 128),
  [150] = Vertex.new(86, 137, -112, 128, 128, 128),
  [216] = Vertex.new(-103, -81, -129, 128, 128, 128),
})
local soulObelisk = Model.new(3138, {
  [42] = Vertex.new(45, 3160, -137, 40, 17, 187),
  [1083] = Vertex.new(45, 3160, -137, 177, 177, 177),
  [1275] = Vertex.new(45, 3160, -137, 177, 177, 177),
  [1589] = Vertex.new(-262, 2294, 611, 177, 177, 177),
  [2175] = Vertex.new(255, 2885, 255, 177, 177, 177),
})
local scorchMarks = Model.new(414, {
  [13] = Vertex.new(-652, 18, -652, 63, 58, 58),
  [22] = Vertex.new(-652, 16, 652, 63, 58, 58),
  [53] = Vertex.new(652, 16, -652, 63, 58, 58),
  [96] = Vertex.new(652, 33, 652, 63, 58, 58),
  [98] = Vertex.new(652, 33, 652, 63, 58, 58),
})
local soulAltar = Model.new(3405, {
  [3112] = Vertex.new(793, 0, -196, 55, 54, 50),
  [3240] = Vertex.new(-482, 0, 699, 55, 54, 50),
  [3287] = Vertex.new(482, 0, -699, 55, 54, 50),
  [3347] = Vertex.new(615, 0, -556, 55, 54, 50),
  [3366] = Vertex.new(-482, 0, 699, 55, 54, 50),
})
local argonlikeRock = Model.new(3678, {
  [207] = Vertex.new(-108, 587, 77, 127, 127, 127),
  [1617] = Vertex.new(-108, 587, 77, 127, 127, 127),
  [1668] = Vertex.new(-108, 587, 77, 127, 127, 127),
  [3594] = Vertex.new(181, -377, -193, 127, 127, 127),
  [3666] = Vertex.new(290, -361, 119, 127, 127, 127),
})
local scorchMarks2 = Model.new(96, {
  [49] = Vertex.new(-249, 28, -249, 60, 55, 55),
  [75] = Vertex.new(0, 28, -249, 60, 55, 55),
  [77] = Vertex.new(124, 28, -249, 60, 55, 55),
  [93] = Vertex.new(249, 28, -249, 60, 55, 55),
  [95] = Vertex.new(249, 28, -249, 60, 55, 55),
})
local ritualMarker = Model.new(8160, {
  [1397] = Vertex.new(-580, 1680, 57, 127, 127, 127),
  [2363] = Vertex.new(580, 1680, 57, 127, 127, 127),
  [5490] = Vertex.new(-48, 1886, 6, 127, 127, 127),
  [5719] = Vertex.new(-489, 1638, 61, 127, 127, 127),
  [5723] = Vertex.new(-489, 1638, 61, 127, 127, 127),
})
local runePouch = Model.new(912, {
  [39] = Vertex.new(-26, 181, -48, 127, 127, 127),
  [53] = Vertex.new(17, 178, -54, 127, 127, 127),
  [56] = Vertex.new(17, 178, -54, 127, 127, 127),
  [598] = Vertex.new(17, 178, -54, 127, 127, 127),
  [634] = Vertex.new(53, 181, -5, 127, 127, 127),
})
local whiteDust = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 145, 132, 111),
  [34] = Vertex.new(100, 0, 60, 145, 132, 111),
  [42] = Vertex.new(100, 0, 60, 145, 132, 111),
  [64] = Vertex.new(132, 0, -24, 145, 132, 111),
  [69] = Vertex.new(112, 0, -32, 145, 132, 111),
})
local sharpRock = Model.new(3036, {
  [1682] = Vertex.new(-6, -400, -46, 127, 127, 127),
  [1738] = Vertex.new(-6, -392, -211, 127, 127, 127),
  [1852] = Vertex.new(-200, -274, -246, 127, 127, 127),
  [1958] = Vertex.new(-199, -263, -11, 127, 127, 127),
  [2737] = Vertex.new(-192, -62, 242, 127, 127, 127),
})
local sparklingPool = Model.new(2646, {
  [1] = Vertex.new(504, 0, 504, 71, 69, 76),
  [5] = Vertex.new(-504, 0, -504, 71, 69, 76),
  [1759] = Vertex.new(-590, -3, 520, 127, 127, 127),
  [1799] = Vertex.new(-621, -1, 501, 127, 127, 127),
  [2515] = Vertex.new(666, 10, 447, 127, 127, 127),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local argonlikeBar = Model.new(234, {
  [71] = Vertex.new(-204, 0, 128, 38, 41, 60),
  [75] = Vertex.new(-204, 0, 128, 38, 41, 60),
  [209] = Vertex.new(-124, 0, 180, 38, 41, 60),
  [213] = Vertex.new(-124, 0, 180, 38, 41, 60),
  [217] = Vertex.new(-204, 0, 128, 38, 41, 60),
})
local argonlikeHelm = Model.multi({
  Model.new(3498, {
    [2429] = Vertex.new(11, 62, -57, 127, 127, 127),
    [2695] = Vertex.new(-11, 62, -57, 127, 127, 127),
    [2723] = Vertex.new(-53, 130, 51, 127, 127, 127),
    [2726] = Vertex.new(-53, 130, 51, 127, 127, 127),
    [3007] = Vertex.new(53, 130, 51, 127, 127, 127),
  }),
  Model.new(12, {
    [3] = Vertex.new(29, 2, -26, 128, 127, 128, 0.000),
    [5] = Vertex.new(24, 2, 0, 128, 127, 128, 0.000),
    [6] = Vertex.new(29, 2, -26, 128, 127, 128, 0.000),
    [9] = Vertex.new(-29, 2, -26, 128, 127, 128, 0.000),
    [12] = Vertex.new(-29, 2, -26, 128, 127, 128, 0.000),
  }),
})
local argonlikePlatebody = Model.new(5322, {
  [542] = Vertex.new(142, 1, 158, 127, 127, 127),
  [1803] = Vertex.new(32, 33, -114, 127, 127, 127),
  [1811] = Vertex.new(-32, 33, -114, 127, 127, 127),
  [1951] = Vertex.new(66, 6, -97, 127, 127, 127),
  [1980] = Vertex.new(-66, 6, -97, 127, 127, 127),
})
local argonlikePlatelegs = Model.new(5112, {
  [499] = Vertex.new(52, 34, 189, 127, 127, 127),
  [1093] = Vertex.new(-52, 34, 189, 127, 127, 127),
  [1897] = Vertex.new(66, 6, 202, 127, 127, 127),
  [1906] = Vertex.new(61, 0, -195, 128, 127, 128),
  [1921] = Vertex.new(-66, 6, 202, 127, 127, 127),
})
local argonlikeGauntlets = Model.new(2556, {
  [5] = Vertex.new(35, 8, 66, 128, 127, 128),
  [7] = Vertex.new(-49, 8, 29, 128, 127, 128),
  [1000] = Vertex.new(55, 33, 37, 127, 127, 127),
  [1060] = Vertex.new(62, 25, 42, 127, 127, 127),
  [1064] = Vertex.new(62, 25, 42, 127, 127, 127),
})
local argonlikeBoots = Model.new(1236, {
  [373] = Vertex.new(67, 6, -80, 127, 127, 127),
  [377] = Vertex.new(67, 6, -80, 127, 127, 127),
  [406] = Vertex.new(75, -1, -38, 127, 127, 127),
  [659] = Vertex.new(-31, 6, -87, 127, 127, 127),
  [663] = Vertex.new(-31, 6, -87, 127, 127, 127),
})
local argonlikeBattleaxe = Model.new(411, {
  [20] = Vertex.new(-56, 20, -168, 102, 89, 65),
  [77] = Vertex.new(-36, 20, -188, 56, 49, 36),
  [80] = Vertex.new(-36, 20, -188, 56, 49, 36),
  [169] = Vertex.new(36, 20, 136, 98, 105, 154),
  [173] = Vertex.new(36, 20, 136, 98, 105, 154),
})
local argonlikeKiteshield = Model.new(795, {
  [29] = Vertex.new(-132, 12, 200, 79, 66, 51),
  [46] = Vertex.new(156, 12, 200, 63, 53, 40),
  [49] = Vertex.new(156, 12, 200, 63, 53, 40),
  [116] = Vertex.new(-132, 12, 200, 102, 85, 65),
  [152] = Vertex.new(156, 12, 200, 88, 73, 56),
})
local emptyRunePouch = Model.multi({
  Model.new(912, {
    [69] = Vertex.new(149, 261, -94, 127, 127, 127),
    [72] = Vertex.new(149, 261, -94, 127, 127, 127),
    [73] = Vertex.new(149, 261, -94, 127, 127, 127),
    [577] = Vertex.new(149, 261, -94, 127, 127, 127),
    [581] = Vertex.new(149, 261, -94, 127, 127, 127),
  }),
  Model.new(30, {
    [4] = Vertex.new(-91, 248, -66, 105, 79, 66),
    [11] = Vertex.new(113, 248, 63, 105, 79, 66),
    [17] = Vertex.new(113, 248, -66, 105, 79, 66),
    [19] = Vertex.new(113, 248, 63, 105, 79, 66),
    [22] = Vertex.new(113, 248, -66, 105, 79, 66),
  }),
})
local oneMeaslyFireRune = Model.new(327, {
  [1] = Vertex.new(44, 0, 104, 94, 87, 86),
  [2] = Vertex.new(64, 0, 60, 94, 87, 86),
  [6] = Vertex.new(64, 0, 60, 94, 87, 86),
  [72] = Vertex.new(-8, 0, 136, 94, 87, 86),
  [75] = Vertex.new(-8, 0, 136, 94, 87, 86),
})
local inertFireRuneFragments = Model.new(462, {
  [207] = Vertex.new(192, 0, -192, 40, 40, 36),
  [215] = Vertex.new(168, 0, -216, 40, 40, 36),
  [218] = Vertex.new(192, 0, -192, 40, 40, 36),
  [224] = Vertex.new(192, 0, -192, 40, 40, 36),
  [225] = Vertex.new(168, 0, -216, 40, 40, 36),
})
local volatileFireRuneFragments = Model.new(576, {
  [97] = Vertex.new(-204, 0, -124, 64, 22, 19),
  [100] = Vertex.new(-164, 0, -152, 64, 22, 19),
  [103] = Vertex.new(-204, 0, -124, 64, 22, 19),
  [106] = Vertex.new(-168, 0, -92, 64, 22, 19),
  [111] = Vertex.new(-164, 0, -152, 64, 22, 19),
})
local soggyPouchOfGreyPaste = Model.new(270, {
  [141] = Vertex.new(-140, 380, 20, 61, 48, 5),
  [142] = Vertex.new(-144, 352, 28, 61, 48, 5),
  [146] = Vertex.new(-140, 380, 20, 61, 48, 5),
  [148] = Vertex.new(-144, 352, 28, 61, 48, 5),
  [200] = Vertex.new(-140, 380, 20, 61, 48, 5),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Adrasteia in the White Knights' Castle's throne room.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.Direction:new(2993, 2725, 3341, { distance = 11 }),
      Action.ModelHighlight:new(Models.objects["falador ground floor ladder"], { distance = 12 }),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2993, 3909, 3341, 3),
      Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5), --already in throne room
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["falador first floor staircase"]) },
    postconditions = { Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["adrasteia"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Adrasteia.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("go to Senntisten") }, --not tested
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"]),
      Action.ConversationHighlight:new("Let's continue our journey."),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Enter the ancient doors in the Varrock Dig Site.",
    title = "Clues in the prison",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Archaeology Journal",
      url = "Archaeology_journal.png",
    },
    neededItems = {},
    recommendedItems = { ["Archaeology journal"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3333.5, 1269, 3453.5),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Discover the large footprint downstairs. (1/5)<ul><li>All discoverable entities in this quest should be marked by a red dot on your minimap.</li></ul>",
    actions = { Action.ModelHighlight:new(largeFootprint) },
    postconditions = { Condition.ChatText:new("clue: 1/5") },
  },
  {
    text = "Go up the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(-10, 0, 1),
        Location:new(-22, 0, 1),
        Location:new(-26, 1504, 1),
        Location:new(-26, 2412, 0),
        Location:new(-26, 2400, -3),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(-26, 2400, -3, 1, true) },
  },
  {
    text = "Discover one of the bodies of demon cultists. (2/5)",
    actions = { Action.ModelHighlight:new(bodyOfDemonCultist, { instanced = true, highlightPriority = "closest" }) },
    postconditions = { Condition.ChatText:new("clue: 2/5") },
  },
  {
    text = "Discover scratch marks. (3/5)",
    actions = { Action.ModelHighlight:new(scratchMarks, { instance = true }) },
    postconditions = { Condition.ChatText:new("clue: 3/5") },
  },
  {
    text = "Discover ancient Zarosian architecture. (4/5)",
    actions = { Action.Direction:new(-10.5, 3600, -16.5, { instance = true }) },
    postconditions = { Condition.ChatText:new("clue: 4/5") },
  },
  {
    text = "Discover note from a worker. (5/5)",
    actions = { Action.ModelHighlight:new(crateWithNote, { instance = true }) },
    postconditions = { Condition.ChatText:new("clue: 5/5") },
  },
  {
    text = "Talk to Adrasteia again to continue.<ul><li>You must dismiss followers for the following steps.</li></ul>",
    actions = { Action.ModelHighlight:new(Models.npcs["adrasteia"], { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to one of the Khazard guards.",
    title = "Khazard",
    actions = { Action.ModelHighlight:new(khazardGuard, { instance = true, highlightPriority = "closest" }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to one of the guards again.",
    actions = { Action.ModelHighlight:new(khazardGuard, { instance = true, highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationText:new("ritual circle myself") },
  },
  {
    text = "Reset the puzzle with one of the long chests until you're prompted to skip the puzzle.",
    actions = {
      Action.ModelHighlight:new(longChest, { instance = true }),
      Action.ConversationHighlight:new("Reset to starting position."),
      Action.ConversationHighlight:new("Skip this puzzle (2 attempts)."),
    },
    postconditions = { Condition.ConversationText:new("new ritual marker") },
  },
  {
    text = "Talk to Adrasteia.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"], { instance = true }),
      Action.ConversationHighlight:new("Yes, I'm ready to move on."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Enter the Grand Library in Menaphos.",
    title = "Clues in Menaphos",
    neededItems = {},
    recommendedItems = {},
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Menaphos lodestone",
      url = "Menaphos_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3171, 8765, 2709.5),
      Action.ConversationHighlight:new("Succession"),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  --coordinates after this step might be slightly off because i got them after leaving and rejoining a few times.
  {
    text = "Talk to Adrasteia in the library.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"]),
      Action.ConversationHighlight:new("Farewell."),
    },
    postconditions = { Condition.ConversationText:new("any damage she caused") },
  },
  {
    text = "Discover The Corruption on the first level from top, near the south-eastern corner. (1/3)",
    actions = {
      Action.Direction:new(-4.5, -1760, -17.5, { instance = true }),
      -- Action.ModelHighlight:new(bookcase, { instance = true, atLocation = Location:new(-4.5, -3160, -17.5) }),
    },
    postconditions = { Condition.ChatText:new("1/9") },
  },
  {
    text = "Discover The Secret Chamber opposite The Corruption, in the south-western corner. (2/3)",
    actions = { Action.Direction:new(-51.5, -2260, -19, { instance = true }) },
    postconditions = { Condition.ChatText:new("2/9") },
  },
  {
    text = "Discover The Living Dead on the bottom level, near the north-eastern corner. (3/3)",
    actions = { Action.Direction:new(-21, -6680, 11.5, { instance = true }) },
    postconditions = { Condition.ChatText:new("3/9") },
  },
  {
    text = "Travel to the Magister's chamber.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"], { instance = true }),
      Action.ConversationHighlight:new("Travel to the Magister's chamber. (0/3)"),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  { postconditions = { Condition.ConversationText:new("look destroyed") } },
  {
    text = "Discover the imbued bones on the western side of the chamber. (1/3)",
    actions = {
      Action.ResetInstance:new(), --Needed. Do not remove.
      Action.ModelHighlight:new(imbuedBones, { instance = true }),
    },
    postconditions = { Condition.ChatText:new("4/9") },
  },
  {
    text = "Discover the fragments of an obelisk next to the soul obelisk. (2/3)",
    actions = { Action.ModelHighlight:new(fragmentsOfAnObelisk, { instance = true }) },
    postconditions = { Condition.ChatText:new("5/9") },
  },
  {
    text = "Touch the soul obelisk.",
    actions = { Action.ModelHighlight:new(soulObelisk, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Magister is indisposed") }, --not tested
  },
  {
    text = "Climb the stairs.",
    actions = { Action.Direction:new(10.5, 800, 31, { instance = true }) },
    postconditions = {
      Condition.DistanceToWithHeight:new(-11, 1696, 36, 4, true), --west stairs
      Condition.DistanceToWithHeight:new(13, 1688, 36, 4, true), --east stairs
    },
  },
  {
    text = "Discover the magical residue. (3/3)",
    actions = { Action.Direction:new(0, 1696, 41, { instance = true, tile = true }) },
    postconditions = { Condition.ChatText:new("6/9") },
  },
  {
    text = "Travel to the Soul Altar.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"], { instance = true }),
      Action.ConversationHighlight:new("Travel to the Soul Altar. (0/3)"),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Discover the scorch marks. (1/3)",
    actions = { Action.ModelHighlight:new(scorchMarks, { instance = true }) },
    postconditions = { Condition.ChatText:new("7/9") },
  },
  {
    text = "Discover the Soul Altar. (2/3)",
    actions = { Action.ModelHighlight:new(soulAltar, { instance = true }) },
    postconditions = { Condition.ChatText:new("8/9") },
  },
  {
    text = "Talk to Ka. (3/3)",
    actions = { Action.ModelHighlight:new(ka) },
    postconditions = { Condition.ChatText:new("9/9") },
  },
  {
    text = "Talk to Adrasteia.<ul><li>You must dismiss followers and have no items for the following steps. Adrasteia can bank your items for you.</li></ul>",
    title = "Vengeance and the Magister",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["adrasteia"]) },
    postconditions = { Condition.ConversationText:new("got so far.") }, --not tested
  },
  {
    text = "Talk to the Magister.",
    actions = { Action.ModelHighlight:new(theMagister) },
    postconditions = { Condition.ConversationText:new("burning inside her") },
  },
  {
    text = "Defeat the Magister.<ul><li>All prayers are disabled. Use abilities in order to win the fight.</li><li>If you die, you will be teleported to Menaphos gate. Enter the gate to return to the quest.</li><li>If you fail to defeat him twice, there will be an option to skip the combat phase.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes, and skip this combat encounter (2 attempts)."),
    },
    postconditions = { Condition.ConversationText:new("Zamorakians are researching") },
  },
  {
    text = "Talk to Adrasteia.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"]),
      Action.ConversationHighlight:new("Yes, I'm ready to move on."),
    },
    postconditions = { Condition.ConversationText:new("throne room in Falador") },
  },
  {
    text = "Talk to Adrasteia in the White Knights' Castle's throne room.",
    title = "Preparing disguise in the Wilderness",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = { ["Wilderness sword"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(2993, 2725, 3341, { distance = 11 }),
      Action.ModelHighlight:new(Models.objects["falador ground floor ladder"], { distance = 12 }),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2993, 3909, 3341, 3),
      Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5), --already in throne room
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["falador first floor staircase"]) },
    postconditions = { Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"]),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Never mind."),
    },
    postconditions = { Condition.ConversationText:new("let's go") },
  },
  {
    text = "Mine the argonlike rocks for 28 argonlike ore north-east of Fort Forinthry in the wilderness.", --<ul><li>Adrasteia will yap the whole time without contributing.</li></ul>",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fort Forinthry lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3351, 1293, 3577, { distance = 23 }),
      Action.ModelHighlight:new(argonlikeRock, { distance = 24 }),
      Action.ConversationHighlight:new("Let's continue our journey"),
    },
    postconditions = { Condition.ChatText:new("(28/28)") },
  },
  {
    text = "Smelt at the furnace in the eastern ruins.",
    actions = {
      Action.Direction:new(3209, 2061, 3740),
      Action.ConversationHighlight:new("Let's continue our journey"),
    },
    postconditions = {
      -- Condition.InventoryContains:new(argonlikeBar, 28), --uncomment once number detection bug is fixed
      Condition.ConversationText:new("keep a low profile"),
    },
  },
  {
    text = "Smith at the anvil in the building west of the Mage Arena.",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "1,2",
      hover = "Wilderness Sword Edgeville teleport",
      url = "Wilderness_sword_1.png",
    },
    actions = { Action.Direction:new(3064, 1397, 3951) },
    postconditions = { Condition.ChatText:new("argonlike pickaxe") },
  },
  {
    text = "Talk to Adrasteia.",
    actions = { Action.ModelHighlight:new(Models.npcs["adrasteia"]) },
    postconditions = {
      Condition.InInstance:new(),
      Condition.ConversationText:new("meet you there"),
    },
  },
  {
    text = "Equip the argonlike equipment.",
    title = "Mage Arena",
    actions = { Action.InventoryHighlight:new(argonlikeHelm) },
    postconditions = { Condition.InventoryDoesNotContain:new(argonlikeHelm) },
  },
  {
    actions = { Action.InventoryHighlight:new(argonlikePlatebody) },
    postconditions = { Condition.InventoryDoesNotContain:new(argonlikePlatebody) },
  },
  {
    actions = { Action.InventoryHighlight:new(argonlikePlatelegs) },
    postconditions = { Condition.InventoryDoesNotContain:new(argonlikePlatelegs) },
  },
  {
    actions = { Action.InventoryHighlight:new(argonlikeGauntlets) },
    postconditions = { Condition.InventoryDoesNotContain:new(argonlikeGauntlets) },
  },
  {
    actions = { Action.InventoryHighlight:new(argonlikeBoots) },
    postconditions = { Condition.InventoryDoesNotContain:new(argonlikeBoots) },
  },
  {
    actions = { Action.InventoryHighlight:new(argonlikeBattleaxe) },
    postconditions = { Condition.InventoryDoesNotContain:new(argonlikeBattleaxe) },
  },
  {
    actions = { Action.InventoryHighlight:new(argonlikeKiteshield) },
    postconditions = { Condition.InventoryDoesNotContain:new(argonlikeKiteshield) },
  },
  {
    text = "Talk to Adrasteia the Forgotten to the south-east up the stairs leading up to the Mage Arena.",
    actions = {
      Action.Direction:new(3086, 2405, 3934, { distance = 20 }),
      Action.ModelHighlight:new(adrasteiaTheForgotten, { distance = 21 }),
      Action.ConversationHighlight:new("Yes, enter the Mage Arena."),
    },
    postconditions = {
      Condition.ConversationText:new("might be in charge"),
      Condition.InInstance:new(),
    },
  },
  {
    text = "Talk to Vengeance.",
    actions = {
      Action.ModelHighlight:new(vengeance, { instance = true }),
      Action.ConversationHighlight:new("I wait for orders."),
    },
    postconditions = { Condition.ConversationText:new("missive in her hand") },
  },
  {
    actions = {
      Action.ModelHighlight:new(vengeance, { instance = true }),
      Action.ConversationHighlight:new("I chase after them."),
    },
    postconditions = { Condition.ConversationText:new("What will you do") },
  },
  {
    actions = {
      Action.ModelHighlight:new(vengeance, { instance = true }),
      Action.ConversationHighlight:new("I refuse."),
    },
    postconditions = { Condition.ConversationText:new("Are you ready") },
  },
  {
    actions = {
      Action.ModelHighlight:new(vengeance, { instance = true }),
      Action.ConversationHighlight:new("I'm ready"),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Defeat the 3 waves of enemies.<ul><li>You can use prayers. It is recommended to use magic protection prayers in waves 1, 2 and melee protection in wave 3.</li><li>After each enemy is defeated, restore your health with the health orbs.</li><li>If you fail a wave two times, the wave can be skipped to continue.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, and skip this combat encounter (2 attempts).") },
    postconditions = { Condition.ConversationText:new("Excellent") },
  },
  { postconditions = { Condition.ChangedInstance:new() } },
  {
    text = "Discover the scuff marks. (1/4)",
    actions = { Action.ModelHighlight:new(scratchMarks, { instance = true }) },
    postconditions = { Condition.ChatText:new("clue: 1/4") },
  },
  {
    text = "Eavesdrop the defeated forgotten mage in the north. (2/4)",
    actions = { Action.ModelHighlight:new(forgottenArcher, { instance = true }) },
    postconditions = { Condition.ChatText:new("clue: 2/4") },
  },
  {
    text = "Discover the scorch mark. (3/4)",
    actions = { Action.ModelHighlight:new(scorchMarks2, { instance = true }) },
    postconditions = { Condition.ChatText:new("clue: 3/4") },
  },
  {
    text = "Eavesdrop the defeated forgotten mage in the south. (4/4)",
    warning = "Known issue: can't highlight the defeated forgotten mage.",
    actions = { Action.ModelHighlight:new(necrolord, { instance = true }) },
    postconditions = { Condition.ChatText:new("clue: 4/4") },
  },
  {
    text = "Talk to Adrasteia.",
    actions = { Action.ModelHighlight:new(adrasteiaTheForgotten, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Kolodion.",
    actions = { Action.ModelHighlight:new(kolodion, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Adrasteia.",
    actions = {
      Action.ModelHighlight:new(adrasteiaTheForgotten, { instance = true }),
    },
    postconditions = { Condition.ConversationText:new("what to do next") },
  },
  {
    text = "Exit the Mage Arena.",
    actions = {
      Action.Direction:new(-5, -1636, -0.5, { instance = true }),
      Action.ConversationHighlight:new("Yes, search the caves beneath the Mage Arena."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Inspect the modified ritual marker.",
    title = "Ritual marker",
    actions = { Action.ModelHighlight:new(ritualMarker, { instance = true }) },
    postconditions = { Condition.ConversationText:new("test") }, --DC'd. Don't know what happens
  },
  {
    text = "Take the rune pouch.",
    warning = "Known issue: No tracking for this step due to a quest bug at the time of development.",
    actions = { Action.ModelHighlight:new(runePouch, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(runePouch) },
  },
  {
    text = "Search the rune pouch in your backpack.",
    actions = { Action.InventoryHighlight:new(runePouch) },
    postconditions = { Condition.InventoryContains:new(emptyRunePouch) },
  },
  {
    text = "Take the white dust and choose the option empty rune pouch.",
    warning = "Known issue: No tracking for this step due to a quest bug at the time of development.",
    actions = { Action.ModelHighlight:new(whiteDust, { instance = true }) },
    postconditions = {},
  },
  {
    text = "Use the one measly fire rune on a sharp rock.",
    actions = {
      Action.ModelHighlight:new(sharpRock, { instance = true }),
      Action.InventoryHighlight:new(oneMeaslyFireRune),
    },
    postconditions = { Condition.InventoryContains:new(inertFireRuneFragments) },
  },
  {
    text = "Talk to Adrasteia and choose the option inert fire rune fragments.",
    actions = {
      Action.ModelHighlight:new(adrasteiaTheForgotten, { instance = true }),
      Action.ConversationHighlight:new("[Show an item in your backpack to Adrasteia.]"),
    },
    postconditions = { Condition.InventoryContains:new(volatileFireRuneFragments) },
  },
  {
    text = "Use the pouch of white dust on the sparkling pool to the south.",
    actions = {
      Action.ModelHighlight:new(sparklingPool, { instance = true }),
      -- Action.InventoryHighlight:new(pouchOfWhiteDust),
    },
    postconditions = { Condition.InventoryContains:new(soggyPouchOfGreyPaste) },
  },
  {
    text = "Sabotage the modified ritual marker and choose the option volatile fire rune fragments.",
    actions = { Action.ModelHighlight:new(ritualMarker, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Alright") },
  },
  {
    text = "Conceal the blatantly sabotaged ritual marker and choose the option soggy pouch of grey paste.",
    actions = { Action.ModelHighlight:new(ritualMarker, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Good luck") },
  },
  {
    text = "Step into the sparkling pool.",
    actions = {
      Action.ModelHighlight:new(sparklingPool, { instance = true }),
      Action.ConversationHighlight:new("Exit this cave and return to the deep Wilderness."),
    },
    postconditions = { Condition.ChangedInstance:new() }, --I DC'd. Don't know what happens.
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to Adrasteia.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["adrasteia"]),
      Action.ConversationHighlight:new("[Say nothing]"),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Adrasteia again.",
    actions = {
      Action.ModelHighlight:new(adrasteiaWhiteOutfit, { instance = true }),
      Action.ConversationHighlight:new("I have the seating plan"),
      Action.ConversationHighlight:new("The Asgarnian Ambassador"),
    },
    postconditions = { Condition.ConversationText:new("nearest me on my left") },
  },
  {
    actions = {
      Action.ModelHighlight:new(adrasteiaWhiteOutfit, { instance = true }),
      Action.ConversationHighlight:new("The Kandarian Ambassador"),
    },
    postconditions = { Condition.ConversationText:new("furthest from me on my right") },
  },
  {
    actions = {
      Action.ModelHighlight:new(adrasteiaWhiteOutfit, { instance = true }),
      Action.ConversationHighlight:new("The Menaphite Ambassador"),
    },
    postconditions = { Condition.ConversationText:new("the last seat") },
  },
  {
    actions = {
      Action.ModelHighlight:new(adrasteiaWhiteOutfit, { instance = true }),
      Action.ConversationHighlight:new("The Misthalinian Ambassador"),
    },
    postconditions = { Condition.ConversationText:new("hello from time to time") },
  },
  {
    actions = {
      Action.ModelHighlight:new(adrasteiaWhiteOutfit, { instance = true }),
      Action.ConversationHighlight:new("I promise"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Watch all cutscenes.",
    title = "The coronation and usurpation",
    warning = "Clicking out of the dialogue after any of the cutscenes will cause you to rewatch the cutscenes.",
    postconditions = { Condition.ConversationText:new("Guards - kill her") },
  },
  {
    text = "Kill 10 forgotten warriors.<ul><li>Use the abilities via the interface at the top of the screen. Make frequent use of the Blast ability (3) to kill multiple enemies around you.</li></ul>",
    postconditions = { Condition.ChatText:new("10/10") },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("righteous and the hope") } },
  {
    text = "Kill 20 more forgotten warriors.",
    postconditions = { Condition.ChatText:new("20/20") },
  },
  {
    text = "Watch the cutscene.",
    title = "Finishing up",
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Continue dialogue with Adrasteia.",
    actions = { Action.ModelHighlight:new(adrasteiaWhiteOutfitCrown) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Succession",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1670198400,
  prereqQuests = { "Civil War III (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Construction", 62),
    Types.QuestReq.skill("Defence", 76),
    Types.QuestReq.skill("Dungeoneering", 50),
  },
  neededItems = {},
  recommendedItems = {
    ["Archaeology journal"] = { quantity = 1 },
    ["Wilderness sword"] = { quantity = 1 },
  },
  combatNPCs = {},
})
