local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local zealot = Model.new(3756, {
  [2053] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2061] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2063] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3621] = Vertex.new(-65, 670, 50, 161, 148, 147),
  [3652] = Vertex.new(65, 670, 50, 161, 148, 147),
})
--#endregion
--#region Objects
local minecart = Model.new(510, {
  [45] = Vertex.new(-200, 112, 160, 43, 40, 39),
  [83] = Vertex.new(200, 112, -160, 43, 40, 39),
  [153] = Vertex.new(-168, 80, 176, 43, 40, 39),
  [305] = Vertex.new(224, 96, 144, 43, 40, 39),
  [321] = Vertex.new(-224, 96, -144, 43, 40, 39),
})
local pointsSettings = Model.new(1146, {
  [705] = Vertex.new(1312, 1693, 5232, 116, 107, 107),
  [722] = Vertex.new(1296, 1425, 5232, 116, 107, 107),
  [807] = Vertex.new(1376, 1291, 5232, 116, 107, 107),
  [993] = Vertex.new(1216, 1540, 5228, 185, 30, 16),
  [995] = Vertex.new(1216, 1501, 5228, 185, 30, 16),
})
local stairs = Model.any({
  Model.new(738, {
    [71] = Vertex.new(5440, 864, 3428, 49, 45, 45),
    [370] = Vertex.new(5632, 864, 2304, 49, 45, 45),
    [689] = Vertex.new(6656, 864, 3584, 49, 45, 45),
    [699] = Vertex.new(7168, 864, 3072, 49, 45, 45),
    [702] = Vertex.new(7168, 864, 3072, 49, 45, 45),
  }),
  Model.new(738, {
    [107] = Vertex.new(4096, 1100, 3272, 13, 12, 12),
    [370] = Vertex.new(3584, 1120, 3328, 49, 45, 45),
    [401] = Vertex.new(3584, 992, 3328, 49, 45, 45),
    [708] = Vertex.new(4096, 1120, 3584, 49, 45, 45),
    [714] = Vertex.new(4096, 1120, 3584, 49, 45, 45),
  }),
})
local innocentLookingKeyObj = Model.new(333, {
  [15] = Vertex.new(-28, 480, -36, 152, 120, 13),
  [33] = Vertex.new(36, 480, -104, 152, 120, 13),
  [105] = Vertex.new(0, 480, -48, 152, 120, 13),
  [321] = Vertex.new(-32, 480, 44, 152, 120, 13),
  [323] = Vertex.new(-32, 480, 44, 152, 120, 13),
})
local upstairs = Model.new(522, {
  [237] = Vertex.new(6144, 1314, 3836, 76, 83, 76),
  [374] = Vertex.new(6144, 2079, 3816, 69, 76, 70),
  [413] = Vertex.new(6144, 2203, 4096, 58, 63, 58),
  [465] = Vertex.new(6144, 291, 2048, 0, 0, 0),
  [521] = Vertex.new(6144, 2203, 4096, 0, 0, 0),
})
--#endregion
--#region Quest Items
local zealotsKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 123, 113, 112),
  [104] = Vertex.new(68, 16, 68, 123, 113, 112),
  [397] = Vertex.new(68, 16, 68, 123, 113, 112),
  [400] = Vertex.new(68, 16, 68, 123, 113, 112),
  [408] = Vertex.new(68, 16, 68, 123, 113, 112),
})
local glowingFungus = Model.new(186, {
  [124] = Vertex.new(24, 80, 88, 114, 122, 150),
  [127] = Vertex.new(24, 80, 88, 114, 122, 150),
  [132] = Vertex.new(-24, 80, 88, 114, 122, 150),
  [133] = Vertex.new(-24, 80, 88, 114, 122, 150),
  [141] = Vertex.new(-24, 80, 88, 114, 122, 150),
})
local innocentLookingKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 152, 120, 13),
  [104] = Vertex.new(68, 16, 68, 152, 120, 13),
  [397] = Vertex.new(68, 16, 68, 152, 120, 13),
  [400] = Vertex.new(68, 16, 68, 152, 120, 13),
  [408] = Vertex.new(68, 16, 68, 152, 120, 13),
})
--#endregion

local xButtonTexture =
  "\x6b\x2d\x28\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6d\x31\x2a\xff\x6d\x31\x2a\xff\x6b\x2d\x28\xff\xb3\x8c\x5d\xff\xcb\xab\x6f\xff\xb3\x8c\x5d\xff\x6b\x2d\x28\xff\x6b\x2d\x28\xff\x6d\x31\x2a\xff\x73\x32\x2b\xff\x73\x32\x2b\xff\x6b\x2d\x28\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Speak to the Zealot, north of the mine west of Burgh de Rott.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(3442, 1141, 3259) },
    postconditions = { Condition.ModelVisible:new(zealot) },
  },
  {
    actions = {
      Action.ModelHighlight:new(zealot),
      Action.ConversationHighlight:new("I follow the path of Saradomin."),
      Action.ConversationHighlight:new("I come seeking challenges and quests."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(zealot) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Zealot.",
    actions = {
      Action.ModelHighlight:new(zealot),
      Action.ConversationHighlight:new("into the mines"), --not tested
      Action.ConversationHighlight:new("borrow"),
    },
    postconditions = { Condition.ConversationText:new("too high") },
  },
  {
    text = "Pickpocket Zealot to get Zealot's key.",
    actions = { Action.ModelHighlight:new(zealot) },
    postconditions = { Condition.InventoryContains:new(zealotsKey) },
  },
  {
    text = "Climb over the cart to the south.",
    actions = { Action.Direction:new(3445.1, 2629, 3236) },
    postconditions = { Condition.DistanceTo:new(3443, 2597, 3236, 1) },
  },
  {
    text = "Crawl-down the cart tunnel.",
    actions = { Action.Direction:new(3440, 3585, 3232) },
    postconditions = { Condition.DistanceTo:new(3436, 1061, 9637, 4) },
  },
  {
    text = "Run directly west through the cave and crawl-through the cart tunnel.",
    title = "Navigating the cave",
    actions = { Action.Direction:new(3404, 1461, 9631) },
    postconditions = { Condition.DistanceTo:new(3429, 1925, 3233, 4) },
  },
  {
    text = "Re-enter the cave via the southern entrance.",
    actions = { Action.Direction:new(3429, 2225, 3225) },
    postconditions = { Condition.DistanceTo:new(3409, 1061, 9623, 4) },
  },
  {
    text = "Descend the nearby ladder.",
    actions = { Action.Direction:new(3422, 1061, 9625, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2783, 941, 4569, 4) },
  },
  {
    text = "Go down the ladder directly east.",
    actions = { Action.Direction:new(2798, 1157, 4567, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2733, 997, 4503, 4) },
  },
  {
    text = "Go down the south ladder.",
    warning = "Avoid the mine cart.",
    actions = {
      Action.Direction:new(2725, 2781, 4486, { tile = true }),
      Action.ModelHighlight:new(minecart),
    },
    postconditions = { Condition.DistanceTo:new(2789, 1189, 4487, 4) },
  },
  {
    text = "Pick a glowing fungus.",
    title = "Glowing mushrooms",
    actions = { Action.Direction:new(2781, 1197, 4488) },
    postconditions = { Condition.InventoryContains:new(glowingFungus) },
  },
  {
    text = "Use the glowing fungus on the mine cart.",
    actions = {
      Action.Direction:new(2778, 1021, 4506),
      Action.InventoryHighlight:new(glowingFungus),
    },
    postconditions = { Condition.ChatText:new("glowing fungus in the mine cart") },
  },
  {
    text = "Check points settings.",
    actions = { Action.ModelHighlight:new(pointsSettings) },
    postconditions = { Condition.Generic2DVisible:new(15, 15, 7, xButtonTexture) },
  },
  {
    text = "Pull the levers until your points settings matches the wiki. Click start on points settings.",
    warning = "If you fail, go back to the fungus step.",
    actions = {
      Action.Direction:new(2785, 757, 4516),
      Action.Direction:new(2769, 861, 4532),
    },
    postconditions = {
      Condition.ConversationText:new("cart is anywhere useful now"),
      Condition.ChatText:new("cart is anywhere useful now"),
    },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2789, 1589, 4486) },
    postconditions = { Condition.DistanceTo:new(2725, 2781, 4487, 4) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2734, 1405, 4503) },
    postconditions = { Condition.DistanceTo:new(2799, 1301, 4567, 4) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2782, 1341, 4569) },
    postconditions = { Condition.DistanceTo:new(3423, 1061, 9625, 4) },
  },
  {
    text = "Crawl through the cart tunnel.",
    actions = { Action.Direction:new(3408, 1461, 9623) },
    postconditions = { Condition.DistanceTo:new(3428, 1925, 3225, 4) },
  },
  {
    text = "Crawl through the cart tunnel.",
    actions = { Action.Direction:new(3429, 1925, 3233) },
    postconditions = { Condition.DistanceTo:new(3405, 1061, 9631, 4) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(3413, 1061, 9633, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2772, 1341, 4577, 4) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(2798, 941, 4599, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2734, 829, 4535, 4) },
  },
  {
    text = "Climb down the ladder to the south-west.",
    warning = "Avoid the mine carts.",
    actions = { Action.Direction:new(2710, 1125, 4540, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2774, 901, 4539, 4) },
  },
  {
    text = "Search the mine cart.",
    actions = {
      Action.Direction:new(2774, 1221, 4537),
      Action.ConversationHighlight:new("Take it."),
    },
    postconditions = { Condition.InventoryContains:new(glowingFungus) },
  },
  {
    text = "Climb up the ladder.",
    title = "Treus Dayth",
    neededItems = {
      ["Zealot's key"] = { quantity = 1, model = zealotsKey },
      ["Glowing fungus"] = { quantity = 1, model = glowingFungus },
    },
    recommendedItems = {},
    actions = { Action.Direction:new(2774, 1401, 4540) },
    postconditions = { Condition.DistanceTo:new(2711, 1157, 4540, 4) },
  },
  {
    text = "Climb down the ladder to the east.",
    warning = "Avoid the mine cart.",
    actions = { Action.Direction:new(2732, 725, 4529, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2795, 917, 4529, 4) },
  },
  {
    text = "Use the Zealot's key on water valve.",
    actions = {
      Action.Direction:new(2808, 1493, 4496),
      Action.InventoryHighlight:new(zealotsKey),
    },
    postconditions = { Condition.ChatText:new("You open the valve") },
  },
  {
    text = "Quickly, go down the lift.",
    actions = { Action.Direction:new(2807, 1093, 4493) },
    postconditions = { Condition.ChatText:new("flooded with water") },
  },
  {
    text = "Walk down the stairs to the south-east.",
    actions = { Action.ModelHighlight:new(stairs, { highlightPriority = "closest" }) },
    postconditions = { Condition.DistanceTo:new(2811, 1077, 4454, 4) },
  },
  {
    text = "Open the door.",
    actions = { Action.Direction:new(2798.5, 1525, 4453) },
    postconditions = { Condition.DistanceTo:new(2797, 1125, 4453, 1) },
  },
  {
    text = "Take the innocent looking key.",
    actions = { Action.ModelHighlight:new(innocentLookingKeyObj) },
    postconditions = { Condition.ConversationText:new("the") },
  },
  {
    text = "Kill Treus Dayth.",
    warning = "No tracking for this step.",
  },
  {
    text = "Pick up the key.<ul><li>Optional: Add it to the steel key ring if you have it.</li></ul>",
    actions = { Action.ModelHighlight:new(innocentLookingKeyObj) },
    postconditions = { Condition.InventoryContains:new(innocentLookingKey) },
  },
  {
    text = "Walk up the stairs to the east.",
    title = "Getting the Crystal",
    actions = { Action.ModelHighlight:new(upstairs) },
    postconditions = { Condition.DistanceTo:new(2750, 1013, 4437, 4) },
  },
  {
    text = "Walk down the stairs to the west.",
    actions = { Action.ModelHighlight:new(stairs, { atLocation = Location:new(2692.5, 1029, 4438) }) },
    postconditions = { Condition.DistanceTo:new(2758, 949, 4454, 4) },
  },
  {
    text = "Open the large door.",
    actions = { Action.Direction:new(2772.5, 1681, 4449.95) },
    postconditions = { Condition.DistanceTo:new(2772, 1381, 4448, 1) },
  },
  {
    text = "Cut a piece of crystal outcrop.<ul><li>If you want extra salve amulets you can mine multiple crystals.</li></ul>",
    actions = { Action.Direction:new(2789, 1429, 4428) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Haunted Mine",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1103587200,
  prereqQuests = { "Priest in Peril" },
  questReqs = {
    Types.QuestReq.skill("Agility", 15),
    Types.QuestReq.skill("Crafting", 35),
  },
  neededItems = {},
  recommendedItems = {
    ["Combat equipment and food"] = { quantity = 1 },
    ["Games necklace"] = { quantity = 1 },
    ["Nature Grotto"] = { quantity = 1 },
    ["Ball of wool"] = { quantity = 1 },
    ["Burgh de Rott"] = { quantity = 1 },
    ["Drakan's medallion"] = { quantity = 1 },
  },
  combatNPCs = { ["Treus Dayth"] = { level = "63", quantity = 1 } },
})
