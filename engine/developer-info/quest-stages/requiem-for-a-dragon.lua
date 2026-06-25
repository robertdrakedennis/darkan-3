local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Enums = require("core.enums")
local Types = require("core.types")

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Aster in the Fort Forinthry Town Hall.",
    title = "Starting out",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Talk about quests..."),
      Action.ConversationHighlight:new("Talk about 'Requiem for a Dragon'."),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Head east to the rowboat docked next to the Grove cabin.",
    title = "Approaching Vorkath",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Interact with the rowboat to travel to Ungael.<ul><li>You will spawn next to hostile Zamorakian scouts; praying against Ranged can be helpful. This is a safe encounter, and you will respawn outside of the rowboat upon death.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Aster § Afterwards.)") },
  },
  { text = "Run north-west past the scouts and enter the colossal cave." },
  {
    text = "Head north-west and approach Vorkath.",
    actions = {
      Action.ConversationHighlight:new("Reach out your hand. (friendly)"),
      Action.ConversationHighlight:new("Keep your hand extended. (friendly)"),
      Action.ConversationHighlight:new("Encourage Vorkath. (friendly)"),
      Action.ConversationHighlight:new("Try not to shiver. (friendly)"),
      Action.ConversationHighlight:new("Study the creature. (neutral)"),
      Action.ConversationHighlight:new("Lay the dragon's soul to rest. (Necromancy)"),
    },
    postconditions = {
      Condition.ConversationText:new(" If it worked on a human soul, I guess it'll work on a dragon too?"),
    },
  },
  { text = "Inspect the crystal shard in the backpack." },
  {
    text = "Return to the Ungael beach, this time travelling north-east up the hill and interact with the Ruins Entrance to enter.",
  },
  { text = "Head north, just in front of the Imposing Statue, and interact with the doorway to the south." },
  { text = "Follow the path south-west and kill the translator." },
  { text = "Pick up the dropped Zamorakian translator's notes and read them." },
  { text = "Search the bookcase on the northern wall for Zorgoth's Journal." },
  { text = "Search the shelves immediately southeast for a broken focus." },
  { text = "Inspect the dragonkin mural on the western wall." },
  { text = "Pick up the congealed potion in the south-eastern corner." },
  {
    text = "Listen to the Archivist about all findings.",
    actions = {
      Action.ConversationHighlight:new("The wall mural."),
      Action.ConversationHighlight:new("The congealed potion."),
      Action.ConversationHighlight:new("The broken focus."),
      Action.ConversationHighlight:new("The crystal shard."),
    },
    postconditions = { Condition.ConversationText:new(" Kranon failed. He belongs to the sunken black. Despicable.") },
  },
  {
    text = "Return to Fort Forinthry and enter the floor hatch east of the Command Centre (south-east).",
    title = "Investigating Vorkath's corruption - Fort Forinthry",
  },
  {
    text = "Talk to Zemouregal.<ul><li>If your Necromancy level is 120, you can skip most of the conversation</li><li>Otherwise, you must follow further dialogue options</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Show off your mastery of necromancy."),
      Action.ConversationHighlight:new("What did you do to Vorkath?"),
      Action.ConversationHighlight:new("I've heard enough."),
      Action.ConversationHighlight:new("Mock his skeletal form."),
      Action.ConversationHighlight:new("Remind him of the other times you've defeated him."),
      Action.ConversationHighlight:new("I have a soul and you don't."),
      Action.ConversationHighlight:new("What did you do to Vorkath?"),
      Action.ConversationHighlight:new("I've heard enough."),
    },
  },
  {
    text = "Talk to the Tree of Balance, south of the Archaeology Guild.",
    title = "Finding focus - Tree of Balance",
    actions = {
      Action.ConversationHighlight:new("Continue Requiem for a Dragon."),
      Action.ConversationHighlight:new("Let's get straight to the point."),
    },
  },
  {
    text = "Head to Memorial to Guthix (do not enter the Hall of Memories)<ul><li>You can teleport directly there using memory strand or Sixth-Age circuit, otherwise teleport to Eagles' Peak lodestone and run north-west</li></ul>",
    title = "Memorial to Guthix",
  },
  { text = "South-east outside the memorial, uncover both fertile soil spots" },
  {
    text = "Excavate both halves of the soul beacon from the uncovered standing stone debris and runic debris. There is a material storage container just north of the spots.<ul><li>Damaged soul beacon (base)</li><li>Damaged soul beacon (core)</li></ul>",
  },
  {
    text = "Return to the Tree of Balance with the two damaged parts.",
    title = "Restoring the beacon",
    actions = { Action.ConversationHighlight:new("Continue Requiem for a Dragon.") },
    postconditions = {
      Condition.ConversationText:new(
        "(Repeated in a pop-up:) You do not have enough inventory space to reclaim the Soul Beacon from the Tree of Balance.(Continues below.)"
      ),
    },
  },
  { text = "Head to any archaeologist's workbench to restore the damaged soul beacon." },
  {
    text = "Return to the Tree of Balance with the restored soul beacon. Chat options can vary, but the correct order is: Change, Elemental, Commune, Change.",
    actions = {
      Action.ConversationHighlight:new("Continue Requiem for a Dragon."),
      Action.ConversationHighlight:new("Deduce the glyph order."),
      Action.ConversationHighlight:new("Change"),
      Action.ConversationHighlight:new("Elemental"),
      Action.ConversationHighlight:new("Commune"),
      Action.ConversationHighlight:new("Change"),
    },
    postconditions = {
      Condition.ConversationText:new(
        "(If you answered any wrong:) No, that doesn't sound right.(Shows the previous options.)"
      ),
    },
  },
  {
    text = "Return to the City of Um and talk to Death.",
    title = "Soulfaring",
    neededItems = { ["Soul beacon"] = { quantity = 1 }, ["Soulfarer (ritual) ingredients"] = { quantity = 1 } },
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Just tell me what the lead is."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Misalionar once mentioned a focus he was creating. A beacon, which could force back shadow and light the path for wayward souls."
      ),
    },
  },
  { text = "Complete a soulfarer ritual at the Um ritual site." },
  {
    text = "Travel to Fort Forinthry and take the boat back to Ungael.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Aster § Afterwards.)") },
  },
  { text = "Return to the colossal cave in Ungael and head north." },
  { text = "Repair the 13 spots of the ruined ritual site, just east of Vorkath." },
  { text = "Set up the soulfarer ritual." },
  {
    text = "Perform the ritual. A cutscene will begin.<ul><li>Note that this is a safe encounter, and you will respawn outside of the rowboat upon death.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Aster § Afterwards.)") },
  },
  {
    text = "Perform the ritual again, pausing to:<ul><li>Kill Vengeance whenever she appears. Vengeance can drastically drain your stats so super restores can be useful. Deathtouched darts can be used. If you use speed III on spare ritual space, and speed II or higher in necromancy cape, and never miss a shadow rift, you will only have to kill Vengeance once.</li><li>Close any Shadow Rifts as soon as they appear. Continuing the ritual while a Shadow Rift is open will reverse the ritual's progress meter.</li></ul>",
  },
  { text = "Watch the cutscene." },
  { text = "Head west and inspect Vorkath." },
  {
    text = "Return to the City of Um and talk to Death.",
    title = "City of Um",
    neededItems = { ["Soul beacon"] = { quantity = 1 }, ["Soulfarer (ritual)"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Aster § Afterwards.)") },
  },
  { text = "Complete a soulfarer ritual at the Um ritual site." },
  {
    text = "Travel to Fort Forinthry and take the boat back to Ungael.",
    title = "Ungael",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Aster § Afterwards.)") },
  },
  { text = "Return to the colossal cave in Ungael and head north." },
  { text = "Repair the 13 spots of the ruined ritual site, just east of Vorkath." },
  { text = "Set up the soulfarer ritual." },
  {
    text = "Perform the ritual. A cutscene will begin.<ul><li>Note that this is a safe encounter, and you will respawn outside of the rowboat upon death.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Aster § Afterwards.)") },
  },
  {
    text = "Perform the ritual again, pausing to:<ul><li>Kill Vengeance whenever she appears. Vengeance can drastically drain your stats so super restores can be useful. Deathtouched darts can be used. If you use speed III on spare ritual space, and speed II or higher in necromancy cape, and never miss a shadow rift, you will only have to kill Vengeance once.</li><li>Close any Shadow Rifts as soon as they appear. Continuing the ritual while a Shadow Rift is open will reverse the ritual's progress meter.</li></ul>",
  },
  { text = "Watch the cutscene." },
  { text = "Head west and inspect Vorkath." },
  {
    text = "Talk to Death in City of Um.",
    title = "Aftermath - City of Um",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Aster § Afterwards.)") },
  },
  {
    text = "Head south-east, south of Selene and directly north of the large cave opening in the southern part of the city and approach Vorkath.",
  },
  {
    text = "Return to Fort Forinthry and enter the floor hatch east of the Command Centre.",
    title = "Confronting Zemouregal",
  },
  { text = "Talk to Zemouregal to complete the quest." },
  {
    text = "Return to Fort Forinthry and enter the floor hatch east of the Command Centre.",
    title = "Confronting Zemouregal",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Talk to Zemouregal to complete the quest." },
}

return Quest:new({
  name = "Requiem for a Dragon",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1713744000,
  prereqQuests = { "Battle of Forinthry", "Tomes of the Warlock", "Kili Row" },
})
