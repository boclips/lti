package com.boclips.lti.v1p3.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p3.domain.model.SessionKeys
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class LtiOnePointThreeLoginControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `redirects to the Platform on a valid login request`() {
        val iss = "https://a-learning-platform.com"
        val loginHint = "a-user-login-hint"
        mvc.perform(
                post("/v1p3/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("iss", iss)
                    .param("login_hint", loginHint)
                    .param("target_link_uri", "https://tool.com/resource/super-cool")
            )
            .andExpect(status().isFound)
            .andDo { result ->
                val location = result.response.getHeader("Location")
                val locationUri = URI(location!!)

                // TODO I believe the value of iss parameter doesn't necessarily need to match the Platform authentication endpoint.
                // We probably need to store a mapping on our side and retrieve the auth endpoint based on iss value.
                assertThat(location).startsWith(iss)

                assertThat(locationUri).hasParameter("scope", "openid")
                assertThat(locationUri).hasParameter("response_type", "id_token")
                assertThat(locationUri).hasParameter("client_id", "boclips")
                assertThat(locationUri).hasParameter("redirect_uri", "http://localhost/v1p3/auth")
                assertThat(locationUri).hasParameter("login_hint", loginHint)
                assertThat(locationUri).hasParameter("state", result.request.session?.getAttribute(SessionKeys.state) as? String)
                assertThat(locationUri).hasParameter("response_mode", "form_post")
                val nonceParameter = UriComponentsBuilder.fromUri(locationUri).build().queryParams["nonce"]
                assertThat(nonceParameter).isNotEmpty
                assertThat(locationUri).hasParameter("prompt", "none")
            }
    }

    @Test
    fun `returns a bad request response when issuer is not a valid https URL`() {
        mvc.perform(
            post("/v1p3/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("iss", "http://insecure.com")
                .param("login_hint", "a-user-login-hint")
                .param("target_link_uri", "https://tool.com/resource/super-cool")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString("issuer")))
    }

    @Test
    fun `returns a bad request response when issuer is not provided`() {
        mvc.perform(
                post("/v1p3/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("login_hint", "a-user-login-hint")
                    .param("target_link_uri", "https://tool.com/resource/super-cool")
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString("issuer")))
    }

    @Test
    fun `returns a bad request response when login_hint is not provided`() {
        mvc.perform(
                post("/v1p3/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("iss", "https://a-learning-platform.com")
                    .param("target_link_uri", "https://tool.com/resource/super-cool")
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString("loginHint")))
    }

    @Test
    fun `returns a bad request response when target_link_uri is not provided`() {
        mvc.perform(
                post("/v1p3/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("iss", "https://a-learning-platform.com")
                    .param("login_hint", "a-user-login-hint")
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString("targetLinkUri")))
    }
}
