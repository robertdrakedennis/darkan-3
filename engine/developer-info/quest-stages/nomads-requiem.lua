local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Model, Vertex = Types.Model, Types.Vertex

---@type QuestStep[]
local steps = {
  { text = "Teleport to Edgeville and run south-east into the Soul Wars portal.", title = "Getting started" },
  { text = "Talk to Zimberfizz the imp in the lobby.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Investigate Nomad's tent and watch the cutscene." },
  {
    text = "Talk to Nomad.",
    actions = { Action.ConversationHighlight:new("Zimberfizz sent me here to see where you went.") },
    postconditions = { Condition.ConversationText:new("(Continues above.)") },
  },
  { text = "Go up the ladder." },
  {
    text = "Talk to Zimberfizz.",
    actions = { Action.ConversationHighlight:new("Talk about Nomad's Requiem.") },
    postconditions = {
      Condition.ConversationText:new(
        " Really? Oh, I hope he ain't mad with me. You didn't make him too angry did you?"
      ),
    },
  },
  { text = "Investigate the tent." },
  { text = "Enter the south-western doorway." },
  { text = "Climb up the south-eastern ladder and pull the north lever." },
  { text = "Climb up the north-eastern ladder and move both pillars over the drains." },
  {
    text = "Climb down the ladder and make sure that water is flowing through the southern channel. If it is not, operate the levers until it is. The symbol at the top of a pillar represents the direction of water flow through the pillar.",
  },
  {
    text = "Climb down the south-eastern ladder and complete the puzzle. A solution is pictured to the right and another solution is right below. Click on the statues in the following order:<ul><li>Centre, west statue</li><li>North, west statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li><li>South, centre statue</li><li>Centre, centre statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li><li>South, centre statue</li><li>South, west statue</li><li>Centre, west statue</li><li>North, west statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li></ul>",
  },
  { text = "Climb up the ladder." },
  { text = "Mine the rubble at the tunnel at the end of the eastern channel" },
  { text = "Pull the southern lever until water flows to the new tunnel." },
  { text = "Climb down the ladder to the bottom floor." },
  { text = "Enter the northern doorway," },
  { text = "Enter the south-eastern doorway." },
  {
    text = "There should be a waterfall in this room. If water is only dripping, the levers were not operated properly.",
  },
  {
    text = "Take some Elemental fuel out of the Fuel hopper located at the north-eastern or south-eastern corner of the room (take 6 if following the solution pictured to the right).",
  },
  {
    text = "Lure an Elemental creature out of the strange device and into the waterfall by placing the fuel on the floor.",
  },
  { text = "Do the above step a total of three times." },
  { text = "Exit the room through the northern doorway." },
  { text = "Enter the arcane doorway and then enter the western doorway." },
  { text = "Attempt to destroy a root surrounding the strange device." },
  { text = "Follow the vines on the floor and destroy four lone roots to which the vines lead." },
  { text = "Go back to the strange device." },
  {
    text = "Kill the Decaying avatar. Destroy the roots when they appear at the corners of the room. Strategies can be found here.",
  },
  { text = "Exit the room through the eastern doorway." },
  { text = "Talk to the knight near the eastern doorway." },
  {
    text = "Before entering the next room, consider turning on Piety to avoid constant damage and stat reduction or bringing a super restore potion to keep your magic level high enough to cast a fire spell. The Corruption beasts inside restore prayer upon death.",
  },
  { text = "Enter the eastern doorway." },
  { text = "Pick up five stone slabs (Take-slab from the rubble piles all around the room)." },
  { text = "Place the stone slabs to complete and cross the bridge across the central body of water." },
  {
    text = "Ignite the barrels with a magic weapon equipped and a fire spell active (75 Magic required to ignite the barrels).",
  },
  { text = "Cross the bridge and exit the room through the western doorway." },
  { text = "Enter the south-western doorway." },
  { text = "Climb up the south-eastern ladder and pull the north lever." },
  { text = "Climb up the north-eastern ladder and move both pillars over the drains." },
  {
    text = "Climb down the ladder and make sure that water is flowing through the southern channel. If it is not, operate the levers until it is. The symbol at the top of a pillar represents the direction of water flow through the pillar.",
  },
  {
    text = "Climb down the south-eastern ladder and complete the puzzle. A solution is pictured to the right and another solution is right below. Click on the statues in the following order:<ul><li>Centre, west statue</li><li>North, west statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li><li>South, centre statue</li><li>Centre, centre statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li><li>South, centre statue</li><li>South, west statue</li><li>Centre, west statue</li><li>North, west statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li></ul>",
  },
  { text = "Climb up the ladder." },
  { text = "Mine the rubble at the tunnel at the end of the eastern channel" },
  { text = "Pull the southern lever until water flows to the new tunnel." },
  { text = "Climb down the ladder to the bottom floor." },
  { text = "Enter the northern doorway," },
  { text = "Enter the south-eastern doorway." },
  {
    text = "There should be a waterfall in this room. If water is only dripping, the levers were not operated properly.",
  },
  {
    text = "Take some Elemental fuel out of the Fuel hopper located at the north-eastern or south-eastern corner of the room (take 6 if following the solution pictured to the right).",
  },
  {
    text = "Lure an Elemental creature out of the strange device and into the waterfall by placing the fuel on the floor.",
  },
  { text = "Do the above step a total of three times." },
  { text = "Exit the room through the northern doorway." },
  { text = "Enter the arcane doorway and then enter the western doorway." },
  { text = "Attempt to destroy a root surrounding the strange device." },
  { text = "Follow the vines on the floor and destroy four lone roots to which the vines lead." },
  { text = "Go back to the strange device." },
  {
    text = "Kill the Decaying avatar. Destroy the roots when they appear at the corners of the room. Strategies can be found here.",
  },
  { text = "Exit the room through the eastern doorway." },
  { text = "Talk to the knight near the eastern doorway." },
  {
    text = "Before entering the next room, consider turning on Piety to avoid constant damage and stat reduction or bringing a super restore potion to keep your magic level high enough to cast a fire spell. The Corruption beasts inside restore prayer upon death.",
  },
  { text = "Enter the eastern doorway." },
  { text = "Pick up five stone slabs (Take-slab from the rubble piles all around the room)." },
  { text = "Place the stone slabs to complete and cross the bridge across the central body of water." },
  {
    text = "Ignite the barrels with a magic weapon equipped and a fire spell active (75 Magic required to ignite the barrels).",
  },
  { text = "Cross the bridge and exit the room through the western doorway." },
  { text = "Enter the south-western doorway." },
  { text = "Climb up the south-eastern ladder and pull the north lever." },
  { text = "Climb up the north-eastern ladder and move both pillars over the drains." },
  {
    text = "Climb down the ladder and make sure that water is flowing through the southern channel. If it is not, operate the levers until it is. The symbol at the top of a pillar represents the direction of water flow through the pillar.",
  },
  {
    text = "Climb down the south-eastern ladder and complete the puzzle. A solution is pictured to the right and another solution is right below. Click on the statues in the following order:<ul><li>Centre, west statue</li><li>North, west statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li><li>South, centre statue</li><li>Centre, centre statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li><li>South, centre statue</li><li>South, west statue</li><li>Centre, west statue</li><li>North, west statue</li><li>North, centre statue</li><li>North, east statue</li><li>Centre, east statue</li><li>South, east statue</li></ul>",
  },
  { text = "Climb up the ladder." },
  { text = "Mine the rubble at the tunnel at the end of the eastern channel" },
  { text = "Pull the southern lever until water flows to the new tunnel." },
  { text = "Climb down the ladder to the bottom floor." },
  { text = "Enter the northern doorway," },
  { text = "Enter the south-eastern doorway." },
  {
    text = "There should be a waterfall in this room. If water is only dripping, the levers were not operated properly.",
  },
  {
    text = "Take some Elemental fuel out of the Fuel hopper located at the north-eastern or south-eastern corner of the room (take 6 if following the solution pictured to the right).",
  },
  {
    text = "Lure an Elemental creature out of the strange device and into the waterfall by placing the fuel on the floor.",
  },
  { text = "Do the above step a total of three times." },
  { text = "Exit the room through the northern doorway." },
  { text = "Enter the arcane doorway and then enter the western doorway." },
  { text = "Attempt to destroy a root surrounding the strange device." },
  { text = "Follow the vines on the floor and destroy four lone roots to which the vines lead." },
  { text = "Go back to the strange device." },
  {
    text = "Kill the Decaying avatar. Destroy the roots when they appear at the corners of the room. Strategies can be found here.",
  },
  { text = "Exit the room through the eastern doorway." },
  { text = "Talk to the knight near the eastern doorway." },
  {
    text = "Before entering the next room, consider turning on Piety to avoid constant damage and stat reduction or bringing a super restore potion to keep your magic level high enough to cast a fire spell. The Corruption beasts inside restore prayer upon death.",
  },
  { text = "Enter the eastern doorway." },
  { text = "Pick up five stone slabs (Take-slab from the rubble piles all around the room)." },
  { text = "Place the stone slabs to complete and cross the bridge across the central body of water." },
  {
    text = "Ignite the barrels with a magic weapon equipped and a fire spell active (75 Magic required to ignite the barrels).",
  },
  { text = "Cross the bridge and exit the room through the western doorway." },
  { text = "Climb up the ladder." },
  { text = "Mine the rubble at the tunnel at the end of the eastern channel" },
  { text = "Pull the southern lever until water flows to the new tunnel." },
  { text = "Climb down the ladder to the bottom floor." },
  { text = "Enter the northern doorway," },
  { text = "Enter the south-eastern doorway." },
  {
    text = "There should be a waterfall in this room. If water is only dripping, the levers were not operated properly.",
  },
  {
    text = "Take some Elemental fuel out of the Fuel hopper located at the north-eastern or south-eastern corner of the room (take 6 if following the solution pictured to the right).",
  },
  {
    text = "Lure an Elemental creature out of the strange device and into the waterfall by placing the fuel on the floor.",
  },
  { text = "Do the above step a total of three times." },
  { text = "Exit the room through the northern doorway." },
  { text = "Enter the arcane doorway and then enter the western doorway." },
  { text = "Attempt to destroy a root surrounding the strange device." },
  { text = "Follow the vines on the floor and destroy four lone roots to which the vines lead." },
  { text = "Go back to the strange device." },
  {
    text = "Kill the Decaying avatar. Destroy the roots when they appear at the corners of the room. Strategies can be found here.",
  },
  { text = "Exit the room through the eastern doorway." },
  { text = "Talk to the knight near the eastern doorway." },
  {
    text = "Before entering the next room, consider turning on Piety to avoid constant damage and stat reduction or bringing a super restore potion to keep your magic level high enough to cast a fire spell. The Corruption beasts inside restore prayer upon death.",
  },
  { text = "Enter the eastern doorway." },
  { text = "Pick up five stone slabs (Take-slab from the rubble piles all around the room)." },
  { text = "Place the stone slabs to complete and cross the bridge across the central body of water." },
  {
    text = "Ignite the barrels with a magic weapon equipped and a fire spell active (75 Magic required to ignite the barrels).",
  },
  { text = "Cross the bridge and exit the room through the western doorway." },
  { text = "Enter the arcane doorway and then enter the western doorway." },
  { text = "Attempt to destroy a root surrounding the strange device." },
  { text = "Follow the vines on the floor and destroy four lone roots to which the vines lead." },
  { text = "Go back to the strange device." },
  {
    text = "Kill the Decaying avatar. Destroy the roots when they appear at the corners of the room. Strategies can be found here.",
  },
  { text = "Exit the room through the eastern doorway." },
  { text = "Talk to the knight near the eastern doorway." },
  {
    text = "Before entering the next room, consider turning on Piety to avoid constant damage and stat reduction or bringing a super restore potion to keep your magic level high enough to cast a fire spell. The Corruption beasts inside restore prayer upon death.",
  },
  { text = "Enter the eastern doorway." },
  { text = "Pick up five stone slabs (Take-slab from the rubble piles all around the room)." },
  { text = "Place the stone slabs to complete and cross the bridge across the central body of water." },
  {
    text = "Ignite the barrels with a magic weapon equipped and a fire spell active (75 Magic required to ignite the barrels).",
  },
  { text = "Cross the bridge and exit the room through the western doorway." },
  { text = "Enter the arcane doorway and then enter the western doorway." },
  { text = "Attempt to destroy a root surrounding the strange device." },
  { text = "Follow the vines on the floor and destroy four lone roots to which the vines lead." },
  { text = "Go back to the strange device." },
  {
    text = "Kill the Decaying avatar. Destroy the roots when they appear at the corners of the room. Strategies can be found here.",
  },
  { text = "Exit the room through the eastern doorway." },
  { text = "Talk to the knight near the eastern doorway." },
  {
    text = "Before entering the next room, consider turning on Piety to avoid constant damage and stat reduction or bringing a super restore potion to keep your magic level high enough to cast a fire spell. The Corruption beasts inside restore prayer upon death.",
  },
  { text = "Enter the eastern doorway." },
  { text = "Pick up five stone slabs (Take-slab from the rubble piles all around the room)." },
  { text = "Place the stone slabs to complete and cross the bridge across the central body of water." },
  {
    text = "Ignite the barrels with a magic weapon equipped and a fire spell active (75 Magic required to ignite the barrels).",
  },
  { text = "Cross the bridge and exit the room through the western doorway." },
  {
    text = "Talk to the knight near the eastern doorway.",
    title = "Smoke",
    neededItems = { ["Fire rune"] = { quantity = 1 }, ["Air rune"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Before entering the next room, consider turning on Piety to avoid constant damage and stat reduction or bringing a super restore potion to keep your magic level high enough to cast a fire spell. The Corruption beasts inside restore prayer upon death.",
  },
  { text = "Enter the eastern doorway." },
  { text = "Pick up five stone slabs (Take-slab from the rubble piles all around the room)." },
  { text = "Place the stone slabs to complete and cross the bridge across the central body of water." },
  {
    text = "Ignite the barrels with a magic weapon equipped and a fire spell active (75 Magic required to ignite the barrels).",
  },
  { text = "Cross the bridge and exit the room through the western doorway." },
  { text = "Make any necessary preparations to fight Nomad. You can teleport out, and bank to prepare for the fight." },
  {
    text = "To return to the area, teleport to Edgeville, enter into the Soul Wars portal, and investigate the tent again.",
  },
  {
    text = "Enter the arcane doorway at the northern wall then the arcane doorway on the inner southern wall.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Kill Nomad. (Deathtouched darts work)" },
  {
    text = "Talk to Zimberfizz.",
    actions = {
      Action.ConversationHighlight:new("Talk about Nomad's Requiem."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Nomad's Requiem",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1263168000,
  prereqQuests = { "King's Ransom", "Knight Waves training ground", "Soul Wars" },
})
