package com.boclips.lti.presentation

import com.boclips.lti.configuration.LtiContext
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.net.URI

class LtiControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `endpoint redirects the user to LTI landing page if it receives a minimal correct request`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val unsignedParameters = mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to LtiContext.CONSUMER_KEY,
                "resource_link_id" to "test-resource-link-id"
        )

        val signedParameters = ltiOauthSigner.signParameters(
                unsignedParameters,
                LtiContext.CONSUMER_KEY,
                LtiContext.CONSUMER_SECRET,
                "$serviceBaseUrl/v1/lti",
                "POST"
        )

        val requestParameters = LinkedMultiValueMap<String, String>(signedParameters.mapValues { listOf(it.value) })
        val entity = HttpEntity<MultiValueMap<String, String>>(requestParameters, headers)

        val responseEntity = restTemplate.exchange("/v1/lti", HttpMethod.POST, entity, Void::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.SEE_OTHER)
        assertThat(responseEntity.headers.location).isEqualTo(URI(LtiContext.LANDING_PAGE))
    }

    @Test
    fun `endpoint redirects the user to LTI error page if it receives a blank request`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val requestParameters = LinkedMultiValueMap<String, String>()
        val entity = HttpEntity<MultiValueMap<String, String>>(requestParameters, headers)

        val responseEntity = restTemplate.exchange("/v1/lti", HttpMethod.POST, entity, Void::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.SEE_OTHER)
        assertThat(responseEntity.headers.location).isEqualTo(URI(LtiContext.ERROR_PAGE))
    }
}
