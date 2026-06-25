local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local nougatBunny = Model.new(3216, {
  [36] = Vertex.new(88, 483, -33, 127, 127, 127),
  [37] = Vertex.new(88, 483, -33, 127, 127, 127),
  [40] = Vertex.new(88, 465, -27, 127, 127, 127),
  [41] = Vertex.new(88, 483, -33, 127, 127, 127),
  [58] = Vertex.new(-87, 483, -33, 127, 127, 127),
})
--#endregion
--#region Objects
local goldenEgg = Model.new(768, {
  [59] = Vertex.new(-13, 132, 6, 128, 128, 127),
  [130] = Vertex.new(-22, 113, -22, 128, 128, 127),
  [136] = Vertex.new(-22, 113, -22, 128, 128, 127),
  [143] = Vertex.new(-22, 113, -22, 128, 128, 127),
  [683] = Vertex.new(10, 132, 11, 128, 128, 127),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Nougat Bunny.",
    title = "Egg helper",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Blooming Burrow Teleport",
      url = "Blooming_Burrow_Teleport_icon.png",
    },
    actions = { Action.Direction:new(3779, 2109, 4955) },
    postconditions = { Condition.ModelVisible:new(nougatBunny, { atLocation = Location:new(3779, 2109, 4955) }) },
  },
  {
    actions = { Action.ModelHighlight:new(nougatBunny, { atLocation = Location:new(3779, 2109, 4955) }) },
    jumpconditions = { Condition.ModelNotVisible:new(nougatBunny) },
    jumpOffset = -1,
    postconditions = {
      Condition.ConversationText:new("find them all"),
      Condition.ConversationText:new("good luck"),
    },
  },
  {
    text = "Find 12 golden eggs.",
    actions = { Action.ModelHighlight:new(goldenEgg, { highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("12/12") },
  },
  {
    text = "Return to the Nougat Bunny.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Blooming Burrow Teleport",
      url = "Blooming_Burrow_Teleport_icon.png",
    },
    actions = { Action.Direction:new(3779, 2109, 4955) },
    postconditions = { Condition.ModelVisible:new(nougatBunny, { atLocation = Location:new(3779, 2109, 4955) }) },
  },
  {
    actions = { Action.ModelHighlight:new(nougatBunny, { atLocation = Location:new(3779, 2109, 4955) }) },
    jumpconditions = { Condition.ModelNotVisible:new(nougatBunny) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Best of luck") },
  },
}

return Quest:new({
  name = "Egg Helper",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1689552000,
  prereqQuests = {},
})
