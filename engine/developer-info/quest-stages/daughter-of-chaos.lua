local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local faladorLadder = Models.objects["falador ground floor ladder"]
local faladorStaircase = Model.new(756, {
  [12] = Vertex.new(4948, 4526, 5772, 109, 91, 69),
  [33] = Vertex.new(4272, 4825, 6144, 109, 91, 69),
  [96] = Vertex.new(4948, 4825, 5708, 109, 91, 69),
  [390] = Vertex.new(4948, 4769, 6076, 150, 149, 148),
  [477] = Vertex.new(4948, 4825, 5604, 164, 161, 158),
})
local woundedCultist = Model.new(9648, {
  [0] = Vertex.new(-32, 456, -54, 127, 127, 127),
})
local adrasteia = Model.new(49356, {
  [1] = Vertex.new(34, 742, -1, 127, 127, 127),
})
local anne = Model.new(10803, {
  [0] = Vertex.new(-42, 708, -43, 127, 127, 127),
})
local bilrach = Model.new(6936, {
  [0] = Vertex.new(-227, 478, -24, 253, 253, 253),
})
local avaryss = Model.new(14397, {
  [27] = Vertex.new(331, 1219, 385, 127, 127, 127),
  [672] = Vertex.new(59, 1191, -45, 127, 127, 127),
  [1194] = Vertex.new(406, 1121, 428, 127, 127, 127),
  [1287] = Vertex.new(363, 1219, 351, 127, 127, 127),
  [2136] = Vertex.new(67, 1136, -41, 127, 127, 127),
})
local moia = Model.new(57804, {
  [29194] = Vertex.new(-14, 788, -14, 177, 177, 177),
  [29226] = Vertex.new(0, 767, -47, 177, 177, 177),
  [55363] = Vertex.new(14, 783, -23, 177, 177, 177),
  [55601] = Vertex.new(31, 760, -57, 177, 177, 177),
  [55697] = Vertex.new(24, 793, -18, 177, 177, 177),
})
local trindine = Model.new(5730, {
  [1340] = Vertex.new(-37, 651, -77, 238, 238, 238),
  [1957] = Vertex.new(37, 651, -77, 238, 238, 238),
  [5697] = Vertex.new(0, 961, -81, 238, 238, 238),
  [5712] = Vertex.new(0, 1004, -73, 238, 238, 238),
  [5730] = Vertex.new(0, 1004, -73, 238, 238, 238),
})

local fadedMemory = Model.new(249, {
  [241] = Vertex.new(-2, 535, -4, 146, 23, 12, 0.000),
  [242] = Vertex.new(-2, 541, 2, 146, 23, 12, 0.000),
  [247] = Vertex.new(-2, 624, -4, 146, 23, 12, 0.000),
  [248] = Vertex.new(-2, 630, 2, 146, 23, 12, 0.000),
  [249] = Vertex.new(4, 624, 2, 146, 23, 12, 0.000),
})
local pointOfInterest = Model.new(240, {
  [7] = Vertex.new(-74, 589, -380, 211, 99, 18),
  [31] = Vertex.new(-93, 616, -334, 211, 99, 18),
  [190] = Vertex.new(-47, 635, -361, 211, 99, 18),
  [193] = Vertex.new(-47, 635, -361, 211, 99, 18),
  [196] = Vertex.new(-47, 635, -361, 211, 99, 18),
})
local memoryFragment = Model.new(240, {
  [7] = Vertex.new(-74, 589, -380, 211, 99, 18),
  [31] = Vertex.new(-93, 616, -334, 211, 99, 18),
  [190] = Vertex.new(-47, 635, -361, 211, 99, 18),
  [193] = Vertex.new(-47, 635, -361, 211, 99, 18),
  [196] = Vertex.new(-47, 635, -361, 211, 99, 18),
})
local restoredMemory = Model.new(657, {
  [649] = Vertex.new(-2, 535, -4, 182, 15, 29, 0.000),
  [650] = Vertex.new(-2, 541, 2, 182, 15, 29, 0.000),
  [655] = Vertex.new(-2, 624, -4, 182, 15, 29, 0.000),
  [656] = Vertex.new(-2, 630, 2, 182, 15, 29, 0.000),
  [657] = Vertex.new(4, 624, 2, 182, 15, 29, 0.000),
})

local greaterDemon = Model.new(42378, {
  [36117] = Vertex.new(-52, 1475, -253, 128, 127, 127),
  [36119] = Vertex.new(-49, 1479, -263, 128, 127, 127),
  [36662] = Vertex.new(52, 1475, -253, 128, 127, 127),
  [36665] = Vertex.new(49, 1479, -263, 128, 127, 127),
  [36670] = Vertex.new(49, 1479, -263, 128, 127, 127),
})
local choasDemon = Model.new(42960, {
  [36571] = Vertex.new(-33, 1466, -267, 128, 127, 127),
  [36724] = Vertex.new(-13, 1503, -256, 128, 127, 127),
  [37060] = Vertex.new(33, 1466, -267, 128, 127, 127),
  [37213] = Vertex.new(13, 1503, -256, 128, 127, 127),
  [37216] = Vertex.new(13, 1503, -256, 128, 127, 127),
})
local zamorakianCultist = Model.new(11178, {
  [2755] = Vertex.new(-76, 643, 19, 127, 127, 127),
  [2764] = Vertex.new(76, 643, 19, 127, 127, 127),
  [2767] = Vertex.new(0, 735, -7, 127, 127, 127),
  [2768] = Vertex.new(0, 732, -7, 127, 127, 127),
  [2769] = Vertex.new(0, 732, -8, 127, 127, 127),
})
local chaosWitch = Model.new(11082, {
  [292] = Vertex.new(-34, 757, -24, 127, 127, 127),
  [446] = Vertex.new(16, 755, 46, 127, 127, 127),
  [661] = Vertex.new(-14, 760, -46, 127, 127, 127),
  [677] = Vertex.new(38, 769, -29, 127, 127, 127),
  [679] = Vertex.new(38, 769, -29, 127, 127, 127),
})

---@type QuestStep[]
local steps = {
  {
    text = "Teleport to Falador and run south towards the White Knights' Castle.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = { Action.Direction:new(2965, 2725, 3353) },
    postconditions = { Condition.DistanceTo:new(2965, 2725, 3353, 5) },
  },
  {
    text = "Climb the ladder to the east, then the staircase behind Sir Renitee.",
    actions = { Action.Direction:new(2994, 2725, 3341) },
    postconditions = { Condition.DistanceTo:new(2993, 2725, 3341, 11) },
  },
  {
    actions = { Action.ModelHighlight:new(faladorLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(2993, 3909, 3341, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(faladorStaircase) },
    postconditions = { Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5) },
  },
  {
    text = "Talk to Adrasteia in the White Knights' Castle's throne room (2nd floor[UK]3rd floor[US], east side).",
    actions = { Action.ModelHighlight:new(adrasteia) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Adrasteia, press escape to skip the cutscene.",
    actions = {
      Action.ConversationHighlight:new("No."),
      Action.ConversationHighlight:new("I'll see it done."),
    },
    postconditions = { Condition.ConversationText:new("have every faith") },
  },
  {
    text = "Talk to Anne Dimitri in the interrogation room (1st floor[UK] 2nd floor[US], centre).",
    title = "Abandoned camp",
    actions = { Action.Direction:new(2984, 5093, 3339) },
    postconditions = { Condition.DistanceToWithHeight:new(2985, 3909, 3336, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(anne),
      Action.ConversationHighlight:new("Agree with Adrasteia."),
      Action.ConversationHighlight:new("Agree with Anne Dimitri."),
    },
    postconditions = { Condition.ConversationText:new("Let's meet there.") },
  },
  {
    text = "Talk to Anne Dimitri across the Wilderness wall north of the Grand Exchange underwall tunnel.<ul><li>Make sure PVP is turned off. Most of the quest takes place in instances.</li></ul>",
    actions = {
      Action.Direction:new(3136, 933, 3535, { distance = 20 }),
      Action.ModelHighlight:new(anne, { distance = 20 }),
      Action.ConversationHighlight:new("I'm ready."),
    },
    postconditions = { Condition.ConversationText:new("the symbol of Bilrach's forces") },
  },
  {
    text = "Talk to Anne Dimitri again after arriving at the abandoned camp.",
    actions = { Action.ModelHighlight:new(anne, { instance = true }) },
    postconditions = { Condition.ConversationText:new("What do we have here") },
  },
  {
    text = "Talk to Anne Dimitri yet again.",
    actions = { Action.ModelHighlight:new(anne, { instance = true }) },
    postconditions = { Condition.ConversationText:new("On it.") },
  },
  {
    text = "Investigate the northern point of interest.",
    actions = {
      Action.Direction:new(-24, -128, 11, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(pointOfInterest, { instance = true, distance = 12 }),
    },
    postconditions = { Condition.ChatText:new("Points of interest found : 1/3") },
  },
  {
    text = "Investigate the western point of interest.",
    actions = {
      Action.Direction:new(-39, 224, -5, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(pointOfInterest, { instance = true, distance = 12 }),
    },
    postconditions = { Condition.ChatText:new("Points of interest found : 2/3") },
  },
  {
    text = "Investigate the southern point of interest.",
    actions = {
      Action.Direction:new(-12, -216, -14, { instance = true, distance = 12 }),
      Action.ModelHighlight:new(pointOfInterest, { instance = true, distance = 12 }),
    },
    postconditions = { Condition.ChatText:new("Points of interest found : 3/3") },
  },
  {
    text = "Talk to Anne Dimitri.",
    actions = { Action.ModelHighlight:new(anne, { instance = true }) },
    postconditions = { Condition.ConversationText:new("I'm ready.") },
  },
  {
    text = "Capture 6 memory fragments that spawn.",
    actions = { Action.ModelHighlight:new(memoryFragment, { highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("Fragments collected : 6/6") },
  },
  {
    text = "Interact with the Restored Memory twice.",
    actions = { Action.ModelHighlight:new(restoredMemory, { instance = true }) },
    postconditions = { Condition.ModelNotVisible:new(restoredMemory) },
  },
  {
    text = "Talk to Bilrach in the west.",
    actions = {
      Action.ResetInstance:new(),
      Action.Direction:new(-1, -32, 1, { instance = true, distance = 16 }),
      Action.ModelHighlight:new(bilrach, { instance = true, distance = 16 }),
    },
    postconditions = { Condition.ConversationText:new("Defend the camp") },
  },
  {
    text = "Defend the camp.<ul></li><li>1 (Vault): use only to dodge chaos rift area of effect.</li><li>2 (Drain): use on cooldown to heal (channelled).</li><li>3 (Blast): use on cooldown near enemies for extra damage.</li></ul>",
    actions = {
      actions = { Action.ModelHighlight:new(anne) }, --if user dies
      Action.ModelHighlight:new(zamorakianCultist, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(chaosWitch, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(choasDemon, { instance = true, highlightPriority = "all" }),
    },
    postconditions = { Condition.ChatText:new("All enemies have been defeated!") },
  },
  {
    text = "Talk to Bilrach.",
    actions = { Action.ModelHighlight:new(bilrach, { instance = true }) },
    postconditions = {
      Condition.ConversationText:new("As you command"),
      Condition.ConversationText:new("You still with me"),
    },
  },
  { postconditions = { Condition.ConversationText:new("You still with me") } },
  {
    text = "Talk to Anne Dimitri.",
    title = "Red Dragon Isle",
    actions = { Action.ModelHighlight:new(anne) },
    postconditions = { Condition.ConversationText:new("have you been working for") },
  },
  {
    -- text = "debug",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(anne),
      Action.ConversationHighlight:new("We should move on."),
    },
    postconditions = { Condition.ConversationText:new("see another memory fragment") },
  },
  {
    text = "Talk to Anne Dimitri again.",
    actions = { Action.ModelHighlight:new(anne) },
    postconditions = { Condition.ConversationText:new("that resonate with the memory") },
  },
  {
    text = "Investigate the point of interest next to you.",
    actions = { Action.ModelHighlight:new(pointOfInterest, { instance = true }) },
    postconditions = { Condition.ChatText:new("Points of interest found : 1/3") },
  },
  {
    text = "Investigate the point of interest to the south-east.",
    actions = {
      Action.Direction:new(8, -32, -40, { instance = true, distance = 20 }),
      Action.ModelHighlight:new(pointOfInterest, { instance = true, distance = 20 }),
    },
    postconditions = { Condition.ChatText:new("Points of interest found : 2/3") },
  },
  {
    text = "Investigate the point of interest to the west.",
    actions = {
      Action.Direction:new(-21, 0, -40, { instance = true, distance = 20 }),
      Action.ModelHighlight:new(pointOfInterest, { instance = true, distance = 20 }),
    },
    postconditions = { Condition.ChatText:new("Points of interest found : 3/3") },
  },
  {
    text = "Talk to Anne Dimitri.",
    actions = {
      Action.Direction:new(2, 272, -4, { instance = true, distance = 20 }),
      Action.ModelHighlight:new(anne, { instance = true, distance = 20 }),
    },
    postconditions = { Condition.ConversationText:new("I'm on it.") },
  },
  {
    text = "Capture 6 memory fragments.",
    actions = { Action.ModelHighlight:new(memoryFragment, { instanced = true, highlightPriority = "all" }) },
    postconditions = { Condition.ChatText:new("Fragments collected : 6/6") },
  },
  {
    text = "Interact with the Restored Memory twice.",
    actions = { Action.ModelHighlight:new(restoredMemory, { instanced = true }) },
    postconditions = {
      Condition.ModelVisible:new(moia),
      Condition.ConversationText:new("attacked us from both"),
    },
  },
  {
    text = "Kill the 6 enemies same as the previous section.",
    actions = {
      actions = { Action.ModelHighlight:new(anne) }, --if user dies
      Action.ModelHighlight:new(zamorakianCultist, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(chaosWitch, { instance = true, highlightPriority = "all" }),
      Action.ModelHighlight:new(choasDemon, { instance = true, highlightPriority = "all" }),
    },
    postconditions = { Condition.ChatText:new("All enemies have been defeated!") },
  },
  {
    text = "Talk to the wounded cultist in the south-western corner of the isle.",
    actions = { Action.ModelHighlight:new(woundedCultist, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(trindine) },
  },
  {
    text = "Talk to Trindine.",
    actions = { Action.ModelHighlight:new(trindine, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("have an update") },
  },
  {
    text = "Talk to Bilrach.",
    postconditions = { Condition.ModelVisible:new(anne) },
  },
  {
    text = "Talk to Anne.",
    title = "Wilderness Crater",
    actions = { Action.ModelHighlight:new(anne, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("Here we are") },
  },
  {
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(anne, { instanced = true }),
    },
    postconditions = { Condition.ConversationText:new("Let's investigate") },
  },
  {
    text = "Talk to Anne next to the Restored Memory.",
    actions = { Action.ModelHighlight:new(anne, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("get this portal open") },
  },
  {
    text = "Interact with the Restored Memory.",
    actions = { Action.ModelHighlight:new(restoredMemory, { instanced = true }) },
    postconditions = { Condition.ModelVisible:new(avaryss) },
  },
  {
    text = "Talk to Avaryss",
    actions = { Action.ModelHighlight:new(avaryss, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("time for you and your") },
  },
  {
    text = "Kill Avaryss, the Unceasing.<ul><li>She periodically summons two greater demons that can be killed for health orbs.</li></ul>",
    actions = { Action.ModelHighlight:new(anne) }, --if user dies
    postconditions = { Condition.ConversationText:new("You are weak") },
  },
  {
    text = "Talk to Avaryss.",
    actions = { Action.ModelHighlight:new(avaryss, { instanced = true }) },
    postconditions = { Condition.ConversationText:new("watching the memory") },
  },
  {
    text = "Talk to Moia.",
    title = "Confronting Moia",
    actions = {
      Action.ResetInstance:new(),
      Action.ModelHighlight:new(moia, { instanced = true }),
      Action.ConversationHighlight:new("Adrasteia sent me."),
      Action.ConversationHighlight:new("Respond diplomatically."),
    },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Talk to Trindine.",
    actions = { Action.ConversationHighlight:new("I'll report back to Adrasteia.") },
    postconditions = { Condition.ConversationText:new("Tell her I said") },
  },
  {
    text = "Return to Adrasteia in the White Knights' Castle's throne room (2nd floor[UK] 3rd floor[US], east side).",
    actions = {
      Action.Direction:new(2993, 2725, 3341, { distance = 11 }),
      Action.ModelHighlight:new(faladorLadder, { distance = 12 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(2993, 3909, 3341, 3) },
  },
  {
    actions = { Action.ModelHighlight:new(faladorStaircase) },
    postconditions = { Condition.DistanceToWithHeight:new(2984, 5093, 3340, 5) },
  },
  {
    actions = { Action.ModelHighlight:new(adrasteia) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Daughter of Chaos",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1659312000,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Archaeology", 40),
    Types.QuestReq.skill("Divination", 40),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
