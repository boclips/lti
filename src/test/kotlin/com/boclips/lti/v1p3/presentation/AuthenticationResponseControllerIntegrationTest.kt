package com.boclips.lti.v1p3.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.NonceDocumentFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.model.SessionKeys
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant.now
import java.util.UUID
import com.boclips.lti.core.application.model.SessionKeys as CoreSessionKeys

class AuthenticationResponseControllerIntegrationTest : AbstractSpringIntegrationTest() {
    private val jwtToken = "a dummy token because JwtService is mocked to reduce test complexity"

    @MockBean
    private lateinit var jwtService: JwtService

    @Test
    fun `initiates a user session and redirects to requested resource`() {
        val issuer = "https://platform.com/for-learning"
        val resource = "https://lti.resource/we-expose"
        val nonce = "super-random-nonce"
        val clientId = "test-client-id"

        whenever(jwtService.isSignatureValid(jwtToken)).thenReturn(true)
        whenever(jwtService.decode(jwtToken)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = issuer,
                audienceClaim = listOf(clientId),
                nonceClaim = nonce,
                targetLinkUriClaim = resource,
                messageTypeClaim = "LtiResourceLinkRequest"
            )
        )

        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer, clientId = clientId))

        val state = UUID.randomUUID().toString()
        val session = LtiTestSessionFactory.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.statesToTargetLinkUris to mapOf(state to resource)
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", jwtToken)
        )
            .andExpect(status().isFound)
            .andDo { result ->
                val location = result.response.getHeader("Location")

                assertThat(location).isEqualTo(resource)
                assertThat(result.request.session?.getAttribute(CoreSessionKeys.integrationId)).isEqualTo(issuer)
            }
    }

    @Test
    fun `returns an unauthorised response when JWT signature verification fails`() {
        whenever(jwtService.isSignatureValid(jwtToken)).thenReturn(false)

        val state = UUID.randomUUID().toString()

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(LtiTestSessionFactory.unauthenticated() as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", jwtToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns an unauthorised response when states do not match`() {
        val session = LtiTestSessionFactory.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.statesToTargetLinkUris to mapOf("a united state of lti" to "https://lti.resource/we-expose")
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", "this is a rebel state")
                .param("id_token", jwtToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns an unauthorised response when target_link_uri in the token does not match what's in the session`() {
        val issuer = "https://platform.com/for-learning"

        whenever(jwtService.isSignatureValid(jwtToken)).thenReturn(true)
        whenever(jwtService.decode(jwtToken)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = issuer,
                targetLinkUriClaim = "https://lti.resource/we-expose"
            )
        )

        val state = "a united state of lti"
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer))

        val session = LtiTestSessionFactory.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.statesToTargetLinkUris to mapOf(state to "https://lti.resource/this-is-a-different-resource")
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", jwtToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns a bad request response when a message type other than LtiResourceLinkRequest is used`() {
        val issuer = "https://platform.com/for-learning"
        val resource = "https://lti.resource/we-expose"
        val clientId = "test-client-id"

        whenever(jwtService.isSignatureValid(jwtToken)).thenReturn(true)
        whenever(jwtService.decode(jwtToken)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = issuer,
                audienceClaim = listOf(clientId),
                targetLinkUriClaim = resource,
                messageTypeClaim = "I can has cheezbureger?"
            )
        )

        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer, clientId = clientId))

        val state = UUID.randomUUID().toString()

        val session = LtiTestSessionFactory.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.statesToTargetLinkUris to mapOf(state to resource)
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", jwtToken)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns unauthorised when the token has invalid security claims`() {
        val issuer = "https://platform.com/for-learning"
        val resource = "https://lti.resource/we-expose"

        whenever(jwtService.isSignatureValid(jwtToken)).thenReturn(true)
        whenever(jwtService.decode(jwtToken)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = issuer,
                targetLinkUriClaim = resource,
                expClaim = now().minusSeconds(120).epochSecond
            )
        )

        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer))

        val state = UUID.randomUUID().toString()

        val session = LtiTestSessionFactory.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.statesToTargetLinkUris to mapOf(state to resource)
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", jwtToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns a bad request response when the token is missing required LTI message claims`() {
        val issuer = "https://platform.com/for-learning"
        val resource = "https://lti.resource/we-expose"
        val clientId = "test client id"

        whenever(jwtService.isSignatureValid(jwtToken)).thenReturn(true)
        whenever(jwtService.decode(jwtToken)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = issuer,
                audienceClaim = listOf(clientId),
                targetLinkUriClaim = resource,
                ltiVersionClaim = null
            )
        )

        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer, clientId = clientId))

        val state = UUID.randomUUID().toString()

        val session = LtiTestSessionFactory.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.statesToTargetLinkUris to mapOf(state to resource)
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", jwtToken)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns unauthorised when nonce has already been used within the allowed time window`() {
        val issuer = "https://platform.com/for-learning"
        val resource = "https://lti.resource/we-expose"

        val nonce = UUID.randomUUID().toString()
        mongoNonceDocumentRepository.insert(NonceDocumentFactory.sample(value = nonce))

        whenever(jwtService.isSignatureValid(jwtToken)).thenReturn(true)
        whenever(jwtService.decode(jwtToken)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = issuer,
                targetLinkUriClaim = resource,
                nonceClaim = nonce
            )
        )

        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer))

        val state = UUID.randomUUID().toString()

        val session = LtiTestSessionFactory.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.statesToTargetLinkUris to mapOf(state to resource)
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", jwtToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns a bad request response when state parameter is missing`() {
        mvc.perform(
            post("/v1p3/authentication-response")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id_token", "a token")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString("state")))
    }

    @Test
    fun `returns a bad request response when id_token parameter is missing`() {
        mvc.perform(
            post("/v1p3/authentication-response")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", "of art")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString("id_token")))
    }
}
