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
    text = "Travel to Mos Le'Harmless (fairy ring DIP if you have repaired the bridge with 8 planks and 16 nails and partial completion of A Fairy Tale III).",
    title = "Getting started",
  },
  {
    text = "Talk to Bill Teach in The Other Inn.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Speak about Rocking Out.") },
  },
  {
    text = "[Accept Quest]<ul><li>If you have lost your Little Book o' Piracy, talk to him to get a new one.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  { text = "Buy three pieces of pirate clothing from Dodgy Mike's Second-hand Clothing." },
  {
    text = "Go to house in south-east Rimmington with the Customs Sergeant while wearing three pieces of pirate clothing.",
    title = "Getting arrested",
    neededItems = { ["Pirate clothing"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Read the noticeboard next to the entrance door for information on the answers to the trick questions. The answers to the trick questions are different for everyone.",
  },
  {
    text = "Talk to the Customs Sergeant<ul><li>Taking note of the ship name/s he references, select the answers that match the story on noticeboard.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Any option."),
      Action.ConversationHighlight:new("Varies - match noticeboard."),
    },
  },
  {
    text = "Deposit all your items in the Locker on the north side of the room. You need to store all the items you are wearing as well. Deactivate all auras and dismiss any familiars.",
  },
  { text = "Talk to the Customs Sergeant again." },
  { text = "Once in the prison, shout-through the barred window to your east.", title = "In prison" },
  {
    text = "Pick up the tin cup in the cell and use it on the cell door 3 times until Heavy-Handed Harry throws stew on you, turning your prison uniform top into a fishy prison uniform top.",
  },
  { text = "Right-click tap the pipes on the eastern wall to receive a pipe." },
  { text = "Shout-through your cell door until you receive a quill, paper, and ink bottle." },
  { text = "Leave your cell through the hole to the north and search the rubble to receive an accordion (broken)." },
  { text = "Use the ink bottle on the paper to receive inky paper and ink bottle (empty)." },
  {
    text = "Use the ink bottle (empty) on the sharp rock outside on the east-most wall near the pier to get a smashed bottle.",
  },
  { text = "In order, use the smashed bottle, pipe, and inky paper on the accordion to get a vacuum pump." },
  { text = "Use your fishy prison uniform top on the perch rock just east of the hole in the wall." },
  { text = "Right-click use the vacuum pump on the perch rock once the seagull appears." },
  { text = "Right-click use the vacuum pump and gull on the barred window." },
  {
    text = "Dive off the pier on the east side of the island.",
    actions = { Action.ConversationHighlight:new("Dive") },
  },
  {
    text = "Travel to Braindeath Island by speaking to Pirate Pete north of Port Phasmatys.",
    title = "Captains' marks - Captain Braindeath",
    neededItems = {
      ["Bronze wire"] = { quantity = 1 },
      ["Pirate clothing"] = { quantity = 1 },
      ["Charcoal"] = { quantity = 1 },
      ["Diving apparatus"] = { quantity = 1 },
      ["Fishbowl helmet"] = { quantity = 1 },
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Coins"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Braindeath Island, please!") },
  },
  {
    text = "Go west and speak to Captain Braindeath. He will give you the fake beard, fake moustache and nose, fake monocle, moustache and nose, curly wig, and straight wig disguises.",
    actions = { Action.ConversationHighlight:new("Talk about Rocking Out") },
  },
  { text = "Travel to the bar in north Port Sarim called The Rusty Anchor." },
  { text = "Talk to the Bartender.", actions = { Action.ConversationHighlight:new("Ask about 'rum'.") } },
  {
    text = "Talk to the Bartender again.<ul><li>Repeat this about five times until he says he's tired.</li></ul>",
    actions = { Action.ConversationHighlight:new("I'll have a drink... and you have one too!") },
  },
  {
    text = "Go outside, put on a disguise, and then ask about the rum again. Repeat this step with each disguise.",
    actions = { Action.ConversationHighlight:new("Ask about 'rum'.") },
  },
  {
    text = "Talk to the bartender again, this time with no disguise, and he will give you an order slip.",
    actions = { Action.ConversationHighlight:new("Ask about 'rum'.") },
  },
  {
    text = "Speak to Redbeard Frank outside of The Rusty Anchor in Port Sarim.",
    title = "Redbeard Frank",
    actions = { Action.ConversationHighlight:new("Speak about Rocking Out.") },
  },
  {
    text = "Travel to Mos Le'Harmless via the charter boat, fairy ring DIP if you have repaired the bridge, or Bill Teach, and talk to Brother Tranquillity to receive a letter.",
    actions = { Action.ConversationHighlight:new("Talk about the letter for Frank.") },
  },
  {
    text = "Return to Redbeard Frank on the Port Sarim dock and deliver the letter.",
    actions = { Action.ConversationHighlight:new("Speak about Rocking Out.") },
  },
  { text = "Put on your pirate clothing and head back to Braindeath Island." },
  {
    text = "Talk to Captain Braindeath for his mark.",
    actions = { Action.ConversationHighlight:new("Talk about Rocking Out.") },
  },
  { text = "Go down the southern stairs, and talk to Captain Donnie to get plans." },
  {
    text = "Head north-west to 50% Luke by the bridge and use charcoal on him.<ul><li>There is charcoal in a crate on the other side of the gate near 50% Luke.</li></ul>",
  },
  {
    text = "Return to Redbeard Frank on the Port Sarim docks and exchange the Charcoal rubbing for Frank's mark.",
    actions = { Action.ConversationHighlight:new("Speak about Rocking Out.") },
  },
  {
    text = "Optional: Talk to the bartender and ask about 'rum' once more to complete the Rusty Reward achievement required for the master quest cape.",
    actions = { Action.ConversationHighlight:new("Ask about 'rum'.") },
  },
  { text = "Travel to Mos Le'Harmless.", title = "Bill Teach and Captain Brass Hand Harry" },
  {
    text = "Talk to Bill Teach in The Other Inn to obtain an introduction letter.",
    actions = { Action.ConversationHighlight:new("Speak about Rocking Out.") },
  },
  {
    text = "Climb-up the bamboo ladder in Harpoon Joe's House of 'Rum' just north and speak with 50 Ships Mufassah.",
    actions = { Action.ConversationHighlight:new("Yes! Where do I sign up?") },
  },
  { text = "Talk to Brass Hand Harry for 5 broken hands." },
  { text = "Take-apart the five broken hands." },
  { text = "With the two bronze wires in your inventory, use any piece of the hand on another to trigger a puzzle." },
  {
    text = "Complete the puzzle to obtain a brass hand (the interface will close automatically when the puzzle has been completed).<ul><li>Try flipping the brace (bottom) piece if you're having trouble completing the puzzle.</li><li>The location of the hand is as important as the components; make sure you're constructing the hand atop the silhouette.</li></ul>",
  },
  {
    text = "Talk to Brass Hand Harry (nearby) to receive an ink pad. Use the ink pad on the brass hand to get a inky hand. Then use the inky hand on the paper.",
  },
  {
    text = "Talk to Brass Hand Harry again to show him the completed Brass Hand Harry's mark.<ul><li>If you haven't done so yet, feel free to destroy the ink pad and hand.</li></ul>",
  },
  {
    text = "<i>For the following part, you must weigh under 28 kg.</i><br>Equip your ghostspeak amulet, diving apparatus, and fishbowl helmet.",
  },
  {
    text = "Travel to Dragontooth Island by talking to the Ghost captain at the small boat in Port Phasmatys (he is at the south-eastern end of the docks).",
    actions = { Action.ConversationHighlight:new("Please take me to Dragontooth Island!") },
  },
  {
    text = "Travel to the north-east part of Dragontooth Island and dive via the chain.<ul><li>You will need both hands free to dive.</li></ul>",
  },
  {
    text = "Swim around until you find a Karamthulhu and catch it. (It is the only fish that appears on the mini map.)",
  },
  { text = "Head over to the ship and climb-up the stairs." },
  {
    text = "Open and search the chest to get a crowbar.<ul><li>An aggressive giant lobster may attack you. You can open and search the chest while being attacked.</li></ul>",
  },
  { text = "Return down the stairs and climb down the ladder." },
  {
    text = "Go west on the bottom floor of the ship and dig the mess a several times to uncover the idol of many heads buried in the pile of planks and sand. Pick it up.<ul><li>You can wield weapons while down there digging, so you can kill any aggressive giant crabs or lobsters that may be hindering you from digging successfully.</li></ul>",
  },
  {
    text = "Bring the idol back to Bill Teach to receive Bill Teach's mark.",
    actions = { Action.ConversationHighlight:new("Speak about Rocking Out.") },
  },
  {
    text = "Travel to Brimhaven and go to the Brimhaven Agility Arena entrance, located south-east of the docks.",
    title = "Cap'n Izzy No-Beard",
  },
  { text = "Speak to Cap'n Izzy No-Beard.", actions = { Action.ConversationHighlight:new("Talk about Rocking Out.") } },
  {
    text = "Return to the Rimmington Customs Office. Deposit all of your items in the locker and get arrested by talking to the Customs Sergeant.",
  },
  { text = "Search the bed for a crude lockpick and use it on your cell door." },
  {
    text = "Kill one of the customs officers, loot the uniform and equip it to disguise yourself.<ul><li>You can withdraw your pickaxe from your tool belt to use as a weapon.</li><li>Make sure you do not have lootshare enabled, as this can prevent the uniform from being dropped. If you do not get the loot, try relogging.</li></ul>",
  },
  { text = "Climb the stairs twice to the top floor." },
  { text = "Open the door to the room with the cabinets." },
  {
    text = "Search the northern-most cabinet on the second column from the west for 'Wanda the Fish'.",
    actions = { Action.ConversationHighlight:new("Yes. Wanda the Fish") },
  },
  { text = "Talk to the Locker Officer and receive the brooch." },
  {
    text = "Return to Cap'n Izzy No-Beard in Brimhaven to receive Izzy's mark.",
    actions = { Action.ConversationHighlight:new("Talk about Rocking Out.") },
  },
  {
    text = "Talk to the Customs Sergeant in Rimmington with the five marks. He will give you a file.",
    title = "Back to prison and quest completion",
  },
  { text = "Use one of the marks on the file." },
  { text = "Talk to the Customs Sergeant to give him the filled file." },
  { text = "Talk to him again to get sent back to prison." },
  {
    text = "Search the bed for the crude lockpick and break out of your cell. Kill the Customs Officer(s) for another uniform, equip it, then head to the top floor.<ul><li>You may get a message saying that the lock is rusty, and the lockpick does not work. Continue trying until it works.</li></ul>",
  },
  { text = "Go to the top floor and talk to the Locker Officer to get back the filled file." },
  {
    text = "Return to your cell and right-click open the door.<ul><li>If you do not have the option to open the cell door, hop to a different world.</li></ul>",
  },
  { text = "Shout-through the barred window to Young Ralph. A cutscene will follow." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Rocking Out",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1213142400,
  prereqQuests = { "The Great Brain Robbery" },
})
