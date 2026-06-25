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
    text = "Start the quest by speaking to Sanfew, who lives  on the east side of Taverley, south-west of the summoning shop.",
    title = "Getting started",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Ask general questions."),
      Action.ConversationHighlight:new("Have you any more work for me, to help reclaim the circle?"),
    },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Have you any more work for me?") },
  },
  {
    text = "[Accept Quest]<ul><li>If Zogre Flesh Eaters is not complete, use the following option:</li><li>[Accept Quest]</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Equip your climbing boots and make your way to the Troll Stronghold via either the main entrance or the secret entrance.<ul><li>From the main entrance, go south and then climb down the stairs into the Troll kitchen.</li><li>If using the secret entrance, go to the ambush commander and climb the nearby wall, then head west.</li><li>Once inside, go all the way north and climb up the stairs.</li><li>Go south into the Troll kitchen.</li><li>Once inside, go all the way north and climb up the stairs.</li><li>Go south into the Troll kitchen.</li></ul>",
    title = "The troll cook",
  },
  { text = "Speak to Burntmeat." },
  {
    text = "Equip your climbing boots and make your way to the Troll Stronghold via either the main entrance or the secret entrance.<ul><li>From the main entrance, go south and then climb down the stairs into the Troll kitchen.</li><li>If using the secret entrance, go to the ambush commander and climb the nearby wall, then head west.</li><li>Once inside, go all the way north and climb up the stairs.</li><li>Go south into the Troll kitchen.</li><li>Once inside, go all the way north and climb up the stairs.</li><li>Go south into the Troll kitchen.</li></ul>",
    title = "The troll cook",
    neededItems = { ["Climbing boots"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Speak to Burntmeat." },
  {
    text = "Walk out of the stronghold's main entrance and head east to the top of the mountain (Eadgar's cave). Located on top of the mountain with the thrower trolls, the world map marks this as Trollheim.",
    title = "Tricking the trolls",
    neededItems = {
      ["Vodka"] = { quantity = 1 },
      ["Pineapple chunks"] = { quantity = 1 },
      ["Wheat"] = { quantity = 1 },
      ["Raw chicken"] = { quantity = 1 },
      ["Logs"] = { quantity = 1 },
      ["Ranarr potion (unfinished)"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  { text = "Enter the cave." },
  {
    text = "Talk to Eadgar.",
    actions = {
      Action.ConversationHighlight:new("What do you have to offer?"),
      Action.ConversationHighlight:new("I'd like some mountain goat stew, please."),
    },
  },
  {
    text = "Talk to Parroty Pete at the Ardougne Zoo. He is located at the northern end by the wolves.",
    actions = { Action.ConversationHighlight:new("What do you feed them?") },
    postconditions = {
      Condition.ConversationText:new(
        " Well, fruit and wheat mostly. I try to give them a balanced diet, but their favourite treat is pineapple chunks."
      ),
    },
  },
  {
    text = "Talk to him again.",
    actions = { Action.ConversationHighlight:new("When did you add it?") },
    postconditions = {
      Condition.ConversationText:new(
        " Just recently. It would have been sooner, but some wretch thought it would be amusing to replace their drinking water with vodka. The vet had to nurse them back to health for weeks!"
      ),
    },
  },
  { text = "Use the pineapple chunks on the vodka to obtain alco-chunks." },
  { text = "Use the alco-chunks on the Aviary Hatch on the west side of the parrot cage for a drunk parrot." },
  { text = "Return to Eadgar.", actions = { Action.ConversationHighlight:new("No thanks, Eadgar.") } },
  { text = "Go back to the stronghold and climb down the northern Stone Staircase." },
  { text = "Unlock the Prison Door to the east and climb down the staircase." },
  { text = "Use the drunk parrot on the rack north of the prison cells." },
  {
    text = "Talk to Tegid in Taverley for a dirty robe. He is located south of Sanfew, washing laundry in the lake.",
    actions = { Action.ConversationHighlight:new("Sanfew won't be happy...") },
    postconditions = {
      Condition.ConversationText:new(
        " What? Oh well, if it's a matter of that much importance, I suppose you can borrow one..."
      ),
    },
  },
  {
    text = "Talk to Eadgar with the wheat, raw chickens, logs and the dirty robe.",
    actions = { Action.ConversationHighlight:new("No thanks, Eadgar.") },
  },
  { text = "Exit the cave and find a grassy area and pick the thistle.", title = "Truth serum" },
  {
    text = "Use the troll thistle on a fire.<ul><li>If you didn't bring logs, there are two fires just west in the encampment.</li></ul>",
  },
  { text = "Grind the dried thistle." },
  { text = "Use the ground thistle on ranarr potion (unfinished) to obtain a troll potion." },
  { text = "Talk to Eadgar to give him the potion." },
  { text = "Go back to the prison in Troll Stronghold." },
  { text = "Search the rack to retrieve the drunk parrot." },
  {
    text = "Talk to Eadgar to obtain a fake man.<ul><li>Speak to him again if you don't receive the fake man the first time.</li></ul>",
    actions = { Action.ConversationHighlight:new("No thanks, Eadgar.") },
  },
  {
    text = "Talk to Burntmeat back in the kitchen of the stronghold.",
    title = "Obtaining goutweed",
    actions = { Action.ConversationHighlight:new("I'll be going now.") },
    postconditions = { Condition.ConversationText:new(" Bye, bye.") },
  },
  { text = "Open and right-click search the Kitchen Drawers along the south wall for the storeroom key." },
  { text = "Climb-down the stone staircase directly north of the kitchen drawers to enter the storeroom." },
  { text = "Open the Storeroom Door with the key." },
  {
    text = "Obtain goutweed:<ul><li>At the next door, wait until the guard patrolling the northernmost lane goes south.</li><li>Open the door and run west, then surge directly south to search the Goutweed Crate.</li><li>If done correctly, you will surge through the western most guards and be able to immediately obtain a goutweed.</li><li>Alternatively, use safe spots (U-shaped areas with boxes on three sides) to pass the guards.</li><li>Run to the first safe spot, the middle safe spot in the third row of boxes from the entrance, after the trolls patrolling the first row and the eastern second row just pass you.</li><li>Wait for the troll which travels north on the western side to make the turn to the east, then run to the safe spot on the southern side of the third row.</li><li>Wait until the troll guard approaching you from the south has turned the west, then run to the crate with the goutweed. Make sure that you are standing to the side of the crate or you will get caught.</li></ul>",
  },
  { text = "Return to Taverley and talk to Sanfew with the goutweed." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Eadgar's Ruse",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1096934400,
  prereqQuests = { "Druidic Ritual", "Troll Stronghold", "Eadgar" },
})
