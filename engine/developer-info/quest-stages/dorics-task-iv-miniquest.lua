local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local suak = Model.new(6075, {
  [89] = Vertex.new(-26, 475, -105, 89, 70, 36),
  [229] = Vertex.new(25, 484, -101, 184, 189, 193),
  [253] = Vertex.new(-25, 484, -101, 184, 189, 193),
  [2649] = Vertex.new(-92, 251, -69, 129, 125, 118),
  [2678] = Vertex.new(92, 251, -69, 129, 125, 118),
})
--#endregion
--#region Objects
local adamantFullHelm = Model.new(885, {
  [1] = Vertex.new(-47, 96, 10, 16, 14, 12),
  [2] = Vertex.new(-31, 73, 43, 16, 14, 12),
  [3] = Vertex.new(-31, 78, -31, 16, 14, 12),
  [5] = Vertex.new(-40, 79, -3, 16, 14, 12),
  [6] = Vertex.new(-25, 84, -34, 16, 14, 12),
})
local adamantPlatebody = Model.new(1344, {
  [1] = Vertex.new(-27, 77, -49, 45, 37, 28),
  [2] = Vertex.new(-41, 50, -65, 45, 37, 28),
  [3] = Vertex.new(-39, 70, -39, 45, 37, 28),
  [5] = Vertex.new(-25, 70, -52, 22, 18, 14),
  [7] = Vertex.new(25, 77, -49, 45, 37, 28),
})
local adamantPlatelegs = Model.new(1260, {
  [1] = Vertex.new(-87, 68, 129, 0, 0, 0),
  [2] = Vertex.new(-89, 57, 144, 0, 0, 0),
  [3] = Vertex.new(-81, 67, 144, 0, 0, 0),
  [5] = Vertex.new(-93, 58, 129, 0, 0, 0),
  [7] = Vertex.new(-97, 16, 130, 0, 0, 0),
})
local adamantGauntlets = Model.new(348, {
  [1] = Vertex.new(-112, 0, 59, 0, 0, 0),
  [2] = Vertex.new(-114, 36, 63, 0, 0, 0),
  [3] = Vertex.new(-127, 16, 49, 0, 0, 0),
  [5] = Vertex.new(-98, 20, 65, 0, 0, 0),
  [7] = Vertex.new(45, 36, 108, 0, 0, 0),
})
local adamantBoots = Model.new(480, {
  [1] = Vertex.new(-64, 80, -8, 58, 76, 60),
  [2] = Vertex.new(-36, 84, 32, 58, 76, 60),
  [3] = Vertex.new(-57, 82, 20, 58, 76, 60),
  [4] = Vertex.new(64, 80, -8, 58, 76, 60),
  [5] = Vertex.new(57, 82, 20, 58, 76, 60),
})
local adamantBurialSet
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Doric in his hut north of Falador.",
    title = "Burial armour",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = {
      ["Adamant bars (in metal bank) or"] = { quantity = 48 },
      ["Adamant full helm +2"] = { quantity = 1 },
      ["Adamant platebody +2"] = { quantity = 1 },
      ["Adamant platelegs +2"] = { quantity = 1 },
      ["Adamant gauntlets +2"] = { quantity = 1 },
      ["Adamant armoured boots +2"] = { quantity = 1 },
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
    text = "Make an adamant burial set in the Artisans' Workshop located in southeast Falador.<ul><li>You must make the 'burial set' and not make the burial items individually.</li><li>If the player has never made burial armour before they must first talk to Suak in the eastern part of the Artisans' Workshop.</li></ul>",
    actions = { Action.Direction:new(3041, 1125, 3340) },
    postconditions = { Condition.DistanceTo:new(3041, 1125, 3340, 8) },
  },
  { postconditions = { Condition.InventoryContains:new(adamantBurialSet) } },
  {
    text = "Talk to Suak.",
    actions = { Action.Direction:new(3059, 1125, 3344) },
    postconditions = { Condition.DistanceTo:new(3059, 1125, 3344, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(suak),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task IV (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1350432000,
  prereqQuests = { "Doric's Task III (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 40),
    Types.QuestReq.ironmanOnlySkill("Mining", 40, true),
  },
  neededItems = {
    ["48 Adamant bars (in metal bank) or"] = { quantity = 1, model = Models.items["adamant bar"] },
    ["Adamant full helm +2"] = { quantity = 1, model = adamantFullHelm },
    ["Adamant platebody +2"] = { quantity = 1, model = adamantPlatebody },
    ["Adamant platelegs +2"] = { quantity = 1, model = adamantPlatelegs },
    ["Adamant gauntlets +2"] = { quantity = 1, model = adamantGauntlets },
    ["Adamant armoured boots +2"] = { quantity = 1, model = adamantBoots },
  },
  recommendedItems = {},
  combatNPCs = {},
})
