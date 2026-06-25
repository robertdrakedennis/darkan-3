local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--NPCs
local kingArthur = Model.new(4566, {
  [2922] = Vertex.new(5, 766, -88, 134, 100, 70),
  [3654] = Vertex.new(-48, 783, -5, 145, 123, 60),
})
local sirGwain = Model.new(4281, {
  [563] = Vertex.new(-160, 576, -472, 98, 90, 90),
  [630] = Vertex.new(-160, 576, -472, 98, 90, 90),
  [632] = Vertex.new(-160, 576, -472, 98, 90, 90),
  [2502] = Vertex.new(64, 784, -44, 51, 38, 21),
  [2720] = Vertex.new(-64, 784, -44, 51, 38, 21),
})
local sirLancelot = Model.new(4347, {
  [2559] = Vertex.new(64, 784, -44, 97, 72, 40),
  [2792] = Vertex.new(-64, 784, -44, 97, 72, 40),
  [3054] = Vertex.new(5, 769, -87, 134, 100, 70),
  [3191] = Vertex.new(0, 775, 32, 134, 100, 70),
  [3194] = Vertex.new(0, 775, 32, 134, 100, 70),
})
local sirMordred = Model.new(4134, {
  [1785] = Vertex.new(44, 296, -84, 24, 31, 37),
  [1803] = Vertex.new(-48, 276, -84, 24, 31, 37),
  [2037] = Vertex.new(-53, 737, 20, 23, 30, 44),
  [2953] = Vertex.new(0, 739, -80, 83, 62, 34),
  [2961] = Vertex.new(36, 768, -22, 83, 62, 34),
})
local morganLaFey = Model.new(4623, {
  [2235] = Vertex.new(24, 738, -38, 32, 31, 30),
  [2259] = Vertex.new(-24, 738, -38, 32, 31, 30),
  [2281] = Vertex.new(-2, 716, -57, 110, 81, 57),
  [2287] = Vertex.new(2, 716, -57, 110, 81, 57),
  [2291] = Vertex.new(6, 716, -52, 110, 81, 57),
})
local giantBat = Model.new(2394, {
  [65] = Vertex.new(9, 886, -165, 113, 103, 87),
  [132] = Vertex.new(-9, 886, -165, 113, 103, 87),
  [205] = Vertex.new(19, 873, -155, 140, 128, 108),
  [211] = Vertex.new(-19, 873, -155, 140, 128, 108),
  [762] = Vertex.new(-533, 790, -11, 64, 41, 51),
})
local candlemaker = Model.new(4761, {
  [2818] = Vertex.new(-2, 725, -59, 110, 81, 57),
  [2823] = Vertex.new(-7, 724, -51, 110, 81, 57),
  [2826] = Vertex.new(2, 725, -59, 110, 81, 57),
  [2828] = Vertex.new(7, 724, -51, 110, 81, 57),
  [4388] = Vertex.new(-30, 721, -31, 92, 66, 8),
})
local ladyOfTheLake = Model.new(3873, {
  [2025] = Vertex.new(24, 738, -38, 32, 31, 30),
  [2049] = Vertex.new(-24, 738, -38, 32, 31, 30),
  [2071] = Vertex.new(-2, 716, -57, 110, 81, 57),
  [2077] = Vertex.new(2, 716, -57, 110, 81, 57),
  [2081] = Vertex.new(6, 716, -52, 110, 81, 57),
})
local wydin = Model.new(3456, {
  [1261] = Vertex.new(-2, 725, -59, 110, 81, 57),
  [1266] = Vertex.new(-7, 724, -51, 110, 81, 57),
  [1269] = Vertex.new(2, 725, -59, 110, 81, 57),
  [1271] = Vertex.new(7, 724, -51, 110, 81, 57),
  [2616] = Vertex.new(0, 722, 23, 92, 62, 19),
})
local beggar = Model.new(3585, {
  [1894] = Vertex.new(-2, 725, -59, 110, 81, 57),
  [1899] = Vertex.new(-7, 724, -51, 110, 81, 57),
  [1902] = Vertex.new(2, 725, -59, 110, 81, 57),
  [1904] = Vertex.new(7, 724, -51, 110, 81, 57),
  [2740] = Vertex.new(0, 735, -7, 30, 142, 129),
})

--Objects
local camelotGroundFloorStairs = Model.new(600, {
  [183] = Vertex.new(8128, 1680, 4224, 74, 61, 47),
  [591] = Vertex.new(8000, 1920, 3968, 60, 50, 39),
  [593] = Vertex.new(8128, 1920, 3808, 60, 50, 39),
  [597] = Vertex.new(7232, 1920, 3968, 60, 50, 39),
  [599] = Vertex.new(7360, 1920, 3808, 60, 50, 39),
})
local lafeyGroundFloorDoubleDoorsOpen = Model.new(1152, {
  [96] = Vertex.new(-136, 528, -68, 175, 126, 16),
  [152] = Vertex.new(-136, 508, -132, 175, 126, 16),
  [255] = Vertex.new(-228, 460, -180, 132, 115, 27),
  [263] = Vertex.new(-236, 488, -64, 132, 115, 27),
  [510] = Vertex.new(-256, 488, 240, 96, 86, 74),
})
local lafeyGroundFloorStaircase = Model.new(135, {
  [3] = Vertex.new(1472, 2400, 6272, 83, 76, 64),
  [111] = Vertex.new(704, 2520, 6144, 83, 76, 64),
  [117] = Vertex.new(704, 2000, 6272, 83, 76, 64),
  [128] = Vertex.new(1344, 2000, 6272, 83, 76, 64),
  [132] = Vertex.new(1472, 2520, 6272, 83, 76, 64),
})
local lafeyFirstFloorStaircase = Model.new(12, {
  [3] = Vertex.new(576, 3360, 3200, 83, 76, 64),
  [5] = Vertex.new(576, 3840, 3808, 83, 76, 64),
  [8] = Vertex.new(1472, 3480, 3200, 83, 76, 64),
  [9] = Vertex.new(1472, 3840, 3808, 83, 76, 64),
  [11] = Vertex.new(1472, 3360, 3200, 83, 76, 64),
})
local chaosAltar = Model.new(4086, {
  [361] = Vertex.new(6180, 1709, 2805, 127, 127, 127),
  [424] = Vertex.new(6184, 1709, 2805, 127, 127, 127),
  [431] = Vertex.new(6184, 1709, 2805, 127, 127, 127),
  [505] = Vertex.new(6124, 1651, 2806, 127, 127, 127),
  [526] = Vertex.new(6120, 1651, 2806, 127, 127, 127),
})
local crystal = Model.new(99, {
  [9] = Vertex.new(-40, 1092, 44, 7, 36, 79, 0.6078),
  [13] = Vertex.new(24, 1092, 0, 7, 36, 79, 0.6078),
  [37] = Vertex.new(-40, 1092, 44, 7, 36, 79, 0.6078),
  [81] = Vertex.new(-60, 1092, -24, 62, 99, 148, 0.6078),
  [87] = Vertex.new(-40, 1092, 44, 7, 36, 79, 0.6078),
})

--Items
local insectRepellent = Model.new(402, {
  [188] = Vertex.new(4, 288, -4, 84, 78, 77),
  [191] = Vertex.new(-4, 288, -4, 84, 78, 77),
  [192] = Vertex.new(4, 288, -4, 84, 78, 77),
  [212] = Vertex.new(132, 156, 4, 1, 14, 1),
  [219] = Vertex.new(124, 148, -4, 1, 14, 1),
})
local excalibur = Model.new(726, {
  [1] = Vertex.new(53, 12, 129, 127, 127, 127),
  [2] = Vertex.new(51, 12, 128, 127, 127, 127),
  [3] = Vertex.new(52, 13, 129, 127, 127, 127),
  [4] = Vertex.new(53, 6, 129, 127, 127, 127),
  [5] = Vertex.new(52, 4, 129, 127, 127, 127),
})

--Quest Items
local blackCandle = Model.new(60, {
  [1] = Vertex.new(8, 0, -12, 24, 22, 22),
  [2] = Vertex.new(8, 20, -12, 24, 22, 22),
  [3] = Vertex.new(8, 20, 12, 24, 22, 22),
  [4] = Vertex.new(-16, 20, -12, 24, 22, 22),
  [15] = Vertex.new(-16, 0, -12, 24, 22, 22),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to King Arthur at Camelot.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Seers' Village lodestone",
      url = "Seers'_Village_lodestone_icon.png",
    },
    actions = { Action.Direction:new(2762, 645, 3510) },
    postconditions = { Condition.DistanceTo:new(2762, 645, 3510, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(kingArthur),
      Action.ConversationHighlight:new("I'm looking for a quest."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue speaking to King Arthur.",
    actions = {
      Action.ModelHighlight:new(kingArthur),
      Action.ConversationHighlight:new("I want to become a knight of the round table!"),
    },
    postconditions = { Condition.ConversationText:new("Unfortunately, our current quest is to rescue Merlin.") }, --not properly tested
  },
  {
    text = "Talk to Sir Gawain nearby. Ask him how Merlin got stuck in the crystal.",
    actions = {
      Action.ModelHighlight:new(sirGwain),
      Action.ConversationHighlight:new("Do you know how Merlin got trapped?"),
      Action.ConversationHighlight:new("Thank you for the information."),
    },
    postconditions = { Condition.ConversationText:new("It is the least I can do.") },
  },
  {
    text = "Go upstairs, and talk to Sir Lancelot. Ask if he has any ideas on getting into Keep Le Faye.",
    actions = { Action.ModelHighlight:new(camelotGroundFloorStairs) },
    postconditions = { Condition.DistanceToWithHeight:new(2751, 1925, 3513, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(sirLancelot),
      Action.ConversationHighlight:new("Any ideas on how to get into Morgan Le Faye's stronghold?"),
    },
    postconditions = { Condition.ConversationText:new("They take all their deliveries by boat.") }, --not properly tested
  },
  {
    text = "Hide in the crate just west of the candle-maker in Catherby.",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.Direction:new(2792, 997, 3427),
    },
    postconditions = { Condition.ConversationText:new("You wait.") },
  },
  {
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new("You climb out of the crate."),
      Condition.DistanceTo:new(2778, 1941, 3401, 4),
    },
  },
  {
    text = "Climb to the top floor.",
    title = "Inside Keep Le Faye",
    actions = { Action.Direction:new(2775.5, 1925, 3401) },
    postconditions = { Condition.ModelVisible:new(lafeyGroundFloorDoubleDoorsOpen) },
  },
  {
    actions = { Action.ModelHighlight:new(lafeyGroundFloorStaircase) },
    postconditions = { Condition.DistanceToWithHeight:new(2770, 2885, 3407, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(lafeyFirstFloorStaircase) },
    postconditions = { Condition.DistanceToWithHeight:new(2770, 3845, 3401, 4) },
  },
  {
    text = "Attack Sir Mordred until his health drops to 0.<ul><li><b>Don't use DOTs.</b> If you use bleeds/poisons/conjures, future dialogue will be interrupted until the bleed/poison effect ends.</li></ul>",
    actions = { Action.ModelHighlight:new(sirMordred) },
    postconditions = { Condition.ModelVisible:new(morganLaFey) },
  },
  {
    text = "Talk to Morgan Le Faye.",
    actions = {
      Action.ModelHighlight:new(morganLaFey),
      Action.ConversationHighlight:new("Tell me how to untrap Merlin and I might."),
      Action.ConversationHighlight:new("OK I will do all that."),
    },
    postconditions = {
      Condition.ConversationText:new("Ok, I will go do all that."),
      Condition.ConversationText:new("Morgan Le Faye vanishes."),
    },
  },
  {
    text = "If you do not have bat bones, exit the keep to the west and kill a Giant bat.",
    actions = { Action.Direction:new(2770, 3845, 3400) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2770, 2885, 3397, 4),
      Condition.InventoryContains:new(Models.items["bones"]),
    },
  },
  {
    actions = { Action.Direction:new(2770, 2885, 3406) },
    postconditions = {
      Condition.DistanceToWithHeight:new(2770, 1925, 3403, 4),
      Condition.InventoryContains:new(Models.items["bones"]),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(giantBat),
      Action.ModelHighlight:new(Models.items["bones"]),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["bones"]) },
  },
  {
    text = "Talk to the candle-maker in Catherby.",
    title = "Getting a black candle",
    neededItems = { ["Bucket"] = { quantity = 1 }, ["Insect repellent"] = { quantity = 1 } },
    actions = { Action.Direction:new(2795, 997, 3428) },
    postconditions = { Condition.DistanceTo:new(2795, 997, 3428, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(candlemaker),
      Action.ConversationHighlight:new("Have you got any black candles?"),
    },
    postconditions = { Condition.ConversationText:new("IF you can bring me a bucket FULL of wax.") },
  },
  {
    text = "Gather a bucket of wax from the beehives to the north-west. A bucket can be found at the beehives.<ul><li>If you do not have insect repellent then speak to the beekeeper nearby for some. You can also add it to your tool belt.</li></ul>",
    actions = { Action.Direction:new(2762, 597, 3443), Action.ModelHighlight:new(Models.items["bucket"]) },
    postconditions = { Condition.DistanceTo:new(2762, 597, 3443, 4) },
  },
  {
    text = "Left click the hive to use insect repellent on it and then use an empty bucket on a beehive to fill the bucket.",
    actions = { Action.Direction:new(2762, 597, 3443), Action.ModelHighlight:new(Models.items["bucket"]) },
    postconditions = { Condition.ConversationText:new("You pour your insect repellent on the beehive.") }, --not properly tested
  },
  {
    -- text = "debug",
    actions = { Action.InventoryHighlight:new(Models.items["bucket"]) },
    postconditions = { Condition.ConversationText:new("You get some wax from the hive.") },
  },
  {
    text = "Talk to the candle-maker again to get the black candle from him.",
    actions = { Action.Direction:new(2795, 997, 3428) },
    postconditions = { Condition.DistanceTo:new(2795, 997, 3428, 12) },
  },
  {
    -- text = "debug",
    actions = { Action.ModelHighlight:new(candlemaker) },
    postconditions = { Condition.InventoryContains:new(blackCandle) },
  },
  {
    text = "Talk to the Lady of the Lake in southern Taverley. Ask about the sword Excalibur.",
    title = "Getting Excalibur",
    neededItems = { ["Bread"] = { quantity = 1 } },
    actions = { Action.Direction:new(2925, 405, 3406) },
    postconditions = { Condition.DistanceTo:new(2925, 405, 3406, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(ladyOfTheLake),
      Action.ConversationHighlight:new("I seek the sword Excalibur."),
    },
    postconditions = { Condition.ConversationText:new("Okay, that seems easy enough.") },
  },
  {
    text = "Lodestone to Port Sarim.",
    actions = {}, -- To cover last bit of dialogue from the lady of the lake
    postconditions = { Condition.DistanceTo:new(3011, 965, 3214, 4) },
  },
  {
    text = "If you don't have bread, buy a loaf from Wydin's Food Store, just south of the Port Sarim lodestone.",
    actions = { Action.Direction:new(3015, 965, 3206) },
    postconditions = {
      Condition.DistanceTo:new(3015, 965, 3206, 6),
      Condition.InventoryContains:new(Models.items["bread"]),
    },
  },
  {
    actions = { Action.ModelHighlight:new(wydin) },
    postconditions = { Condition.InventoryContains:new(Models.items["bread"]) },
  },
  {
    text = "Enter Grum's Gold Exchange to the north of Port Sarim lodestone.",
    actions = { Action.Direction:new(3015.5, 965, 3246) },
    postconditions = { Condition.ConversationText:new("Please kind sir"), Condition.ModelVisible:new(beggar) },
  },
  {
    text = "Give the beggar your bread and you'll receive Excalibur.",
    actions = { Action.ModelHighlight:new(beggar), Action.ConversationHighlight:new("Yes certainly.") },
    postconditions = { Condition.InventoryContains:new(excalibur) },
  },
  {
    text = "Go to the Temple of Zamorak south of the Varrock east bank.",
    title = "Ritual",
    actions = { Action.Direction:new(3260, 1125, 3382) },
    postconditions = { Condition.DistanceTo:new(3260, 1125, 3382, 8) },
    neededItems = { ["Bat bones"] = { quantity = 1 }, ["Black candle"] = { quantity = 1, model = blackCandle } },
  },
  {
    text = "<i>Check</i> the chaos altar's inscription.",
    actions = { Action.ModelHighlight:new(chaosAltar) },
    postconditions = {
      Condition.ConversationText:new("You find a small inscription at the bottom of the altar."),
    },
  },
  {
    text = "Return to Camelot and enter the gates.",
    actions = { Action.Direction:new(2757, 645, 3486) },
    postconditions = { Condition.DistanceTo:new(2757, 645, 3486, 8) },
  },
  {
    text = "Run north-east outside of the castle to the ritual site (the gazebo outside the castle).",
    actions = { Action.Direction:new(772, 645, 3491) },
    postconditions = { Condition.DistanceTo:new(772, 645, 3491, 4) },
  },
  {
    actions = { Action.Direction:new(2780, 1061, 3515) },
    postconditions = { Condition.DistanceTo:new(2780, 1061, 3515, 4) },
  },
  {
    text = "Stand on the star and light the black candle.",
    actions = { Action.Direction:new(2780, 1061, 3515), Action.InventoryHighlight:new(blackCandle) },
    postconditions = {
      Condition.ChatText:new("You light the candle."),
      Condition.InventoryDoesNotContain:new(blackCandle),
    },
  },
  {
    text = "Drop the bat bones on the star.",
    actions = { Action.Direction:new(2780, 1061, 3515), Action.InventoryHighlight:new(Models.items["bones"]) },
    postconditions = { Condition.ConversationText:new("Suddenly a mighty spirit appears!") },
  },
  {
    text = "The magic words are 'Snarthon Candtrick Termanto'.<ul><li>If you get this wrong, you will have to get the black candle remade; or click away before proceeding with the dialogue and re-drop the bat bones to try again.</li></ul>",
    actions = { Action.ConversationHighlight:new("Snarthon Candtrick Termanto") },
    postconditions = { Condition.ConversationText:new("Begone! And leave me once more in peace.") },
  },
  {
    text = "Enter the castle and climb the top of the south-east tower.",
    title = "Finishing up",
    neededItems = { ["Excalibur"] = { quantity = 1, model = excalibur } },
    actions = { Action.Direction:new(2757, 645, 3506) },
    postconditions = { Condition.DistanceTo:new(2757, 645, 3506, 8) },
  },
  {
    actions = { Action.Direction:new(2769, 645, 3493) },
    postconditions = { Condition.DistanceToWithHeight:new(2768, 1925, 3493, 4) },
  },
  {
    actions = { Action.Direction:new(2767, 1925, 3491) },
    postconditions = { Condition.DistanceToWithHeight:new(2767, 3205, 3492, 4) },
  },
  {
    text = "Smash the giant crystal to free Merlin.",
    actions = { Action.ModelHighlight:new(crystal) },
    postconditions = { Condition.ConversationText:new("Go speak to King Arthur, I'm sure he'll reward you!") },
  },
  {
    text = "Talk to King Arthur.",
    actions = { Action.Direction:new(2767, 3205, 3491) },
    postconditions = { Condition.DistanceToWithHeight:new(2767, 1925, 3492, 4) },
  },
  {
    actions = { Action.Direction:new(2769, 1925, 3493) },
    postconditions = { Condition.DistanceToWithHeight:new(2768, 645, 3493, 4) },
  },
  { actions = { Action.ModelHighlight:new(kingArthur) }, postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Merlin's Crystal",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1014768000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Bucket"] = { quantity = 1, duringQuest = true, model = Models.items["bucket"] },
    ["bread"] = { quantity = 1, duringQuest = true },
    ["Bread or 24 coins to buy some"] = {
      quantity = 1,
      model = Models.items["bread"],
      duringQuest = true,
    },
    ["Insect repellent"] = { quantity = 1, model = insectRepellent, duringQuest = true },
    ["Bat bones"] = { quantity = 1, model = Models.items["bones"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = { ["Sir Mordred"] = { level = "23", quantity = 1 } },
})
