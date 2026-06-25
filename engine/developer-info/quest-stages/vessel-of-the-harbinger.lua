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
    text = "Speak to Death in the City of Um.",
    title = "Starting out",
    neededItems = {
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Cramulet"] = { quantity = 1 },
      ["Hard Morytania achievements"] = { quantity = 1 },
    },
    recommendedItems = { ["Ectophial"] = { quantity = 1 } },
  },
  { text = "Teleport to the Ectofuntus using the Ectophial." },
  { text = "Speak to Necrovarus." },
  {
    text = "Kill 5 Tortured souls with Necromancy to the north to fill the soul urn.",
    title = "Cleansing the souls",
    neededItems = {
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Cramulet"] = { quantity = 1 },
      ["Hard Morytania achievements"] = { quantity = 1 },
      ["Bucket of slime"] = { quantity = 1 },
    },
    recommendedItems = { ["Ectophial"] = { quantity = 1 } },
  },
  {
    text = "Worship the Ectofuntus with the filled urn and a bucket of slime in backpack.<ul><li>Gather a bucket of slime from the Pool of Slime, no bucket is required to gather the slime.</li></ul>",
  },
  { text = "Speak to Necrovarus." },
  {
    text = "Head to Netty's house, on the eastern side of the Slayer Tower.<ul><li>Teleport to the Slayer Tower by using ring of slaying or Dungeoneering cape (0,0,2) or mask of the Abyss.</li><li>Otherwise, teleport to Canifis lodestone and run west past graveyard, where ghouls wander, and then directly north.</li></ul>",
    title = "Retrieving the book of Haricanto",
    neededItems = {
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Cramulet"] = { quantity = 1 },
      ["Hard Morytania achievements"] = { quantity = 1 },
      ["Ectoplasm"] = { quantity = 1 },
      ["Conduit"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Inspect the pile of letters just inside Netty's front door. If you do not have the option to inspect, you have missed a previous step.",
  },
  {
    text = "Teleport back to the Ectofuntus and enter Port Phasmatys to speak to Velorina who is located at the northernmost house.",
  },
  { text = "Head to the Last Call pub in the City of Um, east of the City of Um lodestone." },
  {
    text = "Speak to Netty in the northeast corner of the pub to receive Netty's death certificate and will.",
    actions = { Action.ConversationHighlight:new("Talk about 'Vessel of the Harbinger'.") },
    postconditions = {
      Condition.ConversationText:new(
        " Those are important. I will give you another copy, but please be more careful this time."
      ),
    },
  },
  { text = "Head to the bank in Port Phasmatys and speak to a ghost banker to claim Netty's key." },
  { text = "Head back to Netty's house." },
  { text = "Open the chest." },
  { text = "Use your Conjure Skeleton Warrior with a conduit equipped and attack Netty's skeleton. She will flee." },
  { text = "Open the chest again to receive the book of Haricanto." },
  {
    text = "Head back to Ectofuntus and talk to Necrovarus.",
    title = "Empower vessel ritual",
    neededItems = {
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Cramulet"] = { quantity = 1 },
      ["Hard Morytania achievements"] = { quantity = 1 },
      ["Bucket"] = { quantity = 1 },
      ["Basic ghostly ink"] = { quantity = 1 },
      ["Regular ghostly ink"] = { quantity = 1 },
      ["Basic ritual candle"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Use a bucket on the Ectofuntus." },
  { text = "Go to the ritual site in City of Um." },
  {
    text = "Prepare an empower vessel ritual by using the pedestal.<ul><li>It requires 2 Elemental I, 1 Change I, 1 Commune I, 4 ritual light sources and 1 bucket of powerful slime as the focus.</li><li>Total 11 basic ghostly ink, 2 regular ghostly ink, 4 basic ritual candles or better to prepare the ritual from scratch.</li></ul>",
  },
  { text = "Complete the ritual." },
  { text = "Watch the cutscene.<ul><li>If the cutscene does not play, talk to Necrovarus.</li></ul>" },
  { text = "Speak to Death." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Vessel of the Harbinger",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1691366400,
  prereqQuests = { "Rune Mythos", "Ghosts Ahoy" },
})
