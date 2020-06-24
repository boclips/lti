package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import com.boclips.lti.v1p3.domain.model.getIntegrationId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import java.net.URL

class HandleDeepLinkingMessageIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `returns a deep linking resource selection page URL`() {
        val issuer = "https://lms.com"
        insertPlatform(issuer)

        val message = DeepLinkingMessage(issuer = URL(issuer))

        val returnedUrl = handleDeepLinkingMessage(message, session)
        val deepLinkingURl = resourceLinkService.getDeepLinkingLink()

        assertThat(returnedUrl).isEqualTo(deepLinkingURl)
    }

    @Test
    fun `sets up a session`() {
        val issuer = "https://lms.com"
        insertPlatform(issuer)
        val message = DeepLinkingMessage(issuer = URL(issuer))

        handleDeepLinkingMessage(message, session)

        assertThat(session.getIntegrationId()).isEqualTo(issuer)
    }

    private val session = MockHttpSession()

    @Autowired
    private lateinit var handleDeepLinkingMessage: HandleDeepLinkingMessage

    @Autowired
    private lateinit var resourceLinkService: ResourceLinkService
}
