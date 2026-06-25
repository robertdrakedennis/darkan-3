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
    text = "Talk to the Priest of Guthix just southwest of the Piscatoris Fishing Colony (slightly north west if using fairy ring AKQ).",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("No more questions.") },
  },
  { text = "Enter the cave." },
  {
    text = "Proceed through the caves whilst cutting twigs from the trees along the way (you should collect 5 total)",
    title = "The Caves",
  },
  { text = "In the final cave, fletch the twigs into ribbons.", title = "The Phoenix Lair" },
  { text = "Craft the pyre then light it." },
  { text = "Talk to the Phoenix." },
  { text = "Exit the cave entrance." },
  { text = "Talk to Brian Twitcher (Priest of Guthix)." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "In Pyre Need",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1231200000,
  prereqQuests = {},
})
