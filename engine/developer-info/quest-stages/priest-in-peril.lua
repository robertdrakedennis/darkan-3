local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--NPCS
local cerberus = Model.new(1587, {
  [417] = Vertex.new(84, 640, -384, 66, 58, 50),
  [721] = Vertex.new(-84, 640, -384, 66, 58, 50),
  [815] = Vertex.new(24, 432, -444, 47, 42, 36),
  [965] = Vertex.new(24, 432, -444, 47, 42, 36),
  [1017] = Vertex.new(-24, 432, -444, 47, 42, 36),
})
local zamorakPriest = Model.new(3909, {
  [1447] = Vertex.new(-2, 725, -59, 170, 148, 130),
  [1452] = Vertex.new(-7, 724, -51, 170, 148, 130),
  [1454] = Vertex.new(7, 724, -51, 170, 148, 130),
  [1458] = Vertex.new(2, 725, -59, 170, 148, 130),
  [2977] = Vertex.new(0, 735, -7, 45, 228, 207),
})

--Quest Items
local goldenKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 132, 113, 11),
  [2] = Vertex.new(-12, 16, -84, 132, 113, 11),
  [3] = Vertex.new(-20, 16, -92, 132, 113, 11),
  [6] = Vertex.new(-60, 16, -52, 132, 113, 11),
  [7] = Vertex.new(-32, 16, -48, 132, 113, 11),
})
local ironKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 61, 56, 55),
  [2] = Vertex.new(-12, 16, -84, 61, 56, 55),
  [3] = Vertex.new(-20, 16, -92, 61, 56, 55),
  [6] = Vertex.new(-60, 16, -52, 61, 56, 55),
  [7] = Vertex.new(-32, 16, -48, 61, 56, 55),
})
local ironKey2 = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 63, 58, 58),
  [2] = Vertex.new(-12, 16, -84, 63, 58, 58),
  [3] = Vertex.new(-20, 16, -92, 63, 58, 58),
  [6] = Vertex.new(-60, 16, -52, 63, 58, 58),
  [7] = Vertex.new(-32, 16, -48, 63, 58, 58),
})
local bucketOfMurkeyWater = Model.new(570, {
  [2] = Vertex.new(62, 117, 50, 7, 45, 83),
  [23] = Vertex.new(-58, 117, -54, 7, 45, 83),
  [79] = Vertex.new(0, 20, -64, 93, 74, 38),
  [81] = Vertex.new(-48, 0, -44, 93, 74, 38),
  [98] = Vertex.new(52, 0, 40, 93, 74, 38),
  [99] = Vertex.new(68, 0, 0, 93, 74, 38),
  [107] = Vertex.new(0, 0, 60, 93, 74, 38),
  [117] = Vertex.new(-48, 0, 40, 93, 74, 38),
  [118] = Vertex.new(-48, 20, -44, 93, 74, 38),
  [123] = Vertex.new(-64, 0, 0, 93, 74, 38),
  [277] = Vertex.new(16, 164, 60, 93, 86, 85),
  [279] = Vertex.new(0, 140, 80, 93, 86, 85),
  [281] = Vertex.new(-60, 140, -56, 93, 86, 85),
  [286] = Vertex.new(-8, 200, -44, 84, 77, 77),
  [287] = Vertex.new(-8, 200, -36, 84, 77, 77),
  [319] = Vertex.new(-60, 104, 52, 93, 86, 85),
  [327] = Vertex.new(64, 104, 52, 93, 86, 85),
  [360] = Vertex.new(-24, 200, -40, 84, 77, 77),
  [372] = Vertex.new(36, 164, 52, 84, 77, 77),
  [374] = Vertex.new(64, 140, 52, 84, 77, 77),
})
local bucketOfBlessedWater = Model.new(570, {
  [2] = Vertex.new(62, 117, 50, 7, 45, 83),
  [3] = Vertex.new(81, 117, 0, 7, 45, 83),
  [4] = Vertex.new(62, 117, 50, 7, 45, 83),
  [6] = Vertex.new(0, 117, 74, 7, 45, 83),
  [7] = Vertex.new(62, 117, -54, 7, 45, 83),
  [14] = Vertex.new(62, 117, -54, 7, 45, 83),
  [18] = Vertex.new(-74, 117, 0, 7, 45, 83),
  [19] = Vertex.new(-58, 117, -54, 7, 45, 83),
  [46] = Vertex.new(-68, 140, -60, 93, 74, 38),
  [129] = Vertex.new(-60, 140, 52, 93, 74, 38),
  [141] = Vertex.new(68, 140, 60, 93, 74, 38),
  [153] = Vertex.new(64, 140, -52, 93, 74, 38),
  [155] = Vertex.new(56, 140, -60, 93, 74, 38),
  [286] = Vertex.new(-8, 200, -44, 84, 77, 77),
  [331] = Vertex.new(20, 196, 28, 84, 77, 77),
  [332] = Vertex.new(24, 200, 36, 84, 77, 77),
  [337] = Vertex.new(8, 200, 36, 93, 86, 85),
  [338] = Vertex.new(8, 196, 28, 93, 86, 85),
  [343] = Vertex.new(-28, 196, -32, 93, 86, 85),
  [344] = Vertex.new(-24, 200, -40, 93, 86, 85),
})

--Objects
local well = Model.new(33768, {
  [1206] = Vertex.new(3840, -23, 1743, 177, 177, 177),
  [20928] = Vertex.new(3855, 1807, 4249, 108, 106, 106),
  [20986] = Vertex.new(3886, 1806, 4274, 103, 102, 101),
  [21009] = Vertex.new(3886, 1806, 4274, 103, 102, 101),
  [22727] = Vertex.new(3825, 1807, 4249, 108, 106, 106),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to King Roald in Varrock Palace.",
    title = "A missing monk",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(3222, 1253, 3473, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["king roald"], { distance = 8 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to King Roald.",
    actions = { Action.ModelHighlight:new(Models.npcs["king roald"]) },
    postconditions = { Condition.ConversationText:new("Many thanks adventurer") },
  },
  {
    text = "Knock on the door at the temple in Silvarea, east of the earth altar.",
    title = "Dogsitting",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Archaeology journal teleport",
      url = "Archaeology_journal.png",
    },
    neededItems = { ["Combat gear"] = { quantity = 1 } },
    recommendedItems = { ["Archaeology journal"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3404, 4005, 3485.5),
      Action.ConversationHighlight:new("Knock at the door."),
      Action.ConversationHighlight:new("Roald sent me to check on Drezel."),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("As you wish") },
  },
  {
    text = "Go north and enter the mausoleum.",
    actions = { Action.Direction:new(3405, 2821, 3505) },
    postconditions = { Condition.ModelVisible:new(cerberus) },
  },
  {
    text = "Kill Cerberus.",
    actions = { Action.ModelHighlight:new(cerberus) },
    postconditions = { Condition.ModelNotVisible:new(cerberus) },
  },
  {
    text = "Return to King Roald.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Varrock lodestone",
      url = "Varrock_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3222, 1253, 3473, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["king roald"], { distance = 8 }),
      Action.ConversationHighlight:new("Talk about Priest in Peril."),
    },
    postconditions = { Condition.ConversationText:new("Y-yes your Highness.") },
  },
  {
    text = "Enter the Temple.",
    title = "Making things right",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Archaeology journal teleport",
      url = "Archaeology_journal.png",
    },
    neededItems = {
      ["Bucket"] = { quantity = 1 },
      ["Rune essence (unnoted)"] = { quantity = 50 },
      ["Pure essence (unnoted)"] = { quantity = 25 },
    },
    recommendedItems = { ["Archaeology journal"] = { quantity = 1 } },
    actions = { Action.Direction:new(3404, 4005, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3407, 4005, 3485.5, 2) },
  },
  {
    text = "Kill a Monk of Zamorak.",
    actions = { Action.ModelHighlight:new(zamorakPriest, { highlightPriority = "closest" }) },
    postconditions = { Condition.ModelVisible:new(goldenKey) },
  },
  {
    text = "Pick up the golden key",
    actions = { Action.ModelHighlight:new(goldenKey) },
    postconditions = { Condition.InventoryContains:new(goldenKey) },
  },
  {
    text = "Go outside and enter the Mausoleum to the north.",
    actions = { Action.Direction:new(3405, 2821, 3505) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Go through the gate and enter the monument room.",
    actions = { Action.Direction:new(0, 673, -11.5, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(0, 673, -14, 2, true) },
  },
  {
    actions = { Action.Direction:new(14, 673, -20, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(14, 673, -20, 8, true) },
  },
  {
    text = "<i>Study</i> the monuments and <i>use</i> the golden key on the one that has the iron key.",
    actions = {
      Action.InventoryHighlight:new(goldenKey),
      Action.Direction:new(12, 673, -16, { instance = true }),
      Action.Direction:new(14, 673, -12, { instance = true }),
      Action.Direction:new(18, 673, -10, { instance = true }),
      Action.Direction:new(22, 673, -12, { instance = true }),
      Action.Direction:new(24, 673, -16, { instance = true }),
      Action.Direction:new(22, 673, -20, { instance = true }),
      Action.Direction:new(18, 673, -22, { instance = true }),
    },
    postconditions = {
      Condition.InventoryContains:new(ironKey),
      Condition.InventoryContains:new(ironKey2),
    },
  },
  {
    text = "<i>Use</i> a bucket on the well in the centre of the monument room.",
    actions = {
      Action.Direction:new(9375, 5, 5074),
      Action.InventoryHighlight:new(Models.items["bucket"]),
    },
    postconditions = { Condition.InventoryContains:new(bucketOfMurkeyWater) },
  },
  {
    text = "Go back to the temple and climb to the top floor.",
    title = "Saradomin's blessing",
    neededItems = {
      ["Bucket of murky water"] = { quantity = 1, model = bucketOfMurkeyWater, duringQuest = true },
      ["Rune essence (unnoted)"] = { quantity = 50 },
      ["Pure essence (unnoted)"] = { quantity = 25 },
    },
    actions = { Action.Direction:new(0, 708, 0, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(3405, 2821, 3504, 4) },
  },
  {
    actions = { Action.Direction:new(3404, 3781, 3485) },
    postconditions = { Condition.DistanceTo:new(3408, 3781, 3485, 4) },
  },
  {
    actions = { Action.Direction:new(3414, 3781, 3481) },
    postconditions = { Condition.DistanceToWithHeight:new(3415, 6405, 3482, 3) },
  },
  {
    actions = { Action.Direction:new(3407, 6405, 3482) },
    postconditions = { Condition.DistanceToWithHeight:new(3407, 8261, 3484, 3) },
  },
  {
    text = "Open the cell door.",
    actions = {
      Action.InventoryHighlight:new(ironKey),
      Action.Direction:new(3412.5, 8261, 3484),
    },
    postconditions = { Condition.DistanceTo:new(3415, 8261, 3484, 2) },
  },
  {
    text = "Talk to Drezel.",
    actions = { Action.ModelHighlight:new(Models.npcs["drezel"]) },
    postconditions = { Condition.ConversationText:new("Yes! Great idea!") },
  },
  {
    text = "<i>Use</i> the bucket of blessed water on the Morytania coffin.",
    actions = {
      Action.Direction:new(3410, 8261, 3486),
      Action.InventoryHighlight:new(bucketOfBlessedWater),
    },
    postconditions = { Condition.ChatText:new("You pour the blessed water over the coffin...") },
  },
  {
    text = "Return to the mausoleum, enter the monument room, and speak with Drezel.",
    actions = { Action.Direction:new(3405, 2821, 3505) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    actions = { Action.Direction:new(0, 673, -11.5, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(0, 673, -14, 2, true) },
  },
  {
    actions = { Action.Direction:new(14, 673, -20, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(14, 673, -20, 8, true) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["drezel"]) },
    postconditions = { Condition.ConversationText:new("Well I have no knowledge of these ores") },
  },
  {
    text = "Talk to him again to give him 50 unnoted rune essence or 25 unnoted pure essence.<ul><li>If giving rune essence, it is advised to bring a wicked hood and withdraw the 50 essence from it.</li></ul>",
    actions = { Action.ModelHighlight:new(Models.npcs["drezel"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Priest in Peril",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1088467200,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Bucket"] = { quantity = 1, model = Models.items["bucket"] },
    ["Rune essence (unnoted)"] = { quantity = 50, model = Models.items["rune essence"] },
    ["Pure essence (unnoted)"] = { quantity = 25, model = Models.items["pure essence"] },
    ["Combat gear"] = { quantity = 1 },
  },
  recommendedItems = { ["Archaeology journal"] = { quantity = 1 } },
  combatNPCs = {
    ["Cerberus"] = { level = "33", quantity = 1 },
    ["Monk of Zamorak"] = { level = "23", quantity = 1 },
  },
})
