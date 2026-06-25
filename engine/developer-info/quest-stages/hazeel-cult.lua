local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local clivet = Model.new(3801, {
  [2245] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2253] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2255] = Vertex.new(7, 724, -51, 106, 78, 54),
  [2831] = Vertex.new(28, 771, 20, 49, 45, 45),
  [2952] = Vertex.new(-28, 771, 20, 49, 45, 45),
})
local alomone = Model.new(4023, {
  [2101] = Vertex.new(-2, 725, -59, 117, 104, 89),
  [2106] = Vertex.new(-7, 724, -51, 117, 104, 89),
  [2109] = Vertex.new(2, 725, -59, 117, 104, 89),
  [2111] = Vertex.new(7, 724, -51, 117, 104, 89),
  [2587] = Vertex.new(0, 735, -7, 27, 139, 126),
})
--#endregion
--#region Objects
local valve = Model.new(924, {
  [65] = Vertex.new(-176, 388, -196, 63, 68, 63),
  [74] = Vertex.new(-188, 200, -216, 63, 68, 63),
  [119] = Vertex.new(-176, 376, -168, 63, 68, 63),
  [122] = Vertex.new(-176, 388, -180, 63, 68, 63),
  [250] = Vertex.new(-188, 172, -212, 63, 68, 63),
})
local caveEntrance = Model.new(1239, {
  [521] = Vertex.new(5484, 1641, 2264, 90, 83, 83),
  [1052] = Vertex.new(5292, 1165, 1560, 90, 83, 83),
  [1056] = Vertex.new(5292, 1165, 1560, 90, 83, 83),
})
local stairs = Model.new(888, {
  [180] = Vertex.new(4202, 2529, 1867, 80, 71, 50),
  [719] = Vertex.new(4583, 1600, 2480, 80, 71, 50),
  [723] = Vertex.new(4583, 1600, 2480, 80, 71, 50),
  [794] = Vertex.new(4583, 2480, 1536, 80, 71, 50),
  [797] = Vertex.new(4583, 2529, 1536, 80, 71, 50),
})
local range = Model.new(1674, {
  [848] = Vertex.new(138, 484, 129, 24, 22, 22),
  [850] = Vertex.new(127, 484, 135, 102, 96, 93),
  [853] = Vertex.new(135, 484, 135, 68, 64, 63),
  [1019] = Vertex.new(200, 136, 93, 9, 8, 8),
  [1574] = Vertex.new(171, 315, 76, 24, 22, 22),
})
--#endregion
--#region Quest Items
local carnilleanArmour = Model.new(465, {
  [12] = Vertex.new(136, 0, -92, 9, 12, 109),
  [36] = Vertex.new(-96, 0, -92, 9, 12, 109),
  [54] = Vertex.new(124, 28, 32, 109, 17, 9),
  [268] = Vertex.new(48, 28, 112, 9, 12, 109),
  [452] = Vertex.new(80, 48, -72, 49, 45, 45),
})
local poison = Model.new(120, {
  [47] = Vertex.new(-16, 104, -36, 159, 147, 146),
  [51] = Vertex.new(-12, 84, -40, 12, 12, 12),
  [53] = Vertex.new(-4, 92, -40, 12, 12, 12),
  [57] = Vertex.new(4, 84, -40, 12, 12, 12),
  [59] = Vertex.new(12, 92, -40, 12, 12, 12),
})
local chestKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 153, 120, 13),
  [104] = Vertex.new(68, 16, 68, 153, 120, 13),
  [397] = Vertex.new(68, 16, 68, 153, 120, 13),
  [400] = Vertex.new(68, 16, 68, 153, 120, 13),
  [408] = Vertex.new(68, 16, 68, 153, 120, 13),
})
local hazeelScroll = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 146, 146, 134),
  [5] = Vertex.new(-72, 0, -56, 146, 146, 134),
  [187] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [190] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [195] = Vertex.new(-84, 28, -84, 89, 63, 7),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Ceril Carnillean in his house south of the Ardougne.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2565.5, 1605, 3269) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["ceril carnillean"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["ceril carnillean"]),
      Action.ConversationHighlight:new("What's wrong?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["ceril carnillean"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Ceril Carnillean.",
    actions = { Action.ModelHighlight:new(Models.npcs["ceril carnillean"]) },
    postconditions = { Condition.ConversationText:new("get to work") },
  },
  {
    text = "Turn the valve behind Ceril's house to the right.",
    title = "Laying the groundwork",
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2572, 1277, 3263) }) },
    postconditions = { Condition.ConversationText:new("turn the") },
  },
  {
    text = "Turn the valve west of the Clock Tower to the right.",
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2562, 1205, 3247) }) },
    postconditions = { Condition.DistanceTo:new(2562, 1205, 3247, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2562, 1205, 3247) }) },
    postconditions = { Condition.ConversationText:new("turn the") },
  },
  {
    text = "Turn the valve north of the cave to the left.",
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2585, 989, 3245) }) },
    postconditions = { Condition.DistanceTo:new(2585, 989, 3245, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2585, 989, 3245) }) },
    postconditions = { Condition.ConversationText:new("turn the") },
  },
  {
    text = "Turn the valve south of the penguine cage to the right.",
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2597, 1141, 3263) }) },
    postconditions = { Condition.DistanceTo:new(2597, 1141, 3263, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2597, 1141, 3263) }) },
    postconditions = { Condition.ConversationText:new("turn the") },
  },
  {
    text = "Turn the valve north of the monastery to the right.",
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2611, 781, 3242) }) },
    postconditions = { Condition.DistanceTo:new(2611, 781, 3242, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(valve, { atLocation = Location:new(2611, 781, 3242) }) },
    postconditions = { Condition.ConversationText:new("turn the") },
  },
  {
    text = "Enter the cave entrance.",
    actions = { Action.ModelHighlight:new(caveEntrance) },
    postconditions = { Condition.DistanceTo:new(2570, 645, 9682, 4) },
  },
  {
    text = "Pick a side and follow the guide for the according side.",
    warning = "There's no tracking for this. You have to choose yourself.",
  },
  {
    text = "Talk to Clivet.",
    title = "Carnillean's Side",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(clivet),
      Action.ConversationHighlight:new("What do you mean?"),
      Action.ConversationHighlight:new("I will never help you."),
    },
    postconditions = {
      Condition.ConversationText:new("into the sewer system"),
    },
  },
  {
    text = "Board the raft.",
    actions = { Action.Direction:new(2567.5, 645, 9679) },
    postconditions = { Condition.DistanceTo:new(2606, 669, 9692, 4) },
  },
  {
    text = "Talk to Alomone.",
    actions = { Action.ModelHighlight:new(alomone) },
    postconditions = { Condition.ConversationText:new("Prepare to die") },
  },
  { text = "Kill Alomone.", postconditions = { Condition.ModelVisible:new(carnilleanArmour) } },
  {
    text = "Pick up the Carnillean armour he drops.<ul><li>Drop the armour and kill Alomone again if you want another. Drop it before talking to Ceril.</li></ul>",
    actions = { Action.ModelHighlight:new(carnilleanArmour) },
    postconditions = { Condition.InventoryContains:new(carnilleanArmour) },
  },
  {
    text = "Return to Ceril Carnillean.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2565.5, 1605, 3269) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["ceril carnillean"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["ceril carnillean"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["ceril carnillean"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("prove his treachery") },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.ModelHighlight:new(stairs) },
    postconditions = { Condition.DistanceToWithHeight:new(2569, 2597, 3267, 4) },
  },
  {
    text = "Open and search the wardrobe.",
    actions = { Action.Direction:new(2573, 3097, 3267) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    jumpconditions = { Condition.Always:new() },
    jumpOffset = 23,
  },
  {
    text = "Talk to Clivet.",
    title = "The Cult's Side",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(clivet),
      Action.ConversationHighlight:new("What do you mean?"),
      Action.ConversationHighlight:new("So, what would I have to do?"),
      Action.ConversationHighlight:new("Okay, count me in."),
    },
    postconditions = { Condition.ConversationText:new("speak to me once more") },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(2570.5, 1245, 9684) },
    postconditions = { Condition.DistanceTo:new(2587, 1157, 3237, 4) },
  },
  {
    text = "Climb down the trapdoor in the Carnillean mansion.",
    actions = { Action.Direction:new(2569, 1605, 3267, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2569, 5, 9668, 4) },
  },
  {
    text = "Use the bottle of poison on the range.",
    actions = {
      Action.ModelHighlight:new(range),
      Action.InventoryHighlight:new(poison),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(poison),
      Condition.ConversationText:new("paying attention"),
    },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2569, 605, 9667) },
    postconditions = { Condition.DistanceTo:new(2569, 1605, 3268, 4) },
  },
  {
    text = "Talk to Ceril Carnillean.",
    actions = { Action.ModelHighlight:new(Models.npcs["ceril carnillean"]) },
    postconditions = { Condition.ConversationText:new("yeah, me too") },
  },
  {
    text = "Return to the cave entrance.",
    actions = { Action.ModelHighlight:new(caveEntrance) },
    postconditions = { Condition.DistanceTo:new(2570, 645, 9682, 4) },
  },
  {
    text = "Board the raft.",
    actions = { Action.Direction:new(2567.5, 645, 9679) },
    postconditions = { Condition.DistanceTo:new(2606, 669, 9692, 4) },
  },
  {
    text = "Talk to Alomone.",
    actions = { Action.ModelHighlight:new(alomone) },
    postconditions = { Condition.ConversationText:new("true power and glory") },
  },
  {
    text = "Return to the Carnillean mansion basement.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2569, 1605, 3267, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2569, 5, 9668, 4) },
  },
  {
    text = "Search the crates.",
    actions = { Action.Direction:new(2572, 405, 9668) },
    postconditions = { Condition.InventoryContains:new(chestKey) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2569, 605, 9667) },
    postconditions = { Condition.DistanceTo:new(2569, 1605, 3268, 4) },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.ModelHighlight:new(stairs) },
    postconditions = { Condition.DistanceToWithHeight:new(2569, 2597, 3267, 4) },
  },
  {
    text = "Knock on the odd wall.",
    actions = { Action.Direction:new(2566, 3097, 3274.4) },
    postconditions = { Condition.DistanceTo:new(2566, 2597, 3276, 1) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(2565, 3097, 3275) },
    postconditions = { Condition.DistanceTo:new(2566, 3557, 3275, 4) },
  },
  {
    text = "Unlock the chest.",
    actions = { Action.Direction:new(2565, 3557, 3272, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(hazeelScroll) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2565, 3557, 3275, { tile = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(2566, 2597, 3275, 4) },
  },
  {
    text = "Climb down the stairs.",
    actions = { Action.Direction:new(2568, 2597, 3267.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2568, 1605, 3269, 4) },
  },
  {
    text = "Return to the cave entrance.",
    actions = { Action.ModelHighlight:new(caveEntrance) },
    postconditions = { Condition.DistanceTo:new(2570, 645, 9682, 4) },
  },
  {
    text = "Board the raft.",
    actions = { Action.Direction:new(2567.5, 645, 9679) },
    postconditions = { Condition.DistanceTo:new(2606, 669, 9692, 4) },
  },
  {
    text = "Talk to Alomone.",
    actions = { Action.ModelHighlight:new(alomone) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  { postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Hazeel Cult",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.short,
  releaseDate = 1029369600,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = { ["Alomone"] = { level = "9", optional = true, quantity = 1 } },
})
