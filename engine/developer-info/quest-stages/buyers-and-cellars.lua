local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, Objects, Npcs, Items = Types.Model, Types.Vertex, Models.objects, Models.npcs, Models.items

-- This fire isn't the particles, but the "burning" logs 3d model under the particles.
local fire = Model.new(798, {
  [213] = Vertex.new(-298, 8, -297, 255, 197, 57, 0.8431),
  [215] = Vertex.new(298, 8, 299, 255, 197, 57, 0.8431),
  [787] = Vertex.new(-184, 103, 129, 236, 211, 182, 0.000),
  [795] = Vertex.new(222, 103, 76, 236, 211, 182, 0.000),
  [797] = Vertex.new(121, 169, -121, 236, 211, 182, 0.000),
})

local complexKey = Model.new(474, {
  [1] = Vertex.new(-24, -4, 32, 128, 114, 52),
  [2] = Vertex.new(-24, 4, 32, 128, 114, 52),
  [3] = Vertex.new(-24, 0, 32, 128, 114, 52),
  [4] = Vertex.new(-36, 4, 20, 145, 130, 59),
  [5] = Vertex.new(-60, -4, 40, 145, 130, 59),
})

local goldenChalice = Model.new(882, {
  [1] = Vertex.new(-32, 128, 32, 135, 119, 41),
  [2] = Vertex.new(-8, 104, 8, 135, 119, 41),
  [3] = Vertex.new(0, 104, 8, 135, 119, 41),
  [4] = Vertex.new(32, 128, 32, 135, 119, 41),
  [5] = Vertex.new(8, 104, 8, 135, 119, 41),
})

local chaliceCase = Model.new(150, {
  [101] = Vertex.new(-84, 404, 84, 167, 159, 171, 0.4118),
  [120] = Vertex.new(48, 404, 112, 167, 159, 171, 0.4118),
  [126] = Vertex.new(96, 404, 72, 167, 159, 171, 0.4118),
  [127] = Vertex.new(96, 404, 72, 167, 159, 171, 0.4118),
  [130] = Vertex.new(96, 404, 72, 167, 159, 171, 0.4118),
})

local thievingGuildCellarTrapDoor = Action.Direction:new(3223, 1125, 3268.25)
local southLumbridgeWall = Action.Direction:new(3213, 1477, 3208)
local closeToSouthLumbridgeWall = Condition.DistanceTo:new(3213, 1477, 3208, 2)
local fatherUrhneysFrontDoor = Action.Direction:new(3207, 869, 3151)
local fatherUrhneysWindow = Action.Direction:new(3209, 869, 3152)
local closeToFatherUrhneysDoor = Condition.DistanceTo:new(3207, 869, 3149, 2)
local swampTree = Action.Direction:new(3214, 845, 3150)
local closeToThieveingGuildCellarLadder = Condition.DistanceTo:new(4664, 5, 5891, 2)

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Guildmaster Darren Lightfinger in the Thieves' Guild north of the house with a furnace in Lumbridge (enter the trapdoor).",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Lumbridge lodestone",
      url = "Lumbridge_lodestone_icon.png",
    },
    actions = {
      thievingGuildCellarTrapDoor,
      Action.ModelHighlight:new(Npcs["darren lightfinger"]),
      -- Yes this period is actually here.
      Action.ConversationHighlight:new("What are you doing down here? (Skip speech.)"),
      Action.ConversationHighlight:new("And what is it you need done? (Skip speech)"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    actions = {},
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue the conversation with Guildmaster Darren Lightfinger.",
    actions = {
      Action.ConversationHighlight:new("No, I think I've got the hang of this."),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("Thanks.") },
  },
  {
    text = "Go to Lumbridge castle.",
    title = "South Lumbridge castle wall",
    actions = {
      Action.ModelHighlight:new(Objects["thieves guild cellar ladder"]),
      southLumbridgeWall,
    },
    postconditions = { closeToSouthLumbridgeWall },
  },
  {
    text = "Talk to Chief Thief Robin, south of Lumbridge Castle by the castle wall.",
    title = "The chalice",
    neededItems = { ["Logs"] = { quantity = 1 }, ["Swamp tree"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(Npcs["chief thief robin"]),
      Action.ConversationHighlight:new("Go ahead."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Be subtle...if an adventurer can be subtle. See if you can pick his pocket for the key. Good luck."
      ),
    },
  },
  {
    text = "Go to Father Urhney's house south of Lumbridge Swamp.",
    actions = { fatherUrhneysFrontDoor },
    postconditions = { closeToFatherUrhneysDoor },
  },
  {
    text = "Speak to Father Urhney in his house, south in the Lumbridge Swamp.",
    actions = {
      Action.ModelHighlight:new(Npcs["father urhney"]),
      Action.ConversationHighlight:new("Nice chalice."),
      Action.ConversationHighlight:new("Can I have a look at it?"),
    },
    postconditions = { Condition.ConversationText:new("And get grubby fingermarks over it? I think not.") },
  },
  {
    text = "Chop a log from the swap tree.",
    actions = { swampTree },
    postconditions = {
      Condition.InventoryContains:new(Items["logs"]),
      Condition.InventoryContains:new(Items["any logs"]),
    },
  },
  {
    text = "Light a fire outside one of Urhney's windows.",
    actions = {
      fatherUrhneysWindow,
      Action.InventoryHighlight:new(Items["logs"]),
      Action.InventoryHighlight:new(Items["any logs"]),
    },
    postconditions = { Condition.ModelVisible:new(fire) },
  },
  {
    text = "Talk to Urhney and mention the chalice then the fire.",
    actions = {
      Action.ModelHighlight:new(Npcs["father urhney"]),
      Action.ConversationHighlight:new("Nice chalice."),
      Action.ConversationHighlight:new("Fire! Fire!"),
    },
    postconditions = { Condition.ConversationText:new("Oh, no! My house...that I built with my own two hands!") },
  },
  {
    text = "Pickpocket Urhney quickly to get a complex key.",
    actions = { Action.ModelHighlight:new(Npcs["father urhney"]) },
    postconditions = { Condition.InventoryContains:new(complexKey) },
  },
  {
    text = "Open the display case.",
    actions = { Action.ModelHighlight:new(chaliceCase) },
    postconditions = { Condition.InventoryContains:new(goldenChalice) },
  },
  {
    text = "Return to Darren's cellar.",
    title = "Finishing up",
    neededItems = { ["Golden chalice"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { thievingGuildCellarTrapDoor },
    postconditions = { closeToThieveingGuildCellarLadder },
  },
  {
    text = "Use the chalice on Darren.",
    actions = {
      Action.InventoryHighlight:new(goldenChalice),
      Action.ModelHighlight:new(Npcs["darren lightfinger"]),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Buyers and Cellars",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1272412800,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Thieving", 5) },
  neededItems = { ["Logs"] = { quantity = 1, model = Items["logs"], duringQuest = true } },
  recommendedItems = {},
  combatNPCs = {},
})
