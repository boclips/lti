package com.boclips.lti.v1p3.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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

                assertThat(location).startsWith(iss)
                assertThat(locationUri).hasParameter("scope", "openid")
                assertThat(locationUri).hasParameter("response_type", "id_token")
                assertThat(locationUri).hasParameter("client_id", "boclips")
                assertThat(locationUri).hasParameter("redirect_uri", "http://localhost/v1p3/auth")
                assertThat(locationUri).hasParameter("login_hint", loginHint)
                assertThat(locationUri).hasParameter("state", result.request.session?.getAttribute("state") as? String)
                assertThat(locationUri).hasParameter("response_mode", "form_post")
                assertThat(locationUri).hasParameter("nonce", result.request.session?.getAttribute("nonce") as? String)
                assertThat(locationUri).hasParameter("prompt", "none")
            }
    }

    @Test
    fun `returns a bad request response when issuer is not a valid https URL`() {
        TODO("Not yet implemented")
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
