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
    text = "Talk to Closure.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to Ariane." },
  { text = "Talk to Sir Tiffy." },
  { text = "Talk to Zanik." },
  {
    text = "Talk to Closure.",
    actions = {
      Action.ConversationHighlight:new("Icthlarin"),
      Action.ConversationHighlight:new("Sliske"),
      Action.ConversationHighlight:new("Death"),
    },
  },
  {
    text = "Go into the wheat field in Draynor Village south east of the lodestone and pick up three spooky scarecrows.",
    title = "Setting up the quest",
  },
  {
    text = "Place the scarecrows at the  markers north-west of the lodestone, just behind the entrance to Death's Office by the trees. Look for the lit candles near wooden markers.  Click on the wooden markers to plant the scarecrows.",
  },
  { text = "Go back and talk to Closure again." },
  { text = "Hide the confiscated sword at the marker just west of the scarecrow markers." },
  { text = "Go back and talk to Closure again." },
  {
    text = "Go to the Dancing Donkey Inn (south east Varrock) and talk to Relomia twice.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Empty your Backpack and get rid of all your worn equipment, aura, and followers, and talk to Closure.",
    actions = { Action.ConversationHighlight:new("Yes!") },
  },
  { text = "Talk to Relomia.", title = "Relomia's quest" },
  { text = "Follow Relomia and talk to her each time she approaches the Evil Goons, and finally at the sword." },
  { text = "Equip the wand. Attempt to kill Relomia but let her defeat you." },
  { text = "Talk to Relomia." },
  { text = "Talk to Closure." },
}

return Quest:new({
  name = "Once Upon a Time in Gielinor: Fortunes",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1625443200,
  prereqQuests = {
    "Once Upon a Time in Gielinor: Flashback",
    "Missing, Presumed Death",
    "Dishonour among Thieves",
    "The Death of Chivalry",
  },
})
