local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Maggie who is located just west of the Draynor lodestone.",
    title = "Getting started",
    neededItems = {},
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Oo, something smells rather...interesting!"),
      Action.ConversationHighlight:new("Is there any way I could help?"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Sure, I can do that for you.") },
  },
  {
    text = "Talk to Betty in furthest north-western house in Port Sarim.",
    title = "The puzzles - Betty",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Talk to Betty about Swept Away.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Enter the trapdoor in the south-east corner of the house." },
  {
    text = "Talk to Lottie.",
    actions = { Action.ConversationHighlight:new("I need to retrieve Betty's wand.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Complete the puzzle.<ul><li>Put the blackbird in the holding pen.</li><li>Put the rat in the blackbird pen.</li><li>Put the spider in the spiders pen.</li><li>Put the lizard in the reptiles pen.</li><li>Put the rat in the rat pen.</li><li>Put the blackbird in the blackbird pen.</li></ul>",
  },
  { text = "Open and search the chest near the ladder you came from." },
  {
    text = "Climb up the ladder and talk to Betty.",
    actions = { Action.ConversationHighlight:new("Talk to Betty about Swept Away.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Head to Rimmington (run southwest from Betty's shop in Port Sarim).", title = "Hetty" },
  { text = "Talk to Hetty.", actions = { Action.ConversationHighlight:new("Talk about Swept Away.") } },
  { text = "Enter the trapdoor just south of her house." },
  { text = "Talk to Gus.", actions = { Action.ConversationHighlight:new("It's a deal.") } },
  { text = "Take from the crate that's labelled 'Newts and Toads'." },
  { text = "Place the label of the creature you removed on that box." },
  {
    text = "If you placed the 'Newts' label previously, place the 'Toads' label on the 'Newts' crate. Otherwise place the 'Newts' label on the 'Toads' crate.",
  },
  { text = "Place the remaining label on the last box and talk to Gus." },
  { text = "Take a newt from the newt crate." },
  { text = "Talk to Hetty.", actions = { Action.ConversationHighlight:new("Talk about Swept Away.") } },
  { text = "Use the ointment on the broom." },
  {
    text = "Talk to Aggie in Draynor Village. You will get teleported to a new area.",
    title = "Aggie",
    actions = {
      Action.ConversationHighlight:new("Talk about Swept Away."),
      Action.ConversationHighlight:new("Yes, I'm ready to go now."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "Talk to Aggie again." },
  { text = "Complete the puzzle." },
  {
    text = "Talk to Maggie.",
    title = "Finishing up",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I have good news for you."),
      Action.ConversationHighlight:new("Of course I could."),
    },
    postconditions = { Condition.ConversationText:new(" Sounds easy enough.") },
  },
  { text = "Stir the cauldron with the broomstick." },
  {
    text = "Talk to Maggie.",
    actions = { Action.ConversationHighlight:new("I've stirred the cauldron for you.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Swept Away",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1225152000,
  prereqQuests = {},
})
