local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

---@type QuestStep[]
local steps = {
  { text = "Dismiss any followers now.", title = "Tracking" },
  {
    text = "Talk to Captain Rovin in Varrock Palace, on the 2nd floor[UK]3rd floor[US] of north-west tower.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Ooh, good, sounds like a quest.") },
  },
  {
    text = "[Accept Quest]<ul><li>If he will not give you the quest, make sure you have claimed kudos from Historian Minas at Varrock Museum.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Okay, let's get going.") },
  },
  {
    text = "Enter the graveyard then talk to Hartwin.",
    actions = { Action.ConversationHighlight:new("I want to talk about the mission.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Inspect the tree stump just south of the west graveyard entrance (just south of the arch) to uncover more footprints.",
  },
  {
    text = "Follow the footprints and continue searching bushes, trees, stumps, mushrooms, and bones to uncover more footprints.<ul><li>Search the small forest of dead trees south of the Graveyard of Shadows, northwest of the Chaos Temple for a Grubby key.</li></ul>",
  },
  { text = "The trail eventually leads to Simon near the Chaos Temple." },
  { text = "Run south to the trapdoor, unlock it using the Grubby key and climb down." },
  { text = "Run north and look over the balcony.", title = "Undead army" },
  { text = "Run west and take 3 bottles." },
  { text = "Kill 3 armoured zombies, using your bottles on red mist to fill them." },
  { text = "Proceed deeper into the cave through the door." },
  { text = "Run north and go through the next door and look over another balcony." },
  { text = "Hartwin will give you a Varrock teleport tablet to return to Varrock quickly." },
  { text = "Return to Captain Rovin in Varrock." },
  {
    text = "Talk to Thurgo in Thurgo's Peninsula, south of Port Sarim lodestone, north of fairy ring AIQ.  or  if you have 99 Smithing.<ul><li>If you don't have a blurite ore, now is the opportune time to mine one in the snowy area of the dungeon nearby.</li></ul>",
    title = "The Sacred Forge",
    neededItems = { ["Blurite ore"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I'd like to ask about the Shield of Arrav."),
      Action.ConversationHighlight:new("I'd like to ask about the Shield of Arrav."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " I have heard of the artefact of which you speak. I believe it currently resides in the city of Varrock."
      ),
    },
  },
  { text = "Climb Ice Mountain, west of Edgeville." },
  { text = "Dig with your spade 2 steps east of the white tree (next to the material cache)." },
  { text = "Dig the churned-up snow then enter the hole." },
  {
    text = "Talk to Ramarno.  If you have a redberry pie, you can give it to Ramarno for 1,000 Smithing experience.",
    actions = { Action.ConversationHighlight:new("I wish to use the Sacred Forge.") },
  },
  { text = "Use blurite ore on the forge." },
  { text = "Talk to Ramarno for a cutscene (note that pressing escape during the cutscene will reset it)." },
  {
    text = "Return to Captain Rovin, ignoring the zombies. He gives you a restored shield.",
    title = "Zombie invasion",
  },
  {
    text = "Talk to Reldo in the library on the ground floor[UK] 1st floor[US]: 'I'd like to talk about the invading zombies.'",
  },
  { text = "Read the Varrock Census on the lectern." },
  { text = "Search the scrolls on the floor a few steps south after talking to Reldo about the situation." },
  { text = "Read the list of elders." },
  {
    text = "Talk to the following NPCs until one mentions they are not a blood descendant:<ul><li>King Roald, southeast corner of the ground floor[UK]1st floor[US]  or  if the quest Lord of Vampyrium has been completed.</li><li>Aeonisig Raispher, next to King Roald.</li><li>Sir Prysin, west of the king.</li><li>Curator Haig Halen, in the Varrock Museum.</li><li>Horvik the smith north-east of Varrock Square.</li></ul>",
    title = "Shield of Arrav",
    actions = {
      Action.ConversationHighlight:new("Talk about Zombies."),
      Action.ConversationHighlight:new("Talk about Zombies."),
      Action.ConversationHighlight:new("Defender of Varrock."),
      Action.ConversationHighlight:new("Talk about the zombie invasion."),
      Action.ConversationHighlight:new("Talk about the zombie invasion."),
      Action.ConversationHighlight:new("Talk about the zombie invasion."),
    },
  },
  {
    text = "Talk to Dimintheis in south-east Varrock where you started the quest Family Crest.",
    actions = { Action.ConversationHighlight:new("Ask about something else.") },
  },
  { text = "Talk to Captain Rovin." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Defender of Varrock",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1222646400,
  prereqQuests = { "Shield of Arrav", "The Knight's Sword", "Family Crest", "What Lies Below", "Kudos" },
})
