package com.boclips.lti.v1p3.presentation

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.infrastructure.service.Auth0JwksKeyProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.URL

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeepLinkingResponseSigningEndToEndTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can verify issued JWT using keys exposed in JWKS endpoint`() {
        mongoPlatformDocumentRepository.save(PlatformDocumentFactory.sample(issuer = "https://hello-lms.com"))
        val session = LtiTestSessionFactory.authenticated(integrationId = "https://hello-lms.com")

        val responseBody = mvc.perform(
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

        val decodedJwt = extractJwtTokenFromDeepLinkingResponseBody(responseBody)

        val keyProvider = Auth0JwksKeyProvider(
            UrlJwkProvider(URL("http://localhost:$serverPort/.well-known/jwks"), 100, 100),
            retrier
        )
        val algorithm = Algorithm.RSA256(keyProvider)

        assertDoesNotThrow { algorithm.verify(decodedJwt) }
    }

    @LocalServerPort
    private lateinit var serverPort: Integer
}
