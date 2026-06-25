local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local ladder = Model.new(576, {
  [1] = Vertex.new(4475, 1713, 3705, 127, 127, 127),
})

local caelyn = Models.npcs["caelyn"]
local caelynHl = Action.ModelHighlight:new(caelyn)

local thessalia = Models.npcs["thessalia"]
local curator = Models.npcs["curator haig"]
local oldman = Models.npcs["wise old man"]
local mordaut = Models.npcs["mr mordaut"]

local scarf = Model.new(540, {
  [1] = Vertex.new(5, 25, 9, 127, 127, 127),
})

local staircaseDown = Model.new(4566, {
  [1] = Vertex.new(1408, 1504, 3200, 108, 108, 99),
})

local staircaseUp = Model.new(4416, {
  [1] = Vertex.new(2907, 2296, 4315, 177, 177, 177),
})

local drunkenDwarf = Model.new(9240, {
  [123] = Vertex.new(-38, 528, -102, 97, 92, 89),
})

local bartender = Model.new(4875, {
  [2049] = Vertex.new(-24, 738, -38, 30, 29, 27),
})

local attendee = Model.new(13383, {
  [10105] = Vertex.new(0, 735, -7, 167, 167, 167),
  [10106] = Vertex.new(0, 732, -7, 167, 167, 167),
})

local maskedWoman = Model.new(16899, {
  [4975] = Vertex.new(0, 735, -7, 127, 128, 127),
})

local displayCase = Model.new(84, {
  [5] = Vertex.new(231, 655, -181, 127, 127, 127),
})

local displayCase2 = Model.new(4548, {
  [1945] = Vertex.new(-234, 674, 196, 127, 127, 127),
})

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Caelyn upstairs in the house directly north of Edgeville bank.",
    title = "Preparations of the heist",
    actions = { Action.Direction:new(3096, 1733, 3510) },
    postconditions = { Condition.DistanceTo:new(3096, 1733, 3510, 4), Condition.ModelVisible:new(caelyn) },
  },
  {
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = { Condition.ModelVisible:new(caelyn) },
  },
  {
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      caelynHl,
      Action.ConversationHighlight:new("I'm listening..."),
      Action.ConversationHighlight:new("What do you need?"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {},
  },
  {
    text = "Continue the conversation with Caelyn.",
    actions = {
      Action.ConversationHighlight:new("Let's get the disguises."),
      Action.ConversationHighlight:new("I'm on it."),
    },
    postconditions = {
      Condition.ConversationText:new("Here's some gold"),
    },
  },
  {
    text = "Go to Thessalia's Fine Clothes in Varrock and talk to Thessalia  to receive masquerade masks.",
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = {
      Condition.DistanceToWithHeight:new(3096, 1733, 3510, 5),
      Condition.DistanceTo:new(3204, 1125, 3416, 10),
    },
  },
  {
    actions = { Action.Direction:new(3204, 1125, 3416) },
    postconditions = { Condition.DistanceTo:new(3204, 1125, 3416, 10) },
  },
  {
    actions = { Action.Direction:new(3204, 1125, 3416) },
    postconditions = { Condition.DistanceTo:new(3204, 1125, 3416, 4), Condition.ModelVisible:new(thessalia) },
  },
  {
    actions = {
      Action.ModelHighlight:new(thessalia),
      Action.ConversationHighlight:new("Talk about Heartstealer."),
    },
    postconditions = { Condition.ConversationText:new("I will. Thank you") },
  },
  {
    text = "Return to Caelyn.",
    actions = { Action.Direction:new(3096, 1733, 3510) },
    postconditions = { Condition.DistanceTo:new(3096, 1733, 3510, 4), Condition.ModelVisible:new(caelyn) },
  },
  {
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = { Condition.ModelVisible:new(caelyn) },
  },
  {
    actions = {
      Action.ModelHighlight:new(caelyn),
      Action.ConversationHighlight:new("Let's create some Smoke bombs."),
    },
    postconditions = { Condition.ConversationText:new("Just follow my") },
  },
  {
    text = "Click the interactable objects in the room. After failing, lower the difficulty setting by speaking with her.",
    actions = {
      Action.ConversationHighlight:new("I'm ready."),
    },
    postconditions = { Condition.ConversationText:new("Are you ready to have another go") },
  },
  {
    text = "Follow Caelyn's instructions / the arrows to click on the areas of the room to make the smoke bombs.",
    actions = {
      Action.ConversationHighlight:new("That was tougher than I expected."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("I'm ready."),
    },
    postconditions = {
      Condition.ConversationText:new("I finished off those Smoke bombs"),
    },
  },
  {
    text = "Finish the dialogue with Caelyn to receive paper and crayons.",
    actions = {
      Action.ConversationHighlight:new("I'll get started on that."),
    },
    postconditions = { Condition.ConversationText:new("Good luck.") },
  },
  {
    text = "Head to the Varrock Museum and pickpocket Curator Haig Halen.",
    title = "The heist",
    actions = { Action.Direction:new(3256, 1637, 3446) },
    postconditions = { Condition.DistanceTo:new(3256, 1637, 3446, 10) },
  },
  {
    actions = { Action.Direction:new(3256, 1637, 3446) },
    postconditions = { Condition.DistanceTo:new(3256, 1637, 3446, 4), Condition.ModelVisible:new(curator) },
  },
  {
    actions = {
      Action.ModelHighlight:new(curator),
      Action.ConversationHighlight:new("Lie"),
    },
    postconditions = { Condition.ConversationText:new("Whew.") },
  },
  {
    text = "Return to Caelyn.",
    actions = { Action.Direction:new(3096, 1733, 3510) },
    postconditions = { Condition.DistanceTo:new(3096, 1733, 3510, 4), Condition.ModelVisible:new(caelyn) },
  },
  {
    actions = { Action.ModelHighlight:new(ladder) },
    postconditions = { Condition.ModelVisible:new(caelyn) },
  },
  {
    actions = { caelynHl },
    postconditions = { Condition.ConversationText:new("Meet me outside") },
  },
  {
    text = "Head to the Varrock Museum and talk to Caelyn outside.",
    actions = { Action.Direction:new(3251, 1637, 3448) },
    postconditions = { Condition.DistanceTo:new(3251, 1637, 3448, 10) },
  },
  {
    actions = {
      caelynHl,
      Action.ConversationHighlight:new("Give her a pep talk."),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.InventoryContains:new(scarf) },
  },
  {
    text = "Wear the Scarlet Thief's mask and talk to Caelyn again.",
    actions = { Action.InventoryHighlight:new(scarf) },
    postconditions = { Condition.InventoryDoesNotContain:new(scarf) },
  },
  {
    actions = {
      caelynHl,
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("Nice one mate, let's get going.") },
  },
  {
    text = "Go through the short cutscene.",
    actions = {
      Action.ConversationHighlight:new("What was that about"),
    },
    postconditions = { Condition.ConversationText:new("Let's check out the basement") },
  },
  {
    text = "Attempt to walk-down the stairs",
    actions = { Action.ModelHighlight:new(staircaseDown) },
    postconditions = { Condition.ConversationText:new("Let's head upstairs") },
  },
  {
    text = "Walk up the staircase to the east.",
    actions = { Action.ModelHighlight:new(staircaseUp) },
    postconditions = { Condition.ModelVisible:new(oldman) },
  },
  {
    text = "Talk to the Drunken Dwarf to the south.",
    actions = { Action.ModelHighlight:new(drunkenDwarf) },
    postconditions = { Condition.ConversationText:new("Let's grab our tokens") },
  },
  {
    text = "Talk to the Bartender downstairs to get 1 drink token.",
    actions = { Action.ModelHighlight:new(staircaseUp) },
    postconditions = { Condition.ModelVisible:new(curator) },
  },
  {
    actions = { Action.ModelHighlight:new(bartender) },
    postconditions = { Condition.ConversationText:new("We just need to get hold of ") },
  },
  {
    text = "Pickpocket an attendee south-west for 1 drink token.",
    actions = { Action.ModelHighlight:new(attendee) },
    postconditions = { Condition.ConversationText:new("We just need to get hold of ") },
  },
  {
    text = "Talk to the masked woman to the west upstairs and accept the dance off.",
    actions = { Action.ModelHighlight:new(staircaseUp) },
    postconditions = { Condition.ModelVisible:new(oldman) },
  },
  {
    actions = { Action.ModelHighlight:new(maskedWoman), Action.ConversationHighlight:new("We accept.") },
    postconditions = {
      Condition.ConversationText:new("It's time to put"),
    },
  },
  {
    text = "After the cutscene, talk to the masked woman again to get the 1 drink token.",
    actions = {},
    postconditions = { Condition.ConversationText:new("We just need to get hold of ") },
  },
  {
    text = "Talk to Mr Mordaut, the dragon.",
    actions = { Action.ModelHighlight:new(mordaut) },
    postconditions = { Condition.ConversationText:new("Let's have a look around") },
  },
  {
    text = "Go downstairs and search the crate next to the Bartender to get a cheese tray.",
    actions = { Action.ModelHighlight:new(staircaseUp) },
    postconditions = { Condition.ModelVisible:new(curator) },
  },
  {
    actions = { Action.Direction:new(12, 0, 3, { instance = true }) },
    postconditions = { Condition.ConversationText:new("Let's get this cheese") },
  },
  {
    text = "Go upstairs and talk to Mr Mordaut again for the last drink token.",
    actions = { Action.ModelHighlight:new(staircaseUp) },
    postconditions = { Condition.ModelVisible:new(oldman) },
  },
  {
    actions = { Action.ModelHighlight:new(mordaut) },
    postconditions = { Condition.ConversationText:new("That's the last of the") },
  },
  {
    text = "Talk to the Drunken Dwarf.",
    actions = {
      Action.ModelHighlight:new(drunkenDwarf),
      Action.ConversationHighlight:new("Start the distraction."),
    },
    postconditions = { Condition.ConversationText:new(" Well, here goes nothin'.") },
  },
  {
    text = "After another cutscene, go down to the basement.",
    actions = {},
    postconditions = { Condition.ConversationText:new("Let's get down to the basement") },
  },
  {
    actions = { Action.ModelHighlight:new(staircaseDown) },
    postconditions = { Condition.ConversationText:new("Do you see that") },
  },
  {
    text = "Avoid the guards, following the wires on the ground to find the levers.<ul><li>There is one lever in each of the north, east and western rooms. Avoid the guards by blocking their line of sight with objects where possible.</li><li>North: Avoid the guards and hide in the crate (halfway along the back wall) to avoid the guard.</li><li>East: Hide in the crate (Halfway along the eastern wall) to avoid the guard.</li><li>West: Notice the walking pattern of each guard. Then walk directly behind the guards along their route waiting for when they turn away to move.</li>",
    actions = {},
    postconditions = { Condition.ConversationText:new("Looks like that's done it") },
  },
  {
    text = "Open the display case.",
    actions = { Action.ModelHighlight:new(displayCase) },
    postconditions = { Condition.ConversationText:new("Right, that's done the trick") },
  },
  {
    text = "After a cutscene, take from the display case.",
    actions = { Action.ModelHighlight:new(displayCase2) },
    postconditions = { Condition.ConversationText:new("Finally, the ruby") },
  },
  {
    actions = { Action.ConversationHighlight:new("") },
    postconditions = { Condition.ConversationText:new("Nooo!") },
  },
  {
    text = "Talk to Caelyn.",
    actions = {
      caelynHl,
      Action.ConversationHighlight:new("Is this all?"),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Heartstealer",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1612742400,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
