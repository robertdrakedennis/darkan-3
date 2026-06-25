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
    text = "Talk to Ali the Wise in Nardah (north-most building).",
    title = "Getting to the tunnels",
    neededItems = {
      ["Trollheim Teleport"] = { quantity = 1 },
      ["Trollheim tablet"] = { quantity = 1 },
      ["God Wars Dungeon Teleport"] = { quantity = 1 },
      ["Climbing boots"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>To get there: teleport to fairy ring DLQ and run south.</li><li>Or teleport to Al Kharid and go south to Shantay Pass, then take a magic carpet to Nardah.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("Where is the tunnel entrance?"),
      Action.ConversationHighlight:new("Time for me to go to Trollweiss, then."),
    },
  },
  {
    text = "Follow the directions on the map below to get to the Ritual plateau tunnels, or use Trollheim Teleport if you have it. If you travel through Death Plateau or Troll Stronghold, you must bring the climbing boots.<ul><li>Prepare for combat before going through Trollheim as you don't have any access to bank in there.</li></ul>",
  },
  {
    text = "Navigate through the caves by mining the rubble; each rubble must be mined once on each side to clear it.<ul><li>Optional: pick up the stone, granite, slate, and shale tablets.</li><li>Optional: Search the small bookcase for notes (a-j) if you want to give all information to Ali the Wise after the quest is completed. There is no reward for doing so.</li></ul>",
    title = "The tunnels",
  },
  { text = "Enter the room east of the caves." },
  { text = "Climb the stairs." },
  {
    text = "Talk to Arrav, who will attack you immediately.<ul><li>After he starts attacking you, if you do not auto-retaliate, right-click and attack him.</li></ul>",
  },
  { text = "Fight until he teleports away. The fight will pause multiple times for dialogue." },
  { text = "Enter the room to the south and search the tapestry on the west wall for base key and base plans." },
  { text = "Travel back to Nardah and speak to Ali the Wise." },
  { text = "After the cutscene, speak with Ali the Wise again." },
  {
    text = "Enter the nearby Uzer Mastaba pyramid.<ul><li>To get there, either run north-east of Ali the Wise, use the nearby magic carpet network to get to Uzer and run south, or teleport to fairy ring DLQ and run east.</li></ul>",
    title = "Preparing the jar",
    neededItems = { ["Dwellberries"] = { quantity = 1 }, ["Ring of life"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Enter the pyramid, head north-west through the imposing doors, then south, down the stairs and talk to Senliten for oil in a canopic jar.<ul><li>Players who have completed certain quests can also choose to head directly to the Pharaoh Queen from the pyramid doors.</li></ul>",
    actions = { Action.ConversationHighlight:new("Talk about Curse of Arrav.") },
  },
  {
    text = "Use (do not eat) your dwellberries and ring of life on the oil in a canopic jar to obtain a full canopic jar.",
  },
  {
    text = "Return to Ali the Wise and speak to him.<ul><li>If Leela is in the mummy's tomb, she can return you outside the Uzer Mastaba .</li></ul>",
    actions = { Action.ConversationHighlight:new("Return to the surface") },
  },
  {
    text = "Go to the Varrock Palace and speak to Hartwin (northwest tower, 2nd floor[UK]3rd floor[US]) to travel to Chaos Temple Dungeon.",
    title = "Saving Arrav's heart",
    neededItems = {
      ["Insulated boots"] = { quantity = 1 },
      ["Macaw pouch"] = { quantity = 1 },
      ["Ravenous locust pouch"] = { quantity = 1 },
      ["Crossbow"] = { quantity = 1 },
      ["Mithril grapple"] = { quantity = 1 },
      ["Enhanced grappling hook"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Climb down the trapdoor." },
  { text = "Follow the path, opening three doors along the way." },
  { text = "Head east to the room with the range." },
  { text = "Equip your insulated boots then enter the pipe on the south wall of the room." },
  { text = "Run through the sewers and attempt to enter the next pipe." },
  { text = "Summon your familiar, interact with it and choose the remote view option." },
  { text = "Run to the room south of you, search all the tables for decoder strips and notes (k-z)." },
  { text = "Right-click pick-lock on the chest to obtain the code key." },
  {
    text = "Go north to the room east of the pipe and operate the keypad and solve the keypad puzzle:<ul><li>First read the code key and memorise or write down the letters.</li><li>Drag-and-drop strip #1 over the line indicated by the first letter of the code key (A, B, C...).</li><li>The strip has a hole that reveals a specific number, this is the first digit of the passcode.</li><li>Use the up and down arrows to enter that number, and press the right arrow to move to the next digit.</li><li>Repeat this for the other three strips.</li></ul>",
  },
  { text = "Enter the room, equip your crossbow and mithril grapple, then grapple the pipe above you." },
  { text = "Take from the pedestal for a heart in a canopic jar, then teleport out." },
  { text = "Return to Ali the Wise and speak to him." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Curse of Arrav",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1248134400,
  prereqQuests = {
    "The Tale of the Muspah",
    "Missing My Mummy",
    "Defender of Varrock",
    "Temple of Ikov",
    "Trollheim",
    "Troll Stronghold",
    "Senliten",
    "Missing My Mummy",
  },
})
