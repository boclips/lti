package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.application.model.RsaKeyPair

class KeyPairService(private val keyPairs: List<RsaKeyPair>) {
    fun getLatestKeyPair(): RsaKeyPair {
        return keyPairs.maxOrNull() ?: throw IllegalStateException("No key pairs have been configured")
    }

    fun getAllKeyPairs(): List<RsaKeyPair> = keyPairs
}
