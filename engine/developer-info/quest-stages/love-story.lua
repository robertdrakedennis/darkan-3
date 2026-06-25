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
    text = "Talk to Mabel in the barn east of the Draynor Village lodestone.",
    title = "A damsel in distress",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Can you tell me what's wrong?") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Okay, off I go.") },
  },
  { text = "Go up the ladder." },
  {
    text = "Solve the hay bale puzzle:<ul><li>As you arrive, go north once and push the one hay bale there all the way to the western wall.</li><li>Standing where you are, push a pair of hay bales at your immediate south twice.</li><li>Go one square east and push another pair of hay bales to the south.</li><li>Push the single hay bale directly west of you.</li><li>Finally, push the two hay bales to the south of you to clear the path.</li></ul>",
  },
  { text = "Pick up the ring." },
  { text = "Climb down the ladder." },
  {
    text = "Talk to Zenevivia.",
    actions = {
      Action.ConversationHighlight:new("What's happened to Mabel?"),
      Action.ConversationHighlight:new("Are you going to give Mabel her ring?"),
      Action.ConversationHighlight:new("What do you want me to do?"),
      Action.ConversationHighlight:new("Okay, off I go."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Search any hay bale for a twig, give it to her." },
  {
    text = "Talk to her again.",
    actions = {
      Action.ConversationHighlight:new("Will you return Mabel's ring and leave now?"),
      Action.ConversationHighlight:new("I'm trying to complete this quest."),
      Action.ConversationHighlight:new("Alright, I'll accept. I'll make you regret this!"),
    },
  },
  {
    text = "Go to Draynor Village and talk to the Wise Old Man.<ul><li>This option may not show up unless the quest Garden of Tranquillity is completed if you started it.</li></ul>",
    title = "Wise Old Man",
    actions = { Action.ConversationHighlight:new("A woman called Zenevivia wishes to challenge us.") },
    postconditions = { Condition.ConversationText:new(" Zenevivia? Y-y-you've met Zenevivia?") },
  },
  {
    text = "After the cutscene, talk to him outside his house. If you have a familiar summoned then you must dismiss it to be able to progress.",
    actions = { Action.ConversationHighlight:new("No."), Action.ConversationHighlight:new("Yes, let's go.") },
    postconditions = { Condition.ConversationText:new("The Wise Old Man begins following the player.") },
  },
  {
    text = "Head to the Rimmington house portal, talk to him again (Home Teleport works, teleport to Port Sarim and head west).",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("How can we get in there?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " I believe we'll be able to do it by chipping a teleport spell to change its destination manually. Magic is so flexible if you know these tricks!"
      ),
    },
  },
  {
    text = "When he leaves, walk to the house just east of the portal and talk to him once more.",
    actions = { Action.ConversationHighlight:new("Yes, I'm ready.") },
    postconditions = {
      Condition.ConversationText:new(
        " Now, listen carefully. You've got to access the core of the tablet - where the spell is stored - so that you can modify it manually."
      ),
    },
  },
  {
    text = "Open the 'chipped' teleport to house tablet, solve the puzzle by dragging and dropping tiles from the left side on to the correct spots according to the photo here.",
  },
  {
    text = "While standing next to the Wise Old Man, break the chipped tablet.",
    title = "Zenevivia's house",
    neededItems = {
      ["Oak plank"] = { quantity = 1 },
      ["Mithril bar"] = { quantity = 1 },
      ["Steel bar"] = { quantity = 1 },
      ["Clockwork"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Enter the dungeon.",
    actions = { Action.ConversationHighlight:new("Traps? Very dangerous. You go first.") },
    postconditions = { Condition.ConversationText:new(" Hmmph.") },
  },
  {
    text = "After the cutscene, continue dialogue with the Wise Old Man.",
    actions = { Action.ConversationHighlight:new("What are we going to build?") },
    postconditions = {
      Condition.ConversationText:new(
        " You'll need a mithril bar, 2 oak planks and either a clockwork mechanism or a steel bar. Bring them to Zenevivia's workshop and I'll show you what to do next."
      ),
    },
  },
  {
    text = "Craft via on the Clockmaker's bench in the workshop room.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Use the bench again to create a dummy.",
    actions = { Action.ConversationHighlight:new("Okay, I'll get on with it.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Enter the dungeon. During this part, there will be a pulsating purple light around your screen. Any players sensitive to flashing lights should proceed with caution.",
  },
  {
    text = "Traverse through the dungeon, deactivating all the traps by walking on them and then return to the surface.<ul><li>Pit guardian rooms have 2 traps each, except for the scabarite room, having only one.</li></ul>",
  },
  {
    text = "Climb the staircase.",
    actions = { Action.ConversationHighlight:new("Yes, get it out now.") },
    postconditions = { Condition.ConversationText:new(" You retrieve the dummy from the dungeon.") },
  },
  {
    text = "Enter the dungeon.",
    title = "The dungeon pit",
    actions = { Action.ConversationHighlight:new("Yes, I'm sure I found them all. Let's enter.") },
    postconditions = { Condition.ConversationText:new("The player and the Wise Old Man both enter the dungeon.") },
  },
  {
    text = "Traverse through the dungeon, killing all the enemies (high-level players need not worry about using super antifire for the iron dragon as it is fairly weak and may be killed quickly).",
  },
  {
    text = "Enter the final door, talk to the Wise Old Man.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("She's insane; let's kill her and leave!"),
      Action.ConversationHighlight:new("Okay, let's go and face her."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "Climb the stairs." },
  {
    text = "Talk to Zenevivia to start the battle. If you fail your first  attempts, return to the Wise Old Man at the house in Rimmington. Right click on the house teleport tabs to modify them for another teleport to the fight.<ul><li>Avoid the flames on the floor, they deal low damage.</li><li>Zenevivia uses magic spells and flames on the floor that can quickly deal around 600 damage.</li><li>She will sometimes use protection prayers so prepare to switch attack styles or just use Necromancy.</li><li>She can be knocked off pillars using abilities that knock back enemies, such as Kick.</li></ul>",
    title = "The final battle",
  },
  { text = "After she is defeated, talk to her." },
  { text = "You will return to Draynor Village.", title = "Robbing the Wizards' Tower" },
  {
    text = "Talk to Zenevivia in the Wise Old Man's house.",
    actions = {
      Action.ConversationHighlight:new("Have you still got Mabel's ring?"),
      Action.ConversationHighlight:new("When will you give me Mabel's ring?"),
      Action.ConversationHighlight:new("Stealing is wrong."),
      Action.ConversationHighlight:new("When are you going to attack?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Quite soon, I think. Go upstairs and look through our telescope, you should get quite a good view."
      ),
    },
  },
  { text = "Go upstairs and look through the telescope and watch the Wizards' Tower Robbery." },
  {
    text = "Head downstairs, talk to Zenevivia again.",
    actions = {
      Action.ConversationHighlight:new("Are you going to try any more robberies?"),
      Action.ConversationHighlight:new("Can I have Mabel's ring back now?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Oh, are you still fussing about that stupid girl's ring? Yes, I'll give it back, and I promise I won't bother her again."
      ),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Love Story",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1278288000,
  prereqQuests = { "Swan Song" },
})
