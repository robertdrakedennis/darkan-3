local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--Objects
local spiritAltar = Model.new(1914, {
  [92] = Vertex.new(-225, 2950, 0, 127, 127, 127),
  [601] = Vertex.new(437, 2426, -329, 127, 127, 127),
  [1362] = Vertex.new(609, 2446, 328, 127, 127, 127),
  [1365] = Vertex.new(609, 2446, 328, 127, 127, 127),
  [1367] = Vertex.new(609, 2446, 328, 127, 127, 127),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Malignius Mortifer next to the ritual site.",
    title = "Walkthrough",
    actions = { Action.Direction:new(1036, 7237, 1764) },
    postconditions = { Condition.DistanceTo:new(1036, 7237, 1764, 16) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["malignius mortifer"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Perform a lesser essence ritual and collect impure essence from the ritual chest.<ul><li>Malignius Mortifer will give you eight basic ghostly ink, four regular ghostly ink, and 200 pure essence for the lesser essence ritual.</li><li>You need two Elemental I glyphs, two Change I glyphs, and four light sources.</li></ul>",
    title = "Craft impure essence",
    actions = {
      Action.ConversationHighlight:new("I'm good. What's next?"),
      Action.ModelHighlight:new(Models.objects["necromancy ritual platform"]),
      Action.ModelHighlight:new(Models.objects["necromancy ritual pedestal"]),
    },
    postconditions = { Condition.ChatText:new("You complete the ritual.") },
  },
  {
    text = "Withdraw the output of the ritual from the ritual chest on the ritual site.",
    actions = { Action.ModelHighlight:new(Models.objects["necromancy ritual chest"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["impure essence"]) },
  },
  {
    text = "Head to north-eastern area of the city and enter through the dark portal with some unnoted impure essence.",
    title = "Craft spirit runes",
    neededItems = { ["Impure essence"] = { quantity = 1, model = Models.items["impure essence"] } },
    actions = { Action.Direction:new(1164, 8453, 1820) },
    postconditions = { Condition.DistanceTo:new(1311, 2469, 1954, 8) },
  },
  {
    text = "Attempt to make some spirit runes.",
    actions = { Action.ModelHighlight:new(spiritAltar) },
    postconditions = { Condition.ConversationText:new("Stop playing with things you clearly aren't ready for.") },
  },
  {
    text = "Interact with Rasial, the First Necromancer.",
    actions = { Action.ModelHighlight:new(Models.npcs["rasial"]) },
    postconditions = { Condition.ConversationText:new("Urgh, why is he so frustrating?") },
  },
  {
    text = "Craft your runes.",
    actions = { Action.ModelHighlight:new(spiritAltar) },
    postconditions = { Condition.InventoryContains:new(Models.items["spirit rune"]) },
  },
  {
    text = "Head back to Malignius.",
    title = "Finishing up",
    actions = { Action.Direction:new(1036, 7237, 1764) },
    postconditions = { Condition.DistanceTo:new(1036, 7237, 1764, 16) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["malignius mortifer"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Rune Mythos",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1691366400,
  prereqQuests = { "Necromancy!" },
  questReqs = { Types.QuestReq.skill("Necromancy", 24) },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
