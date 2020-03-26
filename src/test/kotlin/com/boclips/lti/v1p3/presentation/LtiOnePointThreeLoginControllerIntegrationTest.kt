package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.LtiTestSession
import com.boclips.lti.v1p3.domain.model.SessionKeys
import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.UUID
import com.boclips.lti.core.application.model.SessionKeys as CoreSessionKeys

class LtiOnePointThreeLoginControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Nested
    inner class InitiateLogin {
        @Test
        fun `redirects to the Platform authentication endpoint on a valid login initiation request`() {
            val iss = "https://a-learning-platform.com"
            val authenticationEndpoint = "https://idp.a-learning-platform.com/auth"
            val loginHint = "a-user-login-hint"

            mongoPlatformDocumentRepository.insert(
                PlatformDocument(
                    id = ObjectId(),
                    issuer = iss,
                    authenticationEndpoint = authenticationEndpoint
                )
            )

            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iss", iss)
                        .param("login_hint", loginHint)
                        .param("target_link_uri", "https://tool.com/resource/super-cool")
                )
                .andExpect(status().isFound)
                .andDo { result ->
                    val location = result.response.getHeader("Location")
                    val locationUri = URI(location!!)

                    assertThat(location).startsWith(authenticationEndpoint)

                    assertThat(locationUri).hasParameter("scope", "openid")
                    assertThat(locationUri).hasParameter("response_type", "id_token")
                    assertThat(locationUri).hasParameter("client_id", "boclips")
                    assertThat(locationUri).hasParameter(
                        "redirect_uri",
                        "http://localhost/v1p3/authentication-response"
                    )
                    assertThat(locationUri).hasParameter("login_hint", loginHint)
                    assertThat(locationUri).hasParameter(
                        "state",
                        result.request.session?.getAttribute(SessionKeys.state) as? String
                    )
                    assertThat(locationUri).hasParameter("response_mode", "form_post")
                    val nonceParameter = UriComponentsBuilder.fromUri(locationUri).build().queryParams["nonce"]
                    assertThat(nonceParameter).isNotEmpty
                    assertThat(locationUri).hasParameter("prompt", "none")
                }
        }

        @Test
        fun `passes lti_message_hint if it's provided in the request`() {
            val iss = "https://a-learning-platform.com"
            val authenticationEndpoint = "https://idp.a-learning-platform.com/auth"
            val ltiMessageHint = "lti-message-hint"

            mongoPlatformDocumentRepository.insert(
                PlatformDocument(
                    id = ObjectId(),
                    issuer = iss,
                    authenticationEndpoint = authenticationEndpoint
                )
            )

            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iss", iss)
                        .param("login_hint", "a-user-login-hint")
                        .param("lti_message_hint", ltiMessageHint)
                        .param("target_link_uri", "https://tool.com/resource/super-cool")
                )
                .andExpect(status().isFound)
                .andDo { result ->
                    val locationUri = URI(result.response.getHeader("Location")!!)

                    assertThat(locationUri).hasParameter("lti_message_hint", ltiMessageHint)
                }
        }

        @Test
        fun `does not pass lti_message_hint if it's not provided in the request`() {
            val iss = "https://a-learning-platform.com"
            val authenticationEndpoint = "https://idp.a-learning-platform.com/auth"

            mongoPlatformDocumentRepository.insert(
                PlatformDocument(
                    id = ObjectId(),
                    issuer = iss,
                    authenticationEndpoint = authenticationEndpoint
                )
            )

            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iss", iss)
                        .param("login_hint", "a-user-login-hint")
                        .param("target_link_uri", "https://tool.com/resource/super-cool")
                )
                .andExpect(status().isFound)
                .andDo { result ->
                    val locationUri = URI(result.response.getHeader("Location")!!)

                    assertThat(locationUri).hasNoParameter("lti_message_hint")
                }
        }

        @Test
        fun `stores the requested resource on the browser session`() {
            val issuer = "https://a-learning-platform.com"
            val resource = "https://tool.com/resource/super-cool"

            mongoPlatformDocumentRepository.insert(
                PlatformDocument(
                    id = ObjectId(),
                    issuer = issuer,
                    authenticationEndpoint = "https://a-learning-platform.com/auth"
                )
            )

            val session = mvc.perform(
                post("/v1p3/initiate-login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("iss", issuer)
                    .param("login_hint", "a login hint")
                    .param("target_link_uri", resource)
            )
                .andExpect(status().isFound)
                .andReturn().request.session

            assertThat(session?.getAttribute(SessionKeys.targetLinkUri)).isEqualTo(resource)
        }

        @Test
        fun `returns a bad request response when issuer is not a valid https URL`() {
            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iss", "http://insecure.com")
                        .param("login_hint", "a-user-login-hint")
                        .param("target_link_uri", "https://tool.com/resource/super-cool")
                )
                .andExpect(status().isBadRequest)
                .andExpect(content().string(containsString("iss")))
        }

        @Test
        fun `returns a bad request response when issuer is not provided`() {
            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("login_hint", "a-user-login-hint")
                        .param("target_link_uri", "https://tool.com/resource/super-cool")
                )
                .andExpect(status().isBadRequest)
                .andExpect(content().string(containsString("iss")))
        }

        @Test
        fun `returns a bad request response when login_hint is not provided`() {
            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iss", "https://a-learning-platform.com")
                        .param("target_link_uri", "https://tool.com/resource/super-cool")
                )
                .andExpect(status().isBadRequest)
                .andExpect(content().string(containsString("login_hint")))
        }

        @Test
        fun `returns a bad request response when target_link_uri is not provided`() {
            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iss", "https://a-learning-platform.com")
                        .param("login_hint", "a-user-login-hint")
                )
                .andExpect(status().isBadRequest)
                .andExpect(content().string(containsString("target_link_uri")))
        }

        @Test
        fun `returns a bad request when target_link_uri is not a URL`() {
            mvc.perform(
                    post("/v1p3/initiate-login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("iss", "https://a-learning-platform.com")
                        .param("login_hint", "a-user-login-hint")
                        .param("target_link_uri", "definitely not an URI")
                )
                .andExpect(status().isBadRequest)
                .andExpect(content().string(containsString("target_link_uri")))
        }
    }

    @Nested
    inner class HandleAuthenticationResponse {
        @Test
        fun `initiates a user session and redirects to requested resource`() {
            val state = UUID.randomUUID().toString()
            val issuer = "https://platform.com/for-learning"
            val resource = "https://lti.resource/we-expose"

            val idToken = JWT.create().withIssuer(issuer).sign(Algorithm.HMAC256("super-secret"))

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

        @Disabled
        @Test
        fun `returns an unauthorised response when JWT signature verification fails`() {
            TODO("Not yet implemented")
        }

        @Test
        fun `returns an unauthorised response when states do not match`() {
            val idToken = JWT.create()
                .withIssuer("https://platform.com/for-learning")
                .sign(Algorithm.HMAC256("super-secret"))

            val session = LtiTestSession.unauthenticated(
                sessionAttributes = mapOf(
                    SessionKeys.state to "a united state of lti",
                    SessionKeys.targetLinkUri to "https://lti.resource/we-expose"
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
}
