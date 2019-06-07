package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
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
            .andExpect(header().string("Location", "/lti/v1p1/video/$videoResource"))
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
    fun `if request is invalid do not set a session`() {
        val session = mvc.perform(
            post("/lti/v1p1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andReturn().request.session

        mvc.perform(get("/lti/v1p1/video/$videoResource").session(session as MockHttpSession))
            .andExpect(status().isUnauthorized)
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

    @Test
    fun `valid launch request establishes an LTI session`() {
        val session = mvc.perform(
            post("/lti/v1p1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLtiLaunchRequestPayload)
        ).andReturn().request.session

        mvc.perform(get("/lti/v1p1/video/$videoResource").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(view().name("video"))
            .andExpect(model().attribute("videoUrl", "${apiProperties.url}/v1/videos/$videoResource"))
    }

    @Test
    fun `accessing a video without a session should result in unauthorised response`() {
        mvc.perform(get("/lti/v1p1/video/$videoResource"))
            .andExpect(status().isUnauthorized)
    }

    val videoResource = "test-video-resource"

    lateinit var validLtiLaunchRequestPayload: LinkedMultiValueMap<String, String>

    @BeforeEach
    fun setUp() {
        validLtiLaunchRequestPayload = prepareLaunchRequest(
            mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to ltiProperties.consumer.key,
                "resource_link_id" to videoResource
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
