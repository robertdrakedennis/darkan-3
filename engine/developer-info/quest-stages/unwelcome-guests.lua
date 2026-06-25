local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Objects
local buildingSupplies = Model.any({
  Model.new(4800, {
    [3404] = Vertex.new(-490, 269, -977, 127, 127, 127),
    [4207] = Vertex.new(-392, 320, -995, 127, 127, 127),
    [4244] = Vertex.new(-490, 269, -977, 127, 127, 127),
    [4256] = Vertex.new(-490, 269, -977, 127, 127, 127),
    [4309] = Vertex.new(-406, 335, -982, 127, 127, 127),
  }),
  Model.new(7842, {
    [5443] = Vertex.new(-179, 260, 746, 127, 127, 127),
    [5453] = Vertex.new(-286, 241, 1172, 127, 127, 127),
    [5521] = Vertex.new(-294, 231, 1169, 127, 127, 127),
    [5669] = Vertex.new(-44, 22, 1406, 127, 127, 127),
    [5677] = Vertex.new(-44, 11, 1406, 127, 127, 127),
  }),
})
--#endregion
--#region Items
local mapleFrame = Model.new(435, {
  [376] = Vertex.new(-182, 1094, -49, 120, 99, 92),
  [380] = Vertex.new(-182, 1094, -49, 120, 99, 92),
  [428] = Vertex.new(-178, 1097, 101, 120, 99, 92),
  [431] = Vertex.new(-178, 1097, 101, 120, 99, 92),
  [435] = Vertex.new(-178, 1097, 101, 120, 99, 92),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Overseer Siv in the Command Centre at Fort Forinthry.",
    title = "Starting off",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3319, 1085, 3540) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["overseer siv"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["overseer siv"]),
      Action.ConversationHighlight:new("Talk about quests."),
      Action.ConversationHighlight:new("Talk about 'Unwelcome Guests'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["overseer siv"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Overseer Siv.",
    actions = { Action.ModelHighlight:new(Models.npcs["overseer siv"]) },
    postconditions = { Condition.ConversationText:new("it with me") },
  },
  {
    text = "Talk to the Raptor.",
    title = "Fetid frenzy",
    neededItems = { ["Combat gear"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3292, 685, 3544) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["the raptor"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["the raptor"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Got it") },
  },
  {
    text = "Open the northern gates.",
    actions = {
      Action.ModelHighlight:new(Models.objects["forinthry northern gate"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill 10 fetid zombies to the west.",
    actions = { Action.ModelHighlight:new(Models.npcs["fetid zombie"], { highlightPriority = "all" }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to the Raptor.",
    actions = { Action.Direction:new(3292, 685, 3544) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["the raptor"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["the raptor"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("zombie gymnastics") },
  },
  {
    text = "Talk to Bill.",
    title = "Strengthening the fortifications",
    actions = {
      Action.ModelHighlight:new(Models.npcs["bill"]),
      Action.ConversationHighlight:new("Talk about 'Unwelcome Guests'."),
    },
    postconditions = { Condition.ConversationText:new("you need them") },
  },
  {
    text = "Open the northern gates.",
    actions = {
      Action.ModelHighlight:new(Models.objects["forinthry northern gate"]),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes, I understand the restrictions."),
    },
    postconditions = {
      Condition.DistanceTo:new(3286, 749, 3579, 1),
      Condition.DistanceTo:new(3287, 749, 3579, 1),
    },
  },
  {
    text = "Fully construct the building supplies along the walls.<ul><li>Turning off auto-retaliate.</li></ul>",
    actions = { Action.ModelHighlight:new(buildingSupplies, { highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("to the last one") },
  },
  {
    actions = { Action.ModelHighlight:new(buildingSupplies, { highlightPriority = "all" }) },
    postconditions = { Condition.ModelNotVisible:new(buildingSupplies) }, --easily broken
  },
  {
    text = "Talk to Overseer Siv.",
    title = "Building the Guardhouse",
    neededItems = {
      ["Maple frames"] = { quantity = 14 },
      ["Stone wall segments"] = { quantity = 6 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(3319, 1085, 3540) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["overseer siv"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["overseer siv"]),
      Action.ConversationHighlight:new("Talk about quests."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["overseer siv"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("Very good") },
  },
  {
    text = "Talk to Bill.",
    actions = { Action.Direction:new(3286, 965, 3557) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["bill"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["bill"]),
      Action.ConversationHighlight:new("Talk about 'Unwelcome Guests'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["bill"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("need to begin construction") },
  },
  {
    text = "Start building the guardhouse at the blueprints table.",
    actions = { Action.ModelHighlight:new(Models.objects["forinthry blueprint table"]) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(mapleFrame),
      Condition.InventoryDoesNotContain:new(Models.items["stone wall segments"]),
      Condition.ModelVisible:new(Models.objects["forinthry optimal hotspot"]),
    },
  },
  {
    text = "Build the guardhouse south of the Raptor.",
    actions = { Action.ModelHighlight:new(Models.objects["forinthry optimal hotspot"]) },
    postconditions = {
      Condition.ConversationText:new("say about it"),
      Condition.ChatText:new("advanced your building"),
    },
  },
  {
    text = "Talk to the Raptor.",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Open the northern gates.",
    title = "Investigating the crypt",
    neededItems = { ["Combat gear"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Models.objects["forinthry northern gate"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill 10 fetid zombies to the west.",
    actions = { Action.ModelHighlight:new(Models.npcs["fetid zombie"], { highlightPriority = "all" }) },
    postconditions = {
      Condition.ConversationText:new("clear"),
      Condition.ChatText:new("all of them"),
    },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to the Raptor.<ul><li>Remember to unlock the grove after completing the quest.</li></ul>",
    title = "Finishing up",
    actions = { Action.Direction:new(3292, 1085, 3544) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["the raptor"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["the raptor"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Unwelcome Guests",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1681171200,
  prereqQuests = { "Murder on the Border" },
  questReqs = {
    Types.QuestReq.skill("Construction", 50),
    Types.QuestReq.skill("Slayer", 10),
    Types.QuestReq.skill("Woodcutting", 40, true),
  },
  neededItems = {
    ["Maple frames"] = { quantity = 14, model = mapleFrame },
    ["Stone wall segments"] = { quantity = 6, model = Models.items["stone wall segments"] },
    ["Combat gear"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = { ["Fetid zombies"] = { level = "66", quantity = 20 } },
})
