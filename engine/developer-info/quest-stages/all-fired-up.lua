local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Objects
local unlitBeacon1 = Model.new(3444, {
  [441] = Vertex.new(-229, 2448, -430, 203, 190, 189),
  [1821] = Vertex.new(408, 1090, 482, 70, 77, 92),
  [2229] = Vertex.new(405, 1548, -550, 70, 77, 92),
  [2544] = Vertex.new(-372, 1269, -404, 56, 61, 73),
  [3027] = Vertex.new(405, 1548, -550, 70, 77, 92),
})
local unlitBeaconL1 = Model.new(4302, {
  [1497] = Vertex.new(408, 1090, 482, 70, 77, 92),
  [2127] = Vertex.new(405, 1548, -550, 70, 77, 92),
  [2217] = Vertex.new(-372, 1269, -404, 56, 61, 73),
  [2805] = Vertex.new(405, 1548, -550, 70, 77, 92),
  [3465] = Vertex.new(-229, 2448, -430, 203, 190, 189),
})
local litBeacon = Model.new(789, {
  [781] = Vertex.new(-81, 1767, 310, 194, 193, 200, 0.000),
  [782] = Vertex.new(154, 1767, 310, 194, 193, 200, 0.000),
  [787] = Vertex.new(-314, 1839, -238, 194, 193, 200, 0.000),
  [788] = Vertex.new(-31, 1839, 330, 194, 193, 200, 0.000),
  [789] = Vertex.new(289, 1839, -174, 194, 193, 200, 0.000),
})
local unlitBeacon2 = Model.new(7890, {
  [73] = Vertex.new(-680, 1322, -378, 127, 127, 127),
  [109] = Vertex.new(-284, 1322, -594, 127, 127, 127),
  [121] = Vertex.new(-862, 1322, 54, 127, 127, 127),
  [373] = Vertex.new(-680, 1322, -378, 127, 127, 127),
  [1234] = Vertex.new(-680, 1322, -378, 127, 127, 127),
})
local logsInBeacon = Model.new(1170, {
  [474] = Vertex.new(-564, 409, 134, 127, 127, 127),
  [525] = Vertex.new(-272, 409, 370, 127, 127, 127),
  [749] = Vertex.new(-144, 409, -318, 73, 68, 62),
  [755] = Vertex.new(-564, 409, 134, 73, 68, 62),
  [1134] = Vertex.new(-524, 409, -150, 106, 91, 79),
})
local litLogsInBeacon = Model.new(789, {
  [781] = Vertex.new(-278, 663, 354, 162, 149, 151, 0.000),
  [784] = Vertex.new(-426, 720, 32, 162, 149, 151, 0.000),
  [786] = Vertex.new(-191, 720, -203, 162, 149, 151, 0.000),
  [787] = Vertex.new(-511, 735, -194, 162, 149, 151, 0.000),
  [788] = Vertex.new(-228, 735, 374, 162, 149, 151, 0.000),
})
local dyingBeacon = Model.new(4302, {
  [1437] = Vertex.new(408, 1090, 482, 70, 77, 92),
  [2067] = Vertex.new(405, 1548, -550, 70, 77, 92),
  [2157] = Vertex.new(-372, 1269, -404, 56, 61, 73),
  [2745] = Vertex.new(405, 1548, -550, 70, 77, 92),
  [3405] = Vertex.new(-229, 2448, -430, 203, 190, 189),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to King Roald in Varrock Palace.",
    title = "Getting Started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3222, 1253, 3472) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["king roald"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["king roald"]),
      Action.ConversationHighlight:new("Greet the king."),
      Action.ConversationHighlight:new("Oh? Tell me more!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["king roald"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to King Roald.",
    actions = { Action.ModelHighlight:new(Models.npcs["king roald"]) },
    postconditions = { Condition.ConversationText:new("in your hands") },
  },
  {
    text = "Talk to Blaze Sharpeye in Silvarea.",
    title = "Tending the Beacons",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    neededItems = { ["Logs"] = { quantity = 20 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3394, 4821, 3469) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["blaze sharpeye"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["blaze sharpeye"]),
      Action.ConversationHighlight:new("Does it matter what type of log I use?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["blaze sharpeye"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("well use normal logs") },
  },
  {
    text = "Add-logs to the unlit beacon (20 logs).",
    actions = {
      Action.ModelHighlight:new(unlitBeacon1),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = {
      Condition.ModelVisible:new(unlitBeaconL1),
      Condition.ModelVisible:new(litBeacon),
    },
  },
  {
    text = "Light the beacon.",
    actions = { Action.ModelHighlight:new(unlitBeaconL1) },
    postconditions = { Condition.ModelVisible:new(litBeacon) },
  },
  {
    text = "Talk to Blaze again.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["blaze sharpeye"]),
      Action.ConversationHighlight:new("Okay."),
    },
    postconditions = { Condition.ConversationText:new("Okay.") },
  },
  {
    text = "Acquire 20 more logs by chopping or banking.",
    postconditions = { Condition.InventoryContains:new(Models.items["logs"], 20) },
  },
  {
    text = "Add-logs to the unlit beacon near the Odd Old Man in Silvarea (20 logs).",
    actions = { Action.Direction:new(3344, 3941, 3511) },
    postconditions = { Condition.ModelVisible:new(unlitBeacon2, { atLocation = Location:new(3344, 3941, 3511) }) },
  },
  {
    actions = {
      Action.ModelHighlight:new(unlitBeacon2, { atLocation = Location:new(3344, 3941, 3511) }),
      Action.ConversationHighlight:new("Yes."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(unlitBeacon2) },
    jumpOffset = -1,
    postconditions = {
      Condition.ModelVisible:new(logsInBeacon),
      Condition.ModelVisible:new(litLogsInBeacon),
    },
  },
  {
    text = "Light the beacon.",
    actions = { Action.ModelHighlight:new(unlitBeacon2, { atLocation = Location:new(3344, 3941, 3511) }) },
    postconditions = { Condition.ModelVisible:new(litLogsInBeacon) },
  },
  {
    text = "Return to Blaze.",
    actions = { Action.Direction:new(3394, 4821, 3469) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["blaze sharpeye"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["blaze sharpeye"]),
      Action.ConversationHighlight:new("Oh, alright, then."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["blaze sharpeye"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Backing away now") },
  },
  {
    text = "Add-logs to the beacon (5 logs).",
    actions = {
      Action.ModelHighlight:new(dyingBeacon),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ChatText:new("back to life") },
  },
  {
    text = "Talk to Blaze.",
    title = "Finishing up",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["blaze sharpeye"]),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("right on that") },
  },
  {
    text = "Return to King Roald.",
    actions = { Action.Direction:new(3222, 1253, 3472) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["king roald"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["king roald"]),
      Action.ConversationHighlight:new("Talk about All Fired Up."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["king roald"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "All Fired Up",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1219708800,
  prereqQuests = { "Priest in Peril" },
  questReqs = { Types.QuestReq.skill("Firemaking", 43) },
  neededItems = { ["Logs"] = { quantity = 45, model = Models.items["logs"], duringQuest = true } },
  recommendedItems = {},
  combatNPCs = {},
})
