package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.configuration.properties.ApiProperties
import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import org.apache.http.impl.client.HttpClientBuilder
import org.imsglobal.lti.launch.LtiOauthSigner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate


@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

@AutoConfigureMockMvc
abstract class AbstractSpringIntegrationTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    protected lateinit var ltiProperties: LtiProperties

    @Autowired
    protected lateinit var apiProperties: ApiProperties

    val ltiOauthSigner = LtiOauthSigner()
}
