local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Enums = require("core.enums")
local Types = require("core.types")
local Models = require("util.models")
local Model, Vertex = Types.Model, Types.Vertex

--NPCs
local pikkupstix = Models.npcs["pikkupstix"]
local petShopOwner = Model.new(4944, {
  [2539] = Vertex.new(-20, 614, -67, 39, 35, 24),
  [2582] = Vertex.new(20, 614, -67, 39, 35, 24),
  [2665] = Vertex.new(0, 739, -3, 34, 174, 158),
  [2666] = Vertex.new(0, 736, -3, 34, 174, 158),
  [2667] = Vertex.new(0, 736, -4, 34, 174, 158),
})

--Objects
local taverlyWell = Model.new(3105, {
  [102] = Vertex.new(3916, 163, 3019, 0, 0, 0),
  [1977] = Vertex.new(4490, 878, 3608, 53, 48, 40),
  [3103] = Vertex.new(4243, 1133, 3529, 72, 41, 29),
  [3104] = Vertex.new(4233, 1133, 3526, 72, 41, 29),
  [3105] = Vertex.new(4246, 1142, 3520, 72, 41, 29),
})
local pikkupstixStairs = Model.new(2172, {
  [1671] = Vertex.new(783, 1654, 3012, 46, 35, 23),
  [1689] = Vertex.new(886, 1796, 3020, 46, 35, 23),
  [1695] = Vertex.new(1005, 1898, 3027, 32, 25, 16),
  [1713] = Vertex.new(1005, 1898, 3027, 46, 35, 23),
  [1857] = Vertex.new(875, 1793, 3041, 43, 35, 27),
})
local stikklebrixCorpse = Model.new(4605, {
  [1] = Vertex.new(-213, 107, -117, 42, 32, 12),
})
local taverlyObelisk = Model.new(7527, {
  [293] = Vertex.new(658, 25, 739, 211, 190, 134),
  [1327] = Vertex.new(-80, 691, 64, 170, 198, 187),
  [5777] = Vertex.new(-732, 0, 691, 49, 49, 45),
  [5831] = Vertex.new(665, 0, 773, 49, 49, 45),
  [5837] = Vertex.new(665, 0, 773, 49, 49, 45),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Pikkupstix in his house just north of Taverley's east entrance.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Taverley lodestone",
      url = "Taverley_lodestone_icon.png",
    },
    neededItems = {},
    recommendedItems = {},
    actions = { Action.Direction:new(2929, 869, 3448) },
    postconditions = { Condition.DistanceTo:new(2929, 869, 3448, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(pikkupstix),
      Action.ConversationHighlight:new("Do you have a quest for me?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head to south of the Taverley lodestone.",
    title = "Assistants",
    actions = {
      Action.Direction:new(2880, 869, 3429),
    },
    postconditions = { Condition.ModelVisible:new(taverlyWell) },
  },
  {
    text = "Enter the well and watch the short cutscene.",
    actions = {
      Action.ModelHighlight:new(taverlyWell),
      Action.ConversationHighlight:new("Look, I'll just go and take a look myself..."),
    },
    postconditions = { Condition.ConversationText:new("Please hurry!") },
  },
  {
    text = "Talk to Pikkupstix back at the east side of Taverley.",
    actions = {
      Action.Direction:new(2929, 869, 3448),
    },
    postconditions = { Condition.DistanceTo:new(2929, 869, 3448, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(pikkupstix),
      Action.ConversationHighlight:new("Bowloftrix has been kidnapped by trolls!"),
      Action.ConversationHighlight:new("I think I had better go."),
    },
    postconditions = { Condition.ConversationText:new("Well, best of luck in your hunt.") },
  },
  {
    text = "Go upstairs in Pikkupstix's house and search the cluttered drawers in the north-west corner.",
    title = "Pouch materials",
    actions = {
      Action.ModelHighlight:new(pikkupstixStairs),
    },
    postconditions = { Condition.DistanceToWithHeight:new(2930, 2085, 3445, 3) },
  },
  {
    actions = { Action.Direction:new(2928, 2085, 3451) },
    postconditions = {
      Condition.ConversationText:new("You have found the embroidered pouch under some socks in this drawer."),
    },
  },
  {
    text = "Go to the ground floor of the pet shop south of Pikkupstix and ask the owner about white hare meat.",
    actions = {
      Action.Direction:new(2929, 2085, 3445),
    },
    postconditions = {
      Condition.DistanceToWithHeight:new(2927, 869, 3446, 3),
      Condition.DistanceToWithHeight:new(2934, 869, 3431, 3),
    },
  },
  {
    actions = { Action.Direction:new(2932, 869, 3434) },
    postconditions = { Condition.DistanceTo:new(2932, 869, 3434, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(petShopOwner),
      Action.ConversationHighlight:new("Ask about the white hare meat."),
    },
    postconditions = {
      Condition.ConversationText:new("I hope things work out for you!"),
    },
  },
  {
    text = "Take the path up the mountain next to the well.",
    actions = {
      Action.Direction:new(2858, 5541, 3479),
    },
    postconditions = { Condition.DistanceTo:new(2872, 2109, 3427, 8) },
  },
  {
    text = "Continue down the path.",
    actions = { Action.Direction:new(2866, 4869, 3462) },
    postconditions = { Condition.DistanceTo:new(2866, 4869, 3462, 4) },
  },
  {
    text = "Search Stikklebrix's body.",
    actions = { Action.ModelHighlight:new(stikklebrixCorpse) },
    postconditions = { Condition.ConversationText:new("You take the amulet from the sad remains of Stikklebrix.") },
  },
  {
    text = "Talk to Pikkupstix after the items are obtained.",
    actions = {
      Action.Direction:new(2929, 869, 3448),
    },
    postconditions = { Condition.DistanceTo:new(2929, 869, 3448, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(pikkupstix),
      Action.ConversationHighlight:new("I need to ask you something about the quest..."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "You never know what could go wrong with such a monumental summoning effort like this one!"
      ),
    },
  },
  {
    text = "Infuse the pouch using the obelisk on the eastern side of his house.",
    title = "Finishing up",
    neededItems = { ["Rare summoning items"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ModelHighlight:new(taverlyObelisk) },
    postconditions = { Condition.ConversationText:new("It thrums with barely contained power.") },
  },
  {
    text = "Talk to him again.",
    actions = {
      Action.ModelHighlight:new(pikkupstix),
      Action.ConversationHighlight:new("I need to ask you something about the quest..."),
      Action.ConversationHighlight:new("I made the giant wolpertinger pouch!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "But Scalectrix is strong too. Between you both you should be able to accomplish this."
      ),
    },
  },
  {
    text = "Go west back to the well and talk to Scalectrix (make sure you dismiss your pet if you have one out).<br><br>Watch the cutscene",
    actions = { Action.Direction:new(2880, 869, 3429) },
    postconditions = { Condition.ModelVisible:new(taverlyWell) },
  },
  {
    actions = { Action.ModelHighlight:new(taverlyWell) },
    postconditions = { Condition.ConversationText:new("Thank you both! I was a gonner until you showed up!") },
  },
  { postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "Wolf Whistle",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1327968000,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
