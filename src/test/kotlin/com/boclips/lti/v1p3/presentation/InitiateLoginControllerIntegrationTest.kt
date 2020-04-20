package com.boclips.lti.v1p3.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.model.getTargetLinkUri
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class InitiateLoginControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `redirects to the Platform authentication endpoint on a valid login initiation request`() {
        val iss = "https://a-learning-platform.com"
        val authenticationEndpoint = "https://idp.a-learning-platform.com/auth"
        val loginHint = "a-user-login-hint"
        val resource = "https://tool.com/resource/super-cool"

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = iss,
                authenticationEndpoint = authenticationEndpoint
            )
        )

        mvc.perform(
            post("/v1p3/initiate-login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("iss", iss)
                .param("login_hint", loginHint)
                .param("target_link_uri", resource)
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

                val stateParameterValue = extractStateFromLocationHeader(result.response)
                val session = result.request.session!!
                assertThat(session.getTargetLinkUri(stateParameterValue)).isEqualTo(resource)
                assertThat(locationUri).hasParameter("response_mode", "form_post")

                val nonceParameter = UriComponentsBuilder.fromUri(locationUri).build().queryParams["nonce"]
                assertThat(nonceParameter).isNotEmpty

                assertThat(locationUri).hasParameter("prompt", "none")
            }
    }

    @Test
    fun `supports initiation by GET`() {
        val iss = "https://a-learning-platform.com"
        val authenticationEndpoint = "https://idp.a-learning-platform.com/auth"
        val loginHint = "a-user-login-hint"
        val resource = "https://tool.com/resource/super-cool"

        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = iss,
                authenticationEndpoint = authenticationEndpoint
            )
        )

        mvc.perform(
            get("/v1p3/initiate-login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("iss", iss)
                .param("login_hint", loginHint)
                .param("target_link_uri", resource)
        )
            .andExpect(status().isFound)
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
            .andExpect(content().string(Matchers.containsString("iss")))
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
            .andExpect(content().string(Matchers.containsString("iss")))
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
            .andExpect(content().string(Matchers.containsString("login_hint")))
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
            .andExpect(content().string(Matchers.containsString("target_link_uri")))
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
            .andExpect(content().string(Matchers.containsString("target_link_uri")))
    }
}
