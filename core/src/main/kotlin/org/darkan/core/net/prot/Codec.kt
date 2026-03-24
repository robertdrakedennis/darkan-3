package org.darkan.core.net.prot

import io.ktor.utils.io.*
import kotlinx.io.Source
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

class Codec {
    val serverProts = mutableMapOf<KClass<out ServerProt>, ServerProtCodec>()
    val clientProtsByOpcode = mutableMapOf<Int, ClientProtCodec<*>>()
    private val opcodeToClassMap = mutableMapOf<Int, KClass<out ClientProt>>()

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

        return try {
            if (protClass.isValue) {
                val constructor = protClass.primaryConstructor
                return if (constructor != null) createValueClassInstance(constructor) as T else null
            } else {
                val constructor = protClass.constructors.firstOrNull { it.parameters.isEmpty() }
                return constructor?.call() as T
            }
        } catch (e: Exception) {
            return null
        }
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
            val codec = Codec().apply(init)
            return codecs.getOrPut(revision) { codec }
        }

        fun get(revision: Int) = codecs[revision]
    }
}
