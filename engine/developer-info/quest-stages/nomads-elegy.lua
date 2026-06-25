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
    text = "Make sure you have combat equipment (but not your familiar yet) from this point. If you die, you will be returned to Death's office and will have to leave and re-enter via Death's Hourglass (i.e. not War's Retreat) to resume progress with the quest.",
    title = "Beginning",
  },
  {
    text = "Speak to Zimberfizz in the Soul Wars area.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about Nomad's Elegy") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Investigate Nomad's tent to the east." },
  { text = "Investigate the throne, some crystals to the west, and sticky goo south of that." },
  { text = "Investigate the strange emissions to the southwest. You will be sent to Death's Realm." },
  {
    text = "Go through the dialogue and travel through Death's door to the north (the mosaic of Death behind his desk).",
  },
  {
    text = "Speak to Death again.",
    title = "Freeing Zanik",
    actions = {
      Action.ConversationHighlight:new("What do we need to do now?"),
      Action.ConversationHighlight:new("I have to go."),
    },
  },
  {
    text = "Embark on the Bloodstained Jetty to the north-east. (You cannot bring a familiar here.)",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "If you leave the Underworld, you can return by entering Death's Office from Draynor. Do not enter from War's Retreat.",
  },
  {
    text = "Defeat oncoming waves of Bandosians to protect the cave goblins. There are three waves, and progress is saved after each wave. Aggression potions can help, though some of the Bandosians can slip past.",
  },
  {
    text = "Embark on the Dusty Jetty to the north-west.",
    title = "Gathering your allies",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  { text = "Speak to Astrid/Brand." },
  { text = "Speak to Korasi/Jessika in the house north of Bob's Brilliant Axes.", title = "Jessika/Korasi" },
  {
    text = "Travel to the south-west of Lumbridge Castle grounds. Speak to Wizard Grayzag for half of Korasi's/Jessika's memories.",
    actions = {
      Action.ConversationHighlight:new("I'm not Korasi/Jessika."),
      Action.ConversationHighlight:new("My name is Korasi/Jessika."),
      Action.ConversationHighlight:new("Wizard Grayzag?"),
      Action.ConversationHighlight:new("You represent danger."),
      Action.ConversationHighlight:new("I am not afraid of you!"),
      Action.ConversationHighlight:new("Then I won't fail."),
    },
  },
  {
    text = "Travel to the 1st floor[UK] 2nd floor[US] of the Lumbridge Castle wall using the southern gate ladder (not within the main castle keep), and speak to the person you saved in The Void Stares Back to recover the other half of Korasi's/Jessika's memory.",
  },
  { text = "Combine the memory halves." },
  { text = "Return Korasi's/Jessika's completed memory to her north of Bob's Axes." },
  { text = "Talk to Hazelmere on the 1st floor[UK] 2nd floor[US] of Lumbridge castle.", title = "Hazelmere" },
  {
    text = "Find and talk to him again on the 1st floor[UK] 2nd floor[US] and above on the 3rd floor[UK] 4th floor[US] above where the bank normally is by using the ladder on the north side of the building.",
  },
  {
    text = "Climb either of the two tower ladders of the castle gate (not within the main castle keep); he will be on the 2nd floor[UK]3rd floor[US].",
  },
  { text = "Speak to Hazelmere in what would normally be Bob's Axes." },
  { text = "Speak to Hazelmere west of Lumbridge Castle." },
  {
    text = "Speak to Hazelmere next to Astrid/Brand.",
    actions = { Action.ConversationHighlight:new("I need you to help me fight Nomad.") },
  },
  {
    text = "Speak to Xenia in the Lumbridge Castle ground floor[UK] 1st floor[US].",
    title = "Xenia",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Attempt to open the chest in the northern room on the 1st floor[UK] 2nd floor[US].<ul><li>Kill Guilt, pick up the guilt ridden key, then open and search the chest for a Xenia memory half. The key will not drop if you are in a group.</li></ul>",
  },
  {
    text = "Climb to the 2nd floor[UK] 3rd floor[US] and attempt to open the chest in the room.<ul><li>Kill Shame, pick up the shame filled key, then open and search the chest for a other Xenia memory half.</li></ul>",
  },
  { text = "Combine the memory halves and return the completed memory to Xenia." },
  { text = "Talk to Astrid/Brand.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Enter the portal to leave.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Speak to Korasi/Jessika in the house north of Bob's Brilliant Axes." },
  {
    text = "Travel to the south-west of Lumbridge Castle grounds. Speak to Wizard Grayzag for half of Korasi's/Jessika's memories.",
    actions = {
      Action.ConversationHighlight:new("I'm not Korasi/Jessika."),
      Action.ConversationHighlight:new("My name is Korasi/Jessika."),
      Action.ConversationHighlight:new("Wizard Grayzag?"),
      Action.ConversationHighlight:new("You represent danger."),
      Action.ConversationHighlight:new("I am not afraid of you!"),
      Action.ConversationHighlight:new("Then I won't fail."),
    },
  },
  {
    text = "Travel to the 1st floor[UK]2nd floor[US] of the Lumbridge Castle wall using the southern gate ladder (not within the main castle keep), and speak to the person you saved in The Void Stares Back to recover the other half of Korasi's/Jessika's memory.",
  },
  { text = "Combine the memory halves." },
  { text = "Return Korasi's/Jessika's completed memory to her north of Bob's Axes." },
  { text = "Talk to Hazelmere on the 1st floor[UK]2nd floor[US] of Lumbridge castle." },
  {
    text = "Find and talk to him again on the 1st floor[UK]2nd floor[US] and above on the 3rd floor[UK]4th floor[US] above where the bank normally is by using the ladder on the north side of the building.",
  },
  {
    text = "Climb either of the two tower ladders of the castle gate (not within the main castle keep); he will be on the 2nd floor[UK]3rd floor[US].",
  },
  { text = "Speak to Hazelmere in what would normally be Bob's Axes." },
  { text = "Speak to Hazelmere west of Lumbridge Castle." },
  {
    text = "Speak to Hazelmere next to Astrid/Brand.",
    actions = { Action.ConversationHighlight:new("I need you to help me fight Nomad.") },
  },
  {
    text = "Speak to Xenia in the Lumbridge Castle ground floor[UK]1st floor[US].",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Attempt to open the chest in the northern room on the 1st floor[UK]2nd floor[US].<ul><li>Kill Guilt, pick up the guilt ridden key, then open and search the chest for a Xenia memory half. The key will not drop if you are in a group.</li></ul>",
  },
  {
    text = "Climb to the 2nd floor[UK]3rd floor[US] and attempt to open the chest in the room.<ul><li>Kill Shame, pick up the shame filled key, then open and search the chest for a other Xenia memory half.</li></ul>",
  },
  { text = "Combine the memory halves and return the completed memory to Xenia." },
  { text = "Talk to Astrid/Brand.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Enter the portal to leave.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Hazelmere on the 1st floor[UK]2nd floor[US] of Lumbridge castle." },
  {
    text = "Find and talk to him again on the 1st floor[UK]2nd floor[US] and above on the 3rd floor[UK]4th floor[US] above where the bank normally is by using the ladder on the north side of the building.",
  },
  {
    text = "Climb either of the two tower ladders of the castle gate (not within the main castle keep); he will be on the 2nd floor[UK]3rd floor[US].",
  },
  { text = "Speak to Hazelmere in what would normally be Bob's Axes." },
  { text = "Speak to Hazelmere west of Lumbridge Castle." },
  {
    text = "Speak to Hazelmere next to Astrid/Brand.",
    actions = { Action.ConversationHighlight:new("I need you to help me fight Nomad.") },
  },
  {
    text = "Speak to Xenia in the Lumbridge Castle ground floor[UK]1st floor[US].",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Attempt to open the chest in the northern room on the 1st floor[UK]2nd floor[US].<ul><li>Kill Guilt, pick up the guilt ridden key, then open and search the chest for a Xenia memory half. The key will not drop if you are in a group.</li></ul>",
  },
  {
    text = "Climb to the 2nd floor[UK]3rd floor[US] and attempt to open the chest in the room.<ul><li>Kill Shame, pick up the shame filled key, then open and search the chest for a other Xenia memory half.</li></ul>",
  },
  { text = "Combine the memory halves and return the completed memory to Xenia." },
  { text = "Talk to Astrid/Brand.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Enter the portal to leave.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Speak to Xenia in the Lumbridge Castle ground floor[UK]1st floor[US].",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Attempt to open the chest in the northern room on the 1st floor[UK]2nd floor[US].<ul><li>Kill Guilt, pick up the guilt ridden key, then open and search the chest for a Xenia memory half. The key will not drop if you are in a group.</li></ul>",
  },
  {
    text = "Climb to the 2nd floor[UK]3rd floor[US] and attempt to open the chest in the room.<ul><li>Kill Shame, pick up the shame filled key, then open and search the chest for a other Xenia memory half.</li></ul>",
  },
  { text = "Combine the memory halves and return the completed memory to Xenia." },
  { text = "Talk to Astrid/Brand.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Enter the portal to leave.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Plan the war table in the centre area.", title = "Approaching the fortress - Interrogation" },
  {
    text = "Begin interrogating Legio Septimus. The player has the option to kill him outright by repeated torture which will end this section quickly, or to make his willpower low and restore his sanity high, so he answers your questions truthfully which unlocks a shortcut in the battering ram battle later.<ul><li>To kill him outright, select Threaten him, then 'remove' crystals until Legio dies.</li><li>To fully interrogate him instead, threaten him until it is no longer effective. Then use Zanik's Good Cop option and Xenia's Bad Cop option until his sanity is about 50%. Then use Charm to lower his willpower to about 50% as well. Finally raise his sanity back up to about 80% using Zanik's Bad Cop option and Xenia's Good Cop Option. Ask the questions, and his answers should be sane and cooperative. Once answered, choose whether to kill or release him (choice currently has no effect on future events)</li></ul>",
    actions = {
      Action.ConversationHighlight:new("[Ask him about subject]"),
      Action.ConversationHighlight:new("Tell me about Nomad."),
      Action.ConversationHighlight:new("What is Nomad's plan?"),
      Action.ConversationHighlight:new("Who was Nomad's master?"),
      Action.ConversationHighlight:new("Tell me about Nomad's defences."),
      Action.ConversationHighlight:new("How do I defeat him?"),
      Action.ConversationHighlight:new("[Back]"),
      Action.ConversationHighlight:new("Tell me about the Order."),
      Action.ConversationHighlight:new("What is the order up to?"),
      Action.ConversationHighlight:new("What do they need the souls for?"),
      Action.ConversationHighlight:new("Why are you working with Nomad?"),
      Action.ConversationHighlight:new("[Ask something else]"),
      Action.ConversationHighlight:new("[Threaten him]"),
      Action.ConversationHighlight:new("[Threaten him]"),
      Action.ConversationHighlight:new("[Threaten him]"),
      Action.ConversationHighlight:new("[Threaten him]"),
      Action.ConversationHighlight:new("[Threaten him]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
      Action.ConversationHighlight:new("['Remove' crystals]"),
    },
  },
  { text = "Click the war table and select any two companions." },
  {
    text = "Collect 25 wood and 25 metal. You can put both companions on the same material; by the time you complete the other, they will be finished.",
  },
  { text = "Leave through the exit once complete." },
  { text = "Build the battering ram to the north (takes a few minutes while dialogue plays)." },
  { text = "Return to the war table." },
  { text = "Slowly walk the battering ram across the bridge and through the doors." },
  {
    text = "Choose to either head north and assault the fortress head-on, or if you unlocked the information in the earlier interrogation, detour through the east door and enter the secret passage in the north wall of the cave, which will bypass the second half of the assault.",
  },
  { text = "Once inside, you'll have to fight Nomad." },
  { text = "Click the war table and select any two companions." },
  {
    text = "Collect 25 wood and 25 metal. You can put both companions on the same material; by the time you complete the other, they will be finished.",
  },
  { text = "Leave through the exit once complete." },
  { text = "Build the battering ram to the north (takes a few minutes while dialogue plays)." },
  { text = "Return to the war table." },
  { text = "Slowly walk the battering ram across the bridge and through the doors." },
  {
    text = "Choose to either head north and assault the fortress head-on, or if you unlocked the information in the earlier interrogation, detour through the east door and enter the secret passage in the north wall of the cave, which will bypass the second half of the assault.",
  },
  { text = "Once inside, you'll have to fight Nomad." },
  { text = "Click the war table and select any two companions." },
  {
    text = "Collect 25 wood and 25 metal. You can put both companions on the same material; by the time you complete the other, they will be finished.",
  },
  { text = "Leave through the exit once complete." },
  { text = "Build the battering ram to the north (takes a few minutes while dialogue plays)." },
  { text = "Return to the war table." },
  { text = "Slowly walk the battering ram across the bridge and through the doors." },
  {
    text = "Choose to either head north and assault the fortress head-on, or if you unlocked the information in the earlier interrogation, detour through the east door and enter the secret passage in the north wall of the cave, which will bypass the second half of the assault.",
  },
  { text = "Once inside, you'll have to fight Nomad." },
  {
    text = "Dwindle away Nomad's health, and move away from the centre of the bridge to avoid Gielinor's hand. Also move away from shadows that appear on the ground to avoid rapid powerful magic attacks. Use Surge if you can.",
    title = "Fighting Nomad and Gielinor",
  },
  {
    text = "Stand in the middle of each of the three sections in the area and constantly use the 3 abilities made available. Move to the next one when one side is finished.<ul><li>Use Fire Blast when Rorarii are present.</li><li>Use Shield Dome when Gladii are present.</li><li>Use Shadow Stalk when Scutarii are present.</li><li>Every time you attack a Capsarii, it will heal Xenia for a small amount.</li></ul>",
  },
  {
    text = "In addition to the previous phase's attacks, when Nomad teleports you near him and orders you to 'face his wrath', switch to your shield to use Resonance to negate any and all damage from the attack.",
  },
  {
    text = "Go directly north, south, east and west of Gielinor. Each time provoking him and quickly getting out of the way, to then use Scythe on the hand, and finally claim the released spirit.",
  },
  {
    text = "In addition to the previous phases' attacks, Nomad will create a clone of himself when he reaches half health. Kill them both (you might kill Nomad first, in which case you will move on).",
  },
  {
    text = "Quickly and without stopping, run around the area anti-clockwise, releasing the souls as you get in the middle of a group of them. Make sure to get over half of the initial total number of spirits.",
  },
  { text = "The more souls you release, the more powerful the attack reflected onto Gielinor will be." },
  {
    text = "Once all souls have been released, continue circling until Gielinor stops firing the beam, then shield yourself from Gielinor's attack with the ability, in order to reflect the damage onto Gielinor.",
  },
  { text = "You will have to repeat this process a few times." },
  {
    text = "In this phase, Nomad will only be using rapid melee attacks. Kill him by any means, if using Ranged or Magic, then you can run around the room to avoid damage from Nomad, using Surge to gain distance. Watch out for Gielinor's attacks. Protect from Melee or Deflect Melee, especially combined with Devotion, can reduce or negate much of Nomad's damage in this phase, though the speed of his attacks can still be problematic.",
  },
  {
    text = "Stand in the middle of each of the three sections in the area and constantly use the 3 abilities made available. Move to the next one when one side is finished.<ul><li>Use Fire Blast when Rorarii are present.</li><li>Use Shield Dome when Gladii are present.</li><li>Use Shadow Stalk when Scutarii are present.</li><li>Every time you attack a Capsarii, it will heal Xenia for a small amount.</li></ul>",
  },
  {
    text = "In addition to the previous phase's attacks, when Nomad teleports you near him and orders you to 'face his wrath', switch to your shield to use Resonance to negate any and all damage from the attack.",
  },
  {
    text = "Go directly north, south, east and west of Gielinor. Each time provoking him and quickly getting out of the way, to then use Scythe on the hand, and finally claim the released spirit.",
  },
  {
    text = "In addition to the previous phases' attacks, Nomad will create a clone of himself when he reaches half health. Kill them both (you might kill Nomad first, in which case you will move on).",
  },
  {
    text = "Quickly and without stopping, run around the area anti-clockwise, releasing the souls as you get in the middle of a group of them. Make sure to get over half of the initial total number of spirits.",
  },
  { text = "The more souls you release, the more powerful the attack reflected onto Gielinor will be." },
  {
    text = "Once all souls have been released, continue circling until Gielinor stops firing the beam, then shield yourself from Gielinor's attack with the ability, in order to reflect the damage onto Gielinor.",
  },
  { text = "You will have to repeat this process a few times." },
  {
    text = "In this phase, Nomad will only be using rapid melee attacks. Kill him by any means, if using Ranged or Magic, then you can run around the room to avoid damage from Nomad, using Surge to gain distance. Watch out for Gielinor's attacks. Protect from Melee or Deflect Melee, especially combined with Devotion, can reduce or negate much of Nomad's damage in this phase, though the speed of his attacks can still be problematic.",
  },
  {
    text = "In addition to the previous phase's attacks, when Nomad teleports you near him and orders you to 'face his wrath', switch to your shield to use Resonance to negate any and all damage from the attack.",
  },
  {
    text = "Go directly north, south, east and west of Gielinor. Each time provoking him and quickly getting out of the way, to then use Scythe on the hand, and finally claim the released spirit.",
  },
  {
    text = "In addition to the previous phases' attacks, Nomad will create a clone of himself when he reaches half health. Kill them both (you might kill Nomad first, in which case you will move on).",
  },
  {
    text = "Quickly and without stopping, run around the area anti-clockwise, releasing the souls as you get in the middle of a group of them. Make sure to get over half of the initial total number of spirits.",
  },
  { text = "The more souls you release, the more powerful the attack reflected onto Gielinor will be." },
  {
    text = "Once all souls have been released, continue circling until Gielinor stops firing the beam, then shield yourself from Gielinor's attack with the ability, in order to reflect the damage onto Gielinor.",
  },
  { text = "You will have to repeat this process a few times." },
  {
    text = "In this phase, Nomad will only be using rapid melee attacks. Kill him by any means, if using Ranged or Magic, then you can run around the room to avoid damage from Nomad, using Surge to gain distance. Watch out for Gielinor's attacks. Protect from Melee or Deflect Melee, especially combined with Devotion, can reduce or negate much of Nomad's damage in this phase, though the speed of his attacks can still be problematic.",
  },
  {
    text = "Go directly north, south, east and west of Gielinor. Each time provoking him and quickly getting out of the way, to then use Scythe on the hand, and finally claim the released spirit.",
  },
  {
    text = "In addition to the previous phases' attacks, Nomad will create a clone of himself when he reaches half health. Kill them both (you might kill Nomad first, in which case you will move on).",
  },
  {
    text = "Quickly and without stopping, run around the area anti-clockwise, releasing the souls as you get in the middle of a group of them. Make sure to get over half of the initial total number of spirits.",
  },
  { text = "The more souls you release, the more powerful the attack reflected onto Gielinor will be." },
  {
    text = "Once all souls have been released, continue circling until Gielinor stops firing the beam, then shield yourself from Gielinor's attack with the ability, in order to reflect the damage onto Gielinor.",
  },
  { text = "You will have to repeat this process a few times." },
  {
    text = "In this phase, Nomad will only be using rapid melee attacks. Kill him by any means, if using Ranged or Magic, then you can run around the room to avoid damage from Nomad, using Surge to gain distance. Watch out for Gielinor's attacks. Protect from Melee or Deflect Melee, especially combined with Devotion, can reduce or negate much of Nomad's damage in this phase, though the speed of his attacks can still be problematic.",
  },
  {
    text = "In addition to the previous phases' attacks, Nomad will create a clone of himself when he reaches half health. Kill them both (you might kill Nomad first, in which case you will move on).",
  },
  {
    text = "Quickly and without stopping, run around the area anti-clockwise, releasing the souls as you get in the middle of a group of them. Make sure to get over half of the initial total number of spirits.",
  },
  { text = "The more souls you release, the more powerful the attack reflected onto Gielinor will be." },
  {
    text = "Once all souls have been released, continue circling until Gielinor stops firing the beam, then shield yourself from Gielinor's attack with the ability, in order to reflect the damage onto Gielinor.",
  },
  { text = "You will have to repeat this process a few times." },
  {
    text = "In this phase, Nomad will only be using rapid melee attacks. Kill him by any means, if using Ranged or Magic, then you can run around the room to avoid damage from Nomad, using Surge to gain distance. Watch out for Gielinor's attacks. Protect from Melee or Deflect Melee, especially combined with Devotion, can reduce or negate much of Nomad's damage in this phase, though the speed of his attacks can still be problematic.",
  },
  {
    text = "Quickly and without stopping, run around the area anti-clockwise, releasing the souls as you get in the middle of a group of them. Make sure to get over half of the initial total number of spirits.",
  },
  { text = "The more souls you release, the more powerful the attack reflected onto Gielinor will be." },
  {
    text = "Once all souls have been released, continue circling until Gielinor stops firing the beam, then shield yourself from Gielinor's attack with the ability, in order to reflect the damage onto Gielinor.",
  },
  { text = "You will have to repeat this process a few times." },
  {
    text = "In this phase, Nomad will only be using rapid melee attacks. Kill him by any means, if using Ranged or Magic, then you can run around the room to avoid damage from Nomad, using Surge to gain distance. Watch out for Gielinor's attacks. Protect from Melee or Deflect Melee, especially combined with Devotion, can reduce or negate much of Nomad's damage in this phase, though the speed of his attacks can still be problematic.",
  },
  {
    text = "In this phase, Nomad will only be using rapid melee attacks. Kill him by any means, if using Ranged or Magic, then you can run around the room to avoid damage from Nomad, using Surge to gain distance. Watch out for Gielinor's attacks. Protect from Melee or Deflect Melee, especially combined with Devotion, can reduce or negate much of Nomad's damage in this phase, though the speed of his attacks can still be problematic.",
  },
  {
    text = "Choose who ends Gielinor.",
    title = "Aftermath",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Decide Nomad's fate.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "After the cutscene, speak with Death.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Choose Zanik's fate.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Nomad's Elegy",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1455494400,
  prereqQuests = {
    "Dishonour among Thieves",
    "Nomad's Requiem",
    "Heart of Stone",
    "The Mighty Fall",
    "Throne of Miscellania",
    "The Void Stares Back",
    "While Guthix Sleeps",
    "Blood Runs Deep",
  },
})
