local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local prizedCow = Model.new(5706, {
  [1] = Vertex.new(-24, 263, -313, 192, 169, 58),
  [100] = Vertex.new(-100, 556, -186, 112, 38, 33),
})

local superEgg = Model.new(246, {
  [1] = Vertex.new(44, 12, 0, 110, 87, 69),
  [2] = Vertex.new(36, 12, 24, 110, 87, 69),
  [3] = Vertex.new(28, 0, 12, 110, 87, 69),
  [4] = Vertex.new(-28, 44, -44, 115, 91, 72),
  [5] = Vertex.new(0, 44, -52, 115, 91, 72),
})

local topQualityMilk = Model.new(669, {
  [1] = Vertex.new(-68, 0, 8, 161, 148, 147),
})

local extraFineFlour = Model.new(492, {
  [1] = Vertex.new(88, 8, 16, 162, 149, 148),
})

local millie = Model.new(3120, {
  [1] = Vertex.new(-44, 392, -28, 128, 118, 117),
})

local ladderGroundFloor = Model.new(504, {
  [194] = Vertex.new(6514, 3392, 6059, 127, 127, 127),
  [196] = Vertex.new(6514, 3392, 6059, 127, 127, 127),
  [200] = Vertex.new(6515, 3357, 6059, 127, 127, 127),
  [209] = Vertex.new(6514, 3392, 6059, 127, 127, 127),
  [215] = Vertex.new(6451, 3391, 6059, 127, 127, 127),
})
local ladderMiddleFloor = Model.new(432, {
  [303] = Vertex.new(6497, 4656, 6034, 127, 127, 127),
  [306] = Vertex.new(6497, 4656, 6034, 127, 127, 127),
  [311] = Vertex.new(6433, 4655, 6034, 127, 127, 127),
  [313] = Vertex.new(6497, 4656, 6034, 127, 127, 127),
  [321] = Vertex.new(6497, 4619, 6034, 127, 127, 127),
})
local ladderTopFloor = Model.new(1536, {
  [303] = Vertex.new(6497, 4656, 6034, 127, 127, 127),
  [306] = Vertex.new(6497, 4656, 6034, 127, 127, 127),
  [311] = Vertex.new(6433, 4655, 6034, 127, 127, 127),
  [313] = Vertex.new(6497, 4656, 6034, 127, 127, 127),
  [321] = Vertex.new(6497, 4619, 6034, 127, 127, 127),
})
local millGrinder = Model.new(3384, {
  [1835] = Vertex.new(-79, -56, 504, 127, 127, 127),
  [1969] = Vertex.new(319, -56, -494, 127, 127, 127),
  [2927] = Vertex.new(253, 495, 428, 127, 127, 127),
  [2953] = Vertex.new(226, 374, 502, 127, 127, 127),
  [2965] = Vertex.new(-14, 374, 520, 127, 127, 127),
})

local hopperControls = Model.new(480, {
  [329] = Vertex.new(-108, 644, 124, 57, 47, 36),
  [332] = Vertex.new(-108, 644, 124, 57, 47, 36),
  [398] = Vertex.new(-108, 644, 124, 57, 47, 36),
  [410] = Vertex.new(76, 644, 124, 57, 47, 36),
  [414] = Vertex.new(100, 652, 100, 57, 47, 36),
})

local flourBin = Model.new(204, {
  [1] = Vertex.new(357, 183, -362, 139, 140, 128),
  [6] = Vertex.new(482, 183, -108, 139, 140, 128),
  [25] = Vertex.new(-357, 183, -362, 139, 140, 128),
  [31] = Vertex.new(-482, 183, -108, 139, 140, 128),
  [33] = Vertex.new(-357, 183, -362, 139, 140, 128),
})

local steps = {
  {
    title = "Start the quest",
    text = "Talk to the Cook in Lumbridge Castle. If you don't have a pot, one can be found on the table near the chef.",
    actions = {
      Action.Direction:new(3209, 1477, 3215),
    },
    postconditions = {
      Condition.DistanceTo:new(3209, 1477, 3215, 8),
    },
    neededItems = {
      ["Pot"] = {
        model = Models.items["pot"],
        desiredQuantity = 1,
      },
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.items["pot"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["pot"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["lumbridge cook"]),
      Action.ConversationHighlight:new("What's wrong?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("I've marked some places on your world map in red.") },
  },
  {
    title = "Obtaining the ingredients",
    text = "Go to the cow pen and get some top-quality milk by using a bucket on the prized dairy cow.",
    actions = {
      Action.Direction:new(3262, 821, 3278),
    },
    postconditions = {
      Condition.DistanceTo:new(3262, 821, 3278, 10),
    },
    neededItems = {
      ["Bucket"] = {
        model = Models.items["bucket"],
        desiredQuantity = 1,
      },
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.items["bucket"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["bucket"]) },
  },
  {
    actions = { Action.ModelHighlight:new(prizedCow) },
    postconditions = { Condition.InventoryContains:new(topQualityMilk) },
  },
  {
    text = "Head west across the river to the chicken farm and pick up the super large egg.",
    actions = { Action.Direction:new(3204, 965, 3287) },
    postconditions = { Condition.DistanceTo:new(3204, 965, 3287, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(superEgg) },
    postconditions = { Condition.InventoryContains:new(superEgg, 1) }, -- does not work consistently
  },
  {
    text = "Head west to the mill and pick one bundle of wheat.",
    actions = { Action.Direction:new(3162, 1149, 3295) },
    postconditions = { Condition.DistanceTo:new(3162, 1149, 3295, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.objects["wheat in ground"], { highlightPriority = "closest" }),
    },
    postconditions = {
      Condition.InventoryContains:new(Models.items["wheat"]),
    },
  },
  {
    text = "Talk to Millie Miller.",
    actions = { Action.Direction:new(3169, 1509, 3306) },
    postconditions = { Condition.ModelVisible:new(millie) },
  },
  {
    actions = {
      Action.ModelHighlight:new(millie),
      Action.ConversationHighlight:new("I'm looking for extra fine flour."),
      Action.ConversationHighlight:new("I'm fine, thanks."),
    },
    postconditions = { Condition.ConversationText:new("Really? How marvellous!") },
  },
  {
    text = "Go up to the top of the mill.",
    actions = {
      Action.ModelHighlight:new(ladderGroundFloor),
      Action.ModelHighlight:new(ladderMiddleFloor),
    },
    postconditions = {
      Condition.ModelVisible:new(millGrinder), -- does not work consistently
    },
  },
  {
    text = "Use the wheat on the hopper.",
    actions = {
      Action.InventoryHighlight:new(Models.items["wheat"]),
      Action.Direction:new(3165.7, 4665, 3306.5),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(Models.items["wheat"]),
    },
  },
  {
    text = "Operate the hopper controls.",
    actions = {
      Action.ModelHighlight:new(hopperControls),
      Action.Direction:new(3165, 4165, 3305),
    },
    postconditions = {
      Condition.ModelVisible:new(hopperControls, { animated = true }), -- TODO: sometimes hopper controls are visible sometimes not. Perhaps a check for animated?
    },
  },
  {
    text = "Climb down the two ladders and take the flour from the flour bin with an empty pot.<ul><li>If you don't have a pot, there is one on the table nearby.</li></ul>",
    actions = { Action.ModelHighlight:new(ladderTopFloor) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3165, 3333, 3307, 0),
      Condition.DistanceToWithHeight:new(3165, 1509, 3307, 0),
    },
  },
  {
    actions = { Action.ModelHighlight:new(ladderMiddleFloor) },
    postconditions = { Condition.DistanceToWithHeight:new(3165, 1509, 3307, 0) },
  },
  {
    actions = { Action.ModelHighlight:new(flourBin) },
    postconditions = { Condition.InventoryContains:new(extraFineFlour) },
  },
  {
    title = "Completion",
    text = "Report back to the cook.",
    actions = { Action.Direction:new(3209, 1477, 3215) },
    postconditions = { Condition.DistanceTo:new(3209, 1477, 3215, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["lumbridge cook"]),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Cook's Assistant",
  steps = steps,
  members = false,
  length = Enums.length.short,
  timeline = Enums.timeline.pathfinder,
  releaseDate = 978566400000,
  neededItems = {
    ["Pot"] = { quantity = 1, model = Models.items["pot"], duringQuest = true },
    ["Bucket"] = { quantity = 1, model = Models.items["bucket"], duringQuest = true },
  },
})
