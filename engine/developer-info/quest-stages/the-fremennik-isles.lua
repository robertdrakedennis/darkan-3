local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local slugHemligssen = Model.new(4827, {
  [3394] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3399] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [3402] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3404] = Vertex.new(7, 724, -51, 108, 80, 56),
  [4489] = Vertex.new(0, 735, -7, 29, 141, 128),
})
local thakkradSigmundson = Model.new(5958, {
  [3366] = Vertex.new(43, 766, -19, 55, 49, 42),
  [3372] = Vertex.new(-43, 766, -19, 55, 49, 42),
  [3378] = Vertex.new(-4, 771, -63, 55, 49, 42),
  [3380] = Vertex.new(4, 771, -63, 55, 49, 42),
  [5222] = Vertex.new(5, 766, -51, 55, 49, 42),
})
local hringHring = Model.new(4932, {
  [3343] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3347] = Vertex.new(7, 724, -51, 108, 80, 56),
  [3361] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3366] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [4688] = Vertex.new(40, 711, -12, 93, 73, 48),
})
local skuliMyrka = Model.new(4695, {
  [3] = Vertex.new(43, 766, -19, 55, 49, 42),
  [9] = Vertex.new(-43, 766, -19, 55, 49, 42),
  [15] = Vertex.new(-4, 771, -63, 55, 49, 42),
  [17] = Vertex.new(4, 771, -63, 55, 49, 42),
  [3959] = Vertex.new(5, 766, -51, 55, 49, 42),
})
local vanliggaGastfrihet = Model.new(4509, {
  [2091] = Vertex.new(24, 738, -38, 31, 30, 29),
  [2115] = Vertex.new(-24, 738, -38, 31, 30, 29),
  [2137] = Vertex.new(-2, 716, -57, 108, 80, 56),
  [2143] = Vertex.new(2, 716, -57, 108, 80, 56),
  [2147] = Vertex.new(6, 716, -52, 108, 80, 56),
})
local keepaKettilon = Model.new(5238, {
  [3868] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3873] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [3876] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3878] = Vertex.new(7, 724, -51, 108, 80, 56),
  [4135] = Vertex.new(0, 735, -7, 29, 141, 128),
})
local raumUrdaStein = Model.new(4968, {
  [3085] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3093] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3095] = Vertex.new(7, 724, -51, 108, 80, 56),
  [4371] = Vertex.new(-65, 670, 50, 43, 43, 13),
  [4402] = Vertex.new(65, 670, 50, 43, 43, 13),
})
local flosiDalksson = Model.new(4365, {
  [2182] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [2187] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [2190] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2192] = Vertex.new(7, 724, -51, 108, 80, 56),
  [4208] = Vertex.new(-30, 721, -31, 134, 81, 27),
})
local yak = Model.new(29400, {
  [4666] = Vertex.new(144, 649, -679, 127, 127, 127),
  [6753] = Vertex.new(-145, 696, -647, 127, 127, 127),
  [7016] = Vertex.new(145, 656, -689, 127, 127, 127),
  [7020] = Vertex.new(145, 656, -689, 127, 127, 127),
  [7022] = Vertex.new(145, 656, -689, 127, 127, 127),
})
local jofridr = Model.new(4251, {
  [2007] = Vertex.new(24, 738, -38, 31, 30, 29),
  [2031] = Vertex.new(-24, 738, -38, 31, 30, 29),
  [2053] = Vertex.new(-2, 716, -57, 108, 80, 56),
  [2059] = Vertex.new(2, 716, -57, 108, 80, 56),
  [2063] = Vertex.new(6, 716, -52, 108, 80, 56),
})
local iceTrollKing = Model.new(5766, {
  [1] = Vertex.new(8, 580, 0, 95, 84, 73),
  [2] = Vertex.new(0, 556, 0, 95, 84, 73),
  [3] = Vertex.new(-8, 580, 0, 95, 84, 73),
  [4308] = Vertex.new(24, 340, -160, 136, 142, 148),
  [4316] = Vertex.new(-24, 340, -160, 136, 142, 148),
})
--#endregion
--#region Objects
local arcticPine = Model.new(1590, {
  [1229] = Vertex.new(7803, 2602, 5264, 137, 137, 137),
  [1239] = Vertex.new(7732, 2607, 5290, 137, 137, 137),
  [1349] = Vertex.new(7820, 2599, 5196, 137, 137, 137),
  [1353] = Vertex.new(7825, 2615, 5193, 137, 137, 137),
  [1359] = Vertex.new(7801, 2607, 5256, 137, 137, 137),
})
local caveEntrance = Model.new(945, {
  [37] = Vertex.new(1280, 1696, 552, 142, 143, 155),
  [44] = Vertex.new(1280, 1696, 552, 142, 143, 155),
  [422] = Vertex.new(1616, 1696, 1024, 142, 143, 155),
  [449] = Vertex.new(1264, 2160, 1488, 142, 143, 155),
  [455] = Vertex.new(1444, 2160, 1488, 142, 143, 155),
})
local deadIceTrollKing = Model.new(5766, {
  [1] = Vertex.new(228, 304, -100, 114, 102, 88),
  [2] = Vertex.new(220, 280, -92, 114, 102, 88),
  [3] = Vertex.new(212, 304, -108, 114, 102, 88),
  [3402] = Vertex.new(300, 28, -124, 165, 171, 177),
  [3980] = Vertex.new(256, 20, -140, 165, 171, 177),
})
--#endregion
--#region Items
local rawTuna = Model.new(444, {
  [45] = Vertex.new(128, 0, -92, 163, 151, 150),
  [48] = Vertex.new(128, 0, -92, 163, 151, 150),
  [50] = Vertex.new(128, 0, -92, 163, 151, 150),
  [52] = Vertex.new(160, 0, 56, 163, 151, 150),
  [55] = Vertex.new(160, 0, 56, 163, 151, 150),
})
local splitLogs = Model.new(228, {
  [8] = Vertex.new(232, 0, -56, 146, 121, 76),
  [20] = Vertex.new(232, 0, -56, 111, 92, 58),
  [117] = Vertex.new(-116, 68, -196, 146, 121, 76),
  [128] = Vertex.new(192, 28, 160, 146, 121, 76),
  [132] = Vertex.new(192, 28, 160, 146, 121, 76),
})
local yakHair = Model.new(471, {
  [3] = Vertex.new(-24, 16, -224, 56, 50, 35),
  [6] = Vertex.new(-24, 16, -224, 56, 50, 35),
  [247] = Vertex.new(228, 4, -156, 56, 50, 35),
  [250] = Vertex.new(228, 4, -156, 56, 50, 35),
  [253] = Vertex.new(228, 4, -156, 56, 50, 35),
})
local yakHide = Model.new(534, {
  [241] = Vertex.new(136, 40, -120, 56, 44, 29),
  [244] = Vertex.new(136, 40, -120, 56, 44, 29),
  [352] = Vertex.new(28, 164, 212, 56, 44, 29),
  [355] = Vertex.new(28, 164, 212, 56, 44, 29),
  [391] = Vertex.new(-152, 196, -80, 56, 44, 29),
})
local curedYakhide = Model.new(471, {
  [12] = Vertex.new(-128, 0, 92, 65, 50, 50),
  [17] = Vertex.new(-128, 0, 92, 65, 50, 50),
  [28] = Vertex.new(100, 0, 152, 65, 50, 50),
  [34] = Vertex.new(100, 0, 152, 65, 50, 50),
  [41] = Vertex.new(100, 0, 152, 65, 50, 50),
})
local yakHideBody = Model.new(600, {
  [68] = Vertex.new(-88, 24, 164, 154, 153, 141),
  [124] = Vertex.new(148, 28, 116, 79, 70, 61),
  [127] = Vertex.new(-144, 28, 124, 79, 70, 61),
  [367] = Vertex.new(92, 92, 132, 73, 65, 56),
  [463] = Vertex.new(-92, 92, 132, 73, 65, 56),
})
local yakHideLegs = Model.new(267, {
  [1] = Vertex.new(-100, 84, -8, 59, 49, 38),
  [4] = Vertex.new(100, 80, -8, 59, 49, 38),
  [8] = Vertex.new(108, 20, -44, 72, 60, 46),
  [11] = Vertex.new(-116, 20, -40, 72, 60, 46),
  [54] = Vertex.new(96, 0, -164, 84, 75, 65),
})
local roundSheild = Model.new(438, {
  [127] = Vertex.new(-112, 0, 96, 123, 117, 64),
  [130] = Vertex.new(-112, 0, 96, 123, 117, 64),
  [133] = Vertex.new(-76, 4, 136, 123, 117, 64),
  [136] = Vertex.new(-76, 4, 136, 123, 117, 64),
  [141] = Vertex.new(-76, 4, 136, 123, 117, 64),
})
--#endregion
--#region Quest Items
local jesterHat = Model.new(396, {
  [366] = Vertex.new(-84, 92, -40, 178, 176, 16),
  [372] = Vertex.new(-72, 92, -48, 178, 176, 16),
  [378] = Vertex.new(-72, 92, -48, 178, 176, 16),
  [384] = Vertex.new(-72, 92, -48, 178, 176, 16),
  [390] = Vertex.new(-72, 92, -48, 178, 176, 16),
})
local jesterTop = Model.new(267, {
  [97] = Vertex.new(68, 8, 116, 187, 177, 177),
  [100] = Vertex.new(-44, 8, 116, 187, 177, 177),
  [103] = Vertex.new(12, 44, 108, 187, 177, 177),
  [192] = Vertex.new(72, 24, -64, 156, 146, 49),
  [200] = Vertex.new(132, 12, 24, 156, 146, 49),
})
local jesterTights = Model.new(165, {
  [2] = Vertex.new(48, 28, 188, 54, 127, 12),
  [24] = Vertex.new(-28, 36, -192, 134, 23, 12),
  [26] = Vertex.new(-52, 24, -196, 134, 23, 12),
  [74] = Vertex.new(-52, 24, -196, 134, 23, 12),
  [99] = Vertex.new(44, 36, -192, 131, 129, 12),
})
local jesterBoots = Model.new(366, {
  [1] = Vertex.new(24, 72, 68, 134, 23, 12),
  [61] = Vertex.new(24, 72, 68, 134, 23, 12),
  [64] = Vertex.new(24, 52, 76, 134, 23, 12),
  [66] = Vertex.new(24, 72, 68, 134, 23, 12),
  [67] = Vertex.new(24, 52, 76, 134, 23, 12),
})
local royalDecree = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 148, 148, 136),
  [5] = Vertex.new(-72, 0, -56, 148, 148, 136),
  [187] = Vertex.new(-84, 28, -84, 91, 65, 8),
  [190] = Vertex.new(-84, 28, -84, 91, 65, 8),
  [195] = Vertex.new(-84, 28, -84, 91, 65, 8),
})
local iceTrollKingHead = Model.new(2004, {
  [122] = Vertex.new(84, 328, 236, 97, 97, 89),
  [177] = Vertex.new(-92, 328, 236, 97, 97, 89),
  [180] = Vertex.new(-92, 328, 236, 85, 85, 78),
  [183] = Vertex.new(-92, 328, 236, 85, 85, 78),
  [186] = Vertex.new(-92, 328, 236, 97, 97, 89),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Right click and talk to Mord Gunnars on the northern Rellekka dock about Jatizso's history.",
    title = "Starting out",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    neededItems = {
      ["Raw tuna"] = { quantity = 1 },
      ["Tin ore"] = { quantity = 8 },
      ["Coal"] = { quantity = 7 },
      ["Mithril ore"] = { quantity = 6 },
    },
    actions = {
      Action.Direction:new(2644, 325, 3709, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["mord gunnars"], { distance = 16 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Mord Gunnars.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["mord gunnars"]),
      Action.ConversationHighlight:new("Can you ferry me to Jatizso?"),
    },
    postconditions = { Condition.DistanceTo:new(2420, 389, 3782, 4) },
  },
  {
    text = "Talk to King Gjuki Sorvott IV in the building with tiled floors.",
    title = "Send in the clowns",
    actions = {
      Action.Direction:new(2407, 965, 3804, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["king gjuki sorvott iv"], { distance = 3 }),
    },
    postconditions = { Condition.ConversationText:new("you can buy some from my fishmonger") },
  },
  {
    text = "Talk to the king with a raw tuna.<ul><li>Can be obtained from the cage/harpoon fishing spots to the south.</li></ul>",
    actions = {
      Action.ModelHighlight:new(Models.npcs["king gjuki sorvott iv"]),
      Action.InventoryHighlight:new(rawTuna),
    },
    postconditions = { Condition.ConversationText:new("Excellent") },
  },
  {
    text = "Talk to the King again, then give him your ores.<ul><li>There is a mine down the stairs to the north-west if you didn't bring them.</li></ul>",
    actions = { Action.ModelHighlight:new(Models.npcs["king gjuki sorvott iv"]) },
    postconditions = { Condition.ConversationText:new("you'll find him") },
  },
  {
    text = "Open the chest (behind the throne) and take the silly jester outfit.",
    actions = {
      Action.Direction:new(2407, 965, 3800),
      Action.ConversationHighlight:new("Take the jester's hat."),
    },
    postconditions = {
      Condition.InventoryContains:new(jesterHat),
      Condition.ChatText:new("the complete jester's costume"),
    },
  },
  {
    actions = {
      Action.Direction:new(2407, 965, 3800),
      Action.ConversationHighlight:new("Take the jester's top."),
    },
    postconditions = {
      Condition.InventoryContains:new(jesterTop),
      Condition.ChatText:new("the complete jester's costume"),
    },
  },
  {
    actions = {
      Action.Direction:new(2407, 965, 3800),
      Action.ConversationHighlight:new("Take the jester's tights."),
    },
    postconditions = {
      Condition.InventoryContains:new(jesterTights),
      Condition.ChatText:new("the complete jester's costume"),
    },
  },
  {
    actions = { Action.Direction:new(2407, 965, 3800) },
    postconditions = {
      Condition.InventoryContains:new(jesterBoots),
      Condition.ChatText:new("the complete jester's costume"),
    },
  },
  {
    text = "Return to Rellekka. ",
    actions = {
      Action.Direction:new(2421, 389, 3781, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["mord gunnars"], { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2643, 325, 3710, 4) },
  },
  {
    text = "Travel-Neitiznot with Maria Gunnars.",
    actions = {
      Action.Direction:new(2644, 325, 3710, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["maria gunnars"], { distance = 8 }),
    },
    postconditions = { Condition.DistanceTo:new(2310, 325, 3782, 4) },
  },
  {
    text = "Equip the silly jester outfit, with nothing in your hands, and talk to Slug Hemligssen, north of the bank.",
    title = "Clowning around",
    actions = {
      Action.Direction:new(2337, 1029, 3810, { distance = 16 }),
      Action.ModelHighlight:new(slugHemligssen, { distance = 16 }),
      Action.InventoryHighlight:new(jesterHat),
      Action.InventoryHighlight:new(jesterTop),
      Action.InventoryHighlight:new(jesterTights),
      Action.InventoryHighlight:new(jesterBoots),
      Action.ConversationHighlight:new("Free stuff please."),
      Action.ConversationHighlight:new("I am ready."),
    },
    postconditions = { Condition.ConversationText:new("will make sure the King is informed") },
  },
  {
    text = "Talk to Mawnis Burowgar, south of the bank.",
    warning = "Dismiss any Summoning familiars and pets.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = {
      Condition.InInstance:new(),
    },
  },
  { postconditions = { Condition.ConversationText:new("was much better than the last jester") } },
  {
    text = "Return to Slug Hemligssen.",
    actions = {
      Action.Direction:new(2337, 1029, 3810, { distance = 16 }),
      Action.ModelHighlight:new(slugHemligssen, { distance = 16 }),
      Action.ConversationHighlight:new("Yes I have."),
      Action.ConversationHighlight:new("They will be ready in two days."),
      Action.ConversationHighlight:new("Seventeen militia have been trained."),
      Action.ConversationHighlight:new("There are two bridges to repair."),
    },
    postconditions = { Condition.ConversationText:new("take your jester's costume off first") },
  },
  {
    text = "Unequip the silly jester outfit (keep it for later).",
    title = "Bridge repairs",
    neededItems = { ["Split log"] = { quantity = 8 }, ["Rope"] = { quantity = 8 } },
    postconditions = { Condition.InventoryContains:new(jesterHat) },
  },
  { postconditions = { Condition.InventoryContains:new(jesterTop) } },
  { postconditions = { Condition.InventoryContains:new(jesterTights) } },
  { postconditions = { Condition.InventoryContains:new(jesterBoots) } },
  {
    text = "Kill 8 yaks for their hair, and pick up 3 yak hides too.",
    actions = {
      Action.ModelHighlight:new(yak, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(yakHair),
    },
    postconditions = {
      Condition.InventoryContains:new(yakHair, 8),
      Condition.InventoryContains:new(Models.items["rope"], 8),
    },
  },
  {
    actions = { Action.Direction:new(2352, 1029, 3794) },
    postconditions = {
      Condition.InventoryContains:new(Models.items["rope"], 8),
    },
  },
  {
    text = "Chop an Arctic Pine for 8 logs.",
    actions = { Action.ModelHighlight:new(arcticPine) },
    postconditions = {
      Condition.InventoryContains:new(Models.items["arctic pine logs"], 8),
      Condition.InventoryContains:new(splitLogs, 8),
    },
  },
  {
    text = "Craft 8 split logs.",
    actions = { Action.Direction:new(2324, 1109, 3831) },
    postconditions = { Condition.InventoryContains:new(splitLogs, 8) },
  },
  {
    text = "Talk to Mawnis Burowgar.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("shall speak with you again once you have") },
  },
  {
    text = "Talk to Mawnis Burowgar again.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("you again once you have repaired the bridges") },
  },
  {
    text = "Right click bridges, north of Neitiznot, to repair them.",
    actions = {
      Action.Direction:new(2355, 1285, 3840),
      Action.Direction:new(2314, 1357, 3840),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(splitLogs) },
  },
  {
    text = "Talk to Mawnis Burowgar.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("Then make your way with haste") },
  },
  {
    text = "Return to Rellekka, and take the boat to Jatizso.",
    title = "Taxes",
    actions = {
      Action.Direction:new(2311, 325, 3781, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["maria gunnars"], { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2643, 325, 3710, 4) },
  },
  {
    actions = {
      Action.Direction:new(2644, 325, 3709, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["mord gunnars"], { distance = 16 }),
    },
    postconditions = { Condition.DistanceTo:new(2420, 389, 3782, 4) },
  },
  {
    text = "Return to King Gjuki Sorvott IV on Jatizso.",
    actions = {
      Action.Direction:new(2407, 965, 3804, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["king gjuki sorvott iv"], { distance = 3 }),
    },
    postconditions = { Condition.ConversationText:new("you for helping our fine city") },
  },
  {
    text = "Talk to Hring Hring (8,000).",
    actions = {
      Action.Direction:new(2396, 965, 3797, { distance = 4 }),
      Action.ModelHighlight:new(hringHring, { distance = 6 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Skuli Myrka (6,000).",
    actions = {
      Action.Direction:new(2396, 965, 3805, { distance = 4 }),
      Action.ModelHighlight:new(skuliMyrka, { distance = 4 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Vanligga Gastfrihet (5,000).",
    actions = {
      Action.Direction:new(2407, 965, 3813, { distance = 4 }),
      Action.ModelHighlight:new(vanliggaGastfrihet, { distance = 4 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Keepa Kettilon (5,000).",
    actions = {
      Action.Direction:new(2417, 965, 3815, { distance = 3 }),
      Action.ModelHighlight:new(keepaKettilon, { distance = 3 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  {
    text = "Return to King Gjuki Sorvott IV.",
    actions = {
      Action.Direction:new(2407, 965, 3804, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["king gjuki sorvott iv"], { distance = 3 }),
    },
    postconditions = { Condition.ConversationText:new("Of course you do") },
  },
  {
    text = "Talk to Hring Hring (1,000).",
    actions = {
      Action.Direction:new(2396, 965, 3797, { distance = 4 }),
      Action.ModelHighlight:new(hringHring, { distance = 6 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Raum Urda-Stein (1,000).",
    actions = {
      Action.Direction:new(2396, 965, 3797, { distance = 4 }),
      Action.ModelHighlight:new(raumUrdaStein, { distance = 6 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Skuli Myrka (1,000).",
    actions = {
      Action.Direction:new(2396, 965, 3805, { distance = 4 }),
      Action.ModelHighlight:new(skuliMyrka, { distance = 4 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Keepa Kettilon (1,000).",
    actions = {
      Action.Direction:new(2417, 965, 3815, { distance = 3 }),
      Action.ModelHighlight:new(keepaKettilon, { distance = 3 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  { postconditions = { Condition.ConversationInactive:new() } },
  {
    text = "Talk to Flosi Dalksson (1,000).",
    actions = {
      Action.Direction:new(2417, 965, 3815, { distance = 3 }),
      Action.ModelHighlight:new(flosiDalksson, { distance = 3 }),
      Action.ConversationHighlight:new("But rules are rules. Pay up!"),
      Action.ConversationHighlight:new("But, rules are rules. Pay up!"),
    },
    postconditions = { Condition.ConversationText:new("into the tax bag.") },
  },
  {
    text = "Return to King Gjuki Sorvott IV.",
    actions = {
      Action.Direction:new(2407, 965, 3804, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["king gjuki sorvott iv"], { distance = 3 }),
    },
    postconditions = { Condition.ConversationText:new("Neitiznot with all haste") },
  },
  {
    text = "Return to Rellekka, and take the boat to Neitiznot.",
    title = "Preparing for battle",
    neededItems = {
      ["Bronze nails"] = { quantity = 1 },
      ["Arctic pine logs"] = { quantity = 2 },
      ["Rope"] = { quantity = 1 },
      ["Yak-hide"] = { quantity = 3 },
      ["Thread"] = { quantity = 2 },
      ["Silly jester outfit"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(2421, 389, 3781, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["mord gunnars"], { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2643, 325, 3710, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["maria gunnars"]) },
    postconditions = { Condition.DistanceTo:new(2311, 325, 3781, 4) },
  },
  {
    text = "Equip the silly jester outfit, <b>unequip your weapons</b>, and talk to Slug Hemligssen.",
    actions = {
      Action.Direction:new(2337, 1029, 3810, { distance = 16 }),
      Action.ModelHighlight:new(slugHemligssen, { distance = 16 }),
      Action.InventoryHighlight:new(jesterHat),
      Action.InventoryHighlight:new(jesterTop),
      Action.InventoryHighlight:new(jesterTights),
      Action.InventoryHighlight:new(jesterBoots),
    },
    postconditions = { Condition.ConversationText:new("the Burgher and get jestering") },
  },
  {
    text = "Entertain Mawnis Burowgar again.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("that was much better than the last jester") },
  },
  {
    text = "Talk to Slug Hemligssen.",
    actions = {
      Action.Direction:new(2337, 1029, 3810, { distance = 16 }),
      Action.ModelHighlight:new(slugHemligssen, { distance = 16 }),
      Action.ConversationHighlight:new("Yes, I am."),
      Action.ConversationHighlight:new("They are in a secluded bay, near Etceteria."),
      Action.ConversationHighlight:new("They will be given some potions."),
      Action.ConversationHighlight:new("I have been helping Neitiznot."),
    },
    postconditions = { Condition.ConversationText:new("better go and talk to the King") },
  },
  {
    text = "Return to Rellekka, and take the boat to Jatizso.",
    actions = {
      Action.Direction:new(2311, 325, 3781, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["maria gunnars"], { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2643, 325, 3710, 4) },
  },
  {
    actions = {
      Action.Direction:new(2644, 325, 3709, { distance = 16 }),
      Action.ModelHighlight:new(Models.npcs["mord gunnars"], { distance = 16 }),
    },
    postconditions = { Condition.DistanceTo:new(2420, 389, 3782, 4) },
  },
  {
    text = "Talk to King Gjuki Sorvott IV in Jatizso until he gives you a Royal Decree.",
    actions = {
      Action.Direction:new(2407, 965, 3804, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["king gjuki sorvott iv"], { distance = 3 }),
    },
    postconditions = { Condition.InventoryContains:new(royalDecree) },
  },
  {
    text = "Return to Rellekka, and take the boat to Neitiznot.",
    actions = {
      Action.Direction:new(2421, 389, 3781, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["mord gunnars"], { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2643, 325, 3710, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["maria gunnars"]) },
    postconditions = { Condition.DistanceTo:new(2311, 325, 3781, 4) },
  },
  {
    text = "Unequip the silly jester outfit (bank or destroy it).",
    postconditions = { Condition.InventoryContains:new(jesterHat) },
  },
  { postconditions = { Condition.InventoryContains:new(jesterTop) } },
  { postconditions = { Condition.InventoryContains:new(jesterTights) } },
  { postconditions = { Condition.InventoryContains:new(jesterBoots) } },
  {
    text = "Talk to Mawnis Burowgar.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("shall return with the yak") },
  },
  {
    text = "Get 3 yak hides.",
    actions = { Action.ModelHighlight:new(yak, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(yakHide, 3) },
  },
  {
    text = "<i>Craft-goods</i> with Thakkrad Sigmundson.",
    actions = {
      Action.Direction:new(2335, 1029, 3798, { distance = 6 }),
      Action.ModelHighlight:new(thakkradSigmundson, { distance = 6 }),
      Action.ConversationHighlight:new("Cure my yak-hide, please."),
      Action.ConversationHighlight:new("Cure all my hides."),
    },
    postconditions = { Condition.InventoryContains:new(curedYakhide, 3) },
  },
  {
    text = "Craft yak-hide armour.",
    actions = { Action.InventoryHighlight:new(curedYakhide) },
    postconditions = { Condition.InventoryContains:new(yakHideBody) },
  },
  {
    actions = { Action.InventoryHighlight:new(curedYakhide) },
    postconditions = { Condition.InventoryContains:new(yakHideLegs) },
  },
  {
    text = "Talk to Mawnis Burowgar.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new(" shall return with a shield soon") },
  },
  {
    text = "Craft or buy a rope.",
    actions = {
      Action.ModelHighlight:new(yak, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(yakHair),
    },
    postconditions = {
      Condition.InventoryContains:new(yakHair),
      Condition.InventoryContains:new(Models.items["rope"]),
    },
  },
  {
    actions = { Action.Direction:new(2352, 1029, 3794) },
    postconditions = {
      Condition.InventoryContains:new(Models.items["rope"]),
    },
  },
  {
    text = "Buy one bronze nail from Jofridr in the bank.",
    actions = {
      Action.Direction:new(2336, 1029, 3806, { distance = 3 }),
      Action.ModelHighlight:new(jofridr, { distance = 3 }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["bronze nails"]) },
  },
  {
    text = "Chop an Arctic Pine for 2 logs.",
    actions = { Action.ModelHighlight:new(arcticPine) },
    postconditions = { Condition.InventoryContains:new(Models.items["arctic pine logs"], 2) },
  },
  {
    text = "Craft a round shield.",
    actions = { Action.Direction:new(2324, 1109, 3831) },
    postconditions = { Condition.InventoryContains:new(roundSheild) },
  },
  {
    text = "Talk to Mawnis Burowgar.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("head to the eastern end of the") },
  },
  {
    text = "Prepare for battle. Once you're ready, move to the next step manually.<ul><li>It's recommended to wear the Yak-hide armour and Fremennik round shield, and prioritise high range defence.</li><li>It is not advised to kill the boss with ranged weapons.</li></ul>",
    warning = "Familiars are not allowed.",
    title = "Regicide",
  },
  {
    text = "Enter the Ice Troll Caves to the far east of the northern-most island.",
    actions = {
      Action.Direction:new(2403, 1429, 3888, { distance = 20 }),
      Action.ModelHighlight:new(caveEntrance, { distance = 20 }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill 10 ice trolls.<ul><li>Optional: Talk to Bork for food, strength potions, and prayer potions.</li></ul>",
    warning = "Farcasting the ones on the east does not count.",
    actions = { Action.ModelHighlight:new(Models.npcs["any ice troll"], { highlightPriority = "closest" }) },
    postconditions = { Condition.ChatText:new("You have defeated enough trolls to attack the King") },
  },
  {
    text = "Cross the bridge.",
    actions = { Action.Direction:new(2385, 2197, 10263) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill and then decapitate the Ice Troll King.",
    actions = { Action.ModelHighlight:new(iceTrollKing), Action.ModelHighlight:new(deadIceTrollKing) },
    postconditions = { Condition.InventoryContains:new(iceTrollKingHead) },
  },
  {
    text = "Take the head to Mawnis Burowgar.",
    actions = {
      Action.Direction:new(2336, 1029, 3799, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["mawnis burowgar"], { distance = 4 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Fremennik Isles",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1170720000,
  prereqQuests = { "The Fremennik Trials" },
  questReqs = {
    Types.QuestReq.skill("Agility", 40),
    Types.QuestReq.skill("Construction", 20),
    Types.QuestReq.skill("Crafting", 46),
    Types.QuestReq.skill("Woodcutting", 56),
    Types.QuestReq.misc("Ores required depend on your mining level. Tin: 1-10, Coal: 11-54, Mithril: 55+."),
  },
  neededItems = {
    ["Raw tuna"] = { quantity = 1, duringQuest = true },
    ["Tin ore"] = { quantity = 8 },
    ["Coal"] = { quantity = 7, duringQuest = true },
    ["Mithril ore"] = { quantity = 6, duringQuest = true },
    ["Bronze nails"] = { quantity = 1, model = Models.items["bronze nails"], duringQuest = true },
    ["Rope"] = { quantity = 8, model = Models.items["rope"], duringQuest = true },
    ["Split logs"] = { quantity = 8, model = splitLogs, duringQuest = true },
    ["Thread"] = { quantity = 2, model = Models.items["thread"], duringQuest = true },
  },
  combatNPCs = {
    ["Ice trolls"] = { level = "Various", quantity = 10 },
    ["Ice Troll King"] = { level = "91" },
  },
})
