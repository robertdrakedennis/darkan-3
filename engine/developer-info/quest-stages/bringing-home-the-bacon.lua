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
    text = "Speak to Eli in Falador Farm north of Port Sarim.",
    title = "Protecting the pork",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Eat the piece of bacon he gives you.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Make sure you have at least 3 (better 6) free inventory spaces or Eli won't be able to give you planks" },
  { text = "Speak to Eli again." },
  {
    text = "During the cutscene, avoid clicking too fast through the dialogue or you will have to restart the cutscene.",
  },
  {
    text = "Build the hotspots and keep adding bacon until Eli has escorted the pigs away (focus on the middle one and they will all swarm to it).",
  },
  {
    text = "Talk to Eli. Choose any 1 of the 3 show pig suggestions.   or   or",
    actions = {
      Action.ConversationHighlight:new("Maybe they can fight?"),
      Action.ConversationHighlight:new("Maybe they can carry an altar?"),
      Action.ConversationHighlight:new("Maybe they can carry items?"),
    },
  },
  {
    text = "If necessary, gather 5 wheat from the field south-east of the Draynor lodestone.",
    title = "Piglet payment",
  },
  {
    text = "Talk to Martin the Master Gardener in Draynor Village.  ( if Vampyre Slayer is completed)",
    actions = {
      Action.ConversationHighlight:new("Bringing Home the Bacon."),
      Action.ConversationHighlight:new("Bringing Home the Bacon."),
    },
  },
  {
    text = "Go north-west of Martin and talk to each character (accuse each to quickly find the suspect).<ul><li>Continue until you have found the suspect (usually Pickpocket Dan)</li></ul>",
    actions = { Action.ConversationHighlight:new("You're the one that picked Martin's pockets!") },
  },
  { text = "View cutscene after accusing." },
  {
    text = "Go back to Eli in the storm cellar and talk to him for construction materials.",
    title = "Adulterate the addicts",
    actions = {
      Action.ConversationHighlight:new("You broke your hammer on a hiker?"),
      Action.ConversationHighlight:new("I think I should go."),
    },
  },
  {
    text = "Build the pig machine in the north of the cellar.<ul><li>Click a second time on the pig machine again to improve it.</li></ul>",
  },
  { text = "Speak to Eli.", actions = { Action.ConversationHighlight:new("I think I should go.") } },
  {
    text = "Speak to Eli again to give him the 5 wheat you gathered.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Go outside and put bacon on the decoys then poison them with the sleeping potion." },
  { text = "Talk to Eli.", title = "Prized pig" },
  {
    text = "Pick 5 onions just south of Draynor bank. These must be picked after having talked to him; you cannot use onions you already have, even if you picked them yourself.",
  },
  { text = "Deliver the 5 onions to Eli.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Interact with pig machine." },
  { text = "Watch the cutscene about the pig." },
  { text = "Talk to Eli to receive a pig.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Take the pig to each location (a cutscene will play once you reach the correct area).<ul><li>Lumbridge Castle courtyard.</li><li>Varrock Marketplace (in the centre of the fountain).</li><li>Ardougne Marketplace.</li><li>Falador centre (just south of the north gate).</li></ul>",
  },
  { text = "Return to Eli." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Bringing Home the Bacon",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1373328000,
  prereqQuests = {},
})
