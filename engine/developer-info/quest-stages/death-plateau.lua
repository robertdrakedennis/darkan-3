local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Enums = require("core.enums")
local Types = require("core.types")
local Model = Types.Model
local Vertex = Types.Vertex

local denulth = Model.new(5730, {
  [1] = Vertex.new(12, 745, -55, 46, 34, 23),
})

local sabbot = Model.new(4608, {
  [1] = Vertex.new(207, 243, -24, 132, 139, 145),
})

local freda = Model.new(5769, {
  [1] = Vertex.new(-23, 460, -106, 59, 98, 115),
})

local climbing = Model.new(732, {
  [1] = Vertex.new(-25, 49, 19, 47, 36, 36),
})

local spiked = Model.new(750, {
  [1] = Vertex.new(-45, 49, 23, 47, 36, 36),
})

local dunstan = Model.new(7722, {
  [1] = Vertex.new(-69, 13, -75, 132, 97, 68),
})

local survey = Model.new(609, {
  [1] = Vertex.new(58, 16, 40, 144, 134, 110),
})

local surveyPieceTexture =
  "\xc1\x64\x3a\x78\xc3\x4b\x23\xb9\xc4\x39\x14\xe1\xc4\x32\x0d\xf1\xc5\x2d\x09\xf6\xc5\x2d\x09\xf3\xbe\x2d\x0a\xf4\xaf\x27\x07\xfb\xb6\x2c\x0a\xed\xbc\x33\x0f\xe8\xc4\x39\x14\xda\xc4\x39\x14\xd9\xbc\x33\x0f\xe9\xbe\x2d\x0a\xf4\xc5\x2d\x09\xf6\xc4\x32\x0d\xf2\xc4\x32\x0d\xe9\xc4\x39\x14\xda\xc2\x53\x2b\x9f\xc0\x7a\x4d\x4d\x93\x73\x4e\x47\x61\x50\x37\x7c\x9d\x81\x59\x2e\xb2\x93\x65\x10\xbd\x9c\x6b\x03\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x04\xa2\x85\x5c\x28\xbd\x9c\x6b\x02\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x02\x94\x7a\x54\x36\xa5\x89\x5e\x20\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xa2\x85\x5c\x27\xb6\x96\x68\x09\xb2\x93\x65\x13\xae\x90\x63\x14\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xab\x8d\x61\x17\xab\x8d\x61\x19\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xb6\x96\x68\x0a\x9d\x81\x59\x2d\xa8\x8a\x5f\x1e\xab\x8d\x61\x16\x9a\x7f\x58\x30\x83\x6c\x4a\x50\x9a\x7f\x58\x30\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x03\xb2\x93\x65\x0f\xae\x90\x63\x15\xab\x8d\x61\x1e\xbd\x9c\x6b\x07\xbd\x9c\x6b\x00\xb6\x96\x68\x09\xb2\x93\x65\x0d\xb2\x93\x65\x0c\xbd\x9c\x6b\x05\xb6\x96\x68\x09\xbd\x9c\x6b\x01\xbd\x9c\x6b\x00\xb6\x96\x68\x09\x75\x61\x42\x63\xb2\x93\x65\x11\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xbd\x9c\x6b\x00\xb2\x93\x65\x10\x75\x61\x42\x62\x6d\x5a\x3d\x6d\x33\x2a\x1d\xba\x01\x01\x00\xfe\x71\x5d\x40\x68"

local mineWall = Model.new(2787, {
  [1] = Vertex.new(-239, 792, 256, 91, 83, 83),
})

local openWall = Model.new(2436, {
  [1] = Vertex.new(-239, 792, -256, 91, 83, 83),
})

local ropeSwing = Model.new(348, {
  [1] = Vertex.new(256, 262, 256, 35, 29, 22),
})

local themap = Model.new(5787, {
  [1] = Vertex.new(57, 701, -100, 83, 69, 52),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Commander Denulth who is located north east of the Burthorpe Lodestone, south of the agility course.",
    title = "Getting started",
    actions = { Action.Direction:new(2918, 1605, 3561) },
    postconditions = { Condition.DistanceTo:new(2918, 1605, 3561, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(denulth),
      Action.ConversationHighlight:new("Do you have any quests for me?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head west past the castle and through a defensive wall, into a cave.",
    title = "The dwarves",
    actions = { Action.Direction:new(2858.5, 1589, 3579) },
    postconditions = { Condition.ModelVisible:new(sabbot) },
  },
  {
    text = "Talk to Sabbot.",
    actions = {
      Action.ModelHighlight:new(sabbot),
      Action.ConversationHighlight:new("I've been sent to look for a route to Death Plateau. Can you help?"),
    },
    postconditions = {
      Condition.ConversationText:new("Aye, see you later then."),
    },
  },
  {
    text = "Exit the cave and go to the house south-west.",
    actions = { Action.Direction:new(2269, 1013, 4751) },
    postconditions = { Condition.DistanceTo:new(2858, 1589, 3579, 5) },
  },
  {
    actions = { Action.Direction:new(2821, 3525, 3555) },
    postconditions = { Condition.DistanceTo:new(2821, 3525, 3555, 2) },
  },
  {
    text = "Talk to Freda.",
    actions = { Action.ModelHighlight:new(freda) },
    postconditions = { Condition.InventoryContains:new(climbing) },
  },
  {
    text = "Home Teleport to Burthorpe or walk east. Speak to Dunstan outside the house to the south-east of Denulth to add spikes to the climbing boots.",
    title = "Boots and Routes",
    actions = { Action.Direction:new(2925, 1333, 3551) },
    postconditions = { Condition.DistanceTo:new(2925, 1333, 3551, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dunstan),
      Action.ConversationHighlight:new("Can you put some fresh spikes on these climbing boots for me?"),
    },
    postconditions = { Condition.InventoryContains:new(spiked) },
  },
  {
    text = "Bring the spiked boots back to Freda.",
    actions = { Action.Direction:new(2821, 3525, 3555) },
    postconditions = { Condition.DistanceTo:new(2821, 3525, 3555, 2) },
  },
  {
    actions = { Action.ModelHighlight:new(freda) },
    postconditions = { Condition.InventoryContains:new(survey) },
  },
  {
    text = "Read the Survey that is received.",
    actions = { Action.InventoryHighlight:new(survey) },
    postconditions = { Condition.Generic2DVisible:new(125, 115, 45, surveyPieceTexture) },
  },
  {
    text = "Go back to Sabbot's cave and mine the wall west of him.",
    actions = { Action.Direction:new(2858.5, 1589, 3579) },
    postconditions = { Condition.ModelVisible:new(sabbot) },
  },
  {
    actions = { Action.ModelHighlight:new(mineWall) },
    postconditions = { Condition.ModelVisible:new(openWall) },
  },
  {
    text = "Proceed through the cavern's obstacles.",
    actions = { Action.ModelHighlight:new(openWall) },
    postconditions = {
      Condition.DistanceTo:new(3405, 3365, 4283, 10),
      Condition.DistanceTo:new(3422, 2245, 4280, 0),
      Condition.DistanceToWithHeight:new(3406, 2245, 4279, 10),
    }, -- we add multiple posts to ensure people don't have to go back if they stop midway through
  },
  {
    actions = { Action.Direction:new(3406, 3365, 4281) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3406, 2245, 4279, 10),
      Condition.DistanceTo:new(3422, 2245, 4280, 0),
    },
  },
  {
    actions = { Action.Direction:new(3421, 2245, 4280) },
    postconditions = { Condition.DistanceTo:new(3422, 2245, 4280, 0) },
  },
  {
    text = "Keep going through the cave.",
    actions = { Action.Direction:new(3434, 2245, 4276) },
    postconditions = {
      Condition.DistanceTo:new(3434, 2245, 4275, 0),
      Condition.DistanceTo:new(3430, 2245, 4261, 0),
      Condition.DistanceTo:new(3415, 2245, 4260, 0),
      Condition.DistanceTo:new(3417, 2245, 4252, 0),
    },
  },
  {
    actions = { Action.ModelHighlight:new(ropeSwing) },
    postconditions = {
      Condition.DistanceTo:new(3430, 2245, 4261, 0),
      Condition.DistanceTo:new(3415, 2245, 4260, 0),
      Condition.DistanceTo:new(3417, 2245, 4252, 0),
    },
  },
  {
    actions = { Action.Direction:new(3421, 2245, 4260) },
    postconditions = { Condition.DistanceTo:new(3415, 2245, 4260, 0), Condition.DistanceTo:new(3417, 2245, 4252, 0) },
  },
  {
    actions = { Action.Direction:new(3417, 2245, 4253) },
    postconditions = { Condition.DistanceTo:new(3417, 2245, 4252, 0) },
  },
  {
    text = "Exit the cave through the cave exit.",
    actions = { Action.Direction:new(3421.5, 2245, 4238.5) },
    postconditions = { Condition.DistanceTo:new(3425, 3365, 4238, 0) },
  },
  {
    actions = { Action.Direction:new(3436, 3365, 4240) },
    postconditions = { Condition.ModelVisible:new(themap) },
  },
  {
    text = "The Map will approach the player.",
    actions = {
      Action.ModelHighlight:new(themap),
      Action.ConversationHighlight:new("Prepare to die, troll!"),
    },
    postconditions = { Condition.InCombatWith:new("The Map") },
  },
  {
    text = "Kill The Map.<ul><li>The Map will not be counted as killed if you are in a group.</li></ul>",
    postconditions = { Condition.ConversationText:new("I think that takes care of him") },
  },
  {
    text = "Home teleport to Burthorpe and talk to Commander Denulth. Then watch or skip the cutscene.",
    title = "Finishing up",
    actions = { Action.Direction:new(2918, 1605, 3561) },
    postconditions = { Condition.DistanceTo:new(2918, 1605, 3561, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(denulth),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Death Plateau",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 1327968000,
  prereqQuests = {},
})
