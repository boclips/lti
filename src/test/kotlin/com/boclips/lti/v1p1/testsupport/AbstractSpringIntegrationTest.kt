package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.lti.v1p1.presentation.service.ToVideoMetadata
import com.boclips.videos.service.client.internal.FakeClient
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

    @Autowired
    protected lateinit var videoServiceClient: FakeClient

    @Autowired
    protected lateinit var toVideoMetadata: ToVideoMetadata

    @Autowired
    protected lateinit var videoRepository: VideoRepository

    @Autowired
    protected lateinit var collectionRepository: CollectionRepository

    val ltiOauthSigner = LtiOauthSigner()
}
