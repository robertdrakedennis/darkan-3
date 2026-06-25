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
    text = "Enter the River of Noumenon by passing through the door behind Death in Death's office.",
    title = "Getting started",
    actions = { Action.ConversationHighlight:new("River Noumenon.") },
  },
  {
    text = "Approach the Sinister figure on the west side at the bottom of the stairs, then sift through Nomad's memory.",
  },
  {
    text = "In each of the following locations, in order, 'Sift through', 'End' and then 'Witness' the memory and receive the text saying where the next memory is.",
  },
  { text = "Once there is text overlaid on your screen, you can click away and move on to the next memory" },
  {
    text = "By the graves near the yew trees in Edgeville, south of the bank",
    title = "Visiting the memories of Nomad",
  },
  { text = "West of Aubury's rune shop in Varrock, east of the Blue Moon Inn" },
  { text = "At the Ourania altar, near the altar itself at the end of the tunnel" },
  { text = "Nomad's throne room - enter the tent in the Soul Wars lobby area" },
  { text = "In Dragith Nurn's chamber at the end of the Lumbridge Catacombs" },
  { text = "Near the small guard house just outside of the east gate in to Varrock" },
  { text = "Between the two graveyards in the Soul Wars lobby" },
  { text = "Nomad's throne room (again) - enter the tent in the Soul Wars lobby area" },
  { text = "Falador castle courtyard, southwest area" },
  { text = "Just west of the entrance to the Black Knight's Fortress" },
  {
    text = "Near the incandescent wisps south of the poison waste - north west of the crater, on the shore of the waste",
  },
  { text = "Return to the start point of the miniquest, and talk to the Sinister figure." },
}

return Quest:new({
  name = "Tales of Nomad (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1474848000,
  prereqQuests = { "Nomad's Elegy" },
})
