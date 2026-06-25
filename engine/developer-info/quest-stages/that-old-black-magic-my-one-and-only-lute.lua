local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--NPCs
local dragithNurn = Model.new(5937, {
  [1394] = Vertex.new(-20, 196, -24, 34, 53, 44),
  [5376] = Vertex.new(-160, 384, 372, 185, 161, 119),
  [5868] = Vertex.new(-164, 372, -416, 94, 98, 103),
  [5871] = Vertex.new(-164, 372, -416, 94, 98, 103),
  [5906] = Vertex.new(-168, 360, -392, 190, 33, 18),
})

--Quest Items
local guitar = Model.new(6072, {
  [1] = Vertex.new(3, 45, 27, 127, 127, 127),
  [2] = Vertex.new(27, 44, 49, 127, 127, 127),
  [3] = Vertex.new(2, 44, 28, 127, 127, 127),
  [5] = Vertex.new(27, 43, 50, 127, 127, 127),
  [13] = Vertex.new(120, 21, 147, 127, 127, 127),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Lady Grey, behind Death in the City of Um and agree to get her guitar back.",
    title = "Help the band",
    actions = { Action.Direction:new(1113, 3845, 1775) },
    postconditions = { Condition.DistanceTo:new(1113, 3845, 1775, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["lady grey"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue speaking to Lady Grey.",
    actions = { Action.ModelHighlight:new(Models.npcs["lady grey"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Subquest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue speaking to Lady Grey.",
    actions = { Action.ModelHighlight:new(Models.npcs["lady grey"]) },
    postconditions = { Condition.ConversationText:new("I'll try to be gentle.") },
  },
  {
    text = "Travel to the Lumbridge Catacombs.",
    actions = { Action.Direction:new(3247, 965, 3198) },
    postconditions = { Condition.DistanceTo:new(3877, 2457, 5526, 8) },
  },
  {
    text = "At the end of the catacombs, kill Dragith Nurn using necromancy and pick up Lady Grey's guitar.",
    actions = { Action.Direction:new(3874, 2457, 5527) },
    postconditions = { Condition.DistanceToWithHeight:new(3869, 985, 5524, 8) },
  },
  {
    actions = { Action.Direction:new(3867, 985, 5524) },
    postconditions = { Condition.DistanceToWithHeight:new(3972, 985, 5565, 8) },
  },
  {
    actions = { Action.Direction:new(3995, 997, 5470) },
    postconditions = { Condition.ModelVisible:new(dragithNurn) },
  },
  {
    actions = { Action.ModelHighlight:new(dragithNurn) },
    postconditions = { Condition.InventoryContains:new(guitar) },
  },
  {
    text = "Return to the City of Um and speak to Lady Grey. Hand her the guitar.",
    actions = { Action.Direction:new(1113, 3845, 1775) },
    postconditions = { Condition.DistanceTo:new(1113, 3845, 1775, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["lady grey"]),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Trumpeter."),
      Action.ConversationHighlight:new("None right now."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "That Old Black Magic: My One and Only Lute",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1691366400,
  prereqQuests = { "The Blood Pact" },
  questReqs = { Types.QuestReq.skill("Necromancy", 32) },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
