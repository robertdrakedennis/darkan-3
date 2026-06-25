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
    text = "Search the north-east bookcase in the Seers' Village church.",
    title = "Starting out",
    neededItems = { ["Coal"] = { quantity = 1 }, ["Soft clay"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Read the ragged book." },
  {
    text = "Use soft clay on the book to get the key mould. If you do not have soft clay, you can mine some from the clay rocks in the western mine of the workshop then use it on the sink in the northern room.",
  },
  {
    text = "Enter the Elemental Workshop and go into the mine to the west. Mine 3 elemental rocks, killing the earth elementals that appear, then pick up the 3 elemental ores that they drop. Simply killing the earth elementals wandering around the mine will not yield the ore.",
  },
  {
    text = "In the fire elemental room (south), use the elemental ore on the furnace to create 3 elemental bars. 4 coal is required per bar.",
  },
  { text = "Use 1 elemental bar on the furnace again to make a ragged elemental key." },
  { text = "Climb down the hatch in the centre of the middle room." },
  {
    text = "Prime the remaining two elemental bars (see primed bar on how to do this) using the jig cart, but don't infuse them with the Mind machine.",
  },
  { text = "Go down the northwest stairwell." },
  { text = "In the middle of the hallway, open the 'Body door' on the western wall with the ragged elemental key." },
  { text = "Head down the stairs.", title = "The puzzle" },
  {
    text = "Label the levers as follows and use the interface to move them. The levers on the north and south are the same, and the levers east and west are the same so feel free to use the closest lever to your current orientation. You can use the Minigame window to hit the levers, you don't need to run to each one.",
  },
  {
    text = "The 'Reset' and 'Undo' levers can be found on the west wall (when the camera is not locked into the fixed overhead view; otherwise, they're to the north)",
  },
  {
    text = "Pull the levers in the following order:<ul><li>C, 2, F, 6</li><li>C, 2, F, 6</li><li>C, 2, E, 5</li><li>B, 3, F, 2</li><li>B, 3, F, 2</li><li>E, 4, F, 2</li></ul>",
    title = "First section",
  },
  { text = "A cutscene will play. Pull the reset lever." },
  {
    text = "Pull the levers in the following order:<ul><li>A, 5, C, 6</li><li>B, 5, D, 6</li><li>C, 5, E, 6</li><li>C, 5, F, 6</li><li>D, 5, E, 3</li><li>F, 4, E</li></ul>",
    title = "Second section",
  },
  { text = "No cutscene will play. Pull the reset lever." },
  {
    text = "Pull the levers in the following order:<ul><li>2, B, 6, A</li><li>5, B, 6, C</li><li>5, B, 2, C</li><li>5, B, 3, E</li><li>6, F, 5, B</li><li>3, C, 6, F</li><li>2, C, 6, E</li><li>3, B, 5, E</li><li>6, F, 4, E</li><li>6, F, 4, E</li><li>5</li></ul>",
    title = "Third section",
  },
  { text = "A cutscene will play. Pull the reset lever." },
  {
    text = "Pull the levers in the following order:<ul><li>2, C, 3, B</li><li>2, A, 3, E</li><li>2, B, 3, F</li><li>2, C, 3, F</li><li>2, C, 5, F</li><li>2, E</li></ul>",
    title = "Fourth section",
  },
  { text = "A message appears: 'you hear a crunching of gears'. Pull the reset lever." },
  {
    text = "Pull the levers in the following order:<ul><li>B, 2, A, 3</li><li>B, 2, F, 6</li><li>B, 2, D, 3</li><li>F, 6, C, 5</li><li>D, 6, F, 5</li><li>C, 6, F, 2</li><li>E, 5, F, 2</li><li>E, 5, D, 6</li><li>F, 4, E, 6</li></ul>",
    title = "Lowering the tower",
  },
  { text = "Many of the gears start moving and the machine makes a noise." },
  { text = "Pull the reset lever." },
  {
    text = "Pull the levers in the following order:<ul><li>4, E, 6, F</li><li>4, E, 6, A</li><li>5, B, 6, E</li><li>5, A, 6, F</li><li>5, B, 2, A</li><li>3, B, 4, C</li><li>2, B, 6, E</li><li>5, C, 2, D</li><li>3, E, 5, C</li><li>3, F, 6</li></ul>",
    title = "Spinning the towe",
  },
  { text = "Many of the gears start rotating and the central column stats rotating." },
  { text = "Do not pull the reset lever." },
  {
    text = "If your puzzle looks exactly like Figure 1, pull the levers in the following order:<ul><li>B, 4, C, 3</li></ul>",
    title = "Raising the tower",
  },
  {
    text = "If your puzzle looks like that image, but with the left and right sides reversed ('inverse position'), pull the levers in the following order:<ul><li>B, 4, C, 5</li></ul>",
  },
  {
    text = "A message appears: 'As the machine raises, you hear a whirring noise from the level above'. Do not pull the reset lever.",
  },
  {
    text = "Go upstairs (click the metallic stairs on the bottom-left or bottom-right of the screen), and make a body bar.",
    title = "Body body",
  },
  {
    text = "The metallic stairs may be blocked by your interface due to the camera angle, so closing out or resizing your interface may be needed.<ul><li>Put a primed bar in the slot next to the door.</li><li>Enter the room and flip the lever.</li><li>Push the button.</li><li>Flip the lever.</li><li>Retrieve the body bar from the slot.</li></ul>",
  },
  { text = "Go downstairs." },
  {
    text = "If your puzzle was in the 'guide position' mentioned above, pull the levers in the following order:<ul><li>2 (The tower drops)</li><li>4, B (The tower spins)</li><li>C, 3 (The tower rises)</li></ul>",
  },
  {
    text = "Or if your puzzle was in the 'inverse position', pull the levers in the following order:<ul><li>4, 3 (The tower drops)</li><li>4, B (The tower spins)</li><li>C, 3 (The tower rises)</li></ul>",
  },
  {
    text = "Go upstairs and make a second body bar.<ul><li>Put a primed bar in the slot next to the door.</li><li>Enter the room and flip the lever.</li><li>Push the button.</li><li>Flip the lever.</li><li>Retrieve the body bar from the slot.</li></ul>",
  },
  {
    text = "Use a workbench on the first-level basement (the level with water, air, and fire elementals where you can obtain elemental ore) to create a body body. The ragged book must be in your inventory.",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Elemental Workshop III",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1274745600,
  prereqQuests = { "Elemental Workshop II" },
})
