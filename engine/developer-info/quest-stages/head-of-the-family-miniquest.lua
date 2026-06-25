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
    text = "Talk to the Waiko moai on the hill north-east of Waiko (if you have just completed Impressing the Locals you may need to relog for the chat options to appear).",
    title = "Moai transport",
    neededItems = { ["Fish oil"] = { quantity = 1 }, ["Bamboo"] = { quantity = 1 }, ["Chimes"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Hello. Who are you?"), Action.ConversationHighlight:new("Farewell.") },
  },
  { text = "Charter a ship to Aminishi." },
  { text = "Run north along the west coast and talk to Aminishi moai." },
  {
    text = "Sail back to Waiko and talk to Rosie in the north-west section of the marketplace. Don't buy any supplies.",
  },
  {
    text = "Run south-east past the bamboo stalks and talk to Yuehanxun the Dealer. Confirm the payment of 15 fish oil.",
  },
  {
    text = "Sail to Whale's Maw and on the north-west coast, talk to Sea Witch Kaula.",
    actions = { Action.ConversationHighlight:new("Tell me of the moai that travelled on water.") },
  },
  { text = "Return to Rosie for a soil bag. Don't buy any supplies." },
  { text = "Sail to Aminishi and fill the bag on the beach." },
  { text = "Return to Rosie (with filled bag and 5 bamboo) and confirm the payment. Don't buy any supplies." },
  { text = "Sail to Aminishi, run east and talk to Cap'n Ekahi. Confirm the payment of 300 chimes." },
  { text = "Talk to Aminishi moai, north of your ship." },
  { text = "Travel by the Rowboat next to Cap'n Ekahi on Aminishi." },
  { text = "Enable deflect Melee and defeat the 3 cyclopes. There is a safespot behind the mast." },
  { text = "Return to Waiko moai where you started the quest." },
  { text = "Miniquest complete!" },
}

return Quest:new({
  name = "Head of the Family (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1469404800,
  prereqQuests = { "Impressing the Locals" },
})
