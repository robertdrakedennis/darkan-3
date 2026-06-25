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
    text = "Talk to Sir Cadian outside of the Senntisten surface entrance.  Afterwards, talk to Sir Cadian again and finish the conversation.",
    title = "Investigating the asylum",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>To get there, you may use the Archaeology journal teleport and run north.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Enter the nearby Ancient door.<ul><li>Do not use the pontifex ring's Senntisten teleport as it will exit the instance.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Naressa or Gregorovic in the house near the southwest corner of Senntisten and agree to meet them in the asylum.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'll come with you now."),
    },
  },
  { text = "Talk to Naressa or Gregorovic once inside." },
  {
    text = "Talk to them a second time and pull the crane lever under the desk, not the lever on the wall.<ul><li>Make sure to click-through the dialogue till it says 'Was that a clunk I heard upstairs?' or you will not be able to find the key.</li></ul>",
  },
  { text = "Head up the stairs" },
  {
    text = "To the south<ul><li>Search the drawer of the filing cabinet for a chain of keys (right top corner of cabinet).</li><li>If you cannot find the keys, head downstairs and flip the lever again (see image).</li><li>If you cannot find the keys still, search the crate, then the bookcase upstairs, the drawer on the filing cabinet will be clickable after that.</li><li>If you cannot find the keys, head downstairs and flip the lever again (see image).</li><li>If you cannot find the keys still, search the crate, then the bookcase upstairs, the drawer on the filing cabinet will be clickable after that.</li></ul>",
  },
  {
    text = "To the east<ul><li>Search the notes on the ground near the northeastern bookcase for key instructions</li></ul>",
  },
  { text = "Head downstairs" },
  {
    text = "Read the key instructions in order to figure out which key opens the gate.<ul><li>The long or short purple key is usually the key to unlock the asylum gate.</li><li>This differs depending on a randomly generated riddle but can be brute-forced by testing each key.</li></ul>",
    actions = { Action.ConversationHighlight:new("Short Purple Key") },
  },
  { text = "Walk through the gate" },
  { text = "Operate the asylum elevator inside for a short cutscene" },
  { text = "Talk to Naressa and Gregorovic inside" },
  { text = "Enter the room to the east to be arrested by an abyssal savage" },
  {
    text = "Talk to Yk'Lagor, Cyrillus, or Dahaka in the room.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Collect the following items from the area.<ul><li>Syringe from the crate of syringes in the incinerator room (south).</li><li>Infernal ashes from the spiky chair several rooms later (west). It is in small rectangular room with a single abyssal savage.</li><li>Vial from the crate of vials in the room directly north-east of Senecianus in one of the final rooms (north-west).</li></ul>",
    title = "Curing the demons",
  },
  {
    text = "Find Rowena (little girl ghost) starting at the south-east, and follow her through the dungeon back to the starting room.",
  },
  {
    text = "Speak to Senecianus (appearing as Rowena) in the north-western-most room, who will give you a Break Curse scroll.",
  },
  {
    text = "Create the cure. (You can use the items on each other while running)<ul><li>Use the syringe on Senecianus (little girl ghost) (north-west) to obtain a Syringe of Chthonian blood.</li><li>Fill the vial using the sink in the southwestern operating room (south, room west of the room with Nabor's body) to obtain Vial of vile water.</li><li>Right click to use the infernal ashes on the vial to obtain Experimental potion 6. Be careful not to scatter the infernal ashes.</li><li>Use the blood syringe on the incomplete potion to create the SIPD cure.</li></ul>",
  },
  { text = "Talk to Yk'Lagor with the cure. .", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Talk to Yk'Lagor again and watch the cutscene.<ul><li>If in the last room, walk through the gate then down broken railing by rope.</li></ul>",
    actions = { Action.ConversationHighlight:new("This is for the best, Greg.") },
  },
  { text = "Continue the dialogue and the group will agree to go to Falador." },
  {
    text = "Speak to Saradomin on his throne in the White Knights' Castle in Falador and agree to continue the quest.",
    title = "Saradomin's counsel",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Speak to Saradomin again.<ul><li>You may need to speak multiple times until your quest journal states 'The White Knights in the Jolly Boar Inn' as the next step.</li></ul>",
  },
  {
    text = "Talk to the four white knights at the Jolly Boar Inn (can quickly get there using the Fort Forinthry lodestone, or alternatively an Archaeology teleport or the Master archaeologist's outfit to teleport to the Infernal Source).<ul><li>Sir Cull</li><li>Sir Berus</li><li>Sir Dated</li><li>Sir Plus</li></ul>",
    title = "Infiltrating the Infernal Source",
  },
  {
    text = "Enter the Infernal Source Dig Site archaeology dungeon just west of the inn.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Blythe.",
    actions = {
      Action.ConversationHighlight:new("Growth through chaos."),
      Action.ConversationHighlight:new("Philippa Wharton."),
      Action.ConversationHighlight:new("Dagon."),
    },
  },
  { text = "Put on the cultists robes and climb down the secret passage." },
  {
    text = "Pickpocket cultists until you get a cast iron key. Can be done by repeatedly pickpocketing the same cultist.",
  },
  {
    text = "Collect information about the cult.<ul><li>Go north, talk to Wilona and join the ritual circle</li><li>Talk to Audrey in the same room as Wilona.</li><li>Talk to Ridley in the hallway.</li><li>Talk to Edda in the southeast room.</li><li>In the south-east room search the crate against the southern wall for spare cultist robes, and give the robes to Tyne in the cell in the south central room.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Domine fortis chaos"),
      Action.ConversationHighlight:new("In sanguine et in morte"),
      Action.ConversationHighlight:new("Honoramus tibi hoc sacrificium"),
    },
  },
  { text = "Once all 5 pieces of information are obtained, return to Saradomin." },
  {
    text = "Talk to Saradomin and agree to continue the quest. Then speak to him again for a skippable cutscene.",
    title = "Guardians of Guthix",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to Saradomin after the cutscene and go through all the dialogue options." },
  {
    text = "Go down one floor, and talk to Adrasteia on the southern side near the bookcase; east of the interrogation racks. (If she is not there, check the next room or lobby and return.)",
  },
  {
    text = "Talk to the Tree of Balance, south-west of the Archaeology Campus.<ul><li>Use the Archaeology journal teleport and run south-west</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  { text = "Talk to the guardians of Guthix for a cutscene." },
  { text = "Return to Saradomin and talk to him for an Energy scanner." },
  {
    text = "Scan with the energy scanner, then go to the Memorial to Guthix<ul><li>You can teleport directly to the Memorial using either Sixth-Age circuit or by using memory strand from the currency pouch</li></ul>",
    title = "Preparing for the spell",
  },
  { text = "Stand in front of the pool and talk to The Archivist if he is visible near the pool." },
  {
    text = "Scan with the energy scanner, then go to the Gleaming wisp divination site south-east of the Karamja lodestone and interact with the green Mystical wisp.<ul><li>You can use the Elder divination outfit for a direct teleport to the gleaming wisp colony, the Wicked hood to the nature altar, or Karamja gloves 3 or higher to teleport nearby to the Shilo Village mine.</li></ul>",
  },
  { text = "Talk to the Confused automaton and complete all the dialogue." },
  {
    text = "Exit the cache and scan with the energy scanner, then talk to Merethiel at The Lost Grove .<ul><li>You can use the Boss portal to Solak, fairy ring BJS if repaired (Take 5 Bittercap mushroom to repair it if you have not) and then interact with the magical mushrooms, the Elder divination outfit to the Incandescent wisp colony, or BKP and then run west to the Standstone to get to The Lost Grove.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes"), Action.ConversationHighlight:new("Yes - please send me in.") },
  },
  { text = "Speak to Guardians of Guthix once inside." },
  {
    text = "Talk to Isaura at the Black Knights' Base (northeastern corner of the base, next to the kitchen) in the Taverley Dungeon  for a crate of birds.<ul><li>You can quickly get to her using the Archaeology teleport or the Master archaeologist's outfit teleport to Collectors, then Isaura. (0→4)</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Return to The Lost Grove, then talk to Merethiel.",
    title = "Casting the spell",
    actions = { Action.ConversationHighlight:new("Yes"), Action.ConversationHighlight:new("Yes - please send me in.") },
  },
  { text = "Speak to Juna once inside." },
  { text = "Place effigy on the stone circle in the middle of the arena." },
  { text = "Harvest from the energy spring until you have 52 Guthixian energy." },
  { text = "Make all of the letters at the offering cradle (the large orb in the middle of the area)." },
  {
    text = "Collect four stone circle building kits from the material cart slightly north-east of Juna and construct four empty stone circles starting from the west.",
  },
  {
    text = "The spelling of 3 words is required for each of the 3 rituals (9 total).<ul><li>For each ritual phase, the order of words is random per player.</li><li>To spell words, add a rune (letter) on each of the stone circles you've just constructed.</li><li>Words are spelled in clockwise orientation on the stone circles along the perimeter of the arena.</li><li>To attempt the word you spelled, place effigy on stone circle, followed by 'start ritual'.</li><li>Each successful ritual phase will yield the chat message: Ritual #: #/3 words completed.</li></ul>",
  },
  {
    text = "In the first ritual, the words are; in no certain order:<ul><li>H A L T</li><li>W A R D</li><li>G O D S</li></ul>",
    title = "Completing the rituals",
  },
  { text = "Talk to Juna." },
  { text = "Collect from the material cart again and build the 5th stone circle." },
  {
    text = "In the second ritual, the words are; in no certain order:<ul><li>L E A V E</li><li>A N I M A</li><li>A E G I S</li></ul>",
  },
  { text = "Talk to Juna for me?" },
  { text = "Collect from the material cart again and build the 6th stone circle." },
  {
    text = "In the third ritual, the words are; in no certain order:<ul><li>B A N I S H</li><li>S H I E L D</li><li>D I V I N E</li></ul>",
  },
  { text = "Talk to Juna." },
  {
    text = "Return to Saradomin's throne at the White Knights' Castle at Falador  and speak to Adrasteia.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Talk to Adrasteia again." },
  {
    text = "Go to the Empyrean Citadel (Adrasteia offers to teleport you).<ul><li>Alternatively use the invitation box.</li></ul>",
    title = "Finishing",
    actions = {
      Action.ConversationHighlight:new("Yes please."),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("Yes"),
    },
  },
  { text = "Talk to the gods." },
  { text = "Interact with the fake quest completion certificate and talk to the voices in your head." },
  { text = "Quest Complete!" },
}

return Quest:new({
  name = "Twilight of the Gods",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1654473600,
  prereqQuests = { "Extinction", "Broken Home", "Eyes in Their Stars", "Naressa" },
})
