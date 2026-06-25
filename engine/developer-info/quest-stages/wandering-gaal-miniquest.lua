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
    text = "Use a ring of visibility on a ring of stone anywhere outside of the TzHaar City.",
    title = "Marco polo",
  },
  {
    text = "Talk to TzHaar-Ga'al-Kot.",
    actions = {
      Action.ConversationHighlight:new("Can you read the ancient TzHaar Language?"),
      Action.ConversationHighlight:new("Go. I'll meet you at the Fight Cauldron."),
    },
  },
}

return Quest:new({
  name = "Wandering Ga'al (miniquest)",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.veryshort,
  releaseDate = 1357171200,
  prereqQuests = { "The Brink of Extinction", "Desert Treasure" },
})
