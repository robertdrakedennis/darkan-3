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
    text = "Head to Nardah (south of fairy ring DLQ) and talk to Awusah the Mayor in the building east of the fountain.",
    title = "Getting started",
    neededItems = {
      ["Thread"] = { quantity = 1 },
      ["Law rune"] = { quantity = 1 },
      ["Air rune"] = { quantity = 1 },
      ["Telekinetic Grab"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("I am an adventurer in search of quests."),
      Action.ConversationHighlight:new("Any idea how you got this curse?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head to the house north-east of the fountain and talk to Ghaslor the Elder. You should receive a Ballad.",
    actions = {
      Action.ConversationHighlight:new("I am trying to find out the cause of this town's curse."),
      Action.ConversationHighlight:new("River spirits, what are they?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " They're the guardians of the source of the river Elid, they'll know of our curse, seeing as it's related to water we bring from the river. It's said that nothing happens on the river that they don't know about."
      ),
    },
  },
  { text = "Head to the building that has a statuette plinth just west of Ghaslor's house and north of the fountain." },
  {
    text = "Telegrab the ancestral key from the table.<ul><li>You may attach the ancestral key to your steel key ring if you've completed the One Small Favour quest</li></ul>",
  },
  {
    text = "Go to the other side of the building and open the northernmost cupboard and search it for the robe top and bottom.",
  },
  { text = "Fix the top and bottom robes." },
  {
    text = "Head north, past the Dominion Tower until a waterfall is seen.<ul><li>You can teleport to there by using a traveller's necklace to south of the Uzer eagles' eyrie.</li><li>There is a bronze arrow south-east of Dominion Tower and a shortbow north-east of it.</li></ul>",
    title = "The Golems",
    neededItems = { ["Rope"] = { quantity = 1 }, ["Thread"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Climb the waterfall." },
  {
    text = "Equip the Robes of Elidinis and open the door.<ul><li>If the door will not open, make sure you repaired and are wearing both robe pieces, and make sure you have the Ancestral key you tele-grabbed earlier.</li></ul>",
  },
  { text = "Head east to the three doors." },
  {
    text = "Try to open any door, kill the golem that spawns, then enter the room and clear the water channel for each room.<ul><li>If you click the door before the golem's death animation has finished, it will respawn and you will have to fight it again.</li></ul>",
  },
  {
    text = '<table class="wikitable"><tbody><tr><th>Door</th><th>Golem</th><th>Weakness</th><th>Clear water channel via</th></tr><tr><td>South</td><td>White golem</td><td>Stab</td><td>Thieving</td></tr><tr><td>East</td><td>Grey golem</td><td>Slash</td><td>Using a pickaxe (tool belt works)</td></tr><tr><td>North-east</td><td>Black golem</td><td>Crush</td><td>Shoot target with a bow and arrow, magic, or necromancy</td></tr></tbody></table>',
  },
  {
    text = "Head through the door to the north and talk to one of the spirits in the large area.",
    actions = {
      Action.ConversationHighlight:new("I come as an emissary from the people of Nardah."),
      Action.ConversationHighlight:new("Is there anything they can do to get their fountain working again?"),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head back to Nardah.<ul><li>It is possible to be bugged when exiting the cave. To get out of the bug, simply use a Home Teleport spell.</li><li>Amulet of glory teleport to Al Kharid and then taking the magic carpet to Nardah is a quick option.</li></ul>",
  },
  { text = "Talk to Awusah the Mayor.", title = "The Genie" },
  { text = "Take shoes just south from the entrance door inside the Mayor's house." },
  {
    text = "Go outside the house, then remove-sole from the shoes to obtain a sole.<ul><li>Ensure you have a light source or you will not be able to talk to the next NPC without being interrupted.</li></ul>",
  },
  { text = "Head west from the Nardah General Store, past a camp of trainee adventurers and climb-down the crevice." },
  {
    text = "Talk to the Genie , then talk again.<ul><li>If your backpack was full, pick up the Statuette. - There will be room now as you gave the Genie a sole.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I'm after a statue that was thrown down here."),
      Action.ConversationHighlight:new("Maybe I can make a deal for it?"),
      Action.ConversationHighlight:new("Ok I agree to the deal."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head back to Nardah and use the statuette on the plinth in the shrine room, north of the fountain.<ul><li>Optional: Pray at the statue for medium desert task achievement Heathen Idle.</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Spirits of the Elid",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1133740800,
  prereqQuests = {},
})
