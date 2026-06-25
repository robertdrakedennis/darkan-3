local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--#region NPCs
local brynna = Model.new(8892, {
  [2598] = Vertex.new(-64, 828, -78, 217, 217, 217),
  [2600] = Vertex.new(-64, 828, -80, 217, 217, 217),
  [3087] = Vertex.new(-17, 824, 28, 217, 217, 217),
  [7437] = Vertex.new(-22, 840, -39, 217, 217, 217),
  [7470] = Vertex.new(-22, 838, -39, 217, 217, 217),
})
local masterChef = Model.new(9189, {
  [13] = Vertex.new(-15, 503, 114, 127, 127, 127),
  [25] = Vertex.new(17, 503, 114, 127, 127, 127),
  [2820] = Vertex.new(40, 798, 25, 127, 127, 127),
  [8578] = Vertex.new(17, 567, -85, 127, 127, 127),
  [8587] = Vertex.new(-15, 567, -85, 127, 127, 127),
})
local dezzick = Model.new(7278, {
  [6994] = Vertex.new(-21, 797, -32, 218, 218, 218),
  [6998] = Vertex.new(-27, 794, -29, 218, 218, 218),
  [7001] = Vertex.new(-27, 794, -29, 218, 218, 218),
  [7018] = Vertex.new(21, 797, -32, 218, 218, 218),
  [7023] = Vertex.new(27, 794, -29, 218, 218, 218),
})
local financialAdvisor = Model.new(8730, {
  [2384] = Vertex.new(-35, 797, 16, 218, 218, 218),
  [2451] = Vertex.new(-37, 790, 7, 218, 218, 218),
  [2757] = Vertex.new(32, 798, 9, 218, 218, 218),
  [2774] = Vertex.new(37, 790, 7, 218, 218, 218),
  [6268] = Vertex.new(-19, 796, -29, 218, 218, 218),
})
local brotherBrace = Model.new(7596, {
  [1607] = Vertex.new(-20, 796, -31, 218, 218, 218),
  [2538] = Vertex.new(20, 796, -31, 218, 218, 218),
  [5481] = Vertex.new(-22, 795, -26, 218, 218, 218),
  [5482] = Vertex.new(-20, 796, -31, 218, 218, 218),
  [6068] = Vertex.new(22, 795, -26, 218, 218, 218),
})
local terrova = Model.new(9036, {
  [2735] = Vertex.new(54, 812, -67, 167, 167, 167),
  [2738] = Vertex.new(87, 803, -48, 167, 167, 167),
  [2749] = Vertex.new(-3, 894, 47, 167, 167, 167),
  [2751] = Vertex.new(-25, 883, 36, 167, 167, 167),
  [2767] = Vertex.new(-36, 857, 46, 167, 167, 167),
})
local helmetChicken = Model.new(2214, {
  [454] = Vertex.new(-24, 88, 13, 178, 178, 178),
  [455] = Vertex.new(-34, 88, 13, 178, 178, 178),
  [456] = Vertex.new(-29, 98, 13, 178, 178, 178),
  [557] = Vertex.new(29, 98, 13, 178, 178, 178),
  [558] = Vertex.new(34, 88, 13, 178, 178, 178),
})
local hector = Model.new(4596, {
  [1526] = Vertex.new(-14, 777, 1, 182, 139, 116),
  [2201] = Vertex.new(-30, 613, -57, 66, 53, 42),
  [2205] = Vertex.new(-27, 634, -53, 66, 53, 42),
  [2213] = Vertex.new(30, 613, -57, 66, 53, 42),
  [2217] = Vertex.new(27, 634, -53, 66, 53, 42),
})
local crassian = Model.new(7524, {
  [951] = Vertex.new(-32, 531, -165, 254, 254, 254),
  [1041] = Vertex.new(32, 531, -165, 254, 254, 254),
  [1131] = Vertex.new(-44, 545, -148, 254, 254, 254),
  [1161] = Vertex.new(-44, 545, -148, 254, 254, 254),
  [1221] = Vertex.new(44, 545, -148, 254, 254, 254),
})
--#endregion
--#region Objects
local messageInABottle = Model.new(504, {
  [1] = Vertex.new(-38, 201, -38, 76, 109, 69, 0.7059),
  [28] = Vertex.new(-95, 152, -40, 76, 109, 69, 0.7059),
  [41] = Vertex.new(-95, 152, -40, 76, 109, 69, 0.7059),
  [43] = Vertex.new(-72, 149, -65, 76, 109, 69, 0.7059),
  [45] = Vertex.new(-95, 152, -40, 76, 109, 69, 0.7059),
})
local shrimp = Model.new(2484, {
  [217] = Vertex.new(-83, 406, 134, 127, 127, 127),
  [649] = Vertex.new(146, 432, -72, 127, 127, 127),
  [651] = Vertex.new(136, 432, -100, 127, 127, 127),
  [652] = Vertex.new(136, 432, -100, 127, 127, 127),
  [704] = Vertex.new(136, 432, -100, 127, 127, 127),
})
local seaweed = Model.new(5832, {
  [5545] = Vertex.new(57, 933, 96, 250, 220, 58),
  [5547] = Vertex.new(50, 931, 88, 250, 220, 58),
  [5552] = Vertex.new(57, 933, 96, 250, 220, 58),
  [5655] = Vertex.new(43, 931, 89, 250, 220, 58),
  [5657] = Vertex.new(50, 933, 97, 250, 220, 58),
})
local boulder = Model.new(1296, {
  [62] = Vertex.new(224, 64, -140, 78, 65, 49),
  [750] = Vertex.new(148, 238, -72, 106, 88, 67),
  [1211] = Vertex.new(215, 156, 100, 75, 62, 47),
  [1215] = Vertex.new(215, 156, 100, 75, 62, 47),
  [1248] = Vertex.new(215, 156, 100, 75, 62, 47),
})
local steamVent = Model.new(735, {
  [117] = Vertex.new(-169, 7, 154, 157, 157, 157),
  [186] = Vertex.new(-251, 7, -63, 157, 157, 157),
  [384] = Vertex.new(-190, 7, -184, 157, 157, 157),
  [471] = Vertex.new(157, 7, -184, 157, 157, 157),
  [526] = Vertex.new(-190, 7, -184, 157, 157, 157),
})
local tastyLookingSeaweed = Model.new(23328, {
  [1908] = Vertex.new(391, 297, 428, 161, 136, 117),
  [2280] = Vertex.new(505, 297, 353, 125, 96, 94),
  [7020] = Vertex.new(392, 300, 434, 161, 136, 117),
  [7296] = Vertex.new(504, 296, 350, 125, 96, 94),
  [7368] = Vertex.new(307, 301, 496, 67, 49, 48),
})
local preparationTable = Model.new(612, {
  [3] = Vertex.new(-456, 352, -184, 75, 63, 51),
  [5] = Vertex.new(-456, 352, -184, 75, 63, 51),
  [56] = Vertex.new(-456, 352, -184, 93, 78, 63),
  [57] = Vertex.new(-416, 352, -192, 93, 78, 63),
  [69] = Vertex.new(448, 352, -168, 93, 78, 63),
})
local openFurnace = Model.new(4470, {
  [2] = Vertex.new(353, 271, 232, 13, 14, 14),
  [384] = Vertex.new(-448, 632, -656, 14, 13, 13),
  [2610] = Vertex.new(460, 627, 280, 32, 33, 35),
  [2934] = Vertex.new(948, 252, -224, 76, 70, 69),
  [2946] = Vertex.new(948, 436, -224, 76, 70, 69),
})
local pump = Model.new(1506, {
  [945] = Vertex.new(223, 905, 211, 63, 52, 40),
  [951] = Vertex.new(194, 905, -177, 63, 52, 40),
  [969] = Vertex.new(-212, 872, 236, 76, 60, 48),
  [999] = Vertex.new(-208, 869, 211, 76, 60, 48),
  [1005] = Vertex.new(-238, 869, -177, 63, 52, 40),
})
local closedFurnace = Model.new(4470, {
  [432] = Vertex.new(-448, 632, -656, 14, 13, 13),
  [795] = Vertex.new(441, 252, 296, 76, 70, 69),
  [801] = Vertex.new(441, 436, 296, 76, 70, 69),
  [3078] = Vertex.new(460, 627, 280, 32, 33, 35),
  [3203] = Vertex.new(460, 627, -280, 32, 33, 35),
})
local deadSeaRat = Model.new(3147, {
  [2] = Vertex.new(21, 75, -346, 68, 68, 62),
  [12] = Vertex.new(-21, 75, -346, 68, 68, 62),
  [364] = Vertex.new(39, 94, -312, 127, 127, 116),
  [2907] = Vertex.new(6, 82, 892, 92, 76, 71),
  [2909] = Vertex.new(-6, 82, 892, 92, 76, 71),
})
local vault = Model.new(5814, {
  [5676] = Vertex.new(-768, 896, -256, 79, 71, 50),
  [5729] = Vertex.new(768, 896, -256, 79, 71, 50),
  [5763] = Vertex.new(498, 959, -256, 69, 62, 44),
  [5781] = Vertex.new(768, 0, -192, 69, 62, 44),
  [5811] = Vertex.new(-768, 0, -256, 69, 62, 44),
})
local coral = Model.new(4908, {
  [8] = Vertex.new(-193, 76, 322, 236, 40, 21),
  [975] = Vertex.new(-34, 745, 23, 254, 254, 254),
  [1050] = Vertex.new(-353, 261, -277, 254, 254, 254),
  [1344] = Vertex.new(-312, 354, -66, 254, 254, 254),
  [2421] = Vertex.new(84, 455, 191, 254, 254, 254),
})
local anchor = Model.new(8142, {
  [2883] = Vertex.new(-326, -23, -106, 254, 254, 254),
  [5691] = Vertex.new(275, 296, 320, 216, 211, 211),
  [7782] = Vertex.new(238, 71, 211, 53, 41, 39),
  [7794] = Vertex.new(69, 100, 271, 53, 41, 39),
  [8130] = Vertex.new(118, 92, 359, 53, 41, 39),
})
local cannon = Model.new(8142, {
  [2883] = Vertex.new(-326, -23, -106, 254, 254, 254),
  [5691] = Vertex.new(275, 296, 320, 216, 211, 211),
  [7782] = Vertex.new(238, 71, 211, 53, 41, 39),
  [7794] = Vertex.new(69, 100, 271, 53, 41, 39),
  [8130] = Vertex.new(118, 92, 359, 53, 41, 39),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local strongSeaweed = Model.new(276, {
  [1] = Vertex.new(-52, 10, -160, 8, 90, 62),
  [2] = Vertex.new(-36, 10, -208, 8, 90, 62),
  [3] = Vertex.new(-56, 10, -180, 8, 90, 62),
  [5] = Vertex.new(-40, 10, -164, 8, 90, 62),
  [12] = Vertex.new(-44, 10, -28, 8, 90, 62),
})
local soggyBranch = Model.new(219, {
  [1] = Vertex.new(-188, 20, -172, 68, 64, 43),
  [2] = Vertex.new(-156, -8, -188, 68, 64, 43),
  [3] = Vertex.new(-188, -8, -172, 68, 64, 43),
  [6] = Vertex.new(-156, 20, -188, 68, 64, 43),
  [7] = Vertex.new(-108, 16, -60, 58, 53, 30),
})
local rawShrimp = Model.new(579, {
  [1] = Vertex.new(4, 8, -12, 147, 124, 112),
  [2] = Vertex.new(-12, 0, 20, 147, 124, 112),
  [3] = Vertex.new(8, 0, -12, 147, 124, 112),
  [4] = Vertex.new(0, 0, -12, 147, 124, 112),
  [10] = Vertex.new(20, 0, 84, 147, 124, 112),
})
local boiledShrimp = Model.new(579, {
  [1] = Vertex.new(4, 8, -12, 153, 98, 13),
  [2] = Vertex.new(-12, 0, 20, 153, 98, 13),
  [3] = Vertex.new(8, 0, -12, 153, 98, 13),
  [4] = Vertex.new(0, 0, -12, 153, 98, 13),
  [10] = Vertex.new(20, 0, 84, 153, 98, 13),
})
local burnedShrimp = Model.new(579, {
  [1] = Vertex.new(4, 8, -12, 72, 66, 66),
  [2] = Vertex.new(-12, 0, 20, 72, 66, 66),
  [3] = Vertex.new(8, 0, -12, 72, 66, 66),
  [4] = Vertex.new(0, 0, -12, 72, 66, 66),
  [10] = Vertex.new(20, 0, 84, 72, 66, 66),
})
local levsMusicBox = Model.new(744, {
  [1] = Vertex.new(-112, 88, 80, 0, 0, 0),
  [2] = Vertex.new(-112, 102, 80, 0, 0, 0),
  [3] = Vertex.new(-112, 102, -74, 0, 0, 0),
  [9] = Vertex.new(112, 102, -74, 0, 0, 0),
  [12] = Vertex.new(112, 88, -74, 0, 0, 0),
})
local tastySeaweed = Model.new(276, {
  [1] = Vertex.new(-52, 10, -160, 8, 91, 63),
  [2] = Vertex.new(-36, 10, -208, 8, 91, 63),
  [3] = Vertex.new(-56, 10, -180, 8, 91, 63),
  [5] = Vertex.new(-40, 10, -164, 8, 91, 63),
  [12] = Vertex.new(-44, 10, -28, 8, 91, 63),
})
local bronzeButterKnife = Model.new(390, {
  [1] = Vertex.new(0, 13, -113, 65, 63, 60),
  [2] = Vertex.new(10, 0, -117, 65, 63, 60),
  [3] = Vertex.new(0, 10, -117, 65, 63, 60),
  [5] = Vertex.new(13, 0, -113, 65, 63, 60),
  [7] = Vertex.new(-13, 0, -113, 81, 77, 74),
})
local friendsList = Model.new(18, {
  [1] = Vertex.new(-55, 5, -109, 98, 88, 78),
  [2] = Vertex.new(89, 5, -128, 98, 88, 78),
  [3] = Vertex.new(-68, 5, -128, 98, 88, 78),
  [7] = Vertex.new(-76, 5, -73, 98, 88, 78),
  [9] = Vertex.new(-89, 5, -95, 98, 88, 78),
})
local prayerBook = Model.new(204, {
  [1] = Vertex.new(-56, 12, -72, 97, 89, 89),
  [2] = Vertex.new(56, 36, -72, 97, 89, 89),
  [3] = Vertex.new(56, 12, -72, 97, 89, 89),
  [5] = Vertex.new(-56, 36, -72, 97, 89, 89),
  [19] = Vertex.new(-64, 0, 84, 111, 110, 10),
})
local beamStruggle = Model.new(4134, {
  [2416] = Vertex.new(-6, 740, -50, 89, 39, 36),
  [2420] = Vertex.new(-26, 738, -37, 89, 39, 36),
  [2433] = Vertex.new(26, 738, -37, 89, 39, 36),
  [2781] = Vertex.new(-24, 736, -38, 31, 30, 29),
  [2793] = Vertex.new(24, 736, -38, 31, 30, 29),
})
--#endregion

---@type QuestStep[]
local steps = {
  --#region Back to the basics
  {
    text = "Speak to Wizard Myrtle at the fountain near the entrance to the Wizards' Tower. (Fairy ring DIS)<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered'.</li></ul>",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Draynor Village lodestone",
      url = "Draynor_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3094, 1869, 3185) },
    postconditions = { Condition.DistanceTo:new(3094, 1869, 3185, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["wizard myrtle"]),
      Action.ConversationHighlight:new("[Continue]"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with Wizard Myrtle.",
    actions = {
      Action.ModelHighlight:new(NPCs["wizard myrtle"]),
      Action.ConversationHighlight:new("I know Vannaka!"),
    },
    postconditions = { Condition.ConversationText:new("Ah, it would seem I found the right person for the job then!") },
  },
  {
    text = "Talk to Vannaka south of the Edgeville bank.",
    title = "Under the sea",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Edgeville lodestone",
      url = "Edgeville_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3091, 965, 3478) },
    postconditions = { Condition.ModelVisible:new(NPCs["vannaka"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["vannaka"]),
      Action.ConversationHighlight:new("Talk about Beneath Cursed Tides."),
      Action.ConversationHighlight:new("[Continue...]"),
      Action.ConversationHighlight:new("Yes, I do."),
    },
    postconditions = {
      Condition.ConversationText:new("Then I will accompany you"),
      Condition.ConversationText:new("We should both go and meet"),
    },
  },
  {
    text = "Return to the Wizards' Tower, and talk to Wizard Myrtle.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Draynor Village lodestone",
      url = "Draynor_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3094, 1869, 3185) },
    postconditions = { Condition.DistanceTo:new(3094, 1869, 3185, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["wizard myrtle"]),
      Action.ConversationHighlight:new("What's the plan then?"),
      Action.ConversationHighlight:new("How do I get to Mudskipper Point?"),
    },
    postconditions = {
      Condition.ConversationText:new("Anything else?"),
      Condition.ConversationText:new("Nope, I'm ready to go!"),
      -- fallback since this did not activate for me in my playthrough
      Condition.ModelNotVisible:new(NPCs["wizard myrtle"]),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["wizard myrtle"]),
      Action.ConversationHighlight:new("What's the plan then?"),
      Action.ConversationHighlight:new("Nope, I'm ready to go!"),
    },
    postconditions = { Condition.ConversationText:new("Nope, I'm ready to go!") },
  },
  {
    text = "Make your way to Mudskipper Point, south of the Port Sarim lodestone.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "AIQ",
    },
    actions = { Action.Direction:new(3000, 341, 3112) },
    postconditions = { Condition.DistanceTo:new(3000, 341, 3112, 10) },
  },
  {
    text = "Unequip your weapons and dismiss followers, then talk to Wizard Myrtle.",
    actions = {
      Action.ModelHighlight:new(NPCs["wizard myrtle"]),
      Action.ConversationHighlight:new("Sounds perfect, let's do this!"),
      Action.ConversationHighlight:new("I'd like to dive to the sunken island."),
      Action.ConversationHighlight:new("Yes, I'm ready."),
    },
    postconditions = {
      Condition.ConversationText:new("Ok, you too Vannaka."),
    },
  },
  {
    text = "East of the room, read the message in the bottle for the key and open the door.",
    actions = { Action.ModelHighlight:new(messageInABottle) },
    postconditions = {
      Condition.ConversationText:new("Maybe some of the islanders are still alive!"),
    },
  },
  {
    -- text = "debug",
    actions = { Action.Direction:new(5209.5, 5157, 803) },
    postconditions = {
      Condition.DistanceTo:new(5212, 5157, 802, 2),
      Condition.ConversationText:new("You open the door with the key you found."),
    },
  },
  --#endregion
  --#region Anglerfish
  {
    text = "Talk to Brynna, the survival expert (directly to the south).",
    title = "Anglerfish",
    actions = { Action.ModelHighlight:new(brynna) },
    postconditions = {
      Condition.ConversationText:new("Hmm, well I guess if I'm to break the curse I'll figure it out."),
    },
  },
  {
    text = "Catch two raw shrimps.",
    actions = { Action.ModelHighlight:new(shrimp) },
    postconditions = { Condition.InventoryContains:new(rawShrimp, 2) },
  },
  {
    text = "Gather the seaweed nearby.",
    actions = { Action.ModelHighlight:new(seaweed) },
    postconditions = { Condition.InventoryContains:new(strongSeaweed) },
  },
  {
    text = "Cut a tree to get a soggy branch.",
    actions = { Action.Direction:new(5211.6, 4277, 791.5) },
    postconditions = { Condition.InventoryContains:new(soggyBranch) },
  },
  {
    text = "Interact with the boulder.",
    actions = { Action.ModelHighlight:new(boulder) },
    postconditions = { Condition.ChatText:new("The nearby steam vent roars into life as the pressure is increased.") },
  },
  {
    text = "Cook both shrimp on the steam vent.",
    actions = { Action.InventoryHighlight:new(rawShrimp), Action.ModelHighlight:new(steamVent) },
    postconditions = { Condition.InventoryContains:new(boiledShrimp, 2) },
  },
  {
    text = "Boil only <b>1</b> shrimp again to burn it.",
    actions = { Action.InventoryHighlight:new(boiledShrimp), Action.ModelHighlight:new(steamVent) },
    postconditions = { Condition.InventoryContains:new(burnedShrimp) },
  },
  {
    text = "Talk to Brynna twice.",
    actions = { Action.ModelHighlight:new(brynna) },
    postconditions = { Condition.ConversationText:new("I wish you the best of luck.") },
  },
  {
    text = "Continue through the west gate.",
    actions = { Action.Direction:new(5201.5, 4261, 788) },
    postconditions = { Condition.DistanceTo:new(5199, 4149, 786, 2) },
  },
  --#endregion
  --#region Jellyfish
  {
    text = "Go to the building to the west and talk to Lev, the master chef.",
    title = "Jellyfish",
    actions = { Action.Direction:new(5188, 2629, 781) },
    postconditions = { Condition.DistanceTo:new(5187, 2629, 781, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(masterChef),
    },
    postconditions = { Condition.ConversationText:new("*sigh* Fine.") },
  },
  {
    text = "Go to the large jellyfish south-east of the chef's house and play the music box.",
    actions = {
      Action.Direction:new(5192, 2685, 772),
      Action.InventoryHighlight:new(levsMusicBox),
      Action.ConversationHighlight:new("Turn second handle."),
    },
    postconditions = {
      Condition.ConversationText:new("PLAY ME MORE BEAUTIFUL SOUNDS!"),
    },
  },
  {
    actions = {
      Action.Direction:new(5192, 2685, 772),
      Action.ConversationHighlight:new("Turn third handle."),
    },
    postconditions = {
      Condition.ConversationText:new("DON'T KEEP ME WAITING!"),
    },
  },
  {
    actions = {
      Action.Direction:new(5192, 2685, 772),
      Action.ConversationHighlight:new("Turn first handle."),
    },
    postconditions = { Condition.ConversationText:new("Hmm? Oh sure, go ahead.") },
  },
  {
    text = "Gather tasty seaweed.",
    actions = { Action.ModelHighlight:new(tastyLookingSeaweed) },
    postconditions = { Condition.InventoryContains:new(tastySeaweed) },
  },
  {
    text = "Talk to the master chef again.",
    actions = { Action.Direction:new(5188, 2629, 781) },
    postconditions = { Condition.DistanceTo:new(5187, 2629, 781, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(masterChef),
    },
    postconditions = {
      Condition.ConversationText:new("Fantastic, go place the seaweed on the table over there and we can get started."),
    },
  },
  {
    text = "Click <i>prepare</i> on the large table.",
    actions = { Action.ModelHighlight:new(preparationTable) },
    postconditions = {
      Condition.ConversationText:new("You roll the seaweed out on the table."),
      Condition.ConversationText:new("Great, now we have the seaweed I am going to give you"),
    },
  },
  {
    text = "First - <b>1 up, 2 up, 3 up</b> (Sweet, Sour, Spicy).",
    actions = {},
    postconditions = { Condition.ConversationText:new("mellow yet sour with some bitter undertones") },
  },
  {
    text = "Second - <b>2 down, 2 up, 3 down</b> (Mellow, Sour, Bitter).",
    actions = {},
    postconditions = { Condition.ConversationText:new("something spicy with a salty bitterness") },
  },
  {
    text = "Third - <b>3 up, 1 down, 3 down</b> (Spicy, Salty, Bitter).",
    actions = {},
    postconditions = { Condition.ConversationText:new("Fantastic, that's exactly what I was looking for!") },
  },
  {
    text = "Continue through the west door to the small house in the north.",
    actions = { Action.Direction:new(5184.5, 2629, 786) },
    postconditions = { Condition.DistanceTo:new(5182, 2637, 786, 2) },
  },
  --#endregion
  --#region Rockfish
  {
    text = "Climb down the ladder.",
    title = "Rockfish",
    actions = { Action.Direction:new(5198, 2717, 821.5) },
    postconditions = { Condition.DistanceTo:new(5198, 2725, 819, 2) },
  },
  {
    actions = { Action.Direction:new(5200, 2725, 815) },
    postconditions = { Condition.DistanceTo:new(5464, 2893, 743, 3) },
  },
  {
    text = "Talk to Dezzick, the mining instructor.",
    actions = {
      Action.ModelHighlight:new(dezzick),
      Action.ConversationHighlight:new("Absolutely!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "I need you to make a bronze dagger. You'll need to mine some tin and copper ore, then use the furnace to smelt it."
      ),
    },
  },
  {
    text = "Mine soggy copper ore and soggy tin ore.",
    actions = { Action.ModelHighlight:new(Objects["copper rock"]) },
    postconditions = { Condition.InventoryContains:new(Items["copper ore"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["tin rock"]) },
    postconditions = { Condition.InventoryContains:new(Items["tin ore"]) },
  },
  {
    text = "Add the ores to furnace.",
    actions = { Action.ModelHighlight:new(openFurnace), Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ChatText:new("You add your ores to the furnace, but it doesn't heat them.") },
  },
  {
    text = "Right click and close the doors.",
    actions = { Action.ModelHighlight:new(openFurnace) },
    postconditions = { Condition.ChatText:new("You close the furnace doors.") },
  },
  {
    text = "Operate the pump and light the furnace.",
    actions = { Action.ModelHighlight:new(pump) },
    postconditions = { Condition.ChatText:new("You pump the water out of the furnace.") },
  },
  {
    text = "Retrieve the bronzish bar from the furnace.",
    actions = { Action.ModelHighlight:new(closedFurnace) },
    postconditions = { Condition.InventoryContains:new(Items["bronze bar"]) },
  },
  {
    text = "Smith a bronze butter knife.",
    actions = {
      Action.Direction:new(5459, 1293, 722),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InventoryContains:new(bronzeButterKnife) },
  },
  {
    text = "Talk to the mining instructor again.",
    actions = {
      Action.ModelHighlight:new(dezzick),
      Action.ConversationHighlight:new("Absolutely!"),
    },
    postconditions = { Condition.ConversationText:new("You should seek out your next tutor.") },
  },
  {
    text = "Continue through the east gate.",
    actions = { Action.Direction:new(5470.5, 1189, 725.5) },
    postconditions = { Condition.DistanceTo:new(5473, 1189, 726, 2) },
  },
  --#endregion
  --#region Swordfish
  {
    text = "Talk to Vannaka, the combat instructor.",
    title = "Swordfish",
    actions = {
      Action.ModelHighlight:new(NPCs["vannaka"]),
      Action.ConversationHighlight:new("Sure, I'm up for a challenge."),
      Action.ConversationHighlight:new("Let's get on with the task."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Don the blind fold I've wrapped around the hilt. Use only your senses to dispatch every giant sea rat that comes at you."
      ),
    },
  },
  {
    text = "Open the gate and click on the sword in the middle of the room to begin combat training.",
    actions = { Action.Direction:new(5486.5, 837, 741.5) },
    postconditions = { Condition.DistanceTo:new(5484, 837, 741, 2) },
  },
  {
    actions = { Action.Direction:new(5479, 5, 741) },
    postconditions = {
      Condition.ConversationText:new("You close your eyes and begin to listen to your deepest senses."),
    },
  },
  {
    text = "Click on the rats and other elements bouncing on the screen.",
    postconditions = { Condition.ModelVisible:new(deadSeaRat) },
  },
  {
    text = "Exit the cage and climb the ladder to the north-east.",
    actions = { Action.Direction:new(5486.5, 837, 741.5) },
    postconditions = { Condition.DistanceTo:new(5489, 933, 743, 2) },
  },
  {
    actions = { Action.Direction:new(5487, 1645, 749) },
    postconditions = { Condition.DistanceToWithHeight:new(5223, 2565, 821, 3) },
  },
  --#endregion
  --#region Goldfish
  {
    text = "Talk to the financial advisor in the house directly to the east.",
    title = "Goldfish",
    actions = { Action.Direction:new(5233.5, 3173, 817.5) },
    postconditions = { Condition.DistanceTo:new(5233, 3173, 817, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(financialAdvisor),
      Action.ConversationHighlight:new("Can we skip to the task?"),
    },
    postconditions = {
      Condition.ConversationText:new("I'm sure if you rifle through Jed's notes you'll find some clues."),
      Condition.ConversationText:new("I'm sure there are clues to the code littered around the bank booth."),
    },
  },
  {
    text = "Enter the vault (the code is 139).",
    actions = { Action.ModelHighlight:new(vault) },
    postconditions = { Condition.ChatText:new("The vault door makes a clicking sound and swings open.") },
  },
  {
    text = "Talk to the financial advisor again.",
    actions = {
      Action.ModelHighlight:new(financialAdvisor),
    },
    postconditions = {
      Condition.ConversationText:new("Go and seek the next tutor. Take the east door out of the bank."),
    },
  },
  {
    text = "Leave through the 2 doors to the east.",
    actions = { Action.Direction:new(5236.5, 3173, 820) },
    postconditions = { Condition.DistanceTo:new(5239, 3173, 820, 2) },
  },
  {
    actions = { Action.Direction:new(5241.5, 3173, 820) },
    postconditions = { Condition.DistanceTo:new(5244, 3173, 820, 2) },
  },
  --#endregion
  --#region Monkfish
  {
    text = "Head south to the church through double doors.",
    title = "Monkfish",
    actions = { Action.Direction:new(5240.5, 2629, 802.5) },
    postconditions = { Condition.DistanceTo:new(5238, 2629, 803, 2) },
  },
  {
    text = "Talk to Brother Brace.",
    actions = { Action.ModelHighlight:new(brotherBrace) },
    postconditions = {
      Condition.ConversationText:new(
        "Lovely, I held mass not too long ago so they shouldn't have strayed too far from the chapel."
      ),
    },
  },
  {
    text = "Backtrack to talk the fish on the friends list.",
    actions = { Action.InventoryHighlight:new(friendsList) },
    postconditions = {
      Condition.ConversationText:new("Erm, I think I saw something"),
      Condition.ConversationText:new("anchor east of the bank"),
      Condition.ConversationText:new("the cannon to the west of the ladder to the caves"),
      Condition.ConversationText:new("the coral east of the church"),
    },
  },
  {
    actions = {
      Action.Direction:new(5248, 2821, 804),
      Action.ModelHighlight:new(coral),
    },
    postconditions = {
      Condition.ConversationText:new("anchor east of the bank"),
      Condition.ConversationText:new("the cannon to the west of the ladder to the caves"),
      Condition.InventoryContains:new(prayerBook),
    },
  },
  {
    actions = {
      Action.Direction:new(5244.5, 2661, 826),
      Action.ModelHighlight:new(anchor),
    },
    postconditions = {
      Condition.ConversationText:new("the coral east of the church"),
      Condition.ConversationText:new("the cannon to the west of the ladder to the caves"),
      Condition.InventoryContains:new(prayerBook),
    },
  },
  {
    actions = {
      Action.Direction:new(5207.5, 3237, 819.5),
      Action.ModelHighlight:new(cannon),
    },
    postconditions = {
      Condition.ConversationText:new("the coral east of the church"),
      Condition.ConversationText:new("around the anchor east of the bank"),
      Condition.InventoryContains:new(prayerBook),
    },
  },
  -- Removed blank step impeding progression.
  {
    text = "Give the book to Brother Brace.",
    actions = { Action.Direction:new(5240.5, 2629, 802.5) },
    postconditions = { Condition.DistanceTo:new(5238, 2629, 803, 2) },
  },
  {
    actions = { Action.ModelHighlight:new(brotherBrace) },
    postconditions = {
      Condition.ConversationText:new("Go with faith, my friend. You are walking a righteous path!"),
    },
  },
  {
    text = "Go through the south door to the south-east building and talk to Wizard Terrova.",
    actions = { Action.Direction:new(5234, 2629, 798.5) },
    postconditions = { Condition.DistanceTo:new(5234, 2629, 796, 2) },
  },
  --#endregion
  --#region Moonfish
  {
    text = "Attack a chicken, then talk to the wizard.",
    title = "Moonfish",
    actions = { Action.Direction:new(5253, 3141, 785) },
    postconditions = { Condition.DistanceTo:new(5254, 3141, 783, 2) },
  },
  {
    actions = { Action.ModelHighlight:new(helmetChicken), Action.ModelHighlight:new(terrova) },
    postconditions = {
      Condition.ConversationText:new(
        "Welcome traveller. I can't help but notice that you let yourself into my house and bubbled my chickens uninvited."
      ),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(terrova),
    },
    postconditions = {
      Condition.ConversationText:new("How dare you try to break my curse. Come out here where I can see you!"),
    },
  },
  --#endregion
  --#region Commotion in the ocean
  {
    text = "Pick any option during the cutscene.",
    title = "Commotion in the ocean",
    actions = {
      Action.ConversationHighlight:new("I'm the world guardian."),
      Action.ConversationHighlight:new("I'm an adventurer."),
      Action.ConversationHighlight:new("I'm your worst nightmare!"),
      Action.ConversationHighlight:new("You don't deserve an introduction."),
    },
    postconditions = {
      Condition.ConversationText:new("Keep his minions off me and lend me your power when you're able!"),
    },
  },
  {
    text = "Channel the spell while repelling Crassians.",
    actions = { Action.ModelHighlight:new(crassian), Action.ModelHighlight:new(beamStruggle) },
    postconditions = { Condition.ConversationText:new("You must put a stop to him") },
  },
  {
    text = "Finish Hector Vivian.",
    actions = { Action.ModelHighlight:new(hector) },
    postconditions = {
      Condition.ConversationText:new("Bloop."),
      Condition.ConversationText:new("Arrrrgh curse you!"),
    },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("I'll round up the other tutors, we'll be right with you!") },
  },
  {
    text = "Talk to Wizard Myrtle.",
    actions = {
      Action.ModelHighlight:new(NPCs["wizard myrtle"]),
      Action.ConversationHighlight:new("We did it!"),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
  --#endregion
}

return Quest:new({
  name = "Beneath Cursed Tides",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1450051200,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Attack", 30),
    Types.QuestReq.skill("Cooking", 30),
    Types.QuestReq.skill("Firemaking", 30),
    Types.QuestReq.skill("Magic", 30),
    Types.QuestReq.skill("Mining", 30),
    Types.QuestReq.skill("Smithing", 30),
    Types.QuestReq.skill("Strength", 30),
    Types.QuestReq.skill("Woodcutting", 30),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
