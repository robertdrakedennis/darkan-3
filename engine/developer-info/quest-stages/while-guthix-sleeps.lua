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
    text = "Talk to Radimus Erkle in the Legends' Guild (fairy ring BLR).",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Yes, I'm interested, where do I report?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "In Taverley, talk to Ivy Sophista in the house west of the summoning obelisk.",
    actions = {
      Action.ConversationHighlight:new("Ask about 'While Guthix Sleeps'."),
      Action.ConversationHighlight:new("Our friend in common places great faith in totems."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Good, I'm glad you've arrived. Meet Thaerisk upstairs, there is much to discuss and Lucien's spies are everywhere."
      ),
    },
  },
  { text = "Go upstairs and talk to Thaerisk Cemphier. Kill both assassins that appear." },
  {
    text = "Talk to Thaerisk again.",
    actions = {
      Action.ConversationHighlight:new("What efforts are you making against Lucien?"),
      Action.ConversationHighlight:new("What are our options?"),
      Action.ConversationHighlight:new("I know about the 'Fist of Guthix'."),
      Action.ConversationHighlight:new("What do you need me to do?"),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Talk to Reldo in Varrock Palace library and 'Talk about Lucien and Movario'<ul><li>If Desperate Times is completed, you need to choose one more chat option before proceed the aforementioned quest dialogue.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about Lucien and Movario."),
      Action.ConversationHighlight:new("What can you tell me about Movario?"),
      Action.ConversationHighlight:new("What does Movario do?"),
      Action.ConversationHighlight:new("Where does Movario live?"),
      Action.ConversationHighlight:new("Okay, thanks."),
      Action.ConversationHighlight:new("[Talk to Reldo.]"),
    },
  },
  {
    text = "Talk to the Hunting expert in Feldip Hunter area and ask about tracking down a person.  She is north of Oo'glog lodestone, or south-west of fairy ring AKS. After the conversation she will give you a mort myre fungus.",
    title = "Finding Movario's base",
    neededItems = { ["Logs"] = { quantity = 1 }, ["Ring of charos (a)"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("A creature to help track down a person."),
      Action.ConversationHighlight:new("How can I catch a wild broav?"),
      Action.ConversationHighlight:new("What do I do once I've caught a broav?"),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Go west and trap pit (which builds a pitfall trap) at the square trap pit (uses one set of logs), then use your mort myre fungus on the built trap.",
  },
  { text = "Dismantle the collapsed trap to obtain an unconscious broav." },
  {
    text = "Talk to the Hunting expert again about tracking a person.",
    actions = { Action.ConversationHighlight:new("A creature to help track down a person.") },
    postconditions = { Condition.ConversationText:new(" Okay, I'll key it to you.") },
  },
  {
    text = "Head to the west of Fight Arena and into the laundry room two houses north of the pub. (Kandarin monastery Teleport then run south, or Watchtower Teleport and run north are quick ways to get there.)",
  },
  {
    text = "With your ring of charos (a) equipped, talk to the Khazard launderer to obtain dirty laundry.<ul><li>If you brought coins instead, take them out of your coin pouch and use them on the launderer.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Do you know someone called Movario?"),
      Action.ConversationHighlight:new("(Charm) Tell me all you know about Movario."),
      Action.ConversationHighlight:new("No, I need information. Do you know someone called Movario?"),
      Action.ConversationHighlight:new("Offer five thousand coins for information on Movario."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Not exactly the information I was interested in... Oh, hang on, wait...does that mean you have some of his clothes in to be washed?"
      ),
    },
  },
  { text = "Go to the Khazard Battleground north-west (north of Tree Gnome Village and across the bridges)." },
  { text = "Climb-over the crumbled wall inside the north-westernmost building." },
  { text = "Drop your Broav in the room with the broken table then use the dirty laundry on him." },
  { text = "Search the broken table to reveal a trapdoor, then go down." },
  { text = "Go through the door, and follow the path to the north, then climb-down the stairs." },
  {
    text = "Search the old battered door and find the correct rune to use on it. After you enter the correct rune, you'll hear a click.",
  },
  {
    text = "Search the door again (boost your Thieving level if needed), and you will find a trap to disarm.",
    actions = { Action.ConversationHighlight:new("Yes, I'm an awesome thief, I'll disable it easily!") },
    postconditions = { Condition.ConversationText:new("(If unsuccessful:)(Transcript missing. edit)") },
  },
  { text = "Go through the old battered door." },
  {
    text = "Search the painting on the east wall .",
    actions = { Action.ConversationHighlight:new("Yes, I'll flip the catch and see what happens.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Choose the Search option on the bookcases until you receive the chat response  CLICK!." },
  {
    text = "Follow the wire that faintly glows to the next bookshelf. (for those with colorblindness, zoom the camera in to see the wire slightly pulsate)<ul><li>Search that bookshelf.</li><li>Repeat, searching each bookshelf until the electricity on the gate disappears.</li></ul>",
  },
  { text = "Open the gate and search the spiral staircase to disarm the trap, then climb-up." },
  { text = "Search the desk to obtain Movario's notes (volume 1)<ul><li>Read the notes.</li></ul>" },
  {
    text = "Pick-up the waste-paper basket.<ul><li>With Movario's notes in your backpack, search the basket.</li></ul>",
  },
  {
    text = "Search the bookcase to the west of the waste-paper basket.<ul><li>Use the ruby key on the bookcase.</li><li>Climb-up the stairs just north of you.</li></ul>",
  },
  {
    text = "Search the bed.<ul><li>Use the ruby key on the bed chest.</li><li>Search the bed chest to disarm the traps</li><li>Open and search it.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, I'll attempt it.") },
    postconditions = { Condition.ConversationText:new("(If successful:) You disarm the trap.") },
  },
  { text = "Climb back down twice to the wire room and cross-over the broken wall, then search the drawers." },
  {
    text = "Pick-up your Broav and look-at the thermometer. Note down the number it says. Check your current character's weight in your equipment interface. You must equalise both numbers. If you weigh more than the number: subtract the number from the thermometer from your character's weight to get the result number. For example: if the thermometer says 20 and you weigh 30 kg, you would need 10 kg. If you weigh less than the number: subtract your character's weight from the number from to get the result number.<ul><li>Pick up your Broav if you haven't already, otherwise it will mess with the numbers.</li></ul>",
  },
  {
    text = "On the floor of this room is a Pile of weights. Take weights equalling the result number you got. CAUTION: You will not be able to recover any items dropped in this room.",
  },
  {
    text = "Climb back up the spiral staircase. If you weigh more than the number: use all the weights on the statue just north of you, then open the door just east. If you weigh less than the number: keep the weights in your inventory and open the door east of the statue.",
  },
  { text = "Head east through the door and up the ladder to leave the hideout." },
  {
    text = "Talk to the Hunting expert in Feldip Hunter area and ask about tracking down a person.  She is north of Oo'glog lodestone, or south-west of fairy ring AKS. After the conversation she will give you a mort myre fungus.",
    actions = {
      Action.ConversationHighlight:new("A creature to help track down a person."),
      Action.ConversationHighlight:new("How can I catch a wild broav?"),
      Action.ConversationHighlight:new("What do I do once I've caught a broav?"),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Go west and trap pit (which builds a pitfall trap) at the square trap pit (uses one set of logs), then use your mort myre fungus on the built trap.",
  },
  { text = "Dismantle the collapsed trap to obtain an unconscious broav." },
  {
    text = "Talk to the Hunting expert again about tracking a person.",
    actions = { Action.ConversationHighlight:new("A creature to help track down a person.") },
    postconditions = { Condition.ConversationText:new(" Okay, I'll key it to you.") },
  },
  {
    text = "Head to the west of Fight Arena and into the laundry room two houses north of the pub. (Kandarin monastery Teleport then run south, or Watchtower Teleport and run north are quick ways to get there.)",
  },
  {
    text = "With your ring of charos (a) equipped, talk to the Khazard launderer to obtain dirty laundry.<ul><li>If you brought coins instead, take them out of your coin pouch and use them on the launderer.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Do you know someone called Movario?"),
      Action.ConversationHighlight:new("(Charm) Tell me all you know about Movario."),
      Action.ConversationHighlight:new("No, I need information. Do you know someone called Movario?"),
      Action.ConversationHighlight:new("Offer five thousand coins for information on Movario."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Not exactly the information I was interested in... Oh, hang on, wait...does that mean you have some of his clothes in to be washed?"
      ),
    },
  },
  { text = "Go to the Khazard Battleground north-west (north of Tree Gnome Village and across the bridges)." },
  { text = "Climb-over the crumbled wall inside the north-westernmost building." },
  { text = "Drop your Broav in the room with the broken table then use the dirty laundry on him." },
  { text = "Search the broken table to reveal a trapdoor, then go down." },
  { text = "Go through the door, and follow the path to the north, then climb-down the stairs." },
  {
    text = "Search the old battered door and find the correct rune to use on it. After you enter the correct rune, you'll hear a click.",
  },
  {
    text = "Search the door again (boost your Thieving level if needed), and you will find a trap to disarm.",
    actions = { Action.ConversationHighlight:new("Yes, I'm an awesome thief, I'll disable it easily!") },
    postconditions = { Condition.ConversationText:new("(If unsuccessful:)(Transcript missing. edit)") },
  },
  { text = "Go through the old battered door." },
  {
    text = "Search the painting on the east wall .",
    actions = { Action.ConversationHighlight:new("Yes, I'll flip the catch and see what happens.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Choose the Search option on the bookcases until you receive the chat response  CLICK!." },
  {
    text = "Follow the wire that faintly glows to the next bookshelf. (for those with colorblindness, zoom the camera in to see the wire slightly pulsate)<ul><li>Search that bookshelf.</li><li>Repeat, searching each bookshelf until the electricity on the gate disappears.</li></ul>",
  },
  { text = "Open the gate and search the spiral staircase to disarm the trap, then climb-up." },
  { text = "Search the desk to obtain Movario's notes (volume 1)<ul><li>Read the notes.</li></ul>" },
  {
    text = "Pick-up the waste-paper basket.<ul><li>With Movario's notes in your backpack, search the basket.</li></ul>",
  },
  {
    text = "Search the bookcase to the west of the waste-paper basket.<ul><li>Use the ruby key on the bookcase.</li><li>Climb-up the stairs just north of you.</li></ul>",
  },
  {
    text = "Search the bed.<ul><li>Use the ruby key on the bed chest.</li><li>Search the bed chest to disarm the traps</li><li>Open and search it.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, I'll attempt it.") },
    postconditions = { Condition.ConversationText:new("(If successful:) You disarm the trap.") },
  },
  { text = "Climb back down twice to the wire room and cross-over the broken wall, then search the drawers." },
  {
    text = "Pick-up your Broav and look-at the thermometer. Note down the number it says. Check your current character's weight in your equipment interface. You must equalise both numbers. If you weigh more than the number: subtract the number from the thermometer from your character's weight to get the result number. For example: if the thermometer says 20 and you weigh 30 kg, you would need 10 kg. If you weigh less than the number: subtract your character's weight from the number from to get the result number.<ul><li>Pick up your Broav if you haven't already, otherwise it will mess with the numbers.</li></ul>",
  },
  {
    text = "On the floor of this room is a Pile of weights. Take weights equalling the result number you got. CAUTION: You will not be able to recover any items dropped in this room.",
  },
  {
    text = "Climb back up the spiral staircase. If you weigh more than the number: use all the weights on the statue just north of you, then open the door just east. If you weigh less than the number: keep the weights in your inventory and open the door east of the statue.",
  },
  { text = "Head east through the door and up the ladder to leave the hideout." },
  {
    text = "Head to the west of Fight Arena and into the laundry room two houses north of the pub. (Kandarin monastery Teleport then run south, or Watchtower Teleport and run north are quick ways to get there.)",
  },
  {
    text = "With your ring of charos (a) equipped, talk to the Khazard launderer to obtain dirty laundry.<ul><li>If you brought coins instead, take them out of your coin pouch and use them on the launderer.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Do you know someone called Movario?"),
      Action.ConversationHighlight:new("(Charm) Tell me all you know about Movario."),
      Action.ConversationHighlight:new("No, I need information. Do you know someone called Movario?"),
      Action.ConversationHighlight:new("Offer five thousand coins for information on Movario."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Not exactly the information I was interested in... Oh, hang on, wait...does that mean you have some of his clothes in to be washed?"
      ),
    },
  },
  { text = "Go to the Khazard Battleground north-west (north of Tree Gnome Village and across the bridges)." },
  { text = "Climb-over the crumbled wall inside the north-westernmost building." },
  { text = "Drop your Broav in the room with the broken table then use the dirty laundry on him." },
  { text = "Search the broken table to reveal a trapdoor, then go down." },
  { text = "Go through the door, and follow the path to the north, then climb-down the stairs." },
  {
    text = "Search the old battered door and find the correct rune to use on it. After you enter the correct rune, you'll hear a click.",
  },
  {
    text = "Search the door again (boost your Thieving level if needed), and you will find a trap to disarm.",
    actions = { Action.ConversationHighlight:new("Yes, I'm an awesome thief, I'll disable it easily!") },
    postconditions = { Condition.ConversationText:new("(If unsuccessful:)(Transcript missing. edit)") },
  },
  { text = "Go through the old battered door." },
  {
    text = "Search the painting on the east wall .",
    actions = { Action.ConversationHighlight:new("Yes, I'll flip the catch and see what happens.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Choose the Search option on the bookcases until you receive the chat response  CLICK!." },
  {
    text = "Follow the wire that faintly glows to the next bookshelf. (for those with colorblindness, zoom the camera in to see the wire slightly pulsate)<ul><li>Search that bookshelf.</li><li>Repeat, searching each bookshelf until the electricity on the gate disappears.</li></ul>",
  },
  { text = "Open the gate and search the spiral staircase to disarm the trap, then climb-up." },
  { text = "Search the desk to obtain Movario's notes (volume 1)<ul><li>Read the notes.</li></ul>" },
  {
    text = "Pick-up the waste-paper basket.<ul><li>With Movario's notes in your backpack, search the basket.</li></ul>",
  },
  {
    text = "Search the bookcase to the west of the waste-paper basket.<ul><li>Use the ruby key on the bookcase.</li><li>Climb-up the stairs just north of you.</li></ul>",
  },
  {
    text = "Search the bed.<ul><li>Use the ruby key on the bed chest.</li><li>Search the bed chest to disarm the traps</li><li>Open and search it.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, I'll attempt it.") },
    postconditions = { Condition.ConversationText:new("(If successful:) You disarm the trap.") },
  },
  { text = "Climb back down twice to the wire room and cross-over the broken wall, then search the drawers." },
  {
    text = "Pick-up your Broav and look-at the thermometer. Note down the number it says. Check your current character's weight in your equipment interface. You must equalise both numbers. If you weigh more than the number: subtract the number from the thermometer from your character's weight to get the result number. For example: if the thermometer says 20 and you weigh 30 kg, you would need 10 kg. If you weigh less than the number: subtract your character's weight from the number from to get the result number.<ul><li>Pick up your Broav if you haven't already, otherwise it will mess with the numbers.</li></ul>",
  },
  {
    text = "On the floor of this room is a Pile of weights. Take weights equalling the result number you got. CAUTION: You will not be able to recover any items dropped in this room.",
  },
  {
    text = "Climb back up the spiral staircase. If you weigh more than the number: use all the weights on the statue just north of you, then open the door just east. If you weigh less than the number: keep the weights in your inventory and open the door east of the statue.",
  },
  { text = "Head east through the door and up the ladder to leave the hideout." },
  { text = "Go through the door, and follow the path to the north, then climb-down the stairs." },
  {
    text = "Search the old battered door and find the correct rune to use on it. After you enter the correct rune, you'll hear a click.",
  },
  {
    text = "Search the door again (boost your Thieving level if needed), and you will find a trap to disarm.",
    actions = { Action.ConversationHighlight:new("Yes, I'm an awesome thief, I'll disable it easily!") },
    postconditions = { Condition.ConversationText:new("(If unsuccessful:)(Transcript missing. edit)") },
  },
  { text = "Go through the old battered door." },
  {
    text = "Search the painting on the east wall .",
    actions = { Action.ConversationHighlight:new("Yes, I'll flip the catch and see what happens.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Choose the Search option on the bookcases until you receive the chat response  CLICK!." },
  {
    text = "Follow the wire that faintly glows to the next bookshelf. (for those with colorblindness, zoom the camera in to see the wire slightly pulsate)<ul><li>Search that bookshelf.</li><li>Repeat, searching each bookshelf until the electricity on the gate disappears.</li></ul>",
  },
  { text = "Open the gate and search the spiral staircase to disarm the trap, then climb-up." },
  { text = "Search the desk to obtain Movario's notes (volume 1)<ul><li>Read the notes.</li></ul>" },
  {
    text = "Pick-up the waste-paper basket.<ul><li>With Movario's notes in your backpack, search the basket.</li></ul>",
  },
  {
    text = "Search the bookcase to the west of the waste-paper basket.<ul><li>Use the ruby key on the bookcase.</li><li>Climb-up the stairs just north of you.</li></ul>",
  },
  {
    text = "Search the bed.<ul><li>Use the ruby key on the bed chest.</li><li>Search the bed chest to disarm the traps</li><li>Open and search it.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, I'll attempt it.") },
    postconditions = { Condition.ConversationText:new("(If successful:) You disarm the trap.") },
  },
  { text = "Climb back down twice to the wire room and cross-over the broken wall, then search the drawers." },
  {
    text = "Pick-up your Broav and look-at the thermometer. Note down the number it says. Check your current character's weight in your equipment interface. You must equalise both numbers. If you weigh more than the number: subtract the number from the thermometer from your character's weight to get the result number. For example: if the thermometer says 20 and you weigh 30 kg, you would need 10 kg. If you weigh less than the number: subtract your character's weight from the number from to get the result number.<ul><li>Pick up your Broav if you haven't already, otherwise it will mess with the numbers.</li></ul>",
  },
  {
    text = "On the floor of this room is a Pile of weights. Take weights equalling the result number you got. CAUTION: You will not be able to recover any items dropped in this room.",
  },
  {
    text = "Climb back up the spiral staircase. If you weigh more than the number: use all the weights on the statue just north of you, then open the door just east. If you weigh less than the number: keep the weights in your inventory and open the door east of the statue.",
  },
  { text = "Head east through the door and up the ladder to leave the hideout." },
  {
    text = "Search the painting on the east wall .",
    actions = { Action.ConversationHighlight:new("Yes, I'll flip the catch and see what happens.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Choose the Search option on the bookcases until you receive the chat response  CLICK!." },
  {
    text = "Follow the wire that faintly glows to the next bookshelf. (for those with colorblindness, zoom the camera in to see the wire slightly pulsate)<ul><li>Search that bookshelf.</li><li>Repeat, searching each bookshelf until the electricity on the gate disappears.</li></ul>",
  },
  { text = "Open the gate and search the spiral staircase to disarm the trap, then climb-up." },
  { text = "Search the desk to obtain Movario's notes (volume 1)<ul><li>Read the notes.</li></ul>" },
  {
    text = "Pick-up the waste-paper basket.<ul><li>With Movario's notes in your backpack, search the basket.</li></ul>",
  },
  {
    text = "Search the bookcase to the west of the waste-paper basket.<ul><li>Use the ruby key on the bookcase.</li><li>Climb-up the stairs just north of you.</li></ul>",
  },
  {
    text = "Search the bed.<ul><li>Use the ruby key on the bed chest.</li><li>Search the bed chest to disarm the traps</li><li>Open and search it.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, I'll attempt it.") },
    postconditions = { Condition.ConversationText:new("(If successful:) You disarm the trap.") },
  },
  { text = "Climb back down twice to the wire room and cross-over the broken wall, then search the drawers." },
  {
    text = "Pick-up your Broav and look-at the thermometer. Note down the number it says. Check your current character's weight in your equipment interface. You must equalise both numbers. If you weigh more than the number: subtract the number from the thermometer from your character's weight to get the result number. For example: if the thermometer says 20 and you weigh 30 kg, you would need 10 kg. If you weigh less than the number: subtract your character's weight from the number from to get the result number.<ul><li>Pick up your Broav if you haven't already, otherwise it will mess with the numbers.</li></ul>",
  },
  {
    text = "On the floor of this room is a Pile of weights. Take weights equalling the result number you got. CAUTION: You will not be able to recover any items dropped in this room.",
  },
  {
    text = "Climb back up the spiral staircase. If you weigh more than the number: use all the weights on the statue just north of you, then open the door just east. If you weigh less than the number: keep the weights in your inventory and open the door east of the statue.",
  },
  { text = "Head east through the door and up the ladder to leave the hideout." },
  {
    text = "Return to Thaerisk Cemphier in Taverley with the items obtained in Movario's base.",
    title = "Catching Lucien's spy",
    neededItems = {
      ["Strange key loop"] = { quantity = 1 },
      ["Strange key teeth"] = { quantity = 1 },
      ["Movario's notes (volume 1)"] = { quantity = 1 },
      ["Movario's notes (volume 2)"] = { quantity = 1 },
      ["Mercenary axeman"] = { quantity = 1 },
      ["Mercenary mage"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("What should I do now?"),
      Action.ConversationHighlight:new("Sorry, I have to go."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head to the building in McGrubor's Wood (Seers' Village lodestone or fairy ring ALS), and a cutscene will start. Kill all the mercenaries.",
  },
  { text = "Search the dead mercenaries and talk to the wounded Guardian of Armadyl." },
  {
    text = "Go upstairs in the Seers' Village pub and talk to Idria.",
    actions = {
      Action.ConversationHighlight:new("Do you know where Lucien is?"),
      Action.ConversationHighlight:new("How can I get to Lucien?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " We've been unable to track down his base of operations. So we're going to have to be more aggressive - we'll work our way up the food chain."
      ),
    },
  },
  {
    text = "Talk to Sir Tiffy Cashien in the Falador Park.  or  if the Slug Menace hasn't been completed.",
    actions = { Action.ConversationHighlight:new("While Guthix Sleeps.") },
    postconditions = { Condition.ConversationText:new(" What, pray tell, can my expertise assist with?") },
  },
  { text = "Go to the White Knights' Castle." },
  {
    text = "In the eastern room on the ground floor[UK]1st floor[US], talk to Akrisae.",
    actions = {
      Action.ConversationHighlight:new("How can I get to Draynor?"),
      Action.ConversationHighlight:new("Yes, please!"),
    },
    postconditions = { Condition.ConversationText:new("You hand over 20 gold pieces in return for the dye") },
  },
  {
    text = "At Draynor Village, use the teleorb on the shady stranger wearing white robes and near the shore. This may take several attempts.",
  },
  { text = "Talk to Akrisae in the White Knight's Castle. Cutscene plays." },
  {
    text = "If you don't have a snapdragon seed for the next step, buy one from Thaerisk.",
    actions = {
      Action.ConversationHighlight:new("Could you help me get a snapdragon seed?"),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new(" You manage to lever the rock away from the brazier.") },
  },
  {
    text = "Talk to Thaerisk to teleport to Port Sarim.",
    actions = { Action.ConversationHighlight:new("Can you teleport me to Port Sarim, please?") },
    postconditions = { Condition.ConversationText:new(" Very well, prepare yourself.") },
  },
  {
    text = "Talk to Betty in the Port Sarim magic shop for truth serum and pink dye.  (If The Hand In The Sand quest is completed )",
    title = "Super truth serum and recruiting the Heroes",
    neededItems = {
      ["Lantern lens"] = { quantity = 1 },
      ["Snapdragon seed"] = { quantity = 1 },
      ["Coins"] = { quantity = 1 },
      ["Air rune"] = { quantity = 1 },
      ["Cosmic rune"] = { quantity = 1 },
      ["Astral rune"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Talk to Betty about While Guthix Sleeps."),
      Action.ConversationHighlight:new("Yes, please can I have a standard truth serum?"),
      Action.ConversationHighlight:new("Yes, please!"),
      Action.ConversationHighlight:new("Talk to Betty about While Guthix Sleeps."),
      Action.ConversationHighlight:new("Yes, please can I have a standard truth serum?"),
      Action.ConversationHighlight:new("Yes, please!"),
    },
    postconditions = { Condition.ConversationText:new("You hand over 20 gold pieces in return for the dye") },
  },
  {
    text = "If the snapdragon seed isn't already on the counter, talk to her again until she places the seed on the counter.",
  },
  { text = "Use the pink dye on your lantern lens." },
  { text = "Stand just inside the doorway, use the lens with the counter and take the enriched snapdragon seed." },
  { text = "If not already, switch to the Lunar spellbook." },
  {
    text = "Using the most western staircase in White Knight Castle, head to the top of the castle and plant the seed in the herb patch.",
  },
  {
    text = "Go back downstairs and talk to Thaerisk on the ground floor of the eastern part of the castle.",
    actions = { Action.ConversationHighlight:new("I've planted the enriched snapdragon seed.") },
    postconditions = { Condition.ConversationText:new(" Very good Player!") },
  },
  {
    text = "Cast NPC Contact to talk to the following people:<ul><li>Turael</li><li>Duradel</li><li>Mazchna</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you about Lucien."),
      Action.ConversationHighlight:new("I need to talk to you about Lucien."),
      Action.ConversationHighlight:new("I need to talk to you about Lucien."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Ah, that troublesome Mahjarrat! I've heard rumours of his evil. What is he conspiring to do now?"
      ),
    },
  },
  { text = "Talk to Hazelmere upstairs in the hut east of Yanille near fairy ring CLS." },
  {
    text = "Pick the enriched snapdragon (top of west tower at White Knights' Castle) and mix it into your truth serum for a super truth serum.",
    title = "Questioning the spy",
    neededItems = {
      ["Truth serum"] = { quantity = 1 },
      ["Astral rune"] = { quantity = 1 },
      ["Cosmic rune"] = { quantity = 1 },
      ["Air rune"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Head back to the room with Thaerisk and the others." },
  { text = "Open and search the drawers to find charcoal and papyrus." },
  {
    text = "Go into the cell and use the serum on the shady stranger.  The correct chat option varies from player to player. Exhaust chat options until you reach the correct one so that the stranger drinks the serum.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to the shady stranger again." },
  { text = "Talk to Idria." },
  {
    text = "Cast NPC Contact to talk to Cyrisus.",
    actions = { Action.ConversationHighlight:new("I need to talk to you about Lucien.") },
    postconditions = {
      Condition.ConversationText:new(
        " Ah, that troublesome Mahjarrat! I've heard rumours of his evil. What is he conspiring to do now?"
      ),
    },
  },
  {
    text = "Head to the Warriors' Guild and talk to the following:<ul><li>Ghommal just outside</li><li>Harrallak Menarous in the main lobby</li><li>Sloane upstairs</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you about Lucien!"),
      Action.ConversationHighlight:new("Recruit Sloane for the hero party."),
    },
    postconditions = {
      Condition.ConversationText:new(" Lucien? I've heard the name, and not good things along with it."),
    },
  },
  { text = "Return to the White Knights' Castle and speak to Idria, then Akrisae." },
  {
    text = "Switch back to the standard spellbook.",
    title = "Into the Black Knights' Fortress",
    neededItems = {
      ["Unpowered orb"] = { quantity = 1 },
      ["Charge Orb"] = { quantity = 1 },
      ["Skull of Remembrance"] = { quantity = 1 },
      ["Bronze med helm"] = { quantity = 1 },
      ["Iron chainbody"] = { quantity = 1 },
      ["Black armour set"] = { quantity = 1 },
      ["Black Knight captain's armour"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "You will need 10 free inventory slots to proceed." },
  {
    text = "Equip your disguise and head inside Black Knights' Fortress with all the items above or use the skull of remembrance to teleport to the ritual chamber, and make your way down to the ground floor[UK]1st floor[US].",
  },
  { text = "Climb down the ladder in the south-west corner." },
  { text = "Along the east wall, right-click search the floor tile. Cast your charge orb spell on the floor tile." },
  { text = "Climb-down the trapdoor." },
  {
    text = "Kill Elite Black Knights to obtain a helmet, platebody and platelegs if you don't have a set already. The three pieces of armor are gaurenteed to drop until 3 pieces are received. Do not use multi-target abilities as killing two Elite Black Knights at once will cause them both to drop the same piece, with both counting toward the first 3 pieces received. Equip the full set to remove their aggression.",
  },
  { text = "Head north by jumping across the broken bridge, go east, and climb up the wall." },
  { text = "Jump over the barricade and walk north-west until you reach a fork in the path, then head east." },
  {
    text = "Talk to Silif in the northmost cell.",
    actions = {
      Action.ConversationHighlight:new("Silif, it's me, [Name]. Are you okay?"),
      Action.ConversationHighlight:new("I want to get you out of here. Any suggestions?"),
      Action.ConversationHighlight:new("I'll be back to try to get you out."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Go back west and enter the northern centre Solid black door." },
  {
    text = "Search all desks and wardrobes in the room for:<ul><li>Another set of elite black armour</li><li>Full set of Dagon'hai robes</li><li>Restore potion (4) (Do not drink the restore potion)</li><li>Strange teleorb</li><li>Lobster (Do not eat the lobster)</li><li>If you accidentally eat the lobster, any kind of cooked food will work as well.</li><li>If you accidentally eat the lobster, any kind of cooked food will work as well.</li></ul>",
  },
  { text = "Right-click search the key rack on the northern wall (a few tiles east of the wardrobe) for a Cell key." },
  {
    text = "Enter Silif's cell. Use the piece of food on Silif, then the potion.",
    actions = { Action.ConversationHighlight:new("Do you have a plan?") },
    postconditions = {
      Condition.ConversationText:new(
        " Yes, we should try to get into Dark Squall's inner base. I believe I can memorise the maps and plans in there and duplicate them when I return to Falador."
      ),
    },
  },
  { text = "Give Silif a set of elite black armour (dismiss your familiar if you have one)." },
  { text = "Return to the northern room. Walk to the map board on the southern wall then talk to Silif." },
  { text = "Plant the teleorb on Dark Squall, who is located in the east of the room, then escape back to Falador." },
  {
    text = "Talk to Akrisae.",
    actions = {
      Action.ConversationHighlight:new("What would this orb look like?"),
      Action.ConversationHighlight:new("How will I be able to get to Lucien's base?"),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Wear your Dagon'hai robes and enter the nearby jail cell." },
  {
    text = "Climb up the ladder in the north-eastern corner, stand in the stone circle in the middle of the room, and right-click activate the strange teleorb. (Note: Activation will fail if the required runes are in a pouch.)",
  },
  {
    text = "Follow the north-eastern path towards the chapel and climb-up the ice wall. There is a stick in the ice wall to indicate where to climb.",
  },
  { text = "Jump-to the ledge on the chapel into the Wilderness." },
  { text = "Walk east to activate a cutscene." },
  { text = "Return to Falador after the cutscene." },
  {
    text = "Talk to Idria.<ul><li>If she won't talk about Movario and instead keeps saying to investigate Lucien's camp, then unfortunately the game glitched and you must go back and rewatch the cutscene. Remember to bring the strange teleorb, a law rune, and a death rune. A good indicator that the cutscene registered is to check for the 'You have unlocked a new music track: The Evil Within' game message.</li></ul>",
  },
  {
    text = "Talk to Akrisae.",
    title = "Lucien's Army",
    neededItems = {
      ["Strange teleorb"] = { quantity = 1 },
      ["Dagon'hai robe armour"] = { quantity = 1 },
      ["Law rune"] = { quantity = 1 },
      ["Death rune"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("What would this orb look like?"),
      Action.ConversationHighlight:new("How will I be able to get to Lucien's base?"),
      Action.ConversationHighlight:new("Okay, thanks."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Wear your Dagon'hai robes and enter the nearby jail cell." },
  {
    text = "Climb up the ladder in the north-eastern corner, stand in the stone circle in the middle of the room, and right-click activate the strange teleorb. (Note: Activation will fail if the required runes are in a pouch.)",
  },
  {
    text = "Follow the north-eastern path towards the chapel and climb-up the ice wall. There is a stick in the ice wall to indicate where to climb.",
  },
  { text = "Jump-to the ledge on the chapel into the Wilderness." },
  { text = "Walk east to activate a cutscene." },
  { text = "Return to Falador after the cutscene." },
  {
    text = "Talk to Idria.<ul><li>If she won't talk about Movario and instead keeps saying to investigate Lucien's camp, then unfortunately the game glitched and you must go back and rewatch the cutscene. Remember to bring the strange teleorb, a law rune, and a death rune. A good indicator that the cutscene registered is to check for the 'You have unlocked a new music track: The Evil Within' game message.</li></ul>",
  },
  {
    text = "Go to the Tears of Guthix cavern.",
    title = "Into the chasm",
    neededItems = {
      ["Sapphire lantern (lit)"] = { quantity = 1 },
      ["Dagon'hai robe armour"] = { quantity = 1 },
      ["Bloom"] = { quantity = 1 },
      ["Druid pouch"] = { quantity = 1 },
      ["Ouroboros pouch"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Equip your Dagon'hai robes and talk to Movario.",
    actions = {
      Action.ConversationHighlight:new("I am Surok, Lucien has sent me to check on your progress."),
      Action.ConversationHighlight:new("I could go down to the bottom first, to make sure it's safe!"),
      Action.ConversationHighlight:new("I shall return."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "With a sapphire lantern lit, right-click travel into cavern with a light creature." },
  {
    text = "Search the northernmost skeleton for a silver sickle (b) and 6 druid pouches.<ul><li>If you have an Ouroboros pouch or brought two druid pouches of your own, you can drop the silver sickle to save inventory space.</li></ul>",
  },
  {
    text = "Search the rocks  in the north-eastern corner. Then search on the brazier that appears, choosing the chisel option to receive a fire orb.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  { text = "Repeat on the north-west rock for the earth orb." },
  {
    text = "Repeat for the standing lit braziers for air and water orbs in the south-western and south-eastern corners.",
  },
  {
    text = "Head further south to reach three skull cavities on the southern wall. Examine each recessed block and use the respective orb on the block.",
  },
  { text = "Climb up the wall twice and use the water orb on the recessed block." },
  {
    text = "Jump down the same way you came up. For each of the three lower skulls, enter its nose cavity.<ul><li>Dismiss any followers and empty both your main and off-hand slots before entering the cavities.</li></ul>",
  },
  {
    text = "Crawl along the tunnel in each cavity to its end. Examine the door there and insert the respective element key into the door lock. Crawl back through the tunnel and exit the skull.",
  },
  {
    text = "Climb up the walls to the upper skull. Use the fire key on the skull's recessed block, then search the stone cube in front of the skull.",
  },
  { text = "Climb-through the cave opening to enter the Guthixian temple." },
  {
    text = "You will now need at least two free inventory slots, though more can make it quicker to proceed. Ensure that you have also deactivated any sign of the porter effects.",
  },
  {
    text = "There are eight statues in this temple. Each, when examined, will reveal what type of potion must be 'made' at that statue.",
  },
  {
    text = "Use your druid pouches on the druid spirits. Each will drop required ingredients for the statues once - if these are lost (e.g. by dropping and letting them timeout), you will have to get replacements yourself. Grace of the elves will send items to the bank so unequip it or disable its porter functionality.",
  },
  {
    text = "Do not let your druid pouch empty completely or it disappears! You need 3 open inventory slots to add (3) Flowers into the druid pouch.<ul><li>Use the Bloom spell on your Silver sickle (b) while standing on or close to the dead vine to grow some Vine flower to recharge your druid pouch.</li></ul>",
  },
  {
    text = "Use the herbs and the secondary ingredients on the various statues referencing the table below. Not all keywords will be on statues.<ul><li>If you have an ouroboros pouch, you do not need to worry about the druid pouch losing charges. However, you must carry a druid pouch in your inventory to 'use' it on the spirits, though it will never deplete.</li><li>If you need to refill your druid pouch, right-click bloom with your silver sickle near dead vines to get flowers. These can be used to refill the druid pouch. If you received a mort myre fungus from the druids, use it with the correct statue or drop it for a moment before filling the druid pouch else you will lose it!</li></ul>",
  },
  {
    text = "Use the dolmens you receive from the statues on the Stone Table in the middle of the temple. It will open the huge door in front of it.",
  },
  {
    text = "You will now need at least two free inventory slots, though more can make it quicker to proceed. Ensure that you have also deactivated any sign of the porter effects.",
  },
  {
    text = "There are eight statues in this temple. Each, when examined, will reveal what type of potion must be 'made' at that statue.",
  },
  {
    text = "Use your druid pouches on the druid spirits. Each will drop required ingredients for the statues once - if these are lost (e.g. by dropping and letting them timeout), you will have to get replacements yourself. Grace of the elves will send items to the bank so unequip it or disable its porter functionality.",
  },
  {
    text = "Do not let your druid pouch empty completely or it disappears! You need 3 open inventory slots to add (3) Flowers into the druid pouch.<ul><li>Use the Bloom spell on your Silver sickle (b) while standing on or close to the dead vine to grow some Vine flower to recharge your druid pouch.</li></ul>",
  },
  {
    text = "Use the herbs and the secondary ingredients on the various statues referencing the table below. Not all keywords will be on statues.<ul><li>If you have an ouroboros pouch, you do not need to worry about the druid pouch losing charges. However, you must carry a druid pouch in your inventory to 'use' it on the spirits, though it will never deplete.</li><li>If you need to refill your druid pouch, right-click bloom with your silver sickle near dead vines to get flowers. These can be used to refill the druid pouch. If you received a mort myre fungus from the druids, use it with the correct statue or drop it for a moment before filling the druid pouch else you will lose it!</li></ul>",
  },
  {
    text = "Use the dolmens you receive from the statues on the Stone Table in the middle of the temple. It will open the huge door in front of it.",
  },
  {
    text = "You will now need at least two free inventory slots, though more can make it quicker to proceed. Ensure that you have also deactivated any sign of the porter effects.",
  },
  {
    text = "There are eight statues in this temple. Each, when examined, will reveal what type of potion must be 'made' at that statue.",
  },
  {
    text = "Use your druid pouches on the druid spirits. Each will drop required ingredients for the statues once - if these are lost (e.g. by dropping and letting them timeout), you will have to get replacements yourself. Grace of the elves will send items to the bank so unequip it or disable its porter functionality.",
  },
  {
    text = "Do not let your druid pouch empty completely or it disappears! You need 3 open inventory slots to add (3) Flowers into the druid pouch.<ul><li>Use the Bloom spell on your Silver sickle (b) while standing on or close to the dead vine to grow some Vine flower to recharge your druid pouch.</li></ul>",
  },
  {
    text = "Use the herbs and the secondary ingredients on the various statues referencing the table below. Not all keywords will be on statues.<ul><li>If you have an ouroboros pouch, you do not need to worry about the druid pouch losing charges. However, you must carry a druid pouch in your inventory to 'use' it on the spirits, though it will never deplete.</li><li>If you need to refill your druid pouch, right-click bloom with your silver sickle near dead vines to get flowers. These can be used to refill the druid pouch. If you received a mort myre fungus from the druids, use it with the correct statue or drop it for a moment before filling the druid pouch else you will lose it!</li></ul>",
  },
  {
    text = "Use the dolmens you receive from the statues on the Stone Table in the middle of the temple. It will open the huge door in front of it.",
  },
  {
    text = "Make whatever arrangements necessary to battle the level 117 Balance Elemental that can use all three combat styles, and lowers your combat stats (except for Necromancy). You can leave the area to get supplies, but always bring your lit sapphire lantern. Don't forget to bring at least two combat styles for the tormented demon fight, unless using Necromancy. The Balance Elemental drains combat stats during the fight, so be sure to bring at least one full super restore.",
    title = "The Stone of Jas",
  },
  { text = "Proceed down the corridor and enter a large chamber." },
  {
    text = "Once ready, search the mysterious stone  and kill the Balance Elemental that appears. See the strategy guide here.",
    actions = { Action.ConversationHighlight:new("Yes, I need to investigate this, even if it kills me!") },
  },
  {
    text = "Search the mysterious stone again for a cutscene. Doing so boosts all your combat stats to level 255 except Prayer.",
  },
  {
    text = "Continue dialogue with Movario , then with Lucien.",
    actions = {
      Action.ConversationHighlight:new("What are you going to do with the Stone of Jas?"),
      Action.ConversationHighlight:new("What is the Stone of Jas?"),
      Action.ConversationHighlight:new("You can't let Lucien get the Stone of Jas!"),
      Action.ConversationHighlight:new("What if someone should stand in your way?"),
      Action.ConversationHighlight:new("What are you going to do with the Stone of Jas?"),
      Action.ConversationHighlight:new("Why is the Stone of Jas so important to you?"),
      Action.ConversationHighlight:new("Do you really hope to follow in the footsteps of Zamorak?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " My destiny is my concern! You needn't bother yourself, your days will end soon enough."
      ),
    },
  },
  { text = "Kill the two Tormented demons, switching between your two combat styles." },
  {
    text = "Talk to Idria to have her teleport you to Falador.",
    actions = { Action.ConversationHighlight:new("Yes, a teleport out of here would be good.") },
    postconditions = { Condition.ConversationText:new(" Arcanus Sempitor Teleportus Retainus.") },
  },
  { text = "Continue dialogue with Idria in Falador." },
  { text = "Quest complete!" },
  {
    text = "Optional: after using of all four of Idria's 100,000 exp boosts, talk to her again to view the 'Elsewhere...' epilogue.",
  },
}

return Quest:new({
  name = "While Guthix Sleeps",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.veryverylong,
  releaseDate = 1227657600,
  prereqQuests = {
    "Defender of Varrock",
    "Dream Mentor",
    "Shilo Village",
    "Temple of Ikov",
    "Legends' Quest",
    "The Path of Glouphrie",
    "Tears of Guthix (quest)",
    "Wanted!",
    "The Hunt for Surok (miniquest)",
    "Warriors' Guild",
    "Nature Spirit",
  },
})
