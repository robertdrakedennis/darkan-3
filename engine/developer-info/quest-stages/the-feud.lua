local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local aliMorrisane = Model.new(3960, {
  [81] = Vertex.new(-40, 740, 4, 120, 120, 110),
  [93] = Vertex.new(32, 744, -12, 120, 120, 110),
  [1960] = Vertex.new(-4, 721, -3, 54, 142, 28),
  [1987] = Vertex.new(0, 735, -7, 54, 142, 28),
  [1989] = Vertex.new(0, 732, -8, 54, 142, 28),
})
local rugMerchant = Model.new(3855, {
  [2254] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2259] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2262] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2264] = Vertex.new(7, 724, -51, 106, 78, 54),
  [3199] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local jalal = Model.new(3831, {
  [2140] = Vertex.new(-2, 725, -59, 87, 69, 45),
  [2145] = Vertex.new(-7, 724, -51, 87, 69, 45),
  [2148] = Vertex.new(2, 725, -59, 87, 69, 45),
  [2150] = Vertex.new(7, 724, -51, 87, 69, 45),
  [2578] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local menaphiteThug = Model.new(4341, {
  [2893] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2898] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2901] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2903] = Vertex.new(7, 724, -51, 85, 66, 43),
  [3649] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local bandit = Model.new(4299, {
  [2068] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2073] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2076] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2078] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2965] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local brigand = Model.new(4215, {
  [2092] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2097] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2100] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2102] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2671] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local kebabSeller = Model.new(3333, {
  [2248] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2253] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2256] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2258] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2527] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local camelMan = Model.new(4437, {
  [2005] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2009] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2023] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [3948] = Vertex.new(-65, 670, 50, 89, 14, 7),
  [3979] = Vertex.new(65, 670, 50, 89, 14, 7),
})
local rashid = Model.new(4503, {
  [2851] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2856] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2859] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2861] = Vertex.new(7, 724, -51, 85, 66, 43),
  [3631] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local villager = Model.new(3855, {
  [2548] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2553] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2556] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2558] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2821] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local womanVillager = Model.new(4143, {
  [1743] = Vertex.new(24, 738, -38, 30, 28, 27),
  [1767] = Vertex.new(-24, 738, -38, 30, 28, 27),
  [1795] = Vertex.new(2, 716, -57, 85, 66, 43),
  [2607] = Vertex.new(-17, 756, -51, 161, 148, 147),
  [2717] = Vertex.new(17, 756, -51, 161, 148, 147),
})
local streetUrchin = Model.new(2634, {
  [1705] = Vertex.new(19, 518, -49, 53, 49, 27),
  [1712] = Vertex.new(-19, 518, -49, 53, 49, 27),
  [2053] = Vertex.new(34, 483, 6, 87, 59, 35),
  [2619] = Vertex.new(36, 508, -44, 148, 148, 135),
  [2631] = Vertex.new(-36, 508, -44, 148, 148, 135),
})
local faisal = Model.new(3333, {
  [2248] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2253] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2256] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2258] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2527] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local maysa = Model.new(3618, {
  [1446] = Vertex.new(24, 616, -180, 161, 148, 147),
  [1489] = Vertex.new(-24, 616, -180, 161, 148, 147),
  [1495] = Vertex.new(-24, 616, -180, 161, 148, 147),
  [1499] = Vertex.new(-24, 616, -180, 161, 148, 147),
  [2151] = Vertex.new(27, 627, -191, 28, 26, 21),
})
local badir = Model.new(4380, {
  [509] = Vertex.new(-16, 700, -384, 72, 59, 36),
  [513] = Vertex.new(-16, 700, -384, 72, 59, 36),
  [518] = Vertex.new(-16, 700, -384, 75, 62, 38),
  [668] = Vertex.new(16, 688, -384, 72, 59, 36),
  [2829] = Vertex.new(5, 724, -70, 94, 69, 48),
})
local snake = Model.new(1146, {
  [129] = Vertex.new(-8, 40, -232, 161, 148, 147),
  [131] = Vertex.new(8, 40, -232, 161, 148, 147),
  [424] = Vertex.new(0, 32, 600, 53, 53, 27),
  [427] = Vertex.new(0, 32, 600, 53, 53, 27),
  [815] = Vertex.new(0, 32, 600, 53, 53, 27),
})
local menaphiteLeader = Model.new(3681, {
  [2041] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [2046] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [2049] = Vertex.new(2, 725, -59, 85, 66, 43),
  [2051] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2971] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local banditLeader = Model.new(4023, {
  [1918] = Vertex.new(-2, 725, -59, 85, 66, 43),
  [1923] = Vertex.new(-7, 724, -51, 85, 66, 43),
  [1926] = Vertex.new(2, 725, -59, 85, 66, 43),
  [1928] = Vertex.new(7, 724, -51, 85, 66, 43),
  [2899] = Vertex.new(0, 735, -7, 27, 139, 126),
})
--#endregion
--#region Objects
local trough = Model.new(7494, {
  [2136] = Vertex.new(4922, 3349, 6947, 163, 163, 163),
  [2154] = Vertex.new(4879, 3400, 7089, 163, 163, 163),
  [2157] = Vertex.new(4879, 3400, 7089, 163, 163, 163),
  [2217] = Vertex.new(4219, 3342, 6966, 163, 163, 163),
  [2253] = Vertex.new(4412, 3221, 6427, 163, 163, 163),
})
local camelDung = Model.new(684, {
  [398] = Vertex.new(120, 248, 132, 65, 48, 26),
  [402] = Vertex.new(120, 248, 132, 65, 48, 26),
  [554] = Vertex.new(120, 248, 132, 65, 48, 26),
  [555] = Vertex.new(76, 248, 160, 65, 48, 26),
  [626] = Vertex.new(-140, 256, -92, 65, 48, 26),
})
local staircase = Model.new(228, {
  [86] = Vertex.new(6656, 2419, 2064, 130, 129, 119),
  [87] = Vertex.new(7168, 2419, 2064, 130, 129, 119),
  [110] = Vertex.new(7168, 960, 641, 129, 128, 119),
  [161] = Vertex.new(7168, 2419, 2064, 129, 128, 119),
  [163] = Vertex.new(7168, 2419, 2064, 129, 128, 119),
})
--#endregion
--#region Items
local oakBlackjack = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 150, 116, 114),
  [5] = Vertex.new(-72, 0, -56, 150, 116, 114),
  [187] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [190] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [195] = Vertex.new(-84, 28, -84, 89, 63, 7),
})
--#endregion
--#region Quest Items
local fakeBeard = Model.new(186, {
  [128] = Vertex.new(64, 16, 40, 55, 50, 50),
  [130] = Vertex.new(60, 16, 32, 55, 50, 50),
  [133] = Vertex.new(60, 16, 32, 55, 50, 50),
  [135] = Vertex.new(64, 16, 40, 55, 50, 50),
  [138] = Vertex.new(64, 16, 40, 55, 50, 50),
})
local kharidianHeadpiece = Model.new(231, {
  [60] = Vertex.new(20, 16, 56, 148, 148, 135),
  [62] = Vertex.new(20, 16, 56, 148, 148, 135),
  [168] = Vertex.new(-32, 68, 32, 148, 148, 135),
  [170] = Vertex.new(-32, 68, 32, 148, 148, 135),
  [182] = Vertex.new(-32, 68, 32, 148, 148, 135),
})
local desertDisguise = Model.new(417, {
  [194] = Vertex.new(0, 84, 64, 148, 148, 135),
  [195] = Vertex.new(-28, 80, 56, 148, 148, 135),
  [197] = Vertex.new(0, 84, 64, 148, 148, 135),
  [317] = Vertex.new(-28, 80, 56, 148, 148, 135),
  [320] = Vertex.new(-28, 80, 56, 148, 148, 135),
})
local hotSauce = Model.new(354, {
  [3] = Vertex.new(-24, 88, -24, 0, 0, 0),
  [9] = Vertex.new(-24, 88, -24, 135, 21, 11),
  [11] = Vertex.new(-24, 88, -24, 135, 21, 11),
  [15] = Vertex.new(-24, 88, -24, 135, 21, 11),
  [21] = Vertex.new(-12, 96, -8, 135, 21, 11),
})
local camelReceipt = Model.new(276, {
  [3] = Vertex.new(20, 0, -72, 150, 116, 114),
  [5] = Vertex.new(-72, 0, -56, 150, 116, 114),
  [187] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [190] = Vertex.new(-84, 28, -84, 89, 63, 7),
  [195] = Vertex.new(-84, 28, -84, 89, 63, 7),
})
local ugthankiDung = Model.new(600, {
  [4] = Vertex.new(68, 140, 60, 76, 56, 15),
  [44] = Vertex.new(64, 140, -52, 76, 56, 15),
  [100] = Vertex.new(68, 140, 60, 76, 56, 15),
  [192] = Vertex.new(64, 140, -52, 90, 71, 36),
  [204] = Vertex.new(68, 140, 60, 90, 71, 36),
})
local keys = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 152, 120, 13),
  [104] = Vertex.new(68, 16, 68, 152, 120, 13),
  [397] = Vertex.new(68, 16, 68, 152, 120, 13),
  [400] = Vertex.new(68, 16, 68, 152, 120, 13),
  [408] = Vertex.new(68, 16, 68, 152, 120, 13),
})
local jewels = Model.new(600, {
  [4] = Vertex.new(68, 140, 60, 76, 56, 15),
  [44] = Vertex.new(64, 140, -52, 76, 56, 15),
  [100] = Vertex.new(68, 140, 60, 76, 56, 15),
  [192] = Vertex.new(64, 140, -52, 90, 71, 36),
  [204] = Vertex.new(68, 140, 60, 90, 71, 36),
})
local snakeCharmer = Model.new(306, {
  [168] = Vertex.new(36, 60, -20, 0, 0, 0),
  [170] = Vertex.new(36, 60, -8, 0, 0, 0),
  [174] = Vertex.new(24, 60, -8, 0, 0, 0),
  [192] = Vertex.new(-12, 60, 20, 0, 0, 0),
  [194] = Vertex.new(-12, 60, 32, 0, 0, 0),
})
local filledSnakeBasket = Model.new(483, {
  [157] = Vertex.new(148, 200, -148, 53, 53, 27),
  [161] = Vertex.new(148, 200, -148, 53, 53, 27),
  [434] = Vertex.new(84, 312, -84, 97, 93, 61),
  [446] = Vertex.new(84, 312, -84, 97, 93, 61),
  [450] = Vertex.new(84, 312, -84, 97, 93, 61),
})
local snakePoison = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 113, 101, 45),
    [3] = Vertex.new(4, 96, -12, 113, 101, 45),
    [4] = Vertex.new(-4, 96, -12, 113, 101, 45),
    [7] = Vertex.new(4, 96, 12, 113, 101, 45),
    [9] = Vertex.new(-4, 96, 12, 113, 101, 45),
  }),
  Model.new(240, {
    [194] = Vertex.new(12, 96, 4, 133, 135, 146, 0.4980),
    [221] = Vertex.new(-12, 96, -4, 133, 135, 146, 0.4980),
    [225] = Vertex.new(-12, 96, -4, 133, 135, 146, 0.4980),
    [234] = Vertex.new(-4, 96, 12, 133, 135, 146, 0.4980),
    [237] = Vertex.new(-4, 96, 12, 133, 135, 146, 0.4980),
  }),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Ali Morrisane in Al Kharid.",
    title = "Getting started",
    actions = {
      Action.ModelHighlight:new(aliMorrisane, { distance = 12 }),
      Action.Direction:new(3303, 1077, 3211, { distance = 12 }),
      Action.ConversationHighlight:new("If you are, then why are you still selling goods from a stall?"),
      Action.ConversationHighlight:new("I'd like to help you but...."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue talking to Ali Morrisane.",
    actions = {
      Action.ModelHighlight:new(aliMorrisane, { distance = 12 }),
      Action.Direction:new(3303, 1077, 3211, { distance = 12 }),
      Action.ConversationHighlight:new("No, I'm really too busy."),
    },
    postconditions = { Condition.ConversationText:new("Now have a look at my wares.") },
  },
  {
    text = "Take the magic carpet next to Shantay Pass to Pollnivneach.",
    title = "Pollnivneach",
    neededItems = { ["Beer"] = { quantity = 3 } },
    actions = {
      Action.ModelHighlight:new(rugMerchant, { distance = 8 }),
      Action.Direction:new(3311, 1157, 3109, { distance = 8 }),
    },
    postconditions = { Condition.DistanceTo:new(3351, 1010, 3002, 8) },
  },
  {
    text = "Head to the local bar.",
    actions = { Action.Direction:new(3358, 965, 2956) },
    postconditions = { Condition.DistanceTo:new(3358, 965, 2956, 5) },
  },
  {
    text = "Give three beers to Jalal the Drunk by right-click <i>use</i> on him. Finish the dialogue with each beer.",
    actions = {
      Action.ModelHighlight:new(jalal),
      Action.InventoryHighlight:new(Models.items["beer"]),
    },
    postconditions = { Condition.ConversationText:new("What do you think?") },
  },
  {
    text = "Right click <i>talk to</i> a Menaphite Thug to the west.",
    actions = { Action.ModelHighlight:new(menaphiteThug, { highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationText:new("Well I suppose so.") },
  },
  {
    text = "Right click <i>talk to</i> a brigand or bandit in the northern part of Pollnivneach.",
    actions = { Action.Direction:new(3361, 965, 2988) },
    postconditions = { Condition.DistanceTo:new(3361, 965, 2988, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(bandit, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(brigand, { highlightPriority = "closest" }),
    },
    postconditions = {
      Condition.ConversationText:new("Maybe if I got both gangs a camel"),
    },
  },
  {
    text = "Buy red hot sauce from Isma'il the Kebab seller, north-west of the well in the town centre.",
    title = "Camels",
    neededItems = { ["Coins"] = { quantity = 500 }, ["Bucket"] = { quantity = 1 } },
    actions = {
      Action.ModelHighlight:new(kebabSeller, { distance = 4 }),
      Action.Direction:new(3354, 965, 2975, { distance = 4 }),
      Action.ConversationHighlight:new("Would you sell me that bottle of special kebab sauce?"),
      Action.ConversationHighlight:new("No thanks, I'm good."),
    },
    postconditions = { Condition.ConversationText:new("Come back soon, I could do with a few more customers.") },
  },
  {
    text = "In the house immediately to the south-west, ask Sami the Camel Man for two camels. Offer 500 coins.",
    actions = {
      Action.ModelHighlight:new(camelMan, { distance = 4 }),
      Action.Direction:new(3349, 965, 2964, { distance = 4 }),
      Action.ConversationHighlight:new("Are those camels around the side for sale?"),
      Action.ConversationHighlight:new("What price do you want for both of them?"),
      Action.ConversationHighlight:new("Would 500 gold coins for the pair of them do?"),
    },
    postconditions = {
      Condition.InventoryContains:new(camelReceipt),
      Condition.ConversationText:new(
        "Sami the Camel Man gives you a receipt for the camels in return for 500 gold coins."
      ),
    },
  },
  {
    text = "Use the sauce on a trough in the camel shop.",
    actions = { Action.InventoryHighlight:new(hotSauce), Action.Direction:new(3342.5, 965, 2960) },
    postconditions = { Condition.ModelVisible:new(camelDung) },
  },
  {
    text = "Pick up the dung with a bucket in the backpack if the dung is brown. If it is green, use more sauce on a trough until it is brown.<ul><li>2 empty buckets can be found in the camel shop.</li><li>Obtain more buckets of dung if you plan to complete Dealing with Scabaras (1 bucket, does not consume) and My Arm's Big Adventure (3 buckets).</li></ul>",
    actions = { Action.InventoryHighlight:new(Models.items["bucket"]), Action.ModelHighlight:new(camelDung) },
    postconditions = { Condition.InventoryContains:new(ugthankiDung) },
  },
  {
    text = "Talk to a Brigand to give them one of the receipts.",
    actions = { Action.Direction:new(3362, 965, 2988) },
    postconditions = { Condition.DistanceTo:new(3362, 965, 2988, 6) },
  },
  {
    actions = {
      Action.ModelHighlight:new(bandit, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(brigand, { highlightPriority = "closest" }),
    },
    postconditions = { Condition.ConversationText:new("Ahhh why do I bother!") },
  },
  {
    text = "Talk to a Menaphite Thug to give them the other receipt.",
    actions = { Action.Direction:new(3346, 965, 2958) },
    postconditions = { Condition.DistanceTo:new(3346, 965, 2958, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(menaphiteThug) },
    postconditions = { Condition.ConversationText:new("Thanks") },
  },
  {
    text = "Go south-west of Pollnivneach to a big purple tent and ask Rashid the Operator to join his gang.",
    title = "Pickpocketing",
    neededItems = { ["Coins"] = { quantity = 10 } },
    actions = {
      Action.ModelHighlight:new(rashid, { distance = 8 }),
      Action.Direction:new(3333, 965, 2948, { distance = 8 }),
      Action.ConversationHighlight:new("Yes, of course, those bandits should be taught a lesson."),
      Action.ConversationHighlight:new("No thanks."),
    },
    postconditions = { Condition.ConversationText:new("No thanks.") },
  },
  {
    text = "Attempt to <i>pickpocket</i> a villager.<ul><li>For the tracking to work properly for this next section, game messages need to be set to 'On'.</li></ul>",
    actions = {
      Action.Direction:new(3359, 965, 2969, { distance = 12 }),
      Action.ModelHighlight:new(villager, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("You've been stunned.") },
  },
  {
    text = "Talk to the street urchin near the centre town fountain.",
    actions = {
      Action.ModelHighlight:new(streetUrchin),
      Action.ConversationHighlight:new("Here you go, thanks."),
    },
    postconditions = { Condition.ChatText:new("Ya, I see them. Give me a sec.") },
  },
  {
    text = "Try to <i>pickpocket</i> a villager again.",
    actions = {
      Action.ModelHighlight:new(streetUrchin),
      Action.ModelHighlight:new(villager),
    },
    postconditions = { Condition.ConversationText:new("You've been stunned.") },
  },
  {
    text = "Return to Rashid the Operator for advice.",
    actions = {
      Action.ModelHighlight:new(rashid, { distance = 8 }),
      Action.Direction:new(3333, 965, 2948, { distance = 8 }),
      Action.ConversationHighlight:new("Yeah, I could do with a bit of advice."),
      Action.ConversationHighlight:new("No thanks."),
    },
    postconditions = { Condition.InventoryContains:new(oakBlackjack) },
  },
  {
    text = "Equip the oak-blackjack and then lure a villager into a secluded area.",
    actions = {
      Action.InventoryHighlight:new(oakBlackjack),
      Action.ModelHighlight:new(villager, { distance = 8 }),
      Action.ModelHighlight:new(womanVillager, { distance = 8 }),
      Action.Direction:new(3350, 965, 2960, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("Follow me") },
  },
  {
    text = "<i>Knock-Out</i> the villager with the blackjack and <i>pickpocket</i> them.",
    actions = {
      Action.Direction:new(3350, 965, 2955),
      Action.ModelHighlight:new(villager),
      Action.ModelHighlight:new(womanVillager),
    },
    postconditions = {
      -- Condition.ChatText:new("You smack the villager over the head"),
      Condition.ChatText:new("You pick the villager's pocket"),
    },
  },
  {
    text = "Talk to Rashid the Operator.<ul><li>You can set your game messages to filtered now.</li></ul>",
    title = "Jewels",
    neededItems = {
      ["Keys"] = { quantity = 1 },
      ["Leather gloves"] = { quantity = 1 },
      ["Desert disguise"] = { quantity = 1 },
    },
    actions = {
      Action.ModelHighlight:new(rashid, { distance = 8 }),
      Action.Direction:new(3333, 965, 2948, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("No! Now get going.") },
  },
  {
    text = "Equip the desert disguise and hide-behind the cactus near the mayor's house next to the fountain.",
    actions = { Action.InventoryHighlight:new(desertDisguise), Action.Direction:new(3363.2, 965, 2967.5) },
    postconditions = { Condition.ConversationText:new("The coast is clear, there appears to be nobody home.") },
  },
  {
    text = "Use the keys on the door.<ul><li>If lost, talk to Rashid the Operator for another.</li></ul>",
    actions = { Action.InventoryHighlight:new(keys), Action.Direction:new(3370.5, 965, 2971) },
    postconditions = { Condition.DistanceTo:new(3373, 965, 2971, 2) },
  },
  {
    text = "Climb the stairs.",
    actions = { Action.ModelHighlight:new(staircase) },
    postconditions = { Condition.DistanceToWithHeight:new(3374, 2437, 2978, 3) },
  },
  {
    text = "<i>Search</i> the landscape picture near the bed.",
    actions = { Action.Direction:new(3375.5, 2437, 2974) },
    postconditions = { Condition.ConversationText:new("safe") },
  },
  {
    text = "Click <b>1-1-2-3-5-8</b> in that order <i>clockwise</i>. Right-click the dials to help with the numbers.",
    postconditions = { Condition.InventoryContains:new(jewels) },
  },
  {
    text = "Give the jewels to Rashid the Operator.",
    actions = {
      Action.ModelHighlight:new(rashid, { distance = 8 }),
      Action.Direction:new(3333, 965, 2948, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("Well then get to it.") },
  },
  {
    text = "Talk to Rashid the Operator.",
    title = "Traitor",
    neededItems = {
      ["Bucket"] = { quantity = 1 },
      ["Coins"] = { quantity = 1 },
    },
    actions = {
      Action.ModelHighlight:new(rashid, { distance = 8 }),
      Action.Direction:new(3333, 965, 2948, { distance = 8 }),
      Action.ConversationHighlight:new("No thanks."),
    },
    postconditions = { Condition.ConversationText:new("No thanks.") },
  },
  {
    text = "Talk to a Menaphite Thug.",
    actions = { Action.ModelHighlight:new(menaphiteThug) },
    postconditions = {
      Condition.ConversationText:new("Well what can I say, he doesn't have that name for being loyal!"),
    },
  },
  {
    text = "Talk to Rashid the Operator.",
    actions = {
      Action.ModelHighlight:new(rashid, { distance = 8 }),
      Action.Direction:new(3333, 965, 2948, { distance = 8 }),
      Action.ConversationHighlight:new("No thanks."),
    },
    postconditions = { Condition.ConversationText:new("No thanks.") },
  },
  {
    text = "Talk to Faisal the Barman.",
    actions = {
      Action.ModelHighlight:new(faisal, { distance = 8 }),
      Action.Direction:new(3358, 965, 2957, { distance = 8 }),
      Action.ConversationHighlight:new("I'm looking for Traitorous Hesham."),
      Action.ConversationHighlight:new("No thanks I'm ok."),
    },
    postconditions = { Condition.ConversationText:new("No thanks I'm ok. Thanks for your time.") },
  },
  {
    text = "Talk to Maysa the Hag, in the house north-west of the bar, on the hill.",
    actions = {
      Action.ModelHighlight:new(maysa, { distance = 8 }),
      Action.Direction:new(3343, 3205, 2989, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("it's about as dangerous as a rubber knife.") },
  },
  {
    text = "Right-click and <i>talk to</i> Badir the Snake Charmer sitting south of the bar.",
    actions = {
      Action.ModelHighlight:new(badir, { distance = 8 }),
      Action.Direction:new(3355, 965, 2952, { distance = 8 }),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.InventoryContains:new(snakeCharmer) },
  },
  {
    text = "Go north of the Menaphite tent.",
    actions = { Action.Direction:new(3331, 757, 2960) },
    postconditions = { Condition.DistanceTo:new(3331, 757, 2960, 8) },
  },
  {
    text = "Right-click <i>use</i> the snake charm on a desert snake and wait until it goes into the snake basket.<ul><li>If Badir the Snake Charmer did not give you a snake basket, it may be in your bank or talk to him again.</li></ul>",
    actions = { Action.InventoryHighlight:new(snakeCharmer), Action.ModelHighlight:new(snake) },
    postconditions = { Condition.InventoryContains:new(filledSnakeBasket) },
  },
  {
    text = "Talk to Maysa the Hag twice to give her the full snake basket and ugthanki dung.",
    actions = {
      Action.ModelHighlight:new(maysa, { distance = 8 }),
      Action.Direction:new(3343, 3205, 2989, { distance = 8 }),
    },
    postconditions = { Condition.InventoryContains:new(snakePoison) },
  },
  {
    text = "Use the Hag's poison on the table in the bar (it's the square one with a full beer on it, not the one with the empty beer glass).",
    actions = { Action.Direction:new(3356, 965, 2957), Action.InventoryHighlight:new(snakePoison) },
    postconditions = {
      Condition.ConversationText:new("You poison the drink."), --not properly tested
    },
  },
  {
    text = "Talk to Rashid the Operator.",
    actions = {
      Action.ModelHighlight:new(rashid, { distance = 8 }),
      Action.Direction:new(3333, 965, 2948, { distance = 8 }),
    },
    postconditions = {
      Condition.ConversationText:new("He wants to see you now to discuss the future direction of the gang."),
    },
  },
  {
    text = "Talk to the Menaphite Leader outside the tent.",
    title = "Dealing with the thugs",
    actions = { Action.ModelHighlight:new(menaphiteLeader) },
    postconditions = { Condition.ConversationText:new("Fool! You'll never get to me. Guards protect me.") },
  },
  {
    text = "Kill the Tough Guy.",
    postconditions = { Condition.ModelNotVisible:new(menaphiteLeader) }, --easy to break
  },
  {
    text = "Talk to a villager.",
    actions = {
      Action.ModelHighlight:new(villager),
      Action.ConversationHighlight:new("Ok I'll get to it."),
    },
    postconditions = { Condition.ConversationText:new("Ok I'll get to it.") },
  },
  {
    text = "Talk to the Bandit Leader east of the northern magic carpet station.<ul><li>The Bandit leader will not appear if you did not talk to the villager in the previous step.</li></ul>",
    actions = {
      Action.ModelHighlight:new(banditLeader, { distance = 8 }),
      Action.Direction:new(3353, 965, 3002, { distance = 8 }),
    },
    postconditions = { Condition.ConversationText:new("Start packing your bags!") },
  },
  { text = "Kill the Bandit champion.", postconditions = { Condition.ConversationText:new("I've defeated") } },
  {
    text = "Talk to a villager.",
    actions = {
      Action.ModelHighlight:new(villager, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(womanVillager, { highlightPriority = "closest" }),
    },
    postconditions = { Condition.ConversationText:new("I still haven't found Ali's nephew.") },
  },
  {
    text = "Talk to Hakeem the Mayor located next to the well in the middle of the town.",
    actions = {
      -- Action.ModelHighlight:new(hakeem, { distance = 8 }), --hakeem's model is shared with others
      Action.Direction:new(3360, 965, 2969),
    },
    postconditions = { Condition.ConversationText:new("Let's just put it down to adventurer's intuition.") },
  },
  {
    text = "Return to Ali Morrisane in Al-Kharid.",
    actions = {
      Action.ModelHighlight:new(aliMorrisane, { distance = 12 }),
      Action.Direction:new(3303, 1077, 3211, { distance = 12 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Feud",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1112572800,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Thieving", 30) },
  neededItems = {
    ["Combat gear"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
    ["Desert disguise (or coins to purcahse the items needed)"] = {
      quantity = 1,
      model = desertDisguise,
      duringQuest = true,
    },
    ["A bucket"] = { quantity = 1, model = Models.items["bucket"], duringQuest = true },
    ["Beer"] = { quantity = 1, model = Models.items["beer"], duringQuest = true },
    ["Leather gloves"] = { quantity = 1, Models.items["leather gloves"] },
    ["Red hot sauce (check your bank beforehand)"] = { quantity = 1 },
    ["Coins"] = { quantity = 1500 },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Bandit champion"] = { level = "54", quantity = 1 },
    ["Tough Guy"] = { level = "54", quantity = 1 },
  },
})
