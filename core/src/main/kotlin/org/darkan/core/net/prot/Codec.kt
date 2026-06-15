package org.darkan.core.net.prot

import io.ktor.utils.io.*
import kotlinx.io.Source
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logInfo
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

class Codec {
    val serverProts = mutableMapOf<KClass<out ServerProt>, ServerProtCodec>()
    val clientProtsByOpcode = mutableMapOf<Int, ClientProtCodec<*>>()
    private val opcodeToClassMap = mutableMapOf<Int, KClass<out ClientProt>>()

    /**
     * Decoder-less packets (e.g. the per-second Ping keepalive) are stateless singletons —
     * resolve the reflective instance once per class and reuse it for every packet.
     */
    private val decoderlessInstances = ConcurrentHashMap<KClass<out ClientProt>, ClientProt>()

    /** ServerProt types already reported as lacking an encoder in this codec (log once, not per send). */
    private val reportedUnsupportedServerProts = ConcurrentHashMap.newKeySet<KClass<out ServerProt>>()

    /** Opcode-indexed metadata for ALL server prots (name + size), for proxy/debug use. */
    val serverProtInfo = mutableMapOf<Int, ProtInfo>()
    /** Opcode-indexed metadata for ALL client prots (name + size), for proxy/debug use. */
    val clientProtInfo = mutableMapOf<Int, ProtInfo>()

    data class ProtInfo(val name: String, val size: ProtSize)

    data class ServerProtCodec(
        val opcode: Int,
        val size: ProtSize,
        val encoder: (suspend ServerProt.(ByteWriteChannel) -> Unit)?
    )

    data class ClientProtCodec<T : ClientProt>(
        val size: ProtSize,
        val decoder: (suspend Source.(Int) -> T)?,
        val protClass: KClass<T>
    )

    internal inline fun <reified T : ServerProt> serverProt(
        opcode: Int,
        size: ProtSize = ProtSize.Fixed(0),
        noinline encoder: (suspend T.(ByteWriteChannel) -> Unit)? = null
    ) {
        serverProts[T::class] = ServerProtCodec(
            opcode = opcode,
            size = size,
            encoder = encoder?.let { { output -> (this as T).it(output) } }
        )
        serverProtInfo[opcode] = ProtInfo(T::class.simpleName ?: "UNKNOWN_$opcode", size)
    }

    internal inline fun <reified T : ServerProt> serverProt(
        opcode: Int,
        size: Int,
        noinline encoder: (suspend T.(ByteWriteChannel) -> Unit)? = null
    ) {
        val protSize = ProtSize.Fixed(size)
        serverProts[T::class] = ServerProtCodec(
            opcode = opcode,
            size = protSize,
            encoder = encoder?.let { { output -> (this as T).it(output) } }
        )
        serverProtInfo[opcode] = ProtInfo(T::class.simpleName ?: "UNKNOWN_$opcode", protSize)
    }

    internal inline fun <reified T : ClientProt> clientProt(opcodes: IntArray, size: ProtSize = ProtSize.Fixed(0), noinline decoder: (suspend Source.(Int) -> T)? = null) {
        val codec = ClientProtCodec(size, decoder, T::class)
        val name = T::class.simpleName ?: "UNKNOWN"
        opcodes.forEach { opcode ->
            clientProtsByOpcode[opcode] = codec
            opcodeToClassMap[opcode] = T::class
            clientProtInfo[opcode] = ProtInfo(name, size)
        }
    }

    internal inline fun <reified T : ClientProt> clientProt(opcodes: IntArray, size: Int, noinline decoder: (suspend Source.(Int) -> T)? = null) {
        val protSize = ProtSize.Fixed(size)
        val codec = ClientProtCodec(protSize, decoder, T::class)
        val name = T::class.simpleName ?: "UNKNOWN"
        opcodes.forEach { opcode ->
            clientProtsByOpcode[opcode] = codec
            opcodeToClassMap[opcode] = T::class
            clientProtInfo[opcode] = ProtInfo(name, protSize)
        }
    }

    internal inline fun <reified T : ClientProt> clientProt(opcode: Int, size: ProtSize = ProtSize.Fixed(0), noinline decoder: (suspend Source.() -> T)? = null) {
        clientProt<T>(
            opcodes = intArrayOf(opcode),
            size = size,
            decoder = decoder?.let { { _ -> this.it() } }
        )
    }

    internal inline fun <reified T : ClientProt> clientProt(opcode: Int, size: Int, noinline decoder: (suspend Source.() -> T)? = null) {
        clientProt<T>(
            opcodes = intArrayOf(opcode),
            size = ProtSize.Fixed(size),
            decoder = decoder?.let { { _ -> this.it() } }
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ClientProt> createInstanceForOpcode(opcode: Int): T? {
        val protClass = opcodeToClassMap[opcode] ?: return null

        val cached = decoderlessInstances[protClass]
        if (cached != null) return cached as T

        val instance = try {
            if (protClass.isValue) {
                protClass.primaryConstructor?.let { createValueClassInstance(it) }
            } else {
                protClass.constructors.firstOrNull { it.parameters.isEmpty() }?.call()
            }
        } catch (e: Exception) {
            logError("Failed to instantiate ClientProt ${protClass.simpleName} for opcode $opcode", e)
            return null
        }
        if (instance == null) {
            logError("No usable constructor for ClientProt ${protClass.simpleName} (opcode $opcode)")
            return null
        }
        decoderlessInstances[protClass] = instance as ClientProt
        return instance as T
    }

    /**
     * True if this codec has an encoder entry registered for [type]. When it does not,
     * the capability gap is logged once (INFO) so callers can [gate sends][org.darkan.core.net.Session.sendIfSupported]
     * without producing per-send warn spam.
     */
    fun supportsServerProt(type: KClass<out ServerProt>): Boolean {
        if (serverProts.containsKey(type)) return true
        if (reportedUnsupportedServerProts.add(type)) {
            logInfo("ServerProt ${type.simpleName} has no encoder registered in this codec revision — sends of it will be skipped")
        }
        return false
    }

    private fun <T : Any> createValueClassInstance(constructor: KFunction<T>): T {
        val args = mutableMapOf<kotlin.reflect.KParameter, Any?>()
        for (param in constructor.parameters) {
            when (param.type.classifier) {
                Int::class -> args[param] = 0
                String::class -> args[param] = ""
                Boolean::class -> args[param] = false
                else -> args[param] = null
            }
        }
        return constructor.callBy(args)
    }

    /** Register opcode metadata (name + size) without an encoder. For proxy/debug framing. */
    internal fun serverProtStub(opcode: Int, name: String, size: ProtSize) {
        serverProtInfo.putIfAbsent(opcode, ProtInfo(name, size))
    }

    internal fun serverProtStub(opcode: Int, name: String, size: Int) {
        serverProtInfo.putIfAbsent(opcode, ProtInfo(name, ProtSize.Fixed(size)))
    }

    /** Get the size (as int: fixed=N, varByte=-1, varShort=-2) for a server opcode. */
    fun serverProtSize(opcode: Int): Int = serverProtInfo[opcode]?.size?.toInt() ?: 0

    /** Get the name for a server opcode. */
    fun serverProtName(opcode: Int): String = serverProtInfo[opcode]?.name ?: "UNKNOWN_$opcode"

    /** Get the size (as int) for a client opcode. */
    fun clientProtSize(opcode: Int): Int = clientProtInfo[opcode]?.size?.toInt() ?: 0

    /** Get the name for a client opcode. */
    fun clientProtName(opcode: Int): String = clientProtInfo[opcode]?.name ?: "UNKNOWN_$opcode"

    companion object {
        private val codecs = mutableMapOf<Int, Codec>()

        fun register(revision: Int, init: Codec.() -> Unit): Codec {
            // Only run init for a NEW revision — running it for an already-registered one
            // would leak global side effects (mask-encoder singletons, ActiveMaskKeys)
            // from the discarded Codec instance.
            return codecs.getOrPut(revision) { Codec().apply(init) }
        }

        fun get(revision: Int) = codecs[revision]
    }
}
