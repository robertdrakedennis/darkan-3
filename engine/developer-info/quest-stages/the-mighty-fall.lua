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
    text = "Speak to councillor Ur-tag in the north-easternmost house on the 1st floor[UK]2nd floor[US] of Dorgesh-Kaan.",
    title = "Graardor's march",
    neededItems = { ["Light source"] = { quantity = 1 } },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("How can I help?") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Climb-down the dark hole under tree in the Lumbridge Swamp. It is northwest of the water ruins.",
    title = "Bandosian congregation",
    neededItems = { ["Light source"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Head to the north-east in the caves and speak to the group of leaders.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Enough questions."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'm done here."),
    },
  },
  {
    text = "Return to Dorgesh-Kaan and speak to Zanik, who is standing near Ur-tag back at the quest start.",
    actions = { Action.ConversationHighlight:new("They don't stand a chance!") },
  },
  {
    text = "Travel to the Bandosian camp on the east side of the Goblin Village and talk to Zanik once more (a teleport using the Wicked hood to the mind altar, or a Goblin Village sphere can be used).",
    actions = {
      Action.ConversationHighlight:new("What do you mean, dying?"),
      Action.ConversationHighlight:new("Go on."),
      Action.ConversationHighlight:new("Don't say that. We can still save them."),
    },
  },
  { text = "Prepare for combat before proceeding to the next step." },
  { text = "Enter the portal to travel to Yu'biusk.", title = "Return to Yu'biusk" },
  {
    text = "Go around the mountain, up one ladder and follow the path south until you reach Zarador. Talk to him.",
    actions = {
      Action.ConversationHighlight:new("I have no more rules questions."),
      Action.ConversationHighlight:new("I have no more questions."),
    },
  },
  {
    text = "Head to your camp (travel west, down 2 ladders into a small room with a cooking pot). Talk to Burntmeat or My Arm.",
    actions = {
      Action.ConversationHighlight:new("Enough questions."),
      Action.ConversationHighlight:new("Great, thanks!"),
      Action.ConversationHighlight:new("That sure is a mystery."),
    },
  },
  {
    text = "Kill the human infiltrator when he appears.<ul><li>If Burntmeat doesn't suggest searching his body, check the normal guide for possible solutions to progress the quest.</li></ul>",
  },
  { text = "Return east to Zarador.", actions = { Action.ConversationHighlight:new("I'll get right on it.") } },
  { text = "Talk to the following goblins and kill them." },
  --TODO: Add goblin dialogue table
  {
    text = "Return to Zarador once all infiltrators have been dealt with.",
    title = "The Kyzaj Tournament",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Use the bank chest beside Zarador to bank any forbidden items. He will also explain the rules of the tournament.",
  },
  {
    text = "Talk to him once more to begin the tournament.",
    actions = { Action.ConversationHighlight:new("Just bring it!") },
  },
  {
    text = "After defeating Yelps, you <i>will</i> choose to kill him.",
    title = "Yelps",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "After you have made your choice speak to Zanik outside of the portal.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I need to get back to the tournament."),
    },
  },
  {
    text = "Return to Zarador after talking to Zanik. He will inform you that your next opponent is Lol, the troll champion.<ul><li>Suggested items: Stat-boosting potions such as overloads, extreme potions, or super potions, prayer potions, Enhanced Excalibur (or another good weapon), and lots of good food.</li><li>Forbidden items: You cannot wear armour, including auras, pocket items, and jewellery.</li></ul>",
    title = "Lol",
    actions = { Action.ConversationHighlight:new("Just bring it!") },
  },
  {
    text = "After Lol has been defeated, you must once again choose to kill or spare him.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Talk to Zarador again and, after some dialogue, he will give you a weapon known as the Kyzaj, the namesake of the tournament. He will tell you that you must use it against your final opponent, General Graardor, the champion of the ourgs.",
    title = "General Graardor",
    actions = { Action.ConversationHighlight:new("Enough questions, give me the Kyzaj so I can practice with it.") },
  },
  {
    text = "Talk to Zanik outside of the portal and she will teach you how to wield and use the Kyzaj.",
    actions = { Action.ConversationHighlight:new("I need you to tell me about the Kyzaj.") },
  },
  {
    text = "When you are ready to begin the final battle, return to Zarador and talk to him.<ul><li>Rules for this round: You may not bring in your own weapons (you must use the Kyzaj, a tier 70 weapon), and prayer is disabled. You may equip armour and jewellery, although runes and non-combat items (excluding food) are forbidden.</li><li>The Kyzaj will automatically equip itself once the fight starts; you will not be able to equip it beforehand.</li><li>Fighting General Graardor he has three main mechanics.</li><li>The first one is a ground smash that affects the centre of the arena. Being in the centre makes you get hit for up to 5k damage.</li><li>The second attack is a cleave animation with no warning. Sidestep a few tiles and go back to attacking.</li><li>The third mechanic he will destroy a section of the outer ring. This will hit for 5k damage if you do not move to another outer ring.</li><li>The first one is a ground smash that affects the centre of the arena. Being in the centre makes you get hit for up to 5k damage.</li><li>The second attack is a cleave animation with no warning. Sidestep a few tiles and go back to attacking.</li><li>The third mechanic he will destroy a section of the outer ring. This will hit for 5k damage if you do not move to another outer ring.</li></ul>",
    actions = { Action.ConversationHighlight:new("Just bring it!") },
  },
  {
    text = "Zarador throws Zanik into the arena, giving you two choices: you may either slay Zanik or spare her.<ul><li>Your Kyzaj appearance will initially depend upon your decision to kill or spare Zanik (you receive the Bloodied kyzaj if you kill her or the Honourable kyzaj if you spare her), but these can be freely exchanged after the quest.</li></ul>",
    title = "The new Big High War God",
    actions = {
      Action.ConversationHighlight:new("Enough questions. I need to speak to Zanik"),
      Action.ConversationHighlight:new("I'm ready."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  {
    text = "If you chose to kill Zanik, you must speak to General Graardor to find out his plans (Graardor can be found by going south from Zarador, up the ladder, and taking the first pathway to the west to the bank spot).",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "If you chose to spare Zanik, you must speak to her and find out her plans (Zanik is standing next to Zarador).",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Return to Ur-tag in Dorgesh-Kaan." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Mighty Fall",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1401667200,
  prereqQuests = {
    "The Chosen Commander",
    "Missing, Presumed Death",
    "My Arm's Big Adventure",
    "The Hunt for Surok (miniquest)",
  },
})
