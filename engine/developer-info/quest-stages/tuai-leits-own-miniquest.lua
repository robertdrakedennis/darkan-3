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
    text = "Talk to Navigator Jemi (if you have just completed the Harbinger miniquest you may need to relog for the chat option to show up).",
    title = "Rumberry elixir",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Tuai Leit's Own miniquest.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Travel to Tuai Leit." },
  { text = "Talk to Sensei Hakase in the middle of the island." },
  { text = "If you don't have them already, pick 30 rumberries at the south-west farming spot." },
  { text = "Talk twice to Cheri the gnome on the south coast." },
  {
    text = "Take the rumberry elixir to Lookout Ekahi.<ul><li>Do not exit the dialogue or you will have to get another elixir.</li></ul>",
    actions = { Action.ConversationHighlight:new("Tuai Leit's Own miniquest.") },
  },
  { text = "Return to Sensei Hakase." },
  { text = "Chop down the inferior bamboo to the west of the island to reach Tuai Leit moai." },
  {
    text = "Talk to Tuai Leit moai.",
    actions = { Action.ConversationHighlight:new("I seek a cure for a spiritual affliction.") },
  },
  {
    text = "If you don't have them already, harvest 30 positive energy from Cyclosis island.",
    title = "Positive energy",
    neededItems = { ["Spirit dragon charm"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Travel to Aminishi with some spirit dragon charms." },
  {
    text = "Enter the spirit realm via The Well of Spirits in the easternmost part of the island near the Sakadagami.",
  },
  { text = "Talk twice to Yulong in the spirit realm, on the south beach." },
  {
    text = "Hand him the positive energy to trigger a cutscene with a white flash.<ul><li>Finish dialogue or you have to give positive energy again.</li></ul>",
  },
  { text = "Leave the spirit realm. You can quickly leave by exiting to lobby or by logging out then back in." },
  { text = "Talk to Lookout Ekahi.", actions = { Action.ConversationHighlight:new("Tuai Leit's Own miniquest.") } },
  { text = "Talk to Sensei Hakase in Tuai Leit." },
  { text = "Talk to Sensei Seaworth nearby." },
  { text = "Talk to Sojobo in the Waiko marketplace." },
  { text = "Talk to Navigator Jemi.", actions = { Action.ConversationHighlight:new("Tuai Leit's Own miniquest.") } },
}

return Quest:new({
  name = "Tuai Leit's Own (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1476057600,
  prereqQuests = { "Impressing the Locals", "Harbinger (miniquest)" },
})
