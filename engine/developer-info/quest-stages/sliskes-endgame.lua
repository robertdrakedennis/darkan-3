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
    text = "Speak to Relomia in Draynor Village.",
    title = "Invitations",
    neededItems = { ["Nomad"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Sliske's Endgame."),
      Action.ConversationHighlight:new("Where are the gods?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Speak to each of the gods.<ul><li>Speak to Seren and Zaros up in the Tower of Voices by using the stairs north-east of the Prifddinas lodestone.</li><li>Speak to Saradomin in the throne room on the east side of the 2nd floor[UK]3rd floor[US] of White Knights' Castle. He will not speak to you if you are holding the Skull of Remembrance or have the 'Of Zamorak' title active (Lobbying may be necessary if he only insults you).</li><li>Speak to Armadyl on the roof of his tower south-east of the Clan Camp.</li><li>Speak to Zamorak on the 1st floor[UK]2nd floor[US] of Black Knights' Fortress or at Zamorak's hideout (Use the wicked hood to Mind altar, a communication device, or a Skull of Remembrance).</li><li>Speak to Icthlarin in Death's office by entering through portal in Draynor Village (War's Retreat will not work).</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Speak with Seren about Sliske's Endgame."),
      Action.ConversationHighlight:new("Speak to Death about Sliske's Endgame"),
    },
  },
  {
    text = "Return to Relomia.",
    actions = { Action.ConversationHighlight:new("Sliske's Endgame") },
    postconditions = {
      Condition.ConversationText:new(" He has left this world for the moment, seeking something beyond the stars."),
    },
  },
  { text = "Dismiss any familiar or follower you may have following you." },
  {
    text = "Speak to Relomia in the Heart of Gielinor campsite (west of the entrance, outside of the Heart of Gielinor).",
    actions = {
      Action.ConversationHighlight:new("Yes, I am prepared."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Defeat Nomad. He uses melee/mage and doesn't use special attacks." },
  { text = "Run through both the mazes to the exit on the north-east. The solutions are given below.", title = "Maze" },
  {
    text = "The green lines show the main path to follow, and the blue lines lead to puzzle rooms off the main map that must be completed.<ul><li>See the puzzles section below with help solving the puzzles.</li><li>Return to the green paths after completing the puzzles.</li></ul>",
  },
  { text = "The orange lines lead to optional cutscenes." },
  {
    text = "These maps and your minimap may not line up exactly until the puzzle rooms have been completed as completing puzzle rooms rotates parts of the labyrinth.",
  },
  { text = "Click the Solution panel to view the Clue", title = "Rotation puzzles" },
  { text = "Use the table below to find the Solution" },
  {
    text = "Rotate the Shadow casters till they match the solution.<br><br><table><tbody><tr><th>Clue</th><th>Solution</th></tr><tr><td>Misery loves company.</td><td>All four casters need sad faces</td></tr><tr><td>You and I are beautifully broken. The others resent our superiority</td><td>Sad faces on the south casters and broken faces on the north casters.</td></tr><tr><td>When the sun rises, we are happy. When the sun sets, we are sad.</td><td>Happy faces on both of the east casters, sad faces on the west casters.</td></tr><tr><td>I'm bored of helping you, you can figure this one out on your own</td><td>North-west: Neutral - north-east: Happy - south-east: Sad - south-west: Broken</td></tr><tr><td>I'm not a morning person; nor am I a mourning person</td><td>Sad faces on the eastern wall, leave the western two without masks.</td></tr><tr><td>Never let anyone see how you truly feel, they will use it against you</td><td>All neutral expressions on all four casters</td></tr></tbody></table>",
  },
  {
    text = "Kill any wights in the room to obtain missing Masks (they will drop a Mask of Glee, Mask of Sorrow or Mask of Indifference)",
    title = "Projection puzzles",
  },
  { text = "Click the Viewing panel to begin the projection mode" },
  { text = "If it says one of the projectors are damaged, repair them" },
  { text = "Rotate the Shadow casters till it forms the solution image shown on the right (a complete Sliske mask)" },
  {
    text = "Click the solution panel to view the Clue, then view the Solution from the table in the section above",
    title = "Mask replacement puzzles",
  },
  {
    text = "Kill any wights in the room to obtain missing Masks (they will drop a Mask of Glee, Mask of Sorrow or Mask of Indifference)",
  },
  {
    text = "Take the mask from all the casters that don't match the description, and re-attach the mask on another caster till it matches",
  },
  { text = "When all the casters are matching the solution, the puzzle will be solved" },
  { text = "Stand in the spotlight", title = "Spotlight puzzles" },
  { text = "At the end of the maze, ascend the stairs", title = "Reaching the stone" },
  {
    text = "Participate in the race and go through the dialogue and cutscenes",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Use the purple exit portal to leave, and prepare for a tough fight with the best combat gear you have.<ul><li>Tier 90 armour like masterwork equipment greatly helps.</li><li>Bring a beast of burden with high healing foods.</li></ul>",
    title = "Fight",
  },
  { text = "Return to Relomia using The Heart teleports and resume the battle against Sliske" },
  {
    text = "The fight is divided into 3 phases, at the end of each phase you can bank and return to resume from that checkpoint",
  },
  {
    text = "Fight against wights of increasing difficulty, kill enough of them to increase the progress bar on top.<ul><li>Use Deflect Melee or Deflect Ranged depending on the types of wights you are fighting</li><li>Be aware that all remaining unstable wight footsoldiers will explode at the end of the wave, potentially causing death if the player is surrounded but you will continue onto the next wave when you return.</li></ul>",
    title = "Phase 1",
  },
  {
    text = "Kill Nomad.<ul><li>Use Deflect Ranged while fighting him</li><li>Nomad plants mines and Gregorovic rains damage on the arena. Make sure to walk off of marked tiles.</li></ul>",
    title = "Phase 2",
  },
  {
    text = "Kill Linza the Disgraced.<ul><li>Use Deflect Melee while fighting her</li><li>Linza reflects damage which is less deadly after she's the only surviving boss.</li><li>She also has a blue charged bar that appears above her head and does a very high damage move. She will also bind before she does this move so have surge or freedom ready. It is recommended to dodge this attack.</li></ul>",
  },
  {
    text = "Kill Gregorovic.<ul><li>Use Deflect Ranged while fighting him</li><li>The spirits that spawn towards Gregorovic will heal him if they reach him. Kill them before they touch him.</li></ul>",
  },
  {
    text = "Kill Sliske. You cannot use Deathtouched darts to kill Sliske.<ul><li>Use Deflect Magic throughout the fight as the majority of the damage will be magic classed. Keep in mind that prayer points are drained faster than normal in this fight.</li><li>If you are having trouble, wearing the Armour of Trials and wielding Vanquish can help greatly has it can provide a 25% defensive and offensive bonus during the fight if you die 5 times in a row.</li><li>Beware of his special attack where several shadow pits will appear below your feet. Step away to avoid significant damage. He will summon 4 shadow pits in the first round, 5 in the second, and 4 in the subsequent two rounds. On rounds 3 and 4, Sliske's attacks will predict where you are walking towards on the 4th and final shadow pit.</li><li>Attack Sliske until he reaches 10,000 health, he will teleport away.</li><li>Destroy the Power Source in the centre of the room.</li><li>Attack Sliske until he reaches 10,000 health again.</li><li>Enter one of the rifts. Sliske will bring you back into the arena.</li><li>Attack Sliske until he reaches 10,000 health again.</li><li>Jump on the rock formation in front of Sliske.</li><li>Attack Sliske until he reaches 10,000 health again.</li><li>Defeat Sliske one final time. You have unlimited life points and adrenaline and all ability cooldowns are halved. (If you have Onslaught Ability you can kill him automatically)</li></ul>",
    title = "Phase 3",
  },
  {
    text = "Talk to Jas.  (Except insulting Jas - does not allow you to continue)",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Talk to the gods on the surface.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Return to Relomia in Draynor.",
    actions = {
      Action.ConversationHighlight:new("Sliske's Endgame."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Sliske's Endgame",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1482105600,
  prereqQuests = {
    "The Death of Chivalry",
    "One of a Kind",
    "Nomad's Elegy",
    "Kindred Spirits",
    "Hero's Welcome",
    "Children of Mah",
  },
})
