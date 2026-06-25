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
    text = "Talk to Emir Ali Mirza in Al Kharid palace to begin the quest.",
    title = "New trade deal",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about Jack of Spades.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Go upstairs. Talk to Osman.", actions = { Action.ConversationHighlight:new("No more questions.") } },
  {
    text = "From the same floor, climb the central north or northwest staircase to the roof and talk to Grand Vizier Hassan. The steps are built into the wall and may be difficult to notice at a glance. Look south west of the fountain.",
    actions = { Action.ConversationHighlight:new("I know all I need - let's go!") },
  },
  {
    text = "Dismiss any follower and unequip the item in your main and off-hand slots. Talk to Grand Vizier Hassan.",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  {
    text = "Talk to Hassan while on the carpet to teleport to Menaphos.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Follow Hassan into the city." },
  {
    text = "Talk to Hassan. Make sure you finish dialogue there is a pause somewhere.",
    title = "The streets of Menaphos",
    actions = { Action.ConversationHighlight:new("Talk about Jack of Spades.") },
  },
  { text = "Head north east to the shifting tombs entrance in the centre of the market square." },
  { text = "Choose the Travel Ports District option on the tombs to go to the Port district." },
  { text = "Talk to 'Admiral' Wadud (Ports) in The Golden Scarab Inn just north." },
  { text = "Choose the Travel Imperial District option on the tombs to the Imperial district." },
  {
    text = "Speak to Commander Akhomet (Imperial) in front of the entrance to the Great Pyramid just west. Make sure you finish dialogue there is a pause somewhere.",
  },
  { text = "Choose the Travel Worker District option on the tombs to the Worker district." },
  { text = "Talk to Batal (Worker) next to the fire pit. Make sure you finish dialogue there is a pause somewhere." },
  { text = "Enter the shifting tombs to go inside.", title = "Shifty" },
  { text = "Ensure you have 4 backpack spaces." },
  {
    text = "Talk to the Jack of Spades.",
    actions = {
      Action.ConversationHighlight:new("Why steal from people?"),
      Action.ConversationHighlight:new("I've heard enough."),
    },
  },
  { text = "Exit the shifting tombs to any district." },
  { text = "Referencing above locations, return the four stolen items to their respective districts." },
  {
    text = "Return to Hassan in the Merchant district.",
    actions = { Action.ConversationHighlight:new("Talk about Jack of Spades.") },
  },
  { text = "Quest complete!" },
  { text = "Before leaving the city, feel free to activate the Menaphos lodestone." },
}

return Quest:new({
  name = "The Jack of Spades",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1496620800,
  prereqQuests = { "Diamond in the Rough" },
})
