local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local registrar = Model.new(6111, {
  [4699] = Vertex.new(0, 747, -3, 69, 180, 36),
  [4700] = Vertex.new(0, 744, -3, 69, 180, 36),
  [4701] = Vertex.new(0, 744, -4, 69, 180, 36),
  [4702] = Vertex.new(20, 777, -28, 192, 196, 200),
  [4714] = Vertex.new(-20, 777, -28, 192, 196, 200),
})
local guard = Model.new(4545, {
  [2320] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [2325] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [2328] = Vertex.new(2, 725, -59, 107, 79, 55),
  [2330] = Vertex.new(7, 724, -51, 107, 79, 55),
  [3512] = Vertex.new(-30, 721, -31, 47, 36, 14),
})
--#endregion
--#region Objects
local wallClock = Model.new(3114, {
  [1921] = Vertex.new(-192, 628, 140, 27, 25, 24),
  [1922] = Vertex.new(-192, 620, 132, 27, 25, 24),
  [1925] = Vertex.new(-192, 688, 132, 27, 25, 24),
  [1929] = Vertex.new(-192, 688, 132, 27, 25, 24),
  [1931] = Vertex.new(-192, 688, 132, 27, 25, 24),
})
local openTrapdoor = Model.new(1128, {
  [117] = Vertex.new(-112, -728, -68, 55, 49, 34),
  [131] = Vertex.new(112, -728, -68, 55, 49, 34),
  [171] = Vertex.new(-112, -608, -88, 55, 49, 34),
  [185] = Vertex.new(112, -608, -88, 55, 49, 34),
  [239] = Vertex.new(112, -500, -104, 55, 49, 34),
})
--#endregion
--#region Items
local willowBlackjack = Model.new(102, {
  [27] = Vertex.new(-52, 4, -84, 121, 95, 10),
  [29] = Vertex.new(-60, 20, -68, 121, 95, 10),
  [33] = Vertex.new(-60, 20, -68, 121, 95, 10),
  [66] = Vertex.new(-52, 4, -84, 121, 95, 10),
  [84] = Vertex.new(-60, 20, -68, 121, 95, 10),
})
local iritPotionUnf = Model.multi({
  Model.new(90, {
    [3] = Vertex.new(-12, 52, 20, 90, 136, 12),
    [6] = Vertex.new(-20, 52, -12, 90, 136, 12),
    [8] = Vertex.new(-12, 52, 20, 90, 136, 12),
    [21] = Vertex.new(20, 52, -16, 90, 136, 12),
    [23] = Vertex.new(20, 52, -16, 90, 136, 12),
  }),
  Model.new(288, {
    [242] = Vertex.new(4, 96, 12, 134, 136, 147, 0.4980),
    [243] = Vertex.new(-4, 96, 12, 134, 136, 147, 0.4980),
    [246] = Vertex.new(-4, 96, 12, 134, 136, 147, 0.4980),
    [264] = Vertex.new(-12, 96, -4, 134, 136, 147, 0.4980),
    [269] = Vertex.new(-12, 96, -4, 134, 136, 147, 0.4980),
  }),
})
local choppedOnion = Model.new(597, {
  [131] = Vertex.new(-72, 40, 8, 137, 126, 126),
  [251] = Vertex.new(-72, 40, 8, 154, 142, 141),
  [528] = Vertex.new(-84, 96, -48, 111, 93, 45),
  [532] = Vertex.new(-84, 96, -48, 111, 93, 45),
  [538] = Vertex.new(-84, 96, -48, 111, 93, 45),
})
--#endregion
--#region Quest Items
local vialOfStench = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 143, 128, 58),
    [2] = Vertex.new(4, 112, -12, 143, 128, 58),
    [3] = Vertex.new(4, 96, -12, 143, 128, 58),
    [4] = Vertex.new(-4, 96, -12, 143, 128, 58),
    [7] = Vertex.new(4, 96, 12, 143, 128, 58),
    [8] = Vertex.new(-4, 112, 12, 143, 128, 58),
    [9] = Vertex.new(-4, 96, 12, 143, 128, 58),
    [13] = Vertex.new(-12, 96, -4, 143, 128, 58),
    [17] = Vertex.new(-12, 112, -4, 143, 128, 58),
    [36] = Vertex.new(12, 96, -4, 143, 128, 58),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 173, 174, 184, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 173, 174, 184, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 173, 174, 184, 0.4980),
    [5] = Vertex.new(4, 96, 20, 173, 174, 184, 0.4980),
    [7] = Vertex.new(20, 84, 4, 173, 174, 184, 0.4980),
    [11] = Vertex.new(20, 96, 4, 173, 174, 184, 0.4980),
    [13] = Vertex.new(20, 84, -4, 173, 174, 184, 0.4980),
    [17] = Vertex.new(20, 96, -4, 173, 174, 184, 0.4980),
    [19] = Vertex.new(4, 84, -20, 173, 174, 184, 0.4980),
    [23] = Vertex.new(4, 96, -20, 173, 174, 184, 0.4980),
    [49] = Vertex.new(-4, 68, 16, 106, 150, 77, 0.8745),
    [50] = Vertex.new(-4, 84, 12, 106, 150, 77, 0.8745),
    [51] = Vertex.new(-12, 84, 4, 106, 150, 77, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 106, 150, 77, 0.8745),
    [55] = Vertex.new(-12, 12, 40, 106, 150, 77, 0.8745),
    [60] = Vertex.new(-12, 84, -4, 106, 150, 77, 0.8745),
    [63] = Vertex.new(-40, 12, 12, 106, 150, 77, 0.8745),
    [64] = Vertex.new(-4, 0, 36, 106, 150, 77, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 106, 150, 77, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 106, 150, 77, 0.8745),
  }),
})
local vialOfStenchA = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 143, 128, 58),
    [2] = Vertex.new(4, 112, -12, 143, 128, 58),
    [3] = Vertex.new(4, 96, -12, 143, 128, 58),
    [4] = Vertex.new(-4, 96, -12, 143, 128, 58),
    [7] = Vertex.new(4, 96, 12, 143, 128, 58),
    [8] = Vertex.new(-4, 112, 12, 143, 128, 58),
    [9] = Vertex.new(-4, 96, 12, 143, 128, 58),
    [13] = Vertex.new(-12, 96, -4, 143, 128, 58),
    [17] = Vertex.new(-12, 112, -4, 143, 128, 58),
    [36] = Vertex.new(12, 96, -4, 143, 128, 58),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 173, 174, 184, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 173, 174, 184, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 173, 174, 184, 0.4980),
    [5] = Vertex.new(4, 96, 20, 173, 174, 184, 0.4980),
    [7] = Vertex.new(20, 84, 4, 173, 174, 184, 0.4980),
    [11] = Vertex.new(20, 96, 4, 173, 174, 184, 0.4980),
    [13] = Vertex.new(20, 84, -4, 173, 174, 184, 0.4980),
    [17] = Vertex.new(20, 96, -4, 173, 174, 184, 0.4980),
    [19] = Vertex.new(4, 84, -20, 173, 174, 184, 0.4980),
    [23] = Vertex.new(4, 96, -20, 173, 174, 184, 0.4980),
    [49] = Vertex.new(-4, 68, 16, 64, 84, 34, 0.8745),
    [50] = Vertex.new(-4, 84, 12, 64, 84, 34, 0.8745),
    [51] = Vertex.new(-12, 84, 4, 64, 84, 34, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 64, 84, 34, 0.8745),
    [55] = Vertex.new(-12, 12, 40, 64, 84, 34, 0.8745),
    [60] = Vertex.new(-12, 84, -4, 64, 84, 34, 0.8745),
    [63] = Vertex.new(-40, 12, 12, 64, 84, 34, 0.8745),
    [64] = Vertex.new(-4, 0, 36, 64, 84, 34, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 64, 84, 34, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 64, 84, 34, 0.8745),
  }),
})
local cuckooClockKey = Model.new(306, {
  [81] = Vertex.new(-24, 0, -16, 94, 84, 38),
  [87] = Vertex.new(-24, 0, -16, 94, 84, 38),
  [161] = Vertex.new(28, 0, -16, 94, 84, 38),
  [168] = Vertex.new(28, 0, -16, 94, 84, 38),
  [173] = Vertex.new(28, 0, -16, 94, 84, 38),
})
local leverKey = Model.new(693, {
  [290] = Vertex.new(20, 0, 180, 94, 84, 38),
  [294] = Vertex.new(20, 0, 180, 94, 84, 38),
  [297] = Vertex.new(20, 0, 180, 94, 84, 38),
  [441] = Vertex.new(0, 4, 176, 94, 84, 38),
  [444] = Vertex.new(0, 4, 176, 94, 84, 38),
})
local bonds = Model.new(426, {
  [44] = Vertex.new(-252, 24, 124, 47, 37, 4),
  [183] = Vertex.new(180, 60, 180, 47, 37, 4),
  [204] = Vertex.new(196, 60, 168, 47, 37, 4),
  [213] = Vertex.new(108, 72, -232, 50, 46, 46),
  [221] = Vertex.new(-160, 72, 180, 50, 46, 46),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Darren Lightfinger just inside of the Thieves' Guild in Lumbridge.",
    title = "Overview",
    actions = { Action.Direction:new(3223, 1125, 3268.3) },
    postconditions = { Condition.DistanceTo:new(4634, 5, 5768, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["darren lightfinger2"]),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", actions = {}, postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Darren Lightfinger.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["darren lightfinger2"]),
    },
    postconditions = { Condition.ConversationText:new("Talk to him about getting it set up for you.") },
  },
  {
    text = "Talk to Chief Thief Robin.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["chief thief robin"]),
      Action.ConversationHighlight:new("About this caper..."),
    },
    postconditions = { Condition.ConversationText:new("I'll be back with the vial, then.") },
  },
  {
    text = "Use a bowl on an onion to make chopped onion.",
    actions = {
      Action.InventoryHighlight:new(Models.items["onion"]),
      Action.InventoryHighlight:new(Models.items["bowl"]),
    },
    postconditions = { Condition.InventoryContains:new(choppedOnion) },
  },
  {
    text = "Use irit potion (unfinished) with chopped onion to get a vial of stench.<ul><li>Be careful not to eat the chopped onion.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(iritPotionUnf),
      Action.InventoryHighlight:new(choppedOnion),
    },
    postconditions = { Condition.InventoryContains:new(vialOfStench) },
  },
  {
    text = "Talk to Chief Thief Robin to get a vial of stench (a).<ul><li>If it does not work, talk to Darren Lightfinger about a disguise, and then talk to Robin again.</li></ul>",
    actions = {
      Action.ModelHighlight:new(Models.npcs["chief thief robin"]),
      Action.ConversationHighlight:new("About this caper..."),
    },
    postconditions = { Condition.InventoryContains:new(vialOfStenchA) },
  },
  {
    text = "In a building south-east of Ardougne market.<ul><li>If you fail any of the below steps, come back to this step.</li></ul>",
    title = "Guild registry",
    neededItems = {
      ["Lockpick"] = { quantity = 1 },
      ["Blackjack"] = { quantity = 1 },
      ["Vial of stench (a)"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2677.5, 965, 3289) },
    postconditions = { Condition.DistanceTo:new(0, 0, 0, 8, true) },
  },
  {
    text = "Pickpocket the Registrar for a cuckoo clock key.",
    actions = { Action.ModelHighlight:new(registrar, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(cuckooClockKey) },
  },
  {
    text = "Wind the clock on the wall in the north room.",
    actions = {
      Action.ModelHighlight:new(wallClock, { instanced = true }),
    },
    postconditions = { Condition.ConversationText:new("A few seconds to") },
  },
  {
    text = "Go in the south room, wait for the Registrar to get to the clock room.",
    actions = { Action.Direction:new(1, 0, -1, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(1, 0, -1, 2, true) },
  },
  {
    text = "Equip your blackjack, <i>Lure</i>, <i>Knock-out</i>, then <i>Steal-from</i> the guard.",
    actions = {
      Action.InventoryHighlight:new(willowBlackjack),
      Action.ModelHighlight:new(guard, { instanced = true }),
    },
    postconditions = { Condition.InventoryContains:new(leverKey) },
  },
  {
    text = "Pull the vault lever behind the desk.",
    actions = { Action.Direction:new(4, 0, 0, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(openTrapdoor),
      Condition.ChatText:new("You unlock the lever and pull it down."),
    },
  },
  {
    text = "Go down the trapdoor that opens, the following steps are no longer time sensitive.",
    actions = { Action.ModelHighlight:new(openTrapdoor, { instanced = true }) },
    postconditions = { Condition.DistanceTo:new(545, 1, -23, 4, true) },
  },
  {
    text = "Right-click <i>pick-lock</i> on the vault door.",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(545, 1, -26.5),
    },
    postconditions = { Condition.DistanceTo:new(545, 1, -27, 1, true) },
  },
  {
    text = "Right-click <i>check</i> the chest for traps, then open it.",
    actions = { Action.Direction:new(546, 1, -27) },
    postconditions = { Condition.InventoryContains:new(bonds) },
  },
  {
    text = "Inspect the boxes under the square hole in the wall to exit.",
    actions = {
      Action.Direction:new(547, 1, -24),
      Action.ConversationHighlight:new("Leave the vault."),
    },
    postconditions = {
      Condition.DistanceTo:new(2680, 1013, 3295, 8),
    },
  },
  {
    text = "Bank (recommended) or destroy your vial of stench (a).",
    title = "Finishing up",
    neededItems = { ["Bonds (A Guild of Our Own)"] = { quantity = 1 } },
    actions = { Action.InventoryHighlight:new(vialOfStenchA) },
    postconditions = { Condition.InventoryDoesNotContain:new(vialOfStenchA) },
  },
  {
    text = "Re-enter the building and talk to the Registrar.",
    actions = {
      Action.Direction:new(2677.5, 965, 3289),
    },
    postconditions = { Condition.DistanceTo:new(0, 0, 0, 8, true) },
  },
  {
    actions = { Action.ModelHighlight:new(registrar, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Then I think I'll be on my way") },
  },
  {
    text = "Return to Darren Lightfinger.",
    actions = { Action.Direction:new(3223, 1125, 3268.3) },
    postconditions = { Condition.DistanceTo:new(4634, 5, 5768, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["darren lightfinger2"]),
      Action.ConversationHighlight:new("I'd like to talk abou tthe caper I'm doing for you."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "A Guild of Our Own (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1272412800,
  prereqQuests = { "Lost Her Marbles (miniquest)", "The Feud" },
  questReqs = {
    Types.QuestReq.skill("Agility", 40),
    Types.QuestReq.skill("Herblore", 46),
    Types.QuestReq.skill("Thieving", 62),
  },
  neededItems = {
    ["Any blackjack"] = { quantity = 1, model = willowBlackjack, duringQuest = true }, --maybe make multimodel for all blackjacks?
    ["Lockpick"] = { quantity = 1, model = Models.items["lockpick"], duringQuest = true },
    ["Irit potion (unfinished)"] = { quantity = 1, model = iritPotionUnf },
    ["Onion"] = { quantity = 1, model = Models.items["onion"], duringQuest = true },
    ["Bowl"] = { quantity = 1, model = Models.items["bowl"], duringQuest = true },
  },
})
