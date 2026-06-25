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
    text = "Optional: Equip a ring of visibility to skip some dialogue.",
    title = "The World Gate",
    neededItems = {
      ["Face mask"] = { quantity = 1 },
      ["Masked earmuffs"] = { quantity = 1 },
      ["Slayer helmet"] = { quantity = 1 },
      ["Slayer helmet stand"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Find the World Gate, south of the Eagles' Peak lodestone." },
  { text = "Speak to Azzanadra.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("I'll help if I can."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Okay, let's hear your proposal."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "The following part can be skipped if you already activated the World Gate after Mahjarrat Memories. NOTE: A current bug causes the gate symbols to fail to display correctly while you rotate the mechanism. Even if you appear to enter the correct combination, the gate will not open if this visual bug occurs. Use the audio and visual cues instead. When you rotate to the correct symbol, the gate gives off a bright glow and a clear sound effect. Advance to the next symbol only after you see the glow and hear the sound.<ul><li>Click the controls right near the gate until you reach .</li><li>Rotate left until you reach .</li><li>Rotate right until you reach .</li><li>Step away from the controls, and speak to Sliske.</li></ul>",
  },
  {
    text = "Put on a face mask or equivalent protection to avoid taking 50 damage every 15 seconds while in Freneskae.",
  },
  { text = "Enter the World Gate to enter Freneskae." },
  {
    text = "Run through Freneskae using the map below to reach the Sanctum. Move fast and avoid the hazards. There are healing points along the way if needed.",
    title = "Freneskae",
  },
  {
    text = "Optional: collect the memoriam crystals along the way. They are numbered on the map below; for efficiency, start with collecting number 9, then 2, 1, 4, 5, 6, 7, 8.",
  },
  { text = "Optional: Deposit collected Memoriam crystals in the middle to see memories.", title = "The Sanctum" },
  { text = "Go to North side of the room to the door puzzle." },
  {
    text = "Match the symbols up together to light up the lines in between them three times to open the door, then enter.",
  },
  {
    text = "Kill the four nihils then enter the next area to the north. All nihils use a special attack after hissing. Each nihil always runs in the same direction. Therefore, a pair of nihil (ice and shadow or smoke and blood) can be killed first, leaving areas between the tracks unused and safe. Don't stand in the middle, as you might be dashed by multiple nihils at once.<ul><li>Killing the nihils while all 4 are attacking you will unlock the title [Name] the Annihilator and the achievement Annihilator.</li><li>The smoke nihil attacks with Magic. Its special attack will decrease Attack, Strength, Magic, Ranged, and Defence by 15. Its charge will decrease these stats by 5.</li><li>The shadow nihil attacks with Ranged, and its special attack creates Nex-like shadow bomb at the position of player that deals rapid damage. Players should move away as soon as the floor below them is darkened by a circular spot. Shadow nihil darkens the whole screen whenever it approaches the player, both when charging or fighting the player, but this causes no gameplay difference.</li><li>The blood nihil attacks with melee. Its special attack causes all incoming damage to heal it, indicated when the creature lowers its head and begin to glow. It also will passively heal other nihil if it is damaged. Its charge inflicts a bleed similar to Slaughter that begins at 100 damage.</li><li>The ice nihil attacks with Magic, and its special attack applies a six second stun and drains player's Prayer points by 50% of its maximum. Its charge drains the player's Prayer points by 25% of its maximum.</li></ul>",
  },
  {
    text = "Attempt to descend the ledge to the north then speak to Zaros.<ul><li>If you accept Zaros' help against Mah, he will provide you with full prayer for use with Ancient Curses along the fight and less damage from Mah.</li></ul>",
    title = "Meeting Zaros",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I've heard all I need to."),
      Action.ConversationHighlight:new("No, just tell me the crucial info."),
      Action.ConversationHighlight:new("Yes, allow him in for now."),
    },
  },
  {
    text = "Defend against monsters while Mah has her nightmare (progress displayed at the top of the screen).<ul><li>Use Freedom or Anticipate after Mah clenches her hands to avoid being stunned when she screams in agony (though you will still take damage).</li></ul>",
  },
  { text = "When the progress is 0%, climb down the ledge next to Mah's hand." },
  {
    text = "Collect The Measure and place it on the ground near faint wisps to activate them for harvesting (wisps can be found on the centre and edges of the cave. After revealing one, it will be a yellow dot on minimap).",
  },
  {
    text = "Convert Mah memories into Mah Energy using the Energy rift. When you gather 250 energy, weave it into a simulacrum (dark to help Zaros, light to hinder Zaros). Hop worlds if you are having trouble finding wisps. You will be outside of the World Gate, enter again using the checkpoint option.",
  },
  {
    text = "Return and speak to Zaros by exiting the rock face on the wall between two hallways (west of the energy rift). Speak to Zaros again after the cutscene.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Talk to Azzanadra through the world gate.",
    actions = { Action.ConversationHighlight:new("I'm ready."), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Fate of the Gods",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1395619200,
  prereqQuests = {
    "Missing, Presumed Death",
    "The World Wakes",
    "Ritual of the Mahjarrat",
    "The Temple at Senntisten",
    "The Firemaker's Curse",
  },
})
