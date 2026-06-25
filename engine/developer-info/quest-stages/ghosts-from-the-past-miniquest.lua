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
    text = "Travel to The Islands That Once Were Turtles.",
    title = "Ghosts from the past",
  },
  {
    text = "Talk to Quartermaster Gully (if you have just completed Harbringer you may need to relog for the chat option to show up).",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Ghosts from the Past miniquest.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Enough questions. Let's move on.") },
  },
  {
    text = "Climb both watchtowers on the island and talk to both skeletons of the Skulls pirate and the Skulls mercenary.",
  },
  {
    text = "Continue north and climb up the observatory.<ul><li>Talk to the skeleton of the Skulls thief at the top.</li></ul>",
  },
  {
    text = "Return to Quartermaster Gully.",
    actions = { Action.ConversationHighlight:new("Ghosts from the Past miniquest.") },
  },
}

return Quest:new({
  name = "Ghosts from the Past (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1476057600,
  prereqQuests = { "Impressing the Locals", "Harbinger (miniquest)" },
})
