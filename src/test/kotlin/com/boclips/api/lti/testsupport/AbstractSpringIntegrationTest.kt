package com.boclips.api.lti.testsupport

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractSpringIntegrationTest {

    @LocalServerPort
    lateinit var appPort: String

    @Autowired
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    lateinit var restTemplate: RestTemplate
    lateinit var ltiBaseUrl: String

    @BeforeEach
    fun setUp() {
        ltiBaseUrl = "http://localhost:$appPort"
        restTemplate = restTemplateBuilder.rootUri(ltiBaseUrl).build()
    }

}