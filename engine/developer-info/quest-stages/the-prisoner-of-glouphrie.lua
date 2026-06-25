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
    text = "Speak to Golrie in the Gnome Village Dungeon.  (Note: If you run into a bug where you can't click the X to close the letter without it moving your character, right-click on the X and click 'Close' or press 'Esc' on your keyboard to close the note.)",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("[Look at the letter]") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("What should we do next?") },
  },
  { text = "After asking what is next, you will be transported to the surface for a conversation with Golrana." },
  { text = "Talk with Golrana inside the Gnome Village." },
  {
    text = "Teleport to Castle Wars (or follow Elkoy out of the maze) and head to the highlighted area north of the Observatory and south of the Ourania altar (just north-east of the crystal tree). Make sure to pass the Observatory on its eastern side, alternatively use a 'Chipped' Watchtower teleport to teleport directly there.",
  },
  {
    text = "Make sure to dismiss your pet/follower. Talk to Golrana.",
    actions = { Action.ConversationHighlight:new("Yes, let's go.") },
  },
  { text = "Walk up the path north-west until you reach a large dead tree. Search it, then climb-through it." },
  {
    text = "Investigate the ledge on the south end of the chasm, continue the conversation with Golrana, she then will cross the chasm and throw a rope for you to cross.",
  },
  { text = "Move south and continue through the cave by jumping across gaps." },
  { text = "Travel north-west until reaching a water stream with stepping stones, but do not cross yet." },
  {
    text = "Before crossing the set of stepping stones, investigate the small crevice in the south wall to your immediate east.",
  },
  {
    text = "Golrana will enter the crevice and emerge on the other side, she then will move a stone for you to complete the jumping part.",
  },
  {
    text = "Cross the stepping stones, head southwest until you reach a crevice, then climb-through and out of the cave.",
  },
  { text = "Go west to the ledge overlooking Lletya and investigate the sturdy tree, then climb-down it." },
  {
    text = "After a cutscene, take the handwritten book on the crate next to you and read it.",
    title = "Bolrie's Lab",
  },
  {
    text = "Search all crates, lamps, shelves and the picture in the room. You will be notified when you have gathered everything in the room.",
  },
  { text = "Build the crate at the desk north of the ladder, then calibrate it." },
  { text = "Note down the number and colour value for all three." },
  {
    text = "Use the calculator on the wiki to get a working pair of shapes for each of the three value pairs. Note down the pair of shapes for each.",
  },
  {
    text = "Once you sing all 6 desired shapes, calibrate the device then insert the correct shapes into their corresponding slots.",
  },
  { text = "Click the green tick mark at the bottom to complete the puzzle." },
  {
    text = "Climb up the ladder and talk to Golrana underneath the steps just to the east of Lletya centre. (If you teleport to Lletya instead of leaving via the ladder, she will not appear. You can return to the lab from a trapdoor in the sheep pen in the northwest Lletya.)",
    title = "Arposandra",
    actions = { Action.ConversationHighlight:new("Yes, let's go.") },
  },
  { text = "Once back up on the ledge, walk past the large boulder to the east, and Golrana should spot an air vent." },
  { text = "Open the air vent on the rock wall just south of the boulder, then climb-down it." },
  { text = "After the cutscene Climb-up the stairs just next to you." },
  { text = "Head east to the prison and rotate the watcher once." },
  {
    text = "Enter the prison through the metal doors. Talk to the guard.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Pick-lock on the northwest cell door. Talk to Bolrie.",
    actions = {
      Action.ConversationHighlight:new("I fell down the shaft and we got separated."),
      Action.ConversationHighlight:new("Ilfeen."),
      Action.ConversationHighlight:new("Gena."),
      Action.ConversationHighlight:new("42."),
      Action.ConversationHighlight:new("The Grand Tree."),
    },
  },
  { text = "Lead Bolrie out of the jail and west to the corridor behind the stairs you just came up." },
  { text = "Continue through the door in the end and watch the cutscene." },
  { text = "After the cutscene, stand north of the big boulder and push it southward to block the air vent." },
  { text = "Talk to the gnomes for a cutscene." },
  { text = "Talk to them again to watch a second cutscene." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Prisoner of Glouphrie",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1297209600,
  prereqQuests = { "The Path of Glouphrie", "Roving Elves" },
})
