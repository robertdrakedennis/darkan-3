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
    text = "Talk to either NPC in the north eastern most building on the 1st floor[UK]2nd floor[US] of Dorgesh-Kaan.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("What are you arguing about?") },
  },
  {
    text = "[Accept Quest]<ul><li>Ambassador Alvijar</li><li>[Accept Quest]</li><li>If Ambassador Alvijar says you might be a human spy, you haven't completed The Giant Dwarf yet.</li><li>If you have just completed Death to the Dorgeshuun, you may need to exit to the lobby and re-enter the game for him to appear.</li><li>Ur-Tag</li><li>[Accept Quest]</li><li>If Ambassador Alvijar says you might be a human spy, you haven't completed The Giant Dwarf yet.</li><li>If you have just completed Death to the Dorgeshuun, you may need to exit to the lobby and re-enter the game for him to appear.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "While on the same floor, head west then south until you find a  transportation map icon (Show map).",
    title = "Artefacts",
  },
  {
    text = "Enter the doorway, head south down the tracks and talk to Tegdak (you need 2 free spaces for the specimen brush and trowel).",
  },
  {
    text = "Use the trowel on the 6 artefacts on the ground to dig them up. They have the same sparkles as time sprite.<ul><li>If you need more backpack space, you can talk to Tegdak per restored artefact, you don't need to give all in at once.</li></ul>",
  },
  { text = "Use each of them on the specimen table." },
  { text = "Talk to Tegdak and Zanik will be following you after the dialogue." },
  {
    text = "If Zanik isn't following you, dismiss any pets/familiars and talk to Zanik.<ul><li>Zanik will now be following you.</li><li>If you lose Zanik you will need to return here to retrieve her. (Teleporting will leave Zanik behind)</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "With Zanik following, exit the train tunnel using the doorway to the north." },
  { text = "Talk to the Goblin scribe, in the building directly west of the quest start point." },
  { text = "In the house just west of the Goblin scribe, climb down the stairs." },
  {
    text = "Talk to Oldak.<ul><li>If you have Invention unlocked.</li></ul>",
    actions = { Action.ConversationHighlight:new("No - talk about quest or other things.") },
  },
  { text = "You will be teleported to Goblin Village with Zanik, receiving a Dorgesh-kaan sphere in your inventory." },
  {
    text = "In Goblin Village, in the northernmost building, talk to either General Bentnoze or Wartface.",
    title = "The Goblin Village",
  },
  {
    text = "After the cutscene, go behind the buildings to the west, go south, then climb up the ladder to the east.<ul><li>If you attempt to walk around the front of the buildings, you will be stopped by falling arrows.</li></ul>",
  },
  {
    text = "Kill the H.A.M. Mage and H.A.M. Archer.<ul><li>A crossbow and 50 bronze bolts can be found on the ground here.</li></ul>",
  },
  {
    text = "Talk to either General about how to rescue Zanik. .<ul><li>If Recipe for Disaster: Freeing the Goblin Generals was started.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("How can I rescue Zanik?"),
      Action.ConversationHighlight:new("How can I rescue Zanik?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Pink robe man say they digging tunnel under Lumbridge Swamp Caves. You go there to rescue Chosen Commander."
      ),
    },
  },
  {
    text = "With a light source, head to the Lumbridge Swamp Caves entrance north-west of the Water altar.<ul><li>If you brought a water altar teleport or Enlightened amulet to teleport to Lumbridge Swamp, use them now for quicker travel.</li></ul>",
    title = "Infiltrating H.A.M.",
  },
  { text = "Talk to Sergeant Slimetoes or Mossfists outside the entrance to the Lumbridge Swamp Caves." },
  {
    text = "Climb down the dark hole under the tree entering the Lumbridge Swamp Caves.<ul><li>If you haven't already placed rope in the entrance you will need rope.</li></ul>",
  },
  { text = "Go down the ladder, directly south east of the Climbing Rope." },
  {
    text = "For the next part, you need to distract the guards by luring them to Slimetoes and Mossfists.<ul><li>If a guard catches you by getting too close, you will be thrown out to the Lumbridge Swamp Caves.</li><li>Finishing the dialogue and waiting allows the first guard to return with the second guard and they should begin fighting the goblins.</li><li>Once the 2 guards are fighting the goblins, carefully go towards the south end of the tunnel. (A guard will begin patrolling the tunnel once you get close)</li><li>Let the patrolling guard spot you from a distance, then lure the guard to the goblins.</li><li>For the final guard blocking the exit, step next to the pillar in his view and he will spot you. Lure him to the goblins.</li></ul>",
  },
  { text = "With the guards engaging the goblins, it is safe to run south to the ladder." },
  {
    text = "Enabling legacy combat for this section can make your fight with Sigmund significantly quicker.",
    title = "Sigmund Reloaded",
  },
  { text = "Equip the ancient mace and climb down the ladder." },
  {
    text = "Initiate combat with Sigmund.<ul><li>If you have legacy combat enabled, simply use the Ancient Mace's special attack at the start of the fight and finish him however you choose.</li><li>If you have do not have legacy combat enabled, use the Ancient Mace's special attack to disable Sigmund's prayers once you reach 100% adrenaline.</li><li>The special attack ability can be found in the Constitution ability book or you can click on your adrenaline bar on your main ability bar to use the special attack once at 100% adrenaline.</li><li>You can use any combat style you choose once Sigmund's prayer is down.</li><li>The special attack ability can be found in the Constitution ability book or you can click on your adrenaline bar on your main ability bar to use the special attack once at 100% adrenaline.</li><li>You can use any combat style you choose once Sigmund's prayer is down.</li></ul>",
  },
  { text = "After defeating Sigmund, untie Zanik." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Another Slice of H.A.M.",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1177372800,
  prereqQuests = { "Death to the Dorgeshuun", "Goblin Diplomacy", "The Giant Dwarf" },
})
