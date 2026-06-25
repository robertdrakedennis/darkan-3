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
    text = "Talk to Chuck in the Ardougne Zoo",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Back to the Freezer Quest") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Note down the blue text.<ul><li>Alternatively, the Passphrase may also be found in your quest journal.</li></ul>",
  },
  { text = "In the Rellekka marketplace, talk to Skot or Mundsen." },
  { text = "When prompted, tell them the passphrase from Chuck." },
  { text = "After telling the passphrase, finish the dialogue and cutscene.", title = "Desert" },
  {
    text = "South-west of Pollnivneach, talk to Dundee, who will give you a crocspeak amulet.",
    actions = { Action.ConversationHighlight:new("Ask about the penguins.") },
  },
  { text = "Equip the crocspeak amulet." },
  { text = "Just west, talk to Croc.", actions = { Action.ConversationHighlight:new("Bye") } },
  { text = "South-west, investigate the T.A.R.D.I.S.." },
  {
    text = "Return to Chuck in Ardougne.",
    title = "The secret-secret agent",
    actions = { Action.ConversationHighlight:new("Back to the Freezer Quest") },
  },
  { text = "Note down the agent's favourite food Chuck tells you. Favourite food may be found in your quest journal." },
  { text = "Return to the T.A.R.D.I.S." },
  { text = "Unequip any item in your cape and weapon slot." },
  {
    text = "With your clockwork suit in your backpack talk to any named penguin and you will automatically equip your clockwork suit.<ul><li>If you don't have a clockwork suit with you return to the zoo and speak with Chuck. Chuck will give you a clockwork suit.</li></ul>",
  },
  {
    text = "In order, talk to the following penguins:<ul><li>Emperor Wing</li><li>Elon</li><li>Hugh</li><li>Gordon</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Remind me what happened."),
      Action.ConversationHighlight:new("What happened to you at the party?"),
      Action.ConversationHighlight:new("What happened to you at the party?"),
      Action.ConversationHighlight:new("What happened to you at the party?"),
      Action.ConversationHighlight:new("What happened to you at the party?"),
      Action.ConversationHighlight:new("Are you a spy?"),
    },
  },
  { text = "Gordon will give you a G.P.S.. If he does not, simply talk to him again." },
  { text = "Walk to the edge of the icy patch to get out of the clockwork suit." },
  {
    text = "Head to the pier north of Fremennik Province lodestone and north-east of Rellekka, travel to the Iceberg via the smaller boat.",
    title = "The secret bunker",
    neededItems = { ["Ice cooler"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Locate with the G.P.S., follow the directions until you find the Mysterious Hatch." },
  { text = "To the north-east, right click Jim the polar bear and choose 'Tuxedo-Time'." },
  { text = "Interact with the hatch and type in '1234' to enter." },
  { text = "Enter the north-east room and search the interactable cupboard on the north wall for a hat." },
  { text = "Exit the room and then follow the corridor south." },
  {
    text = "Head west and take the first turn north. Enter the large door leading to the large room in the centre of the bunker.",
  },
  {
    text = "Enter the locker room to your west and search Gordon's locker on the west side for the de-lore-ing device parts list and de-lore-ing device (incomplete).",
  },
  { text = "Exit the large door to go back to the main corridor" },
  {
    text = "Enter the southwest room full of crates and search or look in them until you find both a hammer and flax.",
  },
  {
    text = "In the north-western room, Peruse the ice shelf (looks like a normal shelf) for The Penguin Book of Gielinor.",
  },
  { text = "Behind you, Get Ice from the Ice Maker by the door to take ice cubes." },
  { text = "Use the hammer on the ice cubes to create the flax cap-ice-i-tor." },
  { text = "Use the flax cap-ice-i-tor on the de-lore-ing device (incomplete) for the de-lore-ing device." },
  { text = "Operate the de-lore-ing device." },
  {
    text = "Take the monkey wrench (north & slightly east), use it on the de-lore-ing device then operate the device again.",
    title = "Time travelling!",
  },
  {
    text = "Take the penguin bongos and cowbells in the north-west corner, use either on the device, operate the device again.",
  },
  { text = "Pick cabbage, use it on the device, operate the device again." },
  {
    text = "Speak to the man with a dragon pickaxe, use worthless old boots on the device, operate the device one last time.",
  },
  { text = "Talk to Gordon in the south-western room.", title = "Back in the bunker" },
  { text = "Talk to Elon in the centre of the bunker." },
  { text = "Get the fishing rod from the corner of the War room south of the big door." },
  {
    text = "Go to the 'cold storage' crate room in the southwest and search until you have both the cog and coolant holder.",
  },
  { text = "Use the cog on the fishing rod to create the 'fishvention' rod." },
  {
    text = "Go to the western room and gather Pure S-now - wait until you have gathered all 2,000. (Process takes about a minute)",
  },
  { text = "Search the crate north of the Mysterious Ice Block in the same room for a lemon." },
  { text = "Use the Mysterious ice block in the room to craft 2000 lemon sole runes." },
  { text = "Go to the north-western room and click on the Ice Maker twice to create cool-ant and an ice box." },
  { text = "Talk to Buzz in the prison to the east for 21 killerwatt energy." },
  { text = "Leave the bunker via the door to the north." },
  {
    text = "Walk east, talk to Jim the polar bear to exit your disguise.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Equip your insulated boots and prepare for combat.",
    title = "Getting Energy",
    neededItems = { ["Insulated boots"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Make your way to the top floor of Draynor Manor (staircase then ladder)." },
  { text = "Enter the eastern room, then the Interdimensional Rift." },
  { text = "Kill 10 Killerwatts until you have 121 killerwatt energy." },
  {
    text = "Return to the iceberg.<ul><li>Head to a pier north of Fremennik Province lodestone and north-east of Rellekka, travel to the Iceberg via the smaller boat.</li></ul>",
    title = "Returning to the iceberg",
    neededItems = {
      ["Bright energy"] = { quantity = 1 },
      ["Flickering energy"] = { quantity = 1 },
      ["Gleaming energy"] = { quantity = 1 },
      ["Sparkling energy"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Head to north-east of the iceberg, talk with Jim the polar bear, right click 'Tuxedo-Time'." },
  { text = "Re-enter the bunker via the Mysterious Hatch." },
  { text = "In the centre of the bunker, talk to Elon to give him all the items." },
  { text = "Fill the energy container to the north-west with your divination energy." },
  { text = "Configure the battery control station, north-west." },
  { text = "Solve the puzzle. See the full guide for advice on solving the puzzle." },
  {
    text = "Once complete, speak to the following penguins, in order:<ul><li>Elon, in the same (central) room, roaming around T.A.R.D.I.S.</li><li>Gordon, in the south-west room (northern part).</li><li>Hugh roaming around the north-west room.</li><li>Emperor Wing, in the central room again, easy to recognise due to his size.</li></ul>",
  },
  { text = "Click the T.A.R.D.I.S. in the centre for a cutscene." },
  { text = "Return to Chuck.", actions = { Action.ConversationHighlight:new("Back to the Freezer Quest") } },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Back to the Freezer",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1488153600,
  prereqQuests = { "Some Like It Cold", "Ernest the Chicken" },
})
