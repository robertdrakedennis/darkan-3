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
    text = "Talk to Closure who can be found in the Closure's Study in Death's office.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Watch the cutscene." },
  {
    text = "Talk to the bartender in the Blue Moon Inn for the handwritten message.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Read the handwritten message." },
  {
    text = "Head to the Jolly Boar Inn located north-east of Varrock, outside the city walls near the Infernal source digsite and the Fort Forinthry lodestone.",
  },
  { text = "Talk to Relomia at the bar for a stack of Closure's biographies." },
  { text = "Return and talk to Closure with his biographies." },
  {
    text = "Talk to Merlin in the waiting room of Closure's office to enter his memory.",
    title = "Fixing memories - Merlin",
  },
  {
    text = "Interact with the Oneiric manipulator in your inventory to play the memory.",
    actions = { Action.ConversationHighlight:new("Play the memory") },
  },
  {
    text = "Interact with it again to reset the memory.",
    actions = { Action.ConversationHighlight:new("Reset the memory") },
  },
  {
    text = "Interact with the NPC next to you (shares your character's name).",
    actions = { Action.ConversationHighlight:new("Excalibur") },
  },
  { text = "Interact with the Latsyrc crystal twice so it becomes a Giant crystal." },
  { text = "Interact with the 'Merlin sucks' shield on the western wall once so it becomes a Wallshield." },
  {
    text = "Interact with the Oneiric manipulator in your inventory again.",
    actions = { Action.ConversationHighlight:new("Play the memory") },
  },
  { text = "Once back in Closure's office,  talk to Merlin." },
  { text = "Talk to Ozan to enter his memory.", title = "Ozan" },
  {
    text = "Interact with the Oneiric manipulator in your inventory to play the memory.",
    actions = { Action.ConversationHighlight:new("Play the memory") },
  },
  {
    text = "Interact with the Oneiric manipulator to reset the memory.",
    actions = { Action.ConversationHighlight:new("Reset the memory") },
  },
  { text = "Interact with Ozan.", actions = { Action.ConversationHighlight:new("Jump second.") } },
  {
    text = "Interact with the NPC that shares your character name.",
    actions = { Action.ConversationHighlight:new("Jump first.") },
  },
  { text = "Enter the portal and interact the empty cart once so it becomes a cart." },
  {
    text = "Interact with the Oneiric manipulator in your inventory.",
    actions = { Action.ConversationHighlight:new("Play the memory") },
  },
  { text = "Once back in Closure's office, talk to Ozan." },
  { text = "Talk to Ava to enter her memory.", title = "Ava" },
  {
    text = "Interact with the Oneiric manipulator to play the memory.",
    actions = { Action.ConversationHighlight:new("Play the memory") },
  },
  {
    text = "Interact with the Oneiric manipulator.",
    actions = { Action.ConversationHighlight:new("Reset the memory") },
  },
  {
    text = "Interact with NPC that shares your character's name.",
    actions = { Action.ConversationHighlight:new("Undead chickens") },
  },
  { text = "Investigate Lever B on the wall twice so it becomes a lever." },
  { text = "Investigate the Spooky clock twice so it becomes a wardrobe." },
  {
    text = "Interact with the Oneiric manipulator again.",
    actions = { Action.ConversationHighlight:new("Play the memory") },
  },
  { text = "Once back in Closure's office, talk to Ava." },
  { text = "Talk to Closure.", title = "Closure" },
}

return Quest:new({
  name = "Once Upon a Time in Gielinor: Flashback",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.short,
  releaseDate = 1617667200,
  prereqQuests = {
    "Once Upon a Time in Gielinor: Foreshadowing",
    "Merlin's Crystal",
    "Animal Magnetism",
    "Diamond in the Rough",
  },
})
