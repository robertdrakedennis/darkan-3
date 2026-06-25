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
    text = "Talk to Grand Vizier Hassan in the Merchant district, south of the bank deposit box.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about Our Man in the North.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Go to the Grand Library of Menaphos in the Imperial district. The entrance is next to the deposit box (bank chest).",
    title = "The bloodline",
  },
  {
    text = "Talk to Kohnen the librarian, who is just north of the entrance.",
    actions = { Action.ConversationHighlight:new("*whisper* I'm looking for a book.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Exit the Grand Library." },
  {
    text = "Head north to talk to Commander Akhomet.",
    actions = {
      Action.ConversationHighlight:new("Talk about Our Man in the North."),
      Action.ConversationHighlight:new("Historical text and bloodlines."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Head to Pollnivneach." },
  {
    text = "Speak to Aristarchus, who is north-west of the south magic carpet station.",
    actions = { Action.ConversationHighlight:new("Talk about Our Man in the North") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "You will have to fight four Menaphite Thugs.<ul><li>They are level 122 and fight using melee.</li><li>You can hide behind Aristarchus if you need a safe spot and are using ranged/magic/necromancy.</li></ul>",
  },
  {
    text = "Speak to Aristarchus again who will agree to meet you in the library.",
    actions = { Action.ConversationHighlight:new("Talk about Our Man in the North") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Return to the Grand Library and speak to Aristarchus, who is south of the entrance inside the library." },
  { text = "Go to Uzer Mastaba and enter the pyramid to trigger a cutscene. (fairy code DLQ)" },
  {
    text = "After the cutscene, enter the pyramid once more and speak to Senliten.",
    actions = {
      Action.ConversationHighlight:new("Talk about Our Man in the North."),
      Action.ConversationHighlight:new("Ozan is Senliten's descendant?"),
      Action.ConversationHighlight:new("Where are you going with this?"),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Go to Al Kharid and speak to Osman, who is in the throne room, south of the lodestone." },
  {
    text = "Speak to Emir Ali Mirza in front of the throne.",
    actions = { Action.ConversationHighlight:new("Talk about Our Man in the North.") },
    postconditions = {
      Condition.ConversationText:new(
        " I cannot believe he would stoop so low. He has been spymaster so long, he has forgotten how to act."
      ),
    },
  },
  {
    text = "Leave the Palace and head north-east to Dommik's Crafting Store.<ul><li>Ignore the quest active area marked on the minimap.</li></ul>",
    title = "Tracking down Jabari",
  },
  { text = "Climb the stairs on the inside of the crafting store." },
  { text = "Climb the ladder south of the stairs." },
  {
    text = "Follow these steps across the rooftops:<ul><li>Walk across the plank to the south.</li><li>Slide down the awning to the south.</li><li>Go through the building.</li><li>Walk across the washing line (north-west from where you started).</li><li>Climb the ladder to the west.</li><li>Talk to Jabari.</li><li>Cross the planks to the west.</li><li>Climb the ladder to the southwest.</li><li>Jump from the scaffold to the west.</li><li>Climb down the rug to the south.</li><li>Swing across the wooden frame immediately west.</li><li>Jump from the scaffold (west) to the next building.</li><li>Bounce on the awning (south) to the bank.</li><li>Climb up the brickwork to the west on the wall.</li><li>Talk to Jabari.</li><li>Parrot drop on the rug to the south.</li><li>Do not shimmy across the rope.</li><li>Run north of the bank, west of the furnace, and talk to Jabari.</li><li>Run north-east of the Al Kharid lodestone into a tent, and talk to Jabari.</li><li>Run north to the house near the north-east exit of Al Kharid, and talk to Jabari.</li><li>You can get a free heal from the Surgeon General west of this house.</li><li>Do not shimmy across the rope.</li><li>You can get a free heal from the Surgeon General west of this house.</li></ul>",
  },
  { text = "Go to Het's Oasis." },
  {
    text = "Prepare to battle Jabari four times. He is level 112 and uses magic. He has a weakness to slash.",
    title = "Fighting Jabari",
  },
  {
    text = "Talk to Jabari.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Jabari will teleport you into the centre of the arena, surrounding you with his clones.<ul><li>Choosing the wrong Jabari will cause an explosion that damages the player and scales with level.</li><li>Find the real Jabari by attacking the odd one out. As it is random for each player, it could be any of these:</li><li>The only one facing backwards.</li><li>The only one in attack position.</li><li>The one with the smallest sceptre.</li><li>The largest one of them.</li><li>The one targeted by the audience.</li><li>The one with the correctly spelled name.</li><li>The one with a different robe colour.</li><li>The one without prayer renewal particles.</li><li>The one who is not translucent.</li><li>The only one facing backwards.</li><li>The only one in attack position.</li><li>The one with the smallest sceptre.</li><li>The largest one of them.</li><li>The one targeted by the audience.</li><li>The one with the correctly spelled name.</li><li>The one with a different robe colour.</li><li>The one without prayer renewal particles.</li><li>The one who is not translucent.</li></ul>",
  },
  { text = "Talk to Het." },
  {
    text = "Talk to Emir Ali Mirza, in the Al Kharid throne room.",
    actions = { Action.ConversationHighlight:new("Talk about Our Man in the North.") },
    postconditions = {
      Condition.ConversationText:new(
        " I cannot believe he would stoop so low. He has been spymaster so long, he has forgotten how to act."
      ),
    },
  },
  {
    text = "After the cutscene, talk to Emir Ali Mirza again.",
    actions = { Action.ConversationHighlight:new("Talk about Our Man in the North.") },
    postconditions = {
      Condition.ConversationText:new(
        " I cannot believe he would stoop so low. He has been spymaster so long, he has forgotten how to act."
      ),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Our Man in the North",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1496620800,
  prereqQuests = { "The Feud", "Do No Evil", "Crocodile Tears" },
})
