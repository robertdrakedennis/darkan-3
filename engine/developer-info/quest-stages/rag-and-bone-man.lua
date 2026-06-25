local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local fortunato = Model.new(4011, {
  [1] = Vertex.new(-72, 160, -4, 98, 85, 50),
})
local goblin = Model.any({
  Model.new(4560, {
    [2536] = Vertex.new(-25, 594, -60, 49, 49, 45),
    [2537] = Vertex.new(-27, 588, -61, 49, 49, 45),
    [2542] = Vertex.new(25, 594, -60, 49, 49, 45),
    [2543] = Vertex.new(27, 587, -55, 49, 49, 45),
    [2544] = Vertex.new(27, 588, -61, 49, 49, 45),
  }),
  Model.new(4608, {
    [2091] = Vertex.new(-16, 529, -108, 139, 139, 128),
    [2098] = Vertex.new(13, 522, -110, 139, 139, 128),
    [2099] = Vertex.new(14, 533, -106, 139, 139, 128),
    [2102] = Vertex.new(4, 533, -111, 139, 139, 128),
    [2103] = Vertex.new(10, 521, -112, 139, 139, 128),
  }),
})
local ram = Model.new(7512, {
  [1] = Vertex.new(-11, 483, -141, 128, 128, 128),
})
local giantRat = Model.new(32256, {
  [1789] = Vertex.new(0, 169, 893, 127, 127, 127),
  [1793] = Vertex.new(0, 160, 890, 127, 127, 127),
  [1797] = Vertex.new(0, 169, 893, 127, 127, 127),
  [2041] = Vertex.new(0, 169, 893, 127, 127, 127),
  [22415] = Vertex.new(0, 145, -381, 127, 127, 127),
})
local bigFrog = Model.new(1359, {
  [482] = Vertex.new(468, 84, 112, 7, 79, 49),
  [488] = Vertex.new(448, 106, 112, 7, 79, 49),
  [578] = Vertex.new(-442, 118, 112, 7, 79, 43),
  [640] = Vertex.new(-442, 118, 112, 7, 79, 43),
  [643] = Vertex.new(-442, 118, 112, 7, 79, 43),
})
local monkey = Model.new(1371, {
  [1] = Vertex.new(12, 0, 20, 60, 115, 98),
})
local bear = Model.new(2802, {
  [2] = Vertex.new(-27, 412, -603, 128, 128, 127),
  [4] = Vertex.new(-28, 413, -589, 128, 128, 127),
  [5] = Vertex.new(-27, 412, -603, 128, 128, 127),
  [27] = Vertex.new(27, 412, -603, 128, 128, 127),
  [28] = Vertex.new(28, 413, -589, 128, 128, 127),
})
local unicorn = Model.new(49122, {
  [1] = Vertex.new(-71, 118, -150, 157, 157, 157),
})
local giantBat = Model.new(2394, {
  [1] = Vertex.new(42, 836, 56, 37, 23, 30),
})
--#endregion
--#region Objects
local stile = Model.new(1200, {
  [897] = Vertex.new(7911, 1142, 3744, 127, 127, 127),
  [905] = Vertex.new(7911, 1141, 3662, 127, 127, 127),
  [908] = Vertex.new(7911, 1141, 3662, 127, 127, 127),
  [915] = Vertex.new(7981, 1138, 3703, 127, 127, 127),
  [995] = Vertex.new(7956, 1133, 3390, 127, 127, 127),
})
local lumbridgeSwampTree = Model.new(5943, {
  [138] = Vertex.new(356, 690, -12, 77, 80, 74),
  [2844] = Vertex.new(785, 2264, -181, 102, 102, 78),
  [4994] = Vertex.new(835, 735, -539, 45, 50, 38),
  [5895] = Vertex.new(596, 735, -908, 45, 50, 38),
  [5942] = Vertex.new(596, 735, -908, 45, 50, 38),
})
local emptyPotBoiler = Model.new(2340, {
  [1751] = Vertex.new(180, 352, 124, 127, 121, 98),
  [1764] = Vertex.new(180, 352, 124, 127, 121, 98),
  [1857] = Vertex.new(180, 352, 124, 140, 134, 108),
  [1893] = Vertex.new(-204, 316, -76, 140, 134, 108),
  [2268] = Vertex.new(108, 248, -40, 39, 36, 36),
})
local emptyPotBoilerWithLogs = Model.new(2730, {
  [1] = Vertex.new(204, 0, 24, 133, 127, 102),
})
local potBoilerWithLogs = Model.new(3036, {
  [1] = Vertex.new(204, 0, 24, 133, 127, 102),
})
local litPotBoiler = Model.new(3510, {
  [217] = Vertex.new(-176, 628, 0, 155, 112, 14),
  [2759] = Vertex.new(180, 352, 124, 127, 121, 98),
  [2772] = Vertex.new(180, 352, 124, 127, 121, 98),
  [2865] = Vertex.new(180, 352, 124, 140, 134, 108),
  [2901] = Vertex.new(-204, 316, -76, 140, 134, 108),
})
local readyPotBoiler = Model.new(2928, {
  [1895] = Vertex.new(180, 352, 124, 127, 121, 98),
  [1908] = Vertex.new(180, 352, 124, 127, 121, 98),
  [2001] = Vertex.new(180, 352, 124, 140, 134, 108),
  [2037] = Vertex.new(-204, 316, -76, 140, 134, 108),
  [2412] = Vertex.new(108, 248, -40, 39, 36, 36),
})
--#endregion
--#region Quest Items
local vinegar = Model.new(366, {
  [1] = Vertex.new(-24, 120, 0, 54, 42, 41),
})
local potOfVinegar = Model.new(306, {
  [1] = Vertex.new(16, 160, -48, 54, 42, 41),
})
local boneInVinegar = Model.new(588, {
  [1] = Vertex.new(20, 160, 48, 54, 42, 41),
})
local goblinSkull = Model.new(234, {
  [28] = Vertex.new(96, 116, 56, 113, 113, 104),
  [34] = Vertex.new(96, 116, 56, 113, 113, 104),
  [44] = Vertex.new(96, 116, 56, 113, 113, 104),
  [51] = Vertex.new(96, 116, 56, 113, 113, 104),
  [56] = Vertex.new(96, 116, -56, 113, 113, 104),
})
local bearRibs = Model.new(600, {
  [1] = Vertex.new(40, 232, -184, 96, 95, 87),
})
local bigFrogLeg = Model.new(597, {
  [1] = Vertex.new(-12, 8, -92, 98, 98, 90),
})
local ramSkull = Model.new(477, {
  [1] = Vertex.new(16, 12, -72, 113, 113, 104),
})
local unicornBone = Model.new(252, {
  [1] = Vertex.new(24, 36, -44, 113, 113, 104),
})
local monkeyPaw = Model.new(522, {
  [1] = Vertex.new(8, 20, -16, 113, 113, 104),
})
local giantRatBone = Model.new(90, {
  [1] = Vertex.new(-60, 24, -96, 113, 113, 104),
})
local giantBatWing = Model.new(462, {
  [1] = Vertex.new(80, 0, 112, 113, 113, 104),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the Odd Old Man in Silvarea.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    neededItems = { ["Empty pot"] = { quantity = 8 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3366, 2445, 3501) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["odd old man"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["odd old man"]),
      Action.ConversationHighlight:new("Anything I can do to help?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["odd old man"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Odd Old Man.",
    actions = { Action.ModelHighlight:new(Models.npcs["odd old man"]) },
    postconditions = { Condition.ConversationText:new("Bye!") },
  },
  {
    text = "Talk to Fortunato in the Draynor Village market.",
    title = "Vinegar",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Draynor Village lodestone",
      url = "Draynor_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3085, 1317, 3249) },
    postconditions = { Condition.ModelVisible:new(fortunato) },
  },
  {
    actions = {
      Action.ModelHighlight:new(fortunato),
      Action.ConversationHighlight:new("Talk about Rag and Bone Man."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(fortunato) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("I have some in specially") },
  },
  { text = "Buy 8 jugs of vinegar.", postconditions = { Condition.InventoryContains:new(vinegar, 8) } },
  {
    text = "Use a jugs of vinegar on an empty pot.",
    actions = {
      Action.InventoryHighlight:new(vinegar),
      Action.InventoryHighlight:new(Models.items["pot"]),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(vinegar) },
  },
  {
    text = "Kill a goblin north of the Lumbridge lodestone.<ul><li>You can drop the empty jugs.</li></ul>",
    title = "Goblin skull",
    warning = "Might not be visible after killing the mob.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Lumbridge lodestone",
      url = "Lumbridge_lodestone_icon.png",
    },
    neededItems = {
      ["Combat gear"] = { quantity = 1 },
      ["Pot of vinegar"] = { quantity = 1, model = potOfVinegar },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(3212, 917, 3279) },
    postconditions = { Condition.DistanceTo:new(3212, 917, 3279, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(goblin, { highlightPriority = "closest" }) },
    postconditions = {
      Condition.ModelVisible:new(goblinSkull),
      Condition.InventoryContains:new(goblinSkull),
    },
  },
  {
    text = "Pick up the goblin skull.",
    actions = { Action.ModelHighlight:new(goblinSkull) },
    postconditions = { Condition.InventoryContains:new(goblinSkull) },
  },
  {
    text = "Use the goblin skull on a pot of vinegar.",
    actions = {
      Action.InventoryHighlight:new(goblinSkull),
      Action.InventoryHighlight:new(potOfVinegar),
    },
    postconditions = { Condition.InventoryContains:new(boneInVinegar) },
  },
  {
    text = "Climb over the stile.",
    title = "Ram skull",
    neededItems = { ["Pot of vinegar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(stile) },
    postconditions = { Condition.DistanceTo:new(3199, 909, 3288, 1) },
  },
  {
    text = "Kill a ram.",
    actions = { Action.ModelHighlight:new(ram, { highlightPriority = "closest" }) },
    postconditions = { Condition.ModelVisible:new(ramSkull) },
  },
  {
    text = "Pick up the ram skull.",
    actions = { Action.ModelHighlight:new(ramSkull) },
    postconditions = { Condition.InventoryContains:new(ramSkull) },
  },
  {
    text = "Use the ram skull on a pot of vinegar.",
    actions = {
      Action.InventoryHighlight:new(ramSkull),
      Action.InventoryHighlight:new(potOfVinegar),
    },
    postconditions = { Condition.InventoryContains:new(boneInVinegar, 2) },
  },
  {
    text = "Kill a giant rat to the south.",
    title = "Giant rat bone",
    neededItems = { ["Pot of vinegar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3188, 1301, 3210) },
    postconditions = {
      Condition.ModelVisible:new(giantRat),
      Condition.ModelVisible:new(giantRatBone),
      Condition.InventoryContains:new(giantRatBone),
    },
  },
  {
    actions = { Action.ModelHighlight:new(giantRat, { highlightPriority = "closest" }) },
    jumpconditions = { Condition.ModelNotVisible:new(giantRat) },
    jumpOffset = -1,
    postconditions = {
      Condition.ModelVisible:new(giantRatBone),
      Condition.InventoryContains:new(giantRatBone),
    },
  },
  {
    text = "Pick up the giant rat bone.",
    actions = { Action.ModelHighlight:new(giantRatBone) },
    postconditions = { Condition.InventoryContains:new(giantRatBone) },
  },
  {
    text = "Use the giant rat bone on a pot of vinegar.",
    actions = {
      Action.InventoryHighlight:new(giantRatBone),
      Action.InventoryHighlight:new(potOfVinegar),
    },
    postconditions = { Condition.InventoryContains:new(boneInVinegar, 3) },
  },
  {
    text = "Climb down the dark hole under the tree in the Lumbridge swamp.<ul><li>If this is your first visit, use a rope on the tree.</li></ul>",
    title = "Giant frog leg",
    neededItems = {
      ["Pot of vinegar"] = { quantity = 1 },
      ["Any light source"] = { quantity = 1 },
      ["Rope (if never entered Lumbridge caves)"] = { quantity = 1 },
    },
    recommendedItems = {
      ["Spiny helmet"] = { quantity = 1 },
      ["Bullseye lantern"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3164, 949, 3167.5) },
    postconditions = {
      Condition.ModelVisible:new(lumbridgeSwampTree),
      Condition.DistanceTo:new(3168, 1029, 9572, 4),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(lumbridgeSwampTree),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    jumpconditions = { Condition.ModelNotVisible:new(lumbridgeSwampTree) },
    jumpOffset = -1,
    postconditions = { Condition.DistanceTo:new(3168, 1029, 9572, 4) },
  },
  {
    text = "Kill a big frog.",
    actions = { Action.ModelHighlight:new(bigFrog, { highlightPriority = "closest" }) },
    postconditions = {
      Condition.ModelVisible:new(bigFrogLeg),
      Condition.InventoryContains:new(bigFrogLeg),
    },
  },
  {
    text = "Pick up the big frog leg.",
    actions = { Action.ModelHighlight:new(bigFrogLeg) },
    postconditions = { Condition.InventoryContains:new(bigFrogLeg) },
  },
  {
    text = "Use the big frog leg on a pot of vinegar.",
    actions = {
      Action.InventoryHighlight:new(bigFrogLeg),
      Action.InventoryHighlight:new(potOfVinegar),
    },
    postconditions = { Condition.InventoryContains:new(boneInVinegar, 4) },
  },
  {
    text = "Kill a monkey near the Karamja lodestone.",
    title = "Monkey paw",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Karamja lodestone",
      url = "Karamja_lodestone_icon.png",
    },
    neededItems = { ["Pot of vinegar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2744, 789, 3162) },
    postconditions = {
      Condition.ModelVisible:new(monkey),
      Condition.ModelVisible:new(monkeyPaw),
      Condition.InventoryContains:new(monkeyPaw),
    },
  },
  {
    actions = { Action.ModelHighlight:new(monkey) },
    jumpconditions = { Condition.ModelNotVisible:new(monkey) },
    jumpOffset = -1,
    postconditions = {
      Condition.ModelVisible:new(monkeyPaw),
      Condition.InventoryContains:new(monkeyPaw),
    },
  },
  {
    text = "Pick up the monkey paw.",
    actions = { Action.ModelHighlight:new(monkeyPaw) },
    postconditions = { Condition.InventoryContains:new(monkeyPaw) },
  },
  {
    text = "Use the monkey paw on a pot of vinegar.",
    actions = { Action.InventoryHighlight:new(monkeyPaw), Action.InventoryHighlight:new(potOfVinegar) },
    postconditions = { Condition.InventoryContains:new(boneInVinegar, 5) },
  },
  {
    text = "Kill a bear east of the Ardougne lodestone.",
    title = "Bear ribs",
    warning = "Might not be visible after killing the mob.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = { ["Pot of vinegar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2694, 973, 3346) },
    postconditions = {
      Condition.ModelVisible:new(bear),
      Condition.ModelVisible:new(bearRibs),
      Condition.InventoryContains:new(bearRibs),
    },
  },
  {
    actions = { Action.ModelHighlight:new(bear, { highlightPriority = "closest" }) },
    jumpconditions = { Condition.ModelNotVisible:new(bear) },
    jumpOffset = -1,
    postconditions = {
      Condition.ModelVisible:new(bearRibs),
      Condition.InventoryContains:new(bearRibs),
    },
  },
  {
    text = "Pick up the bear ribs.",
    actions = { Action.ModelHighlight:new(bearRibs) },
    postconditions = { Condition.InventoryContains:new(bearRibs) },
  },
  {
    text = "Use the bear ribs on a pot of vinegar.",
    actions = {
      Action.InventoryHighlight:new(bearRibs),
      Action.InventoryHighlight:new(potOfVinegar),
    },
    postconditions = { Condition.InventoryContains:new(boneInVinegar, 6) },
  },
  {
    text = "Kill a unicorn to the north.",
    title = "Unicorn bone",
    warning = "Might not be visible after killing the mob.",
    neededItems = { ["Pot of vinegar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2700, 1141, 3434) },
    postconditions = {
      Condition.ModelVisible:new(unicorn),
      Condition.ModelVisible:new(unicornBone),
      Condition.InventoryContains:new(unicornBone),
    },
  },
  {
    actions = { Action.ModelHighlight:new(unicorn, { highlightPriority = "closest" }) },
    jumpconditions = { Condition.ModelNotVisible:new(unicorn) },
    jumpOffset = -1,
    postconditions = {
      Condition.ModelVisible:new(unicornBone),
      Condition.InventoryContains:new(unicornBone),
    },
  },
  {
    text = "Pick up the unicorn bone.",
    actions = { Action.ModelHighlight:new(unicornBone) },
    postconditions = { Condition.InventoryContains:new(unicornBone) },
  },
  {
    text = "Use the unicorn bone on a pot of vinegar.",
    actions = {
      Action.InventoryHighlight:new(unicornBone),
      Action.InventoryHighlight:new(potOfVinegar),
    },
    postconditions = { Condition.InventoryContains:new(boneInVinegar, 7) },
  },
  {
    text = "Kill a giant bat to the south-east.",
    title = "Giant bat wing",
    neededItems = { ["Pot of vinegar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2752, 933, 3402) },
    postconditions = {
      Condition.ModelVisible:new(giantBat),
      Condition.ModelVisible:new(giantBatWing),
      Condition.InventoryContains:new(giantBatWing),
    },
  },
  {
    actions = { Action.ModelHighlight:new(giantBat, { highlightPriority = "closest" }) },
    jumpconditions = { Condition.ModelNotVisible:new(giantBat) },
    jumpOffset = -1,
    postconditions = {
      Condition.ModelVisible:new(giantBatWing),
      Condition.InventoryContains:new(giantBatWing),
    },
  },
  {
    text = "Pick up the giant bat wing.",
    actions = { Action.ModelHighlight:new(giantBatWing) },
    postconditions = { Condition.InventoryContains:new(giantBatWing) },
  },
  {
    text = "Use the giant bat wing on a pot of vinegar.",
    actions = {
      Action.InventoryHighlight:new(giantBatWing),
      Action.InventoryHighlight:new(potOfVinegar),
    },
    postconditions = { Condition.InventoryContains:new(boneInVinegar, 8) },
  },
  {
    text = "Go back to the Odd Old Man's hut.",
    title = "Cleaning the bones",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    neededItems = {
      ["Bone in vinegar"] = { quantity = 8, model = boneInVinegar },
      ["Any type of logs"] = { quantity = 8, model = Models.items["any logs"] },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(3361, 2501, 3505) },
    postconditions = { Condition.DistanceTo:new(3361, 2501, 3505, 10) },
  },
  {
    text = "Add logs to the pot boiler.",
    actions = { Action.ModelHighlight:new(emptyPotBoiler) },
    postconditions = { Condition.ModelVisible:new(emptyPotBoilerWithLogs) },
  },
  {
    text = "Add a pot of bones to the pot boiler.",
    actions = { Action.ModelHighlight:new(emptyPotBoilerWithLogs) },
    postconditions = { Condition.ModelVisible:new(potBoilerWithLogs) },
  },
  {
    text = "Light the fire.",
    actions = { Action.ModelHighlight:new(potBoilerWithLogs) },
    postconditions = { Condition.ModelVisible:new(litPotBoiler) },
  },
  {
    text = "Wait for the bone to boil.<ul><li>Hopping worlds will skip the boiling.</li></ul>",
    postconditions = { Condition.ModelVisible:new(readyPotBoiler) },
  },
  {
    text = "Remove the bone from the pot boiler.",
    actions = { Action.ModelHighlight:new(readyPotBoiler) },
    postconditions = { Condition.ModelVisible:new(emptyPotBoiler) },
  },
  {
    jumpconditions = { Condition.InventoryContains:new(boneInVinegar) },
    jumpOffset = -5,
    postconditions = { Condition.InventoryDoesNotContain:new(boneInVinegar) },
  },
  {
    text = "Talk to the Odd Old Man.",
    title = "Finishing up",
    actions = { Action.ModelHighlight:new(Models.npcs["odd old man"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Rag and Bone Man",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1144627200,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Empty pot"] = { quantity = 8, model = Models.items["pot"] },
    ["Any type of logs"] = { quantity = 8, model = Models.items["any logs"], duringQuest = true },
    ["Any light source"] = { quantity = 1, model = Models.items["light source"] },
    ["Rope (if never entered Lumbridge caves)"] = { quantity = 1, model = Models.items["rope"] },
    ["Combat gear"] = { quantity = 1 },
  },
  recommendedItems = {
    ["Spiny helmet"] = { quantity = 1 },
    ["Bullseye lantern"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Goblin"] = { level = "11", quantity = 1 },
    ["Ram"] = { level = "2", quantity = 1 },
    ["Giant rat"] = { level = "4", quantity = 1 },
    ["Big frog"] = { level = "18", quantity = 1 },
    ["Monkey"] = { level = "8", quantity = 1 },
    ["Bear"] = { level = "32", quantity = 1 },
    ["Unicorn"] = { level = "15", quantity = 1 },
    ["Giant bat"] = { level = "14", quantity = 1 },
  },
})
