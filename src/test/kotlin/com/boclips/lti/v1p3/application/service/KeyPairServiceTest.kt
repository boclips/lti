package com.boclips.lti.v1p3.application.service

import com.boclips.lti.testsupport.factories.RsaKeyPairFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

class KeyPairServiceTest {
    @Test
    fun `throws when configured with an empty list`() {
        val service = KeyPairService(emptyList())

        assertThrows<IllegalStateException> { service.getCurrentKeyPair() }
    }

    @Test
    fun `returns the key with latest timestamp`() {
        val first = RsaKeyPairFactory.sample(generationTimestamp = 10)
        val second = RsaKeyPairFactory.sample(generationTimestamp = 100)

        val service = KeyPairService(listOf(first, second))

        assertThat(service.getCurrentKeyPair()).isEqualTo(second)
    }
}
