local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex, NPCs, Objects, Items = Types.Model, Types.Vertex, Models.npcs, Models.objects, Models.items

-- NPCs
local livingRockBrawler = Model.new(3564, {
  [86] = Vertex.new(187, 394, -73, 88, 73, 46),
  [1516] = Vertex.new(-133, 412, -75, 106, 88, 56),
  [2138] = Vertex.new(-129, 413, 40, 112, 93, 59),
  [2564] = Vertex.new(187, 394, -73, 106, 88, 56),
  [2566] = Vertex.new(187, 394, -73, 106, 88, 56),
})

-- Objects
-- Items
-- Quest Items

---@type QuestStep[]
local steps = {
  {
    title = "Having a brawl",
    text = "Head down the stairs at Doric's workshop to speak to Boric.",
    neededItems = {
      ["Coal"] = { quantity = 30, model = Items["coal"] },
      ["Highest level ore box available"] = { quantity = 1 },
    },
    -- recommendedItems = { ["Highest level ore box available"] = { quantity = 1 } },
    actions = { Action.Direction:new(2959, 869, 3439) },
    postconditions = { Condition.DistanceTo:new(2959, 869, 3439, 2) },
  },
  {
    actions = { Action.Direction:new(2959, 919, 3444) },
    postconditions = { Condition.DistanceTo:new(1572, 1317, 5988, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["boric"]),
      Action.ConversationHighlight:new("Do you have any Mining tasks for me?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Finish the conversation with Boric.<br><br>Head to the following mining locations and mine any ore. Multiple ores may need to be mined. A living rock brawler will spawn, kill it. The locations are:",
    actions = {
      Action.ModelHighlight:new(NPCs["boric"]),
    },
    postconditions = {
      Condition.ConversationText:new(
        "Oh, and please be discreet about my involvement in this. I don't want our workers to get the wrong impression."
      ),
    },
  },
  {
    text = "Legends' Guild south-west mine (Ardougne lodestone).",
    actions = { Action.Direction:new(2709, 1029, 3331), Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelVisible:new(livingRockBrawler) },
  },
  {
    actions = { Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelNotVisible:new(livingRockBrawler) },
  },
  {
    text = "Al Kharid mine (Al Kharid lodestone).",
    actions = { Action.Direction:new(3300, 549, 3298), Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelVisible:new(livingRockBrawler) },
  },
  {
    actions = { Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelNotVisible:new(livingRockBrawler) },
  },
  {
    text = "Karamja north-west mine (Karamja lodestone).",
    actions = { Action.Direction:new(2733, 549, 3223), Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelVisible:new(livingRockBrawler) },
  },
  {
    actions = { Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelNotVisible:new(livingRockBrawler) },
  },
  {
    text = "Lumbridge south-west mine (Lumbridge lodstone).",
    actions = { Action.Direction:new(3148, 5, 3148), Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelVisible:new(livingRockBrawler) },
  },
  {
    actions = { Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelNotVisible:new(livingRockBrawler) },
  },
  {
    text = "Agility Pyramid north-west mine<ul><li>Use the Menaphos lodestone, exit the city, run North.</li><li>Use the Al Kharid lodestone, go South through Shantay Pass, go south through Pollnivneach.</li></ul>",
    actions = { Action.Direction:new(3322, 133, 2874), Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelVisible:new(livingRockBrawler) },
  },
  {
    actions = { Action.ModelHighlight:new(livingRockBrawler) },
    postconditions = { Condition.ModelNotVisible:new(livingRockBrawler) },
  },
  {
    text = "Head back and talk to Boric.",
    actions = { Action.Direction:new(2959, 869, 3439) },
    postconditions = { Condition.DistanceTo:new(2959, 869, 3439, 2) },
  },
  {
    actions = { Action.Direction:new(2959, 919, 3444) },
    postconditions = { Condition.DistanceTo:new(1572, 1317, 5988, 5) },
  },
  {
    actions = {
      Action.ModelHighlight:new(NPCs["boric"]),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Boric's Task II (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.veryshort,
  releaseDate = 1350432000,
  prereqQuests = { "Boric's Task I (miniquest)" },
  questReqs = {
    Types.QuestReq.skill("Mining", 40),
  },
  neededItems = {},
  recommendedItems = {},
  combatNPCs = {},
})
