local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local sisterCatherina = Model.new(5736, {
  [17] = Vertex.new(18, 743, -16, 49, 89, 162),
  [63] = Vertex.new(18, 747, -16, 31, 49, 103),
  [89] = Vertex.new(-18, 747, -16, 31, 49, 103),
  [1224] = Vertex.new(9, 588, -41, 162, 69, 49),
  [1230] = Vertex.new(-18, 575, -35, 162, 69, 49),
})
local abbessBenita = Model.new(6909, {
  [5376] = Vertex.new(-32, 717, 55, 52, 48, 48),
  [5379] = Vertex.new(-43, 712, 35, 52, 48, 48),
  [5407] = Vertex.new(24, 724, 55, 52, 48, 48),
  [5408] = Vertex.new(32, 717, 55, 52, 48, 48),
  [5411] = Vertex.new(43, 712, 35, 52, 48, 48),
})
local valerio = Model.new(7575, {
  [6676] = Vertex.new(-156, 437, -133, 105, 45, 32),
  [7029] = Vertex.new(286, 380, -104, 0, 0, 0),
  [7047] = Vertex.new(256, 381, -109, 0, 0, 0),
  [7065] = Vertex.new(286, 406, -104, 0, 0, 0),
  [7083] = Vertex.new(256, 404, -109, 0, 0, 0),
})
local sisterElena = Model.new(5484, {
  [4321] = Vertex.new(16, 748, -16, 32, 29, 29),
  [4710] = Vertex.new(14, 750, -16, 28, 26, 25),
  [4990] = Vertex.new(-15, 754, -14, 194, 185, 185),
  [4997] = Vertex.new(-15, 754, -14, 194, 185, 185),
  [5008] = Vertex.new(15, 754, -14, 194, 185, 185),
})
local sisterCecilia = Model.new(5466, {
  [480] = Vertex.new(-48, 402, -21, 94, 41, 29),
  [4929] = Vertex.new(-34, 713, 53, 67, 40, 35),
  [4932] = Vertex.new(-38, 717, 30, 67, 40, 35),
  [4979] = Vertex.new(34, 713, 53, 67, 40, 35),
  [4982] = Vertex.new(38, 717, 30, 67, 40, 35),
})
local sisterCecilia2 = Model.new(5610, {
  [33] = Vertex.new(-48, 402, -21, 94, 40, 29),
  [5073] = Vertex.new(-34, 713, 53, 67, 40, 34),
  [5076] = Vertex.new(-38, 717, 30, 67, 40, 34),
  [5123] = Vertex.new(34, 713, 53, 67, 40, 34),
  [5126] = Vertex.new(38, 717, 30, 67, 40, 34),
})
local sisterDebora = Model.new(5163, {
  [4588] = Vertex.new(59, 457, -45, 65, 69, 71),
  [4589] = Vertex.new(56, 456, -50, 65, 69, 71),
  [4590] = Vertex.new(53, 457, -45, 65, 69, 71),
  [4623] = Vertex.new(-39, 737, 24, 138, 115, 88),
  [4659] = Vertex.new(39, 737, 24, 138, 115, 88),
})
local valerio2 = Model.new(6573, {
  [1875] = Vertex.new(-34, 637, -35, 134, 64, 12),
  [2073] = Vertex.new(34, 637, -35, 134, 64, 12),
  [6426] = Vertex.new(-24, 637, -37, 0, 0, 0),
  [6441] = Vertex.new(-17, 617, -48, 0, 0, 0),
  [6450] = Vertex.new(25, 637, -37, 0, 0, 0),
})
local ripperDemon = Model.new(4122, {
  [2880] = Vertex.new(116, 515, 21, 49, 38, 38),
  [2907] = Vertex.new(-70, 376, -91, 131, 97, 68),
  [2913] = Vertex.new(-36, 350, -109, 131, 97, 68),
  [3056] = Vertex.new(70, 376, -91, 131, 97, 68),
  [3062] = Vertex.new(36, 350, -109, 131, 97, 68),
})
local ripperDemon2 = Model.new(6324, {
  [5314] = Vertex.new(-28, 675, -58, 25, 26, 28),
  [5329] = Vertex.new(32, 675, -58, 25, 26, 28),
  [5330] = Vertex.new(39, 657, -65, 25, 26, 28),
  [5351] = Vertex.new(-12, 687, -78, 25, 26, 28),
  [5355] = Vertex.new(16, 687, -78, 25, 26, 28),
})
--#endregion
--#region Objects
local infirmaryBody = Model.new(2490, {
  [383] = Vertex.new(-290, 147, 432, 56, 55, 60),
  [629] = Vertex.new(290, 160, -533, 71, 70, 76),
  [834] = Vertex.new(286, 147, 434, 68, 67, 73),
  [888] = Vertex.new(290, 160, -533, 68, 67, 73),
  [1094] = Vertex.new(-200, 0, -504, 76, 66, 48),
})
local clocktowerStairs = Model.new(4104, {
  [2367] = Vertex.new(3879, 8798, 3747, 49, 39, 31),
  [3140] = Vertex.new(3879, 8798, 3747, 37, 29, 23),
  [3186] = Vertex.new(3763, 8655, 3862, 49, 39, 31),
  [3318] = Vertex.new(3544, 7943, 4649, 49, 39, 31),
  [3378] = Vertex.new(4115, 8686, 3593, 37, 29, 23),
})
local deadSisterElena = Model.new(4989, {
  [3776] = Vertex.new(-12, 78, 447, 57, 53, 52),
  [3778] = Vertex.new(-11, 76, 450, 32, 29, 29),
  [4463] = Vertex.new(-16, 75, 448, 0, 0, 0),
  [4476] = Vertex.new(-16, 76, 449, 0, 0, 0),
  [4479] = Vertex.new(-16, 76, 449, 0, 0, 0),
})
local closedGate = Model.new(13986, {
  [5465] = Vertex.new(-302, 122, -1128, 145, 121, 92),
  [9578] = Vertex.new(-302, 1440, 1024, 190, 174, 156),
  [9582] = Vertex.new(-302, 935, 1536, 186, 169, 150),
  [10647] = Vertex.new(-302, 935, 1536, 183, 165, 145),
  [12281] = Vertex.new(-288, 917, -1536, 85, 74, 65),
})
local gateLadder = Model.new(1716, {
  [17] = Vertex.new(4024, 6016, 395, 52, 36, 27),
  [20] = Vertex.new(3992, 6016, 395, 52, 36, 27),
  [32] = Vertex.new(4024, 6016, 395, 52, 36, 27),
  [36] = Vertex.new(4024, 6016, 395, 52, 36, 27),
  [104] = Vertex.new(3806, 6016, 395, 52, 36, 27),
})
local raisedGate = Model.new(14898, {
  [645] = Vertex.new(3584, 6163, 4096, 85, 74, 65),
  [930] = Vertex.new(3584, 6163, 4096, 179, 160, 139),
  [9305] = Vertex.new(3282, 6138, 920, 145, 121, 92),
  [10412] = Vertex.new(3282, 7456, 3072, 190, 174, 156),
  [13334] = Vertex.new(3381, 7404, 2904, 93, 81, 71),
})
local candelabrum = Model.new(1038, {
  [842] = Vertex.new(-54, 418, 9, 182, 182, 171),
  [933] = Vertex.new(-50, 447, 4, 201, 198, 193),
  [975] = Vertex.new(50, 447, 4, 201, 198, 193),
  [996] = Vertex.new(-50, 447, 4, 252, 207, 127),
  [1002] = Vertex.new(-50, 447, 4, 252, 207, 127),
})
local movedCandelabrum = Model.new(930, {
  [897] = Vertex.new(51, 426, 2, 60, 59, 31),
  [899] = Vertex.new(51, 418, 4, 60, 59, 31),
  [909] = Vertex.new(-49, 426, 2, 60, 59, 31),
  [917] = Vertex.new(50, 426, 3, 60, 59, 31),
  [929] = Vertex.new(-50, 426, 3, 60, 59, 31),
})
local cabbelabrum = Model.new(1848, {
  [923] = Vertex.new(33, 497, 47, 128, 152, 97),
  [953] = Vertex.new(-47, 481, 45, 128, 152, 97),
  [959] = Vertex.new(-49, 509, 23, 141, 167, 107),
  [977] = Vertex.new(-11, 511, 51, 138, 163, 104),
  [983] = Vertex.new(19, 524, 27, 141, 167, 107),
})
local fakeSister = Model.new(3567, {
  [2669] = Vertex.new(33, 528, 47, 134, 159, 101),
  [2747] = Vertex.new(-47, 512, 45, 134, 159, 101),
  [2753] = Vertex.new(-49, 540, 23, 149, 176, 112),
  [2771] = Vertex.new(-11, 542, 51, 143, 170, 108),
  [2777] = Vertex.new(19, 555, 27, 149, 176, 112),
})
local towerStairsDown = Model.new(20592, {
  [13408] = Vertex.new(769, 18848, 2305, 56, 144, 30),
  [13409] = Vertex.new(768, 18848, 2304, 56, 144, 30),
  [13414] = Vertex.new(1281, 18848, 1793, 56, 144, 30),
  [13417] = Vertex.new(1793, 18848, 1281, 56, 144, 30),
  [13418] = Vertex.new(1792, 18848, 1280, 56, 144, 30),
})
local wallCandle = Model.new(1173, {
  [33] = Vertex.new(-155, 859, 84, 83, 76, 76),
  [1164] = Vertex.new(-143, 988, 41, 59, 59, 31),
  [1166] = Vertex.new(-143, 972, 49, 59, 59, 31),
  [1170] = Vertex.new(-143, 951, -43, 59, 59, 31),
  [1172] = Vertex.new(-143, 935, -35, 59, 59, 31),
})
local clocktowerGlass = Model.new(564, {
  [47] = Vertex.new(-24, 1030, -158, 132, 106, 84, 0.8039),
  [53] = Vertex.new(-24, 856, -378, 132, 106, 84, 0.8039),
  [134] = Vertex.new(-241, 856, 261, 132, 106, 84, 0.8039),
  [164] = Vertex.new(-241, 1030, -158, 132, 106, 84, 0.8039),
  [173] = Vertex.new(-241, 856, -378, 132, 106, 84, 0.8039),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local sisterAnnasCellKey = Model.new(321, {
  [1] = Vertex.new(66, 14, 49, 100, 97, 91),
  [2] = Vertex.new(87, 16, 50, 100, 97, 91),
  [3] = Vertex.new(71, 16, 34, 100, 97, 91),
  [5] = Vertex.new(72, 14, 55, 100, 97, 91),
  [13] = Vertex.new(56, 14, 71, 100, 97, 91),
})
local investigatorsNotebook = Model.new(666, {
  [1] = Vertex.new(87, 7, -28, 27, 28, 25),
  [2] = Vertex.new(89, 34, -21, 27, 28, 25),
  [3] = Vertex.new(89, 13, -21, 27, 28, 25),
  [5] = Vertex.new(87, 43, -28, 27, 28, 25),
  [7] = Vertex.new(80, 13, -30, 46, 48, 44),
})
local clothFragment = Model.new(132, {
  [1] = Vertex.new(47, 2, 49, 87, 56, 45),
  [2] = Vertex.new(54, 12, 82, 87, 56, 45),
  [3] = Vertex.new(76, 4, 66, 87, 56, 45),
  [6] = Vertex.new(5, 8, 72, 87, 56, 45),
  [7] = Vertex.new(-12, 0, 21, 87, 56, 45),
})
local clothFragment2 = Model.new(132, {
  [1] = Vertex.new(47, 2, 49, 67, 50, 35),
  [2] = Vertex.new(54, 12, 82, 67, 50, 35),
  [3] = Vertex.new(76, 4, 66, 67, 50, 35),
  [6] = Vertex.new(5, 8, 72, 67, 50, 35),
  [7] = Vertex.new(-12, 0, 21, 67, 50, 35),
})
local bundleOfLetters = Model.new(609, {
  [1] = Vertex.new(58, 16, 40, 131, 128, 120),
  [2] = Vertex.new(32, 19, 58, 131, 128, 120),
  [3] = Vertex.new(41, 19, 56, 131, 128, 120),
  [4] = Vertex.new(-57, 9, -68, 131, 128, 120),
  [5] = Vertex.new(-33, 9, -62, 131, 128, 120),
})
local citharedeRobeTop = Model.new(1404, {
  [1] = Vertex.new(-16, -1, 121, 139, 149, 152),
  [2] = Vertex.new(-55, 11, 124, 139, 149, 152),
  [3] = Vertex.new(-43, -1, 109, 139, 149, 152),
  [4] = Vertex.new(77, 57, 63, 122, 130, 133),
  [5] = Vertex.new(51, 74, 65, 122, 130, 133),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Sister Catherina east of Al Kharid and the Desert strykewyrms.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered', and chat timestamps on.</li></ul>",
    title = "Getting started",
    actions = {
      Action.Direction:new(3405, 5733, 3165, { distance = 16 }),
      Action.ModelHighlight:new(sisterCatherina, { distance = 16 }),
      Action.ConversationHighlight:new("Yes! I've come to answer your prayers!"),
      Action.ConversationHighlight:new("Yes, I'll help you."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with Sister Catherina.",
    actions = { Action.ModelHighlight:new(sisterCatherina) },
    postconditions = { Condition.ConversationText:new("abbess's office is just inside") },
  },
  {
    text = "Talk to Abbess Benita in the abbey, in the centre room north of the fountain.",
    actions = {
      Action.Direction:new(3424, 6031, 3178, { distance = 6 }),
      Action.ModelHighlight:new(abbessBenita, { distance = 6 }),
      Action.ConversationHighlight:new("What can I do to help?"),
      Action.ConversationHighlight:new("Of course."),
    },
    postconditions = { Condition.ConversationText:new("examined the crime scene") },
  },
  {
    text = "Enter Anna's cell in the southeast corner, the westernmost one.",
    title = "Murder Mystery",
    actions = { Action.Direction:new(3440, 8061, 3153.5) },
    postconditions = { Condition.DistanceTo:new(3440, 7461, 3152, 1) },
  },
  {
    text = "Inspect the bed.",
    actions = { Action.Direction:new(3439, 7861, 3149.5) },
    postconditions = { Condition.InventoryContains:new(clothFragment) },
  },
  {
    text = "Inspect the window.",
    actions = { Action.Direction:new(3440, 8261, 3148) },
    postconditions = { Condition.ChatText:new("Clue added: Sister Anna's window was broken from the inside.") },
  },
  {
    text = "Go back northwest to the room west of Abbess Benita's office.",
    actions = { Action.Direction:new(3417, 6021, 3175) },
    postconditions = { Condition.DistanceTo:new(3417, 6021, 3175, 2) },
  },
  {
    text = "Inspect the beds for another cloth fragment.",
    actions = {
      Action.Direction:new(3415.5, 6321, 3174),
      Action.Direction:new(3415.5, 6321, 3177),
      Action.Direction:new(3420, 6321, 3173.5),
    },
    postconditions = { Condition.InventoryContains:new(clothFragment2) },
  },
  {
    text = "Inspect the body in the infirmary.",
    actions = {
      Action.Direction:new(3417, 6321, 3149.5, { distance = 5 }),
      Action.ModelHighlight:new(infirmaryBody, { distance = 5 }),
    },
    postconditions = { Condition.ChatText:new("Clue added: Victim's feet mutilated.") },
  },
  {
    text = "Talk to Valerio the musician outside of the abbey.",
    title = "Interogations",
    actions = {
      Action.Direction:new(3400, 5445, 3149, { distance = 14 }),
      Action.ModelHighlight:new(valerio, { distance = 14 }),
      Action.ConversationHighlight:new("What were you singing about?"),
    },
    postconditions = { Condition.ChatText:new("Clue added: Valerio's love song.") },
  },
  {
    actions = {
      Action.ModelHighlight:new(valerio),
      Action.ConversationHighlight:new("You were singing about Sister Anna, weren't you?"),
    },
    postconditions = { Condition.ChatText:new("Clue added: Valerio had a history with Sister Anna.") },
  },
  {
    actions = {
      Action.ModelHighlight:new(valerio),
      Action.ConversationHighlight:new("Where were you between Vespers and Compline yesterday?"),
    },
    postconditions = {
      Condition.ChatText:new("Clue added: Valerio claims he was in Al Kharid at the time of the murder."),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(valerio),
      Action.ConversationHighlight:new("I can't tell you, sorry."),
    },
    postconditions = { Condition.ConversationText:new("Zamorak with you") },
  },
  {
    text = "Talk to Sister Elena, north of the cabbages, in the kitchen.",
    actions = {
      Action.Direction:new(3429, 6021, 3176, { distance = 4 }),
      Action.ModelHighlight:new(sisterElena, { distance = 4 }),
      Action.ConversationHighlight:new("Where were you between Vespers and Compline yesterday?"),
    },
    postconditions = {
      Condition.ChatText:new("Clue added: Sister Elena claims she was in the kitchen at the time of the murder."),
      Condition.ChatText:new("Clue added: Sister Elena claims that food had been stolen from the cellar."),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(sisterElena),
      Action.ConversationHighlight:new("What did you think of Sister Anna?"),
    },
    postconditions = {
      Condition.ChatText:new(
        "Clue added: Sister Elena disliked Sister Anna. She claimed that Sister Anna argued with Abbess Benita."
      ),
    },
  },
  {
    text = "Talk to Sister Cecilia in the chapel.",
    actions = {
      Action.Direction:new(3444, 7481, 3167, { distance = 10 }),
      Action.ModelHighlight:new(sisterCecilia, { distance = 10 }),
      Action.ConversationHighlight:new("What are you rehearsing?"),
    },
    postconditions = {
      Condition.ChatText:new("Clue added: Sister Cecilia's hymn to Saint Elspeth."),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(sisterCecilia),
      Action.ConversationHighlight:new("No - could you tell me?"),
      Action.ConversationHighlight:new("Go on."),
    },
    postconditions = {
      Condition.ChatText:new("Clue added: Sister Cecilia told me about the Ripper demon."),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(sisterCecilia),
      Action.ConversationHighlight:new("Back to my other questions..."),
      Action.ConversationHighlight:new("Where were you between Vespers and Compline yesterday?"),
    },
    postconditions = {
      Condition.ChatText:new("Clue added: Sister Cecilia claims she was in the oratory at the time of the murder."),
    },
  },
  {
    text = "Talk to Sister Catherina in the chapel.",
    actions = {
      Action.ModelHighlight:new(sisterCatherina),
      Action.ConversationHighlight:new("Where were you between Vespers and Compline yesterday?"),
      Action.ConversationHighlight:new("I'd like that."), --unsure which is right
      Action.ConversationHighlight:new("I'd like that"),
    },
    postconditions = {
      Condition.ChatText:new(
        "Clue added: Sister Catherina claims she was in the scriptorium at the time of the murder."
      ),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(sisterCatherina),
      Action.ConversationHighlight:new("What did you think of Sister Anna?"),
      Action.ConversationHighlight:new("What other applicant?"),
    },
    postconditions = { Condition.ChatText:new("Clue added: An applicant visited the abbey recently but disappeared.") },
  },
  {
    text = "Go up the stairs to the north.",
    actions = { Action.Direction:new(3446, 7861, 3176) },
    postconditions = { Condition.DistanceToWithHeight:new(3449, 8901, 3174, 3) },
  },
  {
    text = "Go up the stairs again.",
    actions = { Action.Direction:new(3451, 9301, 3177) },
    postconditions = { Condition.DistanceToWithHeight:new(3447, 16599, 3179, 3) },
  },
  {
    text = "Talk to Feet (Sister Debora).",
    actions = {
      Action.ModelHighlight:new(sisterDebora),
      Action.ConversationHighlight:new("Where were you between Vespers and Compline yesterday?"),
    },
    postconditions = { Condition.ConversationText:new("Could you set it to the right time?") },
  },
  {
    text = "<i>Operate</i> the clock mechanism to show 12 o'clock.",
    actions = { Action.Direction:new(3446, 16621, 3174.5) },
    postconditions = { Condition.ConversationText:new("Ah, yes, that's it.") },
  },
  { postconditions = { Condition.ConversationText:new("Oh, I'm terribly sorry.") } },
  {
    text = "Talk to Sister Debora.",
    actions = {
      Action.ModelHighlight:new(sisterDebora),
      Action.ConversationHighlight:new("What did you think of Sister Anna?"),
    },
    postconditions = {
      Condition.ChatText:new(
        "Clue added: Abbess Benita was planning to retire, and Sister Anna would have succeeded her as abbess."
      ),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(sisterDebora),
      Action.ConversationHighlight:new("Where were you between Vespers and Compline yesterday?"),
    },
    postconditions = { Condition.ChatText:new("claims she was in the clock tower") },
  },
  {
    text = "Return to Abbess Benita, select any suspect.",
    actions = { Action.Direction:new(3448, 16339, 3179) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3451, 8901, 3176, 3),
      Condition.InInstance:new(),
    },
  },
  {
    actions = { Action.Direction:new(3448, 8741, 3174) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3446, 7461, 3177, 4),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.Direction:new(3424, 6031, 3178, { distance = 6 }),
      Action.ModelHighlight:new(abbessBenita, { distance = 6 }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      Condition.InInstance:new(),
      Condition.ChatText:new("scream from the east"),
    },
  },
  {
    text = "Climb down the stairs to the cellar in the room northeast of the chapel.",
    title = "A development",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(28.5, 1430, -3, { instance = true }),
    },
    postconditions = { Condition.DistanceFrom:new(28, 1430, -2, 200, true) },
  },
  { postconditions = { Condition.ConversationText:new("got to protect us") } }, --to buffer moving from instance -> instance -> instance
  {
    text = "Talk to Sister Cecilia.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(sisterCecilia2),
    },
    postconditions = { Condition.ConversationText:new("investigate Sister Elena") },
  },
  {
    text = "Investigate the body (5 clues).",
    actions = { Action.ModelHighlight:new(deadSisterElena) },
    postconditions = { Condition.ChatText:new("Clue added: Wound on the back of the second victim's head.") },
  },
  {
    text = "Investigate the bloodstains (1 clue).",
    actions = { Action.Direction:new(3.5, 1, -4.5, { instance = true }) },
    postconditions = { Condition.ChatText:new("body moved from the reliquary") },
  },
  {
    text = "Search the robe cabinet south of Sister Elena's body. (1 clue)",
    actions = { Action.Direction:new(-2.15, 400, -7, { instance = true }) },
    postconditions = { Condition.ChatText:new("Clue added: Killer may have hidden in the robe cabinet.") },
  },
  {
    text = "Return to Abbess Benita for a cell key.",
    actions = { Action.Direction:new(0, 601, 2, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(3452, 7461, 3174, 8) },
  },
  {
    actions = {
      Action.Direction:new(3424, 6031, 3178, { distance = 6 }),
      Action.ModelHighlight:new(abbessBenita, { distance = 6 }),
      Action.ConversationHighlight:new("I need the key to Sister Elena's cell."),
    },
    postconditions = { Condition.ConversationText:new("Abbess Benita gives you a key.") },
  },
  {
    text = "Search the sack in the kitchen.",
    actions = { Action.Direction:new(3428, 6221, 3178.125) },
    postconditions = { Condition.ChatText:new("Clue added: Secret passage from the kitchen to outside the abbey.") },
  },
  {
    text = "Enter the second cell in the southeast corner of the abbey.",
    actions = { Action.Direction:new(3446, 8061, 3153.5) },
    postconditions = { Condition.DistanceTo:new(3446, 7461, 3152, 1) },
  },
  {
    text = "Inspect the bed.",
    actions = { Action.Direction:new(3447, 7581, 3149.5) },
    postconditions = {
      Condition.ConversationText:new("You find a bundle of letters under the bed."),
      Condition.InventoryContains:new(bundleOfLetters),
    },
  },
  {
    text = "Read the letters (1 clue).",
    actions = { Action.InventoryHighlight:new(bundleOfLetters) },
    postconditions = { Condition.ChatText:new("between Sister Elena") },
  },
  {
    text = "Talk to Valerio the musician about Sister Elena's death (1 clue).",
    actions = {
      Action.Direction:new(3400, 5445, 3149, { distance = 14 }),
      Action.ModelHighlight:new(valerio, { distance = 14 }),
      Action.ConversationHighlight:new("Were you having an affair with Sister Elena?"),
      Action.ConversationHighlight:new("Sister Elena is dead."),
      Action.ConversationHighlight:new("[Any option]"),
    },
    postconditions = { Condition.ChatText:new("Clue added: Valerio confirmed he was seeing Sister Elena.") },
  },
  {
    text = "Talk to Sister Cecilia in the chapel (1 clue).",
    actions = {
      Action.Direction:new(3444, 7881, 3158.4, { distance = 8 }),
      Action.ModelHighlight:new(sisterCecilia, { distance = 8 }),
      Action.ConversationHighlight:new("Can you tell me exactly what happened?"),
    },
    postconditions = { Condition.ChatText:new("Clue added: Sister Cecilia claims she saw the Ripper demon.") },
  },
  {
    text = "Return to Abbess Benita and blame anyone.",
    actions = {
      Action.Direction:new(3424, 6031, 3178, { distance = 6 }),
      Action.ModelHighlight:new(abbessBenita, { distance = 6 }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("Could you go and fetch her please?") },
  },
  {
    text = "Talk to Sister Catherina, who is now dancing outside near Valerio.",
    actions = {
      Action.Direction:new(3399, 6045, 3152, { distance = 14 }),
      Action.ModelHighlight:new(sisterCatherina, { distance = 14 }),
      Action.ConversationHighlight:new("Abbess Benita asked me to bring you back to the abbey."),
    },
    postconditions = { Condition.ConversationText:new("you dance with me first") },
  },
  {
    actions = {
      Action.Direction:new(3399, 6045, 3152, { distance = 14 }),
      Action.ModelHighlight:new(sisterCatherina, { distance = 14 }),
      Action.ConversationHighlight:new("No. You should go back into the abbey."),
    },
    postconditions = { Condition.ConversationText:new("no need to be like that") },
  },
  {
    text = "Talk to Sister Catherina again.",
    actions = {
      Action.ModelHighlight:new(sisterCatherina),
      Action.ConversationHighlight:new("Abbess Benita asked me to bring you back to the abbey."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { -- This step needs more testing
    text = "Watch the cutscene.",
    postconditions = {
      Condition.ModelVisible:new(gateLadder),
      Condition.ModelVisible:new(raisedGate),
    },
  },
  {
    text = "Climb the nearby ladder.<ul><li>You do not need to do this step if you skip the cutscene.</li></ul>",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(gateLadder),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(-2, 1450, -4, 4, true),
      Condition.ModelVisible:new(raisedGate),
    },
  },
  {
    text = "Operate the winch.",
    actions = { Action.Direction:new(-2, 1650, -5, { instance = true }) },
    postconditions = {
      Condition.ChatText:new("You raise the portcullis."),
      Condition.ModelVisible:new(raisedGate),
    },
  },
  {
    text = "Talk to Valerio near the dead body.",
    actions = { Action.ModelHighlight:new(valerio2) },
    postconditions = {
      Condition.ConversationText:new(
        "This must end. Adventurer, troubadour, please meet me in my office. We need a plan."
      ),
    },
  },
  {
    text = "Talk to Abbess Benita.",
    actions = {
      Action.Direction:new(3424, 6031, 3178, { distance = 6 }),
      Action.ModelHighlight:new(abbessBenita, { distance = 6 }),
      Action.ConversationHighlight:new("(More...)"),
      Action.ConversationHighlight:new("I don't know who did it."),
      Action.ConversationHighlight:new("We should lay a trap."),
      Action.ConversationHighlight:new("A new victim."),
      Action.ConversationHighlight:new("They were all associated with Valerio."),
    },
    postconditions = { Condition.ConversationText:new("Saradomin go with all of us.") },
  },
  {
    text = "Pick a cabbage.",
    title = "Catching the Killer",
    neededItems = { ["Cabbage"] = { quantity = 1 } },
    actions = { Action.Direction:new(3430, 6037, 3165) },
    postconditions = { Condition.InventoryContains:new(Models.items["cabbage"]) },
  },
  {
    text = "Go downstairs to the cellar in the room northeast of the chapel.",
    actions = { Action.Direction:new(3452.5, 7461, 3173) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Click move on the candelabrum near the eastern wall in the centre of the room north of the Reliquary.",
    actions = { Action.ModelHighlight:new(candelabrum) },
    postconditions = { Condition.ModelVisible:new(movedCandelabrum) },
  },
  {
    text = "<i>Use</i> a cabbage on it. <b>Don't eat it.</b>",
    actions = {
      Action.ModelHighlight:new(movedCandelabrum),
      Action.InventoryHighlight:new(Models.items["cabbage"], true),
    },
    postconditions = { Condition.ModelVisible:new(cabbelabrum) },
  },
  {
    text = "Search the robe cabinet.",
    actions = { Action.Direction:new(-2.15, 400, -7, { instance = true }) },
    postconditions = {
      Condition.ConversationText:new("You take the Citharede robes."),
      Condition.InventoryContains:new(citharedeRobeTop),
    },
  },
  {
    text = "Use the robes on the cabbage-on-candelabrum.",
    actions = {
      Action.InventoryHighlight:new(citharedeRobeTop),
      Action.ModelHighlight:new(cabbelabrum),
    },
    postconditions = { Condition.ModelVisible:new(fakeSister) },
  },
  {
    text = "Hide-in the cabinet.",
    actions = {
      Action.Direction:new(-2.15, 400, -7, { instance = true }),
      Action.ConversationHighlight:new("Hide in the robe cabinet and wait."),
    },
    postconditions = { Condition.ModelNotVisible:new(fakeSister) },
  },
  {
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ModelVisible:new(ripperDemon) },
  },
  {
    text = "<i>Knock out</i> the killer, then chase after them around the abbey.",
    actions = { Action.ModelHighlight:new(ripperDemon) },
    postconditions = { Condition.ModelNotVisible:new(ripperDemon) },
  },
  {
    text = "Go upstairs.",
    title = "Endgame",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(1, 400, 8, { instance = true }),
    },
    postconditions = {
      Condition.DistanceTo:new(3452, 7461, 3174, 4), --If left instance and rejoined
      Condition.InInstance:new(), --Going from instanced cellar to overworld abbey
    },
  },
  { postconditions = { Condition.InInstance:new() } }, --Going from overworld abbey to instanced abbey
  { postconditions = { Condition.DistanceFromWithHeight:new(0, 0, 0, 400, true) } }, --After 3 frames, you start at 0,0,0, then jump up 7k in height
  {
    text = "Chase the killer around the abbey.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(ripperDemon),
      Action.PathGuide:new({ --not tested
        Location:new(0, 0, 0),
        Location:new(-2, 0, 3),
        Location:new(-9.5, 0, 3),
        Location:new(-17, 0, 4),
        Location:new(-22, -1440, 4),
        Location:new(-23, -1440, -1.5),
        Location:new(-26, -1420, -3),
        Location:new(-26, -1440, -5),
        Location:new(-24, -1440, -5),
        Location:new(-17, 0, -5),
        Location:new(-15, 20, -5),
        Location:new(-15, 20, -10),
        Location:new(-13, 20, -10),
        Location:new(-3, 20, -10),
        Location:new(-3, 20, -3.5),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-3, 0, -3, 1, true) },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(-6, 200, 2, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-3, 1440, 0, 4, true) },
  },
  {
    text = "Climb up the stairs again.",
    actions = { Action.Direction:new(-1, 1640, 3, { instance = true }) },
    postconditions = {
      Condition.DistanceToWithHeight:new(-5, 9126, 5, 4, true),
      Condition.DistanceToWithHeight:new(-5, 9140, 5, 4, true), --not sure which is correct
    },
  },
  {
    text = "Climb up the stairs yet again.",
    actions = { Action.Direction:new(-1, 9140, 2, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Stay back") },
  },
  {
    text = "Talk to the killer.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(ripperDemon2),
      Action.ConversationHighlight:new("Who are you?"),
      Action.ConversationHighlight:new("So the body I found..."),
      Action.ConversationHighlight:new("Enough talk!"),
    },
    postconditions = { Condition.ConversationText:new("Enough talk!") },
  },
  {
    text = "Climb down the stairs. You will get sent all the way down.",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(1, 0, 0, { instance = true }),
    },
    postconditions = { Condition.DistanceFrom:new(1, 1, 1, 100, true) },
  },
  { postconditions = { Condition.ModelVisible:new(wallCandle) } },
  {
    text = "Climb up the stairs.",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(-6, 200, 2, { instance = true }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(-3, 1440, 0, 4, true) },
  },
  {
    text = "Climb up the stairs again.",
    actions = { Action.Direction:new(-1, 1640, 3, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(clocktowerGlass) },
  },
  {
    text = "Operate the clock mechanism so that the minute hand points to 12",
    actions = { Action.ResetInstance:new() },
    postconditions = { Condition.ConversationText:new("hear the clock mechanism catch") },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(4, 300, -3, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(ripperDemon2),
      Condition.ModelVisible:new(sisterDebora),
    },
  },
  {
    text = "Talk to the killer.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(ripperDemon2),
      Action.ConversationHighlight:new("Listen - is that the bell ringing?"),
    },
    postconditions = { Condition.DistanceTo:new(3, 14, -4, 2, true) }, --not tested
  },
  {
    text = "Talk to Sister Debora.",
    actions = {
      Action.ModelHighlight:new(sisterDebora),
      Action.ConversationHighlight:new("What should we do with Sister Anna?"),
    },
    postconditions = { Condition.ConversationText:new("good Saradominist") },
  },
  {
    text = "Talk to Sister Anna.",
    actions = {
      Action.ModelHighlight:new(ripperDemon2),
      Action.ConversationHighlight:new("No. You die now."),
    },
    postconditions = { Condition.DistanceTo:new(3424, 6031, 3176, 6) },
  },
  {
    text = "Talk to Abbess Benita.",
    actions = {
      Action.ModelHighlight:new(abbessBenita),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "One Piercing Note",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.mediumlong,
  releaseDate = 1320624000,
  prereqQuests = {},
  questReqs = {},
  neededItems = { ["Cabbage"] = { quantity = 1, model = Models.items["cabbage"], duringQuest = true } },
  recommendedItems = {},
  combatNPCs = {},
})
