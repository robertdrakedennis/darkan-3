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
    text = "Talk to Olaf Hradson north-east of Rellekka.",
    title = "Family honour",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Go east up the icy mountain and chop the windswept tree." },
  { text = "Return and talk to Olaf." },
  {
    text = "Go to Rellekka and talk to Ingrid Hradson next to the well in the south-east.",
    title = "The windswept tree",
  },
  { text = "Head north and talk to Volf Olafson who is wandering outside the helmet shop." },
  { text = "Return and talk to Olaf." },
  { text = "Use the damp planks on the embers on the ground next to him." },
  {
    text = "Talk to Olaf.",
    actions = { Action.ConversationHighlight:new("Alright, here, have some food. Now give me the map.") },
    postconditions = { Condition.ConversationText:new("Player has one piece of food removed from them.") },
  },
  { text = "Read the map." },
  {
    text = "Go back up the icy mountain and dig west of the windswept tree (left-click a pile of snow).",
    title = "Brine Rat Cavern",
  },
  {
    text = "Once inside the caverns, follow the path east and then north, continuing along the northern path until you reach a 'picture wall' door.",
  },
  { text = "Kill a nearby skeleton and pick up the key that it drops from the ground." },
  {
    text = "Search the door and solve the puzzle:<ul><li>Down</li><li>Right</li><li>Left</li><li>Up</li><li>Bottom</li></ul>",
  },
  { text = "Search the door again to pass through." },
  { text = "In the next area, pick up two rotten barrels and six ropes.", title = "Resilience" },
  {
    text = "Head towards the north-east walkway made of planks and use rotten barrels on the gaps in the walkway.<ul><li>Make sure you walk over all obstacles here, running will give you a 100% failure rate.</li><li>Toggle by clicking the Energy button.</li></ul>",
  },
  {
    text = "Open the gate by clicking the keyhole that matches the key (e.g. if the bottom of the key is triangle, then it goes into the triangle key slot).<ul><li>If you were running, which causes you to slip and fall after this step re obtain the key before trying again.</li></ul>",
  },
  { text = "Attempt to open the chest inside the wrecked ship." },
  { text = "Kill Ulfric.<ul><li>If he spawns again, re-open the chest before killing him a second time.</li></ul>" },
  { text = "Attempt to open the chest again and you'll receive a parchment." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Olaf's Quest",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1176163200,
  prereqQuests = { "The Fremennik Trials" },
})
