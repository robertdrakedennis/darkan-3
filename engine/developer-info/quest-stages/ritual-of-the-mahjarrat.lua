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
    text = "Talk to Sir Tiffy Cashien in Falador Park about the Ritual of the Mahjarrat.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Ritual of the Mahjarrat.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Accept the teleport.",
    actions = { Action.ConversationHighlight:new("I'll get right on it."), Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Sir Tendeth in the southern pub on Mos Le'Harmless.",
    title = "Mos Le'Harmless",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Talk to Sir Tendeth again after the cutscene." },
  { text = "Talk to the injured pirates outside and other pirates outside." },
  { text = "Go north past the walls, to the blue area marked on the mini-map.", title = "The mighty jungle" },
  {
    text = "Using the western side of the trees as cover and following the burnt areas, head to the east side of the island.<ul><li>Hide west of tropical trees and jungle grass with the examine Looks good for hiding in.... Once north of Trouble Brewing, you must keep hiding on the west side of the trees or grass. Do not move too quickly. Count at least 1 or 2 seconds before moving to next square.</li><li>Surge, the Magic ability may be used.</li><li>The music will change when you are not behind cover.</li><li>If hit by a fireball, heal by entering the nearby bank.</li><li>Remaining in the open for too long will cause a fireball to do damage and reset the player to the town gates.</li><li>If you misclick while going through the dialogue during the cutscene, you will exit it. Take one step to the west then step back behind the tree to re-trigger the cutscene.</li></ul>",
  },
  { text = "After the cutscene, report back to Sir Tiffy." },
  {
    text = "Talk to Sir Tiffy and explain the situation to him. If you haven't completed Quiet Before the Swarm then",
    title = "The archives",
    neededItems = { ["Catspeak amulet (e)"] = { quantity = 1 }, ["Cramulet"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Ritual of the Mahjarrat."),
      Action.ConversationHighlight:new("I think they were dragonkin."),
      Action.ConversationHighlight:new("Ritual of the Mahjarrat."),
      Action.ConversationHighlight:new("I think they were dragonkin."),
    },
  },
  {
    text = "Talk to Lady Table or Sir Tiffy.",
    actions = {
      Action.ConversationHighlight:new("I think I understand the reincarnation bit."),
      Action.ConversationHighlight:new("He's reincarnated as a cat."),
      Action.ConversationHighlight:new("I think I understand the stonetoucher part."),
      Action.ConversationHighlight:new("I think it's me."),
    },
  },
  {
    text = "Use the Catspeak Amulet to find Bob back on the surface, who will be roaming anywhere in Asgarnia or Misthalin. Or can be easily found in Apprentice Clara's Shop in Burthorpe.",
  },
  { text = "Once found, ask him for his collar." },
  { text = "Study Bob's collar — the back, too!" },
  {
    text = "Go through any fairy ring, dial DIR and teleport to the Gorak Plane.",
    title = "Traversing Isle of Sann",
    neededItems = { ["Bob's collar"] = { quantity = 1 }, ["Spade"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Right-click on the fairy ring and click 'Select Destination', dial AKS and teleport to Kethsi." },
  {
    text = "Head north-west and search the rubble west of the mural to get tetrahedron 4. (If you do not receive the tetrahedron 4, check the bank for it.)",
  },
  { text = "Go south-east and climb up the ramp." },
  { text = "Use Bob's collar on the wall design and solve the puzzle by positioning Bob's collar, pictured right." },
  { text = "Press the up arrow once to submit your puzzle solution." },
  {
    text = "Investigate the wall design to get tetrahedron 1, statue arm, note to you, note to Robert and Robert's necklace. Robert's necklace has no purpose in the quest and may be destroyed or banked.",
  },
  { text = "Go south-west and jump across the ledge." },
  { text = "Use the Statue arm on the statue." },
  { text = "Cross the fallen spire that appears and go down the ladder." },
  { text = "Go east and up the wall jump." },
  { text = "Climb the wall to the west." },
  { text = "Use the swing pole to the north." },
  { text = "Walk across the beam." },
  { text = "Jump east over the gap." },
  { text = "Jump from the floor to the south." },
  { text = "Go down the southern ladder twice." },
  { text = "Squeeze through the pipe to the northeast of the ladder." },
  { text = "Use a pickaxe to mine the rocks or right-click and choose mine if using the pickaxe on your tool belt." },
  {
    text = "Climb the nearby wall and run across the walls to the west. If you fail either wall run or the handholds, go back to the pipe.",
  },
  { text = "Climb across the handholds then go down the ladder." },
  { text = "Go east (along the northern coast) and search the rubble to find tetrahedron 3 and a strange device." },
  { text = "Go back up the ladder twice and jump from the floor to the south." },
  { text = "Slide down the roof to the south<ul><li>Grab the nearby spade if you didn't have one with you.</li></ul>" },
  { text = "Walk across the plank to the south." },
  { text = "Search the rubble to find tetrahedron 2." },
  {
    text = "Once all four tetrahedrons are collected, go down the stairs north of the area where tetrahedron 2 was located.",
    title = "The lost library",
  },
  {
    text = "Use each tetrahedron on an indentation (on the walls close to the entrance, also a second spade spawns by the stairs)",
  },
  { text = "Check the quest journal for the code that must be found." },
  {
    text = "Find the coordinates received and dig there with a spade to receive a Kethsian key. The strange device tells you your current location. Below is a map of Kethsi with coordinates.",
  },
  { text = "Go back to the dungeon and through the door to the south." },
  {
    text = "Search the southern bookcase for Dathana's message.<ul><li>Optional: Search the south-east bookcase for the tune banite ore scroll.</li></ul>",
  },
  {
    text = "Read the message, then take it to Sir Tiffy.  If you haven't completed Quiet Before the Swarm then",
    title = "Planning",
    actions = {
      Action.ConversationHighlight:new("Ritual of the Mahjarrat."),
      Action.ConversationHighlight:new("Ritual of the Mahjarrat."),
    },
  },
  {
    text = "Go to the White Knights' Castle and talk to either Idria, Thaerisk Cemphier, or Akrisae.",
    actions = {
      Action.ConversationHighlight:new("So, instead, you'd risk all our lives?"),
      Action.ConversationHighlight:new("I can't think of another way."),
      Action.ConversationHighlight:new("Yes, we'll have to be careful."),
      Action.ConversationHighlight:new("I will risk my own life to negotiate."),
    },
  },
  {
    text = "Make sure to get Arrav's heart before going to speak to Azzanadra or he will not talk to you about this quest.",
  },
  {
    text = "Go to the north-eastern Varrock Dig Site winch, climb down and talk to Azzanadra. Ask him about the quest.",
    actions = { Action.ConversationHighlight:new("Ritual of the Mahjarrat.") },
  },
  {
    text = "From the Rellekka Hunter area (fairy ring code: DKS) take the canoe (north-west from fairy ring code: DKS, north of Larry and past the hunter area) to the Mahjarrat Ritual Site Cavern, then squeeze-past the ice block to the east (do not enter the cavern itself). This allows one to skip the Wilderness.<ul><li>If there is no heat globe on the pedestal, you may reclaim it from Azzanadra and place on it.</li></ul>",
    title = "Setting up the site",
    neededItems = { ["Ring of visibility"] = { quantity = 1 }, ["Rope"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "At the Ghorrock Fort, jump over the pillar to the south (it is outside of the walls of the fort, there is no red line to indicate it, the obstacle is to the south along a line of trees and pillars.) then go through the tunnel.<ul><li>Avoid the undead Broavs or the player will get thrown into Zemouregal's dungeon. To escape, search the bed and lift up the tiles to the east of the cell. Enter the tunnel to escape.</li></ul>",
  },
  {
    text = "Several items will need to be placed around the ritual site:<ul><li>Northern Beacon: Run west from the ice entrance at the north of the plateau. Place the beacon on the closest tree.</li><li>Southern Beacon: Run directly south, and place the second beacon in the tree south-south-west of the ritual stone. If this is done correctly, you should have a message saying the south beacon is opposite the northern beacon.</li><li>Arrav's Heart: Place Arrav's heart in the rocks south of the ritual marker. The site is several paces south of the marker and just a pace or two east. If you cannot place the heart in a pile of rocks, you are at the wrong place.</li><li>Western Beacon: Run up the west side of the plateau until you find a tree just north of a central line. Place the beacon here. Using the tree just south of the central line will not work.</li><li>Eastern Beacon: Run straight east and place the beacon on the tree directly opposite the western tree. You should have a message saying the east beacon is directly opposite the west beacon.</li><li>Go to the north-west corner of the plateau and look for an Overhanging Tree above the Cavern entrance. Use a rope on it to create a rope climb from the beach to the plateau, turning it into a Snow-covered Tree with an attached rope. This will be used for your allies to climb to the ritual. (From now on, you can also use the rope yourself to get between the canoe landing site and the plateau without having to go past the dragons around Ghorrock.)</li></ul>",
  },
  { text = "Head to Zemouregal's fort which is south-west of the ritual site.", title = "Zemouregal's fort" },
  { text = "Kill the Armoured zombie outside of the main entrance and pick up the Code key and Decoder strips." },
  {
    text = "Read the code key and open the main entrance by obtaining the code from the key and strips. (In this example we take the code 'EIHC', see the image on the right for the solution)<ul><li>Drag the red strip number 1 to the first letter of the code key (example: 'E').</li><li>A single number will be revealed by the code strip (example: 'E' row). Enter that number in the code box using the controls.</li><li>Drag the red strip number 2 to the second letter of the code key (example: 'I').</li><li>A single number will be revealed by the code strip (example: 'I' row). Enter that number in the code box using the controls.</li><li>Drag the red strip number 3 to the third letter of the code key (example: 'H').</li><li>A single number will be revealed by the code strip (example: 'H' row). Enter that number in the code box using the controls.</li><li>Drag the red strip number 4 to the fourth letter of the code key (example: 'C').</li><li>A single number will be revealed by the code strip (example: 'C' row). Enter that number in the code box using the controls.</li></ul>",
  },
  {
    text = "Go through the front door and search the crate by the north-eastern weapon rack for the storeroom's Code key.",
  },
  { text = "Read it and use it to open the storeroom door to the west in the same manner as the front entrance." },
  { text = "In the northern part of the storeroom, search the crates (2 empty spaces required)." },
  { text = "Read the Heart magic notes." },
  {
    text = "Exit the room, head up the western stairs, and stand next to the southern door to listen to Zemouregal and Sharathteerk (you may have to move to the tile east of the door to allow the dialogue to play).",
  },
  {
    text = "Head back downstairs, then go up the eastern stairs. Use the previously acquired Code key to enter the reliquary.",
  },
  { text = "Smash the black stone in the southern half of the room." },
  {
    text = "Exit the fortress and head to the south-eastern part of the ritual site.",
    title = "Searching for the Stone of Jas",
  },
  {
    text = "Talk to Movario.",
    actions = {
      Action.ConversationHighlight:new("So, you're still working for Lucien?"),
      Action.ConversationHighlight:new("What are you doing up here?"),
    },
  },
  {
    text = "With the ring of visibility equipped, run west from Movario along southern wall of the site to find a shadow pedestal (almost directly south-east from the ritual marker).",
  },
  {
    text = "Go back inside the centre of the Ghorrock Castle courtyard (past the dragons), and climb the western stairs next to the statue of Khazard.",
  },
  { text = "Go east and climb down the stairs." },
  { text = "Scale the Damaged Wall next to the door." },
  {
    text = "Run past the smashed pedestal, and climb down the smashed rampart on west side of the trapdoor in the courtyard.",
  },
  {
    text = "Enter the trapdoor. The icefiends encountered here will hit hard and there will be a good number of them. Bring high level food (sharks or swordfish) just in case for comfortable exploration.",
  },
  { text = "Enter the room to the west and then head south." },
  { text = "Take the Heat globe and exit the same way you came." },
  { text = "Return to the shadow pedestal, south east of the ritual marker." },
  {
    text = "Put the globe on the pedestal.<ul><li>If you try to put heat globe on the shadow pedestal and it says 'That could be a good idea, but you've got other things to do first.', it means either that the beacons are not aligned properly, you have not placed Arrav's heart in the rocks to the south of the Ritual marker, you haven't placed the rope to the north-west of the tunnel or you have not talked to Movario after having done so. If you are completely sure you have done all of these things, talk to Movario to the east of the shadow pedestal, and he will place the heat globe in the pedestal. A few players have removed a beacon from a tree and put in back in the tree—this also has worked. Some report that reading the notes/Dathana's message also fixes this.</li></ul>",
  },
  { text = "Enter the newly opened entrance, south of the shadowy pedestal.", title = "The ritual" },
  { text = "Follow the open passageway and touch the Stone of Jas." },
  { text = "After the cutscenes, defeat Khazard.", title = "The battle - Khazard" },
  { text = "It helps to pray against magic and when Bouncer is summoned, lure him to Wahisietel so that he dies." },
  {
    text = "Defeat the Enhanced ice titans.<ul><li>Lucien will be casting spells at the player whilst fighting the ice titans. If a smoking black skull is flying towards you, move at least two squares away from your current position. Also, praying melee completely negates damage from the titans' normal attacks. If ice starts encasing the player, they should click rapidly on the ground around them to escape.</li><li>You will only be able to damage the two enhanced ice titans that run at you; your teammates must kill the rest.</li><li>Avoid attacking Lucien, as doing so causes Lucien to hit you with a high damage attack.</li></ul>",
    title = "Ice titans",
  },
  {
    text = "Defeat the Ice Demons<ul><li>Praying against magic helps limit the damage the demons can cause. They will also summon icicles which will fall around the player. When this happens, run out of the icicles to avoid being trapped due to Lucien still casting the skull spell at the player.</li><li>You will only be able to damage the two ice demons that run at you; your teammates must kill the rest.</li><li>You are able to use Split Soul on the two that your teammates are meant to kill to speed up the process.</li></ul>",
    title = "Ice demons",
  },
  { text = "Attack the armoured zombies until  Arrav  is summoned.", title = "Arrav" },
  { text = "Attack Arrav until he follows you. You may have to attack the zombies before Arrav will follow you." },
  {
    text = "Lure him over to where his heart is placed and wait for Arrav to fight Zemouregal before proceeding.<ul><li>If you have a combat follower out, it is best to dismiss it when Arrav starts following you. Otherwise, it is possible that the combat familiar and Arrav will fight. If this occurs when Arrav gets his heart back, the familiar's attack may distract Arrav from attacking Zemouregal. Instead, Arrav will stand near the rockpile where his heart was hidden and will not move. This is a glitch, and the only way to overcome it is to leave the battle, return, and fight the fourth battle again from its start.</li></ul>",
  },
  { text = "Head to where the southern beacon was.", title = "The broken beacon" },
  { text = "Turn on a prayer against ranged or magic attacks and pick up the beacon pieces." },
  { text = "Click on a piece to assemble all of them and then place the beacon back into the tree." },
  {
    text = "Watch the cutscenes and pick a number for the new hiding spot for the Stone of Jas.",
    title = "Finishing up",
  },
  {
    text = "Go to the stalls area of the town (north-west of where the bank would be in Draynor Village) in the destroyed version of Draynor Village.",
  },
  { text = "Watch the last cutscene." },
  {
    text = "Talk to Sir Tiffy.  If Quiet Before the Swarm isn't completed .",
    actions = {
      Action.ConversationHighlight:new("Ritual of the Mahjarrat."),
      Action.ConversationHighlight:new("Ritual of the Mahjarrat."),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Ritual of the Mahjarrat",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.veryverylong,
  releaseDate = 1315958400,
  prereqQuests = {
    "The Temple at Senntisten",
    "While Guthix Sleeps",
    "Hazeel Cult",
    "Enakhra's Lament",
    "The Slug Menace",
    "A Fairy Tale II - Cure a Queen",
    "Cabin Fever",
    "A Tail of Two Cats",
    "Fight Arena",
    "The General's Shadow (miniquest)",
  },
})
