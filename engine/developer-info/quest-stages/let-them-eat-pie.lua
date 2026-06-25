local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

local nailsNewton = Model.new(7338, {
  [1] = Vertex.new(21, 2, 7, 54, 49, 49),
})

local flouryBin = Model.new(732, {
  [1] = Vertex.new(691, 0, -216, 87, 83, 78),
})

local maggotPieShell = Model.new(390, {
  [1] = Vertex.new(-68, 25, 54, 114, 65, 47),
})

local farmerJones = Model.new(7044, {
  [1] = Vertex.new(-127, 475, -240, 94, 90, 86),
})

local mankyCrayfish = Model.new(636, {
  [1] = Vertex.new(32, 0, -88, 52, 45, 33),
})

local stinkyPotato = Model.new(594, {
  [1] = Vertex.new(-57, 60, 20, 55, 48, 35),
})

local uncookedTerriblePie = Model.new(1248, {
  [1] = Vertex.new(31, 70, 13, 98, 104, 66),
})

local terriblePie = Model.new(1248, {
  [22] = Vertex.new(-85, 65, 54, 97, 103, 65),
  [26] = Vertex.new(-85, 65, 54, 84, 81, 53),
  [28] = Vertex.new(-49, 47, 104, 84, 81, 53),
  [32] = Vertex.new(-49, 47, 104, 97, 103, 65),
  [116] = Vertex.new(57, 50, -75, 97, 103, 65),
})

local expensiveSpices = Model.new(243, {
  [1] = Vertex.new(-32, 3, -7, 200, 197, 193),
})

local rolo = Model.new(7341, {
  [1] = Vertex.new(-156, 0, 147, 40, 33, 25),
})

local chestUpstairs = Model.new(2115, {
  [1] = Vertex.new(-71, 55, -327, 110, 101, 100),
})

local seal = Model.new(504, {
  [1] = Vertex.new(8, 69, 6, 54, 49, 49),
})

local forgedLetter = Model.new(417, {
  [1] = Vertex.new(-22, 8, 0, 181, 167, 143),
})

local steps = {
  {
    text = "Talk to Nails Newton a few steps east of the Taverley lodestone.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Taverley lodestone",
      url = "Taverley_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.Direction:new(2881, 933, 3444, { distance = 6 }),
      Action.ModelHighlight:new(nailsNewton, { distance = 6 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Go south and then east across the bridge and take some fishing bait from the ground near the bait barrel and hanging fish.",
    title = "The pie shell",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.Direction:new(2906, 317, 3412, { distance = 6 }),
      Action.ModelHighlight:new(Models.items["fishing bait"], { distance = 6 }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["fishing bait"], 1) },
  },
  {
    text = "Go south and pick some wheat.",
    actions = { Action.Direction:new(2896, 461, 3401) },
    postconditions = { Condition.InventoryContains:new(Models.items["wheat"], 1) },
  },
  {
    text = "Go north into the mill and climb the staircase.",
    actions = { Action.Direction:new(2890, 424, 3427) },
    postconditions = { Condition.DistanceTo:new(2890, 1605, 3425, 0) },
  },
  {
    text = "Put the wheat and fishing bait in the hopper and then operate it.",
    actions = {
      Action.Direction:new(2893, 1605, 3426),
      Action.InventoryHighlight:new(Models.items["wheat"]),
      Action.InventoryHighlight:new(Models.items["fishing bait"]),
    },
    postconditions = {
      Condition.ConversationText:new("I hope nobody down there"),
    },
  },
  {
    text = "Go down the staircase and left click the flour bin to obtain maggoty flour.",
    actions = { Action.Direction:new(2890, 1605, 3426) },
    postconditions = { Condition.DistanceTo:new(2890, 424, 3428, 0) },
  },
  {
    actions = { Action.ModelHighlight:new(flouryBin) },
    postconditions = { Condition.ModelNotVisible:new(flouryBin) },
  },
  {
    text = "Talk to Nails, he will give you a maggoty pie shell.",
    actions = {
      Action.Direction:new(2881, 933, 3444, { distance = 6 }),
      Action.ModelHighlight:new(nailsNewton, { distance = 6 }),
    },
    postconditions = { Condition.InventoryContains:new(maggotPieShell, 1) },
  },
  {
    text = "Go north and trade Head Farmer Jones outside of the flax field to receive a free raw potato.",
    title = "The Filling",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(farmerJones) },
    postconditions = { Condition.InventoryContains:new(Models.items["raw potato"], 1) },
  },
  {
    text = "Go east and catch a raw crayfish.",
    actions = { Action.ModelHighlight:new(Models.objects["fishing bubble"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["crayfish"], 1) },
  },
  {
    text = "Go to the flax or animal field and watch the cutscene.",
    actions = { Action.Direction:new(2888, 757, 3473) },
    postconditions = { Condition.InventoryContains:new(mankyCrayfish, 1) },
  },
  {
    text = "Talk to Nails.",
    actions = {
      Action.Direction:new(2881, 933, 3444, { distance = 6 }),
      Action.ModelHighlight:new(nailsNewton, { distance = 6 }),
    },
    postconditions = { Condition.ConversationText:new("I don't think I'll ever feel clean again") },
  },
  {
    text = "Go north-west along the mountain into the snowy area (lower snow area) and use a raw potato on the hole to receive a stinking potato.",
    actions = { Action.Direction:new(2868, 1605, 3475), Action.InventoryHighlight:new(Models.items["raw potato"]) },
    postconditions = { Condition.InventoryContains:new(stinkyPotato, 1) },
  },
  {
    text = "Use the stinking potato or manky crayfish on the maggoty pie shell to create a terrible pie (uncooked).",
    actions = { Action.InventoryHighlight:new(stinkyPotato), Action.InventoryHighlight:new(maggotPieShell) },
    postconditions = { Condition.InventoryContains:new(uncookedTerriblePie, 1) },
  },
  {
    text = "Cook the pie. There is an oven in The Pick and Lute pub which is right next to Nails Newton.",
    actions = { Action.Direction:new(2897, 837, 3440) },
    postconditions = { Condition.InventoryContains:new(terriblePie, 1) },
  },
  {
    text = "Talk to Nails.",
    actions = {
      Action.Direction:new(2881, 933, 3444, { distance = 6 }),
      Action.ModelHighlight:new(nailsNewton, { distance = 6 }),
    },
    postconditions = { Condition.ConversationText:new("Then you can take it up to Rolo and see if he swallows it") },
  },
  {
    text = "Head south of Nails and pickpocket Foppish Pierre (location is marked on the map) for expensive spices.",
    title = "Finishing Up",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(Models.npcs["foppish pierre"]) },
    postconditions = { Condition.InventoryContains:new(expensiveSpices, 1) },
  },
  {
    text = "Use the expensive spices on the pie to create a mouth-watering pie.",
    actions = { Action.InventoryHighlight:new(expensiveSpices), Action.InventoryHighlight:new(terriblePie) },
    -- The "terrible pie" and "mouth watering pie" have the same vertices so checking for "mouth watering pie" will trigger on a "terrible pie".
    -- A conversation window pops up when a user uses the spices on the terrible pie, so this should hopefully suffice.
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    text = "Go to the 1st floor [UK] - 2nd floor [US] of The Pick and Lute pub and talk to Rolo the Stout.",
    actions = { Action.Direction:new(2888, 837, 3444) },
    postconditions = { Condition.ModelVisible:new(rolo) },
  },
  {
    actions = {
      Action.ModelHighlight:new(rolo),
      Action.ConversationHighlight:new("Squishy Crust Belly Filler"),
      Action.ConversationHighlight:new("I think I've had enough, thanks."),
    },
    postconditions = { Condition.ConversationText:new("I should check back with Nails") },
  },
  {
    text = "After the cutscene, talk to Nails.",
    actions = {
      Action.Direction:new(2881, 933, 3444, { distance = 6 }),
      Action.ModelHighlight:new(nailsNewton, { distance = 6 }),
    },
    postconditions = {
      Condition.ConversationText:new("We'd best move fast, in case he decides to send for it when he comes round."),
    },
  },
  {
    text = "Go upstairs in the pub, open the chest and steal the seal.",
    actions = { Action.Direction:new(2888, 837, 3444) },
    postconditions = { Condition.ModelVisible:new(chestUpstairs) },
  },
  {
    actions = { Action.ModelHighlight:new(chestUpstairs) },
    postconditions = { Condition.InventoryContains:new(seal, 1) },
  },
  {
    text = "Talk to Nails to receive a forged letter.",
    actions = { Action.Direction:new(2890, 2053, 3443) },
    postconditions = { Condition.DistanceTo:new(2887, 837, 3443, 0) },
  },
  {
    actions = {
      Action.Direction:new(2881, 933, 3444, { distance = 6 }),
      Action.ModelHighlight:new(nailsNewton, { distance = 6 }),
    },
    postconditions = { Condition.InventoryContains:new(forgedLetter, 1) },
  },
  {
    text = "Deliver the letter to Foppish Pierre, just south of Nails.",
    actions = { Action.ModelHighlight:new(Models.npcs["foppish pierre"]) },
    postconditions = { Condition.ConversationText:new("Well that worked") },
  },
  {
    text = "Go back and talk to Nails.",
    actions = {
      Action.Direction:new(2881, 933, 3444, { distance = 6 }),
      Action.ModelHighlight:new(nailsNewton, { distance = 6 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Let Them Eat Pie",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1327968000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Raw potato"] = { quantity = 1, model = Models.items["raw potato"], duringQuest = true },
    ["Raw crayfish"] = { quantity = 1, model = Models.items["crayfish"], duringQuest = true },
    ["Wheat"] = { quantity = 1, model = Models.items["wheat"], duringQuest = true },
    ["Fishing bait"] = { quantity = 1, model = Models.items["fishing bait"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = {},
})
