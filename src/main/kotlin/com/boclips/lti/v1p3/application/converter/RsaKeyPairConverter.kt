package com.boclips.lti.v1p3.application.converter

import com.boclips.lti.v1p3.application.model.RsaKeyPair
import com.boclips.lti.v1p3.configuration.properties.RsaKeyPairProperties
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

object RsaKeyPairConverter {
    private val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")

    fun toSigningKeyPair(rsaKeyPairProperties: RsaKeyPairProperties): RsaKeyPair {
        return RsaKeyPair(
            generationTimestamp = rsaKeyPairProperties.generationTimestamp,
            privateKey = decodePrivateKey(rsaKeyPairProperties.privateKey),
            publicKey = decodePublicKey(rsaKeyPairProperties.publicKey)
        )
    }

    private fun decodePrivateKey(privateKeyString: String): RSAPrivateKey {
        val privateKeySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(sanitiseKeyContent(privateKeyString)))
        return keyFactory.generatePrivate(privateKeySpec) as RSAPrivateKey
    }

    private fun decodePublicKey(publicKeyString: String): RSAPublicKey {
        val publicKeySpec = X509EncodedKeySpec(Base64.getDecoder().decode(sanitiseKeyContent(publicKeyString)))
        return keyFactory.generatePublic(publicKeySpec) as RSAPublicKey
    }

    private fun sanitiseKeyContent(keyContentString: String) = keyContentString
        .replace(Regex("-----BEGIN.*?KEY-----"), "")
        .replace(Regex("-----END.*?KEY-----"), "")
        .replace("\n", "")
        .replace(" ", "")
}
