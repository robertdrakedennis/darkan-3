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
    text = "Talk to Relomia at the Blue Moon Inn in Varrock.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Take a drink from the party table.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Drink the punch and wear the party hat." },
  {
    text = "Relomia will then tell stories of your adventures. In order to end the dialogue and progress choose the correct statement about the quests.",
  },
  {
    text = "Talk to Relomia again.",
    actions = {
      Action.ConversationHighlight:new("It was a wooden stake"),
      Action.ConversationHighlight:new("It wasn't called Blacklight!"),
      Action.ConversationHighlight:new("It had one head, not three!"),
    },
  },
  { text = "Continue the dialogue with Death." },
  { text = "Unequip the party hat as this will save time by skipping some dialogue.", title = "Closure" },
  {
    text = "Enter the Closure's Study, the door to the east of Death's office.<ul><li>If you cannot interact with the door, go back to the lobby and log back in.</li></ul>",
  },
  {
    text = "Talk to Delrith.",
    actions = {
      Action.ConversationHighlight:new("Carlem."),
      Action.ConversationHighlight:new("Aber."),
      Action.ConversationHighlight:new("Camerinthum."),
      Action.ConversationHighlight:new("Purchai."),
      Action.ConversationHighlight:new("Gabindo."),
    },
  },
  { text = "Talk to Count Draynor." },
  { text = "Talk to Elvarg for the Last Will and Testament." },
  {
    text = "Head to the kitchen in the northwest corner of the ground floor[UK]1st floor[US] of Draynor Manor and turn off the range.",
  },
  {
    text = "Talk to banker in the Varrock Grand Exchange or a bank (for example Burthorpe bank, or Draynor bank, except for the one in Max Guild) to receive Elvarg's treasure hoard from the  Last Will and Testament.",
  },
  { text = "Return to the Closure's Study in Death's office." },
  { text = "Talk to Elvarg.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to Count Draynor." },
  { text = "Talk to Closure to the north." },
  {
    text = "Re-equip the party hat and speak to Relomia in the Blue Moon Inn.<ul><li>The party hat can be taken from the table again in case you destroyed it.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Hello, [player name]."),
      Action.ConversationHighlight:new("I'm [player name]'s biggest fan!"),
      Action.ConversationHighlight:new("Can I get your autograph?"),
    },
  },
  { text = "Return to Closure." },
}

return Quest:new({
  name = "Once Upon a Time in Gielinor: Foreshadowing",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1611532800,
  prereqQuests = { "Demon Slayer", "Dragon Slayer", "Vampyre Slayer" },
})
