local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local commanderMontai = Model.new(5985, {
  [2857] = Vertex.new(0, 440, -10, 14, 166, 40),
  [2858] = Vertex.new(2, 440, -12, 14, 166, 40),
  [2859] = Vertex.new(-2, 440, -12, 14, 166, 40),
  [3717] = Vertex.new(-8, 535, 58, 59, 60, 55),
  [3740] = Vertex.new(8, 551, 38, 59, 60, 55),
})
--#endregion
--#region Items
local steelPlatebody = Model.new(576, {
  [63] = Vertex.new(-120, 5, 118, 133, 128, 140),
  [476] = Vertex.new(-146, 5, -139, 26, 26, 28),
  [480] = Vertex.new(146, 5, -139, 26, 26, 28),
  [554] = Vertex.new(-146, 5, -139, 94, 90, 98),
  [561] = Vertex.new(146, 5, -139, 94, 90, 98),
})
local runeSword = Model.new(369, {
  [5] = Vertex.new(152, 1, -161, 47, 35, 19),
  [54] = Vertex.new(195, 1, -151, 60, 94, 115),
  [74] = Vertex.new(190, 1, -158, 36, 55, 68),
  [80] = Vertex.new(195, 1, -151, 36, 55, 68),
  [81] = Vertex.new(190, 1, -158, 36, 55, 68),
})
--#endregion
--#region Quest Items
local gnomePlatebody = Model.new(480, {
  [2] = Vertex.new(-92, 0, 51, 24, 22, 22),
  [5] = Vertex.new(92, 0, 51, 24, 22, 22),
  [24] = Vertex.new(-104, 0, 69, 70, 72, 66),
  [138] = Vertex.new(-100, 0, 45, 72, 74, 68),
  [150] = Vertex.new(100, 0, 45, 72, 74, 68),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Doric in his hut north of Falador.",
    title = "A gnomish request",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = {
      ["Rune swords"] = { quantity = 3 },
      ["Rune bars (in metal bank)"] = { quantity = 6 },
      ["Steel platebodies"] = { quantity = 4 },
    },
    recommendedItems = {},
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
    postconditions = { Condition.ConversationText:new("work on it now") },
  },
  {
    text = "Smith 4 Gnome platebodies.",
    actions = { Action.Direction:new(2962, 1269, 3439) },
    postconditions = { Condition.InventoryContains:new(gnomePlatebody, 4) },
  },
  {
    text = "Smith 3 rune swords + 1.",
    actions = { Action.Direction:new(2962, 1269, 3439) },
    postconditions = { Condition.ChatText:new("finish smithing: Rune sword + 1") }, --not working, but is correct
  },
  {
    actions = { Action.Direction:new(2962, 1269, 3439) },
    postconditions = { Condition.ChatText:new("finish smithing: Rune sword + 1") }, --not working, but is correct
  },
  {
    actions = { Action.Direction:new(2962, 1269, 3439) },
    postconditions = { Condition.ChatText:new("finish smithing: Rune sword + 1") }, --not working, but is correct
  },
  {
    text = "Talk to Commander Montai on the Khazard Battlefield south of Ardougne.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Kandarin monastery Teleport",
      url = "Kandarin_monastery_Teleport_icon.png",
    },
    actions = { Action.Direction:new(2520, 989, 3209) },
    postconditions = { Condition.ModelVisible:new(commanderMontai) },
  },
  {
    actions = { Action.ModelHighlight:new(commanderMontai) },
    jumpconditions = { Condition.ModelNotVisible:new(commanderMontai) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task V (miniquest)",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.short,
  releaseDate = 1350432000,
  prereqQuests = { "Doric's Task IV (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 50),
    Types.QuestReq.ironmanOnlySkill("Mining", 50, true),
  },
  neededItems = {
    ["Rune swords"] = { quantity = 3, model = runeSword },
    ["Rune bars (in metal bank)"] = { quantity = 6 },
    ["Steel platebodies"] = { quantity = 4, model = steelPlatebody },
  },
  recommendedItems = {},
  combatNPCs = {},
})
