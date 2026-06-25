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
    text = "Talk to Captain Undak, east of the bank (by the eastern water fountain) in Dorgesh-Kaan. (You must dismiss any follower or pet you have or he will not go with you.)",
    title = "Food poisoning",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Can I help with anything?"),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("Okay. What's your point?"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Climb the nearby stairs (north) and talk to Mernik in the nursery. She is the adult in the building." },
  {
    text = "Talk to Torzek outside.",
    actions = {
      Action.ConversationHighlight:new("Calm down. I'm here to help."),
      Action.ConversationHighlight:new("Have the children eaten anything unusual lately?"),
      Action.ConversationHighlight:new("What did they have?"),
    },
  },
  {
    text = "Talk to any nearby child against the building wall about what they ate.<ul><li>Andil:  The chat options may vary from person to person.</li><li>Delgon:</li><li>Gerdi:</li></ul>",
    actions = {
      Action.ConversationHighlight:new("What food did you eat yesterday?"),
      Action.ConversationHighlight:new("What food did you have yesterday?"),
      Action.ConversationHighlight:new("What food did you eat yesterday?"),
    },
  },
  {
    text = "Talk to Captain Undak.  (If you are unable to talk with Captain Undak, make sure you do not have 'Hide Familiar options' checked under Settings>Combat & Action Bar>Choose Option Menu and log out and back in)",
    actions = { Action.ConversationHighlight:new("I've worked out which foods are poisoned.") },
  },
  { text = "Go down the stairs and head south to the market." },
  {
    text = "Talk to Markog, at the south-eastern-most stall of the market.",
    actions = { Action.ConversationHighlight:new("Which merchants have you bought ingredients from?") },
  },
  {
    text = "Talk to Turgok, at the south-western-most stall of the market.",
    actions = { Action.ConversationHighlight:new("Which merchants have you bought ingredients from?") },
  },
  {
    text = "Talk to Merchant Walton, just outside of the market to the south-east.",
    actions = {
      Action.ConversationHighlight:new("This is the one, Captain."),
      Action.ConversationHighlight:new("Yes, I'm sure."),
      Action.ConversationHighlight:new("[Attack the merchant]"),
      Action.ConversationHighlight:new("What's that behind you?"),
    },
  },
  { text = "Proceed to kill the merchant (cannot be ranged)." },
  {
    text = "Talk to Zanik.",
    title = "Zanik",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Yes! What happened?"),
    },
  },
  { text = "Climb on the goblin south-west of the bowl statue." },
  {
    text = "Jump on statues to reach the statue holding the bowl of water.<ul><li>If the jump-on option does not appear, try clicking on the head.</li></ul>",
  },
  { text = "Continue jumping north and take the crossbow from the ledge." },
  { text = "Grapple the spear of the north-eastern statue and take the pendant." },
  { text = "Look into the bowl." },
  { text = "Enter the western portal (the statues will move out of the way) for a cutscene." },
  { text = "Continue conversation with Zanik.", actions = { Action.ConversationHighlight:new("Let's go.") } },
  {
    text = "With Zanik following, go to the north end of Dorgesh-Kaan and up the northern most set of stairs.",
    title = "The council",
  },
  {
    text = "Watch the cutscene.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Zanik is right. You should kill him."),
      Action.ConversationHighlight:new("She means well."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Continue conversation with Captain Undak.",
    actions = {
      Action.ConversationHighlight:new("Where should I look?"),
      Action.ConversationHighlight:new("I'll start looking now."),
    },
  },
  { text = "Run south of the city, up and over staircases until you reach the agility course. Bring a light source." },
  { text = "Descend the ladder top and head all the way south." },
  {
    text = "Talk to Zanik.",
    actions = {
      Action.ConversationHighlight:new("[Sit down next to Zanik.]"),
      Action.ConversationHighlight:new("[Say nothing.]"),
      Action.ConversationHighlight:new("[Say nothing.]"),
      Action.ConversationHighlight:new("[Say nothing.]"),
      Action.ConversationHighlight:new("Yes, but you should have respected the council."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("What do you wish had happened?"),
      Action.ConversationHighlight:new("Someday, we'll build that world."),
    },
  },
  {
    text = "Watch the cutscene.",
    actions = {
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("I have nothing else to say."),
    },
  },
  { text = "After the cutscene talk to Zanik.", actions = { Action.ConversationHighlight:new("Goodbye, Zanik.") } },
  {
    text = "Talk to Captain Undak for a set of H.A.M. robes.",
    actions = {
      Action.ConversationHighlight:new("What is this task?"),
      Action.ConversationHighlight:new("I'll do it."),
    },
  },
  { text = "Prepare for battle, but only wear the H.A.M. robes." },
  {
    text = "Enter the H.A.M. Hideout in Lumbridge forest.",
    title = "Beef with H.A.M.",
    neededItems = { ["H.A.M. robe outfit"] = { quantity = 1 } },
    recommendedItems = {},
  },
  {
    text = "Talk to Johanhus Ulsbrecht in the centremost of the three southern rooms.<ul><li>Before you do, make sure to wear all of the H.A.M. robes</li></ul>",
    actions = {
      Action.ConversationHighlight:new("What are you planning to do about the cave goblins?"),
      Action.ConversationHighlight:new("Isn't Sigmund working with you any more?"),
      Action.ConversationHighlight:new("How can I find Sigmund's base?"),
      Action.ConversationHighlight:new("I'll go there now."),
    },
  },
  {
    text = "Talk to Milton in the windmill near the Fishing Guild, north of the Ardougne lodestone.",
    actions = {
      Action.ConversationHighlight:new("I want to get in to Sigmund's base."),
      Action.ConversationHighlight:new("Arrav."),
    },
  },
  { text = "Climb down the ladder inside the windmill." },
  { text = "Run directly east through the door, continuing east through stacks of crates." },
  {
    text = "Talk to the guard next to the prison.",
    actions = {
      Action.ConversationHighlight:new("I need to see the prisoner."),
      Action.ConversationHighlight:new("Letter?"),
      Action.ConversationHighlight:new("What day is it today?"),
      Action.ConversationHighlight:new("Never mind."),
    },
  },
  { text = "Enter the main area (back through the doors to the west) and pickpocket Sam." },
  { text = "Return to the prison guard.", actions = { Action.ConversationHighlight:new("I have my letter here.") } },
  { text = "Enter the prison and take the key, open the gate." },
  {
    text = "Escape with Grubfoot:<ul><li>Talk to the prisoner, Grubfoot.</li><li>Run into the kitchen, squeeze through the hole.</li><li>Open 1 of the 2 doors.</li><li>Go back through and open the other door as well.</li><li>Return to Grubfoot.</li><li>Stand north of the guard, talk to him to face you and have his back turned. After selecting the chat option, do not continue the conversation until Grubfoot walks past the guard into the next room.</li><li>Talk to Grubfoot in the next room.</li><li>Squeeze into the kitchen. Take the dirty plates from the sink, and wait for the H.A.M. guard to come talk to you.</li><li>Talk to Grubfoot in the bedroom.</li><li>Exit the east door and run to the big doors north.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Wait there. I'll come back for you."),
      Action.ConversationHighlight:new("Let's go"),
      Action.ConversationHighlight:new("I'll distract the guard so you can make a break for it."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Wait at the west door; I'll distract the guard."),
      Action.ConversationHighlight:new("Yes."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("Follow me."),
      Action.ConversationHighlight:new("Let's get out of here."),
    },
  },
  {
    text = "Watch the cutscene and wait for Sigmund to confront you.",
    actions = { Action.ConversationHighlight:new("Time for you to die!") },
  },
  {
    text = "Kill Sigmund.<ul><li>If Zanik doesn't attack Sigmund, climb up the ladder, then back down.</li></ul>",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Kill Zanik repeatedly.<ul><li>Turn off revolution in combat settings and avoid using bleeds and channelled abilities as they cancel the dialogue forcing you to repeat the phase.</li><li>If revolution was left on and dialogue interrupted, run up and then down the ladder to the west to avoid having to fight again.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I won't kill you, Zanik."),
      Action.ConversationHighlight:new("Zanik! What's happened to you?"),
      Action.ConversationHighlight:new("Does our friendship mean nothing to you?"),
      Action.ConversationHighlight:new("But am I *your* enemy?"),
      Action.ConversationHighlight:new("Zanik, you're being controlled. Fight it!"),
      Action.ConversationHighlight:new("Zanik, it's the pendant. Take it off!"),
      Action.ConversationHighlight:new("You've got to do it, otherwise it will win!"),
    },
  },
  { text = "Run west for an earthquake.", title = "Lumbridge caves" },
  { text = "Right click Clear the fallen rocks." },
  {
    text = "Talk to Zanik.",
    actions = {
      Action.ConversationHighlight:new("Try grappling across with your crossbow."),
      Action.ConversationHighlight:new("I can see somewhere it could catch."),
      Action.ConversationHighlight:new("Right a bit."),
      Action.ConversationHighlight:new("Right a bit."),
      Action.ConversationHighlight:new("You've overshot. Go left."),
      Action.ConversationHighlight:new("It's alright, Zanik. You can do it."),
      Action.ConversationHighlight:new("Just a bit further left."),
      Action.ConversationHighlight:new("I'm not leaving you, Zanik."),
    },
  },
  { text = "Go through the cave and talk to Juna." },
  { text = "Run north-west to exit the cave." },
  { text = "Go over the stepping stones and run directly north." },
  {
    text = "Squeeze through the hole.<ul><li>If you don't have the shortcut unlocked, you'll have to go all the way around towards Lumbridge Castle's Cellar.</li><li>If at any point Zanik stops following you, she can be found back at Juna.</li></ul>",
  },
  { text = "Right click Follow Kazgar.", actions = { Action.ConversationHighlight:new("Dorgeshuun Mines") } },
  {
    text = "Enter Dorgesh-Kaan and talk to Captain Undak north of the market (between the fountains).",
    title = "Time capsule",
    neededItems = { ["Light sources"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("What can I do?"), Action.ConversationHighlight:new("Goodbye.") },
    postconditions = { Condition.ConversationText:new("") },
  },
  {
    text = "Climb the northern stairs for a cutscene (close the Bandos's Ultimatum screen that pops up).",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Climb back up the stairs, talk to the Goblin scribe.",
    actions = {
      Action.ConversationHighlight:new("What do you want me to do?"),
      Action.ConversationHighlight:new("I'll help you."),
      Action.ConversationHighlight:new("I'll go now."),
    },
  },
  { text = "Climb down the stairs and run south-east through a corridor (near the quest start point)." },
  {
    text = "Talk to Tegdak in the north-east room of the ground floor[UK]1st floor[US].",
    actions = { Action.ConversationHighlight:new("The scribe sent me to get a box of artefacts.") },
  },
  {
    text = "Return to the Goblin scribe upstairs.",
    actions = { Action.ConversationHighlight:new("Here it is."), Action.ConversationHighlight:new("I'll go now.") },
  },
  { text = "Exit the city through the bone door north-east, downstairs." },
  { text = "Talk to Mistag.", actions = { Action.ConversationHighlight:new("Could you bury this time capsule?") } },
  {
    text = "Return to the city and talk to Oldak north of the bank.<ul><li>if invention has been unlocked.</li></ul>",
    title = "Preparing for battle",
    actions = {
      Action.ConversationHighlight:new("What are you arguing about?"),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("What do you want me to do?"),
      Action.ConversationHighlight:new("I'll go now."),
      Action.ConversationHighlight:new("No - talk about quest or other things."),
    },
  },
  { text = "Return to the agility course (south end of Dorgesh-Kaan) - do not climb down the ladder." },
  { text = "Run east to the power station and climb down the stairs." },
  {
    text = "Talk to Turgall, make sure you have at least 2 free inventory spots .",
    actions = { Action.ConversationHighlight:new("Oldak needs a pair of energy projectors and a focusing chamber.") },
  },
  {
    text = "Talk to Oldak, now south-east of the market.",
    actions = { Action.ConversationHighlight:new("I have the parts here.") },
  },
  {
    text = "Talk to Zanik.",
    actions = {
      Action.ConversationHighlight:new("Oldak has modified your crossbow."),
      Action.ConversationHighlight:new("Oldak said it was certain to work."),
      Action.ConversationHighlight:new("I'll talk to you later."),
    },
  },
  {
    text = "Talk to Mernik in the children's nursery north-east upstairs.",
    actions = {
      Action.ConversationHighlight:new("What can I do?"),
      Action.ConversationHighlight:new("I'll talk to him."),
    },
  },
  {
    text = "Talk to Ambassador Alvijar  in the house north-west of the nursery.",
    actions = {
      Action.ConversationHighlight:new("I want to talk about the children."),
      Action.ConversationHighlight:new("You coward!"),
    },
  },
  {
    text = "Return to Mernik.",
    actions = {
      Action.ConversationHighlight:new("I talked to him... He said no."),
      Action.ConversationHighlight:new("Nothing. Everything is fine."),
    },
  },
  { text = "Prepare to fight enemies weak to magic.", title = "Final fight" },
  {
    text = "Talk to Zanik, south-east of the market.",
    actions = { Action.ConversationHighlight:new("I'm ready now.") },
  },
  {
    text = "Defeat the Bandos avatar.",
    actions = { Action.ConversationHighlight:new("We'll see about that. Zanik - now!") },
  },
  { text = "Pick up all 4 parts of the crossbow while killing statues." },
  { text = "Use the crossbow pieces on each other.", actions = { Action.ConversationHighlight:new("Enough talk.") } },
  {
    text = "Kill the avatar again.",
    actions = { Action.ConversationHighlight:new("[Use Zanik's crossbow's special attack.]") },
  },
  { text = "Take the avatar's pendant." },
  {
    text = "Talk to Zanik.",
    actions = {
      Action.ConversationHighlight:new("[Sit down next to Zanik.]"),
      Action.ConversationHighlight:new("[Say nothing.]"),
      Action.ConversationHighlight:new("About time you woke up!"),
      Action.ConversationHighlight:new("We won. Oldak's crossbow worked."),
      Action.ConversationHighlight:new("The mark on your forehead is gone."),
      Action.ConversationHighlight:new("Here it is."),
    },
  },
  { text = "Follow Zanik through the portal." },
  { text = "Click the screen to exit cutscene." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "The Chosen Commander",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1237248000,
  prereqQuests = { "Land of the Goblins" },
})
