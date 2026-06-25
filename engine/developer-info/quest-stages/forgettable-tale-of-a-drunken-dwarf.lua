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
    text = "Talk to Commander Veldaban west of the Keldagrim bank.",
    title = "Quid pro quo",
    neededItems = {
      ["Beer"] = { quantity = 1 },
      ["Barley malt"] = { quantity = 1 },
      ["Buckets of water"] = { quantity = 1 },
      ["Beer glass"] = { quantity = 1 },
      ["Empty pot"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]<ul><li>If you did not bring beer, go to the bar south of the bank prior to speaking to Commander Veldaban. Purchase 3 beer by speaking to the Barman (one to empty out).</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Sounds like just the job for me!"),
    },
  },
  {
    text = "After the cutscene, speak to the Drunken Dwarf.",
    actions = { Action.ConversationHighlight:new("I need to know about the Red Axe...") },
  },
  {
    text = "Speak to him again with a beer in your inventory.",
    actions = { Action.ConversationHighlight:new("I need to know about the Red Axe...") },
  },
  {
    text = "Collect the four Kelda seeds:<ul><li>The drunken dwarf gives the first seed.</li><li>Fulfil the rowdy dwarf's request for the second seed. He walks a circle around the brewery in east Keldagrim. Give him the random item he asked for.</li><li>If you can't find the item he asks for or it is difficult to obtain (e.g. flier for Bob's Axes), talk to him again multiple times until there is a dialogue option that will change the item he asks for. You may need to lobby.</li><li>If you have accepted a task to fetch an item for the dwarf before this point in the quest, fulfilling that task now may not count towards the quest and you will have to perform another one.</li><li>Talk to Khorvak in a cave north of the Taverley lodestone (under White Wolf Mountain, not on the top). There is a convenient cart to the south of the rowdy dwarf that will take you straight to the cave. The cave is also accessible northeast of the Catherby lodestone.</li><li>Talk to Gauss to have a beer with him, located in the bar south of the Keldagrim bank for the fourth seed.</li><li>If you can't find the item he asks for or it is difficult to obtain (e.g. flier for Bob's Axes), talk to him again multiple times until there is a dialogue option that will change the item he asks for. You may need to lobby.</li><li>If you have accepted a task to fetch an item for the dwarf before this point in the quest, fulfilling that task now may not count towards the quest and you will have to perform another one.</li></ul>",
    actions = { Action.ConversationHighlight:new("No wait, I want to borrow it after all.") },
  },
  { text = "Talk to Rind the gardener, east of the pub where you have just talked to Gauss." },
  { text = "Plant the four seeds in the nearby Kelda Hops Patch." },
  {
    text = "Wait 20 minutes and harvest the Kelda hops.<ul><li>A supreme growth potion (sundry) will not work here. If you choose to spend the waiting time logged out or in lobby, upon login the hop patch may appear weedy as if nothing was planted in it. Simply click the patch and you will receive your hops all the same.</li><li>Optionally, talk to Rind and he will ask you to deliver a letter to Elstan at the allotment patches south of Falador. Upon returning to Rind, he will reward you 2 marrentill seeds.</li></ul>",
  },
  { text = "Go to the brewery, upstairs in the pub, on the eastern side of Keldagrim." },
  {
    text = "Pick up the empty pot in the corner of the room and ask Blandebir to fill it up with ale yeast for 25 coins.",
    actions = {
      Action.ConversationHighlight:new("Can you sell me any ale yeast?"),
      Action.ConversationHighlight:new("Fill your pot"),
    },
  },
  { text = "Select 'Brew' option on fermenting vat and select 'Kelda stout'." },
  {
    text = "Wait up to 5-20 minutes, until you get a notification in your chatbox.<ul><li>'Perhaps I should have a look and see if my Kelda Stout has brewed...'</li></ul>",
  },
  {
    text = "Turn the valve on the connected pipe behind the vat and then use the empty beer glass in your inventory on the barrel to drain it.<ul><li>If you are drinking your beer to empty it, ensure you have 'Destroy empty beer glasses when drinking' disabled in the Settings/Interfaces/Inventory menu, or you will have to buy another.</li></ul>",
  },
  {
    text = "Return to the Drunken Dwarf in the eastern-most of the two houses between the two wells in east Keldagrim and talk to him about the Red Axe.",
    actions = { Action.ConversationHighlight:new("I need to know about the Red Axe...") },
  },
  {
    text = "Talk to the southern most cart conductor that is near the closed tunnel in south-east Keldagrim, the other cart conductor in the area will not work.",
    title = "The drunken dwarf",
    actions = { Action.ConversationHighlight:new("Ask about closed off tunnel.") },
  },
  {
    text = "Talk to the director of the Consortium mining company you joined during The Giant Dwarf. If you don't remember, you can talk to Commander Veldaban and he will tell you.  (There are eight mining companies in the Consortium, each stationed in the upstairs of the marketplace.)",
    actions = { Action.ConversationHighlight:new("Can you help me with a boarded up tunnel?") },
  },
  {
    text = "Un-equip any weapons you are holding, and if you have a familiar out, pick it up. Then ride the southern cart into the tunnel.",
  },
  { text = "Collect the square stones from the box.", title = "Room 1: a chasm" },
  {
    text = "Operate the controls: To place/rotate stones, click once for green, twice for yellow, three times to remove the stone.<ul><li>There will be no confirmation prompt after you've set the route. Just click on the minecart or walk away once you've added the stones.</li><li>Alternatively, place all of the Green stones then the Yellow stones, or vice versa.</li></ul>",
  },
  {
    text = "After each part, ride on the cart, search the newly found box, and take the cart back to the initial platform.",
  },
  {
    text = "After the dialogue, crawl along the southern path, and through the hole to the east.",
    title = "Room 2: eavesdropping",
  },
  { text = "Take the stones from the box and follow the patterns below.", title = "Room 3: another chasm" },
  { text = "Search all three crates with papers on top of them.", title = "Room 4: reports" },
  { text = "Search the bookcases until you find the 'Red Axe Employee Records' book. Read it." },
  { text = "Enter the cave entrance to the east.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Take the stones from the box and follow the patterns below.", title = "Room 5: yet another chasm" },
  { text = "Watch the entire cutscene as you enter the last room." },
  {
    text = "Go back and speak to Commander Veldaban west of Keldagrim bank.",
    title = "Kebabs and beer",
    neededItems = { ["Beer"] = { quantity = 1 }, ["Kebab"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Head to the Laughing Miner Pub in eastern Keldagrim. While inside, consume either your beer or kebab, though you'll need both in your inventory. If you don't have a kebab yet, talk to Kjut in his shop south-east of the northern bridge.<ul><li>You must watch the entire cutscene!</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Forgettable Tale of a Drunken Dwarf",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1122336000,
  prereqQuests = { "The Giant Dwarf", "Fishing Contest" },
})
