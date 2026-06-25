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
    text = "Enter the New Foundations portal outside King Roald's throne room in Varrock Palace.",
    title = "The beginning",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new(" Is there no way that you will reconsider?") },
  },
  {
    text = "After the conversation has ended, enter through the door and talk to King Roald.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "After the cutscene, talk with King Roald.",
    actions = { Action.ConversationHighlight:new("Sign me up!") },
    postconditions = {
      Condition.ConversationText:new(
        "The screen fades out and back in. The player has returned to the starting point outside the throne room door."
      ),
    },
  },
  {
    text = "Travel north-east from Varrock, near Jolly Boar Inn and the easternmost boundary of the Wilderness wall.<ul><li>Fastest way to get there is by teleporting to the Infernal Source Dig Site and run slightly north-east (need level 20 Archaeology).</li></ul>",
    title = "Saving Bill the architect",
  },
  {
    text = "Click the portal to 'Continue New Foundations'.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Defeat the armoured zombies." },
  { text = "Talk to Bill towards the north of the well." },
  {
    text = "When the chat has ended, go southwest to the Fort Forinthry blueprints (table behind the bank chest) to build the Workshop.",
  },
  {
    text = "Check the plans with the Fort Forinthry blueprints and start building the Workshop (Tier 1) which requires:<ul><li>6 stone wall segments (can be made from 24 limestone bricks)</li><li>8 wooden frames (can be made from 96 planks)</li></ul>",
    title = "Building the Workshop",
    neededItems = { ["Wooden frame"] = { quantity = 1 }, ["Stone wall segment"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Build the Workshop by interacting with one of the six Construction hotspots around the edges of the Workshop area.<ul><li>The optimal Construction hotspot moves periodically but improves the progress slightly.</li></ul>",
  },
  { text = "Talk to Bill." },
  {
    text = "Head to the Blue Moon Inn in Varrock and talk to Aster (outside by the tables).",
    title = "Recruiting help",
  },
  { text = "Head to Gunnarsgrunn (Barbarian Village) and talk to Overseer Siv (centre of the village)." },
  { text = "Head to Draynor Village and speak to Father Flint in the Draynor Village marketplace." },
  { text = "Head back to the fort and speak to Bill." },
  {
    text = "Proceed to build the fortifications around the fort. The order does not matter.",
    title = "Building the fortifications",
    neededItems = { ["Plank"] = { quantity = 1 }, ["Stone wall segment"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Find the appropriate hotspot located within the area." },
  {
    text = "Front gate<ul><li>6 stone wall segments (can be made from 24 limestone bricks)</li><li>10 planks</li></ul>",
    title = "Fortifications",
  },
  {
    text = "Rear gate<ul><li>6 stone wall segments (can be made from 24 limestone bricks)</li><li>10 planks</li></ul>",
  },
  { text = "West wall<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "North-west wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "North wall section 1<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "North wall section 2<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "North-east wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "East wall<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "South-east wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "South wall section 1<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "South wall section 2<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "South-west wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  {
    text = "Front gate<ul><li>6 stone wall segments (can be made from 24 limestone bricks)</li><li>10 planks</li></ul>",
  },
  {
    text = "Rear gate<ul><li>6 stone wall segments (can be made from 24 limestone bricks)</li><li>10 planks</li></ul>",
  },
  { text = "West wall<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "North-west wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "North wall section 1<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "North wall section 2<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "North-east wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "East wall<ul><li>10 stone wall segments (can be made from 40 limestone bricks)</li></ul>" },
  { text = "South-east wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "South wall section 1<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "South wall section 2<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  { text = "South-west wall<ul><li>5 stone wall segments (can be made from 20 limestone bricks)</li></ul>" },
  {
    text = "Once all structures have been built, talk to Bill and finish the dialogue.<ul><li>The title you choose may be changed afterwards.</li></ul>",
    title = "Finishing up",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "New Foundations",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.long,
  releaseDate = 1676246400,
  prereqQuests = {},
})
