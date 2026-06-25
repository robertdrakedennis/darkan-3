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
    text = "Talk to Dondakan the Dwarf about cannonballs in the Keldagrim south-west mine.",
    title = "The dwarven scholar",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Why are you firing a cannon at a wall?"),
      Action.ConversationHighlight:new("So why were you trying to get through the rock again?"),
      Action.ConversationHighlight:new("Sounds interesting!"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("I'll be back later.") },
  },
  {
    text = "In the western half of the Keldagrim Palace (the big building in the centre of Keldagrim) ground floor[UK]1st floor[US], talk to the Dwarven Engineer.",
  },
  {
    text = "Teleport to the Falador lodestone and run north-east to the Dwarven mines (stay outside)<ul><li>Teleport using the skull sceptre and run west.</li><li>Or teleport to the Invention Guild and run directly east.</li></ul>",
    title = "Arzinian Avatar",
  },
  {
    text = "Talk to Rolad, who is located in the most north-eastern building near the ladder to the mines. There is a pause in the dialogue; be sure to finish the whole conversation.",
  },
  { text = "Go down the ladder to enter the Dwarven mines." },
  { text = "Kill scorpions in the southeast to get the first page. It will appear in your backpack." },
  {
    text = "Search the mine carts just to the north for the second page. They may also be in the mine carts just south of the ladder you entered the dungeon from.",
  },
  {
    text = "Mine any ore rocks in the mine to find the last page (the rocks in the resource dungeon connected to the mine do not work).",
  },
  {
    text = "Return to Rolad  and read the book. Don't destroy the book yet.<ul><li>If you don't have an ammo mould for the next step, it can be bought from Nulodion for 5 coins. The ammo mould can be stored on your tool belt.</li></ul>",
    actions = { Action.ConversationHighlight:new("Of course.") },
    postconditions = { Condition.ConversationText:new(" Splendid, now, there's a good dwarf!") },
  },
  {
    text = "Return to Dondakan. Talk to him, and then use a gold bar on him.",
    title = "Yellow stones",
    neededItems = { ["Gold bar"] = { quantity = 1 }, ["Ammo mould"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Head to any furnace with an ammo mould and make a golden cannonball.<ul><li>There is a furnace in the west side of the Keldagrim Palace.</li></ul>",
  },
  {
    text = "Return to Dondakan and use the golden cannonball on him.",
    actions = {
      Action.ConversationHighlight:new("Yes, I'm sure this will crack open the rock."),
      Action.ConversationHighlight:new("So you want to... fire me into the rock?"),
      Action.ConversationHighlight:new("I can't argue with that, shoot me in!"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Well, it's not so simple... like I said, I need to make some modifications to my cannon. They're not really designed to fire humans, you know."
      ),
    },
  },
  {
    text = "Read the dwarven lore book to find a piece of base schematics.<ul><li>If you lost the book, you may speak to Rolad.</li></ul>",
    title = "Cannon ergonomics",
    neededItems = { ["Gold bar"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Teleport to the Taverley lodestone, run north and enter the cave by the tree patch." },
  {
    text = "Go to the bar inside the Dwarven Tunnel and talk to Khorvak, a dwarven engineer.",
    actions = { Action.ConversationHighlight:new("No, I've had enough of buying drinks for people!") },
    postconditions = {
      Condition.ConversationText:new(
        " It's always like this... Sure, I'll help you Player, just as soon as you get me a drink!"
      ),
    },
  },
  {
    text = "Talk to the Dwarven Engineer on the west side of the ground floor[UK]1st floor[US] of the Keldagrim Palace (the same one you went to earlier) for another part of the schematics.",
  },
  { text = "Assemble on any of the schematics." },
  { text = "Move around (and rotate if needed) the pieces, referring to finished schematics pictured right." },
  {
    text = "Smith a gold helmet on the anvil in the same room which requires three gold bars by using a gold bar on the anvil.<ul><li>If you are proceeding or completed Legends' Quest.</li></ul>",
    actions = { Action.ConversationHighlight:new("Gold helmet"), Action.ConversationHighlight:new("Gold helmet") },
    postconditions = {
      Condition.ConversationText:new(" You forge a beautiful and very heavy helmet made out of solid gold."),
    },
  },
  {
    text = "Prepare to fight a level 50-56 enemy, along with your gold helmet.",
    title = "Striking gold",
    neededItems = { ["Gold helmet"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Return to Dondakan, equip your gold helmet and ask him to shoot you into the rock.<ul><li>During the cutscene, click directly on the text 'Click to continue', clicking anywhere else will exit and restart the cutscene.</li></ul>",
    actions = { Action.ConversationHighlight:new("You may fire when ready!") },
    postconditions = {
      Condition.ConversationText:new(
        " Ah, such enthusiasm! Okay, here we go, let me have a look at those schematics and we'll get started."
      ),
    },
  },
  { text = "Once inside the cave, do not remove the gold helmet or you will have to restart." },
  {
    text = "Mine at least 6 gold ores, which is required to damage the enemy, and keep them in the backpack. The more ores mined (up to a maximum of 15), the lower its combat level.",
  },
  { text = "Jump-through the first wall of flames and right-click talk on the next ring of fire." },
  { text = "Kill the Arzinian Avatar." },
  { text = "Speak to Dondakan." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Between a Rock...",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1111363200,
  prereqQuests = { "Dwarf Cannon", "Fishing Contest" },
})
