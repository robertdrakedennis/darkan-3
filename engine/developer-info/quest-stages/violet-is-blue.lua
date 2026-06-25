local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local violet = Models.npcs["violet"]
local trevor = Models.npcs["trevor"]
local firefly = Model.new(120, {
  [9] = Vertex.new(-242, 60, 242, 130, 195, 17),
})
local violetInABarrel = Model.new(40878, {
  [23875] = Vertex.new(0, 309, -49, 127, 127, 127),
  [23907] = Vertex.new(40, 303, -14, 127, 127, 127),
  [39512] = Vertex.new(12, 300, -38, 127, 127, 127),
  [39551] = Vertex.new(31, 301, -26, 127, 127, 127),
  [39739] = Vertex.new(-31, 301, -26, 127, 127, 127),
})
--#endregion
--#region Objects
local bed = Model.new(3894, {
  [3225] = Vertex.new(258, 252, -731, 127, 127, 127),
  [3228] = Vertex.new(-198, 258, -754, 127, 127, 127),
  [3234] = Vertex.new(258, 252, -731, 127, 127, 127),
  [3447] = Vertex.new(325, 258, -648, 127, 127, 127),
  [3885] = Vertex.new(295, 226, -687, 79, 64, 51),
})
local toybox = Model.new(10335, {
  [120] = Vertex.new(617, 305, -120, 127, 127, 127),
  [520] = Vertex.new(441, 435, -149, 128, 127, 127),
  [541] = Vertex.new(481, 435, -120, 128, 127, 127),
  [566] = Vertex.new(479, 435, -118, 128, 127, 127),
  [7540] = Vertex.new(153, 218, -371, 94, 87, 79),
})
local wardrobe = Model.new(1212, {
  [717] = Vertex.new(-400, 946, 253, 127, 127, 127),
  [744] = Vertex.new(415, 837, 253, 127, 127, 127),
  [834] = Vertex.new(396, 891, 253, 127, 127, 127),
  [963] = Vertex.new(426, 946, 253, 127, 127, 127),
  [1008] = Vertex.new(415, 856, 253, 127, 127, 127),
})
local frozenBucketObj = Model.new(573, {
  [529] = Vertex.new(60, 240, 116, 91, 70, 58),
  [533] = Vertex.new(60, 240, 116, 91, 70, 58),
  [534] = Vertex.new(48, 245, 96, 91, 70, 58),
  [548] = Vertex.new(96, 245, 48, 91, 70, 58),
  [555] = Vertex.new(60, 240, 116, 85, 65, 55),
})
local tree1 = Model.new(2004, {
  [42] = Vertex.new(2541, 3171, 3264, 177, 177, 177),
})
local tree2 = Model.new(3006, {
  [504] = Vertex.new(3498, 3228, 4055, 177, 177, 177),
})
local tree3 = Model.new(1002, {
  [75] = Vertex.new(3731, 3137, 1731, 177, 177, 177),
})
local tree4 = Model.new(3006, {
  [75] = Vertex.new(3731, 3073, 3779, 177, 177, 177),
})
local bush = Model.new(108, {
  [3] = Vertex.new(145, 518, -60, 190, 205, 111),
  [15] = Vertex.new(47, 582, -98, 190, 205, 111),
  [18] = Vertex.new(47, 582, -98, 190, 205, 111),
  [27] = Vertex.new(-47, 585, -68, 190, 205, 111),
  [31] = Vertex.new(105, 533, -54, 190, 205, 111),
})
local snow = Model.new(4512, {
  [3] = Vertex.new(-425, 1, 232, 128, 128, 128),
})
local snowman0 = Model.new(516, {
  [244] = Vertex.new(-200, 0, -240, 183, 188, 192, 0.2235),
  [250] = Vertex.new(-256, 0, -180, 183, 188, 192, 0.2235),
  [321] = Vertex.new(232, 0, 220, 183, 188, 192, 0.2235),
  [323] = Vertex.new(232, 0, 220, 183, 188, 192, 0.2235),
  [325] = Vertex.new(232, 0, 220, 183, 188, 192, 0.2235),
})
local snowman1 = Model.new(2016, {
  [109] = Vertex.new(-1, 529, 19, 178, 178, 178),
  [948] = Vertex.new(-81, 456, 14, 177, 177, 177),
  [1842] = Vertex.new(14, 529, -15, 178, 178, 178),
  [1845] = Vertex.new(14, 529, -15, 178, 178, 178),
  [1851] = Vertex.new(14, 529, -15, 178, 178, 178),
})
local snowman2 = Model.new(3780, {
  [23] = Vertex.new(322, 356, -9, 177, 177, 177),
  [26] = Vertex.new(322, 356, -9, 177, 177, 177),
  [64] = Vertex.new(302, 377, 3, 177, 177, 177),
  [147] = Vertex.new(-322, 356, -9, 177, 177, 177),
  [184] = Vertex.new(-302, 377, 3, 177, 177, 177),
})
local snowman3 = Model.new(5304, {
  [1] = Vertex.new(-302, 377, 3, 177, 177, 177),
  [99] = Vertex.new(-322, 356, -9, 177, 177, 177),
  [156] = Vertex.new(-322, 356, -9, 177, 177, 177),
  [521] = Vertex.new(322, 356, -9, 177, 177, 177),
  [529] = Vertex.new(302, 377, 3, 177, 177, 177),
})
local barrel = Model.new(750, {
  [198] = Vertex.new(-133, -106, -23, 127, 127, 127),
})
local crate = Model.new(102, {
  [55] = Vertex.new(116, 349, 272, 84, 70, 59),
})
local rottenBarrel = Model.new(1080, {
  [753] = Vertex.new(348, -13, -175, 22, 17, 14),
})
local openSpace = Model.new(10572, {
  [5230] = Vertex.new(3758, 8128, 2790, 222, 217, 217, 0.3020),
})
local sled = Model.new(10266, {
  [9293] = Vertex.new(20, 849, 305, 167, 167, 167),
})
--#endregion
--#region Quest Items
local frozenBucketItem = Model.new(582, {
  [77] = Vertex.new(64, 140, -52, 52, 46, 27),
  [85] = Vertex.new(68, 140, 60, 52, 46, 27),
  [88] = Vertex.new(68, 140, 60, 52, 46, 27),
  [111] = Vertex.new(64, 140, -52, 70, 63, 45),
  [123] = Vertex.new(68, 140, 60, 70, 63, 45),
})
local spile = Model.new(90, {
  [1] = Vertex.new(24, 33, -14, 80, 59, 41),
})
local jar = Model.multi({
  Model.new(612, {
    [1] = Vertex.new(-80, 223, 0, 15, 22, 14),
  }),
  Model.new(414, {
    [1] = Vertex.new(-67, 19, 67, 59, 63, 65, 0.6078),
  }),
})
local bucketSyrup = Model.new(570, {
  [1] = Vertex.new(0, 117, 0, 163, 138, 67),
})
local snowball = Model.new(258, {
  [1] = Vertex.new(52, 44, 76, 156, 161, 169),
})
local coal = Model.new(120, {
  [1] = Vertex.new(4, 28, -20, 19, 8, 7),
})
local tophat = Model.new(534, {
  [1] = Vertex.new(-40, 24, -24, 86, 30, 26),
})
local carrot = Model.multi({
  Model.new(102, {
    [1] = Vertex.new(-7, 5, 76, 103, 43, 13),
  }),
  Model.new(24, {
    [1] = Vertex.new(31, 7, -83, 7, 82, 9),
  }),
})
local branch = Model.new(219, {
  [1] = Vertex.new(-188, 20, -172, 69, 64, 44),
})
local barrelInv = Model.new(909, {
  [1] = Vertex.new(64, 68, 120, 48, 48, 53),
})
local barrelParts = Model.new(438, {
  [1] = Vertex.new(122, 73, 226, 35, 32, 32),
})
local lantern = Model.multi({
  Model.new(465, {
    [1] = Vertex.new(0, 84, -4, 36, 30, 23),
  }),
  Model.new(48, {
    [1] = Vertex.new(-80, 124, -68, 118, 125, 153, 0.4980),
  }),
})
local charcoal = Model.new(120, {
  [1] = Vertex.new(4, 28, -20, 19, 8, 7),
})
local yetiSign = Model.multi({
  Model.new(9060, {
    [4019] = Vertex.new(43, 1116, 35, 127, 127, 127),
    [5040] = Vertex.new(43, 1051, 114, 127, 127, 127),
    [5052] = Vertex.new(43, 1116, -114, 127, 127, 127),
    [5094] = Vertex.new(43, 1116, -114, 127, 127, 127),
    [7191] = Vertex.new(43, 1051, 114, 127, 127, 127),
  }),
  Model.new(216, {
    [6] = Vertex.new(140, 1144, -307, 127, 127, 127),
    [12] = Vertex.new(140, 884, -295, 127, 127, 127),
    [42] = Vertex.new(140, 1140, -172, 127, 127, 127),
    [60] = Vertex.new(140, 1144, -307, 127, 127, 127),
    [192] = Vertex.new(140, 1140, -172, 127, 127, 127),
  }),
  Model.new(90, {
    [18] = Vertex.new(137, 915, -469, 128, 128, 128),
    [39] = Vertex.new(139, 1256, -111, 128, 128, 128),
    [50] = Vertex.new(151, 1256, -469, 128, 128, 128),
    [52] = Vertex.new(151, 1256, -469, 128, 128, 128),
    [54] = Vertex.new(149, 1161, -469, 128, 128, 128),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Violet found on the southern tip of White Wolf Mountain.",
    title = "Starting out",
    actions = {
      Action.Direction:new(2855, 5469, 3460, { distance = 23 }),
      Action.ModelHighlight:new(violet, { distance = 23 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Enter the Land of Snow portal.",
    actions = { Action.ModelHighlight:new(Models.objects["winter portal"]) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.ConversationText:new("here and face me") },
  },
  {
    text = "Talk to Violet at the top of the hill after dodging the snowballs.",
    title = "Land of Snow",
    actions = {
      Action.ModelHighlight:new(violet, { instance = true }),
      Action.PathGuide:new({
        Location:new(3, 0, 0),
        Location:new(3, 2560, 16),
        Location:new(3, 4640, 29),
        Location:new(3, 7360, 46),
        Location:new(3, 7704, 49),
      }, { instance = true }),
    },
    postconditions = {
      Condition.ChangedInstance:new(),
      Condition.ConversationText:new("or a cute little cabin"),
    },
  },
  {
    text = "Knock on the door.",
    actions = {
      Action.Direction:new(0, 300, 16.5, { instance = true }),
      Action.ConversationHighlight:new("Break the door down."),
      Action.ConversationHighlight:new("To go on an adventure."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Trevor.",
    actions = {
      Action.ModelHighlight:new(trevor, { instance = true }),
      Action.ConversationHighlight:new("YOU'VE CAPTURED A HUMAN GIRL!"),
      Action.ConversationHighlight:new("Yes!"),
    },
    postconditions = { Condition.ConversationText:new("You should probably tell her the good news") },
  },
  {
    text = "Talk to Violet in the room to the north.",
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Silly me") },
  },
  {
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = { Condition.ConversationText:new("something beginning with") },
  },
  {
    text = "Search for and bring Violet an item that begins with the letter she requested 5 times.",
    actions = {
      Action.ModelHighlight:new(bed, { instanced = true }),
      Action.ModelHighlight:new(toybox, { instanced = true }),
      Action.ModelHighlight:new(wardrobe, { instanced = true }),
    },
    postconditions = { Condition.ConversationText:new("go tell Mum and Dad") },
  },
  {
    text = "Talk to Trevor.",
    actions = { Action.ModelHighlight:new(trevor, { instance = true }) },
    postconditions = { Condition.ConversationText:new("No, I don't think they'll fit me") },
  },
  {
    text = "Exit the building.",
    actions = { Action.Direction:new(0, 600, -1.5, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    actions = { Action.ConversationHighlight:new("Go on an adventure!") },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the frozen bucket for an empty bucket.",
    title = "Going on an adventure",
    actions = { Action.ModelHighlight:new(frozenBucketObj, { instanced = true }) },
    postconditions = { Condition.InventoryContains:new(frozenBucketItem) },
  },
  {
    text = "Investigate the maple tree.",
    actions = { Action.Direction:new(-13.5, 800, -4.5, { instance = true }) },
    postconditions = { Condition.ConversationText:new("A stick") },
  },
  {
    text = "Investigate the maple tree again.",
    actions = { Action.Direction:new(-13.5, 800, -4.5, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["maple logs"]) },
  },
  {
    text = "Investigate the logs.",
    actions = {
      Action.InventoryHighlight:new(Models.items["maple logs"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InventoryContains:new(spile) },
  },
  {
    text = "Use the spile on the maple tree.",
    actions = {
      Action.Direction:new(-13.5, 800, -4.5, { instance = true }),
      Action.InventoryHighlight:new(spile, true),
    },
    postconditions = { Condition.ChatText:new("gently tap") },
  },
  {
    text = "Use the empty bucket on the same maple tree.",
    actions = {
      Action.Direction:new(-13.5, 800, -4.5, { instance = true }),
      Action.InventoryHighlight:new(frozenBucketItem, true),
    },
    postconditions = { Condition.InventoryContains:new(bucketSyrup) },
  },
  {
    text = "Check the four leaning trees to the north.",
    actions = {
      Action.ModelHighlight:new(tree1),
      Action.ModelHighlight:new(tree2),
      Action.ModelHighlight:new(tree3),
      Action.ModelHighlight:new(tree4),
    },
    postconditions = { Condition.ChatText:new("4/4 areas with syrup") },
  },
  {
    text = "Talk to Trevor.",
    actions = {
      Action.ModelHighlight:new(trevor, { instance = true }),
      Action.ConversationHighlight:new("Do you have anything useful?"),
    },
    postconditions = { Condition.InventoryContains:new(jar) },
  },
  {
    text = "Shake the bushes to catch 10 fireflies.",
    actions = {
      Action.ModelHighlight:new(bush, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(firefly, { instance = true, highlightPriority = "all" }),
    },
    postconditions = { Condition.ChatText:new("10/10") },
  },
  {
    text = "Talk to Violet.",
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Violet.",
    title = "Making snow heads",
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = { Condition.ConversationText:new("There is some snow") },
  },
  {
    text = "Take the icy snow from behind Violet.",
    actions = {
      Action.ModelHighlight:new(snow),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("what a great plan") },
  },
  {
    text = "Spam click around the headless golems to reattach all 15 heads.",
    postconditions = { Condition.ChatText:new("15/15") },
  },
  {
    text = "Talk to Violet.",
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    title = "Making snowmen",
    postconditions = { Condition.ChatText:new("Violet to roar at") },
  },
  {
    text = "Collect 75 snowballs at the pile of snow to the south.",
    actions = { Action.Direction:new(2, 328, -2.5, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(snowball, 75) },
  },
  {
    text = "Search the abandoned crate for 3 carrots, 3 top hats, 21 coal.",
    actions = { Action.Direction:new(8, 756, -3, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(carrot, 3) },
  },
  {
    actions = { Action.Direction:new(8, 756, -3, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(tophat, 3) },
  },
  {
    actions = { Action.Direction:new(8, 756, -3, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(coal, 21) },
  },
  {
    text = "Prune the tree for 6 branches.",
    actions = { Action.Direction:new(-3, 716, -2.8, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(branch, 6) },
  },
  {
    text = "Build all melted snowmen",
    actions = {
      Action.ModelHighlight:new(snowman0, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman1, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman2, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman3, { instance = true, highlightPriority = "all" }),
    },
    postconditions = { Condition.ModelNotVisible:new(snowman0) },
  },
  {
    actions = {
      Action.ModelHighlight:new(snowman0, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman1, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman2, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman3, { instance = true, highlightPriority = "all" }),
    },
    postconditions = { Condition.ModelNotVisible:new(snowman1) },
  },
  {
    actions = {
      Action.ModelHighlight:new(snowman0, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman1, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman2, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman3, { instance = true, highlightPriority = "all" }),
    },
    postconditions = { Condition.ModelNotVisible:new(snowman2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(snowman0, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman1, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman2, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(snowman3, { instance = true, highlightPriority = "all" }),
    },
    postconditions = { Condition.ModelNotVisible:new(snowman3) },
  },
  {
    text = "Talk to Violet three times, until all snowmen are gone.",
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = { Condition.ChatText:new("enough to walk across") },
  },
  {
    text = "Reach into the icy water on the east shore to obtain a barrel.",
    title = "Crossing the ice",
    actions = { Action.ModelHighlight:new(barrel) },
    postconditions = { Condition.InventoryContains:new(barrelInv) },
  },
  {
    text = "Talk to Violet.",
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(violetInABarrel) },
  },
  {
    text = "Push Violet north",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(violetInABarrel, { instance = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(violetInABarrel, { instance = true, atLocation = Location:new(0, 0, 5) }),
    },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(0, 0, 0),
        Location:new(0, 0, 4),
        Location:new(5, 0, 4),
        Location:new(5, 0, 5),
        Location:new(1, 0, 5),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(1, 0, 5, 0, true) },
  },
  {
    text = "Push Violet west.",
    actions = { Action.ModelHighlight:new(violetInABarrel, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(violetInABarrel, { instance = true, atLocation = Location:new(-5, 0, 5) }),
    },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(1, 0, 5),
        Location:new(-4, 0, 5),
        Location:new(-4, 0, 0),
        Location:new(-5, 0, 1),
        Location:new(-5, 0, 4),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-5, 0, 4, 0, true) },
  },
  {
    text = "Push Violet north.",
    actions = { Action.ModelHighlight:new(violetInABarrel, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(violetInABarrel, { instance = true, atLocation = Location:new(-5, 0, 10) }),
    },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(-5, 0, 4),
        Location:new(-5, 0, 9),
        Location:new(-8, 0, 9),
        Location:new(-6, 0, 7),
        Location:new(-6, 0, 10),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(-6, 0, 10, 0, true) },
  },
  {
    text = "Push Violet east.",
    actions = { Action.ModelHighlight:new(violetInABarrel, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(violetInABarrel, { instance = true, atLocation = Location:new(3, 0, 10) }),
    },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(-6, 0, 10),
        Location:new(2, 0, 10),
        Location:new(2, 0, 7),
        Location:new(4, 0, 9),
        Location:new(3, 0, 9),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(3, 0, 9, 0, true) },
  },
  {
    text = "Push Violet north.",
    actions = { Action.ModelHighlight:new(violetInABarrel, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(violetInABarrel, { instance = true, atLocation = Location:new(3, 0, 13) }),
    },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(3, 0, 9),
        Location:new(3, 0, 12),
        Location:new(4, 0, 12),
        Location:new(4, 0, 15),
        Location:new(8, 0, 15),
        Location:new(6, 0, 13),
        Location:new(4, 0, 13),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(4, 0, 13, 0, true) },
  },
  {
    text = "Push Violet west.",
    actions = { Action.ModelHighlight:new(violetInABarrel, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(violetInABarrel, { instance = true, atLocation = Location:new(0, 0, 13) }),
    },
  },
  {
    text = "Follow the path.",
    actions = {
      Action.PathGuide:new({
        Location:new(4, 0, 13),
        Location:new(0, 0, 9),
        Location:new(0, 0, 12),
      }, { instance = true }),
    },
    postconditions = { Condition.DistanceTo:new(0, 0, 12, 0, true) },
  },
  {
    text = "Push Violet north.",
    actions = { Action.ModelHighlight:new(violetInABarrel, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(violetInABarrel, { instance = true, atLocation = Location:new(0, 0, 16) }),
    },
  },
  {
    text = "Walk north.",
    actions = { Action.Direction:new(0, 200, 15, { instance = true }) },
    postconditions = { Condition.ConversationText:new("we did it") },
  },
  {
    text = "Talk to Violet",
    actions = { Action.ModelHighlight:new(violetInABarrel, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to Violet.",
    title = "Constructing the sled",
    actions = { Action.ModelHighlight:new(violet) },
    postconditions = { Condition.ConversationText:new("find a clear spot") },
  },
  {
    text = "Investigate the crate in the west.",
    actions = { Action.ModelHighlight:new(crate) },
    postconditions = { Condition.InventoryContains:new(Models.items["rope"]) },
  },
  {
    text = "Investigate the rotten barrel in the east.",
    actions = { Action.ModelHighlight:new(rottenBarrel) },
    postconditions = { Condition.InventoryContains:new(barrelParts) },
  },
  {
    text = "Investigate the ice in the north-east",
    actions = { Action.Direction:new(18, -806, 2, { instance = true, tile = true }) },
    postconditions = { Condition.InventoryContains:new(lantern) },
  },
  {
    text = "Investigate the fire north.",
    actions = { Action.Direction:new(11, 1160, 13, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(charcoal) },
  },
  {
    text = "Chop down the Yeti Village sign to the south.",
    actions = { Action.Direction:new(2, 2460, -20, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(yetiSign) },
  },
  {
    text = "Investigate the open space until the sled is built.",
    actions = { Action.ModelHighlight:new(openSpace) },
    postconditions = { Condition.ModelVisible:new(sled) },
  },
  {
    text = "Talk to Violet. Choose any name.",
    actions = { Action.ModelHighlight:new(violet, { instance = true }) },
    postconditions = {
      Condition.ConversationText:new("SO excited"), --not tested
      Condition.ChangedInstance:new(),
    },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Violet is Blue",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 1545004800,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
