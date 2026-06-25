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
    text = "Talk to the High Priest in Sophanem.",
    title = "Running errands",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("What news?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Climb the ladder behind the altar." },
  {
    text = "After the cutscene, talk to Maisa.",
    actions = { Action.ConversationHighlight:new("Why are you here?") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, you know that my partner, Kaleef, was slain below the city by those foul Scabaras creatures."
      ),
    },
  },
  { text = "Talk to the High Priest." },
  {
    text = "Go to the south-east of the Agility Pyramid, take the rope shortcut just to the east.<ul><li>If you haven't unlocked this shortcut, enter to the swamp south-east of Sophanem and go north, then climb the fallen pillar and the low wall. Last, use the rope on the rock.</li></ul>",
  },
  {
    text = "Climb over the low wall and talk to Lead archaeologist Abigail and Assistant archaeologist Kerner by the tents. Read their questions carefully and always round down the answer.",
    actions = { Action.ConversationHighlight:new("I've had dealings with his sort before; feel free to test me.") },
    postconditions = {
      Condition.ConversationText:new(
        " As a general rule, always round down to the nearest whole number. So, 17 divided by 10 would equal 1 in this trial."
      ),
    },
  },
  { text = "Talk to them again for an empty crate." },
  {
    text = "Fetch the bronze gear that are written in your quest journal.",
    title = "Distressing bronze gear",
    neededItems = {
      ["Ugthanki dung"] = { quantity = 1 },
      ["Bucket"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "At this point, if you have 60 Crafting, continue with the section Distressing by players." },
  { text = "Use the bronze gear on the crate.", title = "Distressing by archaeologists" },
  {
    text = "Return to the archaeologists. This will take 3 hours so if 60 Crafting is achievable by any boosts within that time frame it is highly recommended to do that instead.",
    actions = {
      Action.ConversationHighlight:new("Indeed, I am."),
      Action.ConversationHighlight:new("Indeed, I would."),
    },
  },
  { text = "Continue with the section Collecting scabarite notes." },
  { text = "While standing in the desert, use Ugthanki dung on the bronze items.", title = "Distressing by players" },
  { text = "Use the oxidized bronze items on the crate." },
  { text = "Travel to Pollnivneach (Pollnivneach Teleport or Magic carpet network are easiest)." },
  { text = "Enter the kebab shop North-West of the well in the center of the town." },
  {
    text = "Talk to Isma'il the Kebab seller and purchase Red hot sauce",
    actions = { Action.ConversationHighlight:new("Would you sell me that bottle of special kebab sauce?") },
  },
  { text = "Head to the building South-West." },
  { text = "Go inside the fenced area with two Jasim the Camels." },
  { text = "Use the Red hot sauce on the trough to the South." },
  { text = "Use a bucket on the dung produced by Jasim the Camel to get Ugthanki dung." },
  { text = "While standing in the desert, use Ugthanki dung on the bronze items." },
  { text = "Use the oxidized bronze items on the crate." },
  { text = "Use the bronze gear on the crate." },
  {
    text = "Return to the archaeologists. This will take 3 hours so if 60 Crafting is achievable by any boosts within that time frame it is highly recommended to do that instead.",
    actions = {
      Action.ConversationHighlight:new("Indeed, I am."),
      Action.ConversationHighlight:new("Indeed, I would."),
    },
  },
  { text = "Continue with the section Collecting scabarite notes." },
  { text = "While standing in the desert, use Ugthanki dung on the bronze items." },
  { text = "Use the oxidized bronze items on the crate." },
  { text = "Travel to Pollnivneach (Pollnivneach Teleport or Magic carpet network are easiest)." },
  { text = "Enter the kebab shop North-West of the well in the center of the town." },
  {
    text = "Talk to Isma'il the Kebab seller and purchase Red hot sauce",
    actions = { Action.ConversationHighlight:new("Would you sell me that bottle of special kebab sauce?") },
  },
  { text = "Head to the building South-West." },
  { text = "Go inside the fenced area with two Jasim the Camels." },
  { text = "Use the Red hot sauce on the trough to the South." },
  { text = "Use a bucket on the dung produced by Jasim the Camel to get Ugthanki dung." },
  { text = "While standing in the desert, use Ugthanki dung on the bronze items." },
  { text = "Use the oxidized bronze items on the crate." },
  { text = "While standing in the desert, use Ugthanki dung on the bronze items." },
  { text = "Use the oxidized bronze items on the crate." },
  { text = "Travel to Pollnivneach (Pollnivneach Teleport or Magic carpet network are easiest)." },
  { text = "Enter the kebab shop North-West of the well in the center of the town." },
  {
    text = "Talk to Isma'il the Kebab seller and purchase Red hot sauce",
    actions = { Action.ConversationHighlight:new("Would you sell me that bottle of special kebab sauce?") },
  },
  { text = "Head to the building South-West." },
  { text = "Go inside the fenced area with two Jasim the Camels." },
  { text = "Use the Red hot sauce on the trough to the South." },
  { text = "Use a bucket on the dung produced by Jasim the Camel to get Ugthanki dung." },
  { text = "While standing in the desert, use Ugthanki dung on the bronze items." },
  { text = "Use the oxidized bronze items on the crate." },
  {
    text = "Talk to Simon Templeton at the Agility Pyramid.",
    title = "Collecting scabarite notes",
    neededItems = {
      ["Pyramid top"] = { quantity = 1 },
      ["Gold artefact"] = { quantity = 1 },
      ["Smelly crate"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Some archaeologists asked me to deliver some artefacts to you."),
      Action.ConversationHighlight:new("I'd like to supply some gold objects from Pyramid Plunder."),
      Action.ConversationHighlight:new("I'd like to supply a golden Agility Pyramid artefact."),
      Action.ConversationHighlight:new("I'd like to supply a crate of bronze antiquities."),
    },
  },
  {
    text = "Talk to the archaeologists (back at the campfire along the shortcut to the east) for a book scabaras research.",
  },
  {
    text = "Kill undead creatures nearby until you get 4 different scabarite notes. They should have different examine texts.<ul><li>Dried zombies and skeletons to the north.</li><li>Mummies to the west.</li><li>Locust lancers, rangers, scabaras lancers, and rangers in the swamp to the south-east.</li></ul>",
  },
  { text = "Copy the notes to the scabaras research." },
  { text = "Talk to the archaeologists for a key scabarite key." },
  { text = "Run north and enter the northernmost dungeon." },
  {
    text = "This section is a race against time. Read it fully before continuing. You need to pass all challenges successfully in one go, otherwise you need to tell the clay golem to reset the levers so you can try again.",
    title = "Dungeon puzzles",
  },
  {
    text = "It is possible to empty the storage box of logs so you might need to bring your own in case you fail at first attempt. If you have a lower Firemaking level, you might not be able to stoke the furnace. For example, the storage box may give you magic logs, but if your Firemaking level is below 95, you won't be able to continue, unless you bring your own logs.",
  },
  { text = "Use the logs on the furnace to stoke the furnace.", title = "Challenges" },
  {
    text = "Talk to the golem.",
    actions = {
      Action.ConversationHighlight:new("I am ready for you to operate the levers."),
      Action.ConversationHighlight:new("Set the mysterious box room to the highest power."),
      Action.ConversationHighlight:new("Set the empty room to the second highest power."),
      Action.ConversationHighlight:new("Set the narrow walkway room to the second lowest power."),
    },
  },
  { text = "Relight the furnace." },
  { text = "Enter the narrow walkway room.<ul><li>Cross the pipe and pull the lever.</li></ul>" },
  { text = "Enter the empty room.<ul><li>Pull the lever.</li></ul>" },
  { text = "Enter the scarab room.<ul><li>Kill the level 92 giant scarab.</li><li>Pull the lever.</li></ul>" },
  {
    text = "Enter the mysterious box room.<ul><li>Investigate the mysterious mechanism to start the puzzle. Clicking outside of the interface will fail the attempt and you'll have to start the whole process over.</li><li>Starting from the upper left corner, note the runes shown, flipping three runes in a row. Do this for all rows until all runes are noted. You should finish with 8 attempts left.</li><li>On the right side of the interface to increase your attempts from 20 to 35. Click on the cog, mechanical screw parts and dial (three areas) and you will receive an extra 15 attempts giving 35 in total. If you don't, you will fail partway through the puzzle in the next section!</li><li>Flip the rune in the upper left corner. Use your notes to identify the locations of other matching runes and flip them over (You are matching groups of 3 runes). Continue until all runes have been flipped.</li></ul>",
  },
  {
    text = "Once you have completed all 4 challenges in a row, it is possible to teleport out and restock without having to do them again.",
  },
  {
    text = "Run to the High Priest of Scabaras in the north-west room in the dungeon. Disarm the traps along the way.",
    title = "Vault area",
  },
  {
    text = "Talk to the High Priest.",
    actions = {
      Action.ConversationHighlight:new("Conversion, eh?"),
      Action.ConversationHighlight:new("I do not fear your pathetic beetle-brained magics."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Scabaras provides power to those who follow him. You will squirm like a day-old larva."
      ),
    },
  },
  { text = "Defeat him." },
  {
    text = "Talk to him again.",
    actions = {
      Action.ConversationHighlight:new("By all the deities, what is going on here?"),
      Action.ConversationHighlight:new("I sense the hand of the Devourer in this. Am I correct?"),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Defeat him." },
  {
    text = "Talk to him once more.",
    actions = {
      Action.ConversationHighlight:new("By all the deities, what is going on here?"),
      Action.ConversationHighlight:new("Sounds like the Devourer dragged you into heresy for her own ends."),
    },
    postconditions = {
      Condition.ConversationText:new(" I am the High Priest of Scabaras! Accusing me of heresy is...is heresy!"),
    },
  },
  { text = "Defeat him." },
  {
    text = "Talk to him once more.",
    actions = {
      Action.ConversationHighlight:new("Conversion, eh?"),
      Action.ConversationHighlight:new("I sense the hand of the Devourer in this. Am I correct?"),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Return to the High Priest in Sophanem." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Dealing with Scabaras",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.long,
  releaseDate = 1199232000,
  prereqQuests = { "Contact!" },
})
