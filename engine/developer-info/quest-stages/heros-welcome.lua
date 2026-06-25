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
    text = "Talk to Freya Lune or Alfrick the Planner near Rellekka docks.",
    title = "Welcoming V",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to the following people about 'V':<ul><li>Yrsa in the clothes shop nearby.</li><li>Peer the Seer who is south-west of the marketplace.</li><li>Swensen the Navigator who is in the house south of the marketplace.</li><li>Sigmund The Merchant in the middle of the marketplace.</li><li>Brundt the Chieftain in the main hall.</li><li>Manni the Reveller, also in the hall.</li><li>Olaf the Bard, east of the hall.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Can I ask you about V?"),
      Action.ConversationHighlight:new("Can I ask you about V?"),
      Action.ConversationHighlight:new("Can I ask you about V?"),
      Action.ConversationHighlight:new("Can I ask you about V?"),
      Action.ConversationHighlight:new("Can I ask you about V?"),
      Action.ConversationHighlight:new("Can I ask you about V?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " He was a great hero to our people. Why couldn't they have gone for one of the bad gods, like Zamorak or Saradomin?"
      ),
    },
  },
  { text = "Return to Alfrick or Freya, right-click celebrate with them.", title = "Scavenging for energy" },
  { text = "Watch the cutscene.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Return to Alfrick at the docks.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Enter cave entrance and talk to V.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Why can't you do it yourself?"),
      Action.ConversationHighlight:new("I need to get moving."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Exit the cave." },
  {
    text = "Investigate 5 locations on the island. There are 2 of each location that you must pair up:<ul><li>2 old docks. (One to the south of the island and one to the east)</li><li>2 fishing boats. (One to the east of the island and one to the west; the western boat is located on land not in the water)</li><li>2 gnarled trees. (One to the south-west of the island and one to the north-west)</li><li>2 cave entrances. (The trapdoors, not the cave you went in earlier) (One in the middle of the island and one to the south-east)</li><li>2 rock formations. (One to the north of the island and one to the north-west)</li></ul>",
  },
  { text = "Return to V in the cave." },
  { text = "After the cutscene, pass the door into the north room." },
  { text = "Search the belongings to the east of the door (south-east corner of the room) and read the journal." },
  { text = "Solve the map fragments." },
  {
    text = "Recommended to bring games necklace, ring of slaying, slayer cape, or fairy ring BJQ for quick teleport.<ul><li>If using a games necklace, travel to the barbarian outpost and run south to dive into the whirl pool. Then climb-down the rough hewn steps to the north-east.</li></ul>",
    title = "Ancient cave",
    neededItems = { ["Soft clay"] = { quantity = 1 }, ["Mithril bar"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Go to the south-west corner of the cavern and climb-up the rough hewn steps." },
  {
    text = "South of Kuradal in the ancient cavern is an ancient door along the western wall. Try to open the door, then use your soft clay on it.",
  },
  {
    text = "Go to the mithril dragons. (Head north, go down the rough hewn steps, then up the stairs to the north-east.) Attack a mithril dragon, then use your key mould on the dragon.",
  },
  { text = "Go back south west up the rough hewn steps." },
  {
    text = "Open the ancient door with the key.",
    actions = { Action.ConversationHighlight:new("Yes, I am ready.") },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "Kill the dragon." },
  { text = "After the cutscene, run west to the stepping stones." },
  { text = "Jump diagonally to the chest and open it, then continue north." },
  {
    text = "Don't step on any pressure plates. Push the crates out of your way (you are able to push the 2 crates in a line).<ul><li>Push the first eastern crate north twice.</li><li>Walk around to the middle and push the two crates north twice.</li><li>Push the eastern crate to the east twice, then push the northern-most crate to the west once.</li><li>Push the crate to your south once, and walk to the door.</li></ul>",
  },
  { text = "Open the eastern door and search the chest." },
  { text = "Read the notes and equip the dragonkin protection charm." },
  {
    text = "Enter the bottom floor of the Grotworm Lair near Port Sarim. (There's an agility shortcut from the entrance requiring level 50 Agility.)",
  },
  { text = "Climb the stairs in the centre of the open area, west of the Queen Black Dragon entrance." },
  { text = "Defeat Tarshak, avoiding his charge attack by moving out of the way." },
  {
    text = "Talk to Phalaks.",
    actions = { Action.ConversationHighlight:new("I'm done here.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Take lots of high-healing food, including a full BoB if possible. Wear the Dragonkin protective charm at all times, and pray against melee during stage 2.",
    title = "Final fight",
  },
  {
    text = "Defeat Abomination in the Brimhaven Dungeon in the tunnel found in the south-west corner where steel dragons are.",
  },
  {
    text = "Focus on mining the 4 pillars in the room while avoiding his attacks. Surge, double surge, and bladed dive can be useful in avoiding damage from the Abomination's rotating beam of fire.<ul><li>His charge attacks are often not aimed at the player, meaning you don't always need to move.</li></ul>",
    title = "Stage 1",
  },
  {
    text = "When a pillar cannot be mined any more, lure a fireball attack into the pillar. Click the water pool if you get hit to cool off.",
  },
  { text = "Repeat until all 4 pillars are broken." },
  {
    text = "A good strategy is to stand near the pillar and take the fireball damage, this guarantees the destruction of the pillar as long as it is no longer minable.",
  },
  { text = "Lure Tarshak into the falling rocks, don't stand too close yourself.", title = "Stage 2" },
  { text = "You may also attack with a regular weapon at the same time." },
  {
    text = "Once Tarshak reaches 458 life points, he will no longer take damage from falling rocks, and you must deal the final blow yourself.",
  },
  {
    text = "Watch the cutscene.",
    title = "V's Island",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Return to Freya and Alfrick in Rellekka.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Enter the cave and pass the door." },
  { text = "Talk to Freya or Alfrick." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Hero's Welcome",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1435536000,
  prereqQuests = { "Lunar Diplomacy", "Ancient Cavern", "One of a Kind", "Ritual of the Mahjarrat" },
})
