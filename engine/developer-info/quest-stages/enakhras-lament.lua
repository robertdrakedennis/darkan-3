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
    text = "Head to the granite and sandstone quarry southwest of the Bandit Camp lodestone.",
    title = "Getting started",
  },
  { text = "Talk to Lazim.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Okay, I'll get on with it.") },
  },
  { text = "If you don't already have your sandstones, mine 5 kg of sandstone in the nearby quarry." },
  {
    text = "Give Lazim 32 kg of sandstone.",
    title = "Underground",
    actions = {
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a small 2 kg block."),
    },
  },
  { text = "Craft the sandstone (32kg) he gives you, use it on the flat ground." },
  { text = "Talk to Lazim.", actions = { Action.ConversationHighlight:new("I'll do it right away!") } },
  {
    text = "Give Lazim another 20kg of sandstone.",
    actions = {
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
      Action.ConversationHighlight:new("Yes, I have more stone."),
      Action.ConversationHighlight:new("Here's a medium 5 kg block."),
    },
  },
  { text = "Craft the sandstone (20kg) he gives you, use it on the statue base." },
  {
    text = "Chisel the headless statue, speak to Lazim.",
    actions = { Action.ConversationHighlight:new("I think it should have a camel's head.") },
  },
  { text = "Mine or obtain two 5kg granite chunks." },
  {
    text = "Craft a 5kg chunk of granite.",
    actions = {
      Action.ConversationHighlight:new("Carve a head for the statue."),
      Action.ConversationHighlight:new("The head of a camel"),
    },
  },
  { text = "Use the stone camel head on the headless statue." },
  { text = "Speak to Lazim." },
  {
    text = "Talk to him again.",
    actions = { Action.ConversationHighlight:new("Do you know where the statue's head is?") },
  },
  {
    text = "Interact with the fallen statue four times, use the 'Chisel' option.",
    actions = {
      Action.ConversationHighlight:new("Chisel"),
      Action.ConversationHighlight:new("Remove the statue's left arm"),
      Action.ConversationHighlight:new("Chisel"),
      Action.ConversationHighlight:new("Remove the statue's right arm"),
      Action.ConversationHighlight:new("Chisel"),
      Action.ConversationHighlight:new("Remove the statue's left leg"),
      Action.ConversationHighlight:new("Chisel"),
    },
  },
  { text = "Take-sigil from the nearby pedestal for the letter 'M'." },
  { text = "Go north and through the door. Click to skip the cutscene." },
  { text = "Go all the way west and through the door to the south. Click to skip the cutscene." },
  {
    text = "Go south and take the path to east which is leading to the middle room. Unlock the door using the M sigil.",
  },
  {
    text = "Optionally, leave the middle room and go through the remaining two outer doors, gather the remaining three sigils, and unlock the remaining three doors that lead to the middle room. The outer doors will become permanently locked post-quest if the stone limbs are destroyed as they cannot be reclaimed.",
  },
  { text = "Climb up the middle room's ladder." },
  { text = "Use your soft clay on the pedestal for a camel mould (p).", title = "Enakhra's temple" },
  {
    text = "Craft your second chunk of granite 5 kg.",
    actions = { Action.ConversationHighlight:new("Carve a shape to fit the pedestal.") },
  },
  { text = "Use the new head on the pedestal, a cutscene starts (which can be skipped)." },
  { text = "Talk to Lazim.", title = "Finishing off" },
  { text = "Go north-west and speak to Pentyn." },
  {
    text = "Use either bread, cake, pie, potato, or a pizza on him.",
    actions = { Action.ConversationHighlight:new("It's okay, I don't need any help.") },
  },
  {
    text = "Go to the west room, equip your staff or wand, melt the fountain.<ul><li>If you have a waterskin, you can refill it at the fountain for a Hard Desert Achievement, Enaqua.</li></ul>",
  },
  {
    text = "Go to the northeast room, clear the furnace with a staff or wand equipped.<ul><li>Grab your coal if it's not already in your inventory.</li></ul>",
  },
  {
    text = "Go to the east room, investigate each brazier, using the respective resource on each of them.<ul><li>You can retrieve coal from your metal bank at the furnace in the northeast room if you didn't bring it.</li></ul>",
  },
  { text = "Pass-through the magical barrier in the centre, climb-up the ladder." },
  { text = "Talk to Lazim." },
  { text = "Climb-over pile of bones south." },
  { text = "Climb-down the stone ladder." },
  {
    text = "Talk to the Boneguard.",
    actions = {
      Action.ConversationHighlight:new("Of course, I'll help you out."),
      Action.ConversationHighlight:new("Okay, I'll start building."),
    },
  },
  { text = "Take-rock from the nearby rubble for a chunk of sandstone. Do this three times." },
  { text = "Inspect the wall to add the sandstone then inspect it again to chisel it. Repeat this twice more." },
  { text = "Talk to the Boneguard for the final cutscene (this can also be skipped)." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Enakhra's Lament",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1137974400,
  prereqQuests = {},
})
