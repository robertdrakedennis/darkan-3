local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local gardenerGhost = Model.new(2925, {
  [2386] = Vertex.new(76, 643, 19, 163, 172, 14, 0.000),
  [2387] = Vertex.new(81, 630, 19, 163, 172, 14, 0.000),
  [2389] = Vertex.new(-76, 643, 19, 163, 172, 14, 0.000),
  [2391] = Vertex.new(-81, 630, 19, 163, 172, 14, 0.000),
  [2392] = Vertex.new(0, 676, 53, 54, 142, 28, 0.000),
})
local experiment = Model.new(1476, {
  [39] = Vertex.new(12, 684, -292, 134, 99, 70),
  [107] = Vertex.new(-12, 684, -292, 134, 99, 70),
  [420] = Vertex.new(84, 728, -240, 129, 119, 118),
  [427] = Vertex.new(84, 728, -240, 129, 119, 118),
  [561] = Vertex.new(-84, 728, -240, 129, 119, 118),
})
--#endregion
--#region Objects
local grave = Model.new(30, {
  [26] = Vertex.new(4396, 1250, 1840, 35, 31, 22),
  [29] = Vertex.new(4336, 1246, 1839, 35, 31, 22),
  [30] = Vertex.new(4396, 1250, 1840, 35, 31, 22),
})
local fireplace = Model.new(672, {
  [345] = Vertex.new(256, 468, 256, 48, 48, 19),
  [353] = Vertex.new(-216, 528, 256, 48, 48, 19),
  [369] = Vertex.new(256, 628, 24, 48, 48, 19),
  [416] = Vertex.new(256, 704, 256, 46, 45, 4),
  [420] = Vertex.new(-256, 628, -40, 46, 45, 4),
})
local pickledBrainObj = Model.new(240, {
  [147] = Vertex.new(20, 180, 48, 98, 66, 37, 0.4980),
  [150] = Vertex.new(20, 180, 48, 98, 66, 37, 0.4980),
  [168] = Vertex.new(48, 180, -20, 98, 66, 37, 0.4980),
  [171] = Vertex.new(-48, 180, -20, 98, 66, 37, 0.4980),
  [180] = Vertex.new(16, 180, -48, 98, 66, 37, 0.4980),
})
local cavernKeyObj = Model.new(285, {
  [99] = Vertex.new(-84, 0, -68, 73, 67, 67),
  [183] = Vertex.new(88, 0, 72, 95, 91, 73),
  [184] = Vertex.new(88, 0, 72, 95, 91, 73),
  [187] = Vertex.new(88, 0, 72, 95, 91, 73),
  [231] = Vertex.new(36, 0, 68, 95, 91, 73),
})
--#endregion
--#region Items
local bronzeWire = Model.new(294, {
  [168] = Vertex.new(68, 104, 96, 71, 39, 6),
  [230] = Vertex.new(32, 140, 96, 71, 39, 6),
  [236] = Vertex.new(32, 140, 96, 71, 39, 6),
  [239] = Vertex.new(68, 104, 96, 71, 39, 6),
  [240] = Vertex.new(32, 140, 96, 71, 39, 6),
})
--#endregion
--#region Quest Items
local obsidianAmulet = Model.new(129, {
  [60] = Vertex.new(76, 0, 44, 3, 33, 34),
  [62] = Vertex.new(76, 0, 44, 3, 33, 34),
  [68] = Vertex.new(76, 0, 44, 3, 33, 34),
  [71] = Vertex.new(-76, 0, 44, 3, 33, 34),
  [80] = Vertex.new(-76, 0, 44, 3, 33, 34),
})
local marbleAmulet = Model.new(81, {
  [28] = Vertex.new(-76, 16, -44, 58, 58, 63),
  [31] = Vertex.new(-76, 16, -44, 58, 58, 63),
  [39] = Vertex.new(-76, 16, -44, 58, 58, 63),
  [54] = Vertex.new(76, 16, -44, 58, 58, 63),
  [58] = Vertex.new(76, 16, -44, 58, 58, 63),
})
local starAmulet = Model.new(210, {
  [130] = Vertex.new(-76, 16, -44, 58, 58, 63),
  [133] = Vertex.new(-76, 16, -44, 58, 58, 63),
  [141] = Vertex.new(-76, 16, -44, 58, 58, 63),
  [156] = Vertex.new(76, 16, -44, 58, 58, 63),
  [160] = Vertex.new(76, 16, -44, 58, 58, 63),
})
local pickledBrain = Model.multi({
  Model.new(348, {
    [253] = Vertex.new(20, 196, 48, 98, 66, 37),
    [255] = Vertex.new(-20, 196, 48, 98, 66, 37),
    [256] = Vertex.new(20, 196, 48, 98, 66, 37),
    [267] = Vertex.new(-48, 196, -20, 98, 66, 37),
    [271] = Vertex.new(-20, 196, 48, 98, 66, 37),
  }),
  Model.new(240, {
    [145] = Vertex.new(-20, 180, 48, 98, 66, 37, 0.4980),
    [147] = Vertex.new(20, 180, 48, 98, 66, 37, 0.4980),
    [150] = Vertex.new(20, 180, 48, 98, 66, 37, 0.4980),
    [180] = Vertex.new(16, 180, -48, 98, 66, 37, 0.4980),
    [185] = Vertex.new(16, 180, -48, 98, 66, 37, 0.4980),
  }),
})
local cavernKey = Model.new(285, {
  [183] = Vertex.new(88, 0, 72, 93, 88, 71),
  [184] = Vertex.new(88, 0, 72, 93, 88, 71),
  [187] = Vertex.new(88, 0, 72, 93, 88, 71),
  [231] = Vertex.new(36, 0, 68, 93, 88, 71),
  [233] = Vertex.new(36, 0, 68, 93, 88, 71),
})
local decapitatedHead = Model.new(324, {
  [8] = Vertex.new(0, 12, -84, 96, 77, 61),
  [39] = Vertex.new(-16, 128, 60, 76, 75, 48),
  [43] = Vertex.new(-16, 128, 60, 76, 75, 48),
  [294] = Vertex.new(-16, 128, 60, 30, 30, 23),
  [297] = Vertex.new(-16, 128, 60, 30, 30, 23),
})
local torso = Model.new(282, {
  [35] = Vertex.new(104, 28, -116, 91, 67, 47),
  [154] = Vertex.new(-96, 28, -116, 96, 77, 61),
  [162] = Vertex.new(-96, 28, -116, 96, 77, 61),
  [188] = Vertex.new(104, 28, -116, 96, 77, 61),
  [192] = Vertex.new(104, 28, -116, 96, 77, 61),
})
local arms = Model.new(312, {
  [134] = Vertex.new(-224, 16, 104, 85, 77, 54),
  [137] = Vertex.new(-188, 24, 144, 85, 77, 54),
  [143] = Vertex.new(-188, 24, 144, 85, 77, 54),
  [150] = Vertex.new(-188, 24, 144, 85, 77, 54),
  [152] = Vertex.new(-188, 24, 144, 85, 77, 54),
})
local legs = Model.new(528, {
  [24] = Vertex.new(-36, 104, -240, 94, 75, 59),
  [41] = Vertex.new(48, 132, -188, 94, 75, 59),
  [288] = Vertex.new(48, 132, -188, 94, 86, 48),
  [293] = Vertex.new(48, 132, -188, 94, 86, 48),
  [299] = Vertex.new(-36, 104, -240, 94, 86, 48),
})
local gardenBrush = Model.new(324, {
  [2] = Vertex.new(180, 56, -136, 113, 98, 72),
  [9] = Vertex.new(168, 56, -148, 113, 98, 72),
  [15] = Vertex.new(160, 56, -160, 113, 98, 72),
  [21] = Vertex.new(148, 56, -172, 113, 98, 72),
  [27] = Vertex.new(140, 56, -184, 113, 98, 72),
})
local gardenCane = Model.new(108, {
  [4] = Vertex.new(-192, 0, -204, 79, 71, 50),
  [13] = Vertex.new(-192, 32, -204, 79, 71, 50),
  [18] = Vertex.new(-192, 32, -204, 79, 71, 50),
  [64] = Vertex.new(-192, 32, -204, 79, 71, 50),
  [73] = Vertex.new(-192, 32, -204, 79, 71, 50),
})
local extendedBrush = Model.new(468, {
  [26] = Vertex.new(204, 56, -224, 113, 98, 72),
  [30] = Vertex.new(216, 56, -212, 113, 98, 72),
  [33] = Vertex.new(224, 56, -204, 113, 98, 72),
  [39] = Vertex.new(244, 56, -188, 113, 98, 72),
  [41] = Vertex.new(252, 56, -180, 113, 98, 72),
})
local lightningConductorMould = Model.new(141, {
  [1] = Vertex.new(-56, 0, -152, 129, 129, 118),
  [2] = Vertex.new(36, 0, 140, 129, 129, 118),
  [3] = Vertex.new(48, 0, -156, 129, 129, 118),
  [4] = Vertex.new(-56, 0, -152, 129, 129, 118),
  [5] = Vertex.new(-56, 0, 140, 129, 129, 118),
})
local conductor = Model.new(123, {
  [1] = Vertex.new(-256, 0, 256, 97, 90, 89),
  [2] = Vertex.new(256, 0, -256, 97, 90, 89),
  [3] = Vertex.new(256, 0, 256, 97, 90, 89),
  [4] = Vertex.new(-256, 0, 256, 97, 90, 89),
  [5] = Vertex.new(-256, 0, -256, 97, 90, 89),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Dr Fenkenstrain at his castle to the north-east of Canifis.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Canifis lodestone",
      url = "Canifis_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["dr fenkenstrain"], { distance = 16 }),
      Action.Direction:new(3548, 5125, 3551, { distance = 20 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Dr. Fenkenstrain.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["dr fenkenstrain"]),
      Action.ConversationHighlight:new("Braindead."),
      Action.ConversationHighlight:new("Grave-digging."),
    },
    postconditions = { Condition.ConversationText:new("if you insist") },
  },
  {
    text = "Climb up the staircase.",
    title = "Light reading",
    neededItems = {
      ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(3537.5, 5825, 3552) },
    postconditions = { Condition.DistanceToWithHeight:new(3537, 6085, 3554, 4) },
  },
  {
    text = "Search the bookcase.",
    actions = {
      Action.Direction:new(3542, 6785, 3558),
      Action.ConversationHighlight:new("The Joy of Gravedigging"),
    },
    postconditions = { Condition.InventoryContains:new(marbleAmulet) },
  },
  {
    text = "Search the bookcase to the east.",
    actions = {
      Action.Direction:new(3555, 6785, 3558),
      Action.ConversationHighlight:new("Handy Maggot Avoidance Techniques"),
    },
    postconditions = { Condition.InventoryContains:new(obsidianAmulet) },
  },
  {
    text = "Use the marble amulet on the obsidian amulet.",
    actions = {
      Action.InventoryHighlight:new(obsidianAmulet),
      Action.InventoryHighlight:new(marbleAmulet),
    },
    postconditions = { Condition.InventoryContains:new(starAmulet) },
  },
  {
    text = "Climb down the staircase.",
    title = "Getting head",
    neededItems = { ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] } },
    recommendedItems = { ["Holy symbol"] = { quantity = 1 } },
    actions = { Action.Direction:new(3559.5, 6085, 3552) },
    postconditions = { Condition.DistanceToWithHeight:new(3559, 5133, 3549, 4) },
  },
  {
    text = "Talk to the Gardener Ghost on the ground floor to the north.",
    actions = {
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
      Action.ModelHighlight:new(gardenerGhost),
      Action.ConversationHighlight:new("What happened to your head?"),
    },
    postconditions = { Condition.ConversationText:new("pose oi") },
  },
  {
    text = "Stand on the marked tile and 'dig' with the grave marker.",
    actions = {
      Action.Direction:new(3608, 877, 3490, { tile = true }),
      Action.ModelHighlight:new(grave),
    },
    postconditions = { Condition.InventoryContains:new(decapitatedHead) },
  },
  {
    text = "Pick up the pickled brain in the Canifis tavern.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Canifis lodestone",
      url = "Canifis_lodestone_icon.png",
    },
    neededItems = { ["Coins"] = { quantity = 50 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3492, 893, 3474) },
    postconditions = { Condition.ModelVisible:new(pickledBrainObj) },
  },
  {
    actions = {
      Action.ModelHighlight:new(pickledBrainObj),
      Action.ConversationHighlight:new("I'll buy one."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(pickledBrainObj) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(pickledBrain) },
  },
  {
    text = "Use the decapitated head on the pickled brain.",
    actions = {
      Action.InventoryHighlight:new(pickledBrain),
      Action.InventoryHighlight:new(decapitatedHead),
    },
    postconditions = { Condition.ConversationText:new("squeeze the pickled brain") },
  },
  {
    text = "Use the star amulet on the memorial east of Fenkenstrain's castle.",
    title = "Arms, legs and torso",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ectophial",
      url = "Ectophial.png",
    },
    neededItems = {},
    recommendedItems = { ["Ectophial"] = { quantity = 1 } },
    actions = {
      Action.InventoryHighlight:new(starAmulet),
      Action.Direction:new(3578, 849, 3527),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(starAmulet),
      Condition.ConversationText:new("star amulet"),
    },
  },
  {
    text = "Push the memorial.",
    actions = { Action.Direction:new(3578, 849, 3527) },
    postconditions = { Condition.DistanceToWithHeight:new(3577, 1149, 9927, 12) },
  },
  {
    text = "Kill the marked experiment creature.",
    actions = {
      Action.ModelHighlight:new(experiment, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(cavernKeyObj),
    },
    postconditions = { Condition.ModelVisible:new(cavernKey) },
  },
  {
    text = "Pick up the cavern key.",
    actions = { Action.ModelHighlight:new(cavernKey) },
    postconditions = { Condition.InventoryContains:new(cavernKey) },
  },
  {
    text = "Open the entrance.",
    actions = { Action.Direction:new(3510.5, 1245, 9957) },
    postconditions = { Condition.DistanceTo:new(3509, 517, 9957, 1) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(3504, 1013, 9970) },
    postconditions = { Condition.DistanceToWithHeight:new(3504, 645, 3569, 10) },
  },
  {
    text = "Right-click dig on all 3 of the graves.",
    actions = { Action.Direction:new(3502, 909, 3576) },
    postconditions = { Condition.InventoryContains:new(torso) },
  },
  {
    actions = { Action.Direction:new(3504, 941, 3577) },
    postconditions = { Condition.InventoryContains:new(arms) },
  },
  {
    actions = { Action.Direction:new(3506, 957, 3576) },
    postconditions = { Condition.InventoryContains:new(legs) },
  },
  {
    text = "Return to Dr Fenkenstrain.",
    title = "Lightning",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Canifis lodestone",
      url = "Canifis_lodestone_icon.png",
    },
    neededItems = {
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Needle"] = { quantity = 1 },
      ["Thread"] = { quantity = 5 },
      ["Bronze wire"] = { quantity = 3 },
      ["Silver bar"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I have some body parts for you."),
      Action.ModelHighlight:new(Models.npcs["dr fenkenstrain"], { distance = 16 }),
      Action.Direction:new(3548, 5125, 3551, { distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("need 5 lots of thread") },
  },
  {
    text = "Talk to Dr Fenkenstrain again.",
    actions = { Action.ModelHighlight:new(Models.npcs["dr fenkenstrain"]) },
    postconditions = { Condition.ConversationText:new("Repair") },
  },
  {
    text = "Talk to the Gardener Ghost.",
    actions = {
      Action.ModelHighlight:new(gardenerGhost),
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
      Action.ConversationHighlight:new("Do you know where the key to the shed is?"),
    },
    postconditions = { Condition.ConversationText:new("rusty key") },
  },
  {
    text = "Enter the shed.",
    actions = { Action.Direction:new(3547.6, 5825, 3565) },
    postconditions = { Condition.DistanceTo:new(3546, 5125, 3564, 1) },
  },
  {
    text = "Search the cupboard.",
    actions = { Action.Direction:new(3546, 5825, 3563.5) },
    postconditions = { Condition.InventoryContains:new(gardenBrush) },
  },
  {
    text = "Take 3 garden canes from the pile.",
    actions = { Action.Direction:new(3551, 5125, 3564, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(gardenCane, 3) },
  },
  {
    text = "Use the 3 garden canes on the brush.",
    actions = {
      Action.InventoryHighlight:new(gardenCane),
      Action.InventoryHighlight:new(gardenBrush),
      Action.InventoryHighlight:new(extendedBrush),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(gardenCane) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(3537.5, 5825, 3552) },
    postconditions = { Condition.DistanceToWithHeight:new(3537, 6085, 3554, 4) },
  },
  {
    text = "Use the extended brush on the fireplace.",
    actions = {
      Action.ModelHighlight:new(fireplace, { atLocation = Location:new(3544, 6085, 3555) }),
      Action.InventoryHighlight:new(extendedBrush),
    },
    postconditions = { Condition.InventoryContains:new(lightningConductorMould) },
  },
  {
    text = "Make the conductor with a silver bar at any furnace.",
    postconditions = { Condition.InventoryContains:new(conductor) },
  },
  {
    text = "Climb up the staircase in Fenkenstrain's castle.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Canifis lodestone",
      url = "Canifis_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3537.5, 5825, 3552) },
    postconditions = { Condition.DistanceToWithHeight:new(3537, 6085, 3554, 4) },
  },
  {
    text = "Climb up the ladder in the southern room.",
    actions = { Action.Direction:new(3548, 6685, 3539) },
    postconditions = { Condition.DistanceToWithHeight:new(3548, 7045, 3540, 2) },
  },
  {
    text = "Repair the lightning conductor.",
    actions = { Action.Direction:new(3548.5, 7045, 3537, { tile = true }) },
    postconditions = { Condition.InventoryDoesNotContain:new(conductor) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3548, 7045, 3539, { tile = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(3548, 6085, 3538, 4) },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(3537.5, 6085, 3552) },
    postconditions = { Condition.DistanceToWithHeight:new(3537, 5125, 3549, 5) },
  },
  {
    text = "Talk to Dr Fenkenstrain.",
    title = "Lord Rologarth",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["dr fenkenstrain"]) },
    postconditions = { Condition.ConversationText:new("the key") },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(3537.5, 5825, 3552) },
    postconditions = { Condition.DistanceToWithHeight:new(3537, 6085, 3554, 4) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(3548, 6685, 3554) },
    postconditions = { Condition.DistanceToWithHeight:new(3548, 7045, 3553, 4) },
  },
  {
    text = "Talk to Fenkenstrain's Monster.",
    actions = { Action.ModelHighlight:new(Models.npcs["lord rologarth"]) },
    postconditions = { Condition.ConversationText:new("stop Fenkenstrain") },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3548, 7045, 3554, { tile = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(3548, 6085, 3553, 4) },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(3537.5, 6085, 3552) },
    postconditions = { Condition.DistanceToWithHeight:new(3537, 5125, 3549, 5) },
  },
  {
    text = "Pickpocket Dr Fenkenstrain.",
    actions = { Action.ModelHighlight:new(Models.npcs["dr fenkenstrain"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Creature of Fenkenstrain",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1107129600,
  prereqQuests = { "The Restless Ghost" },
  questReqs = {
    Types.QuestReq.skill("Crafting", 20),
    Types.QuestReq.skill("Thieving", 25),
    Types.QuestReq.ironmanOnlySkill("Mining", 20, true),
    Types.QuestReq.ironmanOnlySkill("Smithing", 20, true),
  },
  neededItems = {
    ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] },
    ["Coins"] = { quantity = 50 },
    ["Silver bar (metal bank works)"] = { quantity = 1 },
    ["Bronze wire"] = { quantity = 3, model = bronzeWire },
    ["Needle"] = { quantity = 1, model = Models.items["needle"] },
    ["Thread"] = { quantity = 5, model = Models.items["thread"] },
  },
  recommendedItems = {
    ["Holy symbol"] = { quantity = 1 },
    ["Ectophial"] = { quantity = 1 },
  },
  combatNPCs = { ["Experiment"] = { level = "35", quantity = 1 } },
})
