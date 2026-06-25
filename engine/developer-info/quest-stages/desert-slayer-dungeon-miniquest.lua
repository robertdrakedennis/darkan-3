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
    title = "Getting started",
    text = "Enter the well in Pollnivneach.",
  },
  {
    text = "In the room with mighty banshees, pass the mystic barrier to the southwest and climb down the stairs. The quest is just about killing 4 bosses in their specific locations. Below is a recommended order.",
  },
  {
    text = "Mightiest turoth (SW - bottom left barrier)<ul><li>Use Slayer Dart and Protect from Melee prayer. If you stand far away from the Mightiest turoth, it will use Ranged-based attacks on you. Ignore the minions.</li><li>If you choose to use melee, do not forget to bring a leaf-bladed spear or sword.</li></ul>",
  },
  { text = "Kurask overlord (NW - top left barrier)<ul><li>Use same strategies as above.</li></ul>" },
  {
    text = "Basilisk boss (SE - bottom right barrier)<ul><li>Use melee and wield the mirror shield.</li><li>Use Protect from Magic prayer. Also watch your stats as they will be drained with each attack. Bring a restore potion.</li><li>Take a dose of Super strength potion and Super attack potion, a Combat potion, or Super warmaster's potion to help.</li></ul>",
  },
  {
    text = "Monstrous cave crawler (NE - top right barrier)<ul><li>Use melee and Protect from Ranged prayer. The poison from its ranged attack severely reduces the duration of any antipoison potions, including super antipoison. Every second or third hit poisons the player even with potions. An anti-poison totem will not work. The only way to negate the poison is by using a poison purge aura.</li></ul>",
  },
}

return Quest:new({
  name = "Desert Slayer Dungeon (miniquest)",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1212624000,
  prereqQuests = { "Smoking Kills" },
})
