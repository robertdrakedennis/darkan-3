local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local crashedStar = Model.new(1578, {
  [1] = Vertex.new(421, 262, -35, 127, 127, 127),
  [1500] = Vertex.new(282, -20, -179, 127, 127, 127),
})

local rosalina = Model.new(9438, {
  [1] = Vertex.new(-119, 733, -7, 127, 127, 127),
})

local chambers = Model.new(9285, {
  [1] = Vertex.new(22, 800, 3, 5, 20, 28),
})

local chambers2 = Model.new(8955, {
  [1] = Vertex.new(22, 800, 3, 5, 20, 28),
})

local portalBase = Model.new(1896, {
  [1] = Vertex.new(-332, 38, 21, 127, 127, 127),
})

local wings = Model.new(10338, {
  [1] = Vertex.new(182, 350, 10, 127, 128, 128),
})

local decay = Model.new(759, {
  [1] = Vertex.new(-1229, -248, 530, 127, 127, 127),
})

local decay2 = Model.new(759, {
  [1] = Vertex.new(-1229, -128, 530, 127, 127, 127),
})

local decay3 = Model.new(759, {
  [1] = Vertex.new(-1229, -279, 530, 127, 127, 127),
})

local rug = Model.new(325, {
  [1] = Vertex.new(620, 0, 358, 127, 127, 127),
})

local observatoryRope = Model.new(222, {
  [1] = Vertex.new(187, 86, -89, 79, 65, 50),
})

local observatoryProf = Model.new(4863, {
  [1] = Vertex.new(24, 707, 29, 2, 3, 29),
})

local darkSphere = Model.new(2280, {
  [1] = Vertex.new(3201, 3133, 3548, 0, 0, 0),
})

local whisp = Model.new(921, {
  [1] = Vertex.new(186, 1046, -78, 0, 0, 0),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the crashed star west of Piscatoris.",
    title = "Mysterious star found",
    actions = { Action.Direction:new(2288, 69, 3690) },
    neededItems = {},
    recommendedItems = {},
    postconditions = { Condition.DistanceTo:new(2288, 69, 3690, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(crashedStar) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Mine the crashed star and finish the dialogue.",
    actions = { Action.ModelHighlight:new(crashedStar) },
    postconditions = { Condition.ConversationText:new("Oh, hello!") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("I shall be right behind you") },
  },
  {
    text = "Go to the south side of the 2nd floor[UK] (3rd floor[US]) of the Wizard's Tower and talk to Wizard Rosalina. Players can quickly teleport to her with stardust.",
    title = "Sending Zoob back",
    actions = { Action.Direction:new(3103, 1925, 3155) },
    postconditions = { Condition.DistanceToWithHeight:new(3103, 16773, 3155, 50) }, -- distance is quite high since y difference is massive and some people tp directly with stardust
    neededItems = {
      ["Cosmic Rune"] = { quantity = 152, model = Models.items["cosmic rune"] },
      ["Astral Rune"] = { quantity = 52, model = Models.items["astral rune"] },
      ["Wizard Blizzard"] = { quantity = 1 },
    },
  },
  {
    actions = {
      Action.ModelHighlight:new(rosalina),
      Action.ConversationHighlight:new("Talk about 'Stellar Born'."),
      Action.ConversationHighlight:new("They seem just the right amount of glowy."),
      Action.ConversationHighlight:new("We could jump really high"),
    },
    postconditions = {
      Condition.ConversationText:new("See you there"),
    },
  },
  {
    text = "Return to the crashed star west of Piscatoris.",
    actions = { Action.Direction:new(2282, 637, 3692) },
    postconditions = { Condition.DistanceTo:new(2282, 637, 3692, 15) },
  },
  {
    text = "Talk to Wizard Rosalina to hand her your cosmic and astral runes and the Wizard Blizzard drink. Finish the cutscene.",
    actions = {
      Action.ModelHighlight:new(rosalina),
    },
    postconditions = {
      Condition.ConversationText:new("Alrighty then!"),
    },
  },
  {
    text = "Head to the desert goebie camp, north-west of Menaphos.",
    actions = { Action.Direction:new(3101, 1221, 2861) },
    postconditions = { Condition.DistanceTo:new(3101, 1221, 2861, 10) },
  },
  {
    text = "Talk to Wizard Chambers.",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Stellar Born'."),
      Action.ModelHighlight:new(chambers),
    },
    postconditions = {
      Condition.ConversationText:new("I'll meet you"),
    },
  },
  {
    text = "Return to the Piscatoris crash site and talk to Wizard Chambers or Wizard Rosalina.",
    actions = { Action.Direction:new(2282, 637, 3692) },
    postconditions = { Condition.DistanceTo:new(2282, 637, 3692, 15) },
  },
  {
    actions = {
      Action.ModelHighlight:new(rosalina),
      Action.ModelHighlight:new(chambers2),
    },
    postconditions = { Condition.ConversationText:new("I bet Zoob is looking forward to getting home") },
  },
  {
    text = "Talk to them again.",
    actions = {
      Action.ModelHighlight:new(rosalina),
      Action.ModelHighlight:new(chambers2),
    },
    postconditions = { Condition.ConversationText:new("Let's begin!") },
  },
  {
    text = "Shoo away fireflies to keep them away from the crashed star until the portal progress at the top of your screen reaches 50.",
    actions = {},
    postconditions = { Condition.ConversationText:new("Come on...") },
  },
  {
    text = "Talk to them again.",
    actions = {},
    postconditions = { Condition.ConversationText:new("Here goes nothing, then.") },
  },
  {
    text = "Teleport to Zolarea with the portal.",
    title = "Exploring the homeland of star sprites",
    actions = { Action.ModelHighlight:new(portalBase) },
    postconditions = { Condition.ConversationText:new("Rosalina? Zoob? Anyone?") },
  },
  {
    text = "Unequip any items in your main/off-hand slots and jump the rocky edge to the east.",
    actions = { Action.Direction:new(4836, 8797, 5539) },
    postconditions = { Condition.ModelVisible:new(wings) },
  },
  {
    text = "Fly to the large island to the east and jump to the rocky edge to land. Players can Surge and Dive during flight.",
    actions = { Action.Direction:new(4862, 8493, 5492) },
    postconditions = { Condition.ModelNotVisible:new(wings) },
  },
  {
    text = "Head to the giant crystal towards the centre and talk to Wizard Rosalina.",
    actions = {
      Action.ConversationHighlight:new("Well, I think you're cute"),
      Action.ModelHighlight:new(rosalina),
    },
    postconditions = { Condition.ConversationText:new("I'll crack on with it.") },
  },
  {
    text = "Jump the rocky edge on the northernmost side of the main island.",
    actions = { Action.Direction:new(4929, 8077, 5508) },
    postconditions = { Condition.ModelVisible:new(wings) },
  },
  {
    text = "Head to the north-easternmost island and inspect the mysterious decay.",
    actions = { Action.Direction:new(4958, 12365, 5551) },
    postconditions = { Condition.DistanceTo:new(4958, 12365, 5551, 20) },
  },
  {
    actions = { Action.ModelHighlight:new(decay) },
    postconditions = { Condition.ConversationText:new("There must be more around") },
  },
  {
    text = "Return to the main island and then inspect the mysterious decay south of the rocky edge and west of the flower patch.",
    actions = { Action.Direction:new(4951, 10925, 5538) },
    postconditions = { Condition.ModelVisible:new(wings) },
  },
  {
    actions = { Action.Direction:new(4929, 8077, 5508) },
    postconditions = { Condition.ModelNotVisible:new(wings) },
  },
  {
    actions = { Action.Direction:new(4903, 6645, 5515) },
    postconditions = { Condition.DistanceTo:new(4903, 6645, 5515, 15) },
  },
  {
    actions = { Action.ModelHighlight:new(decay2) },
    postconditions = { Condition.ConversationText:new("There might be another") },
  },
  {
    text = "Attempt to enter the cave entrance to the west of the giant crystal. (1/12)",
    actions = { Action.Direction:new(4876.5, 4093, 5499) },
    postconditions = { Condition.ConversationText:new("Maybe Blip could tell me") },
  },
  {
    text = "Head to the south side of the main island and inspect the mysterious decay, then talk to Rox. (2/12)",
    actions = { Action.Direction:new(4905, 5669, 5469) },
    postconditions = { Condition.DistanceTo:new(4905, 5669, 5469, 15) },
  },
  {
    actions = { Action.ModelHighlight:new(decay3) },
    postconditions = { Condition.ConversationText:new("These parts of the planet look like") },
  },
  {
    text = "Head west to the southernmost house and lift the woven rug, then talk to Nuub. (3/12)",
    actions = { Action.Direction:new(4891, 4965, 5467) },
    postconditions = { Condition.DistanceTo:new(4891, 4965, 5467, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(rug) },
    postconditions = { Condition.ConversationText:new("Hmm, okay") }, -- We cannot really highlight nuub because he has too many vertices slowing down the game a lot (I went from 110 to 50 fps) and his model is not unique
  },
  {
    text = "Talk to Blip near Wizard Rosalina. (4/12)",
    actions = { Action.Direction:new(4896, 5973, 5492) },
    postconditions = { Condition.ConversationText:new("Isee. I'd best not sneak in there, then.") },
  },
  {
    text = "Talk to Wizard Rosalina.  If the necessary chat options display '???', you missed a clue. If done correctly, you receive a wrinkly scroll.",
    actions = {
      Action.ModelHighlight:new(rosalina),
      Action.ConversationHighlight:new("I found some information..."),
      Action.ConversationHighlight:new("Parts of the planet are dying."),
      Action.ConversationHighlight:new("There is a cave in the center of the planet."),
      Action.ConversationHighlight:new("The cave is where elder star sprites go to die."),
      Action.ConversationHighlight:new("The cave is where their power source can be found."),
    },
    postconditions = { Condition.ConversationText:new("Gronigen... Coordinates... Got it") },
  },
  {
    text = "Head to the observatory west of Tree Gnome Village and north of Castle Wars. The fastest teleport is 'Chipped' Watchtower teleport made by Dave's spellbook.",
    title = "Playing around dark star",
    actions = { Action.Direction:new(2448, 93, 3172) },
    postconditions = { Condition.DistanceTo:new(2448, 93, 3172, 15) },
  },
  {
    text = "Climb the rope outside the north side of the observatory, then go up the stairs.",
    actions = { Action.ModelHighlight:new(observatoryRope) },
    postconditions = { Condition.DistanceToWithHeight:new(2443, 3813, 3164, 0) },
  },
  {
    actions = { Action.Direction:new(2444, 3813, 3161) },
    postconditions = { Condition.DistanceToWithHeight:new(2442, 4813, 3159, 0) },
  },
  {
    text = "Talk to the Observatory professor on the 1st floor[UK]2nd floor[US].",
    actions = {
      Action.ModelHighlight:new(observatoryProf),
      Action.ConversationHighlight:new("Talk about 'Stellar Born'."),
    },
    postconditions = {
      Condition.ConversationText:new("I'd best let Rosalina know what you found. Thank you for the help!"),
    },
  },
  {
    text = "After the cutscene, return to Zolarea via the portal at the crash site west of Piscatoris.",
    actions = { Action.Direction:new(2282, 637, 3692) },
    postconditions = { Condition.DistanceTo:new(2282, 637, 3692, 15) },
  },
  {
    actions = { Action.ModelHighlight:new(portalBase) },
    postconditions = { Condition.DistanceTo:new(4897, 6613, 5525, 50) },
  },
  {
    text = "Talk to Wizard Rosalina in the centre of the island.",
    actions = { Action.Direction:new(4896, 5973, 5492) },
    postconditions = { Condition.DistanceTo:new(4896, 5973, 5492, 15) },
  },
  {
    actions = { Action.ModelHighlight:new(rosalina) },
    postconditions = { Condition.ConversationText:new("Meet us there") },
  },
  {
    text = "Enter the cave entrance on the west.",
    actions = { Action.Direction:new(4876.5, 4093, 5499) },
    postconditions = { Condition.ModelVisible:new(darkSphere) },
  },
  {
    text = "Talk to Wizard Rosalina.",
    actions = { Action.ModelHighlight:new(rosalina) },
    postconditions = { Condition.ConversationText:new("I'll come back here once I'm done.") },
  },
  {
    text = "Exit the cave.", -- This is instanced, we cannot use direction here and cave exit is too large to highlight
    postconditions = { Condition.DistanceTo:new(4877, 4093, 5500, 5) },
  },
  {
    text = "Gather the dark star wisps that have appeared. Wisps are visible on the minimap as a yellow dot:<ul><li>A bit north of the cave, west of the mysterious decay. (1/5)</li><li>Jump the rocky edge to the north and gather the wisp on the small, north-north-western island. (2/5)</li><li>Return to the east side of the main island and gather the wisp near the meteorites. (The mining symbol on your minimap) (3/5)</li><li>Go to the south side of the island and gather the wisp near the rocky edge. (4/5)</li><li>Jump the rocky edge to the south and gather the wisp on the south-easternmost island. (5/5)</li></ul>",
    actions = { Action.ModelHighlight:new(whisp) },
    postconditions = { Condition.ConversationText:new("That's all the baby dark stars gathered up") },
  },
  {
    text = "Return to the cave and talk to Wizard Rosalina.",
    actions = { Action.Direction:new(4876.5, 4093, 5499) },
    postconditions = { Condition.ModelVisible:new(darkSphere) },
  },
  {
    actions = {
      Action.ModelHighlight:new(rosalina),
      Action.ConversationHighlight:new("Could we sacrifice a star sprite?"),
      Action.ConversationHighlight:new("Could we sacrifice Blip?"),
      Action.ConversationHighlight:new("Could I sacrifice myself?"),
      Action.ConversationHighlight:new("Could we sacrifice you?"),
    },
    postconditions = { Condition.ConversationText:new("I will gather up the rest of the younglings") },
  },
  {
    text = "Select the star sprites in the following order:<ul><li>Wave 1: Glorp, Bop, Rox</li><li>Wave 2: Zag, Fuz, Nuub, Zorb</li><li>Wave 3: Zig, Eetee, Bop, Eetee, Glorp</li></ul> (QuestHelper cannot help here)",
    actions = {},
    postconditions = { Condition.ConversationText:new("Meet us back outside") },
  },
  {
    text = "Exit the cave and talk to Wizard Rosalina in the centre.",
    actions = {
      Action.ModelHighlight:new(rosalina),
      Action.ConversationHighlight:new("Yes please!"),
    },
    postconditions = { Condition.DistanceTo:new(2290, 61, 3688, 10) },
  },
  {
    text = "Talk to Wizard Rosalina back at the crash site.",
    actions = { Action.ModelHighlight:new(rosalina) },
  },
}

return Quest:new({
  name = "Stellar Born",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1759708800,
  prereqQuests = { "Observatory Quest", "Complete the Wing Out mystery" },
  questReqs = {},
  neededItems = {
    ["Cosmic Rune"] = { quantity = 152, model = Models.items["cosmic rune"] },
    ["Astral Rune"] = { quantity = 52, model = Models.items["astral rune"] },
    ["Wizard Blizzard"] = { quantity = 1 },
  },
  recommendedItems = {},
  combatNPCs = {},
})
