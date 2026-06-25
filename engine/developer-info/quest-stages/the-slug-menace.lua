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
    text = "Talk to Sir Tiffy Cashien in Falador park.",
    title = "Witchaven weirdness",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("The Slug Menace.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Goodbye.") },
  },
  {
    text = "Talk to Col. O'Niall on the pier in Witchaven (fairy ring BLR or Cape of Legends and run south).",
    actions = {
      Action.ConversationHighlight:new("Who are the important people in Witchaven?"),
      Action.ConversationHighlight:new("That's enough for now."),
    },
    postconditions = { Condition.ConversationText:new(" Peace be with you child.") },
  },
  {
    text = "Talk to Mayor Hobb in the western building just south of Caroline.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("What improvements are you making to the shrine?"),
      Action.ConversationHighlight:new("Nothing at the moment thanks."),
    },
    postconditions = {
      Condition.ConversationText:new(" Then please feel free to stay in our village a while longer.Farewell."),
    },
  },
  {
    text = "When you try to exit the building, Savant will contact you and request that you scan nearby the Mayor using the Commorb v2",
  },
  { text = "Go back into the mayor's building, stand beside him and begin scanning." },
  {
    text = "Talk to Brother Maledict in the church.",
    actions = { Action.ConversationHighlight:new("That's enough for now.") },
    postconditions = { Condition.ConversationText:new(" Peace be with you child.") },
  },
  { text = "Talk to Holgart to the north." },
  {
    text = "Report back to Col. O'Niall.",
    actions = { Action.ConversationHighlight:new("That's enough for now.") },
    postconditions = { Condition.ConversationText:new(" Peace be with you child.") },
  },
  {
    text = "Climb-down the old ruin entrance west of Witchaven.",
    title = "Imposing door",
    neededItems = { ["Commorb v2"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Push the wall to your immediate east." },
  { text = "Enter the Wall Opening" },
  {
    text = "Run through the cave until you reach a set of imposing doors.<ul><li>Take the Agility shortcut (requires level 30).</li></ul>",
  },
  { text = "Savant will contact you." },
  { text = "Scan with the commorb to obtain a door transcription." },
  { text = "Pick up the dead sea slug on the ground nearby. You only need one." },
  {
    text = "Talk to Jorral in the small Outpost, north-west of West Ardougne.<ul><li>Traveller's necklace to the Outpost.</li></ul>",
    actions = { Action.ConversationHighlight:new("Translations") },
    postconditions = { Condition.ConversationText:new(" Yes, can I help you?") },
  },
  { text = "Savant will contact you while talking to Jorral and after." },
  { text = "Talk to Col. O'Niall in Witchaven (fairy ring BLR or cape of legends and run south)." },
  { text = "Go near the church (a cutscene will begin)." },
  {
    text = "Talk to Brother Maledict in the church.",
    actions = { Action.ConversationHighlight:new("Talk about something else") },
  },
  {
    text = "Search the Mayor's study desk. You may fail, so repeat until you receive Page 1.",
    title = "Page pieces",
    neededItems = {
      ["Commorb v2"] = { quantity = 1 },
      ["Swamp paste"] = { quantity = 1 },
      ["Dead sea slug"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Talk to Ezekial Lovecraft in the fishing shop.",
    actions = { Action.ConversationHighlight:new("You are a bit too strange for me. Bye.") },
  },
  { text = "Talk to Col. O'Niall. This step requires 3 backpack spaces." },
  { text = "Try to use the swamp paste on the fragments.<ul><li>Savant will contact you.</li></ul>" },
  { text = "Talk to Jeb to reach the fishing platform." },
  { text = "Talk to Bailey to the west." },
  { text = "Use the Sea slug glue on the fragments." },
  { text = "Use the image to solve the puzzle, there are 2 sides to the pieces so you might need to flip some." },
  {
    text = "With essence in your inventory, right click the papers to create 5 shapes. Page 1: Earth, Air. Page 2: Fire, Water. Page 3: Mind.",
    title = "Shaped runes",
    neededItems = { ["Rune Essence"] = { quantity = 1 }, ["Pure essence"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Use the blank shapes on their respective runecrafting altars. You can use the wicked hood to quickly teleport to the respective altars. You need to enter the mysterious ruins of each altar, and use the rune on the altar. Using spell book teleports will use the runes you have created.",
  },
  {
    text = "It is possible to fail at chiselling the essence and/or at converting the blank shapes at the altar. More rune essence will be required if unsuccessful.",
  },
  {
    text = "<table><tbody><tr><th>Altar</th><th>Location</th></tr><tr><td>Air</td><td>West of Varrock</td></tr><tr><td>Earth</td><td>South of Fort Forinthry</td></tr><tr><td>Mind</td><td>East of Goblin Village</td></tr><tr><td>Fire</td><td>West of Het's Oasis</td></tr><tr><td>Water</td><td>In western Lumbridge Swamp</td></tr></tbody></table>",
  },
  { text = "Climb down the old ruins in Witchaven, then enter the wall opening to your immediate east." },
  { text = "Follow the path to the imposing door again." },
  {
    text = "Use the runes on the door. Immediately after the placement of the last elemental block, a cutscene will happen prompting you into combat.",
  },
  { text = "Kill the Slug Prince. The prince is immune to magic, range and necromancy and is level 51 combat." },
  {
    text = "Talk to Sir Tiffy.",
    actions = { Action.ConversationHighlight:new("The Slug Menace.") },
    postconditions = { Condition.ConversationText:new(" Sir Tiffy, it's all gone horribly wrong!") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Slug Menace",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1158710400,
  prereqQuests = { "Sea Slug", "Wanted!" },
})
