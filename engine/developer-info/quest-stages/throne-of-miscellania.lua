local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local derrik = Model.new(5715, {
  [3589] = Vertex.new(2, 725, -59, 108, 80, 56),
  [3593] = Vertex.new(7, 724, -51, 108, 80, 56),
  [3607] = Vertex.new(-2, 725, -59, 108, 80, 56),
  [3612] = Vertex.new(-7, 724, -51, 108, 80, 56),
  [5471] = Vertex.new(40, 711, -12, 0, 0, 0),
})
local flowerGirl = Model.new(3567, {
  [1851] = Vertex.new(24, 738, -38, 31, 30, 29),
  [1875] = Vertex.new(-24, 738, -38, 31, 30, 29),
  [1897] = Vertex.new(-2, 716, -57, 108, 80, 56),
  [1903] = Vertex.new(2, 716, -57, 108, 80, 56),
  [1907] = Vertex.new(6, 716, -52, 108, 80, 56),
})
--#endregion
--#region Objects
--#endregion
--#region Items
local goldRing = Model.new(531, {
  [129] = Vertex.new(33, 5, 34, 154, 132, 14),
  [131] = Vertex.new(33, 5, 34, 154, 132, 14),
  [136] = Vertex.new(33, 5, 34, 154, 132, 14),
  [150] = Vertex.new(16, 5, 42, 154, 132, 14),
  [156] = Vertex.new(16, 5, 42, 154, 132, 14),
})
local oakShortbow = Model.new(144, {
  [3] = Vertex.new(-128, 16, -120, 97, 89, 89),
  [11] = Vertex.new(-128, 16, -120, 97, 89, 89),
  [32] = Vertex.new(-104, 16, -152, 123, 97, 11),
  [35] = Vertex.new(-104, 16, -152, 123, 97, 11),
  [89] = Vertex.new(-104, 16, -152, 123, 97, 11),
})
local flowers = Model.new(600, {
  [102] = Vertex.new(36, 60, -80, 154, 153, 14),
  [105] = Vertex.new(-32, 60, -80, 154, 153, 14),
  [114] = Vertex.new(32, 40, -104, 154, 153, 14),
  [408] = Vertex.new(-12, 32, -124, 154, 153, 14),
  [417] = Vertex.new(-32, 44, -100, 154, 153, 14),
})
--#endregion
--#region Quest Items
local giantNib = Model.new(147, {
  [10] = Vertex.new(-44, 32, -36, 132, 125, 69),
  [11] = Vertex.new(-40, 32, -28, 132, 125, 69),
  [13] = Vertex.new(-44, 32, -36, 132, 125, 69),
  [19] = Vertex.new(-24, 32, -44, 132, 125, 69),
  [20] = Vertex.new(-32, 32, -48, 132, 125, 69),
})
local giantPen = Model.new(363, {
  [10] = Vertex.new(-228, 32, 236, 132, 125, 69),
  [11] = Vertex.new(-220, 32, 232, 132, 125, 69),
  [13] = Vertex.new(-228, 32, 236, 132, 125, 69),
  [19] = Vertex.new(-236, 32, 216, 132, 125, 69),
  [20] = Vertex.new(-240, 32, 224, 132, 125, 69),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to King Vargas, 1st floor (2nd floor[US]) in the castle on the western island of Miscellania.",
    title = "Starting out",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to King Vargas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("that's what he's here for") },
  },
  {
    text = "Talk to Queen Sigrid on the 1st floor (2nd floor[US]) of the castle on the eastern half of the island.",
    title = "Being the messenger",
    neededItems = {
      ["Iron bar"] = { quantity = 1 },
      ["Logs"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2614, 1201, 3867) },
    postconditions = { Condition.DistanceToWithHeight:new(2615, 1869, 3867, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["queen sigrid"]) },
    postconditions = { Condition.ConversationText:new("officially recognise Etceteria") },
  },
  {
    text = "Go back and talk to King Vargas.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("convince her there are better anthems") },
  },
  {
    text = "Go back and talk to Queen Sigrid.",
    actions = { Action.Direction:new(2614, 1201, 3867) },
    postconditions = { Condition.DistanceToWithHeight:new(2613, 1925, 3875, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["queen sigrid"]) },
    postconditions = { Condition.ConversationText:new("see what I can do") },
  },
  {
    text = "Talk to Prince Brand in the bedroom south of King Vargas's room.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["prince brand"]) },
    postconditions = { Condition.ConversationText:new("Don't mention it") },
  },
  {
    text = "Talk to Advisor Ghrim in King Vargas's room.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["advisor ghrim"]),
      Action.ConversationHighlight:new("How do I make peace with Etceteria?"),
    },
    postconditions = { Condition.ConversationText:new("Are there any other matters") },
  },
  {
    text = "Go back and talk to Queen Sigrid.",
    actions = { Action.Direction:new(2614, 1201, 3867) },
    postconditions = { Condition.DistanceToWithHeight:new(2615, 1869, 3867, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["queen sigrid"]) },
    postconditions = { Condition.ConversationText:new("King Vargas' signature") },
  },
  {
    text = "Talk to King Vargas.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("pen large enough for me") },
  },
  {
    text = "Talk to Derrik at the anvil on the north of the island.",
    actions = {
      Action.Direction:new(2550, 965, 3896, { distance = 4 }),
      Action.ModelHighlight:new(derrik, { distance = 4 }),
      Action.ConversationHighlight:new("I have a slightly strange request..."),
    },
    postconditions = { Condition.InventoryContains:new(giantNib) },
  },
  {
    text = "Use the giant nib on your logs.<ul><li>You can chop an evergreen for logs.</li></ul>",
    actions = {
      Action.InventoryHighlight:new(giantNib),
      Action.InventoryHighlight:new(Models.items["any logs"]),
    },
    postconditions = { Condition.InventoryContains:new(giantPen) },
  },
  {
    text = "Buy some flowers from the flower girl if you don't have any.",
    actions = { Action.ModelHighlight:new(flowerGirl) },
    postconditions = { Condition.InventoryContains:new(flowers) },
  },
  {
    text = "Talk to King Vargas.",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("keep at it then") },
  },
  {
    text = "Talk to Advisor Ghrim about marriage.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["advisor ghrim"]),
      Action.ConversationHighlight:new("How am I supposed to get the Prince or Princess to marry me?"),
    },
    postconditions = { Condition.ConversationText:new("would be a good way to start") },
  },
  {
    text = "Talk to Princess Astrid on the same floor to the north.",
    title = "To marry the princess",
    neededItems = {
      ["Flowers"] = { quantity = 1 },
      ["Bow"] = { quantity = 1 },
      ["Ring"] = { quantity = 1 },
    },
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("I will consider accepting to marry you") },
  },
  {
    text = "Talk to Princess Astrid 3 more times.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("He's been very helpful."),
      Action.ConversationHighlight:new("Hahahaha!"),
      Action.ConversationHighlight:new("Archery is a noble art!"),
    },
    postconditions = {
      Condition.ConversationText:new("he chose him to be his advisor"),
      Condition.ConversationText:new("so glad you agree"),
      Condition.ConversationText:new("have confidence in your skills"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("He's been very helpful."),
      Action.ConversationHighlight:new("Hahahaha!"),
      Action.ConversationHighlight:new("Archery is a noble art!"),
    },
    postconditions = {
      Condition.ConversationText:new("he chose him to be his advisor"),
      Condition.ConversationText:new("so glad you agree"),
      Condition.ConversationText:new("have confidence in your skills"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("He's been very helpful."),
      Action.ConversationHighlight:new("Hahahaha!"),
      Action.ConversationHighlight:new("Archery is a noble art!"),
    },
    postconditions = {
      Condition.ConversationText:new("he chose him to be his advisor"),
      Condition.ConversationText:new("so glad you agree"),
      Condition.ConversationText:new("have confidence in your skills"),
    },
  },
  {
    text = "Use the flowers on her.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.InventoryHighlight:new(flowers),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("They're lovely") },
  },
  { text = "Perform the Dance emote in her room.", postconditions = { Condition.ConversationText:new("Impressive") } },
  {
    text = "Talk to Princess Astrid to tell her your name.",
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationText:new("Very well, Princess") },
  },
  {
    text = "Talk to Princess Astrid again and agree with her again 3 times.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("What happened next?"),
      Action.ConversationHighlight:new("That sounds like a good idea."),
      Action.ConversationHighlight:new("I'm quite fond of it myself."),
    },
    postconditions = {
      Condition.ConversationText:new("realised that neither of us"),
      Condition.ConversationText:new("Thank you"),
      Condition.ConversationText:new("someone who shares my interests"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("What happened next?"),
      Action.ConversationHighlight:new("That sounds like a good idea."),
      Action.ConversationHighlight:new("I'm quite fond of it myself."),
    },
    postconditions = {
      Condition.ConversationText:new("realised that neither of us"),
      Condition.ConversationText:new("Thank you"),
      Condition.ConversationText:new("someone who shares my interests"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("What happened next?"),
      Action.ConversationHighlight:new("That sounds like a good idea."),
      Action.ConversationHighlight:new("I'm quite fond of it myself."),
    },
    postconditions = {
      Condition.ConversationText:new("realised that neither of us"),
      Condition.ConversationText:new("Thank you"),
      Condition.ConversationText:new("someone who shares my interests"),
    },
  },
  {
    text = "Give her a bow by using it on her.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.InventoryHighlight:new(oakShortbow, true),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ConversationText:new("It's lovely") },
  },
  {
    text = "Talk to Princess Astrid.",
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationText:new("onto the shortlist") },
  },
  {
    text = "Talk to Princess Astrid again and agree with her again 3 times.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("I suppose you don't have much opportunity to."),
      Action.ConversationHighlight:new("It's a lovely little country."),
      Action.ConversationHighlight:new("And what a great bard he makes!"),
    },
    postconditions = {
      Condition.ConversationText:new("take me with you sometime"),
      Condition.ConversationText:new("glad you like it"),
      Condition.ConversationText:new("quite the bard"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("I suppose you don't have much opportunity to."),
      Action.ConversationHighlight:new("It's a lovely little country."),
      Action.ConversationHighlight:new("And what a great bard he makes!"),
    },
    postconditions = {
      Condition.ConversationText:new("take me with you sometime"),
      Condition.ConversationText:new("glad you like it"),
      Condition.ConversationText:new("quite the bard"),
    },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["princess astrid"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["princess astrid"]),
      Action.ConversationHighlight:new("I suppose you don't have much opportunity to."),
      Action.ConversationHighlight:new("It's a lovely little country."),
      Action.ConversationHighlight:new("And what a great bard he makes!"),
    },
    postconditions = {
      Condition.ConversationText:new("take me with you sometime"),
      Condition.ConversationText:new("glad you like it"),
      Condition.ConversationText:new("quite the bard"),
    },
  },
  { text = "Blow a kiss at Princess Astrid.", postconditions = { Condition.ConversationText:new("you the charmer") } },
  {
    text = "Use the ring on her.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new("I have agreed to marry you") },
  },
  {
    text = "Talk to King Vargas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Go to the room to the south and talk to Prince Brand.",
    title = "To marry the prince",
    warning = "This path is not tracked currently. It will be implemented at a later date.",
    neededItems = {
      ["Flowers"] = { quantity = 1 },
      ["Cake"] = { quantity = 1 },
      ["Ring"] = { quantity = 1 },
    },
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Talk to Prince Brand about 3 more times, trying to impress and/or agree with him.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Use the clap or cheer emote.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Use the flowers on him.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Talk to Prince Brand and agree with him again 3 times.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Listen to his saga.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Use the cake on him.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Talk to Prince Brand and agree with him again 3 times.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "When he calls you 'Dear', blow a kiss at him.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Use the ring on Prince Brand.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Talk to King Vargas.",
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.ConversationText:new("You should try to gain their support") },
  },
  {
    text = "Talk to Advisor Ghrim about support.",
    title = "Helping the population",
    actions = {
      Action.ModelHighlight:new(Models.npcs["advisor ghrim"]),
      Action.ConversationHighlight:new("How do I gain the support of the population?"),
    },
    postconditions = { Condition.ConversationText:new("of the population supports you") },
  },
  {
    text = "You need to make 75% of the population like you. You can check your approval percentage by talking to the people you're helping. To do this, perform any of the following tasks on the island:<ul><li>Raking weeds</li><li>Chopping trees.</li><li>Mining coal.</li><li>Fishing at the docks to the south.</li><li>Purchase Flowers (mixed) from the Flower Girl (50% chance of +1% per flower)</li></ul>",
    postconditions = { Condition.ConversationText:new("seem remarkably popular for a newcomer") },
  },
  {
    text = "Talk to King Vargas.",
    title = "Finishing up",
    actions = { Action.Direction:new(2505, 2245, 3848.5) },
    postconditions = { Condition.DistanceToWithHeight:new(2504, 3237, 3849, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["king vargas"]) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Throne of Miscellania",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1101686400,
  prereqQuests = { "The Fremennik Trials" },
  questReqs = {},
  neededItems = {
    ["Iron bar"] = { quantity = 1, model = Models.items["iron bar"] },
    ["Any log"] = { quantity = 1, model = Models.items["any logs"], duringQuest = true },
    ["Flowers"] = { quantity = 1, model = flowers, duringQuest = true },
    ["Cake (if courting Prince Brand)"] = { quantity = 1, model = Models.items["cake"] },
    ["Oak shortbow (if courting Princess Astrid)"] = { quantity = 1, model = oakShortbow },
    ["Gold ring (any tradeable, metal ring works)"] = { quantity = 1, model = goldRing },
  },
  recommendedItems = {},
  combatNPCs = {},
})
