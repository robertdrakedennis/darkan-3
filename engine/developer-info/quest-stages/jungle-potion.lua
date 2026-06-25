local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
--#endregion
--#region Objects
local mossRock = Model.new(1080, {
  [10] = Vertex.new(191, 84, 353, 127, 127, 127),
  [382] = Vertex.new(-349, -64, -331, 127, 127, 127),
  [387] = Vertex.new(-349, -64, -331, 127, 127, 127),
  [583] = Vertex.new(-349, -64, -331, 127, 127, 127),
  [972] = Vertex.new(265, 32, 366, 127, 127, 127),
})
--#endregion
--#region Items
local grimySitoFoil = Model.new(438, {
  [128] = Vertex.new(-56, 4, -24, 6, 65, 8),
  [239] = Vertex.new(-72, 4, 76, 68, 52, 21),
  [318] = Vertex.new(-32, -4, -40, 6, 65, 8),
  [386] = Vertex.new(-76, 4, 64, 6, 65, 8),
  [403] = Vertex.new(-72, 4, 76, 6, 65, 8),
})
local sitoFoil = Model.new(402, {
  [34] = Vertex.new(-76, 4, 64, 8, 88, 10),
  [106] = Vertex.new(-72, 4, 76, 8, 88, 10),
  [128] = Vertex.new(-72, 4, 76, 6, 65, 8),
  [383] = Vertex.new(-56, 4, -24, 8, 88, 10),
  [387] = Vertex.new(-32, -4, -40, 8, 88, 10),
})
local grimyVolenciaMoss = Model.new(438, {
  [128] = Vertex.new(-56, 4, -24, 6, 65, 8),
  [239] = Vertex.new(-72, 4, 76, 68, 52, 21),
  [318] = Vertex.new(-32, -4, -40, 6, 65, 8),
  [386] = Vertex.new(-76, 4, 64, 6, 65, 8),
  [403] = Vertex.new(-72, 4, 76, 6, 65, 8),
})
local volenciaMoss = Model.new(402, {
  [34] = Vertex.new(-76, 4, 64, 8, 88, 10),
  [106] = Vertex.new(-72, 4, 76, 8, 88, 10),
  [128] = Vertex.new(-72, 4, 76, 6, 65, 8),
  [383] = Vertex.new(-56, 4, -24, 8, 88, 10),
  [387] = Vertex.new(-32, -4, -40, 8, 88, 10),
})
--#endregion
--#region Quest Items
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Trufitus in his house located northeast of Tai Bwo Wannai.",
    title = "Getting started",
    actions = {
      Action.Direction:new(2809.5, 1221, 3086, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["trufitus"], { distance = 8 }),
      Action.ConversationHighlight:new("It's a nice village, but where is everyone?"),
      Action.ConversationHighlight:new("Me? How can I help?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Trufitus.",
    actions = { Action.ModelHighlight:new(Models.npcs["trufitus"]) },
    postconditions = { Condition.ConversationText:new("water kisses your feet") },
  },
  {
    text = "Search marshy jungle vines for a grimy snake weed.<ul><li>Take three if you intend to do Legends' Quest and Zogre Flesh Eaters.</li></ul>",
    title = "Snake weed",
    actions = {
      Action.Direction:new(2759, 143, 3043, { tile = true }),
      Action.Direction:new(2757, 0, 3041, { tile = true }),
      Action.Direction:new(2759, 81, 3039, { tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["grimy snake weed"]) },
  },
  {
    text = "Clean the herb.",
    actions = { Action.InventoryHighlight:new(Models.items["grimy snake weed"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["snake weed"]) },
  },
  {
    text = "Talk to Trufitus to give him a clean snake weed.",
    actions = {
      Action.Direction:new(2809.5, 1221, 3086, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["trufitus"], { distance = 8 }),
      Action.ConversationHighlight:new("Of course!"),
    },
    postconditions = { Condition.ConversationText:new("should search for it") },
  },
  {
    text = "Search some palm trees for a grimy ardrigal.<ul><li>Take two if you intend to do Legends' Quest.</li></ul>",
    title = "Ardrigal",
    actions = { Action.Direction:new(2864.5, 661, 3118.5) },
    postconditions = { Condition.InventoryContains:new(Models.items["grimy ardrigal"]) },
  },
  {
    text = "Clean the herb.",
    actions = { Action.InventoryHighlight:new(Models.items["grimy ardrigal"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["ardrigal"]) },
  },
  {
    text = "Talk to Trufitus to give him a clean ardrigal.",
    actions = {
      Action.Direction:new(2809.5, 1221, 3086, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["trufitus"], { distance = 8 }),
      Action.ConversationHighlight:new("Of course!"),
    },
    postconditions = { Condition.ConversationText:new("blackened by the living flame") },
  },
  {
    text = "Search the scorched earth for a grimy sito foil.",
    title = "Sito foil",
    actions = { Action.Direction:new(2791, 563, 3047, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(grimySitoFoil) },
  },
  {
    text = "Clean the herb.",
    actions = { Action.InventoryHighlight:new(grimySitoFoil) },
    postconditions = { Condition.InventoryContains:new(sitoFoil) },
  },
  {
    text = "Talk to Trufitus to give him a clean sito foil.",
    actions = {
      Action.Direction:new(2809.5, 1221, 3086, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["trufitus"], { distance = 8 }),
      Action.ConversationHighlight:new("Of course!"),
    },
    postconditions = { Condition.ConversationText:new("high metal content") },
  },
  {
    text = "Search the green-coloured rocks for a grimy volencia moss.",
    title = "Volencia moss",
    actions = {
      Action.Direction:new(2850, 517, 3034, { distance = 20 }),
      Action.ModelHighlight:new(mossRock, { distance = 20 }),
    },
    postconditions = { Condition.InventoryContains:new(grimyVolenciaMoss) },
  },
  {
    text = "Clean the herb.",
    actions = { Action.InventoryHighlight:new(grimyVolenciaMoss) },
    postconditions = { Condition.InventoryContains:new(volenciaMoss) },
  },
  {
    text = "Talk to Trufitus to give him a clean volencia moss.",
    actions = {
      Action.Direction:new(2809.5, 1221, 3086, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["trufitus"], { distance = 8 }),
      Action.ConversationHighlight:new("Of course!"),
    },
    postconditions = { Condition.ConversationText:new("it may be dangerous") },
  },
  {
    text = "Enter the cave to the north.",
    title = "Rogue's purse",
    actions = {
      Action.Direction:new(2824.5, 473, 3118.5, { distance = 20 }),
      Action.ModelHighlight:new(Models.objects["karamja coast cave entrance"], { distance = 20 }),
      Action.ConversationHighlight:new("Yes, I'll enter the cave."),
    },
    postconditions = { Condition.DistanceTo:new(2830, 853, 9523, 8) },
  },
  {
    text = "Search a fungus covered Cavern wall for a grimy rogue's purse.<ul><li>Take two if you intend to do Zogre Flesh Eaters.</li><li>(Optional) Kill one jogre to complete one of the easy Karamja achievements, 'It's a Jungle Ogre'.</li></ul>",
    actions = { Action.Direction:new(2831, 1897, 9490) },
    postconditions = { Condition.InventoryContains:new(Models.items["grimy rogue's purse"]) },
  },
  {
    text = "Clean the herb.",
    actions = { Action.InventoryHighlight:new(Models.items["grimy rogue's purse"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["rogue's purse"]) },
  },
  {
    text = "Talk to Trufitus to give him a clean rogue's purse.",
    actions = {
      Action.Direction:new(2809.5, 1221, 3086, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["trufitus"], { distance = 8 }),
      Action.ConversationHighlight:new("Of course!"),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Jungle Potion",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.short,
  releaseDate = 1035331200,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Herblore", 3),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
