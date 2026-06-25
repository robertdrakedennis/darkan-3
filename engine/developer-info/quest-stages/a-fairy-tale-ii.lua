local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local coOrdinator = Model.new(1416, {
  [1328] = Vertex.new(-52, 856, -140, 169, 165, 177),
  [1329] = Vertex.new(-44, 836, -124, 169, 165, 177),
  [1349] = Vertex.new(44, 840, -128, 169, 165, 177),
  [1377] = Vertex.new(-40, 868, -160, 179, 176, 186),
  [1400] = Vertex.new(32, 876, -156, 179, 176, 186),
})
local gorak = Model.new(4551, {
  [1285] = Vertex.new(-201, 392, 208, 65, 102, 125),
  [1287] = Vertex.new(-202, 385, 150, 65, 102, 125),
  [1288] = Vertex.new(201, 392, 208, 65, 102, 125),
  [1289] = Vertex.new(202, 385, 150, 65, 102, 125),
  [1290] = Vertex.new(164, 373, 133, 65, 102, 125),
})
--#endregion
--#region Objects
local healingCertificateObj = Model.new(360, {
  [39] = Vertex.new(28, 36, -204, 84, 75, 65),
  [149] = Vertex.new(148, 12, -112, 84, 75, 65),
  [194] = Vertex.new(148, 4, -188, 84, 75, 65),
  [206] = Vertex.new(136, 36, 236, 84, 75, 65),
  [210] = Vertex.new(-120, 12, 124, 76, 68, 58),
})
local grownStarFlower = Model.new(1506, {
  [53] = Vertex.new(28, 652, -12, 207, 205, 19),
  [54] = Vertex.new(32, 648, -16, 207, 205, 19),
  [59] = Vertex.new(32, 648, -16, 207, 205, 19),
  [63] = Vertex.new(-4, 640, -48, 207, 205, 19),
  [69] = Vertex.new(4, 640, -48, 207, 205, 19),
})
local sleepingFairyQueen = Model.new(999, {
  [2] = Vertex.new(-116, 148, 0, 160, 169, 172, 0.6078),
  [339] = Vertex.new(-112, 176, 32, 160, 169, 172, 0.6078),
  [578] = Vertex.new(72, 156, 104, 160, 169, 172, 0.6078),
  [666] = Vertex.new(72, 156, 104, 160, 169, 172, 0.6078),
  [891] = Vertex.new(144, 164, -4, 95, 84, 73, 0.000),
})
--#endregion
--#region Quest Items
local starFlower = Model.new(600, {
  [371] = Vertex.new(52, 36, 108, 152, 139, 142),
  [383] = Vertex.new(-52, 36, 108, 152, 139, 142),
  [559] = Vertex.new(92, 28, -24, 152, 139, 142),
  [571] = Vertex.new(52, 36, 104, 152, 139, 142),
  [583] = Vertex.new(-52, 36, 104, 152, 139, 142),
})
local magicEssenceUnf = Model.multi({
  Model.new(66, {
    [1] = Vertex.new(-4, 96, -12, 84, 79, 54),
    [2] = Vertex.new(4, 112, -12, 84, 79, 54),
    [3] = Vertex.new(4, 96, -12, 84, 79, 54),
    [5] = Vertex.new(-4, 112, -12, 84, 79, 54),
    [7] = Vertex.new(4, 96, 12, 84, 79, 54),
    [8] = Vertex.new(-4, 112, 12, 84, 79, 54),
    [9] = Vertex.new(-4, 96, 12, 84, 79, 54),
    [11] = Vertex.new(4, 112, 12, 84, 79, 54),
    [13] = Vertex.new(-12, 96, -4, 84, 79, 54),
    [17] = Vertex.new(-12, 112, -4, 84, 79, 54),
  }),
  Model.new(240, {
    [1] = Vertex.new(4, 84, 20, 135, 137, 148, 0.4980),
    [2] = Vertex.new(-4, 96, 20, 135, 137, 148, 0.4980),
    [3] = Vertex.new(-4, 84, 20, 135, 137, 148, 0.4980),
    [5] = Vertex.new(4, 96, 20, 135, 137, 148, 0.4980),
    [7] = Vertex.new(20, 84, 4, 135, 137, 148, 0.4980),
    [11] = Vertex.new(20, 96, 4, 135, 137, 148, 0.4980),
    [13] = Vertex.new(20, 84, -4, 135, 137, 148, 0.4980),
    [17] = Vertex.new(20, 96, -4, 135, 137, 148, 0.4980),
    [19] = Vertex.new(4, 84, -20, 135, 137, 148, 0.4980),
    [23] = Vertex.new(4, 96, -20, 135, 137, 148, 0.4980),
    [49] = Vertex.new(-4, 68, 16, 120, 111, 110, 0.8745),
    [50] = Vertex.new(-4, 84, 12, 120, 111, 110, 0.8745),
    [51] = Vertex.new(-12, 84, 4, 120, 111, 110, 0.8745),
    [54] = Vertex.new(-16, 68, 4, 120, 111, 110, 0.8745),
    [55] = Vertex.new(-12, 12, 40, 120, 111, 110, 0.8745),
    [60] = Vertex.new(-12, 84, -4, 120, 111, 110, 0.8745),
    [63] = Vertex.new(-40, 12, 12, 120, 111, 110, 0.8745),
    [64] = Vertex.new(-4, 0, 36, 120, 111, 110, 0.8745),
    [69] = Vertex.new(-32, 0, 4, 120, 111, 110, 0.8745),
    [72] = Vertex.new(-16, 68, -4, 120, 111, 110, 0.8745),
  }),
})
local gorakClaws = Model.new(123, {
  [1] = Vertex.new(244, 0, -32, 38, 29, 29),
  [2] = Vertex.new(240, 0, -24, 38, 29, 29),
  [3] = Vertex.new(236, 0, -32, 38, 29, 29),
  [90] = Vertex.new(72, 0, 96, 101, 93, 93),
  [92] = Vertex.new(72, 0, 96, 101, 93, 93),
})
local gorakClawPowder = Model.new(597, {
  [136] = Vertex.new(72, 0, 80, 120, 111, 110),
  [175] = Vertex.new(100, 0, 60, 120, 111, 110),
  [178] = Vertex.new(100, 0, 60, 120, 111, 110),
  [568] = Vertex.new(132, 0, -24, 120, 111, 110),
  [571] = Vertex.new(132, 0, -24, 120, 111, 110),
})
local magicEssence = Model.multi({
  Model.new(90, {
    [2] = Vertex.new(-20, 52, 8, 174, 98, 15),
    [11] = Vertex.new(-12, 52, -20, 174, 98, 15),
    [14] = Vertex.new(12, 52, 20, 174, 98, 15),
    [23] = Vertex.new(20, 52, -16, 174, 98, 15),
    [67] = Vertex.new(-4, 96, 12, 84, 79, 54),
    [68] = Vertex.new(-4, 112, 12, 84, 79, 54),
    [69] = Vertex.new(-12, 112, 4, 84, 79, 54),
    [72] = Vertex.new(-12, 96, 4, 84, 79, 54),
    [74] = Vertex.new(-4, 112, -12, 84, 79, 54),
    [75] = Vertex.new(-12, 112, -4, 84, 79, 54),
    [77] = Vertex.new(4, 112, -12, 84, 79, 54),
    [80] = Vertex.new(12, 112, -4, 84, 79, 54),
    [83] = Vertex.new(12, 112, 4, 84, 79, 54),
    [86] = Vertex.new(4, 112, 12, 84, 79, 54),
  }),
  Model.new(288, {
    [50] = Vertex.new(40, 12, -12, 174, 98, 15, 0.8745),
    [53] = Vertex.new(12, 12, -40, 174, 98, 15, 0.8745),
    [153] = Vertex.new(-4, 84, -12, 135, 137, 148, 0.4980),
    [186] = Vertex.new(4, 84, -12, 135, 137, 148, 0.4980),
    [241] = Vertex.new(-4, 96, 20, 135, 137, 148, 0.4980),
    [242] = Vertex.new(4, 96, 12, 135, 137, 148, 0.4980),
    [243] = Vertex.new(-4, 96, 12, 135, 137, 148, 0.4980),
    [244] = Vertex.new(-12, 96, 4, 135, 137, 148, 0.4980),
    [248] = Vertex.new(4, 96, 20, 135, 137, 148, 0.4980),
    [251] = Vertex.new(-20, 96, 4, 135, 137, 148, 0.4980),
    [254] = Vertex.new(12, 96, 4, 135, 137, 148, 0.4980),
    [256] = Vertex.new(-20, 96, -4, 135, 137, 148, 0.4980),
    [260] = Vertex.new(20, 96, 4, 135, 137, 148, 0.4980),
    [264] = Vertex.new(-12, 96, -4, 135, 137, 148, 0.4980),
    [265] = Vertex.new(12, 96, -4, 135, 137, 148, 0.4980),
    [270] = Vertex.new(-4, 96, -12, 135, 137, 148, 0.4980),
    [273] = Vertex.new(20, 96, -4, 135, 137, 148, 0.4980),
    [276] = Vertex.new(-4, 96, -20, 135, 137, 148, 0.4980),
    [277] = Vertex.new(4, 96, -12, 135, 137, 148, 0.4980),
    [281] = Vertex.new(4, 96, -20, 135, 137, 148, 0.4980),
  }),
})
--#endregion

--150
local certificateTexture =
  "\x04\x0b\x10\xff\x05\x0d\x12\xff\x10\x17\x19\xff\x77\x5a\x3d\xff\x74\x56\x38\xff\x7f\x61\x40\xff\x97\x75\x51\xff\xb1\x8d\x63\xff\xb3\x91\x65\xff\xb7\x95\x69\xff\xac\x88\x5d\xff\x72\x58\x39\xff\x73\x59\x3a\xff\x75\x5b\x3c\xff\x7e\x66\x44\xff\x8f\x75\x52\xff\x98\x7d\x57\xff\xa1\x86\x5e\xff\xa7\x89\x60\xff\xae\x8c\x61\xff\xb1\x8e\x62\xff\xb3\x92\x67\xff\xb6\x95\x68\xff\xba\x98\x6a\xff\xbd\x9c\x6b\xff\xc0\x9f\x6f\xff\xc1\x9d\x6b\xff\xc4\xa1\x6d\xff\xc6\xa5\x72\xff\xc6\xa4\x70\xff\xc6\xa4\x70\xff\xc8\xa5\x71\xff\xc9\xa5\x71\xff\xcd\xa9\x78\xff\xce\xab\x7a\xff\xcf\xac\x7a\xff\xcf\xad\x77\xff\xd2\xaf\x7d\xff\xd2\xaf\x7d\xff\xd2\xae\x7b\xff\xd3\xb0\x7e\xff\xd5\xb4\x83\xff\xd4\xb1\x7f\xff\xd2\xb0\x7a\xff\xd3\xb1\x7c\xff\xd1\xae\x77\xff\xd3\xb2\x7a\xff\xd2\xb0\x79\xff\xd1\xae\x75\xff\xd4\xb5\x7c\xff\xd7\xb8\x81\xff\xd8\xba\x82\xff\xda\xbc\x87\xff\xdb\xbf\x8e\xff\xdc\xc0\x96\xff\xda\xbb\x8b\xff\xda\xb9\x86\xff\xda\xb8\x84\xff\xdb\xb9\x85\xff\xdb\xb9\x87\xff\xdc\xba\x87\xff\xdb\xba\x87\xff\xdc\xbc\x8a\xff\xdc\xbc\x8a\xff\xdd\xbc\x8b\xff\xdd\xbc\x88\xff\xd9\xb6\x81\xff\xda\xb9\x85\xff\xdb\xb9\x87\xff\xdb\xbb\x89\xff\xdd\xbc\x8c\xff\xe1\xc2\x92\xff\xe1\xc1\x90\xff\xe2\xc1\x8e\xff\xe1\xbe\x8c\xff\xe1\xbe\x8c\xff\xe0\xbe\x89\xff\xe1\xbe\x8b\xff\xe0\xbc\x86\xff\xde\xba\x83\xff\xe1\xbf\x8b\xff\xe1\xbf\x8d\xff\xdf\xbe\x8d\xff\xde\xbd\x8c\xff\xde\xbd\x8d\xff\xdf\xbf\x8d\xff\xe0\xc0\x8e\xff\xe1\xc3\x91\xff\xe0\xc3\x91\xff\xe0\xc2\x90\xff\xe1\xc3\x94\xff\xe2\xc5\x96\xff\xe3\xc5\x98\xff\xe3\xc7\x95\xff\xe3\xc8\x95\xff\xe4\xc7\x98\xff\xe6\xc7\x99\xff\xe6\xc9\x9b\xff\xe8\xcd\x9f\xff\xe9\xcd\xa0\xff\xe9\xcc\xa0\xff\xe7\xc8\x9c\xff\xe8\xca\x9d\xff\xe7\xca\x9c\xff\xe9\xcb\x9e\xff\xe9\xcc\x9e\xff\xe9\xcb\x9d\xff\xe6\xc9\x99\xff\xe5\xc7\x96\xff\xe5\xc6\x96\xff\xe5\xc5\x95\xff\xe5\xc6\x97\xff\xe7\xc8\x99\xff\xe6\xc7\x97\xff\xe8\xc9\x99\xff\xe8\xca\x99\xff\xe9\xc8\x96\xff\xe8\xc6\x96\xff\xe6\xc5\x96\xff\xe6\xc5\x95\xff\xe7\xc5\x95\xff\xe6\xc4\x93\xff\xe4\xc1\x90\xff\xe5\xc2\x93\xff\xe4\xc0\x91\xff\xe5\xc2\x92\xff\xe6\xc1\x94\xff\xe7\xc4\x96\xff\xe8\xc8\x99\xff\xea\xca\x9a\xff\xea\xca\x99\xff\xe8\xc8\x96\xff\xe7\xc7\x96\xff\xe7\xc9\x98\xff\xe7\xc8\x96\xff\xe9\xc9\x99\xff\xe8\xca\x9a\xff\xe6\xc9\x9a\xff\xe7\xc7\x98\xff\xe8\xc5\x98\xff\xe7\xc2\x94\xff\xe9\xc4\x97\xff\xe9\xc4\x96\xff\xe7\xc1\x92\xff\xe7\xc3\x94\xff\xe8\xc5\x95\xff\xe8\xc6\x97\xff\xe7\xc5\x96\xff\xe7\xc4\x96\xff\xe7\xc6\x9a\xff\xe7\xc4\x97\xff\xe6\xc7\x98\xff\xe4\xc6\x95\xff\xe2\xc4\x91\xff\xe2\xc5\x92\xff\xe2\xc6\x94\xff\xe4\xc8\x96\xff\xe6\xc9\x97\xff\xe6\xc9\x98\xff\xe4\xc5\x95\xff\xe3\xc4\x95\xff\xe3\xc4\x96\xff\xe4\xc4\x97\xff\xe2\xc2\x94\xff\xe3\xc4\x94\xff\xe2\xc2\x92\xff\xe3\xc3\x94\xff\xe4\xc6\x96\xff\xe4\xc6\x96\xff\xe5\xc8\x98\xff\xe4\xc8\x97\xff\xe4\xc8\x97\xff\xe4\xca\x9a\xff\xe5\xca\x99\xff\xe6\xca\x9a\xff\xe5\xca\x9c\xff\xe5\xcb\x9c\xff\xe6\xcc\x9f\xff\xe6\xcb\x9f\xff\xe6\xcb\x9e\xff\xe6\xca\x9e\xff\xe3\xc7\x9b\xff\xe4\xc7\x9c\xff\xe3\xc5\x9a\xff\xe1\xc3\x96\xff\xe0\xc2\x92\xff\xe2\xc2\x92\xff\xe1\xc1\x91\xff\xe0\xc3\x91\xff\xdf\xc2\x91\xff\xe0\xc1\x93\xff\xe0\xc2\x95\xff\xe0\xc1\x94\xff\xe1\xc0\x92\xff\xe2\xc0\x90\xff\xe2\xc1\x91\xff\xe3\xc1\x93\xff\xe5\xc4\x97\xff\xe7\xc7\x98\xff\xe5\xc5\x97\xff\xe6\xc7\x9a\xff\xe7\xc8\x99\xff\xe8\xca\x9b\xff\xe7\xc6\x95\xff\xe9\xcb\x9d\xff\xe9\xcb\x9c\xff\xe9\xcb\x9d\xff\xe9\xc9\x9b\xff\xe8\xc8\x98\xff\xe7\xc8\x98\xff\xe7\xc7\x97\xff\xe7\xc8\x95\xff\xe9\xca\x95\xff\xea\xcb\x97\xff\xeb\xce\x9a\xff\xea\xcd\x9a\xff\xea\xcc\x99\xff\xe9\xcb\x99\xff\xea\xce\x9f\xff\xe8\xcb\x9a\xff\xeb\xcf\x9f\xff\xeb\xcf\x9f\xff\xeb\xcd\x9e\xff\xed\xce\x9f\xff\xed\xcf\x9f\xff\xeb\xcd\x9c\xff\xe9\xcc\x9a\xff\xe6\xc7\x94\xff\xe7\xc8\x96\xff\xe7\xc7\x95\xff\xe6\xc5\x93\xff\xe5\xc3\x92\xff\xe7\xc5\x97\xff\xe5\xc2\x93\xff\xe6\xc5\x96\xff\xe8\xc9\x9a\xff\xe8\xc9\x9a\xff\xe7\xc9\x99\xff\xe8\xc9\x9a\xff\xe7\xc6\x97\xff\xe8\xc8\x9b\xff\xe7\xc6\x9a\xff\xe6\xca\xa0\xff\xe7\xca\x9d\xff\xe8\xca\x9b\xff\xe8\xca\x99\xff\xe7\xc9\x97\xff\xe7\xc9\x97\xff\xe5\xc7\x94\xff\xe5\xc8\x95\xff\xe4\xc7\x94\xff\xe5\xc6\x94\xff\xe5\xc6\x97\xff\xe3\xc3\x94\xff\xe2\xc3\x94\xff\xe2\xc2\x92\xff\xe4\xc6\x97\xff\xe6\xc8\x9b\xff\xe4\xc6\x98\xff\xe5\xc7\x96\xff\xe4\xc6\x95\xff\xe4\xc7\x95\xff\xe5\xc7\x96\xff\xe4\xc7\x96\xff\xe2\xc5\x93\xff\xe5\xc9\x98\xff\xe3\xc6\x96\xff\xe3\xc5\x96\xff\xe2\xc2\x94\xff\xe4\xc3\x97\xff\xe4\xc3\x97\xff\xe3\xc0\x94\xff\xe0\xbd\x91\xff\xe1\xbd\x90\xff\xe2\xbe\x90\xff\xe1\xbd\x90\xff\xe1\xbd\x8e\xff\xe0\xbc\x8d\xff\xe0\xbf\x8e\xff\xdf\xbf\x8e\xff\xdf\xbb\x8c\xff\xdd\xb6\x87\xff\xde\xb8\x87\xff\xe2\xc0\x92\xff\xe3\xc2\x93\xff\xe3\xbf\x92\xff\xe3\xc1\x92\xff\xe4\xc2\x91\xff\xe4\xc5\x97\xff\xe2\xc3\x94\xff\xe3\xc5\x95\xff\xe3\xc7\x98\xff\xe3\xc7\x98\xff\xe4\xc6\x98\xff\xe2\xc6\x98\xff\xe0\xc2\x94\xff\xe0\xc3\x94\xff\xe1\xc3\x93\xff\xe0\xbf\x8f\xff\xe0\xc0\x91\xff\xdf\xbf\x90\xff\xde\xbf\x8f\xff\xda\xb8\x85\xff\xdb\xba\x84\xff\xdd\xbb\x86\xff\xde\xbc\x8b\xff\xe0\xbf\x8f\xff\xdd\xbc\x8a\xff\xdf\xbf\x91\xff\xe1\xc3\x95\xff\xe1\xc5\x96\xff\xe1\xc6\x91\xff\xe1\xc5\x93\xff\xe1\xc4\x97\xff\xe0\xc3\x95\xff\xe0\xc2\x93\xff\xe0\xc2\x90\xff\xe0\xc3\x91\xff\xe1\xc3\x91\xff\xe0\xc0\x8e\xff\xe0\xbf\x8d\xff\xdf\xbe\x8d\xff\xdf\xbd\x8c\xff\xdf\xbf\x8d\xff\xe2\xc0\x8e\xff\xe1\xbf\x8c\xff\xdf\xbc\x87\xff\xdf\xbc\x87\xff\xdf\xbd\x8a\xff\xdf\xbc\x8a\xff\xdf\xbb\x89\xff\xdf\xbc\x89\xff\xde\xbd\x8a\xff\xdd\xbc\x89\xff\xde\xbd\x8b\xff\xdc\xba\x88\xff\xdc\xba\x88\xff\xdc\xb9\x87\xff\xdc\xb9\x86\xff\xdd\xbc\x89\xff\xe3\xc7\x98\xff\xe3\xc9\x9f\xff\xe4\xc9\xa1\xff\xe5\xc9\x9e\xff\xe5\xc7\x9a\xff\xe5\xc5\x94\xff\xe4\xc5\x95\xff\xe5\xc5\x93\xff\xe4\xc3\x92\xff\xe5\xc2\x90\xff\xe6\xc4\x95\xff\xe6\xc2\x92\xff\xe5\xc2\x94\xff\xe6\xc5\x96\xff\xe4\xc2\x93\xff\xe5\xc4\x94\xff\xe6\xc3\x93\xff\xe6\xc4\x95\xff\xe7\xc6\x95\xff\xe6\xc6\x95\xff\xe6\xc5\x94\xff\xe6\xc5\x95\xff\xe6\xc8\x98\xff\xe7\xc9\x98\xff\xe7\xc7\x96\xff\xe4\xc3\x8e\xff\xe4\xc3\x92\xff\xe4\xc3\x93\xff\xe5\xc4\x94\xff\xe6\xc5\x95\xff\xe7\xc7\x97\xff\xe9\xcb\x9e\xff\xe8\xcb\x9d\xff\xe9\xcd\x9e\xff\xea\xcd\x9d\xff\xec\xce\x9e\xff\xed\xcf\xa0\xff\xed\xcf\xa0\xff\xec\xcd\x9d\xff\xea\xcb\x99\xff\xe7\xc8\x95\xff\xe7\xc7\x94\xff\xe7\xc6\x93\xff\xe6\xc4\x94\xff\xe6\xc3\x93\xff\xe7\xc4\x95\xff\xe5\xc2\x93\xff\xe7\xc6\x96\xff\xe8\xc8\x99\xff\xe8\xc8\x98\xff\xe6\xc7\x96\xff\xe8\xc8\x99\xff\xe6\xc5\x96\xff\xe8\xc7\x99\xff\xe7\xc7\x99\xff\xe5\xc8\x9a\xff\xe6\xc8\x95\xff\xe8\xc9\x97\xff\xe8\xc9\x96\xff\xe8\xc9\x97\xff\xe6\xc9\x96\xff\xe5\xc8\x96\xff\xe4\xc8\x95\xff\xe4\xc7\x94\xff\xe4\xc6\x94\xff\xe3\xc3\x92\xff\xe1\xbf\x8e\xff\xe3\xc4\x95\xff\xe3\xc3\x95\xff\xe5\xc7\x99\xff\xe5\xc8\x9a\xff\xe5\xc7\x99\xff\xe4\xc6\x96\xff\xe3\xc5\x95\xff\xe4\xc7\x96\xff\xe5\xc6\x97\xff\xe6\xc8\x98\xff\xe4\xc7\x96\xff\xe4\xc8\x96\xff\xe3\xc7\x96\xff\xe3\xc6\x96\xff\xe4\xc4\x96\xff\xe4\xc3\x96\xff\xe3\xc3\x96\xff\xe2\xc1\x94\xff\xe1\xbe\x91\xff\xe1\xbe\x90\xff\xe2\xc0\x92\xff\xe2\xbf\x90\xff\xe2\xc0\x90\xff\xe0\xbf\x8e\xff\xde\xbe\x8d\xff\xde\xbe\x8e\xff\xdc\xbe\x8d\xff\xdc\xbd\x8b\xff\xdc\xbd\x8c\xff\xdd\xc0\x92\xff\xdc\xbe\x90\xff\xdc\xbd\x8f\xff\xdc\xbd\x90\xff\xde\xc0\x92\xff\xdd\xc0\x94\xff\xdc\xbe\x90\xff\xdb\xbd\x8e\xff\xda\xbe\x8d\xff\xdc\xc0\x8f\xff\xdb\xbe\x8d\xff\xdb\xbe\x8d\xff\xda\xbd\x8e\xff\xda\xbc\x8e\xff\xda\xbd\x8e\xff\xda\xbe\x8e\xff\xd9\xbd\x8d\xff\xd8\xba\x8a\xff\xd8\xb7\x89\xff\xd8\xb4\x87\xff\xd6\xb3\x85\xff\xd7\xb4\x88\xff\xd7\xb5\x88\xff\xd4\xb1\x85\xff\xd4\xaf\x82\xff\xd3\xae\x7e\xff\xd1\xad\x7c\xff\xce\xaa\x7a\xff\xcd\xab\x79\xff\xcc\xa8\x77\xff\xcc\xa8\x76\xff\xc8\xa4\x71\xff\xc6\x9f\x6b\xff\xc4\xa1\x6f\xff\xc2\x9f\x6b\xff\xbf\xa0\x6c\xff\xbd\x9e\x6c\xff\xba\x9a\x69\xff\xb6\x95\x65\xff\xb2\x8f\x63\xff\xa7\x87\x5d\xff\x9f\x85\x5c\xff\x96\x82\x5a\xff\x85\x71\x4d\xff\x7b\x68\x44\xff\x75\x62\x40\xff\x75\x60\x3e\xff\x77\x5e\x3f\xff\x7b\x62\x41\xff\xc5\xa3\x78\xff\xc7\xa3\x77\xff\xbb\x93\x67\xff\xa6\x7f\x53\xff\x95\x6f\x45\xff\x7b\x57\x35\xff\x61\x46\x28\xff\x6f\x55\x39\xff\x06\x0e\x0f\xff\x06\x0b\x0f\xff\x06\x12\x16\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Martin the Master Gardener in the Draynor Market.",
    title = "Getting started",
    neededItems = { ["Dramen/lunar staff"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3077, 1301, 3253, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["martin the master gardener"], { distance = 20 }),
      Action.ConversationHighlight:new("Talk about farming problems and fairies."),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Wait 5 minutes, then talk to Martin again.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["martin the master gardener"]),
      Action.ConversationHighlight:new("Talk about farming problems and fairies."),
    },
    postconditions = { Condition.ConversationText:new("problem is then") },
  },
  {
    text = "Equip your dramen/lunar staff and head to the Lumbridge Swamp to enter the Lost City shed.",
    title = "Spirited away",
    actions = {
      Action.Direction:new(3201.5, 1045, 3169),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
    postconditions = { Condition.DistanceTo:new(2452, 589, 4473, 4) },
  },
  {
    text = "Go to Fairy Nuff's cottage, north of the Zanaris bank.",
    actions = { Action.Direction:new(2387, 1149, 4469.5) },
    postconditions = { Condition.ConversationText:new("trashed this house") },
  },
  {
    text = "Pick up Nuff's healing certificate. Keep this in your inventory throughout the quest.",
    actions = { Action.ModelHighlight:new(healingCertificateObj) },
    postconditions = { Condition.InventoryContains:new(Models.items["nuff's certificate"]) },
  },
  {
    text = "Right-click Study Nuff's certificate.",
    actions = { Action.InventoryHighlight:new(Models.items["nuff's certificate"], true) },
    postconditions = { Condition.Generic2DVisible:new(496, 293, 150, certificateTexture) },
  },
  {
    text = "Read the rune temple sign on the ground, south of the cosmic altar.<ul><li>You may use a wicked hood to teleport to the cosmic altar.</li></ul>",
    actions = {
      Action.Direction:new(2409, 629, 4369),
      Action.InventoryHighlight:new(Models.items["wicked hood"]),
    },
    postconditions = { -- rune temple sign is a bigicon, not 2d
      Condition.DistanceTo:new(2409, 629, 4370, 0),
      Condition.DistanceTo:new(2408, 549, 4369, 0),
      Condition.DistanceTo:new(2409, 629, 4369, 0),
    },
  },
  {
    text = "Talk to the Fairy Godfather (located in the Throne Room) about the Queen's disappearance.",
    actions = {
      Action.Direction:new(2447, 645, 4426, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["fairy godfather"], { distance = 4 }),
      Action.ConversationHighlight:new("Where is the Fairy Queen?"),
      Action.ConversationHighlight:new("Do you have any idea who could have done this?"),
      Action.ConversationHighlight:new("Yes, okay."),
    },
    postconditions = { Condition.ConversationText:new("da fairyreengs work") },
  },
  {
    text = "Talk to the Co-ordinator just north of the throne room at the crossroads.",
    actions = {
      Action.Direction:new(2451, 933, 4450, { distance = 12 }),
      Action.ModelHighlight:new(coOrdinator, { distance = 16 }),
      Action.ConversationHighlight:new("The fairy rings."),
    },
    postconditions = { Condition.ConversationText:new("left to do it") },
  },
  {
    text = "Talk to Fairy Fixit near the fairy ring, south-west of the wheat field.",
    title = "Resistance hideout",
    neededItems = {
      ["Dramen/lunar staff"] = { quantity = 1 },
      ["Nuff's certificate"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(2412, 941, 4433, { distance = 20 }),
      Action.ModelHighlight:new(Models.npcs["fairy fixit"], { distance = 24 }),
    },
    postconditions = { Condition.ConversationText:new("get hold of it seems") },
  },
  {
    text = "Equip your dramen/lunar staff, click on the fairy ring, and teleport to the following codes (in this order):<br/><br/><b>AIR</b>.",
    warning = "Nuff's certificate must be in your inventory.",
    actions = {
      Action.Direction:new(2412, 989, 4434),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
    postconditions = { Condition.DistanceTo:new(2700, 341, 3247, 4) },
  },
  {
    text = "<b>DLR</b>.",
    actions = { Action.Direction:new(2700, 341, 3247) },
    postconditions = { Condition.DistanceTo:new(2213, 445, 3099, 4) },
  },
  {
    text = "<b>DJQ</b>. You'll appear in Zanaris. This is intended.",
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = {
      Condition.DistanceTo:new(2412, 989, 4434, 0),
      Condition.ConversationText:new("useful"),
    },
  },
  {
    text = "<b>AJS</b>.",
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = { Condition.DistanceTo:new(2254, 965, 4426, 4) },
  },
  {
    text = "Talk to Fairy Nuff to the north-east. Don't click away during the pause in the conversation.",
    actions = {
      Action.Direction:new(2279, 1101, 4456, { distance = 18 }),
      Action.ModelHighlight:new(Models.npcs["fairy nuff"], { distance = 22 }),
    },
    postconditions = { Condition.ConversationText:new("watching out for him") },
  },
  {
    text = "Return to Zanaris.",
    actions = { Action.Direction:new(2254, 965, 4426) },
    postconditions = { Condition.DistanceTo:new(2412, 989, 4434, 4) },
  },
  {
    text = "Pickpocket the Fairy Godfather.<ul><li>The Fairy Godfather, Fat Rocco and Slim Louie need to be looking away from you when you pickpocket.</li></ul>",
    actions = {
      Action.Direction:new(2447, 645, 4426, { distance = 3 }),
      Action.ModelHighlight:new(Models.npcs["fairy godfather"], { distance = 4 }),
    },
    postconditions = { Condition.InventoryContains:new(Models.items["queen's secateurs"]) },
  },
  {
    text = "Return to the Resistance Hideout by selecting the 'Fairy Resistance HQ' option in the Fairy Ring.",
    warning = "Nuff's certificate must be in your inventory.",
    actions = {
      Action.Direction:new(2412, 989, 4434),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
    postconditions = {
      Condition.DistanceTo:new(2700, 341, 3247, 4),
      Condition.DistanceTo:new(2254, 965, 4426, 4), --hideout
    },
  },
  {
    actions = { Action.Direction:new(2700, 341, 3247) },
    postconditions = {
      Condition.DistanceTo:new(2213, 445, 3099, 4),
      Condition.DistanceTo:new(2254, 965, 4426, 4), --hideout
    },
  },
  {
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = {
      Condition.DistanceTo:new(2412, 989, 4434, 0),
      Condition.ConversationText:new("useful"),
      Condition.DistanceTo:new(2254, 965, 4426, 4), --hideout
    },
  },
  {
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = { Condition.DistanceTo:new(2254, 965, 4426, 4) },
  },
  {
    text = "Talk to Fairy Nuff.",
    actions = {
      Action.Direction:new(2279, 1101, 4456, { distance = 18 }),
      Action.ModelHighlight:new(Models.npcs["fairy nuff"], { distance = 22 }),
    },
    postconditions = { Condition.ConversationText:new("been asleep too long") },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["fairy nuff"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Travel to the Cosmic Entity plane using <b>CKP</b>.",
    title = "Magic essence",
    neededItems = {
      ["Vial of water"] = { quantity = 1 },
      ["Nuff's certificate"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = { Condition.DistanceTo:new(2075, 2365, 4848, 4) },
  },
  {
    text = "Pick the star flower near the fairy ring (may take 1-2 minutes to spawn).",
    actions = { Action.ModelHighlight:new(grownStarFlower, { highlightPriority = "closest" }) },
    postconditions = { Condition.InventoryContains:new(starFlower) },
  },
  {
    text = "Use the star flower on a vial of water.",
    actions = {
      Action.InventoryHighlight:new(Models.items["vial of water"]),
      Action.InventoryHighlight:new(starFlower),
    },
    postconditions = { Condition.InventoryContains:new(magicEssenceUnf) },
  },
  {
    text = "Return to Zanaris to bank. Otherwise, skip to the next step.",
    actions = { Action.Direction:new(2075, 2397, 4848) },
    postconditions = { Condition.DistanceTo:new(2412, 989, 4434, 4) },
  },
  { --unsure if they're aggresive
    text = "Gear to fight, then travel to the Gorak plane using <b>DIR</b>.<ul><li>The Goraks wont attack you until you move off of the fairy ring.</li></ul>",
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = { Condition.ModelVisible:new(gorak) },
  },
  {
    text = "Kill Goraks until you receive Gorak claws.",
    actions = {
      Action.ModelHighlight:new(gorak, { highlightPriority = "closest" }),
      Action.ModelHighlight:new(gorakClaws),
    },
    postconditions = { Condition.InventoryContains:new(gorakClaws) },
  },
  {
    text = "Grind the Gorak claws.",
    actions = { Action.InventoryHighlight:new(gorakClaws) },
    postconditions = { Condition.InventoryContains:new(gorakClawPowder) },
  },
  {
    text = "Use the Gorak claw powder on the Magic essence (unf)",
    actions = {
      Action.InventoryHighlight:new(gorakClawPowder),
      Action.InventoryHighlight:new(magicEssenceUnf),
    },
    postconditions = { Condition.InventoryContains:new(magicEssence) },
  },
  {
    text = "Return to Zanaris.",
    title = "Finishing up",
    actions = {
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
      Action.Direction:new(3038, 1997, 5348),
    },
    postconditions = { Condition.DistanceTo:new(2412, 989, 4434, 4) },
  },
  {
    text = "Return to the Resistance Hideout.",
    actions = {
      Action.Direction:new(2412, 989, 4434),
      Action.InventoryHighlight:new(Models.items["dramen staff"]),
    },
    postconditions = {
      Condition.DistanceTo:new(2700, 341, 3247, 4),
      Condition.DistanceTo:new(2254, 965, 4426, 4), --hideout
    },
  },
  {
    actions = { Action.Direction:new(2700, 341, 3247) },
    postconditions = {
      Condition.DistanceTo:new(2213, 445, 3099, 4),
      Condition.DistanceTo:new(2254, 965, 4426, 4), --hideout
    },
  },
  {
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = {
      Condition.DistanceTo:new(2412, 989, 4434, 0),
      Condition.ConversationText:new("useful"),
      Condition.DistanceTo:new(2254, 965, 4426, 4), --hideout
    },
  },
  {
    actions = { Action.Direction:new(2412, 989, 4434) },
    postconditions = { Condition.DistanceTo:new(2254, 965, 4426, 4) },
  },
  {
    text = "Talk to Fairy Nuff.",
    actions = {
      Action.Direction:new(2279, 1101, 4456, { distance = 18 }),
      Action.ModelHighlight:new(Models.npcs["fairy nuff"], { distance = 22 }),
    },
    postconditions = { Condition.ConversationText:new("give it to") },
  },
  {
    text = "Right-click Use the Magic essence on the Queen, laying on the stretcher near Fairy Nuff.",
    actions = {
      Action.ModelHighlight:new(sleepingFairyQueen),
      Action.InventoryHighlight:new(magicEssence, true),
    },
    postconditions = { Condition.ConversationText:new("what happened") },
  },
  { text = "Talk to the Fairy Queen.", postconditions = { Condition.QuestComplete:new() } },
}

return Quest:new({
  name = "A Fairy Tale II - Cure a Queen",
  steps = steps,
  timeline = Enums.timeline.heroic,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1152576000,
  prereqQuests = { "A Fairy Tale I - Growing Pains" },
  questReqs = {
    Types.QuestReq.skill("Farming", 49),
    Types.QuestReq.skill("Herblore", 57),
    Types.QuestReq.skill("Thieving", 40),
  },
  neededItems = {
    ["Dramen/lunar staff"] = { quantity = 1, model = Models.items["dramen staff"] },
    ["Vial of water"] = { quantity = 1, model = Models.items["vial of water"] },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Gorak"] = { level = "74", quantity = 2 },
  },
})
