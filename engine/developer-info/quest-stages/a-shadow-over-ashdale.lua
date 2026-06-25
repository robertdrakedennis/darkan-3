local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model = Types.Model
local Vertex = Types.Vertex

--#region NPCs
local gudrik = Model.new(8241, {
  [1] = Vertex.new(-102, 25, -31, 84, 85, 91),
})

local lucy = Model.new(4641, {
  [63] = Vertex.new(24, 738, -38, 50, 48, 46),
  [87] = Vertex.new(-24, 738, -38, 50, 48, 46),
  [109] = Vertex.new(-2, 716, -57, 139, 111, 88),
  [115] = Vertex.new(2, 716, -57, 139, 111, 88),
  [3821] = Vertex.new(-26, 757, -7, 78, 59, 23),
})

local crassianScout = Model.new(7500, {
  [2454] = Vertex.new(-309, 351, -309, 254, 254, 254),
  [2638] = Vertex.new(268, 365, -302, 254, 254, 254),
  [2640] = Vertex.new(309, 351, -309, 254, 254, 254),
  [5173] = Vertex.new(-268, 365, -302, 254, 254, 254),
  [5420] = Vertex.new(309, 351, -309, 254, 254, 254),
})

local scout = Model.new(7500, {
  [1] = Vertex.new(56, 141, -223, 254, 254, 254),
})

local tentacle = Model.any({
  Model.new(1173, {
    [1] = Vertex.new(156, -764, -49, 254, 254, 254),
  }),
  Model.new(1173, {
    [1] = Vertex.new(19, 2370, -138, 254, 254, 254),
  }),
})
--#endregion
--#region Objects
local gangplank = Model.new(660, {
  [1] = Vertex.new(2048, 1245, 3072, 130, 103, 89),
})

--- The small grid-like pattern visible in the tunnel
local gridInTunnel = Model.new(6, {
  [1] = Vertex.new(969, 41, 1529, 99, 132, 155),
})

local tunnelLadder = Model.new(2193, {
  [1] = Vertex.new(4002, 3025, 3599, 193, 187, 190),
})

local stormDrain = Model.new(48, {
  [1] = Vertex.new(2833, 6330, 1403, 43, 50, 55),
})

local closedDoor = Model.new(1032, {
  [1] = Vertex.new(444, 10778, 1002, 119, 109, 109),
})

local openDoor = Model.new(1032, {
  [1] = Vertex.new(-278, 346, -188, 119, 109, 109),
})

local stairscase = Model.new(2073, {
  [1] = Vertex.new(-209, -4, -807, 181, 116, 56),
})

local torch = Model.new(795, {
  [1] = Vertex.new(-204, 1056, -18, 255, 254, 254),
})

local valveOuterWheel = Model.new(288, {
  [1] = Vertex.new(114, 0, 0, 71, 45, 22),
})

local valveInnerWheel = Model.new(252, {
  [1] = Vertex.new(-7, 18, 9, 0, 0, 0),
})

local pitch = Model.new(672, {
  [1] = Vertex.new(-24, 45, -24, 0, 0, 0),
})

local brokenPipe = Model.new(768, {
  [1] = Vertex.new(-550, 49, -40, 254, 254, 254),
})

local valveWheel = Model.new(540, {
  [1] = Vertex.new(-7, 18, 9, 0, 0, 0),
})

local waterPuzzle1 = Model.new(1011, {
  [1] = Vertex.new(3839, 1882, 3648, 91, 236, 47, 0.000),
  [3] = Vertex.new(3840, 1881, 3648, 91, 236, 47, 0.000),
  [648] = Vertex.new(3876, 1915, 3611, 143, 153, 156),
  [780] = Vertex.new(4084, 2086, 3431, 143, 153, 156),
  [782] = Vertex.new(4072, 2046, 3469, 143, 153, 156),
})

local waterPuzzleLocation = Model.new(243, {
  [186] = Vertex.new(-103, 518, 244, 143, 153, 156),
  [192] = Vertex.new(143, 513, 235, 143, 153, 156),
  [210] = Vertex.new(-143, 513, -235, 143, 153, 156),
  [216] = Vertex.new(-172, 682, 99, 38, 164, 194, 0.7490),
  [241] = Vertex.new(-172, 682, -99, 38, 164, 194, 0.7490),
})

local openDoor2 = Model.new(3402, {
  [1] = Vertex.new(-260, 79, 411, 119, 109, 109),
})

local lever = Model.new(312, {
  [1] = Vertex.new(-375, 637, -18, 0, 0, 0),
})

local bigbarrier = Model.new(11376, {
  [1] = Vertex.new(-1314, 98, -6, 122, 160, 33),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
--#endregion

-- In this quest we cannot use directions, as most of the quest is instanced

local steps = {
  {
    text = "To start, talk to Gudrik, east of Gerrant's Fishy Business in Port Sarim.",
    title = "Return to Ashdale",
    actions = { Action.Direction:new(3029, 741, 3229) },
    postconditions = { Condition.DistanceTo:new(3029.5, 741, 3229.5, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gudrik),
      Action.ConversationHighlight:new("Ask about A Shadow over Ashdale."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Gudrik to travel to Ashdale (by night).",
    actions = {
      Action.ConversationHighlight:new("Ashdale by night (A Shadow over Ashdale)."),
      Action.ConversationHighlight:new("Can you take me to the island of Ashdale?"),
    },
    postconditions = { Condition.ModelVisible:new(gangplank) },
  },
  {
    text = "Run north over the bridge and then immediately north-west to Lucy's house (look for the only yellow dot on the minimap) and talk with her.",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(1, 3232, 53, { distance = 12, instance = true }),
      Action.ModelHighlight:new(lucy, { distance = 12 }),
      Action.ConversationHighlight:new("I'm here to help."),
      Action.ConversationHighlight:new("Why haven't you tried to leave?"),
    },
    postconditions = {
      Condition.ConversationText:new("Please be careful."),
    },
  },
  {
    text = "Enter the storm drain just west of the house.",
    actions = {
      Action.Direction:new(-5, 1600, 55, { instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(gridInTunnel) },
  },
  {
    text = "Go through in the sewer, running past crassians",
    title = "Sewer raider",
    actions = {
      Action.Direction:new(28, 522, 12, { instance = true }),
      Action.ResetInstance:new(),
    },
    postconditions = {
      Condition.ModelVisible:new(crassianScout),
    },
  },

  {
    -- splitting these steps in case someone comes back
    text = "After the cutscene continue to the ladder and climb up.",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(-46, 2496, 31, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(tunnelLadder),
    },
    postconditions = { Condition.ModelVisible:new(stormDrain) },
  },
  {
    text = "Run west, then south up the stairs. Continue south through Ashdale, through the gate and into the house.",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(closedDoor, { distance = 12 }),
      Action.Direction:new(-7, 1544, -46, { instance = true, distance = 12 }),
    },
    postconditions = {

      Condition.ModelVisible:new(openDoor),
    },
  },
  {
    text = "Inspect the bust by the fireplace.",
    actions = {
      Action.Direction:new(-9, 4128, -53, { instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(stairscase) },
  },
  {
    text = "Descend the stairs.",
    actions = { Action.ModelHighlight:new(stairscase), Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ModelVisible:new(torch) },
  },
  {
    text = "Run past the crassians and collect the valve outer wheel, valve inner wheel, and pitch.",
    title = "Smuggler's cavern",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(40, 816, -6, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(valveInnerWheel, { distance = 12 }),
    },
    postconditions = { Condition.InventoryContains:new(valveInnerWheel) },
  },
  {
    actions = {
      Action.ModelHighlight:new(pitch),
    },
    postconditions = { Condition.InventoryContains:new(pitch) },
  },
  {
    actions = { Action.ModelHighlight:new(valveOuterWheel) },
    postconditions = { Condition.InventoryContains:new(valveOuterWheel) },
  },
  {
    text = "Repair the leaking pipe on the ground.",
    actions = {
      Action.Direction:new(42, 816, -8, { instance = true }),
      -- This model is a bit too large imo. The direction should be preferable here.
      -- Action.ModelHighlight:new(brokenPipe)
    },
    postconditions = { Condition.ModelNotVisible:new(brokenPipe) },
  },
  {
    text = "Combine valve parts and use it on socket to repair the central barrel.",
    actions = { Action.InventoryHighlight:new(valveInnerWheel), Action.InventoryHighlight:new(valveOuterWheel) },
    postconditions = { Condition.InventoryContains:new(valveWheel) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(valveWheel),
      Action.Direction:new(37, 816, -6, { instance = true }),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(valveWheel),
    },
  },
  {
    text = "Turn the valves on the filled barrel to move the water through the pipes to the door.",
    actions = {
      Action.Direction:new(39, 816, -4, { instance = true }),
    },
    postconditions = {
      -- This does work, have this "just in case" check for openDoor2 in case they leave and the coordinates get fucky
      Condition.ModelVisible:new(waterPuzzleLocation, { atLocation = Location:new(35, 816, -4), instance = true }),
      Condition.ModelVisible:new(openDoor2),
    },
  },
  {
    actions = {
      Action.Direction:new(34, 816, -4, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(waterPuzzleLocation, { atLocation = Location:new(37, 816, -6), instance = true }),
      Condition.ModelVisible:new(openDoor2),
    },
  },
  {
    actions = {
      Action.Direction:new(37, 816, -7, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(waterPuzzleLocation, { atLocation = Location:new(34, 816, -8), instance = true }),
      Condition.ModelVisible:new(openDoor2),
    },
  },
  {
    actions = {
      Action.Direction:new(34, 816, -8, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(waterPuzzleLocation, { atLocation = Location:new(39, 816, -8), instance = true }),
      Condition.ModelVisible:new(openDoor2),
    },
  },
  {
    actions = {
      Action.Direction:new(39, 816, -9, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(openDoor2),
    },
  },
  {
    text = "Go through and kill all scouts to open the barrier.",
    actions = { Action.ModelHighlight:new(scout, { highlightPriority = "closest" }) },
    postconditions = { Condition.ModelNotVisible:new(scout) },
  },
  {
    text = "Pull the lever to the south when the Crassian Warrior isn't looking. If seen, the route will reset. Repeat this for each Crassian Warrior and run past the scouts.",
    actions = { Action.ModelHighlight:new(lever) },
    postconditions = {
      -- This distanceTo can false positive if the player leaves part way through and rejoins
      Condition.DistanceTo:new(36, 2024, 54, 40),
      -- This big barrier false positives a lot.
      --Condition.ModelVisible:new(bigbarrier)
      -- Adding in this as a fallback
      Condition.ModelVisible:new(tentacle),
    },
  },
  {
    text = "Climb on the ship.",
    title = "Release the Kraken",
    -- I think highlighting the gangplank is wrong actually.
    postconditions = { Condition.ModelVisible:new(tentacle) },
  },
  {
    text = "Kill the 4 tentacles, moving to avoid their attacks.",
    actions = { Action.ModelHighlight:new(tentacle, { highlightPriority = "all" }) },
    postconditions = { Condition.ModelNotVisible:new(tentacle) },
  },
  {
    text = "Kill Agoroth, ideally from a distance.",
    postconditions = { Condition.ConversationText:new("You did it -") },
  },
  {
    text = "Talk to Lucy.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("Hello there, laddie") },
  },
  {
    text = "Return to Gudrik and ask for a reward. This requires 7 free inventory spaces.",
    actions = {
      Action.ConversationHighlight:new("Ask about A Shadow over Ashdale"),
      Action.ConversationHighlight:new("Is there a reward?"),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "A Shadow over Ashdale",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1398124800,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
