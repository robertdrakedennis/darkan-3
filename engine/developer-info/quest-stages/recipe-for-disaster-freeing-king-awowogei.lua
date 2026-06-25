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
    text = "Inspect King Awowogei in the Lumbridge castle dining room.",
    title = "King Awowogei",
  },
  { text = "Travel to Ape Atoll." },
  { text = "If not already, equip any monkey greegree and the monkeyspeak amulet." },
  {
    text = "If you do not already have a banana and monkey nuts head to Solihib's Food Stall on the north-east of the market and buy one of each.",
  },
  { text = "If you do not already have a rope, buy one from Ifaba at the south-west of the market." },
  { text = "Head to the south-easternmost building in Marim." },
  { text = "Talk to the Elder Guard." },
  { text = "Talk to Awowogei. Ask about his favourite dish." },
  { text = "Head to the Temple of Marimbo to the north of Awowogei's seat." },
  {
    text = "Talk to the three wise monkeys in the north-west corner of the temple.",
    actions = { Action.ConversationHighlight:new("Do you know anything about the king's favourite dish?") },
    postconditions = { Condition.ConversationText:new("  Well, I am also on a top secret mission.") },
  },
  {
    text = "Talk to the three wise monkeys two more times (first discussing the banana and then the same for the monkey nuts).",
  },
  {
    text = "Head to Crash Island and enter the pit east of Lumdo.  Lower level combat players may find it useful to turn on Protect from Melee before entering the pit and also having an emergency teleport. Anti-poison is also useful.",
    title = "Obtaining the Ingredients - The Snake",
    actions = { Action.ConversationHighlight:new("Yes, I'm as hard as nails.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Kill a snake to get a snake corpse. Optionally, collect multiple in the event you burn the dish." },
  { text = "Find the Red Banana Tree on the coast west of Marim.", title = "The Red Banana" },
  { text = "Equip a gorilla greegree." },
  { text = "Use a rope on the Red Banana Tree." },
  {
    text = "Slice the red banana. Once the previous one is sliced, you can collect another, however it has no other uses.",
  },
  { text = "Head south of Marim to the Ape Atoll Agility Course.", title = "Tchiki Nuts" },
  { text = "Equip the ninja greegree." },
  {
    text = "Proceed through the course until reaching the hole right after the rope swing. The course starts with the small stone in the water next to the palm tree.<ul><li>If you have a ninja monkey greegree (small) and fairy ring access, you may also teleport to fairy ring CLR and climb through the hollow log to quickly get there.</li></ul>",
  },
  { text = "Enter the hole and pick tchiki monkey nuts from the bush." },
  { text = "Grind the nut. Once the previous one is ground, you can collect another, however it has no other uses." },
  { text = "Climb up the rope and finish the course." },
  { text = "If below level 70 Cooking, make sure to have the boost at this part.", title = "Freeing King Awowogei" },
  {
    text = "Use the snake corpse on either the red banana slices or the tchiki monkey nut paste to prepare a raw stuffed snake.",
  },
  { text = "Enter the trapdoor near the eastern wall of the temple." },
  {
    text = "Walk down one of the wooden platforms and Enter the crack in the east wall between the wooden platforms, called 'Exit'.",
  },
  { text = "Equip the zombie greegree." },
  { text = "Walk across the coals and Use the raw stuffed snake on the long rock." },
  { text = "Head to Lumbridge Castle and make sure you have equipped your monkeyspeak amulet." },
  { text = "Use the stuffed snake on King Awowogei. Be careful not to eat it." },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing King Awowogei",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest", "Monkey Madness" },
})
