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
    text = "Talk to Queen Ellamaria in the garden of Varrock Palace.",
    title = "The ring of charos (a)",
    neededItems = { ["Ring of charos"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to her again to receive a trolley that is needed later.",
    actions = { Action.ConversationHighlight:new("How am I supposed to move statues all the way here?") },
  },
  {
    text = "Equip the ring of charos and talk to the Wise Old Man in Draynor Village.<ul><li>Show them a range of colours so that they can come to a compromise.</li><li>Take his generous gift even though you have no need for it.</li><li>It's absolutely, unquestionably the most interesting thing I've ever done!</li><li>Put on the silly helmet and jump into the cannon.</li><li>You of course Pkmaster0036, no one could ever challenge your greatness!</li><li>Ask me nicely and I might consider it.</li><li>No, especially not that wise old man, who doesn't look at all suspicious.</li></ul>",
    actions = { Action.ConversationHighlight:new("Queen Ellamaria has sent me to seek your guidance.") },
    postconditions = {
      Condition.ConversationText:new(
        " Ellamaria? Well, if you're in her service my guidance is this: do the fastest run that you can off the shortest pier you can find."
      ),
    },
  },
  {
    text = "Thanks for your help, Wise Old Man.",
    actions = { Action.ConversationHighlight:new("Thanks for your help, Wise Old Man.") },
  },
  { text = "If you don't have onion seeds, cabbage seeds or marigold seed, purchase them from Draynor Seed Market." },
  {
    text = "Charm Lyra at the allotments near Port Phasmatys (east of the Canifis lodestone). Accept to grow the patch of onions.",
    title = "Orchids from Port Phasmatys",
    neededItems = {
      ["Ring of charos (a)"] = { quantity = 1 },
      ["Onion seed"] = { quantity = 1 },
      ["Ghostspeak amulet"] = { quantity = 1 },
      ["Ecto-token"] = { quantity = 1 },
      ["Ghosts Ahoy"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Do you have any orchid seeds to spare?"),
      Action.ConversationHighlight:new("[Charm] If you tell me your problems, I may be able to help you."),
      Action.ConversationHighlight:new("[Charm] Yes, I was wondering about that."),
      Action.ConversationHighlight:new("[Charm] Times must be very hard for you."),
      Action.ConversationHighlight:new("[Charm] Whatever you've done, I'm sure you had just cause."),
      Action.ConversationHighlight:new("[Charm] And what is the nature of this fee that you pay?"),
      Action.ConversationHighlight:new("[Charm] If not yours, then whose blood are you offering?"),
      Action.ConversationHighlight:new("[Charm] How can you deal with so much guilt?"),
      Action.ConversationHighlight:new("That's a deal - I'll grow a patch of onions for you."),
    },
  },
  {
    text = "Plant onion seeds in allotment patch. Planting in one patch is enough, planting two increases the success rate of healthy growth. This will take about 40 minutes, so continue the quest and come back later.<ul><li>Apply compost or supercompost to allotment patch or activate Greenfingers aura to protect the crops.</li></ul>",
  },
  {
    text = "Charm Kragen at the allotments at Manor Farm. Accept to grow the cabbages.",
    title = "Snowdrops from Ardougne",
    neededItems = { ["Ring of charos (a)"] = { quantity = 1 }, ["Cabbage seed"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Do you have any snowdrop seeds to spare?"),
      Action.ConversationHighlight:new("[Charm] You seem to be a little irritable, my friend."),
      Action.ConversationHighlight:new("[Charm] I don't like to see a fellow human being so upset."),
      Action.ConversationHighlight:new("[Charm] So what ails you, my friend?"),
      Action.ConversationHighlight:new("[Charm] Well, is there anything I can do for you?"),
      Action.ConversationHighlight:new("[Charm] So what can I do for you?"),
      Action.ConversationHighlight:new("That's a deal - I'll let you know when your cabbages are ready."),
    },
  },
  {
    text = "Plant cabbage seeds in allotment patch. Planting in one patch is enough, planting two increases the success rate of healthy growth. This too will take about 40 minutes, so continue the quest and come back later. Rosemary must be fully grown to protect cabbage.<ul><li>Apply compost or supercompost to allotment patch or activate Greenfingers aura to protect the crops.</li></ul>",
  },
  {
    text = "Use cabbage-port on your explorer's ring 3 or teleport to Draynor Village lodestone and run north-west past the wall to the farming patch in Falador farm.",
    title = "Delphiniums from Falador",
    neededItems = { ["Ring of charos (a)"] = { quantity = 1 }, ["Marigold seed"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Charm Elstan at the allotments north of Port Sarim and agree to grow some marigolds.",
    actions = {
      Action.ConversationHighlight:new("Do you have any delphinium seeds to spare?"),
      Action.ConversationHighlight:new("[Charm] That is why I have come to an expert for advice."),
      Action.ConversationHighlight:new("[Charm] Not just AN expert, Elstan - they say you are THE expert."),
      Action.ConversationHighlight:new("[Charm] Oh no, I love listening to gardening stories..."),
      Action.ConversationHighlight:new("[Charm] Millions? Ah, just what I wanted to hear..."),
      Action.ConversationHighlight:new("Okay, I'll grow you some marigolds."),
    },
  },
  { text = "Plant a marigold seed in the flower patch. This will take about 20 minutes." },
  { text = "Apply compost or supercompost to flower patch." },
  { text = "Add water till fully grown this will take 10 to 20 minutes." },
  { text = "Pick up the grown marigold." },
  {
    text = "Talk with Elstan and finish the dialogue. Aborting the dialogue between handing him the unnoted marigolds and receiving the seeds, you will have to grow the marigolds again.",
  },
  {
    text = "Go to the Edgeville Monastery. Try to take a seed from one of Brother Althric's rose bushes.",
    title = "Rosebushes from the Edgeville Monastery",
    neededItems = {
      ["Ring of charos (a)"] = { quantity = 1 },
      ["Fishing rod"] = { quantity = 1 },
      ["Fly fishing rod"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Charm Brother Althric near the monastery rose bushes.",
    actions = { Action.ConversationHighlight:new("[Charm] These are the most beautiful rosebushes I've ever seen.") },
  },
  { text = "Unequip the ring of charos and use it on Edgeville's well east of the lodestone." },
  { text = "Try to take a seed from one of Brother Althric's rose bushes again." },
  { text = "Talk to Brother Althric." },
  {
    text = "Pick 4 pink, white and red rose seeds (just click each type of bush once; you will pick 4 of them automatically).",
  },
  { text = "Use your fishing rod on the well and equip the ring of charos." },
  {
    text = "Talk to Dantaera at the Catherby allotment farming patch with the ring equipped. Select all the options beginning with [Charm].",
    title = "The white tree",
    neededItems = {
      ["Ring of charos (a)"] = { quantity = 1 },
      ["Secateurs"] = { quantity = 1 },
      ["Magic secateurs"] = { quantity = 1 },
      ["Plant pot"] = { quantity = 1 },
      ["Watering can"] = { quantity = 1 },
      ["Magic watering can"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Do you know how I could grow a White Tree?"),
      Action.ConversationHighlight:new("[Charm] I think that there is something that you are not telling me."),
      Action.ConversationHighlight:new("[Charm] A secret is a dreadful burden to have to keep to yourself."),
      Action.ConversationHighlight:new("[Charm] Unless you allow me to do this she will die anyway."),
    },
  },
  { text = "Interact with the white tree on Ice Mountain west of Edgeville, select secateurs." },
  { text = "Use the white tree shoot on a plant pot and water it." },
  {
    text = "It will take 3 minutes for the shoot to turn into a White tree sapling. It is recommended to plant the tree in the Queen's Garden as soon as possible, as this will take about 6 minutes longer to grow than the other plants.",
  },
  {
    text = "Charm Bernald who is south of the Taverley pub.",
    title = "Vines from Taverley",
    neededItems = {
      ["Ring of charos (a)"] = { quantity = 1 },
      ["Plant cure"] = { quantity = 1 },
      ["Rune essence"] = { quantity = 1 },
      ["Pure essence"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("[Charm] But it is the only way that these vines will be cured."),
      Action.ConversationHighlight:new("I accept the deal."),
    },
  },
  { text = "Use a plant cure on his grapevines." },
  { text = "Talk to Bernald again." },
  {
    text = "Run north and talk to Alain at the tree patch. Do not charm him.",
    actions = {
      Action.ConversationHighlight:new("I need to ask you about strong plant cures."),
      Action.ConversationHighlight:new("Are you sure there's nothing you can suggest?"),
    },
  },
  {
    text = "Use an essence on any anvil (you will find anvils walking north), grind the shards, use the rune dust on a plant cure.",
  },
  { text = "Use the new plant cure on his grapevines." },
  { text = "Talk to Bernald." },
  {
    text = "If you have no trolley, talk to Ellamaria in the Varrock Palace garden to receive one.",
    title = "Rock and roll",
    neededItems = { ["Trolley"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Teleport to Lumbridge, and at the entrance of the Lumbridge Castle courtyard, use the trolley on the southern statue.",
  },
  {
    text = "Push the trolley east across the nearby bridge.<ul><li>You may select the 'Big-push' option to move the statue more quickly.</li></ul>",
  },
  {
    text = "Push it into the Queen's garden and use the 'place' option when statue is close to the plinth, the one against the east wall. If you are not fast enough to push it and place it, it will disappear and respawn in Lumbridge.",
  },
  { text = "Teleport to Falador, and use the trolley on the statue of Saradomin south of the Falador lodestone." },
  { text = "After the cutscene, push it north out the gates and onto the garden plinth using the place option again." },
  {
    text = "If you take too long to move the statues to the plinth, they will vanish and return back to their original position.",
  },
  {
    text = "You can now return to the patches and collect your seeds from the farmers (if you haven't done it already)<ul><li>Orchid seed (pink) and orchid seed (yellow) from Lyra east of Canifis.</li><li>Snowdrop seeds from Kragen north-east of Ardougne.</li><li>Delphinium seeds from Elstan north-east of Port Sarim.</li></ul>",
    title = "Finishing the garden",
    neededItems = {
      ["White rose seed"] = { quantity = 1 },
      ["Pink rose seed"] = { quantity = 1 },
      ["Red rose seed"] = { quantity = 1 },
      ["Snowdrop seed"] = { quantity = 1 },
      ["White tree sapling"] = { quantity = 1 },
      ["Vine seed"] = { quantity = 1 },
      ["Orchid seed (pink)"] = { quantity = 1 },
      ["Orchid seed (yellow)"] = { quantity = 1 },
      ["Delphinium seed"] = { quantity = 1 },
      ["Compost"] = { quantity = 1 },
      ["Supercompost"] = { quantity = 1 },
      ["Ring of charos (a)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Plant the seeds back at the garden.<ul><li>Use your buckets of compost on the nearby plantpots. If having issues with 'this patch needs weeding first', use regular compost, not supercompost.</li><li>Plant the rose seed, flower seeds, and white tree sapling in their spots.</li><li>Plant the vine seeds in the vine patch.</li><li>Plant the orchids in the plant pots. You may use Inspect option on a plant pot to find out what goes there.</li></ul>",
  },
  {
    text = "Wait for everything to grow which takes around 15 minutes; 20 for the White Tree if you haven't planted it yet.",
  },
  { text = "Talk to the Queen." },
  {
    text = "Charm King Roald while equipped with the ring of charos (a).",
    actions = {
      Action.ConversationHighlight:new("Ask King Roald to follow you."),
      Action.ConversationHighlight:new("[Charm] Of course, your majesty - please forgive me."),
      Action.ConversationHighlight:new("[Charm] The Queen asked me to bring you."),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Garden of Tranquillity",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1125360000,
  prereqQuests = { "Creature of Fenkenstrain" },
})
