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
    text = "Talk to Adrasteia in the library on the 1st floor[UK] 2nd floor[US] of White Knights' Castle in Falador.",
    title = "To fight a god",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Enter Senntisten through the ancient door at the Varrock Dig Site." },
  {
    text = "Once inside, enter the Zamorakian Undercity by using the nearby pulley lift (a few steps east of the larger-looking pulley used to enter and exit Senntisten).",
    actions = { Action.ConversationHighlight:new("Story mode") },
  },
  { text = "Talk to Sir Cadian close to the entry." },
  {
    text = "Clear the Zamorakian Undercity on any difficulty.<ul><li>Players who have already cleared it may skip this step.</li></ul>",
  },
  {
    text = "Talk to Adrasteia in the library on the 1st floor[UK] 2nd floor[US]. She is not in the throne room.",
    title = "The edicts re-established",
  },
  {
    text = "Go to the throne room on the 2nd floor[UK] 3rd floor[US] and talk to Saradomin.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Talk to anyone again to continue the dialogue.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Travel to the Kharid-et Dig Site and talk to Dr Nabanik.",
    title = "The Zarosians",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Talk to Nex, Trindine, Wahisietel, or Azzanadra." },
  { text = "Talk to Armadyl in his tower south of Falador.", title = "Armadyl's regret" },
  {
    text = "Talk to Adrasteia in the library on the 1st floor[UK] 2nd floor[US], not the one in the throne room.",
    title = "Finishing up",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Aftermath",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1656892800,
  prereqQuests = { "Twilight of the Gods" },
})
