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
    text = "Talk to Linza in Burthorpe, near the furnaces south of the Burthorpe lodestone.",
    title = "Mysterious disappearances",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Relomia in the middle of Draynor Village.",
    actions = {
      Action.ConversationHighlight:new("Kindred Spirits"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Linza in south-eastern Daemonheim. Dismiss any followers.<ul><li>(Optional) Bring some good healing food, no more than 20 pieces.</li></ul>",
  },
  {
    text = "Open the trapdoor (you need at least 6 free inventory spaces)",
    actions = { Action.ConversationHighlight:new("Yes, I'm ready.") },
  },
  { text = "Watch the cutscene." },
  { text = "Interact with the Dragonkin guard.", title = "Escaping the prison" },
  { text = "Talk to Sliske." },
  { text = "Talk to Sliske again.", actions = { Action.ConversationHighlight:new("You're a better actor.") } },
  { text = "Interact with the Dragonkin guard for some Dragonkin food." },
  { text = "Use the Dragonkin food on the latrine." },
  { text = "Use the plate on the loose brick." },
  { text = "Use the plate half on the brick to make a blade." },
  { text = "Use the blade on the barracks bed for a cloth strip." },
  { text = "Use the brick on the cloth strip to create a ludicrous flail." },
  { text = "Talk to Sliske to give him the ludicrous flail, which he will use to distract the guard." },
  { text = "Interact with the Dragonkin guard to steal the bag of crystals." },
  { text = "Talk to Sliske to get back a cloth strip." },
  { text = "Use the bag of crystals on the latrine to get an empty bottle." },
  { text = "Use the cloth strip on the empty bottle to create a bottle on a string." },
  { text = "Use the bottle on a string on the latrine to get a bottle of acid." },
  { text = "Talk to Sliske." },
  { text = "Use the bottle of acid on your cell door." },
  { text = "Talk to Sliske." },
  { text = "Exit through either door." },
  { text = "Watch the cutscene.", actions = { Action.ConversationHighlight:new("All right Sliske, I'll do it.") } },
  { text = "Enter the fighting pit directly south.", title = "The games" },
  {
    text = "Talk to Dharok.",
    actions = {
      Action.ConversationHighlight:new("We need to solve this."),
      Action.ConversationHighlight:new("Why not let them kill you?"),
      Action.ConversationHighlight:new("You are already dead."),
    },
  },
  { text = "After Dharok dies, talk to him again." },
  {
    text = "Go north, navigate to the centre of the maze. Use the map here as reference.<ul><li>First choice.</li><li>Second choice.</li><li>Third choice.</li><li>Fourth choice.</li><li>Fifth choice.</li><li>Sixth choice.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I think Ahrim should go left."),
      Action.ConversationHighlight:new("You'd let someone else be hurt?"),
      Action.ConversationHighlight:new("That's no reason to hurt someone else."),
      Action.ConversationHighlight:new("I think Ahrim should be hurt."),
      Action.ConversationHighlight:new("Then suffer now so it won't be as bad later."),
      Action.ConversationHighlight:new("Think about the innocents Ahrim."),
      Action.ConversationHighlight:new("I think Ahrim should be hurt."),
      Action.ConversationHighlight:new("It's more practical to hurt you."),
      Action.ConversationHighlight:new("Sliske won't kill you, he wants you at the end."),
      Action.ConversationHighlight:new("I think Ahrim should be hurt."),
      Action.ConversationHighlight:new("Going west gets it over with faster."),
      Action.ConversationHighlight:new("It's the hesitation that makes it worse."),
      Action.ConversationHighlight:new("So make there be only one choice - you."),
      Action.ConversationHighlight:new("I think Ahrim should be hurt."),
      Action.ConversationHighlight:new("Don't be weak Ahrim."),
      Action.ConversationHighlight:new("Then fight, Ahrim!"),
      Action.ConversationHighlight:new("You'll remember it as a moment of weakness."),
      Action.ConversationHighlight:new("You're Saradomin's holy warrior."),
      Action.ConversationHighlight:new("I think Ahrim should be hurt."),
      Action.ConversationHighlight:new("You're pathetic Ahrim."),
      Action.ConversationHighlight:new("A disappointment."),
      Action.ConversationHighlight:new("...knows he's strong enough to continue."),
      Action.ConversationHighlight:new("...Saradomin's greatest soldier."),
    },
  },
  { text = "Watch the cutscene.", actions = { Action.ConversationHighlight:new("[Any option]") } },
  { text = "Enter the impressive doorway." },
  {
    text = "Talk-To Verac.",
    actions = {
      Action.ConversationHighlight:new("I need the [[Saradomin key]]."),
      Action.ConversationHighlight:new("Make the cut"),
      Action.ConversationHighlight:new("Reach inside"),
      Action.ConversationHighlight:new("Go deeper"),
      Action.ConversationHighlight:new("Pull harder"),
    },
  },
  { text = "Unlock the Saradomin statue." },
  { text = "Talk to Verac." },
  { text = "Pick up the 4 books in the next rooms (do not use area loot to pick up the books)." },
  {
    text = "Investigate the Orrery in the larger room against the western wall.",
    actions = { Action.ConversationHighlight:new("Yes") },
  },
  {
    text = "Investigate the shadow focus in the centre.<ul><li>If no cutscene occurs, lobby and pick up the books again, without area loot.</li></ul>",
  },
  {
    text = "Investigate the lever.",
    actions = {
      Action.ConversationHighlight:new("Investigate the lever."),
      Action.ConversationHighlight:new("Heroically"),
    },
  },
  { text = "Watch or skip the cutscene." },
  {
    text = "Escape in 120 seconds.<ul><li>Run to the illuminated areas and try to climb the ropes.</li><li>Right-click Check on the ropes first to check if they're climbable. Using the wrong rope will result in you taking damage and getting stunned.</li><li>Use Freedom and Anticipation where possible.</li><li>The falling rocks can kill you (which will be an unsafe death), but running out of time will only result in the escape sequence being reset. If either happens, the minimap will be usable in the next escape attempt.</li></ul>",
  },
  { text = "Talk to Dharok." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Kindred Spirits",
  steps = steps,
  timeline = Enums.timeline.worldguardian,
  members = true,
  length = Enums.length.long,
  releaseDate = 1463961600,
  prereqQuests = { "Missing, Presumed Death", "Deadliest Catch" },
})
