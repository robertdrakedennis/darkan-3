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
    text = "Charter a ship to Aminishi and talk to Ling.",
    title = "Creatures of Aminishi",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>If you have just completed Impressing the Locals, you may need to relog for the chat option to show up.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Kill creatures on the island until you receive 3 books.<ul><li>Ordered from best drop rate to worst, kill Anagami, Sakadagami or Sotapanna.</li><li>The 3 book names are:</li><li>The Path of the Dragon</li><li>The Path of the Monk</li><li>The Path of the Elemental</li><li>Pick up some spirit dragon charms because you will need 1-2 later. (Note: Ensure to have toggled off your spirit imp destroy option)</li><li>The Path of the Dragon</li><li>The Path of the Monk</li><li>The Path of the Elemental</li></ul>",
  },
  { text = "Return to Ling." },
  {
    text = "Enter The Well of Spirits on the middle tier to travel to the spirit realm.",
    title = "Spirit realm",
  },
  {
    text = "Head south-west and talk to Yulong on the shore. He is exactly where you would find Ling outside the spirit realm.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Return to Ling." },
}

return Quest:new({
  name = "Spiritual Enlightenment (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1469404800,
  prereqQuests = { "Impressing the Locals", "Meet the Assassin" },
})
