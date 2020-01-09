package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.application.service.VideoServiceAccessTokenProviderTest
import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.lti.v1p1.presentation.service.ToVideoMetadata
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import com.boclips.videos.service.client.internal.FakeClient
import com.boclips.videos.service.client.spring.MockVideoServiceClient
import org.imsglobal.lti.launch.LtiOauthSigner
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
    properties = [
        "video-service.access-token-uri=http://localhost:${VideoServiceAccessTokenProviderTest.API_SERVER_PORT}/v1/token",
        "video-service.client-id=test-client-id",
        "video-service.client-secret=test-client-secret"
    ]
)
abstract class AbstractSpringIntegrationTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    protected lateinit var ltiProperties: LtiProperties

    @Autowired
    protected lateinit var videosClient: VideosClientFake

    @Autowired
    protected lateinit var collectionsClient: CollectionsClientFake

    @Autowired
    protected lateinit var toVideoMetadata: ToVideoMetadata

    @Autowired
    protected lateinit var videoRepository: VideoRepository

    @Autowired
    protected lateinit var collectionRepository: CollectionRepository

    val ltiOauthSigner = LtiOauthSigner()
}
