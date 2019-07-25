package com.boclips.lti.v1p1.application.service

import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.badRequest
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException

@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
class VideoServiceAccessTokenProviderTest : AbstractSpringIntegrationTest() {
    @Test
    fun `fetches the token and automatically refreshes after it expires`() {
        stubFor(
            post(urlEqualTo("/v1/token"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .withRequestBody(equalTo("grant_type=client_credentials"))
                .withBasicAuth(videoServiceProperties.clientId, videoServiceProperties.clientSecret)
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
              {
                "access_token": "brand-new-access-token",
                "expires_in": 300,
                "token_type": "bearer"
              }
            """.trimIndent()
                        )
                )
        )

        assertThat(videoServiceAccessTokenProvider.getAccessToken()).isEqualTo("brand-new-access-token")
    }

    @Test
    fun `propagates RestTemplate HTTP errors`() {
        stubFor(
            post(urlEqualTo("/v1/token"))
                .withRequestBody(equalTo("grant_type=client_credentials"))
                .withBasicAuth(videoServiceProperties.clientId, videoServiceProperties.clientSecret)
                .willReturn(
                    badRequest()
                )
        )

        assertThatThrownBy { videoServiceAccessTokenProvider.getAccessToken() }.isInstanceOf(HttpClientErrorException::class.java)
    }

    @Autowired
    private lateinit var videoServiceProperties: VideoServiceProperties
    @Autowired
    private lateinit var videoServiceAccessTokenProvider: VideoServiceAccessTokenProvider

    companion object {
        const val API_SERVER_PORT = 8081

        private val apiMockServer = WireMockServer(options().port(API_SERVER_PORT))

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            apiMockServer.start()
            configureFor("localhost", API_SERVER_PORT)
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            apiMockServer.stop()
        }
    }
}
