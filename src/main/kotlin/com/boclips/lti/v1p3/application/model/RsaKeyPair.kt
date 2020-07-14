package com.boclips.lti.v1p3.application.model

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class RsaKeyPair(
    val generationTimestamp: Long,
    val privateKey: RSAPrivateKey,
    val publicKey: RSAPublicKey
) : Comparable<RsaKeyPair> {
    override fun compareTo(other: RsaKeyPair): Int {
        return generationTimestamp.compareTo(other.generationTimestamp)
    }
}
