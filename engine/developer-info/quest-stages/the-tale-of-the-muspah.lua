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
    text = "Talk to Erjolf on the mountain north-east of Rellekka. (Fairy ring DKS)",
    title = "How to Become a Fremennik",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Okay, your secret is safe with me.") },
  },
  { text = "Jump the flat rock to cross the river." },
  { text = "Enter the Cave opening to the north-west." },
  {
    text = "Talk to Erjolf about his progress and offer to help.<ul><li>You should proceed the dialogue and cutscene thoroughly.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Tell me more about your Trials."),
      Action.ConversationHighlight:new("Perhaps I can help with your Trials."),
      Action.ConversationHighlight:new("I'm ready to learn how to melt the ice."),
    },
  },
  {
    text = "Take at least 14 driftwood from the driftwood pile (can also be done in smaller batches if backpack space is a concern).",
    title = "Rafts of fire",
  },
  {
    text = "Make-fireraft with the driftwood you picked up.<ul><li>You should have 14 fire raft in the backpack.</li></ul>",
    actions = { Action.ConversationHighlight:new("X") },
  },
  { text = "Block-stream using the ice mound in the northwest." },
  { text = "Jump the flat rock in the southeast on your side." },
  { text = "Jump the flat rock in the northeast on your side." },
  { text = "Block-stream using the ice mound in the north." },
  { text = "Jump the flat rock in the northwest on your side." },
  { text = "Walk to the tile as same as the picture shown below." },
  {
    text = "Launch 5 fire rafts quickly from the northern shore of the pool. Launch them from the same spot such that when they float south, they end up across the east side of the frozen armour and are blocked from leaving the cave by a rock. You do not need to face correctly to launch fire rafts, the player character will launch the raft into the water next to the tile they are standing on.",
  },
  { text = "Walk to the tile as same as the picture shown below." },
  {
    text = "Launch 3 fire rafts from the northern shore of the pool. Launch them on 3 adjacent spots that, when they float south, they end up on the north side of the frozen armour.",
  },
  { text = "Unblock-stream using blocked icy stream in the southwest." },
  { text = "Jump the flat rock in the east on your side." },
  { text = "Jump the flat rock in the southeast on your side." },
  { text = "Block-stream using the ice mound in the southeast." },
  { text = "Walk to the tile as same as the picture shown below." },
  {
    text = "Launch 4 fire rafts quickly from the eastern shore of the pool. Light them on the same spot that, when they float west, they end up across the south side of the frozen armour.",
  },
  { text = "Unblock-stream using blocked icy stream in the northeast." },
  { text = "Jump the flat rock in the southwest on your side." },
  { text = "Block-stream using the ice mound in the northwest." },
  { text = "Walk to the tile as same as the picture shown below." },
  {
    text = "Launch 1 fire raft from the western shore of the pool. Light them on the spot that, when they float east, they end up on the west side of the frozen armour. You'll need to light the rafts in the western stream, not against the pool shore.",
  },
  { text = "Jump the flat rock in the west on your side." },
  { text = "Walk to the another tile as same as the picture shown below." },
  { text = "Launch 1 fire raft from the western shore of the pool." },
  {
    text = "Talk to Erjolf.",
    title = "The monster in ice",
    actions = {
      Action.ConversationHighlight:new("What should we do now?"),
      Action.ConversationHighlight:new("Yes, look at the creature."),
    },
  },
  {
    text = "Exit the cave, climb down the mountain and go north across the narrow strip of land. Head north-east and past the sabre-toothed kyatts in the Rellekka Hunter area.",
  },
  {
    text = "Talk to the natural historian.",
    actions = {
      Action.ConversationHighlight:new("Don't you know what a yeti looks like?"),
      Action.ConversationHighlight:new("Are you any good at identifying creatures?"),
      Action.ConversationHighlight:new("Okay, I can start describing it now."),
    },
  },
  {
    text = "Describe the creature to him.",
    actions = {
      Action.ConversationHighlight:new("It's yellow."),
      Action.ConversationHighlight:new("It has four arms."),
      Action.ConversationHighlight:new("It has no legs, like a snail."),
      Action.ConversationHighlight:new("It has spikes, which run down its spine."),
      Action.ConversationHighlight:new("It has a spiky tail, with vicious spines."),
      Action.ConversationHighlight:new("It has pincers, like a crab."),
    },
  },
  {
    text = "Continue dialogue with the historian.",
    actions = {
      Action.ConversationHighlight:new("What is the creature I described?"),
      Action.ConversationHighlight:new("Where can I find out more about this creature?"),
    },
  },
  { text = "Head north of Pollnivneach along the River Elid to a sand pile.", title = "Terror of the mind" },
  { text = "Dig the sand pile." },
  {
    text = "After Ali the Wise speaks, talk again and ask about the Muspah.",
    actions = {
      Action.ConversationHighlight:new("I've found a creature just like this statue."),
      Action.ConversationHighlight:new("Why do you think the creature I've found is a myth?"),
      Action.ConversationHighlight:new("If the Muspah is a myth, how could I have found it?"),
      Action.ConversationHighlight:new("How can I clear the ice from around the Muspah?"),
      Action.ConversationHighlight:new("Can you help me get back to the Muspah cave?"),
    },
  },
  {
    text = "Head back to the Muspah cave.<ul><li>The ice cave where you started, by Fremennik Province lodestone, or fairy ring DKS.</li></ul>",
    title = "The muspah's true form",
    neededItems = {
      ["Sapphire"] = { quantity = 1 },
      ["Water rune"] = { quantity = 1 },
      ["Cosmic rune"] = {
        quantity = 1,
      },
    },
    recommendedItems = {},
  },
  { text = "If you haven't obtained sapphires yet, mine the sapphire rocks nearby." },
  { text = "Cross the floating bridge." },
  { text = "Bore-sapphires in the ice block, then enchant-sapphires on it." },
  { text = "Pick up the four sapphires from the ground, or more will have to be obtained." },
  {
    text = "Talk to the Muspah and offer to help.",
    actions = {
      Action.ConversationHighlight:new("I only wanted to help!"),
      Action.ConversationHighlight:new("That's okay."),
      Action.ConversationHighlight:new("What were you doing in the ice?"),
      Action.ConversationHighlight:new("Tell me about the rejuvenation ritual."),
      Action.ConversationHighlight:new("So what do you need now?"),
      Action.ConversationHighlight:new("Never mind."),
    },
  },
  {
    text = "Talk to Erjolf just outside the chamber.",
    title = "Hibernating",
    neededItems = {
      ["Water rune"] = { quantity = 1 },
      ["Cosmic rune"] = { quantity = 1 },
      ["Sapphire"] = {
        quantity = 1,
      },
    },
    recommendedItems = {},
  },
  { text = "Exit the caves and head north to the icy Rellekka Hunter area." },
  { text = "On the western shore, make-canoe with the fallen log, then travel with the canoe." },
  { text = "Enter the cavern." },
  {
    text = "Dismiss any followers you have (if Jhallan still says to dismiss your pet, turn off any overrides you may have for other pets).",
  },
  {
    text = "Slightly south, talk to Jhallan.",
    actions = { Action.ConversationHighlight:new("Yes, follow me. We'll find somewhere you can rest.") },
  },
  { text = "Lead to the far south-west corner of the cave, away from the monsters." },
  {
    text = "At the end of the path, talk to Jhallan.",
    actions = { Action.ConversationHighlight:new("Is this spot peaceful enough for you?") },
  },
  { text = "After speaking, bore-sapphires on Jhallan, then enchant-sapphires on him." },
  {
    text = "Return to the icy hunter area via the canoe or the Fremennik Province lodestone.",
    title = "Back to the future",
  },
  {
    text = "Talk to Erjolf to give him the Muspah tail.<ul><li>Talk to the natural historian for an experience lamp.</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Tale of the Muspah",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1240272000,
  prereqQuests = {},
})
