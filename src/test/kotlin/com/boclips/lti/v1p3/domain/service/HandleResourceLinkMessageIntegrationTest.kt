package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.MessageFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.domain.model.getIntegrationId
import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import java.net.URL
import javax.servlet.http.HttpSession

class HandleResourceLinkMessageIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `returns a URL to requested resource`() {
        val issuer = URL("https://platform.com/")
        val resource = URL("https://this-is.requested/alright")
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer.toString()))

        val url = handleResourceLinkMessage(
            message = MessageFactory.sampleResourceLinkMessage(
                issuer = issuer,
                requestedResource = resource
            ),
            session = session
        )

        assertThat(url).isEqualTo(resource)
    }

    @Test
    fun `returns extra params for search request`() {
        val issuer = URL("https://platform.com/")
        val resource = URL("https://this-is.requested/search")
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer.toString()))

        val url = handleResourceLinkMessage(
            message = MessageFactory.sampleResourceLinkMessage(
                issuer = issuer,
                requestedResource = resource
            ),
            session = session
        )

        assertThat(url).hasParameter("embeddable_video_url", "http://localhost/embeddable-videos/%7Bid%7D")
        assertThat(url).hasParameter("show_copy_link", "true")
    }

    @Test
    fun `sets up a user session`() {
        val issuer = URL("https://lms.com/ok")
        val resource = URL("https://this-is.requested/alright")
        val userId = "user-id-123"

        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer.toString()))

        handleResourceLinkMessage(
            message = MessageFactory.sampleResourceLinkMessage(
                issuer = issuer,
                requestedResource = resource,
                subject = userId
            ),
            session = session
        )

        assertThat(session.getIntegrationId()).isEqualTo(issuer.toString())
        assertThat(session.getAttribute("userId")).isEqualTo("user-id-123")
    }

    @Test
    fun `throw an exception if given issuer does not exist on our side`() {
        val resource = URL("https://this-is.requested/alright")

        assertThrows<PlatformNotFoundException> {
            handleResourceLinkMessage(
                message = MessageFactory.sampleResourceLinkMessage(
                    issuer = URL("https:/this.does/not/exist"),
                    requestedResource = resource
                ),
                session = session
            )
        }
    }

    @BeforeEach
    fun initialiseHttpSession() {
        session = MockHttpSession()
    }

    private lateinit var session: HttpSession

    @Autowired
    private lateinit var handleResourceLinkMessage: HandleResourceLinkMessage
}
