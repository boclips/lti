package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.JwtTokenFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.domain.model.getUserId
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
class ResourceLinkRequestEndToEndTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can handle a full login to resource link request round-trip`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        val issuer = "https://a-learning-platform.com"
        val resource = "https://tool.com/resource/super-cool"
        val clientId = "test-client-id"
        val userId = "a-sample-users-id"

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
                .param("target_link_uri", resource)
        )
            .andExpect(status().isFound)
            .andReturn()

        val session = loginInitResult.request.session
        val state = extractStateFromLocationHeader(loginInitResult.response)

        val idToken = JwtTokenFactory.sampleResourceLinkRequestJwt(
            issuer = issuer,
            audience = listOf(clientId),
            targetLinkUri = resource,
            signatureAlgorithm = Algorithm.RSA256(
                tokenSigningSetup.keyPair.first,
                tokenSigningSetup.keyPair.second
            ),
            subject = userId
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", idToken)
        )
            .andExpect(status().isSeeOther)
            .andDo { result ->
                val location = result.response.getHeader("Location")

                assertThat(location).isEqualTo(resource)
                assertThat(
                    result.request.session?.getAttribute(
                        com.boclips.lti.core.application.model.SessionKeys.integrationId
                    )
                ).isEqualTo(issuer)
                assertThat(
                    result.request.session?.getUserId()
                ).isEqualTo(userId)
            }
    }

    @Test
    fun `is able to handle interweaving resource requests from the same platform`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        val issuer = "https://a-learning-platform.com"
        val firstResource = "https://tool.com/resource/first"
        val secondResource = "https://tool.com/resource/second"
        val clientId = "test-client-id"

        val tokenSigningSetup = setupTokenSigning(server, uri)

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = issuer,
                clientId = clientId,
                authenticationEndpoint = "https://idp.a-learning-platform.com/auth",
                jwksUrl = tokenSigningSetup.jwksUrl
            )
        )

        val firstResult = mvc.perform(
            post("/v1p3/initiate-login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("iss", issuer)
                .param("login_hint", "a-user-login-hint")
                .param("target_link_uri", firstResource)
        )
            .andExpect(status().isFound)
            .andReturn()

        val session = firstResult.request.session
        val firstState = extractStateFromLocationHeader(firstResult.response)

        val secondResult = mvc.perform(
            post("/v1p3/initiate-login")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("iss", issuer)
                .param("login_hint", "a-user-login-hint")
                .param("target_link_uri", secondResource)
        )
            .andExpect(status().isFound)
            .andReturn()

        val secondState = extractStateFromLocationHeader(secondResult.response)

        val firstResourceToken = JwtTokenFactory.sampleResourceLinkRequestJwt(
            issuer = issuer,
            audience = listOf(clientId),
            targetLinkUri = firstResource,
            signatureAlgorithm = Algorithm.RSA256(
                tokenSigningSetup.keyPair.first,
                tokenSigningSetup.keyPair.second
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", firstState)
                .param("id_token", firstResourceToken)
        )
            .andExpect(status().isSeeOther)
            .andDo { result ->
                val location = result.response.getHeader("Location")

                assertThat(location).isEqualTo(firstResource)
            }

        val secondResourceToken = JwtTokenFactory.sampleResourceLinkRequestJwt(
            issuer = issuer,
            audience = listOf(clientId),
            targetLinkUri = secondResource,
            signatureAlgorithm = Algorithm.RSA256(
                tokenSigningSetup.keyPair.first,
                tokenSigningSetup.keyPair.second
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", secondState)
                .param("id_token", secondResourceToken)
        )
            .andExpect(status().isSeeOther)
            .andDo { result ->
                val location = result.response.getHeader("Location")

                assertThat(location).isEqualTo(secondResource)
            }
    }
}
