local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--#region NPCs
local dukeHoarse = Model.new(12843, {
  [1] = Vertex.new(0, 735, -7, 127, 128, 128),
  [2] = Vertex.new(0, 732, -7, 127, 128, 128),
  [3] = Vertex.new(0, 732, -8, 127, 128, 128),
  [4] = Vertex.new(126, 380, -15, 127, 128, 127),
  [6] = Vertex.new(124, 380, -17, 127, 128, 127),
})
local rodney = Model.new(20916, {
  [3682] = Vertex.new(-145, 872, 159, 128, 127, 127),
  [3797] = Vertex.new(-114, 915, 157, 128, 127, 127),
  [3801] = Vertex.new(-114, 915, 157, 128, 127, 127),
  [3802] = Vertex.new(-114, 915, 157, 128, 127, 127),
  [5401] = Vertex.new(-53, 890, 109, 128, 128, 128),
})
local roald = Model.new(13440, {
  [9070] = Vertex.new(-57, 461, -29, 127, 127, 127),
  [9233] = Vertex.new(12, 454, -59, 127, 127, 127),
  [9260] = Vertex.new(57, 461, -29, 127, 127, 127),
  [10190] = Vertex.new(0, 697, 35, 127, 127, 127),
  [11090] = Vertex.new(0, 697, 35, 127, 127, 127),
})
local bianca = Model.new(4719, {
  [1767] = Vertex.new(24, 738, -38, 32, 30, 29),
  [1791] = Vertex.new(-24, 738, -38, 32, 30, 29),
  [1813] = Vertex.new(-2, 716, -57, 71, 52, 37),
  [1819] = Vertex.new(2, 716, -57, 71, 52, 37),
  [1823] = Vertex.new(6, 716, -52, 71, 52, 37),
})
local simon = Model.new(15480, {
  [11790] = Vertex.new(-46, 575, -72, 127, 127, 127),
  [11792] = Vertex.new(-46, 575, -72, 127, 127, 127),
  [11794] = Vertex.new(-46, 575, -72, 127, 127, 127),
  [11848] = Vertex.new(-35, 527, -65, 127, 127, 127),
  [13702] = Vertex.new(0, 707, 44, 127, 127, 127),
})
local duchessAlba = Model.new(14217, {
  [13] = Vertex.new(-76, 643, 19, 128, 128, 127),
  [16] = Vertex.new(76, 643, 19, 128, 128, 127),
  [40] = Vertex.new(0, 735, -7, 127, 128, 128),
  [41] = Vertex.new(0, 732, -7, 127, 128, 128),
  [42] = Vertex.new(0, 732, -8, 127, 128, 128),
})
local assassin = Model.new(12024, {
  [923] = Vertex.new(126, 424, 20, 127, 127, 127),
  [929] = Vertex.new(126, 424, 20, 127, 127, 127),
  [947] = Vertex.new(-126, 424, 20, 127, 127, 127),
  [1065] = Vertex.new(116, 623, 68, 127, 127, 127),
  [1168] = Vertex.new(86, 643, 66, 127, 127, 127),
})
local deadAssassin = Model.new(12024, { --rip bozo
  [923] = Vertex.new(126, 424, 20, 127, 127, 127),
  [929] = Vertex.new(126, 424, 20, 127, 127, 127),
  [947] = Vertex.new(-126, 424, 20, 127, 127, 127),
  [1065] = Vertex.new(116, 623, 68, 127, 127, 127),
  [1168] = Vertex.new(86, 643, 66, 127, 127, 127),
})
local iris = Model.new(3960, {
  [2834] = Vertex.new(6, 742, -50, 37, 27, 15),
  [2847] = Vertex.new(-6, 742, -50, 37, 27, 15),
  [3920] = Vertex.new(-26, 740, -37, 59, 44, 24),
  [3921] = Vertex.new(-26, 735, -28, 59, 44, 24),
  [3923] = Vertex.new(26, 740, -37, 59, 44, 24),
})
local ellamaria = Model.new(19344, {
  [35] = Vertex.new(27, 758, -25, 43, 20, 4),
  [66] = Vertex.new(-27, 759, -26, 43, 20, 4),
  [257] = Vertex.new(26, 761, -30, 43, 20, 4),
  [2321] = Vertex.new(-25, 764, -31, 43, 20, 4),
  [2323] = Vertex.new(-25, 764, -31, 43, 20, 4),
})
--#endregion
--#region Objects
local kitchenWall = Model.new(27444, {
  [9808] = Vertex.new(1997, 103, 1849, 127, 127, 127),
  [12602] = Vertex.new(2074, 1448, 2176, 127, 127, 127),
  [14570] = Vertex.new(2074, 1448, -1664, 127, 127, 127),
  [20795] = Vertex.new(2074, 1448, 1920, 127, 127, 127),
  [20933] = Vertex.new(2043, 3, 2170, 127, 127, 127),
})
local spiralStaircase = Model.new(5796, {
  [3905] = Vertex.new(234, 381, -725, 127, 127, 127),
  [4718] = Vertex.new(-668, 1138, 427, 127, 127, 127),
  [4790] = Vertex.new(223, 385, -737, 127, 127, 127),
  [4862] = Vertex.new(-634, 762, -473, 127, 127, 127),
  [5036] = Vertex.new(-611, 1221, 510, 127, 127, 127),
})
local firstFloorSpiralStaircase = Model.new(8271, {
  [389] = Vertex.new(-116, 1999, 804, 127, 127, 127),
  [407] = Vertex.new(-116, 1999, 804, 127, 127, 127),
  [6890] = Vertex.new(-729, 1690, 382, 127, 127, 127),
  [7547] = Vertex.new(-34, 2019, 779, 127, 127, 127),
  [7574] = Vertex.new(-36, 2018, 818, 127, 127, 127),
})
local nutRoast = Model.new(1098, {
  [27] = Vertex.new(-163, 418, 445, 127, 127, 127),
  [445] = Vertex.new(-187, 418, 424, 127, 127, 127),
  [451] = Vertex.new(-177, 418, 437, 127, 127, 127),
  [563] = Vertex.new(-177, 418, 437, 127, 127, 127),
  [649] = Vertex.new(-177, 418, 437, 127, 127, 127),
})
local strangeSatchel = Model.new(1350, {
  [369] = Vertex.new(20, 69, -81, 40, 21, 13),
  [483] = Vertex.new(20, 69, 78, 40, 21, 13),
  [663] = Vertex.new(20, 53, -81, 51, 26, 15),
  [774] = Vertex.new(21, 84, -99, 46, 24, 14),
  [786] = Vertex.new(-19, 68, 97, 46, 24, 14),
})
local halfBuriedBox = Model.new(1845, {
  [1375] = Vertex.new(231, 82, -42, 53, 46, 33),
  [1389] = Vertex.new(231, 82, -42, 76, 66, 48),
  [1465] = Vertex.new(114, 126, -100, 47, 47, 51),
  [1661] = Vertex.new(63, 126, -118, 79, 72, 72),
  [1828] = Vertex.new(-203, 1, 111, 140, 108, 43),
})
local supplyCupboard = Model.new(1260, {
  [307] = Vertex.new(-256, 365, 268, 127, 127, 127),
  [313] = Vertex.new(-256, 365, 268, 127, 127, 127),
  [565] = Vertex.new(-256, 365, 268, 127, 127, 127),
  [1117] = Vertex.new(-30, 307, -142, 127, 127, 127),
  [1136] = Vertex.new(30, 307, -142, 127, 127, 127),
})
local range = Model.new(3204, {
  [1307] = Vertex.new(-475, 322, 173, 127, 127, 127),
  [2360] = Vertex.new(476, 322, -161, 127, 127, 127),
  [2383] = Vertex.new(-475, 1, 187, 127, 127, 127),
  [2462] = Vertex.new(476, 322, 173, 127, 127, 127),
  [2684] = Vertex.new(476, 1, 187, 127, 127, 127),
})
local dukesMeal = Model.new(1038, {
  [121] = Vertex.new(165, 408, -491, 127, 127, 127),
  [123] = Vertex.new(176, 408, -494, 127, 127, 127),
  [132] = Vertex.new(179, 410, -483, 127, 127, 127),
  [154] = Vertex.new(165, 408, -491, 127, 127, 127),
  [752] = Vertex.new(198, 414, -542, 127, 127, 127),
})
local secondFloorSpiralStaircase = Model.new(8271, {
  [389] = Vertex.new(-116, 1999, 804, 127, 127, 127),
  [407] = Vertex.new(-116, 1999, 804, 127, 127, 127),
  [6890] = Vertex.new(-729, 1690, 382, 127, 127, 127),
  [7547] = Vertex.new(-34, 2019, 779, 127, 127, 127),
  [7574] = Vertex.new(-36, 2018, 818, 127, 127, 127),
})
local roofTrapDoor = Model.new(2316, {
  [319] = Vertex.new(265, 289, -541, 127, 127, 127),
  [321] = Vertex.new(-282, 102, -541, 127, 127, 127),
  [323] = Vertex.new(-282, 35, -541, 127, 127, 127),
  [469] = Vertex.new(-282, 33, 540, 127, 127, 127),
  [473] = Vertex.new(265, 299, 540, 127, 127, 127),
})
local waxSeal = Model.new(531, {
  [365] = Vertex.new(-30, 6, -42, 100, 92, 91),
  [389] = Vertex.new(60, 6, 16, 100, 92, 91),
  [401] = Vertex.new(46, 6, -89, 19, 18, 18),
  [515] = Vertex.new(7, 6, -103, 19, 18, 18),
})
local burntParchment = Model.new(348, {
  [6] = Vertex.new(-102, 9, -50, 154, 137, 118),
  [12] = Vertex.new(9, 9, -178, 154, 137, 118),
  [50] = Vertex.new(-91, 14, 173, 168, 150, 129),
  [80] = Vertex.new(189, 14, -45, 168, 150, 129),
  [330] = Vertex.new(-85, 37, -58, 142, 129, 109),
})
local miscItems = Model.new(759, {
  [17] = Vertex.new(-420, 6, 420, 127, 127, 127),
  [26] = Vertex.new(-420, 6, -420, 127, 127, 127),
})
local hiddenCache = Model.new(2610, {
  [535] = Vertex.new(-103, 78, 63, 127, 127, 127),
  [1496] = Vertex.new(-103, 73, 63, 127, 127, 127),
  [1652] = Vertex.new(-103, 98, 63, 127, 127, 127),
  [1784] = Vertex.new(103, 98, 63, 127, 127, 127),
  [1802] = Vertex.new(98, 97, 50, 127, 127, 127),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local poisonDetectionBase = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [2] = Vertex.new(4, 112, -12, 115, 103, 48),
    [3] = Vertex.new(4, 96, -12, 115, 103, 48),
    [4] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [7] = Vertex.new(4, 96, 12, 115, 103, 48),
    [8] = Vertex.new(-4, 112, 12, 115, 103, 48),
    [9] = Vertex.new(-4, 96, 12, 115, 103, 48),
    [13] = Vertex.new(-12, 96, -4, 115, 103, 48),
    [17] = Vertex.new(-12, 112, -4, 115, 103, 48),
    [33] = Vertex.new(12, 112, -4, 115, 103, 48),
    [36] = Vertex.new(12, 96, -4, 115, 103, 48),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 136, 137, 148, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 136, 137, 148, 0.4980),
    [7] = Vertex.new(20, 84, 4, 136, 137, 148, 0.4980),
    [11] = Vertex.new(20, 96, 4, 136, 137, 148, 0.4980),
    [31] = Vertex.new(-20, 84, -4, 136, 137, 148, 0.4980),
    [41] = Vertex.new(-20, 96, 4, 136, 137, 148, 0.4980),
    [49] = Vertex.new(-4, 68, 16, 109, 142, 119, 0.8745),
    [51] = Vertex.new(-12, 84, 4, 109, 142, 119, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 109, 142, 119, 0.8745),
    [108] = Vertex.new(4, 68, -16, 109, 142, 119, 0.8745),
    [114] = Vertex.new(12, 84, -4, 109, 142, 119, 0.8745),
    [126] = Vertex.new(16, 68, -4, 109, 142, 119, 0.8745),
    [194] = Vertex.new(12, 96, 4, 136, 137, 148, 0.4980),
    [195] = Vertex.new(4, 96, 12, 136, 137, 148, 0.4980),
    [221] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [225] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [234] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
    [237] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
  }),
})
local hollyhock = Model.new(288, {
  [9] = Vertex.new(-109, 14, 21, 128, 127, 128),
  [22] = Vertex.new(-109, 14, 21, 128, 127, 128),
  [45] = Vertex.new(-109, 15, 21, 128, 127, 128),
  [58] = Vertex.new(-109, 15, 21, 128, 127, 128),
  [65] = Vertex.new(-109, 15, 21, 128, 127, 128),
})
local unheatedPoisonDetection = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [2] = Vertex.new(4, 112, -12, 115, 103, 48),
    [3] = Vertex.new(4, 96, -12, 115, 103, 48),
    [4] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [7] = Vertex.new(4, 96, 12, 115, 103, 48),
    [8] = Vertex.new(-4, 112, 12, 115, 103, 48),
    [9] = Vertex.new(-4, 96, 12, 115, 103, 48),
    [13] = Vertex.new(-12, 96, -4, 115, 103, 48),
    [17] = Vertex.new(-12, 112, -4, 115, 103, 48),
    [33] = Vertex.new(12, 112, -4, 115, 103, 48),
    [36] = Vertex.new(12, 96, -4, 115, 103, 48),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 136, 137, 148, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 136, 137, 148, 0.4980),
    [7] = Vertex.new(20, 84, 4, 136, 137, 148, 0.4980),
    [11] = Vertex.new(20, 96, 4, 136, 137, 148, 0.4980),
    [31] = Vertex.new(-20, 84, -4, 136, 137, 148, 0.4980),
    [41] = Vertex.new(-20, 96, 4, 136, 137, 148, 0.4980),
    [49] = Vertex.new(-4, 68, 16, 33, 155, 160, 0.8745),
    [51] = Vertex.new(-12, 84, 4, 33, 155, 160, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 33, 155, 160, 0.8745),
    [108] = Vertex.new(4, 68, -16, 33, 155, 160, 0.8745),
    [114] = Vertex.new(12, 84, -4, 33, 155, 160, 0.8745),
    [126] = Vertex.new(16, 68, -4, 33, 155, 160, 0.8745),
    [194] = Vertex.new(12, 96, 4, 136, 137, 148, 0.4980),
    [195] = Vertex.new(4, 96, 12, 136, 137, 148, 0.4980),
    [221] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [225] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [234] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
    [237] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
  }),
})
local poisonDetectionPotion = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [2] = Vertex.new(4, 112, -12, 115, 103, 48),
    [3] = Vertex.new(4, 96, -12, 115, 103, 48),
    [4] = Vertex.new(-4, 96, -12, 115, 103, 48),
    [7] = Vertex.new(4, 96, 12, 115, 103, 48),
    [8] = Vertex.new(-4, 112, 12, 115, 103, 48),
    [9] = Vertex.new(-4, 96, 12, 115, 103, 48),
    [13] = Vertex.new(-12, 96, -4, 115, 103, 48),
    [17] = Vertex.new(-12, 112, -4, 115, 103, 48),
    [33] = Vertex.new(12, 112, -4, 115, 103, 48),
    [36] = Vertex.new(12, 96, -4, 115, 103, 48),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 136, 137, 148, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 136, 137, 148, 0.4980),
    [7] = Vertex.new(20, 84, 4, 136, 137, 148, 0.4980),
    [11] = Vertex.new(20, 96, 4, 136, 137, 148, 0.4980),
    [31] = Vertex.new(-20, 84, -4, 136, 137, 148, 0.4980),
    [41] = Vertex.new(-20, 96, 4, 136, 137, 148, 0.4980),
    [49] = Vertex.new(-4, 68, 16, 33, 160, 36, 0.8745),
    [51] = Vertex.new(-12, 84, 4, 33, 160, 36, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 33, 160, 36, 0.8745),
    [108] = Vertex.new(4, 68, -16, 33, 160, 36, 0.8745),
    [114] = Vertex.new(12, 84, -4, 33, 160, 36, 0.8745),
    [126] = Vertex.new(16, 68, -4, 33, 160, 36, 0.8745),
    [194] = Vertex.new(12, 96, 4, 136, 137, 148, 0.4980),
    [195] = Vertex.new(4, 96, 12, 136, 137, 148, 0.4980),
    [221] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [225] = Vertex.new(-12, 96, -4, 136, 137, 148, 0.4980),
    [234] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
    [237] = Vertex.new(-4, 96, 12, 136, 137, 148, 0.4980),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Aster in the Town Hall at Fort Forinthry.",
    title = "Starting off",
    actions = {
      Action.Direction:new(3302, 1221, 3571, { distance = 8 }),
      Action.ModelHighlight:new(NPCs["aster"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about quests..."),
      Action.ConversationHighlight:new("Talk about 'Murder on the Border'."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Aster.",
    actions = {
      Action.Direction:new(3302, 1221, 3571, { distance = 8 }),
      Action.ModelHighlight:new(NPCs["aster"], { distance = 8 }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      Condition.ConversationText:new("I'll speak to Bill then. He should be at the workshop, as usual."),
    },
  },
  {
    text = "Talk to Bill about building the Kitchen.",
    title = "Building the kitchen",
    neededItems = { ["Willow frame"] = { quantity = 12 }, ["Stone wall segment"] = { quantity = 6 } },
    actions = {
      Action.ModelHighlight:new(NPCs["bill"]),
      Action.ConversationHighlight:new("Talk about 'Murder on the Border'."),
    },
    postconditions = { Condition.ConversationText:new("Thanks Bill.") },
  },
  {
    text = "Build the Kitchen.",
    postconditions = {
      Condition.ChatText:new("You've advanced your building!"),
      Condition.ModelVisible:new(kitchenWall),
    },
  },
  {
    text = "Talk to Aster.",
    title = "The banquet",
    actions = {
      Action.Direction:new(3302, 1221, 3571, { distance = 8 }),
      Action.ModelHighlight:new(NPCs["aster"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about quests..."),
      Action.ConversationHighlight:new("Talk about 'Murder on the Border'."),
    },
    postconditions = {
      Condition.ConversationText:new("Meet me by the front gates when you are ready to greet our guests."),
    },
  },
  {
    text = "Click the light blue portal south of the well to start the cutscene.",
    actions = { Action.Direction:new(3303.5, 485, 3541.5), Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("We are all prepared to start the banquet, your grace.") },
  },
  {
    text = "Watch the cutscene and welcome the guests as they arrive.",
    actions = { Action.ResetInstance:new(), Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("To put it plainly, don't mess it up!") },
  },
  {
    text = "Talk to Duke Hoarse.",
    actions = {
      Action.ResetInstance:new(),
      Action.ConversationHighlight:new(""),
      Action.Direction:new(6, 752, 24, { distance = 6, instance = true }),
      Action.ModelHighlight:new(dukeHoarse, { distance = 6, instanced = true }),
    },
    postconditions = { Condition.ChatText:new("Groups spoken to: 1/3") },
  },
  {
    text = "Talk to Rodney.",
    actions = {
      Action.Direction:new(-6, 752, 27, { distance = 6, instance = true }),
      Action.ModelHighlight:new(rodney, { distance = 6, instanced = true }),
    },
    postconditions = { Condition.ChatText:new("Groups spoken to: 2/3") },
  },
  {
    text = "Go upstairs and talk to Bianca.",
    actions = { Action.ModelHighlight:new(spiralStaircase, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(bianca) },
  },
  {
    actions = { Action.ModelHighlight:new(bianca, { instanced = true }) },
    postconditions = {
      Condition.ChatText:new("Groups spoken to: 3/3"),
      Condition.ConversationText:new("I should speak to Aster."),
    },
  },
  {
    text = "Talk to Aster in the central room on ground floor (1st floor[US]).",
    actions = { Action.ModelHighlight:new(firstFloorSpiralStaircase, { instanced = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-8, 752, 25, 3, true) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["aster"], { instanced = true }),
      Action.ConversationHighlight:new("Yes, let's start the feast."),
    },
    postconditions = { Condition.ConversationText:new("Thank you for inviting us to this fine fort of yours") },
  },
  {
    text = "Converse with the guests during the feast cutscene.",
    actions = {
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      Condition.ConversationText:new(
        "It would appear my many years of experience analysing mystery novels may be put to practical use." --not properly tested
      ),
    },
  },
  {
    text = "Once the cutscene is completed, continue the conversation with Aster to obtain a mystery journal.",
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("Let's get to work.") },
  },
  {
    text = "(1/8) Go back to the Kitchen and investigate the nut roast located on a table in the southeast corner of the room.",
    title = "Investigating the murder - Part 1",
    actions = {
      Action.ModelHighlight:new(nutRoast, { instance = true, distance = 5 }),
      Action.Direction:new(12, 720, 25, { instance = true, distance = 8 }),
    },
    postconditions = {
      -- Condition.ConversationText:new("We should investigate."),
      Condition.ChatText:new("Found clue: 1/8."),
    },
  },
  {
    text = "(2/8) Investigate the strange satchel.",
    actions = {
      Action.ModelHighlight:new(strangeSatchel, { instance = true, distance = 8 }),
      Action.Direction:new(-13, 480, 28, { instance = true, distance = 8 }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      -- Condition.ConversationText:new("A page also seems to have been torn out...how peculiar."),
      Condition.ChatText:new("Found clue: 2/8."),
    },
  },
  {
    text = "(3/8) Talk to Simon outside of the Town Hall and Link 'How to Poison Dummies for Dummies'.",
    actions = {
      Action.ModelHighlight:new(simon, { instance = true, distance = 10 }),
      Action.Direction:new(4, 720, 18, { instance = true, distance = 10 }),
      Action.ConversationHighlight:new("Link a clue to Simon."),
    },
    postconditions = {
      -- Condition.ConversationText:new("Thank you."),
      Condition.ChatText:new("Found clue: 3/8."),
    },
  },
  {
    text = "(4/8) Investigate the half-buried box in the northeastern corner of the fort.",
    actions = {
      Action.ModelHighlight:new(halfBuriedBox, { instance = true, distance = 10 }),
      Action.Direction:new(25, 168, 29, { instance = true, distance = 10 }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      -- Condition.ConversationText:new("Thank you."),
      Condition.ChatText:new("Found clue: 4/8."),
    },
  },
  {
    text = "Investigate the strange satchel again and take a poison detection potion (base).",
    actions = {
      Action.ModelHighlight:new(strangeSatchel, { instance = true, distance = 8 }),
      Action.Direction:new(-13, 480, 28, { instance = true, distance = 8 }),
      Action.ConversationHighlight:new("Take a vial."),
    },
    postconditions = { Condition.InventoryContains:new(poisonDetectionBase) },
  },
  {
    text = "Search the supply cupboard in the Kitchen and take a hollyhock.",
    actions = {
      Action.ModelHighlight:new(supplyCupboard, { instance = true, distance = 4 }),
      Action.Direction:new(11, 720, 25, { instance = true, distance = 4 }),
      Action.ConversationHighlight:new("Take hollyhock. "), --here on purpose
      Action.ConversationHighlight:new("Take hollyhock."),
    },
    postconditions = { Condition.InventoryContains:new(hollyhock) },
  },
  {
    text = "<i>Use</i> the hollyhock on the vial to create an unheated poison detection potion (hollyhock).",
    actions = { Action.InventoryHighlight:new(hollyhock), Action.InventoryHighlight:new(poisonDetectionBase) },
    postconditions = { Condition.InventoryDoesNotContain:new(hollyhock) },
  },
  {
    text = "Heat the vial on the range.",
    actions = { Action.InventoryHighlight:new(unheatedPoisonDetection), Action.ModelHighlight:new(range) },
    postconditions = {
      Condition.ConversationText:new("You heat the potion on the range while stirring the contents vigorously."),
      Condition.InventoryContains:new(poisonDetectionPotion),
    },
  },
  {
    text = "(5/8) <i>Use</i> the poison detection potion (hollyhock) on the duke's meal (meat pâté) on the dining table.",
    actions = {
      Action.ModelHighlight:new(dukesMeal, { instance = true }),
      Action.InventoryHighlight:new(poisonDetectionPotion),
      Action.ConversationHighlight:new("Use it on the duke's meat pâté. "), --not working
      Action.ConversationHighlight:new("Use it on the duke's meat pâté."), --not working
    },
    postconditions = { Condition.ChatText:new("Found clue: 5/8.") },
  },
  {
    text = "(6/8) Talk to King Roald in the Chapel.",
    actions = {
      Action.ModelHighlight:new(roald, { instance = true, distance = 8 }),
      Action.Direction:new(25, 144, 20, { instance = true, distance = 8 }),
      Action.ConversationHighlight:new("Talk to King Roald."),
      Action.ConversationHighlight:new("Did you notice anything suspicious?"),
    },
    postconditions = { Condition.ChatText:new("Found clue: 6/8.") },
  },
  {
    text = "(7/8) Go to the Command Centre and speak to Duchess Alba, and Link the 'Nut Roast'.",
    actions = {
      Action.ModelHighlight:new(duchessAlba, { instance = true, distance = 8 }),
      Action.Direction:new(13, 80, -5, { instance = true, distance = 8 }),
      Action.ConversationHighlight:new("Link a clue to Duchess Alba"),
    },
    postconditions = { Condition.ChatText:new("Linked clue: 7/8.") },
  },
  {
    text = "(8/8) Talk to Bianca outside the chapel, and Link the 'Stolen Jewellery'.",
    actions = {
      Action.ModelHighlight:new(bianca, { instance = true, distance = 10 }),
      Action.Direction:new(16, 128, 14, { instance = true, distance = 10 }),
      Action.ConversationHighlight:new("Link a clue to Bianca."),
      Action.ConversationHighlight:new("That's unacceptable."),
    },
    postconditions = { Condition.ChatText:new("Found clue: 8/8.") },
  },
  {
    text = "Ascend to the top floor of the Town Hall and talk to Aster.<ul><li>For tracking to work properly, please clear your chat.</li></ul>",
    title = "Part 2",
    actions = {
      Action.ModelHighlight:new(spiralStaircase, { instance = true, distance = 6 }),
      Action.Direction:new(-8, 752, 23, { instance = true, distance = 6 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(-7, 2416, 24, 4, true) },
  },
  {
    actions = { Action.ModelHighlight:new(firstFloorSpiralStaircase, { instance = true }) },
    postconditions = {
      Condition.DistanceToWithHeight:new(-7, 3920, 24, 4, true), --on second floor
      Condition.DistanceToWithHeight:new(-7, 6480, 24, 4, true),
    },
  },
  {
    actions = { Action.ModelHighlight:new(secondFloorSpiralStaircase, { instance = true }) },
    postconditions = {
      Condition.DistanceToWithHeight:new(-7, 6480, 24, 4, true), --on roof
    },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["aster"], { instance = true }) },
    postconditions = { Condition.ConversationText:new("Watch out!") },
  },
  {
    text = "Defeat the assassin, then talk to Aster.<ul><li>If you die, Princess will kill the assassin for you.</li></ul>",
    actions = {},
    postconditions = { Condition.ConversationText:new("We should inspect the assassin's body for evidence.") },
  },
  {
    text = "Search the assassin's body for an assassin's letter.",
    actions = { Action.ModelHighlight:new(deadAssassin, { instance = true }) },
    postconditions = { Condition.ConversationText:new("We should check on the guests.") },
  },
  {
    text = "Right click 'Bottom floor' on the trapdoor.",
    actions = { Action.ModelHighlight:new(roofTrapDoor, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-7, 752, 25, 4, true) },
  },
  {
    text = "(1/8) Speak with King Roald outside by the well.",
    actions = {
      Action.ModelHighlight:new(roald, { instance = true }),
      Action.ConversationHighlight:new("Talk about the assassin."),
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      Condition.ConversationText:new("I, for one, will be in the command centre, enjoying what little peace I can."),
      Condition.ChatText:new("Found clue: 1/8."), --most likely can't check this because previous steps text still in chat
    },
  },
  {
    text = "(2/8) Investigate the wax seal outside the northwestern corner of the Chapel.",
    actions = {
      Action.ModelHighlight:new(waxSeal, { instance = true, distance = 4 }),
      Action.Direction:new(18, 360, 25, { instance = true, distance = 8 }),
    },
    postconditions = {
      Condition.ConversationText:new("We need to find out who has been receiving letters in private."),
      Condition.ChatText:new("Found clue: 2/8."),
    },
  },
  {
    text = "(3/8) Speak to Iris in the Workshop and Link the 'Strange Seal' to obtain the amulet of spanielspeak clue.",
    actions = {
      Action.Direction:new(-25, 144, 11, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(iris, { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Link a clue to Iris."),
      Action.ConversationHighlight:new("I think she really is this dense."),
    },
    postconditions = {
      Condition.ChatText:new("Linked clue: 3/8."),
      -- Condition.ConversationText:new("Iris hands you an amulet of spanielspeak."),
    },
  },
  {
    text = "(4/8) In the Town Hall, investigate the burnt parchment on the floor near the fireplace.",
    actions = {
      Action.Direction:new(0, 752, 29, { instance = true, distance = 8 }),
      Action.ModelHighlight:new(burntParchment, { instance = true, distance = 8 }),
    },
    postconditions = { Condition.ChatText:new("Found clue: 4/8.") },
  },
  {

    text = "Talk to Princess, the dog, just east of the stonecutter outside the Workshop and Link the 'Amulet of Spanielspeak'.",
    actions = {
      Action.Direction:new(-14, 48, 8, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(NPCs["princess the dog"], { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Link a clue to Princess."),
    },
    postconditions = { Condition.ConversationText:new("the way") }, --needs testing
  },
  {
    text = "Go to the northwestern corner of the fort next to the ladder.",
    actions = { Action.Direction:new(-27, 144, 25, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(-27, 144, 25, 3, true) },
  },
  {
    text = "Speak to Princess.",
    actions = { Action.ModelHighlight:new(NPCs["princess the dog"], { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(miscItems) },
  },
  {
    text = "Investigate the miscellaneous items that appear.",
    actions = { Action.ModelHighlight:new(miscItems, { instance = true }) },
    postconditions = { Condition.ConversationText:new("More suspicious things over there. Come, come!") },
  },
  {
    text = "Stand near Queen Ellamaria outside by the well, then speak to Princess again.",
    actions = {
      Action.Direction:new(5, -32, 8, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(5, -32, 8, 1, true) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["princess the dog"], { instance = true }) },
    postconditions = { Condition.ConversationText:new("One more thing hidden. Come with me, come!") },
  },
  {
    text = "Stand at the altar in the chapel, then speak to Princess once again.",
    actions = {
      Action.Direction:new(27, 144, 20, { instance = true }),
      Action.ModelHighlight:new(NPCs["princess the dog"], { instance = true, distance = 2 }),
    },
    postconditions = { Condition.DistanceTo:new(27, 144, 20, 1, true) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["princess the dog"], { instance = true }) },
    postconditions = { Condition.ModelVisible:new(hiddenCache) },
  },
  {
    text = "(5/8) Investigate the unearthed coffer (which may be underneath Aster, Princess, or your character).",
    actions = { Action.ModelHighlight:new(hiddenCache, { instance = true }) },
    postconditions = { Condition.ChatText:new("Found clue: 5/8.") },
  },
  {
    text = "(6/8) Talk to Duchess Alba in the Command Centre.",
    actions = {
      Action.ModelHighlight:new(duchessAlba, { instance = true, distance = 8 }),
      Action.Direction:new(13, 80, -5, { instance = true, distance = 8 }),
      Action.ConversationHighlight:new("Talk to Duchess Alba."),
      Action.ConversationHighlight:new("What's your beef with King Roald?"),
      Action.ConversationHighlight:new("I'm so sorry to hear that."),
    },
    postconditions = { Condition.ChatText:new("Found clue: 6/8.") },
  },
  {
    text = "Talk to Rodney outside the Workshop and ask about Ellamaria.",
    actions = {
      Action.ModelHighlight:new(rodney, { instance = true, distance = 12 }),
      Action.Direction:new(-18, 18, 20, { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Talk to Rodney."),
      Action.ConversationHighlight:new("Ask about Ellamaria."),
      Action.ConversationHighlight:new("That's all for now"),
    },
    postconditions = { Condition.ConversationText:new("We should ask her about it.") },
  },
  {
    text = "(7/8) Speak to Queen Ellamaria outside by the well and ask about being friends with Bianca.",
    actions = {
      Action.ModelHighlight:new(ellamaria, { instance = true, distance = 12 }),
      Action.Direction:new(5, -32, 8, { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Talk to Ellamaria."),
      Action.ConversationHighlight:new("I heard you were once friends with Bianca."),
    },
    postconditions = { Condition.ChatText:new("Found clue: 7/8.") },
  },
  {
    text = "(8/8) Speak to Rodney again and Link the 'Scorched Will', and finish speaking with Aster.",
    actions = {
      Action.ModelHighlight:new(rodney, { instance = true, distance = 12 }),
      Action.Direction:new(-18, 18, 20, { instance = true, distance = 12 }),
      Action.ConversationHighlight:new("Link a clue to Rodney."),
      Action.ConversationHighlight:new("Yes, I'm ready."),
    },
    postconditions = { Condition.ChatText:new("Found clue: 8/8.") },
  },
  {
    text = "Talk to Aster in the courtyard.",
    actions = {
      Action.ModelHighlight:new(NPCs["aster"], { instance = true }),
      Action.ConversationHighlight:new("Yes, I'm ready."),
    },
    postconditions = { Condition.ConversationText:new("We are eager to hear the result") },
  },
  {
    text = "Identify the murderer as Bianca (5).",
    actions = {
      Action.ConversationHighlight:new("Simon left it there himself."),
      Action.ConversationHighlight:new("They wanted his wealth."),
      Action.ConversationHighlight:new("It was an excuse to protect you."),
      Action.ConversationHighlight:new("The Zamorakians offered them power."),
      Action.ConversationHighlight:new("She broke the law and needs to pay the price."),
    },
    postconditions = { Condition.ConversationText:new("Get her out of my sight.") },
  },
  {
    text = "Talk to King Roald.",
    title = "Finishing up",
    actions = {
      Action.ModelHighlight:new(roald, { instance = true }),
      Action.ConversationHighlight:new("End the banquet."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Aster takes two weeks' leave following the events of the banquet, to reflect on all that transpired."
      ),
      Condition.DistanceTo:new(3302, 1221, 3570, 6),
    },
  },
  {
    text = "Talk to Aster in the Town Hall.",
    actions = {
      Action.ModelHighlight:new(NPCs["aster"]),
      Action.ConversationHighlight:new("Talk about quests..."),
      Action.ConversationHighlight:new("Talk about 'Murder on the Border'."),
      Action.ConversationHighlight:new("Ask how Aster is feeling."),
    },
    postconditions = { Condition.ConversationText:new("Rodney? I wasn't expecting that.") }, -- not tested
  },
  {
    text = "Talk to Rodney in the Kitchen.",
    actions = { Action.ModelHighlight:new(rodney), Action.ConversationHighlight:new("") },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Murder on the Border",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1679270400,
  prereqQuests = { "New Foundations" },
  questReqs = { Types.QuestReq.ironmanOnlySkill("Woodcutting", 20, true) },
  neededItems = {
    ["Willow frames"] = { quantity = 12 },
    ["Stone wall segments"] = { quantity = 6 },
  },
  recommendedItems = {},
  combatNPCs = { ["Assassin"] = { level = "Scaled", quantity = 1 } },
})
