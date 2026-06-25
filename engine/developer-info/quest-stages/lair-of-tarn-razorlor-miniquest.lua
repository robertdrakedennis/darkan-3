local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Objects
local passageway = Model.new(1719, {
  [1250] = Vertex.new(252, 793, -256, 0, 0, 0),
  [1445] = Vertex.new(-156, 1080, 256, 58, 58, 53),
  [1469] = Vertex.new(-212, 877, -256, 58, 58, 53),
  [1597] = Vertex.new(128, 1147, -256, 86, 90, 83),
  [1670] = Vertex.new(-212, 1080, -256, 58, 58, 53),
})
--#endregion
--#region Quest Items
local diary = Model.new(501, {
  [153] = Vertex.new(-48, 0, -120, 101, 56, 8),
  [180] = Vertex.new(-24, 16, 132, 116, 116, 107),
  [182] = Vertex.new(-48, 8, 132, 116, 116, 107),
  [222] = Vertex.new(72, 8, 88, 34, 22, 3),
  [254] = Vertex.new(24, 44, 140, 34, 22, 3),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Enter the maze in the haunted mine.",
    title = "Navigating the maze",
    actions = { Action.Direction:new(3424, 1461, 9660.5) },
    postconditions = { Condition.DistanceTo:new(3166, 993, 4547, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3166, 993, 4547),
        Location:new(3166, 993, 4549),
        Location:new(3167, 993, 4550),
        Location:new(3169, 993, 4550),
        Location:new(3170, 993, 4549),
        Location:new(3182, 993, 4549),
        Location:new(3184, 993, 4548),
        Location:new(3191, 993, 4548),
        Location:new(3191, 993, 4554),
        Location:new(3193, 993, 4554),
        Location:new(3196, 993, 4554),
        Location:new(3196, 993, 4561),
        Location:new(3196, 993, 4570),
        Location:new(3195, 993, 4570),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3195, 2145, 4575, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3195, 2145, 4575),
        Location:new(3195, 2145, 4585),
        Location:new(3176, 2145, 4585),
        Location:new(3175, 2145, 4577),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3170, 993, 4577, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3170, 993, 4577),
        Location:new(3168, 993, 4577),
        Location:new(3168, 993, 4579),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3168, 993, 4586, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3168, 993, 4586),
        Location:new(3168, 993, 4589),
        Location:new(3166, 993, 4589),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3161, 2145, 4589, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3161, 2145, 4589),
        Location:new(3155, 2145, 4589),
        Location:new(3155, 2145, 4591),
        Location:new(3156, 2145, 4592),
        Location:new(3156, 2145, 4593),
        Location:new(3155, 2145, 4594),
        Location:new(3155, 2145, 4597),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3151, 2145, 4597, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3151, 2145, 4597),
        Location:new(3150, 2145, 4596),
        Location:new(3150, 2145, 4595),
        Location:new(3148, 2117, 4595),
        Location:new(3146, 2117, 4595),
        Location:new(3144, 2117, 4595),
        Location:new(3142, 2117, 4595),
        Location:new(3140, 2117, 4595),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3140, 2117, 4595, 0) },
  },
  {
    text = "Search the floor.",
    actions = { Action.Direction:new(3138, 2145, 4595, { tile = true }) },
    postconditions = { Condition.ChatText:new("hear a click") },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3139, 2145, 4595),
        Location:new(3140, 2117, 4595),
        Location:new(3142, 2117, 4595),
        Location:new(3144, 2117, 4595),
        Location:new(3144, 2117, 4597),
        Location:new(3144, 2117, 4599),
        Location:new(3144, 2117, 4601),
        Location:new(3147, 2145, 4604),
        Location:new(3148, 2145, 4604),
        Location:new(3151, 2145, 4604),
        Location:new(3153, 2145, 4604),
        Location:new(3156, 2145, 4604),
        Location:new(3160, 2145, 4604),
        Location:new(3160, 2145, 4600),
        Location:new(3161, 2145, 4600),
        Location:new(3163, 2145, 4600),
        Location:new(3165, 2145, 4600),
        Location:new(3170, 2145, 4600),
        Location:new(3172, 2145, 4600),
        Location:new(3174, 2145, 4600),
        Location:new(3175, 2145, 4598),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3179, 2145, 4598, 4) },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3179, 2145, 4598),
        Location:new(3180, 2145, 4598),
        Location:new(3180, 2117, 4600),
        Location:new(3182, 2117, 4600),
        Location:new(3184, 2117, 4600),
        Location:new(3184, 2117, 4598),
        Location:new(3186, 2117, 4598),
        Location:new(3186, 2117, 4596),
        Location:new(3188, 2117, 4596),
        Location:new(3190, 2117, 4596),
        Location:new(3190, 2145, 4597),
        Location:new(3194, 2117, 4597),
        Location:new(3194, 2145, 4598),
      }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3189, 993, 4598, 4) },
  },
  {
    text = "Enter Tarn's lair.",
    title = "Fighting Tarn Razorlor",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "4",
      hover = "Ring of slaying",
      url = "Ring_of_slaying_(1).png",
    },
    actions = { Action.Direction:new(3185, 1293, 4601.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill both terror dogs, Mutant Tarn, and Tarn",
    postconditions = { Condition.ChatText:new("Detarnation") },
  },
  {
    text = "Head north through the passageway and take Tarn's diary.",
    actions = {
      Action.ModelHighlight:new(passageway),
      Action.ModelHighlight:new(diary),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Lair of Tarn Razorlor (miniquest)",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1169424000,
  prereqQuests = { "Haunted Mine" },
})
