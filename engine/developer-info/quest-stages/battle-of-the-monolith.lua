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
    text = "<ul><li>Each of the battles can last around 10 minutes.</li><li>Position your allies in the positions indicated for each battle, then start the battles.</li><li>It is recommended to train skills at the bank chest during the battles to make good use of time.</li></ul>",
    title = "Starting off",
  },
  {
    text = "Talk to Ali the Wise or Wizard Trindy found north west of the mysterious monolith (Archaeology Guild).",
    title = "First battle",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Move Thaerisk Cemphier, Commander Zilyana, and Helwyr south-west of the barricade." },
  { text = "Press the start button to begin the battle." },
  { text = "Talk to Ali the Wise.", title = "Second battle" },
  { text = "Move Helwyr and Nex just inside the southern barricade." },
  { text = "Move Zilyana and Thaerisk just inside the northern barricade." },
  { text = "Start the battle." },
  {
    text = "Talk to Wizard Trindy or Ali the Wise.",
    title = "Third battle",
    actions = { Action.ConversationHighlight:new("The TokHaar?") },
  },
  {
    text = "Head to the Fight Kiln and enter it.",
    actions = { Action.ConversationHighlight:new("The Battle of the Monolith.") },
  },
  {
    text = "Return to the Archaeology Guild and talk to Ali the Wise again.",
    actions = { Action.ConversationHighlight:new("Begin the third battle.") },
  },
  { text = "Move Helwyr and Commander Zilyana just outside the northern barricade of the Gower farm." },
  { text = "Move Thaerisk and Nex just outside the southern barricade of the Gower Farm." },
  { text = "Move the 3 TokHaar just outside the Varrock east gate." },
  { text = "Start the battle." },
  { text = "Talk to Ali the Wise.", title = "Fourth Battle" },
  { text = "Move Helwyr to the southern barricade.<ul><li>Helwyr is located west of the south barriers.</li></ul>" },
  {
    text = "Move Thaerisk and Nex to the crossroads outside Varrock's east gate. (Sometimes an enemy can slip the crossroads defence and start attacking the north-western barricade.)<ul><li>Nex is located south-east of Varrock.</li></ul>",
  },
  { text = "Move Commander Zilyana to the northern barricade." },
  { text = "Start the final battle." },
  {
    text = "Talk to Ali the Wise for a cutscene.",
    title = "Aftermath",
    actions = { Action.ConversationHighlight:new("Leave."), Action.ConversationHighlight:new("Yes!") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Battle of the Monolith",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1621814400,
  prereqQuests = { "Azzanadra's Quest", "The Brink of Extinction" },
})
