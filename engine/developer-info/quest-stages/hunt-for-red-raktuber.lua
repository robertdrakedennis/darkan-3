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
    text = "Talk to Larry in the south-west of the Ardougne Zoo and agree to help.",
    title = "Larry's holiday",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Hunt for Red Raktuber") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Go east to the south coast of Witchaven, and inspect one of the giant footprints on the western side of the southern coast.",
  },
  { text = "Return to Larry for a cutscene.", actions = { Action.ConversationHighlight:new("Hunt for Red Raktuber") } },
  {
    text = "North-east of Rellekka (north of Fremennik lodestone), travel via the small boat to the Iceberg.",
    title = "Penguin espionage",
    neededItems = {
      ["Penguin suit"] = { quantity = 1 },
      ["Silk"] = { quantity = 1 },
      ["Planks"] = { quantity = 1 },
      ["Thread"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Talk to Larry.", actions = { Action.ConversationHighlight:new("Alright, I guess so.") } },
  { text = "Enter penguin suit by selecting 'Tuxedo-time' option on Larry or using the clockwork suit on Larry." },
  { text = "North-west, investigate the avalanche." },
  {
    text = "Enter the west room, speak to KGP Interrogator.",
    actions = {
      Action.ConversationHighlight:new("Yes, I am."),
      Action.ConversationHighlight:new("My captain trusted me to deliver the message."),
      Action.ConversationHighlight:new("Your captain must trust you a great deal."),
      Action.ConversationHighlight:new("My captain found a greater leader."),
      Action.ConversationHighlight:new("What leader could possibly be greater than the Pescaling."),
      Action.ConversationHighlight:new("The sea may control your Captain, but he controls your life."),
      Action.ConversationHighlight:new("He will bring unity by destroying the humans."),
      Action.ConversationHighlight:new("Why not unite all penguins to The Sea, first."),
      Action.ConversationHighlight:new("He's only demonstrating his impatience."),
      Action.ConversationHighlight:new("Nothing can stop him"),
      Action.ConversationHighlight:new("If he comes to rescue you, he'll be captured."),
      Action.ConversationHighlight:new("'Our sacrifice'? Only you appear to be making the sacrifice."),
    },
  },
  { text = "Talk to captured penguin and note down the three dance emotes." },
  { text = "Search the crate behind you to get a conch shell, puffer, octopus, monkfish, and a ray." },
  {
    text = "Take note of which headwear the captured penguin is wearing.<ul><li>The fish hat will either be a puffer, octopus, monkfish, or ray fish.</li></ul>",
  },
  { text = "Talk to Ping & Pong. (north-east from interrogation room)" },
  { text = "Talk to the KGP Agent by the control panel to the north." },
  {
    text = "Return to Ping & Pong.",
    actions = {
      Action.ConversationHighlight:new("Chicklings"),
      Action.ConversationHighlight:new("Squirrels"),
      Action.ConversationHighlight:new("Blue"),
      Action.ConversationHighlight:new("Bears"),
      Action.ConversationHighlight:new("Kiss"),
      Action.ConversationHighlight:new("Sharks"),
      Action.ConversationHighlight:new("Yes, that sounds great!"),
    },
  },
  {
    text = "Return to the KGP Agent by the control panel. Sing him the lullaby.",
    actions = { Action.ConversationHighlight:new("I want to sing you a song.") },
  },
  { text = "Operate the control panel. Head north-east into the war room." },
  { text = "Talk to any dwarf.", actions = { Action.ConversationHighlight:new("Yes") } },
  { text = "Speak to the dwarf again." },
  { text = "Exit war room to get caught." },
  { text = "Talk to Larry (twice).", actions = { Action.ConversationHighlight:new("Alright, I guess so.") } },
  {
    text = "Re-enter the hideout via the avalanche and return to the dwarves.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Talk to them again and give them the supplies to get a toolbox." },
  {
    text = "Perform practice run on the engine panel north of the dwarves.<ul><li>Wrench -> wirebox.</li><li>Wire cutter -> wirebox.</li><li>Wire -> wirebox.</li><li>Tape -> wirebox.</li><li>Bellows -> pipe.</li><li>Click bellows twice, the pressure gauge will point to the red.</li><li>Turn valve wheel three times, the pressure gauge arrow points straight up in the green.</li><li>Pull lever.</li></ul>",
  },
  { text = "Talk to a dwarf again.", actions = { Action.ConversationHighlight:new("No.") } },
  { text = "Exit war room to get caught." },
  { text = "Talk to Larry.", actions = { Action.ConversationHighlight:new("Alright, I guess so.") } },
  { text = "North-west, talk to Noodle.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Re-enter the hideout via the avalanche. Enter first room to the east." },
  {
    text = "Navigate to the sorting table without being spotted using the picture to the right (see the full guide for more details).",
  },
  {
    text = "Search sorting table to get a telegram. Exit the small room and ensure the door is closed before you move further.",
  },
  {
    text = "Navigate back out of the office the way you came, without being spotted. Once back in the main hall, exit using the southern door. If you are caught you will lose the item and have to repeat the process.",
  },
  { text = "Return to Larry and talk to him twice." },
  {
    text = "Southeast of Yanille, just outside the city walls, talk to Larry.",
    title = "Disabling the Red Raktuber",
    actions = { Action.ConversationHighlight:new("Alright, I guess so.") },
  },
  { text = "Enter the penguin suit and wear the fish hat the captured penguin was wearing." },
  { text = "Blow the conch shell, then perform the three emotes." },
  { text = "Talk with a penguin, and they will tell you to report to Captain Marlin." },
  { text = "Climb up the ladder and walk all the way to the east, opening doors along the way." },
  { text = "Talk to Captain Marlin." },
  { text = "Pick either honest or lie.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "In the brig, walk west and search the hatstand to get an electric eel, shark tooth, crab claw, seaweed, and a swordfish.",
  },
  { text = "Open the east door." },
  {
    text = "Inspect the engine panel.<ul><li>Crab claw -> wirebox.</li><li>Shark tooth -> wirebox.</li><li>Electric eel -> wirebox.</li><li>Seaweed -> wirebox.</li><li>Pufferfish -> pipe.</li><li>Click pufferfish twice, the pressure gauge will point to the red.</li><li>Octopus -> Turn valve wheel three times, the pressure gauge arrow points straight up in the green.</li><li>Swordfish -> lever.</li><li>Pull the lever.</li></ul>",
  },
  {
    text = "Talk to Chuck towards the east side of the island to leave the island and get a cutscene.<ul><li>If you teleport off the island, talk to Chuck at the Ardougne Zoo to get teleported back to the Desert Island and talk to him again once there.</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Hunt for Red Raktuber",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1243900800,
  prereqQuests = { "Cold War", "Sea Slug" },
})
