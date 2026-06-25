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
    text = "Talk to Larry near the penguin enclosure in the Ardougne Zoo.",
    title = "Penguin spotting",
    neededItems = { ["Oak planks"] = { quantity = 1 }, ["Steel nails"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Cold War") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Larry again when carrying 10 oak planks and 10 steel nails.",
    actions = {
      Action.ConversationHighlight:new("Cold War"),
      Action.ConversationHighlight:new("Yes, I have all the materials."),
      Action.ConversationHighlight:new("Yes"),
    },
  },
  { text = "Talk to Larry once on the Iceberg." },
  { text = "Use an oak plank on the firm snow patch." },
  {
    text = "Attempt to enter the bird hide structure to cover it in snow with spade.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  { text = "Enter snowy bird hide." },
  {
    text = "Take note of the emotes the left penguin is doing. The snowy bird hide can be re-entered to watch again if needed. You can check the quest journal later in the quest if you aren't sure.",
  },
  { text = "Talk to Larry.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Travel using the nearby boat.", actions = { Action.ConversationHighlight:new("Yes") } },
  { text = "Talk to Larry once on the dock." },
  {
    text = "Make a clockwork suit on crafting table 3 or 4 in a player-owned house, which requires a clockwork, regular plank, and some silk.<ul><li>Making an extra suit at this point is optional for the subsequent quests.</li></ul>",
    title = "Clockwork penguin",
    neededItems = {
      ["Clockwork"] = { quantity = 1 },
      ["Steel bar"] = { quantity = 1 },
      ["Silk"] = { quantity = 1 },
      ["Plank"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Return to Larry (in Ardougne Zoo or north of Fremennik Province lodestone).",
    actions = {
      Action.ConversationHighlight:new("Cold War"),
      Action.ConversationHighlight:new("Yes, I have it."),
      Action.ConversationHighlight:new("Yes"),
    },
  },
  {
    text = "Talk to Larry on the iceberg.<ul><li>Accept teleport to the Ardougne Zoo.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("It looks like a warning message to keep us away."),
      Action.ConversationHighlight:new("Yes"),
    },
  },
  { text = "Unequip cape and weapons and use the tuxedo-time option on Larry.", title = "Suit up!" },
  { text = "Enter the penguin pen and talk to the penguin." },
  {
    text = "Use the greeting emotes from earlier. If you haven't noted down the emotes, look in your quest log after you've talked to the penguin.",
  },
  { text = "Read the mission report and exit the penguin pen." },
  { text = "Talk to Larry twice.", actions = { Action.ConversationHighlight:new("Cold War") } },
  {
    text = "Go to the west section of the Lumbridge cow field by the gate and steal a cowbell from a dairy cow by using the steal-cowbell option for later on in the quest.<ul><li>Players without 15 Thieving can get a cowbell by talking to the dairy cow in Zanaris.</li></ul>",
    title = "Incognito!",
    neededItems = { ["Raw cod"] = { quantity = 1 }, ["Ring of charos"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Talk to Larry inside the sheep pen west of Fred the Farmer's house in Lumbridge." },
  {
    text = "Enter penguin suit by right clicking Larry and selecting 'tuxedo time' or using the clockwork suit on Larry and talk to the penguins disguised as a sheep (the sheep with flippers).",
  },
  { text = "Do the greeting emotes." },
  { text = "Talk to sheep penguin again." },
  { text = "Talk to Larry twice.", actions = { Action.ConversationHighlight:new("Yes") } },
  {
    text = "Equip the ring of charos or have a raw cod in the inventory and use the tuxedo-time option on Larry and enter the penguin suit again once at Ardougne Zoo.",
  },
  {
    text = "Enter penguin pen and talk to penguin.",
    actions = {
      Action.ConversationHighlight:new("The penguins in Lumbridge refuse to talk to me."),
      Action.ConversationHighlight:new("I must have left the outpost before they gave out the phrase."),
      Action.ConversationHighlight:new("Sure!"),
    },
  },
  { text = "Talk to Larry twice.", actions = { Action.ConversationHighlight:new("Cold War") } },
  { text = "Go back to the Lumbridge sheep pen.", title = "Intelligence" },
  {
    text = "Enter penguin suit by right clicking Larry and selecting 'tuxedo time' or using the clockwork suit on Larry.",
  },
  { text = "Talk to sheep penguin." },
  { text = "Talk to Larry twice." },
  {
    text = "Talk to Fred the Farmer nearby.<ul><li>Note: Chat options may vary for some players. Choose the option for penguins.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you about penguins."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Enter penguin suit and talk to sheep penguin again.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to Larry twice.", actions = { Action.ConversationHighlight:new("Yes") } },
  {
    text = "Dismiss any pets or followers.",
    title = "Surprise, surprise",
    neededItems = { ["Swamp tar"] = { quantity = 1 }, ["Feathers"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Talk to Larry on the iceberg and get in the penguin suit again and talk to KGP Agent north-west of Larry.",
  },
  { text = "Do the greeting emotes." },
  { text = "Talk to Noodle twice.", actions = { Action.ConversationHighlight:new("Yeah, I got it.") } },
  {
    text = "Talk to KGP Agent.<ul><li>Make sure you finish the dialogue or you won't be able to enter the penguin lair.</li></ul>",
  },
  { text = "Investigate avalanche just north of KGP Agent." },
  { text = "Enter the first west room." },
  { text = "Talk to KGP Agent." },
  { text = "Exit room through the west door and then through the north door." },
  { text = "Walk north to the agility course." },
  {
    text = "Climb down the steps and into the water.<ul><li>Stepping across the checkered flag should automatically drop you in water.</li></ul>",
    title = "Agility course",
  },
  { text = "Avoid the moving ice in the water." },
  { text = "Climb the stepping stone at the end." },
  { text = "Jump across the next stones until you reach the other side." },
  {
    text = "After crossing, continue on the path until you come to an arch covered in icicles. Use the tread softly option to pass. This must be repeated several times at each arch. Players who fail will be hit with a small amount of damage.",
  },
  {
    text = "Next, climb up the ice. This can be very annoying, as players lose 20 life points each time they fail, and the failure rate is surprisingly high. For players with low Agility levels, a small amount of food may help.",
  },
  { text = "After that, a short cutscene will play of the player sliding down a hill." },
  { text = "Talk to the Agility Instructor to leave the gate." },
  { text = "Leave the outpost and talk to Larry on the iceberg (twice)." },
  {
    text = "Enter the penguin suit and return to the outpost.",
    title = "Berg bards",
    neededItems = {
      ["Mahogany plank"] = { quantity = 1 },
      ["Leather"] = { quantity = 1 },
      ["Cowbell"] = {
        quantity = 1,
      },
    },
    recommendedItems = {},
  },
  { text = "Enter the second east room and talk to Ping and Pong." },
  { text = "Return to Larry and exit penguin suit." },
  { text = "Use a mahogany plank on the leather." },
  { text = "Enter penguin suit and return to Ping and Pong." },
  { text = "Talk to them again.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Operate the control panel in the north-west corner of the main hall." },
  { text = "Enter the War Room to the east." },
  { text = "Kill an Icelord and exit the pen.", title = "Conclusive evidence" },
  { text = "Climb the chasm to the east in the room." },
  { text = "Talk to Larry." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Cold War",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1170028800,
  prereqQuests = {},
})
