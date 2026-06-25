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
    text = "Talk to Lookout Ekahi beside your ship in either Waiko or Aminishi.",
    title = "Lookout Ekahi",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new(
        "Jed Hunter miniquest (if you have just completed the [[Head of the Family (miniquest)|Head of the Family miniquest]] then you may need to relog for this option to appear)."
      ),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes, I promise.") },
  },
  {
    text = "Talk to Bosun Higgs beside your ship.",
    title = "Bosun Higgs",
    neededItems = { ["Uncharted island map"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest.") },
  },
  { text = "Talk to Sojobo in the centre of Waiko marketplace." },
  { text = "Kill 3 Gu ronin on uncharted isles." },
  { text = "Return to Sojobo on Waiko." },
  {
    text = "Return to Bosun Higgs with the Tetsu set.",
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest.") },
  },
  {
    text = "Talk to Navigator Jemi beside your ship.",
    title = "Navigator Jemi",
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest.") },
  },
  {
    text = "Talk to Sea Witch Kaula on the north-western shore of Whale's Maw.<ul><li>You may have to speak to her twice to give her the tortle shell bowls.</li></ul>",
    actions = { Action.ConversationHighlight:new("Ask about communicating with the Thalassus.") },
  },
  { text = "Talk to Navigator Jemi while standing next to Kaula." },
  {
    text = "Go back to the ship and talk to Navigator Jemi.",
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest.") },
  },
  {
    text = "Talk to Quartermaster Gully beside your ship.",
    title = "Quartermaster Gully",
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest.") },
  },
  { text = "Talk to Rosie in the centre of Waiko." },
  { text = "Talk to Rosie again." },
  {
    text = "Deliver the buoyancy materials back to Quartermaster Gully.",
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest.") },
  },
  {
    text = "Talk to Lookout Ekahi.",
    title = "Finale",
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest.") },
  },
  {
    text = "Talk to Yuehanxun the Dealer in the south-east of Waiko. She is at the last hut in the South of Waiko, north of the water barrel.",
  },
  {
    text = "Talk to Lookout Ekahi at the boat to set sail.",
    actions = { Action.ConversationHighlight:new("Jed Hunter miniquest."), Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Once on the ship, talk to Lookout Ekahi again.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Once on the other ship, unlock the hold in the centre of the ship.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
}

return Quest:new({
  name = "Jed Hunter (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1469404800,
  prereqQuests = {
    "Impressing the Locals",
    "Flag Fall (miniquest)",
    "Head of the Family (miniquest)",
    "Spiritual Enlightenment (miniquest)",
    "Deadliest Catch",
  },
})
