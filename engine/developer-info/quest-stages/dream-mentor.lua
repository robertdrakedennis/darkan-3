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
    text = "Enter the mine in the north-east area of Lunar Isle.",
    title = "A helping hand",
  },
  { text = "Crawl-through the cave entrance (quest minimap icon)." },
  {
    text = "Talk to the fallen man (close the interface when it appears).",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Starting with one of your 7 item foods, feed him 4 food while cycling through your different foods.<ul><li>Starting with a food item of which you brought 7 items will ensure you do not run out of variety.</li></ul>",
  },
  {
    text = "Talk to him again.",
    actions = { Action.ConversationHighlight:new("Just don't worry."), Action.ConversationHighlight:new("Of course.") },
  },
  { text = "Feed him 4 more food as before." },
  {
    text = "Talk to repeatedly to raise his spirits:<ul><li>Of the four chat options, choose the one that appears on the table below.</li><li>Select the response according to the table.</li><li>Repeat until a short cutscene plays and he sits up.</li></ul>",
    title = "Raising his spirit",
  },
  {
    text = '<table class="wikitable sortable mw-collapsible jquery-tablesorter mw-made-collapsible"><thead><tr><th class="headerSort" tabindex="0" role="columnheader button" title="Sort ascending">Answer #1</th><th class="headerSort" tabindex="0" role="columnheader button" title="Sort ascending"><button type="button" class="mw-collapsible-toggle mw-collapsible-toggle-default mw-collapsible-toggle-expanded" aria-expanded="true" tabindex="0"><span class="mw-collapsible-text">hide</span></button>Answer #2</th></tr></thead><tbody><tr style=""><td>You seem like a nice guy.</td><td>Just being honest.</td></tr><tr style=""><td>When we get out of here I\'ll buy you a drink!</td><td>Whatever and wherever you want - my treat.</td></tr><tr style=""><td>I\'m very impressed you managed to get into this cave.</td><td>I would have given up personally.</td></tr><tr style=""><td>You\'ll survive this easily.</td><td>Think of all the places you can visit when you get out!</td></tr><tr style=""><td>What are you going to do when you get out of here?</td><td>That\'s up to you. You could travel with me!</td></tr><tr style=""><td>It\'s a good thing you have me to look after you.</td><td>Not that I\'m bragging or anything.</td></tr><tr style=""><td>Not long now and you\'ll be back on your feet!</td><td>On whether you mind me helping you further.</td></tr><tr style=""><td>You\'re sounding much better.</td><td>If you need anything, just let me know.</td></tr><tr style=""><td>It\'s quite cosy in here.</td><td>The perfect environment for getting back on your feet!</td></tr><tr style=""><td>You\'re very safe in this little cave.</td><td>The suqah will never fit through that tunnel.</td></tr><tr style=""><td>Just don\'t worry.</td><td>Of course.</td></tr><tr style=""><td>You\'re looking better now.</td><td>Well, you look and sound more lively.</td></tr><tr style=""><td>Are you looking forward to getting out?</td><td>That\'s the spirit!</td></tr><tr style=""><td>Tell me a bit about yourself.</td><td>Fishing!</td></tr></tbody><tfoot></tfoot></table>',
  },
  { text = "Feed him 6 more food." },
  { text = "Talk to him and raise his spirit as before until he stands up." },
  { text = "Exit the cave and climb up the ladder.<ul><li>Or just use the Lunar Isle lodestone</li></ul>" },
  {
    text = "Talk to 'Bird's-Eye' Jack in the bank.  You will need 1 free inventory space for Cyrisus's chest.",
    actions = { Action.ConversationHighlight:new("Cyrisus in the mine") },
  },
  {
    text = "Select the equipment he needs. The equipment is usually, but not always, dependent on your highest combat stats (Magic, Ranged, Melee):<ul><li>Melee: Dragon helm, Abyssal whip, Ahrim's robe top, Ahrim's robe skirt, Ranger boots</li><li>Ranged: Splitbark Helm, Karil's top, Torag's platelegs, Adamant boots, Magic shortbow</li><li>Magic: Robin Hood hat, Dragon Chainbody, Black dragonhide chaps, Infinity boots, Ancient staff</li></ul>",
  },
  { text = "You can use the NPC Contact spell to ensure you have the right equipment." },
  {
    text = "Return and talk to Cyrisus for a cutscene.",
    actions = { Action.ConversationHighlight:new("Talk about the Armament") },
  },
  { text = "Right-click 'inspect' and fill the remaining stats until he tells you that he is ok (if not already)." },
  {
    text = "Talk to the Oneiromancer at the astral altar (most south-eastern part of the Lunar Isle) for a dream vial (herb).",
    title = "Dream potion",
    actions = { Action.ConversationHighlight:new("Cyrisus") },
  },
  { text = "Change your spellbook as necessary if you wish to use magic in the final fight." },
  {
    text = "Use an astral rune on an anvil (there's one on Lunar Isle in a small building to the south), then grind the shards. Add it to the potion.",
  },
  {
    text = "Prepare for 4 fights (below level 100). Prayers and emergency teleports won't work. Neither will familiars.",
  },
  { text = "Light the Ceremonial Brazier in the long hall building, near the lodestone.", title = "Dreamland" },
  {
    text = "Talk to Cyrisus (dismiss any followers before).<ul><li>Do not click away of the chat or you will be teleported out</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  {
    text = "During the fight, you can leave at any time by using the pedestal.<ul><li>Kill The Inadequacy</li><li>Kill The Everlasting (can safespot by the pedestal)</li><li>Kill The Untouchable (can safespot by the pedestal)</li><li>Kill The Illusive</li></ul>",
  },
  { text = "Return to the Oneiromancer.", actions = { Action.ConversationHighlight:new("Cyrisus") } },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Dream Mentor",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1179187200,
  prereqQuests = { "Lunar Diplomacy" },
})
