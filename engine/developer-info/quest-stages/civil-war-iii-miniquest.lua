local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local polletix = Model.new(11634, {
  [946] = Vertex.new(-22, 679, 19, 128, 128, 128),
  [949] = Vertex.new(-22, 679, 19, 128, 128, 128),
  [1130] = Vertex.new(22, 679, 19, 128, 128, 128),
  [11300] = Vertex.new(20, 634, -37, 127, 127, 127),
  [11569] = Vertex.new(-20, 634, -37, 127, 127, 127),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Anne Dimitri, north of Quercus in the Wilderness.",
    title = "Walkthrough",
    neededItems = {},
    recommendedItems = { ["Sand seed/mystical sand seed"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3136, 933, 3535, { distance = 23 }),
      Action.ModelHighlight:new(Models.npcs["anne dimitri"], { distance = 24 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Anne Dimitri.",
    actions = { Action.ModelHighlight:new(Models.npcs["anne dimitri"]) },
    postconditions = { Condition.ConversationText:new("Het's Oasis") },
  },
  {
    text = "Talk to Polletix in the Garden of Kharid, north of Al Kharid.",
    actions = {
      Action.Direction:new(3323, 1849, 3308, { distance = 23 }),
      Action.ModelHighlight:new(polletix, { distance = 24 }),
      Action.ConversationHighlight:new("How's it going? (Civil War III)"),
    },
    postconditions = {
      Condition.ConversationText:new("get back to it"),
      Condition.ConversationText:new("texture of the leaves like"), --not tested
    },
  },
  {
    text = "Complete the Power Planter I achievement by earning 250 Crux Eqal favor and purchasing the first unlock in Sydekix's Shop of Balance.",
    postconditions = {
      Condition.ChatText:new("Power Planter I"),
      Condition.ConversationText:new("texture of the leaves like"), --not tested
    },
  },
  {
    text = "Return to Polletix.",
    actions = {
      Action.Direction:new(3323, 1849, 3308, { distance = 23 }),
      Action.ModelHighlight:new(polletix, { distance = 24 }),
      Action.ConversationHighlight:new("How's it going? (Civil War III)"),
    },
    postconditions = { Condition.ConversationText:new("give her an update") },
  },
  {
    text = "Talk to Anne Dimitri.",
    actions = {
      Action.Direction:new(3136, 933, 3535, { distance = 23 }),
      Action.ModelHighlight:new(Models.npcs["anne dimitri"], { distance = 24 }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Continue talking to Anne Dimitri.",
    actions = { Action.ModelHighlight:new(Models.npcs["anne dimitri"]) },
    postconditions = { Condition.ConversationText:new("we'll meet again") },
  },
  {
    text = "Talk to Adrasteia in the White Knights' Castle's throne room.",
    actions = {
      Action.Direction:new(2993, 2725, 3341, { distance = 11 }),
      Action.ModelHighlight:new(Models.objects["falador ground floor ladder"], { distance = 12 }),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2993, 3909, 3341, 3),
      Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5), --already in throne room
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["falador first floor staircase"]) },
    postconditions = { Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["adrasteia"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Civil War III (miniquest)",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1667779200,
  prereqQuests = { "Civil War II (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Farming", 50),
  },
  neededItems = {},
  recommendedItems = { ["Sand seed/mystical sand seed"] = { quantity = 1 } },
  combatNPCs = {},
})
