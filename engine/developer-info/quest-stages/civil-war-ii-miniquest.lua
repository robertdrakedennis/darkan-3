local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local nikkel = Model.new(25728, {
  [688] = Vertex.new(-30, 325, -45, 127, 127, 127),
  [739] = Vertex.new(-30, 324, -42, 127, 127, 127),
  [764] = Vertex.new(30, 325, -45, 127, 127, 127),
  [1919] = Vertex.new(-31, 329, -37, 127, 127, 127),
  [1972] = Vertex.new(31, 329, -37, 127, 127, 127),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Anne Dimitri, north of Quercus in the Wilderness.",
    title = "Walkthrough",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(3136, 933, 3535, { distance = 23 }),
      Action.ModelHighlight:new(Models.npcs["anne dimitri"], { distance = 24 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Anne Dimitri.",
    actions = { Action.ModelHighlight:new(Models.npcs["anne dimitri"]) },
    postconditions = { Condition.ConversationText:new("diplomatic approach") },
  },
  {
    text = "Complete the achievement Walk on the Wildy Side I by completing one Wilderness Flash Event.",
    postconditions = { Condition.ChatText:new("Walk on the Wildy Side") }, --not tested
  },
  {
    text = "Talk to Nikkel, west of Quercus.",
    actions = {
      Action.Direction:new(3116, 1429, 3520, { distance = 23 }),
      Action.ModelHighlight:new(nikkel, { distance = 24 }),
      Action.ConversationHighlight:new("Discuss Civil War II."),
      Action.ConversationHighlight:new("Bilrach's"),
    },
    postconditions = { Condition.ConversationText:new("'Anne'") }, --not tested
  },
  {
    text = "Talk to Anne Dimitri.",
    actions = {
      Action.Direction:new(3136, 933, 3535, { distance = 23 }),
      Action.ModelHighlight:new(Models.npcs["anne dimitri"], { distance = 24 }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Continue talking to Anne Dimitri.",
    actions = { Action.ModelHighlight:new(Models.npcs["anne dimitri"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Civil War II (miniquest)",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1665964800,
  prereqQuests = { "Civil War I (miniquest)" },
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
