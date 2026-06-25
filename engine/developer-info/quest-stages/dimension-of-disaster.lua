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
    title = "Starting off",
    text = "Talk to Aris in her tent south-west of Varrock square.",
  },
  { text = "Complete Dimension of Disaster: Coin of the Realm starter quest (exit out of this, its in the list)." },
  { text = "Complete all other Dimension of Disaster subquests." },
  { text = "Talk to Aris. (Chat 1)" },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Dimension of Disaster",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.veryverylong,
  releaseDate = 1427068800,
  prereqQuests = {},
})
