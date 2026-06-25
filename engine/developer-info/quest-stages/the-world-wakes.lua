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
    text = "Talk to Orlando Smith, West of the Legends' Guild (fairy code BLR, or use the Ardougne lodestone).",
    title = "Helping Orlando",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Can I help at all?"),
      Action.ConversationHighlight:new("So you just need me to open the doors?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Open the door." },
  {
    text = "Investigate the following 6 artefacts:<ul><li>Inactive obelisk (north-west)</li><li>Stone carvings (north)</li><li>The other stone carvings (north-east)</li><li>Clouded vials (north-east)</li><li>Dusty parchment (south-east)</li><li>Shattered blade (south)</li></ul>",
  },
  { text = "Investigate the Guthixian statue to the east." },
  {
    text = "Enter the next room to the east and kill the 3 automatons.<ul><li>You must finish the entire dialogue with the Automatons before clicking to attack them (there's a small break in the dialogue) or you will have to redo the cutscene again.</li><li>Warning: Automatons can deal high damage to players standing within their special attack range. Lower-level players should use ranged, magic, or necromancy to attack them.</li></ul>",
  },
  {
    text = "Talk to Sliske.",
    actions = {
      Action.ConversationHighlight:new("I, [Player], stand before you! Do your worst!"),
      Action.ConversationHighlight:new("What does this mean?"),
      Action.ConversationHighlight:new("What do you mean, trouble?"),
      Action.ConversationHighlight:new("Okay then."),
    },
  },
  {
    text = "Continue talking to Sliske after the cutscene.",
    actions = {
      Action.ConversationHighlight:new("What do you gain from telling me this?"),
      Action.ConversationHighlight:new("So how do I come into this?"),
      Action.ConversationHighlight:new("I already know my decision."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "If Kree'arra and Commander Zilyana don't spawn, talk to Sliske again." },
  {
    text = "After the cutscenes, talk to Kree'arra.",
    title = "Defeating Kree'arra",
    actions = {
      Action.ConversationHighlight:new("Why are you following Zilyana, then?"),
      Action.ConversationHighlight:new("If you want to get rid of me, you'll have to do it the hard way."),
    },
  },
  { text = "Defeat Kree'arra. Avoid standing on the tornadoes." },
  { text = "Pass through the door (east)." },
  {
    text = "Run to the other side of the room and activate the control panel.",
    title = "Lock mechanism",
    actions = { Action.ConversationHighlight:new("Guthix is in trouble. I'm here to protect him.") },
  },
  {
    text = "While using the control panel you can release a boulder by clicking on the dispenser in the corner of the screen. The goal is to get the boulder from the dispenser to the receptacle tile (circular cavity with a green dot).",
  },
  {
    text = "To solve the puzzle:<ul><li>Rotate the tracks to create a path from the dispenser to the green hole, matching the solutions below (NOTE: puzzle can crash based on camera settings try classic)</li><li>Click the appropriate dispenser to release the boulder</li><li>When the boulder halts at a gate, wait for the next tile to auto-rotate such that the path continues, and click the gates to release the boulder at the right time</li></ul>",
  },
  { text = "After the cutscenes (skippable), view the strange map to the east.", title = "Leading the guardians" },
  {
    text = "Answer four randomised questions to balance the pillars (Chaos, Order, Good, and Evil must all be exactly 50%). Refer to the below table and pick the appropriate answers:",
  },
  {
    text = '<table class="wikitable sticky-header"><tbody><tr><th style="width:40ch">Question</th><th style="width:42ch">Options</th><th style="background-color:Orange;">Chaos</th><th style="background-color:DodgerBlue;">Order</th><th style="background-color:MediumSeaGreen;">Good</th><th style="background-color:Tomato;">Evil</th></tr><tr><td rowspan="5">You find a world without balance. Chaos triumphs and violence is rife. The naragi - inhabitants of this world - fight over limited resources. What is your first action to bring balance?</td></tr><tr><td>Give the naragi rules to follow to live in peace.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Introduce more resources.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Impose rules on the naragi, smiting those who disobey.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Give the naragi knowledge to advance their weapons.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td colspan="6"></td></tr><tr><td rowspan="5">Soon a young warlord begins to gain power, earning respect through fear. If she were to rule, she would take all the resources for her court. Most naragi would have to fend for themselves. What do you do?</td></tr><tr><td>Fully back her.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Encourage the naragi to choose a more peaceful leader.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Encourage the naragi to stand against her forcefully.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Persuade the warlord to enforce rules for resources.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td colspan="6"></td></tr><tr><td rowspan="5">You sense rumbling in the mountains. A volcano is preparing to explode. What action do you take in the wake of this threat?</td></tr><tr><td>Warn them at the last minute.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Stop the disaster and avoid destruction.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Do nothing; let nature take its course.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Give the naragi plenty of warning to get to safety.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td colspan="6"></td></tr><tr><td rowspan="5">Over the next few years, a sinister disease becomes evident in some areas. It is threatening to spread widely. What action do you take?</td></tr><tr><td>Quarantine the areas that contain the disease.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Destroy the areas that contain the disease.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Encourage the naragi to take care of themselves.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Introduce medicines and knowledge to the naragi.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td colspan="6"></td></tr><tr><td rowspan="5">A council of naragi are gaining popularity to rule. They would bring rules of honour and justice to the population. What action do you take during the election?</td></tr><tr><td>Tell the council to use more drastic tactics.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Encourage the naragi to peacefully stand against them.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Kill the council.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Back the council fully.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td colspan="6"></td></tr><tr><td rowspan="5">Aside from overpopulation, the earth itself is unstable. You sense an earthquake on the horizon. What action do you take?</td></tr><tr><td>Give the naragi plenty of warning to get to safety.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Warn them at the last minute.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Stop the disaster and avoid destruction.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Do nothing; let nature take its course.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td colspan="6"></td></tr><tr><td rowspan="5">Tensions in the naragi society are building. Violence is flaring up. A full-blown war is liable to start at any moment. What do you do?</td></tr><tr><td>Encourage the naragi warlords to meet diplomatically.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Threaten the warlords if they break the peace.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Seed violence between the naragi warlords.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Introduce a natural disaster to distract the naragi.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td colspan="6"></td></tr><tr><td rowspan="5">As time passes, a warlord begins to increase in power. If he rules the naragi, he will enforce strict laws and harsh punishment. What action do you take?</td></tr><tr><td>Kill the potential ruler.</td><td style="background-color:Orange;">25%</td><td></td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Provide him with weapons to help him rule.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td></td><td style="background-color:Tomato;">25%</td></tr><tr><td>Persuade him to give up his cause entirely.</td><td style="background-color:Orange;">25%</td><td></td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr><tr><td>Persuade him to relax his punishments.</td><td></td><td style="background-color:DodgerBlue;">25%</td><td style="background-color:MediumSeaGreen;">25%</td><td></td></tr></tbody></table>',
  },
  {
    text = "Give the group a motivational speech",
    actions = {
      Action.ConversationHighlight:new("Enthuse the group."),
      Action.ConversationHighlight:new("Reinforce the importance of protecting the edicts."),
      Action.ConversationHighlight:new("Highlight the strengths of the group."),
    },
  },
  { text = "Drag the guardian icons to the correct threat based on the hover text in the top row:" },
  --TODO: Add table
  {
    text = "Talk to Juna.",
    actions = {
      Action.ConversationHighlight:new("I've finished setting up."),
      Action.ConversationHighlight:new("I'm happy with the formation for now."),
    },
  },
  {
    text = "Finish the dialogue after the cutscene (including a pause in dialogue) or you will be forced to watch it again.",
  },
  { text = "Drink from the elixirs if you need to restore your health or prayer." },
  { text = "Note: you can bank if you talk to Druidess. Look for the bank icon on the minimap." },
  {
    text = "Enter the north-west storage wing guarded by Chaeldar and Thaerisk Cemphier.",
    title = "Defeating Graardor",
    actions = { Action.ConversationHighlight:new("Yes, I'm sure. Let's go.") },
  },
  { text = "Defeat General Graardor. Protect from Melee is preferred." },
  { text = "Enter the exit." },
  {
    text = "Enter the north storage wing guarded by Cres.",
    title = "Defeating Zemouregal",
    actions = { Action.ConversationHighlight:new("Yes, I'm sure. Let's go.") },
  },
  { text = "Defeat Zemouregal. Bringing Antipoison is recommended." },
  --TODO: Add bulleted list
  { text = "Enter the exit." },
  {
    text = "Enter the south-west storage wing guarded by Death.",
    title = "Defeating K'ril Tsutsaroth",
    actions = { Action.ConversationHighlight:new("Yes, I'm sure. Let's go.") },
  },
  { text = "Defeat K'ril Tsutsaroth. Be aware of his special attacks." },
  { text = "Enter the exit." },
  { text = "Enter the south storage wing guarded by Fiara and Valluta.", title = "Defeating Enakhra" },
  { text = "Defeat Enakhra." },
  --TODO: Add bulleted list
  { text = "Enter the exit." },
  { text = "Talk to Cres in the center.", title = "Protecting Juna" },
  { text = "Talk to Juna in the east." },
  { text = "Defend Juna from the spiritual enemies for 2 minutes." },
  --TODO: Add bulleted list
  {
    text = "Talk to Azzanadra after the skippable cutscene.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Teleport out to bank all gear and items; bring high-healing food.", title = "Guthix" },
  { text = "Enter the shattered wall." },
  {
    text = "Run down the path, avoiding the boulders and flames. If you get trapped between ice spikes, click them to break free.",
  },
  { text = "After the skippable cutscene, walk east down to the pedestal in front of Guthix. He will teleport you." },
  {
    text = "Walk between the islands, talking to Guthix along the way.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "On your return, talk to one of the guardians.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "After a skippable cutscene, Saradomin will appear in front of you. Talk to him.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to Juna (9 free inventory slots are required)." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The World Wakes",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1362355200,
  prereqQuests = {
    "Ritual of the Mahjarrat",
    "The Firemaker's Curse",
    "The Branches of Darkmeyer",
    "The Void Stares Back",
    "The Chosen Commander",
    "Ritual of the Mahjarrat",
    "The Firemaker's Curse",
    "The Branches of Darkmeyer",
    "The Void Stares Back",
    "The Chosen Commander",
  },
})
