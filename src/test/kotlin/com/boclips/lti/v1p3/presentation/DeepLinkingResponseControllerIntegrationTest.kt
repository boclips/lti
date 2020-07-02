package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.JWT
import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
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
    fun `returns unauthorised when the session's integration id is bad`() {
        val session = LtiTestSessionFactory.authenticated(integrationId = "hello")
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
    fun `returns unauthorised when platform is invalid`() {
        integrationDocumentRepository.save(
            IntegrationDocument(
                id = ObjectId.get(),
                integrationId = "hello",
                clientSecret = "123",
                clientId = "missing"
            )
        )

        val session = LtiTestSessionFactory.authenticated(integrationId = "hello")
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
    fun `returns ok response with the JWT when payload with no items is sent`() {
        integrationDocumentRepository.save(
            IntegrationDocument(
                id = ObjectId.get(),
                integrationId = "hello",
                clientSecret = "123",
                clientId = "clientId"
            )
        )

        mongoPlatformDocumentRepository.save(PlatformDocumentFactory.sample(clientId = "clientId"))

        val session = LtiTestSessionFactory.authenticated(integrationId = "hello")

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

        val jwtToken = JsonPath.parse(response).read<String>("$.jwt")
        val decodedJwt = JWT.decode(jwtToken)

        assertThat(decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/data").asString()).isEqualTo("i-am-data")
        assertThat(decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id").asString()).isEqualTo("123")
        assertThat(decodedJwt.getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/content_items").asArray(String::class.java)).isEmpty()
    }
}
