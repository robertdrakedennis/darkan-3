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
    text = "Talk to Surok Magis outside the Tunnel of Chaos, at the Saradomin statue south of Jolly Boar Inn, south of Fort Forinthry. If he's not there, click dig on the statue, then enter and leave.",
    title = "Tunnel chase",
  },
  { text = "After the cutscene, follow him into the tunnel." },
  { text = "Follow him into the portal in the western room (where Dakh'thoulan Aegis is)." },
  { text = "This version of the miniquest is still a WIP. It is recommended to use the wiki for this step." },
  {
    text = "Follow Surok through the Chaos Tunnels portals in the route in the above map.<ul><li>Be aware that there are aggressive monsters, dragons, and prayer-draining monsters on the way. It is recommended to bring food and combat equipment.</li><li>You may be randomly teleported by a portal - if so, locate yourself on the map and make your way back to the route.</li></ul>",
  },
  { text = "You need to see three cutscenes along the route." },
  { text = "Enter the final portal in the dagannoth room.", title = "Boss battle" },
  { text = "Defeat Bork." },
}

return Quest:new({
  name = "The Hunt for Surok (miniquest)",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1204588800,
  prereqQuests = { "What Lies Below" },
})
