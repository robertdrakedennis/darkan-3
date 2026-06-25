local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region Objects
local staircase = Model.new(4416, {
  [1151] = Vertex.new(2885, 2418, 8083, 127, 127, 127),
  [1489] = Vertex.new(2903, 2128, 7962, 127, 127, 127),
  [2833] = Vertex.new(2885, 2418, 8083, 127, 127, 127),
  [3047] = Vertex.new(2903, 2187, 7962, 127, 127, 127),
  [3548] = Vertex.new(2885, 2418, 8083, 127, 127, 127),
})
--#endregion
--#region Quest Items
local blueBook = Model.new(495, {
  [7] = Vertex.new(-80, 16, -108, 8, 10, 87),
  [47] = Vertex.new(-80, 16, 116, 8, 10, 87),
  [398] = Vertex.new(68, 60, -24, 163, 150, 149),
  [400] = Vertex.new(48, 60, -44, 163, 150, 149),
  [495] = Vertex.new(36, 60, 72, 163, 150, 149),
})
local intelReport = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 148, 148, 136),
  [5] = Vertex.new(-72, 0, -56, 148, 148, 136),
  [187] = Vertex.new(-84, 28, -84, 91, 66, 8),
  [190] = Vertex.new(-84, 28, -84, 91, 66, 8),
  [195] = Vertex.new(-84, 28, -84, 91, 66, 8),
})
local phoenixCrossbow = Model.new(558, {
  [547] = Vertex.new(-24, 40, -92, 45, 45, 29),
  [549] = Vertex.new(-64, 44, 24, 45, 45, 29),
  [550] = Vertex.new(-24, 40, -92, 45, 45, 29),
  [555] = Vertex.new(-196, 40, 36, 45, 45, 29),
  [557] = Vertex.new(-76, 44, 28, 45, 45, 29),
})
local brokenShieldRight = Model.new(540, {
  [36] = Vertex.new(125, 20, 176, 127, 127, 127),
  [39] = Vertex.new(125, 20, 176, 127, 127, 127),
  [492] = Vertex.new(117, 33, 154, 127, 127, 127),
  [510] = Vertex.new(116, 20, -69, 127, 127, 127),
  [518] = Vertex.new(-12, 28, 162, 127, 127, 127),
})
local brokenShieldLeft = Model.new(471, {
  [390] = Vertex.new(101, 20, -80, 127, 127, 127),
  [405] = Vertex.new(24, 39, -187, 127, 127, 127),
  [426] = Vertex.new(-84, 22, -122, 127, 127, 127),
  [447] = Vertex.new(24, 39, -187, 127, 127, 127),
  [453] = Vertex.new(43, 26, -166, 127, 127, 127),
})
local shieldOfArrav = Model.new(1056, {
  [28] = Vertex.new(0, 24, 163, 127, 127, 127),
  [408] = Vertex.new(-62, 16, 164, 127, 127, 127),
  [894] = Vertex.new(92, 19, -87, 127, 127, 127),
  [909] = Vertex.new(-95, 18, -90, 127, 127, 127),
  [1035] = Vertex.new(-78, 24, 120, 127, 127, 127),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Reldo in the Varrock Palace library.",
    title = "Getting started",
    actions = {
      Action.Direction:new(3210, 1253, 3493, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["reldo"], { distance = 8 }),
      Action.ConversationHighlight:new("I'm in search of a quest."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Reldo.",
    actions = {
      Action.Direction:new(3210, 1253, 3493, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["reldo"], { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("I'm sure it's in the library here somewhere") },
  },
  {
    text = "Search the bookcase for <i>The Shield of Arrav</i> book.",
    actions = { Action.Direction:new(3211.8, 1953, 3493) },
    postconditions = { Condition.InventoryContains:new(blueBook) },
  },
  {
    text = "Read the book.",
    actions = { Action.InventoryHighlight:new(blueBook) },
    postconditions = { Condition.ConversationText:new("I should show this book") },
  },
  {
    text = "Talk to Reldo.",
    neededItems = { ["Coins"] = { quantity = 10 } },
    actions = {
      Action.Direction:new(3210, 1253, 3493, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["reldo"], { distance = 8 }),
      Action.ConversationHighlight:new("Do you know where I can find the Phoenix Gang?"),
    },
    postconditions = { Condition.ConversationText:new("with the Phoenix Gang") },
  },
  {
    text = "Talk to Baraek in the north-east of Varrock square.",
    title = "The Phoenix Gang",
    actions = {
      Action.Direction:new(3219, 965, 3434, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["baraek"], { distance = 8 }),
      Action.ConversationHighlight:new("Can you tell me where I can find the Phoenix Gang?"),
      Action.ConversationHighlight:new("Alright. Have 10 gold coins."),
      Action.ConversationHighlight:new("Thanks!"),
    },
    postconditions = { Condition.ConversationText:new("types to be messed about") },
  },
  {
    text = "Climb down the ladder into the Phoenix Gang Hideout.",
    actions = { Action.Direction:new(3244, 1125, 3383) },
    postconditions = { Condition.DistanceTo:new(3243, 5, 9783, 5) },
  },
  {
    text = "Talk to Straven and ask to join. Tell him you know who he is.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["straven"]),
      Action.ConversationHighlight:new("I know who you are!"),
      Action.ConversationHighlight:new("I'd like to offer you my services."),
    },
    postconditions = { Condition.ConversationText:new("I'll get right on it") },
  },
  {
    text = "Exit the hideout.",
    actions = { Action.Direction:new(3244, 5, 9783) },
    postconditions = { Condition.DistanceTo:new(3244, 1125, 3382, 4) },
  },
  {
    text = "Kill Jonny the beard in the Blue Moon Inn.",
    actions = {
      Action.Direction:new(3223, 1125, 3397, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["jonny the beard"], { distance = 8 }),
    },
    postconditions = { Condition.ModelVisible:new(intelReport) },
  },
  {
    text = "Pickup the intel report.",
    actions = { Action.ModelHighlight:new(intelReport) },
    postconditions = { Condition.InventoryContains:new(intelReport) },
  },
  {
    text = "Talk to Straven.",
    actions = { Action.Direction:new(3244, 1125, 3383) },
    postconditions = { Condition.DistanceTo:new(3243, 5, 9783, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["straven"]) },
    postconditions = { Condition.ConversationText:new("round the front of this building") },
  },
  {
    text = "Search the chest in the south-west corner of the hideout.",
    actions = { Action.Direction:new(3235, 5, 9761) },
    postconditions = { Condition.InventoryContains:new(brokenShieldRight) },
  },
  {
    text = "Exit the hideout.",
    actions = { Action.Direction:new(3244, 5, 9783) },
    postconditions = { Condition.DistanceTo:new(3244, 1125, 3382, 20) },
  },
  {
    text = "Climb the ladder in the house east of the Phoenix Gang Hideout.",
    actions = { Action.Direction:new(3252, 1125, 3384) },
    postconditions = { Condition.DistanceToWithHeight:new(3252, 2341, 3385, 4) },
  },
  {
    text = "Pick up the two Phoenix crossbows.",
    actions = { Action.ModelHighlight:new(phoenixCrossbow, { highlightPriority = "all" }) },
    postconditions = { Condition.InventoryContains:new(phoenixCrossbow, 2) },
  },
  {
    text = "Talk to Charlie the Tramp north of the Varrock lodestone.",
    title = "The Black Arm Gang",
    neededItems = { ["Coins"] = { quantity = 10 } },
    actions = {
      Action.Direction:new(3207, 1061, 3392, { distance = 14 }),
      Action.ModelHighlight:new(Models.npcs["charlie the tramp"], { distance = 14 }),
      Action.ConversationHighlight:new("Do you know where I can find the Black Arm Gang hideout?"),
      Action.ConversationHighlight:new("That sounds fair. (Pay 10 gold.)"),
    },
    postconditions = { Condition.ConversationText:new("She's pretty dangerous") },
  },
  {
    text = "Talk to Katrine.",
    actions = {
      Action.Direction:new(3185, 1125, 3385, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["katrine"], { distance = 12 }),
      Action.ConversationHighlight:new("I've heard you're the Black Arm Gang."),
      Action.ConversationHighlight:new("I'd rather not reveal my sources."), --fix
      Action.ConversationHighlight:new("I want to become a member of your gang."),
      Action.ConversationHighlight:new("Well, you can give me a try, can't you?"),
      Action.ConversationHighlight:new("No problem. I'll get you two phoenix crossbows."),
    },
    postconditions = { Condition.ConversationText:new("due east of here") },
  },
  {
    text = "Talk to Katrine again.",
    actions = {
      Action.Direction:new(3185, 1125, 3385, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["katrine"], { distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("Feel free to enter any of the rooms") },
  },
  {
    text = "Climb up the staircase to the north.",
    actions = { Action.ModelHighlight:new(staircase) },
    postconditions = { Condition.DistanceToWithHeight:new(3188, 2309, 3392, 4) },
  },
  {
    text = "Open and search the cupboard on the south-eastern wall.",
    actions = { Action.Direction:new(3189, 2609, 3385.9) },
    postconditions = { Condition.InventoryContains:new(brokenShieldLeft) },
  },
  {
    text = "Use one half of the shield on the other.",
    title = "Finishing up",
    actions = {
      Action.InventoryHighlight:new(brokenShieldRight),
      Action.InventoryHighlight:new(brokenShieldLeft),
    },
    postconditions = { Condition.InventoryContains:new(shieldOfArrav) },
  },
  {
    text = "Talk to King Roald in the Varrock Palace.",
    actions = {
      Action.Direction:new(3222, 1253, 3473, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["king roald"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about the Shield of Arrav."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Shield of Arrav",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.medium,
  releaseDate = 978566400,
  prereqQuests = {},
  questReqs = {},
  neededItems = { ["Coins"] = { quantity = 20 } },
  recommendedItems = {},
  combatNPCs = { ["Jonny the Beard"] = { level = "1", quantity = 1 } },
})
