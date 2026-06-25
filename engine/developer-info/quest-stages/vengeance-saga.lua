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
    text = "Find a collection of swords while raiding the 12-17 abandoned floors of Daemonheim, and recover memory of them.",
    title = "Beginning of the story",
  },
  { text = "You need to bank everything, including the ring of kinship." },
  {
    text = "Talk to Skaldrun and ask him to tell you a story.",
    actions = {
      Action.ConversationHighlight:new("Forgot what he says here"),
      Action.ConversationHighlight:new("Tell me a story"),
    },
  },
  { text = "Head south through the door and kill all 4 Forgotten warriors.", title = "Noble path" },
  {
    text = "As Vengeance (a forgotten warrior) head back to the starting room and talk to Orin.",
    actions = { Action.ConversationHighlight:new("Give Orin the fish, he needs it more than you do.") },
    postconditions = { Condition.ConversationText:new("Player has giant flatfish removed from them.") },
  },
  { text = "Continue south, unlock and open the silver crescent door with the newly acquired key." },
  { text = "Head inside and kill Lotheria Seldorina." },
  { text = "Pick up the food and Lotheria's ring of kinship." },
  { text = "Solve the puzzle by flipping all the tiles to one colour. Right click the tiles to force change them." },
  { text = "Open the east door.<ul><li>Optional: Kill both dungeon spiders for extra food.</li></ul>" },
  { text = "Continue east through the door." },
  { text = "Kill Ican Haz and do not pull the lever." },
  { text = "Pick up the food, Icans ring of kinship and the blue triangle key." },
  { text = "Head back west, unlock the Blue triangle door and enter through it." },
  { text = "Kill the hellhounds for antipoison elixir and drink it, this will only temporarily stop the poison." },
  {
    text = "Continue north, kill the dungeon spider and talk to Argax.",
    actions = { Action.ConversationHighlight:new("Let her die. She's earned her rest.") },
  },
  { text = "Continue east and then south to reach a lever room." },
  {
    text = "Pull all five levers within the time limit and continue east then north.<ul><li>Optional: Kill the hellhound for more antipoison elixir or the dungeon spider for food.</li></ul>",
  },
  { text = "Talk to Korel.", actions = { Action.ConversationHighlight:new("Let Korel go, he's learned his lesson.") } },
  { text = "Continue north and kill Lola Wut, pick up the crimson rectangle key, food and Lola's ring of kinship." },
  { text = "Continue through the west door." },
  { text = "Investigate Shianna's body then continue north and east." },
  {
    text = "Talk to Peleas.",
    actions = {
      Action.ConversationHighlight:new("Lie to him. Give him comfort at the end."),
    },
  },
  { text = "Head west, unlock and continue through the Crimson rectangle door and then continue once more west." },
  {
    text = "Attack and then show Kay Thanxby the corpses.",
    actions = { Action.ConversationHighlight:new("Confront Kay's lies, show her the corpses.") },
  },
  { text = "Head south through the door and kill all 4 Forgotten warriors." },
  {
    text = "As Vengeance (a forgotten warrior) head back to the starting room and talk to Orin.",
    actions = { Action.ConversationHighlight:new("Give Orin the fish, he needs it more than you do.") },
    postconditions = { Condition.ConversationText:new("Player has giant flatfish removed from them.") },
  },
  { text = "Continue south, unlock and open the silver crescent door with the newly acquired key." },
  { text = "Head inside and kill Lotheria Seldorina." },
  { text = "Pick up the food and Lotheria's ring of kinship." },
  { text = "Solve the puzzle by flipping all the tiles to one colour. Right click the tiles to force change them." },
  { text = "Open the east door.<ul><li>Optional: Kill both dungeon spiders for extra food.</li></ul>" },
  { text = "Continue east through the door." },
  { text = "Kill Ican Haz and do not pull the lever." },
  { text = "Pick up the food, Icans ring of kinship and the blue triangle key." },
  { text = "Head back west, unlock the Blue triangle door and enter through it." },
  { text = "Kill the hellhounds for antipoison elixir and drink it, this will only temporarily stop the poison." },
  {
    text = "Continue north, kill the dungeon spider and talk to Argax.",
    actions = { Action.ConversationHighlight:new("Let her die. She's earned her rest.") },
  },
  { text = "Continue east and then south to reach a lever room." },
  {
    text = "Pull all five levers within the time limit and continue east then north.<ul><li>Optional: Kill the hellhound for more antipoison elixir or the dungeon spider for food.</li></ul>",
  },
  { text = "Talk to Korel.", actions = { Action.ConversationHighlight:new("Let Korel go, he's learned his lesson.") } },
  { text = "Continue north and kill Lola Wut, pick up the crimson rectangle key, food and Lola's ring of kinship." },
  { text = "Continue through the west door." },
  { text = "Investigate Shianna's body then continue north and east." },
  {
    text = "Talk to Peleas.",
    actions = {
      Action.ConversationHighlight:new("Lie to him. Give him comfort at the end."),
    },
  },
  { text = "Head west, unlock and continue through the Crimson rectangle door and then continue once more west." },
  {
    text = "Attack and then show Kay Thanxby the corpses.",
    actions = { Action.ConversationHighlight:new("Confront Kay's lies, show her the corpses.") },
  },
  {
    text = "As Vengeance (a forgotten warrior) head back to the starting room and talk to Orin.",
    actions = { Action.ConversationHighlight:new("Give Orin the fish, he needs it more than you do.") },
    postconditions = { Condition.ConversationText:new("Player has giant flatfish removed from them.") },
  },
  { text = "Continue south, unlock and open the silver crescent door with the newly acquired key." },
  { text = "Head inside and kill Lotheria Seldorina." },
  { text = "Pick up the food and Lotheria's ring of kinship." },
  { text = "Solve the puzzle by flipping all the tiles to one colour. Right click the tiles to force change them." },
  { text = "Open the east door.<ul><li>Optional: Kill both dungeon spiders for extra food.</li></ul>" },
  { text = "Continue east through the door." },
  { text = "Kill Ican Haz and do not pull the lever." },
  { text = "Pick up the food, Icans ring of kinship and the blue triangle key." },
  { text = "Head back west, unlock the Blue triangle door and enter through it." },
  { text = "Kill the hellhounds for antipoison elixir and drink it, this will only temporarily stop the poison." },
  {
    text = "Continue north, kill the dungeon spider and talk to Argax.",
    actions = { Action.ConversationHighlight:new("Let her die. She's earned her rest.") },
  },
  { text = "Continue east and then south to reach a lever room." },
  {
    text = "Pull all five levers within the time limit and continue east then north.<ul><li>Optional: Kill the hellhound for more antipoison elixir or the dungeon spider for food.</li></ul>",
  },
  { text = "Talk to Korel.", actions = { Action.ConversationHighlight:new("Let Korel go, he's learned his lesson.") } },
  { text = "Continue north and kill Lola Wut, pick up the crimson rectangle key, food and Lola's ring of kinship." },
  { text = "Continue through the west door." },
  { text = "Investigate Shianna's body then continue north and east." },
  {
    text = "Talk to Peleas.",
    actions = {
      Action.ConversationHighlight:new("Lie to him. Give him comfort at the end."),
    },
  },
  { text = "Head west, unlock and continue through the Crimson rectangle door and then continue once more west." },
  {
    text = "Attack and then show Kay Thanxby the corpses.",
    actions = { Action.ConversationHighlight:new("Confront Kay's lies, show her the corpses.") },
  },
  { text = "Head south through the door and kill all 4 Forgotten warriors.", title = "Ruthless path" },
  {
    text = "As Vengeance (a forgotten warrior) head back to the starting room and talk to Orin.",
    actions = { Action.ConversationHighlight:new("Keep the fish, you need your strength.") },
    postconditions = { Condition.ConversationText:new("Player receives silver crescent key.") },
  },
  { text = "Continue south, unlock and open the silver crescent door with the newly acquired key." },
  { text = "Head inside and kill Lotheria Seldorina sneaking behind pillars." },
  { text = "Pick up the food and Lotheria's ring of kinship." },
  { text = "Solve the puzzle by flipping all the tiles to one colour." },
  { text = "Open the east door.<ul><li>Optional: Kill both dungeon spiders for extra food.</li></ul>" },
  { text = "Continue east through the door." },
  { text = "Pull the lever, this summons a horde of skeletons to kill Ican Haz." },
  { text = "Pick up the food, Icans ring of kinship and the Blue triangle key." },
  { text = "Head back west, unlock the Blue triangle door and enter through it." },
  { text = "Kill the hellhounds for antipoison exlir and drink it, this is only temporarily stop the poison." },
  {
    text = "Continue north, kill the dungeon spider and talk to Argax.",
    actions = { Action.ConversationHighlight:new("Keep her awake. She has to fight for life.") },
  },
  { text = "Continue east and then south to reach a lever room." },
  {
    text = "Pull all five levers within the time limit and continue east then north.<ul><li>Optional: Kill the hellhound for more antipoison exlir or the dungeon spider for food.</li></ul>",
  },
  {
    text = "Talk to Korel.",
    actions = { Action.ConversationHighlight:new("Kill Korel, prevent him from harming again.") },
  },
  { text = "Continue north and search the table in the south east corner." },
  {
    text = "Head west and use the poison on the fishing spot, then continue north and east to gain visibility of all four rooms. Return to the starting room and wait for Lola to do a full rotation and eat the poisoned fish. This will damage her for 1999 lifepoints and head back to the room to kill Lola Wut for 1 lifepoint. Pick up the crimson rectangle key, food and Lola's ring of kinship.",
  },
  { text = "Investigate Shianna's body in the south-western part of the room then continue north and east." },
  {
    text = "Talk to Peleas.",
    actions = { Action.ConversationHighlight:new("Tell him the truth. He must understand their brutality.") },
  },
  { text = "Head west, unlock and continue through the Crimson rectangle door and then continue once more west." },
  { text = "Kill Kay Thanxby.", actions = { Action.ConversationHighlight:new("Make her suffer.") } },
  { text = "Head south through the door and kill all 4 Forgotten warriors." },
  {
    text = "As Vengeance (a forgotten warrior) head back to the starting room and talk to Orin.",
    actions = { Action.ConversationHighlight:new("Keep the fish, you need your strength.") },
    postconditions = { Condition.ConversationText:new("Player receives silver crescent key.") },
  },
  { text = "Continue south, unlock and open the silver crescent door with the newly acquired key." },
  { text = "Head inside and kill Lotheria Seldorina sneaking behind pillars." },
  { text = "Pick up the food and Lotheria's ring of kinship." },
  { text = "Solve the puzzle by flipping all the tiles to one colour." },
  { text = "Open the east door.<ul><li>Optional: Kill both dungeon spiders for extra food.</li></ul>" },
  { text = "Continue east through the door." },
  { text = "Pull the lever, this summons a horde of skeletons to kill Ican Haz." },
  { text = "Pick up the food, Icans ring of kinship and the Blue triangle key." },
  { text = "Head back west, unlock the Blue triangle door and enter through it." },
  { text = "Kill the hellhounds for antipoison exlir and drink it, this is only temporarily stop the poison." },
  {
    text = "Continue north, kill the dungeon spider and talk to Argax.",
    actions = { Action.ConversationHighlight:new("Keep her awake. She has to fight for life.") },
  },
  { text = "Continue east and then south to reach a lever room." },
  {
    text = "Pull all five levers within the time limit and continue east then north.<ul><li>Optional: Kill the hellhound for more antipoison exlir or the dungeon spider for food.</li></ul>",
  },
  {
    text = "Talk to Korel.",
    actions = { Action.ConversationHighlight:new("Kill Korel, prevent him from harming again.") },
  },
  { text = "Continue north and search the table in the south east corner." },
  {
    text = "Head west and use the poison on the fishing spot, then continue north and east to gain visibility of all four rooms. Return to the starting room and wait for Lola to do a full rotation and eat the poisoned fish. This will damage her for 1999 lifepoints and head back to the room to kill Lola Wut for 1 lifepoint. Pick up the crimson rectangle key, food and Lola's ring of kinship.",
  },
  { text = "Investigate Shianna's body in the south-western part of the room then continue north and east." },
  {
    text = "Talk to Peleas.",
    actions = { Action.ConversationHighlight:new("Tell him the truth. He must understand their brutality.") },
  },
  { text = "Head west, unlock and continue through the Crimson rectangle door and then continue once more west." },
  { text = "Kill Kay Thanxby.", actions = { Action.ConversationHighlight:new("Make her suffer.") } },
  {
    text = "As Vengeance (a forgotten warrior) head back to the starting room and talk to Orin.",
    actions = { Action.ConversationHighlight:new("Keep the fish, you need your strength.") },
    postconditions = { Condition.ConversationText:new("Player receives silver crescent key.") },
  },
  { text = "Continue south, unlock and open the silver crescent door with the newly acquired key." },
  { text = "Head inside and kill Lotheria Seldorina sneaking behind pillars." },
  { text = "Pick up the food and Lotheria's ring of kinship." },
  { text = "Solve the puzzle by flipping all the tiles to one colour." },
  { text = "Open the east door.<ul><li>Optional: Kill both dungeon spiders for extra food.</li></ul>" },
  { text = "Continue east through the door." },
  { text = "Pull the lever, this summons a horde of skeletons to kill Ican Haz." },
  { text = "Pick up the food, Icans ring of kinship and the Blue triangle key." },
  { text = "Head back west, unlock the Blue triangle door and enter through it." },
  { text = "Kill the hellhounds for antipoison exlir and drink it, this is only temporarily stop the poison." },
  {
    text = "Continue north, kill the dungeon spider and talk to Argax.",
    actions = { Action.ConversationHighlight:new("Keep her awake. She has to fight for life.") },
  },
  { text = "Continue east and then south to reach a lever room." },
  {
    text = "Pull all five levers within the time limit and continue east then north.<ul><li>Optional: Kill the hellhound for more antipoison exlir or the dungeon spider for food.</li></ul>",
  },
  {
    text = "Talk to Korel.",
    actions = { Action.ConversationHighlight:new("Kill Korel, prevent him from harming again.") },
  },
  { text = "Continue north and search the table in the south east corner." },
  {
    text = "Head west and use the poison on the fishing spot, then continue north and east to gain visibility of all four rooms. Return to the starting room and wait for Lola to do a full rotation and eat the poisoned fish. This will damage her for 1999 lifepoints and head back to the room to kill Lola Wut for 1 lifepoint. Pick up the crimson rectangle key, food and Lola's ring of kinship.",
  },
  { text = "Investigate Shianna's body in the south-western part of the room then continue north and east." },
  {
    text = "Talk to Peleas.",
    actions = { Action.ConversationHighlight:new("Tell him the truth. He must understand their brutality.") },
  },
  { text = "Head west, unlock and continue through the Crimson rectangle door and then continue once more west." },
  { text = "Kill Kay Thanxby.", actions = { Action.ConversationHighlight:new("Make her suffer.") } },
}

return Quest:new({
  name = "Vengeance (saga)",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1306886400,
  prereqQuests = { "Skaldrun" },
})
