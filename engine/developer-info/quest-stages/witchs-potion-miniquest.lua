local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local hetty = Model.new(3579, {
  [2544] = Vertex.new(0, 760, 64, 74, 67, 38),
  [3365] = Vertex.new(36, 884, -12, 76, 57, 90),
  [3368] = Vertex.new(36, 884, -12, 76, 57, 90),
  [3369] = Vertex.new(16, 864, -12, 76, 57, 90),
  [3413] = Vertex.new(36, 884, -12, 76, 57, 90),
})

-- Objects
local cauldron = Model.new(570, {
  [91] = Vertex.new(88, 316, -92, 6, 68, 8),
  [94] = Vertex.new(88, 316, -92, 6, 68, 8),
  [95] = Vertex.new(84, 316, -88, 6, 68, 8),
  [97] = Vertex.new(56, 316, -124, 6, 68, 8),
  [99] = Vertex.new(88, 316, -92, 6, 68, 8),
})

-- Items
local burntMeat = Model.new(126, {
  [1] = Vertex.new(-4, 24, 64, 57, 52, 52),
  [2] = Vertex.new(-4, 24, 8, 57, 52, 52),
  [3] = Vertex.new(-16, 24, 12, 57, 52, 52),
  [6] = Vertex.new(-36, 24, -44, 57, 52, 52),
  [10] = Vertex.new(8, 24, 72, 57, 52, 52),
})

-- Quest Items
local ratsTail = Model.new(204, {
  [45] = Vertex.new(-72, 20, -24, 141, 105, 58),
  [79] = Vertex.new(-60, 20, 48, 141, 105, 58),
  [180] = Vertex.new(20, 20, -64, 141, 105, 58),
  [182] = Vertex.new(20, 20, -64, 141, 105, 58),
  [197] = Vertex.new(20, 20, -64, 141, 105, 58),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Hetty in her house in Rimmington, west of the Port Sarim lodestone and south of Falador.",
    title = "Hell's Kitchen",
    actions = { Action.Direction:new(2967, 805, 3206) },
    postconditions = { Condition.DistanceTo:new(2967, 805, 3206, 2) },
    neededItems = {
      ["Burnt meat (use cooked meat on a fire)"] = { quantity = 1 },
      ["Eye of newt"] = { quantity = 1 },
      ["Onion"] = { quantity = 1 },
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(hetty),
      Action.ConversationHighlight:new("I'm looking for work."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with Hetty.",
    actions = { Action.ModelHighlight:new(hetty) },
    postconditions = { Condition.ConversationText:new("Oh, and a piece of burnt meat.") },
  },
  {
    text = "Kill a rat for a rat's tail.<ul><li>Some rats can be found in Brian's Archery Supplies.</li></ul>",
    actions = { Action.Direction:new(2957, 805, 3192) },
    postconditions = { Condition.DistanceTo:new(2957, 805, 3192, 2) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["rat"]) },
    postconditions = { Condition.ModelVisible:new(ratsTail) },
  },
  {
    actions = { Action.ModelHighlight:new(ratsTail) },
    postconditions = { Condition.InventoryContains:new(ratsTail) },
  },
  {
    text = "Give Hetty the ingredients.",
    actions = { Action.Direction:new(2967, 805, 3206) },
    postconditions = { Condition.DistanceTo:new(2967, 805, 3206, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(hetty),
    },
    postconditions = { Condition.ConversationText:new("Okay, now drink from the cauldron.") },
  },
  {
    text = "Drink from the cauldron.",
    actions = { Action.ModelHighlight:new(cauldron) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Witch's Potion (miniquest)",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.veryshort,
  releaseDate = 986515200,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Burnt meat (use cooked meat on a fire)"] = { quantity = 1, model = burntMeat },
    ["Eye of newt"] = { quantity = 1, model = Items["eye of newt"] },
    ["Onion"] = { quantity = 1, model = Items["onion"] },
  },
  recommendedItems = {},
  combatNPCs = { ["Rat"] = { level = "1", quantity = 1 } },
})
