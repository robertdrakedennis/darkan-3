local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local sinclairGuard = Model.new(6333, {
  [3121] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [3129] = Vertex.new(2, 725, -59, 106, 78, 54),
  [5816] = Vertex.new(-25, 758, -32, 95, 87, 87),
  [5829] = Vertex.new(4, 726, -67, 95, 87, 87),
  [5834] = Vertex.new(-4, 726, -67, 95, 87, 87),
})
local gossip = Model.new(3633, {
  [2140] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [2145] = Vertex.new(-7, 724, -51, 109, 81, 57),
  [2148] = Vertex.new(2, 725, -59, 109, 81, 57),
  [2150] = Vertex.new(7, 724, -51, 109, 81, 57),
  [3260] = Vertex.new(-30, 721, -31, 49, 38, 15),
})
local poisonSalesman = Model.new(3069, {
  [1942] = Vertex.new(-2, 725, -59, 109, 81, 57),
  [1947] = Vertex.new(-7, 724, -51, 109, 81, 57),
  [1950] = Vertex.new(2, 725, -59, 109, 81, 57),
  [1952] = Vertex.new(7, 724, -51, 109, 81, 57),
  [2696] = Vertex.new(-30, 721, -31, 123, 21, 11),
})
local carol = Model.new(4197, {
  [2682] = Vertex.new(24, 738, -38, 30, 29, 28),
  [2706] = Vertex.new(-24, 738, -38, 30, 29, 28),
  [2845] = Vertex.new(-2, 716, -57, 107, 79, 55),
  [2851] = Vertex.new(2, 716, -57, 107, 79, 55),
  [2855] = Vertex.new(6, 716, -52, 107, 79, 55),
})
local elizabeth = Model.new(4155, {
  [1731] = Vertex.new(24, 738, -38, 30, 29, 28),
  [1755] = Vertex.new(-24, 738, -38, 30, 29, 28),
  [1777] = Vertex.new(-2, 716, -57, 107, 79, 55),
  [1783] = Vertex.new(2, 716, -57, 107, 79, 55),
  [1787] = Vertex.new(6, 716, -52, 107, 79, 55),
})
local frank = Model.new(3291, {
  [1954] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [1959] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [1962] = Vertex.new(2, 725, -59, 107, 79, 55),
  [1964] = Vertex.new(7, 724, -51, 107, 79, 55),
  [2557] = Vertex.new(0, 735, -7, 28, 139, 127),
})
local david = Model.new(3093, {
  [1576] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [1581] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [1584] = Vertex.new(2, 725, -59, 107, 79, 55),
  [1586] = Vertex.new(7, 724, -51, 107, 79, 55),
  [2521] = Vertex.new(0, 735, -7, 28, 139, 127),
})
local anna = Model.new(4227, {
  [1803] = Vertex.new(24, 738, -38, 30, 29, 28),
  [1827] = Vertex.new(-24, 738, -38, 30, 29, 28),
  [1849] = Vertex.new(-2, 716, -57, 107, 79, 55),
  [1855] = Vertex.new(2, 716, -57, 107, 79, 55),
  [1859] = Vertex.new(6, 716, -52, 107, 79, 55),
})
local bob = Model.new(3183, {
  [1954] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [1959] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [1962] = Vertex.new(2, 725, -59, 107, 79, 55),
  [1964] = Vertex.new(7, 724, -51, 107, 79, 55),
  [2611] = Vertex.new(0, 735, -7, 28, 139, 127),
})
--#endregion
--#region Objects
local brokenWindow = Model.new(144, {
  [3] = Vertex.new(-216, 528, 212, 22, 96, 25, 0.4510),
  [63] = Vertex.new(-216, 823, -120, 22, 96, 25, 0.4510),
  [72] = Vertex.new(-216, 823, -144, 22, 96, 25, 0.4510),
  [75] = Vertex.new(-208, 528, 212, 22, 96, 25, 0.4510),
  [144] = Vertex.new(-208, 823, -144, 22, 96, 25, 0.4510),
})
local fountain = Model.new(432, {
  [175] = Vertex.new(6551, 812, 6432, 92, 119, 120),
  [177] = Vertex.new(6438, 812, 6544, 92, 119, 120),
  [336] = Vertex.new(6126, 1358, 6347, 183, 237, 239),
  [363] = Vertex.new(6344, 1358, 6154, 183, 237, 239),
  [366] = Vertex.new(6344, 1358, 6154, 183, 237, 239),
})
--#endregion
--#region Quest Items
local criminalsDagger = Model.new(99, {
  [1] = Vertex.new(0, 28, 68, 21, 45, 51),
  [2] = Vertex.new(0, 44, 88, 21, 45, 51),
  [3] = Vertex.new(0, 20, 88, 21, 45, 51),
  [5] = Vertex.new(0, 36, 68, 21, 45, 51),
  [8] = Vertex.new(-20, 44, 84, 21, 45, 51),
})
local pungentPot = Model.new(474, {
  [1] = Vertex.new(48, 176, -20, 108, 32, 10),
  [2] = Vertex.new(48, 160, -20, 108, 32, 10),
  [3] = Vertex.new(48, 160, 16, 108, 32, 10),
  [9] = Vertex.new(20, 160, 48, 108, 32, 10),
  [12] = Vertex.new(20, 176, 48, 108, 32, 10),
})
local criminalsThreadBlue = Model.new(144, {
  [1] = Vertex.new(-104, 4, -56, 0, 0, 0),
  [2] = Vertex.new(-56, 4, -72, 0, 0, 0),
  [3] = Vertex.new(-64, 4, -80, 0, 0, 0),
  [6] = Vertex.new(-16, 4, -72, 0, 0, 0),
  [9] = Vertex.new(-16, 4, -88, 0, 0, 0),
  [11] = Vertex.new(20, 4, -60, 0, 0, 0),
  [14] = Vertex.new(4, 4, -56, 0, 0, 0),
  [17] = Vertex.new(8, 4, -24, 0, 0, 0),
  [20] = Vertex.new(0, 4, -24, 0, 0, 0),
  [23] = Vertex.new(-20, 4, 32, 0, 0, 0),
  [97] = Vertex.new(80, 8, 116, 14, 19, 155),
  [98] = Vertex.new(48, 8, 104, 14, 19, 155),
  [99] = Vertex.new(44, 8, 108, 14, 19, 155),
  [102] = Vertex.new(-16, 8, 80, 14, 19, 155),
  [105] = Vertex.new(-20, 8, 84, 14, 19, 155),
  [108] = Vertex.new(-28, 8, 64, 14, 19, 155),
  [111] = Vertex.new(-36, 8, 64, 14, 19, 155),
  [114] = Vertex.new(-20, 8, 32, 14, 19, 155),
  [117] = Vertex.new(-28, 8, 28, 14, 19, 155),
  [120] = Vertex.new(16, 8, -24, 14, 19, 155),
})
local criminalsThreadGreen = Model.new(144, {
  [1] = Vertex.new(-104, 4, -56, 0, 0, 0),
  [2] = Vertex.new(-56, 4, -72, 0, 0, 0),
  [3] = Vertex.new(-64, 4, -80, 0, 0, 0),
  [6] = Vertex.new(-16, 4, -72, 0, 0, 0),
  [9] = Vertex.new(-16, 4, -88, 0, 0, 0),
  [11] = Vertex.new(20, 4, -60, 0, 0, 0),
  [14] = Vertex.new(4, 4, -56, 0, 0, 0),
  [17] = Vertex.new(8, 4, -24, 0, 0, 0),
  [20] = Vertex.new(0, 4, -24, 0, 0, 0),
  [23] = Vertex.new(-20, 4, 32, 0, 0, 0),
  [97] = Vertex.new(80, 8, 116, 14, 154, 18),
  [98] = Vertex.new(48, 8, 104, 14, 154, 18),
  [99] = Vertex.new(44, 8, 108, 14, 154, 18),
  [102] = Vertex.new(-16, 8, 80, 14, 154, 18),
  [105] = Vertex.new(-20, 8, 84, 14, 154, 18),
  [108] = Vertex.new(-28, 8, 64, 14, 154, 18),
  [111] = Vertex.new(-36, 8, 64, 14, 154, 18),
  [114] = Vertex.new(-20, 8, 32, 14, 154, 18),
  [117] = Vertex.new(-28, 8, 28, 14, 154, 18),
  [120] = Vertex.new(16, 8, -24, 14, 154, 18),
})
local criminalsThreadRed = Model.new(144, {
  [1] = Vertex.new(-104, 4, -56, 0, 0, 0),
  [2] = Vertex.new(-56, 4, -72, 0, 0, 0),
  [3] = Vertex.new(-64, 4, -80, 0, 0, 0),
  [6] = Vertex.new(-16, 4, -72, 0, 0, 0),
  [9] = Vertex.new(-16, 4, -88, 0, 0, 0),
  [11] = Vertex.new(20, 4, -60, 0, 0, 0),
  [14] = Vertex.new(4, 4, -56, 0, 0, 0),
  [17] = Vertex.new(8, 4, -24, 0, 0, 0),
  [20] = Vertex.new(0, 4, -24, 0, 0, 0),
  [23] = Vertex.new(-20, 4, 32, 0, 0, 0),
  [97] = Vertex.new(80, 8, 116, 153, 25, 13),
  [98] = Vertex.new(48, 8, 104, 153, 25, 13),
  [99] = Vertex.new(44, 8, 108, 153, 25, 13),
  [102] = Vertex.new(-16, 8, 80, 153, 25, 13),
  [105] = Vertex.new(-20, 8, 84, 153, 25, 13),
  [108] = Vertex.new(-28, 8, 64, 153, 25, 13),
  [111] = Vertex.new(-36, 8, 64, 153, 25, 13),
  [114] = Vertex.new(-20, 8, 32, 153, 25, 13),
  [117] = Vertex.new(-28, 8, 28, 153, 25, 13),
  [120] = Vertex.new(16, 8, -24, 153, 25, 13),
})
local silverNecklace = Model.new(672, {
  [1] = Vertex.new(12, 11, -49, 73, 68, 67),
  [2] = Vertex.new(5, 18, -43, 73, 68, 67),
  [3] = Vertex.new(7, 13, -39, 73, 68, 67),
  [5] = Vertex.new(0, 13, -39, 73, 68, 67),
  [9] = Vertex.new(8, 17, -50, 73, 68, 67),
})
local silverCup = Model.new(354, {
  [1] = Vertex.new(-20, 28, 44, 77, 65, 59),
  [2] = Vertex.new(-44, 28, -12, 77, 65, 59),
  [3] = Vertex.new(-44, 28, 20, 77, 65, 59),
  [8] = Vertex.new(12, 28, -36, 77, 65, 59),
  [11] = Vertex.new(36, 28, -12, 77, 65, 59),
})
local silverBottle = Model.new(354, {
  [1] = Vertex.new(16, 48, -40, 163, 150, 149),
  [2] = Vertex.new(32, 144, -16, 163, 150, 149),
  [3] = Vertex.new(64, 48, -32, 163, 150, 149),
  [5] = Vertex.new(16, 144, -24, 163, 150, 149),
  [7] = Vertex.new(-16, 48, -40, 163, 150, 149),
})
local silverBook = Model.new(138, {
  [1] = Vertex.new(-56, 12, -72, 148, 148, 136),
  [2] = Vertex.new(-56, 36, -72, 148, 148, 136),
  [3] = Vertex.new(56, 36, -72, 148, 148, 136),
  [6] = Vertex.new(56, 12, -72, 148, 148, 136),
  [20] = Vertex.new(64, 36, -84, 121, 111, 111),
})
local silverPot = Model.new(474, {
  [1] = Vertex.new(48, 176, -20, 121, 111, 111),
  [2] = Vertex.new(48, 160, -20, 121, 111, 111),
  [3] = Vertex.new(48, 160, 16, 121, 111, 111),
  [9] = Vertex.new(20, 160, 48, 121, 111, 111),
  [12] = Vertex.new(20, 176, 48, 121, 111, 111),
})
local silverNeedle = Model.multi({
  Model.new(144, {
    [1] = Vertex.new(-116, 8, -84, 111, 111, 121),
    [2] = Vertex.new(8, 0, -12, 111, 111, 121),
    [3] = Vertex.new(0, 0, 0, 111, 111, 121),
    [4] = Vertex.new(92, 12, 64, 111, 111, 121),
    [5] = Vertex.new(100, 0, 52, 111, 111, 121),
  }),
  Model.new(27, {
    [1] = Vertex.new(4, -8, -4, 0, 0, 0, 0.4980),
    [2] = Vertex.new(12, -8, -16, 0, 0, 0, 0.4980),
    [3] = Vertex.new(-112, -8, -88, 0, 0, 0, 0.4980),
    [4] = Vertex.new(76, -8, 48, 0, 0, 0, 0.4980),
    [8] = Vertex.new(84, -8, 32, 0, 0, 0, 0.4980),
  }),
})
local flypaper = Model.new(24, {
  [1] = Vertex.new(-128, 0, -160, 115, 105, 89),
  [2] = Vertex.new(96, 0, -80, 115, 105, 89),
  [3] = Vertex.new(128, 0, -160, 115, 105, 89),
  [5] = Vertex.new(-80, 0, -96, 115, 105, 89),
  [8] = Vertex.new(112, 0, 16, 115, 105, 89),
})
--#endregion

local testPot = Model.new(198, {
  [182] = Vertex.new(-4, 12, -108, 12, 130, 14),
  [183] = Vertex.new(-8, 12, -100, 12, 130, 14),
  [187] = Vertex.new(4, 12, -108, 12, 130, 14),
  [188] = Vertex.new(-4, 12, -108, 12, 130, 14),
  [195] = Vertex.new(4, 12, -108, 12, 130, 14),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to a guard on the Sinclair Mansion property which is located north of Camelot Castle.<ul><li>For the tracking to work properly, you need to have the chat visible, game messages set to 'On' or 'Filtered', and chat timestamps 'On'. It's also best to change to a chat channel where you aren't getting any messages from players.</li></ul>",
    title = "Getting started",
    actions = {
      Action.ModelHighlight:new(sinclairGuard, { distance = 12 }),
      Action.Direction:new(2741, 645, 3560, { distance = 12 }),
      Action.InventoryHighlight:new(testPot),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Pickup the criminal's dagger and pungent pot off the floor in the mansion.",
    title = "House of crime",
    actions = {
      Action.Direction:new(2746, 645, 3578, { distance = 8 }),
      Action.ModelHighlight:new(criminalsDagger, { distance = 8 }),
    },
    postconditions = { Condition.InventoryContains:new(criminalsDagger) },
  },
  {
    actions = { Action.ModelHighlight:new(pungentPot) },
    postconditions = {
      Condition.InventoryContains:new(pungentPot),
    },
  },
  {
    text = "Investigate the window to find a criminal's thread.",
    actions = { Action.ModelHighlight:new(brokenWindow) },
    postconditions = {
      Condition.ChatText:new("You take the thread."),
      Condition.InventoryContains:new(criminalsThreadRed),
      Condition.InventoryContains:new(criminalsThreadGreen),
      Condition.InventoryContains:new(criminalsThreadBlue),
    },
  },
  {
    text = "Search the barrels in the bedrooms owned by the following two suspects for two silver items.<ul><li>If the thread is red, search the barrels belonging to Bob to the west and Carol upstairs for a silver cup and silver bottle.</li><li>If the thread is green, search the barrels belonging to Anna downstairs and David upstairs for a silver necklace and silver book.</li><li>If the thread is blue, search the barrels belonging to Elizabeth and Frank upstairs for a silver needle and silver pot.</li></ul>",
    postconditions = { --this is here in case if the inventory checking for threads break
      Condition.ChatText:new("You take the thread."),
      Condition.InventoryContains:new(criminalsThreadRed),
      Condition.InventoryContains:new(criminalsThreadGreen),
      Condition.InventoryContains:new(criminalsThreadBlue),
    },
  },
  --Red section
  {
    -- text = "debug red 0",
    actions = { Action.Direction:new(2735, 645, 3579) },
    postconditions = {
      Condition.InventoryContains:new(silverCup),
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
    },
  },
  { --stairs
    -- text = "debug red 1",
    actions = { Action.Direction:new(2736, 645, 3581) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2736, 1637, 3580, 2),
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
    },
  },
  {
    -- text = "debug red 2",
    actions = { Action.Direction:new(2733, 1637, 3580) },
    postconditions = {
      Condition.InventoryContains:new(silverBottle),
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
    },
  },
  --Green section
  {
    -- text = "debug green 0",
    actions = { Action.Direction:new(2733, 645, 3575) },
    postconditions = {
      Condition.InventoryContains:new(silverNecklace),
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
    },
  },
  { --stairs
    -- text = "debug green 1",
    actions = { Action.Direction:new(2736, 645, 3581) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2736, 1637, 3580, 2),
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
    },
  },
  {
    -- text = "debug green 2",
    actions = { Action.Direction:new(2733, 1637, 3577) },
    postconditions = {
      Condition.InventoryContains:new(silverBook),
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
    },
  },
  --Blue section
  { --stairs
    -- text = "debug blue 0",
    actions = { Action.Direction:new(2736, 645, 3581) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2736, 1637, 3580, 2),
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
    },
  },
  {
    -- text = "debug blue 1",
    actions = { Action.Direction:new(2747, 1637, 3577) },
    postconditions = {
      Condition.InventoryContains:new(silverPot),
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
    },
  },
  {
    -- text = "debug blue 2",
    actions = { Action.Direction:new(2747, 1637, 3581) },
    postconditions = {
      Condition.InventoryContains:new(silverNeedle),
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
    },
  },
  {
    text = "Fill the empty pot from the barrel of flour from the kitchen.",
    title = "The evidence",
    neededItems = { ["Empty pot"] = { quantity = 3 } },
    actions = { Action.Direction:new(2736, 1637, 3581) },
    postconditions = { Condition.DistanceToWithHeight:new(2736, 645, 3580, 8) },
  },
  {
    actions = { Action.Direction:new(2733, 645, 3582) },
    postconditions = { Condition.InventoryContains:new(Models.items["pot of flour"], 3) },
  },
  {
    text = "Go to the gardener's shed in the very north-west of the mansion and take three flypapers from the sack.<ul><li>You have to get the flypaper one at a time.</li></ul>",
    actions = {
      Action.Direction:new(2731, 645, 3582),
      Action.ConversationHighlight:new("Yes, it might be useful."),
    },
    postconditions = { Condition.InventoryContains:new(flypaper, 3) },
  },
  {
    text = "Use the pot of flour on the dagger.",
    actions = {
      Action.InventoryHighlight:new(criminalsDagger),
      Action.InventoryHighlight:new(Models.items["pot of flour"]),
    },
    postconditions = { Condition.ChatText:new("You sprinkle a small amount of flour on the murder weapon.") },
  },
  {
    text = "Use the flypaper on the dagger to lift the fingerprint.",
    actions = {
      Action.InventoryHighlight:new(criminalsDagger),
      Action.InventoryHighlight:new(flypaper),
    },
    postconditions = { Condition.ChatText:new("You have a clean impression of the murderer's finger prints.") },
  },
  {
    text = "Repeat with the two silver objects.",
    postconditions = { Condition.ChatText:new("You have a clean impression of the murderer's finger prints.") },
  },
  --Red section: Silver cup, Silver bottle
  {
    -- text = "red debug 0",
    actions = { Action.InventoryHighlight:new(silverCup), Action.InventoryHighlight:new(Models.items["pot of flour"]) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("You sprinkle the flour on Bob's cup."),
      Condition.ChatText:new("The cup is now coated with a thin layer of flour."),
    },
  },
  {
    -- text = "red debug 1",
    actions = { Action.InventoryHighlight:new(silverCup), Action.InventoryHighlight:new(flypaper) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("You use the flypaper on the flour covered necklace."),
      Condition.ChatText:new("You have a clean impression of Bob's finger prints."),
    },
  },
  {
    -- text = "red debug 2",
    actions = {
      Action.InventoryHighlight:new(silverBottle),
      Action.InventoryHighlight:new(Models.items["pot of flour"]),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("You sprinkle the flour on Carol's bottle."),
      Condition.ChatText:new("The bottle is now coated with a thin layer of flour."),
    },
  },
  {
    -- text = "red debug 3",
    actions = { Action.InventoryHighlight:new(silverBottle), Action.InventoryHighlight:new(flypaper) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("You use the flypaper on the flour covered bottle."),
      Condition.ChatText:new("You have a clean impression of Carol's finger prints."),
    },
  },
  --Green section: Silver necklace, Silver book
  {
    -- text = "green debug 0",
    actions = {
      Action.InventoryHighlight:new(silverNecklace),
      Action.InventoryHighlight:new(Models.items["pot of flour"]),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ChatText:new("You sprinkle the flour on Anna's necklace."),
      Condition.ChatText:new("The necklace is now coated with a thin layer of flour."),
    },
  },
  {
    -- text = "green debug 1",
    actions = { Action.InventoryHighlight:new(silverNecklace), Action.InventoryHighlight:new(flypaper) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ChatText:new("You use the flypaper on the flour covered necklace."),
      Condition.ChatText:new("You have a clean impression of Anna's finger prints."),
    },
  },
  {
    -- text = "green debug 2",
    actions = { Action.InventoryHighlight:new(silverBook), Action.InventoryHighlight:new(Models.items["pot of flour"]) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ChatText:new("You sprinkle the flour on David's book."),
      Condition.ChatText:new("The Book is now coated with a thin layer of flour."),
    },
  },
  {
    -- text = "green debug 3",
    actions = { Action.InventoryHighlight:new(silverBook), Action.InventoryHighlight:new(flypaper) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ChatText:new("You use the flypaper on the flour covered book."),
      Condition.ChatText:new("You have a clean impression of David's finger prints."),
    },
  },
  --Blue section: Silver needle, Silver pot
  {
    -- text = "blue debug 0",
    actions = {
      Action.InventoryHighlight:new(silverNeedle),
      Action.InventoryHighlight:new(Models.items["pot of flour"]),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ChatText:new("You sprinkle the flour on Elizabeth's needle."),
      Condition.ChatText:new("The Needle is now coated with a thin layer of flour."),
    },
  },
  {
    -- text = "blue debug 1",
    actions = { Action.InventoryHighlight:new(silverNeedle), Action.InventoryHighlight:new(flypaper) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ChatText:new("You use the flypaper on the flour covered needle."),
      Condition.ChatText:new("You have a clean impression of Elizabeth's finger prints."),
    },
  },
  {
    -- text = "blue debug 2",
    actions = { Action.InventoryHighlight:new(silverPot), Action.InventoryHighlight:new(Models.items["pot of flour"]) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ChatText:new("You sprinkle the flour on Frank's pot"),
      Condition.ChatText:new("The Pot is now coated with a thin layer of flour."),
    },
  },
  {
    -- text = "blue debug 3",
    actions = { Action.InventoryHighlight:new(silverPot), Action.InventoryHighlight:new(flypaper) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ChatText:new("You use the flypaper on the flour covered pot."),
      Condition.ChatText:new("You have a clean impression of Frank's finger prints."),
    },
  },
  {
    text = "Compare each fingerprint by using each suspect's print with the unknown print. The unknown print will then turn to the killer's print.",
    postconditions = { Condition.ChatText:new("The finger prints are an exact match") },
  },
  {
    text = "Go to the mansion's gate and talk to Gossip.",
    actions = {
      Action.ModelHighlight:new(gossip, { distance = 12 }),
      Action.Direction:new(2742, 653, 3554, { distance = 12 }),
      Action.ConversationHighlight:new("Who do you think was responsible?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Especially as I heard that the poison salesman in the Seers' village made a big sale to one of the family the other day."
      ),
    },
  },
  {
    text = "Go to the Forester's Arms in Seers' Village and talk to the Poison Salesman (to get there quickly, teleport to the Seers' Village lodestone).",
    actions = {
      Action.Direction:new(2695, 1157, 3493, { distance = 8 }),
      Action.ModelHighlight:new(poisonSalesman, { distance = 8 }),
      Action.ConversationHighlight:new("Talk about the Murder Mystery Quest."),
      Action.ConversationHighlight:new("Who did you sell Poison to at the house?"),
    },
    postconditions = { Condition.ConversationText:new("Uh... no, it's ok.") },
  },
  {
    text = "Return to the mansion.",
    title = "A criminal revealed",
    actions = { Action.Direction:new(2741, 645, 3574) },
    postconditions = { Condition.DistanceTo:new(2741, 645, 3574, 12) },
  },
  -- Red section
  { -- Carol
    text = "Talk to the suspect with the matching fingerprint then investigate their claim.<ul><li>Carol: Drain west of the front doors.</li><li>Bob: Beehive in the garden.</li><li>David: Spiders upstairs.</li><li>Anna: Compost heap in the garden.</li><li>Frank: Family crest east of the front doors.</li><li>Elizabeth: Fountain in the garden.</li></ul>",
    actions = { Action.Direction:new(2736, 645, 3581) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("The finger prints are an exact match to Bob's."),
      Condition.DistanceToWithHeight:new(2736, 1637, 3580, 2),
    },
  },
  {
    -- text = "red debug 1",
    actions = {
      Action.ModelHighlight:new(carol),
      Action.ConversationHighlight:new("Why'd you buy poison the other day?"),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("The finger prints are an exact match to Bob's."),
      Condition.ConversationText:new("but the drain outside was"),
    },
  },
  { --down stairs
    -- text = "red debug 2",
    actions = {
      Action.Direction:new(2736, 1637, 3581),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("The finger prints are an exact match to Bob's."),
      Condition.DistanceToWithHeight:new(2736, 645, 3580, 4),
    },
  },
  {
    -- text = "red debug 3",
    actions = { Action.Direction:new(2736, 645, 3573) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ChatText:new("The finger prints are an exact match to Bob's."),
      Condition.ConversationText:new("The drain is totally blocked."),
    },
  },
  { -- Bob
    -- text = "red debug 4",
    actions = {
      Action.Direction:new(2748, 645, 3559, { distance = 8 }),
      Action.ModelHighlight:new(bob, { distance = 8 }),
      Action.ConversationHighlight:new("Why'd you buy poison the other day?"),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ConversationText:new("we had a problem with the beehive in the garden"),
    },
  },
  {
    -- text = "red debug 5",
    actions = { Action.Direction:new(2730, 629, 3559) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadRed),
      Condition.ConversationText:new("The beehive buzzes with activity."),
    },
  },
  -- Green section
  { -- David
    -- text = "green debug 0",
    actions = {
      Action.ModelHighlight:new(david, { distance = 8 }),
      Action.Direction:new(2739, 645, 3581, { distance = 8 }),
      Action.ConversationHighlight:new("Why'd you buy poison the other day?"),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ChatText:new("The finger prints are an exact match to Anna's."),
      Condition.ConversationText:new("There was a nest of spiders upstairs"),
    },
  },
  {
    -- text = "green debug 1",
    actions = { Action.Direction:new(2736, 645, 3581) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ChatText:new("The finger prints are an exact match to Anna's."),
      Condition.DistanceToWithHeight:new(2736, 1637, 3580, 2),
    },
  },
  {
    -- text = "green debug 2",
    actions = { Action.Direction:new(2740, 1637, 3574) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ChatText:new("The finger prints are an exact match to Anna's."),
      Condition.ConversationText:new("There is a spiders' nest here."),
    },
  },
  { -- Anna
    -- text = "green debug 3",
    actions = {
      Action.ModelHighlight:new(anna, { distance = 7 }),
      Action.Direction:new(2734, 645, 3575, { distance = 7 }),
      Action.ConversationHighlight:new("Why'd you buy poison the other day?"),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ConversationText:new("That useless Gardener Stanford has let his compost heap fester"),
    },
  },
  {
    -- text = "green debug 4",
    actions = { Action.Direction:new(2730.5, 645, 3572.5) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadGreen),
      Condition.ConversationText:new("The compost is teeming with maggots."),
    },
  },
  -- Blue section
  { -- Frank
    -- text = "blue debug 0",
    actions = {
      Action.ModelHighlight:new(frank, { distance = 5 }),
      Action.Direction:new(2742, 645, 3577, { distance = 5 }),
      Action.ConversationHighlight:new("Why'd you buy poison the other day?"),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ChatText:new("The finger prints are an exact match to Elizabeth's."),
      Condition.ConversationText:new("I just used a bit to clean that family crest outside up a bit"),
    },
  },
  {
    -- text = "blue debug 1",
    actions = {
      Action.Direction:new(2746, 645, 3573.2),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ChatText:new("The finger prints are an exact match to Elizabeth's."),
      Condition.ConversationText:new("It looks like the Sinclair family crest but it is very dirty."),
    },
  },
  { -- Elizabeth
    -- text = "blue debug 2",
    actions = { Action.Direction:new(2736, 645, 3581) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.DistanceToWithHeight:new(2736, 1637, 3580, 2),
    },
  },
  {
    -- text = "blue debug 3",
    actions = {
      Action.ModelHighlight:new(elizabeth),
      Action.ConversationHighlight:new("Why'd you buy poison the other day?"),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ConversationText:new("There was a nest of mosquitos under the fountain in the garden"),
    },
  },
  { --down stairs
    -- text = "blue debug 4",
    actions = { Action.Direction:new(2736, 1637, 3581) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.DistanceToWithHeight:new(2736, 645, 3580, 4),
    },
  },
  {
    -- text = "blue debug 5",
    actions = {
      Action.ModelHighlight:new(fountain, { distance = 8 }),
      Action.Direction:new(2747.5, 645, 3563.5, { distance = 8 }),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(criminalsThreadBlue),
      Condition.ConversationText:new("The fountain is swarming with mosquitos."),
      Condition.ConversationText:new("I hate mosquitos"),
    },
  },
  {
    text = "Talk to a guard.",
    title = "Finishing up",
    actions = {
      Action.ModelHighlight:new(sinclairGuard, { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("I know who did it!"),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Murder Mystery",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1055116800,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Empty pot"] = { quantity = 3, model = Models.items["pot"], duringQuest = false },
    ["Empty inventory spaces"] = { quantity = 11, maxQuantity = 16 },
  },
  recommendedItems = {},
  combatNPCs = {},
})
