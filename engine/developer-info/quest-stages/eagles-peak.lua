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
    text = "Talk to Charlie in Ardougne Zoo.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Ah, you sound like someone who needs a quest doing!") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head to Eagles' Peak west of the Tree Gnome Stronghold.<ul><li>If Eagles' Peak lodestone is activated, teleport to it and run northwest.</li><li>Alternatively, teleport to the Memorial of Guthix using memory stands in the currency pouch and run southeast.</li></ul>",
    title = "Eagles' Peak",
  },
  { text = "Go to the northern side of the mountain's base to a campsite." },
  { text = "Inspect the books for a Bird book." },
  { text = "Read the book to obtain a Metal feather." },
  { text = "Climb the rocks attached to the mountain next to the campsite (25 Agility needed)." },
  { text = "Use the metal feather on the rocky outcrop." },
  { text = "Enter the cave, then head south until a cutscene with Nickolaus plays." },
  {
    text = "Shout-to Nickolaus.",
    actions = {
      Action.ConversationHighlight:new("The Ardougne zookeeper sent me to find you."),
      Action.ConversationHighlight:new("So it's not that you're trapped then?"),
      Action.ConversationHighlight:new("Could I help at all?"),
    },
  },
  { text = "Pick up 10 eagle feathers in the dungeon (called 'giant feathers' on the floor)." },
  { text = "Head to the south-east corner of Varrock to the Fancy Clothes Store.", title = "The disguise" },
  {
    text = "Talk to the Fancy-Dress shop owner.",
    actions = { Action.ConversationHighlight:new("Well, specifically I'm after a couple of bird costumes.") },
  },
  {
    text = "Have the owner make you two eagle capes and two fake beaks.",
    actions = {
      Action.ConversationHighlight:new("I've got the feathers and the materials you requested."),
      Action.ConversationHighlight:new("Okay, here are the materials. Eagle me up."),
    },
  },
  {
    text = "Equip your disguise (eagle cape and fake beak) and go back to the Eagle's Peak cave and shout to Nickolaus.",
  },
  {
    text = "Go to the intersection by the entrance of the dungeon and follow the east path.",
    title = "Golden feather",
  },
  { text = "Go through the tunnel entrance." },
  { text = "This quest is still a WIP. Use the wiki for this puzzle.", title = "The puzzle" },
  {
    text = "Take at least 6 handfuls of bird seed from the birdseed holder near the entrance of the cave.",
  },
  { text = "Put seed in feeder 1." },
  { text = "Pull lever 1." },
  { text = "Pull lever 2." },
  { text = "Put seed in feeder 3." },
  { text = "Put seed in feeder 4." },
  { text = "Pull lever 3." },
  { text = "Push-up lever 2." },
  { text = "Put seed in feeder 5." },
  { text = "Pull lever 4." },
  { text = "Put seed in feeder 2." },
  { text = "Walk down the western hallway and take the golden feather. " },
  { text = "Put seed in feeder 1." },
  { text = "Put seed in feeder 1." },
  { text = "Exit the tunnel (next to the birdseed holder)", title = "Silver feather" },
  { text = "Enter the middle tunnel to the west. (South of the entrance you originally used to enter the cave)" },
  {
    text = "Inspect the stone pedestal and complete the trail by inspecting the first pile of rocks to the east, then north-east.",
  },
  { text = "Inspect the opening in the wall to the north and kill the kebbit." },
  { text = "Take the silver feather dropped by the kebbit." },
  { text = "Exit this tunnel back into the main cave." },
  { text = "Go to the south-west corner of the main cave and enter through the entrance.", title = "Bronze feather" },
  { text = "Try to grab the feather from the pedestal." },
  { text = "Operate each winch in the 4 corners of the room in any order." },
  { text = "Grab the bronze feather from the pedestal." },
  { text = "Leave the tunnel into the main cave." },
  {
    text = "Back in the main cave, head directly east to a stone door with an eagle carved in it.",
    title = "The eagle door",
  },
  {
    text = "Use each feather on the door.<ul><li>Use the golden feather on the door</li><li>Use the silver feather on the door</li><li>Use the bronze feather on the door</li></ul>",
  },
  { text = "Open the stone door" },
  { text = "While wearing the disguise, Walk-past the giant eagle directly north" },
  { text = "Talk to Nickolaus." },
  {
    text = "Head back to the campsite by teleporting to Eagles' Peak lodestone and running northwest, or alternatively:<ul><li>Walk-past the eagle again</li><li>Exit the main cave back to eagles peak</li><li>Climb down the rocks</li></ul>",
    title = "Finishing up",
  },
  {
    text = "Talk to Nickolaus.",
    actions = {
      Action.ConversationHighlight:new("Well I was originally sent to find you because of a ferret."),
      Action.ConversationHighlight:new("That sounds good to me."),
    },
  },
  { text = "Watch the cutscene." },
  { text = "Take the ferret back to Charlie in the Ardougne Zoo." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Eagles' Peak",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1164672000,
  prereqQuests = {},
})
