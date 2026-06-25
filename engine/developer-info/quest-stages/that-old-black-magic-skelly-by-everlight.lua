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
    text = "Talk to 'No Gums' Murray in the City of Um's bandstand, behind Death, next to Lady Grey.",
    title = "'No Gums' Murray",
    neededItems = { ["Everlight trumpet"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>If Murray doesn't show up, perform any ritual using a Speed alteration glyph, Speed I, Speed II, or Speed III. Then, return and talk to him.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Talk to 'No Gums' Murray and attempt to give him the restored Everlight trumpet.<ul><li>If you don't have a restored Everlight trumpet, excavate amphitheatre debris in the Everlight Dig Site to obtain an Everlight trumpet (damaged) (or obtain it from any other source) and restore it.</li></ul>",
  },
  { text = "Perform ensoul trumpet ritual at the Um ritual site." },
  { text = "Take 'No Gums' Murray's trumpet from the ritual chest." },
  { text = "Return to 'No Gums' Murray and talk to him.", actions = { Action.ConversationHighlight:new("Yes") } },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "That Old Black Magic: Skelly By Everlight",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1691366400,
  prereqQuests = { "That Old Black Magic: My One and Only Lute" },
})
