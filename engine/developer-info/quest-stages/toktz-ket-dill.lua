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
    text = "After entering the TzHaar City, go east as far as possible along the southern wall, then directly north (just west of Fight Cauldron teleport). Speak to one of the three guards.",
    title = "The TzHaar tourist guide",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("What has happened here?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to TzHaar-Ket-Grol south-west, at the crack in the wall (with at least one empty inventory slot).",
    actions = { Action.ConversationHighlight:new("I was told to talk to you") },
  },
  { text = "Enter the tunnel to the library north-west of the Main Plaza." },
  {
    text = "Speak to TzHaar-Mej-Lor. .",
    actions = { Action.ConversationHighlight:new("I need help with the TzHaar language.") },
  },
  {
    text = "Talk to Reldo in the Varrock library about TzHaar language.",
    actions = { Action.ConversationHighlight:new("I have a question about the TzHaar language.") },
  },
  { text = "Search the second bookcase from the south on the west wall (with at least one empty inventory slot)." },
  { text = "Read the TzHaar Tourist Guide along with the note inside." },
  {
    text = "Return to TzHaar-Ket-Grol.",
    actions = {
      Action.ConversationHighlight:new("I know what to bring now."),
      Action.ConversationHighlight:new("A pickaxe."),
      Action.ConversationHighlight:new("Oak planks."),
      Action.ConversationHighlight:new("Blocks of stone."),
    },
  },
  {
    text = "Go east, talk to TzHaar-Ket-Rok for permission to enter.",
    actions = { Action.ConversationHighlight:new("Can I go in here?") },
  },
  {
    text = "Enter the mine directly east and speak with TzHaar-Hur-Brekt on the east side , then talk again.",
    title = "The TzHaar theatre",
    actions = {
      Action.ConversationHighlight:new("Can you teach me magma mining?"),
      Action.ConversationHighlight:new("But it's very important!"),
      Action.ConversationHighlight:new("We should change the plot."),
      Action.ConversationHighlight:new("The humans started it."),
    },
  },
  {
    text = "Speak to TzHaar-Hur-Klag on the west wall.",
    actions = {
      Action.ConversationHighlight:new("How can I make the story better?"),
      Action.ConversationHighlight:new("TzHaar who gets assaulted in its home is TzHaar-Hur."),
      Action.ConversationHighlight:new("That was it."),
    },
  },
  {
    text = "Speak to TzHaar-Mej-Kol, just north.",
    actions = {
      Action.ConversationHighlight:new("How can I make the story better?"),
      Action.ConversationHighlight:new("One is a TzHaar-Mej who is aware of the situation."),
      Action.ConversationHighlight:new("That was it."),
    },
  },
  {
    text = "Speak to TzHaar-Xil-Mor, south-east in the mine.",
    actions = {
      Action.ConversationHighlight:new("How can I make the story better?"),
      Action.ConversationHighlight:new("The criminal could be a TzHaar-Xil."),
    },
  },
  {
    text = "Speak to TzHaar-Hur-Brekt.",
    actions = {
      Action.ConversationHighlight:new("We should decide an ending."),
      Action.ConversationHighlight:new("The criminal's plans are foiled."),
    },
  },
  {
    text = "Speak to TzHaar-Xil-Mor.",
    actions = {
      Action.ConversationHighlight:new("How can I make the story better?"),
      Action.ConversationHighlight:new("The plans aren't always foiled."),
    },
  },
  {
    text = "Speak to TzHaar-Hur-Brekt.",
    actions = { Action.ConversationHighlight:new("They are all happy with the plot.") },
  },
  {
    text = "Speak with TzHaar-Ket-Jok, south-west in the mine.",
    title = "The TzHaar plot",
    actions = {
      Action.ConversationHighlight:new("You will play the role..."),
      Action.ConversationHighlight:new("...JalYt-Jenny"),
      Action.ConversationHighlight:new("What do you think of your current role?"),
      Action.ConversationHighlight:new("We should modify your current role."),
      Action.ConversationHighlight:new("No, we can't have that."),
      Action.ConversationHighlight:new("That was it."),
    },
  },
  {
    text = "Speak with TzHaar-Xil-Mor.",
    actions = {
      Action.ConversationHighlight:new("You will play the role..."),
      Action.ConversationHighlight:new("...TokTz-Ket-Ek-Mack."),
      Action.ConversationHighlight:new("What do you think of your current role?"),
      Action.ConversationHighlight:new("That was it."),
    },
  },
  {
    text = "Speak with TzHaar-Hur-Klag.",
    actions = {
      Action.ConversationHighlight:new("You will play the role..."),
      Action.ConversationHighlight:new("...TzHaar-Hur."),
      Action.ConversationHighlight:new("What do you think of your current role?"),
      Action.ConversationHighlight:new("We should modify your current role."),
      Action.ConversationHighlight:new("Eh, I guess so."),
      Action.ConversationHighlight:new("That was it."),
    },
  },
  {
    text = "Speak with TzHaar-Mej-Kol.",
    actions = {
      Action.ConversationHighlight:new("You will play the role..."),
      Action.ConversationHighlight:new("...KetKul-Schmul."),
      Action.ConversationHighlight:new("We should modify your current role."),
      Action.ConversationHighlight:new("That was it."),
    },
  },
  {
    text = "Finally speak to TzHaar-Hur-Brekt.",
    actions = { Action.ConversationHighlight:new("Yes, all roles have been given out.") },
  },
  {
    text = "Mine the Stone slab vein and craft 4 pillars for the first cave section (you can easily return for more).",
    title = "Obsidian pillars",
  },
  {
    text = "Go to the quest start location and have your planks treated by the small TzHaar-Hur-Frok.",
    title = "Treating the planks",
    actions = {
      Action.ConversationHighlight:new("Can you treat these oak planks for me?"),
      Action.ConversationHighlight:new("Here you go."),
    },
  },
  { text = "Enter the crack in the south wall by TzHaar-Ket-Grol.", title = "Repairing the tunnels" },
  --TODO: Add bulleted list
  {
    text = "The first cave requires 6 treated planks and 4 pillars. After fixing the cave-ins, mine the blocked tunnel and proceed to the next checkpoint.",
  },
  {
    text = "The second cave requires 6 treated planks and 6 pillars. The fire monster can only be harmed by magic with water spells or necromancy.",
  },
  {
    text = "The third cave requires 2 treated planks and 7 pillars. The first two lava monsters can only be harmed by ranged weapons with an attack range of 5 spaces or higher or by using necromancy. You will need to use magic with water spells or necromancy on the final fire monster. You cannot cross the stones before killing all three lava monsters.",
  },
  {
    text = "Once you are ready to fight the TokTz-Ket-Dill, enter the tunnel to the north.<ul><li>If you die, your gravestone will appear in the chamber with the trapped TzHaar-Hurs, outside the tunnel to the TokTz-Ket-Dill.</li></ul>",
    title = "Battling the TokTz-Ket-Dill",
    neededItems = { ["Adamant pickaxe"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Kill the TokTz-Ket-Dill by breaking it's armour with melee, then using any style to finish it off. Do not use prayers until the shell is broken.",
  },
  {
    text = "After killing the TokTz-Ket-Dill, exit to the north, head south out of the caves, and then talk to any of the three starting TzHaar to finish the quest.",
  },
}

return Quest:new({
  name = "TokTz-Ket-Dill",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1210636800,
  prereqQuests = {},
})
