local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local anyMeiyerditchCitizen = Model.any({
  Model.new(3900, {
    [2118] = Vertex.new(24, 738, -38, 30, 29, 28),
    [2142] = Vertex.new(-24, 738, -38, 30, 29, 28),
    [2281] = Vertex.new(-2, 716, -57, 111, 102, 101),
    [2287] = Vertex.new(2, 716, -57, 111, 102, 101),
    [2291] = Vertex.new(6, 716, -52, 111, 102, 101),
  }),
  Model.new(3231, {
    [1654] = Vertex.new(-2, 725, -59, 111, 102, 101),
    [1659] = Vertex.new(-7, 724, -51, 111, 102, 101),
    [1662] = Vertex.new(2, 725, -59, 111, 102, 101),
    [1664] = Vertex.new(7, 724, -51, 111, 102, 101),
    [2635] = Vertex.new(0, 735, -7, 28, 139, 127),
  }),
  Model.new(3903, {
    [2332] = Vertex.new(-2, 725, -59, 111, 102, 101),
    [2337] = Vertex.new(-7, 724, -51, 111, 102, 101),
    [2340] = Vertex.new(2, 725, -59, 111, 102, 101),
    [2342] = Vertex.new(7, 724, -51, 111, 102, 101),
    [3579] = Vertex.new(29, 727, -29, 95, 88, 87),
  }),
})
local oldManRal = Model.new(3501, {
  [2041] = Vertex.new(-2, 725, -59, 107, 98, 97),
  [2046] = Vertex.new(-7, 724, -51, 107, 98, 97),
  [2049] = Vertex.new(2, 725, -59, 107, 98, 97),
  [2051] = Vertex.new(7, 724, -51, 107, 98, 97),
  [2866] = Vertex.new(0, 735, -7, 28, 139, 127),
})
local vertidaSefalatis = Model.new(2922, {
  [10] = Vertex.new(0, 722, -50, 128, 127, 127),
  [25] = Vertex.new(0, 699, -50, 128, 127, 127),
  [198] = Vertex.new(4, 710, -50, 127, 127, 127),
  [204] = Vertex.new(-4, 715, -50, 127, 127, 127),
  [714] = Vertex.new(-12, 456, -61, 127, 127, 127),
})

--#endregion
--#region Objects
local brokenBoat = Model.new(1239, {
  [43] = Vertex.new(28, 188, -672, 53, 58, 36),
  [53] = Vertex.new(-32, 184, 680, 53, 58, 36),
  [59] = Vertex.new(-28, 80, -600, 53, 58, 36),
  [1182] = Vertex.new(32, 184, 680, 53, 58, 36),
  [1239] = Vertex.new(-36, 188, -672, 53, 58, 36),
})
local brokenBoatChute = Model.new(162, {
  [20] = Vertex.new(256, -88, 192, 42, 49, 20),
  [24] = Vertex.new(256, -88, 192, 42, 49, 20),
  [47] = Vertex.new(-208, -56, -256, 42, 49, 20),
  [92] = Vertex.new(256, -98, 112, 42, 49, 20),
  [137] = Vertex.new(256, -130, -144, 42, 49, 20),
})
local fixedBoat = Model.new(1209, {
  [905] = Vertex.new(-32, 184, 680, 53, 58, 36),
  [1128] = Vertex.new(-36, 188, -672, 53, 58, 36),
  [1143] = Vertex.new(28, 188, -672, 53, 58, 36),
  [1146] = Vertex.new(-32, 184, 680, 53, 58, 36),
  [1191] = Vertex.new(32, 184, 680, 53, 58, 36),
})
local flatDoor = Model.new(159, {
  [51] = Vertex.new(-256, -172, -256, 37, 37, 28),
  [77] = Vertex.new(-256, -172, -256, 37, 37, 28),
  [81] = Vertex.new(-256, -100, 256, 37, 37, 28),
  [132] = Vertex.new(316, -108, 180, 37, 37, 28),
  [159] = Vertex.new(320, -92, -256, 37, 37, 28),
})
local door = Model.new(174, {
  [3] = Vertex.new(584, 32, 12, 115, 133, 150),
  [5] = Vertex.new(560, 32, -12, 115, 133, 150),
  [102] = Vertex.new(392, 1184, 256, 37, 37, 28),
  [114] = Vertex.new(416, 1184, -192, 37, 37, 28),
  [159] = Vertex.new(608, 32, -256, 37, 37, 28),
})
local flatDoor2 = Model.new(159, {
  [69] = Vertex.new(320, 68, -256, 37, 37, 28),
  [77] = Vertex.new(-256, -12, -256, 37, 37, 28),
  [81] = Vertex.new(-256, 60, 256, 37, 37, 28),
  [132] = Vertex.new(316, 52, 180, 37, 37, 28),
  [159] = Vertex.new(320, 68, -256, 37, 37, 28),
})
local table = Model.new(192, {
  [2] = Vertex.new(-256, 0, -256, 37, 34, 34),
  [3] = Vertex.new(-256, 0, 256, 37, 34, 34),
  [5] = Vertex.new(256, 0, -256, 37, 34, 34),
  [140] = Vertex.new(-248, 212, 240, 60, 53, 46),
  [164] = Vertex.new(248, 196, 240, 60, 53, 46),
})
local table2 = Model.new(462, {
  [407] = Vertex.new(248, 196, 240, 60, 53, 46),
  [410] = Vertex.new(-248, 212, 240, 60, 53, 46),
  [413] = Vertex.new(-248, 212, 240, 60, 53, 46),
  [419] = Vertex.new(-232, 208, -240, 60, 53, 46),
  [443] = Vertex.new(-248, 212, 240, 60, 53, 46),
})
local trapdoorTunnel = Model.new(78, {
  [7] = Vertex.new(256, -368, 108, 42, 29, 13),
  [11] = Vertex.new(-256, 0, -256, 58, 48, 36),
  [17] = Vertex.new(256, 0, -256, 58, 48, 36),
  [62] = Vertex.new(256, -368, 108, 0, 0, 0),
  [76] = Vertex.new(256, -368, 108, 42, 29, 13),
})
local wallWithLadderTop = Model.new(354, {
  [188] = Vertex.new(-296, 645, 172, 58, 51, 44),
  [204] = Vertex.new(-296, 645, 172, 51, 46, 39),
  [210] = Vertex.new(-200, 645, -172, 51, 46, 39),
  [321] = Vertex.new(-64, 1198, 192, 41, 41, 31),
  [348] = Vertex.new(-40, 1198, -256, 41, 41, 31),
})
local brokenLadderTop = Model.new(264, {
  [77] = Vertex.new(-188, 0, 184, 54, 48, 42),
  [79] = Vertex.new(-188, 0, 184, 54, 48, 42),
  [82] = Vertex.new(-180, 0, 192, 54, 48, 42),
  [83] = Vertex.new(-172, 0, 184, 54, 48, 42),
  [191] = Vertex.new(184, 0, 172, 54, 48, 42),
})
local lumpyRug = Model.new(60, {
  [33] = Vertex.new(-192, 56, -212, 12, 30, 22),
  [35] = Vertex.new(-128, 56, -256, 12, 30, 22),
  [38] = Vertex.new(-256, 56, -192, 12, 30, 22),
})
local hideoutTrapdoor = Model.any({
  Model.new(714, {
    [573] = Vertex.new(-8, -760, 128, 35, 30, 3),
    [585] = Vertex.new(52, -944, 128, 35, 30, 3),
    [687] = Vertex.new(260, 480, -212, 12, 30, 22),
    [689] = Vertex.new(260, 416, -256, 12, 30, 22),
    [692] = Vertex.new(260, 544, -192, 12, 30, 22),
  }),
  Model.new(714, {
    [573] = Vertex.new(-8, -760, 128, 36, 31, 3),
    [585] = Vertex.new(52, -944, 128, 36, 31, 3),
    [687] = Vertex.new(260, 480, -212, 13, 31, 23),
    [689] = Vertex.new(260, 416, -256, 13, 31, 23),
    [692] = Vertex.new(260, 544, -192, 13, 31, 23),
  }),
})
local bushWithBody = Model.new(108, {
  [2] = Vertex.new(403, 9, 225, 34, 87, 18),
  [7] = Vertex.new(403, 9, 225, 34, 87, 18),
  [54] = Vertex.new(465, 61, 87, 66, 144, 45),
  [55] = Vertex.new(259, 95, 237, 66, 144, 45),
  [62] = Vertex.new(286, 87, -207, 66, 144, 45),
})
local minecart = Model.new(2556, {
  [17] = Vertex.new(-155, 0, -256, 66, 60, 60),
  [35] = Vertex.new(-136, 25, 256, 45, 42, 41),
  [45] = Vertex.new(-143, 25, -256, 100, 92, 91),
  [95] = Vertex.new(136, 25, 256, 45, 42, 41),
  [105] = Vertex.new(143, 25, -256, 100, 92, 91),
})
local fullMinecart = Model.new(3639, {
  [45] = Vertex.new(-143, 25, -256, 100, 92, 91),
  [95] = Vertex.new(136, 25, 256, 45, 42, 41),
  [105] = Vertex.new(143, 25, -256, 100, 92, 91),
  [446] = Vertex.new(-114, 393, 204, 10, 112, 108),
  [1035] = Vertex.new(110, 393, 204, 5, 59, 57),
})
local rockySurface = Model.new(309, {
  [151] = Vertex.new(4352, 5945, 6144, 63, 68, 65),
  [186] = Vertex.new(4596, 5770, 6144, 59, 59, 45),
  [188] = Vertex.new(4596, 5925, 6072, 59, 59, 45),
  [192] = Vertex.new(4568, 5770, 6048, 59, 59, 45),
  [197] = Vertex.new(4568, 5915, 5816, 59, 59, 45),
})
local fireplace = Model.new(840, {
  [279] = Vertex.new(6076, 5165, 2936, 30, 37, 23),
  [585] = Vertex.new(5700, 5545, 2896, 30, 37, 23),
  [633] = Vertex.new(6076, 5330, 2736, 30, 37, 23),
  [657] = Vertex.new(6076, 5545, 2744, 30, 37, 23),
  [729] = Vertex.new(6000, 5475, 2628, 30, 37, 23),
})
local portrait = Model.new(966, {
  [62] = Vertex.new(-212, 800, -8, 140, 131, 89),
  [219] = Vertex.new(-208, 732, 72, 81, 75, 74),
  [228] = Vertex.new(-208, 828, 120, 81, 75, 74),
  [230] = Vertex.new(-208, 828, -120, 81, 75, 74),
  [578] = Vertex.new(-208, 740, -56, 76, 70, 70),
})
local slashedPortrait = Model.new(1068, {
  [366] = Vertex.new(-208, 732, -76, 81, 75, 74),
  [375] = Vertex.new(-208, 828, 116, 81, 75, 74),
  [377] = Vertex.new(-208, 828, -124, 81, 75, 74),
  [713] = Vertex.new(-208, 740, -60, 76, 70, 70),
  [873] = Vertex.new(-208, 776, -20, 81, 75, 74),
})
local tapestry = Model.new(420, {
  [195] = Vertex.new(-176, 910, 48, 96, 95, 8),
  [197] = Vertex.new(-176, 910, -40, 96, 95, 8),
  [213] = Vertex.new(-176, 840, 100, 96, 95, 8),
  [339] = Vertex.new(-180, 840, 80, 64, 31, 5),
  [351] = Vertex.new(-180, 910, 28, 64, 31, 5),
})
local slashedTapestry = Model.new(945, {
  [156] = Vertex.new(-192, 910, -20, 48, 8, 4),
  [237] = Vertex.new(-192, 840, 36, 48, 8, 4),
  [489] = Vertex.new(-189, 910, -20, 96, 95, 8),
  [783] = Vertex.new(-189, 960, 16, 64, 31, 5),
  [788] = Vertex.new(-189, 840, 36, 64, 31, 5),
})
local vampyreStatue = Model.new(1500, {
  [606] = Vertex.new(0, 832, -68, 102, 94, 93),
  [630] = Vertex.new(36, 956, -36, 102, 94, 93),
  [750] = Vertex.new(-72, 488, 36, 102, 94, 93),
  [1266] = Vertex.new(-36, 956, -36, 102, 94, 93),
  [1293] = Vertex.new(-36, 956, -36, 102, 94, 93),
})
local vampyreStatueWithKey = Model.new(1782, {
  [837] = Vertex.new(-36, 956, -36, 102, 94, 93),
  [1491] = Vertex.new(36, 500, -220, 143, 142, 44),
  [1497] = Vertex.new(24, 484, -220, 143, 142, 44),
  [1503] = Vertex.new(36, 468, -220, 143, 142, 44),
  [1604] = Vertex.new(36, 500, -220, 143, 142, 44),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local doorKey = Model.new(207, {
  [93] = Vertex.new(116, 0, 76, 92, 80, 48),
  [97] = Vertex.new(116, 0, 76, 92, 80, 48),
  [99] = Vertex.new(76, 0, 120, 92, 80, 48),
  [103] = Vertex.new(76, 0, 120, 92, 80, 48),
  [106] = Vertex.new(76, 0, 120, 92, 80, 48),
})
local ladderTop = Model.new(186, {
  [60] = Vertex.new(-192, 300, 256, 54, 48, 42),
  [61] = Vertex.new(-192, 300, 256, 54, 48, 42),
  [75] = Vertex.new(192, -8, 204, 54, 48, 42),
  [80] = Vertex.new(-192, 300, 256, 48, 43, 37),
  [84] = Vertex.new(192, -8, 204, 48, 43, 37),
})
local decoratedWall = Model.new(642, {
  [300] = Vertex.new(-188, 1198, -252, 41, 41, 31),
  [414] = Vertex.new(-192, 895, -20, 70, 64, 64),
  [444] = Vertex.new(-192, 740, -12, 70, 64, 64),
  [450] = Vertex.new(-192, 940, 28, 92, 84, 84),
  [483] = Vertex.new(-192, 940, 28, 92, 84, 84),
})
local message = Model.new(333, {
  [6] = Vertex.new(84, 56, 84, 83, 82, 43),
  [116] = Vertex.new(-76, 56, 84, 83, 82, 43),
  [134] = Vertex.new(84, 56, 84, 101, 100, 64),
  [138] = Vertex.new(84, 56, 84, 101, 100, 64),
  [216] = Vertex.new(-76, 56, 84, 101, 100, 64),
})
local castleSketch1 = Model.new(261, {
  [3] = Vertex.new(-140, 4, -60, 102, 88, 65),
  [5] = Vertex.new(-136, 0, 68, 102, 88, 65),
  [74] = Vertex.new(-124, 8, -16, 82, 65, 34),
  [198] = Vertex.new(-116, 12, -20, 102, 88, 65),
  [206] = Vertex.new(-112, 12, -8, 102, 88, 65),
})
local castleSketch2 = Model.new(261, {
  [3] = Vertex.new(-140, 4, -60, 102, 88, 65),
  [5] = Vertex.new(-136, 0, 68, 102, 88, 65),
  [74] = Vertex.new(-124, 8, -16, 82, 65, 34),
  [198] = Vertex.new(-116, 12, -20, 102, 88, 65),
  [206] = Vertex.new(-112, 12, -8, 102, 88, 65),
})
local castleSketch3 = Model.new(261, {
  [3] = Vertex.new(-140, 4, -60, 102, 88, 65),
  [5] = Vertex.new(-136, 0, 68, 102, 88, 65),
  [74] = Vertex.new(-124, 8, -16, 82, 65, 34),
  [198] = Vertex.new(-116, 12, -20, 102, 88, 65),
  [206] = Vertex.new(-112, 12, -8, 102, 88, 65),
})
local message2 = Model.new(588, {
  [137] = Vertex.new(104, 12, -56, 82, 61, 34),
  [281] = Vertex.new(96, 8, 104, 115, 86, 47),
  [286] = Vertex.new(56, 20, 116, 82, 61, 34),
  [287] = Vertex.new(52, 20, 104, 82, 61, 34),
  [315] = Vertex.new(40, 20, 116, 82, 61, 34),
})
local ornateKey = Model.new(432, {
  [2] = Vertex.new(-24, 20, -116, 118, 117, 37),
  [26] = Vertex.new(-24, 20, -100, 118, 117, 37),
  [111] = Vertex.new(24, 4, -116, 118, 117, 37),
  [381] = Vertex.new(-24, 20, -116, 109, 103, 45),
  [387] = Vertex.new(-24, 20, -116, 109, 103, 45),
})
local haemalchemyBook = Model.new(315, {
  [155] = Vertex.new(-40, 16, -104, 96, 75, 8),
  [159] = Vertex.new(-40, 0, -152, 123, 48, 11),
  [171] = Vertex.new(-24, 0, -152, 123, 48, 11),
  [239] = Vertex.new(32, 56, 40, 117, 151, 152),
  [245] = Vertex.new(20, 56, 40, 117, 151, 152),
})
--#endregion

local xButton =
  "\x54\x48\x2c\xff\x54\x48\x2c\xff\x00\x00\x00\x00\x9e\x89\x5b\xff\xab\x95\x68\xff\xab\x95\x68\xff\xab\x95\x68\xff\xa1\x8d\x66\xff\x7b\x68\x45\xff\x5f\x52\x3c\xff\x83\x70\x47\xff\x00\x00\x00\x00\x8c\x78\x4c\xff\x94\x7f\x52\xff\xab\x95\x68\xff\xab\x95\x68\xff\xab\x95\x68\xff\xab\x95\x68\xff\xab\x95\x68\xff\x8c\x78\x4c\xff\x6a\x57\x3d\xff\x6b\x5d\x43\xff\x00\x00\x00\x00\x54\x48\x2c\xff\x54\x48\x2c\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Veliaf Hurtz in the Burgh de Rott pub basement.",
    title = "Starting out",
    neededItems = {
      ["Any nails (except rune)"] = { quantity = 8 },
      ["Regular/oak/teak/mahogany planks"] = { quantity = 2 },
    },
    actions = { Action.Direction:new(3490, 965, 3232) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Is there something I can do to help out?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue speaking to Veliaf Hurtz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("What is the Sanguinesti region?"),
    },
    postconditions = { Condition.ConversationText:new("Rod of Ivandis") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("How can I search for the Sanguinesti order of the Myreque?"),
    },
    postconditions = { Condition.ConversationText:new("the vyrewatch cannot be") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Can you tell me about the Sanguinesti Myreque?"),
    },
    postconditions = { Condition.ConversationText:new("hid in Mort Myre") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Tell me again how I get into the Sanguinesti region."),
    },
    postconditions = { Condition.ConversationText:new("Burgh de Rott") },
  },
  {
    text = "Search the chest to the north for garlic.",
    actions = { Action.Direction:new(3493, 1093, 9631) },
    postconditions = { Condition.InventoryContains:new(Models.items["garlic"]) },
  },
  {
    text = "Exit the pub basement.",
    actions = { Action.Direction:new(3490, 1245, 9632) },
    postconditions = { Condition.DistanceTo:new(3491, 965, 3232, 4) },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Search the boat in the boathouse to the south-east.",
    title = "The ruined Icyene city",
    actions = {
      Action.Direction:new(3523.5, 869, 3178, { distance = 12 }),
      Action.ModelHighlight:new(brokenBoat, { distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("pretty broken") },
  },
  {
    text = "Use a plank on it to fix it.",
    actions = {
      Action.InventoryHighlight:new(Models.items["any original planks"]),
      Action.ModelHighlight:new(brokenBoat, { distance = 12 }),
    },
    postconditions = { Condition.ModelVisible:new(fixedBoat) },
  },
  {
    text = "Use a plank on the boat chute to fix it.",
    actions = { Action.ModelHighlight:new(brokenBoatChute) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(Models.items["any original planks"]),
      Condition.ConversationText:new("manage to fix the chute"),
    },
  },
  {
    text = "Push the boat down the chute.",
    actions = { Action.ModelHighlight:new(fixedBoat) },
    postconditions = { Condition.InInstance:new() },
  },
  { postconditions = { Condition.DistanceFrom:new(0, 0, 0, 5, true) } }, --not tested
  {
    text = "Board the boat.",
    actions = { Action.ModelHighlight:new(fixedBoat) },
    postconditions = { Condition.DistanceTo:new(3605, 69, 3161, 4) },
  },
  {
    text = "Exit the boat, kick down the floor, and climb down.",
    warning = "Manually click Freedom if a Vyrewatch stops you.",
    actions = { Action.Direction:new(3589.4, 1557, 3173) },
    postconditions = { Condition.DistanceToWithHeight:new(3588, 101, 3173, 4) },
  },
  {
    text = "Climb the wall rubble.",
    actions = { Action.Direction:new(3588.8, 533, 3180) },
    postconditions = { Condition.DistanceTo:new(3593, 101, 3180, 2) },
  },
  {
    text = "Talk to a Meiyerditch citizen.",
    actions = {
      Action.ModelHighlight:new(anyMeiyerditchCitizen, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("(whisper) Do you know about the Myreque?"),
      Action.ConversationHighlight:new("(whisper) I really need to meet the Myreque."),
      Action.ConversationHighlight:new("How can Old Man Ral help me?"),
    },
    postconditions = { Condition.ConversationText:new("Hope that helps") },
  },
  {
    text = "Talk to Old Man Ral (Achievement icon on the map).<ul><li>(Optional) If you want to reduce detection chance by the Vyrewatch, you can buy a set of Vyrewatch clothing from Trader Sven just south of Old Man Ral before continuing.</li></ul>",
    actions = {
      Action.Direction:new(3602, 4485, 3208, { distance = 4 }),
      Action.ModelHighlight:new(oldManRal, { distance = 8 }),
      Action.ConversationHighlight:new("Someone said you could help me."),
      Action.ConversationHighlight:new("Old Man Ral, the sage of Sanguinesti."),
    },
    postconditions = { Condition.ConversationText:new("thanks very much") },
  },
  {
    text = "Go to the building to the west-southwest of Old Man Ral and climb the ladder.",
    actions = { Action.Direction:new(3595, 5285, 3204) },
    postconditions = { Condition.DistanceToWithHeight:new(3595, 5701, 3205, 4) },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Jump to the floorboards in the south, then to the east.",
    title = "Navigating Meiyerditch",
    actions = {
      Action.PathGuide:new({
        Location:new(3598, 5701, 3203),
        Location:new(3598, 5701, 3200),
        Location:new(3601, 5701, 3200),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3602, 5701, 3200, 1) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3605, 6401, 3203.5) },
    postconditions = { Condition.ModelVisible:new(flatDoor) },
  },
  {
    text = "Walk-across the floor.",
    actions = { Action.ModelHighlight:new(flatDoor) },
    postconditions = { Condition.DistanceTo:new(3606, 5701, 3207, 1) },
  },
  {
    text = "Crawl-under the wall to the north.",
    actions = { Action.Direction:new(3606, 6501, 3207.5) },
    postconditions = { Condition.DistanceTo:new(3606, 5701, 3209, 1) },
  },
  {
    text = "Push the wall.",
    actions = { Action.ModelHighlight:new(door) },
    postconditions = { Condition.ModelVisible:new(flatDoor2) },
  },
  {
    text = "Walk-across the floor.",
    actions = { Action.ModelHighlight:new(flatDoor2) },
    postconditions = { Condition.DistanceTo:new(3600, 5701, 3215, 1) },
  },
  {
    text = "Climb-down the ladder.",
    actions = { Action.Direction:new(3601, 5701, 3215.8) },
    postconditions = { Condition.DistanceToWithHeight:new(3601, 4485, 3214, 4) },
  },

  ------------------

  {
    text = "Follow the path.",
    title = "Navigating Meiyerditch",
    actions = {
      Action.PathGuide:new({
        Location:new(3598, 5701, 3203),
        Location:new(3598, 5701, 3200),
        Location:new(3601, 5701, 3200),
        Location:new(3605, 5701, 3203),
        Location:new(3605, 5701, 3206),
        Location:new(3606, 5701, 3207),
        Location:new(3606, 5701, 3214),
        Location:new(3600, 5701, 3214),
        Location:new(3600, 5701, 3216),
        Location:new(3601, 5701, 3216),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3601, 4485, 3214, 4) },
  },
  {
    text = "Search the table next to the wall.",
    actions = { Action.ModelHighlight:new(table) },
    postconditions = { Condition.ModelVisible:new(table2) },
  },
  {
    text = "Open the trapdoor table.",
    actions = { Action.ModelHighlight:new(table2) },
    postconditions = { Condition.ModelVisible:new(trapdoorTunnel) },
  },
  {
    text = "Climb-into the trapdoor tunnel.",
    actions = { Action.ModelHighlight:new(trapdoorTunnel) },
    postconditions = { Condition.DistanceTo:new(3598, 4485, 3220, 2) },
  },
  {
    text = "Climb-up the shelf next to the sickle logo in the north-west corner of the room.",
    actions = { Action.Direction:new(3594, 5485, 3223.2) },
    postconditions = { Condition.DistanceToWithHeight:new(3595, 5701, 3223, 2) },
  },
  {
    text = "Crawl-under the wall, jump-to the floorboards and climb-down the ladder.",
    actions = {
      Action.PathGuide:new({
        Location:new(3595, 5701, 3223),
        Location:new(3597, 5701, 3223),
        Location:new(3598, 5701, 3222),
        Location:new(3601, 5701, 3222),
        Location:new(3603, 5701, 3222.5),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3603, 4485, 3221, 4) },
  },
  {
    text = "Search the pots.",
    actions = { Action.Direction:new(3608, 4485, 3222) },
    postconditions = { Condition.InventoryContains:new(doorKey) },
  },
  {
    text = "Open the door.",
    actions = { Action.Direction:new(3608.5, 5285, 3221) },
    postconditions = { Condition.DistanceTo:new(3610, 4485, 3221, 1) },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Climb up the ladder in the room to the east, by the sickle.",
    title = "Sector 2 directions",
    actions = { Action.Direction:new(3617.8, 5285, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(3617, 5701, 3219, 4) },
  },
  {
    text = "Climb up the shelf at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3617, 5701, 3219),
        Location:new(3615, 5701, 3218),
        Location:new(3615, 5701, 3216),
        Location:new(3618, 5701, 3215),
        Location:new(3618, 5701, 3212),
        Location:new(3614.75, 6301, 3210),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3614, 6917, 3210, 4) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(3610, 7617, 3209.85) },
    postconditions = { Condition.DistanceToWithHeight:new(3610, 8133, 3209, 4) },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3610, 8133, 3209),
        Location:new(3613, 8133, 3208),
        Location:new(3613, 8133, 3205),
        Location:new(3613, 8133, 3203),
        Location:new(3612.3, 8133, 3203),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3611, 6917, 3203, 4) },
  },
  {
    text = "Follow the path and climb down the ladder.",
    actions = {
      Action.PathGuide:new({
        Location:new(3611, 6917, 3202),
        Location:new(3616, 6917, 3202),
        Location:new(3622, 6917, 3202),
        Location:new(3625, 6917, 3202.7),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3625, 5701, 3204, 4) },
  },
  {
    text = "Climb up the shelf at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3625, 5701, 3204),
        Location:new(3623, 5701, 3207),
        Location:new(3623, 5701, 3210),
        Location:new(3623, 5701, 3216),
        Location:new(3623, 6301, 3217.5),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3623, 6917, 3218, 4) },
  },
  {
    text = "Climb down the shelf to the north-east.",
    actions = { Action.Direction:new(3625.5, 6917, 3221) },
    postconditions = { Condition.DistanceToWithHeight:new(3626, 5701, 3221, 4) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3626, 5701, 3220),
        Location:new(3626, 5701, 3223),
        Location:new(3623, 5701, 3223),
        Location:new(3623, 5701, 3226),
        Location:new(3622, 5701, 3230),
        Location:new(3622, 5701, 3232),
        Location:new(3624, 5701, 3240),
        Location:new(3626, 5701, 3240),
        Location:new(3630.1, 6301, 3239),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3631, 6917, 3239, 2) },
  },
  {
    text = "Search the wall to the west to get the ladder top.",
    actions = { Action.ModelHighlight:new(wallWithLadderTop) },
    postconditions = { Condition.InventoryContains:new(ladderTop) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3630.3, 6917, 3239) },
    postconditions = { Condition.DistanceToWithHeight:new(3629, 5701, 3239, 4) },
  },
  {
    text = "Repair the broken ladder.",
    actions = {
      Action.ModelHighlight:new(brokenLadderTop),
      Action.InventoryHighlight:new(ladderTop),
    },
    postconditions = {
      Condition.ModelNotVisible:new(brokenLadderTop),
      Condition.ConversationText:new("attach the top of the"),
    },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3628.7, 5701, 3240) },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 4485, 3240, 4) },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Climb the ladder at the end of the path.",
    title = "Sector 3 directions",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 4485, 3240),
        Location:new(3632, 4485, 3240),
        Location:new(3632, 4485, 3242),
        Location:new(3629, 4485, 3242),
        Location:new(3629, 4485, 3250),
        Location:new(3629, 4485, 3250),
        Location:new(3629, 4485, 3250),

        Location:new(3625, 4485, 3252),
        Location:new(3624, 4485, 3252),
        Location:new(3624, 4485, 3261),
        Location:new(3629, 4485, 3261),
        Location:new(3631, 4485, 3260),
        Location:new(3631, 5285, 3258),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Jump to the floorboards in the east and climb down the stairs.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(decoratedWall) },
    postconditions = { Condition.ModelVisible:new(lumpyRug) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(lumpyRug) },
    postconditions = { Condition.InventoryContains:new(hideoutTrapdoor) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(hideoutTrapdoor) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Talk to Vertida Sefalatis.",
    title = "Myreque meetings",
    actions = {
      Action.Direction:new(3629, 965, 9644, { distance = 16 }),
      Action.ModelHighlight:new(vertidaSefalatis, { distance = 16 }),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    postconditions = { Condition.InventoryContains:new(message) },
  },
  {
    text = "Talk to Veliaf Hurtz, back in the basement of the pub in Burgh de Rott.",
    actions = { Action.Direction:new(3490, 965, 3232) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("I have a message for you from Vertida!"),
    },
    postconditions = { Condition.ConversationText:new("Paterdomus") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("What do you want me to do?"),
    },
    postconditions = { Condition.ConversationText:new("company for the trip") },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Talk to Drezel in the Mausoleum under the Paterdomus temple west of Canifis.<ul><li>Use CKS or the invitation box to teleport nearby.</li></ul>",
    title = "Ambush at Paterdomus",
    actions = { Action.Direction:new(3424, 1465, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3423, 5, 9891, 20) },
  },
  {
    actions = {
      Action.Direction:new(3440, 677, 9895, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["drezel"], { distance = 20 }),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("Did you hear the sounds as well?"),
    },
    postconditions = { Condition.ConversationText:new("let me know if you find something") },
  },
  {
    text = "Take the west exit, past the monuments.",
    actions = { Action.Direction:new(3405, 1412, 9907.2) },
    postconditions = { Condition.DistanceTo:new(3405, 2821, 3504, 4) },
  },
  {
    text = "Search the small bush slightly south of the western church steps.",
    actions = {
      Action.Direction:new(3390, 1109, 3479, { distance = 20 }),
      Action.ModelHighlight:new(bushWithBody, { distance = 20 }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    postconditions = {
      Condition.ConversationText:new("Of course"),
      Condition.DistanceFrom:new(0, 0, 0, 20, true), --not tested
    },
  },
  {
    text = "Talk to Drezel in the Mausoleum.",
    actions = { Action.Direction:new(3405, 3621, 3505) },
    postconditions = { Condition.DistanceTo:new(3423, 5, 9891, 20) },
  },
  {
    actions = {
      Action.Direction:new(3440, 677, 9895, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["drezel"], { distance = 20 }),
      Action.ConversationHighlight:new("Talk about something else."),
    },
    postconditions = { Condition.ConversationText:new("bid you good luck") }, --not tested
  },
  {
    text = "Talk to King Roald in the Varrock Palace.<ul><li>You will be teleported back to the Paterdomus.</li></ul>",
    actions = {
      Action.Direction:new(3222, 1253, 3473, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["king roald"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about Darkness of Hallowvale."),
      Action.ConversationHighlight:new("What should I do now?"),
      Action.ConversationHighlight:new("Yes thanks, I'll accept the free teleport."),
    },
    postconditions = { Condition.DistanceTo:new(3423, 5, 9891, 20) },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Talk to Drezel about what to do next.",
    title = "Meeting Safalaan Hallow",
    actions = {
      Action.Direction:new(3440, 677, 9895, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["drezel"], { distance = 20 }),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    postconditions = { Condition.ConversationText:new("I wonder") },
  },
  {
    text = "Talk to Veliaf Hurtz back in the basement of the pub in Burgh de Rott.",
    actions = { Action.Direction:new(3490, 965, 3232) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("I was attacked near to Paterdomus!"),
    },
    postconditions = { Condition.ConversationText:new("Paterdomus") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("King Roald cannot send troops!"),
    },
    postconditions = { Condition.ConversationText:new("Misthalin might heed this call") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("What should we do now?"),
    },
    postconditions = { Condition.ConversationText:new("Sanguinesti hideout") },
  },
  {
    text = "Exit the pub basement.",
    actions = { Action.Direction:new(3490, 1245, 9632) },
    postconditions = { Condition.DistanceTo:new(3491, 965, 3232, 4) },
  },
  {
    text = "Board the boat to the south-east.",
    actions = {
      Action.Direction:new(3523, 165, 3170, { distance = 20 }),
      Action.ModelHighlight:new(fixedBoat, { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(3605, 69, 3161, 4) },
  },
  {
    text = "Get caught by a Vyrewatch.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["any vyrewatch"], { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Send me to the mines! (Do a bit of menial work)"),
    },
    postconditions = { Condition.DistanceTo:new(2263, 2765, 4620, 10) },
  },
  {
    text = "Mine 15 ore from the walls.",
    postconditions = { Condition.InventoryContains:new(Models.items["daeyalt ore"], 15) },
  },
  {
    text = "Use them on a minecart",
    actions = {
      Action.ModelHighlight:new(minecart, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Yes, I'll place it all in the cart."),
    },
    postconditions = { Condition.ModelVisible:new(fullMinecart) },
  },
  {
    text = "Talk to a guard.",
    actions = { Action.ModelHighlight:new(Models.npcs["any juvinate"], { highlightPriority = "closest" }) },
    postconditions = { Condition.DistanceTo:new(3621, 4485, 3322, 4) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3621, 4485, 3322),
        Location:new(3624, 4485, 3318),
        Location:new(3624, 4485, 3314),
        Location:new(3632, 4485, 3305),
        Location:new(3628, 4485, 3300),
        Location:new(3628, 4485, 3294),
        Location:new(3633, 4485, 3294),
        Location:new(3633, 4485, 3289),
        Location:new(3635, 4485, 3288),
        Location:new(3635, 4485, 3284),
        Location:new(3632, 4485, 3283),
        Location:new(3632, 4485, 3277),
        Location:new(3634, 4485, 3275),
        Location:new(3634, 4485, 3268),
        Location:new(3631, 4485, 3267),
        Location:new(3631, 4485, 3259),
        Location:new(3631, 5285, 3258),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Climb down the stairs at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(decoratedWall) },
    postconditions = { Condition.ModelVisible:new(lumpyRug) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(lumpyRug) },
    postconditions = { Condition.ModelVisible:new(hideoutTrapdoor) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(hideoutTrapdoor) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    text = "Talk to Vertida<ul><li>You will be teleported back to sector 1.</li></ul>",
    actions = {
      Action.Direction:new(3629, 965, 9644, { distance = 16 }),
      Action.ModelHighlight:new(vertidaSefalatis, { distance = 16 }),
      Action.ConversationHighlight:new("What should I do now?"),
      Action.ConversationHighlight:new("Okay, lead the way."),
    },
    postconditions = { Condition.DistanceTo:new(3596, 5701, 3203, 4) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3595, 5701, 3204.3) },
    postconditions = { Condition.DistanceToWithHeight:new(3595, 4485, 3203, 4) },
  },
  {
    text = "Climb over the wall rubble near the shore.",
    actions = { Action.Direction:new(3591, 901, 3180) },
    postconditions = { Condition.DistanceTo:new(3587, 133, 3180, 1) },
  },
  {
    text = "Climb the fallen floor.",
    actions = { Action.Direction:new(3589, 901, 3173) },
    postconditions = { Condition.DistanceToWithHeight:new(3590, 1557, 3173, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3590, 1557, 3173),
        Location:new(3589, 1605, 3177),
        Location:new(3586, 1573, 3178),
        Location:new(3587, 1957, 3183),
        Location:new(3587, 5957, 3196),
        Location:new(3588, 5957, 3209.3),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3588, 4485, 3211, 4) },
  },
  {
    text = "Choose the 'search' option on the rocky surface.",
    actions = { Action.ModelHighlight:new(rockySurface) },
    postconditions = { Condition.ConversationText:new("click") },
  },
  {
    text = "Climb up the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3591, 4485, 3211),
        Location:new(3589, 4485, 3214.5),
        Location:new(3589, 4485, 3230),
        Location:new(3593.3, 5285, 3230),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3594, 5957, 3230, 4) },
  },
  {
    text = "Climb the ladder to the north.",
    actions = {
      Action.PathGuide:new({
        Location:new(3594, 5957, 3230),
        Location:new(3592, 5957, 3232),
        Location:new(3590, 5957, 3237),
        Location:new(3590, 5957, 3241),
        Location:new(3588, 5957, 3244),
        Location:new(3588, 6657, 3251.3),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3588, 7045, 3252, 4) },
  },
  {
    text = "Climb down the ladder to the north.",
    actions = {
      Action.PathGuide:new({
        Location:new(3588, 7045, 3251.3),
        Location:new(3588, 7045, 3258.7),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3588, 5957, 3260, 4) },
  },
  {
    text = "Climb up the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3588, 5957, 3260),
        Location:new(3588, 5989, 3266),
        Location:new(3589, 5989, 3268),
        Location:new(3589, 5989, 3278),
        Location:new(3587, 5853, 3279),
        Location:new(3587, 5861, 3289),
        Location:new(3591, 5989, 3290),
        Location:new(3591, 5989, 3306),
        Location:new(3595, 5989, 3307),
        Location:new(3595, 5989, 3310),
        Location:new(3593, 5989, 3310),
        Location:new(3593, 6689, 3311.1),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3593, 7589, 3313, 4) },
  },
  {
    text = "Talk to Safalaan to the north.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("I was attacked near Paterdomus on my travels."),
    },
    postconditions = { Condition.ConversationText:new("Paterdomus") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("Can I help in some way?"),
    },
    postconditions = { Condition.ConversationText:new("completed the plans") },
  },
  {
    text = "Run along the wall and stand on the sickle on the ground.",
    actions = { Action.Direction:new(3556, 7589, 3384) },
    postconditions = { Condition.DistanceTo:new(3556, 7589, 3384, 0) },
  },
  {
    text = "Use the papyrus on the charcoal and watch the cutscene.",
    actions = {
      Action.InventoryHighlight:new(Models.items["charcoal"]),
      Action.InventoryHighlight:new(Models.items["papyrus"]),
    },
    postconditions = { Condition.ConversationText:new("Vanstrom") },
  },
  { postconditions = { Condition.DistanceTo:new(3556, 7589, 3384, 4) } }, --not tested
  {
    text = "Use papyrus on charcoal at the next spot.",
    actions = { Action.Direction:new(3524, 7589, 3348) },
    postconditions = { Condition.DistanceTo:new(3524, 7589, 3348, 0) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(Models.items["charcoal"]),
      Action.InventoryHighlight:new(Models.items["papyrus"]),
    },
    postconditions = { Condition.InventoryContains:new(castleSketch2) },
  },
  {
    text = "Use papyrus on charcoal at the last spot.",
    actions = { Action.Direction:new(3583, 7589, 3334) },
    postconditions = { Condition.DistanceTo:new(3583, 7589, 3334, 0) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(Models.items["charcoal"]),
      Action.InventoryHighlight:new(Models.items["papyrus"]),
    },
    postconditions = { Condition.ConversationText:new("we meet again") },
  },
  {
    text = "Watch cutscene. Let Vanstrom attack you a bit which will knock you out.<ul><li>Talk to and finish the dialogue with Sarius if she didn't automatically talk to you when Vanstrom knocked you out.</li></ul>",
    postconditions = { Condition.ConversationText:new("why else would you be here") },
  },
  {
    text = "Finish the final sketch.",
    actions = { Action.Direction:new(3583, 7589, 3334) },
    postconditions = { Condition.DistanceTo:new(3583, 7589, 3334, 0) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(Models.items["charcoal"]),
      Action.InventoryHighlight:new(Models.items["papyrus"]),
    },
    postconditions = { Condition.InventoryContains:new(castleSketch3) },
  },
  {
    text = "Go south back to the ladder and climb down.",
    actions = { Action.Direction:new(3593, 7589, 3312) },
    postconditions = { Condition.DistanceToWithHeight:new(3593, 5989, 3310, 4) },
  },
  {
    text = "Get caught by a Vyrewatch.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["any vyrewatch"], { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Send me to the mines! (Do a bit of menial work)"),
    },
    postconditions = { Condition.DistanceTo:new(2263, 2765, 4620, 10) },
  },
  {
    text = "Mine 15 ore from the walls.",
    postconditions = { Condition.InventoryContains:new(Models.items["daeyalt ore"], 15) },
  },
  {
    text = "Use them on a minecart",
    actions = {
      Action.ModelHighlight:new(minecart, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Yes, I'll place it all in the cart."),
    },
    postconditions = { Condition.ModelVisible:new(fullMinecart) },
  },
  {
    text = "Talk to a guard.",
    actions = { Action.ModelHighlight:new(Models.npcs["any juvinate"], { highlightPriority = "closest" }) },
    postconditions = { Condition.DistanceTo:new(3621, 4485, 3322, 4) },
  },
  {
    text = "Climb the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3621, 4485, 3322),
        Location:new(3624, 4485, 3318),
        Location:new(3624, 4485, 3314),
        Location:new(3632, 4485, 3305),
        Location:new(3628, 4485, 3300),
        Location:new(3628, 4485, 3294),
        Location:new(3633, 4485, 3294),
        Location:new(3633, 4485, 3289),
        Location:new(3635, 4485, 3288),
        Location:new(3635, 4485, 3284),
        Location:new(3632, 4485, 3283),
        Location:new(3632, 4485, 3277),
        Location:new(3634, 4485, 3275),
        Location:new(3634, 4485, 3268),
        Location:new(3631, 4485, 3267),
        Location:new(3631, 4485, 3259),
        Location:new(3631, 5285, 3258),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Climb down the stairs at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(decoratedWall) },
    postconditions = { Condition.ModelVisible:new(lumpyRug) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(lumpyRug) },
    postconditions = { Condition.ModelVisible:new(hideoutTrapdoor) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(hideoutTrapdoor) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    text = "Talk to Safalaan.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("I have all the sketches for you!"),
    },
    postconditions = { Condition.ConversationText:new("Myreque prevail") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    postconditions = { Condition.ConversationText:new("you could get a hold of it") },
  },

  --------------------------------------------------------------------------------------------------

  {
    text = "Leave the hideout.",
    title = "Locating the laboratory",
    actions = { Action.Direction:new(3626, 1665, 9617.3) },
    postconditions = { Condition.DistanceTo:new(3638, 4517, 3249, 4) },
  },
  {
    text = "Push the wall and climb the stairs.",
    actions = { Action.Direction:new(3639, 4485, 3258.3) },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 5701, 3255, 4) },
  },
  {
    text = "Climb down the ladder.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630.3, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3255),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3632, 4485, 3258, 4) },
  },
  {
    text = "Look at the fireplace to the south.",
    actions = { Action.ModelHighlight:new(fireplace) },
    postconditions = { Condition.InventoryContains:new(message2) },
  },
  {
    text = "Read the message.",
    warning = "There isn't tracking for this step currently.",
    actions = { Action.InventoryHighlight:new(message2) },
    postconditions = { Condition.Generic2DVisible:new(4096, 4096, 626, xButton) },
  },
  {
    text = "Search the portrait and slash it with the knife.",
    actions = { Action.ModelHighlight:new(portrait) },
    postconditions = { Condition.ModelVisible:new(slashedPortrait) },
  },
  {
    text = "Search the portrait again to pick up a large ornate key.",
    actions = { Action.ModelHighlight:new(slashedPortrait) },
    postconditions = { Condition.InventoryContains:new(ornateKey) },
  },
  {
    text = "Talk to Safalaan in the base and give him the message.",
    actions = {
      Action.Direction:new(3639, 4485, 3249),
    },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("I have a message for you Safalaan - it may be important."),
    },
    postconditions = { Condition.ConversationText:new("you look into this for us") },
  },
  {
    text = "Push the wall and climb the stairs.",
    actions = { Action.Direction:new(3639, 4485, 3258.3) },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 5701, 3255, 4) },
  },
  {
    text = "Climb down the ladder.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630.3, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3255),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3632, 4485, 3258, 4) },
  },
  {
    text = "Follow the path to the house with the tapestry.",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 4485, 3259),
        Location:new(3631, 4485, 3267),
        Location:new(3634, 4485, 3268),
        Location:new(3634, 4485, 3275),
        Location:new(3632, 4485, 3277),
        Location:new(3632, 4485, 3283),
        Location:new(3635, 4485, 3284),
        Location:new(3635, 4485, 3288),
        Location:new(3633, 4485, 3289),
        Location:new(3633, 4485, 3293),
        Location:new(3634, 4485, 3294),
        Location:new(3635, 4485, 3300),
        Location:new(3640, 4485, 3300),
        Location:new(3640, 4485, 3302),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3638, 4485, 3302, 1) },
  },
  {
    text = "Look-at the tapestry and slash it with a knife.",
    actions = { Action.ModelHighlight:new(tapestry) },
    postconditions = { Condition.ModelVisible:new(slashedTapestry) },
  },
  {
    text = "Walk-through the slashed tapestry.",
    actions = { Action.ModelHighlight:new(slashedTapestry) },
    postconditions = { Condition.DistanceTo:new(3638, 4485, 3306, 1) },
  },
  {
    text = "Use the large ornate key on the vampyre statue.",
    actions = {
      Action.InventoryHighlight:new(ornateKey),
      Action.ModelHighlight:new(vampyreStatue),
    },
    postconditions = { Condition.ModelVisible:new(vampyreStatueWithKey) },
  },
  {
    text = "Climb-down the staircase.",
    actions = { Action.Direction:new(3643, 4485, 3305) },
    postconditions = { Condition.DistanceTo:new(3637, 965, 9695, 1) },
  },
  {
    text = "Use telekinetic grab on Haemalchemy (Vol. 1).<ul><li>Runes can be found by searching the broken rune case.</li></ul>",
    actions = { Action.ModelHighlight:new(haemalchemyBook) },
    postconditions = { Condition.InventoryContains:new(haemalchemyBook) },
  },

  ----------------------------------------------------------------------------------------------------

  {
    text = "Climb-up the staircase.",
    title = "Finishing up",
    actions = { Action.Direction:new(3637, 1465, 9696) },
    postconditions = { Condition.DistanceTo:new(3643, 4485, 3305, 4) },
  },
  {
    text = "Head back to the Meiyerditch Myreque base.",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 4485, 3259),
        Location:new(3631, 4485, 3267),
        Location:new(3634, 4485, 3268),
        Location:new(3634, 4485, 3275),
        Location:new(3632, 4485, 3277),
        Location:new(3632, 4485, 3283),
        Location:new(3635, 4485, 3284),
        Location:new(3635, 4485, 3288),
        Location:new(3633, 4485, 3289),
        Location:new(3633, 4485, 3293),
        Location:new(3634, 4485, 3294),
        Location:new(3635, 4485, 3300),
        Location:new(3640, 4485, 3300),
        Location:new(3640, 4485, 3302),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3630, 5701, 3258, 4) },
  },
  {
    text = "Climb down the stairs at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630, 5701, 3258),
        Location:new(3630, 5701, 3259),
        Location:new(3632, 5701, 3259),
        Location:new(3633, 5701, 3256),
        Location:new(3636, 5701, 3256),
        Location:new(3639, 5701, 3256),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 4485, 3258, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4517, 3251, 1) },
  },
  {
    text = "Push the decorated wall from the southern side.",
    actions = { Action.ModelHighlight:new(decoratedWall) },
    postconditions = { Condition.ModelVisible:new(lumpyRug) },
  },
  {
    text = "Open the lumpy rug.",
    actions = { Action.ModelHighlight:new(lumpyRug) },
    postconditions = { Condition.ModelVisible:new(hideoutTrapdoor) },
  },
  {
    text = "Climb down trapdoor to enter the hideout.",
    actions = { Action.ModelHighlight:new(hideoutTrapdoor) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9618, 4) },
  },
  {
    text = "Speak to Safalaan.",
    actions = { Action.ModelHighlight:new(Models.npcs["safalaan"]) },
    postconditions = { Condition.ConversationText:new("Farewell and good luck") },
  },
  {
    text = "Take the sealed message to Veliaf in the Burgh de Rott cellar.",
    actions = { Action.Direction:new(3490, 965, 3232) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("I have a message for you from Safalaan."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Darkness of Hallowvale",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1157328000,
  prereqQuests = { "In Aid of the Myreque" },
  questReqs = {
    Types.QuestReq.skill("Agility", 26),
    Types.QuestReq.skill("Construction", 5),
    Types.QuestReq.skill("Crafting", 32),
    Types.QuestReq.skill("Magic", 33),
    Types.QuestReq.skill("Mining", 20),
    Types.QuestReq.skill("Strength", 40),
    Types.QuestReq.skill("Thieving", 22),
  },
  neededItems = {
    ["Any nails (except rune)"] = { quantity = 8, model = Models.items["any nails"], duringQuest = true },
    ["Regular/oak/teak/mahogany planks"] = { quantity = 2, model = Models.items["any original planks"] },
    ["Law rune"] = { quantity = 1, model = Models.items["law rune"], duringQuest = true },
    ["Air rune"] = { quantity = 1, model = Models.items["air rune"], duringQuest = true },
    ["Charcoal"] = { quantity = 1, model = Models.items["charcoal"], duringQuest = true },
    ["Papyrus"] = { quantity = 3, model = Models.items["papyrus"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = {},
})
