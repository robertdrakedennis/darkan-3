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
    text = "Make sure you have 5 free inventory spaces.",
    title = "Getting started",
  },
  {
    text = "Head to the cave entrance located just east of the Fishing Guild's entrance. Manor farm Teleport is a quick teleport nearby. Enter the cave and walk north-west until you find two goblin guards guarding the entrance to the temple.",
  },
  {
    text = "Drink one dose of the goblin potion and climb down the stairs to the north. Do not equip anything when in goblin form.",
  },
  {
    text = "Once in the temple, enter the crypt through the door to the north. Equip your ring of visibility and amulet of ghostspeak.<ul><li>If you have received the ability to see into the Shadow Realm from Sliske, you do not need to equip the ring to see Hopespear.</li></ul>",
  },
  {
    text = "Speak to the Ghost.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("I'm [Player name]"),
      Action.ConversationHighlight:new("Tell me about the prophecy."),
      Action.ConversationHighlight:new("Tell me more about the Chosen Commander."),
      Action.ConversationHighlight:new("Tell me about the Big High War God."),
      Action.ConversationHighlight:new("Why are you here?"),
      Action.ConversationHighlight:new("I visited Yu'Biusk."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("What is it?"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("I'll do that.") },
  },
  {
    text = "For the next part, you have to kill each priest skeleton and collect its bones. Make sure to unequip everything for the whole duration of the fight.<ul><li>At the south-west grave, say the name  Kill Snothead.</li><li>At the south-east grave, say the name  Kill Snailfeet.</li><li>At the north-west grave, say the name  Kill Mosschin.</li><li>At the north-east grave, say the name  Kill Redeyes.</li><li>At the northern grave, say the name  Kill Strongbones.</li></ul>",
    title = "Priests to kill",
    actions = {
      Action.ConversationHighlight:new("Snothead"),
      Action.ConversationHighlight:new("Snailfeet"),
      Action.ConversationHighlight:new("Mosschin"),
      Action.ConversationHighlight:new("Redeyes"),
      Action.ConversationHighlight:new("Strongbones"),
    },
  },
  { text = "Teleport out and use a fairy ring to BLQ.", title = "To Yu'biusk" },
  { text = "Bury all five newly acquired bones." },
}

return Quest:new({
  name = "Hopespear's Will (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1194825600,
  prereqQuests = {
    "Land of the Goblins",
    "A Fairy Tale I - Growing Pains",
    "A Fairy Tale II - Cure a Queen",
    "Desert Treasure",
  },
})
