local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local commanderMontai = Model.new(5985, {
  [2857] = Vertex.new(0, 440, -10, 14, 166, 40),
  [2858] = Vertex.new(2, 440, -12, 14, 166, 40),
  [2859] = Vertex.new(-2, 440, -12, 14, 166, 40),
  [3717] = Vertex.new(-8, 535, 58, 59, 60, 55),
  [3740] = Vertex.new(8, 551, 38, 59, 60, 55),
})
local firstTrackerGnome = Model.new(4992, {
  [3037] = Vertex.new(0, 440, -10, 14, 166, 40),
  [3038] = Vertex.new(2, 440, -12, 14, 166, 40),
  [3039] = Vertex.new(-2, 440, -12, 14, 166, 40),
  [4878] = Vertex.new(-144, 556, 88, 78, 97, 39),
  [4927] = Vertex.new(-140, 564, 92, 64, 80, 32),
})
local secondTrackerGnome = Model.new(4221, {
  [2545] = Vertex.new(0, 440, -10, 14, 166, 40),
  [2546] = Vertex.new(2, 440, -12, 14, 166, 40),
  [2547] = Vertex.new(-2, 440, -12, 14, 166, 40),
})
local thirdTrackerGnome = Model.new(4434, {
  [3952] = Vertex.new(0, 440, -10, 14, 166, 40),
  [3953] = Vertex.new(2, 440, -12, 14, 166, 40),
  [3954] = Vertex.new(-2, 440, -12, 14, 166, 40),
  [4299] = Vertex.new(-144, 556, 88, 78, 97, 39),
  [4348] = Vertex.new(-140, 564, 92, 64, 80, 32),
})
local elkoy = Model.new(4533, {
  [1101] = Vertex.new(-108, 204, 256, 54, 45, 34),
  [2869] = Vertex.new(0, 440, -10, 14, 166, 40),
  [2870] = Vertex.new(2, 440, -12, 14, 166, 40),
  [2871] = Vertex.new(-2, 440, -12, 14, 166, 40),
  [3927] = Vertex.new(-47, 483, 19, 51, 14, 4),
})
local khazardWarlord = Model.new(5325, {
  [2965] = Vertex.new(0, 735, -7, 27, 139, 126),
  [3242] = Vertex.new(-28, 750, 17, 131, 96, 67),
  [3258] = Vertex.new(28, 750, 17, 131, 96, 67),
  [4194] = Vertex.new(-65, 670, 50, 89, 14, 7),
  [4225] = Vertex.new(65, 670, 50, 89, 14, 7),
})

-- Objects
local ballista = Model.new(8205, {
  [1130] = Vertex.new(-1087, 330, 349, 108, 93, 68),
  [1442] = Vertex.new(1033, 330, 349, 108, 93, 68),
  [2181] = Vertex.new(-1093, 287, 352, 115, 100, 73),
  [4026] = Vertex.new(26, 226, -864, 48, 49, 52),
  [4034] = Vertex.new(-75, 156, -904, 39, 41, 43),
})
local ladder = Model.new(756, {
  [17] = Vertex.new(3728, 2856, 2504, 69, 54, 28),
  [35] = Vertex.new(3728, 2748, 2488, 69, 54, 28),
  [551] = Vertex.new(3728, 2764, 2452, 61, 50, 31),
  [587] = Vertex.new(3728, 2872, 2468, 61, 50, 31),
  [593] = Vertex.new(3728, 2856, 2504, 47, 35, 14),
})

-- Items
-- Quest Items
local orbOne = Model.new(240, {
  [1] = Vertex.new(-64, 112, 16, 75, 182, 15),
  [2] = Vertex.new(-40, 128, 8, 75, 182, 15),
  [3] = Vertex.new(-40, 128, -8, 75, 182, 15),
  [8] = Vertex.new(0, 136, 0, 75, 182, 15),
  [12] = Vertex.new(-8, 128, -32, 75, 182, 15),
})
local orbTwoAndThree = Model.new(480, {
  [1] = Vertex.new(8, 112, 56, 75, 182, 15),
  [2] = Vertex.new(32, 128, 48, 75, 182, 15),
  [3] = Vertex.new(32, 128, 32, 75, 182, 15),
  [8] = Vertex.new(72, 136, 40, 75, 182, 15),
  [12] = Vertex.new(64, 128, 8, 75, 182, 15),
})

---@type QuestStep[]
local steps = {
  --#region Getting started
  {
    text = "Squeeze through the loose railing at the end of the gnome maze.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Yanille lodestone",
      url = "Yanille_lodestone_icon.png",
    },
    actions = {
      Action.PathGuide:new({
        Location:new(2504, 965, 3191),
        Location:new(2511, 965, 3191),
        Location:new(2512, 965, 3188),
        Location:new(2532, 965, 3188),
        Location:new(2532, 965, 3183),
        Location:new(2529, 965, 3181),
        Location:new(2524, 965, 3181),
        Location:new(2522, 957, 3185),
        Location:new(2520, 965, 3181),
        Location:new(2520, 965, 3179),
        Location:new(2514, 965, 3179),
        Location:new(2514, 965, 3177),
        Location:new(2527, 965, 3177),
        Location:new(2527, 965, 3179),
        Location:new(2529, 965, 3179),
        Location:new(2531, 965, 3177),
        Location:new(2531, 997, 3179),
        Location:new(2533, 965, 3179),
        Location:new(2533, 965, 3177),
        Location:new(2544, 965, 3177),
        Location:new(2544, 965, 3175),
        Location:new(2549, 965, 3174),
        Location:new(2549, 965, 3165),
        Location:new(2545, 965, 3164),
        Location:new(2545, 965, 3159),
        Location:new(2551, 965, 3157),
        Location:new(2549, 965, 3156),
        Location:new(2549, 965, 3145),
        Location:new(2538, 965, 3145),
        Location:new(2539, 965, 3150),
        Location:new(2541, 965, 3150),
        Location:new(2543, 965, 3148),
        Location:new(2545, 965, 3150),
        Location:new(2545, 965, 3156),
        Location:new(2520, 965, 3156),
        Location:new(2518, 965, 3159),
        Location:new(2515, 965, 3159),
        Location:new(2515, 965, 3161),
      }),
    },
    postconditions = { Condition.DistanceTo:new(2515, 965, 3163, 2) },
  },
  {
    text = "Talk to King Bolren.",
    actions = { Action.Direction:new(2539, 965, 3169) },
    postconditions = { Condition.DistanceTo:new(2539, 965, 3169, 6) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["king bolren"]),
      Action.ConversationHighlight:new("Can I help at all?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to King Bolren.",
    actions = { Action.ModelHighlight:new(NPCs["king bolren"]) },
    postconditions = { Condition.DistanceTo:new(2504, 965, 3191, 6) },
  },
  --#endregion
  --#region The ballista
  {
    text = "Talk to Commander Montai north of the maze.",
    title = "The ballista",
    neededItems = { ["Normal logs"] = { quantity = 6 } },
    actions = {
      Action.ModelHighlight:new(commanderMontai),
      Action.ConversationHighlight:new("Ok, I'll gather some wood."),
    },
    postconditions = {
      Condition.ConversationText:new("Please be as quick as you can, I don't know how much longer we can hold out."),
    },
  },
  {
    text = "Talk to him again to give him six logs.",
    actions = {
      Action.ModelHighlight:new(commanderMontai),
      Action.ConversationHighlight:new("Ok, I'll gather some wood."),
    },
    postconditions = {
      Condition.ConversationText:new("Please be as quick as you can, I don't know how much longer we can hold out."),
    },
  },
  {
    text = "Talk to Commander Montai again.",
    actions = { Action.ModelHighlight:new(commanderMontai), Action.ConversationHighlight:new("I'll try my best") },
    postconditions = {
      Condition.ConversationText:new(
        " If you can retrieve the orb and bring safety back to my people, none of the blood spilled on this field will be in vain."
      ),
    },
  },
  {
    text = "Talk to the first tracker gnome.",
    title = "debug",
    actions = { Action.Direction:new(2498, 1549, 3263) },
    postconditions = { Condition.ModelVisible:new(firstTrackerGnome) },
  },
  {
    actions = { Action.ModelHighlight:new(firstTrackerGnome) },
    postconditions = { Condition.ConversationText:new("OK, take care.") },
  },
  {
    text = "Talk to the second tracker gnome.",
    actions = { Action.Direction:new(2524, 2053, 3256) },
    postconditions = { Condition.ModelVisible:new(secondTrackerGnome) },
  },
  {
    actions = { Action.ModelHighlight:new(secondTrackerGnome) },
    postconditions = { Condition.ConversationText:new("Go!") },
  },
  {
    text = "Talk to the third tracker gnome.",
    actions = { Action.Direction:new(2497, 1029, 3236) },
    postconditions = { Condition.ModelVisible:new(thirdTrackerGnome) },
  },
  {
    actions = { Action.ModelHighlight:new(thirdTrackerGnome) },
    postconditions = {
      Condition.ConversationText:new("All day we pray in the hay, hee hee."),
    },
  },
  {
    text = "West of Commander Montai, fire the ballista and choose the x coordinate until successful.",
    actions = { Action.ModelHighlight:new(ballista) },
    postconditions = {
      Condition.ConversationText:new("reduced to rubble"), --Wiki transcription doesn't seem to be right
    },
  },
  --#endregion
  --#region Orbs
  {
    text = "The building next to Tracker gnome 1, climb over the crumbled wall which is to the right of the locked door.",
    title = "Orbs",
    actions = { Action.Direction:new(2509, 2053, 3253.5) },
    postconditions = { Condition.DistanceTo:new(2509, 2053, 3256, 2) },
  },
  {
    text = "Climb up the ladder and search the chest for an orb of protection.",
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = { Condition.DistanceToWithHeight:new(2503, 3045, 3251, 4) },
  },
  {
    actions = { Action.Direction:new(2506, 3045, 3259) },
    postconditions = { Condition.InventoryContains:new(orbOne) },
  },
  {
    text = "Climb back down the ladder and head out the door.",
    actions = { Action.Direction:new(2503, 3045, 3252) },
    postconditions = { Condition.DistanceToWithHeight:new(2503, 2053, 3251, 3) },
  },
  {
    actions = { Action.Direction:new(2502, 2053, 3250) },
    postconditions = { Condition.DistanceTo:new(2502, 2037, 3248, 2) },
  },
  {
    text = "Run back towards the north-west entrance of the maze, click Elkoy to Follow him.",
    actions = { Action.Direction:new(2502, 965, 3191) },
    postconditions = { Condition.ModelVisible:new(elkoy) },
  },
  { actions = { Action.ModelHighlight:new(elkoy) }, postconditions = { Condition.DistanceTo:new(2515, 965, 3159, 4) } },
  {
    text = "Talk to King Bolren.",
    actions = { Action.Direction:new(2539, 965, 3169) },
    postconditions = { Condition.DistanceTo:new(2539, 965, 3169, 6) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["king bolren"]),
      Action.ConversationHighlight:new("I will find the warlord and bring back the orbs."),
    },
    postconditions = { Condition.DistanceTo:new(2504, 965, 3191, 6) },
  },
  {
    text = "Head north-west of the battlefield and follow the Ardougne wall all the way west past the wolves.",
    actions = { Action.Direction:new(2451, 1389, 3294) },
    postconditions = { Condition.DistanceTo:new(2451, 1005, 3302, 10) },
  },
  {
    text = "Kill the Khazard warlord.",
    actions = { Action.ModelHighlight:new(khazardWarlord) },
    postconditions = { Condition.InventoryContains:new(orbTwoAndThree) },
  },
  {
    text = "Return to the maze and follow Elkoy.",
    actions = { Action.Direction:new(2502, 965, 3191) },
    postconditions = { Condition.ModelVisible:new(elkoy) },
  },
  { actions = { Action.ModelHighlight:new(elkoy) }, postconditions = { Condition.DistanceTo:new(2515, 965, 3159, 4) } },
  {
    text = "Talk to King Bolren.",
    actions = { Action.Direction:new(2539, 965, 3169) },
    postconditions = { Condition.DistanceTo:new(2539, 965, 3169, 6) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["king bolren"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
  --#endregion
}

return Quest:new({
  name = "Tree Gnome Village",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1027382400,
  prereqQuests = {},
  neededItems = {
    ["Normal logs"] = { quantity = 6, model = Items["logs"], duringQuest = true },
  },
})
