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
    text = "Talk to Postie Pete-za by the entrance to Varrock Sewers with at least 1 free inventory space.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Open the manhole and enter the Varrock Sewers." },
  { text = "Go east and squeeze through the pipe.", title = "Tortle time" },
  {
    text = "Talk to Wolfgang on the bridge.",
    actions = { Action.ConversationHighlight:new("What will you do now?"), Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Talk to the Varrock estate agent.  He is located in a building just north of the sewer entrance.",
    actions = { Action.ConversationHighlight:new("Tortle Combat.") },
  },
  {
    text = "Return to Wolfgang.",
    actions = { Action.ConversationHighlight:new("You should probably stop sending rats to the estate agent.") },
  },
  {
    text = "Build the equipment around the area with the 16 planks, 2 buckets of sand, 1 empty sack, 3 ropes, and 60 iron nails.",
  },
  {
    text = "Talk to Wolfgang.",
    actions = {
      Action.ConversationHighlight:new("I have constructed all your equipment!"),
    },
  },
  { text = "Talk to Gustav to the north of Wolfgang." },
  {
    text = "Talk to Granny Potterington at Manor Farm with a kebab and Dense honeycomb in your inventory.",
    actions = { Action.ConversationHighlight:new("[Ask about Tortle Combat.]") },
  },
  { text = "Use the extremely dense honeycomb on your adult or elder common brown rabbit." },
  { text = "Bring Gustav the soft clay, 2 law runes and the plump rabbit." },
  { text = "Talk to Edvard to the south." },
  { text = "Talk to Wolfgang." },
  { text = "Talk to Johann to the southwest." },
  {
    text = "Talk to Hofuthand at the west side of Grand Exchange.",
    actions = { Action.ConversationHighlight:new("[Tortle Combat.]") },
  },
  { text = "Return to Johann." },
  {
    text = "You will then need to give each tortle its correct weapon.<ul><li>Gustav.</li><li>Johann.</li><li>'Weird' Alfred. (northeast)</li><li>Wolfgang.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Give Gustav a weapon"),
      Action.ConversationHighlight:new("Dwarven army axe"),
      Action.ConversationHighlight:new("Give Johann a weapon"),
      Action.ConversationHighlight:new("Old rake handle"),
      Action.ConversationHighlight:new("Give 'Weird' Alfred a weapon"),
      Action.ConversationHighlight:new("Rabid chinchompa"),
      Action.ConversationHighlight:new("Give Wolfgang a weapon"),
      Action.ConversationHighlight:new("Mismatched set of cutlery"),
    },
  },
  { text = "Talk to Wolfgang, wait for the cutscene, and then talk with him again" },
}

return Quest:new({
  name = "Tortle Combat (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1612137600,
  prereqQuests = { "Player-owned farm tutorial" },
})
