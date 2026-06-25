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
    title = "Fremennik of War",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = { ["Orikalkum bars (in metal bank)"] = { quantity = 16 } },
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
    postconditions = { Condition.ConversationText:new("Do us proud") },
  },
  {
    text = "Smith an Orikalkum warhammer + 3.",
    actions = { Action.Direction:new(2962, 1269, 3439) },
    postconditions = { Condition.ChatText:new("finish smithing: Orikalkum warhammer + 3") }, --not working, but is correct
  },
  {
    text = "Talk to Skulgrimen north of the long hall in Rellekka.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Fremennik Province lodestone",
      url = "Fremennik_Province_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2664, 1093, 3693) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["skulgrimen"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["skulgrimen"]),
      Action.ConversationHighlight:new("I have your order from Doric & Son."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["skulgrimen"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task VI (miniquest)",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.short,
  releaseDate = 1350432000,
  prereqQuests = { "Doric's Task V (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 60),
    Types.QuestReq.ironmanOnlySkill("Mining", 60, true),
  },
  neededItems = { ["Orikalkum bars (in metal bank)"] = { quantity = 16 } },
  recommendedItems = {},
  combatNPCs = {},
})
