local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local charlie = Model.new(3309, {
  [2386] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2391] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2394] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2396] = Vertex.new(7, 724, -51, 106, 78, 54),
  [2653] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local captainErrdo = Model.new(3975, {
  [2276] = Vertex.new(-40, 473, -41, 131, 120, 119),
  [2285] = Vertex.new(-20, 473, -53, 131, 120, 119),
  [2289] = Vertex.new(40, 473, -41, 131, 120, 119),
  [2298] = Vertex.new(20, 473, -53, 131, 120, 119),
  [2300] = Vertex.new(2, 440, -12, 14, 166, 40),
})
local shipyardWorker = Model.new(3855, {
  [2329] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2334] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2337] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2339] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3367] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local foreman = Model.new(3759, {
  [1924] = Vertex.new(2, 725, -59, 106, 78, 54),
  [1928] = Vertex.new(7, 724, -51, 106, 78, 54),
  [1942] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [1947] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [3515] = Vertex.new(40, 711, -12, 104, 101, 95),
})
local femi = Model.new(4134, {
  [212] = Vertex.new(-96, 216, -332, 115, 116, 106),
  [2107] = Vertex.new(-1, 437, -12, 15, 182, 44),
  [2108] = Vertex.new(0, 437, -10, 15, 182, 44),
  [2109] = Vertex.new(1, 437, -12, 15, 182, 44),
  [2111] = Vertex.new(-1, 437, -12, 15, 182, 44),
})
local anita = Model.new(4077, {
  [3752] = Vertex.new(34, 517, 64, 127, 103, 135),
  [3772] = Vertex.new(-34, 517, 64, 127, 103, 135),
  [3798] = Vertex.new(44, 468, 2, 105, 86, 112),
  [3925] = Vertex.new(-44, 468, 2, 105, 86, 112),
  [3928] = Vertex.new(-44, 468, 2, 105, 86, 112),
})
local blackDemon = Model.new(5583, {
  [5442] = Vertex.new(964, 1534, 120, 76, 70, 69),
  [5444] = Vertex.new(964, 1534, 120, 76, 70, 69),
  [5460] = Vertex.new(-964, 1534, 120, 76, 70, 69),
  [5490] = Vertex.new(875, 1528, 120, 131, 120, 119),
  [5504] = Vertex.new(-875, 1528, 120, 131, 120, 119),
})

-- Objects
local gloughGroundLadder = Model.new(228, {
  [182] = Vertex.new(6415, 2242, 3993, 65, 48, 26),
  [185] = Vertex.new(6399, 2260, 3993, 65, 48, 26),
  [188] = Vertex.new(6399, 2260, 3993, 65, 48, 26),
  [191] = Vertex.new(6383, 2242, 3993, 65, 48, 26),
  [192] = Vertex.new(6399, 2260, 3993, 65, 48, 26),
})
local gloughsChest = Model.new(1491, {
  [60] = Vertex.new(1302, 2500, 3476, 115, 96, 73),
  [63] = Vertex.new(1302, 2500, 3476, 115, 96, 73),
  [1251] = Vertex.new(1291, 2516, 3439, 91, 71, 47),
  [1266] = Vertex.new(1262, 2516, 3439, 91, 71, 47),
  [1445] = Vertex.new(1277, 2486, 3434, 68, 53, 35),
})
local gloughFirstFloorLadder = Model.new(36, {
  [5] = Vertex.new(2173, 2282, 3712, 61, 48, 24),
  [9] = Vertex.new(2487, 3606, 3968, 61, 48, 24),
  [17] = Vertex.new(2226, 2278, 3968, 61, 48, 24),
  [21] = Vertex.new(2536, 3594, 3712, 61, 48, 24),
})

-- Items
-- Quest Items
local hazelmeresScroll = Model.new(276, {
  [1] = Vertex.new(-28, 0, -72, 146, 146, 134),
  [2] = Vertex.new(68, 0, -60, 146, 146, 134),
  [3] = Vertex.new(20, 0, -72, 146, 146, 134),
  [5] = Vertex.new(-72, 0, -56, 146, 146, 134),
  [7] = Vertex.new(-60, 0, -4, 146, 146, 134),
})
local gloughsJournal = Model.new(282, {
  [1] = Vertex.new(36, 36, -4, 134, 150, 114),
  [2] = Vertex.new(56, 36, -72, 134, 150, 114),
  [3] = Vertex.new(-32, 36, -52, 134, 150, 114),
  [6] = Vertex.new(0, 36, 52, 134, 150, 114),
  [7] = Vertex.new(-56, 36, 84, 36, 47, 29),
})
local lumberOrder = Model.new(276, {
  [1] = Vertex.new(-28, 0, -72, 146, 146, 134),
  [2] = Vertex.new(68, 0, -60, 146, 146, 134),
  [3] = Vertex.new(20, 0, -72, 146, 146, 134),
  [5] = Vertex.new(-72, 0, -56, 146, 146, 134),
  [7] = Vertex.new(-60, 0, -4, 146, 146, 134),
})
local gloughsKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 152, 120, 13),
  [2] = Vertex.new(-12, 16, -84, 152, 120, 13),
  [3] = Vertex.new(-20, 16, -92, 152, 120, 13),
  [6] = Vertex.new(-60, 16, -52, 152, 120, 13),
  [7] = Vertex.new(-32, 16, -48, 152, 120, 13),
})
local twigT = Model.new(174, {
  [1] = Vertex.new(4, 0, 52, 46, 36, 4),
  [2] = Vertex.new(-4, 0, 64, 46, 36, 4),
  [3] = Vertex.new(-20, 16, 52, 46, 36, 4),
  [6] = Vertex.new(-4, 16, 40, 46, 36, 4),
  [7] = Vertex.new(-4, 32, 64, 46, 36, 4),
})
local twigU = Model.new(318, {
  [1] = Vertex.new(32, 0, -116, 46, 36, 4),
  [2] = Vertex.new(24, 0, -104, 46, 36, 4),
  [3] = Vertex.new(12, 16, -112, 46, 36, 4),
  [6] = Vertex.new(20, 16, -120, 46, 36, 4),
  [7] = Vertex.new(24, 32, -104, 46, 36, 4),
})
local twigZ = Model.new(318, {
  [1] = Vertex.new(-60, 0, -96, 46, 36, 4),
  [2] = Vertex.new(-68, 0, -80, 46, 36, 4),
  [3] = Vertex.new(-84, 16, -88, 46, 36, 4),
  [6] = Vertex.new(-76, 16, -104, 46, 36, 4),
  [7] = Vertex.new(-68, 32, -80, 46, 36, 4),
})
local twigO = Model.new(576, {
  [1] = Vertex.new(108, 0, 60, 46, 36, 4),
  [2] = Vertex.new(128, 0, 72, 46, 36, 4),
  [3] = Vertex.new(120, 16, 88, 46, 36, 4),
  [6] = Vertex.new(104, 16, 76, 46, 36, 4),
  [7] = Vertex.new(128, 32, 72, 46, 36, 4),
})
local twigTPlaced = Model.new(174, {
  [87] = Vertex.new(100, 16, 88, 72, 59, 37),
  [89] = Vertex.new(100, 16, 88, 72, 59, 37),
  [96] = Vertex.new(100, 16, 88, 72, 59, 37),
  [104] = Vertex.new(116, 16, 64, 72, 59, 37),
  [134] = Vertex.new(32, 16, 152, 72, 59, 37),
})
local twigUPlaced = Model.new(318, {
  [141] = Vertex.new(96, 16, 120, 72, 59, 37),
  [158] = Vertex.new(120, 16, 112, 72, 59, 37),
  [161] = Vertex.new(120, 16, 112, 72, 59, 37),
  [163] = Vertex.new(120, 16, 112, 72, 59, 37),
  [261] = Vertex.new(-80, 16, 144, 72, 59, 37),
})
local twigZPlaced = Model.new(318, {
  [187] = Vertex.new(-72, 32, -136, 72, 59, 37),
  [201] = Vertex.new(-96, 32, 124, 72, 59, 37),
  [203] = Vertex.new(-96, 32, 124, 72, 59, 37),
  [210] = Vertex.new(-96, 32, 124, 72, 59, 37),
  [219] = Vertex.new(-96, 0, 124, 72, 59, 37),
})
local twigOPlaced = Model.new(576, {
  [237] = Vertex.new(144, 32, 112, 72, 59, 37),
  [240] = Vertex.new(144, 32, 112, 72, 59, 37),
  [243] = Vertex.new(144, 32, 112, 72, 59, 37),
  [327] = Vertex.new(-120, 32, -116, 72, 59, 37),
  [507] = Vertex.new(100, 32, -152, 72, 59, 37),
})
local daconiaRock = Model.new(84, {
  [1] = Vertex.new(-60, 36, -148, 36, 41, 57),
  [2] = Vertex.new(-108, 0, -84, 36, 41, 57),
  [3] = Vertex.new(-152, 36, -108, 36, 41, 57),
  [6] = Vertex.new(-76, 0, 40, 36, 41, 57),
  [7] = Vertex.new(-48, 152, -76, 36, 41, 57),
})

---@type QuestStep[]
local steps = {
  --#region Getting started
  {
    text = "Speak to King Narnode Shareen on the ground floor (1st floor[US]) of the Grand Tree.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(2465.5, 3205, 3495.5) },
    postconditions = { Condition.DistanceTo:new(2465, 3205, 3495, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["king narnode shareen"]),
      Action.ConversationHighlight:new("You seem worried, what's up?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with King Narnode Shareen.",
    actions = { Action.ModelHighlight:new(NPCs["king narnode shareen"]) },
    postconditions = { Condition.DistanceTo:new(2464, 3205, 3497, 3) },
  },
  --#endregion
  --#region The crisis
  {
    text = "Take the bark sample and book to Hazelmere; he lives east of Yanille.<ul><li>If you have A Fairy Tale II - Cure a Queen completed, you can use code CLS, otherwise teleport to the Yanille lodestone.</li></ul>",
    title = "The crisis",
    actions = { Action.Direction:new(2633, 869, 3087) },
    postconditions = {
      Condition.DistanceTo:new(2633, 869, 3087, 8),
      Condition.DistanceTo:new(2677, 3725, 3086, 12), -- Fairy ring handling
    },
  },
  {
    actions = { Action.Direction:new(2677, 2501, 3088) },
    postconditions = { Condition.ModelVisible:new(Objects["hazelmere ladder"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["hazelmere ladder"]) },
    postconditions = { Condition.DistanceToWithHeight:new(2677, 3725, 3086, 6) },
  },
  {
    text = "Talk to Hazelmere.",
    actions = { Action.ModelHighlight:new(NPCs["hazelmere"]) },
    postconditions = {
      Condition.ConversationText:new("Hazelmere has given you the scroll."),
      Condition.InventoryContains:new(hazelmeresScroll),
    },
  },
  {
    text = "Take Hazelmere's scroll back to the King.",
    actions = { Action.Direction:new(2465.5, 3205, 3495.5) },
    postconditions = { Condition.DistanceTo:new(2465, 3205, 3495, 4) },
  },
  {
    text = "Talk to the King.",
    actions = {
      Action.ModelHighlight:new(NPCs["king narnode shareen"]),
      Action.ConversationHighlight:new("I think so!"),
      Action.ConversationHighlight:new("None of the above."),
      Action.ConversationHighlight:new("None of the above."),
      Action.ConversationHighlight:new("A man came to me with the King's seal."),
      Action.ConversationHighlight:new("I gave the man Daconia rocks."),
      Action.ConversationHighlight:new("And Daconia rocks will kill the tree!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "If he's not there he will be at his girlfriend Anita's place. Meet me back here once you've told him."
      ),
      Condition.ConversationText:new("OK! I'll be back soon."),
    },
  },
  --#endregion
  --#region Glough
  {
    text = "Climb up the ladder of the tree house south-east of the Grand Tree, north of the Agility course.",
    title = "Glough",
    actions = { Action.Direction:new(2476, 1317, 3463) },
    postconditions = { Condition.DistanceTo:new(2476, 1317, 3463, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(gloughGroundLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(2477, 2317, 3463, 6) },
  },
  {
    text = "Speak to Glough.",
    actions = { Action.ModelHighlight:new(NPCs["glough"]) },
    postconditions = {
      Condition.ConversationText:new("Your type can't be trusted! I'll take care of this! Go back to the King."),
    },
  },
  {
    text = "Speak to the King.",
    actions = { Action.Direction:new(2465.5, 3205, 3495.5) },
    postconditions = { Condition.DistanceTo:new(2465, 3205, 3495, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["king narnode shareen"]) },
    postconditions = {
      Condition.ConversationText:new("Certainly. He's on the top level of the tree. Be careful, it's a long way down!"),
    },
  },
  {
    text = "Climb to the top floor of the Grand Tree.",
    actions = { Action.ModelHighlight:new(Objects["grand tree ground floor ladder"]) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2466, 4165, 3494, 8),
      Condition.DistanceToWithHeight:new(2466, 6125, 3494, 8),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["grand tree first floor ladder"]) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2465, 5125, 3495, 8),
      Condition.DistanceToWithHeight:new(2466, 6125, 3494, 8),
    },
  },
  {
    actions = { Action.Direction:new(2466, 5125, 3495) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2466, 6125, 3494, 8),
      Condition.DistanceToWithHeight:new(2466, 6125, 3494, 8),
    },
  },
  {
    text = "Talk to Charlie the prisoner.",
    actions = { Action.ModelHighlight:new(charlie) },
    postconditions = { Condition.ConversationText:new("Good luck!") },
  },
  {
    text = "Return to Glough's tree house.",
    actions = { Action.Direction:new(2476, 1317, 3463) },
    postconditions = { Condition.DistanceTo:new(2476, 1317, 3463, 8) },
  },
  {
    text = "Search the cupboard north of the ladder to get Glough's journal.",
    actions = { Action.Direction:new(2476.5, 2317, 3465) },
    postconditions = { Condition.InventoryContains:new(gloughsJournal) },
  },
  {
    text = "Speak to Glough.",
    actions = { Action.ModelHighlight:new(NPCs["glough"]) },
    postconditions = { Condition.ModelVisible:new(charlie) },
  },
  {
    text = "Speak to Charlie and leave your prison cell.",
    actions = { Action.ModelHighlight:new(charlie) },
    postconditions = { Condition.ModelVisible:new(NPCs["king narnode shareen"]) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["king narnode shareen"]) },
    postconditions = { Condition.ConversationText:new("I'm sorry again Traveller!") },
  },
  {
    text = "Talk to the Captain Errdo just north of the cells to get to Karamja.<ul><li>Beware if you are low level, there are multiple level 57 Jogres here so be ready.</li></ul>",
    actions = {
      Action.ModelHighlight:new(captainErrdo),
      Action.ConversationHighlight:new("Take me to Karamja, please."),
    },
    postconditions = { Condition.ConversationText:new("Okay, you're the boss! Hold on tight, it'll be a rough ride.") },
  },
  {
    text = "Run east and attempt to open the gate.",
    actions = { Action.Direction:new(2944.5, 581, 3041.5) },
    postconditions = {
      Condition.ConversationText:new("Hey you! What are you up to?"),
      Condition.ConversationText:new("I'm trying to open the gate!"),
    },
  },
  {
    text = "Tell him you work for Glough, Ka-Lu-Min is the password.",
    actions = {
      Action.ModelHighlight:new(shipyardWorker),
      Action.ConversationHighlight:new("Glough sent me."),
      Action.ConversationHighlight:new("Ka."),
      Action.ConversationHighlight:new("Lu."),
      Action.ConversationHighlight:new("Min."),
    },
    postconditions = { Condition.DistanceTo:new(2947, 597, 3041, 2) },
  },
  {
    text = "Once inside, run east to the south dock.",
    actions = { Action.Direction:new(3001, 965, 3041) },
    postconditions = { Condition.ModelVisible:new(foreman) },
  },
  {
    text = "Either kill the foreman, or talk to him and answer his questions.",
    actions = {
      Action.ModelHighlight:new(foreman),
      Action.ConversationHighlight:new("Sadly his wife is no longer with us!"),
      Action.ConversationHighlight:new("He loves worm holes."),
      Action.ConversationHighlight:new("Anita."),
    },
    postconditions = {
      Condition.ConversationText:new("OK. I'll head off and give this order to Glough."),
      Condition.ModelVisible:new(lumberOrder),
    },
  },
  --#endregion
  --#region Breaking and entering
  {
    text = "Talk to Femi at the Gnome Stronghold gates to enter inside.<ul><li>You may offer help to get inside for free, or 1,000 coins if you do not.</li><li>Alternatively teleport to the spirit tree if Tree Gnome Village quest is complete.</li></ul>",
    title = "Breaking and entering",
    actions = {
      Action.ModelHighlight:new(lumberOrder), -- Handling for killing the foreman
      Action.Direction:new(2461, 981, 3381),
    },
    postconditions = {
      Condition.ModelVisible:new(femi),
      Condition.DistanceTo:new(2461, 1317, 3444, 8), -- Spirit tree handling
    },
  },
  {
    actions = { Action.ModelHighlight:new(femi) },
    postconditions = {
      Condition.DistanceTo:new(2459, 997, 3409, 6),
      Condition.DistanceTo:new(2461, 1317, 3444, 8), -- Spirit tree handling
    },
  },
  {
    text = "Talk to the King.",
    actions = { Action.Direction:new(2465.5, 3205, 3495.5) },
    postconditions = { Condition.DistanceTo:new(2465, 3205, 3495, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["king narnode shareen"]),
    },
    postconditions = {
      Condition.ConversationText:new(
        "That's enough Traveller, you sound as paranoid as him! Traveller please leave! It's bad enough having one human locked up."
      ),
    },
  },
  {
    text = "Talk to Charlie the prisoner.",
    actions = { Action.ModelHighlight:new(Objects["grand tree ground floor ladder"]) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2466, 4165, 3494, 8),
      Condition.DistanceToWithHeight:new(2466, 6165, 3494, 8),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["grand tree first floor ladder"]) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2465, 5125, 3495, 8),
      Condition.DistanceToWithHeight:new(2466, 6165, 3494, 8),
    },
  },
  {
    actions = { Action.Direction:new(2466, 5125, 3495) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2466, 6125, 3494, 8),
      Condition.DistanceToWithHeight:new(2466, 6165, 3494, 8),
    },
  },
  {
    actions = { Action.ModelHighlight:new(charlie) },
    postconditions = { Condition.ConversationText:new("OK, I'll see what I can find.") },
  },
  {
    text = "Speak to Anita. She can be found in the northwesternmost tree house in the stronghold.",
    actions = { Action.Direction:new(2388, 1189, 3512) },
    postconditions = { Condition.DistanceToWithHeight:new(2388, 2189, 3513, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(anita) },
    postconditions = { Condition.ConversationText:new("No...thank you!") },
  },
  {
    text = "Use the key on the chest in Glough's house and search it to receive the invasion plans.",
    actions = { Action.Direction:new(2476, 1317, 3463) },
    postconditions = { Condition.DistanceTo:new(2476, 1317, 3463, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gloughGroundLadder),
      Action.InventoryHighlight:new(gloughsKey),
      Action.ModelHighlight:new(gloughsChest),
    },
    postconditions = { Condition.ConversationText:new("You have found a scroll!") },
  },
  {
    text = "Talk to the King.",
    actions = { Action.Direction:new(2465.5, 3205, 3495.5) },
    postconditions = { Condition.DistanceTo:new(2465, 3205, 3495, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["king narnode shareen"]),
    },
    postconditions = {
      Condition.ConversationText:new("The Grand Tree's still slowly dying, if it is human sabotage we must respond!"),
    },
  },
  --#endregion
  --#region The fight
  {
    text = "Bank to prepare to fight a level 98 black demon.<ul><li>The demon has a weakness to water spells.</li><li>You can also use a halberd from the safespot.</li><li>When you're ready, go to the top level of Glough's house.</li></ul>",
    title = "The fight",
    actions = { Action.Direction:new(2476, 1317, 3463) },
    postconditions = { Condition.DistanceTo:new(2476, 1317, 3463, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(gloughFirstFloorLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(2485, 3381, 3463, 3) },
  },
  {
    text = "Use the twigs on the pillars to spell TUZO.",
    actions = { Action.InventoryHighlight:new(twigT), Action.Direction:new(2485, 3381, 3467) },
    postconditions = { Condition.ModelVisible:new(twigTPlaced) },
  },
  {
    actions = { Action.InventoryHighlight:new(twigU), Action.Direction:new(2486, 3381, 3467) },
    postconditions = { Condition.ModelVisible:new(twigUPlaced) },
  },
  {
    actions = { Action.InventoryHighlight:new(twigZ), Action.Direction:new(2487, 3381, 3467) },
    postconditions = { Condition.ModelVisible:new(twigZPlaced) },
  },
  {
    actions = { Action.InventoryHighlight:new(twigO), Action.Direction:new(2488, 3381, 3467) },
    postconditions = { Condition.ModelVisible:new(twigOPlaced) },
  },
  {
    text = "Open the trapdoor.",
    actions = { Action.Direction:new(2486, 3381, 3464) },
    postconditions = { Condition.ModelVisible:new(blackDemon) },
  },
  {
    text = "Kill the demon. (Can be safespotted by standing within the rocks to the south)",
    actions = { Action.Direction:new(12447, 837, 1803), Action.ModelHighlight:new(blackDemon) },
    postconditions = { Condition.ChatText:new("Glough's run off!") },
  },
  --#endregion
  --#region Finishing up
  {
    text = "Run to the middle of the cave and talk to the king. Don't click away yet, wait for the scripted dialogue to finish.",
    title = "Finishing up",
    actions = { Action.Direction:new(2462, 773, 9896) },
    postconditions = { Condition.ModelVisible:new(NPCs["king narnode shareen"]) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["king narnode shareen"]) },
    postconditions = {
      Condition.ConversationText:new(
        "A reward will have to wait though, the tree is still dying! The guards are clearing Glough's rock supply now but there must be more Daconia hidden somewhere in the roots! Help us search, we have little time!"
      ),
    },
  },
  {
    text = "Search the nearby roots for a rock.<ul><li>If you don't find the rock try the other roots in the cave.</li><li>Be sure to space-through or click through the text boxes, or else the Daconia rock may not appear in your inventory.</li></ul>",
    postconditions = { Condition.InventoryContains:new(daconiaRock) },
  },
  {
    text = "Take the rock to the king.",
    actions = { Action.Direction:new(2462, 773, 9896) },
    postconditions = { Condition.ModelVisible:new(NPCs["king narnode shareen"]) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["king narnode shareen"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
  --#endregion
}

return Quest:new({
  name = "The Grand Tree",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1039651200,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Agility", 25) },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = { ["Black demon"] = { level = "98", quantity = 1 } },
})
