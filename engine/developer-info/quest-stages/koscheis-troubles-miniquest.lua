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
    text = "Talk to Thorvald the Warrior north of Rellekka inside Skulgrimen's Battle Gear.",
    title = "Koschei the Deathless returns",
    actions = { Action.ConversationHighlight:new("Does he need my help?") },
    postconditions = { Condition.ConversationText:new("(Continues above.)") },
  },
  { text = "Climb down the ladder." },
  {
    text = "Talk to Koschei the Deathless.",
    title = "The Axe and the tree",
    neededItems = { ["Balmung"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Let's go fight it - together!") },
  },
  {
    text = "With the Balmung in your inventory, chop down the Blossoming tree north-west of the bridge south of Rellekka.",
  },
  { text = "Take the chest from tree stump." },
  { text = "Return and talk to Koschei." },
  { text = "Open the chest." },
  { text = "Inspect the toy ship.", title = "Floating the ship" },
  { text = "Talk to Koschei." },
  { text = "Climb up the ladder and return to Rellekka." },
  { text = "Go to the well south-east of the longhall." },
  { text = "Use the toy ship on the well." },
  { text = "Return and talk to Koschei.", title = "The jewel" },
  {
    text = "Interact with the jewel.",
    actions = { Action.ConversationHighlight:new("Attempt to cut the jewel") },
    postconditions = {
      Condition.ConversationText:new(
        "(If the jewel is not blue:)You attempt to craft the jewel. It responds to your actions by turning blue."
      ),
    },
  },
  { text = "Talk to Koschei.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Miniquest complete!<ul><li>For additional completion, talk to Kharshai.</li><li>Interact blue jewel  (Requires 90 )</li><li>Talk to Kharshai.</li><li>Talk to Kharshai again.</li><li>Interact red jewel  (Requires 90 )</li><li>Talk to Kharshai.</li><li>Repeat the steps once again but make a new memory instead (blue) and the miniquest will be completed</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I want to talk about your identity crisis."),
      Action.ConversationHighlight:new("About that jewel..."),
      Action.ConversationHighlight:new("May I have a look at the jewel?"),
      Action.ConversationHighlight:new("Attempt to crush the jewel"),
      Action.ConversationHighlight:new("I want to talk about your identity crisis."),
      Action.ConversationHighlight:new("Kharshai, have a look at the jewel."),
      Action.ConversationHighlight:new("I want to talk about your identity crisis."),
      Action.ConversationHighlight:new("About that jewel..."),
      Action.ConversationHighlight:new("May I have a look at the jewel?"),
      Action.ConversationHighlight:new("Attempt to detect any curses"),
      Action.ConversationHighlight:new("I want to talk about your identity crisis."),
      Action.ConversationHighlight:new("Kharshai, have a look at the jewel."),
    },
  },
}

return Quest:new({
  name = "Koschei's Troubles (miniquest)",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.short,
  releaseDate = 1361145600,
  prereqQuests = { "Blood Runs Deep", "Ritual of the Mahjarrat" },
})
