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
    text = "Talk to Grish in Jiggig, located a short run south of the Castle Wars.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("What are Zogres?"),
      Action.ConversationHighlight:new("Can I help in any way?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to the ogre guard (without the combat level, east)." },
  { text = "Climb over the crushed barricade." },
  { text = "Run east and down the stairs." },
  {
    text = "Run as far north-west as you can, and search the skeleton, kill the zombie, and take his ruined backpack.<ul><li>If you're in combat when trying to search the skeleton, the zombie will not spawn.</li></ul>",
  },
  {
    text = "Open the ruined backpack. You will get a dragon inn tankard, a knife, and rotten food. Drop all except the dragon inn tankard.",
  },
  { text = "Search the broken lectern behind the skeleton." },
  { text = "Read the torn page." },
  { text = "Search the nearby ogre coffin until you find a black prism." },
  { text = "Enter the Dragon Inn pub in Yanille.", title = "Yanille" },
  { text = "Use the dragon inn tankard on the bartender." },
  {
    text = "Talk to Zavistic Rarve on the 1st floor[UK]2nd floor[US] of the Wizards' Guild. Ring the bell outside if you lack the requirements.",
    actions = { Action.ConversationHighlight:new("What did you say I should do?") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Enter the house just north of the guild, 1st floor[UK]2nd floor[US], talk to Sithik in bed.",
    actions = { Action.ConversationHighlight:new("Do you mind if I look around?") },
    postconditions = {
      Condition.ConversationText:new(
        " Well,err....well, actually yes I do mind...it's my place and I don't want strangers going through my things."
      ),
    },
  },
  { text = "Search the drawers, the cupboard, and the wardrobe." },
  { text = "Read the 3 books, use the papyrus on Sithik to make a portrait, then use the portrait on Sithik." },
  {
    text = "Make sure that Sithik says that he appreciates 'honesty' of the portrait or you will have to make another. If he says 'truth' you will have to try again. There is more papyrus in the drawers.",
  },
  {
    text = "Use the portrait on the bartender. He will return it as a Signed portrait.<ul><li>If he just says that he recognises Sithik without signing the portrait, it means that you forgot to show him the dragon inn tankard.</li></ul>",
  },
  {
    text = "Talk to to Zavistic Rarve. He will give you a strange potion.",
    actions = {
      Action.ConversationHighlight:new("I'm here about the sicks...err Zogres."),
      Action.ConversationHighlight:new("I have some items that I'd like you to look at."),
    },
    postconditions = { Condition.ConversationText:new("You show the Necromancy book to Zavistic.") },
  },
  { text = "Use the strange potion on the cup of tea beside Sithik." },
  {
    text = "Climb down the ladder, climb up the ladder, ask Sithik all the possible questions.",
    actions = {
      Action.ConversationHighlight:new("How do I remove the effects of the spell from the area?"),
      Action.ConversationHighlight:new("How do I get rid of the undead ogres?"),
      Action.ConversationHighlight:new("How do I get rid of the disease?"),
      Action.ConversationHighlight:new("Sorry, I have to go."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Return to Grish at the quest start, he will give you a key.",
    title = "Final Battle",
    actions = { Action.ConversationHighlight:new("I found who's responsible for the Zogres being here.") },
    postconditions = {
      Condition.ConversationText:new(" Where is da creature? Me's wants to squeeze him till he's a deadun..."),
    },
  },
  {
    text = "Talk to him again to be able to create a comp ogre bow and brutal arrows, which can be used to fight the upcoming boss.<ul><li>(Optional) Use a clean rogue's purse on a vial of water, and then add a clean snakeweed to create a relicym's balm. Use this on Uglug Nar next to Grish, and you unlock his shop Uglug's Stuffsies which sells the comp ogre bow. This will also complete the Brutal Stuffsies achievement.</li><li>(Optional) Either buy brutal arrows from the Grand Exchange, or fletch them at this point; there are achey trees to the west of Grish, chop them down and fletch their logs into shafts, then attach feathers, and tip them with nails of your choosing.</li></ul>",
    actions = { Action.ConversationHighlight:new("There must be an easier way to kill these zogres!") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Cross the barricade to the east and go down the stairs again." },
  { text = "Once inside, run directly west, through the double doors and down more stairs." },
  { text = "Go north-east and search the stand in front of the pile of skulls. Prepare for battle." },
  { text = "Kill Slash Bash, pick up the Ogre artefact he drops." },
  {
    text = "Return to Grish at quest start.",
    actions = { Action.ConversationHighlight:new("Yeah, I have them here!") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Zogre Flesh Eaters",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1116288000,
  prereqQuests = { "Big Chompy Bird Hunting", "Jungle Potion" },
})
