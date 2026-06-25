local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local larrissa = Model.any({
  Model.new(4833, {
    [2619] = Vertex.new(24, 738, -38, 31, 30, 28),
    [2643] = Vertex.new(-24, 738, -38, 31, 30, 28),
    [2665] = Vertex.new(-2, 716, -57, 108, 79, 56),
    [2671] = Vertex.new(2, 716, -57, 108, 79, 56),
    [2675] = Vertex.new(6, 716, -52, 108, 79, 56),
  }),
  Model.new(4899, {
    [2265] = Vertex.new(24, 738, -38, 31, 30, 28),
    [2289] = Vertex.new(-24, 738, -38, 31, 30, 28),
    [2311] = Vertex.new(-2, 716, -57, 108, 79, 56),
    [2317] = Vertex.new(2, 716, -57, 108, 79, 56),
    [2321] = Vertex.new(6, 716, -52, 108, 79, 56),
  }),
})
local gunnjorn = Model.new(5523, {
  [2624] = Vertex.new(-32, 752, -46, 114, 19, 10),
  [2630] = Vertex.new(-32, 744, -46, 114, 19, 10),
  [2634] = Vertex.new(12, 748, -54, 114, 19, 10),
  [2636] = Vertex.new(32, 748, -46, 114, 19, 10),
  [2642] = Vertex.new(32, 740, -46, 114, 19, 10),
})
local jossik = Model.any({
  Model.new(4488, {
    [2326] = Vertex.new(-2, 725, -59, 108, 79, 56),
    [2331] = Vertex.new(-7, 724, -51, 108, 79, 56),
    [2334] = Vertex.new(2, 725, -59, 108, 79, 56),
    [2336] = Vertex.new(7, 724, -51, 108, 79, 56),
    [2593] = Vertex.new(0, 735, -7, 28, 140, 128),
  }),
})
local dagannoth = Model.new(4266, {
  [3944] = Vertex.new(-161, 0, 87, 65, 65, 59),
  [3946] = Vertex.new(-161, 0, 87, 65, 65, 59),
  [3987] = Vertex.new(69, 0, 89, 65, 65, 59),
  [4033] = Vertex.new(-69, 0, 89, 65, 65, 59),
  [4089] = Vertex.new(161, 0, 87, 65, 65, 59),
})
local dagannothMother = Model.new(4266, {
  [3944] = Vertex.new(-305, 0, 164, 128, 128, 117),
  [3946] = Vertex.new(-305, 0, 164, 128, 128, 117),
  [3987] = Vertex.new(130, 0, 169, 128, 128, 117),
  [4033] = Vertex.new(-130, 0, 169, 128, 128, 117),
  [4089] = Vertex.new(305, 0, 164, 128, 128, 117),
})
--#endregion
--#region Objects
local brokenBridge = Model.new(6, {
  [2] = Vertex.new(3072, 384, 4096, 96, 89, 88),
  [3] = Vertex.new(3072, 384, 4608, 96, 89, 88),
  [5] = Vertex.new(2048, 384, 4096, 96, 89, 88),
})
local bookcase = Model.new(4209, {
  [662] = Vertex.new(2959, 7462, 2487, 127, 127, 127),
  [826] = Vertex.new(2963, 7429, 2462, 127, 127, 127),
  [1571] = Vertex.new(2959, 7510, 2317, 127, 127, 127),
  [1690] = Vertex.new(2963, 7429, 2514, 127, 127, 127),
  [3323] = Vertex.new(2758, 7379, 2440, 127, 127, 127),
})
local lightingMechanism = Model.new(2031, {
  [886] = Vertex.new(1024, 512, -32, 84, 84, 92),
  [890] = Vertex.new(1024, 512, -32, 84, 84, 92),
  [1049] = Vertex.new(1024, 512, -32, 84, 84, 92),
  [1892] = Vertex.new(8, 1016, -320, 84, 84, 92),
  [1951] = Vertex.new(-24, 1140, -128, 88, 88, 96),
})
--#endregion
--#region Items
local bronzeArrow = Model.new(105, {
  [12] = Vertex.new(-24, 0, 168, 58, 43, 24),
  [44] = Vertex.new(-12, 0, -148, 90, 15, 8),
  [66] = Vertex.new(8, 0, -160, 90, 15, 8),
  [74] = Vertex.new(8, 0, -160, 90, 15, 8),
  [103] = Vertex.new(-24, 0, 168, 58, 43, 24),
})
local bronzeSword = Model.new(402, {
  [75] = Vertex.new(-12, 0, -95, 95, 70, 39),
  [206] = Vertex.new(-15, 0, -84, 73, 57, 38),
  [225] = Vertex.new(15, 0, -84, 73, 57, 38),
  [320] = Vertex.new(-3, 14, -101, 170, 133, 88),
  [327] = Vertex.new(3, 14, -101, 170, 133, 88),
})
--#endregion
--#region Quest Items
local journal = Model.new(138, {
  [133] = Vertex.new(-32, 52, 52, 145, 144, 111),
  [134] = Vertex.new(32, 52, 32, 145, 144, 111),
  [135] = Vertex.new(-32, 52, 32, 145, 144, 111),
  [136] = Vertex.new(-32, 52, 52, 145, 144, 111),
  [137] = Vertex.new(32, 52, 52, 145, 144, 111),
})
local diary = Model.new(282, {
  [138] = Vertex.new(64, 56, 84, 98, 67, 40),
  [180] = Vertex.new(52, 56, -72, 98, 67, 40),
  [215] = Vertex.new(64, 56, 84, 98, 67, 40),
  [279] = Vertex.new(-32, 48, 32, 153, 152, 79),
  [281] = Vertex.new(32, 48, 52, 153, 152, 79),
})
local manual = Model.new(204, {
  [87] = Vertex.new(36, 36, -84, 123, 123, 94),
  [93] = Vertex.new(64, 36, 56, 123, 123, 94),
  [159] = Vertex.new(36, 36, -84, 111, 49, 45),
  [201] = Vertex.new(-32, 52, 32, 145, 144, 111),
  [203] = Vertex.new(32, 52, 52, 145, 144, 111),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Head to the lighthouse north of the Barbarian Outpost and talk to Larrissa.",
    title = "Getting started",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2508, 3717, 3632) },
    postconditions = { Condition.ModelVisible:new(larrissa) },
  },
  {
    actions = {
      Action.ModelHighlight:new(larrissa),
      Action.ConversationHighlight:new("With what?"),
      Action.ConversationHighlight:new("But how can I help?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(larrissa) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },

  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Larrissa.",
    actions = {
      Action.ModelHighlight:new(larrissa),
      Action.ConversationHighlight:new("I'll see what I can do"),
    },
    postconditions = { Condition.ConversationText:new("what I can do") },
  },
  {
    text = "Go south to the Barbarian Outpost, pick up two planks north of the Agility course.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Enter through the gate, then go through the Obstacle pipe.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Talk to Gunnjorn in the Barbarian Agility Course.<ul><li>Planks spawn north of him.</li></ul>",
    title = "Preparations",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "2",
      hover = "Games necklace teleport",
      url = "Games_necklace_(1).png",
    },
    neededItems = {
      ["Steel nails"] = { quantity = 60 },
      ["Planks"] = { quantity = 2 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2541, 645, 3548) },
    postconditions = { Condition.ModelVisible:new(gunnjorn) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gunnjorn),
      Action.ConversationHighlight:new("Talk about Horror from the Deep."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(gunnjorn) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Here you go") },
  },
  {
    text = "Fix the broken bridge next to the strange altar.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fremennik Province lodestone",
      url = "Fremennik_Province_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2596.5, 413, 3608) },
    postconditions = { Condition.ModelVisible:new(brokenBridge) },
  },
  {
    actions = { Action.ModelHighlight:new(brokenBridge) },
    jumpconditions = { Condition.ModelNotVisible:new(brokenBridge) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("half a makeshift walkway") },
  },
  {
    text = "Cross the broken bridge.",
    actions = { Action.ModelHighlight:new(brokenBridge) },
    postconditions = { Condition.DistanceTo:new(2595, 317, 3608, 0) },
  },
  {
    text = "Fix the broken bridge again.",
    actions = { Action.ModelHighlight:new(brokenBridge) },
    postconditions = { Condition.ConversationText:new("makeshift walkway") },
  },
  {
    text = "Talk to Larrissa.",
    actions = { Action.Direction:new(2508, 3717, 3632) },
    postconditions = {
      Condition.ModelVisible:new(larrissa),
      Condition.InInstance:new(),
    },
  },
  {
    actions = { Action.ModelHighlight:new(larrissa) },
    jumpconditions = { Condition.ModelNotVisible:new(larrissa) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("to my beloved") },
  },
  {
    text = "Enter the lighthouse.",
    actions = { Action.ModelHighlight:new(Models.objects["fremennik lighthouse door"]) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Climb up the staircase.",
    title = "The lighthouse",
    neededItems = {
      ["Molten glass"] = { quantity = 1 },
      ["Swamp tar"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.objects["fremennik lighthouse staircase 0"]) },
    postconditions = { Condition.DistanceToWithHeight:new(-3, 2336, 3, 4, true) },
  },
  {
    text = "Search the bookcase.",
    actions = { Action.ModelHighlight:new(bookcase) },
    postconditions = { Condition.InventoryContains:new(journal) },
  },
  {
    text = "Read the manual.",
    actions = { Action.InventoryHighlight:new(manual) },
    postconditions = { Condition.ItemClicked:new(manual) },
  },
  {
    text = "Read the dairy.",
    actions = { Action.InventoryHighlight:new(diary) },
    postconditions = { Condition.ItemClicked:new(diary) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.ModelHighlight:new(Models.objects["fremennik lighthouse staircase 0"]) },
    postconditions = { Condition.DistanceToWithHeight:new(-4, 5376, 5, 4, true) },
  },
  {
    text = "Repair the lighting mechanism.",
    actions = { Action.ModelHighlight:new(lightingMechanism) },
    postconditions = { Condition.ChatText:new("torch with your tinderbox") },
  },
  {
    text = "<i>Climb-bottom</i> the staircase.",
    actions = { Action.Direction:new(-3, 5384, 5, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-2, 0, 4, 4, true) },
  },
  {
    text = "Talk to Larrissa.",
    actions = { Action.ModelHighlight:new(larrissa) },
    postconditions = { Condition.ConversationText:new("darling Jossik") },
  },
  {
    text = "Climb the iron ladder.",
    title = "Jossik",
    neededItems = {
      ["Fire rune"] = { quantity = 1 },
      ["Air rune"] = { quantity = 1 },
      ["Water rune"] = { quantity = 1 },
      ["Earth rune"] = { quantity = 1 },
      ["Bronze sword"] = { quantity = 1 },
      ["Bronze arrow"] = { quantity = 1 },
      ["A melee-slash weapon"] = { quantity = 1 },
      ["A ranged-thrown weapon"] = { quantity = 1 },
      ["A magic weapon"] = { quantity = 1 },
      ["Runes for all 4 elemental spells"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(-0.8, 0, 8.0125, { instance = true, tile = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Use a fire rune on the strange door.",
    warning = "You cannot retrieve every item you use on the strange door.",
    actions = {
      Action.Direction:new(2514.5, 2585, 4626.5),
      Action.InventoryHighlight:new(Models.items["fire rune"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("fire rune into") },
  },
  {
    text = "Use an air rune on the strange door.",
    actions = {
      Action.Direction:new(2514.5, 2585, 4626.5),
      Action.InventoryHighlight:new(Models.items["air rune"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("air rune into") },
  },
  {
    text = "Use a water rune on the strange door.",
    actions = {
      Action.Direction:new(2514.5, 2585, 4626.5),
      Action.InventoryHighlight:new(Models.items["water rune"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("water rune into") },
  },
  {
    text = "Use an earth rune on the strange door.",
    actions = {
      Action.Direction:new(2514.5, 2585, 4626.5),
      Action.InventoryHighlight:new(Models.items["earth rune"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("earth rune into") },
  },
  {
    text = "Use a bronze sword on the strange door.",
    actions = {
      Action.Direction:new(2514.5, 2585, 4626.5),
      Action.InventoryHighlight:new(bronzeSword),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("a sword into") },
  },
  {
    text = "Use a bronze arrow on the strange door.",
    actions = {
      Action.Direction:new(2514.5, 2585, 4626.5),
      Action.InventoryHighlight:new(bronzeArrow),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("an arrow into") },
  },
  {
    text = "Open the eastern strange wall.",
    actions = { Action.Direction:new(2516, 2585, 4626.5) },
    postconditions = { Condition.DistanceTo:new(2516, 2021, 4628, 1) },
  },
  {
    text = "Go down the ladder.",
    actions = { Action.Direction:new(2515, 2037, 4630) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Jossik.",
    title = "The battle",
    neededItems = {
      ["A melee-slash weapon"] = { quantity = 1 },
      ["A ranged-thrown weapon"] = { quantity = 1 },
      ["A magic weapon"] = { quantity = 1 },
      ["Runes for all 4 elemental spells"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(jossik) },
    postconditions = { Condition.ConversationText:new("coming again") },
  },
  {
    text = "Kill the dagannoth.",
    postconditions = { Condition.ModelVisible:new(dagannoth) },
  },
  { postconditions = { Condition.ModelNotVisible:new(dagannoth) } },
  {
    text = "Talk to Jossik.",
    actions = { Action.ModelHighlight:new(jossik) },
    postconditions = { Condition.ConversationText:new("babies") },
  },
  {
    text = 'Kill the Dagannoth mother.<tbody><tr><th style="padding:6px;">Color</th><th style="padding:6px;">Weakness</th></tr><tr><td style="padding:6px;"><b>White</b></td><td style="padding:6px;">Air spells</td></tr><tr><td style="color:mediumblue; padding:6px;"><b>Blue</b></td><td style="padding:6px;">Water spells</td></tr><tr><td style="color:saddlebrown; padding:6px;"><b>Brown</b></td><td style="padding:6px;">Earth spells</td></tr><tr><td style="color:red; padding:6px;"><b>Red</b></td><td style="padding:6px;">Fire spells</td></tr><tr><td style="color:darkorange; padding:6px;"><b>Orange</b></td><td style="padding:6px;">Melee attacks</td></tr><tr><td style="color:green; padding:6px;"><b>Green</b></td><td style="padding:6px;">Ranged attacks</td></tr></tbody>',
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Horror from the Deep",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1100649600,
  prereqQuests = { "Bar Crawl (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Agility", 35),
    Types.QuestReq.misc("Planks spawn next to the Barbarian Agility Course."),
  },
  neededItems = {
    ["Fire rune"] = { quantity = 1, model = Models.items["fire rune"] },
    ["Earth rune"] = { quantity = 1, model = Models.items["earth rune"] },
    ["Air rune"] = { quantity = 1, model = Models.items["air rune"] },
    ["Water rune"] = { quantity = 1, model = Models.items["water rune"] },
    ["Bronze sword"] = { quantity = 1, model = bronzeSword },
    ["Bronze arrow"] = { quantity = 1, model = bronzeArrow },
    ["Steel nails"] = { quantity = 60, model = Models.items["steel nails"] },
    ["Planks"] = { quantity = 2, model = Models.items["plank"] },
    ["Molten glass"] = { quantity = 1, model = Models.items["molten glass"] },
    ["Swamp tar"] = { quantity = 1, model = Models.items["swamp tar"] },
    ["A melee-slash weapon"] = { quantity = 1 },
    ["A ranged-thrown weapon"] = { quantity = 1 },
    ["A magic weapon"] = { quantity = 1 },
    ["Runes for all 4 elemental spells"] = { quantity = 1 },
  },
  recommendedItems = {
    ["Games necklace"] = { quantity = 1 },
    ["Player-owned house"] = { quantity = 1 },
    ["Lighthouse Teleport"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Dagannoth"] = { level = "100", quantity = 1 },
    ["Dagannoth mother"] = { level = "84", quantity = 1 },
  },
})
