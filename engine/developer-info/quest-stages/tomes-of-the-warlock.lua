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
    text = "Talk to Death in the City of Um.",
    title = "Learning about Orcus",
    neededItems = { ["Necromantic focus"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Teleport to Kharid-et Dig Site and talk to Dr Nabanik.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        "(Without space in backpack:) Oh, you'll need to clear up some room to take it first."
      ),
    },
  },
  {
    text = "Head to the Exam Centre and talk to the Head of Research next to the research notes, giving him a restored necromantic focus artefact.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        "(Without space in backpack:) Oh, you'll need to clear up some room to take it first."
      ),
    },
  },
  {
    text = "Go through the first three (smoke and blood are the same) book dialogues, which requires talking to him multiple times.<ul><li></li><li></li><li></li></ul>",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Ice."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Smoke."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Shadow."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Sources suggest a copy was kept within Kharid-et's vaults, though unfortunately one that was at some point stolen from."
      ),
    },
  },
  {
    text = "Talk to Reldo, the librarian in Varrock Palace to retrieve the damaged Book of Ice.",
    title = "The Book of Ice",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        "(Without space in backpack:) Oh, you'll need to clear up some room to take it first."
      ),
    },
  },
  { text = "Talk to Aubury at Aubury's Rune Shop, south of Varrock east bank.", title = "The Book of Smoke" },
  {
    text = "Head to the ground floor[UK]1st floor[US] of Wizards' Tower, by fairy ring DIS, wicked hood, or teleporting with stardust.",
  },
  {
    text = "Talk to Wizard Borann located on the west side of the ground floor[UK]1st floor[US] to retrieve the damaged Book of Smoke.",
  },
  { text = "Continue the dialogue to ask him about the Book of Blood." },
  { text = "Head to Wizards' Guild.", title = "The Book of Blood" },
  { text = "Talk to Wizard Frumscone in the basement of the guild to receive the damaged Book of Blood." },
  { text = "Head to Menaphos Imperial district.", title = "The Book of Shadow" },
  { text = "Talk to Kohnen the librarian in the Grand Library of Menaphos." },
  { text = "Inspect the sundial in the middle of the bottom floor to retrieve the damaged Book of Shadow." },
  {
    text = "Repair the books at any archaeologist's workbench.",
    title = "Handing in the books",
    neededItems = {
      ["Ancient vis"] = { quantity = 120 },
      ["Blood of Orcus"] = { quantity = 104 },
      ["Imperial steel"] = { quantity = 80 },
    },
    recommendedItems = {},
  },
  {
    text = "Hand in to Head of Research.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        "(Without space in backpack:) Oh, you'll need to clear up some room to take it first."
      ),
    },
  },
  { text = "Talk to Death in City of Um.", title = "Parsing necromancy glyphs" },
  { text = "Talk to Ted beside the Well of Souls at the ritual site." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Tomes of the Warlock",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.long,
  releaseDate = 1691366400,
  prereqQuests = { "The Spirit of War", "The Jack of Spades" },
})
