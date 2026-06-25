local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local warrior = Model.new(18372, {
  [11248] = Vertex.new(1, 743, -7, 127, 127, 127),
  [11249] = Vertex.new(1, 740, -7, 127, 127, 127),
  [11250] = Vertex.new(1, 740, -8, 127, 127, 127),
  [11254] = Vertex.new(77, 651, 19, 127, 127, 127),
  [11257] = Vertex.new(-75, 651, 19, 127, 127, 127),
})
local shamus = Model.new(4047, {
  [14] = Vertex.new(40, 240, -16, 54, 142, 28),
  [15] = Vertex.new(32, 240, -16, 54, 142, 28),
  [17] = Vertex.new(-32, 240, -16, 54, 142, 28),
  [18] = Vertex.new(-40, 240, -16, 54, 142, 28),
  [1594] = Vertex.new(44, 548, -24, 37, 86, 30),
})
local treeSpirit = Model.new(1794, {
  [530] = Vertex.new(0, 568, 156, 36, 28, 27),
  [608] = Vertex.new(0, 568, 156, 46, 42, 42),
  [614] = Vertex.new(0, 568, 156, 46, 42, 42),
  [805] = Vertex.new(0, 784, -36, 69, 47, 27),
  [869] = Vertex.new(0, 764, -52, 64, 41, 19),
})
--#endregion
--#region Objects
local dramenTree = Model.new(138, {
  [17] = Vertex.new(6800, 3244, 3856, 127, 128, 128),
  [53] = Vertex.new(6780, 3332, 3804, 127, 128, 128),
  [107] = Vertex.new(6848, 3224, 3904, 127, 128, 127),
  [116] = Vertex.new(6848, 3224, 3904, 127, 128, 127),
  [120] = Vertex.new(6848, 3224, 3904, 127, 128, 127),
})
local shedDoor = Model.new(1356, {
  [1032] = Vertex.new(-220, 736, -48, 14, 28, 2),
  [1155] = Vertex.new(-220, 736, -48, 33, 56, 16),
  [1200] = Vertex.new(-220, 700, 44, 33, 56, 16),
  [1260] = Vertex.new(-220, 764, 16, 60, 87, 35),
  [1269] = Vertex.new(-220, 672, -20, 14, 28, 2),
})
--#endregion
--#region Items
local dramenBranch = Model.multi({
  Model.new(165, {
    [2] = Vertex.new(-76, 0, 128, 34, 34, 3),
    [6] = Vertex.new(-76, 0, 128, 34, 34, 3),
    [122] = Vertex.new(24, 0, 108, 34, 34, 3),
    [161] = Vertex.new(88, 0, 68, 34, 34, 3),
    [165] = Vertex.new(88, 0, 68, 34, 34, 3),
  }),
  Model.new(6, {
    [1] = Vertex.new(-80, 0, 136, 128, 128, 128),
    [2] = Vertex.new(16, 0, -128, 128, 128, 128),
    [3] = Vertex.new(-24, 0, -128, 128, 128, 128),
    [4] = Vertex.new(-80, 0, 136, 128, 128, 128),
    [5] = Vertex.new(96, 0, 104, 128, 128, 128),
  }),
})
local dramenStaff = Model.new(138, {
  [3] = Vertex.new(-224, 12, 224, 93, 66, 8),
  [11] = Vertex.new(192, 24, -176, 93, 66, 8),
  [21] = Vertex.new(192, 24, -176, 93, 66, 8),
  [48] = Vertex.new(-224, 12, 224, 93, 66, 8),
  [111] = Vertex.new(192, 24, -176, 93, 66, 8),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the Warrior northwest of the Lumbridge swamp to start the quest.",
    title = "Seeking the lost city",
    actions = {
      Action.ModelHighlight:new(warrior, { distance = 12 }),
      Action.Direction:new(3150, 1397, 3204, { distance = 12 }),
      Action.ConversationHighlight:new("Why are you camped out here?"),
      Action.ConversationHighlight:new("Who's Zanaris?"),
      Action.ConversationHighlight:new("If it's hidden, how are you planning to find it?"),
      Action.ConversationHighlight:new("Looks like you don't know either."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the warrior.",
    actions = { Action.ModelHighlight:new(warrior) },
    postconditions = {
      Condition.ConversationText:new("He must be hiding around here somewhere."),
    },
  },
  {
    text = "Attempt to chop the swamp tree south of the campsite.",
    actions = {
      Action.Direction:new(3154.5, 741, 3193.2),
    },
    postconditions = { Condition.ModelVisible:new(shamus) },
  },
  {
    text = "Talk to Shamus.",
    actions = {
      Action.ModelHighlight:new(shamus),
      Action.ConversationHighlight:new("I've been in that shed and I didn't see a city."),
      Action.ConversationHighlight:new("Yes, please, a teleport would be useful."),
    },
    postconditions = { Condition.ConversationText:new("Right yer are. Hold on!") },
  },
  {
    text = "Bring items to craft a weapon on Entrana. Once you're ready, manually move to the next step.<ul><li>If you plan on using magic, you must have a magic weapon; you cannot cast spells without one.</li><li>You can also remove tool belt items once you enter the dungeon, e.g. a mithril hatchet, for use in combat.</li></ul>",
    title = "Entrana",
    neededItems = {
      ["Items to craft combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
  },
  {
    text = "Travel to Entrana.<ul><li><b>Do not craft your weapons now or you'll be thrown out of Entrana.</b></li></ul>",
    actions = {
      Action.ModelHighlight:new(Models.npcs["monk of entrana"], { distance = 8 }),
      Action.Direction:new(3045, 741, 3236, { distance = 8 }),
    },
    postconditions = { Condition.DistanceTo:new(2834, 965, 3335, 8) },
  },
  {
    text = "Run north-west though the wooded area and climb down the ladder.",
    actions = {
      Action.Direction:new(2820, 693, 3373),
      Action.ConversationHighlight:new("Well, that is a risk I will have to take."),
    },
    postconditions = { Condition.DistanceTo:new(2822, 1061, 9774, 8) },
  },
  {
    text = "Craft your weapons now. After you're geared, run to the southern-most part of the dungeon.",
    actions = {
      Action.Direction:new(2860, 1701, 9733),
    },
    postconditions = { Condition.DistanceTo:new(2860, 1701, 9733, 8) },
  },
  {
    text = "Chop the dramen tree.",
    actions = { Action.ModelHighlight:new(dramenTree) },
    postconditions = { Condition.ModelVisible:new(treeSpirit) },
  },
  {
    text = "Kill the dramen tree spirit. Safe spot is marked.<ul><li>Make sure to let the dialogue appear before chopping again, or you may spawn a second spirit.</li></ul>",
    actions = { Action.Direction:new(2853, 885, 9733) },
    postconditions = {
      Condition.ConversationText:new("With the Tree Spirit defeated, you can now chop the tree."), --might not work. needs testing
    },
  },
  {
    text = "Chop the dramen tree again.<ul><li>Get multiple branches just in case.</li><li>Grind one with a pestal and mortar for a future quest.</li></ul>",
    actions = { Action.ModelHighlight:new(dramenTree) },
    postconditions = { Condition.InventoryContains:new(dramenBranch) },
  },
  {
    text = "Craft the dramen branch using a knife",
    actions = { Action.InventoryHighlight:new(dramenBranch) },
    postconditions = { Condition.InventoryContains:new(dramenStaff) },
  },
  {
    text = "Use the Lumbridge lodestone, then enter the shed in the centre of the Lumbridge swamp while wielding a dramen staff.",
    title = "Welcome to the moon",
    actions = {
      Action.ModelHighlight:new(shedDoor, { distance = 12 }),
      Action.Direction:new(3201, 645, 3169, { distance = 12 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Lost City",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1014768000,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Crafting", 31),
    Types.QuestReq.skill("Woodcutting", 36),
  },
  neededItems = {
    ["Items to craft combat gear"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = { ["Dramen tree spirit"] = { level = "63", quantity = 1 } },
})
