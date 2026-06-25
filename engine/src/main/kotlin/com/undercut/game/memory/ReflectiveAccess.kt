package com.undercut.game.memory

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Modifier

object ReflectiveAccess {
    inline fun <reified T> getFunctionHandle(name: String): MethodHandle {
        // Get all declared methods named "doActionHook" from class T
        val methods = T::class.java.declaredMethods.filter { it.name == name }

        // Handle cases where the method is not found or overloaded
        when {
            methods.isEmpty() -> error("Not found in ${T::class.java.name}")
            methods.size > 1 -> error("Multiple methods found in ${T::class.java.name}")
        }

        val method = methods.first()
        val methodType = MethodType.methodType(
            method.returnType,
            method.parameterTypes[0],
            *method.parameterTypes.copyOfRange(1, method.parameterTypes.size)
        )

        return when {
            Modifier.isStatic(method.modifiers) -> MethodHandles.lookup().findStatic(T::class.java, name, methodType)
            else -> MethodHandles.lookup().findVirtual(T::class.java, name, methodType)
        }
    }
}