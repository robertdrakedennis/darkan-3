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
    text = "Talk to Orlando Smith in the New Varrock Museum.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Farewell.") },
  },
  { text = "Speak to Charlie the Tramp (Chat 1•1)", title = "Black Arm Gang" },
  { text = "Speak to Katrine in the Black Arm Gang Hideout. (Chat 1•1•2•4)" },
  { text = "Talk to Jonny the Beard in the Blue Moon Inn." },
  {
    text = "Talk again and defeat Jonny the Beard.<ul><li>If he disappears when you start fighting him, be sure to have auto retaliate on. You can reset the fight by walking upstairs and talking to him again.</li></ul>",
  },
  { text = "Take the black arm to Katrine (Chat 1•1•4)<ul><li>Join the Black Arm Gang.</li></ul>" },
  { text = "Talk to Moira in the back room until you get the documents." },
  { text = "Talk to Baraek in New Varrock square. (Chat 1•1•4)", title = "Phoenix Gang" },
  { text = "Talk to Straven near the south-eastern wall of New Varrock. (Chat 1•2•1)" },
  { text = "Talk to Lowe in his archery emporium." },
  { text = "Go upstairs and take the moving egg by walking around the normal eggs." },
  { text = "Talk to Straven. (Chat 1•3•1)" },
  { text = "Join Phoenix Gang. (Chat 1)" },
  { text = "Open the door to Molly's room using the pass phrase." },
  { text = "Talk to Molly in the next room until you get the documents." },
  { text = "Head to the West New Varrock Bank.", title = "Finishing up" },
  {
    text = "Talk to Skeleton Clerk.",
    actions = {
      Action.ConversationHighlight:new("I'm here for the Shield of Arrav."),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Head to the East New Varrock Bank and go upstairs." },
  {
    text = "Talk to the Skeleton Clerk.",
    actions = {
      Action.ConversationHighlight:new("I'm here for the Shield of Arrav."),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Use the 2 shield pieces together to make the Shield of Arrav." },
  { text = "Talk to Orlando Smith at the New Varrock Museum." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Dimension of Disaster: Shield of Arrav",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1427068800,
  prereqQuests = { "Dimension of Disaster: Coin of the Realm", "Shield of Arrav" },
})
