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
    text = "Take a light source and go to the Lumbridge Castle cellar.",
    title = "What's with the cabal?",
    neededItems = { ["Light source"] = { quantity = 1 }, ["H.A.M. robes"] = { quantity = 1 } },
    recommendedItems = {},
  },
  { text = "Squeeze through the hole in the wall." },
  { text = "Right click on Kazgar, and use the option 'Follow'." },
  {
    text = "Talk to Mistag and ask about the favour.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("What is this favour?") },
  },
  {
    text = "[Accept Quest]<ul><li>Alternatively to leave mines, right click on Mistag and use 'Follow'.</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
    actions = { Action.ConversationHighlight:new("Can you show me the way out of the mines?") },
  },
  {
    text = "Return to the Lumbridge Castle cellar, talk to Zanik.",
    actions = {
      Action.ConversationHighlight:new("Yes, I'm [Player name]!"),
      Action.ConversationHighlight:new("Yes, I have two sets of robes!"),
    },
  },
  { text = "Talk to the Cook inside Lumbridge Castle.", title = "Lumbridge safari" },
  {
    text = "Talk to Duke Horacio upstairs in the castle.",
    actions = { Action.ConversationHighlight:new("Have you heard of any HAM activity lately?") },
    postconditions = {
      Condition.ConversationText:new(
        " No. I know they've been gaining members but I haven't heard anything else from them. They may be planning something but then again they may not."
      ),
    },
  },
  { text = "Go east outside the castle on the ground floor[UK]1st floor[US] for a cutscene." },
  { text = "Talk to an unnamed man or a woman (there are many in the marketplace)." },
  { text = "Talk to one of the named people around the castle, such as Lachtopher or Donie." },
  {
    text = "Talk to either the Lumbridge General Store shopkeeper or his assistant.",
    actions = { Action.ConversationHighlight:new("No, thanks.") },
  },
  {
    text = "Talk to Doomsayer near the castle gate.",
    actions = { Action.ConversationHighlight:new("Don't worry, Zanik.") },
    postconditions = {
      Condition.ConversationText:new(" Thanks, Player. If there's any doom, I'm sure we can overcome it together!"),
    },
  },
  { text = "Talk to the Lumbridge Sage right in front of the castle gate." },
  {
    text = "Take Zanik to a level 2 goblin across the east bridge.<ul><li>Make sure to not click while Zanik walks off the bridge of the east side. She starts a dialogue that is easy to click away from accidentally.</li></ul>",
  },
  {
    text = "Talk to Father Aereck in the church.",
    actions = { Action.ConversationHighlight:new("Bye.") },
    postconditions = { Condition.ConversationText:new(" May Saradomin bless you in all your pursuits!") },
  },
  {
    text = "Talk to Bob at Bob's Brilliant Axes.",
    actions = { Action.ConversationHighlight:new("Let's go, Zanik.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Talk to Zanik.<ul><li>If she doesn't have the talk option, teleport away and talk to her in the Lumbridge Castle cellar.</li></ul>",
    actions = { Action.ConversationHighlight:new("Will you tell me about the mark on your forehead?") },
    postconditions = { Condition.ConversationText:new("(Continues below.)") },
  },
  { text = "After the cutscene, put on your set of H.A.M. robes and prepare to head to the H.A.M. hideout." },
  {
    text = "Open and then climb down the trapdoor in the Lumbridge forest northwest of the Lumbridge Castle to enter the hideout.",
    title = "Requiescat in pace",
  },
  {
    text = "Talk with Johanhus Ulsbrecht (in the southern room) about the cave goblins.",
    actions = {
      Action.ConversationHighlight:new("Are you planning to do anything about the cave goblins?"),
      Action.ConversationHighlight:new("That's good."),
    },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Go next to the southern hanging brazier in the main room - Zanik will spot a trapdoor (Make sure Zanik is beside you and not trapped in the previous room).",
  },
  { text = "Pick-lock on the hidden trap door by right clicking it, then climb down." },
  { text = "Walk past the first guard (Zanik shoots him).", title = "Slipping past the guards" },
  { text = "Squeeze through the crack in the wall behind where the first guard stood." },
  { text = "Squeeze through the crack in the opposite wall while the guard in the corridor has his back turned." },
  { text = "Go behind the west guard. Talk to him for a cutscene." },
  {
    text = "Continuing the dialogue then wait for the corridor's guard to turn his back, tell Zanik 'Now!'.",
    actions = { Action.ConversationHighlight:new("Now!") },
    postconditions = {
      Condition.ConversationText:new(
        "(If the guard was facing away from Zanik:)Zanik kills the guard. We got him, Player!"
      ),
    },
  },
  {
    text = "Go east to the end of the central corridor, but not around the corner.<ul><li>Talk to Zanik, telling  her to wait.</li></ul>",
    actions = { Action.ConversationHighlight:new("Wait here.") },
  },
  {
    text = "Run out of the corridor and south down to the south-east corner, making the north-east guard follow you. Zanik will shoot him.",
  },
  {
    text = "Go to the end of either the west or east passages, but not around the corner.<ul><li>Talk to Zanik, telling her to wait again.</li></ul>",
    actions = { Action.ConversationHighlight:new("Wait here.") },
  },
  { text = "Go to the other passage, via the central corridor, and approach the guard (Zanik shoots him)." },
  { text = "Listen at the door (you will be caught)." },
  { text = "Pick the jail's door lock and leave the H.A.M. hideout.", title = "Divine resurrection" },
  { text = "Pick up Zanik, lying near a tree south of the hideout entrance." },
  { text = "If you have finished the Tears of Guthix quest, you can teleport there with her using a games necklace." },
  { text = "Climb down the trapdoor in the Lumbridge castle, and squeeze through the hole in the cellar wall." },
  { text = "Squeeze through the hole to the south, run south then jump across the stepping stones." },
  { text = "Enter the tunnel to reach the Tears of Guthix cavern." },
  {
    text = "With Zanik in your inventory and both hands free, talk to Juna.",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = {
      Condition.ConversationText:new(
        "(If you have a follower:) You should pick up your pet. This is no time to have an animal at your feet to trip you up."
      ),
    },
  },
  {
    text = "Collect  20 blue tears from the walls to trigger a cutscene.<ul><li>Green tears empty your bowl.</li></ul>",
  },
  { text = "Talk to Zanik.", actions = { Action.ConversationHighlight:new("Let's Go!") } },
  {
    text = "Prepare for the fight against Sigmund and his guards. (but keep the H.A.M robes with you)",
    title = "To be continued...",
  },
  { text = "Teleport to Lumbridge then enter the castle cellar." },
  {
    text = "Talk to Zanik and have her follow you.",
    actions = { Action.ConversationHighlight:new("Let's go!") },
    postconditions = { Condition.ConversationText:new(" Okay!") },
  },
  { text = "Go to Seth Groats's farm on the east side of the river near the cow paddock, enter the south gate." },
  { text = "Unequip any weapons or shields." },
  { text = "Equip full H.A.M. set." },
  {
    text = "Search one of the empty crates by the tree.",
    actions = {
      Action.ConversationHighlight:new("I don't know, what are you thinking?"),
      Action.ConversationHighlight:new("Good idea."),
    },
    postconditions = { Condition.ConversationText:new("Zanik jumps in an empty crate and the player picks it up.") },
  },
  { text = "After Zanik climbs in the crate, carry it down into the trapdoor just north of the empty crates." },
  {
    text = "Go west and walk up to Sigmund, instead of immediately clicking to attack him. You must trigger the dialogue with Sigmund in order to have Zanik attack him, which is needed to complete the quest.<ul><li>Kill the guards first. This allows Zanik to attack Sigmund who will then switch to Protect from Missiles.</li><li>Kill Sigmund, who uses the Protect from Missiles prayer. If he's protecting against your attack style, avoid him so he switches back to Protect from Missile.</li></ul>",
  },
  { text = "Smash the drilling machine at the centre of the room." },
  { text = "Go east then south and unblock the tunnel for a cutscene." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Death to the Dorgeshuun",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1150848000,
  prereqQuests = { "The Lost Tribe" },
})
