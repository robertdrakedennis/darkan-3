local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
-- Can't track Brother Omad since his model is shared with every nearby monk
-- Cedric does too, but he's far enough away where it doesn't matter
local brotherCedric = Model.new(3069, {
  [1702] = Vertex.new(-2, 725, -59, 106, 77, 54),
  [1707] = Vertex.new(-7, 724, -51, 106, 77, 54),
  [1710] = Vertex.new(2, 725, -59, 106, 77, 54),
  [1712] = Vertex.new(7, 724, -51, 106, 77, 54),
  [2161] = Vertex.new(0, 735, -7, 27, 138, 126),
})

-- Quest Items
local childsBlanketGround = Model.new(96, {
  [3] = Vertex.new(48, 0, -84, 141, 131, 107),
  [47] = Vertex.new(-32, 0, 168, 141, 131, 107),
  [49] = Vertex.new(-32, -4, 168, 142, 127, 89),
  [51] = Vertex.new(120, -4, 132, 142, 127, 89),
  [96] = Vertex.new(48, -4, -84, 142, 127, 89),
})
local childsBlanketInventory1 = Model.new(96, {
  [1] = Vertex.new(-112, 0, -84, 141, 131, 107),
  [2] = Vertex.new(88, 20, -40, 141, 131, 107),
  [3] = Vertex.new(48, 0, -84, 141, 131, 107),
  [5] = Vertex.new(-104, 0, -60, 141, 131, 107),
  [8] = Vertex.new(96, 20, 0, 141, 131, 107),
})
local childsBlanketInventory2 = Model.new(96, {
  [1] = Vertex.new(-112, 0, -84, 141, 132, 108),
  [2] = Vertex.new(88, 20, -40, 141, 132, 108),
  [3] = Vertex.new(48, 0, -84, 141, 132, 108),
  [5] = Vertex.new(-104, 0, -60, 141, 132, 108),
  [8] = Vertex.new(96, 20, 0, 141, 132, 108),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Brother Omad on the ground floor (1st floor[US]) of the Monastery south of East Ardougne zoo.",
    title = "Getting started",
    actions = { Action.Direction:new(2606, 2405, 3211) },
    postconditions = { Condition.DistanceTo:new(2606, 2405, 3211, 8) },
  },
  {
    actions = {
      Action.ConversationHighlight:new("Why can't you sleep?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Brother Omad.",
    actions = {
      Action.ConversationHighlight:new("Why can't you sleep?"),
    },
    postconditions = {
      Condition.ConversationText:new("It's hidden under a ring of stones."),
      Condition.ConversationText:new("Please bring back the blanket!"),
    },
  },
  {
    text = "Stand in a nearby circle of stones and climb down the ladder that appears.",
    title = "The secret tunnel",
    actions = { Action.Direction:new(2561, 1061, 3222) },
    postconditions = { Condition.DistanceTo:new(2561, 645, 9621, 4) },
  },
  {
    text = "Go south, enter the room with thieves, and grab the Child's blanket on the table to the east.",
    actions = { Action.ModelHighlight:new(childsBlanketGround) },
    postconditions = {
      Condition.InventoryContains:new(childsBlanketInventory1),
      Condition.InventoryContains:new(childsBlanketInventory2),
      Condition.InventoryContains:new(childsBlanketGround),
    },
  },
  {
    text = "Return to the monastery and talk to Brother Omad.",
    actions = { Action.Direction:new(2606, 2405, 3211) },
    postconditions = { Condition.DistanceTo:new(2606, 2405, 3211, 8) },
  },
  {
    actions = {
      Action.ConversationHighlight:new("Is there anything else I can help with?"),
      Action.ConversationHighlight:new("Who's Brother Cedric?"),
      Action.ConversationHighlight:new("Where should I look?"),
    },
    postconditions = {
      Condition.ConversationText:new("Ok, I'll go and find him."),
    },
  },
  {
    text = "Talk to Brother Cedric.",
    title = "The party",
    neededItems = { ["Jug of water"] = { quantity = 1 }, ["Logs"] = { quantity = 1 } },
    actions = { Action.Direction:new(2618, 1029, 3248) },
    postconditions = {
      Condition.DistanceTo:new(2618, 1029, 3248, 8),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(brotherCedric),
      Action.ConversationHighlight:new("Yes, I'd be happy to!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Pleashhh, find me a jug of water. Once I'm sober I'll 'elp you take the wine back."
      ),
    },
  },
  {
    text = "Talk to him again to give him a jug of water.",
    actions = {
      Action.ModelHighlight:new(brotherCedric),
      Action.ConversationHighlight:new("Yes, I'd be happy to!"),
    },
    postconditions = { Condition.ConversationText:new("OK, I'll see what I can find.") },
  },
  {
    text = "Talk to him once more to give him a set of logs.",
    actions = {
      Action.ModelHighlight:new(brotherCedric),
      Action.ConversationHighlight:new("Yes, I'd be happy to!"),
    },
    postconditions = { Condition.ConversationText:new("Ok! I'll see you later!") },
  },
  {
    text = "Talk to Brother Omad.<ul><li>Pop a balloon for area task.</li></ul>",
    actions = { Action.Direction:new(2606, 2405, 3211) },
    postconditions = { Condition.DistanceTo:new(2606, 2405, 3211, 8) },
  },
  { actions = {}, postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Monk's Friend",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.short,
  releaseDate = 1022544000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Jug of water"] = { quantity = 1, model = Items["jug of water"] },
    ["Normal logs"] = { quantity = 1, model = Items["logs"] },
  },
  recommendedItems = {},
  combatNPCs = {},
})
