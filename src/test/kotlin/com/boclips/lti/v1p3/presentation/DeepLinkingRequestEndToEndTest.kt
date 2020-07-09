package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.JwtTokenFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.lanwen.wiremock.ext.WiremockResolver
import ru.lanwen.wiremock.ext.WiremockUriResolver

@ExtendWith(
    value = [
        WiremockResolver::class,
        WiremockUriResolver::class
    ]
)
class DeepLinkingRequestEndToEndTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can handle full deep linking request round-trip`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        val issuer = "https://a-learning-platform.com"
        val clientId = "test-client-id"
        val deepLinkingUrl = resourceLinkService.getDeepLinkingLink().toString()

        val tokenSigningSetup = setupTokenSigning(server, uri)

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = issuer,
                clientId = clientId,
                authenticationEndpoint = "https://idp.a-learning-platform.com/auth",
                jwksUrl = tokenSigningSetup.jwksUrl
            )
        )

        val loginInitResult = mvc.perform(
            post("/v1p3/initiate-login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("iss", issuer)
                .param("login_hint", "a-user-login-hint")
                .param("target_link_uri", deepLinkingUrl)
        )
            .andExpect(status().isFound)
            .andReturn()

        val session = loginInitResult.request.session
        val state = extractStateFromLocationHeader(loginInitResult.response)

        val idToken = JwtTokenFactory.sampleDeepLinkingRequestJwt(
            issuer = issuer,
            audience = listOf(clientId),
            signatureAlgorithm = Algorithm.RSA256(
                tokenSigningSetup.keyPair.first,
                tokenSigningSetup.keyPair.second
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", idToken)
        )
            .andExpect(status().isFound)
            .andDo { result ->
                val location = result.response.getHeader("Location")

                assertThat(location).startsWith(deepLinkingUrl)
                assertThat(
                    result.request.session?.getAttribute(
                        SessionKeys.integrationId
                    )
                ).isEqualTo(issuer)
            }
    }
}
