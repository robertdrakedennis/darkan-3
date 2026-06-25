local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local edmond = Model.new(3813, {
  [1828] = Vertex.new(-2, 725, -59, 53, 39, 21),
  [1833] = Vertex.new(-7, 724, -51, 53, 39, 21),
  [1836] = Vertex.new(2, 725, -59, 53, 39, 21),
  [1838] = Vertex.new(7, 724, -51, 53, 39, 21),
  [2620] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local alrena = Model.new(4701, {
  [2571] = Vertex.new(24, 738, -38, 30, 29, 27),
  [2595] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [2617] = Vertex.new(-2, 716, -57, 107, 78, 55),
  [2623] = Vertex.new(2, 716, -57, 107, 78, 55),
  [2627] = Vertex.new(6, 716, -52, 107, 78, 55),
})
local jethick = Model.new(3330, {
  [1810] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [1815] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [1818] = Vertex.new(2, 725, -59, 106, 78, 54),
  [1820] = Vertex.new(7, 724, -51, 106, 78, 54),
  [2686] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local martha = Model.new(3882, {
  [2070] = Vertex.new(24, 738, -38, 30, 29, 27),
  [2094] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [2116] = Vertex.new(-2, 716, -57, 106, 78, 54),
  [2122] = Vertex.new(2, 716, -57, 106, 78, 54),
  [2126] = Vertex.new(6, 716, -52, 106, 78, 54),
})
local ted = Model.new(4017, {
  [2677] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2682] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2685] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2687] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3172] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local milli = Model.new(3090, {
  [441] = Vertex.new(220, 288, -4, 107, 93, 82),
  [443] = Vertex.new(220, 288, -4, 107, 93, 82),
  [445] = Vertex.new(220, 288, -4, 107, 93, 82),
  [492] = Vertex.new(-220, 288, -4, 107, 93, 82),
  [1857] = Vertex.new(20, 528, -60, 53, 49, 27),
})
local clerk = Model.new(3651, {
  [2734] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2739] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2742] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2744] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3001] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local bravek = Model.new(5289, {
  [2917] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2925] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2927] = Vertex.new(7, 724, -51, 106, 78, 54),
  [4053] = Vertex.new(-65, 670, 50, 89, 14, 7),
  [4084] = Vertex.new(65, 670, 50, 89, 14, 7),
})
--#endregion
--#region Objects
local mudPatch = Model.new(213, {
  [2] = Vertex.new(256, 0, 256, 137, 137, 137),
  [3] = Vertex.new(256, 0, -256, 137, 137, 137),
  [5] = Vertex.new(-256, 0, 256, 137, 137, 137),
})
local pipeGrill = Model.new(504, {
  [15] = Vertex.new(-256, 656, 184, 58, 55, 36),
  [27] = Vertex.new(-256, 640, -184, 58, 55, 36),
  [318] = Vertex.new(-240, 640, 172, 58, 55, 36),
  [335] = Vertex.new(-256, 656, 184, 58, 55, 36),
  [414] = Vertex.new(-240, 656, -172, 58, 55, 36),
})
local spookyStairs = Model.new(1902, {
  [1563] = Vertex.new(4160, 64, 2380, 26, 22, 16),
  [1574] = Vertex.new(4184, 64, 2200, 26, 22, 16),
  [1580] = Vertex.new(4264, 64, 2200, 26, 22, 16),
  [1583] = Vertex.new(4224, 64, 2256, 26, 22, 16),
  [1586] = Vertex.new(4224, 64, 2256, 26, 22, 16),
})
local closedManhole = Model.new(210, {
  [138] = Vertex.new(1024, 1184, 3904, 85, 78, 78),
  [162] = Vertex.new(960, 1184, 4032, 85, 78, 78),
  [168] = Vertex.new(960, 1184, 4032, 85, 78, 78),
  [186] = Vertex.new(832, 1184, 4096, 85, 78, 78),
  [207] = Vertex.new(704, 1184, 4096, 85, 78, 78),
})
local openManhole = Model.new(1236, {
  [601] = Vertex.new(-219, -832, 135, 40, 25, 3),
  [657] = Vertex.new(-151, -832, 207, 40, 25, 3),
  [661] = Vertex.new(147, -832, 207, 40, 25, 3),
  [669] = Vertex.new(210, -832, 135, 40, 25, 3),
  [804] = Vertex.new(-219, -64, 191, 93, 59, 8),
})
--#endregion
--#region Items
local chocolateDust = Model.new(72, {
  [28] = Vertex.new(72, 0, 80, 73, 41, 22),
  [34] = Vertex.new(100, 0, 60, 73, 41, 22),
  [42] = Vertex.new(100, 0, 60, 73, 41, 22),
  [64] = Vertex.new(132, 0, -24, 73, 41, 22),
  [72] = Vertex.new(132, 0, -24, 73, 41, 22),
})
local chocolateyMilk = Model.new(570, {
  [2] = Vertex.new(62, 117, 50, 84, 52, 34),
  [4] = Vertex.new(62, 117, 50, 84, 52, 34),
  [7] = Vertex.new(62, 117, -54, 84, 52, 34),
  [14] = Vertex.new(62, 117, -54, 84, 52, 34),
  [19] = Vertex.new(-58, 117, -54, 84, 52, 34),
})
local hangoverCure = Model.new(570, {
  [2] = Vertex.new(62, 117, 50, 91, 119, 92),
  [4] = Vertex.new(62, 117, 50, 91, 119, 92),
  [7] = Vertex.new(62, 117, -54, 91, 119, 92),
  [14] = Vertex.new(62, 117, -54, 91, 119, 92),
  [19] = Vertex.new(-58, 117, -54, 91, 119, 92),
})
--#endregion
--#region Quest Items
local elenaPicture = Model.new(294, {
  [1] = Vertex.new(-128, 4, -160, 143, 131, 130),
  [3] = Vertex.new(128, 4, -160, 143, 131, 130),
  [5] = Vertex.new(-128, 4, 160, 143, 131, 130),
  [146] = Vertex.new(64, 8, -92, 62, 53, 5),
  [237] = Vertex.new(-4, 8, 140, 80, 51, 7),
})
local gasMask = Model.new(1362, {
  [1026] = Vertex.new(-45, 22, 76, 108, 104, 68),
  [1076] = Vertex.new(45, 22, 76, 108, 104, 68),
  [1131] = Vertex.new(-45, 22, 76, 47, 45, 29),
  [1208] = Vertex.new(45, 22, 76, 47, 45, 29),
  [1303] = Vertex.new(-45, 22, 76, 21, 20, 13),
})
local book = Model.new(138, {
  [133] = Vertex.new(-32, 52, 52, 146, 146, 134),
  [134] = Vertex.new(32, 52, 32, 146, 146, 134),
  [135] = Vertex.new(-32, 52, 32, 146, 146, 134),
  [136] = Vertex.new(-32, 52, 52, 146, 146, 134),
  [137] = Vertex.new(32, 52, 52, 146, 146, 134),
})
local scruffyNote = Model.new(87, {
  [3] = Vertex.new(40, 0, 112, 161, 148, 147),
  [14] = Vertex.new(72, 0, -48, 161, 148, 147),
  [27] = Vertex.new(-40, 0, -72, 161, 148, 147),
  [32] = Vertex.new(-52, 0, 64, 0, 0, 0),
  [42] = Vertex.new(36, 0, 76, 0, 0, 0),
})
local warrent = Model.new(372, {
  [3] = Vertex.new(20, 0, -72, 146, 146, 134),
  [5] = Vertex.new(-72, 0, -56, 146, 146, 134),
  [187] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [190] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [195] = Vertex.new(-84, 28, -84, 89, 63, 7),
})
local smallKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 152, 120, 13),
  [104] = Vertex.new(68, 16, 68, 152, 120, 13),
  [397] = Vertex.new(68, 16, 68, 152, 120, 13),
  [400] = Vertex.new(68, 16, 68, 152, 120, 13),
  [408] = Vertex.new(68, 16, 68, 152, 120, 13),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Edmond who is located at a house north of the Ardougne Castle and Flying Horse Inn in East Ardougne.",
    title = "Getting started",
    actions = {
      Action.ModelHighlight:new(edmond, { distance = 8 }),
      Action.Direction:new(2567, 1165, 3333, { distance = 8 }),
      Action.ConversationHighlight:new("What's happened to her?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue speaking to Edmond.",
    actions = {
      Action.ModelHighlight:new(edmond, { distance = 8 }),
      Action.Direction:new(2567, 1165, 3333, { distance = 8 }),
    },
    postconditions = {
      Condition.ConversationText:new("The foresters keep a close eye on it, but there is a back way in."),
    },
  },
  {
    text = "Talk to his wife Alrena directly east in the house and give her the dwellberries.",
    title = "Breaking in",
    neededItems = {
      ["Dwellberries"] = { quantity = 1 },
      ["Buckets of water"] = { quantity = 4 },
      ["Rope"] = { quantity = 1 },
      ["Chocolate dust"] = { quantity = 1 },
      ["Snape grass"] = { quantity = 1 },
      ["Bucket of milk"] = { quantity = 1 },
    },
    actions = {
      Action.ModelHighlight:new(alrena, { distance = 6 }),
      Action.Direction:new(2576, 1445, 3333, { distance = 6 }),
    },
    postconditions = {
      Condition.ConversationText:new("I'll make a spare mask."),
      Condition.InventoryContains:new(gasMask),
    },
  },
  {
    text = "Pick up the picture of Elena from the table in the eastern room of their house.",
    actions = {
      Action.ModelHighlight:new(elenaPicture, { distance = 6 }),
      Action.Direction:new(2576, 1445, 3333, { distance = 6 }),
    },
    postconditions = { Condition.InventoryContains:new(elenaPicture) },
  },
  {
    text = "Talk to Edmond.",
    actions = {
      Action.ModelHighlight:new(edmond, { distance = 8 }),
      Action.Direction:new(2567, 1165, 3333, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("I'll keep an eye out for the mourners.") },
  },
  {
    text = "Equip the gas mask.",
    actions = { Action.InventoryHighlight:new(gasMask) },
    postconditions = { Condition.InventoryDoesNotContain:new(gasMask) }, --easy to break
  },
  {
    text = "Use all 4 buckets of water on the mud patch in the south part of his garden.<ul><li>There's a bucket spawn in Edmond's garden, and a sink in his house.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(Models.items["bucket of water"]),
      Action.ModelHighlight:new(mudPatch, { distance = 8 }),
      Action.Direction:new(2566, 1125, 3332, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("The soil is now soft enough to dig into.") },
  },
  {
    text = "Dig on the mud patch.<ul><li>Bring all of the required items with you <b>before</b> you enter the mud patch.</li></ul>",
    actions = {
      Action.ModelHighlight:new(mudPatch, { distance = 8 }),
      Action.Direction:new(2566, 1125, 3332, { distance = 8 }),
    },
    postconditions = {
      Condition.ConversationText:new("Suddenly it crumbles away!"),
      Condition.DistanceTo:new(2518, 1125, 9760, 8),
    },
  },
  {
    text = "Go south and try to open the grill on the pipe.",
    actions = {
      Action.ModelHighlight:new(pipeGrill, { distance = 8 }),
      Action.Direction:new(2514, 1125, 9739, { distance = 8 }),
    },
    postconditions = {
      Condition.ConversationText:new("The grill is too secure."),
      Condition.ConversationText:new("You can't pull it off alone."),
    },
  },
  {
    text = "Use a rope on the grill.",
    actions = {
      Action.ModelHighlight:new(pipeGrill, { distance = 8 }),
      Action.Direction:new(2514, 1125, 9739, { distance = 8 }),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    postconditions = { Condition.ConversationText:new("You tie the end of the rope to the sewer pipe's grill.") }, -- fix
  },
  {
    text = "Talk to Edmond.",
    actions = { Action.ModelHighlight:new(edmond) },
    postconditions = { Condition.ConversationText:new("let's get to it") },
  },
  { actions = {}, postconditions = { Condition.ConversationText:new("Thanks, I will.") } },
  {
    text = "Make sure the gas mask obtained earlier is <b>equipped</b>. Climb up the pipe.",
    actions = {
      Action.Direction:new(2514, 1125, 9738, { distance = 8 }),
    },
    postconditions = { Condition.DistanceTo:new(2529, 1189, 3304, 8) },
  },
  {
    text = "Use the picture on Jethick, in the town square.<ul><li>Optional: Talk to the nearby Recruiter for one of easy Ardougne achievements, Red Revolution.</li></ul>",
    title = "The search for Elena",
    actions = {
      Action.ModelHighlight:new(jethick),
      Action.ConversationHighlight:new("Hi, I'm looking for a woman from East Ardougne."),
      Action.ConversationHighlight:new("Yes, I'll return it for you."),
    },
    postconditions = { Condition.InventoryContains:new(book) },
  },
  {
    text = "Knock on the Rehnison family household's door, north of the town hall building.",
    actions = { Action.Direction:new(2531, 1285, 3328.5) },
    postconditions = { Condition.ConversationText:new("Thanks, I've been missing that.") },
  },
  {
    text = "Talk to Martha Rehnison or Ted Rehnison.",
    actions = {
      Action.ModelHighlight:new(martha),
      Action.ModelHighlight:new(ted),
    },
    postconditions = { Condition.ConversationText:new("Milli is upstairs if you wish to speak to her.") },
  },
  {
    text = "Go up the ladder and talk to Milli Rehnison.",
    actions = { Action.Direction:new(2527, 1285, 3333) },
    postconditions = { Condition.DistanceToWithHeight:new(2528, 2245, 3333, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(milli) },
    postconditions = {
      Condition.ConversationText:new(
        "It was the boarded up building with no windows in the south east corner of West Ardougne."
      ),
    },
  },
  {
    text = "Go to the central southern house with spooky stairs and attempt to open one of the doors with a big X.<ul><li>Optional: Pray at the altar in the church for one of easy Ardougne achievements, Preaching to the Infected.</li></ul>",
    actions = { Action.Direction:new(2533, 1029, 3272.5) },
    postconditions = { Condition.ConversationText:new("The door won't open.") },
  },
  {
    actions = {
      Action.ConversationHighlight:new("But I think a kidnap victim is in here."),
      Action.ConversationHighlight:new("I want to check anyway."),
    },
    postconditions = { Condition.ConversationText:new("Bravek the city warder") },
  },
  {
    text = "Go north to the large building you first arrived at, in the town square.",
    title = "Getting to Elena",
    actions = { Action.Direction:new(2525.5, 1189, 3310.5) },
    postconditions = { Condition.DistanceTo:new(2526, 1189, 3313, 2) },
  },
  {
    text = "Talk to the Clerk on the ground floor (1st floor[US]), and tell him it's urgent.",
    actions = {
      Action.ModelHighlight:new(clerk),
      Action.ConversationHighlight:new("Who is through that door?"),
      Action.ConversationHighlight:new("This is urgent though!"),
    },
    postconditions = { Condition.ConversationText:new("I suppose they can come in then. If they keep it short.") },
  },
  {
    text = "Climb the stairs and enter the eastmost room.",
    actions = { Action.Direction:new(2528, 1189, 3316.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2530, 2149, 3317, 6) },
  },
  {
    text = "Talk to Bravek.",
    actions = {
      Action.ModelHighlight:new(bravek),
      Action.ConversationHighlight:new("This is really important though!"),
      Action.ConversationHighlight:new("Do you know what's in the cure?"),
    },
    postconditions = { Condition.InventoryContains:new(scruffyNote) },
  },
  {
    text = "Add the chocolate dust to the bucket of milk.",
    actions = {
      Action.InventoryHighlight:new(chocolateDust),
      Action.InventoryHighlight:new(Models.items["bucket of milk"]),
    },
    postconditions = { Condition.InventoryContains:new(chocolateyMilk) },
  },
  {
    text = "Add snape grass to the chocolatey milk.",
    actions = {
      Action.InventoryHighlight:new(chocolateyMilk),
      Action.InventoryHighlight:new(Models.items["snape grass"]),
    },
    postconditions = { Condition.InventoryContains:new(hangoverCure) },
  },
  {
    text = "Talk to Bravek and give him the cure. He will give you a warrant to enter.",
    actions = {
      Action.ModelHighlight:new(bravek),
      Action.ConversationHighlight:new("They won't listen to me!"),
    },
    postconditions = {
      Condition.InventoryContains:new(warrent),
    },
  },
  {
    text = "Go back and enter the house with the black Xs.",
    actions = { Action.Direction:new(2533, 1029, 3272.5) },
    postconditions = {
      Condition.DistanceTo:new(2534, 1029, 3270, 2),
      Condition.DistanceTo:new(2539, 1029, 3270, 2),
    },
  },
  {
    text = "Search the barrel on the western side of the building's ground floor (1st floor[US]) to obtain a small key.",
    actions = { Action.Direction:new(2534, 1029, 3268) },
    postconditions = { Condition.InventoryContains:new(smallKey) },
  },
  {
    text = "Go down the spooky stairs.",
    actions = { Action.ModelHighlight:new(spookyStairs) },
    postconditions = { Condition.DistanceToWithHeight:new(2537, 645, 9670, 4) },
  },
  {
    text = "Open the gate and talk to Elena. Make sure to finish the dialogue",
    actions = { Action.Direction:new(2539.5, 645, 9672) },
    postconditions = { Condition.DistanceTo:new(2541, 645, 9672, 1) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["elena"]) },
    postconditions = { Condition.ConversationText:new("Now I'd better leave while I still can.") },
  },
  {
    text = "Return to the town square you arrived at, and re-enter the manhole.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(2536.5, 645, 9672, { distance = 8 }),
    },
    postconditions = { Condition.DistanceTo:new(2536, 1029, 3271, 8) },
  },
  {
    actions = {
      Action.Direction:new(2529, 1189, 3303),
      Action.ModelHighlight:new(openManhole, { distance = 8 }),
      Action.ModelHighlight:new(closedManhole, { distance = 8 }),
    },
    postconditions = { Condition.DistanceTo:new(2514, 1125, 9739, 5) },
  },
  {
    text = "Talk to Edmond.",
    actions = {
      Action.Direction:new(2516, 1125, 9755, { distance = 12 }),
      Action.ModelHighlight:new(edmond, { distance = 12 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Plague City",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1030406400,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Buckets of water"] = { quantity = 4, model = Models.items["bucket of water"], duringQuest = true },
    ["Dwellberries"] = { quantity = 1, model = Models.items["dwellberries"] },
    ["Rope"] = { quantity = 1, model = Models.items["rope"] },
    ["Chocolate dust"] = { quantity = 1, model = chocolateDust },
    ["Snape grass"] = { quantity = 1, model = Models.items["snape grass"] },
    ["Bucket of milk"] = { quantity = 1, model = Models.items["bucket of milk"] },
  },
  recommendedItems = {},
  combatNPCs = {},
})
