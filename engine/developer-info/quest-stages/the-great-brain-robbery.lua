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
    text = "Travel to Mos Le'Harmless (fairy ring DIP if you have repaired the bridge), walk a bit north off the dock and talk to Brother Tranquillity (if you arrived via the fairy ring, run into town and he is north of the docks).",
    title = "Disharmony",
    neededItems = {
      ["Plank"] = { quantity = 4 },
      ["Nails"] = { quantity = 10 },
      ["Holy symbol"] = { quantity = 1 },
      ["Fishbowl helmet"] = { quantity = 1 },
      ["Diving apparatus"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Bank/dismiss any pets or familiars." },
  {
    text = "Upon arrival on Harmony Island, go through the dialogue, or Brother Tranquillity will teleport you back to Mos Le'Harmless.",
  },
  { text = "Run outside and head slightly north to the statue. Pull the statue, then climb-down statue." },
  { text = "Equip your diving gear and descend the stairs." },
  { text = "Run past the zombie pirates." },
  {
    text = "Repair the stairs at the end of the tunnel, then climb them.<ul><li>If you happened to bring your planks in a plank box, be sure to remove them so you can repair the stairs.</li></ul>",
  },
  { text = "Climb the ladder." },
  { text = "Peer-through the peephole on the wall." },
  { text = "Make your way back to Brother Tranquillity on Harmony Island and speak with him." },
  { text = "Travel to the Edgeville Monastery." },
  {
    text = "Search the southern bookcase on the ground floor[UK]1st floor[US] of the western wing to find a prayer book.",
  },
  { text = "Read the book." },
  { text = "Head back to Brother Tranquillity who teleports you to Harmony Island." },
  { text = "Wear your holy symbol and choose the option Recite-prayer on the prayer book." },
  { text = "Talk to Brother Tranquillity.", title = "Family doctor" },
  { text = "Travel to Fenkenstrain's Castle, which is directly north-east of the Canifis lodestone." },
  { text = "Go up on the staircase in the north-western corner." },
  { text = "Go up on the northern ladder behind the jail gate in the centre." },
  { text = "Talk to Dr Fenkenstrain." },
  {
    text = "Equip your ring of charos (a) and talk to Rufus in the Canifis meat store.",
    title = "Err... Meow?",
    neededItems = {
      ["Wooden cat"] = { quantity = 10 },
      ["Nails"] = { quantity = 35 },
      ["Plank"] = { quantity = 4 },
      ["Ring of charos (a)"] = { quantity = 1 },
      ["Wolf whistle"] = { quantity = 1 },
      ["Crate parts"] = { quantity = 1 },
    },
    actions = { Action.ConversationHighlight:new("Talk about the meat shipment") },
    postconditions = {
      Condition.ConversationText:new(
        "(With the Ring of Charos equipped:)(First time:) Hey, Rufus, I heard that you do deliveries of meat. Could you send me a crate of supplies to Mos Le'Harmless for me? Well, it's a bit of a way to go, but for you, I'd wrrrite up a shipping orderrr. What do you want to send? Er, some meat? Well, I didn't think you wanted to send shoes, frrriend; this is a meat shop. What I mean is, what KIND of meat do you want to send? Oh, don't worry about it, I'll be providing the meat myself. It's a treat for some werewolves down there, you see. Hmm, I hadn't hearrrd about any packs running with the pirrrates...still, I trust you. Look, I aprrreciate you'rrre wanting to catch the meat yourrrself, I mean that's the sort of wolf I am too, but therrre is one thing. I'll gladly give you a crate for the meat, but I'll have to inspect it beforrre you send it over. I don't want my name associated with anything less than 100% frrresh, you understand. Oh sure, that's entirely reasonable.(Without at least one free backpack space:) Grrreat! I have the parrrts for the crrrate forrr you when you have the space to carrrry them.(Dialogue ends.)(With at least one free backpack space:)Player receives 6 crate parts. Grrreat! Here's the parrrts of the crrrate. Sorrrry, but I seem to be out of nails. So, can I get a hint as to what you arrre sending? Rrrabbits? Ducks? Sheep? Oh, I'm sure that I'll be able to find some good meat from somewhere. Wait, you are sending it live, rrright? I mean it is going to take a while to get therrre, so I'm not going to ship it unless it's at least a little alive. What? Err, I mean, what do you mean only a little alive? Of course a good werewolf like me will be sending the food live. Ha, ha, ha! Of courrrse, of course! So, what can I expect to see when I inspect this shipment of yourrrs? Errr, you know, something...special... It's cats isn't it? Cats? Oh, don't worrrry, you sly dog, I'll not tell anyone. Tell you what, I'll get the shipping order for a crrrate of cats wrrritten up rrright now. You just provide the meat. But... No buts, frrriend, you get going. I know how trrricky those things are to grab!(Without at least one free backpack space:) You're carrying too much for me to give this to you now, but I have a whistle you can blow to call me when you have the crrrate filled. Come talk to me when you have some frrree space forrr it.(Dialogue ends.)(With at least one free backpack space:)Player receives wolf whistle (item). Here, when you need me to inspect the meat, give this a blow and we'll get those tasty kitties of yours shipped off lickety split!Screen fade out/in. Cutscene begins. The player walks to the shop's exit. Well, at least I got him to agree to ship the crate for me. I suppose in the meantime I'll set the crate up in Fenkenstrain's tower where he can get to it. All I have to do now is get a load of cats from somewhere...Elfinlocks and her cat walk by. Keep up, cat! Click, whirr! Of course! I'll make some fake cats from wood and fur! With a little help from the Ring of Charos, he'll never know the difference. It's just crazy enough to work...Screen fade out/in. Cutscene ends.You can now craft wooden cats in the workshop of a player-owned house! Note: This need not be your own house.(With crate parts in the backpack:) How goes the hunting, frrriend? It goes very well. Thanks for asking.(If the crate parts have been lost:) Rufus, can I have some crate parts?(Without at least one free backpack space:) Well surrre, as soon as you have some frrree space for them.(With at least one free backpack space:)Player receives 6 crate parts. Yes, herrre are some new ones.(With the shipping order in the backpack:) Hello there, Player. How did your pirrrate friends like theirrr 'treat'? I haven't posted it to them just yet. Well, I hope you do so beforrre those cats starrrt to get ill from being cooped up in that box.(If the shipping order has been lost:) Rufus, can I have a shipping order?Player receives shipping order. Fine, but I'm not happy about putting my name on so many orrrders for cats. Try to be more careful with this one."
      ),
    },
  },
  {
    text = "Start crafting on a clockmaker's bench in a player-owned house to make 10 wooden cats with 10 planks and 10 bear fur. You can also buy them off the Grand Exchange.",
  },
  { text = "Travel back to Dr Fenkenstrain with all of the required items." },
  { text = "Build the crate on the hotspot, east of the ladder." },
  { text = "Add-bottom to the crate." },
  { text = "Fill the crate with the wooden cats." },
  { text = "Take off your fishbowl helmet and Blow the wolf whistle to call Rufus." },
  { text = "Use the shipping order on the crate." },
  {
    text = "Travel back to Mos Le'Harmless.",
    title = "Scrubs",
    neededItems = {
      ["Fishbowl helmet"] = { quantity = 1 },
      ["Diving apparatus"] = { quantity = 1 },
      ["Hammer"] = { quantity = 1 },
      ["Tinderbox"] = { quantity = 1 },
      ["Combat gear"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Buy a hammer and a tinderbox from the Trader Crewmember (located south-west on the dock)." },
  {
    text = "Speak with Brother Tranquillity to get back on Harmony Island.  (Brother Tranquillity now also has a 'transport' option available to players)",
    actions = { Action.ConversationHighlight:new("Yes. please.") },
  },
  {
    text = "Climb down the ladder and talk to Dr Fenkenstrain.<ul><li>If he is not there, exit to lobby and join the world again.</li></ul>",
  },
  { text = "Return up the ladder." },
  {
    text = "Equip your diving suit and head north to the ship that has run aground.<ul><li>If you walk outside without diving suit, you will be teleported back.</li><li>Equip your weapons in preparation for fighting enemies, otherwise you will have to return to the windmill to do so. Attempting to do so outside will give the message: 'You cannot wear that without the gas affecting you.'</li></ul>",
  },
  { text = "Open and then search the locker on the west end of the ground floor[UK]1st floor[US] to get a fuse." },
  {
    text = "Climb the ladder and take a keg of powder found next to a cannon.<ul><li>Grab the tinderbox from the front (east side) of the ship if you have not brought your own.</li></ul>",
  },
  { text = "Go to the front door of the monastery (the main building on Harmony Island)." },
  { text = "Use the keg on the door." },
  { text = "Use the fuse on the door." },
  { text = "Use your tinderbox on the fuse." },
  {
    text = "Pass the door to enter the monastery.",
    title = "Barrelchest",
    neededItems = {
      ["Combat gear"] = { quantity = 1 },
      ["Fishbowl helmet"] = { quantity = 1 },
      ["Diving apparatus"] = { quantity = 1 },
    },
    recommendedItems = { ["Food"] = { quantity = 1 }, ["Potion"] = { quantity = 1 } },
  },
  {
    text = "Kill some sorebones until you get the following items (the items drop one at a time and the next item won't spawn until the current one is picked up):<ul><li>3 bell jars</li><li>A pair of brain tongs</li><li>A cranial clamp</li><li>30 skull staples</li></ul>",
  },
  {
    text = "Talk to Dr Fenkenstrain downstairs of south-western house. Make sure to give him a hammer or you will be stuck.",
  },
  {
    text = "After the cutscene, go upstairs and talk to Brother Tranquillity.",
    actions = { Action.ConversationHighlight:new("Not right now, thanks.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Pass the door of the monastery and confront Mi-Gor." },
  { text = "Kill Barrelchest." },
  { text = "Take the Barrelchest anchor." },
  { text = "Return to Brother Tranquillity." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Great Brain Robbery",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1173139200,
  prereqQuests = { "Creature of Fenkenstrain", "Cabin Fever", "Recipe for Disaster: Freeing Pirate Pete" },
})
