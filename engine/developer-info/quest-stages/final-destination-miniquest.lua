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
    text = "Talk to Lookout Ekahi (if you have just completed the previous miniquests you may need to relog for the chat option to show up).",
    title = "Commandeering the ship",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Final Destination miniquest.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Fire the cannon." },
  { text = "Climb the ladder." },
  { text = "Kill the Zyclopses." },
  { text = "Climb another ladder." },
  { text = "Kill the zombies." },
  { text = "Kill the 2 skeletons." },
  { text = "Climb the ladder." },
  { text = "Kill 2 more Zyclopses." },
  { text = "Defeat Captain Cora then talk to her." },
  { text = "Talk to Captain Cora again.", title = "Goshima" },
  { text = "Run east to the gates. Cora will talk to you." },
  { text = "Run southwest to the destroyed colony. Cora will talk to you again." },
  { text = "Jiangshi will appear between the colony and the gates, talk to Jiangshi." },
  { text = "Talk to Captain Cora." },
}

return Quest:new({
  name = "Final Destination (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1476057600,
  prereqQuests = {
    "Impressing the Locals",
    "Tuai Leit's Own (miniquest)",
    "Ghosts from the Past (miniquest)",
    "Damage Control (miniquest)",
  },
})
