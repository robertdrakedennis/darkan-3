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
    text = "Run north of the Golden Apple Tree south-east of Rellekka (fairy ring code AJR).",
    title = "Starting out",
  },
  { text = "Climb the cliffside to the north of the musician with a rope in your backpack." },
  {
    text = "Talk to Hamal the Chieftain in the nearby village. Ask him why everyone is so hostile and continue through all the dialogue; he will then offer the quest.  (If The Fremennik Trials is completed: )",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Why is everyone so hostile?"),
      Action.ConversationHighlight:new("So what are you doing up here?"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Why is everyone so hostile?"),
      Action.ConversationHighlight:new("So what are you doing up here?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Walk south and Dig-below the Roots for a lump of Mud.", title = "Searching for the daughter" },
  { text = "If you do not have a pole and a plank, go north of Hamal's house and pick up one of each." },
  { text = "Run north to the lake, then west along the edge." },
  { text = "Use the mud on the tall tree, climb it." },
  { text = "Use your staff/pole on the clump of rocks." },
  { text = "Use your plank on the flat stone." },
  {
    text = "Stand next to the pile of rocks and listen to one of the shining pools.",
    actions = {
      Action.ConversationHighlight:new("Hello! Who are you!"),
      Action.ConversationHighlight:new("So what exactly do you want from me?"),
      Action.ConversationHighlight:new("That sounds like something I can do."),
      Action.ConversationHighlight:new("I'll get right on it."),
    },
  },
  { text = "Use your plank on the flat stone again.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Talk to Hamal the Chieftain.",
    title = "Peace and food",
    actions = { Action.ConversationHighlight:new("About the people of Rellekka...") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Talk to Jokul in the goat farm to the north." },
  {
    text = "Talk to Svidi wandering near the Fremennik lodestone.",
    actions = { Action.ConversationHighlight:new("Can't I persuade you to go in there somehow?") },
    postconditions = { Condition.ConversationText:new(" I won't go in there unless I know it's safe.") },
  },
  {
    text = "Go to the longhall in Rellekka, talk to Brundt the Chieftain about the Mountain Camp.  (If Lunar Diplomacy has been started or completed: )",
    actions = {
      Action.ConversationHighlight:new("Ask about the Mountain Camp."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Ask about the Mountain Camp."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Mine the ancient rock in the mountain camp's southern tent." },
  {
    text = "Return to Brundt the Chieftain in the longhall.  (If Lunar Diplomacy has been started or completed: )",
    actions = {
      Action.ConversationHighlight:new("Ask about the Mountain Camp."),
      Action.ConversationHighlight:new("Ask about the Mountain Camp."),
    },
    postconditions = { Condition.ConversationText:new(" Really? Let me see!") },
  },
  { text = "Give the safety guarantee to Svidi." },
  {
    text = "Make your way to White Wolf Mountain<ul><li>If you have completed The Grand Tree, teleport to Al Kharid lodestone, run north-west and click on 'Glider Captain Dalbur'</li></ul>",
    actions = { Action.ConversationHighlight:new("Sindarpos.") },
  },
  { text = "Wearing leather gloves (or gauntlets), pick from the bush next to the gnome glider." },
  { text = "Eat the fruit." },
  {
    text = "Return to Hamal the Chieftain in the Mountain Camp.",
    actions = {
      Action.ConversationHighlight:new("About your food supplies..."),
      Action.ConversationHighlight:new("About the people of Rellekka..."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Go to the centre of the lake and stand next to the pile of rocks.", title = "The Kendal" },
  { text = "Listen-to the shining pool again." },
  { text = "Jump-across the flat stone, falling into the water, and run east along the northern shore." },
  { text = "Chop down some of the small trees." },
  { text = "Enter the cave." },
  {
    text = "If you've not fully completed both tasks in the section above and gone through the dialogue completely, you'll receive a message referring to a stench that prevents you from entering the cave. Repeat the tasks until you're allowed to enter the cave.",
  },
  {
    text = "Talk to the Kendal.",
    actions = {
      Action.ConversationHighlight:new("It's just me, no one special."),
      Action.ConversationHighlight:new("You mean sacrifice?"),
      Action.ConversationHighlight:new("You look like a man in a bearsuit!"),
      Action.ConversationHighlight:new("Can I see that corpse?"),
      Action.ConversationHighlight:new("Hand the body over to me!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " You demand nothing! You may have uncovered my disguise, but my claws can still tear you apart at will! I will not allow you to leave!"
      ),
    },
  },
  { text = "Kill the Kendal." },
  { text = "Take the corpse of woman next to all the skeletons." },
  {
    text = "Return to Hamal the Chieftain.",
    title = "The burial",
    actions = {
      Action.ConversationHighlight:new("But he killed your daughter!"),
      Action.ConversationHighlight:new("I will."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  {
    text = "Take 5 muddy rocks from the camp. They can be found around the mud pond and in the goat pen north of the tent.",
  },
  {
    text = "Talk to Ragnar by the lake for Asleif's necklace.",
    actions = { Action.ConversationHighlight:new("Thank you. I will make sure she's given a proper burial now.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Bury the corpse in the centre of the lake." },
  { text = "Use the rocks on the burial mound." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Mountain Daughter",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1110153600,
  prereqQuests = {},
})
