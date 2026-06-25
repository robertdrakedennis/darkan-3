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
    text = "Talk to Horacio outside Handelmort Mansion in East Ardougne and accept the quest.",
    title = "Starting out",
    neededItems = { ["Soft clay"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("So... who are you?"),
      Action.ConversationHighlight:new("Do you need any help?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Wizard Cromperty in his house to the north-east of the market.",
    actions = { Action.ConversationHighlight:new("Talk about Back to my Roots") },
    postconditions = {
      Condition.ConversationText:new(
        " Ah yes, my very latest invention, but it's still in experimental stages at the moment, and quite fragile."
      ),
    },
  },
  { text = "Talk to an RPDT employee south of the southern bank in Ardougne." },
  { text = "Open the smelly package on the table outside." },
  {
    text = "Return to Wizard Cromperty.",
    actions = { Action.ConversationHighlight:new("Talk about Back to my Roots") },
    postconditions = {
      Condition.ConversationText:new(
        " Ah yes, my very latest invention, but it's still in experimental stages at the moment, and quite fragile."
      ),
    },
  },
  {
    text = "Make a pot lid (any pot lid acquired before this point will not work). The potter's wheel and pottery oven in the north-west area of East Ardougne, Draynor Village or a portable crafter will work.",
  },
  {
    text = "Return to Wizard Cromperty.",
    actions = { Action.ConversationHighlight:new("Talk about Back to my Roots") },
    postconditions = {
      Condition.ConversationText:new(
        " Ah yes, my very latest invention, but it's still in experimental stages at the moment, and quite fragile."
      ),
    },
  },
  {
    text = "Talk to Garth, the farmer located at the fruit tree patch in Brimhaven, north of the Karamja lodestone.",
    actions = { Action.ConversationHighlight:new("Talk about the Back to my Roots quest.") },
    postconditions = { Condition.ConversationText:new(" Oo, oi. That I do.") },
  },
  {
    text = "South-east of the nature altar, enter the maze by climbing the vine (north side). Do not leave the maze until you have followed all steps in this section.<ul><li>Monsters in the maze drop body parts used in One Foot in the Grave. It is highly recommended to collect these during your time in the maze if you also plan to complete this quest.</li></ul>",
    title = "Karamja vine maze",
    neededItems = { ["Pot lid"] = { quantity = 1 }, ["Plant pot"] = { quantity = 1 }, ["Empty pot"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Navigate the maze to reach the roots using the solution provided." },
  { text = "Once you reach the root, dig the loose soil, cut the root, then use it on your plant pot." },
  {
    text = "If it does not grow, dig up and cut a different root. Out of the five roots in the area, only one of them can successfully grow. Try a different root until it works.<ul><li>Fast travelling before covering the plant will destroy the clipping.</li></ul>",
  },
  { text = "Once the correct root takes, use the potted root on your empty pot to make a sealed pot." },
  {
    text = "If you have not obtained all 6 body parts from One Foot in the Grave, it is highly recommended to do so now.",
  },
  {
    text = "Return to Horacio and speak to him.",
    title = "The battle",
    neededItems = { ["Hatchets"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Use the sealed pot on the east patch to plant the vine." },
  {
    text = "Attempt to attack the wild jade vine.",
    actions = { Action.ConversationHighlight:new("Okay, I'm ready for combat.") },
    postconditions = {
      Condition.ConversationText:new(
        " It doesn't look like you're prepared properly. You'll need to be wielding a hatchet to fight the vine!"
      ),
    },
  },
  { text = "Once killed, cut the remaining vine." },
  { text = "Talk to Horacio." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Back to my Roots",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1191888000,
  prereqQuests = { "The Hand in the Sand", "Tribal Totem" },
})
