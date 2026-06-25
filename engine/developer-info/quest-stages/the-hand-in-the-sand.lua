local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local bert = Model.new(4434, {
  [3307] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [3312] = Vertex.new(-7, 724, -51, 109, 81, 57),
  [3315] = Vertex.new(2, 725, -59, 109, 81, 57),
  [3317] = Vertex.new(7, 724, -51, 109, 81, 57),
  [4061] = Vertex.new(-30, 721, -31, 59, 50, 46),
})
local bartender = Model.new(3453, {
  [1933] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [1938] = Vertex.new(-7, 724, -51, 109, 81, 57),
  [1941] = Vertex.new(2, 725, -59, 109, 81, 57),
  [1943] = Vertex.new(7, 724, -51, 109, 81, 57),
  [3013] = Vertex.new(0, 735, -7, 29, 142, 129),
})
local guardCaptain = Model.new(4635, {
  [2305] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [2313] = Vertex.new(2, 725, -59, 109, 81, 57),
  [3872] = Vertex.new(-25, 758, -32, 98, 90, 90),
  [3897] = Vertex.new(4, 726, -67, 98, 90, 90),
  [3902] = Vertex.new(-4, 726, -67, 98, 90, 90),
})
local sandy = Model.new(4461, {
  [2878] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [2883] = Vertex.new(-7, 724, -51, 109, 81, 57),
  [2886] = Vertex.new(2, 725, -59, 109, 81, 57),
  [2888] = Vertex.new(7, 724, -51, 109, 81, 57),
  [3661] = Vertex.new(0, 735, -7, 29, 142, 129),
})
local mazion = Model.new(3846, {
  [2671] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [2676] = Vertex.new(-7, 724, -51, 109, 81, 57),
  [2679] = Vertex.new(2, 725, -59, 109, 81, 57),
  [2681] = Vertex.new(7, 724, -51, 109, 81, 57),
  [2938] = Vertex.new(0, 735, -7, 29, 142, 129),
})
--#endregion
--#region Objects
local sandPit = Model.new(1752, {
  [69] = Vertex.new(384, 0, -408, 65, 37, 6),
  [87] = Vertex.new(-384, 0, 408, 74, 42, 7),
  [336] = Vertex.new(-404, 72, -408, 74, 42, 7),
  [339] = Vertex.new(408, 64, -380, 74, 42, 7),
  [357] = Vertex.new(-408, 64, 380, 65, 37, 6),
})
local closedDoor = Model.new(582, {
  [15] = Vertex.new(4700, 1548, 6127, 177, 177, 177),
  [202] = Vertex.new(4690, 1801, 6120, 83, 74, 58),
  [211] = Vertex.new(4629, 1801, 6120, 83, 74, 58),
  [315] = Vertex.new(4696, 1563, 6090, 112, 109, 109),
  [555] = Vertex.new(4667, 1810, 6126, 84, 78, 68),
})
local openDoor = Model.new(606, {
  [1] = Vertex.new(-241, 455, -211, 177, 177, 177),
  [6] = Vertex.new(-241, 391, -211, 177, 177, 177),
  [12] = Vertex.new(-241, 391, -147, 177, 177, 177),
  [42] = Vertex.new(-168, 391, -211, 177, 177, 177),
  [315] = Vertex.new(-168, 603, 202, 112, 109, 109),
})
local counter = Model.new(1020, {
  [636] = Vertex.new(244, 485, -41, 98, 90, 90),
  [642] = Vertex.new(232, 485, -62, 98, 90, 90),
  [650] = Vertex.new(-502, 316, -179, 30, 28, 23),
  [653] = Vertex.new(372, 316, -179, 30, 28, 23),
  [665] = Vertex.new(-478, 316, 35, 30, 28, 23),
})
local coffeeMug = Model.new(558, {
  [63] = Vertex.new(-176, 248, 188, 72, 51, 22),
  [95] = Vertex.new(-176, 248, 188, 59, 38, 5),
  [113] = Vertex.new(-252, 248, -216, 83, 62, 34),
  [131] = Vertex.new(252, 288, -212, 72, 51, 22),
  [221] = Vertex.new(-252, 288, -212, 83, 62, 34),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local bertsRota = Model.new(462, {
  [201] = Vertex.new(-60, 12, 52, 0, 0, 0),
  [237] = Vertex.new(-52, 12, -68, 0, 0, 0),
  [239] = Vertex.new(28, 12, -76, 0, 0, 0),
  [243] = Vertex.new(-32, 12, 68, 0, 0, 0),
  [245] = Vertex.new(48, 12, 60, 0, 0, 0),
})
local sandysRota = Model.new(432, {
  [201] = Vertex.new(-60, 12, 52, 0, 0, 0),
  [203] = Vertex.new(20, 12, 44, 0, 0, 0),
  [207] = Vertex.new(-40, 12, 32, 0, 0, 0),
  [213] = Vertex.new(-32, 12, 68, 0, 0, 0),
  [215] = Vertex.new(48, 12, 60, 0, 0, 0),
})
local sand = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 132, 116, 41),
  [34] = Vertex.new(100, 0, 60, 132, 116, 41),
  [42] = Vertex.new(100, 0, 60, 132, 116, 41),
  [64] = Vertex.new(132, 0, -24, 132, 116, 41),
  [72] = Vertex.new(132, 0, -24, 132, 116, 41),
})
local magicalOrb = Model.new(288, {
  [2] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
  [6] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
  [8] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
  [12] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
  [71] = Vertex.new(-8, 92, -24, 106, 106, 115, 0.6235),
})
local bottledWater = Model.multi({
  Model.new(180, {
    [1] = Vertex.new(-32, 0, 16, 92, 93, 120),
    [2] = Vertex.new(-40, 16, -20, 92, 93, 120),
    [3] = Vertex.new(-32, 0, -16, 92, 93, 120),
    [5] = Vertex.new(-40, 16, 20, 92, 93, 120),
    [7] = Vertex.new(-4, 0, 20, 92, 93, 120),
    [12] = Vertex.new(-12, 16, -24, 92, 93, 120),
    [15] = Vertex.new(-4, 0, -20, 92, 93, 120),
    [17] = Vertex.new(-12, 16, 24, 92, 93, 120),
    [19] = Vertex.new(4, 0, 20, 92, 93, 120),
    [24] = Vertex.new(12, 16, -24, 92, 93, 120),
    [115] = Vertex.new(-12, 140, 4, 116, 104, 48),
    [116] = Vertex.new(-12, 164, -4, 116, 104, 48),
    [117] = Vertex.new(-12, 140, -4, 116, 104, 48),
    [120] = Vertex.new(-4, 164, -12, 116, 104, 48),
    [123] = Vertex.new(-4, 140, -12, 116, 104, 48),
    [125] = Vertex.new(-12, 164, 4, 116, 104, 48),
    [130] = Vertex.new(-4, 140, 12, 116, 104, 48),
    [135] = Vertex.new(4, 164, -12, 116, 104, 48),
    [141] = Vertex.new(4, 140, -12, 116, 104, 48),
    [143] = Vertex.new(-4, 164, 12, 116, 104, 48),
  }),
  Model.new(192, {
    [2] = Vertex.new(4, 140, 12, 98, 90, 90, 0.5608),
    [3] = Vertex.new(-4, 140, 12, 98, 90, 90, 0.5608),
    [6] = Vertex.new(-4, 140, 12, 98, 90, 90, 0.5608),
    [11] = Vertex.new(12, 140, 4, 98, 90, 90, 0.5608),
    [14] = Vertex.new(-20, 140, 4, 98, 90, 90, 0.5608),
    [17] = Vertex.new(20, 140, 4, 98, 90, 90, 0.5608),
    [22] = Vertex.new(4, 120, 20, 98, 90, 90, 0.5608),
    [25] = Vertex.new(20, 120, 4, 98, 90, 90, 0.5608),
    [33] = Vertex.new(-4, 120, 20, 98, 90, 90, 0.5608),
    [42] = Vertex.new(-20, 120, 4, 98, 90, 90, 0.5608),
    [92] = Vertex.new(-12, 140, -4, 98, 90, 90, 0.5608),
    [96] = Vertex.new(-12, 140, -4, 98, 90, 90, 0.5608),
    [97] = Vertex.new(-40, 56, -20, 98, 90, 90, 0.5608),
    [99] = Vertex.new(-40, 96, -20, 98, 90, 90, 0.5608),
    [104] = Vertex.new(-12, 56, -24, 98, 90, 90, 0.5608),
    [116] = Vertex.new(-12, 120, -4, 98, 90, 90, 0.5608),
    [135] = Vertex.new(12, 120, -4, 98, 90, 90, 0.5608),
    [136] = Vertex.new(12, 56, -24, 98, 90, 90, 0.5608),
    [151] = Vertex.new(40, 96, -20, 98, 90, 90, 0.5608),
    [158] = Vertex.new(40, 56, -20, 98, 90, 90, 0.5608),
  }),
})
local redberryJuice = Model.multi({
  Model.new(180, {
    [1] = Vertex.new(-32, 0, 16, 113, 51, 47),
    [2] = Vertex.new(-40, 16, -20, 113, 51, 47),
    [3] = Vertex.new(-32, 0, -16, 113, 51, 47),
    [5] = Vertex.new(-40, 16, 20, 113, 51, 47),
    [7] = Vertex.new(-4, 0, 20, 113, 51, 47),
    [12] = Vertex.new(-12, 16, -24, 113, 51, 47),
    [15] = Vertex.new(-4, 0, -20, 113, 51, 47),
    [17] = Vertex.new(-12, 16, 24, 113, 51, 47),
    [19] = Vertex.new(4, 0, 20, 113, 51, 47),
    [24] = Vertex.new(12, 16, -24, 113, 51, 47),
    [115] = Vertex.new(-12, 140, 4, 116, 104, 48),
    [116] = Vertex.new(-12, 164, -4, 116, 104, 48),
    [117] = Vertex.new(-12, 140, -4, 116, 104, 48),
    [120] = Vertex.new(-4, 164, -12, 116, 104, 48),
    [123] = Vertex.new(-4, 140, -12, 116, 104, 48),
    [125] = Vertex.new(-12, 164, 4, 116, 104, 48),
    [130] = Vertex.new(-4, 140, 12, 116, 104, 48),
    [135] = Vertex.new(4, 164, -12, 116, 104, 48),
    [141] = Vertex.new(4, 140, -12, 116, 104, 48),
    [143] = Vertex.new(-4, 164, 12, 116, 104, 48),
  }),
  Model.new(192, {
    [2] = Vertex.new(4, 140, 12, 98, 90, 90, 0.5608),
    [3] = Vertex.new(-4, 140, 12, 98, 90, 90, 0.5608),
    [6] = Vertex.new(-4, 140, 12, 98, 90, 90, 0.5608),
    [11] = Vertex.new(12, 140, 4, 98, 90, 90, 0.5608),
    [14] = Vertex.new(-20, 140, 4, 98, 90, 90, 0.5608),
    [17] = Vertex.new(20, 140, 4, 98, 90, 90, 0.5608),
    [22] = Vertex.new(4, 120, 20, 98, 90, 90, 0.5608),
    [25] = Vertex.new(20, 120, 4, 98, 90, 90, 0.5608),
    [33] = Vertex.new(-4, 120, 20, 98, 90, 90, 0.5608),
    [42] = Vertex.new(-20, 120, 4, 98, 90, 90, 0.5608),
    [92] = Vertex.new(-12, 140, -4, 98, 90, 90, 0.5608),
    [96] = Vertex.new(-12, 140, -4, 98, 90, 90, 0.5608),
    [97] = Vertex.new(-40, 56, -20, 98, 90, 90, 0.5608),
    [99] = Vertex.new(-40, 96, -20, 98, 90, 90, 0.5608),
    [104] = Vertex.new(-12, 56, -24, 98, 90, 90, 0.5608),
    [116] = Vertex.new(-12, 120, -4, 98, 90, 90, 0.5608),
    [135] = Vertex.new(12, 120, -4, 98, 90, 90, 0.5608),
    [136] = Vertex.new(12, 56, -24, 98, 90, 90, 0.5608),
    [151] = Vertex.new(40, 96, -20, 98, 90, 90, 0.5608),
    [158] = Vertex.new(40, 56, -20, 98, 90, 90, 0.5608),
  }),
})
local pinkDye = Model.new(372, {
  [308] = Vertex.new(-4, 164, 12, 116, 104, 48),
  [312] = Vertex.new(-4, 164, 12, 116, 104, 48),
  [341] = Vertex.new(-12, 164, -4, 116, 104, 48),
  [344] = Vertex.new(-12, 164, -4, 116, 104, 48),
  [348] = Vertex.new(-12, 164, -4, 116, 104, 48),
})
local roseTintedLens = Model.new(84, {
  [3] = Vertex.new(-48, 28, -152, 164, 90, 86, 0.5608),
  [37] = Vertex.new(-48, 28, -152, 164, 90, 86, 0.5608),
  [39] = Vertex.new(-104, 0, -100, 164, 90, 86, 0.5608),
  [40] = Vertex.new(-48, 28, -152, 164, 90, 86, 0.5608),
  [69] = Vertex.new(-48, 28, -152, 164, 90, 86, 0.5608),
})
local truthSerum = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 116, 104, 48),
    [2] = Vertex.new(4, 112, -12, 116, 104, 48),
    [3] = Vertex.new(4, 96, -12, 116, 104, 48),
    [4] = Vertex.new(-4, 96, -12, 116, 104, 48),
    [7] = Vertex.new(4, 96, 12, 116, 104, 48),
  }),
  Model.new(240, {
    [50] = Vertex.new(-4, 84, 12, 145, 115, 60, 0.8745),
    [74] = Vertex.new(-16, 68, 4, 145, 115, 60, 0.8745),
    [76] = Vertex.new(-16, 68, -4, 145, 115, 60, 0.8745),
    [77] = Vertex.new(-12, 84, -4, 145, 115, 60, 0.8745),
    [89] = Vertex.new(-4, 84, -12, 145, 115, 60, 0.8745),
    [93] = Vertex.new(-4, 68, -16, 145, 115, 60, 0.8745),
    [96] = Vertex.new(4, 84, -12, 145, 115, 60, 0.8745),
    [105] = Vertex.new(-4, 0, -36, 145, 115, 60, 0.8745),
    [114] = Vertex.new(12, 84, -4, 145, 115, 60, 0.8745),
    [116] = Vertex.new(-12, 12, -40, 145, 115, 60, 0.8745),
    [117] = Vertex.new(12, 12, -40, 145, 115, 60, 0.8745),
    [123] = Vertex.new(4, 0, -36, 145, 115, 60, 0.8745),
    [138] = Vertex.new(40, 12, -12, 145, 115, 60, 0.8745),
    [151] = Vertex.new(32, 0, -4, 145, 115, 60, 0.8745),
    [166] = Vertex.new(4, 68, 16, 145, 115, 60, 0.8745),
    [172] = Vertex.new(32, 0, 4, 145, 115, 60, 0.8745),
    [173] = Vertex.new(40, 12, 12, 145, 115, 60, 0.8745),
    [183] = Vertex.new(4, 0, 36, 145, 115, 60, 0.8745),
    [188] = Vertex.new(12, 12, 40, 145, 115, 60, 0.8745),
    [192] = Vertex.new(-4, 0, 36, 145, 115, 60, 0.8745),
  }),
})
local MagicalOrbA = Model.multi({
  Model.new(288, {
    [2] = Vertex.new(-20, 88, 4, 72, 113, 101),
    [6] = Vertex.new(-20, 88, 4, 72, 113, 101),
    [8] = Vertex.new(-20, 88, 4, 72, 113, 101),
    [12] = Vertex.new(-20, 88, 4, 72, 113, 101),
    [71] = Vertex.new(-4, 88, -20, 72, 113, 101),
  }),
  Model.new(288, {
    [2] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
    [6] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
    [8] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
    [12] = Vertex.new(-24, 92, 8, 106, 106, 115, 0.6235),
    [71] = Vertex.new(-8, 92, -24, 106, 106, 115, 0.6235),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Bert in the building near Yanille's house portal.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Yanille lodestone",
      url = "Yanille_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2551, 965, 3100) },
    postconditions = { Condition.ModelVisible:new(bert) },
  },
  {
    actions = {
      Action.ModelHighlight:new(bert),
      Action.ConversationHighlight:new("Eww a hand, in the sand! Why haven't you told the authorities?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(bert) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Bert.",
    actions = { Action.ModelHighlight:new(bert) },
    postconditions = { Condition.ConversationText:new("2 seconds") },
  },
  {
    text = "Use an empty bucket on the sand pit.",
    actions = {
      Action.ModelHighlight:new(sandPit),
      Action.InventoryHighlight:new(Models.items["bucket"]),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["bucket of sand"]) },
  },
  {
    text = "Buy a beer from the bartender.",
    actions = { Action.Direction:new(2553, 997, 3079) },
    postconditions = { Condition.ModelVisible:new(bartender) },
  },
  {
    actions = {
      Action.ModelHighlight:new(bartender),
      Action.ConversationHighlight:new("One cheap beer please!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(bartender) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(Models.items["beer"]) },
  },
  {
    text = "Talk to the Guard Captain in the nearby pub.",
    title = "A helping hand",
    neededItems = { ["Beer"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2553, 997, 3079) },
    postconditions = { Condition.ModelVisible:new(guardCaptain) },
  },
  {
    actions = { Action.ModelHighlight:new(guardCaptain) },
    jumpconditions = { Condition.ModelNotVisible:new(guardCaptain) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("talk to a wizard then") },
  },
  {
    text = "Ring the bell on the eastern side of the Wizards' Guild.",
    actions = { Action.Direction:new(2598, 1765, 3085) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["zavistic rarve"]) },
  },
  {
    text = "Talk to the Zavistic Rarve.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["zavistic rarve"]),
      Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you."),
    },
    postconditions = { Condition.ConversationText:new("isn't natural") },
  },
  {
    text = "Return to Bert.",
    actions = { Action.Direction:new(2551, 965, 3100) },
    postconditions = { Condition.ModelVisible:new(bert) },
  },
  {
    actions = { Action.ModelHighlight:new(bert) },
    jumpconditions = { Condition.ModelNotVisible:new(bert) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Sandy in Brimhaven") },
  },
  {
    text = "Talk to Sandy in Brimhaven.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Karamja lodestone",
      url = "Karamja_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2788, 2629, 3176) },
    postconditions = { Condition.ModelVisible:new(sandy) },
  },
  {
    actions = { Action.ModelHighlight:new(sandy) },
    jumpconditions = { Condition.ModelNotVisible:new(sandy) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("take a look around") },
  },
  {
    text = "Search his desk to receive Sandy's rota.",
    actions = { Action.Direction:new(2788.5, 2229, 3174) },
    postconditions = { Condition.InventoryContains:new(sandysRota) },
  },
  {
    text = "Pickpocket Sandy for some sand.",
    actions = { Action.ModelHighlight:new(sandy) },
    postconditions = { Condition.InventoryContains:new(sand) },
  },
  {
    text = "Return to Bert.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Yanille lodestone",
      url = "Yanille_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2551, 965, 3100) },
    postconditions = { Condition.ModelVisible:new(bert) },
  },
  {
    actions = { Action.ModelHighlight:new(bert) },
    jumpconditions = { Condition.ModelNotVisible:new(bert) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("quick sharp") },
  },
  {
    text = "Ring the Wizards' Guild bell.",
    actions = { Action.Direction:new(2598, 1765, 3085) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["zavistic rarve"]) },
  },
  {
    text = "Talk to the Zavistic Rarve.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["zavistic rarve"]),
      Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you."),
      Action.ConversationHighlight:new("Can you help me more?."),
      Action.ConversationHighlight:new("Okay"),
    },
    postconditions = { Condition.ConversationText:new("find the murderer") },
  },
  {
    text = "Talk to Betty in Port Sarim's magic shop.",
    title = "Truth serum",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Port Sarim lodestone",
      url = "Port_Sarim_lodestone_icon.png",
    },
    neededItems = {
      ["Empty vial"] = { quantity = 1 },
      ["Redberries"] = { quantity = 1 },
      ["White berries"] = { quantity = 1 },
      ["Lantern lens"] = { quantity = 1 },
      ["Sand"] = { quantity = 1, model = sand },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(3014, 965, 3259) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["betty"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["betty"]),
      Action.ConversationHighlight:new("Talk to Betty about The Hand in the Sand."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["betty"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("good") },
  },
  {
    text = "Use redberries on the bottled water.",
    actions = {
      Action.InventoryHighlight:new(Models.items["redberries"]),
      Action.InventoryHighlight:new(bottledWater),
    },
    postconditions = { Condition.InventoryContains:new(redberryJuice) },
  },
  {
    text = "Use white berries on the redberry juice.",
    actions = {
      Action.InventoryHighlight:new(Models.items["white berries"]),
      Action.InventoryHighlight:new(redberryJuice),
    },
    postconditions = { Condition.InventoryContains:new(pinkDye) },
  },
  {
    text = "Use the pink dye on your lantern lens.",
    actions = {
      Action.InventoryHighlight:new(pinkDye),
      Action.InventoryHighlight:new(Models.items["lantern lens"]),
    },
    postconditions = { Condition.InventoryContains:new(roseTintedLens) },
  },
  {
    text = "Talk to Betty again.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["betty"]),
      Action.ConversationHighlight:new("Talk to Betty about The Hand in the Sand."),
    },
    postconditions = { Condition.ConversationText:new("her counter") },
  },
  {
    text = "Open the door to the shop.",
    actions = { Action.ModelHighlight:new(closedDoor) },
    postconditions = { Condition.ModelVisible:new(openDoor) },
  },
  {
    text = "Stand on the marked tile and use your rose-tinted lens on the counter.",
    actions = {
      Action.Direction:new(3016, 965, 3259, { tile = true }),
      Action.ModelHighlight:new(counter),
      Action.InventoryHighlight:new(roseTintedLens),
    },
    postconditions = { Condition.InventoryContains:new(truthSerum) },
  },
  {
    text = "Talk to Betty yet again.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["betty"]),
      Action.ConversationHighlight:new("Talk to Betty about The Hand in the Sand."),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(sand) },
  },
  {
    text = "Return to Sandy. Try each chat option until he's distracted.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Karamja lodestone",
      url = "Karamja_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2788, 1829, 3176) },
    postconditions = { Condition.ModelVisible:new(sandy) },
  },
  {
    actions = { Action.ModelHighlight:new(sandy) },
    jumpconditions = { Condition.ModelNotVisible:new(sandy) },
    jumpOffset = -1,
    postconditions = { Condition.ChatText:new("now is your chance") },
  },
  {
    text = "Use the truth serum on the coffee mug in Sandy's house.",
    actions = {
      Action.ModelHighlight:new(coffeeMug),
      Action.InventoryHighlight:new(truthSerum),
    },
    postconditions = { Condition.ConversationText:new("drink it") },
  },
  {
    text = "Activate the magical orb.",
    actions = { Action.InventoryHighlight:new(magicalOrb) },
    postconditions = { Condition.InventoryContains:new(MagicalOrbA) },
  },
  {
    text = "Talk to Sandy again.",
    actions = {
      Action.ModelHighlight:new(sandy),
      Action.ConversationHighlight:new("Why is Bert's rota different from the original?"),
    },
    postconditions = { Condition.ConversationText:new("I changed it") },
  },
  {
    actions = {
      Action.ModelHighlight:new(sandy),
      Action.ConversationHighlight:new("Why doesn't Bert remember the change in his hours?"),
    },
    postconditions = { Condition.ConversationText:new("paying him more") },
  },
  {
    actions = {
      Action.ModelHighlight:new(sandy),
      Action.ConversationHighlight:new("What happened to the wizard?"),
    },
    postconditions = { Condition.ConversationText:new("returned to the Wizard") },
  },
  {
    text = "Ring the Wizards' Guild bell.",
    title = "Retrieving the head",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Yanille lodestone",
      url = "Yanille_lodestone_icon.png",
    },
    neededItems = {
      ["Bucket of sand"] = { quantity = 1 },
      ["Earth runes"] = { quantity = 5 },
    },
    recommendedItems = { ["Wicked hood"] = { quantity = 1 } },
    actions = { Action.Direction:new(2598, 1765, 3085) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["zavistic rarve"]) },
  },
  {
    text = "Talk to the Zavistic Rarve.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["zavistic rarve"]),
      Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you."),
    },
    postconditions = { Condition.ConversationText:new("get you the") },
  },
  {
    text = "Talk to the Zavistic Rarve again.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["zavistic rarve"]),
      Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talking to Zavistic Rarve.",
    actions = { Action.ModelHighlight:new(Models.npcs["zavistic rarve"]) },
    postconditions = { Condition.ConversationText:new("Entrana sandpit") },
  },
  {
    text = "Talk to Mazion at the sand pit in south-west Entrana.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Law altar teleport",
      url = "Wicked_hood.png",
    },
    actions = { Action.Direction:new(2819, 941, 3340) },
    postconditions = { Condition.ModelVisible:new(mazion) },
  },
  {
    actions = { Action.ModelHighlight:new(mazion) },
    jumpconditions = { Condition.ModelNotVisible:new(mazion) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("you the head") },
  },
  {
    text = "Ring the Wizards' Guild bell.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Yanille lodestone",
      url = "Yanille_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2598, 1765, 3085) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["zavistic rarve"]) },
  },
  {
    text = "Talk to the Zavistic Rarve.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["zavistic rarve"]),
      Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Hand in the Sand",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1136851200,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Crafting", 49),
    Types.QuestReq.skill("Thieving", 17),
  },
  neededItems = {
    ["Beer"] = { quantity = 1, model = Models.items["beer"], duringQuest = true },
    ["Bucket of sand"] = { quantity = 1, model = Models.items["bucket of sand"], duringQuest = true },
    ["Lantern lens"] = { quantity = 1, Models.items["lantern lens"] },
    ["Earth runes"] = { quantity = 5, Models.items["earth rune"] },
    ["Empty vial"] = { quantity = 1, Models.items["vial"] },
    ["Redberries"] = { quantity = 1, Models.items["redberries"] },
    ["White berries"] = { quantity = 1, Models.items["white berries"] },
  },
  recommendedItems = {
    ["Wicked hood"] = { quantity = 1 },
  },
  combatNPCs = {},
})
