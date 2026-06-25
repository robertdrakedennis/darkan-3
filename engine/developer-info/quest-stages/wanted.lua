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
    text = "Talk to Sir Tiffy in Falador park.",
    title = "Communicate with White Knights",
    neededItems = {
      ["Enchanted gem"] = { quantity = 1 },
      ["Law rune"] = { quantity = 1 },
      ["Molten glass"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Do you have any jobs for me yet?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to him again.", actions = { Action.ConversationHighlight:new("Ask about the Wanted! quest") } },
  {
    text = "Talk to Sir Amik Varze, 2nd floor[UK]3rd floor[US] of White Knights' Castle in the west wing. Decline his offer.",
    actions = {
      Action.ConversationHighlight:new("Speak about the Wanted! quest."),
      Action.ConversationHighlight:new("No, not right now..."),
    },
  },
  { text = "Return to Sir Tiffy.", actions = { Action.ConversationHighlight:new("Ask about the Wanted! quest") } },
  {
    text = "Talk to Amik Varze again.",
    actions = {
      Action.ConversationHighlight:new("Speak about the Wanted! quest."),
      Action.ConversationHighlight:new("Sure, I'll help you!"),
    },
  },
  {
    text = "Return to Sir Tiffy for a commorb.<ul><li>If you are buying a commorb</li><li>If you are making one</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Ask about the Wanted! quest"),
      Action.ConversationHighlight:new("Buy one"),
      Action.ConversationHighlight:new("YES"),
      Action.ConversationHighlight:new("YES"),
      Action.ConversationHighlight:new("Have One Made"),
      Action.ConversationHighlight:new("YES"),
    },
  },
  {
    text = "Right-click contact with the commorb to view your assignment details.",
    title = "Finding Solus Dellagar",
    neededItems = {
      ["Rune essence"] = { quantity = 1 },
      ["Pure essence"] = { quantity = 1 },
      ["Commorb"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Current Assignment") },
  },
  { text = "Head to the Taverley Dungeon and go to the south-westernmost area of the Black Knight's Base." },
  { text = "Talk to Lord Daquarius in the southwest room with the throne, south of the furnace icon." },
  { text = "Kill a Black Knight." },
  { text = "Talk to Lord Daquarius again." },
  {
    text = "Take twenty unnoted essence to the Mage of Zamorak in the south-east corner of Varrock.  Make sure not to wear god armour.<ul><li>If you don't already have the essence, have Aubury (in the rune shop north of the Mage of Zamorak) teleport you to the essence mine and mine the 20 essence.</li><li>If you are wearing any Saradomin or Guthix gear, you must remove it before speaking to the Mage.</li></ul>",
    actions = { Action.ConversationHighlight:new("Solus Dellagar") },
  },
  { text = "Talk again to give him the essence.", actions = { Action.ConversationHighlight:new("Solus Dellagar") } },
  {
    text = "Go to Canifis and you will be contacted, complete the conversation. If you accidentally exit out, right-click 'Contact' on the commorb to restart the dialogue.",
  },
  { text = "Scan using the commorb just outside the Canifis bank to find Solus." },
  {
    text = "An item will appear in your backpack. The table below shows where to scan next. Repeat this 5 times.<ul><li>If you are wearing one, take off your ring of life; it will not help you and will be destroyed.</li></ul>",
  },
  --TODO: Add table
  {
    text = "Once you have received the pure essence, teleport to the Rune Essence mine by talking to Carwen Essencebinder in Burthorpe (fastest way from Burthorpe lodestone) or Aubury in Varrock or Archmage Sedridor in the Wizards' Tower.",
    actions = { Action.ConversationHighlight:new("yes") },
  },
  { text = "Kill Solus Dellagar." },
  {
    text = "Give Sir Amik Varze the hat.",
    actions = { Action.ConversationHighlight:new("Speak about the Wanted! quest.") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Wanted!",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1129507200,
  prereqQuests = { "Recruitment Drive", "Enter the Abyss (miniquest)", "Morytania" },
})
