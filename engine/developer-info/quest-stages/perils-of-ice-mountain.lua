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
    text = "Talk to Lakki the delivery dwarf, who can be found to the east of the Invention Guild, north of Falador.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("What's the matter?"),
      Action.ConversationHighlight:new("I could help you find the crate."),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Along the path west towards Falador are eight bushes you can search. Search each of them until a crate is found.",
    title = "Delivery",
  },
  {
    text = "Give the crate to Drorkar, who is in the Power Station west of the Dwarven Mines.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("Okay.") },
  },
  { text = "Head to the Edgeville Monastery (use the Edgeville lodestone to save time)." },
  {
    text = "Give the letter to Brother Bordiss in the garden.",
    actions = { Action.ConversationHighlight:new("I have a letter for you.") },
  },
  {
    text = "Talk to Brother Althric.",
    actions = {
      Action.ConversationHighlight:new("Can I help?"),
      Action.ConversationHighlight:new("I'll go and get some compost."),
    },
  },
  {
    text = "Use compost on the dying roses, then water them. If your watering can is empty, you may use the fountains in the monastery to fill it.",
  },
  {
    text = "Talk to Professor Arblenap (the gnome) half way up to the top of Ice Mountain.",
    title = "Ice Mountain",
    actions = {
      Action.ConversationHighlight:new("What are you doing?"),
      Action.ConversationHighlight:new("Can I help?"),
    },
  },
  { text = "Wield the icefiend net and catch the four baby icefiends." },
  { text = "Talk to Professor Arblenap." },
  {
    text = "Talk to the Oracle, who is just north on the mountain.",
    actions = {
      Action.ConversationHighlight:new("What happened to your tent?"),
      Action.ConversationHighlight:new("I could fix your tent for you."),
    },
  },
  {
    text = "Take the wrecked tent from the base of the mountain, south of the Black Knights' Fortress entrance and east of the Oracle. It does not appear as a red dot on the minimap.",
  },
  {
    text = "With the tent, two planks, and two nails, use the tent on the remains atop the mountain, next to the Oracle.",
  },
  {
    text = "Continue dialogue with the Oracle.",
    actions = { Action.ConversationHighlight:new("I'm going to go and do something.") },
  },
  {
    text = "Talk to Drorkar at the Power Station.",
    title = "Finishing Up",
    actions = {
      Action.ConversationHighlight:new("You've got to shut down this power station."),
      Action.ConversationHighlight:new("The Oracle predicted it would cause the end of civilisation!"),
      Action.ConversationHighlight:new("I'll go and talk to Nurmof, then."),
    },
  },
  { text = "Pickpocket Drorkar for a Dwarven key." },
  {
    text = "Go in the Dwarven Mines and talk to Nurmof in the north-western corner of the mines, at the pickaxe shop.",
    actions = {
      Action.ConversationHighlight:new("You've got to shut down the power station!"),
      Action.ConversationHighlight:new("What was this other power source?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  {
    text = "Talk to Brother Bordiss at the Edgeville Monastery.",
    actions = { Action.ConversationHighlight:new("Nurmof says you made plans for an alternative power station.") },
  },
  { text = "Head back down into the Dwarven Mines, then immediately east into a small alcove." },
  { text = "Open the small, bright chest against the north wall." },
  {
    text = "Return to the pickaxe shop and talk to Nurmof.",
    actions = {
      Action.ConversationHighlight:new("You've got to shut down the power station."),
      Action.ConversationHighlight:new("I have the plans to the alternative power station here."),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Perils of Ice Mountain",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1210118400,
  prereqQuests = {},
})
