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
    text = "Talk to Lex on the west side of the Hair of the Dog tavern, south of the Canifis lodestone.",
    title = "Off to Lex",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "This is still a WIP. It is recommended to use the wiki for this step.", title = "Locations" },
  { text = "Squeeze all 13 plushies." },
  {
    text = "Teleport back to Canifis and talk to Lex.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new("(If the player still has plushies left to find:)(Continues above.)"),
    },
  },
}

return Quest:new({
  name = "The Lost Toys (miniquest)",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1460332800,
  prereqQuests = { "The Branches of Darkmeyer" },
})
