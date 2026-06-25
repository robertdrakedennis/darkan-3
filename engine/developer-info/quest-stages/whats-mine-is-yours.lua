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
    text = "Talk to Doric in his hut, north of the Falador lodestone, until you get an ore bag and mining sites map. (You'll need 2 backpack space)",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Don't worry, I'll help you.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Mine any rock in each of the following locations until a living rock brawler appears, kill it, and take the ore it drops:<ul><li>Dwarven mines, to the east up the hill</li><li>Rimmington mine, west of Port Sarim</li><li>Watch the cutscene.</li><li>Varrock south-west mine</li><li>Varrock south-east mine</li><li>Watch the cutscene.</li></ul>",
  },
  { text = "Talk to Doric." },
  {
    text = "Withdraw the ores from the Ore bag and make two High-quality bronze bars using the furnace.",
    title = "The weapons",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Using Doric's Anvil, smith a High-quality bronze sword and a High-quality bronze dagger." },
  {
    text = "Talk to Doric.",
    actions = { Action.ConversationHighlight:new("I'll leave right away.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Go to the Artisans' Workshop in the south-east of Falador." },
  {
    text = "Talk to Aksel.",
    actions = { Action.ConversationHighlight:new("Doric sent me here with these weapons.") },
    postconditions = {
      Condition.ConversationText:new(
        "(With both weapons in backpack:) And look how beautifully made they are - almost as good as if we had made them here ourselves. So what did Doric want us to do with them? He told me to bring them to you so you could 'finish' them. I would be more than happy to help Doric out. The art of finishing requires a delicate hand and more dedication than that of simply creating a blade.Screen fades out and back in.Player has high-quality bronze dagger and high-quality bronze sword removed from them.Player receives finished bronze dagger and finished bronze sword.Many hours later, one of Aksel's master smiths finishes your high-quality weapons. With Doric's talents and our finish, these blades are fit for a king. Sir Amik, actually. Close enough. You'll want to head to the castle north-west of here and talk to Sir Amik's squire, Cerlyn. Do you have any more questions before you leave?(Non-quest dialogue) What's an artisan?(Non-quest dialogue) Tell me more about yourself.I've got to go.(Same as below.)"
      ),
    },
  },
  {
    text = "Head to the White Knights' Castle and talk to Squire Cerlyn in the west tower on the ground floor[UK] 1st floor[US].",
    title = "Doric and Boric",
  },
  {
    text = "After the cutscene, help Doric and Boric reconcile. Use the following responses:<ul><li>Boric, tell Doric about your graduation.</li><li>Boric, tell Doric what you thought when Doric sent you away.</li><li>Boric, tell Doric what you thought of your education.</li><li>Boric, tell Doric why you wanted to stay here.</li><li>Boric, tell Doric what you thought of Keldagrim</li><li>Doric, tell Boric what you thought when Boric was born.</li><li>Doric, tell Boric why you sent him to Keldagrim.</li><li>Doric, tell Boric how you felt bringing Boric up alone.</li><li>Doric, tell Boric what you want Boric's future to be.</li><li>Doric, tell Boric what you hoped sending him away would teach him.</li></ul>",
  },
  {
    text = "Talk to Doric and agree to take the bars.",
    actions = { Action.ConversationHighlight:new("I'd love to."), Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Smith the items that were asked for on Doric's anvil.<ul><li>Ornamental platebody</li><li>Ornamental chainbody</li><li>Shop sign</li></ul>",
  },
  {
    text = "Head down the stairs into Doric's cave (if the 'climb down' option on the northern doorway does not appear, try switching textures off temporarily, this is a known bug).",
  },
  { text = "Mine the rocks blocking the way." },
  {
    text = "Head back upstairs and talk to Doric to give him the items and trigger a cutscene.",
    actions = { Action.ConversationHighlight:new("Yes, I have them here.") },
    postconditions = {
      Condition.ConversationText:new(
        "(If the workshop has been cleared:) Thank you for all the assistance you've given us. I have something for you as a reward, but first let's get the opening ceremony started.(Continues below.)"
      ),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "What's Mine is Yours",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = false,
  length = Enums.length.medium,
  releaseDate = 1350432000,
  prereqQuests = {},
})
