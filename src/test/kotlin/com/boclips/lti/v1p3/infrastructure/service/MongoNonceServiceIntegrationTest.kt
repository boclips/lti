package com.boclips.lti.v1p3.infrastructure.service

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.NonceDocumentFactory
import com.boclips.lti.v1p3.infrastructure.repository.MongoNonceDocumentRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant.now
import java.time.temporal.ChronoUnit

class MongoNonceServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class StoreNonce {
        @Test
        fun `stores a nonce`() {
            val nonceValue = "there we go"

            val nonceBeforeStoring = nonceDocumentRepository.findOneByValue(nonceValue)
            assertThat(nonceBeforeStoring).isNull()

            service.storeNonce(nonceValue)

            val nonceAfterStoring = nonceDocumentRepository.findOneByValue(nonceValue)
            assertThat(nonceAfterStoring!!.value).isEqualTo(nonceValue)
        }

        @Test
        fun `sets a createdAt timestamp for the nonce`() {
            val nonceValue = "hello hello hello"

            service.storeNonce(nonceValue)

            val nonce = nonceDocumentRepository.findOneByValue(nonceValue)
            assertThat(nonce!!.createdAt).isCloseTo(now(), within(10, ChronoUnit.SECONDS))
        }
    }

    @Nested
    inner class CheckingIfNonceHasBeenUsed {
        @Test
        fun `returns true if nonce has been used within specified time window`() {
            val nonceValue = "well behaving nonce"

            nonceDocumentRepository.insert(NonceDocumentFactory.sample(value = nonceValue))

            assertThat(service.hasNonceBeenUsedAlready(nonceValue)).isEqualTo(true)
        }

        @Test
        fun `returns false if nonce has been used outside the specified time window`() {
            val nonceValue = "sneaky nonce"

            service = MongoNonceService(nonceDocumentRepository, 1)
            nonceDocumentRepository.insert(
                NonceDocumentFactory.sample(
                    value = nonceValue,
                    createdAt = now().minus(61, ChronoUnit.MINUTES)
                )
            )

            assertThat(service.hasNonceBeenUsedAlready(nonceValue)).isEqualTo(false)
        }

        @Test
        fun `returns false if nonce has not been used at all`() {
            assertThat(service.hasNonceBeenUsedAlready("cheeky nonce")).isEqualTo(false)
        }
    }

    @BeforeEach
    fun initialiseService() {
        service = MongoNonceService(nonceDocumentRepository, 24)
    }

    private lateinit var service: MongoNonceService

    @Autowired
    private lateinit var nonceDocumentRepository: MongoNonceDocumentRepository
}
