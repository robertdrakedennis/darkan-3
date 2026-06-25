local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--#region NPCs
local guildmaster = Model.new(7104, {
  [1282] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [1290] = Vertex.new(2, 725, -59, 106, 78, 54),
  [1292] = Vertex.new(7, 724, -51, 106, 78, 54),
  [6279] = Vertex.new(-65, 670, 50, 54, 43, 10),
  [6310] = Vertex.new(65, 670, 50, 54, 43, 10),
})
local oziach = Model.new(5307, {
  [1309] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [1314] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [1317] = Vertex.new(2, 725, -59, 106, 78, 54),
  [1319] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3133] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local smallRatZombie = Model.new(2979, {
  [2] = Vertex.new(12, 46, -182, 134, 134, 123),
  [6] = Vertex.new(-12, 46, -182, 134, 134, 123),
  [14] = Vertex.new(18, 50, -172, 134, 134, 123),
  [27] = Vertex.new(-18, 50, -172, 134, 134, 123),
  [185] = Vertex.new(22, 48, -164, 144, 144, 132),
})
local thinPatterenedGhost = Model.new(1446, {
  [1054] = Vertex.new(204, 600, 8, 119, 119, 130, 0.8431),
  [1056] = Vertex.new(204, 608, 8, 119, 119, 130, 0.8431),
  [1074] = Vertex.new(-204, 608, 8, 119, 119, 130, 0.8431),
  [1076] = Vertex.new(-204, 608, 8, 119, 119, 130, 0.8431),
  [1077] = Vertex.new(-204, 600, 8, 119, 119, 130, 0.8431),
})
local roundShieldSkeleton = Model.new(5142, {
  [1328] = Vertex.new(3, 729, 62, 121, 110, 92),
  [1350] = Vertex.new(-2, 729, 62, 121, 110, 92),
  [1394] = Vertex.new(2, 729, 61, 53, 48, 40),
  [4919] = Vertex.new(26, 862, -37, 71, 69, 65),
  [4928] = Vertex.new(-26, 862, -37, 71, 69, 65),
})
local melzar = Model.new(3597, {
  [1753] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [1758] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [1761] = Vertex.new(2, 725, -59, 106, 78, 54),
  [1763] = Vertex.new(7, 724, -51, 106, 78, 54),
  [2329] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local smallLesserDemon = Model.new(4095, {
  [41] = Vertex.new(71, 557, -185, 70, 58, 44),
  [50] = Vertex.new(20, 642, -172, 102, 85, 64),
  [54] = Vertex.new(-20, 642, -172, 102, 85, 64),
  [56] = Vertex.new(13, 689, -153, 102, 85, 64),
  [60] = Vertex.new(-13, 689, -153, 102, 85, 64),
})
local wormbrain = Model.new(4647, {
  [486] = Vertex.new(-15, 539, -103, 138, 138, 127),
  [488] = Vertex.new(11, 538, -100, 138, 138, 127),
  [489] = Vertex.new(12, 529, -104, 138, 138, 127),
  [2302] = Vertex.new(44, 569, -34, 137, 105, 42),
  [2304] = Vertex.new(40, 562, -39, 137, 105, 42),
})
local klarense = Model.new(3981, {
  [2704] = Vertex.new(-2, 725, -59, 108, 79, 55),
  [2709] = Vertex.new(-7, 724, -51, 108, 79, 55),
  [2712] = Vertex.new(2, 725, -59, 108, 79, 55),
  [2714] = Vertex.new(7, 724, -51, 108, 79, 55),
  [3055] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local elvarg = Model.new(4515, {
  [188] = Vertex.new(-176, 532, -212, 51, 51, 26),
  [206] = Vertex.new(176, 532, -212, 51, 51, 26),
  [845] = Vertex.new(-80, 544, -816, 84, 87, 92),
  [1638] = Vertex.new(48, 568, -952, 38, 38, 20),
  [4461] = Vertex.new(-44, 568, -944, 154, 158, 167),
})
--#endregion

--#region Objects
local deckHole = Model.new(42, {
  [1] = Vertex.new(22, 16, 555, 96, 70, 49),
  [3] = Vertex.new(124, 16, 555, 96, 70, 49),
  [24] = Vertex.new(-116, 16, 517, 96, 70, 49),
  [31] = Vertex.new(-148, 16, -270, 96, 70, 49),
  [42] = Vertex.new(-254, 16, 252, 96, 70, 49),
})
local crandorHole = Model.new(780, {
  [78] = Vertex.new(2052, 3861, 4932, 157, 157, 157),
  [80] = Vertex.new(1880, 3860, 5116, 157, 157, 157),
  [84] = Vertex.new(1880, 3860, 5116, 157, 157, 157),
  [87] = Vertex.new(1880, 3860, 5116, 157, 157, 157),
  [423] = Vertex.new(2060, 3848, 4664, 157, 157, 157),
})
--#endregion

--#region Items
local unfiredBowl = Model.new(288, {
  [1] = Vertex.new(24, 4, 36, 73, 52, 6),
  [2] = Vertex.new(48, 100, 84, 73, 52, 6),
  [3] = Vertex.new(84, 100, 48, 73, 52, 6),
  [6] = Vertex.new(100, 100, 0, 73, 52, 6),
  [7] = Vertex.new(-16, 4, 40, 73, 52, 6),
})
local crayfishCage = Model.new(390, {
  [1] = Vertex.new(-80, 12, 80, 66, 55, 42),
  [2] = Vertex.new(80, 0, 80, 66, 55, 42),
  [3] = Vertex.new(80, 12, 80, 66, 55, 42),
  [5] = Vertex.new(-80, 0, 80, 66, 55, 42),
  [31] = Vertex.new(-64, 12, -72, 73, 67, 67),
})
local wizardsMindBomb = Model.new(306, {
  [1] = Vertex.new(-8, 96, -32, 161, 147, 161),
  [2] = Vertex.new(24, 104, -40, 161, 147, 161),
  [3] = Vertex.new(24, 96, -32, 161, 147, 161),
  [5] = Vertex.new(-8, 104, -40, 161, 147, 161),
  [8] = Vertex.new(24, 112, -32, 161, 147, 161),
})
local antiDragonShield = Model.multi({
  Model.new(1116, {
    [1] = Vertex.new(-72, 33, 50, 41, 6, 3),
    [2] = Vertex.new(-74, 35, 51, 41, 6, 3),
    [3] = Vertex.new(-63, 38, 49, 41, 6, 3),
    [5] = Vertex.new(-57, 42, 57, 58, 9, 5),
    [6] = Vertex.new(-56, 40, 56, 58, 9, 5),
  }),
  Model.new(36, {
    [1] = Vertex.new(13, 56, -203, 168, 131, 87),
    [2] = Vertex.new(-13, 56, -229, 168, 131, 87),
    [3] = Vertex.new(-13, 56, -203, 168, 131, 87),
    [7] = Vertex.new(-71, 39, -136, 168, 131, 87),
    [8] = Vertex.new(-90, 30, -158, 168, 131, 87),
  }),
})
local elvargsHead = Model.new(882, {
  [1] = Vertex.new(120, 60, 36, 17, 17, 9),
  [2] = Vertex.new(156, 48, 48, 17, 17, 9),
  [3] = Vertex.new(148, 60, 48, 17, 17, 9),
  [4] = Vertex.new(104, 0, 16, 38, 38, 20),
  [7] = Vertex.new(32, 0, 44, 37, 31, 28),
})
--#endregion

--#region Quest Items
local mazeKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 95, 87, 86),
  [2] = Vertex.new(-12, 16, -84, 95, 87, 86),
  [3] = Vertex.new(-20, 16, -92, 95, 87, 86),
  [6] = Vertex.new(-60, 16, -52, 95, 87, 86),
  [7] = Vertex.new(-32, 16, -48, 95, 87, 86),
})
local redKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 152, 24, 13),
  [2] = Vertex.new(-12, 16, -84, 152, 24, 13),
  [3] = Vertex.new(-20, 16, -92, 152, 24, 13),
  [6] = Vertex.new(-60, 16, -52, 152, 24, 13),
  [7] = Vertex.new(-32, 16, -48, 152, 24, 13),
})
local orangeKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 152, 97, 13),
  [2] = Vertex.new(-12, 16, -84, 152, 97, 13),
  [3] = Vertex.new(-20, 16, -92, 152, 97, 13),
  [6] = Vertex.new(-60, 16, -52, 152, 97, 13),
  [7] = Vertex.new(-32, 16, -48, 152, 97, 13),
})
local yellowKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 152, 151, 13),
  [2] = Vertex.new(-12, 16, -84, 152, 151, 13),
  [3] = Vertex.new(-20, 16, -92, 152, 151, 13),
  [6] = Vertex.new(-60, 16, -52, 152, 151, 13),
  [7] = Vertex.new(-32, 16, -48, 152, 151, 13),
})
local blueKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 13, 17, 152),
  [2] = Vertex.new(-12, 16, -84, 13, 17, 152),
  [3] = Vertex.new(-20, 16, -92, 13, 17, 152),
  [6] = Vertex.new(-60, 16, -52, 13, 17, 152),
  [7] = Vertex.new(-32, 16, -48, 13, 17, 152),
})
local magentaKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 152, 13, 151),
  [2] = Vertex.new(-12, 16, -84, 152, 13, 151),
  [3] = Vertex.new(-20, 16, -92, 152, 13, 151),
  [6] = Vertex.new(-60, 16, -52, 152, 13, 151),
  [7] = Vertex.new(-32, 16, -48, 152, 13, 151),
})
local greenKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 20, 104, 22),
  [2] = Vertex.new(-12, 16, -84, 20, 104, 22),
  [3] = Vertex.new(-20, 16, -92, 20, 104, 22),
  [6] = Vertex.new(-60, 16, -52, 20, 104, 22),
  [7] = Vertex.new(-32, 16, -48, 20, 104, 22),
})
local mapPieceOne = Model.new(117, {
  [1] = Vertex.new(-56, 12, 128, 149, 134, 94),
  [2] = Vertex.new(-32, 12, 92, 149, 134, 94),
  [3] = Vertex.new(-56, 12, 100, 149, 134, 94),
  [6] = Vertex.new(36, 12, 128, 149, 134, 94),
  [17] = Vertex.new(-64, 12, -76, 149, 134, 94),
})
local mapPieceTwo = Model.new(282, {
  [1] = Vertex.new(92, 32, -12, 151, 136, 96),
  [2] = Vertex.new(52, 32, 136, 151, 136, 96),
  [3] = Vertex.new(80, 32, 136, 151, 136, 96),
  [6] = Vertex.new(60, 32, -20, 151, 136, 96),
  [8] = Vertex.new(28, 32, 116, 151, 136, 96),
})
local mapPieceThree = Model.new(207, {
  [1] = Vertex.new(40, 32, 132, 151, 135, 96),
  [2] = Vertex.new(-64, 32, 112, 151, 135, 96),
  [3] = Vertex.new(-48, 32, 128, 151, 135, 96),
  [9] = Vertex.new(-40, 32, -136, 151, 135, 96),
  [12] = Vertex.new(48, 32, 108, 151, 135, 96),
})
local map = Model.new(594, {
  [1] = Vertex.new(224, 32, -20, 151, 135, 96),
  [2] = Vertex.new(188, 32, 116, 151, 135, 96),
  [3] = Vertex.new(212, 32, 116, 151, 135, 96),
  [6] = Vertex.new(196, 32, -28, 151, 135, 96),
  [8] = Vertex.new(164, 32, 100, 151, 135, 96),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the Guildmaster in the Champions' Guild near the Varrock lodestone.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered'.</li></ul>",
    title = "Starting out",
    actions = { Action.Direction:new(3191, 1221, 3360) },
    postconditions = { Condition.DistanceTo:new(3191, 1221, 3360, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("Can I have a quest?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with the Guildmaster.",
    actions = { Action.ModelHighlight:new(guildmaster) },
    postconditions = { Condition.ConversationText:new("Oziach") },
  },
  {
    text = "Talk to Oziach in the house directly north of the Edgeville lodestone.",
    title = "Going on an adventure",
    actions = { Action.Direction:new(3068, 997, 3516) },
    postconditions = { Condition.DistanceTo:new(3068, 997, 3516, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(oziach),
      Action.ConversationHighlight:new("Can you sell me a rune platebody?"),
      Action.ConversationHighlight:new("The Guildmaster of the Champions' Guild told me."),
      Action.ConversationHighlight:new("I thought you were going to give me a quest."),
      Action.ConversationHighlight:new("A dragon, that sounds like fun."),
    },
    postconditions = { Condition.ConversationText:new("Go talk to the Guildmaster in the Champions' Guild.") },
  },
  {
    text = "Go back to the Guildmaster in the Champions' Guild.",
    actions = { Action.Direction:new(3191, 1221, 3360) },
    postconditions = { Condition.DistanceTo:new(3191, 1221, 3360, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("I talked to Oziach..."),
    },
    postconditions = {
      Condition.ConversationText:new("If you're serious about taking on Elvarg, first you'll need to get to Crandor."),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("How can I find the route to Crandor?"),
    },
    postconditions = {
      Condition.ConversationText:new("Only one map exists that shows the route through the reefs of Crandor."),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("Where is Melzar's map piece?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Melzar built a castle on the site of the Crandorian refugee camp, north of Rimmington."
      ),
      Condition.InventoryContains:new(mazeKey),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("Where is Thalzar's map piece?"),
    },
    postconditions = { Condition.ConversationText:new("Thalzar was the most paranoid of the three wizards.") },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("Where is Lozar's map piece?"),
    },
    postconditions = { Condition.ConversationText:new("Unfortunately, goblin raiders killed her and stole everything") },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("Where can I find the right ship?"),
    },
    postconditions = {
      Condition.ConversationText:new("If there's still one in existence, it's probably in Port Sarim"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("How can I protect myself from the dragon's breath?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "I believe the Duke of Lumbridge has a special shield in his armoury that is enchanted against dragon's breath."
      ),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(guildmaster),
      Action.ConversationHighlight:new("Okay, I'll get going!"),
    },
    postconditions = { Condition.ConversationText:new("Okay, I'll get going!") },
  },
  {
    text = "Go to Melzar's Maze, north-west of Rimmington.",
    title = "First map part",
    neededItems = {
      ["Maze key"] = { quantity = 1, model = mazeKey },
      ["Combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2937, 709, 3246.5) },
    postconditions = { Condition.DistanceTo:new(2924, 1925, 9584, 8) },
  },
  {
    text = "Enter the door with the maze key.",
    actions = { Action.Direction:new(2911.5, 965, 9584) },
    postconditions = { Condition.DistanceToWithHeight:new(2910, 965, 9584, 2) },
  },
  {
    text = "Kill the small zombie rat with the long tail and pick up the red key.",
    actions = { Action.ModelHighlight:new(smallRatZombie), Action.ModelHighlight:new(redKey) },
    postconditions = { Condition.InventoryContains:new(redKey) },
  },
  {
    text = "With the red key in your inventory, go through the north-west door.",
    actions = { Action.InventoryHighlight:new(redKey), Action.Direction:new(2896.5, 965, 9589) },
    postconditions = { Condition.DistanceTo:new(2894, 965, 9589, 2) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(2899, 965, 9592) },
    postconditions = { Condition.DistanceToWithHeight:new(2898, 2149, 9592, 3) },
  },
  {
    text = "Kill the thin hooded ghost with the patterned robe to receive the orange key.",
    actions = { Action.ModelHighlight:new(thinPatterenedGhost), Action.ModelHighlight:new(orangeKey) },
    postconditions = { Condition.InventoryContains:new(orangeKey) },
  },
  {
    text = "With the orange key in your inventory, go through the door second from the north on the eastern wall.",
    actions = { Action.Direction:new(2901.5, 2149, 9589) },
    postconditions = { Condition.DistanceTo:new(2903, 2149, 9589, 2) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(2905, 2149, 9590) },
    postconditions = { Condition.DistanceToWithHeight:new(2905, 3333, 9589, 3) },
  },
  {
    text = "Kill the skeleton with the small round shield and pick up the yellow key.",
    actions = { Action.ModelHighlight:new(roundShieldSkeleton), Action.ModelHighlight:new(yellowKey) },
    postconditions = { Condition.InventoryContains:new(yellowKey) },
  },
  {
    text = "With the yellow key in your inventory, go through the south-western most door.",
    actions = { Action.Direction:new(2895, 3333, 9585.5) },
    postconditions = { Condition.DistanceTo:new(2895, 3333, 9583, 2) },
  },
  {
    text = "Climb down the ladder at the end of the hallway.",
    actions = { Action.Direction:new(2911, 3333, 9576) },
    postconditions = { Condition.DistanceToWithHeight:new(2910, 2149, 9576, 3) },
  },
  {
    text = "Climb down the ladder again.",
    actions = { Action.Direction:new(2908, 2149, 9576) },
    postconditions = { Condition.DistanceToWithHeight:new(2909, 965, 9576, 3) },
  },
  {
    text = "Climb down the ladder again.",
    actions = { Action.Direction:new(2903, 965, 9576) },
    postconditions = { Condition.DistanceToWithHeight:new(2932, 965, 9641, 3) },
  },
  {
    text = "Kill the zombies until one drops a blue key. One of them will drop a blue key.",
    actions = { Action.ModelHighlight:new(NPCs["zombie"]), Action.ModelHighlight:new(blueKey) },
    postconditions = { Condition.InventoryContains:new(blueKey) },
  },
  {
    text = "With the blue key in your inventory, open the blue door.",
    actions = { Action.Direction:new(2930.5, 965, 9644) },
    postconditions = { Condition.DistanceTo:new(2928, 965, 9644, 2) },
  },
  {
    text = "Kill Melzar the Mad and pick up the magenta key.",
    actions = { Action.ModelHighlight:new(melzar), Action.ModelHighlight:new(magentaKey) },
    postconditions = { Condition.InventoryContains:new(magentaKey) },
  },
  {
    text = "With the magenta key in your inventory, pass through the magenta door.",
    actions = { Action.Direction:new(2929, 965, 9651.5) },
    postconditions = { Condition.DistanceTo:new(2929, 965, 9653, 2) },
  },
  {
    text = "Kill the lesser demon and pick up the green key.",
    actions = { Action.ModelHighlight:new(smallLesserDemon), Action.ModelHighlight:new(greenKey) },
    postconditions = { Condition.InventoryContains:new(greenKey) },
  },
  {
    text = "With the green key in your inventory, pass through the green door.",
    actions = { Action.Direction:new(2936, 965, 9655.5) },
    postconditions = { Condition.DistanceTo:new(2936, 965, 9658, 2) },
  },
  {
    text = "Open the chest and search it for the map part.<ul><li>Note: Make sure you finish the dialogue, or you will not receive the map part.</li></ul>",
    actions = { Action.Direction:new(2935, 965, 9657) },
    postconditions = { Condition.InventoryContains:new(mapPieceOne) },
  },
  {
    text = "Talk to the Oracle on Ice Mountain south-west of the Edgeville lodestone.",
    title = "Second map part",
    actions = { Action.Direction:new(3011, 6149, 3500) },
    postconditions = { Condition.DistanceTo:new(3011, 6149, 3500, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["oracle"]),
      Action.ConversationHighlight:new("I seek a piece of the map to the island of Crandor."),
    },
    postconditions = { Condition.ConversationText:new("Last, a bowl that's not seen heat.") },
  },
  {
    text = "Travel south into the dwarven mines.",
    actions = { Action.Direction:new(3018.5, 2565, 3450) },
    postconditions = { Condition.DistanceTo:new(3018, 965, 9850, 8) },
  },
  {
    text = "Use the unfired bowl on the 'Magic door'.",
    actions = { Action.ModelHighlight:new(unfiredBowl), Action.Direction:new(3050.5, 917, 9840) },
    postconditions = { Condition.ChatText:new("You put the unfired bowl into the opening in the door.") },
  },
  {
    text = "Use the wizard's mind bomb on the 'Magic door'.",
    actions = { Action.ModelHighlight:new(wizardsMindBomb), Action.Direction:new(3050.5, 917, 9840) },
    postconditions = { Condition.ChatText:new("You pour the Wizard's Mind Bomb into the opening in the door.") },
  },
  {
    text = "Use the crayfish cage/lobster pot on the 'Magic door'.",
    actions = { Action.ModelHighlight:new(crayfishCage), Action.Direction:new(3050.5, 917, 9840) }, --add lobster pot later
    postconditions = { Condition.ChatText:new("You put the small crustacean cage into the opening in the door.") },
  },
  {
    text = "Use the silk on the 'Magic door'.",
    actions = { Action.ModelHighlight:new(Items["silk"]), Action.Direction:new(3050.5, 917, 9840) },
    postconditions = { Condition.ChatText:new("The door opens...") },
  },
  {
    text = "Open the chest in the room and search for the second map part after opening. You need to click again to search after the dialogue, it does not automatically loot or search.",
    actions = { Action.Direction:new(3057, 917, 9841) },
    postconditions = { Condition.InventoryContains:new(mapPieceTwo) },
  },
  {
    text = "Head to Port Sarim Jail and talk to Wormbrain in the cell.<ul><li>You may need to speak with the Guild Master at this point before you can attack Wormbrain.</li></ul>",
    title = "Third map part",
    neededItems = {
      ["Coins"] = { quantity = 10000 },
    },
    actions = { Action.Direction:new(3011, 1317, 3193) },
    postconditions = { Condition.DistanceTo:new(3011, 1317, 3193, 4) },
  },
  {
    text = "There are two ways to obtain the third map part:<ul><li>Talk to Wormbrain and pay him 10,000 coins for the map part.</li><li>Attack Wormbrain and use the loot interface or telekinetic grab to get the map part that he drops.</li></ul>",
    actions = {
      Action.ModelHighlight:new(wormbrain),
      Action.ModelHighlight:new(mapPieceThree),
      Action.ConversationHighlight:new("I believe you've got a piece of map that I need."),
      Action.ConversationHighlight:new("I suppose I could pay you for the map piece..."),
      Action.ConversationHighlight:new("Alright then, 10,000 it is."),
    },
    postconditions = { Condition.InventoryContains:new(mapPieceThree) },
  },
  {
    text = "Use a map part on any one of the other parts to form the complete Crandor map.",
    actions = {
      Action.InventoryHighlight:new(mapPieceOne),
      Action.InventoryHighlight:new(mapPieceTwo),
      Action.InventoryHighlight:new(mapPieceThree),
    },
    postconditions = { Condition.InventoryContains:new(map) },
  },
  {
    text = "Talk to Duke Horacio on the 1st floor (2nd floor [US]) of Lumbridge Castle.<ul><li>You can also buy an anti-dragon shield from the Grand Exchange to skip this step entirely.</li><li>If the dialogue option for the anti-dragon shield doesn't appear, you need to talk to the Guildmaster about protection from dragon fire</li></ul>",
    title = "Obtaining an anti-dragon shield",
    actions = { Action.Direction:new(3204, 1477, 3209.5) },
    postconditions = { Condition.DistanceTo:new(3213, 1477, 3218, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["lumbridge bottom stairs 0"]) },
    postconditions = { Condition.DistanceToWithHeight:new(3205, 2693, 3209, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["duke horacio"]),
      Action.ConversationHighlight:new("I seek a shield that will protect me from dragonbreath."),
      Action.ConversationHighlight:new("Elvarg, the dragon of Crandor island!"),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("So, are you going to give me the shield or not?"),
    },
    postconditions = { Condition.InventoryContains:new(antiDragonShield) },
  },
  {
    text = "In Port Sarim, buy the beautiful ship <i>Lady Lumbridge</i> from Klarense, by the Void knight Squire.",
    title = "Obtaining a ship",
    neededItems = {
      ["Coins"] = { quantity = 2000 },
      ["Crandor map"] = { quantity = 1, model = map },
      ["Steel nails"] = { quantity = 90 },
      ["Plank"] = { quantity = 3 },
    },
    actions = { Action.Direction:new(3047, 741, 3204) },
    postconditions = { Condition.DistanceTo:new(3047, 741, 3204, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(klarense),
      Action.ConversationHighlight:new("I'd like to buy her."),
      Action.ConversationHighlight:new("Yep, sounds good."),
    },
    postconditions = { Condition.ConversationText:new("Okey dokey, she's all yours!") },
  },
  {
    text = "Repair the damaged deck of the ship.",
    actions = { Action.ModelHighlight:new(deckHole) },
    postconditions = {
      Condition.ConversationText:new(
        "You nail a final plank over the hole. You have successfully patched the hole in the deck."
      ),
    },
  },
  {
    text = "Talk to Ned, in the house north-east of the bank in Draynor Village.",
    actions = { Action.Direction:new(3100, 1317, 3257) },
    postconditions = { Condition.DistanceTo:new(3100, 1317, 3257, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["ned"]),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("You're a sailor? Could you take me to Crandor?"),
    },
    postconditions = { Condition.ConversationText:new("Excellent! I'll meet you at the ship, then.") },
  },
  {
    text = "Bank to prepare the battle against Elvarg.<ul><li>Melee is recommended due to how hit chances are calculated.</li><li>Players with level 37 Prayer can use Protect from Magic.</li></ul>",
    title = "The fight",
    actions = { Action.Direction:new(3049, 901, 3209) },
    postconditions = { Condition.DistanceTo:new(3049, 901, 3209, 8) },
  },
  {
    text = "Board your ship and talk to Ned. Tell him you're ready to sail to Crandor Isle.",
    actions = {
      Action.ModelHighlight:new(NPCs["ned"]),
      Action.ConversationHighlight:new("Yes, let's go!"),
    },
    postconditions = { Condition.ConversationText:new("Yes, let's go!") },
  },
  {
    actions = {},
    postconditions = {
      Condition.ConversationText:new("You are knocked unconscious and later awake on an ash-strewn beach."),
      Condition.DistanceTo:new(2849, 53, 3237, 8),
    },
  },
  {
    text = "After crashing on the island, follow the light grey path around and up to the top of the mountain. Climb down the hole.",
    actions = { Action.Direction:new(2834, 3893, 3258) },
    postconditions = { Condition.ModelVisible:new(crandorHole) },
  },
  {
    actions = { Action.ModelHighlight:new(crandorHole) },
    postconditions = { Condition.DistanceToWithHeight:new(2833, 4405, 9658, 3) },
  },
  {
    text = "Before you climb over to Elvarg its advisable to open the shortcut in the wall south of the short wall.",
    actions = { Action.Direction:new(2836, 2741, 9599.5) },
    postconditions = { Condition.DistanceTo:new(2836, 2597, 9598, 1) },
  },
  {
    text = "Climb over the wall and kill Elvarg.<ul><li>Wear your dragonfire protection before hopping over, or your stats will get drained resulting most likely in a failed kill.</li><li>If you leave Crandor before killing Elvarg and opening the shortcut, you will need 3 planks and 90 steel nails to repair the ship to get back.</li></ul>",
    actions = { Action.Direction:new(2847, 3333, 9636) },
    postconditions = { Condition.DistanceTo:new(2849, 3333, 9636, 3) },
  },
  { postconditions = { Condition.InventoryContains:new(elvargsHead) } }, --Not highlighting elvarg in case if it distracts the user mid fight
  {
    text = "Take Elvarg's head to Oziach in Edgeville.",
    title = "Finishing up",
    actions = { Action.Direction:new(3068, 997, 3516) },
    postconditions = { Condition.DistanceTo:new(3068, 997, 3516, 2) },
  },
  { actions = { Action.ModelHighlight:new(oziach) }, postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Dragon Slayer",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.long,
  releaseDate = 1001203200,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.questpoints(32),
    Types.QuestReq.ironmanOnlySkill("Crafting", 8, true),
    Types.QuestReq.ironmanOnlySkill("Smithing", 20, true),
  },
  neededItems = {
    ["An unfired bowl"] = { quantity = 1, model = unfiredBowl },
    ["Wizard's mind bomb"] = { quantity = 1, model = wizardsMindBomb },
    ["Crayfish cage or lobster pot"] = { quantity = 1, model = crayfishCage },
    ["A piece of silk"] = { quantity = 1, model = Items["silk"] },
    ["Regular planks"] = { quantity = 3, model = Items["plank"] },
    ["Steel nails"] = { quantity = 90, model = Items["steel nails"] },
    ["An anti-dragon shield"] = { quantity = 1, model = antiDragonShield, duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = {},
})
