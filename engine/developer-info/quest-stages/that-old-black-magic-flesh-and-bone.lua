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
    text = "Talk to Zommy 'Brains' Walker (red pin on map to the right) at the centre of the City of Um, behind Death, to accept the subquest.",
    title = "Delivering the keys",
    neededItems = { ["Flesh rune"] = { quantity = 360 }, ["Bone rune"] = { quantity = 520 } },
    recommendedItems = {},
  },
  {
    text = "Head north-east to talk to the Former Master Crafter (blue pin on map to the right).",
    actions = { Action.ConversationHighlight:new("Ask about piano keys.") },
  },
  {
    text = "Talk to him to give him 520 bone runes and 360 flesh runes.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Take the white and black piano keys from the Former Master Crafter and return to Zommy." },
  { text = "Give Zommy the piano keys.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "That Old Black Magic: Flesh and Bone",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1691366400,
  prereqQuests = { "That Old Black Magic: My One and Only Lute" },
})
