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
    text = "Talk to Manni the Reveller in Rellekka longhall.",
    title = "Birthday feast",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to King Vargas in Miscellania." },
  { text = "Talk to Manni the Reveller in Rellekka longhall." },
  {
    text = "Talk to Brundt the Chieftain back in Rellekka Longhall.",
    actions = { Action.ConversationHighlight:new("Ask about the birthday feast.") },
  },
  { text = "After the cutscene you'll be given cave directions." },
  { text = "Then return to Miscellania and talk to King Vargas." },
  {
    text = "After the cutscene, talk to Mawnis Burowgar in Neitiznot, south of the bank.  Watch the cutscene.",
    actions = { Action.ConversationHighlight:new("Chieftain Brundt's birthday feast.") },
  },
  {
    text = "Travel to the 'i' icon ('question mark' on legacy interfaces) in the snowy Rellekka Hunter area.",
    title = "The yeti's lair",
  },
  {
    text = "Inspect the tracks in the north-west corner of the platform and follow them to a mound of ice in the north-east corner. Inspect the tracks directly in front of the ice mound, then dig through the ice mound and go inside.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Run to the southern wall and enter the tunnel." },
  { text = "Kill Nial Swiftfling and take all of his items." },
  {
    text = "Talk to Freygerd in Jatizso near the west gate.",
    actions = { Action.ConversationHighlight:new("Talk about Nial.") },
  },
  {
    text = "Talk to King Gjuki Sorvott IV in the central building.",
    actions = { Action.ConversationHighlight:new("Talk about Freygerd and the new tax.") },
  },
  { text = "Talk to Freygerd near the west gate.", actions = { Action.ConversationHighlight:new("Talk about Nial.") } },
  {
    text = "Talk to Mawnis Burowgar in Neitiznot, south of the bank.",
    actions = { Action.ConversationHighlight:new("Chieftain Brundt's birthday feast.") },
  },
  {
    text = "Talk to King Vargas in the throne room in Miscellania.",
    title = "The king's cure",
    neededItems = {
      ["Lvl-4 Enchant"] = { quantity = 1 },
      ["Herb pouch"] = { quantity = 1 },
      ["Yeti hair"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Ensure you have the standard spellbook enabled." },
  {
    text = "Travel to the Mountain Camp. (Fairy code AJR or east of the Fremennik Province lodestone.)<ul><li>A pole and plank can be found near the goats if you didn't bring a staff and plank.</li></ul>",
  },
  {
    text = "Climb the tree next to the lake, use the staff/pole on the clump of rocks, then use the plank on the flat stone.",
  },
  {
    text = "Stand south of the rocks and listen-to a shining pool in the water.<ul><li>If the diamond root was not placed in your backpack, open the herb pouch with at least one available backpack space.</li></ul>",
  },
  { text = "Use your vial on the spring that appeared on the rock near you." },
  { text = "Use a clean irit on the vial of mountain water for a mountain irit potion (unfinished)." },
  {
    text = "Grind the diamond root, cast Lvl-4 Enchant on the diamond root dust, then use the enchanted diamond root dust and yeti hair to the unfinished vial to make yeti curse cure.",
  },
  { text = "Return and talk to King Vargas. After the cutscene, he transforms into his human form." },
  {
    text = "Talk to Brundt the Chieftain in the Rellekka longhall about the birthday feast.",
    actions = { Action.ConversationHighlight:new("Ask about the birthday feast.") },
  },
  { text = "After the cutscene, talk to Manni the Reveller in the longhall and watch another cutscene." },
  { text = "Quest complete!" },
  {
    text = "Optionally after the quest:<ul><li>Take the unfinished astral rune to Baba Yaga on Lunar Isle.</li><li>Take the prophecy tablet to Historian Minas on the 1st floor[UK]2nd floor[US] of the Varrock Museum.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about astral runes."),
      Action.ConversationHighlight:new("I have some information that might be of use in your displays."),
    },
  },
}

return Quest:new({
  name = "Glorious Memories",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1238544000,
  prereqQuests = { "Mountain Daughter", "The Fremennik Isles", "Royal Trouble", "Lunar Diplomacy" },
})
