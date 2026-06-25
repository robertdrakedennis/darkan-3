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
    title = "Getting started",
    text = "Talk to Irwinsson.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Tell me more about yourself.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to Sigli the Huntsman located at the main gate of Rellekka." },
  {
    text = "Head back to Anachronia and give Irwinsson his greatbow.",
    actions = { Action.ConversationHighlight:new("Talk about Sigli.") },
    postconditions = {
      Condition.ConversationText:new(
        " He is deeply sorry that he treated you so harshly and insisted it was only ever out of love."
      ),
    },
  },
}

return Quest:new({
  name = "Father and Son (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1570406400,
  prereqQuests = { "Anachronia base camp tutorial" },
})
