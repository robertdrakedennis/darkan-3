local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

-- Sir Amik Varze and Sir Tiffy Cashien are shared Models in Models.npcs

--#region npcs
local msHynnTerprett = Model.new(7032, {
  [2013] = Vertex.new(24, 738, -38, 30, 29, 27),
  [2037] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [2059] = Vertex.new(-2, 716, -57, 106, 78, 54),
  [2065] = Vertex.new(2, 716, -57, 106, 78, 54),
  [2069] = Vertex.new(6, 716, -52, 106, 78, 54),
})

local sirLeye = Model.new(6954, {
  [1738] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [1743] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [1746] = Vertex.new(2, 725, -59, 106, 78, 54),
  [1748] = Vertex.new(7, 724, -51, 106, 78, 54),
  [5146] = Vertex.new(26, 741, -37, 76, 70, 69),
})

local sirSpishyus = Model.new(1188, {
  [75] = Vertex.new(156, 298, -224, 253, 252, 188, 0.1843),
  [512] = Vertex.new(176, 266, 216, 253, 252, 188, 0.1843),
  [536] = Vertex.new(156, 298, -224, 253, 252, 188, 0.1843),
  [540] = Vertex.new(156, 298, -224, 253, 252, 188, 0.1843),
  [551] = Vertex.new(-160, 278, -212, 253, 252, 188, 0.1843),
})

local missCheevers = Model.new(5451, {
  [1425] = Vertex.new(24, 738, -38, 30, 29, 27),
  [1449] = Vertex.new(-24, 738, -38, 30, 29, 27),
  [1471] = Vertex.new(-2, 716, -57, 106, 78, 54),
  [1477] = Vertex.new(2, 716, -57, 106, 78, 54),
  [1481] = Vertex.new(6, 716, -52, 106, 78, 54),
})

local sirKuamFerentse = Model.new(7206, {
  [1900] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [1908] = Vertex.new(2, 725, -59, 106, 78, 54),
  [1910] = Vertex.new(7, 724, -51, 106, 78, 54),
  [6681] = Vertex.new(-65, 670, 50, 89, 14, 7),
  [6712] = Vertex.new(65, 670, 50, 89, 14, 7),
})

-- I missed lady table... lol

--#endRegion

--#region Objects
local chicken = Model.new(1200, {
  [18] = Vertex.new(-99, 179, -38, 95, 72, 28),
  [30] = Vertex.new(-118, 134, -75, 95, 72, 28),
  [756] = Vertex.new(-118, 135, 75, 95, 72, 28),
  [762] = Vertex.new(-118, 135, -75, 95, 72, 28),
  [765] = Vertex.new(-99, 181, 38, 95, 72, 28),
})

local grainSack = Model.new(468, {
  [24] = Vertex.new(-18, 253, -80, 168, 168, 168),
  [158] = Vertex.new(-173, 185, -68, 79, 68, 50),
  [275] = Vertex.new(-64, 241, 163, 79, 68, 50),
  [378] = Vertex.new(126, 0, 64, 68, 56, 35),
  [380] = Vertex.new(126, 0, 64, 68, 56, 35),
})

local fox = Model.new(1500, {
  [166] = Vertex.new(-20, 416, -180, 107, 64, 21),
  [167] = Vertex.new(-44, 448, -188, 107, 64, 21),
  [172] = Vertex.new(44, 448, -188, 107, 64, 21),
  [173] = Vertex.new(20, 416, -180, 107, 64, 21),
  [527] = Vertex.new(44, 448, -188, 107, 64, 21),
})

local bronzeMaceStatue = Model.new(2367, {
  [2349] = Vertex.new(8, 828, -48, 141, 89, 42),
  [2351] = Vertex.new(4, 872, -52, 141, 89, 42),
  [2357] = Vertex.new(-8, 872, -52, 141, 89, 42),
  [2361] = Vertex.new(-16, 828, -48, 141, 89, 42),
  [2363] = Vertex.new(-20, 872, -52, 141, 89, 42),
})
local silveMaceStatue = Model.new(2367, {
  [2349] = Vertex.new(8, 828, -48, 180, 169, 169),
  [2351] = Vertex.new(4, 872, -52, 180, 169, 169),
  [2357] = Vertex.new(-8, 872, -52, 180, 169, 169),
  [2361] = Vertex.new(-16, 828, -48, 180, 169, 169),
  [2363] = Vertex.new(-20, 872, -52, 180, 169, 169),
})
local goldMaceStatue = Model.new(2367, {
  [2349] = Vertex.new(8, 828, -48, 222, 163, 44),
  [2351] = Vertex.new(4, 872, -52, 222, 163, 44),
  [2357] = Vertex.new(-8, 872, -52, 222, 163, 44),
  [2361] = Vertex.new(-16, 828, -48, 222, 163, 44),
  [2363] = Vertex.new(-20, 872, -52, 222, 163, 44),
})

local goldGreatAxe = Model.new(2475, {
  [2047] = Vertex.new(-156, 128, -184, 222, 163, 44),
  [2048] = Vertex.new(128, 128, -156, 222, 163, 44),
  [2049] = Vertex.new(152, 128, -184, 222, 163, 44),
})
local silverGreatAxe = Model.new(2475, {
  [2047] = Vertex.new(-156, 128, -184, 180, 169, 169),
  [2048] = Vertex.new(128, 128, -156, 180, 169, 169),
  [2049] = Vertex.new(152, 128, -184, 180, 169, 169),
})
local bronzeGreatAxe = Model.new(2475, {
  [2047] = Vertex.new(-156, 128, -184, 141, 89, 42),
  [2048] = Vertex.new(128, 128, -156, 141, 89, 42),
  [2049] = Vertex.new(152, 128, -184, 141, 89, 42),
})

local goldHalberd = Model.new(2367, {
  [2345] = Vertex.new(36, 884, 12, 195, 148, 58),
  [2349] = Vertex.new(28, 840, 16, 195, 148, 58),
  [2351] = Vertex.new(24, 884, 12, 195, 148, 58),
  [2355] = Vertex.new(16, 840, 16, 195, 148, 58),
  [2366] = Vertex.new(8, 884, 12, 195, 148, 58),
})
local silverHalberd = Model.new(2367, {
  [2345] = Vertex.new(36, 884, 12, 166, 153, 153),
  [2349] = Vertex.new(28, 840, 16, 166, 153, 153),
  [2351] = Vertex.new(24, 884, 12, 166, 153, 153),
  [2355] = Vertex.new(16, 840, 16, 166, 153, 153),
  [2366] = Vertex.new(8, 884, 12, 166, 153, 153),
})
local bronzeHalberd = Model.new(2367, {
  [2345] = Vertex.new(36, 884, 12, 135, 75, 11),
  [2349] = Vertex.new(28, 840, 16, 135, 75, 11),
  [2351] = Vertex.new(24, 884, 12, 135, 75, 11),
  [2355] = Vertex.new(16, 840, 16, 135, 75, 11),
  [2366] = Vertex.new(8, 884, 12, 135, 75, 11),
})

-- The sword statue models are broken when I did this quest.
local goldSword = Model.new(2298, {
  [1103] = Vertex.new(24, 959, -48, 142, 140, 130),
  [1109] = Vertex.new(12, 959, -48, 142, 140, 130),
  [1115] = Vertex.new(-4, 959, -48, 142, 140, 130),
  [1121] = Vertex.new(-16, 959, -48, 142, 140, 130),
  [1125] = Vertex.new(-24, 915, -52, 142, 140, 130),
})

local metalSpade = Model.new(237, {
  [4] = Vertex.new(-152, 24, 216, 85, 82, 78),
  [5] = Vertex.new(-136, 20, 216, 85, 82, 78),
  [27] = Vertex.new(-216, 20, 136, 85, 82, 78),
  [100] = Vertex.new(-192, 24, 184, 106, 101, 97),
  [101] = Vertex.new(-152, 24, 216, 106, 101, 97),
})

-- bunsen burner cannot be selected since its part of a larger combined model.

--#endRegion

--#region items
local foxWorn = Model.new(1500, {
  [166] = Vertex.new(-20, 416, -180, 122, 72, 24),
  [167] = Vertex.new(-44, 448, -188, 122, 72, 24),
  [172] = Vertex.new(44, 448, -188, 122, 72, 24),
  [520] = Vertex.new(-44, 448, -188, 122, 72, 24),
  [527] = Vertex.new(44, 448, -188, 122, 72, 24),
})

local grainWorn = Model.new(468, {
  [156] = Vertex.new(-84, 312, 212, 79, 68, 50),
  [275] = Vertex.new(-84, 312, 212, 79, 68, 50),
  [282] = Vertex.new(-84, 312, 212, 79, 68, 50),
  [378] = Vertex.new(164, 0, 84, 68, 56, 35),
  [380] = Vertex.new(164, 0, 84, 68, 56, 35),
})

local chickenWorn = Model.new(1167, {
  [3] = Vertex.new(44, 184, 68, 88, 67, 26),
  [54] = Vertex.new(-40, 184, 68, 88, 67, 26),
  [741] = Vertex.new(44, 188, 68, 88, 67, 26),
  [744] = Vertex.new(-40, 188, 68, 88, 67, 26),
  [1159] = Vertex.new(16, 172, -104, 0, 0, 0),
})

local aceticAcid = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 91, 83, 46, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 91, 83, 46, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 91, 83, 46, 0.8745),
  }),
})

local vialOfLiquid = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 93, 99, 147, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 93, 99, 147, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 93, 99, 147, 0.8745),
  }),
})

local cupricSulphate = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 109, 143, 110, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 109, 143, 110, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 109, 143, 110, 0.8745),
  }),
})

local tinOrePowder = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 90, 83, 82, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 90, 83, 82, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 90, 83, 82, 0.8745),
  }),
})

local cupricOrePowder = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 158, 82, 31, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 158, 82, 31, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 158, 82, 31, 0.8745),
  }),
})

-- This is fully identical to an empty vial.
local nitrousOxide = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [70] = Vertex.new(-4, 0, -36, 133, 135, 146, 0.4980),
    [74] = Vertex.new(12, 12, -40, 133, 135, 146, 0.4980),
    [75] = Vertex.new(4, 0, -36, 133, 135, 146, 0.4980),
    [76] = Vertex.new(4, 0, 36, 133, 135, 146, 0.4980),
  }),
})

local sodiumChloride = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 159, 159, 145, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 159, 159, 145, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 159, 159, 145, 0.8745),
  }),
})

local gypsum = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 122, 112, 112, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 122, 112, 112, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 122, 112, 112, 0.8745),
  }),
})

local cupricSulphate = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 109, 143, 110, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 109, 143, 110, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 109, 143, 110, 0.8745),
  }),
})

local aceticAcid = Model.multi({
  Models.items["vial shared"],
  Model.new(240, {
    [64] = Vertex.new(-4, 0, 36, 91, 83, 46, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 91, 83, 46, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 91, 83, 46, 0.8745),
  }),
})

local metalSpadeBunsoned = Model.new(141, {
  [3] = Vertex.new(-216, 20, 136, 106, 101, 97),
  [4] = Vertex.new(-152, 24, 216, 85, 82, 78),
  [5] = Vertex.new(-136, 20, 216, 85, 82, 78),
  [27] = Vertex.new(-216, 20, 136, 85, 82, 78),
  [74] = Vertex.new(-152, 24, 216, 106, 101, 97),
})

-- Cake tin might work here.
local tin = Model.new(228, {
  [38] = Vertex.new(-76, 56, -36, 55, 50, 50),
  [41] = Vertex.new(-76, 56, -36, 55, 50, 50),
  [45] = Vertex.new(-76, 56, -36, 55, 50, 50),
  [48] = Vertex.new(-76, 56, -36, 55, 50, 50),
  [51] = Vertex.new(-76, 56, -36, 55, 50, 50),
})

local tinWithJuice = Model.new(168, {
  [49] = Vertex.new(-68, 36, 32, 154, 140, 117),
  [51] = Vertex.new(-68, 36, -32, 154, 140, 117),
  [52] = Vertex.new(-32, 36, 68, 154, 140, 117),
  [54] = Vertex.new(-68, 36, 32, 154, 140, 117),
  [55] = Vertex.new(-68, 36, -32, 154, 140, 117),
})
-- missed the bronzeKey at the end here.

--#endRegion

---@type QuestStep[]
local steps = {
  {
    text = "Teleport to Falador.",
    title = "Getting started",
  },
  { text = "Go to the White Knights' Castle, and head up to the 2nd floor[UK]3rd floor[US] of the western tower." },
  {
    text = "Talk to Sir Amik Varze.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("I seek a quest!") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Sir Tiffy Cashien without any items equipped or in the inventory. He can be found in the Falador Park.",
    title = "The puzzles",
    actions = { Action.ConversationHighlight:new("Yes, let's go!") },
  },
  { text = "Optionally, sit on the bench next to Sir Tiffy Cashien to complete the achievement: Tiffy Time." },
  {
    text = "In this room there will be eleven statues when there should be twelve. The goal is to study the statues then, when the twelfth one is returned, pick the one that was originally missing.<ul><li>Easy method: look for the colour (bronze, silver, gold) of statue that only has 3 statues instead of 4, then note which weapon of that colour is missing out of the 4 possibilities (sword, halberd, great axe, or mace.)</li><li>Alternative method: the moment it shows 12 statues, take a screenshot. Then open the screenshot and compare which was missing and click it.</li></ul>",
    title = "Lady Table",
  },
  { text = "Find the statue that wasn't there originally and touch it." },
  {
    text = "As a female character, kill Sir Leye.<ul><li>If you have not visited the Makeover Mage before starting the quest, you can change your gender via the Customisation interface, under the Appearance tab.</li></ul>",
    title = "Sir Kuam Ferentse/Sir Leye",
  },
  {
    text = "In this room, the chicken, grain, and the fox must all be transported to the other side of the bridge in a specific order.",
    title = "Sir Spishyus",
  },
  {
    text = "Take the chicken across and put it down. In order to put down an object, open your equipment panel and click on the equipped object.",
  },
  { text = "Take the fox across and put it down. Grab the chicken and return back." },
  { text = "Drop the chicken and take the Grain across." },
  { text = "Drop the grain and return back." },
  { text = "Take the chicken across and drop it." },
  { text = "Search the most south-western bookshelf for a knife.", title = "Miss Cheevers - Collect items" },
  { text = "Search all four of the most southern shelves with vials, take all of them." },
  { text = "Search the north-eastern middle-sized crate next to the chest for a tin." },
  { text = "Search the four northern shelves for the remaining vials, take all of them." },
  { text = "Pick up the metal spade on the table." },
  { text = "Use the metal spade on the bunsen burner in the room.", title = "First door" },
  { text = "Use the metal spade on the door." },
  { text = "Use cupric sulphate on the door." },
  { text = "Use a vial of liquid on the door." },
  { text = "Pull on the spade to open the door." },
  { text = "Use the gypsum on the tin.", title = "Second door" },
  { text = "Use a vial of liquid on the tin." },
  { text = "Use the tin on the key chained to the south-western wall." },
  { text = "Use the cupric ore powder on the tin." },
  { text = "Use the tin ore powder on the tin." },
  { text = "Heat the tin on the bunsen burner." },
  { text = "Use a  knife, bronze wire, or chisel on the tin." },
  { text = "Unlock the door with the bronze key." },
  { text = "Talk to Sir Ren Itchood.", title = "Sir Ren Itchood" },
  { text = "The first letter of each line he says is the answer to the door's lock." },
  { text = "Possible combinations are: BITE, FISH, LAST, MEAT, RAIN, or TIME." },
  { text = "Talk to Ms. Hynn Terprett.", title = "Ms. Hynn Terprett" },
  {
    text = "She will give you a riddle.<br><br><table><tbody><tr><th>Riddle</th><th>Answer</th></tr><tr><td>If you were sentenced to death, what would you rather choose, thrown into a lake of acid, burned over a fire, fed to wolves that haven't eaten in 30 days or being thrown off a castle turret?</td><td>Being fed to the wolves.</td></tr><tr><td>I dropped four identical stones, into four identical buckets, each containing an identical amount of water. The first bucket was at 32 degrees Fahrenheit, the second was at 33 degrees, the third was at 34 and the fourth was at 35 degrees. Which bucket's stone dropped to the bottom of the bucket last?</td><td>The First Bucket.</td></tr><tr><td>A father is 4 times as old as his daughter. In 20 years he will be 2 times as old. How old is the daughter?</td><td>10</td></tr><tr><td>I estimate there to be one million inhabitants in the world of Gielinor; creatures and people both. What number would you get if you multiply the number of fingers on everything's left hand, to the nearest million?</td><td>0</td></tr><tr><td>The number of false statements here is one. The number of false statements here is two. The number of false statements here is three. The number of false statements here is four. How many false statements are there?</td><td>There are 3 false statements.</td></tr></tbody></table>",
  },
  {
    text = "After you talk to him, do not move, stand there and do nothing until he talks again. Do not take the hourglass.",
    title = "Sir Tinley",
  },
  { text = "After all the puzzles are done, quest complete!" },
}

return Quest:new({
  name = "Recruitment Drive",
  steps = steps,
  timeline = Enums.timeline.adventurer,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1119830400,
  prereqQuests = {},
})
