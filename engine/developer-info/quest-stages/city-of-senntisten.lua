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
    text = "Talk to Ariane at the entrance to the Heart of Gielinor and enter the Elder Halls.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Talk to Charos. Go through all chat options about what to do with the eggs.",
    actions = {
      Action.ConversationHighlight:new("Could we destroy them?"),
      Action.ConversationHighlight:new("Could we drain them?"),
      Action.ConversationHighlight:new("Could we hide them?"),
      Action.ConversationHighlight:new("Could we fight the elder gods?"),
      Action.ConversationHighlight:new("Could we move them?"),
      Action.ConversationHighlight:new("I'm stumped."),
    },
  },
  {
    text = "Talk to Azzanadra, after which a cutscene will play.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to Charos and Ariane.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Go to the Archaeology Guild, upstairs onto the balcony. Talk to Dr Nabanik.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Teleport to Port Sarim lodestone (or use the Clan vexillum to teleport to the Falador Clan Camp) and go to Armadyl's Tower. Climb to the top and talk to Armadyl.",
  },
  {
    text = "Go to Burthorpe Castle and climb up the stone staircase.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  { text = "Talk to Icthlarin at the southeast of the table." },
  { text = "Talk to Seren at the west of the table." },
  { text = "Talk to Moia at the south of the table.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Zamorak.", actions = { Action.ConversationHighlight:new("Er, help?") } },
  {
    text = "Go to the throne room on the 2nd floor[UK]3rd floor[US] (east side) of the White Knights' Castle and talk to Saradomin.",
    actions = { Action.ConversationHighlight:new("City of Senntisten") },
  },
  { text = "Return to Dr Nabanik on the balcony of the main building at the Archaeology Guild." },
  {
    text = "Enter the City of Senntisten through the ancient door to the north of the Archaeology Guild.",
    title = "Senntisten",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Excavate the forum entrance. (Use your grace of the elves charges to move the materials to the material storage container if you have it. Porters also work.)",
  },
  { text = "Interact with the pedestal to obtain Pontifex Maximus figurine (damaged)." },
  { text = "Exit via the pulley.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Intern Jimmy at the entrance to get materials." },
  {
    text = "Restore the Pontifex Maximus figurine at an archaeologist's workbench. (The one south by the guild is closest.)",
  },
  { text = "Return to Azzanadra, and interact with the pedestal to place the artefact upon it." },
  {
    text = "Follow Azzanadra to the cathedral, and speak with him. (You may go directly to the cathedral and wait for him.)<ul><li>Alternatively, you can turn off run energy and click on him once, following him all the way to the cathedral.</li></ul>",
    actions = { Action.ConversationHighlight:new("No, let's bring in the eggs.") },
  },
  {
    text = "Search the bookcases, chests, crates, and pews in the cathedral, including the upstairs balconies, for two light globes (see map below).",
  },
  { text = "Talk to Azzanadra." },
  { text = "Pass through the western cathedral door." },
  {
    text = "Within the city, put light globes on the light pedestals to illuminate the immediate area. When outside of an illuminated area, you will receive stacks of Shadows of the Empire debuff. The more stacks you have, the more damage you are dealt. Standing in an illuminated area will remove stacks of the debuff.  This includes stepping into houses with lights. The ward only dims the light from the globes.",
    title = "Wards",
  },
  {
    text = "Light globes will persist when teleporting out and additional light globes are spread through the west district of the city. You don't need to obtain all light globes to complete the quest, so it may be more beneficial to juggle them as you explore new areas.",
  },
  { text = "See the map for ward and objective locations." },
  {
    text = "Blood ward<ul><li>Head to the blood bank (north-western building in southern section).</li><li>Inspect the blood bottle by the interior door leading to the blood ward.</li><li>Search the old desk.</li><li>Search the pile of books on the table near the entrance.</li><li>Head north to the marketplace and enter the southern house south of the colosseum gateway.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Search the ransacked desk near the chimney for the Blood Bank Ledger.</li><li>Return to the blood bank.</li><li>Search the shelves for bottles of blood:</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>Be prepared for a dangerous boss fight immediately after finishing the puzzle. The Blood Warden will heal from any damage-over-time abilities. Additionally, its special attack will cause it to heal over time.</li><li>Inspect the blood bottle to start the puzzle, and add blood:</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li><li>Kill the Blood Warden</li><li>Take the ward from the pedestal.</li><li>Activate the blood bottle near the door to exit.</li><li>Talk to Azzanadra with the ward.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Ice ward<ul><li>Go down two flights of stairs from the cathedral to the west, then enter to the house immediately to the south.</li><li>Take the ice ward from the pedestal.</li><li>Go back to the cathedral and talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Shadow ward<ul><li>Ensure you have enough healing ability with you as you will take a lot of damage while returning the shadow ward. Each open house is to lower said stacks of damage.</li><li>Go to the northern-most building, where the ward is located, and head upstairs.</li><li>Take the journal on the desk to get the Inquisition Profiles.</li><li>Read the Inquisition Profiles and complete the dialogue.</li><li>Open the marked drawers as below. Start from the bottom and work your way up or zoom in for ease.</li><li>Head downstairs and take the ward from the pedestal.</li><li>Return to Azzanadra as fast as possible. Be prepared to take a lot of damage. Using a powerburst of acceleration will make this easier.</li><li>Talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes"), Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Smoke ward<ul><li>The smoke ward can be found in the large building directly south of the ice ward on the map. As you approach an animation with gargoyle sentinels should play.</li><li>Find and smash the stones to reveal the gargoyles sentinels. Smash them manually to complete the final blow, or automatically if relevant perk is available.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li><li>Return to the arsenal and take the ward from the pedestal.</li><li>Talk to Azzanadra with the ward.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "This quest helper version is still a WIP. Refer to the wiki for this puzzle solution.",
    title = "Puzzle Solution",
  },
  { text = "Talk to Azzanadra and complete the dialogue.", title = "Finishing up" },
  {
    text = "Talk to Ariane at the entrance to the Heart of Gielinor and enter the Elder Halls.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Talk to Charos. Go through all chat options about what to do with the eggs.",
    actions = {
      Action.ConversationHighlight:new("Could we destroy them?"),
      Action.ConversationHighlight:new("Could we drain them?"),
      Action.ConversationHighlight:new("Could we hide them?"),
      Action.ConversationHighlight:new("Could we fight the elder gods?"),
      Action.ConversationHighlight:new("Could we move them?"),
      Action.ConversationHighlight:new("I'm stumped."),
    },
  },
  {
    text = "Talk to Azzanadra, after which a cutscene will play.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Talk to Charos and Ariane.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Go to the Archaeology Guild, upstairs onto the balcony. Talk to Dr Nabanik.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Teleport to Port Sarim lodestone (or use the Clan vexillum to teleport to the Falador Clan Camp) and go to Armadyl's Tower. Climb to the top and talk to Armadyl.",
  },
  {
    text = "Go to Burthorpe Castle and climb up the stone staircase.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  { text = "Talk to Icthlarin at the southeast of the table." },
  { text = "Talk to Seren at the west of the table." },
  { text = "Talk to Moia at the south of the table.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Zamorak.", actions = { Action.ConversationHighlight:new("Er, help?") } },
  {
    text = "Go to the throne room on the 2nd floor[UK]3rd floor[US] (east side) of the White Knights' Castle and talk to Saradomin.",
    actions = { Action.ConversationHighlight:new("City of Senntisten") },
  },
  { text = "Return to Dr Nabanik on the balcony of the main building at the Archaeology Guild." },
  {
    text = "Enter the City of Senntisten through the ancient door to the north of the Archaeology Guild.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Excavate the forum entrance. (Use your grace of the elves charges to move the materials to the material storage container if you have it. Porters also work.)",
  },
  { text = "Interact with the pedestal to obtain Pontifex Maximus figurine (damaged)." },
  { text = "Exit via the pulley.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Intern Jimmy at the entrance to get materials." },
  {
    text = "Restore the Pontifex Maximus figurine at an archaeologist's workbench. (The one south by the guild is closest.)",
  },
  { text = "Return to Azzanadra, and interact with the pedestal to place the artefact upon it." },
  {
    text = "Follow Azzanadra to the cathedral, and speak with him. (You may go directly to the cathedral and wait for him.)<ul><li>Alternatively, you can turn off run energy and click on him once, following him all the way to the cathedral.</li></ul>",
    actions = { Action.ConversationHighlight:new("No, let's bring in the eggs.") },
  },
  {
    text = "Search the bookcases, chests, crates, and pews in the cathedral, including the upstairs balconies, for two light globes (see map below).",
  },
  { text = "Talk to Azzanadra." },
  { text = "Pass through the western cathedral door." },
  {
    text = "Within the city, put light globes on the light pedestals to illuminate the immediate area. When outside of an illuminated area, you will receive stacks of Shadows of the Empire debuff. The more stacks you have, the more damage you are dealt. Standing in an illuminated area will remove stacks of the debuff.  This includes stepping into houses with lights. The ward only dims the light from the globes.",
  },
  {
    text = "Light globes will persist when teleporting out and additional light globes are spread through the west district of the city. You don't need to obtain all light globes to complete the quest, so it may be more beneficial to juggle them as you explore new areas.",
  },
  { text = "See the map for ward and objective locations." },
  {
    text = "Blood ward<ul><li>Head to the blood bank (north-western building in southern section).</li><li>Inspect the blood bottle by the interior door leading to the blood ward.</li><li>Search the old desk.</li><li>Search the pile of books on the table near the entrance.</li><li>Head north to the marketplace and enter the southern house south of the colosseum gateway.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Search the ransacked desk near the chimney for the Blood Bank Ledger.</li><li>Return to the blood bank.</li><li>Search the shelves for bottles of blood:</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>Be prepared for a dangerous boss fight immediately after finishing the puzzle. The Blood Warden will heal from any damage-over-time abilities. Additionally, its special attack will cause it to heal over time.</li><li>Inspect the blood bottle to start the puzzle, and add blood:</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li><li>Kill the Blood Warden</li><li>Take the ward from the pedestal.</li><li>Activate the blood bottle near the door to exit.</li><li>Talk to Azzanadra with the ward.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Ice ward<ul><li>Go down two flights of stairs from the cathedral to the west, then enter to the house immediately to the south.</li><li>Take the ice ward from the pedestal.</li><li>Go back to the cathedral and talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Shadow ward<ul><li>Ensure you have enough healing ability with you as you will take a lot of damage while returning the shadow ward. Each open house is to lower said stacks of damage.</li><li>Go to the northern-most building, where the ward is located, and head upstairs.</li><li>Take the journal on the desk to get the Inquisition Profiles.</li><li>Read the Inquisition Profiles and complete the dialogue.</li><li>Open the marked drawers as below. Start from the bottom and work your way up or zoom in for ease.</li><li>Head downstairs and take the ward from the pedestal.</li><li>Return to Azzanadra as fast as possible. Be prepared to take a lot of damage. Using a powerburst of acceleration will make this easier.</li><li>Talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes"), Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Smoke ward<ul><li>The smoke ward can be found in the large building directly south of the ice ward on the map. As you approach an animation with gargoyle sentinels should play.</li><li>Find and smash the stones to reveal the gargoyles sentinels. Smash them manually to complete the final blow, or automatically if relevant perk is available.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li><li>Return to the arsenal and take the ward from the pedestal.</li><li>Talk to Azzanadra with the ward.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Talk to Azzanadra and complete the dialogue." },
  {
    text = "Enter the City of Senntisten through the ancient door to the north of the Archaeology Guild.",
    title = "Senntisten",
    neededItems = {
      ["Mattocks"] = { quantity = 1 },
      ["Tool belt"] = { quantity = 1 },
      ["Dragonstone"] = {
        quantity = 1,
      },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Excavate the forum entrance. (Use your grace of the elves charges to move the materials to the material storage container if you have it. Porters also work.)",
  },
  { text = "Interact with the pedestal to obtain Pontifex Maximus figurine (damaged)." },
  { text = "Exit via the pulley.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Intern Jimmy at the entrance to get materials." },
  {
    text = "Restore the Pontifex Maximus figurine at an archaeologist's workbench. (The one south by the guild is closest.)",
  },
  { text = "Return to Azzanadra, and interact with the pedestal to place the artefact upon it." },
  {
    text = "Follow Azzanadra to the cathedral, and speak with him. (You may go directly to the cathedral and wait for him.)<ul><li>Alternatively, you can turn off run energy and click on him once, following him all the way to the cathedral.</li></ul>",
    actions = { Action.ConversationHighlight:new("No, let's bring in the eggs.") },
  },
  {
    text = "Search the bookcases, chests, crates, and pews in the cathedral, including the upstairs balconies, for two light globes (see map below).",
  },
  { text = "Talk to Azzanadra." },
  { text = "Pass through the western cathedral door." },
  {
    text = "Within the city, put light globes on the light pedestals to illuminate the immediate area. When outside of an illuminated area, you will receive stacks of Shadows of the Empire debuff. The more stacks you have, the more damage you are dealt. Standing in an illuminated area will remove stacks of the debuff.  This includes stepping into houses with lights. The ward only dims the light from the globes.",
  },
  {
    text = "Light globes will persist when teleporting out and additional light globes are spread through the west district of the city. You don't need to obtain all light globes to complete the quest, so it may be more beneficial to juggle them as you explore new areas.",
  },
  { text = "See the map for ward and objective locations." },
  {
    text = "Blood ward<ul><li>Head to the blood bank (north-western building in southern section).</li><li>Inspect the blood bottle by the interior door leading to the blood ward.</li><li>Search the old desk.</li><li>Search the pile of books on the table near the entrance.</li><li>Head north to the marketplace and enter the southern house south of the colosseum gateway.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Search the ransacked desk near the chimney for the Blood Bank Ledger.</li><li>Return to the blood bank.</li><li>Search the shelves for bottles of blood:</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>Be prepared for a dangerous boss fight immediately after finishing the puzzle. The Blood Warden will heal from any damage-over-time abilities. Additionally, its special attack will cause it to heal over time.</li><li>Inspect the blood bottle to start the puzzle, and add blood:</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li><li>Kill the Blood Warden</li><li>Take the ward from the pedestal.</li><li>Activate the blood bottle near the door to exit.</li><li>Talk to Azzanadra with the ward.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Ice ward<ul><li>Go down two flights of stairs from the cathedral to the west, then enter to the house immediately to the south.</li><li>Take the ice ward from the pedestal.</li><li>Go back to the cathedral and talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Shadow ward<ul><li>Ensure you have enough healing ability with you as you will take a lot of damage while returning the shadow ward. Each open house is to lower said stacks of damage.</li><li>Go to the northern-most building, where the ward is located, and head upstairs.</li><li>Take the journal on the desk to get the Inquisition Profiles.</li><li>Read the Inquisition Profiles and complete the dialogue.</li><li>Open the marked drawers as below. Start from the bottom and work your way up or zoom in for ease.</li><li>Head downstairs and take the ward from the pedestal.</li><li>Return to Azzanadra as fast as possible. Be prepared to take a lot of damage. Using a powerburst of acceleration will make this easier.</li><li>Talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes"), Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Smoke ward<ul><li>The smoke ward can be found in the large building directly south of the ice ward on the map. As you approach an animation with gargoyle sentinels should play.</li><li>Find and smash the stones to reveal the gargoyles sentinels. Smash them manually to complete the final blow, or automatically if relevant perk is available.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li><li>Return to the arsenal and take the ward from the pedestal.</li><li>Talk to Azzanadra with the ward.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Talk to Azzanadra and complete the dialogue." },
  {
    text = "Within the city, put light globes on the light pedestals to illuminate the immediate area. When outside of an illuminated area, you will receive stacks of Shadows of the Empire debuff. The more stacks you have, the more damage you are dealt. Standing in an illuminated area will remove stacks of the debuff.  This includes stepping into houses with lights. The ward only dims the light from the globes.",
  },
  {
    text = "Light globes will persist when teleporting out and additional light globes are spread through the west district of the city. You don't need to obtain all light globes to complete the quest, so it may be more beneficial to juggle them as you explore new areas.",
  },
  { text = "See the map for ward and objective locations." },
  {
    text = "Blood ward<ul><li>Head to the blood bank (north-western building in southern section).</li><li>Inspect the blood bottle by the interior door leading to the blood ward.</li><li>Search the old desk.</li><li>Search the pile of books on the table near the entrance.</li><li>Head north to the marketplace and enter the southern house south of the colosseum gateway.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Search the ransacked desk near the chimney for the Blood Bank Ledger.</li><li>Return to the blood bank.</li><li>Search the shelves for bottles of blood:</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>Be prepared for a dangerous boss fight immediately after finishing the puzzle. The Blood Warden will heal from any damage-over-time abilities. Additionally, its special attack will cause it to heal over time.</li><li>Inspect the blood bottle to start the puzzle, and add blood:</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li><li>Kill the Blood Warden</li><li>Take the ward from the pedestal.</li><li>Activate the blood bottle near the door to exit.</li><li>Talk to Azzanadra with the ward.</li><li>The correct house is marked with Bloodied Note on the map.</li><li>Bottle of blood (human)</li><li>Bottle of blood (unicorn)</li><li>Bottle of blood (dragon)</li><li>Bottle of blood (aviansie)</li><li>2/8 human blood</li><li>3/8 unicorn blood</li><li>1/8 dragon blood</li><li>2/8 aviansie blood</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Ice ward<ul><li>Go down two flights of stairs from the cathedral to the west, then enter to the house immediately to the south.</li><li>Take the ice ward from the pedestal.</li><li>Go back to the cathedral and talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Shadow ward<ul><li>Ensure you have enough healing ability with you as you will take a lot of damage while returning the shadow ward. Each open house is to lower said stacks of damage.</li><li>Go to the northern-most building, where the ward is located, and head upstairs.</li><li>Take the journal on the desk to get the Inquisition Profiles.</li><li>Read the Inquisition Profiles and complete the dialogue.</li><li>Open the marked drawers as below. Start from the bottom and work your way up or zoom in for ease.</li><li>Head downstairs and take the ward from the pedestal.</li><li>Return to Azzanadra as fast as possible. Be prepared to take a lot of damage. Using a powerburst of acceleration will make this easier.</li><li>Talk to Azzanadra with the ward.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes"), Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Smoke ward<ul><li>The smoke ward can be found in the large building directly south of the ice ward on the map. As you approach an animation with gargoyle sentinels should play.</li><li>Find and smash the stones to reveal the gargoyles sentinels. Smash them manually to complete the final blow, or automatically if relevant perk is available.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li><li>Return to the arsenal and take the ward from the pedestal.</li><li>Talk to Azzanadra with the ward.</li><li>Directly outside the arsenal, the building with the ward.</li><li>Further west and south of the first, in a clearing in front of a house door.</li><li>To the north in the market, the square containing the market stalls.</li><li>West of the market, slightly south of the colosseum gateway.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  { text = "Talk to Azzanadra and complete the dialogue." },
  { text = "Talk to Azzanadra and complete the dialogue." },
  { text = "Talk to Azzanadra and complete the dialogue." },
}

return Quest:new({
  name = "City of Senntisten",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1624233600,
  prereqQuests = { "Battle of the Monolith", "Desert Treasure", "The Temple at Senntisten" },
})
