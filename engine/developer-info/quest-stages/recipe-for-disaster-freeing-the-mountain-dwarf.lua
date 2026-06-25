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
    text = "Inspect the Dwarf.",
    title = "Freeing the Dwarf",
  },
  { text = "Head to the Rising Sun Inn pub in Falador." },
  {
    text = "Ask Emily about dwarves and ale (wear Ring of charos (a) if you have it).<ul><li>If wearing Ring of charos (a):</li></ul>",
    actions = {
      Action.ConversationHighlight:new("What can you tell me about dwarves and ale?"),
      Action.ConversationHighlight:new("I could offer you some in return, how about 200 gold?"),
      Action.ConversationHighlight:new("What can you tell me about dwarves and ale?"),
      Action.ConversationHighlight:new("(Persuade)I'm really quite trustworthy once you get to know me."),
    },
  },
  { text = "Head to The Pick and Lute pub in Taverley." },
  { text = "Buy 4 Asgarnian ales from Tostig." },
  {
    text = "Add 1 gold coin to each glass of ale. (you can take coins out of your money pouch by right clicking the price checker in your inventory and selecting withdraw)",
  },
  { text = "Head to the tunnel under White Wolf Mountain (cave north of Taverley lodestone)." },
  { text = "Talk to the Old Dwarf, Rohak. You must finish this dialogue to proceed." },
  {
    text = "Use 4 Asgoldian ales on him and then after the fade, talk to him. You may need to repeat this process if you either: exit the cave before the next step, or do not give him the four ales quickly enough.",
  },
  {
    text = "Talk to Rohak again to give him one-hundred coins, a bucket of milk, an egg, a pot of flour, and a bowl of water.",
  },
  {
    text = "Quickly pick up the Dwarven rock cake whilst wearing gloves.<ul><li>If the cake is not picked up fast enough, the ingredients are required to make another one.</li></ul>",
  },
  {
    text = "Cooling the cake:<ul><li>If ice gloves were worn whilst picking up the cake, simply return to Lumbridge.</li><li>If ice gloves were not worn, either drop the cake and pick it up with ice gloves equipped or kill an icefiend with it in your inventory.</li></ul>",
  },
  { text = "Use the cooled rock cake on the Dwarf.", title = "Finishing up" },
  { text = "Subquest complete!" },
}

return Quest:new({
  name = "Recipe for Disaster: Freeing the Mountain Dwarf",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.short,
  releaseDate = 1142380800,
  prereqQuests = { "Recipe for Disaster: Another Cook's Quest", "Fishing Contest" },
})
