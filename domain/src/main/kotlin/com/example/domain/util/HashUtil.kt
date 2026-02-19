package com.example.domain.util

import at.favre.lib.crypto.bcrypt.BCrypt

object HashUtil {
    fun hash(password: String): String = BCrypt.withDefaults().hashToString(12, password.toCharArray())
    fun verify(password: String, hash: String): Boolean =
        BCrypt.verifyer().verify(password.toCharArray(), hash).verified
}