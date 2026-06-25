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
    text = "Talk to Leela, north of the Draynor Village jail.",
    title = "Pharaoh's pyramid",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>If Leela does not show up in Draynor Village, you need to complete the Stolen Hearts and Diamond in the Rough quests first, even if Icthlarin's Little Helper is already completed.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Enter the flat-topped pyramid Uzer Mastaba from the west side, south of Uzer.<ul><li>Teleport to the fairy ring DLQ and then run east.</li><li>Teleport to Al Kharid and go south to Shantay's Pass (Or use amulet of glory's teleport to Al Kharid).</li><li>Use the magic carpet network to get to Uzer.</li><li>Teleport with the traveller's necklace to desert eagles' eyrie.</li></ul>",
  },
  { text = "In the initial east chamber, rummage and kill the skeleton for a canopic jar." },
  { text = "Head north-west and through the imposing doors to the south." },
  {
    text = "Kill the four skeletons in the room for a mummy hand and the scroll of the dead. The mummy hand and scroll of the dead don't appear in area loot, you need to right click to take them.",
  },
  {
    text = "Return to Leela in Draynor Village.",
    actions = { Action.ConversationHighlight:new("I think I can deal with this myself.") },
    postconditions = { Condition.ConversationText:new(" Tread softly in the land of the living.") },
  },
  {
    text = "The scroll of praise:<ul><li>Get an Al Kharid flyer from Waseem the Leaflet Dropper north of the Al Kharid mine (at the entrance to the desert).</li><li>With your Al Kharid flyer, talk to Reldo in the Varrock palace library about the pyramid.  or  He will give you a scroll of praise.</li></ul>",
    title = "Preparing the sarcophagus",
    neededItems = {
      ["Al Kharid flyer"] = { quantity = 1 },
      ["Sq'irkjuice"] = { quantity = 1 },
      ["Ring of charos (a)"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I'd like to talk about a pyramid."),
      Action.ConversationHighlight:new("I'd like to talk about a pyramid."),
    },
    postconditions = {
      Condition.ConversationText:new(" I'm afraid I'm a busy man. I can't just answer general questions, you know."),
    },
  },
  {
    text = "The copied name papyrus and mummy's name papyrus:<ul><li>Play the Sorceress's Garden minigame and obtain a mug of any sq'irkjuice.</li><li>You need to use the sq'irkjuice on Ali Morrisane at his stall. Then talk with him. .</li><li>Talk to him again with a ring of charos (a) equipped. .</li><li>This step will only work after talking to the Wise Old Man during the Garden of Tranquillity, as the ring does not allow you to persuade people before this step.</li><li>You should have received both a copied name papyrus and a mummy's name papyrus.</li><li>Do not destroy the copied name papyrus or you will have to re-obtain a sq'irkjuice for another copy.</li><li>Talk to him again with a ring of charos (a) equipped. .</li><li>This step will only work after talking to the Wise Old Man during the Garden of Tranquillity, as the ring does not allow you to persuade people before this step.</li><li>You should have received both a copied name papyrus and a mummy's name papyrus.</li><li>Do not destroy the copied name papyrus or you will have to re-obtain a sq'irkjuice for another copy.</li><li>Do not destroy the copied name papyrus or you will have to re-obtain a sq'irkjuice for another copy.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I have some questions about a tomb."),
      Action.ConversationHighlight:new("I'm after ways to reunite the mummy in the pyramid."),
      Action.ConversationHighlight:new("I can't think of anything sensible to ask."),
      Action.ConversationHighlight:new("I have some questions about a tomb."),
      Action.ConversationHighlight:new("I'm after ways to reunite the mummy in the pyramid."),
      Action.ConversationHighlight:new("I can't think of anything sensible to ask."),
    },
  },
  { text = "Return to the pyramid with prayer points.", title = "Collecting canopic jars`" },
  { text = "Again, open the imposing door in the north-west corner." },
  {
    text = "Kill the golem and descend the stairs (You may be placed in the mummy's tomb after descending the stairs, past the puzzle. If so, you will need to cross the puzzle to do the next 4 steps).",
  },
  { text = "Find a path of blue and green blocks or of red and yellow blocks all the way through." },
  {
    text = "Go across the tiled room and collect the first canopic jar to the west of the northern staircase.<ul><li>If you spawn at the bottom of the northern staircase simply pick up the canopic jar to the west.</li></ul>",
  },
  {
    text = "Collect the two remaining canopic jars and mummy with no hand in the tunnels. If you did not collect the fourth earlier, it can be found on one of the skeleton bodies upstairs, 'Rummage' them and defeat them.",
  },
  { text = "Use the mummy hand on the mummy with no hand." },
  {
    text = "Return to the tiled room and cross back towards the empty sarcophagus.<ul><li>You may go upstairs, and back downstairs to teleport to the tomb room to skip the tile puzzle.</li></ul>",
  },
  {
    text = "Place jar on the shelves 4 times.",
    title = "Restoring the tomb",
    neededItems = {
      ["Fire rune"] = { quantity = 1 },
      ["Willow log"] = { quantity = 1 },
      ["Jug of wine"] = { quantity = 1 },
      ["Spice"] = { quantity = 1 },
      ["Empty pot"] = { quantity = 1 },
      ["Wheat"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Fill the empty sarcophagus." },
  { text = "Pray at the 5 broken statues." },
  { text = "Repair the chair and table." },
  { text = "Use spice on your jug of wine." },
  { text = "Use wheat on your empty pot." },
  { text = "Replace the pot and jug." },
  {
    text = "Run east and kill the shadow. Then light the enchanted sconces in the following order: south, north, west, east. Shadows will spawn and must be killed between each sconce lighting.",
  },
  { text = "Return to the Queen's chamber and create the statue on the empty base, then carve it 2 times." },
  {
    text = "Talk to the Pharaoh Queen who has appeared in front of the table to give her the 2 papyri and the Scroll of Praise.  (Not required for quest completion, only for 100% rebuild)",
    actions = { Action.ConversationHighlight:new("I have nothing more to say.") },
    postconditions = { Condition.ConversationText:new(" Tread softly in the land of the living.") },
  },
  {
    text = "Right-click check-progress on your scroll of the dead or pyramid journal to verify you've rebuilt the mummy 75%, otherwise check any parts missed. (Or 100% if you want to finish the mummy)",
  },
  { text = "Talk to Leela in Draynor Village." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Missing My Mummy",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1242691200,
  prereqQuests = { "Icthlarin's Little Helper", "The Golem", "Diamond in the Rough", "Garden of Tranquillity" },
})
