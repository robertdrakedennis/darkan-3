local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local barbarianGuard = Model.new(6192, {
  [1986] = Vertex.new(20, 737, -44, 115, 19, 10),
  [1988] = Vertex.new(-20, 737, -44, 115, 19, 10),
  [1990] = Vertex.new(23, 743, -39, 115, 19, 10),
  [1993] = Vertex.new(9, 744, -45, 115, 19, 10),
  [1996] = Vertex.new(-23, 743, -39, 115, 19, 10),
})
local blurberry = Model.new(3267, {
  [2890] = Vertex.new(0, 440, -10, 15, 168, 42),
  [2891] = Vertex.new(2, 440, -12, 15, 168, 42),
  [2892] = Vertex.new(-2, 440, -12, 15, 168, 42),
  [2961] = Vertex.new(-34, 442, -30, 79, 61, 24),
  [3032] = Vertex.new(34, 442, -30, 79, 61, 24),
})
local forestersBartender = Model.new(4203, {
  [2248] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2252] = Vertex.new(7, 724, -51, 109, 80, 57),
  [2266] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2271] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [3743] = Vertex.new(40, 711, -12, 49, 37, 15),
})
local flyingBartender = Model.new(3579, {
  [1789] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [1794] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [1797] = Vertex.new(2, 725, -59, 109, 80, 57),
  [1799] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3422] = Vertex.new(-30, 721, -31, 49, 37, 15),
})
local dragonBartender = Model.new(3453, {
  [1933] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [1938] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [1941] = Vertex.new(2, 725, -59, 109, 80, 57),
  [1943] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3013] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local deadBartender = Model.new(3993, {
  [2014] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2019] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [2022] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2024] = Vertex.new(7, 724, -51, 109, 80, 57),
  [2743] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local rustyBartender = Model.new(3957, {
  [2281] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2286] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [2289] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2291] = Vertex.new(7, 724, -51, 109, 80, 57),
  [3500] = Vertex.new(-30, 721, -31, 49, 37, 15),
})
local seaman = Model.any({
  Model.new(3831, {
    [2176] = Vertex.new(-2, 725, -59, 109, 80, 57),
    [2181] = Vertex.new(-7, 724, -51, 109, 80, 57),
    [2184] = Vertex.new(2, 725, -59, 109, 80, 57),
    [2186] = Vertex.new(7, 724, -51, 109, 80, 57),
    [2833] = Vertex.new(0, 735, -7, 29, 141, 129),
  }),
  Model.new(3651, {
    [1846] = Vertex.new(-2, 725, -59, 109, 80, 57),
    [1851] = Vertex.new(-7, 724, -51, 109, 80, 57),
    [1854] = Vertex.new(2, 725, -59, 109, 80, 57),
    [1856] = Vertex.new(7, 724, -51, 109, 80, 57),
    [2653] = Vertex.new(0, 735, -7, 29, 141, 128),
  }),
})
local zembo = Model.new(4995, {
  [3628] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [3633] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [3636] = Vertex.new(2, 725, -59, 109, 80, 57),
  [3638] = Vertex.new(7, 724, -51, 109, 80, 57),
  [4327] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local blueBartender = Model.new(3954, {
  [2662] = Vertex.new(-16, 632, 8, 32, 29, 29),
  [2687] = Vertex.new(16, 632, 8, 32, 29, 29),
  [2689] = Vertex.new(-8, 508, -4, 159, 146, 147),
  [2767] = Vertex.new(-28, 680, 60, 159, 146, 147),
  [2777] = Vertex.new(28, 680, 60, 159, 146, 147),
})
local jollyBartender = Model.new(3465, {
  [2542] = Vertex.new(-2, 725, -59, 109, 80, 57),
  [2547] = Vertex.new(-7, 724, -51, 109, 80, 57),
  [2550] = Vertex.new(2, 725, -59, 109, 80, 57),
  [2552] = Vertex.new(7, 724, -51, 109, 80, 57),
  [2809] = Vertex.new(0, 735, -7, 29, 141, 129),
})
local emily = Model.new(4875, {
  [507] = Vertex.new(-24, 738, -38, 32, 30, 29),
  [525] = Vertex.new(24, 738, -38, 32, 30, 29),
  [571] = Vertex.new(-2, 716, -57, 109, 80, 57),
  [577] = Vertex.new(2, 716, -57, 109, 80, 57),
  [581] = Vertex.new(6, 716, -52, 109, 80, 57),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to barbarian guard located east of Barbarian assault.",
    warning = "For the tracking to work properly, you need to have the chat visible, and game messages set to 'On', and chat timestamps on.",
    title = "Getting started",
    actions = {
      Action.Direction:new(2545.5, 965, 3570, { distance = 12 }),
      Action.ModelHighlight:new(barbarianGuard, { distance = 12, highlightPriority = "closest" }),
      Action.ConversationHighlight:new("I want to come through this gate."),
      Action.ConversationHighlight:new("Looks can be deceiving, I am in fact a barbarian."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with the guard.",
    warning = "If you lose your barcrawl card, you have to start over from the beginning.",
    actions = { Action.ModelHighlight:new(barbarianGuard) },
    postconditions = { Condition.ConversationText:new("When you've done all that, we'll be happy to let you in.") },
  },
  {
    text = "Go to the 1st floor (2nd floor[US]) of the Grand Tree.<ul><li>Spirit tree to the Tree Gnome Stronghold (completed Tree Gnome Village and The Grand Tree).</li><li>Lodestone to Eagles' Peak, run east (61 Agility).</li><li>Lodestone to Al Kharid, Gnome Glider to the Tree Gnome Stronghold (completed The Grand Tree).</li></ul>",
    title = "Visiting the bars",
    neededItems = { ["Coins"] = { quantity = 208 }, ["Barcrawl card"] = { quantity = 1 } },
    actions = { Action.Direction:new(2466, 3905, 3495) },
    postconditions = { Condition.DistanceToWithHeight:new(2466, 4165, 3494, 3) },
  },
  {
    text = "Talk to Blurberry to the east.",
    actions = {
      Action.Direction:new(2482, 4205, 3488, { distance = 10 }),
      Action.ModelHighlight:new(blurberry, { distance = 10 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = { Condition.ChatText:new("Blurberry signs your card.") },
  },
  {
    text = "Talk to the Bartender at the Forester's Arms, north of the <b>Seers' Village lodestone.</b>",
    actions = {
      Action.Direction:new(2693, 1157, 3493, { distance = 8 }),
      Action.ModelHighlight:new(forestersBartender, { distance = 8 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = { Condition.ChatText:new("The bartender scrawls his signature on your card.") },
  },
  {
    text = "Talk to the Bartender at the Flying Horse Inn, south-east of the <b>Ardougne lodestone.</b>",
    actions = {
      Action.Direction:new(2574, 1445, 3322, { distance = 5 }),
      Action.ModelHighlight:new(flyingBartender, { distance = 5 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = { Condition.ChatText:new("Through your tears you see the bartender...") },
  },
  {
    text = "Talk to the Bartender at the Dragon Inn, east of the <b>Yanille lodestone.</b>",
    actions = {
      Action.Direction:new(2553, 997, 3080, { distance = 6 }),
      Action.ModelHighlight:new(dragonBartender, { distance = 6 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = {
      Condition.ChatText:new("You can just about make out the bartender signing your barcrawl card."),
    },
  },
  {
    text = "Talk to the Bartender at the Dead Man's Chest, north-east of the <b>Karamja lodestone.</b>",
    actions = {
      Action.Direction:new(2795, 965, 3155, { distance = 12 }),
      Action.ModelHighlight:new(deadBartender, { distance = 12 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = { Condition.ChatText:new("You think you see 2 bartenders signing 2 barcrawl cards.") },
  },
  {
    text = "Talk to the Bartender at the Rusty Anchor, north-east of the <b>Port Sarim lodestone.</b>",
    actions = {
      Action.Direction:new(3045, 965, 3257, { distance = 8 }),
      Action.ModelHighlight:new(rustyBartender, { distance = 8 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = { Condition.ChatText:new("The bartender signs your card.") },
  },
  {
    text = "Take the ship to Karamja.",
    actions = {
      Action.Direction:new(3027, 741, 3219, { distance = 14 }),
      Action.ModelHighlight:new(seaman, { distance = 14, highlightPriority = "all" }),
      Action.ConversationHighlight:new("Yes please."),
    },
    postconditions = { Condition.DistanceTo:new(2956, 645, 3146, 8) },
  },
  {
    text = "Talk to Zembo at the Karamja Spirits Bar, west of the docks.",
    actions = {
      Action.Direction:new(2926, 677, 3144, { distance = 6 }),
      Action.ModelHighlight:new(zembo, { distance = 6 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = { Condition.ChatText:new("Zembo signs your card.") },
  },
  {
    text = "Talk to the Bartender at the Blue Moon Inn, north of the <b>Varrock lodestone.</b>",
    actions = {
      Action.Direction:new(3226, 1125, 3399, { distance = 10 }),
      Action.ModelHighlight:new(blueBartender, { distance = 10 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's Barcrawl."),
    },
    postconditions = { Condition.ChatText:new("Your insides feel terrible.") },
  },
  {
    text = "Talk to the Bartender at the Jolly Boar Inn, south-west of the <b>Fort Forinthry lodestone.</b>",
    actions = {
      Action.Direction:new(3280, 965, 3488, { distance = 14 }),
      Action.ModelHighlight:new(jollyBartender, { distance = 14 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhands Barcrawl."), --no apostrophe is correct
    },
    postconditions = { Condition.ChatText:new("Your head is spinning.") },
  },
  {
    text = "Talk to Emily in the Rising Sun Inn, south-west of the <b>Falador lodestone.</b>",
    actions = {
      Action.Direction:new(2954, 1189, 3371, { distance = 6 }),
      Action.ModelHighlight:new(emily, { distance = 6 }),
      Action.ConversationHighlight:new("I'm doing Alfred Grimhand's barcrawl."), --lowercase b is correct
    },
    postconditions = { Condition.ChatText:new("The barmaid signs your card.") },
  },
  {
    text = "Return to the Barbarian guard at the Barbarian Outpost.",
    title = "Finishing up",
    actions = {
      Action.Direction:new(2545.5, 965, 3570, { distance = 12 }),
      Action.ModelHighlight:new(barbarianGuard, { distance = 12, highlightPriority = "closest" }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Bar Crawl (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1017014400,
  prereqQuests = {},
  neededItems = { ["Coins"] = { quantity = 208 } },
  recommendedItems = {
    ["Games necklace"] = { quantity = 1 },
    ["Explorer's ring"] = { quantity = 1 },
  },
})
