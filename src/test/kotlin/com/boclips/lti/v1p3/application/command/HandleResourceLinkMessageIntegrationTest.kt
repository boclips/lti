package com.boclips.lti.v1p3.application.command

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.MessageFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.model.getIntegrationId
import com.boclips.lti.v1p3.application.model.setTargetLinkUri
import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import com.boclips.lti.v1p3.domain.exception.ResourceDoesNotMatchException
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
        session.setTargetLinkUri(resource.toString())
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
    fun `sets up a user session`() {
        val issuer = URL("https://lms.com/ok")
        val resource = URL("https://this-is.requested/alright")
        session.setTargetLinkUri(resource.toString())
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer.toString()))

        handleResourceLinkMessage(
            message = MessageFactory.sampleResourceLinkMessage(
                issuer = issuer,
                requestedResource = resource
            ),
            session = session
        )

        assertThat(session.getIntegrationId()).isEqualTo(issuer.toString())
    }

    @Test
    fun `throw an exception if given issuer does not exist on our side`() {
        val resource = URL("https://this-is.requested/alright")
        session.setTargetLinkUri(resource.toString())

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

    @Test
    fun `throws an exception if requested resource does not match what was requested originally`() {
        val issuer = URL("https://lms.com/ok")
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer.toString()))
        session.setTargetLinkUri("https://this-is.requested/orignally")

        assertThrows<ResourceDoesNotMatchException> {
            handleResourceLinkMessage(
                message = MessageFactory.sampleResourceLinkMessage(
                    issuer = issuer,
                    requestedResource = URL("https://this-is.requested/actually")
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
