local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local dwarvenBoatman = Model.new(6090, {
  [4362] = Vertex.new(-26, 486, -90, 151, 146, 139),
  [4413] = Vertex.new(26, 486, -90, 72, 50, 37),
  [4447] = Vertex.new(25, 495, -86, 164, 152, 150),
  [4455] = Vertex.new(25, 495, -86, 164, 152, 150),
  [4471] = Vertex.new(-25, 495, -86, 164, 152, 150),
})
local stonemason = Model.new(6219, {
  [6039] = Vertex.new(45, 513, -78, 76, 48, 31),
  [6047] = Vertex.new(45, 523, -71, 76, 48, 31),
  [6083] = Vertex.new(-43, 522, -77, 76, 48, 31),
  [6087] = Vertex.new(-46, 513, -72, 76, 48, 31),
  [6089] = Vertex.new(-46, 527, -26, 76, 48, 31),
})
--#endregion
--#region Objects
local stairs = Model.new(522, {
  [369] = Vertex.new(6208, 1920, 4608, 49, 49, 45),
  [446] = Vertex.new(6592, 1984, 4608, 49, 49, 45),
  [447] = Vertex.new(6592, 1920, 4608, 49, 49, 45),
  [452] = Vertex.new(6592, 2048, 4544, 49, 49, 45),
  [458] = Vertex.new(6592, 2112, 4480, 49, 49, 45),
})
local stoneCrusher = Model.new(1479, {
  [368] = Vertex.new(-20, 356, 124, 52, 48, 48),
  [374] = Vertex.new(48, 356, 124, 52, 48, 48),
  [375] = Vertex.new(24, 356, 124, 52, 48, 48),
  [429] = Vertex.new(-44, 356, 124, 52, 48, 48),
  [722] = Vertex.new(48, 356, 124, 52, 48, 48),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local taskList = Model.new(177, {
  [36] = Vertex.new(56, 8, -132, 125, 117, 96),
  [69] = Vertex.new(-16, 4, -172, 113, 106, 87),
  [72] = Vertex.new(-8, 4, 208, 113, 106, 87),
  [77] = Vertex.new(16, 4, 192, 113, 106, 87),
  [108] = Vertex.new(136, 12, 44, 66, 65, 61),
})
local blackGuardWarhammer = Model.multi({
  Model.new(1248, {
    [175] = Vertex.new(-37, 63, 163, 55, 51, 55),
    [176] = Vertex.new(-41, 58, 163, 55, 51, 55),
    [178] = Vertex.new(-41, 58, 163, 55, 51, 55),
    [179] = Vertex.new(-37, 54, 165, 55, 51, 55),
    [185] = Vertex.new(-37, 63, 163, 43, 39, 43),
  }),
  Model.new(24, {
    [5] = Vertex.new(-8, 17, -19, 87, 80, 87),
    [17] = Vertex.new(-8, 52, -24, 87, 80, 87),
    [19] = Vertex.new(11, 53, -20, 77, 71, 77),
    [21] = Vertex.new(7, 56, 1, 77, 71, 77),
    [22] = Vertex.new(11, 53, -20, 77, 71, 77),
  }),
})
local crackedBlackGuardWarhammer = Model.multi({
  Model.new(1332, {
    [680] = Vertex.new(-83, 72, 120, 32, 30, 32),
    [1226] = Vertex.new(-8, 65, 129, 66, 61, 66),
    [1242] = Vertex.new(-33, 45, 126, 49, 45, 49),
    [1254] = Vertex.new(-33, 45, 126, 61, 56, 61),
    [1298] = Vertex.new(-8, 65, 129, 66, 61, 66),
  }),
  Model.new(24, {
    [5] = Vertex.new(-12, 19, -25, 87, 80, 87),
    [17] = Vertex.new(-12, 58, -31, 87, 80, 87),
    [19] = Vertex.new(10, 59, -26, 77, 71, 77),
    [21] = Vertex.new(5, 62, -3, 77, 71, 77),
    [22] = Vertex.new(10, 59, -26, 77, 71, 77),
  }),
})
local crushedBlackGuardWarhammer = Model.multi({
  Model.new(1356, {
    [300] = Vertex.new(-10, 75, 110, 36, 33, 36),
    [1260] = Vertex.new(-46, 50, 122, 49, 45, 49),
    [1272] = Vertex.new(-46, 50, 122, 61, 56, 61),
    [1322] = Vertex.new(-14, 72, 122, 66, 61, 66),
    [1325] = Vertex.new(-14, 72, 122, 66, 61, 66),
  }),
  Model.new(24, {
    [5] = Vertex.new(-8, 19, -17, 87, 80, 87),
    [17] = Vertex.new(-8, 58, -23, 87, 80, 87),
    [19] = Vertex.new(14, 59, -18, 77, 71, 77),
    [21] = Vertex.new(9, 62, 5, 77, 71, 77),
    [22] = Vertex.new(14, 59, -18, 77, 71, 77),
  }),
})
local rustedBlackGuardWarhammer = Model.multi({
  Model.new(1410, {
    [74] = Vertex.new(13, 99, 118, 46, 42, 46),
    [83] = Vertex.new(13, 99, 118, 39, 36, 39),
    [503] = Vertex.new(2, 67, 158, 52, 48, 52),
    [506] = Vertex.new(2, 67, 158, 52, 48, 52),
    [1326] = Vertex.new(-46, 50, 121, 61, 56, 61),
  }),
  Model.new(24, {
    [5] = Vertex.new(-8, 19, -18, 87, 80, 87),
    [17] = Vertex.new(-8, 58, -24, 87, 80, 87),
    [19] = Vertex.new(14, 59, -19, 77, 71, 77),
    [21] = Vertex.new(9, 62, 4, 77, 71, 77),
    [22] = Vertex.new(14, 59, -19, 77, 71, 77),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Doric in his hut north of Falador.",
    title = "Nostalgic Warhammers",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = { ["Ring of wealth"] = { quantity = 1 } },
    actions = { Action.Direction:new(2958, 869, 3439) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["doric"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["doric"]),
      Action.ConversationHighlight:new("Do you have any more Smithing tasks for me?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["doric"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Doric.",
    actions = { Action.ModelHighlight:new(Models.npcs["doric"]) },
    postconditions = { Condition.ConversationText:new("I'm on it") },
  },
  {
    text = "Talk to Santiri in the north-westernmost building in western Keldagrim.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fremennik Province lodestone",
      url = "Fremennik_Province_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2829, 1285, 10228),
      Action.ModelHighlight:new(dwarvenBoatman),
      Action.ConversationHighlight:new("Yes, I'm ready and don't mind it taking a few minutes."),
    },
    postconditions = { Condition.ModelVisible:new(Models.npcs["santiri"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["santiri"]),
      Action.ConversationHighlight:new("Doric sent me here to make your dreams come true."),
      Action.ConversationHighlight:new("I'll ask him for it now."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["santiri"]) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(taskList) },
  },
  {
    text = "Climb up the stairs to the south.",
    actions = { Action.ModelHighlight:new(stairs) },
    postconditions = { Condition.DistanceToWithHeight:new(2828, 2885, 10214, 4) },
  },
  {
    text = "Talk to the Supreme Commander.",
    actions = { Action.ModelHighlight:new(Models.npcs["supreme commander"]) },
    postconditions = { Condition.InventoryContains:new(blackGuardWarhammer) },
  },
  {
    text = "Use the anvil to the south.",
    actions = { Action.Direction:new(2828, 2325, 10201) },
    postconditions = { Condition.ChatText:new("crack the Black") },
  },
  {
    text = "Talk to Santiri.",
    actions = { Action.Direction:new(2829, 1285, 10228) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["santiri"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["santiri"]),
      Action.ConversationHighlight:new("I have your warhammer."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["santiri"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("be right back") },
  },
  {
    text = "Talk to the Librarian to the east.",
    actions = { Action.Direction:new(2861, 1285, 10225) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["librarian"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["librarian"]),
      Action.ConversationHighlight:new("Can you tell me about first generation black warhammers?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["librarian"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("can find out") },
  },
  {
    text = "Talk to the Stonemason to the south.",
    actions = { Action.Direction:new(2848.5, 1925, 10184) },
    postconditions = { Condition.ModelVisible:new(stonemason) },
  },
  {
    actions = {
      Action.ModelHighlight:new(stonemason),
      Action.ConversationHighlight:new("Do you know anything about crushing warhammers?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(stonemason) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("it if you want") },
  },
  {
    text = "Use the cracked warhammer on the stone crusher.",
    actions = {
      Action.ModelHighlight:new(stoneCrusher),
      Action.InventoryHighlight:new(crackedBlackGuardWarhammer),
    },
    postconditions = { Condition.ChatText:new("crushed under the stone") },
  },
  {
    text = "Talk to Santiri.",
    actions = { Action.Direction:new(2829, 1285, 10228) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["santiri"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["santiri"]),
      Action.ConversationHighlight:new("I have your warhammer."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["santiri"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Use the crushed warhammer on the well to the east.",
    actions = {
      Action.Direction:new(2897.5, 1877, 10219.5),
      Action.InventoryHighlight:new(crushedBlackGuardWarhammer),
    },
    postconditions = { Condition.InventoryContains:new(rustedBlackGuardWarhammer) },
  },
  {
    text = "Return to Santiri.",
    actions = { Action.Direction:new(2829, 1285, 10228) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["santiri"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["santiri"]),
      Action.ConversationHighlight:new("I have your warhammer."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["santiri"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task VII (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1350432000,
  prereqQuests = { "Doric's Task VI (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 65),
  },
  neededItems = {},
  recommendedItems = {
    ["Ring of slaying"] = { quantity = 1 },
    ["Ring of wealth"] = { quantity = 1 },
    ["Fairy ring access"] = { quantity = 1 },
  },
  combatNPCs = {},
})
