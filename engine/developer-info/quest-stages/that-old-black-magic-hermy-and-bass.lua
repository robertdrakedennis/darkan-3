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
    text = "Talk to Lady Grey about the drummer.",
    title = "The drummer",
    actions = { Action.ConversationHighlight:new("Drummer.") },
  },
  {
    text = "Kill Hermod, the Spirit of War until you receive animated drumsticks.<ul><li>Requires 65 Necromancy and completion of The Spirit of War.</li></ul>",
  },
  { text = "Return to Lady Grey.", actions = { Action.ConversationHighlight:new("Drummer.") } },
  { text = "Talk to the The Spirit of Rhythm." },
  {
    text = "Unequip items from both hand slots and talk to Ed.<ul><li>He's in the boat rental shop east of the northernmost docks.</li></ul>",
    actions = { Action.ConversationHighlight:new("Take a boat out onto the lake.") },
  },
  {
    text = "Manifest the animated drumsticks and a splash will appear at the location of a lost drum. Refer to the location maps below.",
  },
  { text = "Continue to manifest the animated drumsticks until you have found all four drums." },
  { text = "Return to the bandstand and talk to the The Spirit of Rhythm." },
}

return Quest:new({
  name = "That Old Black Magic: Hermy and Bass",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.long,
  releaseDate = 1691366400,
  prereqQuests = { "That Old Black Magic: My One and Only Lute", "The Spirit of War" },
})
