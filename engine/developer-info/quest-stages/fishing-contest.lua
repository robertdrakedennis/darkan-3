local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local vestri = Model.new(6501, {
  [627] = Vertex.new(-242, 173, -147, 152, 112, 30),
  [740] = Vertex.new(-241, 188, -127, 152, 112, 30),
  [747] = Vertex.new(-242, 177, -147, 152, 112, 30),
  [5567] = Vertex.new(49, 480, -87, 74, 70, 67),
  [5586] = Vertex.new(-46, 481, -85, 74, 70, 67),
})
local bonzo = Model.new(4431, {
  [2857] = Vertex.new(-2, 725, -59, 50, 40, 32),
  [2862] = Vertex.new(-7, 724, -51, 50, 40, 32),
  [2865] = Vertex.new(2, 725, -59, 50, 40, 32),
  [2867] = Vertex.new(7, 724, -51, 50, 40, 32),
  [3883] = Vertex.new(0, 735, -7, 27, 139, 126),
})

-- Objects
local pipe = Model.new(1128, {
  [681] = Vertex.new(-184, 468, 88, 52, 68, 69),
  [683] = Vertex.new(-184, 468, -96, 52, 68, 69),
  [704] = Vertex.new(-184, 424, -48, 42, 45, 46),
  [716] = Vertex.new(-184, 672, -112, 63, 67, 69),
  [1010] = Vertex.new(-184, 704, -60, 70, 74, 76),
})

-- Quest Items
local fishingPass = Model.new(117, {
  [1] = Vertex.new(0, 0, 32, 127, 122, 116),
  [2] = Vertex.new(0, 0, -32, 127, 122, 116),
  [3] = Vertex.new(-104, 0, 0, 127, 122, 116),
  [4] = Vertex.new(104, 0, 0, 127, 122, 116),
  [7] = Vertex.new(104, 12, 64, 129, 114, 98),
})
local rawGiantCarp = Model.new(312, {
  [1] = Vertex.new(-156, -20, -4, 71, 65, 65),
  [2] = Vertex.new(-136, -20, 0, 71, 65, 65),
  [3] = Vertex.new(-144, -20, 8, 71, 65, 65),
  [4] = Vertex.new(-156, 20, -4, 71, 65, 65),
  [5] = Vertex.new(-144, 20, 8, 71, 65, 65),
})
local fishingTrophy = Model.new(468, {
  [1] = Vertex.new(0, 64, -52, 168, 166, 50),
  [2] = Vertex.new(-32, 64, -32, 168, 166, 50),
  [3] = Vertex.new(0, 64, 0, 168, 166, 50),
  [5] = Vertex.new(-32, 52, -32, 152, 132, 30),
  [8] = Vertex.new(0, 52, -52, 152, 132, 30),
})

---@type QuestStep[]
local steps = {
  --#region Getting started
  {
    text = "Talk to Vestri north of the Catherby lodestone.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered'.</li></ul>",
    title = "Getting started",
    actions = { Action.Direction:new(2804, 1957, 3460) },
    postconditions = { Condition.DistanceTo:new(2804, 1957, 3460, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(vestri),
      Action.ConversationHighlight:new("I was wondering what was down that tunnel?"),
      Action.ConversationHighlight:new("Why not?"),
      Action.ConversationHighlight:new("If you were my friend I wouldn't mind it."),
      Action.ConversationHighlight:new("Well, let's be friends!"),
      Action.ConversationHighlight:new("And how am I meant to do that?"),
    },
    postconditions = {
      Condition.QuestInterfaceOpen:new(),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Finish the conversation with Vestri.",
    actions = { Action.ModelHighlight:new(vestri) },
    postconditions = { Condition.ConversationText:new("Go to Hemenster and do us proud!") },
  },
  --#endregion
  --#region Getting some help
  {
    text = "If you don't have the garlic, head to the house southwest of the Seers' Village bank.",
    title = "Getting some help",
    neededItems = {
      ["Garlic"] = { quantity = 1 },
      ["Red vine worm"] = { quantity = 3 },
    },
    actions = { Action.Direction:new(2714, 1285, 3478) },
    postconditions = {
      Condition.DistanceTo:new(2713, 1285, 3480, 2),
      Condition.InventoryContains:new(Items["garlic"]),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Items["garlic"]) },
    postconditions = { Condition.InventoryContains:new(Items["garlic"]) },
  },
  {
    text = "If you don't have the worms:<ul><li>Go to McGrubor's Wood just west of the Seers' Village lodestone and enter through the loose railing along the north fence, or use fairy ring ALS.</li><li>Run to the west side of the woods and 'check' the red-coloured vines to receive red vine worms; take at least 3.</li><li>Grab 4 dwellberries for other quests.</li></ul>",
    actions = { Action.Direction:new(2661, 1093, 3500) },
    postconditions = {
      Condition.DistanceTo:new(2661, 1093, 3500, 4),
      Condition.InventoryContains:new(Items["red vine worm"], 3),
    },
  },
  {
    actions = { Action.Direction:new(2630, 757, 3495) },
    postconditions = { Condition.InventoryContains:new(Items["red vine worm"], 3) },
  },
  {
    text = "Head to Hemenster, south-west of Seers' Village and directly west of the Ranging Guild.",
    actions = { Action.Direction:new(2643, 261, 3441) },
    postconditions = { Condition.DistanceTo:new(2643, 261, 3441, 8) },
  },
  --#endregion
  --#region The vampyre
  {
    text = "Enter the gate.",
    title = "The vampyre",
    neededItems = {
      ["Garlic"] = { quantity = 1 },
      ["Red vine worm"] = { quantity = 1 },
      ["Fishing pass"] = { quantity = 1, model = fishingPass },
    },
    actions = { Action.Direction:new(2642, 261, 3441.5) },
    postconditions = { Condition.DistanceTo:new(2640, 261, 3441, 2) },
  },
  {
    text = "Use the garlic on the eastern wall pipes by the north side.",
    actions = {
      Action.Direction:new(2638, 29, 3446),
      Action.InventoryHighlight:new(Items["garlic"]),
    },
    postconditions = { Condition.ConversationText:new("You stash the garlic in the pipe.") },
  },
  {
    text = "Either talk to Bonzo and pay him 5 coins or fish at a fishing spot to start the contest.",
    actions = {
      Action.ModelHighlight:new(bonzo),
      Action.ConversationHighlight:new("I'll enter the competition please."),
    },
    postconditions = { Condition.ConversationText:new("Your fishing competition spot is now beside the pipes.") },
  },
  {
    text = "Fish at the newly obtained fishing spot until a Raw giant carp is caught.",
    actions = { Action.Direction:new(2637, 53, 3444) },
    postconditions = { Condition.InventoryContains:new(rawGiantCarp) },
  },
  --#endregion
  --#region Finishing up
  {
    text = "Give the carp to Bonzo.",
    title = "Finishing up",
    actions = {
      Action.InventoryHighlight:new(rawGiantCarp),
      Action.ModelHighlight:new(bonzo),
      Action.ConversationHighlight:new("I have this big fish. Is it enough to win?"),
    },
    postconditions = {
      Condition.ConversationText:new("We have a new winner!"),
      Condition.InventoryContains:new(fishingTrophy),
    },
  },
  {
    text = "Report back to Austri or Vestri.",
    actions = { Action.Direction:new(2804, 1957, 3460) },
    postconditions = { Condition.DistanceTo:new(2804, 1957, 3460, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(vestri),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
  --#endregion
}

return Quest:new({
  name = "Fishing Contest",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.short,
  releaseDate = 1022544000,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Fishing", 10) },
  neededItems = {
    ["Coins"] = { quantity = 5 },
    ["Garlic"] = { quantity = 1, model = Items["garlic"], duringQuest = true },
    ["Red vine worm"] = { quantity = 3, model = Items["red vine worm"], duringQuest = true },
  },
  recommendedItems = { ["A few food if low level"] = { quantity = 1 } },
  combatNPCs = {},
})
