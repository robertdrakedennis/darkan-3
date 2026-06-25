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
    text = "Talk to Jones the fisherman outside the Fishing Guild bank.",
    title = "Getting Started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("You need an extra rower?"),
      Action.ConversationHighlight:new("You need a giant harpoon?"),
      Action.ConversationHighlight:new("You need a particular sea chart?"),
      Action.ConversationHighlight:new("Can you tell me more about the Thalassus?"),
      Action.ConversationHighlight:new("That's horrible!"),
      Action.ConversationHighlight:new("Actually, I'll be right back."),
    },
  },
  {
    text = "Pickpocket the Master fisher (near the guild entrance) for the sea chart. This may take several attempts.",
  },
  {
    text = "Talk to the Master fisher.",
    actions = { Action.ConversationHighlight:new("Ask about the Deadliest Catch.") },
  },
  { text = "Talk to Linza who is east of the Fishing Guild in Hemenster.", title = "Preparing a Hunt" },
  { text = "Go to the Tower of Life south of Ardougne. (Fairy ring DJP or Kandarin monastery Teleport)" },
  { text = "Talk to 'The Guns'." },
  { text = "Talk to Bonafido for builder's tea." },
  {
    text = "East of the tower, pick the southern most of the 3 lowland heather bunch directly east of the Tower of Life. Only one is needed.",
  },
  { text = "Use the heather on the tea." },
  {
    text = "Talk to 'The Guns'.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head back to the Fishing Guild and talk to Jones.",
    title = "Fish Finding",
    actions = { Action.ConversationHighlight:new("Lead the way!") },
    postconditions = { Condition.ConversationText:new(" Oi, Guns, we're off!") },
  },
  {
    text = "Track the Thalassus via the wreckage. Dismiss your follower if you have one.<ul><li>The track is always the same during the same session, even after a reset.</li><li>Optional: Each shipwreck has some loot, but you can only take 3 'items' in total. PC: Right click and loot; Mobile: Long press and loot.</li><li>Note: The remaining loot will sink when the loot window has been closed for that specific shipwreck.</li><li>Optional: Each shipwreck has some loot, but you can only take 3 'items' in total. PC: Right click and loot; Mobile: Long press and loot.</li><li>Note: The remaining loot will sink when the loot window has been closed for that specific shipwreck.</li><li>Note: The remaining loot will sink when the loot window has been closed for that specific shipwreck.</li></ul>",
  },
  {
    text = "Talk to the group of mermaids.",
    title = "The Mermaids",
    actions = {
      Action.ConversationHighlight:new("The Thalassus has eaten Jones the mighty fisherman."),
      Action.ConversationHighlight:new("It should, he's been hunting for decades."),
      Action.ConversationHighlight:new("Very horrid! After he's killed the Thalassus, he'll probably come after you."),
      Action.ConversationHighlight:new("Trust me, I'm a great adventurer, I should know."),
      Action.ConversationHighlight:new("Did I mention Jones has a harpoon with him?"),
      Action.ConversationHighlight:new("It's no ordinary harpoon."),
    },
    postconditions = { Condition.ConversationText:new(" What do you mean?") },
  },
  {
    text = "Track the Thalassus again. Note that it takes a different path this time.<ul><li>Optional: It is possible to loot 3 items again.</li></ul>",
    title = "Rescuing Jones",
  },
  { text = "Talk to Jones." },
  { text = "Talk to Linza. She is at her smithing corner like previously." },
  { text = "Talk to Jones again." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Deadliest Catch",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1307404800,
  prereqQuests = { "Tower of Life" },
})
