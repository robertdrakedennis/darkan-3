local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local captainLawgof = Model.new(7101, {
  [1389] = Vertex.new(110, 470, 39, 24, 24, 26),
  [5078] = Vertex.new(136, 457, 44, 131, 93, 11),
  [5085] = Vertex.new(163, 437, 36, 131, 93, 11),
  [5139] = Vertex.new(-134, 457, 44, 131, 93, 11),
  [5145] = Vertex.new(-161, 437, 36, 131, 93, 11),
})
local nulodion = Model.new(5358, {
  [4499] = Vertex.new(19, 551, -3, 75, 51, 38),
  [4515] = Vertex.new(-15, 550, -6, 75, 51, 38),
  [4946] = Vertex.new(67, 381, -102, 53, 36, 27),
  [4950] = Vertex.new(-67, 381, -102, 53, 36, 27),
  [5039] = Vertex.new(-56, 417, -71, 53, 36, 27),
})
--#endregion
--#region Objects
local brokenWall = Model.any({
  Model.new(1650, {
    [278] = Vertex.new(-457, 445, 0, 127, 127, 127),
    [353] = Vertex.new(-434, 317, -29, 127, 127, 127),
    [968] = Vertex.new(-434, 314, 29, 127, 127, 127),
    [1141] = Vertex.new(-457, 445, 0, 127, 127, 127),
    [1145] = Vertex.new(-457, 445, 0, 127, 127, 127),
  }),
  Model.new(1650, {
    [278] = Vertex.new(-457, 323, 0, 127, 127, 127),
    [1087] = Vertex.new(-391, 389, 64, 127, 127, 127),
    [1118] = Vertex.new(-428, 349, 63, 127, 127, 127),
    [1141] = Vertex.new(-457, 323, 0, 127, 127, 127),
    [1145] = Vertex.new(-457, 323, 0, 127, 127, 127),
  }),
  Model.new(1650, {
    [278] = Vertex.new(-457, 458, 0, 127, 127, 127),
    [968] = Vertex.new(-434, 328, 29, 127, 127, 127),
    [1118] = Vertex.new(-428, 475, 63, 127, 127, 127),
    [1141] = Vertex.new(-457, 458, 0, 127, 127, 127),
    [1145] = Vertex.new(-457, 458, 0, 127, 127, 127),
  }),
  Model.new(1650, {
    [278] = Vertex.new(-457, 464, 0, 127, 127, 127),
    [353] = Vertex.new(-434, 328, -29, 127, 127, 127),
    [968] = Vertex.new(-434, 338, 29, 127, 127, 127),
    [1141] = Vertex.new(-457, 464, 0, 127, 127, 127),
    [1145] = Vertex.new(-457, 464, 0, 127, 127, 127),
  }),
  Model.new(1650, {
    [278] = Vertex.new(-457, 448, 0, 127, 127, 127),
    [353] = Vertex.new(-434, 316, -29, 127, 127, 127),
    [968] = Vertex.new(-434, 318, 29, 127, 127, 127),
    [1141] = Vertex.new(-457, 448, 0, 127, 127, 127),
    [1145] = Vertex.new(-457, 448, 0, 127, 127, 127),
  }),
  Model.new(1650, {
    [278] = Vertex.new(-457, 466, 0, 127, 127, 127),
    [968] = Vertex.new(-434, 336, 29, 127, 127, 127),
    [1118] = Vertex.new(-428, 483, 63, 127, 127, 127),
    [1141] = Vertex.new(-457, 466, 0, 127, 127, 127),
    [1145] = Vertex.new(-457, 466, 0, 127, 127, 127),
  }),
})
--#endregion
--#region Items
local ammoMould = Model.new(414, {
  [111] = Vertex.new(-224, 0, -184, 80, 74, 73),
  [114] = Vertex.new(-224, 0, -184, 80, 74, 73),
  [120] = Vertex.new(-224, 0, 192, 80, 74, 73),
  [126] = Vertex.new(-224, 0, 192, 80, 74, 73),
  [128] = Vertex.new(-224, 0, 192, 80, 74, 73),
})
--#endregion
--#region Quest Items
local stones = Model.new(972, {
  [233] = Vertex.new(-181, 248, 58, 127, 127, 127),
  [859] = Vertex.new(105, 262, -58, 127, 127, 127),
  [863] = Vertex.new(105, 262, -58, 127, 127, 127),
  [868] = Vertex.new(105, 262, -58, 127, 127, 127),
  [874] = Vertex.new(105, 262, -58, 127, 127, 127),
})
local dwarfRemains = Model.new(408, {
  [39] = Vertex.new(212, 0, -120, 126, 126, 115),
  [302] = Vertex.new(-140, 0, 236, 126, 126, 115),
  [374] = Vertex.new(-152, 0, 172, 126, 126, 115),
  [377] = Vertex.new(-164, 0, 180, 126, 126, 115),
  [408] = Vertex.new(-172, 0, 160, 126, 126, 115),
})
local toolkit = Model.new(561, {
  [48] = Vertex.new(-80, -8, 112, 78, 78, 71),
  [141] = Vertex.new(-76, 0, 100, 87, 87, 80),
  [159] = Vertex.new(-88, 0, 88, 87, 87, 80),
  [533] = Vertex.new(132, -4, -40, 68, 53, 34),
  [560] = Vertex.new(-116, -8, -92, 68, 53, 34),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Captain Lawgof by the coal truck mining site west of Seers' Village.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, and game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(2568, 1669, 3460, { distance = 16 }),
      Action.ModelHighlight:new(captainLawgof, { distance = 16 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Captain Lawgof.",
    actions = {
      Action.Direction:new(2568, 1669, 3460, { distance = 16 }),
      Action.ModelHighlight:new(captainLawgof, { distance = 16 }),
    },
    postconditions = { Condition.ConversationText:new("back to me once you") },
  },
  {
    text = "Inspect the six broken sections of wall.",
    title = "Repair and retrieval",
    actions = { Action.ModelHighlight:new(brokenWall, { highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("now fixed all these walls") },
  },
  {
    text = "Talk to Captain Lawgof.",
    actions = {
      Action.Direction:new(2568, 1669, 3460, { distance = 16 }),
      Action.ModelHighlight:new(captainLawgof, { distance = 16 }),
    },
    postconditions = { Condition.ConversationText:new("we mop up these remaining goblins") },
  },
  {
    text = "Climb up the ladders, directly south of the gate.",
    actions = { Action.Direction:new(2570, 3605, 3441) },
    postconditions = { Condition.DistanceToWithHeight:new(2570, 4165, 3442, 4) },
  },
  {
    actions = { Action.Direction:new(2570, 4765, 3443) },
    postconditions = { Condition.DistanceToWithHeight:new(2569, 5157, 3443, 4) },
  },
  {
    text = "Take the dwarf remains.",
    actions = { Action.ModelHighlight:new(dwarfRemains) },
    postconditions = { Condition.InventoryContains:new(dwarfRemains) },
  },
  {
    text = "Talk to Captain Lawgof.",
    actions = {
      Action.Direction:new(2568, 1669, 3460, { distance = 16 }),
      Action.ModelHighlight:new(captainLawgof, { distance = 16 }),
    },
    postconditions = { Condition.ConversationText:new("see if I can find their hideout") },
  },
  {
    text = "Enter the cave directly north of the Ardougne lodestone.",
    actions = { Action.Direction:new(2627.5, 285, 3393) },
    postconditions = { Condition.DistanceTo:new(2619, 1237, 9797, 4) },
  },
  {
    text = "Search the crates at the end of the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(2619, 1237, 9797),
        Location:new(2612, 1029, 9806),
        Location:new(2600, 1197, 9811),
        Location:new(2593, 1037, 9820),
        Location:new(2596, 1133, 9824),
        Location:new(2592, 37, 9829),
        Location:new(2579, 797, 9841),
        Location:new(2568, 69, 9846),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2568, 69, 9846, 10) },
  },
  { postconditions = { Condition.ConversationText:new("Thanks again") } },
  {
    text = "Return and talk to Captain Lawgof.",
    actions = {
      Action.Direction:new(2568, 1669, 3460, { distance = 16 }),
      Action.ModelHighlight:new(captainLawgof, { distance = 16 }),
      Action.ConversationHighlight:new("Okay, I'll see what I can do."),
    },
    postconditions = { Condition.ConversationText:new("back to me") },
  },
  {
    text = "<i>Use</i> the toolkit on the broken multicannon.<ul><li>Use the hooked tool on the spring.</li><li>Use the pliers on the safety switch at the bottom.</li><li>Use the tooth tool on the gear located at the bottom of the hammer.</li></ul>",
    actions = { Action.InventoryHighlight:new(toolkit) },
    postconditions = { Condition.ChatText:new("fixed the cannon") },
  },
  {
    text = "Talk to Captain Lawgof. Make sure to finish conversation.<ul><li>This conversation must be completely finished.</li></ul>",
    actions = {
      Action.Direction:new(2568, 1669, 3460, { distance = 16 }),
      Action.ModelHighlight:new(captainLawgof, { distance = 16 }),
      Action.ConversationHighlight:new("Okay then, just for you!"),
    },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Talk to Nulodion, next to the dwarven multicannon north-east of the Falador lodestone.<ul><li>Drop the ammo mould, and talk to him for another. Add the second one to your toolbelt.</li></ul>",
    title = "Finishing up",
    actions = {
      Action.Direction:new(3012, 2565, 3442, { distance = 16 }),
      Action.ModelHighlight:new(nulodion, { distance = 17 }),
    },
    postconditions = { Condition.ChatText:new("gives you another mould") },
  },
  {
    text = "Return to Captain Lawgof.",
    actions = {
      Action.Direction:new(2568, 1669, 3460, { distance = 16 }),
      Action.ModelHighlight:new(captainLawgof, { distance = 16 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Dwarf Cannon",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1053993600,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
