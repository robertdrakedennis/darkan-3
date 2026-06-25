local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local chiefTess = Model.new(4347, {
  [990] = Vertex.new(168, 320, -88, 67, 27, 34),
  [2622] = Vertex.new(12, 956, -128, 178, 178, 167),
  [2630] = Vertex.new(-12, 956, -128, 178, 178, 167),
  [2724] = Vertex.new(12, 972, -112, 178, 178, 167),
  [2738] = Vertex.new(-12, 972, -112, 178, 178, 167),
})
local balnea = Model.new(4551, {
  [2985] = Vertex.new(24, 738, -38, 31, 30, 29),
  [3009] = Vertex.new(-24, 738, -38, 31, 30, 29),
  [3031] = Vertex.new(-2, 716, -57, 108, 80, 56),
  [3037] = Vertex.new(2, 716, -57, 108, 80, 56),
  [3041] = Vertex.new(6, 716, -52, 108, 80, 56),
})
local chargurr = Model.new(3825, {
  [2076] = Vertex.new(-152, 888, -56, 169, 165, 156),
  [3740] = Vertex.new(-12, 956, -128, 178, 178, 167),
  [3747] = Vertex.new(12, 956, -128, 178, 178, 167),
  [3767] = Vertex.new(-12, 972, -112, 178, 178, 167),
  [3783] = Vertex.new(12, 972, -112, 178, 178, 167),
})
local seegud = Model.new(1377, {
  [5] = Vertex.new(-70, 812, 49, 74, 61, 96),
  [10] = Vertex.new(-49, 812, -7, 74, 61, 96),
  [126] = Vertex.new(70, 812, 49, 74, 61, 96),
  [130] = Vertex.new(49, 812, -7, 74, 61, 96),
  [135] = Vertex.new(35, 784, -49, 74, 61, 96),
})
local snurgh = Model.new(3363, {
  [741] = Vertex.new(260, 416, 140, 102, 93, 78),
  [1601] = Vertex.new(-12, 956, -128, 178, 178, 167),
  [1611] = Vertex.new(12, 956, -128, 178, 178, 167),
  [1811] = Vertex.new(-12, 972, -112, 178, 178, 167),
  [1827] = Vertex.new(12, 972, -112, 178, 178, 167),
})
local kringk = Model.new(4347, {
  [1607] = Vertex.new(-180, 344, 184, 74, 61, 96),
  [3128] = Vertex.new(-12, 956, -128, 178, 178, 167),
  [3138] = Vertex.new(12, 956, -128, 178, 178, 167),
  [3416] = Vertex.new(-12, 972, -112, 178, 178, 167),
  [3432] = Vertex.new(12, 972, -112, 178, 178, 167),
})
local snarrk = Model.new(3051, {
  [624] = Vertex.new(-104, 268, -32, 201, 193, 193),
  [818] = Vertex.new(-164, 300, 92, 122, 89, 78),
  [831] = Vertex.new(164, 300, 92, 122, 89, 78),
  [1026] = Vertex.new(120, 232, -68, 115, 84, 73),
  [1178] = Vertex.new(-120, 232, -68, 115, 84, 73),
})
--#endregion
--#region Objects
local cookingFirePit = Model.new(2868, {
  [448] = Vertex.new(-724, 4, -660, 87, 70, 17),
  [450] = Vertex.new(-692, 4, -720, 87, 70, 17),
  [454] = Vertex.new(-692, 4, -720, 87, 70, 17),
  [538] = Vertex.new(-712, 4, 684, 87, 70, 17),
  [541] = Vertex.new(-672, 4, 716, 87, 70, 17),
})
local litCookingFirePit = Model.new(3852, {
  [2667] = Vertex.new(-76, -1724, -208, 207, 117, 19),
  [3010] = Vertex.new(-76, -1756, -360, 250, 203, 59),
  [3670] = Vertex.new(352, -1728, 304, 250, 203, 59),
  [3676] = Vertex.new(420, -1728, 164, 250, 203, 59),
  [3680] = Vertex.new(420, -1728, 164, 250, 203, 59),
})
local firePit = Model.new(1326, {
  [451] = Vertex.new(432, 9, 228, 102, 85, 65),
  [682] = Vertex.new(-404, 9, 392, 102, 85, 65),
  [688] = Vertex.new(-404, 9, 392, 102, 85, 65),
  [760] = Vertex.new(320, 9, 404, 102, 85, 65),
  [850] = Vertex.new(-240, 9, 408, 102, 85, 65),
})
local firePitWithLogs = Model.new(1647, {
  [448] = Vertex.new(432, 9, 228, 102, 85, 65),
  [619] = Vertex.new(-240, 9, 408, 102, 85, 65),
  [685] = Vertex.new(-404, 9, 392, 102, 85, 65),
  [691] = Vertex.new(-404, 9, 392, 102, 85, 65),
  [769] = Vertex.new(320, 9, 404, 102, 85, 65),
})
local litFirePit = Model.new(2862, {
  [1851] = Vertex.new(-104, -1744, -124, 213, 120, 19),
  [2497] = Vertex.new(160, -1748, 192, 251, 206, 73),
  [2503] = Vertex.new(200, -1748, 104, 251, 206, 73),
  [2518] = Vertex.new(-104, -1760, -220, 251, 206, 73),
  [2522] = Vertex.new(-104, -1760, -220, 251, 206, 73),
})
local boulderTrap = Model.new(144, {
  [46] = Vertex.new(260, 412, -212, 90, 75, 57),
  [49] = Vertex.new(260, 412, -212, 90, 75, 57),
  [58] = Vertex.new(292, 308, 192, 90, 75, 57),
  [106] = Vertex.new(260, 412, -212, 96, 80, 61),
  [115] = Vertex.new(308, 496, 40, 96, 80, 61),
})
local setBoulderTrap = Model.new(462, {
  [47] = Vertex.new(344, 312, 8, 95, 71, 39),
  [191] = Vertex.new(344, 312, -8, 95, 71, 39),
  [195] = Vertex.new(344, 312, -8, 95, 71, 39),
  [209] = Vertex.new(328, 312, 0, 95, 71, 39),
  [364] = Vertex.new(260, 412, -212, 90, 75, 57),
})
local setBirdSnare = Model.new(570, {
  [3] = Vertex.new(104, 536, -36, 100, 91, 76),
  [5] = Vertex.new(104, 536, -36, 100, 91, 76),
  [21] = Vertex.new(116, 536, -20, 100, 91, 76),
  [33] = Vertex.new(120, 536, -8, 100, 91, 76),
  [537] = Vertex.new(4, 532, -8, 100, 91, 76),
})
local caughtWimpyBird = Model.new(2031, {
  [423] = Vertex.new(4, 532, -8, 100, 91, 76),
  [467] = Vertex.new(104, 536, -36, 100, 91, 76),
  [474] = Vertex.new(104, 536, -36, 100, 91, 76),
  [488] = Vertex.new(120, 536, -8, 100, 91, 76),
  [495] = Vertex.new(116, 536, -20, 100, 91, 76),
})
local setBoxTrap = Model.new(780, {
  [707] = Vertex.new(32, 204, 84, 89, 70, 36),
  [711] = Vertex.new(76, 484, 4, 89, 70, 36),
  [737] = Vertex.new(12, 204, 84, 89, 70, 36),
  [763] = Vertex.new(76, 484, 4, 137, 125, 105),
  [766] = Vertex.new(76, 484, 4, 137, 125, 105),
})
local caughtKebit = Model.new(504, {
  [185] = Vertex.new(296, 8, -80, 77, 60, 40),
  [406] = Vertex.new(260, 412, -212, 83, 76, 76),
  [409] = Vertex.new(260, 412, -212, 83, 76, 76),
  [418] = Vertex.new(292, 308, 192, 83, 76, 76),
  [466] = Vertex.new(260, 412, -212, 92, 85, 85),
})
--#endregion
--#region Items
local teasingStick = Model.new(90, {
  [2] = Vertex.new(224, 32, 248, 82, 65, 33),
  [5] = Vertex.new(224, 32, 248, 82, 65, 33),
  [7] = Vertex.new(224, 32, 248, 82, 65, 33),
  [27] = Vertex.new(-240, 24, -240, 82, 65, 33),
  [48] = Vertex.new(-240, 24, -240, 82, 65, 33),
})
local eucalyptusLogs = Model.new(564, {
  [92] = Vertex.new(-104, 156, -16, 132, 121, 102),
  [96] = Vertex.new(-104, 156, -16, 132, 121, 102),
  [140] = Vertex.new(-116, 72, -72, 132, 121, 102),
  [531] = Vertex.new(-104, 156, -16, 103, 101, 95),
  [534] = Vertex.new(-104, 156, -16, 103, 101, 95),
})
local archeyTreeLogs = Model.new(594, {
  [296] = Vertex.new(184, 148, 12, 82, 73, 52),
  [303] = Vertex.new(184, 148, 12, 82, 73, 52),
  [315] = Vertex.new(-172, 156, -16, 82, 73, 52),
  [320] = Vertex.new(-172, 156, -16, 82, 73, 52),
  [534] = Vertex.new(184, 148, 12, 66, 55, 20),
})
local wolfBones = Model.new(504, {
  [1] = Vertex.new(-48, 4, -28, 126, 126, 115),
  [2] = Vertex.new(-72, 20, -24, 126, 126, 115),
  [3] = Vertex.new(-72, 4, -24, 126, 126, 115),
  [5] = Vertex.new(-48, 20, -28, 126, 126, 115),
  [8] = Vertex.new(-76, 20, -52, 126, 126, 115),
  [9] = Vertex.new(-76, 4, -52, 126, 126, 115),
  [14] = Vertex.new(-56, 20, -52, 126, 126, 115),
  [15] = Vertex.new(-56, 4, -52, 126, 126, 115),
  [20] = Vertex.new(-40, 20, -72, 126, 126, 115),
  [21] = Vertex.new(-40, 4, -72, 126, 126, 115),
  [26] = Vertex.new(-28, 20, -72, 126, 126, 115),
  [27] = Vertex.new(-28, 4, -72, 126, 126, 115),
  [31] = Vertex.new(68, 20, 76, 126, 126, 115),
  [32] = Vertex.new(40, 4, 72, 126, 126, 115),
  [33] = Vertex.new(40, 20, 72, 126, 126, 115),
  [35] = Vertex.new(68, 4, 76, 126, 126, 115),
  [37] = Vertex.new(64, 20, 92, 126, 126, 115),
  [41] = Vertex.new(64, 4, 92, 126, 126, 115),
  [43] = Vertex.new(40, 20, 96, 126, 126, 115),
  [47] = Vertex.new(40, 4, 96, 126, 126, 115),
})
local stripyFeathers = Model.new(69, {
  [9] = Vertex.new(-16, 0, 28, 84, 96, 61),
  [15] = Vertex.new(-36, 0, -24, 84, 96, 61),
  [33] = Vertex.new(-44, 0, 12, 84, 95, 8),
  [39] = Vertex.new(-68, 0, -8, 84, 95, 8),
  [44] = Vertex.new(68, 0, 4, 201, 200, 193),
})
local larupiaFur = Model.any({
  Model.new(600, { --tatty
    [1] = Vertex.new(248, 0, 120, 108, 69, 33),
    [96] = Vertex.new(136, 0, -244, 108, 69, 33),
    [153] = Vertex.new(-76, 0, -248, 108, 69, 33),
    [169] = Vertex.new(-196, 0, -104, 108, 69, 33),
    [260] = Vertex.new(-8, 0, 240, 108, 69, 33),
  }),
  Model.new(585, {
    [6] = Vertex.new(-20, 0, -240, 108, 69, 33),
    [222] = Vertex.new(164, 0, -164, 108, 69, 33),
    [236] = Vertex.new(-160, 0, 232, 108, 69, 33),
    [238] = Vertex.new(124, 0, 228, 108, 69, 33),
    [256] = Vertex.new(-188, 0, -160, 108, 69, 33),
  }),
})
--#endregion
--#region Quest Items
local stinkbloom = Model.new(336, {
  [3] = Vertex.new(32, 52, -56, 120, 105, 137),
  [27] = Vertex.new(60, 44, 52, 120, 105, 137),
  [63] = Vertex.new(12, 28, 100, 120, 105, 137),
  [75] = Vertex.new(-40, 44, 148, 120, 105, 137),
  [87] = Vertex.new(-84, 72, 80, 120, 105, 137),
})
local feverGrass = Model.new(846, {
  [28] = Vertex.new(196, 144, 40, 109, 154, 80),
  [32] = Vertex.new(124, 204, -44, 92, 131, 68),
  [35] = Vertex.new(200, 144, 40, 92, 131, 68),
  [106] = Vertex.new(120, 204, -44, 92, 131, 68),
  [595] = Vertex.new(228, 84, -8, 109, 154, 80),
})
local lavender = Model.new(1479, {
  [418] = Vertex.new(124, 68, 180, 51, 43, 84),
  [423] = Vertex.new(124, 68, 180, 51, 43, 84),
  [1265] = Vertex.new(128, 72, 180, 128, 101, 194),
  [1269] = Vertex.new(128, 72, 180, 128, 101, 194),
  [1278] = Vertex.new(136, 36, 180, 128, 101, 194),
})
local primweed = Model.new(90, {
  [5] = Vertex.new(12, 52, -168, 125, 80, 91),
  [8] = Vertex.new(128, 60, 108, 125, 80, 91),
  [65] = Vertex.new(-120, 52, 140, 125, 80, 91),
  [77] = Vertex.new(-124, 64, -156, 125, 80, 91),
  [89] = Vertex.new(140, 40, -120, 125, 80, 91),
})
local tansymum = Model.new(1866, {
  [752] = Vertex.new(68, 136, 140, 176, 140, 72),
  [755] = Vertex.new(68, 136, 140, 176, 140, 72),
  [1628] = Vertex.new(84, 152, 112, 164, 115, 50),
  [1634] = Vertex.new(84, 152, 112, 164, 115, 50),
  [1829] = Vertex.new(76, 100, 168, 164, 115, 50),
})
local diseasedKebbitFur = Model.new(522, {
  [357] = Vertex.new(-204, 0, -72, 87, 81, 17),
  [369] = Vertex.new(-176, 0, -60, 87, 81, 17),
  [392] = Vertex.new(212, 0, -72, 87, 81, 17),
  [393] = Vertex.new(220, 0, -80, 87, 81, 17),
  [404] = Vertex.new(184, 0, -60, 87, 81, 17),
})
local peter = Model.new(396, {
  [29] = Vertex.new(-20, 8, 60, 70, 52, 36),
  [38] = Vertex.new(-20, 8, 60, 70, 52, 36),
  [44] = Vertex.new(-20, 8, 60, 70, 52, 36),
  [338] = Vertex.new(0, -12, 56, 58, 37, 17),
  [342] = Vertex.new(0, -12, 56, 58, 37, 17),
})
local penelope = Model.new(396, {
  [29] = Vertex.new(-20, 8, 60, 66, 50, 42),
  [38] = Vertex.new(-20, 8, 60, 66, 50, 42),
  [44] = Vertex.new(-20, 8, 60, 66, 50, 42),
  [338] = Vertex.new(0, -12, 56, 52, 43, 33),
  [342] = Vertex.new(0, -12, 56, 52, 43, 33),
})
local peanut = Model.new(396, {
  [29] = Vertex.new(-20, 8, 60, 82, 65, 52),
  [38] = Vertex.new(-20, 8, 60, 82, 65, 52),
  [44] = Vertex.new(-20, 8, 60, 82, 65, 52),
  [338] = Vertex.new(0, -12, 56, 70, 52, 36),
  [342] = Vertex.new(0, -12, 56, 70, 52, 36),
})
local patrick = Model.new(396, {
  [29] = Vertex.new(-20, 8, 60, 82, 65, 52),
  [38] = Vertex.new(-20, 8, 60, 82, 65, 52),
  [44] = Vertex.new(-20, 8, 60, 82, 65, 52),
  [338] = Vertex.new(0, -12, 56, 70, 52, 36),
  [342] = Vertex.new(0, -12, 56, 70, 52, 36),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Head to Oo'glog and talk to Chief Tess.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Oo'glog lodestone",
      url = "Oo'glog_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2548, 1461, 2853) },
    postconditions = { Condition.ModelVisible:new(chiefTess) },
  },
  {
    actions = {
      Action.ModelHighlight:new(chiefTess),
      Action.ConversationHighlight:new("What exactly is going on around here?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(chiefTess) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Chief Tess.",
    actions = {
      Action.ModelHighlight:new(chiefTess),
      Action.ConversationHighlight:new("So, about this 'business venture'..."),
    },
    postconditions = { Condition.ConversationText:new("yet for anybody") }, --not tested
  },
  {
    text = "Talk to Balnea in the Oo'glog bank.",
    actions = { Action.Direction:new(2557, 1061, 2838) },
    postconditions = { Condition.ModelVisible:new(balnea) },
  },
  {
    actions = {
      Action.ModelHighlight:new(balnea),
      Action.ConversationHighlight:new("Um, yes. So, what's going on around here?"),
      Action.ConversationHighlight:new("What's the problem?"),
      Action.ConversationHighlight:new("Okay, I'll bite. Tell me more."),
      Action.ConversationHighlight:new("But of course!"),
      Action.ConversationHighlight:new("Well, why not? I've nothing better to do."),
      Action.ConversationHighlight:new("Sure thing."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(balnea) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Oh, nothing") },
  },
  {
    text = "Talk to Chargurr north of the bank.",
    title = "The food",
    neededItems = {
      ["Eucalyptus logs"] = { quantity = 10 },
      ["Achey tree logs"] = { quantity = 8 },
      ["Any non-poisoned spear (bone spear recommended)"] = { quantity = 4 },
      ["Raw chompy bird"] = { quantity = 2 },
    },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(chargurr),
      Action.ConversationHighlight:new("I think I could get that stuff for you."),
    },
    postconditions = { Condition.ConversationText:new("gets de woods") },
  },
  {
    text = "Talk to Chargurr again.",
    actions = { Action.ModelHighlight:new(chargurr) },
    postconditions = { Condition.ConversationText:new("Sure thing") },
  },
  {
    text = "Talk to Chargurr yet again.",
    actions = {
      Action.ModelHighlight:new(chargurr),
      Action.ConversationHighlight:new("Fine, I'll get some spears for you"),
    },
    postconditions = { Condition.ConversationText:new("have de spears") },
  },
  {
    text = "Talk to Chargurr once again.",
    actions = {
      Action.ModelHighlight:new(chargurr),
      Action.ConversationHighlight:new("Yes, I'd like to give 4 spears to Chargurr."),
    },
    postconditions = { Condition.ConversationText:new("No problem") },
  },
  {
    text = "Talk to Chargurr (again).",
    actions = {
      Action.ModelHighlight:new(chargurr),
      Action.ConversationHighlight:new("But of course! I'll bring you some chompy birds."),
    },
    postconditions = { Condition.ConversationText:new("coming right up") },
  },
  {
    text = "Use two raw chompies on Chargurr.",
    actions = {
      Action.ModelHighlight:new(chargurr),
      Action.InventoryHighlight:new(Models.items["raw chompy bird"]),
    },
    postconditions = { Condition.ConversationText:new("afraid") },
  },
  {
    text = "Talk to Chargurr.",
    actions = { Action.ModelHighlight:new(chargurr) },
    postconditions = { Condition.ConversationText:new("That I can do") },
  },
  {
    text = "Light the fire pit.",
    actions = { Action.ModelHighlight:new(cookingFirePit) },
    postconditions = { Condition.ModelVisible:new(litCookingFirePit) },
  },
  {
    text = "Talk to Chargurr.",
    actions = { Action.ModelHighlight:new(chargurr) },
    postconditions = { Condition.ConversationText:new("that in mind") },
  },
  {
    text = "Talk to Balnea.",
    title = "The beds",
    neededItems = {
      ["Stripy feathers"] = { quantity = 8 },
      ["Bird snare"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2557, 1061, 2838) },
    postconditions = { Condition.ModelVisible:new(balnea) },
  },
  {
    actions = {
      Action.ModelHighlight:new(balnea),
      Action.ConversationHighlight:new("Right-ho. I'll go and see Snurgh at the hotel and 'transition' away."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(balnea) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("sure to do that") },
  },
  {
    text = "Talk to Snurgh to the east.",
    actions = { Action.Direction:new(2596, 545, 2845) },
    postconditions = { Condition.ModelVisible:new(snurgh) },
  },
  {
    actions = {
      Action.ModelHighlight:new(snurgh),
      Action.ConversationHighlight:new("Sure, I'll bring you some feathers."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(snurgh) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("line of questioning") },
  },
  {
    text = "Talk to Snurgh with the stripy feathers.<ul><li>Catch tropical wagtails near the Oo'glog lodestone for stripy feathers.</li></ul>",
    actions = { Action.Direction:new(2596, 545, 2845) },
    postconditions = { Condition.ModelVisible:new(snurgh) },
  },
  {
    actions = { Action.ModelHighlight:new(snurgh) },
    jumpconditions = { Condition.ModelNotVisible:new(snurgh) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("touch base with Balnea") },
  },
  {
    text = "Talk to Balnea.",
    title = "The salon",
    neededItems = {
      ["Normal/tattered larupia furs"] = { quantity = 8 },
      ["Wolf bones"] = { quantity = 4 },
      ["Teasing stick"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2557, 1061, 2838) },
    postconditions = { Condition.ModelVisible:new(balnea) },
  },
  {
    actions = {
      Action.ModelHighlight:new(balnea),
      Action.ConversationHighlight:new("You're going to ask me to go to the salon, right?"),
      Action.ConversationHighlight:new("Yeah, sure, I'll go to the salon."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(balnea) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("exactly what you said") },
  },
  {
    text = "Talk to Kringk to the east.",
    actions = { Action.Direction:new(2581, 1237, 2844) },
    postconditions = { Condition.ModelVisible:new(kringk) },
  },
  {
    actions = {
      Action.ModelHighlight:new(kringk),
      Action.ConversationHighlight:new("Why, sure, I'd be happy to help."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(kringk) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Alright, fine") },
  },
  {
    text = "Talk to Kringk with the furs and bones.<ul><li>Catch larupias north of the Oo'glog lodestone.</li><li>Kill wolves west of Oo'glog.</li></ul>",
    actions = { Action.Direction:new(2581, 1237, 2844) },
    postconditions = { Condition.ModelVisible:new(kringk) },
  },
  {
    actions = {
      Action.ModelHighlight:new(kringk),
      Action.ConversationHighlight:new("You sure can!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(kringk) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("I'll do that") },
  },
  {
    text = "Talk to Balnea.",
    title = "First trial",
    neededItems = {
      ["Eucalyptus logs"] = { quantity = 1 },
      ["Tansymum"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2557, 1061, 2838) },
    postconditions = { Condition.ModelVisible:new(balnea) },
  },
  {
    actions = { Action.ModelHighlight:new(balnea) },
    jumpconditions = { Condition.ModelNotVisible:new(balnea) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("generous of you") },
  },
  {
    text = "Talk to Seegud.",
    actions = { Action.Direction:new(2571, 837, 2856) },
    postconditions = { Condition.ModelVisible:new(seegud) },
  },
  {
    actions = { Action.ModelHighlight:new(seegud) },
    jumpconditions = { Condition.ModelNotVisible:new(seegud) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("head all at once") },
  },
  {
    text = "Use a eucalyptus log on the fire pit.",
    actions = {
      Action.ModelHighlight:new(firePit, { atLocation = Location:new(2578, 837, 2866) }),
      Action.InventoryHighlight:new(eucalyptusLogs),
    },
    postconditions = { Condition.ChatText:new("eucalyptus logs in the fire pit") },
  },
  {
    text = "Use tansymum on the fire pit.",
    actions = {
      Action.ModelHighlight:new(firePitWithLogs),
      Action.InventoryHighlight:new(tansymum),
    },
    postconditions = { Condition.ChatText:new("tansymum in the fire pit") },
  },
  {
    text = "Light the fire pit.",
    actions = { Action.ModelHighlight:new(firePitWithLogs) },
    postconditions = { Condition.ConversationText:new("what's going on") },
  },
  {
    text = "Talk to Seegud.",
    actions = { Action.Direction:new(2571, 837, 2856) },
    postconditions = { Condition.ModelVisible:new(seegud) },
  },
  {
    actions = {
      Action.ModelHighlight:new(seegud),
      Action.ConversationHighlight:new("I think I'm a little stuck."),
      Action.ConversationHighlight:new("Can you explain what I should be doing again?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(seegud) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Good luck") },
  },
  {
    text = "Talk to Snarrk.",
    actions = { Action.Direction:new(2582, 837, 2865) },
    postconditions = { Condition.ModelVisible:new(snarrk) },
  },
  {
    actions = { Action.ModelHighlight:new(snarrk) },
    jumpconditions = { Condition.ModelNotVisible:new(snarrk) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("get right on it") },
  },
  {
    text = "Use logs and plants on the fire pits to disperse the bugs and find which plant corresponds to which rock.<ul><li>You have to remember which stone corresponds to which plant.</li><li>Plant numbers are estimates.</li><li>The wiki has a nice calculator for this section.</li></ul>",
    title = "Pest control",
    warning = "No tracking for this step.",
    neededItems = {
      ["Eucalyptus logs"] = { quantity = 20 },
      ["Fever grass"] = { quantity = 14 },
      ["Lavender"] = { quantity = 11 },
      ["Primweed"] = { quantity = 6 },
      ["Stinkbloom"] = { quantity = 6 },
      ["Tansymum"] = { quantity = 12 },
    },
    recommendedItems = {},
  },
  {
    text = "Thermal bath - yellow egg-shaped rock.",
    actions = {
      Action.ModelHighlight:new(firePit, { atLocation = Location:new(2578, 837, 2866) }),
      Action.ModelHighlight:new(firePitWithLogs, { atLocation = Location:new(2578, 837, 2866) }),
    },
    postconditions = { Condition.ChatText:new("makes the insects disperse") },
  },
  {
    text = "Mud bath - red sphere rock. Add the yellow egg-shaped plant too.",
    actions = {
      Action.ModelHighlight:new(firePit, { atLocation = Location:new(2603, 165, 2860) }),
      Action.ModelHighlight:new(firePitWithLogs, { atLocation = Location:new(2603, 165, 2860) }),
    },
    postconditions = { Condition.ChatText:new("makes the insects disperse") },
  },
  {
    text = "Salt-water spring - green gumdrop rock. Add the red sphere plant too.",
    actions = {
      Action.ModelHighlight:new(firePit, { atLocation = Location:new(2562, 837, 2865) }),
      Action.ModelHighlight:new(firePitWithLogs, { atLocation = Location:new(2562, 837, 2865) }),
    },
    postconditions = { Condition.ChatText:new("makes the insects disperse") },
  },
  {
    text = "Sulphur spring - purple tall rock. Add the green gumdrop plant too.",
    actions = {
      Action.ModelHighlight:new(firePit, { atLocation = Location:new(2534, 877, 2850) }),
      Action.ModelHighlight:new(firePitWithLogs, { atLocation = Location:new(2534, 877, 2850) }),
    },
    postconditions = { Condition.ChatText:new("makes the insects disperse") },
  },
  {
    text = "Bandos pool - orange squarish rock. Add the red sphere and purple tall plants too.",
    actions = {
      Action.ModelHighlight:new(firePit, { atLocation = Location:new(2534, 877, 2850) }),
      Action.ModelHighlight:new(firePitWithLogs, { atLocation = Location:new(2534, 877, 2850) }),
    },
    postconditions = { Condition.ChatText:new("makes the insects disperse") },
  },
  {
    text = "Return to Seegud.",
    actions = { Action.Direction:new(2571, 837, 2856) },
    postconditions = { Condition.ModelVisible:new(seegud) },
  },
  {
    actions = { Action.ModelHighlight:new(seegud) },
    jumpconditions = { Condition.ModelNotVisible:new(seegud) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Thanks for your help") },
  },
  {
    text = "Talk to Balnea.",
    title = "The animals",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2557, 1061, 2838) },
    postconditions = { Condition.ModelVisible:new(balnea) },
  },
  {
    actions = {
      Action.ModelHighlight:new(balnea),
      Action.ConversationHighlight:new("Sure, anything to avoid talking to you for a while."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(balnea) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("speak to Chief Tess") },
  },
  {
    text = "Talk to Chief Tess.",
    actions = { Action.Direction:new(2548, 1461, 2853) },
    postconditions = { Condition.ModelVisible:new(chiefTess) },
  },
  {
    actions = {
      Action.ModelHighlight:new(chiefTess),
      Action.ConversationHighlight:new("Talk about As a First Resort quest."),
      Action.ConversationHighlight:new("I know how to smoke a trap to remove my scent."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(chiefTess) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("creatures to my traps") },
  },
  {
    text = "Set the boulder traps east of the salt-water pool.",
    title = "Kebbits",
    neededItems = {
      ["Logs"] = { quantity = 4 },
      ["Fever grass"] = { quantity = 8 },
    },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(boulderTrap, { atLocation = Location:new(2567, 933, 2865) }),
      Action.ModelHighlight:new(boulderTrap, { atLocation = Location:new(2568, 901, 2863) }),
    },
    postconditions = {
      Condition.ModelVisible:new(setBoulderTrap, { atLocation = Location:new(2567, 933, 2865) }),
      Condition.ModelVisible:new(setBoulderTrap, { atLocation = Location:new(2568, 901, 2863) }),
    },
  },
  {
    text = "Light fever grass.",
    actions = { Action.InventoryHighlight:new(feverGrass) },
    postconditions = { Condition.ChatText:new("light the fever grass") },
  },
  {
    text = "Run around the trap with the lit fever grass.",
    postconditions = { Condition.ModelVisible:new(caughtKebit) },
  },
  {
    text = "Do this until all 2 kebbits are caught.",
    postconditions = { Condition.InventoryContains:new(diseasedKebbitFur, 2) },
  },
  {
    text = "Repeat the steps on the west side of the salt-water pool.",
    actions = {
      Action.ModelHighlight:new(boulderTrap, { atLocation = Location:new(2550, 853, 2860) }),
      Action.ModelHighlight:new(boulderTrap, { atLocation = Location:new(2551, 837, 2864) }),
      Action.ModelHighlight:new(boulderTrap, { atLocation = Location:new(2546, 805, 2861) }),
    },
    postconditions = {
      Condition.ModelVisible:new(setBoulderTrap, { atLocation = Location:new(2550, 853, 2860) }),
      Condition.ModelVisible:new(setBoulderTrap, { atLocation = Location:new(2551, 837, 2864) }),
      Condition.ModelVisible:new(setBoulderTrap, { atLocation = Location:new(2546, 805, 2861) }),
    },
  },
  {
    text = "Light fever grass.",
    actions = { Action.InventoryHighlight:new(feverGrass) },
    postconditions = { Condition.ChatText:new("light the fever grass") },
  },
  {
    text = "Run around the trap with the lit fever grass.",
    postconditions = { Condition.ModelVisible:new(caughtKebit) },
  },
  {
    text = "Do this until all 2 kebbits are caught.",
    postconditions = { Condition.InventoryContains:new(diseasedKebbitFur, 4) },
  },
  {
    text = "Set up a bird snare west of the bank.",
    title = "Wimpy birds",
    neededItems = {
      ["Bird snare"] = { quantity = 1 },
      ["Tansymum"] = { quantity = 8 },
    },
    recommendedItems = {},
    actions = {
      Action.Direction:new(2538, 1173, 2844),
      Action.InventoryHighlight:new(Models.items["bird snare"]),
    },
    postconditions = { Condition.ModelVisible:new(setBirdSnare) },
  },
  {
    text = "Light a tansymum from the inventory and move around.",
    actions = { Action.InventoryHighlight:new(tansymum) },
    postconditions = { Condition.ChatText:new("light the tansymum") },
  },
  {
    text = "Run around the trap until a bird is caught.",
    postconditions = { Condition.ModelVisible:new(caughtWimpyBird) },
  },
  {
    text = "Do this until all birds are caught.",
    warning = "No tracking for this step.",
  },
  {
    text = "Setup a box trap on the marked tile.",
    title = "Platypodes",
    neededItems = {
      ["Box trap"] = { quantity = 1 },
      ["Lavender"] = { quantity = 8 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2528, 973, 2852, { tile = true }) },
    postconditions = { Condition.ModelVisible:new(setBoxTrap) },
  },
  {
    text = "Burn lavender.",
    actions = { Action.InventoryHighlight:new(lavender) },
    postconditions = { Condition.ChatText:new("light the lavender") },
  },
  {
    text = "Move around the area until a platypode is caught.",
    warning = "No tracking for this step.",
  },
  {
    text = "Catch all 4 platypodes.",
    postconditions = { Condition.InventoryContains:new(peter) },
  },
  { postconditions = { Condition.InventoryContains:new(penelope) } },
  { postconditions = { Condition.InventoryContains:new(peanut) } },
  { postconditions = { Condition.InventoryContains:new(patrick) } },
  {
    text = "Stand next to the plants in the water to the east.",
    actions = { Action.Direction:new(2615, 93, 2863, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2615, 93, 2863, 0) },
  },
  {
    text = "Release the platypodes.",
    actions = { Action.InventoryHighlight:new(peter) },
    postconditions = { Condition.InventoryDoesNotContain:new(peter) },
  },
  {
    actions = { Action.InventoryHighlight:new(penelope) },
    postconditions = { Condition.InventoryDoesNotContain:new(penelope) },
  },
  {
    actions = { Action.InventoryHighlight:new(peanut) },
    postconditions = { Condition.InventoryDoesNotContain:new(peanut) },
  },
  {
    actions = { Action.InventoryHighlight:new(patrick) },
    postconditions = { Condition.InventoryDoesNotContain:new(patrick) },
  },
  {
    text = "Talk to Balnea.",
    title = "Finishing up",
    warning = "Dismiss any followers before continuing.",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2557, 1061, 2838) },
    postconditions = { Condition.ModelVisible:new(balnea) },
  },
  {
    actions = { Action.ModelHighlight:new(balnea) },
    jumpconditions = { Condition.ModelNotVisible:new(balnea) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("feel free to use the pools") } }, --not tested
  {
    text = "Talk to Balnea.",
    actions = { Action.ModelHighlight:new(balnea) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "As a First Resort",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1201564800,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Hunter", 48),
    Types.QuestReq.skill("Firemaking", 51),
    Types.QuestReq.skill("Woodcutting", 58),
  },
  neededItems = {
    ["Bird snare"] = { quantity = 1, model = Models.items["bird snare"] },
    ["Box trap"] = { quantity = 1, model = Models.items["box trap"] },
    ["Teasing stick"] = { quantity = 1, model = teasingStick },
    ["Any non-poisoned spear (bone spear recommended)"] = { quantity = 4 },
    ["Achey tree logs"] = { quantity = 8, model = archeyTreeLogs, duringQuest = true },
    ["Eucalyptus logs"] = { quantity = 20, model = eucalyptusLogs, duringQuest = true },
    ["Raw chompy bird"] = { quantity = 2, model = Models.items["raw chompy bird"], duringQuest = true },
    ["Stripy feathers"] = { quantity = 8, model = stripyFeathers, duringQuest = true },
    ["Wolf bones"] = { quantity = 4, model = wolfBones, duringQuest = true },
    ["Normal/tattered larupia furs"] = { quantity = 4, model = larupiaFur, duringQuest = true },
    ["Fever grass"] = { quantity = 14, model = feverGrass, duringQuest = true },
    ["Lavender"] = { quantity = 11, model = lavender, duringQuest = true },
    ["Primweed"] = { quantity = 6, model = primweed, duringQuest = true },
    ["Stinkbloom"] = { quantity = 6, model = stinkbloom, duringQuest = true },
    ["Tansymum"] = { quantity = 12, model = tansymum, duringQuest = true },
    ["Logs"] = { quantity = 4, model = Models.items["logs"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Wolves"] = { level = "11", quantity = 4 },
  },
})
