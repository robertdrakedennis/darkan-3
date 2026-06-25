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
    text = "Equip all ghostly robe outfit, the ring of visibility and a ghostspeak amulet or cramulet.",
    title = "Ghosting",
  },
  {
    text = "Find and then talk to the ghost of General Khazard, west of the swaying tree south of Fremennik Province lodestone.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Uh, well, you don't really have eyes, so how can you tell?") },
  },
  {
    text = "[Accept Quest]<ul><li>Khazard, and all of the ghost scouts you'll locate next, cannot be seen on the minimap and have a fair-sized wander radius.</li><li>It is recommended to use the skybox bloodstone if the player has completed The Lord of Vampyrium quest but can be done without, or use High Contrast Mode under Accessibility Options.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Talk to Sin Seer in Seers' Village. She is upstairs in the spinning wheel house.",
    actions = { Action.ConversationHighlight:new("Bribe"), Action.ConversationHighlight:new("Here's the money.") },
  },
  { text = "Return to General Khazard." },
  {
    text = "Talk to the ghost scout south-east of Tai Bwo Wannai (search between the hardwood grove and the drakolith mine).",
  },
  { text = "Talk to the ghost scout south of the Tree Gnome Stronghold entrance (east of the Outpost)." },
  { text = "Talk to the ghost scout in the area between Falador and Draynor Manor." },
  { text = "Talk to the ghost scout south of the Shantay Pass." },
  { text = "Return to General Khazard." },
  { text = "Prepare for battle before fighting against a level 91 enemy." },
  { text = "Enter the goblin cave east of the Fishing Guild." },
  {
    text = "Run to the northeast 'room' and enter the crack in the southern wall.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Kill the Bouncer. You'll throw the severed leg when its health reaches 0.<ul><li>If Bouncer's health is stuck at 0, the player should leave the room, dismiss any familiars, and reenter to try killing Bouncer again. Once at 0 health, continue clicking on him until your character automatically throws the severed leg.</li></ul>",
  },
  {
    text = "Miniquest complete!",
  },
}

return Quest:new({
  name = "The General's Shadow (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1171238400,
  prereqQuests = { "The Curse of Zaros (miniquest)", "Fight Arena" },
})
