package com.boclips.lti.v1p3.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
                        "data": "i-am-data",
                        "deploymentId": "123",
                        "selectedItems": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns a bad request when expected payload is not sent`() {
        val session = LtiTestSessionFactory.authenticated(integrationId = "https://hello-lms.com")

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
    fun `returns unauthorised when the session's integration id is bad`() {
        val session = LtiTestSessionFactory.authenticated(integrationId = "blah")
        mvc.perform(
            post("/v1p3/deep-linking-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "data": "i-am-data",
                        "deploymentId": "123",
                        "selectedItems": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns unauthorised when platform is not registered`() {
        val session = LtiTestSessionFactory.authenticated(integrationId = "https://hello-lms.com")
        mvc.perform(
            post("/v1p3/deep-linking-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "data": "i-am-data",
                        "deploymentId": "123",
                        "selectedItems": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `returns OK response with the JWT when payload with no items is sent`() {
        mongoPlatformDocumentRepository.save(PlatformDocumentFactory.sample(issuer = "https://hello-lms.com"))

        val session = LtiTestSessionFactory.authenticated(integrationId = "https://hello-lms.com")

        val response = mvc.perform(
            post("/v1p3/deep-linking-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "data": "i-am-data",
                        "deploymentId": "123",
                        "selectedItems": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val decodedJwt = extractJwtTokenFromDeepLinkingResponseBody(response)

        assertThat(
            decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/data").asString()
        ).isEqualTo("i-am-data")
        assertThat(decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id").asString()).isEqualTo(
            "123"
        )
        assertThat(
            decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/content_items")
                .asArray(String::class.java)
        ).isEmpty()

        assertThat(decodedJwt.keyId).isEqualTo(keyPairService.getLatestKeyPair().generationTimestamp.toString())
    }

    @Test
    fun `returns OK response with the JWT when items are selected`() {
        val issuer = "https://hello-lms.com"
        mongoPlatformDocumentRepository.save(PlatformDocumentFactory.sample(issuer = issuer))
        saveVideo(VideoResourcesFactory.sampleVideo(videoId = "abc123"), issuer)
        saveVideo(VideoResourcesFactory.sampleVideo(videoId = "def456"), issuer)

        val session = LtiTestSessionFactory.authenticated(integrationId = issuer)

        val response = mvc.perform(
            post("/v1p3/deep-linking-response")
                .session(session as MockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "data": "i-am-data",
                        "deploymentId": "deployment-123",
                        "selectedItems": [
                            { "id": "abc123" },
                            { "id": "def456" }
                        ]
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        val decodedJwt = extractJwtTokenFromDeepLinkingResponseBody(response)

        val returnedData = decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/data").asString()
        val returnedDeploymentId = decodedJwt
            .getClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id").asString()
        val returnedVideos = decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/content_items")
            .asArray(HashMap::class.java)

        assertThat(returnedData).isEqualTo("i-am-data")
        assertThat(returnedDeploymentId).isEqualTo("deployment-123")
        assertThat(returnedVideos.map { it["url"].toString() }).containsExactlyInAnyOrder(
            "http://localhost/videos/abc123",
            "http://localhost/videos/def456"
        )
        assertThat(returnedVideos.map { it["type"].toString() }).containsExactlyInAnyOrder(
            "ltiResourceLink",
            "ltiResourceLink"
        )
    }
}
