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
    text = "Speak to Doric in his hut north of Falador.",
    title = "Warhammers and longswords",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = { ["Mithril bars (in metal bank)"] = { quantity = 8 } },
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
    text = "Make a mithril longsword +1.",
    actions = { Action.Direction:new(2962, 869, 3439) },
    postconditions = {
      Condition.ChatText:new("Mithril longsword + 1"),
      Condition.ConversationText:new("Mithril longsword + 1"),
    },
  },
  {
    text = "Make a mithril warhammer +1.",
    actions = { Action.Direction:new(2962, 869, 3439) },
    postconditions = {
      Condition.ChatText:new("Mithril warhammer + 1"),
      Condition.ConversationText:new("Mithril warhammer + 1"),
    },
  },
  {
    text = "Talk to either General Bentnoze or General Wartface in the Goblin Village to the north.",
    actions = { Action.Direction:new(2957, 645, 3513) },
    postconditions = {
      Condition.ModelVisible:new(Models.npcs["bentnoze"]),
      Condition.ModelVisible:new(Models.npcs["wartface"]),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["bentnoze"]),
      Action.ModelHighlight:new(Models.npcs["wartface"]),
      Action.ConversationHighlight:new("No"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["bentnoze"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Stop squabbling you two!") },
  },
  {
    text = "Talk to either again.",
    actions = {
      Action.ConversationHighlight:new("No"),
      Action.ModelHighlight:new(Models.npcs["bentnoze"]),
      Action.ModelHighlight:new(Models.npcs["wartface"]),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task III (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.veryshort,
  releaseDate = 1350432000,
  prereqQuests = { "Doric's Task II (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 30),
    Types.QuestReq.ironmanOnlySkill("Mining", 30, true),
  },
  neededItems = {
    ["Mithril bars (in metal bank)"] = { quantity = 8 },
  },
  recommendedItems = {},
  combatNPCs = {},
})
