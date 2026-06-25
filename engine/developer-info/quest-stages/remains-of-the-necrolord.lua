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
    title = "Starting out",
    text = "Talk to Death in the City of Um.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to the three listless dead around Daemonheim to free them:<ul><li>Near the fountain south of the ring of kinship's teleport location.</li><li>Beside the entrance to the Kal'gerion resource dungeon (Dungeoneering cape teleport ).</li><li>Along the east coast, at the Marmaros & Gorgonite Mine north of the Fremennik ship (Shadow reef).</li></ul>",
    actions = {
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("Kal'gerion demon dungeon"),
    },
  },
  {
    text = "Teleport with the ring of kinship again and talk to Lord Yudura (with ghostspeak amulet equipped if you haven't completed the hard Morytania achievements).<ul><li>Lord Yudura is located immediately at the teleport location, but may be difficult to see, as he blends into the snow. Using High Contrast Mode can be helpful.</li></ul>",
    title = "Exploring the floors of Daemonheim",
  },
  { text = "Inspect Captain Toma's corpse." },
  { text = "Inspect Taevas's corpse and Vengeance's corpse." },
  { text = "Inspect Lord Yudura's corpse." },
  {
    text = "Enter the room to the west and take at least the following number from the crates (you can use secondary option to take 5 at a time).<ul><li>12 inkwells of basic ghostly ink</li><li>12 inkwells of regular ghostly ink</li><li>6 inkwells of greater ghostly ink</li><li>4 greater ritual candles</li></ul>",
  },
  {
    text = "Go two rooms east and set up the ritual site.<ul><li>Place Lord Yudura's corpse on the focus object spot.</li><li>Place a greater ritual candle on each of the light source spots.</li><li>Draw a glyph on each of the glyph spots.</li></ul>",
  },
  { text = "Return west and talk to Lord Yudura." },
  { text = "Talk to Lord Yudura.", title = "After the ritual" },
  { text = "Go to the southern room." },
  { text = "Talk to Lord Yudura again." },
  { text = "Kill the skeletal archer, sorcerer, and warrior." },
  { text = "Talk to Lord Yudura again." },
  { text = "Talk to Death in the City of Um." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Remains of the Necrolord",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1691366400,
  prereqQuests = { "Tomes of the Warlock", "Vengeance (saga)", "Nadir (saga)" },
})
