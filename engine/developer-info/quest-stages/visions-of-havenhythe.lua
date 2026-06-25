local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local bartender = Model.new(3957, {
  [2281] = Vertex.new(-2, 725, -59, 107, 78, 55),
  [2286] = Vertex.new(-7, 724, -51, 107, 78, 55),
  [2289] = Vertex.new(2, 725, -59, 107, 78, 55),
  [2291] = Vertex.new(7, 724, -51, 107, 78, 55),
  [3500] = Vertex.new(-30, 721, -31, 47, 36, 14),
})
local boulderCrab = Model.any({
  Model.new(27696, {
    [706] = Vertex.new(-91, 71, -192, 127, 127, 127),
    [929] = Vertex.new(-107, 43, -204, 127, 127, 127),
    [965] = Vertex.new(-125, 47, -187, 127, 127, 127),
    [2464] = Vertex.new(-125, 47, -187, 127, 127, 127),
    [2467] = Vertex.new(-125, 47, -187, 127, 127, 127),
  }),
})
--#endregion
--#region Objects
local lodestoneHotspot = Model.new(4572, {
  [29] = Vertex.new(-231, 531, -246, 127, 127, 127),
  [133] = Vertex.new(247, 503, -236, 127, 127, 127),
  [151] = Vertex.new(256, 502, 200, 127, 127, 127),
  [349] = Vertex.new(-231, 531, -246, 127, 127, 127),
  [2635] = Vertex.new(-231, 531, -246, 127, 127, 127),
})
--#endregion
--#region Quest Items
local dragonfireMead = Model.new(306, {
  [237] = Vertex.new(48, 80, 0, 128, 101, 11),
  [266] = Vertex.new(48, 72, 16, 128, 101, 11),
  [270] = Vertex.new(48, 72, 16, 128, 101, 11),
  [287] = Vertex.new(48, 72, 16, 128, 101, 11),
  [305] = Vertex.new(48, 80, 0, 128, 101, 11),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the bartender at the Rusty Anchor pub in Port Sarim.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Port Sarim lodestone",
      url = "Port_Sarim_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3045, 1465, 3257) },
    postconditions = { Condition.ModelVisible:new(bartender) },
  },
  {
    actions = {
      Action.ModelHighlight:new(bartender),
      Action.ConversationHighlight:new("Talk about 'Visions of Havenhythe'"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(bartender) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the bartender.",
    actions = { Action.ModelHighlight:new(bartender) },
    postconditions = { Condition.InventoryContains:new(dragonfireMead) },
  },
  {
    text = "Drink the dragonfire mead.",
    title = "Visions of a shadowed sun",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(dragonfireMead) },
    postconditions = { Condition.ChatText:new("drink the dragonfire mead") },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.DistanceToWithHeight:new(3049, 3109, 3256, 1) } },
  {
    text = "Go downstairs.",
    actions = { Action.Direction:new(3053.4, 3709, 3257) },
    postconditions = { Condition.DistanceToWithHeight:new(3054, 965, 3255, 4) },
  },
  {
    text = "Talk to the bartender.",
    actions = {
      Action.ModelHighlight:new(bartender),
      Action.ConversationHighlight:new("Talk about 'Visions of Havenhythe'"),
    },
    postconditions = { Condition.ConversationText:new("each other out") },
  },
  {
    text = "Talk to 'Shady' Sullivan.",
    actions = { Action.ModelHighlight:new(Models.npcs["'shady' sullivan"]) },
    postconditions = { Condition.ConversationText:new("without a cap") },
  },
  {
    text = "Talk to Captain Lorris to the south.",
    actions = { Action.Direction:new(3028, 1441, 3221) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["captain lorris"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["captain lorris"]),
      Action.ConversationHighlight:new("I'm ready"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["captain lorris"]) },
    jumpOffset = -1,
    postconditions = { Condition.DistanceTo:new(3388, 773, 1530, 4) },
  },
  {
    text = "Talk to Old Man Jocko.",
    title = "Brave new world",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.npcs["old man jocko"]),
      Action.ConversationHighlight:new("Talk about 'Visions of Havenhythe'"),
    },
    postconditions = { Condition.ConversationText:new("big one at the end") },
  },
  {
    text = "Talk to Adam.",
    actions = { Action.Direction:new(3483, 9013, 1573) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["adam"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["adam"]),
      Action.ConversationHighlight:new("What do we do now?"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["adam"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("see what she says") },
  },
  {
    text = "Talk to Shrine Tender Joanna to the south.",
    actions = { Action.Direction:new(3547, 5741, 1425) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["shrine tender joanna"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["shrine tender joanna"]),
      Action.ConversationHighlight:new("Talk about 'Visions of Havenhythe'"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["shrine tender joanna"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("will give us a sign") },
  },
  {
    text = "Pray at the Altar of Inanna.",
    actions = { Action.ModelHighlight:new(Models.objects["altar of inanna"]) },
    postconditions = { Condition.ConversationText:new("We must speak") },
  },
  {
    text = "Talk to Shrine Tender Joanna again.",
    actions = { Action.ModelHighlight:new(Models.npcs["shrine tender joanna"]) },
    postconditions = { Condition.ConversationText:new("always be welcome") },
  },
  {
    text = "Return to Adam.",
    actions = { Action.Direction:new(3483, 9013, 1573) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["adam"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["adam"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["adam"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("what I can do") },
  },
  {
    text = "Talk to Zeke to the east.",
    title = "Building the lodestone",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3496, 8965, 1565) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["zeke"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["zeke"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["zeke"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("miss the little buggers") },
  },
  {
    text = "Kill 10 juvenile boulder crabs on the beach. Pick up the limestone they drop.",
    actions = { Action.Direction:new(3425, 1181, 1564) },
    postconditions = { Condition.ModelVisible:new(boulderCrab) },
  },
  {
    actions = {
      Action.ModelHighlight:new(boulderCrab, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(Models.items["limestone"]),
    },
    jumpconditions = { Condition.ModelNotVisible:new(boulderCrab) },
    jumpOffset = -1,
    postconditions = { Condition.InventoryContains:new(Models.items["limestone"], 10) },
  },
  {
    text = "Return to Zeke.",
    actions = { Action.Direction:new(3496, 8965, 1565) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["zeke"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["zeke"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["zeke"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("head over there, then") },
  },
  {
    text = "Climb up the stairs in the lighthouse to the south.",
    actions = { Action.Direction:new(3450, 7837, 1495.4) },
    postconditions = { Condition.DistanceToWithHeight:new(3454, 11509, 1495, 4) },
  },
  {
    text = "Talk to Esther.",
    actions = { Action.ModelHighlight:new(Models.npcs["esther"]) },
    postconditions = { Condition.ConversationText:new("Good luck!") },
  },
  {
    text = "Craft the limestones into limestone bricks.",
    actions = { Action.InventoryHighlight:new(Models.items["limestone"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["limestone brick"], 10) },
  },
  {
    text = "Go downstairs.",
    title = "Finishing up",
    actions = { Action.Direction:new(3452.5, 11517, 1495.5) },
    postconditions = { Condition.DistanceToWithHeight:new(3452, 7237, 1494, 4) },
  },
  {
    text = "Build the lodestone to the north.",
    actions = { Action.Direction:new(3461, 7445, 1520) },
    postconditions = {
      Condition.ModelVisible:new(lodestoneHotspot),
      Condition.QuestComplete:new(),
    },
  },
  {
    actions = { Action.ModelHighlight:new(lodestoneHotspot) },
    jumpconditions = { Condition.ModelNotVisible:new(lodestoneHotspot) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Visions of Havenhythe",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1774278419,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Crafting", 12) },
  neededItems = {
    ["Limestone bricks"] = { quantity = 10, model = Models.items["limestone brick"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = { ["Juvenile boulder crabs"] = { level = "1", quantity = 10 } },
})
