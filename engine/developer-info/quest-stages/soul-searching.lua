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
    text = "Talk to Trista on the dock north of Soul Supplies in the City of Um.",
    title = "Suspicious Stranger",
  },
  { text = "Choose the 'Eavesdrop' option on Trista.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Death in the City of Um.",
    title = "Death",
    actions = { Action.ConversationHighlight:new("Talk about 'Soul Searching'") },
    postconditions = { Condition.ConversationText:new(" I'm working on it.") },
  },
  {
    text = "Talk to the Suspicious Stranger on the dock where Trista was earlier.",
    actions = {
      Action.ConversationHighlight:new("Convince"),
      Action.ConversationHighlight:new("Persuade."),
      Action.ConversationHighlight:new("Threaten."),
    },
    postconditions = { Condition.ConversationText:new(" Right then, what are you going to do? Kill me?") },
  },
  {
    text = "Talk to Death in the City of Um.",
    actions = { Action.ConversationHighlight:new("Talk about 'Soul Searching'") },
    postconditions = { Condition.ConversationText:new(" I'm working on it.") },
  },
  {
    text = "Talk to Malignius Mortifer at the Um ritual site with the required items in your backpack.",
    title = "Malignius",
    neededItems = {
      ["Cadava berries"] = { quantity = 1 },
      ["Fishing bait"] = { quantity = 1 },
      ["Antipoison"] = { quantity = 1 },
      ["Charcoal"] = { quantity = 1 },
      ["Papyrus"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Quests."),
      Action.ConversationHighlight:new("Soul Searching."),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Return to the dock with the Suspicious Stranger." },
  {
    text = "Drink the potion of zombification at least 8 paces away from the Suspicious Stranger but still closer than the bottom of the stairs to transmogrify into a zombie.",
    title = "Zombification",
    neededItems = { ["Potion of zombification"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Talk to the Suspicious Stranger." },
  { text = "Board the gondola." },
  {
    text = "Continue dialogue with Icthlarin.",
    actions = { Action.ConversationHighlight:new("What now? (Continue Quest)") },
    postconditions = { Condition.ConversationText:new(" I ask that very same question Player.") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Soul Searching",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1721606400,
  prereqQuests = { "Necromancy!" },
})
