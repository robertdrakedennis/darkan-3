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
    text = "Inspect Pirate Pete in the banquet hall in Lumbridge Castle.",
    title = "Pirate Pete",
  },
  {
    text = "Talk to the Cook in the Lumbridge Castle.",
    actions = {
      Action.ConversationHighlight:new("Protecting the Pirate"),
      Action.ConversationHighlight:new("Where do I get Ground Cod?"),
      Action.ConversationHighlight:new("Where do I get Ground Giant Crab Meat?"),
      Action.ConversationHighlight:new("Where do I get Breadcrumbs?"),
      Action.ConversationHighlight:new("Thanks!"),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Bank any Summoning pouches or Pets.",
    title = "Diving",
    neededItems = { ["Fishbowl"] = { quantity = 1 }, ["Needle"] = { quantity = 1 }, ["Bronze wire"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Talk to Murphy on the docks of Port Khazard.",
    actions = { Action.ConversationHighlight:new("Talk about Recipe for Disaster.") },
  },
  {
    text = "With your fishbowl, talk to him again.",
    actions = {
      Action.ConversationHighlight:new("Talk about Recipe for Disaster."),
      Action.ConversationHighlight:new("Not just yet."),
    },
    postconditions = { Condition.ConversationText:new(" Ok, well I'll be here if you change your mind.") },
  },
  { text = "Equip the fishbowl helmet and diving apparatus." },
  {
    text = "Whilst weighing under or exactly 27 kg, having your needle and bronze wire, talk to Murphy again.<ul><li>There is a bank deposit box next to Murphy.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about Recipe for Disaster."),
      Action.ConversationHighlight:new("Yes, Let's go diving."),
    },
  },
  {
    text = "Pick some kelp (your inventory is hidden).<ul><li>It is advisable to obtain multiple in the event you burn the fishcake.</li></ul>",
  },
  { text = "Swim north and talk to Nung." },
  { text = "Head west to the Mudskipper cave and pick up five rocks." },
  { text = "Enter the cave and equip your weapon." },
  { text = "Kill mudskippers for 5 hides." },
  { text = "Return to Nung with the five hides, your needle, and three bronze wire." },
  { text = "Talk to Nung again." },
  { text = "Enter the crab pen and equip your weapon." },
  {
    text = "Kill some crabs and take their meat.<ul><li>It is advised to obtain multiple in the event you burn the fishcake.</li></ul>",
  },
  {
    text = "While in the pen teleport out to Lumbridge.<ul><li>Alternatively, leave the crab pen and climb up the anchor chain to the south.</li></ul>",
  },
  {
    text = "Return to the kitchen in Lumbridge Castle.",
    title = "Freeing Pirate Pete",
    neededItems = {
      ["Kelp (Mogre Camp)"] = { quantity = 1 },
      ["Crab meat"] = { quantity = 1 },
      ["Raw cod"] = { quantity = 1 },
      ["Bread"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Prepare the ingredients:<ul><li>Grind the kelp.</li><li>Choose the Grind option on the crab meat.</li><li>Choose the Grind option on the raw cod.</li><li>Choose the Slice option on the bread.</li><li>If at any point while preparing the ingredients, the message 'Nothing interesting happens' appears, talk to the Cook.</li></ul>",
  },
  { text = "Talk to the Cook.", actions = { Action.ConversationHighlight:new("Protecting the Pirate") } },
  { text = "Use the ingredients on each other." },
  { text = "Cook the Raw fishcake." },
  {
    text = "Use fishcake on Pirate Pete.<ul><li>If the fishcake is eaten, you need to re-obtain all of the ingredients.</li></ul>",
  },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing Pirate Pete",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest" },
})
