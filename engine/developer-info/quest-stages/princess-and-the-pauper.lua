local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local strayDog = Model.new(41868, {
  [74] = Vertex.new(16, 270, -205, 127, 127, 127),
  [2365] = Vertex.new(-16, 270, -205, 127, 127, 127),
  [2494] = Vertex.new(-3, 258, -245, 127, 127, 127),
  [13139] = Vertex.new(-10, 260, -200, 127, 127, 127),
  [14797] = Vertex.new(10, 260, -200, 127, 127, 127),
})
--#endregion
--#region Quest Items
local lostSausage = Model.new(576, {
  [40] = Vertex.new(38, 8, -21, 127, 127, 127),
  [45] = Vertex.new(38, 8, -21, 127, 127, 127),
  [105] = Vertex.new(38, 8, -21, 127, 127, 127),
  [114] = Vertex.new(38, 8, -21, 127, 127, 127),
  [129] = Vertex.new(36, -1, -27, 127, 127, 127),
})
local gentlySmokedSalmon = Model.new(312, {
  [7] = Vertex.new(-48, -68, -100, 158, 94, 81),
  [11] = Vertex.new(-48, -68, -100, 158, 94, 81),
  [13] = Vertex.new(-48, -68, -100, 158, 94, 81),
  [16] = Vertex.new(-48, 68, -100, 158, 94, 81),
  [21] = Vertex.new(-48, 68, -100, 158, 94, 81),
})
local juciestRedberries = Model.new(378, {
  [210] = Vertex.new(-60, 8, 52, 4, 47, 5),
  [213] = Vertex.new(-60, 12, 52, 4, 47, 5),
  [363] = Vertex.new(-64, 8, -44, 4, 47, 5),
  [366] = Vertex.new(-40, 8, -60, 4, 47, 5),
  [369] = Vertex.new(-40, 12, -60, 4, 47, 5),
})
local finestDoogleLeaves = Model.new(402, {
  [34] = Vertex.new(-76, 4, 64, 7, 85, 10),
  [106] = Vertex.new(-72, 4, 76, 7, 85, 10),
  [128] = Vertex.new(-72, 4, 76, 5, 62, 7),
  [383] = Vertex.new(-56, 4, -24, 7, 85, 10),
  [387] = Vertex.new(-32, -4, -40, 7, 85, 10),
})
local uncookedDogsDinner = Model.new(324, {
  [182] = Vertex.new(52, 64, 92, 110, 79, 9),
  [200] = Vertex.new(52, 64, 92, 110, 79, 9),
  [204] = Vertex.new(52, 64, 92, 110, 79, 9),
  [209] = Vertex.new(-52, 64, 92, 110, 79, 9),
  [212] = Vertex.new(-52, 64, 92, 110, 79, 9),
})
--#endregion

local steps = {
  {
    text = "Talk to Rodney in the kitchen of Fort Forinthry.<ul><li>For the tracking to work properly, you need to have the chat visible, and game messages set to 'On' or 'Filtered', and timestamps on.</li></ul>",
    title = "Yet another mystery",
    actions = {
      Action.Direction:new(3315, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["rodney"], { distance = 4 }),
      Action.ConversationHighlight:new("Start 'Princess and the Pauper'."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Rodney.",
    actions = {
      Action.Direction:new(3315, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["rodney"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("Aster will know.") },
  },
  {
    text = "Talk to Aster at Fort Forinthry.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["aster"]),
      Action.ConversationHighlight:new("Talk about quests..."),
      Action.ConversationHighlight:new("Talk about 'Princess and the Pauper'."),
    },
    postconditions = { Condition.ConversationText:new("Let's see what Siv has to say about it, then.") },
  },
  {
    text = "Talk to Overseer Siv in the Command Centre",
    actions = {
      Action.Direction:new(3319, 485, 3540, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["overseer siv"], { distance = 4 }),
      Action.ConversationHighlight:new("Talk about quests."),
      Action.ConversationHighlight:new("Talk about 'Princess and the Pauper'."),
    },
    postconditions = { Condition.ConversationText:new("Let's have a search, then.") },
  },
  {
    text = "Go to the main entrance of the fort, then talk to Princess who will direct you to the first clue. Let the dialogue finish.",
    actions = { Action.Direction:new(3306, 589, 3534) },
    postconditions = { Condition.DistanceTo:new(3306, 589, 3534, 2) },
  },
  {
    actions = { Action.Direction:new(3306, 589, 3534), Action.ModelHighlight:new(Models.npcs["princess the dog"]) },
    postconditions = { Condition.ConversationText:new("This way!") },
  },
  {
    text = "Go to the kitchen at Jolly Boar Inn and track the lost sausage.",
    actions = { Action.Direction:new(3280, 965, 3498) },
    postconditions = { Condition.DistanceTo:new(3280, 965, 3504, 2) },
  },
  {
    actions = { Action.Direction:new(3283, 965, 3489), Action.InventoryHighlight:new(lostSausage) },
    postconditions = { Condition.ConversationText:new("In here!") },
  },
  { postconditions = { Condition.ConversationText:new("We'll head outside and take a look.") } },
  {
    text = "Head north to the Wilderness Wall just outside the inn and talk to Princess for a half-eaten meat pie.",
    actions = { Action.Direction:new(3275, 861, 3520) },
    postconditions = { Condition.DistanceTo:new(3275, 861, 3520, 2) },
  },
  {
    actions = { Action.Direction:new(3275, 861, 3520), Action.ModelHighlight:new(Models.npcs["princess the dog"]) },
    postconditions = { Condition.ConversationText:new("This way!") },
  },
  {
    text = "Got to the small cabbage patch just north-east of the Varrock lodestone and talk to Princess.",
    actions = { Action.Direction:new(3227, 1125, 3385) },
    postconditions = { Condition.DistanceTo:new(3227, 1125, 3385, 2) },
  },
  {
    actions = { Action.Direction:new(3227, 1125, 3385), Action.ModelHighlight:new(Models.npcs["princess the dog"]) },
    postconditions = { Condition.ConversationText:new("In here!") },
  },
  {
    text = "After the cutscene, talk to Princess again.",
    actions = { Action.ModelHighlight:new(Models.npcs["princess the dog"]) },
    postconditions = { Condition.ConversationText:new("All together, makes a Princess special!") },
  },
  {
    text = "Talk to Rodney at Fort Forinthry",
    title = "Preparing a meal",
    neededItems = { ["Bowl of water"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3315, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["rodney"], { distance = 4 }),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("The smoked fish."),
    },
    postconditions = { Condition.ConversationText:new("I prefer to use salmon.") },
  },
  {
    actions = {
      Action.Direction:new(3315, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["rodney"], { distance = 4 }),
      Action.ConversationHighlight:new("The tender meat."),
    },
    postconditions = { Condition.ConversationText:new("Any meat will do") },
  },
  {
    actions = {
      Action.Direction:new(3315, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["rodney"], { distance = 4 }),
      Action.ConversationHighlight:new("The red berries."),
    },
    postconditions = { Condition.ConversationText:new("Ah, yes, redberries") },
  },
  {
    actions = {
      Action.Direction:new(3315, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["rodney"], { distance = 4 }),
      Action.ConversationHighlight:new("The secret ingredient."),
    },
    postconditions = { Condition.ConversationText:new("Ask Gertrude") },
  },
  {
    text = "Use a hammer on some raw meat to make tenderised meat.",
    actions = {
      Action.InventoryHighlight:new(Models.items["hammer"]),
      Action.InventoryHighlight:new(Models.items["raw beef"]),
    },
    postconditions = { Condition.ChatText:new("You tenderise the meat.") },
  },
  {
    text = "Use the salmon on a range and make smoked salmon.",
    actions = {
      Action.ModelHighlight:new(Models.objects["forinthry kitchen range"]),
      Action.InventoryHighlight:new(Models.items["raw salmon"]),
      Action.ConversationHighlight:new("Smoked salmon."),
    },
    postconditions = { Condition.InventoryContains:new(gentlySmokedSalmon) }, --can be tricked with cooked salmon
  },
  {
    text = "Pick the redberry bush east of Varrock lodestone.",
    actions = {
      Action.Direction:new(3272, 1045, 3372),
      Action.ConversationHighlight:new("The juciest redberries."),
    },
    postconditions = { Condition.InventoryContains:new(juciestRedberries) },
  },
  {
    text = "Talk to Gertrude in her house and ask about the secret ingredient.",
    actions = {
      Action.Direction:new(3151, 965, 3410, { distance = 8 }),
      Action.ModelHighlight:new(Models.npcs["gertrude"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about Rodney's secret ingredient."),
    },
    postconditions = { Condition.InventoryContains:new(finestDoogleLeaves) },
  },
  {
    text = "Use a bowl of water on an ingredient to make an uncooked dog's dinner.",
    actions = {
      Action.InventoryHighlight:new(Models.items["bowl of water"]),
      Action.InventoryHighlight:new(juciestRedberries),
    },
    postconditions = { Condition.InventoryContains:new(uncookedDogsDinner) },
  },
  {
    text = "Cook the uncooked dog's dinner at a range, then use the doogle leaves on the cooked dinner.<ul><li>Be careful not to eat it.</li></ul>", --needs testing
    actions = {
      Action.Direction:new(3156.5, 965, 3410),
      Action.InventoryHighlight:new(uncookedDogsDinner),
      Action.InventoryHighlight:new(finestDoogleLeaves),
    },
    postconditions = { Condition.ChatText:new("You add some seasoning to the dinner.") },
  },
  {
    text = "Talk to Princess in the kitchen of Fort Forinthry.",
    actions = {
      Action.Direction:new(3314, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["princess the dog"], { distance = 4 }),
    },
    postconditions = { Condition.DistanceTo:new(3314, 1189, 3569, 1) }, --needs better handling
  },
  {
    text = "Go to the kitchen in Jolly Boar Inn and talk to Princess.",
    actions = {
      Action.Direction:new(3284, 965, 3489),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.DistanceTo:new(3284, 965, 3489, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess the dog"]),
      Action.ConversationHighlight:new("Continue 'Princess and the Pauper.'"),
    },
    postconditions = { Condition.ConversationText:new("You again.") },
  },
  {
    postconditions = { Condition.ConversationText:new("Go on, boy - get out of here.") },
  },
  {
    text = "Talk to princess.",
    actions = { Action.ModelHighlight:new(Models.npcs["princess the dog"]) },
    postconditions = { Condition.ConversationText:new("Outside!") },
  },
  {
    text = "Talk to the stray dog outside the inn.",
    actions = {
      Action.Direction:new(3279, 613, 3520, { distance = 10 }),
      Action.ModelHighlight:new(strayDog, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("Now let's get you two home.") },
  },
  {
    text = "Talk to Rodney at Fort Forinthry.",
    actions = {
      Action.Direction:new(3315, 1189, 3569, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["rodney"], { distance = 4 }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("I'll see if Bill can put something together for him.") },
  },
  {
    text = "Talk to Bill.",
    title = "Building a doghouse",
    actions = {
      Action.Direction:new(3286, 565, 3557, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["bill"], { distance = 12 }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("Bring me two") },
  },
  {
    text = "Talk to Bill again with the materials in your inventory.",
    actions = {
      Action.Direction:new(3286, 565, 3557, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["bill"], { distance = 12 }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("Right you are.") },
  },
  {
    text = "Choose a name for the dog.",
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Princess and the Pauper",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.medium,
  releaseDate = 000000,
  prereqQuests = { "Gertrude's Cat", "Murder on the Border" },
  questReqs = {
    Types.QuestReq.skill("Cooking", 25),
    Types.QuestReq.skill("Construction", 25),
  },
  neededItems = {
    ["Raw salmon"] = { quantity = 1, model = Models.items["raw salmon"] },
    ["Raw meat"] = { quantity = 1, model = Models.items["raw beef"] },
    ["A hammer (not on toolbelt)"] = { quantity = 1, model = Models.items["hammer"] },
    ["Wooden frame"] = { quantity = 2, model = Models.items["wooden frame"] },
    ["Planks"] = { quantity = 6, model = Models.items["plank"], duringQuest = true },
    ["Steel nails"] = { quantity = 10, model = Models.items["steel nail"], duringQuest = true },
    ["Bolt of cloth"] = { quantity = 2, model = Models.items["bolt of cloth"], duringQuest = true },
    ["Wool"] = { quantity = 2, model = Models.items["wool"] },
    ["Bowl of water"] = { quantity = 1, model = Models.items["bowl of water"] },
  },
  recommendedItems = {},
  combatNPCs = {},
})
