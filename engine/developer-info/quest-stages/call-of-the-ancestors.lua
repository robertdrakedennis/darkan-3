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
    text = "The Liberation of Mazcab grouping teleport may be helpful for a few parts of this quest. Especially if you lack reputation.",
    title = "Getting started",
  },
  {
    text = "Talk to Tunks at the desert goebie camp, north-west of the Menaphos gates.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Go through the world window slightly to the north west, then run north and east to Kanatah." },
  {
    text = "Talk to Acca Kanatah.",
    actions = {
      Action.ConversationHighlight:new("I've simply come to help."),
      Action.ConversationHighlight:new("Talk about Call of the Ancestors."),
      Action.ConversationHighlight:new("The airut are gathering to completely wipe out Kanatah."),
      Action.ConversationHighlight:new("Ok, I'm done talking. What do I need to do?"),
    },
  },
  {
    text = "If you have a pet, dismiss it and go east to the beach and talk to Tunks for a cutscene.",
    actions = { Action.ConversationHighlight:new("No problem.") },
  },
  { text = "Jump over the chasm and collect the bundle of logs.", title = "Gathering the bundles of logs" },
  {
    text = "Headbutt the cracked wall. Control Peck by clicking on him and squeeze through the hole. Gather the bundle of logs.",
  },
  {
    text = "Talk to Acca Kanatah.",
    actions = {
      Action.ConversationHighlight:new("Talk about Call of the Ancestors."),
      Action.ConversationHighlight:new("Great, where do I get more?"),
    },
  },
  {
    text = "Enter Nemi Forest, to the west of Mazcab. Select the correct option on the chatbox when entering the Forest.<ul><li>Talk to Mazcab guide by the city entrance to get to Nemi Forest faster, if you have the reputation.</li></ul>",
    title = "Nemi poison",
    actions = {
      Action.ConversationHighlight:new("Enter Nemi Forest for Call of the Ancestors."),
      Action.ConversationHighlight:new("Forest Entrance"),
    },
  },
  {
    text = "Talk to the Ancient goebie",
    actions = {
      Action.ConversationHighlight:new("Acca Kanatah needs to create more poison."),
      Action.ConversationHighlight:new("Friend?"),
      Action.ConversationHighlight:new("Looks like we're visiting a temple then."),
    },
  },
  {
    text = "Enter the temple to the north, then talk to Xinachto",
    actions = { Action.ConversationHighlight:new("I'm an adventurer!"), Action.ConversationHighlight:new("Goodbye.") },
  },
  { text = "Enter the first door on the western wall.", title = "First trial" },
  {
    text = "Control Lunch. Step on the second tile from the west, then the one east of it, then the easternmost one, then the westernmost one. Do not step on a pressure plate twice.",
  },
  {
    text = "Control Tunks. Jump over the chasm, then press the easternmost pressure plate, the one west of it, then the westernmost pressure plate, then the remaining one to the east of it.",
  },
  { text = "Control Lunch. Headbutt the cracked wall on the western wall. Then step on the westernmost plate." },
  {
    text = "Control Peck. Squeeze through the small gap you just opened, and the one to the north, then investigate the magic.",
  },
  { text = "To exit, control Lunch and exit the southern door." },
  { text = "Enter the door on the eastern wall.", title = "Second trial" },
  { text = "Control Tunks and jump over the chasm to the north once." },
  { text = "Control Lunch at the entrance and headbutt the cracked wall on the west." },
  { text = "Control Peck, squeeze through, and pull the lever." },
  { text = "Control Lunch and push the heavy block onto the north-eastern pressure plate." },
  { text = "Control Tunks and stand on the pressure plate on the centre platform." },
  {
    text = "Control Lunch and pull the heavy block onto the south-eastern pressure plate. Walk around to stand on the north-eastern pressure plate.",
  },
  { text = "Control Tunks, jump the chasm north and stand on one of the north-western pressure plates." },
  { text = "Control Peck and squeeze through the north-western gap." },
  { text = "Control Lunch, and walk past the three pillars and stand on one of the north-western pressure plates." },
  { text = "Control Peck and pull the lever, squeeze back through the gap and investigate the magic." },
  { text = "To exit, go through the southern door as any of the goebies." },
  { text = "Enter the door on the northern wall.", title = "Third trial" },
  { text = "Control Lunch and headbutt the western wall." },
  { text = "Control Peck and squeeze through. Pull the lever to reveal a pressure plate." },
  {
    text = "Control Tunks. Jump over chasm then place Tunks on the north-eastern small tile and then place Lunch on the south-eastern large tile.",
    title = "Lever 1",
  },
  {
    text = "Place Tunks on the north-western small tile and then place Lunch on the south-eastern large tile.",
    title = "Lever 2",
  },
  { text = "Place Tunks on the south-eastern small tile and then place Lunch on the north-western large tile." },
  {
    text = "Place Tunks on the southern small tile and then place Lunch on the north-western large tile.",
    title = "Lever 3",
  },
  {
    text = "Place Tunks on the south-western small tile and then place Lunch on the south-eastern large tile.",
  },
  { text = "Place Tunks on the northern small tile and then place Lunch on the south-western large tile." },
  {
    text = "Place Tunks on the north-western small tile and then place Lunch on the south-western large tile.",
    title = "Lever 4",
  },
  { text = "Place Tunks on the north-eastern small tile and then place Lunch on the south-eastern large tile." },
  { text = "Place Tunks on the eastern small tile and then place Lunch on the north-eastern large tile." },
  { text = "Place Tunks on the south-eastern small tile and then place Lunch on the north-western large tile." },
  { text = "Investigate the magic as Peck, then exit the southern door.", title = "Release Xinachto" },
  {
    text = "Talk to Xinachto, break the shackle.",
    actions = {
      Action.ConversationHighlight:new("Why is Xinchto allowing this to happen?"),
      Action.ConversationHighlight:new("You're right, break the shackles!"),
    },
  },
  { text = "Go outside through the southern door." },
  { text = "Talk to the Ancient goebie." },
  {
    text = "Kill the waves of Airut and chargers by clicking on the ground or the minimap to attack.<ul><li>If you fail, speak to the Ancient goebie to start the waves again.</li></ul>",
    title = "Fighting",
  },
  { text = "Talk to the Ancient goebie for the poison stalk.", title = "Wrapping Up" },
  { text = "Run south-east to exit the 'Nemi forest'." },
  {
    text = "Run east to Kanatah and talk to Acca Kanatah.<ul><li>Talk to Mazcab guide outside Nemi Forest to return quickly to Acca Kanatah if you have the reputation.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about Call of the Ancestors."),
      Action.ConversationHighlight:new("I found the poison."),
      Action.ConversationHighlight:new(
        "We explored a lost temple, freed a spirit and used forgotten magic to defeat airut."
      ),
      Action.ConversationHighlight:new("Kanatah"),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Call of the Ancestors",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1444608000,
  prereqQuests = {},
})
