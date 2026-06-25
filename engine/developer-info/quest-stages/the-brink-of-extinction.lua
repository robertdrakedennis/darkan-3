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
    text = "Speak to TzHaar-Mej-Ak or TzHaar-Mej-Jeh in the main plaza of TzHaar City.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Run east through the arch. A cutscene will begin." },
  {
    text = "Run north-east to the Birthing Pool and speak to TzHaar-Mej-Het.",
    actions = {
      Action.ConversationHighlight:new("I require TokKul."),
      Action.ConversationHighlight:new("That's the TokKul I'm looking for!"),
    },
  },
  { text = "Run around the wall to where the Fight Kiln entrance is, and mine the Statue of TzHaar-Ket-Teg." },
  { text = "Run south-west to the TzHaar City entrance. Mine the nearby Statue of TzHaar-Xil-Kal." },
  {
    text = "Return to the main plaza. Melt the two stacks of fragments into TokKul with the furnace slightly south-east of the main plaza.",
  },
  {
    text = "Speak to TzHaar-Mej-Ak or TzHaar-Mej-Jeh again.",
    actions = {
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  {
    text = "Go to the entrance to the Library, north-west of the main plaza and speak to Ga'al.",
    title = "The Ga'al",
    neededItems = { ["Communication orb"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Run south until you reach the cooking icon on the minimap. Speak to the next Ga'al.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Continue south along the eastern wall to the southwest corner of the city (east of the  TzHaar City mine), and speak to the final Ga'al.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Return to the main plaza and speak to TzHaar-Mej-Ak or TzHaar-Mej-Jeh again." },
  {
    text = "Enter the Fight Kiln entrance east of Main Plaza.",
    actions = { Action.ConversationHighlight:new("'The Brink of Extinction'.") },
  },
  {
    text = "A cutscene plays.",
    actions = { Action.ConversationHighlight:new("We have to take the risk. Reforge the Ga'al in the Kiln.") },
  },
  {
    text = "Run to the easternmost part of TzHaar City, where a cave entrance is. The entrance is not clickable until after you interact with the orb, cave entrance is indicated on your map.",
  },
  {
    text = "Activate the communication orb in your backpack.",
    actions = { Action.ConversationHighlight:new("TokKul of TzHaar-Mej-Gek.") },
  },
  { text = "Enter the cave." },

  {
    text = '<table><tbody><tr><th>Room</th><th>Wave</th><th>Combat levels</th><th>Pray against</th><th>Weakness(es)</th></tr><tr class=""><td>1</td><td>4 Tz-Kih</td><td>85</td><td>Melee</td><td>Stab</td></tr><tr class=""><td>2</td><td>2 Tz-Kek</td><td>86</td><td>Melee</td><td>Water</td></tr><tr class=""><td>2</td><td>1 Tz-Kih, 1 Tok-Xil</td><td>85,105</td><td>Ranged</td><td>Stab, Slash</td></tr><tr class=""><td>3</td><td>2 TokHaar-Hur, 2 TokHaar-Ket</td><td>112/119</td><td>Melee</td><td>Water</td></tr><tr class=""><td>3</td><td>3 TokHaar-Xil</td><td>119</td><td>Ranged</td><td>Crush</td></tr><tr><td>3</td><td>1 TokHaar-Ket Champion</td><td>112</td><td>Melee</td><td>-</td></tr><tr class=""><td>4</td><td>4 Tz-Kil</td><td>103</td><td>Magic</td><td>Bolts</td></tr><tr class=""><td>5</td><td>3 Tz-Kih</td><td>85</td><td>Melee</td><td>Stab</td></tr><tr class=""><td>5</td><td>2 Tz-Kil, 1 Yt-MejKot</td><td>103,105</td><td>Melee</td><td>Bolts, -</td></tr><tr class=""><td>6</td><td>4 TokHaar-Hur</td><td>112</td><td>Melee</td><td>Water</td></tr><tr class=""><td>6</td><td>2 TokHaar-Xil</td><td>119</td><td>Ranged</td><td>Crush</td></tr><tr><td>6</td><td>2 TokHaar-Mej</td><td>119</td><td>Magic</td><td>Bolts</td></tr><tr class=""><td>6</td><td>2 TokHaar-Ket Champion</td><td>112</td><td>Melee</td><td>-</td></tr><tr><td>7</td><td>3 Tz-Kek</td><td>86</td><td>Melee</td><td>Water</td></tr><tr><td>8</td><td>3 Tz-Kil</td><td>103</td><td>Magic</td><td>Bolts</td></tr><tr><td>8</td><td>2 Tz-Kek, 1 Ket-Zek</td><td>86,105</td><td>Magic</td><td>Water, Bolts</td></tr></tbody></table>',
    title = "Combat Weaknesses",
  },

  { text = "Kill the 4 Tz-Kih.", title = "Room 1" },
  { text = "Pick up the mace, sword and knife." },
  { text = "Put the mace on the western and the sword on the eastern scales." },
  { text = "Go through the ancient gate." },
  { text = "Kill the 2 Tz-Kek.", title = "Room 2" },
  { text = "Kill the Tz-Kih and Tok-Xil" },
  {
    text = "Move the statues such that TzHaar-Mej faces TzHaar-Ket, who faces TzHaar-Xil, who faces TzHaar-Mej. This involves a triangle formed by Xil and Mej (parallel) to the south and Ket to the north. If it doesn't work, make the triangle smaller.",
  },
  { text = "Go through the ancient gate." },
  {
    text = "Turn off Auto-retaliate so your character does not inadvertently step on a glowing square when running to attack a monster (they deal 1000+ damage per second).",
    title = "Room 3",
  },
  { text = "Pass the hot vent door to the east." },
  { text = "Avoid standing on glowing floor squares." },
  { text = "Kill the 2 TokHaar-Kets and 2 TokHaar-Hurs." },
  { text = "Kill the 3 TokHaar-Xils." },
  { text = "Kill the TokHaar-Ket Champion and pick up the obsidian maul it drops." },
  { text = "Pass back through the hot vent door." },
  { text = "Place the maul on the scales." },
  { text = "Go through the ancient gate." },
  { text = "Kill the 4 Tz-Kils.", title = "Room 4" },
  { text = "Pick up each of the obsidian weapons: throwing ring, sword, mace and staff." },
  { text = "Place each of the obsidian weapons on the scales according to their location:" },
  {
    text = '<table class="wikitable lighttable"><tbody><tr><th>Scale location</th><th>Weapon</th></tr><tr class=""><td>North-west</td><td>Obsidian sword</td></tr><tr class=""><td>North-east</td><td>Obsidian mace</td></tr><tr class=""><td>South-west</td><td>Obsidian staff</td></tr><tr class="highlight-over"><td>South-east</td><td>Obsidian throwing ring</td></tr></tbody></table>',
  },
  { text = "Go through the Ancient gate." },
  { text = "Kill the 3 Tz-Kihs.", title = "Room 5" },
  { text = "Kill the 2 Tz-Kils and Yt-MejKot." },
  { text = "Touch one of the pedestals." },
  {
    text = "Touch the pedestals in the order that they light up until the puzzle is complete. There are 8 combinations you will need to complete correctly.<ul><li>Recommended to give each pedestal a number between 1-4. Write the number of the last pedestal that lights up each round in your chatbox, forming a list. When touching pedestals, simply follow the numbers in your chatbox.</li></ul>",
  },
  { text = "Go through the Ancient gate." },
  {
    text = "Turn off Auto-retaliate so your character does not inadvertently step on a glowing square when running to attack a monster (they deal 1000+ damage per second).",
    title = "Room 6",
  },
  { text = "Pass the hot vent door to the east." },
  { text = "Avoid standing on glowing floor squares." },
  { text = "Kill the 4 TokHaar-Hurs." },
  { text = "Kill the 2 TokHaar-Xils." },
  { text = "Kill the 2 TokHaar-Mejs." },
  { text = "Kill the 2 TokHaar-Ket Champions and pick up the obsidian maul dropped." },
  { text = "Pass back through the Hot vent door." },
  { text = "Place the maul on the scales." },
  { text = "Go through the ancient gate." },
  { text = "Kill the 3 Tz-Keks.", title = "Room 7" },
  { text = "Pick up the sword, maul, mace, knife and staff." },
  {
    text = "Inspect each scale for the TzHaar word. Use the correct obsidian weapon on each scale according to the table below:",
  },
  {
    text = '<table class="wikitable lighttable"><tbody><tr><th>TzHaar word</th><th>Weapon</th></tr><tr class=""><td>Xil</td><td>Obsidian sword</td></tr><tr class=""><td>Mej</td><td>Obsidian staff</td></tr><tr class=""><td>Ket</td><td>Obsidian maul</td></tr><tr class=""><td>Ket and Hur</td><td>Obsidian mace</td></tr><tr class=""><td>Xil and Hur</td><td>Obsidian knife</td></tr></tbody></table>',
  },
  { text = "Go through the ancient gate." },
  { text = "Kill the 3 Tz-Kils.", title = "Room 8" },
  { text = "Kill the Ket-Zek and 2 Tz-Keks." },
  { text = "Rotate the tiles on the ground to form the TzHaar symbol." },
  { text = "Go through the Ancient gate." },
  { text = "Switch to Legacy Combat Mode", title = "Room 9" },
  { text = "Pass the hot vent door to the east." },
  { text = "Talk to Ga'al-Xox.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Rotate the eastern valve." },
  {
    text = "Kill TokHaar-Hok.<ul><li>You cannot teleport out or leave the fight early.</li><li>Pray Deflect/Protection from Melee the entire fight.</li><li>Avoid standing on glowing floor squares, as always.</li><li>You do not need to kill the spawned enemies - higher level players may focus on TokHaar-Hok. When he is defeated, the spawned enemies will die.</li><li>Stun TokHaar-Hok once he has lost half his health, this prevents him from healing at the flowing lava.</li><li>The stun mechanic may be bugged and you may only be able to use  basic Ranged ability, Binding Shot, the basic Magic ability, Impact and the Magic threshold ability, Asphyxiate, and the basic melee ability, Backhand.</li><li>In Legacy Mode, you need to stun him with a special attack.</li><li>In Revolution mode, you can easily stun-lock him with Backhand, Kick, Forceful Backhand, Stomp (threshold), Destroy (dual-wield threshold), or by using Death Grasp (T70+ weapon special attack) with Necromancy.</li><li>The stun mechanic may be bugged and you may only be able to use  basic Ranged ability, Binding Shot, the basic Magic ability, Impact and the Magic threshold ability, Asphyxiate, and the basic melee ability, Backhand.</li><li>In Legacy Mode, you need to stun him with a special attack.</li><li>In Revolution mode, you can easily stun-lock him with Backhand, Kick, Forceful Backhand, Stomp (threshold), Destroy (dual-wield threshold), or by using Death Grasp (T70+ weapon special attack) with Necromancy.</li></ul>",
  },
  { text = "Talk to TokHaar-Hok.", title = "Finishing up" },
  { text = "Talk to Ga'al-Xox.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to Ga'al-Xox again.", actions = { Action.ConversationHighlight:new("Farewell.") } },
  {
    text = "Return to the city through the door. Activate your communication orb.<ul><li>If you have destroyed your communication orb, talk to either TzHaar-Mej-Ak or TzHaar-Mej-Jeh in the main plaza of TzHaar City.</li></ul>",
  },
  {
    text = "Go back to the Fight Kiln entrance.",
    actions = { Action.ConversationHighlight:new("'The Brink of Extinction'") },
  },
  {
    text = "A cutscene ensues.",
    actions = {
      Action.ConversationHighlight:new("I can't wait to battle the TokHaar."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Brink of Extinction",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1354579200,
  prereqQuests = { "The Elder Kiln" },
})
