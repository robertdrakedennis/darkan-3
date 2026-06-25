local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
--#endregion
--#region Objects
local dragonkinStatue = Model.new(780, {
  [3] = Vertex.new(328, 1525, 254, 127, 127, 127),
  [23] = Vertex.new(-348, 1544, 291, 127, 127, 127),
  [31] = Vertex.new(432, 561, -282, 127, 127, 127),
  [455] = Vertex.new(432, 561, -282, 127, 127, 127),
  [479] = Vertex.new(-432, 561, -282, 127, 127, 127),
})
local dragonkinStatueDoor = Model.new(2088, {
  [1645] = Vertex.new(670, 1279, 2205, 127, 127, 127),
  [1646] = Vertex.new(-718, 1279, 2205, 127, 127, 127),
  [1649] = Vertex.new(-718, 1279, 157, 127, 127, 127),
})
local dragonfleshObj = Model.new(3090, {
  [2791] = Vertex.new(1747, 2697, 3291, 127, 127, 127),
  [2875] = Vertex.new(1689, 2713, 3357, 127, 127, 127),
  [2887] = Vertex.new(1739, 2713, 3459, 127, 127, 127),
  [2905] = Vertex.new(1739, 2713, 3459, 127, 127, 127),
  [2911] = Vertex.new(1582, 2777, 3472, 127, 127, 127),
})
local animaNuggetsObj = Model.new(2622, {
  [1031] = Vertex.new(2046, 1106, 1495, 127, 127, 127),
  [1347] = Vertex.new(2058, 1141, 1421, 127, 127, 127),
  [1410] = Vertex.new(2049, 1143, 1458, 127, 127, 127),
  [1670] = Vertex.new(2049, 1143, 1458, 127, 127, 127),
  [1688] = Vertex.new(2049, 1143, 1458, 127, 127, 127),
})
local scrapMetalObj = Model.new(2382, {
  [2216] = Vertex.new(2445, 1081, 1993, 127, 127, 127),
  [2224] = Vertex.new(2481, 1081, 1841, 127, 127, 127),
  [2245] = Vertex.new(2577, 1081, 1841, 127, 127, 127),
  [2324] = Vertex.new(2445, 1081, 1993, 127, 127, 127),
  [2371] = Vertex.new(2445, 1081, 1993, 127, 127, 127),
})
local nicknacksObj = Model.new(2982, {
  [1410] = Vertex.new(3209, 2739, 1712, 127, 127, 127),
  [2437] = Vertex.new(3393, 2684, 1290, 128, 128, 127),
  [2503] = Vertex.new(3207, 2671, 1107, 128, 128, 127),
  [2515] = Vertex.new(3219, 2696, 1118, 128, 128, 127),
  [2875] = Vertex.new(3393, 2684, 1290, 128, 128, 127),
})
local effigyCasingObj = Model.new(14430, {
  [5835] = Vertex.new(720, 1120, 4282, 167, 167, 167),
  [5838] = Vertex.new(705, 1118, 4278, 167, 167, 167),
  [5841] = Vertex.new(720, 1120, 4282, 167, 167, 167),
  [6020] = Vertex.new(736, 1105, 4281, 167, 167, 167),
  [6023] = Vertex.new(737, 1090, 4275, 167, 167, 167),
})
local kerapacsWorkbench = Model.new(27066, {
  [100] = Vertex.new(819, 2116, 2980, 127, 127, 127),
  [142] = Vertex.new(1514, 2116, 2861, 127, 127, 127),
  [148] = Vertex.new(1493, 2116, 2889, 127, 127, 127),
  [4897] = Vertex.new(1594, 2144, 2929, 127, 127, 127),
  [5179] = Vertex.new(1593, 2086, 2953, 127, 127, 127),
})
local valve = Model.new(12420, {
  [11] = Vertex.new(-82, 1097, -173, 167, 167, 168),
  [14] = Vertex.new(-110, 1097, 173, 167, 167, 168),
  [21] = Vertex.new(-105, 881, -103, 167, 167, 168),
  [23] = Vertex.new(-110, 1097, -173, 167, 167, 168),
  [3799] = Vertex.new(-118, 1078, 170, 167, 167, 167),
})
local incubatorHatch = Model.new(2931, {
  [158] = Vertex.new(85, 656, 293, 127, 127, 128),
  [165] = Vertex.new(-69, 726, 587, 127, 127, 128),
  [249] = Vertex.new(89, 1030, 524, 127, 127, 128),
  [881] = Vertex.new(-87, 737, 594, 127, 127, 128),
  [886] = Vertex.new(-87, 737, 594, 127, 127, 128),
})
--#endregion
--#region Items
local enrichedDragonflesh = Model.new(900, {
  [625] = Vertex.new(-84, 78, 70, 127, 127, 127),
  [631] = Vertex.new(-84, 78, 70, 127, 127, 127),
  [697] = Vertex.new(105, 52, -55, 127, 127, 127),
  [721] = Vertex.new(159, 106, 68, 127, 127, 127),
  [727] = Vertex.new(159, 106, 68, 127, 127, 127),
})
local effigyCasing = Model.new(1530, {
  [799] = Vertex.new(0, 119, 78, 127, 127, 127),
  [970] = Vertex.new(0, 193, -26, 127, 127, 127),
  [1104] = Vertex.new(0, 193, -26, 127, 127, 127),
  [1165] = Vertex.new(0, 193, -26, 127, 127, 127),
  [1337] = Vertex.new(0, 193, -26, 127, 127, 127),
})
local volatileEffigy = Model.new(1530, {
  [799] = Vertex.new(0, 119, 78, 127, 127, 127),
  [970] = Vertex.new(0, 193, -26, 127, 127, 127),
  [1104] = Vertex.new(0, 193, -26, 127, 127, 127),
  [1165] = Vertex.new(0, 193, -26, 127, 127, 127),
  [1337] = Vertex.new(0, 193, -26, 127, 127, 127),
})
--#endregion
--#region Quest Items
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Mr Mordaut at the Anachronia base camp.",
    title = "Getting started",
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
      Action.ConversationHighlight:new("Talk about Kerapac."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["mr mordaut"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Mr Mordaut.",
    actions = { Action.ModelHighlight:new(Models.npcs["mr mordaut"]) },
    postconditions = { Condition.ConversationText:new("they saw") },
  },
  {
    text = "Talk to Eliza south of the lodestone.",
    title = "Walkthrough",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Anachronia lodestone",
      url = "Anachronia_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(5432, 6661, 2335) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["eliza"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["eliza"]),
      Action.ConversationHighlight:new("Kerapac"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["eliza"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("go and investigate") },
  },
  {
    text = "Investigate the Dragonkin statue to the north.",
    actions = { Action.Direction:new(5408, 5249, 2404) },
    postconditions = {
      Condition.ModelVisible:new(dragonkinStatue),
      Condition.ModelVisible:new(dragonkinStatueDoor),
    },
  },
  {
    actions = { Action.ModelHighlight:new(dragonkinStatue) },
    jumpconditions = { Condition.ModelNotVisible:new(dragonkinStatue) },
    jumpOffset = -1,
    postconditions = { Condition.ModelVisible:new(dragonkinStatueDoor) },
  },
  {
    text = "Enter the door.",
    actions = { Action.ModelHighlight:new(dragonkinStatueDoor) },
    postconditions = { Condition.DistanceTo:new(5513, 4581, 2971, 4) },
  },
  {
    text = "Talk to Vicendithas.",
    actions = { Action.Direction:new(5526, 2661, 2979) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["vicendithas"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["vicendithas"]),
      Action.ConversationHighlight:new("What are you doing here?"),
      Action.ConversationHighlight:new("Can I help with the machine?"),
      Action.ConversationHighlight:new("Play tutorial."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["vicendithas"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Vicendithas again.",
    actions = { Action.ModelHighlight:new(Models.npcs["vicendithas"]) },
    postconditions = { Condition.ConversationText:new("distil them") },
  },
  {
    text = "Collect 20 resources from one of the nodes.<ul><li>A chat box will appear. Confirm it or you won't be able to continue dialogue with Vicendithas.</li></ul>",
    warning = "Only dragonflesh is tracked as of now.",
    actions = {
      Action.ModelHighlight:new(dragonfleshObj, { instance = true }),
      Action.ModelHighlight:new(animaNuggetsObj, { instance = true }),
      Action.ModelHighlight:new(scrapMetalObj, { instance = true }),
      Action.ModelHighlight:new(nicknacksObj, { instance = true }),
    },
    postconditions = {
      Condition.InventoryContains:new(enrichedDragonflesh, 20),
      --add in items from other nodes
    },
  },
  {
    text = "Collect an effigy casing.<ul><li>Another chat box will appear. Confirm it too.</li></ul>",
    actions = { Action.ModelHighlight:new(effigyCasingObj, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(effigyCasing) },
  },
  {
    text = "Talk to Vicendithas.",
    actions = { Action.ModelHighlight:new(Models.npcs["vicendithas"]) },
    postconditions = { Condition.ConversationText:new("bring it") },
  },
  {
    text = "Go to Kerapac's workbench and construct the effigy.",
    warning = "Only the volatile effigy is tracked as of now.",
    actions = { Action.ModelHighlight:new(kerapacsWorkbench, { instance = true }) },
    postconditions = {
      Condition.InventoryContains:new(volatileEffigy),
      --add other effigies
    },
  },
  {
    text = "Talk to Vicendithas.",
    actions = { Action.ModelHighlight:new(Models.npcs["vicendithas"]) },
    postconditions = { Condition.ChatText:new("Turn the") },
  },
  {
    text = "Turn the valves in the order instructed.",
    actions = { Action.ModelHighlight:new(valve, { instance = true, highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("Excellent") },
  },
  {
    text = "Interact with the incubator hatch to deposit the effigy.",
    actions = { Action.ModelHighlight:new(incubatorHatch, { instance = true }) },
    postconditions = { Condition.ConversationText:new("incubate for you") },
  },
  {
    text = "Wait for the effigy to be processed.",
    postconditions = { Condition.ChatText:new("processing the effigies") },
  },
  {
    text = "Interact with the incubator hatch again.",
    actions = { Action.ModelHighlight:new(incubatorHatch, { instance = true }) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Talk to Vicendithas.",
    actions = { Action.ModelHighlight:new(Models.npcs["vicendithas"], { instanced = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Vicendithas again.",
    actions = { Action.ModelHighlight:new(Models.npcs["vicendithas"], { instanced = true }) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Sins of the Father",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1600041600,
  prereqQuests = { "Desperate Measures" },
  questReqs = {
    Types.QuestReq.misc("85 in Crafting, Runecrafting, Smithing, or Invention"),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
