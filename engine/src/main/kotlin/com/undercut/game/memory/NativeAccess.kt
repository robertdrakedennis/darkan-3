package com.undercut.game.memory

import com.undercut.game.nxt.types.SharedPointer
import java.lang.foreign.*
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType

object NativeAccess {

    lateinit var BASE_ADDR: MemorySegment

    private val symbols = mutableMapOf<String, MemorySegment>()
    private val functions = mutableMapOf<String, MethodHandle>()
    private val lookups = mutableMapOf<String, SymbolLookup>()
    private val linker = Linker.nativeLinker()

    fun init(baseAddr: MemorySegment) {
        BASE_ADDR = baseAddr
        System.loadLibrary("undercutbootstrap")
        addLookup("loaderLookup", SymbolLookup.loaderLookup())
    }

    fun addLookup(name: String, lookup: SymbolLookup) {
        if (lookups[name] == null)
            lookups[name] = lookup
    }

    fun addPathLookup(symbolFile: String) {
        addLookup(symbolFile, SymbolLookup.libraryLookup(symbolFile, Arena.global()))
    }

    fun getSymbol(name: String): MemorySegment {
        return symbols.getOrPut(name) {
            lookups.values.asSequence()
                .mapNotNull { it.find(name).orElse(null) }
                .firstOrNull()
                ?: error("Symbol $name not found")
        }
    }

    fun getFunction(name: String, desc: () -> FunctionDescriptor): MethodHandle {
        if (functions.containsKey(name))
            return functions[name]!!
        val ptr = getSymbol(name)
        val handle = ptr.toFunctionHandle(desc())
        functions[name] = handle
        return handle
    }

    fun getLibraryFunction(library: String, name: String, desc: () -> FunctionDescriptor): MethodHandle {
        if (functions.containsKey(name))
            return functions[name]!!
        addPathLookup(library)
        val ptr = getSymbol(name)
        val handle = ptr.toFunctionHandle(desc())
        functions[name] = handle
        return handle
    }

    fun MemorySegment.toFunctionHandle(desc: FunctionDescriptor): MethodHandle {
        return linker.downcallHandle(this, desc)
    }

    fun MethodHandle.toGlobalArenaPtr(): MemorySegment {
        return Linker.nativeLinker().upcallStub(this, this.type().toDescriptor(), Arena.global())
    }

    fun MethodType.toDescriptor(): FunctionDescriptor {
        val returnLayout = if (this.returnType() == Void.TYPE) null else javaTypeToMemoryLayout(this.returnType())
        val parameterLayouts = this.parameterList().map { javaTypeToMemoryLayout(it) }

        return if (returnLayout == null) {
            FunctionDescriptor.ofVoid(*parameterLayouts.toTypedArray())
        } else {
            FunctionDescriptor.of(returnLayout, *parameterLayouts.toTypedArray())
        }
    }

    private fun javaTypeToMemoryLayout(clazz: Class<*>): ValueLayout {
        return when (clazz) {
            Int::class.java -> JAVA_INT
            Long::class.java -> JAVA_LONG
            Float::class.java -> JAVA_FLOAT
            Double::class.java -> JAVA_DOUBLE
            Short::class.java -> JAVA_SHORT
            Byte::class.java -> JAVA_BYTE
            Char::class.java -> JAVA_CHAR
            Boolean::class.java -> JAVA_BOOLEAN
            else -> ADDRESS // For object references and MemorySegment
        }
    }

    fun MemorySegment.getByte(offset: Long = 0L): Byte {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_BYTE, offset)
    }

    fun MemorySegment.getShort(offset: Long = 0L): Short {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_SHORT, offset)
    }

    fun MemorySegment.getInt(offset: Long = 0L): Int {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_INT, offset)
    }

    fun MemorySegment.getLong(offset: Long = 0L): Long {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_LONG, offset)
    }

    fun MemorySegment.getFloat(offset: Long = 0L): Float {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_FLOAT, offset)
    }

    fun MemorySegment.getDouble(offset: Long = 0L): Double {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_DOUBLE, offset)
    }

    fun MemorySegment.getChar(offset: Long = 0L): Char {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_CHAR, offset)
    }

    fun MemorySegment.getBoolean(offset: Long = 0L): Boolean {
        if (address() == 0L) throw NullPointerException()
        return this.get(JAVA_BOOLEAN, offset)
    }

    fun MemorySegment.readInt(offset: Long = 0L): Int {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_INT).get(JAVA_INT, 0)
    }

    fun MemorySegment.readShort(offset: Long = 0L): Short {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_SHORT).get(JAVA_SHORT, 0)
    }

    fun MemorySegment.readLong(offset: Long = 0L): Long {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_LONG).get(JAVA_LONG, 0)
    }

    fun MemorySegment.readFloat(offset: Long = 0L): Float {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_FLOAT).get(JAVA_FLOAT, 0)
    }

    fun MemorySegment.readDouble(offset: Long = 0L): Double {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_DOUBLE).get(JAVA_DOUBLE, 0)
    }

    fun MemorySegment.readChar(offset: Long = 0L): Char {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_CHAR).get(JAVA_CHAR, 0)
    }

    fun MemorySegment.readBoolean(offset: Long = 0L): Boolean {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_BOOLEAN).get(JAVA_BOOLEAN, 0)
    }

    fun MemorySegment.readByte(offset: Long = 0L): Byte {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, JAVA_BYTE).get(JAVA_BYTE, 0)
    }

    fun MemorySegment.pointerAtOffset(offset: Long = 0L, size: Long): MemorySegment {
        if (address() == 0L) throw NullPointerException()
        return this.asSlice(offset, ADDRESS).reinterpret(size)
    }

    fun MemorySegment.deref(offset: Long = 0L, size: Long): MemorySegment {
        if (address() == 0L) throw NullPointerException()
        return this.get(ADDRESS, offset).reinterpret(size)
    }

    val MemorySegment.getOrNull: MemorySegment?
        get() = if (address() == 0L) null else this

    fun MemorySegment.toShared(): SharedPointer {
        if (address() == 0L) throw NullPointerException()
        return SharedPointer(this.reinterpret(0x10))
    }

    fun Long.toMemorySegment(size: Long): MemorySegment = MemorySegment.ofAddress(this).reinterpret(size)
}