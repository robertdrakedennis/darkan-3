local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

local doctorOutfitItem = Model.any({
  -- nurse hat
  Model.new(138, {
    [111] = Vertex.new(-16, 12, -44, 19, 93, 8),
    [121] = Vertex.new(-8, 32, -52, 19, 93, 8),
    [125] = Vertex.new(4, 32, -52, 19, 93, 8),
    [131] = Vertex.new(12, 24, -48, 19, 93, 8),
    [135] = Vertex.new(-8, 4, -36, 19, 93, 8),
  }),
  -- doctor hat
  Model.new(144, {
    [3] = Vertex.new(8, 8, -40, 68, 68, 52),
    [9] = Vertex.new(-8, 8, -40, 68, 68, 52),
    [104] = Vertex.new(24, 48, -20, 114, 132, 150),
    [105] = Vertex.new(28, 32, -32, 114, 132, 150),
    [108] = Vertex.new(24, 48, -20, 114, 132, 150),
  }),
})

---@type QuestStep[]
local steps = {
  {
    text = "Equip your catspeak amulet, have your pet cat with you, and talk to Unferth. His house is in eastern Burthorpe, south of Dunstan, east of Tam McGrubor and north of Xuan.",
    title = "Starting the quest",
    neededItems = { ["Catspeak amulet"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Speak to Hild in the building north of the Heroes' Guild. She will enchant the catspeak amulet for 5 death runes.",
    title = "Cathunt",
    neededItems = { ["Death rune"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Locate Bob the cat using the catspeak amulet.<ul><li>Right-click and 'open' the amulet.</li><li>Click on the whiskers until the arrow and the eyes glow white.</li><li>Run in the direction of the arrow, occasionally re-checking the amulet, until you find Bob.</li><li>The easiest way to find Bob is to keep hopping worlds in the Carwen Essencebinder Magical Runes Shop in Burthorpe.</li></ul>",
  },
  { text = "Talk to Bob." },
  {
    text = "Ask Gertrude about Bob's parents in her house west of Varrock.<ul><li>If Rat Catchers hasn't been started.</li></ul>",
    title = "Storng lineage",
    actions = {
      Action.ConversationHighlight:new("Ask about Bob's parents."),
      Action.ConversationHighlight:new("Ask about Bob's parents."),
    },
  },
  {
    text = "Speak to Reldo in the Varrock library.",
    actions = { Action.ConversationHighlight:new("Ask about Robert the Strong.") },
    postconditions = { Condition.ConversationText:new(" Hello Reldo.") },
  },
  { text = "Go back and talk to Bob the cat again, in the same spot you found him." },
  {
    text = "Go to Sophanem and talk to the Sphinx.<ul><li>Talk to Raetul and buy a full desert robe set, if you don't have them already.</li></ul>",
    title = "Memories and Bob's list",
    neededItems = {
      ["Potato seed"] = { quantity = 1 },
      ["Logs"] = { quantity = 1 },
      ["Chocolate cake"] = { quantity = 1 },
      ["Bucket of milk"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Ask the Sphinx for help."),
      Action.ConversationHighlight:new("Skip the cutscene and view summary."),
    },
  },
  {
    text = "Return to Unferth's house in Burthorpe and do the following:<ul><li>Rake the patch behind his house and plant potato seeds in it. Your cat will notify you when they are done (typically 25–35 minutes to grow). A supreme growth potion from player-owned farm cannot be used to accelerate growth. It will not get disease, and it does not need composting or watering.</li><li>If you don't have your cat out, chatbox will tell 'Perhaps I should have a look and see if Unferth's potatoes have grown...'.</li><li>Use the logs on fireplace, then light the fireplace.</li><li>Use the chocolate cake and bucket of milk on the table.</li><li>Make his bed upstairs.</li><li>Give Unferth a haircut.</li><li>If you don't have your cat out, chatbox will tell 'Perhaps I should have a look and see if Unferth's potatoes have grown...'.</li></ul>",
    actions = { Action.ConversationHighlight:new("Cut Unferth's hair") },
  },
  { text = "Talk to Unferth once all the chores are done." },
  {
    text = "Talk to the Apothecary in south-west Varrock.",
    title = "Down with the 'sickness'",
    neededItems = {
      ["Vial of water"] = { quantity = 1 },
      ["Desert robe outfit (white)"] = { quantity = 1 },
      ["Druid's robe outfit"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Tell the Apothecary that Unferth is ill."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Unequip your weapons and equip the doctors hat/nurse hat and your white robes." },
  { text = "Return to Unferth with a vial of water." },
  {
    text = "Talk to Bob the cat. If he's not at his previous location, locate him again using the catspeak amulet (e).",
  },
  { text = "After the cutscene, talk to Unferth again." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "A Tail of Two Cats",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1127692800,
  prereqQuests = { "Icthlarin's Little Helper" },
  questReqs = {
    Types.QuestReq.skill("Cooking", 35, true, 50),
    -- Types.QuestReq.questpoints(500),
    -- Types.QuestReq.combat(220),
    -- Types.QuestReq.misc("Some misc text"),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
