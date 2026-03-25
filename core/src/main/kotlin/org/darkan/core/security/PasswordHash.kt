package org.darkan.core.security

import de.mkammerer.argon2.Argon2Factory

/**
 * Argon2 password hashing utilities.
 */
object PasswordHash {
    private val argon2 = Argon2Factory.create()

    fun hash(password: String): String {
        return argon2.hash(10, 65536, 1, password.toCharArray())
    }

    fun verify(password: String, hash: String): Boolean {
        return argon2.verify(hash, password.toCharArray())
    }
}
