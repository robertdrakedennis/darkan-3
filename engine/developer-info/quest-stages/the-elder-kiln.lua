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
    text = "Talk to TzHaar-Mej-Jeh at the Birthing Pool in TzHaar City.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Keep the gauge within the two arrows using fire spells to heat it and water spells to cool it until the egg hatches.<ul><li>The two TzHaar will also fire occasionally. Keep an eye on them as both may shoot fire spells at the egg simultaneously which could result in overheating.</li></ul>",
    title = "Hatching",
  },
  {
    text = "Talk to TzHaar-Mej-Jeh.",
    actions = {
      Action.ConversationHighlight:new("What's a Ga'al?"),
      Action.ConversationHighlight:new("Why do you do this?"),
      Action.ConversationHighlight:new("Can't you teach them your ways?"),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  {
    text = "Go west to the main plaza and talk to TzHaar-Mej-Ak.",
    title = "In the pits",
    actions = {
      Action.ConversationHighlight:new("I've come for that Ga'al."),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "Kill all enemies in the Fight Pits whilst protecting Ga'al-Jeh." },
  { text = "After the cutscene, kill TzHaar-Ket-Yit'tal." },
  {
    text = "Talk to one of the TzHaar-Mejs.",
    title = "Path to the kiln",
    actions = {
      Action.ConversationHighlight:new("It's the remains of your dead that you use as currency."),
      Action.ConversationHighlight:new("Of course I'll help the TzHaar."),
      Action.ConversationHighlight:new("Okay."),
      Action.ConversationHighlight:new("I had better be on my way."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Head east of the plaza to the kiln." },
  {
    text = "Enter the cave.",
    actions = { Action.ConversationHighlight:new("Yes, follow me.") },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  {
    text = "Jump the chasm  and kill all of the monsters.<ul><li>Do not use protection prayers and disable your auras until the TokTz-Ket-Dill's shell is broken as this will increase the frequency of making the rocks fall on you, dealing over 1,000 damage.</li><li>You need a pickaxe to break the shell of TokTz-Ket-Dill, which is weak against water spell after its shell is broken.</li></ul>",
    title = "Room one",
    actions = { Action.ConversationHighlight:new("Come on, Xox!") },
    postconditions = { Condition.ConversationText:new(" Ground stop here.") },
  },
  {
    text = "Investigate the skeleton at the end of the cavern  to receive Journal of Perjour.",
    actions = { Action.ConversationHighlight:new("Reach in and take the book.") },
    postconditions = {
      Condition.ConversationText:new(
        "This appears to be an old journal. Despite its obvious age, it is in very good condition - an old magic must be protecting it from the heat within the tunnels."
      ),
    },
  },
  { text = "Proceed to the next room." },
  { text = "Take the TokKul on the ground and kill all of the monsters.", title = "Room two" },
  { text = "Proceed to the next room." },
  {
    text = "Kill all of the monsters and examine the note in the Journal of Perjour (the book which should be in your backpack).",
    title = "Room three",
    actions = { Action.ConversationHighlight:new("Read the note.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "For the left scale, use the picture to find its resulting sum and put that much tokkul on it.  Repeat for the right scale. A Tzhaar number symbol can be higher than 15 that is shown in the picture of this guide on the right.",
  },
  { text = "Proceed to the next room." },
  {
    text = "Go through the room, jumping over chasms and killing the TokTz-Ket-Dill along the way.",
    title = "Room four",
  },
  { text = "Proceed to the next room." },
  { text = "Kill the monsters in this room.", title = "Room five" },
  {
    text = "Proceed to the next room.",
    actions = { Action.ConversationHighlight:new("I'm ready.") },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "Complete all seven waves of the Kiln.", title = "The elder kiln" },
  {
    text = "Talk to TokHaar-Hok.",
    actions = {
      Action.ConversationHighlight:new("Let's see what rock you're forged from, then."),
      Action.ConversationHighlight:new("I need your help."),
      Action.ConversationHighlight:new("I think he should, so the Ga'al can live good lives."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  {
    text = "Proceed back through the caves, ignoring monsters along the way.<ul><li>Teleporting away and attempting to avoid heading back through the caves will result in the player being forced to return where Ga'al Xox was left at the kiln, then head back through the caves as originally intended.</li></ul>",
    title = "Finishing up",
  },
  {
    text = "Return to the centre of the Main Plaza and talk to one of TzHaar-Mejs.",
    actions = {
      Action.ConversationHighlight:new("Even after all we've done, you can't trust the Ga'al?"),
      Action.ConversationHighlight:new("[Let Ga'al-Xox fight]"),
      Action.ConversationHighlight:new("I'm only interested in the power of that Kiln."),
    },
    postconditions = { Condition.ConversationText:new(" You have been a good friend and teacher. Goodbye, Player.") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Elder Kiln",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.long,
  releaseDate = 1329177600,
  prereqQuests = {},
})
