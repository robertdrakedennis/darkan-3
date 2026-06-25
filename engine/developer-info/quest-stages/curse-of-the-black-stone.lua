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
    text = "Use the ring of kinship to quickly teleport to Daemonheim. You can get one for free as you'll need to visit Daemonheim many times during the quest.",
    title = "Getting started",
  },
  {
    text = "Talk to Bryll Thoksdottir in Daemonheim beside the rewards trader.",
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Travel to Waiko via Quartermaster Gully in the northern part of Port Sarim.",
    title = "The Jade Spider",
  },
  {
    text = "Talk to Zhuka (bamboo merchant) at the southern part of the marketplace.",
    actions = { Action.ConversationHighlight:new("Ask about the Jade Spider?") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Travel to Whale's Maw by talking to Quartermaster Gully (via travel option and select Whale's Maw)." },
  {
    text = "Talk to Sea Witch Kaula at the northwestern part of the island.",
    actions = { Action.ConversationHighlight:new("Ask about the Jade Spider") },
    postconditions = {
      Condition.ConversationText:new(
        " Yes, the Spider said you would come. Dark days are coming, a shadow threatens all of us, can you feel it? It's like a cold current beneath the surface that threatens to drag us all under."
      ),
    },
  },
  {
    text = "Dig the lump of sand near the western-most ship.",
    actions = { Action.ConversationHighlight:new("Get on with tortle hunting.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Check the tortle to the south to obtain a note from the Jade Spider." },
  { text = "Travel to Tuai Leit via Quartermaster Gully." },
  {
    text = "Speak to Sensei Seaworth , who is south-west at the market.",
    actions = { Action.ConversationHighlight:new("Ask about the Jade Spider.") },
    postconditions = { Condition.ConversationText:new(" Have you tried poking it?") },
  },
  {
    text = "Travel to Goshima via Quartermaster Gully.",
    actions = { Action.ConversationHighlight:new("Search for the Jade spider.") },
  },
  { text = "Kill all 12 crassians on the island." },
  {
    text = "Open and go through the Gate of Goshima and talk to Madame Shih (the Jade Spider) in the easternmost building.",
    actions = { Action.ConversationHighlight:new("She mentioned something") },
    postconditions = {
      Condition.ConversationText:new(
        " There is a being called the Ambassador. He is an ancient creature in service to something dark and terrible. I've been investigating him for some time and I think I've finally found a lead on him. There is a new religious order that has grown on the island of Aminishi. They preach purity and transcendence, but the truth is far far darker than that. I managed to sneak inside, pretended to be one of the faithful, and I rose high enough to join their council. That's when I learned they were dealing with the black stone. Something I have dealt with in the past and it's bad news. Whatever this stone touches, it corrupts and I could already see the tendrils of corruption infesting the Temple of Aminishi. Unfortunately I got careless and someone learned that I wasn't truly faithful. I killed them before they could tell anyone, but it's too risky for me to stay there now. I know that suspicion is creeping in on me. Which is why I need you. See I'm good at warding magic and they used my skills to protect their dungeon from intruders. But what they don't know is that I added a backdoor to the wards. I tied the magic of the wards to the nearby guards so that someone with a powerful enough magical presence...say the World Guardian...would be able to break the wards just by taking out some of the security. Crude, but effective. So that's what I need you to do. I need you to make your way into the Temple of Aminishi and find out what they're doing in the lower levels. You don't need to do it alone, if you've got some friends you can trust, they can join the fight with you. It won't be an easy fight, but you're the World Guardian, I'm sure you can handle it."
      ),
    },
  },
  {
    text = "Complete the Temple of Aminishi.<ul><li>If you have already completed the dungeon, skip the battle.</li><li>If this is your first time completing the dungeon, travel back to Madame Shih after defeating Seiryu the Azure Serpent.</li></ul>",
    actions = { Action.ConversationHighlight:new("Skip the dungeon.") },
    postconditions = {
      Condition.ConversationText:new(
        " What is that stuff? It seems to be the source of all this strangeness. Come on. We should go and catch up with Madame Shih, she's the mystic pirate type, maybe she knows more. There has to be a connection between these dungeons. The presence of that black stone is too much of a coincidence."
      ),
    },
  },
  { text = "Back to Goshima and talk to Madame Shih." },
  {
    text = "Talk to Mr Mordaut in his office in the basement of Varrock Museum that is accessed behind the stairs.<ul><li>Use a 'chipped' Varrock teleport, Dave's spellbook, or the Varrock lodestone to get to the museum quickly.</li></ul>",
    title = "The dungeon",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Go to Daemonheim and Investigate the fireplace to the east of the fountain to find a carved dragonstone (left).",
  },
  {
    text = "Cut your dragonstone to create the right eye, making a carved dragonstone (right) and use it on the fountain.",
    actions = { Action.ConversationHighlight:new("Place the stone in the eye socket.") },
    postconditions = {
      Condition.ConversationText:new("The statue clicks and there is the sound of a mechanism moving to the west."),
    },
  },
  { text = "Enter the crumbling watchtower to the west." },
  { text = "Talk to Hannibus in the west room." },
  {
    text = "Head to the room of the entrance of adamant dragon dungeon in the Brimhaven Dungeon between the fire giants and black demons just north of the iron & steel dragons.<ul><li>Travel to the Karamja lodestone and head to the dungeon or use metallic dragon trinkets's teleport or use a Dungeoneering cape teleport (Brimhaven metal dragon dungeon (0,6)) or use Kethsi ring. Go out to the entrance of the adamant dragon dungeon if using Kethsi ring or dragon trinkets.</li></ul>",
  },
  {
    text = "Enter the adamant dragon dungeon  and investigate the mithril smelter on the most eastern wall to get a metallic object.<ul><li>If you have Eek with you, you may investigate the adamant smelter for some extra dialogue.</li></ul>",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Talk to the adamant dragon in the south-western room to heat the metallic object.",
    actions = {
      Action.ConversationHighlight:new("Can you heat this metal?"),
      Action.ConversationHighlight:new("Your baby is cold, warm her up."),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Player has the metallic object removed from them. Player receives a heated metallic object."
      ),
    },
  },
  {
    text = "With a rope in the backpack, investigate one of the pits north of the entrance.",
    actions = { Action.ConversationHighlight:new("Attach a rope and drop the heated metal into the pit.") },
  },
  {
    text = "Investigate the dragon head near the stairs.",
    actions = { Action.ConversationHighlight:new("Smash the tablet on the teeth.") },
    postconditions = {
      Condition.ConversationText:new(
        " I can see the tablet. Let me take a look. You're not going to like this...it looks like it's sending us in circles. I still can't decipher it. But there's a drawing of something that looks familiar. I think I saw it back at the lab in Daemonheim."
      ),
    },
  },
  { text = "Head back to Kerapac's Lab by entering the crumbling watchtower in Daemonheim.", title = "Kerapac's lab" },
  {
    text = "Investigate the effigy shelves in the small room in the middle of the middle room.",
    actions = { Action.ConversationHighlight:new("I'm looking for clues.") },
    postconditions = { Condition.ConversationText:new(" On where all the high mucky mucks went yeah?") },
  },
  { text = "Referencing the image to the right, activate the following dragonkin statues in any order: 1, 3, 5." },
  {
    text = "Enter the portal in the northern room.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(Otherwise:)(Continues below.)") },
  },
  { text = "Speak to Kerapac." },
  {
    text = "Speak to Bryll Thoksdottir back in Daemonheim.",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Complete the Dragonkin Laboratory.<ul><li>If you have already completed the dungeon, skip the battle.</li><li>If this is your first time completing the dungeon, travel back to Bryll Thoksdottir after defeating the black stone dragon.</li></ul>",
    actions = { Action.ConversationHighlight:new("Skip the dungeon.") },
    postconditions = {
      Condition.ConversationText:new(
        " What is that stuff? It seems to be the source of all this strangeness. Come on. We should go and catch up with Madame Shih, she's the mystic pirate type, maybe she knows more. There has to be a connection between these dungeons. The presence of that black stone is too much of a coincidence."
      ),
    },
  },
  {
    text = "Travel back to Goshima.  (A cutscene will start)",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Head back to Daemonheim and speak to Bryll Thoksdottir.",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Speak to Wizard Myrtle near the Wizards' Tower.<ul><li>Use the traveller's necklace or wicked hood to navigate to the Wizards' Tower, or use the fairy ring DIS to teleport, or run to it from the Draynor lodestone.</li></ul>",
    title = 'Enchanted "vacuum" vial',
    actions = {
      Action.ConversationHighlight:new("Curse of the Black Stone."),
      Action.ConversationHighlight:new("Can you help me get the fish now?"),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = { Condition.ConversationText:new("(Otherwise:)(Continues below.)") },
  },
  { text = "Give Wizard Myrtle any raw fish and a law rune to obtain small raw sillago." },
  { text = "Fill the empty enchanted 'vacuum' vial on the Ice Mountain near Edgeville." },
  { text = "Dry the wet seaweed while standing near the huge fountain at Abbey of St. Elspeth Citharede." },
  { text = "Combine the potion of waterbreathing and drink it." },
  {
    text = "Return to Daemonheim and speak with Hannibus next to the heroes of the Shadow Reef and the Fremennik ship (Shadow reef) on the east coast.<ul><li>If he is not there, instead talk to Bryll Thoksdottir.</li></ul>",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Travel to the Edgeville lodestone to activate the artefact northwest.",
    title = "Preparing for war",
    actions = { Action.ConversationHighlight:new("Yes - Meet with Hannibus.") },
    postconditions = { Condition.ConversationText:new(" Hello old friend. It's been too long.") },
  },
  {
    text = "Talk to Mr Mordaut at the Varrock Museum.",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Travel to the Heart of Gielinor to meet with Vindicta. You may need to leave the dungeon via the lift, and re-enter for the chat option to appear.",
    actions = { Action.ConversationHighlight:new("Yes - Meet with Vindicta.") },
    postconditions = {
      Condition.ConversationText:new(
        " Well met my friend. To see another ilujanka here, on Gielinor and to see you riding a dragon as I once did."
      ),
    },
  },
  {
    text = "Talk to Bryll Thoksdottir near the ring of kinship's teleport location  and then Quartermaster Gully near the Heroes of the Shadow Reef (you may talk to him at Port Sarim).",
    actions = { Action.ConversationHighlight:new("[Curse of the Black Stone]") },
    postconditions = {
      Condition.ConversationText:new(
        " We saved the whole world. Do you realise that? We stopped that creepy Ambassador thing from summoning...something...and destroying everything!"
      ),
    },
  },
  {
    text = "Travel to Waiko and speak to Steven's wife the seagull in the north-east  (top of northernmost rock pillar)",
    title = "Marriage counselling",
  },
  {
    text = "Speak to the 6 seagulls (locations on the map) to find evidence.<ul><li>On a water barrel near the Khan's house, on the eastern part of the island</li><li>In the village, on a water barrel east of the southern pier</li><li>On the southern pier, on a water barrel</li><li>On a crate in the shipwreck, near the western pier</li><li>On a crate on the western pier</li><li>Inside of a cage on the northern pier</li></ul>",
  },
  { text = "Go back to Steven's wife and speak to her." },
  { text = "Talk to Quartermaster Gully at the war table, east of Daemonheim.", title = "Elite forces" },
  { text = "Talk to Bryll Thoksdottir." },
  { text = "Talk to Bosun Higgs." },
  {
    text = "Talk to Lieutenant Crane next to the map table in the Black Knights' Base in the very south of Taverley Dungeon<ul><li>Use the master archaeologist's outfit (0,4) to teleport to Isaura.</li><li>Dungeoneering cape (0,1 - Taverley blue dragon dungeon), head east through the gate, then head south.</li></ul>",
    actions = { Action.ConversationHighlight:new("Evie sent me.") },
    postconditions = {
      Condition.ConversationText:new("Lieutenant Crane leans in close and drops her voice to a whisper."),
    },
  },
  { text = "Run north-west of the Edgeville lodestone to reach the Black Knights' Fortress." },
  { text = "Talk to the fortress guard at the entrance of the fortress while wearing the disguise." },
  { text = "Interact with Tessa's suit of armour in the western barracks room." },
  { text = "Head to the north west corner and head up the stairs." },
  {
    text = "Talk to the slave standing along the north wall on the 1st floor[UK]2nd floor[US] of the Black Knights' Fortress.",
  },
  {
    text = "Talk to Lieutenant Crane.  Wearing a ring of charos (a) allows lying to Lieutenant Crane and skipping the steps to free Tessa.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "If you did not skip freeing Tessa:<ul><li>Return to the fortress and interact with Tessa's suit of armour.</li><li>Head upstairs to smuggle out Tessa.</li><li>Give her the suit of armour.</li><li>Talk to Lieutenant Crane.</li><li>Head back to Daemonheim and speak to Bosun Higgs at the war table.</li><li>Talk to Commandant Bletchley in the Black Knights' Fortress on the 1st floor[UK]2nd floor[US]. He is standing along the north wall.</li><li>Talk to Lieutenant Crane.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Pick lock."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Speak to Bryll Thoksdottir near the war table.<ul><li>If you have already completed the dungeon, skip the battle.</li><li>Otherwise, choose any option for your speech.</li><li>Complete the Shadow Reef.</li><li>If this is your first time completing the dungeon, travel back to Bryll Thoksdottir after defeating the Ambassador.</li></ul>",
    title = "The Shadow Reef",
    actions = {
      Action.ConversationHighlight:new("Skip the dungeon."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Curse of the Black Stone",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1551052800,
  prereqQuests = {
    "Impressing the Locals",
    "Sliske's Endgame",
    "Pieces of Hate",
  },
})
