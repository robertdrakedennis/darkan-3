local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local caleb = Model.new(4413, {
  [3274] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3279] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [3282] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3284] = Vertex.new(7, 724, -51, 108, 80, 56),
  [3757] = Vertex.new(0, 735, -7, 29, 141, 128),
})
local avan = Model.new(4839, {
  [2656] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [2664] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2666] = Vertex.new(7, 724, -51, 108, 80, 56),
  [4488] = Vertex.new(-65, 670, 50, 154, 153, 14),
  [4519] = Vertex.new(65, 670, 50, 154, 153, 14),
})
local boot = Model.new(9249, {
  [1684] = Vertex.new(-202, 169, 84, 127, 127, 127),
  [1690] = Vertex.new(-217, 169, 83, 127, 127, 127),
  [1702] = Vertex.new(-216, 165, 82, 127, 127, 127),
  [3346] = Vertex.new(-221, 156, 116, 127, 127, 127),
  [3380] = Vertex.new(-203, 158, 117, 127, 127, 127),
})
local johnathon = Model.new(4947, {
  [3100] = Vertex.new(-2, 725, -59, 110, 81, 57),
  [3105] = Vertex.new(-7, 724, -51, 110, 81, 57),
  [3108] = Vertex.new(2, 725, -59, 110, 81, 57),
  [3110] = Vertex.new(7, 724, -51, 110, 81, 57),
  [3367] = Vertex.new(0, 735, -7, 30, 142, 129),
})
local chronozon = Model.new(43164, {
  [35770] = Vertex.new(58, 1459, -242, 127, 127, 127),
  [36091] = Vertex.new(-58, 1459, -242, 127, 127, 127),
  [36283] = Vertex.new(-20, 1452, -295, 128, 127, 127),
  [36830] = Vertex.new(20, 1452, -295, 128, 127, 127),
  [36956] = Vertex.new(20, 1452, -295, 128, 127, 127),
})
--#endregion
--#region Objects
local upLever = Model.new(408, {
  [123] = Vertex.new(-256, 932, -108, 81, 76, 74),
  [147] = Vertex.new(-256, 852, 84, 73, 68, 67),
  [269] = Vertex.new(-184, 880, -68, 90, 86, 83),
  [290] = Vertex.new(-192, 888, -56, 90, 86, 83),
  [347] = Vertex.new(-256, 932, -108, 73, 68, 67),
})
local perfectGoldRock = Model.new(3252, {
  [207] = Vertex.new(-108, 515, 77, 127, 127, 127),
  [2751] = Vertex.new(120, -299, -234, 128, 128, 127),
  [2752] = Vertex.new(120, -299, -234, 128, 128, 127),
  [2844] = Vertex.new(-226, 369, -124, 128, 128, 127),
  [2932] = Vertex.new(221, -66, 252, 128, 128, 127),
})
local jollyBoarInnStaircase = Model.new(4416, {
  [959] = Vertex.new(3461, 1298, 3738, 177, 177, 177),
  [1103] = Vertex.new(3400, 1049, 4085, 177, 177, 177),
  [2665] = Vertex.new(3461, 1357, 3738, 177, 177, 177),
  [2734] = Vertex.new(3461, 1357, 3738, 177, 177, 177),
  [3337] = Vertex.new(3461, 1357, 3738, 177, 177, 177),
})
--#endregion
--#region Items
local shrimp = Model.new(579, {
  [537] = Vertex.new(60, 16, 52, 0, 0, 0),
  [546] = Vertex.new(-36, 16, 100, 0, 0, 0),
  [552] = Vertex.new(-40, 16, 84, 0, 0, 0),
  [564] = Vertex.new(60, 0, 52, 0, 0, 0),
  [573] = Vertex.new(-36, 0, 100, 0, 0, 0),
})
local salmon = Model.new(312, {
  [7] = Vertex.new(-48, -68, -100, 158, 93, 81),
  [11] = Vertex.new(-48, -68, -100, 158, 93, 81),
  [13] = Vertex.new(-48, -68, -100, 158, 93, 81),
  [16] = Vertex.new(-48, 68, -100, 158, 93, 81),
  [21] = Vertex.new(-48, 68, -100, 158, 93, 81),
})
local bass = Model.new(444, {
  [45] = Vertex.new(128, 0, -92, 158, 93, 81),
  [48] = Vertex.new(128, 0, -92, 158, 93, 81),
  [50] = Vertex.new(128, 0, -92, 158, 93, 81),
  [52] = Vertex.new(160, 0, 56, 158, 93, 81),
  [55] = Vertex.new(160, 0, 56, 158, 93, 81),
})
local tuna = Model.new(444, {
  [45] = Vertex.new(128, 0, -92, 120, 96, 91),
  [48] = Vertex.new(128, 0, -92, 120, 96, 91),
  [50] = Vertex.new(128, 0, -92, 120, 96, 91),
  [52] = Vertex.new(160, 0, 56, 120, 96, 91),
  [55] = Vertex.new(160, 0, 56, 120, 96, 91),
})
local swordfish = Model.new(276, {
  [51] = Vertex.new(-224, 12, -128, 132, 73, 142),
  [54] = Vertex.new(-224, 12, -128, 132, 73, 142),
  [56] = Vertex.new(-224, 12, -128, 132, 73, 142),
  [59] = Vertex.new(-224, 12, -128, 132, 73, 142),
  [269] = Vertex.new(52, 28, 52, 132, 73, 142),
})
--#endregion
--#region Quest Items
local calebCrestPart = Model.new(120, {
  [106] = Vertex.new(-92, 32, 116, 72, 76, 112),
  [108] = Vertex.new(-120, 32, 92, 72, 76, 112),
  [111] = Vertex.new(-120, 32, -92, 72, 76, 112),
  [112] = Vertex.new(-148, 32, 64, 72, 76, 112),
  [120] = Vertex.new(-148, 32, -64, 72, 76, 112),
})
local perfectGoldOre = Model.new(336, {
  [14] = Vertex.new(80, 80, -132, 85, 67, 44),
  [16] = Vertex.new(-132, 0, 144, 85, 67, 44),
  [131] = Vertex.new(80, 80, -132, 85, 67, 44),
  [237] = Vertex.new(80, 80, -132, 85, 67, 44),
  [239] = Vertex.new(80, 80, -132, 85, 67, 44),
})
local perfectGoldBar = Model.new(84, {
  [1] = Vertex.new(-56, 0, 80, 155, 123, 14),
  [4] = Vertex.new(-56, 0, 80, 155, 123, 14),
  [9] = Vertex.new(56, 0, -80, 155, 123, 14),
  [13] = Vertex.new(56, 0, -80, 155, 123, 14),
  [16] = Vertex.new(56, 0, -80, 155, 123, 14),
})
local perfectRing = Model.new(624, {
  [228] = Vertex.new(33, 4, 34, 155, 133, 14),
  [230] = Vertex.new(33, 4, 34, 155, 133, 14),
  [235] = Vertex.new(33, 4, 34, 155, 133, 14),
  [252] = Vertex.new(16, 4, 42, 155, 133, 14),
  [258] = Vertex.new(16, 4, 42, 155, 133, 14),
})
local perfectNecklace = Model.new(672, {
  [306] = Vertex.new(24, 9, 50, 135, 116, 12),
  [309] = Vertex.new(30, 9, 37, 135, 116, 12),
  [429] = Vertex.new(-24, 9, 50, 135, 116, 12),
  [474] = Vertex.new(-31, 9, 37, 135, 116, 12),
  [501] = Vertex.new(-15, 7, 59, 135, 116, 12),
})
local avanCrestPart = Model.new(123, {
  [86] = Vertex.new(64, 32, 132, 23, 15, 2),
  [89] = Vertex.new(92, 32, 116, 73, 78, 114),
  [90] = Vertex.new(120, 32, 92, 73, 78, 114),
  [96] = Vertex.new(64, 32, 144, 73, 78, 114),
  [98] = Vertex.new(-64, 32, 144, 73, 78, 114),
})
local johnathonCrestPart = Model.new(123, {
  [94] = Vertex.new(92, 32, -116, 73, 78, 114),
  [96] = Vertex.new(120, 32, -92, 73, 78, 114),
  [98] = Vertex.new(148, 32, 64, 73, 78, 114),
  [105] = Vertex.new(148, 32, -64, 73, 78, 114),
  [106] = Vertex.new(-64, 32, -144, 73, 78, 114),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Dimintheis in the house next to the Fancy Clothes Store in south-eastern Varrock.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3280, 1285, 3403, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["dimintheis"], { distance = 8 }),
      Action.ConversationHighlight:new("Hi, I am a bold adventurer."),
      Action.ConversationHighlight:new("So where is this crest?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Dimintheis.",
    actions = { Action.ModelHighlight:new(Models.npcs["dimintheis"]) },
    postconditions = { Condition.ConversationText:new("still loves them") },
  },
  {
    text = "Talk to Caleb at Gertrude's house.",
    title = "Caleb's piece",
    neededItems = {
      ["Cooked swordfish"] = { quantity = 1 },
      ["Cooked bass"] = { quantity = 1 },
      ["Cooked tuna"] = { quantity = 1 },
      ["Cooked salmon"] = { quantity = 1 },
      ["Cooked shrimp"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.Direction:new(3152, 965, 3407.5, { distance = 5 }),
      Action.ModelHighlight:new(caleb, { distance = 9 }),
      Action.ConversationHighlight:new("Are you Caleb Fitzharmon?"),
      Action.ConversationHighlight:new("So can I have your bit?"),
      Action.ConversationHighlight:new("Ok, I will get those."),
    },
    postconditions = { Condition.ConversationText:new("help me a lot") },
  },
  {
    text = "Talk to him again.",
    actions = { Action.ModelHighlight:new(caleb) },
    postconditions = { Condition.InventoryContains:new(calebCrestPart) },
  },
  {
    text = "Then ask Caleb where the rest of the crest is.",
    actions = {
      Action.ModelHighlight:new(caleb),
      Action.ConversationHighlight:new("Uh... what happened to the rest of it?"),
    },
    postconditions = { Condition.ConversationText:new("easily as I have") },
  },
  {
    text = "Talk to Avan just south of the Al Kharid mine.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Al Kharid lodestone",
      url = "Al_Kharid_lodestone_icon.png",
    },
    title = "Avan's piece",
    actions = {
      Action.Direction:new(3298, 517, 3284, { distance = 21 }),
      Action.ModelHighlight:new(avan, { distance = 25 }),
      Action.ConversationHighlight:new("I'm looking for a man named Avan Fitzharmon."),
    },
    postconditions = { Condition.ConversationText:new("what I can do") },
  },
  {
    text = "Climb down the ladder into the Dwarven Mines.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3018.5, 2565, 3450, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3018, 965, 9850, 4) },
  },
  {
    text = "Talk to Boot.",
    actions = {
      Action.Direction:new(2984, 957, 9810, { distance = 21 }),
      Action.ModelHighlight:new(boot, { distance = 24 }),
      Action.ConversationHighlight:new("very high quality"),
    },
    postconditions = { Condition.ConversationText:new("get to though") },
  },
  {
    text = "Climb down the ruins just to the east of Ardougne.",
    title = "Creating the necklace",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2696, 1265, 3283) },
    postconditions = { Condition.DistanceTo:new(2696, 2661, 9684, 4) },
  },
  {
    text = "Pull the lever up to the east.",
    warning = "Logging out will reset this puzzle.",
    actions = { Action.Direction:new(2722, 3497, 9710.15) },
    postconditions = { Condition.ModelVisible:new(upLever, { atLocation = Location:new(2722, 3497, 9710.15) }) },
  },
  {
    text = "Pull the lever up to the south.",
    actions = { Action.Direction:new(2724.15, 1501, 9669) },
    postconditions = { Condition.ModelVisible:new(upLever, { atLocation = Location:new(2724.15, 1501, 9669) }) },
  },
  {
    text = "Pull the lever down to the north.",
    actions = { Action.Direction:new(2722, 3497, 9710.15) },
    postconditions = { Condition.DistanceTo:new(2722, 3497, 9710.15, 4) },
  },
  {
    actions = { Action.Direction:new(2722, 3497, 9710.15) },
    postconditions = { Condition.ModelNotVisible:new(upLever) },
  },
  {
    text = "Pull the lever up inside the room.",
    actions = { Action.Direction:new(2722, 3545, 9718.15) },
    postconditions = { Condition.ModelVisible:new(upLever) },
  },
  {
    text = "Pull the lever up oustide the room.",
    actions = { Action.Direction:new(2722, 3497, 9710.15) },
    postconditions = { Condition.ModelVisible:new(upLever, { atLocation = Location:new(2722, 3497, 9710.15) }) },
  },
  {
    text = "Pull the lever down to the south.",
    actions = { Action.Direction:new(2724.15, 1501, 9669) },
    postconditions = { Condition.DistanceTo:new(2724, 501, 9669, 4) },
  },
  {
    actions = { Action.Direction:new(2724.15, 1501, 9669) },
    postconditions = { Condition.ModelNotVisible:new(upLever) },
  },
  {
    text = "Mine two 'Perfect' gold ore in the hellhound room.<ul><li>Turn auto-retaliate off to avoid interruption.</li></ul>",
    actions = { Action.ModelHighlight:new(perfectGoldRock, { atLocation = Location:new(2735, 997, 9694) }) },
    postconditions = { Condition.InventoryContains:new(perfectGoldOre, 2) },
  },
  {
    text = "Smelt the ores into bars.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Al Kharid lodestone",
      url = "Al_Kharid_lodestone_icon.png",
    },
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.InventoryContains:new(perfectGoldBar, 2) },
  },
  {
    text = "Craft a 'Perfect' ring and a 'Perfect' necklace with two rubies.",
    actions = { Action.ConversationHighlight:new("Yes - make a 'perfect' ring") },
    postconditions = { Condition.InventoryContains:new(perfectRing) },
  },
  {
    actions = { Action.ConversationHighlight:new("Yes - make a 'perfect' necklace") },
    postconditions = { Condition.InventoryContains:new(perfectNecklace) },
  },
  {
    text = "Return to Avan.",
    actions = {
      Action.Direction:new(3298, 517, 3284, { distance = 21 }),
      Action.ModelHighlight:new(avan, { distance = 25 }),
    },
    postconditions = { Condition.ConversationText:new("Thanks Avan") },
  },
  {
    text = "Climb the stairs in the Jolly Boar Inn.",
    title = "Johnathon's piece",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fort Forinthry lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    neededItems = { ["Antipoison/super antipoison"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3285.5, 1365, 3495, { distance = 16 }),
      Action.ModelHighlight:new(jollyBoarInnStaircase, { distance = 17 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3285, 2149, 3492, 4) },
  },
  {
    text = "Talk to Johnathon",
    actions = { Action.ModelHighlight:new(johnathon) },
    postconditions = { Condition.ConversationText:new("pouring down") },
  },
  {
    text = "Use an antipoison on him.",
    actions = {
      Action.ModelHighlight:new(johnathon),
      Action.ConversationHighlight:new("Where can I find Chronozon?"),
    },
    postconditions = { Condition.ConversationText:new("lead you to him") },
  },
  {
    text = "Enter the Edgeville Dungeon. Equip your tier 30/40 magic weapon.",
    title = "Defeating Chronozon",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Edgeville lodestone",
      url = "Edgeville_lodestone_icon.png",
    },
    neededItems = {
      ["Tier 30/40 magic weapon"] = { quantity = 1 },
      ["Runes for all blast spells"] = { quantity = 1 },
      ["Deathtouched dart"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3097, 965, 3468, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3096, 4069, 9868, 4) },
  },
  {
    text = "Use air blast on Chronozon.",
    actions = { Action.ModelHighlight:new(chronozon) },
    postconditions = { Condition.ChatText:new("Air Blast seems") },
  },
  {
    text = "Use water blast on Chronozon.",
    actions = { Action.ModelHighlight:new(chronozon) },
    postconditions = { Condition.ChatText:new("Water Blast seems") },
  },
  {
    text = "Use earth blast on Chronozon.",
    actions = { Action.ModelHighlight:new(chronozon) },
    postconditions = { Condition.ChatText:new("Earth Blast seems") },
  },
  {
    text = "Use fire blast on Chronozon.",
    actions = { Action.ModelHighlight:new(chronozon) },
    postconditions = { Condition.ChatText:new("First Blast seems") },
  },
  {
    text = "Kill Chronozon.",
    actions = { Action.ModelHighlight:new(chronozon) },
    postconditions = { Condition.ModelVisible:new(johnathonCrestPart) },
  },
  {
    text = "Pick up the crest piece.",
    actions = { Action.ModelHighlight:new(johnathonCrestPart) },
    postconditions = { Condition.InventoryContains:new(johnathonCrestPart) },
  },
  {
    text = "Return to Dimintheis.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3280, 1285, 3403, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["dimintheis"], { distance = 8 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Family Crest",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1018310400,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Crafting", 40),
    Types.QuestReq.skill("Mining", 40),
    Types.QuestReq.skill("Smithing", 40),
    Types.QuestReq.skill("Magic", 59, true),
    Types.QuestReq.ironmanOnlySkill("Cooking", 45, true),
    Types.QuestReq.ironmanOnlySkill("Fishing", 50, true),
  },
  neededItems = {
    ["Cooked shrimp"] = { quantity = 1, model = shrimp },
    ["Cooked salmon"] = { quantity = 1, model = salmon },
    ["Cooked tuna"] = { quantity = 1, model = tuna },
    ["Cooked bass"] = { quantity = 1, model = bass },
    ["Cooked swordfish"] = { quantity = 1, model = swordfish },
    ["Cut rubies"] = { quantity = 2, model = Models.items["ruby"] },
    ["Antipoison/super antipoison"] = { quantity = 1 },
    ["Tier 30/40 magic weapon"] = { quantity = 1 },
    ["Runes for all blast spells"] = { quantity = 1 },
  },
  recommendedItems = { ["Archaeology journal"] = { quantity = 1 } },
  combatNPCs = { ["Chronozon"] = { level = "84", quantity = 1 } },
})
