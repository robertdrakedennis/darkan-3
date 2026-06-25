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
    text = "Talk to the monk just outside of Paterdomus which is east of Silvarea.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head to Doric's hut north of the Falador lodestone and use a mithril 2h sword on the whetstone outside of the hut.",
    title = "The weapon",
    neededItems = { ["Bowstring"] = { quantity = 1 }, ["Mithril 2h sword"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Use a bowstring on the slender blade." },
  { text = "Return to the monk.", actions = { Action.ConversationHighlight:new("Yep, got it right here for you.") } },
  {
    text = "Use the orb on a large pouch.",
    title = "Entrana",
    neededItems = { ["Large pouch"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Bank everything that you are not allowed to take to Entrana, and ensure you keep the large pouch in your inventory.",
  },
  {
    text = "Reach Entrana either through the Abyss or by using a law altar teleport.<ul><li>A wicked hood will not work here.</li><li>If entering through the Abyss, enter the Abyss via the Mage of Zamorak north of Edgeville, then make your way to the law rift. Dismiss any summoning familiar. Leave the Law Rune Temple by using the portal.</li></ul>",
  },
  { text = "Head into the church of Entrana to the south." },
  { text = "Use the pouch on the prayer altar. Doing this will cause you to lose your large pouch." },
  { text = "Watch the cutscene.<ul><li>Talk to the High Priest if you didn't during the cutscene.</li></ul>" },
  { text = "Return to the monk, back outside Paterdomus.", title = "Finishing up" },
  { text = "Search the Dead Monk." },
  { text = "Talk to the High Priest back on Entrana.<ul><li>A wicked hood works now.</li></ul>" },
  {
    text = "Talk to Sir Tiffy Cashien in Falador Park.<ul><li>if The Slug Menace has not yet been started.</li></ul>",
    actions = { Action.ConversationHighlight:new("Devious Minds") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Devious Minds",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1134950400,
  prereqQuests = { "Recruitment Drive", "What's Mine is Yours", "Morytania" },
})
