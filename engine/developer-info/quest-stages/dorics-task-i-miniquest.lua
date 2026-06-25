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
    text = "Talk to Doric in his hut north of Falador.",
    title = "Delivery for Aksel",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = { ["Iron bars (in metal bank)"] = { quantity = 4 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2958, 869, 3439) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["doric"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["doric"]),
      Action.ConversationHighlight:new("Do you have any more Smithing tasks for me?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["doric"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Doric.",
    actions = { Action.ModelHighlight:new(Models.npcs["doric"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["task list"]) },
  },
  {
    text = "Smith an iron full helm +1.",
    actions = { Action.Direction:new(2961, 1269, 3438) },
    postconditions = {
      Condition.ConversationText:new("Iron full helm + 1"),
      Condition.ChatText:new("Iron full helm + 1"), --not working, but is correct
    },
  },
  {
    text = "Talk to Aksel at the Artisans' Workshop in south-east Falador.",
    actions = { Action.Direction:new(3038, 1125, 3342) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["aksel"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["aksel"]),
      Action.ConversationHighlight:new("Doric sent me here with another delivery."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["aksel"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task I (miniquest)",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = false,
  length = Enums.length.veryshort,
  releaseDate = 1350432000,
  prereqQuests = { "What's Mine is Yours" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 10),
    Types.QuestReq.ironmanOnlySkill("Mining", 10, true),
  },
  neededItems = { ["Iron bars (in metal bank)"] = { quantity = 4 } },
  recommendedItems = {},
  combatNPCs = {},
})
