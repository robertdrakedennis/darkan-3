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
    title = "Earthquake aftermath",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Continue...") },
  },
  {
    text = "Clear the fallen masonry at points along the agility course. If you have already cleared it, this will be mentioned in the chat and you can start closing the rifts.<ul><li>At the start of the course.</li><li>At the bottom of the rope ladder next to the Statue of Het.</li><li>On a walkway on the eastern side of the course.</li><li>At the end of a walkway further along the southern-most part of the course to the west.</li></ul>",
    title = "Clearing the rubble",
  },
  {
    text = "Finish the course or teleport back to the Het's Oasis. A ring of duelling is the fastest teleport method back.",
  },
  { text = "Talk to Icthlarin." },
  {
    text = "Close the rifts around Het's Oasis. They must be cleared in this particular order. (When closing a rift, it will direct you to the next one. Alternatively, refer to the map here.)<ul><li>Next to Icthlarin.</li><li>North-eastern corner of the Het's Oasis.</li><li>South-eastern corner of the Het's Oasis.</li><li>Behind the statue of Het in the middle of Het's Oasis. (Can do the agility course to get there.)</li></ul>",
    title = "Closing the rifts",
  },
  { text = "Finish the agility course or teleport back to the Het's Oasis with a ring of duelling." },
  {
    text = "Talk to Icthlarin again",
    actions = { Action.ConversationHighlight:new("Yes!") },
    postconditions = { Condition.ConversationText:new("Icthlarin begins to diffuse the defences.") },
  },
  { text = "Stabilise the three unstable rifts that spawn." },
  { text = "Talk to Icthlarin after stabilising the rifts.", title = "Stabilise the rifts" },
  {
    text = "Pry the hidden door at the back of the statue of Het.<ul><li>Right-clicking the door brings up two options. Ensure you click just below the statue.</li><li>Choose 'Pry open hidden door'.</li></ul>",
  },
  {
    text = "If you have completed Our Man in the North and have level 80 Strength, the Eye of Het can be found in the statue's left hand inside the tomb. Take the eye.<ul><li>If you don't meet these requirements, the relic will simply be placed into your inventory upon attempting to enter.</li></ul>",
  },
  {
    text = "Talk to Icthlarin at the entrance of Het's Oasis. Talking to Icthlarin at Het's statue does not work.<ul><li>Note: If you encounter a bug where Icthlarin says you still need to find the relic after you have obtained it, either teleport out or log out and back in and talk to Icthlarin near the bonfire.</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Eye of Het I",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1641254400,
  prereqQuests = { "City of Senntisten" },
})
