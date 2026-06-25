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
    text = "Talk to Veliaf Hurtz north of Canifis.",
    title = "Off to Canifis",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Build ten statues on the statue plinths with two granite (5kg) for each statue." },
  { text = "Add the following items to each statue along with a blisterwood sickle." },
  { text = "Talk to Veliaf Hurtz." },
}

return Quest:new({
  name = "In Memory of the Myreque (miniquest)",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1460332800,
  prereqQuests = { "The Lord of Vampyrium", "Legends' Quest" },
})
