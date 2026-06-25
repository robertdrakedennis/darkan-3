local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

---@type QuestStep[]
local steps = {
  { text = "Go to the Troll Stronghold, head south then downstairs into the kitchen.", title = "The adventurer" },
  { text = "Speak to Burntmeat.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Alright, I'll lend him a hand.") },
  },
  { text = "Talk to My Arm, east of Burntmeat." },
  {
    text = "Travel to Burthorpe and head north-west past Troll Invasion.",
    title = "Getting the goutweed",
    neededItems = {
      ["Climbing boots"] = { quantity = 1 },
      ["Ugthanki dung"] = { quantity = 1 },
      ["Supercompost"] = { quantity = 1 },
      ["Bucket"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Climb the rocks and head east past the thrower trolls to reach the Death Plateau." },
  {
    text = "Search the cooking pot to obtain a goutweedy lump.<ul><li>If you receive the message '... but you can't reach very far into it.', use an empty bucket on the cooking pot.</li></ul>",
  },
  { text = "Return to the Troll Stronghold kitchen and speak to My Arm." },
  { text = "Once on the surface, talk to him near the farming patch." },
  { text = "Read the farming manual he gives you, then talk to him again." },
  { text = "Use the 3 buckets of ugthanki dung and 7 buckets of supercompost on the soil patch." },
  {
    text = "Talk to My Arm for a cutscene.",
    actions = { Action.ConversationHighlight:new("This is My Arm. We'd like to go to Karamja.") },
  },
  { text = "Talk to Captain Barnaby if you find yourself in Ardougne.", title = "Getting the tubers" },
  { text = "Talk to My Arm south-east of the Karamja lodestone. He is east of Jiminua's Jungle Store." },
  {
    text = "Talk to Murcaily, guarding the tree grove north-east of Tai Bwo Wannai.",
    actions = { Action.ConversationHighlight:new("A troll called My Arm wants a favour...") },
  },
  { text = "A cutscene will play. Go through the dialogue." },
  {
    text = "Return to the roof of the Troll Stronghold where the soil patch was.",
    title = "Teaching a troll to farm",
    neededItems = {
      ["Spade"] = { quantity = 1 },
      ["Rake"] = { quantity = 1 },
      ["Hardy gout tubers"] = { quantity = 1 },
      ["Seed dibber"] = { quantity = 1 },
      ["Plant cure"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "If you don't already have the farming tools, right-click talk to the Tool Leprechaun to buy some.",
    actions = { Action.ConversationHighlight:new("Would you like to trade?") },
  },
  { text = "Talk to My Arm." },
  {
    text = "Use the following items on him in order:<ul><li>Rake (twice as he breaks it)</li><li>Pick up and use the rake head on the rake handle, give him the assembled rake again.</li><li>Supercompost (optional but recommended). Without supercompost, the patch may become diseased and require curing. Regular compost and ultracompost have no effect.</li><li>Hardy gout tubers</li><li>Seed dibber</li><li>If the patch becomes diseased, a plant cure</li><li>Pick up and use the rake head on the rake handle, give him the assembled rake again.</li></ul>",
  },
  { text = "Defeat the Baby Roc after the cutscene." },
  {
    text = "Make sure to have a spade or buy one from the tool leprechaun now, to avoid any extra steps after the next cutscene.",
  },
  { text = "Talk to My Arm." },
  { text = "Defeat the Giant Roc that appears after the cutscene." },
  { text = "Talk to My Arm.", title = "The end?" },
  {
    text = "Use a spade on him.<ul><li>If needed, you can get the leprechaun to come back by climbing the ladder down and up again.</li></ul>",
  },
  { text = "Return to Burntmeat in the stronghold's kitchen and talk to him." },
  { text = "Return to My Arm and speak with him." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "My Arm's Big Adventure",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1161561600,
  prereqQuests = { "Eadgar's Ruse", "Jungle Potion" },
})
