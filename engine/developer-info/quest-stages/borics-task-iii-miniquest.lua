local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local miningGuildDwarf = Model.new(6732, {
  [177] = Vertex.new(-241, 162, -127, 154, 115, 32),
  [183] = Vertex.new(-242, 173, -147, 154, 115, 32),
  [296] = Vertex.new(-241, 188, -127, 154, 115, 32),
  [303] = Vertex.new(-242, 177, -147, 154, 115, 32),
  [327] = Vertex.new(-156, 177, -147, 154, 115, 32),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Head down the stairs at Doric's workshop to speak to Boric.",
    title = "Mining for the Guild",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(2959, 919, 3444) },
    postconditions = { Condition.DistanceTo:new(1572, 1317, 5988, 20) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["boric"]),
      Action.ConversationHighlight:new("Do you have any Mining tasks for me?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Finish the conversation with Boric.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["boric"]),
    },
    postconditions = { Condition.ConversationText:new("Fine") }, --not tested
  },
  {
    text = "Talk to one of the dwarfs at the mining guild.",
    actions = {
      Action.Direction:new(3019, 1125, 3339, { distance = 20 }),
      Action.ModelHighlight:new(miningGuildDwarf, { distance = 20, highlightPriority = "closest" }),
      Action.ConversationHighlight:new("Boric sent me here to mine some ores."),
    },
    postconditions = { Condition.ConversationText:new("you're willing to muck") },
  },
  {
    text = "Climb down into the ladder.",
    actions = { Action.Direction:new(3019.6, 1125, 3339) },
    postconditions = { Condition.DistanceTo:new(3021, 965, 9739, 4) },
  },
  {
    text = "Mine 10 runite ore and 15 orichalcite ore.<ul><li>Boosts and perks such as Varrock armour, Shooting Star, and extra ore gained from leveling do count towards the 25 total ores.</li></ul>",
    postconditions = {
      Condition.InventoryContains:new(Models.items["runite ore"], 10),
      Condition.ChatText:new("You've gathered enough ore to satisfy the Mining Guild dwarves."),
    },
  },
  {
    postconditions = {
      Condition.InventoryContains:new(Models.items["orichalcite ore"], 15),
      Condition.ChatText:new("You've gathered enough ore to satisfy the Mining Guild dwarves."),
    },
  },
  {
    text = "Return to Boric with the ore in your inventory (cannot be noted or in an ore box).",
    actions = { Action.Direction:new(2959, 919, 3444) },
    postconditions = { Condition.DistanceTo:new(1572, 1317, 5988, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["boric"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Boric's Task III (miniquest)",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.short,
  releaseDate = 1350432000,
  prereqQuests = { "Boric's Task II (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Mining", 60),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
