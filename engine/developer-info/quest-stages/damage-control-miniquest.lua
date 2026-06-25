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
    title = "Starting out",
    text = "Travel to Waiko.",
  },
  {
    text = "Talk to Bosun Higgs who is next to your ship (if you have just completed the Harbinger miniquest you may need to relog for the chat option to show up).",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Damage Control miniquest.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to Rosie in the Waiko marketplace for a ship repair kit in exchange for 2,500 chimes." },
  {
    text = "Talk to Bosun Higgs to set sail.",
    actions = { Action.ConversationHighlight:new("Damage Control miniquest.") },
  },
  {
    title = "Cannon parts",
    text = "Collect the 6 cannon parts at the locations shown, while avoiding the malignant entity.",
  },
  { text = "Take the Goshima note hanging on the left wooden pillar next to the gate entrance." },
  { text = "Talk to Bosun Higgs.", actions = { Action.ConversationHighlight:new("Damage Control miniquest.") } },
}

return Quest:new({
  name = "Damage Control (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1476057600,
  neededItems = {
    ["chimes"] = { quantity = 2500 },
  },
  prereqQuests = { "Impressing the Locals", "Harbinger (miniquest)" },
})
