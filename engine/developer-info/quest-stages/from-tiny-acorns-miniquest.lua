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
    text = "Talk to Darren Lightfinger in the Thieves' Guild.",
    title = "Walkthrough",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to the market guard in Varrock Square.",
    actions = {
      Action.ConversationHighlight:new("Who's the dwarf over there?"),
      Action.ConversationHighlight:new("Do you know what he's working on?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " It's a toy dragon of some sort. I've been keeping an eye on him 'cos he's working with some seriously valuable materials, but nobody's tried anything yet."
      ),
    },
  },
  { text = "Pickpocket Urist until successful. He is the dwarf just north east of the Varrock general store." },
  { text = "Walk north of Urist and left-click Urist's talisman." },
  {
    text = "To steal from the stall, follow these steps quickly:<ul><li>Speak to Urist again.</li><li>Run to the guard and right-click to distract him.</li><li>Steal from the stall.</li></ul>",
  },
  { text = "Talk to Urist for the banker's note." },
  { text = "Go back to the Thieves' Guild and use the banker's note on Darren with the toy dragon in your inventory." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "From Tiny Acorns (miniquest)",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.short,
  releaseDate = 1272412800,
  prereqQuests = { "Buyers and Cellars" },
  questReqs = { Types.QuestReq.skill("Thieving", 24) },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
