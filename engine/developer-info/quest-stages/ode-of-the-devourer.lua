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
    text = "Read note on the cabbage dummy at the Workshop.",
    title = "Visiting Bill",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Overseer Siv in the Command Centre to the south-east.",
    actions = { Action.ConversationHighlight:new("Talk about quests.") },
    postconditions = {
      Condition.ConversationText:new(
        " It's funny you should ask, Your Grace - I believe he went on a supply trip to Al Kharid for the beer garden. Something about 'only the finest acadia wood'."
      ),
    },
  },
  {
    text = "Continue with the Ode of the Devourer portal west of the Al Kharid lodestone.",
    title = "Finding Bill",
    neededItems = { ["Combat"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  {
    text = "Kill the feline akhs near the following civilians, then save them by fully finishing their dialogue:<ul><li>Ali Morrisane, south of Ranael's Super Skirt Store.</li><li>Ayesha, north of the tanner's building.</li><li>Zeke, between three tents, east of the silk stall.</li></ul>",
  },
  { text = "Crawl under the cart south of the Al Kharid bank." },
  { text = "Kill the feline akh." },
  { text = "Talk to Bill." },
  { text = "Head to the City of Um northern docks.", title = "Icthlarin and Zemouregal" },
  { text = "Take the gondola that's located next to where the Suspicious Stranger was sitting." },
  {
    text = "Continue with the Ode of the Devourer portal outside the Sanctum of Rebirth.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  {
    text = "Talk to Icthlarin.",
    actions = { Action.ConversationHighlight:new("Continue Quest.") },
    postconditions = {
      Condition.ConversationText:new(
        " We need to cleanse the corruption infesting the Gate. Once that connection has been severed, The Devourer's curse will be lifted."
      ),
    },
  },
  {
    text = "Enter the floor hatch in Fort Forinthry (outside the eastern wall of the Command Centre).",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  {
    text = "Talk to Zemouregal.",
    actions = { Action.ConversationHighlight:new("Find the idol of Amascut.") },
    postconditions = {
      Condition.ConversationText:new(
        " They were stored at the temple of Amascut southwest of Al Kharid. I will meet you there."
      ),
    },
  },
  {
    text = "Continue with the Ode of the Devourer portal south-west of the Kalphite Hive (Fairy code BIQ).",
    title = "Finding the objects",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("What's he talking about?"),
      Action.ConversationHighlight:new("Not much. Tell me the abridged version."),
      Action.ConversationHighlight:new("Continue Conversation."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " You said there's something here that will help us cleanse the Gate. What is it?"
      ),
    },
  },
  {
    text = "Continue with the Ode of the Devourer portal outside Al Kharid's north-eastern entrance.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  { text = "Talk to Father Salman just north of Zeke's Superior Scimitars." },
  {
    text = "Talk to Ali Morrisane north-east.",
    actions = { Action.ConversationHighlight:new("Look for a different option.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Walk five game squares away." },
  { text = "Talk to Zemouregal." },
  {
    text = "Talk to any 5 citizens around the city with an option to spread rumours about Ali Morrisane.",
    actions = { Action.ConversationHighlight:new("Spread rumours about Ali Morrisane.") },
    postconditions = {
      Condition.ConversationText:new(" I came to discuss plans for the future of this cesspit with that..."),
    },
  },
  { text = "Talk to Ali Morrisane." },
  { text = "Talk to Father Salman." },
  {
    text = "If you haven't completed Ritual of the Mahjarrat:<ul><li>Return to Fort Forinthry.</li><li>Talk to Zemouregal.</li></ul>",
    title = "Shard of the Gate",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Ode of the Devourer'."),
      Action.ConversationHighlight:new("Travel to the ritual site."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "If you have completed Ritual of the Mahjarrat:<ul><li>Use fairy ring DKQ to travel to the Glacor Cave, leave via the exit to the north-east, then run north-west.</li><li>Continue with the Ode of the Devourer portal near the ritual marker.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  {
    text = "Talk to Icthlarin.",
    actions = { Action.ConversationHighlight:new("Continue Conversation.") },
    postconditions = {
      Condition.ConversationText:new(
        " You said there's something here that will help us cleanse the Gate. What is it?"
      ),
    },
  },
  { text = "Search the north-western and south-eastern forgotten warriors." },
  { text = "Talk to Zemouregal." },
  { text = "Use the absorption rune on the ritual marker twice." },
  {
    text = "Use the ceremonial dagger on the ritual marker to receive Shard of the Gate.",
    actions = { Action.ConversationHighlight:new("Do it.") },
    postconditions = {
      Condition.ConversationText:new(
        "(With backpack space:)(Before having absorbed power twice:)The player tries to pry the shard but is concussed by the energy.The energy pulsing from the ritual marker forces your hand away. You were unable to remove the shard.(A random dialogue is selected from the following:) Have you tried kicking it? I'm sure you'll figure it out. Eventually. Oh, and you were so close that time! Take your time. I'm sure that servant of yours will be fine...(After having absorbed power twice:)The player pries the shard out.Player receives shard of the Gate.You carefully remove the shard from the ritual marker."
      ),
    },
  },
  { text = "Talk to Zemouregal." },
  {
    text = "Continue with the Ode of the Devourer portal south-west of the Kalphite Hive (Fairy code BIQ).",
    title = "Idol of Amascut",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("What's he talking about?"),
      Action.ConversationHighlight:new("Not much. Tell me the abridged version."),
      Action.ConversationHighlight:new("Continue Conversation."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " You said there's something here that will help us cleanse the Gate. What is it?"
      ),
    },
  },
  {
    text = "Continue with the Ode of the Devourer portal outside Al Kharid's north-eastern entrance.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  { text = "Talk to Father Salman just north of Zeke's Superior Scimitars." },
  {
    text = "Talk to Ali Morrisane north-east.",
    actions = { Action.ConversationHighlight:new("Look for a different option.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  { text = "Walk five game squares away." },
  { text = "Talk to Zemouregal." },
  {
    text = "Talk to any 5 citizens around the city with an option to spread rumours about Ali Morrisane.",
    actions = { Action.ConversationHighlight:new("Spread rumours about Ali Morrisane.") },
    postconditions = {
      Condition.ConversationText:new(" I came to discuss plans for the future of this cesspit with that..."),
    },
  },
  { text = "Talk to Ali Morrisane." },
  { text = "Talk to Father Salman." },
  {
    text = "If you haven't completed Ritual of the Mahjarrat:<ul><li>Return to Fort Forinthry.</li><li>Talk to Zemouregal.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about 'Ode of the Devourer'."),
      Action.ConversationHighlight:new("Travel to the ritual site."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "If you have completed Ritual of the Mahjarrat:<ul><li>Use fairy ring DKQ to travel to the Glacor Cave, leave via the exit to the north-east, then run north-west.</li><li>Continue with the Ode of the Devourer portal near the ritual marker.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  {
    text = "Talk to Icthlarin.",
    actions = { Action.ConversationHighlight:new("Continue Conversation.") },
    postconditions = {
      Condition.ConversationText:new(
        " You said there's something here that will help us cleanse the Gate. What is it?"
      ),
    },
  },
  { text = "Search the north-western and south-eastern forgotten warriors." },
  { text = "Talk to Zemouregal." },
  { text = "Use the absorption rune on the ritual marker twice." },
  {
    text = "Use the ceremonial dagger on the ritual marker to receive Shard of the Gate.",
    actions = { Action.ConversationHighlight:new("Do it.") },
    postconditions = {
      Condition.ConversationText:new(
        "(With backpack space:)(Before having absorbed power twice:)The player tries to pry the shard but is concussed by the energy.The energy pulsing from the ritual marker forces your hand away. You were unable to remove the shard.(A random dialogue is selected from the following:) Have you tried kicking it? I'm sure you'll figure it out. Eventually. Oh, and you were so close that time! Take your time. I'm sure that servant of yours will be fine...(After having absorbed power twice:)The player pries the shard out.Player receives shard of the Gate.You carefully remove the shard from the ritual marker."
      ),
    },
  },
  { text = "Talk to Zemouregal." },
  {
    text = "If you haven't completed Ritual of the Mahjarrat:<ul><li>Return to Fort Forinthry.</li><li>Talk to Zemouregal.</li></ul>",
    title = "Shard of the Gate",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Talk about 'Ode of the Devourer'."),
      Action.ConversationHighlight:new("Travel to the ritual site."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "If you have completed Ritual of the Mahjarrat:<ul><li>Use fairy ring DKQ to travel to the Glacor Cave, leave via the exit to the north-east, then run north-west.</li><li>Continue with the Ode of the Devourer portal near the ritual marker.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  {
    text = "Talk to Icthlarin.",
    actions = { Action.ConversationHighlight:new("Continue Conversation.") },
    postconditions = {
      Condition.ConversationText:new(
        " You said there's something here that will help us cleanse the Gate. What is it?"
      ),
    },
  },
  { text = "Search the north-western and south-eastern forgotten warriors." },
  { text = "Talk to Zemouregal." },
  { text = "Use the absorption rune on the ritual marker twice." },
  {
    text = "Use the ceremonial dagger on the ritual marker to receive Shard of the Gate.",
    actions = { Action.ConversationHighlight:new("Do it.") },
    postconditions = {
      Condition.ConversationText:new(
        "(With backpack space:)(Before having absorbed power twice:)The player tries to pry the shard but is concussed by the energy.The energy pulsing from the ritual marker forces your hand away. You were unable to remove the shard.(A random dialogue is selected from the following:) Have you tried kicking it? I'm sure you'll figure it out. Eventually. Oh, and you were so close that time! Take your time. I'm sure that servant of yours will be fine...(After having absorbed power twice:)The player pries the shard out.Player receives shard of the Gate.You carefully remove the shard from the ritual marker."
      ),
    },
  },
  { text = "Talk to Zemouregal." },
  {
    text = "Carry an almost empty backpack and your best mining equipment, plus 10 pieces of food and a beast of burden.<ul><li>Optionally follow The Gate of Elidinis strategy article for help on gear setup.</li></ul>",
    title = "Cleansing The Gate of Elidinis",
    neededItems = { ["Idol of Amascut"] = { quantity = 1 }, ["Shard of the Gate"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Continue with the Ode of the Devourer portal outside the Sanctum of Rebirth.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  { text = "Talk to Icthlarin." },
  { text = "Receive blessing on the idol of Amascut or the shard of the Gate." },
  { text = "Enter The Gate of Elidinis stream." },
  {
    text = "Cleanse The Gate of Elidinis.<ul><li>Begin the encounter. Staying south of the moonstone conduits will make learning The Gate of Elidinis encounter easier.</li><li>Gather 25-40 moonstone fragments from nearby moonstone outcrops. Moonstone fragments provide 10% barrier strength per repair action on any of the moonstone conduits. Moonstone fragments also deal 7,500 damage per throw to summoned feline akhs by clicking on the feline akhs.</li><li>Repair any of the moonstone conduits located in the middle of the encounter with moonstone fragments until the barrier's strength reaches at least 30%. Staying south of the moonstone conduits will use their barrier strength to reduce the damage taken from each 'Begone!' mechanic proportional to the barrier's strength. This mechanic also depletes up to 30% of the barriers strength.</li><li>Transmute any corrupt shard of Elidinis until it becomes a cleansed shard of Elidinis. Transmuting adds corruption stacks which increases the damage the player takes over time in the encounter.</li><li>Mine the cleansed shard of Elidinis to obtain cleansed statue shards.</li><li>Activate the action button once 10+ cleansed statue shards have been collected to ask Icthlarin for aid. This will spawn pillars to help the player jump up to The Gate of Elidinis, consuming all the cleansed statue shards on them and applying a damage bonus depending on how quick the player performed all the jumps. Corruption stacks decrease by the amount of cleansed statue shards consumed with the jump.</li><li>Stand within Icthlarin's protective shield after performing all the jumps. Icthlarin's shield will mitigate the incoming attack. The attack also depletes all the barrier strength from the moonstone conduits.</li><li>Keep collecting cleansed statue shards by transmuting corrupt shards of Elidinis and mining cleansed shards of Elidinis, using the action button as needed, repairing the moonstone conduits as needed, and clicking on summoned feline akhs to throw moonstone fragments at them.</li></ul>",
  },
  { text = "Carry your highest level combat equipment, food and potions.", title = "The Shadowsands" },
  {
    text = "Proceed through the cutscene.",
    actions = { Action.ConversationHighlight:new("Meow.") },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  {
    text = "Kill the four waves of monsters.<ul><li>Pray against melee. The easiest way to beat this is to stay close to Icthlarin constantly, and attack the monsters that come near you.</li><li>Stay inside the green barrier to avoid instant kills occurring when the timer at the top runs out.</li></ul>",
  },
  {
    text = "Proceed through the dialogue.",
    title = "Aftermath",
    neededItems = { ["Backpack"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("We all got what we wanted.") },
    postconditions = {
      Condition.ConversationText:new(" Indeed. I'd say this was a mutually beneficial little partnership."),
    },
  },
  {
    text = "Talk to Zemouregal.",
    actions = { Action.ConversationHighlight:new("Send Zemouregal to the Fort Forinthry Prison.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Enter the floor hatch in Fort Forinthry.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.ConversationText:new("(See the transcript for Bill § Afterwards.)") },
  },
  { text = "Talk to Bill." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Ode of the Devourer",
  steps = steps,
  timeline = Enums.timeline.ageofchaos,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1726444800,
  prereqQuests = { "Requiem for a Dragon", "Soul Searching" },
})
