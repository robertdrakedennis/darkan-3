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
    text = "Inspect the Lumbridge Sage.",
    title = "The sage",
    actions = { Action.ConversationHighlight:new("Yes, I'm sure I can make a cake.") },
    postconditions = { Condition.ConversationText:new(" Sounds a bit weird though. What does it involve?") },
  },
  {
    text = "Head to the 2nd floor[UK] 3rd floor[US] of the Wizards' Tower and talk to Wizard Traiborn who is west of the beam.",
    actions = { Action.ConversationHighlight:new("[Ask about the Lumbridge Sage.]") },
  },
  {
    text = "Talk to Traiborn again for the first quiz.",
    title = "The egg",
    actions = {
      Action.ConversationHighlight:new("[Ask about the Lumbridge Sage.]"),
      Action.ConversationHighlight:new("Ok. Let's Start!"),
    },
  },
  {
    text = "Talk to Traiborn again for the second quiz.",
    title = "The milk",
    actions = {
      Action.ConversationHighlight:new("[Ask about the Lumbridge Sage.]"),
      Action.ConversationHighlight:new("Ok. I'm ready!"),
    },
  },
  {
    text = '<table class="wikitable lighttable" width="600"><caption>Quiz answers</caption><tbody><tr><th>Question</th><th>Answer</th></tr><tr class=""><td>What is the Defence level requirement to wear a Mystic Hat?</td><td>50</td></tr><tr class=""><td>What doesn\'t lie between Morytania and Asgarnia?</td><td>Keep Le Faye</td></tr><tr class=""><td>What is the nearest guild to the Fishing Platform (as the seagull flies)?</td><td>Legends\'</td></tr><tr class=""><td>What is the name of the toy seller in Draynor Village?</td><td>Diango</td></tr><tr class=""><td>The River Salve runs from...?</td><td>North to South</td></tr><tr class=""><td>Take the number of Fire Runes requires to cast Fire Strike, and multiply by the number by the number of Air Runes used to cast Air Strike before adding the number of Earth Runes used to cast Earth Wave. What do you get?</td><td>5</td></tr><tr><td>The combat level of unarmed goblins near Lumbridge is:</td><td>2</td></tr><tr><td>If I\'m going to need glass, first I will need:</td><td>Sand, bucket, soda ash, glass blowing pipe</td></tr><tr><td>A gold ring is to 5 as a holy symbol is to...</td><td>16</td></tr><tr><td>A glass vial is to 33, as a glass orb is to...</td><td>46</td></tr><tr><td>What ingredients are used in a cake?</td><td>Flour, Eggs and milk</td></tr><tr><td>I can hear howling in one direction and buzzing in the other - where am I?</td><td>Catherby</td></tr><tr><td>I\'m in a bar west of Pollnivneach. Where am I?</td><td>Bandit Camp</td></tr><tr><td>Which tower is closest to the Crafting Guild?</td><td>Dark Wizards\'</td></tr><tr><td>GP to bribe an Al Kharid gate guard</td><td>10</td></tr></tbody></table>',
  },
  {
    text = "Talk to Traiborn for the final quiz.",
    title = "The flour",
    actions = {
      Action.ConversationHighlight:new("[Ask about the Lumbridge Sage.]"),
      Action.ConversationHighlight:new("Quiz me!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " For enchanting this item you will be shown a selection of items, when you stop viewing this selection you'll have to answer three questions correctly in a row and then the flour will be enchanted with guidance."
      ),
    },
  },
  {
    text = "After completing his final quiz, you receive an enchanted egg, a bucket of enchanted milk and pot of enchanted flour.",
  },
  { text = "Use any of the raw ingredients on a cake tin.", title = "Freeing the Lumbridge Sage" },
  { text = "Bake the raw guide cake on a range." },
  { text = "Use the cake of guidance on the Lumbridge Sage back in Lumbridge." },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing the Lumbridge Sage",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest" },
})
