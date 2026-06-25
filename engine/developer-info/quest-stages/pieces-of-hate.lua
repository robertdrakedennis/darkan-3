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
    text = "Enter the player-owned house portal. Postie Pete will appear and give you a letter from Bill Teach.",
    title = "Getting started",
  },
  { text = "Read the letter.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Deposit all items." },
  {
    text = "Go to the Rimmington Customs Office without any items, then talk to the Customs Sergeant.  You will be sent to the Rock.",
    actions = {
      Action.ConversationHighlight:new("Talk about Pieces of Hate."),
      Action.ConversationHighlight:new("I would like to confess!"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Talk to Bill Teach about Pieces of Hate.", title = "Escape the Rock (again)" },
  {
    text = "Talk to Two-Eyed Eric through barred window.",
    actions = {
      Action.ConversationHighlight:new("Two-Eyed Eric"),
      Action.ConversationHighlight:new("Can you give me a hand?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Use the hook on the bed, then combine the cloth with the hook." },
  { text = "Go outside and use the cloth-hook on the fishing spot." },
  { text = "Use the fish on the perch rock to attract a seagull." },
  { text = "Use the fish on the door, then use the hook on the door." },
  { text = "Push the barrels by the door to block out the guards." },
  { text = "Use the hook on Bill Teach's cell door." },
  { text = "Search the desk and locker. Put the customs uniform on." },
  { text = "Investigate the pile of crates to take a small crate." },
  { text = "Use the spoon on the crate to make a trap, then use the sandwich on it to bait it." },
  {
    text = "Use the trap on the rat hole in Bill Teach's cell.",
    actions = {
      Action.ConversationHighlight:new("[Firmly admonish the rat.]"),
      Action.ConversationHighlight:new("[Inform the rat of your plight.]"),
      Action.ConversationHighlight:new("[Introduce yourself properly.]"),
      Action.ConversationHighlight:new("[Ask for a way to free the others.]"),
    },
  },
  { text = "Use the guard's keys on any remaining prisoner's cell door." },
  { text = "Attempt to open the door in the middle of the prison (leading to the stairs)." },
  { text = "Talk to Madame Shih." },
  { text = "Talk to Jimmy the Parrot (one of the pirates)." },
  { text = "Open the door in the middle of the prison (leading to the stairs)." },
  {
    text = "Investigate the poster next to the stairs, then climb the stairs wearing the customs outfit.<ul><li>If the 'investigate' option doesn't appear on the bed, make sure you have checked the poster downstairs.</li></ul>",
  },
  {
    text = "Search the lockers (to the north). Use the pineapple on the puddle of slime in the corner, then use it on Wilson (the rat).",
  },
  { text = "Use Pineapple Wilson on the door to the south room." },
  { text = "Enter the room and investigate the east bed to find first floor keys." },
  { text = "Open the north door between the beds." },
  {
    text = "Right-click 'Use' the 'rum' on the strange egg.<ul><li>If you drink the 'rum' accidentally, then you can get a new one from the lockers to the east.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes - I am the god of hellfire.") },
  },
  { text = "Climb up the stairs to the east." },
  { text = "After the cutscene, dive off the pier." },
  {
    text = "Speak to anyone.  This is the first point where you can leave the quest and save progress.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Sit in the chair in the north-east corner of Harpoon Joe's House of 'Rum'.",
    title = "Finding a friend",
    neededItems = { ["Leather"] = { quantity = 1 }, ["Gold bar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("A Long Drop.") },
  },
  {
    text = "Speak to the Zombie pirate head.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Kind interrogation.]"),
      Action.ConversationHighlight:new("Maybe we can come to an arrangement."),
      Action.ConversationHighlight:new("We can make that work."),
      Action.ConversationHighlight:new("Deal."),
    },
  },
  {
    text = "Go to the west of Varrock and speak to Gertrude in her house.  (or if completed Rat Catchers)",
    actions = {
      Action.ConversationHighlight:new("Ask about a pet for the zombie head."),
      Action.ConversationHighlight:new("Ask about a pet for the zombie head."),
    },
  },
  { text = "Talk to Philop or Kanel inside Gertrude's house." },
  {
    text = "Go to the abandoned house northwest of the apothecary in south-west Varrock and search the jiggling crate to find Wilson.",
    actions = {
      Action.ConversationHighlight:new("Yes - I fear nothing."),
      Action.ConversationHighlight:new("Yes - it's probably fine."),
    },
  },
  {
    text = "Travel to the Karamja lodestone and head about 10 paces south of the deposit chest; the head will tell you to stop.",
    title = "Seeing the sunset",
    neededItems = {
      ["Blurberry special"] = { quantity = 1 },
      ["Drunk dragon"] = { quantity = 1 },
      ["Chocolate saturday"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Put the zombie head down.") },
  },
  {
    text = "Talk to the head again.  A cutscene of the sunset will start.",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Prepare for combat. Return to Harpoon Joe's House of 'Rum' and return to the Interrogation Room by sitting in the north-east chair.",
    title = "Zogoth",
    actions = { Action.ConversationHighlight:new("A Long Drop.") },
  },
  {
    text = "When ready for the fight, head south-west of Harpoon Joe's House of 'Rum' and row the rowboat.",
    actions = { Action.ConversationHighlight:new("Yes!") },
  },
  { text = "Enter the door to the south." },
  { text = "Kill all four tentacles; a cutscene follows." },
  {
    text = "Kill Zogoth; a cutscene follows.<ul><li>Stay within melee range to avoid a typeless damage attack from the tentacles.</li><li>Move away from the water jet attack to avoid being pushed to the back of the boat.</li><li>Move away from shadows when Zogoth shields itself to avoid acid spray attacks (occurs at 50% and 25% health). The acid will follow you around until the attack ends.</li></ul>",
  },
  {
    text = "Once on Mos Le'Harmless, during Rabid Jack's Invasion, the player must clear two waves of enemies.",
    title = "Rabid Jack's invasion",
  },
  {
    text = "Wave 1 (initial attack):<ul><li>Kill the barrelchest and zombie captains at the main gate to the north and repair the barricades.</li><li>Kill the zombie captains up the ramp (on the western wall), then help up the pirate on the wall.</li><li>Kill the zombie captains at the south-east coast of the town, then repair the cannons and sink the zomboats.</li><li>Kill the zombie captains at the south-west coast of the town, then repair the cannons and sink the zomboats.</li></ul>",
  },
  {
    text = "Wave 2 (next attack):<ul><li>Kill the DKS and the zombie captains at the main gate and repair the barricades.</li><li>Kill the zombie captains up the ramp (on the western wall), then help up the pirate on the wall.</li><li>Kill the zombie captains at the south-east coast of the town, then repair the cannons and sink the zomboats.</li><li>Kill the zombie captains at the south-west coast of the town, then repair the cannons and sink the zomboats.</li><li>Kill the zombie captains at river east of the town, then destroy the makeshift bridges.</li><li>Kill the DKS and the zombie captains at the wheat field north of the town, then destroy the cannon.</li><li>Kill the DKS and the zombie captains at the coast north-west of the town, then destroy the grounded ship. This should be done last.</li></ul>",
  },
  {
    text = "Finish the conversation with the pirates in the invasion. You will not be able to progress without doing so.",
  },
  {
    text = "Head to Braindeath Island via Pirate Pete north of Port Phasmatys.  You can also use the pirate spell sheet  or Dungeoneering cape.",
    title = "Braindeath Island",
    actions = {
      Action.ConversationHighlight:new("Travel to Braindeath Island (Pieces of Hate)."),
      Action.ConversationHighlight:new("Braindeath Island"),
      Action.ConversationHighlight:new("Travel to Braindeath Island (Pieces of Hate)."),
      Action.ConversationHighlight:new("Braindeath Island"),
      Action.ConversationHighlight:new("Travel to Braindeath Island (Pieces of Hate)."),
    },
  },
  {
    text = "Head south, and down the wooden stairs. Run west along the building and up the wooden stairs on the west of the bunkroom.",
  },
  {
    text = "Head through the bunkroom to the east side, then north-east. Listen at the window to the room with Captain Braindeath.",
  },
  { text = "After the dialogue, head back to the bunkroom, and talk to Davey." },
  {
    text = "Head down the wooden stairs, put on the barrelchest disguise then talk to Captain Donnie south-east.",
    actions = { Action.ConversationHighlight:new("Mi-Gor is in need of some witchwood.") },
  },
  { text = "Remove the disguise, for speed." },
  {
    text = "Head north-west and pass through the gate by 66.6...% Luke.<ul><li>You cannot use the dungeoneering cape teleport here to get to the north of the island.</li></ul>",
  },
  { text = "Head north, up the mountain to the pool (marked with  on the minimap) and investigate the perch rock." },
  { text = "Baron von Hattenkrapper will appear. Complete the dialogue, then return south through the gate." },
  {
    text = "Next to the southern wooden stairs, put on the barrelchest disguise, then ascend any of the wooden stairs.",
  },
  {
    text = "Talk with the Barrelchest Mk I.  All options will result in a reply of 'Incorrect.'",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("No.") },
  },
  {
    text = "Head back down the stairs, and talk with Captain Donnie again.",
    actions = { Action.ConversationHighlight:new("Mi-Gor wants to see you right now!") },
  },
  {
    text = "Head back up the wooden stairs, and once again talk with the Barrelchest Mk I.",
    actions = { Action.ConversationHighlight:new("'How appropriate. You fight like a zombie sea cow.'") },
  },
  { text = "Climb the ladder in the south-east corner of the building." },
  { text = "Remove the disguise, for speed." },
  { text = "Head west, and take the Barrels of 'rum' from on top of the crate, head north and do the same again." },
  {
    text = "Block the pressure barrel (just south-east of the second of the barrels of 'rum'), then overload the pressure lever beside it.",
  },
  {
    text = "Return to Baron von Hattenkrapper and investigate.",
    actions = { Action.ConversationHighlight:new("Unleash!") },
  },
  {
    text = "Guide Baron von Hattenkrapper and hit the mysterious entrance to the south-west 3 times; a cutscene follows.",
  },
  {
    text = "Talk with Mi-Gor, and set sail.",
    actions = { Action.ConversationHighlight:new("[Set sail...for Kraken Tooth Island.]") },
  },
  {
    text = "Head south and equip gear from the crate of diving gear, then head north and dive into the cracked hull.",
    title = "Underwater",
  },
  { text = "Head south-west, and jump off the edge." },
  {
    text = "Head south, then west, then north to the shimmering barrier.<ul><li>EPILEPSY WARNING: If you suffer from epilepsy, talk to the shimmering kitten to prevent aggravating symptoms.</li><li>You need to prepare for combat for the next part.</li><li>You need to prepare for combat for the next part.</li></ul>",
  },
  {
    text = "If well-prepared for combat, pass through the shimmering barrier; a cutscene follows.<ul><li>Note: If you choose to re-gear use the rowboat in the south-west of Mos Le'Harmless to get back to the temple.</li></ul>",
  },
  {
    text = "Reduce Jack's health to 0.<ul><li>Stand at the north, south, west, east walls or doorway to avoid being damaged and stunned by the black hand in the centre of the room.</li><li>Use Freedom or Anticipation if stunned by Jack's stun attack. Avoid running from Jack to reduce the frequency of this attack.</li><li>Drink 'rum' from barrels in the room to reduce 'insanity'. Insanity is indicated by the black cat debuff icon and will cause Jack hallucinations to spawn and the camera to spin if it becomes too high. Drink as soon as the debuff icon appears.</li><li>If you die you can return to fight Rabid Jack by travelling back to Mos Le'Harmless and taking the small rowboat.</li></ul>",
    title = "Rabid Jack fight",
  },
  { text = "Loosen an ancient chain." },
  { text = "Repeat this process for the 3 other chains; a cutscene follows." },
  { text = "Speak with Madame Shih.", actions = { Action.ConversationHighlight:new("Let's go!") } },
  {
    text = "Sit in the chair in Harpoon Joe's House of 'Rum'",
    actions = { Action.ConversationHighlight:new("A Long Drop.") },
  },
  { text = "Talk to Bill Teach with 6 free inventory spaces; a cutscene follows." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Pieces of Hate",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1521417600,
  prereqQuests = { "A Clockwork Syringe", "Gertrude's Cat" },
})
