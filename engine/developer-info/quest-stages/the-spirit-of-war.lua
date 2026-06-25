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
    text = "Interact with Kharen on the docks in the north-western City of Um, close to the transportation icon.",
    title = "Rescuing Wayward Souls",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Either:<ul><li>Speak to Ed in a building to the north-east to rent a boat.</li><li>Or choose the option Rent boat on Ed.</li></ul>",
    actions = { Action.ConversationHighlight:new("Take a boat out onto the lake.") },
  },
  {
    text = "Rescue four Wayward Souls. They are ghastly sphere shapes floating above the Lake Mnemosyne in each of the four corners of the map. Some may be a good distance away from the coastline.<ul><li>High contrast mode is beneficial for spotting them.</li><li>Dive and Surge can be used to speed up movement. The Mobile perk (must be on an armour piece, as both hands must be free) and using a powerburst of acceleration will also help.</li><li>(Optional) Talk to Deano and Sammy when you see them for a reward of 25 souls each.</li></ul>",
  },
  { text = "Return back to the docks and disembark in the dock south of Kharen. (Or teleport out of the rowboat.)" },
  {
    text = "Prepare for combat and don't forget to re-equip your weapons.",
    title = "Fighting Hermod, the Spirit of War",
  },
  {
    text = "Interact with Kharen.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Go through the dialogue to travel to Rasial's Citadel." },
  { text = "Speak to Rasial, the First Necromancer." },
  {
    text = "Fight Hermod, the Spirit of War in story mode.<ul><li>Kill minions when they appear, otherwise Hermod will be immune to damage</li></ul>",
  },
  { text = "Speak to Rasial, the First Necromancer once more." },
  { text = "Return and interact with Kharen with two free inventory spots.", title = "Finishing up" },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Spirit of War",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1691366400,
  prereqQuests = { "Vessel of the Harbinger", "Soul (Well of Souls)", "Kili Row" },
})
