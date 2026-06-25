local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--NPCs
local almera = Model.new(3579, {
  [1791] = Vertex.new(24, 738, -38, 30, 29, 28),
  [1815] = Vertex.new(-24, 738, -38, 30, 29, 28),
  [1837] = Vertex.new(-2, 716, -57, 85, 60, 26),
  [1843] = Vertex.new(2, 716, -57, 85, 60, 26),
  [1847] = Vertex.new(6, 716, -52, 85, 60, 26),
})
local hudon = Model.new(2928, {
  [719] = Vertex.new(-20, 512, -68, 66, 48, 27),
  [743] = Vertex.new(-20, 512, -68, 66, 48, 27),
  [851] = Vertex.new(-20, 512, -68, 66, 48, 27),
  [1923] = Vertex.new(220, 288, -4, 139, 110, 88),
  [1974] = Vertex.new(-220, 288, -4, 139, 110, 88),
})
local glorie = Model.new(4443, {
  [354] = Vertex.new(-2, 533, -36, 133, 79, 27),
  [4168] = Vertex.new(0, 440, -10, 14, 166, 41),
  [4169] = Vertex.new(2, 440, -12, 14, 166, 41),
  [4170] = Vertex.new(-2, 440, -12, 14, 166, 41),
})

--Objects
local waterfallCaveEntrance = Model.new(279, {
  [231] = Vertex.new(256, 672, 156, 58, 48, 30),
  [247] = Vertex.new(256, 800, 256, 58, 48, 30),
  [252] = Vertex.new(-256, 800, 256, 58, 48, 30),
  [253] = Vertex.new(-256, 800, 256, 58, 48, 30),
  [274] = Vertex.new(-256, 680, 160, 58, 48, 30),
})
local glarialsStatue = Model.new(6246, {
  [447] = Vertex.new(67, 853, 90, 127, 127, 127),
  [462] = Vertex.new(66, 852, 90, 127, 127, 127),
  [3903] = Vertex.new(68, 843, 87, 127, 127, 127),
  [5217] = Vertex.new(68, 844, 87, 127, 127, 127),
  [5904] = Vertex.new(72, 825, 92, 127, 127, 127),
})

--Items
--Quest Items
local bookOnBaxtorian = Model.new(138, {
  [1] = Vertex.new(-56, 12, -72, 147, 147, 134),
  [2] = Vertex.new(-56, 36, -72, 147, 147, 134),
  [3] = Vertex.new(56, 36, -72, 147, 147, 134),
  [6] = Vertex.new(56, 12, -72, 147, 147, 134),
  [20] = Vertex.new(64, 36, -84, 51, 62, 5),
})
local dungeonKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 153, 121, 13),
  [2] = Vertex.new(-12, 16, -84, 153, 121, 13),
  [3] = Vertex.new(-20, 16, -92, 153, 121, 13),
  [6] = Vertex.new(-60, 16, -52, 153, 121, 13),
  [7] = Vertex.new(-32, 16, -48, 153, 121, 13),
})
local glarialsUrn = Model.new(408, {
  [1] = Vertex.new(-28, 48, 28, 114, 134, 149),
  [2] = Vertex.new(-20, 0, 20, 114, 134, 149),
  [3] = Vertex.new(0, 0, 28, 114, 134, 149),
  [4] = Vertex.new(-28, 0, 0, 114, 134, 149),
  [9] = Vertex.new(0, 48, 40, 114, 134, 149),
})
local waterfallKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 153, 121, 13),
  [2] = Vertex.new(-12, 16, -84, 153, 121, 13),
  [3] = Vertex.new(-20, 16, -92, 153, 121, 13),
  [6] = Vertex.new(-60, 16, -52, 153, 121, 13),
  [7] = Vertex.new(-32, 16, -48, 153, 121, 13),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Almera inside her house, south of the Barbarian Outpost.<ul><li>Optional: If you have 24 Construction, bring 5 coils of rope and 3 oak planks to build a rope rack next to the log raft.</li></ul>",
    title = "Getting started",
    actions = { Action.Direction:new(2521, 7045, 3496) },
    postconditions = { Condition.DistanceTo:new(2521, 7045, 3496, 8) },
  },
  { actions = { Action.ModelHighlight:new(almera) }, postconditions = { Condition.QuestInterfaceOpen:new() } },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue speaking to Almera.",
    actions = { Action.ModelHighlight:new(almera) },
    postconditions = { Condition.ConversationText:new("Would you? You are kind.") },
  },
  {
    text = "Board the raft to the west.",
    title = "The Treasure",
    actions = { Action.Direction:new(2509, 6541, 3494) },
    postconditions = { Condition.DistanceTo:new(2512, 5947, 3481, 1) },
  },
  {
    text = "Talk to Hudon, north of where you land.<ul><li>If you do not talk to Hudon you will not be able to find the Book on Baxtorian in a later step.</li></ul>",
    actions = { Action.ModelHighlight:new(hudon) },
    postconditions = { Condition.ConversationText:new("I'm fine alone.") },
  },
  {
    text = "Click swim on the river to the south to end up back on shore.",
    actions = { Action.Direction:new(2512, 5051, 3476) },
    postconditions = { Condition.DistanceTo:new(2527, 117, 3413, 8) },
  },
  {
    text = "Once back on shore, head north-west to the house south of Almera's house, and climb the outside staircase on the south side of the larger building.",
    title = "Glarial's Pebble",
    actions = { Action.Direction:new(2517.5, 1317, 3430) },
    postconditions = { Condition.DistanceToWithHeight:new(2518, 2277, 3431, 4) },
  },
  {
    text = "Search the south-eastern bookcases on the east wall to find the Book on Baxtorian.",
    actions = { Action.Direction:new(2520, 2277, 3427) },
    postconditions = { Condition.InventoryContains:new(bookOnBaxtorian) },
  },
  { --Not sure how to detect reading the book, so combining with next step
    text = "<b>Read the book in full or you cannot progress later.</b> Head to the Tree Gnome Village, located north-west of Yanille.",
    actions = {
      Action.InventoryHighlight:new(bookOnBaxtorian),
      Action.Direction:new(2505, 965, 3191),
    },
    postconditions = { Condition.DistanceTo:new(2505, 965, 3191, 8) },
  },
  {
    text = "Once there, follow the path through the maze, and climb-down the ladder.<ul><li>If the quest Tree Gnome Village is partially completed, follow Elkoy, who is at the beginning and end of the maze.</li><li>If the quest is not partially completed, it is recommended to squeeze through the loose railing and speak to King Bolren while nearby.</li><li>Go east and climb-down the ladder.</li></ul>",
    actions = { Action.Direction:new(2522, 957, 3184) },
    postconditions = {
      Condition.DistanceTo:new(2522, 957, 3184, 1),
      Condition.DistanceTo:new(2533, 965, 3156, 1),
    },
  },
  {
    -- text = "debug 1",
    actions = { Action.Direction:new(2539, 965, 3177) },
    postconditions = {
      Condition.DistanceTo:new(2539, 965, 3177, 1),
      Condition.DistanceTo:new(2533, 965, 3156, 1),
    },
  },
  {
    -- text = "debug 2",
    actions = { Action.Direction:new(2550, 965, 3157) },
    postconditions = {
      Condition.DistanceTo:new(2550, 965, 3157, 1),
      Condition.DistanceTo:new(2533, 965, 3156, 1),
    },
  },
  {
    -- text = "debug 3",
    actions = { Action.Direction:new(2533, 965, 3156) },
    postconditions = { Condition.DistanceTo:new(2533, 4, 9556, 2) },
  },
  {
    text = "Inside the dungeon, go to the eastern room.",
    actions = { Action.Direction:new(2543, 1157, 9560) },
    postconditions = { Condition.DistanceTo:new(2543, 1157, 9560, 4) },
  },
  {
    text = "Search the east stack of crates to find a key.<ul><li>You must read book in order to find key.</li></ul>",
    actions = { Action.Direction:new(2548, 805, 9565) },
    postconditions = {
      Condition.InventoryContains:new(dungeonKey),
      Condition.ChatText:new("and find a large key."),
    },
  },
  {
    text = "Head to the westernmost room and use the key on the gate to unlock it.",
    actions = { Action.Direction:new(2515, 1069, 9563) },
    postconditions = { Condition.DistanceTo:new(2515, 1069, 9563, 6) },
  },
  {
    actions = {
      Action.Direction:new(2515, 1125, 9575.5),
      Action.InventoryHighlight:new(dungeonKey),
    },
    postconditions = { Condition.ChatText:new("You open the gate and walk through.") },
  },
  {
    text = "Speak to Golrie, then wait for your character to find Glarial's pebble.",
    actions = { Action.ModelHighlight:new(glorie) },
    postconditions = { Condition.ConversationText:new("OK... Take care Golrie.") },
  },
  {
    text = "Bank everything except Glarial's pebble and your rope (you may bring food and allowed items).<br><br>Once you're ready, manually move to the next step.",
    title = "Tomb Raiding",
    neededItems = {
      ["Glarial's pebble"] = { quantity = 1, model = Models.items["glarial's pebble"] },
      ["Rope (unless you built the rope rack)"] = { quantity = 1, model = Models.items["rope"] },
      ["Air rune"] = { quantity = 6 },
      ["Water rune"] = { quantity = 6 },
      ["Earth rune"] = { quantity = 6 },
      ["Highest healing food"] = { quantity = 24 },
    },
  },
  {
    text = "Head to Glarial's tomb, north west of the Ardougne lodestone.<ul><li>You may drop 6 air runes, 6 water runes and 6 earth runes outside the tomb and retrieve them upon exiting, in order to skip banking for the final part.</li></ul>",
    actions = { Action.Direction:new(2557.5, 2741, 3444.5) },
    postconditions = { Condition.DistanceTo:new(2557.5, 2741, 3444.5, 6) },
  },
  {
    text = "Drop your runes.",
    actions = { Action.InventoryHighlight:new(Models.items["air rune"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["air rune"]) },
  },
  {
    actions = { Action.InventoryHighlight:new(Models.items["earth rune"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["earth rune"]) },
  },
  {
    actions = { Action.InventoryHighlight:new(Models.items["water rune"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["water rune"]) },
  },
  {
    text = "Use Glarial's pebble on the tombstone.<ul><li>If it says \"It fits perfectly, but nothing happens.\", it's because you are wearing disallowed items or carrying runes.</li><li>Don't forget your runes on the ground if you bank.</li></ul>",
    actions = { Action.InventoryHighlight:new(Models.items["glarial's pebble"]) },
    postconditions = { Condition.DistanceTo:new(2557, 853, 9844, 1) },
  },
  {
    text = "Go west and search the chest to obtain Glarial's amulet.<ul><li>Optional: Drop the amulet and search the chest for a second one, preventing having to obtain a second one after the quest for dungeon access.</li></ul>",
    actions = { Action.Direction:new(2530, 653, 9844) },
    postconditions = { Condition.InventoryContains:new(Models.items["glarial's amulet"]) },
  },
  {
    text = "Go south and search Glarial's Tomb to obtain Glarial's urn.",
    actions = { Action.Direction:new(2542, 1277, 9812) },
    postconditions = { Condition.InventoryContains:new(glarialsUrn) },
  },
  {
    text = "Head north-east back up the ladder you came down.",
    actions = { Action.Direction:new(2557, 853, 9844) },
    postconditions = { Condition.DistanceTo:new(2557.5, 2741, 3444.5, 5) },
  },
  {
    text = "<b>Pick up your items if you dropped them.</b> Otherwise, bank to get your runes and rope. Once you're ready, manually move to the next step.",
    title = "Baxtorian Treasures",
    neededItems = {
      ["Air rune"] = { quantity = 6 },
      ["Water rune"] = { quantity = 6 },
      ["Earth rune"] = { quantity = 6 },
      ["Rope (unless you built the rope rack)"] = { quantity = 1 },
      ["Glarial's urn"] = { quantity = 1, model = glarialsUrn },
      ["Glarial's amulet"] = { quantity = 1, model = Models.items["glarial's amulet"] },
    },
    recommendedItems = {
      ["Games necklace"] = { quantity = 1 },
    },
    actions = { Action.ModelHighlight:new(Models.items["air rune"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["air rune"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.items["earth rune"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["earth rune"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.items["water rune"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["water rune"]) },
  },
  {
    text = "Board the raft next to Almera's house.",
    title = "Waterfall dungeon",
    neededItems = {
      ["Rope"] = { quantity = 1, model = Models.items["rope"], duringQuest = true },
      ["Glarial's amulet"] = { quantity = 1, model = Models.items["glarial's amulet"] },
    },
    actions = { Action.Direction:new(2509, 6741, 3493.5) },
    postconditions = { Condition.DistanceTo:new(2512, 5947, 3481, 1) },
  },
  {
    text = "Use a rope on the rock.",
    warning = "Do not click 'swim to rock'.",
    actions = {
      Action.InventoryHighlight:new(Models.items["rope"]),
      Action.Direction:new(2512, 4837, 3468),
    },
    postconditions = { Condition.DistanceTo:new(2513, 4837, 3468, 1) },
  },
  {
    text = "Use a rope on the dead tree.",
    warning = "Do not click 'climb tree'.",
    actions = {
      Action.InventoryHighlight:new(Models.items["rope"]),
      Action.Direction:new(2512, 5501, 3465),
    },
    postconditions = { Condition.DistanceTo:new(2511, 3269, 3463, 0) },
  },
  {
    text = "Enter the cave door.",
    actions = { Action.ModelHighlight:new(waterfallCaveEntrance) },
    postconditions = { Condition.DistanceTo:new(2575, 749, 9861, 2) },
  },
  {
    text = "Search the crates.",
    actions = { Action.Direction:new(2589, 1245, 9888) },
    postconditions = {
      Condition.ChatText:new("and find a large key"),
      Condition.InventoryContains:new(waterfallKey),
    },
  },
  {
    text = "Open the door next to the fire giants.",
    actions = { Action.Direction:new(2568, 1245, 9893.5) },
    postconditions = { Condition.DistanceTo:new(2568, 645, 9895, 1) },
  },
  {
    text = "Open the door.",
    warning = "You can fail the next steps.",
    actions = { Action.Direction:new(2566, 1281, 9901.5) },
    postconditions = { Condition.DistanceTo:new(2566, 581, 9902, 0) },
  },
  -- {
  --   text = "Go back to Almera's house, south of the Barbarian Outpost.<ul><li>Grab a rope from the rope rack if you have it built.</li></ul>",
  --   actions = { Action.Direction:new(2521, 7045, 3496) },
  --   postconditions = { Condition.DistanceTo:new(2521, 7045, 3496, 8) },
  -- },
  -- {
  --   text = "Board the raft to the west.",
  --   actions = { Action.Direction:new(2509, 6541, 3494) },
  --   postconditions = { Condition.DistanceTo:new(2512, 5947, 3481, 1) },
  -- },
  -- {
  --   text = "<i>Use your rope on the rock</i> situated on the bank to the south. <b>Do not click 'swim to rock'</b>.",
  --   actions = {
  --     Action.InventoryHighlight:new(Models.items["rope"]),
  --     Action.Direction:new(2512, 4837, 3468),
  --   },
  --   postconditions = { Condition.DistanceTo:new(2513, 4837, 3468, 1) },
  -- },
  -- {
  --   text = "<i>Use your rope on the dead tree</i> next to you. <b>Do not click 'climb tree'</b>.",
  --   actions = {
  --     Action.InventoryHighlight:new(Models.items["rope"]),
  --     Action.Direction:new(2512, 4901, 3465),
  --   },
  --   postconditions = { Condition.DistanceTo:new(2511, 3269, 3463, 0) },
  -- },
  -- {
  --   text = "With Glarial's amulet in your inventory, enter the cave door north.",
  --   actions = { Action.ModelHighlight:new(waterfallCaveEntrance) },
  --   postconditions = { Condition.DistanceTo:new(2575, 749, 9861, 2) },
  -- },
  -- {
  --   text = "Go north-east, open the door, and search the light-colored crates on the northern wall for another key.",
  --   actions = { Action.Direction:new(2589, 645, 9888) },
  --   postconditions = {
  --     Condition.ChatText:new("and find a large key"),
  --     Condition.InventoryContains:new(waterfallKey),
  --   },
  -- },
  -- {
  --   text = "Return to the centre room and go down the western passage, past the fire giants, through the doors north.",
  --   actions = { Action.Direction:new(2575, 645, 9875) },
  --   postconditions = { Condition.DistanceTo:new(2575, 645, 9875, 4) },
  -- },
  -- {
  --   actions = { Action.Direction:new(2568, 645, 9893.5) },
  --   postconditions = { Condition.DistanceTo:new(2568, 645, 9894, 0) },
  -- },
  -- {
  --   text = "Use the key on the last door. <b>Make sure you follow the next steps carefully or you will be washed up down stream.</b>",
  --   actions = {
  --     Action.Direction:new(2566, 581, 9901.5),
  --     Action.InventoryHighlight:new(waterfallKey),
  --   },
  --   postconditions = { Condition.DistanceTo:new(2566, 581, 9902, 0) },
  -- },
  {
    text = "Use one air rune, one water rune, and one earth rune on each of the six pillars.",
    actions = {
      Action.InventoryHighlight:new(Models.items["air rune"]),
      Action.InventoryHighlight:new(Models.items["earth rune"]),
      Action.InventoryHighlight:new(Models.items["water rune"]),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["air rune"]) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(Models.items["air rune"]),
      Action.InventoryHighlight:new(Models.items["earth rune"]),
      Action.InventoryHighlight:new(Models.items["water rune"]),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["earth rune"]) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(Models.items["air rune"]),
      Action.InventoryHighlight:new(Models.items["earth rune"]),
      Action.InventoryHighlight:new(Models.items["water rune"]),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["water rune"]) },
  },
  {
    text = "Use Glarial's amulet on the statue of Glarial, the western statue.",
    actions = {
      Action.ModelHighlight:new(glarialsStatue),
      Action.InventoryHighlight:new(Models.items["glarial's amulet"]),
    },
    postconditions = { Condition.DistanceTo:new(2603, 645, 9914, 2) },
  },

  {
    text = "Use Glarial's urn on the Chalice of Eternity, in the centre of the room.",
    actions = { Action.InventoryHighlight:new(glarialsUrn), Action.Direction:new(2603.5, 1157, 9910.5) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Waterfall Quest",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1032825600,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Air rune"] = { quantity = 6, model = Models.items["air rune"] },
    ["Water rune"] = { quantity = 6, model = Models.items["water rune"] },
    ["Earth rune"] = { quantity = 6, model = Models.items["earth rune"] },
    ["Rope"] = { quantity = 1, model = Models.items["rope"] },
  },
  recommendedItems = { ["Games necklace"] = { quantity = 1, model = Models.items["games necklace"] } },
  combatNPCs = {},
})
