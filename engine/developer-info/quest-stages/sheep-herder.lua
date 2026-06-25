local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Items
local plagueJacket = Model.new(567, {
  [455] = Vertex.new(108, 8, -108, 70, 51, 44),
  [461] = Vertex.new(-60, 48, 88, 37, 34, 33),
  [476] = Vertex.new(-16, 48, 104, 37, 34, 33),
  [479] = Vertex.new(-52, 48, 56, 37, 34, 33),
  [515] = Vertex.new(-44, 48, 36, 37, 34, 33),
})
local plagueTrousers = Model.new(171, {
  [66] = Vertex.new(124, 0, 168, 70, 51, 44),
  [149] = Vertex.new(132, 0, 184, 57, 41, 36),
  [150] = Vertex.new(124, 0, 168, 57, 41, 36),
  [152] = Vertex.new(124, 0, 168, 57, 41, 36),
  [156] = Vertex.new(132, 0, 184, 57, 41, 36),
})
--#endregion
--#region Quest Items
local cattleprod = Model.new(165, {
  [71] = Vertex.new(88, 0, 68, 95, 87, 86),
  [75] = Vertex.new(88, 0, 68, 95, 87, 86),
  [98] = Vertex.new(88, 0, 68, 95, 87, 86),
  [101] = Vertex.new(100, 0, 48, 95, 87, 86),
  [105] = Vertex.new(100, 0, 48, 95, 87, 86),
})
local sheepFeed = Model.new(351, {
  [314] = Vertex.new(-8, 112, -88, 161, 148, 147),
  [321] = Vertex.new(24, 72, -96, 161, 148, 147),
  [330] = Vertex.new(-24, 72, -96, 161, 148, 147),
  [344] = Vertex.new(-8, 96, -96, 0, 0, 0),
  [350] = Vertex.new(16, 96, -96, 0, 0, 0),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Councillor Halgrive south of the Ardougne church and north of the zoo.",
    title = "Starting out",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {
      ["Coins"] = { quantity = 100 },
    },
    recommendedItems = {},
    actions = {
      Action.Direction:new(2615, 1197, 3301, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["councillor halgrive"], { distance = 24 }),
      Action.ConversationHighlight:new("What's wrong?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Councillor Halgrive.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["councillor halgrive"]),
    },
    postconditions = { Condition.ConversationText:new("trouble spotting them") },
  },
  {
    text = "Talk to Doctor Orbon inside the church.",
    title = "Getting geared up",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.Direction:new(2616, 1189, 3307, { distance = 5 }),
      Action.ModelHighlight:new(Models.npcs["doctor orbon"], { distance = 9 }),
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("Ok, I'll take it."),
    },
    postconditions = { Condition.InventoryContains:new(plagueJacket) },
  },
  {
    text = "Equip the plague jacket and trousers.",
    actions = {
      Action.InventoryHighlight:new(plagueJacket),
      Action.InventoryHighlight:new(plagueTrousers),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(plagueJacket) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(plagueJacket),
      Action.InventoryHighlight:new(plagueTrousers),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(plagueTrousers) },
  },
  {
    text = "Enter the pen west of the Ardougne lodestone.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2594.5, 1621, 3361.5) },
    postconditions = { Condition.DistanceTo:new(2597, 1221, 3361, 2) },
  },
  {
    text = "Pick up the cattleprod.",
    actions = { Action.ModelHighlight:new(cattleprod) },
    postconditions = { Condition.InventoryContains:new(cattleprod) },
  },
  {
    text = "Equip the cattleprod.",
    actions = { Action.InventoryHighlight:new(cattleprod) },
    postconditions = { Condition.InventoryDoesNotContain:new(cattleprod) },
  },
  {
    text = "Prod a sick-looking sheep (1) into the pen.",
    title = "Herding the sheep",
    actions = { Action.Direction:new(2611, 741, 3343) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["sick-looking sheep (1)"]) },
  },
  {
    actions = {
      Action.Direction:new(2594.5, 1621, 3361.5),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (1)"], { highlightPriority = "closest" }),
    },
    postconditions = {
      Condition.ModelVisible:new(
        Models.npcs["sick-looking sheep (1)"],
        { atLocation = Location:new(2597, 1221, 3362) }
      ),
    },
  },
  {
    text = "Prod a sick-looking sheep (2) into the pen.",
    actions = { Action.Direction:new(2615, 549, 3370) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["sick-looking sheep (2)"]) },
  },
  {
    actions = {
      Action.Direction:new(2594.5, 1621, 3361.5),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (2)"], { highlightPriority = "closest" }),
    },
    postconditions = {
      Condition.ModelVisible:new(
        Models.npcs["sick-looking sheep (2)"],
        { atLocation = Location:new(2598, 1213, 3361) }
      ),
    },
  },
  {
    text = "Prod a sick-looking sheep (4) into the pen.",
    actions = { Action.Direction:new(2610, 413, 3383) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["sick-looking sheep (4)"]) },
  },
  {
    actions = {
      Action.Direction:new(2594.5, 1621, 3361.5),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (4)"], { highlightPriority = "closest" }),
    },
    postconditions = {
      Condition.ModelVisible:new(
        Models.npcs["sick-looking sheep (4)"],
        { atLocation = Location:new(2596, 1213, 3359) }
      ),
    },
  },
  {
    text = "Prod a sick-looking sheep (3) into the pen.",
    actions = { Action.Direction:new(2562, 1197, 3389) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["sick-looking sheep (3)"]) },
  },
  {
    actions = {
      Action.Direction:new(2594.5, 1621, 3361.5),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (3)"], { highlightPriority = "closest" }),
    },
    postconditions = {
      Condition.ModelVisible:new(
        Models.npcs["sick-looking sheep (3)"],
        { atLocation = Location:new(2597, 1221, 3360) }
      ),
    },
  },
  {
    text = "Enter the pen.",
    title = "Euthanising the sheep",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2594.5, 1621, 3361.5) },
    postconditions = { Condition.DistanceTo:new(2597, 1221, 3361, 2) },
  },
  {
    text = "Use the sheep feed on every sheep.",
    warning = "Face camera to the river.",
    actions = {
      Action.InventoryHighlight:new(sheepFeed),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (1)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (2)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (3)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (4)"]),
    },
    postconditions = { Condition.ModelNotVisible:new(Models.npcs["sick-looking sheep (1)"]) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(sheepFeed),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (1)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (2)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (3)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (4)"]),
    },
    postconditions = { Condition.ModelNotVisible:new(Models.npcs["sick-looking sheep (2)"]) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(sheepFeed),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (1)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (2)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (3)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (4)"]),
    },
    postconditions = { Condition.ModelNotVisible:new(Models.npcs["sick-looking sheep (3)"]) },
  },
  {
    actions = {
      Action.InventoryHighlight:new(sheepFeed),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (1)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (2)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (3)"]),
      Action.ModelHighlight:new(Models.npcs["sick-looking sheep (4)"]),
    },
    postconditions = { Condition.ModelNotVisible:new(Models.npcs["sick-looking sheep (4)"]) },
  },
  {
    text = "Pick up all 4 bones.",
    actions = { Action.ModelHighlight:new(Models.items["bones"], { highlightPriority = "all" }) },
    postconditions = { Condition.InventoryContains:new(Models.items["bones"], 4) },
  },
  {
    text = "Use the bones on the incinerator.",
    actions = {
      Action.Direction:new(2605.4, 1721, 3360),
      Action.InventoryHighlight:new(Models.items["bones"]),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["bones"]) },
  },
  {
    text = "Return to Councillor Halgrive.",
    title = "Finishing up",
    neededItems = {},
    recommendedItems = {},
    actions = {
      Action.Direction:new(2615, 1197, 3301, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["councillor halgrive"], { distance = 24 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Sheep Herder",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1029369600,
  prereqQuests = {},
  questReqs = {},
  neededItems = { ["Coins"] = { quantity = 100 } },
  recommendedItems = {},
  combatNPCs = {},
})
