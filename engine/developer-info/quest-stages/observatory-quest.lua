local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

local professor = NPCs["observatory professor"]
local plank = Items["plank"]
local bronzeBar = Items["bronze bar"]
local moltenGlass = Items["molten glass"]
local lensMould = Items["lens mould"]
local observatoryLens = Items["observatory lens"]
local dungeonStairs = Objects["observatory dungeon stairs"]
local kitchenStove = Objects["observatory kitchen stove"]

---@type QuestStep[]
local steps = {
  --#region Getting started
  {
    text = "Talk to the Observatory professor in the reception building north of Castle Wars.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered', and chat timestamps on.</li></ul>",
    title = "Getting started",
    neededItems = {
      ["Plank"] = { quantity = 3, model = plank },
      ["Bronze bar"] = { quantity = 1, model = bronzeBar },
      ["Molten glass"] = { quantity = 1, model = moltenGlass },
    },
    actions = {
      Action.Direction:new(2442, 69, 3186),
    },
    postconditions = { Condition.DistanceTo:new(2442, 69, 3186, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(professor),
      Action.ConversationHighlight:new("Talk about the Observatory Quest."),
      Action.ConversationHighlight:new("An Observatory?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to the professor three times to hand over the 3 planks, bronze bar, and molten glass.",
    actions = {
      Action.ModelHighlight:new(professor),
      Action.ConversationHighlight:new("Talk about the Observatory Quest."),
    },
    postconditions = { Condition.ConversationText:new("I don't suppose you could find it?") },
  },
  --#endregion
  --#region Obtaining the lens mould
  {
    text = "Go east to the abandoned buildings and climb down the dungeon stairs.",
    title = "Obtaining the lens mould",
    actions = {
      Action.Direction:new(2458, 2469, 3187),
    },
    postconditions = { Condition.DistanceTo:new(2458, 2469, 3187, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dungeonStairs),
    },
    postconditions = { Condition.DistanceTo:new(2357, 1061, 9397, 10) },
  },
  {
    text = "Search the chests in the dungeon to find the goblin kitchen key.<ul><li>Check the southeast chests first - if not there, it's guaranteed in the most southeast chest.</li></ul>",
    actions = {
      Action.Direction:new(2312, 1061, 9400),
      Action.Direction:new(2333, 1061, 9405),
      Action.Direction:new(2310, 1061, 9374),
      Action.Direction:new(2326, 1061, 9360),
      Action.Direction:new(2335, 1061, 9374),
      Action.Direction:new(2348, 1061, 9383),
      Action.Direction:new(2356, 1061, 9380),
      Action.Direction:new(2359, 1061, 9376),
      Action.Direction:new(2360, 1061, 9366),
      Action.Direction:new(2351, 1061, 9361),
      Action.Direction:new(2364, 1061, 9355),
    },
    postconditions = { Condition.ConversationText:new("kitchen key") },
  },
  {
    text = "Return to the staircase entrance, then go east, north, and west to find the goblin guard sleeping near the kitchen.",
    actions = { Action.Direction:new(2327, 1061, 9394) },
    postconditions = { Condition.DistanceTo:new(2327, 1061, 9394, 10) },
  },
  {
    text = "Kill or lure the goblin guard away from the kitchen gate.",
    actions = {
      Action.Direction:new(2327, 1061, 9394),
    },
    postconditions = { Condition.DistanceTo:new(2326, 965, 9391, 2) },
  },
  {
    text = "Inspect the stove in the kitchen to receive the lens mould.",
    actions = {
      Action.ModelHighlight:new(kitchenStove),
    },
    postconditions = { Condition.ConversationText:new("lens mould") },
  },
  --#endregion
  --#region Making the lens
  {
    text = "Return to the surface and talk to the professor.",
    title = "Making the lens",
    actions = { Action.Direction:new(2357, 1061, 9397) },
    postconditions = { Condition.DistanceTo:new(2357, 1061, 9397, 8) },
  },
  {
    postconditions = { Condition.DistanceTo:new(2458, 2469, 3185, 15) },
  },
  {
    actions = {
      Action.Direction:new(2442, 69, 3186),
    },
    postconditions = { Condition.DistanceTo:new(2442, 69, 3186, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(professor),
      Action.ConversationHighlight:new("Talk about the Observatory Quest."),
    },
    postconditions = { Condition.ConversationText:new("The professor gives you back the molten glass.") },
  },
  {
    text = "Use the lens mould on the molten glass to create an observatory lens.",
    actions = {
      Action.InventoryHighlight:new(lensMould),
      Action.InventoryHighlight:new(moltenGlass),
    },
    postconditions = { Condition.InventoryContains:new(observatoryLens) },
  },
  {
    text = "Talk to the professor again to give him the lens.",
    actions = {
      Action.ModelHighlight:new(professor),
      Action.ConversationHighlight:new("Talk about the Observatory Quest."),
    },
    postconditions = { Condition.ConversationText:new("the professor has gone ahead") },
  },
  --#endregion
  --#region Finishing up
  {
    text = "Return to the dungeon entrance at the abandoned buildings.",
    title = "Finishing up",
    actions = { Action.Direction:new(2458, 2469, 3187) },
    postconditions = { Condition.DistanceTo:new(2458, 2469, 3187, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dungeonStairs),
    },
    postconditions = { Condition.DistanceTo:new(2357, 1061, 9397, 10) },
  },
  {
    text = "Navigate south through the dungeon and climb the stairs to reach the Observatory.",
    actions = { Action.Direction:new(2337, 1061, 9353) },
    postconditions = { Condition.ConversationText:new("Hi, professor!") },
  },
  {
    actions = {},
    postconditions = { Condition.DistanceTo:new(2438, 3813, 3163, 10) },
  },
  {
    text = "Go upstairs.",
    actions = { Action.Direction:new(2442, 4813, 3159) },
    postconditions = { Condition.DistanceToWithHeight:new(2442, 4813, 3159, 10) },
  },
  {
    text = "Look through the telescope and remember which constellation you see.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered', and chat timestamps on.</li></ul>",
    actions = {},
    postconditions = { Condition.ChatText:new("You look through the telescope.") },
  },
  {
    text = "Tell the professor which constellation you observed. This cannot be solved by us and requires manual input.",
    actions = {
      Action.ModelHighlight:new(professor),
      Action.ConversationHighlight:new("Talk about the Observatory Quest."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
  --#endregion
}

return Quest:new({
  name = "Observatory Quest",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.medium,
  releaseDate = 704851200,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Normal plank"] = { quantity = 3, model = plank, duringQuest = false },
    ["Bronze bar"] = { quantity = 1, model = bronzeBar, duringQuest = false },
    ["Molten glass"] = { quantity = 1, model = moltenGlass, duringQuest = false },
  },
  recommendedItems = { ["Ring of duelling"] = { quantity = 1 } },
  combatNPCs = { ["Goblin guard"] = { level = "28", optional = true, quantity = 1 } },
})
