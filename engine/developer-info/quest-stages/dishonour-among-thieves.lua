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
    text = "Open the invitation box to teleport to the Empyrean Citadel.",
    title = "Starting off",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Moia.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Talk to Sliske.",
    actions = { Action.ConversationHighlight:new("[Continue]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Activate the communication device in your backpack.",
    actions = { Action.ConversationHighlight:new("Teleport to Zamorak's Hideout.") },
  },
  {
    text = "Continue dialogue with Zamorak.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Yes, to steal the Stone of Jas."),
      Action.ConversationHighlight:new("Yes."),
    },
  },
  {
    text = "Talk to Moia.",
    title = "Enakhra/Zemouregal",
    actions = {
      Action.ConversationHighlight:new("[More...]"),
      Action.ConversationHighlight:new("Zemouregal and Enakhra."),
      Action.ConversationHighlight:new("Teleport me."),
      Action.ConversationHighlight:new("Yes."),
    },
  },
  { text = "Kill the armoured zombies." },
  {
    text = "Go upstairs and talk to Zemouregal or Enakhra.",
    actions = {
      Action.ConversationHighlight:new("I've come to speak with you."),
      Action.ConversationHighlight:new("[Continue]"),
    },
  },
  {
    text = "Search the orange and brown chest for free cut gems, 5k coins, and 250 Dungeoneering tokens before leaving the room.",
  },
  { text = "Enter the cave entrance north-west of the Kandarin Monastery.", title = "Hazeel/Jerrod" },
  { text = "Board the raft." },
  { text = "Talk to Hazeel.", actions = { Action.ConversationHighlight:new("[Continue]") } },
  { text = "Go down the trapdoor behind Handelmort Mansion." },
  {
    text = "Talk to Jerrod.",
    actions = {
      Action.ConversationHighlight:new("A friend of Hazeel's."),
      Action.ConversationHighlight:new("The red eyes."),
    },
  },
  { text = "Search the dead butler for a cell key." },
  { text = "Unlock the cell door." },
  { text = "Talk to Jerrod." },
  {
    text = "Search the orange and brown chest for free cut gems, 5k coins, and 250 Dungeoneering tokens before leaving the room.",
  },
  { text = "Go to the Ruins of Uzer (fairy ring DLQ, then north-east).", title = "Khazard" },
  {
    text = "Enter and talk to General Khazard.",
    actions = { Action.ConversationHighlight:new("[Continue]"), Action.ConversationHighlight:new("Yes.") },
  },
  { text = "In the shadow realm, search Palkeera's corpse." },
  { text = "Speak to Khazard." },
  {
    text = "Search the orange and brown chest for free cut gems, 5k coins, and 250 Dungeoneering tokens before leaving the room.",
  },
  {
    text = "Go to the Black Knights' Base in the Taverley Dungeon.<ul><li>Via the Master archaeologist's outfit to Isaura, an artefact collector in the base.</li><li>Via an Archaeology teleport purchased from Ezreal in the Archaeology Guild for 250 chronotes.</li><li>Via a hoardstalker ring or dungeoneering cape  and running south.</li></ul>",
    title = "Lord Daquarius",
    actions = {
      Action.ConversationHighlight:new("Collectors"),
      Action.ConversationHighlight:new("Isaura"),
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("Taverley blue dragon dungeon"),
    },
  },
  {
    text = "Talk to Lord Daquarius in the south-west corner.",
    actions = { Action.ConversationHighlight:new("Dishonour Among Thieves.") },
  },
  {
    text = "Talk to the Preaching Black Knight in the middle.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Speak to the group of black knights in the north-east corner.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Get the poison from the chest in the north-west corner." },
  { text = "Talk to Lord Daquarius." },
  {
    text = "Search the orange and brown chest for free cut gems, 5k coins, and 250 Dungeoneering tokens before leaving the room.",
  },
  {
    text = "Enter Death's office.<ul><li>Home Teleport to the Draynor lodestone and head north.</li><li>War's Retreat Teleport and then exit to Death's office via the portal.</li></ul>",
    title = "Nomad",
    actions = { Action.ConversationHighlight:new("Death's office") },
  },
  {
    text = "Continue dialogue with Death and Nomad.",
    actions = { Action.ConversationHighlight:new("[Continue]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "If ready for combat, accept the teleport to the hideout.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Search the orange and brown chest for free cut gems, 5k coins, and 250 Dungeoneering tokens before leaving the room.",
  },
  {
    text = "Activate the Communication device to return to Zamorak's Hideout.",
    title = "The Plan",
    actions = { Action.ConversationHighlight:new("Teleport to Zamorak's hideout") },
  },
  {
    text = "Continue the dialogue.",
    actions = { Action.ConversationHighlight:new("Move on."), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Go to the Barrows and enter the dungeon to the south-east (outside the stone wall).<ul><li>Via Drakan's medallion</li></ul>",
    actions = { Action.ConversationHighlight:new("Barrows") },
  },
  {
    text = "Search the orange and brown chest for 3 cut ruby gems, 5k coins, and 250 Dungeoneering tokens just North of the lair entrance.",
  },
  { text = "Enter Sliske's lair (you cannot enter with a familiar summoned)." },
  {
    text = "From the first room, enter the cave entrances as follows:<ul><li>Blue (blue paint above the entrance) (western door)</li><li>5 (V) (on the floor) (south door)- players that use 'low detail' may have some trouble seeing the V-shape. You can examine the slabs to be told which number is engraved on it, if it is unclear.</li><li>A sinister green face (northern most door).</li><li>Triangle (north-eastern most door).</li><li>Red (red paint above the door) (south-eastern door).</li><li>Complete face (On the floor) (north-eastern door).</li><li>Grey (may also appear as a white colour; western door in the north-eastern corner).</li></ul>",
    title = "Sliske's maze",
  },
  { text = "Watch a cutscene.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Wait for the Wight to face away from you and forward dive behind them to close the gap. Left-click to assassinate them.",
    title = "Room 1",
  },
  { text = "Wait for the masks to scan 3 times." },
  { text = "Move quickly to the middle of the room and pull the lever." },
  { text = "Get caught, walk west to room 2 (pull the lever)." },
  {
    text = "Stand on the right side of the hallway with the shadow and forward dive to the doorway on the east, avoiding the shadow along the way (use the crevices on the sides of the hallway to hide).<ul><li>It may be very hard to see the shadow. It is a pulsating black light on the floor, moving up and down the corridor.</li></ul>",
    title = "Room 2",
  },
  { text = "Assassinate the Wight and pull the lever." },
  {
    text = "Go through the passageway now open at the beginning of the room, forward dive past the Wight, and assassinate them.",
  },
  { text = "Proceed up the hallway and dive past the last two masks to the lever." },
  { text = "Enter room 3." },
  { text = "Forward dive past the guard (1) to the east and assassinate him.", title = "Room 3" },
  { text = "Continue north and dispatch the next guard (2)." },
  {
    text = "Stand in the doorway to the main hallway and wait for the guard (3) to come to you and dispatch him (watch for the shadow on the floor).",
  },
  { text = "Continue across the hallway and dispatch the guard walking around the pillar (4)." },
  {
    text = "Go north to the lever (2) and pull it. Rocks will fall, distracting the guard (5). Dispatch him and pull the lever (3).",
  },
  { text = "Return to Jerrod. You can get caught to get back quickly." },
  {
    text = "Go back east and then down the narrow hallway and stop before entering the main hallway, wait for the shadow to go to the far end and cross the main hallway, enter the room across from you and assassinate the guard (6).",
  },
  { text = "Return to the south-east corner and pull the lever (1)." },
  {
    text = "Head to the north-east lever, avoiding the shadow. Stand in the south-east corner of the hallway where the shadow is patrolling; when the shadow moves away, move one space north, click the option to turn 45 degrees left, then forward dive. Assassinate the guard (7).",
  },
  { text = "Go behind the shadow and pull the lever (4)." },
  {
    text = "Jerrod will kill the last guard (near lever 5) by the doors. Pull the final lever by the door, watching for the main hallway shadow.",
  },
  { text = "Once inside, finish the dialogue.", actions = { Action.ConversationHighlight:new("Okay, will do.") } },
  {
    text = "Right click track with Jerrod. Use chat options 1 or 2 to direct the camera to face the footprints. Then select option 3 to track. If done correctly, Jerrod will start following the footprints.",
    title = "Memories",
  },
  {
    text = "Right-click read mind with Moia in the centre. If she doesn't let you, wait for Jerrod to be in position.<ul><li>Mouse around the interface until you find the position such that the probe blinks fast (approximately twice per second), then left-click to probe the memory. Turning on sound effects can help.</li></ul>",
  },
  {
    text = "After reading the first wraith's mind, light and shadow levels will show up on the top of the screen, trending in opposite directions. To maintain their balance, right-click switch on either Nomad or Enakhra to make it go the opposite direction.",
  },
  {
    text = "After reading the second wraith's mind, the passageways on the western and eastern walls will show glowing yellow eyes. Talk to Zemouregal to move the purple portals to the two spots with eyes. Note the compass to make sure you are selecting the correct location.",
  },
  {
    text = "Keeping an eye on the light and shadow levels, repeat this process for 5 total memories: track with Jerrod, read mind with Moia, then select the corresponding locations for the glowing eyes with Zemouregal.",
  },
  { text = "Open the large vault door." },
  {
    text = "Watch the cutscene.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Fight and kill your shadow apparition.<ul><li>Run off the platform when teleported to the centre and move out of the path of the smoke. It travels straight down the stairs.</li><li>Stand near the boss when he summons darkness or you will take massive damage.</li><li>Click rapidly to escape cocoons.</li></ul>",
    title = "Your apparition",
  },
  { text = "Kill 30 wights around the room.", title = "Wights" },
  { text = "Use Nomad's special attacks to kill multiple wights with a single hit." },
  { text = "Touch the Stone of Jas.", title = "Apparitions", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Kill everyone's apparition." },
  {
    text = "All damage is increased to 10,000 and above, capping at 25,000.<ul><li>As such, bleed attacks are helpful here as well as AoE.</li></ul>",
  },
  {
    text = "Stop Nomad.<ul><li>You must choose to stop him, otherwise he becomes powerful in future quests.</li></ul>",
    title = "Finishing up",
    actions = {
      Action.ConversationHighlight:new("You don't have to do this."),
      Action.ConversationHighlight:new("Stop Nomad - throw the spear back."),
    },
  },
  {
    text = "Make sure to watch the final cutscene without skipping; Otherwise, you will need to re-enter the hideout",
    actions = {
      Action.ConversationHighlight:new("[Continue]"),
      Action.ConversationHighlight:new("[Continue]"),
      Action.ConversationHighlight:new("Remain neutral."),
      Action.ConversationHighlight:new("No"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Dishonour among Thieves",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1424649600,
  prereqQuests = { "Hazeel Cult", "Missing, Presumed Death", "Morytania", "Nomad's Requiem", "Nadir (saga)" },
})
