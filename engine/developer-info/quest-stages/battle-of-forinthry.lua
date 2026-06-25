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
    text = "Talk to the Raptor in Fort Forinthry.",
    title = "Starting off",
    neededItems = { ["Beast of Burden"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about 'Battle of Forinthry'.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes, I am ready.") },
  },
  { text = "Watch the cutscene." },
  { text = "Begin Encounter with Fort Forinthry Gate." },
  { text = "Run north to the camp with an undead giant.", title = "The battle" },
  { text = "Kill the undead giant. The other enemies are optional." },
  { text = "Disrupt the place of power." },
  { text = "Head west." },
  { text = "Kill the undead giant. The other enemies are optional." },
  { text = "Disrupt the place of power." },
  { text = "Head south." },
  { text = "Kill the undead giant. The other enemies are optional." },
  { text = "Disrupt the place of power." },
  {
    text = "Defeat the Zemouregal & Vorkath encounter.<ul><li>Pray Protect from/Deflect Necromancy.</li><li>Drink 1 dose of super antifire.</li><li>First use of extra action Ballista after Vorkath lands.</li><li>Dive, Surge, or Escape during Vorkath's AoE (10x10 area).</li><li>Second use of extra action Ballista when the Raptor mentions 'Ready the Balista'</li><li>Use Balista as often as possible after this.</li><li>Target Zemouregal only after killing Vorkath.</li><li>Use Balista as often as possible after this.</li></ul>",
  },
  { text = "Talk to the Raptor, near the Guardhouse.", title = "Wrapping up" },
  { text = "Talk to guard captain Sofía or Overseer Siv in the Guardhouse." },
  { text = "Talk to Aster or Bill in the Command Centre." },
  { text = "Talk to Oak or Zoe east of the Command Centre." },
  { text = "Talk to Copperpot, Father Flint or Granny Rowan in the Chapel." },
  { text = "Talk to Princess or Rodney in the Kitchen." },
  { text = "Talk to the Raptor, near the Guardhouse." },
  { text = "Talk to Bill in the Town Hall." },
  { text = "Head east of the Kitchen and collect 4 stone wall segments from the pile of stone." },
  { text = "Fully repair the fortifications with the 4 repair hotspots to the west, north of the broken Workshop." },
  {
    text = "Talk to Aster, Overseer Siv, or Rodney in the Town Hall.",
    actions = { Action.ConversationHighlight:new("Yes, begin the feast.") },
    postconditions = {
      Condition.ConversationText:new(
        "The screen fades out and back in. The feast has begun, with everyone surrounding the table."
      ),
    },
  },
  { text = "Talk to the Raptor, near the Guardhouse.", title = "The aftermath" },
  { text = "Enter floor hatch to the east of the Command Centre." },
  { text = "Talk to Zemouregal in his prison cell.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Battle of Forinthry",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1700438400,
  prereqQuests = { "Ancient Awakening", "Grove", "Botanist's Workbench" },
})
