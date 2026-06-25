local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local dad = Model.new(5721, {
  [3657] = Vertex.new(-36, 1312, -296, 83, 81, 76),
  [3756] = Vertex.new(-48, 1344, -268, 104, 101, 95),
  [4219] = Vertex.new(12, 700, 0, 93, 82, 71),
  [4220] = Vertex.new(0, 660, 0, 93, 82, 71),
  [4221] = Vertex.new(-12, 700, 0, 93, 82, 71),
})
local trollGeneral = Model.new(6138, {
  [183] = Vertex.new(-19, 908, -214, 121, 100, 76),
  [4394] = Vertex.new(13, 895, -224, 110, 98, 84),
  [4443] = Vertex.new(-56, 931, -175, 110, 98, 84),
  [4476] = Vertex.new(-56, 931, -175, 119, 106, 91),
  [4487] = Vertex.new(-56, 931, -175, 137, 122, 105),
})
local berry = Model.new(4899, {
  [2005] = Vertex.new(23, 906, -186, 53, 169, 14),
  [2007] = Vertex.new(16, 903, -189, 53, 169, 14),
  [2009] = Vertex.new(30, 904, -181, 53, 169, 14),
  [2013] = Vertex.new(-15, 903, -189, 53, 169, 14),
  [2015] = Vertex.new(-28, 904, -181, 53, 169, 14),
})
local twig = Model.new(4914, {
  [2395] = Vertex.new(22, 869, -178, 53, 169, 14),
  [2397] = Vertex.new(15, 866, -181, 53, 169, 14),
  [2399] = Vertex.new(28, 867, -174, 53, 169, 14),
  [2403] = Vertex.new(-14, 866, -181, 53, 169, 14),
  [2405] = Vertex.new(-27, 867, -174, 53, 169, 14),
})

-- Objects
local openWall = Model.new(2436, {
  [1] = Vertex.new(-239, 792, -256, 91, 83, 83),
})

local ropeSwing = Model.new(348, {
  [1] = Vertex.new(256, 262, 256, 35, 29, 22),
})

local themap = Model.new(5787, {
  [1] = Vertex.new(57, 701, -100, 83, 69, 52),
})

-- Quest Items
local prisonKey = Model.new(279, {
  [1] = Vertex.new(-16, 16, -64, 162, 141, 83),
  [2] = Vertex.new(-8, 16, -64, 162, 141, 83),
  [3] = Vertex.new(-12, 16, -68, 162, 141, 83),
  [8] = Vertex.new(-36, 16, -28, 162, 141, 83),
  [10] = Vertex.new(-24, 16, -72, 162, 141, 83),
})
local cellKey1 = Model.new(279, {
  [1] = Vertex.new(-16, 16, -64, 110, 102, 84),
  [2] = Vertex.new(-8, 16, -64, 110, 102, 84),
  [3] = Vertex.new(-12, 16, -68, 110, 102, 84),
  [8] = Vertex.new(-36, 16, -28, 110, 102, 84),
  [10] = Vertex.new(-24, 16, -72, 110, 102, 84),
})
local cellKey2 = Model.new(279, {
  [1] = Vertex.new(-16, 16, -64, 96, 99, 91),
  [2] = Vertex.new(-8, 16, -64, 96, 99, 91),
  [3] = Vertex.new(-12, 16, -68, 96, 99, 91),
  [8] = Vertex.new(-36, 16, -28, 96, 99, 91),
  [10] = Vertex.new(-24, 16, -72, 96, 99, 91),
})

---@type QuestStep[]
local steps = {
  --#region Starting out
  {
    text = "Talk to Commander Denulth in Burthorpe, east of the castle.<ul><li>If Denulth tells you that the ambush is still being planned, try asking him about the White Knights first.</li></ul>",
    title = "Starting out",
    actions = { Action.Direction:new(2918, 1597, 3561) },
    postconditions = { Condition.DistanceTo:new(2918, 1597, 3561, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["commander denulth"]),
      Action.ConversationHighlight:new("How goes your fight with the trolls?"),
      Action.ConversationHighlight:new("Is there anything I can do to help?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "If you don't own climbing boots, buy them from Freda first.",
    actions = {
      Action.Direction:new(2820, 3525, 3555),
    },
    postconditions = { Condition.DistanceTo:new(2820, 3525, 3555, 3) },
  },
  {
    actions = {
      Action.ConversationHighlight:new("Can I buy some Climbing boots?"),
      Action.ConversationHighlight:new("Buy standard boots for 12gp."),
    },
    postconditions = { Condition.InventoryContains:new(Items["climbing boots"]) },
  },
  --#endregion
  --#region Mountain climbing
  {
    text = "Enter Sabbot's cave from Death Plateau.",
    title = "Mountain climbing",
    actions = { Action.Direction:new(2858.5, 1589, 3579) },
    postconditions = { Condition.DistanceTo:new(2269, 1013, 4752, 6) },
  },
  {
    text = "Proceed through the cavern's obstacles.",
    actions = { Action.ModelHighlight:new(openWall) },
    postconditions = {
      Condition.DistanceTo:new(3405, 3365, 4283, 10),
      Condition.DistanceTo:new(3422, 2245, 4280, 0),
      Condition.DistanceToWithHeight:new(3406, 2245, 4279, 10),
    }, -- we add multiple posts to ensure people don't have to go back if they stop midway through
  },
  {
    actions = { Action.Direction:new(3406, 3365, 4281) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3406, 2245, 4279, 10),
      Condition.DistanceTo:new(3422, 2245, 4280, 0),
    },
  },
  {
    actions = { Action.Direction:new(3421, 2245, 4280) },
    postconditions = { Condition.DistanceTo:new(3422, 2245, 4280, 0) },
  },
  {
    text = "Keep going through the cave.",
    actions = { Action.Direction:new(3434, 2245, 4276) },
    postconditions = {
      Condition.DistanceTo:new(3434, 2245, 4275, 0),
      Condition.DistanceTo:new(3430, 2245, 4261, 0),
      Condition.DistanceTo:new(3415, 2245, 4260, 0),
      Condition.DistanceTo:new(3417, 2245, 4252, 0),
    },
  },
  {
    actions = { Action.ModelHighlight:new(ropeSwing) },
    postconditions = {
      Condition.DistanceTo:new(3430, 2245, 4261, 0),
      Condition.DistanceTo:new(3415, 2245, 4260, 0),
      Condition.DistanceTo:new(3417, 2245, 4252, 0),
    },
  },
  {
    actions = { Action.Direction:new(3421, 2245, 4260) },
    postconditions = { Condition.DistanceTo:new(3415, 2245, 4260, 0), Condition.DistanceTo:new(3417, 2245, 4252, 0) },
  },
  {
    actions = { Action.Direction:new(3417, 2245, 4253) },
    postconditions = { Condition.DistanceTo:new(3417, 2245, 4252, 0) },
  },
  {
    text = "Exit the cave through the cave exit.",
    actions = { Action.Direction:new(3421.5, 2245, 4238.5) },
    postconditions = { Condition.DistanceTo:new(3425, 3365, 4238, 0) },
  },
  {
    actions = { Action.Direction:new(3436, 3365, 4240) },
    postconditions = { Condition.ModelVisible:new(themap) },
  },
  {
    text = "Climb the rock wall.",
    title = "debug",
    actions = { Action.Direction:new(2848, 3525, 3620) },
    postconditions = { Condition.DistanceTo:new(2848, 3525, 3623, 2) },
  },
  {
    text = "Follow the path and climb another rock wall.",
    actions = { Action.Direction:new(2900, 2565, 3610) },
    postconditions = { Condition.DistanceTo:new(2902, 2565, 3610, 2) },
  },
  --#endregion
  --#region Dad
  {
    text = "Open the arena gate.",
    title = "Dad",
    actions = { Action.Direction:new(2912.5, 1581, 3609.5) },
    postconditions = { Condition.DistanceTo:new(2915, 1581, 3610, 2) },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("No human pass through arena without defeating Dad!") },
  },
  {
    text = "Defeat Dad.<ul><li>Very low level players may need food.</li></ul><ul><li>You may choose to kill him or spare him.</li></ul><ul><li>An easy way to kill Dad safely is to bring him over to the northern gate or down by the southern rocks and safe spot him.</li></ul>",
    actions = { Action.ModelHighlight:new(dad), Action.Direction:new(2927, 1605, 3617) },
    postconditions = { Condition.ConversationText:new("Stop! You win. Not hurt Dad.") },
  },
  {
    text = "Exit through the north gate.",
    title = "debug",
    actions = { Action.Direction:new(2927.5, 1605, 3619) },
    postconditions = { Condition.DistanceTo:new(2927, 1605, 3622, 3) },
  },
  {
    text = "Enter the cave to the northwest.",
    actions = { Action.Direction:new(2911.5, 1605, 3638) },
    postconditions = { Condition.DistanceTo:new(2907, 1277, 10019, 4) },
  },
  {
    text = "Exit the cave to the north.",
    actions = { Action.Direction:new(2907, 1277, 10037) },
    postconditions = { Condition.DistanceTo:new(2922, 1605, 3658, 3) },
  },
  {
    text = "Run around the mountain and enter the cave entrance.<ul><li>If you have 47 agility, you can use the agility shortcuts instead.</li></ul>",
    actions = { Action.Direction:new(2928, 1605, 3678) },
    postconditions = {
      Condition.DistanceTo:new(2928, 1605, 3678, 3),
      Condition.DistanceTo:new(2849, 2565, 3687, 4),
      Condition.DistanceTo:new(2865, 3045, 3664, 4),
    },
  },
  {
    actions = { Action.Direction:new(2923, 1605, 3693) },
    postconditions = {
      Condition.DistanceTo:new(2923, 1605, 3693, 4),
      Condition.DistanceTo:new(2849, 2565, 3687, 4),
      Condition.DistanceTo:new(2865, 3045, 3664, 4),
    },
  },
  {
    actions = { Action.Direction:new(2911, 1605, 3703) },
    postconditions = {
      Condition.DistanceTo:new(2911, 1605, 3703, 4),
      Condition.DistanceTo:new(2849, 2565, 3687, 4),
      Condition.DistanceTo:new(2865, 3045, 3664, 4),
    },
  },
  {
    actions = { Action.Direction:new(2884, 1605, 3701) },
    postconditions = {
      Condition.DistanceTo:new(2884, 1605, 3701, 4),
      Condition.DistanceTo:new(2849, 2565, 3687, 4),
      Condition.DistanceTo:new(2865, 3045, 3664, 4),
    },
  },
  {
    actions = { Action.Direction:new(2849, 2565, 3687) },
    postconditions = { Condition.DistanceTo:new(2837, 2901, 10090, 4) },
  },
  --#endregion
  --#region Prison break
  {
    text = "Run south, go through the open wooden door, then continue north.",
    title = "Prison break",
    actions = { Action.Direction:new(1, 1, 1) },
    postconditions = { Condition.DistanceTo:new(1, 1, 1, 8) },
  },
  {
    text = "Kill a troll general and take the prison key.<ul><li>If you're low level, you can lure the troll general to the marked tile.</li></ul>",
    actions = { Action.ModelHighlight:new(trollGeneral), Action.Direction:new(2832, 2925, 10061) },
    postconditions = { Condition.InventoryContains:new(prisonKey) },
  },
  {
    text = "Climb down the stone staircase to the north.",
    actions = { Action.Direction:new(2843, 2853, 10108.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2840, 1797, 10108, 5) },
  },
  {
    text = "Unlock the Prison Door to the immediate east and head down the stone staircase.",
    actions = { Action.Direction:new(2848, 1877, 10107) },
    postconditions = { Condition.DistanceTo:new(2850, 1893, 10107, 2) },
  },
  {
    actions = { Action.Direction:new(2852.5, 1893, 10107) },
    postconditions = { Condition.DistanceToWithHeight:new(2852, 885, 10104, 6) },
  },
  {
    text = "Pickpocket (or kill) Twig and Berry. Safe spot inside one of the open cells if needed.",
    actions = { Action.ModelHighlight:new(berry) },
    postconditions = { Condition.InventoryContains:new(cellKey1) },
  },
  { actions = { Action.ModelHighlight:new(twig) }, postconditions = { Condition.InventoryContains:new(cellKey2) } },
  {
    text = "Take the cell keys and unlock the two jail cells.",
    actions = { Action.Direction:new(2831.5, 861, 10082) },
    postconditions = {
      Condition.ConversationText:new("Thanks! I'm off back home!"),
    },
  },
  {
    actions = { Action.Direction:new(2831.5, 741, 10078) },
    postconditions = {
      Condition.ConversationText:new("Thank you, my friend."),
    },
  },
  {
    text = "Talk to Dunstan, the blacksmith in north-eastern Burthorpe.",
    actions = { Action.Direction:new(2926, 1349, 3550.5) },
    postconditions = { Condition.ModelVisible:new(NPCs["dunstan"]) },
  },
  { actions = { Action.ModelHighlight:new(NPCs["dunstan"]) }, postconditions = { Condition.QuestComplete:new() } },
  --#endregion
}

return Quest:new({
  name = "Troll Stronghold",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1093305600,
  prereqQuests = { "Death Plateau" },
  questReqs = {
    Types.QuestReq.skill("Agility", 15),
    Types.QuestReq.skill("Thieving", 30),
  },
  neededItems = {
    ["Climbing boots, rock climbing boots or 12 coins"] = {
      quantity = 1,
      model = Items["climbing boots"],
      duringQuest = true,
    },
    ["Free inventory spaces"] = { quantity = 4 },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Twig"] = { level = "70", optional = true, quantity = 1 },
    ["Berry"] = { level = "70", optional = true, quantity = 1 },
    ["Dad"] = { level = "77", quantity = 1 },
    ["Troll General"] = { level = "91", quantity = 1 },
  },
})
