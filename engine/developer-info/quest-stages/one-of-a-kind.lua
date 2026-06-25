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
    text = "Talk to Mr. Mordaut in his office in the basement of Varrock Museum that is accessed behind the stairs.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("[One of a Kind]"), Action.ConversationHighlight:new("I am?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Read the book Hannibus Hunted." },
  {
    text = "Talk to Mr. Mordaut.",
    actions = {
      Action.ConversationHighlight:new("So?"),
      Action.ConversationHighlight:new("Yes, fascinating!"),
      Action.ConversationHighlight:new("Bob the Cat?"),
      Action.ConversationHighlight:new("That cat is the reincarnation of Robert the Strong!"),
    },
  },
  {
    text = "Locate Bob the Cat with the catspeak amulet. An easy method to locate him is to hop worlds at the west Varrock anvils or searching nearby the Burthorpe lodestone. Most of the time he is inside Carwen Essencebinder Magical Runes Shop",
    title = "Bob the Cat",
    neededItems = { ["Catspeak amulet (e)"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Talk to Bob.",
    actions = {
      Action.ConversationHighlight:new("[One of a Kind]"),
      Action.ConversationHighlight:new("A human to look after?"),
      Action.ConversationHighlight:new("Maybe I'll check out this house..."),
    },
  },
  { text = "Travel to Unferth's house east of Burthorpe lodestone.", title = "Unferth's house" },
  {
    text = "Talk to Unferth.",
    actions = {
      Action.ConversationHighlight:new("I want to search your house for a hidden library."),
      Action.ConversationHighlight:new("I'm not interested in your novels."),
      Action.ConversationHighlight:new("I'm looking for the lost library of Robert the Strong."),
      Action.ConversationHighlight:new("Is there any place that Bob the Cat seemed drawn to?"),
      Action.ConversationHighlight:new("That's all for now."),
    },
  },
  { text = "Search the bookcase." },
  { text = "Investigate the fireplace." },
  { text = "Inspect the table." },
  { text = "Remove the carpet." },
  { text = "Enter the trapdoor." },
  {
    text = "Search the desk in the centre of the room.",
    title = "Robert the Strong's library",
    neededItems = { ["Emerald"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Investigate the weapon rack to the north.",
    actions = { Action.ConversationHighlight:new("Take some rust from the weapons.") },
  },
  { text = "Use iron oxide on emerald to get Animate Rock scroll." },
  { text = "Read the Animate Rock scroll by the statue to the south." },
  {
    text = "Talk to Hannibus.",
    actions = {
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("This is the Sixth Age."),
      Action.ConversationHighlight:new("Are you alright?"),
      Action.ConversationHighlight:new("What now?"),
      Action.ConversationHighlight:new("You could go home."),
      Action.ConversationHighlight:new("We could look around this library!"),
      Action.ConversationHighlight:new("Let's take a look!"),
    },
  },
  { text = "Search all bookcases for the Dragonkin Primer to the east and Flight of the Dragonkin to the west." },
  { text = "Click the map in the south of the room to update location names" },
  {
    text = "Head to the mysterious statue east of the Seers' Village bank.",
    title = "The mysterious statues",
    neededItems = { ["Animate Rock scroll"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Read the Animate Rock scroll by it.",
    actions = {
      Action.ConversationHighlight:new("None of those - I'm human!"),
      Action.ConversationHighlight:new("I'll rotate you now."),
      Action.ConversationHighlight:new("Goodbye."),
    },
    postconditions = { Condition.ConversationText:new("(Dialogue ends.)") },
  },
  {
    text = "Repeat for the next 3 statues. Rotating all 4 statues gives access to 10,000 bonus experience in any skill.<ul><li>North-west of the Karamja lodestone.</li><li>North-west of the Ardougne lodestone.</li><li>North of the Tower of Life (fairy ring DJP, Ardougne cloak for east of Kandarin Monastery, or Kandarin Monastery Teleport).</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Just tell me how to rotate you."),
      Action.ConversationHighlight:new("Goodbye."),
      Action.ConversationHighlight:new("Just tell me how to rotate you."),
      Action.ConversationHighlight:new("Goodbye."),
      Action.ConversationHighlight:new("Just tell me how to rotate you."),
      Action.ConversationHighlight:new("Goodbye."),
    },
    postconditions = { Condition.ConversationText:new("(Dialogue ends.)") },
  },
  { text = "Travel to the north-west corner of Entrana." },
  {
    text = "Read the animate rock scroll by the submerged statue.",
    actions = {
      Action.ConversationHighlight:new("Who was your other visitor?"),
      Action.ConversationHighlight:new("Do you know of a way to travel between worlds?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
    postconditions = { Condition.ConversationText:new("(Dialogue ends.)") },
  },
  {
    text = "Run to the green dragons in the south east corner of the Forinthry Dungeon. Hannibus will talk to you if you brought the Dragonkin Primer book.<ul><li>This dungeon is located in the Wilderness, so make sure that you are not opted in to PvP combat by talking to Vala.</li><li>A quick method is to use the Wilderness sword 2 teleport to the dungeon, or run east to the Wilderness Crater lodestone and run straight west to the dungeon entrance.</li><li>If you don't see Hannibus, exit the dungeon and re-enter it, then run towards green dragons, it will give you an option to continue the quest. Make sure you brought the Dragonkin Primer book.</li></ul>",
    title = "Forinthry Dungeon",
    neededItems = { ["Dragonkin Primer"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Run close to the east wall of the room, near the lava.",
    actions = { Action.ConversationHighlight:new("Let's investigate!") },
  },
  { text = "Study the rock (on the ground east of the green dragon)." },
  {
    text = "Travel to the iron/steel dragons in Brimhaven Dungeon.<ul><li>A quick method is using the Dungeoneering cape and selecting 'Brimhaven metal dragon dungeon'</li><li>A slower method is walking west from the Karamja lodestone, entering the dungeon, and walking to the iron and steel dragons</li><li>If you don't get the question about 'One of a Kind' go back to the entrance of the room and walk slowly into the room again.</li></ul>",
    title = "Brimhaven Dungeon",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Kill 3 iron dragons.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Head to the west wall by the mysterious entrance to study the dragonkin writing." },
  {
    text = "Travel to the Grotworm Lair north-west of Port Sarim lodestone.<ul><li>Remora's necklace will take you directly outside of the portal.</li></ul>",
    title = "Grotworm Lair",
  },
  {
    text = "Enter the bottom level of the Grotworm Lair. You can use the shortcut to the south-east of the first floor to get there quickly.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Investigate the summoning portal. The portal is right next to the shortcut, a square hole in the wall gleaming green.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Talk with Hannibus.",
    actions = {
      Action.ConversationHighlight:new("Your majesty - I have some questions."),
      Action.ConversationHighlight:new("I seek means to travel between worlds."),
      Action.ConversationHighlight:new("Tell me about the Dragonkin."),
      Action.ConversationHighlight:new("Yes"),
    },
  },
  {
    text = "Travel to Dragontooth Island by talking to the Ghost captain east of Port Phasmatys (South, in the small boat).<ul><li>The fastest method is teleporting to Dragontooth Island directly, using the Dungeoneering cape teleport, or using the elder divination outfit to teleport to the 'Radiant Wisp Colony'</li><li>A quick method is heading to Port Sarim and taking a charter ship to Port Phasmatys (talk to Trader Stan south-east of the lodestone)</li><li>There are two ghost captains, you need to talk to the southern one who is standing next to a rowboat, not a ship</li></ul>",
    title = "Dragontooth Island",
    neededItems = { ["Ghostspeak amulet"] = { quantity = 1 }, ["Cramulet"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Please take me to Dragontooth Island.") },
  },
  { text = "Enter the mysterious entrance to the south." },
  {
    text = "Fight/Tank a celestial dragon until Hannibus's bond reaches 100% (approximately 2 minutes).<ul><li>Attack multiple to reach 100% faster.</li></ul>",
  },
  { text = "Talk to Hannibus again." },
  {
    text = "Activate the Artefact north of Edgeville Monastery to meet with Hannibus in the King Black Dragon Lair.<ul><li>You will not enter a player-versus-player enabled area despite the warning.</li></ul>",
    title = "The white dragon",
    actions = {
      Action.ConversationHighlight:new("Yes - Meet with Hannibus."),
      Action.ConversationHighlight:new("Continue"),
    },
  },
  {
    text = "Talk with the King Black Dragon.",
    actions = { Action.ConversationHighlight:new("Sorry about killing you.") },
  },
  { text = "Enter Mysterious entrance." },
  {
    text = "Talk with Therragorn.",
    actions = {
      Action.ConversationHighlight:new("Listen to Hannibus."),
      Action.ConversationHighlight:new("Can you help Hannibus get home?"),
    },
  },
  { text = "Kill the 3 Dragon-Hunters." },
  { text = "Talk to Hannibus for a cutscene." },
  {
    text = "Kerapac will talk to you.",
    title = "Daemonheim with Kerapac",
    actions = {
      Action.ConversationHighlight:new("Aaaaargh! A Dragonkin!"),
      Action.ConversationHighlight:new("Ask about companions."),
      Action.ConversationHighlight:new("How's Hannibus?"),
      Action.ConversationHighlight:new("What's the favour?"),
      Action.ConversationHighlight:new("Okay, I'll help."),
    },
  },
  {
    text = "Head into the far northern room. Kerapac will talk to you.",
    actions = { Action.ConversationHighlight:new("Just tell me what to do.") },
  },
  { text = "Harvest 25 dragonkin memories from the protoplasmic wisps and channel them into the cage." },
  { text = "Talk with Kerapac to fight Echo of Jas.", actions = { Action.ConversationHighlight:new("I am ready!") } },
  { text = "Kill the Echo of Jas and then talk to it after you defeat it." },
  {
    text = "Kerapac will talk to you.",
    title = "Wrapping up",
    actions = {
      Action.ConversationHighlight:new("What now?"),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("[Decide Hannibus's fate]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "One of a Kind",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1394409600,
  prereqQuests = {
    "A Tail of Two Cats",
    "Holy Grail",
    "The World Wakes",
    "Missing, Presumed Death",
    "Ritual of the Mahjarrat",
  },
})
