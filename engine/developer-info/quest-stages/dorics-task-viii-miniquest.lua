local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Objects
local treeStump = Model.new(753, {
  [135] = Vertex.new(-531, -327, -435, 80, 59, 32),
  [180] = Vertex.new(-458, -216, -482, 69, 51, 28),
  [182] = Vertex.new(-489, -246, 256, 80, 59, 32),
  [218] = Vertex.new(-531, -327, -435, 76, 56, 31),
  [231] = Vertex.new(516, -82, -353, 80, 59, 32),
})
local treeStumpWithCrate = Model.new(1623, {
  [135] = Vertex.new(-531, 295, -435, 80, 59, 32),
  [180] = Vertex.new(-458, 239, -482, 69, 51, 28),
  [182] = Vertex.new(-489, 285, 256, 80, 59, 32),
  [218] = Vertex.new(-531, 295, -435, 76, 56, 31),
  [231] = Vertex.new(516, -82, -353, 80, 59, 32),
})
--#endregion
--#region Items
local necronium2H = Model.new(7224, {
  [5307] = Vertex.new(-174, 54, 158, 127, 127, 127),
  [5334] = Vertex.new(-172, 56, 153, 127, 127, 127),
  [5436] = Vertex.new(-193, 54, 136, 127, 127, 127),
  [6996] = Vertex.new(-174, 50, 156, 127, 127, 127),
  [7068] = Vertex.new(-178, 47, 159, 127, 127, 127),
})
--#endregion
--#region Quest Items
local weaponsCrate = Model.new(600, {
  [145] = Vertex.new(168, 340, -168, 88, 77, 45),
  [149] = Vertex.new(168, 340, -168, 88, 77, 45),
  [157] = Vertex.new(-168, 340, 168, 88, 77, 45),
  [161] = Vertex.new(-168, 340, 168, 88, 77, 45),
  [531] = Vertex.new(120, 0, 168, 81, 66, 24),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Doric in his hut north of Falador.",
    title = "The mysterious client",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    neededItems = { ["Necronium bars (in metal bank)"] = { quantity = 20 } },
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
    actions = {
      Action.ModelHighlight:new(Models.npcs["doric"]),
      Action.ConversationHighlight:new("I'm sure it will be fine."),
    },
    postconditions = { Condition.InventoryContains:new(weaponsCrate) },
  },
  {
    text = "Smith 5 necronium 2h greataxes at any anvil.",
    postconditions = { Condition.InventoryContains:new(necronium2H, 5) },
  },
  {
    text = "Left-click add items to the weapons crate.",
    actions = {
      Action.InventoryHighlight:new(weaponsCrate),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(necronium2H) }, --easy to break
  },
  {
    text = "Investigate the tree stump south of the Falador gem store.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Falador_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2944, 925, 3328) },
    postconditions = {
      Condition.ModelVisible:new(treeStump),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(treeStump),
      Action.ConversationHighlight:new("Yes."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(treeStump) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Investigate the tree stump again.",
    actions = { Action.ModelHighlight:new(treeStumpWithCrate) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Doric's Task VIII (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1350432000,
  prereqQuests = { "Doric's Task VII (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Smithing", 70),
    Types.QuestReq.ironmanOnlySkill("Mining", 70, true),
  },
  neededItems = { ["Necronium bars (in metal bank)"] = { quantity = 20 } },
  recommendedItems = {},
  combatNPCs = {},
})
