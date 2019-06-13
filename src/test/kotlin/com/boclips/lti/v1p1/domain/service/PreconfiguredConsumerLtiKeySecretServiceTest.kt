package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PreconfiguredConsumerLtiKeySecretServiceTest {
    private val testKey = "test-key"
    private val testSecret = "test-secret"

    private lateinit var ltiProperties: LtiProperties
    private lateinit var service: PreconfiguredConsumerLtiKeySecretService

    @BeforeEach
    fun beforeEach() {
        ltiProperties = LtiProperties()
        ltiProperties.consumer.key = testKey
        ltiProperties.consumer.secret = testSecret
        service = PreconfiguredConsumerLtiKeySecretService(ltiProperties)
    }

    @Test
    fun `service returns the preconfigured secret when passed the preconfigured key`() {
        assertThat(service.getSecretForKey(testKey)).isEqualTo(testSecret)
    }

    @Test
    fun `service returns null when given a different key`() {
        assertThat(service.getSecretForKey("this is odd...")).isEqualTo(null)
    }
}
