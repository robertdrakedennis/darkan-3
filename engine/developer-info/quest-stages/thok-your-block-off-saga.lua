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
    text = "Find the rock and twine bundle while raiding the 36-40 occult floors of Daemonheim, and recover memory of it.",
    title = "Beginning of the story",
  },
  {
    text = "Talk to Skaldrun and ask him to tell you a story.<ul><li>You need to bank everything, including the ring of kinship.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Forgot what he says here"),
      Action.ConversationHighlight:new("Tell me a story"),
    },
  },
  {
    text = "Kill all the enemies in the first room and finish dialogue with Marmaros.",
    title = "Starting room",
  },
  { text = "Enter the boss door to the west." },
  { text = "Watch the cutscene and climb down the ladder to the west." },
  {
    text = "Head north and clear more enemies than Marmaros by clearing out the rooms as fast as possible, you are required to have at least 4 more kills than Marmaros at the end of the dungeon for unabridged tome. Continue this throughout the entire dungeon (legacy mode greatly increases hits). Auto-retaliate helps too.",
    title = "Venturing through the dungeon",
  },
  {
    text = "Head west, then west (reinforcement room) and pick up the Orange thing, west and enter the Orange shield door.",
  },
  {
    text = "Pick Frilly plants to the north of the room and mine Crumbly stuff just west of that. Make stuff on the Spinny stuff-maker south-east of the room to make the Best Thing Ever.",
  },
  {
    text = "Head east, north, east (reinforcement room) and pick up the Blue brick, unlock and proceed through the Blue rectangle room.",
  },
  { text = "Enter the Blue rectangle room again to give Pretty Lass the Best thing ever." },
  {
    text = "Continue north then west and fish the bubbling water to find Mini-marm and Mrs mini-marm. This equips them as weapons which increases damage dealt.",
  },
  {
    text = "Continue north, then east (reinforcement room), then east again, and then south.<ul><li>DO NOT pick up the Red pointy stone.</li></ul>",
    actions = { Action.ConversationHighlight:new("Let's go!") },
  },
  {
    text = "Attack  and kill the Gluttonous behemoth<ul><li>Note: there is a common glitch, where the behemoth is stuck on 1HP and you cannot finish it off. To fix this, you can relog by quitting the RS client and logging back in. You will spawn outside, but you can continue from last checkpoint, which is right before the bossfight.</li><li>Allow the gluttonous behemoth access to the other bosses as it will heal from, and subsequently kill, the Grave Creeper and Hobgoblin Geomancer.  Once they die the 1hp can be finally cleared to finish the fight.</li></ul>",
  },
  { text = "Head west and watch the cutscene." },
}

return Quest:new({
  name = "Thok Your Block Off (saga)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1335225600,
  prereqQuests = { "Skaldrun", "Thok It To 'Em (saga)" },
})
