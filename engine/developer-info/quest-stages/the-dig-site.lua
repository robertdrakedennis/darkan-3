local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCS
local digsiteExaminer = Model.new(4281, {
  [1437] = Vertex.new(24, 738, -38, 32, 30, 29),
  [1461] = Vertex.new(-24, 738, -38, 32, 30, 29),
  [1483] = Vertex.new(-2, 716, -57, 123, 103, 79),
  [1489] = Vertex.new(2, 716, -57, 123, 103, 79),
  [1493] = Vertex.new(6, 716, -52, 123, 103, 79),
})
local digsiteWorkman = Model.new(4257, {
  [1942] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [1947] = Vertex.new(-7, 724, -51, 109, 81, 57),
  [1950] = Vertex.new(2, 725, -59, 109, 81, 57),
  [1952] = Vertex.new(7, 724, -51, 109, 81, 57),
  [2437] = Vertex.new(0, 735, -7, 29, 141, 129),
})

local katarina = Model.new(2082, {
  [915] = Vertex.new(24, 738, -38, 30, 29, 27),
  [939] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [961] = Vertex.new(-2, 716, -57, 106, 78, 54),
  [967] = Vertex.new(2, 716, -57, 106, 78, 54),
  [971] = Vertex.new(6, 716, -52, 106, 78, 54),
})

local dorian = Model.new(8892, {
  [11] = Vertex.new(-172, 359, -118, 127, 127, 127),
  [15] = Vertex.new(-172, 353, -118, 127, 127, 127),
  [6040] = Vertex.new(0, 735, -7, 127, 128, 128),
  [6041] = Vertex.new(0, 732, -7, 127, 128, 128),
  [6042] = Vertex.new(0, 732, -8, 127, 128, 128),
})
local eduardo = Model.new(8847, {
  [121] = Vertex.new(-10, 731, 35, 127, 127, 127),
  [986] = Vertex.new(10, 731, 35, 127, 127, 127),
  [1270] = Vertex.new(132, 377, -18, 128, 127, 127),
  [1273] = Vertex.new(0, 735, -7, 127, 128, 128),
  [1275] = Vertex.new(0, 732, -8, 127, 128, 128),
})
local dougDeeping = Model.new(3729, {
  [2770] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2775] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2778] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2780] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3572] = Vertex.new(-30, 721, -31, 46, 35, 14),
})

-- Objects
local teddyBush = Model.new(108, {
  [9] = Vertex.new(3695, 719, 4111, 24, 74, 6),
  [26] = Vertex.new(3944, 856, 3922, 24, 74, 6),
  [30] = Vertex.new(3750, 768, 4160, 24, 74, 6),
  [62] = Vertex.new(3799, 819, 4150, 27, 83, 7),
  [64] = Vertex.new(3799, 819, 4150, 27, 83, 7),
})
local winch = Model.new(30, {
  [3] = Vertex.new(6099, -34, 5176, 127, 127, 127),
  [7] = Vertex.new(6706, -34, 5176, 127, 127, 127),
  [25] = Vertex.new(6706, -676, 5176, 127, 127, 127),
  [27] = Vertex.new(6706, -34, 5176, 127, 127, 127),
  [30] = Vertex.new(6706, -34, 5176, 127, 127, 127),
})
local filledBarrel = Model.new(1902, {
  [1250] = Vertex.new(54, 480, -152, 127, 127, 127),
  [1388] = Vertex.new(-20, 467, -172, 127, 127, 127),
  [1546] = Vertex.new(58, 467, -172, 127, 127, 127),
  [1556] = Vertex.new(58, 467, -172, 127, 127, 127),
  [1882] = Vertex.new(140, 417, -111, 125, 74, 25),
})

-- Quest Items
local teddyBear = Model.new(582, {
  [1] = Vertex.new(12, 188, -64, 59, 44, 24),
  [2] = Vertex.new(0, 180, -56, 59, 44, 24),
  [3] = Vertex.new(0, 192, -64, 59, 44, 24),
  [4] = Vertex.new(-12, 188, -64, 59, 44, 24),
  [7] = Vertex.new(-4, 204, -56, 59, 44, 24),
})
local panningTray = Model.new(144, {
  [6] = Vertex.new(108, 108, -100, 97, 90, 89),
  [12] = Vertex.new(164, 108, 0, 97, 90, 89),
  [54] = Vertex.new(-84, 108, 84, 97, 90, 89),
  [59] = Vertex.new(-84, 108, 84, 97, 90, 89),
  [115] = Vertex.new(-84, 108, 84, 83, 80, 77),
})
local specialCup = Model.any({
  Model.new(576, {
    [1] = Vertex.new(16, 120, 0, 108, 85, 10),
    [2] = Vertex.new(-4, 120, 16, 108, 85, 10),
    [3] = Vertex.new(12, 120, 12, 108, 85, 10),
    [5] = Vertex.new(-16, 120, 12, 108, 85, 10),
    [8] = Vertex.new(-20, 120, -4, 108, 85, 10),
  }),
  -- For some reason it's not detecting the special cup for me?
  Model.new(576, {
    [255] = Vertex.new(-68, 216, 4, 128, 109, 11),
    [305] = Vertex.new(-64, 192, -20, 128, 109, 11),
  }),
})
local animalSkull = Model.new(573, {
  [36] = Vertex.new(-36, 0, 104, 139, 127, 106),
  [38] = Vertex.new(-36, 0, 104, 139, 127, 106),
  [320] = Vertex.new(112, 108, 124, 96, 64, 19),
  [323] = Vertex.new(112, 108, 124, 96, 64, 19),
  [326] = Vertex.new(112, 108, 124, 96, 64, 19),
})

local ancientTalisman = Model.new(600, {
  [1] = Vertex.new(-132, 40, -4, 160, 124, 82),
  [2] = Vertex.new(-132, 0, -4, 160, 124, 82),
  [3] = Vertex.new(-104, 0, 4, 160, 124, 82),
  [12] = Vertex.new(-104, 40, 4, 160, 124, 82),
  [18] = Vertex.new(-92, 0, 0, 160, 124, 82),
})
local arceniaRoot = Model.new(84, {
  [43] = Vertex.new(-72, 0, 100, 71, 67, 65),
  [46] = Vertex.new(-72, 0, 100, 71, 67, 65),
  [47] = Vertex.new(-72, 20, 100, 71, 67, 65),
  [67] = Vertex.new(88, 0, -76, 71, 67, 65),
  [84] = Vertex.new(-72, 0, 100, 71, 67, 65),
})
local chestKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 152, 120, 13),
  [2] = Vertex.new(-12, 16, -84, 152, 120, 13),
  [3] = Vertex.new(-20, 16, -92, 152, 120, 13),
  [6] = Vertex.new(-60, 16, -52, 152, 120, 13),
  [7] = Vertex.new(-32, 16, -48, 152, 120, 13),
})
local ammoniumNitrate = Model.new(72, {
  [1] = Vertex.new(-44, 0, 36, 150, 138, 137),
  [2] = Vertex.new(0, 40, -8, 150, 138, 137),
  [3] = Vertex.new(-68, 0, 8, 150, 138, 137),
  [4] = Vertex.new(8, 0, 40, 150, 138, 137),
  [9] = Vertex.new(-60, 0, -32, 150, 138, 137),
})
local nitroglycerine = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
    [2] = Vertex.new(4, 112, -12, 113, 101, 45),
    [3] = Vertex.new(4, 96, -12, 113, 101, 45),
    [5] = Vertex.new(-4, 112, -12, 113, 101, 45),
    [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
    [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
    [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
  }),
})
-- The mixedChemicals1, mixedChemicals2, and chemicalCompound all have the same data
local mixedChemicals1 = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
    [2] = Vertex.new(4, 112, -12, 113, 101, 45),
    [3] = Vertex.new(4, 96, -12, 113, 101, 45),
    [5] = Vertex.new(-4, 112, -12, 113, 101, 45),
    [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
    [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
    [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
  }),
})
local mixedChemicals2 = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
    [2] = Vertex.new(4, 112, -12, 113, 101, 45),
    [3] = Vertex.new(4, 96, -12, 113, 101, 45),
    [5] = Vertex.new(-4, 112, -12, 113, 101, 45),
    [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
    [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
    [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
  }),
})
local chemicalCompound = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
    [2] = Vertex.new(4, 112, -12, 113, 101, 45),
    [3] = Vertex.new(4, 96, -12, 113, 101, 45),
    [5] = Vertex.new(-4, 112, -12, 113, 101, 45),
    [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
    [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
    [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
  }),
})
local stoneTablet = Model.new(126, {
  [1] = Vertex.new(-68, 232, 44, 78, 72, 72),
  [2] = Vertex.new(-12, 268, 0, 78, 72, 72),
  [3] = Vertex.new(-60, 232, -24, 78, 72, 72),
  [5] = Vertex.new(-32, 268, 32, 78, 72, 72),
  [7] = Vertex.new(60, 232, -24, 78, 72, 72),
})
local groundCharcoal = Model.new(72, {
  [1] = Vertex.new(-44, 0, 36, 30, 27, 27),
  [2] = Vertex.new(0, 40, -8, 30, 27, 27),
  [3] = Vertex.new(-68, 0, 8, 30, 27, 27),
  [4] = Vertex.new(8, 0, 40, 30, 27, 27),
  [9] = Vertex.new(-60, 0, -32, 30, 27, 27),
})

---@type QuestStep[]
local steps = {
  {
    text = "Speak to an examiner in the Exam Centre.",
    title = "Starting out",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Archaeology journal teleport",
      url = "Archaeology_journal.png",
    },
    actions = { Action.Direction:new(3355, 1477, 3345) },
    postconditions = { Condition.DistanceTo:new(3355, 1477, 3345, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(digsiteExaminer, { highlightPriority = "closest" }) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue talking to the examiner.",
    actions = { Action.ModelHighlight:new(digsiteExaminer, { highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationText:new("stamped letter back") },
  },
  {
    text = "Talk to Seth Minas (site manager) in Archaeology Guild.<ul><li>Make sure you have the unstamped letter otherwise the dialogue with Seth does not progress.</li></ul>",
    title = "Winging it",
    actions = { Action.Direction:new(3328, 1925, 3383) },
    postconditions = {
      Condition.DistanceTo:new(3328, 1925, 3383, 8),
    },
  },
  {
    actions = {
      Action.ConversationHighlight:new("The Dig Site quest."),
      Action.ModelHighlight:new(NPCs["seth minas"]),
    },
    postconditions = { Condition.ConversationText:new("I would like to see how you get on.") },
  },
  {
    text = "Grab a cup of tea from the desk in the south side of the building.",
    actions = { Action.ModelHighlight:new(Items["cup of tea"]) },
    postconditions = { Condition.InventoryContains:new(Items["cup of tea"]) },
  },
  {
    text = "Talk to an examiner in the Exam Centre; choose any option, you will fail the exam regardless.",
    actions = { Action.Direction:new(3355, 1477, 3345) },
    postconditions = { Condition.DistanceTo:new(3355, 1477, 3345, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(digsiteExaminer) },
    postconditions = { Condition.ConversationText:new("Why don't you use the resources here?") },
  },
  {
    text = "Search the green bushes to the west of the Monolith for a teddy bear.",
    title = "Lost and found",
    neededItems = { ["Cup of tea"] = { quantity = 1, model = Items["cup of tea"], duringQuest = true } },
    actions = { Action.Direction:new(3350, 421, 3382) },
    postconditions = { Condition.DistanceTo:new(3350, 421, 3382, 14) },
  },
  {
    actions = { Action.ModelHighlight:new(teddyBush) },
    postconditions = {
      Condition.InventoryContains:new(teddyBear),
      Condition.ConversationText:new("Hey, something has been dropped here..."),
    },
  },
  {
    text = "Take the panning tray from the small building south of the screening station.",
    actions = { Action.Direction:new(3377, 69, 3372) },
    postconditions = { Condition.DistanceTo:new(3377, 69, 3372, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(panningTray) },
    postconditions = { Condition.InventoryContains:new(panningTray) },
  },
  {
    text = "Try to pan the water to the east of where you found the panning tray; give the Panning Guide a cup of tea.",
    actions = {
      Action.ConversationHighlight:new("So how do I become invited then?"),
      Action.ModelHighlight:new(Objects["fishing bubble"]),
    },
    postconditions = {
      Condition.ConversationText:new("You're free to pan all you want."), --cup of tea
      Condition.ConversationText:new("You can pan all you want."), --nettle tea
    },
  },
  {
    text = "Pan until you find a special cup and for an uncut opal.",
    actions = { Action.ModelHighlight:new(Objects["fishing bubble"]) },
    postconditions = { Condition.InventoryContains:new(specialCup) },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["fishing bubble"]) },
    postconditions = {
      Condition.InventoryContains:new(Items["uncut opal"]),
      Condition.InventoryContains:new(Items["opal"]),
    },
  },
  {
    text = "Pickpocket the Dig Site workman in the north-east corner of the dig site until you get an animal skull.<ul><li>Pickpocket the Dig Site workman for 2 rope if you don't have any yet.</li></ul>",
    actions = { Action.Direction:new(3357, 5, 3422) },
    postconditions = { Condition.DistanceTo:new(3357, 5, 3422, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(digsiteWorkman) },
    postconditions = { Condition.InventoryContains:new(animalSkull) },
  },
  {
    text = "Talk to Katarina (student) in the central area of the dig site.",
    actions = {
      Action.Direction:new(3356, 5, 3426),
    },
    postconditions = {
      Condition.DistanceTo:new(3356, 5, 3426, 12),
    },
  },
  {
    actions = { Action.ModelHighlight:new(katarina) },
    postconditions = {
      Condition.ConversationText:new("Oh, great! Thanks!"),
    },
  },
  {
    text = "Talk to her again.",
    actions = { Action.ModelHighlight:new(katarina) },
    postconditions = {
      Condition.ConversationText:new("Great, thanks for your advice."),
    },
  },
  {
    text = "Run south-east, talk to Dorian (student).",
    actions = { Action.Direction:new(3378, 389, 3402) },
    postconditions = { Condition.DistanceTo:new(3378, 389, 3402, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(dorian) },
    postconditions = {
      Condition.ConversationText:new("I hope I didn't lose it in the water!"),
    },
  },
  {
    text = "Talk to him again.",
    actions = { Action.ModelHighlight:new(dorian) },
    postconditions = {
      Condition.ConversationText:new("Thanks for the information."),
    },
  },
  {
    text = "Run west, talk to Eduardo (student) who can be found in the dig spot closest to the well.",
    actions = { Action.Direction:new(3337, 413, 3408) },
    postconditions = { Condition.DistanceTo:new(3337, 413, 3408, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(eduardo) },
    postconditions = {
      Condition.ConversationText:new("Okay, I'll have a look for you."),
    },
  },
  {
    text = "Talk to him again.",
    actions = { Action.ModelHighlight:new(eduardo) },
    postconditions = {
      Condition.ConversationText:new("Okay, I'll remember that."),
    },
  },
  {
    text = "Return to an examiner.",
    title = "Exams",
    neededItems = { ["Opal"] = { quantity = 1, model = Items["opal"], duringQuest = true } },
    actions = { Action.Direction:new(3355, 1477, 3345) },
    postconditions = { Condition.DistanceTo:new(3355, 1477, 3345, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(digsiteExaminer),
      Action.ConversationHighlight:new("Yes, I certainly am."),
      Action.ConversationHighlight:new("The study of the earth, its contents and history."),
      Action.ConversationHighlight:new("All that have passed the appropriate Earth Sciences exam."),
      Action.ConversationHighlight:new("Proper tools must be used."),
    },
    postconditions = {
      Condition.ConversationText:new("Of course, you'll want to get studying for your next exam now!"),
    },
  },
  {
    text = "Talk to Eduardo (student) again.",
    actions = { Action.Direction:new(3337, 413, 3408) },
    postconditions = { Condition.DistanceTo:new(3337, 413, 3408, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(eduardo) },
    postconditions = {
      Condition.ConversationText:new("Okay, I'll remember that."),
    },
  },
  {
    text = "Talk to Katarina (student) again.",
    actions = { Action.Direction:new(3357, 5, 3425) },
    postconditions = {
      Condition.DistanceTo:new(3357, 5, 3425, 12),
    },
  },
  {
    actions = { Action.ModelHighlight:new(katarina) },
    postconditions = {
      Condition.ConversationText:new("Great, thanks for your advice."),
    },
  },
  {
    text = "Talk to Dorian (student) again.",
    actions = { Action.Direction:new(3378, 389, 3402) },
    postconditions = { Condition.DistanceTo:new(3378, 389, 3402, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(dorian) },
    postconditions = {
      Condition.ConversationText:new("Thanks for the information."),
    },
  },
  {
    text = "Return to an examiner.",
    actions = { Action.Direction:new(3355, 1477, 3345) },
    postconditions = { Condition.DistanceTo:new(3355, 1477, 3345, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(digsiteExaminer),
      Action.ConversationHighlight:new("I am ready for the next exam."),
      Action.ConversationHighlight:new("Samples taken in rough form; kept only in sealed containers."),
      Action.ConversationHighlight:new("Finds must be carefully handled."),
      Action.ConversationHighlight:new("Always handle with care; strike cleanly on its cleaving point."),
    },
    postconditions = {
      Condition.ConversationText:new("Of course, you'll want to get studying for your next exam now!"),
    },
  },
  {
    text = "Talk to Eduardo (student) again.",
    actions = { Action.Direction:new(3337, 413, 3408) },
    postconditions = { Condition.DistanceTo:new(3337, 413, 3408, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(eduardo) },
    postconditions = {
      Condition.ConversationText:new("Okay, I'll remember that. Thanks for all your help."),
    },
  },
  {
    text = "Talk to Katarina (student) again; she'll want an opal.",
    actions = { Action.Direction:new(3356, 5, 3426) },
    postconditions = {
      Condition.DistanceTo:new(3356, 5, 3426, 12),
    },
  },
  {
    actions = { Action.ModelHighlight:new(katarina) },
    postconditions = {
      -- This didn't work for me but I missed what she said.
      Condition.ConversationText:new("OK, I'll see what I can turn up for you."),
    },
  },
  {
    text = "Talk to her again.",
    actions = { Action.ModelHighlight:new(katarina) },
    postconditions = {
      Condition.ConversationText:new("Great, thanks for your advice."),
    },
  },
  {
    text = "Talk to Dorian (student) again.",
    actions = { Action.Direction:new(3378, 389, 3402) },
    postconditions = { Condition.DistanceTo:new(3378, 389, 3402, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(dorian) },
    postconditions = {
      Condition.ConversationText:new("Thanks for the information."),
    },
  },
  {
    text = "Return to an examiner.",
    actions = { Action.Direction:new(3355, 1477, 3345) },
    postconditions = { Condition.DistanceTo:new(3355, 1477, 3345, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(digsiteExaminer),
      Action.ConversationHighlight:new("I am ready for the last exam..."),
      Action.ConversationHighlight:new("Samples cleaned, and carried only in specimen jars."),
      Action.ConversationHighlight:new("Brush carefully and slowly using short strokes."),
      Action.ConversationHighlight:new("Handle bones very carefully and keep them away from other samples."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Perhaps you should use your newfound skills to find an artefact on the site that will impress Terry, our archaeological expert."
      ),
    },
  },
  {
    text = 'Optional: Talk to Seth Minas for a Fruit Blast, which is needed for the subquest "Recipe for Disaster: Another Cook\'s Quest."',
    actions = { Action.Direction:new(3328, 1925, 3383) },
    postconditions = {
      Condition.DistanceTo:new(3328, 1925, 3383, 8),
    },
  },
  {
    actions = {
      Action.ConversationHighlight:new("The Dig Site quest."),
      Action.ConversationHighlight:new("Something to drink, please."),
      Action.ModelHighlight:new(NPCs["seth minas"]),
    },
    postconditions = {
      Condition.ConversationText:new("Very good! Come and eat this cake I baked!"), -- In case if user chooses cake
      Condition.ConversationText:new("A cocktail? Cheers!"),
    },
  },
  {
    text = "Excavate the area on the ground named level 3 grid until you find an ancient talisman.",
    title = "Helping the expert",
    actions = { Action.Direction:new(3380, 341, 3401) },
    postconditions = { Condition.InventoryContains:new(ancientTalisman) },
    neededItems = {
      ["Specimen jar"] = { quantity = 1, model = Items["specimen jar"], duringQuest = true },
      ["Specimen brush"] = { quantity = 1, model = Items["specimen brush"], duringQuest = true },
      ["Rope"] = { quantity = 2, model = Items["rope"], duringQuest = true },
    },
  },
  {
    text = "Talk to Terry Balando back at the Exam Centre.",
    actions = { Action.Direction:new(3361, 1477, 3346) },
    postconditions = { Condition.DistanceTo:new(3361, 1477, 3346, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["terry balando"]) },
    postconditions = { Condition.ConversationText:new("You obviously have a keen eye.") },
  },
  {
    text = "Click the northern-eastern winch three times.",
    actions = { Action.Direction:new(3377, 117, 3444) },
    postconditions = {
      Condition.DistanceTo:new(3369, 1021, 9827, 8), --Not working
      Condition.ChatText:new("You have unlocked a new music track: Venture2."),
    },
  },
  {
    text = "Pick up an arcenia root.",
    actions = { Action.ModelHighlight:new(arceniaRoot) },
    postconditions = { Condition.InventoryContains:new(arceniaRoot) },
  },
  {
    text = "Search the bricks.",
    actions = { Action.Direction:new(3378, 1029, 9824) },
    postconditions = {
      Condition.ConversationText:new("That was a good idea"),
      Condition.ConversationText:new("That would be like cutting the lawn"),
      Condition.ChatText:new("Now, what am I trying to achieve here?"),
      -- None of the above worked. This is text I saw after clicking a second time on the bricks.
      Condition.ConversationText:new("room past these bricks."),
    },
  },
  {
    text = "Climb back up.",
    actions = { Action.Direction:new(3368, 1021, 9827) },
    postconditions = { Condition.DistanceTo:new(3377, 117, 3443, 8) },
  },
  {
    text = "Climb down the winch near Katarina to the southwest.",
    actions = { Action.Direction:new(3356, 5, 3433) },
    postconditions = { Condition.DistanceTo:new(3352, 2477, 9817, 8) }, --Not working, can't test it cause I finished the quest
  },
  {
    text = "Talk to Doug Deeping to receive a chest key.",
    actions = {
      Action.ModelHighlight:new(dougDeeping),
      Action.ConversationHighlight:new("How could I move a large pile of rocks?"),
    },
    postconditions = { Condition.InventoryContains:new(chestKey) },
  },
  {
    text = "Search the sack for an extra charcoal. Optional: Get extra for Desert Treasure.",
    actions = { Action.Direction:new(3353, 2525, 9823) },
    postconditions = { Condition.InventoryContains:new(Items["charcoal"]) },
    title = "Explosives expert",
    neededItems = {
      ["Chest key"] = { quantity = 1, model = chestKey, duringQuest = true },
      ["Vial"] = { quantity = 1, model = Items["vial"], duringQuest = false },
      ["Trowel"] = { quantity = 1, model = Items["trowel"], duringQuest = true },
    },
  },
  {
    text = "Climb back up.",
    actions = { Action.Direction:new(3352, 2477, 9816) },
    postconditions = { Condition.DistanceTo:new(3355, 61, 3433, 8) },
  },
  {
    text = "Go back to the northeastern winch and climb down.",
    actions = { Action.Direction:new(3377, 117, 3444) },
    postconditions = {
      Condition.DistanceTo:new(3369, 1021, 9827, 8),
    },
  },
  {
    text = "Use the key to unlock the chest in the north east room and search it for ammonium nitrate.",
    actions = { Action.Direction:new(3383, 933, 9845) },
    postconditions = {
      Condition.InventoryContains:new(ammoniumNitrate),
      Condition.ConversationText:new("You find some ammonium nitrate inside."),
    },
  },
  {
    text = "Use the trowel on the closed barrel directly east of the chest.",
    actions = { Action.InventoryHighlight:new(Items["trowel"]), Action.Direction:new(3389, 1245, 9847) },
    postconditions = { Condition.ModelVisible:new(filledBarrel) },
  },
  {
    text = "Use a vial on the barrel.",
    actions = { Action.InventoryHighlight:new(Items["vial"]), Action.ModelHighlight:new(filledBarrel) },
    postconditions = {
      Condition.InventoryContains:new(nitroglycerine),
      Condition.ConversationText:new("You fill the vial with some nitroglycerine."),
    },
  },
  {
    text = "Grind the charcoal.<ul><li>If you didn't bring charcoal, search the sack of charcoal a few steps north.</li></ul>",
    actions = { Action.InventoryHighlight:new(nitroglycerine) },
    postconditions = { Condition.InventoryContains:new(groundCharcoal) },
  },
  {
    text = "Mix the nitroglycerine and ammonium nitrate together.",
    actions = { Action.InventoryHighlight:new(nitroglycerine), Action.InventoryHighlight:new(ammoniumNitrate) },
    postconditions = {
      -- Commented out all these Inventory contains since they immediately skipped all these steps.
      --Condition.InventoryContains:new(mixedChemicals1),
      Condition.ChatText:new("It has produced a foul mixture."),
    },
  },
  {
    text = "Add ground charcoal to the mixed chemicals.",
    actions = {
      Action.InventoryHighlight:new(mixedChemicals1),
      Action.InventoryHighlight:new(groundCharcoal),
    },
    postconditions = {
      --Condition.InventoryContains:new(mixedChemicals2),
      Condition.ChatText:new("It has produced an even fouler mixture."),
    },
  },
  {
    text = "Add arcenia root to the mixture.",
    actions = {
      Action.InventoryHighlight:new(mixedChemicals2),
      Action.InventoryHighlight:new(arceniaRoot),
    },
    postconditions = {
      --Condition.InventoryContains:new(chemicalCompound),
      Condition.ConversationText:new("Excellent! This looks just right!"),
    },
  },
  {
    text = "Search the bricks to the south and select the compound on the brick.",
    actions = { Action.Direction:new(3378, 1029, 9824) },
    postconditions = {
      Condition.ConversationText:new(
        "Okay, the mixture is all over the bricks. I need some way to ignite this compound."
      ),
    },
  },
  {
    text = "Search the brick again, select your tinderbox.",
    actions = { Action.Direction:new(3378, 1029, 9824) },
    postconditions = { Condition.DistanceTo:new(3494, 797, 10087, 8) },
  },
  {
    text = "Enter the large chamber, take the stone tablet.",
    actions = { Action.Direction:new(3501, 549, 10067) },
    postconditions = { Condition.InventoryContains:new(stoneTablet) },
  },
  {
    text = "Use the tablet on Terry Balando in the Exam Centre.",
    title = "Finishing up",
    actions = { Action.Direction:new(3361, 1477, 3346) },
    postconditions = { Condition.DistanceTo:new(3361, 1477, 3346, 10) },
    neededItems = {
      ["Stone tablet (The Dig Site)"] = { quantity = 1, model = stoneTablet, duringQuest = true },
    },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["terry balando"]), Action.InventoryHighlight:new(stoneTablet) },
    postconditions = { Condition.ConversationText:new("You obviously have a keen eye."), Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Dig Site",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1057708800,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Agility", 10),
    Types.QuestReq.skill("Herblore", 10),
    Types.QuestReq.skill("Thieving", 25),
  },
  neededItems = {
    ["Charcoal"] = { quantity = 1, model = Items["charcoal"], duringQuest = true },
    ["Cup of tea"] = { quantity = 1, model = Items["cup of tea"], duringQuest = true },
    ["Vial"] = { quantity = 1, model = Items["vial"], duringQuest = false },
    ["Rope"] = { quantity = 2, model = Items["rope"], duringQuest = true },
    ["Uncut opal/Opal"] = { quantity = 1, model = Items["uncut opal"], duringQuest = true },
  },
})
