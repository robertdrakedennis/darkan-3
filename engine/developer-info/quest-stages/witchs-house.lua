local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local harvey = Model.new(4140, {
  [999] = Vertex.new(-12, 603, -15, 127, 127, 127),
  [1101] = Vertex.new(12, 603, -15, 127, 127, 127),
  [1339] = Vertex.new(25, 601, -5, 128, 127, 127),
  [3693] = Vertex.new(-25, 601, -5, 128, 128, 128),
  [3707] = Vertex.new(25, 601, -5, 128, 128, 128),
})
local witch = Model.new(4899, {
  [1] = Vertex.new(0, 735, -7, 30, 142, 129),
  [2] = Vertex.new(0, 732, -7, 30, 142, 129),
  [3] = Vertex.new(0, 732, -8, 30, 142, 129),
  [4894] = Vertex.new(-105, 400, -25, 30, 142, 32),
  [4895] = Vertex.new(-103, 400, -27, 30, 142, 32),
})
local experimentChicken = Model.new(2214, {
  [1] = Vertex.new(-24, 88, 13, 128, 128, 128),
  [2] = Vertex.new(-34, 88, 13, 128, 128, 128),
  [3] = Vertex.new(-29, 98, 13, 128, 128, 128),
  [104] = Vertex.new(29, 98, 13, 128, 128, 128),
  [105] = Vertex.new(34, 88, 13, 128, 128, 128),
})
local experimentSpider = Model.new(2655, {
  [1] = Vertex.new(0, 384, 16, 127, 127, 127),
  [2] = Vertex.new(-8, 384, -8, 127, 127, 127),
  [3] = Vertex.new(8, 384, -8, 127, 127, 127),
  [85] = Vertex.new(-190, 95, 333, 127, 127, 127),
  [268] = Vertex.new(190, 95, 333, 127, 127, 127),
})
local experimentBear = Model.new(2802, {
  [2] = Vertex.new(-24, 357, -522, 128, 128, 127),
  [4] = Vertex.new(-25, 358, -510, 128, 128, 127),
  [5] = Vertex.new(-24, 357, -522, 128, 128, 127),
  [27] = Vertex.new(24, 357, -522, 128, 128, 127),
  [28] = Vertex.new(25, 358, -510, 128, 128, 127),
})
local experimentWolf = Model.new(6276, {
  [600] = Vertex.new(-129, 428, -138, 42, 29, 46),
  [2562] = Vertex.new(110, 380, -154, 42, 29, 46),
  [5055] = Vertex.new(129, 428, -138, 42, 29, 46),
  [6261] = Vertex.new(131, 430, -138, 45, 32, 49),
  [6264] = Vertex.new(-131, 430, -138, 45, 32, 49),
})

-- Objects

-- Quest Items
local houseKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 124, 89, 11),
  [2] = Vertex.new(-12, 16, -84, 124, 89, 11),
  [3] = Vertex.new(-20, 16, -92, 124, 89, 11),
  [6] = Vertex.new(-60, 16, -52, 124, 89, 11),
  [7] = Vertex.new(-32, 16, -48, 124, 89, 11),
})
local magnet = Model.new(138, {
  [1] = Vertex.new(48, 32, -128, 0, 0, 0),
  [2] = Vertex.new(96, 64, -96, 0, 0, 0),
  [3] = Vertex.new(96, 32, -96, 0, 0, 0),
  [5] = Vertex.new(48, 64, -128, 0, 0, 0),
  [7] = Vertex.new(16, 32, -112, 0, 0, 0),
})
local shedKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 105, 97, 96),
  [2] = Vertex.new(-12, 16, -84, 105, 97, 96),
  [3] = Vertex.new(-20, 16, -92, 105, 97, 96),
  [6] = Vertex.new(-60, 16, -52, 105, 97, 96),
  [7] = Vertex.new(-32, 16, -48, 105, 97, 96),
})
local ball = Model.new(144, {
  [2] = Vertex.new(36, 122, -36, 127, 127, 127),
  [5] = Vertex.new(51, 122, -1, 127, 127, 127),
  [6] = Vertex.new(36, 122, -36, 127, 127, 127),
  [12] = Vertex.new(36, 122, -36, 127, 127, 127),
  [38] = Vertex.new(-36, 122, -36, 127, 127, 127),
})

---@type QuestStep[]
local steps = {
  {
    title = "Starting off",
    text = "Talk to Harvey, west of Falador by the crumbling wall.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered'.<li></li>The crumbling wall requires level 5 Agility to cross.</li></ul>",
    actions = { Action.Direction:new(2925, 653, 3355) },
    postconditions = { Condition.DistanceTo:new(2925, 653, 3355, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(harvey),
      Action.ConversationHighlight:new("What's the matter?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Go to the front door of the house and look under the potted plant for the house key.",
    title = "Entering the house",
    actions = { Action.Direction:new(2892, 589, 3374) },
    postconditions = { Condition.InventoryContains:new(houseKey) },
  },
  {
    text = "Go inside the house and climb down the stairs.",
    actions = { Action.Direction:new(2892.5, 637, 3373) },
    postconditions = { Condition.DistanceTo:new(2895, 645, 3373, 2) },
  },
  {
    actions = { Action.Direction:new(2899, 645, 3375.4) },
    postconditions = { Condition.DistanceTo:new(2773, 645, 9759, 3) },
  },
  {
    text = "If you didn't bring leather gloves, search the nearby crates for a pair. You may have to search several times.",
    actions = { Action.Direction:new(2773, 645, 9755) },
    postconditions = { Condition.InventoryContains:new(Items["leather gloves"]) },
  },
  {
    text = "Equip your leather gloves and enter the gate.",
    actions = {
      Action.Direction:new(2770, 645, 9756.5),
      Action.InventoryHighlight:new(Items["leather gloves"]),
    },
    postconditions = { Condition.DistanceTo:new(2769, 645, 9756, 1) },
  },
  {
    text = "Open and search the cupboard.",
    actions = { Action.Direction:new(2765, 645, 9756.5) },
    postconditions = {
      Condition.InventoryContains:new(magnet),
      Condition.ConversationText:new("You find a magnet in the cupboard."),
    },
  },
  {
    text = "Go back up the stairs.",
    actions = { Action.Direction:new(2774, 645, 9759.5) },
    postconditions = { Condition.DistanceTo:new(2898, 645, 3375, 4) },
  },
  {
    text = "Grab the cheese off of the table.<ul><li>The leather gloves aren't necessary for the rest of the quest.</li></ul>",
    actions = { Action.ModelHighlight:new(Items["cheese"]) },
    postconditions = { Condition.InventoryContains:new(Items["cheese"]) },
  },
  {
    text = "Go to the room located on the south end of the house.",
    actions = { Action.Direction:new(2894, 645, 3367) },
    postconditions = { Condition.DistanceTo:new(2894, 645, 3366, 1) },
  },
  {
    text = "<i>Right-Click Use</i> cheese on the mouse hole on the eastern wall.<ul><li>If you accidentally eat it, it will respawn on the table.</li></ul>",
    actions = { Action.Direction:new(2895.5, 645, 3366) },
    postconditions = { Condition.ModelVisible:new(NPCs["mouse"]) },
  },
  {
    text = "<i>Use</i> the magnet on the mouse.",
    actions = { Action.InventoryHighlight:new(magnet) },
    postconditions = { Condition.ConversationText:new("There is a strange whirring noise from above the door frame.") },
  },
  {
    text = "Exit the door and make your way to the fountain, being careful not to be seen by the witch.<ul><li>Strategically walk behind the shrubs to hide, being sure to stand in the middle space of each shrub. Surge can be used; timing is key.</li></ul>",
    title = "Retrieving the ball",
    actions = { Action.Direction:new(2894, 645, 3365.5) },
    postconditions = { Condition.DistanceTo:new(2894, 645, 3363, 2) },
  },
  { --hedge 1
    actions = {
      Action.ModelHighlight:new(witch),
      Action.Direction:new(2901, 645, 3360),
    },
    postconditions = { Condition.DistanceTo:new(2901, 645, 3360, 2) },
  },
  { --hedge 2
    actions = {
      Action.ModelHighlight:new(witch),
      Action.Direction:new(2909, 645, 3360),
    },
    postconditions = { Condition.DistanceTo:new(2909, 645, 3360, 2) },
  },
  { --hedge 3
    actions = {
      Action.ModelHighlight:new(witch),
      Action.Direction:new(2917, 645, 3360),
    },
    postconditions = { Condition.DistanceTo:new(2917, 645, 3360, 2) },
  },
  { --hedge 4
    actions = {
      Action.ModelHighlight:new(witch),
      Action.Direction:new(2925, 645, 3365),
    },
    postconditions = { Condition.DistanceTo:new(2925, 645, 3365, 2) },
  },
  { --hedge 5
    actions = {
      Action.ModelHighlight:new(witch),
      Action.Direction:new(2918, 645, 3366),
    },
    postconditions = { Condition.DistanceTo:new(2918, 645, 3366, 2) },
  },
  { --hedge 6
    actions = {
      Action.ModelHighlight:new(witch),
      Action.Direction:new(2911, 645, 3366),
    },
    postconditions = { Condition.DistanceTo:new(2911, 645, 3366, 2) },
  },
  {
    text = "Check fountain for a shed key.",
    actions = { Action.Direction:new(2901, 645, 3369) },
    postconditions = {
      Condition.ConversationText:new("You discover a secret compartment with a small key inside, which you take."),
      Condition.InventoryContains:new(shedKey),
    },
  },
  {
    text = "Return to the shed and open the door.",
    actions = { Action.Direction:new(2926.5, 645, 3364) },
    postconditions = { Condition.DistanceTo:new(2927, 645, 3364, 0) },
  },
  {
    text = "Try to take the ball, the experiment will appear and attack you.",
    actions = { Action.ModelHighlight:new(ball) },
    postconditions = { Condition.ModelVisible:new(experimentChicken) },
  },
  {
    text = "Kill the Witch's experiment four times.<ul><li>All forms of the experiment/shapeshifter are weak to Water Spells.</li><li>You'll have to start over if you leave before killing the last experiment.</li></ul>",
    actions = {
      Action.ModelHighlight:new(experimentChicken),
      Action.ModelHighlight:new(experimentSpider),
      Action.ModelHighlight:new(experimentBear),
      Action.ModelHighlight:new(experimentWolf),
    },
    postconditions = { Condition.ChatText:new("You finally defeat the shapeshifter.") },
  },
  {
    text = "Take the ball.",
    actions = { Action.ModelHighlight:new(ball) },
    postconditions = { Condition.InventoryContains:new(ball) },
  },
  {
    text = "Lodestone out or get caught by the witch to teleport outside quickly. She will <b>not</b> take the ball.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(2920, 645, 3363),
      Action.ModelHighlight:new(witch),
    },
    postconditions = {
      Condition.DistanceTo:new(2891, 629, 3373, 6), --Witch tele (also covers walking out of the house)
      Condition.DistanceTo:new(2967, 1461, 3403, 4), --Falador lodestone
      Condition.DistanceTo:new(2878, 1045, 3442, 4), --Taverly lodestone
    },
  },
  {
    text = "Return and talk to Harvey.",
    actions = { Action.Direction:new(2925, 653, 3355) },
    postconditions = { Condition.DistanceTo:new(2925, 653, 3355, 8) },
  },
  { actions = { Action.ModelHighlight:new(harvey) }, postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Witch's House",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.short,
  releaseDate = 1014768000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Leather gloves"] = { quantity = 1, model = Items["leather gloves"], duringQuest = true },
    ["Cheese"] = { quantity = 1, model = Items["cheese"], duringQuest = true },
  },
  recommendedItems = {
    ["Combat gear (Magic equipment and water spells work best)"] = { quantity = 1 },
    ["Highest healing food"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Chicken"] = { level = "16", quantity = 1 },
    ["Giant spider"] = { level = "23", quantity = 1 },
    ["Bear"] = { level = "37", quantity = 1 },
    ["Wolf"] = { level = "49", quantity = 1 },
  },
})
