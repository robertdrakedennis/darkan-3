local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local goblinMail = Model.new(132, {
  [1] = Vertex.new(159, 0, 44, 56, 43, 17),
})

local redGoblinMail = Model.new(132, {
  [1] = Vertex.new(159, 0, 44, 91, 15, 8),
})

local yellowGoblinMail = Model.new(132, {
  [1] = Vertex.new(159, 0, 44, 106, 92, 22),
})

local blueGoblinMail = Model.new(132, {
  [1] = Vertex.new(159, 0, 44, 27, 30, 52),
})

local orangeGoblinMail = Model.new(132, {
  [1] = Vertex.new(159, 0, 44, 126, 71, 11),
})

local anyMail = Model.any({ goblinMail, redGoblinMail, yellowGoblinMail, blueGoblinMail, orangeGoblinMail })

local bentnoze = Model.new(4197, {
  [1] = Vertex.new(92, 183, -123, 88, 68, 67),
})

local wartface = Model.new(5775, {
  [1] = Vertex.new(-346, 462, -300, 108, 108, 99),
})

local ladder = Model.new(1041, {
  [1] = Vertex.new(5356, 1662, 4481, 113, 102, 72),
})

---@type QuestStep[]
local steps = {
  {
    text = "Teleport to Falador and run north along the road to the Goblin Village.",
    title = "Getting started",
    actions = { Action.Direction:new(2957, 645, 3511) },
    postconditions = { Condition.DistanceTo:new(2957, 645, 3509, 10) },
  },
  {
    text = "Talk to General Bentnoze or General Wartface, both of whom are located in the hut.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ModelHighlight:new(bentnoze),
      Action.ConversationHighlight:new("So how is life for the goblins?"),
      Action.ConversationHighlight:new("Wouldn't you prefer peace?"),
      Action.ConversationHighlight:new("Do you want me to pick an armour colour for you?"),
      Action.ConversationHighlight:new("What about a different colour?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Grab three goblin mail from the following crates:<ul><li>Behind the generals' hut.</li><li>Behind the western hut.</li><li>Up a ladder in the middle of the gate entrance of the camp.</li></ul>",
    actions = {
      Action.Direction:new(2959, 845, 3517),
      Action.Direction:new(2955, 2285, 3497),
      Action.Direction:new(2949, 845, 3507),
      Action.ModelHighlight:new(ladder),
    },
    postconditions = { Condition.InventoryContains:new(anyMail, 3) },
  },
  {
    text = "Dye one goblin mail blue",
    title = "Finishing up",
    neededItems = {
      ["Red dye"] = { quantity = 1, model = Models.items["red dye"] },
      ["Yellow dye"] = { quantity = 1, model = Models.items["yellow dye"] },
      ["Blue dye"] = { quantity = 1, model = Models.items["blue dye"] },
      ["Goblin mail"] = { quantity = 3, model = anyMail },
    },
    actions = { Action.InventoryHighlight:new(Models.items["blue dye"]), Action.InventoryHighlight:new(goblinMail) }, -- let's not do anyMail to prevent player from overpainting an already painted one
    postconditions = { Condition.InventoryContains:new(blueGoblinMail) },
  },
  {
    text = "Dye one goblin mail orange (first combine red and yellow dye)",
    actions = {
      Action.InventoryHighlight:new(Models.items["red dye"]),
      Action.InventoryHighlight:new(Models.items["yellow dye"]),
    },
    postconditions = {
      Condition.InventoryContains:new(Models.items["orange dye"]),
      Condition.InventoryContains:new(orangeGoblinMail),
    },
  },
  {
    actions = { Action.InventoryHighlight:new(Models.items["orange dye"]), Action.InventoryHighlight:new(goblinMail) },
    postconditions = { Condition.InventoryContains:new(orangeGoblinMail) },
  },
  {
    text = "Use the orange goblin mail on one of the generals.",
    actions = {
      Action.InventoryHighlight:new(orangeGoblinMail),
      Action.ModelHighlight:new(bentnoze),
    },
    postconditions = { Condition.ConversationText:new("Grubfoot!") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Human! Get us blue armour!") },
  },
  {
    text = "After the cutscene, use the blue goblin mail on one of them.",
    actions = {
      Action.InventoryHighlight:new(blueGoblinMail),
      Action.ModelHighlight:new(bentnoze),
    },
    postconditions = { Condition.ConversationText:new("Grubfoot!") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Human! Get us brown armour!") },
  },
  {
    text = "Use a normal goblin mail on one of them.",
    actions = {
      Action.InventoryHighlight:new(goblinMail),
      Action.ModelHighlight:new(bentnoze),
    },
    postconditions = { Condition.ConversationText:new("Grubfoot!") },
  },
  {
    actions = {},
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Goblin Diplomacy",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 989280000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Red dye"] = { quantity = 1, model = Models.items["red dye"] },
    ["Yellow dye"] = { quantity = 1, model = Models.items["yellow dye"] },
    ["Blue dye"] = { quantity = 1, model = Models.items["blue dye"] },
    ["Goblin mail"] = { quantity = 3, duringQuest = true, model = anyMail },
  },
  recommendedItems = {},
  combatNPCs = {},
})
