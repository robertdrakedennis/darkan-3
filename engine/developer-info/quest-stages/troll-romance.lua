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
    text = "Go to the middle level of the Troll Stronghold.<ul><li>Enter the cave entrance from Trollheim and go down one level.</li><li>Otherwise, enter the secret entrance from the entrance to Fremennik Slayer Dungeon (fairy ring code AJR) or Death Plateau and go up one level.</li></ul>",
    title = "Hitch",
    neededItems = {
      ["Climbing boots"] = { quantity = 1 },
      ["Trollheim Teleport"] = { quantity = 1 },
      ["Trollheim tablet"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Talk to Ug on the middle level of the Troll Stronghold. Offer him your help.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Awww, you poor troll. What seems to be the problem?"),
      Action.ConversationHighlight:new("Don't worry now. I'll see what I can do."),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Aga in the room to the north.",
    actions = {
      Action.ConversationHighlight:new("So... how's your... um... love life?"),
      Action.ConversationHighlight:new("Errr... I've got to go."),
    },
  },
  {
    text = "Talk to Arrg in the same room.",
    actions = { Action.ConversationHighlight:new("Your girlfriend said you know where to find Trollweiss?") },
  },
  { text = "Talk to Ug again." },
  {
    text = "Teleport to Burthorpe and talk to Freda in the plateaus west of Burthorpe.",
    title = "Survival techniques",
    neededItems = {
      ["Rope"] = { quantity = 1 },
      ["Maple logs"] = { quantity = 1 },
      ["Yew logs"] = { quantity = 1 },
      ["Iron bar"] = { quantity = 1 },
      ["Swamp tar"] = { quantity = 1 },
      ["Cake tin"] = { quantity = 1 },
      ["Bucket of wax"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Do you know where I can find Trollweiss?"),
      Action.ConversationHighlight:new("That's all, thanks."),
    },
  },
  {
    text = "Talk to Dunstan east of the Burthorpe lodestone about a sled.",
    actions = { Action.ConversationHighlight:new("I need a sled!!"), Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Talk to him again to give him a rope, maple or yew logs, and an iron bar." },
  { text = "Use swamp tar on a wax bucket (with a cake tin in your inventory)." },
  { text = "Use your wax on the sled." },
  {
    text = "Enter the cave entrance opposite the ice gate, which located north-west of the main entrance to Troll Stronghold.<ul><li>Preferably use Trollheim Teleport here if you have it.</li><li>Otherwise, you need to go through the Troll Stronghold and past Trollheim.</li></ul>",
    title = "Sledding",
    neededItems = {
      ["Climbing boots"] = { quantity = 1 },
      ["Trollheim Teleport"] = { quantity = 1 },
      ["Trollheim tablet"] = { quantity = 1 },
      ["Sled (waxed)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Head north-west and exit the cave through the crevasse." },
  { text = "Head south, equip your sled and slide through the slope." },
  {
    text = "After the end of the first part of the ride, pick the Trollweiss flowers west of your location. Do not sled any further south at this point or you will have to climb the mountain again.",
  },
  { text = "Go back and talk to Ug." },
  {
    text = "Talk to Arrg and tell him you're here to kill him. The fight will start immediately after that. Arrg also attacks immediately after the teleport, so be prepared.",
    title = "The things we do for love...",
    neededItems = { ["Climbing boots"] = { quantity = 1 }, ["Trollweiss"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("I am here to kill you!") },
  },
  { text = "Kill Arrg." },
  { text = "Return to Ug and talk to him." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Troll Romance",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1104883200,
  prereqQuests = { "Troll Stronghold" },
})
