local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local lucian = Model.new(3267, {
  [2602] = Vertex.new(0, 735, -7, 29, 142, 129),
  [2603] = Vertex.new(0, 732, -7, 29, 142, 129),
  [2604] = Vertex.new(0, 732, -8, 29, 142, 129),
  [3262] = Vertex.new(132, 377, -18, 134, 99, 70),
  [3265] = Vertex.new(-132, 377, -18, 134, 99, 70),
})
local fireWarrior = Model.new(5847, {
  [2830] = Vertex.new(-2, 725, -59, 87, 31, 27),
  [2838] = Vertex.new(2, 725, -59, 87, 31, 27),
  [2840] = Vertex.new(7, 724, -51, 87, 31, 27),
  [5496] = Vertex.new(-65, 670, 50, 92, 15, 8),
  [5527] = Vertex.new(65, 670, 50, 92, 15, 8),
})
local winelda = Model.new(5367, {
  [2355] = Vertex.new(24, 738, -38, 32, 31, 29),
  [2379] = Vertex.new(-24, 738, -38, 32, 31, 29),
  [2401] = Vertex.new(-2, 716, -57, 107, 98, 98),
  [2407] = Vertex.new(2, 716, -57, 107, 98, 98),
  [2411] = Vertex.new(6, 716, -52, 107, 98, 98),
})
local guardianOfArmadyl = Model.any({
  Model.new(5358, {
    [1496] = Vertex.new(-208, 588, 24, 91, 73, 58),
    [1650] = Vertex.new(204, 588, -20, 105, 84, 67),
    [1694] = Vertex.new(-204, 588, -20, 105, 84, 67),
    [1700] = Vertex.new(-188, 584, 40, 105, 84, 67),
    [1703] = Vertex.new(192, 584, 44, 105, 84, 67),
  }),
  Model.new(4017, {
    [2207] = Vertex.new(164, 568, -12, 105, 84, 67),
    [2225] = Vertex.new(-116, 576, 48, 91, 73, 58),
    [2261] = Vertex.new(164, 568, 20, 91, 73, 58),
    [2543] = Vertex.new(-152, 564, -20, 105, 84, 67),
    [2559] = Vertex.new(-128, 564, 48, 105, 84, 67),
  }),
})
--#endregion
--#region Objects
local web = Model.new(876, {
  [179] = Vertex.new(3584, 1016, 3084, 126, 116, 115, 0.6078),
  [230] = Vertex.new(3584, 1784, 3076, 126, 116, 115, 0.6078),
  [461] = Vertex.new(3584, 1044, 3076, 126, 116, 115, 0.6078),
  [521] = Vertex.new(3584, 1808, 3084, 126, 116, 115, 0.6078),
  [851] = Vertex.new(3072, 1812, 3084, 126, 116, 115, 0.6078),
})
local leverObj = Model.new(408, {
  [123] = Vertex.new(-256, 932, -108, 81, 77, 75),
  [147] = Vertex.new(-256, 852, 84, 74, 69, 68),
  [269] = Vertex.new(-184, 880, -68, 91, 87, 84),
  [290] = Vertex.new(-192, 888, -56, 91, 87, 84),
  [347] = Vertex.new(-256, 932, -108, 74, 69, 68),
})
local pulledLever = Model.new(408, {
  [47] = Vertex.new(-256, 852, 84, 74, 69, 68),
  [123] = Vertex.new(-256, 932, -108, 81, 77, 75),
  [289] = Vertex.new(-184, 880, -68, 91, 87, 84),
  [290] = Vertex.new(-192, 888, -56, 91, 87, 84),
  [347] = Vertex.new(-256, 932, -108, 74, 69, 68),
})
local tableWithStaff = Model.new(2964, {
  [429] = Vertex.new(168, 344, 336, 67, 60, 43),
  [1822] = Vertex.new(-68, 200, 448, 158, 153, 145),
  [1862] = Vertex.new(-68, 156, 448, 158, 153, 145),
  [2121] = Vertex.new(-68, 204, -428, 158, 153, 145),
  [2132] = Vertex.new(-68, 64, -428, 158, 153, 145),
})
--#endregion
--#region Items
local bootsOfLightness = Model.new(240, {
  [1] = Vertex.new(64, 112, -32, 74, 74, 96),
  [35] = Vertex.new(-48, 100, 24, 74, 74, 96),
  [147] = Vertex.new(-48, 100, 24, 74, 74, 96),
  [148] = Vertex.new(-48, 100, 24, 74, 74, 96),
  [151] = Vertex.new(-48, 100, 24, 74, 74, 96),
})
--#endregion
--#region Quest Items
local pendantOfLucien = Model.new(237, {
  [1] = Vertex.new(-8, 4, -40, 84, 56, 17),
  [2] = Vertex.new(-8, 16, -40, 84, 56, 17),
  [3] = Vertex.new(-24, 16, -12, 84, 56, 17),
  [6] = Vertex.new(-24, 4, -12, 84, 56, 17),
  [9] = Vertex.new(-28, 16, 20, 84, 56, 17),
  [12] = Vertex.new(-28, 4, 20, 84, 56, 17),
  [15] = Vertex.new(-28, 16, 48, 84, 56, 17),
  [18] = Vertex.new(-28, 4, 48, 84, 56, 17),
  [21] = Vertex.new(-4, 16, 56, 84, 56, 17),
  [24] = Vertex.new(-4, 4, 56, 84, 56, 17),
  [27] = Vertex.new(28, 16, 48, 84, 56, 17),
  [97] = Vertex.new(8, 4, -40, 58, 53, 53),
  [121] = Vertex.new(8, 16, -40, 62, 37, 13),
  [122] = Vertex.new(32, 16, -12, 62, 37, 13),
  [123] = Vertex.new(12, 16, -52, 62, 37, 13),
  [181] = Vertex.new(-4, 20, -68, 155, 153, 14),
  [182] = Vertex.new(-12, 20, -68, 155, 153, 14),
  [202] = Vertex.new(-8, 24, -64, 36, 116, 38),
  [203] = Vertex.new(0, 24, -48, 36, 116, 38),
  [204] = Vertex.new(8, 24, -64, 36, 116, 38),
})
local lever = Model.new(60, {
  [1] = Vertex.new(116, 44, -32, 155, 26, 14),
  [33] = Vertex.new(-108, 44, 28, 74, 68, 68),
  [47] = Vertex.new(-108, 44, 28, 74, 68, 68),
  [53] = Vertex.new(-108, 0, -32, 74, 68, 68),
  [57] = Vertex.new(-108, 44, 28, 74, 68, 68),
})
local iceArrows = Model.new(537, {
  [68] = Vertex.new(-88, 0, -200, 85, 132, 162),
  [140] = Vertex.new(20, 0, -224, 85, 132, 162),
  [212] = Vertex.new(48, 0, -168, 85, 132, 162),
  [284] = Vertex.new(-12, 0, -148, 85, 132, 162),
  [356] = Vertex.new(-48, 0, -188, 85, 132, 162),
})
local shinyKey = Model.new(444, {
  [2] = Vertex.new(-12, 16, -84, 155, 123, 14),
  [86] = Vertex.new(52, 16, 80, 155, 123, 14),
  [99] = Vertex.new(68, 16, 68, 155, 123, 14),
  [104] = Vertex.new(68, 16, 68, 155, 123, 14),
  [397] = Vertex.new(68, 16, 68, 155, 123, 14),
})
local pendantOfArmadyl = Model.new(237, {
  [1] = Vertex.new(-8, 4, -40, 84, 56, 17),
  [2] = Vertex.new(-8, 16, -40, 84, 56, 17),
  [3] = Vertex.new(-24, 16, -12, 84, 56, 17),
  [6] = Vertex.new(-24, 4, -12, 84, 56, 17),
  [9] = Vertex.new(-28, 16, 20, 84, 56, 17),
  [12] = Vertex.new(-28, 4, 20, 84, 56, 17),
  [15] = Vertex.new(-28, 16, 48, 84, 56, 17),
  [18] = Vertex.new(-28, 4, 48, 84, 56, 17),
  [21] = Vertex.new(-4, 16, 56, 84, 56, 17),
  [24] = Vertex.new(-4, 4, 56, 84, 56, 17),
  [27] = Vertex.new(28, 16, 48, 84, 56, 17),
  [97] = Vertex.new(8, 4, -40, 58, 53, 53),
  [121] = Vertex.new(8, 16, -40, 62, 37, 13),
  [122] = Vertex.new(32, 16, -12, 62, 37, 13),
  [123] = Vertex.new(12, 16, -52, 62, 37, 13),
  [181] = Vertex.new(-4, 20, -68, 155, 153, 14),
  [182] = Vertex.new(-12, 20, -68, 155, 153, 14),
  [202] = Vertex.new(-8, 24, -64, 137, 127, 126),
  [203] = Vertex.new(0, 24, -48, 137, 127, 126),
  [204] = Vertex.new(8, 24, -64, 137, 127, 126),
})
local staffOfArmadyl = Models.items["beer"] --need model data
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Lucien north of the Ardougne castle.",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2574, 1445, 3322) },
    postconditions = { Condition.ModelVisible:new(lucian) },
  },
  {
    actions = {
      Action.ModelHighlight:new(lucian),
      Action.ConversationHighlight:new("I'm a mighty hero!"),
      Action.ConversationHighlight:new("That sounds like a laugh!"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(lucian) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Lucien.",
    actions = { Action.ModelHighlight:new(lucian) },
    postconditions = { Condition.ConversationText:new("small holding up there") },
  },
  {
    text = "Enter the Temple of Ikov north-east of the Ardougne lodestone.<ul><li>Make sure you weigh less than 4.5kg.</li></ul>",
    title = "Inside the temple",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {
      ["Light sources"] = { quantity = 1 },
      ["Bow"] = { quantity = 1 },
      ["Limpwurt roots"] = { quantity = 20 },
    },
    recommendedItems = {
      ["Any Ava's device"] = { quantity = 1 },
      ["Ranged armour"] = { quantity = 1 },
      ["Weight-reducing clothing"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2677.5, 1669, 3405.5, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2677, 1861, 9806, 4) },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(2650.5, 805, 9804.5) },
    jumpconditions = { Condition.InventoryContains:new(bootsOfLightness) },
    jumpOffset = 4,
    postconditions = { Condition.DistanceToWithHeight:new(2641, 965, 9764, 4) },
  },
  {
    text = "Cut through the web.",
    actions = { Action.ModelHighlight:new(web) },
    postconditions = { Condition.ChatText:new("cuts through") },
  },
  {
    text = "Pick up the boots of lightness.",
    actions = { Action.ModelHighlight:new(bootsOfLightness) },
    postconditions = { Condition.InventoryContains:new(bootsOfLightness) },
  },
  {
    text = "Climb back up the stairs.",
    actions = { Action.Direction:new(2639.5, 1565, 9763.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2649, 805, 9805, 4) },
  },
  {
    text = "Equip the Pendant of Lucien.",
    actions = { Action.InventoryHighlight:new(pendantOfLucien) },
    postconditions = { Condition.InventoryDoesNotContain:new(pendantOfLucien) },
  },
  {
    text = "Enter the north gate.",
    actions = { Action.Direction:new(2661.5, 1629, 9814.5) },
    postconditions = { Condition.DistanceTo:new(2661, 1029, 9816, 1) },
  },
  {
    text = "Walk across the bridge.<ul><li>Weight needs to be 0kg or less.</li></ul>",
    actions = { Action.Direction:new(2648.5, 1605, 9828.5) },
    postconditions = { Condition.DistanceTo:new(2646, 1013, 9829, 1) },
  },
  {
    text = "Pick up the lever.",
    actions = { Action.Direction:new(2637, 1685, 9819, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(lever) },
  },
  {
    text = "Walk back across the bridge.",
    actions = { Action.Direction:new(2648.5, 1605, 9828.5) },
    postconditions = { Condition.DistanceTo:new(2652, 973, 9828, 1) },
  },
  {
    text = "Use the lever on the lever bracket.",
    actions = {
      Action.Direction:new(2671.05, 2481, 9804.1),
      Action.InventoryHighlight:new(lever),
    },
    postconditions = { Condition.ModelVisible:new(leverObj) },
  },
  {
    text = "Pull the lever.",
    actions = { Action.ModelHighlight:new(leverObj) },
    postconditions = { Condition.ConversationText:new("hidden machinery") },
  },
  {
    text = "Open the gate to the south.",
    title = "Ice arrows",
    neededItems = {
      ["Bow"] = { quantity = 1 },
      ["Limpwurt root"] = { quantity = 20 },
      ["Pendant of Lucien"] = { quantity = 1 },
    },
    recommendedItems = {
      ["Any Ava's device"] = { quantity = 1 },
      ["Ranged armour"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2661.5, 1633, 9802.5) },
    postconditions = { Condition.DistanceTo:new(2662, 869, 9801, 1) },
  },
  {
    text = "Loot the chests in the tunnel for 10 ice arrows.<ul><li>The arrows will only be in 1 of the 6 chests. The location changes after successfully looting them. They can spawn in the same chest again.</li></ul>",
    actions = {
      Action.Direction:new(2710, 1125, 9850, { tile = true }),
      Action.Direction:new(2719, 1149, 9838, { tile = true }),
      Action.Direction:new(2729, 1061, 9850, { tile = true }),
      Action.Direction:new(2747, 1173, 9848, { tile = true }),
      Action.Direction:new(2738, 1389, 9835, { tile = true }),
      Action.Direction:new(2745, 1125, 9821, { tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(iceArrows, 10) },
  },
  {
    text = "Open the gate.",
    actions = { Action.Direction:new(2661.5, 1633, 9802.5) },
    postconditions = { Condition.DistanceTo:new(2662, 853, 9804, 1) },
  },
  {
    text = "Open the gate to the north.",
    actions = { Action.Direction:new(2661.5, 1629, 9814.5) },
    postconditions = { Condition.DistanceTo:new(2661, 1029, 9816, 1) },
  },
  {
    text = "'Search for traps' on the lever, then pull it.",
    actions = { Action.Direction:new(2665, 1705, 9855.35) },
    postconditions = { Condition.ConversationText:new("disable") },
  },
  {
    actions = { Action.Direction:new(2665, 1705, 9855.35) },
    postconditions = { Condition.ModelVisible:new(pulledLever) },
  },
  {
    text = "Open the wooden door to the west.",
    actions = { Action.Direction:new(2648, 1541, 9857.5) },
    postconditions = { Condition.DistanceTo:new(2648, 909, 9859, 1) },
  },
  {
    text = "Open the next door.",
    actions = { Action.Direction:new(2646, 1613, 9870.5) },
    postconditions = { Condition.ModelVisible:new(fireWarrior) },
  },
  {
    text = "Kill the Fire Warrior of Lesarkus.",
    actions = {
      Action.ModelHighlight:new(fireWarrior),
      Action.InventoryHighlight:new(iceArrows),
    },
    postconditions = { Condition.ModelNotVisible:new(fireWarrior) },
  },
  {
    text = "Open the door again.",
    actions = { Action.Direction:new(2646, 1613, 9870.5) },
    postconditions = { Condition.DistanceTo:new(2646, 805, 9872, 1) },
  },
  {
    text = "Talk to Winelda.",
    actions = {
      Action.ModelHighlight:new(winelda),
      Action.ConversationHighlight:new("Yes I do!"),
    },
    postconditions = { Condition.DistanceTo:new(2664, 1277, 9876, 3) },
  },
  {
    text = "Pick up the shiny key to the south-west.",
    actions = { Action.Direction:new(2628, 421, 9859, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(shinyKey) },
  },
  {
    text = "Push through the wall to the north.",
    actions = { Action.Direction:new(2643, 1541, 9892.5) },
    postconditions = { Condition.DistanceTo:new(2643, 741, 9894, 1) },
  },
  {
    text = "Remove your pendant.",
    title = "Siding with the guardians (recommended)",
    neededItems = {},
    recommendedItems = { ["Dramen staff"] = { quantity = 1 } },
    postconditions = { Condition.InventoryContains:new(pendantOfLucien) },
  },
  {
    text = "Talk to a guardian.",
    actions = {
      Action.ModelHighlight:new(guardianOfArmadyl, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("I seek the Staff of Armadyl."),
      Action.ConversationHighlight:new("Lucien will give me a grand reward for it!"),
      Action.ConversationHighlight:new("You're right, it's time for my yearly bath."),
      Action.ConversationHighlight:new("Ok! I'll help!"),
    },
    postconditions = { Condition.ConversationText:new("hands you a pendant") },
  },
  {
    text = "Equip the Armadyl pendant.",
    actions = { Action.InventoryHighlight:new(pendantOfArmadyl) },
    postconditions = { Condition.InventoryDoesNotContain:new(pendantOfArmadyl) },
  },
  {
    text = "Kill Lucien in his home west of the Grand Exchange.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "DKR",
      hover = "Edgeville",
    },
    actions = { Action.Direction:new(3128, 1657, 3486) },
    postconditions = { Condition.ModelVisible:new(lucian) },
  },
  {
    actions = { Action.ModelHighlight:new(lucian) },
    jumpconditions = { Condition.ModelNotVisible:new(lucian) },
    jumpOffset = -1,
    postconditions = { Condition.DistanceTo:new(3128, 1657, 3486, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(lucian) },
    postconditions = { Condition.ModelNotVisible:new(lucian) }, --has a bit of a delay
  },
  {
    jumpconditions = { Condition.Always:new() },
    jumpOffset = 3,
  },
  {
    text = "Try to take the staff from table. Kill any guardians if they stop you until the staff is acquired.",
    title = "Siding with Lucien",
    warning = "No tracking for this step.",
    neededItems = {},
    recommendedItems = { ["Dramen staff"] = { quantity = 1 } },
    actions = { Action.ModelHighlight:new(tableWithStaff) },
    postconditions = { Condition.InventoryContains:new(staffOfArmadyl) },
  },
  {
    text = "Talk to Lucien in his home west of the Grand Exchange.",
    tpHint = {
      type = Enums.tpHintType.fairy,
      text = "DKR",
      hover = "Edgeville",
    },
    actions = { Action.Direction:new(3128, 1657, 3486) },
    postconditions = { Condition.ModelVisible:new(lucian) },
  },
  {
    actions = {
      Action.ModelHighlight:new(lucian),
      Action.ConversationHighlight:new("Yes! Here it is."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(lucian) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Temple of Ikov",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1024272000,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Ranged", 40),
    Types.QuestReq.skill("Thieving", 42),
  },
  neededItems = {
    ["Light source"] = { quantity = 1, model = Models.items["light source"] },
    ["Bow"] = { quantity = 1 },
    ["Limpwurt roots"] = { quantity = 20 },
  },
  recommendedItems = {
    ["Weight-reducing clothing"] = { quantity = 1 },
    ["Prayer potions"] = { quantity = 1 },
    ["Dramen staff"] = { quantity = 1 },
    ["Any Ava's device"] = { quantity = 1 },
    ["Ranged armour"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Fire Warrior of Lesarkus"] = { level = "77", quantity = 1 },
    ["Guardians of Armadyl"] = { level = "42", optional = true, quantity = 1 },
    ["Skeletons"] = { level = "58", optional = true, quantity = 1 },
  },
})
