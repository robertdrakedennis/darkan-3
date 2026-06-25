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
    text = "Speak to the Curator just north of Burthorpe lodestone.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about 'Desperate Creatures'.") },
  },
  {
    text = "[Accept Quest]<ul><li>Slayer cape teleport to Turael/Spria is a good option as you'll need to teleport a few times.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("What do I need to do first?") },
  },
  { text = "Teleport to Anachronia and run west.", title = "Task 1" },
  { text = "Use the Tome of tracking to find all four flowers." },
  {
    text = "After the dialogue between Hannibus and Vindicta teleport back to Burthorpe and speak to the Curator.",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Desperate Creatures'."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Talk to the Curator again for the next task.",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Desperate Creatures'."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  { text = "Teleport to Anachronia.", title = "Task 2" },
  { text = "Find the 4 shrubberies around the camp using the tome." },
  {
    text = "Finish the dialogue with Mr Mordaut and teleport back to Burthorpe and speak to the Curator.",
    actions = {
      Action.ConversationHighlight:new("Talk about Desperate Creatures."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Talk to the Curator again for the next task.",
    actions = {
      Action.ConversationHighlight:new("Talk about Desperate Creatures."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Teleport to Anachronia and navigate to the Anachronia Swamp Fortress.<ul><li>Teleport to the Xolo dig site using the Orthen teleportation device or teleport to Laniakea via Slayer cape.</li></ul>",
    title = "Task 3",
  },
  {
    text = "Alternatively exit Anachronia base camp to the north down the stairs, not via the Agility shortcut.<ul><li>Run east to the liverworts.</li><li>Run south past the lampenfloras while staying as far west as possible.</li></ul>",
  },
  { text = "Find all four mushrooms." },
  {
    text = "Finish the dialogue with Zaros and teleport back to Burthorpe and speak to the Curator.",
    actions = {
      Action.ConversationHighlight:new("Talk about Desperate Creatures."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Talk to the Curator again for the next task.",
    actions = {
      Action.ConversationHighlight:new("Talk about Desperate Creatures."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Teleport to Anachronia and navigate to an area south of the liverworts and north of the lampenfloras.<ul><li>The fastest way to get there is by teleporting to Anachronia and running slightly east to use the Orthen teleportation device. Then teleporting to the Moksha ritual site M and then exiting and running north-east.</li><li>Alternatively, leave Anachronia base camp to the north down the stairs, not via the Agility shortcut. Run east to the liverworts. Run south towards the lampenfloras.</li></ul>",
    title = "Task 4",
  },
  { text = "Find all four insects." },
  {
    text = "Finish the dialogue with Charos and Thok and teleport back to Burthorpe and speak to the Curator.",
    actions = {
      Action.ConversationHighlight:new("Talk about Desperate Creatures."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Talk to the Curator again for the next task.",
    actions = {
      Action.ConversationHighlight:new("Talk about Desperate Creatures."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Teleport to Anachronia and run north.<ul><li>Fastest way to get there is by using the War's Retreat boss portal / the Max Guild boss portal or the Grouping System teleporting to Rex Matriarchs and heading west to the Teleportation Device and activating it, taking you where you need to be.</li><li>Alternatively, leave Anachronia base camp through the southern exit and head north-east to central Anachronia</li></ul>",
    title = "Task 5",
  },
  { text = "Find all four frogs." },
  { text = "Finish the dialogue with Prehistoric Potterington." },
  {
    text = "Teleport back to Burthorpe and speak to the Curator.",
    actions = {
      Action.ConversationHighlight:new("Talk about Desperate Creatures."),
      Action.ConversationHighlight:new("Talk about your progress."),
    },
  },
  {
    text = "Speak to Seren on the 1st floor[UK]2nd floor[US] of the Burthorpe Castle.",
    title = "Finishing up",
    actions = { Action.ConversationHighlight:new("Talk about 'Desperate Creatures'.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Go downstairs, head south and speak to the Curator.",
    actions = { Action.ConversationHighlight:new("Talk about 'Desperate Creatures'.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Desperate Creatures",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1600646400,
  prereqQuests = { "Desperate Measures" },
})
