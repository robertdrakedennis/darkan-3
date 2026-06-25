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
    text = "Talk to Wendy, West of Draynor lodestone.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Have you seen that purple cat wandering around?"),
      Action.ConversationHighlight:new("If I brought you a cat, could you make it purple?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Travel to Betty's Magic Emporium in Port Sarim and enter the trapdoor." },
  { text = "Talk to Lottie." },
  { text = "Blackbird to empty holding pen", title = "Solving the puzzle" },
  { text = "Lizard to Blackbird pen" },
  { text = "Rat to Rat pen" },
  { text = "Snail to Reptile pen" },
  { text = "Spider to Spider pen" },
  { text = "Bat to Snail pen" },
  { text = "Lizard to Bat pen" },
  { text = "Spider to Blackbird pen" },
  { text = "Snail to Spider pen" },
  { text = "Rat to Reptile pen" },
  { text = "Spider to Rat pen" },
  { text = "Lizard to Blackbird pen" },
  { text = "Bat to Bat pen" },
  { text = "Snail to Snail pen" },
  { text = "Lizard to Spider pen" },
  { text = "Spider to Blackbird pen" },
  { text = "Rat to Rat pen" },
  { text = "Lizard to Reptile pen" },
  { text = "Spider to Spider pen" },
  { text = "Blackbird to Blackbird pen" },
  { text = "Open the chest to the northwest and search it.", title = "Finishing up" },
  {
    text = "Bring the magic unguent back to Wendy.",
    actions = { Action.ConversationHighlight:new("I have some magic unguent for you!") },
  },
}

return Quest:new({
  name = "Purple Cat (miniquest)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = false,
  length = Enums.length.short,
  releaseDate = 1225152000,
  prereqQuests = { "Swept Away", "Gertrude's Cat" },
})
