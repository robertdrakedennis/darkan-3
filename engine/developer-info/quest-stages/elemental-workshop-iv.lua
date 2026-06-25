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
    text = "Search the north bookcase on the ground floor[UK]1st floor[US] of the Sorcerer's Tower to find a notched book.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Read the book completely. (8 pages)." },
  {
    text = "Pick up the knife on the ground next to you in the Sorcerer's Tower then head to the anvil near the bank in Seers' Village.",
  },
  {
    text = "Use a knife on the whetstone in the building of the Elemental Workshop, west of the bank, to receive a keyblade.",
  },
  { text = "Head down into the workshop." },
  {
    text = "Make 2 primed bars in the same way you did in the previous Elemental Workshop quests.<ul><li>Optional: Make 5 more if you want a full set.</li></ul>",
  },
  { text = "Head to the basement.", title = "The cosmic room" },
  { text = "Use the keyblade on the southern door." },
  { text = "Head through the door and east to the pillar room via the Cosmic door. (dismiss any followers)" },
  {
    text = "Solve the first puzzle (solution pictured right).<ul><li>Place the runes into the correct emitter locations</li><li>Operate the control panel south-east</li><li>Rotate the pillars using the controls interface (bottom right)</li><li>Pressing the circle on the control interface grid rotates the pillar. They have to match with the picture shown here</li><li>Press 'Power Up'</li><li>Pressing the circle on the control interface grid rotates the pillar. They have to match with the picture shown here</li></ul>",
  },
  {
    text = "Solve the second and third puzzles.<ul><li>Adjust the height of the emitters using the same Control Panel</li></ul>",
  },
  { text = "Operate the draining machine in the south-east corner of the room." },
  { text = "After the cutscene, use a primed bar on the Machine directly south of the control panel." },
  { text = "Correct the pillar that has changed position." },
  { text = "Power up the control panel again." },
  {
    text = "Operate the draining machine.<ul><li>Note: you might need to drink a dose of restoration potion (operating this machine drains your Runecrafting level)</li></ul>",
  },
  { text = "Take the cosmic bar." },
  {
    text = "Head upstairs to the workbench to make cosmic gloves. You can log out and log back in to quickly be placed back in the first room of the Elemental Workshop.",
  },
  { text = "Head back downstairs into the newly-opened chaos room.", title = "The chaos room" },
  {
    text = "Take the Shabby book from the table in the north-west corner. Read it and keep it in your inventory during the quest.",
  },
  {
    text = "Operate the CPU (using the 6 levers) and input the following commands in order:<ul><li>Make the automaton move cubes from shelves/belt by moving the cubes on the conveyor belt into the CPU (instruction cubes on belt are labelled get/put/break)</li><li>The first command will need to be aligned with the left side of the belt. Once in place, push the cube down the belt using the lever to 'input' the first command</li><li>The second command will need to be aligned with the right side of the belt. Once in place, push the cube down the belt using the lever to 'input' the second command</li><li>Get + Nature (grabs cube from shelf)</li><li>Put + Mind (this puts whatever he is holding onto the machine)</li><li>Put the cube with the fire rune on it onto the conveyor belt.</li><li>Get + Cosmic</li><li>Put + Mind</li><li>Put a blank token on the workbench.</li><li>Break + Law</li><li>Get + Fire</li><li>Put + Water</li><li>Carve the token.</li><li>Take the new astral token and use it on the broken astral token to replace it.</li><li>Put the cube with the astral rune on it onto the conveyor belt.</li><li>Get + Body</li><li>Put + Mind</li><li>Put the cube with the earth rune on it onto the conveyor belt.</li><li>Get + Astral</li><li>Put + Mind</li><li>This instructs Da-Vi to break the second wall, then turn the chaos machine on.</li><li>Break + Earth</li><li>Operate + Chaos</li><li>Get + Nature (grabs cube from shelf)</li><li>Put + Mind (this puts whatever he is holding onto the machine)</li><li>Get + Cosmic</li><li>Put + Mind</li><li>Break + Law</li><li>Get + Fire</li><li>Put + Water</li><li>Get + Body</li><li>Put + Mind</li><li>Get + Astral</li><li>Put + Mind</li><li>Break + Earth</li><li>Operate + Chaos</li></ul>",
  },
  { text = "Equip your cosmic gloves." },
  { text = "Use a primed bar on the machine, in the room with the Automaton." },
  {
    text = "Operate the draining machine (drains Thieving level).<ul><li>Requires level 39 Thieving. Use Super restore if needed.</li><li>If it says 'machine unpowered' input 'Operate+Chaos' again then try again.</li></ul>",
  },
  {
    text = "Take the chaos bar.<ul><li>Optional: repeat the last 3 steps for each primed bar if you want to make more chaos pieces.</li></ul>",
  },
  {
    text = "Head upstairs to a workbench to make chaos boots. Lobbying will automatically put you right next to the anvil.",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Elemental Workshop IV",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1298937600,
  prereqQuests = { "Elemental Workshop III" },
})
