package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.presentation.LtiGatewayTest.Companion.API_SERVER_PORT
import com.boclips.videos.service.client.spring.MockVideoServiceClient
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockVideoServiceClient
@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
@TestPropertySource(
  properties = [
    "boclips.lti.gateway.services.apiUrl=http://localhost:$API_SERVER_PORT"
  ]
)
class LtiGatewayTest {
  @Test
  fun `proxies video player requests to api`() {
    apiMock.register(
      get(urlEqualTo("/v1/videos/test-video-id"))
        .willReturn(
          WireMock.aResponse()
            .withHeader("Content-Type", "text/plain")
            .withBody("Hello, World!")
        )
    )

    val response = restTemplate.getForObject("/api/v1/videos/test-video-id", String::class.java)
    assertThat(response).isEqualTo("Hello, World!")
  }

  @LocalServerPort
  private lateinit var appPort: String

  @Autowired
  private lateinit var restTemplateBuilder: RestTemplateBuilder

  private lateinit var restTemplate: RestTemplate
  private lateinit var ltiGatewayBaseUrl: String

  @BeforeEach
  fun setUp() {
    ltiGatewayBaseUrl = "http://localhost:$appPort"
    restTemplate = restTemplateBuilder.rootUri(ltiGatewayBaseUrl).build()

    apiMockServer.resetAll()
  }

  companion object {
    const val API_SERVER_PORT = 8081

    private val apiMockServer = WireMockServer(WireMockConfiguration.options().port(API_SERVER_PORT))
    private val apiMock = WireMock("localhost", API_SERVER_PORT)

    @BeforeAll
    @JvmStatic
    internal fun beforeAll() {
      apiMockServer.start()
    }

    @AfterAll
    @JvmStatic
    internal fun afterAll() {
      apiMockServer.stop()
    }
  }
}
