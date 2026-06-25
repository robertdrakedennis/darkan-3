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
    text = "Speak with Grand Vizier Hassan in the Merchant district of Menaphos.",
    title = "Getting started",
    actions = { Action.ConversationHighlight:new("Talk about 'Phite Club.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'll help if I get to end him."),
    },
  },
  {
    text = "Speak to the faction heads, preferably ending with worker (closest to tomb)<ul><li>Speak to Grand Vizier Ehsan (Merchant) also in the same building.</li><li>Speak to 'Admiral' Wadud (Ports) in the Golden Scarab Inn in the Port district.</li><li>Speak to Commander Akhomet (Imperial) in the Imperial district.</li><li>Speak to Batal (Worker) in the Worker district.</li></ul>",
    title = "One small favour",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Phite Club."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Enter the Shifting Tombs and speak to Ozan inside." },
  {
    text = "Exit the Shifting Tombs and use the mask you received from Ozan with the bucket of blood next to the fish stall in the Merchant district.",
  },
  {
    text = "Speak to Akhomet in the Imperial district.",
    actions = { Action.ConversationHighlight:new("Talk about 'Phite Club.") },
  },
  {
    text = "Give the signets to Batal in the Worker district.",
    actions = { Action.ConversationHighlight:new("Talk about 'Phite Club.") },
  },
  {
    text = "Give the certificate to Wadud in the Port district.",
    actions = { Action.ConversationHighlight:new("Talk about 'Phite Club.") },
  },
  {
    text = "Speak to Ehsan in the Merchant district.",
    actions = { Action.ConversationHighlight:new("Talk about 'Phite Club.") },
  },
  { text = "Gear up for a battle.", title = "Confronting the Pharaoh" },
  {
    text = "Speak to Hassan in the Merchant district.",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Phite Club."),
      Action.ConversationHighlight:new("Yes, let's go confront the Pharaoh."),
    },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  {
    text = "Kill four level 105 Menaphos palace guards. This will be a checkpoint for the final fight. Use Protect from Magic prayer and finish off the ones using melee combat style first.",
  },
  {
    text = "Fight the Pharaoh. Use Protect from Magic. 'Kneel' or 'Usurpers!' - Use the freedom ability to break free and prevent further damage. Move off the green fire as soon as it spawns.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Watch the cutscene.", title = "Finale", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "'Phite Club",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1496620800,
  prereqQuests = { "Our Man in the North" },
  neededItems = {
    ["test"] = { quantity = 1, maxQuantity = 3 },
  },
})
