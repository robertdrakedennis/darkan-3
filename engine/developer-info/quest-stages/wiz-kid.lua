local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local ben = Model.new(3156, {
  [2341] = Vertex.new(-31, 517, 15, 63, 43, 25),
  [2356] = Vertex.new(15, 534, 25, 63, 43, 25),
  [2359] = Vertex.new(15, 534, 25, 63, 43, 25),
  [2415] = Vertex.new(31, 517, 15, 63, 43, 25),
  [2511] = Vertex.new(15, 534, 25, 78, 53, 32),
})
--#endregion
--#region Objects
local stool = Model.new(1020, {
  [341] = Vertex.new(69, 199, -70, 127, 127, 127),
  [343] = Vertex.new(69, 199, -70, 127, 127, 127),
  [358] = Vertex.new(90, 199, 35, 127, 127, 127),
  [413] = Vertex.new(-68, 199, -70, 127, 127, 127),
  [899] = Vertex.new(69, 199, -70, 127, 127, 127),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local bunchOfFlowers = Model.new(600, {
  [102] = Vertex.new(36, 60, -80, 140, 101, 12),
  [105] = Vertex.new(-32, 60, -80, 140, 101, 12),
  [114] = Vertex.new(32, 40, -104, 140, 101, 12),
  [408] = Vertex.new(-12, 32, -124, 140, 101, 12),
  [417] = Vertex.new(-32, 44, -100, 140, 101, 12),
})
local crowBones = Model.new(504, {
  [74] = Vertex.new(-124, 20, -96, 162, 149, 149),
  [78] = Vertex.new(-124, 20, -96, 162, 149, 149),
  [81] = Vertex.new(-116, 4, -112, 162, 149, 149),
  [83] = Vertex.new(-124, 20, -96, 162, 149, 149),
  [378] = Vertex.new(-124, 20, -96, 162, 149, 149),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Ben at the Marigold Farm.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Havenhythe lodestone",
      url = "Visions_of_Havenhythe_icon.png?78f5e", --will fix later
    },
    actions = { Action.Direction:new(3562, 8489, 1480) },
    postconditions = { Condition.ModelVisible:new(ben) },
  },
  {
    actions = { Action.ModelHighlight:new(ben) },
    jumpconditions = { Condition.ModelNotVisible:new(ben) },
    jumpOffset = -1,
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Ben.",
    actions = {
      Action.ModelHighlight:new(ben),
      Action.ConversationHighlight:new("What could go wrong?"),
    },
    postconditions = { Condition.ConversationText:new("Thank you Mr Wizard") },
  },
  {
    text = "Talk to Ben again.",
    title = "Teleport spell",
    neededItems = {
      ["Air runes"] = { quantity = 8, model = Models.items["air rune"] },
      ["Water runes"] = { quantity = 6, model = Models.items["water rune"] },
      ["Earth runes"] = { quantity = 4, model = Models.items["earth rune"] },
      ["Nature runes"] = { quantity = 2, model = Models.items["nature rune"] },
      ["Law runes"] = { quantity = 4, model = Models.items["law rune"] },
    },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(ben),
      Action.ConversationHighlight:new("Yes, I brought enough runes for me too."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Ben yet again.",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("yards from here") },
  },
  { text = "Cast the Wendlewick teleport.", postconditions = { Condition.ChatText:new("WOAH") } },
  {
    text = "Talk to Ben (again).",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("coming from the windmill") },
  },
  {
    text = "Climb up the ladder to the top of the windmill.",
    actions = { Action.Direction:new(3, 1448, 21, { instance = true }) },
    postconditions = {
      Condition.DistanceToWithHeight:new(4, 2672, 21, 4, true),
      Condition.DistanceToWithHeight:new(4, 4080, 21, 4, true),
    },
  },
  {
    actions = { Action.Direction:new(3, 3072, 21, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(4, 4080, 21, 4, true) },
  },
  {
    text = "Talk to Ben.",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("race you") },
  },
  {
    text = "Climb down the ladder to the ground floor.",
    actions = { Action.Direction:new(3, 4080, 21, { instance = true }) },
    postconditions = {
      Condition.DistanceToWithHeight:new(4, 848, 21, 4, true),
      Condition.DistanceToWithHeight:new(4, 2672, 21, 4, true),
    },
  },
  {
    actions = { Action.Direction:new(3, 3072, 21, { instance = true }) },
    postconditions = { Condition.DistanceToWithHeight:new(4, 848, 21, 4, true) },
  },
  {
    text = "Talk to Ben.",
    title = "Telekinetic grab spell",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("prime example of") },
  },
  {
    text = "Take flowers from the kitchen.",
    actions = { Action.Direction:new(4.3, 1060, -10, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(bunchOfFlowers) },
  },
  {
    text = "Place the flowers on the stool near Ben.",
    actions = {
      Action.ModelHighlight:new(stool),
      Action.InventoryHighlight:new(bunchOfFlowers),
    },
    postconditions = {
      Condition.ModelVisible:new(bunchOfFlowers, { instance = true, atLocation = Location:new(8, 688, 8) }),
    },
  },
  {
    text = "Talk to Ben.",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("cast telekinetic grab for") },
  },
  {
    text = "Cast telekinetic grab on the flowers.",
    actions = { Action.ModelHighlight:new(bunchOfFlowers, { instanced = true, atLocation = Location:new(8, 688, 8) }) },
    postconditions = { Condition.ChatText:new("disappeared into the AIR") },
  },
  {
    text = "Talk to Ben.",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("back on the stool") },
  },
  {
    text = "Place the flowers on the stool again.",
    actions = {
      Action.ModelHighlight:new(stool),
      Action.InventoryHighlight:new(bunchOfFlowers),
    },
    postconditions = {
      Condition.ModelVisible:new(bunchOfFlowers, { instance = true, atLocation = Location:new(8, 688, 8) }),
    },
  },
  {
    text = "Talk to Ben.",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("on the training stool") },
  },
  {
    text = "Dig the dirt mound.",
    title = "Bones to bananas spell",
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(-14, 560, -11, { instance = true, tile = true }) },
    postconditions = { Condition.InventoryContains:new(crowBones, 2) },
  },
  {
    text = "Use the crow bones on the training stool.",
    actions = {
      Action.ModelHighlight:new(stool),
      Action.InventoryHighlight:new(bunchOfFlowers),
    },
    postconditions = {
      Condition.ModelVisible:new(crowBones, { instance = true, atLocation = Location:new(8, 688, 8) }),
    },
  },
  {
    text = "Talk to Ben.",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("for you, though") },
  },
  {
    text = "Cast bones to bananas.",
    actions = { Action.ModelHighlight:new(crowBones, { instanced = true, atLocation = Location:new(8, 688, 8) }) },
    postconditions = { Condition.ChatText:new("smell bananas") },
  },
  {
    text = "Talk to Ben again.",
    actions = { Action.ModelHighlight:new(ben, { instanced = true }) },
    postconditions = {
      Condition.ConversationText:new("assess him"),
      Condition.NotInInstance:new(),
    },
  },
  {
    text = "Talk to Archmage Sedridor on the 2nd floor of the Wizards' Tower.",
    title = "Wizards' Tower",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Runecrafting guild teleport",
      url = "Wicked_hood.png",
    },
    neededItems = {},
    recommendedItems = {
      ["Wicked hood"] = { quantity = 1 },
      ["Stardust"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3097, 16773, 3147) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["sedridor"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["sedridor"]),
      Action.ConversationHighlight:new("Talk about 'Wiz Kid'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["sedridor"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("talk to Wizard Isidor") },
  },
  {
    text = "Talk to WIzard Isidor on the floor below.",
    actions = { Action.Direction:new(3093, 8613, 3156) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["wizard isidor"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["wizard isidor"]),
      Action.ConversationHighlight:new("Talk about 'Wiz Kid'."),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["wizard isidor"]) },
    jumpOffset = -1,
    postconditions = { Condition.ConversationText:new("meet you there") },
  },
  {
    text = "Return to Ben.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Havenhythe lodestone",
      url = "Visions_of_Havenhythe_icon.png?78f5e", --will fix later
    },
    actions = { Action.Direction:new(3562, 8489, 1480) },
    postconditions = { Condition.ModelVisible:new(ben) },
  },
  {
    actions = {
      Action.ModelHighlight:new(ben),
      Action.ConversationHighlight:new("Yes"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(ben) },
    jumpOffset = -1,
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("all those spells") } }, --not tested
  {
    text = "Talk to Ben.",
    actions = { Action.ModelHighlight:new(ben) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Wiz Kid",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1774278419,
  prereqQuests = { "Visions of Havenhythe" },
  questReqs = {
    Types.QuestReq.skill("Magic", 33),
  },
  neededItems = {
    ["Air runes"] = { quantity = 8, model = Models.items["air rune"] },
    ["Water runes"] = { quantity = 6, model = Models.items["water rune"] },
    ["Earth runes"] = { quantity = 4, model = Models.items["earth rune"] },
    ["Nature runes"] = { quantity = 2, model = Models.items["nature rune"] },
    ["Law runes"] = { quantity = 4, model = Models.items["law rune"] },
  },
  recommendedItems = {
    ["Wicked hood"] = { quantity = 1 },
    ["Stardust"] = { quantity = 1 },
  },
  combatNPCs = {},
})
