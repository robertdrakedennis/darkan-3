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
    text = "Talk to Kennith along the east coast of Daemonheim near the Fremennik ship (Shadow reef).",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("What are you doing here?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Go west, and talk to him again." },
  {
    text = "Talk to Ezekial  then Eva (Temple Knight) and tell her you're ready to start.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'm ready. Let's go!"),
    },
  },
  { text = "Enter the opening formed in the mountain nearby." },
  { text = "Head east two rooms", title = "Daemonheim Dungeon" },
  { text = "Kill the three enemies in this room" },
  { text = "Find the blue key on the floor" },
  { text = "Return to the first room and unlock and enter the puzzle room door" },
  { text = "Choose a hero (preferably the one with the most health)." },
  {
    text = "One way to solve the puzzle is:<ul><li>Jump south across the first gap</li><li>Jump west twice</li><li>Jump south</li><li>Jump north, back to the same spot</li><li>Jump east three times</li><li>Jump south twice and the puzzle is completed.</li></ul>",
  },
  {
    text = "Inspect the Seeker of Truth and return across the gap. (Quickest method is to get caught and teleported back).",
  },
  {
    text = "Kennith will then teleport you to the Fishing Platform.",
    actions = { Action.ConversationHighlight:new("Teleport to Fishing Platform.") },
  },
  { text = "Once on the platform talk to Kennith.", title = "Fishing Platform" },
  {
    text = "Talk with Bailey.",
    actions = {
      Action.ConversationHighlight:new("*Punch him*"),
      Action.ConversationHighlight:new("So, you don't feel pain?"),
      Action.ConversationHighlight:new("No pain? Prove it."),
    },
  },
  { text = "Head east to the fishing spots." },
  { text = "Lure the fishing spot and go back to Kennith, the slug will follow you." },
  { text = "Talk to Kennith and he will give you a Seeker gland." },
  { text = "Combine the ingredients to make the anti-mind control serum." },
  {
    text = "Talk to Kennith, a little to the west.",
    actions = { Action.ConversationHighlight:new("What should we do now?") },
  },
  {
    text = "Talk to the Slug thralls.",
    actions = {
      Action.ConversationHighlight:new("Why can't we come in?"),
      Action.ConversationHighlight:new("I'm not a stranger."),
      Action.ConversationHighlight:new("I seek enlightenment in the joining."),
      Action.ConversationHighlight:new("I wish to hear..."),
    },
  },
  {
    text = "Leave one character by the Witchaven villager just north of the entrance and move the other 2 to the metal gate to the north-east and kill the Risen knight.",
    title = "Slug Citadel - First Room",
  },
  {
    text = "Use the character you left behind to knock out the Witchaven villager and proceed south with your player.",
  },
  { text = "Go into the room with the lever and pull it." },
  { text = "The metal door to the north will now be open. Send a character through." },
  { text = "Pull your player's lever again." },
  {
    text = "Use the character you left near the entrance to knock out the other villager again, allowing your player to run north to the other characters you left by the gate.",
  },
  {
    text = "Bring the character you left at the entrance to join your player. Use the character in the room to pull the lever to let you all in. Pull the lever again and run south, east, and then north to the exit while killing any risen knights in your path.",
  },
  { text = "Send everyone to the north as far as they can go while avoiding the villager.", title = "Second Room" },
  {
    text = "Pull the lever and send Kennith and your player into the room to the east. Send Ezekial to the western room and leave Eva in the central chamber.",
  },
  { text = "Leave Kennith in the eastern room and send your player south to pull the lever." },
  { text = "After the lever is pulled send Kennith into the easternmost room to pull the lever." },
  { text = "Have your player pull the southern lever again." },
  { text = "Send Ezekial west (if not already in the western room) then all the way south to pull the lever." },
  {
    text = "Make Kennith pull the lever again and then send him into the southernmost room on his side to pull the lever.",
  },
  { text = "Send your player back to the central chamber with Eva." },
  { text = "Make Ezekial pull his lever again and then send Kennith back to the central chamber." },
  { text = "Make Ezekial pull the lever once more and send him back to the central chamber." },
  { text = "Now that your party is regrouped, go to the next room." },
  { text = "Kill all of the Risen Knights.", title = "Third Room" },
  { text = "Send a character to each of the rooms in the four corners." },
  { text = "Pull all four levers in the following order: northeast, southeast, southwest, & then northwest." },
  { text = "Run to the newly opened gates and 'regroup' all of the characters. Proceed to the next room." },
  {
    text = "Use your player to investigate the strange device. Put your arm in.",
    title = "Fourth Room",
    actions = { Action.ConversationHighlight:new("Put your arm in.") },
  },
  {
    text = "Kill the first wave of knights with Kennith, Ezekial, and Eva. Your player will then investigate the second hole.",
  },
  { text = "Kill the second wave of knights. Your player will investigate the third hole." },
  { text = "Kill the third wave of knights." },
  { text = "Enter the tunnel to the north." },
  { text = "Use Kennith to speak to Brother Maledict to the west.", title = "Fifth Room" },
  {
    text = "Once the gate is open, knock down the wall in the west room with Ezekial and move Kennith north. Place him as close as possible to Mother Mallum while standing on the western ledge.",
  },
  { text = "Take control of Ezekial and knock down the wall to the east and proceed north." },
  { text = "Kill the knight and position Ezekial directly opposite Kennith." },
  { text = "Take control of Kennith and attack Mother Mallum." },
  { text = "You now have control of Eva. Talk to Mayor Hobbs and knock him out, then attack Mallum." },
  { text = "Move Eva north of the statue and topple it." },
  { text = "Talk to Lucy.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Leave the Citadel using the cave exit." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Salt in the Wound",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1309824000,
  prereqQuests = { "Kennith's Concerns" },
})
