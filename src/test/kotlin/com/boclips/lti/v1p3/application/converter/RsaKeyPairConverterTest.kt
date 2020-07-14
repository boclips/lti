package com.boclips.lti.v1p3.application.converter

import com.boclips.lti.testsupport.factories.RsaKeyPairPropertiesFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RsaKeyPairConverterTest {
    @Test
    fun `converts properties to key pair`() {
        val validProperties = RsaKeyPairPropertiesFactory.sample()

        val keyPair = RsaKeyPairConverter.toSigningKeyPair(validProperties)

        assertThat(keyPair.generationTimestamp).isEqualTo(validProperties.generationTimestamp)
        assertThat(keyPair.privateKey.algorithm).isEqualTo("RSA")
        assertThat(keyPair.privateKey.encoded).isNotEmpty()
        assertThat(keyPair.publicKey.algorithm).isEqualTo("RSA")
        assertThat(keyPair.publicKey.encoded).isNotEmpty()
    }

    @Test
    fun `throws when unable to decode private key`() {
        assertThrows<Exception> {
            RsaKeyPairConverter.toSigningKeyPair(
                RsaKeyPairPropertiesFactory.sample(
                    privateKey = "this is wrong"
                )
            )
        }
    }

    @Test
    fun `throws when unable to decode public key`() {
        assertThrows<Exception> {
            RsaKeyPairConverter.toSigningKeyPair(
                RsaKeyPairPropertiesFactory.sample(
                    publicKey = "this is also wrong"
                )
            )
        }
    }
}
