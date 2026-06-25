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
    text = "Outside the Carnillean Mansion south-west of East Ardougne, to talk to Xenia.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Yes, I remember you."),
      Action.ConversationHighlight:new("Thanks for fixing it!"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Ceril Carnillean.",
    actions = {
      Action.ConversationHighlight:new("So, you want me to turn Philipe into an adventurer."),
      Action.ConversationHighlight:new("How do you expect me to create a quest?"),
      Action.ConversationHighlight:new("Where can I find all that stuff?"),
      Action.ConversationHighlight:new("I'll come back later."),
    },
  },
  { text = "Search the bookcase in the room with Henryeta Carnillean for a book about the Carnillean family." },
  {
    text = "Talk to Butler Crichton.",
    actions = {
      Action.ConversationHighlight:new("Sir Ceril says you'll help me create a quest."),
      Action.ConversationHighlight:new("Yes, let's go."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Head into the basement and enter the hole on the east wall (cutscene will begin), then head back to the house.",
  },
  {
    text = "Go to the 1st floor[UK]2nd floor[US] and take the red and black dragon head from the wall (2 heads in total).",
    title = "Master bedroom",
  },
  { text = "Open and search the southern wardrobe for the armour and sword." },
  { text = "Tear the net curtain from the north-east wall." },
  { text = "Take the money pouch from the bed or Henryeta's necklace from the dresser." },
  { text = "<i>Search</i> his painting set for red paint.", title = "Philipe's room" },
  {
    text = "Talk to Sarsaparilla. , then talk again.",
    title = "Sarsaparilla's room",
    actions = {
      Action.ConversationHighlight:new("I'm creating a quest for Philipe. Will you help?"),
      Action.ConversationHighlight:new("Are you just going to stay in here?"),
      Action.ConversationHighlight:new("You were talking about your ambitions."),
      Action.ConversationHighlight:new("I'm directing a drama. Would you like a part?"),
      Action.ConversationHighlight:new("Damsel in distress"),
      Action.ConversationHighlight:new("Yes, follow me."),
    },
  },
  {
    text = "Open and search the wardrobe for her dress.",
    actions = { Action.ConversationHighlight:new("I need to borrow a costume.") },
  },
  {
    text = "Head to the cellar and talk to Claus the chef.",
    title = "The cellar",
    actions = { Action.ConversationHighlight:new("Drunken jailer") },
  },
  { text = "Search the cupboard, take everything (wooden boards, twine, shears, and Sir Ceril's wine)." },
  { text = "Search the barrel of junk to collect rusty spikes." },
  { text = "Pick up the spider from the floor." },
  {
    text = "Pick up 7 buckets of water from the floor.<ul><li>Alternatively it is faster to skip this step and build a spike pit instead.</li></ul>",
  },
  {
    text = "Right-click <i>build</i> on the hole hotspot. (The cave going into the next area)",
    title = "The cave",
    actions = { Action.ConversationHighlight:new("Cobweb") },
  },
  {
    text = "Enter the cave and talk to Sarsaparilla  and Claus.",
    actions = { Action.ConversationHighlight:new("Yes, follow me."), Action.ConversationHighlight:new("Follow me.") },
  },
  {
    text = "Continue through the cave and talk to the goblins.",
    actions = {
      Action.ConversationHighlight:new("Can you help me?"),
      Action.ConversationHighlight:new("Here, you can have this armour."),
      Action.ConversationHighlight:new("Patrolling guards"),
    },
  },
  { text = "Head to the south-easternmost part of the tunnels and enter the passage." },
  { text = "Search the spoils near the heap for bone scraps south-west of the room." },
  { text = "Take the cave mouse to have the wolves follow you out." },
  {
    text = "Go to the 1st floor[UK]2nd floor[US] and take the red and black dragon head from the wall (2 heads in total).",
  },
  { text = "Open and search the southern wardrobe for the armour and sword." },
  { text = "Tear the net curtain from the north-east wall." },
  { text = "Take the money pouch from the bed or Henryeta's necklace from the dresser." },
  { text = "Search his painting set for red paint." },
  {
    text = "Talk to Sarsaparilla. , then talk again.",
    actions = {
      Action.ConversationHighlight:new("I'm creating a quest for Philipe. Will you help?"),
      Action.ConversationHighlight:new("Are you just going to stay in here?"),
      Action.ConversationHighlight:new("You were talking about your ambitions."),
      Action.ConversationHighlight:new("I'm directing a drama. Would you like a part?"),
      Action.ConversationHighlight:new("Damsel in distress"),
      Action.ConversationHighlight:new("Yes, follow me."),
    },
  },
  {
    text = "Open and search the wardrobe for her dress.",
    actions = { Action.ConversationHighlight:new("I need to borrow a costume.") },
  },
  {
    text = "Head to the cellar and talk to Claus the chef.",
    actions = { Action.ConversationHighlight:new("Drunken jailer") },
  },
  { text = "Search the cupboard, take everything (wooden boards, twine, shears, and Sir Ceril's wine)." },
  { text = "Search the barrel of junk to collect rusty spikes." },
  { text = "Pick up the spider from the floor." },
  {
    text = "Pick up 7 buckets of water from the floor.<ul><li>Alternatively it is faster to skip this step and build a spike pit instead.</li></ul>",
  },
  {
    text = "Right-click build on the hole hotspot. (The cave going into the next area)",
    actions = { Action.ConversationHighlight:new("Cobweb") },
  },
  {
    text = "Enter the cave and talk to Sarsaparilla  and Claus.",
    actions = { Action.ConversationHighlight:new("Yes, follow me."), Action.ConversationHighlight:new("Follow me.") },
  },
  {
    text = "Continue through the cave and talk to the goblins.",
    actions = {
      Action.ConversationHighlight:new("Can you help me?"),
      Action.ConversationHighlight:new("Here, you can have this armour."),
      Action.ConversationHighlight:new("Patrolling guards"),
    },
  },
  { text = "Head to the south-easternmost part of the tunnels and enter the passage." },
  { text = "Search the spoils near the heap for bone scraps south-west of the room." },
  { text = "Take the cave mouse to have the wolves follow you out." },
  { text = "Search his painting set for red paint." },
  {
    text = "Talk to Sarsaparilla. , then talk again.",
    actions = {
      Action.ConversationHighlight:new("I'm creating a quest for Philipe. Will you help?"),
      Action.ConversationHighlight:new("Are you just going to stay in here?"),
      Action.ConversationHighlight:new("You were talking about your ambitions."),
      Action.ConversationHighlight:new("I'm directing a drama. Would you like a part?"),
      Action.ConversationHighlight:new("Damsel in distress"),
      Action.ConversationHighlight:new("Yes, follow me."),
    },
  },
  {
    text = "Open and search the wardrobe for her dress.",
    actions = { Action.ConversationHighlight:new("I need to borrow a costume.") },
  },
  {
    text = "Head to the cellar and talk to Claus the chef.",
    actions = { Action.ConversationHighlight:new("Drunken jailer") },
  },
  { text = "Search the cupboard, take everything (wooden boards, twine, shears, and Sir Ceril's wine)." },
  { text = "Search the barrel of junk to collect rusty spikes." },
  { text = "Pick up the spider from the floor." },
  {
    text = "Pick up 7 buckets of water from the floor.<ul><li>Alternatively it is faster to skip this step and build a spike pit instead.</li></ul>",
  },
  {
    text = "Right-click build on the hole hotspot. (The cave going into the next area)",
    actions = { Action.ConversationHighlight:new("Cobweb") },
  },
  {
    text = "Enter the cave and talk to Sarsaparilla  and Claus.",
    actions = { Action.ConversationHighlight:new("Yes, follow me."), Action.ConversationHighlight:new("Follow me.") },
  },
  {
    text = "Continue through the cave and talk to the goblins.",
    actions = {
      Action.ConversationHighlight:new("Can you help me?"),
      Action.ConversationHighlight:new("Here, you can have this armour."),
      Action.ConversationHighlight:new("Patrolling guards"),
    },
  },
  { text = "Head to the south-easternmost part of the tunnels and enter the passage." },
  { text = "Search the spoils near the heap for bone scraps south-west of the room." },
  { text = "Take the cave mouse to have the wolves follow you out." },
  {
    text = "Talk to Sarsaparilla. , then talk again.",
    actions = {
      Action.ConversationHighlight:new("I'm creating a quest for Philipe. Will you help?"),
      Action.ConversationHighlight:new("Are you just going to stay in here?"),
      Action.ConversationHighlight:new("You were talking about your ambitions."),
      Action.ConversationHighlight:new("I'm directing a drama. Would you like a part?"),
      Action.ConversationHighlight:new("Damsel in distress"),
      Action.ConversationHighlight:new("Yes, follow me."),
    },
  },
  {
    text = "Open and search the wardrobe for her dress.",
    actions = { Action.ConversationHighlight:new("I need to borrow a costume.") },
  },
  {
    text = "Head to the cellar and talk to Claus the chef.",
    actions = { Action.ConversationHighlight:new("Drunken jailer") },
  },
  { text = "Search the cupboard, take everything (wooden boards, twine, shears, and Sir Ceril's wine)." },
  { text = "Search the barrel of junk to collect rusty spikes." },
  { text = "Pick up the spider from the floor." },
  {
    text = "Pick up 7 buckets of water from the floor.<ul><li>Alternatively it is faster to skip this step and build a spike pit instead.</li></ul>",
  },
  {
    text = "Right-click build on the hole hotspot. (The cave going into the next area)",
    actions = { Action.ConversationHighlight:new("Cobweb") },
  },
  {
    text = "Enter the cave and talk to Sarsaparilla  and Claus.",
    actions = { Action.ConversationHighlight:new("Yes, follow me."), Action.ConversationHighlight:new("Follow me.") },
  },
  {
    text = "Continue through the cave and talk to the goblins.",
    actions = {
      Action.ConversationHighlight:new("Can you help me?"),
      Action.ConversationHighlight:new("Here, you can have this armour."),
      Action.ConversationHighlight:new("Patrolling guards"),
    },
  },
  { text = "Head to the south-easternmost part of the tunnels and enter the passage." },
  { text = "Search the spoils near the heap for bone scraps south-west of the room." },
  { text = "Take the cave mouse to have the wolves follow you out." },
  {
    text = "Head to the cellar and talk to Claus the chef.",
    actions = { Action.ConversationHighlight:new("Drunken jailer") },
  },
  { text = "Search the cupboard, take everything (wooden boards, twine, shears, and Sir Ceril's wine)." },
  { text = "Search the barrel of junk to collect rusty spikes." },
  { text = "Pick up the spider from the floor." },
  {
    text = "Pick up 7 buckets of water from the floor.<ul><li>Alternatively it is faster to skip this step and build a spike pit instead.</li></ul>",
  },
  {
    text = "Right-click build on the hole hotspot. (The cave going into the next area)",
    actions = { Action.ConversationHighlight:new("Cobweb") },
  },
  {
    text = "Enter the cave and talk to Sarsaparilla  and Claus.",
    actions = { Action.ConversationHighlight:new("Yes, follow me."), Action.ConversationHighlight:new("Follow me.") },
  },
  {
    text = "Continue through the cave and talk to the goblins.",
    actions = {
      Action.ConversationHighlight:new("Can you help me?"),
      Action.ConversationHighlight:new("Here, you can have this armour."),
      Action.ConversationHighlight:new("Patrolling guards"),
    },
  },
  { text = "Head to the south-easternmost part of the tunnels and enter the passage." },
  { text = "Search the spoils near the heap for bone scraps south-west of the room." },
  { text = "Take the cave mouse to have the wolves follow you out." },
  {
    text = "Right-click build on the hole hotspot. (The cave going into the next area)",
    actions = { Action.ConversationHighlight:new("Cobweb") },
  },
  {
    text = "Enter the cave and talk to Sarsaparilla  and Claus.",
    actions = { Action.ConversationHighlight:new("Yes, follow me."), Action.ConversationHighlight:new("Follow me.") },
  },
  {
    text = "Continue through the cave and talk to the goblins.",
    actions = {
      Action.ConversationHighlight:new("Can you help me?"),
      Action.ConversationHighlight:new("Here, you can have this armour."),
      Action.ConversationHighlight:new("Patrolling guards"),
    },
  },
  { text = "Head to the south-easternmost part of the tunnels and enter the passage." },
  { text = "Search the spoils near the heap for bone scraps south-west of the room." },
  { text = "Take the cave mouse to have the wolves follow you out." },
  { text = "Head north to the centre bridge.", title = "Building the quest" },
  {
    text = "'Build' the 'jailer' hotspot by leading Claus to it.<ul><li>If the money pouch was taken, use it on Claus.</li></ul>",
  },
  { text = "Cross the bridge and build the dragon hotspots. The wolves will move into position." },
  { text = "Use the dragon heads on the wolves." },
  { text = "Build the Damsel hotspot.<ul><li>If the necklace was taken, use it on Sarsaparilla.</li></ul>" },
  { text = "Head back south across the bridge." },
  {
    text = "Build hotspots in these locations as you backtrack up towards the cellar:<ul><li>Decoration - on the wall east of the fire</li><li>Traps - east and west of the fire</li><li>Decoration - west of the fire at the end of the hall</li><li>Decoration - on the north wall before turning around towards the cave entrance</li><li>Obstacle - on the floor in the southernmost corridor, heading towards the cave entrance</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Bloodstains"),
      Action.ConversationHighlight:new("Tripwire"),
      Action.ConversationHighlight:new("Scary eyes"),
      Action.ConversationHighlight:new("Impaled skulls"),
      Action.ConversationHighlight:new("Spike pit"),
    },
  },
  {
    text = "Head back into the house and talk to Philipe.",
    title = "Philipe",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Pickpocket him. Talk to him again.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("A bag of sweets.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Okay, follow me.") },
  },
  {
    text = "Proceed through the cave while Philipe follows.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Watch the cutscene where Philipe attempts to pass the guards." },
  {
    text = "Talk to Slimepits (one of the goblins) until a dialogue window with the Butler appears - do not continue dialogue until Philipe passes.",
  },
  { text = "Talk to Philipe again to have him follow." },
  { text = "Continue towards Claus.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to Philipe again to have him follow." },
  {
    text = "Continue towards the dragons and talk to Philipe.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "After the dragons are slain, a cave wolf matriarch will arrive." },
  {
    text = "Attempt to attack the matriarch to focus its attacks on you. Dodge or withstand the attacks until Philipe kills it. If you run in circles around the matriarch, most of her attacks will miss and you will take very little damage.<ul><li>Do not use Protect from Magic or the matriarch will revert her attention back to Philipe.</li></ul>",
  },
  {
    text = "Talk to Philipe for a cutscene.",
    title = "Finishing up",
    actions = { Action.ConversationHighlight:new("Yes, let's go now!") },
  },
  { text = "Talk to Ceril." },
  {
    text = "Talk to Xenia.",
    actions = { Action.ConversationHighlight:new("So what happens now?") },
    postconditions = { Condition.ConversationText:new("Xenia teleports away.") },
  },
  { text = "Talk to Ceril." },
  { text = "Quest complete!" },
  { text = "Loot the chest near Ceril." },
}

return Quest:new({
  name = "Carnillean Rising",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1341273600,
  prereqQuests = { "The Blood Pact", "Hazeel Cult" },
})
