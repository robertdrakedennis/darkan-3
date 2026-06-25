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
    text = "Kill Agrith-Na-Na.",
    title = "Agrith-Na-Na",
  },
  {
    text = "Agrith-Na-Na uses melee when in range and uses Fire Blast when not in melee range.<br>Ranged attack is effective against him.",
  },
  { text = "Kill Flambeed.", title = "Flambeed" },
  {
    text = "Ice gloves must be used if using a weapon. Be sure to have a backpack space for your weapon, which will automatically be unequipped when engaging Flambeed. If you do not have an backpack space when Flambeed spawns, your ice gloves might be sent to your bank to make room for your weapon in the backpack.<br>Using water spells or ranged attack is effective against him.",
  },
  { text = "Kill Karamel.", title = "Karamel" },
  {
    text = "It is recommended to bring stat restore potions.<br>It is also recommended to fight her in melee range with a fire spell as her melee attacks do little damage.",
  },
  { text = "Kill Dessourt.", title = "Dessourt" },
  {
    text = "Iban Blast and ranged attack works well on him.<br>At least one restore potion is recommended as he has a chance of draining stats.",
  },
  { text = "Kill Gelantinnoth Mother.", title = "Gelantinnoth Mother" },
  {
    text = "<ul><li>Damage can be done to her in this order: (necromancy attack does not work)<ul><li>Wind spells (white)</li><li>Water spells (blue)</li><li>Melee attack (orange)</li><li>Earth spells (brown)</li><li>Fire spells (red)</li><li>Ranged attack (green)</li><li>After green, she repeats the cycle.</li></ul></li></ul>",
  },
  { text = "Kill the Culinaromancer with any combat style.", title = "Culinaromancer" },
  {
    text = "The Culinaromancer has little to no defence but can hit over 200.",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Recipe for Disaster: Defeating the Culinaromancer",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1142380800,
  prereqQuests = {
    "Recipe for Disaster: Another Cook's Quest",
    "Recipe for Disaster: Freeing the Mountain Dwarf",
    "Recipe for Disaster: Freeing the Goblin Generals",
    "Recipe for Disaster: Freeing Pirate Pete",
    "Recipe for Disaster: Freeing the Lumbridge Sage",
    "Recipe for Disaster: Freeing Evil Dave",
    "Recipe for Disaster: Freeing Skrach Uglogwee",
    "Recipe for Disaster: Freeing Sir Amik Varze",
    "Recipe for Disaster: Freeing King Awowogei",
    "Desert Treasure",
    "Horror from the Deep",
  },
})
