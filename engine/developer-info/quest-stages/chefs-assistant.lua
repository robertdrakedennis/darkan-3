local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, Items, Npcs = Types.Model, Types.Vertex, Models.items, Models.npcs

local headChef = Model.new(4431, {
  [126] = Vertex.new(-57, 652, 66, 64, 22, 71),
  [3388] = Vertex.new(-2, 725, -59, 109, 80, 56),
  [3393] = Vertex.new(-7, 724, -51, 109, 80, 56),
  [3396] = Vertex.new(2, 725, -59, 109, 80, 56),
  [3398] = Vertex.new(7, 724, -51, 109, 80, 56),
})

local brokenDairyChun = Model.new(624, {
  [33] = Vertex.new(-146, 140, 180, 32, 25, 20),
  [64] = Vertex.new(-146, 140, 180, 45, 34, 28),
  [455] = Vertex.new(-146, 140, 180, 66, 51, 42),
  [579] = Vertex.new(271, 2, -136, 45, 34, 28),
  [613] = Vertex.new(167, 48, -213, 45, 34, 28),
})

local fixedDairyChurn = Model.new(894, {
  [267] = Vertex.new(7, 539, -138, 125, 114, 102),
  [287] = Vertex.new(-9, 523, -138, 120, 110, 98),
  [305] = Vertex.new(25, 523, -138, 120, 110, 98),
  [307] = Vertex.new(25, 523, -138, 120, 110, 98),
  [398] = Vertex.new(25, 523, -138, 115, 106, 95),
})

local phil = Model.new(3657, {
  [1165] = Vertex.new(-2, 725, -59, 109, 80, 56),
  [1170] = Vertex.new(-7, 724, -51, 109, 80, 56),
  [1173] = Vertex.new(2, 725, -59, 109, 80, 56),
  [1175] = Vertex.new(7, 724, -51, 109, 80, 56),
  [1864] = Vertex.new(0, 735, -7, 29, 141, 128),
})

local cookingGuildBottomStairCase = Model.new(8271, {
  [407] = Vertex.new(5173, 3022, 4177, 127, 127, 127),
  [821] = Vertex.new(4590, 1916, 3979, 127, 127, 127),
  [823] = Vertex.new(4590, 1907, 3979, 127, 127, 127),
  [7547] = Vertex.new(5155, 3038, 4119, 127, 127, 127),
  [7574] = Vertex.new(5183, 3037, 4121, 127, 127, 127),
})

-- This isn't actually the stair case, it's the wood trim around it.
-- But the staircase box was massive as it was highlighting all floors.
local cookingGuildFirstFloorStairCase = Model.new(528, {
  [73] = Vertex.new(5108, 2466, 4668, 127, 127, 127),
  [193] = Vertex.new(5105, 2466, 4668, 127, 127, 127),
  [209] = Vertex.new(5105, 2615, 4671, 127, 127, 127),
  [211] = Vertex.new(5105, 2615, 4671, 127, 127, 127),
  [473] = Vertex.new(5105, 2615, 4671, 127, 127, 127),
})

local messSergeantRamsey = Model.new(7101, {
  [3806] = Vertex.new(10, 464, 45, 189, 184, 180),
  [3810] = Vertex.new(18, 464, 45, 189, 184, 180),
  [4131] = Vertex.new(-54, 636, -54, 201, 196, 193),
  [4492] = Vertex.new(40, 659, -41, 201, 196, 193),
  [4512] = Vertex.new(50, 636, -54, 201, 196, 193),
})

---@type QuestStep[]
local steps = {
  {
    text = "Head to the Cooks' Guild, the building just south-west of the Grand Exchange main entrance.",
    title = "Getting started",
    neededItems = { ["Chef's hat"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.Direction:new(3143, 1413, 3446) },
    postconditions = { Condition.DistanceTo:new(3143, 1413, 3446, 2) },
  },
  {
    text = "To start, talk to the Head chef in the Cooks' Guild.",
    actions = {
      Action.ModelHighlight:new(headChef),
      Action.InventoryHighlight:new(Items["chefs hat"]),
      Action.ConversationHighlight:new("Do you have any quests for me?"),
      Action.ConversationHighlight:new("Of course, discreet is my middle name!"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue the conversation.",
    actions = {
      Action.ConversationHighlight:new("Cool story, can we get on with this?"),
      Action.ConversationHighlight:new("Sounds tasty!"),
      Action.ConversationHighlight:new("Consider myself braced."),
      Action.ConversationHighlight:new("Pull yourself together!"),
      Action.ConversationHighlight:new("Would you like me to get some more for you?"),
    },
    postconditions = {
      Condition.ConversationText:new("Yes Chef!"),
      -- If the user clicks the step again, the player character says this in response when missing creamcheese.
      Condition.ConversationText:new("Right. Got it."),
    },
  },
  {
    title = "Obtaining the ingredients",
    text = "Speak to Phil or Delphie in Falador, south of the party room.",
    neededItems = {
      ["Logs"] = { quantity = 1 },
      ["Steel bar"] = { quantity = 1 },
      ["Bucket of milk"] = { quantity = 1 },
      ["Pot of flour"] = { quantity = 1 },
      ["Pat of butter"] = { quantity = 1 },
      ["Pot of cream"] = { quantity = 1 },
      ["Cake tin"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.Direction:new(3038, 1445, 3363),
      Action.ModelHighlight:new(phil),
      Action.ConversationHighlight:new("Sure, what do I need?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Great! By the looks of it, some basic logs and a steel bar should suffice for the repair."
      ),
      -- Check for the fixed churn here incase the user tries to go back to this step.
      Condition.ModelVisible:new(fixedDairyChurn),
    },
  },
  {
    text = "Repair the broken dairy churn in the northern part of the room using logs and a steel bar.",
    actions = {
      Action.ModelHighlight:new(brokenDairyChun),
    },
    postconditions = { Condition.ModelVisible:new(fixedDairyChurn) },
  },
  {
    text = "Speak with Phil or Delphie again.",
    actions = {
      Action.ModelHighlight:new(phil),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Just give the milk a good mix in the churn, and you'll have delicious cream cheese in no time!"
      ),
      -- If you speak again, Delphie says this.
      Condition.ConversationText:new(
        "Just give that bucket of milk you have a good mix in the dairy churn, then you'll have delicious cream cheese to take back to the Head Chef."
      ),
    },
  },
  {
    text = "Use bucket of milk on the dairy churn and churn it into some cream cheese.<ul><li>If not done already, make a pot of cream and a pat of butter once you're there.</li></ul>",
    actions = {
      Action.ModelHighlight:new(fixedDairyChurn),
    },
    postconditions = { Condition.DistanceTo:new(3038, 1445, 3366, 0) },
  },
  {
    text = "Make cream cheese at the churn.",
    postconditions = {
      Condition.InventoryContains:new(Items["cream cheese"]),
    },
  },
  {
    text = "Return to the Cook's Guild.",
    neededItems = { ["Chef's hat"] = { quantity = 1 } },
    actions = { Action.Direction:new(3143, 1413, 3446) },
    postconditions = { Condition.DistanceTo:new(3143, 1413, 3446, 2) },
  },
  {
    text = "Speak with the Head Chef.",
    actions = {
      Action.ModelHighlight:new(headChef),
      Action.ConversationHighlight:new("Talk about 'Chef's Assistant'."),
    },
    postconditions = {
      -- If the player is missing ingredients.
      Condition.ConversationText:new("Er... no. Can you remind me what I'm supposed to be doing?"),
      -- If the player speaks with all the ingredients in their inventory.
      Condition.ConversationText:new("Thanks, I'll be back soon."),
      -- If the player speaks with the chef again immediately with no cream cheese.
      Condition.ConversationText:new("OK, I'll be right back."),
    },
  },
  {
    text = "Go upstairs.",
    actions = { Action.ModelHighlight:new(cookingGuildBottomStairCase) },
    postconditions = { Condition.DistanceTo:new(3144, 2597, 3449, 1) },
  },
  {
    text = "Grab the cake tin.",
    actions = { Action.ModelHighlight:new(Items["cake tin"]) },
    postconditions = { Condition.InventoryContains:new(Items["cake tin"]) },
  },
  {
    text = "Make biscuits by combining a pot of flour and pat of butter.",
    actions = {
      Action.InventoryHighlight:new(Items["pot of flour"]),
      Action.InventoryHighlight:new(Items["pat of butter"]),
    },
    postconditions = { Condition.InventoryContains:new(Items["biscuit dough"]) },
  },
  {
    text = "Cook the biscuit dough at the range. (Can burn!)",
    actions = {
      Action.Direction:new(3144, 2597, 3453),
      Action.InventoryHighlight:new(Items["biscuit dough"]),
    },
    postconditions = { Condition.InventoryContains:new(Items["biscuits"]) },
  },
  {
    text = "Speak to the head chef.",
    actions = {
      Action.ModelHighlight:new(headChef),
      Action.ModelHighlight:new(cookingGuildFirstFloorStairCase),
      Action.ConversationHighlight:new("Talk about 'Chef's Assistant'."),
      Action.ConversationHighlight:new("Sorry Chef."),
    },
    postconditions = {
      -- First time speaking with the chef.
      Condition.ConversationText:new("Yes Chef."),
      -- If you speak again without having made the cheesecake.
      Condition.ConversationText:new("OK!"),
    },
  },
  {
    text = "Click the cake tin to create cheesecake.",
    actions = {
      Action.InventoryHighlight:new(Items["cake tin"]),
    },
    postconditions = { Condition.InventoryContains:new(Items["cheesecake"]) },
  },
  {
    text = "Speak to the head chef again.",
    actions = {
      Action.ModelHighlight:new(headChef),
      Action.ConversationHighlight:new("Talk about 'Chef's Assistant'."),
      Action.ConversationHighlight:new("I'm not sure where my list is."),
      Action.ConversationHighlight:new("OK, I'll get right on it."),
    },
    postconditions = {
      -- First time speaking with the head chef.
      Condition.ConversationText:new(
        "Yes, here's a list of people I'd like you to take cheesecake to! Come back to me when you've been to them all."
      ),
      -- If you speak to the chef again without the list.
      Condition.ConversationText:new("Here, take this list! Come back to me when you've been to them all."),
    },
  },
  {
    title = "Cheesy delivery!",
    text = "Deliver the cheesecake to Mess Sergeant Ramsey in Taverley.<ul><li>Make sure not to eat the cheesecake!</li></ul>",
    actions = {
      Action.Direction:new(2896, 837, 3442),
      Action.ModelHighlight:new(messSergeantRamsey),
      Action.ConversationHighlight:new("The Head Chef asked me to bring you this to try."),
    },
    postconditions = {
      Condition.ConversationText:new("...and delicious! Wow! The topping is so rich and creamy! He has my vote!"),
    },
  },
  {
    text = "Deliver the cheesecake to the Cook in Lumbridge castle.",
    actions = {
      Action.Direction:new(3209, 1477, 3215),
      Action.ModelHighlight:new(Npcs["lumbridge cook"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = {
      Condition.ConversationText:new("That buttery biscuit base is to die for! He has my vote!"),
    },
  },
  {
    text = "Deliver the cheesecake to the Cook of the Jolly Boar Inn.",
    actions = {
      Action.Direction:new(3285, 965, 3489),
      Action.ModelHighlight:new(Npcs["jolly boar cook"]),
      Action.ConversationHighlight:new("The Head Chef asked me to bring you this to try."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "This is a taste sensation! The Head Chef has excelled himself this time. He has my vote!"
      ),
    },
  },
  {
    text = "Return to the cooking guild.",
    neededItems = { ["Chef's hat"] = { quantity = 1 } },
    actions = { Action.Direction:new(3143, 1413, 3446) },
    postconditions = { Condition.DistanceTo:new(3143, 1413, 3446, 2) },
  },
  {
    text = "Speak with the head chef.",
    actions = {
      Action.ModelHighlight:new(headChef),
      Action.ConversationHighlight:new("Talk about 'Chef's Assistant'."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Chef's Assistant",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1549843200,
  prereqQuests = { "Cook's Assistant" },
  questReqs = {
    Types.QuestReq.skill("Cooking", 32),
    Types.QuestReq.ironmanOnlySkill("Cooking", 38, true),
  },
  neededItems = {
    ["Logs (normal logs)"] = { quantity = 1, model = Items["logs"], duringQuest = true },
    ["Pot of flour"] = { quantity = 1, model = Items["pot of flour"], duringQuest = true },
    ["Chef's hat"] = { quantity = 1, model = Items["chefs hat"] },
    ["Steel bar"] = { quantity = 1, model = Items["steel bar"] },
    ["Bucket of milk"] = { quantity = 1, model = Items["bucket of milk"] },
    ["Pat of butter"] = { quantity = 1, model = Items["pat of butter"] },
    ["Pot of cream"] = { quantity = 1, model = Items["pot of cream"] },
    ["Cake tin"] = { quantity = 1, model = Items["cake tin"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = {},
})
