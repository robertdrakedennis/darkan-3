local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local shantay = Model.new(5409, {
  [4444] = Vertex.new(0, 735, -7, 54, 142, 28),
  [4791] = Vertex.new(-8, 744, -45, 127, 122, 116),
  [4799] = Vertex.new(-19, 745, -42, 127, 122, 116),
  [4802] = Vertex.new(8, 744, -45, 127, 122, 116),
  [4812] = Vertex.new(19, 745, -42, 127, 122, 116),
})
local irena = Model.new(3699, {
  [2289] = Vertex.new(24, 738, -38, 34, 32, 31),
  [2313] = Vertex.new(-24, 738, -38, 34, 32, 31),
  [2335] = Vertex.new(-2, 716, -57, 123, 91, 63),
  [2341] = Vertex.new(2, 716, -57, 123, 91, 63),
  [2345] = Vertex.new(6, 716, -52, 123, 91, 63),
})
local mercenaryCaptain = Model.new(5211, {
  [2971] = Vertex.new(-2, 725, -59, 106, 78, 55),
  [2979] = Vertex.new(2, 725, -59, 106, 78, 55),
  [2981] = Vertex.new(7, 724, -51, 106, 78, 55),
  [4173] = Vertex.new(-65, 670, 50, 66, 58, 50),
  [4204] = Vertex.new(65, 670, 50, 66, 58, 50),
})
local maleSlave = Model.new(4275, {
  [2590] = Vertex.new(-2, 725, -59, 106, 78, 55),
  [2595] = Vertex.new(-7, 724, -51, 106, 78, 55),
  [2598] = Vertex.new(2, 725, -59, 106, 78, 55),
  [2600] = Vertex.new(7, 724, -51, 106, 78, 55),
  [3902] = Vertex.new(-30, 721, -31, 47, 36, 14),
})
local caveGuard = Model.new(5919, {
  [2356] = Vertex.new(-2, 725, -59, 83, 57, 34),
  [2361] = Vertex.new(-7, 724, -51, 83, 57, 34),
  [2364] = Vertex.new(2, 725, -59, 83, 57, 34),
  [2366] = Vertex.new(7, 724, -51, 83, 57, 34),
  [3042] = Vertex.new(-76, 660, 32, 95, 87, 87),
})
local alShabim = Model.new(5613, {
  [3505] = Vertex.new(-2, 725, -59, 73, 49, 29),
  [3513] = Vertex.new(2, 725, -59, 73, 49, 29),
  [3515] = Vertex.new(7, 724, -51, 73, 49, 29),
  [5229] = Vertex.new(-65, 670, 50, 89, 14, 7),
  [5260] = Vertex.new(65, 670, 50, 89, 14, 7),
})
local captainSiad = Model.new(3861, {
  [2110] = Vertex.new(-2, 725, -59, 87, 59, 35),
  [2115] = Vertex.new(-7, 724, -51, 87, 59, 35),
  [2118] = Vertex.new(2, 725, -59, 87, 59, 35),
  [2120] = Vertex.new(7, 724, -51, 87, 59, 35),
  [3031] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local ana = Model.new(4017, {
  [2517] = Vertex.new(24, 738, -38, 30, 29, 27),
  [2541] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [2563] = Vertex.new(-2, 716, -57, 106, 78, 55),
  [2569] = Vertex.new(2, 716, -57, 106, 78, 55),
  [2573] = Vertex.new(6, 716, -52, 106, 78, 55),
})
local mineCartDriver = Model.new(3081, {
  [1948] = Vertex.new(-2, 725, -59, 106, 78, 55),
  [1953] = Vertex.new(-7, 724, -51, 106, 78, 55),
  [1956] = Vertex.new(2, 725, -59, 106, 78, 55),
  [1958] = Vertex.new(7, 724, -51, 106, 78, 55),
  [2708] = Vertex.new(-30, 721, -31, 47, 36, 14),
})
--#endregion
--#region Objects
local caveEntranceDoors = Model.new(2304, {
  [254] = Vertex.new(7604, 1868, 1508, 129, 112, 25),
  [1248] = Vertex.new(7868, 1936, 1416, 74, 64, 46),
  [1304] = Vertex.new(7804, 1916, 1416, 74, 64, 46),
  [1407] = Vertex.new(7756, 1868, 1508, 129, 112, 25),
  [1415] = Vertex.new(7872, 1896, 1516, 129, 112, 25),
})
local whiteBuildingLadder = Model.new(756, {
  [17] = Vertex.new(5264, 3432, 6600, 69, 54, 28),
  [551] = Vertex.new(5264, 3340, 6548, 94, 81, 59),
  [557] = Vertex.new(5264, 3324, 6584, 82, 68, 42),
  [587] = Vertex.new(5264, 3448, 6564, 94, 81, 59),
  [593] = Vertex.new(5264, 3432, 6600, 82, 68, 42),
})
--#endregion
--#region Items
local desertShirt = Model.multi({
  Model.new(756, {
    [1] = Vertex.new(-84, -1, -28, 133, 130, 122),
    [2] = Vertex.new(-78, -1, -70, 133, 130, 122),
    [3] = Vertex.new(-77, 0, -27, 133, 130, 122),
    [4] = Vertex.new(-89, 18, 30, 156, 152, 142),
    [5] = Vertex.new(-93, 12, 52, 156, 152, 142),
  }),
  Model.new(9, {
    [1] = Vertex.new(-5, 8, -31, 54, 142, 28, 0.000),
    [2] = Vertex.new(0, 8, -39, 54, 142, 28, 0.000),
    [3] = Vertex.new(-10, 8, -39, 54, 142, 28, 0.000),
    [4] = Vertex.new(71, 0, 52, 54, 142, 28, 0.000),
    [5] = Vertex.new(76, 0, 41, 54, 142, 28, 0.000),
  }),
})
local desertRobe = Model.new(471, {
  [1] = Vertex.new(-59, 39, -165, 88, 86, 81),
  [2] = Vertex.new(-82, 0, -165, 88, 86, 81),
  [3] = Vertex.new(-96, 0, -165, 88, 86, 81),
  [4] = Vertex.new(-37, 42, -165, 88, 86, 81),
  [8] = Vertex.new(-37, 0, -165, 88, 86, 81),
})
local desertBoots = Model.new(240, {
  [1] = Vertex.new(64, 112, -32, 161, 148, 147),
  [2] = Vertex.new(48, 96, -16, 161, 148, 147),
  [3] = Vertex.new(48, 100, 24, 161, 148, 147),
  [4] = Vertex.new(-8, 112, -32, 161, 148, 147),
  [5] = Vertex.new(-24, 96, -16, 161, 148, 147),
})
--#endregion
--#region Quest Items
local slaveShirt = Model.new(591, {
  [1] = Vertex.new(24, -4, 144, 94, 88, 59),
  [2] = Vertex.new(48, 12, 116, 94, 88, 59),
  [3] = Vertex.new(24, 8, 140, 94, 88, 59),
  [4] = Vertex.new(-64, -8, -56, 94, 88, 59),
  [5] = Vertex.new(-76, -8, 60, 94, 88, 59),
})
local slaveRobe = Model.new(360, {
  [1] = Vertex.new(-100, 24, -152, 80, 71, 32),
  [2] = Vertex.new(-112, 24, -188, 80, 71, 32),
  [3] = Vertex.new(-112, 20, -176, 80, 71, 32),
  [4] = Vertex.new(116, 24, -164, 80, 71, 32),
  [5] = Vertex.new(112, 8, -152, 80, 71, 32),
})
local slaveBoots = Model.new(240, {
  [1] = Vertex.new(64, 112, -32, 85, 78, 44),
  [2] = Vertex.new(48, 96, -16, 85, 78, 44),
  [3] = Vertex.new(48, 100, 24, 85, 78, 44),
  [4] = Vertex.new(-8, 112, -32, 85, 78, 44),
  [5] = Vertex.new(-24, 96, -16, 85, 78, 44),
})
local metalKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 117, 107, 89),
  [2] = Vertex.new(-12, 16, -84, 117, 107, 89),
  [3] = Vertex.new(-20, 16, -92, 117, 107, 89),
  [6] = Vertex.new(-60, 16, -52, 117, 107, 89),
  [7] = Vertex.new(-32, 16, -48, 117, 107, 89),
})
local bedabinKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 113, 72, 9),
  [2] = Vertex.new(-12, 16, -84, 113, 72, 9),
  [3] = Vertex.new(-20, 16, -92, 113, 72, 9),
  [6] = Vertex.new(-60, 16, -52, 113, 72, 9),
  [7] = Vertex.new(-32, 16, -48, 113, 72, 9),
})
local technicalPlans = Model.new(567, {
  [1] = Vertex.new(-216, 12, -8, 15, 16, 51),
  [2] = Vertex.new(-212, 32, -8, 15, 16, 51),
  [3] = Vertex.new(-188, 32, 212, 15, 16, 51),
  [6] = Vertex.new(-192, 12, 212, 15, 16, 51),
  [7] = Vertex.new(-196, 0, -8, 15, 16, 51),
})
local prototypeDart = Model.new(72, {
  [1] = Vertex.new(-56, 4, 60, 57, 42, 23),
  [2] = Vertex.new(0, 12, -48, 57, 42, 23),
  [3] = Vertex.new(0, 0, -48, 57, 42, 23),
  [6] = Vertex.new(16, 0, -40, 57, 42, 23),
  [9] = Vertex.new(16, 12, -40, 57, 42, 23),
})
local pineapple = Model.new(600, {
  [1] = Vertex.new(-56, 236, 40, 48, 77, 49),
  [2] = Vertex.new(0, 196, 0, 48, 77, 49),
  [3] = Vertex.new(-68, 240, 20, 48, 77, 49),
  [5] = Vertex.new(-104, 264, 48, 54, 86, 55),
  [7] = Vertex.new(-56, 272, 40, 48, 77, 49),
})
local barrel = Model.new(594, {
  [1] = Vertex.new(4, 388, 96, 118, 97, 60),
  [2] = Vertex.new(84, 388, 84, 118, 97, 60),
  [3] = Vertex.new(68, 388, 68, 118, 97, 60),
  [7] = Vertex.new(96, 388, 0, 118, 97, 60),
  [9] = Vertex.new(72, 388, -60, 118, 97, 60),
})
local anaInBarrel = Model.new(1221, {
  [1] = Vertex.new(-120, 388, 0, 137, 113, 70),
  [2] = Vertex.new(-72, 388, -60, 137, 113, 70),
  [3] = Vertex.new(-88, 388, -72, 137, 113, 70),
  [6] = Vertex.new(-96, 388, 0, 137, 113, 70),
  [7] = Vertex.new(96, 388, 0, 137, 113, 70),
})
local cart = Model.new(3150, {
  [13] = Vertex.new(-328, 336, 324, 88, 73, 45),
  [14] = Vertex.new(340, 280, 324, 88, 73, 45),
  [15] = Vertex.new(340, 336, 324, 88, 73, 45),
  [17] = Vertex.new(-328, 280, 324, 88, 73, 45),
  [18] = Vertex.new(340, 280, 324, 88, 73, 45),
})
local cartWithAna = Model.new(3978, {
  [2569] = Vertex.new(-328, 336, 324, 88, 73, 45),
  [2570] = Vertex.new(340, 280, 324, 88, 73, 45),
  [2571] = Vertex.new(340, 336, 324, 88, 73, 45),
  [2573] = Vertex.new(-328, 280, 324, 88, 73, 45),
  [2574] = Vertex.new(340, 280, 324, 88, 73, 45),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "If you don't have desert clothes, buy them from Shantay.",
    title = "Getting started",
    actions = { Action.Direction:new(3304, 1093, 3119) },
    postconditions = {
      Condition.DistanceTo:new(3304, 1093, 3119, 8),
      Condition.InventoryContains:new(desertShirt),
      Condition.InventoryContains:new(desertRobe),
      Condition.InventoryContains:new(desertBoots),
    },
  },
  {
    actions = { Action.ModelHighlight:new(shantay) },
    postconditions = { Condition.InventoryContains:new(desertBoots) },
  },
  {
    actions = { Action.ModelHighlight:new(shantay) },
    postconditions = { Condition.InventoryContains:new(desertRobe) },
  },
  {
    actions = { Action.ModelHighlight:new(shantay) },
    postconditions = { Condition.InventoryContains:new(desertShirt) },
  },
  {
    text = "Begin the quest by talking to Irena (south of Shantay Pass).",
    actions = {
      Action.ModelHighlight:new(irena),
      Action.ConversationHighlight:new("What's the matter?"),
      Action.ConversationHighlight:new("When did she go into the desert?"),
      Action.ConversationHighlight:new("I'll look for your daughter."),
      Action.ConversationHighlight:new("Okay Irena, calm down. I'll get your daughter back for you."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Finish the conversation with Irena.",
    actions = { Action.ModelHighlight:new(irena) },
    postconditions = { Condition.ConversationText:new("lead to the desert mining camp") },
  },
  {
    text = "Run south and slightly west to the gate of the Desert Mining Camp.",
    title = "Desert mining camp",
    actions = { Action.Direction:new(3265, 1253, 3030) },
    postconditions = { Condition.DistanceTo:new(3265, 1253, 3030, 8) },
  },
  {
    text = "<i><b>Talk</i></b> to Mercenary Captain.",
    actions = {
      Action.ModelHighlight:new(mercenaryCaptain),
      Action.ConversationHighlight:new("Wow! A real captain!"),
      Action.ConversationHighlight:new("I'd love to work for a tough guy like you!"),
      Action.ConversationHighlight:new("Can't I do something for a strong Captain like you?"),
      Action.ConversationHighlight:new("Sorry Sir, I don't think I can do that."),
      Action.ConversationHighlight:new("It's a funny captain who can't fight his own battles!"),
    },
  },
  {
    text = "Kill the Mercenary Captain to obtain a metal key.<ul><li>The metal key can be added to the steel key ring after One Small Favour.</li></ul>",
    actions = { Action.ModelHighlight:new(mercenaryCaptain) },
    postconditions = {
      Condition.InventoryContains:new(metalKey),
      Condition.ConversationText:new("add it to your inventory."),
    },
  },
  {
    text = "Unequip <b>all</b> armour (includes amulets, rings, etc.) and weapons, and <b>keep</b> them unequipped whenever inside the mining camp or mine.<br><br>Once you are done unequipping everything, open the gate to enter the camp.",
    actions = { Action.Direction:new(3273.5, 2373, 3028.5) },
    postconditions = { Condition.DistanceTo:new(3276, 2373, 3029, 2) },
  },
  {
    text = "Talk to the male slave on the east of the camp.<ul><li>If a guard catches you unlocking the slave's chains and you end up in the cell, bend the bars on the window and escape. Talk to the slave to try again.</li></ul>",
    title = "Entering the mine",
    actions = { Action.Direction:new(3301, 2629, 3025) },
    postconditions = { Condition.ModelVisible:new(maleSlave) },
  },
  {
    actions = {
      Action.ModelHighlight:new(maleSlave),
      Action.ConversationHighlight:new("I've just arrived."),
      Action.ConversationHighlight:new("Oh yes, that sounds interesting."),
      Action.ConversationHighlight:new("What's that then?"),
      Action.ConversationHighlight:new("I can try to undo them for you."),
      Action.ConversationHighlight:new("It's funny you should say that..."),
      Action.ConversationHighlight:new("Yeah, okay, let's give it a go."),
      Action.ConversationHighlight:new("Yes, I'll trade."),
    },
    postconditions = {
      Condition.ConversationText:new("Yeah, good luck to you too!"),
      Condition.InventoryContains:new(slaveShirt),
    },
  },
  {
    text = "While wearing the slave outfit, open the mine door entrance. The outfit <b>must</b> be equipped any time you enter the mine.",
    actions = { Action.InventoryHighlight:new(slaveShirt) },
    postconditions = { Condition.InventoryDoesNotContain:new(slaveShirt) },
  },
  {
    actions = { Action.InventoryHighlight:new(slaveRobe) },
    postconditions = { Condition.InventoryDoesNotContain:new(slaveRobe) },
  },
  {
    actions = { Action.InventoryHighlight:new(slaveBoots) },
    postconditions = { Condition.InventoryDoesNotContain:new(slaveBoots) },
  },
  {
    actions = { Action.Direction:new(3301.5, 2629, 3036) },
    postconditions = { Condition.DistanceTo:new(3278, 1429, 9427, 8) },
  },
  {
    text = "Follow the cave around until you come to a guarded cave entrance.",
    actions = { Action.Direction:new(3280, 1205, 9414.5) },
    postconditions = { Condition.DistanceTo:new(3280, 1205, 9414.5, 8) },
  },
  {
    text = "Making sure not to attack him, talk to one of the guards.",
    actions = {
      Action.ModelHighlight:new(caveGuard),
      Action.ConversationHighlight:new("I'd like to mine in a different area."),
      Action.ConversationHighlight:new("Yes sir, you're quite right sir."),
      Action.ConversationHighlight:new("Yes sir, we understand each other perfectly."),
    },
    postconditions = { Condition.ConversationText:new("The guard moves back to his post and winks at you knowingly.") },
  },
  {
    text = "Head back to the mine doors and exit.",
    actions = { Action.ModelHighlight:new(caveEntranceDoors) },
    postconditions = { Condition.DistanceTo:new(3301.5, 2629, 3036, 8) },
  },
  {
    text = "Unequip your slave clothing and leave the Desert Mining Camp through the gate.",
    actions = { Action.Direction:new(3273.5, 2373, 3028.5) },
    postconditions = { Condition.DistanceTo:new(3270, 2373, 3028, 4) },
  },
  {
    text = "Run west to the Bedabin Camp.",
    actions = { Action.Direction:new(3171, 645, 3028) },
    postconditions = { Condition.DistanceTo:new(3170, 645, 3028, 4) },
  },
  {
    text = "Talk to Al Shabim in the southernmost tent to receive the Bedabin key.",
    actions = {
      Action.ModelHighlight:new(alShabim),
      Action.ConversationHighlight:new("I am looking for a pineapple."),
      Action.ConversationHighlight:new("Yes, I'm interested."),
    },
    postconditions = {
      Condition.ConversationText:new("Al Shabim gives you a key."),
      Condition.InventoryContains:new(bedabinKey),
    },
  },
  {
    text = "Run east back to the Desert Mining Camp and climb up the ladder in the white building.",
    title = "The plans",
    neededItems = { ["Bronze bar"] = { quantity = 1 }, ["Feather"] = { quantity = 10 } },
    actions = { Action.Direction:new(3273.5, 2373, 3028.5) },
    postconditions = { Condition.DistanceTo:new(3276, 2373, 3029, 2) },
  },
  {
    actions = { Action.Direction:new(3290, 2629, 3034) },
    postconditions = { Condition.DistanceTo:new(3291, 2629, 3032, 2) },
  },
  {
    actions = { Action.ModelHighlight:new(whiteBuildingLadder) },
    postconditions = { Condition.DistanceToWithHeight:new(3290, 3589, 3037, 8) },
  },
  {
    text = "On the western wall search the southernmost bookcase for a book on sailing.",
    actions = { Action.Direction:new(3284, 3589, 3032.5) },
    postconditions = { Condition.ConversationText:new("You notice several books on the subject of sailing.") },
  },
  {
    text = "Talk to Captain Siad in the same room and ask him if you can chat with him, then mention the sailing book.",
    actions = {
      Action.ModelHighlight:new(captainSiad),
      Action.ConversationHighlight:new("I wanted to have a chat?"),
      Action.ConversationHighlight:new("You seem to have a lot of books!"),
      Action.ConversationHighlight:new("So, you're interested in sailing?"),
      Action.ConversationHighlight:new("I could tell by the cut of your jib."),
    },
    postconditions = { Condition.ConversationText:new("salty sea dog") },
  },
  {
    text = "Open the chest beside the table to get the plans.",
    actions = { Action.Direction:new(3292, 3589, 3033) },
    postconditions = {
      Condition.ConversationText:new("You take out the plans."),
      Condition.InventoryContains:new(technicalPlans),
    },
  },
  {
    text = "Go back to the Bedabin Camp and show Al Shabim the plans.",
    actions = { Action.Direction:new(3171, 645, 3028) },
    postconditions = { Condition.DistanceTo:new(3170, 645, 3028, 4) },
  },
  {
    actions = {
      Action.ModelHighlight:new(alShabim),
      Action.ConversationHighlight:new("Yes, I'm very interested."),
      Action.ConversationHighlight:new("Yes, I'm kind of curious."),
    },
    postconditions = { Condition.ConversationText:new("Please bring me the item when it is finished.") },
  },
  {
    text = "Enter the tent to the north.",
    actions = { Action.Direction:new(3169, 709, 3046) },
    postconditions = { Condition.DistanceTo:new(3169, 709, 3048, 2) },
  },
  {
    text = "Use the experimental anvil with a bronze bar in your inventory.",
    actions = { Action.Direction:new(3171, 709, 3048), Action.ConversationHighlight:new("Yes. I'd like to try.") },
    postconditions = { Condition.ConversationText:new("attach feathers") },
  },
  {
    text = "Use a feather on the dart tip to make a prototype dart.",
    actions = { Action.InventoryHighlight:new(Models.items["feather"]) },
    postconditions = {
      Condition.ConversationText:new("You successfully attach the feathers to the dart tip."),
      Condition.InventoryContains:new(prototypeDart),
    },
  },
  {
    text = "Take this back to Al Shabim. He will take the prototype dart, technical plans, and Bedabin key in exchange for a bronze dart and Tenti pineapple.",
    actions = { Action.Direction:new(3171, 645, 3028) },
    postconditions = { Condition.DistanceTo:new(3170, 645, 3028, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(alShabim) },
    postconditions = {
      Condition.ConversationText:new("You receive a tasty looking pineapple from Al Shabim."),
      Condition.InventoryContains:new(pineapple),
    },
  },
  {
    text = "Head back to the cave entrance in the Desert Mining Camp.",
    title = "Finding Ana",
    actions = { Action.Direction:new(3301.5, 2629, 3036) },
    postconditions = { Condition.DistanceTo:new(3301, 2629, 3035, 8) },
  },
  {
    text = "Equip the slave outfit.",
    actions = { Action.InventoryHighlight:new(slaveShirt) },
    postconditions = { Condition.InventoryDoesNotContain:new(slaveShirt) },
  },
  {
    actions = { Action.InventoryHighlight:new(slaveRobe) },
    postconditions = { Condition.InventoryDoesNotContain:new(slaveRobe) },
  },
  {
    actions = { Action.InventoryHighlight:new(slaveBoots) },
    postconditions = { Condition.InventoryDoesNotContain:new(slaveBoots) },
  },
  {
    actions = { Action.Direction:new(3301.5, 2629, 3036) },
    postconditions = { Condition.DistanceTo:new(3278, 1429, 9427, 8) },
  },
  {
    text = "Enter the cave and make your way back to the guard.",
    actions = { Action.Direction:new(3280, 1205, 9414.5) },
    postconditions = { Condition.DistanceTo:new(3280, 1205, 9414.5, 8) },
  },
  {
    text = "Give the pineapple to the guard.",
    actions = { Action.ModelHighlight:new(caveGuard) },
    postconditions = { Condition.ConversationText:new("Yes, yes, of course... a deal's a deal!") },
  },
  {
    text = "<i>Search</i> one of the empty barrels nearby and answer the prompt to take it.",
    actions = {
      Action.Direction:new(3303, 981, 9415),
      Action.ConversationHighlight:new("Yeah, cool!"),
    },
    postconditions = { Condition.InventoryContains:new(barrel) },
  },
  {
    text = "<i>Search</i> the mine cart to ride it.<ul><li>Failing to enter the cart will cause minor damage. If you fail, attempt to enter the cart again.</li></ul>",
    actions = {
      Action.Direction:new(3303, 997, 9417),
      Action.ConversationHighlight:new("Yes, of course."),
    },
    postconditions = { Condition.DistanceTo:new(3319, 989, 9431, 3) },
  },
  {
    text = "Go down the northwestern passageway containing a large number of slaves at the end and you will find Ana.",
    actions = { Action.Direction:new(3301, 1261, 9466) },
    postconditions = { Condition.ModelVisible:new(ana) },
  },
  {
    text = "Use the barrel in your inventory on Ana to squeeze her inside it, and head back to the mine cart.",
    actions = { Action.InventoryHighlight:new(barrel), Action.ModelHighlight:new(ana) },
    postconditions = { Condition.InventoryContains:new(anaInBarrel) },
  },
  {
    text = "Use Ana on the mine cart first, then ride it yourself when it comes back.",
    title = "Rescuing Ana",
    actions = {
      Action.Direction:new(3318, 981, 9431),
      Action.InventoryHighlight:new(anaInBarrel),
      Action.ConversationHighlight:new("Yes, of course."),
    },
    postconditions = { Condition.DistanceTo:new(3302, 981, 9417, 3) },
  },
  {
    text = "Search the barrels around the cart and retrieve the one that has Ana in it.",
    actions = { Action.Direction:new(3303, 981, 9415) },
    postconditions = { Condition.InventoryContains:new(anaInBarrel) },
  },
  {
    text = "Use the winch bucket north-west of the cart to put the barrel on it.",
    actions = {
      Action.Direction:new(3292, 1445, 9423),
      Action.InventoryHighlight:new(anaInBarrel),
      Action.ConversationHighlight:new("Yes please."),
      Action.ConversationHighlight:new("I said you were very gregarious!"),
    },
    postconditions = { Condition.ConversationText:new("better get back to work") },
  },
  {
    text = "Head back to the surface and go to the winch bucket in the south-western corner of the camp.<ul><li>Do not remove your slave clothes at any point.</li></ul>",
    actions = { Action.ModelHighlight:new(caveEntranceDoors) },
    postconditions = { Condition.DistanceTo:new(3301, 2629, 3035, 3) },
  },
  {
    text = "<i>Operate</i> the winch.",
    actions = { Action.Direction:new(3279, 2629, 3018) },
    postconditions = { Condition.ConversationText:new("Get me OUT OF HERE!") },
  },
  {
    text = "Search the nearby barrel for Ana.",
    actions = { Action.Direction:new(3278, 2629, 3017) },
    postconditions = { Condition.InventoryContains:new(anaInBarrel) },
  },
  {
    text = "Use Ana on the wooden cart at the centre of the camp.",
    actions = { Action.InventoryHighlight:new(anaInBarrel), Action.ModelHighlight:new(cart) },
    postconditions = {
      Condition.ConversationText:new("And the desert heat will soon get to Ana."),
      Condition.InventoryDoesNotContain:new(anaInBarrel),
    },
  },
  {
    text = "Talk to the Mine cart driver. (Warning: If you use the wrong responses you'll have to rescue Ana again!)<ul><li>If you fail, you need to head back into the deepest part of the mine with a barrel and get Ana out again.</li></ul>",
    actions = {
      Action.ModelHighlight:new(mineCartDriver),
      Action.ConversationHighlight:new("Nice cart."),
      Action.ConversationHighlight:new("One wagon wheel says to the other, 'I'll see you around'."),
      Action.ConversationHighlight:new("'One good turn deserves another'"),
      Action.ConversationHighlight:new("Fired... no, shot perhaps!"),
      Action.ConversationHighlight:new("In for a penny in for a pound."),
      Action.ConversationHighlight:new("Well, you see, it's like this..."),
      Action.ConversationHighlight:new("Prison riot in ten minutes, get your cart out of here!"),
      Action.ConversationHighlight:new("You can't leave me here, I'll get killed!"),
    },
    postconditions = { Condition.ConversationText:new("better jump in the cart") },
  },
  {
    text = "Search the cart and answer the prompt to get on.",
    actions = { Action.ModelHighlight:new(cartWithAna), Action.ConversationHighlight:new("Yes, I'll get on.") },
    postconditions = { Condition.InventoryContains:new(anaInBarrel) },
  },
  {
    text = "Run northeast towards Shantay Pass and talk to Irena.",
    actions = { Action.Direction:new(3303, 1093, 3111) },
    postconditions = { Condition.ModelVisible:new(irena) },
  },
  {
    actions = { Action.ModelHighlight:new(irena) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Tourist Trap",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1050278400,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Fletching", 10),
    Types.QuestReq.skill("Smithing", 20),
  },
  neededItems = {
    ["Desert shirt"] = { quantity = 1, model = desertShirt, duringQuest = true },
    ["Desert robe"] = { quantity = 1, model = desertRobe, duringQuest = true },
    ["Desert boots"] = { quantity = 1, model = desertBoots, duringQuest = true },
    ["Feather"] = { quantity = 1, model = Models.items["feather"], duringQuest = true },
    ["Bronze bar (in your inventory)"] = { quantity = 1, model = Models.items["bronze bar"] },
    ["Coins"] = { quantity = 200 },
  },
  recommendedItems = {
    ["Desert heat protection"] = { quantity = 1 },
    ["Armour"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
    ["Bandit camp teleports"] = { quantity = 2 },
    ["Lockpick"] = { quantity = 1 },
    ["Backpack spaces"] = { quantity = 15 },
  },
  combatNPCs = { ["Mercenary Captain"] = { level = "15", quantity = 1 } },
})
