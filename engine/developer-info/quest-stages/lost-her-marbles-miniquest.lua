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
    text = "Return to Darren Lightfinger.<ul><li>Talk to Darren . Talk to Robin to collect your loot.</li><li>Repeat 4 times in total.</li></ul>",
    title = "Overview",
    actions = { Action.ConversationHighlight:new(". I've had some success tracking down flame fragments.") },
  },
  {
    text = "Collect a total of 32 flame fragments from any of the NPCs (maximum 6 per type of NPC). You only need to pickpocket 6 different NPCs and only need 2 fragments from the final type of NPC you choose to steal from. The flame fragments are unreclaimable upon death, you can bank them before going to the next location.",
    title = "Collecting fragments",
  },
  { text = "Men and women (Lumbridge Market, and Al Kharid, at the marketplace)" },
  { text = "H.A.M. members" },
  { text = "Farmers (Lumbridge, in the chicken pen area east of the river)" },
  { text = "Master Farmers (Draynor Village, roaming in the marketplace; Lumbridge near the vegetable stall)" },
  { text = "Warriors (Al Kharid, in the palace courtyard)" },
  { text = "Guards (Varrock, near south and west entrances or palace courtyard)" },
  { text = "Rogues (Rogues' Castle, use teleport obelisks in the Wilderness)" },
  { text = "Cave goblins (Dorgesh-Kaan, roaming in the centre of the marketplace)" },
  { text = "Return to the Theives Guild and talk to Darren Lightfinger", title = "Finishing up" },
  { text = "Talk to Darren (3), then talk to Tobin to collect your loot. Repeat this 4 times." },
  { text = "Miniquest complete!" },
}

return Quest:new({
  name = "Lost Her Marbles (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1272412800,
  prereqQuests = { "From Tiny Acorns (miniquest)" },
})
