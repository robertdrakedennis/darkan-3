local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Osman in Al Kharid palace without a follower or pet.",
    title = "Getting Started",
    neededItems = {},
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to Ozan just outside the palace, north of your current location." },
  { text = "After the cutscene, inspect the Het scales to receive the heavy weight." },
  { text = "Climb the rope." },
  { text = "Head north-west and 'Drop-off' the roof next to the rope." },
  { text = "Shimmy across the rope." },
  { text = "'Parrot drop' off the rug to the south." },
  { text = "Head south-east to the Shantay Pass." },
  { text = "Talk to Shantay.", title = "The Sundials", neededItems = {}, recommendedItems = {} },
  { text = "Go through the pass.", actions = { Action.ConversationHighlight:new("Let's keep going.") } },
  { text = "Do not interrupt the cutscene, or you will have to go back to the palace to retrieve Ozan" },
  { text = "Inspect the sundial (Het)." },
  {
    text = "Use the outer ring to align the sundial to the symbol of Het (human face) and continue dialogue with Ozan.",
  },
  {
    text = "Head south-west to the sundial (Apmeken). Inspect it.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "After the cutscene, kill the bandits and their king." },
  { text = "Pick up the sundial gnomon dropped by the last bandit killed." },
  { text = "Inspect the sundial to fix it." },
  { text = "Align the sundial to the symbol of Apmeken (monkey head) and continue dialogue with Ozan." },
  {
    text = "Head north-west for a cutscene.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "After the cutscene, keep entering any of the tunnels. Ozan will eventually mention he is sitting on something uncomfortable.",
    title = "The Kalphite Cavern",
    neededItems = {},
    recommendedItems = {},
  },
  { text = "Inspect the sundial (Crondis) and align it to Crondis (crocodile head)." },
  { text = "Enter the tunnel the sunbeam points to and walk through the tunnel at the end of the path." },
  {
    text = "Slice open the dung kalphites until the Kharid-ib is found.<ul><li>A bronze scimitar can be found on the ground nearby for players without a slashing weapon.</li><li>The sparkling kalphites that have the gems needed must be slashed open. They appear to have an extra glowing circle near their heads.</li><li>You must slice open any three kalphites before they will begin to sparkle.</li><li>Changing various game settings can help identify the sparkling ones.</li><li>Switch your graphics settings' 'Lighting Detail' option to 'Low'.</li><li>Switch your skybox setting 'Midday'.</li><li>Switch your audio settings' 'Ambient Sounds' option on. A twinkle sound indicates the sparkling one is nearby.</li><li>Switch your graphics settings' 'Lighting Detail' option to 'Low'.</li><li>Switch your skybox setting 'Midday'.</li><li>Switch your audio settings' 'Ambient Sounds' option on. A twinkle sound indicates the sparkling one is nearby.</li></ul>",
  },
  { text = "For the last kalphite, pick up the kharid-ib and a cutscene ensues." },
  { text = "Climb-out to exit via the rope that appears in the center." },
  { text = "Talk to Ozan or Leela." },
  { text = "Inspect the sundial (Scabaras) and align it to Scabaras (beetle)." },
  { text = "Head south-west and talk to Lady Keli.", title = "Lady Keli", neededItems = {}, recommendedItems = {} },
  {
    text = "Watch the cutscene.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Kill Apep and Heru." },
  { text = "Talk to Prince Ali Mirza." },
  { text = "Continue dialogue with Osman and Leela." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Diamond in the Rough",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1348617600,
  prereqQuests = { "Stolen Hearts" },
})
