local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--NPCs
local morgan = Model.new(3693, {
  [2796] = Vertex.new(0, 726, -58, 131, 96, 67),
  [2809] = Vertex.new(-5, 728, -66, 131, 96, 67),
  [2814] = Vertex.new(0, 726, -58, 131, 96, 67),
  [3571] = Vertex.new(-46, 749, 9, 131, 96, 67),
  [3685] = Vertex.new(46, 749, 9, 131, 96, 67),
})
local harlow = Model.new(3891, {
  [67] = Vertex.new(-36, 792, -32, 97, 94, 89),
  [70] = Vertex.new(-28, 804, -8, 97, 94, 89),
  [80] = Vertex.new(28, 804, -8, 97, 94, 89),
  [774] = Vertex.new(-76, 400, 84, 128, 91, 11),
  [789] = Vertex.new(-76, 336, 84, 128, 91, 11),
})
local bartender = Model.new(3954, {
  [2662] = Vertex.new(-16, 632, 8, 30, 27, 27),
  [2687] = Vertex.new(16, 632, 8, 30, 27, 27),
  [2689] = Vertex.new(-8, 508, -4, 157, 144, 144),
  [2767] = Vertex.new(-28, 680, 60, 157, 144, 144),
  [2777] = Vertex.new(28, 680, 60, 157, 144, 144),
})
local countDraynor = Model.new(5064, {
  [2459] = Vertex.new(-100, 860, 92, 144, 132, 132),
  [2601] = Vertex.new(100, 860, 92, 144, 132, 132),
  [4164] = Vertex.new(16, 600, -228, 28, 23, 14),
  [5039] = Vertex.new(16, 600, -228, 28, 23, 14),
  [5045] = Vertex.new(16, 600, -228, 41, 34, 21),
})

--Objects
local openDoor = Model.new(828, {
  [534] = Vertex.new(-228, 920, -208, 71, 60, 54),
  [734] = Vertex.new(-200, 936, 128, 62, 53, 47),
  [749] = Vertex.new(-248, 936, 128, 62, 53, 47),
  [752] = Vertex.new(-248, 936, 128, 62, 53, 47),
  [773] = Vertex.new(-248, 936, -128, 62, 53, 47),
})
local stairs = Model.new(5928, {
  [956] = Vertex.new(6656, 960, 6180, 71, 60, 54),
  [1128] = Vertex.new(5632, 960, 6180, 71, 60, 54),
  [5828] = Vertex.new(6676, 1300, 6176, 53, 45, 40),
  [5831] = Vertex.new(6676, 1300, 6176, 53, 45, 40),
  [5910] = Vertex.new(5612, 1300, 6176, 53, 45, 40),
})
local closedCoffin = Model.new(12246, {
  [2414] = Vertex.new(-596, 328, -520, 55, 50, 50),
  [2459] = Vertex.new(-596, 328, 520, 55, 50, 50),
  [3014] = Vertex.new(-596, 328, -520, 40, 37, 36),
  [9341] = Vertex.new(-396, 408, -552, 55, 50, 50),
  [9408] = Vertex.new(-428, 408, 504, 55, 50, 50),
})
local openCoffin = Model.new(7182, {
  [1505] = Vertex.new(-596, 328, -520, 40, 37, 36),
  [2627] = Vertex.new(-596, 328, -520, 55, 50, 50),
  [2672] = Vertex.new(-596, 328, 520, 55, 50, 50),
  [6935] = Vertex.new(-396, 408, -552, 55, 50, 50),
  [7002] = Vertex.new(-428, 408, 504, 55, 50, 50),
})
--Items
--Quest Items
local stake = Model.new(123, {
  [1] = Vertex.new(-16, 0, -116, 107, 100, 82),
  [2] = Vertex.new(-8, 24, -88, 107, 100, 82),
  [3] = Vertex.new(-8, 24, -116, 107, 100, 82),
  [14] = Vertex.new(8, 24, -76, 92, 86, 70),
  [26] = Vertex.new(16, 0, -80, 74, 69, 56),
})
local stakeHammer = Model.new(444, {
  [1] = Vertex.new(12, 32, -84, 88, 68, 45),
  [2] = Vertex.new(-12, 28, -128, 88, 68, 45),
  [3] = Vertex.new(-12, 32, -84, 88, 68, 45),
  [5] = Vertex.new(12, 28, -128, 88, 68, 45),
  [8] = Vertex.new(12, 36, -40, 75, 58, 38),
})

---@type QuestStep[]
local steps = {
  {
    text = "For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered'.<br><br>Talk to Morgan in Draynor Village.",
    title = "Getting started",
    actions = { Action.Direction:new(3098, 1285, 3268) },
    postconditions = { Condition.DistanceTo:new(3098, 1285, 3268, 10) },
  },
  {
    actions = { Action.Direction:new(3098, 1285, 3268) },
    postconditions = { Condition.DistanceTo:new(3098, 1285, 3268, 3), Condition.ModelVisible:new(morgan) },
  },
  {
    actions = { Action.ConversationHighlight:new("What terrible threat?"), Action.ModelHighlight:new(morgan) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue talking to Morgan",
    actions = { Action.ModelHighlight:new(morgan) },
    postconditions = { Condition.ConversationText:new("May Saradomin protect you, my friend!") },
  },
  {
    text = "Go upstairs and search in the northern drawers to get a garlic.",
    title = "Retired Vampyre Slayer",
    actions = { Action.Direction:new(3100, 1285, 3266) },
    postconditions = { Condition.DistanceToWithHeight:new(3102, 2469, 3266, 4) },
  },
  {
    actions = { Action.Direction:new(3096, 2469, 3270) },
    postconditions = { Condition.InventoryContains:new(Models.items["garlic"]) },
  },
  {
    text = "Go to the Blue Moon Inn located in Varrock.",
    actions = { Action.Direction:new(3220, 1125, 3397) },
    postconditions = { Condition.DistanceTo:new(3220, 1125, 3397, 8) },
  },
  {
    text = "Talk to Dr Harlow.",
    actions = {
      Action.ModelHighlight:new(harlow),
      Action.ConversationHighlight:new("Are you Dr Harlow, the famous vampyre slayer?"),
    },
    postconditions = { Condition.ConversationText:new("Buy ush a drink anyway.") },
  },
  {
    text = "Buy a beer from the bartender.",
    actions = {
      Action.ModelHighlight:new(bartender),
      Action.ConversationHighlight:new("A glass of your finest ale please."),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["beer"]) },
  },
  {
    text = "Talk to Dr. Harlow again to get a stake and stake hammer.",
    actions = {
      Action.ModelHighlight:new(harlow),
    },
    postconditions = { Condition.ConversationText:new("Once he's dead, speak to Morgan so he can notify the village.") },
  },
  {
    text = "Go to Draynor Manor, which is just north of Draynor Village.",
    title = "Slaying a Vampyre",
    actions = { Action.Direction:new(3108, 965, 3353.5) },
    postconditions = { Condition.DistanceTo:new(3108, 965, 3353.5, 12) },
    neededItems = {
      ["Garlic"] = { quantity = 1, model = Models.items["garlic"], duringQuest = true },
      ["Stake"] = { quantity = 1, model = stake, duringQuest = true },
      ["Stake hammer"] = { quantity = 1, model = stakeHammer, duringQuest = true },
      ["Combat gear"] = { quantity = 1 },
    },
  },
  {
    text = "Enter the manor and go to the east side.",
    actions = { Action.ModelHighlight:new(Models.objects["draynor manor front door"]) },
    postconditions = { Condition.DistanceTo:new(3108, 965, 3355, 1) },
  },
  { actions = { Action.Direction:new(3112.5, 965, 3360) }, postconditions = { Condition.ModelVisible:new(openDoor) } },
  {
    text = "Walk down the stairs.",
    actions = { Action.ModelHighlight:new(stairs) },
    postconditions = { Condition.DistanceTo:new(3080, 1093, 9776, 4) },
  },
  {
    text = "Open the coffin in the back of the room.",
    actions = { Action.ModelHighlight:new(closedCoffin) },
    postconditions = { Condition.ModelVisible:new(countDraynor), Condition.ModelVisible:new(openCoffin) },
  },
  { --Added extra actions to this in case if the user has to teleport out
    text = "Fight Count Draynor until your character will then automatically use the stake on him to kill him.<ul><li>If you have to teleport out, you'll have to get another stake from Dr. Harlow in Varrock.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I've lost some equipment you gave me, could I have replacements?"),
      Action.ModelHighlight:new(harlow),
      Action.ModelHighlight:new(countDraynor),
    },
    postconditions = { Condition.ChatText:new("You have slain the vampyre.") },
  },
  {
    text = "Return to Morgan. You can destroy the stake hammer.",
    title = "Finishing up",
    actions = { Action.Direction:new(3098, 1285, 3268) },
    postconditions = { Condition.DistanceTo:new(3098, 1285, 3268, 10) },
  },
  {
    actions = { Action.Direction:new(3098, 1285, 3268) },
    postconditions = { Condition.DistanceTo:new(3098, 1285, 3268, 3), Condition.ModelVisible:new(morgan) },
  },
  { actions = { Action.ModelHighlight:new(morgan) }, postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Vampyre Slayer",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 980640000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Beer"] = { quantity = 1, model = Models.items["beer"], duringQuest = true },
  },
  recommendedItems = { ["Garlic"] = { quantity = 1 } },
  combatNPCs = { ["Count Draynor"] = { level = "28", quantity = 1 } },
})
