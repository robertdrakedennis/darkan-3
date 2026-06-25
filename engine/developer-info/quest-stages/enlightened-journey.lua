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
    text = "If you don't have a sack of potatoes yet, fill an empty sack with potatoes in the farm north-east of Auguste. Pick the 6 that are there, wait for more to respawn, then pick 4 more. Click fill on the sack.",
    title = "Getting started",
    neededItems = {
      ["Papyrus"] = { quantity = 1 },
      ["Ball of wool"] = { quantity = 1 },
      ["Potatoes"] = { quantity = 1 },
      ["White candle"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Talk to Auguste on Entrana, north of the Herblore shop.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to him again.",
    actions = { Action.ConversationHighlight:new("Umm, yes. What's your point?") },
    postconditions = { Condition.ConversationText:new(" What's your point?") },
  },
  {
    text = "Talk to him for a third time.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        "(If the player has all the required materials:) Good, you have everything! Now, I need you to create an origami balloon. How do you make the origami balloon? First, use the papyrus on the ball of wool. The papyrus is folded into an origami box and the yarn will support the heat source. Next, add the unlit candle to the balloon structure. It will act as the heat source. Once you have done that let me know and we will begin our experiment.(Dialogue ends.)"
      ),
    },
  },
  { text = "Use a papyrus with a ball of wool." },
  { text = "Use an unlit candle on the balloon structure." },
  {
    text = "Talk to Auguste to launch the origami balloon. (Do not use the balloon's Launch option or you will need to make another.)",
  },
  {
    text = "Talk to Auguste again with two papyrus and a sack of potatoes.",
    actions = { Action.ConversationHighlight:new("Yes, I have them here.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Speak with Auguste once more and he will give you Auguste's sapling and basket of Apples." },
  {
    text = "Fill 8 sacks with sand at the sandpit just south-east of Auguste",
    title = "The Big Balloon",
    neededItems = {
      ["Yellow dye"] = { quantity = 1 },
      ["Red dye"] = { quantity = 1 },
      ["Silk"] = { quantity = 1 },
      ["Bowl"] = { quantity = 1 },
      ["Sacks"] = { quantity = 1 },
      ["Papyrus"] = { quantity = 1 },
      ["Potatoes"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Give Auguste the items listed above.",
    actions = {
      Action.ConversationHighlight:new("Yes, I want to give you some items."),
      Action.ConversationHighlight:new("Dye."),
      Action.ConversationHighlight:new("Sandbags."),
      Action.ConversationHighlight:new("Silk."),
      Action.ConversationHighlight:new("Bowl."),
      Action.ConversationHighlight:new("Fine thanks."),
    },
  },
  { text = "Bank for the willow branches. Go back and use the branches on the balloon frame on the platform." },
  { text = "Obtain at least 10 normal logs from nearby trees." },
  {
    text = "Talk to Auguste to begin the flight.<ul><li>You must take all items from your BoB and dismiss it at this point to begin the flight.</li></ul>",
    title = "In Flight",
    actions = { Action.ConversationHighlight:new("Okay.") },
  },
  {
    text = "In the event the client doesn't load the interface properly, you will need to restart, requiring another 10 logs.",
  },
  { text = "Talk to Auguste." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Enlightened Journey",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1162771200,
  prereqQuests = {},
})
