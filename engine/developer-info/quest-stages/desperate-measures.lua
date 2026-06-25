local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local lessEnormousButStillALizard = Model.new(44604, {
  [39040] = Vertex.new(0, 394, 1334, 157, 157, 157),
  [40640] = Vertex.new(0, 398, 1245, 157, 157, 157),
  [40657] = Vertex.new(0, 406, 1315, 157, 157, 157),
  [41083] = Vertex.new(6, 411, 893, 157, 157, 157),
  [41275] = Vertex.new(-6, 411, 893, 157, 157, 157),
})
local secondWaveDinos = Model.any({
  Model.new(26388, {
    [55] = Vertex.new(-402, 1238, -1097, 167, 167, 167),
    [62] = Vertex.new(-312, 1451, -1083, 167, 167, 167),
    [449] = Vertex.new(148, 1586, -1104, 167, 167, 167),
    [469] = Vertex.new(-163, 1586, -1104, 167, 167, 167),
    [25172] = Vertex.new(-310, 1401, -830, 167, 167, 167),
  }),
  Model.new(41868, {
    [33539] = Vertex.new(6, 402, 1076, 158, 157, 157),
    [33583] = Vertex.new(4, 393, 1142, 157, 157, 157),
    [33587] = Vertex.new(4, 393, 1142, 157, 157, 157),
    [33636] = Vertex.new(7, 407, 1075, 158, 157, 157),
    [33933] = Vertex.new(6, 411, 1053, 157, 157, 157),
  }),
  Model.new(62028, {
    [496] = Vertex.new(-7, 1502, -1190, 157, 157, 157),
    [12078] = Vertex.new(-102, 884, -1661, 157, 157, 157),
    [12886] = Vertex.new(60, 1572, -1104, 157, 157, 157),
    [24226] = Vertex.new(-75, 1572, -1104, 157, 157, 157),
    [24232] = Vertex.new(-75, 1572, -1104, 157, 157, 157),
  }),
})
local nodonCaretaker = Model.new(52623, {
  [45677] = Vertex.new(33, 914, 59, 127, 127, 127),
  [45716] = Vertex.new(13, 912, 72, 127, 127, 127),
  [46349] = Vertex.new(-21, 908, 68, 127, 127, 127),
  [46357] = Vertex.new(-6, 921, 84, 127, 127, 127),
  [46832] = Vertex.new(-26, 901, 88, 127, 127, 127),
})
local blackStoneDragon = Model.new(17730, {
  [479] = Vertex.new(-1793, 1290, -249, 191, 191, 191),
  [487] = Vertex.new(-392, 1106, -765, 191, 191, 191),
  [506] = Vertex.new(-3710, 1306, -1119, 191, 191, 191),
  [5472] = Vertex.new(-2948, 1346, -1195, 191, 191, 191),
  [9509] = Vertex.new(-2579, 1297, 128, 191, 191, 191),
})
--#endregion
--#region Objects
local soil = Model.new(1458, {
  [5] = Vertex.new(-297, 0, -297, 191, 191, 191),
  [9] = Vertex.new(297, 0, 297, 191, 191, 191),
  [11] = Vertex.new(-297, 0, -297, 191, 191, 191),
  [21] = Vertex.new(-296, 0, 297, 191, 191, 191),
  [38] = Vertex.new(-297, 0, -297, 191, 191, 191),
})
local dragonkinRemains = Model.new(759, {
  [14] = Vertex.new(-420, 6, -420, 191, 191, 191),
  [32] = Vertex.new(-420, 6, 420, 191, 191, 191),
})
local orthenRubble = Model.new(1176, {
  [63] = Vertex.new(-166, -173, -64, 191, 191, 191),
  [401] = Vertex.new(63, -143, -185, 191, 191, 191),
  [503] = Vertex.new(-161, -143, -109, 191, 191, 191),
  [505] = Vertex.new(-161, -143, -109, 191, 191, 191),
  [767] = Vertex.new(87, -68, -221, 191, 191, 191),
})
--#endregion
--#region Items
local orthenglass = Model.new(192, {
  [11] = Vertex.new(42, -4, -30, 127, 127, 127),
  [29] = Vertex.new(25, -4, -26, 127, 127, 127),
  [31] = Vertex.new(-29, -4, 38, 127, 127, 127),
  [67] = Vertex.new(25, -4, -26, 127, 127, 127),
  [130] = Vertex.new(-29, -4, 38, 127, 127, 127),
})
--#endregion
--#region Quest Items
local dragonkinDeviceD = Model.new(348, {
  [58] = Vertex.new(9, 29, 46, 69, 8, 94),
  [59] = Vertex.new(10, 31, 48, 69, 8, 94),
  [60] = Vertex.new(6, 29, 47, 69, 8, 94),
  [61] = Vertex.new(-21, 81, 41, 69, 8, 94),
  [62] = Vertex.new(-23, 78, 41, 69, 8, 94),
  [63] = Vertex.new(-19, 81, 42, 69, 8, 94),
  [66] = Vertex.new(-19, 78, 44, 69, 8, 94),
  [69] = Vertex.new(-21, 76, 43, 69, 8, 94),
  [70] = Vertex.new(37, 77, 33, 69, 8, 94),
  [71] = Vertex.new(36, 80, 33, 69, 8, 94),
  [72] = Vertex.new(36, 75, 35, 69, 8, 94),
  [75] = Vertex.new(34, 77, 37, 69, 8, 94),
  [78] = Vertex.new(33, 80, 35, 69, 8, 94),
  [79] = Vertex.new(-44, 32, 19, 69, 8, 94),
  [80] = Vertex.new(-42, 29, 19, 69, 8, 94),
  [81] = Vertex.new(-43, 34, 21, 69, 8, 94),
  [84] = Vertex.new(-41, 32, 23, 69, 8, 94),
  [87] = Vertex.new(-41, 29, 22, 69, 8, 94),
  [88] = Vertex.new(0, 1, -3, 69, 8, 94),
  [89] = Vertex.new(3, 1, -1, 69, 8, 94),
})
local dragonkinDevice = Model.new(348, {
  [58] = Vertex.new(9, 29, 46, 139, 16, 189),
  [59] = Vertex.new(10, 31, 48, 139, 16, 189),
  [60] = Vertex.new(6, 29, 47, 139, 16, 189),
  [61] = Vertex.new(-21, 81, 41, 139, 16, 189),
  [62] = Vertex.new(-23, 78, 41, 139, 16, 189),
  [63] = Vertex.new(-19, 81, 42, 139, 16, 189),
  [66] = Vertex.new(-19, 78, 44, 139, 16, 189),
  [69] = Vertex.new(-21, 76, 43, 139, 16, 189),
  [70] = Vertex.new(37, 77, 33, 139, 16, 189),
  [71] = Vertex.new(36, 80, 33, 139, 16, 189),
  [72] = Vertex.new(36, 75, 35, 139, 16, 189),
  [75] = Vertex.new(34, 77, 37, 139, 16, 189),
  [78] = Vertex.new(33, 80, 35, 139, 16, 189),
  [79] = Vertex.new(-44, 32, 19, 139, 16, 189),
  [80] = Vertex.new(-42, 29, 19, 139, 16, 189),
  [81] = Vertex.new(-43, 34, 21, 139, 16, 189),
  [84] = Vertex.new(-41, 32, 23, 139, 16, 189),
  [87] = Vertex.new(-41, 29, 22, 139, 16, 189),
  [88] = Vertex.new(0, 1, -3, 139, 16, 189),
  [89] = Vertex.new(3, 1, -1, 139, 16, 189),
})
local dragonkinTabletD = Model.new(780, {
  [16] = Vertex.new(-34, 39, 15, 69, 67, 53),
  [17] = Vertex.new(-34, 51, 15, 69, 67, 53),
  [18] = Vertex.new(-57, 40, -29, 69, 67, 53),
  [20] = Vertex.new(-57, 51, -29, 69, 67, 53),
  [23] = Vertex.new(-17, 51, 3, 69, 67, 53),
  [26] = Vertex.new(-17, 39, 3, 69, 67, 53),
  [31] = Vertex.new(57, 51, -29, 69, 67, 53),
  [32] = Vertex.new(34, 51, 15, 69, 67, 53),
  [33] = Vertex.new(57, 40, -29, 69, 67, 53),
  [35] = Vertex.new(34, 39, 15, 69, 67, 53),
  [39] = Vertex.new(17, 51, 3, 69, 67, 53),
  [41] = Vertex.new(17, 39, 3, 69, 67, 53),
  [46] = Vertex.new(-57, 51, 99, 69, 67, 53),
  [244] = Vertex.new(54, 46, -82, 66, 66, 72),
  [245] = Vertex.new(33, 46, -66, 66, 66, 72),
  [246] = Vertex.new(46, 19, -66, 66, 66, 72),
  [409] = Vertex.new(72, 40, -87, 66, 66, 72),
  [410] = Vertex.new(61, 46, -88, 66, 66, 72),
  [411] = Vertex.new(57, 19, -69, 66, 66, 72),
  [413] = Vertex.new(53, 19, -67, 66, 66, 72),
})
local dragonkinTablet = Model.new(780, {
  [16] = Vertex.new(-34, 39, 15, 142, 138, 109),
  [17] = Vertex.new(-34, 51, 15, 142, 138, 109),
  [18] = Vertex.new(-57, 40, -29, 142, 138, 109),
  [20] = Vertex.new(-57, 51, -29, 142, 138, 109),
  [23] = Vertex.new(-17, 51, 3, 142, 138, 109),
  [26] = Vertex.new(-17, 39, 3, 142, 138, 109),
  [31] = Vertex.new(57, 51, -29, 142, 138, 109),
  [32] = Vertex.new(34, 51, 15, 142, 138, 109),
  [33] = Vertex.new(57, 40, -29, 142, 138, 109),
  [35] = Vertex.new(34, 39, 15, 142, 138, 109),
  [39] = Vertex.new(17, 51, 3, 142, 138, 109),
  [41] = Vertex.new(17, 39, 3, 142, 138, 109),
  [46] = Vertex.new(-57, 51, 99, 142, 138, 109),
  [244] = Vertex.new(54, 46, -82, 138, 138, 151),
  [245] = Vertex.new(33, 46, -66, 138, 138, 151),
  [246] = Vertex.new(46, 19, -66, 138, 138, 151),
  [409] = Vertex.new(72, 40, -87, 138, 138, 151),
  [410] = Vertex.new(61, 46, -88, 138, 138, 151),
  [411] = Vertex.new(57, 19, -69, 138, 138, 151),
  [413] = Vertex.new(53, 19, -67, 138, 138, 151),
})
local eyeOfJas = Model.new(816, {
  [83] = Vertex.new(-40, 75, -26, 90, 103, 117),
  [275] = Vertex.new(-40, 75, -26, 90, 103, 117),
  [279] = Vertex.new(-40, 75, -26, 90, 103, 117),
  [531] = Vertex.new(-40, 75, -26, 109, 112, 118),
  [533] = Vertex.new(-40, 75, -26, 109, 112, 118),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Seren on the 1st floor (2nd floor[US]) of Burthorpe Castle.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Burthorpe lodestone",
      url = "Burthorpe_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["seren"]),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Seren.",
    actions = { Action.ModelHighlight:new(Models.npcs["seren"]) },
    postconditions = { Condition.ConversationText:new("test") },
  },
  {
    text = "Talk to Mr. Mordaut at the Anachronia base camp.",
    title = "Thok's story",
    warning = "Dismiss any pets or followers.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(5469, 11781, 2338) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["mr mordaut"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["mr mordaut"]),
      Action.ConversationHighlight:new("Anachronia."),
      Action.ConversationHighlight:new("Ask about Desperate Measures."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["mr mordaut"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("if you hurry") },
  },
  {
    text = "Talk to Thok to the north-west.",
    actions = { Action.Direction:new(5417, 2181, 2396) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["thok"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["thok"]),
      Action.ConversationHighlight:new("Yes"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["thok"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  -- Coords do not change in this instance
  {
    text = "Stand near Charos.",
    actions = { Action.ModelHighlight:new(Models.npcs["charos"], { instance = true }) },
    postconditions = { Condition.ConversationText:new("KNUCKLE SANDWICHES") },
  },
  {
    text = "Smash the less enormous but still a lizard dinos.",
    actions = { Action.ModelHighlight:new(lessEnormousButStillALizard, { instance = true, highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("lizards were no match") },
  },
  {
    text = "Walk down the path for more dialogue.",
    actions = {
      Action.Direction:new(27, 200, 5, { instance = true }),
      Action.ConversationHighlight:new("to stay"),
    },
    postconditions = { Condition.ConversationText:new("poured MORE LIZARDS") },
  },
  {
    text = "Smash more dinos.",
    actions = { Action.ModelHighlight:new(secondWaveDinos, { instance = true, highlightPriority = "all" }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Continue down the path then listen to Thok.",
    actions = { Action.ConversationHighlight:new("to stay") },
    postconditions = { Condition.ConversationText:new("way to the camp") },
  },
  {
    text = "Talk to Laniakea.",
    actions = { Action.ModelHighlight:new(Models.npcs["laniakea"]) },
    postconditions = { Condition.ConversationText:new("we went to the camp") },
  },
  {
    text = "Follow Laniakea.",
    actions = { Action.ModelHighlight:new(Models.npcs["laniakea"]) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Charos.",
    title = "Finding the dragonkin artefacts",
    neededItems = { ["Orthenglass (material storage container)"] = { quantity = 100 } },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["charos"]),
      Action.ConversationHighlight:new("much"),
      Action.ConversationHighlight:new("[Leave]"),
    },
    postconditions = { Condition.ConversationText:new("test") },
  },
  {
    text = "Uncover the soil.",
    actions = { Action.ModelHighlight:new(soil) },
    postconditions = { Condition.ModelVisible:new(dragonkinRemains) },
  },
  {
    text = "Excavate the dragonkin remains.",
    actions = { Action.ModelHighlight:new(dragonkinRemains) },
    postconditions = { Condition.InventoryContains:new(dragonkinDeviceD) },
  },
  {
    text = "Restore the dragonkin device at the archaeologist's workbench.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    actions = { Action.Direction:new(5459, 11057, 2334.5) },
    postconditions = { Condition.InventoryContains:new(dragonkinDevice) },
  },
  {
    text = "Talk to Charos.",
    actions = { Action.Direction:new(5418, 2181, 2399) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["charos"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["charos"]),
      Action.ConversationHighlight:new("[Leave]"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["charos"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["charos"]),
      Action.ConversationHighlight:new("[Leave]"),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Uncover the soil.",
    actions = { Action.ModelHighlight:new(soil) },
    postconditions = { Condition.ModelVisible:new(orthenRubble) },
  },
  {
    text = "Excavate the dragonkin remains.",
    actions = { Action.ModelHighlight:new(dragonkinRemains) },
    postconditions = { Condition.InventoryContains:new(dragonkinTabletD) },
  },
  {
    text = "Restore the dragonkin tablet at an archaeologist's workbench.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    actions = { Action.Direction:new(5459, 11057, 2334.5) },
    postconditions = { Condition.InventoryContains:new(dragonkinTablet) },
  },
  {
    text = "Talk to Charos.",
    actions = { Action.Direction:new(5418, 2181, 2399) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["charos"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["charos"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["charos"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["charos"]),
      Action.ConversationHighlight:new("[Leave]"),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Talk to Hannibus at the Anachronia Dinosaur Farm.<ul><li>Teleport with the mystical tree on the 1st floor (2nd floor[US]) of the Ardougne Manor Farm.</li></ul>",
    title = "Nodon hibernatorium shared memory",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {
      ["Dragonkin device"] = { quantity = 1, model = dragonkinDevice },
      ["Dragonkin tablet"] = { quantity = 1, model = dragonkinTablet },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(5201, 101, 2373) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["hannibus"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["hannibus"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["hannibus"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Hello there") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"]),
      Action.ConversationHighlight:new("[Leave]"),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Enter the Nodon Hibernatorium on the north-eastern coast of Anachronia.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    actions = { Action.Direction:new(5688, 7349, 2451) },
    postconditions = { Condition.DistanceTo:new(5534, 5861, 2825, 4) },
  },
  {
    text = "Talk to Hannibus.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Hannibus again.",
    actions = { Action.ModelHighlight:new(Models.npcs["hannibus"]) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Wait for the Nodon caretaker to enter the pylon passcode. <b>Remember/screenshot the passcode for the a following step.</b>",
    warning = "Remember/screenshot the passcode for the a following step.",
    postconditions = { Condition.ChatText:new("You see the symbols") },
  },
  {
    text = "Talk to Hannibus.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"], { instance = true }),
      Action.ConversationHighlight:new("No."),
    },
    postconditions = { Condition.ConversationText:new("wish") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"], { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Input the passcode on the pylon matching what you saw in the dream.",
    actions = { Action.Direction:new(5543.35, 5173, 2835.55) },
    postconditions = { Condition.ConversationText:new("chamber below") },
  },
  {
    text = "Talk to Hannibus.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Hannibus again.",
    actions = { Action.ModelHighlight:new(Models.npcs["hannibus"]) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Follow the nodon caretaker outside.",
    actions = { Action.Direction:new(1, 2388, -17.25, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Follow the nodon caretaker for the passphrase. <b>Remember/screenshot the passphrase for the a following step.</b>",
    warning = "<b>Remember/screenshot the passphrase for the a following step.</b>",
    postconditions = { Condition.ChatText:new("use the passphrase") },
  },
  {
    text = "Return to Hannibus.<ul><li>Alternatively, you can lobby and log back in.</li></ul>",
    actions = { Action.Direction:new(0, 600, 1, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"], { instance = true }),
      Action.ConversationHighlight:new("No."),
    },
    postconditions = {
      Condition.ConversationText:new("wish"),
      Condition.NotInInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"], { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Exit the room.",
    actions = { Action.Direction:new(5534, 6261, 2824) },
    postconditions = { Condition.DistanceTo:new(5688, 6949, 2450, 4) },
  },
  {
    text = "Enter the ruins.",
    actions = { Action.Direction:new(5691, 5301, 2475) },
    postconditions = { Condition.DistanceTo:new(5534, 5, 2868, 4) },
  },
  {
    text = "Talk to Hannibus.",
    title = "The Kindra council memory",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Hannibus again.",
    actions = { Action.Direction:new(18, 2416, -3, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["hannibus"], { instance = true }) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["hannibus"], { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["hannibus"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("hurt") },
  },
  {
    text = "Watch/skip the cutscene.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new("Ah good"),
      Condition.ChangedInstance:new(),
    },
  },
  {
    text = "Talk to Charos.",
    actions = { Action.ModelHighlight:new(Models.npcs["charos"]) },
    postconditions = { Condition.ConversationText:new("witness") },
  },
  {
    text = "Attack Kerapac or let your party attack him until he leaves.",
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Seren in Burthorpe.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Burthorpe lodestone",
      url = "Burthorpe_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2901.5, 2965, 3570.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2901, 3877, 3564, 8),
      Condition.ModelVisible:new(Models.npcs["seren"]),
    },
  },
  {
    actions = { Action.Direction:new(2892, 3883, 3567) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["seren"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["seren"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["seren"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("answers for us") },
  },
  {
    text = "Talk to Primrose in the house by the Needle.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "AKQ",
    },
    actions = { Action.Direction:new(2196, 805, 3694) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["primrose"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("Ask about Desperate Measures."),
      Action.ConversationHighlight:new("Ask how she's doing."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["primrose"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("miss Gail") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("Ask about the Needle"),
      Action.ConversationHighlight:new("Can the Needle be destroyed?"),
    },
    postconditions = { Condition.ConversationText:new("whatever harms it") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("Can the Needle be counteracted?"),
    },
    postconditions = { Condition.ConversationText:new("too careful") },
  },
  {
    -- text = "debug1",
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("Does the Needle have any weaknesses?"),
    },
    postconditions = { Condition.ConversationText:new("no defence") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("Ask about Gail."),
    },
    postconditions = { Condition.ConversationText:new("where I can") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("Was she vulnerable to anything?"),
    },
    postconditions = { Condition.ConversationText:new("with a guidebook") },
  },
  {
    -- text = "debug2",
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("What was it like being her?"),
    },
    postconditions = { Condition.ConversationText:new("have some ideas") },
  },
  {
    -- text = "debug3",
    actions = {
      Action.ModelHighlight:new(Models.npcs["primrose"]),
      Action.ConversationHighlight:new("Leave"),
    },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Return to Seren in Burthorpe.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Burthorpe lodestone",
      url = "Burthorpe_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2901.5, 2965, 3570.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2901, 3877, 3564, 8),
      Condition.ModelVisible:new(Models.npcs["seren"]),
    },
  },
  {
    actions = { Action.Direction:new(2892, 3883, 3567) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["seren"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["seren"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["seren"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("best behaviour") },
  },
  {
    text = "Talk to Seren outside of the Heart in the Kharidian Desert.",
    title = "Meeting Jas",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "DLQ",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3374, 149, 2887) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["seren"], { atLocation = Location:new(3374, 149, 2887) }),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["seren"]),
      Action.ConversationHighlight:new("Yes."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["seren"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Jas.",
    actions = {
      Action.ConversationHighlight:new("[Ask about Kerapac.]"),
      Action.ConversationHighlight:new("Can you intervene?"),
      Action.ConversationHighlight:new("Why not?"),
      Action.ConversationHighlight:new("Point out her power."),
    },
    postconditions = {
      Condition.ConversationText:new("Anathema"),
    },
  },
  {
    actions = {
      Action.ConversationHighlight:new("[Ask about something else.]"),
      Action.ConversationHighlight:new("[Ask about the Needle.]"),
      Action.ConversationHighlight:new("Can it be stopped?"),
    },
    postconditions = { Condition.ConversationText:new("dragonkin prevents") },
  },
  {
    actions = {
      Action.ConversationHighlight:new("[Ask about something else.]"),
      Action.ConversationHighlight:new("Can you help?"),
      Action.ConversationHighlight:new("will it do"),
    },
    postconditions = { Condition.ConversationText:new("Or we will") },
  },
  {
    actions = { Action.ConversationHighlight:new("Leave") },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Seren.",
    actions = { Action.ModelHighlight:new(Models.npcs["seren"]) },
    postconditions = { Condition.ConversationText:new("all too late") },
  },
  {
    text = "Talk to Charos in the Anachronia base camp.",
    title = "Dinosaur invasion",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    neededItems = { ["Eye of Jas"] = { quantity = 1, model = eyeOfJas } },
    recommendedItems = {},
    actions = { Action.Direction:new(1, 1, 1) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["charos"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["charos"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["charos"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Thok. You can't be wielding anything in your hands.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["thok"]),
      Action.ConversationHighlight:new("I'm ready."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Charos.<ul><li>If you exit the dialogue too early you need to leave and talk to Thok again.</li></ul>",
    postconditions = { Condition.ConversationActive:new() },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Change Charos' attack type to closest (1).",
    actions = { Action.ModelHighlight:new(Models.npcs["charos"], { instance = true }) },
    postconditions = { Condition.ConversationText:new("closer to the action") },
  },
  {
    text = "Move Hannibus to the marked tile.",
    actions = {
      Action.Direction:new(0, 0, -6, { instance = true, tile = true }),
      Action.ModelHighlight:new(Models.npcs["hannibus"], { instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Good luck") },
  },
  {
    text = "Move Hannibus back to his original tile.",
    actions = {
      Action.Direction:new(1, 0, -6, { instance = true, tile = true }),
      Action.ModelHighlight:new(Models.npcs["hannibus"], { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["hannibus"], { instance = true, atLocation = Location:new(2, 0, -6) }),
    },
  },
  {
    text = "Stand on the marked tile.",
    actions = { Action.Direction:new(-5, 0, -3, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-5, 0, -3, 0, true) },
  },
  {
    text = "Click the start button.<ul><li>Your character will automatically attack the dinosaurs.</li><li>If you struggle, move around to get early hits on the bigger dinosaurs.</li></ul>",
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Charos.",
    title = "Kerapac's laboratory",
    neededItems = {
      ["The Measure"] = { quantity = 1, model = Models.items["the measure"] },
      ["Combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["charos"]) },
    postconditions = { Condition.ConversationText:new("fight to Kerapac") },
  },
  {
    text = "Talk to Charos again to the north.<ul><li>If Charos doesn't give you The Measure, check your bank.</li></ul>",
    actions = { Action.Direction:new(5418, 2181, 2399) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["charos"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["charos"]),
      Action.ConversationHighlight:new(""),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["charos"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("you at the sites") },
  },
  {
    text = "Place The Measure on the marked tile.<ul><li>Lobby if Charos isn't near the marked tile for the next few steps.</li></ul>",
    actions = {
      Action.Direction:new(5427, 2181, 2410, { tile = true }),
      Action.InventoryHighlight:new(Models.items["the measure"]),
    },
    postconditions = { Condition.ConversationText:new("anything really useful") },
  },
  {
    text = "Place The Measure on the marked tile again.",
    actions = {
      Action.Direction:new(5489, 6877, 2488, { tile = true }),
      Action.InventoryHighlight:new(Models.items["the measure"]),
    },
    postconditions = { Condition.ConversationText:new("all in good time") },
  },
  {
    text = "Place The Measure on the marked tile yet again.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(5303, 7525, 2397, { tile = true }),
      Action.InventoryHighlight:new(Models.items["the measure"]),
    },
    postconditions = { Condition.ConversationText:new("specifically shaped") },
  },
  {
    text = "Place The Measure on the marked tile once again.",
    actions = {
      Action.Direction:new(5355, 9125, 2396, { tile = true }),
      Action.InventoryHighlight:new(Models.items["the measure"]),
    },
    postconditions = { Condition.ConversationText:new("company of friends") },
  },
  {
    text = "Talk to Charos north of the base camp once you're ready for combat.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    actions = { Action.Direction:new(5418, 2181, 2399) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["charos"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["charos"]),
      Action.ConversationHighlight:new(""),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["charos"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("door is right there") },
  },
  {
    text = "Enter Kerapac's lab.",
    actions = { Action.ModelHighlight:new(Models.objects["anachronia kerapac's lab door"]) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Enter the code <b>2-1-4</b> into the pylon.",
    actions = { Action.Direction:new(-37, -1124, 19, { instance = true }) },
    postconditions = { Condition.ChatText:new("symbols snap") },
  },
  {
    text = "North-western: <b>7-1-5</b>.",
    actions = { Action.Direction:new(-30, -1124, 53, { instance = true }) },
    postconditions = { Condition.ChatText:new("symbols snap") },
  },
  {
    text = "North-eastern: <b>4-1-6</b>.",
    actions = { Action.Direction:new(23, -164, 51, { instance = true }) },
    postconditions = { Condition.ChatText:new("symbols snap") },
  },
  {
    text = "South-eastern: <b>3-1-4</b>.",
    actions = { Action.Direction:new(45, -2692, 9, { instance = true }) },
    postconditions = { Condition.ChatText:new("symbols snap") },
  },
  {
    text = "Kill the black stone dragon to the north.",
    title = "Black stone dragon",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(0, -4576, 100, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(blackStoneDragon, { instance = true }) },
  },
  { postconditions = { Condition.ModelVisible:new(Models.npcs["kerapac"]) } },
  {
    text = "Talk to Kerapac.",
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("what is to come") },
  },
  {
    text = "Escape the lab.<ul><li>When you hit a dead end, turn around or interact with a nearby character.</li><li>If you fail to escape, re-enter Kerapac's lab to try again.</li></ul>",
    actions = {
      Action.ModelHighlight:new(Models.objects["anachronia kerapac's lab door"]),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Return to Seren in Burthorpe.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Burthorpe lodestone",
      url = "Burthorpe_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2901.5, 2965, 3570.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2901, 3877, 3564, 8),
      Condition.ModelVisible:new(Models.npcs["seren"]),
    },
  },
  {
    actions = { Action.Direction:new(2892, 3883, 3567) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["seren"]),
      Condition.QuestComplete:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["seren"]),
      Action.ConversationHighlight:new(""),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["seren"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Desperate Measures",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1595808000,
  prereqQuests = { "Desperate Times" },
  questReqs = {
    Types.QuestReq.skill("Agility", 50),
    Types.QuestReq.skill("Archaeology", 50),
  },
  neededItems = {
    ["Orthenglass (material storage container)"] = { quantity = 100, model = orthenglass, duringQuest = true },
    ["The Measure"] = { quantity = 1, model = Models.items["the measure"], duringQuest = true },
    ["Combat gear"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  recommendedItems = {
    ["The Heart teleport"] = { quantity = 1 },
    ["Grace of the elves"] = { quantity = 1 },
    ["Archaeology journal"] = { quantity = 1 },
    ["Phoenix Lair Teleport"] = { quantity = 1 },
  },
  combatNPCs = { ["Black stone dragon"] = { level = "91", quantity = 1 } },
})
