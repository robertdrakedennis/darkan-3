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
    text = "Talk to the 'Calm' Archaeologist at the rex skeleton in Anachronia base camp to begin the quest.",
    title = "Learning about Osseous",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Head to the teleportation device in the spirit grove." },
  { text = "Activate the teleportation device." },
  { text = "Head east and talk to the 'Calm' Archaeologist." },
  {
    text = "Go north-east towards the Matriarch lair entrance, and then through the archway to the north and follow the path anti-clockwise until you reach an Ancient Artefact (Inactive).",
    title = "Finding a teleportation crystal",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Activate the Ancient Artefact (Inactive) to unlock the first gate." },
  { text = "Go back to the area near the archaeologist and Pass Through the gate." },
  { text = "Go south, then east, then north through the arch, and activate the second Ancient Artefact (Inactive)." },
  { text = "A cutscene ensues, unlocking two more gates." },
  { text = "Go south, looping around to the immediate west and Pass Through the gate." },
  { text = "Go east, following the path south and around anti-clockwise up a hill." },
  { text = "Pass Through the gate." },
  { text = "Uncover the soil." },
  { text = "Excavate the crystallised rubble for a teleportation crystal (damaged)." },
  {
    text = "Restore the teleportation crystal (damaged) at an archaeologist's workbench.",
    title = "Finishing up",
    neededItems = { ["Teleportation crystal (damaged)"] = { quantity = 1 }, ["Orthenglass"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Talk to the 'Calm' Archaeologist in the Rex Matriarch lair with the restored teleportation crystal.<ul><li>He will no longer be found in in the Anachronia base camp, you have to travel to the Rex Matriarch lair to speak with him.</li></ul>",
  },
  { text = "Watch the cutscene." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Osseous Rex",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.short,
  releaseDate = 1716854400,
  prereqQuests = { "Anachronia base camp tutorial" },
})
