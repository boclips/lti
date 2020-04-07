package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.domain.model.SessionKeys
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.lanwen.wiremock.ext.WiremockResolver
import ru.lanwen.wiremock.ext.WiremockUriResolver
import java.time.Instant.now
import java.util.Date
import java.util.UUID

@ExtendWith(
    value = [
        WiremockResolver::class,
        WiremockUriResolver::class
    ]
)
class ResourceLinkRequestEndToEndTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can handle a full login to resource link request round-trip`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        val issuer = "https://a-learning-platform.com"
        val resource = "https://tool.com/resource/super-cool"

        val tokenSigningSetup = setupTokenSigning(server, uri)

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = issuer,
                authenticationEndpoint = "https://idp.a-learning-platform.com/auth",
                jwksUrl = tokenSigningSetup.jwksUrl
            )
        )

        val session = mvc.perform(
            post("/v1p3/initiate-login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("iss", issuer)
                .param("login_hint", "a-user-login-hint")
                .param("target_link_uri", resource)
        )
            .andExpect(status().isFound)
            .andReturn().request.session

        val idToken = JWT.create()
            .withKeyId(tokenSigningSetup.publicKeyId)
            .withIssuer(issuer)
            .withAudience("boclips")
            .withExpiresAt(Date.from(now().plusSeconds(10)))
            .withIssuedAt(Date.from(now().minusSeconds(10)))
            .withClaim("nonce", UUID.randomUUID().toString())
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id", "test-deployment-id")
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri", resource)
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/message_type", "LtiResourceLinkRequest")
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/version", "1.3.0")
            .withClaim(
                "https://purl.imsglobal.org/spec/lti/claim/resource_link",
                mapOf("id" to "test-resource-link-id")
            )
            .sign(
                Algorithm.RSA256(
                    tokenSigningSetup.keyPair.first,
                    tokenSigningSetup.keyPair.second
                )
            )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", session.getAttribute(SessionKeys.state) as String)
                .param("id_token", idToken)
        )
            .andExpect(status().isFound)
            .andDo { result ->
                val location = result.response.getHeader("Location")

                assertThat(location).isEqualTo(resource)
                assertThat(
                    result.request.session?.getAttribute(
                        com.boclips.lti.core.application.model.SessionKeys.integrationId
                    )
                ).isEqualTo(issuer)
            }
    }

    @Disabled
    @Test
    fun `is able to handle interweaving resource requests from the same platform`() {
        TODO("Not yet implemented")
    }
}
