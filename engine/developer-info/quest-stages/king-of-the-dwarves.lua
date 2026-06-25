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
    text = "Speak to Lava-flow miner Sven standing outside of the Lava Flow Mine in Keldagrim East.",
    title = "Starting the quest",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("I'd like the tour.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Free all 6 Lava-flow miners (they are all close to the entrance). If you leave the scene by exiting or teleporting, all progress made of rescuing the miners is lost.<ul><li>Mine the lighter coloured rubble surrounding them, then rescue them and they'll appear in your backpack. Only one Miner can be carried at a time.</li><li>Talk to Foreman Jaak with one in your backpack, then move onto the next one.</li></ul>",
    title = "Rescuing the miners",
  },
  {
    text = "Talk to Veldaban near the protesters outside.",
    title = "Releasing Veldaban",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Go to the top floor of the Keldagrim Palace in the centre of Keldagrim. A short cutscene will play." },
  {
    text = "Talk to Veldaban.",
    actions = {
      Action.ConversationHighlight:new("Why are you so angry?"),
      Action.ConversationHighlight:new("You should try to calm down."),
    },
  },
  {
    text = "Talk to the Green Gemstone Director.",
    actions = {
      Action.ConversationHighlight:new("I was there when the explosion happened."),
      Action.ConversationHighlight:new("I rescued some of the miners."),
    },
  },
  {
    text = "Talk to the Yellow Fortune Director.",
    actions = {
      Action.ConversationHighlight:new("Why did you arrest Veldaban?"),
      Action.ConversationHighlight:new("Veldaban's popular. Arresting him makes you look worse."),
    },
  },
  {
    text = "Talk to the Blue Opal Director.",
    actions = {
      Action.ConversationHighlight:new("Tell me about the Lava Flow Mine."),
      Action.ConversationHighlight:new("So the chaos dwarf blew up the secondary boilers?"),
    },
  },
  {
    text = "Talk to the Brown Engine Director.",
    actions = {
      Action.ConversationHighlight:new("What did you want to do about the miners?"),
      Action.ConversationHighlight:new("It was a difficult decision. I understand."),
    },
  },
  {
    text = "Dismiss any pets and talk to the consortium general secretary.",
    actions = { Action.ConversationHighlight:new("I've talked to everyone I need to.") },
  },
  {
    text = "Talk to Hreidmar outside the eastern entrance of the Keldagrim Palace.",
    title = "Records chamber",
    neededItems = { ["Soft clay"] = { quantity = 1 }, ["Mithril bars"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Talk to one of the protesters.",
    actions = { Action.ConversationHighlight:new("Does anyone know where Meike is?") },
  },
  {
    text = "Head to the King's Axe Inn (Keldagrim West) and talk to Luitger.",
    actions = {
      Action.ConversationHighlight:new("You're not joining the protests?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Go to the Black Guard HQ (west of the bank) and talk to Klaas." },
  {
    text = "Go to the top of the Keldagrim Watchtower (small building just south of the Palace, west Keldagrim) and talk to Meike.",
    actions = {
      Action.ConversationHighlight:new("We could get into the records of the chamber first."),
      Action.ConversationHighlight:new("I'll meet you both at the library."),
    },
  },
  { text = "Go to the library north of the Palace in West Keldagrim." },
  { text = "Talk to Meike or Veldaban." },
  {
    text = "Talk to the Librarian.",
    actions = { Action.ConversationHighlight:new("Could you open the display case, please?") },
  },
  { text = "Talk to Meike or Veldaban." },
  { text = "Open the cabinet north of Meike." },
  {
    text = "Use the soft clay on Meike and then talk to the librarian about the keys.",
    actions = { Action.ConversationHighlight:new("Can you tell me more about the keys?") },
  },
  { text = "Talk to Meike." },
  {
    text = "Use the furnace (not forge) such as the one in the Palace south of the library, if the prompt doesn't show up go to another furnace.",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Go to the Records Chamber (Enter Entrance in the wall, west of the Black Guard HQ)" },
  { text = "Talk to Meike or Veldaban." },
  {
    text = "Match up the keys with the correct keyholes. See the picture below for the solution. The combination to unlock the door",
  },
  { text = "Open the door and watch the cutscene." },
  {
    text = "Talk to Veldaban.",
    title = "Defeating Colonel Grimsson",
    actions = { Action.ConversationHighlight:new("Can you show me the way to the Barendir caverns?") },
  },
  { text = "Enter the tunnel to the east and go to the opposite end of the cavern." },
  {
    text = "Defeat Colonel Grimsson while making sure Veldaban does not die. If you leave the fight, you can restart by talking to Veldaban in the Laughing Miner pub in Keldagrim East.",
  },
  {
    text = "Talk to Veldaban.",
    title = "Recruiting the trolls",
    actions = {
      Action.ConversationHighlight:new("We should get reinforcements."),
      Action.ConversationHighlight:new("What about the trolls?"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'm going to go and talk to them, with or without you."),
    },
  },
  {
    text = "Head back halfway through the cavern until you come to an entrance on the east side. Enter it and speak to Pretty Flower.",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you."),
      Action.ConversationHighlight:new("How can I prove I'm worthy?"),
    },
  },
  { text = "Lift the big rock opposite My Arm." },
  { text = "Lift the troll named Big Rock." },
  {
    text = "Talk to Pretty Flower .",
    actions = {
      Action.ConversationHighlight:new("We want you to help defend Keldagrim from chaos dwarves."),
      Action.ConversationHighlight:new("I'll just be going."),
    },
  },
  {
    text = "Talk to My Arm .",
    actions = {
      Action.ConversationHighlight:new("How can I get Pretty Flower to listen to me?"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Wait, what pretty flower are you talking about?"),
      Action.ConversationHighlight:new("Could I have a pretty flower?"),
      Action.ConversationHighlight:new("I need to give it to Pretty Flower."),
      Action.ConversationHighlight:new("I need to give the plant to the warlord."),
    },
  },
  {
    text = "Talk to Pretty Flower.",
    actions = {
      Action.ConversationHighlight:new("We want you to help defend Keldagrim from chaos dwarves."),
      Action.ConversationHighlight:new("Take a look at this pretty flower..."),
      Action.ConversationHighlight:new("It's a gift from Colonel Grimsson."),
    },
  },
  { text = "Go out through the same way you entered with Veldaban." },
  {
    text = "Get back to the east of the Keldagrim Palace by using the quick travel option on the Dwarven Boatman to the west.",
  },
  { text = "Go to the protest east of Keldagrim Palace.", title = "Finishing up" },
  { text = "Talk to Veldaban, who is standing within the group of protesters." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "King of the Dwarves",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1294099200,
  prereqQuests = { "Forgiveness of a Chaos Dwarf", "My Arm's Big Adventure" },
})
