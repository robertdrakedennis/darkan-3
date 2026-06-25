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
  { text = "Walk into the southern waiting lobby and complete the flashbacks for Kami, Meg, and Philipe." },
  {
    text = "Talk to Kami.",
    title = "Resolving Memories - Kami",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Relomia.",
    actions = {
      Action.ConversationHighlight:new("Assign a task to Fraji."),
      Action.ConversationHighlight:new("Fend off the Tiger's Claw!"),
    },
  },
  { text = "Assign ally to the Big door (large green door).", actions = { Action.ConversationHighlight:new("Kami.") } },
  {
    text = "Assign ally to any of the Intricate locks (blue circular matrix at the center).",
    actions = { Action.ConversationHighlight:new("Honovi.") },
  },
  {
    text = "Assign ally to the Strange symbols (the large golden gong to the east).",
    actions = { Action.ConversationHighlight:new("Sojobo.") },
  },
  { text = "Talk to Relomia", actions = { Action.ConversationHighlight:new("Play back memory.") } },
  { text = "Talk to Kami after the cutscene." },
  { text = "Talk to Meg.", title = "Meg", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Talk to Relomia.",
    actions = {
      Action.ConversationHighlight:new("Assign a task to the Raptor."),
      Action.ConversationHighlight:new("Delay Sutcliffe's return!"),
    },
  },
  { text = "Assign ally to the front door of the castle.", actions = { Action.ConversationHighlight:new("Meg.") } },
  {
    text = "Assign ally to the Black Knight patrol (he stands west of the start of the bridge).",
    actions = { Action.ConversationHighlight:new("Eva Cashien.") },
  },
  {
    text = "Assign ally to the Roof access (the step-ladder east of the start of the bridge).",
    actions = { Action.ConversationHighlight:new("Captain Higgs.") },
  },
  { text = "Talk to Relomia.", actions = { Action.ConversationHighlight:new("Play back memory.") } },
  { text = "Talk to Meg after the cutscene." },
  { text = "Talk to Philipe Carnillean.", title = "Philipe", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Talk to Relomia.",
    actions = {
      Action.ConversationHighlight:new("Assign a task to Oreb."),
      Action.ConversationHighlight:new("Taunt the Bright Inquisitor!"),
    },
  },
  { text = "Assign ally to the World Gate controls.", actions = { Action.ConversationHighlight:new("Philipe.") } },
  {
    text = "Assign ally to the Defender position.",
    actions = {
      Action.ConversationHighlight:new("Commander Zilyana."),
    },
  },
  { text = "Assign ally to the Bright Inquisitor.", actions = { Action.ConversationHighlight:new("Nymora.") } },
  { text = "Talk to Relomia.", actions = { Action.ConversationHighlight:new("Play back memory.") } },
  { text = "Talk to Philipe after the cutscene." },
  { text = "Talk to Closure twice.", title = "Wrapping up" },
}

return Quest:new({
  name = "Once Upon a Time in Gielinor: Finale",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1637539200,
  prereqQuests = {
    "Once Upon a Time in Gielinor: Fortunes",
    "The Death of Chivalry",
    "Carnillean Rising",
    "Impressing the Locals",
  },
})
