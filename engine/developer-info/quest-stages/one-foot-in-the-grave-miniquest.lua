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
    title = "Starting off",
    text = "Check your bank for a Hand (Back to my Roots).<ul><li>If you do not have this then you can get it back from a RPDT employee in East Ardougne.</li><li>If that doesn't work, kill creatures in the jade vine maze instead, it should be a common drop. At least one empty inventory spot is required to be able to get it as a drop.</li><li>If that doesn't work, kill creatures in the jade vine maze instead, it should be a common drop. At least one empty inventory spot is required to be able to get it as a drop.</li></ul>",
  },
  {
    text = "With the hand go to the Wizards' Guild in Yanille and ring the bell outside on the eastern side of the guild.",
  },
  {
    text = "Speak to Zavistic Rarve.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Go to the Jade vine maze on Karamja." },
  {
    text = "Kill creatures inside the maze until you receive the six body parts. These do not show up on area loot.<ul><li>Foot</li><li>Torso</li><li>Left arm</li><li>Right arm</li><li>Left leg</li><li>Right leg</li></ul>",
  },
  {
    text = "Make sure you have at least six free inventory spaces as the items do not drop if your inventory is full.",
  },
  {
    text = "The pieces all drop in the order above. If one piece is not picked up, a duplicate drop can be received. Duplicates cannot be picked up.",
  },
  {
    text = "If you have been to the jade vine maze for another quest, check your bank for all the body parts. Some may have dropped prior to picking up this quest.",
  },
  {
    text = "Go back to the Wizards' guild and ring the bell to speak to Zavistic Rarve.",
    actions = {
      Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you."),
      Action.ConversationHighlight:new("Yes, I'm ready to go."),
    },
    postconditions = { Condition.ConversationText:new(" Okay, just click your heels three times and you'll be there.") },
  },
  { text = "Search Sandy's desk to receive a locked diary." },
  {
    text = "Click on the locked diary to unlock it.<ul><li>There is a rare chance that you may trigger a trap so it is advised you do this in a safe area as it can cause over 8,000 damage.</li></ul>",
  },
  {
    text = "Return to the Wizards' Guild and ring the bell to speak to Zavistic Rarve.",
    actions = {
      Action.ConversationHighlight:new("I have a rather sandy problem that I'd like to palm off on you."),
      Action.ConversationHighlight:new("Sure, I'd be honoured to attend."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
}

return Quest:new({
  name = "One Foot in the Grave (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1191888000,
  prereqQuests = { "The Hand in the Sand", "Back to my Roots" },
})
