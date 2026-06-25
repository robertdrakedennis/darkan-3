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
    text = "Talk to the spirit at the Wilderness wall north of Infernal Source Dig Site.",
    title = "The farm",
    neededItems = { ["Ghostspeak amulet"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Keep trying to catch the spirit until you see a cutscene.",
    actions = { Action.ConversationHighlight:new("OK") },
  },
  { text = "Talk to the father figure with a ghostspeak amulet equipped." },
  {
    text = "Use the table below to respond to the man's emotes.<ul><li>Don't repeat yourself.</li><li>Use the Think emote to see the emote again. Talk to him and your character will say the emote he is doing.</li><li>The rings around the girl will disappear on successful emotes.  When all the rings are gone, the girl is free.</li></ul>",
  },
  {
    text = '<table class="wikitable"><tbody><tr><th>Man</th><th>Possible Response</th></tr><tr><td>Yes</td><td>No, Angry, Wave</td></tr><tr><td>No</td><td>Angry, Wave, Cheer</td></tr><tr><td>Bow/Curtsy</td><td>Yes, No, Angry, Shrug</td></tr><tr><td>Angry</td><td>Bow/Curtsy, Shrug, Cheer</td></tr><tr><td>Wave</td><td>Angry, Bow/Curtsy, Shrug</td></tr><tr><td>Shrug</td><td>Yes, No, Wave, Cheer</td></tr><tr><td>Cheer</td><td>Yes, Bow/Curtsy, Wave</td></tr></tbody></table>',
  },
  {
    text = "Once the girl is free, talk to her.",
    actions = {
      Action.ConversationHighlight:new("What's going on here?"),
      Action.ConversationHighlight:new("Okay, I will come with you to the village you're talking about."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Screen fades in and back out. The spirit transports the players to the west side of the village."
      ),
    },
  },
  {
    text = "Once in the village, ask the girl to be taken back.",
    title = "Fixing the village",
    actions = { Action.ConversationHighlight:new("Can you take me back, please?") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Search a crate for a limpwurt seed." },
  {
    text = "Rake the farming patch and plant the seed.<ul><li>Note: You can use a Supreme growth potion (leafy) on it, but do not harvest the roots yet.</li><li>Wait limpwurt plant to grow (roughly 20 minutes), but do not harvest it. You cannot continue the quest until the plant grows.</li></ul>",
  },
  {
    text = "Pick up 4 steel nails and a hammer in the anvil building (south east).<ul><li>Note: Sometimes you will find only 3 nails there. More nails can be found in the sacks or the building west of the farming patch, near the bucket. You can also smith nails on the anvil.</li></ul>",
  },
  { text = "Pick up 2 planks in the centre-most building." },
  { text = "Use a plank on the wardrobe in the building south-east of the statue." },
  { text = "With three free inventory slots, Pick from the Hollow log near the statue for button mushrooms." },
  { text = "Grab a bucket from the building directly west of the farm patch." },
  { text = "Use the bucket on the barrel of water south-east of the farming patch." },
  { text = "After the limpwurt plant is grown, talk to the spirit for a cutscene." },
  {
    text = "Talk to her again and agree to come along.",
    actions = { Action.ConversationHighlight:new("Okay, I'll come along.") },
    postconditions = {
      Condition.ConversationText:new(
        " It's nice to meet you, Summer. My name is Player. Now let's head back to the farm."
      ),
    },
  },
  {
    text = "Talk to her once more to be taken back.",
    title = "Back in the farm",
    actions = { Action.ConversationHighlight:new("Can you take me back?") },
    postconditions = { Condition.ConversationText:new(" Alright. Please don't be gone for long: we need your help.") },
  },
  { text = "Take a skull and 5 bones east of the farm." },
  { text = "Stand in the portal." },
  {
    text = "Talk to Summer.",
    actions = {
      Action.ConversationHighlight:new("What exactly is going on here?"),
      Action.ConversationHighlight:new("Of course, I will help you."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "Follow her and Climb-down the Trapdoor." },
  {
    text = "Use the skull and right-click Use the bones on the altar.<ul><li>Note: Take care not to bury the bones.</li></ul>",
  },
  {
    text = "Climb-up the ladder and talk to Summer again.",
    actions = {
      Action.ConversationHighlight:new("What should I do now?"),
      Action.ConversationHighlight:new("I better get to work, then."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Optional: Customise your interface to allow you to dismiss familiars as fast as possible.",
    title = "Luring the beast",
    neededItems = {
      ["Spirit wolf pouch"] = { quantity = 1 },
      ["Desert wyrm pouch"] = { quantity = 1 },
      ["Spirit scorpion pouch"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Read all the following steps before executing them:<ul><li>Run north to the wolf platform.</li><li>Summon a spirit wolf so that it stands on the platform (it doesn't have to be in the center).</li><li>The Spirit Beast will only be lured to a platform with a familiar summoned.</li><li>Dismiss the familiar as soon as the beast steps on the platform (here your familiar will be dismissed immediately without the 'are you sure' warning). A lightning effect will show if successful.</li><li>Don't let the beast consume your familiar, and don't move your familiar off the platform.  It is helpful to try to position yourself between the beast and your familiar.</li><li>A cutscene will play if you have succeeded.</li><li>If you have to teleport out for more pouches, you must talk to Summer by the quest start location to return to the spirit realm.</li></ul>",
  },
  { text = "Repeat this process with the Desert Wyrm, using the platform that appears during the cutscene." },
  { text = "And again for the Spirit Scorpion - the platform is far west." },
  { text = "Talk to any of the spirits by the Spirit Beast." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Spirit of Summer",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1217289600,
  prereqQuests = { "The Restless Ghost" },
})
