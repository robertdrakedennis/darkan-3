local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local fycie = Model.new(2886, {
  [630] = Vertex.new(-104, 268, -32, 201, 194, 194),
  [1497] = Vertex.new(140, 244, -52, 157, 137, 82),
  [1553] = Vertex.new(-120, 232, -68, 157, 137, 82),
  [1574] = Vertex.new(-164, 300, 92, 165, 144, 86),
  [1596] = Vertex.new(164, 300, 92, 165, 144, 86),
})
local wolves = Model.new(6276, {
  [924] = Vertex.new(129, 428, -138, 57, 58, 53),
  [1848] = Vertex.new(-129, 428, -138, 57, 58, 53),
  [5211] = Vertex.new(110, 380, -154, 57, 58, 53),
  [6234] = Vertex.new(131, 430, -138, 114, 115, 106),
  [6243] = Vertex.new(-131, 430, -138, 114, 115, 106),
})
local swampToad = Model.new(477, {
  [74] = Vertex.new(68, 0, -80, 75, 95, 29),
  [157] = Vertex.new(-20, 0, -80, 75, 95, 29),
  [216] = Vertex.new(48, 0, -80, 75, 95, 29),
  [330] = Vertex.new(-128, 0, 56, 75, 95, 29),
  [431] = Vertex.new(12, 0, -80, 75, 95, 29),
})
local chompyBird = Model.new(1512, {
  [404] = Vertex.new(0, 280, -200, 99, 81, 31),
  [425] = Vertex.new(-20, 296, -184, 99, 81, 31),
  [1474] = Vertex.new(20, 296, -184, 99, 81, 31),
  [1476] = Vertex.new(0, 292, -188, 99, 81, 31),
  [1479] = Vertex.new(20, 296, -184, 99, 81, 31),
})
local bugs = Model.new(2583, {
  [1] = Vertex.new(4, 372, -8, 152, 170, 187),
  [3] = Vertex.new(-8, 372, 4, 152, 170, 187),
  [5] = Vertex.new(100, 268, -32, 201, 194, 193),
  [8] = Vertex.new(-88, 268, -32, 201, 194, 193),
  [9] = Vertex.new(-104, 268, -32, 201, 194, 193),
})
--#endregion
--#region Objects
local acheyTree = Model.new(2808, {
  [22] = Vertex.new(632, 1489, 1040, 79, 82, 43),
  [23] = Vertex.new(515, 1431, 1073, 79, 82, 43),
  [24] = Vertex.new(638, 1491, 1017, 79, 82, 43),
  [25] = Vertex.new(621, 1505, 972, 79, 82, 43),
  [26] = Vertex.new(629, 1504, 972, 79, 82, 43),
})
local lockedOgreChest = Model.new(1254, {
  [3] = Vertex.new(-224, 216, 160, 75, 62, 39),
  [5] = Vertex.new(224, 216, -160, 75, 62, 39),
  [654] = Vertex.new(-248, 216, -192, 96, 81, 56),
  [1120] = Vertex.new(352, 320, 120, 119, 88, 25),
  [1126] = Vertex.new(-356, 320, 80, 119, 88, 25),
})
local unlockedOgreChest = Model.new(1332, {
  [477] = Vertex.new(-248, 216, -192, 96, 81, 56),
  [891] = Vertex.new(-248, 216, -192, 107, 90, 60),
  [1029] = Vertex.new(-232, 704, -32, 119, 88, 25),
  [1135] = Vertex.new(-456, 500, 172, 127, 101, 52),
  [1264] = Vertex.new(-500, 376, -140, 127, 101, 52),
})
local ogreSpitRoast = Model.new(2157, {
  [3] = Vertex.new(432, 36, -160, 59, 43, 5),
  [117] = Vertex.new(356, 37, -208, 59, 43, 5),
  [173] = Vertex.new(-496, 20, 76, 59, 43, 5),
  [218] = Vertex.new(-424, 15, 204, 59, 43, 5),
  [225] = Vertex.new(-424, 15, 204, 59, 43, 5),
})
--#endregion
--#region Items
local acheyLogs = Model.new(594, {
  [296] = Vertex.new(184, 148, 12, 83, 75, 53),
  [303] = Vertex.new(184, 148, 12, 83, 75, 53),
  [315] = Vertex.new(-172, 156, -16, 83, 75, 53),
  [320] = Vertex.new(-172, 156, -16, 83, 75, 53),
  [534] = Vertex.new(184, 148, 12, 68, 56, 21),
})
local ogreArrowShaft = Model.new(270, {
  [80] = Vertex.new(-80, 0, -200, 75, 65, 39),
  [81] = Vertex.new(-88, 0, -200, 75, 65, 39),
  [84] = Vertex.new(-80, 0, -200, 75, 65, 39),
  [265] = Vertex.new(-88, 0, -200, 75, 65, 39),
  [268] = Vertex.new(-88, 0, -200, 75, 65, 39),
})
local flightedOgreArrow = Model.new(510, {
  [9] = Vertex.new(-68, 0, -212, 164, 151, 150),
  [17] = Vertex.new(-68, 0, -212, 164, 151, 150),
  [41] = Vertex.new(-100, 0, -172, 164, 151, 150),
  [207] = Vertex.new(44, 0, -236, 164, 151, 150),
  [215] = Vertex.new(44, 0, -236, 164, 151, 150),
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
local wolfBoneArrowheads = Model.new(270, {
  [66] = Vertex.new(-84, -4, -64, 0, 0, 0),
  [70] = Vertex.new(-84, -4, -64, 0, 0, 0),
  [165] = Vertex.new(-40, 0, -48, 155, 154, 119),
  [201] = Vertex.new(-80, 0, -60, 155, 154, 119),
  [219] = Vertex.new(28, 0, -84, 155, 154, 119),
})
local ogreArrow = Model.new(537, {
  [68] = Vertex.new(-88, 0, -200, 164, 151, 150),
  [140] = Vertex.new(20, 0, -224, 164, 151, 150),
  [212] = Vertex.new(48, 0, -168, 164, 151, 150),
  [284] = Vertex.new(-12, 0, -148, 164, 151, 150),
  [356] = Vertex.new(-48, 0, -188, 164, 151, 150),
})
local ogreBellows = Model.new(498, {
  [1] = Vertex.new(-96, 4, -144, 113, 98, 73),
  [2] = Vertex.new(-92, 0, -148, 113, 98, 73),
  [7] = Vertex.new(-96, 0, -144, 0, 0, 0),
  [13] = Vertex.new(-20, 84, 44, 107, 88, 56),
  [14] = Vertex.new(16, 72, 44, 107, 88, 56),
  [145] = Vertex.new(-68, 24, -16, 108, 94, 69),
  [146] = Vertex.new(-68, 20, -48, 108, 94, 69),
  [147] = Vertex.new(-72, 20, -20, 108, 94, 69),
  [150] = Vertex.new(-60, 20, 32, 108, 94, 69),
  [153] = Vertex.new(-56, 24, 32, 108, 94, 69),
  [156] = Vertex.new(-12, 24, 64, 108, 94, 69),
  [159] = Vertex.new(-8, 24, 52, 108, 94, 69),
  [162] = Vertex.new(24, 20, 56, 108, 94, 69),
  [165] = Vertex.new(20, 28, 44, 108, 94, 69),
  [166] = Vertex.new(44, 20, 40, 108, 94, 69),
  [295] = Vertex.new(-24, 44, -80, 75, 62, 39),
  [296] = Vertex.new(44, 68, -60, 75, 62, 39),
  [452] = Vertex.new(-68, 16, -120, 98, 90, 90),
  [475] = Vertex.new(-84, 16, -112, 93, 74, 39),
  [476] = Vertex.new(-60, 20, -112, 93, 74, 39),
})
local filledOgreBellows = Model.new(498, {
  [1] = Vertex.new(-96, 4, -144, 113, 98, 73),
  [2] = Vertex.new(-92, 0, -148, 113, 98, 73),
  [7] = Vertex.new(-96, 0, -144, 0, 0, 0),
  [13] = Vertex.new(-20, 84, 44, 107, 88, 56),
  [14] = Vertex.new(16, 72, 44, 107, 88, 56),
  [145] = Vertex.new(-68, 24, -16, 50, 93, 39),
  [146] = Vertex.new(-68, 20, -48, 50, 93, 39),
  [147] = Vertex.new(-72, 20, -20, 50, 93, 39),
  [150] = Vertex.new(-60, 20, 32, 50, 93, 39),
  [153] = Vertex.new(-56, 24, 32, 50, 93, 39),
  [156] = Vertex.new(-12, 24, 64, 50, 93, 39),
  [159] = Vertex.new(-8, 24, 52, 50, 93, 39),
  [162] = Vertex.new(24, 20, 56, 50, 93, 39),
  [165] = Vertex.new(20, 28, 44, 50, 93, 39),
  [166] = Vertex.new(44, 20, 40, 50, 93, 39),
  [295] = Vertex.new(-24, 44, -80, 75, 62, 39),
  [296] = Vertex.new(44, 68, -60, 75, 62, 39),
  [452] = Vertex.new(-68, 16, -120, 98, 90, 90),
  [475] = Vertex.new(-84, 16, -112, 93, 74, 39),
  [476] = Vertex.new(-60, 20, -112, 93, 74, 39),
})
local bloatedToad = Model.new(477, {
  [74] = Vertex.new(68, 0, -80, 75, 95, 29),
  [157] = Vertex.new(-20, 0, -80, 75, 95, 29),
  [168] = Vertex.new(-128, 0, 56, 75, 95, 29),
  [216] = Vertex.new(48, 0, -80, 75, 95, 29),
  [431] = Vertex.new(12, 0, -80, 75, 95, 29),
})
local ogreBow = Model.new(510, {
  [39] = Vertex.new(64, 8, -136, 109, 91, 57),
  [53] = Vertex.new(92, 0, -164, 109, 91, 57),
  [243] = Vertex.new(-232, 16, -252, 136, 121, 105),
  [504] = Vertex.new(64, 12, -136, 109, 91, 57),
  [510] = Vertex.new(-64, 12, -208, 109, 91, 57),
})
--#endregion
--#region Quest Items
local cookedChompyBird = Model.new(462, {
  [1] = Vertex.new(100, 112, 24, 107, 101, 98),
  [2] = Vertex.new(92, 120, 20, 107, 101, 98),
  [3] = Vertex.new(92, 120, 28, 107, 101, 98),
  [5] = Vertex.new(100, 112, 20, 107, 101, 98),
  [7] = Vertex.new(100, 112, -20, 107, 101, 98),
  [8] = Vertex.new(92, 120, -28, 107, 101, 98),
  [9] = Vertex.new(92, 120, -20, 107, 101, 98),
  [11] = Vertex.new(100, 112, -24, 107, 101, 98),
  [15] = Vertex.new(68, 100, 36, 107, 101, 98),
  [21] = Vertex.new(80, 96, 36, 107, 101, 98),
  [73] = Vertex.new(32, 52, 40, 104, 67, 9),
  [74] = Vertex.new(-32, 52, 40, 104, 67, 9),
  [75] = Vertex.new(-44, 32, 60, 104, 67, 9),
  [76] = Vertex.new(88, 96, 20, 104, 67, 9),
  [77] = Vertex.new(68, 104, 24, 104, 67, 9),
  [78] = Vertex.new(80, 96, 24, 104, 67, 9),
  [80] = Vertex.new(-68, 0, 36, 104, 67, 9),
  [81] = Vertex.new(-44, 0, 60, 104, 67, 9),
  [83] = Vertex.new(-88, 32, 40, 104, 67, 9),
  [85] = Vertex.new(44, 32, -56, 104, 67, 9),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Rantz east of Feldip Hills.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "AKS",
    },
    actions = { Action.Direction:new(2630, 989, 2982) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["rantz"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("How do I make the 'stabbers'?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["rantz"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Rantz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("Ok, I'll make you some 'stabbers'."),
    },
    postconditions = { Condition.ConversationText:new("from dog bones") },
  },
  {
    text = "Chop down achey trees for 6 achey logs.",
    title = "Fletching some arrows",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(acheyTree) },
    postconditions = { Condition.InventoryContains:new(acheyLogs, 6) },
  },
  {
    text = "Craft ogre arrow shafts from the logs.",
    actions = { Action.InventoryHighlight:new(acheyLogs) },
    postconditions = { Condition.InventoryContains:new(ogreArrowShaft, 24) },
  },
  {
    text = "Enter Rantz's cave to the north.",
    actions = { Action.ModelHighlight:new(Models.objects["rantz cave entrance"]) },
    postconditions = { Condition.DistanceTo:new(2647, 1133, 9378, 4) },
    jumpconditions = { Condition.InventoryContains:new(Models.items["feather"], 25) },
    jumpOffset = 3,
  },
  {
    text = "Buy 25 feathers from Fycie.",
    actions = {
      Action.ModelHighlight:new(fycie),
      Action.ConversationHighlight:new("Ok, I'll give you 50 bright pretties."),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["feather"], 25) },
  },
  {
    text = "Exit the cave.",
    actions = { Action.Direction:new(2646, 1849, 9376) },
    postconditions = { Condition.DistanceTo:new(2629, 1453, 2997, 4) },
  },
  {
    text = "Use feathers on the ogre arrow shafts.",
    actions = {
      Action.InventoryHighlight:new(Models.items["feather"]),
      Action.InventoryHighlight:new(ogreArrowShaft),
    },
    postconditions = { Condition.InventoryContains:new(flightedOgreArrow, 24) },
  },
  {
    text = "Kill 6 wolves south-west of Rantz for their bones.",
    actions = { Action.ModelHighlight:new(wolves, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(wolfBones, 6) },
  },
  {
    text = "Craft wolf bone arrowheads from the wolf bones. ",
    warning = "Be careful not to bury the bones.",
    actions = { Action.InventoryHighlight:new(wolfBones, true) },
    postconditions = { Condition.InventoryContains:new(wolfBoneArrowheads, 24) },
  },
  {
    text = "Use the wolf bone arrowheads on the shafts.",
    actions = {
      Action.InventoryHighlight:new(wolfBoneArrowheads),
      Action.InventoryHighlight:new(flightedOgreArrow),
    },
    postconditions = { Condition.InventoryContains:new(ogreArrow, 24) },
  },
  {
    text = "Use the ogre arrows on Rantz.",
    actions = { Action.Direction:new(2630, 989, 2982) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["rantz"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.InventoryHighlight:new(ogreArrow),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["rantz"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Now us can stick der") },
  },
  {
    text = "Ask Rantz all possible questions.",
    title = "Bloated toads",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("How do we make the chompys come?"),
    },
    postconditions = { Condition.ConversationText:new("times making fatsy toadies") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("What are 'fatsy toadies'?"),
    },
    postconditions = { Condition.ConversationText:new("big and round") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("Where do we put the fatsy toadies?"),
    },
    postconditions = { Condition.ConversationText:new("no tree's place") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("What do you mean 'sneaky..sneaky, stick da chompy?'"),
    },
    postconditions = { Condition.ConversationText:new("can eat da chompy") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("Ok, thanks."),
    },
    postconditions = { Condition.ConversationText:new("Ok, thanks") },
  },
  {
    text = "Enter Rantz's cave to the north.",
    actions = { Action.ModelHighlight:new(Models.objects["rantz cave entrance"]) },
    postconditions = { Condition.DistanceTo:new(2647, 1133, 9378, 4) },
  },
  {
    text = "'Unlock' the locked ogre chest.",
    actions = { Action.ModelHighlight:new(lockedOgreChest) },
    postconditions = {
      Condition.ChatText:new("rock off the chest"),
      Condition.ModelVisible:new(unlockedOgreChest),
    },
  },
  {
    text = "Search the chest.",
    actions = { Action.ModelHighlight:new(unlockedOgreChest) },
    postconditions = { Condition.InventoryContains:new(ogreBellows) },
  },
  {
    text = "Exit the cave.",
    actions = { Action.Direction:new(2646, 1849, 9376) },
    postconditions = { Condition.DistanceTo:new(2629, 1453, 2997, 4) },
  },
  {
    text = "Fill the bellows.",
    actions = { Action.Direction:new(2601, 45, 2967, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(filledOgreBellows) },
  },
  {
    text = "Bloat 3 nearby swamp toads.",
    actions = { Action.ModelHighlight:new(swampToad, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(bloatedToad, 3) },
  },
  {
    text = "Talk to Rantz.",
    title = "Hunting",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2630, 989, 2982) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["rantz"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["rantz"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["rantz"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("clearing to the south") },
  },
  {
    text = "Drop a toad on the highlighted tile.<ul><li>Drop more if a chompy bird doesn't appear.</li></ul>",
    actions = {
      Action.Direction:new(2636, 853, 2966, { tile = true }),
      Action.InventoryHighlight:new(bloatedToad),
    },
    postconditions = { Condition.ModelVisible:new(bloatedToad, { atLocation = Location:new(2636, 853, 2966) }) },
  },
  {
    text = "Wait for a chompy bird to appear.",
    postconditions = { Condition.ModelVisible:new(chompyBird) },
  },
  {
    text = "Talk to Rantz.",
    actions = { Action.Direction:new(2630, 989, 2982) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["rantz"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["rantz"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["rantz"]) },
    jumpOffset = -1,
    postconditions = { Condition.ChatText:new("arrows are rubbish") },
  },
  {
    text = "Talk to Rantz again.",
    actions = { Action.Direction:new(2630, 989, 2982) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["rantz"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["rantz"]),
      Action.ConversationHighlight:new("Come on, let me have a go..."),
      Action.ConversationHighlight:new("I'm actually quite strong...please let me try."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["rantz"]) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(ogreBow) },
  },
  {
    text = "Equip the ogre bow.",
    actions = { Action.InventoryHighlight:new(ogreBow) },
    postconditions = { Condition.InventoryDoesNotContain:new(ogreBow) },
  },
  {
    text = "Place another toad in the clearing.",
    actions = {
      Action.Direction:new(2636, 853, 2966, { tile = true }),
      Action.InventoryHighlight:new(bloatedToad),
    },
    postconditions = {
      Condition.ModelVisible:new(bloatedToad, { atLocation = Location:new(2636, 853, 2966) }),
      Condition.ModelVisible:new(chompyBird),
    },
  },
  {
    text = "Wait for a chompy bird to appear.",
    postconditions = { Condition.ModelVisible:new(chompyBird) },
    jumpconditions = { Condition.ModelNotVisible:new(bloatedToad) },
    jumpOffset = -1,
  },
  {
    text = "Kill and pluck the chompy bird.",
    actions = { Action.ModelHighlight:new(chompyBird) },
    postconditions = { Condition.ModelVisible:new(Models.items["raw chompy bird"]) },
  },
  {
    text = "Pick up the raw chompy.",
    actions = { Action.ModelHighlight:new(Models.items["raw chompy bird"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["raw chompy bird"]) },
  },
  {
    text = "Talk to Rantz. Remember the ingredient he wants.",
    actions = { Action.Direction:new(2630, 989, 2982) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["rantz"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["rantz"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["rantz"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("HUH") },
  },
  {
    text = "Enter Rantz's cave.",
    title = "Cooking the bird",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.objects["rantz cave entrance"]) },
    postconditions = { Condition.DistanceTo:new(2647, 1133, 9378, 4) },
  },
  {
    text = "Talk to Fycie. Remember the ingredient she wants.",
    actions = { Action.ModelHighlight:new(fycie) },
    postconditions = { Condition.ConversationText:new("wait to eats it") },
  },
  {
    text = "Talk to Bugs. Remember the ingredient he wants.",
    actions = { Action.ModelHighlight:new(bugs) },
    postconditions = { Condition.ConversationText:new("favourite yumm") },
  },
  {
    text = "Onion.",
    warning = "Use the raw chompy on the spit-roast to list the needed ingredients.",
    actions = { Action.Direction:new(2583, 805, 2965, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["onion"]) },
  },
  {
    text = "Doogle leaves.",
    actions = { Action.Direction:new(2565, 965, 2972, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["doogle leaves"]) },
  },
  {
    text = "Tomato.",
    actions = { Action.Direction:new(2581, 1313, 2965) },
    postconditions = { Condition.InventoryContains:new(Models.items["tomato"]) },
  },
  {
    text = "Cabbage.",
    actions = { Action.Direction:new(2587, 805, 2973, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["cabbage"]) },
  },
  {
    text = "Equa leaves.",
    actions = { Action.Direction:new(2648, 293, 2963, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["equa leaves"]) },
  },
  {
    text = "Raw potato.",
    actions = { Action.Direction:new(2640, 685, 2958, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["raw potato"]) },
  },
  {
    text = "Use the raw chompy on the spit-roast north of Rantz.",
    actions = { Action.Direction:new(2629.5, 1537, 2991.5) },
    postconditions = {
      Condition.ModelVisible:new(ogreSpitRoast),
      Condition.InventoryContains:new(cookedChompyBird),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(ogreSpitRoast),
      Action.InventoryHighlight:new(Models.items["raw chompy bird"]),
    },
    jumpconditions = { Condition.ModelNotVisible:new(ogreSpitRoast) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(cookedChompyBird) },
  },
  {
    text = "Talk to Rantz.",
    title = "Finishing up",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["rantz"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Big Chompy Bird Hunting",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1084838400,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Cooking", 30),
    Types.QuestReq.skill("Fletching", 5),
    Types.QuestReq.skill("Ranged", 30),
  },
  neededItems = {
    ["Feathers"] = { quantity = 25, model = Models.items["feather"], duringQuest = true },
    ["Wolf bones"] = { quantity = 6, model = wolfBones, duringQuest = true },
    ["Achey tree logs"] = { quantity = 6, model = acheyLogs, duringQuest = true },
  },
  recommendedItems = {
    ["Ring of dueling"] = { quantity = 1 },
    ["Dramen staff"] = { quantity = 1 },
    ["Ranged combat gear"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Chomby bird"] = { level = "2", quantity = 1 },
    ["Wolves"] = { level = "11", optional = true, quantity = 1 },
  },
})
