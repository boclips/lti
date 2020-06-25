package com.boclips.lti.v1p3.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DeepLinkingResponseControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `returns an unauthorised response when user doesn't have an LTI session in place`() {
        mvc.perform(
            post("/v1p3/deep-linking-response")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "selectedItems": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns a bad request when expected payload is not sent`() {
        val session = LtiTestSessionFactory.authenticated(integrationId = "hello")

        mvc.perform(
            post("/v1p3/deep-linking-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                    
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `returns ok response with the JWT when payload with no items is sent`() {
        val session = LtiTestSessionFactory.authenticated(integrationId = "hello")

        mvc.perform(
            post("/v1p3/deep-linking-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "selectedItems": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.jwt", not(emptyString())))
    }
}
