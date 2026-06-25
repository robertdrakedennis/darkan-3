local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local mirrorShield = Models.items["mirror shield"]
local bagOfSalt = Models.items["bag of salt"]

local blackUnicorn = Model.new(49074, {
  [64] = Vertex.new(-45, 742, -430, 127, 127, 127),
})

local oldMan = Models.npcs["odd old man"]

local blackHide = Model.new(384, {
  [23] = Vertex.new(44, 4, 252, 140, 118, 107),
})
local scrubbedBlackHide = Model.new(384, {
  [1] = Vertex.new(120, 20, -28, 49, 45, 44),
})

local bear = Model.new(2802, {
  [2] = Vertex.new(-27, 412, -603, 128, 128, 127),
})
local bearPelt = Model.new(384, {
  [1] = Vertex.new(120, 20, -28, 49, 45, 44),
})
local scrubbedBearPelt = Model.new(798, {
  [1] = Vertex.new(180, 28, -108, 0, 0, 0),
  [2] = Vertex.new(160, 28, -128, 0, 0, 0),
  [3] = Vertex.new(168, 28, -104, 0, 0, 0),
  [5] = Vertex.new(172, 28, -124, 0, 0, 0),
  [7] = Vertex.new(188, 28, -168, 32, 29, 29),
})
local tannedBearPelt = Model.new(798, {
  [1] = Vertex.new(180, 28, -108, 0, 0, 0),
  [2] = Vertex.new(160, 28, -128, 0, 0, 0),
  [3] = Vertex.new(168, 28, -104, 0, 0, 0),
  [5] = Vertex.new(172, 28, -124, 0, 0, 0),
  [7] = Vertex.new(188, 28, -168, 32, 29, 29),
})

local unicorn = Model.new(49122, {
  [1498] = Vertex.new(-45, 742, -430, 157, 157, 157),
})
local hide = Model.new(381, {
  [23] = Vertex.new(48, 4, 256, 140, 118, 107),
})
local scrubbedHide = Model.new(381, {
  [1] = Vertex.new(120, 20, -28, 156, 144, 143),
})

local caveCrawler = Model.new(4242, {
  [404] = Vertex.new(85, 359, -220, 148, 141, 114),
})
local caveSkin = Model.new(750, {
  [1] = Vertex.new(52, 4, 16, 38, 38, 29),
})
local scrubbedCaveSkin = Model.new(750, {
  [1] = Vertex.new(52, 4, 16, 38, 38, 29),
  [2] = Vertex.new(76, 4, 8, 38, 38, 29),
  [3] = Vertex.new(36, 12, 8, 38, 38, 29),
  [4] = Vertex.new(132, 4, 68, 38, 38, 29),
  [5] = Vertex.new(156, 4, 68, 38, 38, 29),
})
local cockatrice = Model.new(2664, {
  [389] = Vertex.new(-28, 556, -116, 94, 106, 55),
})
local cockatriceSkin = Model.new(312, {
  [122] = Vertex.new(-144, 4, 116, 140, 118, 107),
})
local scrubbedCockatriceSkin = Model.new(312, {
  [1] = Vertex.new(0, 12, 108, 75, 84, 44),
})

local fox = Model.new(1539, {
  [572] = Vertex.new(20, 172, -168, 100, 56, 9),
})
local foxPelt = Model.new(990, {
  [86] = Vertex.new(60, 0, 184, 140, 118, 107),
})
local scrubbedFoxPelt = Model.new(990, {
  [1] = Vertex.new(28, 16, -124, 131, 123, 41),
})

local fenris = Model.new(2640, {
  [187] = Vertex.new(12, 364, -392, 126, 121, 97),
})
local fenrisPelt = Model.new(987, {
  [83] = Vertex.new(64, 0, 180, 140, 118, 107),
})
local scrubbedFenrisPelt = Model.new(987, {
  [1] = Vertex.new(28, 16, -124, 133, 160, 33),
})

local hobgoblin = Model.new(4668, {
  [4379] = Vertex.new(-258, 140, -925, 156, 148, 143),
})
local hobSkin = Model.new(810, {
  [84] = Vertex.new(-197, 4, -19, 113, 90, 72),
})
local scrubbedHobSkin = Model.new(810, {
  [1] = Vertex.new(39, 29, -118, 148, 147, 136),
})

local rockCrab = Model.new(1326, {
  [1044] = Vertex.new(-96, 100, 60, 88, 82, 67),
})
local rockCarcass = Model.new(603, {
  [398] = Vertex.new(-92, 4, 64, 82, 77, 63),
})
local scrubbedRockCarcass = Model.new(603, {
  [1] = Vertex.new(-68, 4, -156, 49, 37, 31),
  [2] = Vertex.new(-56, 12, -144, 49, 37, 31),
  [3] = Vertex.new(-48, 4, -156, 49, 37, 31),
  [6] = Vertex.new(-72, 4, -140, 49, 37, 31),
  [7] = Vertex.new(72, 4, -156, 49, 37, 31),
})

local firepit = Model.new(1422, {
  [59] = Vertex.new(440, 8, 384, 140, 133, 107),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the Odd Old Man in Silvarea.",
    title = "Getting started",
    actions = {
      Action.Direction:new(3364, 2485, 3503, { distance = 15 }),
      Action.ModelHighlight:new(oldMan, { distance = 15 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Finish talking to the odd old man.",
    actions = { Action.ModelHighlight:new(oldMan) },
    postconditions = { Condition.ConversationText:new("Off I go then") },
  },
  {
    text = "Lodestone teleport to the Fremennik Province.",
    title = "Obtaining the furs",
    actions = { Action.Direction:new(2710, 573, 3664) },
    postconditions = { Condition.ModelVisible:new(blackUnicorn) },
  },
  {
    text = "Kill a black unicorn (adult, not a foal) and pick up the hide it drops.",
    actions = { Action.ModelHighlight:new(blackUnicorn, { highlightPriority = "closest" }) },
    warning = "The plugin highlights also the foals, ensure you kill an adult (called Black Unicorn)",
    postconditions = { Condition.ModelVisible:new(blackHide) },
  },
  {
    actions = { Action.ModelHighlight:new(blackHide) },
    postconditions = { Condition.InventoryContains:new(blackHide) },
  },
  {
    text = "Go south, kill an adult bear and pick up the pelt it drops.",
    actions = {
      Action.Direction:new(2719, 973, 3645, { distance = 30 }),
      Action.ModelHighlight:new(bear, { highlightPriority = "closest" }),
    },
    postconditions = { Condition.InventoryContains:new(bearPelt) },
    warning = "The plugin does not detect when the bear drops the pelt, pick up the pelt to continue the quest.",
  },
  {
    text = "Go south east, kill a normal adult unicorn and pick up the hide it drops.",
    actions = {
      Action.Direction:new(2738, 837, 3619, { distance = 30 }),
      Action.ModelHighlight:new(unicorn, { highlightPriority = "closest", distance = 30 }),
    },
    postconditions = { Condition.ModelVisible:new(hide) },
    warning = "The plugin highlights also the foals, ensure you kill an adult (called Unicorn).",
  },
  {
    actions = { Action.ModelHighlight:new(hide) },
    postconditions = { Condition.InventoryContains:new(hide) },
  },
  {
    text = "Enter the Fremennik slayer dungeon",
    actions = { Action.Direction:new(2797, 1605, 3615.5) },
    postconditions = { Condition.DistanceTo:new(2808, 853, 10002, 10) },
  },
  {
    text = "Kill a cave crawler and pick up the skin it drops.",
    actions = { Action.ModelHighlight:new(caveCrawler, { highlightPriority = "closest" }) },
    postconditions = { Condition.ModelVisible:new(caveSkin) },
  },
  {
    actions = { Action.ModelHighlight:new(caveSkin) },
    postconditions = { Condition.InventoryContains:new(caveSkin) },
  },
  {
    text = "Continue through the dungeon, kill a Cockatrice (mirror shield recommended), and pick up the skin it drops.",
    actions = {
      Action.Direction:new(2793, 733, 10033, { distance = 10 }),
      Action.ModelHighlight:new(cockatrice, { highlightPriority = "closest", distance = 15 }),
    },
    postconditions = { Condition.ModelVisible:new(cockatriceSkin) },
  },
  {
    actions = { Action.ModelHighlight:new(cockatriceSkin) },
    postconditions = { Condition.InventoryContains:new(cockatriceSkin) },
  },
  {
    text = "Teleport back to the lodestone, walk south of the city and kill a fox for its pelt.",
    actions = {
      Action.Direction:new(2636, 1005, 3618, { distance = 30 }),
      Action.ModelHighlight:new(fox, { highlightPriority = "closest", distance = 30 }),
    },
    postconditions = { Condition.ModelVisible:new(foxPelt) },
  },
  {
    actions = { Action.ModelHighlight:new(foxPelt) },
    postconditions = { Condition.InventoryContains:new(foxPelt) },
  },
  {
    text = "Kill a Fenris wolf for its pelt.",
    actions = {
      Action.ModelHighlight:new(fenris, { highlightPriority = "closest" }),
    },
    postconditions = { Condition.ModelVisible:new(fenrisPelt) },
  },
  {
    actions = { Action.ModelHighlight:new(fenrisPelt) },
    postconditions = { Condition.InventoryContains:new(fenrisPelt) },
  },
  {
    text = "Go north into the city of Relekka and kill a Hobgoblin for its skin.",
    actions = {
      Action.Direction:new(2684, 1253, 3725, { distance = 10 }),
      Action.ModelHighlight:new(hobgoblin, { highlightPriority = "closest", distance = 15 }),
    },
    postconditions = { Condition.ModelVisible:new(hobSkin) },
  },
  {
    actions = { Action.ModelHighlight:new(hobSkin) },
    postconditions = { Condition.InventoryContains:new(hobSkin) },
  },
  {
    text = "Kill a Rock crab for its carcass",
    actions = {
      Action.ModelHighlight:new(rockCrab, { highlightPriority = "closest", distance = 15 }),
    },
    postconditions = { Condition.ModelVisible:new(rockCarcass) },
  },
  {
    actions = { Action.ModelHighlight:new(rockCarcass) },
    postconditions = { Condition.InventoryContains:new(rockCarcass) },
  },
  {
    text = "Use a bag of salt on each of the hides.",
    title = "Finishing up",
    neededItems = {
      ["Black unicorn hide"] = { quantity = 1, model = blackHide },
      ["Unicorn hide"] = { quantity = 1, model = hide },
      ["Bear pelt"] = { quantity = 1, model = bearPelt },
      ["Cave crawler skin"] = { quantity = 1, model = caveSkin },
      ["Cockatrice skin"] = { quantity = 1, model = cockatriceSkin },
      ["Fox pelt"] = { quantity = 1, model = foxPelt },
      ["Fenris wolf pelt"] = { quantity = 1, model = fenrisPelt },
      ["Hobgoblin skin"] = { quantity = 1, model = hobSkin },
      ["Rock crab carcass"] = { quantity = 1, model = rockCarcass },
      ["Logs (any)"] = { quantity = 8 },
    },
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedBlackHide) },
    warning = "The scrubbing of the bear pelt, rock carcass and cave crawler skins cannot be tracked.",
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedRockCarcass) },
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedBearPelt) },
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedHide) },
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedCaveSkin) },
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedCockatriceSkin) },
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedFoxPelt) },
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedFenrisPelt) },
  },
  {
    actions = { Action.InventoryHighlight:new(bagOfSalt) },
    postconditions = { Condition.InventoryContains:new(scrubbedHobSkin) },
  },
  {
    text = "Return to the Odd Old Man's house.",
    actions = { Action.Direction:new(3364, 2485, 3503) },
    postconditions = { Condition.DistanceTo:new(3364, 2485, 3503, 10) },
  },
  {
    text = "Head to the north-east and click on the fire pit.<ul><li> Do not walk away after clicking - it will process all furs automatically.</li></ul>",
    actions = { Action.ModelHighlight:new(firepit) },
    warning = "No automatic detection for the next step.",
  },
  {
    text = "Talk to the Odd Old Man.",
    actions = { Action.ModelHighlight:new(oldMan) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Fur 'n Seek",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1249862400,
  prereqQuests = { "Rag and Bone Man" },
  neededItems = {
    ["Logs (any)"] = { quantity = 8, model = Models.items["any logs"] },
    ["Bag of salt"] = { quantity = 9, model = bagOfSalt },
  },
  recommendedItems = {
    ["Mirror Shield"] = { quantity = 1 },
  },
})
