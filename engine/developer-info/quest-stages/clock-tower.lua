local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local brotherKojo = Model.new(3069, {
  [1696] = Vertex.new(-2, 725, -59, 107, 78, 55),
  [1701] = Vertex.new(-7, 724, -51, 107, 78, 55),
  [1704] = Vertex.new(2, 725, -59, 107, 78, 55),
  [1706] = Vertex.new(7, 724, -51, 107, 78, 55),
  [2161] = Vertex.new(0, 735, -7, 28, 139, 127),
})
--#endregion
--#region Objects
local pulledLever = Model.new(408, {
  [123] = Vertex.new(-256, 932, -108, 79, 74, 72),
  [147] = Vertex.new(-256, 852, 84, 71, 67, 65),
  [269] = Vertex.new(-184, 880, -68, 88, 84, 81),
  [290] = Vertex.new(-192, 888, -56, 88, 84, 81),
  [347] = Vertex.new(-256, 932, -108, 71, 67, 65),
})
local poisonedFoodTrough = Model.new(558, {
  [195] = Vertex.new(-192, 168, -104, 161, 148, 148),
  [227] = Vertex.new(-368, 168, -44, 161, 148, 148),
  [358] = Vertex.new(488, 112, -164, 59, 47, 12),
  [361] = Vertex.new(-480, 112, 172, 59, 47, 12),
  [396] = Vertex.new(488, 112, -164, 59, 47, 12),
})
--#endregion
--#region Quest Items
local blackCog = Model.new(354, {
  [56] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [60] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [65] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [75] = Vertex.new(100, 0, -120, 95, 88, 87),
  [79] = Vertex.new(100, 0, -120, 95, 88, 87),
})
local redCog = Model.new(354, {
  [56] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [60] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [65] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [75] = Vertex.new(100, 0, -120, 95, 88, 87),
  [79] = Vertex.new(100, 0, -120, 95, 88, 87),
})
local blueCog = Model.new(354, {
  [56] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [57] = Vertex.new(-100, 64, -120, 95, 88, 87),
  [60] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [65] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [75] = Vertex.new(100, 0, -120, 95, 88, 87),
})
local ratPoison = Model.new(120, {
  [47] = Vertex.new(-16, 104, -36, 157, 146, 146),
  [51] = Vertex.new(-12, 84, -40, 12, 12, 12),
  [53] = Vertex.new(-4, 92, -40, 12, 12, 12),
  [57] = Vertex.new(4, 84, -40, 12, 12, 12),
  [59] = Vertex.new(12, 92, -40, 12, 12, 12),
})
local whiteCog = Model.new(354, {
  [56] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [57] = Vertex.new(-100, 64, -120, 95, 88, 87),
  [60] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [65] = Vertex.new(-100, 0, -120, 95, 88, 87),
  [75] = Vertex.new(100, 0, -120, 95, 88, 87),
})
--#endregion

local steps = {
  {
    text = "Talk to Brother Kojo at the Clock Tower south-west of the Ardougne Zoo.<ul><li>There's a bucket spawn east of the Clock Tower next to a well.</li></ul>",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Kandarin Monastery",
      url = "Kandarin_monastery_Teleport_icon.png",
    },
    actions = {
      Action.Direction:new(2570, 1385, 3250, { distance = 5 }),
      Action.ModelHighlight:new(brotherKojo, { distance = 8 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Brother Kojo.",
    actions = { Action.ModelHighlight:new(brotherKojo) },
    postconditions = { Condition.ConversationText:new("full of strange beasts") },
  },
  {
    text = "Climb down the ladder in the central room.",
    actions = { Action.Direction:new(2566, 1285, 3242, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2566, 645, 9643, 4) },
  },
  {
    text = "Enter the northeast door closest to the dark grey tile and go as far east as possible.",
    title = "Black cog",
    neededItems = { ["Bucket of water"] = { quantity = 1 } },
    actions = { Action.Direction:new(2581.5, 1445, 9651) },
    postconditions = { Condition.DistanceTo:new(2583, 645, 9651, 1) },
  },
  {
    text = "Pick up the black cog.",
    actions = { Action.Direction:new(2613, 645, 9639) },
    postconditions = { Condition.InventoryContains:new(blackCog) },
  },
  {
    text = "Use the black cog on the black spindle next to the ladder you came down when you entered the dungeon.",
    actions = {
      Action.Direction:new(2569.75, 1345, 9642),
      Action.InventoryHighlight:new(blackCog),
    },
    postconditions = { Condition.ConversationText:new("fits perfectly") },
  },
  {
    text = "Enter the south-east door.",
    title = "Red cog",
    actions = { Action.Direction:new(2581.5, 1445, 9648) },
    postconditions = { Condition.DistanceTo:new(2583, 645, 9648, 1) },
  },
  {
    text = "Pick up the red cog.",
    actions = { Action.Direction:new(2583, 645, 9613, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(redCog) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2566, 1445, 9642) },
    postconditions = { Condition.DistanceTo:new(2566, 1285, 3243, 4) },
  },
  {
    text = "Use the red cog on the red spindle.",
    actions = {
      Action.Direction:new(2568, 1935, 3242.75),
      Action.InventoryHighlight:new(redCog),
    },
    postconditions = { Condition.ConversationText:new("fits perfectly") },
  },
  {
    text = "Climb down the ladder outside of the Ardougne Zoo near the camel enclosure.",
    title = "Blue cog",
    actions = { Action.Direction:new(2621, 949, 3261, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2621, 645, 9660, 4) },
  },
  {
    text = "Push the wall at the end of the path.",
    actions = { Action.Direction:new(2575.5, 1245, 9631) },
    postconditions = { Condition.DistanceTo:new(2574, 645, 9631, 1) },
  },
  {
    text = "Pick up the blue cog.",
    actions = { Action.ModelHighlight:new(blueCog) },
    postconditions = { Condition.InventoryContains:new(blueCog) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(2572.15, 1245, 9631) },
    postconditions = { Condition.DistanceTo:new(2572, 1205, 3230, 4) },
  },
  {
    text = "Climb up the staircase in the central room of the Clock Tower.",
    actions = { Action.Direction:new(2572.5, 1885, 3240.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2571, 2245, 3241, 4) },
  },
  {
    text = "Use the blue cog on the blue spindle.",
    actions = {
      Action.Direction:new(2569, 2945, 3240.25),
      Action.InventoryHighlight:new(blueCog),
    },
    postconditions = { Condition.ConversationText:new("fits perfectly") },
  },
  {
    text = "Climb down the staircase.",
    title = "White cog",
    actions = { Action.Direction:new(2572.5, 2845, 3240.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2572, 1285, 3242, 4) },
  },
  {
    text = "Climb down the ladder to the basement.",
    actions = { Action.Direction:new(2566, 1285, 3242, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2566, 645, 9643, 4) },
  },
  {
    text = "Enter the north-west door closest to the white tile.",
    actions = { Action.Direction:new(2575.5, 1445, 9651) },
    postconditions = { Condition.DistanceTo:new(2574, 645, 9651, 1) },
  },
  {
    text = "Pick up the rat poison.",
    actions = { Action.ModelHighlight:new(ratPoison) },
    postconditions = { Condition.InventoryContains:new(ratPoison) },
  },
  {
    text = "Pull the western red lever.",
    actions = { Action.Direction:new(2591, 1445, 9660.7) },
    postconditions = { Condition.ModelVisible:new(pulledLever) },
  },
  {
    text = "Use the rat poison on the food trough.",
    actions = {
      Action.Direction:new(2586.5, 945, 9654),
      Action.InventoryHighlight:new(ratPoison),
    },
    postconditions = { Condition.ModelVisible:new(poisonedFoodTrough) },
  },
  {
    text = "Wait for the rats to die.",
    postconditions = { Condition.ChatText:new("seem to be dying") },
  },
  {
    text = "Go through the gate.",
    actions = { Action.Direction:new(2578.5, 1445, 9656) },
    postconditions = { Condition.DistanceTo:new(2577, 645, 9656, 1) },
  },
  {
    text = "Pick up the white cog.",
    actions = { Action.ModelHighlight:new(whiteCog) },
    postconditions = { Condition.InventoryContains:new(whiteCog) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(2576, 1545, 9655) },
    postconditions = { Condition.DistanceTo:new(2577, 261, 3255, 4) },
  },
  {
    text = "Climb the staircase in the Clock Tower to the 2nd floor.",
    actions = { Action.Direction:new(2572.5, 1885, 3240.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2571, 2245, 3241, 4),
      Condition.DistanceToWithHeight:new(2571, 3205, 3241, 4),
    },
  },
  {
    actions = { Action.Direction:new(2572.5, 2245, 3240.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2571, 3205, 3241, 4) },
  },
  {
    text = "Use the white cog on the white spindle.",
    actions = {
      Action.Direction:new(2567.25, 3855, 3241),
      Action.InventoryHighlight:new(whiteCog),
    },
    postconditions = { Condition.ConversationText:new("fits perfectly") },
  },
  {
    text = "Return to Brother Kojo.",
    title = "Finishing up",
    actions = { Action.Direction:new(2572.5, 3205, 3240.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2572.5, 2845, 3240.5, 4),
      Condition.DistanceToWithHeight:new(2572, 1285, 3242, 4),
    },
  },
  {
    actions = { Action.Direction:new(2572.5, 2845, 3240.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2572, 1285, 3242, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(brotherKojo) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Clock Tower",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.short,
  releaseDate = 1024272000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Bucket of water"] = { quantity = 1, model = Models.items["bucket of water"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = {},
})
