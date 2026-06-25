local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local effigy = Model.new(3816, {
  [1867] = Vertex.new(2, 725, -59, 135, 99, 70),
  [1871] = Vertex.new(7, 724, -51, 135, 99, 70),
  [1885] = Vertex.new(-2, 725, -59, 135, 99, 70),
  [1890] = Vertex.new(-7, 724, -51, 135, 99, 70),
  [3572] = Vertex.new(40, 711, -12, 170, 170, 181),
})
local bonafido = Model.new(7770, {
  [3879] = Vertex.new(-268, 104, 480, 106, 88, 67),
  [3969] = Vertex.new(-508, 104, 180, 98, 81, 62),
  [4641] = Vertex.new(392, 84, 396, 98, 81, 62),
  [6436] = Vertex.new(0, 735, -7, 35, 174, 158),
  [6806] = Vertex.new(-5, 745, -52, 19, 17, 17),
})
local blackEye = Model.new(3633, {
  [2326] = Vertex.new(-2, 725, -59, 135, 99, 70),
  [2331] = Vertex.new(-7, 724, -51, 135, 99, 70),
  [2334] = Vertex.new(2, 725, -59, 135, 99, 70),
  [2336] = Vertex.new(7, 724, -51, 135, 99, 70),
  [2635] = Vertex.new(0, 735, -7, 35, 174, 158),
})
local theGuns = Model.new(4281, {
  [3016] = Vertex.new(-2, 725, -59, 135, 99, 70),
  [3021] = Vertex.new(-7, 724, -51, 135, 99, 70),
  [3024] = Vertex.new(2, 725, -59, 135, 99, 70),
  [3026] = Vertex.new(7, 724, -51, 135, 99, 70),
  [3283] = Vertex.new(0, 735, -7, 35, 174, 158),
})
local noFingers = Model.new(4440, {
  [2443] = Vertex.new(-2, 725, -59, 135, 99, 70),
  [2448] = Vertex.new(-7, 724, -51, 135, 99, 70),
  [2451] = Vertex.new(2, 725, -59, 135, 99, 70),
  [2453] = Vertex.new(7, 724, -51, 135, 99, 70),
  [3730] = Vertex.new(0, 735, -7, 35, 174, 158),
})
local homunculusInCage = Model.new(4236, {
  [3041] = Vertex.new(-376, 1564, 312, 91, 91, 99),
  [3075] = Vertex.new(-376, 1564, 312, 91, 91, 99),
  [3083] = Vertex.new(-308, 1564, 312, 91, 91, 99),
  [3545] = Vertex.new(476, 1084, -392, 91, 91, 99),
  [3671] = Vertex.new(-372, 1084, -460, 91, 91, 99),
})
local homunculus = Model.new(2922, {
  [541] = Vertex.new(60, 956, 240, 101, 93, 93),
  [1143] = Vertex.new(60, 956, 240, 92, 85, 85),
  [2496] = Vertex.new(124, 1300, -144, 155, 143, 142),
  [2741] = Vertex.new(60, 956, 240, 81, 74, 74),
  [2781] = Vertex.new(24, 1112, 204, 155, 143, 142),
})
--#endregion
--#region Objects
local ladder = Model.new(48, {
  [24] = Vertex.new(3948, 4910, 2944, 55, 49, 42),
  [28] = Vertex.new(3744, 3712, 2944, 55, 49, 42),
  [35] = Vertex.new(3956, 4282, 2944, 55, 49, 42),
  [38] = Vertex.new(3876, 3712, 2944, 55, 49, 42),
  [42] = Vertex.new(4044, 4910, 2688, 55, 49, 42),
})
local cage = Model.new(1314, {
  [113] = Vertex.new(-376, 1564, 312, 91, 91, 99),
  [147] = Vertex.new(-376, 1564, 312, 91, 91, 99),
  [155] = Vertex.new(-308, 1564, 312, 91, 91, 99),
  [545] = Vertex.new(476, 1084, -392, 91, 91, 99),
  [671] = Vertex.new(-372, 1084, -460, 91, 91, 99),
})
--#endregion
--#region Quest Items
local hardHat = Model.new(225, {
  [1] = Vertex.new(24, -744, 156, 96, 88, 88),
  [2] = Vertex.new(0, -736, 184, 96, 88, 88),
  [3] = Vertex.new(-24, -744, 156, 96, 88, 88),
  [161] = Vertex.new(32, 4, -80, 81, 70, 51),
})
local buildersShirt = Model.new(456, {
  [7] = Vertex.new(-52, 12, -108, 70, 65, 53),
  [23] = Vertex.new(52, 12, -108, 70, 65, 53),
  [80] = Vertex.new(-136, 0, -36, 68, 49, 96),
  [84] = Vertex.new(-84, 0, -44, 30, 31, 59),
  [101] = Vertex.new(136, 0, -36, 68, 49, 96),
})
local buildersBoots = Model.new(456, {
  [399] = Vertex.new(-56, 76, -16, 102, 81, 9),
  [412] = Vertex.new(-44, 76, -20, 102, 81, 9),
  [413] = Vertex.new(-56, 76, -20, 102, 81, 9),
  [414] = Vertex.new(-48, 84, -20, 102, 81, 9),
  [429] = Vertex.new(56, 76, -20, 102, 81, 9),
})
local buildersTrousers = Model.new(348, {
  [27] = Vertex.new(72, 0, -200, 70, 65, 53),
  [212] = Vertex.new(-72, 20, -200, 70, 65, 53),
  [215] = Vertex.new(-72, 20, -200, 70, 65, 53),
  [272] = Vertex.new(-72, 20, -200, 38, 35, 35),
  [274] = Vertex.new(-72, 20, -200, 38, 35, 35),
})
local valveWheel = Model.new(534, {
  [3] = Vertex.new(-84, 0, 28, 22, 29, 22),
  [57] = Vertex.new(28, 0, 88, 22, 29, 22),
  [60] = Vertex.new(100, 0, -36, 22, 29, 22),
  [63] = Vertex.new(28, 0, 88, 22, 29, 22),
  [66] = Vertex.new(100, 0, -36, 22, 29, 22),
})
local colouredBall = Model.new(240, {
  [151] = Vertex.new(-20, 76, -24, 11, 122, 14),
  [157] = Vertex.new(-20, 76, -24, 11, 122, 14),
  [163] = Vertex.new(-20, 76, -24, 11, 122, 14),
  [217] = Vertex.new(-20, 76, -24, 9, 106, 12),
  [220] = Vertex.new(-20, 76, -24, 9, 106, 12),
})
local metalSheet = Model.new(102, {
  [47] = Vertex.new(-160, 0, 56, 72, 66, 66),
  [50] = Vertex.new(-160, 0, 56, 72, 66, 66),
  [54] = Vertex.new(-160, 0, 56, 72, 66, 66),
  [65] = Vertex.new(164, 0, -56, 64, 59, 59),
  [68] = Vertex.new(164, 0, -56, 64, 59, 59),
})
local rivet = Model.new(147, {
  [26] = Vertex.new(24, 0, -8, 22, 22, 14),
  [32] = Vertex.new(-32, 0, 16, 22, 22, 14),
  [35] = Vertex.new(-44, 0, 0, 22, 22, 14),
  [36] = Vertex.new(-32, 0, 16, 22, 22, 14),
  [38] = Vertex.new(-32, 0, -24, 22, 22, 14),
})
local pipe = Model.new(360, {
  [245] = Vertex.new(156, 92, -32, 122, 122, 133),
  [251] = Vertex.new(156, 92, -32, 122, 122, 133),
  [353] = Vertex.new(-160, 96, 28, 122, 122, 133),
  [357] = Vertex.new(-160, 96, 28, 122, 122, 133),
  [359] = Vertex.new(-160, 96, 28, 122, 122, 133),
})
local pipeRing = Model.new(366, {
  [38] = Vertex.new(40, 0, 36, 12, 13, 31),
  [41] = Vertex.new(40, 0, 36, 12, 13, 31),
  [62] = Vertex.new(52, 0, -48, 12, 13, 31),
  [74] = Vertex.new(72, 0, -4, 12, 13, 31),
  [77] = Vertex.new(72, 0, -4, 12, 13, 31),
})
local bindingFluid = Model.new(366, {
  [96] = Vertex.new(-20, 148, 20, 72, 60, 46),
  [97] = Vertex.new(-20, 148, 20, 72, 60, 46),
  [108] = Vertex.new(-24, 148, -20, 72, 60, 46),
  [109] = Vertex.new(-24, 148, -20, 72, 60, 46),
  [112] = Vertex.new(-24, 148, -20, 72, 60, 46),
})
local metalBar = Model.new(132, {
  [2] = Vertex.new(-196, 0, -104, 2, 2, 22),
  [5] = Vertex.new(196, 0, 56, 2, 2, 22),
  [6] = Vertex.new(-196, 0, -104, 2, 2, 22),
  [9] = Vertex.new(176, 0, 96, 72, 72, 78),
  [117] = Vertex.new(176, 0, 96, 74, 74, 81),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Effigy at the Tower of Life near fairy ring code DJP.",
    title = "Getting started",
    actions = {
      Action.Direction:new(2640, 1309, 3218, { distance = 12 }),
      Action.ModelHighlight:new(effigy, { distance = 12 }),
      Action.ConversationHighlight:new("Sure, why not."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Effigy.",
    actions = { Action.ModelHighlight:new(effigy) },
    postconditions = { Condition.ConversationText:new("Hold on and I'll see what I can do") },
  },
  {
    text = "Talk to Bonafido, the builder lying down north of the door to the tower.",
    actions = {
      Action.Direction:new(2650.5, 1389, 3227.5, { distance = 12 }),
      Action.ModelHighlight:new(bonafido, { distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("Count me in!") },
  },
  {
    text = "Talk to 'Black-eye'.",
    title = "Builder's outfit",
    neededItems = { ["Beer"] = { quantity = 1 } },
    actions = {
      Action.ModelHighlight:new(blackEye),
      Action.ConversationHighlight:new("Three"),
      Action.ConversationHighlight:new("Torn curtains"),
      Action.ConversationHighlight:new("10 clay pieces"),
    },
    postconditions = { Condition.InventoryContains:new(hardHat) },
  },
  {
    text = "Talk to 'The Guns' and give him the beer.",
    actions = { Action.ModelHighlight:new(theGuns) },
    postconditions = { Condition.InventoryContains:new(buildersShirt) },
  },
  {
    text = "Talk to 'No fingers'.",
    actions = { Action.ModelHighlight:new(noFingers) },
    postconditions = { Condition.ConversationText:new("Only real builders can wear") },
  },
  {
    text = "Pickpocket the boots from him. It may take multiple attempts.",
    actions = { Action.ModelHighlight:new(noFingers) },
    postconditions = { Condition.InventoryContains:new(buildersBoots) },
  },
  {
    text = "Search the plants south-east of the tower for the trousers.",
    actions = {
      Action.Direction:new(2655, 1389, 3224),
      Action.Direction:new(2658, 1053, 3226),
      Action.Direction:new(2654, 1349, 3226),
      Action.Direction:new(2655, 1405, 3212),
      Action.Direction:new(2654, 1405, 3210),
      Action.Direction:new(2645, 1325, 3209),
      Action.Direction:new(2644, 1269, 3211),
      Action.Direction:new(2643, 1205, 3209),
      Action.Direction:new(2643, 805, 3206),
      Action.Direction:new(2641, 1373, 3213),
    },
    postconditions = { Condition.InventoryContains:new(buildersTrousers) },
  },
  {
    text = "Equip the costume and talk to Bonafido.",
    actions = {
      Action.InventoryHighlight:new(hardHat),
      Action.InventoryHighlight:new(buildersTrousers),
      Action.InventoryHighlight:new(buildersShirt),
      Action.InventoryHighlight:new(buildersBoots),
      Action.ConversationHighlight:new("Tea"),
      Action.ConversationHighlight:new("Whistle for attention"),
      Action.ConversationHighlight:new("Your legs are getting a bit cold"),
      Action.ConversationHighlight:new("Carry on, it'll fix itself"),
    },
    postconditions = { Condition.ConversationText:new("Thanks!") },
  },
  {
    text = "Go into the tower.",
    title = "Tower repair",
    actions = { Action.Direction:new(2649, 1913, 3224.5) },
    postconditions = { Condition.DistanceTo:new(2649, 1413, 3223, 1) },
  },
  {
    text = "Search the crate for 4 valve wheels.",
    actions = { Action.Direction:new(2655, 1813, 3217) },
    postconditions = { Condition.InventoryContains:new(valveWheel, 4) },
  },
  {
    text = "Search the crate for 4 coloured balls.",
    actions = { Action.Direction:new(2644, 1813, 3216) },
    postconditions = { Condition.InventoryContains:new(colouredBall, 4) },
  },
  {
    text = "Search the crate for 3 metal sheets.",
    actions = { Action.Direction:new(2643, 1813, 3219) },
    postconditions = { Condition.InventoryContains:new(metalSheet, 3) },
  },
  {
    text = "Go to the 1st floor (2nd floor[US]).",
    actions = { Action.Direction:new(2645, 1913, 3219.4) },
    postconditions = { Condition.DistanceToWithHeight:new(2644, 2565, 3218, 4) },
  },
  {
    text = "Fix the pressure machine.",
    actions = { Action.Direction:new(2648.5, 3265, 3221.8) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.Direction:new(2648.5, 3265, 3221.8), Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Turn wheel 1 left until blocked.",
    postconditions = { Condition.ChatText:new("Pipe 1: The leak is blocked!") },
  },
  {
    text = "Turn wheel 1 right until fixed.",
    postconditions = { Condition.ChatText:new("Pipe 1: That pipe is now fixed.") },
  },
  {
    text = "Pull left lever, then turn wheel 2 left until blocked.",
    postconditions = { Condition.ChatText:new("Pipe 2: The leak is blocked!") },
  },
  {
    text = "Turn wheel 2 right until fixed.",
    postconditions = { Condition.ChatText:new("Pipe 2: That pipe is now fixed.") },
  },
  {
    text = "Turn wheel 3 right until it reaches the hole, then turn it left to block the hole.",
    postconditions = { Condition.ChatText:new("Pipe 3: The leak is blocked!") },
  },
  {
    text = "Turn wheel 3 right until fixed.",
    postconditions = { Condition.ChatText:new("Pipe 3: That pipe is now fixed.") },
  },
  {
    text = "Pull right lever, then turn wheel 4 right until it reaches the hole, then turn left to block the hole.",
    postconditions = { Condition.ChatText:new("Pipe 4: The leak is blocked!") },
  },
  {
    text = "Turn wheel 4 right until fixed.",
    postconditions = { Condition.ChatText:new("The machine is working!") },
  },
  {
    text = "Go back to the ground floor (1st floor [US]).",
    title = "Pipe machine",
    neededItems = {
      ["Rivets"] = { quantity = 6 },
      ["Pipe"] = { quantity = 4 },
      ["Pipe ring"] = { quantity = 5 },
    },
    actions = { Action.Direction:new(2644, 2465, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2646, 1413, 3220, 4) },
  },
  {
    text = "Search the crate for 6 rivets.",
    actions = { Action.Direction:new(2654, 1813, 3220) },
    postconditions = { Condition.InventoryContains:new(rivet, 6) },
  },
  {
    text = "Search the crate for 5 pipe rings.",
    actions = { Action.Direction:new(2652, 1813, 3222) },
    postconditions = { Condition.InventoryContains:new(pipeRing, 5) },
  },
  {
    text = "Search the crate for 4 pipes.<ul><li></li></ul>",
    actions = { Action.Direction:new(2648, 1813, 3222) },
    postconditions = { Condition.InventoryContains:new(pipe, 4) },
  },
  {
    text = "Go to the 2nd floor (3rd floor[US]).",
    actions = { Action.Direction:new(2645, 1913, 3219.4) },
    postconditions = { Condition.DistanceToWithHeight:new(2644, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2652.5, 2865, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2651, 3717, 3220, 4) },
  },
  {
    text = "Fix the pipe machine and move the pieces as shown in the image on the wiki (sorry can't track the pieces).",
    actions = {
      Action.Direction:new(2649.5, 4017, 3214),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ChatText:new("The machine is working!") },
  },
  {
    text = "Go back to the ground floor (1st floor [US]).",
    title = "The cage",
    neededItems = {
      ["Metal bar"] = { quantity = 5 },
      ["Binding fluid"] = { quantity = 4 },
    },
    actions = { Action.Direction:new(2652, 3517, 3220) },
    postconditions = { Condition.DistanceToWithHeight:new(2653, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2644, 2465, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2646, 1413, 3220, 4) },
  },
  {
    text = "Search the crate for 4 bottles of binding fluid.",
    actions = { Action.Direction:new(2651, 1813, 3213) },
    postconditions = { Condition.InventoryContains:new(bindingFluid, 4) },
  },
  {
    text = "Search the crate for 5 metal bars.",
    actions = { Action.Direction:new(2647, 1813, 3223) },
    postconditions = { Condition.InventoryContains:new(metalBar, 5) },
  },
  {
    text = "Go to the 3rd floor (4th floor[US]).",
    actions = { Action.Direction:new(2645, 1913, 3219.4) },
    postconditions = { Condition.DistanceToWithHeight:new(2644, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2652.5, 2865, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2651, 3717, 3220, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = { Condition.DistanceToWithHeight:new(2648, 4869, 3221, 4) },
  },
  {
    text = "Fix the cage using the instructions below (can't track this, sorry).",
    actions = {
      Action.ModelHighlight:new(cage),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("need to get the sizes correct") },
  },
  {
    text = "Front side<ul><li>Vertical 2</li><li>Horizontal 3</li><li>Horizontal 2</li><li>Turn right</li></ul>",
  },
  {
    text = "Right side<ul><li>Vertical 4</li><li>Vertical 2</li><li>Horizontal 2</li><li>Turn right</li></ul>",
  },
  {
    text = "Rear side<ul><li>Horizontal 4</li><li>Vertical 2</li><li>Vertical 3</li><li>Turn right</li></ul>",
  },
  {
    text = "Left side<ul><li>Horizontal 2</li><li>Horizontal 2</li><li>Vertical 2</li></ul>",
    postconditions = {
      Condition.ChatText:new("It's complete!"),
      Condition.ChatText:new("The cage is complete!"),
    },
  },
  {
    text = "Return to the ground floor (1st floor [US]).",
    title = "Homunculus",
    actions = { Action.Direction:new(2647.4, 4969, 3221) },
    postconditions = { Condition.DistanceToWithHeight:new(2646, 3717, 3221, 4) },
  },
  {
    actions = { Action.Direction:new(2652, 3517, 3220) },
    postconditions = { Condition.DistanceToWithHeight:new(2653, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2644, 2465, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2646, 1413, 3220, 4) },
  },
  {
    text = "Talk to Effigy outside of the tower.",
    actions = { Action.ModelHighlight:new(effigy) },
    postconditions = {
      Condition.ConversationText:new("Why does nobody"),
      Condition.ConversationText:new("Best I follow them"),
    },
  },
  {
    text = "Go to the top of the tower to start a cutscene.",
    actions = { Action.Direction:new(2645, 1913, 3219.4) },
    postconditions = { Condition.DistanceToWithHeight:new(2644, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2652.5, 2865, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2651, 3717, 3220, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = { Condition.InInstance:new() },
  },
  { postconditions = { Condition.ConversationText:new("I must go confront those alchemists") } },
  {
    text = "Return to the ground floor (1st floor [US]).",
    actions = { Action.Direction:new(2652, 3517, 3220) },
    postconditions = { Condition.DistanceToWithHeight:new(2653, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2644, 2465, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2646, 1413, 3220, 4) },
  },
  {
    text = "Talk to Effigy outside of the tower.",
    actions = { Action.ModelHighlight:new(effigy) },
    postconditions = {
      Condition.ConversationText:new("My mum did"),
    },
  },
  {
    text = "Return to the top of the tower.",
    actions = { Action.Direction:new(2645, 1913, 3219.4) },
    postconditions = { Condition.DistanceToWithHeight:new(2644, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2652.5, 2865, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2651, 3717, 3220, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to the Homunculus.",
    actions = {
      Action.ModelHighlight:new(homunculusInCage),
      Action.ConversationHighlight:new("Get some logs and a tinderbox."),
      Action.ConversationHighlight:new("By ignition of gas in their belly as they exhale."),
      Action.ConversationHighlight:new("Fletching, Crafting, Smithing."),
      Action.ConversationHighlight:new("Bury them."),
      Action.ConversationHighlight:new("Run, run as fast as you can."),
      Action.ConversationHighlight:new("People mix together ingredients in vials. The nutrients will help you."),
      Action.ConversationHighlight:new("Take a rune stone to an altar and use a talisman."),
    },
    postconditions = { Condition.ConversationText:new("I surprise right moment") },
  },
  {
    text = "Return to the ground floor (1st floor [US]).",
    title = "Finishing up",
    actions = { Action.Direction:new(2647.4, 4969, 3221) },
    postconditions = { Condition.DistanceToWithHeight:new(2646, 3717, 3221, 4) },
  },
  {
    actions = { Action.Direction:new(2652, 3517, 3220) },
    postconditions = { Condition.DistanceToWithHeight:new(2653, 2565, 3218, 4) },
  },
  {
    actions = { Action.Direction:new(2644, 2465, 3219) },
    postconditions = { Condition.DistanceToWithHeight:new(2646, 1413, 3220, 4) },
  },
  {
    text = "Talk to Effigy for a final cutscene.",
    actions = { Action.ModelHighlight:new(effigy) },
    postconditions = {
      Condition.ConversationText:new("Me look dungeon"),
    },
  },
  {
    text = "Go down the trapdoor inside the tower.",
    actions = { Action.Direction:new(2647, 1413, 3213) },
    postconditions = { Condition.DistanceTo:new(3038, 965, 4376, 4) },
  },
  {
    text = "Talk to Homunculus.",
    actions = { Action.ModelHighlight:new(homunculus) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Tower of Life",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1171843200,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Construction", 10) },
  neededItems = { ["Beer"] = { quantity = 1, model = Models.items["beer"] } },
  recommendedItems = {
    ["Cowhide"] = { quantity = 1 },
    ["Unicorn horn"] = { quantity = 1 },
    ["Raw swordfish"] = { quantity = 1 },
    ["Raw chicken"] = { quantity = 1 },
    ["Raw cave eel"] = { quantity = 1 },
    ["Giant frog legs"] = { quantity = 1 },
  },
  combatNPCs = {},
})
