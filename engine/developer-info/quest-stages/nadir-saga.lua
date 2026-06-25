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
    title = "Getting started",
    text = "Start by finding a damaged device on Daemonheim floors 18-22.<ul><li>You might want to check whether the Nadir saga is already unlocked (see next step).</li></ul>",
  },
  {
    text = "Then ask Skaldrun south-west of the Daemonheim entrance to share their story.<ul><li>You can also right click to Start-Saga, and choose Nadir saga.</li></ul>",
  },
  { text = "Wait approximately one minute for Zemouregal and Lucien to finish their conversation." },
  { text = "Finish the dialogue and cutscene then continue north to interrogate the merchant." },
  {
    text = "Hunt for 3 memories (having sound turned on helps a lot here).<ul><li>Move your mouse around until the orange circle rapidly beats on one spot and click. There are three speeds--you want to click at the fastest, highest-pitched ping (there are three settings). Repeat until all three memories are recovered.</li><li>If only half the memory is recovered, you did not do this step correctly and as a result an unabridged tome will not be awarded, you can lobby to restart the saga.</li><li>A pinging sound will play if game sounds are turned on: a slow hum (level 1), a moderately fast ping (level 2, indicating you are close to the correct position), and a fast, high-frequency ping that plays ~2 times a second. This is the correct location for full memory retrieval.</li><li>The correct location does not always correspond to the centre of a memory you can see. It can also be an empty space.</li><li>Note: On mobile, there is a bug, read the full article to find out how to deal with this.</li></ul>",
  },
  { text = "Continue north and climb down the dungeon entrance." },
  { text = "Continue east through the door.", title = "Frozen floors" },
  { text = "Attempt to pass the captain." },
  { text = "Probe 3 memories again." },
  { text = "Continue through the east door then south twice." },
  {
    text = "Head north through the door and about half way into the room to get a message then leave the same way.",
    title = "Furnished floors",
  },
  {
    text = "Continue south through the door and finish dialogue with the soldier.<ul><li>Select 'Got something to get off your chest, soldier? Be my guest.'</li></ul>",
  },
  {
    text = "Continue south and then west and finish dialogue with the mage.<ul><li>Select 'Ask me later, Druf. I have no time for you right now.'</li></ul>",
  },
  {
    text = "Continue south through the door and finish dialogue with the soldier.<ul><li>Select 'Tell them to wait there, and no practice until I return.'</li></ul>",
  },
  {
    text = "Continue east.<ul><li>If you enter combat in this room then you have failed the dialogue options above, restart from checkpoint.</li></ul>",
  },
  { text = "Continue east, south, and south." },
  { text = "Probe 3 memories again." },
  { text = "Kill the necrolord and continue south." },
  { text = "Head east and east again to the room with bodies to view a cutscene.", title = "Occult floors" },
  {
    text = "Exit the room and continue south, then finish the dialogue with the necrolord.<ul><li>Select 'Cut off their hands.'</li></ul>",
  },
  {
    text = "Continue south, west and finish dialogue with the necrolord again.<ul><li>Select 'Kill him, or I will kill you.'</li></ul>",
  },
  {
    text = "Continue west and drain power from both portals by standing near them and continue south then finish dialogue with the necrolord again.<ul><li>Select 'His body is on the floor above. Fetch it yourself, I am busy.'</li></ul>",
  },
  {
    text = "Continue east.<ul><li>If you enter combat in this room then you have failed the dialogue options above, restart from checkpoint.</li></ul>",
  },
  {
    text = "Continue east, south, and then east.  (Do not fret if you drain the portal prior to this room, or future rooms, it will not fail the unabridged saga)",
  },
  { text = "Kill the Hooved Mage and then drain the portal." },
  { text = "Continue east, kill the four soldiers and two rangers, and then continue east." },
  {
    text = "D O  N O T let your adrenaline or special attack energy (Legacy Mode) run out, otherwise you will not receive an unabridged tome. Restart from last checkpoint if you happen to fail. The Persistent Rage relic will recharge your adrenaline if you are out of combat.",
    neededItems = { ["Standard spells"] = { quantity = 1 } },
    recommendedItems = { ["Persistent Rage relic"] = { quantity = 1 } },
    title = "Warped floors",
  },
  {
    text = "Drain the power from the portal, then head south and kill the two rangers and three soldiers.",
    recommendedItems = {},
  },
  { text = "Head east and unlock the Silver shield door, then drain the north west portal before continuing." },
  {
    text = "Continue east and kill the three soldiers, one ranger, and one mage. Drain the portal power and continue south, drain the portal, and then continue south again.",
  },
  { text = "Kill the two hellhounds and two soldiers, unlock the Green pentagon door, and then drain the portal." },
  {
    text = "Continue west and kill the three soldiers, one ranger, and one mage. Drain the portals and continue west.<ul><li>Your adrenaline will stay constant and will no longer drain. Your adrenaline may drain during the following cutscene, but full completion is still awarded.</li></ul>",
  },
  {
    text = "Continue east, continue dialogue and investigate the Ritual marker.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Talk to Bilrach.",
    actions = {
      Action.ConversationHighlight:new("Why is the Ritual Marker here?"),
      Action.ConversationHighlight:new("What is this place? What is it for?"),
      Action.ConversationHighlight:new("How long have you been here?"),
      Action.ConversationHighlight:new("Tell me of the portals and their power."),
      Action.ConversationHighlight:new("How did you know I was talking to Lucien?"),
      Action.ConversationHighlight:new("What happens now?"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Saga complete!" },
}

return Quest:new({
  name = "Nadir (saga)",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1335225600,
  prereqQuests = { "Skaldrun", "Ritual of the Mahjarrat" },
})
