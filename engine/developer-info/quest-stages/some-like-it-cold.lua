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
    text = "Talk to Chuck the polar bear in Ardougne Zoo.",
    title = "Getting started",
  },
  {
    text = "Speak to him again.",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Some Like It Cold Quest") },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Speak to him a final time to be teleported",
    actions = { Action.ConversationHighlight:new("Some Like It Cold Quest"), Action.ConversationHighlight:new("Yes") },
  },
  { text = "After being teleported by Chuck, head west to the seals.", title = "The seal" },
  {
    text = "Wake up the seal that can be interacted with.",
    actions = {
      Action.ConversationHighlight:new("I'm looking for a polar bear."),
      Action.ConversationHighlight:new("How can I entertain you?"),
    },
  },
  { text = "Use the Dance or Jump For Joy emote." },
  {
    text = "Talk to the seal.",
    actions = {
      Action.ConversationHighlight:new("What's wrong with penguins?"),
      Action.ConversationHighlight:new("Where does your band live?"),
    },
  },
  { text = "Use the smaller boat to the north-east.", actions = { Action.ConversationHighlight:new("Yes.") } },
  { text = "Talk to Ping & Pong. You don't need to be in your tuxedo for this." },
  {
    text = "After receiving the items from them, do one of the following:<ul><li>Option one: Use a crafting table 4 in a player-owned house to make their rock costume. To return, use the Fremennik Province lodestone and head north to board the ship to iceberg.</li><li>Option two: Head to the north-east corner of the Iceberg and right-click Jim and select 'tuxedo-time'. Head to the north-west and investigate the avalanche of snow. Follow the main hall to the end and talk to the gate guard in the booth. After proceeding through the door, head east and then follow the western wall in the war-room to the western workbench. Craft Ping & Pong's costumes there. To get out of the room click the red button west of the gate. Go to Jim to get out of tuxedo.</li></ul>",
  },
  { text = "Give Ping & Pong their costumes." },
  { text = "Board the nearby boat to return to the seals.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Talk to the seal, bank any items and head to the camp. There is a deposit box option when talking to the seal.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to Teddy, the polar bear in a clown costume, in the centre of the camp.",
    title = "Prisoner of Walrus Camp",
  },
  { text = "Talk to Larry, north of Teddy" },
  { text = "Talk to Teddy again." },
  { text = "Talk to Noodle outside the south-west building." },
  { text = "Try to open the door to the southern building where the Walrus is in." },
  {
    text = "Take the bat in the box (slightly west of the door where the walrus is) from the front of the building.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Encourage Teddy, Ping & Pong, and Bouncer to make noise by clicking on them repeatedly for a few minutes. Their bars should all be blue, so it's a good idea to walk in circles.",
  },
  { text = "When the Walrus is outside, enter his building and steal the egg from the table." },
  { text = "Give the egg to Larry in the north." },
  { text = "Talk to Teddy." },
  {
    text = "Talk to all six of the unnamed penguins. (Four in the courtyard, one between the north and north-west buildings and one on fishing grate in the north-east corner)",
  },
  { text = "Talk to Teddy." },
  { text = "Talk to Plaza outside the easternmost building." },
  {
    text = "Collect the following items:<ul><li>Fish the fishing hole in the north-east until you receive a shark tooth and squid.</li><li>Take a board from the wood scrap pile north of the easternmost building.</li><li>Search the shelves in the easternmost building for olive oil, popsicle tray, and a rock carving hammer.</li><li>Right click Teddy and 'Tear off fabric'.</li><li>Search the shelves in the south-west building for lye and a cork screw.</li></ul>",
  },
  {
    text = "Once all of the supplies are obtained:<ul><li>Use the lye on the olive oil.</li><li>Use the unmoulded soap on the popsicle tray.</li><li>Use the squid on the popsicle tray.</li><li>Use the hammer on the soap.</li><li>With a shark tooth in your backpack, use the ripped fabric on the board.</li></ul>",
  },
  { text = "Talk to Plaza." },
  { text = "Talk to Teddy." },
  {
    text = "Talk to Astoria outside the easternmost building.",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = 'Defeat Astoria in a game of Battlefish.<ul><li>Place your fish anywhere on the board.</li><li>You can click on the \'HIT\' and \'MISS\' notifications to speed up this section considerably. But beware spam clicking may exit the game making you start over.</li><li>Solution:</li></ul><table style="border: 1px solid white; width=100%"><caption>Astoria\'s ship locations</caption><tbody><tr><th style="width=100%"></th><th style="width=100%">A</th><th style="width=100%">B</th><th style="width=100%">C</th><th style="width=100%">D</th><th style="width=100%">E</th><th style="width=100%">F</th><th style="width=100%">G</th><th style="width=100%">H</th><th style="width=100%">I</th><th style="width=100%">J</th></tr><tr><th>1</th><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><th>2</th><td></td><td></td><td></td><td></td><td></td><td></td><td style="color:Tomato;"> X </td><td></td><td></td><td></td></tr><tr><th>3</th><td></td><td></td><td></td><td></td><td></td><td></td><td style="color:Tomato;"> X </td><td></td><td></td><td></td></tr><tr><th>4</th><td></td><td></td><td></td><td></td><td></td><td></td><td style="color:Tomato;"> X </td><td></td><td></td><td></td></tr><tr><th>5</th><td></td><td></td><td style="color:Tomato;"> X </td><td style="color:Tomato;"> X </td><td style="color:Tomato;"> X </td><td></td><td style="color:Tomato;"> X </td><td></td><td></td><td></td></tr><tr><th>6</th><td></td><td></td><td></td><td></td><td style="color:Tomato;"> X </td><td style="color:Tomato;"> X </td><td style="color:Tomato;"> X </td><td style="color:Tomato;"> X </td><td></td><td></td></tr><tr><th>7</th><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><th>8</th><td></td><td></td><td style="color:Tomato;"> X </td><td style="color:Tomato;"> X </td><td style="color:Tomato;"> X </td><td></td><td></td><td></td><td></td><td></td></tr><tr><th>9</th><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr><tr><th>10</th><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr></tbody></table>',
    title = "Reparations",
  },
  { text = "In the small building just south-east, take from the circus prop crate on the northern wall." },
  { text = "Talk to Teddy." },
  {
    text = "To repair the engine:<ul><li>Use Bowling pin -> lever opening</li><li>Use Hula hoop -> broken valve wheel</li><li>Use Circus plate -> pressure gauge</li><li>Use Foam finger -> pressure gauge</li><li>Use Balloon -> metal wire</li><li>Use Insulated wire -> wirebox</li><li>Use Cloth -> cracked pipe</li></ul>",
  },
  { text = "Talk to Teddy.", actions = { Action.ConversationHighlight:new("No, I know what needs to be done.") } },
  {
    text = "Defeat the seals in Battlefish (if you accidentally exit, click on the periscope through the door to the left).<ul><li>Solution:</li></ul>",
  },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Some Like It Cold",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.longverylong,
  releaseDate = 1344297600,
  prereqQuests = { "Hunt for Red Raktuber" },
})
