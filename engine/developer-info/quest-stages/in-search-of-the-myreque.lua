local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local hoodedVanstrom = Model.new(4257, {
  [2701] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2709] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2711] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3251] = Vertex.new(28, 771, 20, 75, 38, 23),
  [3372] = Vertex.new(-28, 771, 20, 75, 38, 23),
})
local cyreg = Model.new(4779, {
  [1348] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [1356] = Vertex.new(2, 725, -59, 109, 80, 57),
  [1358] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3829] = Vertex.new(65, 670, 50, 34, 49, 31),
  [3864] = Vertex.new(-65, 670, 50, 34, 49, 31),
})
local curpile = Model.new(4143, {
  [2374] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2379] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [2382] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2384] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3325] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local skeletonHellhound = Model.new(2559, {
  [74] = Vertex.new(20, 404, -348, 86, 86, 79),
  [80] = Vertex.new(20, 404, -348, 86, 86, 79),
  [83] = Vertex.new(12, 400, -360, 86, 86, 79),
  [93] = Vertex.new(-12, 400, -360, 86, 86, 79),
  [102] = Vertex.new(-20, 404, -348, 86, 86, 79),
})
--#endregion
--#region Objects
local brokenBridge = Model.new(2100, {
  [1341] = Vertex.new(-256, 100, -219, 126, 118, 97),
  [1349] = Vertex.new(-256, 100, 179, 103, 96, 79),
  [1689] = Vertex.new(264, 5, 216, 115, 107, 88),
})
--#endregion
--#region Items
local steelDagger = Model.new(594, {
  [42] = Vertex.new(12, 9, -135, 103, 108, 113),
  [46] = Vertex.new(12, 9, -135, 103, 108, 113),
  [149] = Vertex.new(12, 9, -135, 103, 108, 113),
  [151] = Vertex.new(12, 9, -135, 103, 108, 113),
  [410] = Vertex.new(-8, 10, -135, 93, 98, 102),
})
local steelMace = Model.multi({
  Model.new(930, {
    [136] = Vertex.new(75, 59, 167, 93, 98, 102),
    [138] = Vertex.new(82, 66, 163, 93, 98, 102),
    [139] = Vertex.new(82, 66, 163, 67, 70, 73),
    [141] = Vertex.new(90, 59, 160, 67, 70, 73),
    [142] = Vertex.new(90, 59, 160, 67, 70, 73),
  }),
  Model.new(18, {
    [1] = Vertex.new(-53, 31, -151, 93, 98, 102),
    [2] = Vertex.new(-73, 28, -158, 93, 98, 102),
    [3] = Vertex.new(-67, 31, -144, 93, 98, 102),
    [5] = Vertex.new(-59, 29, -164, 93, 98, 102),
    [6] = Vertex.new(-73, 28, -158, 93, 98, 102),
  }),
})
local steelSword = Model.multi({
  Model.new(372, {
    [53] = Vertex.new(10, 2, 124, 96, 71, 40),
    [86] = Vertex.new(54, 8, 100, 72, 75, 79),
    [93] = Vertex.new(-54, 8, 100, 72, 75, 79),
    [153] = Vertex.new(15, 2, 119, 18, 19, 19),
    [195] = Vertex.new(-15, 2, 119, 18, 19, 19),
  }),
  Model.new(12, {
    [1] = Vertex.new(9, 14, 101, 129, 135, 141),
    [3] = Vertex.new(9, 14, 119, 129, 135, 141),
    [8] = Vertex.new(-9, 14, 119, 129, 135, 141),
    [11] = Vertex.new(-9, 14, 101, 129, 135, 141),
    [12] = Vertex.new(-9, 14, 119, 129, 135, 141),
  }),
})
local steelLongsword = Model.new(438, {
  [23] = Vertex.new(10, 4, 145, 79, 63, 50),
  [117] = Vertex.new(15, 4, 140, 63, 65, 68),
  [147] = Vertex.new(-15, 4, 140, 63, 65, 68),
  [272] = Vertex.new(0, 4, -305, 107, 112, 117),
  [306] = Vertex.new(0, 4, -305, 93, 98, 102),
})
local steelWarhammer = Model.multi({
  Model.new(672, {
    [164] = Vertex.new(-23, 21, 164, 99, 104, 108),
    [536] = Vertex.new(22, 38, 207, 113, 118, 123),
    [546] = Vertex.new(22, 38, 207, 122, 127, 133),
    [598] = Vertex.new(-85, 17, 146, 118, 124, 129),
    [603] = Vertex.new(-85, 17, 146, 118, 124, 129),
  }),
  Model.new(30, {
    [3] = Vertex.new(-39, 37, 145, 118, 124, 129),
    [5] = Vertex.new(-21, 39, 134, 118, 124, 129),
    [15] = Vertex.new(-21, 40, 149, 118, 124, 129),
    [27] = Vertex.new(-77, 29, 140, 118, 124, 129),
    [29] = Vertex.new(-59, 32, 127, 118, 124, 129),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Vanstrom Klause in the Hair of the Dog tavern in Canifis.",
    title = "Getting started",
    actions = {
      Action.Direction:new(3503, 693, 3477, { distance = 8 }),
      Action.ModelHighlight:new(hoodedVanstrom, { distance = 8 }),
      Action.ConversationHighlight:new("Why do they need help? Are they in trouble?"),
    },
    postconditions = { Condition.ConversationText:new("a mace and a warhammer") },
  },
  {
    actions = {
      Action.Direction:new(3503, 693, 3477, { distance = 8 }),
      Action.ModelHighlight:new(hoodedVanstrom, { distance = 8 }),
      Action.ConversationHighlight:new("Perhaps I could help you out here."),
      Action.ConversationHighlight:new("Yes, I'll do it!"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Vanstrom Klause.",
    actions = {
      Action.Direction:new(3503, 693, 3477, { distance = 8 }),
      Action.ModelHighlight:new(hoodedVanstrom, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("they can come to rely upon") },
  },
  {
    text = "Talk to Cyreg Paddlehorn before the bridge east of Mort'ton. To get there, you can:<ul><li>Run through the swamp.</li><li>Fairy ring BKR and take the boat to the south-east.</li><li>Use a portal at War's Retreat tuned to the Barrows Brothers.</li></ul>",
    title = "The hideout",
    actions = {
      Action.Direction:new(3521, 197, 3285, { distance = 14 }),
      Action.ModelHighlight:new(cyreg, { distance = 14 }),
      Action.ConversationHighlight:new("Well, I guess they'll just die without weapons."),
      Action.ConversationHighlight:new("Resourceful enough to get their own steel weapons?"),
      Action.ConversationHighlight:new("If you don't tell me, their deaths are on your head!"),
      Action.ConversationHighlight:new("What kind of a man are you to say that you don't care?"),
      Action.ConversationHighlight:new("Give wooden planks to Cyreg."),
    },
    postconditions = { Condition.ConversationText:new("The boatman takes 3 wooden planks from you.") },
  },
  {
    text = "Board the boat.",
    actions = {
      Action.ModelHighlight:new(Models.objects["mort'ton boat"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.DistanceTo:new(3498, 69, 3380, 8) },
  },
  {
    text = "Climb the tree to the north.",
    actions = { Action.Direction:new(3502, 817, 3426) },
    postconditions = { Condition.DistanceToWithHeight:new(3502, 949, 3427, 1) },
  },
  {
    text = "Right-click <i>repair</i> the bridge until you reach the other side, then climb down the tree.",
    actions = { Action.ModelHighlight:new(brokenBridge) },
    postconditions = { Condition.DistanceTo:new(3502, 949, 3430, 0) },
  },
  {
    text = "Talk to Curpile Fyod. Quiz answers:<br><table><tbody><tr><th>Question</th><th>Answer</th></tr><tr><td>Name the only female member of the Myreque.</td><td>Sani Piliu</td></tr><tr><td>Who is the leader of the Myreque?</td><td>Veliaf Hurtz</td></tr><tr><td>What family is rumoured to rule Morytania?</td><td>Drakan</td></tr><tr><td>Who is the youngest member of the Myreque?</td><td>Ivan Strom</td></tr><tr><td>Which member of the Myreque was originally a scholar?</td><td>Polmafi Ferdygris</td></tr><tr><td>What does Myreque mean?</td><td>Hidden in Myre</td></tr><tr><td>What is the boatman's name?</td><td>Cyreg Paddlehorn</td></tr><tr><td>Who was previously a scholar?</td><td>Polmafi Ferdygris</td></tr></tbody></table>",
    actions = {
      Action.ModelHighlight:new(curpile),
      Action.ConversationHighlight:new("I've come to help the Myreque. I've brought weapons."),
    },
    postconditions = { Condition.ConversationText:new("I'll unlock it for you") },
  },
  {
    text = "Open the doors on the rock behind the tree and follow the tunnel.",
    actions = { Action.Direction:new(3509.5, 669, 3446.5) },
    postconditions = { Condition.DistanceTo:new(3500, 605, 9811, 8) },
  },
  {
    text = "Squeeze-past the stalagmite at the east wall, next to the stone brazier.",
    actions = { Action.Direction:new(3492, 941, 9824) },
    postconditions = { Condition.DistanceToWithHeight:new(3505, 3949, 9832, 8) },
  },
  {
    text = "Talk to Veliaf Hurtz.",
    title = "Meet the crew",
    actions = { Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]) },
    postconditions = { Condition.ConversationText:new("Ok, thanks.") },
  },
  {
    text = "Talk to Harold.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["harold evans"]),
      Action.ConversationHighlight:new("What's your job here?"),
    },
    postconditions = { Condition.ConversationText:new("I seem to cheat death") }, --lol
  },
  {
    text = "Talk to Sani.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["sani piliu"]),
      Action.ConversationHighlight:new("Tell me a bit about yourself."),
    },
    postconditions = { Condition.ConversationText:new("Who knows?") },
  },
  {
    text = "Talk to Radigad.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["radigad ponfit"]),
      Action.ConversationHighlight:new("What's your job here?"),
    },
    postconditions = { Condition.ConversationText:new("I like to think that") },
  },
  {
    text = "Talk to Polmafi.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["polmafi ferdygris"]),
      Action.ConversationHighlight:new("What are you doing here?"),
    },
    postconditions = { Condition.ConversationText:new("evil to survive") },
  },
  {
    text = "Talk to Ivan.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["ivan strom"]),
      Action.ConversationHighlight:new("What are you doing here?"),
    },
    postconditions = { Condition.ConversationText:new("serve him as faithfully") },
  },
  {
    text = "Talk to Veliaf again.",
    actions = { Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]) },
    postconditions = { Condition.ConversationText:new("through the door?") },
  },
  {
    postconditions = { Condition.ModelVisible:new(skeletonHellhound) },
  },
  {
    text = "Kill the Skeleton Hellhound.",
    title = "Finishing up",
    actions = { Action.ModelHighlight:new(skeletonHellhound) },
    postconditions = {
      Condition.ModelVisible:new(Models.items["big bones"]),
      Condition.DistanceTo:new(3503, 1581, 9838, 8),
    },
  },
  {
    text = "Talk to Veliaf.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("How do I get out of here?"),
    },
    postconditions = { Condition.ConversationText:new("still hanging around") },
  },
  {
    text = "Exit the cave through the entrance.",
    actions = { Action.Direction:new(3505, 2013, 9831) },
    postconditions = { Condition.DistanceTo:new(3491, 541, 9824, 8) },
  },
  {
    text = "Search the wall to the north.",
    actions = { Action.Direction:new(3480, 925, 9836.5) },
    postconditions = { Condition.DistanceTo:new(3480, 325, 9839, 2) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(3477, 925, 9846) },
    postconditions = { Condition.DistanceToWithHeight:new(3494, 293, 3465, 4) },
  },
  {
    text = "Talk to the stranger next to the stairs where Vanstrom was located.",
    actions = {
      Action.Direction:new(3503, 693, 3477, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["the stranger"], { distance = 8 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "In Search of the Myreque",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.short,
  releaseDate = 1105315200,
  prereqQuests = { "Nature Spirit" },
  questReqs = { Types.QuestReq.skill("Agility", 25) },
  neededItems = {
    ["Druid pouch (5+ charges)"] = { quantity = 5, model = Models.items["druid pouch"] },
    ["Normal planks"] = { quantity = 6, model = Models.items["plank"] },
    ["Steel dagger"] = { quantity = 1, model = steelDagger },
    ["Steel mace"] = { quantity = 1, model = steelMace },
    ["Steel swords"] = { quantity = 2, model = steelSword },
    ["Steel longsword"] = { quantity = 1, model = steelLongsword },
    ["Steel warhammer"] = { quantity = 1, model = steelWarhammer },
    ["Steel nails"] = { quantity = 75, model = Models.items["steel nails"] },
  },
  recommendedItems = {},
  combatNPCs = { ["Skeleton Hellhound"] = { level = "44", quantity = 1 } },
})
