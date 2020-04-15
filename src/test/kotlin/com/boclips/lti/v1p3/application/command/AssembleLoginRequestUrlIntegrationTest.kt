package com.boclips.lti.v1p3.application.command

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.model.getTargetLinkUri
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.web.util.UriComponentsBuilder

class AssembleLoginRequestUrlIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `sets required login parameters`() {
        val iss = "https://a-learning-platform.com"
        val authenticationEndpoint = "https://idp.a-learning-platform.com/auth"
        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = iss,
                authenticationEndpoint = authenticationEndpoint
            )
        )

        val loginHint = "a-user-login-hint"
        val ltiMessageHint = "lti-message-hint"
        val session = MockHttpSession()
        val resource = "https://tool.com/resource/super-cool"
        val url = assembleLoginRequestUrl(
            issuer = iss,
            loginHint = loginHint,
            targetLinkUri = resource,
            ltiMessageHint = ltiMessageHint,
            session = session
        )

        assertThat(url.toString()).startsWith(authenticationEndpoint)

        assertThat(url).hasParameter("scope", "openid")
        assertThat(url).hasParameter("response_type", "id_token")
        assertThat(url).hasParameter("client_id", "boclips")
        assertThat(url).hasParameter(
            "redirect_uri",
            "http://localhost/v1p3/authentication-response"
        )
        assertThat(url).hasParameter("login_hint", loginHint)

        val stateParameter = extractStateFromUrl(url)
        assertThat(stateParameter).isNotBlank()
        assertThat(url).hasParameter("response_mode", "form_post")

        val nonceParameter = UriComponentsBuilder.fromUri(url.toURI()).build().queryParams["nonce"]!!.first()
        assertThat(nonceParameter).isNotBlank()

        assertThat(url).hasParameter("prompt", "none")
    }

    @Test
    fun `passes lti_message_hint if it's provided in the request`() {
        val iss = "https://a-learning-platform.com"
        val ltiMessageHint = "lti-message-hint"

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = iss,
                authenticationEndpoint = "https://idp.a-learning-platform.com/auth"
            )
        )

        val url = assembleLoginRequestUrl(
            issuer = iss,
            loginHint = "a-user-login-hint",
            targetLinkUri = "https://tool.com/resource/super-cool",
            ltiMessageHint = ltiMessageHint,
            session = MockHttpSession()
        )


        assertThat(url).hasParameter("lti_message_hint", ltiMessageHint)
    }

    @Test
    fun `does not pass lti_message_hint if it's not provided in the request`() {
        val iss = "https://a-learning-platform.com"

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = iss,
                authenticationEndpoint = "https://idp.a-learning-platform.com/auth"
            )
        )

        val url = assembleLoginRequestUrl(
            issuer = iss,
            loginHint = "a-user-login-hint",
            targetLinkUri = "https://tool.com/resource/super-cool",
            session = MockHttpSession()
        )

        assertThat(url).hasNoParameter("lti_message_hint")
    }

    @Test
    fun `maps generated state to requested resource on the browser session`() {
        val issuer = "https://a-learning-platform.com"
        val resource = "https://tool.com/resource/super-cool"

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = issuer,
                authenticationEndpoint = "https://a-learning-platform.com/auth"
            )
        )

        val session = MockHttpSession()
        val url = assembleLoginRequestUrl(
            issuer = issuer,
            loginHint = "a-user-login-hint",
            targetLinkUri = "https://tool.com/resource/super-cool",
            session = session
        )

        val state = extractStateFromUrl(url)

        assertThat(session.getTargetLinkUri(state)).isEqualTo(resource)
    }

    @Autowired
    private lateinit var assembleLoginRequestUrl: AssembleLoginRequestUrl
}
