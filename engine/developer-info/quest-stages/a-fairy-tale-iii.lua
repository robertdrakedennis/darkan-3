local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local fairyVeryWise = Model.new(1365, {
  [1277] = Vertex.new(44, 808, -116, 169, 165, 177),
  [1284] = Vertex.new(-44, 808, -116, 169, 165, 177),
  [1307] = Vertex.new(32, 848, -144, 179, 176, 186),
  [1311] = Vertex.new(44, 824, -116, 179, 176, 186),
  [1338] = Vertex.new(-32, 848, -144, 179, 176, 186),
})
local fairyQueen = Model.new(1620, {
  [35] = Vertex.new(28, 832, 0, 184, 178, 174),
  [168] = Vertex.new(-32, 820, 8, 184, 178, 174),
  [530] = Vertex.new(32, 428, -104, 189, 180, 179),
  [539] = Vertex.new(24, 416, -88, 189, 180, 179),
  [579] = Vertex.new(-32, 432, -108, 189, 180, 179),
})
local toothFairy = Model.new(1626, {
  [720] = Vertex.new(-8, 888, -180, 198, 190, 189),
  [723] = Vertex.new(8, 888, -180, 198, 190, 189),
  [782] = Vertex.new(8, 888, -180, 195, 186, 186),
  [791] = Vertex.new(0, 852, -172, 190, 181, 181),
  [1190] = Vertex.new(-100, 624, -304, 62, 50, 40),
})
local gnarly = Model.new(2334, {
  [356] = Vertex.new(40, 264, 92, 180, 162, 115),
  [461] = Vertex.new(-40, 264, 92, 180, 162, 115),
  [1214] = Vertex.new(40, 264, 92, 204, 190, 152),
  [1778] = Vertex.new(44, 408, -76, 200, 184, 143),
  [2017] = Vertex.new(-44, 408, -76, 200, 184, 143),
})
local breegth = Model.new(4776, {
  [2021] = Vertex.new(116, 292, -160, 119, 115, 76),
  [2132] = Vertex.new(-116, 292, -160, 119, 115, 76),
  [2340] = Vertex.new(292, 264, -572, 128, 128, 117),
  [2429] = Vertex.new(-292, 264, -572, 128, 128, 117),
  [2708] = Vertex.new(-292, 220, -508, 128, 128, 117),
})
local ork = Model.new(4665, {
  [1622] = Vertex.new(-136, 640, -80, 66, 20, 23),
  [2150] = Vertex.new(124, 256, -72, 163, 137, 66),
  [2156] = Vertex.new(68, 252, 52, 163, 137, 66),
  [2246] = Vertex.new(-124, 256, -72, 163, 137, 66),
  [2252] = Vertex.new(-68, 252, 52, 163, 137, 66),
})
local kChunk = Model.new(3393, {
  [204] = Vertex.new(-24, 712, -172, 176, 165, 165),
  [365] = Vertex.new(44, 748, -80, 113, 113, 59),
  [477] = Vertex.new(-44, 748, -80, 113, 113, 59),
  [1058] = Vertex.new(-28, 740, -128, 113, 113, 59),
  [3378] = Vertex.new(-32, 232, -100, 135, 112, 70),
})
local woodDryad = Model.new(7140, {
  [2887] = Vertex.new(0, 739, -3, 35, 174, 159),
  [2888] = Vertex.new(0, 736, -3, 35, 174, 159),
  [2889] = Vertex.new(0, 736, -4, 35, 174, 159),
  [3008] = Vertex.new(21, 750, -39, 170, 165, 157),
  [3030] = Vertex.new(-21, 750, -39, 170, 165, 157),
})
local gromblod = Model.new(5610, {
  [2063] = Vertex.new(96, 308, 84, 112, 100, 71),
  [2069] = Vertex.new(192, 312, -100, 112, 100, 71),
  [2093] = Vertex.new(-192, 312, -100, 112, 100, 71),
  [2171] = Vertex.new(-188, 320, 16, 112, 100, 71),
  [2243] = Vertex.new(188, 320, 16, 112, 100, 71),
})
local shredflesh = Model.new(5928, {
  [2366] = Vertex.new(364, 1208, -48, 132, 130, 121),
  [2414] = Vertex.new(368, 980, -272, 132, 130, 121),
  [5537] = Vertex.new(-324, 652, -756, 123, 117, 94),
  [5588] = Vertex.new(-324, 652, -756, 123, 117, 94),
  [5592] = Vertex.new(-324, 652, -756, 123, 117, 94),
})
--#endregion
--#region Objects
local mangoTree = Model.new(804, {
  [731] = Vertex.new(1984, 6380, 920, 79, 99, 51),
  [740] = Vertex.new(2172, 6324, 896, 79, 99, 51),
  [746] = Vertex.new(2312, 6272, 832, 79, 99, 51),
  [755] = Vertex.new(2468, 6328, 700, 79, 99, 51),
  [761] = Vertex.new(2516, 6260, 584, 79, 99, 51),
})
local mangoWithToothObj = Model.new(432, {
  [359] = Vertex.new(4, 4, -176, 94, 82, 72),
  [389] = Vertex.new(4, 4, 220, 94, 82, 72),
  [392] = Vertex.new(4, 4, 220, 94, 82, 72),
  [393] = Vertex.new(80, 4, 164, 94, 82, 72),
  [407] = Vertex.new(96, 4, 84, 94, 82, 72),
})
local rockPile = Model.new(2082, {
  [1980] = Vertex.new(-768, 960, -512, 0, 0, 0),
  [1983] = Vertex.new(-768, 960, -512, 0, 0, 0),
  [1996] = Vertex.new(-500, 960, -512, 29, 27, 22),
  [2034] = Vertex.new(768, 960, -244, 58, 54, 44),
  [2064] = Vertex.new(-768, 960, 512, 58, 54, 44),
})
local fairyTree = Model.new(3414, {
  [1923] = Vertex.new(-264, 959, -688, 91, 118, 99),
  [1940] = Vertex.new(-240, 959, -628, 92, 121, 101),
  [2073] = Vertex.new(-216, 959, -704, 91, 118, 99),
  [2089] = Vertex.new(-264, 959, -688, 74, 97, 81),
  [2094] = Vertex.new(-264, 959, -688, 74, 97, 81),
})
local egg = Model.new(5313, {
  [57] = Vertex.new(180, 24, -196, 175, 171, 134),
  [111] = Vertex.new(-204, 24, -188, 175, 171, 134),
  [165] = Vertex.new(-196, 24, 204, 175, 171, 134),
  [168] = Vertex.new(-196, 24, 204, 159, 155, 122),
  [219] = Vertex.new(196, 24, 196, 175, 171, 134),
})
--#endregion
--#region Quest Items
local toothExtractor = Model.new(768, {
  [49] = Vertex.new(-44, 4, 84, 48, 34, 15),
  [77] = Vertex.new(-52, 4, 76, 62, 44, 19),
  [80] = Vertex.new(-52, 4, 76, 62, 44, 19),
  [81] = Vertex.new(-44, 4, 84, 62, 44, 19),
  [156] = Vertex.new(-44, 4, 84, 48, 34, 15),
})
local gnarlyItem = Model.new(408, {
  [284] = Vertex.new(-180, 440, 224, 191, 172, 123),
  [288] = Vertex.new(-100, 372, 108, 186, 167, 118),
  [292] = Vertex.new(-84, 320, 124, 180, 161, 114),
  [306] = Vertex.new(-84, 320, 124, 177, 159, 113),
  [320] = Vertex.new(-100, 372, 108, 184, 165, 117),
})
local breegthTooth = Model.new(138, {
  [102] = Vertex.new(44, 12, 52, 145, 135, 111),
  [103] = Vertex.new(44, 12, 52, 145, 135, 111),
  [107] = Vertex.new(44, 12, 52, 145, 135, 111),
  [122] = Vertex.new(-16, 12, -60, 155, 145, 119),
  [125] = Vertex.new(-16, 12, -60, 155, 145, 119),
})
local mango = Model.new(132, {
  [39] = Vertex.new(-20, 48, 52, 151, 118, 78),
  [104] = Vertex.new(-4, 36, -60, 141, 80, 57),
  [107] = Vertex.new(-4, 36, -60, 141, 80, 57),
  [110] = Vertex.new(-4, 36, -60, 141, 80, 57),
  [113] = Vertex.new(-4, 36, -60, 141, 80, 57),
})
local mangoWithTooth = Model.new(258, {
  [11] = Vertex.new(-12, 124, 80, 130, 121, 99),
  [31] = Vertex.new(-12, 152, 44, 145, 135, 111),
  [95] = Vertex.new(-12, 124, 80, 145, 135, 111),
  [102] = Vertex.new(-12, 124, 80, 145, 135, 111),
  [106] = Vertex.new(-12, 124, 80, 145, 135, 111),
})
local gromblodTooth = Model.new(138, {
  [102] = Vertex.new(44, 12, 52, 145, 135, 111),
  [103] = Vertex.new(44, 12, 52, 145, 135, 111),
  [107] = Vertex.new(44, 12, 52, 145, 135, 111),
  [122] = Vertex.new(-16, 12, -60, 155, 145, 119),
  [125] = Vertex.new(-16, 12, -60, 155, 145, 119),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Use any fairy ring to travel to Zanaris with Nuff's certificate in your inventory. If you can start the quest, you will be redirected to the Sparse Plane.<ul><li>If you cannot start the quest, enter and leave Zanaris via fairy rings repeatedly until Fairy Very Wise appears and talks to you. It may require up to 30 minutes after completing A Fairy Tale II - Cure a Queen. If you've declined her before, you have to travel by fairy ring BIR to reach her.</li></ul>",
    title = "Starting out",
    neededItems = {
      ["Nuff's certificate"] = { quantity = 1 },
      ["Dramen staff"] = { quantity = 1 },
      ["Lunar staff"] = { quantity = 1 },
    },
    postconditions = { Condition.DistanceTo:new(2455, 965, 4396, 4) },
  },
  {
    text = "Talk to Fairy Very Wise.",
    actions = {
      Action.ModelHighlight:new(fairyVeryWise),
      Action.ConversationHighlight:new("Of course I'll help. What do I need to do?"),
      Action.ConversationHighlight:new("Please teleport me there right away!"),
    },
    postconditions = { Condition.DistanceTo:new(1560, 973, 4235, 4) },
  },
  {
    text = "Talk to the Fairy Queen in the headquarters.",
    actions = {
      Action.ModelHighlight:new(fairyQueen),
      Action.ConversationHighlight:new("Yes, I'll help."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the Fairy Queen.",
    actions = { Action.ModelHighlight:new(fairyQueen) },
    postconditions = { Condition.ConversationText:new("ready to watch it") },
  },
  {
    text = "Turn on the projector to watch the first briefing.",
    title = "Briefing",
    warning = "Be wary of cutscenes that may trigger epilepsy.",
    actions = {
      Action.Direction:new(1584.85, 1369, 4241),
      Action.ConversationHighlight:new("Show a non-flickering briefing."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Talk to the Tooth Fairy again.",
    actions = {
      Action.ModelHighlight:new(toothFairy),
      Action.ConversationHighlight:new("Right, I understand about fairy tooth magic now."),
      Action.ConversationHighlight:new("Yes, I'm ready."),
    },
    postconditions = { Condition.ConversationText:new("the projector when") },
  },
  {
    text = "Turn on the projector to watch the second briefing.",
    actions = {
      Action.Direction:new(1584.85, 1369, 4241),
      Action.ConversationHighlight:new("Show a non-flickering briefing."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Agree to collect the Tooth Fairy's wand.",
    actions = {
      Action.ModelHighlight:new(toothFairy),
      Action.ConversationHighlight:new("Yes, I'll go and collect your wand."),
    },
    postconditions = { Condition.ConversationText:new("go get some") },
  },
  {
    text = "Pick up a hammer and secateurs to the north-west.",
    actions = {
      Action.ModelHighlight:new(Models.items["hammer"], { highlightPriority = "closest" }),
      Action.ModelHighlight:new(Models.items["secateurs"], { highlightPriority = "closest" }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["hammer"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.items["hammer"], { highlightPriority = "closest" }),
      Action.ModelHighlight:new(Models.items["secateurs"], { highlightPriority = "closest" }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["secateurs"]) },
  },
  {
    text = "Use the hammer on the secateurs.",
    actions = {
      Action.InventoryHighlight:new(Models.items["hammer"]),
      Action.InventoryHighlight:new(Models.items["secateurs"]),
    },
    postconditions = { Condition.InventoryContains:new(toothExtractor) },
  },
  {
    text = "Return to Zanaris.",
    actions = { Action.Direction:new(1560, 973, 4234) },
    postconditions = { Condition.DistanceTo:new(2412, 989, 4434, 0) },
  },
  {
    text = "Use the tooth extractor on the door to the house north-west of Zanaris bank.",
    title = "Gnarly",
    neededItems = {
      ["Bucket of milk"] = { quantity = 1 },
      ["Tooth extractor"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(2379.5, 1149, 4468),
      Action.InventoryHighlight:new(toothExtractor),
    },
    postconditions = { Condition.ConversationText:new("lock") },
  },
  {
    text = "Talk to Gnarly inside the home.",
    actions = {
      Action.ModelHighlight:new(gnarly),
      Action.ConversationHighlight:new("Can I do something for you...in return for the wand?"),
      Action.ConversationHighlight:new("Your enamel shimmers like a river of silver in the moonlight."),
      Action.ConversationHighlight:new("I've never seen such amazing, inspired and dream-like toothiness."),
      Action.ConversationHighlight:new("I can see your bicuspid ancestry is an example to all molar-kind."),
      Action.ConversationHighlight:new("You're a wonderful guard tooth with lovely cusps."),
    },
    postconditions = { Condition.ConversationText:new("Gnarly needs milk") }, --not tested
  },
  {
    text = "Use a bucket of milk on Gnarly.",
    actions = {
      Action.ModelHighlight:new(gnarly),
      Action.InventoryHighlight:new(Models.items["bucket of milk"]),
      Action.ConversationHighlight:new("Okay, I'll close my eyes."),
      Action.ConversationHighlight:new("Okay, okay! Keep your enamel on, I'll pick you up."),
    },
    postconditions = { Condition.InventoryContains:new(gnarlyItem) },
  },
  {
    text = "Return to the Fairy Resistance HQ.",
    actions = {
      Action.Direction:new(2412, 989, 4434),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
    postconditions = { Condition.DistanceTo:new(1561, 917, 4234, 4) },
  },
  {
    text = "Talk to the Tooth Fairy.",
    actions = { Action.ModelHighlight:new(toothFairy) },
    postconditions = { Condition.ConversationText:new("turn on the projector") },
  },
  {
    text = "Turn on the projector.",
    actions = {
      Action.Direction:new(1584.85, 1369, 4241),
      Action.ConversationHighlight:new("Show a non-flickering briefing."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { postconditions = { Condition.NotInInstance:new() } },
  {
    text = "After the briefing, agree to the tooth extraction.",
    actions = {
      Action.ConversationHighlight:new("Yes, I understand all of that."),
      Action.ConversationHighlight:new("Yes, I accept."),
      Action.ConversationHighlight:new("Pain? Bring it on! Replace my tooth."),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("Okay, thanks") },
  },
  {
    text = "Step onto the yellow flowers and use the wave emote.",
    actions = { Action.Direction:new(1584, 1245, 4237, { tile = true }) },
    postconditions = { Condition.ConversationText:new("reawwy good") },
  },
  {
    text = "Return to Zanaris.",
    actions = { Action.Direction:new(1560, 973, 4234) },
    postconditions = { Condition.DistanceTo:new(2412, 989, 4434, 0) },
  },
  {
    text = "Talk to General Bre'egth outside of the Zanaris mill, northwest of the fairy ring.",
    title = "Bre'egth's Tooth",
    actions = {
      Action.ModelHighlight:new(breegth),
      Action.ConversationHighlight:new("What's wrong with your mouth?"),
    },
    postconditions = { Condition.ConversationText:new("sure to do that") },
  },
  {
    text = "Talk to a nearby Ork.",
    actions = {
      Action.ModelHighlight:new(ork),
      Action.ConversationHighlight:new("Do you know anything about K'Chunk?"),
    },
    postconditions = { Condition.ConversationText:new("boring me") },
  },
  {
    text = "Talk to Fairy Fixit near the fairy ring.",
    actions = {
      Action.Direction:new(2412, 941, 4433, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["fairy fixit"], { distance = 24 }),
      Action.ConversationHighlight:new("What do you know about K'Chunk?"),
    },
    postconditions = { Condition.ConversationText:new("many thanks") },
  },
  {
    text = "Teleport to DIP.<ul><li>Recommended: Bring 4 regular planks and 8 nails of any kind to fix the bridge by using a plank on the broken bridge.</li><li>Recommended: Bring 8 willow logs if you have level 85 Firemaking, light the bonfire for K'Chunk and receive 10,000 Firemaking experience.</li></ul>",
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = { Condition.DistanceTo:new(3763, 165, 2930, 8) },
  },
  {
    text = "Talk to K'Chunk.",
    actions = {
      Action.ModelHighlight:new(kChunk),
      Action.ConversationHighlight:new("You knocked out Bre'egth's tooth, didn't you?"),
    },
    postconditions = { Condition.ConversationText:new("tricksy fing") },
  },
  {
    text = "Pickpocket him for the tooth.",
    actions = { Action.ModelHighlight:new(kChunk) },
    postconditions = { Condition.InventoryContains:new(breegthTooth) },
  },
  {
    text = "Teleport to BKQ.",
    title = "Gromblod's Tooth",
    actions = { Action.Direction:new(3763, 165, 2930) },
    postconditions = {
      Condition.DistanceTo:new(3041, 1565, 4532, 4),
      Condition.DistanceTo:new(2412, 989, 4434, 4),
    },
  },
  { --if user accidentally teleports to Zanaris
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = { Condition.DistanceTo:new(3041, 1565, 4532, 4) },
  },
  {
    text = "Talk to the Wood Dryad to the west.",
    actions = {
      Action.Direction:new(3029, 885, 4518, { distance = 12 }),
      Action.ModelHighlight:new(woodDryad, { distance = 16 }),
      Action.ConversationHighlight:new("Have you seen Gromblod?"),
    },
    postconditions = { Condition.ConversationText:new("can be done") },
  },
  {
    text = "Talk to General Gromblod.",
    actions = { Action.ModelHighlight:new(gromblod) },
    postconditions = { Condition.ConversationText:new("let's see what") },
  },
  {
    text = "Talk to the Wood Dryad again.",
    actions = { Action.ModelHighlight:new(woodDryad) },
    postconditions = { Condition.ConversationText:new("CLR") },
  },
  {
    text = "Teleport to CLR.",
    actions = { Action.Direction:new(3041, 1549, 4532) },
    postconditions = { Condition.DistanceTo:new(2735, 5669, 2742, 4) },
  },
  {
    text = "Pick a mango.<ul><li>Recommended: unblock this end of the hollow log if you haven't yet.</li></ul>",
    actions = { Action.ModelHighlight:new(mangoTree) },
    postconditions = { Condition.InventoryContains:new(mango) },
  },
  {
    text = "Return to BKQ.",
    actions = { Action.Direction:new(2735, 5669, 2742) },
    postconditions = { Condition.DistanceTo:new(3041, 1565, 4532, 4) },
  },
  {
    text = "Talk to Gromblod.",
    actions = { Action.ModelHighlight:new(gromblod) },
    postconditions = { Condition.ModelVisible:new(mangoWithToothObj) },
  },
  {
    text = "Pick up the mango with tooth.",
    actions = { Action.ModelHighlight:new(mangoWithToothObj) },
    postconditions = { Condition.InventoryContains:new(mangoWithTooth) },
  },
  {
    text = "Extract the tooth from the mango with tooth.",
    actions = {
      Action.InventoryHighlight:new(mangoWithTooth),
      Action.InventoryHighlight:new(toothExtractor),
    },
    postconditions = { Condition.InventoryContains:new(gromblodTooth) },
  },
  {
    text = "Teleport to ALP.",
    title = "Shredflesh's Tooth",
    actions = { Action.Direction:new(3041, 1549, 4532) },
    postconditions = { Condition.DistanceTo:new(2468, 965, 4189, 4) },
  },
  {
    text = "Talk to General Shredflesh.",
    warning = "There's no tracking for this step.",
    actions = {
      Action.ModelHighlight:new(shredflesh),
      Action.ConversationHighlight:new("Can I help with something?"),
      Action.ConversationHighlight:new("So your tooth is really hurting, is it?"),
      Action.ConversationHighlight:new("Which tooth is it that's so terribly painful and hurting?"),
      Action.ConversationHighlight:new("And the pain is very high is it?"),
      Action.ConversationHighlight:new("I'm a dentist; I can extract it for you!"),
      Action.ConversationHighlight:new("What's a bit of pain for a huge ork hero like you?"),
      Action.ConversationHighlight:new("If the tooth isn't pulled out, all your teeth will hurt much worse!"),
      Action.ConversationHighlight:new("Let me pull it out! What are you, an ork or a goblin?"),
      Action.ConversationHighlight:new("I'll make sure it doesn't hurt a bit; I'm a professional."),
    },
    postconditions = { Condition.ConversationText:new("test") }, --I space bar'd through all dialogue by accident
  },
  {
    text = "Use the extractor on him.",
    actions = {
      Action.ModelHighlight:new(shredflesh),
      Action.InventoryHighlight:new(toothExtractor),
    },
    postconditions = { Condition.InventoryContains:new(gromblodTooth, 2) },
  },
  {
    text = "Clear the pile of rocks behind Shredflesh.",
    actions = { Action.ModelHighlight:new(rockPile) },
    postconditions = { Condition.ModelNotVisible:new(rockPile) },
  },
  {
    text = "Return to the hideout.",
    actions = { Action.Direction:new(2468, 965, 4189) },
    postconditions = { Condition.DistanceTo:new(1560, 941, 4234, 4) },
  },
  {
    text = "Talk to the Tooth Fairy.",
    actions = { Action.ModelHighlight:new(toothFairy) },
    postconditions = { Condition.ConversationText:new("on the projector") },
  },
  {
    text = "Turn on the projector.",
    actions = { Action.Direction:new(1584.85, 1369, 4241) },
    postconditions = { Condition.ConversationText:new("Fairy Godfather") },
  },
  {
    text = "Continue talking to the Tooth Fairy.",
    actions = { Action.ModelHighlight:new(toothFairy) },
    postconditions = { Condition.ConversationText:new("you are dismissed") },
  },
  {
    text = "Prepare for the final battle. Move to the next step manually when you're ready.<ul><li>You can bank your dramen/lunar staff.</li><li>Bring combat gear and food.</li><li>Bring the 3 ork teeth.</li><li>Bring the fairy wand.</li></ul>",
    title = "The final battle",
    neededItems = { ["Fairy wand"] = { quantity = 1 } },
  },
  {
    text = "Teleport to BIR, DIP, CLR, ALP. (If the fairy ring at BIR does not appear, lobby and start over.)",
    -- feelin lazy
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Look through fairy tree where you'll encounter a cutscene.",
    actions = { Action.ModelHighlight:new(fairyTree) },
    postconditions = { Condition.ConversationText:new("Queen Bee") },
  },
  {
    postconditions = {
      Condition.DistanceFrom:new(0, 0, 0, 100, true), --not tested
      Condition.ModelVisible:new(toothFairy), --unsure if she was here before the cutscene
    },
  },
  {
    text = "After the cutscene talk to the Tooth Fairy.",
    actions = { Action.ModelHighlight:new(toothFairy) },
    postconditions = { Condition.ConversationText:new("let him escape") },
  },
  {
    text = "Look through the fairy tree.",
    actions = { Action.ModelHighlight:new(fairyTree) },
    postconditions = { Condition.ChatText:new("You squeeze past") },
  },
  {
    text = "How to kill the Fairy Godfather:<ul><li>Use all 3 teeth on the farming patches.</li><li>Stand by the patches and kill the generals and ork warriors; Prioritize any general using the drum near the rift, to stop new orks from spawning.</li><li>Once the teeth have grown, instruct them to fight.</li><li>Focus on ork warriors while the teeth focus on killing the generals.</li><li>After you and the teeth kill the generals, kill the godfather (let the teeth attack the godfather to reduce the shields). Ensure all 3 teeth are attacking the godfather at once, as each one removes a layer of his 3-layer shield (note: if all 3 teeth aren't attacking the godfather, you will not land hits as his shield will still be active)</li></ul>",
    actions = {
      Action.InventoryHighlight:new(breegthTooth),
      Action.InventoryHighlight:new(gromblodTooth),
      Action.Direction:new(6.5, 0, -13.5, { instance = true, tile = true }),
      Action.Direction:new(14.5, 0, -11.5, { instance = true, tile = true }),
      Action.Direction:new(20.5, 0, -4.5, { instance = true, tile = true }),
    },
    postconditions = { Condition.ConversationText:new("Okay, okay") },
  },
  {
    text = "Talk to the Fairy Godfather for a cutscene.",
    title = "Finishing up",
    actions = { Action.ModelHighlight:new(Models.npcs["fairy godfather"]) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Continue talking to the Fairy Queen.",
    actions = {
      Action.ModelHighlight:new(fairyQueen),
      Action.ConversationHighlight:new("Yes, please teleport me there now."),
    },
    postconditions = { Condition.DistanceTo:new(3077, 1301, 3253, 8) },
  },
  {
    text = "Talk to Martin the Master Gardener.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["martin the master gardener"]),
      Action.ConversationHighlight:new("Talk about farming problems and fairies."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "A Fairy Tale III - Battle at Ork's Rift",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1273708800,
  prereqQuests = { "A Fairy Tale II - Cure a Queen" },
  questReqs = {
    Types.QuestReq.skill("Crafting", 36),
    Types.QuestReq.skill("Farming", 54),
    Types.QuestReq.skill("Thieving", 51),
  },
  neededItems = {
    ["Nuff's certificate"] = { quantity = 1, model = Models.items["nuff's certificate"], duringQuest = true },
    ["Bucket of milk"] = { quantity = 1, model = Models.items["bucket of milk"], duringQuest = true },
    ["Dramen/lunar staff"] = { quantity = 1, model = Models.items["dramen staff"] },
    ["Secateurs"] = { quantity = 1, model = Models.items["secateurs"], duringQuest = true },
    ["Hammer"] = { quantity = 1, model = Models.items["hammer"], duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = {
    ["General Grombold"] = { level = "85", quantity = 1 },
    ["General Shredflesh"] = { level = "85", quantity = 1 },
    ["General Bre'egth"] = { level = "85", quantity = 1 },
    ["Fairy Godfather"] = { level = "84", quantity = 1 },
  },
})
