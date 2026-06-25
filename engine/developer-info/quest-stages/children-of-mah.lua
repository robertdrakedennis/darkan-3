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
    text = "Talk to Kharshai beneath the helmet shop in Rellekka (requires completion of the Koschei's Troubles miniquest) or all the way north of Rellekka in the Rellekka Hunter area, next to the Arctic (azure) habitat mine.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("I want to talk about Children of Mah.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("I'm ready to go to the ritual site."),
      Action.ConversationHighlight:new("I want to talk about Children of Mah."),
      Action.ConversationHighlight:new("Yes, I want to leave."),
    },
  },
  {
    text = "Go to the Mahjarrat Ritual Site via:<ul><li>Kharshai at the quest start location can teleport you there.</li><li>If you have completed Ritual of the Mahjarrat you can use:</li><li>DKQ, exit the Glacor Cave, and then go north-west.</li><li>Use the canoe in the snowy hunter area north of Rellekka (requires partial completion of the Tale of the Muspah).</li><li>DKQ, exit the Glacor Cave, and then go north-west.</li><li>Use the canoe in the snowy hunter area north of Rellekka (requires partial completion of the Tale of the Muspah).</li></ul>",
    title = "Ritual site",
  },
  { text = "Investigate the ritual marker to summon Kharshai" },
  { text = "Talk to Kharshai with a free inventory slot to receive the charged engrammeter." },
  {
    text = "Investigate the ritual marker to leave, or teleport to Varrock Dig Site using a Dig Site pendant or Archaeology journal.",
  },
  { text = "Go to the entrance of the throne room of Senntisten on the east side of the Varrock Dig Site." },
  { text = "Harvest the memory spring near the entrance of the Zarosian throne room." },
  {
    text = "Enter the throne room and harvest the 4 other memory springs inside. There are two on each side of the mine cart track (near the stairs).",
  },
  {
    text = "Return to the ritual site and investigate the ritual marker. (If you have not completed Ritual of the Mahjarrat, then return to Kharsai at the quest starting point to teleport back.)",
  },
  { text = "Continue Azzanadra's dialogue.", actions = { Action.ConversationHighlight:new("Yes, let's go!") } },
  {
    text = "Talk to Perjour just to your south.",
    title = "Inside Kharsai's memories",
    actions = {
      Action.ConversationHighlight:new("Could I see that book of yours?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Talk to Zebub a little further south.", actions = { Action.ConversationHighlight:new("Say goodbye.") } },
  { text = "Inspect the broken mouthpiece on the pillar next to Zebub.", title = "Northern platform" },
  {
    text = "Talk to Duke Sucellus to the north.",
    actions = {
      Action.ConversationHighlight:new("What has happened so far?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Talk to Jhallan just to the east.", actions = { Action.ConversationHighlight:new("Say goodbye.") } },
  { text = "Inspect the wall marking on the pillar next to Jhallan." },
  { text = "Inspect the broken enchantment down the first flight of stairs." },
  {
    text = "Talk to Pernix.",
    actions = {
      Action.ConversationHighlight:new("An inquisitor was sent for."),
      Action.ConversationHighlight:new("A red pair of horns."),
      Action.ConversationHighlight:new("There is a plot to overthrow Zaros."),
      Action.ConversationHighlight:new("Say goodbye."),
    },
  },
  {
    text = "Head down a flight of stairs and talk to Akthanakos.",
    actions = {
      Action.ConversationHighlight:new("Mah."),
      Action.ConversationHighlight:new("Icthlarin."),
      Action.ConversationHighlight:new("A camel."),
      Action.ConversationHighlight:new("Say goodbye."),
    },
  },
  {
    text = "Head back up and then down the staircase to the south. Talk to the warpriest.",
    title = "Southern platform",
    actions = { Action.ConversationHighlight:new("Use 'Inquisitorial Truth Serem'.") },
  },
  { text = "Inspect the stashed weapons south of the stairway. They are partially hidden under the stairway." },
  {
    text = "Inspect the wall marking on the north-eastern pillar. Rotate your camera to face south-east if having trouble seeing the marking.",
  },
  {
    text = "Talk to Sliske.",
    actions = {
      Action.ConversationHighlight:new("Do you have any suspects?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Talk to Azzanadra.", title = "Centre", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to the 3 suspects.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to either Azzanadra or Zamorak to choose a side." },
  { text = "Watch or skip the cutscene." },
  {
    text = "Go through the dialogue at the Ritual marker.<ul><li>If the dialogue is interrupted, talk to Zamorak.</li></ul>",
    title = "After the memory",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  { text = "Inspect the broken enchantment down the first flight of stairs." },
  {
    text = "Talk to Pernix.",
    actions = {
      Action.ConversationHighlight:new("An inquisitor was sent for."),
      Action.ConversationHighlight:new("A red pair of horns."),
      Action.ConversationHighlight:new("There is a plot to overthrow Zaros."),
      Action.ConversationHighlight:new("Say goodbye."),
    },
  },
  {
    text = "Head down a flight of stairs and talk to Akthanakos.",
    actions = {
      Action.ConversationHighlight:new("Mah."),
      Action.ConversationHighlight:new("Icthlarin."),
      Action.ConversationHighlight:new("A camel."),
      Action.ConversationHighlight:new("Say goodbye."),
    },
  },
  {
    text = "Head back up and then down the staircase to the south. Talk to the warpriest.",
    actions = { Action.ConversationHighlight:new("Use 'Inquisitorial Truth Serem'.") },
  },
  { text = "Inspect the stashed weapons south of the stairway. They are partially hidden under the stairway." },
  {
    text = "Inspect the wall marking on the north-eastern pillar. Rotate your camera to face south-east if having trouble seeing the marking.",
  },
  {
    text = "Talk to Sliske.",
    actions = {
      Action.ConversationHighlight:new("Do you have any suspects?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Talk to Azzanadra.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to the 3 suspects.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to either Azzanadra or Zamorak to choose a side." },
  { text = "Watch or skip the cutscene." },
  {
    text = "Go through the dialogue at the Ritual marker.<ul><li>If the dialogue is interrupted, talk to Zamorak.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  {
    text = "Head back up and then down the staircase to the south. Talk to the warpriest.",
    actions = { Action.ConversationHighlight:new("Use 'Inquisitorial Truth Serem'.") },
  },
  { text = "Inspect the stashed weapons south of the stairway. They are partially hidden under the stairway." },
  {
    text = "Inspect the wall marking on the north-eastern pillar. Rotate your camera to face south-east if having trouble seeing the marking.",
  },
  {
    text = "Talk to Sliske.",
    actions = {
      Action.ConversationHighlight:new("Do you have any suspects?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Talk to Azzanadra.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to the 3 suspects.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to either Azzanadra or Zamorak to choose a side." },
  { text = "Watch or skip the cutscene." },
  {
    text = "Go through the dialogue at the Ritual marker.<ul><li>If the dialogue is interrupted, talk to Zamorak.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  { text = "Talk to Azzanadra.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to the 3 suspects.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to either Azzanadra or Zamorak to choose a side." },
  { text = "Watch or skip the cutscene." },
  {
    text = "Go through the dialogue at the Ritual marker.<ul><li>If the dialogue is interrupted, talk to Zamorak.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  {
    text = "Go through the dialogue at the Ritual marker.<ul><li>If the dialogue is interrupted, talk to Zamorak.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  {
    text = "Go to the World Gate, dial Freneskae, and enter it (left click, select 'Children of Mah').",
    title = "Pilgrimage to the ritual site",
    actions = { Action.ConversationHighlight:new("Children of Mah.") },
  },
  { text = "Optionally inspect the four Mahjarrat deathstones along the route for a master quest cape requirement." },
  { text = "Talk to Kharshai." },
  {
    text = "Unequip weapon(s) and jump on the rock. The first deathstone is before the first lava ride.<ul><li>Avoid taking damage using the keyboard or on-screen arrows.</li><li>The mini-map can help you predict upcoming obstacles.</li><li>The off-hand Enhanced Excalibur can be activated prior to departure, and will heal you during the ride.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Equip weapon(s) and attack muspahs until a grand force muspah appears." },
  {
    text = "Stand next to the interactable wall and attack it until its adrenaline bar fills and it destroys the wall with its special attack.",
  },
  { text = "Repeat in next area. (Note: There is a checkpoint right before the second river of lava)" },
  {
    text = "Heal up, un-equip weapon(s), and jump on the rock. The second deathstone is before the second lava ride.<ul><li>Avoid taking damage using the keyboard or on-screen arrows as before.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Seren. The third deathstone is to the east of Seren.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Continue...]") },
  },
  { text = "Enter the cave. The final deathstone is right before meeting with the mahjarrat." },
  {
    text = "Go west and talk to Zemouregal.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Continue...]"),
      Action.ConversationHighlight:new("[Continue...]"),
      Action.ConversationHighlight:new("Stay silent."),
    },
  },
  { text = "Watch or skip the cutscene." },
  { text = "Speak with Zaros." },
  {
    text = "Go up the northern path and talk to Seren at the end.",
    title = "Mah's nightmares",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Investigate the core of Mah." },
  {
    text = "Intercept the shadows heading towards the core from the muspahs.<ul><li>Insert the glowing orb into the light shard between the core and the shadow heading towards it. The orb must be retrieved from the light shard it's in before it can be inserted into another.</li></ul>",
  },
  {
    text = "Navigate the area by going from one circle of light to another.<ul><li>Touch the light shard at the end of the path.</li><li>Go back half way and exit on Zemouregal.</li></ul>",
  },
  {
    text = "Investigate the core and intercept the shadows as before. Two orbs will be given this time. Failing three times will result in the option to toggle an easier mode.",
  },
  {
    text = "Navigate the area using the circles of light as before.<ul><li>Touch the western light shard.</li><li>Touch the eastern light shard.</li><li>Touch the central light shard.</li><li>Go south and exit on Seren.</li></ul>",
  },
  {
    text = "Investigate the core and intercept the shadows as before. Three orbs will be given this time. If you're having issues completing this round, after three failed attempts, there will be an option for easy mode.",
  },
  { text = "Talk to Seren.", title = "Finishing up", actions = { Action.ConversationHighlight:new("[Continue...]") } },
  {
    text = "Go down the path and talk to Seren at the ritual site.",
    actions = { Action.ConversationHighlight:new("[Continue...]") },
  },
  { text = "Go back up the path and talk to Zaros.", actions = { Action.ConversationHighlight:new("[Continue...]") } },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Children of Mah",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1479686400,
  prereqQuests = {
    "The Light Within",
    "Dishonour among Thieves",
    "Koschei's Troubles",
    "Ritual of the Mahjarrat",
  },
})
