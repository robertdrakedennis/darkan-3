local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
--#endregion
--#region Objects
local crystal = Model.new(576, {
  [125] = Vertex.new(-440, 556, 100, 14, 15, 35, 0.8745),
  [251] = Vertex.new(640, 356, -100, 14, 15, 35, 0.8745),
  [269] = Vertex.new(604, 400, 100, 14, 15, 35, 0.8745),
  [278] = Vertex.new(604, 400, 100, 14, 15, 35, 0.8745),
  [282] = Vertex.new(604, 400, 100, 14, 15, 35, 0.8745),
})
local rope = Model.new(168, {
  [3] = Vertex.new(232, 959, -16, 111, 100, 70),
  [5] = Vertex.new(232, 959, -16, 111, 100, 70),
  [9] = Vertex.new(232, 959, -16, 111, 100, 70),
  [26] = Vertex.new(248, 959, -16, 111, 100, 70),
  [41] = Vertex.new(220, 959, -16, 111, 100, 70),
})
local crystalCollectorLever = Model.new(144, {
  [12] = Vertex.new(-256, 584, 16, 89, 85, 81),
  [18] = Vertex.new(-256, 604, 16, 89, 85, 81),
  [19] = Vertex.new(-256, 604, 16, 89, 85, 81),
  [22] = Vertex.new(-256, 604, 16, 89, 85, 81),
  [24] = Vertex.new(-256, 616, 4, 89, 85, 81),
})
local crystalCollector = Model.new(138, {
  [26] = Vertex.new(5380, 1300, 7956, 95, 88, 60),
  [41] = Vertex.new(5380, 1360, 7916, 95, 88, 60),
  [45] = Vertex.new(5380, 1360, 7916, 95, 88, 60),
  [51] = Vertex.new(5144, 1444, 7952, 137, 128, 91),
  [120] = Vertex.new(5184, 1472, 7976, 137, 128, 91),
})
local lightBeamUp = Model.new(72, {
  [50] = Vertex.new(-8, 959, 16, 200, 192, 192, 0.1216),
  [56] = Vertex.new(8, 959, 16, 200, 192, 192, 0.1216),
  [62] = Vertex.new(16, 959, 0, 200, 192, 192, 0.1216),
  [68] = Vertex.new(8, 959, -16, 200, 192, 192, 0.1216),
  [72] = Vertex.new(8, 959, -16, 200, 192, 192, 0.1216),
})
local lightBeamUp2 = Model.new(72, {
  [38] = Vertex.new(8, 959, 16, 200, 192, 192, 0.1216),
  [39] = Vertex.new(-8, 959, 16, 200, 192, 192, 0.1216),
  [42] = Vertex.new(8, 959, 16, 200, 192, 192, 0.1216),
  [44] = Vertex.new(16, 959, 0, 200, 192, 192, 0.1216),
  [62] = Vertex.new(-16, 959, 0, 200, 192, 192, 0.1216),
})
local lightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 200, 192, 192, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 200, 192, 192, 0.2157),
  [45] = Vertex.new(16, 576, -256, 200, 192, 192, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 200, 192, 192, 0.2157),
  [69] = Vertex.new(8, 592, -256, 200, 192, 192, 0.2157),
})
local blueLightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 110, 246, 251, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 110, 246, 251, 0.2157),
  [45] = Vertex.new(16, 576, -256, 110, 246, 251, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 110, 246, 251, 0.2157),
  [69] = Vertex.new(8, 592, -256, 110, 246, 251, 0.2157),
})
local purpleLightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 110, 112, 251, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 110, 112, 251, 0.2157),
  [45] = Vertex.new(16, 576, -256, 110, 112, 251, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 110, 112, 251, 0.2157),
  [69] = Vertex.new(8, 592, -256, 110, 112, 251, 0.2157),
})
local redLightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 250, 56, 41, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 250, 56, 41, 0.2157),
  [45] = Vertex.new(16, 576, -256, 250, 56, 41, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 250, 56, 41, 0.2157),
  [69] = Vertex.new(8, 592, -256, 250, 56, 41, 0.2157),
})
local greenLightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 110, 251, 112, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 110, 251, 112, 0.2157),
  [45] = Vertex.new(16, 576, -256, 110, 251, 112, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 110, 251, 112, 0.2157),
  [69] = Vertex.new(8, 592, -256, 110, 251, 112, 0.2157),
})
local yellowLightBeam = Model.new(72, {
  [41] = Vertex.new(8, 592, -256, 251, 249, 110, 0.2157),
  [42] = Vertex.new(-8, 592, -256, 251, 249, 110, 0.2157),
  [45] = Vertex.new(16, 576, -256, 251, 249, 110, 0.2157),
  [59] = Vertex.new(-16, 576, -256, 251, 249, 110, 0.2157),
  [69] = Vertex.new(8, 592, -256, 251, 249, 110, 0.2157),
})
local openLightDoor = Model.new(1212, {
  [36] = Vertex.new(200, 624, -16, 254, 254, 254, 0.4902),
  [493] = Vertex.new(-256, 452, 0, 254, 254, 254, 0.05882),
  [496] = Vertex.new(-256, 452, 0, 254, 254, 254, 0.05882),
  [512] = Vertex.new(256, 452, 0, 254, 254, 254, 0.05882),
  [573] = Vertex.new(-200, 612, 32, 254, 254, 254, 0.3725),
})
local deathRuins = Model.new(885, {
  [310] = Vertex.new(2392, 570, 7551, 137, 126, 125),
  [347] = Vertex.new(2471, 669, 7707, 137, 126, 125),
  [422] = Vertex.new(2367, 345, 8036, 137, 126, 125),
  [788] = Vertex.new(3103, 689, 8394, 137, 126, 125),
  [826] = Vertex.new(2859, 390, 8342, 137, 126, 125),
})
local deathAltar = Model.new(42, {
  [1] = Vertex.new(165, 630, -330, 115, 114, 105),
  [5] = Vertex.new(-378, 546, 234, 115, 114, 105),
  [16] = Vertex.new(-262, 554, 326, 115, 114, 105),
  [38] = Vertex.new(470, 691, 264, 115, 114, 105),
  [42] = Vertex.new(470, 691, 264, 115, 114, 105),
})
local deathAltarPortal = Model.new(1572, {
  [11] = Vertex.new(-192, 152, 160, 178, 63, 55, 0.1765),
  [14] = Vertex.new(168, 152, 176, 178, 63, 55, 0.1765),
  [359] = Vertex.new(-228, 152, -108, 178, 63, 55, 0.1765),
  [389] = Vertex.new(-140, 140, -208, 178, 63, 55, 0.1765),
  [1463] = Vertex.new(136, 144, -212, 178, 63, 55, 0.1765),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local newKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 153, 121, 13),
  [104] = Vertex.new(68, 16, 68, 153, 121, 13),
  [397] = Vertex.new(68, 16, 68, 153, 121, 13),
  [400] = Vertex.new(68, 16, 68, 153, 121, 13),
  [408] = Vertex.new(68, 16, 68, 153, 121, 13),
})
local edernsJournal = Model.new(204, {
  [87] = Vertex.new(36, 36, -84, 81, 45, 7),
  [159] = Vertex.new(36, 36, -84, 110, 109, 9),
  [171] = Vertex.new(64, 36, 56, 110, 109, 9),
  [201] = Vertex.new(-32, 52, 32, 121, 129, 99),
  [203] = Vertex.new(32, 52, 52, 121, 129, 99),
})
local blackenedCrystal = Model.new(96, {
  [5] = Vertex.new(24, 48, 24, 69, 64, 63, 0.8039),
  [12] = Vertex.new(24, 48, -24, 69, 64, 63, 0.8039),
  [17] = Vertex.new(24, 48, -24, 69, 64, 63, 0.8039),
  [24] = Vertex.new(-24, 48, -24, 69, 64, 63, 0.8039),
  [29] = Vertex.new(-24, 48, -24, 69, 64, 63, 0.8039),
})
local newlyMadeCrystal = Model.new(96, {
  [5] = Vertex.new(24, 48, 24, 222, 217, 217, 0.8039),
  [12] = Vertex.new(24, 48, -24, 222, 217, 217, 0.8039),
  [17] = Vertex.new(24, 48, -24, 222, 217, 217, 0.8039),
  [24] = Vertex.new(-24, 48, -24, 222, 217, 217, 0.8039),
  [29] = Vertex.new(-24, 48, -24, 222, 217, 217, 0.8039),
})
local mirror = Model.multi({
  Model.new(522, {
    [71] = Vertex.new(16, 0, -64, 64, 55, 12),
    [74] = Vertex.new(16, 0, -64, 64, 55, 12),
    [75] = Vertex.new(-16, 0, -64, 64, 55, 12),
    [78] = Vertex.new(16, 0, -64, 64, 55, 12),
    [80] = Vertex.new(-16, 0, -64, 64, 55, 12),
  }),
  Model.new(12, {
    [1] = Vertex.new(8, 8, 56, 161, 148, 148, 0.4980),
    [3] = Vertex.new(-56, 8, 8, 161, 148, 148, 0.4980),
    [5] = Vertex.new(40, 8, 40, 161, 148, 148, 0.4980),
    [9] = Vertex.new(56, 8, -8, 161, 148, 148, 0.4980),
    [11] = Vertex.new(-40, 8, -40, 161, 148, 148, 0.4980),
  }),
})
local yellowCrystal = Model.new(96, {
  [5] = Vertex.new(24, 48, 24, 216, 213, 19, 0.8039),
  [12] = Vertex.new(24, 48, -24, 216, 213, 19, 0.8039),
  [17] = Vertex.new(24, 48, -24, 216, 213, 19, 0.8039),
  [24] = Vertex.new(-24, 48, -24, 216, 213, 19, 0.8039),
  [29] = Vertex.new(-24, 48, -24, 216, 213, 19, 0.8039),
})
local cyanCrystal = Model.new(96, {
  [5] = Vertex.new(24, 48, 24, 19, 209, 216, 0.8039),
  [12] = Vertex.new(24, 48, -24, 19, 209, 216, 0.8039),
  [17] = Vertex.new(24, 48, -24, 19, 209, 216, 0.8039),
  [24] = Vertex.new(-24, 48, -24, 19, 209, 216, 0.8039),
  [29] = Vertex.new(-24, 48, -24, 19, 209, 216, 0.8039),
})
local fracturedCrystal = Model.new(96, {
  [6] = Vertex.new(-32, 48, -24, 221, 217, 217, 0.4902),
  [11] = Vertex.new(-32, 48, -24, 221, 217, 217, 0.4902),
  [42] = Vertex.new(20, 48, -36, 221, 217, 217, 0.4902),
  [47] = Vertex.new(20, 48, -36, 221, 217, 217, 0.4902),
  [50] = Vertex.new(-32, 48, -24, 221, 217, 217, 0.8039),
})
local blueCrystal = Model.new(96, {
  [5] = Vertex.new(24, 48, 24, 19, 24, 216, 0.8039),
  [12] = Vertex.new(24, 48, -24, 19, 24, 216, 0.8039),
  [17] = Vertex.new(24, 48, -24, 19, 24, 216, 0.8039),
  [24] = Vertex.new(-24, 48, -24, 19, 24, 216, 0.8039),
  [29] = Vertex.new(-24, 48, -24, 19, 24, 216, 0.8039),
})
local fracturedCrystal2 = Model.new(96, {
  [5] = Vertex.new(-32, 52, -28, 221, 217, 217, 0.8039),
  [48] = Vertex.new(-32, 52, -28, 221, 217, 217, 0.8039),
  [51] = Vertex.new(-32, 52, -28, 221, 217, 217, 0.8039),
  [91] = Vertex.new(-32, 52, -28, 221, 217, 217, 0.8039),
  [95] = Vertex.new(-32, 52, -28, 221, 217, 217, 0.8039),
})
local poweredNewlyMadeCrystal = Model.new(192, {
  [101] = Vertex.new(28, 60, 28, 222, 218, 217, 0.3686),
  [108] = Vertex.new(28, 60, -28, 222, 218, 217, 0.3686),
  [113] = Vertex.new(28, 60, -28, 222, 218, 217, 0.3686),
  [120] = Vertex.new(-28, 60, -28, 222, 218, 217, 0.3686),
  [125] = Vertex.new(-28, 60, -28, 222, 218, 217, 0.3686),
})
--#endregion

local pillars = {
  [0] = Model.new(492, {
    [420] = Vertex.new(-32, 576, -260, 14, 12, 12),
    [424] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [431] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [432] = Vertex.new(0, 612, -260, 14, 12, 12),
    [434] = Vertex.new(20, 600, -260, 14, 12, 12),
  }),
  [1] = Model.new(468, {
    [400] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [407] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [408] = Vertex.new(0, 612, -260, 14, 12, 12),
    [410] = Vertex.new(20, 600, -260, 14, 12, 12),
    [422] = Vertex.new(260, 600, 20, 14, 12, 12),
  }),
  [2] = Model.new(606, {
    [488] = Vertex.new(-260, 600, -20, 14, 12, 12),
    [493] = Vertex.new(-260, 600, -20, 14, 12, 12),
    [500] = Vertex.new(-260, 600, 20, 14, 12, 12),
    [514] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [524] = Vertex.new(20, 600, -260, 14, 12, 12),
  }),
  [3] = Model.new(537, {
    [445] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [452] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [453] = Vertex.new(0, 612, -260, 14, 12, 12),
    [455] = Vertex.new(20, 600, -260, 14, 12, 12),
    [467] = Vertex.new(260, 600, 20, 14, 12, 12),
  }),
  [4] = Model.new(492, {
    [420] = Vertex.new(-32, 576, -260, 14, 12, 12),
    [424] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [431] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [432] = Vertex.new(0, 612, -260, 14, 12, 12),
    [434] = Vertex.new(20, 600, -260, 14, 12, 12),
  }),
  [5] = Model.new(468, {
    [400] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [407] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [408] = Vertex.new(0, 612, -260, 14, 12, 12),
    [410] = Vertex.new(20, 600, -260, 14, 12, 12),
    [422] = Vertex.new(260, 600, 20, 14, 12, 12),
  }),
  [6] = Model.new(561, {
    [469] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [476] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [477] = Vertex.new(0, 612, -260, 14, 12, 12),
    [479] = Vertex.new(20, 600, -260, 14, 12, 12),
    [491] = Vertex.new(260, 600, 20, 14, 12, 12),
  }),
  [7] = Model.new(468, {
    [396] = Vertex.new(-32, 576, -260, 13, 12, 12),
    [400] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [407] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [408] = Vertex.new(0, 612, -260, 13, 12, 12),
    [410] = Vertex.new(20, 600, -260, 13, 12, 12),
  }),
  [8] = Model.new(468, {
    [400] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [407] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [408] = Vertex.new(0, 612, -260, 13, 12, 12),
    [410] = Vertex.new(20, 600, -260, 13, 12, 12),
    [422] = Vertex.new(260, 600, 20, 13, 12, 12),
  }),
  [9] = Model.new(492, {
    [424] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [431] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [432] = Vertex.new(0, 612, -260, 13, 12, 12),
    [434] = Vertex.new(20, 600, -260, 13, 12, 12),
    [446] = Vertex.new(260, 600, 20, 13, 12, 12),
  }),
  [10] = Model.new(537, {
    [445] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [452] = Vertex.new(-20, 600, -260, 13, 12, 12),
    [453] = Vertex.new(0, 612, -260, 13, 12, 12),
    [455] = Vertex.new(20, 600, -260, 13, 12, 12),
    [467] = Vertex.new(260, 600, 20, 13, 12, 12),
  }),
  [11] = Model.new(492, {
    [424] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [431] = Vertex.new(-20, 600, -260, 14, 12, 12),
    [432] = Vertex.new(0, 612, -260, 14, 12, 12),
    [434] = Vertex.new(20, 600, -260, 14, 12, 12),
    [446] = Vertex.new(260, 600, 20, 14, 12, 12),
  }),
  [12] = Model.new(468, {
    [417] = Vertex.new(260, 576, 32, 14, 12, 12),
    [422] = Vertex.new(260, 600, 20, 14, 12, 12),
    [427] = Vertex.new(260, 600, 20, 14, 12, 12),
    [429] = Vertex.new(260, 612, 0, 14, 12, 12),
    [434] = Vertex.new(260, 600, -20, 14, 12, 12),
  }),
  [13] = Model.new(444, {
    [393] = Vertex.new(260, 576, 32, 14, 12, 12),
    [398] = Vertex.new(260, 600, 20, 14, 12, 12),
    [403] = Vertex.new(260, 600, 20, 14, 12, 12),
    [405] = Vertex.new(260, 612, 0, 14, 12, 12),
    [410] = Vertex.new(260, 600, -20, 14, 12, 12),
  }),
  [14] = Model.new(630, {
    [512] = Vertex.new(-260, 600, -20, 14, 13, 13),
    [517] = Vertex.new(-260, 600, -20, 14, 13, 13),
    [524] = Vertex.new(-260, 600, 20, 14, 13, 13),
    [538] = Vertex.new(-20, 600, -260, 14, 13, 13),
    [548] = Vertex.new(20, 600, -260, 14, 13, 13),
  }),
}

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Arianwyn in Lletya.",
    title = "Starting out",
    neededItems = {},
    recommendedItems = {},
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    actions = { Action.Direction:new(2352, 2069, 3172) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["arianwyn"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.ConversationHighlight:new("Talk about Mourning's End"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["arianwyn"]) },
    jumpOffset = -1,
    postconditions = {
      Condition.ConversationText:new("with the Head Mourner"),
      Condition.ConversationText:new("You must stop them"),
    },
  },
  {
    text = "Enter the Mourners' headquarters in the north-eastern corner of West Ardougne.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2551, 1785, 3320.4) },
    postconditions = {
      Condition.DistanceTo:new(2551, 1285, 3322, 1),
      Condition.DistanceTo:new(2045, 7973, 4629, 20),
    },
  },
  {
    text = "Descend the trapdoor.",
    actions = { Action.Direction:new(2542, 1285, 3327, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2045, 7973, 4629, 20) },
  },
  {
    text = "Talk to the Head Mourner.",
    actions = { Action.ModelHighlight:new(Models.npcs["head mourner"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Head Mourner.",
    actions = { Action.ModelHighlight:new(Models.npcs["head mourner"]) },
    postconditions = { Condition.ConversationText:new("Good Luck!") },
  },
  {
    text = "Run west through the cave.",
    title = "Blackened crystal",
    neededItems = {
      ["Mourner gear"] = { quantity = 1 },
    },
    recommendedItems = {
      ["Food"] = { quantity = 1 },
      ["Crystal teleport seed"] = { quantity = 1 },
    },
    actions = {
      Action.PathGuide:new({
        Location:new(2041, 7973, 4636),
        Location:new(2035, 7973, 4636),
        Location:new(2032, 7973, 4636),
        Location:new(2026, 7135, 4636),
        Location:new(2018, 6319, 4636),
        Location:new(2012, 5895, 4638),
        Location:new(2003, 6021, 4638),
        Location:new(1997, 5764, 4632),
        Location:new(1991, 5125, 4626),
        Location:new(1985, 4805, 4626),
        Location:new(1974, 3589, 4627),
        Location:new(1969, 3613, 4631),
        Location:new(1962, 3229, 4634),
        Location:new(1953, 3373, 4634),
        Location:new(1946, 3197, 4636),
        Location:new(1946, 3197, 4636),
        Location:new(1942, 3193, 4636),
      }),
    },
    postconditions = { Condition.ConversationText:new("doesn't find out") },
  },
  {
    text = "Search the bodies.",
    actions = {
      Action.Direction:new(1931, 1709, 4639.5),
      Action.Direction:new(1924, 589, 4639.5),
      Action.Direction:new(1925, 717, 4642),
      Action.Direction:new(1920.5, 349, 4638),
      Action.Direction:new(1926, 709, 4634.5),
      Action.Direction:new(1925, 581, 4633.5),
      Action.Direction:new(1922, 357, 4630.5),
    },
    postconditions = { Condition.InventoryContains:new(edernsJournal) },
  },
  {
    text = "Climb up the staircase.", --entrance
    actions = { Action.Direction:new(1903.5, 405, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1901, 997, 4638, 4) },
  },
  {
    text = "Climb up ladder to the south.",
    actions = { Action.Direction:new(1898, 1497, 4610.5) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 1989, 4612, 4) },
  },
  {
    text = "Climb down the staircase to the north.",
    actions = { Action.Direction:new(1891, 1989, 4635) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 997, 4638, 4) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(1891, 1297, 4641) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 1989, 4644, 4) },
  },
  {
    text = "Search the crystal.",
    actions = {
      Action.ModelHighlight:new(crystal),
      Action.ConversationHighlight:new("Try to chisel a shard off the crystal."),
    },
    postconditions = { Condition.InventoryContains:new(blackenedCrystal) },
  },
  {
    text = "Talk to Arianwyn in Lletya.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    actions = { Action.Direction:new(2352, 2069, 3172) },
    postconditions = { Condition.ModelVisible:new(Models.npcs["arianwyn"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["arianwyn"]),
      Action.ConversationHighlight:new("Talk about Mourning's End"),
    },
    jumpconditions = { Condition.ModelNotVisible:new(Models.npcs["arianwyn"]) },
    jumpOffset = -1,
    postconditions = {
      Condition.ConversationText:new("I shall take a look"),
    },
  },
  {
    text = "Use the blackened crystal on Eluned.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["eluned"]),
      Action.InventoryHighlight:new(blackenedCrystal),
    },
    postconditions = { Condition.ConversationText:new("a few seconds") },
  },
  {
    text = "Talk to Eluned again.",
    actions = { Action.ModelHighlight:new(Models.npcs["eluned"]) },
    postconditions = { Condition.InventoryContains:new(newlyMadeCrystal) },
  },
  {
    text = "Talk to Arianwyn again.",
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    postconditions = { Condition.ConversationText:new("get to it") },
  },
  {
    text = "Go back to the Temple of Light.",
    title = "Temple of Light",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Ardougne lodestone",
      url = "Ardougne_lodestone_icon.png",
    },
    neededItems = {
      ["Rope"] = { quantity = 1 },
      ["Newly made crystal"] = { quantity = 1, model = newlyMadeCrystal },
      ["New key"] = { quantity = 1, model = newKey },
      ["Mourner gear"] = { quantity = 1 },
    },
    recommendedItems = {
      ["Lots of food"] = { quantity = 1 },
      ["Bunyip"] = { quantity = 1 },
      ["Beast of burden"] = { quantity = 1 },
      ["Death/Omni talisman"] = { quantity = 1 },
      ["Agility boost"] = { quantity = 1 },
      ["Weight-reducing clothing"] = { quantity = 1 },
      ["Any shield"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2551, 1785, 3320.4) },
    postconditions = {
      Condition.DistanceTo:new(2551, 1285, 3322, 1),
      Condition.DistanceTo:new(2045, 7973, 4629, 20),
    },
  },
  {
    actions = { Action.Direction:new(2542, 1285, 3327, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2045, 7973, 4629, 20) },
  },
  {
    actions = {
      Action.PathGuide:new({
        Location:new(2044, 7973, 4649),
        Location:new(2044, 7973, 4636),
        Location:new(2044, 7973, 4636),
        Location:new(2035, 7973, 4636),
        Location:new(2032, 7973, 4636),
        Location:new(2026, 7135, 4636),
        Location:new(2018, 6319, 4636),
        Location:new(2012, 5895, 4638),
        Location:new(2003, 6021, 4638),
        Location:new(1997, 5764, 4632),
        Location:new(1991, 5125, 4626),
        Location:new(1985, 4805, 4626),
        Location:new(1974, 3589, 4627),
        Location:new(1969, 3613, 4631),
        Location:new(1962, 3229, 4634),
        Location:new(1953, 3373, 4634),
        Location:new(1946, 3197, 4636),
        Location:new(1946, 3197, 4636),
        Location:new(1946, 3197, 4636),
        Location:new(1939, 2485, 4640),
        Location:new(1926, 645, 4640),
        Location:new(1918, 5, 4639),
        Location:new(1905, 5, 4639),
      }),
    },
    postconditions = { Condition.DistanceTo:new(1901, 997, 4638, 4) },
  },
  {
    text = "Tie a rope to the rock.",
    actions = { Action.Direction:new(1876, 997, 4620) },
    postconditions = { Condition.ModelVisible:new(rope) },
  },
  {
    text = "Pull the crystal collector lever to reset the puzzle.",
    actions = { Action.ModelHighlight:new(crystalCollectorLever) },
    postconditions = { Condition.ChatText:new("pull the lever") },
  },
  {
    text = "Collect with the crystal collector.<ul><li>Drop your new key if you need inventory space.</li></ul>",
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(yellowCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
    },
    postconditions = { Condition.InventoryContains:new(mirror, 4) },
  },
  {
    text = "Mirror #1 points north.",
    title = "Cyan crystal",
    neededItems = {
      ["Yellow crystal"] = { quantity = 1, model = yellowCrystal },
      ["Mirror"] = { quantity = 4, model = mirror },
    },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(pillars[0], { atLocation = Location:new(1909, 997, 4639) }),
      Action.ModelHighlight:new(pillars[7], { atLocation = Location:new(1909, 997, 4639) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1909, 1497, 4643) }) },
  },
  {
    text = "Mirror #2 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[1], { atLocation = Location:new(1909, 997, 4650) }),
      Action.ModelHighlight:new(pillars[4], { atLocation = Location:new(1909, 997, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1904, 1497, 4650) }) },
  },
  {
    text = "Mirror #3 points south.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1898, 1497, 4644) }) },
  },
  {
    text = "Place the yellow crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4628) }),
      Action.InventoryHighlight:new(yellowCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(yellowCrystal) },
  },
  {
    text = "Mirror #4 points east.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1898, 997, 4613) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(openLightDoor) },
  },
  {
    text = "Cross the gap to the other side.<ul><li>Use any agility boosts you have.</li></ul>",
    actions = {
      Action.PathGuide:new({
        Location:new(1901, 1747, 4611.5),
        Location:new(1911, 1747, 4611.5),
      }),
      Action.ModelHighlight:new(openLightDoor),
    },
    postconditions = { Condition.DistanceTo:new(1914, 997, 4613, 1) },
  },
  {
    text = "Open and search the chest.",
    actions = { Action.Direction:new(1917, 997, 4613, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(cyanCrystal) },
  },
  {
    text = "Reset the puzzle at the crystal dispenser and collect all items.",
    actions = { Action.ModelHighlight:new(crystalCollectorLever) },
    postconditions = { Condition.ChatText:new("pull the lever") },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(cyanCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(yellowCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
    },
    postconditions = { Condition.InventoryContains:new(mirror, 6) },
  },
  {
    text = "Mirror #1 points north.",
    title = "Fractured crystal",
    neededItems = {
      ["Yellow crystal"] = { quantity = 1 },
      ["Cyan crystal"] = { quantity = 1, model = cyanCrystal },
      ["Mirror"] = { quantity = 6 },
    },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(pillars[0], { atLocation = Location:new(1909, 997, 4639) }),
      Action.ModelHighlight:new(pillars[7], { atLocation = Location:new(1909, 997, 4639) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1909, 1497, 4643) }) },
  },
  {
    text = "Mirror #2 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[1], { atLocation = Location:new(1909, 997, 4650) }),
      Action.ModelHighlight:new(pillars[4], { atLocation = Location:new(1909, 997, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1904, 1497, 4650) }) },
  },
  {
    text = "Place the cyan crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4650) }),
      Action.InventoryHighlight:new(cyanCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(cyanCrystal) },
  },
  {
    text = "Mirror #3 points north.",
    actions = {
      Action.ModelHighlight:new(pillars[8], { atLocation = Location:new(1887, 997, 4650) }),
      Action.ModelHighlight:new(pillars[9], { atLocation = Location:new(1887, 997, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(blueLightBeam, { atLocation = Location:new(1887, 1497, 4657) }) },
  },
  {
    text = "Mirror #4 points east.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1887, 997, 4665) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(blueLightBeam, { atLocation = Location:new(1892, 1497, 4665) }) },
  },
  {
    text = "Place the yellow crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1897, 997, 4665) }),
      Action.InventoryHighlight:new(yellowCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(yellowCrystal) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1914, 997, 4665, 1) },
  },
  {
    text = "Open and search the chest.",
    actions = { Action.Direction:new(1917, 997, 4665, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(mirror, 4) },
  },
  {
    text = "Remove the yellow crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1897, 997, 4665) }),
      Action.ConversationHighlight:new("Take the crystal."),
    },
    postconditions = { Condition.InventoryContains:new(yellowCrystal) },
  },
  {
    text = "Mirror #5 points up.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1897, 997, 4665) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(pillars[6]) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(1898, 1497, 4667.4) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 1989, 4666, 4) },
  },
  {
    text = "Mirror #6 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1898, 1989, 4665) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(purpleLightBeam) }, --not happy with this
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(1898, 2489, 4667.4) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 997, 4667, 4) },
  },
  {
    text = "Climb up the ladder to the south.",
    actions = { Action.Direction:new(1898, 1497, 4610.6) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 1989, 4612, 4) },
  },
  {
    text = "Mirror #7 points down (big mirror side east).",
    warning = "No tracking for this step",
    actions = {
      Action.Direction:new(1860, 2589, 4665),
      Action.ModelHighlight:new(pillars[8], { atLocation = Location:new(1860, 1989, 4665) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    -- postconditions = { Condition.InventoryDoesNotContain:new(mirror, 2) }, --too buggy
  },
  {
    text = "Climb down the ladder to the south.",
    actions = { Action.Direction:new(1898, 2489, 4610.6) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 997, 4611, 4) },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(1903, 997, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1905, 5, 4639, 4) },
  },
  {
    text = "Pass through the light door to the north-west.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1861, 5, 4665, 1) },
  },
  {
    text = "Turn Mirror #8 to point south.",
    actions = {
      Action.ModelHighlight:new(pillars[0]),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(openLightDoor, { atLocation = Location:new(1860, 5, 4662) }) },
  },
  {
    text = "Open and search the chest.",
    actions = {
      Action.Direction:new(1880, 5, 4659, { tile = true }),
      Action.ModelHighlight:new(openLightDoor),
    },
    postconditions = { Condition.InventoryContains:new(fracturedCrystal) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1860, 5, 4664, 1) },
  },
  {
    text = "Rotate the mirror east.",
    actions = {
      Action.ModelHighlight:new(pillars[0]),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(openLightDoor, { atLocation = Location:new(1863, 5, 4665) }) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1865, 5, 4665, 1) },
  },
  {
    text = "Climb up the staircase.",
    title = "Blue crystal",
    actions = { Action.Direction:new(1903.5, 405, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1901, 997, 4638, 4) },
  },
  {
    text = "Remove the cyan crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4650) }),
      Action.ConversationHighlight:new("Take the crystal."),
    },
    postconditions = { Condition.InventoryContains:new(cyanCrystal) },
  },
  {
    text = "Place the yellow crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4650) }),
      Action.InventoryHighlight:new(yellowCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(yellowCrystal) },
  },
  {
    text = "Climb up the ladder to the south.",
    actions = { Action.Direction:new(1898, 1497, 4610.6) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 1989, 4612, 4) },
  },
  {
    text = "Rotate Mirror #7 south. This light should be red.",
    actions = {
      Action.Direction:new(1860, 2589, 4665),
      Action.ModelHighlight:new(pillars[8], { atLocation = Location:new(1860, 1989, 4665) }),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(redLightBeam, { atLocation = Location:new(1860, 1989, 4657) }) },
  },
  {
    text = "Mirror #8 point downs (big mirror side north).",
    warning = "No tracking for this step.",
    actions = {
      Action.ModelHighlight:new(pillars[8], { atLocation = Location:new(1860, 1989, 4613) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
  },
  {
    text = "Climb down the ladder to the south.",
    actions = { Action.Direction:new(1898, 2489, 4610.6) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 997, 4611, 4) },
  },
  {
    text = "Climb down the rope shortcut.",
    actions = { Action.Direction:new(1876, 997, 4620) },
    postconditions = { Condition.DistanceToWithHeight:new(1878, 5, 4620, 4) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1860, 5, 4614, 1) },
  },
  {
    text = "Open and search the chest.",
    actions = { Action.Direction:new(1858, 5, 4613, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(blueCrystal) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1860, 5, 4618, 1) },
  },
  {
    text = "Climb up rope.",
    actions = { Action.ModelHighlight:new(rope) },
    postconditions = { Condition.DistanceToWithHeight:new(1876, 997, 4620, 8) },
  },
  {
    text = "Reset the puzzle at the crystal dispenser and collect all items.",
    actions = { Action.ModelHighlight:new(crystalCollectorLever) },
    postconditions = { Condition.ChatText:new("pull the lever") },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(cyanCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(yellowCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(fracturedCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(blueCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
    },
    postconditions = { Condition.InventoryContains:new(mirror, 10) },
  },
  {
    text = "Mirror #1 points north.",
    title = "Second fractured crystal",
    neededItems = {
      ["Yellow crystal"] = { quantity = 1 },
      ["Cyan crystal"] = { quantity = 1 },
      ["Blue crystal"] = { quantity = 1, model = blueCrystal },
      ["Fractured crystal"] = { quantity = 1, model = fracturedCrystal },
      ["Mirror"] = { quantity = 10 },
    },
    recommendedItems = {},
    actions = {
      Action.ModelHighlight:new(pillars[0], { atLocation = Location:new(1909, 997, 4639) }),
      Action.ModelHighlight:new(pillars[7], { atLocation = Location:new(1909, 997, 4639) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1909, 1497, 4643) }) },
  },
  {
    text = "Mirror #2 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[1], { atLocation = Location:new(1909, 997, 4650) }),
      Action.ModelHighlight:new(pillars[4], { atLocation = Location:new(1909, 997, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1904, 1497, 4650) }) },
  },
  {
    text = "Mirror #3 points south.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1898, 1497, 4644) }) },
  },
  {
    text = "Place the yellow crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4628) }),
      Action.InventoryHighlight:new(yellowCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(yellowCrystal) },
  },
  {
    text = "Mirror #4 points east.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1898, 997, 4613) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(openLightDoor) },
  },
  {
    text = "Cross the gap to the other side.<ul><li>Use any agility boosts you have.</li></ul>",
    actions = {
      Action.PathGuide:new({
        Location:new(1901, 1747, 4611.5),
        Location:new(1911, 1747, 4611.5),
      }),
      Action.ModelHighlight:new(openLightDoor),
    },
    postconditions = { Condition.DistanceTo:new(1914, 997, 4613, 1) },
  },
  {
    text = "Place the blue crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[5], { atLocation = Location:new(1915, 997, 4613) }),
      Action.InventoryHighlight:new(blueCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(blueCrystal) },
  },
  {
    text = "Go back across the gap.",
    actions = {
      Action.PathGuide:new({
        Location:new(1901, 1747, 4611.5),
        Location:new(1911, 1747, 4611.5),
      }),
    },
    postconditions = { Condition.DistanceTo:new(1900, 997, 4613, 1) },
  },
  {
    text = "Remove Mirror #4.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1898, 997, 4613) }),
      Action.ConversationHighlight:new("Take the mirror."),
    },
    postconditions = { Condition.InventoryContains:new(mirror, 7) },
  },
  {
    text = "Remove the yellow crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4628) }),
      Action.ConversationHighlight:new("Take the crystal."),
    },
    postconditions = { Condition.InventoryContains:new(yellowCrystal) },
  },
  {
    text = "Rotate Mirror #3 pointing up.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 997, 4650) }),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeamUp) },
  },
  {
    text = "Climb up the ladder to the south.",
    actions = { Action.Direction:new(1898, 1497, 4610.6) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 1989, 4612, 4) },
  },
  {
    text = "Mirror #4 points south.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 1989, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1898, 2389, 4642) }) },
  },
  {
    text = "Place the fractured crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 1989, 4628) }),
      Action.InventoryHighlight:new(fracturedCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(fracturedCrystal) },
  },
  {
    text = "Mirror #5 points down (big mirror side east).",
    warning = "No tracking for this step.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1887, 1989, 4628) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    -- postconditions={}
  },
  {
    text = "Mirror #6 points east.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1898, 1989, 4613) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1906, 1989, 4613) }) },
  },
  {
    text = "Mirror #7 points down (big mirror side west).",
    warning = "No tracking for this step.",
    actions = {
      Action.ModelHighlight:new(pillars[8], { atLocation = Location:new(1914, 1989, 4613) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    -- postconditions = {},
  },
  {
    text = "Climb down the ladder to the south.",
    actions = { Action.Direction:new(1898, 2489, 4610.6) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 997, 4611, 4) },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(1903, 997, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1905, 5, 4639, 4) },
  },
  {
    text = "Mirror #8 points south.",
    actions = {
      Action.ModelHighlight:new(pillars[11], { atLocation = Location:new(1887, 5, 4628) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(greenLightBeam, { atLocation = Location:new(1887, 405, 4622) }) },
  },
  {
    text = "Mirror #9 points east.",
    actions = {
      Action.ModelHighlight:new(pillars[3], { atLocation = Location:new(1887, 5, 4613) }),
      Action.ModelHighlight:new(pillars[6], { atLocation = Location:new(1887, 5, 4613) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(greenLightBeam, { atLocation = Location:new(1895, 405, 4613) }) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1914, 5, 4613, 1) },
  },
  {
    text = "Mirror #10 points north.",
    actions = {
      Action.ModelHighlight:new(pillars[11], { atLocation = Location:new(1915, 5, 4613) }),
      Action.ModelHighlight:new(pillars[6], { atLocation = Location:new(1915, 5, 4613) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(openLightDoor, { atLocation = Location:new(1915, 5, 4616) }) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1915, 5, 4616) }) },
    postconditions = { Condition.DistanceTo:new(1914, 5, 4618, 1) },
  },
  {
    text = "Open and search the chest.",
    actions = { Action.Direction:new(1910, 5, 4622, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(fracturedCrystal2) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(1903.5, 405, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1901, 997, 4638, 4) },
  },
  {
    text = "Reset the puzzle at the crystal dispenser and collect all items.",
    actions = { Action.ModelHighlight:new(crystalCollectorLever) },
    postconditions = { Condition.ChatText:new("pull the lever") },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
    },
    postconditions = { Condition.InventoryContains:new(cyanCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
      Action.ConversationHighlight:new("Take the crystals."),
    },
    postconditions = { Condition.InventoryContains:new(yellowCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
      Action.ConversationHighlight:new("Take the crystals."),
    },
    postconditions = { Condition.InventoryContains:new(fracturedCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
      Action.ConversationHighlight:new("Take the crystals."),
    },
    postconditions = { Condition.InventoryContains:new(fracturedCrystal2) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
      Action.ConversationHighlight:new("Take the crystals."),
    },
    postconditions = { Condition.InventoryContains:new(blueCrystal) },
  },
  {
    actions = {
      Action.ModelHighlight:new(crystalCollector),
      Action.ConversationHighlight:new("Take all the items."),
      Action.ConversationHighlight:new("Take the mirrors."),
      Action.ConversationHighlight:new("Take the crystals."),
    },
    postconditions = { Condition.InventoryContains:new(mirror, 13) },
  },
  {
    text = "Mirror #1 points north.",
    title = "Death altar",
    neededItems = {
      ["Yellow crystal"] = { quantity = 1 },
      ["Cyan crystal"] = { quantity = 1 },
      ["Blue crystal"] = { quantity = 1 },
      ["Fractured crystal"] = { quantity = 1 },
      ["Second fractured crystal"] = { quantity = 1, model = fracturedCrystal2 },
      ["Mirror"] = { quantity = 13 },
    },
    actions = {
      Action.ModelHighlight:new(pillars[0], { atLocation = Location:new(1909, 997, 4639) }),
      Action.ModelHighlight:new(pillars[7], { atLocation = Location:new(1909, 997, 4639) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1909, 1497, 4643) }) },
  },
  {
    text = "Mirror #2 points down (big mirror side south)",
    warning = "No tracking for this step.",
    actions = {
      Action.ModelHighlight:new(pillars[1], { atLocation = Location:new(1909, 997, 4650) }),
      Action.ModelHighlight:new(pillars[4], { atLocation = Location:new(1909, 997, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(1903, 997, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1905, 5, 4639, 4) },
  },
  {
    text = "Mirror #3 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[12], { atLocation = Location:new(1909, 5, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1903, 405, 4650) }) },
  },
  {
    text = "Place the second fractured crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 5, 4650) }),
      Action.InventoryHighlight:new(fracturedCrystal2),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(fracturedCrystal2) },
  },
  {
    text = "Mirror #4 points up.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1898, 5, 4665) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeamUp, { atLocation = Location:new(1898, 705, 4665) }) },
  },
  {
    text = "Place the fractured crystal to the south.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 5, 4628) }),
      Action.InventoryHighlight:new(fracturedCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(fracturedCrystal) },
  },
  {
    text = "Mirror #5 points up.",
    actions = {
      Action.ModelHighlight:new(pillars[8], { atLocation = Location:new(1887, 5, 4628) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeamUp2, { atLocation = Location:new(1887, 705, 4628) }) },
  },
  {
    text = "Mirror #6 points up.",
    actions = {
      Action.ModelHighlight:new(pillars[13], { atLocation = Location:new(1909, 5, 4628) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeamUp, { atLocation = Location:new(1909, 705, 4628) }) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(1903.5, 405, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1901, 997, 4638, 4) },
  },
  {
    text = "Place the yellow crystal to the north.",
    actions = {
      Action.ModelHighlight:new(pillars[6], { atLocation = Location:new(1898, 997, 4665) }),
      Action.InventoryHighlight:new(yellowCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(yellowCrystal) },
  },
  {
    text = "Climb up the ladder.",
    actions = { Action.Direction:new(1898, 1497, 4667.4) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 1989, 4666, 4) },
  },
  {
    text = "Mirror #7 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1898, 1989, 4665) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(yellowLightBeam, { atLocation = Location:new(1892, 2389, 4665) }) },
  },
  {
    text = "Climb down the ladder.",
    actions = { Action.Direction:new(1898, 2489, 4667.4) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 997, 4667, 4) },
  },
  {
    text = "Climb up the ladder to the south.",
    actions = { Action.Direction:new(1898, 1497, 4610.6) },
    postconditions = { Condition.DistanceToWithHeight:new(1898, 1989, 4612, 4) },
  },
  {
    text = "Mirror #8 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1887, 1989, 4628) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(greenLightBeam, { atLocation = Location:new(1882, 2589, 4628) }) },
  },
  {
    text = "Mirror #9 points west.",
    actions = {
      Action.ModelHighlight:new(pillars[13], { atLocation = Location:new(1909, 1989, 4628) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1905, 2589, 4628) }) },
  },
  {
    text = "Mirror #10 points north.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1898, 1989, 4628) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1898, 2589, 4639) }) },
  },
  {
    text = "Climb down the staircase.",
    actions = { Action.Direction:new(1891, 1989, 4635) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 997, 4638, 4) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(1891, 1297, 4641) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 1989, 4644, 4) },
  },
  {
    text = "Mirror #11 points west.",
    warning = "No tracking for this step.",
    actions = {
      Action.ModelHighlight:new(pillars[2], { atLocation = Location:new(1897, 1989, 4650) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    -- postconditions = { Condition.ModelVisible:new(lightBeam, { atLocation = Location:new(1892, 2589, 4650) }) },
  },
  {
    text = "Place the blue crystal.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1887, 1989, 4650) }),
      Action.InventoryHighlight:new(blueCrystal),
    },
    postconditions = { Condition.InventoryDoesNotContain:new(blueCrystal) },
  },
  {
    text = "Mirror #12 points south.",
    actions = {
      Action.ModelHighlight:new(pillars[8], { atLocation = Location:new(1860, 1989, 4665) }),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(redLightBeam, { atLocation = Location:new(1860, 2589, 4660) }) },
  },
  {
    text = "Mirror #13 points east.",
    actions = {
      Action.ModelHighlight:new(pillars[10], { atLocation = Location:new(1860, 1989, 4639) }),
      Action.InventoryHighlight:new(mirror),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(redLightBeam, { atLocation = Location:new(1865, 2589, 4639) }) },
  },
  {
    text = "Go down the staircase.",
    actions = { Action.Direction:new(1891, 1989, 4643) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 997, 4640, 4) },
  },
  {
    text = "Go down the staircase again.",
    actions = { Action.Direction:new(1889, 997, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1886, 5, 4639, 4) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor) },
    postconditions = { Condition.DistanceTo:new(1883, 5, 4639, 1) },
  },
  {
    text = "Rotate Mirror #14 west.",
    actions = {
      Action.ModelHighlight:new(pillars[14]),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(redLightBeam, { atLocation = Location:new(1876, 405, 4639) }) },
  },
  {
    text = "Go through to the death altar.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.ConversationText:new("you get in") },
  },
  {
    text = "Talk to Thorgel.",
    title = "Death talisman",
    neededItems = { ["Newly made crystal"] = { quantity = 1 } },
    recommendedItems = { ["Death/Omni talisman"] = { quantity = 1 } },
    jumpconditions = { Condition.ConversationText:new("tell you what I need") },
    jumpOffset = 2,
    actions = { Action.ModelHighlight:new(Models.npcs["thorgel"]) },
    postconditions = { Condition.ConversationText:new("better get started") },
  },
  {
    text = "Retrieve the items Thorgel wants.<ul><li>Rotate Mirror #14 east <b>UNLESS</b> you go through the Underground Pass to return to the Death Altar.</li><li>To enter from the Underground Pass side, enter the underground pass from Tirannwn, make your way to the Dwarven Camp, and enter the tunnel behind the camp.</li></ul>",
    actions = {
      Action.ModelHighlight:new(Models.npcs["thorgel"]),
      Action.ConversationHighlight:new("I've got some of them with me."),
      Action.ConversationHighlight:new("Yeah, sure."),
    },
    postconditions = { Condition.ConversationText:new("with another list") },
  },
  {
    text = "Enter the Death ruins.",
    actions = { Action.ModelHighlight:new(deathRuins) },
    postconditions = { Condition.DistanceTo:new(2208, 2373, 4829, 4) },
  },
  {
    text = "Use the newly made crystal on the altar.",
    actions = {
      Action.ModelHighlight:new(deathAltar),
      Action.InventoryHighlight:new(newlyMadeCrystal),
    },
    postconditions = { Condition.ConversationText:new("a strange") }, --not tested
  },
  {
    text = "Exit through the portal. <b>DO NOT TELEPORT OUT.</b>",
    actions = { Action.ModelHighlight:new(deathAltarPortal) },
    postconditions = { Condition.DistanceTo:new(1863, 125, 4639, 4) },
  },
  {
    text = "Pass through the light door.",
    actions = { Action.ModelHighlight:new(openLightDoor, { atLocation = Location:new(1865, 5, 4639) }) },
    postconditions = { Condition.DistanceTo:new(1867, 5, 4639, 1) },
  },
  {
    text = "Rotate Mirror #14 east.",
    actions = {
      Action.ModelHighlight:new(pillars[14]),
      Action.ConversationHighlight:new("Rotate the mirror."),
    },
    postconditions = { Condition.ModelVisible:new(openLightDoor, { atLocation = Location:new(1885, 5, 4639) }) },
  },
  {
    text = "Climb up the staircase.",
    actions = { Action.Direction:new(1888, 205, 4639) },
    postconditions = { Condition.DistanceToWithHeight:new(1890, 997, 4639, 4) },
  },
  {
    text = "Climb up the staircase again.",
    actions = { Action.Direction:new(1891, 1597, 4642) },
    postconditions = { Condition.DistanceToWithHeight:new(1891, 1989, 4644, 4) },
  },
  {
    text = "Use the powered newly made crystal on the black crystal.",
    actions = {
      Action.ModelHighlight:new(crystal),
      Action.InventoryHighlight:new(poweredNewlyMadeCrystal),
    },
    postconditions = { Condition.ConversationText:new("powered crystal amongst") },
  },
  {
    text = "Talk to Arianwyn in Lletya.",
    title = "Finishing up",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Crystal teleport seed",
      url = "Crystal_teleport_seed.png",
    },
    actions = { Action.ModelHighlight:new(Models.npcs["arianwyn"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Mourning's End Part II",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1129507200,
  prereqQuests = { "Mourning's End Part I" },
  questReqs = {},
  neededItems = {
    ["Mourner gear"] = { quantity = 1, model = Models.items["mourner gear"] },
    ["Rope"] = { quantity = 1, model = Models.items["rope"] },
  },
  recommendedItems = {
    ["Lots of food"] = { quantity = 1 },
    ["Bunyip"] = { quantity = 1 },
    ["Beast of burden"] = { quantity = 1 },
    ["Crystal teleport seed"] = { quantity = 1 },
    ["Death/Omni talisman"] = { quantity = 1 },
    ["Agility boost"] = { quantity = 1 },
    ["Weight-reducing clothing"] = { quantity = 1 },
    ["Any shield"] = { quantity = 1 },
  },
  combatNPCs = { ["Shadows"] = { level = "95", optional = true, quantity = 1 } },
})
