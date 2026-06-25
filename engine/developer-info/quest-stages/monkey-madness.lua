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
    text = "Speak to King Narnode Shareen in the Grand Tree. He will give you a gnome royal seal.",
    title = "Missing 10th squad",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Right-click the ladder, select Top Floor, and take the glider from Captain Errdo to Karamja - 'Gandius'." },
  { text = "Head north along the coast to reach the shipyard and try to enter through the gate." },
  { text = "Speak with G.L.O. Caranock in the hut southeast of the gate." },
  { text = "Return to King Narnode. He gives you Narnode's orders." },
  {
    text = "Talk to Daero, found on the east side of the 1st floor[UK] 2nd floor[US] of the Grand Tree. He is at the Blurberry bar platform.",
    title = "Solving puzzle before crashing",
    actions = {
      Action.ConversationHighlight:new("Talk about the journey"),
      Action.ConversationHighlight:new("Return to previous menu"),
      Action.ConversationHighlight:new("Talk about the 10th squad"),
      Action.ConversationHighlight:new("Return to previous menu"),
      Action.ConversationHighlight:new("Talk about Caranock"),
      Action.ConversationHighlight:new("Return to previous menu"),
      Action.ConversationHighlight:new("Leave..."),
      Action.ConversationHighlight:new("Who is it?"),
    },
    postconditions = { Condition.ConversationText:new(" His name is Flight Commander Waydar.") },
  },
  { text = "Talk to Daero." },
  { text = "Begin the puzzle by clicking on the Reinitialisation Panel to the south-east in front of the long pipe." },
  {
    text = "Unscramble the puzzle. Caution: The A3 and A5 pieces look near identical and can make the solving the puzzle difficult (A3 should have a straight cloud bottom on the tile and A5 should have a small curve at the bottom right corner of the tile).<ul><li>You can use this sliding puzzle solver</li><li>Or pay Glough 200,000 coins. He's located in the tree house south-east of the Grand Tree.  Speak to Daero at the Blurberry bar platform to return to the hangar; you will only have to complete the last move in the puzzle if you pay.</li></ul>",
  },
  {
    text = "There is a cutscene when the puzzle is correct. Speak to Daero and then to Waydar  to fly to Crash Island.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new(" As you wish.") },
  },
  { text = "Speak to Lumdo." },
  {
    text = "Speak to Waydar who will convince Lumdo to follow orders.",
    actions = { Action.ConversationHighlight:new("I cannot convince Lumdo to take us to the island…") },
  },
  {
    text = "Recommended: turn off auto-retaliate and leave it off until the final battle (Can be toggled on/off by clicking the button left of the adrenaline gauge on the Action Bar).",
    title = "Ape Atoll",
  },
  {
    text = "On the island, travel west then north to a bamboo gate. Stand there getting pelted by poisoned arrows until you are brought to a prison. You will take some damage and be poisoned.",
  },
  { text = "Picklock the cell and escape the prison. Stay at least 2 squares away from the guards." },
  {
    text = "Stay in the grass to hide from archer monkeys and travel south against the western wall of the Temple of Marimbo.",
  },
  { text = "Go east along the wall of the temple and around the monkey palace. Speak to Garkor." },
  {
    text = "Head to the U-shaped building in the southern side of the monkey village. Enter through the southern door otherwise you will be knocked out.<ul><li>Stay on the dark brown ground at the perimeter of the room.</li></ul>",
    title = "Zooknock",
    neededItems = {
      ["Gold bar"] = { quantity = 1 },
      ["Ball of wool"] = { quantity = 1 },
      ["Monkey bones"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Search the stacked crates two tiles east of the trapdoor for monkey dentures (you can grab another for making a tooth creature).",
  },
  {
    text = "Search the south-easternmost crate (which is a different style and slightly darker brown) to crawl down into the basement - you may fall and take up to 50% of your life points in damage.",
    actions = { Action.ConversationHighlight:new("Yes, I’m sure") },
  },
  {
    text = "In the northwest corner there's a cluster of crates around a stalagmite; search  the crate directly south of the stalagmite for a monkeyspeak amulet mould.",
  },
  {
    text = "Teleport to Al Kharid, go north, talk to Captain Dalbur to glide to the Tree Gnome Stronghold, then talk to Daero at Blurberry's Bar to return to the hangar , Waydar to return to Crash Island , and then Lumdo to return to Ape Atoll .",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Travel west until you see a dungeon on your mini-map. Climb down." },
  {
    text = "Run through the long dungeon.<ul><li>Protect from Melee is highly recommended here, as are antipoison and energy potions. Make sure to have several pieces of food (and/or Enhanced Excalibur) for healing from the traps and mobs.</li></ul>",
    title = "Ape Atoll Dungeon",
  },
  { text = "At the end of the dungeon (the most north-eastern part), speak to Zooknock first." },
  { text = "Use the gold bar on Zooknock." },
  { text = "Use the monkey dentures on Zooknock." },
  {
    text = "Use the monkeyspeak amulet mould on Zooknock. He will give you an enchanted bar and your mould back.<ul><li>If you use the monkey bones on him here you don't need to get new ones when turning in the talisman. Monkey bones from the monkey zombies in the cave do not work. Keep your monkeyspeak amulet mould as it used in Do No Evil.</li><li>Make sure you finish the dialogue after giving him the three items and the bones; otherwise, you will not receive the enchanted bar and your mould back. If you leave the area, you will have to run all the way back through this cave to finish the dialogue to get the enchanted gold bar.</li></ul>",
  },
  {
    text = "Run through the long dungeon.<ul><li>Protect from Melee is highly recommended here, as are antipoison and energy potions. Make sure to have several pieces of food (and/or Enhanced Excalibur) for healing from the traps and mobs.</li></ul>",
  },
  { text = "At the end of the dungeon (the most north-eastern part), speak to Zooknock first." },
  { text = "Use the gold bar on Zooknock." },
  { text = "Use the monkey dentures on Zooknock." },
  {
    text = "Use the monkeyspeak amulet mould on Zooknock. He will give you an enchanted bar and your mould back.<ul><li>If you use the monkey bones on him here you don't need to get new ones when turning in the talisman. Monkey bones from the monkey zombies in the cave do not work. Keep your monkeyspeak amulet mould as it used in Do No Evil.</li><li>Make sure you finish the dialogue after giving him the three items and the bones; otherwise, you will not receive the enchanted bar and your mould back. If you leave the area, you will have to run all the way back through this cave to finish the dialogue to get the enchanted gold bar.</li></ul>",
  },
  {
    text = "Return to Marim (Ape Atoll) (protect from missiles and surge to gate).",
    title = "Monkey's uncle",
    neededItems = {
      ["Monkeyspeak amulet mould"] = { quantity = 1 },
      ["Enchanted bar"] = { quantity = 1 },
      ["Ball of wool"] = { quantity = 1 },
      ["Monkey corpse"] = { quantity = 1 },
      ["Monkey bones"] = { quantity = 1 },
      ["Bananas"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Escape prison and run east into the temple." },
  {
    text = "Take the southern ladder up, the eastern ladder down, and then the trapdoor down into the dungeon.<ul><li>Alternatively, you can kill one of the two gorilla guards blocking the trapdoor and climb down that way.</li></ul>",
  },
  {
    text = "Use the enchanted bar on a pillar of fire and use the amulet on a ball of wool to create an monkeyspeak amulet.",
  },
  { text = "Go to the banana garden west of the prison." },
  {
    text = "Wait for the monkey's aunt to leave and quickly go into the garden through the corridor at its north-east corner.",
  },
  {
    text = "When she returns, run back into the corridor.<ul><li>You can stay in the area with red monkey knife fighters if the guards are called and climb the ladder to the south up and back down again.</li></ul>",
  },
  {
    text = "Equip the monkeyspeak amulet, talk to the monkey child, and talk to him again .",
    actions = { Action.ConversationHighlight:new("Well I’ll be a monkey’s uncle!") },
  },
  {
    text = "Talk to Monkey child again.",
    actions = { Action.ConversationHighlight:new("How many bananas did Aunty want?") },
    postconditions = {
      Condition.ConversationText:new(" Twenty! But I can't count! It's very mean of her, isn't it Uncle?"),
    },
  },
  {
    text = "You will have to wait for The Monkey's Aunt to pass by the child once again before the child gives you the Monkey talisman.<ul><li>If you're obtaining multiple for Recipe for Disaster: Freeing King Awowogei, Do No Evil or Monkey butler then repeat the same step as before  then speak to the Monkey child again.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Wow - can I borrow it?"),
      Action.ConversationHighlight:new("Ok, I promise!"),
      Action.ConversationHighlight:new("I've lost that toy you gave me..."),
    },
  },
  {
    text = "Take your monkey bones and Monkey talisman and use it on Zooknock at the end of the dungeon under Ape Atoll.<ul><li>Make sure to make the standard Monkey greegree for the next steps as other forms will not work.</li><li>Bring other monkey bones and multiple talismans if you want to make more greegrees and  then use your Monkey talisman and different types of monkey bones on Zooknock.</li><li>You can use multiple greegrees on each other to save bank space.</li><li>Bring other monkey bones and multiple talismans if you want to make more greegrees and  then use your Monkey talisman and different types of monkey bones on Zooknock.</li><li>You can use multiple greegrees on each other to save bank space.</li><li>You can use multiple greegrees on each other to save bank space.</li></ul>",
  },
  { text = "Watch the cutscene. Talk to Zooknock to receive your greegree." },
  { text = "Return to Garkor by the kings palace (as a monkey) and speak to him." },
  {
    text = "Talk to the elder guard west of Garkor, they will say you need permission from Kruk. Do not go to Kruk yet.",
  },
  {
    text = "During this section, do not teleport while you're carrying the monkey or you will have to re-obtain the monkey. This includes spirit tree teleportation.",
    title = "King Awowogei",
    neededItems = { ["Monkey greegree"] = { quantity = 1 }, ["Monkeyspeak amulet"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Enter Ardougne Zoo and equip your greegree near the monkey zone. Speak to the monkey minder.<ul><li>If you have other monkeys types on your monkey greegree, make sure to swap it to the Karamjan monkey (brown with red cap). Otherwise the monkey minder will say to you that you aren't getting anywhere near his monkeys.</li></ul>",
  },
  { text = "Talk to an unattackable monkey. Unequip the greegree and talk to the monkey minder again." },
  { text = "Run to the Grand Tree and talk to Daero,  then Waydar,  and finally Lumdo." },
  { text = "Equip the greegree, pass through the gates." },
  {
    text = "Head west from the gate, walk round and up the hill and then cross the bridge to end up on the other side of the hill.",
  },
  {
    text = "Talk to Kruk, who will grant you an audience with Awowogei.<ul><li>You may need to talk to Garkor, then with the Elder Guard outside of the throne room first.</li></ul>",
  },
  { text = "Inside the throne room, talk to Awowogei." },
  { text = "Talk to him again to give him the rescued monkey." },
  { text = "Talk to the southern Elder Guard to exit the throne room." },
  { text = "Talk to Garkor, this will start a cutscene." },
  { text = "Talk to Garkor again after the cutscene for the 10th squad sigil." },
  {
    text = "Right-click 10th squad sigil teleport when you are ready to kill Jungle Demon.<ul><li>If you rub the sigil with your greegree equipped and with a full inventory, your greegree will drop on the ground.</li></ul>",
    title = "Finishing up",
    neededItems = { ["10th squad sigil"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Kill the Jungle demon.<ul><li>Prayer flicking between Protect from Melee and Magic (or Deflect Melee and Magic) can prevent most damage. Begin on Protect From Melee, and as the demon begins the animation for the magic attack swap to Protect from Magic, then wait for the hit and switch back to Protect from Melee. Otherwise, remain on Protect from or Deflect Magic.</li></ul>",
  },
  { text = "Speak to King Narnode in the Grand Tree." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Monkey Madness",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1102291200,
  prereqQuests = { "The Grand Tree", "Tree Gnome Village" },
})
