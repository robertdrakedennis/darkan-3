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
    text = "Talk to the Gossip south of the Sinclair Mansion (Fairy ring CJR).",
    title = "Shady disappearances",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to the Guard inside the gates." },
  {
    text = "From the outside, break the eastern smashed window of the mansion (right click to choose break).<ul><li>You might have to try again because of the dog</li></ul>",
    title = "Déjà vu",
  },
  { text = "Once inside, go to the large room and take the scrap paper next to the fireplace." },
  { text = "Go up to the 1st floor[UK]2nd floor[US]." },
  { text = "Take the address form from the table in the central northern room and read it." },
  { text = "Search the nearby bookcase for a black knight helmet." },
  { text = "Go back down the staircase and 'break' the smashed window again to climb out." },
  {
    text = "Talk to the Guard three times.  (you can click away after giving the items to skip the first 2 interactions)",
    actions = {
      Action.ConversationHighlight:new("I have proof that the Sinclairs have left."),
      Action.ConversationHighlight:new("I have proof that links the Sinclairs to Camelot."),
      Action.ConversationHighlight:new("I have proof of foul play."),
    },
  },
  {
    text = "Talk to Gossip.",
    title = "The Sinclairs",
    actions = { Action.ConversationHighlight:new("Tell me about Anna Sinclair.") },
    postconditions = {
      Condition.ConversationText:new(
        " Anna? The guards are keeping her in the jail in the courthouse in Seers' Village. I heard she's been acting like a royal pain."
      ),
    },
  },
  { text = "Go to the Seers' Village Courthouse, directly south from the mansion gates." },
  {
    text = "Talk to Anna.",
    actions = { Action.ConversationHighlight:new("Okay, I guess I don't have much of a choice.") },
    postconditions = {
      Condition.ConversationText:new(
        " Try speaking to the servants at the Mansion. They'll probably be able to give you useful testimony."
      ),
    },
  },
  {
    text = "Dismiss any followers you might have and go down the stairs to begin the trial.",
    title = "Courtroom drama",
    actions = { Action.ConversationHighlight:new("Yes, I'm ready.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Call the dog handler as a witness , talk to Pierre.<ul><li>You may need to resize your interface and zoom out before descending the stairs since he appears near the bottom of the screen.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Dog handler"),
      Action.ConversationHighlight:new("Ask about the thread"),
      Action.ConversationHighlight:new("Ask about the poison"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " This salesman came around selling this poison that could fix anything. I think most everyone bought some to use for chores around the mansion."
      ),
    },
  },
  {
    text = "For the following options, talk to the judge to call a witness, then choose who you would like to call:<ul><li>Call the butler , talk to Hobbes.</li><li>Call the maid , talk to Mary.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Butler"),
      Action.ConversationHighlight:new("Ask about the dagger"),
      Action.ConversationHighlight:new("Next page"),
      Action.ConversationHighlight:new("Maid"),
      Action.ConversationHighlight:new("Ask about the night of the murder"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Not all that much. I worked in the garden 'til late, and I was going inside for dinner when all the commotion started."
      ),
    },
  },
  { text = "After the balloons drop, exit via the gate." },
  { text = "Talk to Anna in her cell." },
  {
    text = "Go to the northeast of Camelot Castle, outside of the castle hedges",
    title = "Jailbreaker",
    neededItems = {
      ["Air rune"] = { quantity = 1 },
      ["Law rune"] = { quantity = 1 },
      ["Telekinetic Grab"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Search the statue." },
  {
    text = "Talk to Merlin.",
    actions = {
      Action.ConversationHighlight:new("What do we do now?"),
      Action.ConversationHighlight:new("Reclaim Camelot."),
      Action.ConversationHighlight:new("Retrieve the Holy Grail."),
      Action.ConversationHighlight:new("Save King Arthur."),
    },
  },
  {
    text = "Reach the vent on the northern wall, it should trigger a cutscene in which the imprisoned knights lift Merlin out. Do not reach for the vent again afterwards.<ul><li>If you don't see a cutscene, then talk to Merlin again and exhaust all options.</li></ul>",
  },
  {
    text = "Cast Telekinetic Grab on the guard fixing his hair outside your cell door. Ask the knights around if you didn't bring any Telekinetic Grab runes.<ul><li>Drop trick will work here if you would like an extra hairclip.</li></ul>",
  },
  { text = "Use the hairclip on the metal door." },
  {
    text = "Complete the puzzle.<ul><li>To complete this puzzle, you must set each of four tumblers to the correct height. The most foolproof way to do this (although not necessarily the quickest) is to set all tumblers to the lowest height and try the lock.</li><li>If a tumbler shows a green circle, do not change that tumbler anymore.</li><li>Ignore blue and red circles. For all the other tumblers, increase the height by one and try again.</li><li>Continue until the lock unlocks. This method will take at most six attempts.</li></ul>",
  },
  {
    text = "Once you have bypassed the gate, climb up two sets of staircases to reach the top floor.",
    title = "The holy grail",
  },
  { text = "Search the table and click the purple cylinder second from the right." },
  {
    text = "Go to East Ardougne, northeast corner, and talk to Wizard Cromperty.",
    title = "For Camelot!",
    neededItems = {
      ["Granite"] = { quantity = 1 },
      ["Bronze med helm"] = { quantity = 1 },
      ["Iron chainbody"] = { quantity = 1 },
      ["Holy grail (item)"] = { quantity = 1 },
      ["Animate rock scroll"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Talk about King's Ransom") },
  },
  {
    text = "Equip your bronze med helm and iron chainbody, then enter the Black Knights' Fortress from Edgeville.<ul><li>If you have the Skull of Remembrance, you can teleport to the 3rd floor[UK]4th floor[US] of the fortress and head down to the ground floor[UK]1st floor[US].</li></ul>",
  },
  { text = "Climb-down the ladder in the south-west corner." },
  { text = "Remove your bronze helm and iron chainbody, then free King Arthur." },
  { text = "Talk to King Arthur. Give him the bronze helm and iron chainbody." },
  { text = "Return to Camelot and talk to King Arthur." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "King's Ransom",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1185235200,
  prereqQuests = { "Holy Grail", "Murder Mystery", "One Small Favour" },
})
