local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region Items
local deathGuard = Model.new(570, {
  [1] = Vertex.new(25, 2, 37, 127, 127, 127),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Interact with the white portal north of the Draynor lodestone.",
    title = "Starting out",
    neededItems = {},
    recommendedItems = {},
    actions = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Speak to Death.",
    actions = {},
    postconditions = { Condition.ConversationText:new("To the Underworld, where life goes on beyond death.") },
  },
  {
    text = "After the cutscene ends, speak to Death again.",
    actions = {},
    postconditions = {
      Condition.ConversationText:new(
        "I know the Underworld like no other, but I am no necromancer. We will need a tutor."
      ),
    },
  },
  {
    text = "Speak to Malignius Mortifer.",
    title = "Communion ritual",
    actions = {},
    neededItems = {},
    recommendedItems = {},
    postconditions = { Condition.ConversationText:new("Allergies. Just do the inner area for now.") },
  },
  -- Don't put an action here since the quest already highlights it with yellow arrows
  {
    text = "Clear the mounds of dust around the ritual site.",
    postconditions = { Condition.ConversationText:new("*achoo* That's better. Now, listen carefully.") },
  },
  {
    text = "Place the bones on the pedestal.",
    actions = { Action.InventoryHighlight:new(Models.items["bones"]) },
    postconditions = { Condition.ConversationText:new("I suppose you'd better place the...candles next.") },
  },
  {
    text = "Speak to Malignius again.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You're given some ritual supplies.") },
  },
  {
    text = "Place the basic ritual candles on the four pentagon-shaped light source spots around the ritual site.",
    postconditions = {
      Condition.ConversationText:new(
        "Now, the glyphs. To draw them we'll need ink, and I don't know where we'll find some..."
      ),
    },
  },
  {
    text = "Speak to Malignius again.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You're given some ritual supplies.") },
  },
  {
    text = "Draw the Commune I glyph on a nearby glyph spot. Repeat this again at another glyph spot.",
    actions = {},
    postconditions = {
      Condition.ConversationText:new(
        "Glyphs and candles will degrade with each ritual. If they deplete, you'll need to repair or replace them."
      ),
    },
  },
  {
    text = "Speak to Malignius again.",
    actions = {},
    postconditions = { Condition.ConversationText:new("You're given some ritual supplies.") },
  },
  {
    text = "Repair the broken glyph.",
    actions = {},
    postconditions = {
      Condition.ConversationText:new("Now everything's prepared, go and stand on the skull platform over there."),
    },
  },
  {
    text = "Stand on the nearby skull platform.",
    postconditions = {
      Condition.ConversationText:new("I can't believe you managed to get it right on your first attempt."),
    },
  },
  {
    text = "Continue talking with Ted.",
    title = "The Well of Souls",
    neededItems = {},
    recommendedItems = {},
    actions = {},
    postconditions = { Condition.ConversationText:new("Take a closer look at the Well of Souls.") },
  },
  {
    text = "After the dialogue, manage the Well of Souls and select the Conjure Skeleton Warrior talent at the bottom of the interface.",
    actions = {},
    postconditions = { Condition.ConversationText:new("Equip that death guard and attack them!") },
  },
  {
    text = "Wield the death guard that Sostratus gives you.",
    actions = { Action.InventoryHighlight:new(deathGuard) },
    postconditions = { Condition.InventoryDoesNotContain:new(deathGuard) },
  },
  {
    text = "Kill the nearby ghost troll brute and ghost troll lout.",
    actions = {},
    postconditions = { Condition.ConversationText:new("And that's how it's done!") },
  },
  {
    text = "Interact with the Well of Souls again, and unlock the Conjure Skeleton Warrior talent.",
    actions = {},
    postconditions = { Condition.ConversationText:new("Did ya learn anything?") },
  },
  {
    text = "Speak to Malignius Mortifer.",
    actions = {},
    postconditions = { Condition.ConversationText:new("Until next we meet") },
  },
}

return Quest:new({
  name = "Necromancy!",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = false,
  length = Enums.length.short,
  releaseDate = 1691366400,
  prereqQuests = {},
  questReqs = {},
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {
    ["Ghost troll brute"] = { level = "3", quantity = 1 },
    ["Ghost troll lout"] = { level = "3", quantity = 1 },
  },
})
