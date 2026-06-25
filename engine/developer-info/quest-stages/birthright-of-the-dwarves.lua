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
    text = "Talk to King Veldaban on the top floor of Keldagrim Palace.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Hello, Veldaban."),
      Action.ConversationHighlight:new("So you're king - what now?"),
      Action.ConversationHighlight:new("So what are you going to do?"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Okay, bye.") },
  },
  {
    text = "Enter the records chamber cave to the west of the bank (along the west wall).",
    title = "Investigating the spies",
    neededItems = { ["Royal dwarven seal"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Talk to Lieutenant Brae.",
    actions = {
      Action.ConversationHighlight:new("He's not coming. He sent me instead."),
      Action.ConversationHighlight:new("Thanks."),
    },
  },
  { text = "Talk to Vigr." },
  {
    text = "Talk to Brendt.",
    actions = {
      Action.ConversationHighlight:new("Why did King Sorvott say you were from the Red Axe?"),
      Action.ConversationHighlight:new("I'll come back."),
    },
  },
  {
    text = "Talk to Grundt.",
    actions = { Action.ConversationHighlight:new("What was the nickname of your ship's captain?") },
  },
  {
    text = "Talk to Brendt.",
    actions = {
      Action.ConversationHighlight:new("Grundt says the ship's captain was Sigridsdottir."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Klaas.",
    actions = {
      Action.ConversationHighlight:new("Calling for the overthrow of the king is treason."),
      Action.ConversationHighlight:new("How do you know Veldaban isn't the rightful king?"),
      Action.ConversationHighlight:new("I think the Red Axe told you to say this."),
      Action.ConversationHighlight:new("I'll come back."),
    },
  },
  {
    text = "Go to the western ground floor[UK]1st floor[US] of the Consortium and search the furnace in the north-eastern room.",
  },
  { text = "Read the bundle of letters." },
  {
    text = "Go to the King's Axe Inn, south of the bank, and talk to Meike.",
    actions = {
      Action.ConversationHighlight:new("Klaas is calling for a revolution."),
      Action.ConversationHighlight:new("Why does Klaas think Veldaban isn't the rightful king?"),
      Action.ConversationHighlight:new("Could he have got the idea from the Red Axe?"),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  { text = "Return to the cave the suspects are in." },
  {
    text = "Talk to Vigr.",
    actions = {
      Action.ConversationHighlight:new("Here are some letters I found in your workshop."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Klaas.",
    actions = {
      Action.ConversationHighlight:new("I spoke to Meike. She vouched for you."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Don't forget to judge all the dwarfs and make sure they are gone. Otherwise you won't find Veldaban in the cave later on.",
  },
  {
    text = "Return to King Veldaban.",
    actions = {
      Action.ConversationHighlight:new("I've dealt with the suspected spies."),
      Action.ConversationHighlight:new("Vigr said the Red Axe broke off contact with him."),
      Action.ConversationHighlight:new("Goodbye."),
    },
  },
  {
    text = "Go to the Taverley Dungeon and kill chaos dwarves (south of the Lesser demons) until Ikadia the Exile appears.",
    title = "Taverley chaos dwarves",
  },
  {
    text = "After she talks to you and teleports away, go to the chaos druid circle in the north-eastern corner of the dungeon and talk to her.",
    actions = { Action.ConversationHighlight:new("I am ready.") },
  },
  {
    text = "Defeat her and talk to her again.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Go to the Yanille's Watchtower<ul><li>Use Watchtower Teleport or Watchtower teleport tablet</li><li>Alternatively, teleport to Yanille lodestone to Yanille and use the northern shortcuts to go under the north wall (requires 16 Agility) then west outside the northern wall</li><li>If you do not have the Watchtower quest completed, Climb-up the Trellis on the northern side of the Watchtower.</li></ul>",
    title = "Yanille's Watchtower Wizard",
  },
  {
    text = "Dismiss any Summoning familiar you have, and talk to the Watchtower Wizard on the top floor.<ul><li>You'll be given a memory wand. Keep the wand for the rest of the quest, do not destroy it.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("King Veldaban sent me..."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I'm ready."),
    },
  },
  { text = "Use the wand on the 4 floating objects." },
  { text = "Catch all the loose memory fragments. There will be 16 fragments total, 4 of each type." },
  {
    text = "Investigate each memory void. Based on the text that appears, use the corresponding memory fragment on the memory void following the table below:",
  },
  {
    text = "After the cutscene, return to King Veldaban.",
    actions = {
      Action.ConversationHighlight:new("I investigated Taverley dungeon."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I got a wand from the Watchtower Wizard."),
      Action.ConversationHighlight:new("I'll keep hold of it."),
      Action.ConversationHighlight:new("I'll meet you in the Barendir caves."),
    },
  },
  {
    text = "Enter the Barendir caves southeast of west Keldagrim. The caves can be accessed by selecting 'Travel' on the Dwarven Boatman on the eastern side of Keldagrim near the northern bridge, or selecting 'Cross-river' on the Dwarven Ferryman to the south of western Keldagrim.",
    title = "The assault on the Red Axe",
  },
  { text = "Proceed to the north end of the tunnel and enter the cave at the end." },
  { text = "Talk to Veldaban. If Veldaban isn't there, make sure the cave with suspects is empty." },
  { text = "Talk to Nulodion. He'll give you explosive gears." },
  {
    text = "Fire the cannon. This part is not a safe death. Should you die, your gravestone will be a short walk inside the Barendir caves.",
  },
  {
    text = "After the cutscene, stand behind the pillars to get the cannons to shoot them down, and sabotage the cannons as you progress. The first one is to the west. Do not stand near a cannon if it is about to explode, as it will hit for 5,000+ damage. Praying against range is strongly recommended.",
  },
  { text = "After destroying all 5 cannons, walk up onto the platform with Colonel Grimsson." },
  { text = "Pull the lever that's on top of the platform above the minecart, then get into the mine cart." },
  {
    text = "After getting in the cart, there will be a cutscene and you will end up in another memory.",
    title = "Repairing the memories",
  },
  {
    text = "Use the Memory wand on the royal Red Axe standard (flag, west of Veldaban), the Zamorakian Chaplain, and the chaos dwarf standing next to him.",
  },
  { text = "Catch all the loose memory fragments. There will be 9 fragments total, 3 of each type." },
  {
    text = "Investigate each memory void. Based on the text that appears, use the corresponding memory fragment on the memory void following the table below:",
  },
  { text = "Talk to Veldaban. You will then be put into the next memory." },
  {
    text = "In the next memory, use the wand on the two trolls directly next to Grimsson (each provide 6 memory fragments) and repeat the memory fragment process following the table below:",
  },
  { text = "Talk to Grimsson (his dialogue won't appear while he's busy fending the trolls)." },
  { text = "In the next memory, use the wand on Zamorak and repeat the memory fragment process." },
  {
    text = "In the next memory, use the wand on the 3 dwarves, catch the memory fragments only when they're not labelled 'dangerous', and repeat the process following the table below:",
  },
  { text = "Kill Grunsh or let him go.", title = "Veldaban of the Red Axe" },
  {
    text = "Enter the eastern doorway behind King Veldaban, then enter the palace and go up to the 1st floor[UK]2nd floor[US].",
  },
  { text = "Try to open the door with the chaos dwarf behind it." },
  {
    text = "Right click and talk to one of the directors.",
    actions = { Action.ConversationHighlight:new("My friend here would like to join the Red Axe.") },
  },
  { text = "Kill 7 of the 8 directors." },
  { text = "Talk to the remaining director." },
  { text = "Go upstairs to the top floor of the palace." },
  {
    text = "Talk to Grimsson and begin the fight.",
    title = "The final confrontation",
    actions = { Action.ConversationHighlight:new("All right, Grimsson - bring it on.") },
  },
  {
    text = "Kill Grimsson. If Veldaban dies, you will respawn outside the cave and need to re-enter and try again from the start.",
  },
  {
    text = "Kill Chaos Grimsson. Try to lure and isolate him on the side with the stairs, just outside the main room. When his adrenaline bar fills up and he's about to charge you, go down the stairs and wait for 10 seconds before coming back up.",
  },
  { text = "Kill Chaos Hreidmar." },
  { text = "Either kill or save Veldaban.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Speak with any director." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Birthright of the Dwarves",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.verylong,
  releaseDate = 1380672000,
  prereqQuests = { "King of the Dwarves", "The Fremennik Isles", "Watchtower" },
})
