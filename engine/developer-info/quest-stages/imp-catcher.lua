local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local mizgog = Model.new(5706, {
  [1] = Vertex.new(0, 678, 40, 11, 75, 122),
})

local sink = Model.new(180, {
  [1] = Vertex.new(2755, 1946, 7830, 0, 0, 0),
})

local range = Model.new(2427, {
  [1] = Vertex.new(6317, 1939, 7811, 30, 27, 27),
})

local hotWater = Model.multi({
  Model.new(276, {
    [1] = Vertex.new(-16, -4, 48, 109, 78, 9),
  }),
  Model.new(324, {
    [1] = Vertex.new(-52, 184, 24, 135, 137, 148),
  }),
})

local imp = Model.new(2364, {
  [1] = Vertex.new(12, 280, -27, 127, 127, 127),
})

local phlegmatic = Model.new(906, {
  [1] = Vertex.new(8, 50, 55, 154, 144, 143),
})

local phlegmaticBead = Model.new(168, {
  [1] = Vertex.new(-16, 24, 48, 161, 148, 147),
})

local choleric = Model.new(906, {
  [1] = Vertex.new(8, 50, 55, 133, 125, 33),
})

local cholericBead = Model.new(168, {
  [1] = Vertex.new(-16, 24, 48, 152, 151, 13),
})

local melancholic = Model.new(906, {
  [1] = Vertex.new(8, 50, 55, 25, 25, 25),
})

local melancholicBead = Model.new(168, {
  [1] = Vertex.new(-16, 24, 48, 0, 0, 0),
})

local sanguine = Model.new(906, {
  [1] = Vertex.new(8, 50, 55, 119, 39, 33),
})

local sanguineBead = Model.new(168, {
  [1] = Vertex.new(-16, 24, 48, 152, 24, 13),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Wizard Mizgog on the 1st floor (2nd floor [US]) of the Wizards' Tower, just southeast of the beam.",
    title = "Looking for imps",
    actions = { Action.Direction:new(3102.5, 1925, 3155.5) },
    postconditions = { Condition.DistanceTo:new(3102.5, 1925, 3155.5, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["wizard tower beam 0"]) },
    postconditions = { Condition.DistanceToWithHeight:new(3102, 8613, 3155, 1000) }, -- check on same level
  },
  {
    actions = {
      Action.ModelHighlight:new(mizgog),
      Action.ConversationHighlight:new("Can I help you?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Go to Lumbridge Castle kitchen to make a bowl of hot water. If you have not done Cook's Assistant, use the range in the southeasternmost house in Lumbridge.",
    action = { Action.Direction:new(3208, 1477, 3213) },
    postconditions = {
      Condition.DistanceTo:new(3208, 1477, 3213, 15),
      Condition.InventoryContains:new(Models.items["bowl of water"]),
      Condition.InventoryContains:new(hotWater),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.items["bowl"]) },
    postconditions = {
      Condition.InventoryContains:new(Models.items["bowl"]),
      Condition.InventoryContains:new(Models.items["bowl of water"]),
      Condition.InventoryContains:new(hotWater),
    },
  },
  {
    actions = { Action.ModelHighlight:new(sink) },
    postconditions = { Condition.InventoryContains:new(Models.items["bowl of water"]) },
  },
  {
    actions = { Action.ModelHighlight:new(range) },
    postconditions = { Condition.InventoryContains:new(hotWater) },
  },
  {
    text = "Go to the Water ruins (west side of Lumbridge Swamp)",
    actions = { Action.Direction:new(3185, 1229, 3165) },
    postconditions = { Condition.DistanceTo:new(3185, 1229, 3165, 10) },
  },
  {
    text = "Talk to the Phlegmatic imp.",
    actions = {
      Action.ModelHighlight:new(imp),
      Action.ConversationHighlight:new("Have you tried a bowl of hot water?"),
    },
    postconditions = { Condition.ConversationText:new("Now, where can I find") },
  },
  {
    text = "Talk to the imp again, with a bowl of hot water in your inventory.",
    actions = {
      Action.ModelHighlight:new(imp),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ModelVisible:new(phlegmatic), Condition.InventoryContains:new(phlegmaticBead) },
  },
  {
    text = "Take the Phlegmatic bead it drops.",
    actions = { Action.ModelHighlight:new(phlegmatic) },
    postconditions = { Condition.InventoryContains:new(phlegmaticBead) },
  },
  {
    text = "Go to the Fire ruins (north of Al Kharid; outside Het's Oasis), talk to and fight the Choleric imp.",
    actions = { Action.Direction:new(3317, 1205, 3252) },
    postconditions = { Condition.DistanceTo:new(3317, 1205, 3252, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(imp),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ModelVisible:new(choleric), Condition.InventoryContains:new(cholericBead) },
  },
  {
    text = "Pick up the Choleric bead it drops.",
    actions = { Action.ModelHighlight:new(choleric) },
    postconditions = { Condition.InventoryContains:new(cholericBead) },
  },
  {
    text = "Go to the Earth ruins (north-east of Varrock; south of Fort Forinthry) talk to the Melancholic imp.",
    actions = { Action.Direction:new(3303, 21, 3477) },
    postconditions = { Condition.DistanceTo:new(3303, 21, 3477, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(imp),
      Action.ConversationHighlight:new("Why would he be disappointed?"),
      Action.ConversationHighlight:new("Some days you'll have setbacks. Tomorrow is a new day."),
      Action.ConversationHighlight:new("Take your time. No one is rushing you to feel better."),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ModelVisible:new(melancholic), Condition.InventoryContains:new(melancholicBead) },
  },
  {
    text = "Pick up the Melancholic bead it drops.",
    actions = { Action.ModelHighlight:new(melancholic) },
    postconditions = { Condition.InventoryContains:new(melancholicBead) },
  },
  {
    text = "Go to the Air ruins (west of Varrock; southeast of Barbarian Village) and talk to the Sanguine imp.",
    actions = { Action.Direction:new(3127, 1301, 3410) },
    postconditions = { Condition.DistanceTo:new(3127, 1301, 3410, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(imp) },
    postconditions = { Condition.ConversationText:new("I-accept!") },
  },
  {
    text = "Race the imp around the altar 4 times by clicking and running inside the four different checkpoints when they appear.",
    postconditions = { Condition.ConversationText:new("You've done... worn me out.") },
  },
  {
    text = "Continue the conversation and take the Sanguine bead it drops.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ModelVisible:new(sanguine), Condition.InventoryContains:new(sanguineBead) },
  },
  {
    actions = { Action.ModelHighlight:new(sanguine) },
    postconditions = { Condition.InventoryContains:new(sanguineBead) },
  },
  {
    text = "Return and talk to Wizard Mizgog.",
    actions = { Action.Direction:new(3102.5, 1925, 3155.5) },
    postconditions = { Condition.DistanceTo:new(3102.5, 1925, 3155.5, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["wizard tower beam 0"]) },
    postconditions = { Condition.DistanceToWithHeight:new(3102, 8613, 3155, 1000) }, -- check on same level
  },
  {
    actions = {
      Action.ModelHighlight:new(mizgog),
      Action.ConversationHighlight:new("I've got all four beads."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Imp Catcher",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 982281600,
  prereqQuests = {},
  questReqs = {},
  neededItems = { ["Bowl of hot water"] = { quantity = 1 } },
  recommendedItems = {},
  combatNPCs = { ["Choleric imp"] = { level = "5", quantity = 1 } },
})
