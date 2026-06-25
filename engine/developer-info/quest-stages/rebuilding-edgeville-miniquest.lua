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
    text = "Start by talking to Vannaka near the lever teleport from Edgeville, for a To-Do list.",
    title = "Checklist",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Did you see what attacked Edgeville?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head to Aggie's house which is north-west of the Draynor Village bank and take the broom when Aggie is not looking at you to obtain Aggie's broom (uncharged).",
  },
  { text = "Charge the uncharged broom with 100 water runes to obtain Aggie's broom." },
  { text = "Head back to Edgeville and sweep four scorch marks." },
  {
    text = "Talk to Jeffery, who is east of the bank and near a furnace, for Jeffery's hammer.",
    actions = { Action.ConversationHighlight:new("Have you seen the damage done to Edgeville?") },
  },
  {
    text = "Fix the following buildings by getting close to the locations and selecting the first option of the hammer in your backpack. You will need to remove them from your plank box if using that to carry them.<ul><li>The house north of the bank, requiring ten mahogany planks.</li><li>The market stall north of the bank, requiring ten planks.</li><li>The broken window on the north side of the bank, requiring five planks and a unit of molten glass.</li><li>Jeffery's Armoury, east of the bank, requiring five planks.</li><li>The guard house, north-east of the bank, requiring a marble block.</li><li>The house across River Lum, between the river and Grand Exchange, requiring ten oak planks.</li></ul>",
  },
  { text = "Use five bittercap mushrooms on the fairy ring south of Quercus." },
  { text = "Clear the Destroyed Roof and Destroyed Wall just west of the fairy ring." },
  { text = "Consecrate or desecrate the burnt skeleton north of the bank." },
  { text = "Nurture four burnt trees. Any four around Edgeville work." },
  {
    text = "Go back to Vannaka for your reward.",
    actions = {
      Action.ConversationHighlight:new("Rebuilding Edgeville"),
    },
  },
  { text = "Miniquest complete!" },
}

return Quest:new({
  name = "Rebuilding Edgeville (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1523232000,
  prereqQuests = { "Ritual of the Mahjarrat", "A Fairy Tale III - Battle at Ork's Rift" },
})
