local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--NPCs
local kili = NPCs["kili"]

--Quest Items
local kilisTools = Model.new(561, {
  [1] = Vertex.new(72, 0, 100, 89, 89, 81),
  [2] = Vertex.new(72, -4, 96, 89, 89, 81),
  [3] = Vertex.new(76, 4, 92, 89, 89, 81),
  [4] = Vertex.new(64, 4, 100, 89, 89, 81),
  [5] = Vertex.new(68, 8, 104, 89, 89, 81),
})
local toolsOnPedestal = Model.new(561, {
  [1] = Vertex.new(72, 958, 100, 90, 89, 82),
}) --Used to determine if current ritual is the quest ritual since you can't drop the tools, but they are visible on the pedestal

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Kili by the anvils in north-eastern corner of the City of Um.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered'.</li></ul>",
    title = "Starting out",
    actions = { Action.Direction:new(1146, 5445, 1807) },
    postconditions = { Condition.DistanceTo:new(1146, 5445, 1807, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(kili) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation to get Kili's tools.",
    actions = {},
    postconditions = {
      Condition.InventoryContains:new(kilisTools),
      Condition.ConversationText:new("Bye for now then!"),
    },
  },
  {
    text = "Go to the ritual site in the City of Um.",
    title = "Lesser ensoul material ritual",
    neededItems = {
      ["Kili's tools"] = { quantity = 1, model = kilisTools },
      ["Basic ritual candle"] = { quantity = 4 },
      ["Basic ghostly ink"] = { quantity = 7 },
      ["Regular ghostly ink"] = { quantity = 2 },
    },
    actions = { Action.Direction:new(1038, 6789, 1776) },
    postconditions = { Condition.DistanceTo:new(1038, 6789, 1776, 12) },
  },
  {
    text = "Select the Lesser ensoul material (Kili Row) with the pedestal.",
    actions = { Action.ModelHighlight:new(Objects["necromancy ritual pedestal"]) },
    postconditions = {
      Condition.ModelVisible:new(toolsOnPedestal),
      Condition.ModelVisible:new(kilisTools),
    },
  },
  {
    text = "Complete the ritual.",
    actions = { Action.ModelHighlight:new(Objects["necromancy ritual platform"]) },
    postconditions = { Condition.ChatText:new("1 x Kili's tools (ensouled)") }, --needs more testing
  },
  {
    text = "Withdraw the output from the chest.",
    actions = { Action.ModelHighlight:new(Objects["necromancy ritual chest"]) },
    postconditions = { Condition.InventoryContains:new(kilisTools) },
  },
  {
    text = "Go back to Kili to get a soul urn.",
    title = "Soul urn",
    actions = { Action.Direction:new(1146, 5445, 1807) },
    postconditions = { Condition.DistanceTo:new(1146, 5445, 1807, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(kili) },
    postconditions = { Condition.ConversationText:new("Perfect. Come back to me when you've filled the urn.") },
  },
  {
    text = "Kill five chickens using Necromancy or go back to the ritual site and perform a lesser communion ritual.",
    actions = { Action.ModelHighlight:new(NPCs["any chicken"]) },
    postconditions = { Condition.ChatText:new("Your soul urn tightly shuts as it's filled with the last soul.") },
  },
  {
    text = "Talk to Kili.",
    title = "Finishing up",
    actions = { Action.Direction:new(1146, 5445, 1807) },
    postconditions = { Condition.DistanceTo:new(1146, 5445, 1807, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(kili) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Kili Row",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1691366400,
  prereqQuests = { "Necromancy!" },
  questReqs = { Types.QuestReq.skill("Necromancy", 20) },
  neededItems = {
    ["Regular ghostly ink"] = { quantity = 2, model = Items["regular ghostly ink"] },
    ["Basic ghostly ink"] = { quantity = 7, model = Items["basic ghostly ink"] },
    ["Basic ritual candle"] = { quantity = 4, model = Items["basic ritual candle"] },
  },
  recommendedItems = {
    ["Bones"] = { quantity = 11 },
    ["Basic ghostly ink"] = { quantity = 11 },
    ["Necromancy weapons if killing chickens"] = { quantity = 1 },
  },
  combatNPCs = { ["Chickens"] = { level = "1", optional = true, quantity = 5 } },
})
