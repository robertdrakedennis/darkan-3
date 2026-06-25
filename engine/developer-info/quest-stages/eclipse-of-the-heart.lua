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
    text = "Enter the Golden Palace in Menaphos.",
    title = "Gathering desert demigods",
    neededItems = {
      ["Cat"] = { quantity = 1 },
      ["Monkeyspeak amulet"] = { quantity = 1 },
      ["Cramulet"] = {
        quantity = 1,
      },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Speak with Leela inside the pyramid.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Speak with Leela outside the pyramid." },
  {
    text = "Cross the bridge to Sophanem and speak with Ozan.",
    actions = { Action.ConversationHighlight:new("Continue."), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Speak with the following citizens (if you need to leave the area to visit a bank, you will need to talk to Ozan again to proceed):<ul><li>The Sphinx (west of larger pyramid, cat is required but catspeak amulet is not required to proceed the quest)</li><li>Jex (inside the north-east temple)</li><li>Raetul (north-east of larger pyramid; by silk stall)</li><li>Urbi (south of the larger pyramid)</li><li>Nathifa, south of the Temple of Icthlarin (must speak to the other 4 before Nathifa dialogue progresses)</li></ul>",
    actions = {
      Action.ConversationHighlight:new("What proposition? (Continue quest)"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Speak to Ozan and prepare well for an intense battle which includes four waves of multiple heavy hitting enemies and stuns. Death is unsafe here.",
  },
  {
    text = "Go to the Sophanem Slayer Dungeon and inspect the dungeon entrance.<ul><li>If this option does not appear, speak to Ozan again.</li></ul>",
  },
  {
    text = "Kill the waves of soul devourers. You may need to speak to Het or Apmeken to continue receiving waves. If you die, you will have to restart all waves.",
  },
  { text = "Talk to Het or Apmeken and finish the dialogue." },
  { text = "Return to Ozan and finish the dialogue." },
  {
    text = "Speak to Scabaras in the Temple of Isolation with at least 1 backpack space.<ul><li>The entrance to the temple is north of Pollnivneach in the Scabarite Cavern, where you can use the old boat.</li></ul>",
    actions = { Action.ConversationHighlight:new("(Continue quest)") },
  },
  {
    text = "Head to Het's Oasis and speak to Het (outside west entrance) to continue the quest and be transported to the agility course.<ul><li>There is a possible graphical bug. After talking to Het to continue the quest, your screen will remain black. Re-log and talk to Het again to fix.</li></ul>",
  },
  { text = "Talk to Het to be tasked with doing the agility course." },
  { text = "Complete 1 lap of the agility course (from your current location) by climbing the rope ladder." },
  { text = "Start a new lap on the course until you get back to Crondis and Het." },
  { text = "Speak to Het by the rope ladder." },
  {
    text = "Gather 1 of each flower from the perfect flower bushes in each corner of Het's Oasis, do not teleport as you will need to speak with Het again. Each one will be marked with a light beam.<ul><li>Perfect iris bush (north-east)</li><li>Perfect rose bush (north)</li><li>Perfect hollyhock bush (south)</li><li>Perfect hydrangea bush (south-east)</li></ul>",
  },
  { text = "Speak to Crondis by the rope ladder." },
  {
    text = "Go to the monkey colony east of the Bandit Camp and talk to Apmeken with a monkeyspeak amulet or cramulet equipped.",
  },
  {
    text = "Run slightly north (somewhere quieter) and talk to Apmeken again.",
    actions = { Action.ConversationHighlight:new("We could go get a chimp ice? (Continue quest)") },
    postconditions = { Condition.ConversationText:new(" A what?") },
  },
  {
    text = "Head to choc ice stall in Nardah and speak to Rokuh.<ul><li>You may teleport to Nardah using a desert amulet 2 or higher.</li><li>Buying choc Ice without speaking to him will not progress the quest.</li></ul>",
  },
  {
    text = "Head back to the monkey colony and continue quest (unlike other instances, players may teleport without the item melting, and ice spells are not required).",
  },
  {
    text = "Kill Mizaru, Kikazaru and Iwazaru.<ul><li>Remember to kill the demons for Kikazaru; beware Iwazaru can unequip your helmet to the backpack.</li></ul>",
  },
  { text = "Speak to Apmeken in the Monkey Colony to receive Apmeken's Favor." },
  { text = "Speak to Scabaras in the Temple of Isolation." },
  {
    text = "Puzzle solution:<ul><li>Het northeast, then north</li><li>Scabaras north, then northeast</li><li>Elidinis west once</li><li>Apmeken west once</li><li>Scabaras southeast</li><li>Elidinis northeast</li><li>Apmeken west, then south</li><li>Scabaras west twice</li><li>Crondis north, then west</li><li>Elidinis southeast, then south</li><li>Het south, then southeast</li><li>Scabaras northeast, then north</li><li>Crondis west once</li><li>Het northwest once</li><li>Elidinis north, then west</li><li>Het southeast, then south</li><li>Scabaras south, then southeast</li></ul>",
  },
  { text = "Talk to memory of Tumeken" },
  { text = "Go through gate" },
  {
    text = "Move:<ul><li>Icthlarin north west</li><li>Scabaras west</li><li>Het north east, then north west</li><li>Amascut north west, then north east</li><li>Apmeken east</li><li>Crondis south east</li><li>Scabaras south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Apmeken north east</li><li>Crondis east then south east</li><li>Apmeken south west</li><li>Amascut south east</li><li>Het east</li></ul>",
  },
  {
    text = "Move:<ul><li>Icthlarin south east</li><li>Apmeken west</li><li>Amascut south west</li><li>Het south east</li><li>Icthlarin east</li><li>Scabaras north east</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken north west</li><li>Amascut west</li><li>Het south west</li><li>Icthlarin south east</li><li>Scabaras east</li><li>Apmeken north east, then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Scabaras west, then south west</li><li>Icthlarin north west, then west</li><li>Het north east, then north west</li><li>Amascut east, then north east</li><li>Scabaras south east, then east</li><li>Icthlarin south west, then south east</li><li>Apmeken south east, then south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Scabaras north east</li><li>Icthlarin east</li><li>Apmeken south east</li><li>Het south west</li><li>Amascut west then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het north east, then east</li><li>Apmeken north west, then north east</li><li>Icthlarin west, then north west</li><li>Scabaras south west, then west</li><li>Het south east</li><li>Apmeken east</li></ul>",
  },
  { text = "Talk to memory of Tumeken" },
  { text = "Go through gate" },
  {
    text = "Move:<ul><li>Elidinis northwest</li><li>Icthlarin west</li><li>Crondis northeast</li><li>Apmeken east</li><li>Scabaras east</li><li>Het southeast</li><li>Amascut west</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southwest</li><li>Icthlarin northwest</li><li>Crondis west</li><li>Apmeken northeast</li><li>Scabaras east</li><li>Elidinis southeast</li><li>Amascut east</li></ul>",
  },
  {
    text = "Move:<ul><li>Het northwest</li><li>Elidinis west</li><li>Amascut southeast</li><li>Icthlarin southwest</li><li>Crondis northwest</li><li>Apmeken west</li><li>Scabaras northeast</li></ul>",
  },
  {
    text = "Move:<ul><li>Amascut east</li><li>Icthlarin southeast</li><li>Het east</li><li>Elidinis northwest</li><li>Icthlarin west</li><li>Het southeast</li><li>Crondis southwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken northwest</li><li>Het northeast</li><li>Crondis southeast</li><li>Apmeken southwest</li><li>Het northwest</li><li>Crondis northeast</li><li>Icthlarin east</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southeast</li><li>Apmeken west</li><li>Icthlarin northwest</li><li>Crondis southwest</li><li>Het southeast</li><li>Icthlarin northeast</li><li>Crondis northwest (this opens the other pillar space)</li></ul>",
  },
  {
    text = "Move:<ul><li>Het southwest</li><li>Icthlarin southeast</li><li>Crondis northeast, then north</li><li>Icthlarin northwest then southwest</li><li>Het northeast, then northwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis east then northeast</li><li>Icthlarin southeast then west</li><li>Elidinis southwest</li><li>Het southeast</li><li>Crondis south, then southwest</li></ul>",
  },
  { text = "Go through gate." },
  { text = "Open sarcophagus." },
  { text = "Talk to memory of Tumeken." },
  {
    text = "Talk to Scabaras (down the first set of stairs to the south) - Make sure to finish the dialogue, there's a brief animation.",
  },
  {
    text = "Head to Menaphos and speak to Pharaoh Leela at the top of the stairs north-west of the Menaphos lodestone, not inside the throne room.",
  },
  {
    text = "Puzzle solution:<ul><li>Het northeast, then north</li><li>Scabaras north, then northeast</li><li>Elidinis west once</li><li>Apmeken west once</li><li>Scabaras southeast</li><li>Elidinis northeast</li><li>Apmeken west, then south</li><li>Scabaras west twice</li><li>Crondis north, then west</li><li>Elidinis southeast, then south</li><li>Het south, then southeast</li><li>Scabaras northeast, then north</li><li>Crondis west once</li><li>Het northwest once</li><li>Elidinis north, then west</li><li>Het southeast, then south</li><li>Scabaras south, then southeast</li></ul>",
  },
  { text = "Talk to memory of Tumeken" },
  { text = "Go through gate" },
  {
    text = "Move:<ul><li>Icthlarin north west</li><li>Scabaras west</li><li>Het north east, then north west</li><li>Amascut north west, then north east</li><li>Apmeken east</li><li>Crondis south east</li><li>Scabaras south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Apmeken north east</li><li>Crondis east then south east</li><li>Apmeken south west</li><li>Amascut south east</li><li>Het east</li></ul>",
  },
  {
    text = "Move:<ul><li>Icthlarin south east</li><li>Apmeken west</li><li>Amascut south west</li><li>Het south east</li><li>Icthlarin east</li><li>Scabaras north east</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken north west</li><li>Amascut west</li><li>Het south west</li><li>Icthlarin south east</li><li>Scabaras east</li><li>Apmeken north east, then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Scabaras west, then south west</li><li>Icthlarin north west, then west</li><li>Het north east, then north west</li><li>Amascut east, then north east</li><li>Scabaras south east, then east</li><li>Icthlarin south west, then south east</li><li>Apmeken south east, then south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Scabaras north east</li><li>Icthlarin east</li><li>Apmeken south east</li><li>Het south west</li><li>Amascut west then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het north east, then east</li><li>Apmeken north west, then north east</li><li>Icthlarin west, then north west</li><li>Scabaras south west, then west</li><li>Het south east</li><li>Apmeken east</li></ul>",
  },
  { text = "Talk to memory of Tumeken" },
  { text = "Go through gate" },
  {
    text = "Move:<ul><li>Elidinis northwest</li><li>Icthlarin west</li><li>Crondis northeast</li><li>Apmeken east</li><li>Scabaras east</li><li>Het southeast</li><li>Amascut west</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southwest</li><li>Icthlarin northwest</li><li>Crondis west</li><li>Apmeken northeast</li><li>Scabaras east</li><li>Elidinis southeast</li><li>Amascut east</li></ul>",
  },
  {
    text = "Move:<ul><li>Het northwest</li><li>Elidinis west</li><li>Amascut southeast</li><li>Icthlarin southwest</li><li>Crondis northwest</li><li>Apmeken west</li><li>Scabaras northeast</li></ul>",
  },
  {
    text = "Move:<ul><li>Amascut east</li><li>Icthlarin southeast</li><li>Het east</li><li>Elidinis northwest</li><li>Icthlarin west</li><li>Het southeast</li><li>Crondis southwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken northwest</li><li>Het northeast</li><li>Crondis southeast</li><li>Apmeken southwest</li><li>Het northwest</li><li>Crondis northeast</li><li>Icthlarin east</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southeast</li><li>Apmeken west</li><li>Icthlarin northwest</li><li>Crondis southwest</li><li>Het southeast</li><li>Icthlarin northeast</li><li>Crondis northwest (this opens the other pillar space)</li></ul>",
  },
  {
    text = "Move:<ul><li>Het southwest</li><li>Icthlarin southeast</li><li>Crondis northeast, then north</li><li>Icthlarin northwest then southwest</li><li>Het northeast, then northwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis east then northeast</li><li>Icthlarin southeast then west</li><li>Elidinis southwest</li><li>Het southeast</li><li>Crondis south, then southwest</li></ul>",
  },
  { text = "Go through gate." },
  { text = "Open sarcophagus." },
  { text = "Talk to memory of Tumeken." },
  {
    text = "Talk to Scabaras (down the first set of stairs to the south) - Make sure to finish the dialogue, there's a brief animation.",
  },
  {
    text = "Head to Menaphos and speak to Pharaoh Leela at the top of the stairs north-west of the Menaphos lodestone, not inside the throne room.",
  },
  {
    text = "Puzzle solution:<ul><li>Het northeast, then north</li><li>Scabaras north, then northeast</li><li>Elidinis west once</li><li>Apmeken west once</li><li>Scabaras southeast</li><li>Elidinis northeast</li><li>Apmeken west, then south</li><li>Scabaras west twice</li><li>Crondis north, then west</li><li>Elidinis southeast, then south</li><li>Het south, then southeast</li><li>Scabaras northeast, then north</li><li>Crondis west once</li><li>Het northwest once</li><li>Elidinis north, then west</li><li>Het southeast, then south</li><li>Scabaras south, then southeast</li></ul>",
  },
  { text = "Talk to memory of Tumeken" },
  { text = "Go through gate" },
  {
    text = "Move:<ul><li>Icthlarin north west</li><li>Scabaras west</li><li>Het north east, then north west</li><li>Amascut north west, then north east</li><li>Apmeken east</li><li>Crondis south east</li><li>Scabaras south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Apmeken north east</li><li>Crondis east then south east</li><li>Apmeken south west</li><li>Amascut south east</li><li>Het east</li></ul>",
  },
  {
    text = "Move:<ul><li>Icthlarin south east</li><li>Apmeken west</li><li>Amascut south west</li><li>Het south east</li><li>Icthlarin east</li><li>Scabaras north east</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken north west</li><li>Amascut west</li><li>Het south west</li><li>Icthlarin south east</li><li>Scabaras east</li><li>Apmeken north east, then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Scabaras west, then south west</li><li>Icthlarin north west, then west</li><li>Het north east, then north west</li><li>Amascut east, then north east</li><li>Scabaras south east, then east</li><li>Icthlarin south west, then south east</li><li>Apmeken south east, then south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Scabaras north east</li><li>Icthlarin east</li><li>Apmeken south east</li><li>Het south west</li><li>Amascut west then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het north east, then east</li><li>Apmeken north west, then north east</li><li>Icthlarin west, then north west</li><li>Scabaras south west, then west</li><li>Het south east</li><li>Apmeken east</li></ul>",
  },
  { text = "Talk to memory of Tumeken" },
  { text = "Go through gate" },
  {
    text = "Move:<ul><li>Elidinis northwest</li><li>Icthlarin west</li><li>Crondis northeast</li><li>Apmeken east</li><li>Scabaras east</li><li>Het southeast</li><li>Amascut west</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southwest</li><li>Icthlarin northwest</li><li>Crondis west</li><li>Apmeken northeast</li><li>Scabaras east</li><li>Elidinis southeast</li><li>Amascut east</li></ul>",
  },
  {
    text = "Move:<ul><li>Het northwest</li><li>Elidinis west</li><li>Amascut southeast</li><li>Icthlarin southwest</li><li>Crondis northwest</li><li>Apmeken west</li><li>Scabaras northeast</li></ul>",
  },
  {
    text = "Move:<ul><li>Amascut east</li><li>Icthlarin southeast</li><li>Het east</li><li>Elidinis northwest</li><li>Icthlarin west</li><li>Het southeast</li><li>Crondis southwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken northwest</li><li>Het northeast</li><li>Crondis southeast</li><li>Apmeken southwest</li><li>Het northwest</li><li>Crondis northeast</li><li>Icthlarin east</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southeast</li><li>Apmeken west</li><li>Icthlarin northwest</li><li>Crondis southwest</li><li>Het southeast</li><li>Icthlarin northeast</li><li>Crondis northwest (this opens the other pillar space)</li></ul>",
  },
  {
    text = "Move:<ul><li>Het southwest</li><li>Icthlarin southeast</li><li>Crondis northeast, then north</li><li>Icthlarin northwest then southwest</li><li>Het northeast, then northwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis east then northeast</li><li>Icthlarin southeast then west</li><li>Elidinis southwest</li><li>Het southeast</li><li>Crondis south, then southwest</li></ul>",
  },
  { text = "Go through gate." },
  { text = "Open sarcophagus." },
  { text = "Talk to memory of Tumeken." },
  {
    text = "Talk to Scabaras (down the first set of stairs to the south) - Make sure to finish the dialogue, there's a brief animation.",
  },
  {
    text = "Head to Menaphos and speak to Pharaoh Leela at the top of the stairs north-west of the Menaphos lodestone, not inside the throne room.",
  },
  {
    text = "Move:<ul><li>Icthlarin north west</li><li>Scabaras west</li><li>Het north east, then north west</li><li>Amascut north west, then north east</li><li>Apmeken east</li><li>Crondis south east</li><li>Scabaras south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Apmeken north east</li><li>Crondis east then south east</li><li>Apmeken south west</li><li>Amascut south east</li><li>Het east</li></ul>",
  },
  {
    text = "Move:<ul><li>Icthlarin south east</li><li>Apmeken west</li><li>Amascut south west</li><li>Het south east</li><li>Icthlarin east</li><li>Scabaras north east</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken north west</li><li>Amascut west</li><li>Het south west</li><li>Icthlarin south east</li><li>Scabaras east</li><li>Apmeken north east, then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Scabaras west, then south west</li><li>Icthlarin north west, then west</li><li>Het north east, then north west</li><li>Amascut east, then north east</li><li>Scabaras south east, then east</li><li>Icthlarin south west, then south east</li><li>Apmeken south east, then south west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het west</li><li>Amascut north west</li><li>Scabaras north east</li><li>Icthlarin east</li><li>Apmeken south east</li><li>Het south west</li><li>Amascut west then north west</li></ul>",
  },
  {
    text = "Move:<ul><li>Het north east, then east</li><li>Apmeken north west, then north east</li><li>Icthlarin west, then north west</li><li>Scabaras south west, then west</li><li>Het south east</li><li>Apmeken east</li></ul>",
  },
  { text = "Talk to memory of Tumeken" },
  { text = "Go through gate" },
  {
    text = "Move:<ul><li>Elidinis northwest</li><li>Icthlarin west</li><li>Crondis northeast</li><li>Apmeken east</li><li>Scabaras east</li><li>Het southeast</li><li>Amascut west</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southwest</li><li>Icthlarin northwest</li><li>Crondis west</li><li>Apmeken northeast</li><li>Scabaras east</li><li>Elidinis southeast</li><li>Amascut east</li></ul>",
  },
  {
    text = "Move:<ul><li>Het northwest</li><li>Elidinis west</li><li>Amascut southeast</li><li>Icthlarin southwest</li><li>Crondis northwest</li><li>Apmeken west</li><li>Scabaras northeast</li></ul>",
  },
  {
    text = "Move:<ul><li>Amascut east</li><li>Icthlarin southeast</li><li>Het east</li><li>Elidinis northwest</li><li>Icthlarin west</li><li>Het southeast</li><li>Crondis southwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken northwest</li><li>Het northeast</li><li>Crondis southeast</li><li>Apmeken southwest</li><li>Het northwest</li><li>Crondis northeast</li><li>Icthlarin east</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southeast</li><li>Apmeken west</li><li>Icthlarin northwest</li><li>Crondis southwest</li><li>Het southeast</li><li>Icthlarin northeast</li><li>Crondis northwest (this opens the other pillar space)</li></ul>",
  },
  {
    text = "Move:<ul><li>Het southwest</li><li>Icthlarin southeast</li><li>Crondis northeast, then north</li><li>Icthlarin northwest then southwest</li><li>Het northeast, then northwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis east then northeast</li><li>Icthlarin southeast then west</li><li>Elidinis southwest</li><li>Het southeast</li><li>Crondis south, then southwest</li></ul>",
  },
  { text = "Go through gate." },
  { text = "Open sarcophagus." },
  { text = "Talk to memory of Tumeken." },
  {
    text = "Talk to Scabaras (down the first set of stairs to the south) - Make sure to finish the dialogue, there's a brief animation.",
  },
  {
    text = "Head to Menaphos and speak to Pharaoh Leela at the top of the stairs north-west of the Menaphos lodestone, not inside the throne room.",
  },
  {
    text = "Move:<ul><li>Elidinis northwest</li><li>Icthlarin west</li><li>Crondis northeast</li><li>Apmeken east</li><li>Scabaras east</li><li>Het southeast</li><li>Amascut west</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southwest</li><li>Icthlarin northwest</li><li>Crondis west</li><li>Apmeken northeast</li><li>Scabaras east</li><li>Elidinis southeast</li><li>Amascut east</li></ul>",
  },
  {
    text = "Move:<ul><li>Het northwest</li><li>Elidinis west</li><li>Amascut southeast</li><li>Icthlarin southwest</li><li>Crondis northwest</li><li>Apmeken west</li><li>Scabaras northeast</li></ul>",
  },
  {
    text = "Move:<ul><li>Amascut east</li><li>Icthlarin southeast</li><li>Het east</li><li>Elidinis northwest</li><li>Icthlarin west</li><li>Het southeast</li><li>Crondis southwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Apmeken northwest</li><li>Het northeast</li><li>Crondis southeast</li><li>Apmeken southwest</li><li>Het northwest</li><li>Crondis northeast</li><li>Icthlarin east</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis southeast</li><li>Apmeken west</li><li>Icthlarin northwest</li><li>Crondis southwest</li><li>Het southeast</li><li>Icthlarin northeast</li><li>Crondis northwest (this opens the other pillar space)</li></ul>",
  },
  {
    text = "Move:<ul><li>Het southwest</li><li>Icthlarin southeast</li><li>Crondis northeast, then north</li><li>Icthlarin northwest then southwest</li><li>Het northeast, then northwest</li></ul>",
  },
  {
    text = "Move:<ul><li>Elidinis east then northeast</li><li>Icthlarin southeast then west</li><li>Elidinis southwest</li><li>Het southeast</li><li>Crondis south, then southwest</li></ul>",
  },
  { text = "Go through gate." },
  { text = "Open sarcophagus." },
  { text = "Talk to memory of Tumeken." },
  {
    text = "Talk to Scabaras (down the first set of stairs to the south) - Make sure to finish the dialogue, there's a brief animation.",
  },
  {
    text = "Head to Menaphos and speak to Pharaoh Leela at the top of the stairs north-west of the Menaphos lodestone, not inside the throne room.",
  },
  { text = "Speak to Pharaoh Leela twice." },
  { text = "Speak to Urluk and the council north of shifting tombs." },
  {
    text = "Speak to the four aspects (Het, Apmeken, Scabaras, and Crondis), then Commander Akhomet.<ul><li>If Crondis does not respond to your clicks, move to the south side of the pool edge, and try again.</li></ul>",
  },
  {
    text = "Speak to Leela and assign :<ul><li>Main gate -> Akhomet</li><li>Supporting main gate and flank the enemy -> Apmeken</li><li>East Gate -> Het</li><li>Entrances to the tombs and caverns -> Scabaras</li><li>Healers -> Crondis</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I am"),
      Action.ConversationHighlight:new("Akhomet's forces"),
      Action.ConversationHighlight:new("Apmeken's forces"),
      Action.ConversationHighlight:new("Het's forces"),
      Action.ConversationHighlight:new("Scarabas' forces"),
    },
  },
  { text = "Speak to Icthlarin.", actions = { Action.ConversationHighlight:new("Continue Quest") } },
  { text = "Go to Ungael Ritual Site." },
  { text = "Take the soul beacon north of archivist." },
  { text = "Place the Soul Beacon as a focus on the pedestal. Draw the glyphs for the ensoul beacon ritual." },
  {
    text = "Perform ritual, close shadow rifts as they spawn.<ul><li>If unable to start the ritual, attempt to take the soul beacon again and confirm with yes.</li></ul>",
  },
  { text = "Interact with the beacon west of the ritual site." },
  { text = "Teleport to Um Ritual Site and continue quest at marker." },
  { text = "Speak with Vorkath then Icthlarin." },
  {
    text = "Place the beacon on the pedestal and perform the powerful ritual of severance. Vorkath and Icthlarin will deal with the devourers.",
  },
  { text = "Prepare for the encounter of Amascut, the Devourer." },
  { text = "When you're ready, return to the Um Ritual Site and speak to Icthlarin to continue." },
  { text = "Run through the city to the Great Pyramid and speak to any of the lesser gods for a cutscene." },
  { text = "Speak to Tumeken, click through all the chat options to continue." },
  {
    text = "The fight then begins with Amascut. Attack the soul obelisks, she switches between magic, ranged, and melee attacks. During the last phase, you will be teleported onto a platform.",
  },
  {
    text = "After the fight, go through the dialogue.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Speak to Icthlarin to leave the instance, go through the dialogue to end the quest.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
}

return Quest:new({
  name = "Eclipse of the Heart",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1753660800,
  prereqQuests = { "Pharaoh's Folly" },
})
