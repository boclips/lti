package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.MessageFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.users.api.factories.UserResourceFactory
import com.boclips.users.api.httpclient.test.fakes.UsersClientFake
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

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
        val userClient: UsersClientFake = getUsersClient(issuer)
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
    fun `handles responsive search`() {
        val issuer = URL("https://platform.com/")
        val resource = URL("https://this-is.requested/responsive-search")
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer.toString()))
        val session = LtiTestSessionFactory.authenticated(integrationId = issuer.toString())
        val userClient: UsersClientFake = getUsersClient(issuer)
        userClient.setLoggedInUser(userClient.add(UserResourceFactory.sample()))

        val url = handleResourceLinkMessage(
            message = MessageFactory.sampleResourceLinkMessage(
                issuer = issuer,
                requestedResource = resource
            ),
            session = session
        )

        assertThat(url.path).isEqualTo("/responsive-search")
        assertThat(url).hasParameter("embeddable_video_url", "http://localhost/embeddable-videos/%7Bid%7D")
    }

    @Test
    fun `correctly defaults when no explicit value provided`() {
        val issuer = URL("https://platform.com/")
        val resource = URL("https://this-is.requested/search")
        val userClient: UsersClientFake = getUsersClient(issuer)
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
        val userClient: UsersClientFake = getUsersClient(issuer)
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

    private fun getUsersClient(issuer: URL) = usersClientFactory.getClient(issuer.toString()) as UsersClientFake

    @Autowired
    private lateinit var handleResourceLinkMessage: HandleResourceLinkMessage
}
