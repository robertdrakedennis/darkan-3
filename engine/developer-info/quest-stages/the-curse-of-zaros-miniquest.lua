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
    text = "Make sure you are wearing your ring of visibility and your ghostspeak amulet.",
    title = "Getting started",
  },
  {
    text = "Speak to the Mysterious ghost near Glarial's tombstone northwest of the Fishing Guild.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "This ghost will give you one of three clues which determine your entire route, which we will label Sequence 1, 2, and 3:<ul><li>Sequence 1 - He mentions pirates and a shipwreck.</li><li>Sequence 2 - He mentions the thief being north-east of there.</li><li>Sequence 3 - He mentions the thief being south-east of there.</li></ul>",
  },
  {
    text = "This version of the miniquest is a WIP. It's best to use the wiki for this section. Be careful in the wilderness!",
    title = "The sequences to follow",
  },
  {
    text = "Make your way and speak to Rennard in the location given in the table above to receive Ghostly gloves.",
    title = "Talking to the ghosts",
    actions = { Action.ConversationHighlight:new("Tell me your story") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, if you have spoken to Rennard, then you will know that he had somehow managed to obtain a very valuable weapon, and was looking for buyers. What he probably didn't tell you, was that he met me in a drunken stupor in some smoke filled tavern, and I offered to arrange a purchaser for this item, in exchange for a small finders fee."
      ),
    },
  },
  {
    text = "Make your way and speak to Kharrim in the location given in the table above to receive Ghostly boots.",
    actions = { Action.ConversationHighlight:new("Tell me your story") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, if you have spoken to Rennard, then you will know that he had somehow managed to obtain a very valuable weapon, and was looking for buyers. What he probably didn't tell you, was that he met me in a drunken stupor in some smoke filled tavern, and I offered to arrange a purchaser for this item, in exchange for a small finders fee."
      ),
    },
  },
  {
    text = "Make your way and speak to Lennissa in the location given in the table above to receive Ghostly robe bottom.",
    actions = { Action.ConversationHighlight:new("Tell me your story") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, if you have spoken to Rennard, then you will know that he had somehow managed to obtain a very valuable weapon, and was looking for buyers. What he probably didn't tell you, was that he met me in a drunken stupor in some smoke filled tavern, and I offered to arrange a purchaser for this item, in exchange for a small finders fee."
      ),
    },
  },
  {
    text = "Make your way and speak to Dhalak in the location given in the table above to receive Ghostly hood.",
    actions = { Action.ConversationHighlight:new("Tell me your story") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, if you have spoken to Rennard, then you will know that he had somehow managed to obtain a very valuable weapon, and was looking for buyers. What he probably didn't tell you, was that he met me in a drunken stupor in some smoke filled tavern, and I offered to arrange a purchaser for this item, in exchange for a small finders fee."
      ),
    },
  },
  {
    text = "Make your way and speak to Viggora in the location given in the table above to receive Ghostly cloak.",
    actions = { Action.ConversationHighlight:new("Tell me your story") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, if you have spoken to Rennard, then you will know that he had somehow managed to obtain a very valuable weapon, and was looking for buyers. What he probably didn't tell you, was that he met me in a drunken stupor in some smoke filled tavern, and I offered to arrange a purchaser for this item, in exchange for a small finders fee."
      ),
    },
  },
  { text = "Miniquest complete!" },
}

return Quest:new({
  name = "The Curse of Zaros (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1122336000,
  prereqQuests = { "Temple of Ikov", "The Restless Ghost", "Desert Treasure" },
})
