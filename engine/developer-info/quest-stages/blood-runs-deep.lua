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
    text = "Talk to Baba Yaga on Lunar Isle about the man in the bed.",
    title = "The sick man's dream world",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about the man in the bed.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("Is there anything we can do?"),
      Action.ConversationHighlight:new("Yes?"),
      Action.ConversationHighlight:new("An excellent idea!"),
    },
  },
  {
    text = "Talk to Baba Yaga again to go to the Dream World.",
    title = "The Dagannoth attack",
    actions = {
      Action.ConversationHighlight:new("Talk about the man in the bed."),
      Action.ConversationHighlight:new("I am ready."),
      Action.ConversationHighlight:new("I've no idea."),
      Action.ConversationHighlight:new("I think I've got it."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " It's a lighthouse. This is his home, but he's no sea-captain; he's the lighthouse keeper."
      ),
    },
  },
  {
    text = "Listen Silas.",
    actions = { Action.ConversationHighlight:new("He's crazy.") },
    postconditions = {
      Condition.ConversationText:new(
        " Having come into his dream world to help him with his real-world catatonia, I would be quite disappointed if he weren't."
      ),
    },
  },
  { text = "Search the bookcase to obtain a prophecy tablet." },
  { text = "Climb the stairs." },
  {
    text = "Eavesdrop-on Silas and continue through the tunnel.",
    actions = { Action.ConversationHighlight:new("She didn't count on us finding out.") },
    postconditions = {
      Condition.ConversationText:new(
        " Once my beautiful children have developed the new immunities I have mastered, the world will be as a toy to us."
      ),
    },
  },
  {
    text = "Talk to Silas.",
    actions = { Action.ConversationHighlight:new("I'm a friend of your nephew.") },
    postconditions = {
      Condition.ConversationText:new(
        " We're here to help you away from here. Hurry, [Fremennik name], try the lectern."
      ),
    },
  },
  {
    text = "Read Our Lives.<ul><li>Prepare to face multiple (4-7) Dagannoth and Wallasalki at a time. This will go on for a few minutes.</li><li>Do not use your dwarf multicannon in this area or you will lose it.</li></ul>",
    actions = { Action.ConversationHighlight:new("What do you mean, 'not working'?") },
    postconditions = { Condition.ConversationText:new(" I don't know, I'll have to study it.") },
  },
  {
    text = "Talk to Baba Yaga to be teleported to Rellekka.<ul><li>Prepare to face many Dagannoth.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about the man in the bed."),
      Action.ConversationHighlight:new("What is?"),
      Action.ConversationHighlight:new("I'm ready. What's your trick?"),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "After killing sufficient Dagannoth, Brundt the Chieftain will command you to get onto his boat." },
  {
    text = "Talk to Queen Sigrid.",
    actions = {
      Action.ConversationHighlight:new("What is the matter, Your Majesty?"),
      Action.ConversationHighlight:new("I'll set out right away."),
    },
    postconditions = { Condition.ConversationText:new(" Good show, [Fremennik name]. We can hold the fort here.") },
  },
  {
    text = "Talk to Brundt to get another seal of passage.",
    actions = { Action.ConversationHighlight:new("I'll go with you.") },
    postconditions = { Condition.ConversationText:new(" Of course.") },
  },
  {
    text = "Bank before teleporting to Waterbirth. Bring prayer potions and a full load of food. Leave 1 space empty.",
    title = "Saving King Vargas",
  },
  {
    text = "Talk to Jarvald (on the western pier) and select the option Travel-Waterbirth to head to Waterbirth Island.",
  },
  {
    text = "Talk to Bardur near the cave entrance for a rope.",
    actions = {
      Action.ConversationHighlight:new("What kind of voices?"),
      Action.ConversationHighlight:new("Could it be an old man echoing from underground?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Now that you mention it, I did see a hole that I hadn't noticed before, way round to the south. I've got spare rope if you want to go down there."
      ),
    },
  },
  { text = "Make your way to the southwestern tip of the island and tie the rope on the hole." },
  { text = "Climb-down the hole." },
  {
    text = "Talk to King Vargas.",
    actions = {
      Action.ConversationHighlight:new("We aren't out yet."),
      Action.ConversationHighlight:new("Come on, this isn't the King Vargas I'm used to."),
      Action.ConversationHighlight:new("No, I can imagine."),
      Action.ConversationHighlight:new("Certainly, I've got some here."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " That's excellent but, on second thought, perhaps we should save it for the time-being. If I get injured, I'd really appreciate it if you could hand me some."
      ),
    },
  },
  {
    text = "Lead King Vargas down the path to the north-east and go down the ladder.<ul><li>Don't get too far ahead of King Vargas in the tunnels, or the dagannoth will get him and return him to the first chamber meaning you will have to start over. Staying close to King Vargas also ensures he will not be attacked so you won't have to feed him any food. It's recommended to turn off run while escorting King Vargas.</li></ul>",
  },
  { text = "Continue to the far-east side of the next tunnel, past the small obelisk, and climb up the ladder." },
  {
    text = "The next ladder to climb up is to the west, in this short tunnel dagannoth will spawn that prioritise attacking King Vargas, make sure you attack them so they will focus their attacks on you instead.",
  },
  {
    text = "Make your way through the last two short tunnels with rock lobsters and climb up the ladder.",
    actions = {
      Action.ConversationHighlight:new("Well, as quickly as you can, please."),
      Action.ConversationHighlight:new("Don't touch anything!"),
    },
    postconditions = {
      Condition.ConversationText:new(" Oh my, if we're not careful the whole lot could... No! LOOK OUT BELOW!"),
    },
  },
  {
    text = "Talk to Eir.",
    title = "Fremennik spiritual realm",
    actions = {
      Action.ConversationHighlight:new("Pardon?"),
      Action.ConversationHighlight:new("I'm not supposed to be here?"),
      Action.ConversationHighlight:new("Can you at least explain where I am?"),
      Action.ConversationHighlight:new("How do I get back then?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " This time I will send you to where your friends are. If you die again in pursuit of your current task I will be able to put you back on that track, or send you to the place you'd normally awake after such an incident."
      ),
    },
  },
  {
    text = "Talk to Nial Swiftfling.",
    actions = {
      Action.ConversationHighlight:new("[Say nothing]"),
      Action.ConversationHighlight:new("The dagannoths are mounting a massive attack on nearby settlements."),
      Action.ConversationHighlight:new("It's nothing I can't handle."),
    },
    postconditions = { Condition.ConversationText:new(" Mastery before modesty! That's what a hero needs!") },
  },
  {
    text = "Talk to Asleif Hamalsdotter.",
    actions = {
      Action.ConversationHighlight:new("Just passing through."),
      Action.ConversationHighlight:new("You realise they're waiting for a huge battle?"),
      Action.ConversationHighlight:new("I don't recall that working out so well last time."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " I think I did quite well during that whole 'tablet' thing. It's only when the man in the bear suit took me by surprise afterwards that things went sour."
      ),
    },
  },
  {
    text = "Talk to Eir.<ul><li>If the spiritual realm sequence is interrupted, speak with Brundt in the long hall in Rellekka to obtain a letter. Read the letter to return.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, please.") },
    postconditions = { Condition.ConversationText:new("The player is transported to Brundt the Chieftain's longboat.") },
  },
  { text = "Speak to Brundt the Chieftain.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Talk to King Vargas.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Cut to the chase]"),
    },
    postconditions = { Condition.ConversationText:new(" Apart from the names and about him being my husband?") },
  },
  { text = "Talk to Princess Astrid or Prince Brand.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Talk to Brundt to continue and bank.<ul><li>Prepare for a fight with Dagannoth sentinels and the Dagannoth Mother.</li></ul>",
    actions = { Action.ConversationHighlight:new("Actually, could you drop me off at the bank on Etceteria?") },
    postconditions = {
      Condition.ConversationText:new(
        " Of course. I have to go back to Rellekka, though, so look for me in the long hall there when you're ready."
      ),
    },
  },
  {
    text = "Talk to Brundt in the long hall in Rellekka when you are ready.",
    title = "Waterbirth",
    neededItems = { ["Balmung"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Ask about the war against the daggermouths."),
      Action.ConversationHighlight:new("Yes, let's go now."),
    },
    postconditions = {
      Condition.ConversationText:new("Cutscene plays. The player and company begin their seafaring journey."),
    },
  },
  {
    text = "Talk to Baba Yaga on the boat.",
    actions = {
      Action.ConversationHighlight:new("[Say nothing]"),
      Action.ConversationHighlight:new("What do you think?"),
      Action.ConversationHighlight:new("Yes, I have."),
      Action.ConversationHighlight:new("No, of course not!"),
      Action.ConversationHighlight:new("I'm ready."),
    },
    postconditions = { Condition.ConversationText:new(" I was hoping you'd say that.") },
  },
  { text = "Climb down the hole on the southwestern tip of the island." },
  {
    text = "Enter the southern tunnel.",
    actions = { Action.ConversationHighlight:new("That sounds reasonable.") },
    postconditions = { Condition.ConversationText:new("The player and company enter the tunnel.") },
  },
  {
    text = "Enter the next tunnel to fight the Dagannoth sentinels.<ul><li>After defeating the sentinels, you can go to bank to get more food. You don't need to fight the sentinels again. Also you don't need the tablets or the seal of passage for the fights (saves you a few slots for food).</li></ul>",
    actions = { Action.ConversationHighlight:new("Good luck.") },
    postconditions = { Condition.ConversationText:new(" We will be fine, I promise.") },
  },
  { text = "Continue to the next tunnel." },
  {
    text = "Go to the south-east intersection and enter the tunnel entrance.<ul><li>Use the picture on the right for reference.</li></ul>",
  },
  { text = "Proceed south and enter the tunnel." },
  {
    text = "Go through the next tunnel and select either chat option.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Start the battle with Balmung (the special weapon you received from Brundt) and kill the Dagannoth Mother, switching styles according to her colour.<ul><li>If you have trouble telling between the colours (specially red/orange/yellow), you can use the chatbox. It will say 'The dagannoth changes to red...' etc.</li></ul>",
  },
  {
    text = "After the Dagannoth Mother has been defeated, go back to the previous room, collect the pickaxe and plank and use them to collapse one of the pillars.",
  },
  {
    text = "Talk to Eir.",
    actions = {
      Action.ConversationHighlight:new("Excellent!"),
      Action.ConversationHighlight:new("I'll live."),
      Action.ConversationHighlight:new("Yes, I am."),
    },
    postconditions = { Condition.ConversationText:new(" Until the next time...") },
  },
  { text = "Check the bodies and finish chat.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk with Baba Yaga and Koschei." },
  {
    text = "Attempt to climb up the rope.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Quest complete!" },
  {
    text = "Speak to Brundt to receive your reward.",
    actions = { Action.ConversationHighlight:new("Ask about the war against the daggermouths.") },
  },
}

return Quest:new({
  name = "Blood Runs Deep",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.long,
  releaseDate = 1260835200,
  prereqQuests = { "Horror from the Deep", "Dream Mentor", "Glorious Memories" },
})
