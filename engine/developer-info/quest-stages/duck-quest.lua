local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

-- Note: direction/position does not work the moment we are a duck.
-- It looks like Bolt cannot handle us becoming a duck (position is always the same)

local fishingSpot = Model.new(1185, {
  [1] = Vertex.new(-435, 0, -105, 127, 127, 127),
})

local duckfood = Model.new(420, {
  [1] = Vertex.new(-52, 28, 28, 143, 134, 110),
})

local debris = Model.new(327, {
  [1] = Vertex.new(-140, 33, -153, 137, 114, 88),
})

local breadstix = Model.new(3369, {
  [1] = Vertex.new(-43, 218, -38, 108, 80, 56),
})

local recipe = Model.new(414, {
  [1] = Vertex.new(28, 48, 60, 111, 92, 58),
})

local potion = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 115, 103, 47),
  }),
  Model.new(240, {
    [50] = Vertex.new(-4, 84, 12, 112, 145, 129, 0.8745),
  }),
})

local jemima = Model.new(27162, {
  [1] = Vertex.new(-149, 103, -34, 128, 128, 128),
})

local toad = Model.new(14916, {
  [1] = Vertex.new(57, 66, 26, 127, 127, 127),
})

local relax = Model.new(4077, {
  [1] = Vertex.new(-27, 41, -23, 127, 127, 127),
})

local peas = Model.any({
  Model.new(396, {
    [1] = Vertex.new(128, -30, 128, 120, 110, 110),
  }),
  Model.new(396, {
    [1] = Vertex.new(128, -24, 128, 120, 110, 110),
  }),
  Model.new(396, {
    [1] = Vertex.new(128, 24, 128, 120, 110, 110),
  }),
  Model.new(396, {
    [1] = Vertex.new(128, 12, 128, 120, 110, 110),
  }),
  Model.new(396, {
    [1] = Vertex.new(128, 24, 128, 120, 110, 110),
  }),
  Model.new(396, {
    [1] = Vertex.new(128, 38, 128, 120, 110, 110),
  }),
})

local badger = Model.new(1719, {
  [1] = Vertex.new(-93, 118, -56, 36, 36, 40),
})

local floorButton1 = Model.new(384, {
  [1] = Vertex.new(0, 106, 156, 127, 127, 127),
})

local floorButton2 = Model.new(384, {
  [1] = Vertex.new(0, 4, 156, 127, 127, 127),
})

local treeBud = Model.new(1200, {
  [1] = Vertex.new(-390, 281, -21, 128, 128, 128),
})

local statue = Model.new(8310, {
  [1] = Vertex.new(-1325, 236, 129, 127, 127, 127),
})

local dad = Model.new(27162, {
  [1] = Vertex.new(-149, 103, -34, 128, 128, 128),
})

---@type QuestStep[]
local steps = {
  {
    text = "Head south of the bank in Draynor Village and fish the fishing spot.",
    title = "Following the trail",
    actions = { Action.Direction:new(3087, 101, 3224) },
    postconditions = { Condition.DistanceTo:new(3087, 101, 3224, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(fishingSpot) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Finish the cutscene.",
    actions = { Action.ConversationHighlight:new("There, there!") },
    postconditions = { Condition.ConversationText:new("Let's continue") },
  },
  {
    text = "Pick up the debris north of the bridge and continue picking it up:<ul><li>South east along the coast</li><li>East into the swamp</li><li>North past the Lumbridge castle towards Fred the Farmer's house (through Lumbridge market)</li><li>North over the bridge</li><li>Just west near the Waddle Willow tree.</li>",
    actions = { Action.ModelHighlight:new(debris) },
    postconditions = { Condition.ConversationText:new("Perhaps that druid") },
  },
  {
    text = "Talk to Breadstix.",
    title = "Inducktion",
    neededItems = { ["Vial of Water"] = { quantity = 1 }, ["Duck Food"] = { quantity = 1 } },
    actions = {
      Action.ModelHighlight:new(breadstix),
      Action.ConversationHighlight:new("What are you up to at the moment?"),
      Action.ConversationHighlight:new("What's the language?"),
    },
    postconditions = { Condition.InventoryContains:new(recipe) },
  },
  {
    text = "Use duck food on a vial of water to make a duckspeak potion.",
    actions = { Action.InventoryHighlight:new(duckfood), Action.InventoryHighlight:new(Models.items["vial of water"]) },
    postconditions = { Condition.InventoryContains:new(potion) },
  },
  {
    text = "Talk to Breadstix again.",
    actions = { Action.ModelHighlight:new(breadstix) },
    postconditions = { Condition.ConversationText:new("Of course!") },
  },
  {
    text = "Drink the potion.",
    actions = {
      Action.InventoryHighlight:new(potion),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("You've almost got it") },
  },
  {
    text = "Talk to Jemima.",
    actions = {
      Action.ModelHighlight:new(jemima),
      Action.ConversationHighlight:new("Okay - I'll start the Inducktion."),
    },
    postconditions = { Condition.ConversationText:new("Hello there") },
  },
  {
    text = "Talk to Invigilatoad.",
    actions = {
      Action.ModelHighlight:new(toad),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = {
      Condition.ConversationText:new("Okay, okay! I'll eat the peas"),
    },
  },
  {
    text = "Eat each of the six peas. They are indicated as red dots on the minimap.",
    actions = {
      Action.ModelHighlight:new(peas, { highlightPriority = "all" }),
    },
    postconditions = { Condition.ConversationText:new("I should let the") },
  },
  {
    text = "Talk to Invigilatoad again.",
    actions = {
      Action.ModelHighlight:new(toad),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("It sounds like this will be a little more difficult") },
  },
  {
    text = "Sit down at the relaxing spot at the end of the path to the west.",
    actions = { Action.ModelHighlight:new(relax) },
    postconditions = { Condition.ConversationText:new("Take in your surroundings") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Okay, we're finally in Paddlington") },
  },
  {
    title = "Paddlington",
    text = "Talk to Paddlington Badger on the west edge of the sanctuary.",
    actions = { Action.ModelHighlight:new(badger) },
    postconditions = { Condition.ConversationText:new("No worries, dear!") },
  },
  {
    text = "Interact with the floor button on the east side of the sanctuary.",
    actions = { Action.ModelHighlight:new(floorButton1) },
    postconditions = { Condition.ConversationText:new("Hmm, interesting.") },
  },
  {
    text = "Press the floor button south from Paddlington badger.",
    actions = {
      Action.ModelHighlight:new(floorButton2),
      Action.ConversationHighlight:new("Something might happen?"),
    },
    postconditions = {
      Condition.ConversationText:new("That was much easier"),
    },
  },
  {
    text = "Press the floor button on the east side of the sanctuary.",
    actions = { Action.ModelHighlight:new(floorButton1) },
    postconditions = {
      Condition.ConversationText:new("At the same time, you and the duckling step on to the buttons."),
    },
  },
  {
    text = "Interact with the tree bud directly north of the east button.",
    actions = { Action.ModelHighlight:new(treeBud) },
    postconditions = { Condition.ConversationText:new("Now you have a beak full of soapy tree sap.") },
  },
  {
    text = "Interact with the toad statue twice.",
    actions = { Action.ModelHighlight:new(statue) },
    postconditions = { Condition.ConversationText:new("You and the duckling decide") },
  },
  {
    text = "Talk to duckling's dad or mum.",
    actions = { Action.ModelHighlight:new(dad) },
    postconditions = { Condition.ConversationText:new("You  make your way through Paddlington") },
  },
  {
    actions = {},
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Duck Quest",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1744588800,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Vial of Water"] = {
      quantity = 1,
      model = Models.items["vial of water"],
    },
    ["Duck Food"] = {
      quantity = 1,
      model = duckfood,
    },
  },
  recommendedItems = {},
  combatNPCs = {},
})
