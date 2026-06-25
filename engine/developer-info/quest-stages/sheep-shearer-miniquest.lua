local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local fred = Model.new(4026, {
  [2943] = Vertex.new(68, 704, -160, 132, 123, 84),
  [2949] = Vertex.new(76, 696, -172, 132, 123, 84),
  [3060] = Vertex.new(76, 704, -156, 124, 116, 79),
  [3627] = Vertex.new(38, 779, -31, 133, 98, 68),
  [3744] = Vertex.new(-38, 779, -31, 133, 98, 68),
})
--#endregion
--#region Items
local ballOfWool = Model.new(513, {
  [1] = Vertex.new(60, 0, -92, 136, 125, 125),
  [24] = Vertex.new(-76, 0, -60, 136, 125, 125),
  [62] = Vertex.new(60, 0, -92, 136, 125, 125),
  [101] = Vertex.new(-76, 0, -60, 136, 125, 125),
  [149] = Vertex.new(-48, 0, -132, 136, 125, 125),
})
--#endregion
--#region Quest Items
local blackWool = Model.new(225, {
  [1] = Vertex.new(-20, 16, 132, 33, 29, 25),
  [2] = Vertex.new(8, 16, 156, 33, 29, 25),
  [3] = Vertex.new(0, 24, 88, 33, 29, 25),
  [4] = Vertex.new(-76, 16, 132, 33, 29, 25),
  [6] = Vertex.new(-40, 36, 88, 33, 29, 25),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Fred the Farmer north of Lumbridge.",
    title = "Walkthrough",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Lumbridge lodestone",
      url = "Lumbridge_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3205, 965, 3291, { distance = 4 }),
      Action.ModelHighlight:new(fred, { distance = 8 }),
      Action.ConversationHighlight:new("I'm looking for a quest."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with Fred.",
    actions = {
      Action.ModelHighlight:new(fred),
      Action.ConversationHighlight:new("Of course!"),
      Action.ConversationHighlight:new("I'm something of an expert, actually."),
    },
    postconditions = { Condition.ConversationText:new("I'm not paying you by the hour!") },
  },
  {
    text = "Get 20 balls of wool:<ul><li>Buy the balls of wool from the Grand Exchange.</li><li>Buy the balls of wool from the general store in East Ardougne.</li></ul>",
    postconditions = { Condition.InventoryContains:new(ballOfWool) },
  },
  {
    text = "Talk to Fred.",
    actions = {
      Action.Direction:new(3205, 965, 3291, { distance = 4 }),
      Action.ModelHighlight:new(fred, { distance = 8 }),
      Action.ConversationHighlight:new("I have some balls of wool for you."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Sheep Shearer (miniquest)",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.veryshort,
  releaseDate = 978566400,
  prereqQuests = {},
  neededItems = { ["Balls of wool"] = { quantity = 20, model = ballOfWool } },
  questReqs = {},
  recommendedItems = {},
  combatNPCs = {},
})
