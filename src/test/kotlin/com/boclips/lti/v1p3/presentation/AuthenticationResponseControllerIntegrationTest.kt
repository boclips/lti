package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.LtiTestSession
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.domain.model.SessionKeys
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.lanwen.wiremock.ext.WiremockResolver
import ru.lanwen.wiremock.ext.WiremockUriResolver
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.UUID
import com.boclips.lti.core.application.model.SessionKeys as CoreSessionKeys

@ExtendWith(
    value = [
        WiremockResolver::class,
        WiremockUriResolver::class
    ]
)
class AuthenticationResponseControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `initiates a user session and redirects to requested resource`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        val tokenSigningSetup = setupTokenSigning(server, uri)

        val issuer = "https://platform.com/for-learning"
        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = issuer,
                jwksUrl = tokenSigningSetup.jwksUrl
            )
        )

        val state = UUID.randomUUID().toString()
        val resource = "https://lti.resource/we-expose"

        val idToken = JWT.create()
            .withKeyId(tokenSigningSetup.publicKeyId)
            .withIssuer(issuer)
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri", resource)
            .sign(Algorithm.RSA256(tokenSigningSetup.keyPair.first, tokenSigningSetup.keyPair.second))

        val session = LtiTestSession.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.state to state,
                SessionKeys.targetLinkUri to resource
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

                assertThat(location).isEqualTo(resource)
                assertThat(result.request.session?.getAttribute(CoreSessionKeys.integrationId)).isEqualTo(issuer)
            }
    }

    @Test
    fun `returns an unauthorised response when JWT signature verification fails`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        val tokenSigningSetup = setupTokenSigning(server, uri)

        val issuer = "https://platform.com/for-learning"
        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = issuer,
                jwksUrl = tokenSigningSetup.jwksUrl
            )
        )

        val state = UUID.randomUUID().toString()
        val resource = "https://lti.resource/we-expose"

        val otherKeyPair = KeyPairGenerator.getInstance("RSA").genKeyPair()
        val idToken = JWT.create()
            .withKeyId(tokenSigningSetup.publicKeyId)
            .withIssuer(issuer)
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri", resource)
            .sign(Algorithm.RSA256(otherKeyPair.public as RSAPublicKey, otherKeyPair.private as RSAPrivateKey))

        val session = LtiTestSession.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.state to state,
                SessionKeys.targetLinkUri to resource
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", idToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns an unauthorised response when states do not match`() {
        val resource = "https://lti.resource/we-expose"

        val idToken = JWT.create()
            .withIssuer("https://platform.com/for-learning")
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri", resource)
            .sign(Algorithm.HMAC256("super-secret"))

        val session = LtiTestSession.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.state to "a united state of lti",
                SessionKeys.targetLinkUri to resource
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", "this is a rebel state")
                .param("id_token", idToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns an unauthorised response when target_link_uri in the token does not match what's in the session`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        val tokenSigningSetup = setupTokenSigning(server, uri)

        val state = "a united state of lti"
        val issuer = "https://platform.com/for-learning"
        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = issuer,
                jwksUrl = tokenSigningSetup.jwksUrl
            )
        )

        val idToken = JWT.create()
            .withKeyId(tokenSigningSetup.publicKeyId)
            .withIssuer(issuer)
            .withClaim(
                "https://purl.imsglobal.org/spec/lti/claim/target_link_uri",
                "https://lti.resource/we-expose"
            )
            .sign(Algorithm.RSA256(tokenSigningSetup.keyPair.first, tokenSigningSetup.keyPair.second))

        val session = LtiTestSession.unauthenticated(
            sessionAttributes = mapOf(
                SessionKeys.state to state,
                SessionKeys.targetLinkUri to "https://lti.resource/this-is-a-different-resource"
            )
        )

        mvc.perform(
            post("/v1p3/authentication-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("state", state)
                .param("id_token", idToken)
        )
            .andExpect(status().isUnauthorized)
    }

    @Disabled
    @Test
    fun `returns a bad request when nonce has already been used within the allowed time window`() {
        TODO("Not yet implemented")
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
