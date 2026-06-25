local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- Quest Items
local driftwood = Model.new(66, {
  [6] = Vertex.new(276, 0, -225, 123, 107, 87),
  [12] = Vertex.new(-213, 0, 280, 123, 107, 87),
  [15] = Vertex.new(276, 0, -225, 102, 90, 77),
  [36] = Vertex.new(176, 34, -284, 126, 109, 89),
  [60] = Vertex.new(-269, 32, 195, 126, 109, 89),
})

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Boni at the Waiko market.<ul><li>If you have <i>just</i> completed Impressing the Locals, you may need to relog for the chat option to show up.</li><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered'.</li></ul>",
    title = "A quest for driftwood",
    actions = { Action.Direction:new(1826, 1253, 11616) },
    postconditions = { Condition.DistanceTo:new(1826, 1253, 11616, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["boni"]),
      Action.ConversationHighlight:new("How do I claim an uncharted isle?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Finish the conversation with Boni.",
    actions = { Action.Direction:new(1826, 1253, 11616) },
    postconditions = { Condition.ConversationText:new(" I'll get back to you when I have enough driftwood.") },
  },
  {
    text = "Take supplies from Rosie's crate.",
    actions = { Action.Direction:new(11813, 1253, 11611) },
    postconditions = { Condition.ChatText:new("You take some supplies from Rosie's crate.") },
  },
  {
    text = "Talk to Quartermaster Gully to charter your ship on a voyage.<ul><li>A trip to a Short Junket costs 5 supplies per voyage.</li></ul>",
    actions = { Action.Direction:new(1809, 485, 11652) },
    postconditions = { Condition.DistanceTo:new(1809, 485, 11653, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["quartermaster gully"]) },
    postconditions = { Condition.ModelVisible:new(driftwood) },
  }, --I didn't get the conversation text, and transcription doesn't have his dialogue
  {
    text = "Collect all the driftwood (if any) from the island.<br>Repeat this process until 5 driftwood has been gathered.",
    actions = { Action.ModelHighlight:new(driftwood) },
    postconditions = { Condition.InventoryContains:new(driftwood, 5) },
  },
  {
    text = "Return to Boni.",
    actions = { Action.Direction:new(1826, 1253, 11616) },
    postconditions = { Condition.DistanceTo:new(1826, 1253, 11616, 8) },
  },
  {
    actions = { Action.ConversationHighlight:new("How do I claim an uncharted isle?") },
    postconditions = {
      Condition.ChatText:new("Congratulations! You have completed: 'Flag Fall (miniquest)' - Complete this miniquest."),
      Condition.QuestComplete:new(),
    },
  },
}

return Quest:new({
  name = "Flag Fall (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1469404800,
  prereqQuests = { "Impressing the Locals" },
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
