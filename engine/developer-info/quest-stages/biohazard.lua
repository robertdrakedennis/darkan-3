local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local omart = Model.new(4374, {
  [3100] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3105] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [3108] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3110] = Vertex.new(7, 724, -51, 108, 80, 56),
  [3490] = Vertex.new(0, 735, -7, 29, 141, 128),
})
local nurseSarah = Model.new(3579, {
  [1791] = Vertex.new(24, 738, -38, 31, 30, 29),
  [1815] = Vertex.new(-24, 738, -38, 31, 30, 29),
  [1837] = Vertex.new(-2, 716, -57, 75, 53, 23),
  [1843] = Vertex.new(2, 716, -57, 75, 53, 23),
  [1847] = Vertex.new(6, 716, -52, 75, 53, 23),
})
local chancy = Model.new(4482, {
  [1651] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [1659] = Vertex.new(2, 725, -59, 107, 79, 55),
  [1661] = Vertex.new(7, 724, -51, 107, 79, 55),
  [3477] = Vertex.new(-65, 670, 50, 55, 44, 11),
  [3508] = Vertex.new(65, 670, 50, 55, 44, 11),
})
local hops = Model.any({
  Model.new(4575, {
    [2710] = Vertex.new(-2, 725, -59, 107, 79, 55),
    [2715] = Vertex.new(-7, 724, -51, 107, 79, 55),
    [2718] = Vertex.new(2, 725, -59, 107, 79, 55),
    [2720] = Vertex.new(7, 724, -51, 107, 79, 55),
    [3682] = Vertex.new(0, 735, -7, 28, 140, 127),
  }),
  Model.new(4497, {
    [2980] = Vertex.new(-2, 725, -59, 107, 79, 55),
    [2985] = Vertex.new(-7, 724, -51, 107, 79, 55),
    [2988] = Vertex.new(2, 725, -59, 107, 79, 55),
    [2990] = Vertex.new(7, 724, -51, 107, 79, 55),
    [3424] = Vertex.new(0, 735, -7, 28, 140, 127),
  }),
})
local daVinci = Model.new(4929, {
  [3124] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [3132] = Vertex.new(2, 725, -59, 107, 79, 55),
  [3134] = Vertex.new(7, 724, -51, 107, 79, 55),
  [4176] = Vertex.new(-65, 670, 50, 55, 44, 11),
  [4207] = Vertex.new(65, 670, 50, 55, 44, 11),
})
local daVinciAndChancy = Model.new(5523, {
  [2084] = Vertex.new(-284, 672, -220, 91, 46, 37),
  [2093] = Vertex.new(-228, 672, -220, 91, 46, 37),
  [4776] = Vertex.new(220, 716, 272, 73, 58, 30),
  [4778] = Vertex.new(220, 720, 304, 73, 58, 30),
  [4784] = Vertex.new(220, 724, 280, 73, 58, 30),
})
local dressShopOwner = Model.new(3705, {
  [1504] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [1509] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [1512] = Vertex.new(2, 725, -59, 107, 79, 55),
  [1514] = Vertex.new(7, 724, -51, 107, 79, 55),
  [2419] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local guidor = Model.new(4458, {
  [3151] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [3156] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [3159] = Vertex.new(2, 725, -59, 107, 79, 55),
  [3161] = Vertex.new(7, 724, -51, 107, 79, 55),
  [3422] = Vertex.new(14, 757, -38, 135, 131, 124),
})
--#endregion
--#region Objects
local gapInFence = Model.new(774, {
  [23] = Vertex.new(7156, 1648, 2048, 51, 47, 47),
  [27] = Vertex.new(7140, 1648, 1864, 51, 47, 47),
  [333] = Vertex.new(7140, 1880, 1536, 57, 52, 52),
  [350] = Vertex.new(7156, 1880, 2048, 57, 52, 52),
  [359] = Vertex.new(7140, 1868, 1844, 57, 52, 52),
})
local cauldron = Model.new(570, {
  [91] = Vertex.new(88, 316, -92, 92, 73, 38),
  [94] = Vertex.new(88, 316, -92, 92, 73, 38),
  [95] = Vertex.new(84, 316, -88, 92, 73, 38),
  [97] = Vertex.new(56, 316, -124, 92, 73, 38),
  [99] = Vertex.new(88, 316, -92, 92, 73, 38),
})
local box = Model.new(468, {
  [36] = Vertex.new(2904, 1024, 7616, 82, 73, 52),
  [230] = Vertex.new(2896, 1296, 7316, 112, 101, 72),
  [252] = Vertex.new(2896, 1416, 7576, 82, 73, 52),
  [280] = Vertex.new(2896, 1452, 7576, 112, 101, 72),
  [324] = Vertex.new(2224, 1452, 7576, 112, 101, 72),
})
local openBox = Model.new(834, {
  [285] = Vertex.new(324, 392, 152, 82, 73, 52),
  [313] = Vertex.new(-324, 392, 152, 82, 73, 52),
  [557] = Vertex.new(280, 464, 152, 56, 50, 35),
  [643] = Vertex.new(336, 392, 152, 82, 73, 52),
  [663] = Vertex.new(-336, 392, 152, 82, 73, 52),
})
local gate = Model.new(984, {
  [290] = Vertex.new(19, 1474, 7168, 127, 127, 127),
  [620] = Vertex.new(28, 1412, 7168, 127, 127, 127),
  [688] = Vertex.new(19, 1615, 7168, 127, 127, 127),
  [698] = Vertex.new(19, 1615, 7168, 127, 127, 127),
  [962] = Vertex.new(19, 1442, 7168, 127, 127, 127),
})
--#endregion
--#region Items
local rottenTomato = Model.new(186, {
  [2] = Vertex.new(0, 80, -52, 99, 95, 52),
  [3] = Vertex.new(-8, 92, -28, 99, 95, 52),
  [4] = Vertex.new(-8, 92, -28, 99, 95, 52),
  [11] = Vertex.new(44, 80, 28, 99, 95, 52),
  [17] = Vertex.new(-48, 80, 16, 99, 95, 52),
})
local priestGownTop = Model.new(354, {
  [33] = Vertex.new(-132, 20, -124, 0, 0, 0),
  [35] = Vertex.new(-132, 20, -124, 0, 0, 0),
  [38] = Vertex.new(-132, 20, -124, 0, 0, 0),
  [333] = Vertex.new(132, 20, -124, 0, 0, 0),
  [337] = Vertex.new(132, 20, -124, 0, 0, 0),
})
local priestGownBottoms = Model.new(123, {
  [3] = Vertex.new(120, -4, -200, 0, 0, 0),
  [65] = Vertex.new(52, 12, 212, 0, 0, 0),
  [111] = Vertex.new(-120, -4, -200, 0, 0, 0),
  [116] = Vertex.new(-120, -4, -200, 0, 0, 0),
  [120] = Vertex.new(52, 12, 212, 0, 0, 0),
})
--#endregion
--#region Quest Items
local birdFeed = Model.new(90, {
  [43] = Vertex.new(-36, 0, -52, 64, 41, 5),
  [45] = Vertex.new(36, 0, -52, 64, 41, 5),
  [46] = Vertex.new(-36, 0, -52, 64, 41, 5),
  [57] = Vertex.new(-36, 0, -52, 64, 41, 5),
  [79] = Vertex.new(36, 0, -52, 64, 41, 5),
})
local pigeonCage = Model.new(576, {
  [3] = Vertex.new(144, 0, -160, 42, 30, 3),
  [549] = Vertex.new(144, 160, -160, 42, 30, 3),
  [552] = Vertex.new(144, 160, -160, 42, 30, 3),
  [572] = Vertex.new(-144, 160, 80, 42, 30, 3),
  [575] = Vertex.new(-144, 160, 80, 42, 30, 3),
})
local doctorsGown = Model.new(210, {
  [3] = Vertex.new(28, 32, 92, 163, 150, 149),
  [17] = Vertex.new(-120, 24, 16, 163, 150, 149),
  [34] = Vertex.new(120, 24, 16, 163, 150, 149),
  [139] = Vertex.new(120, 24, 16, 163, 150, 149),
  [191] = Vertex.new(-120, 24, 16, 163, 150, 149),
})
local key = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 154, 122, 14),
  [104] = Vertex.new(68, 16, 68, 154, 122, 14),
  [397] = Vertex.new(68, 16, 68, 154, 122, 14),
  [400] = Vertex.new(68, 16, 68, 154, 122, 14),
  [408] = Vertex.new(68, 16, 68, 154, 122, 14),
})
local distillator = Model.new(198, {
  [26] = Vertex.new(60, 8, -68, 68, 39, 6),
  [39] = Vertex.new(60, 8, -68, 68, 39, 6),
  [146] = Vertex.new(-68, 0, -76, 118, 136, 154),
  [150] = Vertex.new(-68, 0, -76, 118, 136, 154),
  [156] = Vertex.new(-68, 0, -76, 118, 136, 154),
})
local plagueSample = Model.new(330, {
  [225] = Vertex.new(-8, 116, 40, 0, 0, 0),
  [226] = Vertex.new(-32, 124, 36, 0, 0, 0),
  [231] = Vertex.new(28, 128, 36, 0, 0, 0),
  [232] = Vertex.new(12, 112, 40, 0, 0, 0),
  [237] = Vertex.new(8, 112, 40, 0, 0, 0),
})
local touchPaper = Model.new(42, {
  [3] = Vertex.new(-96, 0, 64, 149, 149, 136),
  [5] = Vertex.new(64, 0, -96, 149, 149, 136),
  [8] = Vertex.new(-112, 0, -112, 153, 152, 117),
  [9] = Vertex.new(-112, 0, 48, 153, 152, 117),
  [11] = Vertex.new(48, 0, -112, 153, 152, 117),
})
local liquidHoney = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 114, 102, 46),
    [2] = Vertex.new(4, 112, -12, 114, 102, 46),
    [3] = Vertex.new(4, 96, -12, 114, 102, 46),
    [4] = Vertex.new(-4, 96, -12, 114, 102, 46),
    [7] = Vertex.new(4, 96, 12, 114, 102, 46),
    [8] = Vertex.new(-4, 112, 12, 114, 102, 46),
    [9] = Vertex.new(-4, 96, 12, 114, 102, 46),
    [13] = Vertex.new(-12, 96, -4, 114, 102, 46),
    [17] = Vertex.new(-12, 112, -4, 114, 102, 46),
    [36] = Vertex.new(12, 96, -4, 114, 102, 46),
  }),
  Model.new(240, {
    [50] = Vertex.new(-4, 84, 12, 121, 95, 10, 0.8745),
    [74] = Vertex.new(-16, 68, 4, 121, 95, 10, 0.8745),
    [76] = Vertex.new(-16, 68, -4, 121, 95, 10, 0.8745),
    [77] = Vertex.new(-12, 84, -4, 121, 95, 10, 0.8745),
    [89] = Vertex.new(-4, 84, -12, 121, 95, 10, 0.8745),
    [93] = Vertex.new(-4, 68, -16, 121, 95, 10, 0.8745),
    [96] = Vertex.new(4, 84, -12, 121, 95, 10, 0.8745),
    [105] = Vertex.new(-4, 0, -36, 121, 95, 10, 0.8745),
    [114] = Vertex.new(12, 84, -4, 121, 95, 10, 0.8745),
    [116] = Vertex.new(-12, 12, -40, 121, 95, 10, 0.8745),
    [117] = Vertex.new(12, 12, -40, 121, 95, 10, 0.8745),
    [123] = Vertex.new(4, 0, -36, 121, 95, 10, 0.8745),
    [138] = Vertex.new(40, 12, -12, 121, 95, 10, 0.8745),
    [151] = Vertex.new(32, 0, -4, 121, 95, 10, 0.8745),
    [166] = Vertex.new(4, 68, 16, 121, 95, 10, 0.8745),
    [172] = Vertex.new(32, 0, 4, 121, 95, 10, 0.8745),
    [173] = Vertex.new(40, 12, 12, 121, 95, 10, 0.8745),
    [183] = Vertex.new(4, 0, 36, 121, 95, 10, 0.8745),
    [188] = Vertex.new(12, 12, 40, 121, 95, 10, 0.8745),
    [192] = Vertex.new(-4, 0, 36, 121, 95, 10, 0.8745),
  }),
})
local ethenea = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 114, 102, 46),
    [2] = Vertex.new(4, 112, -12, 114, 102, 46),
    [3] = Vertex.new(4, 96, -12, 114, 102, 46),
    [4] = Vertex.new(-4, 96, -12, 114, 102, 46),
    [7] = Vertex.new(4, 96, 12, 114, 102, 46),
    [8] = Vertex.new(-4, 112, 12, 114, 102, 46),
    [9] = Vertex.new(-4, 96, 12, 114, 102, 46),
    [13] = Vertex.new(-12, 96, -4, 114, 102, 46),
    [17] = Vertex.new(-12, 112, -4, 114, 102, 46),
    [36] = Vertex.new(12, 96, -4, 114, 102, 46),
  }),
  Model.new(240, {
    [50] = Vertex.new(-40, 12, -12, 134, 136, 147, 0.4980),
    [70] = Vertex.new(-4, 0, -36, 134, 136, 147, 0.4980),
    [74] = Vertex.new(12, 12, -40, 134, 136, 147, 0.4980),
    [75] = Vertex.new(4, 0, -36, 134, 136, 147, 0.4980),
    [76] = Vertex.new(4, 0, 36, 134, 136, 147, 0.4980),
    [77] = Vertex.new(12, 12, 40, 134, 136, 147, 0.4980),
    [89] = Vertex.new(40, 12, 12, 134, 136, 147, 0.4980),
    [90] = Vertex.new(32, 0, 4, 134, 136, 147, 0.4980),
    [105] = Vertex.new(16, 68, 4, 134, 136, 147, 0.4980),
    [114] = Vertex.new(-12, 12, 40, 134, 136, 147, 0.4980),
    [116] = Vertex.new(4, 68, 16, 134, 136, 147, 0.4980),
    [117] = Vertex.new(-4, 68, 16, 134, 136, 147, 0.4980),
    [123] = Vertex.new(4, 84, 12, 134, 136, 147, 0.4980),
    [138] = Vertex.new(-12, 84, 4, 134, 136, 147, 0.4980),
    [163] = Vertex.new(-4, 68, -16, 134, 136, 147, 0.4980),
    [166] = Vertex.new(-12, 12, -40, 134, 136, 147, 0.4980),
    [173] = Vertex.new(-4, 84, -12, 134, 136, 147, 0.4980),
    [174] = Vertex.new(4, 84, -12, 134, 136, 147, 0.4980),
    [183] = Vertex.new(-16, 68, -4, 134, 136, 147, 0.4980),
    [188] = Vertex.new(-12, 84, -4, 134, 136, 147, 0.4980),
  }),
})
local sulphuricBroline = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 114, 102, 46),
    [2] = Vertex.new(4, 112, -12, 114, 102, 46),
    [3] = Vertex.new(4, 96, -12, 114, 102, 46),
    [4] = Vertex.new(-4, 96, -12, 114, 102, 46),
    [7] = Vertex.new(4, 96, 12, 114, 102, 46),
    [8] = Vertex.new(-4, 112, 12, 114, 102, 46),
    [9] = Vertex.new(-4, 96, 12, 114, 102, 46),
    [13] = Vertex.new(-12, 96, -4, 114, 102, 46),
    [17] = Vertex.new(-12, 112, -4, 114, 102, 46),
    [36] = Vertex.new(12, 96, -4, 114, 102, 46),
  }),
  Model.new(240, {
    [50] = Vertex.new(-4, 84, 12, 13, 153, 38, 0.8745),
    [74] = Vertex.new(-16, 68, 4, 13, 153, 38, 0.8745),
    [76] = Vertex.new(-16, 68, -4, 13, 153, 38, 0.8745),
    [77] = Vertex.new(-12, 84, -4, 13, 153, 38, 0.8745),
    [89] = Vertex.new(-4, 84, -12, 13, 153, 38, 0.8745),
    [93] = Vertex.new(-4, 68, -16, 13, 153, 38, 0.8745),
    [96] = Vertex.new(4, 84, -12, 13, 153, 38, 0.8745),
    [105] = Vertex.new(-4, 0, -36, 13, 153, 38, 0.8745),
    [114] = Vertex.new(12, 84, -4, 13, 153, 38, 0.8745),
    [116] = Vertex.new(-12, 12, -40, 13, 153, 38, 0.8745),
    [117] = Vertex.new(12, 12, -40, 13, 153, 38, 0.8745),
    [123] = Vertex.new(4, 0, -36, 13, 153, 38, 0.8745),
    [138] = Vertex.new(40, 12, -12, 13, 153, 38, 0.8745),
    [151] = Vertex.new(32, 0, -4, 13, 153, 38, 0.8745),
    [166] = Vertex.new(4, 68, 16, 13, 153, 38, 0.8745),
    [172] = Vertex.new(32, 0, 4, 13, 153, 38, 0.8745),
    [173] = Vertex.new(40, 12, 12, 13, 153, 38, 0.8745),
    [183] = Vertex.new(4, 0, 36, 13, 153, 38, 0.8745),
    [188] = Vertex.new(12, 12, 40, 13, 153, 38, 0.8745),
    [192] = Vertex.new(-4, 0, 36, 13, 153, 38, 0.8745),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Elena in a house just west of the East Ardougne north bank, across the river.",
    title = "Getting started",
    actions = {
      Action.Direction:new(2592, 1157, 3336, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["elena"], { distance = 3 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Elena.",
    actions = {
      Action.Direction:new(2592, 1157, 3336, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["elena"], { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("he lives next to the") },
  },
  {
    text = "Talk to Jerico, south of the Ardougne north bank.",
    title = "Hoppin' the wall",
    actions = {
      Action.Direction:new(2614, 1189, 3324, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["jerico"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("Thanks Jerico.") },
  },
  {
    text = "Search the cupboard.",
    actions = { Action.Direction:new(2611, 1589, 3326) },
    postconditions = { Condition.InventoryContains:new(birdFeed) },
  },
  {
    text = "Pick up a pigeon cage behind Jerico's house.",
    actions = { Action.ModelHighlight:new(pigeonCage) },
    postconditions = { Condition.InventoryContains:new(pigeonCage) },
  },
  {
    text = "<i>Use</i> bird feed on the watchtower fence.",
    actions = {
      Action.Direction:new(2563, 1293, 3304),
      Action.InventoryHighlight:new(birdFeed),
    },
    postconditions = { Condition.ChatText:new("The mourners do not seem") },
  },
  {
    text = "Open the pigeon cage.",
    actions = { Action.InventoryHighlight:new(pigeonCage) },
    postconditions = { Condition.ChatText:new("The pigeons fly towards the watch tower.") },
  },
  {
    text = "Talk to Omart along the wall to the south.",
    actions = {
      Action.ModelHighlight:new(omart, { distance = 8 }),
      Action.Direction:new(2559, 1357, 3266, { distance = 8 }),
      Action.ConversationHighlight:new("Ok, lets do it."),
    },
    postconditions = { Condition.DistanceTo:new(2554, 1157, 3268, 2) },
  },
  {
    text = "Pick up the rotten apple to the north.",
    title = "Infiltration",
    actions = {
      Action.Direction:new(2535, 1253, 3333, { distance = 12 }),
      Action.ModelHighlight:new(rottenTomato, { distance = 12 }),
    },
    postconditions = { Condition.InventoryContains:new(rottenTomato) },
  },
  {
    text = "Squeeze through the gap in the metal fence.",
    actions = { Action.ModelHighlight:new(gapInFence) },
    postconditions = { Condition.DistanceTo:new(2544, 1285, 3331, 2) },
  },
  {
    text = "Use the rotten apple on the cauldron.<ul><li>Don't eat the rotten apple.</li></ul>",
    actions = {
      Action.ModelHighlight:new(cauldron),
      Action.InventoryHighlight:new(rottenTomato),
    },
    postconditions = { Condition.ChatText:new("That wasn't very nice") },
  },
  {
    text = "Talk to Nurse Sarah, south-west of the church.",
    actions = {
      Action.Direction:new(2516, 1029, 3274, { distance = 6 }),
      Action.ModelHighlight:new(nurseSarah, { distance = 6 }),
    },
    postconditions = { Condition.ConversationText:new("strange that") },
  },
  {
    text = "Search the box on the north side of the house to obtain a doctors' gown.",
    actions = { Action.ModelHighlight:new(box), Action.ModelHighlight:new(openBox) },
    postconditions = { Condition.InventoryContains:new(doctorsGown) },
  },
  {
    text = "Enter the Mourner Headquarters with the gown equipped.",
    actions = {
      Action.Direction:new(2551, 1285, 3320.5),
      Action.InventoryHighlight:new(doctorsGown),
    },
    postconditions = { Condition.DistanceTo:new(2551, 1285, 3322, 1) },
  },
  {
    text = "Go upstairs.",
    actions = { Action.Direction:new(2544, 1685, 3325) },
    postconditions = { Condition.DistanceToWithHeight:new(2545, 2245, 3325, 4) },
  },
  {
    text = "Kill a Mourner.",
    actions = { Action.ModelHighlight:new(Models.npcs["mourner"], { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(key) },
  },
  {
    text = "<i>Use</i> the key on the gate.",
    actions = { Action.Direction:new(2551.5, 2945, 3325.5), Action.InventoryHighlight:new(key) },
    postconditions = { Condition.DistanceTo:new(2553, 2245, 3326, 1) },
  },
  {
    text = "Search the crate.",
    actions = { Action.Direction:new(2554, 2245, 3327) },
    postconditions = { Condition.InventoryContains:new(distillator) },
  },
  {
    text = "Ardougne lodestone, then talk to Elena again.",
    actions = {
      Action.Direction:new(2592, 1157, 3336, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["elena"], { distance = 3 }),
    },
    postconditions = { Condition.ConversationText:new("Those vials are fragile") },
  },
  {
    text = "<b>Do not teleport.</b> Bank your plague sample.",
    title = "Samples",
    actions = { Action.Direction:new(2616, 1689, 3331) },
    postconditions = { Condition.InventoryDoesNotContain:new(plagueSample) },
  },
  {
    text = "Lodestone to Falador and withdraw the plague sample.",
    actions = { Action.Direction:new(2946, 1125, 3367) },
    postconditions = { Condition.InventoryContains:new(plagueSample) },
  },
  {
    text = "Talk to the Chemist west of Rimmington. Be careful to choose the right chat option.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["chemist"], { distance = 8 }),
      Action.Direction:new(2932, 981, 3210, { distance = 8 }),
      Action.ConversationHighlight:new("Ask about Biohazard."),
      Action.ConversationHighlight:new("It's ok, I'm Elena's friend."),
      Action.ConversationHighlight:new("I just need some touch paper for a guy called Guidor."),
    },
    postconditions = { Condition.InventoryContains:new(touchPaper) },
  },
  {
    text = "Talk to Chancy outside.",
    actions = {
      Action.ModelHighlight:new(chancy),
      Action.ConversationHighlight:new("You give him the vial of liquid honey..."),
    },
    postconditions = { Condition.ChatText:new("vial of liquid honey") },
  },
  {
    text = "Talk to Da Vinci.",
    actions = {
      Action.ModelHighlight:new(daVinci),
      Action.ConversationHighlight:new("You give him the vial of ethenea..."),
    },
    postconditions = { Condition.ChatText:new("vial of ethenea") },
  },
  {
    text = "Talk to Hops.",
    actions = {
      Action.ModelHighlight:new(hops),
      Action.ConversationHighlight:new("You give him the vial of sulphuric broline..."),
    },
    postconditions = { Condition.ChatText:new("vial of sulphuric") },
  },
  {
    text = "Deposit your plague sample again. A nearby deposit box is marked.",
    actions = { Action.Direction:new(3047, 1241, 3237) },
    postconditions = { Condition.InventoryDoesNotContain:new(plagueSample) },
  },
  {
    text = "Lodestone to Varrock, then retrieve the plague sample from the east bank.",
    title = "Delivering the samples",
    neededItems = { ["Touch paper"] = { quantity = 1 } },
    actions = { Action.Direction:new(3254, 1937, 3419) },
    postconditions = { Condition.InventoryContains:new(plagueSample) },
  },
  {
    text = "Go through the gate in the south-east corner of Varrock.",
    actions = {
      Action.ModelHighlight:new(gate, { distance = 12 }),
      Action.Direction:new(3263.5, 1125, 3405.5, { distance = 12 }),
    },
    postconditions = { Condition.DistanceTo:new(3266, 1125, 3405, 2) },
  },
  {
    text = "Talk to the 3 chemist's assistants.",
    actions = {
      Action.Direction:new(3269, 1125, 3390, { distance = 8 }),
      Action.ModelHighlight:new(hops, { distance = 8 }),
      Action.ModelHighlight:new(daVinciAndChancy, { distance = 8 }),
    },
    postconditions = { Condition.InventoryContains:new(liquidHoney) },
  },
  {
    actions = {
      Action.Direction:new(3269, 1125, 3390, { distance = 8 }),
      Action.ModelHighlight:new(hops, { distance = 8 }),
      Action.ModelHighlight:new(daVinciAndChancy, { distance = 8 }),
    },
    postconditions = { Condition.InventoryContains:new(ethenea) },
  },
  {
    actions = {
      Action.Direction:new(3269, 1125, 3390, { distance = 8 }),
      Action.ModelHighlight:new(hops, { distance = 8 }),
      Action.ModelHighlight:new(daVinciAndChancy, { distance = 8 }),
    },
    postconditions = { Condition.InventoryContains:new(sulphuricBroline) },
  },
  {
    text = "Buy a priest gown top and bottoms.",
    actions = {
      Action.Direction:new(3281, 1285, 3397, { distance = 4 }),
      Action.ModelHighlight:new(dressShopOwner, { distance = 4 }),
    },
    postconditions = { Condition.InventoryContains:new(priestGownTop) },
  },
  {
    actions = {
      Action.Direction:new(3281, 1285, 3397, { distance = 4 }),
      Action.ModelHighlight:new(dressShopOwner, { distance = 4 }),
    },
    postconditions = { Condition.InventoryContains:new(priestGownBottoms) },
  },
  {
    text = "Equip the priest gown, then talk to Guidor.",
    actions = {
      Action.Direction:new(3284, 1125, 3382, { distance = 8 }),
      Action.ModelHighlight:new(guidor, { distance = 8 }),
      Action.InventoryHighlight:new(priestGownTop),
      Action.InventoryHighlight:new(priestGownBottoms),
      Action.ConversationHighlight:new("I've come to ask your assistance in stopping a plague."),
      Action.ConversationHighlight:new("I've been sent by your old pupil Elena."),
      Action.ConversationHighlight:new("That's why Elena wanted you to do it."),
    },
    postconditions = { Condition.ConversationText:new("The only question is") },
  },
  {
    text = "Lodestone to Ardougne, then talk to Elena.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(2592, 1157, 3336, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["elena"], { distance = 3 }),
    },
    postconditions = { Condition.ConversationText:new("King of East Ardougne") }, --not tested
  },
  {
    text = "Talk to King Lathas, on the 1st floor (2nd floor US) of the castle.",
    actions = { Action.Direction:new(2571, 2225, 3307) },
    postconditions = { Condition.DistanceToWithHeight:new(2572, 2917, 3306, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["king lathas"]),
      Action.ConversationHighlight:new("I don't understand..."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Biohazard",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1035331200,
  prereqQuests = { "Plague City" },
  questReqs = {},
  neededItems = { ["coins"] = { quantity = 10 } },
  recommendedItems = {},
  combatNPCs = {},
})
