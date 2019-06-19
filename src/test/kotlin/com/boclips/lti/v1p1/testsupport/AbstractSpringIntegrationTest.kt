package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.videos.service.client.spring.MockVideoServiceClient
import org.imsglobal.lti.launch.LtiOauthSigner
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@MockVideoServiceClient
@AutoConfigureMockMvc
abstract class AbstractSpringIntegrationTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    protected lateinit var ltiProperties: LtiProperties

    val ltiOauthSigner = LtiOauthSigner()
}
