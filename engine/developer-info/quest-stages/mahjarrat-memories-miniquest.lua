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
    text = "Speak to Kharshai, located under the helmet shop in Rellekka. Agree to help him.",
    title = "Beginning",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("I want to talk about the Mahjarrat memories."),
      Action.ConversationHighlight:new("Tell me more."),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Sign the contract.") },
  },
  {
    text = "With the engrammeter, head to any divination colony of vibrant wisps or higher. Fill it with 500 divine memories.<ul><li>You may utilise World 79 (Community Divination World) for more frequent enriched springs.</li></ul>",
  },
  { text = "Visit the locations below, use the charged engrameter to collect the memory." },
  {
    text = "Return to Kharshai. Talk to him to give him the memory.",
    actions = { Action.ConversationHighlight:new("I want to talk about the Mahjarrat memories.") },
  },
  { text = "Recharge the engrammeter with another 500 divine memories, and repeat for the remaining 14 memories." },
  {
    text = "This version of the miniquest is still a WIP. It's best to use the wiki for this step.",
    title = "Finding the memories",
  },
  {
    text = "Take the final memory to Kharshai, receive your reward.",
    title = "Finding the memories",
    neededItems = {
      ["Macaw"] = { quantity = 1 },
      ["Insulated boots"] = { quantity = 1 },
      ["Invitation box"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
}

return Quest:new({
  name = "Mahjarrat Memories (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.veryverylong,
  releaseDate = 1395014400,
  prereqQuests = { "Koschei's Troubles (miniquest)", "Missing, Presumed Death" },
})
