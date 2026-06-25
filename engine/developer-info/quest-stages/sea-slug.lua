local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local holgart = Model.new(4158, {
  [1721] = Vertex.new(-10, 705, -92, 54, 36, 10),
  [2272] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2277] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2280] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2282] = Vertex.new(7, 724, -51, 106, 78, 54),
})
--#endregion
--#region Objects
local badlyRepairedWall = Model.new(543, {
  [374] = Vertex.new(-176, 958, -256, 113, 102, 73),
  [375] = Vertex.new(-192, 958, -256, 113, 102, 73),
  [441] = Vertex.new(-240, 958, 256, 80, 64, 16),
  [467] = Vertex.new(-192, 958, -256, 80, 64, 16),
  [471] = Vertex.new(-240, 0, -256, 70, 55, 6),
})
local kickedWall = Model.new(429, {
  [284] = Vertex.new(-176, 958, -256, 113, 102, 73),
  [285] = Vertex.new(-192, 958, -256, 113, 102, 73),
  [327] = Vertex.new(-240, 958, 256, 80, 64, 16),
  [353] = Vertex.new(-192, 958, -256, 80, 64, 16),
  [360] = Vertex.new(-240, 0, -256, 70, 55, 6),
})
--#endregion
--#region Items
local unlitTorch = Model.new(228, {
  [139] = Vertex.new(-212, 12, -4, 71, 43, 15),
  [141] = Vertex.new(-212, 8, -8, 71, 43, 15),
  [145] = Vertex.new(-212, 12, -4, 71, 43, 15),
  [153] = Vertex.new(-212, 12, -4, 71, 43, 15),
  [223] = Vertex.new(-212, 8, -8, 71, 43, 15),
})
local brokenGlass = Model.new(156, {
  [30] = Vertex.new(-48, 1, 140, 222, 218, 218, 0.4118),
  [86] = Vertex.new(-48, 17, 140, 77, 91, 28, 0.4078),
  [90] = Vertex.new(-48, 17, 140, 77, 91, 28, 0.4078),
  [107] = Vertex.new(-48, 17, 140, 77, 91, 28, 0.5294),
  [131] = Vertex.new(-48, 17, 140, 222, 218, 218, 0.2941),
})
local litTorch = Model.new(324, {
  [289] = Vertex.new(204, 176, -4, 96, 38, 9),
  [292] = Vertex.new(208, 172, -8, 96, 38, 9),
  [297] = Vertex.new(204, 176, -12, 96, 38, 9),
  [298] = Vertex.new(204, 176, -12, 96, 38, 9),
  [300] = Vertex.new(208, 172, -8, 96, 38, 9),
})
--#endregion
--#region Quest Items
local dampSticks = Model.new(279, {
  [5] = Vertex.new(-124, 8, -72, 45, 38, 29),
  [115] = Vertex.new(120, 16, -76, 45, 38, 29),
  [118] = Vertex.new(120, 16, -76, 45, 38, 29),
  [129] = Vertex.new(120, 16, -76, 45, 38, 29),
  [185] = Vertex.new(120, 16, -76, 45, 38, 29),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Caroline in Witchaven east of Ardougne.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    neededItems = { ["Swamp paste"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(2716, 677, 3304, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["caroline"], { distance = 20 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Caroline.",
    actions = { Action.ModelHighlight:new(Models.npcs["caroline"]) },
    postconditions = { Condition.ConversationText:new("Kennith and my husband") },
  },
  {
    text = "Talk to Holgart.",
    actions = { Action.ModelHighlight:new(holgart) },
    postconditions = { Condition.ConversationText:new("Holgart the swamp paste") },
  },
  {
    text = "Talk to him again.",
    actions = {
      Action.ModelHighlight:new(holgart),
      Action.ConversationHighlight:new("Will you take me there?"),
    },
    postconditions = { Condition.DistanceTo:new(2782, 645, 3273, 4) },
  },
  {
    text = "Climb up the ladder to the north.",
    title = "The Fishing Platform",
    actions = { Action.Direction:new(2784, 1045, 3286) },
    postconditions = { Condition.DistanceToWithHeight:new(2783, 1605, 3287, 4) },
  },
  {
    text = "Talk to Kennith from the marked tile.",
    actions = {
      Action.Direction:new(2765, 1605, 3286, { tile = true }),
      Action.ModelHighlight:new(Models.npcs["kennith"]),
    },
    postconditions = { Condition.ConversationText:new("find your father") }, --not tested
  },
  {
    text = "Go back down the ladder.",
    actions = { Action.Direction:new(2784, 1605, 3285.8) },
    postconditions = { Condition.DistanceToWithHeight:new(2784, 645, 3287, 4) },
  },
  {
    text = "Talk to Holgart to leave the platform.",
    actions = { Action.ModelHighlight:new(holgart) },
    postconditions = { Condition.DistanceTo:new(2800, 157, 3320, 4) },
  },
  {
    text = "Talk to Kent. Wait for the pause.",
    actions = { Action.ModelHighlight:new(Models.npcs["kent"]) },
    postconditions = { Condition.ConversationText:new("Thanks Kent") },
  },
  {
    text = "Talk to Holgart to return to the platform.",
    actions = { Action.ModelHighlight:new(holgart) },
    postconditions = { Condition.DistanceTo:new(2782, 645, 3273, 4) },
  },
  {
    text = "Ttalk to Bailey.",
    title = "Helping Kennith",
    actions = {
      Action.Direction:new(2761.5, 645, 3275.5, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["bailey"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("light a thing") },
  },
  {
    text = "Pick up the broken glass.",
    actions = { Action.ModelHighlight:new(brokenGlass, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(brokenGlass) },
  },
  {
    text = "Pick up the damp sticks at the northern end of the platform.",
    actions = { Action.ModelHighlight:new(dampSticks, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(dampSticks) },
  },
  {
    text = "Use the broken glass on the damp sticks, then rub together the dry sticks.",
    actions = {
      Action.InventoryHighlight:new(brokenGlass),
      Action.InventoryHighlight:new(dampSticks),
      Action.InventoryHighlight:new(unlitTorch),
    },
    postconditions = { Condition.InventoryContains:new(litTorch) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(2784, 1045, 3286) },
    postconditions = { Condition.DistanceToWithHeight:new(2783, 1605, 3287, 4) },
  },
  {
    text = "Talk to Kennith from the marked tile.",
    actions = {
      Action.Direction:new(2765, 1605, 3286, { tile = true }),
      Action.ModelHighlight:new(Models.npcs["kennith"]),
    },
    postconditions = { Condition.ConversationText:new("get you out") }, --not tested
  },
  {
    text = "Kick the 'badly repaired wall'.",
    actions = { Action.ModelHighlight:new(badlyRepairedWall) },
    postconditions = { Condition.ModelVisible:new(kickedWall) },
  },
  {
    text = "Go back inside and talk to Kennith.",
    actions = {
      Action.Direction:new(2765, 1605, 3286, { tile = true }),
      Action.ModelHighlight:new(Models.npcs["kennith"]),
    },
    postconditions = { Condition.ConversationText:new("when you have") }, --not tested
  },
  {
    text = "Rotate the crane.",
    actions = { Action.Direction:new(2772, 2105, 3290.25) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    postconditions = {
      Condition.NotInInstance:new(),
      Condition.ConversationText:new("away to safety"), --not tested
    },
  },
  {
    text = "Go down the ladder.",
    title = "Finishing up",
    actions = { Action.Direction:new(2784, 1605, 3285.8) },
    postconditions = { Condition.DistanceToWithHeight:new(2784, 645, 3287, 4) },
  },
  {
    text = "Talk to Holgart.",
    actions = { Action.ModelHighlight:new(holgart) },
    postconditions = { Condition.DistanceTo:new(2722, 69, 3305, 4) },
  },
  {
    text = "Talk to Caroline.",
    actions = { Action.ModelHighlight:new(Models.npcs["caroline"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Sea Slug",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1031529600,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Firemaking", 30) },
  neededItems = {
    ["Swamp paste"] = { quantity = 1, model = Models.items["swamp paste"] },
    ["Fishing bait"] = { quantity = 1, model = Models.items["fishing bait"] },
  },
  recommendedItems = {},
  combatNPCs = {},
})
