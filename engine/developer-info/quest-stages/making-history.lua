local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local silverMerchant = Model.new(6126, {
  [4144] = Vertex.new(2, 725, -59, 107, 79, 55),
  [4148] = Vertex.new(7, 724, -51, 107, 79, 55),
  [4162] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [4167] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [5882] = Vertex.new(40, 711, -12, 47, 36, 14),
})
local blanin = Model.new(4299, {
  [2179] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [2187] = Vertex.new(2, 725, -59, 107, 79, 55),
  [2189] = Vertex.new(7, 724, -51, 107, 79, 55),
  [3762] = Vertex.new(-65, 670, 50, 68, 54, 43),
  [3793] = Vertex.new(65, 670, 50, 68, 54, 43),
})
local dron = Model.new(5325, {
  [2902] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [2910] = Vertex.new(2, 725, -59, 107, 79, 55),
  [2912] = Vertex.new(7, 724, -51, 107, 79, 55),
  [3876] = Vertex.new(-5, 766, -51, 54, 48, 41),
  [3887] = Vertex.new(5, 766, -51, 54, 48, 41),
})
local droalak = Model.new(2859, {
  [2398] = Vertex.new(76, 643, 19, 228, 242, 21, 0.000),
  [2399] = Vertex.new(81, 630, 19, 228, 242, 21, 0.000),
  [2401] = Vertex.new(-76, 643, 19, 228, 242, 21, 0.000),
  [2403] = Vertex.new(-81, 630, 19, 228, 242, 21, 0.000),
  [2404] = Vertex.new(0, 676, 53, 78, 203, 41, 0.000),
})
local melina = Model.new(3483, {
  [2302] = Vertex.new(76, 643, 19, 83, 217, 43, 0.000),
  [2303] = Vertex.new(81, 630, 19, 83, 217, 43, 0.000),
  [2305] = Vertex.new(-76, 643, 19, 83, 217, 43, 0.000),
  [2307] = Vertex.new(-81, 630, 19, 83, 217, 43, 0.000),
  [2308] = Vertex.new(0, 676, 53, 83, 217, 43, 0.000),
})
--#endregion
--#region Items
local sapphireAmulet = Model.new(432, {
  [1] = Vertex.new(10, 7, -61, 10, 14, 121),
  [20] = Vertex.new(-10, 7, -61, 10, 14, 121),
  [22] = Vertex.new(-10, 7, -61, 10, 14, 121),
  [56] = Vertex.new(10, 7, -61, 10, 14, 121),
  [300] = Vertex.new(0, -2, 72, 127, 127, 127),
})
--#endregion
--#region Quest Items
local enchantedKey = Model.new(759, {
  [317] = Vertex.new(-44, 0, 156, 126, 106, 51),
  [319] = Vertex.new(-44, 0, 156, 126, 106, 51),
  [322] = Vertex.new(-44, 0, 156, 126, 106, 51),
  [329] = Vertex.new(-20, 0, 180, 126, 106, 51),
  [331] = Vertex.new(-20, 0, 180, 126, 106, 51),
})
local chest = Model.new(342, {
  [47] = Vertex.new(180, -16, -20, 58, 43, 23),
  [74] = Vertex.new(-72, 64, -16, 101, 79, 52),
  [326] = Vertex.new(-108, 160, 76, 58, 43, 23),
  [339] = Vertex.new(-108, 160, 76, 58, 43, 23),
  [341] = Vertex.new(-108, 160, 76, 58, 43, 23),
})
local journal = Model.new(204, {
  [87] = Vertex.new(36, 36, -84, 70, 76, 39),
  [93] = Vertex.new(64, 36, 56, 70, 76, 39),
  [159] = Vertex.new(36, 36, -84, 57, 37, 5),
  [201] = Vertex.new(-32, 52, 32, 131, 120, 100),
  [203] = Vertex.new(32, 52, 52, 131, 120, 100),
})
local scroll = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 131, 120, 100),
  [5] = Vertex.new(-72, 0, -56, 131, 120, 100),
  [187] = Vertex.new(-84, 28, -84, 90, 64, 8),
  [190] = Vertex.new(-84, 28, -84, 90, 64, 8),
  [195] = Vertex.new(-84, 28, -84, 90, 64, 8),
})
local letter = Model.new(138, {
  [7] = Vertex.new(-100, 16, -56, 131, 120, 100),
  [10] = Vertex.new(-100, 16, -56, 131, 120, 100),
  [16] = Vertex.new(100, 24, -56, 131, 120, 100),
  [46] = Vertex.new(36, 36, 4, 140, 128, 107),
  [67] = Vertex.new(-100, 16, 56, 140, 128, 107),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Jorral at the Outpost south of the Eagles' Peak lodestone.",
    title = "Getting started",
    neededItems = {},
    recommendedItems = { ["Traveller's necklace"] = { quantity = 1 } },
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Eagles' Peak lodestone",
      url = "Eagles'_Peak_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2437, 1241, 3347) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["jorral"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["jorral"]),
      Action.ConversationHighlight:new("Tell me more."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["jorral"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("many occupants") },
  },
  {
    text = "Watch the cutscene.",
    postconditions = {
      Condition.ConversationText:new("all goes well"),
      Condition.ConversationText:new("do you think"),
    },
  },
  {
    text = "Continue talking to Jorral.",
    actions = { Action.ModelHighlight:new(Models.npcs["jorral"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Jorral.",
    actions = { Action.ModelHighlight:new(Models.npcs["jorral"]) },
    postconditions = { Condition.ConversationText:new("be able to help") },
  },
  {
    text = "Talk to the silver merchant in the East Ardougne marketplace.<ul><li>Do not steal from the stall otherwise the merchant will not talk with you.</li></ul>",
    title = "Erin",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = { ["Spade"] = { quantity = 1 } },
    recommendedItems = {
      ["Ring of duelling"] = { quantity = 1 },
      ["Dramen/lunar staff"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2659, 1285, 3316) },
    postconditions = { Condition.ModelVisible:new(silverMerchant) },
  },
  {
    actions = {
      Action.ModelHighlight:new(silverMerchant),
      Action.ConversationHighlight:new("Ask about the outpost."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(silverMerchant) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(enchantedKey) },
  },
  {
    text = "Dig on the marked tile north-east of Castle Wars.",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "2",
      hover = "Ring of duelling",
      url = "Ring_of_duelling_(1).png",
    },
    actions = {
      Action.Direction:new(2441, 1109, 3139, { tile = true }),
      Action.InventoryHighlight:new(Models.items["spade"]),
    },
    postconditions = { Condition.InventoryContains:new(chest) },
  },
  {
    text = "Use the enchanted key on the chest.",
    actions = {
      Action.InventoryHighlight:new(enchantedKey, true),
      Action.InventoryHighlight:new(chest),
    },
    postconditions = { Condition.InventoryContains:new(journal) },
  },
  {
    text = "Talk to Droalak south of the General Store in Port Phasmatys.",
    title = "Droalak",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ectophial teleport",
      url = "Ectophial.png",
    },
    neededItems = {
      ["Ecto-tokens"] = { quantity = 2 },
      ["Sapphire amulet"] = { quantity = 1 },
      ["Ghostspeak amulet"] = { quantity = 1 },
    },
    recommendedItems = { ["Ectophial"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3659, 261, 3466),
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
    },
    postconditions = { Condition.ModelVisible:new(droalak) },
  },
  {
    actions = {
      Action.ModelHighlight:new(droalak),
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
    },
    jumpconditions = { Condition.ModelNotVisible:new(droalak) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("No problem") },
  },
  {
    text = "Talk to Melina to the east.",
    actions = { Action.Direction:new(3672, 1125, 3479) },
    postconditions = { Condition.ModelVisible:new(melina) },
  },
  {
    actions = { Action.ModelHighlight:new(melina) },
    jumpconditions = { Condition.ModelNotVisible:new(melina) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Goodbye") },
  },
  {
    text = "Talk to Droalak again.",
    actions = { Action.Direction:new(3659, 261, 3466) },
    postconditions = { Condition.ModelVisible:new(droalak) },
  },
  {
    actions = { Action.ModelHighlight:new(droalak) },
    jumpconditions = { Condition.ModelNotVisible:new(droalak) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(scroll) },
  },
  {
    text = "Talk to Blanin, west of the cow pen in Rellekka.",
    title = "Dron",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fremennik Province lodestone",
      url = "Fremennik_Province_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2674, 1205, 3671) },
    postconditions = { Condition.ModelVisible:new(blanin) },
  },
  {
    actions = { Action.ModelHighlight:new(blanin) },
    jumpconditions = { Condition.ModelNotVisible:new(blanin) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("secretive guy") },
  },
  {
    text = "Talk to Dron, west of the helmet shop.",
    actions = { Action.Direction:new(2659, 573, 3698) },
    postconditions = { Condition.ModelVisible:new(dron) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("I'm after important answers."),
      Action.ConversationHighlight:new("Why, you're the famous warrior Dron!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(dron) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("weapon do I") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("An iron mace."),
    },
    postconditions = { Condition.ConversationText:new("rats") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Breakfast."),
    },
    postconditions = { Condition.ConversationText:new("devoured") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Lunch."),
    },
    postconditions = { Condition.ConversationText:new("tea") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Bunnies."),
    },
    postconditions = { Condition.ConversationText:new("spider blood") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Red."),
    },
    postconditions = { Condition.ConversationText:new("years") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("36."),
    },
    postconditions = { Condition.ConversationText:new("months") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("8."),
    },
    postconditions = { Condition.ConversationText:new("interesting") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Fifth and Fourth"),
    },
    postconditions = { Condition.ConversationText:new("house") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Northeast side of town"),
    },
    postconditions = { Condition.ConversationText:new("name") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Blanin."),
    },
    postconditions = { Condition.ConversationText:new("pet") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("Fluffy."),
    },
    postconditions = { Condition.ConversationText:new("plus") },
  },
  {
    actions = {
      Action.ModelHighlight:new(dron),
      Action.ConversationHighlight:new("12, but what does that have to do with anything?"),
    },
    postconditions = { Condition.ConversationText:new("BE GONE") },
  },
  {
    text = "Return to Jorral at the outpost, talk to him until he gives you a Letter.",
    title = "Jorral",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Eagles' Peak lodestone",
      url = "Eagles'_Peak_lodestone_icon.png",
    },
    neededItems = {
      ["Journal"] = { quantity = 1, model = journal },
      ["Scroll"] = { quantity = 1, model = scroll },
    },
    recommendedItems = { ["Traveller's necklace"] = { quantity = 1 } },
    actions = { Action.Direction:new(2437, 1241, 3347) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["jorral"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["jorral"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["jorral"]) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(letter) },
  },
  {
    text = "Go to 1st floor (2nd floor[US]) of Ardougne Castle in East Ardougne.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2571.5, 2525, 3307.5) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2571, 2917, 3306, 4),
      Condition.DistanceToWithHeight:new(2576, 2917, 3297, 5),
    },
  },
  {
    text = "Talk to King Lathas.",
    warning = "Remove Mourner outfit if equipped.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["king lathas"]),
      Action.ConversationHighlight:new("Jorral and the outpost"),
    },
    postconditions = { Condition.ConversationText:new("to hand you") },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king lathas"]) },
    postconditions = { Condition.ConversationInactive:new() }, --not tested
  },
  {
    text = "Return to Jorral.",
    title = "Finishing up",
    actions = { Action.Direction:new(2437, 1241, 3347) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["jorral"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["jorral"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["jorral"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Making History",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.short,
  releaseDate = 1132617600,
  prereqQuests = {
    "The Restless Ghost",
    "Priest in Peril",
  },
  questReqs = {},
  neededItems = {
    ["Spade"] = { quantity = 1, model = Models.items["spade"] },
    ["Ecto-tokens"] = { quantity = 2 },
    ["Sapphire amulet"] = { quantity = 1, model = sapphireAmulet },
    ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] },
  },
  recommendedItems = {
    ["Traveller's necklace"] = { quantity = 1 },
    ["Ectophial"] = { quantity = 1 },
    ["Ring of duelling"] = { quantity = 1 },
    ["Dramen/lunar staff"] = { quantity = 1 },
  },
  combatNPCs = {},
})
