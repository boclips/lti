package com.boclips.lti.v1p3.application.service

import com.boclips.lti.testsupport.factories.RsaKeyPairFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

class KeyPairServiceTest {
    @Nested
    inner class GetLatestKeyPair {
        @Test
        fun `throws when configured with an empty list`() {
            val service = KeyPairService(emptyList())

            assertThrows<IllegalStateException> { service.getLatestKeyPair() }
        }

        @Test
        fun `returns the key with latest timestamp`() {
            val first = RsaKeyPairFactory.sample(generationTimestamp = 10)
            val second = RsaKeyPairFactory.sample(generationTimestamp = 100)

            val service = KeyPairService(listOf(first, second))

            assertThat(service.getLatestKeyPair()).isEqualTo(second)
        }
    }

    @Nested
    inner class GetAllKeyPairs {
        @Test
        fun `returns all provided key pairs`() {
            val first = RsaKeyPairFactory.sample(generationTimestamp = 10)
            val second = RsaKeyPairFactory.sample(generationTimestamp = 100)
            val third = RsaKeyPairFactory.sample(generationTimestamp = 1000)

            val service = KeyPairService(listOf(first, second, third))

            assertThat(service.getAllKeyPairs()).containsExactlyInAnyOrder(first, second, third)
        }
    }
}
