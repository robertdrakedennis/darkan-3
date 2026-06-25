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
    text = "Enter the Golden Palace in Menaphos.",
    title = "Collecting reports",
    neededItems = { ["Backpack"] = { quantity = 4 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new("(Repeated in a pop-up info box:)You can't afford that.(Continues below.)"),
    },
  },
  { text = "Talk to Pharaoh Osman, Chosen of Tumeken.", postconditions = { Condition.QuestInterfaceOpen:new() } },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Go across the bridge east of the Menaphos lodestone into Sophanem and talk to the High Priest.",
    actions = { Action.ConversationHighlight:new("Talk about 'Beneath Scabaras' Sands'.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Go to Pollnivneach and talk to Hakeem the Mayor.",
    actions = { Action.ConversationHighlight:new("Talk about 'Beneath Scabaras' Sands'.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Go to Nardah and talk to Awusah the Mayor.",
    actions = { Action.ConversationHighlight:new("Talk about 'Beneath Scabaras' Sands'.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Go to Al Kharid and talk to Emir Ali Mirza in the Al Kharid palace.",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Beneath Scabaras' Sands'."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Enter the Golden Palace in Menaphos.",
    title = "Under attack!",
    neededItems = {
      ["Report: Al Kharid"] = { quantity = 1 },
      ["Report: Nardah"] = { quantity = 1 },
      ["Report: Pollnivneach"] = { quantity = 1 },
      ["Report: Sophanem"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new("(Repeated in a pop-up info box:)You can't afford that.(Continues below.)"),
    },
  },
  { text = "Kill the 4 Profane Scabarites and burst the 4 corrupted scarabs nearby." },
  {
    text = "Head down the steps near the Menaphos lodestone and kill the 4 Profane Scabarites and burst the 4 corrupted scarabs.<ul><li>The Profane Scabarites and corrupted scarabs from the previous step must dealt with for the ones on the lower level to spawn.</li></ul>",
  },
  { text = "Head back up to the palace and talk to Commander Akhomet." },
  { text = "Enter the Golden Palace and talk to Pharaoh Osman, Chosen of Tumeken." },
  {
    text = "Enter the Grand Library.",
    title = "The hunt for the golden scarab",
    neededItems = { ["Category:Zarosian artefacts"] = { quantity = 1 }, ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Beneath Scabaras' Sands") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Talk to Kohnen's assistant. You do not need to follow the assistant, and can just run ahead to Leela and Maisa who are in the south-western corner (down two levels, yellow dot on the minimap).",
  },
  { text = "Talk to Leela or Maisa." },
  {
    text = "Search the nearby bookcases until you obtain The Shiny Scarab.<ul><li>The location can be different for each player.</li></ul>",
  },
  { text = "Talk to Leela or Maisa again." },
  {
    text = "Enter the Uzer Mastaba.",
    actions = { Action.ConversationHighlight:new("Head directly to the Pharaoh Queen.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Talk to Senliten." },
  { text = "Talk to Lead Archaeologist Kerner in the ruins of Uzer." },
  {
    text = "Talk to Lead Archaeologist Kerner again.",
    actions = { Action.ConversationHighlight:new("Yes") },
    postconditions = { Condition.ConversationText:new(" Meet us at the city gates and we can start over.") },
  },
  {
    text = "Gather scorched sand from any of the nearby locations. Continue screening the sand with the screening tray until you have 30 Kharidian gold and 2 ancient rubies.",
  },
  { text = "Restore the damaged golden scarab with the nearby archaeologist's workbench." },
  {
    text = "Return and enter the Golden Palace",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new("(Repeated in a pop-up info box:)You can't afford that.(Continues below.)"),
    },
  },
  {
    text = "Talk to Pharaoh Osman, Chosen of Tumeken.",
    title = "Discovering the temple",
    neededItems = { ["Golden scarab (Beneath Scabaras' Sands)"] = { quantity = 1 }, ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Talk to Leela just outside the northern gates of Menaphos." },
  { text = "Release the golden scarab." },
  {
    text = "Follow the golden scarab at the locations indicated on the map on the right until you reach the Scabarite Cavern.<ul><li>Teleporting directly to the cavern will not work.</li><li>Don't miss the final golden scarab spot directly in front of the cavern. Doing so will cause you to have to repeat the entire path from Menaphos.</li></ul>",
  },
  {
    text = "Enter the cave entrance.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new("(Repeated in a pop-up info box:)You can't afford that.(Continues below.)"),
    },
  },
  { text = "Head south and follow the golden scarab once more." },
  {
    text = "Scavenge the piles of debris in the cavern until you have five sets of boat materials. If a Profane Scabarite appears, it must be killed to obtain the materials. Teleporting out will result in loss of materials.",
  },
  { text = "Repair the old boat." },
  { text = "Travel with the old boat." },
  { text = "Head east and up the steps and talk to Scabaras." },
  {
    text = "Go back down the steps and interact with Fear. You need to keep interacting with Fear to scare it up the steps to Scabaras. Guide it up the northern side of the steps as it is more likely to get stuck on the southern side.",
  },
  {
    text = "Go through all options during the flashback. Scabaras will expect a specific response to each of his three statements, and the order is random for each player.<ul><li>'Mention of his children would have given him pause.'</li><li>'What if we had proposed an alternative?'</li><li>'What would Lady Elidinis have thought?'</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Amascut and Icthlarin are not ready."),
      Action.ConversationHighlight:new("There has to be another way."),
      Action.ConversationHighlight:new("What insight has Lady Elidinis provided?"),
    },
    postconditions = {
      Condition.ConversationText:new(" She knows what must be done. For the future of the empire. For our children."),
    },
  },
  {
    text = "Following the cutscene, continue through the dialogue.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Interact with Ego at the bottom of the steps." },
  {
    text = "Pick from a cavern dwellberry bush near the old boat. Filling your backpack with several is helpful, but only one cavern dwellberry is needed if you're picking it back up just after dropping them in the next step.",
  },
  {
    text = "Drop the cavern dwellberries to lure Ego up the steps.<ul><li>Ego can only be lured within five game squares.</li></ul>",
  },
  {
    text = "Go through all options during the second flashback.<ul><li>'Taking a firmer stance against her transgression may have dissuaded her.'</li><li>'What if we had challenged her perspective?'</li><li>'What if we had placated her?'</li></ul>",
    actions = {
      Action.ConversationHighlight:new("That is unacceptable!"),
      Action.ConversationHighlight:new("Your father sought to protect you."),
      Action.ConversationHighlight:new("You are right."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Elidinis has turned her back on us. Tumeken's sacrifice sowed the seeds of discord and decay."
      ),
    },
  },
  { text = "Following the cutscene, continue through the dialogue." },
  {
    text = "Interact with Melancholy at the bottom of the steps and use the chat options to motivate the scarab to make it move up the steps repeatedly. Go through all chat options until it moves from each stop.",
  },
  {
    text = "After the third flashback, head down the steps to find Grief.<ul><li>Grief appears as a red dot on the minimap.</li></ul>",
    title = "Stomping out the grief",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Keep stamping out Grief until it transforms into a level 146 monster." },
  { text = "Defeat the monster." },
  {
    text = "Talk to Scabaras.",
    actions = {
      Action.ConversationHighlight:new("Ask about Amascut's plan."),
      Action.ConversationHighlight:new("Continue Quest."),
    },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Beneath Scabaras' Sands",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1736726400,
  prereqQuests = { "'Phite Club", "Ode of the Devourer" },
})
