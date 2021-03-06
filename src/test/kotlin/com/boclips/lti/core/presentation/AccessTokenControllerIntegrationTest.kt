package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.service.ApiAccessTokenProvider
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AccessTokenControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `does not permit unauthenticated requests`() {
        mvc.perform(get("/auth/token"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `does not permit requests with invalid LTI session`() {
        mvc.perform(get("/auth/token").session(LtiTestSessionFactory.unauthenticated() as MockHttpSession))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `permits requests with a valid LTI session and returns the token`() {
        mvc.perform(get("/auth/token").session(LtiTestSessionFactory.authenticated(integrationId = integrationId) as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string("test-auth-token"))
    }

    @BeforeEach
    fun setup() {
        whenever(apiAccessTokenProvider.getAccessToken(integrationId)).thenReturn("test-auth-token")
    }

    val integrationId = "test-lti-consumer"

    @MockBean(name = "apiAccessTokenProvider")
    lateinit var apiAccessTokenProvider: ApiAccessTokenProvider
}
