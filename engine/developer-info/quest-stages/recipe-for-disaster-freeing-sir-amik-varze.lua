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
    text = "Inspect Sir Amik Varze.",
    title = "Sir Amik",
  },
  {
    text = "Talk to the Cook in the Lumbridge Castle kitchen.",
    actions = {
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("Protecting Sir Amik Varze"),
    },
  },
  {
    text = "Use a pot of cream with a bucket of milk.",
    title = "The Brulee",
    neededItems = {
      ["Pot of cream"] = { quantity = 1 },
      ["Bucket of milk"] = { quantity = 1 },
      ["Sweetcorn"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Locate a windmill (Taverley windmill doesn't work) and pick up an empty pot on the ground floor[UK] 1st floor[US] .",
  },
  { text = "Put sweetcorn into the hopper on the 2nd floor[UK] 3rd floor[US]." },
  { text = "Operate the hopper controls, then take from the flour bin on the ground floor[UK] 1st floor[US]." },
  { text = "Use the milky mixture with the pot of cornflour." },
  {
    text = "Go to the Kharazi Jungle with a machete and hatchet (tool belt works for both) or use  CJS if unlocked.<ul><li>Have Radimus notes with you, if Legends' Quest isn't completed.</li></ul>",
    title = "Other Ingredients - Vanilla Pod",
  },
  { text = "Head southwest of the pool to the beach to find five vanilla plants." },
  { text = "Search a Vanilla plant and add the vanilla pod to the cornflour mixture for brulee." },
  {
    text = "Go to Draynor Village and talk to the Wise Old Man.",
    title = "Evil Chicken's Egg and Dragon Token",
    actions = {
      Action.ConversationHighlight:new("I'd just like to ask you something."),
      Action.ConversationHighlight:new("Strange beasts"),
      Action.ConversationHighlight:new("The Evil Chicken"),
      Action.ConversationHighlight:new("Thanks, maybe some other time."),
    },
    postconditions = { Condition.ConversationText:new(" As you wish. Farewell, Player.") },
  },
  {
    text = "Head to Zanaris. Use a raw chicken on the Chicken shrine. (Kill a chicken near the shrine for a raw chicken if you need one).",
  },
  {
    text = "Head north and kill the Evil Chicken.<ul><li>If the chicken is not in the shrine, talk to the Cook and attempt to fight it again.</li></ul>",
  },
  { text = "Take the evil chicken's egg (right-click the loot pile, as area loot won't show it)." },
  { text = "Head south and kill a black dragon." },
  { text = "Take the dragon token it drops (right-click the loot pile, as area loot won't show it)." },
  { text = "Use the egg on the brulee." },
  {
    text = "Craft your dramen branches branch, selecting pestle and mortar.<ul><li>If you do not have Dramen branches, cut one from the dramen tree in the Entrana Dungeon.</li></ul>",
    title = "Cinnamon",
  },
  { text = "Add the cinnamon to the brulee." },
  {
    text = "Wear the ice gloves or dragonfire protection, otherwise you can take up to 500 damage if you don't have them on for the next few steps.",
    title = "Freeing Sir Amik",
  },
  { text = "Rub the dragon token in either Lumbridge or Zanaris." },
  {
    text = "Ask the Fairy Dragon to flambé the brulee.",
    actions = { Action.ConversationHighlight:new("Please flambe this creme brulee for me.") },
    postconditions = { Condition.ConversationText:new(" Sure, that's easy.") },
  },
  { text = "Head back to Lumbridge Castle then use brulee supreme on Sir Amik." },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing Sir Amik Varze",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest", "Lost City", "Legends' Quest" },
})
