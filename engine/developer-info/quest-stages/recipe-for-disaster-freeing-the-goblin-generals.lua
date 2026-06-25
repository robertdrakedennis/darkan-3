local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local goblinCook = Model.new(4773, {
  [1051] = Vertex.new(21, 555, -109, 161, 159, 147),
  [1052] = Vertex.new(29, 569, -102, 161, 159, 147),
  [1054] = Vertex.new(-23, 555, -109, 161, 159, 147),
  [1055] = Vertex.new(-31, 553, -101, 161, 159, 147),
  [1056] = Vertex.new(-27, 566, -102, 161, 159, 147),
})
local dirtyGoblinCook = Model.new(4773, {
  [43] = Vertex.new(21, 555, -109, 161, 159, 147),
  [44] = Vertex.new(29, 569, -102, 161, 159, 147),
  [46] = Vertex.new(-23, 555, -109, 161, 159, 147),
  [47] = Vertex.new(-31, 553, -101, 161, 159, 147),
  [48] = Vertex.new(-27, 566, -102, 161, 159, 147),
})
--#endregion
--#region Items
local orange = Model.new(168, {
  [58] = Vertex.new(48, 52, -24, 152, 97, 13),
  [61] = Vertex.new(48, 52, -24, 152, 97, 13),
  [64] = Vertex.new(48, 52, 24, 152, 97, 13),
  [67] = Vertex.new(48, 52, 24, 152, 97, 13),
  [69] = Vertex.new(48, 52, -24, 152, 97, 13),
})
local orangeSlices = Model.new(375, {
  [320] = Vertex.new(-52, 0, 96, 152, 97, 13),
  [335] = Vertex.new(-52, 0, 96, 152, 97, 13),
  [341] = Vertex.new(-80, 0, 72, 152, 97, 13),
  [347] = Vertex.new(-80, 0, 72, 152, 97, 13),
  [357] = Vertex.new(-80, 0, 72, 152, 97, 13),
})
--#endregion
--#region Quest Items
local dyedOrangeSlices = Model.new(375, {
  [320] = Vertex.new(-52, 0, 96, 81, 74, 74),
  [335] = Vertex.new(-52, 0, 96, 81, 74, 74),
  [341] = Vertex.new(-80, 0, 72, 81, 74, 74),
  [347] = Vertex.new(-80, 0, 72, 81, 74, 74),
  [357] = Vertex.new(-80, 0, 72, 81, 74, 74),
})
local spicyMaggots = Model.new(108, {
  [3] = Vertex.new(-84, 0, -24, 105, 49, 9),
  [12] = Vertex.new(-84, 0, 8, 105, 49, 9),
  [24] = Vertex.new(-52, 0, 48, 105, 49, 9),
  [48] = Vertex.new(40, 0, -60, 105, 49, 9),
  [99] = Vertex.new(28, 0, 72, 105, 49, 9),
})
local soggyBread = Model.new(360, {
  [15] = Vertex.new(40, 24, -80, 92, 86, 70),
  [20] = Vertex.new(40, 24, -80, 92, 86, 70),
  [27] = Vertex.new(40, 24, -80, 92, 86, 70),
  [29] = Vertex.new(40, 24, -80, 92, 86, 70),
  [33] = Vertex.new(40, 24, -80, 92, 86, 70),
})
local slopOfCompromise = Model.new(264, {
  [170] = Vertex.new(36, 52, 36, 110, 99, 69),
  [177] = Vertex.new(36, 52, 36, 110, 99, 69),
  [179] = Vertex.new(36, 52, -36, 110, 99, 69),
  [182] = Vertex.new(36, 52, -36, 110, 99, 69),
  [188] = Vertex.new(36, 52, 36, 110, 99, 69),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Inspect either General Bentnoze or General Wartface. They are located in the Lumbridge Castle banquet room.<ul><li>If you've already inspected them, move to the next step.</li></ul>",
    title = "Freeing the Generals",
    actions = { Action.Direction:new(3212.5, 1477, 3221.5) },
    postconditions = { Condition.DistanceTo:new(-7, 673, 2, 8, true) },
  },
  {
    actions = { Action.Direction:new(-7, 673, 2, { instance = true }) },
    postconditions = {
      Condition.ConversationText:new("You should be able to find it just to the North of Falador"), --not tested
      Condition.ConversationText:new("I remember now..."),
    },
  },
  {
    text = "Head to Goblin Village and climb down the ladder in the eastern building.",
    actions = {
      Action.Direction:new(2961, 645, 3507),
    },
    postconditions = { Condition.DistanceTo:new(0, 321, 0, 8, true) },
  },
  {
    text = "Talk to Goblin Cook.",
    actions = {
      Action.ModelHighlight:new(goblinCook, { instance = true }),
      Action.ConversationHighlight:new("I need your help..."),
      Action.ConversationHighlight:new("What do you need? Maybe I can get it for you."),
    },
    postconditions = { Condition.ConversationText:new("Ok, I'll see what I can do.") },
  },
  {
    text = "Give him charcoal.",
    actions = {
      Action.InventoryHighlight:new(Models.items["charcoal"]),
      Action.ModelHighlight:new(goblinCook, { instance = true }),
      Action.ConversationHighlight:new("I've got the charcoal you were after."),
    },
    postconditions = {
      Condition.ConversationText:new("Must be moving. Follow me."),
    },
  },
  {
    text = "After the cutscene, talk to him again.",
    actions = {
      Action.ModelHighlight:new(dirtyGoblinCook, { instance = true }),
      Action.ConversationHighlight:new("I need your help..."),
    },
    postconditions = { Condition.ConversationText:new("Wonderful. Well, I guess I'll have to see what I can do.") },
  },
  {
    text = "Slice an orange into orange slices (not chunks).",
    actions = { Action.InventoryHighlight:new(orange) },
    postconditions = { Condition.InventoryContains:new(orangeSlices) },
  },
  {
    text = "Use any colour of dye except orange, yellow, red, or pink on the slices to create dyed orange.",
    actions = {
      Action.InventoryHighlight:new(Models.items["blue dye"]),
      Action.InventoryHighlight:new(Models.items["purple dye"]),
      Action.InventoryHighlight:new(Models.items["green dye"]),
      Action.InventoryHighlight:new(orangeSlices),
    },
    postconditions = {
      Condition.InventoryContains:new(dyedOrangeSlices),
    },
  },
  {
    text = "Use spice on fishing bait to create spicy maggots.",
    actions = {
      Action.InventoryHighlight:new(Models.items["spice"]),
      Action.InventoryHighlight:new(Models.items["fishing bait"]),
    },
    postconditions = {
      Condition.InventoryContains:new(spicyMaggots),
    },
  },
  {
    text = "Right click Use bread on bucket, bowl, or jug of water to create soggy bread.",
    actions = {
      Action.InventoryHighlight:new(Models.items["bread"]),
      Action.InventoryHighlight:new(Models.items["bowl of water"]),
      Action.InventoryHighlight:new(Models.items["jug of water"]),
      Action.InventoryHighlight:new(Models.items["bucket of water"]),
    },
    postconditions = {
      Condition.InventoryContains:new(soggyBread),
    },
  },
  {
    text = "Talk to Goblin Cook.",
    actions = {
      Action.ModelHighlight:new(dirtyGoblinCook, { instance = true }),
      Action.ConversationHighlight:new("I've got the ingredients we need..."),
    },
    postconditions = { Condition.InventoryContains:new(slopOfCompromise) },
  },
  {
    text = "Head back to the dining room in Lumbridge Castle.",
    actions = { Action.Direction:new(3212.5, 1477, 3221.5) },
    postconditions = { Condition.DistanceTo:new(-7, 673, 2, 8, true) },
  },
  {
    text = "Use the slop of compromise on either of the Generals.",
    actions = {
      Action.InventoryHighlight:new(slopOfCompromise),
      Action.Direction:new(-7, 673, 2, { instance = true }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing the Goblin Generals",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest", "Goblin Diplomacy" },
  neededItems = {
    ["Charcoal"] = { quantity = 1, model = Models.items["charcoal"] },
    ["Spice or gnome spice"] = { quantity = 1, model = Models.items["spice"] },
    ["Fishing bait"] = { quantity = 1, model = Models.items["fishing bait"] },
    ["Bowl of water/jug of water/bucket of water"] = { quantity = 1, model = Models.items["bowl of water"] },
    ["Bread"] = { quantity = 1, model = Models.items["bread"] },
    ["Orange"] = { quantity = 1, model = orange },
    ["Blue dye"] = { quantity = 1, model = Models.items["blue dye"] },
  },
})
