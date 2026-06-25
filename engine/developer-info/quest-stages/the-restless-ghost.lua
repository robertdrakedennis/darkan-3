local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--NPCs
local restlessGhost = Model.new(573, {
  [13] = Vertex.new(76, 643, 19, 75, 147, 125, 0.000),
  [14] = Vertex.new(81, 630, 19, 75, 147, 125, 0.000),
  [16] = Vertex.new(-76, 643, 19, 75, 147, 125, 0.000),
  [18] = Vertex.new(-81, 630, 19, 75, 147, 125, 0.000),
  [25] = Vertex.new(0, 676, 53, 75, 147, 125, 0.000),
})

--Objects
local coffin = Model.new(5841, {
  [74] = Vertex.new(242, 711, 21, 0, 0, 0),
  [77] = Vertex.new(272, 711, 39, 0, 0, 0),
  [80] = Vertex.new(299, 711, 39, 59, 60, 55),
  [83] = Vertex.new(328, 711, 23, 75, 76, 69),
  [86] = Vertex.new(328, 711, -16, 106, 108, 99),
})
local openCoffin = Model.new(5841, {
  [80] = Vertex.new(328, 125, 405, 59, 60, 55),
  [83] = Vertex.new(356, 143, 402, 75, 76, 69),
  [86] = Vertex.new(353, 181, 394, 106, 108, 99),
  [89] = Vertex.new(323, 195, 391, 65, 66, 60),
  [92] = Vertex.new(297, 193, 391, 65, 66, 60),
})
local rock = Model.new(3234, {
  [1215] = Vertex.new(346, -279, 264, 127, 127, 127),
  [1218] = Vertex.new(346, -279, 264, 127, 127, 127),
  [1227] = Vertex.new(375, -259, 231, 127, 127, 127),
  [1751] = Vertex.new(346, -279, 264, 127, 127, 127),
  [3225] = Vertex.new(361, 275, -242, 127, 127, 127),
})

--Quest Items
local skull = Model.new(1239, {
  [1] = Vertex.new(17, 2, 23, 87, 80, 67),
  [2] = Vertex.new(4, 6, 4, 87, 80, 67),
  [3] = Vertex.new(7, 6, 2, 87, 80, 67),
  [4] = Vertex.new(4, 35, -36, 79, 72, 61),
  [5] = Vertex.new(-19, 51, -39, 79, 72, 61),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Father Aereck in Lumbridge church.",
    title = "Getting started",
    actions = { Action.Direction:new(3246, 965, 3205) },
    postconditions = { Condition.DistanceTo:new(3246, 965, 3205, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["father aereck"]),
      Action.ConversationHighlight:new("I'm looking for a quest!"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Father Aereck.",
    actions = { Action.ModelHighlight:new(Models.npcs["father aereck"]) },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Go through the graveyard's southern exit and head west to a hut in the swamp.",
    title = "Ghostspeak amulet",
    actions = { Action.Direction:new(3207, 869, 3149) },
    postconditions = { Condition.DistanceTo:new(3207, 869, 3149, 10) },
  },
  {
    text = "Talk to Father Urhney inside the hut to get the ghostspeak amulet.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["father urhney"]),
      Action.ConversationHighlight:new("Father Aereck sent me to talk to you."),
      Action.ConversationHighlight:new("A ghost is haunting his graveyard."),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["ghostspeak amulet"]) },
  },
  {
    text = "Go back to the graveyard and open a coffin located south of the catacombs.",
    actions = { Action.Direction:new(3249, 965, 3193) },
    postconditions = { Condition.DistanceTo:new(3250, 965, 3193, 8) },
  },
  { actions = { Action.ModelHighlight:new(coffin) }, postconditions = { Condition.ModelVisible:new(restlessGhost) } },
  {
    text = "Equip the Ghostspeak amulet and talk to the ghost.",
    actions = {
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
      Action.ModelHighlight:new(restlessGhost),
      Action.ConversationHighlight:new("Yep. Now, tell me what the problem is."),
    },
    postconditions = {
      Condition.ConversationText:new("Okay. I'll try to get your skull back for you so you can rest in peace."),
    },
  },
  {
    text = "Search some rocks east of the lumbridge mining site for a muddy skull.",
    actions = { Action.Direction:new(3235, 597, 3148) },
    postconditions = { Condition.DistanceTo:new(3235, 597, 3148, 12) },
    title = "The skull",
  },
  { actions = { Action.ModelHighlight:new(rock) }, postconditions = { Condition.InventoryContains:new(skull) } },
  {
    text = "Kill the skeleton or run away.<br><br>Go back to the graveyard, reopen the coffin and use the skull on the open coffin.",
    actions = { Action.Direction:new(3249, 965, 3193) },
    postconditions = { Condition.DistanceTo:new(3250, 965, 3193, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(coffin) },
    postconditions = { Condition.ModelVisible:new(openCoffin) },
  },
  {
    actions = { Action.InventoryHighlight:new(skull), Action.ModelHighlight:new(openCoffin) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Restless Ghost",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 978566400,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = { ["Skeleton warlock"] = { level = "7", optional = true, quantity = 1 } },
})
