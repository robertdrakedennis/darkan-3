local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--#region NPCs
--#endregion
--#region Objects
--#endregion
--#region Items
--#endregion
--#region Quest Items
local eyeOfNewt = Model.new(180, {
  [170] = Vertex.new(-20, 52, -8, 0, 0, 0),
  [171] = Vertex.new(-24, 52, 0, 0, 0, 0),
  [173] = Vertex.new(-8, 56, -8, 0, 0, 0),
  [174] = Vertex.new(-20, 52, -8, 0, 0, 0),
  [179] = Vertex.new(-12, 52, 8, 0, 0, 0),
})
local greenmansAle = Model.new(306, {
  [237] = Vertex.new(48, 80, 0, 131, 100, 40),
  [266] = Vertex.new(48, 72, 16, 131, 100, 40),
  [270] = Vertex.new(48, 72, 16, 131, 100, 40),
  [287] = Vertex.new(48, 72, 16, 131, 100, 40),
  [305] = Vertex.new(48, 80, 0, 131, 100, 40),
})
local rottenTomato = Model.new(186, {
  [2] = Vertex.new(36, 56, 24, 100, 95, 52),
  [7] = Vertex.new(-8, 64, -24, 100, 95, 52),
  [11] = Vertex.new(0, 56, -44, 100, 95, 52),
  [12] = Vertex.new(-8, 64, -24, 100, 95, 52),
  [17] = Vertex.new(-40, 56, 16, 100, 95, 52),
})
local ashes = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 148, 136, 136),
  [34] = Vertex.new(100, 0, 60, 148, 136, 136),
  [42] = Vertex.new(100, 0, 60, 148, 136, 136),
  [64] = Vertex.new(132, 0, -24, 148, 136, 136),
  [72] = Vertex.new(132, 0, -24, 148, 136, 136),
})
local fruitBlast = Model.multi({
  Model.new(465, {
    [89] = Vertex.new(64, 168, -8, 61, 16, 12),
    [92] = Vertex.new(64, 160, -12, 130, 22, 12),
    [119] = Vertex.new(64, 168, -8, 130, 22, 12),
    [173] = Vertex.new(64, 168, -8, 130, 22, 12),
    [177] = Vertex.new(64, 168, -8, 130, 22, 12),
  }),
  Model.new(132, {
    [2] = Vertex.new(16, 120, -40, 146, 146, 159, 0.9373),
    [5] = Vertex.new(-16, 120, -40, 146, 146, 159, 0.9373),
    [6] = Vertex.new(16, 120, -40, 146, 146, 159, 0.9373),
    [8] = Vertex.new(-16, 120, -40, 146, 146, 159, 0.9373),
    [11] = Vertex.new(-40, 120, -16, 146, 146, 159, 0.9373),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "It is recommended that players grow a pet kitten before they start the quest. If they don't already have an adult cat, it will take 2.5 hours for the pet kitten to grow into a cat. If done at the start of the quest, it should be fully grown and Recipe for Disaster: Freeing Evil Dave can be completed last.",
    title = "Before starting the quest",
  },
  {
    text = "Talk to the Cook in the kitchen of Lumbridge Castle.",
    title = "Another small favour",
    neededItems = {
      ["Eye of newt"] = { quantity = 1 },
      ["Greenman's ale"] = { quantity = 1 },
      ["Rotten tomato"] = { quantity = 1 },
      ["Ashes"] = { quantity = 1 },
      ["Fruit blast"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3208, 1477, 3214) },
    postconditions = {
      Condition.DistanceTo:new(3214, 1477, 3217, 2),
      Condition.DistanceTo:new(3207, 1477, 3214, 2),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["lumbridge cook"]),
      Action.ConversationHighlight:new("Do you have any other quests for me?"),
      Action.ConversationHighlight:new("I don't really care to be honest."),
      Action.ConversationHighlight:new("What seems to be the problem?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the cook.",
    actions = {
      Action.ModelHighlight:new(NPCs["lumbridge cook"]),
    },
    postconditions = { Condition.ConversationText:new("I'll go look for those for you then!") },
  },
  {
    text = "<i>Use</i> the ashes on the fruit blast. Be careful not to drink the fruit blast.",
    actions = { Action.InventoryHighlight:new(ashes), Action.InventoryHighlight:new(fruitBlast) },
    postconditions = { Condition.ConversationText:new("That looks disgusting, but it's what the cook asked for...") },
  },
  {
    text = "Give the items to the cook.<ul><li>After the quest complete popup, go through the dining hall door and complete the dialogue to unlock the rest of subquests.</li></ul>",
    actions = {
      Action.Direction:new(3207, 1477, 3217.5),
      Action.ModelHighlight:new(NPCs["lumbridge cook"]),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Recipe for Disaster: Another Cook's Quest",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1142380800,
  prereqQuests = { "Cook's Assistant" },
  questReqs = {
    Types.QuestReq.skill("Cooking", 10),
  },
  neededItems = {
    ["Eye of newt"] = { quantity = 1, model = eyeOfNewt },
    ["Greenman's ale"] = { quantity = 1, model = greenmansAle },
    ["Rotten tomato"] = { quantity = 1, model = rottenTomato },
    ["Ashes"] = { quantity = 1, model = ashes },
    ["Fruit blast"] = { quantity = 1, model = fruitBlast },
  },
})
