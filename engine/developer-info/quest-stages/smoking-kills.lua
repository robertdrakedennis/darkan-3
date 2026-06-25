local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local sumona = Model.new(3516, {
  [1743] = Vertex.new(-20, 744, -44, 164, 93, 49),
  [1747] = Vertex.new(20, 760, -44, 164, 93, 49),
  [1755] = Vertex.new(20, 760, -44, 164, 93, 49),
  [1838] = Vertex.new(24, 672, 56, 164, 93, 49),
  [1853] = Vertex.new(-20, 676, 56, 164, 93, 49),
})
local wellCat = Model.new(1737, {
  [13] = Vertex.new(40, 448, -60, 94, 99, 103),
  [14] = Vertex.new(40, 448, 20, 94, 99, 103),
  [15] = Vertex.new(-40, 448, -60, 94, 99, 103),
})
local mummyWarrior = Model.new(2661, {
  [26] = Vertex.new(256, 616, 172, 139, 117, 56),
  [417] = Vertex.new(256, 616, 172, 139, 117, 56),
  [609] = Vertex.new(256, 616, 172, 97, 84, 49),
  [844] = Vertex.new(-256, 616, 116, 139, 117, 56),
  [1214] = Vertex.new(-264, 492, -212, 107, 99, 98),
})
local catolax = Model.new(3117, {
  [907] = Vertex.new(-55, 748, 60, 111, 145, 128, 0.4118),
  [1263] = Vertex.new(-4, 805, -88, 111, 145, 128, 0.4118),
  [1299] = Vertex.new(6, 805, -88, 111, 145, 128, 0.4118),
  [2192] = Vertex.new(33, 744, -24, 111, 145, 128, 0.4118),
  [2198] = Vertex.new(33, 744, -24, 111, 145, 128, 0.4118),
})
local mightyBanshee = Model.new(2004, {
  [443] = Vertex.new(0, 844, -96, 105, 96, 105, 0.7059),
  [974] = Vertex.new(-52, 732, -12, 57, 65, 49, 0.8039),
  [976] = Vertex.new(-40, 732, 28, 57, 65, 49, 0.8039),
  [1005] = Vertex.new(52, 732, -12, 57, 65, 49, 0.8039),
  [1006] = Vertex.new(40, 732, 28, 57, 65, 49, 0.8039),
})
--#endregion
--#region Objects
local fallenPillar = Model.any({
  Model.new(348, {
    [77] = Vertex.new(6144, 1029, 1443, 112, 107, 85),
    [83] = Vertex.new(6144, 1110, 1422, 112, 107, 85),
    [84] = Vertex.new(6144, 1124, 1367, 112, 107, 85),
    [95] = Vertex.new(6144, 783, 1280, 112, 107, 85),
    [215] = Vertex.new(6144, 783, 1280, 112, 107, 85),
  }),
  Model.new(348, {
    [95] = Vertex.new(6144, 783, 1280, 112, 107, 85),
    [187] = Vertex.new(6144, 1029, 1443, 112, 107, 85),
    [193] = Vertex.new(6144, 1110, 1422, 112, 107, 85),
    [202] = Vertex.new(6144, 1124, 1367, 112, 107, 85),
    [215] = Vertex.new(6144, 783, 1280, 112, 107, 85),
  }),
})
local shortcutRock = Model.any({
  Model.new(627, {
    [544] = Vertex.new(276, -35, 160, 131, 96, 67),
    [552] = Vertex.new(284, -36, -164, 131, 96, 67),
    [559] = Vertex.new(364, -46, -160, 131, 96, 67),
    [585] = Vertex.new(512, -64, -4, 131, 96, 67),
    [586] = Vertex.new(512, -64, -4, 131, 96, 67),
  }),
  Model.new(2079, {
    [1775] = Vertex.new(220, 1530, -8, 75, 59, 6),
    [1787] = Vertex.new(220, 1530, 8, 75, 59, 6),
    [1996] = Vertex.new(204, 1530, 12, 75, 59, 6),
    [1998] = Vertex.new(220, 1530, 8, 75, 59, 6),
    [2002] = Vertex.new(192, 1530, 0, 75, 59, 6),
  }),
})
local shortcutRockRope = Model.new(1374, {
  [1243] = Vertex.new(264, -33, 160, 131, 96, 67),
  [1251] = Vertex.new(272, -34, -164, 131, 96, 67),
  [1258] = Vertex.new(364, -46, -160, 131, 96, 67),
  [1284] = Vertex.new(512, -64, -4, 131, 96, 67),
  [1285] = Vertex.new(512, -64, -4, 131, 96, 67),
})
local destroyedDoor = Model.new(84, {
  [23] = Vertex.new(-160, 0, 256, 131, 96, 67),
  [29] = Vertex.new(-160, 384, 240, 131, 96, 67),
  [38] = Vertex.new(-224, 0, 128, 131, 96, 67),
  [58] = Vertex.new(-160, 384, 240, 131, 96, 67),
  [65] = Vertex.new(-160, 384, 240, 131, 96, 67),
})
local bansheeVoiceObj = Model.new(954, {
  [27] = Vertex.new(0, 116, -8, 92, 84, 92, 0.7059),
  [77] = Vertex.new(28, 96, 52, 92, 84, 92, 0.7059),
  [80] = Vertex.new(-28, 96, 52, 92, 84, 92, 0.7059),
  [117] = Vertex.new(28, 96, 52, 92, 84, 92, 0.7059),
  [242] = Vertex.new(0, 112, 28, 105, 96, 105, 0.7059),
})
local mysticBarrier = Model.new(2205, {
  [80] = Vertex.new(9641, 1953, 2427, 32, 105, 164, 0.6118),
  [318] = Vertex.new(6991, 1953, 2427, 30, 99, 155, 0.6118),
  [351] = Vertex.new(9726, 1953, 2427, 30, 99, 155, 0.6118),
  [352] = Vertex.new(9726, 1953, 2196, 30, 99, 155, 0.6118),
  [384] = Vertex.new(8355, 1953, 2427, 30, 99, 155, 0.6118),
})
local wellRope = Model.new(2007, {
  [719] = Vertex.new(498, 5381, -41, 123, 90, 63),
  [743] = Vertex.new(482, 5391, -33, 131, 96, 67),
  [747] = Vertex.new(503, 5378, -23, 131, 96, 67),
  [2000] = Vertex.new(759, 1036, 258, 124, 120, 113),
  [2006] = Vertex.new(759, 1202, 258, 124, 120, 113),
})
--#endregion
--#region Items
local maskedEarmuffs = Model.new(504, {
  [17] = Vertex.new(0, 88, -76, 125, 112, 79),
  [94] = Vertex.new(0, 88, -76, 125, 112, 79),
  [260] = Vertex.new(-56, 80, -16, 59, 50, 45),
  [344] = Vertex.new(-56, 80, -16, 39, 33, 30),
  [345] = Vertex.new(-44, 88, -20, 39, 33, 30),
})
--#endregion
--#region Quest Items
local rangedPathKey = Model.new(369, {
  [124] = Vertex.new(28, 28, 108, 177, 162, 136),
  [126] = Vertex.new(28, 40, 108, 177, 162, 136),
  [135] = Vertex.new(8, 76, 92, 177, 162, 136),
  [136] = Vertex.new(8, 76, 92, 177, 162, 136),
  [153] = Vertex.new(-20, 40, 108, 177, 162, 136),
})
local magicPathKey = Model.new(360, {
  [6] = Vertex.new(4, 48, 96, 184, 18, 209, 0.7490),
  [258] = Vertex.new(20, 32, 96, 209, 58, 18, 0.7490),
  [260] = Vertex.new(20, 32, 96, 209, 58, 18, 0.7490),
  [263] = Vertex.new(4, 48, 96, 209, 58, 18, 0.7490),
  [264] = Vertex.new(20, 32, 96, 209, 58, 18, 0.7490),
})
local bansheeVoice = Model.multi({
  Model.new(204, {
    [183] = Vertex.new(-12, 108, 48, 200, 17, 76),
    [185] = Vertex.new(12, 108, 48, 200, 17, 76),
    [191] = Vertex.new(-28, 96, 52, 200, 17, 76),
    [194] = Vertex.new(-32, 92, 48, 200, 17, 76),
    [204] = Vertex.new(32, 92, 48, 200, 17, 76),
  }),
  Model.new(954, {
    [42] = Vertex.new(-28, 96, 52, 92, 84, 92, 0.7059),
    [77] = Vertex.new(28, 96, 52, 92, 84, 92, 0.7059),
    [80] = Vertex.new(-28, 96, 52, 92, 84, 92, 0.7059),
    [117] = Vertex.new(28, 96, 52, 92, 84, 92, 0.7059),
    [242] = Vertex.new(0, 112, 28, 105, 96, 105, 0.7059),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Sumona in Pollnivneach.",
    title = "Getting started",
    warning = "Pick up your cat if it's out before talking to Sumona.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Al Kharid lodestone",
      url = "Al_Kharid_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3358, 965, 2993) },
    postconditions = { Condition.ModelVisible:new(sumona) },
  },
  {
    actions = { Action.ModelHighlight:new(sumona) },
    jumpconditions = { Condition.ModelNotVisible:new(sumona) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Sumona.",
    actions = { Action.ModelHighlight:new(sumona) },
    postconditions = { Condition.ConversationText:new("Get to it") },
  },
  {
    text = "Equip your catspeak amulet.",
    title = "Finding the tomb",
    neededItems = {
      ["Catspeak amulet"] = { quantity = 1 },
      ["Rope"] = { quantity = 1 },
    },
    recommendedItems = {
      ["Desert heat protection"] = { quantity = 1 },
    },
    actions = { Action.InventoryHighlight:new(Models.items["catspeak amulet"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["catspeak amulet"]) },
  },
  {
    text = "Attempt to climb down the well.",
    actions = { Action.Direction:new(3358.5, 1265, 2971.5) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to Ali Cat.",
    actions = { Action.ModelHighlight:new(wellCat) },
    postconditions = { Condition.ConversationText:new("take your cat") },
  },
  {
    text = "Climb up onto the plataeu to the south-east.",
    actions = {
      Action.PathGuide:new({ -- when map direction arrow is implemented, make this into a direction arrow
        Location:new(3357, 965, 2973),
        Location:new(3357, 965, 2967),
        Location:new(3356, 965, 2963),
        Location:new(3352, 965, 2962),
        Location:new(3350, 965, 2958),
        Location:new(3347, 965, 2958),
        Location:new(3347, 965, 2949),
        Location:new(3333, 829, 2941),
        Location:new(3316, 533, 2934),
        Location:new(3301, 533, 2926),
        Location:new(3291, 485, 2920),
        Location:new(3283, 581, 2912),
        Location:new(3283, 341, 2883),
        Location:new(3292, 1005, 2875),
        Location:new(3298, 1224, 2861),
        Location:new(3305, 869, 2851),
        Location:new(3319, 694, 2841),
        Location:new(3331, 941, 2830),
        Location:new(3341, 834, 2819),
        Location:new(3354, 1221, 2806),
        Location:new(3359, 933, 2796),
        Location:new(3368, 1573, 2785),
        Location:new(3377, 709, 2770),
        Location:new(3381, 821, 2763),
        Location:new(3391, 1181, 2760),
        Location:new(3398, 813, 2757),
        Location:new(3404, 477, 2760),
        Location:new(3412, 301, 2766),
        Location:new(3418, 101, 2774),
        Location:new(3420, 101, 2782),
        Location:new(3428, 133, 2791),
        Location:new(3428, 173, 2798),
        Location:new(3421, 157, 2795),
        Location:new(3418, 101, 2797),
        Location:new(3418, 461, 2800),
        Location:new(3419, 565, 2801),
      }),
    },
    jumpconditions = { Condition.ModelVisible:new(shortcutRockRope) },
    jumpOffset = 1, --positive jumpoffset values seem to be bugged
    postconditions = {
      Condition.DistanceTo:new(3419, 1221, 2804, 1),
      Condition.DistanceTo:new(3382, 2157, 2822, 1),
    },
  },
  {
    actions = { Action.ModelHighlight:new(shortcutRockRope) },
    postconditions = {
      Condition.DistanceTo:new(3419, 1221, 2804, 1),
      Condition.DistanceTo:new(3382, 2157, 2822, 1),
    },
  },
  {
    text = "Use a rope on the rock to the north-west.",
    actions = { Action.Direction:new(3382, 2205, 2824) },
    postconditions = {
      Condition.ModelVisible:new(shortcutRock),
      Condition.ModelVisible:new(shortcutRockRope),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(shortcutRock),
      Action.InventoryHighlight:new(Models.items["rope"]),
    },
    jumpconditions = { Condition.ModelNotVisible:new(shortcutRock) },
    jumpOffset = -1,
    postconditions = { Condition.ModelVisible:new(shortcutRockRope) },
  },
  {
    text = "Search the western wall.",
    actions = { Action.Direction:new(3404.5, 2917, 2839.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Drop your cat.",
    title = "Inside the tomb",
    neededItems = {
      ["Any cat"] = { quantity = 1 },
      ["Catspeak amulet"] = { quantity = 1 },
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Earmuffs"] = { quantity = 1 },
      ["Face mask"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.InventoryHighlight:new(Models.items["any cat"]) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["any cat"]) },
  },
  {
    text = "Search the western holey wall.",
    actions = { Action.Direction:new(-6.5, 50, 3, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(-6, -1920, 4, 4, true) },
  },
  {
    actions = { Action.ResetInstance:new({ atLocation = Location:new(0, -1920, 4) }) },
    postconditions = { Condition.Always:new() },
  },
  {
    text = "Destroy the broken door.",
    actions = { Action.Direction:new(-11, 400, 5.5, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(destroyedDoor, { instance = true, atLocation = Location:new(-11, 0, 5.5) }),
    },
  },
  {
    text = "Search the wall twice.",
    actions = { Action.Direction:new(-12.5, 650, 7, { instance = true }) },
    postconditions = { Condition.ChatText:new("just ahead of you") },
  },
  --chattext is broken again. it will fire off if two chattext conditions are b2b
  {
    actions = { Action.Direction:new(-12.5, 650, 7, { instance = true }) },
    postconditions = { Condition.ChatText:new("just ahead of you") },
  },
  {
    text = "Enter the door to the north.",
    actions = { Action.Direction:new(-14, 400, 16.25, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(-14, 0, 18, 1, true) },
  },
  {
    text = "Kill the zombies and skeletons.<ul><li>It might take a second for the mummy warrior to spawn.</li></ul>",
    postconditions = { Condition.ModelVisible:new(mummyWarrior) },
  },
  {
    text = "Kill the mummy warrior.",
    postconditions = { Condition.ModelNotVisible:new(mummyWarrior) },
  },
  {
    text = "Pick up the ranged path key.",
    actions = { Action.ModelHighlight:new(rangedPathKey) },
    postconditions = { Condition.InventoryContains:new(rangedPathKey) },
  },
  {
    text = "Open the door to the east.",
    actions = { Action.Direction:new(-10.5, 400, 25, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(-9, 0, 25, 1, true) },
  },
  {
    text = "Pull the lever on the northern wall.",
    actions = { Action.Direction:new(-7, 500, 25, { instance = true }) },
    postconditions = { Condition.ConversationText:new("pull the lever") },
  },
  {
    text = "Return to the starting room.",
    actions = { Action.Direction:new(0, 400, 4, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(0, 400, 4, 8, true) },
  },
  {
    text = "Destroy the broken door.",
    actions = { Action.Direction:new(12, 400, 5.5, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(destroyedDoor, { instance = true, atLocation = Location:new(12, 0, 5.5) }),
    },
  },
  {
    text = "Search the wall twice.",
    actions = { Action.Direction:new(13.5, 650, 7, { instance = true }) },
    postconditions = { Condition.ChatText:new("just ahead of you") },
  },
  --chattext is broken again. it will fire off if two chattext conditions are b2b
  {
    actions = { Action.Direction:new(13.5, 650, 7, { instance = true }) },
    postconditions = { Condition.ChatText:new("just ahead of you") },
  },
  {
    text = "Enter the door to the north.",
    actions = { Action.Direction:new(16, 400, 16.25, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(14, 0, 18, 1, true) },
  },
  {
    text = "Kill the zombies and skeletons.<ul><li>It might take a second for the mummy warrior to spawn.</li></ul>",
    postconditions = { Condition.ModelVisible:new(mummyWarrior) },
  },
  {
    text = "Kill the mummy warrior.",
    postconditions = { Condition.ModelNotVisible:new(mummyWarrior) },
  },
  {
    text = "Pick up the magic path key.",
    actions = { Action.ModelHighlight:new(magicPathKey) },
    postconditions = { Condition.InventoryContains:new(magicPathKey) },
  },
  {
    text = "Open the door to the west.",
    actions = { Action.Direction:new(11.5, 400, 24, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(10, 0, 24, 1, true) },
  },
  {
    text = "Pull the lever on the northern wall.",
    actions = { Action.Direction:new(8, 500, 25, { instance = true }) },
    postconditions = { Condition.ConversationText:new("pull the lever") },
  },
  {
    text = "Return to the starting room.",
    actions = { Action.Direction:new(0, 400, 4, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(0, 400, 4, 8, true) },
  },
  {
    text = "Equip a ghostspeak amulet.",
    actions = { Action.InventoryHighlight:new(Models.items["ghostspeak amulet"]) },
    postconditions = { Condition.InventoryDoesNotContain:new(Models.items["ghostspeak amulet"]) },
  },
  {
    text = "Talk to Catolax.",
    actions = { Action.ModelHighlight:new(catolax) },
    postconditions = { Condition.ConversationText:new("Slayer face mask") }, --not tested
  },
  {
    text = "Use earmuffs on a face mask.",
    actions = {
      Action.InventoryHighlight:new(Models.items["earmuffs"]),
      Action.InventoryHighlight:new(Models.items["face mask"]),
    },
    postconditions = { Condition.InventoryContains:new(maskedEarmuffs) },
  },
  {
    text = "Equip your masked earmuffs.",
    title = "The final fight",
    neededItems = {
      ["Masked earmuffs"] = { quantity = 1, model = maskedEarmuffs },
      ["Ranged/Magic/Necromancy combat gear"] = { quantity = 1 },
    },
    recommendedItems = {
      ["Ring of slaying"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    actions = { Action.InventoryHighlight:new(maskedEarmuffs) },
    postconditions = {
      Condition.ItemClicked:new(maskedEarmuffs),
      Condition.InventoryDoesNotContain:new(maskedEarmuffs),
    },
  },
  {
    text = "Enter the well in the Pollnivneach town centre.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Al Kharid lodestone",
      url = "Al_Kharid_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3358.5, 1265, 2971.5) },
    postconditions = { Condition.DistanceTo:new(3358, 1669, 9354, 4) },
  },
  {
    text = "Kill a mighty banshee.",
    actions = { Action.ModelHighlight:new(mightyBanshee) },
    postconditions = {
      Condition.ModelVisible:new(bansheeVoiceObj),
      Condition.InventoryContains:new(bansheeVoice),
    },
  },
  {
    text = "Pick up the banshee voice.",
    actions = { Action.ModelHighlight:new(bansheeVoiceObj) },
    postconditions = { Condition.InventoryContains:new(bansheeVoice) },
  },
  {
    text = "Pick up your cat/dismiss your follower.",
    actions = { Action.ModelHighlight:new(Models.npcs["any cat"]) },
    postconditions = { Condition.InventoryContains:new(Models.items["any cat"]) },
  },
  {
    text = "Pass through the mystic barrier to the north.",
    actions = { Action.ModelHighlight:new(mysticBarrier) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the cutscene.",
    warning = "Do not click on anything while in the cutscene.",
    postconditions = { Condition.ConversationText:new("righteous wrath") },
  },
  {
    text = "Kill the Banshee mistress.<ul><li>She is weak to bolts.</li><li>Protect/deflect ranged when she gets to half health.</li></ul>",
    postconditions = { Condition.ConversationText:new("Once more") },
  },
  {
    text = "Watch the cutscene.",
    warning = "Do not click on anything while in the cutscene.",
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Climb out of the well to the south.",
    title = "Finishing up",
    actions = { Action.ModelHighlight:new(wellRope) },
    postconditions = { Condition.DistanceTo:new(3358.5, 1265, 2971.5, 8) },
  },
  {
    text = "Talk to Sumona.",
    actions = { Action.Direction:new(3358, 965, 2993) },
    postconditions = { Condition.ModelVisible:new(sumona) },
  },
  {
    actions = { Action.ModelHighlight:new(sumona) },
    jumpconditions = { Condition.ModelNotVisible:new(sumona) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Smoking Kills",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.short,
  releaseDate = 1212624000,
  prereqQuests = {
    "The Restless Ghost",
    "Icthlarin's Little Helper",
  },
  questReqs = {
    Types.QuestReq.skill("Crafting", 25),
    Types.QuestReq.skill("Slayer", 35),
  },
  neededItems = {
    ["Any cat"] = { quantity = 1, model = Models.items["any cat"] },
    ["Catspeak amulet"] = { quantity = 1, model = Models.items["catspeak amulet"] },
    ["Earmuffs"] = { quantity = 1, model = Models.items["earmuffs"] },
    ["Face mask"] = { quantity = 1, model = Models.items["face mask"] },
    ["Ghostspeak amulet"] = { quantity = 1, model = Models.items["ghostspeak amulet"] },
    ["Ranged/Magic/Necromancy combat gear"] = { quantity = 1 },
    ["Rope"] = { quantity = 1 },
  },
  recommendedItems = {
    ["Ring of slaying"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
    ["Desert heat protection"] = { quantity = 1 },
  },
  combatNPCs = {
    ["Banshee mistress"] = { level = "93", quantity = 1 },
    ["Zombies"] = { level = "12", quantity = 1 },
    ["Skeletons"] = { level = "12", quantity = 1 },
    ["Mummy warrior"] = { level = "77", quantity = 2 },
    ["Mighty banshee"] = { level = "61", quantity = 1 },
  },
})
