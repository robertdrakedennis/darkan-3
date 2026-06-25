local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")

---@type QuestStep[]
local steps = {
  {
    text = "Enter the ruins beneath the Wizards' Tower.",
    title = "Getting started",
    neededItems = {},
    recommendedItems = {},
  },
  {
    text = "Pass through the two doors in the ruins and talk to Ariane.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("What do you need help with?"),
      Action.ConversationHighlight:new("I'd be happy to help."),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("How can I get you out?"),
      Action.ConversationHighlight:new("Okay, what do you want me to do?"),
    },
  },
  { text = "Use the prepared fire rune on a red vortex and the prepared air rune on a grey vortex." },
  { text = "Talk to Ariane.", actions = { Action.ConversationHighlight:new("OK, I'll do that now.") } },
  { text = "Head through the eastern library door.", title = "Memories", neededItems = {}, recommendedItems = {} },
  { text = "Put the glowing fire rune on the red chair and the glowing air rune on the grey chair." },
  { text = "Head west back through the door." },
  {
    text = "Talk to Ariane.",
    actions = {
      Action.ConversationHighlight:new("I saw them talking about the 'teleportation problem'."),
      Action.ConversationHighlight:new("So, what now?"),
    },
  },
  {
    text = "Use each prepared rune on a vortex corresponding to the colour of the background of the rune.",
    title = "The meeting of the head wizards",
    neededItems = {},
    recommendedItems = {},
  },
  { text = "Talk to Ariane.", actions = { Action.ConversationHighlight:new("OK, I'll do that now.") } },
  {
    text = "Head west and climb up the staircase to go back to the room with the rune guardian.<ul><li>Use the glowing cosmic rune with the Grey Order statue (north-west).</li><li>Use the glowing nature rune with the Green Order statue (north-east).</li><li>Use the glowing law rune with the Blue Order statue (south-east).</li><li>Use the glowing chaos rune with the Red Order statue (south-west).</li></ul>",
  },
  {
    text = "Return to the main chamber and talk to Ariane.",
    actions = {
      Action.ConversationHighlight:new("The Green master stole the Red apprentice's idea!"),
      Action.ConversationHighlight:new("Okay, so what do we do now?"),
      Action.ConversationHighlight:new("OK."),
    },
  },
  {
    text = "Head through the northern studies door.",
    title = "Memories of the Abyss",
    neededItems = {},
    recommendedItems = {},
  },
  {
    text = "Head into the north-eastern room.<ul><li>Use the glowing chaos rune on the large chair.</li><li>Use the glowing fire rune on the middle chair.</li></ul>",
  },
  {
    text = "Return to the main chamber and talk to Ariane.",
    actions = {
      Action.ConversationHighlight:new("The Red master summoned a demon!"),
      Action.ConversationHighlight:new("So what do we do now?"),
      Action.ConversationHighlight:new("OK."),
    },
  },
  {
    text = "Head through the eastern library door.",
    title = "The teleportation debate",
    neededItems = {},
    recommendedItems = {},
  },
  {
    text = "Use Glowing Runes in the following order:<ul><li>Use the glowing air rune on the grey chair</li><li>Use the glowing fire rune on the red chair</li><li>Use the glowing earth rune on the green chair</li><li>Use the glowing water rune on the blue chair</li><li>Use the glowing chaos rune on the table.</li></ul>",
  },
  {
    text = "Return to the main chamber and talk to Ariane.",
    actions = {
      Action.ConversationHighlight:new("The wizards were about to start the ritual."),
      Action.ConversationHighlight:new("OK, what now?"),
      Action.ConversationHighlight:new("OK."),
    },
  },
  { text = "Head through the eastern library door." },
  { text = "Search the shelves for<ul><li>Document fragment (1/5)</li><li>Document fragment (3/5)</li></ul>" },
  { text = "Return to the main chamber." },
  { text = "Head through the northern studies door." },
  {
    text = "Search the crates and chests in each room for<ul><li>Document fragment (2/5)</li><li>Document fragment (4/5)</li><li>Document fragment (5/5)</li><li>Diary</li></ul>",
  },
  { text = "Once you have found all of the document fragments (5 in total) head back to the main chamber." },
  {
    text = "Talk to Ariane. (Do this before placing the runes or it will not trigger the cutscene.)",
    actions = {
      Action.ConversationHighlight:new("I found some document fragments."),
      Action.ConversationHighlight:new("I found a diary in the ruins."),
      Action.ConversationHighlight:new("Enough about the diary."),
      Action.ConversationHighlight:new("OK."),
    },
  },
  {
    text = "Place the Runes in their corresponding positions on the floor surrounding the blue beam: If the cutscene doesn't trigger, the placement is incorrect. Try picking up one of the runes and placing it down again in the same spot.<ul><li>North: Law</li><li>North-east: Fire</li><li>East: Nature</li><li>South-east: Air</li><li>South: Chaos</li><li>South-west: Water</li><li>West: Cosmic</li><li>North-west: Earth</li></ul>",
    title = "The beam",
    neededItems = {},
    recommendedItems = {},
  },
  {
    text = "Head west and climb up the staircase to go back to the surface.<ul><li>Alternately use the wicked hood teleport and descend to the bottom floor.</li></ul>",
  },
  {
    text = "Talk to Wizard Ellaron who is now at the entrance to the Wizards' Tower.",
    actions = {
      Action.ConversationHighlight:new("Ariane is hurt in the ruins of the old Tower!"),
      Action.ConversationHighlight:new("I didn't say she was in a coma."),
      Action.ConversationHighlight:new("Enough talk! Prepare to fight, Red Wizard!"),
    },
  },
  { text = "Talk to Ariane.", title = "The burning tower", neededItems = {}, recommendedItems = {} },
  {
    text = "Use the following prepared runes on Ariane corresponding to the beam Must be completed in order from top to bottom:<ul><li>Cosmic rune - Thick (spinning) grey beam</li><li>Water rune - Simple blue beam</li></ul>",
  },
  { text = "Ascend the beam<ul><li>Earth rune - Simple green beam</li><li>Fire rune - Simple red beam</li></ul>" },
  {
    text = "Ascend the beam<ul><li>Law rune - Thick (spinning) blue beam</li><li>Nature rune - Thick (spinning) green beam</li></ul>",
  },
  {
    text = "Ascend the beam<ul><li>Chaos rune - Thick (spinning) red beam</li><li>Air rune - Simple grey beam</li></ul>",
  },
  {
    text = "Talk to Ariane.",
    actions = {
      Action.ConversationHighlight:new("Are you OK?"),
      Action.ConversationHighlight:new("So... what now?"),
      Action.ConversationHighlight:new("So, is there a reward?"),
      Action.ConversationHighlight:new("We did find out what happened to the old tower."),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Rune Memories",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.long,
  releaseDate = 1354060800,
  prereqQuests = { "Rune Mysteries" },
})
