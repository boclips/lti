package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import org.apache.http.impl.client.HttpClientBuilder
import org.imsglobal.lti.launch.LtiOauthSigner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate


@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractSpringIntegrationTest {

    @LocalServerPort
    lateinit var appPort: String

    @Autowired
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    @Autowired
    protected lateinit var ltiProperties: LtiProperties

    lateinit var restTemplate: RestTemplate
    lateinit var serviceBaseUrl: String
    lateinit var ltiOauthSigner: LtiOauthSigner

    @BeforeEach
    fun setUp() {
        serviceBaseUrl = "http://localhost:$appPort"
        restTemplate = restTemplateBuilder
            .rootUri(serviceBaseUrl)
            .requestFactory { ltiTestsRequestFactory() }
            .build()
        ltiOauthSigner = LtiOauthSigner()
    }

    private fun ltiTestsRequestFactory(): ClientHttpRequestFactory {
        val nonRedirectingHttpClient = HttpClientBuilder.create()
            .setRedirectStrategy(DoNotFollowRedirectStrategy)
            .build()

        val factory = HttpComponentsClientHttpRequestFactory()
        factory.httpClient = nonRedirectingHttpClient

        return factory
    }
}
