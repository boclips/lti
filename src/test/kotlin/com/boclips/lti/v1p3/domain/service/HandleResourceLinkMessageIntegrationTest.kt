package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.MessageFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.domain.model.getIntegrationId
import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import com.boclips.users.api.factories.OrganisationResourceFactory
import com.boclips.users.api.factories.UserResourceFactory
import com.boclips.users.api.httpclient.test.fakes.UsersClientFake
import com.boclips.users.api.response.user.UserResource
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
        val session = LtiTestSessionFactory.authenticated(integrationId = issuer.toString())

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
        val userClient: UsersClientFake = usersClientFactory.getClient(issuer.toString()) as UsersClientFake
        val user = UserResourceFactory.sample(
            id = "1",
            firstName = "Baptiste",
            features = mapOf(
                "LTI_COPY_RESOURCE_LINK" to true
            )
        )
        userClient.add(user)
        userClient.setLoggedInUser(user)
        val session = LtiTestSessionFactory.authenticated(integrationId = issuer.toString())

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
    fun `correctly defaults when no explicit value provided`() {
        val issuer = URL("https://platform.com/")
        val resource = URL("https://this-is.requested/search")
        val userClient: UsersClientFake = usersClientFactory.getClient(issuer.toString()) as UsersClientFake
        val user = UserResourceFactory.sample(
            id = "1",
            firstName = "Baptiste",
            features = null
        )
        userClient.add(user)
        userClient.setLoggedInUser(user)
        val session = LtiTestSessionFactory.authenticated(integrationId = issuer.toString())
        val url = handleResourceLinkMessage(
            message = MessageFactory.sampleResourceLinkMessage(
                issuer = issuer,
                requestedResource = resource
            ),
            session = session
        )

        assertThat(url).hasParameter("embeddable_video_url", "http://localhost/embeddable-videos/%7Bid%7D")
        assertThat(url).hasParameter("show_copy_link", "false")
    }

    @Test
    fun `when copy link feature is disabled for a user, param value is set appropriately`() {
        val issuer = URL("https://platform.com/")
        val resource = URL("https://this-is.requested/search")
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer.toString()))
        val userClient: UsersClientFake = usersClientFactory.getClient(issuer.toString()) as UsersClientFake
        val user = UserResourceFactory.sample(
            id = "1",
            firstName = "Baptiste",
            features = mapOf(
                "LTI_COPY_RESOURCE_LINK" to false
            )
        )
        userClient.add(user)
        userClient.setLoggedInUser(user)
        val session = LtiTestSessionFactory.authenticated(integrationId = issuer.toString())

        val url = handleResourceLinkMessage(
            message = MessageFactory.sampleResourceLinkMessage(
                issuer = issuer,
                requestedResource = resource
            ),
            session = session
        )

        assertThat(url).hasParameter("embeddable_video_url", "http://localhost/embeddable-videos/%7Bid%7D")
        assertThat(url).hasParameter("show_copy_link", "false")
    }

    @Autowired
    private lateinit var handleResourceLinkMessage: HandleResourceLinkMessage
}
