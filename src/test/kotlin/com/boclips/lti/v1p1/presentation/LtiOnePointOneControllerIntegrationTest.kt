package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.net.URI

class LtiOnePointOneControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `endpoint redirects user to landing page if it receives a minimal correct request`() {
        val entity = prepareLaunchRequest(
            mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to ltiProperties.consumer.key,
                "resource_link_id" to "test-resource-link-id"
            ),
            ltiProperties.consumer.key,
            ltiProperties.consumer.secret
        )

        val responseEntity = restTemplate.exchange("/lti/v1p1", HttpMethod.POST, entity, Void::class.java)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.SEE_OTHER)
        assertThat(responseEntity.headers.location).isEqualTo(URI(ltiProperties.landingPage))
    }

    @Test
    fun `endpoint redirects user to error page if request misses resource_link_id`() {
        val entity = prepareLaunchRequest(
            mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to ltiProperties.consumer.key
            ),
            ltiProperties.consumer.key,
            ltiProperties.consumer.secret
        )

        val responseEntity = restTemplate.exchange("/lti/v1p1", HttpMethod.POST, entity, Void::class.java)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.SEE_OTHER)
        assertThat(responseEntity.headers.location).isEqualTo(URI(ltiProperties.errorPage))
    }

    @Test
    fun `endpoint redirects the user to LTI error page if it receives a blank request`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val entity = HttpEntity<MultiValueMap<String, String>>(LinkedMultiValueMap<String, String>(), headers)

        val responseEntity = restTemplate.exchange("/lti/v1p1", HttpMethod.POST, entity, Void::class.java)

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.SEE_OTHER)
        assertThat(responseEntity.headers.location).isEqualTo(URI(ltiProperties.errorPage))
    }

    private fun prepareLaunchRequest(parameters: Map<String, String>, key: String, secret: String): HttpEntity<MultiValueMap<String, String>> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val signedParameters = ltiOauthSigner.signParameters(
            parameters,
            key,
            secret,
            "$serviceBaseUrl/lti/v1p1",
            "POST"
        )

        val requestParameters = LinkedMultiValueMap<String, String>(signedParameters.mapValues { listOf(it.value) })
        return HttpEntity(requestParameters, headers)
    }
}
