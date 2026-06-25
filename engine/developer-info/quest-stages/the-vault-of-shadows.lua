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
    text = "Speak to Dr Nabanik near the bank chest at Kharid-et Dig Site. He will 'loan' you any of the spell scrolls that you are unable to obtain.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head into the prison block section of the dig site.",
    title = "Ice button",
    neededItems = {
      ["Pontifex signet ring"] = { quantity = 1 },
      ["'Incite Fear' spell scroll"] = { quantity = 1 },
      ["'Incite Fear' spell scroll (loaned)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Enter the eastern door which leads to the solitary confinement area.<ul><li>If you haven't completed Time Served but you have the repaired artefact in your inventory, then just inspect the ancient mechanism to the North. Insert the artefact and then the eastern door will open.</li></ul>",
  },
  {
    text = "Go down to the southern end of the corridor, there will be an inactive dial on the west wall, just before the last empty room.",
  },
  { text = "Cast an 'Incite Fear' spell scroll whilst standing next to the dial." },
  {
    text = "Press the ice button and enter the vault",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Dr Nabanik.)") },
  },
  { text = "Pick up the praetor's log page 1 and right click Transcribe." },
  { text = "NE, E, NE', SW', S, S", title = "First puzzle" },
  { text = "SE, SE, C, N'" },
  { text = "S', W, NW" },
  {
    text = "Head to the Main Fortress of the Kharid-et Dig Site (first room) and go to the south-east corner (the chapel).",
    title = "Blood button",
    neededItems = {
      ["Pontifex signet ring"] = { quantity = 1 },
      ["'Exsanguinate' spell scroll"] = { quantity = 1 },
      ["'Exsanguinate' spell scroll (loaned)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Pass the Pontifex barrier and find the inactive dial on the northern wall, directly north of the Orcus altar.",
  },
  { text = "Cast a 'Exsanguinate' spell scroll whilst standing next to the dial." },
  {
    text = "Press the Blood button and enter the vault",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Dr Nabanik.)") },
  },
  { text = "SW', C, NW, NW, SW'", title = "Second puzzle" },
  { text = "C', C', NE, SE, C'" },
  { text = "NE', NE', C', C', NE'" },
  { text = "C', SW', SE, C, SE" },
  { text = "C, SE, SW, C, SW'" },
  { text = "C, C" },
  {
    text = "Return to the main fortress of the Kharid-et Dig Site and go directly north of the Pontifex barrier, by the oven.",
    title = "Smoke button",
    neededItems = {
      ["Pontifex signet ring"] = { quantity = 1 },
      ["'Smoke Cloud' spell scroll"] = { quantity = 1 },
      ["'Smoke Cloud' spell scroll (loaned)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Find the inactive dial on the wall immediately west of the oven." },
  { text = "Cast a 'Smoke Cloud' spell scroll whilst standing next to the dial." },
  {
    text = "Press the smoke button and enter the vault",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Dr Nabanik.)") },
  },
  { text = "5', 2, 7', 3, 3, 4' - Top row complete (all pink)" },
  { text = "2, 7', 2'" },
  { text = "1, 1, 8', 1', 1'" },
  { text = "3, 3, 8, 3, 3 - Second row complete (all red)" },
  { text = "7', 4, 8, 4' - Puzzle complete" },
  {
    text = "Return to the Main Fortress of the Kharid-et Dig Site and enter the war room by clicking the praetorium war table (middle of the Main Fortress)",
    title = "Shadow button",
    neededItems = {
      ["Shadow anchors"] = { quantity = 1 },
      ["Pylon"] = { quantity = 1 },
      ["'Animate Dead' spell scroll"] = { quantity = 1 },
      ["'Animate Dead' spell scroll (loaned)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Find the inactive dial on the western wall in the small western room." },
  { text = "Cast an 'Animate Dead' spell scroll whilst standing next to the dial." },
  {
    text = "Press the Shadow button and enter the vault",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Dr Nabanik.)") },
  },
  { text = "3, 3, 1’, 3, 3" },
  { text = "1’, 5’, 2, 2, 4" },
  { text = "1’, 3’, 1, 5’, 3" },
  { text = "3, 5, 2’, 3’, 2’" },
  { text = "3, 3, 4, 4, 3, 3" },
  { text = "Pick up the shadow engrammeter (depleted) from the centre of the vault.", title = "Finishing up" },
  {
    text = "Collect 500 divination memories (vibrant tier or higher). Recharge the Shadow engrammeter when you have an inventory full of memories to charge it. Enriched memories count as double.",
  },
  { text = "Return to the centre of the vault via any of the dials and Operate the engrammeter." },
  {
    text = "Take Trindine's memory to Dr Nabanik.<ul><li>You will receive the memory as a book, and be granted 25,000 Divination experience.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I want to talk about mysteries."),
      Action.ConversationHighlight:new("The Vault of Shadows."),
    },
  },
  { text = "Return to the vault using any of the dials." },
  {
    text = "Speak to Praetor Trindine in the centre. This will complete The Vault of Shadows mystery.<ul><li>Trindine will not appear if the shadow anchors weren't activated or if the Pylon isn't active, as she is invisible.</li></ul>",
  },
  { text = "Return to and speak to Dr Nabanik." },
  { text = "Speak to him again after the cutscene." },
}

return Quest:new({
  name = "The Vault of Shadows",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1611532800,
  prereqQuests = {},
})
