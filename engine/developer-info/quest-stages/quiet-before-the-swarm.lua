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
    text = "Speak to Sir Tiffy Cashien in the Falador Park about the void knights.",
    title = "Starting",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Void Knights.") },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestInterfaceOpen:new() },
    actions = { Action.ConversationHighlight:new("Void Knights.") },
  },
  {
    text = "[Accept Quest]<ul><li>Or, if Slug Menace isn't complete</li><li>[Accept Quest]</li></ul>",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Go to the Void Knights' Outpost.<ul><li>To get there, speak to the Squire on the pier at Port Sarim to travel to the island by ship free of charge.</li></ul>",
    actions = { Action.ConversationHighlight:new("I'd like to go to your outpost.") },
  },
  { text = "Speak to Commodore Matthias in the north-east building." },
  { text = "Talk to Captain Tyr just south of the building." },
  {
    text = "Talk to the following people about escaping pests:<ul><li>Knight Ami</li><li>Knight Bernard</li><li>Knight Diana</li><li>Knight Mikhal</li><li>Jessika</li><li>Mariah</li><li>Mrs Gord</li><li>Squire Sam</li><li>Terry Gord</li></ul>",
    actions = {
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("Have you heard anything about pests escaping?"),
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("Have you heard anything about pests escaping?"),
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("Have you heard anything about pests escaping?"),
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("Have you heard anything about pests escaping?"),
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("Have you heard anything about pests escaping?"),
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("Ask about escaped pest."),
      Action.ConversationHighlight:new("That's all, thanks."),
      Action.ConversationHighlight:new("Have you heard anything about pests escaping?"),
      Action.ConversationHighlight:new("That's all, thanks."),
    },
  },
  { text = "Talk to Captain Tyr.", title = "The accused" },
  {
    text = "Talk to Jessika about the crimes she has committed.",
    actions = {
      Action.ConversationHighlight:new("Confront about a crime."),
      Action.ConversationHighlight:new("You let a pest escape to the mainland."),
    },
  },
  { text = "Talk to Captain Tyr." },
  { text = "Talk to Commodore Matthias." },
  { text = "Agree during the cutscene.", actions = { Action.ConversationHighlight:new("Yes.") } },
  {
    text = "Solve the 3x3 slider puzzle. The border helps you find their correct positions.<ul><li>The centre piece will appear when you have placed the border pieces.</li></ul>",
    title = "Inside Jessika's mind",
  },
  { text = "Watch the cutscene and continue the dialogue with Commodore Mathias." },
  {
    text = "Bank all your items, both equipped and in your inventory. Dismiss any pets or familiars.",
    title = "Arresting Jessika",
  },
  {
    text = "Return to Sir Tiffy and talk about Void Knights.<ul><li>Or, if Slug Menace isn't complete</li></ul>",
    actions = { Action.ConversationHighlight:new("Void Knights."), Action.ConversationHighlight:new("Void Knights.") },
  },
  {
    text = "Talk to Commander Korasi.",
    title = "Bridge and the sword",
    actions = { Action.ConversationHighlight:new("Yes.") },
  },
  {
    text = "Talk to the toll booth to start the puzzle.",
    actions = { Action.ConversationHighlight:new("What do I need to do?") },
  },
  {
    text = "Complete the puzzle:<ul><li>Talk to Bernard and cross with him.</li><li>Talk to the toll booth to send yourself over.</li><li>Talk to Ami or Diana and have them cross together.</li><li>Talk to the toll booth to send Bernard over.</li><li>Talk to Bernard and cross with him.</li></ul>",
    actions = {
      Action.ConversationHighlight:new("I'll cross with you."),
      Action.ConversationHighlight:new("Send someone over the bridge."),
      Action.ConversationHighlight:new("Yourself"),
      Action.ConversationHighlight:new("You cross with [Diana/Ami]."),
      Action.ConversationHighlight:new("Send someone over the bridge."),
      Action.ConversationHighlight:new("Bernard"),
      Action.ConversationHighlight:new("I'll cross with you."),
    },
  },
  { text = "Right-click <i>assign</i> the people to the following:", title = "Ship positions" },
  {
    text = '<table class="wikitable lighttable"><tbody><tr><th>Name</th><th>Ship position</th><th>Thinking</th><th>Saw</th></tr><tr class=""><td><b>Ami</b></td><td>Rigging (</i> <span class="chat-options-underline" title="Ship position">1</span>•<span class="chat-options-underline" title="Rigging">4</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-e801e9426b1f1f89029eefdd8e8c0706" style="" title="Click for explanation, click again to close"></span></td><td>Sister (</i> <span class="chat-options-underline" title="Thinking">2</span>•<span class="chat-options-underline" title="Sister">4</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-d00c77dc9c1cf1a488517027ad4ac77d" style="" title="Click for explanation, click again to close"></span></td><td>Shark (</i> <span class="chat-options-underline" title="Saw">3</span>•<span class="chat-options-underline" title="Shark">2</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-38f4edccfdb52ad8b9fff43e6826942a" style="" title="Click for explanation, click again to close"></span></td></tr><tr class=""><td><b>Bernard</b></td><td>Crow\'s nest (</i> <span class="chat-options-underline" title="Ship position">1</span>•<span class="chat-options-underline" title="Crow\'s nest">5</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-66c24b7a16496b6d6248f9fa7550fe47" style="" title="Click for explanation, click again to close"></span></td><td>Lunch (</i> <span class="chat-options-underline" title="Thinking">2</span>•<span class="chat-options-underline" title="Lunch">1</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-aa50bc129363733dc1fcd38d7cc3a3ac" style="" title="Click for explanation, click again to close"></span></td><td>Seaweed (</i> <span class="chat-options-underline" title="Saw">3</span>•<span class="chat-options-underline" title="Seaweed">4</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-477fc2c03a89463d0ec72804ae9993b4" style="" title="Click for explanation, click again to close"></span></td></tr><tr><td><b>Diana</b></td><td>Main deck (</i> <span class="chat-options-underline" title="Ship position">1</span>•<span class="chat-options-underline" title="Main deck">2</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-1e5593b0a37a7e02519211e11e37286f" style="" title="Click for explanation, click again to close"></span></td><td>Fighting (</i> <span class="chat-options-underline" title="Thinking">2</span>•<span class="chat-options-underline" title="Fighting">5</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-7cda181e3b1f047fe5ac35bffa11e02c" style="" title="Click for explanation, click again to close"></span></td><td>Seagull (</i> <span class="chat-options-underline" title="Saw">3</span>•<span class="chat-options-underline" title="Seagull">1</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-b35447909d458268a2dfb7bf9203fe81" style="" title="Click for explanation, click again to close"></span></td></tr><tr><td><b>Korasi</b></td><td>Hold (</i> <span class="chat-options-underline" title="Ship position">1</span>•<span class="chat-options-underline" title="Hold">1</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-5275d044843a9ac8c2818684a6a2e59e" style="" title="Click for explanation, click again to close"></span></td><td>Promotion (</i> <span class="chat-options-underline" title="Thinking">2</span>•<span class="chat-options-underline" title="Promotion">3</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-3f647a293ae597674725c6e3d76e9b7c" style="" title="Click for explanation, click again to close"></span></td><td>Pest (</i> <span class="chat-options-underline" title="Saw">3</span>•<span class="chat-options-underline" title="Pest">5</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-09244d75458b2b5c35068183427c1a20" style="" title="Click for explanation, click again to close"></span></td></tr><tr><td><b>Commodore</b></td><td>Helm (</i> <span class="chat-options-underline" title="Ship position">1</span>•<span class="chat-options-underline" title="Helm">3</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-f328c191203fba9e3500aad84f316f4d" style="" title="Click for explanation, click again to close"></span></td><td>Jessika (</i> <span class="chat-options-underline" title="Thinking">2</span>•<span class="chat-options-underline" title="Jessika">2</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-cd008a698ae518106d14e36ff4142640" style="" title="Click for explanation, click again to close"></span></td><td>Clouds (</i> <span class="chat-options-underline" title="Saw">3</span>•<span class="chat-options-underline" title="Clouds">3</span>)</span><span class="js-tooltip-click chat-options-dialogue" data-tooltip-name="c_option-8f81e32edcdfe430dfd13bc7b7882552" style="" title="Click for explanation, click again to close"></span></td></tr></tbody><tfoot><tr><th colspan="4"><span class="ht-reset oo-ui-widget oo-ui-widget-enabled oo-ui-buttonElement oo-ui-buttonElement-framed oo-ui-iconElement oo-ui-labelElement oo-ui-buttonWidget"><a class="oo-ui-buttonElement-button" role="button" title="Removes all highlights from the table" tabindex="0" rel="nofollow"><span class="oo-ui-iconElement-icon oo-ui-icon-clear"></span><span class="oo-ui-labelElement-label">Clear selection<span class="ht-reset-counter"> (0/5)</span></span><span class="oo-ui-indicatorElement-indicator oo-ui-indicatorElement-noIndicator"></span></a></span></th></tr></tfoot></table>',
  },
  { text = "Completing the puzzle correctly triggers a cutscene." },
  { text = "Kill the torcher.", title = "The Pests' attack" },
  { text = "Go up the ladder." },
  { text = "Kill the shifter and wait for another cutscene." },
  { text = "Once teleported, talk to Sir Tiffy." },
  { text = "Speak with Captain Tyr at the Void Knights' Outpost." },
  { text = "Quest complete!" },
}

return Quest:new({
  name = "Quiet Before the Swarm",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.mediumlong,
  releaseDate = 1277337600,
  prereqQuests = { "Imp Catcher", "Wanted!", "Pest Control" },
})
