local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Adrasteia in the White Knights' Castle's throne room.",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    title = "Walkthrough",
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
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Adrasteia.",
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("Safe travels") },
  },
  {
    text = "Talk to Anne Dimitri, north of Quercus in the Wilderness.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Edgeville lodestone",
      url = "Edgeville_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3136, 933, 3535, { distance = 23 }),
      Action.ModelHighlight:new(Models.npcs["anne dimitri"], { distance = 24 }),
    },
    postconditions = {
      Condition.ConversationText:new("what I can do"), --not tested
      Condition.ConversationText:new("already killed a large number"), --not tested
    },
  },
  {
    text = "Kill 144 demons in the Wilderness for the Gross Misconduct achievement if not already completed.<ul><li>Skipped if achievement is already compeleted.</li></ul>",
    postconditions = {
      Condition.ChatText:new("Gross Misconduct"),
      Condition.ConversationText:new("already killed a large number"), --not tested
    },
  },
  {
    text = "Talk to Anne Dimitri.",
    actions = {
      Action.Direction:new(3136, 933, 3535, { distance = 23 }),
      Action.ModelHighlight:new(Models.npcs["anne dimitri"], { distance = 24 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Continue talking to Anne Dimitri.",
    actions = { Action.ModelHighlight:new(Models.npcs["anne dimitri"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Civil War I (miniquest)",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1665964800,
  prereqQuests = { "Daughter of Chaos" },
  questReqs = {},
  neededItems = { ["Wilderness sword"] = { quantity = 1 } },
  recommendedItems = {},
  combatNPCs = { ["Demons"] = { level = "?", quantity = 144 } },
})
