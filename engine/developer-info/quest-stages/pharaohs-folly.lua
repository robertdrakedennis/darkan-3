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
    text = "Talk with Leela outside of the northern gates of Menaphos.",
    title = "The folly of man",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("We should get it over with. (Continue quest)") },
  },
  {
    text = "Empty your backpack, worn equipment and dismiss any followers and then head to the throne room inside the Golden Palace.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new(" I can fill you in if you need a reminder of what to do here.") },
  },
  {
    text = "Talk to anyone inside to begin the conversation.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Attempt to exit the shifting tomb via the rope.", title = "A shifting perspective" },
  { text = "Search the arrow shafts to receive a mysterious message and a spare arrow shaft." },
  { text = "Read the mysterious message." },
  {
    text = "Speak to the four cats (indicated by yellow dots on your minimap) to receive a curved bone:<ul><li>Bestopet</li><li>Katarina</li><li>Shebit</li><li>Takhuit</li></ul>",
  },
  { text = "Mine crystalline corruption to obtain crystal shards." },
  { text = "Search treasure chests to obtain feathers." },
  { text = "Smash urns to obtain a gut string." },
  {
    text = "Search sarcophagi to obtain mouldy bandages. To do this, click on the panels the energy bounces off in the order of which it started.",
  },
  { text = "Use the gut string on the curved bone to create a fragile bow." },
  { text = "Use the feathers on the spare arrow shaft to create a feathered spare arrow shaft." },
  { text = "Use the crystal shards on the feathered spare arrow shaft to make a scrappy arrow." },
  { text = "Stand next to the arrow shafts where the exit rope was, then fire arrow with the bow in your backpack." },
  { text = "Exit the rope." },
  { text = "Talk to Ozan." },
  {
    text = "Talk to Commander Akhomet outside the northern gates of Sophanem.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Just tell me what to do. (continue quest)"),
      Action.ConversationHighlight:new("I'll crack on (Continue quest)"),
    },
  },
  {
    text = "Talk to the following people:<ul><li>Maisa next to the High priest in Sophanem.</li><li>Grand Vizier Hassan in the south-west building of the Menaphos merchant district.</li><li>Lydia in Pollnivneach, south-east of Sumona.</li><li>Emir Ali Mirza in the Al Kharid palace.</li></ul>",
    title = "The keys to Menaphos",
    actions = { Action.ConversationHighlight:new("Yes."), Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new(" I can fill you in if you need a reminder of what to do here.") },
  },
  {
    text = "Talk to Ozan back in the Shifting Tombs lobby.",
    title = "Jailbreaking Leela",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new(" I can fill you in if you need a reminder of what to do here.") },
  },
  {
    text = "Exit the tomb west to the Imperial district.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new(" I can fill you in if you need a reminder of what to do here.") },
  },
  {
    text = "Sneak past the guards and enter the library.<ul><li>Head north and around the edge of the pool towards the VIP skilling area.</li><li>Go west and go behind the two guards near the Golden Palace.</li><li>Head south to the library, going behind the stationary guards. Be careful of the roaming guards who can still catch you.</li></ul>",
  },
  { text = "Talk to Ozan for a cutscene, and finish the dialogue to get the signal torch." },
  { text = "Go south and make your way to the staircase on the south side, avoiding the guard." },
  { text = "Stand halfway down the stairs and use the signal torch in your backpack." },
  {
    text = "Head a bit west and talk to Leela to have her follow you (you will be able to continue the next step if she gets stuck).",
  },
  {
    text = "Continue to the bottom floor using the eastern stairs and talk to Ozan.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Speak to Leela or Ozan on the Sophanem beach.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Head to the throne room inside the Golden Palace.",
    title = "The end of the follies",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new(" I can fill you in if you need a reminder of what to do here.") },
  },
  {
    text = "Talk to each of the members to turn the bar over their heads red. Exhausting all chat options is not required.<ul><li>Grand Vizier Ehsan.</li><li>'Admiral' Wadud.</li><li>Batal.</li><li>Commander Akhomet.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I want to call a witness."),
      Action.ConversationHighlight:new("Emissary Qar"),
      Action.ConversationHighlight:new("Emir Ali."),
      Action.ConversationHighlight:new("I want to call a witness."),
      Action.ConversationHighlight:new("Lydia"),
      Action.ConversationHighlight:new("Coenus' death."),
      Action.ConversationHighlight:new("I want to call a witness."),
      Action.ConversationHighlight:new("Maisa"),
      Action.ConversationHighlight:new("Scabaras."),
      Action.ConversationHighlight:new("I want to call a witness."),
      Action.ConversationHighlight:new("Grand Visier Hassan"),
      Action.ConversationHighlight:new("Osman's strengths."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " I was always been taken with the strength of his mind - the sharpest I have ever known."
      ),
    },
  },
  { text = "Re-enter the Golden Palace", actions = { Action.ConversationHighlight:new("Go to throne room.") } },
  { text = "Talk to Pharaoh Leela." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Pharaoh's Folly",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1743984000,
  prereqQuests = { "Beneath Scabaras' Sands" },
})
