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
    text = "Speak to Navigator Jemi on Cyclosis (if you have just completed the Eye for an Eye miniquest you may need to relog for the chat option to show up).",
    title = "Tuai Leit",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Harbinger miniquest.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Travel to Tuai Leit.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Survive for 2 minutes. The player can simply stand there and wait.<ul><li>Repairing the hull is unnecessary as the damage dealt by the cannonballs is not enough to sink the ship in 2 minutes and Bosun Higgs repairs most of the damages for you.</li><li>White rings will indicate where the cannonballs will hit the ship. To reduce the need for food, move your character to avoid the incoming cannonballs.</li></ul>",
  },
  { text = "Talk to Navigator Jemi to finish." },
}

return Quest:new({
  name = "Harbinger (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1476057600,
  prereqQuests = { "Impressing the Locals", "Eye for an Eye (miniquest)" },
})
