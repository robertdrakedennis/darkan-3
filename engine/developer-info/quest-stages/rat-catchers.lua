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
    text = "Talk to Gertrude in west edge of Varrock.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about the 'Ratcatchers' quest.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about the 'Ratcatchers' quest.") },
  },
  {
    text = "[Accept Quest]<ul><li>If you have a Hellcat out</li><li>[Accept Quest]</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  { text = "Take out your cat or kitten." },
  {
    text = "Talk to Phingspet and Grimesquit in the Varrock Sewers right next to the eastern entrance of Varrock Palace (down the manhole).",
  },
  {
    text = "Catch 8 rats with your cat/kitten by choosing the 'interact-with' option<ul><li>The 'Hide familiar options' setting must be off in Gameplay settings</li></ul>",
    actions = { Action.ConversationHighlight:new("Chase-Vermin.") },
  },
  { text = "Talk to them again to obtain a Rat pole." },
  {
    text = "Talk to Jimmy Dazzler in the house directly west of the Flying Horse Inn in East Ardougne, north of the castle. Ensure you finish the dialogue, or you will not be able to follow the next step.",
    title = "Just the ticket",
  },
  {
    text = "Read the 'directions' scroll he gives you.",
    actions = { Action.ConversationHighlight:new("Follow the directions to the house.") },
  },
  {
    text = "Avoid guards whilst traversing throughout. You can use Surge or Dive to navigate quickly.",
    title = "Breaking in",
  },
  { text = "Hide at the hedges directly west of the mansion when the southwest guard walks toward the bridge." },
  { text = "Run to the north-west corner when the guard is behind the hedges." },
  { text = "Run to the hedges directly north of the mansion." },
  { text = "Climb up the nearby trellis." },
  { text = "Once inside continue to avoid guards." },
  { text = "Wield your rat pole." },
  { text = "Catch 1 rat with your cat in the north-west room." },
  { text = "Catch 2 rats with your cat in the south-east room." },
  { text = "Go down the ladder in this room." },
  {
    text = "Catch 2 rats with your cat in this room, then 1 in the room to the north.<ul><li>There is a chance that a rat will spawn west of the ladder room instead of to the north.</li></ul>",
  },
  { text = "Leave the area by using your home teleport to Ardougne" },
  { text = "Talk to Jimmy Dazzler.<ul><li>You can destroy your directions scroll now.</li></ul>" },
  {
    text = "Talk to Hooknosed Jack in south-east Varrock, inside the fenced area past the guard. Talk to him again, if you're making the Rat poison and don't have the ingredients in your inventory on the first talk.",
    title = "Hooknosed Jack",
  },
  { text = "Enter the warehouse directly south of the pub  and climb-up the ladder." },
  { text = "Use rat poison on all 4 pieces of cheese." },
  { text = "Use poisoned cheese on all of the rat holes." },
  { text = "Return to Jack.", actions = { Action.ConversationHighlight:new("Can I help?") } },
  {
    text = "Talk to the Apothecary in western Varrock for cat antipoison.",
    actions = { Action.ConversationHighlight:new("I need to talk to you about cats.") },
  },
  { text = "Return to Jack. Finish the dialogue or you will not be able to progress." },
  { text = "Go back to the warehouse and up the ladder." },
  {
    text = "Pick up your cat and use it on the 'hole in wall' (not the rat holes). Make sure you finish the dialogue to start the fight, green health bars appear.",
  },
  {
    text = "Keep using fish on the 'hole in wall' (not on the cat) to restore the cat's health until the king rat dies.<ul><li>If you lose your cat by going down the ladder,  logging out and logging back in should return it to you.</li><li>If you are in a group or have lootshare active, then the kill will not register and you will need to go down the ladder to reset the king rat.</li></ul>",
  },
  { text = "Retrieve your cat by calling your follower." },
  { text = "Return to Jack to tell him the king rat is dead." },
  {
    text = "Talk to Smokin' Joe in east Keldagrim, just east of the mining shop.<ul><li>If this is your first time entering Keldagrim proceed with the following route:</li><li>Enter the tunnel entrance north-east of Rellekka.</li><li>Immediately go-through the cave entrance with the two dwarven statues next to it.</li><li>Talk to the Dwarven Boatman and get him to take you into Keldagrim, this might cause you to automatically start the quest The Giant Dwarf.</li><li>Proceed to the east part of Keldagrim and talk with Smokin' Joe.</li><li>Enter the tunnel entrance north-east of Rellekka.</li><li>Immediately go-through the cave entrance with the two dwarven statues next to it.</li><li>Talk to the Dwarven Boatman and get him to take you into Keldagrim, this might cause you to automatically start the quest The Giant Dwarf.</li><li>Proceed to the east part of Keldagrim and talk with Smokin' Joe.</li></ul>",
    title = "Smokin' Joe",
    actions = { Action.ConversationHighlight:new("I could help you.") },
  },
  { text = "Use some weeds on an empty pot and light it." },
  { text = "Use the smouldering pot on the rat hole near the crates." },
  {
    text = "Use the pot on the same hole again with your cat out and your catspeak amulet on, your cat will offer to help.<ul><li>If your cat doesn't automatically initiate a conversation with you, you may have to attempt this a third time.</li></ul>",
  },
  { text = "Talk to Joe again.<ul><li>You can drop the smouldering pot now.</li></ul>" },
  { text = "Talk to Felkrash, north of Port Sarim lodestone.", title = "The Face and the Felkrash" },
  { text = "Talk to The Face nearby." },
  { text = "Head south of the bar in Pollnivneach." },
  {
    text = "Interact with the money pot next to Badir the Snake Charmer  and then complete the conversation.  (unless wearing a ring of charos).<ul><li>He will give you a music scroll.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("I want to talk to you about animal charming."),
      Action.ConversationHighlight:new("What if I offered you some money?"),
      Action.ConversationHighlight:new("Forget about it. I don't care."),
      Action.ConversationHighlight:new("Walk away slowly"),
      Action.ConversationHighlight:new("Stop"),
    },
  },
  { text = "If you don't have a snake charm, claim another one at this point." },
  { text = "Go back to Felkrash in Port Sarim." },
  {
    text = "Play the snake charm. Only 1 note can be set per page.<ul><li>Go through the pages and click the correct notes (one note per page). The red dots in the music scroll correspond to the black dots on the flutes.</li><li>On page 5, click the <) icon in the top left before you play the note. You can go back and make corrections to the pages if needed.</li><li>Click play once all the notes are correctly selected.</li><li>There will be short cutscene upon completing correctly.</li></ul>",
  },
  { text = "Talk to Felkrash.<ul><li>You can destroy your music scroll now.</li></ul>" },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Rat Catchers",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1133136000,
  prereqQuests = { "Icthlarin's Little Helper" },
})
