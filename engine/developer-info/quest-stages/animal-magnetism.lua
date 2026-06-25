local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local alice = Model.new(4305, {
  [2601] = Vertex.new(24, 738, -38, 30, 29, 27),
  [2625] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [2647] = Vertex.new(-2, 716, -57, 106, 78, 54),
  [2653] = Vertex.new(2, 716, -57, 106, 78, 54),
  [2657] = Vertex.new(6, 716, -52, 106, 78, 54),
})
local alicesHusband = Model.new(4014, {
  [2827] = Vertex.new(76, 643, 19, 228, 241, 20, 0.000),
  [2828] = Vertex.new(81, 630, 19, 228, 241, 20, 0.000),
  [2830] = Vertex.new(-76, 643, 19, 228, 241, 20, 0.000),
  [2832] = Vertex.new(-81, 630, 19, 228, 241, 20, 0.000),
  [2833] = Vertex.new(0, 676, 53, 77, 203, 40, 0.000),
})
local alicesHusbandAmulet = Model.new(4371, {
  [3184] = Vertex.new(76, 643, 19, 229, 242, 21, 0.000),
  [3185] = Vertex.new(81, 630, 19, 229, 242, 21, 0.000),
  [3187] = Vertex.new(-76, 643, 19, 229, 242, 21, 0.000),
  [3189] = Vertex.new(-81, 630, 19, 229, 242, 21, 0.000),
  [3190] = Vertex.new(0, 676, 53, 78, 203, 41, 0.000),
})
local helda = Model.new(3441, {
  [1047] = Vertex.new(24, 738, -38, 30, 29, 27),
  [1071] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [1747] = Vertex.new(0, 735, -7, 54, 142, 28),
  [1749] = Vertex.new(0, 732, -8, 54, 142, 28),
  [2716] = Vertex.new(126, 380, -15, 54, 142, 28),
})
--#endregion
--#region Objects
local deadTree = Model.new(756, {
  [50] = Vertex.new(-202, 1098, -312, 127, 127, 127),
  [86] = Vertex.new(-55, 1199, -16, 127, 127, 127),
  [247] = Vertex.new(-202, 1098, -312, 127, 127, 127),
  [305] = Vertex.new(-235, 931, -178, 127, 127, 127),
  [377] = Vertex.new(-202, 1098, -312, 127, 127, 127),
})
--#endregion
--#region Items
local mithrilHatchet = Model.new(429, {
  [195] = Vertex.new(7, 0, -97, 22, 22, 29),
  [241] = Vertex.new(57, 0, 123, 119, 119, 155),
  [251] = Vertex.new(57, 0, 123, 66, 66, 86),
  [407] = Vertex.new(15, 6, 79, 7, 7, 9),
  [428] = Vertex.new(43, 6, 62, 7, 7, 9),
})
local holySymbol = Model.new(180, {
  [71] = Vertex.new(60, 16, 136, 111, 102, 102),
  [77] = Vertex.new(60, 16, 136, 111, 102, 102),
  [84] = Vertex.new(60, 16, 136, 111, 102, 102),
  [130] = Vertex.new(60, 16, 136, 111, 102, 102),
  [136] = Vertex.new(60, 16, 136, 111, 102, 102),
})
local polishedButtons = Model.new(600, {
  [367] = Vertex.new(40, 12, -52, 50, 46, 46),
  [371] = Vertex.new(44, 12, -32, 50, 46, 46),
  [595] = Vertex.new(0, 28, 56, 50, 46, 46),
  [597] = Vertex.new(8, 24, 52, 50, 46, 46),
  [600] = Vertex.new(8, 24, 48, 50, 46, 46),
})
local hardLeather = Model.new(120, {
  [2] = Vertex.new(104, 0, 128, 42, 37, 22),
  [31] = Vertex.new(-76, 0, 120, 42, 37, 22),
  [34] = Vertex.new(-76, 0, 120, 42, 37, 22),
  [75] = Vertex.new(104, 0, -88, 42, 37, 22),
  [120] = Vertex.new(-108, 0, -64, 42, 37, 22),
})
--#endregion
--#region Quest Items
local alteredGhostspeakAmulet = Model.new(432, {
  [151] = Vertex.new(-36, 0, 72, 149, 132, 114),
  [154] = Vertex.new(-36, 0, 72, 149, 132, 114),
  [186] = Vertex.new(36, 0, 72, 149, 132, 114),
  [187] = Vertex.new(36, 0, 72, 149, 132, 114),
  [190] = Vertex.new(36, 0, 72, 149, 132, 114),
})
local undeadChicken = Model.new(564, {
  [55] = Vertex.new(-128, 408, -92, 142, 136, 109),
  [58] = Vertex.new(-128, 408, -92, 142, 136, 109),
  [63] = Vertex.new(-132, 408, -92, 142, 136, 109),
  [142] = Vertex.new(-132, 408, -92, 142, 136, 109),
  [145] = Vertex.new(-132, 408, -92, 142, 136, 109),
})
local selectedIron = Model.new(270, {
  [34] = Vertex.new(56, 0, 132, 60, 55, 55),
  [35] = Vertex.new(56, 32, 132, 60, 55, 55),
  [36] = Vertex.new(-56, 32, 132, 60, 55, 55),
  [38] = Vertex.new(-56, 0, 116, 60, 55, 55),
})
local barMagnet = Model.new(270, {
  [34] = Vertex.new(-48, 32, -120, 93, 15, 8),
  [36] = Vertex.new(44, 32, -124, 93, 15, 8),
  [38] = Vertex.new(-36, 60, -112, 93, 15, 8),
  [39] = Vertex.new(36, 56, -108, 93, 15, 8),
})
local blessedHatchet = Model.new(429, {
  [235] = Vertex.new(-74, 0, -164, 159, 151, 145),
  [249] = Vertex.new(-68, 0, -147, 65, 65, 85),
  [251] = Vertex.new(-74, 0, -164, 65, 65, 85),
  [407] = Vertex.new(-36, 6, -116, 6, 7, 9),
  [428] = Vertex.new(-66, 6, -103, 6, 7, 9),
})
local undeadTwigs = Model.new(339, {
  [74] = Vertex.new(-140, 68, 152, 65, 48, 26),
  [78] = Vertex.new(-140, 68, 152, 65, 48, 26),
  [79] = Vertex.new(-140, 68, 152, 65, 48, 26),
  [249] = Vertex.new(-40, 20, 400, 51, 36, 4),
  [251] = Vertex.new(-40, 20, 400, 51, 36, 4),
})
local researchNotes = Model.new(426, {
  [44] = Vertex.new(-252, 24, 124, 47, 37, 4),
  [183] = Vertex.new(180, 60, 180, 47, 37, 4),
  [204] = Vertex.new(196, 60, 168, 47, 37, 4),
  [213] = Vertex.new(108, 72, -232, 50, 46, 46),
  [221] = Vertex.new(-160, 72, 180, 50, 46, 46),
})
local aPattern = Model.new(237, {
  [54] = Vertex.new(100, 24, -72, 55, 50, 50),
  [108] = Vertex.new(116, 28, -84, 100, 100, 76),
  [117] = Vertex.new(124, 28, 80, 100, 100, 76),
  [118] = Vertex.new(-136, 12, -88, 98, 97, 74),
  [128] = Vertex.new(-124, 16, 80, 98, 97, 74),
})
local aContainer = Model.new(294, {
  [8] = Vertex.new(-100, 16, -88, 0, 0, 0),
  [45] = Vertex.new(-100, 16, -88, 83, 83, 76),
  [47] = Vertex.new(-100, 16, -88, 83, 83, 76),
  [104] = Vertex.new(-56, 0, 80, 29, 35, 46),
  [108] = Vertex.new(-56, 0, 80, 29, 35, 46),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Ava inside the western room of Draynor Manor.<ul><li>To get to her room, a candle sconce (North of the entrance) must be pulled.</li></ul>",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Draynor Village lodestone",
      url = "Draynor_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3096.8, 1965, 3360) },
    postconditions = { Condition.DistanceTo:new(3093, 965, 3357, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["ava"]),
      Action.ConversationHighlight:new("I would be happy to make your home a better place."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Ava.",
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = { Condition.ConversationText:new("with bated breath") },
  },
  {
    text = "Talk to Alice at the farm west of the Ectofuntus.",
    title = "Undead poultry",
    neededItems = {
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Ecto-token"] = { quantity = 20 },
    },
    recommendedItems = { ["Ectophial"] = { quantity = 1 } },
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ectophial",
      url = "Ectophial.png",
    },
    actions = {
      Action.Direction:new(3628, 893, 3525.5, { distance = 23 }),
      Action.ModelHighlight:new(alice, { distance = 24 }),
      Action.ConversationHighlight:new("I'm here about a quest."),
    },
    postconditions = { Condition.ConversationText:new("to the dead") },
  },
  {
    text = "Talk to Alice's husband.",
    actions = {
      Action.ModelHighlight:new(alicesHusband),
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
    },
    postconditions = { Condition.ConversationText:new("think about it") },
  },
  {
    text = "Talk to Alice.",
    actions = {
      Action.ModelHighlight:new(alice),
      Action.ConversationHighlight:new("I'm here about a quest."),
    },
    postconditions = { Condition.ConversationText:new("uses I suppose") },
  },
  {
    text = "Talk to Alice's husband.",
    actions = { Action.ModelHighlight:new(alicesHusband) },
    postconditions = { Condition.ConversationText:new("hair turns white") },
  },
  {
    text = "Talk to Alice.",
    actions = {
      Action.ModelHighlight:new(alice),
      Action.ConversationHighlight:new("I'm here about a quest."),
    },
    postconditions = { Condition.ConversationText:new("cash in Varrock bank") },
  },
  {
    text = "Talk to Alice's husband.",
    actions = { Action.ModelHighlight:new(alicesHusband) },
    postconditions = { Condition.ConversationText:new("dealin' scammer") },
  },
  {
    text = "Talk to Alice.",
    actions = {
      Action.ModelHighlight:new(alice),
      Action.ConversationHighlight:new("I'm here about a quest."),
    },
    postconditions = { Condition.ConversationText:new("looks pretty normal") },
  },
  {
    text = "Talk to Netty in her house east of the Slayer Tower.<ul><li>If Vessel of the Harbinger is completed, Netty will be in the City of Um at The Last Call.</li></ul>",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Canifis lodestone",
      url = "Canifis_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3462, 813, 3558, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["netty"], { distance = 3 }),
      Action.ConversationHighlight:new("I'm here about the farmers east of here."),
      Action.ConversationHighlight:new("Talk about 'Animal Magnetism.'"),
    },
    postconditions = { Condition.ConversationText:new("human other than") }, --not tested
  },
  {
    text = "Talk to her again.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["netty"]),
      Action.ConversationHighlight:new("I'm here about the farmers east of here."),
      Action.ConversationHighlight:new("'Animal Magnetism'."),
    },
    postconditions = { Condition.InventoryContains:new(alteredGhostspeakAmulet) },
  },
  {
    text = "Talk to Alice's husband.",
    actions = {
      Action.Direction:new(3619, 149, 3527, { distance = 24 }),
      Action.ModelHighlight:new(alicesHusband, { distance = 26 }),
      Action.ConversationHighlight:new("Okay, you need it more than I do, I suppose."),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(alteredGhostspeakAmulet) },
  },
  {
    text = "Talk to him again.",
    actions = { Action.ModelHighlight:new(alicesHusbandAmulet) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to Alice's husband.",
    actions = {
      Action.ModelHighlight:new(alicesHusbandAmulet),
      Action.ConversationHighlight:new("Could I buy those chickens now, then?"),
      Action.ConversationHighlight:new("Could I buy 2 chickens?"),
    },
    postconditions = { Condition.InventoryContains:new(undeadChicken, 2) },
  },
  {
    text = "Talk to Ava.",
    title = "Making a magnet",
    neededItems = { ["Iron bars"] = { quantity = 5 } },
    recommendedItems = {},
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Draynor Village lodestone",
      url = "Draynor_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3096.8, 1965, 3360) },
    postconditions = { Condition.DistanceTo:new(3093, 965, 3357, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = {
      Condition.ConversationText:new("Witch next door"),
      Condition.ConversationText:new("finished praising yourself"),
    },
  },
  {
    text = "Talk to Helda.",
    actions = { Action.ModelHighlight:new(helda) },
    postconditions = { Condition.ConversationText:new("be back") },
  },
  {
    text = "Talk to her again.",
    actions = { Action.ModelHighlight:new(helda) },
    postconditions = { Condition.InventoryContains:new(selectedIron) },
  },
  {
    text = "Face north on the marked tile and click the selected iron.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Port Sarim lodestone",
      url = "Port_Sarim_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2971, 165, 3233, { tile = true }),
      Action.InventoryHighlight:new(selectedIron),
    },
    postconditions = { Condition.InventoryContains:new(barMagnet) },
  },
  {
    text = "Talk to Ava.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Draynor Village lodestone",
      url = "Draynor_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3096.8, 1965, 3360) },
    postconditions = { Condition.DistanceTo:new(3093, 965, 3357, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = { Condition.ConversationText:new("trees are pretty close") },
  },
  {
    text = "Cut an undead tree.",
    title = "Blessed hatchet",
    neededItems = {
      ["Mithril hatchet"] = { quantity = 1 },
      ["Holy symbol"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(deadTree, { highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("report this to Ava") },
  },
  {
    text = "Talk to Ava.",
    actions = { Action.Direction:new(3096.8, 1965, 3360) },
    postconditions = { Condition.DistanceTo:new(3093, 965, 3357, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = { Condition.ConversationText:new("into a laughing stock") },
  },
  {
    text = "Talk to Turael in Burthorpe.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Burthorpe lodestone",
      url = "Burthorpe_lodestone_icon.png",
    },
    actions = {
      Action.ModelHighlight:new(Models.npcs["turael"]),
      Action.ConversationHighlight:new("I'm here about a quest."),
    },
    postconditions = { Condition.ConversationText:new("hatchet and a symbol") },
  },
  {
    text = "Talk to him again.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["turael"]),
      Action.ConversationHighlight:new("Hello, I'm here about those trees again."),
      Action.ConversationHighlight:new("I'd love one, thanks."),
    },
    postconditions = { Condition.InventoryContains:new(blessedHatchet) },
  },
  {
    text = "Cut an undead tree.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Draynor Village lodestone",
      url = "Draynor_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3108.5, 965, 3343.5, { distance = 21 }),
      Action.ModelHighlight:new(deadTree, { highlightPriority = "all", distance = 22 }),
    },
    postconditions = { Condition.InventoryContains:new(undeadTwigs) },
  },
  {
    text = "Talk to Ava.",
    actions = { Action.Direction:new(3096.8, 1965, 3360) },
    postconditions = { Condition.DistanceTo:new(3093, 965, 3357, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = { Condition.ConversationText:new("ask me for a copy") },
  },
  {
    text = "Talk to Ava.",
    title = "Finishing up",
    neededItems = {
      ["Hard leather"] = { quantity = 1 },
      ["Polished buttons"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = { Condition.InventoryContains:new(researchNotes) },
  },
  {
    text = "Translate Ava's research notes. Solution:<br/>&#128992;&#128994;&#128992;&#128992;&#128994;&#128992;&#128992;&#128992;&#128994;",
    actions = { Action.InventoryHighlight:new(researchNotes) },
    postconditions = { Condition.ChatText:new("all makes sense") },
  },
  {
    text = "Talk to Ava.",
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = { Condition.InventoryContains:new(aPattern) },
  },
  {
    text = "Use the pattern with a piece of hard leather.",
    actions = {
      Action.InventoryHighlight:new(hardLeather, true),
      Action.InventoryHighlight:new(aPattern),
    },
    postconditions = { Condition.InventoryContains:new(aContainer) },
  },
  {
    text = "Talk to Ava.",
    actions = { Action.ModelHighlight:new(Models.npcs["ava"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Animal Magnetism",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1165881600,
  prereqQuests = { "The Restless Ghost", "Ernest the Chicken" },
  questReqs = {
    Types.QuestReq.skill("Crafting", 19),
    Types.QuestReq.skill("Ranged", 30),
    Types.QuestReq.skill("Slayer", 18),
    Types.QuestReq.skill("Thieving", 15),
    Types.QuestReq.skill("Woodcutting", 35),
    Types.QuestReq.ironmanOnlySkill("Smithing", 10),
  },
  neededItems = {
    ["Mithril hatchet"] = { quantity = 1, model = mithrilHatchet },
    ["Iron bars"] = { quantity = 5, model = Models.items["iron bar"] },
    ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] },
    ["Holy symbol"] = { quantity = 1, model = holySymbol },
    ["Polished buttons"] = { quantity = 1, model = polishedButtons },
    ["Hard leather"] = { quantity = 1, model = hardLeather },
    ["Ecto-tokens"] = { quantity = 20 },
  },
  recommendedItems = { ["Ectophial"] = { quantity = 1 } },
  combatNPCs = {},
})
