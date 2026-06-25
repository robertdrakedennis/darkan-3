local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local veronica = Model.new(3972, {
  [1] = Vertex.new(61, 67, 2, 56, 38, 23),
})

local staircaseBottom = Model.new(4881, {
  [1] = Vertex.new(-644, 480, 72, 56, 47, 43),
})

local professor = Model.new(5307, {
  [1] = Vertex.new(70, 21, -25, 64, 44, 26),
})

local fishfood = Model.any({
  Model.new(435, {
    [1] = Vertex.new(-8, 160, -48, 15, 16, 49),
  }),
  Model.new(435, {
    [1] = Vertex.new(-8, 160, -48, 12, 13, 42),
  }),
})

local poison = Model.new(120, {
  [1] = Vertex.new(-48, 0, -32, 21, 109, 24),
})

local fountain = Model.new(432, {
  [1] = Vertex.new(8734, 1445, 4050, 94, 121, 122),
})

local gauge = Model.new(216, {
  [1] = Vertex.new(36, 24, 36, 98, 90, 90),
})

local mound = Model.new(774, {
  [1] = Vertex.new(7404, 1124, 748, 127, 127, 127),
})

local key = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 155, 123, 14),
})

local tuber = Model.new(168, {
  [1] = Vertex.new(-64, 12, -96, 0, 0, 0),
})

local leverA = Model.new(660, {
  [49] = Vertex.new(-220, 684, -68, 24, 51, 21),
})
local leverAMh = Action.ModelHighlight:new(leverA)

local leverB = Model.new(660, {
  [49] = Vertex.new(-220, 684, -68, 71, 18, 14),
})
local leverBMh = Action.ModelHighlight:new(leverB)

local leverC = Model.new(660, {
  [49] = Vertex.new(-220, 684, -68, 14, 41, 71),
})
local leverCMh = Action.ModelHighlight:new(leverC)

local leverD = Model.new(660, {
  [49] = Vertex.new(-220, 684, -68, 96, 76, 9),
})
local leverDMh = Action.ModelHighlight:new(leverD)

local leverE = Model.new(660, {
  [49] = Vertex.new(-220, 684, -68, 35, 12, 57),
})
local leverEMh = Action.ModelHighlight:new(leverE)

local leverF = Model.new(660, {
  [49] = Vertex.new(-220, 684, -68, 83, 40, 7),
})
local leverFMh = Action.ModelHighlight:new(leverF)

local oilcan = Model.new(360, {
  [1] = Vertex.new(-140, 80, 144, 105, 96, 96),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Veronica located just inside the gate to Draynor Manor.",
    title = "Getting started",
    actions = { Action.Direction:new(3109, 965, 3331) },
    postconditions = { Condition.DistanceTo:new(3109, 965, 3331, 15) },
  },
  {
    actions = { Action.ModelHighlight:new(veronica) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Enter the manor  and climb the stairs, then the ladder.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.Direction:new(3109, 965, 3354) },
    postconditions = { Condition.DistanceTo:new(3109, 965, 3354, 0), Condition.DistanceTo:new(3108, 965, 3354, 0) },
  },
  {
    actions = { Action.ModelHighlight:new(staircaseBottom) },
    postconditions = { Condition.DistanceToWithHeight:new(3109, 2213, 3366, 2) },
  },
  {
    actions = { Action.Direction:new(3105, 2213, 3363) },
    postconditions = { Condition.DistanceToWithHeight:new(3105, 3429, 3362, 0) },
  },
  {
    text = "Talk to Professor Oddenstein.",
    actions = {
      Action.ModelHighlight:new(professor),
      Action.ConversationHighlight:new("I'm looking for a guy called Ernest."),
      Action.ConversationHighlight:new("I'm glad"),
    },
    postconditions = { Condition.ConversationText:new("Oh, and if I were you") },
  },
  {
    text = "On the 1st floor [2nd US], grab the fish food in the room south of the staircase.",
    title = "Obtaining the items",
    actions = { Action.Direction:new(3105, 3429, 3363) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3105, 2213, 3364, 0),
      Condition.InventoryContains:new(fishfood),
    },
  },
  {
    actions = { Action.ModelHighlight:new(fishfood) },
    postconditions = { Condition.InventoryContains:new(fishfood) },
  },
  {
    text = "On the ground floor [1st US], take the poison from the room south of the kitchen, in the northwest of the manor.",
    actions = { Action.Direction:new(3108.5, 2213, 3365) },
    postconditions = { Condition.DistanceToWithHeight:new(3109, 965, 3361, 2) },
  },
  {
    actions = { Action.ModelHighlight:new(poison) },
    postconditions = { Condition.InventoryContains:new(poison) },
  },
  {
    text = "Use the poison on the fish food.",
    actions = { Action.InventoryHighlight:new(poison), Action.InventoryHighlight:new(fishfood) },
    postconditions = { Condition.InventoryDoesNotContain:new(poison) }, -- poisoned fish food is same model as fish food
  },
  {
    text = "Exit the manor through the eastern-most room and walk to the fountain in the southwest corner of the yard.",
    actions = { Action.Direction:new(3123, 965, 3363.5) },
    postconditions = { Condition.DistanceTo:new(3123, 965, 3363, 1) },
  },
  {
    actions = { Action.Direction:new(3091, 965, 3335) },
    postconditions = { Condition.DistanceTo:new(3091, 965, 3335, 6) },
  },
  {
    text = "Use the poisoned fish food on the fountain.",
    actions = { Action.ModelHighlight:new(fountain), Action.InventoryHighlight:new(fishfood) },
    postconditions = { Condition.InventoryDoesNotContain:new(fishfood) },
  },
  {
    text = "Search the fountain for the pressure gauge.",
    actions = { Action.ModelHighlight:new(fountain) },
    postconditions = { Condition.InventoryContains:new(gauge) },
  },
  {
    text = "Go north to the compost mound and search it until a key is found.",
    actions = { Action.Direction:new(3086, 925, 3360) },
    postconditions = { Condition.DistanceTo:new(3086, 925, 3360, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(mound) },
    postconditions = { Condition.InventoryContains:new(key) },
  },
  {
    text = "Go inside the manor, enter the small room behind the staircase and take the tuber",
    actions = { Action.Direction:new(3109, 965, 3354) },
    postconditions = { Condition.DistanceTo:new(3109, 965, 3354, 0), Condition.DistanceTo:new(3108, 965, 3354, 0) },
  },
  {
    actions = { Action.Direction:new(3111, 965, 3368) },
    postconditions = { Condition.DistanceTo:new(3111, 965, 3368, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(tuber) },
    postconditions = { Condition.InventoryContains:new(tuber) },
  },
  {
    title = "Oil can puzzle",
    text = "Return to the entrance hall of the manor and go into the room to the west.",
    actions = { Action.Direction:new(3104, 965, 3360) },
    postconditions = { Condition.DistanceTo:new(3104, 965, 3360, 0) },
  },
  {
    text = "Search the candle sconce on the western wall to open the wall.",
    actions = { Action.Direction:new(3096.8, 1900, 3360) },
    postconditions = { Condition.DistanceTo:new(3095, 965, 3359, 1) },
  },
  {
    text = "Climb down the trap door ladder to the north in the new room.",
    actions = { Action.Direction:new(3092, 965, 3362) },
    postconditions = { Condition.DistanceTo:new(3117, 965, 9753, 1) },
  },
  {
    text = "Pull lever B up (ensure your chat is visible with timestamps or highlighting will not work)",
    actions = { leverBMh },
    postconditions = { Condition.ChatText:new("You pull lever B up.") },
  },
  {
    text = "Pull lever A up.",
    actions = { leverAMh },
    postconditions = { Condition.ChatText:new("You pull lever A up.") },
  },
  {
    text = "Pull lever D up",
    actions = { leverDMh },
    postconditions = { Condition.ChatText:new("You pull lever D up.") },
  },
  {
    text = "Pull lever B down",
    actions = { leverBMh },
    postconditions = { Condition.ChatText:new("You pull lever B down.") },
  },
  {
    text = "Pull levers A down",
    actions = { leverAMh },
    postconditions = { Condition.ChatText:new("You pull lever A down.") },
  },
  {
    text = "Pull lever E up",
    actions = { leverEMh },
    postconditions = { Condition.ChatText:new("You pull lever E up.") },
  },
  {
    text = "Pull lever F up",
    actions = { leverFMh },
    postconditions = { Condition.ChatText:new("You pull lever F up.") },
  },
  {
    text = "Pull lever C up",
    actions = { leverCMh },
    postconditions = { Condition.ChatText:new("You pull lever C up.") },
  },
  {
    text = "Pull lever E down",
    actions = { leverEMh },
    postconditions = { Condition.ChatText:new("You pull lever E down.") },
  },
  {
    text = "Go through the door in order to obtain the oil can.",
    actions = { Action.ModelHighlight:new(oilcan) },
    postconditions = { Condition.InventoryContains:new(oilcan) },
  },
  {
    text = "Return to Professor Oddenstein.",
    actions = { Action.Direction:new(3117, 965, 9754) },
    postconditions = { Condition.DistanceTo:new(3092, 965, 3361, 1) },
  },
  {
    actions = { Action.Direction:new(3096.3, 1800, 3357) },
    postconditions = { Condition.DistanceTo:new(3098, 965, 3359, 1) },
  },
  {
    actions = { Action.ModelHighlight:new(staircaseBottom) },
    postconditions = { Condition.DistanceToWithHeight:new(3109, 2213, 3366, 2) },
  },
  {
    actions = { Action.Direction:new(3105, 2213, 3363) },
    postconditions = { Condition.DistanceToWithHeight:new(3105, 3429, 3362, 0) },
  },
  {
    actions = { Action.ModelHighlight:new(professor) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Ernest the Chicken",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.medium,
  releaseDate = 980035200,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = { ["Skeleton"] = { level = "11", optional = true, quantity = 1 } },
})
