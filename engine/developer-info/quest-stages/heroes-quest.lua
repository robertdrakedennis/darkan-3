local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local jailer = Model.new(5565, {
  [3718] = Vertex.new(-193, 318, -3, 37, 38, 35),
  [3719] = Vertex.new(-191, 315, -3, 37, 38, 35),
  [3720] = Vertex.new(-191, 321, -3, 37, 38, 35),
  [3721] = Vertex.new(-189, 318, -3, 37, 38, 35),
})
local velrak = Model.new(3831, {
  [2179] = Vertex.new(2, 725, -59, 108, 79, 56),
  [2183] = Vertex.new(7, 724, -51, 108, 79, 56),
  [2197] = Vertex.new(-2, 725, -59, 108, 79, 56),
  [2202] = Vertex.new(-7, 724, -51, 108, 79, 56),
  [3176] = Vertex.new(40, 711, -12, 96, 89, 88),
})
local gerrant = Model.new(4839, {
  [2878] = Vertex.new(-2, 725, -59, 106, 77, 54),
  [2883] = Vertex.new(-7, 724, -51, 106, 77, 54),
  [2886] = Vertex.new(2, 725, -59, 106, 77, 54),
  [2888] = Vertex.new(7, 724, -51, 106, 77, 54),
  [4285] = Vertex.new(0, 735, -7, 27, 138, 126),
})
local achietties = Model.new(5583, {
  [2535] = Vertex.new(24, 738, -38, 30, 28, 27),
  [2559] = Vertex.new(-24, 738, -38, 30, 28, 27),
  [2581] = Vertex.new(-2, 716, -57, 106, 77, 54),
  [2587] = Vertex.new(2, 716, -57, 106, 77, 54),
  [2591] = Vertex.new(6, 716, -52, 106, 77, 54),
})
local firebird = Model.new(3021, {
  [141] = Vertex.new(-8, 356, -304, 193, 179, 18),
  [162] = Vertex.new(8, 356, -304, 193, 179, 18),
  [2969] = Vertex.new(-120, 388, 164, 32, 29, 29),
  [2971] = Vertex.new(-120, 388, 164, 32, 29, 29),
  [2983] = Vertex.new(-72, 424, 172, 32, 29, 29),
})
local trobert = Model.new(4935, {
  [2788] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [2796] = Vertex.new(2, 725, -59, 109, 81, 57),
  [2798] = Vertex.new(7, 724, -51, 109, 81, 57),
  [4800] = Vertex.new(-65, 670, 50, 92, 15, 8),
  [4831] = Vertex.new(65, 670, 50, 92, 15, 8),
})
local iceQueen = Model.new(5475, {
  [2496] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [2641] = Vertex.new(-2, 716, -57, 104, 111, 137),
  [2646] = Vertex.new(-6, 716, -52, 104, 111, 137),
  [2647] = Vertex.new(2, 716, -57, 104, 111, 137),
  [2651] = Vertex.new(6, 716, -52, 104, 111, 137),
})
local grip = Model.new(4743, {
  [1843] = Vertex.new(76, 660, 32, 84, 77, 77),
  [1846] = Vertex.new(72, 652, -20, 84, 77, 77),
  [1920] = Vertex.new(-76, 660, 32, 84, 77, 77),
  [1923] = Vertex.new(-72, 652, -20, 84, 77, 77),
  [3160] = Vertex.new(0, 735, -7, 29, 141, 129),
})
--#endregion
--#region Objects
local fishingBubbles = Model.new(576, {
  [397] = Vertex.new(160, 236, -96, 61, 34, 5),
  [463] = Vertex.new(-164, 236, -120, 61, 34, 5),
  [466] = Vertex.new(-164, 236, -120, 61, 34, 5),
  [499] = Vertex.new(88, 236, 148, 61, 34, 5),
  [571] = Vertex.new(-168, 236, 108, 61, 34, 5),
})
local rockslide = Model.any({
  Model.new(5037, {
    [1266] = Vertex.new(4219, 11002, 8174, 127, 127, 127),
    [1269] = Vertex.new(4219, 11002, 8174, 127, 127, 127),
    [3390] = Vertex.new(4262, 10924, 7926, 127, 127, 127),
    [3981] = Vertex.new(4473, 10894, 7715, 127, 127, 127),
    [3999] = Vertex.new(4340, 10916, 7874, 127, 127, 127),
  }),
  Model.new(5037, {
    [4668] = Vertex.new(4196, 11136, 8785, 127, 127, 127),
    [4692] = Vertex.new(4151, 11139, 8782, 127, 127, 127),
    [4719] = Vertex.new(4342, 11041, 8704, 127, 127, 127),
    [4737] = Vertex.new(4196, 11136, 8785, 127, 127, 127),
    [4995] = Vertex.new(4059, 11133, 8769, 127, 127, 127),
  }),
})
--#endregion
--#region Items
local fishingRod = Model.new(180, {
  [175] = Vertex.new(-36, 44, -76, 0, 0, 0),
  [176] = Vertex.new(-28, 44, -84, 0, 0, 0),
  [177] = Vertex.new(-36, 44, -84, 0, 0, 0),
  [178] = Vertex.new(-36, 44, -76, 0, 0, 0),
  [179] = Vertex.new(-28, 44, -76, 0, 0, 0),
})
local harralanderPotionUnf = Model.multi({
  Model.new(90, {
    [2] = Vertex.new(-20, 52, 8, 54, 106, 55),
    [11] = Vertex.new(-12, 52, -20, 54, 106, 55),
    [14] = Vertex.new(12, 52, 20, 54, 106, 55),
    [23] = Vertex.new(20, 52, -16, 54, 106, 55),
    [67] = Vertex.new(-4, 96, 12, 112, 100, 45),
    [68] = Vertex.new(-4, 112, 12, 112, 100, 45),
    [69] = Vertex.new(-12, 112, 4, 112, 100, 45),
    [72] = Vertex.new(-12, 96, 4, 112, 100, 45),
    [74] = Vertex.new(-4, 112, -12, 112, 100, 45),
    [75] = Vertex.new(-12, 112, -4, 112, 100, 45),
    [77] = Vertex.new(4, 112, -12, 112, 100, 45),
    [80] = Vertex.new(12, 112, -4, 112, 100, 45),
    [83] = Vertex.new(12, 112, 4, 112, 100, 45),
    [86] = Vertex.new(4, 112, 12, 112, 100, 45),
  }),
  Model.new(288, {
    [145] = Vertex.new(-16, 68, 4, 133, 135, 146, 0.4980),
    [152] = Vertex.new(-12, 84, -4, 133, 135, 146, 0.4980),
    [153] = Vertex.new(-4, 84, -12, 133, 135, 146, 0.4980),
    [186] = Vertex.new(4, 84, -12, 133, 135, 146, 0.4980),
    [241] = Vertex.new(-4, 96, 20, 133, 135, 146, 0.4980),
    [242] = Vertex.new(4, 96, 12, 133, 135, 146, 0.4980),
    [243] = Vertex.new(-4, 96, 12, 133, 135, 146, 0.4980),
    [244] = Vertex.new(-12, 96, 4, 133, 135, 146, 0.4980),
    [248] = Vertex.new(4, 96, 20, 133, 135, 146, 0.4980),
    [251] = Vertex.new(-20, 96, 4, 133, 135, 146, 0.4980),
    [254] = Vertex.new(12, 96, 4, 133, 135, 146, 0.4980),
    [256] = Vertex.new(-20, 96, -4, 133, 135, 146, 0.4980),
    [260] = Vertex.new(20, 96, 4, 133, 135, 146, 0.4980),
    [264] = Vertex.new(-12, 96, -4, 133, 135, 146, 0.4980),
    [265] = Vertex.new(12, 96, -4, 133, 135, 146, 0.4980),
    [270] = Vertex.new(-4, 96, -12, 133, 135, 146, 0.4980),
    [273] = Vertex.new(20, 96, -4, 133, 135, 146, 0.4980),
    [276] = Vertex.new(-4, 96, -20, 133, 135, 146, 0.4980),
    [277] = Vertex.new(4, 96, -12, 133, 135, 146, 0.4980),
    [281] = Vertex.new(4, 96, -20, 133, 135, 146, 0.4980),
  }),
})
local blackFullHelm = Model.new(564, {
  [227] = Vertex.new(29, 93, -10, 79, 72, 79),
  [288] = Vertex.new(-29, 93, -10, 79, 72, 79),
  [402] = Vertex.new(39, 230, 36, 49, 45, 45),
  [417] = Vertex.new(39, 230, 36, 39, 36, 36),
  [506] = Vertex.new(-39, 230, 36, 49, 45, 45),
})
local blackPlatebody = Model.new(918, {
  [240] = Vertex.new(147, 2, -122, 109, 100, 109),
  [336] = Vertex.new(119, 2, 163, 9, 8, 8),
  [339] = Vertex.new(119, 2, 163, 49, 45, 45),
  [419] = Vertex.new(-119, 2, 163, 9, 8, 8),
  [422] = Vertex.new(-119, 2, 163, 49, 45, 45),
})
local blackPlatelegs = Model.new(402, {
  [1] = Vertex.new(-63, 2, -161, 0, 0, 0),
  [75] = Vertex.new(-26, 34, 149, 99, 20, 26),
  [87] = Vertex.new(26, 34, 149, 99, 20, 26),
  [134] = Vertex.new(-63, 2, -161, 39, 36, 39),
  [168] = Vertex.new(63, 2, -161, 39, 36, 39),
})
--#endregion
--#region Quest Items
local jailKey = Model.new(444, {
  [2] = Vertex.new(-12, 16, -84, 122, 87, 11),
  [86] = Vertex.new(52, 16, 80, 122, 87, 11),
  [99] = Vertex.new(68, 16, 68, 122, 87, 11),
  [104] = Vertex.new(68, 16, 68, 122, 87, 11),
  [397] = Vertex.new(68, 16, 68, 122, 87, 11),
})
local blamishSnailSlime = Model.new(372, {
  [308] = Vertex.new(-4, 164, 12, 112, 100, 45),
  [312] = Vertex.new(-4, 164, 12, 112, 100, 45),
  [341] = Vertex.new(-12, 164, -4, 112, 100, 45),
  [344] = Vertex.new(-12, 164, -4, 112, 100, 45),
  [348] = Vertex.new(-12, 164, -4, 112, 100, 45),
})
local blamishOil = Model.multi({
  Model.new(66, {
    [2] = Vertex.new(4, 112, -12, 112, 100, 45),
    [3] = Vertex.new(4, 96, -12, 112, 100, 45),
    [9] = Vertex.new(-4, 96, 12, 112, 100, 45),
    [11] = Vertex.new(4, 112, 12, 112, 100, 45),
    [12] = Vertex.new(-4, 112, 12, 112, 100, 45),
    [14] = Vertex.new(-4, 112, -12, 112, 100, 45),
    [15] = Vertex.new(-4, 96, -12, 112, 100, 45),
    [20] = Vertex.new(-12, 112, -4, 112, 100, 45),
    [21] = Vertex.new(-12, 96, -4, 112, 100, 45),
    [23] = Vertex.new(-12, 112, 4, 112, 100, 45),
  }),
  Model.new(240, {
    [49] = Vertex.new(-4, 68, 16, 94, 87, 86, 0.8745),
    [50] = Vertex.new(-4, 84, 12, 94, 87, 86, 0.8745),
    [53] = Vertex.new(-12, 84, 4, 94, 87, 86, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 94, 87, 86, 0.8745),
    [60] = Vertex.new(-12, 84, -4, 94, 87, 86, 0.8745),
    [68] = Vertex.new(-40, 12, 12, 94, 87, 86, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 94, 87, 86, 0.8745),
    [93] = Vertex.new(-4, 68, -16, 94, 87, 86, 0.8745),
    [95] = Vertex.new(-4, 84, -12, 94, 87, 86, 0.8745),
    [96] = Vertex.new(4, 84, -12, 94, 87, 86, 0.8745),
    [131] = Vertex.new(12, 84, -4, 94, 87, 86, 0.8745),
    [132] = Vertex.new(12, 84, 4, 94, 87, 86, 0.8745),
    [152] = Vertex.new(40, 12, -12, 94, 87, 86, 0.8745),
    [153] = Vertex.new(40, 12, 12, 94, 87, 86, 0.8745),
    [157] = Vertex.new(32, 0, -4, 94, 87, 86, 0.8745),
    [159] = Vertex.new(32, 0, 4, 94, 87, 86, 0.8745),
    [177] = Vertex.new(12, 12, 40, 94, 87, 86, 0.8745),
    [183] = Vertex.new(4, 0, 36, 94, 87, 86, 0.8745),
    [186] = Vertex.new(-12, 12, 40, 94, 87, 86, 0.8745),
    [192] = Vertex.new(-4, 0, 36, 94, 87, 86, 0.8745),
  }),
})
local rawLavaEel = Model.new(156, {
  [1] = Vertex.new(112, 16, 112, 152, 72, 13),
  [2] = Vertex.new(64, 32, 96, 152, 72, 13),
  [3] = Vertex.new(64, 0, 96, 152, 72, 13),
  [5] = Vertex.new(96, 32, 64, 152, 72, 13),
  [11] = Vertex.new(96, 0, 64, 152, 72, 13),
  [20] = Vertex.new(0, 32, 32, 152, 72, 13),
  [23] = Vertex.new(32, 32, 0, 152, 72, 13),
  [29] = Vertex.new(32, 0, 0, 152, 72, 13),
  [133] = Vertex.new(32, 56, 32, 152, 119, 13),
  [134] = Vertex.new(64, 32, 64, 152, 119, 13),
  [135] = Vertex.new(32, 32, 32, 152, 119, 13),
  [139] = Vertex.new(16, 56, -64, 152, 119, 13),
  [140] = Vertex.new(16, 32, -16, 152, 119, 13),
  [141] = Vertex.new(16, 32, -64, 152, 119, 13),
  [145] = Vertex.new(-64, 48, -104, 152, 119, 13),
  [146] = Vertex.new(-16, 32, -88, 152, 119, 13),
  [147] = Vertex.new(-64, 32, -104, 152, 119, 13),
  [151] = Vertex.new(-152, 48, -56, 152, 119, 13),
  [152] = Vertex.new(-112, 32, -96, 152, 119, 13),
  [153] = Vertex.new(-152, 32, -56, 152, 119, 13),
})
local cookedLavaEel = Model.new(156, {
  [1] = Vertex.new(112, 16, 112, 105, 74, 9),
  [2] = Vertex.new(64, 32, 96, 105, 74, 9),
  [3] = Vertex.new(64, 0, 96, 105, 74, 9),
  [5] = Vertex.new(96, 32, 64, 105, 74, 9),
  [11] = Vertex.new(96, 0, 64, 105, 74, 9),
  [20] = Vertex.new(0, 32, 32, 105, 74, 9),
  [23] = Vertex.new(32, 32, 0, 105, 74, 9),
  [29] = Vertex.new(32, 0, 0, 105, 74, 9),
  [133] = Vertex.new(32, 56, 32, 88, 56, 7),
  [134] = Vertex.new(64, 32, 64, 88, 56, 7),
  [135] = Vertex.new(32, 32, 32, 88, 56, 7),
  [139] = Vertex.new(16, 56, -64, 88, 56, 7),
  [140] = Vertex.new(16, 32, -16, 88, 56, 7),
  [141] = Vertex.new(16, 32, -64, 88, 56, 7),
  [145] = Vertex.new(-64, 48, -104, 88, 56, 7),
  [146] = Vertex.new(-16, 32, -88, 88, 56, 7),
  [147] = Vertex.new(-64, 32, -104, 88, 56, 7),
  [151] = Vertex.new(-152, 48, -56, 88, 56, 7),
  [152] = Vertex.new(-112, 32, -96, 88, 56, 7),
  [153] = Vertex.new(-152, 32, -56, 88, 56, 7),
})
local idPapers = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 149, 148, 136),
  [5] = Vertex.new(-72, 0, -56, 149, 148, 136),
  [187] = Vertex.new(-84, 28, -84, 92, 66, 8),
  [190] = Vertex.new(-84, 28, -84, 92, 66, 8),
  [195] = Vertex.new(-84, 28, -84, 92, 66, 8),
})
local miscKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 123, 89, 11),
  [104] = Vertex.new(68, 16, 68, 123, 89, 11),
  [397] = Vertex.new(68, 16, 68, 123, 89, 11),
  [400] = Vertex.new(68, 16, 68, 123, 89, 11),
  [408] = Vertex.new(68, 16, 68, 123, 89, 11),
})
local candlestick = Model.new(102, {
  [3] = Vertex.new(-16, 132, -24, 155, 133, 14),
  [5] = Vertex.new(24, 132, -12, 155, 133, 14),
  [14] = Vertex.new(-16, 132, 24, 155, 133, 14),
  [17] = Vertex.new(-32, 132, 12, 155, 133, 14),
  [18] = Vertex.new(-16, 132, 24, 155, 133, 14),
})
local armband = Model.new(138, {
  [128] = Vertex.new(16, 4, 32, 92, 15, 8),
  [129] = Vertex.new(16, 12, 32, 92, 15, 8),
  [130] = Vertex.new(-16, 4, 32, 92, 15, 8),
  [132] = Vertex.new(-16, 12, 32, 92, 15, 8),
  [137] = Vertex.new(0, 28, 32, 92, 15, 8),
})
local iceGloves = Model.new(312, {
  [73] = Vertex.new(112, 36, -72, 80, 140, 157),
  [78] = Vertex.new(112, 36, -72, 80, 140, 157),
  [92] = Vertex.new(-72, 44, -88, 80, 140, 157),
  [127] = Vertex.new(112, 36, -72, 80, 140, 157),
  [271] = Vertex.new(112, 36, -72, 80, 140, 157),
})
local firebirdFeather = Model.new(174, {
  [5] = Vertex.new(64, 0, 32, 155, 75, 14),
  [9] = Vertex.new(64, -4, -24, 155, 75, 14),
  [10] = Vertex.new(64, -4, 32, 155, 75, 14),
  [116] = Vertex.new(124, 0, 16, 2, 30, 3),
  [120] = Vertex.new(124, -4, 8, 2, 30, 3),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Achietties outside the Heroes' Guild in Burthorpe.",
    title = "Starting out",
    actions = {
      Action.Direction:new(2918, 1093, 3514, { distance = 16 }),
      Action.ModelHighlight:new(achietties, { distance = 16 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Achietties.",
    actions = {
      Action.Direction:new(2918, 1093, 3514, { distance = 16 }),
      Action.ModelHighlight:new(achietties, { distance = 16 }),
    },
    postconditions = {
      Condition.ConversationText:new("Lava Eel"),
      Condition.ConversationText:new("lava Eel"),
    },
  },
  {
    text = "Talk to Gerrant, the fishing shop owner in Port Sarim.<ul><li>Buy a fishing rod and fishing bait from Gerrant.</li></ul>",
    title = "Lava eel",
    neededItems = {
      ["Fishing rod"] = { quantity = 1 },
      ["Fishing bait"] = { quantity = 1 },
      ["Harralander potion (unfinished)"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3013, 965, 3224, { distance = 6 }),
      Action.ModelHighlight:new(gerrant, { distance = 8 }),
      Action.ConversationHighlight:new("I want to find out how to catch a lava eel."),
    },
    postconditions = { Condition.InventoryContains:new(blamishSnailSlime) },
  },
  {
    text = "Use the blamish snail slime on your harralander potion (unfinished).",
    actions = {
      Action.InventoryHighlight:new(blamishSnailSlime),
      Action.InventoryHighlight:new(harralanderPotionUnf),
    },
    postconditions = { Condition.InventoryContains:new(blamishOil) },
  },
  {
    text = "Use the blamish oil on your fishing rod.",
    actions = {
      Action.InventoryHighlight:new(fishingRod),
      Action.InventoryHighlight:new(blamishOil, true),
    },
    postconditions = {
      -- Condition.InventoryContains:new(oilyFishingRod),
      Condition.ChatText:new("rub the oil into"), --its skin, or
    },
  },
  {
    text = "Use the Taverly lodestone and enter the Taverley Dungeon to the south.",
    warning = "If you have the dusty key on the keyring, manually skip ahead to 'Open the gate with the dusty key in your inventory'.",
    neededItems = {
      ["Dusty key"] = { quantity = 1 },
      ["Food if lower combat"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2885, 493, 3397) },
    postconditions = { Condition.DistanceToWithHeight:new(2886, 1061, 9795, 3) },
  },
  {
    text = "Follow the path to the Black Knights compound.",
    actions = { Action.Direction:new(2888.5, 1285, 9830.5) },
    postconditions = {
      Condition.DistanceTo:new(2891, 1285, 9830, 2),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2892.5, 1421, 9825.5) },
    postconditions = {
      Condition.DistanceTo:new(2893, 1597, 9823, 2),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2911, 1261, 9819) },
    postconditions = {
      Condition.DistanceTo:new(2906, 1245, 9819, 4),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2940, 701, 9806) },
    postconditions = {
      Condition.DistanceTo:new(2940, 701, 9806, 8),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2936, 965, 9777) },
    postconditions = {
      Condition.DistanceTo:new(2936, 965, 9777, 8),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2931, 933, 9755) },
    postconditions = {
      Condition.DistanceTo:new(2931, 933, 9755, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]), -- don't have 70 agility, but have key in inventory
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2914, 949, 9741) },
    postconditions = {
      Condition.DistanceTo:new(2914, 949, 9741, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2908, 1000, 9712) },
    postconditions = {
      Condition.DistanceTo:new(2908, 1000, 9712, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Go to the cells to the south-east and kill the Jailer.",
    actions = { Action.Direction:new(2931, 985, 9692) },
    postconditions = {
      Condition.DistanceTo:new(2931, 985, 9692, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.ModelHighlight:new(jailer) },
    postconditions = {
      Condition.ModelVisible:new(jailKey),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Pick up the jail key.",
    actions = { Action.ModelHighlight:new(jailKey) },
    postconditions = {
      Condition.InventoryContains:new(jailKey),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Use the jail key on the cell door.",
    actions = { Action.Direction:new(2931, 965, 9689.5), Action.InventoryHighlight:new(jailKey) },
    postconditions = {
      Condition.DistanceTo:new(2931, 965, 9687, 2),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Talk to Velrak the explorer for a dusty key.",
    actions = {
      Action.ModelHighlight:new(velrak),
      Action.ConversationHighlight:new("So...do you know anywhere good to explore?"),
      Action.ConversationHighlight:new("Yes, please!"),
    },
    postconditions = {
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Circle back the way you came and bridge you passed earlier.",
    actions = { Action.Direction:new(2927, 925, 9756) },
    postconditions = {
      Condition.DistanceTo:new(2936, 965, 9777, 8), -- if entered dungeon with key in inventory
      Condition.DistanceTo:new(2927, 925, 9756, 8),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Open the gate with the dusty key in your inventory.",
    actions = { Action.Direction:new(2923.5, 1173, 9803) },
    postconditions = {
      Condition.DistanceTo:new(2921, 1125, 9803, 2),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Run past the blue dragons.",
    actions = { Action.Direction:new(2895, 1213, 9796) },
    postconditions = {
      Condition.DistanceTo:new(2894, 1373, 9792, 4),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Catch a lava eel.",
    actions = {
      Action.Direction:new(2893, 965, 9769, { distance = 10 }),
      Action.ModelHighlight:new(fishingBubbles, { distance = 8, highlightPriority = "closest" }),
    },
    postconditions = { Condition.InventoryContains:new(rawLavaEel) },
  },
  { text = "Cook the eel on a range or fire.", postconditions = { Condition.InventoryContains:new(cookedLavaEel) } },
  {
    text = "Talk to Katrine.",
    title = "Master thief's armband - Black Arm gang",
    neededItems = {
      ["Lockpick"] = { quantity = 5 },
      ["Black full helm"] = { quantity = 1 },
      ["Black platebody"] = { quantity = 1 },
      ["Black platelegs"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3185, 1125, 3385, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["katrine"], { distance = 12 }),
      Action.ConversationHighlight:new("How can I get a master thief's armband?"),
    },
    postconditions = { Condition.ConversationText:new("four leafed clover") },
  },
  {
    text = "Lodestone to Karamja and enter the hideout.",
    actions = {
      Action.Direction:new(2810.5, 2109, 3170),
      Action.ConversationHighlight:new("Four leaved clover."),
    },
    postconditions = { Condition.DistanceTo:new(2813, 1509, 3172, 2) },
  },
  {
    text = "Talk to Trobert.",
    actions = {
      Action.ModelHighlight:new(trobert),
      Action.ConversationHighlight:new("So can you help me get Scarface Pete's candlesticks?"),
      Action.ConversationHighlight:new("I volunteer to undertake that mission!"),
    },
    postconditions = { Condition.InventoryContains:new(idPapers) },
  },
  {
    text = "Equip your Black armour.",
    actions = { Action.InventoryHighlight:new(blackFullHelm) },
    postconditions = { Condition.InventoryDoesNotContain:new(blackFullHelm) },
  },
  {
    actions = { Action.InventoryHighlight:new(blackPlatebody) },
    postconditions = { Condition.InventoryDoesNotContain:new(blackPlatebody) },
  },
  {
    actions = { Action.InventoryHighlight:new(blackPlatelegs) },
    postconditions = { Condition.InventoryDoesNotContain:new(blackPlatelegs) },
  },
  {
    text = "Enter the large mansion in the north-west part of town.",
    actions = { Action.Direction:new(2774, 2429, 3187.5) },
    postconditions = { Condition.DistanceTo:new(2774, 1861, 3189, 1) },
  },
  {
    text = "Talk to Grip.",
    actions = {
      Action.ModelHighlight:new(grip),
      Action.ConversationHighlight:new("So what do my duties involve?"),
    },
    postconditions = { Condition.InventoryContains:new(miscKey) },
  },
  {
    text = "Go to the north-west part of the building and open the Treasure room door with a lockpick to enter.",
    actions = { Action.Direction:new(2763.5, 1829, 3197) },
    postconditions = { Condition.DistanceTo:new(2765, 1829, 3197, 1) },
  },
  {
    text = "Open and search the northern chest.",
    actions = { Action.Direction:new(2766, 2029, 3199) },
    postconditions = { Condition.InventoryContains:new(candlestick) },
  },
  {
    text = "Bring the candlestick back to Katrine to receive a Master Thief's Armband.",
    actions = {
      Action.Direction:new(3185, 1125, 3385, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["katrine"], { distance = 12 }),
      Action.ConversationHighlight:new("I have a candlestick now."),
    },
    postconditions = { Condition.InventoryContains:new(armband) },
  },
  {
    text = "Lodestone to Burthope and enter the cave on the mountain.",
    title = "Ice gloves",
    warning = "If you already have the ice gloves, withrdaw them from your bank or unequip them.",
    actions = { Action.Direction:new(2851, 5533, 3505.5) },
    postconditions = {
      Condition.DistanceTo:new(2825, 11941, 3524, 4),
      Condition.InventoryContains:new(iceGloves),
    },
  },
  {
    text = "Mine the rock slide.",
    actions = { Action.ModelHighlight:new(rockslide) },
    postconditions = {
      Condition.DistanceTo:new(2841, 11029, 3520, 1),
      Condition.InventoryContains:new(iceGloves),
    },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2848, 10869, 3513) },
    postconditions = {
      Condition.DistanceTo:new(2847, 1093, 9912, 2),
      Condition.InventoryContains:new(iceGloves),
    },
  },
  {
    text = "Climb up the ladder to the west.",
    warning = "Be careful of the ice giants if you're low level.",
    actions = { Action.Direction:new(2824, 1405, 9907) },
    postconditions = {
      Condition.DistanceTo:new(2821, 18277, 3507, 4),
      Condition.InventoryContains:new(iceGloves),
    },
  },
  {
    text = "Climb down the laldder.",
    actions = { Action.Direction:new(2825, 18277, 3510) },
    postconditions = { Condition.DistanceTo:new(2827, 901, 9911, 1), Condition.InventoryContains:new(iceGloves) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = { Action.Direction:new(2857, 1893, 9917) },
    postconditions = {
      Condition.DistanceTo:new(2855, 8981, 3516, 4),
      Condition.InventoryContains:new(iceGloves),
    },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2857, 8861, 3519) },
    postconditions = {
      Condition.DistanceTo:new(2860, 1093, 9920, 2),
      Condition.InventoryContains:new(iceGloves),
    },
  },
  {
    text = "Kill the ice queen and pick up the ice gloves.",
    actions = {
      Action.Direction:new(2866, 981, 9953, { distance = 16 }),
      Action.ModelHighlight:new(iceQueen, { distance = 16 }),
      Action.ModelHighlight:new(iceGloves),
    },
    postconditions = { Condition.InventoryContains:new(iceGloves) },
  },
  {
    text = "Sail to Entrana or teleport to the law altar using the wicked hood or through the Abyss.",
    title = "Entrana firebird feather",
    neededItems = { ["Ice gloves"] = { quantity = 1 }, ["Entrana"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3045, 741, 3235, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["monk of entrana"], { distance = 20, highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = {
      Condition.DistanceTo:new(2834, 965, 3335, 4),
      Condition.DistanceTo:new(2846, 1965, 3381, 16),
    },
  },
  {
    text = "Equip the ice gloves, kill the Firebird, and pick up the firebird feather.",
    actions = {
      Action.Direction:new(2846, 1965, 3381, { distance = 16 }),
      Action.ModelHighlight:new(firebird, { distance = 20 }),
      Action.ModelHighlight:new(firebirdFeather),
      Action.InventoryHighlight:new(iceGloves),
    },
    postconditions = { Condition.InventoryContains:new(firebirdFeather) },
  },
  {
    text = "Take the feather, eel, and armband to Achietties outside the Heroes' Guild.",
    title = "Finishing up",
    neededItems = {
      ["Entranan firebird feather"] = { quantity = 1 },
      ["Lava eel"] = { quantity = 1 },
      ["Master thief's armband"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(2918, 1093, 3514, { distance = 16 }),
      Action.ModelHighlight:new(achietties, { distance = 16 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Heroes' Quest",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1014768000,
  prereqQuests = { "Shield of Arrav", "Dragon Slayer" },
  questReqs = {
    Types.QuestReq.skill("Cooking", 53),
    Types.QuestReq.skill("Fishing", 53),
    Types.QuestReq.skill("Herblore", 25),
    Types.QuestReq.skill("Mining", 50),
    Types.QuestReq.skill("Defence", 25),
    Types.QuestReq.questpoints(56),
  },
  neededItems = {
    ["Ice gloves"] = { quantity = 1, model = iceGloves, duringQuest = true },
    ["Harralander potion (unfinished)"] = { quantity = 1 },
    ["Fishing rod"] = { quantity = 1, model = fishingRod, duringQuest = true },
    ["Fishing bait"] = { quantity = 5, model = Models.items["fishing bait"], duringQuest = true },
    ["Lockpick"] = { quantity = 5, model = Models.items["lockpick"], duringQuest = true },
    ["BAG: Black full helm"] = { quantity = 1, model = blackFullHelm },
    ["BAG: Black platebody"] = { quantity = 1, model = blackPlatebody },
    ["BAG: Black platelegs"] = { quantity = 1, model = blackPlatelegs },
    ["PG: Coins or Ring of Charos (a)"] = { quantity = 1000 },
    ["PG: Magic/ranged weapon"] = { quantity = 1 },
  },
  recommendedItems = { ["Dusty key"] = { quantity = 1 } },
  combatNPCs = {
    ["Entranan firebird"] = { level = "1" },
    ["Grip"] = { level = "21" },
    ["Jailer"] = { level = "39" },
    ["Ice Queen"] = { level = "77" },
  },
})
