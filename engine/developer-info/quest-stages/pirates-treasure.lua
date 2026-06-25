local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--#region NPCs
local redbeardFrank = Model.new(7275, {
  [336] = Vertex.new(-8, 724, -54, 127, 127, 127),
  [337] = Vertex.new(-2, 725, -62, 127, 127, 127),
  [358] = Vertex.new(2, 725, -62, 127, 127, 127),
  [1293] = Vertex.new(32, 746, -13, 128, 127, 127),
  [1349] = Vertex.new(-32, 746, -13, 128, 127, 127),
})
local seaman = Model.new(3831, {
  [2176] = Vertex.new(-2, 725, -59, 106, 78, 55),
  [2181] = Vertex.new(-7, 724, -51, 106, 78, 55),
  [2184] = Vertex.new(2, 725, -59, 106, 78, 55),
  [2186] = Vertex.new(7, 724, -51, 106, 78, 55),
  [2833] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local zembo = Model.new(4995, {
  [3628] = Vertex.new(-2, 725, -59, 106, 78, 55),
  [3633] = Vertex.new(-7, 724, -51, 106, 78, 55),
  [3636] = Vertex.new(2, 725, -59, 106, 78, 55),
  [3638] = Vertex.new(7, 724, -51, 106, 78, 55),
  [4327] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local luthas = Model.new(5121, {
  [4060] = Vertex.new(-2, 725, -59, 69, 53, 35),
  [4542] = Vertex.new(48, 748, 52, 0, 0, 0),
  [4544] = Vertex.new(56, 748, 52, 0, 0, 0),
  [4548] = Vertex.new(64, 744, 52, 0, 0, 0),
  [4550] = Vertex.new(72, 744, 52, 0, 0, 0),
})
local customsOfficer = Model.new(3999, {
  [2055] = Vertex.new(24, 738, -38, 30, 29, 27),
  [2079] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [2101] = Vertex.new(-2, 716, -57, 80, 59, 32),
  [2107] = Vertex.new(2, 716, -57, 80, 59, 32),
  [2111] = Vertex.new(6, 716, -52, 80, 59, 32),
})
--#endregion
--#region Objects
local karamjaCrate = Model.new(600, {
  [276] = Vertex.new(3960, 400, 4008, 85, 57, 17),
  [387] = Vertex.new(4008, 400, 4008, 85, 57, 17),
  [405] = Vertex.new(3672, 400, 4008, 85, 57, 17),
  [507] = Vertex.new(4008, 400, 4008, 85, 57, 17),
  [531] = Vertex.new(4008, 400, 3720, 110, 81, 44),
})
local hangingWhiteApron = Model.new(120, {
  [9] = Vertex.new(-12, 616, 168, 161, 148, 147),
  [12] = Vertex.new(12, 616, 168, 161, 148, 147),
  [18] = Vertex.new(0, 624, 168, 161, 148, 147),
  [95] = Vertex.new(52, 156, 152, 161, 148, 147),
  [119] = Vertex.new(12, 616, 168, 161, 148, 147),
})
local blueMoonInnStairs = Model.new(4416, {
  [1511] = Vertex.new(6762, 2128, 1386, 127, 127, 127),
  [2351] = Vertex.new(7059, 2418, 1306, 127, 127, 127),
  [3118] = Vertex.new(6762, 2187, 1386, 127, 127, 127),
  [3145] = Vertex.new(7059, 2418, 1306, 127, 127, 127),
  [3764] = Vertex.new(7059, 2418, 1306, 127, 127, 127),
})
--#endregion
--#region Items
local whiteApron = Model.new(240, {
  [1] = Vertex.new(-84, 16, 100, 159, 146, 159),
  [2] = Vertex.new(-8, 20, 28, 159, 146, 159),
  [3] = Vertex.new(-60, 16, 108, 159, 146, 159),
  [5] = Vertex.new(-64, 8, -8, 159, 146, 159),
  [7] = Vertex.new(48, 8, 60, 159, 146, 159),
})
--#endregion
--#region Quest Items
local rum = Model.new(306, {
  [1] = Vertex.new(-4, 208, -12, 113, 101, 46),
  [2] = Vertex.new(4, 220, -12, 113, 101, 46),
  [3] = Vertex.new(4, 208, -12, 113, 101, 46),
  [5] = Vertex.new(-4, 220, -12, 113, 101, 46),
  [13] = Vertex.new(-12, 208, -4, 113, 101, 46),
})
local chestKey = Model.new(444, {
  [1] = Vertex.new(-40, 16, -56, 153, 120, 13),
  [2] = Vertex.new(-12, 16, -84, 153, 120, 13),
  [3] = Vertex.new(-20, 16, -92, 153, 120, 13),
  [6] = Vertex.new(-60, 16, -52, 153, 120, 13),
  [7] = Vertex.new(-32, 16, -48, 153, 120, 13),
})
local pirateMessage = Model.new(276, {
  [1] = Vertex.new(-28, 0, -72, 146, 146, 134),
  [2] = Vertex.new(68, 0, -60, 146, 146, 134),
  [3] = Vertex.new(20, 0, -72, 146, 146, 134),
  [5] = Vertex.new(-72, 0, -56, 146, 146, 134),
  [7] = Vertex.new(-60, 0, -4, 146, 146, 134),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Redbeard Frank just outside Port Sarim's pub.",
    title = "Getting started",
    actions = { Action.Direction:new(3049, 965, 3253) },
    postconditions = { Condition.DistanceTo:new(3049, 965, 3253, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(redbeardFrank),
      Action.ConversationHighlight:new("I'm in search of treasure."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with Redbeard Frank.",
    actions = { Action.ModelHighlight:new(redbeardFrank) },
    postconditions = { Condition.ConversationText:new("Arr, that's the spirit!") },
  },
  {
    text = "Take the ship to Karamja.",
    actions = { Action.Direction:new(3027, 741, 3219) },
    postconditions = { Condition.DistanceTo:new(3027, 741, 3219, 8) },
  },
  {
    actions = { Action.ModelHighlight:new(seaman), Action.ConversationHighlight:new("Yes please.") },
    postconditions = { Condition.DistanceTo:new(2958, 645, 3146, 8) },
  },
  {
    text = "Head inside the pub.",
    title = "The smuggling job",
    actions = { Action.Direction:new(2925, 677, 3146) },
    postconditions = { Condition.DistanceTo:new(2924, 677, 3145, 2) },
  },
  {
    text = "Buy one Karamjan rum from Zembo.",
    actions = { Action.ModelHighlight:new(zembo) },
    postconditions = { Condition.InventoryContains:new(rum) },
  },
  {
    text = "Pick 10 bananas from the Banana Trees to the north-west.",
    actions = {
      Action.Direction:new(2918.5, 1213, 3155.5),
      Action.Direction:new(2915.5, 1293, 3158.5),
      Action.Direction:new(2912.5, 1189, 3158.5),
    },
    postconditions = { Condition.InventoryContains:new(Items["banana"], 10) },
  },
  {
    text = "Go east to the house by the docks.",
    actions = { Action.Direction:new(2938, 389, 3154) },
    postconditions = { Condition.DistanceTo:new(2938, 389, 3154, 2) },
  },
  {
    text = "Talk to Luthas about the customs officer.",
    actions = {
      Action.ModelHighlight:new(luthas),
      Action.ConversationHighlight:new("That customs officer is annoying isn't she?"),
    },
    postconditions = { Condition.ConversationText:new("I believe it is run by a man called Wydin.") },
  },
  {
    text = "Talk to Luthas again and ask for a job.",
    actions = {
      Action.ModelHighlight:new(luthas),
      Action.ConversationHighlight:new("Could you offer me employment on your plantation?"),
    },
    postconditions = { Condition.ConversationText:new("If you could fill it up with bananas, I'll pay you 30 gold.") },
  },
  {
    text = "<i>Use</i> the Karamjan rum on the wooden crate.",
    actions = { Action.InventoryHighlight:new(rum), Action.ModelHighlight:new(karamjaCrate) },
    postconditions = {
      Condition.ConversationText:new("You stash the rum in the crate."),
      Condition.ChatText:new("There is some rum in here, although no bananas to cover it. It is a little obvious."),
      Condition.ChatText:new("The crate is filled with bananas. There is also some rum stashed in here too."), --If user accidentally fills with bananas first
    },
  },
  {
    text = "Right-click <i>Fill</i> the crate.",
    actions = { Action.ModelHighlight:new(karamjaCrate) },
    postconditions = {
      Condition.ConversationText:new("You pack all your bananas into the crate."),
      Condition.ChatText:new("The crate is filled with bananas. There is also some rum stashed in here too."),
    },
  },
  {
    text = "Talk to Luthas.",
    actions = { Action.ModelHighlight:new(luthas) },
    postconditions = {
      Condition.ConversationText:new("Well done, here's your payment"),
      Condition.ConversationText:new("Luthas hands you 30 coins"),
    },
  },
  {
    text = "Talk to the Customs officer to return to Port Sarim.",
    actions = { Action.Direction:new(2955, 645, 3147) },
    postconditions = { Condition.DistanceTo:new(2955, 645, 3147, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(customsOfficer),
      Action.ConversationHighlight:new("Can I journey on this ship?"),
      Action.ConversationHighlight:new("Search away, I have nothing to hide."),
      Action.ConversationHighlight:new("Ok."),
    },
    postconditions = { Condition.DistanceTo:new(3028, 741, 3219, 8) },
  },
  {
    text = "Grab a white apron out of the fishing store.",
    title = "Retrieving the rum",
    actions = { Action.Direction:new(3016, 965, 3229) },
    postconditions = {
      Condition.ModelVisible:new(hangingWhiteApron),
      Condition.InventoryContains:new(whiteApron),
    },
  },
  {
    actions = { Action.ModelHighlight:new(hangingWhiteApron) },
    postconditions = { Condition.InventoryContains:new(whiteApron) },
  },
  {
    text = "Equip your white apron and enter Wydin's Food Store south of the lodestone.",
    actions = {
      Action.InventoryHighlight:new(whiteApron),
      Action.Direction:new(3012.5, 965, 3204),
    },
    postconditions = { Condition.DistanceTo:new(3014, 965, 3206, 2) },
  },
  {
    text = "Attempt to open the door in the back and take the job.",
    actions = {
      Action.InventoryHighlight:new(whiteApron),
      Action.Direction:new(3012.5, 965, 3204),
    },
    postconditions = { Condition.ConversationText:new("Hey, you can't go in there.") },
  },
  {
    actions = {
      Action.ConversationHighlight:new("Well, can I get a job here?"),
      Action.ConversationHighlight:new("Can I get a job here?"),
    },
    postconditions = { Condition.ConversationText:new("Go through to the back and tidy up for me, please.") },
  },
  {
    text = "Search the crate with the banana on it.",
    actions = { Action.Direction:new(3009, 965, 3207) },
    postconditions = { Condition.InventoryContains:new(rum) },
  },
  {
    text = "With the rum, return to Redbeard Frank.",
    actions = { Action.Direction:new(3049, 965, 3253) },
    postconditions = { Condition.DistanceTo:new(3049, 965, 3253, 10) },
  },
  {
    actions = {
      Action.ModelHighlight:new(redbeardFrank),
      Action.ConversationHighlight:new("Ok thanks, I'll go and get it."),
    },
    postconditions = { Condition.ConversationText:new("Ok thanks, I'll go and get it.") },
  },
  {
    text = "Go upstairs in the Blue Moon Inn in south Varrock.",
    title = "Treasure hunting",
    neededItems = { ["Chest key"] = { quantity = 1, model = chestKey } },
    actions = { Action.Direction:new(3226, 1125, 3394) },
    postconditions = { Condition.DistanceTo:new(3226, 1125, 3394, 12) },
  },
  {
    actions = { Action.ModelHighlight:new(blueMoonInnStairs) },
    postconditions = { Condition.DistanceToWithHeight:new(3230, 2309, 3394, 3) },
  },
  {
    text = "Open the chest in the south-west room for a pirate message. Read it.",
    actions = { Action.Direction:new(3219, 2309, 3395) },
    postconditions = { Condition.InventoryContains:new(pirateMessage) },
  },
  {
    text = "Go to Falador Park and stand on the X east of the pond.",
    actions = { Action.Direction:new(2999, 997, 3383) },
    postconditions = { Condition.DistanceTo:new(1, 1, 1, 8) },
  },
  {
    text = "Right-click <i>Dig</i> using the pirate message. Kill the gardener. Dig on the X again again.<ul><li>If your character doesn't want to dig up the flowers, you need to read the pirate message still.</li></ul>",
    actions = { Action.Direction:new(2999, 997, 3383) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Pirate's Treasure",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.short,
  releaseDate = 992217600,
  prereqQuests = {},
  questReqs = {},
  neededItems = {
    ["Coins"] = { quantity = 60 },
    ["Bananas"] = { quantity = 10, model = Items["banana"], duringQuest = true },
    ["White apron"] = { quantity = 1, model = whiteApron, duringQuest = true },
  },
  recommendedItems = {},
  combatNPCs = { ["Gardener"] = { level = "4", optional = true, quantity = 1 } },
})
