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
    text = "Talk to Ichi on the east side of Cyclosis island (if you have just completed the Jed Hunter miniquest you may need to relog for the chat option to show up).",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to Lookout Ekahi.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Talk to Lookout Ekahi again, in the centre of the island.",
    title = "Night watch",
    neededItems = { ["Alaea"] = { quantity = 1 }, ["Sea salt"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Kill all 27 Zyclopes." },
  { text = "Talk to Lookout Ekahi.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Lookout Ekahi again." },
  { text = "Talk to Ichi." },
  {
    text = "Talk to See in the south-west corner of the island.",
    actions = { Action.ConversationHighlight:new("Do you know anything about zyclopes?") },
  },
  {
    text = "Mine 30 sea salt crystals from the salty crabletines on the north-west beach if you don't have them already.",
  },
  {
    text = "Talk to Sinuman in the north-east corner for a bag of crushed salt.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Daya on the west side of the island to receive a bag of blessed salt.",
    actions = { Action.ConversationHighlight:new("Do you know anything about zyclopes?") },
  },
  { text = "Return to Ichi east on the island.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Go along the northern coast and speak to Undead Jed. Do not click anywhere until Undead Jed becomes attackable.",
    title = "Searching for Lookout Ekahi",
  },
  { text = "Kill Undead Jed." },
  { text = "Talk to Lookout Ekahi." },
  {
    text = "Talk to Lookout Ekahi again.",
    actions = { Action.ConversationHighlight:new("Eye for an Eye miniquest.") },
  },
}

return Quest:new({
  name = "Eye for an Eye (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1476057600,
  prereqQuests = { "Impressing the Locals", "Jed Hunter (miniquest)", "Cyclosis" },
})
