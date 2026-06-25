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
    text = "Talk to Moia or Hebe outside of Senntisten, north of the Archaeology Guild.",
    title = "Defending Senntisten",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Enter Senntisten via the ancient door.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Kill 12 TzekHaar monsters." },
  { text = "Enter the cathedral door to the south." },
  {
    text = "Talk to Moia.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("Continue...") },
    postconditions = {
      Condition.ConversationText:new(
        " He chose to stand by so we might face this threat without him. It was by his design, and we grew stronger for it."
      ),
    },
  },
  { text = "Head out of the cathedral to the east and continue east to the Croesus Front.", title = "Croesus Front" },
  {
    text = "Remove each fungus from the skilling node; Croesus will use mechanics from the boss fight in the following order: Yellow Smoke, Stun, Green Smoke, Blue Smoke, Slime, Red Smoke.<ul><li>You may want to bring either super restore potions or the runes for Crystal Mask.</li><li>If your stats get too low, Gorvek will save you by teleporting you back to the cathedral. Your fungus removal progress will be saved.</li><li>Fishing</li><li>Woodcutting</li><li>Mining</li><li>Hunter</li><li>If your stats get too low, Gorvek will save you by teleporting you back to the cathedral. Your fungus removal progress will be saved.</li></ul>",
  },
  {
    text = "Return to the cathedral and talk to Saradomin. You can't teleport back, but you can lobby and re-enter Senntisten.",
  },
  {
    text = "Head out of the cathedral to the south. Pass through the barrier and talk to Azzanadra. This is an unsafe death.",
    title = "Glacor Front",
  },
  {
    text = "Light the braziers following the combinations below, after lighting, enter the portal and kill the generating glacors. You can ignore the glacytes.<ul><li>Food is advised as whilst lighting the braziers the Arch-Glacor will use similar mechanics from the boss fight.</li><li>Light only the east brazier, then enter portal to kill the glacor.</li><li>Extinguish the east brazier and light only the middle brazier, then enter portal to kill the glacor.</li><li>Extinguish the middle brazier and light only the west brazier, then enter portal to kill the glacor.</li><li>Lastly, light both the west and middle braziers, then enter portal and kill the glacor.</li></ul>",
  },
  { text = "Return to the Cathedral and talk to Saradomin. You can lobby to get there faster." },
  {
    text = "Head out of the Cathedral to the west and command the abandoned siege engine.<ul><li>Click attack on each of the siege engines to destroy them, and for the two at the back, click on the floor nearby.</li><li>Repeat this a few times until you enter another dialogue with Kerapac.</li><li>After the dialogue, kill the remaining siege engines.</li></ul>",
    title = "Nodon Front",
  },
  { text = "Return to the Cathedral and talk to Seren." },
  {
    text = "Talk to Moia.",
    title = "Searching for Seren",
    neededItems = { ["The Measure"] = { quantity = 1 }, ["Communication device (Extinction)"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Continue...") },
    postconditions = {
      Condition.ConversationText:new(
        " He chose to stand by so we might face this threat without him. It was by his design, and we grew stronger for it."
      ),
    },
  },
  {
    text = "Retrieve the Measure.<ul><li>If the quest Fate of the Gods is not done or if The Measure is lost, upon finishing dialogue with Vicendithas (see below), visit Mr Mordaut east of the Anachronia lodestone to reclaim it, the quest does not need to be completed. If option does not exist, talk to Vicendithas and try again.</li><li>Alternatively, teleport to the World Gate and travel to the Elder Halls where it can be picked up from the north of where you teleport in.</li></ul>",
    actions = { Action.ConversationHighlight:new("Extinction (Quest)") },
    postconditions = {
      Condition.ConversationText:new(
        " Hannibus said I left it here on Anachronia and your were keeping it safe for me?"
      ),
    },
  },
  { text = "Teleport to the Anachronia lodestone, and head north to the Effigy Incubator laboratory." },
  {
    text = "Enter the door under the Dragonkin statue.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Do the following once inside the Effigy Incubator area:<ul><li>Talk to Vicendithas.</li><li>Talk to Vicendithas again to give him the Measure.</li><li>Talk to Hannibus.</li><li>Talk to Vicendithas.</li><li>Talk to Hannibus again.</li><li>Talk to Vicendithas again.</li><li>Wait a moment and talk to Vicendithas again.</li><li>Activate the communication device and talk to Moia.</li><li>Talk to Vicendithas.</li></ul>",
    actions = { Action.ConversationHighlight:new("Continue."), Action.ConversationHighlight:new("Continue.") },
    postconditions = { Condition.ConversationText:new(" I hope we're not too late.") },
  },
  {
    text = "Go to the World Gate and talk to Moia (or one of the other NPCs).<ul><li>Closest teleport is Sixth-Age circuit or Eagles' Peak lodestone.</li></ul>",
    title = "Hopping worlds",
    actions = { Action.ConversationHighlight:new("Tarddiad") },
    postconditions = {
      Condition.ConversationText:new(
        " There's a personal connection - it would be the first place she thinks of. She might think to hide there to keep the eggs safe."
      ),
    },
  },
  {
    text = "Enter the World Gate to Tarddiad and do the following once inside:<ul><li>Talk to Hannibus (or one of the other NPCs).</li><li>Investigate the three resonating crystals:</li><li>First crystal: to the south-east, next to Vicendithas.</li><li>Second crystal: just north-east of the first, next to Adrasteia and Moia.</li><li>Third crystal: in the Stone Gazebo, west of the previous crystal.</li><li>Talk to Vicendithas (or one of the other NPCs) to the north over the bridge.</li><li>First crystal: to the south-east, next to Vicendithas.</li><li>Second crystal: just north-east of the first, next to Adrasteia and Moia.</li><li>Third crystal: in the Stone Gazebo, west of the previous crystal.</li></ul>",
  },
  {
    text = "Talk to Moia (or one of the other NPCs) and then enter the World Gate to Naragun and do the following:<ul><li>Talk to Hannibus (or one of the other NPCs) just inside the World Gate.</li><li>Continue up the path and talk to Adrasteia or Hannibus.</li><li>Continue along the path and talk to Vicendithas or Moia.</li><li>Continue to the end of the path and talk to Adrasteia (or one of the other NPCs).</li><li>Return to the World Gate.</li><li>You can exit to lobby and back to the world.</li><li>Talk to Moia (or one of the other NPCs).</li><li>You can exit to lobby and back to the world.</li></ul>",
  },
  {
    text = "Enter the World Gate to Kethsi and do the following:<ul><li>Continue the dialogue with Kerapac.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Enter the World Gate again to Freneskae and do the following:<ul><li>Talk to Adrasteia (or one of the other NPCs).</li><li>Descend ledge to the north.</li><li>Talk to Hannibus (or one of the other NPCs).</li><li>Exit rock face to the west.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("Extinction"),
      Action.ConversationHighlight:new("Continue Extinction."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Approach the dark portal.", title = "Final stop" },
  {
    text = "Descend the ledge to the north again.",
    actions = { Action.ConversationHighlight:new("Extinction") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Talk to Hannibus (or one of the other NPCs)." },
  {
    text = "Exit rock face again.",
    actions = { Action.ConversationHighlight:new("Continue Extinction.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Talk to Moia (or one of the other NPCs)  and a cutscene will play.",
    actions = { Action.ConversationHighlight:new("Continue...") },
    postconditions = {
      Condition.ConversationText:new(
        " He chose to stand by so we might face this threat without him. It was by his design, and we grew stronger for it."
      ),
    },
  },
  {
    text = "When you are ready to start, approach dark portal.",
    title = "First visit to Erebus",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = {
      Condition.ConversationText:new("The screen fades to black and back in. Everyone appears at The Cradle."),
    },
  },
  {
    text = "Mine the two shadow animica rocks and crush the resulting shadow animica for a total of 8% attunement. (8% total attunement.)",
  },
  { text = "Capture the two motes of shadow anima for 8% attunement. (16% total attunement.)" },
  { text = "Enter the rift in the east." },
  {
    text = "Capture the skittish mote of shadow anima for 4% attunement. (20% total attunement; you now move slightly faster.)",
  },
  { text = "Capture the telekinetic soul remnant." },
  { text = "Return back to the initial area." },
  { text = "Levitate the boulder using the enduring soul remnant." },
  { text = "Move the boulder out of the way to open the way north." },
  {
    text = "Mine the shadow animica rock and crush the resulting shadow animica for 4% attunement. (24% total attunement.)",
  },
  { text = "Capture the abyssal soul remnant." },
  { text = "Use the abyssal soul remnant to enter the rift in the south." },
  { text = "Capture the regular and skittish motes of shadow anima for 8% attunement. (32% total attunement.)" },
  { text = "Leave via the extra action button or wait for the time to expire" },
  {
    text = "Re-enter Erebus and re-obtain the abyssal soul remnant. The following must be done without exiting, so you may wish to re-enter again after obtaining the remnant to guarantee you have enough time.",
    title = "Second visit to Erebus",
  },
  { text = "Capture the telekinetic soul remnant." },
  { text = "Use the abyssal soul remnant to enter the rift in the west." },
  {
    text = "Capture the regular and skittish motes of shadow anima for 8% attunement. (40% total attunement; you may now stay for a minute and 15 seconds at a time without the handicap.)",
  },
  { text = "Levitate the boulder using the enduring soul remnant." },
  { text = "Move the boulder out of the way to open the way east." },
  {
    text = "Mine the shadow animica rock and crush the resulting shadow animica for 4% attunement. (44% total attunement.)",
  },
  {
    text = "Capture the two regular and one skittish mote of shadow anima for 12% attunement. (56% total attunement.)",
  },
  { text = "Leave via the extra action button or wait for the time to expire" },
  {
    text = "Re-enter Erebus and re-obtain the abyssal and enduring soul remnants. The following must be done without exiting, so you may wish to re-enter again after obtaining the remnants to guarantee you have enough time.",
    title = "Third visit to Erebus",
  },
  { text = "Use the abyssal soul remnant to enter the rift in the west." },
  { text = "Levitate the boulder using the enduring soul remnant." },
  { text = "Move the boulder on one of the three circles on the timeworn lodestone in the east." },
  { text = "Return to the initial area and capture another telekinetic soul remnant." },
  {
    text = "Return to the area with the three circles and levitate the boulder (which has an abyssal soul remnant underneath) using the enduring soul remnant.",
  },
  { text = "Move the boulder to another of the circles of the timeworn lodestone." },
  { text = "Capture the revealed abyssal soul remnant." },
  { text = "Use the abyssal soul remnant to enter the rift in the east." },
  { text = "Capture the mote of shadow anima for 4% attunement. (60% total attunement; you may now run.)" },
  { text = "Capture the telekinetic soul remnant." },
  { text = "Return to the previous area." },
  { text = "Levitate the remaining boulder using the enduring soul remnant." },
  { text = "Move the boulder to the last circle on the timeworn lodestone." },
  {
    text = "Go on top of the timeworn lodestone. You will be teleported to an island and find the relic of the titans. Carrying this causes the western rift in the initial area to be active when entering Erebus, and automatically moves the three boulders on timeworn lodestone when entering any rifts.",
  },
  {
    text = "Mine the shadow animica rock and crush the resulting shadow animica for 4% attunement. (64% total attunement.)",
  },
  { text = "Capture the mote of shadow anima for 4% attunement. (68% total attunement.)" },
  { text = "Leave via the extra action button or wait for the time to expire" },
  {
    text = "Re-enter Erebus and re-obtain the abyssal and enduring soul remnants. The following must be done without exiting, so you may wish to re-enter again after obtaining the remnants to guarantee you have enough time.",
    title = "Fourth visit to Erebus",
  },
  { text = "Navigate back to the island through the timeworn lodestone, ensuring you still have the soul remnants." },
  { text = "Use the dark shadow anima bridge to traverse to the next island. (The anima bridges are one-way only.)" },
  { text = "Capture the two motes of shadow anima for 8% attunement. (76% total attunement.)" },
  { text = "Levitate the boulder next to the Boiling Rift using the enduring soul remnant." },
  { text = "Move the boulder to open the way to the inactive rift." },
  { text = "Enter the rift using the abyssal soul remnant." },
  {
    text = "Mine the shadow animica rock and crush the resulting shadow animica for 4% attunement. (80% total attunement; you may now stay for a minute and 30 seconds at a time without the handicap.)",
  },
  { text = "Capture the two motes of shadow anima for 8% attunement. (88% total attunement.)" },
  { text = "Use the dark shadow anima bridge to traverse to the next island." },
  { text = "Capture the three motes of shadow anima for 12% attunement. (100% total attunement.)" },
  { text = "Harvest the font of unimaginable memories to receive an unimaginable memory." },
  { text = "Re-enter Erebus and re-obtain an abyssal soul remnant." },
  { text = "Navigate back to the Boiling Rift." },
  { text = "Give the unimaginable memory to the Boiling Rift to transform it into a soul geyser." },
  { text = "Activate the soul geyser to travel to the next island." },
  { text = "Use the abyssal soul remnant to activate the inactive rift and travel through it." },
  { text = "Activate the Idol of the Leviathan." },
  { text = "A cutscene will play." },
  { text = "Talk to Moia (or one of the other NPCs).", title = "Iaia" },
  { text = "Take the 5 needle shards on the floor." },
  { text = "Talk to Hannibus." },
  {
    text = "Descend ledge to the north.",
    actions = { Action.ConversationHighlight:new("Extinction") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Talk to Hannibus (or one of the other NPCs).",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Teleport through the portal." },
  { text = "Talk to Moia (or one of the other NPCs)." },
  { text = "Talk to Elder Chahoua to the south." },
  { text = "Talk to the Pastkeeper (north of Elder Chahoua, next to Hannibus)." },
  {
    text = "Talk to the Plantkeeper (north of Seren).<ul><li>Take 4 different seeds from around Iaia.</li><li>Just north-east of the Plantkeeper - flowering marsh plant.</li><li>West of the Plantkeeper - roundleaf marsh plant (slightly south and before the path continues west).</li><li>South-west of the Plantkeeper - marsh-fire plant.</li><li>Just east of Elder Chahoua - longleaf marsh plant.</li><li>Talk to the Plantkeeper.</li><li>Just north-east of the Plantkeeper - flowering marsh plant.</li><li>West of the Plantkeeper - roundleaf marsh plant (slightly south and before the path continues west).</li><li>South-west of the Plantkeeper - marsh-fire plant.</li><li>Just east of Elder Chahoua - longleaf marsh plant.</li></ul>",
  },
  {
    text = "Talk to the Pondkeeper (north-east of the Plantkeeper).<ul><li>Catch 4 fish in the fishing spot just north.</li><li>Talk to the Pondkeeper.</li></ul>",
  },
  {
    text = "Talk to the Craftmaster (south-east of the Pondkeeper).<ul><li>Collect the 3 Ilujankan tools nearby to the east.</li><li>Talk to the Craftmaster.</li></ul>",
  },
  {
    text = "This is the final boss fight. Deaths are unsafe, so focus on surviving.<ul><li>Each phase serves as a checkpoint, so if you teleport or die during the fight, you will resume from the start of the phase you left off at.</li><li>Re-gear if necessary and teleport back to the World Gate to resume, . Enter the World Gate twice.</li></ul>",
    title = "Finale",
    actions = { Action.ConversationHighlight:new("Continue Extinction") },
  },
  { text = "Talk to Moia." },
  { text = "Talk to Seren." },
  { text = "Talk to Vicendithas." },
  {
    text = "Place 2 Needle shards on any of the marked spots.<ul><li>Kill the small shadows that spawn to gain a light core.</li><li>Attack the Dark Lord while having a light core in your inventory and repeat.</li><li>After the Dark Lord is dead, move the unstable energy to the Light Lord until it also dies.</li></ul>",
  },
  {
    text = "Place another Needle shard on any marked spot.<ul><li>Kill the Small, Large, and Enraged muspahs; after each wave, damage Mah's Core (Mah wisp).</li><li>Avoid the fire walls (magic damage), lightning and fire balls (typeless damage).</li><li>Low level players may try to trap the melee muspah behind the core or Seren for semi safe-spotting.</li><li>Avoid the fire walls (magic damage), lightning and fire balls (typeless damage).</li><li>Low level players may try to trap the melee muspah behind the core or Seren for semi safe-spotting.</li></ul>",
  },
  {
    text = "Place another Needle shard on any marked spot.<ul><li>Avoid the fire, and then avoid the eggs (Surge and Bladed Dive can help).</li><li>Focus on surviving for around 2 minutes; you do not need to kill everything.</li><li>Defensive abilities such as Barricade and Reflect can help.</li><li>A powerburst of vitality may be very helpful.</li><li>Try staying in the south-west corner as the eggs won't go there.</li><li>If you survive long enough, you can continue.</li></ul>",
  },
  {
    text = "Head south towards the World Gate. Don't Surge or Bladed Dive as you may encounter a bug and have to talk to Seren again.",
  },
  { text = "Activate the World Gate for a cutscene." },
  { text = "Talk to Seren.", title = "Finishing up", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Return to Senntisten via the ancient door.<ul><li>You are able to use the Pontifex shadow ring teleport option to teleport to the surface entrance.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = {
      Condition.ConversationText:new("The screen fades to black and back in. Everyone appears at The Cradle."),
    },
  },
  { text = "Head south, enter Cathedral door, and talk to Azzanadra." },
  { text = "Talk to Azzanadra again after Saradomin and Armadyl leave." },
  {
    text = "Exit Senntisten (can leave quickly by lobbying or teleporting with your pontifex shadow ring) and talk to Azzanadra outside with 4 backpack space.",
    actions = { Action.ConversationHighlight:new("Continue...") },
    postconditions = {
      Condition.ConversationText:new(
        " He chose to stand by so we might face this threat without him. It was by his design, and we grew stronger for it."
      ),
    },
  },
  { text = "Quest complete!" },
  {
    text = "Unlock the Dream of Iaia and claim warped gem:<ul><li>Talk to Vicendithas in the Effigy Incubator laboratory.</li><li>Talk to Hannibus.</li><li>Enter the hibernation pod to the west near the exit.</li><li>Exit via the hibernation pod.</li><li>Talk to Vicendithas for the warped gem.</li><li>Combine the gem with a ring of vigour in your backpack.  This causes the effects of the ring of vigour to become passive effects for the player.</li><li>Combine the gem with a ring of vigour in your backpack.  This causes the effects of the ring of vigour to become passive effects for the player.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("[Continue.]"),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes, and don't ask me again."),
      Action.ConversationHighlight:new("Yes, and don't ask me again."),
      Action.ConversationHighlight:new("Do you have any Extinction rewards for me?"),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
}

return Quest:new({
  name = "Extinction",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1649030400,
  prereqQuests = { "Sins of the Father", "Eye of Het II", "Sliske's Endgame", "One of a Kind", "Children of Mah" },
})
