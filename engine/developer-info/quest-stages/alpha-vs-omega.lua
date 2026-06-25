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
    text = "Talk to Rasial, the First Necromancer on the bridge in the City of Um.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>If Rasial, the First Necromancer isn't there, home teleport to the City of Um or relog.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Talk to Kharen in City of Um to travel to Rasial's Citadel.<ul><li>You must talk to Kharen; using the right-click travel option will not start the fight.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        "(The following is also shown as a regular overhead message:) Rasial: I think, perhaps, it is finally time to test you. (Continues below.)"
      ),
    },
  },
  { text = "Defeat Hermod, the Spirit of War. Cannot be killed with deathtouched dart.", title = "The fight" },
  { text = "Defeat Rasial, the First Necromancer. Cannot be killed with deathtouched dart." },
  { text = "A cutscene ensues. Continue speaking with Rasial, the First Necromancer." },
  { text = "Return to Death in the City of Um to complete the quest.", title = "Finishing up" },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Alpha vs Omega",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1691366400,
  prereqQuests = { "Remains of the Necrolord", "Soul (Well of Souls)" },
})
