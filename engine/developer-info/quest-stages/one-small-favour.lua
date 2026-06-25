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
    text = "Talk to Yanni Salika in Shilo Village (north of the bridge).",
    title = "Karamja",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = {
      Action.ConversationHighlight:new("Is there anything else interesting to do around here?"),
      Action.ConversationHighlight:new("Ok, see you in a tick!"),
    },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to one of the jungle foresters south of the village. You will receive their blunt hatchet.",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you about red mahogany."),
      Action.ConversationHighlight:new("Ok, I'll take your hatchet to get it sharpened."),
    },
    postconditions = { Condition.ConversationText:new("The Jungle Forester hands you the blunt hatchet.") },
  },
  {
    text = "Speak to Brian in Port Sarim (Axe shop north-east of the lodestone). Make sure he takes the blunt hatchet before you leave.",
    title = "Asgarnia and Misthalin",
    neededItems = {
      ["Steel bar"] = { quantity = 1 },
      ["Clean harralander"] = { quantity = 1 },
      ["Clean marrentill"] = { quantity = 1 },
      ["Vial of water"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = {
      Action.ConversationHighlight:new("Do you sharpen hatchets?"),
      Action.ConversationHighlight:new("Look, can you sharpen this cursed hatchet or what?"),
      Action.ConversationHighlight:new("Ok, ok, I'll do it! I'll go and see Aggie."),
    },
    postconditions = { Condition.ConversationText:new("Player has blunt hatchet removed from them.") },
  },
  {
    text = "Speak to Aggie, the witch in Draynor Village.",
    actions = {
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("Let me guess, you're going to ask me to do you a favour?"),
      Action.ConversationHighlight:new("Oh, Ok, I'll see if I can find Jimmy."),
    },
  },
  {
    text = "Go to the H.A.M. Hideout (north-west of the Lumbridge Castle, in the Lumbridge forest). Pick the lock on the trapdoor, and climb down. Head to the middle southern cavern and speak to Johanhus Ulsbrecht.<ul><li>Or if player has completed The Chosen Commander.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I'm looking for Jimmy the Chisel."),
      Action.ConversationHighlight:new("And I suppose you need me to do you a favour?"),
      Action.ConversationHighlight:new("Ok, Jimmy has to be worth more than a few scrawny chickens!"),
      Action.ConversationHighlight:new("One Small Favour"),
      Action.ConversationHighlight:new("I'm looking for Jimmy the Chisel."),
      Action.ConversationHighlight:new("And I suppose you need me to do you a favour?"),
      Action.ConversationHighlight:new("Ok, Jimmy has to be worth more than a few scrawny chickens!"),
    },
    postconditions = {
      Condition.ConversationText:new(" That's exactly right! Do this for us and we'll let Brother Jimmy go!"),
    },
  },
  {
    text = "Talk to Fred the Farmer near the sheep's pen in Lumbridge.<ul><li>Or if player has seen 'The Thing'.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you about Jimmy."),
      Action.ConversationHighlight:new("I need to talk to you about Jimmy."),
    },
  },
  {
    text = "Talk with Seth Groats near the chickens' pen in Lumbridge.  (Back on the east side of the river)",
    actions = { Action.ConversationHighlight:new("Oh, ok! I guess it's not that much further to Varrock!") },
    postconditions = {
      Condition.ConversationText:new(" Many thanks! Remember, you need to take him three steel bars."),
    },
  },
  {
    text = "Take 3 steel bars (cannot be noted) to Horvik in the platebody shop in Varrock (north-east of the central fountain), who is ill and needs a herbal tincture.",
    actions = {
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("Hi, I need to talk to you about chicken cages!"),
      Action.ConversationHighlight:new("Ok, I guess one good turn deserves another."),
    },
    postconditions = { Condition.ConversationText:new(" Ok, I guess one good turn deserves another.") },
  },
  {
    text = "Speak to the Apothecary twice, located in south-west Varrock, at the potion icon.<ul><li>Or if player has destroyed their Doctor's hat.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("Horvik is ill, I need herbal tincture."),
      Action.ConversationHighlight:new("Oh, ok, I guess it's not that far to the Barbarian Village."),
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("Horvik is ill, I need breathing salts and herbal tincture."),
      Action.ConversationHighlight:new("Oh, ok, I guess it's not that far to the Barbarian Village."),
    },
    postconditions = { Condition.ConversationText:new(" Wonderful, I'd really appreciate it!") },
  },
  {
    text = "Go to Barbarian Village (also called Gunnarsgrunn) and talk to Tassie Slipcast, located within the pottery building in the south-east.",
    actions = { Action.ConversationHighlight:new("Ok, I'll deal with Hammerspike!") },
    postconditions = { Condition.ConversationText:new(" Really, you will?") },
  },
  {
    text = "Enter the Dwarven Mine and find Hammerspike Stoutbeard on the far west side ('Hammerspike's hangout' on the map). Ask him about Tassie Slipcast.",
    actions = {
      Action.ConversationHighlight:new("Have you always been a gangster?"),
      Action.ConversationHighlight:new("Ok, another favour..I think I can manage that."),
    },
  },
  {
    text = "Go to Taverley and talk with Sanfew, located in the house slightly south-west of Pikkupstix's Summoning Shop.<ul><li>The quest Druidic Ritual must be completed to speak with Sanfew about One Small Favour.</li><li>If Eadgar's Ruse is completed, or the third option is 'Are you taking any new initiates?'</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Are you taking any new initiates?"),
      Action.ConversationHighlight:new("Do you accept dwarves?"),
      Action.ConversationHighlight:new("A dwarf I know wants to become an initiate."),
      Action.ConversationHighlight:new("Yep, it's a deal."),
      Action.ConversationHighlight:new("Are you taking any new initiates?"),
      Action.ConversationHighlight:new("Do you accept dwarves?"),
      Action.ConversationHighlight:new("A dwarf I know wants to become an initiate."),
      Action.ConversationHighlight:new("Yep, it's a deal."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " All you need to do is add clean harralander to a vial, then add clean marrentill. It'll warm that poor fellow up a treat!"
      ),
    },
  },
  {
    text = "Make a Guthix rest by first adding the harralander to the vial of water, then adding the marrentill. Make sure you use the harralander on the vial, instead of clicking the vial, as you may accidentally use the marrentill first.",
  },
  {
    text = "Travel to White Wolf Mountain, and speak to Captain Bleemadge.  Right-click to talk to him.<ul><li>To reach him enter the cave southwest of the Burthorpe lodestone (behind the Burthorpe mine entrance), then south towards the gnome glider.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I have a special potion here for you from Sanfew!"),
      Action.ConversationHighlight:new("Ok, I'll go and get you some T.R.A.S.H."),
    },
    postconditions = { Condition.ConversationText:new(" Wonderful, my friend, wonderful!") },
  },
  {
    text = "Travel to Catherby and speak to Arhein on the docks. (General store owner, south of the lodestone)<ul><li>While here, purchase an empty pot from Arhein's Store.</li></ul>",
    title = "Kandarin",
    actions = {
      Action.ConversationHighlight:new("I need to talk T.R.A.S.H. to you."),
      Action.ConversationHighlight:new("Yes, Ok, I'll do it!"),
    },
  },
  {
    text = "Go to Seers' Village and speak to Phantuwti Fanstuwi Farsight, south-east of the lodestone.",
    actions = {
      Action.ConversationHighlight:new("Hi, can you give me a weather forecast?"),
      Action.ConversationHighlight:new("What can I do to help?"),
      Action.ConversationHighlight:new("Yes, Ok, I'll do it."),
    },
  },
  {
    text = "Enter the Goblin Cave south-east of the fishing guild (teleport to the Ardougne lodestone and run north).<ul><li>Sometimes the cave is not marked on the minimap. It is just outside the fence opposite the fishing guild building near the windmill.</li></ul>",
  },
  { text = "Once inside, head north, then east where you find Petra Fiyed trapped in rock. Search the sculpture." },
  {
    text = "Go to Ardougne and speak to Wizard Cromperty, located north-east from the market.<ul><li>While you are in Ardougne, pick up five pigeon cages from the back yard of the first house south of the north-western bank. There are three cages that will respawn immediately.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you about a girl stuck in some rock!"),
      Action.ConversationHighlight:new("Oh! Ok, one more 'small favour' isn't going to kill me..I hope!"),
    },
  },
  {
    text = "Go to Port Khazard (teleport to Catherby and use a charter to Port Khazard or teleport to the Yanille lodestone, run east through town then north) and talk to Tindel Marchant on the southern pier.<ul><li>While you are here you can also speak to him about identifying swords for one of easy Ardougne achievements, They're Long and Pointy</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Ask about iron oxide."),
      Action.ConversationHighlight:new("Ok, I'll do it!"),
      Action.ConversationHighlight:new("Chat about Antiques Store"),
      Action.ConversationHighlight:new("What do you do here?"),
    },
  },
  {
    text = "Go to the Feldip Hills and talk to Rantz, located to the far east of fairy ring AKS.",
    actions = {
      Action.ConversationHighlight:new("I need to talk to you about a mattress."),
      Action.ConversationHighlight:new("Ok, I'll see what I can do."),
    },
    postconditions = {
      Condition.ConversationText:new(" Dat would be da good fing...den me's help wiv da flufsies sack."),
    },
  },
  {
    text = "Go west to the gnome glider and right-click to talk to Gnormadium Avlafrim.",
    actions = {
      Action.ConversationHighlight:new("Rantz said I should come and help you finish this project."),
      Action.ConversationHighlight:new("Yes, I'll take a look at them."),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Ok then...well, just pop over and let me know if you're not sure what you're doing...I'm sure that I'll just have to fix it myself anyway. If you need any materials, just pop over."
      ),
    },
  },
  {
    text = "For each of the 8 gnome landing lights immediately east:<ul><li>Select the 'Search' option on the gnome landing light to receive an uncut gem.</li><li>Cut the gem. If you crush one talk to the gnome to buy a replacement. .</li><li>Use the cut gem on the light it came from (from west to east is sapphire, opal, red topaz, jade).</li><li>Make sure to use them on the gnome landing light.</li><li>Make sure to use them on the gnome landing light.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I need new gem to fix the lights."),
      Action.ConversationHighlight:new("Ok, here's 500 gold, I'll buy one."),
    },
  },
  { text = "Talk to the gnome again.", actions = { Action.ConversationHighlight:new("I've fixed all the lights!") } },
  {
    text = "Go back to Rantz to receive a comfy mattress.",
    title = "Completing the favours",
    neededItems = {
      ["Bronze bar"] = { quantity = 1 },
      ["Iron bar"] = { quantity = 1 },
      ["Steel bar"] = { quantity = 1 },
      ["Empty pot"] = { quantity = 1 },
      ["Pigeon cage"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Ok, I've helped that Gnome, he shouldn't bother you anymore.") },
  },
  {
    text = "Take the mattress to Tindel in Port Khazard, who gives you iron oxide.",
    actions = { Action.ConversationHighlight:new("I have the mattress.") },
  },
  {
    text = "Take the iron oxide to Wizard Cromperty in Ardougne, who gives you an animate rock scroll.",
    actions = { Action.ConversationHighlight:new("I have that iron oxide you asked for!") },
  },
  { text = "Read Animate rock scroll in the Goblin Cave near the trapped Petra and kill Slagilith when it appears." },
  { text = "Read Animate rock scroll again, then talk to Petra." },
  {
    text = "Go to Seers' Village and speak to Phantuwti in his house.",
    actions = {
      Action.ConversationHighlight:new("I've released Petra, she should have returned."),
      Action.ConversationHighlight:new("I'll run you through if you don't give me that weather report."),
      Action.ConversationHighlight:new("Why can't you get a clear picture?"),
      Action.ConversationHighlight:new("Which special Seers tools do you mean?"),
      Action.ConversationHighlight:new("What do you mean, 'special combination of items'?"),
    },
    postconditions = {
      Condition.ConversationText:new(
        " Well, let me think now...There's a copper eye, a rotating thing, and some pointy direction thingies. It's all highly scientific - and of course mystical - in a very magical 'foresight' sort of way."
      ),
    },
  },
  {
    text = "Access the roof via either the ladder on the north wall of Phantuwti's house, or the ladder in the building to the east (you have to go up two ladders to get to the roof).",
  },
  { text = "Search the weather vane twice to get three broken vane parts." },
  {
    text = "With a bronze bar, an iron bar, and a steel bar, use each of the the parts of the weather vane on any anvil (there is an anvil just north of the house).",
  },
  { text = "Go back to the roof of the house and put the weathervane pillar, the directionals and the ornament back." },
  {
    text = "Talk to Phantuwti again and he will give you the weather report.",
    actions = { Action.ConversationHighlight:new("I've fixed the weather vane!") },
  },
  {
    text = "Take the report to Arhein in Catherby.",
    actions = { Action.ConversationHighlight:new("I have the weather report for you.") },
  },
  {
    text = "Talk to Captain Bleemadge on the White Wolf Mountain.",
    actions = { Action.ConversationHighlight:new("Hey there, did you get your T.R.A.S.H?") },
    postconditions = {
      Condition.ConversationText:new(
        " Oh yes, thanks...I've already installed it. I should go like the wind now! Tell Sanfew, that yakking old druid, I'll be happy to take him to the ogre area now... and yourself, of course, if you should like to go."
      ),
    },
  },
  {
    text = "Talk to Sanfew in Taverley.  (  If the player has completed 'Eadgar's Ruse.')",
    actions = {
      Action.ConversationHighlight:new("Hi there, the Gnome Pilot has agreed to take you to see the ogres!"),
      Action.ConversationHighlight:new("Hi there, the Gnome Pilot has agreed to take you to see the ogres!"),
    },
    postconditions = { Condition.ConversationText:new(" He's agreed to take me? Well, that's wonderful! Many thanks.") },
  },
  { text = "Talk to Hammerspike Stoutbeard in the Dwarven Mine. Kill his minions. Talk to him again." },
  { text = "Talk to Tassie Slipcast in Barbarian Village." },
  { text = "Form the unfired pot lid at the nearby potter's wheel." },
  { text = "Fire the pot lid in the pottery oven." },
  { text = "Use the pot lid on an empty pot to get an airtight pot." },
  {
    text = "Go to the apothecary in Varrock and give him the pot.<ul><li>Or if player has destroyed their Doctor's hat.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("Hey there! I have an air-tight pot for you!"),
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("Hey there! I have an air-tight pot for you!"),
    },
  },
  {
    text = "Go to Horvik (Varrock Platebody Shop) and give him the herbal tincture and breathing salts.",
    actions = {
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("I have the tincture and the breathing salts."),
    },
  },
  {
    text = "Talk to him again with five pigeon cages to get some chicken cages.",
    actions = {
      Action.ConversationHighlight:new("Talk about One Small Favour."),
      Action.ConversationHighlight:new("I have the five pigeon cages you asked for!"),
    },
  },
  { text = "Go to Seth Groats in the farm in Lumbridge and give him the chicken cages." },
  {
    text = "Go to the H.A.M. Hideout and talk to Johanhus.",
    actions = { Action.ConversationHighlight:new("You're in luck, I've managed to swing that chicken deal for you.") },
    postconditions = {
      Condition.ConversationText:new(
        " Hmmm. Congratulations on your achievement, your help will ensure the success of our venture and we can surely take our efforts forwards. Tell me, won't you consider joining our calling?"
      ),
    },
  },
  {
    text = "Go to Draynor Village and talk with Aggie the witch.",
    actions = { Action.ConversationHighlight:new("Talk about One Small Favour.") },
    postconditions = {
      Condition.ConversationText:new(
        " Yes! He's just returned! I'll happily be a character witness for Brian now! Please let him know that I'll help his friend however I can."
      ),
    },
  },
  {
    text = "Go to Port Sarim and talk with Brian in his axe shop (north-east of the lodestone), who gives you a sharpened hatchet.",
    actions = { Action.ConversationHighlight:new("I've returned with good news.") },
  },
  {
    text = "Go back to Shilo Village and speak with any jungle forester (closest fairy ring CKR), who gives you red mahogany logs in exchange for the hatchet.",
    actions = { Action.ConversationHighlight:new("Good news, I have your sharpened hatchet!") },
  },
  {
    text = "Speak with Yanni in Shilo Village.",
    actions = { Action.ConversationHighlight:new("Here's the red mahogany you asked for.") },
    postconditions = { Condition.ConversationText:new(" Well, that's absolutely wonderful my friend! Many thanks!") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "One Small Favour",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.long,
  releaseDate = 1109548800,
  prereqQuests = { "Shilo Village" },
})
