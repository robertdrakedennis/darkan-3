local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--NPCs
local oldCrone = Model.new(3315, {
  [1767] = Vertex.new(24, 738, -38, 29, 28, 27),
  [1791] = Vertex.new(-24, 738, -38, 29, 28, 27),
  [1813] = Vertex.new(-2, 716, -57, 106, 77, 54),
  [1819] = Vertex.new(2, 716, -57, 106, 77, 54),
  [1823] = Vertex.new(6, 716, -52, 106, 77, 54),
})
local galahad = Model.new(4005, {
  [1612] = Vertex.new(-2, 725, -59, 106, 77, 54),
  [1617] = Vertex.new(-7, 724, -51, 106, 77, 54),
  [1620] = Vertex.new(2, 725, -59, 106, 77, 54),
  [1622] = Vertex.new(7, 724, -51, 106, 77, 54),
  [3049] = Vertex.new(0, 735, -7, 27, 138, 126),
})
local blackKnightTitan = Model.new(1686, {
  [471] = Vertex.new(-24, 612, -72, 92, 92, 84),
  [473] = Vertex.new(8, 620, -72, 92, 92, 84),
  [744] = Vertex.new(0, 980, -216, 60, 55, 55),
  [753] = Vertex.new(0, 964, -216, 60, 55, 55),
  [1091] = Vertex.new(120, 1132, -184, 0, 0, 0),
})
local fisherman = Model.new(4296, {
  [2650] = Vertex.new(-2, 725, -59, 106, 77, 54),
  [2655] = Vertex.new(-7, 724, -51, 106, 77, 54),
  [2658] = Vertex.new(2, 725, -59, 106, 77, 54),
  [2660] = Vertex.new(7, 724, -51, 106, 77, 54),
  [3499] = Vertex.new(0, 735, -7, 27, 138, 126),
})
local fisherKing = Model.new(7146, {
  [3373] = Vertex.new(-2, 725, -59, 106, 77, 54),
  [3381] = Vertex.new(2, 725, -59, 106, 77, 54),
  [3383] = Vertex.new(7, 724, -51, 106, 77, 54),
  [6192] = Vertex.new(-65, 670, 50, 88, 14, 7),
  [6223] = Vertex.new(65, 670, 50, 88, 14, 7),
})

--Quest Items
local magicWhistle = Model.new(126, {
  [1] = Vertex.new(-36, 56, 0, 0, 0, 0),
  [2] = Vertex.new(24, 48, 16, 0, 0, 0),
  [3] = Vertex.new(24, 56, 0, 0, 0, 0),
  [5] = Vertex.new(-36, 48, 16, 0, 0, 0),
  [13] = Vertex.new(24, 16, 16, 114, 105, 104),
})
local grailBell = Model.new(408, {
  [21] = Vertex.new(24, 68, -72, 138, 127, 126),
  [24] = Vertex.new(24, 68, -72, 138, 127, 126),
  [28] = Vertex.new(24, 68, -72, 138, 127, 126),
  [72] = Vertex.new(-56, 108, 36, 138, 127, 126),
  [118] = Vertex.new(72, 108, 36, 138, 127, 126),
})
local magicGoldFeather = Model.new(174, {
  [1] = Vertex.new(64, 4, 0, 136, 135, 54),
  [2] = Vertex.new(88, 4, 0, 136, 135, 54),
  [3] = Vertex.new(64, 0, -24, 136, 135, 54),
  [7] = Vertex.new(88, 0, 0, 136, 135, 54),
  [9] = Vertex.new(64, -4, -24, 136, 135, 54),
})
local holyGrail = Model.new(408, {
  [1] = Vertex.new(20, 8, 8, 124, 114, 10),
  [2] = Vertex.new(8, 64, 28, 124, 114, 10),
  [3] = Vertex.new(8, 8, 20, 124, 114, 10),
  [5] = Vertex.new(28, 64, 8, 124, 114, 10),
  [11] = Vertex.new(20, 92, 60, 124, 114, 10),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to King Arthur in Camelot Castle.",
    title = "Royal hunt",
    actions = { Action.Direction:new(2762, 645, 3510) },
    postconditions = { Condition.DistanceTo:new(2762, 645, 3510, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["king arthur"]),
      Action.ConversationHighlight:new("Tell me of this quest."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", actions = {}, postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to King Arthur.",
    actions = {
      Action.ModelHighlight:new(NPCs["king arthur"]),
      Action.ConversationHighlight:new("Tell me of this quest."),
    },
    postconditions = { Condition.ConversationText:new("He has set up his workshop in the room next to the library.") },
  },
  {
    text = "Go to the 1st floor (2nd floor[US]) using the staircase and talk to Merlin.",
    actions = { Action.ModelHighlight:new(Objects["camelot ground floor stairs"]) },
    postconditions = { Condition.DistanceToWithHeight:new(2751, 1925, 3513, 8) },
  },
  {
    actions = { Action.Direction:new(2767, 1925, 3501) },
    postconditions = { Condition.ModelVisible:new(NPCs["merlin"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["merlin"]),
      Action.ConversationHighlight:new("Tell me of this quest."),
      Action.ConversationHighlight:new("Where can I find Sir Galahad?"),
    },
    postconditions = { Condition.ConversationText:new("He lives somewhere west of McGrubor's Wood I think.") },
  },
  {
    text = "Go to Entrana by talking to a Monk of Entrana. You will be asked to bank armour and weapons, which can also be done at the bank deposit box at the docks.",
    title = "The Fisher King",
    actions = { Action.Direction:new(3047, 741, 3236) },
    postconditions = { Condition.DistanceTo:new(3047, 741, 3236, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["monk of entrana"]) },
    postconditions = { Condition.DistanceTo:new(2834, 965, 3335, 2) },
  },
  {
    text = "Speak to the high priest, who is in the main chapel of the church.",
    actions = { Action.Direction:new(2851, 1010, 3348) },
    postconditions = { Condition.DistanceTo:new(2851, 1010, 3348, 8) },
  },
  {
    actions = {
      Action.ConversationHighlight:new("What can you tell me about this place?"),
      Action.ModelHighlight:new(NPCs["entrana high priest"]),
    },
    postconditions = {
      Condition.ConversationText:new("Nor do I really care."),
    },
  },
  {
    text = "Speak to the crone who appears.",
    actions = {
      Action.ModelHighlight:new(oldCrone),
      Action.ConversationHighlight:new("I will go searching."),
    },
    postconditions = { Condition.ConversationText:new("Good luck with that.") },
  },
  {
    text = "Head to Seers' Village lodestone.",
    title = "Another realm",
    actions = { Action.Direction:new(2689, 1077, 3482) },
    postconditions = { Condition.DistanceTo:new(2689, 1077, 3482, 2) },
  },
  {
    text = "Talk to Galahad, located in a building west of McGrubor's Wood and east of the coal trucks.<ul><li>You must speak to the crone on Entrana first, or he will not give you the holy table napkin.</li></ul>",
    actions = { Action.Direction:new(2613, 965, 3477) },
    postconditions = { Condition.DistanceTo:new(2613, 965, 3477, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(galahad),
      Action.ConversationHighlight:new("I'm on a quest to find the Holy Grail!"),
      Action.ConversationHighlight:new("Why did you leave?"),
    },
    postconditions = {
      Condition.ConversationText:new("I have a feeling you may need to come back and speak to me anyway..."),
    },
  },
  {
    text = "Head to the Draynor Manor.",
    actions = { Action.Direction:new(3108, 965, 3352) },
    postconditions = { Condition.DistanceTo:new(3108, 965, 3352, 12) },
  },
  {
    text = "Take the holy table napkin to the southern room of the top-most floor of Draynor Manor.",
    actions = { Action.ModelHighlight:new(Objects["draynor manor front door"]) },
    postconditions = { Condition.DistanceTo:new(3109, 965, 3355, 1) },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["draynor manor ground floor stairs"]) },
    postconditions = { Condition.DistanceToWithHeight:new(3108, 2213, 3366, 2) },
  },
  {
    actions = { Action.Direction:new(3105, 2213, 3363) },
    postconditions = { Condition.DistanceToWithHeight:new(3105, 3429, 3362, 2) },
  },
  {
    text = "Pick up the two magic whistles on the table.",
    actions = { Action.Direction:new(3107, 3429, 3359) },
    postconditions = { Condition.InventoryContains:new(magicWhistle, 2) },
  },
  {
    text = "Bank and get your combat gear (if you haven't already). Bring Excalibur. Move to the next step when you're ready.",
  },
  {
    text = "Go to Brimhaven and blow the whistle under the wooden watchtower.<ul><li>Optional: If you have 40 Mining, and haven't completed the easy Karamja diaries, mine gold nearby before using the whistle.</li></ul>",
    title = "Black guardian",
    neededItems = { ["Magic whistle"] = { quantity = 2, model = magicWhistle }, ["Excalibur"] = { quantity = 1 } },
    actions = { Action.Direction:new(2742, 517, 3235) },
    postconditions = { Condition.DistanceTo:new(2806, 909, 4715, 4) },
  },
  {
    text = "Go north-west and defeat Black Knight Titan with Excalibur.<ul><li>Low levels may find it useful to safespot the Black Knight Titan with Magic, Ranged, or Necromancy and last hit him with Excalibur.</li></ul>",
    actions = { Action.ModelHighlight:new(blackKnightTitan) },
    postconditions = { Condition.DistanceTo:new(2790, 661, 4722, 0) },
  },
  {
    text = "Head south along the river and talk to the fisherman by the river. <b>Be sure to keep the magic whistle in your inventory from this point forward.</b>",
    actions = { Action.Direction:new(2797, 1149, 4704) },
    postconditions = { Condition.ModelVisible:new(fisherman) },
  },
  {
    actions = {
      Action.ModelHighlight:new(fisherman),
      Action.ConversationHighlight:new("Any idea how to get into the castle?"),
    },
    postconditions = {
      Condition.ConversationText:new("You must be blind then. There's ALWAYS bells there when I go to the castle."),
    },
  },
  {
    text = "Go south-west, to the castle. Take the grail bell on the ground outside the castle and ring it.",
    title = "Heirloom",
    actions = { Action.Direction:new(2762, 3621, 4694) },
    postconditions = { Condition.ModelVisible:new(grailBell) },
  },
  {
    actions = { Action.ModelHighlight:new(grailBell) },
    postconditions = { Condition.InventoryContains:new(grailBell) },
  },
  {
    actions = { Action.InventoryHighlight:new(grailBell) },
    postconditions = { Condition.DistanceTo:new(2761, 3621, 4692, 1) },
  },
  {
    text = "Head upstairs and talk to the Fisher King.",
    actions = { Action.Direction:new(2761, 3621, 4681) },
    postconditions = { Condition.DistanceToWithHeight:new(2762, 4581, 4682, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(fisherKing),
      Action.ConversationHighlight:new("You don't look too well."),
    },
    postconditions = { Condition.ConversationText:new("I shall go and see if I can find him.") },
  },
  {
    text = "Talk to King Arthur in Camelot Castle. Make sure you have space to receive the magic gold feather.",
    actions = { Action.Direction:new(2762, 645, 3510) },
    postconditions = { Condition.DistanceTo:new(2762, 645, 3510, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["king arthur"]) },
    postconditions = { Condition.InventoryContains:new(magicGoldFeather) },
  },
  {
    text = "Teleport to the Falador lodestone.",
    actions = { Action.Direction:new(2967, 1461, 3403) },
    postconditions = { Condition.DistanceTo:new(2967, 1461, 3403, 1) },
  },
  {
    text = "Head north to the Goblin Village.",
    actions = { Action.Direction:new(2955, 645, 3502) },
    postconditions = { Condition.DistanceTo:new(2955, 645, 3502, 8) },
  },
  {
    text = "Go into the eastern house, and right-click to open the sacks to find Sir Percival.",
    actions = {
      Action.Direction:new(2962, 645, 3504),
      Action.ConversationHighlight:new("Your father wishes to speak to you."),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(magicWhistle, 2) }, --not properly tested
  },
  {
    text = "Return to the Fisher King's castle in his realm by blowing your whistle north west of Brimhaven (or by using Fairy ring BJR).",
    actions = { Action.Direction:new(2742, 517, 3235) },
    postconditions = { Condition.DistanceTo:new(2678, 1005, 4715, 4) },
  },
  {
    actions = { Action.Direction:new(2635, 3621, 4693) },
    postconditions = { Condition.DistanceToWithHeight:new(2635, 3621, 4693, 8) },
  },
  {
    text = "Go up the staircase on the east side of the castle, climb to the top floor and grab the Holy grail.",
    actions = { Action.Direction:new(2648.5, 3621, 4684) },
    postconditions = { Condition.DistanceToWithHeight:new(2651, 4581, 4684, 4) },
  },
  {
    actions = { Action.Direction:new(2651, 4581, 4684) },
    postconditions = { Condition.DistanceToWithHeight:new(2650, 5541, 4684, 2) },
  },
  {
    actions = { Action.Direction:new(2649, 5541, 4684) },
    postconditions = { Condition.InventoryContains:new(holyGrail) },
  },
  {
    text = "Go back to King Arthur.",
    actions = { Action.Direction:new(2762, 645, 3510) },
    postconditions = { Condition.DistanceTo:new(2762, 645, 3510, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["king arthur"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Holy Grail",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1027382400,
  prereqQuests = { "Merlin's Crystal" },
  questReqs = { Types.QuestReq.skill("Attack", 30) },
  neededItems = { ["Excalibur"] = { quantity = 1, model = Items["excalibur"], duringQuest = false } },
  recommendedItems = {
    ["Camelot teleport tablets"] = { quantity = 5 },
    ["Amulet of glory"] = { quantity = 1 },
    ["Wicked hood"] = { quantity = 1 },
    ["Tokkul-Zo, for quick access to the Fairy ring network"] = { quantity = 1 },
    ["Activated Draynor, Karamja, and Port Sarim lodestones"] = { quantity = 1 },
  },
  combatNPCs = { ["Black Knight Titan"] = { level = "28", quantity = 1 } },
})
