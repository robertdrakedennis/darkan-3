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
    text = "Talk to Eluned in the Ithell Clan district, next to the Singing Bowl.",
    title = "Starting off",
    neededItems = { ["Enchanted key (Meeting History)"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Of course!") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Climb the stairs in the Tower of Voices.",
    actions = {
      Action.ConversationHighlight:new("The Light Within."),
      Action.ConversationHighlight:new("I've come to offer my help."),
      Action.ConversationHighlight:new("How do we decide?"),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("What now?"),
    },
  },
  {
    text = "Go to Guthixian ruins in the caves west of the Legends' Guild, northwest of Fairy ring <b>B L R</b>.",
    title = "Time-travelling",
    neededItems = { ["Enchanted key (Meeting History)"] = { quantity = 1 } },
  },
  { text = "Walk up to Guthix as close as possible and rub the enchanted key." },
  {
    text = "Talk to Guthix.",
    actions = {
      Action.ConversationHighlight:new("I am the world guardian."),
      Action.ConversationHighlight:new("I need to restore Seren."),
    },
  },
  {
    text = "Go west and talk to Haluned.",
    actions = {
      Action.ConversationHighlight:new("Yes, we have."),
      Action.ConversationHighlight:new("What are you doing here?"),
      Action.ConversationHighlight:new("Are you related to Eluned?"),
      Action.ConversationHighlight:new("[Continue...]"),
    },
  },
  {
    text = "Talk to Guthix.",
    actions = { Action.ConversationHighlight:new("[Continue...]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Return to the conclave in the Tower of Voices.",
    actions = { Action.ConversationHighlight:new("The Light Within.") },
  },
  { text = "Talk to everyone, talking to Eluned is optional." },
  { text = "Read Baxtorian's journal, given to you by Arianwyn." },
  {
    text = "Equip the Trahaearn exoskeleton set.",
    title = "Shards - Artisan shard",
    neededItems = { ["Trahaearn exoskeleton set"] = { quantity = 1 } },
    recommendedItems = { ["Crystal teleport seed"] = { quantity = 1 } },
  },
  { text = "Investigate the well in Lletya.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Attempt to pass the Trahaearn automaton mk V.",
    actions = {
      Action.ConversationHighlight:new("Rune"),
      Action.ConversationHighlight:new("Cynog"),
      Action.ConversationHighlight:new("Five"),
      Action.ConversationHighlight:new("The future is doomed"),
    },
  },
  { text = "Take the Seren shard (prudence)." },
  {
    text = "Talk to Coeden, the living tree surrounded by ivy in the Crwys Clan district.",
    title = "Naturalist shard",
    actions = { Action.ConversationHighlight:new("The Light Within.") },
  },
  {
    text = "Click on the elven clan symbols which appear throughout the dialogue.<ul><li>You will receive a wintercup mushroom.</li></ul>",
  },
  {
    text = "Pick a bloodcap mushroom just west of the Tirannwn mushroom farming patch. It can be reached by teleporting to the Tirannwn lodestone and going west through the dense forest. Players with a Tirannwn quiver 3 or 4 can use it to teleport directly to the mushroom patch.",
  },
  { text = "Combine the mushrooms with a grand defence potion to create an Elixir of Revealment." },
  {
    text = "Travel to the currently active crystal tree (excluding the one in Prifddinas) and use the elixir on any of the tree's shards. Players with the Nature's sentinel outfit can teleport there directly.",
  },
  { text = "Enter the cave entrance that appears and take the Seren shard (harmony)." },
  {
    text = "Go to the Waterfall Dungeon by boarding the log raft at the top of Baxtorian Falls, using a rope on the rock and then the dead tree, and entering the door.<ul><li>Alternatively, it can be reached by teleporting to the Baxtorian Falls resource dungeon.</li></ul>",
    title = "Military shard",
  },
  { text = "Enter the eastern room." },
  { text = "Search the odd coloured crate to find a key." },
  {
    text = "Ensure you have read Baxtorian's journal, then proceed to western room and enter through the two doors to the north.",
  },
  {
    text = "Investigate Statue of King Baxtorian.",
    actions = {
      Action.ConversationHighlight:new("[Polite]My name is Player."),
      Action.ConversationHighlight:new("We need to restore Seren."),
    },
  },
  { text = "Take the Seren shard (integrity)." },
  {
    text = "Go to the Corrupted Seren Stone in the Hefin Cathedral, located in the northern-most part of Prifddinas, a chatbox will appear at the top of the steps.",
    title = "Wisdom shard",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("I'm ready to start!") },
  },
  {
    text = "Lady Hefin will lead you through the 3 sets of poses and incantations, starting with 3 poses and 3 words, then 4, then 5 poses and words. After she speaks each incantation, you must speak the words in the same order she did.<ul><li>Consider writing down the first letter of every word of the incantations.</li></ul>",
  },
  {
    text = "Return to the Tower of Voices and climb up the stairs.<ul><li>There will be a short cutscene.</li><li>Go up the stairs again to return to the conclave.</li></ul>",
    title = "Tarddiad",
    neededItems = { ["Seren shard (wisdom)"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("The Light Within"),
      Action.ConversationHighlight:new("The Light Within"),
    },
  },
  {
    text = "Give the Seren shards to the respective Elders:<ul><li>Lady Meilyr or Lord Crwys - Harmony</li><li>Lady Hefin or Lord Amlodd - Wisdom</li><li>Lord Iorwerth or Arianwyn - Integrity</li><li>Lady Ithell or Lady Trahaearn - Prudence</li></ul>",
  },
  {
    text = "Talk to Morvran in the Iorwerth Clan district.",
    actions = { Action.ConversationHighlight:new("Ask for access to the Grand Library.") },
  },
  { text = "Enter the western door in the Grand Library." },
  {
    text = "Take Seren shard (dark) in the south-western corner on the edge of the platform.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Return to the conclave in the Tower of Voices.",
    actions = { Action.ConversationHighlight:new("The Light Within") },
  },
  { text = "Go to the World Gate." },
  {
    text = "Rotate the controls until you reach .<ul><li>There is currently a bug where the symbol is not synchronised with its actual rotation direction. The first sentence of the description on the 'Travel to Tarddiad' prompt when you attempt to enter the World Gate is 'Through the gate you see a world covered almost entirely by trees.'</li></ul>",
  },
  { text = "Enter the World Gate." },
  {
    text = "Obtain 50 Tarddian crystals by killing crystal Shapeshifters, or by mining solid crystals, chopping branching crystals, or fishing floating crystals to collect 150 crystal fragments and exchanging them with Angof to the north at a ratio of 3 fragments to 1 crystal.<ul><li>Angof has a toggle option to increase the amount of shapeshifters in the area, and make them aggressive towards you.</li></ul>",
  },
  {
    text = "Talk to Angof to get the last piece of the Song of Restoration.",
    actions = {
      Action.ConversationHighlight:new("Hello, pleased to meet you."),
      Action.ConversationHighlight:new("[Leave]"),
    },
  },
  {
    text = "Return to the Tower of Voices by talking to Eluned.<ul><li>If Eluned is beside Angof, you can return with her.</li></ul>",
    actions = { Action.ConversationHighlight:new("Let's return.") },
  },
  { text = "Climb up the stairs.", actions = { Action.ConversationHighlight:new("The Light Within") } },
  {
    text = "Watch the cutscene and go through the dialogue. Skipping won't work. At the end of the cutscene you will arrive on Freneskae with Seren in your arms. (If the options don't pop up leave and return to the room)",
    title = "Freneskae",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'll go now."),
      Action.ConversationHighlight:new("I'm ready."),
    },
  },
  { text = "Avoid Muspah attacks by walking around." },
  { text = "Talk to Zaros." },
  {
    text = "There are three rings in the puzzle: outer, middle and core. To solve the puzzles, rotate any crystal from each ring as many times and in the direction specified below.",
    title = "Light puzzle",
  },
  {
    text = "Puzzle 1<br><table><tbody><tr><td>Outer ring: +2 (2x clockwise)</td></tr><tr><td>Middle ring: -2 (2x anti-clockwise)</td></tr><tr><td>Core ring: -1 (1x anti-clockwise)</td></tr></tbody></table>",
  },
  {
    text = "Puzzle 2<br><table><tbody><tr><td>Outer ring: +2 (2x clockwise)</td></tr><tr><td>Middle ring: +1 (1x clockwise)</td></tr><tr><td>Core ring: -2 (2x anti-clockwise)</td></tr></tbody></table>",
  },
  {
    text = "Puzzle 3<br><table><tbody><tr><td>Outer ring: 0 (no movement)</td></tr><tr><td>Middle ring: -2 (2x anti-clockwise)</td></tr><tr><td>Core ring: -3 (3x anti-clockwise)</td></tr></tbody></table>",
  },
  {
    text = "Puzzle 4<br><table><tbody><tr><td>Outer ring: -2 (2x anti-clockwise)</td></tr><tr><td>Middle ring: +2 (2x clockwise)</td></tr><tr><td>Core ring: -2 (2x anti-clockwise)</td></tr></tbody></table>",
  },
  {
    text = "Talk to Seren.",
    title = "Seren's return",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("Yes") },
  },
  { text = "Return to the conclave in the Tower of Voices." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Light Within",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1440374400,
  prereqQuests = {
    "Meeting History",
    "The Temple at Senntisten",
    "Plague's End",
    "The World Wakes",
    "Fate of the Gods",
  },
})
