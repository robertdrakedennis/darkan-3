local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local mercenaryAdventurer = Model.new(6189, {
  [3274] = Vertex.new(23, 777, -39, 68, 112, 131),
  [3275] = Vertex.new(33, 758, -39, 68, 112, 131),
  [3276] = Vertex.new(30, 758, -43, 68, 112, 131),
  [3301] = Vertex.new(-23, 777, -39, 68, 112, 131),
  [3302] = Vertex.new(-30, 758, -43, 68, 112, 131),
})
--#endregion
--#region Objects
local paterdomusStairs = Model.new(1869, {
  [477] = Vertex.new(-299, 1161, 419, 85, 85, 78),
  [696] = Vertex.new(895, 872, 536, 40, 38, 36),
  [1740] = Vertex.new(817, -610, 549, 0, 0, 0),
  [1743] = Vertex.new(817, 1389, -538, 0, 0, 0),
  [1769] = Vertex.new(0, 1428, -513, 32, 32, 29),
})
local storageUnit = Model.new(2223, {
  [235] = Vertex.new(128, 544, 40, 148, 25, 13),
  [1682] = Vertex.new(4, 708, -78, 131, 122, 83),
  [1772] = Vertex.new(126, 893, -33, 153, 137, 63),
  [2189] = Vertex.new(-256, 972, -256, 94, 98, 89),
  [2197] = Vertex.new(-256, 1040, 256, 50, 52, 47),
})
local paterdomusBasementStairs = Model.new(876, {
  [188] = Vertex.new(3072, 2832, 3116, 80, 84, 77),
  [225] = Vertex.new(3584, 2832, 3116, 80, 84, 77),
  [246] = Vertex.new(3584, 1792, 3116, 80, 84, 77),
  [341] = Vertex.new(3072, 1792, 3156, 80, 84, 77),
  [432] = Vertex.new(3584, 1792, 3156, 80, 84, 77),
})
local woodenPost = Model.new(144, {
  [25] = Vertex.new(-236, 1140, -28, 42, 42, 32),
  [27] = Vertex.new(-236, 1140, 100, 42, 42, 32),
  [37] = Vertex.new(-164, 1140, -28, 56, 50, 43),
  [49] = Vertex.new(-156, 1140, 100, 42, 42, 32),
  [110] = Vertex.new(-236, 1140, 100, 42, 42, 32),
})
local fixedWoodenPost = Model.new(654, {
  [35] = Vertex.new(-56, 1080, 68, 122, 123, 113),
  [443] = Vertex.new(-164, 1140, -28, 56, 50, 43),
  [450] = Vertex.new(-236, 1140, 100, 56, 50, 43),
  [452] = Vertex.new(-236, 1140, 100, 56, 50, 43),
  [518] = Vertex.new(-236, 1140, 100, 42, 42, 32),
})
local massDebris = Model.new(1464, {
  [63] = Vertex.new(-452, 0, 508, 45, 45, 42),
  [369] = Vertex.new(-448, 205, 508, 52, 51, 47),
  [381] = Vertex.new(-448, 205, 508, 30, 36, 3),
  [387] = Vertex.new(-256, 200, 512, 48, 48, 25),
  [417] = Vertex.new(-488, 205, -412, 52, 51, 47),
})
local emptyFurnace = Model.new(1119, {
  [9] = Vertex.new(-452, 0, 508, 45, 45, 42),
  [237] = Vertex.new(-448, 205, 508, 52, 51, 47),
  [243] = Vertex.new(-448, 205, 508, 30, 36, 3),
  [249] = Vertex.new(-256, 200, 512, 48, 48, 25),
  [267] = Vertex.new(-488, 205, -412, 52, 51, 47),
})
local filledFurnace = Model.new(1977, {
  [867] = Vertex.new(-452, 0, 508, 45, 45, 42),
  [1095] = Vertex.new(-448, 205, 508, 52, 51, 47),
  [1101] = Vertex.new(-448, 205, 508, 30, 36, 3),
  [1107] = Vertex.new(-256, 200, 512, 48, 48, 25),
  [1125] = Vertex.new(-488, 205, -412, 52, 51, 47),
})
local litFurnace = Model.new(4149, {
  [2132] = Vertex.new(177, 160, -156, 166, 60, 34),
  [2174] = Vertex.new(193, 160, -152, 190, 137, 17),
  [2424] = Vertex.new(-448, 205, 508, 30, 36, 3),
  [2835] = Vertex.new(-448, 205, 508, 52, 51, 47),
  [2847] = Vertex.new(-488, 205, -412, 52, 51, 47),
})
local silverBarBarrel = Model.new(1197, {
  [101] = Vertex.new(-96, 276, -96, 60, 56, 38),
  [392] = Vertex.new(-104, 276, -104, 68, 61, 52),
  [394] = Vertex.new(-104, 276, -104, 68, 61, 52),
  [428] = Vertex.new(104, 276, 104, 68, 61, 52),
  [1080] = Vertex.new(44, 392, -40, 117, 117, 127),
})
local silverSickleShelf = Model.new(798, {
  [2] = Vertex.new(-256, 780, -192, 56, 50, 43),
  [6] = Vertex.new(-256, 780, -192, 56, 50, 43),
  [11] = Vertex.new(-256, 795, 188, 56, 50, 43),
  [51] = Vertex.new(-216, 795, 188, 42, 42, 39),
  [528] = Vertex.new(-192, 830, 156, 63, 58, 58),
})
local chainLinkMould = Model.new(588, {
  [182] = Vertex.new(120, 0, -120, 106, 98, 98),
  [183] = Vertex.new(-120, 0, -120, 106, 98, 98),
  [186] = Vertex.new(-120, 0, -120, 106, 98, 98),
  [189] = Vertex.new(120, 0, -120, 106, 98, 98),
  [191] = Vertex.new(-120, 0, -120, 106, 98, 98),
})
local deadSafalaan = Model.new(5970, {
  [3459] = Vertex.new(-26, 3, 118, 118, 106, 49),
  [3461] = Vertex.new(-16, 4, 122, 118, 106, 49),
  [4109] = Vertex.new(-121, 30, -418, 0, 0, 0),
  [4115] = Vertex.new(-121, 30, -418, 17, 12, 11),
  [4118] = Vertex.new(-121, 30, -418, 17, 12, 11),
})
local sawedDoor = Model.new(1230, {
  [482] = Vertex.new(256, 600, 44, 51, 51, 39),
  [1166] = Vertex.new(-156, 636, -28, 48, 48, 30),
  [1178] = Vertex.new(-116, 976, -28, 48, 48, 30),
  [1209] = Vertex.new(256, 600, -56, 59, 67, 51),
  [1220] = Vertex.new(256, 600, 52, 59, 67, 51),
})
local corpse = Model.new(1362, {
  [63] = Vertex.new(4064, 1292, 2452, 56, 52, 52),
  [642] = Vertex.new(3820, 1376, 2636, 56, 52, 52),
  [795] = Vertex.new(3912, 1200, 2724, 56, 52, 52),
  [869] = Vertex.new(3816, 1376, 2620, 56, 52, 52),
  [1116] = Vertex.new(4044, 1292, 2472, 56, 52, 52),
})
local strangeStones = Model.new(3936, {
  [669] = Vertex.new(676, 0, 676, 37, 36, 29),
  [2525] = Vertex.new(720, 4, 648, 37, 36, 29),
  [2529] = Vertex.new(720, 4, 648, 37, 36, 29),
  [2670] = Vertex.new(608, 0, 696, 37, 36, 29),
  [3608] = Vertex.new(728, 4, 596, 37, 36, 29),
})
local vyrewatchCorpseObj = Model.new(6618, {
  [2187] = Vertex.new(168, 42, -301, 161, 161, 173),
  [2193] = Vertex.new(169, 39, -301, 161, 161, 173),
  [2220] = Vertex.new(167, 40, -300, 161, 161, 173),
  [2228] = Vertex.new(173, 29, -306, 161, 161, 173),
  [2234] = Vertex.new(172, 31, -304, 161, 161, 173),
})
local funeralPyre = Model.new(5586, {
  [564] = Vertex.new(128, 192, -448, 60, 62, 57),
  [968] = Vertex.new(-160, 736, 416, 75, 77, 71),
  [977] = Vertex.new(160, 736, -416, 75, 77, 71),
  [989] = Vertex.new(-160, 736, -416, 75, 77, 71),
  [2591] = Vertex.new(-229, 254, -295, 56, 52, 52),
})
local ornateTombKeyObj = Model.new(285, {
  [175] = Vertex.new(20, 0, -44, 104, 108, 56),
  [177] = Vertex.new(20, 0, -36, 104, 108, 56),
  [182] = Vertex.new(20, 0, -44, 104, 108, 56),
  [197] = Vertex.new(4, 0, -48, 104, 108, 56),
  [264] = Vertex.new(-20, 0, 64, 104, 108, 56),
})
--#endregion
--#region Items
local teakPyreLogs = Model.new(564, {
  [4] = Vertex.new(100, 0, 12, 100, 83, 52),
  [5] = Vertex.new(92, 36, 0, 100, 83, 52),
  [6] = Vertex.new(92, 4, 16, 100, 83, 52),
  [7] = Vertex.new(100, 84, 52, 100, 83, 52),
  [8] = Vertex.new(92, 72, 16, 100, 83, 52),
  [9] = Vertex.new(100, 76, 4, 100, 83, 52),
  [10] = Vertex.new(-108, 4, 16, 100, 83, 52),
  [11] = Vertex.new(-116, 28, -12, 100, 83, 52),
  [12] = Vertex.new(-116, 0, 12, 100, 83, 52),
  [13] = Vertex.new(-108, 72, 48, 100, 83, 52),
  [14] = Vertex.new(-116, 76, 4, 100, 83, 52),
  [15] = Vertex.new(-108, 72, 16, 100, 83, 52),
  [16] = Vertex.new(100, 0, 60, 100, 83, 52),
  [18] = Vertex.new(92, 4, 52, 100, 83, 52),
  [23] = Vertex.new(-108, 4, 52, 100, 83, 52),
  [27] = Vertex.new(-116, 0, 60, 100, 83, 52),
  [29] = Vertex.new(100, 40, 84, 100, 83, 52),
  [33] = Vertex.new(92, 40, 72, 100, 83, 52),
  [39] = Vertex.new(92, 72, 48, 100, 83, 52),
  [48] = Vertex.new(100, 28, -12, 100, 83, 52),
})
local vyrewatchCorpse = Model.multi({
  Model.new(6843, {
    [3225] = Vertex.new(240, 71, -203, 67, 64, 62),
    [5855] = Vertex.new(166, 45, -303, 161, 161, 173),
    [5859] = Vertex.new(166, 41, -303, 161, 161, 173),
    [5862] = Vertex.new(175, 41, -298, 161, 161, 173),
    [5865] = Vertex.new(175, 38, -297, 161, 161, 173),
  }),
  Model.new(936, {
    [247] = Vertex.new(363, 32, -509, 70, 180, 37, 0.000),
    [248] = Vertex.new(358, 34, -516, 70, 180, 37, 0.000),
    [249] = Vertex.new(363, 33, -517, 70, 180, 37, 0.000),
    [302] = Vertex.new(238, 130, -539, 70, 180, 37, 0.000),
    [303] = Vertex.new(242, 130, -539, 70, 180, 37, 0.000),
  }),
})
--#endregion
--#region Quest Items
local glove = Model.new(375, {
  [5] = Vertex.new(36, 0, -72, 170, 157, 157),
  [56] = Vertex.new(40, 0, 68, 170, 157, 157),
  [59] = Vertex.new(40, 0, 68, 170, 157, 157),
  [62] = Vertex.new(32, 0, 76, 170, 157, 157),
  [216] = Vertex.new(36, 0, -72, 115, 115, 105),
})
local bookPage = Model.multi({
  Model.new(138, {
    [41] = Vertex.new(76, 0, -80, 115, 115, 105),
    [87] = Vertex.new(-80, 0, -96, 74, 73, 67),
    [91] = Vertex.new(-92, 0, 96, 74, 73, 67),
    [94] = Vertex.new(-92, 0, 96, 74, 73, 67),
    [97] = Vertex.new(76, 0, -80, 115, 115, 105),
  }),
  Model.new(207, {
    [2] = Vertex.new(52, 0, 92, 66, 66, 60, 0.6078),
    [3] = Vertex.new(52, 0, 84, 66, 66, 60, 0.6078),
    [6] = Vertex.new(52, 0, 92, 66, 66, 60, 0.6078),
    [11] = Vertex.new(-56, 0, 92, 66, 66, 60, 0.6078),
    [21] = Vertex.new(52, 0, 84, 66, 66, 60, 0.7059),
  }),
})
local crate = Model.new(564, {
  [138] = Vertex.new(-152, 132, 12, 62, 58, 48),
  [140] = Vertex.new(-152, 132, 12, 62, 58, 48),
  [165] = Vertex.new(128, 92, 52, 77, 72, 59),
  [207] = Vertex.new(-108, 92, 72, 77, 72, 59),
  [390] = Vertex.new(128, 92, 52, 65, 61, 50),
})
local combatBook = Model.new(591, {
  [7] = Vertex.new(-80, 16, -112, 61, 79, 61),
  [38] = Vertex.new(24, 16, 96, 61, 79, 61),
  [47] = Vertex.new(-80, 16, 112, 61, 79, 61),
  [480] = Vertex.new(-28, 60, 72, 95, 88, 87),
  [546] = Vertex.new(-36, 60, -72, 35, 33, 32),
})
local ornateTombKey = Model.new(285, {
  [175] = Vertex.new(20, 0, -44, 104, 108, 56),
  [177] = Vertex.new(20, 0, -36, 104, 108, 56),
  [180] = Vertex.new(20, 0, -36, 104, 108, 56),
  [182] = Vertex.new(20, 0, -44, 104, 108, 56),
  [264] = Vertex.new(-20, 0, 64, 104, 108, 56),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the mercenary adventurer east of Paterdomus in Morytania.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    neededItems = {},
    recommendedItems = {},
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "CKS",
    },
    actions = { Action.Direction:new(3439, 653, 3485) },
    postconditions = { Condition.ModelVisible:new(mercenaryAdventurer) },
  },
  {
    actions = { Action.ModelHighlight:new(mercenaryAdventurer) },
    jumpconditions = { Condition.ModelNotVisible:new(mercenaryAdventurer) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Mercenary Adventurer.",
    actions = { Action.ConversationHighlight:new("Okay, thanks.") },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Enter the Paterdomus Mausoleum.",
    title = "An evil conspiracy",
    neededItems = { ["Silver weapon"] = { quantity = 1 } },
    recommendedItems = {
      ["Havensilver greatsword"] = { quantity = 1 },
      ["Invitation box"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
      ["Necromancy combat gear"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3424, 1465, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3423, 5, 9891, 20) },
  },
  {
    text = "Talk to Drezel.",
    actions = { Action.Direction:new(3440, 677, 9895) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["drezel"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Talk about something else"),
      Action.ConversationHighlight:new("Who do you think these suspicious people are?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["drezel"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Zamorakians") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Climb down the stairway to the west.",
    actions = { Action.ModelHighlight:new(paterdomusStairs) },
    postconditions = { Condition.DistanceTo:new(3422, 1797, 9965, 4) },
  },
  {
    text = "Search the sparkling wall storage unit.",
    actions = { Action.ModelHighlight:new(storageUnit) },
    postconditions = { Condition.ConversationText:new("talisman") },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.ModelHighlight:new(paterdomusBasementStairs) },
    postconditions = { Condition.DistanceTo:new(3425, 712, 9899, 4) },
  },
  {
    text = "Climb the ladder to the west.",
    actions = { Action.Direction:new(3405, 1212, 9907) },
    postconditions = { Condition.DistanceTo:new(3405, 2821, 3504, 4) },
  },
  {
    text = "Enter the Paterdomus temple.",
    actions = { Action.Direction:new(3403.5, 4205, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3406, 3781, 3486, 1) },
  },
  {
    text = "Climb the stairs.",
    actions = { Action.Direction:new(3414.5, 4281, 3481.25) },
    postconditions = { Condition.DistanceToWithHeight:new(3415, 6405, 3482, 4) },
  },
  {
    text = "Climb the stairs again.",
    actions = { Action.Direction:new(3406.5, 7005, 3482.525) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    postconditions = {
      Condition.ConversationText:new("teleport"),
      Condition.ConversationText:new("Teleport"),
    },
  },
  {
    text = "Kill Fistandantilus and Zaromark Sliver.",
    postconditions = { Condition.ConversationText:new("believe it myself") },
  },
  {
    text = "Search the crude table.",
    actions = { Action.Direction:new(-0.4, 500, 5, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(glove) },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(0, 0, -1, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(0, -1856, -1, 4, true) },
  },
  {
    text = "Climb down the stairs again.",
    actions = { Action.Direction:new(7.5, -1456, -2.3, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(7, -4480, -2, 4, true) },
  },
  {
    text = "Return to the Paterdomus Mausoleum.",
    actions = { Action.Direction:new(-2, -4840, 21, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(3423, 5, 9891, 20) },
  },
  {
    text = "Return to Drezel.",
    actions = { Action.Direction:new(3440, 677, 9895) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["drezel"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Do you think this has anything to do with the Guthixian Edicts?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["drezel"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("bit scary") },
  },
  {
    text = "Pass through the barrier.",
    actions = { Action.ModelHighlight:new(Models.objects["paterdomus holy barrier"]) },
    postconditions = { Condition.DistanceTo:new(3424, 1465, 3485.5, 4) },
  },
  {
    text = "Talk to the Mercenary Adventurer.<ul><li>Easy path recommended.</li></ul>",
    actions = { Action.Direction:new(3439, 653, 3485) },
    postconditions = { Condition.ModelVisible:new(mercenaryAdventurer) },
  },
  {
    actions = {
      Action.ModelHighlight:new(mercenaryAdventurer),
      Action.ConversationHighlight:new("You still want to go to Burgh de Rott?"),
      Action.ConversationHighlight:new("I can take you to Burgh de Rott."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(mercenaryAdventurer) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Complete the run of Temple Trekking.",
    actions = { Action.ConversationHighlight:new("ask again") },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to the Mercenary Adventurer.<ul><li>If the Mercenary Adventurer isn't here, skip this step.</li></ul>",
    actions = { Action.ModelHighlight:new(mercenaryAdventurer) },
    postconditions = { Condition.ConversationText:new("Drakan") },
  },
  {
    text = "Climb down the trapdoor inside the pub in Burgh de Rott.",
    title = "The Myreque's task",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3490, 965, 3232, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    text = "Talk to Veliaf Hurtz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Do you have a job for me?"),
    },
    postconditions = { Condition.InventoryContains:new(crate) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(3490, 1245, 9632) },
    postconditions = { Condition.DistanceTo:new(3491, 965, 3232, 4) },
  },
  {
    text = "Board the boat to Meiyerditch south-east of Burgh de Rott.",
    actions = { Action.Direction:new(3523, 165, 3170) },
    postconditions = { Condition.ModelVisible:new(Models.objects["burgh de rott fixed boat"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["burgh de rott fixed boat"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.objects["burgh de rott fixed boat"]) },
    jumpOffset = -1,
    postconditions = { Condition.DistanceTo:new(3605, 69, 3161, 4) },
  },
  {
    text = "Get caught by a Vyrewatch.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["any vyrewatch"], { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Send me to the mines! (Do a bit of menial work)"),
    },
    postconditions = { Condition.DistanceTo:new(2263, 2765, 4620, 10) },
  },
  {
    text = "Mine 15 ore from the walls.",
    postconditions = { Condition.InventoryContains:new(Models.items["daeyalt ore"], 15) },
  },
  {
    text = "Use the ore on a minecart",
    actions = {
      Action.ModelHighlight:new(Models.objects["meiyerditch minecart"], { highlightPriority = "closest" }),
      Action.InventoryHighlight:new(Models.items["daeyalt ore"]),
      Action.ConversationHighlight:new("Yes, I'll place it all in the cart."),
    },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch full minecart"]) },
  },
  {
    text = "Talk to a guard.",
    actions = { Action.ModelHighlight:new(Models.npcs["any juvinate"], { highlightPriority = "closest" }) },
    postconditions = { Condition.DistanceTo:new(3621, 4485, 3322, 4) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3621, 4485, 3322),
        Location:new(3624, 4485, 3318),
        Location:new(3624, 4485, 3314),
        Location:new(3632, 4485, 3305),
        Location:new(3628, 4485, 3300),
        Location:new(3628, 4485, 3294),
        Location:new(3633, 4485, 3294),
        Location:new(3633, 4485, 3289),
        Location:new(3635, 4485, 3288),
        Location:new(3635, 4485, 3284),
        Location:new(3632, 4485, 3283),
        Location:new(3632, 4485, 3277),
        Location:new(3634, 4485, 3275),
        Location:new(3634, 4485, 3268),
        Location:new(3631, 4485, 3267),
        Location:new(3631, 4485, 3259),
        Location:new(3631, 5285, 3258),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Climb down the stairs at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch decorated wall"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch lumpy rug"]) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch lumpy rug"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch hideout trapdoor"]) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch hideout trapdoor"]) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    text = "Talk to Flaygian Screwte.",
    actions = { Action.Direction:new(3622, 965, 9644) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["flaygian screwte"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["flaygian screwte"]),
      Action.ConversationHighlight:new("How's the research going?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["flaygian screwte"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("random detail") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["flaygian screwte"]),
      Action.ConversationHighlight:new("Can I help in some way?"),
    },
    postconditions = { Condition.ConversationText:new("such matters") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["flaygian screwte"]),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("Okay, thanks") },
  },
  {
    text = "Talk to Andiess Juip or Kael Forshaw.",
    title = "In search of the weapon",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["andiess juip"]),
      Action.ModelHighlight:new(Models.npcs["kael forshaw"]),
      Action.ConversationHighlight:new("Hi there, I was wondering if I could ask some questions?"),
    },
    postconditions = { Condition.ConversationText:new("simple enough") },
  },
  {
    text = "Search the bunk bed.",
    actions = { Action.Direction:new(3631, 1565, 9632.5) },
    postconditions = { Condition.InventoryContains:new(combatBook) },
  },
  {
    text = "Read the book.",
    warning = "Remember which page mentioned flails.",
    actions = { Action.InventoryHighlight:new(combatBook) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Talk to Flaygian.",
    warning = "Need the page number from the previous step.",
    actions = { Action.Direction:new(3622, 965, 9644) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["flaygian screwte"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["flaygian screwte"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["flaygian screwte"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Good luck") },
  },
  {
    text = "Exit the hideout.",
    actions = { Action.Direction:new(3626, 1665, 9617.3) },
    postconditions = { Condition.DistanceTo:new(3638, 4517, 3249, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4485, 3254, 1) },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(3639, 5285, 3256.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 5701, 3255, 4) },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630.5, 5701, 3258),
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3255),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3632, 4485, 3258, 4) },
  },
  {
    text = "Enter the home at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 4485, 3259),
        Location:new(3631, 4485, 3267),
        Location:new(3634, 4485, 3268),
        Location:new(3634, 4485, 3275),
        Location:new(3632, 4485, 3277),
        Location:new(3632, 4485, 3283),
        Location:new(3635, 4485, 3284),
        Location:new(3635, 4485, 3288),
        Location:new(3633, 4485, 3289),
        Location:new(3633, 4485, 3294),
        Location:new(3628, 4485, 3294),
        Location:new(3628, 4485, 3300),
        Location:new(3632, 4485, 3305),
        Location:new(3632, 4485, 3305),
        Location:new(3631, 4485, 3312),
        Location:new(3632, 4485, 3316),
        Location:new(3633, 4485, 3316),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3634, 4485, 3316, 1) },
  },
  {
    text = "Make-ladder on the wooden post.",
    actions = { Action.ModelHighlight:new(woodenPost) },
    postconditions = { Condition.ModelVisible:new(fixedWoodenPost) },
  },
  {
    text = "Climb up the post.",
    actions = { Action.ModelHighlight:new(fixedWoodenPost) },
    postconditions = { Condition.DistanceToWithHeight:new(3636, 5701, 3318, 4) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3636, 5701, 3318),
        Location:new(3637, 5701, 3318),
        Location:new(3639, 5701, 3318),
        Location:new(3639, 5701, 3320),
        Location:new(3639, 5701, 3324),
        Location:new(3634, 5701, 3324),
        Location:new(3634, 5701, 3326),
        Location:new(3633, 5701, 3326),
        Location:new(3633, 6201, 3325),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3633, 6917, 3324, 4) },
  },
  {
    text = "Search the trough.",
    actions = { Action.Direction:new(3635.52, 7217, 3324) },
    postconditions = { Condition.ConversationText:new("below") },
  },
  {
    text = "Search the coal barrel.",
    actions = { Action.Direction:new(3636, 7217, 3323) },
    postconditions = { Condition.InventoryContains:new(Models.items["coal"]) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3633, 6917, 3324.7) },
    postconditions = { Condition.DistanceToWithHeight:new(3633, 5701, 3326, 4) },
  },
  {
    text = "Climb down the ladder again.",
    actions = { Action.Direction:new(3634.7, 5701, 3323) },
    postconditions = { Condition.DistanceToWithHeight:new(3636, 4485, 3323, 4) },
  },
  {
    text = "'Dig' on the mass of debris.",
    actions = { Action.ModelHighlight:new(massDebris) },
    postconditions = { Condition.ModelVisible:new(emptyFurnace) },
  },
  {
    text = "Use the coal on the furnace.",
    actions = {
      Action.ModelHighlight:new(emptyFurnace),
      Action.InventoryHighlight:new(Models.items["coal"]),
    },
    postconditions = { Condition.ModelVisible:new(filledFurnace) },
  },
  {
    text = "Light the furnace.",
    actions = { Action.ModelHighlight:new(filledFurnace) },
    postconditions = { Condition.ModelVisible:new(litFurnace) },
  },
  {
    text = "Climb the ladder.",
    title = "Laying the groundwork",
    actions = { Action.Direction:new(3634.8, 5085, 3323) },
    postconditions = { Condition.DistanceToWithHeight:new(3634, 5701, 3323, 4) },
  },
  {
    text = "Climb down the wooden beam at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3634, 5701, 3323),
        Location:new(3634, 5701, 3322),
        Location:new(3636, 5701, 3322),
        Location:new(3636, 5701, 3324),
        Location:new(3639, 5701, 3324),
        Location:new(3639, 5701, 3320),
        Location:new(3639, 5701, 3318),
        Location:new(3637, 5701, 3318),
        Location:new(3635.4, 5701, 3318),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3634, 4485, 3318, 4) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 5285, 3258),
        Location:new(3631, 4485, 3259),
        Location:new(3631, 4485, 3267),
        Location:new(3634, 4485, 3268),
        Location:new(3634, 4485, 3275),
        Location:new(3632, 4485, 3277),
        Location:new(3632, 4485, 3283),
        Location:new(3635, 4485, 3284),
        Location:new(3635, 4485, 3288),
        Location:new(3633, 4485, 3289),
        Location:new(3633, 4485, 3294),
        Location:new(3628, 4485, 3294),
        Location:new(3628, 4485, 3300),
        Location:new(3632, 4485, 3305),
        Location:new(3632, 4485, 3305),
        Location:new(3631, 4485, 3312),
        Location:new(3632, 4485, 3316),
        Location:new(3633, 4485, 3316),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Climb down the stairs at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch decorated wall"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch lumpy rug"]) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch lumpy rug"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch hideout trapdoor"]) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch hideout trapdoor"]) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    text = "Talk to Flaygian.",
    actions = { Action.Direction:new(3622, 965, 9644) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["flaygian screwte"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["flaygian screwte"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["flaygian screwte"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("within reason") },
  },
  {
    text = "Take 2 silver and 2 mithril bars from the barrel on the northern wall.",
    actions = { Action.ModelHighlight:new(silverBarBarrel) },
    postconditions = { Condition.InventoryContains:new(Models.items["silver bar"], 2) },
  },
  {
    actions = { Action.ModelHighlight:new(silverBarBarrel) },
    postconditions = { Condition.InventoryContains:new(Models.items["mithril bar"], 2) },
  },
  {
    text = "Take a silver sickle (b) from the shelf.",
    actions = { Action.ModelHighlight:new(silverSickleShelf) },
    postconditions = { Condition.InventoryContains:new(Models.items["silver sickle (b)"]) },
  },
  {
    text = "Search the crate to the south.",
    actions = { Action.Direction:new(3632, 1165, 9619) },
    postconditions = { Condition.InventoryContains:new(chainLinkMould) },
  },
  {
    text = "Add the chain link mould to your tool belt.",
    actions = { Action.InventoryHighlight:new(chainLinkMould) },
    postconditions = { Condition.InventoryDoesNotContain:new(chainLinkMould) },
  },
  {
    text = "Exit the hideout.",
    actions = { Action.Direction:new(3626, 1665, 9617.3) },
    postconditions = { Condition.DistanceTo:new(3638, 4517, 3249, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4485, 3254, 1) },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(3639, 5285, 3256.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 5701, 3255, 4) },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630.5, 5701, 3258),
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3255),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3632, 4485, 3258, 4) },
  },
  {
    text = "Enter the home at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 4485, 3259),
        Location:new(3631, 4485, 3267),
        Location:new(3634, 4485, 3268),
        Location:new(3634, 4485, 3275),
        Location:new(3632, 4485, 3277),
        Location:new(3632, 4485, 3283),
        Location:new(3635, 4485, 3284),
        Location:new(3635, 4485, 3288),
        Location:new(3633, 4485, 3289),
        Location:new(3633, 4485, 3294),
        Location:new(3628, 4485, 3294),
        Location:new(3628, 4485, 3300),
        Location:new(3632, 4485, 3305),
        Location:new(3632, 4485, 3305),
        Location:new(3631, 4485, 3312),
        Location:new(3632, 4485, 3316),
        Location:new(3633, 4485, 3316),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3634, 4485, 3316, 1) },
  },
  {
    text = "Climb up the post.",
    actions = { Action.ModelHighlight:new(fixedWoodenPost) },
    postconditions = { Condition.DistanceToWithHeight:new(3636, 5701, 3318, 4) },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3636, 5701, 3318),
        Location:new(3637, 5701, 3318),
        Location:new(3639, 5701, 3318),
        Location:new(3639, 5701, 3320),
        Location:new(3639, 5701, 3324),
        Location:new(3636, 5701, 3324),
        Location:new(3636, 5701, 3322),
        Location:new(3634, 5701, 3322),
        Location:new(3634, 5701, 3323),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3636, 4485, 3323, 4) },
  },
  {
    text = "Make a silvthril chain.",
    actions = { Action.ModelHighlight:new(litFurnace) },
    postconditions = { Condition.InventoryContains:new(Models.items["silvthril chain"]) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(3634.8, 5085, 3323) },
    postconditions = { Condition.DistanceToWithHeight:new(3634, 5701, 3323, 4) },
  },
  {
    text = "Climb down the wooden beam at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3634, 5701, 3323),
        Location:new(3634, 5701, 3322),
        Location:new(3636, 5701, 3322),
        Location:new(3636, 5701, 3324),
        Location:new(3639, 5701, 3324),
        Location:new(3639, 5701, 3320),
        Location:new(3639, 5701, 3318),
        Location:new(3637, 5701, 3318),
        Location:new(3635.4, 5701, 3318),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3634, 4485, 3318, 4) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 5285, 3258),
        Location:new(3631, 4485, 3259),
        Location:new(3631, 4485, 3267),
        Location:new(3634, 4485, 3268),
        Location:new(3634, 4485, 3275),
        Location:new(3632, 4485, 3277),
        Location:new(3632, 4485, 3283),
        Location:new(3635, 4485, 3284),
        Location:new(3635, 4485, 3288),
        Location:new(3633, 4485, 3289),
        Location:new(3633, 4485, 3294),
        Location:new(3628, 4485, 3294),
        Location:new(3628, 4485, 3300),
        Location:new(3632, 4485, 3305),
        Location:new(3632, 4485, 3305),
        Location:new(3631, 4485, 3312),
        Location:new(3632, 4485, 3316),
        Location:new(3633, 4485, 3316),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Climb down the stairs at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch decorated wall"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch lumpy rug"]) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch lumpy rug"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch hideout trapdoor"]) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch hideout trapdoor"]) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    text = "Talk to Flaygian.",
    actions = { Action.Direction:new(3622, 965, 9644) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["flaygian screwte"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["flaygian screwte"]),
      Action.ConversationHighlight:new("How about using this as the head for the flail?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["flaygian screwte"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("on you as well") },
  },
  {
    text = "Use a silver sickle (b) on Flaygian.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["flaygian screwte"]),
      Action.InventoryHighlight:new(Models.items["silver sickle (b)"]),
      Action.ConversationHighlight:new("How about using this as the head for the flail?"),
    },
    postconditions = { Condition.ConversationText:new("more than pleased") },
  },
  {
    text = "Talk to Safalaan.",
    actions = { Action.ModelHighlight:new(Models.npcs["safalaan"]) },
    postconditions = { Condition.ConversationText:new("see you there") },
  },
  {
    text = "Exit the hideout.",
    title = "Back to the lab",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3626, 1665, 9617.3) },
    postconditions = { Condition.DistanceTo:new(3638, 4517, 3249, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4485, 3254, 1) },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(3639, 5285, 3256.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 5701, 3255, 4) },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630.5, 5701, 3258),
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3255),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3632, 4485, 3258, 4) },
  },
  {
    text = "Climb down the staircase at the end of the path.",
    warning = "Dismiss any followers",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 4485, 3259),
        Location:new(3631, 4485, 3267),
        Location:new(3634, 4485, 3268),
        Location:new(3634, 4485, 3275),
        Location:new(3632, 4485, 3277),
        Location:new(3632, 4485, 3283),
        Location:new(3635, 4485, 3284),
        Location:new(3635, 4485, 3288),
        Location:new(3633, 4485, 3289),
        Location:new(3633, 4485, 3293),
        Location:new(3633, 4485, 3293),
        Location:new(3635, 4485, 3295),
        Location:new(3635, 4485, 3300),
        Location:new(3640, 4485, 3300),
        Location:new(3640, 4485, 3302),
        Location:new(3637, 4485, 3302),
        Location:new(3637, 4485, 3304),
        Location:new(3638, 4485, 3304),
        Location:new(3638, 4485, 3305),
        Location:new(3641, 4485, 3305),
        Location:new(3641, 4485, 3307),
        Location:new(3642, 4485, 3307),
        Location:new(3643, 4485, 3306),
        Location:new(3643, 4485, 3305),
      }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("Myreque") } },
  {
    text = "Look at Safalaan.",
    actions = { Action.ModelHighlight:new(deadSafalaan) },
    postconditions = { Condition.ConversationText:new("human again") },
  },
  {
    text = "Talk to Safalaan.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("Is there anything I can do?"),
    },
    postconditions = { Condition.ConversationText:new("go ahead") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Search the door to the south. Use the saw.",
    actions = { Action.Direction:new(-7.5, 600, -15, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(sawedDoor) },
  },
  {
    text = "Go through the door.",
    actions = { Action.Direction:new(-7.5, 600, -15, { instance = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Follow the path to the large chamber where the strange stones are.",
    actions = {}, --TODO: get direction, not instanced
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Safalaan.",
    title = "Vyrewatch and the Ivandis flail",
    neededItems = {
      ["Cosmic rune"] = { quantity = 2, model = Models.items["cosmic rune"] },
      ["Air rune"] = { quantity = 6, model = Models.items["air rune"] },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["safalaan"]) },
    postconditions = {
      Condition.ConversationText:new("fair enough"),
      Condition.ConversationText:new("Fair enough"),
    },
  },
  {
    text = "Search the corpse next to the rocks.",
    actions = { Action.ModelHighlight:new(corpse) },
    postconditions = { Condition.ChatText:new("Bloodstained Treasure") },
  },
  {
    text = "Search the strange stones in the northern part of the cavern.",
    actions = { Action.ModelHighlight:new(strangeStones) },
    postconditions = { Condition.ConversationText:new("Safalaan") },
  },
  {
    text = "Talk to Safalaan.",
    actions = { Action.ModelHighlight:new(Models.npcs["safalaan"]) },
    postconditions = { Condition.ConversationText:new("some danger") },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("GO") },
  },
  {
    text = "Defend against the vyrewatches. You can't damage them.",
    postconditions = { Condition.ConversationText:new("Safalaan") },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Safalaan.",
    warning = "Need 4 inventory spaces.",
    actions = { Action.Direction:new(3628, 965, 9646) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["safalaan"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["safalaan"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["safalaan"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Blimey") },
  },
  {
    text = "Use an emerald on a silver sickle (b).",
    actions = {
      Action.InventoryHighlight:new(Models.items["emerald"]),
      Action.InventoryHighlight:new(Models.items["silver sickle (b)"]),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["silver sickle emerald (b)"]) },
  },
  {
    text = "Cast Lvl-2 Enchant spell on the silver sickle emerald (b).",
    warning = "No tracking for this step.",
    actions = { Action.InventoryHighlight:new(Models.items["silver sickle emerald (b)"]) },
  },
  {
    text = "Use the chain on enchanted emerald sickle.",
    actions = {
      Action.InventoryHighlight:new(Models.items["silvthril chain"]),
      Action.InventoryHighlight:new(Models.items["silver sickle emerald (b)"]),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["ivandis flail"]) },
  },
  {
    text = "Talk to Safalaan.",
    actions = { Action.ModelHighlight:new(Models.npcs["safalaan"]) },
    postconditions = { Condition.ConversationText:new("coming right up") },
  },
  {
    text = "Exit the hideout.",
    actions = { Action.Direction:new(3626, 1665, 9617.3) },
    postconditions = { Condition.DistanceTo:new(3638, 4517, 3249, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4485, 3254, 1) },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(3639, 5285, 3256.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 5701, 3255, 4) },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630.5, 5701, 3258),
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3255),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3632, 4485, 3258, 4) },
  },
  {
    text = "Equip the Ivandis flail.",
    actions = { Action.InventoryHighlight:new(Models.items["ivandis flail"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["ivandis flail"]) },
  },
  {
    text = "Kill a vyrewatch.",
    actions = { Action.ModelHighlight:new(Models.npcs["any vyrewatch"], { highlightPriority = "closest" }) },
    postconditions = { Condition.ModelVisible:new(vyrewatchCorpseObj) },
  },
  {
    text = "Pick up its corpse.",
    actions = { Action.ModelHighlight:new(vyrewatchCorpse) },
    postconditions = { Condition.InventoryContains:new(vyrewatchCorpse) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(3631, 5085, 3258) },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Climb down the stairs at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch decorated wall"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch lumpy rug"]) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch lumpy rug"]) },
    postconditions = { Condition.ModelVisible:new(Models.objects["meiyerditch hideout trapdoor"]) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(Models.objects["meiyerditch hideout trapdoor"]) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    text = "Talk to Safalaan.",
    actions = { Action.Direction:new(3629, 965, 9644) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["safalaan"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["safalaan"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["safalaan"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("move out") },
  },
  {
    text = "Return to the basement of the pub in Burgh de Rott.",
    title = "Back to the Columbarium",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Canifis lodestone",
      url = "Canifis_lodestone_icon.png",
    },
    neededItems = { ["Teak pyre logs"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3490, 965, 3232, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    text = "Talk to Veliaf.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("The Flail of Ivandis kills Vyrewatch!"),
    },
    postconditions = { Condition.ConversationText:new("Great story") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    postconditions = { Condition.ConversationText:new("good plan") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Return to Paterdomus.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "CKS",
    },
    actions = { Action.Direction:new(3424, 1465, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3423, 5, 9891, 20) },
  },
  {
    text = "Talk to Drezel.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Talk about something else"),
      Action.ConversationHighlight:new("Veliaf asked me to bring this Vyrewatch corpse to you."),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("soul to rest") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Climb down the stairway to the west.",
    actions = { Action.ModelHighlight:new(paterdomusStairs) },
    postconditions = { Condition.DistanceTo:new(3422, 1797, 9965, 4) },
  },
  {
    text = "Use the pyre logs on a funeral pyre.",
    actions = {
      Action.ModelHighlight:new(funeralPyre, { atLocation = Location:new(3422, 1797, 9954) }),
      Action.InventoryHighlight:new(teakPyreLogs),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(teakPyreLogs) },
  },
  {
    text = "Use the vyre corpse on the funeral pyre.",
    actions = {
      Action.ModelHighlight:new(funeralPyre, { atLocation = Location:new(3422, 1797, 9954) }),
      Action.InventoryHighlight:new(vyrewatchCorpse),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(vyrewatchCorpse) },
  },
  {
    text = "Light it up.",
    actions = { Action.ModelHighlight:new(funeralPyre) },
    postconditions = { Condition.ModelVisible:new(ornateTombKeyObj) },
  },
  {
    text = "Take the ornate tomb key from the pedestal.",
    actions = { Action.ModelHighlight:new(ornateTombKeyObj) },
    postconditions = { Condition.InventoryContains:new(ornateTombKey) },
  },
  {
    text = "Use the key on the wall storage unit.",
    actions = {
      Action.ModelHighlight:new(storageUnit),
      Action.InventoryHighlight:new(ornateTombKey),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Go upstairs.",
    title = "Finishing up",
    actions = { Action.ModelHighlight:new(paterdomusBasementStairs) },
    postconditions = { Condition.DistanceTo:new(3425, 712, 9899, 4) },
  },
  {
    text = "Talk to Drezel.",
    actions = { Action.Direction:new(3440, 677, 9895) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["drezel"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["drezel"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("bushed") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Return to the Burgh de Rott pub basement.<ul><li>It's recommended to do an easy Temple Trek to get back quickly.</li></ul>",
    actions = { Action.Direction:new(3490, 965, 3232, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    text = "Talk to Veliaf.",
    actions = { Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Legacy of Seergaze",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1208822400,
  prereqQuests = {
    "The Darkness of Hallowvale",
    "Shades of Mort'ton",
  },
  questReqs = {
    Types.QuestReq.skill("Agility", 29),
    Types.QuestReq.skill("Construction", 20),
    Types.QuestReq.skill("Crafting", 47),
    Types.QuestReq.skill("Magic", 49),
    Types.QuestReq.skill("Mining", 35),
    Types.QuestReq.skill("Slayer", 31),
  },
  neededItems = {
    ["Teak pyre logs"] = { quantity = 1, model = teakPyreLogs },
    ["Coal"] = { quantity = 1, model = Models.items["coal"], duringQuest = true },
    ["Cosmic rune"] = { quantity = 2, model = Models.items["cosmic rune"] },
    ["Air rune"] = { quantity = 6, model = Models.items["air rune"] },
    ["Mithril bar"] = { quantity = 1, model = Models.items["mithril bar"], duringQuest = true },
    ["Silver bar"] = { quantity = 1, model = Models.items["silver bar"], duringQuest = true },
    ["Silver weapon"] = { quantity = 1, model = Models.items["silver weapon"] },
    ["Emerald"] = { quantity = 1, model = Models.items["emerald"], duringQuest = true },
    ["Nails"] = { quantity = 10, model = Models.items["any nails"], duringQuest = true },
  },
  recommendedItems = {
    ["Food"] = { quantity = 1 },
    ["Havensilver greatsword"] = { quantity = 1 },
    ["Necromancy combat gear"] = { quantity = 1 },
    ["Druid pouch"] = { quantity = 1 },
    ["Invitation box"] = { quantity = 1 },
    ["Shortcut key"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Fistandantilus"] = { level = "77", quantity = 1 },
    ["Zaromark Sliver"] = { level = "77", quantity = 1 },
    ["Vyrewatch"] = { level = "72", quantity = 1 },
  },
})
