local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local gertrude = Model.new(4365, {
  [1992] = Vertex.new(24, 738, -38, 31, 30, 28),
  [2016] = Vertex.new(-24, 738, -38, 31, 30, 28),
  [2155] = Vertex.new(-2, 716, -57, 108, 79, 55),
  [2161] = Vertex.new(2, 716, -57, 108, 79, 55),
  [2165] = Vertex.new(6, 716, -52, 108, 79, 55),
})
local wilough = Model.new(3090, {
  [2288] = Vertex.new(28, 512, -60, 58, 43, 24),
  [2291] = Vertex.new(-28, 512, -60, 58, 43, 24),
  [2305] = Vertex.new(28, 512, -60, 58, 43, 24),
  [2325] = Vertex.new(8, 497, -60, 58, 43, 24),
  [2594] = Vertex.new(-8, 497, -60, 58, 43, 24),
})
local fluffs = Model.new(1611, {
  [13] = Vertex.new(37, 420, -56, 88, 92, 96),
  [14] = Vertex.new(37, 420, 18, 88, 92, 96),
  [15] = Vertex.new(-37, 420, -56, 88, 92, 96),
})
--#endregion
--#region Objects
local abandonedHouseLadder = Model.new(24354, {
  [151] = Vertex.new(1877, 2033, 3673, 127, 127, 127),
  [169] = Vertex.new(1815, 2006, 3972, 127, 127, 127),
  [199] = Vertex.new(1911, 2270, 3673, 127, 127, 127),
  [205] = Vertex.new(1858, 2227, 3972, 127, 127, 127),
  [217] = Vertex.new(1858, 2227, 3972, 127, 127, 127),
})
local abandonedHouseUpperLadder = Model.new(24355, {
  [1] = Vertex.new(1995, 2748, 3673, 127, 127, 127),
  [7] = Vertex.new(1937, 2703, 3972, 127, 127, 127),
  [19] = Vertex.new(1937, 2703, 3972, 127, 127, 127),
  [115] = Vertex.new(1927, 2297, 3997, 127, 127, 127),
  [257] = Vertex.new(1911, 2490, 3972, 127, 127, 127),
})
local shakingCrate = Model.new(110055, {
  [155] = Vertex.new(-132, 304, -84, 127, 127, 127),
  [161] = Vertex.new(133, 312, -112, 127, 127, 127),
  [167] = Vertex.new(95, 312, -136, 127, 127, 127),
  [227] = Vertex.new(-148, 302, 110, 127, 127, 127),
  [233] = Vertex.new(138, 302, -93, 127, 127, 127),
})
--#endregion
--#region Items
local rawSardine = Model.new(174, {
  [157] = Vertex.new(-76, 16, -8, 73, 67, 67),
  [158] = Vertex.new(-68, 16, 0, 73, 67, 67),
  [159] = Vertex.new(-60, 20, -4, 73, 67, 67),
  [160] = Vertex.new(-76, -4, -8, 73, 67, 67),
  [165] = Vertex.new(-56, 20, -12, 113, 148, 114),
})
local doogleLeaves = Model.new(402, {
  [34] = Vertex.new(-76, 4, 64, 8, 87, 10),
  [106] = Vertex.new(-72, 4, 76, 8, 87, 10),
  [128] = Vertex.new(-72, 4, 76, 5, 64, 7),
  [383] = Vertex.new(-56, 4, -24, 8, 87, 10),
  [387] = Vertex.new(-32, -4, -40, 8, 87, 10),
})
--#endregion
--#region Quest items
local doogleSardine = Model.new(255, {
  [229] = Vertex.new(-88, 16, -12, 73, 67, 67),
  [230] = Vertex.new(-80, 16, -4, 73, 67, 67),
  [231] = Vertex.new(-68, 20, -8, 73, 67, 67),
  [243] = Vertex.new(-64, 20, -12, 113, 148, 114),
  [247] = Vertex.new(-64, 28, 28, 20, 99, 22),
})
local threeLittleKittens = Model.new(1548, {
  [706] = Vertex.new(-84, 128, 96, 89, 71, 56),
  [718] = Vertex.new(-84, 128, 96, 89, 71, 56),
  [730] = Vertex.new(-84, 128, 96, 89, 71, 56),
  [736] = Vertex.new(-84, 128, 96, 89, 71, 56),
  [1458] = Vertex.new(-124, 128, 44, 89, 65, 46),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Gertrude, house north-west of the Varrock lodestone.",
    title = "Getting started",
    actions = {
      Action.Direction:new(3151, 965, 3411, { distance = 6 }),
      Action.ModelHighlight:new(gertrude, { distance = 7 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with Gertrude.",
    actions = { Action.ModelHighlight:new(gertrude) },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Go behind the house and pick up a doogle leaf.",
    actions = {
      Action.Direction:new(3153, 1045, 3399, { distance = 6 }),
      Action.ModelHighlight:new(doogleLeaves, { distance = 6 }),
    },
    postconditions = { Condition.InventoryContains:new(doogleLeaves) },
  },
  {
    text = "Talk to Shilop or Wilough, northeastern corner of Varrock Square.",
    title = "The secret hideout",
    actions = {
      Action.Direction:new(3222, 1029, 3436, { distance = 20 }),
      Action.ModelHighlight:new(wilough, { distance = 20 }),
      Action.ConversationHighlight:new("What will make you tell me?"),
      Action.ConversationHighlight:new("Okay then, I'll pay."),
    },
    postconditions = { Condition.ConversationText:new("technically you are trespassing") },
  },
  {
    text = "Climb the ladder in abandoned house south of the Smithing workshop.",
    warning = "Don't skip any dialogue for this section. Only use the pick-up option with Fluffs.",
    actions = {
      Action.Direction:new(3187, 1625, 3415, { distance = 3 }),
      Action.ModelHighlight:new(abandonedHouseLadder, { distance = 3 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3186, 2309, 3415, 3) },
  },
  {
    text = "Try to pick up Fluffs.",
    title = "Convincing Fluffs",
    neededItems = {
      ["Bucket of milk"] = { quantity = 1 },
      ["Raw sardine"] = { quantity = 1 },
      ["Doogle leaf"] = { quantity = 1 },
    },
    actions = { Action.ModelHighlight:new(fluffs) },
    postconditions = { Condition.ConversationText:new("maybe she is thirsty") },
  },
  {
    text = "<i>Use</i> a bucket of milk on Fluffs.",
    actions = { Action.InventoryHighlight:new(Models.items["bucket of milk"]) },
    postconditions = { Condition.ConversationText:new("laps up the milk greedily") },
  },
  {
    text = "Try to pick up Fluffs.",
    actions = { Action.ModelHighlight:new(fluffs) },
    postconditions = { Condition.ConversationText:new("maybe she is hungry too?") },
  },
  {
    text = "Talk to Gertrude.",
    actions = {
      Action.Direction:new(3151, 965, 3411, { distance = 2 }),
      Action.ModelHighlight:new(gertrude, { distance = 3 }),
    },
    postconditions = { Condition.ConversationText:new("raw sardines seasoned with doogle leaves") },
  },
  {
    text = "<i>Use</i> a raw sardine on a doogle leaf.",
    actions = {
      Action.InventoryHighlight:new(doogleLeaves),
      Action.InventoryHighlight:new(rawSardine),
    },
    postconditions = { Condition.InventoryContains:new(doogleSardine) },
  },
  {
    text = "Head back to Fluffs in the abandoned house.",
    actions = {
      Action.Direction:new(3187, 1625, 3415, { distance = 3 }),
      Action.ModelHighlight:new(abandonedHouseLadder, { distance = 3 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3186, 2309, 3415, 3) },
  },
  {
    text = "<i>Use</i> the doogle sardine on Fluffs.",
    actions = {
      Action.InventoryHighlight:new(doogleSardine),
      Action.ModelHighlight:new(fluffs),
    },
    postconditions = { Condition.ConversationText:new("devours the doogle sardine") },
  },
  {
    text = "Try to pick up Fluffs.",
    actions = { Action.ModelHighlight:new(fluffs) },
    postconditions = { Condition.ConversationText:new("afraid to leave") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.ModelHighlight:new(abandonedHouseUpperLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(3186, 1125, 3415, 3) },
  },
  {
    text = "Search the jiggling crate in the ruined building directly east.",
    actions = { Action.ModelHighlight:new(shakingCrate) },
    postconditions = { Condition.InventoryContains:new(threeLittleKittens) },
  },
  {
    text = "Return to Fluffs.",
    actions = {
      Action.Direction:new(3187, 1625, 3415, { distance = 3 }),
      Action.ModelHighlight:new(abandonedHouseLadder, { distance = 3 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3186, 2309, 3415, 3) },
  },
  {
    text = "<i>Use</i> the three little kittens on her.<ul><li>You might need to use them on her twice.</li></ul>",
    actions = { Action.ModelHighlight:new(fluffs), Action.InventoryHighlight:new(threeLittleKittens) },
    postconditions = { Condition.ConversationText:new("Purr...") },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("offspring") },
  },
  {
    text = "Return to Gertrude.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(3151, 965, 3411, { distance = 2 }),
      Action.ModelHighlight:new(gertrude, { distance = 3 }),
      Action.ModelHighlight:new(abandonedHouseUpperLadder),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Gertrude's Cat",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1059350400,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Bucket of milk"] = { quantity = 1, model = Models.items["bucket of milk"] },
    ["Raw sardine"] = { quantity = 1, model = rawSardine },
    ["Doogle leaves"] = { quantity = 1, model = doogleLeaves },
  },
  recommendedItems = {},
  combatNPCs = {},
})
