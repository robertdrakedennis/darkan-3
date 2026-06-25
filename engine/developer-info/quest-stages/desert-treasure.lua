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
    text = "Talk to Asgarnia Smith for some etchings. He is in the Bedabin Camp south-west of the Shantay Pass.",
    title = "Asgarnia Smith",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Do you have any quests?") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Terry Balando in the Exam Centre.<ul><li>If he offers you a Codex Ultimatus, refuse it or talk to him again.</li></ul>",
    actions = { Action.ConversationHighlight:new("Ask about the Desert Treasure quest.") },
    postconditions = { Condition.ConversationText:new("Player receives translation.") },
  },
  {
    text = "Talk to him again for the Translation.",
    actions = { Action.ConversationHighlight:new("Ask about the Desert Treasure quest.") },
    postconditions = { Condition.ConversationText:new("Player receives translation.") },
  },
  {
    text = "Return to Asgarnia Smith with the book.",
    title = "Bandit Camp",
    neededItems = {
      ["Charcoal"] = { quantity = 1 },
      ["Ashes"] = { quantity = 1 },
      ["Bones"] = { quantity = 1 },
      ["Blood rune"] = { quantity = 1 },
      ["Steel bar"] = { quantity = 1 },
      ["Magic logs"] = { quantity = 1 },
      ["Molten glass"] = { quantity = 1 },
      ["Translation"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Don't read book") },
    postconditions = { Condition.ConversationText:new("Player has translation removed from them.") },
  },
  {
    text = "Talk to Asgarnia again and offer to help him.",
    actions = { Action.ConversationHighlight:new("Help him") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Run south to the Bandit Camp.<ul><li>If you're wearing any Zamorak or Saradomin equipment, bandits become aggressive.</li></ul>",
  },
  {
    text = "Talk to the bartender at the pub for a Bandit's brew.",
    actions = { Action.ConversationHighlight:new("Buy a drink"), Action.ConversationHighlight:new("Buy a beer") },
    postconditions = { Condition.ConversationText:new("Player receives bandit's brew.") },
  },
  {
    text = "Talk to the bartender again.",
    actions = { Action.ConversationHighlight:new("I heard about four diamonds...") },
    postconditions = { Condition.ConversationText:new(" The four diamonds of Azzanadra?How did you hear about them?") },
  },
  {
    text = "Talk to Eblis, by or in the eastern-most building of the Bandit Camp.  (if the first chat option is not 'Ask about Archaeology' )<ul><li>He will not speak to you if you're wearing any Zamorak or Saradomin equipment.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Ask about Desert Treasure"),
      Action.ConversationHighlight:new("Tell me of The four diamonds of Azzanadra"),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("Yes, I will go get those for you."),
      Action.ConversationHighlight:new("Tell me of The four diamonds of Azzanadra"),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("Yes, I will go get those for you."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Use all the items on Eblis, then talk to him again.",
    actions = { Action.ConversationHighlight:new("Ask about Desert Treasure") },
    postconditions = { Condition.ConversationText:new(" Yes I have.") },
  },
  {
    text = "Run south-east, and talk to Eblis by the Bandit Camp lodestone.",
    actions = { Action.ConversationHighlight:new("Ask about Desert Treasure") },
    postconditions = { Condition.ConversationText:new(" Yes I have.") },
  },
  {
    text = "Retrieving the four diamonds can be done in any order. Jump to §Blood diamond Jump to §Ice diamond Jump to §Smoke diamond Jump to §Shadow diamond",
  },
  {
    text = "Enter the pub in Canifis. After a short cutscene, talk to Malak.",
    title = "Blood diamond",
    neededItems = {
      ["Garlic"] = { quantity = 1 },
      ["Silver bar"] = { quantity = 1 },
      ["Spice"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
      ["Air spells"] = { quantity = 1 },
      ["Magic"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("I am looking for a special Diamond..."),
      Action.ConversationHighlight:new("Yes"),
      Action.ConversationHighlight:new("How can I kill Dessous?"),
      Action.ConversationHighlight:new("Actually, I don't need to know anything."),
    },
    postconditions = {
      Condition.ConversationText:new(" As you wish. Come and see me when you have managed to kill Dessous."),
    },
  },
  { text = "Take a silver bar and talk to Ruantun in the Draynor Sewers for a Silver pot." },
  { text = "Take the pot to the High Priest on Entrana for a Blessed pot." },
  { text = "Talk to Malak in Canifis for a blood-filled Blessed pot (this deals 50 damage)." },
  {
    text = "Crush the garlic, use the garlic powder on the blessed pot, then add spice to get a completed Blessed pot.<ul><li>If you add the spice first, the pot will become useless and you must start again from taking the silver bar. Uncrushed garlic cannot be added into the pot.</li></ul>",
  },
  {
    text = "Prepare for a level 84 boss fight.  If you die, you will have to re-do the previous step. Swapping between Protect from Magic and Protect from Melee every game tick results in the boss never damaging you.<ul><li>Note: It is recommended to bank your cake or chocolate bar, as a ghast may turn it into rotten food on the journey.</li></ul>",
  },
  { text = "Run south-east to the Graveyard (map pictured right)." },
  { text = "Use the pot on the tomb, then kill Dessous." },
  { text = "Return to Canifis." },
  { text = "Speak to Malak to receive the Blood diamond." },
  {
    text = "Bank the blood diamond immediately. While holding the blood diamond, a stranger capable of dealing over 5,000 damage per attack can spawn at any time.",
  },
  {
    text = "Prepare for combat by getting plenty of food, a few super restore and super energy potions (even at max combat). Also grab a cake or a chocolate bar if not already in your inventory.<ul><li>Get runes and a magic weapon for fire spells if using Magic. Only fire spells will work if using Magic on Kamil.</li></ul>",
    title = "Ice diamond",
    neededItems = {
      ["Cake"] = { quantity = 1 },
      ["Chocolate cake"] = { quantity = 1 },
      ["Chocolate bar"] = { quantity = 1 },
      ["Cooking apple"] = { quantity = 1 },
      ["Pineapple pizza"] = { quantity = 1 },
      ["Spiked boots"] = { quantity = 1 },
      ["Climbing boots"] = { quantity = 1 },
      ["Trollheim Teleport"] = { quantity = 1 },
      ["Trollheim tablet"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "If you don't have spiked boots, get them first from Dunstan in Burthorpe (requires an iron bar)." },
  {
    text = "If you used your climbing boots to get the spiked boots, then get a new pair of climbing boots from Freda.",
  },
  { text = "Consider using the greater surefooted aura in place of energy potions to keep your run energy up." },
  {
    text = "Travel to Trollheim using Trollheim Teleport.<ul><li>Otherwise, take the path from the Fremennik Slayer Dungeon or the Death Plateau to Troll Stronghold with climbing boots.</li></ul>",
  },
  { text = "Take the path that leads to the snow area north-west of Troll Stronghold." },
  { text = "Talk to the troll child a few squares west of the ice gate." },
  {
    text = "Use a cake or a chocolate bar on him. Talk again to help him.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = {
      Condition.ConversationText:new(
        "(If successful:) ...You successfully picked the first lock! You attempt to pick the second lock...(If successful:) ...You successfully picked the second lock! You attempt to pick the final lock...(If successful:) You manage to pick final lock.Player receives gilded cross.  Inside the chest, hidden under some rags, you find a Gilded Cross.(If unsuccessful:)(Same as below.)(If unsuccessful:)(Same as below.)"
      ),
    },
  },
  { text = "Enable Protect from Melee, and head east through the ice gate." },
  { text = "Kill 5 trolls, then travel through the cave entrance to your east and keep running." },
  { text = "Kill Kamil when he appears." },
  { text = "After you kill Kamil once, continue the path to the north and walk to the ice ledge. You can use Surge." },
  { text = "Equip your spiked boots and climb the path to the top." },
  { text = "Pass through the ice gate and across the bridge." },
  { text = "Break the two ice blocks at the top (by casting a fire spell or choosing the 'smash-ice' option)." },
  { text = "Talk to the trolls, the child will give you the Ice diamond." },
  {
    text = "Bank the ice diamond immediately. While holding the ice diamond, a stranger capable of dealing over 5,000 damage per attack can spawn at any time.",
  },
  {
    text = "Prepare for combat by bringing plenty of food, few super energy potions, melee gear, or water spells if using magic, or ice arrows if using ranged.",
    title = "Smoke diamond",
    neededItems = {
      ["Face mask"] = { quantity = 1 },
      ["Masked earmuffs"] = { quantity = 1 },
      ["Slayer helmet"] = { quantity = 1 },
      ["Ice gloves"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Travel to Pollnivneach." },
  {
    text = "With your face mask and ice gloves equipped enter the Smoke Dungeon.<ul><li>If Smoking Kills is complete the dungeon can be entered from the Pollnivneach well and through the dark stairs to the west.</li><li>Otherwise the player can enter the smokey well which is located west of Pollnivneach.</li><li>Killing a dust devil completes the achievement Drafty in Here.</li><li>Killing a dust devil completes the achievement Drafty in Here.</li></ul>",
  },
  {
    text = "Light the four standing torches, 1 in each corner of the dungeon. This part is timed and torches will go out if you are not fast enough.",
  },
  { text = "Consider using the Greater surefooted aura in place of energy potions to keep your run energy up." },
  {
    text = "Open the burnt chest in the middle of the dungeon to receive a Warm key.<ul><li>Each torch will remain lit for exactly 5 minutes, and you will receive a game message when each one dies out. You will be unable to open the chest if any of the torches have gone out, even if you have lit all 4 torches before attempting.</li></ul>",
  },
  { text = "With the warm key, open the gate on the eastern side of the dungeon." },
  {
    text = "Defeat Fareed for the Smoke diamond.<ul><li>You must use melee, ice arrows, necromancy, or water spells.</li><li>You must wear ice gloves to wield a main-hand weapon while fighting him, including staves and bows. Off-hand weapons are not restricted.</li></ul>",
  },
  {
    text = "Bank the smoke diamond immediately. While holding the smoke diamond, a stranger capable of dealing over 5,000 damage per attack can spawn at any time.",
  },
  {
    text = "Travel to Rasolo (north-west from Ardougne lodestone; see the map to the right).",
    title = "Shadow diamond",
    neededItems = { ["Lockpick"] = { quantity = 1 }, ["Earth spells"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Ask about the Diamonds of Azzanadra"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "(If successful:) ...You successfully picked the first lock! You attempt to pick the second lock...(If successful:) ...You successfully picked the second lock! You attempt to pick the final lock...(If successful:) You manage to pick final lock.Player receives gilded cross.  Inside the chest, hidden under some rags, you find a Gilded Cross.(If unsuccessful:)(Same as below.)(If unsuccessful:)(Same as below.)"
      ),
    },
  },
  { text = "Return to the desert Bandit Camp." },
  {
    text = "Unlock the secure chest in the southern-most tent. You might consume between 10 to 100 lockpicks.<ul><li>Hair clips are very useful here, as you can reclaim them from Merlin for free.</li><li>You can steal from the level 54 Bandits around the camp to get lock picks and antipoison.</li><li>Master thief's lockpick also works and will never break.</li><li>Five-finger discount aura and its variants help.</li></ul>",
  },
  { text = "Prepare for combat before returning the cross to Rasolo who will give you a ring of visibility." },
  {
    text = "Equip the ring, run into the nearby triangular fenced area and climb down the ladder. You may equip any other ring now as this was only required to see the invisible ladder.",
  },
  {
    text = "Run east to Damis and kill him in both phases for the Shadow diamond. See the map below for the path to take.<ul><li>If Damis despawns, run a small distance back from where you came, then run back.</li></ul>",
  },
  {
    text = "Bank the shadow diamond immediately. While holding the shadow diamond, a stranger capable of dealing over 5,000 damage per attack can spawn at any time.",
  },
  {
    text = "Travel to Rasolo (north-west from Ardougne lodestone; see the map to the right).",
    actions = {
      Action.ConversationHighlight:new("Ask about the Diamonds of Azzanadra"),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "(If successful:) ...You successfully picked the first lock! You attempt to pick the second lock...(If successful:) ...You successfully picked the second lock! You attempt to pick the final lock...(If successful:) You manage to pick final lock.Player receives gilded cross.  Inside the chest, hidden under some rags, you find a Gilded Cross.(If unsuccessful:)(Same as below.)(If unsuccessful:)(Same as below.)"
      ),
    },
  },
  { text = "Return to the desert Bandit Camp." },
  {
    text = "Unlock the secure chest in the southern-most tent. You might consume between 10 to 100 lockpicks.<ul><li>Hair clips are very useful here, as you can reclaim them from Merlin for free.</li><li>You can steal from the level 54 Bandits around the camp to get lock picks and antipoison.</li><li>Master thief's lockpick also works and will never break.</li><li>Five-finger discount aura and its variants help.</li></ul>",
  },
  { text = "Prepare for combat before returning the cross to Rasolo who will give you a ring of visibility." },
  {
    text = "Equip the ring, run into the nearby triangular fenced area and climb down the ladder. You may equip any other ring now as this was only required to see the invisible ladder.",
  },
  {
    text = "Run east to Damis and kill him in both phases for the Shadow diamond. See the map below for the path to take.<ul><li>If Damis despawns, run a small distance back from where you came, then run back.</li></ul>",
  },
  {
    text = "Bank the shadow diamond immediately. While holding the shadow diamond, a stranger capable of dealing over 5,000 damage per attack can spawn at any time.",
  },
  {
    text = "Return to the desert Bandit Camp.",
    title = "Stealing the cross",
    neededItems = { ["Lockpick"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Unlock the secure chest in the southern-most tent. You might consume between 10 to 100 lockpicks.<ul><li>Hair clips are very useful here, as you can reclaim them from Merlin for free.</li><li>You can steal from the level 54 Bandits around the camp to get lock picks and antipoison.</li><li>Master thief's lockpick also works and will never break.</li><li>Five-finger discount aura and its variants help.</li></ul>",
  },
  { text = "Prepare for combat before returning the cross to Rasolo who will give you a ring of visibility." },
  {
    text = "Equip the ring, run into the nearby triangular fenced area and climb down the ladder. You may equip any other ring now as this was only required to see the invisible ladder.",
  },
  {
    text = "Run east to Damis and kill him in both phases for the Shadow diamond. See the map below for the path to take.<ul><li>If Damis despawns, run a small distance back from where you came, then run back.</li></ul>",
  },
  {
    text = "Bank the shadow diamond immediately. While holding the shadow diamond, a stranger capable of dealing over 5,000 damage per attack can spawn at any time.",
  },
  {
    text = "Prepare for combat before returning the cross to Rasolo who will give you a ring of visibility.",
    title = "Getting the diamond",
    neededItems = {
      ["Gilded cross"] = { quantity = 1 },
      ["Combat equipment"] = { quantity = 1 },
      ["Earth spells"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Equip the ring, run into the nearby triangular fenced area and climb down the ladder. You may equip any other ring now as this was only required to see the invisible ladder.",
  },
  {
    text = "Run east to Damis and kill him in both phases for the Shadow diamond. See the map below for the path to take.<ul><li>If Damis despawns, run a small distance back from where you came, then run back.</li></ul>",
  },
  {
    text = "Bank the shadow diamond immediately. While holding the shadow diamond, a stranger capable of dealing over 5,000 damage per attack can spawn at any time.",
  },
  {
    text = "Make sure your weight is less than 5 kg or you'll keep failing traps.<ul><li>Boots of lightness and any other weight reducing equipment helps.</li></ul>",
    title = "Jaldraocht Pyramid",
  },
  { text = "Return to the Bandit Camp, and head directly south of Eblis to the pyramid." },
  {
    text = "Use the diamonds on the obelisks surrounding the pyramid.<ul><li>Blood diamond in the north-west obelisk.</li><li>Shadow diamond in the south-west obelisk.</li><li>Ice diamond in the south-east obelisk.</li><li>Smoke diamond in the north-east obelisk.</li></ul>",
  },
  { text = "Climb down the ladder at the top of the pyramid." },
  {
    text = "Run through the pyramid using the map. Don't stand still, or you may be transported back to the start.<ul><li>There's a possibility that scarabs spawn in Azzanadra's room so bring a weapon, antipoison, and some more food just in case.</li></ul>",
  },
  { text = "Talk to Azzanadra." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Desert Treasure",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1113782400,
  prereqQuests = { "The Dig Site", "Troll Stronghold", "Morytania" },
})
