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
    text = "Talk to Grubfoot in the Dorgeshuun Mines.",
    title = "Land of the Goblins",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Mistag, why won't you let Grubfoot into the city?"),
      Action.ConversationHighlight:new("I'll take responsibility for Grubfoot."),
    },
  },
  {
    text = "[Accept Quest]<ul><li>Dismiss any familiar or Grubfoot won't follow you.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "With Grubfoot following, enter Dorgesh-Kaan using the door to the south and head north-west where Oldak is.",
    title = "Yu'biusk",
  },
  {
    text = "In the north-west room, talk to Zanik.",
    actions = {
      Action.ConversationHighlight:new("So why have you come to talk to Zanik?"),
      Action.ConversationHighlight:new("What was this new dream?"),
      Action.ConversationHighlight:new("How do you know your dream was true?"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I promise not to tell anyone where the temple is."),
      Action.ConversationHighlight:new("I'm ready."),
    },
    postconditions = { Condition.ConversationText:new(" Then let's go!") },
  },
  {
    text = "If you forgot to bring the toadflax potion, proceed until Zanik has entered into the temple.<ul><li>Otherwise, you will need to go back to Oldak in Dorgesh-Kaan to meet with her, and provide him two law runes and one molten glass.</li></ul>",
    title = "You not a goblin!",
    neededItems = {
      ["Black mushroom ink"] = { quantity = 1 },
      ["Vial"] = { quantity = 1 },
      ["Toadflax potion (unfinished)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Once you teleport with Zanik, head north-west to the temple entrance blocked by two goblin guards.<ul><li>Pick and grind a black mushroom into an empty vial if you don't already have black mushroom ink.</li></ul>",
  },
  { text = "Talk to the guards and they will allow Zanik to pass into the temple." },
  {
    text = "Talk to the Makeover Mage south-west of Falador.",
    actions = {
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("Can you turn me into a goblin?"),
      Action.ConversationHighlight:new("I need to slip past some goblin guards."),
      Action.ConversationHighlight:new("Can you turn me into a goblin or not?"),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Use the pharmakos berries on an unfinished toadflax potion to create the goblin potion.<ul><li>(Optional) After making the potion, collect another set of pharmakos berries if planning to complete the Contract Claws mystery.</li></ul>",
  },
  { text = "Head to the goblin cave (just north of the Ardougne lodestone)." },
  {
    text = "Unequip all items and dismiss any followers.",
    title = "Transmogrification",
    neededItems = { ["Goblin mail"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "In front of the goblin guards, drink the goblin potion (pick any goblin appearance)." },
  {
    text = "Climb down the entrance.",
    actions = {
      Action.ConversationHighlight:new("Me want get into temple."),
      Action.ConversationHighlight:new("Yes, I'm [goblin name]."),
    },
  },
  {
    text = "North, sitting on his throne, talk to the High Priest.",
    actions = {
      Action.ConversationHighlight:new("Can you tell me about Yu'biusk?"),
      Action.ConversationHighlight:new("I understand the ways of the Big High War God."),
      Action.ConversationHighlight:new("I'm ready for the test."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Take the quiz.",
    actions = {
      Action.ConversationHighlight:new("True."),
      Action.ConversationHighlight:new("The Big High War God commands it."),
      Action.ConversationHighlight:new("False."),
      Action.ConversationHighlight:new("Goblins were not mighty warriors before he chose us."),
      Action.ConversationHighlight:new("False."),
      Action.ConversationHighlight:new("That's one of the commandments."),
      Action.ConversationHighlight:new("Lead goblins to victory over the whole world."),
    },
    postconditions = {
      Condition.ConversationText:new(" That right! And war will end in victory and victory last forever!"),
    },
  },
  {
    text = "After the quiz, ask about Yu'biusk.",
    actions = {
      Action.ConversationHighlight:new("Can you tell me about Yu'biusk?"),
      Action.ConversationHighlight:new("Where is Yu'biusk?"),
      Action.ConversationHighlight:new("Never mind."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Dye your goblin mail black with the mushroom ink." },
  { text = "Wear the black goblin mail and go in the north-east room by talking to the goblin in the passage." },
  { text = "Pickpocket the priest for a key. Keep this key. You will need it later." },
  {
    text = "Talk to Zanik in the prison cell.",
    actions = { Action.ConversationHighlight:new("Sit tight. I'll be back soon.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Search the crate in the room to find the teleport sphere." },
  {
    text = "Talk to Zanik again, she will teleport.",
    actions = { Action.ConversationHighlight:new("I have a teleport sphere here.") },
    postconditions = { Condition.ConversationText:new(" Oh, thank you, [Player]!") },
  },
  {
    text = "Leave the cave.<ul><li>You can equip a piece of equipment to be thrown out, then you can teleport to Draynor.</li></ul>",
    title = "Whitefish",
    neededItems = {
      ["Coins"] = { quantity = 1 },
      ["Black goblin mail"] = { quantity = 1 },
      ["Slimy eel"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Talk to Aggie in Draynor Village.<ul><li>Or, if Vampyre Slayer is complete.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("Can you make dyes for me please?"),
      Action.ConversationHighlight:new("Can you make black or white dye?"),
      Action.ConversationHighlight:new("More..."),
      Action.ConversationHighlight:new("Can you make dyes for me please?"),
      Action.ConversationHighlight:new("Can you make black or white dye?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Black dye can be made by grinding up black mushrooms. That's dead easy. The mushrooms grow in some old caves and ruins, and you can grind them up yourself with a pestle and mortar."
      ),
    },
  },
  {
    text = "Teleport to Seers' Village lodestone and head south-west to Hemenster near the Ranging Guild. Alternatively, use a combat bracelet.<ul><li>Be sure to have a raw slimy eel or you will have to repeat the next steps.</li></ul>",
  },
  {
    text = "Attempt to open the gate near Morris.",
    actions = { Action.ConversationHighlight:new("I need to catch a Hemenster whitefish.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "After finishing the dialogue, open the gate." },
  { text = "Catch a whitefish." },
  { text = "Return to Aggie and use the goblin mail on her." },
  {
    text = "Unequip any items and dismiss any followers.",
    title = "Macabre crypt",
    neededItems = {
      ["Blue dye"] = { quantity = 1 },
      ["Yellow dye"] = { quantity = 1 },
      ["Purple dye"] = { quantity = 1 },
      ["Orange dye"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Go back to the goblin temple entrance, take a sip of your goblin potion, and equip your white goblin mail.",
  },
  { text = "Enter the temple." },
  {
    text = "Steal the white key from the priest in the room west of the temple.<ul><li>Remove the goblin mail to leave the room quicker.</li></ul>",
  },
  {
    text = "Repeat the operation of stealing keys, dying your mail in the following order:<ul><li>Yellow (north-west)</li><li>Purple (south-west)</li><li>Orange (south-east)</li><li>Blue (east)</li></ul>",
  },
  { text = "Enter the crypt (large door north of the temple).", title = "Prepare for battle" },
  { text = "Prepare for battle by equipping weapons and armour." },
  {
    text = "Use the Say-name option on each of the graves. After killing them, talk to them.<ul><li>South-west: Snothead</li><li>South-east: Snailfeet</li><li>North-west: Mosschin</li><li>North-east: Redeyes</li><li>North: Strongbones (ignore his skeleton minions)</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Where is Yu'biusk?"),
      Action.ConversationHighlight:new("What was your predecessor's name?"),
      Action.ConversationHighlight:new("Goodbye."),
      Action.ConversationHighlight:new("Snailfeet"),
      Action.ConversationHighlight:new("Mosschin"),
      Action.ConversationHighlight:new("Redeyes"),
      Action.ConversationHighlight:new("Strongbones"),
    },
  },
  {
    text = "Talk to Strongbones after defeating him.",
    actions = { Action.ConversationHighlight:new("Where is Yu'biusk?"), Action.ConversationHighlight:new("Goodbye.") },
    postconditions = {
      Condition.ConversationText:new(
        " Goodbye, human. I am sure that the Chosen Commander will find what she is looking for when you bring her to Yu'biusk."
      ),
    },
  },
  { text = "Return to Dorgesh-Kaan and speak with Zanik in the north-west (Oldak's lab).", title = "Home portal" },
  {
    text = "With a light source, head to the south end of Dorgesh-Kaan using fairy code AJQ. Alternatively, without using fairy rings:<ul><li>Go up the stairs to the agility course.</li><li>At the start of the agility course, climb down the ladder west of the agility cable.</li><li>Follow the cave path east then south to the Fairy ring.</li></ul>",
  },
  { text = "Adjust the machines next to Oldak, use combination 9-4-1, activate the machine." },
  { text = "Open the strange box in the north-western part of Yu'biusk for a cutscene." },
  { text = "Continue dialogue with Oldak." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Land of the Goblins",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1194825600,
  prereqQuests = { "Another Slice of H.A.M.", "Fishing Contest" },
})
