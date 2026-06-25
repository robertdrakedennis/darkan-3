local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local apprentice = Model.new(5181, {
  [641] = Vertex.new(32, 764, -104, 115, 115, 105),
})
local apprenticeHl = Action.ModelHighlight:new(apprentice)

local dougal = Model.new(4722, {
  [794] = Vertex.new(32, 816, -104, 115, 115, 105),
})

local bindingNeck = Model.new(672, {
  [1] = Vertex.new(12, 11, -49, 10, 116, 14),
})

local fireRuins = Model.new(885, {
  [310] = Vertex.new(234, 1835, 3794, 134, 39, 12),
})

local fireAltar = Model.new(3297, {
  [3003] = Vertex.new(-482, 0, 699, 55, 54, 50),
})

local edvin = Model.new(4809, {
  [1405] = Vertex.new(-24, 0, -12, 34, 31, 26),
  [1406] = Vertex.new(16, 0, -12, 34, 31, 26),
  [1407] = Vertex.new(0, 0, 20, 34, 31, 26),
})

local switch = Model.new(792, {
  [194] = Vertex.new(52, 300, 108, 173, 109, 219, 0.4980),
})

local tool = Model.new(480, {
  [1] = Vertex.new(0, 36, -84, 88, 87, 67),
})

local shrug = Model.new(6570, {
  [985] = Vertex.new(32, 642, 60, 91, 91, 83),
})

local enchantedEmeralds = Model.new(114, {
  [1] = Vertex.new(-16, 104, 36, 64, 215, 44, 0.7490),
  [2] = Vertex.new(-36, 68, -44, 64, 215, 44, 0.7490),
  [3] = Vertex.new(-52, 68, 36, 64, 215, 44, 0.7490),
  [5] = Vertex.new(-16, 88, -44, 64, 215, 44, 0.7490),
  [7] = Vertex.new(-52, 36, 36, 51, 172, 36, 0.7490),
})

local animationTable = Model.new(4677, {
  [970] = Vertex.new(-400, 404, -276, 66, 62, 60),
})

local lump = Model.new(591, {
  [1] = Vertex.new(172, 4, 188, 79, 79, 86),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Apprentice Clerval in the basement of the Mage Training Arena.",
    title = "Getting started",
    actions = { Action.Direction:new(3357, 3205, 3305) },
    postconditions = { Condition.DistanceTo:new(3619, 965, 4814, 2) },
  },
  {
    actions = { apprenticeHl },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Wizard Dougal who is also in the basement.<ul><li>You need four free spaces in your inventory for Dougal to give you items. If he doesn't give you the items, he will not accept the steam runes.</li></ul>",
    title = "Wizard Dougal",
    neededItems = {
      ["Mind rune"] = { quantity = 5 },
      ["Body rune"] = { quantity = 5 },
      ["Water rune"] = { quantity = 5 },
      ["Water talisman"] = { quantity = 1 },
      ["Pure essence (unnoted)"] = { quantity = 5 },
      ["Access to fire altar"] = { quantity = 1 },
    },
    actions = {
      Action.ModelHighlight:new(dougal),
      Action.ConversationHighlight:new("Complement him and massage his ego."),
    },
    postconditions = { Condition.InventoryContains:new(bindingNeck) },
  },
  {
    text = "Wear the binding necklace that Dougal gave you.",
    actions = { Action.InventoryHighlight:new(bindingNeck) },
    postconditions = { Condition.InventoryDoesNotContain:new(bindingNeck) },
  },
  {
    text = "Craft at least one steam rune. <ul><li>The remaining runes (4 steam runes, 5 mind runes, 5 body runes) can be obtained or bought ahead of time.<li>We use the fire altar, you can also use the water altar with fire runes and water talisman.</li></ul>",
    actions = {
      Action.Direction:new(3314, 1365, 3253, { distance = 15 }),
      Action.ModelHighlight:new(fireRuins, { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2577, 2141, 4845, 5) },
    warning = "Don't left click the altar as you will make fire runes instead.",
  },
  {
    actions = {
      Action.InventoryHighlight:new(Models.items["water rune"]),
      Action.ModelHighlight:new(fireAltar),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["steam rune"], 5) },
  },

  {
    text = "Talk to Wizard Dougal.",
    actions = { Action.Direction:new(3357, 3205, 3305) },
    postconditions = { Condition.DistanceTo:new(3619, 965, 4814, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dougal),
    },
    postconditions = { Condition.ConversationText:new("for a novice like yourself") },
  },
  {
    text = "Give the runes to Clerval.",
    actions = { apprenticeHl },
    postconditions = { Condition.ConversationText:new("I haven't managed") },
  },
  {
    text = "Talk to Wizard Edvin who is also in the basement. <ul><li>You will need to dismiss any pets currently active</li></ul>",
    title = "Wizard Edvin",
    actions = { Action.ModelHighlight:new(edvin), Action.ConversationHighlight:new("I guess I'll give it a try.") },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Solve the portal maze.<ul><li>From the start, go through the west portal, then go to the northern portal 3 times.</li></ul>",
    actions = {
      Action.Direction:new(-1.5, 400, 0, { instance = true }),
      Action.Direction:new(-4, 400, 1.5, { instance = true }),
      Action.Direction:new(-8, 400, 9.5, { instance = true }),
      Action.Direction:new(-8, 400, 13.5, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(0, 0, 20, 0, true) },
  },
  {
    text = "Exit the maze.",
    actions = { Action.ModelHighlight:new(switch, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(tool) },
  },
  {
    text = "Take Edvin's tool to Clerval.",
    actions = { apprenticeHl },
    postconditions = { Condition.InventoryDoesNotContain:new(tool) },
  },
  {
    text = "Talk to Wizard Shug on the top floor of the arena.",
    title = "Wizard Shug",
    neededItems = {
      ["Emerald"] = { quantity = 2 },
      ["Pure essence (unnoted)"] = { quantity = 20 },
      ["Pizzaz points"] = { quantity = 5 },
    },
    actions = { Action.Direction:new(3618, 1265, 4814) },
    postconditions = { Condition.DistanceTo:new(3358, 3205, 3305, 1), Condition.DistanceTo:new(3365, 4645, 3320, 2) },
  },
  {
    actions = { Action.Direction:new(3358.5, 3805, 3306) },
    postconditions = { Condition.DistanceTo:new(3357, 4645, 3307, 1), Condition.DistanceTo:new(3365, 4645, 3320, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(shrug),
      Action.Direction:new(3365, 4645, 3320, { distance = 5 }),
      Action.ConversationHighlight:new("Get back on topic!"),
    },
    postconditions = { Condition.ConversationText:new("I understand. Goodbye") },
  },
  {
    text = "Go back down and enchant the emeralds using the workbench by Clerval.",
    actions = { Action.Direction:new(3357, 4645, 3306) },
    postconditions = { Condition.DistanceTo:new(3360, 3205, 3306, 2), Condition.DistanceTo:new(3619, 965, 4814, 20) },
  },
  {
    actions = { Action.Direction:new(3357, 3205, 3305) },
    postconditions = { Condition.DistanceTo:new(3619, 965, 4814, 20) },
  },
  {
    actions = { Action.Direction:new(3626.5, 1465, 4818) },
    postconditions = { Condition.InventoryContains:new(enchantedEmeralds, 2) },
  },
  {
    text = "Talk to Clerval, holding 20 pure essence.",
    title = "Finishing up",
    neededItems = { ["Pure essence (unnoted)"] = { quantity = 20 } },
    actions = { apprenticeHl, Action.ConversationHighlight:new("Comfort and reassure him.") },
    postconditions = { Condition.ConversationText:new("Now, let's do this thing") },
  },
  {
    text = "Fix the animation table nearby. If you're stuck, use the wiki button for the result. When finished, click the green button.",
    actions = {
      Action.ModelHighlight:new(animationTable),
      Action.ConversationHighlight:new("You are useless, but, yes, I'll do it."),
    },
    postconditions = { Condition.InventoryContains:new(tool) },
  },
  {
    text = "Use the carving tool on the lump of rune essence.",
    actions = { Action.InventoryHighlight:new(lump), Action.InventoryHighlight:new(tool) },
    postconditions = { Condition.InventoryDoesNotContain:new(lump) },
  },
  { text = "Talk to Clerval.", actions = { apprenticeHl }, postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Rune Mechanics",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.short,
  releaseDate = 1264982400,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Construction", 25),
    Types.QuestReq.skill("Magic", 27),
    Types.QuestReq.skill("Runecrafting", 20),
  },
  neededItems = {
    ["Water rune"] = { quantity = 5, model = Models.items["water rune"] },
    ["Mind rune"] = { quantity = 5, model = Models.items["mind rune"] },
    ["Body rune"] = { quantity = 5, model = Models.items["body rune"] },
    ["Emerald"] = { quantity = 2, model = Models.items["emerald"] },
    ["Water talisman"] = { quantity = 1, model = Models.items["water talisman"] },
    ["Pure essence (unnoted)"] = { quantity = 21, model = Models.items["pure essence"] },
    ["Progress hat"] = { quantity = 1 },
    ["Access to fire altar"] = {
      quantity = 1,
      model = Model.any({
        Models.items["wicked hood"],
        Models.items["fire talisman"],
        Models.items["fire tiara"],
      }),
    },
  },
})
