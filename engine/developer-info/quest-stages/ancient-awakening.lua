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
    text = "Talk to Overseer Siv in the Command Centre in the Fort Forinthry.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about 'Ancient Awakening'.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Go north of Fort Forinthry and enter the Wilderness crypt entrance. Beware of risen ghosts inside." },
  {
    text = "Go across the bridge and enter the ancient door.<ul><li>If the bridge is missing, log out and back in to have it reappear.</li></ul>",
    title = "Finding artefacts",
  },
  {
    text = "Talk to any character near the map table.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Collect 20 dragonkin artefacts in the tomb. Kill any tomb zombies that appear, and then collect the artefacts they drop. The zombies are based on your combat level, e.g. 3,200 life points at combat level 152. One can be collected from each Dragonkin artefact hotspot throughout the crypt as well.",
  },
  { text = "Talk to anyone near the map table again.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Go to the Jolly Boar Inn, south-west of Fort Forinthry. Use the marker to continue the quest at the entrance, then stay on the ground floor[UK]1st floor[US].",
    title = "Jolly Boar Inn",
  },
  {
    text = "Talk to Bill at the bar.",
    actions = { Action.ConversationHighlight:new("I don't need to know your family history, Bill.") },
    postconditions = { Condition.ConversationText:new(" I should be off now, anyways. Goodbye, Your Grace.") },
  },
  {
    text = "Go up the staircase, talk to Ellamaria.",
    actions = { Action.ConversationHighlight:new("Let's skip the small talk.") },
  },
  { text = "Continue the quest with the marker near the bridge west of the Champions' Guild." },
  {
    text = "Talk to Aster slightly south.",
    actions = { Action.ConversationHighlight:new("I need you to return to the fort.") },
    postconditions = {
      Condition.ConversationText:new(
        " We will be embarking on an expedition to Ungael. I need you back at the fort to discuss preparations."
      ),
    },
  },
  { text = "Go back to Fort Forinthry, continue Ancient Awakening at the Town Hall." },
  { text = "Talk to anyone in the Town Hall." },
  {
    text = "Leave through the marker at the entrance to the Town Hall.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Gear up for combat.<ul><li>Most of the enemies are weak to magic (earth or fire spells)</li></ul>",
    title = "Ungael",
  },
  {
    text = "Go to the Grove east of the fort and interact with the rowboat to travel to Ungael.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Kill all of the attacking monsters." },
  { text = "Talk to the team and make your way north-east up the path." },
  { text = "Interact with the Ruins Entrance." },
  {
    text = "Go up the stairs to the east, then head west. Interact with the tower entrance.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Watch the cutscene." },
  {
    text = "Defeat 12 waves of Zemouregal's minions. After each wave of enemies, you have to activate necrotic energy and sometimes choose a power-up for one of your allies. See the full guide for details on all waves and power-ups.",
    title = "Undead waves",
  },
  { text = "Watch the cutscene." },
  {
    text = "Talk to Aster.",
    title = "Getting off the island",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Interact with the doorway to the south." },
  { text = "Walk west, then south, then interact with runic projector." },
  {
    text = "Collect three motes of energy from:<ul><li>Go north to catch the mote just outside the tower.</li><li>Searching bookcase in the north.</li><li>Searching ruined pillar in the south-west.</li></ul>",
  },
  { text = "Interact with the runic projector." },
  {
    text = "Listen to the archivist.",
    title = "Finishing up",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("What is this place?"),
      Action.ConversationHighlight:new("Tell me about Vorkath."),
      Action.ConversationHighlight:new("Tell me about this tomb."),
      Action.ConversationHighlight:new("Can you activate this conduit?"),
    },
  },
  { text = "Inspect the mysterious device to the south-east." },
  { text = "Finish the conversation." },
  { text = "Return to Fort Forinthry and continue via the marker in front of the Town Hall." },
  { text = "Talk to anyone in the Town Hall." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Ancient Awakening",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1696809600,
  prereqQuests = { "Dead and Buried" },
})
