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
    text = "Talk to Phoenix south of Eagles' Peak.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Travelling firemaker? What does that involve?"),
      Action.ConversationHighlight:new("This seems dangerous. Why put your lives at risk?"),
      Action.ConversationHighlight:new("What are you waiting for?"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes, let's go.") },
  },
  { text = "Enter the tunnel." },
  {
    text = "Talk to Flint for a pitch can.",
    title = "The cave system - Room 1",
    actions = { Action.ConversationHighlight:new("That's all, thanks.") },
  },
  {
    text = "Use the pitch can to light fires by right-clicking make-fire-here to complete the arrow on the ground. If you mess up, you can right-click the fire to stomp it out. You can also drag the pitch can onto an action bar, and use a key-bind to light fires with it. However, this will light a fire one tile in front of you.",
  },
  { text = "Light the fire pit.", title = "Room 2" },
  { text = "Take the Firemaking journal on the rock.", actions = { Action.ConversationHighlight:new("What plan?") } },
  { text = "Light the fire again.", actions = { Action.ConversationHighlight:new("What do you want from us?") } },
  {
    text = "Enter the tunnel on the east side of the room.",
    actions = { Action.ConversationHighlight:new("We must continue through the caves.") },
  },
  { text = "Complete the pattern which is in the shape of a falling boulder.", title = "Room 3" },
  {
    text = "Avoid falling boulders. Pick-up rocks one at a time, placing them on the rock pile to the east. (Repeat this 6 times)",
  },
  { text = "Climb up the rock pile." },
  { text = "Light the fire pit.", title = "Room 4" },
  {
    text = "Take the journal.",
    actions = {
      Action.ConversationHighlight:new("Did you say Zaros?"),
      Action.ConversationHighlight:new("Were you close to Zaros?"),
      Action.ConversationHighlight:new("What happened to you?"),
    },
  },
  {
    text = "Tie-up firemaker on the Column, selecting Twig.",
    actions = { Action.ConversationHighlight:new("Twig"), Action.ConversationHighlight:new("Yes!") },
  },
  { text = "Light the fire pit again." },
  { text = "Enter the tunnel to the east." },
  { text = "Using the bowls of red and yellow powder by the entrance, complete the puzzle.", title = "Room 5" },
  { text = "Immediately after the puzzle, dodge 2 fire walls by standing in the open gaps." },
  { text = "Proceed to the next room north." },
  {
    text = "The lights dim after a short time, dealing 50-100 damage every few seconds. Lighting the fires postpones this effect.<ul><li>If you come close to dying in this room, you will be rescued by the firemakers and restored to full health. If this happens, any activated pillars will remain activated, but all lit fires will be extinguished. You don't have to relight all the fires.</li></ul>",
    title = "Room 6",
  },
  {
    text = "Complete the puzzle:<ul><li>Go west, light the fire pit. Push nearby column switch.</li><li>Go far east, light the fire pit. Push nearby column switch.</li><li>Go back west and north to the centre and jump the pillar east of you. Push the nearby column switch.</li><li>Jump the ledge just south-east of you, jump the next ledge east. Light the fire pit.</li><li>North of you is the flame switch - loop around by going back west then east and push it.</li><li>Head west, light the fire pit then push the switch. Move fast here to avoid being hit by the rocks above you.</li><li>Jump back to the centre and push the switch immediately south of you.</li><li>Head to the north-west corner and light the fire pit, press the switch just south. Watch out for the falling rocks.</li></ul>",
  },
  { text = "Enter the tunnel in the north-east corner." },
  {
    text = "Light the fire and pick up the journal.",
    title = "Room 7",
    actions = {
      Action.ConversationHighlight:new("Zamorak!"),
      Action.ConversationHighlight:new("Yes, he's a terrible, hateful god."),
    },
  },
  {
    text = "Tie-up firemaker on the Column, selecting Sera.",
    actions = {
      Action.ConversationHighlight:new("More options"),
      Action.ConversationHighlight:new("Sera"),
      Action.ConversationHighlight:new("Yes!"),
    },
  },
  { text = "Light the fire again and proceed to the next room east." },
  { text = "Complete the puzzle.", title = "Room 8" },
  {
    text = "Immediately after the puzzle, dodge 4 quicker fire walls by standing in the open gaps.<ul><li>There might be a bug where 2 firewalls spawn at the same time, leaving no open gaps. A solution might be to stand in the far south-east corner of the room or run around the fire walls.</li></ul>",
  },
  { text = "Proceed to the next room north." },
  { text = "Light one of the oil pools on the wall.", title = "Room 9" },
  {
    text = "Avoiding being damaged by the fire balls. You have to hold on to all the waves for about 10 minutes.<ul><li>You can light the oil pool lines as a barricade.</li><li>If you stay in the north-west corner and immediately light the oil pools as soon as they're ready you should stay safe. There is no guarantee however.</li><li>If the fire balls start congregating on one side, run to opposite corner, placing a barricade behind you.</li><li>Another tactic is to use the option on the oil pools and use 'right' and 'left' to perform a clockwise rotation of the edges of the pit.</li></ul>",
  },
  { text = "Proceed to the next room north." },
  { text = "Light the fire and grab the journal.", title = "Room 10" },
  {
    text = "Tie up Twig again  and light the fire.",
    actions = { Action.ConversationHighlight:new("Twig"), Action.ConversationHighlight:new("Yes!") },
  },
  { text = "Proceed to the next room east." },
  { text = "Complete the puzzle.", title = "Room 11" },
  { text = "Light the fire pit." },
  { text = "Take-torch from the fire pit and continue down the path." },
  {
    text = "When there are dark tendrils, ward them off at the edge of your screen by clicking on them.<ul><li>Tendrils may hide behind your interface if you have panels on the edges of your screen. To make the tendrils more visible, right click on World Map and click 'Toggle High Contrast Mode'.</li></ul>",
  },
  { text = "Proceed to the next room at the end of the path." },
  { text = "Light the fire and take the journal.", title = "Room 12" },
  {
    text = "Tie-up Emmett  and light the fire.",
    actions = { Action.ConversationHighlight:new("Emmett"), Action.ConversationHighlight:new("Yes!") },
  },
  { text = "Proceed to the next room east." },
  { text = "During this fight, you must fight Char with Necromancy, Magic or Ranged.", title = "Face-off with Char" },
  {
    text = "Teleport out and prepare for the battle.<ul><li>Carry an inventory full of food (such as sharks or rocktails).</li><li>Carry necromancy, range or a mage weapon.</li><li>Do not bring any armour as it has no effect during this battle.</li><li>Do not bring a familiar as this might cause the dialogue with Char to be glitched.</li></ul>",
  },
  { text = "Teleport back to Eagles' Peak and run south to the entrance of the cave." },
  { text = "Talk to Flint to get another Pitch can." },
  { text = "Climb down the steps to enter Char's lair." },
  {
    text = "Take the journal.",
    actions = {
      Action.ConversationHighlight:new("That's a good idea."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Talk to Char to engage the fight." },
  { text = "During the fight, you should keep a distance from Char at all times.", title = "Strategy to defeat Char" },
  {
    text = "The amount of damage you deal is determined by the amount of flames you have lit (100 damage per active flame).",
  },
  { text = "As you fight her, keep lighting flames." },
  { text = "Add your pitch can to your ability bar to make fires much more easily." },
  { text = "Run around the edges of the arena lighting flames as fast as you can." },
  {
    text = "When you have 10 active flames, run some distance from Char and attack her a few times, dealing 1,000-5,000 damage in total.",
  },
  { text = "Repeat steps 1-2 until she is defeated." },
  {
    text = "Remember to avoid the firewalls by maneuvering through the gaps, or you'll be hit for 1,000-4,000 damage.",
  },
  { text = "While Char is glowing she is invincible, so focus on lighting flames during this period." },
  {
    text = "After the battle ends, talk to Char.",
    title = "After the battle",
    actions = { Action.ConversationHighlight:new("How do we get out?") },
  },
  {
    text = "There may be a glitch where you cannot finish the dialogue, in this case leave the cave through the southern tunnel and come back in.",
  },
  { text = "Exit through the tunnel to the north.", title = "Finishing up" },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Firemaker's Curse",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1326240000,
  prereqQuests = {},
})
