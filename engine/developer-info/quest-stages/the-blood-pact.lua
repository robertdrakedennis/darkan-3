local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Enums = require("core.enums")
local Types = require("core.types")
local Model = Types.Model
local Vertex = Types.Vertex

local xenia = Model.new(7560, {
  [1] = Vertex.new(38, 58, 20, 39, 31, 20),
})

local kayle = Model.new(5325, {
  [1] = Vertex.new(-28, 745, -22, 187, 149, 119),
})

local caitlyn = Model.new(7539, {
  [1] = Vertex.new(-3, 726, -43, 118, 78, 75),
})

local bronzeDagger = Model.new(390, {
  [1] = Vertex.new(0, 13, -113, 66, 63, 60),
})

local chargebow = Model.new(228, {
  [1] = Vertex.new(-124, 3, -212, 76, 73, 70),
})

local tombDoor = Model.new(4380, {
  [1] = Vertex.new(156, 40, 128, 85, 78, 65),
})

local whinch = Model.new(651, {
  [1] = Vertex.new(16, 216, 0, 148, 135, 113),
})

local barrier = Model.new(912, {
  [1] = Vertex.new(196, 959, 0, 121, 111, 111),
})

local staff = Model.new(936, {
  [1] = Vertex.new(188, 7, 193, 44, 11, 8),
})

local sword = Model.new(240, {
  [1] = Vertex.new(14, 3, 21, 125, 115, 114),
})

local ilona = Model.new(5622, {
  [1] = Vertex.new(33, 686, 6, 77, 58, 23),
})

---@type QuestStep[]
local steps = {
  {
    text = "Start the quest by talking to Xenia in the Lumbridge graveyard.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Lumbridge lodestone",
      url = "Lumbridge_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3244, 885, 3199) },
    postconditions = { Condition.DistanceTo:new(3244, 885, 3199, 15) },
  },
  {
    actions = {
      Action.ModelHighlight:new(xenia),
      Action.ConversationHighlight:new("What help do you need?"),
      Action.ConversationHighlight:new("I'll help you."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Xenia.",
    actions = { Action.ModelHighlight:new(xenia) },
    postconditions = { Condition.ConversationText:new("down the stairs") },
  },
  {
    text = "Enter the catacombs.",
    title = "The dungeon",
    actions = { Action.Direction:new(3247, 1165, 3198) },
    postconditions = { Condition.ConversationText:new("Come on, Kayle!") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Looks like there's a guard") },
  },
  {
    text = "After a cutscene, proceed north.<ul><li>Talk to Xenia if there is no dialogue.</li><li>Equip a melee weapon if you have one in your inventory (or the bronze dagger Xenia gifts you)</li></ul>",
    actions = { Action.ModelHighlight:new(kayle) },
    postconditions = {
      Condition.ConversationText:new("Ah..."),
      Condition.ConversationText:new("You'll need a weapon"),
      Condition.ConversationText:new("You'd best arm yourself"),
    },
  },
  {
    actions = { Action.InventoryHighlight:new(bronzeDagger) },
    postconditions = { Condition.InventoryDoesNotContain:new(bronzeDagger), Condition.ConversationText:new("Ah...") },
  },
  {
    actions = { Action.ModelHighlight:new(xenia) },
    postconditions = { Condition.ConversationActive:new(), Condition.ConversationText:new("Ah...") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Ah...") },
  },
  {
    text = "Defeat Kayle then talk to him.",
    actions = {
      Action.ModelHighlight:new(kayle),
      Action.ConversationHighlight:new("Yes. Now die!"),
      Action.ConversationHighlight:new("I can handle this."),
    },
    postconditions = { Condition.ModelVisible:new(chargebow) },
  },
  {
    text = "Take his chargebow.<ul><li>(Optional) Equip it if you don't have any Magic, Ranged, or Necromancy weapon.</li></ul>",
    actions = { Action.ModelHighlight:new(chargebow) },
    postconditions = { Condition.InventoryContains:new(chargebow) },
  },
  {
    text = "Head west, open the tomb door, and then go through it.",
    actions = { Action.ModelHighlight:new(tombDoor) },
    postconditions = { Condition.ModelNotVisible:new(tombDoor) },
  },
  {
    text = "Defeat Caitlin.",
    actions = { Action.ModelHighlight:new(caitlyn) },
    postconditions = { Condition.ConversationText:new("Well done!") },
  },
  {
    text = "Go south and operate the winch.",
    actions = { Action.ModelHighlight:new(whinch) },
    postconditions = { Condition.ModelNotVisible:new(barrier) },
  },
  {
    text = "Talk to Caitlin.",
    actions = {
      Action.ModelHighlight:new(caitlyn),
      Action.ConversationHighlight:new("Time for you to die!"),
    },
    postconditions = { Condition.ModelVisible:new(staff) },
  },
  {
    text = "Take her staff.",
    actions = { Action.ModelHighlight:new(staff) },
    postconditions = { Condition.InventoryContains:new(staff) },
  },
  {
    text = "Go down one of the stairs to the west.",
    actions = { Action.ConversationHighlight:new("Go downstairs") },
    postconditions = { Condition.ConversationText:new("The potion") },
  },
  {
    text = "Defeat Reese and talk to him.",
    actions = {
      Action.ConversationHighlight:new("My name's"),
      Action.ConversationHighlight:new("Time for you to die!"),
    },
    postconditions = { Condition.ModelVisible:new(sword) },
  },
  {
    text = "Untie Ilona.",
    actions = {
      Action.ModelHighlight:new(ilona),
      Action.ConversationHighlight:new("Yes, rescue Ilona."),
    },
    postconditions = { Condition.ConversationText:new("Thank the gods") },
  },
  {
    text = "Talk to Xenia",
    title = "Finishing up",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("I'm ready for my reward.") },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Blood Pact",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 1268611200,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {
    ["Kayle"] = { level = "1", quantity = 1 },
    ["Caitlin"] = { level = "1", quantity = 1 },
    ["Reese"] = { level = "1", quantity = 1 },
  },
})
