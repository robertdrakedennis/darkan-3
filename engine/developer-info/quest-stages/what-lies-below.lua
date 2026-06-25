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
    text = "Talk to Rat Burgiss, south-east of the Varrock lodestone by the crossroads to Lumbridge.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Hello there!"),
      Action.ConversationHighlight:new("Shall I get them back for you?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Immediately west of the Grand Exchange is a camp. Kill 5 outlaws there.", title = "Helping Rat" },
  { text = "Take all 5 Rat's papers that they drop." },
  { text = "Use the 5 papers on an empty folder." },
  { text = "Talk to Rat Burgiss.", actions = { Action.ConversationHighlight:new("Hello there!") } },
  {
    text = "Go to the Varrock palace library, and talk to Surok Magis.<ul><li>Optional: search the bookcase south of the telescope in the north-east section of the room for the Dagon'hai History; it can be turned in to Historian Minas after the quest for 5 kudos.</li></ul>",
    title = "Surok Magis",
    neededItems = {
      ["Bowl"] = { quantity = 1 },
      ["Chaos runes"] = { quantity = 15 },
      ["Pure essence"] = {
        quantity = 1,
      },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Go on, then!") },
  },
  { text = "Use any method to enter the chaos altar." },
  {
    text = "With 15 chaos runes, use the wand on the altar.<ul><li>If you brought essence instead, craft the runes now.</li></ul>",
  },
  {
    text = "Give Surok Magis a bowl and the infused wand.",
    actions = { Action.ConversationHighlight:new("I have the things you wanted!") },
  },
  {
    text = "Talk to Rat Burgiss.",
    actions = {
      Action.ConversationHighlight:new("Hello there!"),
      Action.ConversationHighlight:new("Yes! I have a letter for you."),
    },
  },
  {
    text = "Talk to Zaff, in his shop north-west of Varrock Square.",
    actions = { Action.ConversationHighlight:new("Rat Burgiss sent me.") },
  },
  { text = "Talk to Surok Magis.", actions = { Action.ConversationHighlight:new("Bring it on!") } },
  { text = "Attack King Roald, and right-click summon with the beacon ring when he has 1 life point remaining." },
  {
    text = "After the cutscene, return to Rat Burgiss.",
    title = "Finishing up",
    actions = { Action.ConversationHighlight:new("Hello there!") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "What Lies Below",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1174953600,
  prereqQuests = {},
})
