local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local razmireAfflicted = Model.new(3861, {
  [2395] = Vertex.new(-2, 725, -59, 103, 113, 72),
  [2400] = Vertex.new(-7, 724, -51, 103, 113, 72),
  [2403] = Vertex.new(2, 725, -59, 103, 113, 72),
  [2405] = Vertex.new(7, 724, -51, 103, 113, 72),
  [3421] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local razmireCured = Model.new(3861, {
  [2395] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2400] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [2403] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2405] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3421] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local loreShadePuddle = Model.new(360, {
  [273] = Vertex.new(-160, 0, 224, 4, 42, 5, 0.4980),
  [275] = Vertex.new(32, 0, 288, 4, 42, 5, 0.4980),
  [280] = Vertex.new(160, 0, 224, 4, 42, 5, 0.4980),
  [282] = Vertex.new(32, 0, 288, 4, 42, 5, 0.4980),
  [345] = Vertex.new(-32, 0, -288, 4, 42, 5, 0.4980),
})
local loreShade = Model.new(1503, {
  [772] = Vertex.new(108, 616, 52, 4, 42, 5),
  [1152] = Vertex.new(64, 100, 116, 4, 42, 5),
  [1170] = Vertex.new(-52, 80, 132, 4, 42, 5),
  [1176] = Vertex.new(-76, 88, 108, 4, 42, 5),
  [1194] = Vertex.new(-104, 100, 64, 4, 42, 5),
})
local ulsquireAfflicted = Model.new(3219, {
  [2038] = Vertex.new(-2, 725, -59, 103, 113, 72),
  [2043] = Vertex.new(-7, 724, -51, 103, 113, 72),
  [2046] = Vertex.new(2, 725, -59, 103, 113, 72),
  [2048] = Vertex.new(7, 724, -51, 103, 113, 72),
  [2305] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local ulsquireCured = Model.new(3219, {
  [2038] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2043] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [2046] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2048] = Vertex.new(7, 724, -51, 109, 80, 57),
  [2305] = Vertex.new(0, 735, -7, 29, 141, 129),
})
--#endregion
--#region Objects
local builtAltar = Model.new(396, {
  [320] = Vertex.new(-32, 608, 64, 51, 47, 47),
  [326] = Vertex.new(32, 608, 64, 51, 47, 47),
  [329] = Vertex.new(32, 544, 64, 51, 47, 47),
  [332] = Vertex.new(-64, 608, -32, 51, 47, 47),
  [356] = Vertex.new(64, 608, 32, 51, 47, 47),
})
local litAltar = Model.new(870, {
  [139] = Vertex.new(300, -640, 20, 130, 74, 12),
  [371] = Vertex.new(320, -640, 0, 130, 74, 12),
  [385] = Vertex.new(320, -640, 0, 130, 74, 12),
  [521] = Vertex.new(284, -640, 0, 130, 74, 12),
  [800] = Vertex.new(32, 608, 64, 51, 47, 47),
})
local funeralPyre = Model.new(1350, {
  [813] = Vertex.new(-132, 264, 160, 32, 29, 29),
  [837] = Vertex.new(76, 264, -244, 32, 29, 29),
  [872] = Vertex.new(176, 264, 116, 32, 29, 29),
  [1062] = Vertex.new(-144, 264, 132, 32, 29, 29),
  [1100] = Vertex.new(76, 264, 256, 32, 29, 29),
})
--#endregion
--#region Items
local tarrominPotionUnf = Model.multi({
  Model.new(90, {
    [3] = Vertex.new(-12, 52, 20, 28, 135, 44),
    [6] = Vertex.new(-20, 52, -12, 28, 135, 44),
    [8] = Vertex.new(-12, 52, 20, 28, 135, 44),
    [21] = Vertex.new(20, 52, -16, 28, 135, 44),
    [23] = Vertex.new(20, 52, -16, 28, 135, 44),
  }),
  Model.new(288, {
    [242] = Vertex.new(4, 96, 12, 136, 137, 148, 0.4980),
    [243] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
    [246] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
    [264] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [269] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
  }),
})
local serum207 = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 8, 80, 96),
    [2] = Vertex.new(-20, 52, 8, 8, 80, 96),
    [3] = Vertex.new(-12, 52, 20, 8, 80, 96),
    [6] = Vertex.new(-20, 52, -12, 8, 80, 96),
    [9] = Vertex.new(12, 52, 20, 8, 80, 96),
    [11] = Vertex.new(-12, 52, -20, 8, 80, 96),
    [15] = Vertex.new(20, 52, 8, 8, 80, 96),
    [25] = Vertex.new(-12, 96, 4, 115, 103, 48),
    [26] = Vertex.new(-12, 112, 4, 115, 103, 48),
    [27] = Vertex.new(-12, 112, -4, 115, 103, 48),
    [30] = Vertex.new(-12, 96, -4, 115, 103, 48),
    [33] = Vertex.new(-4, 112, -12, 115, 103, 48),
    [36] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [39] = Vertex.new(4, 112, -12, 115, 103, 48),
  }),
  Model.new(288, {
    [1] = Vertex.new(-20, 84, 4, 136, 137, 148, 0.4980),
    [2] = Vertex.new(-20, 96, -4, 136, 137, 148, 0.4980),
    [3] = Vertex.new(-20, 84, -4, 136, 137, 148, 0.4980),
    [5] = Vertex.new(-20, 96, 4, 136, 137, 148, 0.4980),
    [9] = Vertex.new(-4, 96, -20, 136, 137, 148, 0.4980),
    [10] = Vertex.new(-4, 84, 20, 136, 137, 148, 0.4980),
    [15] = Vertex.new(-4, 84, -20, 136, 137, 148, 0.4980),
    [17] = Vertex.new(-4, 96, 20, 136, 137, 148, 0.4980),
    [21] = Vertex.new(4, 96, -20, 136, 137, 148, 0.4980),
    [22] = Vertex.new(4, 84, 20, 136, 137, 148, 0.4980),
    [49] = Vertex.new(4, 0, -36, 8, 80, 96, 0.8745),
    [50] = Vertex.new(40, 12, -12, 8, 80, 96, 0.8745),
    [51] = Vertex.new(32, 0, -4, 8, 80, 96, 0.8745),
    [53] = Vertex.new(12, 12, -40, 8, 80, 96, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 8, 80, 96, 0.8745),
    [60] = Vertex.new(40, 12, 12, 8, 80, 96, 0.8745),
    [63] = Vertex.new(32, 0, 4, 8, 80, 96, 0.8745),
    [65] = Vertex.new(20, 52, -16, 8, 80, 96, 0.8745),
    [69] = Vertex.new(20, 52, 8, 8, 80, 96, 0.8745),
    [74] = Vertex.new(12, 52, -20, 8, 80, 96, 0.8745),
  }),
})
local loreRemains = Model.new(408, {
  [39] = Vertex.new(212, 0, -120, 5, 59, 7),
  [302] = Vertex.new(-140, 0, 236, 5, 59, 7),
  [374] = Vertex.new(-152, 0, 172, 5, 59, 7),
  [377] = Vertex.new(-164, 0, 180, 5, 59, 7),
  [408] = Vertex.new(-172, 0, 160, 5, 59, 7),
})
local oliveOil = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 126, 155, 14),
    [2] = Vertex.new(-20, 52, 8, 126, 155, 14),
    [3] = Vertex.new(-12, 52, 20, 126, 155, 14),
    [6] = Vertex.new(-20, 52, -12, 126, 155, 14),
    [9] = Vertex.new(12, 52, 20, 126, 155, 14),
    [11] = Vertex.new(-12, 52, -20, 126, 155, 14),
    [15] = Vertex.new(20, 52, 8, 126, 155, 14),
    [25] = Vertex.new(-12, 96, 4, 115, 103, 48),
    [26] = Vertex.new(-12, 112, 4, 115, 103, 48),
    [27] = Vertex.new(-12, 112, -4, 115, 103, 48),
    [30] = Vertex.new(-12, 96, -4, 115, 103, 48),
    [33] = Vertex.new(-4, 112, -12, 115, 103, 48),
    [36] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [39] = Vertex.new(4, 112, -12, 115, 103, 48),
  }),
  Model.new(288, {
    [1] = Vertex.new(-20, 84, 4, 136, 137, 148, 0.4980),
    [2] = Vertex.new(-20, 96, -4, 136, 137, 148, 0.4980),
    [3] = Vertex.new(-20, 84, -4, 136, 137, 148, 0.4980),
    [5] = Vertex.new(-20, 96, 4, 136, 137, 148, 0.4980),
    [9] = Vertex.new(-4, 96, -20, 136, 137, 148, 0.4980),
    [10] = Vertex.new(-4, 84, 20, 136, 137, 148, 0.4980),
    [15] = Vertex.new(-4, 84, -20, 136, 137, 148, 0.4980),
    [17] = Vertex.new(-4, 96, 20, 136, 137, 148, 0.4980),
    [21] = Vertex.new(4, 96, -20, 136, 137, 148, 0.4980),
    [22] = Vertex.new(4, 84, 20, 136, 137, 148, 0.4980),
    [49] = Vertex.new(4, 0, -36, 126, 155, 14, 0.8745),
    [50] = Vertex.new(40, 12, -12, 126, 155, 14, 0.8745),
    [51] = Vertex.new(32, 0, -4, 126, 155, 14, 0.8745),
    [53] = Vertex.new(12, 12, -40, 126, 155, 14, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 126, 155, 14, 0.8745),
    [60] = Vertex.new(40, 12, 12, 126, 155, 14, 0.8745),
    [63] = Vertex.new(32, 0, 4, 126, 155, 14, 0.8745),
    [65] = Vertex.new(20, 52, -16, 126, 155, 14, 0.8745),
    [69] = Vertex.new(20, 52, 8, 126, 155, 14, 0.8745),
    [74] = Vertex.new(12, 52, -20, 126, 155, 14, 0.8745),
  }),
})
local timberBeam = Model.new(48, {
  [5] = Vertex.new(252, 0, 164, 97, 84, 62),
  [7] = Vertex.new(-164, 0, -256, 97, 84, 62),
  [10] = Vertex.new(-164, 0, -256, 97, 84, 62),
  [11] = Vertex.new(-256, 0, -164, 97, 84, 62),
  [48] = Vertex.new(252, 0, 164, 75, 58, 23),
})
local limestoneBrick = Model.new(66, {
  [1] = Vertex.new(240, 0, -116, 91, 90, 83),
  [4] = Vertex.new(240, 0, -116, 91, 90, 83),
  [9] = Vertex.new(236, 0, 48, 91, 90, 83),
  [15] = Vertex.new(240, 0, -116, 91, 90, 83),
  [55] = Vertex.new(236, 0, 48, 91, 90, 83),
})
local swampPaste = Model.new(303, {
  [17] = Vertex.new(48, 0, -96, 32, 29, 29),
  [26] = Vertex.new(48, 0, -96, 32, 29, 29),
  [30] = Vertex.new(48, 0, -96, 32, 29, 29),
  [124] = Vertex.new(52, 0, -88, 32, 29, 29),
  [136] = Vertex.new(52, 0, -88, 32, 29, 29),
})
local sacredOil = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 77, 104, 9),
    [2] = Vertex.new(-20, 52, 8, 77, 104, 9),
    [3] = Vertex.new(-12, 52, 20, 77, 104, 9),
    [6] = Vertex.new(-20, 52, -12, 77, 104, 9),
    [9] = Vertex.new(12, 52, 20, 77, 104, 9),
    [11] = Vertex.new(-12, 52, -20, 77, 104, 9),
    [15] = Vertex.new(20, 52, 8, 77, 104, 9),
    [25] = Vertex.new(-12, 96, 4, 115, 103, 48),
    [26] = Vertex.new(-12, 112, 4, 115, 103, 48),
    [27] = Vertex.new(-12, 112, -4, 115, 103, 48),
    [30] = Vertex.new(-12, 96, -4, 115, 103, 48),
    [33] = Vertex.new(-4, 112, -12, 115, 103, 48),
    [36] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [39] = Vertex.new(4, 112, -12, 115, 103, 48),
  }),
  Model.new(288, {
    [1] = Vertex.new(-20, 84, 4, 136, 137, 148, 0.4980),
    [2] = Vertex.new(-20, 96, -4, 136, 137, 148, 0.4980),
    [3] = Vertex.new(-20, 84, -4, 136, 137, 148, 0.4980),
    [5] = Vertex.new(-20, 96, 4, 136, 137, 148, 0.4980),
    [9] = Vertex.new(-4, 96, -20, 136, 137, 148, 0.4980),
    [10] = Vertex.new(-4, 84, 20, 136, 137, 148, 0.4980),
    [15] = Vertex.new(-4, 84, -20, 136, 137, 148, 0.4980),
    [17] = Vertex.new(-4, 96, 20, 136, 137, 148, 0.4980),
    [21] = Vertex.new(4, 96, -20, 136, 137, 148, 0.4980),
    [22] = Vertex.new(4, 84, 20, 136, 137, 148, 0.4980),
    [49] = Vertex.new(4, 0, -36, 77, 104, 9, 0.8745),
    [50] = Vertex.new(40, 12, -12, 77, 104, 9, 0.8745),
    [51] = Vertex.new(32, 0, -4, 77, 104, 9, 0.8745),
    [53] = Vertex.new(12, 12, -40, 77, 104, 9, 0.8745),
    [55] = Vertex.new(-4, 0, -36, 77, 104, 9, 0.8745),
    [60] = Vertex.new(40, 12, 12, 77, 104, 9, 0.8745),
    [63] = Vertex.new(32, 0, 4, 77, 104, 9, 0.8745),
    [65] = Vertex.new(20, 52, -16, 77, 104, 9, 0.8745),
    [69] = Vertex.new(20, 52, 8, 77, 104, 9, 0.8745),
    [74] = Vertex.new(12, 52, -20, 77, 104, 9, 0.8745),
  }),
})
local pyreLogs = Model.new(564, {
  [1] = Vertex.new(-116, 0, 12, 66, 44, 13),
  [2] = Vertex.new(68, 28, -8, 66, 44, 13),
  [3] = Vertex.new(100, 0, 12, 66, 44, 13),
  [307] = Vertex.new(100, 28, -12, 66, 44, 13),
  [311] = Vertex.new(100, 76, 4, 66, 44, 13),
  [313] = Vertex.new(88, 76, 12, 66, 44, 13),
  [314] = Vertex.new(-48, 112, 28, 66, 44, 13),
  [315] = Vertex.new(-104, 76, 12, 66, 44, 13),
  [316] = Vertex.new(-104, 76, -28, 66, 44, 13),
  [320] = Vertex.new(88, 76, -28, 66, 44, 13),
  [323] = Vertex.new(40, 120, -52, 66, 44, 13),
  [325] = Vertex.new(88, 124, -52, 66, 44, 13),
  [329] = Vertex.new(88, 168, -24, 66, 44, 13),
  [333] = Vertex.new(-48, 120, -52, 66, 44, 13),
  [336] = Vertex.new(-104, 124, -52, 66, 44, 13),
  [339] = Vertex.new(28, 156, -24, 66, 44, 13),
  [344] = Vertex.new(-36, 156, -24, 66, 44, 13),
  [351] = Vertex.new(-104, 168, -24, 66, 44, 13),
  [352] = Vertex.new(-104, 160, 16, 66, 44, 13),
  [355] = Vertex.new(28, 148, 12, 66, 44, 13),
})
--#endregion
--#region Quest Items
local diary = Model.new(204, {
  [87] = Vertex.new(36, 36, -84, 48, 38, 4),
  [93] = Vertex.new(64, 36, 56, 48, 38, 4),
  [159] = Vertex.new(36, 36, -84, 48, 38, 4),
  [201] = Vertex.new(-32, 52, 32, 83, 68, 26),
  [203] = Vertex.new(32, 52, 52, 83, 68, 26),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Search the shelf on the wall south-western ruined house in Mort'ton.<ul><li></li></ul>",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(3480.65, 1501, 3279) },
    postconditions = { Condition.InventoryContains:new(diary) },
  },
  {
    text = "Read the diary.<ul><li>If you didn't find a diary, check your bank.</li></ul>",
    actions = { Action.InventoryHighlight:new(diary) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Pick up the vials (on the ground near the shelf).",
    actions = { Action.ModelHighlight:new(Models.items["vial"], { highlightPriority = "all" }) },
    postconditions = {
      Condition.InventoryContains:new(Models.items["vial"], 2),
      Condition.InventoryContains:new(Models.items["vial of water"], 2),
    },
  },
  {
    text = "Make vials of water by using the empty vials on the sink nearby.<ul><li>(Optional) Search the smashed table for extra tarromin and one grimy rogue's purse.</li></ul>",
    actions = { Action.Direction:new(3495.5, 901, 3288) },
    postconditions = { Condition.InventoryContains:new(Models.items["vial of water"], 2) },
  },
  {
    text = "Make 2 tarromin potions (unfinished).",
    title = "Serum 207",
    actions = { Action.InventoryHighlight:new(Models.items["vial of water"]) },
    postconditions = { Condition.InventoryContains:new(tarrominPotionUnf) },
  },
  {
    text = "Mix the tarromin potion (unfinished) with ashes to make 2 Serum 207 (3)s.",
    actions = { Action.InventoryHighlight:new(tarrominPotionUnf) },
    postconditions = { Condition.InventoryContains:new(serum207, 2) },
  },
  {
    text = "<i>Use</i> the Serum 207 on Afflicted(Razmire) in the general store, then talk to him.",
    actions = {
      Action.Direction:new(3488, 901, 3296, { distance = 2 }),
      Action.ModelHighlight:new(razmireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(razmireCured, { distance = 2 }),
      Action.ConversationHighlight:new("What are all these shadowy creatures?"),
      Action.ConversationHighlight:new("Yes, I'll dispatch those dark and evil creatures."),
    },
    postconditions = { Condition.ConversationText:new("I want to inspect them") }, --not tested
  },
  {
    text = "Kill five Loar Shades and pick up their remains.<ul><li>Be sure to talk to Razmire first.</li><li>They can be safespotted using the rocks south west of the waypost in the middle of town.</li></ul>",
    actions = {
      Action.ModelHighlight:new(loreShadePuddle, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(loreShade, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(loreRemains),
    },
    postconditions = { Condition.InventoryContains:new(loreRemains, 5) },
  },
  {
    text = "Give him the remains. He may require more Serum 207.",
    actions = {
      Action.Direction:new(3488, 901, 3296, { distance = 2 }),
      Action.ModelHighlight:new(razmireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(razmireCured, { distance = 2 }),
    },
    postconditions = { Condition.ConversationText:new("I can trade with you now") }, --not tested
  },
  {
    text = "Use the serum on Ulsquire Shauncy, located in the house east of the general store.",
    actions = {
      Action.Direction:new(3496, 901, 3289, { distance = 1 }),
      Action.ModelHighlight:new(ulsquireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(ulsquireCured, { distance = 2 }),
    },
    postconditions = { Condition.ConversationText:new("He wanders off to study the bones") },
  },
  {
    text = "Wait a minute and ask him about the remains and the temple.",
    actions = {
      Action.Direction:new(3496, 901, 3289, { distance = 1 }),
      Action.ModelHighlight:new(ulsquireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(ulsquireCured, { distance = 2 }),
      Action.ConversationHighlight:new("What did you find out about the remains?"),
    },
    postconditions = { Condition.ConversationText:new("I'm not surprised that he never took") },
  },
  {
    actions = {
      Action.Direction:new(3496, 901, 3289, { distance = 1 }),
      Action.ModelHighlight:new(ulsquireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(ulsquireCured, { distance = 2 }),
      Action.ConversationHighlight:new("What can you tell me about that temple?"),
    },
    postconditions = { Condition.ConversationText:new("Thankfully Razmire stocks") },
  },
  {
    actions = {
      Action.Direction:new(3496, 901, 3289, { distance = 1 }),
      Action.ModelHighlight:new(ulsquireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(ulsquireCured, { distance = 2 }),
      Action.ConversationHighlight:new("Ok, thanks"),
    },
    postconditions = { Condition.ConversationText:new("Ok, thanks") },
  },
  {
    text = "Talk to Razmire.",
    actions = {
      Action.Direction:new(3488, 901, 3296, { distance = 2 }),
      Action.ModelHighlight:new(razmireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(razmireCured, { distance = 2 }),
      Action.ConversationHighlight:new("I have another question."),
      Action.ConversationHighlight:new("What can you tell me about that temple?"),
    },
    postconditions = { Condition.ConversationText:new("what it looked like back in pagan times") },
  },
  {
    text = 'Buy at least one olive oil from his general store.<ul><li>(Optional) Buy a Flamtaer hammer to build the temple 75% faster, and for the medium Morytania achievement, "Flamtaer Will Get You Everywhere".</li></ul>',
    actions = {
      Action.Direction:new(3488, 901, 3296, { distance = 2 }),
      Action.ModelHighlight:new(razmireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(razmireCured, { distance = 2 }),
      Action.ConversationHighlight:new("Can you open a store for me?"),
      Action.ConversationHighlight:new("Can I see the general store please?"),
    },
    postconditions = { Condition.InventoryContains:new(oliveOil) },
  },
  {
    text = "Open his builders' store. Buy 5 timber beams, 5 limestone bricks, 25 swamp paste.",
    actions = {
      Action.Direction:new(3488, 901, 3296, { distance = 2 }),
      Action.ModelHighlight:new(razmireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(razmireCured, { distance = 2 }),
    },
    postconditions = { Condition.InventoryContains:new(timberBeam, 5) },
  },
  {
    actions = {
      Action.Direction:new(3488, 901, 3296, { distance = 2 }),
      Action.ModelHighlight:new(razmireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(razmireCured, { distance = 2 }),
    },
    postconditions = { Condition.InventoryContains:new(limestoneBrick, 5) },
  },
  {
    actions = {
      Action.Direction:new(3488, 901, 3296, { distance = 2 }),
      Action.ModelHighlight:new(razmireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(razmireCured, { distance = 2 }),
    },
    postconditions = { Condition.InventoryContains:new(swampPaste, 25) },
  },
  {
    text = "Head north-east and kill shades or repair the temple to increase your sanctity.<ul><li>Hop to World 88 which is the official world for this minigame.</li></ul>",
    title = "Repairing the temple",
    actions = { Action.Direction:new(3506, 1285, 3316) },
    postconditions = { Condition.DistanceTo:new(3506, 1285, 3316, 8) },
  },
  {
    text = "Repair the temple until you reach 100% and at least 20 sanctity.<ul><li>Shade luring dummies can be used to distract the Loar Shades when repairing the temple.</li><li>Using the Flamtaer bracelet, the solo player can instantly create the temple walls section by section, until at 100%.</li></ul>",
    postconditions = { Condition.ModelVisible:new(builtAltar) },
  },
  {
    text = "Light the sacred altar in the centre of the temple.",
    actions = { Action.ModelHighlight:new(builtAltar) },
    postconditions = { Condition.ModelVisible:new(litAltar) },
  },
  {
    text = "Use your olive oil on the flame to make sacred oil.<ul><li>(Optional) Use Serum 207 on the flame to make Serum 208, which permanently cures NPCs and is required for an achievement.</li></ul>",
    actions = {
      Action.ModelHighlight:new(litAltar),
      Action.InventoryHighlight:new(oliveOil),
    },
    postconditions = { Condition.InventoryContains:new(sacredOil) },
  },
  {
    text = "Use the sacred oil on a log to create a pyre log.",
    title = "Burning a shade",
    actions = {
      Action.InventoryHighlight:new(Models.items["logs"]),
      Action.InventoryHighlight:new(sacredOil),
    },
    postconditions = { Condition.InventoryContains:new(pyreLogs) },
  },
  {
    text = "Click on any funeral pyre add the pyre logs. Click again to add the loar remains. Click again to sacrifice them.",
    actions = {
      Action.Direction:new(3507, 797, 3277, { distance = 14 }),
      Action.ModelHighlight:new(funeralPyre, { highlightPriority = "closest", distance = 14 }),
    },
    postconditions = {
      Condition.ChatText:new("A Bronze key"),
      Condition.ChatText:new("coins have been added to"), --Only works if Game Messages are set to 'On'
    },
  },
  {
    text = "Talk to Ulsquire.",
    actions = {
      Action.Direction:new(3496, 901, 3289, { distance = 1 }),
      Action.ModelHighlight:new(ulsquireAfflicted, { distance = 2 }),
      Action.ModelHighlight:new(ulsquireCured, { distance = 2 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Shades of Mort'ton",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1098057600,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Crafting", 20),
    Types.QuestReq.skill("Herblore", 15),
    Types.QuestReq.skill("Firemaking", 6, true),
    Types.QuestReq.misc("It's best to do this quest on world 88."),
  },
  neededItems = {
    ["Vial of water"] = { quantity = 2, model = Models.items["vial of water"], duringQuest = true },
    ["Ashes"] = { quantity = 2, model = Models.items["ashes"], duringQuest = true },
    ["Clean tarromin"] = { quantity = 2, maxQuantity = 7, model = Models.items["clean tarromin"], duringQuest = true },
    ["Normal logs"] = { quantity = 1, model = Models.items["logs"], duringQuest = true },
    ["Combat gear"] = { quantity = 1 },
    ["Coins"] = { quantity = 1000 },
  },
  recommendedItems = {
    ["Flamtaer bracelet"] = { quantity = 1 },
    ["Flamtaer hammer"] = { quantity = 1 },
  },
  combatNPCs = { ["Loar shades"] = { level = "58", quantity = 5 } },
})
