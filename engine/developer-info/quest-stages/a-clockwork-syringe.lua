local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

local deliveryNote = Model.new(480, {
  [1] = Vertex.new(-4, 24, 20, 98, 91, 62),
})

local largeCrate = Model.new(31554, {
  [28497] = Vertex.new(-910, 652, 494, 127, 127, 127),
})

local barrelChest = Model.new(24417, {
  [5340] = Vertex.new(-369, 969, 324, 127, 127, 127),
})

local zombieHead = Model.new(1521, {
  [149] = Vertex.new(104, 257, -104, 66, 59, 51),
})

local zombieHeadInv = Model.new(765, {
  [1] = Vertex.new(70, 110, 70, 35, 16, 3),
})

local estateAgent = Model.new(4515, {
  [552] = Vertex.new(-57, 652, 69, 103, 96, 79),
})

local bill = Models.npcs["bill teach"]
local billHl = Action.ModelHighlight:new(bill)
local captain = Models.npcs["captain braindeath"]
local captainHl = Action.ModelHighlight:new(captain)

local evidenceTable = Model.new(582, {
  [485] = Vertex.new(476, 320, 484, 59, 44, 12),
})

local tableWithHead = Model.new(1938, {
  [1076] = Vertex.new(476, 320, 484, 59, 44, 12),
})

local pirateSheet = Model.new(480, {
  [1] = Vertex.new(-4, 24, 20, 98, 91, 62),
})

local mystEntrance = Model.new(2835, {
  [225] = Vertex.new(-244, 145, -456, 56, 51, 51, 0.9922),
})

local deadBrewers = Model.any({
  Model.new(3699, {
    [1496] = Vertex.new(-113, 41, -172, 100, 100, 92),
  }),
  Model.new(3537, {
    [1521] = Vertex.new(173, 9, 171, 131, 125, 100),
  }),
  Model.new(3369, {
    [21] = Vertex.new(351, 45, 207, 29, 29, 22),
  }),
})

local twiblick = Model.new(1500, {
  [1] = Vertex.new(-120, 110, -120, 53, 50, 48),
})

-- for some reason, the open lockers have a completely different location
local gunLocker = Model.any({
  -- Model.new(624, {
  --   [333] = Vertex.new(-248, 216, -192, 55, 36, 11),
  -- }),
  Model.new(504, {
    [173] = Vertex.new(448, 216, 1528, 65, 50, 19),
  }),
})

local cannonball = Model.new(144, {
  [1] = Vertex.new(57, 79, 0, 41, 39, 37),
})

local repairLocker = Model.any({
  Model.new(336, {
    [39] = Vertex.new(3520, 216, 520, 55, 36, 11),
  }),
  -- Model.new(624, {
  --   [333] = Vertex.new(-248, 216, -192, 55, 36, 11),
  -- }),
})

local chain = Model.new(1377, {
  [1] = Vertex.new(-64, 4, -115, 89, 90, 98),
})

local heatedChain = Model.new(1377, {
  [1] = Vertex.new(-64, 4, -115, 198, 126, 17),
})

local cannonballChain = Model.new(1539, {
  [1] = Vertex.new(-64, 4, -115, 89, 90, 98),
})

local gunpowder = Model.new(72, {
  [1] = Vertex.new(-44, 0, 36, 26, 26, 17),
})

local barrel = Model.new(909, {
  [1] = Vertex.new(64, 68, 120, 48, 48, 53),
})

local cannonballBarrel = Model.new(2025, {
  [1] = Vertex.new(144, 68, 174, 48, 48, 53),
})

local eastCannon = Model.new(1464, {
  [855] = Vertex.new(32, 216, -344, 44, 40, 40),
})

local barrelParts = Model.new(1320, {
  [1] = Vertex.new(3, 395, 212, 72, 72, 55),
})

local surgicalMask = Model.new(138, {
  [1] = Vertex.new(-68, 11, -11, 119, 119, 109),
})

local bandages = Model.new(108, {
  [1] = Vertex.new(-104, 36, 48, 165, 165, 151),
})

local disguise = Model.multi({
  Model.new(5004, {
    [1] = Vertex.new(236, 886, 148, 91, 81, 69),
  }),
  Model.new(6, {
    [1] = Vertex.new(204, 438, -32, 150, 168, 186, 0.000),
  }),
})

local disguiseCharacter = Model.new(7116, {
  [5204] = Vertex.new(256, 536, -116, 57, 57, 44),
})

local gascanister = Model.new(2502, {
  [1254] = Vertex.new(-107, 620, 86, 46, 89, 51),
})

local disorderly = Model.any({
  Model.new(3132, {
    [1959] = Vertex.new(-56, 668, 64, 44, 40, 40),
  }),
  Model.new(3354, {
    [2135] = Vertex.new(-252, 416, -48, 72, 45, 17),
  }),
})

local evidenceLetter = Model.new(198, {
  [1] = Vertex.new(-166, 10, 16, 173, 168, 161),
})

local grimterm = Model.new(2790, {
  [1472] = Vertex.new(0, 796, -60, 63, 69, 63),
})

local arms = Model.new(1530, {
  [702] = Vertex.new(203, 577, -302, 103, 92, 79),
})

local gunpowderBarrel = Model.new(600, {
  [1] = Vertex.new(-74, 415, 97, 41, 39, 37),
})

local incompleteChest = Model.new(3720, {
  [2100] = Vertex.new(85, 40, 130, 30, 29, 28),
})

local riggedChest = Model.new(6732, {
  [4110] = Vertex.new(85, 40, 130, 30, 29, 28),
})

local destroyedConveyer = Model.new(4356, {
  [2700] = Vertex.new(235, 577, -196, 29, 26, 22),
})

local smallWall = Model.new(600, {
  [83] = Vertex.new(-192, 269, 252, 0, 0, 0),
})

local dontknowName = Model.new(3705, {
  [2576] = Vertex.new(-40, 745, -73, 62, 54, 32),
})

local boat = Model.new(4098, {
  [1] = Vertex.new(448, 20, -321, 86, 113, 102),
})

---@type QuestStep[]
local steps = {
  {
    text = "Go to your player-owned house portal, and attempt to enter your house.",
    title = "Delivery note in the house",
    actions = {
      Action.ConversationHighlight:new("A Clockwork Syringe"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InventoryContains:new(deliveryNote) },
  },
  {
    text = "Read the delivery note and accept the quest.",
    actions = { Action.InventoryHighlight:new(deliveryNote) },
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Attempt to enter your house again and select the 'A Clockwork Syringe' option once more.",
    actions = { Action.ConversationHighlight:new("A Clockwork Syringe."), Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Investigate the large crate.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ModelHighlight:new(largeCrate) },
    postconditions = { Condition.ModelVisible:new(barrelChest) },
  },
  { text = "Kill the level 98 Barrelchest Mk II.", postconditions = { Condition.ModelVisible:new(zombieHead) } },
  {
    text = "Talk to the estate agent.",
    title = "Dealing with the delivery",
    actions = { Action.ModelHighlight:new(estateAgent) },
    postconditions = { Condition.ConversationText:new("Good day!") },
  },
  {
    text = "Interrogate the zombie head.",
    actions = { Action.ModelHighlight:new(zombieHead) },
    postconditions = { Condition.ConversationText:new("We'll soon see about that") },
  },
  {
    text = "Torture the head using all five options, once maximum stress, talk to the head again.",
    actions = { Action.ModelHighlight:new(zombieHead) },
    postconditions = { Condition.ConversationText:new("Now I need to go and warn Bill") },
  },
  {
    text = "With the zombie head, talk to Bill Teach on Mos Le'Harmless, either in The Other Inn or on The Adventurous ship.<ul><li>Head to the docks of Port Phasmatys, cross the gangplank on the easternmost ship, right-click travel via Bill Teach to travel to Mos Le'Harmless.</li><li>Alternatively, charter a ship directly to Mos Le'Harmless from Port Sarim.</li></ul>",
    title = "Warning Bill Teach",
    neededItems = {
      ["Zombie head"] = { quantity = 1, model = zombieHeadInv },
    },
    actions = {
      Action.ConversationHighlight:new("Speak about A Clockwork Syringe."),
      Action.Direction:new(3033, 741, 3191, { distance = 10 }),
      Action.ModelHighlight:new(Models.npcs["trader stan"], { distance = 10 }),
      Action.Direction:new(3712, 965, 3496, { distance = 1 }),
      billHl,
    },
    postconditions = { Condition.ConversationText:new("Trust me, Player") },
  },
  {
    text = "Walk north to Harpoon Joe's House of 'Rum', sit in the chair in the north-east corner and ask for a 'Long Drop'.",
    actions = {
      Action.Direction:new(3669, 645, 2995, { tile = true }),
      Action.ConversationHighlight:new("A Long Drop."),
    },
    postconditions = { Condition.ModelVisible:new(bill) },
  },
  {
    text = "Talk to Bill.",
    actions = { billHl },
    postconditions = { Condition.ConversationText:new("Put it on that table") },
  },
  {
    text = "Place-evidence on table nearby to set the zombie head on top.",
    actions = { Action.ModelHighlight:new(evidenceTable) },
    postconditions = { Condition.ModelVisible:new(tableWithHead) },
  },
  {
    text = "Talk to Bill again.",
    actions = { billHl },
    postconditions = { Condition.ConversationText:new("Side effects") },
  },
  {
    text = "Climb-up the ladder.",
    actions = { Action.Direction:new(1, 0, 0, { tile = true, instance = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Select-spell with the pirate spell sheet in your backpack to travel to Braindeath Island.",
    title = "Finding the workers",
    neededItems = {
      ["Pirate spell sheet"] = { quantity = 1, model = pirateSheet },
      ["Fishbowl Helmet (can be reclaimed during quest if destroyed)"] = { quantity = 1 },
      ["Diving apparatus (can be reclaimed during quest if destroyed)"] = { quantity = 1 },
    },
    actions = { Action.ConversationHighlight:new("Braindeath Island."), Action.InventoryHighlight:new(pirateSheet) },
    postconditions = { Condition.DistanceTo:new(2162, 1061, 5114, 10) },
  },
  {
    text = "Go directly west and talk to Captain Braindeath.<ul><li>Also get a diving apparatus and a fishbowl helmet from him, if you don't already have them.</li></ul>",
    actions = {
      Action.Direction:new(2144, 2021, 5108, { distance = 5 }),
      captainHl,
      Action.ConversationHighlight:new("Talk about A Clockwork Syringe."),
    },
    postconditions = { Condition.ConversationText:new("While ye're searchin") },
  },
  {
    text = "Go to the Braindeath Island 'Rum'-geon entrance:<ul><li>Optional: If you have it, activate the Dungeoneering cape to travel directly there, skipping these steps.</li></ul>",
    actions = {
      Action.PathGuide:new({
        Location:new(2144, 2021, 5108),
        Location:new(2144, 2021, 5106),
        Location:new(2142, 2021, 5106),
        Location:new(2138, 2021, 5102),
        Location:new(2138, 2021, 5096),
        Location:new(2129, 2021, 5096),
        Location:new(2129, 981, 5093),
        Location:new(2127, 1197, 5091),
        Location:new(2120, 1245, 5091),
        Location:new(2120, 349, 5098),
        Location:new(2120, 565, 5103),
        Location:new(2120, 349, 5109),
        Location:new(2120, 501, 5114),
        Location:new(2120, 285, 5120),
        Location:new(2120, 717, 5126),
        Location:new(2121, 757, 5127),
        Location:new(2121, 1069, 5131),
        Location:new(2121, 1245, 5137),
        Location:new(2121, 953, 5142),
        Location:new(2124, 1233, 5145),
        Location:new(2126, 997, 5147),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2126, 997, 5147, 0) },
  },
  {
    text = "Equip your diving apparatus and fishbowl helmet.",
    actions = {
      Action.InventoryHighlight:new(Models.items["fishbowl helmet"]),
      Action.InventoryHighlight:new(Models.items["diving apparatus"]),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(Models.items["fishbowl helmet"]),
      Condition.InventoryDoesNotContain:new(Models.items["diving apparatus"]),
    },
  },
  {
    text = "Enter the mysterious entrance.<ul><li>Optional: kill a crab in order to complete the hard Daemonheim achievement Alcrabholic.</li></ul>",
    actions = { Action.ModelHighlight:new(mystEntrance) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Go down the west path and search the dead brewers to discover his names.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 6),
        Location:new(-9, 0, 6),
        Location:new(-15, 0, 13),
        Location:new(-15, 16, 20),
        Location:new(-14, 16, 21),
        Location:new(-14, 16, 23),
        Location:new(-16, 16, 25),
        Location:new(-16, 16, 36),
        Location:new(-14, 0, 38),
        Location:new(-14, 0, 45),
      }, { instance = true }),
      Action.ModelHighlight:new(deadBrewers, { distance = 10 }),
    },
    postconditions = { Condition.ConversationText:new("I'll see if I can find his name badge") },
    warning = "Ensure you've seen the name of the brewer in the conversation window, the quest plugin might not fully detect this.",
  },
  {
    text = "Go down the middle path and search the dead brewer to discover his names",
    actions = {
      Action.PathGuide:new({
        Location:new(-14, 0, 45),
        Location:new(-14, 0, 38),
        Location:new(-16, 16, 36),
        Location:new(-16, 16, 25),
        Location:new(-14, 16, 23),
        Location:new(-14, 16, 21),
        Location:new(-15, 16, 20),
        Location:new(-15, 0, 13),
        Location:new(-9, 0, 6),
        Location:new(0, 0, 6),
        Location:new(0, 0, 32),
        Location:new(4, 0, 36),
        Location:new(4, 0, 38),
      }, { instance = true }),
    },
    -- postconditions = { Condition.DistanceTo:new(4, 0, 38, 2, true) },
    warning = "Ensure you've seen the name of the brewer in the conversation window, the quest plugin might not fully detect this.",
  },
  {
    actions = {
      Action.ModelHighlight:new(deadBrewers, { distance = 10 }),
    },
    postconditions = { Condition.ConversationText:new("I'll see if I can find his name badge") },
  },
  {
    text = "Go down the middle path and search the dead brewer to discover his names",
    actions = {
      Action.PathGuide:new({
        Location:new(4, 0, 38),
        Location:new(4, 0, 36),
        Location:new(0, 0, 32),
        Location:new(0, 0, 6),
        Location:new(10, 0, 6),
        Location:new(13, 0, 3),
        Location:new(20, 0, 3),
        Location:new(20, 0, 9),
        Location:new(16, 0, 13),
        Location:new(16, 0, 39),
        Location:new(19, 0, 42),
        Location:new(22, 0, 42),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(22, 0, 42, 4, true) },
    warning = "Ensure you've seen the name of the brewer in the conversation window, the quest plugin might not fully detect this.",
  },
  {
    actions = {
      Action.ModelHighlight:new(deadBrewers, { distance = 10 }),
    },
    postconditions = { Condition.ConversationText:new("I'll see if I can find his name badge") },
  },
  {
    text = "Return to Captain Braindeath and receive the Twiblick night special.",
    actions = {
      Action.PathGuide:new({
        Location:new(22, 0, 42),
        Location:new(19, 0, 42),
        Location:new(16, 0, 39),
        Location:new(16, 0, 13),
        Location:new(20, 0, 9),
        Location:new(20, 0, 3),
        Location:new(13, 0, 3),
        Location:new(10, 0, 6),
        Location:new(0, 0, 6),
        Location:new(0, 0, 0),
      }, { instance = true }),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(2126, 997, 5147),
        Location:new(2124, 1233, 5145),
        Location:new(2121, 953, 5142),
        Location:new(2121, 1245, 5137),
        Location:new(2121, 1069, 5131),
        Location:new(2121, 757, 5127),
        Location:new(2120, 717, 5126),
        Location:new(2120, 285, 5120),
        Location:new(2120, 501, 5114),
        Location:new(2120, 349, 5109),
        Location:new(2120, 565, 5103),
        Location:new(2120, 349, 5098),
        Location:new(2120, 1245, 5091),
        Location:new(2127, 1197, 5091),
        Location:new(2129, 981, 5093),
        Location:new(2129, 2021, 5096),
        Location:new(2138, 2021, 5096),
        Location:new(2138, 2021, 5102),
        Location:new(2142, 2021, 5106),
        Location:new(2144, 2021, 5106),
        Location:new(2144, 2021, 5108),
      }),
      captainHl,
      Action.ConversationHighlight:new("Talk about A Clockwork Syringe."),
      Action.ConversationHighlight:new("I found the three missing brewers..."),
      Action.ConversationHighlight:new("Yes, I'm ready."),
    },
    postconditions = { Condition.InventoryContains:new(twiblick) },
  },
  {
    text = "Teleport back to Mos Le'Harmless with the pirate spell sheet.",
    actions = { Action.InventoryHighlight:new(pirateSheet), Action.ConversationHighlight:new("Mos Le'Harmless.") },
    postconditions = { Condition.DistanceTo:new(3684, 45, 2958, 15) },
  },
  {
    text = "Return to the basement in Joe's pub.",
    actions = {
      Action.Direction:new(3669, 645, 2995, { tile = true }),
      Action.ConversationHighlight:new("A Long Drop."),
    },
    postconditions = { Condition.ModelVisible:new(bill) },
  },
  {
    text = "Talk to Bill and receive the unlocked Twiblick night special.",
    actions = { billHl },
    postconditions = { Condition.ConversationText:new("Open the Twiblick Night Special") },
  },
  {
    text = "Open the box.",
    actions = {
      Action.InventoryHighlight:new(twiblick),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes!"),
      Action.ConversationHighlight:new("Just open it already!"),
    },
    postconditions = { Condition.ConversationText:new("but I'm not sure I want to") },
  },
  {
    text = "Talk to Bill again.",
    actions = {
      billHl,
      Action.ConversationHighlight:new("How are we going to use this to get information?"),
    },
    postconditions = { Condition.ConversationText:new("Time to choose how we make") },
  },
  {
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("I'll talk, I'll talk") },
  },
  {
    text = "Talk to Bill again.",
    actions = { billHl },
    postconditions = { Condition.ConversationText:new("Meet me aboard the ship") },
  },

  {
    text = "Return south to Bill's ship, cross the gangplank, talk to him, and use the 'A Clockwork Syringe' option to begin your travel.",
    title = "Getting into Bloodsplatter Isle",
    actions = { Action.Direction:new(1, 0, 0, { tile = true, instance = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    actions = {
      Action.Direction:new(3684, 965, 2950, { distance = 2 }),
      billHl,
      Action.ConversationHighlight:new("Speak about A Clockwork Syringe."),
      Action.ConversationHighlight:new("Yes, let's get underway."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Bill.",
    actions = { billHl, Action.ConversationHighlight:new("Awesome! I'll get right on it!") },
    postconditions = { Condition.ConversationText:new("That's what I like about ye") },
  },
  {
    text = "Climb-down two ladders to the hold of the ship (lowest level).",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 1.5),
        Location:new(1, -480, 3),
        Location:new(0, -480, 4),
        Location:new(0, -480, 11),
        Location:new(-1, -480, 11),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(-1, -1440, 11, 1, true) },
  },
  {
    text = "Open and search the gun locker",
    actions = { Action.ModelHighlight:new(gunLocker, { atLocation = Location:new(0, -1440, 7), instanced = true }) },
    postconditions = { Condition.InventoryContains:new(cannonball) },
    warning = "No highlighting for the open chest available",
  },
  {
    text = "Open and search the repair locker",
    actions = { Action.ModelHighlight:new(repairLocker, { atLocation = Location:new(-2, -1440, 6), instanced = true }) },
    postconditions = { Condition.InventoryContains:new(chain) },
    warning = "No highlighting for the open chest available",
  },
  {
    text = "Take 3 gunpowder from the powder barrel",
    actions = { Action.Direction:new(-2, -1440, 2, { tile = true, instance = true }) },
    postconditions = { Condition.InventoryContains:new(gunpowder, 3) },
  },
  {
    text = "Take a barrel from the barrel stack",
    actions = { Action.Direction:new(0, -1440, 0, { tile = true, instance = true }) },
    postconditions = { Condition.InventoryContains:new(barrel) },
  },
  {
    text = "Use the gunpowder on the chain",
    actions = { Action.InventoryHighlight:new(gunpowder), Action.InventoryHighlight:new(chain) },
    postconditions = { Condition.InventoryContains:new(heatedChain) },
  },
  {
    text = "Use the anvil to smith the heated chain and cannonball together",
    actions = { Action.Direction:new(-1, -1440, -2, { tile = true, instance = true }) },
    postconditions = { Condition.InventoryContains:new(cannonballChain) },
  },
  {
    text = "Use your cannonball and chain on the barrel to receive a cannonball barrel-boat",
    actions = { Action.InventoryHighlight:new(cannonballChain), Action.InventoryHighlight:new(barrel) },
    postconditions = { Condition.InventoryContains:new(cannonballBarrel) },
  },
  {
    text = "Go back up the ladder to the main deck. On the eastern cannon, choose 'Take-The-Ride' option to go to Bloodsplatter Isle.",
    actions = {
      Action.PathGuide:new({
        Location:new(-1, -1440, -1),
        Location:new(-1, -1440, 2),
        Location:new(0, -1440, 3),
        Location:new(0, -1440, 5),
        Location:new(-1, -1440, 6),
        Location:new(-1, -1440, 8),
        Location:new(-2, -1440, 8),
        Location:new(-2, -1440, 11),
        Location:new(-1, -1440, 11),
        Location:new(-1, -1440, -1),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(-1, -480, 11, 20, true) },
  },
  {
    actions = { Action.ModelHighlight:new(eastCannon), Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.DistanceTo:new(15, 137, 0, 10, true) },
  },
  {
    text = "Investigate the Perch Rock.  You must steer him around while dropping cannonballs on the Barrelchests.",
    title = "Investigating Bloodsplatter Isle",
    actions = {
      Action.Direction:new(19, 985, 0, { instance = true, tile = true }),
      Action.ConversationHighlight:new(""),
      Action.ConversationHighlight:new("I'm ready to begin."),
    },
    postconditions = { Condition.ChatText:new("All enemies defeated!") },
    warning = "Ensure chat text is on or filtered to have automatic progression of the plugin.",
  },
  {
    text = "Leave the Isle by diving at the pier and swim.",
    actions = {
      Action.Direction:new(12, 61, 1, { instance = true, tile = true }),
      Action.ConversationHighlight:new("Swim."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Climb the ladder and talk to Bill on his ship, request to go to Bloodsplatter Isle.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, -2),
        Location:new(0, 0, -4),
        Location:new(0, 0, -6),
        Location:new(0, 480, -7.5),
        Location:new(0, 480, -8),
        Location:new(-1, 480, -9),
      }, { instance = true }),
      billHl,
      Action.ConversationHighlight:new("Can you take me to Bloodsplatter Isle?"),
    },
    postconditions = { Condition.ConversationText:new("Signal me") },
  },
  {
    text = "Walk in to the middle of the factory building in order to get caught.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 1),
        Location:new(22, 960, 1),
        Location:new(22, 960, 4),
        Location:new(28, 960, 10),
        Location:new(28, 960, 20),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(33, 960, 2, 3, true) },
  },
  {
    text = "Escape your cell and Barrelchest parts (abscond with shelf)",
    actions = {
      Action.Direction:new(33, 960, 5, { instance = true, tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(barrelParts) },
  },
  {
    text = "Get a surgical mask.",
    actions = {
      Action.Direction:new(35, 960, 6, { instance = true, tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(surgicalMask) },
  },
  {
    text = "Get a roll of bandage.",
    actions = {
      Action.Direction:new(33, 960, 8, { instance = true, tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(bandages) },
  },
  {
    text = "Get a barrel from the stack",
    actions = {
      Action.Direction:new(35, 960, 8, { instance = true, tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(barrel) },
  },
  {
    text = "Return to Bill by using the signalling point at the end of the dock where you arrived.",
    actions = {
      Action.PathGuide:new({
        Location:new(34, 960, 8),
        Location:new(34, 960, 6),
        Location:new(23, 960, 6),
        Location:new(23, 960, 2),
        Location:new(22, 960, 1),
        Location:new(-1, 0, 1),
      }, { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Get the Barrelchest disguise from Bill.",
    actions = {
      billHl,
      Action.ConversationHighlight:new("Cap'n...I've a problem."),
      Action.ConversationHighlight:new("I've gathered the things you asked for."),
    },
    postconditions = { Condition.ConversationText:new("Ye should be able to wander") },
  },
  {
    text = "Talk to Bill again and have him send you back to the island.",
    actions = { billHl, Action.ConversationHighlight:new("Can you take me to Bloodsplatter Isle?") },
  },
  {
    text = "Enter the the factory and take 3 barrels from the stack and 3 gunpowder from the barrel - where you picked up the supplies for the Barrelchest disguise.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 1),
        Location:new(22, 960, 1),
        Location:new(22, 960, 4),
        Location:new(24, 960, 6),
        Location:new(34, 960, 6),
        Location:new(34, 960, 7),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(34, 960, 7, 0, true) },
  },
  {
    actions = {
      Action.Direction:new(35, 960, 7, { instance = true, tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(gunpowder, 3) },
  },
  {
    actions = {
      Action.Direction:new(35, 960, 8, { instance = true, tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(barrel, 3) },
  },
  {
    text = "Change into the Barrelchest disguise. Must be 3x3 clear around your character.",
    title = "Collecting evidence",
    neededItems = { ["Barrelchest disguise"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(disguise) },
    postconditions = { Condition.ModelVisible:new(disguiseCharacter) },
  },
  {
    text = "Letter One: Go to the south-west room, sabotage the southern gas canister. 'Tell-joke' to each of the Dis-orderly and then brutalise each of them. Investigate the noticeboard.",
    actions = {
      Action.PathGuide:new({
        Location:new(28, 960, 12),
        Location:new(28, 960, 23),
        Location:new(21, 960, 23),
        Location:new(28, 960, 12),
      }, { instance = true }),
      Action.ModelHighlight:new(gascanister, { distance = 10, instanced = true }),
    },
    postconditions = { Condition.ChatText:new("The room is flooded") },
    warning = "Ensure chat text is on or filtered for automatic progression in the plugin.",
  },
  {
    actions = {
      Action.ModelHighlight:new(disorderly, { highlightPriority = "all" }),
    },
    postconditions = { Condition.ConversationText:new("I should be free") },
  },
  {
    actions = {
      Action.Direction:new(26, 960, 18, { instance = true, tile = true }),
    },
    postconditions = { Condition.InventoryContains:new(evidenceLetter) },
  },
  {
    text = "Letter Two: Kill each of Grimterns in the western-most room. Investigate the noticeboard after killing them.",
    actions = {
      Action.ModelHighlight:new(grimterm, { highlightPriority = "all" }),
    },
    postconditions = { Condition.ConversationText:new("Phew. That's the last of them") },
    warning = "Automatic progression might be inconsistent",
  },
  {
    postconditions = { Condition.ConversationText:new("You find a suspicious letter") },
  },
  {
    text = "Letter Three: Talk to one of the drunk zombies lying on the beds in the north-west room, then investigate the noticeboard.",
    warning = "No guidance on this step.",
    postconditions = { Condition.ConversationText:new("You find a suspicious letter") },
  },
  {
    text = "Letter Four: Loosen each of the three undead arms in the north-east room, then inspect one of the arms, finally investigate the noticeboard.",
    actions = { Action.ModelHighlight:new(arms, { highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("Ouch, that looked painful") },
    warning = "Highlighting is cluncky on this step.",
  },
  {
    postconditions = { Condition.ConversationText:new("You find a suspicious letter") },
  },
  {
    text = "Letter Five: This step requires at least 300 life points. Use the three gunpowder on the barrels in your backpack. Then in the south-eastern room, finish-building on each of the three incomplete barrelchest stations, then detonate one of them to destroy all three. Finally, investigate the noticeboard.",
    actions = { Action.InventoryHighlight:new(gunpowder), Action.InventoryHighlight:new(barrel) },
    postconditions = { Condition.InventoryContains:new(gunpowderBarrel, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(incompleteChest, { highlightPriority = "all" }) },
    postconditions = { Condition.ModelNotVisible:new(incompleteChest) },
  },
  {
    actions = { Action.ModelHighlight:new(riggedChest, { highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationText:new("You find a suspicious letter") },
  },
  {
    text = "Change out of the barrelchest disguise from your backpack. Changing in the middle of the factory gets you caught.",
    actions = { Action.InventoryHighlight:new(disguise) },
    postconditions = { Condition.ModelNotVisible:new(disguiseCharacter) },
  },
  {
    text = "Return back to Bill by signalling him from the dock to the south.",
    actions = {
      Action.PathGuide:new({
        Location:new(29, 960, 6),
        Location:new(23, 960, 6),
        Location:new(23, 960, 2),
        Location:new(22, 960, 1),
        Location:new(-1, 0, 1),
      }, { instance = true }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Bill onboard his ship and show him the evidence files. Then return to Bloodsplatter Isle (without disguise)",
    title = "Destroying the factory construction line",
    neededItems = { ["Evidence file"] = { quantity = 1 } },
    actions = { billHl, Action.ConversationHighlight:new("Can you take me to Bloodsplatter Isle?") },
  },
  {
    text = "Head back into the factory, enter the easternmost room and pull the lever.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(1, 0, 1),
        Location:new(22, 960, 1),
        Location:new(22, 960, 4),
        Location:new(29, 960, 11),
        Location:new(29, 960, 21),
        Location:new(41, 960, 21),
        Location:new(41, 960, 19),
        Location:new(44, 960, 16),
      }, { instance = true }),
      Action.Direction:new(45, 960, 16, { instance = true, tile = true }),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.DistanceTo:new(44, 960, 16, 0, true) },
  },
  {
    text = "Left-click the conveyor belts to load Bundle o' kegs onto them.<ul><li>Continue loading the conveyor belts until each of the conveyor belts explode.</li><li>You can safely ignore the zombies that make it to you; killing them does nothing and they automatically die when all three belts explode.</li></ul>",
    postconditions = { Condition.ModelVisible:new(destroyedConveyer) },
  },
  {
    text = "Jump over the destroyed wall between the conveyor lines, and head east through the door opening to the docks.",
    actions = { Action.ModelHighlight:new(smallWall) },
    title = "Battle at sea",
    postconditions = { Condition.DistanceTo:new(44, 960, 24, 0, true) },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(44, 960, 24),
        Location:new(44, 960, 27),
        Location:new(49, 960, 27),
        Location:new(51, 960, 25),
        Location:new(74, 0, 25),
      }, { instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(dontknowName) },
  },
  {
    text = "Board the unoccupied zomboat and sink all the boats.<ul><li>Fire cannonballs at the boats either by left-clicking on them, or using the minigame menu.</li><li>Don't forget to repair your boat when it gets to about 25% health - it is not instant and you can only repair while your boat is at a stand still.</li></ul>",
    actions = { Action.ModelHighlight:new(boat) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "After you sink all of the other boats, climb up the east ladder and talk to Bill on his ship.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(-2, 0, -2),
        Location:new(0, 0, -4),
        Location:new(0, 0, -6),
        Location:new(0, 480, -7.5),
        Location:new(0, 480, -8),
        Location:new(-1, 480, -9),
      }, { instance = true }),
      billHl,
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Head back to the basement in Joe's pub.",
    actions = {
      Action.Direction:new(3669, 645, 2995, { tile = true }),
    },
    postconditions = { Condition.DistanceTo:new(3669, 645, 2995, 5) },
  },
  {

    actions = {
      Action.Direction:new(3669, 645, 2995, { tile = true }),
      Action.ConversationHighlight:new("A Long Drop."),
    },
    postconditions = { Condition.ModelVisible:new(bill) },
  },
  {
    text = "Talk to Bill to finish the quest.",
    actions = { billHl },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "A Clockwork Syringe",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.long,
  releaseDate = 1301270400,
  prereqQuests = { "Rocking Out" },
  questReqs = {
    Types.QuestReq.skill("Construction", 62),
    Types.QuestReq.skill("Defence", 76),
    Types.QuestReq.skill("Dungeoneering", 50),
    Types.QuestReq.skill("Slayer", 61),
    Types.QuestReq.skill("Summoning", 65),
    Types.QuestReq.skill("Thieving", 74),
    Types.QuestReq.skill("Smithing", 74),
  },
  combatNPCs = {
    ["Barrelchest MK II"] = {
      level = "98",
      quantity = 4,
    },
  },
  neededItems = {
    ["Fishbowl Helmet"] = { quantity = 1, model = Models.items["fishbowl helmet"], duringQuest = true },
    ["Diving apparatus"] = { quantity = 1, model = Models.items["diving apparatus"], duringQuest = true },
  },
  recommendedItems = {
    ["Combat gear"] = { quantity = 1 },
    ["Ectophial"] = { quantity = 1 },
    ["Dungeoneering cape"] = { quantity = 1 },
    ["Salve amulet (e)"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
})
