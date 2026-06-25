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
    title = "Getting started",
    text = "Talk to Benedict in a house north-west of the Varrock lodestone.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    title = "All players",
    text = "Travel to Varrock Palace. Go to the north-west staircase and then to the top floor to collect the fox pelt.",
  },
  { text = "Head to the roof of Al Kharid Palace and collect the toffee apple." },
  {
    text = "Head to Het's Oasis and go to the statue in the centre to collect the package.<ul><li>Teleport there with a ring of duelling.</li><li>If you are below 65 Agility, run north around the whirligig pool to cross the fallen palm tree, which has no Agility requirement.</li></ul>",
    actions = { Action.ConversationHighlight:new("Het's Oasis") },
  },
  {
    text = "Enter the low-level Runespan portal to collect the crystal to the south-west (on the same island you start on).<ul><li>Teleport there with the wicked hood or stardust.</li></ul>",
  },
  {
    text = "Head  to Mudskipper Point and head south-west to collect the toy ship.<ul><li>Use a fairy Ring AIQ or the Port Sarim lodestone to teleport there</li></ul>",
  },
  { text = "Head to the 2nd floor[UK]3rd floor[US] of Lumbridge Castle and collect a pipe by the north stairs." },
  {
    text = "Head to the west side of the White Knights' Castle in Falador, and climb the stairs and ladders to the roof to collect a monocle.",
  },
  {
    text = "Head to the beacon south of Paterdomus to collect a scarf.<ul><li>Teleport there with either the Skeletal horror Teleport, an invitation box, a Fort Forinthry Teleport, an Archaeology journal.</li><li>Alternatively, teleport there with a Dig Site pendant to the Varrock Dig Site.</li></ul>",
    actions = { Action.ConversationHighlight:new("Digsite") },
  },
  { text = "Teleport to Ashdale lodestone and head directly south to collect the surgical mask." },
  {
    title = "Members-only",
    text = "Head to White Wolf Mountain and collect the raw fish-like thing, which is right next to the glider.<ul><li>Ride on the gnome glider of Captain Dalbur from Al Kharid.</li></ul>",
    actions = { Action.ConversationHighlight:new("Sindarpos") },
  },
  {
    text = "Head to the Grand Tree and head south of the 2nd floor[UK]3rd floor[US] to collect a flask of tea.<ul><li>Ride on the gnome glider.</li><li>Alternatively, teleport via spirit trees.</li></ul>",
    actions = { Action.ConversationHighlight:new("Ta Quir Priw") },
  },
  {
    text = "Head to Castle Wars, then head north-west of the portals, and walk on to the spectating wall to collect the sweets.<ul><li>Teleport with a ring of duelling.</li></ul>",
    actions = { Action.ConversationHighlight:new("Castle Wars Arena") },
  },
  {
    text = "Enter the Polypore Dungeon and go slightly east to collect cinnamon, which is next to the neem drupe branch.<ul><li>Use a fairy Ring BIP to teleport there.</li><li>Alternatively, the Dungeoneering cape teleport (0, 7) or</li><li>Delver's anklet (3) can be used.</li></ul>",
  },
  { text = "Head to the top floor of the Sorcerer's Tower north-east of Ardougne to collect a money pouch." },
  { text = "Teleport to Anachronia lodestone to collect the foot slightly south-east of the lodestone." },
  {
    text = "Head north of the Menaphos gates to pass through the world window and continue slightly north to collect the frogburger.<ul><li>If you did not finish the dialog with Benedict, talk to him again to complete the miniquest.</li></ul>",
  },
  { text = "Miniquest complete!" },
}

return Quest:new({
  name = "Benedict's World Tour (miniquest)",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = false,
  length = Enums.length.short,
  releaseDate = 1460937600,
  prereqQuests = { "Stolen Hearts", "Anachronia base camp tutorial" },
})
