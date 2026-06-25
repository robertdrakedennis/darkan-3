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
    text = "Go to the Outpost.",
    title = "Starting out",
  },
  {
    text = "Speak to Jorral. Accept his offer to bank your items.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Anything else I can help you with?") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("I'm sure I'll enjoy finding out."),
      Action.ConversationHighlight:new("Yes"),
    },
  },
  {
    text = "Run east to the sheep pen across the river, or run north-west from Ardougne lodestone.",
    title = "The right spot",
  },
  {
    text = "Right-click rub the key in the location shown right (stand on the flowers west of the sheep pen west of the lodestone).",
  },
  { text = "Talk to Laura.", title = "The past" },
  { text = "Rub the enchanted key again." },
  { text = "Return to Jorral and talk to him." },
  {
    text = "After the cutscene, go back to the location shown above and rub the key again, choosing 'Past - A.' (Don't forget to bank your traveller's necklace if you used it to get back to the outpost.)",
  },
  { text = "While in Past A, talk to Jack.", title = "Helping Jack" },
  {
    text = "Rub the key, go to Past B, talk to Jack.",
    actions = { Action.ConversationHighlight:new("Perhaps you'd spare the time if I read your mind with magic?") },
    postconditions = {
      Condition.ConversationText:new(" Magic? Well I did have an interest in it when I was younger... Okay."),
    },
  },
  {
    text = "Answer with the following:",
    actions = {
      Action.ConversationHighlight:new("Super Jack The Fantasmic."),
      Action.ConversationHighlight:new("Cosmic, mind, body, and fire runes."),
      Action.ConversationHighlight:new("Misalionar."),
      Action.ConversationHighlight:new("Seven."),
      Action.ConversationHighlight:new("Trying to create fire."),
      Action.ConversationHighlight:new("Create chocolate cakes."),
      Action.ConversationHighlight:new("Good."),
      Action.ConversationHighlight:new("Strawberry."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " That's very impressive. If you're so good at looking into the past, then perhaps you could answer a more useful question?"
      ),
    },
  },
  { text = "Go to Past A, take a spade and 6 plant pots from the backyard." },
  { text = "Use the spade or a plant pot on each seedling." },
  { text = "Use the seedling pots on the correct tree spots using this image." },
  { text = "Go to Past B, talk to Sarah about the trees." },
  { text = "Talk to Jack for a cutscene." },
  { text = "Go to Past A, talk to Roger.", title = "Healing Baby Sarah" },
  { text = "Go to Past B, talk to Sarah." },
  {
    text = "Go to Past A, search the south-east side table inside to find a pestle and mortar, bucket of milk and a clean guam.",
  },
  { text = "Search the the middle shelf (south side) for a bowl." },
  { text = "Use the bowl on the beehive just west of the house." },
  { text = "Use the pestle and mortar on the clean guam." },
  { text = "Use the bucket of milk on the bowl of honey." },
  { text = "Add the ground guam." },
  {
    text = "Use the bowl of milk, honey and guam on Baby Sarah (right click so that you don't use the bowl on the basket).",
  },
  { text = "Go to Past B, talk to Laura.", title = "A map for Laura" },
  {
    text = "Go to Past A, search either the south-western or south-eastern shelves in the house for a papyrus and charcoal.",
  },
  { text = "Use the charcoal on the papyrus." },
  { text = "Give this to Laura." },
  { text = "Go to Past B, talk to Laura." },
  { text = "Go to Past A.", title = "Laura's brooch" },
  { text = "Inside the house, search the 3 stone bedside tables (One of them should contain the brooch)" },
  { text = "Use the brooch on the soil mound west of the shed Laura is building." },
  { text = "Go to Past B, take the spade from the shed." },
  { text = "Dig up the brooch." },
  { text = "Talk to Laura for a cutscene." },
  { text = "Return to the present.", title = "Finishing up" },
  { text = "Talk to Jorral." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Meeting History",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1217894400,
  prereqQuests = { "Making History" },
})
