local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local roarkwin = Model.new(6279, {
  [175] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [186] = Vertex.new(2, 725, -59, 107, 79, 55),
  [2931] = Vertex.new(170, 515, -181, 104, 81, 53),
  [5934] = Vertex.new(-65, 670, 50, 76, 12, 6),
  [5965] = Vertex.new(65, 670, 50, 76, 12, 6),
})
local seasingerUmi = Model.new(6180, {
  [1869] = Vertex.new(58, 492, -34, 127, 127, 127),
  [1988] = Vertex.new(-58, 492, -34, 127, 127, 127),
  [3092] = Vertex.new(-23, 491, -63, 127, 128, 128),
  [3116] = Vertex.new(-27, 483, -65, 128, 128, 128),
  [3138] = Vertex.new(23, 491, -63, 127, 128, 128),
})

-- Objects
local barStaircase = Model.new(804, {
  [255] = Vertex.new(1161, 2975, 2988, 112, 88, 75),
  [275] = Vertex.new(1301, 2942, 2988, 80, 64, 56),
  [279] = Vertex.new(1412, 3042, 2988, 29, 25, 22),
  [285] = Vertex.new(1272, 3075, 2988, 112, 88, 75),
  [305] = Vertex.new(1412, 3042, 2988, 80, 64, 56),
})
local draynorStaircase = Model.new(8832, {
  [4436] = Vertex.new(5187, 2456, 978, 127, 127, 127),
  [5419] = Vertex.new(5530, 2050, 1000, 127, 127, 127),
  [6413] = Vertex.new(5187, 2456, 978, 127, 127, 127),
  [6874] = Vertex.new(5374, 2218, 992, 127, 127, 127),
  [8431] = Vertex.new(5187, 2456, 978, 127, 127, 127),
})

-- Items
-- Quest Items

---@type QuestStep[]
local steps = {
  --#region Starting out
  {
    text = "Talk to Trader Stan on the south-eastern dock of Port Sarim.",
    title = "Starting out",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Port Sarim lodestone",
      url = "Port_Sarim_lodestone_icon.png",
    },
    actions = { Action.Direction:new(3033, 741, 3190) },
    postconditions = { Condition.DistanceTo:new(3033, 741, 3190, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["trader stan"]),
      Action.ConversationHighlight:new("Talk about the Skulls pirate, Jed."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue talking to Trader Stan.",
    actions = {
      Action.ModelHighlight:new(NPCs["trader stan"]),
      Action.ConversationHighlight:new("About a quartermaster."),
    },
    postconditions = { Condition.ConversationText:new("I'd suggest you talk to Surula in your personal port bar") },
  },
  {
    text = "Ask about the different potential crewmates.",
    actions = {
      Action.ModelHighlight:new(NPCs["trader stan"]),
      Action.ConversationHighlight:new("About a navigator."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "It's rare to find one outside the Eastern Lands, but I happen to know one called Umi. "
      ),
      Condition.ConversationText:new("You should go to Umi"),
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["trader stan"]),
      Action.ConversationHighlight:new("About a bosun."),
    },
    postconditions = {
      Condition.ConversationText:new("Port Sarim lodestone"),
      Condition.ConversationText:new("old lighthouse jail"),
    },
  },
  --#endregion
  --#region Assembling a crew
  {
    text = "Talk to Guard Captain Roarkwin in the Port Sarim Jail located to the west.",
    title = "Guide",
    actions = {
      Action.Direction:new(3013, 1317, 3185),
      Action.ConversationHighlight:new("Never mind."), --To handle ending of Trader Stan dialogue
    },
    postconditions = { Condition.DistanceTo:new(3013, 1317, 3185, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(roarkwin),
    },
    postconditions = {
      Condition.ConversationText:new("Yeah, she's rented a room above the pottery there."),
    },
  },
  {
    text = "Talk to Seasinger Umi inside the player-owned port portal in Port Sarim.",
    actions = { Action.Direction:new(3033, 965, 3243) },
    postconditions = { Condition.ModelVisible:new(Objects["player owned ports portal"]) },
  },
  {
    actions = { Action.ModelHighlight:new(Objects["player owned ports portal"]) },
    postconditions = { Condition.ModelVisible:new(seasingerUmi) },
  },
  {
    actions = { Action.ModelHighlight:new(seasingerUmi) },
    postconditions = {
      Condition.ConversationText:new(
        "Besides, she's better than me at navigating anyway AND she's a full-blown seasinger. She's the clever one, so mum always said."
      ),
    },
  },
  {
    text = "Talk to Surula, the Barmaid in the player-owned port bar.",
    actions = {
      Action.ModelHighlight:new(NPCs["surula"]),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "If he gives you the run-around, just tell him I sent you and that'll set him straight." --male player character
      ),
      Condition.ConversationText:new("I'm sure he won't be able to say no... or much of anything, really."), --female player character
    },
  },
  {
    text = "Climb the stairs and talk to Mister Gully.",
    actions = { Action.ModelHighlight:new(barStaircase) },
    postconditions = { Condition.ModelVisible:new(NPCs["mister gully"]) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["mister gully"]) },
    postconditions = { Condition.ConversationText:new("I'll inform Trader Stan you've joined the crew!") },
  },
  {
    text = "Talk to the Black Knight sergeant upstairs in the pottery house southwest of the Draynor lodestone.",
    actions = { Action.Direction:new(3091, 1285, 3283) },
    postconditions = { Condition.DistanceTo:new(3091, 1285, 3283, 4) },
  },
  {
    actions = { Action.ModelHighlight:new(draynorStaircase) },
    postconditions = { Condition.DistanceToWithHeight:new(3094, 2469, 3281) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["black knight sergeant bosun"]) },
    postconditions = {
      Condition.ConversationText:new(
        "Thank you, sergeant...well, I guess bosun now. I'll let Trader Stan know you've joined the crew."
      ),
    },
  },
  {
    text = "Talk to Seasinger Jemi at the Rimmington well.",
    actions = { Action.Direction:new(2956, 805, 3208) },
    postconditions = { Condition.DistanceTo:new(2956, 805, 3208, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(NPCs["seasinger jemi"]) },
    postconditions = { Condition.ConversationText:new("Great, I'll go tell Trader Stan that I've got a navigator!") },
  },
  --#region Finishing up
  {
    text = "Return to Trader Stan.",
    title = "Finishing up",
    actions = { Action.Direction:new(3033, 741, 3190) },
    postconditions = { Condition.DistanceTo:new(3033, 741, 3190, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["trader stan"]),
      Action.ConversationHighlight:new("Talk about the Skulls pirate, Jed."),
    },
    postconditions = {
      Condition.ChatText:new("Congratulations! You have completed: 'The Arc' - Access to The Arc"),
      Condition.QuestComplete:new(),
    },
  },
  --#endregion
}

return Quest:new({
  name = "Impressing the Locals",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1468195200,
  prereqQuests = {},
  questReqs = {},
  neededItems = { ["Coins"] = { quantity = 1000 } },
  recommendedItems = {},
  combatNPCs = {},
})
