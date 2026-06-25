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
    text = "Bring Leela to Senliten by talking to her along the fence north of the prison in Draynor Village. Agree to travel with her and you will end up in front of Senliten.<ul><li>If you sent her to Senliten without following her, travel to the Uzer Mastaba (the pyramid south of Uzer) via some other means.</li></ul>",
    title = "Starting out",
    actions = { Action.ConversationHighlight:new("Head directly to the Pharaoh Queen.") },
  },
  {
    text = "Speak with Senliten.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Talk about Crocodile Tears."),
      Action.ConversationHighlight:new("Where are you going with this?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to the Sphinx in Sophanem with your cat (do not wear a catspeak amulet or cramulet).",
    title = "Learning about Crondis",
    neededItems = {
      ["Rope"] = { quantity = 1 },
      ["Cat"] = { quantity = 1 },
      ["Kitten"] = { quantity = 1 },
      ["Hellcat"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Can you tell me about the gods of the Menaphites?"),
      Action.ConversationHighlight:new("Tell me about the minor gods."),
      Action.ConversationHighlight:new("Tell me about Crondis."),
      Action.ConversationHighlight:new("Thanks, I think I now have a better understanding of this place."),
    },
  },
  {
    text = "Talk to Jex, who is north-east of the Sphinx.",
    actions = {
      Action.ConversationHighlight:new("Could you tell me more about the minor gods?"),
      Action.ConversationHighlight:new("Could you tell me more about Crondis?"),
      Action.ConversationHighlight:new("Could you tell me more about Crondis's connection with crocodiles?"),
      Action.ConversationHighlight:new("Could you tell me more about Crondis's followers?"),
      Action.ConversationHighlight:new("Could you tell me more about the Crondis's influence over the desert?"),
    },
  },
  {
    text = "Return to Senliten.",
    actions = { Action.ConversationHighlight:new("Talk about Crocodile Tears.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Right click Leela to return to the surface." },
  {
    text = "Enter the temple again but use the explore option.",
    title = "Dousing for Crondis",
    actions = { Action.ConversationHighlight:new("Explore the temple.") },
  },
  { text = "Head north and click the ship to get the dowsing rod (Pick up relic Massive stone boat)." },
  {
    text = "Click the dowsing rod and follow the arrow to the following locations in the desert, until your character starts talking, before moving to the next location. (Note: Some players report that closing the arrow interface, using a magic carpet, or teleporting might cause bugs. If this happens, you may need to destroy the rod and do it all over again. It is possible that this is now fixed.)<ul><li>The oasis just north-west of the temple.</li><li>The oasis by the Camel Warriors, east of Sophanem.</li><li>The oasis near the Bedabin Camp.</li></ul>",
  },
  {
    text = "Return to Senliten.",
    actions = { Action.ConversationHighlight:new("Talk about Crocodile Tears.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Head to the waterfall north of the Dominion Tower and climb it." },
  { text = "Walk north through both sets of double doors" },
  {
    text = "Talk to the spirits (Tirrie, Nirrie, or Hallak) in the north part of the cave.",
    actions = { Action.ConversationHighlight:new("I need your help to charge the dowsing rod.") },
    postconditions = { Condition.ConversationText:new(" Alas, Crondis is not silent, we") },
  },
  {
    text = "Talk to the Sphinx in Sophanem with your cat (do not wear a catspeak amulet or cramulet).",
    actions = {
      Action.ConversationHighlight:new("Can you tell me about the gods of the Menaphites?"),
      Action.ConversationHighlight:new("Tell me about the minor gods."),
      Action.ConversationHighlight:new("Tell me about Crondis."),
      Action.ConversationHighlight:new("Thanks, I think I now have a better understanding of this place."),
    },
  },
  {
    text = "Talk to Jex, who is north-east of the Sphinx.",
    actions = {
      Action.ConversationHighlight:new("Could you tell me more about the minor gods?"),
      Action.ConversationHighlight:new("Could you tell me more about Crondis?"),
      Action.ConversationHighlight:new("Could you tell me more about Crondis's connection with crocodiles?"),
      Action.ConversationHighlight:new("Could you tell me more about Crondis's followers?"),
      Action.ConversationHighlight:new("Could you tell me more about the Crondis's influence over the desert?"),
    },
  },
  {
    text = "Return to Senliten.",
    actions = { Action.ConversationHighlight:new("Talk about Crocodile Tears.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Right click Leela to return to the surface." },
  {
    text = "Enter the temple again but use the explore option.",
    actions = { Action.ConversationHighlight:new("Explore the temple.") },
  },
  { text = "Head north and click the ship to get the dowsing rod (Pick up relic Massive stone boat)." },
  {
    text = "Click the dowsing rod and follow the arrow to the following locations in the desert, until your character starts talking, before moving to the next location. (Note: Some players report that closing the arrow interface, using a magic carpet, or teleporting might cause bugs. If this happens, you may need to destroy the rod and do it all over again. It is possible that this is now fixed.)<ul><li>The oasis just north-west of the temple.</li><li>The oasis by the Camel Warriors, east of Sophanem.</li><li>The oasis near the Bedabin Camp.</li></ul>",
  },
  {
    text = "Return to Senliten.",
    actions = { Action.ConversationHighlight:new("Talk about Crocodile Tears.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Head to the waterfall north of the Dominion Tower and climb it." },
  { text = "Walk north through both sets of double doors" },
  {
    text = "Talk to the spirits (Tirrie, Nirrie, or Hallak) in the north part of the cave.",
    actions = { Action.ConversationHighlight:new("I need your help to charge the dowsing rod.") },
    postconditions = { Condition.ConversationText:new(" Alas, Crondis is not silent, we") },
  },
  {
    text = "Enter the temple again but use the explore option.",
    title = "Dousing for Crondis",
    neededItems = { ["Rope"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Explore the temple.") },
  },
  { text = "Head north and click the ship to get the dowsing rod (Pick up relic Massive stone boat)." },
  {
    text = "Click the dowsing rod and follow the arrow to the following locations in the desert, until your character starts talking, before moving to the next location. (Note: Some players report that closing the arrow interface, using a magic carpet, or teleporting might cause bugs. If this happens, you may need to destroy the rod and do it all over again. It is possible that this is now fixed.)<ul><li>The oasis just north-west of the temple.</li><li>The oasis by the Camel Warriors, east of Sophanem.</li><li>The oasis near the Bedabin Camp.</li></ul>",
  },
  {
    text = "Return to Senliten.",
    actions = { Action.ConversationHighlight:new("Talk about Crocodile Tears.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Head to the waterfall north of the Dominion Tower and climb it." },
  { text = "Walk north through both sets of double doors" },
  {
    text = "Talk to the spirits (Tirrie, Nirrie, or Hallak) in the north part of the cave.",
    actions = { Action.ConversationHighlight:new("I need your help to charge the dowsing rod.") },
    postconditions = { Condition.ConversationText:new(" Alas, Crondis is not silent, we") },
  },
  {
    text = "Talk to Portmaster Kags in the Menaphos Port district on the southwest pier by the house portal.",
    title = "Dealing with Crondis",
    neededItems = { ["Dowsing rod"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Transport to Crondis's pyramid.") },
    postconditions = { Condition.ConversationText:new("The player appears next to Crondis's pyramid.") },
  },
  { text = "Enter Crondis's pyramid." },
  { text = "Talk to the High Priest of Crondis." },
  {
    text = "You must wait until after Crondis asks for each item before gathering it or the requirement will not be satisfied.",
  },
  {
    text = "Catch a raw beltfish either on the northwest or southeast pier of the Menaphos Port district or in the VIP skilling area.<ul><li>If using Bait and Switch first catch a beltfish, then obtain it via trading or bank raw.</li></ul>",
  },
  {
    text = "Return to Crondis",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Cut two acadia logs at the acadia trees in the Menaphos Imperial district." },
  {
    text = "Return to Crondis.",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Collect four pyramid tops from the Agility Pyramid." },
  {
    text = "Return to Crondis.",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Catch eight plover birds in southern Sophanem. Any sort of double drop will not count for this. It must be 8 catches, not just 8 plover birds.<ul><li>Sophanem Slayer Dungeon teleport and run south is the fastest way to get there.</li></ul>",
  },
  {
    text = "Return to Crondis.",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Without teleporting after buying, purchase at least 20 (preferably 24) croc ices from Rokuh in Nardah near the fountain to ensure you still have 16 left to turn in (because one croc ice is eaten automatically every 15 seconds, and teleporting with them turns them into chocolatey goo).<ul><li>Run west and Nardah magic carpet to Menaphos.</li><li>Use the Merchant District Shifting Tombs to get to the Port District and return to Crondis' pyramid.</li><li>They do not need to all be collected in one trip.</li></ul>",
  },
  {
    text = "You must wait until after Crondis asks for each item before gathering it or the requirement will not be satisfied.",
    title = "Crondis Fetch Quests",
    neededItems = { ["Fishing bait"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Catch a raw beltfish either on the northwest or southeast pier of the Menaphos Port district or in the VIP skilling area.<ul><li>If using Bait and Switch first catch a beltfish, then obtain it via trading or bank raw.</li></ul>",
  },
  {
    text = "Return to Crondis",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Cut two acadia logs at the acadia trees in the Menaphos Imperial district." },
  {
    text = "Return to Crondis.",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Collect four pyramid tops from the Agility Pyramid." },
  {
    text = "Return to Crondis.",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Catch eight plover birds in southern Sophanem. Any sort of double drop will not count for this. It must be 8 catches, not just 8 plover birds.<ul><li>Sophanem Slayer Dungeon teleport and run south is the fastest way to get there.</li></ul>",
  },
  {
    text = "Return to Crondis.",
    actions = { Action.ConversationHighlight:new("Yes, of course.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Without teleporting after buying, purchase at least 20 (preferably 24) croc ices from Rokuh in Nardah near the fountain to ensure you still have 16 left to turn in (because one croc ice is eaten automatically every 15 seconds, and teleporting with them turns them into chocolatey goo).<ul><li>Run west and Nardah magic carpet to Menaphos.</li><li>Use the Merchant District Shifting Tombs to get to the Port District and return to Crondis' pyramid.</li><li>They do not need to all be collected in one trip.</li></ul>",
  },
  {
    text = "Talk to Crondis. Use any chat option except 1 for all dialogue boxes.<ul><li>If not already equipped for combat, leave the conversation just before the player yells 'THAT'S ENOUGH!' to prepare for the fight.</li><li>Talk to Crondis again to continue forward to the fight when you return.</li></ul>",
    title = "Boss Fight",
    neededItems = { ["Ukunduka"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("THAT'S ENOUGH!") },
    postconditions = { Condition.ConversationText:new(" !") },
  },
  {
    text = "Kill Ukunduka (stay out of melee distance or use Saradomin brews). There is a safe spot on the stairs leading up to Crondis.",
  },
  { text = "Talk to the High Priest of Crondis." },
  { text = "Return to Senliten." },
  {
    text = "After the cutscene, speak with Senliten.",
    actions = { Action.ConversationHighlight:new("Talk about Crocodile Tears.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Crocodile Tears",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.shortmedium,
  releaseDate = 1496620800,
  prereqQuests = {
    "The Jack of Spades",
    "Spirits of the Elid",
    "Missing My Mummy",
    "Dealing with Scabaras",
    "Leela",
    "Senliten",
    "Missing My Mummy",
  },
})
