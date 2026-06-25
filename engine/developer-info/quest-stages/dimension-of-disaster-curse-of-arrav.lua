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
    text = "Talk to Arrav in the centre of the castle to start the quest.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Ernie upstairs in a pottery building south of the east bank in New Varrock and east of Aubury's Rune Shop to get a decorated cooking urn.<ul><li>If you have an active urn in your bank in the real dimension, it might get teleported to you and will count towards the quest progression.</li></ul>",
    title = "Preparing the jar",
    actions = { Action.ConversationHighlight:new("Arrav said you might have a canopic jar.") },
    postconditions = { Condition.ConversationText:new(" Uh…thanks.") },
  },
  {
    text = "Go to the zombie cow pen south-east of the eastern bank, kill roughly 11 cows (the exact number of meat needed varies), and collect the raw undead beef. It needs one backpack space or the urn may not start filling.",
  },
  {
    text = "Chop and light some logs (there is a tree west next to Ernie's house) and cook the meat to fill your cooking urn.<ul><li>Also, a range can be used to cook the raw undead beef.</li><li>If you have a started decorated cooking urn in your real bank, you might need a free inventory space for it to teleport into your inventory when you start cooking.</li><li>Note: There is still a bug with this quest where sometimes you will receive a stackable urn whilst having a partially filled one in your inventory. You may need to go to the real Varrock to check your bank.*</li><li>Note: There is still a bug with this quest where sometimes you will receive a stackable urn whilst having a partially filled one in your inventory. You may need to go to the real Varrock to check your bank.*</li></ul>",
  },
  {
    text = "Use the full cooking urn on Ernie if your setting to teleport full urns automatically is disabled. Otherwise, when full, it will teleport directly to him.",
  },
  {
    text = "Talk to Ernie to get an empty canopic jar.",
    actions = {
      Action.ConversationHighlight:new("Now will you give me the canopic jar?"),
      Action.ConversationHighlight:new("Farewell."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Stop at Aubury's Magic Shop for 1 cosmic and 10 earth runes (a staff of earth also works).<ul><li>Optional: sell the cooked meat to the general store.</li></ul>",
  },
  {
    text = "Buy a diamond from Urist Loric in New Varrock square and cut it.<ul><li>Optional: buy the 3 other gems, cut them, and sell them in the General Store for more Zemomarks than purchase price.</li></ul>",
  },
  { text = "Buy gold ore from Sani at the furnace west of New Varrock square." },
  {
    text = "Make a diamond ring<ul><li>Smelt the ore by clicking on the furnace and then the gold ore under 'Core Ores' on the left side of the smelting menu</li><li>Make a diamond ring by clicking on the furnace and then the gold bar under the 'Casting Metals' menu on the left side of the smelting menu.</li></ul>",
  },
  {
    text = "Enchant the diamond ring to get a ring of life (1 cosmic, 10 earth runes and have standard magic book active).",
  },
  {
    text = "If you don't already have one, buy a watering can at the general store in New Varrock Square and fill it up. Alternatively, you can pick up an already filled watering can in the atrium (centre room) of the castle.<ul><li>Replay: Not needed if you saved vial of purple mist.</li></ul>",
  },
  {
    text = "Go to the church in north-east of New Varrock and speak with Father Lawrence. If you are using Ancient Curse prayers, switch to normal prayers by praying at the altar.",
  },
  { text = "Activate a prayer depending on what Father Lawrence says:" },
  { text = "After activating the correct prayer five times, Father Lawrence will give you the sacred oil." },
  {
    text = "Make sure you have a vial of red mist<ul><li>If you need one:</li><li>Get a vial from the kitchen on the ground floor[UK]1st floor[US] of the castle.</li><li>Head to the 1st floor[UK]2nd floor[US] using the stairs in the south-eastern corner of the fort.</li><li>Kill an armoured zombie and collect the red mist that they drop.</li><li>Get a vial from the kitchen on the ground floor[UK]1st floor[US] of the castle.</li><li>Head to the 1st floor[UK]2nd floor[US] using the stairs in the south-eastern corner of the fort.</li><li>Kill an armoured zombie and collect the red mist that they drop.</li></ul>",
  },
  {
    text = "Go to the south-eastern corner of the ground floor[UK]1st floor[US] in New Varrock Castle and pass through the north red barrier.",
  },
  { text = "Search the corpse of Ambassador Ferrnook to obtain dwellberries." },
  {
    text = "Add the sacred oil, ring of life, and dwellberries to the canopic jar and make a prepared canopic jar.<ul><li>If you accidentally eat the dwellberries, simply search the corpse again.</li></ul>",
  },
  {
    text = "Show the canopic jar to Arrav.",
    actions = {
      Action.ConversationHighlight:new("Where is your heart?"),
      Action.ConversationHighlight:new("How do I get into the vault?"),
      Action.ConversationHighlight:new("How do I get through orange and purple barriers?"),
      Action.ConversationHighlight:new("Farewell."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Talk to Arrav.<ul><li><b>Replay:</b> If you kept the orange and the purple mists, skip ahead to taking the heart after this.</li></ul>",
    title = "Obtaining the heart",
  },
  { text = "Get two vials from the easternmost room and fill them with water." },
  { text = "Talk to Ellamaria in the south-easternmost room near the steel cages to get Delphinium seeds." },
  {
    text = "Rake the Delphinium patch that's east of Arrav in the courtyard and plant the seeds, then water the patch and pick them.",
  },
  { text = "If you don't have three total vials of red mist in your inventory, then collect these now." },
  { text = "Buy an onion from Xuan north-west of the fountain, or pickpocket zombie citizens until you get one." },
  {
    text = "Talk to Thessalia and have her make a yellow dye and a blue dye.",
    actions = {
      Action.ConversationHighlight:new("Arrav said you can make some dye."),
      Action.ConversationHighlight:new("I have the ingredients to make yellow dye."),
      Action.ConversationHighlight:new("I have the ingredients to make blue dye."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Use the yellow and blue dyes on separate vials of red mist to get a vial of orange mist and a vial of purple mist.",
  },
  {
    text = "Go back to the New Varrock castle and enter the north-west room (orange barrier) on the ground floor[UK]1st floor[US] and search the desk to get a code key. Optional: search the chest in this room (90 thieving and lockpick required) to complete an elite task/achievement.",
  },
  {
    text = "Go up one floor, to the 1st floor[UK]2nd floor[US] to the south-east corner by the armoured zombies.<ul><li>Replay: If you kept the dyes/mists, skip to this step.</li><li>In the south-eastern room, search the Crate of decoder strips to get decoder strips if you don't have them and can't remember the combination.</li><li>Replay: Skip this step if you kept the decoder strips or you remember the combination.</li><li>Replay: Skip this step if you kept the decoder strips or you remember the combination.</li></ul>",
  },
  {
    text = "Enter the north-easternmost room on the 1st floor[UK]2nd floor[US] (purple barrier). Search the footlocker near the bed to get a vault key.",
    title = "Taking the heart",
  },
  {
    text = "Head to the south-westernmost room, unlock the large vault door to the west, and enter the room.<ul><li>Before proceeding across the light beams, it is highly recommended that you deface the 3 paintings on the north wall of this room to complete the Bank-Z achievement. If you don't do it now, you need to replay all 4 New Varrock quests in order to complete this New Varrock achievement.</li></ul>",
  },
  {
    text = "Equip the mithril crossbow and mithril grapple, and grapple the chandelier on the ceiling to cross the beams of light. If you've lost your crossbow and grapple, you can reclaim them from your respective gang, Black arm or Phoenix.",
  },
  {
    text = "Fill in the code using the decoding strips and the code you got from Zemouregal's desk in the downstairs study to unlock the door (the code is random for everyone).<ul><li>Replay: The old code is displayed in chatbox, but the final combination is the same every time. If needed, decoder strips can be found in the armoured zombie room to the east of the vault outer door.</li></ul>",
  },
  { text = "Enter the vault and take the heart from the pedestal." },
  { text = "Return to Arrav and talk to him." },
  { text = "Go to the Museum in east New Varrock." },
  {
    text = "Press the big red button (near the rock remains) to distract Orlando Smith (choose any option).<ul><li>Pickpocket the key from Curator Haig Halen</li><li>Then take the shield of Arrav from the display case.</li><li>If you have not completed the elite achievement Dark Imperator-ment, pressing the button again and using the remaining three options will do so (requires 91 Constitution and 75 in each of Attack, Magic and Ranged).</li></ul>",
  },
  { text = "Head back to Arrav and talk to him to give him the shield and Darklight." },
  { text = "Open the throne room door (behind Arrav) and enter the room by pressing <i>OK</i>", title = "The Fight" },
  { text = "Talk to Sharathteerk." },
  {
    text = "Defeat Sharathteerk. You can spare or kill him, it doesn't matter.<ul><li>Pick-up the healing orb to heal yourself back to full health.</li></ul>",
  },
  { text = "Go up the north-east stairs." },
  {
    text = "Defeat Zemouregal<ul><li>Replay: You can choose to skip the fight if you've purchased this option from Aris' Reward Shop.</li><li>For the Moo-er of All Bombs achievement: it is highly recommended that you lure the five cows to him in order to complete the achievement on your first time through so you can skip the fight in the future (assuming you purchased this option from Aris' Reward Shop).</li><li>This can be easily completed in the final phase of the fight, during which he spawns many cows continuously, thus be wary of attacking him in this final phase before getting the achievement.</li><li>You must destroy all the Protection Portals before the cows will damage Zemouregal (and count towards your achievement).</li><li>If you are having troubles remember the following:</li><li>You can use the cows to help you destroy the portals.</li><li>Each portal you have tagged before being exploded will still drop healing orbs, so if HP is a problem you should try to hit as many portals as you can 1-2 times and moving on to the next.</li><li>It is better to die and try the fight again than to accidentally kill Zemouregal because completing this fight will force you to replay through the entirety of Dimension of Disaster again including all four of the quests again.</li><li>This can be easily completed in the final phase of the fight, during which he spawns many cows continuously, thus be wary of attacking him in this final phase before getting the achievement.</li><li>You must destroy all the Protection Portals before the cows will damage Zemouregal (and count towards your achievement).</li><li>If you are having troubles remember the following:</li><li>You can use the cows to help you destroy the portals.</li><li>Each portal you have tagged before being exploded will still drop healing orbs, so if HP is a problem you should try to hit as many portals as you can 1-2 times and moving on to the next.</li><li>It is better to die and try the fight again than to accidentally kill Zemouregal because completing this fight will force you to replay through the entirety of Dimension of Disaster again including all four of the quests again.</li><li>You can use the cows to help you destroy the portals.</li><li>Each portal you have tagged before being exploded will still drop healing orbs, so if HP is a problem you should try to hit as many portals as you can 1-2 times and moving on to the next.</li><li>It is better to die and try the fight again than to accidentally kill Zemouregal because completing this fight will force you to replay through the entirety of Dimension of Disaster again including all four of the quests again.</li></ul>",
  },
  {
    text = "Final cutscene.",
    actions = { Action.ConversationHighlight:new("I guess this is goodbye.") },
    postconditions = { Condition.ConversationText:new(" As are you, Arrav.") },
  },
  {
    text = "Be sure to talk to Aris and complete the main quest to earn your silver pennies.<ul><li>Do not use 'Complete' in her right-click menu. You will not be rewarded your 5 silver pennies.</li></ul>",
    title = "Complete main quest",
  },
}

return Quest:new({
  name = "Dimension of Disaster: Curse of Arrav",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1427068800,
  prereqQuests = {
    "Dimension of Disaster: Coin of the Realm",
    "Dimension of Disaster: Shield of Arrav",
    "Dimension of Disaster: Demon Slayer",
    "Dimension of Disaster: Defender of Varrock",
    "The Curse of Arrav",
  },
})
