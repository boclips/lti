package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.application.service.VideoServiceAccessTokenProvider
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p1.testsupport.LtiTestSession
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LtiAuthControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `does not permit unauthenticated requests`() {
        mvc.perform(MockMvcRequestBuilders.get("/auth/token"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `does not permit requests with invalid LTI session`() {
        mvc.perform(MockMvcRequestBuilders.get("/auth/token").session(LtiTestSession.getInvalid() as MockHttpSession))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `permits requests with a valid LTI session and returns the token`() {
        mvc.perform(MockMvcRequestBuilders.get("/auth/token").session(LtiTestSession.getValid() as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string("test-auth-token"))
    }

    @BeforeEach
    fun setup() {
        whenever(videoServiceAccessTokenProvider.getAccessToken(LtiTestSession.TEST_CONSUMER_KEY)).thenReturn("test-auth-token")
    }

    @MockBean(name = "videoServiceAccessTokenProvider")
    lateinit var videoServiceAccessTokenProvider: VideoServiceAccessTokenProvider
}
