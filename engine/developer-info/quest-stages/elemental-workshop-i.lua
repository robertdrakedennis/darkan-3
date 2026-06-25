local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- Objects
local bellows = Model.new(3000, {
  [94] = Vertex.new(540, 696, 1025, 58, 52, 37),
  [96] = Vertex.new(524, 696, 1041, 58, 52, 37),
  [684] = Vertex.new(252, 568, 556, 83, 69, 53),
  [1558] = Vertex.new(-540, 696, 1025, 58, 52, 37),
  [2135] = Vertex.new(-252, 568, 556, 83, 69, 53),
})
local deadFurnace = Model.new(1296, {
  [773] = Vertex.new(512, 608, -280, 47, 42, 30),
  [845] = Vertex.new(512, 608, 308, 47, 42, 30),
  [857] = Vertex.new(512, 320, 308, 47, 42, 30),
  [867] = Vertex.new(472, 608, -308, 47, 42, 30),
  [879] = Vertex.new(472, 320, -308, 47, 42, 30),
})
local litFurnace = Model.new(1296, {
  [767] = Vertex.new(512, 608, -280, 47, 42, 30),
  [830] = Vertex.new(512, 608, 308, 47, 42, 30),
  [840] = Vertex.new(472, 608, -308, 47, 42, 30),
  [1013] = Vertex.new(512, 320, 308, 47, 42, 30),
  [1023] = Vertex.new(472, 320, -308, 47, 42, 30),
})

-- Quest Items
local batteredBook = Model.new(486, {
  [1] = Vertex.new(-8, 48, -84, 54, 41, 48),
  [2] = Vertex.new(0, 48, -72, 54, 41, 48),
  [3] = Vertex.new(12, 48, -84, 54, 41, 48),
  [4] = Vertex.new(-16, 48, -64, 54, 41, 48),
  [5] = Vertex.new(24, 48, -64, 54, 41, 48),
})
local stoneBowl = Model.new(258, {
  [1] = Vertex.new(-16, 64, -60, 67, 61, 61),
  [2] = Vertex.new(8, 0, -32, 67, 61, 61),
  [3] = Vertex.new(16, 64, -60, 67, 61, 61),
  [5] = Vertex.new(-8, 0, -32, 67, 61, 61),
  [7] = Vertex.new(-60, 64, -16, 67, 61, 61),
})
local stoneBowlLava = Model.new(258, {
  [1] = Vertex.new(-16, 64, -60, 133, 66, 40),
  [2] = Vertex.new(8, 0, -32, 133, 66, 40),
  [3] = Vertex.new(16, 64, -60, 133, 66, 40),
  [5] = Vertex.new(-8, 0, -32, 133, 66, 40),
  [7] = Vertex.new(-60, 64, -16, 133, 66, 40),
})

---@type QuestStep[]
local steps = {
  {
    text = "Search the bookcase in the house southwest of the Seers' Village bank.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(2716.1, 1285, 3481.5) },
    postconditions = { Condition.DistanceTo:new(2713, 1285, 3480, 4) },
  },
  {
    text = "Read the book.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Right-click on the book and click <i>'Cut-open'</i> to get a battered key. <b>Do not drop the book.</b>",
    actions = { Action.InventoryHighlight:new(batteredBook) },
    postconditions = { Condition.InventoryContains:new(Items["battered key"]) },
  },
  {
    text = "Go to the building with the anvil and click on the northern wall to open the odd-looking wall.",
    actions = { Action.Direction:new(2709, 1125, 3495.5) },
    postconditions = { Condition.DistanceTo:new(2709, 1125, 3498, 2) },
  },
  {
    text = "Go down the stairs.",
    actions = { Action.Direction:new(2710, 1125, 3498) },
    postconditions = { Condition.DistanceTo:new(2716, 965, 9888, 4) },
  },
  {
    text = "Go into the room to the north.",
    title = "Repairing a workshop",
    actions = { Action.Direction:new(2719, 965, 9904) },
    postconditions = { Condition.DistanceTo:new(2719, 965, 9904, 4) },
  },
  {
    text = "Turn the water controls east of the water wheel to turn it green.<ul><li>Kill a water elemental for a diary task.</li></ul>",
    actions = {
      Action.Direction:new(2726, 965, 9908),
    },
    postconditions = { Condition.ChatText:new("You turn the handle.") },
  },
  {
    text = "Turn the water controls west of the water wheel to turn it green.<ul><li>In the current version of the Quest Helper, we can't track the second turn. Please manually move to the next step.</li></ul>",
    actions = { Action.Direction:new(2713, 965, 9908) },
  },
  {
    text = "Pull the lever by the water wheel.",
    actions = { Action.Direction:new(2722, 965, 9906) },
    postconditions = { Condition.ChatText:new("You hear the sound of a water wheel starting up.") },
  },
  {
    text = "To obtain leather, search the crate north of the staircase in the middle room.",
    actions = { Action.Direction:new(2717, 965, 9894) },
    postconditions = { Condition.InventoryContains:new(Items["leather"]) },
  },
  {
    text = "Go to the eastern room.",
    actions = { Action.Direction:new(2734, 965, 9891) },
    postconditions = { Condition.DistanceTo:new(2734, 965, 9891, 4) },
  },
  {
    text = "With thread and leather, fix the bellows.<ul><li>Kill an air elemental for a diary task.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(Items["leather"]),
      Action.ModelHighlight:new(bellows),
    },
    postconditions = { Condition.ChatText:new("You stitch the leather over the hole in the bellows.") },
  },
  {
    text = "Pull the lever by the bellows.",
    actions = { Action.Direction:new(2734, 965, 9887) },
    postconditions = { Condition.ChatText:new("The bellows pump air down the pipe.") },
  },
  {
    text = "Go to the western room.",
    actions = { Action.Direction:new(2703, 965, 9894) },
    postconditions = { Condition.DistanceTo:new(2703, 965, 9894, 4) },
  },
  {
    text = "Mine an elemental rock and kill the earth elemental that pops out.",
    actions = { Action.ModelHighlight:new(NPCs["earth elemental"]) },
    postconditions = { Condition.InventoryContains:new(Items["elemental ore"]) },
  },
  {
    text = "Pick up the elemental ore it drops.",
    actions = { Action.ModelHighlight:new(Items["elemental ore"]) },
    postconditions = { Condition.InventoryContains:new(Items["elemental ore"]) },
  },
  {
    text = "Go back to the middle room and search the small boxes in the north-east part of the room to obtain a stone bowl.",
    actions = { Action.Direction:new(2724, 965, 9894) },
    postconditions = { Condition.InventoryContains:new(stoneBowl) },
  },
  {
    text = "Go to the south room.",
    title = "Finishing up",
    actions = { Action.Direction:new(2720, 965, 9877) },
    postconditions = { Condition.DistanceTo:new(2720, 965, 9877, 6) },
  },
  {
    text = "Use the stone bowl on the lava trough to the south.<ul><li>Kill a fire elemental for a diary task.</li></ul>",
    actions = { Action.InventoryHighlight:new(stoneBowl), Action.Direction:new(2716, 965, 9871) },
    postconditions = { Condition.ChatText:new("You fill the bowl with hot lava.") },
  },
  {
    text = "Use the bowl of lava on the furnace in the room.",
    actions = { Action.InventoryHighlight:new(stoneBowlLava), Action.ModelHighlight:new(deadFurnace) },
    postconditions = {
      Condition.ChatText:new("The furnace bursts to life."),
      Condition.ModelVisible:new(litFurnace),
    },
  },
  {
    text = "Use the elemental ore and four coal on the furnace.",
    actions = {
      Action.InventoryHighlight:new(Items["elemental ore"]),
      Action.ModelHighlight:new(litFurnace),
    },
    postconditions = { Condition.InventoryContains:new(Items["elemental bar"]) },
  },
  {
    text = "With the slashed book, go to the middle room and smith an elemental shield on an anvil workbench.",
    actions = { Action.Direction:new(2722, 965, 9888.3) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Elemental Workshop I",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.short,
  releaseDate = 1086134400,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Crafting", 20),
    Types.QuestReq.skill("Mining", 20),
    Types.QuestReq.skill("Smithing", 20),
  },
  neededItems = {
    ["Thread"] = { quantity = 1, model = Items["thread"] },
    ["Soft leather"] = { quantity = 1, model = Items["leather"], duringQuest = true },
    ["Coal"] = { quantity = 4, model = Items["coal"] },
    ["Combat gear"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = { ["Earth elemental"] = { level = "44", quantity = 1 } },
})
