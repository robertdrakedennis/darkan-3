local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local giantScarab = Model.new(1788, {
  [111] = Vertex.new(148, 708, 380, 125, 112, 51),
  [113] = Vertex.new(156, 700, 376, 125, 112, 51),
  [117] = Vertex.new(156, 692, 392, 125, 112, 51),
  [119] = Vertex.new(160, 700, 392, 125, 112, 51),
  [398] = Vertex.new(148, 688, 396, 125, 112, 51),
})
--#endregion
--#region Items
local keris = Model.new(222, {
  [21] = Vertex.new(-12, 16, 40, 125, 112, 51),
  [33] = Vertex.new(-20, 24, 24, 125, 112, 51),
  [123] = Vertex.new(44, 12, -108, 118, 121, 129),
  [126] = Vertex.new(40, 12, -128, 125, 112, 51),
  [129] = Vertex.new(40, 12, -128, 159, 140, 48),
})
--#endregion
--#region Quest Items
local parchment = Model.new(339, {
  [3] = Vertex.new(20, 0, -72, 147, 146, 134),
  [33] = Vertex.new(-12, 0, -104, 93, 15, 8),
  [35] = Vertex.new(-8, 0, -112, 93, 15, 8),
  [327] = Vertex.new(4, 36, -72, 93, 15, 8),
  [338] = Vertex.new(0, 12, -92, 93, 15, 8),
})
--#endregion

local parchmentTexture =
  "\x03\x0a\x0d\xff\x0a\x14\x18\xff\x1a\x20\x20\xff\x78\x59\x3b\xff\x78\x55\x37\xff\x7d\x5b\x3b\xff\x99\x77\x52\xff\xac\x89\x60\xff\xb1\x8b\x61\xff\xb6\x93\x66\xff\xac\x85\x5c\xff\x79\x5d\x3c\xff\x6f\x57\x38\xff\x7b\x63\x40\xff\x85\x69\x47\xff\x8d\x70\x49\xff\x9f\x81\x57\xff\xa4\x83\x58\xff\xa5\x80\x54\xff\xac\x88\x5c\xff\xb1\x8d\x60\xff\xb5\x94\x68\xff\xb9\x98\x6b\xff\xbc\x9b\x6c\xff\xbd\x9b\x6c\xff\xbe\x9d\x6e\xff\xbf\x9b\x6b\xff\xc1\x9d\x6e\xff\xc2\x9e\x70\xff\xc3\x9d\x6d\xff\xc3\x9d\x6f\xff\xc6\xa3\x75\xff\xc8\xa6\x78\xff\xc9\xa6\x79\xff\xcb\xa9\x7a\xff\xcc\xa9\x7b\xff\xcf\xab\x7d\xff\xcf\xaa\x79\xff\xce\xaa\x7a\xff\xd1\xaf\x81\xff\xd2\xb0\x80\xff\xd6\xb6\x87\xff\xd7\xb8\x89\xff\xd7\xb6\x86\xff\xd8\xb7\x86\xff\xd7\xb9\x8a\xff\xd6\xb5\x86\xff\xd6\xb7\x87\xff\xd9\xbd\x8f\xff\xda\xbd\x90\xff\xdc\xbe\x91\xff\xdd\xbf\x90\xff\xdd\xbe\x8d\xff\xdb\xbb\x8b\xff\xdb\xbc\x8a\xff\xd7\xb6\x84\xff\xda\xb8\x86\xff\xd9\xb6\x85\xff\xd9\xb6\x83\xff\xdb\xba\x8c\xff\xdb\xb9\x87\xff\xd8\xb6\x84\xff\xda\xb9\x87\xff\xdd\xbc\x8a\xff\xdd\xbb\x89\xff\xdc\xbb\x88\xff\xdd\xbb\x88\xff\xdc\xb9\x87\xff\xda\xb6\x81\xff\xda\xb7\x80\xff\xda\xb7\x82\xff\xdb\xba\x85\xff\xdd\xbb\x88\xff\xde\xbd\x89\xff\xde\xbe\x8a\xff\xdf\xbf\x8b\xff\xe0\xbf\x8c\xff\xdf\xbe\x8c\xff\xdf\xbf\x8d\xff\xdd\xbe\x8a\xff\xdf\xc0\x8d\xff\xdf\xc0\x8e\xff\xde\xbe\x8e\xff\xdc\xbc\x8b\xff\xdc\xbd\x8c\xff\xdd\xbf\x8d\xff\xdb\xbc\x8a\xff\xdd\xbe\x8e\xff\xdc\xbc\x89\xff\xdb\xbc\x87\xff\xde\xc0\x8e\xff\xde\xbf\x8e\xff\xdd\xbb\x89\xff\xdd\xbb\x88\xff\xde\xbd\x8c\xff\xe1\xbf\x8e\xff\xe3\xc2\x91\xff\xe5\xc7\x97\xff\xe6\xc8\x99\xff\xe6\xc9\x99\xff\xe3\xc5\x92\xff\xe4\xc6\x94\xff\xe5\xc8\x98\xff\xe8\xcc\x9d\xff\xea\xce\xa0\xff\xe9\xce\xa1\xff\xe9\xcd\x9e\xff\xe7\xca\x9c\xff\xe9\xcd\x9e\xff\xe8\xcb\x9b\xff\xe8\xcb\x9b\xff\xea\xcf\x9f\xff\xe9\xce\x9e\xff\xe9\xcd\x9d\xff\xe9\xcd\x9d\xff\xea\xd0\x9d\xff\xeb\xcf\x9e\xff\xea\xcf\x9d\xff\xea\xce\x9c\xff\xeb\xcf\x9d\xff\xea\xce\x9a\xff\xec\xcf\x9b\xff\xec\xcf\x9d\xff\xec\xd0\x9d\xff\xeb\xd2\xa7\xff\xeb\xd2\xab\xff\xeb\xd4\xae\xff\xeb\xce\xa0\xff\xeb\xcd\x9e\xff\xee\xcf\xa0\xff\xee\xcf\xa0\xff\xee\xcf\xa1\xff\xea\xcc\x9b\xff\xe8\xc9\x97\xff\xe9\xca\x98\xff\xe7\xc7\x95\xff\xe6\xc4\x92\xff\xe7\xc4\x93\xff\xe8\xc5\x97\xff\xe7\xc5\x96\xff\xe8\xc6\x94\xff\xe7\xc7\x96\xff\xe8\xc6\x96\xff\xe6\xc7\x95\xff\xe8\xc9\x99\xff\xe5\xc2\x92\xff\xe6\xc5\x96\xff\xe6\xc7\x94\xff\xe6\xc6\x93\xff\xe7\xc8\x96\xff\xe6\xc7\x93\xff\xe6\xc7\x92\xff\xe6\xc9\x94\xff\xe6\xc9\x96\xff\xe4\xc7\x95\xff\xe3\xc7\x94\xff\xe4\xc8\x96\xff\xe2\xc4\x90\xff\xe3\xc5\x92\xff\xe4\xc5\x92\xff\xe3\xc2\x91\xff\xe4\xc5\x96\xff\xe3\xc5\x94\xff\xe5\xc7\x99\xff\xe2\xc4\x93\xff\xe3\xc6\x97\xff\xe5\xc6\x99\xff\xe4\xc4\x97\xff\xe6\xc6\x99\xff\xe6\xc9\x99\xff\xe7\xcb\x9c\xff\xe6\xc9\x99\xff\xe6\xc8\x98\xff\xe5\xc7\x97\xff\xe6\xc7\x9a\xff\xe6\xc7\x9b\xff\xe5\xc5\x99\xff\xe4\xc4\x97\xff\xe4\xc5\x97\xff\xe4\xc5\x96\xff\xe3\xc6\x95\xff\xe4\xc7\x96\xff\xe3\xc5\x95\xff\xe1\xc2\x90\xff\xdf\xbe\x8d\xff\xe0\xc3\x91\xff\xde\xbf\x90\xff\xe0\xbf\x91\xff\xe0\xbb\x8b\xff\xe0\xbc\x8a\xff\xe1\xbc\x8e\xff\xe3\xc1\x94\xff\xe4\xc1\x94\xff\xe5\xc4\x97\xff\xe2\xc5\x97\xff\xe1\xc3\x95\xff\xe3\xc5\x95\xff\xe6\xc8\x97\xff\xe7\xcd\xa1\xff\xe8\xcf\xa3\xff\xe8\xd2\xab\xff\xe7\xd2\xad\xff\xe9\xd1\xa8\xff\xe9\xcd\x9b\xff\xe8\xcd\x9a\xff\xe9\xce\x9d\xff\xe9\xcc\x9b\xff\xe9\xcc\x9b\xff\xe8\xcb\x9a\xff\xe8\xca\x99\xff\xe9\xcd\x9e\xff\xe8\xca\x97\xff\xe7\xc9\x93\xff\xe8\xc9\x97\xff\xea\xcb\x9d\xff\xec\xce\xa0\xff\xec\xcc\x9e\xff\xea\xcc\x9f\xff\xe9\xc9\x9b\xff\xe8\xc8\x9b\xff\xe9\xc8\x9b\xff\xe8\xc6\x98\xff\xe9\xc8\x98\xff\xe9\xca\x98\xff\xe8\xc8\x98\xff\xe7\xc6\x96\xff\xe9\xc9\x9a\xff\xe8\xc6\x96\xff\xe5\xc2\x92\xff\xe4\xc3\x92\xff\xe5\xc5\x95\xff\xe5\xc4\x93\xff\xe6\xc5\x95\xff\xe5\xc5\x96\xff\xe5\xc6\x99\xff\xe6\xc7\x9b\xff\xe5\xc7\x99\xff\xe5\xc8\x9d\xff\xe4\xc7\x9c\xff\xe2\xc4\x98\xff\xe3\xc4\x9a\xff\xe2\xc3\x98\xff\xe0\xbf\x92\xff\xe3\xc4\x99\xff\xe4\xc5\x9a\xff\xe4\xc5\x9a\xff\xe4\xc5\x97\xff\xe6\xc6\x9a\xff\xe5\xc2\x92\xff\xe5\xc3\x96\xff\xe5\xc3\x96\xff\xe2\xbe\x91\xff\xe3\xc2\x93\xff\xe1\xc2\x92\xff\xe1\xc5\x98\xff\xe3\xc6\x97\xff\xe2\xc5\x94\xff\xde\xbf\x8c\xff\xdf\xbf\x8f\xff\xdf\xbd\x8d\xff\xdd\xb7\x8c\xff\xdf\xbc\x8c\xff\xe2\xc0\x91\xff\xe3\xc1\x93\xff\xe2\xc0\x93\xff\xe2\xc0\x92\xff\xe2\xbf\x92\xff\xe2\xbf\x91\xff\xe1\xbf\x91\xff\xe2\xbf\x91\xff\xe3\xc1\x93\xff\xe3\xc0\x92\xff\xe1\xbc\x8f\xff\xe4\xc0\x91\xff\xe4\xc2\x92\xff\xe2\xc2\x8e\xff\xe1\xc1\x8b\xff\xe3\xc4\x92\xff\xe1\xc2\x90\xff\xe0\xc0\x8e\xff\xdf\xbf\x8c\xff\xdf\xbe\x8b\xff\xe0\xbe\x8e\xff\xde\xba\x8a\xff\xdc\xb6\x87\xff\xdd\xba\x8d\xff\xde\xbc\x8e\xff\xdc\xba\x8a\xff\xd8\xb5\x83\xff\xdc\xb8\x88\xff\xde\xbc\x8d\xff\xdf\xc0\x90\xff\xe1\xc4\x93\xff\xe0\xc4\x91\xff\xde\xc2\x8d\xff\xe1\xc5\x91\xff\xdf\xc4\x91\xff\xe3\xc7\x96\xff\xe2\xc7\x95\xff\xe1\xc5\x96\xff\xe0\xc5\x95\xff\xdf\xc3\x95\xff\xdc\xc1\x8e\xff\xdc\xc0\x90\xff\xde\xc2\x93\xff\xdc\xbf\x8d\xff\xdc\xbe\x8d\xff\xdc\xbb\x8a\xff\xdc\xb8\x88\xff\xdb\xb6\x84\xff\xdd\xb8\x89\xff\xdb\xb8\x87\xff\xdb\xb9\x85\xff\xdc\xba\x88\xff\xde\xbe\x8d\xff\xde\xbe\x8d\xff\xdb\xbb\x88\xff\xdd\xbc\x8a\xff\xdd\xbe\x8e\xff\xdc\xbb\x8a\xff\xdd\xbd\x8b\xff\xdd\xbc\x8b\xff\xdd\xbb\x88\xff\xdf\xbf\x8d\xff\xe0\xc0\x8e\xff\xe1\xc0\x8d\xff\xe0\xbf\x8d\xff\xe1\xc0\x8f\xff\xe1\xbf\x8d\xff\xe1\xbf\x8c\xff\xe0\xbd\x88\xff\xe1\xbe\x8b\xff\xe0\xbe\x8a\xff\xde\xbc\x8a\xff\xdd\xc1\x95\xff\xdd\xbf\x93\xff\xdd\xbd\x8b\xff\xdc\xba\x87\xff\xde\xbc\x89\xff\xde\xb9\x86\xff\xe0\xba\x86\xff\xe1\xbc\x8a\xff\xe1\xbc\x8b\xff\xe2\xbe\x8c\xff\xe2\xbe\x8d\xff\xe2\xc0\x8f\xff\xe1\xbe\x8c\xff\xe3\xc0\x8f\xff\xe3\xbf\x8e\xff\xe3\xbf\x8e\xff\xe3\xbe\x8e\xff\xe2\xbd\x8e\xff\xe1\xbe\x8d\xff\xe1\xbf\x8d\xff\xe3\xc1\x90\xff\xe4\xc2\x92\xff\xe3\xc2\x92\xff\xe4\xc4\x94\xff\xe5\xc3\x93\xff\xe7\xc5\x94\xff\xe7\xc7\x96\xff\xe8\xc9\x98\xff\xe8\xc8\x99\xff\xe7\xc5\x96\xff\xe7\xc5\x95\xff\xe5\xc5\x97\xff\xe7\xc7\x95\xff\xe8\xc8\x98\xff\xea\xc9\x99\xff\xea\xc9\x9a\xff\xe9\xc9\x9a\xff\xe9\xc8\x9a\xff\xe9\xc8\x9a\xff\xe9\xc8\x98\xff\xe9\xc8\x9a\xff\xe8\xc7\x98\xff\xe8\xc9\x98\xff\xea\xca\x9a\xff\xe8\xc9\x98\xff\xe9\xc9\x9a\xff\xe8\xc7\x97\xff\xe4\xc3\x91\xff\xe4\xc4\x92\xff\xe6\xc6\x95\xff\xe5\xc5\x94\xff\xe6\xc5\x96\xff\xe5\xc6\x98\xff\xe5\xc6\x98\xff\xe7\xc8\x9d\xff\xe4\xc6\x98\xff\xe4\xc7\x9b\xff\xe5\xc7\x9d\xff\xe2\xc4\x98\xff\xe2\xc3\x98\xff\xe3\xc3\x97\xff\xe1\xc1\x94\xff\xe2\xc3\x98\xff\xe3\xc3\x98\xff\xe3\xc4\x99\xff\xe3\xc4\x97\xff\xe4\xc4\x97\xff\xe4\xc1\x91\xff\xe4\xc2\x95\xff\xe3\xc1\x93\xff\xe1\xbd\x90\xff\xe2\xc1\x91\xff\xe1\xc1\x93\xff\xe2\xc6\x98\xff\xe3\xc7\x98\xff\xe3\xc6\x96\xff\xe0\xc1\x90\xff\xe1\xc1\x92\xff\xe1\xbf\x90\xff\xe0\xbe\x91\xff\xe0\xbd\x8d\xff\xe2\xc1\x93\xff\xe3\xc2\x94\xff\xe3\xc2\x94\xff\xe2\xc0\x92\xff\xe2\xc0\x93\xff\xe2\xc0\x91\xff\xe2\xc0\x91\xff\xe2\xc0\x92\xff\xe3\xc0\x92\xff\xe5\xc2\x94\xff\xe2\xbd\x90\xff\xe5\xc1\x93\xff\xe3\xc1\x92\xff\xe3\xc3\x92\xff\xe2\xc2\x8e\xff\xe2\xc2\x8f\xff\xe1\xc0\x8e\xff\xe1\xc1\x90\xff\xe1\xc1\x90\xff\xe2\xc2\x91\xff\xe4\xc3\x95\xff\xe3\xc2\x93\xff\xe3\xc2\x92\xff\xe4\xc4\x98\xff\xe5\xc4\x97\xff\xe5\xc4\x96\xff\xe2\xc0\x91\xff\xe3\xc3\x94\xff\xe3\xc5\x95\xff\xe3\xc5\x96\xff\xe4\xc6\x98\xff\xe4\xc8\x99\xff\xe3\xc7\x97\xff\xe2\xc6\x96\xff\xe1\xc5\x96\xff\xe0\xc2\x93\xff\xdf\xbf\x91\xff\xde\xbe\x8f\xff\xde\xbe\x8f\xff\xdd\xbe\x8f\xff\xdc\xbd\x8d\xff\xda\xbb\x8d\xff\xd9\xbb\x8b\xff\xd7\xb8\x88\xff\xd7\xb9\x8b\xff\xd6\xb8\x8a\xff\xd3\xb5\x86\xff\xd0\xb1\x7f\xff\xce\xac\x7b\xff\xcc\xaa\x7a\xff\xcb\xab\x77\xff\xcb\xaa\x77\xff\xc8\xa7\x76\xff\xc7\xa5\x76\xff\xc7\xa6\x76\xff\xc3\xa2\x73\xff\xc2\xa0\x73\xff\xbd\x9a\x6d\xff\xba\x97\x69\xff\xb8\x95\x68\xff\xb5\x93\x67\xff\xb3\x94\x69\xff\xa5\x88\x5e\xff\x90\x7a\x50\xff\x8e\x77\x4f\xff\x88\x73\x4d\xff\x82\x6c\x4a\xff\x7a\x63\x41\xff\x73\x5c\x3b\xff\xad\x85\x57\xff\xc6\xa1\x74\xff\xca\xa6\x7a\xff\xbb\x94\x67\xff\xa6\x7f\x56\xff\x90\x6d\x48\xff\x7d\x60\x3f\xff\x72\x56\x36\xff\x6f\x55\x39\xff\x0c\x14\x1c\xff\x07\x16\x1e\xff\x07\x10\x14\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to the High Priest in Sophanem.",
    title = "Getting started",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Menaphos lodestone -> Flying carpet",
      url = "Menaphos_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3311, 4765, 2729, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["sophanem high priest"], { distance = 13 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Continue talking to the High Priest.",
    actions = { Action.ModelHighlight:new(Models.npcs["sophanem high priest"]) },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.NotInInstance:new() } },
  {
    text = "Continue talking to the High Priest.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["sophanem high priest"]),
      Action.ConversationHighlight:new("Is there any way between Menaphos and Sophanem from below?"),
    },
    postconditions = { Condition.ConversationText:new("you all about the place") },
  },
  {
    text = "Talk to Jex.",
    title = "Tunnels under Sophanem",
    neededItems = { ["Any light source"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3302, 325, 2800, { distance = 23 }),
      Action.ModelHighlight:new(Models.npcs["jex"], { distance = 24 }),
    },
    postconditions = { Condition.ConversationText:new("your light goes out") },
  },
  {
    text = "Climb down the ladder in the church east of Jex.<ul><li>The Sophanem Guards will sell a torch for 200 coins.</li></ul>",
    actions = { Action.Direction:new(3315, 325, 2797, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2766, 965, 5131, 4) },
  },
  {
    text = "Climb down the second trapdoor.",
    warning = "Protect/deflect from magic and auto-retaliate off is recommended.",
    actions = { Action.Direction:new(2766, 965, 5130, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3318, 6965, 9273, 4) },
  },
  {
    text = "Follow the path through the maze.",
    warning = "Stand on the line to avoid traps.",
    actions = {
      Action.ConversationHighlight:new("Try to avoid it, using your Agility skill."),
      Action.ConversationHighlight:new("Try to disarm it, using your Thieving skill."),
      Action.PathGuide:new({
        Location:new(3318, 6965, 9273),
        Location:new(3318, 7005, 9265),
        Location:new(3314, 7021, 9265),
        Location:new(3313, 6925, 9272),
        Location:new(3309, 6853, 9273),
        Location:new(3306, 6845, 9274),
        Location:new(3296, 6901, 9274),
        Location:new(3296, 6789, 9268),
        Location:new(3299, 6789, 9267),
      }, { opaque = true }),
      Action.PathGuide:new({
        Location:new(3302, 6789, 9267),
        Location:new(3304, 6909, 9264),
        Location:new(3307, 6781, 9263),
        Location:new(3308, 6877, 9260),
        Location:new(3310, 6780, 9260),
        Location:new(3311, 6861, 9259),
        Location:new(3312, 6861, 9259),
        Location:new(3313, 6845, 9260),
        Location:new(3314, 6861, 9260),
        Location:new(3315, 6821, 9259),
        Location:new(3316, 6789, 9259),
        Location:new(3317, 6741, 9260),
        Location:new(3321, 6925, 9260),
        Location:new(3321, 7021, 9254),
      }, { opaque = true }),
      Action.PathGuide:new({
        Location:new(3321, 7021, 9251),
        Location:new(3321, 7005, 9246),
        Location:new(3312, 6885, 9246),
        Location:new(3310, 6957, 9252),
        Location:new(3305, 6997, 9252),
        Location:new(3304, 7021, 9255),
        Location:new(3297, 6885, 9255),
        Location:new(3297, 6901, 9251),
        Location:new(3301, 7005, 9250),
        Location:new(3301, 6741, 9245),
        Location:new(3301, 6989, 9241),
        Location:new(3295, 6933, 9241),
        Location:new(3293, 6941, 9242),
        Location:new(3292, 6901, 9243),
        Location:new(3291, 6893, 9243),
        Location:new(3285, 6853, 9242),
        Location:new(3285, 6949, 9237),
        Location:new(3291, 6925, 9236),
        Location:new(3294, 6877, 9234),
        Location:new(3296, 6877, 9234),
        Location:new(3297, 6725, 9230),
        Location:new(3297, 6773, 9229),
        Location:new(3296, 6845, 9228),
        Location:new(3296, 6893, 9227),
        Location:new(3296, 6861, 9225),
        Location:new(3287, 6941, 9225),
        Location:new(3283, 6821, 9227),
        Location:new(3280, 6837, 9227),
        Location:new(3279, 6829, 9226),
        Location:new(3276, 6853, 9226),
        Location:new(3275, 6853, 9228),
        Location:new(3269, 6853, 9229),
      }, { opaque = true }),
    },
    postconditions = { Condition.DistanceTo:new(3257, 4485, 9226, 4) },
  },
  {
    text = "Search Kaleef's body.",
    actions = { Action.Direction:new(3239, 1289, 9243.5) },
    postconditions = { Condition.InventoryContains:new(parchment) },
  },
  {
    text = "Read the parchment.",
    actions = { Action.InventoryHighlight:new(parchment) },
    postconditions = { Condition.Generic2DVisible:new(496, 293, 160, parchmentTexture) },
  },
  {
    text = "Talk to Maisa to the west.",
    actions = {
      Action.Direction:new(3218, 2257, 9246, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["maisa"], { distance = 21 }),
      Action.ConversationHighlight:new("Draynor Village"),
      Action.ConversationHighlight:new("Ozan and Leela."),
    },
    postconditions = { Condition.ConversationText:new("happened to the last one") }, --not tested
  },
  {
    text = "Talk to Osman inside the Al Kharid palace.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Menaphos lodestone",
      url = "Menaphos_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3290, 4485, 3157, { distance = 7 }),
      Action.ModelHighlight:new(Models.npcs["osman"], { distance = 7 }),
      Action.ConversationHighlight:new("I want to talk to you about Sophanem."),
      Action.ConversationHighlight:new("It would drive a wedge between the Menaphite cities."),
    },
    postconditions = { Condition.ConversationText:new("close to Sophanem shortly") },
  },
  {
    text = "Talk to Osman outside the northern Sophanem gate.",
    title = "The final fight",
    neededItems = { ["Any light source"] = { quantity = 1 } },
    recommendedItems = {
      ["Combat gear"] = { quantity = 1 },
      ["Food"] = { quantity = 1 },
    },
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Menaphos lodestone -> Flying carpet",
      url = "Menaphos_lodestone_icon.png",
    },
    actions = {
      Action.ModelHighlight:new(Models.npcs["osman"]),
      Action.ConversationHighlight:new("I know of a secret entrance to the north."),
    },
    postconditions = { Condition.ConversationText:new("a look for him") },
  },
  {
    text = "Enter Sophanem.",
    actions = { Action.Direction:new(3297, 1625, 2817) },
    postconditions = { Condition.DistanceTo:new(3297, 325, 2812, 4) },
  },
  {
    text = "Climb down the ladder in the church east of Jex.",
    actions = { Action.Direction:new(3315, 325, 2797, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(2766, 965, 5131, 4) },
  },
  {
    text = "Climb down the second trapdoor.",
    actions = { Action.Direction:new(2766, 965, 5130, { tile = true }) },
    postconditions = { Condition.DistanceTo:new(3318, 6965, 9273, 4) },
  },
  {
    text = "Follow the path through the maze.",
    actions = {
      Action.ConversationHighlight:new("Try to avoid it, using your Agility skill."),
      Action.ConversationHighlight:new("Try to disarm it, using your Thieving skill."),
      Action.PathGuide:new({
        Location:new(3318, 6965, 9273),
        Location:new(3318, 7005, 9265),
        Location:new(3314, 7021, 9265),
        Location:new(3313, 6925, 9272),
        Location:new(3309, 6853, 9273),
        Location:new(3306, 6845, 9274),
        Location:new(3296, 6901, 9274),
        Location:new(3296, 6789, 9268),
        Location:new(3299, 6789, 9267),
      }, { opaque = true }),
      Action.PathGuide:new({
        Location:new(3302, 6789, 9267),
        Location:new(3304, 6909, 9264),
        Location:new(3307, 6781, 9263),
        Location:new(3308, 6877, 9260),
        Location:new(3310, 6780, 9260),
        Location:new(3311, 6861, 9259),
        Location:new(3312, 6861, 9259),
        Location:new(3313, 6845, 9260),
        Location:new(3314, 6861, 9260),
        Location:new(3315, 6821, 9259),
        Location:new(3316, 6789, 9259),
        Location:new(3317, 6741, 9260),
        Location:new(3321, 6925, 9260),
        Location:new(3321, 7021, 9254),
      }, { opaque = true }),
      Action.PathGuide:new({
        Location:new(3321, 7021, 9251),
        Location:new(3321, 7005, 9246),
        Location:new(3312, 6885, 9246),
        Location:new(3310, 6957, 9252),
        Location:new(3305, 6997, 9252),
        Location:new(3304, 7021, 9255),
        Location:new(3297, 6885, 9255),
        Location:new(3297, 6901, 9251),
        Location:new(3301, 7005, 9250),
        Location:new(3301, 6741, 9245),
        Location:new(3301, 6989, 9241),
        Location:new(3295, 6933, 9241),
        Location:new(3293, 6941, 9242),
        Location:new(3292, 6901, 9243),
        Location:new(3291, 6893, 9243),
        Location:new(3290, 6877, 9242),
        Location:new(3285, 6853, 9242),
        Location:new(3285, 6949, 9237),
        Location:new(3291, 6925, 9236),
        Location:new(3294, 6877, 9234),
        Location:new(3296, 6877, 9234),
        Location:new(3297, 6725, 9230),
        Location:new(3297, 6773, 9229),
        Location:new(3296, 6845, 9228),
        Location:new(3296, 6893, 9227),
        Location:new(3296, 6861, 9225),
        Location:new(3287, 6941, 9225),
        Location:new(3283, 6821, 9227),
        Location:new(3280, 6837, 9227),
        Location:new(3279, 6829, 9226),
        Location:new(3276, 6853, 9226),
        Location:new(3275, 6853, 9228),
        Location:new(3269, 6853, 9229),
      }, { opaque = true }),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("Osman!") } },
  {
    text = "Kill the giant scarab and take the Keris.<ul><li>Optional: Players with level 50 Attack may head back up the ladder and kill a scarab mage or locust rider with the Keris dagger. This completes the Ludikeris achievement.</li></ul>",
    actions = { Action.ModelHighlight:new(giantScarab, { instance = true }) },
    postconditions = { Condition.ModelVisible:new(keris) },
  },
  {
    text = "Pick up the Keris dagger.",
    actions = { Action.ModelHighlight:new(keris, { instance = true }) },
    postconditions = { Condition.InventoryContains:new(keris) },
  },
  {
    text = "Talk to Osman.",
    actions = { Action.ModelHighlight:new(Models.npcs["osman"], { instance = true }) },
    postconditions = { Condition.ConversationText:new("Thanks.") },
  },
  {
    text = "Return to the High Priest. Teleporting out is quickest.",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Menaphos lodestone -> Flying carpet",
      url = "Menaphos_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3311, 4765, 2729, { distance = 12 }),
      Action.ModelHighlight:new(Models.npcs["sophanem high priest"], { distance = 13 }),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Contact!",
  steps = steps,
  timeline = Enums.timeline.legendary,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1168387200,
  prereqQuests = { "Icthlarin's Little Helper" },
  questReqs = {},
  neededItems = { ["Any light source"] = { quantity = 1, model = Models.items["light source"], duringQuest = true } },
  recommendedItems = {
    ["Combat gear"] = { quantity = 1 },
    ["Food"] = { quantity = 1 },
  },
  combatNPCs = { ["Giant scarab"] = { level = "86", quantity = 1 } },
})
