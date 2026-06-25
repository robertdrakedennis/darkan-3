local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local herman = Model.new(3978, {
  [3502] = Vertex.new(0, 735, -7, 29, 141, 129),
})

local wom = Models.npcs["wise old man"]

local seaTroll = Model.new(2997, {
  [3] = Vertex.new(8, 728, -204, 100, 68, 41),
})

local gate = Model.new(450, {
  [33] = Vertex.new(3612, 480, 3584, 56, 44, 29),
})

local franklin = Model.new(3939, {
  [3250] = Vertex.new(0, 735, -7, 29, 141, 129),
})

local firebox = Model.any({
  Model.new(1425, {
    [711] = Vertex.new(-512, 628, 188, 74, 66, 57),
  }),
  Model.new(996, {
    [282] = Vertex.new(-512, 628, 188, 74, 66, 57),
  }),
})

local metalPress = Model.new(1464, {
  [759] = Vertex.new(512, 472, 340, 74, 66, 57),
})

local ironSheet = Model.new(72, {
  [1] = Vertex.new(-156, 24, 196, 63, 58, 58),
})

local brokenWall = Model.any({
  Model.new(480, {
    [465] = Vertex.new(-132, 1089, -236, 113, 100, 87),
  }),
  Model.new(780, {
    [491] = Vertex.new(-148, 768, -244, 113, 100, 87),
  }),
  Model.new(342, {
    [215] = Vertex.new(-148, 348, -236, 113, 100, 87),
  }),
  Model.new(480, {
    [461] = Vertex.new(-120, 775, -244, 113, 100, 87),
  }),
  Model.new(780, {
    [491] = Vertex.new(-148, 773, -244, 113, 100, 87),
  }),
})

local arnold = Model.new(3573, {
  [2128] = Vertex.new(0, 735, -7, 29, 141, 129),
})

local fishingBubble = Model.new(1920, {
  [1441] = Vertex.new(-110, 180, 133, 128, 128, 128),
})

local monkfish = Model.new(501, {
  [1] = Vertex.new(-36, 112, -192, 22, 22, 2),
  [2] = Vertex.new(-76, 112, -204, 22, 22, 2),
  [3] = Vertex.new(-36, 136, -192, 22, 22, 2),
  [4] = Vertex.new(192, 20, 204, 88, 57, 46),
  [5] = Vertex.new(184, 20, 196, 88, 57, 46),
})

local freshMonkfish = Model.new(501, {
  [1] = Vertex.new(-36, 112, -192, 22, 22, 2),
  [2] = Vertex.new(-76, 112, -204, 22, 22, 2),
  [3] = Vertex.new(-36, 136, -192, 22, 22, 2),
  [4] = Vertex.new(192, 20, 204, 70, 56, 44),
  [5] = Vertex.new(184, 20, 196, 70, 56, 44),
})

local range = Model.new(5994, {
  [3199] = Vertex.new(2477, 593, 2988, 127, 127, 127),
})

local crafter = Model.new(20322, {
  [9949] = Vertex.new(128, 469, 39, 127, 127, 127),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Herman Caranos outside the Piscatoris Fishing Colony (fairy ring code AKQ or memory strand teleport, then run northeast).",
    title = "Trollish",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.Direction:new(2345, 485, 3650, { distance = 10 }),
      Action.ModelHighlight:new(herman, { distance = 11 }),
      Action.ConversationHighlight:new("What's the rush?"),
      Action.ConversationHighlight:new("Do you need any help?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to the Wise Old Man in Draynor.",
    title = "The Wise Old Man",
    neededItems = {
      ["Blood rune"] = { quantity = 5 },
      ["Mist rune"] = { quantity = 10 },
      ["Lava rune"] = {
        quantity = 10,
      },
    },
    recommendedItems = {},
    actions = {
      Action.Direction:new(3088, 1317, 3254, { distance = 5 }),
      Action.ModelHighlight:new(wom, { distance = 10 }),
      Action.ConversationHighlight:new("Would you like to go on a quest?"),
    },
    postconditions = { Condition.ConversationText:new("I'll see you outside the Colony") },
  },
  {
    text = "Head back to the Piscatoris Colony. Talk to the Wise Old Man.",
    actions = {
      Action.Direction:new(2345, 485, 3650, { distance = 10 }),
      Action.ModelHighlight:new(wom, { distance = 11 }),
      Action.ConversationHighlight:new("I'm ready to fight."),
    },
    postconditions = { Condition.InInstance:new() },
    warning = "Ensure chat is set to on or filtered.",
  },
  {
    text = "Kill the sea trolls and open the colony gate.",
    actions = { Action.ModelHighlight:new(seaTroll, { highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("Got them all!") },
  },
  {
    text = "Talk to Herman in the eastern building.",
    actions = { Action.ModelHighlight:new(gate) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = {
      Action.Direction:new(11, 320, 20, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(herman),
    },
    postconditions = { Condition.ConversationText:new("now please excuse us") },
  },

  {
    text = "Talk to Franklin Caranos near the entrance.",
    title = "Repairing the walls",
    neededItems = { ["Iron bar"] = { quantity = 7 }, ["Logs"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.Direction:new(-1, 32, 4, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(franklin),
    },
    postconditions = { Condition.ConversationText:new("Anyway, off you go") },
  },
  {
    text = "Use a log on the firebox and light it in the building directly north of Franklin Caranos.<ul><li>Logs and iron ores can be obtained just outside of the colony. </li></ul>",
    actions = {
      Action.Direction:new(3, 96, 9, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(firebox),
      Action.InventoryHighlight:new(Models.items["logs"], true),
    },
    postconditions = { Condition.ConversationText:new("The press is now hot enough") },
  },
  {
    text = "Use 5 iron bars on the Metal Press to get 5 iron sheets.",
    actions = { Action.ModelHighlight:new(metalPress), Action.InventoryHighlight:new(Models.items["iron bar"]) },
    postconditions = { Condition.InventoryContains:new(ironSheet, 5) },
  },
  {
    text = "Use the iron sheets on the broken colony walls to the west.",
    actions = {
      Action.Direction:new(-30, 80, 22, { instance = true, distance = 10 }),
      Action.InventoryHighlight:new(ironSheet),
      Action.ModelHighlight:new(brokenWall, { highlightPriority = "all" }),
    },
    postconditions = { Condition.ConversationText:new("is now fixed") },
  },
  {
    text = "Talk to Franklin.",
    actions = {
      Action.Direction:new(-1, 32, 4, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(franklin),
    },
    postconditions = { Condition.ConversationText:new("This is turning into") },
  },
  {
    title = "Fishing monkfish",
    text = "Right-click on Arnold Lydspor in the bank to talk to him.",
    actions = {
      Action.Direction:new(-12, 79, 24, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(arnold),
    },
    postconditions = { Condition.ConversationText:new("You go catch 5 fresh monkfish") },
  },
  {
    text = "Catch at least five fresh monkfish to the northwest. <ul><li>This can take up to 10 minutes depending on your Fishing level.</li><li>You'll be attacked by sea trolls when fishing.</li></ul>",
    actions = { Action.Direction:new(-21, -497, 36, { distance = 8 }), Action.ModelHighlight:new(fishingBubble) },
    postconditions = { Condition.InventoryContains:new(monkfish, 5) },
  },
  {
    text = "Cook the monkfish in the kitchen southwest. <ul><li>You will need to turn in five fresh monkfish; if any are burned, fish for more.</li><li>To cook them, just use them on the range and don't proceed on Make-X interface.</li>",
    actions = {
      Action.Direction:new(-26, 96, 7, { instance = true }),
      Action.ModelHighlight:new(range),
      Action.InventoryHighlight:new(monkfish),
    },
    postconditions = { Condition.InventoryContains:new(freshMonkfish, 5) },
  },
  {
    text = "Talk to Arnold.",
    actions = {
      Action.Direction:new(-12, 79, 24, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(arnold),
    },
    postconditions = { Condition.ConversationText:new("maybe he'll reward me") },
  },
  {
    text = "Talk to the Wise Old Man and Herman (furthest building to the east).",
    title = "Raising an army",
    neededItems = { ["Empty pot"] = { quantity = 1 }, ["Pot lid"] = { quantity = 1 }, ["Bones"] = { quantity = 7 } },
    actions = {
      Action.Direction:new(11, 320, 20, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(herman),
      Action.ConversationHighlight:new("Something else."),
    },
    postconditions = { Condition.ConversationText:new("Yes, please hurry") },
  },
  {
    text = "Talk to Wizard Frumscone in the basement of the Wizards' Guild in Yanille.<ul><li>Fetch the required supplies now, if you haven't already.</li></ul>",
    actions = {
      Action.Direction:new(2594, 965, 3085),
    },
    postconditions = { Condition.DistanceToWithHeight:new(2594, 1285, 9486, 5) },
  },
  {
    actions = {
      Action.ConversationHighlight:new("I'll see what the necromancer needs me to do."),
      Action.ModelHighlight:new(Models.npcs["wizard frumscone"]),
    },
    postconditions = { Condition.ConversationText:new("Hah! Necromancers aren't helpful people") },
  },
  {
    text = "Talk to Malignius Mortifer south-east of Clan Camp.",
    actions = {
      Action.Direction:new(3001, 469, 3266, { distance = 15 }),
      Action.ModelHighlight:new(Models.npcs["malignius mortifer"], { distance = 20 }),
      Action.ConversationHighlight:new("I need help with saving a fishing colony."),
    },
    postconditions = { Condition.ConversationText:new("You must go to the Crafting Guild") },
  },
  {
    text = "Talk to any Almost Master Crafter inside the Crafting Guild until one talks to you about crafting pot lids.",
    actions = { Action.Direction:new(2936, 1605, 3284, { distance = 8 }), Action.ModelHighlight:new(crafter) },
    postconditions = { Condition.ConversationText:new("You need to chill out more") },
  },

  {
    text = "Talk to Malignius Mortifer, he will teleport you to the colony, so gear up before speaking with him.",
    actions = {
      Action.Direction:new(3001, 469, 3266, { distance = 15 }),
      Action.ModelHighlight:new(Models.npcs["malignius mortifer"], { distance = 20 }),
      Action.ConversationHighlight:new("I've spoken to the master crafter..."),
    },
    postconditions = { Condition.DistanceTo:new(2345, 845, 3646, 20) },
  },
  {
    text = "Talk to Herman.",
    title = "The final battle",
    actions = {
      Action.Direction:new(2344, 485, 3651),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    actions = { Action.ModelHighlight:new(gate) },
    postconditions = { Condition.DistanceTo:new(-1, 0, 12, 1, true) },
  },
  {
    actions = {
      Action.Direction:new(9, 320, 30, { instance = true, distance = 5 }),
      Action.ModelHighlight:new(herman),
      Action.ConversationHighlight:new("I'm ready. Let's fight!"),
    },
    postconditions = { Condition.ConversationText:new("I can't move") },
  },
  {
    text = "Kill the Sea Troll Queen.<ul><li>Banks recover health rapidly, and while the bank isn't functional until quest completion, you can run into the building and next to the banker to recover health.</li></ul>",
    postconditions = { Condition.ConversationText:new("Now I've killed") },
  },

  {
    text = "Talk to Herman.",
    actions = { Action.Direction:new(2354, 805, 3680, { distance = 5 }), Action.ModelHighlight:new(herman) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Swan Song",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1146528000,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Cooking", 62),
    Types.QuestReq.skill("Crafting", 40),
    Types.QuestReq.skill("Firemaking", 42),
    Types.QuestReq.skill("Fishing", 62),
    Types.QuestReq.skill("Magic", 66),
    Types.QuestReq.skill("Smithing", 45),
  },
  neededItems = {
    ["Mist rune"] = { quantity = 10, model = Models.items["mist rune"] },
    ["Lava rune"] = { quantity = 10, model = Models.items["lava rune"] },
    ["Blood rune"] = { quantity = 5, model = Models.items["blood rune"] },
    ["Empty pot"] = { quantity = 1, model = Models.items["pot"] },
    ["Pot lid"] = { quantity = 1, model = Models.items["pot lid"] },
    ["Bones"] = { quantity = 7, model = Models.items["bones"], duringQuest = true },
    ["Iron bar"] = { quantity = 5, model = Models.items["iron bar"], duringQuest = true },
    ["Logs"] = { quantity = 1, model = Models.items["logs"], duringQuest = true },
  },
})
