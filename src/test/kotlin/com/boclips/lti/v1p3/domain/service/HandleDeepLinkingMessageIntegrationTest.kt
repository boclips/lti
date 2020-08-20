package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DeepLinkingMessageFactory
import com.boclips.lti.v1p3.domain.model.getIntegrationId
import com.boclips.lti.v1p3.domain.model.getUserId
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

        val message = DeepLinkingMessageFactory.sample(issuer = URL(issuer))

        val returnedUrl = handleDeepLinkingMessage(message, session)
        val deepLinkingURl = resourceLinkService.getDeepLinkingLink(message)

        assertThat(returnedUrl).isEqualTo(deepLinkingURl)
    }

    @Test
    fun `sets up a session with integration id and user id`() {
        val issuer = "https://lms.com"
        val userId = "test-user-id"
        insertPlatform(issuer)
        val message = DeepLinkingMessageFactory.sample(issuer = URL(issuer), subject = userId)

        handleDeepLinkingMessage(message, session)

        assertThat(session.getIntegrationId()).isEqualTo(issuer)
        assertThat(session.getUserId()).isEqualTo(userId)
    }

    private val session = MockHttpSession()

    @Autowired
    private lateinit var handleDeepLinkingMessage: HandleDeepLinkingMessage
}
