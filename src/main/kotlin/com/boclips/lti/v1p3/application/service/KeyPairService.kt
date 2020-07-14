package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.application.model.RsaKeyPair

class KeyPairService(private val keyPairs: List<RsaKeyPair>) {
    fun getCurrentKeyPair(): RsaKeyPair {
        return keyPairs.max() ?: throw IllegalStateException("No key pairs have been configured")
    }
}
