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
    text = "Talk to Laniakea, south-east in the ruins on Anachronia.",
    title = "Walkthrough",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("The doorway at the top of the ruins.") },
  },
  {
    text = "[Accept Quest]<ul><li>She is also at the ruins around the lodestone but she tells you to go to the south-east and talk to her there.</li><li>You can use option 2 on the Slayer cape to get to her quickly or teleport to Xolo City X via the Orthen teleportation device, then run south-east.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("Zaros?"),
      Action.ConversationHighlight:new("Can we open the door somehow?"),
    },
  },
  { text = "Run west, up to the top of the stairs." },
  {
    text = "Siphon the shadow anima pools until you reach 100%.<ul><li>Completing each pool will damage you 200 points. Be aware of that in case you are on low life points.</li></ul>",
  },
  { text = "Unlock the door." },
  { text = "Talk to Zaros." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Raksha, the Shadow Colossus",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = false,
  length = Enums.length.short,
  releaseDate = -62135596800,
  prereqQuests = {},
})
