local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex
local Location = require("util.location")

local portal = Models.objects["winter portal"]
local trevor = Models.npcs["trevor"]
local posty = Model.new(25728, {
  [1] = Vertex.new(-9, 270, 33, 127, 127, 127),
})
local postyHighlight = Action.ModelHighlight:new(posty)

local bucketCarrots = Model.new(873, {
  [1] = Vertex.new(16, 186, -115, 66, 63, 60),
})

local violet = Models.npcs["violet"]

local firstclue = Model.new(1401, {
  [1] = Vertex.new(-57, 66, -175, 53, 57, 58),
})

local secondclue = Model.new(1452, {
  [1] = Vertex.new(246, 125, -475, 127, 127, 127),
})

local thirdclue = Model.new(4986, {
  [1] = Vertex.new(525, -425, -791, 59, 51, 45),
})

local fourthclue = Model.new(6366, {
  [1] = Vertex.new(130, 503, -35, 48, 51, 52),
})

local snowimp = Model.new(26532, {
  [1] = Vertex.new(-9, 270, 33, 191, 191, 191),
})
local bradHighlight = Action.ModelHighlight:new(snowimp, { atLocation = Location:new(5, 380, -6), instanced = true })
local susiHighlight = Action.ModelHighlight:new(snowimp, { atLocation = Location:new(5, 410, -14), instanced = true })

local snowimpHat = Model.new(27408, {
  [688] = Vertex.new(-30, 325, -45, 191, 191, 191),
})

local wheelbarrow = Model.new(3726, {
  [1] = Vertex.new(2552, 1767, 3301, 127, 127, 127),
})

local taylor = Model.new(59778, {
  [1295] = Vertex.new(49, 898, -143, 127, 127, 127),
})

local wobblyTree = Model.new(3744, {
  [446] = Vertex.new(47, 1703, 622, 127, 127, 127),
})

local door = Model.new(336, {
  [249] = Vertex.new(-365, 0, -5, 167, 158, 153),
})

local lamppost = Model.new(432, {
  [370] = Vertex.new(39, 2004, 0, 127, 127, 127),
})

local timothy = Model.new(9636, { -- no clue what this is called lol
  [873] = Vertex.new(-15, 252, -72, 177, 177, 177),
})

local children = Model.new(9636, {
  [873] = Vertex.new(-15, 252, -72, 127, 127, 127),
})

local nicelist = Model.new(177, {
  [115] = Vertex.new(-61, 27, -68, 127, 127, 127),
})

local snowImpling = Model.new(1614, {
  [72] = Vertex.new(0, 872, 44, 41, 23, 3),
})

local decoratedTree = Model.new(20547, {
  [2725] = Vertex.new(-363, 2365, -322, 127, 127, 127),
})

local cc = Action.ContinueConversation:new()

---@type QuestStep[]
local steps = {
  {
    title = "Starting Out",
    text = "Talk to Postie Pete at the Land of Snow portal.",
    actions = { Action.Direction:new(2858, 4869, 3456) },
    postconditions = { Condition.DistanceTo:new(2858, 4869, 3456, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["postie pete"]) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Enter the portal.",
    actions = { Action.ModelHighlight:new(portal) },
    postconditions = { Condition.ModelVisible:new(posty) },
  },
  {
    text = "Talk to Posty and watch the cutscene.",
    actions = { postyHighlight, Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("Now, if I remember correctly") },
  },
  {
    text = "Go north up the hill to Violet's house.",
    actions = { Action.Direction:new(0, 200, 10, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(bucketCarrots) },
  },
  {
    text = "Knock on the door.",
    actions = { Action.ModelHighlight:new(door) },
    postconditions = { Condition.ConversationText:new("You knock") },
  },
  {
    text = "Progress through the dialogue.",
    actions = {},
    postconditions = { Condition.ConversationText:new("I want to show you") },
  },
  {
    text = "Talk to Violet in her room, north-west of Betty and Trevor.",
    actions = { Action.ModelHighlight:new(violet) },
    postconditions = { Condition.ConversationText:new("The first clue") },
  },
  {
    text = "Search the chest in the eastern side of the room with Betty and Trevor.",
    actions = { Action.ModelHighlight:new(firstclue) },
    postconditions = { Condition.ConversationText:new("Nothing in here") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("The next clue") },
  },
  {
    text = "Search the rug in Violet's room.",
    actions = { Action.ModelHighlight:new(secondclue) },
    postconditions = { Condition.ConversationText:new("Nothing underneath") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Your next clue") },
  },
  {
    text = "Search the fireplace in the western side of the room with Betty and Trevor.",
    actions = { Action.ModelHighlight:new(thirdclue) },
    postconditions = { Condition.ConversationText:new("No kidding!") },
  },
  {
    actions = {},
    postconditions = { Condition.ConversationText:new("Okay, last clue") },
  },
  {
    text = "Search the barrel of fish under the stairs.",
    actions = { Action.ModelHighlight:new(fourthclue) },
    postconditions = { Condition.ConversationText:new("*gasp*") },
  },
  {
    text = "Progress through the dialogue with Violet.",
    actions = { Action.ConversationHighlight:new("What should we do?") },
    postconditions = { Condition.ConversationText:new("You'd better not keep her waiting") },
  },
  {
    text = "Exit the house and talk to Violet.",
    actions = { Action.ModelHighlight:new(violet) },
    postconditions = { Condition.ConversationText:new("Okay, I'm shutting them.") },
  },
  {
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("Snow imps!") },
  },
  {
    text = "Talk to one of the snow imps.",
    actions = { Action.ModelHighlight:new(snowimp, { highlightPriority = "closest" }) },
    postconditions = { Condition.ConversationText:new("Let's go find him!") },
  },
  {
    text = "Go south to the abandoned farm.",
    actions = { Action.Direction:new(0, 0, -10, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(wheelbarrow) },
  },
  {
    text = "Open the chest by the door.",
    actions = { Action.Direction:new(-2.5, 300, -28.2, { instance = true }), Action.ResetInstance:new() },
    postconditions = { Condition.ModelVisible:new(snowimpHat) },
  },
  {
    text = "Proceed through dialogue with the snow imp follower (whose name is randomly selected).",
    actions = {
      Action.ConversationHighlight:new("At least you got out before Christmas."),
      Action.ModelHighlight:new(snowimpHat),
    },
    postconditions = { Condition.ConversationText:new("Oh noooo!") },
  },
  {
    title = "Assistant Brad",
    text = "Go through the cutscene. ",
    actions = {},
    postconditions = { Condition.ConversationText:new("Okay, Violet. Let's pick") },
  },
  {
    text = "Talk to Assistant Brad (north-east of the burning Christmas tree).",
    actions = { Action.ConversationHighlight:new("How can we help out?"), bradHighlight },
    postconditions = { Condition.ConversationText:new("We'll bring you back an awesome") },
  },
  {
    text = "Go north over the bridge, then  west to the Christmas tree farm.",
    actions = { Action.Direction:new(-34, -1348, 28, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(taylor) },
  },
  {
    text = "Talk to Taylor north west of the town.",
    actions = { Action.ModelHighlight:new(taylor) },
    postconditions = { Condition.ConversationText:new("Thank you, thank you") },
  },
  {
    text = "Inspect the wonky tree on the western side of the farm.",
    actions = { Action.ModelHighlight:new(wobblyTree) },
    postconditions = { Condition.ConversationText:new("Good thinking") },
  },
  {
    text = "Return to the town centre and talk to Assistant Brad",
    actions = { bradHighlight },
    postconditions = { Condition.ConversationText:new("Well, thank you both") },
  },
  {
    title = "Assistant Susi",
    text = "Talk to Assistant Susi south-east of wonky tree.",
    actions = { susiHighlight },
    postconditions = { Condition.ConversationText:new("Thank you both!") },
  },
  {
    text = "Knock on the door on the southern side of the house east of the town centre.",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(18, 1128, -12), instanced = true }),
      Action.Direction:new(18, 1128, -12, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Thank you, Korg") },
  },
  {
    text = "Knock on the door north-east of the town centre",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(5, 465, 7), instanced = true }),
      Action.Direction:new(5, 465, 7, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Thank you so much") },
  },
  {
    text = "Knock on the door on the north-west of the town centre.",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(-5, 48, 2), instanced = true }),
      Action.Direction:new(-5, 48, 2, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Thank you.") },
  },
  {
    text = "Knock on the door of 'The Mug-Inn' south-west of the town centre",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(-10, 1000, -18), instanced = true }),
      Action.Direction:new(-10, 1000, -18, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Yaay! Merry Christmas") },
  },
  {
    text = "Talk to Assistant Susi.",
    actions = { susiHighlight },
    postconditions = { Condition.ConversationText:new("YES!") },
  },
  {
    text = "Decorate 5 lampposts.",
    actions = { Action.ModelHighlight:new(lamppost, { highlightPriority = "all" }) },
    postconditions = { Condition.ConversationText:new("I'm so happy!") },
  },
  {
    text = "Knock on the door north-east of the town centre",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(5, 465, 7), instanced = true }),
      Action.Direction:new(5, 465, 7, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("That's better") },
  },
  {
    text = "Knock on the door on the north-west of the town centre.",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(-5, 48, 2), instanced = true }),
      Action.Direction:new(-5, 48, 2, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("I like this one!") },
  },
  {
    text = "Knock on the door of 'The Mug-Inn' south-west of the town centre",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(-10, 1000, -18), instanced = true }),
      Action.Direction:new(-10, 1000, -18, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("PERFECT") },
  },
  {
    text = "Knock on the door on the southern side of the house east of the town centre.",
    actions = {
      cc,
      Action.ModelHighlight:new(door, { distance = 10, atLocation = Location:new(18, 1128, -12), instanced = true }),
      Action.Direction:new(18, 1128, -12, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Looks like") },
  },
  {
    text = "Talk to Assistant Susi.",
    actions = { susiHighlight },
    postconditions = { Condition.ConversationText:new("We're on it!") },
  },
  {
    title = "Assistant Timothy",
    text = "Talk to Assistant Timothy north-west of the wonky tree.",
    actions = { Action.ModelHighlight:new(timothy) },
    postconditions = { Condition.ModelVisible:new(posty) },
  },
  {
    text = "Talk to Posty, who appears north of the wonky tree.",
    actions = { postyHighlight },
    postconditions = { Condition.ConversationText:new("Don't worry, we'll find it") },
  },
  {
    text = "Talk to Elizabeth west of the wonky tree.",
    actions = {
      cc,
      Action.ModelHighlight:new(children, { distance = 10, atLocation = Location:new(-14, 96, -6), instanced = true }),
      Action.Direction:new(-14, 96, -6, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Please don't tell on me") },
  },
  {
    text = "Talk to Peter east of the tree.",
    actions = {
      cc,
      Action.ModelHighlight:new(children, { distance = 10, atLocation = Location:new(12, 952, -5), instanced = true }),
      Action.Direction:new(12, 952, -5, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Let's go find Mozzie") },
  },
  {
    text = "Talk to Mozzie in the south-eastern part of the town.",
    actions = {
      cc,
      Action.ModelHighlight:new(
        children,
        { distance = 10, atLocation = Location:new(26, -504, -28), instanced = true }
      ),
      Action.Direction:new(26, -504, -28, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ConversationText:new("Finishing touches?") },
  },
  {
    text = "Talk to Neal in the house on the hill in the southern part of the town.",
    actions = {
      cc,
      Action.ModelHighlight:new(snowimp, { distance = 10, atLocation = Location:new(3, 3400, -36), instanced = true }),
      Action.Direction:new(3, 3400, -36, { distance = 10, instance = true }),
    },
    postconditions = { Condition.ModelVisible:new(nicelist) },
  },
  {
    text = "Take Santa's 'naughty or nice' list outside the eastern window of the house.",
    actions = { Action.ModelHighlight:new(nicelist) },
    postconditions = { Condition.ConversationText:new("Let's get it back") },
  },
  {
    text = "Talk to Posty.",
    actions = { postyHighlight },
    postconditions = { Condition.ConversationText:new("Consider this list checked twice!") },
  },
  {
    title = "Finishing up",
    text = "Talk to the named snow imp (last name Claus) north of the wonky tree.",
    actions = { Action.ModelHighlight:new(snowimpHat) },
    postconditions = { Condition.ConversationText:new("But we'll be sure to make you") },
  },
  {
    text = "Talk to Hal the snow impling just south-west of the wonky tree.",
    actions = { Action.ModelHighlight:new(snowImpling) },
    postconditions = { Condition.ConversationText:new("It seems no matter") },
  },
  {
    text = "Go up the southern hill without going inside the house and launch the snow impling from your backpack three times.",
    actions = {
      Action.Direction:new(1, 2328, -25, { instance = true }),
      Action.InventoryHighlight:new(snowImpling),
    },
    postconditions = { Condition.ConversationText:new("Finished!") },
  },
  {
    text = "Admire the wonky tree.",
    actions = { Action.ModelHighlight:new(decoratedTree) },
    postconditions = { Condition.ConversationText:new("It looks") },
  },
  {
    text = "Talk to  Violet.",
    actions = { Action.ModelHighlight:new(violet) },
    postconditions = { Condition.ConversationText:new("Say hello to Santa") },
  },
  {
    text = "Knock on the door twice.",
    actions = {},
    postconditions = { Condition.ConversationText:new("I can only see your feet") },
  },
  {
    text = "Talk to Betty or Trevor to complete the quest.",
    actions = { Action.ModelHighlight:new(trevor) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Violet is Blue Too",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1607904000,
  prereqQuests = { "Violet is Blue" },
})
