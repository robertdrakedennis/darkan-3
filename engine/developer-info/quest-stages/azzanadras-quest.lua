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
    text = "Go up the stairs in Burthorpe Castle.",
    title = "Prepare to enter Elder Halls",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Talk to Azzanadra at the north-west corner.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  {
    text = "[Accept Quest]<ul><li>Before continuing, make sure Azzanadra asks you to meet at Kharid-et Dig Site. Use the quest log if needed.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Travel to Kharid-et Dig Site (use dig sites map or other means) and enter the main fortress. Talk to Azzanadra at the Praetorium war table (not in the Praetorium).",
  },
  {
    text = "Travel to the World Gate and talk to Azzanadra. Teleporting, logging out or going to lobby will require starting this part over from this step.<ul><li>Use the Sixth-Age circuit to teleport, or run south from the Eagles' Peak lodestone, or teleport to the Outpost with a traveller's necklace and run west.</li></ul>",
    title = "Travel through Freneskae",
  },
  {
    text = "Equip a face mask or masked earmuffs to protect against volcanic ash. Having a slayer helmet on the slayer helmet stand counts for this.",
  },
  {
    text = "Enter the World Gate, travelling to the 'Approach' destination (first option). Make sure to finish the dialogue with Azzanadra upon entry.",
  },
  { text = "Head east over the ledges, then up the cliffside." },
  {
    text = "Continue east, climbing down the drop, then talk to Azzanadra to the south-east.",
    actions = { Action.ConversationHighlight:new("Let's just keep going") },
  },
  { text = "Continue north, crossing the stepping stones and up the cliffside." },
  {
    text = "Continue north, wall run the overhang and talk to Azzanadra again.",
    actions = { Action.ConversationHighlight:new("Let's just keep going") },
  },
  {
    text = "Climb the cliffside, continue west and slide down the slope and head west.<ul><li>You can heal up by standing under the archway with the glowing crystals.</li></ul>",
  },
  {
    text = "Continue south, wrapping around east and north, then climb the nearby cliffside which is south of the archway.",
  },
  {
    text = "Walk across the rock formation and talk to Azzanadra again.",
    actions = { Action.ConversationHighlight:new("Let's just keep going") },
  },
  { text = "Continue east and climb down the drop, then traverse the rock bridge to the east." },
  { text = "Talk to Azzanadra again." },
  { text = "Enter the opening." },
  {
    text = "Travel to the World Gate and talk to Azzanadra. Teleporting, logging out or going to lobby will require starting this part over from this step.<ul><li>Use the Sixth-Age circuit to teleport, or run south from the Eagles' Peak lodestone, or teleport to the Outpost with a traveller's necklace and run west.</li></ul>",
  },
  {
    text = "Equip a face mask or masked earmuffs to protect against volcanic ash. Having a slayer helmet on the slayer helmet stand counts for this.",
  },
  {
    text = "Enter the World Gate, travelling to the 'Approach' destination (first option). Make sure to finish the dialogue with Azzanadra upon entry.",
  },
  { text = "Head east over the ledges, then up the cliffside." },
  {
    text = "Continue east, climbing down the drop, then talk to Azzanadra to the south-east.",
    actions = { Action.ConversationHighlight:new("Let's just keep going") },
  },
  { text = "Continue north, crossing the stepping stones and up the cliffside." },
  {
    text = "Continue north, wall run the overhang and talk to Azzanadra again.",
    actions = { Action.ConversationHighlight:new("Let's just keep going") },
  },
  {
    text = "Climb the cliffside, continue west and slide down the slope and head west.<ul><li>You can heal up by standing under the archway with the glowing crystals.</li></ul>",
  },
  {
    text = "Continue south, wrapping around east and north, then climb the nearby cliffside which is south of the archway.",
  },
  {
    text = "Walk across the rock formation and talk to Azzanadra again.",
    actions = { Action.ConversationHighlight:new("Let's just keep going") },
  },
  { text = "Continue east and climb down the drop, then traverse the rock bridge to the east." },
  { text = "Talk to Azzanadra again." },
  { text = "Enter the opening." },
  { text = "Talk to Azzanadra.", title = "Inside the Halls" },
  { text = "Inspect the fragmented sphere at each end of the halls." },
  {
    text = "Talk to Ariane back at the centre, she gives you a blank observation. Azzanadra will replace this if you lost yours.",
  },
  {
    text = "Collect memories from the elder god memory wisps (50 each).<ul><li>Bik memory wisp (western hallway).</li><li>Wen memory wisp (northern hallway).</li><li>Ful memory wisp (north-eastern hallway).</li><li>Jas memory wisp (south-western hallway).</li></ul>",
  },
  { text = "Inspect the blank observation in your backpack." },
  { text = "Talk to Azzanadra." },
  {
    text = "Light voice and Shadow voice will speak to you.",
    actions = { Action.ConversationHighlight:new("I've heard enough. I have a world to save.") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, we're not going anywhere, so you might as well just get on with whatever it was you were doing."
      ),
    },
  },
  {
    text = "Travel to White Knights' Castle in Falador and enter the courtyard. Talk to Sir Upticious near the entrance.",
    title = "Retrieving the crown",
    neededItems = { ["Slayer bell"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Go up the eastern ladder then up the staircase by Sir Renitee to the altar in the castle. Talk to Father Frith.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Go to the ground floor[UK]1st floor[US], walk one room west from the ladder and talk to Sir Owen.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Return upstairs and talk to Father Frith." },
  {
    text = "Talk to Sir Amik Varze on the 2nd floor[UK]3rd floor[US] in the west wing of the castle.",
    actions = {
      Action.ConversationHighlight:new("Ask about 'Azzanadra's Quest'."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Padomenes in the courtyard.",
    actions = {
      Action.ConversationHighlight:new("Ask about Azzanadra's Quest."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Sir Vyvin, found next to Father Frith on 2nd floor[UK]3rd floor[US].",
    actions = { Action.ConversationHighlight:new("[Ask about Azzanadra's Quest.]") },
    postconditions = {
      Condition.ConversationText:new(" I'm very busy right now, but I can spare a few moments, what do you want?"),
    },
  },
  { text = "Talk to Sir Owen on the ground floor[UK]1st floor[US], in the room west of the ladder." },
  {
    text = "Search the nearby drawers.",
    actions = { Action.ConversationHighlight:new("Look for ritual documents.") },
    postconditions = { Condition.ConversationText:new(" Anything?") },
  },
  { text = "Go up the ladder into Sir Renitee's room and crack open the safe." },
  { text = "Right-click the slayer bell and use the 'Cut clapper' option to create a silent bell." },
  {
    text = "Travel to the Paterdomus and enter the Mausoleum to the north.<ul><li>Use the the Wicked hood Earth altar teleport for fast travel.</li><li>Alternatively, use the Skeletal horror Teleport or invitation box.</li></ul>",
  },
  { text = "Use the bell on the well to create a silent bell (blessed)." },
  {
    text = "Return to Saradomin's throne in the White Knights' Castle by the altar and next to Father Frith on 2nd floor[UK]3rd floor[US].",
  },
  {
    text = "Ring the bell three times, calling Saradomin's name each time.",
    actions = { Action.ConversationHighlight:new("Saradomin!") },
    postconditions = { Condition.ConversationText:new(" Two more to go.") },
  },
  {
    text = "Speak to Saradomin.",
    actions = {
      Action.ConversationHighlight:new("The world is in danger."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("We need to find the eggs."),
      Action.ConversationHighlight:new("We need your crown."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Yes. As the crown can track down elder artefacts, surely it can track down the eggs themselves?"
      ),
    },
  },
  {
    text = "Travel to Kharid-et and talk to Azzanadra at the Praetorium war table.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Travel to the Hall of Memories (use the Sixth-Age circuit or memory strands to teleport to Memorial to Guthix)",
    title = "Hall of Memories",
  },
  {
    text = "Enter the pool.",
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Capture the 6 Archivist's memories, then talk to the Archivist .",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Capture the 6 restricted memories." },
  { text = "Interact with each of the plinths in the north-west and north-east." },
  { text = "Search the memory bud in the centre to receive a restricted engram." },
  { text = "Leave the Hall of Memories and use the restricted engram on the fountain of energy." },
  {
    text = "Travel to the entrance of the TzHaar City (use the TokKul-Zo to teleport to the main plaza then run south and exit the volcano). You can use the boss portal or the combat portal attuned to TzHaar Fight Cave to get there.<ul><li>Alternatively, fairy ring BLP is located near the fight cave. From here, leave the city through the exit to the south.</li></ul>",
    title = "TzHaar City",
  },
  { text = "Talk to Trindine on the surface of Karamja Volcano." },
  {
    text = "Enter the city, talk to Ga'al just north of the entrance.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Go north to the main plaza (you can teleport back here using the TokKul-Zo), talk to Trindine." },
  { text = "Talk to TokHaar-Hok." },
  { text = "Travel to the TzHaar City library (north-west of the main plaza), go inside." },
  { text = "Talk to Trindine." },
  {
    text = "Travel to the Wizards' Tower (use the wicked hood, stardust, or traveller's necklace to teleport)",
    title = "Wizards' Tower",
  },
  {
    text = "Talk to Wizard Trindy on ground floor[UK]1st floor[US] at entrance of the Wizards' Tower.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Search four bookcases to the west." },
  { text = "Talk to Wizard Trindy at the eastern bookcases." },
  { text = "Return to Kharid-et Dig Site's praetorium war table.", title = "Return to Kharid-et Dig Site" },
  { text = "Talk to Azzanadra." },
  {
    text = "Travel to the entrance of the Heart of Gielinor.<ul><li>Use The Heart teleport and leave by the wooden lift.</li><li>See other fast travel options if not using The Heart teleport.</li></ul>",
    title = "Gielinor's Elder Halls",
  },
  { text = "Talk to Azzanadra." },
  {
    text = "Enter the Heart and continue the dialogue.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Observe each of the eggs at the end of the corridors.<ul><li>Bik's egg.</li><li>Wen's egg.</li><li>Ful's egg.</li></ul>",
  },
  { text = "Talk to Ariane." },
  {
    text = "Return to Kharid-et and enter to the main fortress.",
    title = "Trindine and Azzanadra's plan",
    neededItems = { ["Blank observation"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Walk to the praetorium war table (you need to walk into the room where the table is to start the dialogue).",
  },
  { text = "Stand next to any four shadow anchors around Kharid-et (see the map)." },
  { text = "Harvest Trindine wisps until you get 50 memories." },
  { text = "Inspect the blank observation and finish the dialogue or Ga'al will not spawn for the next step." },
  { text = "Return to the TzHaar City main plaza." },
  { text = "Talk to Ga'al in the main plaza." },
  { text = "Return to the Wizards' Tower." },
  { text = "Walk to the entrance to trigger the dialogue." },
  {
    text = "Return to the Hall of Memories and enter the pool.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Talk to the Archivist." },
  {
    text = "Return to the Heart of Gielinor (enter from the entrance).",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Talk to Ariane. A cutscene will follow.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to Ariane again." },
  { text = "Return to the White Knights' Castle, talk to Ariane on the bridge." },
  { text = "A cutscene will follow.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Return to Kharid-et, talk to Azzanadra at the praetorium war table to complete the quest.<ul><li>You need 3 free backpack spaces upon quest completion for the experience lamps rewarded.</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Azzanadra's Quest",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1613952000,
  prereqQuests = { "The Vault of Shadows", "Raksha, the Shadow Colossus (quest)", "Desperate Creatures" },
})
