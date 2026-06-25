local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local nessie = Model.new(3174, {
  [1417] = Vertex.new(20, 528, -60, 52, 52, 48),
  [2880] = Vertex.new(220, 288, -4, 133, 118, 101),
  [2882] = Vertex.new(220, 288, -4, 133, 118, 101),
  [2884] = Vertex.new(220, 288, -4, 133, 118, 101),
  [2931] = Vertex.new(-220, 288, -4, 133, 118, 101),
})
local sanguinusVarnis = Model.new(6843, {
  [5855] = Vertex.new(-10, 673, -95, 153, 153, 166),
  [5859] = Vertex.new(-6, 673, -97, 153, 153, 166),
  [5862] = Vertex.new(-6, 656, -90, 153, 153, 166),
  [5868] = Vertex.new(10, 673, -95, 153, 153, 166),
  [5870] = Vertex.new(6, 673, -97, 153, 153, 166),
})
local bloodveldYoungling = Model.new(5031, {
  [121] = Vertex.new(-3, 137, -214, 127, 116, 127),
  [123] = Vertex.new(0, 142, -242, 127, 116, 127),
  [2695] = Vertex.new(8, 82, 39, 68, 52, 68),
  [2696] = Vertex.new(-8, 83, 40, 68, 52, 68),
  [2697] = Vertex.new(0, 82, 53, 68, 52, 68),
})
local mariaGadderanks = Model.new(3888, {
  [1911] = Vertex.new(24, 738, -38, 30, 29, 27),
  [1935] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [1957] = Vertex.new(-2, 716, -57, 106, 97, 97),
  [1963] = Vertex.new(2, 716, -57, 106, 97, 97),
  [1967] = Vertex.new(6, 716, -52, 106, 97, 97),
})
local valentinaKaust = Model.new(6765, {
  [2657] = Vertex.new(-11, 675, -108, 187, 153, 152),
  [2669] = Vertex.new(-5, 675, -115, 187, 153, 152),
  [2675] = Vertex.new(-2, 675, -117, 187, 153, 152),
  [2703] = Vertex.new(11, 675, -108, 187, 153, 152),
  [2715] = Vertex.new(5, 675, -115, 187, 153, 152),
})
local vyrewatchGuard = Model.new(6843, {
  [5855] = Vertex.new(-10, 673, -95, 153, 153, 166),
  [5859] = Vertex.new(-6, 673, -97, 153, 153, 166),
  [5862] = Vertex.new(-6, 656, -90, 153, 153, 166),
  [5868] = Vertex.new(10, 673, -95, 153, 153, 166),
  [5870] = Vertex.new(6, 673, -97, 153, 153, 166),
})
local ranisDrakan = Model.new(6555, {
  [5229] = Vertex.new(-55, 632, -24, 164, 151, 151),
  [5498] = Vertex.new(-66, 677, 24, 74, 68, 67),
  [5507] = Vertex.new(-72, 670, 16, 74, 68, 67),
  [5511] = Vertex.new(66, 677, 24, 74, 68, 67),
  [5520] = Vertex.new(72, 670, 16, 74, 68, 67),
})
local harold = Model.new(5031, {
  [121] = Vertex.new(-13, 551, -857, 154, 141, 153),
  [123] = Vertex.new(0, 568, -970, 154, 141, 153),
  [2695] = Vertex.new(34, 328, 159, 82, 63, 82),
  [2696] = Vertex.new(-32, 333, 161, 82, 63, 82),
  [2697] = Vertex.new(0, 328, 213, 82, 63, 82),
})
--#endregion
--#region Objects
local letterObj = Model.new(138, {
  [7] = Vertex.new(-100, 16, -56, 158, 144, 121),
  [10] = Vertex.new(-100, 16, -56, 158, 144, 121),
  [16] = Vertex.new(100, 24, -56, 158, 144, 121),
  [46] = Vertex.new(36, 36, 4, 168, 153, 128),
})
local openDoor = Model.new(384, {
  [3] = Vertex.new(-196, 705, 224, 46, 31, 23),
  [267] = Vertex.new(-252, 1125, -244, 50, 44, 38),
  [275] = Vertex.new(-208, 1130, 228, 57, 43, 36),
  [344] = Vertex.new(-252, 1125, -244, 50, 38, 32),
  [348] = Vertex.new(-252, 1125, -244, 50, 38, 32),
})
local propagandaPoster = Model.any({
  Model.new(4266, {
    [41] = Vertex.new(-33, 837, -142, 0, 0, 0),
    [119] = Vertex.new(-33, 837, 161, 0, 0, 0),
    [929] = Vertex.new(-3, 679, -96, 76, 76, 83),
    [2208] = Vertex.new(-3, 661, 135, 13, 12, 12),
    [4176] = Vertex.new(-3, 822, 35, 13, 12, 12),
  }),
  Model.new(4470, {
    [41] = Vertex.new(-33, 837, 161, 0, 0, 0),
    [47] = Vertex.new(-34, 195, 188, 22, 20, 20),
    [119] = Vertex.new(-33, 837, -142, 0, 0, 0),
    [2495] = Vertex.new(-16, 699, -142, 124, 113, 94),
    [4282] = Vertex.new(-16, 837, 161, 56, 51, 43),
  }),
  Model.new(4284, {
    [41] = Vertex.new(-33, 837, 161, 0, 0, 0),
    [119] = Vertex.new(-33, 837, -142, 0, 0, 0),
    [932] = Vertex.new(0, 679, -96, 76, 76, 83),
    [2235] = Vertex.new(0, 661, 135, 13, 12, 12),
    [4191] = Vertex.new(0, 822, 35, 13, 12, 12),
  }),
  Model.new(4470, {
    [41] = Vertex.new(-33, 837, -142, 0, 0, 0),
    [119] = Vertex.new(-33, 837, 161, 0, 0, 0),
    [125] = Vertex.new(-34, 195, 188, 22, 20, 20),
    [2504] = Vertex.new(-16, 699, -142, 94, 120, 124),
    [4282] = Vertex.new(-16, 837, 161, 43, 55, 56),
  }),
})
--#endregion
--#region Items
local gadderhammer = Model.new(408, {
  [16] = Vertex.new(88, 144, 244, 129, 110, 11),
  [19] = Vertex.new(128, 144, 204, 129, 110, 11),
  [20] = Vertex.new(88, 144, 244, 129, 110, 11),
  [22] = Vertex.new(128, 144, 204, 129, 110, 11),
  [24] = Vertex.new(88, 144, 244, 129, 110, 11),
})
local mysteriousMedallion = Model.new(447, {
  [249] = Vertex.new(23, 0, -29, 58, 57, 53),
  [254] = Vertex.new(23, 0, -29, 58, 57, 53),
  [260] = Vertex.new(23, 0, -29, 58, 57, 53),
  [281] = Vertex.new(-23, 0, -29, 58, 57, 53),
  [287] = Vertex.new(-23, 0, -29, 58, 57, 53),
})
local blisterwoodLog = Model.new(606, {
  [197] = Vertex.new(-100, 143, 20, 128, 131, 140),
  [320] = Vertex.new(-104, 156, -16, 128, 131, 140),
  [324] = Vertex.new(-104, 156, -16, 128, 131, 140),
  [411] = Vertex.new(-104, 156, -16, 83, 84, 90),
  [417] = Vertex.new(-104, 156, -16, 83, 84, 90),
})
local blisterwoodStaff = Model.new(885, {
  [1] = Vertex.new(-84, 25, 120, 47, 30, 29),
  [2] = Vertex.new(-128, 22, 124, 47, 30, 29),
  [3] = Vertex.new(-121, 22, 81, 47, 30, 29),
  [221] = Vertex.new(-209, 20, 178, 78, 75, 72),
  [786] = Vertex.new(-230, 21, 251, 90, 87, 83),
})
local blisterwoodPolearm = Model.new(1869, {
  [113] = Vertex.new(-148, 18, 149, 68, 65, 63),
  [231] = Vertex.new(-216, 25, 171, 27, 28, 30),
  [666] = Vertex.new(-235, 25, 181, 27, 28, 30),
  [1158] = Vertex.new(-227, 25, 178, 27, 28, 30),
  [1496] = Vertex.new(296, 22, -238, 118, 121, 129),
})
local blisterwoodStakes = Model.new(453, {
  [112] = Vertex.new(36, 13, -130, 40, 38, 37),
  [116] = Vertex.new(36, 13, -130, 30, 29, 27),
  [157] = Vertex.new(-59, 32, 97, 106, 102, 97),
  [408] = Vertex.new(-49, 30, 88, 71, 68, 65),
  [410] = Vertex.new(-59, 32, 97, 71, 68, 65),
})
--#endregion
--#region Quest Items
local letter = Model.new(138, {
  [7] = Vertex.new(-100, 16, -56, 131, 119, 100),
  [10] = Vertex.new(-100, 16, -56, 131, 119, 100),
  [16] = Vertex.new(100, 24, -56, 131, 119, 100),
  [46] = Vertex.new(36, 36, 4, 139, 127, 106),
  [67] = Vertex.new(-100, 16, 56, 139, 127, 106),
})
local smokeBomb = Model.new(375, {
  [1] = Vertex.new(6, 126, 43, 20, 19, 15),
  [4] = Vertex.new(1, 117, 44, 80, 78, 61),
  [6] = Vertex.new(6, 126, 43, 80, 78, 61),
  [9] = Vertex.new(6, 126, 43, 112, 110, 86),
  [50] = Vertex.new(1, 117, 44, 60, 58, 45),
})
local rippedDarkmeyerOutfit = Model.any({
  Model.new(1053, { --hood
    [282] = Vertex.new(-49, 127, -68, 110, 95, 69),
    [460] = Vertex.new(-57, 130, -57, 46, 42, 46),
    [942] = Vertex.new(-49, 127, -68, 110, 95, 69),
    [945] = Vertex.new(-49, 127, -68, 110, 95, 69),
    [946] = Vertex.new(-57, 130, -57, 110, 95, 69),
  }),
  Model.new(1584, { --torso
    [243] = Vertex.new(112, 30, -150, 30, 27, 27),
    [294] = Vertex.new(89, 22, -100, 30, 27, 27),
    [297] = Vertex.new(53, 34, -119, 30, 27, 27),
    [1501] = Vertex.new(99, 31, 73, 117, 91, 89),
    [1503] = Vertex.new(113, 28, 62, 117, 91, 89),
  }),
  Model.new(657, { --legs
    [9] = Vertex.new(53, 13, -209, 66, 51, 50),
    [39] = Vertex.new(58, 13, -190, 77, 59, 59),
    [133] = Vertex.new(53, 13, -209, 13, 12, 12),
    [587] = Vertex.new(96, 22, -140, 26, 24, 24),
    [593] = Vertex.new(96, 22, -140, 43, 34, 33),
  }),
  Model.new(312, { --boots
    [39] = Vertex.new(119, 0, 37, 0, 0, 0),
    [47] = Vertex.new(108, 0, 66, 0, 0, 0),
    [54] = Vertex.new(108, 0, 66, 0, 0, 0),
    [93] = Vertex.new(119, 0, 37, 16, 11, 10),
    [167] = Vertex.new(108, 0, 66, 16, 11, 10),
  }),
})
local bottleOfBlood = Model.multi({
  Model.new(582, {
    [516] = Vertex.new(15, 90, 41, 120, 129, 131),
    [520] = Vertex.new(15, 90, 41, 120, 129, 131),
    [526] = Vertex.new(15, 90, 41, 120, 129, 131),
    [549] = Vertex.new(26, 90, 29, 120, 129, 131),
    [553] = Vertex.new(26, 90, 29, 120, 129, 131),
  }),
  Model.new(108, {
    [21] = Vertex.new(11, 90, 39, 91, 98, 99, 0.8039),
    [24] = Vertex.new(11, 90, 39, 91, 98, 99, 0.8039),
    [36] = Vertex.new(26, 90, 29, 91, 98, 99, 0.8039),
    [39] = Vertex.new(26, 90, 29, 91, 98, 99, 0.8039),
    [98] = Vertex.new(23, 108, 28, 125, 135, 137, 0.8039),
  }),
})
local holyWater = Model.multi({
  Model.new(330, {
    [3] = Vertex.new(16, 124, 44, 71, 63, 54),
    [6] = Vertex.new(16, 124, 44, 71, 63, 54),
    [18] = Vertex.new(0, 124, 28, 71, 63, 54),
    [21] = Vertex.new(0, 124, 28, 71, 63, 54),
    [39] = Vertex.new(16, 124, 44, 71, 63, 54),
  }),
  Model.new(108, {
    [21] = Vertex.new(8, 84, 44, 91, 98, 99, 0.8039),
    [24] = Vertex.new(8, 84, 44, 91, 98, 99, 0.8039),
    [36] = Vertex.new(32, 84, 28, 91, 98, 99, 0.8039),
    [39] = Vertex.new(32, 84, 28, 91, 98, 99, 0.8039),
    [98] = Vertex.new(24, 104, 28, 125, 135, 137, 0.8039),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Climb down the trapdoor inside the pub in Burgh de Rott.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3490, 965, 3232, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    text = "Talk to Veliaf Hurtz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("What's wrong?"),
      Action.ConversationHighlight:new("Is there anything I can do to help?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Veliaf Hurtz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Okay, I'll go."),
    },
    postconditions = { Condition.ConversationText:new("Wish me luck") },
  },
  {
    text = "Exit the basement.",
    actions = { Action.Direction:new(3490, 1245, 9632) },
    postconditions = { Condition.DistanceTo:new(3491, 965, 3232, 4) },
  },
  {
    text = "Enter the cave entrance south of the Burgh de Rott bank.",
    title = "Mysertious medallion",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3496.5, 941, 3203.6) },
    postconditions = { Condition.DistanceTo:new(2273, 965, 5152, 4) },
  },
  {
    text = "Pick up the letter.",
    actions = { Action.ModelHighlight:new(letterObj) },
    postconditions = { Condition.InventoryContains:new(letter) },
  },
  {
    text = "Search the coffin.",
    actions = {
      Action.Direction:new(2263.5, 1373, 5152),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InventoryContains:new(mysteriousMedallion) },
  },
  {
    text = "Climb the rope",
    actions = { Action.Direction:new(2273.5, 1465, 5152) },
    postconditions = { Condition.DistanceTo:new(3496, 741, 3202, 4) },
  },
  {
    text = "Climb down the trapdoor inside the pub in Burgh de Rott.",
    actions = { Action.Direction:new(3490, 965, 3232, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    text = "Talk to Veliaf Hurtz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("I found a medallion."),
      Action.ConversationHighlight:new("Yes, please send me!"),
    },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9621, 4) },
  },
  {
    text = "Talk to Safalaan.",
    title = "An unexpected help",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3628, 965, 9643) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["safalaan"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("I've got a letter for you."),
      Action.ConversationHighlight:new("What do you think?"),
      Action.ConversationHighlight:new("What do you think we should do?"),
      Action.ConversationHighlight:new("Of course."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["safalaan"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Be careful") },
  },
  {
    text = "Exit the hideout.",
    actions = { Action.Direction:new(3626, 1665, 9617.3) },
    postconditions = { Condition.DistanceTo:new(3638, 4517, 3249, 4) },
  },
  {
    text = "Push the wall.",
    actions = { Action.Direction:new(3640, 5285, 3252.5) },
    postconditions = { Condition.DistanceTo:new(3640, 4485, 3254, 1) },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(3639, 5285, 3256.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3639, 5701, 3255, 4) },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3630.5, 5701, 3258),
        Location:new(3630, 5701, 3258),
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
    text = "Climb up the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3631, 4485, 3259),
        Location:new(3630, 4485, 3261),
        Location:new(3624, 4485, 3261),
        Location:new(3624, 4485, 3252),
        Location:new(3625, 4485, 3252),
        Location:new(3625, 4485, 3251),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3627, 5701, 3251, 4) },
  },
  {
    text = "Talk to the two vyrewatch.",
    postconditions = { Condition.ConversationText:new("bode well") },
  },
  {
    text = "Climb down the ladder at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3627, 5701, 3251),
        Location:new(3627, 5701, 3252),
        Location:new(3626, 5701, 3252),
        Location:new(3626, 5701, 3254),
        Location:new(3624, 5701, 3254),
        Location:new(3624, 5701, 3252),
        Location:new(3623, 5701, 3252),
        Location:new(3620, 5701, 3252),
        Location:new(3620, 5701, 3254.5),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3620, 4485, 3256, 4) },
  },
  {
    text = "Talk to Vertida Sefalatis at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3618, 4485, 3254),
        Location:new(3617, 4485, 3254),
        Location:new(3617, 4485, 3255),
        Location:new(3616, 4485, 3257),
        Location:new(3616, 4485, 3261),
        Location:new(3617, 4485, 3264),
        Location:new(3617, 4485, 3270),
        Location:new(3612, 4485, 3270),
        Location:new(3612, 4485, 3266),
        Location:new(3608, 4485, 3266),
        Location:new(3608, 4485, 3263),
        Location:new(3609, 4485, 3263),
      }),
      Action.ModelHighlight:new(openDoor, { atLocation = Location:new(3617, 4485, 3254) }),
    },
    postconditions = { Condition.DistanceTo:new(3610, 4485, 3263, 1) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["vertida sefalatis"]) },
    postconditions = { Condition.ConversationText:new("keep watch") },
  },
  {
    text = "Climb the wall.",
    actions = { Action.Direction:new(3610, 4985, 3265.5) },
    postconditions = { Condition.DistanceTo:new(3610, 5701, 3263, 4) },
  },
  {
    text = "Talk to Mekritus A'hara.",
    actions = { Action.ModelHighlight:new(Models.npcs["mekritus a'hara"]) },
    postconditions = { Condition.ConversationText:new("support") },
  },
  {
    text = "Stand on the lookout point for a cutscene.",
    actions = { Action.Direction:new(3603, 5701, 3262, { tile = true }) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Drop-down the floorboards to the east.",
    actions = { Action.Direction:new(3610, 5701, 3263, { tile = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(3610, 4485, 3264, 4) },
  },
  {
    text = "Talk to Nessie in the courtyard.",
    actions = {
      Action.ModelHighlight:new(nessie),
      Action.ConversationHighlight:new("How do you know my name?"),
    },
    postconditions = { Condition.ConversationText:new("so glad") },
  },
  {
    actions = {
      Action.ModelHighlight:new(nessie),
      Action.ConversationHighlight:new("What are you doing here?"),
    },
    postconditions = { Condition.ConversationText:new("with you") },
  },
  {
    text = "Attack Vanescula Drakan.",
    actions = { Action.ModelHighlight:new(Models.npcs["vanescula drakan"]) },
    postconditions = { Condition.DistanceTo:new(3604, 4485, 3264, 1) },
  },
  {
    text = "Talk to Vanescula Drakan.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("What are you doing here?"),
      Action.ConversationHighlight:new("How can you help?"),
      Action.ConversationHighlight:new("I'm not telling you anything, vampyre scum!"),
      Action.ConversationHighlight:new("How do you plan on freeing Meiyerditch?"),
    },
    postconditions = {
      Condition.ConversationText:new("Haemalchemy labs"),
      Condition.ConversationText:new("it will last"),
      Condition.ConversationText:new("an envelope"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("You won't get any information out of me."),
      Action.ConversationHighlight:new("Why do you want to kill Drakan?"),
      Action.ConversationHighlight:new("Why should I trust you?"),
      Action.ConversationHighlight:new("I'll listen, for now."),
      Action.ConversationHighlight:new("Tell me more about this tree."),
    },
    postconditions = {
      Condition.ConversationText:new("it will last"),
      Condition.ConversationText:new("an envelope"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("How do I know you are telling the truth?"),
    },
    postconditions = { Condition.ConversationText:new("an envelope") },
  },
  {
    text = "Teleport to Meiyerditch with the Drakan's medallion.",
    title = "Entering Darkmeyer",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "2",
      hover = "Drakan's medallion teleport",
      url = "Drakan's_medallion.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(mysteriousMedallion) },
    postconditions = { Condition.DistanceTo:new(3628, 965, 9624, 8) },
  },
  {
    text = "Talk to Safalaan.",
    actions = { Action.Direction:new(3628, 965, 9643) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["safalaan"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["safalaan"]),
      Action.ConversationHighlight:new("Meiyerditch"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["safalaan"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("me see") }, --not tested
  },
  {
    text = "Talk to Vertida Sefalatis.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["vertida sefalatis"]),
      Action.ConversationHighlight:new("What do I need to go to Darkmeyer?"),
    },
    postconditions = { Condition.ConversationText:new("retaking Morytania") },
  },
  {
    text = "Search the crate to the south-west.",
    actions = { Action.Direction:new(3618, 1165, 9620) },
    postconditions = { Condition.InventoryContains:new(smokeBomb) },
  },
  {
    text = "Teleport to the Meiyerditch laboratory with the Drakan's medallion.",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "3",
      hover = "Drakan's medallion teleport",
      url = "Drakan's_medallion.png",
    },
    actions = { Action.InventoryHighlight:new(mysteriousMedallion) },
    postconditions = { Condition.DistanceTo:new(3633, 965, 9694, 8) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(3637, 1465, 9697) },
    postconditions = { Condition.DistanceTo:new(3643, 4485, 3306, 4) },
  },
  {
    text = "Squeeze-through the grate at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3643, 4485, 3306),
        Location:new(3642, 4485, 3307),
        Location:new(3641, 4485, 3307),
        Location:new(3641, 4485, 3305),
        Location:new(3638, 4485, 3305),
        Location:new(3638, 4485, 3304),
        Location:new(3637, 4485, 3304),
        Location:new(3637, 4485, 3302),
        Location:new(3640, 4485, 3302),
        Location:new(3640, 4485, 3300),
        Location:new(3635, 4485, 3300),
        Location:new(3635, 4485, 3294),
        Location:new(3628, 4485, 3294),
        Location:new(3628, 4485, 3301),
        Location:new(3631, 4485, 3304),
        Location:new(3631, 4485, 3324),
        Location:new(3630, 4485, 3325),
        Location:new(3630, 4485, 3328),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3630, 4485, 3332, 4) },
  },
  {
    text = "Talk to Vanescula.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("What did you tell Safalaan?"),
    },
    postconditions = {
      Condition.ConversationText:new("secret"),
      Condition.ConversationText:new("get on with"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("So, where's this tree?"),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("I'll get on with it, then."),
    },
    postconditions = { Condition.ConversationText:new("get on with") },
  },
  {
    text = "Search chests in the first building.<ul><li>Use smoke bombs to avoid detection.</li><li>Stay away from the vyrewatch.</li></ul>",
    actions = {
      Action.Direction:new(3645, 4491, 3340.8),
      Action.Direction:new(3645, 4491, 3333.2),
      Action.InventoryHighlight:new(smokeBomb),
    },
    postconditions = { Condition.InventoryContains:new(rippedDarkmeyerOutfit) },
  },
  {
    text = "Search the chests in the second building.",
    actions = {
      Action.Direction:new(3656.25, 4485, 3344),
      Action.Direction:new(3661.75, 4491, 3344),
      Action.ModelHighlight:new(Models.npcs["any walking vyrewatch"], { highlightPriority = "closest" }),
      Action.InventoryHighlight:new(smokeBomb),
    },
    postconditions = { Condition.InventoryContains:new(rippedDarkmeyerOutfit, 2) },
  },
  {
    text = "Search the chests in the third building.",
    actions = {
      Action.Direction:new(3655.25, 4491, 3362),
      Action.Direction:new(3660.75, 4491, 3362),
      Action.ModelHighlight:new(Models.npcs["any walking vyrewatch"], { highlightPriority = "all" }),
      Action.InventoryHighlight:new(smokeBomb),
    },
    postconditions = { Condition.InventoryContains:new(rippedDarkmeyerOutfit, 3) },
  },
  {
    text = "Search the chests in the last building.",
    actions = {
      Action.Direction:new(3671, 4491, 3368.75),
      Action.Direction:new(3671, 4491, 3361.25),
      Action.ModelHighlight:new(Models.npcs["any walking vyrewatch"], { highlightPriority = "all" }),
      Action.InventoryHighlight:new(smokeBomb),
    },
    postconditions = { Condition.ChatText:new("all the pieces") },
  },
  {
    text = "Fix all four pieces of the darkmeyer disguise.",
    actions = { Action.InventoryHighlight:new(rippedDarkmeyerOutfit) },
    postconditions = { Condition.InventoryContains:new(Models.items["darkmeyer disguise"], 4) },
  },
  {
    text = "Equip the darkmeyer disguise.",
    actions = { Action.InventoryHighlight:new(Models.items["darkmeyer disguise"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["darkmeyer disguise"]) },
  },
  {
    text = "Return to Vanescula.",
    actions = { Action.Direction:new(3632, 4485, 3332) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["vanescula drakan"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("So can I go and get those logs now?"),
      Action.ConversationHighlight:new("No, of course not."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["vanescula drakan"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("better of you") },
  },
  {
    text = "Talk to Sanguinus Varnis to the north-east.",
    title = "Middle tier",
    neededItems = { ["Darkmeyer disguise"] = { quantity = 1, model = Models.items["darkmeyer disguise"] } },
    recommendedItems = { ["Gadderhammer"] = { quantity = 1 } },
    actions = { Action.Direction:new(3666, 5465, 3380) },
    postconditions = { Condition.ModelVisible:new(sanguinusVarnis) },
  },
  {
    actions = {
      Action.ModelHighlight:new(sanguinusVarnis, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new(""),
    },
    jumpconditions = { Condition.ModelNotVisible:new(sanguinusVarnis) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("any more") },
  },
  {
    text = "Vandalise the propaganda posters.",
    actions = {
      Action.ModelHighlight:new(propagandaPoster, { highlightPriority = "all" }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ChatText:new("all of the posters") },
  },
  {
    text = "Return to Sanguinus Varnis.",
    actions = { Action.Direction:new(3666, 5465, 3380) },
    postconditions = { Condition.ModelVisible:new(sanguinusVarnis) },
  },
  {
    actions = { Action.ModelHighlight:new(sanguinusVarnis, { atLocation = Location:new(3666, 5465, 3380) }) },
    jumpconditions = { Condition.ModelNotVisible:new(sanguinusVarnis) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("among my vyres") }, --not tested
  },
  {
    text = "Terrorize Maria Gadderanks.",
    actions = { Action.Direction:new(3670, 5451, 3399) },
    postconditions = { Condition.ModelVisible:new(mariaGadderanks) },
  },
  {
    actions = {
      Action.ModelHighlight:new(mariaGadderanks),
      Action.InventoryHighlight:new(gadderhammer),
      Action.ConversationHighlight:new("rat"),
      Action.ConversationHighlight:new("wretch"),
      Action.ConversationHighlight:new("dispose"),
      Action.ConversationHighlight:new("back later"),
      Action.ConversationHighlight:new("vermin"),
      Action.ConversationHighlight:new("dead"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(mariaGadderanks) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Leave me alone.") },
  },
  {
    text = "Use the angry or laugh emote next to Maria Gadderanks repeatedly.",
    postconditions = { Condition.ChatText:new("paying attention to you") },
  },
  {
    text = "Talk to Valentina Kaust to the west.",
    actions = { Action.Direction:new(3629, 5451, 3387) },
    postconditions = { Condition.ModelVisible:new(valentinaKaust) },
  },
  {
    actions = {
      Action.ModelHighlight:new(valentinaKaust, { atLocation = Location:new(3629, 5451, 3387) }),
      Action.ConversationHighlight:new("Ok"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(valentinaKaust) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("test") },
  },
  {
    text = "Drink the bottle of blood.",
    warning = "You'll lose half of your health.",
    actions = {
      Action.InventoryHighlight:new(bottleOfBlood),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("cough") },
  },
  {
    text = "Talk to Valentina Kaust again.",
    actions = {
      Action.ModelHighlight:new(valentinaKaust, { atLocation = Location:new(3629, 5451, 3387) }),
      Action.ConversationHighlight:new("best blood"),
    },
    postconditions = { Condition.ChatText:new("upper tier vyres will now acknowledge") },
  },
  {
    text = "Talk to the vyrewatch guard in the prison to the south.",
    title = "Upper tier",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3601, 6411, 3324) },
    postconditions = { Condition.ModelVisible:new(vyrewatchGuard) },
  },
  {
    actions = {
      Action.ModelHighlight:new(vyrewatchGuard, { atLocation = Location:new(3601, 6411, 3324) }),
      Action.ConversationHighlight:new("Yes!"),
      Action.ConversationHighlight:new("Ok."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(vyrewatchGuard) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Ok") },
  },
  {
    text = "Open the jail door.",
    actions = { Action.Direction:new(3602, 7011, 3321.5) },
    postconditions = { Condition.DistanceTo:new(3602, 6411, 3320, 1) },
  },
  {
    text = "Rip and tear.",
    postconditions = { Condition.ChatText:new("enter the Arboretum") },
  },
  {
    text = "Talk to Vanescula to the north.",
    title = "The Arboretum",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3599, 6405, 3356) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["vanescula drakan"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("How do I do that?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["vanescula drakan"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    actions = {
      Action.ConversationHighlight:new("pledge fealty"),
      Action.ConversationHighlight:new("devote my service"),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Attempt to enter the Arboretum.",
    actions = { Action.Direction:new(3622.5, 6925, 3364.5) },
    postconditions = { Condition.ConversationText:new("You there") },
  },
  {
    text = "Talk to Ranis Drakan.",
    actions = {
      Action.ModelHighlight:new(ranisDrakan),
      Action.ConversationHighlight:new("Of course not!"),
      Action.ConversationHighlight:new("I hereby pledge fealty to you, Ranis Drakan."),
    },
    postconditions = { Condition.ConversationText:new("watching you") },
  },
  {
    text = "Enter the Arboretum.",
    actions = { Action.Direction:new(3622.5, 6925, 3364.5) },
    postconditions = { Condition.DistanceTo:new(3624, 6405, 3364, 1) },
  },
  {
    text = "Check the health of the tree.",
    actions = { Action.Direction:new(3640.5, 7125, 3371) },
    postconditions = { Condition.ChatText:new("The tree needs") },
  },
  {
    text = "Pull the lever.<ul><li>Rotate the orbs by clicking the left-bottom orb of a group of four orbs.</li><li>Pull the lever again when the puzzle is complete.</li></ul>",
    actions = { Action.Direction:new(3647.89, 7105, 3364.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = 'Puzzle 1<br><table style="width:100px;height:80px;border-collapse:collapse;"><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td></tr></table>',
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Check the health of the tree.",
    actions = { Action.Direction:new(3640.5, 7125, 3371) },
    postconditions = { Condition.ChatText:new("The tree needs") },
  },
  {
    text = "Pull the lever.",
    actions = { Action.Direction:new(3647.89, 7105, 3364.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = 'Puzzle 2<br><table style="width:100px;height:105px;border-collapse:collapse;"><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td></tr></table>',
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Check the health of the tree.",
    actions = { Action.Direction:new(3640.5, 7125, 3371) },
    postconditions = { Condition.ChatText:new("No water energy may touch") },
  },
  {
    text = "Pull the lever.",
    actions = { Action.Direction:new(3647.89, 7105, 3364.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = 'Puzzle 3<br><table style="width:100px;height:105px;border-collapse:collapse;"><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td></tr></table>',
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Check the health of the tree.",
    actions = { Action.Direction:new(3640.5, 7125, 3371) },
    postconditions = { Condition.ChatText:new("No light energy may touch") },
  },
  {
    text = "Pull the lever.",
    actions = { Action.Direction:new(3647.89, 7105, 3364.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = 'Puzzle 4<br><table style="width:100px;height:160px;border-collapse:collapse;"><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td></tr><tr><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:red;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:green;margin:auto;"></div></td><td style="text-align:center;vertical-align:middle;padding:0;"><div style="width:15px;height:15px;border-radius:50%;background:blue;margin:auto;"></div></td></tr></table>',
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Chop the Blisterwood tree.",
    actions = { Action.Direction:new(3640.5, 7125, 3371) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to Vanescula.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["vanescula drakan"]),
      Action.ConversationHighlight:new("But the vampyres will be furious! You've betrayed us!"),
    },
    postconditions = { Condition.DistanceTo:new(3633, 4485, 3327, 4) },
  },
  {
    text = "Teleport to Meiyerditch with the Drakan's medallion.",
    title = "Preparing for the fight",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "3",
      hover = "Drakan's medallion teleport",
      url = "Drakan's_medallion.png",
    },
    neededItems = { ["Blisterwood logs"] = { quantity = 3, model = blisterwoodLog } },
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(mysteriousMedallion) },
    postconditions = { Condition.DistanceTo:new(3626, 965, 9621, 12) },
  },
  {
    text = "Talk to Vertida Sefalatis.",
    actions = { Action.Direction:new(3627, 965, 9643) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["vertida sefalatis"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vertida sefalatis"]),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["vertida sefalatis"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    actions = {
      Action.ConversationHighlight:new("I managed to get some logs from the blisterwood tree."),
      Action.ConversationHighlight:new("We may be able to trust her, yes."),
      Action.ConversationHighlight:new("I'd be happy to dispatch one of the Myreque's most fearsome opponents."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Vertida again for a bottle of holy water.",
    actions = { Action.ModelHighlight:new(Models.npcs["vertida sefalatis"]) },
    postconditions = { Condition.ConversationText:new("Thanks, Vertida") },
  },
  {
    text = "Talk to Kael Forshaw for two silver sickles.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["kael forshaw"]),
      Action.ConversationHighlight:new("Do you have any sickles I could use?"),
      Action.ConversationHighlight:new("That's all, thanks."),
    },
    postconditions = { Condition.ConversationText:new("thanks") },
  },
  {
    text = "Use the medallion to teleport back to Darkmeyer.",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "4",
      hover = "Drakan's medallion teleport",
      url = "Drakan's_medallion.png",
    },
    actions = { Action.InventoryHighlight:new(mysteriousMedallion) },
    postconditions = { Condition.DistanceTo:new(3627, 6405, 3364, 4) },
  },
  {
    text = "Craft each of the logs into a staff, a polearm, and a set of stakes.<ul><li>Bank if you need to. Keep the bottle of holy water and blisterwood weapons.</li></ul>",
    actions = { Action.ModelHighlight:new(blisterwoodLog) },
    postconditions = { Condition.InventoryContains:new(blisterwoodStaff) },
  },
  {
    actions = { Action.ModelHighlight:new(blisterwoodLog) },
    postconditions = { Condition.InventoryContains:new(blisterwoodPolearm) },
  },
  {
    actions = { Action.ModelHighlight:new(blisterwoodLog) },
    postconditions = { Condition.InventoryContains:new(blisterwoodStakes) },
  },
  {
    text = "Enter to Vanstrom Klause's home.",
    actions = { Action.Direction:new(3606.25, 6705, 3341.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Throw the bottle of holy water at Harold.",
    actions = { Action.InventoryHighlight:new(holyWater) },
    postconditions = { Condition.ChatText:new("holy water over Vanstrom's bloodveld") },
  },
  {
    text = "Kill Harold",
    postconditions = { Condition.ModelNotVisible:new(harold) },
  },
  {
    text = "Open the metal door.",
    actions = { Action.Direction:new(6, 400, 0.5, { instance = true }) },
    postconditions = { Condition.ConversationText:new("pleasant surprise") },
  },
  {
    text = "Kill Vanstrom Klause.<ul><li>Pray melee if close, mage at a distance.</li><li><b>'Stare into the darkness!':</b> Run to the edge and rotate camera away.</li><li><b>'Let the blood consume you!':</b> Run from the blood splodge.</li><li><b>'Come, fiends, and aid your master!':</b> Kill the minions.</li><li><b>Blood bombs:</b> run.</li><li><b>Cloud form:</b> Use holy water near him.</li></ul>",
    title = "The battle with Vanstrom",
    neededItems = {
      ["Darkmeyer disguise"] = { quantity = 1 },
      ["Vertida's bottle of holy water"] = { quantity = 1, model = holyWater },
      ["Blisterwood weaponry"] = { quantity = 1 },
      ["Combat equipment and food"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {},
    postconditions = { Condition.ConversationText:new("deal with Vanstrom's pets") },
  },
  {
    text = "Kill the bloodveld guardians. Shriek as soon as it recharges.",
    postconditions = { Condition.ConversationText:new("dealt with") },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to Vertida.",
    actions = { Action.Direction:new(3630, 965, 9644) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["vertida sefalatis"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vertida sefalatis"]),
      Action.ConversationHighlight:new("What happened?"),
      Action.ConversationHighlight:new("Icyene"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["vertida sefalatis"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Continue talking to Vertida.",
    actions = { Action.ModelHighlight:new(Models.npcs["vertida sefalatis"]) },
    postconditions = { Condition.ConversationText:new("Thank you again") },
  },
  {
    text = "Return to the basement of the pub in Burgh de Rott.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "2",
      hover = "Drakan's medallion teleport",
      url = "Drakan's_medallion.png",
    },
    actions = { Action.Direction:new(3490, 965, 3232, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 20) },
  },
  {
    text = "Talk to Veliaf Hurtz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Vanstrom is dead!"),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Branches of Darkmeyer",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.long,
  releaseDate = 1314748800,
  prereqQuests = { "Legacy of Seergaze" },
  questReqs = {
    Types.QuestReq.skill("Agility", 63),
    Types.QuestReq.skill("Crafting", 64),
    Types.QuestReq.skill("Farming", 63),
    Types.QuestReq.skill("Fletching", 70),
    Types.QuestReq.skill("Magic", 70),
    Types.QuestReq.skill("Slayer", 67),
  },
  neededItems = {},
  recommendedItems = {
    ["Gadderhammer"] = { quantity = 1 },
    ["Combat equipment and food"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Harold"] = { level = "98", quantity = 1 },
    ["Vanstrom Klause"] = { level = "140", quantity = 1 },
  },
})
