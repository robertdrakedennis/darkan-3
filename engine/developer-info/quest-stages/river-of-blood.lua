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
    text = "Talk to King Roald in Varrock Palace.",
    title = "Preparations for Aeonisig",
    neededItems = {
      ["Steel med helm"] = { quantity = 1 },
      ["Steel platebody"] = { quantity = 1 },
      ["Steel platelegs"] = { quantity = 1 },
      ["Blisterwood sickle"] = { quantity = 1 },
      ["Blisterwood polearm"] = { quantity = 1 },
    },
    recommendedItems = {},
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Talk about River of Blood.") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Talk to Drezel in the Paterdomus mausoleum west of Canifis (fairy ring CKS, invitation box or Skeletal horror Teleport for quick access).<ul><li>Reclaim the wolfbane dagger if you had previously destroyed it.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("Talk about something else."),
      Action.ConversationHighlight:new("[Any option]"),
    },
  },
  {
    text = "Talk to Aeonisig south of the temple. You can use the invitation box to get there. Otherwise, exit the building on the west side and head towards the beacon (or exit east side and use agility shortcut). He is at the bottom of the stairs.",
  },
  { text = "Defeat the 3 Vyrewatches using blisterwood weapons." },
  {
    text = "Talk to Aeonisig.",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("Farewell.") },
  },
  { text = "Talk to Captain Rovin (one of the three guards slightly north east) to supply him with armour." },
  { text = "Kill all Zamorak monks in the temple - you can use any combat style/gear here." },
  { text = "Kill the Zamorakian leader and bodyguard on the top floor - you can use any combat style/gear here." },
  {
    text = "Return and talk to Aeonisig. Go through all the chat options.",
    actions = { Action.ConversationHighlight:new("[Any option]") },
  },
  {
    text = "Talk to Drezel.",
    title = "Finding the scroll",
    neededItems = {
      ["Blisterwood logs"] = { quantity = 1 },
      ["Super restore (3)"] = { quantity = 1 },
      ["Guthix balance"] = { quantity = 1 },
      ["Ectophial"] = { quantity = 1 },
    },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Talk About something else") },
  },
  {
    text = "Climb-down the nearby trapdoor.<ul><li>If the trapdoor isn't open, use the temple library key on the keyhole which can be obtained from Drezel.</li></ul>",
  },
  {
    text = "Pull 7 books off the bookcases in the correct order until you find the Blood of the Covenant:<ul><li>Lumbridge</li><li>Varrock</li><li>Edgeville</li><li>Falador</li><li>Rimmington</li><li>Taverley</li><li>Draynor</li><li>'Blood of the Covenant'</li></ul>",
  },
  { text = "Read the book." },
  { text = "Climb-up the ladder." },
  { text = "Talk to Ivan Strom." },
  {
    text = "Enter the trapdoor behind Canifis pub, search the southern wall in the room and take the first cave entrance on the east side of the tunnel.<ul><li>If players are taken to Mort'ton they need to go to the Temple Trekking noticeboard next to the temple west of Canifis, click on the medium NPC and on the right hand side, they'll see the NPC with level 99, which is rewarded with the ability to toggle a shortcut from the trapdoor. After toggling it the door will function as normal or the boat can be taken back to the swamp and run to the cellar doors from here to access the tomb without toggling the function.</li></ul>",
  },
  { text = "Use a Guthix balance on the tomb and open it." },
  { text = "Open the scroll case and read the Scroll of Balance." },
  { text = "Burn a Blisterwood log and take the ashes. They spawn immediately upon lighting the fire." },
  { text = "Grind the silvthril bar at the ectofuntus grinder." },
  { text = "Use the silvthril dust on the Altar of Nature in the Nature Grotto." },
  {
    text = "Use any of the three ingredients on a super restore (3) to make a super Guthix balance potion. Keep the Ivandis's serum in your inventory for later use.",
  },
  {
    text = "Talk to Drezel.",
    title = "Clash of armies",
    neededItems = { ["Super Guthix balance"] = { quantity = 1 }, ["Wyrd"] = { quantity = 1 } },
    recommendedItems = {},
    actions = { Action.ConversationHighlight:new("Talk about something else.") },
  },
  { text = "Pour the Super Guthix balance into the well in the main room to the west." },
  {
    text = "Talk to Aeonisig south of the church.",
    actions = {
      Action.ConversationHighlight:new("About equipping the Varrock guard..."),
      Action.ConversationHighlight:new("About claiming the Paterdomus temple..."),
      Action.ConversationHighlight:new("[Any option]"),
      Action.ConversationHighlight:new("About strengthening the Salve barrier..."),
      Action.ConversationHighlight:new("I should investigate this."),
    },
  },
  {
    text = "Talk to Vanescula on the bridge.<ul><li>Use the the broken Saradomin statue shortcut to the east if you have 65 Agility or teleport to the fairy ring CKS.</li></ul>",
    actions = { Action.ConversationHighlight:new("[Any option]"), Action.ConversationHighlight:new("[Any option]") },
  },
  { text = "Kill the feral vampyres." },
  {
    text = "Kill the Wyrd while stepping away from its special attacks.<ul><li>The Sonic Wave attack deals 7,500 damage when praying melee. Otherwise, it will deal 15,000 damage. This can be avoided by stepping at least two squares back. It is also important to note that if you step off the bridge at any time you will have to redo the fight.</li></ul>",
  },
  { text = "Talk to Ivan Strom on the bridge over the salve river." },
  {
    text = "Travel to Burgh de Rott via Drakan's medallion.",
    title = "Combatting haemalchemy",
    neededItems = {
      ["Sunspear (melee)"] = { quantity = 1 },
      ["Wolfbane dagger"] = { quantity = 1 },
      ["Blisterwood logs"] = { quantity = 1 },
      ["Ivandis's serum"] = { quantity = 1 },
    },
    recommendedItems = {},
  },
  {
    text = "Travel on the boat south of Burgh de Rott to the Icyene graveyard.",
    actions = { Action.ConversationHighlight:new("Icyene graveyard.") },
  },
  { text = "Talk to Efaritay." },
  { text = "Inspect the Queen Efaritay statue within the northern crypt, and take the pendant." },
  { text = "Talk to Efaritay." },
  { text = "Craft a Blisterwood shaft from blisterwood logs." },
  { text = "Use the shaft on the sunspear with the wolfbane dagger and blisterwood shaft in your inventory." },
  { text = "Talk to Efaritay." },
  { text = "Teleport to the Meiyerditch Laboratories via Drakan's medallion." },
  { text = "Talk to Sarius." },
  { text = "Run through the tunnel to the room with the mutated bloodveld." },
  { text = "Take the book from the research table on the north side and read it." },
  { text = "Talk to Sarius." },
  { text = "Climb the stairs and run north-west into the mine." },
  {
    text = "With your vampyre disguise on, talk to a guard about the refinery. (if you don't have your disguise on you must manually fill the cart up with 15 ores yourself)",
  },
  {
    text = "Talk to one of the Juvinate guards.",
    actions = { Action.ConversationHighlight:new("Where's the daeyalt refinery?") },
  },
  { text = "Push the cart." },
  { text = "Kill the juvinates." },
  { text = "Open the 5 cell doors." },
  {
    text = "Run north and talk to Emilia.",
    title = "Refinery puzzle",
    actions = { Action.ConversationHighlight:new("Let the anger flow through you.") },
  },
  { text = "Talk to Sorin.", actions = { Action.ConversationHighlight:new("Isn't this technology awesome?") } },
  {
    text = "Talk to Ileana.",
    actions = { Action.ConversationHighlight:new("I don't approve of this, and neither should you.") },
  },
  {
    text = "Talk to Razvan.",
    actions = { Action.ConversationHighlight:new("You should be ashamed. Have you no remorse?") },
  },
  { text = "Talk to Florin.", actions = { Action.ConversationHighlight:new("Cheer up. Don't give up hope.") } },
  {
    text = "Tithe the Shadum blood lock located on the south western part of the wall.",
    actions = { Action.ConversationHighlight:new("I accept my fate and put my hand in the machine.") },
  },
  { text = "Extract the Refined daeyalt from the refined daeyalt mine.", title = "Curing vampyrism" },
  { text = "Inspect the rejuvenation tank." },
  { text = "Climb the stairs and talk to Safalaan." },
  { text = "Kill the Skeleton Hellhounds." },
  { text = "Climb the stairs." },
  { text = "On Northern end of the room go into the Eastern door into a long, narrow hallway" },
  { text = "Go into the Library through a door on the southern wall of the hallway" },
  { text = "Take the bottle of holy water from the table (red dot on the map)." },
  { text = "Continue east through the narrow hallway." },
  { text = "Throw the bottle of holy water on Harold and climb the stairs." },
  { text = "Cut a log from the blisterwood tree." },
  { text = "Light the firepit in front of the Stone of Jas." },
  { text = "Roll the barrel of gunpowder." },
  { text = "Unlock the blood seal in the northern room." },
  { text = "Climb the stairs." },
  {
    text = "Kill Safalaan with the Sunspear.<ul><li>If you die, you will respawn safely in Darkmeyer, you can enter the boss fight again through the Gatehouse to the west. (This is a safe death for Hardcore Ironmen)</li></ul>",
  },
  { text = "Talk to Efaritay." },
  { text = "Use the daeyalt and Ivandis' serum on Safalaan's blood." },
  { text = "Talk to Efaritay." },
  { text = "Pour the extreme Guthix balance potion into the Paterdomus well." },
  { text = "Talk to Vanescula on the bridge." },
  { text = "Watch the cutscene and then talk to Vanescula once more." },
  {
    text = "Talk to King Roald.",
    actions = { Action.ConversationHighlight:new("Talk about River of Blood.") },
    postconditions = { Condition.ConversationText:new(" For now, at least. The brokered peace may not hold.") },
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "River of Blood",
  steps = steps,
  timeline = Enums.timeline.mythic,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1461542400,
  prereqQuests = { "The Lord of Vampyrium", "Defender of Varrock", "All Fired Up" },
})
