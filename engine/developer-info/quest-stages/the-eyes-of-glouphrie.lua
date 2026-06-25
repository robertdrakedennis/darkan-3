local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local brimstail = Model.new(5331, {
  [3535] = Vertex.new(0, 440, -10, 15, 168, 42),
  [3536] = Vertex.new(2, 440, -12, 15, 168, 42),
  [3537] = Vertex.new(-2, 440, -12, 15, 168, 42),
  [4048] = Vertex.new(-15, 503, -49, 176, 135, 112),
  [5240] = Vertex.new(46, 490, 32, 72, 75, 78),
})
--#endregion
--#region Objects
local caveEntrance = Model.new(939, {
  [45] = Vertex.new(1972, 1006, 6408, 157, 157, 157),
  [647] = Vertex.new(2024, 1536, 6140, 84, 70, 44),
  [677] = Vertex.new(2024, 1404, 5976, 59, 42, 5),
  [695] = Vertex.new(2024, 1408, 6060, 59, 42, 5),
  [713] = Vertex.new(2004, 1332, 6140, 75, 57, 23),
})
local oaknocksMachine = Model.new(1638, {
  [674] = Vertex.new(100, 648, -176, 20, 89, 23),
  [686] = Vertex.new(100, 676, -176, 20, 89, 23),
  [740] = Vertex.new(100, 552, -176, 20, 89, 23),
  [875] = Vertex.new(-52, 688, -172, 93, 85, 85),
  [881] = Vertex.new(-52, 604, -172, 93, 85, 85),
})
local brokenOaknocksMachine = Model.new(1413, {
  [839] = Vertex.new(100, 676, -176, 7, 82, 9),
  [854] = Vertex.new(100, 552, -176, 7, 82, 9),
  [884] = Vertex.new(100, 648, -176, 7, 82, 9),
  [1040] = Vertex.new(-52, 688, -172, 92, 85, 84),
  [1046] = Vertex.new(-52, 604, -172, 92, 85, 84),
})
local evilCreature = Model.new(1929, {
  [123] = Vertex.new(-12, 228, -84, 44, 48, 44),
  [564] = Vertex.new(-12, 228, -84, 44, 48, 44),
  [566] = Vertex.new(-12, 228, -84, 44, 48, 44),
  [657] = Vertex.new(-48, 264, -100, 74, 19, 15),
  [1061] = Vertex.new(0, 212, 200, 54, 59, 54),
})
--#endregion
--#region Items
local groundMudRune = Model.new(132, {
  [13] = Vertex.new(72, 0, 80, 97, 90, 89),
  [19] = Vertex.new(100, 0, 60, 97, 90, 89),
  [27] = Vertex.new(100, 0, 60, 97, 90, 89),
  [49] = Vertex.new(132, 0, -24, 97, 90, 89),
  [57] = Vertex.new(132, 0, -24, 97, 90, 89),
})
--#endregion
--#region Quest Items
local magicGlue = Model.new(570, {
  [2] = Vertex.new(62, 117, 50, 146, 133, 112),
  [4] = Vertex.new(62, 117, 50, 146, 133, 112),
  [7] = Vertex.new(62, 117, -54, 146, 133, 112),
  [14] = Vertex.new(62, 117, -54, 146, 133, 112),
  [19] = Vertex.new(-58, 117, -54, 146, 133, 112),
})
local redSquare = Model.new(240, {
  [93] = Vertex.new(44, 0, 48, 78, 27, 24, 0.6078),
  [98] = Vertex.new(44, 0, 48, 78, 27, 24, 0.6078),
  [173] = Vertex.new(-48, 0, -48, 78, 27, 24, 0.6078),
  [179] = Vertex.new(-48, 0, -48, 78, 27, 24, 0.6078),
  [186] = Vertex.new(-48, 0, -48, 78, 27, 24, 0.6078),
})
local yellowTriangle = Model.new(405, {
  [348] = Vertex.new(64, -4, -48, 149, 148, 61, 0.6078),
  [353] = Vertex.new(64, -4, -48, 149, 148, 61, 0.6078),
  [359] = Vertex.new(64, -4, -48, 149, 148, 61, 0.6078),
  [368] = Vertex.new(-60, -4, -48, 149, 148, 61, 0.6078),
  [374] = Vertex.new(-60, -4, -48, 149, 148, 61, 0.6078),
})
local redTriangle = Model.new(405, {
  [1] = Vertex.new(4, -4, 24, 68, 11, 6, 0.6078),
  [2] = Vertex.new(-4, -4, 48, 68, 11, 6, 0.6078),
  [3] = Vertex.new(4, -4, 36, 68, 11, 6, 0.6078),
  [5] = Vertex.new(-16, -4, 24, 68, 11, 6, 0.6078),
  [7] = Vertex.new(28, -4, -16, 68, 11, 6, 0.6078),
})
local violetPentagon = Model.new(252, {
  [125] = Vertex.new(-36, 0, -52, 112, 81, 127, 0.6078),
  [131] = Vertex.new(-36, 0, -52, 112, 81, 127, 0.6078),
  [185] = Vertex.new(44, 0, -48, 112, 81, 127, 0.6078),
  [191] = Vertex.new(44, 0, -48, 112, 81, 127, 0.6078),
  [198] = Vertex.new(44, 0, -48, 112, 81, 127, 0.6078),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Enter the cave west of the Gnome Stronghold bank.",
    title = "Getting started",
    warning = "For the tracking to work properly, you need to have the chat visible, game messages set to 'On', and chat timestamps on.",
    actions = {
      Action.Direction:new(2403, 1265, 3419, { distance = 16 }),
      Action.ModelHighlight:new(caveEntrance, { distance = 16 }),
    },
    postconditions = { Condition.DistanceTo:new(2409, 965, 9812, 4) },
  },
  {
    text = "Talk to Brimstail.",
    actions = {
      Action.ModelHighlight:new(brimstail),
      Action.ConversationHighlight:new("What's that cute creature wandering around?"),
      Action.ConversationHighlight:new("Yes, that sounds fascinating..."),
      Action.ConversationHighlight:new("Oh, yes I love a bit of History."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to Brimstail.",
    actions = { Action.ModelHighlight:new(brimstail) },
    postconditions = { Condition.ConversationText:new("what you find") },
  },
  {
    text = "Inspect the singing bowl in the western portion of the cave.",
    title = "Elven Mystery",
    actions = { Action.ModelHighlight:new(Models.objects["crystal singing bowl"]) },
    postconditions = { Condition.ConversationText:new("elegant") },
  },
  {
    text = "To the north, attempt to unlock Oaknock's machine and use Oaknock's exchanger.",
    actions = { Action.ModelHighlight:new(oaknocksMachine) },
    postconditions = { Condition.ConversationText:new("no idea how it works") },
  },
  {
    text = "Talk to Brimstail.",
    actions = {
      Action.ModelHighlight:new(brimstail),
      Action.ConversationHighlight:new("I've had a look in the other room now."),
      Action.ConversationHighlight:new("Of course, I'd love to!"),
    },
    postconditions = { Condition.ConversationText:new("better be on my way") },
  },
  {
    text = "Talk to Hazelmere in his house east of Yanille.<ul><li>Yanille lodestone or fairy ring CLS.</li></ul>",
    title = "Memories",
    actions = {
      Action.Direction:new(2677, 2501, 3088, { distance = 12 }),
      Action.ModelHighlight:new(Models.objects["hazelmere ladder"], { distance = 12 }),
    },
    postconditions = { Condition.DistanceToWithHeight:new(2677, 3725, 3086, 6) },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["hazelmere"]) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Watch the series of cutscenes.",
    postconditions = { Condition.ConversationText:new("Arposandra place anyway") },
  },
  {
    text = "Continue talking to Hazelmere.",
    actions = { Action.ModelHighlight:new(Models.npcs["hazelmere"]) },
    postconditions = { Condition.ConversationText:new("Farewell") },
  },
  {
    text = "Return and talk to Brimstail.",
    actions = {
      Action.Direction:new(2403, 1265, 3419, { distance = 16 }),
      Action.ModelHighlight:new(caveEntrance, { distance = 16 }),
    },
    postconditions = { Condition.DistanceTo:new(2409, 965, 9812, 4), Condition.ConversationText:new("machine emits") },
  },
  {
    actions = {
      Action.ModelHighlight:new(brimstail),
      Action.ConversationHighlight:new("I've visited Hazelmere, he told me all sorts of interesting things."),
    },
    postconditions = { Condition.ConversationText:new("machine emits") },
  },
  {
    text = "Watch the cutscene.",
    postconditions = { Condition.DistanceTo:new(2390, 845, 9822, 4) },
  },
  {
    text = "Grind a mud rune.",
    title = "Incapacitated",
    actions = { Action.InventoryHighlight:new(Models.items["mud rune"]) },
    postconditions = { Condition.InventoryContains:new(groundMudRune) },
  },
  {
    text = "Use the ground mud rune on the bucket of sap.",
    actions = {
      Action.InventoryHighlight:new(groundMudRune),
      Action.InventoryHighlight:new(Models.items["bucket of sap"]),
    },
    postconditions = { Condition.InventoryContains:new(magicGlue) },
  },
  {
    text = "Repair Oaknock's machine.",
    actions = {
      Action.InventoryHighlight:new(magicGlue),
      Action.ModelHighlight:new(brokenOaknocksMachine),
    },
    postconditions = { Condition.ModelNotVisible:new(brokenOaknocksMachine) },
  },
  {
    text = "Talk to Brimstail.",
    actions = {
      Action.ModelHighlight:new(brimstail),
      Action.ConversationHighlight:new("I think I've fixed the machine now!"),
    },
    postconditions = { Condition.ConversationText:new("all good training for") },
  },
  {
    text = "Drop the discs you were given, and get more from Brimstail a few times.",
    actions = {
      Action.ModelHighlight:new(brimstail),
      Action.ConversationHighlight:new("I think I've fixed the machine now!"),
    },
    postconditions = { Condition.InventoryContains:new(redSquare, 6) },
  },
  {
    text = "Unlock machine to view the desired number.",
    title = "Machinery",
    warning = "The following puzzle is different for each player. Good luck!",
    actions = { Action.ModelHighlight:new(oaknocksMachine), Action.InventoryHighlight:new(redSquare, true) },
    postconditions = { Condition.DistanceTo:new(2390, 901, 9825, 1) },
  },
  {
    text = '<table><tbody><tr><th rowspan="2">Colour</th><th colspan="6">Value</th></tr><tr><th>Circle</th><th>Triangle</th><th>Square</th><th>Pentagon</th></tr><tr><td style="color:red;"><b>Red</b></td><td>1</td><td>3</td><td>4</td><td>5</td></tr><tr><td style="color:orange;"><b>Orange</b></td><td>2</td><td>6</td><td>8</td><td>10</td></tr><tr><td style="color:#CCCC00;"><b>Yellow</b></td><td>3</td><td>9</td><td>12</td><td>15</td></tr><tr><td style="color:green;"><b>Green</b></td><td>4</td><td>12</td><td>16</td><td>20</td></tr><tr><td style="color:blue;"><b>Blue</b></td><td>5</td><td>15</td><td>20</td><td>25</td></tr><tr><td style="color:indigo;"><b>Indigo</b></td><td>6</td><td>18</td><td>24</td><td>30</td></tr><tr><td style="color:#8F00FF;"><b>Violet</b></td><td>7</td><td>21</td><td>28</td><td>35</td></tr></tbody></table>',
    postconditions = { Condition.DistanceTo:new(2406, 1005, 9818, 40) },
  },
  {
    text = "To solve the puzzle:<ul><li>Use the exchanger to get a disc equal to your number.</li><li>If you need more discs, talk to Brimstail.</li><li>Click the small green square to submit the correct value disc.</li><li>You have to do this a total of 4 times.</li><li>The second number requires using 2 discs and the third requires 3 discs.</li></ul>",
    actions = { Action.ConversationHighlight:new("I Think Oaknock's machine is now unlocked, what do I do now?") },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.DistanceTo:new(2406, 1005, 9818, 40) } },
  {
    text = "Talk to Brimstail.",
    actions = {
      Action.ModelHighlight:new(brimstail),
      Action.ConversationHighlight:new("Phew! I've got that machine working now. What do I need to do now?"),
    },
    postconditions = { Condition.ConversationText:new("better tell the King") },
  },
  {
    text = "Kill the evil creature by Brimstail and talk to him.",
    title = "Assassination",
    actions = { Action.ModelHighlight:new(evilCreature) },
    postconditions = { Condition.ChatText:new("1 spying creature") },
  },
  {
    text = "Talk to King Narnode Shareen on the ground floor of the Grand Tree.",
    actions = { Action.Direction:new(2407, 1265, 9811.5) },
    postconditions = {
      Condition.DistanceTo:new(2402, 965, 3419, 4),
      Condition.DistanceTo:new(2465, 3205, 3495, 4), --for testing
    },
  },
  {
    actions = {
      Action.Direction:new(2465, 3205, 3495, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["king narnode shareen"], { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("right on it") },
  },
  {
    text = "Kill the evil creature next to King Narnode Shareen.",
    actions = { Action.ModelHighlight:new(evilCreature) },
    postconditions = { Condition.ChatText:new("2 spying creature") },
  },
  {
    text = "Kill the evil creature at the top of the Grand Tree.",
    actions = {
      Action.ModelHighlight:new(evilCreature),
      Action.ModelHighlight:new(Models.objects["grand tree ground floor ladder"]),
    },
    postconditions = { Condition.ChatText:new("3 spying creature") },
  },
  {
    text = "Kill the evil creature next to the Spirit tree.",
    actions = {
      Action.Direction:new(2464, 1285, 3444, { distance = 16 }),
      Action.ModelHighlight:new(evilCreature, { distance = 16 }),
    },
    postconditions = { Condition.ChatText:new("4 spying creature") },
  },
  {
    text = "Kill the evil creature in front of the gate of the Tree Gnome Stronghold.",
    actions = {
      Action.Direction:new(2462, 981, 3381, { distance = 16 }),
      Action.ModelHighlight:new(evilCreature, { distance = 16 }),
    },
    postconditions = { Condition.ChatText:new("5 spying creature") },
  },
  {
    text = "Kill the evil creature north of the swamp.",
    actions = {
      Action.Direction:new(2418, 677, 3526, { distance = 16 }),
      Action.ModelHighlight:new(evilCreature, { distance = 16 }),
    },
    postconditions = { Condition.ModelVisible:new(evilCreature) },
  },
  {
    actions = {
      Action.Direction:new(2418, 677, 3526, { distance = 16 }),
      Action.ModelHighlight:new(evilCreature, { distance = 16 }),
    },
    postconditions = { Condition.ModelNotVisible:new(evilCreature) },
  },
  {
    text = "Talk to King Narnode Shareen on the ground floor of the Grand Tree.",
    actions = {
      Action.Direction:new(2465, 3205, 3495, { distance = 4 }),
      Action.ModelHighlight:new(Models.npcs["king narnode shareen"], { distance = 4 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "The Eyes of Glouphrie",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1155600000,
  prereqQuests = { "The Grand Tree" },
  questReqs = {
    Types.QuestReq.skill("Construction", 5),
    Types.QuestReq.skill("Magic", 46),
  },
  neededItems = {
    ["Ground mud rune"] = { quantity = 1, model = groundMudRune },
    ["Maple log"] = { quantity = 1, model = Models.items["maple logs"] },
    ["Oak log"] = { quantity = 1, model = Models.items["oak logs"] },
    ["Bucket of sap"] = { quantity = 1, model = Models.items["bucket of sap"] },
  },
  recommendedItems = {},
  combatNPCs = { ["Evil creatures"] = { level = "1", quantity = 6 } },
})
