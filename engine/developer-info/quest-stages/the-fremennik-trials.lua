local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local councilWorkman = Model.new(4353, {
  [3400] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [3405] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [3408] = Vertex.new(2, 725, -59, 109, 80, 57),
  [3410] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3667] = Vertex.new(0, 735, -7, 29, 141, 128),
})
local lalli = Model.new(3423, {
  [22] = Vertex.new(25, 980, -201, 56, 171, 16),
  [24] = Vertex.new(17, 976, -205, 56, 171, 16),
  [26] = Vertex.new(32, 977, -196, 56, 171, 16),
  [30] = Vertex.new(-16, 976, -205, 56, 171, 16),
  [32] = Vertex.new(-31, 977, -196, 56, 171, 16),
})
local draugen = Model.new(4320, {
  [13] = Vertex.new(56, 768, -32, 96, 95, 61, 0.6902),
  [19] = Vertex.new(-4, 776, -84, 96, 95, 61, 0.6902),
  [24] = Vertex.new(4, 776, -84, 96, 95, 61, 0.6902),
  [25] = Vertex.new(-56, 768, -32, 96, 95, 61, 0.6902),
  [2568] = Vertex.new(8, 768, -76, 96, 95, 61, 0.6902),
})
local sailor = Model.new(4953, {
  [2899] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2907] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2909] = Vertex.new(7, 724, -51, 106, 78, 54),
  [4416] = Vertex.new(-65, 670, 50, 29, 35, 47),
  [4447] = Vertex.new(65, 670, 50, 29, 35, 47),
})
local fisherman = Model.new(5673, {
  [3184] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3192] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3194] = Vertex.new(7, 724, -51, 108, 80, 56),
  [5136] = Vertex.new(-65, 670, 50, 56, 45, 11),
  [5167] = Vertex.new(65, 670, 50, 56, 45, 11),
})
--#endregion
--#region Objects
local swayingTree = Model.new(4455, {
  [1021] = Vertex.new(1602, 2461, 3705, 0, 0, 0),
  [2015] = Vertex.new(1798, 3940, 4146, 64, 53, 41),
  [2108] = Vertex.new(1829, 4262, 3854, 86, 71, 55),
  [2111] = Vertex.new(1829, 4262, 3854, 138, 115, 88),
  [2334] = Vertex.new(1505, 4608, 3553, 129, 107, 82),
})
local lallisStew = Model.new(570, {
  [91] = Vertex.new(132, 474, -138, 93, 74, 39),
  [94] = Vertex.new(132, 474, -138, 93, 74, 39),
  [95] = Vertex.new(126, 474, -132, 93, 74, 39),
  [97] = Vertex.new(84, 474, -186, 93, 74, 39),
  [99] = Vertex.new(132, 474, -138, 93, 74, 39),
})
--#endregion
--#region Items
local rawShark = Model.new(270, {
  [259] = Vertex.new(-80, 88, 64, 126, 116, 115),
  [261] = Vertex.new(-112, 88, -16, 126, 116, 115),
  [263] = Vertex.new(16, 88, 32, 126, 116, 115),
  [267] = Vertex.new(-48, -76, -16, 126, 116, 115),
  [269] = Vertex.new(-80, -76, 64, 126, 116, 115),
})
--#endregion
--#region Quest Items
local kegOfBeer = Model.new(576, {
  [187] = Vertex.new(8, 260, 52, 49, 44, 31),
  [189] = Vertex.new(-8, 260, 52, 49, 44, 31),
  [190] = Vertex.new(8, 260, 52, 49, 44, 31),
  [396] = Vertex.new(20, 260, 40, 49, 44, 31),
  [398] = Vertex.new(20, 260, 40, 49, 44, 31),
})
local strangeObject = Model.new(282, {
  [2] = Vertex.new(100, 124, -4, 44, 38, 23),
  [11] = Vertex.new(100, 124, -4, 44, 38, 23),
  [14] = Vertex.new(100, 124, 4, 44, 38, 23),
  [15] = Vertex.new(100, 124, -4, 44, 38, 23),
  [18] = Vertex.new(100, 124, 4, 44, 38, 23),
})
local swayingTreeBranch = Model.new(165, {
  [2] = Vertex.new(-76, 0, 128, 115, 80, 60),
  [6] = Vertex.new(-76, 0, 128, 115, 80, 60),
  [122] = Vertex.new(24, 0, 108, 115, 80, 60),
  [161] = Vertex.new(88, 0, 68, 115, 80, 60),
  [165] = Vertex.new(88, 0, 68, 115, 80, 60),
})
local unstrungLyre = Model.new(528, {
  [189] = Vertex.new(56, 4, 92, 95, 67, 29),
  [203] = Vertex.new(60, 12, 100, 95, 67, 29),
  [213] = Vertex.new(60, 12, 100, 95, 67, 29),
  [225] = Vertex.new(56, 4, 92, 95, 67, 29),
  [257] = Vertex.new(96, 16, 128, 110, 81, 45),
})
local petRock = Model.new(216, {
  [44] = Vertex.new(53, 112, 30, 62, 56, 48),
  [165] = Vertex.new(53, 112, 30, 68, 61, 53),
  [166] = Vertex.new(53, 112, 30, 68, 61, 53),
  [170] = Vertex.new(53, 112, 30, 68, 61, 53),
  [172] = Vertex.new(57, 116, -2, 68, 61, 53),
})
local goldenFleece = Model.new(219, {
  [77] = Vertex.new(-100, 0, 112, 205, 188, 112),
  [190] = Vertex.new(-76, 0, 132, 205, 188, 112),
  [205] = Vertex.new(-76, 0, 132, 205, 188, 112),
  [214] = Vertex.new(-100, 0, 112, 205, 188, 112),
  [217] = Vertex.new(-100, 0, 112, 205, 188, 112),
})
local goldenWool = Model.multi({
  Model.new(174, {
    [149] = Vertex.new(68, 0, -92, 205, 188, 112),
    [165] = Vertex.new(84, 16, -104, 205, 188, 112),
    [167] = Vertex.new(84, 16, -104, 205, 188, 112),
    [170] = Vertex.new(84, 16, -104, 205, 188, 112),
    [173] = Vertex.new(84, 16, -104, 205, 188, 112),
  }),
  Model.new(120, {
    [90] = Vertex.new(-60, 68, -24, 205, 188, 112, 0.6235),
    [93] = Vertex.new(-60, 68, -24, 205, 188, 112, 0.6235),
    [95] = Vertex.new(-60, 68, -24, 205, 188, 112, 0.6235),
    [106] = Vertex.new(-60, 68, -24, 205, 188, 112, 0.6235),
    [108] = Vertex.new(-24, 68, -60, 205, 188, 112, 0.6235),
  }),
})
local huntersTalisman = Model.new(372, {
  [123] = Vertex.new(52, 0, 160, 49, 54, 49),
  [126] = Vertex.new(52, 0, 160, 49, 54, 49),
  [165] = Vertex.new(-16, 0, 140, 49, 54, 49),
  [323] = Vertex.new(48, 0, -132, 49, 54, 49),
  [329] = Vertex.new(48, 0, -132, 49, 54, 49),
})
local oldRedDisk = Model.new(66, {
  [1] = Vertex.new(-68, 12, -8, 132, 58, 26),
  [2] = Vertex.new(-68, 0, -8, 132, 58, 26),
  [3] = Vertex.new(-52, 12, -8, 132, 58, 26),
  [5] = Vertex.new(-52, 0, -8, 132, 58, 26),
  [7] = Vertex.new(-52, 12, -56, 132, 58, 26),
  [8] = Vertex.new(-52, 0, -56, 132, 58, 26),
  [9] = Vertex.new(-68, 12, -56, 132, 58, 26),
  [11] = Vertex.new(-68, 0, -56, 132, 58, 26),
  [15] = Vertex.new(-36, 12, -24, 132, 58, 26),
  [17] = Vertex.new(-36, 0, -24, 132, 58, 26),
})
local woodenDisk = Model.new(66, {
  [1] = Vertex.new(-68, 12, -8, 116, 96, 73),
  [2] = Vertex.new(-68, 0, -8, 116, 96, 73),
  [3] = Vertex.new(-52, 12, -8, 116, 96, 73),
  [5] = Vertex.new(-52, 0, -8, 116, 96, 73),
  [7] = Vertex.new(-52, 12, -56, 116, 96, 73),
  [8] = Vertex.new(-52, 0, -56, 116, 96, 73),
  [9] = Vertex.new(-68, 12, -56, 116, 96, 73),
  [11] = Vertex.new(-68, 0, -56, 116, 96, 73),
  [15] = Vertex.new(-36, 12, -24, 116, 96, 73),
  [17] = Vertex.new(-36, 0, -24, 116, 96, 73),
})
local redHerring = Model.new(174, {
  [157] = Vertex.new(-116, 20, -16, 95, 87, 87),
  [160] = Vertex.new(-116, -8, -16, 95, 87, 87),
  [165] = Vertex.new(-80, 16, -44, 203, 70, 61),
  [167] = Vertex.new(-48, 28, -52, 203, 70, 61),
  [171] = Vertex.new(-60, -16, -64, 203, 70, 61),
})
local emptyJug = Model.new(366, {
  [1] = Vertex.new(-24, 120, 0, 95, 87, 87),
  [2] = Vertex.new(24, 120, 0, 95, 87, 87),
  [3] = Vertex.new(0, 120, -24, 95, 87, 87),
  [5] = Vertex.new(0, 120, 24, 95, 87, 87),
  [7] = Vertex.new(52, 0, -4, 95, 87, 87),
  [8] = Vertex.new(0, 4, 0, 95, 87, 87),
  [9] = Vertex.new(4, 0, -52, 95, 87, 87),
  [12] = Vertex.new(-4, 0, -52, 95, 87, 87),
  [15] = Vertex.new(-52, 0, -4, 95, 87, 87),
  [18] = Vertex.new(-52, 0, 4, 95, 87, 87),
  [21] = Vertex.new(-4, 0, 52, 95, 87, 87),
  [24] = Vertex.new(4, 0, 52, 95, 87, 87),
  [25] = Vertex.new(28, 104, -8, 95, 87, 87),
  [26] = Vertex.new(48, 112, 8, 95, 87, 87),
  [27] = Vertex.new(48, 112, -8, 95, 87, 87),
  [29] = Vertex.new(28, 104, 8, 95, 87, 87),
  [31] = Vertex.new(52, 0, 4, 95, 87, 87),
  [37] = Vertex.new(-4, 136, -28, 95, 87, 87),
  [39] = Vertex.new(12, 136, -28, 95, 87, 87),
  [45] = Vertex.new(24, 136, -16, 95, 87, 87),
})
local emptyBucket = Model.new(582, {
  [77] = Vertex.new(64, 140, -52, 77, 56, 15),
  [85] = Vertex.new(68, 140, 60, 77, 56, 15),
  [88] = Vertex.new(68, 140, 60, 77, 56, 15),
  [111] = Vertex.new(64, 140, -52, 90, 71, 36),
  [123] = Vertex.new(68, 140, 60, 90, 71, 36),
})
local stickyRedGoop = Model.new(45, {
  [3] = Vertex.new(-92, 0, 120, 104, 26, 20),
  [9] = Vertex.new(92, 0, 124, 104, 26, 20),
  [15] = Vertex.new(120, 0, -128, 104, 26, 20),
  [21] = Vertex.new(-32, 0, 172, 104, 26, 20),
  [27] = Vertex.new(-124, 0, -136, 104, 26, 20),
})
local vaseLid = Model.new(54, {
  [21] = Vertex.new(8, 0, -76, 54, 48, 34),
  [24] = Vertex.new(8, 0, -76, 54, 48, 34),
  [32] = Vertex.new(76, 0, 36, 54, 48, 34),
  [39] = Vertex.new(76, 0, 36, 54, 48, 34),
  [45] = Vertex.new(76, 0, 36, 54, 48, 34),
})
local fullJug = Model.new(366, {
  [1] = Vertex.new(-24, 120, 0, 73, 82, 116),
  [2] = Vertex.new(24, 120, 0, 73, 82, 116),
  [3] = Vertex.new(0, 120, -24, 73, 82, 116),
  [5] = Vertex.new(0, 120, 24, 73, 82, 116),
  [7] = Vertex.new(52, 0, -4, 95, 87, 87),
  [8] = Vertex.new(0, 4, 0, 95, 87, 87),
  [9] = Vertex.new(4, 0, -52, 95, 87, 87),
  [12] = Vertex.new(-4, 0, -52, 95, 87, 87),
  [15] = Vertex.new(-52, 0, -4, 95, 87, 87),
  [18] = Vertex.new(-52, 0, 4, 95, 87, 87),
  [21] = Vertex.new(-4, 0, 52, 95, 87, 87),
  [24] = Vertex.new(4, 0, 52, 95, 87, 87),
  [25] = Vertex.new(28, 104, -8, 95, 87, 87),
  [26] = Vertex.new(48, 112, 8, 95, 87, 87),
  [27] = Vertex.new(48, 112, -8, 95, 87, 87),
  [29] = Vertex.new(28, 104, 8, 95, 87, 87),
  [31] = Vertex.new(52, 0, 4, 95, 87, 87),
  [37] = Vertex.new(-4, 136, -28, 95, 87, 87),
  [39] = Vertex.new(12, 136, -28, 95, 87, 87),
  [45] = Vertex.new(24, 136, -16, 95, 87, 87),
})
local threeFifthsBucket = Model.new(570, {
  [2] = Vertex.new(62, 117, 50, 7, 42, 80),
  [4] = Vertex.new(62, 117, 50, 7, 42, 80),
  [7] = Vertex.new(62, 117, -54, 7, 42, 80),
  [14] = Vertex.new(62, 117, -54, 7, 42, 80),
  [19] = Vertex.new(-58, 117, -54, 7, 42, 80),
})
local vase = Model.new(306, {
  [29] = Vertex.new(4, 300, -76, 69, 64, 52),
  [35] = Vertex.new(4, 300, -76, 69, 64, 52),
  [57] = Vertex.new(4, 300, -76, 57, 53, 43),
  [60] = Vertex.new(4, 300, -76, 57, 53, 43),
  [261] = Vertex.new(92, 300, -36, 80, 74, 61),
})
local sealedVase = Model.new(234, {
  [3] = Vertex.new(-52, 304, 48, 63, 58, 48),
  [8] = Vertex.new(-52, 304, 48, 63, 58, 48),
  [29] = Vertex.new(4, 300, -76, 69, 64, 52),
  [78] = Vertex.new(-52, 304, 48, 54, 48, 34),
  [83] = Vertex.new(-52, 304, 48, 54, 48, 34),
})
local frozenKey = Model.multi({
  Model.new(333, {
    [122] = Vertex.new(32, 36, 60, 69, 64, 52),
    [129] = Vertex.new(32, 36, 60, 69, 64, 52),
    [139] = Vertex.new(68, 36, 80, 69, 64, 52),
    [145] = Vertex.new(68, 36, 80, 69, 64, 52),
    [151] = Vertex.new(84, 36, 64, 69, 64, 52),
  }),
  Model.new(234, {
    [2] = Vertex.new(0, 204, -36, 139, 149, 152, 0.3725),
    [6] = Vertex.new(0, 204, -36, 139, 149, 152, 0.3725),
    [10] = Vertex.new(24, 204, 16, 139, 149, 152, 0.3725),
    [25] = Vertex.new(24, 204, 16, 139, 149, 152, 0.3725),
    [28] = Vertex.new(4, 204, 36, 139, 149, 152, 0.3725),
  }),
})
local seersKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 101, 78, 51),
  [104] = Vertex.new(68, 16, 68, 101, 78, 51),
  [397] = Vertex.new(68, 16, 68, 101, 78, 51),
  [400] = Vertex.new(68, 16, 68, 101, 78, 51),
  [408] = Vertex.new(68, 16, 68, 101, 78, 51),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Brundt the Chieftain in the longhall in Rellekka.",
    title = "The trials",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(2658.5, 2085, 3668, { distance = 5 }),
      Action.ModelHighlight:new(Models.npcs["brundt the chieftain"], { distance = 5 }),
      Action.ConversationHighlight:new("Do you have any quests?"),
      Action.ConversationHighlight:new("Talk about something else"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue speaking to Brundt the Chieftain.",
    actions = {
      Action.Direction:new(2658.5, 2085, 3668, { distance = 5 }),
      Action.ModelHighlight:new(Models.npcs["brundt the chieftain"], { distance = 5 }),
      Action.ConversationHighlight:new("I want to become a Fremennik!"),
    },
    postconditions = { Condition.ConversationInactive:new() }, --not tested
  },
  {
    text = "Talk to Manni the Reveller.",
    title = "Manni the Reveller",
    neededItems = {
      ["Beer"] = { quantity = 1 },
      ["Coins"] = { quantity = 250 },
    },
    actions = {
      Action.ModelHighlight:new(Models.npcs["manni the reveller"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("drink no more and yields") },
  },
  {
    text = "Pick up a Keg of beer.",
    actions = { Action.ModelHighlight:new(kegOfBeer) },
    postconditions = { Condition.InventoryContains:new(kegOfBeer) },
  },
  {
    text = "Pick up a beer.",
    actions = { Action.ModelHighlight:new(Models.items["beer"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["beer"]) },
  },
  {
    text = "Talk to the Poison Salesman at Forester's Arms in Seers' Village.",
    actions = {
      Action.Direction:new(2695, 1157, 3493, { distance = 5 }),
      Action.ModelHighlight:new(Models.npcs["poison salesman"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about the Fremennik Trials"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("Yes please") },
  },
  {
    text = "Use beer on council workman at the bridge south of Rellekka.",
    actions = {
      Action.Direction:new(2654, 901, 3591, { distance = 12 }),
      Action.ModelHighlight:new(councilWorkman, { distance = 12 }),
      Action.InventoryHighlight:new(Models.items["beer"]),
    },
    postconditions = { Condition.InventoryContains:new(strangeObject) },
  },
  {
    text = "<i>Light</i> the strange object and <i>put-inside pipe</i> on the outside eastern wall of the Rellekka longhall.",
    actions = {
      Action.Direction:new(2662.65, 2233, 3673.85),
      Action.InventoryHighlight:new(strangeObject),
    },
    postconditions = { Condition.ConversationText:new("That is going") },
  },
  {
    text = "Stand next to Manni in the longhall.",
    actions = { Action.Direction:new(2658, 2085, 3674) },
    postconditions = { Condition.DistanceTo:new(2658, 2085, 3674, 3) },
  },
  {
    text = "<i>Use</i> low alcohol keg on the keg of beer in the backpack.",
    actions = {
      Action.InventoryHighlight:new(kegOfBeer),
    },
    postconditions = { Condition.ChatText:new("refill it with with low alcohol beer") },
  },
  {
    text = "Challenge Manni.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["manni the reveller"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("counsh... gets my vote") },
  },
  {
    text = "Speak to Olaf the Bard, east of the longhall.",
    title = "Olaf the Bard",
    neededItems = {
      ["Raw potato"] = { quantity = 1 },
      ["Onion"] = { quantity = 1 },
      ["Raw shark, raw manta ray, or raw sea turtle"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(2672, 1253, 3682, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["olaf the bard"], { distance = 12 }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("more music lovers") },
  },
  {
    text = "Chop off a branch of the swaying tree to the south-east.",
    actions = {
      Action.Direction:new(2738.5, 2389, 3638, { distance = 16 }),
      Action.ModelHighlight:new(swayingTree, { distance = 16 }),
    },
    postconditions = { Condition.InventoryContains:new(swayingTreeBranch) },
  },
  {
    text = "Craft the branch into an unstrung lyre.",
    actions = { Action.InventoryHighlight:new(swayingTreeBranch) },
    postconditions = { Condition.InventoryContains:new(unstrungLyre) },
  },
  {
    text = "Talk to Lalli to the south-east.",
    actions = {
      Action.Direction:new(2770, 1189, 3621, { distance = 16 }),
      Action.ModelHighlight:new(lalli, { distance = 16 }),
      Action.ConversationHighlight:new("Other human?"),
    },
    postconditions = { Condition.ConversationText:new("okay, well, bye") },
  },
  {
    text = "Talk to Askeladden south of the longhall.",
    actions = {
      Action.Direction:new(2658, 1309, 3659, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["askeladden"], { distance = 8 }),
    },
    postconditions = { Condition.InventoryContains:new(petRock) },
  },
  {
    text = "Grab a raw potato, an onion, and cabbage from the farm in south-east Rellekka.<ul><li>Grab an extra onion and cabbage in case you accidentally eat them.</li></ul>",
    actions = { Action.Direction:new(2674, 1053, 3655) },
    postconditions = { Condition.InventoryContains:new(Models.items["onion"]) },
  },
  {
    -- text = "debug0",
    actions = { Action.Direction:new(2674, 1101, 3653) },
    postconditions = { Condition.InventoryContains:new(Models.items["raw potato"]) },
  },
  {
    -- text = "debug1",
    actions = { Action.Direction:new(2674, 1125, 3651) },
    postconditions = { Condition.InventoryContains:new(Models.items["cabbage"]) },
  },
  {
    text = "<i>Use</i> the potato, onion, cabbage, and pet rock on Lalli's Stew.",
    actions = {
      Action.Direction:new(2772, 1165, 3623.5, { distance = 20 }),
      Action.ModelHighlight:new(lallisStew, { distance = 20 }),
      Action.InventoryHighlight:new(Models.items["onion"], true),
    },
    postconditions = { Condition.ChatText:new("onion into the cauldron") },
  },
  {
    -- text = "debug0",
    actions = {
      Action.Direction:new(2772, 1165, 3623.5, { distance = 20 }),
      Action.ModelHighlight:new(lallisStew, { distance = 20 }),
      Action.InventoryHighlight:new(Models.items["raw potato"], true),
    },
    postconditions = { Condition.ChatText:new("potato into the cauldron") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.Direction:new(2772, 1165, 3623.5, { distance = 20 }),
      Action.ModelHighlight:new(lallisStew, { distance = 20 }),
      Action.InventoryHighlight:new(Models.items["cabbage"], true),
    },
    postconditions = { Condition.ChatText:new("cabbage into the cauldron") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.Direction:new(2772, 1165, 3623.5, { distance = 20 }),
      Action.ModelHighlight:new(lallisStew, { distance = 20 }),
      Action.InventoryHighlight:new(petRock, true),
    },
    postconditions = { Condition.ChatText:new("pet rock into the cauldron") },
  },
  {
    text = "Talk to Lalli to obtain a golden fleece.<ul><li>Use the drop trick to obtain another if you plan on completing Kili's Knowledge VII.</li></ul>",
    actions = {
      Action.Direction:new(2770, 1189, 3621, { distance = 16 }),
      Action.ModelHighlight:new(lalli, { distance = 16 }),
    },
    postconditions = { Condition.InventoryContains:new(goldenFleece) },
  },
  {
    text = "<i>Use</i> the golden fleece on a spinning wheel in south-west Rellekka.",
    actions = {
      Action.Direction:new(2617, 1041, 3659),
      Action.InventoryHighlight:new(goldenFleece),
    },
    postconditions = { Condition.InventoryContains:new(goldenWool) },
  },
  {
    text = "<i>Use</i> the wool on the unstrung lyre.",
    actions = {
      Action.InventoryHighlight:new(goldenWool),
      Action.InventoryHighlight:new(unstrungLyre),
    },
    postconditions = { Condition.ChatText:new("golden strings to the lyre") },
  },
  {
    text = "<i>Use</i> your raw shark, manta ray or sea turtle on the strange altar south-west of Rellekka.",
    actions = {
      Action.Direction:new(2626, 453, 3598),
      Action.InventoryHighlight:new(rawShark), --i don't have a manta ray or sea turtle to highlight
    },
    postconditions = { Condition.ConversationText:new("Many thanks") },
  },
  {
    text = "Enter the backstage of the longhall through the north-east door.",
    actions = { Action.Direction:new(2666.5, 2485, 3683) },
    postconditions = { Condition.DistanceTo:new(2665, 2085, 3683, 1) },
  },
  {
    text = "Step on stage and play the enchanted lyre.",
    actions = {
      Action.Direction:new(2658.5, 2565, 3683.5),
      Action.InventoryHighlight:new(unstrungLyre),
    },
    postconditions = { Condition.ConversationText:new("certainly earned my vote") },
  },
  {
    text = "Talk to Sigli the Huntsman near Rellekka's southern entrance.",
    title = "Sigli the Huntsman",
    neededItems = { ["Coins"] = { quantity = 5000 } },
    actions = {
      Action.Direction:new(2661, 1221, 3652, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["sigli the huntsman"], { distance = 16 }),
      Action.ConversationHighlight:new("What's a Draugen?"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("have completed this task") },
  },
  {
    text = "Use the received hunters' talisman to locate and kill The Draugen.<ul><li>The Draugen is not visible until you get close to his location.</li><li>Look for a tiny grey butterfly.</li><li>Once next to the butterfly, click on the talisman.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(huntersTalisman),
      Action.ModelHighlight:new(draugen),
    },
    postconditions = { Condition.ChatText:new("absorb the Draugen") },
  },
  {
    text = "Return to Sigli to receive his vote.",
    actions = {
      Action.Direction:new(2661, 1221, 3652, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["sigli the huntsman"], { distance = 16 }),
    },
    postconditions = { Condition.ConversationText:new("Thanks") },
  },
  {
    text = "Talk to Sigmund The Merchant in the marketplace.",
    title = "Sigmund The Merchant",
    actions = {
      Action.Direction:new(2641, 645, 3680, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["sigmund the merchant"], { distance = 16 }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("to find whatever you need") },
  },
  {
    text = "Talk to the Sailor on the middle dock.",
    actions = {
      Action.Direction:new(2629, 325, 3693, { distance = 16 }),
      Action.ModelHighlight:new(sailor, { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("sounds like a fair deal to me") },
  },
  {
    text = "Talk to Olaf the Bard, east of the longhall.",
    actions = {
      Action.Direction:new(2672, 1253, 3682, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["olaf the bard"], { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("I will be happy to spend the time on composing") },
  },
  {
    text = "Talk to Yrsa in the clothing store, west of the marketplace.",
    actions = {
      Action.Direction:new(2624, 741, 3674, { distance = 2 }),
      Action.ModelHighlight:new(Models.npcs["yrsa"], { distance = 3 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("I will see what I can do") },
  },
  {
    text = "Talk to Brundt the Chieftain in the longhall.",
    actions = {
      Action.Direction:new(2658.5, 2085, 3668, { distance = 5 }),
      Action.ModelHighlight:new(Models.npcs["brundt the chieftain"], { distance = 5 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("best of luck with the rest of your trials") },
  },
  {
    text = "Talk to Sigli the Huntsman, south of the longhall.",
    actions = {
      Action.Direction:new(2661, 1221, 3652, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["sigli the huntsman"], { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("who knows where my hunting ground is") },
  },
  {
    text = "Talk to Skulgrimen, north of the longhall.",
    actions = {
      Action.Direction:new(2665, 1093, 3692.5, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["skulgrimen"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("Sounds good to me") },
  },
  {
    text = "Talk to the Fisherman, north of the marketplace.",
    actions = {
      Action.Direction:new(2641, 325, 3699, { distance = 16 }),
      Action.ModelHighlight:new(fisherman, { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Talk to Swensen the Navigator, south of the market.",
    actions = {
      Action.Direction:new(2646.5, 1061, 3659.5, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["swensen the navigator"], { distance = 4 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("time to make a forecast somehow") },
  },
  {
    text = "Talk to Peer the Seer, west of Swensen.",
    actions = {
      Action.Direction:new(2634, 901, 3669, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["peer the seer"], { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
      Action.ConversationHighlight:new("The Fremennik Trials"),
    },
    postconditions = { Condition.ConversationText:new("That is all") },
  },
  {
    text = "Talk to Thorvald the Warrior, north of the longhall.",
    actions = {
      Action.Direction:new(2665, 1093, 3692.5, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["thorvald the warrior"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Talk to Manni the Reveller in the longhall.",
    actions = {
      Action.Direction:new(2658, 2085, 3674, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["manni the reveller"], { distance = 9 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("That's all") },
  },
  {
    text = "Talk to Thora the Barkeep in the longhall.",
    actions = {
      Action.Direction:new(2662, 2085, 3673, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["thora the barkeep"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("go ask him yourself though") },
  },
  {
    text = "Talk to Askeladden outside the longhall.",
    actions = {
      Action.Direction:new(2658, 1309, 3659, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["askeladden"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("I can relieve you of") },
  },
  {
    text = "Talk to Thora the Barkeep.",
    actions = {
      Action.Direction:new(2662, 2085, 3673, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["thora the barkeep"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("called Askeladden coming in here") },
  },
  {
    text = "Talk to Manni the Reveller.",
    actions = {
      Action.Direction:new(2658, 2085, 3674, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["manni the reveller"], { distance = 9 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("happy with the trade then") },
  },
  {
    text = "Talk to Thorvald the Warrior.",
    actions = {
      Action.Direction:new(2665, 1093, 3692.5, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["thorvald the warrior"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("I will fulfill it to my utmost") },
  },
  {
    text = "Talk to Peer the Seer.",
    actions = {
      Action.Direction:new(2634, 901, 3669, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["peer the seer"], { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
      Action.ConversationHighlight:new("The Fremennik Trials"),
    },
    postconditions = { Condition.ConversationText:new("greatest security I can imagine") },
  },
  {
    text = "Talk to Swensen the Navigator.",
    actions = {
      Action.Direction:new(2646.5, 1061, 3659.5, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["swensen the navigator"], { distance = 4 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("local fishing patterns with my gratitude") },
  },
  {
    text = "Talk to the Fisherman.",
    actions = {
      Action.Direction:new(2641, 325, 3699, { distance = 16 }),
      Action.ModelHighlight:new(fisherman, { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("have the stupid fish") },
  },
  {
    text = "Talk to Skulgrimen.",
    actions = {
      Action.Direction:new(2665, 1093, 3692.5, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["skulgrimen"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("Thanks") },
  },
  {
    text = "Talk to Sigli.",
    actions = {
      Action.Direction:new(2661, 1221, 3652, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["sigli the huntsman"], { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("dependent on it for my skill anyway") },
  },
  {
    text = "Talk to Brundt.",
    actions = {
      Action.Direction:new(2658.5, 2085, 3668, { distance = 5 }),
      Action.ModelHighlight:new(Models.npcs["brundt the chieftain"], { distance = 5 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("reduced sales taxes on all") },
  },
  {
    text = "Talk to Yrsa.",
    actions = {
      Action.Direction:new(2624, 741, 3674, { distance = 2 }),
      Action.ModelHighlight:new(Models.npcs["yrsa"], { distance = 3 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("come to me next time for shoes") },
  },
  {
    text = "Talk to Olaf.",
    actions = {
      Action.Direction:new(2672, 1253, 3682, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["olaf the bard"], { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("one of my finest works yet") },
  },
  {
    text = "Talk to the Sailor.",
    actions = {
      Action.Direction:new(2629, 325, 3693, { distance = 16 }),
      Action.ModelHighlight:new(sailor, { distance = 16 }),
      Action.ConversationHighlight:new("Ask about the Merchant's trial"),
    },
    postconditions = { Condition.ConversationText:new("or my chances are worse than ever") },
  },
  {
    text = "Finally, talk to Sigmund.",
    actions = {
      Action.Direction:new(2641, 645, 3680, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["sigmund the merchant"], { distance = 16 }),
    },
    postconditions = { Condition.ConversationText:new("recommend you to the council of elders") },
  },
  {
    text = "Talk to Swensen the Navigator in his house south-west of the longhall.",
    title = "Swensen the Navigator",
    actions = {
      Action.Direction:new(2646, 1061, 3660, { distance = 6 }),
      Action.ModelHighlight:new(Models.npcs["swensen the navigator"], { distance = 6 }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("Watch and learn") },
  },
  {
    text = "Go down the ladder.",
    actions = { Action.Direction:new(2644, 1061, 3657) },
    postconditions = { Condition.DistanceTo:new(2631, 741, 10006, 8) },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(2631, 597, 10002) },
    postconditions = { Condition.DistanceTo:new(2642, 1021, 10017, 2) },
  },
  {
    text = "West.",
    actions = { Action.Direction:new(2639, 893, 10015) },
    postconditions = { Condition.DistanceTo:new(2651, 749, 10004, 2) },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(2656, 749, 10004) },
    postconditions = { Condition.DistanceTo:new(2667, 797, 10015, 2) },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(2665, 837, 10018) },
    postconditions = { Condition.DistanceTo:new(2630, 1277, 10028, 2) },
  },
  {
    text = "South.",
    actions = { Action.Direction:new(2630, 1205, 10023) },
    postconditions = { Condition.DistanceTo:new(2653, 709, 10035, 2) },
  },
  {
    text = "East.",
    actions = { Action.Direction:new(2656, 613, 10037) },
    postconditions = { Condition.DistanceTo:new(2668, 861, 10026, 2) },
  },
  {
    text = "North.",
    actions = { Action.Direction:new(2666, 1093, 10029) },
    postconditions = { Condition.DistanceTo:new(2665, 997, 10038, 3) },
  },
  {
    text = "Climb up the ladder, then talk to Swensen to receive his vote.",
    actions = { Action.Direction:new(2665, 1297, 10037) },
    postconditions = { Condition.DistanceTo:new(2649, 1061, 3661, 2) },
  },
  { postconditions = { Condition.ConversationText:new("Of course outerlander") } },
  {
    text = "Talk to Thorvald the Warrior north of the longhall.<ul><li>You are not allowed to bring in weapons, armour, or beasts of burden.</li><li>You can bring potions, food, rings of recoil, <i>already summoned</i> familiars, and you can remove your pickaxe/hatchet from your tool belt.</li></ul>",
    title = "Thorvald the Warrior",
    actions = {
      Action.Direction:new(2665, 1093, 3692.5, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["thorvald the warrior"], { distance = 8 }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("spell that will do that for you") },
  },
  {
    text = "Climb down the ladder and wait for him to appear to begin the fight.",
    actions = { Action.Direction:new(2667, 1093, 3694) },
    postconditions = { Condition.DistanceTo:new(2671, 2245, 10098, 5) },
  },
  {
    text = "Defeat Koschei the Deathless three times. Either kill him the fourth time or let yourself be defeated. <b>You will not die.</b>",
    actions = {}, --i forgot to get koschei's model to highlight :>
    postconditions = { Condition.ChatText:new("have completed the warriors trial") },
  },
  {
    text = "Talk to Thorvald to receive his vote.",
    actions = { Action.ModelHighlight:new(Models.npcs["thorvald the warrior"]) },
    postconditions = { Condition.ConversationText:new("impressed with your bravery in combat") },
  },
  {
    text = "Speak to Peer the Seer, south-west of the market in Rellekka.",
    title = "Peer the Seer",
    actions = {
      Action.Direction:new(2634, 901, 3669, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["peer the seer"], { distance = 16 }),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("The Fremennik Trials"),
    },
    postconditions = {
      Condition.ConversationText:new("The task is done"),
      Condition.ConversationText:new("I am sorry outerlander"),
      Condition.ConversationText:new("As you wish"),
    },
  },
  {
    text = "Click on the door to the house. Read the riddle. The first sentence determines the answer:<br/><table><tbody><tr><th>Riddle</th><th>Answer</th></tr><tr><td>My first is in mage</td><td>Mind</td></tr><tr><td>My first is in tar</td><td>Tree</td></tr><tr><td>My first is in the well</td><td>Life</td></tr><tr><td>My first is in fish</td><td>Fire</td></tr><tr><td>My first is in water</td><td>Time</td></tr><tr><td>My first is in wizard</td><td>Wind</td></tr></tbody></table>",
    actions = { Action.Direction:new(2631, 1661, 3666.5) },
    postconditions = { Condition.ChatText:new("You have solved the riddle!") },
  },
  {
    text = "Enter the building and climb the ladder.",
    actions = { Action.Direction:new(2631, 1661, 3663.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2631, 2565, 3662, 5) },
  },
  {
    text = "Open then search the south-west cupboard.",
    actions = { Action.Direction:new(2629.5, 3065, 3660) },
    postconditions = { Condition.InventoryContains:new(emptyBucket) },
  },
  {
    text = "Study the unicorn's head.",
    actions = { Action.Direction:new(2632, 3065, 3660) },
    postconditions = { Condition.InventoryContains:new(oldRedDisk) },
  },
  {
    text = "Study the bull's head.",
    actions = { Action.Direction:new(2634, 3065, 3660) },
    postconditions = { Condition.InventoryContains:new(woodenDisk) },
  },
  {
    text = "Open then search the eastern chest.",
    actions = { Action.Direction:new(2638, 2565, 3662) },
    postconditions = { Condition.InventoryContains:new(emptyJug) },
  },
  {
    text = "Search the bookcase.",
    actions = { Action.Direction:new(2634, 3065, 3665.2) },
    postconditions = { Condition.InventoryContains:new(redHerring) },
  },
  {
    text = "<i>Use</i> the red herring on the cooking range.",
    actions = {
      Action.Direction:new(2629, 3065, 3663),
      Action.InventoryHighlight:new(redHerring),
    },
    postconditions = { Condition.InventoryContains:new(stickyRedGoop) },
  },
  {
    text = "<i>Use</i> the goop on the wooden disk.",
    actions = {
      Action.InventoryHighlight:new(woodenDisk),
      Action.InventoryHighlight:new(stickyRedGoop),
    },
    postconditions = { Condition.InventoryContains:new(oldRedDisk, 2) },
  },
  {
    text = "Go down the eastern trapdoor and <i>use</i> the two disks on the abstract mural.",
    actions = { Action.Direction:new(2636, 2565, 3663) },
    postconditions = { Condition.DistanceTo:new(2636, 1061, 3662, 4) },
  },
  {
    actions = {
      Action.Direction:new(2633.66, 1761, 3663),
      Action.InventoryHighlight:new(oldRedDisk),
    },
    postconditions = { Condition.InventoryContains:new(vaseLid) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(2636, 1661, 3663.5) },
    postconditions = { Condition.DistanceTo:new(2636, 2565, 3662, 4) },
  },
  {
    text = "<i>Use</i> the empty jug on the tap.",
    actions = {
      Action.Direction:new(2628.7, 2965, 3661),
      Action.InventoryHighlight:new(emptyJug),
    },
    postconditions = { Condition.InventoryContains:new(fullJug) },
  },
  {
    text = "<i>Use</i> the jug on the empty bucket.",
    actions = {
      Action.InventoryHighlight:new(emptyBucket),
      Action.InventoryHighlight:new(fullJug),
    },
    postconditions = { Condition.InventoryContains:new(threeFifthsBucket) },
  },
  {
    text = "<i>Use</i> the empty jug on the tap again and fill the bucket.",
    actions = { Action.InventoryHighlight:new(emptyJug) },
    postconditions = { Condition.InventoryContains:new(fullJug) },
  },
  {
    text = "<i>Use</i> the full jug on the 3/5ths full bucket.",
    actions = {
      Action.InventoryHighlight:new(threeFifthsBucket),
      Action.InventoryHighlight:new(fullJug),
    },
    postconditions = { Condition.InventoryContains:new(emptyJug) },
  },
  {
    text = "<i>Use</i> the full bucket on the drain to get an empty bucket.",
    actions = { Action.InventoryHighlight:new(threeFifthsBucket) },
    postconditions = { Condition.InventoryContains:new(emptyBucket) },
  },
  {
    text = "<i>Use</i> the 1/3rds full jug on the empty bucket, then <i>Use</i> the empty jug on the tap",
    actions = {
      Action.InventoryHighlight:new(emptyBucket),
      Action.InventoryHighlight:new(emptyJug),
    },
    postconditions = { Condition.InventoryContains:new(fullJug) },
  },
  {
    text = "<i>Use</i> the full jug on the 1/5ths full bucket.",
    actions = {
      Action.InventoryHighlight:new(emptyBucket),
      Action.InventoryHighlight:new(fullJug),
    },
    postconditions = { Condition.InventoryContains:new(emptyJug) },
  },
  {
    text = "<i>Use</i> the 4/5ths full bucket on the northwestern chest.",
    actions = {
      Action.InventoryHighlight:new(threeFifthsBucket),
      Action.Direction:new(2632, 2765, 3664.8),
    },
    postconditions = { Condition.InventoryContains:new(vase) },
  },
  {
    text = "<i>Use</i> the vase on the tap.",
    actions = {
      Action.Direction:new(2628.7, 2965, 3661),
      Action.InventoryHighlight:new(vase, true),
    },
    postconditions = { Condition.ChatText:new("You fill the strange looking vase with water") },
  },
  {
    text = "<i>Use</i> the vase lid on the vase of water.",
    actions = { Action.InventoryHighlight:new(vaseLid) },
    postconditions = { Condition.InventoryContains:new(sealedVase) },
  },
  {
    text = "<i>Use</i> the sealed vase on the frozen table.",
    actions = {
      Action.Direction:new(2638, 2965, 3665),
      Action.InventoryHighlight:new(sealedVase, true),
    },
    postconditions = { Condition.InventoryContains:new(frozenKey) },
  },
  {
    text = "<i>Use</i> the frozen key on the range.",
    actions = {
      Action.Direction:new(2629, 3065, 3663),
      Action.InventoryHighlight:new(frozenKey),
    },
    postconditions = { Condition.InventoryContains:new(seersKey) },
  },
  {
    text = "Go down the trapdoor and exit the building.",
    actions = { Action.Direction:new(2636, 2565, 3663) },
    postconditions = { Condition.DistanceTo:new(2636, 1061, 3662, 4) },
  },
  {
    actions = { Action.Direction:new(2636, 1661, 3666.5) },
    postconditions = { Condition.ConversationText:new("vote in your favour") },
  },
  {
    text = "Talk to Brundt the Chieftain in the longhall in Rellekka.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(2658.5, 2085, 3668, { distance = 5 }),
      Action.ModelHighlight:new(Models.npcs["brundt the chieftain"], { distance = 5 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Fremennik Trials",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1099353600,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Crafting", 40),
    Types.QuestReq.skill("Fletching", 25),
    Types.QuestReq.skill("Woodcutting", 40),
    Types.QuestReq.misc("The quickest way to get a shark on an ironman is to kill a Mogre, requiring 32 slayer."),
  },
  neededItems = {
    ["Coins"] = { quantity = 5250 },
    ["Raw shark, raw manta ray, or raw sea turtle"] = { quantity = 1 },
    ["A beer"] = { quantity = 1, model = Models.items["beer"], duringQuest = true },
    ["Raw potato"] = { quantity = 1, model = Models.items["raw potato"], duringQuest = true },
    ["Cabbage"] = { quantity = 1, model = Models.items["cabbage"], duringQuest = true },
    ["Onion"] = { quantity = 1, model = Models.items["onion"], duringQuest = true },
  },
  recommendedItems = { ["Ring of recoil"] = { quantity = 1 } },
  combatNPCs = {
    ["Koschei the Deathless"] = { level = "N/A" },
    ["The Draugen"] = { level = "96" },
  },
})
