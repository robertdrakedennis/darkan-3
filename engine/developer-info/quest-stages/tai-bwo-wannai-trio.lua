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
    text = "Talk to Timfraku located upstairs in the house northwest of Tai Bwo Wannai.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Trufitus sent me."),
      Action.ConversationHighlight:new("Your gratitude is all I deserve."),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Go to the lake south of Tai Bwo Wannai and fish 23 raw karambwanji (just north of fairy ring code CKR).",
    title = "Lubufu",
  },
  {
    text = "North of the Karamja lodestone, talk to Lubufu.",
    actions = { Action.ConversationHighlight:new("I wasn't going anywhere...") },
  },
  {
    text = "Talk to him again.",
    actions = {
      Action.ConversationHighlight:new("Talk about him..."),
      Action.ConversationHighlight:new("What do you do?"),
      Action.ConversationHighlight:new("What do you use for bait?"),
      Action.ConversationHighlight:new("Talk about him..."),
      Action.ConversationHighlight:new("What do you do?"),
      Action.ConversationHighlight:new("I could help collect the bait."),
      Action.ConversationHighlight:new("You sound like you could do with the help."),
    },
  },
  { text = "Talk to Lubufu to give him 20 raw karambwanji." },
  {
    text = "Talk to Lubufu 3 more times as follows<ul><li>Talk to him again.</li><li>Talk again.</li><li>Talk a third time.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("What do you do with your Karambwan?"),
      Action.ConversationHighlight:new("What do you use to catch Karambwan?"),
      Action.ConversationHighlight:new("What does Karambwan taste like?"),
      Action.ConversationHighlight:new("Yes!"),
    },
  },
  {
    text = "Drop the karambwan vessel and talk to him to get a second vessel, then pick up the first.<ul><li>Optional: Use 1 raw karambwanji on a karambwan vessel, then fish at the spot next to Lubufu (requires 65 ) to complete a Medium Karamja achievement.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Actually, I've lost my Karambwan vessel."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Use another karambwanji on a vessel and keep it for the next step." },
  {
    text = "Head to the north-east coast of Karamja, across the river and talk to Tiadeche (fairy ring code DKP).",
    title = "Tiadeche",
    actions = { Action.ConversationHighlight:new("Is there anything I can do to help?") },
  },
  { text = "Use the loaded vessel on him.", actions = { Action.ConversationHighlight:new("Yes") } },
  { text = "Head south, kill a jogre and take its bones." },
  {
    text = "Go to Musa Point and buy one Karamjan rum from the pub. You can also buy rum from the Dead Man's Chest in Brimhaven. The rum will break if you teleport with it in your inventory. It will be stolen by a monkey if you ride the cart to Shilo Village with it in your inventory.",
  },
  { text = "Pick a banana or buy one from the traders at the Brimhaven dock, cut it for slices." },
  {
    text = "Use a banana slice on the Karamjan rum - you can now teleport without having the rum disappear from your inventory.",
  },
  {
    text = "Take some seaweed near the shore of Cairn Isle (fairy ring code CKR).",
    title = "Tamayu and the Shaikahan",
    neededItems = { ["Agility potion (4)"] = { quantity = 1 }, ["Raw karambwan"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Chop nearby dead trees to get 2 logs to light (or go to the fire south of Tai Bwo Wannai)." },
  { text = "Use the Karambwan (not the Karambwanji) on the fire; select the option poison karambwan." },
  { text = "Grind the poison karambwan to make Karambwan paste (poison)." },
  { text = "Right-click use the paste on your spear to poison it." },
  { text = "Talk to Tamayu southeast of Tai Bwo Wannai, near the mining area." },
  {
    text = "Talk to him again.",
    actions = { Action.ConversationHighlight:new("When will you succeed?"), Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "After the cutscene, use the poisoned spear and agility potion (4) on him.",
    actions = { Action.ConversationHighlight:new("Yes, you can keep it forever.") },
  },
  {
    text = "Talk to him again.",
    actions = { Action.ConversationHighlight:new("Take me on your next hunt for the Shaikahan.") },
  },
  { text = "Kill a nearby monkey, take its corpse, then use it on Tamayu to skin it." },
  {
    text = "Go to Cairn Isle (fairy ring code CKR), climb the rocks and cross the bridge. The surge ability can be used to bypass the agility check for the bridge.",
    title = "Tinsay",
    neededItems = {
      ["Jogre bones"] = { quantity = 1 },
      ["Karamjan rum (sliced banana)"] = { quantity = 1 },
      ["Logs"] = { quantity = 1 },
      ["Monkey skin"] = { quantity = 1 },
      ["Seaweed"] = { quantity = 1 },
      ["Raw karambwanji"] = { quantity = 1 },
      ["Karambwan vessel"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Talk to Tinsay." },
  {
    text = "Give him the Karamjan rum.<ul><li>Use banana slices on the Karamjan rum first if you did not in the previous section.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Use seaweed on the monkey skin, then give it to him.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Right-click light the jogre bones. (Use a furnace if not level 30 in Firemaking.)" },
  {
    text = "Grind your last raw karambwanji (the fish, not the octopus) into paste.<ul><li>If there is no more raw karambwanji in your inventory, fish for more in the lake northeast of Cairn Isle.</li></ul>",
  },
  { text = "Pick up the burnt jogre bones once the fire dies, then use the green paste on it." },
  { text = "Light a log fire, then right-click use the pasty bones on the fire to cook it." },
  { text = "Give him the marinated jogre bones.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Use a karambwan vessel on Tinsay so that he gives you a crafting manual.<ul><li>If he will not take the Vessel go back to Tiadeche and use a loaded Vessel, one with a Karambwanji inside of it. After successfully catching a Karambwan, Tiadeche will ask you to take a Vessel to Tinsay to study. Tiadeche will keep the Vessel you give him so you will need to obtain a new one before returning to Tinsay.</li></ul>",
  },
  { text = "Talk to Tiadeche to give him the crafting manual (fairy ring DKP).", title = "Finishing" },
  {
    text = "Return to Timfraku at the quest start.",
    actions = { Action.ConversationHighlight:new("Eternal gratitude accepted.") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Tai Bwo Wannai Trio",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1095120000,
  prereqQuests = { "Jungle Potion" },
})
