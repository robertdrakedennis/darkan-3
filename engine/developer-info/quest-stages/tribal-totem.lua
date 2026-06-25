local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local kangaiMau = Model.new(3135, {
  [2380] = Vertex.new(-2, 725, -59, 41, 29, 3),
  [2385] = Vertex.new(-7, 724, -51, 41, 29, 3),
  [2388] = Vertex.new(2, 725, -59, 41, 29, 3),
  [2390] = Vertex.new(7, 724, -51, 41, 29, 3),
  [2647] = Vertex.new(0, 735, -7, 28, 139, 127),
})
--#endregion
--#region Quest Items
local addressLabel = Model.new(72, {
  [3] = Vertex.new(-64, 0, -96, 95, 88, 87),
  [5] = Vertex.new(32, 0, 96, 95, 88, 87),
  [15] = Vertex.new(-64, 0, 48, 86, 86, 54),
  [17] = Vertex.new(-24, 0, 96, 86, 86, 54),
  [47] = Vertex.new(-40, 0, 80, 0, 0, 0),
})
local tribalTotem = Model.new(168, {
  [99] = Vertex.new(96, 144, 8, 69, 48, 21),
  [107] = Vertex.new(20, 168, -8, 69, 48, 21),
  [111] = Vertex.new(96, 144, 8, 69, 48, 21),
  [123] = Vertex.new(-20, 168, 8, 69, 48, 21),
  [131] = Vertex.new(-96, 144, -8, 69, 48, 21),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Kangai Mau in the large building north of Brimhaven's pub.",
    title = "Walkthrough",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Karamja lodestone",
      url = "Karamja_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2793, 1829, 3183, { distance = 5 }),
      Action.ModelHighlight:new(kangaiMau, { distance = 8 }),
      Action.ConversationHighlight:new("I'm in search of adventure!"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Kangai Mau.",
    actions = { Action.ModelHighlight:new(kangaiMau) },
    postconditions = { Condition.ConversationText:new("You tell me") },
  },
  {
    text = "Investigate the marked crate at the RPDT depot, south of the eastern bank.<ul><li>Take a ship from Brimhaven to complete the easy Karmaja achievement <i>Avast Ardougne!</i>.</li></ul>",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2650, 1685, 3271) },
    postconditions = { Condition.InventoryContains:new(addressLabel) },
  },
  {
    text = "Use the address label on the stack of two crates.",
    actions = {
      Action.Direction:new(2650, 2000, 3272),
      Action.InventoryHighlight:new(addressLabel),
    },
    postconditions = { Condition.ConversationText:new("deliver it for me") },
  },
  {
    text = "Talk to an RPDT employee.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["rpdt employee"], { highlightPriority = "closest" }),
      Action.ConversationHighlight:new("So, when are you going to deliver this crate?"),
    },
    postconditions = { Condition.ConversationText:new("I guess we could") },
  },
  {
    text = "Talk to Wizard Cromperty in north-east of the Ardougne.",
    actions = {
      Action.Direction:new(2683, 1285, 3324, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["wizard cromperty"], { distance = 8 }),
      Action.ConversationHighlight:new("So what have you invented?"),
      Action.ConversationHighlight:new("Can I be teleported please?"),
      Action.ConversationHighlight:new("Yes, that sounds good. Teleport me!"),
    },
    postconditions = { Condition.ConversationText:new("Okey dokey") },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["wizard cromperty"]) },
    postconditions = { Condition.DistanceFrom:new(2681, 1285, 3325, 10) },
  },
  {
    text = "Open the western door, using the code 'KURT' to get in.",
    actions = { Action.Direction:new(2632.5, 2045, 3322) },
    postconditions = { Condition.DistanceTo:new(2631, 1445, 3322, 1) },
  },
  {
    text = "Right-click investigate the stairs.",
    actions = { Action.Direction:new(2631, 2045, 3324.5) },
    postconditions = { Condition.ConversationText:new("senses") },
  },
  {
    text = "Climb up the stairs.",
    actions = { Action.Direction:new(2631, 2045, 3324.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2629, 2405, 3324, 4) },
  },
  {
    text = "Search the chest in the next room.",
    actions = { Action.Direction:new(2636, 2805, 3324) },
    postconditions = { Condition.InventoryContains:new(tribalTotem) },
  },
  {
    text = "Talk to Kangai Mau in Brimhaven with the totem.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Karamja lodestone",
      url = "Karamja_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(2793, 1829, 3183, { distance = 5 }),
      Action.ModelHighlight:new(kangaiMau, { distance = 8 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Tribal Totem",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.short,
  releaseDate = 1020124800,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Thieving", 21) },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
