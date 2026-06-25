local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Enums = require("core.enums")
local Types = require("core.types")
local Models = require("util.models")
local Model, Vertex = Types.Model, Types.Vertex

--NPCs
local faladorSquire = Models.npcs["falador squire"]
local reldo = Models.npcs["reldo"]
local thurgo = Models.npcs["thurgo"]

--Objects
local faladorLadder = Models.objects["falador ground floor ladder"]
local faladorStaircase = Model.new(756, {
  [12] = Vertex.new(4948, 4526, 5772, 109, 91, 69),
  [33] = Vertex.new(4272, 4825, 6144, 109, 91, 69),
  [96] = Vertex.new(4948, 4825, 5708, 109, 91, 69),
  [390] = Vertex.new(4948, 4769, 6076, 150, 149, 148),
  [477] = Vertex.new(4948, 4825, 5604, 164, 161, 158),
})
local asgarianCaveLadder = Models.objects["asgarnian cave ladder"]
local bluriteRock = Model.new(3252, {
  [207] = Vertex.new(-108, 494, 77, 127, 127, 127),
  [2751] = Vertex.new(120, -343, -234, 127, 127, 128),
  [2752] = Vertex.new(120, -343, -234, 127, 127, 128),
  [2803] = Vertex.new(121, -343, -232, 127, 127, 128),
  [2932] = Vertex.new(221, -66, 252, 127, 127, 128),
})

--Items
local bluriteOre = Models.items["blurite ore"]
local ironBar = Models.items["iron bar"]

--QuestItems
local swordPortait = Model.new(144, {
  [1] = Vertex.new(-128, 0, -160, 142, 130, 130),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Squire Asrol in Falador's White Knights' Castle courtyard.",
    title = "Getting started",
    neededItems = { ["Redberry pie"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(2973, 2725, 3342) },
    postconditions = { Condition.DistanceTo:new(2973, 2725, 3342, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(faladorSquire),
      Action.ConversationHighlight:new("Chat"),
      Action.ConversationHighlight:new("And how is life as a squire?"),
      Action.ConversationHighlight:new("I can make a new sword if you like..."),
      Action.ConversationHighlight:new("So would these dwarves make another one?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Squire Asrol.",
    actions = { Action.ModelHighlight:new(faladorSquire) },
    postconditions = { Condition.ConversationText:new("be with Reldo") },
  },
  {
    text = "Talk to Reldo in Varrock Palace's library about the Imcando dwarf tribe.<ul><li>If The Giant Dwarf quest is complete, skip this step.</li><li>If wielding ring of charos remove that, otherwise option does not appear.</li></ul>",
    title = "The Last of Imcandoria",
    actions = { Action.Direction:new(3210, 1253, 3494) },
    postconditions = { Condition.DistanceTo:new(3210, 1253, 3494, 15) },
  },
  {
    actions = {
      Action.ModelHighlight:new(reldo),
      Action.ConversationHighlight:new("What do you know about the Imcando dwarves?"),
    },
    postconditions = { Condition.ConversationText:new("They REALLY like redberry pie.") },
  },
  {
    text = "Talk to Thurgo south of Port Sarim (north of fairy ring AIQ) with a Redberry pie in the inventory.",
    actions = { Action.Direction:new(3001, 549, 3145) },
    postconditions = { Condition.DistanceTo:new(3001, 549, 3145, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(thurgo),
      Action.ConversationHighlight:new("Something else."),
      Action.ConversationHighlight:new("Would you like some redberry pie?"),
    },
    postconditions = {
      Condition.ConversationText:new("Anyone who makes pie like THAT has got to be alright!"),
    }, --TODO: Didn't test this step correctly
  },

  {
    text = "Talk to him again.",
    actions = {
      Action.ConversationHighlight:new("Something else."),
      Action.ConversationHighlight:new("Can you make me a special sword?"),
    },
    postconditions = { Condition.ConversationText:new("I'll go and ask his squire and see if I can find one.") },
  },
  {
    text = "Talk to Squire Asrol.",
    title = "The portrait",
    actions = { Action.Direction:new(2973, 2725, 3342) },
    postconditions = { Condition.DistanceTo:new(2973, 2725, 3342, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(faladorSquire),
      Action.ConversationHighlight:new("Chat"),
    },
    postconditions = {
      Condition.ConversationText:new("Please don't let him catch you! He MUSTN'T know what happened!"),
    },
  },
  {
    text = "Climb the ladder to the east, then the staircase behind Sir Renitee.",
    actions = { Action.Direction:new(2994, 2725, 3341) },
    postconditions = { Condition.DistanceTo:new(2993, 2725, 3341, 11) },
  },
  {
    actions = { Action.ModelHighlight:new(faladorLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(2993, 3909, 3341, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(faladorStaircase),
    },
    postconditions = { Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5) },
  },
  {
    text = "Search the cupboard in Sir Vyvin's room for the portrait.",
    actions = { Action.Direction:new(2984, 5093, 3336.25) },
    postconditions = { Condition.InventoryContains:new(swordPortait, 1) },
  },
  {
    text = "Give Thurgo the portrait.",
    title = "The sword",
    actions = { Action.Direction:new(3001, 549, 3145) },
    postconditions = { Condition.DistanceTo:new(3001, 549, 3145, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(thurgo),
      Action.ConversationHighlight:new("Something else."),
    },
    postconditions = { Condition.ConversationText:new("Ok. I'll go and find them then.") },
  },
  {
    text = "Climb down the trapdoor on the hill east of Thurgo's house to enter the Asgarnian Ice Dungeon.",
    actions = { Action.ModelHighlight:new(asgarianCaveLadder) },
    postconditions = { Condition.DistanceTo:new(3009, 973, 9550, 10) },
  },
  {
    text = "Mine 4 blurite ore. One ore is needed for another quest, and one is needed for Falador medium diaries.",
    actions = { Action.Direction:new(3048, 1061, 9568) },
    postconditions = { Condition.DistanceTo:new(3049, 1181, 9566, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(bluriteRock) },
    postconditions = { Condition.InventoryContains:new(bluriteOre, 4) },
  },
  {
    text = "Talk to Thurgo with the materials.<ul><li>Bars can be withdrawn at the anvil in Thurgo's hut.</li></ul>",
    title = "Finishing up",
    neededItems = {
      ["Blurite ore"] = { quantity = 1 },
      ["Iron bar"] = { quantity = 2 },
    },
    actions = { Action.Direction:new(3001, 549, 3145) },
    postconditions = { Condition.DistanceTo:new(3001, 549, 3145, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(thurgo),
      Action.InventoryHighlight:new(ironBar),
      Action.InventoryHighlight:new(bluriteOre),
      Action.ConversationHighlight:new("Something else."),
    },
    postconditions = { Condition.ConversationText:new("Thurgo hands you a sword.") },
  },
  {
    text = "Report back to the Squire Asrol.",
    actions = { Action.Direction:new(2973, 2725, 3342) },
    postconditions = { Condition.DistanceTo:new(2973, 2725, 3342, 10) },
  },
  {
    actions = {
      Action.ConversationHighlight:new("Chat"),
      Action.ModelHighlight:new(faladorSquire),
    },
    postconditions = {
      Condition.QuestComplete:new(),
    },
  },
}

return Quest:new({
  name = "The Knight's Sword",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.short,
  releaseDate = 986515200,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Mining", 10),
    Types.QuestReq.ironmanOnlySkill("Cooking", 10, true),
    Types.QuestReq.ironmanOnlySkill("Smithing", 10, true),
  },
  neededItems = {
    ["Redberry pie"] = { quantity = 1, model = Models.items["redberry pie"], duringQuest = false },
    ["Iron bar"] = { quantity = 2, model = Models.items["iron bar"], duringQuest = false },
  },
  recommendedItems = {},
  combatNPCs = {},
})
