local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

local ladyServil = NPCs["lady servil"]
local khazardArmourStand = Objects["khazard armour stand"]
local khazardHelmet = Items["khazard helmet"]
local khazardPlatebody = Items["khazard platebody"]
local khazardCellKeys = Items["khazard cell keys"]
local angor = NPCs["angor"]
local lazyKhazardGuard = NPCs["lazy khazard guard"]
local sleepingKhazardGuard = NPCs["sleeping khazard guard"]
local hengrad = NPCs["hengrad"]
local khazardOgre = NPCs["khazard ogre"]
local khazardScorpion = NPCs["khazard scorpion"]
local generalKhazard = NPCs["general khazard"]

---@type QuestStep[]
local steps = {
  --#region Getting started
  {
    text = "Talk to Lady Servil on the road north-west of the Fight Arena, south of Ardougne.",
    title = "Getting started",
    neededItems = {
      ["Coins"] = { quantity = 5 },
    },
    actions = {
      Action.Direction:new(2565, 600, 3195),
    },
    postconditions = { Condition.DistanceTo:new(2565, 600, 3195, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(ladyServil),
      Action.ConversationHighlight:new("Can I help you?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Lady Servil.",
    actions = {
      Action.ModelHighlight:new(ladyServil),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Please do. My family can reward you for your troubles. I'll be waiting here for you."
      ),
    },
  },
  --#endregion
  --#region Obtaining Khazard armour
  {
    text = "Go to the building north-east of the Fight Arena.",
    title = "Obtaining Khazard armour",
    actions = {
      Action.Direction:new(2609, 869, 3194),
    },
    postconditions = { Condition.DistanceTo:new(2609, 869, 3194, 2) },
  },
  {
    text = "Borrow from the Khazard armour stand to obtain a Khazard helmet and platebody.",
    actions = {
      Action.ModelHighlight:new(khazardArmourStand),
      Action.ConversationHighlight:new("<i>Borrow</i>"),
    },
    postconditions = {
      Condition.InventoryContains:new(khazardHelmet),
      Condition.InventoryContains:new(khazardPlatebody),
    },
  },
  {
    text = "Equip both the Khazard helmet and Khazard platebody.",
    actions = {
      Action.InventoryHighlight:new(khazardHelmet),
      Action.InventoryHighlight:new(khazardPlatebody),
    },
    postconditions = {
      Condition.InventoryDoesNotContain:new(khazardHelmet),
      Condition.InventoryDoesNotContain:new(khazardPlatebody),
    },
  },
  --#endregion
  --#region Freeing Sammy
  {
    text = "With the armour equipped, go south and enter the prison.",
    title = "Freeing Sammy",
    actions = {
      Action.Direction:new(2617, 869, 3172),
    },
    postconditions = { Condition.DistanceTo:new(2617, 869, 3172, 2) },
  },
  {
    text = "Talk to the Lazy Khazard Guard (Gerald) in the south-east corner of the jail.",
    actions = {
      Action.Direction:new(2616, 869, 3146),
      Action.ModelHighlight:new(lazyKhazardGuard),
    },
    postconditions = {
      Condition.ConversationText:new("Yessir!"),
    },
  },
  {
    text = "Go west to the bar and buy a Khali brew from Angor for 5 coins.",
    actions = {
      Action.Direction:new(2567, 600, 3141),
    },
    postconditions = { Condition.DistanceTo:new(2567, 600, 3141, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(angor),
      Action.ConversationHighlight:new("I'd like a Khali brew, please."),
    },
    postconditions = { Condition.ConversationText:new("Khali brew") },
  },
  {
    text = "Return to the Lazy Khazard Guard and give him the Khali brew.",
    actions = {
      Action.Direction:new(2616, 869, 3146),
      Action.ModelHighlight:new(lazyKhazardGuard),
    },
    postconditions = { Condition.ConversationText:new("No problem, I'll keep them in line") },
  },
  {
    text = "Take the keys from the table near the sleeping guard.",
    actions = {
      Action.ModelHighlight:new(sleepingKhazardGuard),
    },
    postconditions = { Condition.InventoryContains:new(khazardCellKeys) },
  },
  {
    text = "Use the keys on Sammy Servil's cell door to the north.",
    actions = {
      Action.Direction:new(2617, 869, 3167),
      Action.InventoryHighlight:new(khazardCellKeys),
    },
    postconditions = { Condition.DistanceTo:new(2617, 869, 3167, 2) },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Hooman go SPLAT.") },
  },
  --#endregion
  --#region Arena battles
  {
    text = "Defeat the Khazard ogre.<ul><li>You can safespot it behind the skeletons in the arena.</li><li>Each fight you win is saved. If you teleport out, opening the prison door will return you to your last checkpoint.</li></ul>",
    title = "Arena battles",
    actions = {
      Action.Direction:new(2599, 925, 3160),
      Action.ModelHighlight:new(khazardOgre),
    },
    postconditions = { Condition.ConversationText:new("You saved both my life and that of my son.") },
  },
  {
    actions = {},
    postconditions = {
      Condition.DistanceTo:new(2605, 861, 3157, 6),
    },
  },
  {
    text = "Talk to Hengrad in the cell.",
    actions = {
      Action.Direction:new(2600, 869, 3142),
    },
    postconditions = { Condition.DistanceTo:new(2600, 869, 3142, 2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(hengrad),
    },
    postconditions = {
      Condition.DistanceTo:new(2605, 861, 3157, 6),
      Condition.ConversationText:new("He'd normally kill imposters like you without a second thought."),
    },
  },
  {
    text = "Defeat the Khazard scorpion.",
    actions = {
      Action.ModelHighlight:new(khazardScorpion),
    },
    postconditions = { Condition.ConversationText:new("Guards! Bring on Bouncer!") },
  },
  {
    text = "Defeat Bouncer (General Khazard's hellhound).<ul><li>You can safespot Bouncer from behind the skeleton remains.</li></ul>",
    actions = {
      Action.Direction:new(2598, 893, 3161),
    },
    postconditions = { Condition.ConversationText:new("Prepare to meet your maker.") },
  },
  {
    text = "Defeat General Khazard or simply leave the arena.",
    actions = {
      Action.ModelHighlight:new(generalKhazard),
    },
    postconditions = {
      Condition.DistanceTo:new(2565, 600, 3195, 15),
      Condition.ConversationText:new("Khazard"),
    },
  },
  --#endregion
  --#region Finishing up
  {
    text = "Return to Lady Servil to complete the quest.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(2565, 600, 3195),
    },
    postconditions = { Condition.DistanceTo:new(2565, 600, 3195, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(ladyServil),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
  --#endregion
}

return Quest:new({
  name = "Fight Arena",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1027382400,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Coins"] = { quantity = 5 },
    ["Combat gear (mage/necro recommended)"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Khazard scorpion"] = { level = "50", quantity = 1 },
    ["Khazard ogre"] = { level = "64", quantity = 1 },
    ["Bouncer"] = { level = "77", quantity = 1 },
    ["General Khazard"] = { level = "50", optional = true, quantity = 2 },
  },
})
