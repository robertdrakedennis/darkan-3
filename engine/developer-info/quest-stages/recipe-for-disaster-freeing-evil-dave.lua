local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Items
local anySpice = Model.new(588, {
  [536] = Vertex.new(4, 160, 36, 37, 34, 34),
})
local stew = Model.new(324, {
  [182] = Vertex.new(52, 64, 92, 110, 79, 9),
  [200] = Vertex.new(52, 64, 92, 110, 79, 9),
  [204] = Vertex.new(52, 64, 92, 110, 79, 9),
  [209] = Vertex.new(-52, 64, 92, 110, 79, 9),
  [212] = Vertex.new(-52, 64, 92, 110, 79, 9),
})
local spicyStew = Model.new(303, {
  [3] = Vertex.new(44, 72, 80, 90, 50, 8),
  [88] = Vertex.new(44, 72, 80, 110, 79, 9),
  [91] = Vertex.new(44, 72, 80, 110, 79, 9),
  [99] = Vertex.new(44, 72, 80, 110, 79, 9),
  [102] = Vertex.new(96, 72, 0, 110, 79, 9),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Interact with Evil Dave to start the miniquest.",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    title = "Freeing Evil Dave",
    neededItems = {
      ["Any cat but overgrown"] = { quantity = 1 },
      ["Stew"] = { quantity = 15 },
    },
    recommendedItems = { ["Underworld Grimoire"] = { quantity = 1 } },
    actions = { Action.Direction:new(-4, 600, 0, { instance = true }) },
    postconditions = { Condition.ConversationText:new("kaaaaay") },
  },
  {
    text = "Talk to Doris west of the Edgeville bank.",
    actions = {
      Action.Direction:new(3079, 1669, 3493, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["doris"], { distance = 6 }),
      Action.ConversationHighlight:new("Is Dave in?"),
      Action.ConversationHighlight:new("Yes, I'm evil!"),
    },
    postconditions = { Condition.ConversationText:new("too much mess") },
  },
  {
    text = "Climb down the trapdoor.",
    actions = { Action.Direction:new(3077, 1669, 3493, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3077, 3045, 9893, 4) },
  },
  {
    text = "Talk to Evil Dave.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["evil dave"]),
      Action.ConversationHighlight:new("General chat"),
      Action.ConversationHighlight:new("What did you eat at the secret council meeting?"),
      Action.ConversationHighlight:new("You've got to tell me because the magic requires it!"),
    },
    postconditions = { Condition.ConversationText:new("talk to your mum") },
  },
  {
    text = "Climb the cellar stairs.",
    actions = { Action.Direction:new(3076, 3645, 9893) },
    postconditions = { Condition.DistanceTo:new(3078, 1669, 3493, 4) },
  },
  {
    text = "Talk to Doris.",
    actions = { Action.ModelHighlight:new(Models.npcs["doris"]) },
    postconditions = { Condition.ConversationText:new("kill some") },
  },
  {
    text = "Head back in the basement.",
    actions = { Action.Direction:new(3077, 1669, 3493, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3077, 3045, 9893, 4) },
  },
  {
    text = "Interact with your cat and select 'Chase-Vermin'.<ul><li>Switch off 'Hide Familiar Options' in Gameplay->Combat & Action Bar-> 'Choose Option' Menu.</li></ul>",
    actions = {
      Action.ModelHighlight:new(Models.npcs["any cat"]),
      Action.InventoryHighlight:new(Models.npcs["any cat"]),
      Action.ConversationHighlight:new("Chase-Vermin"),
    },
    postconditions = { Condition.ModelVisible:new(anySpice) },
  },
  {
    text = "Pick up the spices that drop.",
    actions = { Action.ModelHighlight:new(anySpice) },
    postconditions = { Condition.InventoryContains:new(anySpice) },
  },
  {
    text = "Make Dave's Evil stew by combining doses of each spice on the stews.",
    actions = {
      Action.InventoryHighlight:new(anySpice),
      Action.InventoryHighlight:new(stew),
    },
    postconditions = { Condition.InventoryContains:new(spicyStew) },
  },
  {
    text = "Use the spicy stew on Evil Dave.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["evil dave"]),
      Action.InventoryHighlight:new(spicyStew),
    },
    postconditions = { Condition.ConversationText:new("taste this stew") },
  },
  {
    text = "Test one spice at a time.<ul><li>Use the table on the wiki to track the correct spice dosage.</li></ul>",
    actions = { Action.ConversationHighlight:new("Chase-Vermin") },
    postconditions = { Condition.ConversationText:new("So confused") },
  },
  {
    text = "Head to Lumbridge and give Evil Dave the stew.",
    actions = { Action.Direction:new(3212.5, 2077, 3221.5) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    actions = {
      Action.Direction:new(-4, 600, 0, { instance = true }),
      Action.InventoryHighlight:new(spicyStew, true),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing Evil Dave",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest", "Gertrude's Cat", "Shadow of the Storm" },
  questReqs = {
    Types.QuestReq.skill("Cooking", 25),
  },
  neededItems = {
    ["Any cat but overgrown"] = { quantity = 1, model = Models.items["any cat"] },
    ["Stew"] = { quantity = 15, model = Models.items["beer"] },
  },
  recommendedItems = { ["Underworld Grimoire"] = { quantity = 1 } },
  combatNPCs = {},
})
