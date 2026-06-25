local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local fillimanTarlock = Model.new(3723, {
  [1597] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [1602] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [1605] = Vertex.new(2, 725, -59, 109, 80, 57),
  [1607] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3183] = Vertex.new(29, 727, -29, 117, 124, 152),
})
local invokedGhast = Model.new(5367, {
  [4862] = Vertex.new(-41, 1033, -24, 83, 81, 63),
  [4904] = Vertex.new(45, 1039, -14, 83, 81, 63),
  [5207] = Vertex.new(-20, 1137, -35, 62, 60, 48, 0.9608),
  [5295] = Vertex.new(21, 1070, -50, 95, 105, 67, 0.9608),
  [5312] = Vertex.new(-21, 1070, -50, 95, 105, 67, 0.9608),
})
--#endregion
--#region Objects
local bridgeShortcut = Model.new(3936, {
  [1243] = Vertex.new(972, 169, 1880, 45, 37, 29),
  [1256] = Vertex.new(941, 143, 1921, 45, 37, 29),
  [1257] = Vertex.new(974, 143, 1921, 45, 37, 29),
  [1262] = Vertex.new(974, 143, 1921, 45, 37, 29),
  [1266] = Vertex.new(974, 143, 1921, 45, 37, 29),
})
local fungusLog1 = Model.new(2352, {
  [1269] = Vertex.new(216, 233, 23, 68, 65, 52),
  [1401] = Vertex.new(213, 239, -8, 82, 78, 63),
  [1407] = Vertex.new(216, 233, 23, 82, 78, 63),
  [1411] = Vertex.new(201, 223, 42, 82, 78, 63),
  [1416] = Vertex.new(199, 223, 43, 82, 78, 63),
})
local fungusLog2 = Model.new(2412, {
  [1347] = Vertex.new(-161, 222, 48, 68, 65, 52),
  [1428] = Vertex.new(-164, 213, -29, 82, 78, 63),
  [1450] = Vertex.new(-161, 222, 48, 82, 78, 63),
  [1452] = Vertex.new(-156, 225, 47, 82, 78, 63),
  [1458] = Vertex.new(-185, 208, 32, 82, 78, 63),
})
--#endregion
--#region Items
local mortMyreFungus = Model.new(282, {
  [237] = Vertex.new(-12, 60, 76, 30, 30, 23),
  [249] = Vertex.new(-16, 108, 52, 30, 30, 23),
  [257] = Vertex.new(8, 100, 48, 30, 30, 23),
  [273] = Vertex.new(16, 112, 24, 30, 30, 23),
  [281] = Vertex.new(24, 100, 32, 30, 30, 23),
})
--#endregion
--#region Quest Items
local smallMirror = Model.new(300, {
  [103] = Vertex.new(-39, 2, 62, 42, 31, 17),
  [106] = Vertex.new(-39, 2, 62, 42, 31, 17),
  [113] = Vertex.new(0, 2, 81, 42, 31, 17),
  [125] = Vertex.new(-39, 2, 62, 42, 31, 17),
  [245] = Vertex.new(0, 2, 81, 42, 31, 17),
})
local fillimansJournal = Model.new(282, {
  [138] = Vertex.new(64, 56, 84, 10, 112, 13),
  [180] = Vertex.new(52, 56, -72, 10, 112, 13),
  [215] = Vertex.new(64, 56, 84, 10, 112, 13),
  [279] = Vertex.new(-32, 48, 32, 92, 155, 14),
  [281] = Vertex.new(32, 48, 52, 92, 155, 14),
})
local druidicSpell = Model.new(399, {
  [50] = Vertex.new(-28, 16, 68, 4, 42, 5),
  [156] = Vertex.new(16, 16, 72, 4, 42, 5),
  [188] = Vertex.new(-44, 16, -44, 4, 42, 5),
  [258] = Vertex.new(12, 16, -72, 92, 155, 14),
  [273] = Vertex.new(-20, 16, -64, 92, 155, 14),
})
local aUsedSpell = Model.new(399, {
  [45] = Vertex.new(-16, 16, 68, 92, 155, 14),
  [50] = Vertex.new(-28, 16, 68, 92, 155, 14),
  [63] = Vertex.new(-32, 16, 52, 92, 155, 14),
  [156] = Vertex.new(16, 16, 72, 92, 155, 14),
  [188] = Vertex.new(-44, 16, -44, 92, 155, 14),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Drezel, who is located underground near Paterdomus.<ul><li>Make sure you have prayer points before starting.</li><li>You should have six empty backpack spaces; otherwise the food he gives you will appear on the floor.</li><li>If you have just completed Priest in Peril, exit and re-enter the Mausoleum.</li></ul>",
    title = "Getting started",
    neededItems = { ["Ghostspeak amulet"] = { quantity = 1 } },
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("Is there anything else interesting to do around here?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue speaking to Drezel.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Yes, I'll go and look for him."),
      Action.ConversationHighlight:new("Yes, I'm sure."),
    },
    postconditions = { Condition.ConversationText:new("If you can return to me and confirm he is safe") },
  },
  {
    text = "Exit into Morytania.",
    title = "Mort Myre Swamp",
    actions = { Action.Direction:new(3440, 977, 9886) },
    postconditions = { Condition.DistanceTo:new(3428, 1765, 3485, 5) },
  },
  {
    text = "Go through the gate to the south.<ul><li>If you just completed Priest in Peril, unlock the Canifis lodestone before continuing</li></ul>",
    actions = { Action.Direction:new(3443.5, 669, 3457.5) },
    postconditions = { Condition.DistanceTo:new(3443, 565, 3455, 2) },
  },
  {
    text = "Jump across the broken bridge and onto the round island to the south.",
    actions = { Action.Direction:new(3440.5, 229, 3329) },
    postconditions = { Condition.DistanceTo:new(3440, 69, 3332, 2) },
  },
  {
    text = "<i>Search</i> the bench nearby for a small mirror.",
    title = "The ghost of the grotto",
    actions = { Action.Direction:new(3437, 661, 3337) },
    postconditions = { Condition.InventoryContains:new(smallMirror) },
  },
  {
    text = "<i>Search</i> the grotto tree for Filliman's journal.",
    actions = { Action.ModelHighlight:new(Models.objects["natures grotto entrance"]) },
    postconditions = { Condition.InventoryContains:new(fillimansJournal) },
  },
  {
    text = "<i>Equip</i> your ghostspeak amulet and <i>talk</i> to Filliman Tarlock.",
    actions = {
      Action.ModelHighlight:new(fillimanTarlock),
      Action.ConversationHighlight:new("How long have you been a ghost?"),
    },
    postconditions = { Condition.ConversationText:new("I just have to figure out") },
  },
  {
    actions = {
      Action.ModelHighlight:new(fillimanTarlock),
      Action.ConversationHighlight:new("So, what's your plan?"),
    },
    postconditions = { Condition.ConversationText:new("nature spirit") },
  },
  {
    actions = {
      Action.ModelHighlight:new(fillimanTarlock),
      Action.ConversationHighlight:new("How can I help?"),
    },
    postconditions = { Condition.ConversationText:new("figure out the other things we need") },
  },
  {
    text = "Return to Drezel in Paterdomus.",
    title = "Becoming a nature spirit",
    actions = { Action.Direction:new(3424, 965, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3422, 5, 9892, 30) }, --covers both entrances
  },
  {
    actions = {
      Action.Direction:new(3440, 677, 9895, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["drezel"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("Yes, please"),
    },
    postconditions = { Condition.ConversationText:new("Good luck, adventurer.") }, --not tested
  },
  {
    text = "Jump the bridge.",
    actions = { Action.ModelHighlight:new(bridgeShortcut) },
    postconditions = { Condition.DistanceTo:new(3440, 165, 3327, 2) },
  },
  {
    text = "Run to the marked tile.",
    actions = { Action.Direction:new(3422, 597, 3337) },
    postconditions = { Condition.DistanceTo:new(3422, 597, 3337, 1) },
  },
  {
    text = "Cast the druidic spell in your backpack.",
    actions = { Action.InventoryHighlight:new(druidicSpell) },
    postconditions = {
      Condition.ModelVisible:new(fungusLog1),
      Condition.ModelVisible:new(fungusLog2),
    },
  },
  {
    text = "Pick the fungus from the log.",
    actions = {
      Action.ModelHighlight:new(fungusLog1),
      Action.ModelHighlight:new(fungusLog2),
    },
    postconditions = { Condition.InventoryContains:new(mortMyreFungus) },
  },
  {
    text = "Talk to Filliman.",
    actions = { Action.ModelHighlight:new(bridgeShortcut) },
    postconditions = { Condition.DistanceTo:new(3440, 69, 3332, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(fillimanTarlock),
      Action.ConversationHighlight:new("Ok, thanks."),
    },
    postconditions = { Condition.ConversationText:new("Ok, thanks.") },
  },
  {
    text = "<i>Use</i> the Mort myre fungus on the western, yellow stone.",
    actions = {
      Action.Direction:new(3439, 165, 3336),
      Action.InventoryHighlight:new(mortMyreFungus),
    },
    postconditions = { Condition.ConversationText:new("Aha, yes") },
  },
  { postconditions = { Condition.DistanceTo:new(3441, 141, 3336, 1) } }, --to prevent skipping next step
  {
    text = "<i>Use</i> the used spell on the eastern, grey stone.",
    actions = {
      Action.Direction:new(3441, 141, 3336),
      Action.InventoryHighlight:new(aUsedSpell),
    },
    postconditions = { Condition.ConversationText:new("Aha, yes") },
  },
  {
    text = "Stand on top of the orange stone by right-click selecting 'Walk here'.",
    actions = { Action.Direction:new(3440, 141, 3335) },
    postconditions = { Condition.DistanceTo:new(3440, 141, 3335, 0) },
  },
  {
    text = "Tell Filliman the puzzle is solved.",
    actions = {
      Action.ModelHighlight:new(fillimanTarlock),
      Action.ConversationHighlight:new("I think I've solved the puzzle!"),
    },
    postconditions = { Condition.ConversationText:new("Aha, everything") },
  },
  {
    text = "<i>Enter</i> the Grotto tree.",
    actions = { Action.ModelHighlight:new(Models.objects["natures grotto entrance"]) },
    postconditions = { Condition.DistanceTo:new(2272, 1525, 5334, 5) },
  },
  {
    text = "<i>Search</i> the Grotto in the centre and Filliman will speak to you.",
    actions = { Action.Direction:new(2271.5, 1465, 5340) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ConversationHighlight:new("Ok, thanks.") },
    postconditions = { Condition.ConversationText:new("Ok, thanks.") },
  },
  {
    text = "Obtain a silver sickle by purchasing it from the Grand Exchange or crafting one yourself.<ul><li>If crafting, a sickle mould can be bought from the crafting shop in Al Kharid, and silver mined north of Al Kharid.</li></ul>",
    title = "Killing the ghasts",
    postconditions = { Condition.InventoryContains:new(Models.items["silver sickle"]) },
  },
  {
    text = "Return to the grotto quickly by speaking to Drezel.",
    actions = { Action.Direction:new(3424, 1565, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3422, 5, 9892, 30) }, --covers both entrances
  },
  {
    actions = {
      Action.Direction:new(3440, 677, 9895, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["drezel"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("Yes, please"),
    },
    postconditions = { Condition.DistanceTo:new(3440, 85, 3333, 8) },
  },
  {
    text = "Enter the grotto tree.",
    actions = { Action.ModelHighlight:new(Models.objects["natures grotto entrance"]) },
    postconditions = { Condition.DistanceTo:new(2272, 1525, 5334, 5) },
  },
  {
    text = "Talk to the Nature Spirit.",
    actions = { Action.ModelHighlight:new(Models.npcs["nature spirit"]) },
    postconditions = { Condition.ConversationText:new("Ghast to you until you flee") },
  },
  {
    text = "Exit the grotto.",
    actions = { Action.Direction:new(2272, 1925, 5333) },
    postconditions = { Condition.DistanceTo:new(3440, 173, 3337, 5) },
  },
  {
    text = "Jump the bridge and stand next to the nearby rotting log.",
    actions = { Action.ModelHighlight:new(bridgeShortcut) },
    postconditions = { Condition.DistanceTo:new(3440, 357, 3327, 2) },
  },
  {
    actions = { Action.Direction:new(3423, 685, 3336) },
    postconditions = { Condition.DistanceTo:new(3423, 685, 3336, 0) },
  },
  {
    text = "Right-click <i>Bloom</i> the silver sickle (b) in your backpack.<ul><li>Blooming requires at least 1 prayer point.</li></ul>",
    actions = { Action.InventoryHighlight:new(Models.items["silver sickle (b)"]) },
    postconditions = {
      Condition.ModelVisible:new(fungusLog1),
      Condition.ModelVisible:new(fungusLog2),
    },
  },
  {
    text = "Pick the fungi from the log. Repeat until you have at least 3 total fungi in your inventory.",
    actions = {
      Action.InventoryHighlight:new(Models.items["silver sickle (b)"]),
      Action.ModelHighlight:new(fungusLog1),
      Action.ModelHighlight:new(fungusLog2),
    },
    postconditions = { Condition.InventoryContains:new(mortMyreFungus, 3) },
  },
  {
    text = "Fill the druid pouch.",
    actions = { Action.InventoryHighlight:new(Models.items["druid pouch"]) },
    postconditions = { Condition.ChatText:new("You add 3 natures harvests to your druid pouch.") },
  },
  {
    text = "Invoke and kill 3 nearby ghasts, consuming 1 fungus per invocation.",
    actions = { Action.ModelHighlight:new(invokedGhast, { highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("That's all three ghasts!") },
  },
  {
    text = "Return to the Nature Spirit in the grotto.<ul><li>(Optional) After completing the quest, pray at the altar to complete one of the easy Morytania achievements, Fortified Spirit.</li></ul>",
    title = "Finishing up",
    actions = { Action.ModelHighlight:new(Models.objects["natures grotto entrance"]) },
    postconditions = { Condition.DistanceTo:new(2272, 1525, 5334, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["nature spirit"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Nature Spirit",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1089676800,
  prereqQuests = { "Priest in Peril", "The Restless Ghost" },
  questReqs = {
    Types.QuestReq.ironmanOnlySkill("Crafting", 18, true),
    Types.QuestReq.ironmanOnlySkill("Mining", 20, true),
    Types.QuestReq.ironmanOnlySkill("Smithing", 20, true),
  },
  neededItems = {
    ["Silver sickle "] = { quantity = 1, model = Models.items["silver sickle"], duringQuest = true },
    ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] },
  },
  recommendedItems = {
    ["Combat gear"] = { quantity = 1 },
    ["Food if low level"] = { quantity = 1 },
    ["Prayer potion"] = { quantity = 1 },
  },
  combatNPCs = { ["Ghasts"] = { level = "35", quantity = 3 } },
})
