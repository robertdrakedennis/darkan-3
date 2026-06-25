local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local koftik = Model.new(4449, {
  [2815] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [2820] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [2823] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2825] = Vertex.new(7, 724, -51, 108, 80, 56),
  [3175] = Vertex.new(0, 735, -7, 29, 141, 128),
})
local sirJerro = Model.new(5298, {
  [2824] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [2832] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2834] = Vertex.new(7, 724, -51, 108, 80, 56),
  [4049] = Vertex.new(-30, 721, -31, 149, 114, 46),
  [4199] = Vertex.new(30, 721, -31, 149, 114, 46),
})
local sirHarry = Model.new(5580, {
  [2977] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2981] = Vertex.new(7, 724, -51, 108, 80, 56),
  [2995] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3000] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [4049] = Vertex.new(40, 711, -12, 49, 37, 15),
})
local sirCarl = Model.new(5580, {
  [2977] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2981] = Vertex.new(7, 724, -51, 108, 80, 56),
  [2995] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3000] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [4049] = Vertex.new(40, 711, -12, 49, 37, 15),
})
local niloof = Model.new(5805, {
  [720] = Vertex.new(-237, 204, -157, 44, 42, 41),
  [722] = Vertex.new(-237, 254, -188, 44, 42, 41),
  [728] = Vertex.new(-175, 206, -202, 44, 42, 41),
  [4802] = Vertex.new(19, 551, -3, 77, 53, 40),
  [4818] = Vertex.new(-15, 550, -6, 77, 53, 40),
})
local witchsCat = Model.new(1575, {
  [13] = Vertex.new(40, 448, -60, 89, 93, 97),
  [14] = Vertex.new(40, 448, 20, 89, 93, 97),
  [15] = Vertex.new(-40, 448, -60, 89, 93, 97),
})
local holthion = Model.new(42678, {
  [10111] = Vertex.new(33, 1366, -242, 128, 127, 127),
  [35186] = Vertex.new(23, 1459, -276, 128, 127, 127),
  [35351] = Vertex.new(-23, 1459, -276, 128, 127, 127),
  [36053] = Vertex.new(57, 1476, -218, 128, 127, 127),
  [36495] = Vertex.new(-57, 1476, -218, 128, 127, 127),
})
local othanian = Model.new(42960, {
  [36571] = Vertex.new(-33, 1466, -267, 128, 127, 127),
  [36724] = Vertex.new(-13, 1503, -256, 128, 127, 127),
  [37060] = Vertex.new(33, 1466, -267, 128, 127, 127),
  [37213] = Vertex.new(13, 1503, -256, 128, 127, 127),
  [37216] = Vertex.new(13, 1503, -256, 128, 127, 127),
})
local doomion = Model.new(42378, {
  [36117] = Vertex.new(-52, 1475, -253, 128, 127, 127),
  [36119] = Vertex.new(-49, 1479, -263, 128, 127, 127),
  [36662] = Vertex.new(52, 1475, -253, 128, 127, 127),
  [36665] = Vertex.new(49, 1479, -263, 128, 127, 127),
  [36670] = Vertex.new(49, 1479, -263, 128, 127, 127),
})
local klank = Model.new(7539, {
  [3027] = Vertex.new(33, 346, -123, 117, 86, 24),
  [3033] = Vertex.new(3, 311, -142, 117, 86, 24),
  [3039] = Vertex.new(-16, 277, -153, 117, 86, 24),
  [3051] = Vertex.new(-74, 303, -119, 117, 86, 24),
  [3057] = Vertex.new(-87, 263, -114, 117, 86, 24),
})
local kalrag = Model.new(1824, {
  [161] = Vertex.new(283, 172, 708, 127, 126, 66),
  [165] = Vertex.new(283, 172, 708, 127, 126, 66),
  [350] = Vertex.new(-54, 84, -172, 154, 153, 47),
  [803] = Vertex.new(310, 40, -786, 127, 126, 66),
  [1289] = Vertex.new(-280, 168, 702, 127, 126, 66),
})
local discipleOfIban = Model.new(3003, {
  [1708] = Vertex.new(-2, 725, -59, 108, 79, 56),
  [1713] = Vertex.new(-7, 724, -51, 108, 79, 56),
  [1716] = Vertex.new(2, 725, -59, 108, 79, 56),
  [1718] = Vertex.new(7, 724, -51, 108, 79, 56),
  [2431] = Vertex.new(0, 735, -7, 28, 140, 127),
})
--#endregion
--#region Objects
local thatsTheCutestThingIveEverSeen = Model.new(276, {
  [123] = Vertex.new(356, 808, 100, 53, 38, 4),
  [126] = Vertex.new(356, 808, 100, 53, 38, 4),
  [127] = Vertex.new(412, 760, 52, 53, 38, 4),
  [129] = Vertex.new(356, 808, 100, 53, 38, 4),
  [202] = Vertex.new(240, 536, -448, 53, 38, 4),
})
local tomb = Model.new(8454, {
  [1889] = Vertex.new(887, 1037, 618, 93, 78, 59),
  [2421] = Vertex.new(-791, 1037, 480, 87, 73, 56),
  [3304] = Vertex.new(887, 1037, 618, 99, 79, 63),
  [3440] = Vertex.new(-883, 1037, 513, 93, 74, 59),
  [5359] = Vertex.new(887, 1037, -618, 99, 79, 63),
})
--#endregion
--#region Items
local shortbow = Model.new(144, { --all shortbows
  [3] = Vertex.new(-128, 16, -120, 97, 89, 89),
  [11] = Vertex.new(-128, 16, -120, 97, 89, 89),
})
local metalArrows = Model.new(537, { --all metal arrows I think?
  [54] = Vertex.new(-88, 0, -200, 69, 39, 6),
  [140] = Vertex.new(20, 0, -224, 91, 15, 8),
  [212] = Vertex.new(48, 0, -168, 91, 15, 8),
  [284] = Vertex.new(-12, 0, -148, 91, 15, 8),
  [356] = Vertex.new(-48, 0, -188, 91, 15, 8),
})
local zamorakRobeTop = Model.new(354, {
  [33] = Vertex.new(-132, 20, -124, 110, 18, 10),
  [35] = Vertex.new(-132, 20, -124, 110, 18, 10),
  [38] = Vertex.new(-132, 20, -124, 110, 18, 10),
  [333] = Vertex.new(132, 20, -124, 110, 18, 10),
  [337] = Vertex.new(132, 20, -124, 110, 18, 10),
})
local zamorakRobeBottom = Model.new(123, {
  [3] = Vertex.new(120, -4, -200, 110, 18, 10),
  [65] = Vertex.new(52, 12, 212, 110, 18, 10),
  [111] = Vertex.new(-120, -4, -200, 110, 18, 10),
  [116] = Vertex.new(-120, -4, -200, 110, 18, 10),
  [120] = Vertex.new(52, 12, 212, 110, 18, 10),
})
--#endregion
--#region Quest Items
local dampCloth = Model.new(96, {
  [3] = Vertex.new(48, 0, -84, 59, 49, 38),
  [47] = Vertex.new(-32, 0, 168, 59, 49, 38),
  [49] = Vertex.new(-32, -4, 168, 145, 130, 92),
  [51] = Vertex.new(120, -4, 132, 145, 130, 92),
  [96] = Vertex.new(48, -4, -84, 145, 130, 92),
})
local fireArrow = Model.new(96, {
  [3] = Vertex.new(24, 0, -160, 91, 15, 8),
  [9] = Vertex.new(-40, 0, -120, 91, 15, 8),
  [15] = Vertex.new(-16, 0, 148, 69, 39, 6),
  [26] = Vertex.new(-12, 0, 208, 106, 98, 97),
  [96] = Vertex.new(4, 0, 112, 106, 98, 97),
})
local litFireArrow = Model.multi({
  Model.new(39, {
    [18] = Vertex.new(-32, 0, -120, 91, 15, 8),
    [24] = Vertex.new(16, 0, -160, 91, 15, 8),
  }),
  Model.new(174, {
    [35] = Vertex.new(-24, 0, 192, 111, 80, 10, 0.5608),
    [38] = Vertex.new(-24, 0, 192, 111, 80, 10, 0.5608),
  }),
})
local orbOfLight = Model.new(420, {
  [170] = Vertex.new(0, 208, -16, 161, 96, 84, 0.4353),
  [371] = Vertex.new(0, 224, -16, 161, 96, 84, 0.4353),
  [381] = Vertex.new(0, 224, -16, 161, 96, 84, 0.4353),
  [386] = Vertex.new(0, 224, -16, 161, 96, 84, 0.4353),
  [410] = Vertex.new(-12, 224, 0, 161, 96, 84, 0.4353),
})
local pieceOfRailing = Model.new(48, {
  [1] = Vertex.new(-232, 0, -224, 31, 28, 28),
  [3] = Vertex.new(-200, 0, -248, 31, 28, 28),
  [4] = Vertex.new(-232, 0, -224, 31, 28, 28),
  [17] = Vertex.new(-164, 0, -72, 31, 28, 28),
  [47] = Vertex.new(148, 0, 172, 31, 28, 28),
})
local unicornHorn = Model.new(162, {
  [7] = Vertex.new(0, 16, 176, 163, 162, 85),
  [10] = Vertex.new(0, 16, 176, 163, 162, 85),
  [13] = Vertex.new(0, 16, 176, 163, 162, 85),
  [16] = Vertex.new(0, 16, 176, 163, 162, 85),
  [155] = Vertex.new(24, 32, -112, 163, 162, 85),
})
local paladinsBadge = Model.new(96, {
  [45] = Vertex.new(64, 0, 24, 96, 89, 88),
  [49] = Vertex.new(64, 0, 24, 96, 89, 88),
  [57] = Vertex.new(48, 0, 48, 96, 89, 88),
  [70] = Vertex.new(48, 0, 48, 96, 89, 88),
  [79] = Vertex.new(48, 0, 48, 96, 89, 88),
})
local witchsCatItem = Model.new(414, {
  [13] = Vertex.new(20, -24, 8, 51, 47, 47),
  [17] = Vertex.new(20, -24, 8, 51, 47, 47),
  [27] = Vertex.new(-20, -24, 8, 51, 47, 47),
  [28] = Vertex.new(-20, -24, 8, 51, 47, 47),
  [165] = Vertex.new(-20, -24, 8, 45, 41, 41),
})
local dollOfIban = Model.new(600, {
  [140] = Vertex.new(92, 0, 28, 65, 48, 13),
  [162] = Vertex.new(116, 0, 52, 65, 48, 13),
  [164] = Vertex.new(88, 0, -36, 65, 48, 13),
  [186] = Vertex.new(116, 0, -48, 65, 48, 13),
  [210] = Vertex.new(132, 0, 12, 65, 48, 13),
})
local demonNecklace = Model.new(432, {
  [1] = Vertex.new(10, 7, -61, 68, 11, 6),
  [20] = Vertex.new(-10, 7, -61, 68, 11, 6),
  [22] = Vertex.new(-10, 7, -61, 68, 11, 6),
  [56] = Vertex.new(10, 7, -61, 68, 11, 6),
  [300] = Vertex.new(0, -2, 72, 127, 127, 127),
})
local ibansShadow = Model.multi({
  Model.new(114, {
    [53] = Vertex.new(-136, 112, 0, 31, 29, 29),
    [57] = Vertex.new(-136, 112, 0, 31, 29, 29),
    [59] = Vertex.new(-136, 112, 0, 31, 29, 29),
    [62] = Vertex.new(-136, 112, 0, 31, 29, 29),
    [87] = Vertex.new(0, 112, -132, 31, 29, 29),
  }),
  Model.new(288, {
    [10] = Vertex.new(4, 348, -76, 61, 40, 77, 0.4980),
    [16] = Vertex.new(4, 348, -76, 61, 40, 77, 0.4980),
    [24] = Vertex.new(4, 348, -76, 61, 40, 77, 0.4980),
    [232] = Vertex.new(44, 348, -36, 61, 40, 77, 0.4980),
    [247] = Vertex.new(44, 348, -36, 61, 40, 77, 0.4980),
  }),
})
local dwarfBrew = Model.new(570, {
  [36] = Vertex.new(40, 140, 44, 83, 76, 76),
  [79] = Vertex.new(0, 20, -64, 92, 73, 38),
  [81] = Vertex.new(-48, 0, -44, 92, 73, 38),
  [98] = Vertex.new(52, 0, 40, 92, 73, 38),
  [99] = Vertex.new(68, 0, 0, 92, 73, 38),
  [107] = Vertex.new(0, 0, 60, 92, 73, 38),
  [117] = Vertex.new(-48, 0, 40, 92, 73, 38),
  [118] = Vertex.new(-48, 20, -44, 92, 73, 38),
  [123] = Vertex.new(-64, 0, 0, 92, 73, 38),
  [277] = Vertex.new(16, 164, 60, 92, 85, 85),
  [278] = Vertex.new(8, 140, 56, 92, 85, 85),
  [279] = Vertex.new(0, 140, 80, 92, 85, 85),
  [281] = Vertex.new(-60, 140, -56, 92, 85, 85),
  [286] = Vertex.new(-8, 200, -44, 83, 76, 76),
  [287] = Vertex.new(-8, 200, -36, 83, 76, 76),
  [319] = Vertex.new(-60, 104, 52, 92, 85, 85),
  [327] = Vertex.new(64, 104, 52, 92, 85, 85),
  [360] = Vertex.new(-24, 200, -40, 83, 76, 76),
  [372] = Vertex.new(36, 164, 52, 83, 76, 76),
  [374] = Vertex.new(64, 140, 52, 83, 76, 76),
})
local ibansAshes = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 57, 52, 52),
  [34] = Vertex.new(100, 0, 60, 57, 52, 52),
  [42] = Vertex.new(100, 0, 60, 57, 52, 52),
  [64] = Vertex.new(132, 0, -24, 57, 52, 52),
  [72] = Vertex.new(132, 0, -24, 57, 52, 52),
})
local klanksGauntlets = Model.new(312, {
  [73] = Vertex.new(112, 36, -72, 3, 35, 30),
  [78] = Vertex.new(112, 36, -72, 3, 35, 30),
  [92] = Vertex.new(-72, 44, -88, 3, 35, 30),
  [127] = Vertex.new(112, 36, -72, 3, 35, 30),
  [271] = Vertex.new(112, 36, -72, 3, 35, 30),
})
local ibansDove = Model.new(369, {
  [9] = Vertex.new(-108, 0, -192, 72, 67, 66),
  [116] = Vertex.new(228, 0, 92, 96, 89, 88),
  [126] = Vertex.new(176, 0, 104, 96, 89, 88),
  [128] = Vertex.new(180, 0, 140, 96, 89, 88),
  [140] = Vertex.new(100, 0, 196, 96, 89, 88),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Go to 1st floor (2nd floor[US]) of Ardougne Castle in East Ardougne.",
    title = "Getting started",
    actions = { Action.Direction:new(2571, 2525, 3307) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2571, 2917, 3306, 4),
      Condition.DistanceToWithHeight:new(2576, 2917, 3297, 5),
    },
  },
  {
    text = "Talk to King Lathas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king lathas"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to King Lathas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king lathas"]) },
    postconditions = { Condition.ConversationText:new("won't last long") },
  },
  {
    text = "Talk to Koftik in the western most part of West Ardougne.",
    title = "Getting in and across the bridge",
    neededItems = {
      ["Ropes"] = { quantity = 3 },
      ["Any shortbow"] = { quantity = 1 },
      ["Any metal arrows (bronze, iron, etc.)"] = { quantity = 10 },
    },
    recommendedItems = {},
    actions = {
      Action.Direction:new(2441, 693, 3315, { distance = 20 }),
      Action.ModelHighlight:new(koftik, { distance = 24 }),
      Action.ConversationHighlight:new("I'll take my chances."),
    },
    postconditions = { Condition.ConversationText:new("by the bridge") },
  },
  {
    text = "Enter the cave entrance.",
    actions = { Action.Direction:new(2437, 1153, 3315) },
    postconditions = { Condition.DistanceTo:new(2496, 2693, 9715, 4) },
  },
  {
    text = "Talk to Koftik at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2496, 2693, 9715),
        Location:new(2490, 2109, 9717),
        Location:new(2484, 1277, 9716),
        Location:new(2480, 997, 9716),
        Location:new(2480, 996, 9712),
        Location:new(2479, 893, 9709),
        Location:new(2472, 1002, 9706),
        Location:new(2470, 1213, 9706),
        Location:new(2463, 831, 9706),
        Location:new(2457, 957, 9711),
        Location:new(2457, 1205, 9713),
        Location:new(2452, 837, 9716),
      }),
      Action.ModelHighlight:new(koftik, { distance = 16 }),
      -- Action.ConversationHighlight:new("I'll take my chances."),
    },
    postconditions = { Condition.InventoryContains:new(dampCloth) },
  },
  {
    text = "Use the damp cloth on whichever arrow you brought.",
    actions = {
      Action.InventoryHighlight:new(dampCloth),
      Action.InventoryHighlight:new(metalArrows),
    },
    postconditions = { Condition.InventoryContains:new(fireArrow) },
  },
  {
    text = "Light your fire arrows.",
    actions = { Action.InventoryHighlight:new(fireArrow, true) },
    postconditions = { Condition.ChatText:new("light the cloth wrapped") },
  },
  {
    text = "Equip your bow/lit fire arrows, then stand on the marked tile.",
    actions = {
      Action.Direction:new(2448, 1125, 9721, { tile = true }),
      Action.InventoryHighlight:new(litFireArrow),
      Action.InventoryHighlight:new(shortbow),
    },
    postconditions = { Condition.DistanceTo:new(2448, 1125, 9721, 0) },
  },
  {
    text = "Fire at the guide rope attached to the bridge.",
    actions = { Action.Direction:new(2443.25, 909, 9718) },
    postconditions = { Condition.ChatText:new("arrow sets the rope") },
  },
  {
    text = "Wait for your character to run across the bridge.",
    postconditions = { Condition.DistanceTo:new(2442, 501, 9716, 1) },
  },
  {
    text = "Pick up the plank.",
    actions = { Action.ModelHighlight:new(Models.items["plank"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["plank"]) },
  },
  {
    text = "Use a rope on the old spike at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2435, 1085, 9714),
        Location:new(2434, 1052, 9708),
        Location:new(2436, 933, 9702),
        Location:new(2441, 869, 9699),
        Location:new(2448, 803, 9698),
        Location:new(2455, 613, 9698),
        Location:new(2461, 1189, 9699),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2459, 957, 9699, 4) },
  },
  {
    actions = {
      Action.Direction:new(2462.55, 1437, 9699),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.DistanceTo:new(2467, 1285, 9699, 1) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2466, 1285, 9699),
        Location:new(2468, 1101, 9696),
        Location:new(2474, 973, 9697),
        Location:new(2477, 822, 9699),
        Location:new(2482, 1197, 9701),
        Location:new(2486, 1133, 9703),
        Location:new(2490, 981, 9700),
        Location:new(2491, 1211, 9696),
        Location:new(2491, 717, 9690),
        Location:new(2490, 845, 9687),
        Location:new(2487, 1061, 9679),
        Location:new(2481, 837, 9679),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2480, 837, 9679, 1) },
  },
  {
    text = "Find your path across the grid. Keep a record of the path over the grid for Regicide quest.<ul><li>You can use the table on the wiki to save your path.</li></ul>",
    title = "Grid puzzle",
    postconditions = {
      Condition.DistanceTo:new(2466, 837, 9674, 1),
      Condition.DistanceTo:new(2466, 837, 9677, 1),
      Condition.DistanceTo:new(2466, 837, 9680, 1),
      Condition.DistanceTo:new(2466, 837, 9681, 1),
    },
  },
  {
    text = "Pull the lever to the south at the end to continue.",
    actions = { Action.Direction:new(2466, 837, 9672) },
    postconditions = { Condition.ChatText:new("portcullis opens") },
  },
  {
    text = "Follow the path. Step on the breaks in the line to avoid traps.",
    title = "Lights in the night",
    actions = {
      Action.PathGuide:new({
        Location:new(2464, 885, 9675),
        Location:new(2459, 852, 9674),
        Location:new(2454, 821, 9675),
        Location:new(2451, 541, 9678),
        Location:new(2446, 1004, 9678),
        Location:new(2444, 1069, 9677),
        Location:new(2442, 989, 9677),
        Location:new(2441, 989, 9677),
        Location:new(2439, 885, 9677),
        Location:new(2436, 714, 9676),
        Location:new(2434, 941, 9676),
        Location:new(2433, 981, 9676),
        Location:new(2431, 1053, 9676),
        Location:new(2429, 997, 9676),
        Location:new(2426, 965, 9675),
        Location:new(2422, 1253, 9674),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2423, 1125, 9674, 4) },
  },
  {
    text = "Follow the path, use the plank on the flat rocks.",
    actions = {
      Action.InventoryHighlight:new(Models.items["plank"]),
      Action.PathGuide:new({
        Location:new(2418, 957, 9679),
        Location:new(2418, 837, 9683),
        Location:new(2418, 765, 9686),
        Location:new(2416, 621, 9688),
        Location:new(2416, 589, 9690),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2416, 589, 9690, 1) },
  },
  {
    text = "Take the orb of light.",
    actions = { Action.ModelHighlight:new(orbOfLight, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(orbOfLight, 1) },
  },
  {
    text = "Return back to the altar.",
    actions = {
      Action.InventoryHighlight:new(Models.items["plank"]),
      Action.PathGuide:new({
        Location:new(2418, 957, 9679),
        Location:new(2418, 837, 9683),
        Location:new(2418, 765, 9686),
        Location:new(2416, 621, 9688),
        Location:new(2416, 589, 9690),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2418, 957, 9679, 1) },
  },
  {
    text = "Take the orb next to the ogres.",
    actions = {
      Action.Direction:new(2385, 1437, 9685, { distance = 12 }),
      Action.ModelHighlight:new(orbOfLight, { distance = 12, atLocation = Location:new(2385, 1437, 9685) }),
    },
    postconditions = { Condition.InventoryContains:new(orbOfLight, 2) },
  },
  {
    text = "Follow the path, use the plank on the flat rocks.",
    actions = {
      Action.InventoryHighlight:new(Models.items["plank"]),
      Action.PathGuide:new({
        Location:new(2410, 1109, 9674),
        Location:new(2407, 1165, 9674),
        Location:new(2405, 1093, 9675),
        Location:new(2403, 1093, 9675),
        Location:new(2402, 1093, 9675),
        Location:new(2400, 1093, 9675),
        Location:new(2397, 1093, 9677),
        Location:new(2395, 1093, 9677),
        Location:new(2394, 1093, 9676),
        Location:new(2392, 1093, 9676),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2391, 1061, 9676, 2) },
  },
  {
    text = "Take the orb of light.",
    actions = {
      Action.ModelHighlight:new(orbOfLight, { atLocation = Location:new(2386, 901, 9677) }),
    },
    postconditions = { Condition.InventoryContains:new(orbOfLight, 3) },
  },
  {
    text = "Return back to the altar.",
    actions = {
      Action.InventoryHighlight:new(Models.items["plank"]),
      Action.PathGuide:new({
        Location:new(2410, 1109, 9674),
        Location:new(2407, 1165, 9674),
        Location:new(2405, 1093, 9675),
        Location:new(2403, 1093, 9675),
        Location:new(2402, 1093, 9675),
        Location:new(2400, 1093, 9675),
        Location:new(2397, 1093, 9677),
        Location:new(2395, 1093, 9677),
        Location:new(2394, 1093, 9676),
        Location:new(2392, 1093, 9676),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2410, 1109, 9674, 1) },
  },
  {
    text = "Search the flat rock underneath the orb until successful.",
    actions = {
      Action.Direction:new(2382, 597, 9668, { tile = true }),
      Action.ConversationHighlight:new("Try to disarm it."),
    },
    postconditions = { Condition.InventoryContains:new(orbOfLight, 4) },
  },
  {
    text = "Return to the zombie camp (don't forget about disarming the traps).",
    actions = {
      Action.PathGuide:new({
        Location:new(2451, 541, 9678),
        Location:new(2446, 1004, 9678),
        Location:new(2444, 1069, 9677),
        Location:new(2442, 989, 9677),
        Location:new(2441, 989, 9677),
        Location:new(2439, 885, 9677),
        Location:new(2436, 714, 9676),
        Location:new(2434, 941, 9676),
        Location:new(2433, 981, 9676),
        Location:new(2431, 1053, 9676),
        Location:new(2429, 997, 9676),
        Location:new(2426, 965, 9675),
        Location:new(2422, 1253, 9674),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2451, 541, 9678, 4) },
  },
  {
    text = "Use the 4 orbs on the furnace.",
    actions = {
      Action.Direction:new(2455, 1421, 9682.75),
      Action.InventoryHighlight:new(orbOfLight),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(orbOfLight) }, --buggy
  },
  {
    text = "Return to the altar.",
    actions = {
      Action.PathGuide:new({
        Location:new(2451, 541, 9678),
        Location:new(2446, 1004, 9678),
        Location:new(2444, 1069, 9677),
        Location:new(2442, 989, 9677),
        Location:new(2441, 989, 9677),
        Location:new(2439, 885, 9677),
        Location:new(2436, 714, 9676),
        Location:new(2434, 941, 9676),
        Location:new(2433, 981, 9676),
        Location:new(2431, 1053, 9676),
        Location:new(2429, 997, 9676),
        Location:new(2426, 965, 9675),
        Location:new(2422, 1253, 9674),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2423, 1125, 9674, 4) },
  },
  {
    text = "Climb down the well.",
    actions = { Action.Direction:new(2416.5, 1609, 9674.5) },
    postconditions = { Condition.DistanceTo:new(2423, 1205, 9660, 4) },
  },
  {
    text = "Pick the lock to the cell.<ul><li>Optional: Search the two stacked crates for food.</li></ul>",
    actions = { Action.Direction:new(2393, 1497, 9654.5) },
    postconditions = { Condition.DistanceTo:new(2393, 997, 9653, 1) },
  },
  {
    text = "Dig the mud.",
    actions = { Action.Direction:new(2393, 997, 9650, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2392, 845, 9646, 1) },
  },
  {
    text = "Cross the ledge.",
    actions = { Action.Direction:new(2374, 861, 9644) },
    postconditions = { Condition.DistanceTo:new(2374, 901, 9638, 1) },
    jumpconditions = { Condition.DistanceTo:new(2372, 237, 9642, 1) },
    jumpOffset = -2,
  },
  {
    text = "Reaching the eastern side of the wooden bridge maze.<ul><li>Optional: With 50 Thieving, you can take the shortcut through the cages.</li></ul>",
    actions = {
      Action.PathGuide:new({ --wooden bridge maze
        Location:new(2373, 989, 9634),
        Location:new(2379, 869, 9634),
        Location:new(2381, 869, 9634),
        Location:new(2384, 869, 9634),
        Location:new(2384, 869, 9631),
        Location:new(2386, 869, 9631),
        Location:new(2389, 869, 9631),
        Location:new(2389, 869, 9627),
        Location:new(2391, 869, 9627),
        Location:new(2395, 869, 9627),
        Location:new(2395, 869, 9632),
        Location:new(2398, 869, 9632),
        Location:new(2403, 869, 9632),
        Location:new(2403, 869, 9637),
        Location:new(2405, 869, 9637),
        Location:new(2407, 869, 9637),
        Location:new(2411, 869, 9637),
        Location:new(2415, 229, 9637),
      }),

      Action.PathGuide:new({ --shortcut
        Location:new(2373, 989, 9634),
        Location:new(2372, 1581, 9627),
        Location:new(2374, 1509, 9624),
        Location:new(2376, 1189, 9620),
        Location:new(2380, 981, 9619),
        Location:new(2384, 885, 9619),
        Location:new(2391, 933, 9619),
        Location:new(2392, 933, 9619),
        Location:new(2393, 925, 9618),
        Location:new(2395, 957, 9618),
        Location:new(2396, 901, 9619),
        Location:new(2399, 973, 9619),
        Location:new(2400, 933, 9620),
        Location:new(2403, 941, 9620),
        Location:new(2404, 933, 9620),
      }),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2417, 181, 9637, 1), --wooden bridge maze
      Condition.DistanceTo:new(2405, 1045, 9620, 1), --shortcut
    },
  },
  {
    text = "Enter the obstacle pipe to the south.",
    actions = { Action.Direction:new(2418, 1489, 9605) },
    postconditions = { Condition.DistanceTo:new(2412, 1245, 9605, 1) },
  },
  {
    text = "Search the unicorn cage to find a piece of railing.",
    title = "Right of passage",
    actions = { Action.Direction:new(2397, 1465, 9604.5) },
    postconditions = { Condition.InventoryContains:new(pieceOfRailing) },
  },
  {
    text = "Use the railing on the boulder south of the unicorn.",
    actions = {
      Action.ModelHighlight:new(thatsTheCutestThingIveEverSeen),
      Action.InventoryHighlight:new(pieceOfRailing),
    },
    postconditions = { Condition.DistanceTo:new(2372, 2629, 9594, 4) },
  },
  {
    text = "Search the smashed cage to get a unicorn horn.<ul><li>The piece of railing can be dropped.</li></ul>",
    warning = "You're a monster...",
    actions = { Action.Direction:new(2372, 1465, 9603.5) },
    postconditions = { Condition.InventoryContains:new(unicornHorn) },
  },
  {
    text = "Pass through the tunnel north of the cage.",
    actions = { Action.Direction:new(2375.5, 1589, 9611) },
    postconditions = { Condition.DistanceTo:new(2371, 2349, 9666, 4) },
  },
  {
    text = "Kill Sir Jerro and take his badge.<ul><li>Talk to Sir Jerro first for some free food and potions.</li></ul>",
    actions = {
      Action.Direction:new(2424, 1213, 9721, { distance = 16 }),
      Action.ModelHighlight:new(sirJerro, { distance = 17 }),
      Action.ModelHighlight:new(paladinsBadge),
    },
    postconditions = { Condition.InventoryContains:new(paladinsBadge) },
  },
  {
    text = "Kill Sir Carl and take his badge.",
    actions = {
      Action.ModelHighlight:new(sirCarl),
      Action.ModelHighlight:new(paladinsBadge),
    },
    postconditions = { Condition.InventoryContains:new(paladinsBadge, 2) },
  },
  {
    text = "Kill Sir Harry and take his badge.",
    actions = {
      Action.ModelHighlight:new(sirHarry),
      Action.ModelHighlight:new(paladinsBadge),
    },
    postconditions = { Condition.InventoryContains:new(paladinsBadge, 3) },
  },
  {
    text = "Continue forward, using your plank on the flat rocks, until you see a well.",
    actions = { Action.InventoryHighlight:new(Models.items["plank"]) },
    postconditions = { Condition.DistanceTo:new(2382, 1197, 9718, 4) },
  },
  {
    text = "Use the badges and unicorn horn on the well.",
    actions = {
      Action.Direction:new(2373.5, 1049, 9718.5),
      Action.InventoryHighlight:new(paladinsBadge),
      Action.InventoryHighlight:new(unicornHorn),
    },
    postconditions = { Condition.ChatText:new("came from the skull") },
  },
  {
    text = "Open the door.",
    actions = { Action.Direction:new(2368, 1077, 9718.5) },
    postconditions = { Condition.DistanceTo:new(2173, 6413, 4725, 4) },
  },
  {
    text = "Run along the eastern path and descend the cave stairs.",
    title = "Meeting the dwarves",
    actions = { Action.Direction:new(2150, 6741, 4545) },
    postconditions = { Condition.ConversationText:new("is that you") },
  },
  {
    text = "Koftik will appear with some brief dialogue.",
    postconditions = { Condition.ConversationText:new("pray for you") },
  },
  {
    text = "Talk to Niloof the dwarf.",
    actions = { Action.ModelHighlight:new(niloof) },
    postconditions = { Condition.ConversationText:new("You too") },
  },
  {
    text = "Go back up the stairs to the east.",
    actions = { Action.Direction:new(2336, 1449, 9793) },
    postconditions = { Condition.DistanceTo:new(2150, 6741, 4545, 4) },
  },
  {
    text = "Follow the path and pick up the witch's cat.",
    title = "Black cat in the dark",
    actions = {
      Action.PathGuide:new({
        Location:new(2150, 6341, 4546),
        Location:new(2158, 6429, 4546),
        Location:new(2165, 6469, 4548),
        Location:new(2171, 6379, 4553),
        Location:new(2173, 6533, 4561),
        Location:new(2173, 6429, 4570),
        Location:new(2172, 6613, 4577),
        Location:new(2170, 6725, 4582),
        Location:new(2161, 6345, 4582),
        Location:new(2159, 5765, 4582),
        Location:new(2156, 5701, 4582),
        Location:new(2150, 5605, 4583),
        Location:new(2145, 5653, 4583),
      }),
      Action.ModelHighlight:new(witchsCat),
    },
    postconditions = { Condition.InventoryContains:new(witchsCatItem) },
  },
  {
    text = "Use the cat on the door to the witch's house.",
    actions = { Action.Direction:new(2157.5, 6013, 4566) },
    postconditions = { Condition.ChatText:new("takes the cat inside") },
  },
  {
    text = "Open the door to the house.",
    actions = { Action.Direction:new(2157.5, 6013, 4566) },
    postconditions = { Condition.DistanceTo:new(2156, 5413, 4566, 1) },
  },
  {
    text = "Search the chest. Progress all dialogue.</li><li>The History of Iban can be dropped.</li></ul>",
    actions = { Action.Direction:new(2157, 5813, 4564) },
    postconditions = { Condition.InventoryContains:new(dollOfIban) },
  },
  {
    text = "Kill Doomion, Othainian, and Holthion west of the witch and take their amulets.<ul><li>You can safespot them from the bridge.</li></ul>",
    title = "Ashes to ashes",
    actions = {
      Action.ModelHighlight:new(doomion),
      Action.ModelHighlight:new(othanian),
      Action.ModelHighlight:new(holthion),
    },
    postconditions = { Condition.InventoryContains:new(demonNecklace, 3) },
  },
  {
    text = "Open and search the chest north of the middle demon.",
    actions = { Action.Direction:new(2136, 6965, 4577.75) },
    postconditions = { Condition.InventoryContains:new(ibansShadow) },
  },
  {
    text = "Pour Iban's shadow on the doll of Iban.",
    actions = {
      Action.InventoryHighlight:new(ibansShadow),
      Action.InventoryHighlight:new(dollOfIban),
    },
    postconditions = { Condition.ChatText:new("seeps into the cloth") },
  },
  {
    text = "Talk to Klank the dwarf downstairs.",
    actions = { Action.Direction:new(2150, 6741, 4545) },
    postconditions = {
      Condition.DistanceTo:new(2336, 1449, 9793, 4),
      Condition.DistanceTo:new(2325, 853, 9804, 20),
      -- Condition.DistanceTo:new(, 4), --if you fall
    },
  },
  {
    actions = {
      Action.Direction:new(2323, 773, 9804, { distance = 22 }),
      Action.ModelHighlight:new(klank, { distance = 26 }),
    },
    postconditions = { Condition.ConversationText:new("slap for") },
  },
  {
    text = "Take a bucket if you don't have one.",
    actions = {
      Action.Direction:new(2309, 1093, 9801, { distance = 8 }),
      Action.ModelHighlight:new(Models.items["bucket"], { distance = 8 }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["bucket"]) },
  },
  {
    text = "Use the bucket on the barrel in the smaller house to receive a dwarf brew.",
    actions = {
      Action.Direction:new(2327, 1485, 9799),
      Action.InventoryHighlight:new(Models.items["bucket"]),
    },
    postconditions = { Condition.ChatText:new("fill the bucket") },
  },
  {
    text = "Use the dwarf brew on the tomb to the east.<ul><li>Drop the bucket after.</li></ul>",
    actions = {
      Action.Direction:new(2357.5, 2677, 9803, { distance = 16 }),
      Action.ModelHighlight:new(tomb, { distance = 16 }),
      Action.InventoryHighlight:new(dwarfBrew),
    },
    postconditions = { Condition.ConversationText:new("strong alcohol") },
  },
  {
    text = "Light the tomb to obtain Iban's ashes.",
    actions = { Action.ModelHighlight:new(tomb) },
    postconditions = { Condition.InventoryContains:new(ibansAshes) },
  },
  {
    text = "Use Iban's ashes on the doll of Iban.",
    actions = {
      Action.InventoryHighlight:new(ibansAshes),
      Action.InventoryHighlight:new(dollOfIban),
    },
    postconditions = { Condition.ChatText:new("ashes into the doll") },
  },
  {
    text = "Run to the furthest north-east corner and kill Kalrag the spider.<ul><li>*IMPORTANT* Upon killing Kalrag, the blessed spiders will begin attacking with range, so quickly enable Deflect/Protect from Ranged and run away from the spiders.</li></ul>",
    warning = "Immediately use protect from range after killing Kalrag, then run.",
    actions = {
      Action.Direction:new(2358, 301, 9911, { distance = 20 }),
      Action.ModelHighlight:new(kalrag, { distance = 26 }),
    },
    postconditions = { Condition.ChatText:new("poisoned blood") },
  },
  {
    text = "Go to the north-western corner and ascend the stairs to the half-soulless area.",
    title = "Caging his dove",
    actions = { Action.Direction:new(2304, 3925, 9915) },
    postconditions = { Condition.DistanceTo:new(2113, 4, 4729, 4) },
  },
  {
    text = "Follow the path to the cage.",
    actions = {
      Action.PathGuide:new({
        Location:new(2113, 6373, 4729),
        Location:new(2116, 6805, 4722),
        Location:new(2116, 6780, 4712),
        Location:new(2116, 6283, 4700),
        Location:new(2116, 6855, 4688),
        Location:new(2117, 6821, 4686),
        Location:new(2120, 7029, 4686),
        Location:new(2123, 7141, 4686),
        Location:new(2128, 7333, 4686),
        Location:new(2128, 8261, 4691),
        Location:new(2129, 8261, 4691),
        Location:new(2129, 8773, 4697),
        Location:new(2131, 8773, 4698),
        Location:new(2138, 8197, 4698),
        Location:new(2140, 8197, 4699),
        Location:new(2140, 8037, 4702),
        Location:new(2136, 7813, 4702),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2137, 7813, 4702, 4) },
  },
  {
    text = "Equip Klank's gauntlets and search the cage to find Iban's dove.",
    actions = {
      Action.Direction:new(2134.5, 8213, 4702.5),
      Action.InventoryHighlight:new(klanksGauntlets),
    },
    postconditions = { Condition.InventoryContains:new(ibansDove) },
  },
  {
    text = "Use the Iban's dove on the doll of Iban.",
    actions = {
      Action.InventoryHighlight:new(ibansDove),
      Action.InventoryHighlight:new(dollOfIban),
    },
    postconditions = { Condition.ChatText:new("dove's skeleton into dust") },
  },
  {
    text = "Kill a Disciple of Iban for their robes at the end of the path.",
    title = "Ultimate demise",
    actions = {
      Action.PathGuide:new({
        Location:new(2136, 7813, 4702),
        Location:new(2140, 8037, 4702),
        Location:new(2140, 8197, 4699),
        Location:new(2138, 8197, 4698),
        Location:new(2131, 8773, 4698),
        Location:new(2129, 8773, 4697),
        Location:new(2129, 8261, 4691),
        Location:new(2128, 8261, 4691),
        Location:new(2128, 7333, 4687),
        Location:new(2129, 7333, 4685),
        Location:new(2131, 7077, 4685),
        Location:new(2131, 7077, 4686),
        Location:new(2134, 7045, 4686),
        Location:new(2134, 7077, 4689),
        Location:new(2140, 7077, 4689),
        Location:new(2140, 7077, 4685),
        Location:new(2152, 7045, 4685),
        Location:new(2152, 7045, 4683),
        Location:new(2153, 7069, 4683),
        Location:new(2153, 6373, 4678),
        Location:new(2154, 6373, 4676),
        Location:new(2160, 5893, 4676),
        Location:new(2160, 5381, 4670),
        Location:new(2165, 5381, 4670),
        Location:new(2165, 5381, 4667),
        Location:new(2162, 5381, 4667),
        Location:new(2162, 5381, 4666),
        Location:new(2162, 5381, 4662),
      }),
      Action.ModelHighlight:new(discipleOfIban, { distance = 26, highlightPriority = "closest" }),
      Action.ModelHighlight:new(zamorakRobeTop),
      Action.ModelHighlight:new(zamorakRobeBottom),
    },
    postconditions = {
      Condition.InventoryContains:new(zamorakRobeTop),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(zamorakRobeTop),
      Action.ModelHighlight:new(zamorakRobeBottom),
    },
    postconditions = {
      Condition.InventoryContains:new(zamorakRobeBottom),
    },
  },
  {
    text = "Un-equip everything and equip the robes.<ul><li>You don't need planks or rope anymore.</li><li>If you don't have 3 free backpack spaces you will miss out on 15 death runes and 30 fire runes because you will instantly be teleported away after Iban is destroyed.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(zamorakRobeTop),
      Action.InventoryHighlight:new(zamorakRobeBottom),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(zamorakRobeTop) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(zamorakRobeTop),
      Action.InventoryHighlight:new(zamorakRobeBottom),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(zamorakRobeBottom) },
  },
  {
    text = "Open the door at the end of the path.<ul><li>Low level players should try to time the magical spikes to prevent being hit.</li></ul>",
    actions = {
      Action.PathGuide:new({
        Location:new(2161, 5381, 4660),
        Location:new(2161, 5157, 4657),
        Location:new(2161, 5093, 4653),
        Location:new(2159, 5093, 4653),
        Location:new(2159, 5093, 4649),
        Location:new(2153, 5093, 4649),
        Location:new(2150, 5733, 4648),
        Location:new(2146, 6373, 4648),
        Location:new(2145, 6373, 4648),
      }),
    },
    postconditions = { Condition.ChatText:new("open the large doors") },
  },
  {
    text = "Use the doll on the well.",
    actions = {
      Action.Direction:new(2136.5, 6873, 4647.5),
      Action.InventoryHighlight:new(dollOfIban),
    },
    postconditions = { Condition.ChatText:new("thrown from the temple") },
  },
  {
    text = "Talk to Koftik at the west end of the cave.",
    actions = {
      Action.Direction:new(2443, 4757, 9607, { distance = 24 }),
      Action.ModelHighlight:new(koftik, { distance = 25 }),
    },
    postconditions = { Condition.ChatText:new("and back to the cave entrance") },
  },
  {
    text = "Exit the Underground Pass.",
    title = "Finishing up",
    actions = { Action.Direction:new(2498, 3093, 9715.5) },
    postconditions = { Condition.DistanceTo:new(2439, 637, 3315, 4) },
  },
  {
    text = "Return to King Lathas in Ardougne.",
    actions = { Action.Direction:new(2571, 2525, 3307) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2571, 2917, 3306, 4),
      Condition.DistanceToWithHeight:new(2576, 2917, 3297, 5),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king lathas"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Underground Pass",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1046649600,
  prereqQuests = { "Biohazard" },
  questReqs = {
    Types.QuestReq.skill("Ranged", 25),
  },
  neededItems = {
    ["Ropes"] = { quantity = 3, model = Models.items["rope"] },
    ["Any shortbow"] = { quantity = 1, model = shortbow },
    ["Any metal arrows (bronze, iron, etc.)"] = { quantity = 10, model = metalArrows },
    ["Bucket"] = { quantity = 1, model = Models.items["bucket"], duringQuest = true },
    ["Plank"] = { quantity = 1, model = Models.items["plank"], duringQuest = true },
  },
  recommendedItems = {
    ["Any agility boost"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Kalrag"] = { level = "81" },
    ["Sir Harry"] = { level = "81" },
    ["Sir Jerro"] = { level = "81" },
    ["Sir Carl"] = { level = "81" },
    ["Doomion"] = { level = "78" },
    ["Othanian"] = { level = "78" },
    ["Holthion"] = { level = "78" },
    ["Disciple of Iban"] = { level = "44" },
  },
})
