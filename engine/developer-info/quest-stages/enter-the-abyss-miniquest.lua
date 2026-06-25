local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local zamorakMage = Model.new(3939, {
  [2158] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2163] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2166] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2168] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3089] = Vertex.new(-21, 790, -40, 76, 71, 48),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the Mage of Zamorak north of Edgeville, over the wilderness wall.<ul><li>Do not wear any Saradomin or Guthix equipment.</li></ul>",
    title = "Starting off",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Edgeville lodestone",
      url = "Edgeville_lodestone_icon.png",
    },
    actions = {
      Action.ModelHighlight:new(zamorakMage, { distance = 8 }),
      Action.Direction:new(3107, 1261, 3558, { distance = 8 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Mage of Zamorak.",
    actions = { Action.ModelHighlight:new(zamorakMage) },
    postconditions = { Condition.ConversationText:new("Here is not the place to talk.") },
  },
  {
    text = "Talk to the Mage of Zamorak in the Temple of Zamorak, in the southeast of Varrock.",
    title = "Getting the readings",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    neededItems = { ["Scrying orb"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(zamorakMage, { distance = 6 }),
      Action.Direction:new(3259, 1125, 3385, { distance = 6 }),
      Action.ConversationHighlight:new("Where do you get your runes from?"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = {
      Condition.ConversationText:new("If you encounter any difficulties speak to me again."),
    },
  },
  {
    text = "Choose the <i>teleport</i> option Aubury in his rune shop to the north.",
    actions = {
      Action.Direction:new(3253, 1125, 3401, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["aubury"], { distance = 4 }),
    },
    postconditions = { Condition.DistanceTo:new(-1, 1553, 1, 20, true) },
  },
  {
    text = "Head to the Wizard's Tower south of Draynor and go to the 2nd floor (3rd floor[US]).<ul><li>It is recommended to use a wicked hood to teleport to the tower.</li></ul>",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Runecrafting guild teleport",
      url = "Wicked_hood.png",
    },
    actions = { Action.Direction:new(3102, 1925, 3155) },
    postconditions = {
      Condition.DistanceTo:new(3102, 8613, 3155, 8),
      Condition.DistanceToWithHeight:new(3102, 16773, 3155, 8),
    },
  },
  {
    actions = { Action.Direction:new(3102, 8613, 3155) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3102, 16773, 3155, 8),
    },
  },
  {
    text = "Choose the <i>teleport</i> option on Archmage Sedridor.",
    actions = { Action.ModelHighlight:new(Models.npcs["sedridor"]) },
    postconditions = { Condition.DistanceTo:new(-1, 1553, 1, 20, true) },
  },
  {
    text = "Head to Wizard Cromperty's house in northeast Ardougne.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2683, 1285, 3325) },
    postconditions = { Condition.DistanceTo:new(2683, 1285, 3325, 8) },
  },
  {
    text = "Choose the <i>teleport</i> option on Wizard Cromperty.",
    actions = { Action.ModelHighlight:new(Models.npcs["wizard cromperty"]) },
    postconditions = { Condition.DistanceTo:new(-1, 1553, 1, 20, true) },
  },
  {
    text = "Return to the Temple of Zamorak in southeast of Varrock.<ul><li>Speak to the Mage of Zamorak a third time to unlock the Abyss after completing the miniquest.</li></ul>",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    actions = {
      Action.ModelHighlight:new(zamorakMage, { distance = 6 }),
      Action.Direction:new(3259, 1125, 3385, { distance = 6 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Enter the Abyss (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.veryshort,
  releaseDate = 1118620800,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Runecrafting", 5),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
