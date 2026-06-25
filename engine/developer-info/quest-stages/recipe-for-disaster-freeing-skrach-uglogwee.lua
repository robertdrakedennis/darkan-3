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
    text = "Inspect Skrach Uglogwee.",
    title = "Skrach",
    actions = {
      Action.ConversationHighlight:new("Yes, I'm sure I can get some Jubbly Chompy."),
      Action.ConversationHighlight:new("Oh Ok then, I guess I'll talk to Rantz."),
    },
    postconditions = { Condition.ConversationText:new(" Well, I do pity you, and wish you the best of luck!") },
  },
  {
    text = "Head to Feldip Hills AKS and talk to Rantz to the north-east.",
    actions = {
      Action.ConversationHighlight:new("I'm trying to free Skrach. Can you help?"),
      Action.ConversationHighlight:new("Ok, I'll do it."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Dat's da good fing, you's creature goes down by da watery place, over near da cabbage! Me's says more der'."
      ),
    },
  },
  {
    text = "Follow the coast south and talk to Rantz.",
    actions = { Action.ConversationHighlight:new("I guess this is the watery place. What now?") },
  },
  { text = "Cut the roots of the old tree, carve it" },
  {
    text = "Talk to Rantz.",
    actions = { Action.ConversationHighlight:new("Okay, the boat's ready; now tell me how to get a jubbly.") },
  },
  { text = "With a raw chompy, iron spit, and logs, teleport to the Karamja lodestone." },
  {
    text = "Head south until a row boat is seen along the western coast. Just south-east of the boat is a small tree with Rantz's arrow stuck in it.",
  },
  { text = "Use the iron spit on the raw chompy and light a fire just west of the small tree." },
  { text = "Cook the chompy on the fire." },
  {
    text = "Board the nearby ogre boat.",
    actions = { Action.ConversationHighlight:new("Yes please, I'll get a lift back with you.") },
    postconditions = { Condition.ConversationText:new(" The barely sea-worthy log arrives at the Feldip Hills.") },
  },
  { text = "Talk to Rantz.", actions = { Action.ConversationHighlight:new("Okay, now tell me how to get jubbly.") } },
  {
    text = "Make a balloon toad by using the following steps (it is advised to make a few in the event you burn the raw jubbly).<ul><li>Just west, fill your ogre bellows with swamp bubbles from the swamp, then use the bellows on a swamp toad.</li><li>Directly west of the swamp pool, mine a feldip swamp rock (near AKS).</li><li>With the rock, toad, and a ball of wool in your inventory, use the bellows on the bloated toad again to make a balloon toad (the bellows must be filled).</li></ul>",
  },
  { text = "Drop the balloon toad in the area where Rantz is." },
  {
    text = "When a jubbly bird appears, kill it with an ogre bow and pluck it.<ul><li>If a jubbly bird does not spawn by the time the balloon toad disappears, you will need to make another one.</li></ul>",
  },
  {
    text = "Head north to the ogre spit-roast fire and cook the raw jubbly. If you burn it, you must repeat the hunting process.",
  },
  { text = "Return to Lumbridge Castle and Use cooked jubbly on Skrach." },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing Skrach Uglogwee",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest", "Big Chompy Bird Hunting" },
})
