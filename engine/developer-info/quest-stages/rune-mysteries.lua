local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Ariane in the entrance to Wizards' Tower.",
    title = "Getting started",
    neededItems = {},
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("What's happening here?"),
      Action.ConversationHighlight:new("What can I do to help?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head into the tower, pick up the Tower mindspike and attack the vortex with any spell. You must unequip any ranged, melee, or necromancy armour or you may not be able to lure the vortex.",
  },
  { text = "Lure the vortex into the energy beam." },
  {
    text = "Talk to Ariane on the bridge outside.",
    title = "The old tower",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("What do you want me to do?"),
      Action.ConversationHighlight:new("I'll get on it."),
    },
  },
  {
    text = "Ascend the beam twice, and talk to Wizard Traiborn in the northwestern room.",
    actions = {
      Action.ConversationHighlight:new("Have you seen anything unusual lately?"),
      Action.ConversationHighlight:new("In the power beam?"),
      Action.ConversationHighlight:new("Where do these things come from?"),
    },
  },
  {
    text = "Talk to Archmage Sedridor in the southern room.<ul><li>If Love Story has been completed.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Ariane says the tower is in danger."),
      Action.ConversationHighlight:new("What are you going to do?"),
      Action.ConversationHighlight:new("Goodbye."),
      Action.ConversationHighlight:new("Ariane says the tower is in danger."),
      Action.ConversationHighlight:new("What are you going to do?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  {
    text = "Talk to Wizard Ellaron in the northeastern room.",
    actions = {
      Action.ConversationHighlight:new("Ariane says the tower is in danger."),
      Action.ConversationHighlight:new("Thanks."),
    },
  },
  {
    text = "Descend the beam twice or right-click and select 'Bottom floor Beam' back to the ground floor[UK]1st floor[US].",
  },
  {
    text = "Talk to Ariane.",
    actions = {
      Action.ConversationHighlight:new("I've spoken to some of the wizards..."),
      Action.ConversationHighlight:new("Wizard Traiborn said something came through the library floor."),
      Action.ConversationHighlight:new("Then let's get down there and investigate!"),
      Action.ConversationHighlight:new("I'll get right on it."),
    },
  },
  { text = "Ascend the beam twice.", title = "The hidden key", neededItems = {}, recommendedItems = {} },
  {
    text = "Talk to Wizard Ellaron.",
    actions = {
      Action.ConversationHighlight:new("How can I get into the old tower?"),
      Action.ConversationHighlight:new("You do know, though?"),
      Action.ConversationHighlight:new("You mean the rumour about Water Surge?"),
      Action.ConversationHighlight:new("What do you mean, the key lies in understanding Water Surge?"),
      Action.ConversationHighlight:new("It's such a pity you couldn't help me, Maybe I'll visit the library."),
    },
  },
  {
    text = "Descend the beam twice or select 'bottom floor' option on the beam back to the ground floor[UK]1st floor[US].",
  },
  { text = "Search the easternmost bookcase for The Harmony of the Runes and The Runes of the Spells of Water." },
  { text = "Read the books." },
  { text = "Play the organ at the southern end of the ground floor[UK]1st floor[US]." },
  {
    text = "Press the organ keys in the following order: 10x 'B',  7x 'A#', 1x 'G#', and 1x 'G'. (small black keys are # notes)",
  },
  { text = "Take the organ key." },
  {
    text = "Head to the north side of the island and talk to Ariane,  then head east and enter the door on the seashore.",
    title = "Descent",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I've got the key to the ruins."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Ariane",
    actions = {
      Action.ConversationHighlight:new("We should keep our minds on the job."),
      Action.ConversationHighlight:new("Yes, it's inspiring."),
      Action.ConversationHighlight:new("Okay, what should we do now?"),
    },
  },
  {
    text = "Talk to the rune guardian and answer the questions any way.<ul><li>The options you pick only dictate the title you're given. See the full guide to learn more.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I'm ready to be tested."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Enter the door behind the rune guardian." },
  { text = "Walk down the stairs to the gap in front of Ariane." },
  {
    text = "Climb behind the statue (on the wall above the gap).",
    actions = {
      Action.ConversationHighlight:new("I'm making us a bridge."),
      Action.ConversationHighlight:new("Do you have a better idea?"),
    },
  },
  { text = "Cross the statue (gap) and climb down the staircase." },
  { text = "Talk to Ariane.", actions = { Action.ConversationHighlight:new("I wish I could have seen it.") } },
  {
    text = "Use magic to lure a vortex into Ariane's circle, and keep it there until the circle disappears. You can lure them by attacking them.",
  },
  {
    text = "Talk to Ariane again.",
    actions = {
      Action.ConversationHighlight:new("Never mind the history, what does that mean for us?"),
      Action.ConversationHighlight:new("Okay, so what do we do?"),
      Action.ConversationHighlight:new("Okay, I'll do that now."),
    },
  },
  { text = "Exit the ruins.", title = "Finishing up", neededItems = {}, recommendedItems = {} },
  {
    text = "Head back to the organ on the main floor of the tower and play any of the notes from the lowest set of keys to trigger several cutscenes.",
  },
  {
    text = "Talk to Ariane (east side of the island at the entrance to the lower tower).",
    actions = { Action.ConversationHighlight:new("Is there a reward?") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Rune Mysteries",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.mediumlong,
  releaseDate = 1354060800,
  prereqQuests = {},
})
