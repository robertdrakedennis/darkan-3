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
    text = "Speak to King Bolren in the Tree Gnome Village.",
    title = "Evil dumplings",
    neededItems = { ["Crystal saw"] = { quantity = 1 }, ["Small crystal seed"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Squeeze through the loose railing to the west." },
  {
    text = "Climb down ladder to the east.<ul><li>If you no longer have a key from the Waterfall Quest, go to the east side of the cave and search the off-colour crate to get one.</li><li>Note that the identically named a key also received from the Waterfall Quest will not work.</li></ul>",
  },
  {
    text = "Go to the west side of the cave and talk to Golrie.",
    actions = { Action.ConversationHighlight:new("Anti-illusion devices") },
    postconditions = { Condition.ConversationText:new(" Pretty good! I'm having fun with my pet hobgoblins.") },
  },
  { text = "Use the key on the northern gate." },
  {
    text = "Search the chest to obtain crystalline discs.<ul><li>You may use drop trick to obtain more discs. You should drop all the discs to obtain a new batch of discs.</li></ul>",
  },
  {
    text = "Talk to Golrie again.",
    actions = { Action.ConversationHighlight:new("Anti-illusion devices") },
    postconditions = { Condition.ConversationText:new(" Pretty good! I'm having fun with my pet hobgoblins.") },
  },
  { text = "Go to the eastern part of the cave and crawl through the tunnel (followers must be dismissed)." },
  { text = "Push the monolith north.", title = "Monolith puzzle" },
  {
    text = "Search chest to obtain more crystalline discs.<ul><li>You may use drop trick to obtain more discs. You should drop all the discs to obtain a new batch of discs.</li></ul>",
  },
  { text = "Go to the next set of monoliths and push the south-western one north." },
  { text = "Push the north-west monolith east." },
  {
    text = "Search the western chests to the north.<ul><li>Be sure you receive a key to a chest and Yewnock's notes.</li></ul>",
  },
  { text = "Push the small monolith south." },
  {
    text = "Right-click sing-glass on the singing bowl to transform the seed into a crystal chime.<ul><li>If you have a crystal saw instead of the small crystal seed, right-click revert-crystal on the singing bowl.</li></ul>",
  },
  { text = "Push the north-west monolith west." },
  { text = "Open the mahogany chest, then search it to obtain the strongroom key." },
  {
    text = "Push the south-east monolith west and open the western gate.<ul><li>If you are stuck, exit using either of the tunnels.</li><li>Enter the tunnel again from the other side.</li><li>Push south-western monolith north.</li><li>Open the western gate.</li></ul>",
  },
  { text = "Read the lectern (Chapter 1). Doing so unlocks a music track." },
  {
    text = "Referencing the table below, insert discs on the left side of the machine to equal the values on the right side. Use Yewnock's exchanger to obtain shapes of lesser values (if you are having trouble, see the full guide here).",
    title = "Oaknock's legacy",
  },
  {
    text = '<table><caption>Value of each shape</caption><tbody><tr><th rowspan="2">Colour</th><th colspan="6">Value</th></tr><tr><th>Circle</th><th>Triangle</th><th>Square</th><th>Pentagon</th></tr><tr><td style="color:red;"><b>Red</b></td><td>1</td><td>3</td><td>4</td><td>5</td></tr><tr><td style="color:orange;"><b>Orange</b></td><td>2</td><td>6</td><td>8</td><td>10</td></tr><tr><td style="color:#CCCC00;"><b>Yellow</b></td><td>3</td><td>9</td><td>12</td><td>15</td></tr><tr><td style="color:green;"><b>Green</b></td><td>4</td><td>12</td><td>16</td><td>20</td></tr><tr><td style="color:blue;"><b>Blue</b></td><td>5</td><td>15</td><td>20</td><td>25</td></tr><tr><td style="color:indigo;"><b>Indigo</b></td><td>6</td><td>18</td><td>24</td><td>30</td></tr><tr><td style="color:#8F00FF;"><b>Violet</b></td><td>7</td><td>21</td><td>28</td><td>35</td></tr></tbody></table>',
  },
  {
    text = "Drop all discs and the chest key as you no longer need them. They are not the same shapes that will be used in The Prisoner of Glouphrie.",
  },
  { text = "Exit through the way you came or teleport, returning to Tree Gnome Village." },
  { text = "Kill the evil creature near King Bolren. (If it respawns, hop worlds and try again.)" },
  { text = "Talk to the king." },
  { text = "Use the Spirit Tree to travel to the Tree Gnome Stronghold." },
  {
    text = "Climb to the 1st floor[UK]2nd floor[US] of the Grand Tree.",
    title = "Anima Mundi",
    neededItems = {
      ["Crossbow"] = { quantity = 1 },
      ["Mithril grapple"] = { quantity = 1 },
      ["Crystal chime"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Talk to Gianne Jnr in the north-west corner.",
    actions = { Action.ConversationHighlight:new("Longramble the explorer") },
    postconditions = { Condition.ConversationText:new(" Not yet.") },
  },
  {
    text = "Head south-west of Castle Wars until you reach a bridge crossing a river. Ring of dueling, or fairy ring BKP and head north-west, or Yanille lodestone for quick access.",
  },
  { text = "Just north-east of the bridge, grapple the tree to cross the river." },
  { text = "Talk to Longramble." },
  { text = "Head north and talk to the dying spirit tree." },
  {
    text = "After the cutscene, use the crystal chime on the spirit tree, or ring the crystal chime when next to the spirit tree.",
  },
  { text = "Go west into the large sewer entrance." },
  {
    text = "Travel through the dungeon until you reach a room with 4 doors, 2 warped tortoises, and a pit in the centre. (You may want to kill a warped tortoise/terrorbird for the achievement at this point as it is convenient to do so.)<ul><li>Running through the tar will turn your run off - simply turn it back on.</li><li>You can Surge or Dive across the tar to avoid being slowed down.</li></ul>",
    title = "Warped",
  },
  { text = "Enter the east room and kill the 3 warped terrorbirds." },
  {
    text = "Peek through the hatch door on the east side of the room.<ul><li>People with sensitivity to light, seizures, or other ailments concerning flashing lights should be cautious during the escape scene (about 3–4 minutes). Recommended to hold spacebar and look away. You must watch the cutscene. In the event you cancel it, simply re-enter the room.</li></ul>",
  },
  { text = "Speak to Hazelmere." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Path of Glouphrie",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1188777600,
  prereqQuests = { "The Eyes of Glouphrie", "Tree Gnome Village", "Waterfall Quest" },
})
