package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.application.service.VideoServiceAccessTokenProviderTest
import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.lti.v1p1.infrastructure.service.VideosClientFactory
import com.boclips.lti.v1p1.presentation.service.ToVideoMetadata
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.MongodProcess
import mu.KLogging
import org.imsglobal.lti.launch.LtiOauthSigner
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
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
        "video-service.base-url=http://localhost:${VideoServiceAccessTokenProviderTest.API_SERVER_PORT}",
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
    protected lateinit var videosClientFactory: VideosClientFactory

    @Autowired
    protected lateinit var collectionsClient: CollectionsClientFake

    @Autowired
    protected lateinit var toVideoMetadata: ToVideoMetadata

    @Autowired
    protected lateinit var videoRepository: VideoRepository

    @Autowired
    protected lateinit var collectionRepository: CollectionRepository

    @Autowired
    protected lateinit var mongoClient: MongoClient

    val ltiOauthSigner = LtiOauthSigner()

    companion object : KLogging() {
        private var mongoProcess: MongodProcess? = null

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            if (mongoProcess == null) {
                mongoProcess = TestMongoProcess.process
            }
        }
    }

    @BeforeEach
    fun clearDatabases() {
        mongoClient.apply {
            listDatabaseNames()
                .filterNot { setOf("admin", "config").contains(it) }
                .forEach {
                    println("Dropping $it")
                    dropDatabase(it)
                }
        }
    }

    @BeforeEach
    fun clearFakes() {
        (videosClientFactory.getClient("integration-one") as VideosClientFake).clear()
        collectionsClient.clear()
    }
}
