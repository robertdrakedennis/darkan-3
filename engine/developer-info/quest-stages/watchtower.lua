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
    text = "Make your way to the watchtower north-east of the Yanille lodestone.",
    title = "Starting out",
  },
  {
    text = "Climb up the trellis outside on this building's northern wall, then take the ladder to the top floor of the Watchtower.",
  },
  {
    text = "Talk to the Watchtower Wizard on the top floor of the Watchtower.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("What’s the matter?"),
      Action.ConversationHighlight:new("So how come the spell doesn’t work?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Climb down the ladders to return to the bottom floor.", title = "The pawns" },
  { text = "Search the northwesternmost bush just outside the watchtower to find fingernails." },
  {
    text = "Return to the Watchtower Wizard.",
    actions = {
      Action.ConversationHighlight:new("What do you suggest I do?"),
      Action.ConversationHighlight:new("So what do I do?"),
    },
  },
  {
    text = "Talk to Og, an ogre north-west of the Yanille lodestone.",
    title = "The kings",
    neededItems = {
      ["Rope"] = { quantity = 2 },
      ["Death rune"] = { quantity = 1 },
      ["Dragon bones"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("I seek entrance to the city of ogres.") },
  },
  { text = "Walk south toward the lake's west side, use rope on the branch of a long-branched tree to swing over." },
  { text = "Talk to Grew.", actions = { Action.ConversationHighlight:new("Don't eat me; I can help you.") } },
  { text = "Pick up some jangerberries dropped on Grew's island." },
  {
    text = "Head to and enter the cave entrance marked on the world map (Show map) southwest of Gu'Tanoth and southeast of Jiggig, and you will arrive at the island east of Gu'Tanoth.<ul><li>If you didn't bring a death rune, one spawns directly south of the tunnel entrance.</li><li>Leave the island and head to south-east to the passage between Gu'Tanoth and the island south of Yanille, then head to south-west to the cave entrance.</li><li>You can use fairy ring AKS and head north-west to reach there.</li><li>If A Fairy Tale III - Battle at Ork's Rift is completed, you can use fairy ring ALP, head north to exit the tunnel, and head south-east for quicker access.</li><li>If The Grand Tree quest is completed and partially completed the quest One Small Favour, you can use gnome glider to fly to the Feldip Hills  and head north-west for quicker access.</li></ul>",
    actions = { Action.ConversationHighlight:new("Lemantolly Undri") },
  },
  {
    text = "Talk to Toban twice, he will take the dragon bones and give you relic part 3.<ul><li>If you didn't bring a second rope, take one now from the island.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I seek entrance to the city of ogres."),
      Action.ConversationHighlight:new("I could do something for you…"),
    },
  },
  { text = "Kill Gorad and take the ogre tooth." },
  { text = "Open the Chest and take the gold." },
  { text = "Go back and talk to Grew, giving him the tooth and consuming the second rope." },
  { text = "If you didn't get the yellow crystal, talk to Grew again and ask for it." },
  { text = "Talk to Og, north of Grew's island to receive the relic part 1." },
  {
    text = "Talk to the Watchtower Wizard until you receive an Ogre relic. (This may require multiple left clicks).<ul><li>If you didn't bring a light source, take the lit candle from the ground floor[UK]1st floor[US] of Watchtower.</li></ul>",
    title = "Gu'Tanoth",
    neededItems = {
      ["Coins"] = { quantity = 20 },
      ["Ogre relic"] = { quantity = 1 },
      ["Death rune"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Enter Gu'Tanoth from the north-eastern corner and go west along the northern path." },
  {
    text = "Use the Ogre relic on one of the guards at the west gate. Do not talk to them or you will be hit for some damage and sent back near Copernicus.",
  },
  { text = "Head south-east to the Ogre trader marked with  on the map but don't talk to him." },
  { text = "Steal a rock cake from the counter." },
  { text = "Return to the path and continue south." },
  {
    text = "Talk to the guards , then give (but do not eat) a rock cake to be able to climb over the battlement.",
    actions = { Action.ConversationHighlight:new("But I am a friend to ogres...") },
  },
  {
    text = "Continue up the path and jump over the gap, paying 20 coins to the guards .",
    actions = { Action.ConversationHighlight:new("Okay, I'll pay it.") },
  },
  {
    text = "Talk to one of the City guard. Make sure to use the 'Talk-to' option on the guard. , then talk to them again and give them a death rune.",
    actions = { Action.ConversationHighlight:new("I seek passage into the skavid caves.") },
  },
  {
    text = "Enter the green-pinned east cave, marked 4, on the map. (See map next to(above on mobile) this text, click on the east green marker to reveal the cave number).",
    title = "The skavids",
    neededItems = {
      ["Skavid map"] = { quantity = 1 },
      ["Gold bar"] = { quantity = 1 },
      ["Light source"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Talk to the Scared skavid.",
    actions = { Action.ConversationHighlight:new("Okay, okay, I’m not going to hurt you.") },
  },
  { text = "Enter the green-pinned north cave, marked 1." },
  { text = "Talk to the skavid (respond with 'Ig')." },
  {
    text = "While in this cave, pick up two cave nightshades.<ul><li>Attempting to pick up a cave nightshade without gloves results in a dialogue 'You have been poisoned by the plant!', causing minor damage.</li><li>Attempting to pick up a cave nightshade with regular, non-armoured gloves results in the same dialogue as above and another suggesting to wear armoured gloves, 'Maybe you should wear armoured gloves instead'.</li></ul>",
  },
  { text = "Enter the grey-pinned north cave, marked 2.<ul><li>Talk to the skavid (respond with 'Ar').</li></ul>" },
  { text = "Enter the red-pinned north cave, marked 3.<ul><li>Talk to the skavid (respond with 'Cur').</li></ul>" },
  { text = "Enter the grey-pinned east cave, marked 5.<ul><li>Talk to the skavid (respond with 'Nod').</li></ul>" },
  { text = "Return to the green-pinned east cave, marked 4.<ul><li>Talk to the Scared skavid.</li></ul>" },
  { text = "Go to a gate south of the 4 cave entrance." },
  { text = "Talk to one of the ogre guards and he will take the gold bar and let you in." },
  { text = "Enter the red-pinned south cave, marked 6." },
  {
    text = "Talk to the Mad skavid to obtain the pink crystal.<ul><li>If he says 'Ar cur,' the correct response is 'Gor.'</li><li>If he says 'Bidith ig,' the correct response is 'Cur.'</li><li>If he says 'Gor nod,' the correct response is 'Tanath.'</li><li>If he says 'Cur tanath,' the correct response is 'Bidith.'</li></ul>",
  },
  {
    text = "Head back to Ogre trader marked with  on the map (Ogre marketplace).",
    title = "Invincible",
    neededItems = {
      ["Cave nightshade"] = { quantity = 2 },
      ["Guam potion (unfinished)"] = { quantity = 1 },
      ["Jangerberries"] = { quantity = 1 },
      ["Bat bones"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Use a cave nightshade on one of the enclave guards." },
  { text = "Enter the enclave while the guard is distracted." },
  {
    text = "After the cutscene, Home teleport to Yanille lodestone, head back to the Watchtower and talk to the Watchtower Wizard.",
  },
  { text = "Add jangerberries to guam potion (unfinished)" },
  { text = "Add the ground bat bones." },
  { text = "Talk to the Watchtower Wizard." },
  { text = "Return to the Ogre marketplace." },
  { text = "Use the second cave nightshade on an enclave guard to distract them and enter the enclave again." },
  {
    text = "Use the potion on the six ogre shamans in the enclave to kill them.<ul><li>If you attack a shaman, it will hit you with a very powerful Magic blow, dealing half your life points in damage.</li></ul>",
  },
  { text = "Mine the Rock of Dalgroth on the peninsula in the centre of the enclave." },
  { text = "Talk to the Watchtower Wizard.", title = "Back in action" },
  { text = "Use the pink crystal on the north-eastern pillar." },
  { text = "Use the green crystal on the south-eastern pillar." },
  { text = "Use the yellow crystal on the south-western pillar." },
  { text = "Use the blue crystal on the north-western pillar." },
  { text = "Pull the lever on the west side of the room." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Watchtower",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1052265600,
  prereqQuests = {},
})
