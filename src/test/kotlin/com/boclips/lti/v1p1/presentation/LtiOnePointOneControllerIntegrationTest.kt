package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap

class LtiOnePointOneControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `endpoint redirects user to landing page if it receives a minimal correct request`() {
        mvc.perform(
            post("/lti/v1p1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLtiLaunchRequestPayload)
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", ltiProperties.landingPage))
    }

    @Test
    fun `endpoint redirects user to error page if request misses resource_link_id`() {
        mvc.perform(
            post("/lti/v1p1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLtiLaunchRequestPayload.apply { remove("resource_link_id") })
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", ltiProperties.errorPage))
    }

    @Test
    fun `endpoint redirects the user to LTI error page if it receives a blank request`() {
        mvc.perform(
            post("/lti/v1p1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", ltiProperties.errorPage))
    }

    lateinit var validLtiLaunchRequestPayload: LinkedMultiValueMap<String, String>

    @BeforeEach
    fun setUp() {
        validLtiLaunchRequestPayload = prepareLaunchRequest(
            mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to ltiProperties.consumer.key,
                "resource_link_id" to "test-resource-link-id"
            ),
            ltiProperties.consumer.key,
            ltiProperties.consumer.secret
        )
    }

    private fun prepareLaunchRequest(
        parameters: Map<String, String>,
        key: String,
        secret: String
    ): LinkedMultiValueMap<String, String> {

        val signedParameters = ltiOauthSigner.signParameters(
            parameters,
            key,
            secret,
            "http://localhost/lti/v1p1",
            "POST"
        )

        return LinkedMultiValueMap<String, String>(signedParameters.mapValues { listOf(it.value) })
    }
}
