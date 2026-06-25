local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local lucille = Model.new(3825, {
  [3327] = Vertex.new(3, 778, -30, 56, 51, 51),
  [3332] = Vertex.new(8, 751, -37, 56, 51, 51),
  [3347] = Vertex.new(-9, 774, -33, 53, 48, 48),
  [3435] = Vertex.new(-8, 751, -37, 56, 51, 51),
  [3440] = Vertex.new(-3, 778, -30, 56, 51, 51),
})

-- Objects
local caveEntrance = Model.new(60, {
  [31] = Vertex.new(775, 1600, 249, 0, 0, 0, 0.4000),
  [37] = Vertex.new(775, 1654, 216, 0, 0, 0, 0.3059),
  [43] = Vertex.new(775, 1719, 176, 0, 0, 0, 0.2000),
  [49] = Vertex.new(775, 1807, 122, 0, 0, 0, 0.1020),
  [55] = Vertex.new(775, 1922, 50, 0, 0, 0, 0.05098),
})
local doorway = Model.new(4284, {
  [130] = Vertex.new(-401, 1499, 130, 40, 54, 34),
  [1014] = Vertex.new(-265, 1452, 637, 89, 121, 76),
  [1337] = Vertex.new(-265, 1189, -605, 40, 54, 34),
  [1569] = Vertex.new(-265, 1199, 637, 40, 54, 34),
  [2144] = Vertex.new(-512, 1423, -768, 45, 61, 38),
})
local burningSaplingObject = Model.new(216, {
  [51] = Vertex.new(-76, 148, -70, 42, 32, 21),
  [71] = Vertex.new(-44, 163, 9, 42, 32, 21),
  [75] = Vertex.new(-95, 88, -10, 42, 32, 21),
  [99] = Vertex.new(13, 104, 81, 42, 32, 21),
  [171] = Vertex.new(-56, 120, 56, 42, 32, 21),
})
local waterPot = Model.new(360, {
  [146] = Vertex.new(102, 289, 102, 73, 95, 92, 0.1176),
  [155] = Vertex.new(141, 289, -37, 73, 95, 92, 0.1176),
  [195] = Vertex.new(-102, 289, -103, 73, 95, 92, 0.1176),
  [197] = Vertex.new(-102, 289, -103, 73, 95, 92, 0.1176),
  [200] = Vertex.new(-102, 289, -103, 73, 95, 92, 0.1176),
})
local plantedWaterPot = Model.new(2946, {
  [6] = Vertex.new(-158, 254, 0, 47, 64, 40),
  [21] = Vertex.new(159, 254, 0, 47, 64, 40),
  [24] = Vertex.new(159, 254, 0, 47, 64, 40),
  [222] = Vertex.new(-172, 209, -11, 34, 47, 29),
  [270] = Vertex.new(173, 209, -11, 34, 47, 29),
})
local waterPortal = Model.new(3471, {
  [407] = Vertex.new(3785, 976, 5888, 73, 95, 92, 0.05882),
  [627] = Vertex.new(3046, 2420, 6021, 16, 134, 194, 0.2157),
  [630] = Vertex.new(2573, 2420, 6021, 16, 134, 194, 0.2157),
  [813] = Vertex.new(3435, 2139, 5771, 87, 138, 131, 0.2745),
  [3033] = Vertex.new(3409, 2187, 5771, 87, 138, 131, 0.2745),
})
local shinyWaterPot = Model.new(363, {
  [1] = Vertex.new(-118, 361, -33, 76, 70, 69, 0.000),
  [2] = Vertex.new(9, 358, 64, 76, 70, 69, 0.000),
  [3] = Vertex.new(106, 363, -64, 76, 70, 69, 0.000),
})
local fungusPot = Model.new(3408, {
  [3207] = Vertex.new(-29, 192, -176, 25, 109, 129),
  [3228] = Vertex.new(-126, 220, -129, 29, 126, 149),
  [3249] = Vertex.new(88, 151, -145, 25, 109, 129),
  [3261] = Vertex.new(-11, 192, 175, 25, 109, 129),
  [3282] = Vertex.new(117, 170, 133, 25, 109, 129),
})
local plantedFungusPot = Model.new(4188, {
  [3987] = Vertex.new(-29, 192, -176, 25, 109, 129),
  [4008] = Vertex.new(-126, 220, -129, 29, 126, 149),
  [4029] = Vertex.new(88, 151, -145, 25, 109, 129),
  [4041] = Vertex.new(-11, 192, 175, 25, 109, 129),
  [4062] = Vertex.new(117, 170, 133, 25, 109, 129),
})
local fungusPortal = Model.new(204, {
  [44] = Vertex.new(261, 1456, 428, 188, 16, 105, 0.2157),
  [77] = Vertex.new(256, 0, 428, 188, 16, 105, 0.09804),
  [122] = Vertex.new(183, 1460, 440, 16, 134, 194, 0.2157),
  [159] = Vertex.new(133, 1460, -230, 16, 134, 194, 0.2157),
  [162] = Vertex.new(133, 1460, 243, 16, 134, 194, 0.2157),
})
local shinyFungusPot = Model.new(4188, {
  [3987] = Vertex.new(-29, 192, -176, 25, 109, 129),
  [4008] = Vertex.new(-126, 220, -129, 29, 126, 149),
  [4029] = Vertex.new(88, 151, -145, 25, 109, 129),
  [4041] = Vertex.new(-11, 192, 175, 25, 109, 129),
  [4062] = Vertex.new(117, 170, 133, 25, 109, 129),
})
local ivyPot = Model.new(2526, {
  [2275] = Vertex.new(110, 263, -115, 50, 40, 32),
  [2315] = Vertex.new(110, 263, -115, 35, 28, 22),
  [2316] = Vertex.new(126, 230, -123, 35, 28, 22),
  [2380] = Vertex.new(-132, 263, -91, 50, 40, 32),
  [2415] = Vertex.new(-142, 230, -105, 35, 28, 22),
})
local plantedIvyPot = Model.new(384, {
  [255] = Vertex.new(184, 271, -143, 85, 129, 39),
  [269] = Vertex.new(109, 327, -171, 95, 144, 43),
  [309] = Vertex.new(-115, 344, 151, 95, 144, 43),
  [341] = Vertex.new(-190, 290, 109, 95, 144, 43),
  [377] = Vertex.new(-86, 352, -158, 72, 109, 33),
})
local ivyPortal = Model.new(456, {
  [129] = Vertex.new(3009, 2866, 2836, 95, 144, 43),
  [135] = Vertex.new(2965, 2874, 2729, 95, 144, 43),
  [137] = Vertex.new(2848, 2874, 2956, 95, 144, 43),
  [201] = Vertex.new(3021, 2801, 2965, 95, 144, 43),
  [423] = Vertex.new(2968, 2880, 2715, 57, 45, 29),
})
local grownIvyPot = Model.new(384, {
  [255] = Vertex.new(184, 271, -143, 85, 129, 39),
  [269] = Vertex.new(109, 327, -171, 95, 144, 43),
  [309] = Vertex.new(-115, 344, 151, 95, 144, 43),
  [341] = Vertex.new(-190, 290, 109, 95, 144, 43),
  [377] = Vertex.new(-86, 352, -158, 72, 109, 33),
})
local webPot = Model.new(174, {
  [11] = Vertex.new(127, 230, -123, 171, 178, 182),
  [93] = Vertex.new(-487, 0, 113, 171, 178, 182),
  [96] = Vertex.new(-487, 0, 112, 171, 178, 182),
  [147] = Vertex.new(243, 0, 328, 171, 178, 182),
  [162] = Vertex.new(244, 0, 327, 171, 178, 182),
})
local plantedWebPot = Model.new(216, {
  [99] = Vertex.new(36, 687, 226, 177, 178, 177),
  [123] = Vertex.new(-97, 675, -339, 177, 178, 177),
  [171] = Vertex.new(-179, 791, 138, 177, 178, 177),
  [195] = Vertex.new(-176, 864, -236, 177, 178, 177),
  [215] = Vertex.new(-76, 920, -3, 177, 178, 177),
})
local webPortal = Model.new(423, {
  [380] = Vertex.new(-78, 1815, -473, 171, 178, 182),
  [392] = Vertex.new(-158, 1536, -207, 171, 178, 182),
  [395] = Vertex.new(-73, 1815, -468, 171, 178, 182),
  [407] = Vertex.new(-150, 1536, -201, 171, 178, 182),
  [410] = Vertex.new(123, 1432, -736, 171, 178, 182),
})

-- Items
-- Quest Items
local restlessPotion = Model.multi({
  Model.new(216, {
    [1] = Vertex.new(0, 4, 24, 95, 11, 129),
    [2] = Vertex.new(8, 20, 32, 95, 11, 129),
    [3] = Vertex.new(-8, 20, 32, 95, 11, 129),
    [6] = Vertex.new(-32, 20, 8, 95, 11, 129),
    [7] = Vertex.new(28, 4, 0, 95, 11, 129),
  }),
  Model.new(240, {
    [1] = Vertex.new(-12, 12, -40, 134, 136, 147, 0.3137),
    [2] = Vertex.new(4, 0, -32, 134, 136, 147, 0.3137),
    [3] = Vertex.new(-4, 0, -32, 134, 136, 147, 0.3137),
    [5] = Vertex.new(12, 12, -40, 134, 136, 147, 0.3137),
    [7] = Vertex.new(-36, 0, -4, 134, 136, 147, 0.3137),
  }),
})
local burningSaplingInventory = Model.multi({
  Model.new(756, {
    [1] = Vertex.new(-18, 148, 14, 68, 53, 35),
    [2] = Vertex.new(-16, 147, 16, 68, 53, 35),
    [3] = Vertex.new(-17, 149, 14, 68, 53, 35),
    [5] = Vertex.new(-18, 147, 16, 68, 53, 35),
    [7] = Vertex.new(-2, 62, -12, 68, 53, 35),
  }),
  Model.new(216, {
    [1] = Vertex.new(-24, 81, -90, 33, 25, 16),
    [2] = Vertex.new(-9, 120, -54, 33, 25, 16),
    [3] = Vertex.new(-3, 101, -96, 33, 25, 16),
    [5] = Vertex.new(-36, 108, -51, 33, 25, 16),
    [8] = Vertex.new(-18, 124, -29, 33, 25, 16),
  }),
  Model.new(3, {
    [1] = Vertex.new(57, 76, 75, 80, 56, 24, 0.000),
    [2] = Vertex.new(-12, 76, -111, 80, 56, 24, 0.000),
    [3] = Vertex.new(-71, 76, 14, 80, 56, 24, 0.000),
  }),
})
local threeLeafSapling = Model.multi({
  Model.new(756, {
    [1] = Vertex.new(9, 26, 9, 253, 253, 253),
    [2] = Vertex.new(9, 39, 12, 253, 253, 253),
    [3] = Vertex.new(8, 32, 13, 253, 253, 253),
    [4] = Vertex.new(-1, 112, -11, 253, 253, 253),
    [5] = Vertex.new(-2, 98, -17, 253, 253, 253),
  }),
  Model.new(72, {
    [1] = Vertex.new(-81, 136, -44, 33, 25, 16),
    [2] = Vertex.new(-27, 159, -43, 33, 25, 16),
    [3] = Vertex.new(-66, 148, -68, 33, 25, 16),
    [5] = Vertex.new(-46, 169, -21, 33, 25, 16),
    [7] = Vertex.new(-100, 115, -16, 33, 25, 16),
  }),
  Model.new(3, {
    [1] = Vertex.new(-118, 75, -33, 61, 56, 55, 0.000),
    [2] = Vertex.new(9, 72, 64, 61, 56, 55, 0.000),
    [3] = Vertex.new(106, 77, -64, 61, 56, 55, 0.000),
  }),
})
local buddingSapling = Model.multi({
  Model.new(756, {
    [1] = Vertex.new(-10, 149, 11, 56, 57, 36),
    [2] = Vertex.new(-8, 148, 13, 56, 57, 36),
    [3] = Vertex.new(-9, 150, 11, 56, 57, 36),
    [5] = Vertex.new(-10, 148, 13, 56, 57, 36),
    [7] = Vertex.new(6, 63, -15, 56, 57, 36),
  }),
  Model.new(216, {
    [1] = Vertex.new(-16, 82, -93, 116, 96, 73),
    [2] = Vertex.new(-1, 121, -57, 116, 96, 73),
    [3] = Vertex.new(5, 102, -99, 116, 96, 73),
    [5] = Vertex.new(-28, 109, -54, 116, 96, 73),
    [8] = Vertex.new(-10, 125, -32, 116, 96, 73),
  }),
})
local healthySapling = Model.multi({
  Model.new(873, {
    [1] = Vertex.new(10, 501, 32, 50, 39, 25),
    [2] = Vertex.new(5, 505, 28, 50, 39, 25),
    [3] = Vertex.new(6, 501, 32, 50, 39, 25),
    [4] = Vertex.new(-63, 575, 1, 50, 39, 25),
    [5] = Vertex.new(-63, 591, 31, 50, 39, 25),
  }),
  Model.new(18, {
    [1] = Vertex.new(-14, 396, -137, 127, 127, 127),
    [2] = Vertex.new(-4, 387, -91, 127, 127, 127),
    [3] = Vertex.new(46, 378, -98, 127, 127, 127),
    [4] = Vertex.new(-35, 398, -93, 127, 127, 127),
    [9] = Vertex.new(-26, 393, -72, 127, 127, 127),
  }),
  Model.new(72, {
    [1] = Vertex.new(-110, 29, 8, 87, 91, 80),
    [2] = Vertex.new(-63, 34, 0, 87, 91, 80),
    [3] = Vertex.new(-164, 31, 21, 87, 91, 80),
    [4] = Vertex.new(-66, 18, 0, 87, 91, 80),
    [8] = Vertex.new(-19, 34, -38, 87, 91, 80),
  }),
  Model.new(216, {
    [1] = Vertex.new(238, 219, -64, 127, 128, 127),
    [2] = Vertex.new(115, 293, 20, 127, 128, 127),
    [3] = Vertex.new(238, 219, 16, 127, 128, 127),
    [5] = Vertex.new(131, 326, -61, 127, 128, 127),
    [11] = Vertex.new(50, 332, -6, 127, 128, 127),
  }),
})

---@type QuestStep[]
local steps = {
  --#region Dreaming souls
  {
    text = "Talk to Lucille who is in a house west of the Rimmington well.",
    title = "Dreaming souls",
    actions = { Action.Direction:new(2946, 805, 3207) },
    postconditions = { Condition.DistanceTo:new(2946, 805, 3207, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(lucille),
      Action.ConversationHighlight:new("Is everything okay?"),
      Action.ConversationHighlight:new("Can I help?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Finish the conversation with Lucille.",
    actions = { Action.ModelHighlight:new(lucille) },
    postconditions = {
      Condition.ConversationText:new("Lucille hands you a Restless Sleep potion."),
      Condition.InventoryContains:new(restlessPotion),
    },
  },
  {
    text = "Stay within Lucille's house and drink the restless sleep potion.",
    actions = {
      Action.InventoryHighlight:new(restlessPotion),
      Action.Direction:new(2946, 805, 3207),
    },
    postconditions = {
      Condition.ConversationText:new(
        "As you drink the potion, you fall asleep, but you still have some control over your body."
      ),
      Condition.InventoryDoesNotContain:new(restlessPotion),
    },
  },
  {
    text = "Follow the souls north-east to a cave entrance located south of the Rimmington mine and north-west of the Port Sarim lodestone and enter it.<ul><li>If you go past the cave, exit to the lobby, or logout in the cave, you will have to go back to Lucille and obtain the potion once again.</li></ul>",
    actions = { Action.Direction:new(2955, 805, 3213) },
    postconditions = {
      Condition.DistanceTo:new(2955, 805, 3213, 6),
      Condition.DistanceTo:new(1047, 1775, 6162, 4), -- To skip to cave checkpoint
    },
  },
  {
    actions = { Action.Direction:new(2958, 717, 3217) },
    postconditions = {
      Condition.DistanceTo:new(2958, 717, 3217, 8),
      Condition.DistanceTo:new(1047, 1775, 6162, 4),
    },
  },
  {
    actions = { Action.Direction:new(2955, 805, 3213) },
    postconditions = {
      Condition.ModelVisible:new(caveEntrance),
      Condition.DistanceTo:new(1047, 1775, 6162, 4),
    },
  },
  {
    actions = { Action.ModelHighlight:new(caveEntrance) },
    postconditions = { Condition.DistanceTo:new(1047, 1775, 6162, 4) },
  },
  {
    text = "Talk to the Raptor.",
    actions = {
      Action.ModelHighlight:new(NPCs["the raptor"]),
      Action.ConversationHighlight:new("I'm here to save Waylan."),
    },
    postconditions = {
      Condition.ConversationText:new("Keep your distance and you keep your life."),
      Condition.DistanceTo:new(1067, 549, 6175, 4),
    },
  },
  {
    text = "Enter the door to follow the Raptor.",
    actions = { Action.Direction:new(1067, 549, 6175) },
    postconditions = { Condition.DistanceTo:new(1187, 525, 6174, 8) },
  },
  --#endregion
  --#region The maze
  {
    text = "Enter the north door.",
    title = "The maze",
    actions = { Action.Direction:new(1181, 501, 6183) },
    postconditions = { Condition.DistanceTo:new(1256, 1957, 6178, 4) },
  },
  {
    text = "Enter the west door.",
    actions = { Action.Direction:new(1236, 2061, 6174) },
    postconditions = { Condition.DistanceTo:new(1107, 1975, 6243, 4) },
  },
  {
    text = "Enter the south door.",
    actions = { Action.Direction:new(1128, 237, 6232) },
    postconditions = { Condition.DistanceTo:new(1049, 3269, 6238, 4) },
  },
  {
    text = "Enter the east door.",
    actions = { Action.Direction:new(1069, 3557, 6242) },
    postconditions = { Condition.DistanceTo:new(1436, 269, 6488, 4) },
  },
  {
    text = "Follow the souls and enter the final door to the north.",
    actions = { Action.Direction:new(1441, 229, 6510) },
    postconditions = { Condition.DistanceTo:new(1123, 803, 6105, 4) },
  },
  --#endregion
  --#region Symbol searching
  {
    text = "After the cutscene, go to the north-east corner of the room to discover the first symbol. It is two steps south of the wall feature against the north wall and two steps east of the fissure.",
    title = "Symbol searching",
    actions = { Action.Direction:new(1127, 557, 6118) },
    postconditions = { Condition.DistanceTo:new(1127, 557, 6118, 1) },
  },
  {
    text = "Head to the north-west corner where the second symbol will be located a bit south-west of the wavy section of the ground.",
    actions = { Action.Direction:new(1114, 1035, 6118) },
    postconditions = { Condition.DistanceTo:new(1114, 1035, 6118, 1) },
  },
  {
    text = "Enter the sparkling doorway.",
    actions = { Action.ModelHighlight:new(doorway) },
    postconditions = { Condition.DistanceTo:new(1183, 773, 6103, 4) },
  },
  {
    text = "Go just west of the central fissure to find the first symbol.",
    actions = { Action.Direction:new(1176, 717, 6113) },
    postconditions = { Condition.DistanceTo:new(1176, 717, 6113, 1) },
  },
  {
    text = "Go west of the northern fissure to find the second symbol.",
    actions = { Action.Direction:new(1178, 611, 6121) },
    postconditions = { Condition.DistanceTo:new(1178, 611, 6121, 1) },
  },
  {
    text = "The last symbol is just north-west of the eastern fissure.",
    actions = { Action.Direction:new(1189, 805, 6115) },
    postconditions = { Condition.DistanceTo:new(1189, 805, 6115, 1) },
  },
  {
    text = "Enter the sparkling doorway.",
    actions = { Action.ModelHighlight:new(doorway) },
    postconditions = {
      Condition.DistanceTo:new(1437, 229, 6549, 4),
      Condition.ConversationText:new("I found myself walking the world, sharing the love of dreaming souls."),
    },
  },
  {
    text = "Follow the souls and enter the door to the north.",
    actions = { Action.Direction:new(1437, 1069, 6574) },
    postconditions = {
      Condition.DistanceTo:new(1438, 965, 6104, 4),
      Condition.ConversationText:new(
        "I approached the furious soul. It screamed as if on fire. I tried to soothe it with song, but still it seethed."
      ),
    },
  },
  --#endregion
  --#region Tree growing
  {
    text = "Take the burning sapling from the centre of the room and plant it in the pot overflowing with water to the southwest.",
    title = "Tree growing",
    actions = { Action.ModelHighlight:new(burningSaplingObject) },
    postconditions = { Condition.InventoryContains:new(burningSaplingInventory) },
  },
  {
    actions = { Action.InventoryHighlight:new(burningSaplingInventory), Action.ModelHighlight:new(waterPot) },
    postconditions = { Condition.ModelVisible:new(plantedWaterPot) },
  },
  {
    text = "Enter the doorway with a waterfall to the northeast.",
    actions = { Action.ModelHighlight:new(waterPortal) },
    postconditions = { Condition.ConversationText:new("Although it didn't rage anymore") },
  },
  {
    text = "Take the dead sapling from the previous pot and plant it in the pot with living fungi to the northwest.",
    actions = { Action.ModelHighlight:new(shinyWaterPot) },
    postconditions = { Condition.InventoryContains:new(threeLeafSapling) },
  },
  {
    actions = { Action.InventoryHighlight:new(threeLeafSapling), Action.ModelHighlight:new(fungusPot) },
    postconditions = { Condition.ModelVisible:new(plantedFungusPot) },
  },
  {
    text = "Enter the doorway surrounded by fungus to the southwest.",
    actions = { Action.ModelHighlight:new(fungusPortal) },
    postconditions = { Condition.ConversationText:new("The soul was weak.") },
  },
  {
    text = "Take the budding sapling from the fungus pot and plant it in the pot with ivy growing around it to the southeast.",
    actions = { Action.ModelHighlight:new(shinyFungusPot) },
    postconditions = { Condition.InventoryContains:new(buddingSapling) },
  },
  {
    actions = { Action.InventoryHighlight:new(buddingSapling), Action.ModelHighlight:new(ivyPot) },
    postconditions = { Condition.ModelVisible:new(plantedIvyPot) },
  },
  {
    text = "Enter the doorway surrounded by ivy to the west.",
    actions = { Action.ModelHighlight:new(ivyPortal) },
    postconditions = {
      Condition.ConversationText:new("The soul felt new growth and purpose. But it needed time to become strong."),
    },
  },
  {
    text = "Take the healthy sapling from the previous pot and plant it in the pot covered in cobwebs to the northeast.",
    actions = { Action.ModelHighlight:new(grownIvyPot) },
    postconditions = { Condition.InventoryContains:new(healthySapling) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(healthySapling),
      Action.ModelHighlight:new(webPot),
    },
    postconditions = { Condition.ModelVisible:new(plantedWebPot) },
  },
  {
    text = "Enter the doorway covered in cobwebs to the east.",
    actions = { Action.ModelHighlight:new(webPortal) },
    postconditions = {
      Condition.ConversationText:new(
        "It grew stronger than any soul I have ever encountered. It was so powerful, so overwhelming."
      ),
    },
  },
  --#endregion
  --#region The stomach of the dragon
  {
    text = "Proceed north and enter the doorway.",
    title = "The stomach of the dragon",
    actions = { Action.Direction:new(1438, 981, 6256) },
    postconditions = {
      Condition.ConversationText:new("And then there was darkness. My story ended and another began."),
    },
  },
  { --Split to remove direction arrow
    actions = {},
    postconditions = { Condition.ConversationText:new("Heh.") },
  },
  {
    text = "After the cutscene, jump into the chasm.",
    actions = { Action.Direction:new(1305, 1045, 6240) },
    postconditions = { Condition.DistanceTo:new(977, 1285, 6631, 5) },
  },
  {
    text = "Proceed through the three passageways and enter the openings whilst evading the acid, players with high life points or some food can simply run through the acid taking the shortest route to save time. Running straight through the acid deals some (10-150) damage. Use Surge and Dive when available.",
    actions = { Action.Direction:new(1003.5, 1285, 6618) },
    postconditions = { Condition.DistanceToWithHeight:new(1126, 1285, 6647, 4) },
  },
  {
    actions = { Action.Direction:new(1110.5, 1285, 6602) },
    postconditions = { Condition.DistanceToWithHeight:new(1229, 1285, 6645, 4) },
  },
  {
    actions = { Action.Direction:new(1273, 1301, 6611.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(12634.001953125, 1164, 2976, 8),
      Condition.ConversationText:new("Stop! You can't be here! This is our island. You need to get back on your ship."),
    },
  },
  {
    text = "After an interactive cutscene, finish the dialogue with the siren, Remora.",
    actions = {
      Action.ConversationHighlight:new("We're not on an island."),
      Action.ConversationHighlight:new("What you're talking about isn't real."),
      Action.ConversationHighlight:new("Don't you know how you got here?"),
      Action.ConversationHighlight:new("You have been stealing the souls of villagers."),
      Action.ConversationHighlight:new("Alright."),
      Action.ConversationHighlight:new("Wait, don't kill her!"),
      Action.ConversationHighlight:new("We need her to escape."),
    },
    postconditions = { Condition.DistanceTo:new(2994, 525, 3231, 8) },
  },
  {
    text = "Talk to the Raptor. You will receive 3 items. Make sure you'll have enough inventory space ahead of time.",
    actions = {
      Action.ModelHighlight:new(NPCs["the raptor"]),
      Action.ConversationHighlight:new("What now?"),
      Action.ConversationHighlight:new("We should talk to Lucille."),
    },
    postconditions = { Condition.ConversationText:new("The Raptor hands you Remora's necklace.") },
  },
  {
    text = "Head south-west to Rimmington and talk to Lucille.",
    actions = { Action.Direction:new(2946, 805, 3207) },
    postconditions = { Condition.DistanceTo:new(2946, 805, 3207, 3) },
  },
  { actions = { Action.ModelHighlight:new(lucille) }, postconditions = { Condition.QuestComplete:new() } },
  --#endregion
}

return Quest:new({
  name = "Song from the Depths",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1337644800,
  prereqQuests = {},
  neededItems = {
    ["Combat gear"] = { quantity = 1 },
    ["Food"] = { quantity = 8 },
  },
})
