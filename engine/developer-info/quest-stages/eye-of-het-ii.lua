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
    text = "Talk to Icthlarin at the entrance to Het's Oasis.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to the stranger near the tents north-east of the oasis.", title = "Gather more information" },
  { text = "Search the chest just outside of the tent with the bank chest to find some badly-hidden notes." },
  {
    text = "Read the badly-hidden notes.<ul><li>Make sure to proceed through all the dialogue, or you won't be able to progress.</li></ul>",
  },
  {
    text = "Talk to Azzanadra at the Glacor Front (south of the cathedral in Senntisten). You can teleport to the Arch-Glacor for fast access.<ul><li>Wearing the shard of Erebus will keep glacors from attacking along the way.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about the Duel Arena."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Head to the cathedral and talk to the gods.  (You can use the pontifex shadow ring to teleport directly to the cathedral.)<ul><li>Talk to Seren.</li><li>Talk to Zamorak.</li><li>Talk to Armadyl.</li><li>Talk to Saradomin.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about the Duel Arena."),
      Action.ConversationHighlight:new("Talk about the Duel Arena."),
      Action.ConversationHighlight:new("Talk about the Duel Arena."),
      Action.ConversationHighlight:new("Talk about the Duel Arena."),
    },
    postconditions = { Condition.ConversationText:new(" But... Zamorak...") },
  },
  {
    text = "Attempt to talk to Trindine in the following locations.<ul><li>At the northern entrance to the cathedral.</li><li>West near Nex at the stairs near the Nodon Front.</li><li>South beside Helwyr at the Glacor Front.</li><li>East over the bridge at the Croesus Front.</li><li>North of the Senntisten Dig Site's archaeologist's workbench.</li></ul>",
    title = "Following Trindine",
    actions = { Action.ConversationHighlight:new("Gather Everyone.") },
  },
  { text = "Talk to any god in the cathedral. (Select who you want to accuse)" },
  {
    text = "Talk to Azzanadra.  (If the player logs out or teleports out during this sequence, they can speak to Azzanadra at the Glacor Front to resume it.)",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to Icthlarin at the entrance to Het's Oasis again.", title = "Finishing up" },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Eye of Het II",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1641772800,
  prereqQuests = { "Eye of Het I" },
})
