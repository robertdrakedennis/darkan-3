local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local frizzy = Model.new(3765, {
  [769] = Vertex.new(-140, 212, -280, 118, 109, 108),
  [780] = Vertex.new(-52, 184, -280, 118, 109, 108),
  [3472] = Vertex.new(0, 440, -10, 15, 167, 42),
  [3473] = Vertex.new(2, 440, -12, 15, 167, 42),
  [3474] = Vertex.new(-2, 440, -12, 15, 167, 42),
})
local elstan = Model.new(5592, {
  [3382] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3387] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [3390] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3392] = Vertex.new(7, 724, -51, 108, 80, 56),
  [4968] = Vertex.new(29, 727, -29, 126, 81, 11),
})
local alain = Model.new(5445, {
  [1078] = Vertex.new(-2, 726, -59, 108, 80, 56),
  [1083] = Vertex.new(-7, 725, -51, 108, 80, 56),
  [1086] = Vertex.new(2, 726, -59, 108, 80, 56),
  [1088] = Vertex.new(7, 725, -51, 108, 80, 56),
  [1429] = Vertex.new(0, 735, -7, 28, 140, 128),
})
local dantaera = Model.new(4335, {
  [2805] = Vertex.new(24, 738, -38, 31, 30, 28),
  [2829] = Vertex.new(-24, 738, -38, 31, 30, 28),
  [2851] = Vertex.new(-2, 716, -57, 108, 80, 56),
  [2857] = Vertex.new(2, 716, -57, 108, 80, 56),
  [2861] = Vertex.new(6, 716, -52, 108, 80, 56),
})
local vanessa = Model.new(4197, {
  [2139] = Vertex.new(24, 738, -38, 31, 30, 28),
  [2163] = Vertex.new(-24, 738, -38, 31, 30, 28),
  [2185] = Vertex.new(-2, 716, -57, 108, 80, 56),
  [2191] = Vertex.new(2, 716, -57, 108, 80, 56),
  [2195] = Vertex.new(6, 716, -52, 108, 80, 56),
})
local ellena = Model.new(4749, {
  [3273] = Vertex.new(24, 738, -38, 31, 30, 28),
  [3297] = Vertex.new(-24, 738, -38, 31, 30, 28),
  [3319] = Vertex.new(-2, 716, -57, 108, 80, 56),
  [3325] = Vertex.new(2, 716, -57, 108, 80, 56),
  [3329] = Vertex.new(6, 716, -52, 108, 80, 56),
})
local zandarHorfyre = Model.new(14640, {
  [1393] = Vertex.new(-143, 375, -304, 128, 127, 127),
  [1394] = Vertex.new(-150, 359, -304, 128, 127, 127),
  [1396] = Vertex.new(-126, 382, -304, 128, 127, 127),
  [8203] = Vertex.new(-47, 651, -8, 127, 127, 127),
  [8215] = Vertex.new(47, 651, -8, 127, 127, 127),
})
local tanglefoot = Model.new(3210, {
  [1323] = Vertex.new(0, 1032, -196, 140, 130, 107),
  [1326] = Vertex.new(0, 1052, -40, 140, 130, 107),
  [1329] = Vertex.new(0, 1052, -40, 131, 122, 101),
  [1332] = Vertex.new(0, 1052, -40, 140, 130, 107),
  [1582] = Vertex.new(40, 960, -76, 170, 141, 108),
})
--#endregion
--#region Objects
local shadyGrove = Model.new(8835, {
  [6737] = Vertex.new(-636, 960, -664, 95, 91, 73),
  [6905] = Vertex.new(-732, 960, -520, 95, 91, 73),
  [6944] = Vertex.new(568, 1152, -704, 95, 91, 73),
  [7020] = Vertex.new(736, 960, -520, 95, 91, 73),
  [7064] = Vertex.new(436, 1084, -708, 95, 91, 73),
})
--#endregion
--#region Items
local magicSecteurs = Model.new(342, {
  [3] = Vertex.new(104, 8, 36, 53, 89, 37),
  [188] = Vertex.new(-112, 0, 12, 51, 86, 35),
  [208] = Vertex.new(-112, 0, 12, 51, 86, 35),
  [235] = Vertex.new(-112, 4, -20, 61, 128, 26),
  [266] = Vertex.new(-112, 4, -20, 61, 128, 26),
})
--#endregion
--#region Quest Items
local draynorSkull = Model.new(270, {
  [46] = Vertex.new(12, 28, -68, 150, 135, 96),
  [126] = Vertex.new(12, 28, -68, 150, 135, 96),
  [135] = Vertex.new(32, 28, -20, 150, 135, 96),
  [147] = Vertex.new(-24, 28, -20, 150, 135, 96),
  [196] = Vertex.new(32, 76, 20, 150, 135, 96),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Martin the Master Gardener in Draynor Village.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(3077, 1301, 3253, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["martin the master gardener"], { distance = 20 }),
      Action.ConversationHighlight:new("General Chat"),
      Action.ConversationHighlight:new("Anything I can help with?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Martin.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["martin the master gardener"]),
      Action.ConversationHighlight:new("Now that I think about it, you're right!"),
    },
    postconditions = { Condition.ConversationText:new("some of the other gardeners") },
  },
  {
    text = "Talk to Frizzy Skernip near the Port Sarim spirit tree patch.",
    title = "G.A.G",
    actions = {
      Action.Direction:new(3060, 965, 3256, { distance = 16 }),
      Action.ModelHighlight:new(frizzy, { distance = 16 }),
      Action.ConversationHighlight:new("Are you a member of the Group of Advanced Gardeners?"),
    },
    postconditions = { Condition.ConversationText:new("hard time solving our problems") }, --not tested
  },
  {
    text = "Talk to Elstan, north of the Falador cabbage field.",
    actions = {
      Action.Direction:new(3053, 1189, 3308, { distance = 20 }),
      Action.ModelHighlight:new(elstan, { distance = 20 }),
      Action.ConversationHighlight:new("Are you a member of the Group of Advanced Gardeners?"),
    },
    postconditions = { Condition.ConversationText:new("Insects") },
  },
  {
    text = "Talk to Alain, north of the Taverly lodestone.",
    actions = {
      Action.Direction:new(2885, 805, 3461, { distance = 20 }),
      Action.ModelHighlight:new(alain, { distance = 20 }),
      Action.ConversationHighlight:new("Are you a member of the Group of Advanced Gardeners?"),
    },
    postconditions = { Condition.ConversationText:new("I give up") },
  },
  {
    text = "Talk to Dantaera, north-west of the Catherby lodestone.",
    actions = {
      Action.Direction:new(2784, 2117, 3463, { distance = 20 }),
      Action.ModelHighlight:new(dantaera, { distance = 20 }),
      Action.ConversationHighlight:new("Are you a member of the Group of Advanced Gardeners?"),
    },
    postconditions = { Condition.ConversationText:new("root of our problems") },
  },
  {
    text = "Buy a pair of secateurs.",
    actions = { Action.ModelHighlight:new(vanessa) },
    postconditions = { Condition.InventoryContains:new(Models.items["secateurs"]) },
  },
  {
    text = "Talk to Ellena, easat of the Catherby lodestone.",
    actions = {
      Action.Direction:new(2860, 421, 3430, { distance = 20 }),
      Action.ModelHighlight:new(ellena, { distance = 20 }),
      Action.ConversationHighlight:new("Are you a member of the Group of Advanced Gardeners?"),
    },
    postconditions = { Condition.ConversationText:new("over what you said") },
  },
  {
    text = "Return to Martin.",
    actions = {
      Action.Direction:new(3077, 1301, 3253, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["martin the master gardener"], { distance = 20 }),
      Action.ConversationHighlight:new("Talk about farming problems."),
    },
    postconditions = { Condition.ConversationText:new("spoken to them before") },
  },
  {
    text = "Equip your dramen staff or lunar staff and head to the Lumbridge Swamp to enter the Lost City shed.",
    title = "Finding a cure",
    neededItems = {
      ["Dramen/lunar staff"] = { quantity = 1 },
      ["Secateurs"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3201.5, 1045, 3169),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
    postconditions = { Condition.DistanceTo:new(2452, 589, 4473, 4) },
  },
  {
    text = "Talk to the Fairy Godfather to the south. Make sure to finish the whole dialogue.",
    actions = {
      Action.Direction:new(2447, 645, 4426, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["fairy godfather"], { distance = 4 }),
      Action.ConversationHighlight:new("Where's the Fairy Queen?"),
    },
    postconditions = { Condition.ChatText:new("no longer paying attention") },
  },
  {
    text = "Talk to Fairy Nuff, north of the Zanaris bank.",
    actions = {
      Action.Direction:new(2387, 549, 4467, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["fairy nuff"], { distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("have much time left") },
  },
  {
    text = "Climb to the top of the Dark Wizards' Tower, west of Falador.<ul><li>Bring combat gear if lower level.</li></ul>",
    actions = { Action.Direction:new(2883, 3669, 3335.25) },
    postconditions = { Condition.DistanceToWithHeight:new(2884, 6117, 3336, 4) },
  },
  {
    actions = { Action.Direction:new(2883, 6717, 3336) },
    postconditions = { Condition.DistanceToWithHeight:new(2887, 9381, 3340, 4) },
  },
  {
    text = "Talk to Zandar Horfyre.",
    actions = {
      Action.Direction:new(2883, 10629, 3336, { distance = 3 }),
      Action.ModelHighlight:new(zandarHorfyre, { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("my regards to Nuff") },
  },
  {
    text = "Talk to Malignius Mortifer, north-west of the Port Sarim lodestone.<ul><li>Talking to Malignius at the Um ritual site will not work.</li></ul>",
    title = "Magic secateurs",
    neededItems = { ["Secateurs"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3001, 469, 3266, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["malignius mortifer"], { distance = 20 }),
      Action.ConversationHighlight:new("I need help with fighting a Tanglefoot."),
    },
    postconditions = { Condition.ConversationText:new("see you soon") },
  },
  {
    text = "Dig at the marked gravestone behind the Draynor Manor, north of the Draynor lodestone.",
    actions = { Action.Direction:new(3106, 1465, 3384) },
    postconditions = { Condition.InventoryContains:new(draynorSkull) },
  },
  {
    text = "Return to Malignius.",
    actions = {
      Action.Direction:new(3001, 469, 3266, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["malignius mortifer"], { distance = 20 }),
      Action.ConversationHighlight:new("I was asking you about fighting a Tanglefoot..."),
    },
    postconditions = { Condition.ConversationText:new("thanks very much") },
  },
  {
    text = "Fetch the items that Malignius requested.<ul><li>They will be listed in your quest journal.</li><li>Bring multiple of any edible item, or bring a filled druid pouch to ward off ghasts.</li><li>Manually move to the next step once you have your items.</li></ul>",
  },
  {
    text = "Enter the Grotto tree in the Mort Myre Swamp.",
    actions = {
      Action.Direction:new(3440, 673, 3338, { distance = 20 }),
      Action.ModelHighlight:new(Models.objects["natures grotto entrance"], { distance = 20 }),
    },
    postconditions = { Condition.DistanceTo:new(2272, 1525, 5334, 5) },
  },
  {
    text = "Talk to the Nature Spirit to receive magic secateurs.",
    actions = { Action.ModelHighlight:new(Models.npcs["nature spirit"]) },
    postconditions = { Condition.InventoryContains:new(magicSecteurs) },
  },
  {
    text = "Return to Zanaris.<ul><li>You can use a wicked hood to teleport to the cosmic altar.</li></ul>",
    title = "The final fight",
    neededItems = {
      ["Dramen/lunar staff"] = { quantity = 1 },
      ["Magic secateurs"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3201.5, 1045, 3169),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
      Action.InventoryHighlight:new(Models.items["wicked hood"]),
    },
    postconditions = {
      Condition.DistanceTo:new(2452, 589, 4473, 4), --fairy ring near chicken shrine
      Condition.DistanceTo:new(2406, 1093, 4379, 6), --cosmic altar
    },
  },
  {
    text = "Enter the Shady Grove, west of the cosmic altar.",
    actions = {
      Action.Direction:new(2398.5, 509, 4379.5, { distance = 20 }),
      Action.ModelHighlight:new(shadyGrove, { distance = 20 }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Equip your magic secateurs and kill Tanglefoot.<ul><li>Off-hand weaponry will work here, but will only deal as much as the secateurs would.</li></ul>",
    actions = {
      Action.Direction:new(-21, 0, 11, { instance = true, distance = 20 }),
      Action.ModelHighlight:new(tanglefoot, { instance = true, distance = 20 }),
      Action.InventoryHighlight:new(magicSecteurs),
    },
    postconditions = { Condition.ModelVisible:new(Models.items["queen's secateurs"], { instance = true }) },
  },
  {
    text = "Take the Queen's secateurs (area loot will not work).",
    actions = { Action.ModelHighlight:new(Models.items["queen's secateurs"], { instance = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["queen's secateurs"]) },
  },
  {
    text = "Exit the Shady Grove.",
    title = "Finishing up",
    actions = { Action.Direction:new(1, 600, 0, { instance = true }) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Return to the Fairy Godfather.",
    actions = {
      Action.Direction:new(2447, 645, 4426, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["fairy godfather"], { distance = 4 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "A Fairy Tale I - Growing Pains",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1140998400,
  prereqQuests = { "Lost City", "Nature Spirit" },
  questReqs = {},
  neededItems = {
    ["Dramen/lunar staff"] = { quantity = 1, model = Models.items["dramen staff"] },
    ["Secateurs"] = { quantity = 1, model = Models.items["secateurs"], duringQuest = true },
    ["Three/four random items"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Tanglefoot"] = { level = "68", quantity = 1 },
  },
})
