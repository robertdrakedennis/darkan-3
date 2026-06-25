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
    text = "Enter the tunnel between the two black statues, north-east of Rellekka, close to fairy ring DKS. (teleport to the Fremennik Province and run north-east)",
    title = "Under arrest",
  },
  { text = "Go through cave entrance east to Keldagrim." },
  {
    text = "Talk to the Dwarven Boatman inside the cave, not Dwarven Ferryman.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Go north-west to the little building with doors on both sides. Enter the building and go upstairs. After the cutscene, you should spawn in the same room with Commander Veldaban. The chat instance should end with you accepting the quest.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Exit the house, go north-east across the bridge and talk to Blasidar the Sculptor just north-west of the brewery.",
    actions = { Action.ConversationHighlight:new("Yes, I will do this.") },
    postconditions = { Condition.ConversationText:new(" Excellent, excellent! Now what I need is the following...") },
  },
  {
    text = "In the large building just west, talk to Vermundi at the silk icon.",
    title = "King's clothes",
    neededItems = { ["Coins"] = { quantity = 200 }, ["Coal"] = { quantity = 1 }, ["Logs"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes, I'm looking for some special clothes.") },
    postconditions = { Condition.ConversationText:new(" Oh, that sounds interesting, what's this about?") },
  },
  {
    text = "In the building west of the northern bridge, talk to the Librarian.",
    actions = { Action.ConversationHighlight:new("Do you know anything about King Alvis's clothes?") },
    postconditions = { Condition.ConversationText:new(" His clothes? How do you mean?") },
  },
  { text = "While weighing less than 30kg, attempt to climb any bookcase." },
  {
    text = "With the book, return to Vermundi.",
    actions = { Action.ConversationHighlight:new("Yes, about those special clothes again...") },
    postconditions = { Condition.ConversationText:new(" Can I have those clothes now?") },
  },
  { text = "Use coal and logs on her spinning machine." },
  { text = "Try to light it until the engine starts. (This may take several attempts)" },
  {
    text = "Talk to Vermundi.",
    actions = {
      Action.ConversationHighlight:new("Yes, about those special clothes again..."),
      Action.ConversationHighlight:new("I'll pay."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "(Without at least one free backpack space:) That's great! But you'll need some free space to take them."
      ),
    },
  },
  {
    text = "In the far west of the city, talk to Saro at the anvil icon.",
    title = "King's boots",
    neededItems = {
      ["Air rune"] = { quantity = 1 },
      ["Law rune"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes, I'm looking for a pair of special boots.") },
    postconditions = { Condition.ConversationText:new(" I thought I already told you where to get them?") },
  },
  { text = "North of the bank, talk to Dromund." },
  { text = "Take the left boot, from the table near the door, while his back is turned." },
  {
    text = "Run out back of his house near the window next to the right boot use Telekinetic Grab on the right boot.<ul><li>Make sure his back is turned as well.</li></ul>",
  },
  {
    text = "Go to the north-west corner and talk to Santiri.",
    title = "King's axe",
    neededItems = {
      ["Sapphire"] = { quantity = 3 },
      ["Redberry pie"] = { quantity = 1 },
      ["Iron bar"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Yes, I'm looking for a particular battleaxe."),
      Action.ConversationHighlight:new("Blasidar the sculptor needs it for his statue."),
      Action.ConversationHighlight:new("Perhaps I can repair the axe?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " On your own? I doubt it. The sapphires, perhaps, if you are a skilled enough crafter. But the blade..."
      ),
    },
  },
  { text = "Use 3 sapphires on the battleaxe." },
  {
    text = "If you have not completed The Knight's Sword:<ul><li>Take a redberry pie with you when you talk to Thurgo.</li></ul>",
  },
  {
    text = "Talk to Thurgo in his small hut (fairy ring AIQ), south of Port Sarim, and north of Mudskipper Point.<ul><li>If The Knight's Sword is complete</li><li>Have an iron bar to give to Thurgo (or access your metal bank via the anvil next to Thurgo to obtain one which is stored in it).</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Something else. (3 if skillcape is owned)"),
      Action.ConversationHighlight:new("Would you like some redberry pie?"),
      Action.ConversationHighlight:new("Can you help me with this ancient axe?"),
      Action.ConversationHighlight:new("Return to Keldagrim immediately."),
      Action.ConversationHighlight:new("Something else. (3 if skillcape is owned)"),
      Action.ConversationHighlight:new("Can you help me with this ancient axe?"),
      Action.ConversationHighlight:new("Return to Keldagrim immediately."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "With the 4 items, talk to Riki the sculptor's model near Blasidar the sculptor." },
  { text = "Talk to Blasidar for a cutscene." },
  { text = "Run west to the marketplace building and up the stairs.", title = "Climbing the corporate ladder" },
  {
    text = "Talk to any secretary (yellow is closest to metal bank) - your character's gender may disqualify your eligibility to work for that colour of company, consequently try another colour.",
    actions = {
      Action.ConversationHighlight:new("Is there anything I can help you with?"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Fetch them the required ore (must be un-noted). Repeat with the same secretary for a total of 4 tasks." },
  {
    text = "Talk to the director.",
    actions = {
      Action.ConversationHighlight:new("Do you have any more tasks for me?"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Fetch them the required bars. You might need to repeat for a second task." },
  {
    text = "Talk to the director.",
    actions = {
      Action.ConversationHighlight:new("I'd like to officially join your company."),
      Action.ConversationHighlight:new("Blasidar the sculptor has sent me."),
      Action.ConversationHighlight:new("I would support you."),
      Action.ConversationHighlight:new("Yes! Long live the [company]!"),
    },
  },
  {
    text = "Return to Commander Veldaban in Black Guard headquarters, where you started the quest (west of the palace and west of the bank).",
    actions = {
      Action.ConversationHighlight:new("I'm ready."),
      Action.ConversationHighlight:new("I do NOT want to attend, I don't have time to waste."),
      Action.ConversationHighlight:new("I just have a lot of smithing to do today, I can't attend."),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Giant Dwarf",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1117497600,
  prereqQuests = {},
})
