local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local mossGiant = Model.new(3378, {
  [1696] = Vertex.new(165, 1295, -160, 48, 43, 31),
  [1770] = Vertex.new(-395, 1435, -85, 43, 37, 22),
  [1771] = Vertex.new(-395, 1435, -85, 43, 37, 22),
  [1985] = Vertex.new(-395, 1435, -85, 58, 54, 45),
  [1992] = Vertex.new(-455, 1450, 60, 58, 54, 45),
})
--#endregion
--#region Objects
local waterfallCaveEntrance = Model.new(279, {
  [231] = Vertex.new(256, 672, 156, 58, 48, 30),
  [247] = Vertex.new(256, 800, 256, 58, 48, 30),
  [252] = Vertex.new(-256, 800, 256, 58, 48, 30),
  [253] = Vertex.new(-256, 800, 256, 58, 48, 30),
  [274] = Vertex.new(-256, 680, 160, 58, 48, 30),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local consecrationSeed = Model.new(60, {
  [46] = Vertex.new(-50, 27, -50, 71, 65, 65, 0.6863),
  [49] = Vertex.new(-50, 27, -50, 71, 65, 65, 0.6863),
  [52] = Vertex.new(-50, 27, -50, 71, 65, 65, 0.6863),
  [55] = Vertex.new(-50, 27, -50, 71, 65, 65, 0.6863),
  [58] = Vertex.new(-50, 27, -50, 71, 65, 65, 0.6863),
})
local enchantedConsecrationSeed = Model.new(60, {
  [46] = Vertex.new(-50, 27, -50, 142, 142, 155, 0.6863),
  [49] = Vertex.new(-50, 27, -50, 142, 142, 155, 0.6863),
  [52] = Vertex.new(-50, 27, -50, 142, 142, 155, 0.6863),
  [55] = Vertex.new(-50, 27, -50, 142, 142, 155, 0.6863),
  [58] = Vertex.new(-50, 27, -50, 142, 142, 155, 0.6863),
})
local waterfallKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 153, 121, 13),
  [2] = Vertex.new(-12, 16, -84, 153, 121, 13),
  [3] = Vertex.new(-20, 16, -92, 153, 121, 13),
  [6] = Vertex.new(-60, 16, -52, 153, 121, 13),
  [7] = Vertex.new(-32, 16, -48, 153, 121, 13),
})
local dungeonKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 153, 121, 13),
  [2] = Vertex.new(-12, 16, -84, 153, 121, 13),
  [3] = Vertex.new(-20, 16, -92, 153, 121, 13),
  [6] = Vertex.new(-60, 16, -52, 153, 121, 13),
  [7] = Vertex.new(-32, 16, -48, 153, 121, 13),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Islwyn at the end of the path.<ul><li>Hop worlds if she isn't there.</li></ul>",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = {
      Action.PathGuide:new({
        Location:new(2254, 1301, 3149),
        Location:new(2262, 1157, 3151),
        Location:new(2268, 1084, 3158),
        Location:new(2271, 1029, 3164),
        Location:new(2271, 1029, 3164),
        Location:new(2274, 965, 3171),
        Location:new(2274, 965, 3172),
      }),
      Action.PathGuide:new({
        Location:new(2274, 965, 3176),
        Location:new(2275, 965, 3179),
        Location:new(2274, 997, 3182),
        Location:new(2278, 909, 3187),
        Location:new(2284, 853, 3188),
      }),
      Action.PathGuide:new({
        Location:new(2287, 781, 3188),
        Location:new(2289, 789, 3187),
        Location:new(2293, 821, 3184),
        Location:new(2292, 1125, 3175),
        Location:new(2293, 1301, 3167),
        Location:new(2295, 1349, 3163),
        Location:new(2293, 1381, 3158),
        Location:new(2290, 997, 3146),
      }),
      Action.ModelHighlight:new(Models.npcs["islwyn"], { distance = 20 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Islwyn.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["islwyn"]),
      Action.ConversationHighlight:new("Tell the truth?"),
      Action.ConversationHighlight:new("Maybe I could help."),
    },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Talk to Eluned.",
    title = "Glarial's tomb",
    neededItems = {
      ["No combat equipment"] = { quantity = 1 },
      ["Glarial's pebble"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["eluned"]) },
    postconditions = { Condition.ConversationText:new("as I have it") },
  },
  {
    text = "Use Glarial's pebble on Glarial's tombstone north-west of the Fishing Guild.<ul><li>You can reclaim Glarial's pebble from Golrie in Gnome Village Dungeon.</li></ul>",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2557.5, 2741, 3444.5),
      Action.InventoryHighlight:new(Models.items["glarial's pebble"]),
      Action.ConversationHighlight:new("Yes please!"),
    },
    postconditions = { Condition.DistanceTo:new(2557, 853, 9844, 1) },
  },
  {
    text = "Kill a moss giant.<ul><li>Remove an item from your tool belt to use as a weapon.</li></ul>",
    actions = { Action.ModelHighlight:new(mossGiant, { highlightPriority = "closest" }) },
    postconditions = { Condition.ModelVisible:new(consecrationSeed) },
  },
  {
    text = "Pick up the consecration seed.",
    actions = { Action.ModelHighlight:new(consecrationSeed) },
    postconditions = { Condition.InventoryContains:new(consecrationSeed) },
  },
  {
    text = "Search the chest to the west.",
    actions = { Action.Direction:new(2530, 653, 9844) },
    postconditions = { Condition.InventoryContains:new(Models.items["glarial's amulet"]) },
  },
  {
    text = "Return to Eluned.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = {
      Action.PathGuide:new({
        Location:new(2254, 1301, 3149),
        Location:new(2262, 1157, 3151),
        Location:new(2268, 1084, 3158),
        Location:new(2271, 1029, 3164),
        Location:new(2271, 1029, 3164),
        Location:new(2274, 965, 3171),
        Location:new(2274, 965, 3172),
      }),
      Action.PathGuide:new({
        Location:new(2274, 965, 3176),
        Location:new(2275, 965, 3179),
        Location:new(2274, 997, 3182),
        Location:new(2278, 909, 3187),
        Location:new(2284, 853, 3188),
      }),
      Action.PathGuide:new({
        Location:new(2287, 781, 3188),
        Location:new(2289, 789, 3187),
        Location:new(2293, 821, 3184),
        Location:new(2292, 1125, 3175),
        Location:new(2293, 1301, 3167),
        Location:new(2295, 1349, 3163),
        Location:new(2293, 1381, 3158),
        Location:new(2290, 997, 3146),
      }),
      Action.ModelHighlight:new(Models.npcs["eluned"], { distance = 20 }),
    },
    postconditions = { Condition.InventoryContains:new(enchantedConsecrationSeed) },
  },
  {
    text = "Board the raft next to Almera's house.",
    title = "Waterfall dungeon",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "2",
      hover = "Games necklace",
      url = "Games_necklace_(1).png",
    },
    neededItems = {
      ["Rope"] = { quantity = 1, model = Models.items["rope"], duringQuest = true },
      ["Glarial's amulet"] = { quantity = 1, model = Models.items["glarial's amulet"] },
      ["Consecration seed"] = { quantity = 1 },
    },
    recommendedItems = { ["Games necklace"] = { quantity = 1 } },
    actions = { Action.Direction:new(2509, 6741, 3493.5) },
    postconditions = { Condition.DistanceTo:new(2512, 5947, 3481, 1) },
  },
  {
    text = "Use a rope on the rock.",
    warning = "Do not click 'swim to rock'.",
    actions = {
      Action.InventoryHighlight:new(Models.items["rope"]),
      Action.Direction:new(2512, 4837, 3468),
    },
    postconditions = { Condition.DistanceTo:new(2513, 4837, 3468, 1) },
  },
  {
    text = "Use a rope on the dead tree.",
    warning = "Do not click 'climb tree'.",
    actions = {
      Action.InventoryHighlight:new(Models.items["rope"]),
      Action.Direction:new(2512, 5501, 3465),
    },
    postconditions = { Condition.DistanceTo:new(2511, 3269, 3463, 0) },
  },
  {
    text = "Enter the cave door.",
    actions = { Action.ModelHighlight:new(waterfallCaveEntrance) },
    postconditions = { Condition.DistanceTo:new(2575, 749, 9861, 2) },
  },
  {
    text = "Search the crates.",
    actions = { Action.Direction:new(2589, 1245, 9888) },
    postconditions = {
      Condition.ChatText:new("and find a large key"),
      Condition.InventoryContains:new(waterfallKey),
    },
  },
  {
    text = "Open the door next to the fire giants.",
    actions = { Action.Direction:new(2568, 1245, 9893.5) },
    postconditions = { Condition.DistanceTo:new(2568, 645, 9895, 1) },
  },
  {
    text = "Open the door.",
    actions = { Action.Direction:new(2566, 1281, 9901.5) },
    postconditions = { Condition.DistanceTo:new(2604, 669, 9901, 4) },
  },
  {
    text = "Plant the enchanted consecration seed.",
    actions = { Action.InventoryHighlight:new(enchantedConsecrationSeed) },
    postconditions = { Condition.ChatText:new("crystal seed in the hole") },
  },
  {
    text = "Return to Islwyn.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Tirannwn lodestone",
      url = "Tirannwn_lodestone_icon.png",
    },
    actions = {
      Action.PathGuide:new({
        Location:new(2254, 1301, 3149),
        Location:new(2262, 1157, 3151),
        Location:new(2268, 1084, 3158),
        Location:new(2271, 1029, 3164),
        Location:new(2271, 1029, 3164),
        Location:new(2274, 965, 3171),
        Location:new(2274, 965, 3172),
      }),
      Action.PathGuide:new({
        Location:new(2274, 965, 3176),
        Location:new(2275, 965, 3179),
        Location:new(2274, 997, 3182),
        Location:new(2278, 909, 3187),
        Location:new(2284, 853, 3188),
      }),
      Action.PathGuide:new({
        Location:new(2287, 781, 3188),
        Location:new(2289, 789, 3187),
        Location:new(2293, 821, 3184),
        Location:new(2292, 1125, 3175),
        Location:new(2293, 1301, 3167),
        Location:new(2295, 1349, 3163),
        Location:new(2293, 1381, 3158),
        Location:new(2290, 997, 3146),
      }),
      Action.ModelHighlight:new(Models.npcs["islwyn"], { distance = 20 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Roving Elves",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1107734400,
  prereqQuests = { "Regicide", "Waterfall Quest" },
  questReqs = {},
  neededItems = {
    ["No combat equipment"] = { quantity = 1 },
    ["Rope"] = { quantity = 1, model = Models.items["rope"], duringQuest = true },
    ["Glarial's pebble"] = { quantity = 1, model = Models.items["glarial's pebble"] },
    ["Glarial's amulet"] = { quantity = 1, model = Models.items["glarial's amulet"] },
  },
  recommendedItems = { ["Games necklace"] = { quantity = 1 } },
  combatNPCs = { ["Moss giant"] = { quantity = 1, level = "24" } },
})
