local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model = Types.Model
local Vertex = Types.Vertex

local launa = Model.new(3522, {
  [1] = Vertex.new(-92, 204, 312, 74, 61, 38),
})

local riftEntrance = Model.new(651, {
  [1] = Vertex.new(0, -496, 256, 36, 6, 3),
})

local steps = {
  {
    text = "Talk to Launa who is east of Varrock and south of the path leading to Silvarea.",
    title = "Getting started",
    actions = { Action.Direction:new(3308, 1613, 3453) },
    postconditions = { Condition.DistanceTo:new(3308, 1613, 3453, 10) },
  },
  {
    actions = { Action.ModelHighlight:new(launa) },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Enter the rift.",
    actions = { Action.ModelHighlight:new(riftEntrance) },
    postconditions = { Condition.ConversationText:new("Why should I, Tolna, be trapped in such a wretched place?") },
  },
  {
    text = "Near the entrance is a set of weapons. Only these weapons will defeat the monsters in this room. Different weapons kill different monsters. Kill monsters until the rage meter at the top of the screen is full. Certain equipment will not allow progress. To be safe, un-equip all gear except the anger weapons.<ul><li>Anger sword - kills Angry unicorn</li><li>Anger spear - kills Angry bear</li><li>Anger maul - kills Angry giant rat</li><li>Anger battleaxe - kills Angry goblin</li></ul>",
    title = "Room of Rage",
  },
  { text = "Watch the short cutscene." },
  { text = "Enter the exit to the east to proceed." },
  {
    text = "'Look-inside' a dark hole, if the fear reaper does not show up right away move onto the next hole that has the 'look-inside' option. Move around the room in a clock-wise rotation until the fear reaper has been killed 5 times.",
    title = "Room of Fear",
  },
  { text = "Continue the process until you receive a cutscene." },
  { text = "After the cutscene, Enter the black hole on the west wall to proceed." },
  {
    text = "Start attacking a confusion beast, if you do not deal any damage immediately move onto another beast until you find the one that takes damage.<ul><li>Any confusion beast that you hit will continue to attack you, if you are low combat it is advised to continue hitting a beast until it goes away.</li></ul>",
    title = "Room of Confusion",
  },
  { text = "Find and kill the confusion beast 5 times in total in order to proceed." },
  { text = "After all beasts have been killed enter the remaining confusing door for a cutscene." },
  {
    text = "Kill each of the hopeless creatures 3 times.<ul><li>Eating food will cause the hopeless creature to restore 75% of their life points.</li></ul>",
    title = "Room of Hopelessness",
  },
  { text = "Cross the bridge and 'Use' the exit to proceed." },
  { text = "Kill all three of Tolna's heads.", title = "Tolna", neededItems = {}, recommendedItems = {} },
  { text = "Talk to Tolna.<ul><li>If he doesn't spawn, exit the room and re-enter.</li></ul>" },
  { text = "On the surface, talk to Tolna again." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "A Soul's Bane",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.shortmedium,
  releaseDate = 1144022400,
  prereqQuests = {},
})
