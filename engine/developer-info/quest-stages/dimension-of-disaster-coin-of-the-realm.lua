local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Aris.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Dimension of Disaster.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Here's the coin. Tell my fortune."),
      Action.ConversationHighlight:new("What do you see?"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Dimension of Disaster."),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("Here's the coin. Tell my fortune."),
      Action.ConversationHighlight:new("Ugh, we've been through this already."),
    },
  },
  { text = "Investigate coin, choose 'Flip'." },
  {
    text = "Talk to Aris.  Replay:",
    actions = {
      Action.ConversationHighlight:new("Dimension of Disaster."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Dimension of Disaster."),
      Action.ConversationHighlight:new("Skip it."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Use Double-headed coin on fountain (at Varrock Square)." },
  { text = "Enter dimensional portal." },
  {
    text = "Talk to the Image of Zemouregal.",
    actions = { Action.ConversationHighlight:new("Get to the point.") },
    postconditions = { Condition.ConversationText:new(" I've dealt with your type before. A hero.") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Dimension of Disaster: Coin of the Realm",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1427068800,
  prereqQuests = {},
})
