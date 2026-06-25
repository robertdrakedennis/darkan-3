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
    text = "Recharge your prayer if you have fewer than 470 points.",
    title = "Braindeath Island",
  },
  { text = "Talk to Pirate Pete, north of Port Phasmatys.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  {
    text = "[Accept Quest]<ul><li>If you have the ectophial it will place you close to Pirate Pete.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("Of course, I fear no demon!"),
      Action.ConversationHighlight:new("Nonsense! Keep the money!"),
    },
  },
  {
    text = "Note: If at any point you leave the area you can return via Pirate Pete.",
    title = "Blindweed",
    actions = { Action.ConversationHighlight:new("Okay!") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Complete the dialogue with Captain Braindeath. He gives you a blindweed seed." },
  { text = "Run south and Climb-down a Wooden Stair." },
  { text = "Run south to the farming patches." },
  { text = "Rake the south-easternmost Blindweed Patch (the only one which isn't labelled as 'trashed')." },
  { text = "Use the seed on the patch." },
  { text = "Pick the blindweed once grown (can take up to 5 minutes)." },
  { text = "Return to and Talk to Captain Braindeath." },
  { text = "In the south-east corner of the building, Climb-up the Ladder." },
  { text = "In the north-west corner, Deposit at the Hopper (1 tile east of the Pressure Barrel)." },
  {
    text = "Talk to Captain Braindeath, he'll give you an empty bucket if you don't already have one.",
    title = "Stagnant water",
  },
  {
    text = "Go to the bridges to the west and Open the Gate.<ul><li>Optional: Wear your ring of charos to distract 50% Luke.</li><li>If no ring just click Open and your character will distract him.</li></ul>",
  },
  { text = "Continue north and up the mountain to the Stagnant Lake." },
  { text = "Fill at the Stagnant Lake." },
  { text = "Deposit at the Hopper from earlier (keep the empty bucket for later)." },
  {
    text = "Return to Captain Braindeath. He will give you a Fishbowl and net<ul><li>If you accidentally Untangle the fishbowl, just Use the bowl on the net to re-tangle them.</li></ul>",
    title = "Sluglings",
  },
  { text = "Fish a total of 5 sluglings and/or karamthulhu near the farming patches (a mix of 5 will work)." },
  { text = "Use all 5 on the Pressure Barrel next to the Hopper." },
  { text = "Pull the lever." },
  { text = "Return to Captain Braindeath. He will give you a Wrench.", title = "Trouble Brewing" },
  { text = "Enter the western room and Talk to Davey to get the Holy wrench." },
  { text = "Right-click Use the wrench on the brewing control to the east (the object shaking crazily)." },
  { text = "Kill the Evil spirit." },
  { text = "Return to Captain Braindeath." },
  { text = "Climb-down the nearby north-west Ladder." },
  {
    text = "With slayer gloves equipped, kill a fever spider and take its body.<ul><li>Slayer gloves are not required with high life points or when safespotting the spider behind a crate (with Magic/Ranged/Necromancy)</li></ul>",
  },
  { text = "Deposit at the Hopper." },
  { text = "Return to Captain Braindeath.", title = "Finishing up" },
  { text = "With an empty bucket, Turn the Output Tap (south of the machinery) to receive Unsanitary swill." },
  { text = "Talk to Captain Donnie north of the farming patches." },
  { text = "Return to Captain Braindeath." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Rum Deal",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1130716800,
  prereqQuests = { "Morytania" },
})
