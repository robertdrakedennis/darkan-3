local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local dwarvenMiner = Model.new(4419, {
  [2349] = Vertex.new(-96, 467, 45, 63, 61, 80),
  [2361] = Vertex.new(-96, 468, 45, 75, 74, 96),
  [3975] = Vertex.new(-220, 305, 56, 126, 120, 115),
  [4212] = Vertex.new(-232, 185, -154, 46, 44, 42),
  [4214] = Vertex.new(-232, 235, -185, 46, 44, 42),
})

-- Objects
-- Items
-- Quest Items

---@type QuestStep[]
local steps = {
  {
    title = "Mining some coal",
    text = "Head down the stairs behind Doric's hut to speak to Boric.",
    neededItems = { ["Coal"] = { quantity = 30, model = Items["coal"] } },
    actions = { Action.Direction:new(2959, 869, 3439) },
    postconditions = { Condition.DistanceTo:new(2959, 869, 3439, 2) },
  },
  {
    actions = { Action.Direction:new(2959, 919, 3444) },
    postconditions = { Condition.DistanceTo:new(1572, 1317, 5988, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["boric"]),
      Action.ConversationHighlight:new("Do you have any Mining tasks for me?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Finish the conversation with Boric.",
    actions = { Action.ModelHighlight:new(NPCs["boric"]) },
    postconditions = { Condition.ConversationText:new("Okay, I'll go do that now.") },
  },
  {
    text = "Head to Seers' Village coal truck mining site, west of McGrubor's Wood.<ul><li>If you don't have 20 Agility, it is quicker to use the Ardougne lodestone and run northwest.</li></ul>",
    actions = { Action.Direction:new(2584, 341, 3487) },
    postconditions = { Condition.DistanceTo:new(2584, 341, 3487, 12) },
  },
  {
    text = "Deposit 30 coal into the coal truck.<ul><li>It does not need to be mined, it can be purchased off the Grand exchange. Make sure it isn't noted.</li><li>Once you have deposited the coal, manually move to the next step.</li></ul>",
    actions = { Action.Direction:new(2584, 341, 3487) },
  },
  {
    text = "Talk to the Dwarven Miner to the south.",
    actions = {
      Action.ModelHighlight:new(dwarvenMiner),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Congratulations! You have completed: 'Boric's Task I (miniquest)' - Complete this miniquest."
      ),
      Condition.QuestComplete:new(),
    },
  },
}

return Quest:new({
  name = "Boric's Task I (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.veryshort,
  releaseDate = 1350432000,
  prereqQuests = { "What's Mine is Yours" },
  questReqs = { Types.QuestReq.skill("Mining", 30) },
  neededItems = { ["Coal"] = { quantity = 30, model = Items["coal"] } },
  recommendedItems = {},
  combatNPCs = {},
})
