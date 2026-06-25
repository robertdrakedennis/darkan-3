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
    text = "Talk to Ali the Wise in the northernmost building in Nardah.",
    title = "The mysterious archaeologist",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("How goes the research?"),
      Action.ConversationHighlight:new("Well, I certainly don't like those of the Zamorakian faction."),
      Action.ConversationHighlight:new("Zaros seems to be an honourable god."),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Can you help me get to the Digsite Exam Centre?") },
  },
  {
    text = "Go to the Exam Centre.<ul><li>If you asked Ali for help getting there, he gave you a Dig Site pendant (or Ali's pendant if you have not unlocked the ability to create Dig Site pendants) to teleport directly there.</li><li>Alternatively, teleporting to Varrock Dig Site with either pendant for the first time will grant you the Return to Senntisten achievement.</li></ul>",
  },
  {
    text = "Talk to Dr. Nabanik outside of the Exam Centre south of the Digsite.",
    actions = {
      Action.ConversationHighlight:new("(Whisper) Why are we speaking so quietly?"),
      Action.ConversationHighlight:new("Azzanadra? You are quite the master of disguise."),
      Action.ConversationHighlight:new("Why are you here, away from the pyramid?"),
      Action.ConversationHighlight:new("What's Ali's part in this?"),
      Action.ConversationHighlight:new("Why all the secrecy?"),
      Action.ConversationHighlight:new("Just tell me what you want."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " I wish for your aid in getting a restoration certificate, which will grant me permission to perform some restorations to the temple beneath these ruins."
      ),
    },
  },
  {
    text = "Talk to Terry Balando inside the exam centre for a restoration certificate.",
    actions = {
      Action.ConversationHighlight:new("I'm here about a close friend of mine: Dr Nabanik."),
      Action.ConversationHighlight:new("He was in the pyramid for a very long time."),
      Action.ConversationHighlight:new("I've seen those beetles and they're huge."),
      Action.ConversationHighlight:new("He studies pyramids, particularly their interiors."),
      Action.ConversationHighlight:new("He has an aged benefactor."),
    },
    postconditions = {
      Condition.ConversationText:new(" This benefactor is not likely to die and leave Nabanik suddenly poor, I trust?"),
    },
  },
  { text = "Talk to Dr. Nabanik. He will give you a rope." },
  { text = "Use the rope on the winch in the north-east dig site, north of The Stormbreaker ship." },
  { text = "Operate the winch to enter the Digsite dungeon." },
  {
    text = "Talk to Azzanadra in the southern part of the dungeon.",
    actions = {
      Action.ConversationHighlight:new("I'd like to talk about the temple."),
      Action.ConversationHighlight:new("Is there anything I can do to help?"),
      Action.ConversationHighlight:new("Okay, what do I need to do?"),
      Action.ConversationHighlight:new("Tell me about the mission in the north."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Open the ruined backpack for a battered letter and heat globe." },
  {
    text = "Read the letter and then talk to Azzanadra.",
    actions = {
      Action.ConversationHighlight:new("I'd like to talk about the temple."),
      Action.ConversationHighlight:new("Tell me about the mission in Morytania."),
      Action.ConversationHighlight:new("I have some questions about the mission in Morytania."),
      Action.ConversationHighlight:new("Do you have any advice for me?"),
      Action.ConversationHighlight:new("Let's talk about something else."),
      Action.ConversationHighlight:new("Tell me about the artefacts you need."),
      Action.ConversationHighlight:new("Tell me about the fortress artefact."),
      Action.ConversationHighlight:new("Tell me about the Barrows artefact."),
      Action.ConversationHighlight:new("Never mind."),
    },
    postconditions = { Condition.ConversationText:new(" As you wish.") },
  },
  { text = "Travel to the Barrows.", title = "Barrows icon" },
  {
    text = "Kill the six Barrows Brothers found in their tombs below the ground.<ul><li>Barrows amulets can be used to skip killing up to three brothers.</li><li>Amulets can only be used on the tombs before the brother is summoned.</li><li>The final brother must be killed without using an amulet.</li></ul>",
  },
  {
    text = "Once all six brothers are dead, search the chest at the centre of the tunnels beneath the crypts to get the Barrows icon.",
  },
  {
    text = "Go to the Ghorrock fortress:<ul><li>First use the canoe near Erjolf from Tale of the Muspah for a quick access. It is located on the west coast of the Rellekka Hunter area, north of fairy ring code DKS.</li><li>Go east and place the heat globe on the pedestal near the ice block entrance.</li><li>Squeeze past the ice block.</li></ul>",
    title = "Frostenhorn",
    neededItems = { ["Telekinetic grab"] = { quantity = 1 }, ["Heat globe"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Enter the fortress through the south entrance and climb-up the stairs to the west." },
  { text = "Run to the south-east corner of the castle and climb-down the stairs east." },
  { text = "Scale up the damaged wall north of the door." },
  { text = "Climb down the smashed rampart in the north-west corner." },
  { text = "Enter the trapdoor in front of you." },
  { text = "Run east and take the heat globe.", title = "Upper level" },
  {
    text = "Return to the entrance room of the basement, and place the heat globe on the pedestal on the west side.<ul><li>A waterfiend will be released. It is immune to fire damage, but does not need to be killed.</li></ul>",
  },
  { text = "Go inside the west room, then right-click the radiant pedestal to cast Telekinetic Grab on it." },
  {
    text = "Use the globe on this room's pedestal. Go down the trapdoor.<ul><li>A waterfiend will be released. It is immune to fire damage, but does not need to be killed.</li></ul>",
  },
  { text = "Pick up the globe in the southern room.", title = "Lower level" },
  { text = "Use it on the pedestal back in the northern room with the ladder. 2 waterfiends will spawn." },
  { text = "Loot the crates in the south room for 40 law runes and 300 air runes." },
  { text = "Go to the east room and take the globe from the pedestal for an imperfect heat globe." },
  { text = "Exit the dungeon the way you entered." },
  {
    text = "Scale the damaged wall to the west, then head to the north-east corner to the destroyed pedestal.",
    title = "Surface",
  },
  { text = "Insert-globe in the smashed globe-holder." },
  { text = "Choose the push option on the smashed pedestal on the ground." },
  {
    text = "Return to the dungeon by re-entering the trapdoor.",
    actions = { Action.ConversationHighlight:new("Yes, carry on from where I was.") },
  },
  { text = "Go down the trapdoor in the western room.", title = "Lower level" },
  { text = "Run through the dungeon and take the heat globe from the pedestal in the southwestern corner." },
  {
    text = "Head back east through the hallway you ran through until you reach the southeast corner, where the path turns north.",
  },
  { text = "At that corner, look for a pipe on the south wall." },
  { text = "Choose the 'Place-globe' option on the pipe." },
  { text = "Choose the 'Whack' option on the pipe." },
  { text = "Continue into the southeastern room and climb the ladder to go up." },
  { text = "Head west and kill the ice demon (it cannot be damaged by water spells).", title = "Upper level" },
  { text = "Take the Frostenhorn that it drops." },
  {
    text = "Return to Azzanadra with the items back at the Senntisten Temple underneath the Dig Site.<ul><li>The Dig Site pendant can be used to teleport outside the temple (option 2).</li></ul>",
    title = "The big bang",
    neededItems = { ["Barrows icon"] = { quantity = 1 }, ["Frostenhorn"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I'd like to talk about the temple."),
      Action.ConversationHighlight:new("I have something here for you."),
      Action.ConversationHighlight:new("What does the Frosthorn do?"),
      Action.ConversationHighlight:new("Okay, what do I need to obtain next?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Fear not, there is no more gathering of items for you. Another of my illustrious contacts is making their way here with the final item."
      ),
    },
  },
  {
    text = "Back outside near the winch, talk to the Assassin for a relic.",
    actions = {
      Action.ConversationHighlight:new("Mask or no mask, I remember you."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Return to Azzanadra.",
    actions = {
      Action.ConversationHighlight:new("I'd like to talk about the temple."),
      Action.ConversationHighlight:new("Anyway, what now?"),
      Action.ConversationHighlight:new("I'm not interested, I just want a reward"),
      Action.ConversationHighlight:new("Boring! Get on with it!"),
    },
    postconditions = {
      Condition.ConversationText:new(" Hold thy tongue, Player! I will not have you ruin centuries of planning!"),
    },
  },
  {
    text = "After the cutscene, speak to Azzanadra again.",
    actions = {
      Action.ConversationHighlight:new("I'm glad I could help."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Temple at Senntisten",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1259798400,
  prereqQuests = { "Devious Minds", "Desert Treasure", "The Curse of Arrav", "Kudos" },
})
