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
    text = "Talk to The Curator at the Heart of Gielinor, north of the Agility Pyramid (fairy code DLQ and run southwest).  Make sure to have at least 4 open inventory spots.",
    title = "Curating the tales",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("[Continue...]"),
      Action.ConversationHighlight:new("[More...]"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Be sure to bring the chapters with you to fill them." },
  {
    text = "For chapter #1, collect 20 memory fragments (the green orbs) at the incandescent wisp Divination colony, south of the Poison Waste in Isafdar, west of Castle Wars (fairy code BKP and run west). This will grant 250 Divination XP each, 5,000 Divination XP in total.",
  },
  {
    text = "For chapter #2, use the empty chapter on the Barrows reward chest. This step will grant you 5,000 Summoning XP.",
  },
  {
    text = "For chapter #3, kill 40 Zamorak creatures in the God Wars Dungeon to gain access to K'ril Tsutsaroth (killcount is not required if Totem of Intimidation is active on Anachronia, or if you have unlocked War's Blessing 2). Kill K'ril Tsutsaroth once at the God Wars Dungeon (practice or hard mode also count).",
  },
  {
    text = "For chapter #4, kill 20 each of young grotworms, grotworms, and mature grotworms in any order at the Grotworm Lair, the entrance of which is at the White Knight Camp, west of Port Sarim. This step will grant you 5,000 Slayer XP.",
  },
  { text = "Return to the Heart of Gielinor." },
  {
    text = "Read the floating tome to deposit the chapters.",
    actions = { Action.ConversationHighlight:new("Add the chapters to the tome.") },
  },
  {
    text = "Read the tome again and select the final memory; watch the cutscene. You do not have to watch the other memories to complete the miniquest, but you have to watch them if you want to unlock the music tracks for the Music Maestro achievement.",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("[More...]"),
      Action.ConversationHighlight:new("Final memory."),
    },
  },
  { text = "Talk to The Curator." },
  { text = "Miniquest complete!" },
}

return Quest:new({
  name = "Tales of the God Wars (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1457308800,
  prereqQuests = { "Morytania", "God Wars Dungeon", "Troll Stronghold" },
})
