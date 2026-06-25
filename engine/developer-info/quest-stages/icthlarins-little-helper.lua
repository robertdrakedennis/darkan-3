local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local wanderer = Model.new(4425, {
  [1971] = Vertex.new(24, 738, -38, 31, 29, 28),
  [1995] = Vertex.new(-24, 738, -38, 31, 29, 28),
  [2017] = Vertex.new(-2, 716, -57, 51, 42, 26),
  [2023] = Vertex.new(2, 716, -57, 51, 42, 26),
  [2027] = Vertex.new(6, 716, -52, 51, 42, 26),
})
local sphinx = Model.new(11598, {
  [215] = Vertex.new(-82, 776, -897, 127, 127, 127),
  [1334] = Vertex.new(-50, 818, -928, 127, 127, 127),
  [1336] = Vertex.new(-43, 809, -942, 127, 127, 127),
  [1386] = Vertex.new(-122, 649, -806, 127, 127, 127),
  [7174] = Vertex.new(-68, 680, -803, 127, 127, 127),
})
local highPriest = Model.new(6930, {
  [2075] = Vertex.new(7, 724, -51, 43, 39, 22),
  [2077] = Vertex.new(2, 725, -59, 43, 39, 22),
  [2083] = Vertex.new(-2, 725, -59, 43, 39, 22),
  [5836] = Vertex.new(-27, 745, -27, 127, 127, 127),
  [5881] = Vertex.new(27, 745, -27, 127, 127, 127),
})
local anyApparition = Model.any({
  Model.new(4827, { --het
    [3379] = Vertex.new(-2, 725, -59, 51, 42, 26),
    [3384] = Vertex.new(-7, 724, -51, 51, 42, 26),
    [3387] = Vertex.new(2, 725, -59, 51, 42, 26),
    [3389] = Vertex.new(7, 724, -51, 51, 42, 26),
    [3943] = Vertex.new(0, 735, -7, 28, 140, 127),
  }),
})
local embalmer = Model.new(7218, {
  [2353] = Vertex.new(2, 725, -59, 70, 52, 28),
  [2357] = Vertex.new(7, 724, -51, 70, 52, 28),
  [2377] = Vertex.new(-2, 725, -59, 70, 52, 28),
  [2827] = Vertex.new(-27, 745, -27, 127, 127, 127),
  [2935] = Vertex.new(27, 745, -27, 127, 127, 127),
})
local carpenter = Model.new(5913, {
  [2701] = Vertex.new(-2, 725, -59, 43, 39, 22),
  [2706] = Vertex.new(-7, 724, -51, 43, 39, 22),
  [2709] = Vertex.new(2, 725, -59, 43, 39, 22),
  [2711] = Vertex.new(7, 724, -51, 43, 39, 22),
  [3724] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local raetul = Model.new(5379, {
  [1885] = Vertex.new(2, 725, -59, 43, 39, 22),
  [1909] = Vertex.new(-2, 725, -59, 43, 39, 22),
  [2556] = Vertex.new(-29, 737, -20, 34, 32, 31),
  [4597] = Vertex.new(-27, 745, -27, 127, 127, 127),
  [4705] = Vertex.new(27, 745, -27, 127, 127, 127),
})
local priest = Model.new(7188, {
  [2360] = Vertex.new(7, 724, -51, 43, 39, 22),
  [2362] = Vertex.new(2, 725, -59, 43, 39, 22),
  [2368] = Vertex.new(-2, 725, -59, 43, 39, 22),
  [3541] = Vertex.new(-27, 745, -27, 127, 127, 127),
  [3586] = Vertex.new(27, 745, -27, 127, 127, 127),
})
--#endregion
--#region Objects
local pyramidEntrance = Model.new(1029, {
  [1025] = Vertex.new(7424, 424, 6400, 55, 143, 29),
  [1026] = Vertex.new(7423, 424, 6401, 55, 143, 29),
  [1027] = Vertex.new(7935, 440, 6399, 55, 143, 29),
  [1028] = Vertex.new(7936, 440, 6400, 55, 143, 29),
  [1029] = Vertex.new(7935, 440, 6401, 55, 143, 29),
})
--#endregion
--#region Items
local possessedPriestDrops = Model.any({ --not tested because my account doesn't have these
  Model.multi({ -- defence potion (4)
    Model.new(66, {
      [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [2] = Vertex.new(4, 112, -12, 113, 101, 45),
      [3] = Vertex.new(4, 96, -12, 113, 101, 45),
      [4] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [7] = Vertex.new(4, 96, 12, 113, 101, 45),
      [8] = Vertex.new(-4, 112, 12, 113, 101, 45),
      [9] = Vertex.new(-4, 96, 12, 113, 101, 45),
      [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
      [17] = Vertex.new(-12, 112, -4, 113, 101, 45),
      [36] = Vertex.new(12, 96, -4, 113, 101, 45),
    }),
    Model.new(240, {
      [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
      [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
      [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
      [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
      [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
      [11] = Vertex.new(20, 96, 4, 134, 135, 146, 0.4980),
      [13] = Vertex.new(20, 84, -4, 134, 135, 146, 0.4980),
      [17] = Vertex.new(20, 96, -4, 134, 135, 146, 0.4980),
      [19] = Vertex.new(4, 84, -20, 134, 135, 146, 0.4980),
      [23] = Vertex.new(4, 96, -20, 134, 135, 146, 0.4980),
      [49] = Vertex.new(-4, 68, 16, 31, 158, 34, 0.8745),
      [50] = Vertex.new(-4, 84, 12, 31, 158, 34, 0.8745),
      [51] = Vertex.new(-12, 84, 4, 31, 158, 34, 0.8745),
      [54] = Vertex.new(-16, 68, 4, 31, 158, 34, 0.8745),
      [55] = Vertex.new(-12, 12, 40, 31, 158, 34, 0.8745),
      [60] = Vertex.new(-12, 84, -4, 31, 158, 34, 0.8745),
      [63] = Vertex.new(-40, 12, 12, 31, 158, 34, 0.8745),
      [64] = Vertex.new(-4, 0, 36, 31, 158, 34, 0.8745),
      [69] = Vertex.new(-32, 0, 4, 31, 158, 34, 0.8745),
      [72] = Vertex.new(-16, 68, -4, 31, 158, 34, 0.8745),
    }),
  }),
  Model.multi({ -- agility potion (4)
    Model.new(66, {
      [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [2] = Vertex.new(4, 112, -12, 113, 101, 45),
      [3] = Vertex.new(4, 96, -12, 113, 101, 45),
      [4] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [7] = Vertex.new(4, 96, 12, 113, 101, 45),
      [8] = Vertex.new(-4, 112, 12, 113, 101, 45),
      [9] = Vertex.new(-4, 96, 12, 113, 101, 45),
      [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
      [17] = Vertex.new(-12, 112, -4, 113, 101, 45),
      [36] = Vertex.new(12, 96, -4, 113, 101, 45),
    }),
    Model.new(240, {
      [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
      [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
      [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
      [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
      [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
      [11] = Vertex.new(20, 96, 4, 134, 135, 146, 0.4980),
      [13] = Vertex.new(20, 84, -4, 134, 135, 146, 0.4980),
      [17] = Vertex.new(20, 96, -4, 134, 135, 146, 0.4980),
      [19] = Vertex.new(4, 84, -20, 134, 135, 146, 0.4980),
      [23] = Vertex.new(4, 96, -20, 134, 135, 146, 0.4980),
      [49] = Vertex.new(-4, 68, 16, 79, 97, 8, 0.8745),
      [50] = Vertex.new(-4, 84, 12, 79, 97, 8, 0.8745),
      [51] = Vertex.new(-12, 84, 4, 79, 97, 8, 0.8745),
      [54] = Vertex.new(-16, 68, 4, 79, 97, 8, 0.8745),
      [55] = Vertex.new(-12, 12, 40, 79, 97, 8, 0.8745),
      [60] = Vertex.new(-12, 84, -4, 79, 97, 8, 0.8745),
      [63] = Vertex.new(-40, 12, 12, 79, 97, 8, 0.8745),
      [64] = Vertex.new(-4, 0, 36, 79, 97, 8, 0.8745),
      [69] = Vertex.new(-32, 0, 4, 79, 97, 8, 0.8745),
      [72] = Vertex.new(-16, 68, -4, 79, 97, 8, 0.8745),
    }),
  }),
  Model.multi({ -- attack potion (4)
    Model.new(66, {
      [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [2] = Vertex.new(4, 112, -12, 113, 101, 45),
      [3] = Vertex.new(4, 96, -12, 113, 101, 45),
      [4] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [7] = Vertex.new(4, 96, 12, 113, 101, 45),
      [8] = Vertex.new(-4, 112, 12, 113, 101, 45),
      [9] = Vertex.new(-4, 96, 12, 113, 101, 45),
      [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
      [17] = Vertex.new(-12, 112, -4, 113, 101, 45),
      [36] = Vertex.new(12, 96, -4, 113, 101, 45),
    }),
    Model.new(240, {
      [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
      [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
      [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
      [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
      [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
      [11] = Vertex.new(20, 96, 4, 134, 135, 146, 0.4980),
      [13] = Vertex.new(20, 84, -4, 134, 135, 146, 0.4980),
      [17] = Vertex.new(20, 96, -4, 134, 135, 146, 0.4980),
      [19] = Vertex.new(4, 84, -20, 134, 135, 146, 0.4980),
      [23] = Vertex.new(4, 96, -20, 134, 135, 146, 0.4980),
      [49] = Vertex.new(-4, 68, 16, 31, 153, 158, 0.8745),
      [50] = Vertex.new(-4, 84, 12, 31, 153, 158, 0.8745),
      [51] = Vertex.new(-12, 84, 4, 31, 153, 158, 0.8745),
      [54] = Vertex.new(-16, 68, 4, 31, 153, 158, 0.8745),
      [55] = Vertex.new(-12, 12, 40, 31, 153, 158, 0.8745),
      [60] = Vertex.new(-12, 84, -4, 31, 153, 158, 0.8745),
      [63] = Vertex.new(-40, 12, 12, 31, 153, 158, 0.8745),
      [64] = Vertex.new(-4, 0, 36, 31, 153, 158, 0.8745),
      [69] = Vertex.new(-32, 0, 4, 31, 153, 158, 0.8745),
      [72] = Vertex.new(-16, 68, -4, 31, 153, 158, 0.8745),
    }),
  }),
  Model.multi({ -- super magic potion (4)
    Model.new(66, {
      [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [2] = Vertex.new(4, 112, -12, 113, 101, 45),
      [3] = Vertex.new(4, 96, -12, 113, 101, 45),
      [4] = Vertex.new(-4, 96, -12, 113, 101, 45),
      [7] = Vertex.new(4, 96, 12, 113, 101, 45),
      [8] = Vertex.new(-4, 112, 12, 113, 101, 45),
      [9] = Vertex.new(-4, 96, 12, 113, 101, 45),
      [13] = Vertex.new(-12, 96, -4, 113, 101, 45),
      [17] = Vertex.new(-12, 112, -4, 113, 101, 45),
      [36] = Vertex.new(12, 96, -4, 113, 101, 45),
    }),
    Model.new(240, {
      [1] = Vertex.new(4, 84, 20, 134, 135, 146, 0.4980),
      [2] = Vertex.new(-4, 96, 20, 134, 135, 146, 0.4980),
      [3] = Vertex.new(-4, 84, 20, 134, 135, 146, 0.4980),
      [5] = Vertex.new(4, 96, 20, 134, 135, 146, 0.4980),
      [7] = Vertex.new(20, 84, 4, 134, 135, 146, 0.4980),
      [11] = Vertex.new(20, 96, 4, 134, 135, 146, 0.4980),
      [13] = Vertex.new(20, 84, -4, 134, 135, 146, 0.4980),
      [17] = Vertex.new(20, 96, -4, 134, 135, 146, 0.4980),
      [19] = Vertex.new(4, 84, -20, 134, 135, 146, 0.4980),
      [23] = Vertex.new(4, 96, -20, 134, 135, 146, 0.4980),
      [49] = Vertex.new(-4, 68, 16, 53, 101, 131, 0.8745),
      [50] = Vertex.new(-4, 84, 12, 53, 101, 131, 0.8745),
      [51] = Vertex.new(-12, 84, 4, 53, 101, 131, 0.8745),
      [54] = Vertex.new(-16, 68, 4, 53, 101, 131, 0.8745),
      [55] = Vertex.new(-12, 12, 40, 53, 101, 131, 0.8745),
      [60] = Vertex.new(-12, 84, -4, 53, 101, 131, 0.8745),
      [63] = Vertex.new(-40, 12, 12, 53, 101, 131, 0.8745),
      [64] = Vertex.new(-4, 0, 36, 53, 101, 131, 0.8745),
      [69] = Vertex.new(-32, 0, 4, 53, 101, 131, 0.8745),
      [72] = Vertex.new(-16, 68, -4, 53, 101, 131, 0.8745),
    }),
  }),
})

--#endregion
--#region Quest Items
local anyCanopicJar = Model.any({
  Model.new(573, { -- het
    [356] = Vertex.new(-8, 236, -56, 64, 64, 49),
    [361] = Vertex.new(-8, 236, -56, 64, 64, 49),
    [381] = Vertex.new(-8, 236, -56, 64, 64, 49),
    [513] = Vertex.new(0, 200, -60, 94, 94, 72),
    [555] = Vertex.new(0, 200, -60, 94, 94, 72),
  }),
  Model.new(588, { -- scarabas
    [8] = Vertex.new(-20, 172, -92, 94, 94, 72),
    [15] = Vertex.new(28, 168, -108, 94, 94, 72),
    [138] = Vertex.new(-28, 192, -44, 94, 94, 72),
    [173] = Vertex.new(36, 192, -44, 94, 94, 72),
    [192] = Vertex.new(32, 188, -52, 94, 94, 72),
  }),
  Model.new(564, { -- crondis
    [396] = Vertex.new(32, 192, -56, 94, 94, 72),
    [434] = Vertex.new(32, 192, -56, 94, 94, 72),
    [436] = Vertex.new(32, 192, -56, 94, 94, 72),
    [552] = Vertex.new(12, 224, -120, 94, 94, 72),
    [563] = Vertex.new(-12, 216, -120, 94, 94, 72),
  }),
  Model.new(582, { -- apmeken
    [447] = Vertex.new(-12, 228, -28, 94, 94, 72),
    [450] = Vertex.new(8, 228, -28, 94, 94, 72),
    [494] = Vertex.new(-8, 228, -28, 94, 94, 72),
    [511] = Vertex.new(0, 256, 24, 94, 94, 72),
    [582] = Vertex.new(-8, 228, -28, 94, 94, 72),
  }),
})
local canopicJarOfHet = Model.new(573, {
  [356] = Vertex.new(-8, 236, -56, 64, 64, 49),
  [361] = Vertex.new(-8, 236, -56, 64, 64, 49),
  [381] = Vertex.new(-8, 236, -56, 64, 64, 49),
  [513] = Vertex.new(0, 200, -60, 94, 94, 72),
  [555] = Vertex.new(0, 200, -60, 94, 94, 72),
})
local canopicJarOfScarabas = Model.new(588, {
  [8] = Vertex.new(-20, 172, -92, 94, 94, 72),
  [15] = Vertex.new(28, 168, -108, 94, 94, 72),
  [138] = Vertex.new(-28, 192, -44, 94, 94, 72),
  [173] = Vertex.new(36, 192, -44, 94, 94, 72),
  [192] = Vertex.new(32, 188, -52, 94, 94, 72),
})
local canopicJarOfCrondis = Model.new(564, {
  [396] = Vertex.new(32, 192, -56, 94, 94, 72),
  [434] = Vertex.new(32, 192, -56, 94, 94, 72),
  [436] = Vertex.new(32, 192, -56, 94, 94, 72),
  [552] = Vertex.new(12, 224, -120, 94, 94, 72),
  [563] = Vertex.new(-12, 216, -120, 94, 94, 72),
})
local canopicJarOfApmeken = Model.new(582, {
  [447] = Vertex.new(-12, 228, -28, 94, 94, 72),
  [450] = Vertex.new(8, 228, -28, 94, 94, 72),
  [494] = Vertex.new(-8, 228, -28, 94, 94, 72),
  [511] = Vertex.new(0, 256, 24, 94, 94, 72),
  [582] = Vertex.new(-8, 228, -28, 94, 94, 72),
})
local unholySymbol = Model.new(324, {
  [41] = Vertex.new(4, 12, 56, 60, 73, 55),
  [43] = Vertex.new(4, 12, 56, 60, 73, 55),
  [51] = Vertex.new(4, 12, 56, 60, 73, 55),
  [52] = Vertex.new(4, 12, 56, 60, 73, 55),
  [62] = Vertex.new(-16, 0, 56, 60, 73, 55),
})
local sphinxsToken = Model.new(564, {
  [559] = Vertex.new(36, 216, 12, 114, 113, 35),
  [560] = Vertex.new(36, 204, 12, 114, 113, 35),
  [561] = Vertex.new(36, 208, 0, 114, 113, 35),
  [562] = Vertex.new(-12, 204, 12, 114, 113, 35),
  [563] = Vertex.new(-12, 216, 12, 114, 113, 35),
})
local linen = Model.new(96, {
  [3] = Vertex.new(48, 0, -84, 127, 116, 97),
  [47] = Vertex.new(-32, 0, 168, 127, 116, 97),
  [49] = Vertex.new(-32, -4, 168, 144, 129, 91),
  [51] = Vertex.new(120, -4, 132, 144, 129, 91),
  [96] = Vertex.new(48, -4, -84, 144, 129, 91),
})
local holySymbol = Model.new(324, {
  [1] = Vertex.new(-36, 12, 32, 82, 60, 16),
  [4] = Vertex.new(-36, 12, 32, 82, 60, 16),
  [14] = Vertex.new(-36, 12, 32, 82, 60, 16),
  [18] = Vertex.new(-36, 12, 32, 82, 60, 16),
  [110] = Vertex.new(48, 12, 20, 82, 60, 16),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the Wanderer, north of Sophanem.<ul><li>You can use the magic carpet network south of Shantay's Pass at Al Kharid to get to Nardah, and then run southwest.</li></ul>",
    title = "Starting out",
    warning = "For the tracking to work properly, you need to have the chat visible, and game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(3315, 677, 2849, { distance = 20 }),
      Action.ModelHighlight:new(wanderer, { distance = 8 }),
      Action.ConversationHighlight:new("Why? [Icthlarin's Little Helper]"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Wanderer.",
    actions = {
      Action.Direction:new(3315, 677, 2849, { distance = 20 }),
      Action.ModelHighlight:new(wanderer, { distance = 8 }),
      Action.ConversationHighlight:new("Tell me about Sophanem. [Icthlarin's Little Helper]"), --not tested
    },
    postconditions = { Condition.DistanceTo:new(3295, 365, 2782, 4) },
  },
  {
    text = "Open the northern door on the southern pyramid (picture of a cat).",
    title = "Graverobbing",
    actions = { Action.ModelHighlight:new(pyramidEntrance) },
    postconditions = { Condition.DistanceTo:new(3277, 965, 9170, 4) },
  },
  {
    text = "Run through the maze.",
    actions = {
      Action.PathGuide:new({
        Location:new(3277, 965, 9170),
        Location:new(3289, 965, 9170),
        Location:new(3290, 965, 9171),
        Location:new(3291, 965, 9171),
        Location:new(3292, 965, 9170),
        Location:new(3293, 965, 9170),
        Location:new(3294, 965, 9171),
        Location:new(3299, 965, 9171),
        Location:new(3299, 965, 9170),
        Location:new(3302, 965, 9170),
        Location:new(3302, 965, 9171),
        Location:new(3305, 965, 9171),
        Location:new(3305, 965, 9170),
        Location:new(3308, 965, 9170),
        Location:new(3308, 965, 9181),
        Location:new(3304, 965, 9185),
        Location:new(3301, 965, 9185),
        Location:new(3301, 965, 9184),
        Location:new(3298, 965, 9184),
        Location:new(3298, 965, 9175),
        Location:new(3291, 965, 9175),
        Location:new(3291, 965, 9183),
        Location:new(3287, 965, 9183),
        Location:new(3287, 965, 9174),
        Location:new(3281, 965, 9174),
        Location:new(3277, 965, 9179),
        Location:new(3277, 965, 9188),
        Location:new(3291, 965, 9188),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3291, 965, 9191, 6) },
  },
  {
    text = "Jump across the pit.",
    warning = "This requires at least 20% run energy.",
    actions = { Action.Direction:new(3291.5, 1465, 9196) },
    postconditions = { Condition.DistanceTo:new(3291.5, 965, 9196, 2) },
  },
  {
    text = "Open the doorway of the western room and solve the puzzle.<ul><li>You may reroll your puzzle until it looks like solution below.</li></ul>",
    actions = { Action.Direction:new(3280, 1465, 9199.5) },
    postconditions = { Condition.ConversationText:new("my poor head") },
  },
  {
    text = "Talk to the Sphinx with your cat/kitten out.",
    title = "The Desert Demigods",
    warning = "If you choose the wrong option, you will lose your kitten/cat.",
    actions = {
      Action.ModelHighlight:new(sphinx),
      Action.ConversationHighlight:new("I need help."),
      Action.ConversationHighlight:new("Okay, that sounds fair."),
      Action.ConversationHighlight:new("9."),
      Action.ConversationHighlight:new("Totally positive."),
    },
    postconditions = { Condition.ConversationText:new("I do make exceptions for guardians") },
  },
  {
    text = "Pick up your cat.",
    warning = "Dev note: I don't have all kitten/cat data. Tracking for this step will break.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["any cat"]),
      Action.ConversationHighlight:new("Pick pet up."),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["any cat"]) },
  },
  {
    text = "Talk to the High Priest, in the temple to the south.",
    actions = {
      Action.Direction:new(3311, 4765, 2729, { distance = 12 }),
      Action.ModelHighlight:new(highPriest, { distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("I had better return this jar") },
  },
  {
    text = "Examine the canopic jar in your inventory. (Take note of the lid shape)",
    actions = { Action.InventoryHighlight:new(anyCanopicJar) },
    postconditions = { Condition.ChatText:new("lid shaped like a") },
  },
  {
    text = "Go back to the southern pyramid and open the door.",
    actions = {
      Action.Direction:new(3294.5, 941, 2781.5, { distance = 12 }),
      Action.ModelHighlight:new(pyramidEntrance, { distance = 12 }),
    },
    postconditions = { Condition.DistanceTo:new(3277, 965, 9170, 4) },
  },
  {
    text = "Go through the pyramid again.",
    actions = {
      Action.PathGuide:new({
        Location:new(3277, 965, 9170),
        Location:new(3289, 965, 9170),
        Location:new(3290, 965, 9171),
        Location:new(3291, 965, 9171),
        Location:new(3292, 965, 9170),
        Location:new(3293, 965, 9170),
        Location:new(3294, 965, 9171),
        Location:new(3299, 965, 9171),
        Location:new(3299, 965, 9170),
        Location:new(3302, 965, 9170),
        Location:new(3302, 965, 9171),
        Location:new(3305, 965, 9171),
        Location:new(3305, 965, 9170),
        Location:new(3308, 965, 9170),
        Location:new(3308, 965, 9181),
        Location:new(3304, 965, 9185),
        Location:new(3301, 965, 9185),
        Location:new(3301, 965, 9184),
        Location:new(3298, 965, 9184),
        Location:new(3298, 965, 9175),
        Location:new(3291, 965, 9175),
        Location:new(3291, 965, 9183),
        Location:new(3287, 965, 9183),
        Location:new(3287, 965, 9174),
        Location:new(3281, 965, 9174),
        Location:new(3277, 965, 9179),
        Location:new(3277, 965, 9188),
        Location:new(3291, 965, 9188),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3291, 965, 9191, 6) },
  },
  {
    text = "Jump across the pit.",
    warning = "This requires at least 20% run energy.",
    actions = { Action.Direction:new(3291.5, 1465, 9196) },
    postconditions = { Condition.DistanceTo:new(3280, 965, 9201, 4) },
  },
  {
    text = "Open the doorway of the western room.",
    actions = { Action.Direction:new(3280, 1465, 9199.5) },
    postconditions = { Condition.DistanceTo:new(3280, 1465, 9198, 1) },
  },
  {
    text = "Examine the jars on the ground and take the one that has the same examine as the one in your inventory previously.",
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.ChatText:new("a crocodile"),
      Condition.ChatText:new("a bug"),
      Condition.ChatText:new("an ape"),
      Condition.ModelVisible:new(anyApparition),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfApmeken) },
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.ChatText:new("a crocodile"),
      Condition.ChatText:new("a bug"),
      Condition.ModelVisible:new(anyApparition),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfScarabas) },
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.ChatText:new("a crocodile"),
      Condition.ModelVisible:new(anyApparition),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfCrondis) },
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.ModelVisible:new(anyApparition),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfHet) },
    postconditions = { Condition.ModelVisible:new(anyApparition) },
  },
  {
    text = "Kill the NPC that spawns.<ul><li>You can safe spot it by using the table in the room (unless you have the canopic jar of Apmeken).</li></ul>",
    actions = { Action.ModelHighlight:new(anyApparition) }, --only have het apparition data
    postconditions = { Condition.ModelNotVisible:new(anyApparition) },
  },
  {
    text = "Take the jar again.",
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.ChatText:new("a crocodile"),
      Condition.ChatText:new("a bug"),
      Condition.ChatText:new("an ape"),
      Condition.InventoryContains:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfApmeken) },
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.ChatText:new("a crocodile"),
      Condition.ChatText:new("a bug"),
      Condition.InventoryContains:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfScarabas) },
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.ChatText:new("a crocodile"),
      Condition.InventoryContains:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfCrondis) },
    postconditions = {
      Condition.ChatText:new("a man"),
      Condition.InventoryContains:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.ModelHighlight:new(canopicJarOfHet) },
    postconditions = { Condition.InventoryContains:new(anyCanopicJar) },
  },
  {
    text = "Exit the room",
    actions = { Action.Direction:new(3280, 1465, 9199.5) },
    postconditions = { Condition.DistanceTo:new(3291, 965, 9197, 3) },
  },
  {
    text = "Re-enter the west room.",
    actions = { Action.Direction:new(3280, 1465, 9199.5) },
    postconditions = { Condition.DistanceTo:new(3280, 965, 9197, 2) },
  },
  {
    text = "Left click the empty space with the highlighted outline of the jar to return it.",
    postconditions = {
      Condition.InventoryContains:new(canopicJarOfApmeken),
      Condition.InventoryContains:new(canopicJarOfHet),
      Condition.InventoryContains:new(canopicJarOfCrondis),
      Condition.InventoryContains:new(canopicJarOfScarabas),
      Condition.InventoryDoesNotContain:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.Direction:new(3286, 1165, 9193) },
    postconditions = {
      Condition.InventoryContains:new(canopicJarOfHet),
      Condition.InventoryContains:new(canopicJarOfCrondis),
      Condition.InventoryContains:new(canopicJarOfScarabas),
      Condition.InventoryDoesNotContain:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.Direction:new(3286, 1165, 9196) },
    postconditions = {
      Condition.InventoryContains:new(canopicJarOfHet),
      Condition.InventoryContains:new(canopicJarOfCrondis),
      Condition.InventoryDoesNotContain:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.Direction:new(3286, 1165, 9195) },
    postconditions = {
      Condition.InventoryContains:new(canopicJarOfHet),
      Condition.InventoryDoesNotContain:new(anyCanopicJar),
    },
  },
  {
    actions = { Action.Direction:new(3286, 1165, 9194) },
    postconditions = { Condition.InventoryDoesNotContain:new(anyCanopicJar) },
  },
  {
    text = "Exit the room.",
    actions = { Action.Direction:new(3280, 1465, 9199.5) },
    postconditions = { Condition.DistanceTo:new(3277, 965, 9171, 1) },
  },
  {
    text = "Climb the ladder.",
    actions = { Action.Direction:new(3277, 1465, 9171) },
    postconditions = { Condition.DistanceTo:new(3295, 365, 2782, 4) },
  },
  {
    text = "Talk to the High Priest.<ul><li>Optional: Equip a ghostspeak amulet and talk to the ghost near the pyramid for an achievement.</li></ul>",
    title = "Preparing for the Ceremony",
    actions = {
      Action.Direction:new(3311, 4765, 2729, { distance = 12 }),
      Action.ModelHighlight:new(highPriest, { distance = 12 }),
      Action.ConversationHighlight:new(""),
    },
    postconditions = { Condition.ConversationText:new("problems getting supplies") }, --not tested
  },
  {
    text = "Talk to the Embalmer at the spice shop, north-west of the temple.",
    actions = {
      Action.Direction:new(3282, 301, 2767, { distance = 12 }),
      Action.ModelHighlight:new(embalmer, { distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("north of Falador and") },
  },
  {
    text = "Talk to the Carpenter at the potter's wheel, just east of the southern pyramid.",
    actions = {
      Action.Direction:new(3312, 325, 2772, { distance = 4 }),
      Action.ModelHighlight:new(carpenter, { distance = 4 }),
      Action.ConversationHighlight:new("Alright, I'll get the wood for you."),
    },
    postconditions = { Condition.ConversationText:new("Great.") },
  },
  {
    text = "Talk to him again with a willow log.",
    actions = {
      Action.Direction:new(3312, 325, 2772, { distance = 4 }),
      Action.ModelHighlight:new(carpenter, { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("I'll be back") },
  },
  {
    text = "Talk to Raetul near the Sphinx to obtain linen.",
    actions = {
      Action.Direction:new(3304, 69, 2788, { distance = 12 }),
      Action.ModelHighlight:new(raetul, { distance = 12 }),
    },
    postconditions = { Condition.InventoryContains:new(linen) },
  },
  {
    text = "Talk to the Embalmer.",
    actions = {
      Action.Direction:new(3282, 301, 2767, { distance = 12 }),
      Action.ModelHighlight:new(embalmer, { distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("let the high priest know that") },
  },
  {
    text = "Talk to the Carpenter for a holy symbol.<ul><li>This may take a few seconds before he finishes the symbol.</li></ul>",
    actions = {
      Action.Direction:new(3312, 325, 2772, { distance = 4 }),
      Action.ModelHighlight:new(carpenter, { distance = 4 }),
    },
    postconditions = { Condition.InventoryContains:new(holySymbol) },
  },
  {
    text = "Enter the pyramid once again and jump over the pit.",
    title = "Icthlarin's Little Helper",
    actions = {
      Action.Direction:new(3294.5, 941, 2781.5, { distance = 12 }),
      Action.ModelHighlight:new(pyramidEntrance, { distance = 12 }),
    },
    postconditions = { Condition.DistanceTo:new(3277, 965, 9170, 4) },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(3277, 965, 9170),
        Location:new(3289, 965, 9170),
        Location:new(3290, 965, 9171),
        Location:new(3291, 965, 9171),
        Location:new(3292, 965, 9170),
        Location:new(3293, 965, 9170),
        Location:new(3294, 965, 9171),
        Location:new(3299, 965, 9171),
        Location:new(3299, 965, 9170),
        Location:new(3302, 965, 9170),
        Location:new(3302, 965, 9171),
        Location:new(3305, 965, 9171),
        Location:new(3305, 965, 9170),
        Location:new(3308, 965, 9170),
        Location:new(3308, 965, 9181),
        Location:new(3304, 965, 9185),
        Location:new(3301, 965, 9185),
        Location:new(3301, 965, 9184),
        Location:new(3298, 965, 9184),
        Location:new(3298, 965, 9175),
        Location:new(3291, 965, 9175),
        Location:new(3291, 965, 9183),
        Location:new(3287, 965, 9183),
        Location:new(3287, 965, 9174),
        Location:new(3281, 965, 9174),
        Location:new(3277, 965, 9179),
        Location:new(3277, 965, 9188),
        Location:new(3291, 965, 9188),
      }),
    },
    postconditions = { Condition.DistanceTo:new(3291, 965, 9191, 6) },
  },
  {
    text = "Jump across the pit.",
    warning = "This requires at least 20% run energy.",
    actions = { Action.Direction:new(3291.5, 1465, 9196) },
    postconditions = { Condition.DistanceTo:new(3280, 965, 9201, 4) },
  },
  {
    text = "Go to the east room and open the door.",
    actions = { Action.Direction:new(3306, 1465, 9199.5) },
    postconditions = { Condition.DistanceTo:new(3306, 965, 9197, 2) },
  },
  {
    text = "Search the first sarcophagus to your immediate west, on the northern wall, and hide the unholy symbol in it.<ul><li>Looting the other sarcophagi is optional.</li></ul>",
    actions = {
      Action.Direction:new(3309, 1465, 9198.5),
      Action.InventoryHighlight:new(unholySymbol),
      Action.ConversationHighlight:new("Hide the unholy symbol in this sarcophagus."),
    },
    postconditions = { Condition.ConversationText:new("foolish priests") },
  },
  {
    text = "Exit the room.",
    actions = { Action.Direction:new(3306, 1465, 9199.5) },
    postconditions = { Condition.DistanceTo:new(3291, 965, 9197, 4) },
  },
  {
    text = "Re-enter the east room.",
    actions = { Action.Direction:new(3306, 1465, 9199.5) },
    postconditions = { Condition.DistanceTo:new(3306, 965, 9197, 2) },
  },
  {
    text = "Talk to the High Priest.",
    actions = { Action.ModelHighlight:new(highPriest) },
    postconditions = {
      -- Condition.ConversationText:new("tsttt"),
      Condition.ModelVisible:new(priest, { quantity = 5 }), --not tested
    },
  },
  {
    text = "Kill the Possessed Priest.",
    postconditions = { Condition.ModelVisible:new(possessedPriestDrops) }, --drops different potion based on jar picked up
  },
  {
    text = "Talk to the High Priest.",
    actions = { Action.ModelHighlight:new(highPriest) },
    postconditions = { Condition.ConversationText:new("spirit of Klenter") },
  },
  {
    text = "Exit the room to return to the temple.<ul><li>You may be given the canopic jar again, but you can drop it when you are out of the pyramid.</li></ul>",
    actions = {
      Action.Direction:new(3306, 1465, 9199.5),
      Action.ConversationHighlight:new("Yes, return to the high priest in Sophanem."),
    },
    postconditions = { Condition.DistanceTo:new(3310, 4165, 2729, 4) },
  },
  {
    text = "Talk to the High Priest.",
    actions = {
      Action.Direction:new(3311, 4765, 2729, { distance = 12 }),
      Action.ModelHighlight:new(highPriest, { distance = 12 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Icthlarin's Little Helper",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1114473600,
  prereqQuests = { "Gertrude's Cat", "Diamond in the Rough" },
  questReqs = {},
  neededItems = {
    ["Linen"] = { quantity = 1, model = linen },
    ["Salt shaker (tool belt works)"] = { quantity = 1 },
    ["Bucket of sap"] = { quantity = 1, model = Models.items["bucket of sap"] },
    ["Willow log"] = { quantity = 1, model = Models.items["willow logs"] },
    ["Kitten/cat/overgrown cat/hellcat"] = { quantity = 1, model = Models.items["any cat"] },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Possessed Priest"] = { level = "56" },
    ["Apparitions"] = { level = "56-58" },
    ["Mummies (can skip)"] = { level = "70" },
    ["Scarab swarms (can skip)"] = { level = "12" },
  },
})
