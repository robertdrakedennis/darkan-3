local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local velorina = Model.new(3483, {
  [2302] = Vertex.new(76, 643, 19, 84, 217, 44, 0.000),
  [2303] = Vertex.new(81, 630, 19, 84, 217, 44, 0.000),
  [2305] = Vertex.new(-76, 643, 19, 84, 217, 44, 0.000),
  [2307] = Vertex.new(-81, 630, 19, 84, 217, 44, 0.000),
  [2308] = Vertex.new(0, 676, 53, 84, 217, 44, 0.000),
})

local oldMan = Model.new(3939, {
  [2512] = Vertex.new(2, 725, -59, 108, 80, 56),
  [2516] = Vertex.new(7, 724, -51, 108, 80, 56),
  [2530] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [2535] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [3284] = Vertex.new(40, 711, -12, 112, 103, 103),
})

-- This may be incorrect or incomplete. Seems like sections of the lobster highlighted.
local giantEnemyLobster = Model.new(2292, {
  [783] = Vertex.new(-292, 84, -436, 82, 13, 7),
  [826] = Vertex.new(-296, 76, -464, 90, 31, 28),
  [830] = Vertex.new(-264, 76, -468, 90, 31, 28),
  [1505] = Vertex.new(-316, 76, -216, 82, 13, 7),
  [1512] = Vertex.new(-316, 76, -216, 82, 13, 7),
})

local akHaranu = Model.new(4503, {
  [2698] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [2703] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [2706] = Vertex.new(2, 725, -59, 107, 79, 55),
  [2708] = Vertex.new(7, 724, -51, 107, 79, 55),
  [4130] = Vertex.new(-30, 721, -31, 43, 43, 47),
})

local robin = Model.new(13356, {
  [4666] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [4671] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [4674] = Vertex.new(2, 725, -59, 107, 79, 55),
  [4676] = Vertex.new(7, 724, -51, 107, 79, 55),
  [13031] = Vertex.new(14, 757, -38, 135, 131, 124),
})

local ghostInkeeper = Model.new(2964, {
  [2503] = Vertex.new(76, 643, 19, 228, 242, 21, 0.000),
  [2504] = Vertex.new(81, 630, 19, 228, 242, 21, 0.000),
  [2506] = Vertex.new(-76, 643, 19, 228, 242, 21, 0.000),
  [2508] = Vertex.new(-81, 630, 19, 228, 242, 21, 0.000),
  [2509] = Vertex.new(0, 676, 53, 78, 203, 41, 0.000),
})

local gravingas = Model.new(3618, {
  [2515] = Vertex.new(76, 643, 19, 220, 233, 20, 0.000),
  [2516] = Vertex.new(81, 630, 19, 220, 233, 20, 0.000),
  [2518] = Vertex.new(-76, 643, 19, 220, 233, 20, 0.000),
  [2520] = Vertex.new(-81, 630, 19, 220, 233, 20, 0.000),
  [2521] = Vertex.new(0, 676, 53, 74, 195, 39, 0.000),
})

local ghostVillager = Model.new(2964, {
  [2503] = Vertex.new(76, 643, 19, 220, 233, 20, 0.000),
  [2504] = Vertex.new(81, 630, 19, 220, 233, 20, 0.000),
  [2506] = Vertex.new(-76, 643, 19, 220, 233, 20, 0.000),
  [2508] = Vertex.new(-81, 630, 19, 220, 233, 20, 0.000),
  [2509] = Vertex.new(0, 676, 53, 74, 195, 39, 0.000),
})

--#endregion

--#region Objects

local sailMast = Model.new(546, {
  [409] = Vertex.new(1904, 2240, 3872, 50, 55, 22),
  [504] = Vertex.new(1824, 2240, 3952, 50, 55, 22),
  [505] = Vertex.new(1824, 2240, 3952, 50, 55, 22),
  [508] = Vertex.new(1824, 2240, 3952, 50, 55, 22),
  [510] = Vertex.new(1808, 2176, 4000, 50, 55, 22),
})

local lobsterChestOpen = Model.new(858, {
  [501] = Vertex.new(-192, 512, -80, 65, 70, 54),
  [505] = Vertex.new(192, 512, -80, 65, 70, 54),
  [508] = Vertex.new(192, 512, -80, 65, 70, 54),
  [512] = Vertex.new(192, 512, -80, 65, 70, 54),
  [537] = Vertex.new(224, 480, -80, 65, 70, 54),
})

local lobsterChestClosed = Model.new(666, {
  [111] = Vertex.new(1312, 220, 3568, 48, 55, 42),
  [201] = Vertex.new(1056, 316, 3440, 65, 70, 54),
  [215] = Vertex.new(1056, 316, 3440, 65, 70, 54),
  [269] = Vertex.new(1504, 316, 3376, 65, 70, 54),
  [665] = Vertex.new(1504, 284, 3312, 65, 70, 54),
})

--#endregion

--#region Items
-- the model data for the ship was slightly different for me for some reason
-- maybe the model data for the ship actually determines what color combination you need?
-- would be interesting to see if there were like n! * n! * n! ship models (where n is number of possible quest dye colors)
local modelShip = Model.any({
  Model.new(474, {
    [13] = Vertex.new(56, 140, 40, 111, 111, 85),
    [16] = Vertex.new(56, 140, 40, 111, 111, 85),
  }),
  Model.new(474, {
    [13] = Vertex.new(56, 140, 40, 112, 112, 86),
    [16] = Vertex.new(56, 140, 40, 112, 112, 86),
  }),
})

local shipChestKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 154, 122, 14),
  [104] = Vertex.new(68, 16, 68, 154, 122, 14),
})

local mapScrap1 = Model.new(294, {
  [129] = Vertex.new(-64, 52, -84, 111, 92, 58),
  [158] = Vertex.new(-64, 52, -84, 160, 127, 66),
})

local mapScrap2 = Model.new(282, {
  [75] = Vertex.new(72, 48, -84, 111, 92, 58),
  [77] = Vertex.new(72, 48, -84, 111, 92, 58),
})

local mapScrap3 = Model.new(90, {
  [3] = Vertex.new(-48, 0, -52, 117, 97, 61),
  [8] = Vertex.new(68, 0, -76, 117, 97, 61),
})

local treasureMap = Model.new(591, {
  [183] = Vertex.new(72, 48, -84, 111, 92, 58),
  [185] = Vertex.new(72, 48, -84, 111, 92, 58),
})

local bookOfHaricanto = Model.new(255, {
  [87] = Vertex.new(64, 36, 56, 101, 57, 41),
  [231] = Vertex.new(-20, 56, 32, 47, 7, 4),
})

local translationManual = Model.new(204, {
  [87] = Vertex.new(36, 36, -84, 45, 71, 53),
  [93] = Vertex.new(64, 36, 56, 45, 71, 53),
})

local oakShieldbow = Model.multi({
  Model.new(690, {
    [1] = Vertex.new(-163, 21, -138, 150, 149, 77),
    [3] = Vertex.new(-170, 21, -141, 150, 149, 77),
  }),
  Model.new(24, {
    [9] = Vertex.new(117, 26, -103, 162, 159, 148),
    [11] = Vertex.new(120, 10, -125, 162, 159, 148),
  }),
})

local bedsheet = Model.new(96, {
  [1] = Vertex.new(-112, 0, -84, 99, 86, 51),
  [2] = Vertex.new(88, 20, -40, 99, 86, 51),
  [3] = Vertex.new(48, 0, -84, 99, 86, 51),
})

local ghostSheet = Model.new(96, {
  [3] = Vertex.new(48, 0, -84, 75, 145, 111),
  [47] = Vertex.new(-32, 0, 168, 75, 145, 111),
})

local petition = Model.new(399, {
  [50] = Vertex.new(-28, 16, 68, 0, 0, 0),
  [156] = Vertex.new(16, 16, 72, 0, 0, 0),
})

local boneKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 106, 133, 69),
  [104] = Vertex.new(68, 16, 68, 106, 133, 69),
})

local robesOfNecro = Model.new(432, {
  [3] = Vertex.new(116, -4, -228, 43, 68, 48),
  [60] = Vertex.new(-148, 8, -228, 43, 68, 48),
})

local enchantedGhostspeak = Model.new(432, {
  [151] = Vertex.new(-36, 0, 72, 81, 54, 16),
  [154] = Vertex.new(-36, 0, 72, 81, 54, 16),
  [186] = Vertex.new(36, 0, 72, 81, 54, 16),
  [187] = Vertex.new(36, 0, 72, 81, 54, 16),
  [190] = Vertex.new(36, 0, 72, 81, 54, 16),
})

local nettles = Model.new(522, {
  [18] = Vertex.new(104, 80, 144, 48, 69, 44),
  [45] = Vertex.new(104, 80, 144, 48, 69, 44),
  [81] = Vertex.new(212, 64, 16, 48, 69, 44),
  [129] = Vertex.new(168, 32, -128, 48, 69, 44),
  [165] = Vertex.new(168, 32, -128, 48, 69, 44),
})

local nettleWater = Model.new(324, { --identical to nettle tea
  [182] = Vertex.new(52, 64, 92, 111, 79, 10),
  [200] = Vertex.new(52, 64, 92, 111, 79, 10),
  [204] = Vertex.new(52, 64, 92, 111, 79, 10),
  [209] = Vertex.new(-52, 64, 92, 111, 79, 10),
  [212] = Vertex.new(-52, 64, 92, 111, 79, 10),
})

local bucketOfSlime = Model.new(570, {
  [2] = Vertex.new(62, 117, 50, 15, 167, 19),
  [4] = Vertex.new(62, 117, 50, 15, 167, 19),
  [7] = Vertex.new(62, 117, -54, 15, 167, 19),
  [14] = Vertex.new(62, 117, -54, 15, 167, 19),
  [19] = Vertex.new(-58, 117, -54, 15, 167, 19),
})

local specialCup = Model.new(552, {
  [15] = Vertex.new(-72, 16, 32, 130, 127, 119),
  [146] = Vertex.new(48, 60, 0, 130, 127, 119),
  [150] = Vertex.new(48, 60, 0, 130, 127, 119),
  [158] = Vertex.new(44, 48, 8, 130, 127, 119),
  [162] = Vertex.new(44, 48, 8, 130, 127, 119),
})

local cupOfTea = Model.new(504, {
  [145] = Vertex.new(-48, 52, -12, 46, 72, 46),
  [152] = Vertex.new(12, 52, -40, 46, 72, 46),
  [400] = Vertex.new(44, 44, -16, 130, 127, 119),
  [471] = Vertex.new(-56, 64, 28, 130, 127, 119),
})

local cupOfMilkyTea = Model.new(504, {
  [12] = Vertex.new(-72, 20, -24, 130, 127, 119),
  [156] = Vertex.new(-20, 52, 48, 75, 81, 62),
  [158] = Vertex.new(40, 52, -12, 75, 81, 62),
  [164] = Vertex.new(40, 52, 20, 75, 81, 62),
})

--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Velorina, in the northernmost building just east of the entrance of Port Phasmatys.<ul><li>You must have the ghostspeak amulet equipped.</li></ul>",
    title = "Ghostly trouble",
    neededItems = { ["Ghostspeak amulet"] = { quantity = 1 } },
    recommendedItems = { ["Ecto-token"] = { quantity = 20 } },
    actions = {
      Action.Direction:new(3677, 1061, 3509, { distance = 8 }),
      Action.ModelHighlight:new(velorina, { distance = 8 }),
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
      Action.ConversationHighlight:new("I would like to enter Port Phasmatys - here's 2 Ectotokens."),
      Action.ConversationHighlight:new("Why, what is the matter?"),
      Action.ConversationHighlight:new("Yes, I do. It is a very sad story."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Velorina.",
    actions = {
      Action.Direction:new(3677, 1061, 3509, { distance = 8 }),
      Action.ModelHighlight:new(velorina, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("may yet be another way") },
  },
  {
    text = "Talk to Necrovarus, at the Ectofuntus to the north.",
    actions = {
      Action.Direction:new(3659, 581, 3517, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["necrovarus"], { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("regret your insolence") },
  },
  {
    text = "Get a bucket of slime.",
    actions = { Action.Direction:new(3653, 581, 3519) },
    postconditions = {
      Condition.DistanceTo:new(3669, 3065, 9888, 4),
      Condition.InventoryContains:new(bucketOfSlime),
    },
  },
  {
    actions = { Action.Direction:new(3683, 165, 9888) },
    postconditions = { Condition.InventoryContains:new(bucketOfSlime) },
  },
  {
    text = "Speak to Velorina.",
    actions = {
      Action.Direction:new(3677, 1061, 3509, { distance = 8 }),
      Action.ModelHighlight:new(velorina, { distance = 8 }),
      Action.ConversationHighlight:new("I would like to enter Port Phasmatys - here's 2 Ectotokens."),
      Action.ConversationHighlight:new("Do you know where this woman can be found?"),
    },
    postconditions = { Condition.ConversationText:new("small wooden shack") },
  },
  {
    text = "Teleport to Canifis lodestone.",
    title = "Getting help",
    neededItems = {
      ["Leather gloves"] = { quantity = 1 },
      ["Nettle tea"] = { quantity = 1 },
      ["Silk"] = { quantity = 1 },
      ["Thread"] = { quantity = 1 },
      ["Bucket of milk"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3461, 500, 3555) },
    postconditions = { Condition.DistanceTo:new(3520, 500, 3520, 32) },
  },
  {
    text = "Pick some nettles while wearing gloves.",
    actions = { Action.Direction:new(3524, 485, 3511) },
    postconditions = {
      Condition.InventoryContains:new(nettles),
      Condition.InventoryContains:new(nettleWater),
    },
  },
  {
    text = "Use the nettles on the bowl of water to get nettle-water.",
    actions = {
      Action.InventoryHighlight:new(nettles),
      Action.InventoryHighlight:new(Models.items["bowl of water"]),
    },
    postconditions = { Condition.InventoryContains:new(nettleWater) },
  },
  {
    text = "Boil the nettle-water on a fire or range.",
    warning = "Nettle-water and nettle tea models are the same. If you have nettle tea already, skip this step.",
    actions = {
      Action.InventoryHighlight:new(nettleWater),
      Action.Direction:new(1, 1, 1),
    },
    postconditions = { Condition.ChatText:new("water and make nettle tea.") },
  },
  {
    text = "Talk to Netty, in her house east of the Slayer Tower.",
    actions = {
      Action.Direction:new(3462, 813, 3558, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["netty"], { distance = 3 }),
      Action.ConversationHighlight:new("You are doing so much for me - is there anything I can do for you?"),
    },
    postconditions = { Condition.ConversationText:new("I left Port Phasmatys") },
  },
  {
    text = "Use the nettle tea into the special cup.",
    actions = {
      Action.InventoryHighlight:new(specialCup),
      Action.InventoryHighlight:new(nettleWater),
    },
    postconditions = { Condition.InventoryContains:new(cupOfTea) },
  },
  {
    text = "Use a bucket of milk into the cup of tea.",
    actions = {
      Action.InventoryHighlight:new(Models.items["bucket of milk"]),
      Action.InventoryHighlight:new(cupOfTea),
    },
    postconditions = { Condition.InventoryContains:new(cupOfMilkyTea) },
  },
  {
    text = "Talk to Netty.",
    actions = {
      Action.Direction:new(3462, 813, 3558, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["netty"], { distance = 3 }),
      Action.ConversationHighlight:new("You are doing so much for me - is there anything I can do for you?"),
    },
    postconditions = { Condition.ConversationText:new("I will pass it on") },
  },
  {
    text = "Repair the model ship with the silk. Bring thread just in case.",
    actions = { Action.InventoryHighlight:new(modelShip) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["silk"]) },
  },
  -- Worth noting, we have whole "sections" (based off title) where certain postconditions could mean
  -- This section is done. Not necessarily widely applicable, but could be a nice helper method to support.
  {
    text = "Climb the ladder in the shipwreck east of the Canifis lodestone.",
    title = "The map",
    neededItems = {
      ["Red dye"] = { quantity = 3 },
      ["Blue dye"] = { quantity = 3 },
      ["Yellow dye"] = { quantity = 3 },
    },
    actions = { Action.Direction:new(3613.45, 505, 3543) },
    postconditions = { Condition.DistanceToWithHeight:new(3614, 965, 3543, 4) },
  },
  {
    text = "Climb the ladder to reach the top deck",
    actions = { Action.Direction:new(3615.45, 1465, 3541) },
    postconditions = { Condition.DistanceToWithHeight:new(3616, 1925, 3541, 4) },
  },
  -- Could also be cool to highlight sailMast only when windspeed is low?
  -- TODO: Would be cool to track state that the player has seen Top, Bottom, Skull of the ship and what colors should be
  -- and then be able to highlight the inventory to show what dyes are needed where
  {
    text = "Search the mast. Read the chat box to determine which dye goes on which part of the flag.",
    warning = "This cannot be tracked. You have to read yourself.",
    actions = { Action.ModelHighlight:new(sailMast) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3615.45, 1925, 3541) },
    postconditions = { Condition.DistanceToWithHeight:new(3614, 965, 3543, 4) },
  },
  {
    text = "Speak to the old man.",
    actions = {
      Action.ModelHighlight:new(oldMan),
      Action.ConversationHighlight:new("Is this your toy boat?"),
    },
    postconditions = { Condition.InventoryContains:new(shipChestKey) },
  },
  {
    text = "Use the key on the chest by the Captain and open it for a map scrap.",
    actions = {
      Action.InventoryHighlight:new(shipChestKey),
      Action.Direction:new(3619, 1365, 3544.8),
    },
    postconditions = { Condition.InventoryContains:new(mapScrap1) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3613.45, 965, 3543) },
    postconditions = { Condition.DistanceToWithHeight:new(3612, 5, 3543, 4) },
  },
  {
    text = "Open a chest in the eastern end of the ship.",
    actions = { Action.Direction:new(3618, 5, 3542) },
    postconditions = { Condition.ModelVisible:new(giantEnemyLobster) },
  },
  -- TODO: Test both action highlight and postconditions
  {
    text = "Kill the giant lobster that spawns.",
    actions = { Action.ModelHighlight:new(giantEnemyLobster) },
    postconditions = { Condition.ModelNotVisible:new(giantEnemyLobster) },
  },
  {
    text = "Search the chest again for a map scrap.",
    actions = {
      Action.ModelHighlight:new(lobsterChestOpen),
      Action.ModelHighlight:new(lobsterChestClosed),
    },
    postconditions = { Condition.InventoryContains:new(mapScrap2) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(3613.45, 505, 3543) },
    postconditions = { Condition.DistanceToWithHeight:new(3614, 965, 3543, 4) },
  },
  {
    text = "Cross the gangplank.",
    actions = { Action.Direction:new(3605, 1100, 3546) },
    postconditions = { Condition.DistanceTo:new(3605, 600, 3548, 4) },
  },
  {
    text = "Jump across the rocks until you find a chest. Open it for another map scrap.",
    actions = {
      Action.PathGuide:new({
        Location:new(3605, 965, 3550),
        Location:new(3601, 965, 3550),
        Location:new(3601, 965, 3552),
        Location:new(3595, 965, 3552),
        Location:new(3595, 965, 3557),
        Location:new(3597, 965, 3557),
        Location:new(3597, 965, 3564),
        Location:new(3605, 965, 3564),
      }),
    },
    postconditions = { Condition.InventoryContains:new(mapScrap3) },
  },
  {
    text = "Put the pieces together to get the map.",
    actions = {
      Action.InventoryHighlight:new(mapScrap1),
      Action.InventoryHighlight:new(mapScrap2),
      Action.InventoryHighlight:new(mapScrap3),
    },
    postconditions = { Condition.InventoryContains:new(treasureMap) },
  },
  {
    text = "Travel with the ghost captain back in to Port Phasmatys.",
    title = "Dragontooth Island",
    neededItems = {
      ["Spade"] = { quantity = 1 },
      ["Treasure map"] = { quantity = 1, model = treasureMap },
    },
    actions = {
      Action.Direction:new(3703, 325, 3487, { distance = 16 }),
      Action.ModelHighlight:new(
        Models.npcs["ghost captain"],
        { atLocation = Location:new(3703, 325, 3487), distance = 16 }
      ),
      Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]),
      Action.ConversationHighlight:new("I would like to enter Port Phasmatys - here's 2 Ectotokens."),
      Action.ConversationHighlight:new("Please take me to Dragontooth Island."),
    },
    postconditions = {
      Condition.DistanceTo:new(3794, 477, 3559, 10),
      Condition.InventoryContains:new(bookOfHaricanto),
    },
  },
  {
    text = "Dig at the marked location.",
    actions = {
      Action.Direction:new(3803, 901, 3530),
      Action.InventoryHighlight:new(Models.items["spade"]),
    },
    postconditions = { Condition.InventoryContains:new(bookOfHaricanto) },
  },
  {
    text = "Return to Port Phasmatys.",
    actions = {
      Action.Direction:new(3792, 261, 3560, { distance = 14 }),
      Action.ModelHighlight:new(Models.npcs["ghost captain"], { distance = 14 }),
    },
    postconditions = { Condition.DistanceTo:new(3702, 325, 3487, 12) },
  },
  {
    text = "Talk to Ak-Haranu.",
    title = "Translation",
    neededItems = { ["Oak shieldbow"] = { quantity = 1, model = oakShieldbow } },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(akHaranu),
      Action.ConversationHighlight:new("Okay, wait here - I'll get you your bow."),
    },
    postconditions = { Condition.ConversationText:new("get you your bow") },
  },
  {
    text = "Win 4 games of Rune-Draw against Robin.<ul><li>Just click draw until a player draws a death rune.</li><li>Close the interface to continue dialogue.</li></ul>",
    actions = {
      Action.ModelHighlight:new(robin),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("Yes, I'll give you a game."),
    },
    postconditions = { Condition.ConversationText:new("signs the oak shieldbow") },
  },
  {
    text = "Speak to Ak-Haranu to get the translation manual.",
    actions = {
      Action.ModelHighlight:new(akHaranu),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.InventoryContains:new(translationManual) },
  },
  {
    text = "Talk to the ghost innkeeper.",
    title = "Revolution",
    actions = {
      Action.ModelHighlight:new(ghostInkeeper, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Do you have any jobs I can do?"),
      Action.ConversationHighlight:new("Yes, I'd be delighted."),
    },
    postconditions = { Condition.InventoryContains:new(bedsheet) },
  },
  {
    text = "Use a bucket of slime on the bedsheet.",
    actions = {
      Action.InventoryHighlight:new(bedsheet),
      Action.InventoryHighlight:new(bucketOfSlime),
    },
    postconditions = { Condition.InventoryContains:new(ghostSheet) },
  },
  {
    text = "Wear the bedsheet and speak to Gravingas west of the inn to get a petition form.",
    actions = {
      Action.ModelHighlight:new(gravingas),
      Action.InventoryHighlight:new(ghostSheet),
      Action.ConversationHighlight:new("After hearing Velorina's story I will be happy to help out."),
    },
    postconditions = { Condition.InventoryContains:new(petition) },
  },
  {
    text = "Ask the ghost villager for signatures until you have 10.<ul><li>You can get multiple signatures from the same ghost if you ask another ghost first.</li><li>Some will resist. Just ask others.</li><li>If a ghost ask for ecto-tokens, simply leave the conversation and ask the same ghost again.</li></ul>",
    actions = { Action.ModelHighlight:new(ghostVillager, { highlightPriority = "closest" }) }, --all is probably too cpu intensive
    postconditions = { Condition.ConversationText:new("obtaining 10 signatures") }, --doesn't seem to be working
  },
  {
    text = "Speak to Necrovarus at the Ectofuntus.",
    actions = { Action.ModelHighlight:new(Models.npcs["necrovarus"]) },
    postconditions = { Condition.ModelVisible:new(boneKey) },
  },
  {
    text = "Pick up the bone key he drops.",
    actions = { Action.ModelHighlight:new(boneKey) },
    postconditions = { Condition.InventoryContains:new(boneKey) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(3666.5, 981, 3518) },
    postconditions = { Condition.DistanceToWithHeight:new(3666, 2021, 3522, 2) },
  },
  {
    text = "Use the key on the locked door and enter.",
    actions = {
      Action.Direction:new(3655.5, 2621, 3514),
      Action.InventoryHighlight:new(boneKey),
    },
    postconditions = { Condition.DistanceTo:new(3657, 2021, 3513, 1) },
  },
  {
    text = "Open and search the coffin for Necrovarus' mystical robes.",
    actions = { Action.Direction:new(3659.25, 2421, 3513.7) },
    postconditions = { Condition.InventoryContains:new(robesOfNecro) },
  },
  {
    text = "Return to Netty, in her shack east of the Slayer Tower.",
    title = "Finishing up",
    neededItems = {
      ["Book of Haricanto"] = { quantity = 1, model = bookOfHaricanto },
      ["Translation Manual"] = { quantity = 1, model = translationManual },
      ["Robes of Necrovarus"] = { quantity = 1, model = robesOfNecro },
    },
    actions = {
      Action.Direction:new(3462, 813, 3558, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["netty"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("emits a green glow") }, --doesn't seem to be working, maybe change to ConversationInactive
  },
  {
    text = "Talk to Necrovarus with the enchanted ghostspeak amulet equipped.",
    actions = {
      Action.Direction:new(3659, 581, 3517, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["necrovarus"], { distance = 12 }),
      Action.InventoryHighlight:new(enchantedGhostspeak),
      Action.ConversationHighlight:new("Let any ghost who so wishes pass on into the next world."),
    },
    postconditions = { Condition.ConversationText:new("pass into the next world") },
  },
  {
    text = "Talk to Velorina.",
    actions = {
      Action.Direction:new(3677, 1061, 3509, { distance = 8 }),
      Action.ModelHighlight:new(velorina, { distance = 8 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Ghosts Ahoy",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.short,
  releaseDate = 1108425600,
  prereqQuests = { "The Restless Ghost" },
  questReqs = {
    Types.QuestReq.skill("Agility", 25),
    Types.QuestReq.skill("Cooking", 20),
  },
  neededItems = {
    ["Nettle tea"] = { quantity = 1, model = nettleWater },
    ["Leather gloves"] = { quantity = 1, model = Models.items["leather gloves"] },
    ["Ecto-tokens (check currency pouch)"] = { quantity = 20 },
    ["Silk"] = { quantity = 1, model = Models.items["silk"] },
    ["Thread"] = { quantity = 1, model = Models.items["thread"] },
    ["Bucket of milk"] = { quantity = 1, model = Models.items["bucket of milk"] },
    ["Bucket of slime"] = { quantity = 1, model = bucketOfSlime },
    ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] },
    ["Spade"] = { quantity = 1, model = Models.items["spade"] },
    ["Oak shieldbow"] = { quantity = 1, model = oakShieldbow },
    ["Red dye"] = { quantity = 3, model = Models.items["red dye"] },
    ["Blue dye"] = { quantity = 3, model = Models.items["blue dye"] },
    ["Yellow dye"] = { quantity = 3, model = Models.items["yellow dye"] },
  },
  combatNPCs = { ["Giant lobster"] = { level = "42", quantity = 1 } },
})
