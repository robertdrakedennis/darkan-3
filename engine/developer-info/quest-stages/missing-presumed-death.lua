local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Brother Samwell (west of Paterdomus and east of Varrock).",
    title = "Murders in Silvarea - First crime scene",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Search for the following clues.<ul><li>Investigate northernmost slaughtered monk.</li><li>Investigate nearby plant (east of the chest).</li><li>Investigate the tree with the arrow stuck in it (south of him).</li></ul>",
    actions = { Action.ConversationHighlight:new("Investigate torso.") },
    postconditions = { Condition.ConversationText:new("(Shows the previous options.)") },
  },
  {
    text = "Talk to Brother Samwell.",
    actions = {
      Action.ConversationHighlight:new("I think I've found all the clues."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Up the limestone quarry, speak to Odd Old Man.",
    title = "Second crime scene",
    actions = {
      Action.ConversationHighlight:new("Ask about the slaughtered monks."),
      Action.ConversationHighlight:new("Persuade him."),
      Action.ConversationHighlight:new("Persuade him."),
      Action.ConversationHighlight:new("Threaten him."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Please leave me alone. I've had too many scares for one day, I don't want to talk about it any more."
      ),
    },
  },
  {
    text = "Talk to Brother Samwell.",
    actions = {
      Action.ConversationHighlight:new("He saw an elf heading east along the path."),
      Action.ConversationHighlight:new("He saw a very tall, dark, hooded figure escaping."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Search for additional clues by following the crystals to the east.<ul><li>Investigate the killed elf ranger (southeast of the Temple).</li><li>Investigate the plant west of her, near the middle pile of crystals next to the stairs.</li><li>Investigate the clothing on the fence, to the south of the tree.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Investigate arms."),
      Action.ConversationHighlight:new("Listen to the trapped soul."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "(First time:) You added an additional clue to your notebook.(Repeated in a pop-up:)Additional clue discovered.(Continues below.)"
      ),
    },
  },
  {
    text = "Talk to Blaze Sharpeye by the beacon.",
    actions = {
      Action.ConversationHighlight:new("Ask about the murdered elf."),
      Action.ConversationHighlight:new("Threaten him."),
      Action.ConversationHighlight:new("Threaten him."),
      Action.ConversationHighlight:new("Persuade him."),
    },
    postconditions = {
      Condition.ConversationText:new(" Well... Okay, I've never witnessed anything like it in my life."),
    },
  },
  {
    text = "Return to Brother Samwell.",
    actions = {
      Action.ConversationHighlight:new("I think I've found all the clues."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Go to the Wizards' Tower and speak to Wizard Valina inside the entry way on the lectern.",
    title = "Valina's information",
    actions = {
      Action.ConversationHighlight:new("I was told by Brother Samwell that you might be able to help us."),
      Action.ConversationHighlight:new("Ask about the elves."),
      Action.ConversationHighlight:new("Ask about Saradominist wizards."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Ask about teleportation."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " A possible explanation is that the killer opened a rift to escape to a darker realm or dimension."
      ),
    },
  },
  {
    text = "Return to Brother Samwell.",
    title = "Fighting with Icthlarin",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I think somebody else did this."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  {
    text = "Learn from Icthlarin about Death's disappearance.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Someone killed these people."),
      Action.ConversationHighlight:new("Why does this concern you?"),
      Action.ConversationHighlight:new("Is everyone who dies trapped now?"),
      Action.ConversationHighlight:new("Continue."),
      Action.ConversationHighlight:new("I'm ready to help."),
    },
    postconditions = {
      Condition.ConversationText:new(" Prepare yourself, adventurer. I sense the approach of undead."),
    },
  },
  { text = "Defeat attacking wights." },
  {
    text = "Finish the conversation with Icthlarin.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "While standing next to Icthlarin, open the invitation box.",
    actions = { Action.ConversationHighlight:new("We must, Death needs our help.") },
    postconditions = { Condition.ConversationText:new("You slowly tilt back the lid of the box.") },
  },
  {
    text = "Once in the citadel, talk to Icthlarin.  A cutscene will play.",
    title = "The Citadel",
    actions = { Action.ConversationHighlight:new("I want to continue to the citadel.") },
    postconditions = {
      Condition.ConversationText:new(
        " If I use my invitation to enter the main chamber, you can infiltrate the rest of the citadel."
      ),
    },
  },
  { text = "Enter the door to the west then north to the window to watch another cutscene." },
  {
    text = "Walk out the south window; jump the gap; run across the planks; drop down the rocks; climb up the rock face; walk in the western window.",
  },
  { text = "Climb through the obstacle to the north." },
  { text = "Open the north door, then continue north into the next room." },
  { text = "Pull all three levers in the room (on the west, south, and east walls) to create light." },
  { text = "Lead all the wights that spawn (a group of 3, then 4) into the light to be able to damage them." },
  { text = "View the east window for another cutscene." },
  { text = "Open the door to the north, then continue east into the next room." },
  {
    text = "Use the viewing panel. , then rotate the bottom-left machine once, the top-left one twice, the top-right three times and the bottom-right machine once.",
    actions = {
      Action.ConversationHighlight:new("Stop wasting my time."),
      Action.ConversationHighlight:new("What do I do?"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "View the south window for another cutscene." },
  { text = "Open the door to the east, then continue south into the next room." },
  {
    text = "Talk to the statue of Death.",
    actions = {
      Action.ConversationHighlight:new("Get to the point."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("How do I answer?"),
    },
  },
  {
    text = "Open the chest and take the correct item, then place on the answer plinth pedestal.<ul><li>First chest (east) , place on answer plinth.</li><li>Second chest (middle) , place on answer plinth.</li><li>Second chest, again , place on answer plinth.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Take sand."),
      Action.ConversationHighlight:new("Yes, final answer."),
      Action.ConversationHighlight:new("Take water."),
      Action.ConversationHighlight:new("Yes, final answer."),
      Action.ConversationHighlight:new("Take Shadow."),
      Action.ConversationHighlight:new("Yes, final answer."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Now hold up your end of the bargain. I played your game - you must release Death."
      ),
    },
  },
  { text = "View the west window for another cutscene." },
  { text = "Open the south door, then jump off the drop." },
  {
    text = "Speak to one of the Barrows brothers to enter the main room.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "After a cutscene, walk forward to continue talking with the Gods.",
    actions = {
      Action.ConversationHighlight:new("Let Sliske carry on with his show."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Run around rear of other gods and stamp out the smaller flames on the ground to reach Death's cage while avoiding the fireballs from Strisath. Free him from his cage.<ul><li>The flames might be difficult to see with low graphics settings, especially with shadows turned off.</li></ul>",
  },
  { text = "Run back behind Icthlarin.<ul><li>Getting hit by the fireballs will reset your progress.</li></ul>" },
  { text = "Speak to Icthlarin." },
  { text = "Speak with Brother Samwell." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Missing, Presumed Death",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = false,
  length = Enums.length.mediumlong,
  releaseDate = 1381795200,
  prereqQuests = {
    "Koschei's Troubles (miniquest)",
    "The Chosen Commander",
    "Ritual of the Mahjarrat",
    "The World Wakes",
    "The Death of Chivalry",
  },
})
