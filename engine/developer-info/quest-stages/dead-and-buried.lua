local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local heraldOfVarrock = Model.new(6582, {
  [3645] = Vertex.new(28, 563, -55, 68, 67, 6),
  [4082] = Vertex.new(74, 601, -13, 150, 108, 13),
  [4562] = Vertex.new(-69, 601, -13, 150, 108, 13),
  [5100] = Vertex.new(74, 600, -14, 34, 46, 85),
  [5114] = Vertex.new(-69, 600, -14, 34, 46, 85),
})
local materialGhoul = Model.new(8172, {
  [8086] = Vertex.new(21, 700, -152, 127, 127, 127),
  [8092] = Vertex.new(28, 699, -145, 127, 127, 127),
  [8104] = Vertex.new(-28, 702, -145, 127, 127, 127),
  [8105] = Vertex.new(-21, 703, -152, 127, 127, 127),
  [8107] = Vertex.new(-21, 703, -152, 127, 127, 127),
})
local theNightmare = Model.new(6771, {
  [143] = Vertex.new(24, 742, -42, 127, 127, 127),
  [162] = Vertex.new(-24, 742, -42, 127, 127, 127),
  [3222] = Vertex.new(-27, 761, -34, 40, 52, 44),
  [6483] = Vertex.new(-29, 741, -36, 40, 52, 44),
  [6486] = Vertex.new(29, 741, -36, 40, 52, 44),
})
--#endregion
--#region Objects
local questObj = Model.new(2664, {
  [49] = Vertex.new(-30, 432, 212, 47, 89, 153),
  [127] = Vertex.new(-30, 570, 126, 47, 75, 146),
  [187] = Vertex.new(-30, 570, -126, 47, 75, 146),
  [289] = Vertex.new(30, 432, 212, 47, 89, 153),
  [367] = Vertex.new(30, 570, 126, 47, 75, 146),
})
local constructionSpot = Model.new(4572, {
  [29] = Vertex.new(-346, 801, -369, 167, 167, 167),
  [133] = Vertex.new(370, 760, -354, 167, 167, 167),
  [151] = Vertex.new(384, 747, 300, 167, 167, 167),
  [349] = Vertex.new(-346, 801, -369, 167, 167, 167),
  [2635] = Vertex.new(-346, 801, -369, 167, 167, 167),
})
local ancientDoor = Model.new(7542, {
  [341] = Vertex.new(1017, 1942, 2133, 127, 127, 127),
  [350] = Vertex.new(1017, 1942, 2133, 127, 127, 127),
  [359] = Vertex.new(1017, 1942, 2133, 127, 127, 127),
  [395] = Vertex.new(-622, 1942, 2133, 128, 127, 127),
  [7223] = Vertex.new(959, 1957, 2020, 127, 127, 127),
})
local unlockedPillar = Model.new(1311, {
  [365] = Vertex.new(-68, 919, -92, 127, 127, 127),
  [395] = Vertex.new(78, 919, -92, 127, 127, 127),
  [637] = Vertex.new(24, 1277, -124, 127, 127, 127),
  [724] = Vertex.new(208, 2176, 244, 127, 127, 127),
  [730] = Vertex.new(-188, 2176, 244, 127, 127, 127),
})
local boulder = Model.new(1170, {
  [46] = Vertex.new(-126, 769, 48, 127, 127, 127),
  [278] = Vertex.new(-124, 748, 99, 127, 127, 127),
  [339] = Vertex.new(100, 741, -106, 127, 127, 127),
  [558] = Vertex.new(-124, 748, 99, 127, 127, 127),
  [936] = Vertex.new(-124, 748, 99, 127, 127, 127),
})
local barrier = Model.new(2112, {
  [389] = Vertex.new(-385, 897, 43, 128, 127, 127, 0.01176),
  [793] = Vertex.new(-769, 5, 49, 128, 127, 127, 0.1020),
  [1874] = Vertex.new(-769, 613, -23, 128, 127, 127, 0.01176),
  [1987] = Vertex.new(-769, 0, -69, 128, 127, 127, 0.1020),
  [1997] = Vertex.new(769, 0, -69, 128, 127, 127, 0.1020),
})
local ancientWritings = Model.new(2028, {
  [83] = Vertex.new(-183, 973, 2, 128, 127, 127),
  [233] = Vertex.new(183, 950, -2, 128, 127, 127),
  [235] = Vertex.new(183, 832, -2, 127, 127, 127),
  [523] = Vertex.new(158, 949, -1612, 127, 127, 127),
  [1390] = Vertex.new(-84, 1377, 971, 127, 127, 127),
})
local ancientDoorOpen = Model.new(7734, {
  [347] = Vertex.new(1017, 1942, 2133, 127, 127, 127),
  [365] = Vertex.new(1017, 1942, 2133, 127, 127, 127),
  [401] = Vertex.new(-622, 1942, 2133, 128, 127, 127),
  [483] = Vertex.new(-540, 52, 2018, 127, 127, 127),
  [487] = Vertex.new(907, 52, 2018, 127, 127, 127),
})
local table = Model.new(2418, {
  [799] = Vertex.new(-1327, 0, 1267, 97, 97, 97),
  [833] = Vertex.new(1267, 0, 1327, 97, 97, 97),
  [845] = Vertex.new(-1267, 0, -1327, 97, 97, 97),
  [877] = Vertex.new(-1297, 352, -1297, 97, 97, 97),
  [881] = Vertex.new(1297, 352, 1297, 97, 97, 97),
})
--#endregion
--#region Items
local acadiaFrame = Model.new(435, {
  [376] = Vertex.new(-182, 1094, -49, 68, 49, 43),
  [380] = Vertex.new(-182, 1094, -49, 68, 49, 43),
  [428] = Vertex.new(-178, 1097, 101, 68, 49, 43),
  [431] = Vertex.new(-178, 1097, 101, 68, 49, 43),
  [435] = Vertex.new(-178, 1097, 101, 68, 49, 43),
})
--#endregion
--#region Quest Items
local dragonkinDevice = Model.new(348, {
  [13] = Vertex.new(33, 80, 35, 55, 72, 71),
  [78] = Vertex.new(33, 80, 35, 71, 93, 92),
  [137] = Vertex.new(33, 80, 35, 71, 93, 92),
  [249] = Vertex.new(33, 80, 35, 71, 93, 92),
  [252] = Vertex.new(33, 80, 35, 71, 93, 92),
})
local potionOfRestlessSleep = Model.multi({
  Model.new(216, {
    [151] = Vertex.new(4, 96, 12, 113, 101, 46),
    [153] = Vertex.new(-4, 96, 12, 113, 101, 46),
    [154] = Vertex.new(-4, 96, 12, 113, 101, 46),
    [157] = Vertex.new(-4, 96, 12, 113, 101, 46),
    [159] = Vertex.new(-12, 96, 4, 113, 101, 46),
  }),
  Model.new(240, {
    [194] = Vertex.new(12, 96, -4, 134, 136, 147, 0.3137),
    [195] = Vertex.new(12, 96, 4, 134, 136, 147, 0.3137),
    [198] = Vertex.new(12, 96, 4, 134, 136, 147, 0.3137),
    [216] = Vertex.new(-4, 96, 12, 134, 136, 147, 0.3137),
    [221] = Vertex.new(-4, 96, 12, 134, 136, 147, 0.3137),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the Raptor.",
    title = "Starting off",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(3292, 685, 3544) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["the raptor"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["the raptor"]),
      Action.ConversationHighlight:new("Talk about 'Dead and Buried'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["the raptor"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Raptor.",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    postconditions = { Condition.ConversationText:new("...") },
  },
  {
    text = "Continue with the portal in front of the Town Hall to enter.",
    actions = { Action.Direction:new(3303, 1821, 3562) },
    postconditions = { Condition.ModelVisible:new(questObj) },
  },
  {
    actions = { Action.ModelHighlight:new(questObj) },
    jumpconditions = { Condition.ModelNotVisible:new(questObj) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to the Herald of Varrock.",
    actions = {
      Action.ModelHighlight:new(heraldOfVarrock),
      Action.ConversationHighlight:new("Yes, I know that"),
      Action.ConversationHighlight:new("Must we"),
      Action.ConversationHighlight:new("Leadership skills"),
      Action.ConversationHighlight:new("(Conclude interview.)"),
      Action.ConversationHighlight:new("Yes. I've asked enough questions."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Bill.",
    title = "Building the workroom",
    neededItems = {
      ["Acadia frames"] = { quantity = 14 },
      ["Stone wall segments"] = { quantity = 6 },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(3286, 965, 3557) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["bill"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["bill"]),
      Action.ConversationHighlight:new("Talk about 'Dead and Buried'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["bill"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("fast you work") },
  },
  {
    text = "Start the Ranger's Workroom (Tier 1) blueprint.",
    actions = { Action.ModelHighlight:new(Models.objects["forinthry blueprint table"]) },
    postconditions = {
      Condition.InventoryDoesNotContain:new(acadiaFrame),
      Condition.InventoryDoesNotContain:new(Models.items["stone wall segments"]),
    },
  },
  {
    text = "Build the ranger's workroom in the south-west near the guardhouse.",
    actions = { Action.ModelHighlight:new(Models.objects["forinthry optimal hotspot"]) },
    postconditions = { Condition.ChatText:new("advanced your building") },
  },
  {
    text = "Talk to Guard captain Sofía.",
    actions = { Action.ModelHighlight:new(Models.npcs["guard captain sofia"]) },
    postconditions = { Condition.ConversationText:new("investigate the crypt") },
  },
  {
    text = "Talk to the Raptor.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["the raptor"]),
      Action.ConversationHighlight:new("Talk about 'Dead and Buried'."),
    },
    postconditions = { Condition.ConversationText:new("north of here") },
  },
  {
    text = "Open the gates to the north.",
    title = "Retrieving the dragonkin device",
    actions = { Action.Direction:new(3286.5, 1097, 3576) },
    postconditions = { Condition.ModelVisible:new(Models.objects["forinthry northern gate"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["forinthry northern gate"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.objects["forinthry northern gate"]) },
    jumpOffset = -1,
    postconditions = { Condition.ModelVisible:new(Models.objects["forinthry northern gate open"]) },
  },
  {
    text = "Click on the quest portal to the north.",
    actions = { Action.Direction:new(3290, 675, 3610) },
    postconditions = {
      Condition.ModelVisible:new(Models.objects["quest portal"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.objects["quest portal"]) },
    jumpconditions = { Condition.ModelNotVisible:new(Models.objects["quest portal"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to the Raptor.",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    postconditions = { Condition.ConversationText:new("lava") },
  },
  {
    text = "Search the spooky holes in the western room.",
    actions = {
      Action.Direction:new(-40, -3296, 8, { instance = true }),
      Action.Direction:new(-32, -3584, 2, { instance = true }),
      Action.Direction:new(-45, -3384, 0.5, { instance = true }),
      Action.Direction:new(-37.5, -3744, -4.5, { instance = true }),
      Action.Direction:new(-45, -3408, -8.5, { instance = true }),
      Action.Direction:new(-39.5, -3392, -12.5, { instance = true }),
    },
    postconditions = { Condition.ChatText:new("3 materials") },
  },
  {
    text = "Kill the ghouls.",
    actions = { Action.ModelHighlight:new(materialGhoul) },
    postconditions = { Condition.ChatText:new("found enough materials") },
  },
  {
    text = "Construct the bridge.",
    actions = { Action.Direction:new(-17, -1196, -10, { instance = true }) },
    postconditions = {
      Condition.ModelVisible:new(constructionSpot),
      Condition.ConversationText:new("look at that door"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(constructionSpot) },
    jumpconditions = { Condition.ModelNotVisible:new(constructionSpot) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("look at that door") },
  },
  {
    text = "Investigate the ancient door across the bridge.",
    actions = { Action.ModelHighlight:new(ancientDoor, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(unlockedPillar) },
  },
  {
    text = "Push either pillar.",
    actions = {
      Action.ModelHighlight:new(unlockedPillar, { instance = true, highlightPriority = "closest" }),
    },
    postconditions = { Condition.ConversationText:new("both of us") },
  },
  {
    text = "Switch to the raptor.",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
  },
  {
    text = "Push the other pillar.",
    actions = {
      Action.ModelHighlight:new(unlockedPillar, { instance = true, highlightPriority = "closest" }),
    },
    postconditions = { Condition.ConversationText:new("popped out") },
  },
  {
    text = "Search the ancient door.",
    actions = { Action.ModelHighlight:new(ancientDoor, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(dragonkinDevice) },
  },
  {
    text = "Talk to the Raptor.",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    postconditions = { Condition.ConversationText:new("Reldo") },
  },
  {
    text = "Go to the Varrock Library and continue with the portal by the entrance.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3210, 1253, 3489) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    actions = { Action.Direction:new(3210, 1253, 3489) },
    postconditions = {
      Condition.ModelVisible:new(Models.objects["quest portal"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.objects["quest portal"]),
      Action.ConversationHighlight:new("Yes."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.objects["quest portal"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Reldo.",
    title = "Starting to dream",
    actions = { Action.ModelHighlight:new(Models.npcs["reldo"]) },
    postconditions = { Condition.ConversationText:new("restless sleep potion") },
  },
  {
    text = "Drink the potion.",
    actions = { Action.InventoryHighlight:new(potionOfRestlessSleep) },
    postconditions = { Condition.ConversationText:new("Pleasant dreams") },
  },
  {
    text = "Inspect the dragonkin device.",
    actions = {
      Action.InventoryHighlight:new(dragonkinDevice),
      Action.ConversationHighlight:new("Enter dream."),
    },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Talk to the Raptor.",
    title = "Boulder puzzle 1",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"], { instance = true }) },
    postconditions = { Condition.ConversationText:new("shouldn't be here") },
  },
  {
    text = "Stand on the pressure plate.",
    actions = { Action.Direction:new(-2, -98, 4, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-2, -98, 4, 0, true) },
  },
  {
    text = "Swap to the Raptor.",
    postconditions = { Condition.DistanceTo:new(1, -192, 6, 0, true) },
  },
  {
    text = "Stand on the other pressure plate.",
    actions = { Action.Direction:new(-2, -10, 8, { instance = true, tile = true }) },
    postconditions = { Condition.ConversationText:new("door mechanism") },
  },
  {
    text = "Move the boulder to the marked tile.",
    title = "Boulder puzzle 2",
    actions = {
      Action.ModelHighlight:new(boulder, { instanced = true, highlightPriority = "closest" }),
      Action.Direction:new(1, -170, 19, { instance = true, tile = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(boulder, { instance = true, atLocation = Location:new(1, -170, 19) }),
    },
  },
  {
    text = "Stand on the marked tile.",
    actions = { Action.Direction:new(4, -114, 21, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(4, -114, 21, 0, true) },
  },
  {
    text = "Swap back to the player character.",
    postconditions = { Condition.DistanceTo:new(-2, -98, 4, 0, true) },
  },
  {
    text = "Stand on the marked tile.",
    actions = { Action.Direction:new(4, -138, 17, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(4, -138, 17, 0, true) },
  },
  {
    text = "Swap back to the Raptor.",
    title = "Boulder puzzle 3",
    postconditions = { Condition.DistanceTo:new(4, -114, 21, 0, true) },
  },
  {
    text = "Move the boulder to the marked tile.",
    actions = {
      Action.ModelHighlight:new(boulder, { instanced = true, highlightPriority = "closest" }),
      Action.Direction:new(3, -168, 22, { instance = true, tile = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(boulder, { instance = true, atLocation = Location:new(3, -168, 22) }),
    },
  },
  {
    text = "Push the boulder to the marked tile.",
    actions = {
      Action.ModelHighlight:new(boulder, { instanced = true, atLocation = Location:new(3, -168, 22) }),
      Action.Direction:new(3, 6, 27, { instance = true, tile = true }),
    },
    postconditions = {
      Condition.ModelVisible:new(boulder, { instance = true, atLocation = Location:new(3, 6, 27) }),
    },
  },
  {
    text = "Move the other boulder to the marked tile.",
    actions = {
      Action.ModelHighlight:new(boulder, { instanced = true, highlightPriority = "closest" }),
      Action.Direction:new(5, 22, 26, { instance = true, tile = true }),
    },
    postconditions = { Condition.DistanceTo:new(5, 22, 26, 0, true) },
  },
  {
    text = "Stand on the marked tile.",
    actions = { Action.Direction:new(-1, 38, 27, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-1, 38, 27, 0, true) },
  },
  {
    text = "Swap back to the player character.",
    postconditions = { Condition.DistanceTo:new(4, -138, 17, 0, true) },
  },
  {
    text = "Stand on the marked tile.",
    actions = { Action.Direction:new(-3, 86, 26, { instance = true, tile = true }) },
    postconditions = { Condition.DistanceTo:new(-3, 86, 26, 0, true) },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("to you") } },
  {
    text = "Swap to the player character.",
    title = "Boulder puzzle 4",
    warning = "Due to an issue in development, boulder puzzle 4, boulder puzzle 5, and The nightmare do not have tracking.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Stand on the either of the tiles farthest away from the barrier.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Swap to the Raptor.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Push the boulder onto the pressure plate.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Swap to the player character.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Stand on the other pressure plate.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Swap to the Raptor.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Push the boulder onto the pressure plate.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Stand on one of the two remaining pressure plates.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Move the spawned boulder to the marked tile.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Swap back to the player character.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Stand on the remaining pressure plate.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Walk through.",
    actions = {},
    postconditions = {},
  },
  {
    text = "The Raptor steps on the western most pressure plate.",
    title = "Boulder puzzle 5",
    actions = {},
    postconditions = {},
  },
  {
    text = "The player pulls the boulder and pushes it to the second pressure plate (going clockwise).",
    actions = {},
    postconditions = {},
  },
  {
    text = "The player steps on the third pressure plate, and the second boulder appears.<ul><li>If the second boulder does not spawn, exit to the lobby and continue again with the portal at the library.</li></ul>",
    actions = {},
    postconditions = {},
  },
  {
    text = "The player pushes and pulls the boulder to the fourth pressure plate.",
    actions = {},
    postconditions = {},
  },
  {
    text = "The player steps on the third pressure plate to open the gate.",
    actions = {},
    postconditions = {},
  },
  {
    text = "Run to the strange girl to start a cutscene.",
    actions = {},
    postconditions = { Condition.ConversationText:new("makes you weak") },
  },
  {
    text = "Go through all the chat options, then delve deeper into the dream.",
    title = "The nightmare",
    actions = { Action.ConversationHighlight:new("Why did you marry the king?") },
    postconditions = { Condition.ConversationText:new("my own life") },
  },
  {
    actions = { Action.ConversationHighlight:new("Couldn't you have been a warrior queen?") },
    postconditions = { Condition.ConversationText:new("disastrous") },
  },
  {
    actions = { Action.ConversationHighlight:new("So you started wearing a disguise?") },
    postconditions = { Condition.ConversationText:new("of the time") },
  },
  {
    actions = { Action.ConversationHighlight:new("Is being rude to people an act too?") },
    postconditions = { Condition.ConversationText:new("go deeper") },
  },
  {
    text = "Interact with the dim light.",
    actions = { Action.Direction:new(7, 0, 13, { instance = true, tile = true }) },
    postconditions = { Condition.ModelVisible:new(theNightmare) },
  },
  {
    text = "Lower the health of the Nightmares as the player, then swap to the Raptor to deal the finishing blow.<ul><li>Currently this part is glitched if you are in a group, which will prevent the player from progressing as expected. Simply leaving the group and re-entering the instance will allow progress.</li></ul>",
    actions = {},
    postconditions = {},
  },
  {
    text = "Interact with the pillar of light and talk with the character that appears.",
    actions = { Action.Direction:new(45, 704, 27, { instance = true, tile = true }) },
    postconditions = {},
  },
  {
    text = "Continue on the path, and repeat the same process with the dim lights along the way.",
    actions = { Action.Direction:new(7, 992, 42, { instance = true, tile = true }) },
    postconditions = {},
  },
  {
    text = "Finally, speak to the Raptor that appears.",
    actions = {},
    postconditions = { Condition.ModelVisible:new(ancientWritings) },
  },
  {
    text = "Read the ancient writings on the wall at the end of the path.",
    actions = { Action.ModelHighlight:new(ancientWritings) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Finish talking to the Raptor.",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Continue with the portal in front of the crypt in the Wilderness.",
    title = "Back to the crypt",
    actions = { Action.Direction:new(3290, 675, 3610) },
    postconditions = {
      Condition.ModelVisible:new(Models.objects["quest portal"]),
      Condition.InInstance:new(),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.objects["quest portal"]),
      Action.ConversationHighlight:new("Yes"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.objects["quest portal"]) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ChangedInstance:new() } },
  {
    text = "Enter the ancient door.",
    actions = { Action.ModelHighlight:new(ancientDoorOpen, { instance = true }) },
    postconditions = { Condition.ChangedInstance:new() },
  },
  {
    text = "Investigate the map table in the central room.",
    actions = { Action.Direction:new(-7, -2676, 28, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(table) },
  },
  {
    actions = { Action.ModelHighlight:new(table) },
    jumpconditions = { Condition.ModelNotVisible:new(table) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("faith in you") },
  },
  {
    text = "Kill fetid zombies around the crypt and collect 10 map pieces.",
    warning = "Do not leave the crypt.",
    actions = { Action.ModelHighlight:new(Models.npcs["fetid zombie"], { highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("found enough") },
  },
  {
    text = "Deposit onto the map table to start the puzzle.",
    actions = { Action.Direction:new(-7, -2676, 28, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(table) },
  },
  {
    actions = { Action.ModelHighlight:new(table) },
    jumpconditions = { Condition.ModelNotVisible:new(table) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("pieces around") },
  },
  {
    text = "Refer to the wiki to complete the puzzle.<ul><li>Speaking to the Raptor to turn on accessibility mode is advised; this will highlight tiles while they are in the correct position.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Toggle accessibility mode"),
      Action.ConversationHighlight:new("I'd like it on"),
    },
    postconditions = { Condition.ConversationText:new("complete") },
  },
  {
    text = "Talk to the Raptor.",
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    postconditions = { Condition.ConversationText:new("our secret") },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["the raptor"]) },
    postconditions = { Condition.ConversationText:new("back at the fort") },
  },
  {
    text = "Talk to Overseer Siv in the Command Centre back at Fort Forinthry.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Falador lodestone",
      url = "Fort_Forinthry_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3319, 985, 3540) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["overseer siv"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["overseer siv"]),
      Action.ConversationHighlight:new("Talk about quests."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["overseer siv"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("guardhouse by now") },
  },
  {
    text = "Talk to the Raptor.",
    actions = { Action.Direction:new(3292, 1185, 3544) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["the raptor"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["the raptor"]),
      Action.ConversationHighlight:new("Talk about 'Dead and Buried'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["the raptor"]) },
    jumpOffset = -1,
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Dead and Buried",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1689552000,
  prereqQuests = { "Unwelcome Guests" },
  questReqs = { Types.QuestReq.ironmanOnlySkill("Woodcutting", 50, true) },
  neededItems = {
    ["Acadia frames"] = { quantity = 14, model = acadiaFrame },
    ["Stone wall segments"] = { quantity = 6, model = Models.items["stone wall segments"] },
    ["Combat gear"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Material ghouls"] = { level = "Scaled", quantity = 3 },
    ["Nightmares"] = { level = "Scaled", quantity = 22 },
    ["Fetid zombies"] = { level = "Scaled", quantity = 20 },
  },
})
