local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local grocerHannah = Model.new(7635, {
  [510] = Vertex.new(-32, 622, -50, 127, 127, 127),
  [568] = Vertex.new(46, 609, -57, 127, 127, 127),
  [593] = Vertex.new(59, 485, -31, 127, 127, 128),
  [643] = Vertex.new(-59, 485, -31, 127, 127, 128),
  [1135] = Vertex.new(-46, 609, -57, 127, 127, 127),
})
local shelly = Model.new(2106, {
  [626] = Vertex.new(-48, 44, 90, 56, 56, 61),
  [951] = Vertex.new(-48, 44, 90, 61, 61, 66),
  [983] = Vertex.new(-48, 44, 90, 56, 56, 61),
  [1101] = Vertex.new(50, 44, 82, 61, 61, 66),
  [1103] = Vertex.new(72, 58, 92, 61, 61, 66),
})
local hermit = Model.new(3261, {
  [2224] = Vertex.new(-2, 725, -59, 136, 118, 105),
  [2229] = Vertex.new(-7, 724, -51, 136, 118, 105),
  [2232] = Vertex.new(2, 725, -59, 136, 118, 105),
  [2234] = Vertex.new(7, 724, -51, 136, 118, 105),
  [2707] = Vertex.new(0, 735, -7, 30, 142, 129),
})
local crab = Model.new(2106, {
  [626] = Vertex.new(-48, 44, 90, 111, 50, 23),
  [951] = Vertex.new(-48, 44, 90, 113, 57, 35),
  [983] = Vertex.new(-48, 44, 90, 111, 50, 23),
  [1101] = Vertex.new(50, 44, 82, 113, 57, 35),
  [1103] = Vertex.new(72, 58, 92, 113, 57, 35),
})
local kai = Model.new(2106, {
  [626] = Vertex.new(-72, 66, 135, 111, 50, 23),
  [951] = Vertex.new(-72, 66, 135, 113, 57, 35),
  [983] = Vertex.new(-72, 66, 135, 111, 50, 23),
  [1101] = Vertex.new(75, 66, 123, 113, 57, 35),
  [1103] = Vertex.new(108, 87, 138, 113, 57, 35),
})
local finn = Model.new(2106, {
  [626] = Vertex.new(-72, 66, 135, 56, 56, 61),
  [951] = Vertex.new(-72, 66, 135, 61, 61, 66),
  [983] = Vertex.new(-72, 66, 135, 56, 56, 61),
  [1101] = Vertex.new(75, 66, 123, 61, 61, 66),
  [1103] = Vertex.new(108, 87, 138, 61, 61, 66),
})
local sandy = Model.new(2106, {
  [626] = Vertex.new(-72, 66, 135, 80, 51, 25),
  [951] = Vertex.new(-72, 66, 135, 87, 55, 36),
  [983] = Vertex.new(-72, 66, 135, 80, 51, 25),
  [1101] = Vertex.new(75, 66, 123, 87, 55, 36),
  [1103] = Vertex.new(108, 87, 138, 87, 55, 36),
})
local villager = Model.any({
  Model.new(7623, {
    [2155] = Vertex.new(0, 735, -7, 27, 139, 126),
    [2156] = Vertex.new(0, 732, -7, 27, 139, 126),
    [2157] = Vertex.new(0, 732, -8, 27, 139, 126),
    [2971] = Vertex.new(-132, 377, -18, 128, 127, 127),
    [2974] = Vertex.new(132, 377, -18, 128, 127, 127),
  }),
  Model.new(6255, {
    [3691] = Vertex.new(0, 735, -7, 127, 128, 127),
    [3692] = Vertex.new(0, 732, -7, 127, 128, 127),
    [3693] = Vertex.new(0, 732, -8, 127, 128, 127),
    [6250] = Vertex.new(-132, 377, -18, 128, 127, 127),
    [6253] = Vertex.new(132, 377, -18, 128, 127, 127),
  }),
  Model.new(5313, {
    [3055] = Vertex.new(120, 554, 28, 127, 127, 127),
    [3224] = Vertex.new(-120, 554, 28, 127, 127, 127),
    [3793] = Vertex.new(0, 735, -7, 127, 128, 127),
    [3794] = Vertex.new(0, 732, -7, 127, 128, 127),
    [3795] = Vertex.new(0, 732, -8, 127, 128, 127),
  }),
  Model.new(9885, {
    [9877] = Vertex.new(0, 735, -7, 27, 139, 126),
    [9878] = Vertex.new(0, 732, -7, 27, 139, 126),
    [9879] = Vertex.new(0, 732, -8, 27, 139, 126),
    [9880] = Vertex.new(-126, 380, -15, 27, 139, 30),
    [9881] = Vertex.new(-124, 380, -17, 27, 139, 30),
  }),
  Model.new(6231, {
    [2318] = Vertex.new(60, 485, -31, 121, 128, 133),
    [2368] = Vertex.new(-60, 485, -31, 121, 128, 133),
    [2489] = Vertex.new(-53, 618, -58, 76, 80, 83),
    [2545] = Vertex.new(17, 490, 36, 76, 80, 83),
    [4577] = Vertex.new(53, 618, -58, 76, 80, 83),
  }),
})
--#endregion
--#region Objects
local villageNoticeboard = Model.new(1050, {
  [1015] = Vertex.new(58, 565, -474, 127, 127, 127),
  [1035] = Vertex.new(72, 883, -471, 127, 127, 127),
  [1037] = Vertex.new(-63, 883, 451, 127, 127, 127),
  [1046] = Vertex.new(83, 879, -470, 127, 127, 127),
  [1049] = Vertex.new(-51, 879, 452, 127, 127, 127),
})
local driftwood = Model.any({
  Model.new(660, {
    [17] = Vertex.new(257, 117, 162, 127, 127, 127),
    [63] = Vertex.new(-296, 28, 218, 127, 127, 127),
    [460] = Vertex.new(271, 149, -29, 127, 127, 127),
    [463] = Vertex.new(271, 149, -29, 127, 127, 127),
    [476] = Vertex.new(271, 149, -29, 127, 127, 127),
  }),
  Model.new(660, {
    [37] = Vertex.new(-188, 106, 288, 127, 127, 127),
    [63] = Vertex.new(-296, 77, 218, 127, 127, 127),
    [178] = Vertex.new(-192, 125, 282, 127, 127, 127),
    [183] = Vertex.new(-192, 125, 282, 127, 127, 127),
    [186] = Vertex.new(-192, 125, 282, 127, 127, 127),
  }),
  Model.new(660, {
    [63] = Vertex.new(-296, 81, 218, 127, 127, 127),
    [66] = Vertex.new(-296, 81, 218, 127, 127, 127),
    [67] = Vertex.new(-296, 81, 218, 127, 127, 127),
    [80] = Vertex.new(-296, 58, 225, 127, 127, 127),
    [577] = Vertex.new(-304, 58, 219, 127, 127, 127),
  }),
})

--#endregion
--#region Quest Items
local freshCrabClaw = Model.new(252, {
  [59] = Vertex.new(-136, 4, 48, 119, 61, 37),
  [69] = Vertex.new(-136, 4, 48, 119, 61, 37),
  [71] = Vertex.new(-168, 12, 28, 119, 61, 37),
  [87] = Vertex.new(-168, 12, 28, 119, 61, 37),
  [96] = Vertex.new(-168, 12, 28, 119, 61, 37),
})
local crabAmuletU = Model.new(1470, {
  [167] = Vertex.new(15, 0, 11, 128, 127, 127),
  [190] = Vertex.new(15, 0, 10, 128, 127, 127),
  [653] = Vertex.new(13, 1, 8, 128, 127, 127),
  [844] = Vertex.new(-15, 0, 11, 128, 127, 127),
  [868] = Vertex.new(-15, 0, 10, 128, 127, 127),
})
local crabAmulet = Model.new(1830, {
  [733] = Vertex.new(12, 1, -49, 128, 127, 127),
  [1213] = Vertex.new(11, 1, -54, 128, 127, 127),
  [1270] = Vertex.new(14, 1, -52, 128, 127, 127),
  [1274] = Vertex.new(-11, 1, -54, 128, 127, 127),
  [1330] = Vertex.new(-14, 1, -52, 128, 127, 127),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Read the noticeboard north of the Wendlewick pub.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3490.5, 8205, 1516.5) },
    postconditions = { Condition.ModelVisible:new(villageNoticeboard) },
  },
  {
    actions = {
      Action.ModelHighlight:new(villageNoticeboard),
      Action.ConversationHighlight:new("HELP NEEDED"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(villageNoticeboard) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue reading the noticeboard.",
    actions = { Action.ModelHighlight:new(villageNoticeboard) },
    postconditions = { Condition.ConversationText:new("see what the problem") },
  },
  {
    text = "Talk to Grocer Hannah at the Wendlewick food shop.",
    title = "Crabulet",
    neededItems = { ["Ball of wool"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3492, 8357, 1545) },
    postconditions = { Condition.ModelVisible:new(grocerHannah) },
  },
  {
    actions = {
      Action.ModelHighlight:new(grocerHannah),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
      Action.ConversationHighlight:new("That is quite a lot of crabs"),
      Action.ConversationHighlight:new("I could try herding them out?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(grocerHannah) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("to shreds") },
  },
  {
    actions = {
      Action.ModelHighlight:new(grocerHannah),
      Action.ConversationHighlight:new("What if I threatened them?"),
    },
    postconditions = { Condition.ConversationText:new("No reaction") },
  },
  {
    actions = {
      Action.ModelHighlight:new(grocerHannah),
      Action.ConversationHighlight:new("I could just kill them all"),
    },
    postconditions = { Condition.ConversationText:new("Okay then.") },
  },
  {
    text = "Talk to Shelly, the crab in the store.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ConversationText:new("Progress") },
  },
  {
    text = "Follow Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ModelVisible:new(shelly, { atLocation = Location:new(3477, 8165, 1563) }) },
  },
  {
    text = "Talk to Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ConversationText:new("trip it is") },
  },
  {
    text = "Follow Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ModelVisible:new(shelly, { atLocation = Location:new(3455, 7589, 1591) }) },
  },
  {
    text = "Talk to Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ConversationText:new("Onwards") },
  },
  {
    text = "Follow Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ModelVisible:new(shelly, { atLocation = Location:new(3440, 365, 1597) }) },
  },
  {
    text = "Talk to Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ConversationText:new("to nod") },
  },
  {
    text = "Follow Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ModelVisible:new(shelly, { atLocation = Location:new(3450, 1005, 1622) }) },
  },
  {
    text = "Talk to Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ConversationText:new("take too long") },
  },
  {
    text = "Enter the cave.",
    actions = { Action.Direction:new(3456, 1881, 1620) },
    postconditions = { Condition.DistanceTo:new(3426, 767, 8011, 4) },
  },
  {
    text = "Talk to Hermit.",
    actions = { Action.ModelHighlight:new(hermit) },
    postconditions = { Condition.ConversationText:new("to me once you are done") },
  },
  {
    text = "Kill a crab.",
    actions = { Action.ModelHighlight:new(crab, { highlightPriority = "closest" }) },
    postconditions = { Condition.ModelVisible:new(freshCrabClaw) },
  },
  {
    text = "Pick up the fresh crab claw.",
    actions = { Action.ModelHighlight:new(freshCrabClaw) },
    postconditions = { Condition.InventoryContains:new(freshCrabClaw) },
  },
  {
    text = "Craft the fresh claw into an crab amulet.",
    actions = { Action.InventoryHighlight:new(freshCrabClaw) },
    postconditions = { Condition.InventoryContains:new(crabAmuletU) },
  },
  {
    text = "Use a ball of wool on the crab amulet.",
    actions = {
      Action.InventoryHighlight:new(Models.items["ball of wool"]),
      Action.InventoryHighlight:new(crabAmuletU),
    },
    postconditions = { Condition.InventoryContains:new(crabAmulet) },
  },
  {
    text = "Talk to Hermit again.",
    actions = {
      Action.ModelHighlight:new(hermit),
      Action.ConversationHighlight:new(""),
    },
    postconditions = {
      Condition.ConversationText:new("power of crab"),
      Condition.ConversationText:new("power of Crab"),
    },
  },
  {
    text = "Equip the crabulet.",
    actions = { Action.InventoryHighlight:new(crabAmulet) },
    postconditions = { Condition.InventoryDoesNotContain:new(crabAmulet) },
  },
  {
    text = "Talk to Shelly.",
    actions = { Action.ModelHighlight:new(shelly) },
    postconditions = { Condition.ConversationText:new("meet you there") },
  },
  {
    text = "Talk to Shelly at the Wendlewick food shop.",
    title = "Huts",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    neededItems = {
      ["Crabulet"] = { quantity = 1 },
      ["Seaweed"] = { quantity = 6 },
      ["Ropes"] = { quantity = 3 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(3492, 8357, 1545) },
    postconditions = { Condition.ModelVisible:new(shelly) },
  },
  {
    actions = { Action.ModelHighlight:new(shelly) },
    jumpconditions = { Condition.ModelNotVisible:new(shelly) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("piece of our mind") },
  },
  {
    text = "Talk to any of the crab leaders on the beach south of the Shrine of Inanna.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "BKS",
    },
    actions = { Action.Direction:new(3568, 349, 1338) },
    postconditions = { Condition.ModelVisible:new(kai) },
  },
  {
    actions = {
      Action.ModelHighlight:new(kai),
      Action.ModelHighlight:new(sandy),
      Action.ModelHighlight:new(finn),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(kai) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("planning permission for your") },
  },
  {
    text = "Talk to Adam north of the Wendlewick market.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3482, 8613, 1571) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["adam"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adam"]),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["adam"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("your adventures") },
  },
  {
    text = "Talk to Zeke in the building to the east.",
    actions = { Action.Direction:new(3496, 9465, 1565) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["zeke"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["zeke"]),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["zeke"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("That was easy") },
  },
  {
    text = "Climb to the top of the Wendlewick lighthouse.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Wendlewick lodestone",
      url = "Wendlewick_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3450, 7837, 1495.4) },
    postconditions = { Condition.DistanceToWithHeight:new(3454, 11509, 1495, 4) },
  },
  {
    text = "Talk to Esther.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["esther"]),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
    },
    postconditions = { Condition.ConversationText:new("from the locals") },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(3452.5, 11517, 1495.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3452, 7237, 1494, 4) },
  },
  {
    text = "Talk to 5 different villagers/stall owners around Wendlewick.",
    actions = {
      Action.ModelHighlight:new(villager, { highlightPriority = "all" }),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
    },
    postconditions = {
      Condition.ChatText:new("5 / 5 signatures"),
      Condition.ChatText:new("5/5 signatures"),
    },
  },
  {
    text = "Return to Adam.",
    actions = { Action.Direction:new(3482, 8613, 1571) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["adam"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adam"]),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["adam"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("know the good news") },
  },
  {
    text = "Collect 6 pieces from the beach west of Wendlewick.",
    actions = { Action.Direction:new(3403, 229, 1549) },
    postconditions = {
      Condition.ModelVisible:new(Models.items["seaweed"]),
      Condition.InventoryContains:new(Models.items["seaweed"], 6),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.items["seaweed"], { highlightPriority = "all" }) },
    postconditions = { Condition.InventoryContains:new(Models.items["seaweed"], 6) },
  },
  {
    text = "Return to the crab leaders.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "BKS",
    },
    actions = { Action.Direction:new(3568, 349, 1338) },
    postconditions = { Condition.ModelVisible:new(kai) },
  },
  {
    actions = {
      Action.ModelHighlight:new(kai),
      Action.ModelHighlight:new(sandy),
      Action.ModelHighlight:new(finn),
      Action.ConversationHighlight:new("Talk about 'Hermit Permits'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(kai) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("seaweed per hut") },
  },
  {
    text = "Build the three piles of driftwood on the beach into their huts.",
    actions = { Action.ModelHighlight:new(driftwood, { highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("crab leaders know") },
  },
  {
    text = "Talk to the crab leaders.",
    title = "Finishing up",
    actions = {
      Action.ModelHighlight:new(kai),
      Action.ModelHighlight:new(sandy),
      Action.ModelHighlight:new(finn),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Hermit Permits",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1774278419,
  prereqQuests = { "Visions of Havenhythe" },
  questReqs = {
    Types.QuestReq.skill("Construction", 5),
    Types.QuestReq.skill("Crafting", 15),
  },
  neededItems = {
    ["Ball of wool"] = { quantity = 1, model = Models.items["ball of wool"] },
    ["Seaweed"] = { quantity = 6, model = Models.items["seaweed"], duringQuest = true },
    ["Ropes"] = { quantity = 3, model = Models.items["rope"] },
  },
  recommendedItems = {},
  combatNPCs = { ["Crab"] = { level = "29", quantity = 1 } },
})
