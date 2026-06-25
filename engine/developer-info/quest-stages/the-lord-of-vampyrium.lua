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
    text = "Talk to Vanescula Drakan at the docks south-east of Burgh de Rott.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Board the nearby boat to Icyene graveyard.",
    title = "Gathering of the Myreque",
    actions = { Action.ConversationHighlight:new("Icyene graveyard.") },
  },
  { text = "Talk to Safalaan." },
  { text = "Solve the ring puzzle by rotating the rings until they turn blue and their markings join together." },
  { text = "Inspect the statue of Queen Efaritay to receive Efaritay's pendant." },
  { text = "Talk to Safalaan." },
  {
    text = "Travel to the Myreque Hideout with Drakan's medallion  , use the Meiyerditch teleport option, and walk into the central room.",
  },
  { text = "After the cutscene, talk to Safalaan in the northern room." },
  { text = "Equip your Darkmeyer disguise.", title = "Vyrewatch uniforms" },
  { text = "Use your Drakan's medallion to teleport to Darkmeyer" },
  {
    text = "Talk to Veliaf Hurtz inside the Arboretum. He will not appear until you walk into the central chamber.<ul><li>At this point you are able to craft more types of blisterwood weapons.</li></ul>",
  },
  { text = "Head south-west inside Vanstrom's former mansion." },
  { text = "Open then search the vyrewatch crate to obtain vyrewatch uniforms." },
  {
    text = "Return to Veliaf.  He will give you a set of the House Drakan outfit.",
    title = "Castle Drakan",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "No items other than the Darkmeyer outfit, House Drakan outfit, Drakan medallion, runes, Ivandis flail, and blisterwood weapons and ammo can be taken to Castle Drakan.",
  },
  { text = "Equip the House Drakan outfit, unequip any weapons, and talk to Vanescula near Overwatch Mornid." },
  {
    text = "Complete the following to gain status:<ul><li>North-west, talk to Lady Nadezhda Shadum.</li><li>West, talk to Lord Alexei Jovkai.</li><li>East, talk to Lord Mischa Myrmel.</li><li>Shoo the bats flying around.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Humiliate it."),
      Action.ConversationHighlight:new("Try the old man - he looks defiant."),
      Action.ConversationHighlight:new("Show mercy; make her feel better."),
    },
  },
  {
    text = "Talk to Vanescula.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Exit your cell and unlock the five other cells. You will take damage equal to 10% of your life points. You will have a chance to eat in the next section. Make sure you talk to the Myreque while doing this or the portcullis will not open.",
    title = "Escaping the cells",
  },
  {
    text = "Go to the next chamber and solve the ring puzzle. This is similar to the earlier puzzle with the addition that rotating a ring causes the smaller, adjacent ring to rotate as well. Rotating the centre ring causes the outer ring to rotate.<ul><li>Start from the outside and work your way in.</li></ul>",
  },
  { text = "Go to the next chamber and talk to Safalaan." },
  { text = "Go up the stairs and leave the dungeon." },
  {
    text = "Go to the eastern room and take all the mysterious jerky you are able to from the butchery table.<ul><li>Eat to full health, you can take more after eating.</li></ul>",
  },
  { text = "Talk to Vanescula and Mornid in the western room." },
  {
    text = "Go to the southern room, and read the A Taste of Hope book obtained earlier. Investigate each blood lock. The correct matches are:<ul><li>Ghrazi - Veliaf</li><li>Alzeph - Radigad</li><li>Pyrah - Kael</li><li>Myrmel - Mekritus</li><li>Vitur - Polmafi</li><li>Shadum - Vertida</li><li>Jovkai - Safalaan</li><li>Drakan - Ivan</li></ul>",
    actions = {
      Action.ConversationHighlight:new("[More choices.]"),
      Action.ConversationHighlight:new("Veliaf."),
      Action.ConversationHighlight:new("[More choices.]"),
      Action.ConversationHighlight:new("Radigad."),
      Action.ConversationHighlight:new("Kael."),
      Action.ConversationHighlight:new("Mekritus."),
      Action.ConversationHighlight:new("Polmafi."),
      Action.ConversationHighlight:new("[More choices.]"),
      Action.ConversationHighlight:new("Vertida."),
      Action.ConversationHighlight:new("[More choices.]"),
      Action.ConversationHighlight:new("Safalaan."),
      Action.ConversationHighlight:new("Ivan."),
    },
  },
  { text = "Unlock the tithing door south and escape through the cellar exit." },
  { text = "Enter the tunnel and Lowerniel Drakan will appear." },
  {
    text = "Vandalise the statue of Lord Drakan to obtain a statue spear (if the option to vandalise doesn't appear then enter the east door for a cutscene).",
    title = "Ascending the castle - Ground floor",
  },
  { text = "Brace the entrance door." },
  { text = "Defeat the venator and climb the stairs." },
  { text = "Smash 2 tables and 3 chairs and collect 9 furniture debris." },
  {
    text = "Barricade the three windows. Do not click away before you get the message confirming the window is barricaded.",
  },
  { text = "Defeat the venator if time runs out and one appears." },
  {
    text = "You can collect 3 extra debris from smashing the remaining chairs in this room to make subsequent rooms easier.",
  },
  { text = "Unlock the blood lock and enter the second room. Another timer will start." },
  { text = "Smash the table. You should have 6 furniture debris." },
  { text = "Barricade the two windows. Do not click away before the 'the window is barricaded' message appears." },
  { text = "Defeat the venator if time runs out and one appears." },
  { text = "Unlock the blood lock and enter the third room." },
  { text = "Lift the table." },
  { text = "Defeat the venator." },
  { text = "Lift the table." },
  { text = "Unlock the blood lock and enter the fourth room." },
  {
    text = "Smash the chairs and table to the right of the door and the weapon racks which are against the north and south sides of the staircase.",
  },
  { text = "Barricade the two windows. Defeat the venator if time runs out and one appears." },
  {
    text = "Solve the puzzle by making the following moves (if you mess up, you can reset the puzzle by talking to Polmafi):<ul><li>Rotate ring 'E' clockwise once.</li><li>Rotate ring 'D' clockwise three times.</li><li>Rotate ring 'C' clockwise twice.</li><li>Rotate ring 'B' clockwise twice.</li><li>Rotate ring 'A' clockwise once.</li></ul>",
  },
  { text = "Climb upstairs." },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  {
    text = "Vandalise the statue of Lord Drakan to obtain a statue spear (if the option to vandalise doesn't appear then enter the east door for a cutscene).",
  },
  { text = "Brace the entrance door." },
  { text = "Defeat the venator and climb the stairs." },
  { text = "Smash 2 tables and 3 chairs and collect 9 furniture debris." },
  {
    text = "Barricade the three windows. Do not click away before you get the message confirming the window is barricaded.",
  },
  { text = "Defeat the venator if time runs out and one appears." },
  {
    text = "You can collect 3 extra debris from smashing the remaining chairs in this room to make subsequent rooms easier.",
  },
  { text = "Unlock the blood lock and enter the second room. Another timer will start." },
  { text = "Smash the table. You should have 6 furniture debris." },
  { text = "Barricade the two windows. Do not click away before the 'the window is barricaded' message appears." },
  { text = "Defeat the venator if time runs out and one appears." },
  { text = "Unlock the blood lock and enter the third room." },
  { text = "Lift the table." },
  { text = "Defeat the venator." },
  { text = "Lift the table." },
  { text = "Unlock the blood lock and enter the fourth room." },
  {
    text = "Smash the chairs and table to the right of the door and the weapon racks which are against the north and south sides of the staircase.",
  },
  { text = "Barricade the two windows. Defeat the venator if time runs out and one appears." },
  {
    text = "Solve the puzzle by making the following moves (if you mess up, you can reset the puzzle by talking to Polmafi):<ul><li>Rotate ring 'E' clockwise once.</li><li>Rotate ring 'D' clockwise three times.</li><li>Rotate ring 'C' clockwise twice.</li><li>Rotate ring 'B' clockwise twice.</li><li>Rotate ring 'A' clockwise once.</li></ul>",
  },
  { text = "Climb upstairs." },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  { text = "Smash 2 tables and 3 chairs and collect 9 furniture debris." },
  {
    text = "Barricade the three windows. Do not click away before you get the message confirming the window is barricaded.",
  },
  { text = "Defeat the venator if time runs out and one appears." },
  {
    text = "You can collect 3 extra debris from smashing the remaining chairs in this room to make subsequent rooms easier.",
  },
  { text = "Unlock the blood lock and enter the second room. Another timer will start." },
  { text = "Smash the table. You should have 6 furniture debris." },
  { text = "Barricade the two windows. Do not click away before the 'the window is barricaded' message appears." },
  { text = "Defeat the venator if time runs out and one appears." },
  { text = "Unlock the blood lock and enter the third room." },
  { text = "Lift the table." },
  { text = "Defeat the venator." },
  { text = "Lift the table." },
  { text = "Unlock the blood lock and enter the fourth room." },
  {
    text = "Smash the chairs and table to the right of the door and the weapon racks which are against the north and south sides of the staircase.",
  },
  { text = "Barricade the two windows. Defeat the venator if time runs out and one appears." },
  {
    text = "Solve the puzzle by making the following moves (if you mess up, you can reset the puzzle by talking to Polmafi):<ul><li>Rotate ring 'E' clockwise once.</li><li>Rotate ring 'D' clockwise three times.</li><li>Rotate ring 'C' clockwise twice.</li><li>Rotate ring 'B' clockwise twice.</li><li>Rotate ring 'A' clockwise once.</li></ul>",
  },
  { text = "Climb upstairs." },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  { text = "Smash 2 tables and 3 chairs and collect 9 furniture debris." },
  {
    text = "Barricade the three windows. Do not click away before you get the message confirming the window is barricaded.",
  },
  { text = "Defeat the venator if time runs out and one appears." },
  {
    text = "You can collect 3 extra debris from smashing the remaining chairs in this room to make subsequent rooms easier.",
  },
  { text = "Unlock the blood lock and enter the second room. Another timer will start." },
  { text = "Smash the table. You should have 6 furniture debris." },
  { text = "Barricade the two windows. Do not click away before the 'the window is barricaded' message appears." },
  { text = "Defeat the venator if time runs out and one appears." },
  { text = "Unlock the blood lock and enter the third room." },
  { text = "Lift the table." },
  { text = "Defeat the venator." },
  { text = "Lift the table." },
  { text = "Unlock the blood lock and enter the fourth room." },
  {
    text = "Smash the chairs and table to the right of the door and the weapon racks which are against the north and south sides of the staircase.",
  },
  { text = "Barricade the two windows. Defeat the venator if time runs out and one appears." },
  {
    text = "Solve the puzzle by making the following moves (if you mess up, you can reset the puzzle by talking to Polmafi):<ul><li>Rotate ring 'E' clockwise once.</li><li>Rotate ring 'D' clockwise three times.</li><li>Rotate ring 'C' clockwise twice.</li><li>Rotate ring 'B' clockwise twice.</li><li>Rotate ring 'A' clockwise once.</li></ul>",
  },
  { text = "Climb upstairs." },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  { text = "Smash the table. You should have 6 furniture debris." },
  { text = "Barricade the two windows. Do not click away before the 'the window is barricaded' message appears." },
  { text = "Defeat the venator if time runs out and one appears." },
  { text = "Unlock the blood lock and enter the third room." },
  { text = "Lift the table." },
  { text = "Defeat the venator." },
  { text = "Lift the table." },
  { text = "Unlock the blood lock and enter the fourth room." },
  {
    text = "Smash the chairs and table to the right of the door and the weapon racks which are against the north and south sides of the staircase.",
  },
  { text = "Barricade the two windows. Defeat the venator if time runs out and one appears." },
  {
    text = "Solve the puzzle by making the following moves (if you mess up, you can reset the puzzle by talking to Polmafi):<ul><li>Rotate ring 'E' clockwise once.</li><li>Rotate ring 'D' clockwise three times.</li><li>Rotate ring 'C' clockwise twice.</li><li>Rotate ring 'B' clockwise twice.</li><li>Rotate ring 'A' clockwise once.</li></ul>",
  },
  { text = "Climb upstairs." },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  { text = "Lift the table." },
  { text = "Defeat the venator." },
  { text = "Lift the table." },
  { text = "Unlock the blood lock and enter the fourth room." },
  {
    text = "Smash the chairs and table to the right of the door and the weapon racks which are against the north and south sides of the staircase.",
  },
  { text = "Barricade the two windows. Defeat the venator if time runs out and one appears." },
  {
    text = "Solve the puzzle by making the following moves (if you mess up, you can reset the puzzle by talking to Polmafi):<ul><li>Rotate ring 'E' clockwise once.</li><li>Rotate ring 'D' clockwise three times.</li><li>Rotate ring 'C' clockwise twice.</li><li>Rotate ring 'B' clockwise twice.</li><li>Rotate ring 'A' clockwise once.</li></ul>",
  },
  { text = "Climb upstairs." },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  {
    text = "Smash the chairs and table to the right of the door and the weapon racks which are against the north and south sides of the staircase.",
  },
  { text = "Barricade the two windows. Defeat the venator if time runs out and one appears." },
  {
    text = "Solve the puzzle by making the following moves (if you mess up, you can reset the puzzle by talking to Polmafi):<ul><li>Rotate ring 'E' clockwise once.</li><li>Rotate ring 'D' clockwise three times.</li><li>Rotate ring 'C' clockwise twice.</li><li>Rotate ring 'B' clockwise twice.</li><li>Rotate ring 'A' clockwise once.</li></ul>",
  },
  { text = "Climb upstairs." },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  { text = "Talk to Veliaf." },
  {
    text = "Climb the stairs on both sides and open the blood valves in each room.<ul><li>After defeating the venator in the western room, check on Ivan.</li></ul>",
  },
  { text = "Back on the 2nd floor[UK]3rd floor[US], enter the northern door and open the blood valve." },
  {
    text = "Climb down the stairs and solve the final ring puzzle by making these moves:<ul><li>Rotate ring A anticlockwise once.</li><li>Rotate ring B clockwise three times.</li><li>Rotate ring C clockwise once.</li><li>Rotate ring D anticlockwise three times.</li><li>Rotate ring E anticlockwise three times.</li></ul>",
  },
  {
    text = "Before entering the fight you can use stat-boosting potions and prayer restoration potions to make the fight easier, but these items cannot be brought to the fight.",
    title = "Confronting Drakan",
  },
  { text = "Climb the stairs in the ring puzzle room and prepare to fight Lowerniel Drakan." },
  {
    text = "Kill the three venators. (You can stand behind Vertida, Veliaf and Safalaan when attacking each of the venators and they won't attack you, saving you from needing to eat.)",
  },
  {
    text = "Fight Lowerniel Drakan.<ul><li>Run away from bombs which appear after he says 'Ha ha ha!'.</li><li>When he says things like 'Still trying to run?' or 'You think you can escape?' use Anticipation to avoid being stunned and dealt high damage.</li></ul>",
  },
  {
    text = "Fight Lowerniel Drakan again.<ul><li>When he says 'Fear me!' and transforms into a cloud of dust, run away from the cloud.</li><li>When he says 'Embrace darkness', stand within the sphere of lightning surrounding Safalaan.</li><li>When he says 'Graaah!', use Anticipation or Freedom if you get stunned.</li></ul>",
  },
  {
    text = "Jump into the portal and fight Lowerniel Drakan again - he will use the same attacks as in the previous fights.",
  },
  {
    text = "Talk to Lowerniel Drakan and then fight him for the final time.<ul><li>Avoid bombs as before.</li><li>Move away from him when he is doing a special attack (rapid melee attacks) to avoid taking damage.</li></ul>",
  },
  { text = "Select the 'finish' option on Lowerniel Drakan.", title = "Finishing up" },
  { text = "Talk to Ivan. Cutscene will follow." },
  { text = "Talk to Ivan again.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Go to Varrock Palace and talk to King Roald.",
    actions = { Action.ConversationHighlight:new("Talk about Lord of Vampyrium.") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Lord of Vampyrium",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1442188800,
  prereqQuests = { "The Branches of Darkmeyer" },
})
