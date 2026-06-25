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
    text = "Talk to Sylas south of Taverley (north of the Witch's house).",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head up the east side of White Wolf Mountain near the Burthorpe mine entrance and talk to Grimgnash.",
    title = "Grimgnash",
    actions = {
      Action.ConversationHighlight:new("I heard you were a great and mighty Griffin!"),
      Action.ConversationHighlight:new("There once was graveyard filled with undead."),
      Action.ConversationHighlight:new("There lived a skeleton named Skullrot."),
      Action.ConversationHighlight:new("Skullrot was insane!"),
      Action.ConversationHighlight:new("Skullrot hungrily grabbed the gnome's hair."),
      Action.ConversationHighlight:new("Started to strangle the poor gnome."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Once he is sleeping, take a Feather from next to the nest." },
  { text = "Go back to Sylas and talk to him." },
  { text = "Head to the tower directly south-east of Goblin Village.", title = "Rupert" },
  { text = "Go around the south-east side, climb over the crumbling wall, and talk into the drainpipe." },
  {
    text = "Talk into the drainpipe again.",
    actions = {
      Action.ConversationHighlight:new("I could try and climb up"),
      Action.ConversationHighlight:new("Is there anything up there that can help?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Not really, old spoon. There's not much up here except for me and my beard. Ha ha!"
      ),
    },
  },
  { text = "Climb up Rupert's beard." },
  { text = "Talk to Rupert." },
  { text = "Right-click climb down Rupert." },
  {
    text = "Talk to Miazrqa, located over the crumbling wall and to the north of the tower.",
    actions = {
      Action.ConversationHighlight:new("Your second-cousin, twice removed?"),
      Action.ConversationHighlight:new("I need a key for the house."),
      Action.ConversationHighlight:new("I should be off, I think."),
    },
    postconditions = { Condition.ConversationText:new(" Very well. Good luck with finding my pendant.") },
  },
  {
    text = "Head to the Witch's house south of Taverley, the building next to Sylas.",
    title = "The pendant",
    neededItems = { ["Tarromin potion (unfinished)"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Open the main door.<ul><li>Look under the Potted plant to the left of the main door of the witch's house to obtain a key if not given by the princess.</li></ul>",
  },
  {
    text = "Climb down stairs (cellar bulkhead) and search the crate near the piano for leather gloves. You may need to search multiple times.",
  },
  { text = "Whilst wearing the leather gloves, open the gate and search the Music stand." },
  {
    text = "Play the piano in the first room (refer to the music sheet pictured right).<ul><li>The notes 'EFEDC' are played on the right hand (upper) side and the notes 'AEGA' are played on the left hand (lower) side.</li></ul>",
  },
  { text = "Right-click Search the now-opened piano." },
  {
    text = "Make two shrink-me-quick potions.<ul><li>Combine vial of water and clean tarromin.</li><li>Add shrunk ogleroot.</li><li>Repeat.</li></ul>",
  },
  { text = "Go upstairs and head into the small room leading to the garden." },
  { text = "Drink one of the potions next to the mouse hole and glyph." },
  {
    text = "While in the mouse hole, follow these directions:<ul><li>Run north past the first mouse, find the set of the nails on the eastern wall and climb up them.</li><li>Go south-west, and climb up the nails on the northern wall.</li><li>Climb up the nails on the south wall in the room.</li><li>Run to the north-east corner, and climb down the nails on the eastern wall.</li><li>Run north, and climb up the nails on the eastern wall.</li><li>Pickup the pendant at the end of the next hallway.</li></ul>",
  },
  {
    text = "After taking Miazrqa's pendant, leave the mouse hole either by teleporting to Taverley, or Home teleport to Falador, or going back through the entrance.",
  },
  { text = "Talk to Miazrqa." },
  { text = "Talk to Rupert to get Rupert's helmet." },
  {
    text = "If you need another shrunken oglreroot, you can kill an Experiment No. 2 down the manhole near the entrance to the witch's house, or pick one up from the pile in the room with the experiments.",
  },
  { text = "Talk to Sylas.", title = "The beanstalk" },
  { text = "Plant the magic beans in the Farming patch next to Sylas and water it using a watering can." },
  { text = "Climb the beanstalk and kill Glod." },
  { text = "Take the golden goblin, climb down the stalk on southern sides and give it to Sylas." },
  { text = "Right-click Use the other shrinking potion on the beanstalk.<ul><li>Careful not to drink it!</li></ul>" },
  { text = "Chop the beanstalk." },
  { text = "Talk to Sylas." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Grim Tales",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1180915200,
  prereqQuests = { "Witch's House" },
})
