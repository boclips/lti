package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DeepLinkingMessageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

class HandleDeepLinkingMessageIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `returns a deep linking resource selection page URL`() {
        val issuer = "https://lms.com"
        insertPlatform(issuer)

        val message = DeepLinkingMessageFactory.sample(issuer = URL(issuer))

        val returnedUrl = handleDeepLinkingMessage(message)
        val deepLinkingURl = resourceLinkService.getDeepLinkingLinkWithUrlQuery(message)

        assertThat(returnedUrl).isEqualTo(deepLinkingURl)
    }

    @Autowired
    private lateinit var handleDeepLinkingMessage: HandleDeepLinkingMessage
}
