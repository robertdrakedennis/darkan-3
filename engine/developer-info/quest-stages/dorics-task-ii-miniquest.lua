local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Objects
local burthorpeRopeLadder = Model.new(210, {
  [167] = Vertex.new(7339, 3760, 3983, 77, 66, 54),
  [173] = Vertex.new(7507, 3760, 3967, 77, 66, 54),
  [185] = Vertex.new(7339, 3664, 3983, 77, 66, 54),
  [191] = Vertex.new(7507, 3664, 3967, 77, 66, 54),
  [209] = Vertex.new(7507, 3564, 3967, 77, 66, 54),
})
local brokenCannon = Model.new(3609, {
  [1616] = Vertex.new(-76, 461, 715, 171, 159, 158),
  [2974] = Vertex.new(-80, 444, 221, 70, 181, 37),
  [3239] = Vertex.new(125, 165, 590, 35, 31, 29),
  [3245] = Vertex.new(-125, 165, 682, 35, 31, 29),
  [3263] = Vertex.new(-125, 165, 542, 35, 31, 29),
})
--#endregion
--#region Items
local steelChainbody = Model.new(558, {
  [1] = Vertex.new(72, 1, -120, 26, 25, 28),
  [2] = Vertex.new(53, 1, -99, 26, 25, 28),
  [3] = Vertex.new(66, 12, -105, 26, 25, 28),
  [4] = Vertex.new(117, 14, -47, 26, 25, 28),
  [5] = Vertex.new(93, 1, -36, 26, 25, 28),
})
--#endregion
--#region Quest Items
local cannonPart = Model.new(402, {
  [1] = Vertex.new(-69, 137, 71, 62, 57, 57),
  [2] = Vertex.new(-117, 127, 0, 62, 57, 57),
  [3] = Vertex.new(-83, 127, 85, 62, 57, 57),
  [5] = Vertex.new(-98, 137, 0, 62, 57, 57),
  [8] = Vertex.new(-122, 98, 0, 51, 47, 47),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Doric in his hut north of Falador.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = {},
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
    postconditions = { Condition.ConversationText:new("Good.") },
  },
  {
    text = "Smith two steel <b>chainbodies</b>.",
    title = "Attack of the Trolls",
    neededItems = {
      ["Bronze bars (in your inventory)"] = { quantity = 2 },
      ["Steel bars (in metal bank)"] = { quantity = 10 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2962, 869, 3439) },
    postconditions = { Condition.InventoryContains:new(steelChainbody, 2) },
  },
  {
    text = "Talk to Commander Denulth.",
    warning = "There are breaks in the dialogue. Make sure the conversation is completed.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Burthorpe lodestone",
      url = "Burthorpe_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2918, 1605, 3561) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["commander denulth"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["commander denulth"]),
      Action.ConversationHighlight:new("Ask about Doric's Tasks"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["commander denulth"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("see what I can") },
  },
  {
    text = "Return to Doric.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
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
    postconditions = { Condition.ConversationText:new("Thanks, Doric.") },
  },
  {
    text = "Use Doric's anvil.",
    actions = { Action.Direction:new(2962, 1269, 3439) },
    postconditions = { Condition.InventoryContains:new(cannonPart) },
  },
  {
    text = "Climb the rope ladder north of the Burthorpe lodestone.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Burthorpe lodestone",
      url = "Burthorpe_lodestone_icon.png",
    },
    actions = { Action.ModelHighlight:new(burthorpeRopeLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(2894, 4843, 3560, 3) },
  },
  {
    text = "Repair the middle broken cannon to the north.",
    actions = { Action.ModelHighlight:new(brokenCannon) },
    postconditions = { Condition.ConversationText:new("You fix the cannon using the part you made.") },
  },
  {
    text = "Return to Commander Denulth.",
    actions = { Action.Direction:new(2918, 1605, 3561) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["commander denulth"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["commander denulth"]),
      Action.ConversationHighlight:new("Ask about Doric's Tasks"),
      Action.ConversationHighlight:new("I've fixed the cannon."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["commander denulth"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task II (miniquest)",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = false,
  length = Enums.length.veryshort,
  releaseDate = 1350432000,
  prereqQuests = { "Doric's Task I (miniquest)", "Death Plateau" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 20),
    Types.QuestReq.ironmanOnlySkill("Mining", 20, true),
  },
  neededItems = {
    ["Bronze bars (in your inventory)"] = { quantity = 2, model = Models.items["bronze bar"] },
    ["Steel bars (in metal bank)"] = { quantity = 10 },
  },
  recommendedItems = {},
  combatNPCs = {},
})
