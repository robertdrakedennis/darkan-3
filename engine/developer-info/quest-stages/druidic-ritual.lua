local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local kaqemeex = Model.new(6474, {
  [1] = Vertex.new(-18, 742, -28, 63, 72, 82),
})

local sanfew = Model.new(5958, {
  [1] = Vertex.new(-17, 790, -39, 149, 110, 76),
})

local spring = Model.new(546, {
  [1] = Vertex.new(7770, 1647, 4817, 127, 203, 165),
})

local livingWater = Model.multi({
  Model.new(90, {
    [1] = Vertex.new(0, 52, 0, 66, 89, 105),
  }),
  Model.new(288, {
    [1] = Vertex.new(-20, 84, 4, 134, 135, 146),
  }),
})

local wyrmwood = Model.new(2328, {
  [1] = Vertex.new(44, 9, -27, 103, 92, 41),
})

local pluckedWyrmwood = Model.new(120, {
  [1] = Vertex.new(-22, 32, 31, 50, 61, 5),
})

local fishingSpot = Model.new(846, {
  [1] = Vertex.new(252, 4, -252, 66, 173, 34),
})

local fish = Model.new(534, {
  [1] = Vertex.new(-152, 0, -9, 158, 144, 121),
})

local chiseled = Model.new(810, {
  [1] = Vertex.new(5, 11, 11, 120, 117, 110),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Kaqemeex south of the Heroes' Guild.",
    title = "Getting started",
    actions = { Action.Direction:new(2924, 1997, 3484) },
    postconditions = { Condition.DistanceTo:new(2924, 1997, 3484, 15) },
  },
  {
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ModelHighlight:new(kaqemeex),
      Action.ConversationHighlight:new("Talk about Druidic Ritual."),
      Action.ConversationHighlight:new("What do you need help with?"),
      Action.ConversationHighlight:new("I'm pretty sure I've heard that before."),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { actions = {}, postconditions = { Condition.ConversationText:new("Will do") } },
  {
    text = "Run directly south and enter the last house on the west near the Herblore icon.",
    actions = { Action.Direction:new(2917, 1093, 3438) },
    postconditions = { Condition.DistanceTo:new(2917, 1093, 3438, 5) },
  },
  {
    text = "Talk to Sanfew.",
    actions = {
      Action.ModelHighlight:new(sanfew),
      Action.ConversationHighlight:new("I've been sent to help make an ointment of imbalance for Kaqemeex."),
      Action.ConversationHighlight:new("Ok, I'll do that then."),
    },
    postconditions = { Condition.ConversationText:new("Well thank you very much!") },
  },
  {
    text = "Go north and enter the Burthorpe Slayer Cave directly east of the crayfish pond.",
    actions = { Action.Direction:new(2918, 661, 3466.5) },
    postconditions = { Condition.DistanceTo:new(2214, 1861, 4532, 10) },
    title = "Gathering ingredients",
    neededItems = { ["Vial"] = { quantity = 1, model = Models.items["vial"] } },
  },
  {
    text = "Head south and fill your vial at the subterranean spring for a vial of living water.",
    actions = { Action.Direction:new(2207, 1103, 4492) },
    postconditions = { Condition.DistanceTo:new(2207, 1103, 4492, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(spring) },
    postconditions = { Condition.InventoryContains:new(livingWater) },
  },
  {
    text = "Exit the cave.",
    actions = { Action.Direction:new(2218, 1861, 4532) },
    postconditions = { Condition.DistanceTo:new(2918, 661, 3467, 10) },
  },
  {
    text = "Head south until you find wandering wyrmwood, then take-from one of them for wandering wyrmwood.",
    actions = { Action.Direction:new(2909, 789, 3382) },
    postconditions = { Condition.DistanceTo:new(2909, 789, 3382, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(wyrmwood, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(pluckedWyrmwood) },
  },
  {
    text = "At the west part of the lake, bait the Sparkling Fishing Spot.",
    actions = { Action.Direction:new(2905, 61, 3398) },
    postconditions = { Condition.DistanceTo:new(2905, 61, 3398, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(fishingSpot) },
    postconditions = { Condition.InventoryContains:new(fish) },
  },
  {
    text = "Gather-scales from the stone fish, choosing the chisel option.",
    actions = { Action.InventoryHighlight:new(fish) },
    postconditions = { Condition.InventoryContains:new(chiseled) },
  },
  {
    text = "Talk to Sanfew for a cutscene.",
    title = "Finishing up",
    actions = { Action.Direction:new(2917, 1093, 3438) },
    postconditions = { Condition.DistanceTo:new(2917, 1093, 3438, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(sanfew) },
    postconditions = { Condition.ConversationText:new("There we go.") },
  },
  {
    actions = {},
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Druidic Ritual",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 1327968000,
  neededItems = {
    ["Vial"] = { quantity = 1, model = Models.items["vial"], duringQuest = true },
  },
})
