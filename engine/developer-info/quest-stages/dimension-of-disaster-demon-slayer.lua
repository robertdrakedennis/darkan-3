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
    text = "Talk to Aris in New Varrock Square.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Talk to Sir Prysin in a house north-west of the church." },
  {
    text = "Search everything inside the house and the drain outside until you find the key.<ul><li>Replay: The key will always be found in the same place you first found it.</li></ul>",
  },
  {
    text = "Climb down the trapdoor inside the church along the western wall (be sure to complete the dialogue).<ul><li>Replay: Click the trapdoor, complete the dialogue, then head straight to Aris.</li></ul>",
  },
  { text = "Attempt to pull Silverlight. You will fail.<ul><li>Replay: Skip this step.</li></ul>" },
  { text = "Talk to Aris in her tent, she will give you a spirit measure." },
  {
    text = "Head to the cemetery north of the New Varrock west bank and inspect the tombstone stack next to the statue of Zemouregal on the north wall.",
    title = "Spirit of Faith",
  },
  { text = "Talk to Spirit of Faith. Move 1 step east. Talk again. Then watch the short cutscene." },
  {
    text = "Pick up a rotten tomato in the crate north of the Blue Moon Inn and pick a cabbage to the south of the pub.",
    title = "Spirit of the Body",
    neededItems = {
      ["Cabbage (New Varrock)"] = { quantity = 1 },
      ["Rotten tomato (New Varrock)"] = { quantity = 1 },
      ["Dog hair"] = { quantity = 1 },
      ["Beer glass (New Varrock)"] = { quantity = 1 },
      ["Beer (New Varrock)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Talk to Dr Harlow in the Blue Moon Inn (exit dialogue after the first box comes up). Your Spirit measure may say 'WooOOooOOooOOooOOooOOoo!' when you do.",
  },
  {
    text = "At the cost of 2 Zemomarks, buy beer from the Bartender in the Blue Moon Inn.",
    actions = {
      Action.ConversationHighlight:new("A glass of your finest ale."),
      Action.ConversationHighlight:new("Nothing, thanks."),
    },
  },
  {
    text = "To get dog hair, go west to Gertrude's house, just north of the tanner. Search the kennel next to her house.",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Reach left."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Reach right."),
      Action.ConversationHighlight:new("Rub in some jelly."),
      Action.ConversationHighlight:new("Reach further in."),
      Action.ConversationHighlight:new("Leave it where it is."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "That was probably the single most disgusting thing you've experienced in your life. The memory of it is now irreversibly lodged in your mind."
      ),
    },
  },
  {
    text = "Talk to the Apothecary to the south-east of the Kennel.  He will use the cabbage, dog hair, and rotten tomato.",
    actions = {
      Action.ConversationHighlight:new("Do you know a potion to sober someone up?"),
      Action.ConversationHighlight:new("Nothing, thanks."),
    },
  },
  {
    text = "Add sobriety potion to beer for a sobriety potion in a beer glass.<ul><li>Keep the vial for later.</li></ul>",
  },
  { text = "Talk to Dr Harlow." },
  {
    text = "Talk to the Spirit of the Body, and let them kill you  to arrive in the town square.",
    actions = {
      Action.ConversationHighlight:new("Would you killing me count?"),
      Action.ConversationHighlight:new("Yes, do it - I'll be fine!"),
      Action.ConversationHighlight:new("Yes! Kill me already!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "The Spirit of the Body kills the player. The player reawakens in New Varrock Square."
      ),
    },
  },
  {
    text = "Fill vial with blood at blood fountain at the front of the Varrock castle or north-west of eastern bank.<ul><li>Optional: Take a free bronze sword from the weapon store two buildings south of Aris (only necessary if you don't have any other weapons yet).</li></ul>",
    title = "Spirit of the Mind",
    neededItems = { ["Vial"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Head to the house south of Sir Prysin's house (near the church)." },
  { text = "Search the southern bookcase." },
  { text = "Kill the animated book." },
  {
    text = "Talk to Spirit of the Mind.<ul><li>Replay:  (Always choose the last option whenever that last option says, 'I'm looking for you!')</li></ul>",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'm looking for you!"),
      Action.ConversationHighlight:new("A sword that slays demons."),
      Action.ConversationHighlight:new("I need it to help Delrith get home."),
      Action.ConversationHighlight:new("Agrith Naar."),
      Action.ConversationHighlight:new("Aris!"),
    },
  },
  {
    text = "Climb down the trapdoor in the church.",
    title = "Evil Dave",
    neededItems = { ["Black mushroom (Dimension of Disaster)"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Take Silverlight from the altar." },
  {
    text = "Buy a black robe set from Thessalia's Fine Clothes (90 Zemomarks)<ul><li>Replay: Can use cosmetic override (Cultist outfit) if bought, or saved outfit.</li></ul>",
  },
  { text = "Pick a black mushroom by Evil Dave in south-east New Varrock." },
  { text = "Use the mushroom on your Silverlight for a dyed version." },
  {
    text = "Equip the robes and dyed Silverlight, then talk to Evil Dave (located South-East of New Varrock)<ul><li>The Darklight and the dyed Silverlight override do not work with Silverlight equipped.</li></ul>",
  },
  {
    text = "Pass barrier.<ul><li>Optional: It is recommended to loot the chest in the northeast corner of the area to work towards completing the Pick All achievement.</li></ul>",
    title = "Finishing up - Boss Fight",
  },
  { text = "Talk to Delrith." },
  { text = "Talk to Delrith again for the fight (First time only: Don't forget to loot the chest)." },
  {
    text = "Fight with Agrith Naar - choose 'Yes'.<ul><li>Replay: Choose 'Yes' to skip the fight if unlocked from Aris' Reward Shop.</li><li>Kill minions when they spawn.</li><li>Run away when Agrith says 'Prepare to be incinerated!'</li></ul>",
  },
  { text = "Pass barrier." },
  { text = "Return to Aris." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Dimension of Disaster: Demon Slayer",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1427068800,
  prereqQuests = { "Dimension of Disaster: Coin of the Realm", "Demon Slayer", "Shadow of the Storm" },
})
