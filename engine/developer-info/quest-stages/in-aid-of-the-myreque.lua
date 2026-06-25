local Quest = require("core.quest")
local Condition = require("core.condition")
local Action = require("core.action")
local Models = require("util.models")
local Enums = require("core.enums")
local Types = require("core.types")
local Location = require("util.location")
local Model, Vertex = Types.Model, Types.Vertex

--#region NPCs
local florin = Model.new(3159, {
  [1651] = Vertex.new(-2, 725, -59, 118, 118, 90),
  [1656] = Vertex.new(-7, 724, -51, 118, 118, 90),
  [1659] = Vertex.new(2, 725, -59, 118, 118, 90),
  [1661] = Vertex.new(7, 724, -51, 118, 118, 90),
  [2786] = Vertex.new(-30, 721, -31, 47, 36, 14),
})
local razvan = Model.new(3870, {
  [2809] = Vertex.new(-2, 725, -59, 118, 118, 90),
  [2814] = Vertex.new(-7, 724, -51, 118, 118, 90),
  [2817] = Vertex.new(2, 725, -59, 118, 118, 90),
  [2819] = Vertex.new(7, 724, -51, 118, 118, 90),
  [3076] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local aurel = Model.new(4260, {
  [2299] = Vertex.new(-2, 725, -59, 118, 118, 90),
  [2304] = Vertex.new(-7, 724, -51, 118, 118, 90),
  [2307] = Vertex.new(2, 725, -59, 118, 118, 90),
  [2309] = Vertex.new(7, 724, -51, 118, 118, 90),
  [2794] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local cornelius = Model.new(3387, {
  [1945] = Vertex.new(-2, 725, -59, 118, 118, 90),
  [1950] = Vertex.new(-7, 724, -51, 118, 118, 90),
  [1953] = Vertex.new(2, 725, -59, 118, 118, 90),
  [1955] = Vertex.new(7, 724, -51, 118, 118, 90),
  [2593] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local bankerCornelius = Model.new(3831, {
  [2770] = Vertex.new(-2, 725, -59, 118, 118, 90),
  [2775] = Vertex.new(-7, 724, -51, 118, 118, 90),
  [2778] = Vertex.new(2, 725, -59, 118, 118, 90),
  [2780] = Vertex.new(7, 724, -51, 118, 118, 90),
  [3037] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local grigore = Model.new(3342, {
  [1852] = Vertex.new(-2, 725, -59, 118, 118, 90),
  [1857] = Vertex.new(-7, 724, -51, 118, 118, 90),
  [1860] = Vertex.new(2, 725, -59, 118, 118, 90),
  [1862] = Vertex.new(7, 724, -51, 118, 118, 90),
  [2548] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local gadderanks = Model.new(3885, {
  [2584] = Vertex.new(-2, 725, -59, 101, 97, 77),
  [2589] = Vertex.new(-7, 724, -51, 101, 97, 77),
  [2592] = Vertex.new(2, 725, -59, 101, 97, 77),
  [2594] = Vertex.new(7, 724, -51, 101, 97, 77),
  [3187] = Vertex.new(0, 735, -7, 28, 140, 127),
})
local vampyreJuvinateM = Model.any({
  Model.new(5487, {
    [79] = Vertex.new(0, 756, -7, 55, 143, 29),
    [363] = Vertex.new(3, 730, -58, 145, 137, 133),
    [365] = Vertex.new(6, 730, -58, 145, 137, 133),
    [455] = Vertex.new(-3, 730, -58, 145, 137, 133),
    [459] = Vertex.new(-6, 730, -58, 145, 137, 133),
  }),
  Model.new(5487, {
    [79] = Vertex.new(0, 756, -7, 39, 101, 20),
    [363] = Vertex.new(3, 730, -58, 100, 95, 92),
    [365] = Vertex.new(6, 730, -58, 100, 95, 92),
    [455] = Vertex.new(-3, 730, -58, 100, 95, 92),
    [459] = Vertex.new(-6, 730, -58, 100, 95, 92),
  }),
})
local vampyreJuvinateF = Model.new(5613, {
  [25] = Vertex.new(1, 709, 13, 55, 143, 29),
  [381] = Vertex.new(3, 698, -29, 145, 137, 133),
  [383] = Vertex.new(6, 698, -29, 145, 137, 133),
  [455] = Vertex.new(-3, 698, -29, 145, 137, 133),
  [459] = Vertex.new(-6, 698, -29, 145, 137, 133),
})
local wiskit = Model.new(3927, {
  [2698] = Vertex.new(-2, 725, -59, 123, 109, 94),
  [2703] = Vertex.new(-7, 724, -51, 123, 109, 94),
  [2706] = Vertex.new(2, 725, -59, 123, 109, 94),
  [2708] = Vertex.new(7, 724, -51, 123, 109, 94),
  [2965] = Vertex.new(0, 735, -7, 28, 140, 127),
})
--#endregion
--#region Objects
local rubbleCoveringTrapdoor = Model.new(600, {
  [11] = Vertex.new(-400, -4, -328, 28, 35, 3),
  [329] = Vertex.new(336, 44, 476, 28, 35, 3),
  [341] = Vertex.new(404, 44, 496, 28, 35, 3),
  [393] = Vertex.new(-400, -4, -328, 28, 35, 3),
  [465] = Vertex.new(-400, 48, -172, 28, 35, 3),
})
local closedTrapdoor = Model.new(90, {
  [71] = Vertex.new(-188, 40, 224, 59, 65, 41),
  [75] = Vertex.new(-224, 40, -224, 59, 65, 41),
  [81] = Vertex.new(192, 40, -224, 59, 65, 41),
  [89] = Vertex.new(224, 40, 224, 59, 65, 41),
})
local openTrapdoor = Model.new(396, {
  [188] = Vertex.new(256, -512, -256, 29, 13, 2),
  [335] = Vertex.new(-256, -512, -192, 58, 49, 23),
  [356] = Vertex.new(-256, -512, -192, 55, 44, 11),
  [359] = Vertex.new(-164, -512, -192, 55, 44, 11),
  [360] = Vertex.new(-256, -512, -192, 55, 44, 11),
})
local rubble = Model.any({
  Model.new(564, {
    [114] = Vertex.new(212, 0, -220, 44, 43, 28),
    [134] = Vertex.new(-236, 0, -216, 44, 43, 28),
    [273] = Vertex.new(-236, 0, -216, 44, 43, 28),
    [489] = Vertex.new(-240, 0, 208, 44, 43, 28),
    [501] = Vertex.new(156, 0, 244, 44, 43, 28),
  }),
})
local ladder = Model.new(1278, {
  [873] = Vertex.new(4944, 1620, 3388, 53, 58, 37),
  [905] = Vertex.new(4760, 1612, 3360, 53, 58, 37),
  [927] = Vertex.new(4936, 1752, 3408, 53, 58, 37),
  [959] = Vertex.new(4760, 1744, 3380, 53, 58, 37),
  [1227] = Vertex.new(4940, 1480, 3368, 53, 58, 37),
})
local patchedRoof = Model.new(165, {
  [59] = Vertex.new(152, -164, 224, 60, 55, 12),
  [146] = Vertex.new(160, -108, 204, 84, 91, 87),
  [156] = Vertex.new(244, -176, 188, 84, 91, 87),
  [158] = Vertex.new(244, -172, 204, 84, 91, 87),
  [162] = Vertex.new(244, -176, -184, 84, 91, 87),
})
local patchedWall = Model.new(504, {
  [437] = Vertex.new(-216, 904, 140, 102, 111, 106),
  [441] = Vertex.new(-216, 880, 204, 102, 111, 106),
  [443] = Vertex.new(-216, 896, 208, 102, 111, 106),
  [489] = Vertex.new(-216, 840, -192, 102, 111, 106),
  [491] = Vertex.new(-216, 856, -188, 102, 111, 106),
})
local brokenBankBooth = Model.new(222, {
  [119] = Vertex.new(192, 384, -32, 61, 66, 34),
  [167] = Vertex.new(-192, 900, 28, 61, 66, 34),
  [177] = Vertex.new(-192, 836, 28, 61, 66, 34),
  [184] = Vertex.new(-192, 900, 28, 61, 66, 34),
  [212] = Vertex.new(-192, 900, 28, 61, 66, 34),
})
local fixedBankBooth = Model.new(576, {
  [365] = Vertex.new(128, 832, -32, 70, 76, 39),
  [369] = Vertex.new(132, 832, 32, 70, 76, 39),
  [444] = Vertex.new(-192, 756, -32, 70, 76, 39),
  [464] = Vertex.new(-188, 832, -32, 70, 76, 39),
  [510] = Vertex.new(-192, 752, 32, 70, 76, 39),
})
local bankWallHole = Model.new(18, {
  [3] = Vertex.new(-256, 908, -220, 44, 40, 40),
  [13] = Vertex.new(-256, 908, 216, 44, 40, 40),
  [17] = Vertex.new(-256, 904, 212, 44, 40, 40),
})
local brokenFurnace = Model.new(1476, {
  [42] = Vertex.new(-448, 164, 508, 44, 52, 16),
  [312] = Vertex.new(472, 164, 468, 44, 52, 16),
  [1020] = Vertex.new(100, 1428, 136, 58, 66, 34),
  [1032] = Vertex.new(-116, 1428, 136, 58, 66, 34),
  [1034] = Vertex.new(-116, 1428, 136, 58, 66, 34),
})
local fixedFurnace = Model.new(1464, {
  [642] = Vertex.new(-192, 836, -100, 62, 46, 25),
  [644] = Vertex.new(-196, 828, -104, 62, 46, 25),
  [648] = Vertex.new(-224, 840, -8, 62, 46, 25),
  [654] = Vertex.new(-284, 644, -172, 62, 46, 25),
  [656] = Vertex.new(-288, 636, -176, 62, 46, 25),
})
local fixedLoadedFurnace = Model.new(2322, {
  [1500] = Vertex.new(-192, 836, -100, 62, 46, 25),
  [1502] = Vertex.new(-196, 828, -104, 62, 46, 25),
  [1506] = Vertex.new(-224, 840, -8, 62, 46, 25),
  [1512] = Vertex.new(-284, 644, -172, 62, 46, 25),
  [1514] = Vertex.new(-288, 636, -176, 62, 46, 25),
})
local litFurnace = Model.new(2778, {
  [1956] = Vertex.new(-192, 836, -100, 62, 46, 25),
  [1958] = Vertex.new(-196, 828, -104, 62, 46, 25),
  [1962] = Vertex.new(-224, 840, -8, 62, 46, 25),
  [1968] = Vertex.new(-284, 644, -172, 62, 46, 25),
  [1970] = Vertex.new(-288, 636, -176, 62, 46, 25),
})
local keyholeWall = Model.new(714, {
  [534] = Vertex.new(2043, 2100, 1301, 66, 66, 61),
  [708] = Vertex.new(1588, 672, 1024, 0, 0, 0),
  [709] = Vertex.new(1588, 2100, 1536, 30, 31, 28),
  [710] = Vertex.new(2048, 2100, 1536, 30, 31, 28),
  [714] = Vertex.new(2048, 2100, 1024, 30, 31, 28),
})
local openTempleTrapdoor = Model.new(522, {
  [456] = Vertex.new(-235, -533, -197, 74, 67, 47),
  [468] = Vertex.new(239, -533, 200, 74, 67, 47),
  [486] = Vertex.new(195, -533, -229, 74, 67, 47),
  [507] = Vertex.new(239, -43, 232, 74, 67, 47),
  [522] = Vertex.new(-191, -533, 232, 74, 67, 47),
})
local tomb = Model.new(8634, {
  [1482] = Vertex.new(303, 340, -68, 111, 112, 113),
  [3484] = Vertex.new(220, 358, -112, 127, 129, 131),
  [6750] = Vertex.new(-343, 264, -186, 97, 98, 99),
  [6780] = Vertex.new(-343, 264, 186, 97, 98, 99),
  [7218] = Vertex.new(519, 0, 155, 111, 112, 113),
})
--#endregion
--#region Items
local bronzeHatchet = Model.multi({
  Model.new(480, {
    [81] = Vertex.new(-28, 0, 59, 98, 72, 40),
    [93] = Vertex.new(-45, 0, 121, 98, 72, 40),
    [101] = Vertex.new(-13, 0, 100, 84, 62, 34),
    [118] = Vertex.new(-45, 0, 121, 132, 97, 54),
    [434] = Vertex.new(48, 0, 110, 79, 62, 41),
  }),
  Model.new(6, {
    [1] = Vertex.new(-26, 28, 94, 155, 122, 80),
    [2] = Vertex.new(-20, 28, 70, 155, 122, 80),
    [3] = Vertex.new(-34, 17, 79, 155, 122, 80),
    [4] = Vertex.new(-26, 28, 94, 155, 122, 80),
    [5] = Vertex.new(-10, 24, 85, 155, 122, 80),
  }),
})
local mackerelOrSnail = Model.any({
  Model.new(174, { --mackerel
    [157] = Vertex.new(-116, 20, -16, 72, 66, 66),
    [160] = Vertex.new(-116, -8, -16, 72, 66, 66),
    [165] = Vertex.new(-80, 16, -44, 147, 146, 112),
    [167] = Vertex.new(-48, 28, -52, 147, 146, 112),
    [171] = Vertex.new(-60, -16, -64, 147, 146, 112),
  }),
  Model.multi({ --lean snail
    Model.new(387, {
      [3] = Vertex.new(-60, 40, -92, 74, 77, 31),
      [80] = Vertex.new(-244, 0, -60, 104, 104, 42),
      [87] = Vertex.new(-244, 0, -60, 104, 104, 42),
      [285] = Vertex.new(-100, 56, -104, 74, 77, 31),
      [290] = Vertex.new(-88, 52, -88, 74, 77, 31),
    }),
    Model.new(54, {
      [2] = Vertex.new(220, 0, 116, 74, 77, 31, 0.1843),
      [3] = Vertex.new(168, 0, 0, 74, 77, 31, 0.1843),
      [5] = Vertex.new(100, 0, 220, 74, 77, 31, 0.1843),
      [6] = Vertex.new(220, 0, 116, 74, 77, 31, 0.1843),
      [17] = Vertex.new(-60, 0, -116, 74, 77, 31, 0.1843),
    }),
  }),
  Model.multi({ --thin snail
    Model.new(387, {
      [3] = Vertex.new(-60, 40, -92, 82, 89, 46),
      [80] = Vertex.new(-244, 0, -60, 105, 111, 70),
      [87] = Vertex.new(-244, 0, -60, 105, 111, 70),
      [285] = Vertex.new(-100, 56, -104, 82, 89, 46),
      [290] = Vertex.new(-88, 52, -88, 82, 89, 46),
    }),
    Model.new(54, {
      [2] = Vertex.new(220, 0, 116, 82, 89, 46, 0.1843),
      [3] = Vertex.new(168, 0, 0, 82, 89, 46, 0.1843),
      [5] = Vertex.new(100, 0, 220, 82, 89, 46, 0.1843),
      [6] = Vertex.new(220, 0, 116, 82, 89, 46, 0.1843),
      [17] = Vertex.new(-60, 0, -116, 82, 89, 46, 0.1843),
    }),
  }),
})
local crate = Model.new(696, {
  [84] = Vertex.new(-152, 304, 152, 28, 35, 3),
  [193] = Vertex.new(152, 304, -152, 59, 65, 41),
  [197] = Vertex.new(152, 304, -152, 59, 65, 41),
  [199] = Vertex.new(-152, 304, 152, 59, 65, 41),
  [203] = Vertex.new(-152, 304, 152, 59, 65, 41),
})
local gadderhammer = Model.new(408, {
  [16] = Vertex.new(88, 144, 244, 129, 110, 11),
  [19] = Vertex.new(128, 144, 204, 129, 110, 11),
  [20] = Vertex.new(88, 144, 244, 129, 110, 11),
  [22] = Vertex.new(128, 144, 204, 129, 110, 11),
  [24] = Vertex.new(88, 144, 244, 129, 110, 11),
})
local steelMedHelm = Model.new(456, {
  [41] = Vertex.new(-8, 99, -48, 148, 143, 156),
  [72] = Vertex.new(8, 99, -48, 148, 143, 156),
  [258] = Vertex.new(-8, 99, -48, 136, 131, 143),
  [404] = Vertex.new(0, 52, -65, 73, 70, 77),
  [419] = Vertex.new(0, 52, -65, 73, 70, 77),
})
local steelChainbody = Model.new(558, {
  [7] = Vertex.new(-125, 1, -115, 26, 25, 27),
  [179] = Vertex.new(99, 1, 22, 13, 13, 14),
  [228] = Vertex.new(-99, 1, 22, 13, 13, 14),
  [522] = Vertex.new(-125, 1, -115, 114, 109, 120),
  [524] = Vertex.new(151, 1, -57, 87, 84, 91),
})
local steelPlatelegs = Model.new(510, {
  [251] = Vertex.new(39, 58, 34, 71, 68, 74),
  [259] = Vertex.new(-37, 58, 34, 71, 68, 74),
  [323] = Vertex.new(76, -2, 172, 42, 40, 44),
  [489] = Vertex.new(76, -2, 172, 106, 102, 111),
  [501] = Vertex.new(-76, -2, 172, 106, 102, 111),
})
local cookedSalmon = Model.new(312, {
  [7] = Vertex.new(-48, -68, -100, 158, 94, 81),
  [11] = Vertex.new(-48, -68, -100, 158, 94, 81),
  [13] = Vertex.new(-48, -68, -100, 158, 94, 81),
  [16] = Vertex.new(-48, 68, -100, 158, 94, 81),
  [21] = Vertex.new(-48, 68, -100, 158, 94, 81),
})
local rodClayMould = Model.new(504, {
  [142] = Vertex.new(-180, -4, -248, 72, 52, 6),
  [157] = Vertex.new(-180, -4, -248, 72, 52, 6),
  [164] = Vertex.new(-180, -4, -248, 72, 52, 6),
  [165] = Vertex.new(-240, -4, -188, 72, 52, 6),
  [182] = Vertex.new(-240, -4, -188, 72, 52, 6),
})
local silvthrilRod = Model.new(426, {
  [4] = Vertex.new(192, 8, 132, 54, 58, 59),
  [5] = Vertex.new(192, 4, 132, 54, 58, 59),
  [6] = Vertex.new(164, 8, 128, 54, 58, 59),
  [7] = Vertex.new(188, 4, 140, 54, 58, 59),
  [8] = Vertex.new(188, 8, 140, 54, 58, 59),
  [28] = Vertex.new(180, 0, 184, 54, 58, 59),
  [29] = Vertex.new(180, 16, 184, 54, 58, 59),
  [30] = Vertex.new(176, 0, 172, 54, 58, 59),
  [32] = Vertex.new(176, 12, 172, 54, 58, 59),
  [35] = Vertex.new(196, 16, 164, 54, 58, 59),
  [391] = Vertex.new(220, 4, 196, 15, 106, 173),
  [392] = Vertex.new(220, 8, 196, 15, 106, 173),
  [393] = Vertex.new(220, 8, 204, 15, 106, 173),
  [396] = Vertex.new(216, 8, 208, 15, 106, 173),
  [397] = Vertex.new(212, 4, 204, 13, 82, 153),
  [398] = Vertex.new(212, 8, 204, 13, 82, 153),
  [399] = Vertex.new(204, 0, 200, 13, 82, 153),
  [401] = Vertex.new(204, 12, 200, 13, 82, 153),
  [407] = Vertex.new(216, 12, 188, 13, 82, 153),
  [413] = Vertex.new(216, 0, 188, 13, 82, 153),
})
local silvthrilRodE = Model.new(426, {
  [4] = Vertex.new(192, 8, 132, 59, 64, 64),
  [5] = Vertex.new(192, 4, 132, 59, 64, 64),
  [6] = Vertex.new(164, 8, 128, 59, 64, 64),
  [7] = Vertex.new(188, 4, 140, 59, 64, 64),
  [8] = Vertex.new(188, 8, 140, 59, 64, 64),
  [28] = Vertex.new(180, 0, 184, 59, 64, 64),
  [29] = Vertex.new(180, 16, 184, 59, 64, 64),
  [30] = Vertex.new(176, 0, 172, 59, 64, 64),
  [32] = Vertex.new(176, 12, 172, 59, 64, 64),
  [35] = Vertex.new(196, 16, 164, 59, 64, 64),
  [391] = Vertex.new(220, 4, 196, 15, 106, 173),
  [392] = Vertex.new(220, 8, 196, 15, 106, 173),
  [393] = Vertex.new(220, 8, 204, 15, 106, 173),
  [396] = Vertex.new(216, 8, 208, 15, 106, 173),
  [397] = Vertex.new(212, 4, 204, 13, 82, 153),
  [398] = Vertex.new(212, 8, 204, 13, 82, 153),
  [399] = Vertex.new(204, 0, 200, 13, 82, 153),
  [401] = Vertex.new(204, 12, 200, 13, 82, 153),
  [407] = Vertex.new(216, 12, 188, 13, 82, 153),
  [413] = Vertex.new(216, 0, 188, 13, 82, 153),
})
local rodOfIvandis = Model.new(426, {
  [46] = Vertex.new(176, 0, 172, 100, 109, 103),
  [47] = Vertex.new(176, 12, 172, 100, 109, 103),
  [48] = Vertex.new(156, 4, 148, 100, 109, 103),
  [50] = Vertex.new(156, 8, 148, 100, 109, 103),
  [53] = Vertex.new(188, 12, 164, 100, 109, 103),
  [56] = Vertex.new(160, 8, 140, 100, 109, 103),
  [59] = Vertex.new(188, 0, 164, 100, 109, 103),
  [62] = Vertex.new(160, 4, 140, 100, 109, 103),
  [100] = Vertex.new(36, 0, 36, 100, 109, 103),
  [101] = Vertex.new(36, 12, 36, 100, 109, 103),
  [391] = Vertex.new(220, 4, 196, 15, 106, 173),
  [392] = Vertex.new(220, 8, 196, 15, 106, 173),
  [393] = Vertex.new(220, 8, 204, 15, 106, 173),
  [396] = Vertex.new(216, 8, 208, 15, 106, 173),
  [397] = Vertex.new(212, 4, 204, 13, 82, 153),
  [398] = Vertex.new(212, 8, 204, 13, 82, 153),
  [399] = Vertex.new(204, 0, 200, 13, 82, 153),
  [401] = Vertex.new(204, 12, 200, 13, 82, 153),
  [407] = Vertex.new(216, 12, 188, 13, 82, 153),
  [413] = Vertex.new(216, 0, 188, 13, 82, 153),
})
--#endregion
--#region Quest Items
local templeLibraryKey = Model.new(444, {
  [99] = Vertex.new(68, 16, 68, 67, 85, 26),
  [104] = Vertex.new(68, 16, 68, 67, 85, 26),
  [397] = Vertex.new(68, 16, 68, 67, 85, 26),
  [400] = Vertex.new(68, 16, 68, 67, 85, 26),
  [408] = Vertex.new(68, 16, 68, 67, 85, 26),
})
local theSleepingSeven = Model.new(588, {
  [266] = Vertex.new(-60, 12, 84, 33, 46, 3),
  [386] = Vertex.new(64, 36, 84, 88, 75, 7),
  [393] = Vertex.new(64, 36, 84, 88, 75, 7),
  [551] = Vertex.new(-56, 36, -84, 88, 75, 7),
  [555] = Vertex.new(-56, 36, -84, 88, 75, 7),
})
--#endregion

---@type QuestStep[]
local steps = {
  {
    text = "Talk to Veliaf Hurtz in the hideout behind the Canifis pub.<ul><li>This quest requires a lot of inventory space. Steps will tell you when it is best to bank if you don't have a beast of burden summoning familiar.</li></ul>",
    title = "Getting started",
    neededItems = {
      ["One piece of food"] = { quantity = 1 },
      ["Buckets"] = { quantity = 5 },
      ["Regular planks"] = { quantity = 11 },
      ["Any nails (some obtained during quest)"] = { quantity = 50 },
    },
    actions = { Action.Direction:new(3495, 293, 3465) },
    postconditions = {
      Condition.DistanceTo:new(3477, 325, 9845, 8),
      Condition.DistanceTo:new(3505, 1613, 9832, 8), --if already in the cave
    },
  },
  {
    actions = { Action.Direction:new(3480, 925, 9836.5) },
    postconditions = {
      Condition.DistanceTo:new(3480, 397, 9835, 1),
      Condition.DistanceTo:new(3505, 1613, 9832, 8), --if already in the cave
    },
  },
  {
    actions = { Action.Direction:new(3492, 941, 9824) },
    postconditions = { Condition.DistanceTo:new(3505, 1613, 9832, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("I want to join your organisation."),
      Action.ConversationHighlight:new("Can you tell me about the job?"),
    },
    postconditions = { Condition.QuestInterfaceOpen:new() },
  },
  {
    text = "[Accept Quest]",
    postconditions = { Condition.QuestStarted:new() },
  },
  {
    text = "Continue speaking to Veliaf Hurtz.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Ok, I'll do the job."),
    },
    postconditions = { Condition.ConversationText:new("very good luck to you") },
  },
  {
    text = "Exit the cave.",
    title = "Burgh de Rott",
    actions = { Action.Direction:new(3505, 2013, 9831) },
    postconditions = { Condition.DistanceTo:new(3491, 541, 9824, 8) },
  },
  {
    text = "Exit the tunnel through the wooden doors to the south.",
    actions = { Action.Direction:new(3500.5, 1253, 9813.5) },
    postconditions = { Condition.DistanceTo:new(3509, 85, 3449, 8) },
  },
  {
    text = "Climb the tree to the south.",
    actions = { Action.Direction:new(3502, 917, 3431) },
    postconditions = { Condition.DistanceTo:new(3503, 117, 3423, 2) },
  },
  {
    text = "Board the boat to the south.",
    actions = {
      Action.Direction:new(3499, 369, 3378, { distance = 16 }),
      Action.ModelHighlight:new(Models.objects["mort'ton boat"], { distance = 16 }),
    },
    postconditions = { Condition.DistanceTo:new(3522, 101, 3285, 8) },
  },
  {
    text = "Speak to Florin, south of the bridge to Burgh de Rott.",
    actions = {
      Action.Direction:new(3484, 1009, 3241, { distance = 12 }),
      Action.ModelHighlight:new(florin, { distance = 12 }),
    },
    postconditions = { Condition.ConversationText:new("Right!") },
  },
  {
    text = "Search the chest.",
    actions = { Action.Direction:new(3483, 1109, 3245.8) },
    postconditions = { Condition.ConversationText:new("recently has contained wrapped food packages") },
  },
  {
    text = "Use food on the chest.<ul><li>If you need a piece of food, there are snails to the west of Mort'ton that can be cooked with level 12 Cooking.</li></ul>",
    actions = { Action.Direction:new(3483, 1109, 3245.8) },
    postconditions = {
      Condition.ConversationText:new("You gingerly place the food"),
      Condition.ConversationText:new("Blimey"),
    },
  },
  {
    text = "Jump the gate.",
    actions = { Action.Direction:new(3484.5, 1401, 3243.5) },
    postconditions = { Condition.DistanceTo:new(3485, 917, 3241, 2) },
  },
  {
    text = "Talk to Florin.",
    actions = {
      Action.ModelHighlight:new(florin),
      Action.ConversationHighlight:new("Are there any 'out of the way' places in here?"),
    },
    postconditions = { Condition.ConversationText:new("help to fix things up") },
  },
  {
    text = "Climb over the broken down wall in the building just south-east of the well.",
    actions = { Action.Direction:new(3491, 1265, 3230.5) },
    postconditions = { Condition.DistanceTo:new(3491, 965, 3231, 0) },
  },
  {
    text = "Mine the rubble to clear the trap door.",
    actions = { Action.ModelHighlight:new(rubbleCoveringTrapdoor) },
    postconditions = {
      -- Condition.ModelNotVisible:new(rubbleCoveringTrapdoor),
      Condition.ConversationText:new("You break the wall"),
    },
  },
  {
    text = "Open the trapdoor and climb down.",
    actions = {
      Action.ModelHighlight:new(closedTrapdoor),
      Action.ModelHighlight:new(openTrapdoor),
    },
    postconditions = {
      Condition.ConversationText:new("You see that this"),
      Condition.ModelVisible:new(rubble),
    },
  },
  {
    text = "Mine and use spade to remove all the rubble piles (15).<ul><li>Your bucket can be emptied on the pile outside the pub.</li></ul>",
    actions = { Action.ModelHighlight:new(rubble, { highlightPriority = "closest" }) },
    postconditions = {
      Condition.ConversationText:new("As you clear away"),
      Condition.ConversationText:new("last of the rubble"),
      Condition.ConversationText:new("from the inn basement"),
    },
  },
  {
    text = "Exit the pub basement.",
    actions = { Action.Direction:new(-1, 681, 0, { instance = true }) },
    postconditions = { Condition.DistanceTo:new(3491, 965, 3231, 4) },
  },
  {
    text = "Talk to Razvan, south of the pub (any villager will do though).",
    actions = {
      Action.ModelHighlight:new(razvan),
      Action.ConversationHighlight:new("I'd like to help fix up the town."),
    },
    postconditions = { Condition.ConversationText:new("sounds interesting") },
  },
  {
    text = "Talk to Aurel in the general store.<ul><li>Bank after receiving a crate.</li></ul>",
    title = "Fixing the general store",
    neededItems = {
      ["Piece of coal"] = { quantity = 1 },
      ["Steel bars"] = { quantity = 2 },
      ["Bronze hatchets"] = { quantity = 10 },
      ["Tinderboxes"] = { quantity = 3 },
      ["Raw mackerel or raw snails"] = { quantity = 10 },
      ["Swamp paste"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3517, 965, 3241, { distance = 4 }),
      Action.ModelHighlight:new(aurel, { distance = 4 }),
      Action.ConversationHighlight:new("I'd like to help fix up the town."),
    },
    postconditions = { Condition.ConversationText:new("Once you've done that") },
  },
  {
    text = "Climb the ladder outside and <i>use</i> a plank on the Broken Roof.",
    actions = {
      Action.ModelHighlight:new(ladder),
      Action.InventoryHighlight:new(Models.items["plank"]),
      Action.Direction:new(3515, 2497, 3240),
    },
    postconditions = { Condition.ModelVisible:new(patchedRoof) },
  },
  {
    text = "Climb down and <i>use</i> a plank on the damaged wall.",
    actions = { Action.Direction:new(3513, 2097, 3238.2) },
    postconditions = { Condition.DistanceToWithHeight:new(3513, 941, 3237, 8) },
  },
  {
    actions = {
      Action.Direction:new(3517, 1165, 3238.5),
      Action.InventoryHighlight:new(Models.items["plank"]),
    },
    postconditions = { Condition.ModelVisible:new(patchedWall) },
  },
  {
    text = "Talk to Aurel.",
    actions = {
      Action.Direction:new(3517, 965, 3241, { distance = 4 }),
      Action.ModelHighlight:new(aurel, { distance = 4 }),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    postconditions = { Condition.ConversationText:new("There you go") },
  },
  {
    text = "Fill the box with supplies: 10 bronze hatchet, 3 tinderbox and 10 raw mackerel or raw snails. After filling the crate, right-click check its contents.<ul><li>You can kill snails next to Mort'ton.</li><li>You can fish mackerel south of Burgh de Rott.</li><li>Check the in-game quest journal if you forget what Aurel asks for.</li></ul>",
    actions = { Action.InventoryHighlight:new(crate) },
    postconditions = {
      Condition.ChatText:new("You put 10 bronze axes in the crate."),
      Condition.ChatText:new("This crate is packed with stock"),
    },
  },
  {
    actions = { Action.InventoryHighlight:new(crate) },
    postconditions = {
      Condition.ChatText:new("You put 3 tinderboxes in the crate."),
      Condition.ChatText:new("This crate is packed with stock"),
    },
  },
  {
    actions = { Action.InventoryHighlight:new(crate) },
    postconditions = {
      Condition.ChatText:new("You put 10 snails in the crate."),
      Condition.ChatText:new("You put 10 mackerels in the crate."), --not tested
      Condition.ChatText:new("This crate is packed with stock"),
    },
  },
  {
    text = "Return the crate to Aurel.",
    actions = {
      Action.Direction:new(3517, 965, 3241, { distance = 4 }),
      Action.ModelHighlight:new(aurel, { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("fixing up the bank") },
  },
  {
    text = "Talk to Cornelius in the bank.",
    title = "Fixing the bank",
    neededItems = {
      ["Regular planks"] = { quantity = 5 },
      ["Any nails"] = { quantity = 20 },
      ["Swamp paste"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3496, 965, 3212, { distance = 8 }),
      Action.ModelHighlight:new(cornelius, { distance = 8 }),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    postconditions = { Condition.ConversationText:new("get that booth fixed") },
  },
  {
    text = "<i>Use</i> a plank on the bank booth.<ul><li>You can buy some swamp paste in Mort'ton if you have Shades of Mort'ton completed.</li></ul>",
    actions = {
      Action.Direction:new(3495, 965, 3211, { distance = 8 }),
      Action.ModelHighlight:new(brokenBankBooth, { distance = 8 }),
      Action.InventoryHighlight:new(Models.items["plank"]),
    },
    postconditions = { Condition.ModelVisible:new(fixedBankBooth) },
  },
  {
    text = "<i>Use</i> a plank on the wall behind the booth.",
    actions = {
      Action.ModelHighlight:new(bankWallHole),
      Action.InventoryHighlight:new(Models.items["plank"]),
    },
    postconditions = {
      Condition.ModelVisible:new(patchedWall, { atLocation = Location:new(3491, 965, 3211) }), --not properly tested
    },
  },
  {
    text = "Return to Cornelius.",
    actions = {
      Action.Direction:new(3496, 965, 3212, { distance = 8 }),
      Action.ModelHighlight:new(cornelius, { distance = 8 }),
      Action.ConversationHighlight:new("What should I do now?"),
      Action.ConversationHighlight:new("Do you fancy the job?"),
    },
    postconditions = { Condition.ModelVisible:new(bankerCornelius) },
  },
  {
    text = "Talk to Grigore (any villager will do though).",
    title = "Fixing furnace",
    neededItems = {
      ["Piece of coal"] = { quantity = 1 },
      ["Steel bars"] = { quantity = 2 },
    },
    actions = {
      Action.ModelHighlight:new(grigore),
      Action.ConversationHighlight:new("What should I do now?"),
    },
    postconditions = { Condition.ConversationText:new("old furnace were up and working") },
  },
  {
    text = "<i>Use</i> a steel bar on the furnace to the east.",
    actions = {
      Action.InventoryHighlight:new(Models.items["steel bar"]),
      Action.Direction:new(3526, 965, 3210, { distance = 14 }),
      Action.ModelHighlight:new(brokenFurnace, { distance = 8 }),
    },
    postconditions = { Condition.ModelVisible:new(fixedFurnace) },
  },
  {
    text = "<i>Use</i> a piece of coal on the furnace.",
    actions = {
      Action.InventoryHighlight:new(Models.items["steel bar"]),
      Action.Direction:new(3526, 965, 3210, { distance = 14 }),
      Action.ModelHighlight:new(brokenFurnace, { distance = 8 }),
    },
    postconditions = { Condition.ModelVisible:new(fixedLoadedFurnace) },
  },
  {
    text = "Search the furnace to light it.",
    actions = {
      Action.ModelHighlight:new(fixedLoadedFurnace),
      Action.ConversationHighlight:new("Yes"),
    },
    postconditions = { Condition.ModelVisible:new(litFurnace) },
  },
  { postconditions = { Condition.ConversationText:new("Gadderanks is here and he") } },
  {
    text = "Talk to Gadderanks in the general store.",
    title = "Vampyre slayer",
    neededItems = {
      ["Silver weapon (not bolts)"] = { quantity = 1 },
      ["Food if low level"] = { quantity = 1 },
    },
    actions = {
      Action.Direction:new(3514, 965, 3241, { distance = 4 }),
      Action.ModelHighlight:new(gadderanks, { distance = 4 }),
    },
    postconditions = { Condition.ConversationText:new("see about that") },
  },
  {
    text = "Talk to the female Vampyre Juvinate.",
    actions = { Action.ModelHighlight:new(vampyreJuvinateF) },
    postconditions = { Condition.ConversationText:new("Haa...") },
  },
  {
    text = "Talk to the male Vampyre Juvinate.",
    actions = { Action.ModelHighlight:new(vampyreJuvinateM) },
    postconditions = { Condition.ConversationText:new("Didn't you hear me") },
  },
  {
    text = "Talk to Wiskit.",
    actions = { Action.ModelHighlight:new(wiskit) },
    postconditions = { Condition.ConversationText:new("They're draining my blood") },
  },
  {
    text = "Talk to Gadderanks again.",
    actions = { Action.ModelHighlight:new(gadderanks) },
    postconditions = { Condition.ConversationText:new("I've had enough") },
  },
  {
    text = "Kill Gadderanks and the vampyres.<ul><li>The vampyres can only be damaged by a silver melee weapon.</li></ul>",
    postconditions = { Condition.ConversationText:new("Cough...") }, --this fires before all vampyres are dead
  },
  {
    text = "Talk to Gadderanks.",
    actions = { Action.ModelHighlight:new(gadderanks) },
    postconditions = { Condition.InventoryContains:new(gadderhammer) },
  },
  {
    text = "Talk to Veliaf.",
    title = "Migrating the Myreque",
    neededItems = {
      ["Silver weapon (not bolts)"] = { quantity = 1 },
      ["Steel med helm"] = { quantity = 1 },
      ["Steel chainbody"] = { quantity = 1 },
      ["Steel platelegs"] = { quantity = 1 },
      ["Cooked salmon"] = { quantity = 15 },
      ["Extra silver weapon (not recoverable)"] = { quantity = 1 },
    },
    actions = { Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]) },
    postconditions = { Condition.ConversationText:new("Sure") },
  },
  {
    text = "Lodestone to Canifis.",
    postconditions = { Condition.DistanceTo:new(3516, 301, 3513, 10) },
  },
  {
    text = "Talk to Veliaf in the Canifis hideout.<ul><li>Dismiss your followers if you have any.</li></ul>",
    actions = { Action.Direction:new(3495, 293, 3465) },
    postconditions = { Condition.DistanceTo:new(3477, 325, 9845, 8) },
  },
  {
    actions = { Action.Direction:new(3480, 925, 9836.5) },
    postconditions = {
      Condition.DistanceTo:new(3480, 397, 9835, 1),
    },
  },
  {
    actions = { Action.Direction:new(3492, 941, 9824) },
    postconditions = { Condition.DistanceTo:new(3505, 1613, 9832, 8) },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("Ok, thanks."),
    },
    postconditions = { Condition.ConversationText:new("Just explain when you're ready") },
  },
  {
    text = "Talk to Polmafi.",
    actions = { Action.ModelHighlight:new(Models.npcs["polmafi ferdygris"]) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["polmafi ferdygris"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "Talk to Radigad.",
    actions = { Action.ModelHighlight:new(Models.npcs["radigad ponfit"]) },
    postconditions = { Condition.ConversationActive:new() },
  },
  {
    actions = { Action.ModelHighlight:new(Models.npcs["radigad ponfit"]) },
    postconditions = { Condition.ConversationInactive:new() },
  },
  {
    text = "<i>Use</i> all steel armor, food, and a steel sickle on Ivan, then talk to him.<ul><li>Route 1 is generally easier.</li></ul>",
    actions = {
      Action.ModelHighlight:new(Models.npcs["ivan strom"]),
      Action.ConversationHighlight:new("Yes, I'll offer all of this food item in my inventory to Ivan."),
    },
    postconditions = { Condition.InInstance:new() },
  },
  {
    text = "Kill the vampyres.",
    actions = { Action.ModelHighlight:new(vampyreJuvinateM, { highlightPriority = "all" }) },
    postconditions = { Condition.DistanceTo:new(3432, 965, 3484, 8) },
  },
  {
    text = "Enter the Mausoleum.",
    actions = { Action.Direction:new(3424, 1465, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3440, 677, 9888, 8) },
  },
  {
    text = "Talk to Drezel.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Veliaf told me about Ivandis."),
    },
    postconditions = { Condition.ConversationText:new("If he has made such a claim") },
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("Is there somewhere that I might get more information about Ivandis?"),
    },
    postconditions = { Condition.ConversationText:new("that is as much as I") }, --not tested
  },
  {
    actions = {
      Action.ModelHighlight:new(Models.npcs["drezel"]),
      Action.ConversationHighlight:new("The lives of those pitiful few left in Morytania could rest on this!"),
    },
    postconditions = { Condition.InventoryContains:new(templeLibraryKey) },
  },
  {
    text = "Use the key on the east wall.",
    actions = {
      Action.ModelHighlight:new(keyholeWall),
      Action.InventoryHighlight:new(templeLibraryKey),
    },
    postconditions = { Condition.ModelVisible:new(openTempleTrapdoor) },
  },
  {
    text = "Climb down the secret trapdoor.",
    actions = { Action.ModelHighlight:new(openTempleTrapdoor) },
    postconditions = { Condition.DistanceToWithHeight:new(3414, 9832, 9867, 8) },
  },
  {
    text = "Search the westernmost middle-bookcase for <i>The Sleeping Seven</i>.",
    actions = { Action.Direction:new(3407, 10432, 9866) },
    postconditions = { Condition.InventoryContains:new(theSleepingSeven) },
  },
  {
    text = "Read the book.",
    warning = "There's not tracking for this step.",
    actions = { Action.InventoryHighlight:new(theSleepingSeven) },
  },
  {
    text = "Lodestone to Canifis and enter the trapdoor south of the pub.",
    title = "Silvthril rod",
    neededItems = {
      ["Rope"] = { quantity = 1 },
      ["Water rune"] = { quantity = 1 },
      ["Cosmic rune"] = { quantity = 1 },
      ["Soft clay"] = { quantity = 1 },
      ["Cut sapphire"] = { quantity = 1 },
      ["Silver bar (metal bank is fine)"] = { quantity = 1 },
      ["Mithril bar (metal bank is fine)"] = { quantity = 1 },
    },
    actions = { Action.Direction:new(3495, 293, 3465) },
    postconditions = { Condition.DistanceTo:new(3477, 325, 9845, 8) },
  },
  {
    text = "Enter the passage.",
    actions = { Action.Direction:new(3480, 925, 9836.5) },
    postconditions = { Condition.DistanceTo:new(3480, 485, 9834, 2) },
  },
  {
    text = "Smash the wooden boards on the eastern wall then enter.",
    actions = { Action.Direction:new(3484, 517, 9832) },
    postconditions = { Condition.DistanceToWithHeight:new(3461, 2893, 9820, 4) },
  },
  {
    text = "<i>Use</i> soft clay on the tomb, then add the rod clay mould to your tool belt.",
    actions = { Action.ModelHighlight:new(tomb) },
    postconditions = { Condition.InventoryContains:new(rodClayMould) },
  },
  {
    text = "Smelt a silvthril rod at any furnace (under the Silver Casting section).",
    postconditions = { Condition.InventoryContains:new(silvthrilRod) },
  },
  {
    text = "Enchant it using the Lvl-1 Enchant spell.",
    actions = { Action.InventoryHighlight:new(silvthrilRod) },
    postconditions = { Condition.InventoryContains:new(silvthrilRodE) },
  },
  {
    text = "Use the enchanted silvthril rod on the well in the room west of Drezel to receive the rod of ivandis.",
    actions = { Action.Direction:new(3424, 1465, 3485.5) },
    postconditions = { Condition.DistanceTo:new(3440, 677, 9888, 8) },
  },
  {
    actions = { Action.Direction:new(3423, 95, 9890) },
    postconditions = { Condition.InventoryContains:new(rodOfIvandis) },
  },
  {
    text = "Go back to Burgh de Rott, and go to the cellar in the pub where you previously cleared the rubble.",
    title = "Finishing up",
    neededItems = { ["Rod of Ivandis"] = { quantity = 1 } },
    actions = {
      Action.Direction:new(3490, 965, 3232, { distance = 4 }),
      Action.ModelHighlight:new(openTrapdoor, { distance = 4 }),
    },
    postconditions = { Condition.DistanceTo:new(3490, 645, 9631, 4) },
  },
  {
    text = "Talk to Veliaf.",
    actions = {
      Action.ModelHighlight:new(Models.npcs["veliaf hurtz"]),
      Action.ConversationHighlight:new("I have brought you the Rod of Ivandis!"),
      Action.ConversationHighlight:new("Yes, I've come to give the Rod of Ivandis to you!"),
    },
    postconditions = { Condition.QuestComplete:new() },
  },
}

return Quest:new({
  name = "In Aid of the Myreque",
  steps = steps,
  timeline = Enums.timeline.champion,
  members = true,
  length = Enums.length.long,
  releaseDate = 1142985600,
  prereqQuests = { "In Search of the Myreque" },
  questReqs = {
    Types.QuestReq.skill("Crafting", 25),
    Types.QuestReq.skill("Magic", 7),
    Types.QuestReq.skill("Mining", 15),
    Types.QuestReq.ironmanOnlySkill("Mining", 30, true),
    Types.QuestReq.ironmanOnlySkill("Smithing", 30, true),
  },
  neededItems = {
    ["One piece of food"] = { quantity = 1, duringQuest = true },
    ["Buckets"] = { quantity = 5, model = Models.items["bucket"], duringQuest = true },
    ["Regular planks"] = { quantity = 11, model = Models.items["plank"], duringQuest = true },
    ["Any nails (some obtained during quest)"] = {
      quantity = 50,
      model = Models.items["any nails"],
      duringQuest = true,
    },
    ["Silver weapon (not bolts)"] = { quantity = 1, model = Models.items["silver weapon"] },
    ["Swamp paste (not tar)"] = { quantity = 1, model = Models.items["swamp paste"], duringQuest = true },
    ["Raw mackerel or raw snails"] = { quantity = 10, model = mackerelOrSnail, duringQuest = true },
    ["Bronze hatchets"] = { quantity = 10, model = bronzeHatchet },
    ["Tinderboxes"] = { quantity = 3, model = Models.items["tinderbox"], duringQuest = true },
    ["Steel bars"] = { quantity = 2, model = Models.items["steel bar"] },
    ["Piece of coal"] = { quantity = 1, model = Models.items["coal"] },
    ["Soft clay"] = { quantity = 1, model = Models.items["soft clay"] },
    ["Cosmic rune"] = { quantity = 1, model = Models.items["cosmic rune"] },
    ["Water rune"] = { quantity = 1, model = Models.items["water rune"] },
    ["Rope"] = { quantity = 1, model = Models.items["rope"] },
    ["Silver bar (metal bank is fine)"] = { quantity = 1 },
    ["Mithril bar (metal bank is fine)"] = { quantity = 1 },
    ["Steel med helm (not recoverable)"] = { quantity = 1, model = steelMedHelm },
    ["Steel chainbody (not recoverable)"] = { quantity = 1, model = steelChainbody },
    ["Steel platelegs (not recoverable)"] = { quantity = 1, model = steelPlatelegs },
    ["Cooked salmon (not recoverable)"] = { quantity = 15, model = cookedSalmon },
    ["Extra silver weapon (not recoverable)"] = { quantity = 1, model = Models.items["silver weapon"] },
  },
  recommendedItems = {},
  combatNPCs = {
    ["Gadderanks"] = { level = "58", quantity = 1 },
    ["Vampyre Juvinates"] = { level = "58, 60, 63, 68", quantity = 4 },
  },
})
