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
    text = "Teleport to Karamja (shortest teleport is CKR) and run far south/southeast to the southeast corner of Shilo Village.",
    title = "Starting out",
  },
  {
    text = "Speak to Mosol Rei near the Broken cart.  He will give you a wampum belt.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Why do I need to run?"),
      Action.ConversationHighlight:new("Rashiliyia? Who is she?"),
      Action.ConversationHighlight:new("What can we do?"),
      Action.ConversationHighlight:new("I'll go see the Shaman."),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Teleport to the Karamja lodestone and run southeast to Trufitus, northeast of Tai Bwo Wannai Village, west of the hardwood grove.",
  },
  {
    text = "Use the belt on Trufitus. Tell him you're going to search for 'Ah Za Rhoon'.<ul><li>If you don't yet have a chisel, a rope and torch, you should get those now from Jiminua's Jungle Store. You may also craft bronze wire at the nearby anvil.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Mosol Rei said something about a legend?"),
      Action.ConversationHighlight:new("Why was it called Ah Za Rhoon?"),
      Action.ConversationHighlight:new("I am going to search for Ah Za Rhoon!"),
      Action.ConversationHighlight:new("Yes, I will seriously look for Ah Za Rhoon and I'd appreciate your help."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Ok then Bwana, good luck with your quest, and remember to stock up well with adventuring supplies before setting off. You never know how useful some fairly ordinary things might be when you're adventuring."
      ),
    },
  },
  {
    text = "Travel east of Trufitus' hut, cross a wooden log and then head south to the mound of earth surrounded by three dead trees, Ah Za Rhoon.",
    title = "The mound",
    neededItems = { ["Torch"] = { quantity = 1 }, ["Rope"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Search the mound of earth and select excavate.",
    actions = { Action.ConversationHighlight:new("Try to excavate the mound.") },
  },
  { text = "Use a lit torch on the fissure.<ul><li>To obtain a lit torch, light the torch in the backpack.</li></ul>" },
  { text = "Use a rope on the fissure." },
  {
    text = "Search the fissure, climb down.",
    actions = { Action.ConversationHighlight:new("Yes, I'll give it a go!") },
    postconditions = {
      Condition.ConversationText:new(
        " With some difficulty you manage to push yourself through the small crack in the rock."
      ),
    },
  },
  {
    text = "Search the cave in (see map) and wiggle through the rocks to proceed to the second area.",
    title = "Ah Za Rhoon",
    actions = { Action.ConversationHighlight:new("Yes, I'll wriggle through.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "After wiggling through, follow along the west side of the wall and search the loose rocks against the northwestern wall for a tattered scroll.<ul><li>You might fail and take damage, retry until you succeed.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes, I'll carefully move the rocks to see what's behind them.") },
    postconditions = { Condition.ConversationText:new("Player receives tattered scroll.") },
  },
  { text = "Search the old sacks in the far southeast corner for a crumpled scroll." },
  {
    text = "Search the ancient gallows to the north for the Zadimus corpse.",
    actions = { Action.ConversationHighlight:new("Yes, I may find something else on the corpse.") },
    postconditions = {
      Condition.ConversationText:new(
        "You gently support the frame of the skeleton and lift the skull through the noose."
      ),
    },
  },
  {
    text = "Read both scrolls.",
    actions = { Action.ConversationHighlight:new("Yes please.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Use the Karamja lodestone/Tai Bwo Wannai teleport to get back to Trufitus.", title = "Burial" },
  {
    text = "Use the two scrolls and the corpse on Trufitus and ask about the sacred ground.",
    actions = { Action.ConversationHighlight:new("Is there any sacred ground around here?") },
    postconditions = {
      Condition.ConversationText:new(
        " The ground in the centre of the village is very sacred to us. Maybe you could try there?"
      ),
    },
  },
  {
    text = "Walk west to the nearby Tribal Statue. Bury the corpse then talk to the Spirit of Zadimus for the bone shard.",
  },
  {
    text = "Use the bone shard on Trufitus.",
    actions = {
      Action.ConversationHighlight:new("It appeared when I buried Zadimus' corpse."),
      Action.ConversationHighlight:new("He said something after he gave it to me."),
      Action.ConversationHighlight:new("The spirit said something about keys and kin?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Hmmm, maybe it's a clue of some kind? Rashiliyia's only kin was a son, 'Bervirius'. His remains were entombed on a small island which lies to the South West. I will do some research into this to see if I can find any other details. But I think we must take Zadimus' clue literally and get some item that belonged to Bervirius as it may be the only way to approach Rashiliyia. Perhaps something like this exists in his tomb?"
      ),
    },
  },
  {
    text = "Talk to Trufitus.  Ensure Trufitus mentions the need to find the Tomb of Bervirius.",
    actions = { Action.ConversationHighlight:new("I need help with Bervirius.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head southwest to Cairn Isle by climbing the rocks and going across the bridge. Be careful, you can sometimes fall - use the Surge ability to bypass this.",
    title = "Cairn Island",
    neededItems = {
      ["Chisel"] = { quantity = 1 },
      ["Bronze wire"] = { quantity = 1 },
      ["Bronze bar"] = {
        quantity = 1,
      },
    },
    recommendedItems = {},
  },
  {
    text = "Search the well stacked rocks to the north to enter the Tomb of Bervirius. If you fail search again until you are able to enter.",
    actions = { Action.ConversationHighlight:new("Yes please, I can think of nothing nicer!") },
    postconditions = {
      Condition.ConversationText:new(
        "(If unsuccessful:) You manage to get yourself stuck. You have to wrench yourself free to get out. You manage to pull yourself out, but are hurt in the process.The player is damaged. Maybe you'll have better luck next time?"
      ),
    },
  },
  {
    text = "Inside, run south and search the tomb dolmen to receive three items: sword pommel, locating crystal, and Bervirius notes.<ul><li>If you do not have enough space the notes will drop on the ground.</li></ul>",
  },
  { text = "Read all 3 options on the Bervirius notes." },
  { text = "Use the chisel on the sword pommel to create bone beads (the chisel on the tool belt will not work)." },
  {
    text = "Use the bone beads on the bronze wire to create beads of the dead.<ul><li>You can make the bronze wire from a bronze bar at an anvil near Trufitus.</li></ul>",
  },
  {
    text = "Travel east of Trufitus' hut, over the agility log and run north, to the general area of The Shaikahan.",
    title = "Rashiliyia's Tomb",
    neededItems = {
      ["Chisel"] = { quantity = 1 },
      ["Bone shard"] = { quantity = 1 },
      ["Beads of the dead"] = { quantity = 1 },
      ["Bones"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Search the foliage, then attempt to open the Ancient Door. (See the map for the location.)" },
  { text = "Use a chisel on the bone shard." },
  { text = "Equip the beads of the dead." },
  { text = "Use the bone key on the door to open it." },
  { text = "Open the ancient metal gate." },
  { text = "Climb down the rock." },
  { text = "Run to the south west corner and use 3 bones on the tomb doors. Be careful not to bury them instead." },
  { text = "Open the doors and search the tomb dolmen." },
  {
    text = "Kill all 3 forms of Nazastarool. The stalagmites to the east of the tomb dolmen can be used as a safe spot.",
  },
  { text = "Take Rashiliyia corpse." },
  { text = "Travel back to the Tomb of Bervirius on Cairn Isle.", title = "Finishing up" },
  { text = "Use the Rashiliyia corpse on the tomb dolmen." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Shilo Village",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1043625600,
  prereqQuests = { "Jungle Potion" },
})
