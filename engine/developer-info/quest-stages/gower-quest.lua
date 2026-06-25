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
    text = "Speak to the Gowers at their farm next to rats and a cabbage patch south-east of the Varrock lodestone.",
    title = "Gower's Farm",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("So about that quest, then?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Search the crate in the room to the west to obtain pure essence." },
  { text = "Search the largest water barrel to the west of the southern entrance to obtain a brussels sprout." },
  {
    text = "Search the scarecrow in the south-western part of the farm with the cabbages to obtain a cruciferous mounting.",
  },
  {
    text = "Search the food trough on the east side of the farm in the area with the giant rats to obtain a broccoli root.",
  },
  { text = "Spin the broccoli root into broccoli string on the spinning wheel in the main room." },
  { text = "Use the brussels sprout on the cruciferous mounting to make it into an unstrung brassican amulet." },
  { text = "Use the 'Flip' option on the pure essence to turn it into a life rune." },
  { text = "Use the broccoli string on the unstrung brassican amulet to create a brassican amulet." },
  { text = "Use the life rune on the brassican amulet to enchant it into a cabbagespeak amulet." },
  { text = "Equip the amulet and talk to Crispy the Cabbage south-west on the cabbage field." },
  {
    text = "Talk to the Gower brothers inside of the house.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Speak to the Cabbages again." },
  { text = "Speak to Andrew to receive the disk of returning." },
  { text = "Go to Dwarven Mines (entrance north-east of Falador lodestone).", title = "Black Hole Experience" },
  { text = "Go to the 'room' just before the south-westernmost one (Boot will be in the area)." },
  { text = "Scan with the disk of returning." },
  {
    text = "The room is intentionally black. Move your cursor around to find the hidden objects:<ul><li>Search the table for a rock.</li><li>Use the rock on the wall to the northwest.</li><li>Right click on the table to look under it for an orange.</li><li>Eat the orange for a key.</li><li>Use the small key on the bumpy wall on the right for a tinderbox.</li><li>Use the tinderbox on the scary wispy thing and take the skull.</li><li>Use the skull on the unusual shape to the west.</li><li>Enter The Gate of Lloigh-enn.</li></ul>",
  },
  {
    text = "Talk to Thordur.",
    title = "The Gate of Lloigh-enn",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Off I go, then."),
    },
  },
  {
    text = "Talk to Spiral Orb (the purple spider DJ in the middle).",
    title = "Lloigh-enn",
    actions = {
      Action.ConversationHighlight:new("What's up with the cabbage patches?"),
      Action.ConversationHighlight:new("I'll take a look around myself."),
    },
  },
  {
    text = "Look around in this area for 3 NPCs: Lucien, Steve the Chaos Elemental, and Thok.<ul><li>You can complete the following in any order.</li></ul>",
  },
  {
    text = "Talk to Lucien, north-west of spiral orb and next to central bank portal.",
    title = "Lucien",
    actions = {
      Action.ConversationHighlight:new("Can you help me fix the life altar?"),
      Action.ConversationHighlight:new("Not much, man."),
    },
  },
  { text = "Enter the Central Bank Portal." },
  { text = "Talk to Claire Hick." },
  { text = "Talk to Ernie." },
  { text = "Check out the Locked Vault Door." },
  { text = "Leave the area." },
  {
    text = "Talk to Sphenishchev, located south of the portal next to the bar, the penguin next to Beastmaster Durzag.",
    actions = { Action.ConversationHighlight:new("Will you help me break into the bank?") },
  },
  {
    text = "Talk to Tim or Crunchy, just south-west of Spiral Orb (the DJ).",
    actions = { Action.ConversationHighlight:new("Will you help me break into the bank?") },
  },
  { text = "Ask the bartender for 5 cups of tea and hand them out to all 5 Environment artists around the bar." },
  { text = "Return to Tim and Crunchy.", actions = { Action.ConversationHighlight:new("Everyone's been watered!") } },
  {
    text = "Talk to Romeo, southeast of Beastmaster Durzag.",
    actions = {
      Action.ConversationHighlight:new("Will you help me break into the bank?"),
      Action.ConversationHighlight:new("I'll see who I can find."),
    },
  },
  {
    text = "Talk to each God Wars Boss to the east.<ul><li>Commander Zilyana</li><li>K'ril Tsutsaroth</li><li>General Graardoor</li><li>Kree'arra</li><li>Nex</li></ul>",
  },
  { text = "Return to Romeo.", actions = { Action.ConversationHighlight:new("Yes, some people had ideas.") } },
  {
    text = "Talk to Guthix, south of the bar.",
    actions = { Action.ConversationHighlight:new("Will you help me break into the bank?") },
  },
  { text = "Talk to one of the cabbages on the north-side of the bar." },
  {
    text = "Return to Guthix.",
    actions = { Action.ConversationHighlight:new("Yes, they're very interested in working with you!") },
  },
  { text = "Enter the Central Bank Portal." },
  { text = "Talk to Claire Hick." },
  {
    text = "Talk to Ernie.",
    actions = {
      Action.ConversationHighlight:new("I have someone who wants to speak to you."),
    },
  },
  { text = "Check out the locked vault door, then enter it." },
  { text = "Open the Bank Safety box, then search it for a fragment." },
  { text = "Leave the bank area." },
  {
    text = "Talk to Chaos Elemental, north of the Grand Exchange Maintenance, southwest of the bar.",
    title = "Steve",
    actions = {
      Action.ConversationHighlight:new("Can you help me fix the life altar?"),
      Action.ConversationHighlight:new("NO! No, thank you."),
      Action.ConversationHighlight:new("Goodbye Steve!"),
    },
  },
  { text = "Enter the Grand Exchange Maintenance portal." },
  {
    text = "Repair the Grand Exchange pipes. (there are 3 different thickness pipes, each size must fit each other from the beginning to the end, the interface will close when it's correct)",
  },
  { text = "Leave the Grand Exchange area" },
  {
    text = "Talk to Thok, just north-west of Clawdia.",
    title = "Thok",
    actions = { Action.ConversationHighlight:new("Can you help me fix the life altar?") },
  },
  {
    text = "Enter Beta Room Portal and talk to Max.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'll get right on with that then."),
    },
  },
  {
    text = "Max all 3 skills.<ul><li>First room: You get options to rock, trot, gallop, turn. Just click on the action displayed on top of the screen.</li><li>Second room: Fire at other battleships (searching desert island and bubbles give more xp too).</li><li>Third room: Stay inside the bank on the white floor tiles.</li></ul>",
  },
  { text = "Talk to Max for the third fragment." },
  { text = "Leave the area." },
  { text = "Enter the Life Altar Portal south, opposite of the Beta Room Portal.", title = "Repairing the Life Altar" },
  { text = "Investigate Life altar." },
  {
    text = "Speak with the Black Knight Titan.",
    actions = { Action.ConversationHighlight:new("Can we just get on with the boss fight?") },
  },
  { text = "Kill it 6 times. 3 Before cutscene, 3 after." },
  { text = "Talk to any of the brothers." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Gower Quest",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = false,
  length = Enums.length.mediumlong,
  releaseDate = 1466380800,
  prereqQuests = {},
})
