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
    text = "If you did not bring Leela to Senliten's tomb, speak to Leela in Draynor Village; she will teleport the player to Senliten. Leela can only be found there afterwards.<ul><li>If you need to return later, you can pick the option to return to the Pharaoh Queen directly (without having to go through the pyramid).</li></ul>",
    title = "Before starting the quest",
  },
  {
    text = "Talk to Senliten in Uzer Mastaba.",
    title = "Senliten's concern",
    neededItems = {
      ["Cat"] = { quantity = 1 },
      ["Kitten"] = { quantity = 1 },
      ["Hellcat"] = { quantity = 1 },
      ["Desert robe outfit (white)"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Talk about Do No Evil."),
      Action.ConversationHighlight:new("Where are you going with this?"),
    },
  },
  {
    text = "[Accept Quest]<ul><li>Inside the pyramid east of fairy ring code DLQ or north-east of Nardah which is easily reached by using Desert amulet 2 or above.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Head to Sophanem and talk to Jex north of the Sphinx.<ul><li>Players can right click on Coenus by the bridge (near the Menaphos lodestone) to pass through to Sophanem.</li><li>To quickly enter the magic carpet network, use a charged amulet of glory and teleport to Al Kharid, or use a Desert amulet 2 to teleport to Nardah.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Could you tell me more about the minor gods?"),
      Action.ConversationHighlight:new("Could you tell me more about Apmeken?"),
      Action.ConversationHighlight:new("Could you tell me more about Apmeken's connection with monkeys?"),
      Action.ConversationHighlight:new("Could you tell me more about Apmeken's followers?"),
      Action.ConversationHighlight:new("Could you tell me more about Apmeken's influence over the desert?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Apmeken's influence was subtle but incredible; she was an infectious laugh, a jaunty jig, a joke you've heard a hundred times before, but still chuckle at."
      ),
    },
  },
  {
    text = "Drop your cat and talk to the Sphinx",
    actions = {
      Action.ConversationHighlight:new("Can you tell me about the gods of the Menaphites?"),
      Action.ConversationHighlight:new("Tell me about the minor gods."),
      Action.ConversationHighlight:new("Tell me about Apmeken."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Apmeken has the head of a monkey and is the incarnation of friendship and jest. Good for a chat, but prone to exaggeration, by all reports."
      ),
    },
  },
  {
    text = "Go back and talk to Senliten. .",
    actions = { Action.ConversationHighlight:new("Talk about Do No Evil.") },
    postconditions = {
      Condition.ConversationText:new(
        " Apmeken is only the beginning. In restoring her, you have done the desert a great service, but there is still more to be done. Amascut's deeds, I fear, are widespread. No doubt her actions have bred more evil."
      ),
    },
  },
  {
    text = "Head to Ape Atoll and equip the monkey greegree and monkeyspeak amulet, then speak to three wise monkeys in the Temple of Marimbo (north-easternmost temple).",
    title = "Three Wise Monkeys",
    neededItems = { ["Monkey greegree"] = { quantity = 1 }, ["Monkeyspeak amulet"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I'm trying to take some of our kind back to the desert."),
      Action.ConversationHighlight:new("Admit to being a human."),
    },
    postconditions = { Condition.ConversationText:new(" I'm a human.") },
  },
  {
    text = "Fill your entire backpack with Green bananas by using take banana from the pile of bananas crate to the east and lay a trail to lure the guard to the crate (you can place bananas every few squares to ensure the guard does not give up. Key-binding the Green banana will make this step easier). You will have to go counter-clockwise around the building due to the large gorilla posted on the north side. See the full guide for advice on luring the guard efficiently.",
  },
  { text = "Return to the three wise monkeys and talk to them." },
  { text = "Remove your greegree and speak to them in human form." },
  {
    text = "Equip your greegree and speak to them again.",
    actions = {
      Action.ConversationHighlight:new("The baboon head."),
      Action.ConversationHighlight:new("On the monkey's shoulder."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Silly squirrel thought he was so clever, with his bushy tail, but cunning monkey said:"
      ),
    },
  },
  {
    text = "Talk to Awowogei, who is in the building south of the temple.",
    actions = {
      Action.ConversationHighlight:new("To build the greatest monkey colony ever!"),
      Action.ConversationHighlight:new("Next."),
      Action.ConversationHighlight:new("Choc ices from Nardah."),
    },
    postconditions = { Condition.ConversationText:new(" A choc ice? What's that?") },
  },
  {
    text = "If you don't have a monkeyspeak amulet mould, Hamab's crafting stall sells one, which is needed for the next step.",
  },
  {
    text = "Talk to Rokuh in Nardah next to the fountain.",
    title = "Chimp ice",
    neededItems = {
      ["Monkey greegree"] = { quantity = 1 },
      ["Rope"] = { quantity = 1 },
      ["Monkeyspeak amulet"] = { quantity = 1 },
      ["Monkeyspeak amulet mould"] = { quantity = 1 },
      ["Ancient Magicks"] = { quantity = 1 },
      ["Ice Burst"] = { quantity = 1 },
      ["Ice Blitz"] = { quantity = 1 },
      ["Ice Barrage"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Commission Awowogei's choc ice."),
      Action.ConversationHighlight:new("Deal!"),
      Action.ConversationHighlight:new("So, if I don't teleport it won't melt?"),
    },
    postconditions = { Condition.ConversationText:new(" Of course, of course.") },
  },
  {
    text = "You should receive one chimp ice. If it melts, right click on Rokuh to buy another. Click to stop it from melting. Higher level spells, like Ice Barrage, will last longer.<ul><li>Unlike standard ancient spell casting, you do not need a staff/wand and orb/book for this. Only the runes are enough.</li></ul>",
  },
  {
    text = "Helpful things to keep in mind:<ul><li>Looking at the world map prevents the chimp ice from melting.</li><li>If you are out of run energy, rest and pull up the world map.</li><li>Talking to NPC's prevents the chimp ice from melting.</li><li>Using transportation such as magic carpets and eagles prevents the chimp ice from melting.</li><li>If you are out of run energy, rest and pull up the world map.</li></ul>",
  },
  {
    text = "Take the chimp ice to King Awowogei without teleporting (this route requires completion of Eagles' Peak. For a full list of routes, see the in-depth quest guide):<ul><li>From Nardah, use the magic carpet to Uzer.</li><li>Run north-west to the desert eagle lair located north of the Uzer Hunter area and north-east of the Dominion Tower. Enter the tunnel shown in the picture, and use the rope that spawns nearby on an eagle to be transported.</li><li>Exit the cave via the north-west entrance and climb down the mountain.</li><li>Run east into Tree Gnome Stronghold, up 1st floor[UK]2nd floor[US] of the Grand Tree and find Daero to the east to travel to the Secret Hangar.</li><li>Talk to Waydar to travel to Crash Island.</li><li>Talk to Lumdo to travel to Ape Atoll. You get a checkpoint here where you can restart if the chimp ice melts.</li><li>Run to King Awowogei. The chimp ice can't be frozen while transformed as a monkey, so choose your moments carefully. An easy method is to freeze it at the mahogany tree before the main gate, turn into a monkey, and enter the gate. Run east to the small obelisk across from the agility course and hide in the grass to freeze it again. Then turn back into a monkey, and run to King Awowogei.</li></ul>",
  },
  { text = "Go to the wise monkeys and add them to the barrel." },
  {
    text = "Teleport to Bandit Camp in the Kharidian Desert",
    title = "The colony",
    neededItems = {
      ["Ring of charos (a)"] = { quantity = 1 },
      ["Ava's accumulator"] = { quantity = 1 },
      ["Monkeyspeak amulet"] = { quantity = 1 },
      ["Monkey greegree"] = { quantity = 1 },
      ["Spade"] = { quantity = 1 },
      ["Barrel of monkeys"] = { quantity = 1 },
      ["Gem bag"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Head north-east around the cliff to the area shown here on the map (you need to be below the cliff) and release the monkeys (open the barrel).",
  },
  { text = "Equip Monkey Speak Amulet and talk to one of the Wise Monkeys." },
  {
    text = "With your ring of charos (a) equipped, speak to Ali Morrisane (north of the Al Kharid lodestone).<ul><li>There is currently an issue where this option might only show up after completing The Feud, despite this quest not being listed as a requirement any more.</li></ul>",
    actions = { Action.ConversationHighlight:new("Talk about Do No Evil.") },
    postconditions = {
      Condition.ConversationText:new(
        " Apmeken is only the beginning. In restoring her, you have done the desert a great service, but there is still more to be done. Amascut's deeds, I fear, are widespread. No doubt her actions have bred more evil."
      ),
    },
  },
  { text = "Unequip your Ava's accumulator if you're wearing it." },
  {
    text = "Take the crate to Ava in Draynor Manor.",
    actions = { Action.ConversationHighlight:new("Talk about making a metal detector.") },
    postconditions = {
      Condition.ConversationText:new(
        " 'Oh, hello Ava can you do this? Can you upgrade that?' What happened to 'How are you today, Ava?' or 'Gee, Ava, what's up in your life?'"
      ),
    },
  },
  { text = "Pick up the spade in the easternmost room if you do not already have one." },
  { text = "Go to the fountain in the south-western corner of the manor's grounds." },
  {
    text = "Wear your Ava's alerter and walk two steps east from the dead tree that can be converted to a hidey-hole, just north-east of the fountain. Dig when it says Bwuk! Bwuk! Bwuk! Bwuk!",
  },
  {
    text = "Return to Ava.",
    actions = { Action.ConversationHighlight:new("Talk about Ava's alerter.") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, I'd say that I was surprised... Well, we can agree that at least the alerter is a great success - a mark of great engineering - even if its user isn't."
      ),
    },
  },
  {
    text = "Teleport to Bandit Camp and in the area north-east of the monkey camp search for crates using the map pictured right. You should dig up 5 crates, repeat this until you receive bundle of carpets.<ul><li>If you obtain any uncut gems or noted waterskins, save them for the next step.</li><li>If you discover a scarab, kill it and then dig again.</li></ul>",
  },
  { text = "Talk to the three wise monkeys twice for a book." },
  { text = "Read the book or at least open it." },
  { text = "Clear Rubble from the four building spots in the colony.", title = "Building the colony" },
  { text = "Build-on the 3 stall plots using your supplies." },
  {
    text = "Stock the stalls.<ul><li>Careful not to stock a stall twice - each one needs to be stocked with one set of items.</li></ul>",
  },
  { text = "Build-on Tent plot" },
  { text = "Talk to the wise monkeys." },
  {
    text = "Talk to them again.",
    actions = { Action.ConversationHighlight:new("Right, I'll get on with it.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head back to Ape Atoll and find the building with the monkey knife fighters; it is located just east of the building with the Monkey Child.",
    title = "Arming the colony",
  },
  {
    text = "Punch and then quickly pickpocket the monkey fighters for 6 Monkey knives. The success rate is fairly low, so be prepared to punch at least 30 monkeys.<ul><li>If you are fast enough, you can get three pickpockets until the monkey wakes up again</li></ul>",
  },
  { text = "Return to the three wise monkeys and hand the knives over." },
  { text = "Talk to the monkeys again." },
  {
    text = "Clear Rubble from the four building spots in the colony.",
    title = "Building the colony",
    neededItems = {
      ["Hammer"] = { quantity = 1 },
      ["Saw"] = { quantity = 1 },
      ["Teak plank"] = { quantity = 1 },
      ["Protean plank"] = { quantity = 1 },
      ["Plank box"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Build-on the 3 stall plots using your supplies." },
  {
    text = "Stock the stalls.<ul><li>Careful not to stock a stall twice - each one needs to be stocked with one set of items.</li></ul>",
  },
  { text = "Build-on Tent plot" },
  { text = "Talk to the wise monkeys." },
  {
    text = "Talk to them again.",
    actions = { Action.ConversationHighlight:new("Right, I'll get on with it.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head back to Ape Atoll and find the building with the monkey knife fighters; it is located just east of the building with the Monkey Child.",
  },
  {
    text = "Punch and then quickly pickpocket the monkey fighters for 6 Monkey knives. The success rate is fairly low, so be prepared to punch at least 30 monkeys.<ul><li>If you are fast enough, you can get three pickpockets until the monkey wakes up again</li></ul>",
  },
  { text = "Return to the three wise monkeys and hand the knives over." },
  { text = "Talk to the monkeys again." },
  {
    text = "Head back to Ape Atoll and find the building with the monkey knife fighters; it is located just east of the building with the Monkey Child.",
    title = "Arming the colony",
    neededItems = {
      ["Gorilla greegree"] = { quantity = 1 },
      ["Ninja monkey greegree (small)"] = { quantity = 1 },
      ["Monkeyspeak amulet"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Punch and then quickly pickpocket the monkey fighters for 6 Monkey knives. The success rate is fairly low, so be prepared to punch at least 30 monkeys.<ul><li>If you are fast enough, you can get three pickpockets until the monkey wakes up again</li></ul>",
  },
  { text = "Return to the three wise monkeys and hand the knives over." },
  { text = "Talk to the monkeys again." },
  {
    text = "Return to the pyramid south of the Ruins of Uzer, east of DLQ, and click the door for a cutscene.",
    title = "Boss fights",
    neededItems = { ["Ghostspeak amulet"] = { quantity = 1 }, ["Monkeyspeak amulet"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Enter the pyramid and speak to Senliten.",
    actions = {
      Action.ConversationHighlight:new("Talk about Do No Evil."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Teleport to Bandit Camp and head back to the colony." },
  { text = "Investigate any Corpse to make a ghost monkey appear." },
  { text = "Try to talk to the ghost monkey." },
  { text = "Use one of the amulets on the other to create a cramulet." },
  {
    text = "Talk to the ghost monkey.",
    actions = { Action.ConversationHighlight:new("I must try to rescue them.") },
    postconditions = {
      Condition.ConversationText:new(
        " The one who took Iwazaru is a dangerous foe. Do not fight him from afar, embrace his snapping teeth and deadly grip."
      ),
    },
  },
  {
    text = "Climb down the well in the middle of Pollnivneach, pass through the Wavering mystic barrier to the north .",
    title = "Leeuni",
    actions = { Action.ConversationHighlight:new("You're going to die here.") },
    postconditions = {
      Condition.ConversationText:new(
        " Foolish mortal! I was made from the lips of a goddess. I shall tear off your limbs and lick the skin from your face."
      ),
    },
  },
  { text = "Kill Leeuni (50,000 life points)." },
  { text = "After defeating Leeuni, talk to Iwazaru." },
  { text = "Return to the monkey colony and speak to the ghost monkey again to unlock the next boss." },
  {
    text = "Kill Ayuni (50,000 life points), who is accessed through a tunnel north of the Kalphite Queen within the Kalphite Hive (not the Exiled Kalphite Hive).<ul><li>The Kalphite Queen can be avoided if you set up a custom encounter, as her spawn will be delayed initially. Use practice mode to avoid paying.</li></ul>",
    title = "Ayuni",
  },
  {
    text = "After the battle, talk to Mizaru. Leave through the east exit to unlock a shortcut or get transported .",
    actions = { Action.ConversationHighlight:new("Return to the colony.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Return to the monkey colony and speak to the ghost monkey again to unlock the next boss." },
  {
    text = "<b>Warning!</b> Eruni hits much harder than the other two. Although she has lower defence. Be prepared for an intense fight. Protection prayers and high-healing food strongly recommended.",
    title = "Eruni",
  },
  { text = "Head to the ruins of Uzer, down the steps and through the door to the north into the demonic realm." },
  { text = "Eruni will be invincible whenever she summons lesser demons; they must be killed immediately." },
  { text = "After defeating Eruni, speak to Kikazaru, who will take you to the colony." },
  {
    text = "Speak to the three wise monkeys at the colony - Apmeken will appear and speak to you.",
    title = "Finishing up",
    actions = { Action.ConversationHighlight:new("And what of Apmeken?") },
    postconditions = { Condition.ConversationText:new(" And what of Apmeken?") },
  },
  {
    text = "Return to Senliten.",
    actions = { Action.ConversationHighlight:new("Talk about Do No Evil.") },
    postconditions = {
      Condition.ConversationText:new(
        " Apmeken is only the beginning. In restoring her, you have done the desert a great service, but there is still more to be done. Amascut's deeds, I fear, are widespread. No doubt her actions have bred more evil."
      ),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Do No Evil",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1291161600,
  prereqQuests = {
    "Animal Magnetism",
    "Shadow of the Storm",
    "Desert Treasure",
    "Smoking Kills",
    "Missing My Mummy",
    "Dealing with Scabaras",
    "Recipe for Disaster: Freeing King Awowogei",
    "Senliten",
    "Leela",
    "Missing My Mummy",
  },
})
