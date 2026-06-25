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
    text = "Speak with Commodore Tyr at the Void Knights' Outpost.",
    title = "Starting off",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = {
      Action.ConversationHighlight:new("Why do you need me?"),
      Action.ConversationHighlight:new("Wait, you're sending Jessika?"),
      Action.ConversationHighlight:new("Where should I start?"),
      Action.ConversationHighlight:new("I'm on my way."),
    },
  },
  { text = "Speak with Captain Korasi." },
  {
    text = "Head to the Port Sarim docks and speak to Korasi.",
    title = "Pest-y business",
    actions = { Action.ConversationHighlight:new("On my way.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Right-click talk to Captain Tobias (in the blue sailor outfit), just north of Korasi.",
    actions = { Action.ConversationHighlight:new("No, thank you.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Speak with Gerrant in the fishing shop.<ul><li>Or, if Heroes' Quest is complete</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I'm tracking an escaped animal."),
      Action.ConversationHighlight:new("I'm tracking an escaped animal."),
    },
    postconditions = { Condition.ConversationText:new(" Thanks for your help.") },
  },
  { text = "Follow the path pictured right, searching the bushes, plants, and stones." },
  { text = "Finally check the mound." },
  { text = "Return to Captain Korasi at the docks.", actions = { Action.ConversationHighlight:new("See you there.") } },
  { text = "Talk to the Bartender in Port Sarim's pub.", actions = { Action.ConversationHighlight:new("Bye then.") } },
  { text = "Search the piles of junk on the floor at the north-east corner for a key." },
  { text = "Go down the trapdoor in the north-west corner of the pub." },
  { text = "Inspect the suspicious-looking wall on the north side wall." },
  { text = "Open the door to the room south." },
  {
    text = "Complete the puzzle (follow the steps below, or alternatively, use the image on the right to position the barrels in order to push out the barrow along the red-line path):",
  },
  { text = "If the ordering is confusing, refer to the image on the wiki page.", title = "The Barrel Puzzle" },
  { text = "Kick the barrel south of you twice (Barrel #1)" },
  { text = "Walk 1 south, then 1 east, kick the barrel south of you (Barrel #2)" },
  { text = "Walk 1 west, kick the barrel south of you (Barrel #3)" },
  { text = "Walk 1 south-west, kick the barrel west of you (Barrel #8)" },
  { text = "Walk 1 north-west, kick the barrel east of you twice (Barrel #1)" },
  { text = "Walk 1 north-west, kick the barrel north of you (Barrel #7)" },
  { text = "Walk 1 north, kick the barrel west of you twice (Barrel #12)" },
  { text = "Walk 1 west, kick the barrel south of you thrice (Barrel #13)" },
  { text = "Walk 1 west, kick the barrel south of you (Barrel #14)" },
  {
    text = "Run around to the other side of the crates (3 east, 1 south), kick the barrel west of you twice (Barrel #8)",
  },
  { text = "Kick the south-westernmost barrel south (Barrel #15)" },
  { text = "Kick the southernmost center barrel to the east twice (Barrel #10)" },
  { text = "Walk 1 east and 2 north, kick the barrel north twice (Barrel #4)" },
  { text = "Walk 1 north and 1 north-west and kick the barrel east (Barrel #4)" },
  {
    text = "Walk to the east side of the southern middle barrel, kick it west, then kick it north twice from the south side. (Barrel #9)",
  },
  { text = "After completing the puzzle, leave the room." },
  { text = "Speak with the Bartender again.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  {
    text = "Speak to Korasi on the dock.",
    title = "Getting hotter...",
    actions = { Action.ConversationHighlight:new("To Musa Point, then.") },
  },
  { text = "Travel to Musa Point by paying Captain Tobias, just north of Korasi." },
  {
    text = "Speak with Zembo in the pub.",
    actions = { Action.ConversationHighlight:new("Seen anything odd lately?") },
  },
  { text = "Speak with Shopkeeper Kofi in the general store directly west of the pub." },
  { text = "Take-from the table in the south-west corner of the shop." },
  { text = "Use the chisel on the plank to make a jointed plank and a jointed log." },
  { text = "Use the jointed plank on the jointed log to make a joist." },
  { text = "Use the joist on the Shop wall on the north end of the shop." },
  { text = "Talk to Kofi for a cutscene." },
  { text = "Follow the trail just like the one at Port Sarim." },
  { text = "After Korasi and Jessika appear, search the mound again to get wood shards." },
  { text = "Finish dialogue with them.", actions = { Action.ConversationHighlight:new("Onwards, then.") } },
  { text = "With at least 16 free inventory spaces, head to Rimmington." },
  {
    text = "Ask Rommik in the crafting shop about the waxwood.",
    actions = { Action.ConversationHighlight:new("I need to ask you about waxwood.") },
  },
  {
    text = "Go to westernmost building in Rimmington (Chemists's House, long with a green Taskmaster asterisk in it).",
    title = "Gooey analysis",
  },
  {
    text = "Inside, on the Eastern wall, talk to Korasi .",
    actions = { Action.ConversationHighlight:new("I'll see about this experiment, then.") },
  },
  {
    text = "Talk to the Chemist.",
    actions = {
      Action.ConversationHighlight:new("Ask about A Void Dance."),
      Action.ConversationHighlight:new("Analyse the sample."),
    },
  },
  {
    text = "Analyse the sample by keeping the power and heat at an OK level and keeping the current ingredient as the desired one.",
    actions = { Action.ConversationHighlight:new("Analyse the sample.") },
  },
  {
    text = "When you start to analyse the sample, the in-game table in the top-right shows the combination for how to make each sample. Put the correct secondary sample in the hopper to get the desired sample (top left table).",
  },
  { text = "If you are struggling refer to the full guide for more details." },
  {
    text = "Accept Jessika's offer to teleport you to Falador Park.",
    title = "Predicament",
    actions = { Action.ConversationHighlight:new("Go with Jessika.") },
  },
  { text = "Talk to Korasi or Jessika." },
  {
    text = "South of the east Falador bank, talk to Ali Tist.",
    actions = { Action.ConversationHighlight:new("Yes, I would.") },
  },
  { text = "Attempt-puzzle with the puzzle box." },
  { text = "Return to Korasi or Jessika." },
  {
    text = "Propose a solution to open the box.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  { text = "Talk to Ali Tist and then to Korasi or Jessika." },
  {
    text = "On the 2nd floor[UK]3rd floor[US] of the western tower of White Knights' Castle, talk to Sir Amik Varze.",
    actions = { Action.ConversationHighlight:new("Speak about the A Void Dance quest.") },
  },
  {
    text = "With the search warrant he gives, go back and talk to Ali Tist.",
    actions = { Action.ConversationHighlight:new("I want to examine your stock.") },
  },
  {
    text = "Speak with Korasi or Jessika to get a commorb.",
    title = "Whodunnit?",
    neededItems = { ["Onion"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "You can right click the clue scroll to dig." },
  { text = "Try to enter the hatchway and then again to enter the hideout.", title = "Quick thinking" },
  { text = "Talk to Korasi or Jessika for a second cutscene." },
  { text = "Talk to Korasi or Jessika again for a void drone 'bug'." },
  { text = "Investigate the low box near the door without being caught by the workers to place the drone." },
  { text = "Wait until some workers go by, a cutscene will play." },
  { text = "Investigate the low box to retrieve the drone." },
  { text = "Talk to Jessika." },
  { text = "Pick-pocket a worker walking south from the guarded door." },
  { text = "Talk to Korasi." },
  { text = "Knock on the guarded door.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Talk to Korasi." },
  { text = "Enter the next room then right-click murder any indentured worker." },
  { text = "The enemies in next room can drain your prayer points to heal themselves.", title = "Finishing up" },
  { text = "Enter the next door. Kill the Black Knight Guardian on your side." },
  {
    text = "Talk to the pest cage (void leech).",
    actions = { Action.ConversationHighlight:new("Enough of this."), Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Accept the teleport to the Void Knights' Outpost.",
    actions = { Action.ConversationHighlight:new("Go with Korasi.") },
  },
  {
    text = "Talk to Commodore Tyr for a cutscene.<ul><li>If you cancel the cutscene, you may need to lobby.</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "A Void Dance",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1281398400,
  prereqQuests = { "Quiet Before the Swarm" },
})
