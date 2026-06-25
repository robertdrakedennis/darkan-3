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
    text = "Talk to Kennith in the house west of the church in Witchaven (Fairy ring BLR or Cape of legends and run south).",
    title = "Return of the slugs?",
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  { text = "Climb down the Old ruin entrance to the west." },
  { text = "Enter the wall opening on the eastern wall immediately as you enter." },
  { text = "Navigate through the tunnel. Take the agility shortcut near the entrance if you have the level for it." },
  { text = "Climb over the rocks and talk to one of the villagers." },
  { text = "Climb back over the rocks and a cutscene will occur." },
  { text = "Talk to Kennith again." },
  { text = "Talk to Kennith's parents, Kent and Caroline, east of the church." },
  { text = "Travel north to the entrance of the Legends' Guild (or teleport with Cape of legends) and talk to Kent." },
  {
    text = "Travel back to Witchaven and talk to Kimberly in the northernmost house next to Jeb and Holgart.",
    actions = { Action.ConversationHighlight:new("Of course I will.") },
    postconditions = {
      Condition.ConversationText:new(
        " Thank you, but please don't run off this time! Hang on, it's that sound again... Oh no, it sounds like they're coming to get me!"
      ),
    },
  },
  {
    text = "After the cutscene, run around the house to get all four villagers to follow you. (Note: the following must be done without leaving the area, or exiting to the lobby or it must be done over!)",
  },
  { text = "Trap the villagers inside the church." },
  {
    text = "Talk to Kimberly again  and follow her to Kennith's house.",
    actions = { Action.ConversationHighlight:new("Okay, I've locked them all up.") },
    postconditions = {
      Condition.ConversationText:new(
        "(If not all four villagers are trapped in the church:) No you have not, you big liar! They're still out there... I can hear them!"
      ),
    },
  },
  { text = "Talk to Kimberly." },
  { text = "After the cutscene, get the villagers to follow you and trap them in the church, again." },
  {
    text = "Talk to Kimberly again , then follow her to Ezekial Lovecraft's house.",
    actions = { Action.ConversationHighlight:new("Okay, I've locked them all up.") },
    postconditions = {
      Condition.ConversationText:new(
        "(If not all four villagers are trapped in the church:) No you have not, you big liar! They're still out there... I can hear them!"
      ),
    },
  },
  { text = "Talk to Kimberly." },
  { text = "Talk to Kimberly again." },
  {
    text = "Before continuing, if you are currently under the effect of Sign of the Porter or similar buff, make sure to disable the buff.",
  },
  { text = "Head back to the Old ruin entrance, through the wall opening, over the rocks." },
  { text = "Dismiss any active followers." },
  { text = "Talk to one of the villagers." },
  { text = "Once through the dark entrance, talk to Ezekial." },
  { text = "Go through the northern wooden door." },
  {
    text = "When the three minecart villagers turn their back to you, run south to the southern wall and hide there.",
    title = "First room",
  },
  {
    text = "Wait for the southern villager to face the barrel, then run past him to the three nooks in the eastern wall. Hide inside these nooks to remain undetected.",
  },
  {
    text = "While the northern and southern villagers have their backs turned, prospect the wall until you find a crack (The piece of wall that will have the crack has a slightly different texture on top). Mine the wall, then enter the hole.",
  },
  {
    text = "Run to the other side of the tunnel and pull the lever. Ezekial Lovecraft will enter the area and follow you.",
  },
  { text = "Run back to the eastern side of the tunnel and open the wooden door." },
  { text = "Speak with Kent or Caroline." },
  {
    text = "Talk to Ezekial. Disable any items that would automatically bank ores, such as signs of the porter.",
    title = "Second room",
  },
  { text = "Open the eastern door." },
  {
    text = "Each time the villagers face and walk towards the middle is when you move.<ul><li>Harvest up to 2 Rubium along the Northern wall the first time they face the middle, then take cover behind the NE Wall.</li><li>Harvest another 2 Rubium giving you 4 total and take cover behind a wall.</li><li>Then each time the villagers face and walk towards the middle, Try to fill one Steam vent.</li><li>Repeat this 4 times until the villagers pass out</li><li>Repeat this 4 times until the villagers pass out</li></ul>",
  },
  { text = "Go through the wooden door." },
  { text = "Talk to Ezekial.", title = "Third room" },
  {
    text = "A quick way to proceed to the next room is as follows:<ul><li>As soon as the mine-cart villagers turn their back, break through the rock in the middle of the room - hide one tile east of where the rock was.</li><li>Wait for the north-eastern villager to face to the east and for the door villager to face the door, run north and mine rubium here, while walking behind the patrolling villager (4).</li><li>When the door villager turns to face the door, and the southern villager is not looking, run past him to the south and put the rubium in the south-eastern vent.</li><li>Wait for the door villager to face the door again, then run back to the middle and hide.</li><li>When the mine-cart villagers turn their back, run to the south vent and drop the rubium in, then quickly hide in the middle.</li><li>When the mine-cart villagers turn south again, run north to place the last rubium in both northern vents. They should all pass out now.</li></ul>",
  },
  { text = "Go through the wooden door." },
  { text = "Talk to Katherine or Clive.", title = "The new menace" },
  { text = "Backtrack through the rooms to the tunnel with the lever. Go through the metal door on the western side." },
  { text = "Talk to Kennith." },
  { text = "After the cutscene, talk to Kimberly to find the location of the toy train." },
  {
    text = "Return to Witchaven and find the toy train. It will be on the ground, but there will not be a red dot on the minimap. Going into game settings > Additional Options > Accessibility and turning on entity highlight will make the toy train easier to find, appearing as the color you have set for interactable items. It can be at one of the following locations:<ul><li>The North-East building nearest to the dock, outside the northern door, beside the fishing net.</li><li>Outside the building, on the north side beside the fishing net.</li><li>Near the pillar outside Mayor Hobb's house (which is on the west side).</li><li>By the water (south of the town).</li><li>South of the house with the well.</li></ul>",
  },
  { text = "Return to the room with Kennith and use the toy train on him." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Kennith's Concerns",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1205193600,
  prereqQuests = { "The Slug Menace" },
})
