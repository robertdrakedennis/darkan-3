local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

---@type QuestStep[]
local steps = {
  { text = "Travel to Port Phasmatys.", title = "Setting sail" },
  {
    text = "Talk to Bill Teach in the inn.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Yes, I've always wanted to be a pirate!") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Run to the dock and cross the gangplank to board the eastern-most ship." },
  {
    text = "Talk to Bill Teach.",
    actions = { Action.ConversationHighlight:new("Let's go Cap'n!") },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "Search the repair locker, take 4 ropes.", title = "Sabotage" },
  { text = "Search the Gun Locker, take 1 fuse." },
  { text = "Climb-up the ship's ladder." },
  { text = "Climb the climbing net next to the central mast." },
  {
    text = "Use rope from the backpack on the hoisted sail on the ship you are on.<ul><li>The same actions of climbing and using the rope are used to change ships every time.</li></ul>",
  },
  { text = "Add fuse on the powder barrel on the eastern ship." },
  { text = "Light the fuse (you may fail and have to retry)." },
  { text = "Climb the climbing net and use the rope on the hoisted sail as before." },
  { text = "Talk to Bill." },
  { text = "Climb-down the ship's ladder.", title = "Fixing the leaks" },
  { text = "Search the repair locker and take:<ul><li>30 tacks</li><li>6 planks</li><li>3 swamp paste</li></ul>" },
  {
    text = "For each of the three leaks in the hull:<ul><li>Fill the ship hull.</li><li>Waterproof the ship hull.</li></ul>",
  },
  {
    text = "Climb-up the ship's ladder and talk to Bill.<ul><li>Be sure to finish all the dialogue else you won't be able to proceed.</li></ul>",
  },
  {
    text = "Board the other ship (climb and use rope as before).",
    title = "Pirate plunder",
    neededItems = { ["Rope"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Climb-down their Ship's ladder." },
  { text = "Plunder the closed chest for 5 plunder." },
  { text = "Return to your ship." },
  { text = "Climb-down your ship's ladder." },
  { text = "Store-plunder on the plunder storage." },
  { text = "Return to and talk to Bill." },
  { text = "Climb-down the ship's ladder.", title = "Cannon" },
  { text = "Search the gun locker for a cannon barrel." },
  { text = "Repair the broken cannon on the top deck." },
  { text = "Talk to Bill." },
  {
    text = "Search the gun locker and take:<ul><li>Ramrod.</li><li>Some fuses (take 3 in case of failure).</li><li>Some cannon canisters (take 3 in case of failure).</li></ul>",
  },
  { text = "Climb-up the ship's ladder." },
  {
    text = "Prepare and fire the cannon. If you miss you will need to repeat these steps.<ul><li>If you make a mistake, you can empty-out the cannon. You will still need to clean it with the ramrod (final instruction).</li><li>Take-powder from the powder barrel next to the cannon on the deck.</li><li>Use gunpowder on the cannon.</li><li>Then the ramrod.</li><li>Then a canister.</li><li>Then a fuse.</li><li>Fire! the cannon. Check your chat log to see if you hit or not.</li><li>Use the ramrod on the cannon again to clean it.</li></ul>",
  },
  { text = "Talk to Bill." },
  {
    text = "Return to the gun locker and take:<ul><li>At least 5 cannon balls.</li><li>At least 5 fuses.</li><li>2 cannon barrel (the cannon may blow up).</li></ul>",
  },
  {
    text = "Fire the cannon as before, substituting the canister for a cannon ball, until you put three holes in the enemy's ship.",
  },
  { text = "Quest complete!" },
  { text = "Talk to Bill to claim another reward." },
}

return Quest:new({
  name = "Cabin Fever",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1139270400,
  prereqQuests = { "Pirate's Treasure", "Rum Deal" },
})
