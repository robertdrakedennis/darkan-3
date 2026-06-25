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
    text = "Find the scribbled note while raiding the 30-35 Abandoned 2 floors of Daemonheim, and recover memory of it.",
    title = "Beginning of the story",
  },
  {
    text = "Talk to Skaldrun and ask him to tell you a story.<ul><li>You need to bank everything, including the ring of kinship.</li><li>If the option to start this saga is not available, that means you haven't found the scribbled note on the floor inside Daemonheim.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Forgot what he says here"),
      Action.ConversationHighlight:new("Tell me a story"),
    },
  },
  { text = "Kill Stomp like you would in a normal dungeon.", title = "Starting room" },
  { text = "As Thok, continue down the ladder and kill The Thing with eyes.", title = "Thok" },
  { text = "Continue east and Exorcise Dark Spirit to the east.", title = "Big Rock Room" },
  { text = "Kill the 2 enemies in the next room and continue south." },
  { text = "Kill the 3 enemies in the next room and continue east." },
  {
    text = "Pick up the Silver curvy thing to the southeast and return back to the starting room to unlock the Silver wibbly thing.",
  },
  {
    text = "Activate the big rock and kill all the squishy ghosts until the monolith is fully charged then continue north.",
  },
  { text = "Head west and kill the coward with a bow.", title = "Coward with a bow" },
  {
    text = "Once he has been killed, run back to the room where the Silver curvy thing was found (marked by the summoning icon on the minimap).",
  },
  { text = "Next, continue through the rooms to the east, north and east again." },
  {
    text = "Take the Red block from the floor.<ul><li>If additional food is required, Crunchy rune rocks can be acquired from the supply table.</li></ul>",
  },
  { text = "Run back through the rooms: west, south, and 2 more rooms west.", title = "Bulky Warrior" },
  { text = "Enter the southern door, then go west by unlocking the Red wibbly thing." },
  { text = "Attempt to imbue the tiles 3 times. On the third try, Thok will manage to imbue all the tiles correctly." },
  { text = "Take the Blue pointed thing in the northwestern corner of the room." },
  {
    text = "Go into the next room with the Bulky warrior. Kill him and move out of the direction he is facing if he prepares to charge.",
  },
  {
    text = "Once the bulky warrior has been defeated, return to the room with the summoning icon indicated on the minimap.",
  },
  { text = "Unlock the Blue wibbly thing.", title = "Armoured cow thing" },
  { text = "Upon entering the room, take a pickaxe from the rock and equip it." },
  { text = "Attack the Armoured cow thing until its armour is destroyed." },
  { text = "Continue the fight with Thok's sword." },
  { text = "After the fight, go back to the room with the supply table (North-East side)." },
  {
    text = "In the room to the north, kill the Ramokee skinweaver first, then move on to killing the other ramokees.",
    title = "Gobby demon",
  },
  { text = "Moving to the next room, talk to Thok-blocker until it allows you to enter the room." },
  { text = "Pick up the Orange stick thing in the southeast corner of the room, and return to the room to the east." },
  { text = "Unlock the Orange wibbly thing to face Gobby demon." },
  {
    text = "Thok should be able to kill the demon before any of its attacks are used. However, if portals appear on the ground, move away from them and continue attacking.",
  },
  { text = "Go back through the door and take the door to the west." },
  {
    text = "Kill Warped Gulega. Avoid the 1 lifepoint attack by moving 1 square away when tentacles appear on the ground.",
    title = "Finishing up",
  },
  {
    text = "Talk to Pretty Lass.",
    actions = {
      Action.ConversationHighlight:new("[Tell her about Marmaros]"),
      Action.ConversationHighlight:new("[Take her to Marmaros]"),
    },
  },
  {
    text = "Kill the Walking ice cube.<ul><li>Every 33% damage dealt it will encase itself in ice and running around the dungeon will avoid the player from being hit.</li></ul>",
  },
}

return Quest:new({
  name = "Thok It To 'Em (saga)",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1306886400,
  prereqQuests = { "Skaldrun" },
})
