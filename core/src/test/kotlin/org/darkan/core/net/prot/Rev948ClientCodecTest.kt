package org.darkan.core.net.prot

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Rev948ClientCodecTest {
    private val codec = register948()

    private fun encodeBody(prot: ServerProt): ByteArray = runBlocking {
        val entry = codec.serverProts[prot::class] ?: error("No encoder registered for ${prot::class.simpleName}")
        val encoder = entry.encoder ?: return@runBlocking byteArrayOf()
        val channel = ByteChannel()
        encoder.invoke(prot, channel)
        channel.flush()
        val out = ByteArray(channel.availableForRead)
        channel.readFully(out)
        channel.close(null)
        out
    }

    private fun hexBytes(hex: String): ByteArray =
        hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    @Test
    fun `MAP_BUILD_COMPLETE op107 decodes as concrete zero-payload packet`() {
        val entry = codec.clientProtsByOpcode[107]

        assertEquals(0, codec.clientProtSize(107))
        assertEquals(MapBuildComplete::class, entry?.protClass)
        assertEquals(MapBuildComplete(), codec.createInstanceForOpcode<MapBuildComplete>(107))
    }

    @Test
    fun `post-world macOS sync op203 stays framed as unhandled varshort`() {
        assertEquals(-2, codec.clientProtSize(203))
        assertEquals("UNKNOWN_203", codec.clientProtName(203))
        assertEquals(ProtSize.VarShort, codec.clientProtInfo[203]?.size)
    }

    @Test
    fun `macOS 948-5 live client packets stay framed`() {
        assertEquals(70, codec.clientProtSize(218))
        assertEquals("MacOsLobbyHandoff", codec.clientProtName(218))
        assertEquals(ProtSize.Fixed(70), codec.clientProtInfo[218]?.size)

        assertEquals(7, codec.clientProtSize(240))
        assertEquals("UNKNOWN_240", codec.clientProtName(240))
        assertEquals(ProtSize.Fixed(7), codec.clientProtInfo[240]?.size)
    }

    @Test
    fun `macOS lobby handoff op218 extracts embedded Play Now click`() {
        val entry = codec.clientProtsByOpcode[218] ?: error("missing op218")
        val payload = Buffer().apply {
            write(hexBytes(
                "0000000512ffffffff9f00000037eb020d80083801bb020d8008380184020d80083801" +
                    "cf020d8008380123020d80083801711fc955ee27b8926da151008a037fffff7fd04a81"
            ))
        }

        val decoded = runBlocking { entry.decoder?.invoke(payload, 218) }

        assertEquals(MacOsLobbyHandoff::class, entry.protClass)
        assertEquals(
            MacOsLobbyHandoff(
                IfButton(
                    buttonId = 1,
                    interfaceHash = (906 shl 16) or 81,
                    slotId = 65535,
                    itemId = 65535,
                )
            ),
            decoded,
        )
    }

    @Test
    fun `anti-cheat challenge response op3 decodes mixed-endian payload`() {
        val entry = codec.clientProtsByOpcode[3] ?: error("missing op3")
        val payload = Buffer().apply {
            write(byteArrayOf(
                0x01, 0x02, 0x03, 0x04,
                0x08, 0x07, 0x06, 0x05,
                0x8F.toByte(),
            ))
        }

        val decoded = runBlocking { entry.decoder?.invoke(payload, 3) }

        assertEquals(9, codec.clientProtSize(3))
        assertEquals(AntiCheatChallengeResponse::class, entry.protClass)
        assertEquals(
            AntiCheatChallengeResponse(
                challengeA = 0x01020304,
                challengeB = 0x05060708,
                sequence = 15,
            ),
            decoded,
        )

        val clampedSequencePayload = Buffer().apply {
            write(byteArrayOf(
                0x01, 0x02, 0x03, 0x04,
                0x08, 0x07, 0x06, 0x05,
                0x7F,
            ))
        }

        val decodedClamped = runBlocking { entry.decoder?.invoke(clampedSequencePayload, 3) }

        assertEquals(255, (decodedClamped as AntiCheatChallengeResponse).sequence)
    }

    @Test
    fun `anti-cheat challenge op174 encodes captured big-endian payload`() {
        val entry = codec.serverProts[AntiCheatChallenge::class] ?: error("missing op174")
        val body = encodeBody(AntiCheatChallenge(challengeA = 0x7CFDAE5F, challengeB = 0xBEB5E731.toInt()))

        assertEquals(174, entry.opcode)
        assertEquals(8, codec.serverProtSize(174))
        assertEquals("AntiCheatChallenge", codec.serverProtName(174))
        assertContentEquals(
            byteArrayOf(
                0x7C, 0xFD.toByte(), 0xAE.toByte(), 0x5F,
                0xBE.toByte(), 0xB5.toByte(), 0xE7.toByte(), 0x31,
            ),
            body,
        )
    }

    @Test
    fun `remaining production unknown server packets encode captured payloads`() {
        assertEquals(73, codec.serverProts[MinimapState::class]?.opcode)
        assertEquals(2, codec.serverProtSize(73))
        assertEquals("MinimapState", codec.serverProtName(73))
        assertContentEquals(byteArrayOf(0x80.toByte(), 0x80.toByte()), encodeBody(MinimapState(0, 0)))

        assertEquals(154, codec.serverProts[EntityAnimAtTile::class]?.opcode)
        assertEquals(5, codec.serverProtSize(154))
        assertEquals("EntityAnimAtTile", codec.serverProtName(154))
        assertContentEquals(
            byteArrayOf(0x80.toByte(), 0x10, 0x00, 0x81.toByte(), 0x00),
            encodeBody(EntityAnimAtTile(value = 0, target = 144, cycleOffset = 1)),
        )

        assertEquals(157, codec.serverProts[SceneFlag::class]?.opcode)
        assertEquals(1, codec.serverProtSize(157))
        assertEquals("SceneFlag", codec.serverProtName(157))
        assertContentEquals(byteArrayOf(0x00), encodeBody(SceneFlag(0)))
    }

    @Test
    fun `simple production state packets encode captured payloads`() {
        assertEquals(7, codec.serverProts[ResetEntityLists::class]?.opcode)
        assertEquals(0, codec.serverProtSize(7))
        assertEquals("ResetEntityLists", codec.serverProtName(7))
        assertContentEquals(byteArrayOf(), encodeBody(ResetEntityLists()))

        assertEquals(55, codec.serverProts[DestroyZoneData::class]?.opcode)
        assertEquals(0, codec.serverProtSize(55))
        assertEquals("DestroyZoneData", codec.serverProtName(55))
        assertContentEquals(byteArrayOf(), encodeBody(DestroyZoneData()))

        assertEquals(128, codec.serverProts[NoopVarA::class]?.opcode)
        assertEquals(0, codec.serverProtSize(128))
        assertEquals("NoopVarA", codec.serverProtName(128))
        assertContentEquals(byteArrayOf(), encodeBody(NoopVarA()))

        assertEquals(190, codec.serverProts[ClearPendingUpdates::class]?.opcode)
        assertEquals(0, codec.serverProtSize(190))
        assertEquals("ClearPendingUpdates", codec.serverProtName(190))
        assertContentEquals(byteArrayOf(), encodeBody(ClearPendingUpdates()))

        assertEquals(45, codec.serverProts[SetMultiwayState::class]?.opcode)
        assertEquals(1, codec.serverProtSize(45))
        assertEquals("SetMultiwayState", codec.serverProtName(45))
        assertContentEquals(byteArrayOf(0x00), encodeBody(SetMultiwayState(0)))

        assertEquals(172, codec.serverProts[MinimapFlagA::class]?.opcode)
        assertEquals(1, codec.serverProtSize(172))
        assertEquals("MinimapFlagA", codec.serverProtName(172))
        assertContentEquals(byteArrayOf(0x01), encodeBody(MinimapFlagA(1)))

        assertEquals(204, codec.serverProts[MinimapFlagB::class]?.opcode)
        assertEquals(1, codec.serverProtSize(204))
        assertEquals("MinimapFlagB", codec.serverProtName(204))
        assertContentEquals(byteArrayOf(0xFF.toByte()), encodeBody(MinimapFlagB(1)))
    }

    @Test
    fun `world bootstrap packets encode captured payloads`() {
        val tokenEntry = codec.serverProts[HashedWorldToken::class] ?: error("missing HashedWorldToken")
        val token = "wwGlrZHF5gJWpgnOjhzGiku8LI6RiVLXiJVXPpzBLWDQ"
        assertEquals(54, tokenEntry.opcode)
        assertEquals(ProtSize.VarByte, tokenEntry.size)
        assertContentEquals(
            token.toByteArray(Charsets.ISO_8859_1) + byteArrayOf(0x00),
            encodeBody(HashedWorldToken(token)),
        )

        val midiEntry = codec.serverProts[MidiSong::class] ?: error("missing MidiSong")
        val midiPayload = byteArrayOf(0x7E, 0x8C.toByte(), 0xE3.toByte(), 0x00, 0x00)
        assertEquals(95, midiEntry.opcode)
        assertEquals(ProtSize.Fixed(5), midiEntry.size)
        assertContentEquals(midiPayload, encodeBody(MidiSong(midiPayload)))

        assertContentEquals(
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFD.toByte()) +
                "Follow".toByteArray(Charsets.ISO_8859_1) +
                byteArrayOf(0x00, 0x00),
            encodeBody(SetPlayerOp(2, "Follow", priority = true)),
        )
        assertContentEquals(
            byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFC.toByte()) +
                "Trade with".toByteArray(Charsets.ISO_8859_1) +
                byteArrayOf(0x00, 0x00),
            encodeBody(SetPlayerOp(3, "Trade with", priority = true)),
        )
    }

    @Test
    fun `remaining labelled production server packets encode captured payloads`() {
        assertEquals(1, codec.serverProts[SetNpcOp::class]?.opcode)
        assertEquals(-1, codec.serverProtSize(1))
        assertEquals("SetNpcOp", codec.serverProtName(1))
        assertContentEquals(byteArrayOf(), encodeBody(SetNpcOp()))
        assertContentEquals(
            "Talk-to".toByteArray(Charsets.ISO_8859_1) + byteArrayOf(0x00, 0x12, 0x34),
            encodeBody(SetNpcOp(text = "Talk-to", cursor = 0x1234)),
        )

        assertEquals(12, codec.serverProts[SetPlayerOp2::class]?.opcode)
        assertEquals(2, codec.serverProtSize(12))
        assertEquals("SetPlayerOp2", codec.serverProtName(12))
        assertContentEquals(byteArrayOf(0x00, 0x00), encodeBody(SetPlayerOp2(0)))

        assertEquals(13, codec.serverProts[SetPlayerOp3::class]?.opcode)
        assertEquals(1, codec.serverProtSize(13))
        assertEquals("SetPlayerOp3", codec.serverProtName(13))
        assertContentEquals(byteArrayOf(0x64), encodeBody(SetPlayerOp3(100)))

        assertEquals(104, codec.serverProts[PlayerInfoDecode::class]?.opcode)
        assertEquals(14, codec.serverProtSize(104))
        assertEquals("PlayerInfoDecode", codec.serverProtName(104))
        assertContentEquals(ByteArray(14), encodeBody(PlayerInfoDecode(slot = 0, mode = 0)))
        assertContentEquals(
            byteArrayOf(0xE0.toByte()) + ByteArray(13),
            encodeBody(PlayerInfoDecode(slot = 7, mode = 0)),
        )

        assertEquals(119, codec.serverProts[CutsceneData::class]?.opcode)
        assertEquals(35, codec.serverProtSize(119))
        assertEquals("CutsceneData", codec.serverProtName(119))
        assertContentEquals(
            byteArrayOf(0x00, 0x00, 0x07, 0x02) + ByteArray(31),
            encodeBody(
                CutsceneData(
                    group = 0,
                    slot = 0,
                    mode = 7,
                    extendedMode = 2,
                    shape = 0,
                    flags = 0,
                    id = 0,
                    primaryLong = 0,
                    primaryInt = 0,
                    secondaryInt = 0,
                    secondaryLong = 0,
                    skipLength = 0,
                ),
            ),
        )

        assertEquals(130, codec.serverProts[UpdateIgnoreListRaw::class]?.opcode)
        assertEquals(-1, codec.serverProtSize(130))
        assertEquals("UpdateIgnoreListRaw", codec.serverProtName(130))
        assertContentEquals(ByteArray(10), encodeBody(UpdateIgnoreListRaw()))

        assertEquals(209, codec.serverProts[NpcInfoThunk::class]?.opcode)
        assertEquals(-2, codec.serverProtSize(209))
        assertEquals("NpcInfoThunk", codec.serverProtName(209))
        assertContentEquals(byteArrayOf(), encodeBody(NpcInfoThunk()))
        assertContentEquals(
            byteArrayOf(0x00, 0x01, 0x02),
            encodeBody(
                NpcInfoThunk(
                    payloadKind = NpcInfoThunk.PayloadKind.RawWorldEntityPayload,
                    payload = byteArrayOf(0x00, 0x01, 0x02),
                ),
            ),
        )
    }
}
