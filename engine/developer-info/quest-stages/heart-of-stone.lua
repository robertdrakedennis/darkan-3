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
    text = "Dismiss any followers now. If you recently completed Carnillean Rising, you might have to re-log to start the quest.",
    title = "Kipple",
  },
  {
    text = "Interact with Xenia at the Wizards' Tower entrance south of Draynor Village.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "End their fight.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "On the east side of the Wizards' Tower (outside, near the water), talk to Ariane.",
    actions = { Action.ConversationHighlight:new("Okay, I'll talk to Kipple.") },
  },
  {
    text = "Talk to Kipple.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("No") },
  },
  { text = "Solve the puzzle." },
  { text = "Dismiss any followers now." },
  {
    text = "Talk to Kipple and note the location he tells you.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Head to the Karamja Volcano.<ul><li>Kipple will appear near the entrance.</li></ul>", title = "Karamja" },
  { text = "Right-click open tracker with Kipple to solve the puzzle." },
  { text = "Enter the portal." },
  {
    text = "Kill attendants while avoiding the fires until the green bar at the top fills up (takes about 5 minutes).",
  },
  {
    text = "Talk to FulKra.",
    actions = {
      Action.ConversationHighlight:new("Why was Xenia here?"),
      Action.ConversationHighlight:new("No, it's not right."),
      Action.ConversationHighlight:new("I've got no further questions."),
    },
  },
  { text = "Talk to Kipple.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Travel to the north half of Entrana, east of the dungeon entrance.<ul><li>An easy way to get there is using the wicked hood law altar teleport. No weapons or armour are allowed on the island.</li></ul>",
    title = "Entrana",
  },
  { text = "Right-click open tracker with Kipple to solve the puzzle." },
  { text = "Enter the portal and open the Bik door." },
  {
    text = "Equip a weapon (three basic weapons are provided) and kill the attendants while staying close to Kipple.<ul><li>Kipple needs to grab 3 unstable attendants.</li></ul>",
  },
  {
    text = "Once Kipple has placed 3 unstable attendants by the door, light them. NOTE - There is a bug where sometimes you light them and nothing happens. If this happens leave out the portal and come back in.",
  },
  {
    text = "Talk to BikKra.",
    actions = {
      Action.ConversationHighlight:new("What was Xenia doing?"),
      Action.ConversationHighlight:new("I am sorry mighty one, I'll explain."),
      Action.ConversationHighlight:new("They could be useful."),
      Action.ConversationHighlight:new("I don't care"),
      Action.ConversationHighlight:new("I have no more questions."),
    },
  },
  { text = "Talk to Kipple.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Solve the puzzle." },
  { text = "Head to Taverley and climb up White Wolf Mountain.", title = "White Wolf Mountain" },
  { text = "Right-click open tracker with Kipple to solve the puzzle." },
  { text = "Enter the portal." },
  {
    text = "Click the circuit nodes until you match the power.<ul><li>Each circuit node outputs a certain amount of power, but some nodes will consume more power than they generate, causing your total units to decrease instead.</li><li>The white spires will redirect power to its other pair.</li><li>You need to match the number of power units by activating or deactivating the correct nodes.</li></ul>",
  },
  {
    text = "Talk to WenKra.",
    actions = {
      Action.ConversationHighlight:new("I am not TokHaar."),
      Action.ConversationHighlight:new("What was Xenia doing?"),
      Action.ConversationHighlight:new("Yes, it should be available to all."),
      Action.ConversationHighlight:new("I have no more questions."),
    },
  },
  { text = "Talk to Kipple.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Head east of Sophanem, south of the agility pyramid.", title = "Desert" },
  { text = "Right-click open tracker with Kipple to solve the puzzle." },
  { text = "Head south and enter the portal." },
  { text = "Search any shelf." },
  {
    text = "Combine the following (right-click use):<ul><li>Correction with Transition</li><li>Dispersal with Arrival</li><li>Evolution with Imprinting</li><li>Predator with Focus</li><li>Conflict with Condemnation</li></ul>",
  },
  { text = "Combine Ariane and the Golem and Golem and I." },
  { text = "Use the 4 combinations on JasKra." },
  { text = "Talk to JasKra.", actions = { Action.ConversationHighlight:new("Because power corrupts.") } },
  { text = "Talk to Kipple.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Head to the Lumbridge cemetery, south of the church.", title = "Final fight" },
  {
    text = "Enter the portal for a cutscene.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Defeat the boss. The boss can hit up to 2,900 damage. Do not attack the prehistoric abyssal yet, avoid his energy balls. Keep running around the cave until Kipple finds the correct spot.<ul><li>Destroy 3 abyssal anchors in total.</li><li>Watch the cutscene.</li><li>Kill the prehistoric abyssal as normal.</li></ul>",
  },
  { text = "Talk to Ariane." },
  { text = "Quest complete!" },
  {
    text = "Optional: Talk to Ariane back at the Old Tower to complete the Nano to Kipple, Come in Kipple achievement.",
  },
}

return Quest:new({
  name = "Heart of Stone",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1416787200,
  prereqQuests = { "Carnillean Rising", "Rune Memories", "Rune Mechanics", "The Elder Kiln", "Fate of the Gods" },
})
