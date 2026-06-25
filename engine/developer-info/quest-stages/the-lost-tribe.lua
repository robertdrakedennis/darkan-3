local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--#region Objects
local lumCellarRubble = Model.new(576, {
  [3] = Vertex.new(-264, 696, -172, 51, 51, 47),
  [51] = Vertex.new(-268, 840, 24, 51, 51, 47),
  [54] = Vertex.new(-268, 840, 24, 51, 51, 47),
  [63] = Vertex.new(-268, 724, 116, 51, 51, 47),
  [65] = Vertex.new(-268, 700, 120, 51, 51, 47),
})
--#endregion
--#region Quest Items
local brooch = Model.new(48, {
  [1] = Vertex.new(-64, 0, -8, 83, 76, 76),
  [3] = Vertex.new(-56, 0, 16, 83, 76, 76),
  [6] = Vertex.new(88, 0, 24, 83, 76, 76),
  [8] = Vertex.new(88, 0, 24, 83, 76, 76),
  [9] = Vertex.new(72, 0, 8, 83, 76, 76),
})
local goblinBook = Model.new(396, {
  [7] = Vertex.new(-60, 12, -84, 69, 44, 6),
  [38] = Vertex.new(20, 12, 72, 69, 44, 6),
  [47] = Vertex.new(-60, 12, 84, 69, 44, 6),
  [215] = Vertex.new(64, 36, 84, 79, 55, 24),
  [219] = Vertex.new(64, 36, 84, 79, 55, 24),
})
local sigmundKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 154, 122, 14),
  [104] = Vertex.new(68, 16, 68, 154, 122, 14),
  [397] = Vertex.new(68, 16, 68, 154, 122, 14),
  [400] = Vertex.new(68, 16, 68, 154, 122, 14),
  [408] = Vertex.new(68, 16, 68, 154, 122, 14),
})
local silverware = Model.new(558, {
  [519] = Vertex.new(112, 132, 104, 136, 136, 148),
  [524] = Vertex.new(152, 132, 140, 136, 136, 148),
  [527] = Vertex.new(136, 132, 152, 136, 136, 148),
  [528] = Vertex.new(152, 132, 140, 136, 136, 148),
  [533] = Vertex.new(96, 132, 140, 136, 136, 148),
})
local peaceTreaty = Model.new(480, {
  [327] = Vertex.new(-148, 72, -20, 99, 82, 63),
  [375] = Vertex.new(-144, 76, -28, 118, 98, 75),
  [392] = Vertex.new(144, 76, -28, 118, 98, 75),
  [396] = Vertex.new(144, 76, -28, 118, 98, 75),
  [435] = Vertex.new(-132, 48, -64, 133, 110, 84),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Sigmund on the 1st floor (2nd floor [US]) of Lumbridge Castle.",
    title = "Getting started",
    actions = {
      Action.Direction:new(3205, 1477, 3209, { distance = 8 }),
      Action.ModelHighlight:new(Objects["lumbridge bottom stairs 0"], { distance = 8 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3205, 2693, 3209, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["sigmund advisor"]),
      Action.ConversationHighlight:new("Do you have any quests for me?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Sigmund.",
    actions = { Action.ModelHighlight:new(NPCs["sigmund advisor"]) },
    postconditions = { Condition.ConversationText:new("saw anything") },
  },
  {
    text = "Talk to Duke Horacio next to Sigmund.",
    title = "The goblin",
    actions = {
      Action.ModelHighlight:new(NPCs["duke horacio"]),
      Action.ConversationHighlight:new("What happened in the cellar?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "The wall collapsed because of an earthquake. I'm waiting for a team of builders to come and repair it."
      ),
    },
  },
  {
    text = "Talk to the Cook downstairs in the kitchen.",
    actions = {
      Action.Direction:new(3205, 2469, 3208),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3205, 1477, 3209, 50) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["lumbridge cook"]),
      Action.ConversationHighlight:new("Ask about what happened in the castle cellar"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Oh no, it's terrible, isn't it? There was rock dust everywhere, it got on all my ingredients!"
      ),
    },
  },
  {
    text = "Talk to Hans in the Lumbridge Castle courtyard.",
    actions = {
      Action.ModelHighlight:new(NPCs["hans"]),
      Action.ConversationHighlight:new("Do you know what happened in the cellar?"),
      Action.ConversationHighlight:new("Do you know what happened in the castle cellar?"),
      Action.ConversationHighlight:new("Talk about something else."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "The wall collapsed. I had to spend all day clearing it up! The Duke says it was an earthquake."
      ),
    },
  },
  {
    text = "Talk to Father Aereck in the church across from Lumbridge Castle.<ul><li>If you haven't talked to him before, he'll first tell you about your gravestone.</li></ul>",
    actions = {
      Action.Direction:new(3246, 965, 3205, { distance = 5 }),
      Action.ModelHighlight:new(NPCs["father aereck"], { distance = 5 }),
      Action.ConversationHighlight:new("Do you know what happened in the castle cellar?"),
    },
    postconditions = {
      Condition.ConversationText:new("It's a shame, isn't it?"),
    },
  },
  {
    text = "Talk to Bob in the axe shop across from the church.",
    actions = {
      Action.Direction:new(3228, 965, 3203, { distance = 4 }),
      Action.ModelHighlight:new(NPCs["bob smith"], { distance = 4 }),
      Action.ConversationHighlight:new("Do you know what happened in the castle cellar?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "If you can convince the Duke I'm telling the truth then we can get to the bottom of this mystery."
      ),
    },
  },
  {
    text = "Talk to Duke Horacio about your findings.",
    actions = {
      Action.Direction:new(3205, 1477, 3209, { distance = 8 }),
      Action.ModelHighlight:new(Objects["lumbridge bottom stairs 0"], { distance = 8 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3205, 2693, 3209, 3) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["duke horacio"]),
      Action.ConversationHighlight:new("Bob says he saw something in the cellar"),
    },
    postconditions = {
      Condition.ConversationText:new("Hmm, very well. I give you permission to investigate this mystery."),
    },
  },
  {
    text = "Descend the trapdoor in the kitchen.",
    title = "Dorgeshuun",
    neededItems = { ["Light source"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3205, 2469, 3208),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(3205, 1477, 3209, 10),
    },
  },
  {
    actions = { Action.Direction:new(3209, 1477, 3216) },
    postconditions = { Condition.DistanceTo:new(3208, 1349, 9616, 5) },
  },
  {
    text = "Mine the wall rubble to the east.", --step not working properly
    actions = { Action.ModelHighlight:new(lumCellarRubble) },
    postconditions = {
      -- Condition.ModelNotVisible:new(lumCellarRubble),
      Condition.ConversationText:new("You dig a narrow tunnel through the rocks."),
    },
  },
  {
    text = "Squeeze through the hole and take the brooch.",
    actions = { Action.Direction:new(3220, 1349, 9618) },
    postconditions = {
      Condition.DistanceTo:new(3223, 1029, 9618, 2),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(brooch),
    },
    postconditions = {
      Condition.InventoryContains:new(brooch),
    },
  },
  {
    text = "Show the brooch to Duke Horacio.",
    actions = { Action.Direction:new(3221, 1029, 9618) },
    postconditions = {
      Condition.DistanceTo:new(3217, 1349, 9617, 2),
    },
  },
  {
    actions = { Action.Direction:new(3209, 1449, 9616) },
    postconditions = { Condition.ModelVisible:new(NPCs["lumbridge cook"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Objects["lumbridge bottom stairs 0"]),
      Action.ModelHighlight:new(NPCs["duke horacio"]),
      Action.ConversationHighlight:new("I dug through the rubble..."),
    },
    postconditions = {
      Condition.ConversationText:new("The librarian in Varrock might be able to help identify the symbol."),
    },
  },
  {
    text = "Talk to Reldo in Varrock Palace Library about the brooch.",
    actions = {
      Action.Direction:new(3210, 1253, 3494, { distance = 8 }),
      Action.ModelHighlight:new(NPCs["reldo"], { distance = 8 }),
      Action.ConversationHighlight:new("Ask about the brooch."),
    },
    postconditions = {
      Condition.ConversationText:new("It's somewhere at the west end of the library, I think."),
    },
  },
  {
    text = "Search the northern most bookcase on the western wall for a goblin symbol book.",
    actions = {
      Action.Direction:new(3207, 1253, 3496),
    },
    postconditions = { Condition.InventoryContains:new(goblinBook) },
  },
  {
    text = "Read the book to the end, Reldo will force-greet you and mention the Goblin Village.",
    actions = {
      Action.ModelHighlight:new(NPCs["reldo"]),
      Action.InventoryHighlight:new(goblinBook),
    },
    postconditions = {
      Condition.ConversationText:new(
        "There's a goblin village north of the Falador lodestone in Asgarnia, ruled by General Bentnoze and General Wartface."
      ),
      Condition.ConversationText:new("The symbol of the 'Dorgeshuun' tribe"),
    },
  },
  {
    text = "Talk to Generals Bentnoze and Wartface in the Goblin Village, north of the Falador lodestone.",
    actions = {
      Action.Direction:new(2958, 645, 3513, { distance = 3 }),
      Action.ModelHighlight:new(NPCs["bentnoze"], { distance = 3 }),
      Action.ModelHighlight:new(NPCs["wartface"], { distance = 3 }),
      Action.ConversationHighlight:new("No"),
      Action.ConversationHighlight:new("Have you ever heard of the Dorgeshuun?"),
      Action.ConversationHighlight:new("It doesn't really matter"),
      Action.ConversationHighlight:new("Well either way they refused to fight"),
      Action.ConversationHighlight:new("Well I found a brooch underground..."),
      Action.ConversationHighlight:new("Well why not show me both greetings?"),
      Action.ConversationHighlight:new("Thanks"),
    },
    postconditions = {
      Condition.ConversationText:new("Bye"),
    },
  },
  {
    text = "Climb down the trapdoor in the Lumbridge Castle kitchen and squeeze through the hole in the wall.",
    title = "Finding the lost tribe",
    neededItems = {
      ["light source"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3209, 1478, 3215),
    },
    postconditions = {
      Condition.DistanceTo:new(3208, 1349, 9616, 2),
    },
  },
  {
    actions = {
      Action.Direction:new(3220, 1349, 9618),
    },
    postconditions = {
      Condition.DistanceTo:new(3223, 1029, 9618, 2),
    },
  },
  {
    text = "Run through the Goblin Mine",
    actions = { Action.Direction:new(3220, 1349, 9618) },
    postconditions = { Condition.DistanceTo:new(3220, 1349, 9618, 2) },
  }, -- Could be improved by giving every step the following step's postconditions in case someone gets stranded in the mine
  {
    actions = { Action.Direction:new(3232, 1069, 9610) },
    postconditions = { Condition.DistanceTo:new(3232, 1069, 9610, 2) },
  },
  {
    actions = { Action.Direction:new(3250, 1037, 9616) },
    postconditions = { Condition.DistanceTo:new(3250, 1037, 9616, 2) },
  },
  {
    actions = { Action.Direction:new(3250, 1117, 9631) },
    postconditions = { Condition.DistanceTo:new(3250, 1117, 9631, 2) },
  },
  {
    actions = { Action.Direction:new(3231, 1061, 9632) },
    postconditions = { Condition.DistanceTo:new(3231, 1061, 9632, 2) },
  },
  {
    actions = { Action.Direction:new(3245, 1221, 9648) },
    postconditions = { Condition.DistanceTo:new(3245, 1221, 9648, 2) },
  },
  {
    actions = { Action.Direction:new(3241, 1021, 9639) },
    postconditions = { Condition.DistanceTo:new(3241, 1021, 9639, 2) },
  },
  {
    actions = { Action.Direction:new(3251, 1077, 9640) },
    postconditions = { Condition.DistanceTo:new(3251, 1077, 9640, 2) },
  },
  {
    actions = { Action.Direction:new(3253, 1101, 9647) },
    postconditions = { Condition.DistanceTo:new(3253, 1101, 9647, 2) },
  },
  {
    actions = { Action.Direction:new(3269, 981, 9644) },
    postconditions = { Condition.DistanceTo:new(3269, 981, 9644, 2) },
  },
  {
    actions = { Action.Direction:new(3271, 1069, 9637) },
    postconditions = { Condition.DistanceTo:new(3271, 1069, 9637, 2) },
  },
  {
    actions = { Action.Direction:new(3287, 1093, 9644) },
    postconditions = { Condition.DistanceTo:new(3287, 1093, 9644, 2) },
  },
  {
    actions = { Action.Direction:new(3304, 1101, 9632) },
    postconditions = { Condition.DistanceTo:new(3304, 1101, 9632, 2) },
  },
  {
    actions = { Action.Direction:new(3292, 1133, 9615) },
    postconditions = { Condition.DistanceTo:new(3292, 1133, 9615, 2) },
  },
  {
    actions = { Action.Direction:new(3306, 1029, 9609) },
    postconditions = { Condition.DistanceTo:new(3306, 1029, 9609, 2) },
  },
  {
    text = "Use the Goblin Bow emote near Mistag.",
    actions = { Action.ModelHighlight:new(NPCs["mistag"]) },
    postconditions = {
      Condition.ConversationText:new("We meant no harm!"),
    },
  },
  {
    text = "Right-click <i>follow</i> on Mistag.",
    actions = { Action.ModelHighlight:new(NPCs["mistag"]) },
    postconditions = { Condition.DistanceTo:new(3232, 1069, 9610, 4) },
  },
  {
    text = "Talk to the Duke.",
    title = "Sigmund's treachery",
    actions = { Action.Direction:new(3221, 1029, 9618) },
    postconditions = {
      Condition.DistanceTo:new(3217, 1349, 9617, 2),
    },
  },
  {
    actions = { Action.Direction:new(3209, 1449, 9616) },
    postconditions = { Condition.ModelVisible:new(NPCs["lumbridge cook"]) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Objects["lumbridge bottom stairs 0"]),
      Action.ModelHighlight:new(NPCs["duke horacio"]),
      Action.ConversationHighlight:new("I've made contact with the cave goblins..."),
    },
    postconditions = {
      Condition.ConversationText:new("Unless it is returned, I am afraid I will have no option but war."),
    },
  },
  {
    text = "<i>Pickpocket</i> Sigmund for a key.",
    actions = { Action.ModelHighlight:new(NPCs["sigmund advisor"]) },
    postconditions = {
      Condition.InventoryContains:new(sigmundKey),
    },
  },
  {
    text = "Open the chest in the other room for a H.A.M. hood, shirt, and robe.<ul><li>Make sure to have 3 backpack spaces, otherwise the outfit might disappear with no means of receiving it again.</li></ul>",
    actions = { Action.Direction:new(3209, 2693, 3217) },
    postconditions = {
      Condition.InventoryContains:new(Items["ham hood"]),
      Condition.ConversationText:new("Sigmund must be a member of the Humans Against Monster cult!"),
    },
  },
  {
    text = "Go to the entrance of the H.A.M. Hideout, north-west of Lumbridge Castle.",
    actions = { Action.Direction:new(3165, 2533, 3251) },
    postconditions = { Condition.DistanceTo:new(3165, 2533, 3251, 8) },
  },
  {
    text = "Right-click <i>lockpick</i> the trapdoor, then climb down the trapdoor.<ul><li>It may take a few tries to successfully lockpick the trapdoor.</li></ul>",
    actions = { Action.Direction:new(3165, 2533, 3251) },
    postconditions = { Condition.DistanceTo:new(3149, 2973, 9652, 4) },
  },
  {
    text = "Search the crate near the ladder for the missing silverware.",
    actions = { Action.Direction:new(3152, 2661, 9645) },
    postconditions = { Condition.InventoryContains:new(silverware) },
  },
  {
    text = "Lodestone back to Lumbridge and talk to the Duke.",
    title = "Making peace",
    actions = {
      Action.Direction:new(3205, 1477, 3209, { distance = 8 }),
      Action.ModelHighlight:new(Objects["lumbridge bottom stairs 0"], { distance = 8 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(3205, 2693, 3209, 3) },
  },
  {
    actions = {
      Action.ConversationHighlight:new("I found the missing silverware in the HAM cave!"),
      Action.ModelHighlight:new(NPCs["duke horacio"]),
    },
    postconditions = {
      Condition.InventoryContains:new(peaceTreaty),
      Condition.ConversationText:new("I would like to meet with their leader to sign it."),
    },
  },
  {
    text = "Go back down the trapdoor in the castle kitchen.",
    actions = { Action.Direction:new(3205, 2469, 3208) },
    postconditions = { Condition.DistanceToWithHeight:new(3205, 1477, 3209, 6) },
  },
  {
    actions = { Action.Direction:new(3209, 1477, 3216) },
    postconditions = { Condition.DistanceToWithHeight:new(3208, 1349, 9616, 6) },
  },
  {
    text = "Squeeze through the hole in the wall and follow Kazgar, who'll be waiting just on the other side.",
    actions = { Action.Direction:new(3220, 1349, 9618) },
    postconditions = { Condition.DistanceTo:new(3224, 1061, 9617, 2) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["kazgar"]) },
    postconditions = { Condition.DistanceTo:new(3313, 645, 9613, 8) },
  },
  {
    text = "Talk to Mistag.",
    actions = { Action.ModelHighlight:new(NPCs["mistag"]) },
    postconditions = { Condition.ConversationText:new("I will summon Ur-tag, our headman, at once.") },
  },
  {
    text = "Wait for the cutscene.",
    actions = {},
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Lost Tribe",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1117519200,
  prereqQuests = {},
  questReqs = {
    Types.QuestReq.skill("Agility", 13),
    Types.QuestReq.skill("Mining", 17),
    Types.QuestReq.skill("Thieving", 13),
  },
  neededItems = { ["Any light source"] = { quantity = 1, model = Items["light source"] } },
  recommendedItems = { ["Lockpick"] = { quantity = 1 } },
  combatNPCs = {},
})
