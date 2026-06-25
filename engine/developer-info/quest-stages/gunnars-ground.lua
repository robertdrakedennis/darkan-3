local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

--#region NPCs
local dororan = Model.new(3732, {
  [1229] = Vertex.new(-125, 483, 119, 50, 66, 27),
  [1415] = Vertex.new(-125, 483, 119, 58, 77, 31),
  [1491] = Vertex.new(113, 510, 102, 146, 52, 29),
  [1493] = Vertex.new(92, 541, 63, 146, 52, 29),
  [3399] = Vertex.new(125, 483, 119, 23, 30, 12),
})
local jeffery = Model.new(3759, {
  [1924] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [1929] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [1932] = Vertex.new(2, 725, -59, 107, 79, 55),
  [1934] = Vertex.new(7, 724, -51, 107, 79, 55),
  [3364] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local gudrun = Model.new(4140, {
  [1062] = Vertex.new(-27, 732, 11, 158, 116, 81),
  [1235] = Vertex.new(27, 732, 11, 158, 116, 81),
})
local gunthor = Model.new(4494, {
  [2321] = Vertex.new(-20, 759, -77, 147, 135, 135),
  [2591] = Vertex.new(46, 808, 35, 62, 43, 32),
  [2595] = Vertex.new(46, 808, 35, 62, 43, 32),
  [2646] = Vertex.new(-46, 808, 35, 62, 43, 32),
  [2774] = Vertex.new(46, 808, 35, 23, 16, 12),
})
--#endregion
--#region Quest Items
local lovePoem = Model.new(714, {
  [1] = Vertex.new(112, 36, 32, 116, 97, 74),
  [2] = Vertex.new(14, 49, 12, 116, 97, 74),
  [3] = Vertex.new(112, 56, 20, 116, 97, 74),
  [5] = Vertex.new(14, 21, 16, 116, 97, 74),
  [9] = Vertex.new(112, 16, 16, 116, 97, 74),
})
local goldRing = Model.new(264, {
  [1] = Vertex.new(0, 23, 48, 153, 131, 13),
  [2] = Vertex.new(16, 16, 32, 153, 131, 13),
  [3] = Vertex.new(32, 23, 16, 153, 131, 13),
  [6] = Vertex.new(0, 10, 48, 153, 131, 13),
  [9] = Vertex.new(-32, 23, 48, 153, 131, 13),
})
local engravedGoldRing = Model.new(264, {
  [1] = Vertex.new(0, 23, 48, 62, 53, 5),
  [2] = Vertex.new(16, 16, 32, 62, 53, 5),
  [3] = Vertex.new(32, 23, 16, 62, 53, 5),
  [6] = Vertex.new(0, 10, 48, 62, 53, 5),
  [9] = Vertex.new(-32, 23, 48, 62, 53, 5),
})
local gunnarsGround = Model.new(624, {
  [1] = Vertex.new(112, 36, 32, 116, 97, 74),
  [2] = Vertex.new(14, 49, 12, 116, 97, 74),
  [3] = Vertex.new(112, 56, 20, 116, 97, 74),
  [5] = Vertex.new(14, 21, 16, 116, 97, 74),
  [9] = Vertex.new(112, 16, 16, 116, 97, 74),
})
--#endregion

---@type QuestStep[]
local steps = {
  --#region Getting the ring
  {
    text = "Speak to Dororan near the eastern entrance to Barbarian Village.",
    title = "Getting the ring",
    actions = { Action.Direction:new(3098, 861, 3422) },
    postconditions = { Condition.DistanceTo:new(3098, 861, 3422, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("'...ever learn to fly?'"),
      Action.ConversationHighlight:new("'...eat redberry pie?'"),
      Action.ConversationHighlight:new("'...get the evil eye?'"),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Angina?"),
      Action.ConversationHighlight:new("Hypertension?"),
      Action.ConversationHighlight:new("Coclearabsidosis?"),
      Action.ConversationHighlight:new("Get to the point."),
      Action.ConversationHighlight:new("There must be something you can do."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue the conversation with Dororan.",
    actions = {
      Action.ModelHighlight:new(dororan),
    },
    postconditions = {
      Condition.ConversationText:new("Dororan gives you a poem."),
      Condition.InventoryContains:new(lovePoem),
    },
  },
  {
    text = "Go to Edgeville and speak to Jeffery near the furnace.",
    actions = { Action.Direction:new(3108, 1733, 3499) },
    postconditions = { Condition.DistanceTo:new(3108, 1733, 3499, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(jeffery),
      Action.ConversationHighlight:new("I'm here about a gold ring."),
      Action.ConversationHighlight:new("I was hoping you would trade me a gold ring."),
      Action.ConversationHighlight:new("This splendid love poem."),
      Action.ConversationHighlight:new("Yes, he did."),
      Action.ConversationHighlight:new("Just a plain, gold ring."),
    },
    postconditions = { Condition.ConversationText:new("Now, leave me in peace!") },
  },
  {
    text = "Take the gold ring back to Dororan.",
    actions = { Action.Direction:new(3098, 861, 3422) },
    postconditions = { Condition.DistanceTo:new(3098, 861, 3422, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("What do you want me to engrave?"),
      Action.ConversationHighlight:new("That sounds simple enough."),
    },
    postconditions = { Condition.ConversationText:new("Just use a chisel on the gold ring.") },
  },
  {
    text = "Click the <i>engrave</i> option on the ring.",
    actions = {
      Action.InventoryHighlight:new(goldRing),
    },
    postconditions = {
      Condition.ConversationText:new("You engrave 'Gudrun the Fair, Gudrun the Fiery' onto the ring."),
      Condition.InventoryContains:new(engravedGoldRing),
    },
  },
  {
    text = "Show the ring to Dororan.",
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("It's come out perfectly."),
      Action.ConversationHighlight:new("Of course."),
      Action.ConversationHighlight:new("Very well."),
    },
    postconditions = {
      Condition.ConversationText:new("Please don't tell her I'm a dwarf just yet."),
    },
  },
  --#endregion
  --#region Promoting the courtship
  {
    text = "Talk to Gudrun, west of Dororan.",
    title = "Promoting the courtship",
    actions = { Action.Direction:new(3081, 2309, 3415) },
    postconditions = { Condition.DistanceTo:new(3081, 2309, 3415, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gudrun),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("The ring isn't from me!"),
      Action.ConversationHighlight:new("A great poet."),
      Action.ConversationHighlight:new("So, you want me to talk to your father?"),
    },
    postconditions = {
      Condition.ConversationText:new("I know some of the others feel the same, but they're loyal to papa."),
    },
  },
  {
    text = "Talk to Gunthor, in the longhall north of Gudru.",
    actions = { Action.Direction:new(3078, 2341, 3444) },
    postconditions = { Condition.DistanceTo:new(3078, 2341, 3444, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gunthor),
      Action.ConversationHighlight:new("I need to speak with you, chieftain."),
      Action.ConversationHighlight:new("Your daughter seeks permission to court an outerlander."),
      Action.ConversationHighlight:new("You're barbarians"),
      Action.ConversationHighlight:new("Please wait a moment."),
      Action.ConversationHighlight:new("I'm going!"),
    },
    postconditions = { Condition.ConversationText:new("Now go, before I have Haakon dismember you.") },
  },
  {
    text = "Talk to Gudrun.",
    actions = { Action.Direction:new(3081, 2309, 3415) },
    postconditions = { Condition.DistanceTo:new(3081, 2309, 3415, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gudrun),
      Action.ConversationHighlight:new("What should we do now?"),
    },
    postconditions = { Condition.ConversationText:new("I'll ask him.") },
  },
  --#endregion
  --#region The poem
  {
    text = "Speak to Dororan.",
    title = "The poem",
    actions = { Action.Direction:new(3098, 861, 3422) },
    postconditions = { Condition.DistanceTo:new(3098, 861, 3422, 12) },
  },
  {
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("No, she liked the ring."),
      Action.ConversationHighlight:new("What are we going to do?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "To win the heart of my beloved from her father's iron grasp? It is worth it just to try!"
      ),
    },
  },
  {
    text = "Help him with his poem.",
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("I'm sure that's not true."),
    },
    postconditions = { Condition.ConversationText:new("By the colossus of King Alvis! I can't find the words!") },
  },
  {
    text = "Give any answer.",
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("Cucumber."),
    },
    postconditions = { Condition.ConversationText:new("That doesn't really fit") },
  },
  {
    text = "Stray.",
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("More words"),
      Action.ConversationHighlight:new("Stray."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "That fits! It fits perfectly. Right meaning, right length, right rhyme. Well done!"
      ),
    },
  },
  {
    text = "Give any answer.",
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("Stockade."),
    },
    postconditions = { Condition.ConversationText:new("That doesn't really fit") },
  },
  {
    text = "Threat.",

    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("More words"),
      Action.ConversationHighlight:new("Threat."),
    },
    postconditions = { Condition.ConversationText:new("Perfect! Yes!") },
  },
  {
    text = "Give any answer.",
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("Threw the ball."),
    },
    postconditions = { Condition.ConversationText:new("That doesn't really fit") },
  },
  {
    text = "Swept to war.",
    actions = {
      Action.ModelHighlight:new(dororan),
      Action.ConversationHighlight:new("More words"),
      Action.ConversationHighlight:new("Swept to war."),
    },
    postconditions = {
      Condition.ConversationText:new("Dororan gives you the poem."),
      Condition.InventoryContains:new(gunnarsGround),
    },
  },
  --#endregion
  --#region Sending the poem
  {
    text = "Give the poem to Gudrun to read to her father.",
    title = "Sending the poem",
    actions = { Action.Direction:new(3081, 2309, 3415) },
    postconditions = { Condition.DistanceTo:new(3081, 2309, 3415, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(gudrun),
    },
    postconditions = { Condition.ConversationText:new("How long have they been in there?") },
  },
  {
    text = "Watch the cutscenes.",
    actions = {
      Action.ConversationHighlight:new("They're just starting."),
      Action.ConversationHighlight:new("Why's that?"),
      Action.ConversationHighlight:new("Okay, I will."),
      Action.ConversationHighlight:new("Why would he do that?"),
      Action.ConversationHighlight:new("Now's your chance to find out."),
    },
    postconditions = { Condition.ConversationText:new("Yes, we'll see you there!") },
  },
  {
    text = "Talk to Gudrun again to complete the quest.",
    actions = {
      Action.ModelHighlight:new(gudrun),
      Action.ConversationHighlight:new("I'll see you soon."),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
  --#endregion
}

return Quest:new({
  name = "Gunnar's Ground",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1285027200,
  prereqQuests = {},
  questReqs = { Types.QuestReq.skill("Crafting", 5) },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
