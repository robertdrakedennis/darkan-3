local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local thormac = Model.new(4215, {
  [1897] = Vertex.new(2, 725, -59, 108, 79, 56),
  [1901] = Vertex.new(7, 724, -51, 108, 79, 56),
  [1915] = Vertex.new(-2, 725, -59, 108, 79, 56),
  [1920] = Vertex.new(-7, 724, -51, 108, 79, 56),
  [3476] = Vertex.new(40, 711, -12, 119, 119, 130),
})
local seer = Model.new(3213, {
  [1678] = Vertex.new(-2, 725, -59, 108, 79, 56),
  [1686] = Vertex.new(2, 725, -59, 108, 79, 56),
  [1688] = Vertex.new(7, 724, -51, 108, 79, 56),
  [2840] = Vertex.new(-30, 721, -31, 162, 149, 149),
  [2990] = Vertex.new(30, 721, -31, 162, 149, 149),
})
local jailer = Model.new(5565, {
  [3718] = Vertex.new(-193, 318, -3, 37, 38, 35),
  [3719] = Vertex.new(-191, 315, -3, 37, 38, 35),
  [3720] = Vertex.new(-191, 321, -3, 37, 38, 35),
  [3721] = Vertex.new(-189, 318, -3, 37, 38, 35),
})
local velrak = Model.new(3831, {
  [2179] = Vertex.new(2, 725, -59, 108, 79, 56),
  [2183] = Vertex.new(7, 724, -51, 108, 79, 56),
  [2197] = Vertex.new(-2, 725, -59, 108, 79, 56),
  [2202] = Vertex.new(-7, 724, -51, 108, 79, 56),
  [3176] = Vertex.new(40, 711, -12, 96, 89, 88),
})
local pitScorpion = Model.new(37080, {
  [2827] = Vertex.new(229, 24, -468, 127, 127, 127),
  [5792] = Vertex.new(-229, 24, -468, 127, 127, 127),
  [7543] = Vertex.new(229, 24, -468, 127, 127, 127),
  [7810] = Vertex.new(190, 35, -452, 127, 127, 127),
  [14698] = Vertex.new(-190, 35, -452, 127, 127, 127),
})
--#endregion
--#region Objects
local taverlyDungeonEntranceSteps = Model.new(7347, {
  [5] = Vertex.new(2260, 317, 3877, 0, 0, 0),
  [786] = Vertex.new(3597, 513, 3898, 39, 48, 51),
  [1725] = Vertex.new(2405, -434, 3526, 28, 30, 31),
  [1782] = Vertex.new(2419, -33, 2818, 44, 54, 58),
  [3142] = Vertex.new(3255, -27, 2822, 46, 49, 51),
})
local monasteryEastLadder = Model.new(528, {
  [301] = Vertex.new(1058, 2763, 5721, 127, 127, 127),
  [305] = Vertex.new(1016, 2786, 6020, 127, 127, 127),
  [325] = Vertex.new(1137, 2984, 5721, 127, 127, 127),
  [331] = Vertex.new(1073, 2957, 6020, 127, 127, 127),
  [343] = Vertex.new(1073, 2957, 6020, 127, 127, 127),
})
--#endregion
--#region Quest Items
local scorpionCage = Model.new(444, {
  [3] = Vertex.new(144, 0, -160, 42, 30, 3),
  [417] = Vertex.new(144, 160, -160, 42, 30, 3),
  [420] = Vertex.new(144, 160, -160, 42, 30, 3),
  [440] = Vertex.new(-144, 160, 80, 42, 30, 3),
  [443] = Vertex.new(-144, 160, 80, 42, 30, 3),
})
local jailKey = Model.new(444, {
  [2] = Vertex.new(-12, 16, -84, 122, 87, 11),
  [86] = Vertex.new(52, 16, 80, 122, 87, 11),
  [99] = Vertex.new(68, 16, 68, 122, 87, 11),
  [104] = Vertex.new(68, 16, 68, 122, 87, 11),
  [397] = Vertex.new(68, 16, 68, 122, 87, 11),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Thormac on the top floor of the Sorcerer's Tower, north-east of the Ardougne Lodestone.",
    title = "Getting started",
    actions = { Action.Direction:new(2701, 2045, 3408) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2701, 2405, 3407, 3),
      Condition.ModelVisible:new(thormac), --if user starts the helper from the top of the tower
    },
  },
  {
    actions = { Action.Direction:new(2704, 3005, 3403) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2704, 3365, 3404, 3),
      Condition.ModelVisible:new(thormac), --if user starts the helper from the top of the tower
    },
  },
  {
    actions = { Action.Direction:new(2699, 3965, 3405) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2699, 4325, 3404, 4),
      Condition.ModelVisible:new(thormac), --if user starts the helper from the top of the tower
    },
  },
  { actions = { Action.ModelHighlight:new(thormac) }, postconditions = { Condition.QuestInterfaceOpen:new() } },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Thormac.",
    actions = {
      Action.ModelHighlight:new(thormac),
      Action.ConversationHighlight:new("What assistance do you need?"),
    },
    postconditions = { Condition.ConversationText:new("where the scorpions are now") },
  },
  {
    text = "Teleport to the Seers' Village lodestone, then speak to a Seer.",
    actions = {
      Action.ModelHighlight:new(seer),
      Action.ConversationHighlight:new("Talk about Scorpion Catcher."),
      Action.ConversationHighlight:new("Your friend Thormac sent me to speak to you."),
    },
    postconditions = { Condition.ConversationText:new("Where would be the fun in that?") },
  },
  {
    text = "Use the Taverly lodestone and enter the Taverley Dungeon to the south.",
    title = "First scorpion",
    warning = "If you have the dusty key on the keyring, manually skip ahead to 'Open the gate with the dusty key in your inventory'.",
    neededItems = {
      ["Scorpion cage"] = { quantity = 1, model = scorpionCage },
      ["Food if lower combat"] = { quantity = 1 },
    },
    actions = { Action.ModelHighlight:new(taverlyDungeonEntranceSteps) },
    postconditions = { Condition.DistanceToWithHeight:new(2886, 1061, 9795, 3) },
  },
  {
    text = "Follow the path to the Black Knights compound.",
    actions = { Action.Direction:new(2888.5, 1285, 9830.5) },
    postconditions = {
      Condition.DistanceTo:new(2891, 1285, 9830, 2),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2892.5, 1421, 9825.5) },
    postconditions = {
      Condition.DistanceTo:new(2893, 1597, 9823, 2),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2911, 1261, 9819) },
    postconditions = {
      Condition.DistanceTo:new(2906, 1245, 9819, 4),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2940, 701, 9806) },
    postconditions = {
      Condition.DistanceTo:new(2940, 701, 9806, 8),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2936, 965, 9777) },
    postconditions = {
      Condition.DistanceTo:new(2936, 965, 9777, 8),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2931, 933, 9755) },
    postconditions = {
      Condition.DistanceTo:new(2931, 933, 9755, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]), -- don't have 70 agility, but have key in inventory
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2914, 949, 9741) },
    postconditions = {
      Condition.DistanceTo:new(2914, 949, 9741, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.Direction:new(2908, 1000, 9712) },
    postconditions = {
      Condition.DistanceTo:new(2908, 1000, 9712, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Go to the cells to the south-east and kill the Jailer.",
    actions = { Action.Direction:new(2931, 985, 9692) },
    postconditions = {
      Condition.DistanceTo:new(2931, 985, 9692, 8),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    actions = { Action.ModelHighlight:new(jailer) },
    postconditions = {
      Condition.ModelVisible:new(jailKey),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Pick up the jail key.",
    actions = { Action.ModelHighlight:new(jailKey) },
    postconditions = {
      Condition.InventoryContains:new(jailKey),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Use the jail key on the cell door.",
    actions = { Action.Direction:new(2931, 965, 9689.5), Action.InventoryHighlight:new(jailKey) },
    postconditions = {
      Condition.DistanceTo:new(2931, 965, 9687, 2),
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Talk to Velrak the explorer for a dusty key.",
    actions = {
      Action.ModelHighlight:new(velrak),
      Action.ConversationHighlight:new("So...do you know anywhere good to explore?"),
      Action.ConversationHighlight:new("Yes, please!"),
    },
    postconditions = {
      Condition.InventoryContains:new(Models.items["dusty key"]),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Circle back the way you came and bridge you passed earlier.",
    actions = { Action.Direction:new(2927, 925, 9756) },
    postconditions = {
      Condition.DistanceTo:new(2936, 965, 9777, 8), -- if entered dungeon with key in inventory
      Condition.DistanceTo:new(2927, 925, 9756, 8),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Open the gate with the dusty key in your inventory.",
    actions = { Action.Direction:new(2923.5, 1173, 9803) },
    postconditions = {
      Condition.DistanceTo:new(2921, 1125, 9803, 2),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Run past the blue dragons.",
    actions = { Action.Direction:new(2895, 1213, 9796) },
    postconditions = {
      Condition.DistanceTo:new(2894, 1373, 9792, 4),
      Condition.DistanceTo:new(2892, 1221, 9799, 2), -- used 70 agility shortcut
      Condition.DistanceTo:new(2877, 1237, 9811, 2), -- used 80 agility shortcut
    },
  },
  {
    text = "Search the Odd wall next to the poisonous spiders.<ul><li>If the wall does not open go back to the Seer and make sure to finish the dialogue.</li></ul>",
    actions = { Action.Direction:new(2875, 965, 9799) },
    postconditions = {
      Condition.DistanceTo:new(2876, 997, 9796, 2),
      Condition.DistanceTo:new(2875, 965, 9798, 0),
    },
  },
  {
    text = "Use the scorpion cage on the Kharidian scorpion.",
    actions = { Action.ModelHighlight:new(pitScorpion), Action.InventoryHighlight:new(scorpionCage) },
    postconditions = { Condition.ModelNotVisible:new(pitScorpion) },
  },
  {
    text = "Go to the Barbarian Outpost.<ul><li>A games necklace is highly recommended.</li></ul>",
    title = "Second scorpion",
    actions = { Action.Direction:new(2545.5, 965, 3569.5) },
    postconditions = { Condition.DistanceTo:new(2548, 965, 3569, 2) },
  },
  {
    text = "Use the scorpion cage on the Kharidian scorpion (it might be inside the building).",
    actions = {
      Action.ModelHighlight:new(pitScorpion),
      Action.InventoryHighlight:new(scorpionCage),
    },
    postconditions = { Condition.DistanceTo:new(2553, 965, 3570, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(pitScorpion),
      Action.InventoryHighlight:new(scorpionCage),
    },
    postconditions = { Condition.ModelNotVisible:new(pitScorpion) },
  },
  {
    text = "Go to the Edgeville Monastery, west of the Edgeville lodestone).",
    title = "Third scorpion",
    actions = { Action.Direction:new(3057, 1605, 3483) },
    postconditions = {
      Condition.DistanceTo:new(3053, 1605, 3498, 2),
      Condition.DistanceTo:new(3051, 1605, 3482, 2),
    },
  },
  {
    text = "Climb the eastern ladder and go to the east side of the floor.<ul><li>If you are stopped, talk to Abbot Langley and join the order.</li></ul>",
    actions = {
      Action.ModelHighlight:new(monasteryEastLadder),
      Action.ConversationHighlight:new("Well can I join your order?"),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3056, 2821, 3483, 3) },
  },
  {
    text = "Catch the scorpion.",
    actions = { Action.ModelHighlight:new(pitScorpion), Action.InventoryHighlight:new(scorpionCage) },
    postconditions = { Condition.ModelNotVisible:new(pitScorpion) },
  },
  {
    text = "Return to Thormac.",
    title = "Finishing up",
    actions = { Action.Direction:new(2701, 1445, 3408) },
    postconditions = { Condition.DistanceToWithHeight:new(2701, 2405, 3407, 3) },
  },
  {
    actions = { Action.Direction:new(2704, 2405, 3403) },
    postconditions = { Condition.DistanceToWithHeight:new(2704, 3365, 3404, 3) },
  },
  {
    actions = { Action.Direction:new(2699, 3365, 3405) },
    postconditions = { Condition.DistanceToWithHeight:new(2699, 4325, 3404, 4) },
  },
  { actions = { Action.ModelHighlight:new(thormac) }, postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Scorpion Catcher",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.short,
  releaseDate = 1017014400,
  prereqQuests = { "Bar Crawl (miniquest)" },
  questReqs = { Types.QuestReq.skill("Prayer", 31) },
  neededItems = {
    ["Dusty key (unnecessary with 70 Agility)"] = {
      quantity = 1,
      model = Models.items["dusty key"],
      duringQuest = true,
    },
  },
})
