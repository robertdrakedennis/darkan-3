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
    text = "Equip your ghost speak amulet and Talk to Summer at the Wilderness wall, north-east of Varrock.",
    title = "Luring the beast",
    neededItems = {
      ["Jennica's ring"] = { quantity = 1 },
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Pickaxes"] = { quantity = 1 },
      ["Summoning pouches"] = { quantity = 1 },
      ["Blue charm"] = { quantity = 1 },
      ["Monkfish"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Stand on the white portal.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "After the dialogue, equip and right-click operate Jennica's ring while standing on the portal to return to the real world.",
  },
  { text = "Run north to the dungeon, right-click and collapse the entrance." },
  { text = "Operate Jennica's ring while standing on the white portal to return to the spirit realm." },
  {
    text = "Run north and lure the beast to the entrance with your blue charm familiar. Enter the cave before it has a chance to consume your familiar.",
    actions = { Action.ConversationHighlight:new("Yes, I am.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Avoid the beast's ranged attacks by moving away from the missiles.", title = "Inside the cave" },
  {
    text = "Chop roots along the walls to obtain cursed willow logs. You will need 15 in total.<ul><li>(Optional) Right-click use an extra log on the middle fire pit, and then lure the Beast behind it to avoid attacks while cutting the remaining logs.</li></ul>",
  },
  {
    text = "Right-click use 5 logs on one of the fire pits and then light it.<ul><li>Do not light other fire pits during cutscene.</li></ul>",
  },
  {
    text = "Repeat for a total of three fire pits.<ul><li>If you die during the encounter your progress will be saved but unlit logs will be cleared.</li></ul>",
  },
  { text = "(Optional) Restock your food and return to Summer to continue." },
  {
    text = "Enter the next room by going through the passage in the south-east corner.",
    title = "The dark core",
    neededItems = { ["Energy potion"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes, I know what to do.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Avoid the missiles like before." },
  {
    text = "Soon a dark core will appear, dig soft soil and lure the core into the hole.<ul><li>The core may take several minutes to appear.</li><li>Alternatively, rotate in a circle around the room digging.</li></ul>",
  },
  {
    text = "Repeat this 3 times until you receive a cutscene.<ul><li>If you die during this encounter, your progress is saved after each trapping.</li></ul>",
  },
  {
    text = "Enter the next room by going through the passage to the east.",
    title = "The coloured graves",
    actions = { Action.ConversationHighlight:new("Yes, I am ready.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Take note of which grave the yellow spirit enters." },
  {
    text = "Wait for the Beast to charge a yellow attack and bless the grave marker.<ul><li>Blessing a grave consumes prayer points, which may be replenished at the altar in the room.</li></ul>",
  },
  { text = "Repeat for the red and blue spirit." },
  {
    text = "Talk to the spirits  then once outside, talk again.",
    title = "Finishing up",
    actions = { Action.ConversationHighlight:new("Alright, I'll come with you.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Summer's End",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1221436800,
  prereqQuests = { "Spirit of Summer" },
})
