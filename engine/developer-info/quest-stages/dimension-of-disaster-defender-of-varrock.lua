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
    text = "Speak to Reldo in the Castle library of New Varrock.",
    title = "Zombie disguise",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Arrav?") },
  },
  {
    text = "In the courtyard just east of you, talk to Arrav.",
    actions = { Action.ConversationHighlight:new("Would you be willing to bring down Zemouregal?") },
    postconditions = { Condition.ConversationText:new(" Ha! Bring down Zemouregal? Fwahahaha!") },
  },
  { text = "Tell Reldo what you learned from Arrav." },
  { text = "Search the northern-most bookshelf along the western wall for Heart magic notes." },
  { text = "Talk to Reldo again." },
  {
    text = "If not wearing the zombie override version, equip the full Zombie outfit (mask, shirt, trousers, gloves, and boots). This can be purchased from Thessalia's Fine Clothes. See Zemomark for ways to earn more currency.<ul><li>Replay: Full New Varrock Zombie outfit cosmetic override can be purchased from Aris' Reward shop with silver pennies (you must talk to Aris in the real Varrock. You must exit New Varrock to access her reward shop in Varrock Square in the southwest corner).</li></ul>",
  },
  {
    text = "Return to the New Varrock Castle. Enter the kitchen which is located on the ground floor[UK]1st floor[US] on the east side of the castle. You must enter the kitchen via the west door as a barrier prevents you from entering via the south door. Take 2 vials from the table.<ul><li>Replay: If you have saved red mist from a previous run, just head upstairs.</li></ul>",
  },
  {
    text = "Head upstairs via the south-east staircase of the castle and kill 2 armoured zombies - collecting the red mist that they emit upon dying by clicking on it with a vial in your inventory.<ul><li>Replay: If you have saved red mist, skip.</li></ul>",
  },
  { text = "Replay: Skip this step.", title = "Tin soldier" },
  { text = "Go north and listen to Zemouregal over balcony." },
  { text = "On this same floor, enter the red barrier in the hallway to the centre balcony." },
  {
    text = "Head west, passing through 2 additional red barriers. You will be in a room with several coffins, a large wood table, and a tin soldier strapped to the table.",
  },
  { text = "Inspect the tin soldier on the operating table in the south-western room." },
  { text = "Exit the castle by returning the way in which you entered." },
  {
    text = "Talk to the Trial Announcer at the east Varrock bank to get tin soldier head.",
    actions = {
      Action.ConversationHighlight:new("Can I have a tin soldier head?"),
      Action.ConversationHighlight:new("I'd like to get ahead of competition."),
      Action.ConversationHighlight:new("I have to go."),
    },
  },
  {
    text = "Get three tin ores, four fire runes, and a nature rune. These can be purchased from Sani's ore shop south of the west bank and Aubury's rune shop south of the east bank.",
  },
  {
    text = "Return to the tin soldier. Inspect him three times to activate him fully. , ,<ul><li>Inspect the tin soldier one last time or just use a vial of red mist on the tin soldier if it has not automatically been applied.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Go-go, roboto!"),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Replay: Skip this step if you have the decoder strips.<ul><li>If you don't have the decoder strips, go to the armoured zombie room on the east side of the 1st floor[UK]2nd floor[US] of the castle, take the strips from the crate, then go to the next section.</li></ul>",
    title = "Getting the code",
  },
  { text = "Return to the far eastern room." },
  { text = "Search the crate in the armoured zombie room for some decoder strips." },
  { text = "Kill another armoured zombie if you need to refill empty vial with red mist." },
  { text = "Enter the red barrier to the far north-western room." },
  {
    text = "Scry the scrying pool in the north-eastern corner. Select  (need to re-click on the scrying pool after each one) to unlock the medium New Varrock achievement Scrying Game and then  to continue the quest.",
    actions = {
      Action.ConversationHighlight:new("New Varrock square"),
      Action.ConversationHighlight:new("Demon camp"),
      Action.ConversationHighlight:new("Graveyard"),
      Action.ConversationHighlight:new("Saradominist church"),
      Action.ConversationHighlight:new("Throne room balcony"),
      Action.ConversationHighlight:new("Garden of Hostility"),
      Action.ConversationHighlight:new("Control the tin soldier"),
    },
  },
  { text = "As the tin soldier, enter the room north and search Zemouregal's lab desk for the code." },
  {
    text = "Unlock the treasure room safe (north-west corner of the north-west room) using the code you were given in the chatbox. Drag the red sliders over the numbers.",
    title = "Black prism",
  },
  { text = "Enter the treasure room safe." },
  { text = "Smash the black prism." },
  { text = "Talk to Arrav twice.", title = "Finishing up" },
  { text = "Quest complete!" },
  {
    text = "Pick up the watering can for the next sub-quest.<ul><li>Replay: Not needed if you saved purple mist.</li></ul>",
  },
}

return Quest:new({
  name = "Dimension of Disaster: Defender of Varrock",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1427068800,
  prereqQuests = { "Dimension of Disaster: Coin of the Realm", "Defender of Varrock" },
})
