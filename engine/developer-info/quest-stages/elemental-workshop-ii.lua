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
    text = "Go to the Exam Centre in the south of the Archaeology Campus and search the bookshelf on the eastern wall in the Exam Centre.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>Use Archaeology journal to teleport to the Archaeology Campus just north of the Exam Centre.</li><li>If you end up with Varmen's notes, you searched the wrong bookshelf.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  { text = "Read the scroll inside the beaten book and keep the book." },
  { text = "Head to the building with an anvil west of the Seers' Village bank.", title = "The second key" },
  {
    text = "Click on the odd-looking wall to enter it.<ul><li>If it says 'You see a small hole in the wall but no way to open it', go to the building directly south and search the bookcase on the east wall for a battered key.</li></ul>",
  },
  { text = "Go down the stairs." },
  { text = "Go north to the water elemental room and search the machinery in the southwest of the room to get a key." },
  {
    text = "Obtain 2 elemental ores from the west room by attempting to mine the elemental rocks, and defeating the earth elementals that spawn.",
    title = "Elemental bars",
  },
  {
    text = "Go back to the north room (where you got the key) and pull the lever to start the water wheel (if the wheel is already turning, this step can be skipped).",
  },
  {
    text = "Go to the east room and pull the lever to start the bellows (if the bellows are already working, this step can be skipped).",
  },
  { text = "Smelt two elemental bars in the south room. ('Use' the ore on the furnace.)" },
  { text = "Open the hatch in the middle room, and climb down it." },
  { text = "Head to the building with an anvil west of the Seers' Village bank." },
  {
    text = "Click on the odd-looking wall to enter it.<ul><li>If it says 'You see a small hole in the wall but no way to open it', go to the building directly south and search the bookcase on the east wall for a battered key.</li></ul>",
  },
  { text = "Go down the stairs." },
  { text = "Go north to the water elemental room and search the machinery in the southwest of the room to get a key." },
  {
    text = "Obtain 2 elemental ores from the west room by attempting to mine the elemental rocks, and defeating the earth elementals that spawn.",
  },
  {
    text = "Go back to the north room (where you got the key) and pull the lever to start the water wheel (if the wheel is already turning, this step can be skipped).",
  },
  {
    text = "Go to the east room and pull the lever to start the bellows (if the bellows are already working, this step can be skipped).",
  },
  { text = "Smelt two elemental bars in the south room. ('Use' the ore on the furnace.)" },
  { text = "Open the hatch in the middle room, and climb down it." },
  {
    text = "Obtain 2 elemental ores from the west room by attempting to mine the elemental rocks, and defeating the earth elementals that spawn.",
  },
  {
    text = "Go back to the north room (where you got the key) and pull the lever to start the water wheel (if the wheel is already turning, this step can be skipped).",
  },
  {
    text = "Go to the east room and pull the lever to start the bellows (if the bellows are already working, this step can be skipped).",
  },
  { text = "Smelt two elemental bars in the south room. ('Use' the ore on the furnace.)" },
  { text = "Open the hatch in the middle room, and climb down it." },
  {
    text = "Take From the Schematics crate south of the stairs and take both options.",
    title = "Repairing a workshop",
  },
  { text = "Go back up the spiral stairs and smith a Crane claw on the workbench using 1 elemental bar." },
  { text = "Go back down and lower the Old crane using the lever directly next to the schematic crate." },
  { text = "Use the claw on the crane to repair it." },
  { text = "Place an elemental bar on the jig cart." },
  { text = "Pull the west lever by the schematics crate." },
  { text = "Pull the east lever." },
  { text = "Pull the west lever twice." },
  { text = "Pull the east lever." },
  { text = "Pull the west lever twice." },
  {
    text = "Pull the lever behind you, by the mine cart sign.<ul><li>If the lever is pulled and it says the system is not pressurised, go upstairs and activate the water wheel and bellows.</li></ul>",
  },
  { text = "Climb the staircase in the south-west corner.", title = "The press" },
  { text = "Move to the west side by the steam press." },
  {
    text = "Open the junction box and connect the pipes by clicking on a pair of pipe holes. (solution on the right)<ul><li>Top left and bottom right.</li><li>Top middle and top right.</li><li>Bottom left and bottom middle.</li></ul>",
  },
  {
    text = "Go downstairs and pull the lever next to the three small pipes.<ul><li>If successful, you will get a message that the bar has been flattened.</li></ul>",
  },
  { text = "Pull the lever by the mine cart sign." },
  {
    text = "Search all of the crates in both the area with the track and the upper level with the junction box to find 3 cogs and a pipe.",
    title = "The water tank",
  },
  { text = "Go back up the northeast platform stairs." },
  { text = "Go north of the water tank and 'use' the pipe item on the piping with a hole in it." },
  { text = "Go back down to the water tank and pull the 'old lever' in front of the tank's door to open it." },
  { text = "Turn the corkscrew lever (to the east) twice to load the elemental sheet into the water tank." },
  { text = "Pull the old lever to close the door." },
  { text = "Turn the water valve west of the old lever to open the valve." },
  { text = "Turn the water valve east of the old lever to raise the water level." },
  { text = "Wait for the water to fill the tank, then turn the east valve again, to lower the water level back down." },
  { text = "Turn the water valve west of the old lever to close off the water valve." },
  { text = "Pull the old lever to open the door." },
  { text = "Turn the corkscrew twice." },
  { text = "Pull the old lever to close the door." },
  { text = "Pull the lever by the mine cart sign." },
  { text = "Go over to the machine on the east side.", title = "The fan" },
  {
    text = "On the side of the machine are three pins, place the cogs on the correct pins<ul><li>The small cog goes on the upper left pin, the medium cog on the lower left pin, and the large cog on the right pin.</li></ul>",
  },
  {
    text = "Pull the lever just to the south to start the fan.<ul><li>If the message says the fan is sucking, the cogs are not on the correct pins. Turn the fan off and position the cogs correctly.</li><li>If the machine says 'It must be powered somewhere else', head upstairs and make sure the water wheel to the north is rotating. If not, pull the lever near the wheel.</li></ul>",
  },
  { text = "Pull the lever again." },
  { text = "Pull the lever by the mine cart sign." },
  { text = "Go by the claw and pick up the primed bar." },
  { text = "Go down another level in the workshop (north west stairs).", title = "Finishing up" },
  { text = "Enter the east door (mind door)." },
  {
    text = "Place the bar on the extractor gun. If you miss this step, you will take 10% damage by doing the next step.",
  },
  { text = "Operate the extractor hat." },
  { text = "Take the bar from the extractor gun." },
  { text = "Go back up to the top floor of the workshop." },
  { text = "On the workbench, smith a mind helmet, with the beaten book in the backpack." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Elemental Workshop II",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1159747200,
  prereqQuests = { "Elemental Workshop I" },
})
