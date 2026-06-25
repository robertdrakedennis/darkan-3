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
    text = "Right click on the Ring of kinship to teleport.",
    title = "Starting room",
  },
  {
    text = "Talk to Skaldrun and ask him to tell you a story.<ul><li>You need to bank everything, including the Ring of kinship</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes"), Action.ConversationHighlight:new("Tell me a story") },
  },
  { text = "Control Ariane, pick up the dusk eels from the tables." },
  {
    text = "As Ariane, enter the south door, kill all the enemies and pick up the dusk eels.",
    title = "Guardian room #1",
  },
  { text = "Pick up the gold charm and heim crabs but do not eat them." },
  { text = "Control Sir Owen and check the rocky debris for a novite ore." },
  { text = "Enter the south door." },
  {
    text = "Control Ozan and unlock the door by stepping on each of the 4 pressure plates.<ul><li>The puzzle is different for everyone and every time.</li><li>You can slide diagonally.</li></ul>",
    title = "Ice puzzle room",
  },
  { text = "Open the east door." },
  { text = "Quickly switch to Ariane once in the next room." },
  { text = "As Ariane, kill all the enemies.", title = "Bridge room" },
  { text = "As Ariane, make a cub skinweaver by using the gold charm on the summoning obelisk." },
  { text = "As Ariane, talk to the cub skinweaver." },
  { text = "Enter the east door." },
  {
    text = "As Ariane, flip all tiles until all yellow or green. You can right click 'force' to flip individual tiles.",
    title = "Tile flip room",
  },
  {
    text = "As Ariane, enter the north door, quickly pick up the green rectangle key, and exit the room.",
    title = "Mysterious shade room",
  },
  { text = "Switch to Ozan and return to the starting room without teleporting (west, west, north, north)." },
  { text = "Switch to Ariane.", title = "Magic imbue room" },
  { text = "Unlock the green rectangle door and enter." },
  { text = "'Wait' on each of the pressure pads for Owen and Ozan." },
  { text = "Enter the south door." },
  { text = "Take the crimson triangle key.", title = "Strange crystal room" },
  { text = "Right-click 'Get-premonition' on any strange crystal." },
  {
    text = "Touch the following crystals in order: South red (Easternmost of the Southern pair), 2x West blue (Northernmost), North green (Westernmost) , West green (Southernnmost) , South yellow (Westernmost)",
  },
  { text = "Enter the north door." },
  { text = "Right-click call Owen and Ozan to walk them off the pressure plates." },
  { text = "Enter the west door." },
  { text = "As Ariane, unlock the crimson triangle door and enter it." },
  { text = "As Ariane, kill all the enemies and pick up the dusk eels.", title = "Guardian room #2" },
  { text = "Control Ozan, and enter the east door." },
  {
    text = "As Ozan, talk to the guard.",
    title = "Door guard room",
    actions = {
      Action.ConversationHighlight:new("[Charm] What are you guarding?"),
      Action.ConversationHighlight:new("Isn't guarding a door in these dungeons a little...redundant?"),
      Action.ConversationHighlight:new("Who put you up to guarding the door?"),
      Action.ConversationHighlight:new("Why are you bothering to guard it?"),
    },
  },
  { text = "Enter the east door." },
  {
    text = "Control Owen and push the statues into place.<ul><li>The four southern statues should match the position of the statues in the north.</li></ul>",
    title = "Statue puzzle room",
  },
  { text = "Enter the east door and pick up the fractite pickaxe." },
  { text = "Return west to the guard room and mine the zephyrium rock hidden behind the northwest pillar." },
  { text = "Return east to the statue room." },
  { text = "Control Ozan and enter the northern door.", title = "Agility room" },
  { text = "Proceed through the traps (Ozan will dodge them all) and investigate the broken longsword." },
  { text = "Control Ariane and return to the previous room." },
  { text = "Enter the southern door, kill the enemies and pick up the dusk eel.", title = "Guardian room #3" },
  { text = "Enter the east door and pick up the gold crescent key." },
  {
    text = "Talk to Pikkupstix (in the northwest corner of the room).",
    actions = { Action.ConversationHighlight:new("[Leave]") },
  },
  { text = "Return to the room north of the starting room without teleporting (west, north, west, west)." },
  { text = "Unlock the gold crescent door north of the starting room." },
  {
    text = "Enter the room and continue through the dialogue.",
    title = "Confronting Carn",
    actions = {
      Action.ConversationHighlight:new("Sir Owen"),
      Action.ConversationHighlight:new("Ozan"),
      Action.ConversationHighlight:new("Ariane"),
    },
  },
  { text = "Kill the two arctic bears and then kill Carn." },
  { text = "Once he is killed, choose any chat option." },
}

return Quest:new({
  name = "Three's Company (saga)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1306886400,
  prereqQuests = { "Skaldrun" },
})
