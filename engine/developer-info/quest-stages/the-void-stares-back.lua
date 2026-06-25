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
    text = "Talk to Korasi in Falador Park.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Go to the entrance of the Black Knights' Fortress north-west of the Edgeville Monastery.",
    title = "Infiltration",
    neededItems = {
      ["Limestone brick"] = { quantity = 1 },
      ["Hard leather"] = { quantity = 1 },
      ["Black full helm (bugged)"] = { quantity = 1 },
      ["Black platebody (bugged)"] = { quantity = 1 },
      ["Black platelegs (bugged)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Equip the black full helm (bugged), black platebody (bugged), and black platelegs (bugged). Open the portcullis. Commander Colby will yell at you and teleport you.<ul><li>Once you finally get to the storage room, your helmet will be taken.</li></ul>",
  },
  {
    text = "If you leave the room at any point (including disconnect), or once your helmet is taken, you will no longer be able to use the Black Knights' Fortress to return to the storage area.<ul><li>Instead, enter via the Black Knights' Base inside the Taverley Dungeon.</li><li>Inside the Black Knights' Base, enter the door south from the conquest map table, which found in the middle of the floor.</li></ul>",
  },
  { text = "Talk to Captain Gilroy." },
  { text = "Attempt to take an item from any crate." },
  { text = "Speak to Gilroy.", actions = { Action.ConversationHighlight:new("I think I've got it, sir.") } },
  {
    text = "Talk to Gilroy again. .",
    actions = {
      Action.ConversationHighlight:new("actually weigh 4"),
      Action.ConversationHighlight:new("are of weight 2"),
      Action.ConversationHighlight:new("should be labelled 6"),
      Action.ConversationHighlight:new("really weigh 3"),
      Action.ConversationHighlight:new("need to be marked 5"),
    },
  },
  {
    text = "Balance the scale on the wall to the south, arrange the weights to make 15 on each side. (Top: 3-5-3-4 and Bottom: 2-2-5-6)",
  },
  { text = "Proceed to the next room." },
  { text = "Kill the enemies in the room and pick up the security blocks that are dropped." },
  { text = "Talk to Gilroy.", actions = { Action.ConversationHighlight:new("What do we do now?") } },
  {
    text = "Exit the room and head to the jail, northeast of the storage room, and search the crate for a piece of hard leather.",
  },
  { text = "Search a crate in the kitchen for some logs, just north of the jail." },
  { text = "Head to the tiny forge on the west side of the room (icon on minimap)." },
  { text = "Search the crate in the south-west corner of the forge room for a limestone brick." },
  { text = "Return to the forge and repair it." },
  { text = "Use the logs on the forge to fuel it." },
  { text = "Light the forge." },
  { text = "Use the broken security block on the forge to repair it." },
  { text = "Go back into the room with Gilroy and place all the blocks within the western wall." },
  { text = "Proceed to the next room." },
  {
    text = "Click on the panel on the western wall.",
    actions = { Action.ConversationHighlight:new("Search the panels for magical traps.") },
  },
  { text = "Open the panel." },
  {
    text = "Drag the tiles into the panels so that a horizontal line is going across all three panels, while filling the empty spaces. Press confirm once completed.",
  },
  { text = "Proceed to the next room." },
  {
    text = "Reveal yourself and explain the danger to Lord Daquarius. Accept the allegiance.",
    title = "A little game",
    actions = {
      Action.ConversationHighlight:new("Yes, sir."),
      Action.ConversationHighlight:new("Explain the danger in calm, logical terms."),
      Action.ConversationHighlight:new("Okay, I'll do it."),
    },
  },
  { text = "Talk to Korasi." },
  {
    text = "Talk to Commodore Tyr.<ul><li>At this point you can equip your own armour again instead of the Black Knight (bugged) armour.</li></ul>",
    actions = { Action.ConversationHighlight:new("I'm ready.") },
  },
  {
    text = "Enter the portal and watch the cutscene.  Make sure you go through each dialogue box, otherwise it will kick you out of the portal and you will have to start the cutscene from the start.",
    actions = { Action.ConversationHighlight:new("You bet!") },
  },
  {
    text = "A game of modified conquest must be played and won.<ul><li>Spinners are very powerful but do not heal like in Pest Control.</li><li>Ravagers have the movement range of Scouts and the damage rate of a Knight.</li><li>Shifters are less of a threat than other units and can be tanked if need be.</li><li>Torchers are similar to Mages.</li><li>Defilers are similar to Archers.</li><li>Splatters will explode and deal 100 life points of damage to any adjacent unit that is within 1 square.</li></ul>",
  },
  { text = "Win the game and proceed to the next room." },
  { text = "Sacrifice either Korasi or Jessika.", title = "The Pest Queen" },
  { text = "Speak to Valluta before stocking for the fight or Sir Tiffy won't teleport you back." },
  {
    text = "Make all necessary preparations to fight the Pest Queen. Korasi's sword will be the primary weapon during the fight and its Special attacks will be used a lot.<ul><li>It may be helpful to use manual attack abilities rather than Revolution.</li><li>You can use necromancy to trivialise the encounter. Summon conjures prior to equipping Korasi's sword, and keep your necromancy offhand equipped. Ghost will provide full sustain without food, and skeleton will do the majority of the damage.</li></ul>",
  },
  {
    text = "Kill the Pest Queen.<ul><li>The Weapon Special attack is located under Constitution abilities.</li><li>If you die, talk to Sir Tiffy in Falador park and he will teleport you back to the portal. You will lose the weapon if you die. Reclaim this from Jessika/Korasi before you attack the Pest Queen again.</li><li>Build your adrenaline up more than 60% then use the special attack 'Disrupt' to stun the Pest Queen when she raises her head and starts shaking for several seconds.</li><li>Hurt but don't kill the spawning Elite defilers to defend Void Knight archers. They need to be focussed on you so more don't respawn and so they don't hurt your archers.</li><li>More in-depth strategies can be found here.</li></ul>",
  },
  {
    text = "Talk to Korasi or Jessika. Decide who to let deal with Wizard Grayzag.",
    title = "Finishing up",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to Commodore Tyr." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Void Stares Back",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1286323200,
  prereqQuests = { "A Void Dance", "Conquest" },
})
