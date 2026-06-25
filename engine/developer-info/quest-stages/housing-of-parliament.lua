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
    text = "Talk to Deryn in the owlery, north of the Haunt on the Hill in the City of Um.",
    title = "A lot of mess",
    neededItems = { ["Bucket of water"] = { quantity = 1 }, ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Take from the cleaning supplies (in the western corner of the owlery) a cleaning cloth.",
    actions = { Action.ConversationHighlight:new("Cleaning cloth.") },
    postconditions = {
      Condition.ConversationText:new("You take a cleaning cloth from the stack of cleaning supplies."),
    },
  },
  {
    text = "Take from the cleaning supplies a scrubby brush.",
    actions = { Action.ConversationHighlight:new("Brush.") },
    postconditions = { Condition.ConversationText:new("You take a scrubby brush from the stack of cleaning supplies.") },
  },
  {
    text = "Clean up the three spots.<ul><li>Clean up the owl droppings (either inside or outside on the ground).</li><li>Slash the cobweb (near the cleaning supplies).</li><li>Brush away the dirt pile (near the southern corner on the ground).</li></ul>",
  },
  {
    text = "Talk to Deryn.",
    title = "Building the troughs",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Take 6 nails from the toolbox located under the ladder." },
  { text = "Build both piles of planks; one is inside the owlery and the other side of the same wall, outside." },
  {
    text = "Talk to Deryn.",
    title = "Water trough for a nice bath",
    neededItems = { ["Bucket of water"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Fill the empty trough outside." },
  { text = "Talk to Deryn.", title = "Consulting professionals" },
  {
    text = "Talk to either the pet shop owner in Taverley or the pet shop owner in Yanille.<ul><li>Buy some beetle bits from the Pet Shop if you didn't get some beforehand.</li></ul>",
  },
  {
    text = "Talk to Deryn.",
    title = "Taste testing",
    neededItems = {
      ["King worm"] = { quantity = 1 },
      ["Beetle bits"] = { quantity = 1 },
      ["Snail meat"] = { quantity = 1 },
      ["Ghostly sole"] = { quantity = 1 },
      ["Raw ghostly sole"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Go to the City of Um clock tower, located east of Soul Supplies, and climb up the stairs in the eastern room.",
    title = "Some appetising sprinkles",
    neededItems = { ["Snail meat"] = { quantity = 1 }, ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Take from the crate of hourglasses." },
  { text = "Smash the hourglass." },
  { text = "Use the hourglass 'sprinkles' on a snail meat for a snail with hourglass 'sprinkles'." },
  {
    text = "Return to the owlery and fill the empty trough inside.",
    title = "One last thing",
    neededItems = { ["Snail with hourglass 'sprinkles'"] = { quantity = 1 }, ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Talk to Deryn to complete the quest." },
}

return Quest:new({
  name = "Housing of Parliament",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1707696000,
  prereqQuests = { "Necromancy!" },
})
