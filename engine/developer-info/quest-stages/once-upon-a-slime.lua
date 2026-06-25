local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Peter, next to the well in Rimmington.",
    title = "Return of the king",
    neededItems = {},
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Yeah, I know all about it"),
      Action.ConversationHighlight:new("Aye, I mean, yes."),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "After the cutscene, continue talking to Peter." },
  {
    text = "Talk to Thessalia in her clothing store in Varrock for a giant top hat.",
    title = "Top hat shenanigans",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Talk about Once Upon a Slime.") },
  },
  { text = "Talk to Peter next to the well in Rimmington." },
  {
    text = "Talk to Thessalia.  Be sure to finish the entire conversation before heading on.",
    actions = { Action.ConversationHighlight:new("Talk about Once Upon a Slime.") },
  },
  { text = "Talk to Peter again." },
  { text = "Return to Thessalia and she will direct you to Eva." },
  {
    text = "Talk to Eva inside the building south of Varrock east bank. The building is attached to Aubury's Rune Shop, and features an anvil.",
    title = "Shrinking Machine",
    neededItems = {},
    recommendedItems = {},
  },
  {
    text = "Shrink Something on the Shrinking Machine.  It will automatically put in the hat to shrink it.",
    actions = {
      Action.ConversationHighlight:new("Silver Button"),
      Action.ConversationHighlight:new("Hot"),
      Action.ConversationHighlight:new("Red Button"),
      Action.ConversationHighlight:new("Increase"),
      Action.ConversationHighlight:new("No"),
      Action.ConversationHighlight:new("KSL"),
    },
  },
  { text = "Go back to Peter and return the top hat." },
  {
    text = "After the cutscene, talk to Peter again.",
    title = "Gloop gloop gloop",
    neededItems = {},
    recommendedItems = {},
  },
  { text = "Poke the Queen Slime for pink goop." },
  { text = "Talk to Peter." },
  { text = "Use the bucket on the cauldron in Hetty's house, east of the well in Rimmington." },
  { text = "Use the bucket of purple goop on King Slime and watch the cutscene." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Once Upon a Slime",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1581292800,
  prereqQuests = {},
})
