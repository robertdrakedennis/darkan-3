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
    text = "Talk to Commander Veldaban in the building west of the bank in west Keldagrim.",
    title = "Initial Battle",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Do you have a quest for me?") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Do you think the Red Axe is behind it?") },
  },
  {
    text = "After the cutscene, continue dialogue with Commander Veldaban.",
    actions = {
      Action.ConversationHighlight:new("I'll deal with the chaos dwarf."),
      Action.ConversationHighlight:new("I'm ready."),
    },
  },
  {
    text = "Head upstairs to the roof in the building next to you, kill the chaos dwarf and pick up the mysterious slip.",
  },
  {
    text = "Talk to Commander Veldaban about the slip.",
    actions = {
      Action.ConversationHighlight:new("The chaos dwarf dropped a slip of paper."),
      Action.ConversationHighlight:new("I'll take the slip to the Grand Exchange now."),
    },
  },
  {
    text = "Talk to any Grand Exchange clerk at the Grand Exchange in Varrock.<ul><li>Grand Exchange clerks in Prifddinas, the Max Guild, and Menaphos do not work.</li><li>If you have never used the Grand Exchange (common for ironmen), the Grand Exchange clerk will refuse to talk to you. Speak to the Grand Exchange Tutor just south and state that 'I want the basics from you.' before talking to the Grand Exchange clerk.</li></ul>",
    actions = { Action.ConversationHighlight:new("I have this dwarven order slip...") },
  },
  {
    text = "Talk to Veldaban in the Laughing Miner pub in eastern Keldagrim (if he doesn't appear, logout and log back in. In some cases, even after a client restart, he may not appear visually, if this is the case you may click on an invisible Veldaban by clicking on the the northern seat of the most northwestern table in the Laughing Miner pub to speak to him).",
    title = "Drunk Commander",
    actions = {
      Action.ConversationHighlight:new("What are you doing here?"),
      Action.ConversationHighlight:new("Do you think the Red Axe is involved?"),
    },
  },
  {
    text = "Ask the barmaid for his stout.",
    actions = {
      Action.ConversationHighlight:new("Commander Veldaban wants a Guardsman's Stout."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Veldaban about the battle then take him to the headquarters.",
    actions = {
      Action.ConversationHighlight:new("I didn't know you had a lady-friend."),
      Action.ConversationHighlight:new("What was the Battle of Barendir?"),
      Action.ConversationHighlight:new("The barmaid said she'd seen you with someone."),
      Action.ConversationHighlight:new("Let's talk about something else."),
      Action.ConversationHighlight:new("Are you drunk?"),
      Action.ConversationHighlight:new("What are you doing here?"),
      Action.ConversationHighlight:new("Do you think the Red Axe is involved?"),
      Action.ConversationHighlight:new("Do you think the missing persons are connected to the chaos dwarves?"),
      Action.ConversationHighlight:new("Who's on the case now?"),
      Action.ConversationHighlight:new("Why did he take you off the case?"),
      Action.ConversationHighlight:new("Let's talk about something else."),
      Action.ConversationHighlight:new("I have the Grand Exchange Package"),
      Action.ConversationHighlight:new("Let's Talk about something else"),
      Action.ConversationHighlight:new("Let's go."),
    },
  },
  {
    text = "After the cutscene, head back to watchtower in the south area of west Keldagrim, where you fought the chaos dwarf.",
  },
  { text = "Open the package and take all the items.", title = "Investigation" },
  { text = "Use the boot on the footprints south of the watchtower building (where you defeated the chaos dwarf)." },
  {
    text = "Follow the footsteps south and then west to reach a small fire pit (the one across the bridge - it looks different from the other fire pits in the area).",
  },
  { text = "Use the bowl on the fire pit, remove it, then use the rod on the fire pit." },
  { text = "Turn the rod." },
  { text = "Enter the tunnel west, then enter the doorway at the west end." },
  { text = "Free your hands." },
  { text = "Attempt to ride the train cart (you don't go far)." },
  {
    text = "Leave through the tunnel back to Keldagrim.",
    actions = { Action.ConversationHighlight:new("Return to Keldagrim.") },
  },
  { text = "Dismiss any followers you may have." },
  {
    text = "Talk to Commander Veldaban back where the quest started  and take him back to the caves.",
    actions = {
      Action.ConversationHighlight:new("I found a secret door in the mines."),
      Action.ConversationHighlight:new("Let's go."),
    },
  },
  {
    text = "Try to control the Dwarven Machinery and have Veldaban do it while you ride the cart.",
    title = "Cart Labyrinth",
    actions = { Action.ConversationHighlight:new("You take the stones and man the board. I'll ride the cart.") },
  },
  { text = "Click on the cart to begin." },
  {
    text = "Follow these choices:<ul><li>Yellow</li><li>Green</li><li>Yellow</li><li>Green</li><li>Green</li><li>Yellow</li><li>Yellow</li><li>Yellow</li><li>Yellow</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Yellow Stone"),
      Action.ConversationHighlight:new("Green Stone"),
      Action.ConversationHighlight:new("Yellow Stone"),
      Action.ConversationHighlight:new("Green Stone"),
      Action.ConversationHighlight:new("Green Stone"),
      Action.ConversationHighlight:new("Yellow Stone"),
      Action.ConversationHighlight:new("Yellow Stone"),
      Action.ConversationHighlight:new("Yellow Stone"),
      Action.ConversationHighlight:new("Yellow Stone"),
    },
  },
  { text = "Pull the bridge lever." },
  { text = "Head south to the prison and open the door.", title = "Rescue and Escape" },
  {
    text = "Talk to Fjoila.",
    actions = {
      Action.ConversationHighlight:new("You should get back to Keldagrim now."),
      Action.ConversationHighlight:new("You told the prisoner we'd look for Hilda."),
    },
  },
  {
    text = "Head south, turn either of the switches and go through the doorway. Commander Veldaban may be stuck up north.",
  },
  { text = "Watch the cutscene.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "After the cutscene, talk to Veldaban in the room.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("That wasn't Hilda. Hilda was gone by then."),
      Action.ConversationHighlight:new("There are still some things in the package."),
    },
  },
  { text = "Use the metal rectangle then the turnscrew on the sorting machine." },
  { text = "Squeeze through the hole and ride the train cart for a cutscene." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Forgiveness of a Chaos Dwarf",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1252454400,
  prereqQuests = { "Forgettable Tale of a Drunken Dwarf", "Between a Rock..." },
})
