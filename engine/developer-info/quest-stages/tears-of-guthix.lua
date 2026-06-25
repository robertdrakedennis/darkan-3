local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Objects
local lumbridgeSwampTree = Model.new(5943, {
  [138] = Vertex.new(356, 690, -12, 77, 80, 74),
  [2844] = Vertex.new(785, 2264, -181, 102, 102, 78),
  [4994] = Vertex.new(835, 735, -539, 45, 50, 38),
  [5895] = Vertex.new(596, 735, -908, 45, 50, 38),
  [5942] = Vertex.new(596, 735, -908, 45, 50, 38),
})
local rock = Model.new(1272, {
  [130] = Vertex.new(224, 0, 189, 40, 40, 36),
  [476] = Vertex.new(197, 0, 204, 40, 40, 36),
  [665] = Vertex.new(159, 0, 236, 49, 49, 45),
  [951] = Vertex.new(230, 0, 230, 49, 49, 45),
  [953] = Vertex.new(230, 0, 230, 49, 49, 45),
})
--#endregion
--#region Items
local sapphire = Model.new(114, {
  [98] = Vertex.new(-44, 104, 64, 159, 159, 145, 0.6863),
  [99] = Vertex.new(-36, 104, 20, 159, 159, 145, 0.6863),
  [101] = Vertex.new(-12, 116, 12, 159, 159, 145, 0.6863),
  [110] = Vertex.new(-76, 84, 12, 159, 159, 145, 0.6863),
  [114] = Vertex.new(-44, 104, -48, 159, 159, 145, 0.6863),
})
local bullseyeLantern = Model.multi({
  Model.new(366, {
    [3] = Vertex.new(-44, 80, -48, 43, 39, 39),
    [7] = Vertex.new(-44, 128, -48, 43, 39, 39),
    [13] = Vertex.new(56, 80, -48, 43, 39, 39),
    [18] = Vertex.new(56, 128, -48, 43, 39, 39),
    [23] = Vertex.new(-20, 152, -48, 43, 39, 39),
    [33] = Vertex.new(28, 152, -48, 43, 39, 39),
    [71] = Vertex.new(60, 164, 60, 43, 39, 39),
    [146] = Vertex.new(-48, 156, 40, 55, 50, 50),
    [152] = Vertex.new(-48, 148, 48, 55, 50, 50),
    [156] = Vertex.new(-48, 140, 44, 55, 50, 50),
    [190] = Vertex.new(60, 40, 36, 55, 50, 50),
    [193] = Vertex.new(60, 32, 44, 55, 50, 50),
    [205] = Vertex.new(60, 156, 40, 55, 50, 50),
    [208] = Vertex.new(60, 148, 48, 55, 50, 50),
    [210] = Vertex.new(60, 156, 40, 55, 50, 50),
    [214] = Vertex.new(60, 140, 44, 55, 50, 50),
    [216] = Vertex.new(60, 148, 48, 55, 50, 50),
    [235] = Vertex.new(-44, 196, 24, 55, 50, 50),
    [283] = Vertex.new(4, 228, 24, 55, 50, 50),
    [345] = Vertex.new(56, 196, 24, 55, 50, 50),
  }),
  Model.new(66, {
    [1] = Vertex.new(16, 76, -80, 116, 123, 152, 0.4353),
    [2] = Vertex.new(28, 56, -48, 116, 123, 152, 0.4353),
    [3] = Vertex.new(-20, 56, -48, 116, 123, 152, 0.4353),
    [6] = Vertex.new(-4, 76, -80, 116, 123, 152, 0.4353),
    [9] = Vertex.new(-44, 80, -48, 116, 123, 152, 0.4353),
    [10] = Vertex.new(32, 92, -80, 116, 123, 152, 0.4353),
    [17] = Vertex.new(56, 80, -48, 116, 123, 152, 0.4353),
    [21] = Vertex.new(-20, 92, -80, 116, 123, 152, 0.4353),
    [27] = Vertex.new(-44, 128, -48, 116, 123, 152, 0.4353),
    [28] = Vertex.new(32, 112, -80, 116, 123, 152, 0.4353),
  }),
})
local litBullseyeLantern = Model.multi({
  Model.new(384, {
    [8] = Vertex.new(20, 136, 0, 94, 87, 86),
    [9] = Vertex.new(44, 116, 0, 94, 87, 86),
    [14] = Vertex.new(-32, 116, 0, 94, 87, 86),
    [23] = Vertex.new(20, 64, 0, 94, 87, 86),
    [82] = Vertex.new(56, 80, -48, 43, 39, 39),
    [90] = Vertex.new(56, 128, -48, 43, 39, 39),
    [93] = Vertex.new(-44, 80, -48, 43, 39, 39),
    [97] = Vertex.new(-44, 128, -48, 43, 39, 39),
    [102] = Vertex.new(28, 152, -48, 43, 39, 39),
    [145] = Vertex.new(-44, 196, 24, 55, 50, 50),
    [173] = Vertex.new(56, 196, 24, 55, 50, 50),
    [241] = Vertex.new(4, 228, 24, 55, 50, 50),
    [296] = Vertex.new(-48, 156, 40, 55, 50, 50),
    [302] = Vertex.new(-48, 148, 48, 55, 50, 50),
    [306] = Vertex.new(-48, 140, 44, 55, 50, 50),
    [355] = Vertex.new(60, 156, 40, 55, 50, 50),
    [358] = Vertex.new(60, 148, 48, 55, 50, 50),
    [360] = Vertex.new(60, 156, 40, 55, 50, 50),
    [364] = Vertex.new(60, 140, 44, 55, 50, 50),
    [366] = Vertex.new(60, 148, 48, 55, 50, 50),
  }),
  Model.new(204, {
    [73] = Vertex.new(32, 112, -80, 182, 143, 15, 0.4980),
    [74] = Vertex.new(56, 128, -48, 182, 143, 15, 0.4980),
    [75] = Vertex.new(56, 80, -48, 182, 143, 15, 0.4980),
    [76] = Vertex.new(16, 128, -80, 182, 143, 15, 0.4980),
    [80] = Vertex.new(28, 152, -48, 182, 143, 15, 0.4980),
    [84] = Vertex.new(32, 92, -80, 182, 143, 15, 0.4980),
    [90] = Vertex.new(28, 56, -48, 182, 143, 15, 0.4980),
    [91] = Vertex.new(-4, 128, -80, 182, 143, 15, 0.4980),
    [98] = Vertex.new(-20, 152, -48, 182, 143, 15, 0.4980),
    [102] = Vertex.new(16, 76, -80, 182, 143, 15, 0.4980),
    [105] = Vertex.new(-20, 56, -48, 182, 143, 15, 0.4980),
    [106] = Vertex.new(-20, 112, -80, 182, 143, 15, 0.4980),
    [113] = Vertex.new(-44, 128, -48, 182, 143, 15, 0.4980),
    [117] = Vertex.new(-4, 76, -80, 182, 143, 15, 0.4980),
    [123] = Vertex.new(-44, 80, -48, 182, 143, 15, 0.4980),
    [125] = Vertex.new(-20, 92, -80, 182, 143, 15, 0.4980),
    [139] = Vertex.new(48, 112, -116, 182, 143, 15, 0.2471),
    [140] = Vertex.new(80, 132, -68, 182, 143, 15, 0.2471),
    [141] = Vertex.new(80, 64, -68, 182, 143, 15, 0.2471),
    [142] = Vertex.new(20, 136, -116, 182, 143, 15, 0.2471),
  }),
})
--#endregion
--#region Quest Items
local magicStone = Model.new(264, {
  [149] = Vertex.new(0, 104, -8, 86, 86, 94, 0.7490),
  [152] = Vertex.new(0, 104, 8, 86, 86, 94, 0.7490),
  [182] = Vertex.new(-8, 104, 0, 86, 86, 94, 0.7490),
  [186] = Vertex.new(-8, 104, 0, 86, 86, 94, 0.7490),
  [191] = Vertex.new(-8, 104, 0, 86, 86, 94, 0.7490),
})
local stoneBowl = Model.new(210, {
  [24] = Vertex.new(-28, 72, -76, 73, 73, 67),
  [121] = Vertex.new(-72, 72, -32, 73, 73, 67),
  [127] = Vertex.new(-72, 72, -32, 73, 73, 67),
  [133] = Vertex.new(-28, 72, -76, 73, 73, 67),
  [135] = Vertex.new(-72, 72, -32, 73, 73, 67),
})
--#endregion

local steps = {
  {
    text = "Climb down the dark hole under the tree in the Lumbridge swamp.<ul><li>If this is your first visit, Use the rope on the tree.</li><li>Dismiss all followers and un-equip items held in hand.</li></ul>",
    title = "Getting started",
    neededItems = {
      ["Bullseye lantern"] = { quantity = 1 },
      ["Cut sapphire"] = { quantity = 1 },
      ["Rope"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3164, 949, 3167.5, { distance = 24 }),
      Action.ModelHighlight:new(lumbridgeSwampTree, { distance = 24 }),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.DistanceTo:new(3169, 1029, 9571, 4) },
  },
  {
    text = "Enter the tunnel at the end of the path.",
    actions = { Action.Direction:new(3226, 1557, 9542) },
    postconditions = { Condition.DistanceTo:new(3219, 2733, 9532, 4) },
  },
  {
    text = "Talk to Juna, inside the cave under Lumbridge Swamp.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["juna"]),
      Action.ConversationHighlight:new("Okay..."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Use a cut sapphire on an oil filled bullseye lantern.",
    actions = {
      Action.InventoryHighlight:new(sapphire),
      Action.InventoryHighlight:new(bullseyeLantern),
      Action.InventoryHighlight:new(litBullseyeLantern),
    },
    postconditions = {
      Condition.InventoryContains:new(Models.items["sapphire lantern"]),
      Condition.InventoryContains:new(Models.items["lit sapphire lantern"]),
    },
  },
  {
    text = "Travel across cavern on a light creature.",
    title = "The Bowl of Stone",
    actions = { Action.InventoryHighlight:new(Models.items["lit sapphire lantern"]) },
    postconditions = { Condition.DistanceTo:new(3226, 2685, 9502, 5) },
  },
  {
    text = "Mine one of the rocks.",
    actions = { Action.ModelHighlight:new(rock) },
    postconditions = { Condition.InventoryContains:new(magicStone) },
  },
  {
    text = "Craft the magic stone into a Stone bowl.",
    actions = { Action.InventoryHighlight:new(magicStone) },
    postconditions = { Condition.InventoryContains:new(stoneBowl) },
  },
  {
    text = "Jump the cliff to the east.",
    actions = { Action.Direction:new(3238, 2693, 9499) },
    postconditions = { Condition.DistanceTo:new(3243, 1821, 9498, 2) },
  },
  {
    text = "Talk to Juna.",
    actions = { Action.ModelHighlight:new(Models.npcs["juna"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Tears of Guthix",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.short,
  releaseDate = 1115186400,
  prereqQuests = {},
  neededItems = {
    ["Bullseye lantern"] = { quantity = 1, model = bullseyeLantern },
    ["Cut sapphire"] = { quantity = 1, model = Models.items["sapphire"] },
    ["Rope"] = { quantity = 1, model = Models.items["rope"] },
  },
  questReqs = {
    Types.QuestReq.skill("Crafting", 20),
    Types.QuestReq.skill("Firemaking", 49),
    Types.QuestReq.skill("Mining", 20),
    Types.QuestReq.questpoints(44),
    Types.QuestReq.ironmanOnlySkill("Smithing", 20),
    Types.QuestReq.ironmanOnlySkill("Crafting", 49),
    Types.QuestReq.misc("If you have 50 quest points, you can get a bullseye lantern from the lorehound pet."),
  },
  recommendedItems = {},
  combatNPCs = {},
})
