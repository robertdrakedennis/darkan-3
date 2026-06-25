local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local fatherReen = Model.new(3783, {
  [2224] = Vertex.new(-2, 725, -59, 106, 78, 54),
  [2229] = Vertex.new(-7, 724, -51, 106, 78, 54),
  [2232] = Vertex.new(2, 725, -59, 106, 78, 54),
  [2234] = Vertex.new(7, 724, -51, 106, 78, 54),
  [2971] = Vertex.new(0, 735, -7, 27, 139, 126),
})
local fatherBadden = Model.new(3405, {
  [2230] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [2235] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [2238] = Vertex.new(2, 725, -59, 107, 79, 55),
  [2240] = Vertex.new(7, 724, -51, 107, 79, 55),
  [2497] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local denath = Model.new(5283, {
  [2662] = Vertex.new(-2, 725, -59, 111, 89, 70),
  [2667] = Vertex.new(-7, 724, -51, 111, 89, 70),
  [2704] = Vertex.new(2, 725, -59, 111, 89, 70),
  [2708] = Vertex.new(7, 724, -51, 111, 89, 70),
  [4466] = Vertex.new(26, 729, 15, 19, 18, 17),
})
local jennifer = Model.new(3546, {
  [1530] = Vertex.new(24, 738, -38, 31, 29, 28),
  [1554] = Vertex.new(-24, 738, -38, 31, 29, 28),
  [1693] = Vertex.new(-2, 716, -57, 107, 79, 55),
  [1699] = Vertex.new(2, 716, -57, 107, 79, 55),
  [1703] = Vertex.new(6, 716, -52, 107, 79, 55),
})
local matthew = Model.new(3849, {
  [1774] = Vertex.new(-2, 725, -59, 107, 79, 55),
  [1779] = Vertex.new(-7, 724, -51, 107, 79, 55),
  [1782] = Vertex.new(2, 725, -59, 107, 79, 55),
  [1784] = Vertex.new(7, 724, -51, 107, 79, 55),
  [2083] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local agrithNaar = Model.new(3732, {
  [661] = Vertex.new(9, 922, -164, 32, 11, 9),
  [663] = Vertex.new(7, 931, -162, 32, 11, 9),
  [665] = Vertex.new(-7, 931, -162, 32, 11, 9),
  [681] = Vertex.new(42, 975, -121, 65, 22, 20),
  [701] = Vertex.new(-42, 975, -121, 65, 22, 20),
})
--#endregion
--#region Objects
local brokenKiln1 = Model.new(360, {
  [18] = Vertex.new(160, 704, 416, 88, 88, 68),
  [81] = Vertex.new(352, 0, -416, 71, 71, 45),
  [139] = Vertex.new(160, 704, 416, 72, 72, 55),
  [166] = Vertex.new(160, 704, 416, 88, 88, 68),
  [330] = Vertex.new(416, 0, 352, 71, 71, 45),
})
local brokenKiln2 = Model.new(210, {
  [60] = Vertex.new(-320, 416, -416, 78, 77, 59),
  [84] = Vertex.new(352, 0, -416, 71, 71, 45),
  [146] = Vertex.new(-448, 352, 384, 71, 71, 45),
  [207] = Vertex.new(416, 0, 352, 71, 71, 45),
  [209] = Vertex.new(416, 0, 352, 71, 71, 45),
})
local brokenKiln3 = Model.new(360, {
  [18] = Vertex.new(128, 768, 416, 88, 88, 68),
  [28] = Vertex.new(128, 768, 416, 72, 72, 55),
  [124] = Vertex.new(128, 768, 416, 88, 88, 68),
  [222] = Vertex.new(352, 0, -416, 71, 71, 45),
  [279] = Vertex.new(416, 0, 352, 71, 71, 45),
})
--#endregion
--#region Items
--#endregion
--#region Quest Items
local dyedSilverlight = Model.new(894, {
  [54] = Vertex.new(279, 1, 251, 254, 254, 254),
  [93] = Vertex.new(258, 1, 273, 254, 254, 254),
  [105] = Vertex.new(258, 1, 273, 254, 254, 254),
  [501] = Vertex.new(-172, 1, -172, 254, 254, 254),
  [654] = Vertex.new(-165, 1, -178, 254, 254, 254),
})
local demonicSigilMould = Model.new(579, {
  [481] = Vertex.new(112, 4, -276, 50, 46, 46),
  [486] = Vertex.new(-112, 4, -276, 50, 46, 46),
  [487] = Vertex.new(112, 4, -276, 50, 46, 46),
  [490] = Vertex.new(-112, 4, -276, 50, 46, 46),
  [495] = Vertex.new(112, 4, -276, 50, 46, 46),
})
local demonicSigil = Model.new(441, {
  [1] = Vertex.new(-100, -4, -156, 50, 46, 46),
  [4] = Vertex.new(-84, -4, -104, 50, 46, 46),
  [35] = Vertex.new(48, 8, 192, 115, 106, 106),
  [179] = Vertex.new(48, 8, 192, 115, 106, 106),
  [261] = Vertex.new(-84, -4, -104, 50, 46, 46),
})
local demonicTome = Model.new(1404, {
  [314] = Vertex.new(-104, 68, -80, 47, 4, 46),
  [318] = Vertex.new(-104, 68, -80, 47, 4, 46),
  [324] = Vertex.new(-104, 68, -80, 47, 4, 46),
  [362] = Vertex.new(-96, 72, 80, 47, 4, 46),
  [368] = Vertex.new(-96, 72, 80, 47, 4, 46),
})
--#endregion

local chatboxTexture =
  "\x8a\x76\x5b\xff\x8f\x7a\x5d\xff\x94\x7e\x5f\xff\x98\x81\x63\xff\x9a\x84\x64\xff\x9e\x87\x66\xff\xa2\x89\x68\xff\xa5\x8a\x6a\xff\xa6\x8c\x6c\xff\xa8\x8f\x6e\xff\xa8\x8f\x6e\xff\xa9\x8f\x6e\xff\xaa\x91\x6f\xff\xab\x92\x71\xff\xac\x93\x72\xff\xac\x93\x72\xff\xac\x94\x72\xff\xac\x95\x73\xff\xad\x97\x75\xff\xaf\x97\x75\xff\xaf\x97\x75\xff\xaf\x97\x75\xff\xaf\x97\x75\xff\xaf\x97\x75\xff\xaf\x97\x76\xff\xb1\x9a\x78\xff\xb3\x9c\x7b\xff\xb4\x9e\x7c\xff\xb7\xa0\x7f\xff\xb7\xa0\x7f\xff\xb7\xa0\x7f\xff\xb7\xa0\x7f\xff\xb7\xa0\x7f\xff\xb7\xa1\x80\xff\xb8\xa2\x81\xff\xb8\xa2\x81\xff\xb8\xa2\x81\xff\xb8\xa2\x81\xff\xba\xa4\x82\xff\xbb\xa6\x84\xff\xbc\xa7\x85\xff\xbc\xa6\x85\xff\xbb\xa4\x84\xff\xba\xa4\x82\xff\xba\xa4\x83\xff\xbb\xa5\x83\xff\xbc\xa6\x84\xff\xbc\xa6\x84\xff\xbc\xa7\x85\xff\xbd\xa7\x85\xff\xbd\xa7\x85\xff\xbe\xa8\x86\xff\xbf\xa9\x87\xff\xbf\xa9\x88\xff\xbf\xaa\x88\xff\xbf\xaa\x88\xff\xbf\xaa\x88\xff\xbe\xa8\x86\xff\xbd\xa7\x85\xff\xbd\xa6\x84\xff\xbd\xa6\x84\xff\xbd\xa6\x84\xff\xbd\xa6\x84\xff\xbd\xa6\x84\xff\xbd\xa7\x85\xff\xbd\xa7\x85\xff\xbf\xa9\x87\xff\xc0\xab\x88\xff\xc0\xab\x89\xff\xc0\xab\x89\xff\xc2\xad\x8b\xff\xc2\xad\x8b\xff\xc1\xad\x8b\xff\xc4\xaf\x8e\xff\xc4\xb1\x8f\xff\xc4\xb1\x8f\xff\xc4\xb0\x8e\xff\xc5\xb0\x8e\xff\xc5\xb0\x8e\xff\xc5\xb0\x8e\xff\xc5\xb1\x8e\xff\xc5\xb1\x8e\xff\xc4\xb1\x8f\xff\xc4\xb1\x8f\xff\xc4\xb1\x8f\xff\xc4\xb1\x8f\xff\xc4\xb1\x8f\xff\xc4\xb0\x8f\xff\xc3\xaf\x8d\xff\xc3\xae\x8d\xff\xc3\xae\x8d\xff\xc3\xae\x8d\xff\xc3\xaf\x8e\xff\xc3\xb0\x8f\xff\xc5\xb2\x91\xff\xc5\xb2\x91\xff\xc5\xb1\x90\xff\xc5\xb1\x90\xff\xc4\xb2\x90\xff\xc4\xb2\x91\xff\xc7\xb5\x94\xff\xc9\xb7\x96\xff\xc9\xb7\x96\xff\xcb\xb8\x97\xff\xca\xb8\x97\xff\xcb\xb7\x97\xff\xcb\xb8\x97\xff\xcb\xb8\x97\xff\xcc\xb9\x98\xff\xce\xbb\x9b\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xcd\xbb\x9b\xff\xcd\xbb\x9b\xff\xcd\xbb\x9b\xff\xcd\xbb\x9b\xff\xcd\xbb\x9b\xff\xcd\xbb\x9a\xff\xcc\xba\x99\xff\xcd\xbb\x9a\xff\xce\xbb\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbb\x9b\xff\xcd\xbb\x9b\xff\xcd\xbb\x9b\xff\xcd\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9b\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9a\xff\xce\xbb\x9b\xff\xce\xbb\x9b\xff\xce\xbb\x9b\xff\xce\xbb\x9b\xff\xce\xbb\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9b\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xcf\xbc\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xd0\xbe\x9e\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xd0\xbe\x9e\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9c\xff\xd0\xbe\x9d\xff\xcf\xbe\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xcf\xbc\x9c\xff\xcf\xbc\x9c\xff\xcf\xbc\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xce\xba\x9a\xff\xcd\xba\x99\xff\xcd\xba\x9a\xff\xcd\xbb\x9a\xff\xcc\xba\x9a\xff\xcd\xba\x9a\xff\xcd\xba\x9b\xff\xcd\xba\x9b\xff\xcd\xba\x9a\xff\xcd\xbb\x9a\xff\xcd\xbb\x9b\xff\xcd\xbb\x9b\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xcf\xbd\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xcf\xbd\x9b\xff\xcf\xbd\x9b\xff\xce\xbd\x9b\xff\xce\xbc\x9a\xff\xcf\xbd\x9b\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9b\xff\xce\xbc\x9c\xff\xcf\xbd\x9c\xff\xcf\xbd\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xcf\xbd\x9d\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9c\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xce\xbc\x9b\xff\xcd\xbb\x9a\xff\xcb\xba\x98\xff\xcb\xb8\x97\xff\xcb\xb8\x97\xff\xcb\xb8\x97\xff\xcb\xb8\x97\xff\xca\xb8\x96\xff\xca\xb9\x97\xff\xcb\xb9\x97\xff\xcb\xb9\x97\xff\xcb\xb9\x97\xff\xcb\xb8\x97\xff\xca\xb7\x95\xff\xc9\xb6\x94\xff\xc9\xb6\x94\xff\xc9\xb6\x94\xff\xc8\xb5\x93\xff\xc8\xb5\x93\xff\xc8\xb5\x94\xff\xc8\xb4\x93\xff\xc5\xb3\x92\xff\xc7\xb3\x91\xff\xc6\xb2\x91\xff\xc6\xb2\x91\xff\xc5\xb1\x90\xff\xc4\xb0\x8f\xff\xc4\xb0\x8f\xff\xc4\xb1\x8e\xff\xc2\xb0\x8e\xff\xc2\xaf\x8d\xff\xc2\xae\x8d\xff\xc3\xaf\x8d\xff\xc3\xaf\x8d\xff\xc4\xae\x8c\xff\xc3\xad\x8c\xff\xc2\xad\x8b\xff\xc2\xad\x8b\xff\xc1\xad\x8b\xff\xc0\xad\x8b\xff\xc1\xad\x8b\xff\xc0\xad\x89\xff\xc0\xab\x88\xff\xbf\xaa\x88\xff\xbf\xaa\x88\xff\xbf\xab\x88\xff\xc0\xab\x88\xff\xc0\xaa\x89\xff\xc0\xaa\x89\xff\xc0\xac\x8a\xff\xc0\xac\x8a\xff\xc0\xac\x8a\xff\xc2\xad\x8a\xff\xc2\xaf\x8c\xff\xc2\xb0\x8d\xff\xc5\xb1\x8e\xff\xc5\xb1\x90\xff\xc5\xb2\x91\xff\xc6\xb3\x91\xff\xc6\xb3\x92\xff\xc6\xb3\x92\xff\xc6\xb2\x91\xff\xc5\xb2\x91\xff\xc5\xb2\x90\xff\xc5\xb2\x91\xff\xc5\xb2\x91\xff\xc5\xb2\x8f\xff\xc4\xb1\x90\xff\xc4\xb0\x8f\xff\xc3\xb0\x8e\xff\xc3\xae\x8e\xff\xc1\xad\x8b\xff\xc1\xac\x89\xff\xbf\xab\x89\xff\xbf\xab\x88\xff\xbf\xab\x89\xff\xbf\xab\x89\xff\xbf\xab\x89\xff\xbf\xab\x8a\xff\xbf\xab\x89\xff\xbe\xa9\x88\xff\xbd\xa8\x86\xff\xbd\xa9\x87\xff\xbd\xa9\x87\xff\xbd\xa8\x87\xff\xbd\xa8\x86\xff\xbd\xa8\x85\xff\xbc\xa7\x85\xff\xbb\xa5\x83\xff\xbb\xa5\x83\xff\xbb\xa5\x82\xff\xba\xa4\x82\xff\xba\xa4\x82\xff\xba\xa4\x82\xff\xba\xa4\x82\xff\xba\xa4\x82\xff\xb9\xa4\x81\xff\xbb\xa4\x82\xff\xba\xa4\x81\xff\xba\xa3\x81\xff\xb9\xa4\x81\xff\xb9\xa3\x81\xff\xb9\xa4\x81\xff\xb9\xa4\x81\xff\xb9\xa2\x80\xff\xb8\xa1\x7f\xff\xb7\xa1\x7f\xff\xb7\xa0\x7e\xff\xb8\xa1\x7f\xff\xb8\xa1\x7f\xff\xb8\xa1\x7f\xff\xb8\xa2\x80\xff\xba\xa5\x83\xff\xbb\xa6\x83\xff\xbc\xa6\x83\xff\xbb\xa5\x82\xff\xbb\xa4\x81\xff\xba\xa3\x7f\xff\xb8\xa2\x7f\xff\xb8\xa1\x7f\xff\xb8\xa1\x7f\xff\xb8\xa0\x7e\xff\xb8\xa1\x7e\xff\xb8\xa1\x7d\xff\xb6\x9f\x7d\xff\xb6\x9f\x7c\xff\xb6\x9f\x7c\xff\xb6\x9f\x7c\xff\xb6\x9f\x7b\xff\xb5\x9f\x7b\xff\xb6\x9f\x7b\xff\xb5\x9f\x7b\xff\xb5\x9f\x7b\xff\xb5\x9f\x7c\xff\xb6\xa0\x7d\xff\xb6\xa0\x7d\xff\xb5\xa0\x7d\xff\xb5\xa0\x7d\xff\xb5\x9f\x7c\xff\xb5\x9f\x7d\xff\xb5\x9f\x7d\xff\xb5\xa0\x7d\xff\xb6\xa0\x7d\xff\xb6\xa1\x7e\xff\xb6\xa1\x7e\xff\xb6\x9f\x7d\xff\xb5\x9f\x7b\xff\xb5\x9d\x7b\xff\xb5\x9e\x7b\xff\xb5\x9e\x7b\xff\xb5\x9e\x7b\xff\xb7\xa0\x7e\xff\xb7\xa0\x7e\xff\xb6\xa0\x7d\xff\xb5\x9e\x7a\xff\xb4\x9e\x7a\xff\xb3\x9c\x78\xff\xb2\x9c\x79\xff\xb3\x9b\x77\xff\xb3\x9b\x77\xff\xb2\x9a\x76\xff\xb2\x9a\x75\xff\xb2\x9a\x75\xff\xb1\x99\x76\xff\xb1\x99\x76\xff\xb1\x9a\x76\xff\xb1\x99\x76\xff\xb2\x9a\x76\xff\xb2\x9a\x76\xff\xb2\x9a\x76\xff\xb1\x9a\x77\xff\xb2\x9a\x77\xff\xb0\x98\x75\xff\xb0\x97\x74\xff\xaf\x97\x73\xff\xaf\x97\x74\xff\xaf\x97\x74\xff\xb0\x97\x75\xff\xb0\x97\x75\xff\xb0\x98\x75\xff\xb0\x98\x76\xff\xb0\x98\x75\xff\xb0\x98\x75\xff\xaf\x98\x75\xff\xaf\x98\x74\xff\xae\x98\x74\xff\xaf\x97\x74\xff\xae\x97\x74\xff\xac\x95\x72\xff\xaa\x92\x70\xff\xa8\x91\x6e\xff\xa6\x8f\x6d\xff\xa3\x8d\x6c\xff\x9f\x8a\x6a\xff\x9b\x87\x67\xff\x96\x83\x64\xff\x91\x7f\x61\xff"

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Father Reen south of Al Kharid bank.",
    title = "Starting out",
    tpHint = {
      type = Enums.tpHintType.icon,
      hover = "Al Kharid lodestone",
      url = "Al_Kharid_lodestone_icon.png",
    },
    actions = {
      Action.Direction:new(3272, 1789, 3157, { distance = 20 }),
      Action.ModelHighlight:new(fatherReen, { distance = 24 }),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  { text = "[Accept Quest]", postconditions = { Condition.QuestStarted:new() } },
  {
    text = "Contiue talking to Father Reen.<ul><li>He will give you Silverlight if you've dyed or destroyed it.</li></ul>",
    actions = { Action.ModelHighlight:new(fatherReen) },
    postconditions = { Condition.ConversationText:new("finished some business here") },
  },
  {
    text = "Talk to Father Badden at Uzer, east of Shantay Pass.",
    title = "Evil Dave",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "3",
      hover = "South of the Desert Eagle's Eyrie",
      url = "Traveller's_necklace_(new).png",
    },
    neededItems = {
      ["Silverlight"] = { quantity = 1 },
      ["Empty vial"] = { quantity = 1 },
      ["3 pieces of a black outfit (refer to wiki)"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3482, 741, 3092, { distance = 22 }),
      Action.ModelHighlight:new(fatherBadden, { distance = 26 }),
      Action.ConversationHighlight:new("Reen sent me."),
      Action.ConversationHighlight:new("So what do you want me to do?"),
      Action.ConversationHighlight:new("How can I do that?"),
    },
    postconditions = { Condition.ConversationText:new("some way to convince") },
  },
  {
    text = "Pick a black mushroom.",
    actions = { Action.Direction:new(3495, 1373, 3088, { tile = true }) },
    postconditions = {
      Condition.InventoryContains:new(Models.items["black mushroom"]),
      Condition.InventoryContains:new(dyedSilverlight),
    },
  },
  {
    text = "Use the black mushroom ink on the silverlight.",
    actions = {
      Action.InventoryHighlight:new(Models.items["black mushroom ink"]),
      Action.InventoryHighlight:new(Models.items["silverlight"]),
    },
    postconditions = { Condition.InventoryContains:new(dyedSilverlight) },
  },
  {
    text = "Go down the stairs.",
    actions = { Action.Direction:new(3492, 1333, 3090) },
    postconditions = { Condition.DistanceTo:new(2721, 805, 4886, 4) },
  },
  {
    text = "Pick up the strange implement.",
    actions = { Action.Direction:new(2713, 805, 4913, { tile = true }) },
    postconditions = { Condition.InventoryContains:new(Models.items["strange implement"]) },
  },
  {
    text = "Talk to Evil Dave while wearing your three black items and your dyed Silverlight.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["evil dave"]),
      Action.InventoryHighlight:new(dyedSilverlight),
      Action.ConversationHighlight:new("I want to join your group."),
      Action.ConversationHighlight:new("I'm evil!"),
    },
    postconditions = { Condition.InInstance:new() },
  },
  { text = "Watch the cutscene.", postconditions = { Condition.ConversationText:new("wish to join us") } }, --not tested
  {
    text = "Talk to Denath.",
    title = "Demonic Sigil",
    neededItems = { ["Silver bar (metal bank works)"] = { quantity = 1 } },
    actions = {
      Action.ModelHighlight:new(denath),
      Action.ConversationHighlight:new("What do I have to do?"),
    },
    postconditions = { Condition.ConversationText:new("with my preparations") },
  },
  {
    text = "Talk to Jennifer to get a demonic sigil mould.",
    actions = {
      Action.ModelHighlight:new(jennifer),
      Action.ConversationHighlight:new("Do you have the demonic sigil mould?"),
    },
    postconditions = { Condition.InventoryContains:new(demonicSigilMould) },
  },
  {
    text = "Talk to Matthew.",
    actions = {
      Action.ModelHighlight:new(matthew),
      Action.ConversationHighlight:new("Do you know what happened to Josef?"),
    },
    postconditions = { Condition.ConversationText:new("Denath is really up to") },
  },
  {
    text = "Go to any furnace and smelt a demonic sigil.<ul><li>Your black outfit can be discarded at this point and replaced with combat gear.</li><li>Keep Silverlight for the duration of the quest.</li></ul>",
    actions = { Action.ConversationHighlight:new("Yes.") },
    postconditions = { Condition.InventoryContains:new(demonicSigil) },
  },
  {
    text = "Talk to the clay golem outside the ruins.",
    title = "Ritual",
    tpHint = {
      type = Enums.tpHintType.icon,
      text = "3",
      hover = "South of the Desert Eagle's Eyrie",
      url = "Traveller's_necklace_(new).png",
    },
    neededItems = { ["Demonic sigil"] = { quantity = 1, model = demonicSigil } },
    actions = {
      Action.Direction:new(3482, 741, 3092, { distance = 22 }),
      Action.ModelHighlight:new(Models.npcs["clay golem"], { distance = 26 }),
      Action.ConversationHighlight:new("Did you see anything happen last night?"),
    },
    postconditions = { Condition.ConversationText:new("incident from my memory") },
  },
  {
    text = "Look in the nearby kilns for a demonic tome.",
    actions = {
      Action.ModelHighlight:new(brokenKiln1, { highlightPriority = "all" }),
      Action.ModelHighlight:new(brokenKiln2, { highlightPriority = "all" }),
      Action.ModelHighlight:new(brokenKiln3),
    },
    postconditions = { Condition.InventoryContains:new(demonicTome) },
  },
  {
    text = "Go down the stairs.",
    actions = { Action.Direction:new(3492, 1333, 3090) },
    postconditions = { Condition.DistanceTo:new(2721, 805, 4886, 4) },
  },
  {
    text = "Enter the door to the north.",
    actions = { Action.ModelHighlight:new(Models.objects["uzer portal door"]) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Matthew.",
    actions = { Action.ModelHighlight:new(matthew) },
    postconditions = { Condition.ConversationText:new("We must prepare") },
  },
  {
    text = "Talk to Denath.",
    warning = "Be sure to remember the incantation, as you'll need it again later in the quest.",
    actions = {
      Action.ModelHighlight:new(denath),
      Action.ConversationHighlight:new("I forgot the incantation."),
    },
    postconditions = { Condition.ConversationText:new("Ignoramus") },
  },
  {
    text = "Complete the circle and click the sigil to chant the incantation.",
    actions = {
      Action.Direction:new(-2, 0, 18, { instance = true, tile = true }),
      Action.InventoryHighlight:new(demonicSigil),
    },
    postconditions = { Condition.ModelVisible:new(demonicSigil) },
  },
  {
    text = "Be sure to take the sigil from the floor, or you will have to make a new one at a furnace.",
    actions = { Action.ModelHighlight:new(demonicSigil) },
    postconditions = { Condition.InventoryContains:new(demonicSigil, 2) },
  },
  {
    text = "Enter the portal.",
    actions = { Action.ModelHighlight:new(Models.objects["uzer portal"]) },
    postconditions = { Condition.NotInInstance:new() },
  },
  {
    text = "Take Tanya's sigil.",
    actions = { Action.ModelHighlight:new(demonicSigil) },
    postconditions = { Condition.InventoryContains:new(demonicSigil, 3) },
  },
  {
    text = "Tell Evil Dave to return to the lair - you should have 4 sigils now.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["evil dave"]),
      Action.ConversationHighlight:new("You've got to get back to the throne room!"),
    },
    postconditions = { Condition.InventoryContains:new(demonicSigil, 4) },
  },
  {
    text = "Go up the stairs",
    actions = { Action.Direction:new(2721.5, 1305, 4885) },
    postconditions = { Condition.DistanceTo:new(3491, 1333, 3090, 4) },
  },
  {
    text = "Talk to Father Badden.",
    title = "Rallying the troops",
    actions = { Action.ModelHighlight:new(fatherBadden) },
    postconditions = { Condition.ConversationText:new("We must prepare") },
  },
  {
    text = "Talk to Father Reen.",
    actions = {
      Action.ModelHighlight:new(fatherReen),
      Action.ConversationHighlight:new("Oh, don't be so simple-minded!"),
    },
    postconditions = { Condition.ConversationText:new("hurry inside") },
  },
  {
    text = "Talk to the golem.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["clay golem"]),
      Action.ConversationHighlight:new("[Use the strange implement]"),
    },
    postconditions = { Condition.ConversationText:new("through the portal") },
  },
  {
    text = "Go down the stairs.",
    actions = { Action.Direction:new(3492, 1333, 3090) },
    postconditions = { Condition.DistanceTo:new(2721, 805, 4886, 4) },
  },
  {
    text = "Enter the door to the north.",
    actions = { Action.ModelHighlight:new(Models.objects["uzer portal door"]) },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Talk to Matthew to start the incantation.",
    title = "The final fight",
    actions = {
      Action.ModelHighlight:new(matthew),
      Action.ConversationHighlight:new("Yes."),
    },
    neededItems = {
      ["Silverlight (dyed)"] = { quantity = 1, model = dyedSilverlight },
      ["Demonic sigil"] = { quantity = 4 },
    },
    postconditions = { Condition.ConversationText:new("we go") }, --not tested
  },
  {
    text = "Stand on the marked tile and chant with the demonic sigil.",
    actions = {
      Action.Direction:new(0, 0, 19, { instance = true, tile = true }),
      Action.InventoryHighlight:new(demonicSigil),
    },
    postconditions = { Condition.Generic2DVisible:new(506, 98, 50, chatboxTexture) },
  },
  {
    text = "Chant Demonic Sigil and say the incantation <b>backwards</b>.<ul><li>The incantation is on page 6 of the demonic tome.</li></ul>",
    postconditions = { Condition.ConversationText:new("prepare to die") },
  },
  {
    text = "Kill the demon with the Silverlight (dyed). After defeating it, you will have the Darklight equipped.<ul><li>You can damage the demon with any combat style, but you must equip the Silverlight (dyed) as it dies.</li></ul>",
    actions = { Action.InventoryHighlight:new(dyedSilverlight) },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "Shadow of the Storm",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.medium,
  releaseDate = 1131926400,
  prereqQuests = { "Demon Slayer", "The Golem" },
  questReqs = { Types.QuestReq.skill("Crafting", 30) },
  neededItems = {
    ["Silverlight"] = { quantity = 1, model = Models.items["silverlight"], duringQuest = true },
    ["Silver bar (metal bank works)"] = { quantity = 1, model = Models.items["silver bar"] },
    ["Empty vial"] = { quantity = 1, model = Models.items["vial"] },
    ["3 pieces of a black outfit (refer to wiki)"] = { quantity = 1 },
    ["Strange implement"] = { quantity = 1, model = Models.items["strange implement"], duringQuest = true },
  },
  recommendedItems = {
    ["Waterskins"] = { quantity = 5 },
    ["Food"] = { quantity = 5 },
    ["Desert robes"] = { quantity = 1 },
    ["Energy potions"] = { quantity = 2 },
    ["Amulet of glory"] = { quantity = 1 },
    ["Ring of duelling"] = { quantity = 1 },
    ["Traveller's necklace"] = { quantity = 1 },
  },
  combatNPCs = { ["Agrith Naar"] = { level = "84", quantity = 1 } },
})
