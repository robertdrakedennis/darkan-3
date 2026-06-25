local Quest = require("core.quest")
local QuestStep = require("core.queststep")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")

---@type QuestStep[]
local steps = {
  {
    text = "Speak to Ozan in the northern part of Draynor Village just south of the potter near the water pump.",
    title = "The Skulls",
    neededItems = {},
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "In the house to the west, open the trapdoor.",
    actions = { Action.ConversationHighlight:new("Let me in or I'll poke your eyes out!") },
    postconditions = { Condition.ConversationText:new(" Yeah, you seem alright. Come on in.") },
  },
  {
    text = "Talk to Khnum.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Tell me what jobs you have in the works."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " It's relatively risk-free too; we've got some lady backer doing all the heavy lifting. But you don't need to know any more than that..."
      ),
    },
  },
  { text = "If you have any followers, dismiss them now." },
  {
    text = "Talk to Ozan.",
    actions = {
      Action.ConversationHighlight:new("How do we find the HQ?"),
      Action.ConversationHighlight:new("Yes."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Also, I'm not exactly welcome in Al Kharid these days. I could do with a friend to vouch for me."
      ),
    },
  },
  {
    text = "Follow Khnum. Ozan will warn of getting too close or too far.",
    title = "The Headquarters",
    neededItems = {},
    recommendedItems = {},
  },
  { text = "Open the door Khnum went through." },
  {
    text = "Talk to Khnum.",
    actions = {
      Action.ConversationHighlight:new("I'll lock you up and throw away the key."),
      Action.ConversationHighlight:new("We'll find them easily enough ourselves."),
      Action.ConversationHighlight:new("What will you take pride in while locked up?"),
    },
  },
  {
    text = "Head south of the headquarters towards the sea.",
    title = "Lady Keli",
    neededItems = {},
    recommendedItems = {},
  },
  { text = "Talk to a skulls mercenary or Mercenary Joe. Mercenaries will begin to attack you." },
  { text = "Kill the mercenaries and pick up the ransom note." },
  { text = "Talk to Leela and remember the passphrase she gives you as this is needed later." },
  {
    text = "Agree to travel with Ozan to Al Kharid.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        " Also, I'm not exactly welcome in Al Kharid these days. I could do with a friend to vouch for me."
      ),
    },
  },
  {
    text = "Home Teleport to Al Kharid lodestone<ul><li>If you have the lodestone unlocked, you must Home Teleport so Ozan follows you. Each section will not accept inputs until Ozan finishes his animation.</li></ul>",
    title = "Al Kharid",
    neededItems = {},
    recommendedItems = {},
  },
  { text = "Head south into the Al Kharid Palace and talk to the palace guard." },
  {
    text = "Head northeast of Al Kharid to the Dommik's Crafting Store, and climb the steps inside.<ul><li>If there is no ladder you should need to talk to the guard in the palace again.</li></ul>",
  },
  { text = "Climb the ladder." },
  { text = "Walk across the plank to the south." },
  { text = "Slide down the awning to the south." },
  { text = "Head inside the room to the north, and then walk across the washing line to west." },
  { text = "Climb the ladder to the west." },
  { text = "Cross the planks to the west." },
  { text = "Head west and climb the ladder." },
  { text = "Jump from the scaffold to the west." },
  { text = "Climb down the rug to the south." },
  { text = "Swing across the wooden frame to the west." },
  { text = "Jump from the scaffold to the south." },
  { text = "Bounce on the awning south." },
  { text = "Climb up the brickwork." },
  { text = "'Leave it to Ozan' on the flagpole, and then shimmy across the rope." },
  { text = "'Climb up brickwork' to activate a cutscene." },
  { text = "After the cutscene, climb up the brickwork." },
  {
    text = "Head south and 'Break in' through the skylight. Don't log out once inside, or you will respawn outside the palace.",
  },
  { text = "Inspect the Het scales.", title = "Kharid-ib", neededItems = {}, recommendedItems = {} },
  {
    text = "You have exactly two weighings to determine which is the heaviest weight.<ul><li>The heaviest weight will always be random.</li><li>Drag two sets of three weights onto both sides of the scale and click the central green gem to weigh.</li><li>If one of the sides is heavier, you know the heaviest weight is part of the 3 weights that went down. Put one of those weights on the left side and one weight on the right side of the scale, the last one in the bottom row and click the green gem again. If either side went down you know the heaviest weight. If they are equal you know it's the one you put back in the bottom row.</li><li>If both sides were of equal weight, you know the remaining 3 weights in the bottom row are the heaviest. Put one of those weights on the left side and one weight on the right side of the scale, the last one in the bottom row and click the green gem again. If either side went down you know the heaviest weight. If they are equal you know it's the one you put back in the bottom row.</li><li>Once you've determined the heaviest weight drag it onto the backpack icon to grab it.</li></ul>",
  },
  { text = "'Place weight' on the Kharid-ib display." },
  {
    text = "Tell Osman the passphrase that was obtained from Leela. If you don't remember it, try all options as if you get it wrong enough times Ozan will say the passphrase anyway.",
  },
  {
    text = "During the cutscene, choose either option.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Stolen Hearts",
  steps = steps,
  timeline = Enums.timeline.pathfinder,
  members = false,
  length = Enums.length.mediumlong,
  releaseDate = 1348617600,
  prereqQuests = {},
})
