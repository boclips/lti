package com.boclips.lti.testsupport

import com.boclips.lti.core.application.service.ApiAccessTokenProviderTest
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.core.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.core.infrastructure.service.VideosClientFactory
import com.boclips.lti.core.presentation.service.ToVideoViewModel
import com.boclips.lti.testsupport.configuration.FakeClientsConfig
import com.boclips.lti.v1p1.infrastructure.repository.LtiOnePointOneConsumerRepository
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
@TestPropertySource(properties = [
    "boclips.api.baseUrl=http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}/v1",
    "boclips.api.tokenUrl=http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}/v1/token"
])
abstract class AbstractSpringIntegrationTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    protected lateinit var videosClientFactory: VideosClientFactory

    @Autowired
    protected lateinit var collectionsClientFactory: CollectionsClientFactory

    @Autowired
    protected lateinit var toVideoViewModel: ToVideoViewModel

    @Autowired
    protected lateinit var videoRepository: VideoRepository

    @Autowired
    protected lateinit var collectionRepository: CollectionRepository

    @Autowired
    protected lateinit var mongoClient: MongoClient

    @Autowired
    protected lateinit var ltiOnePointOneConsumerRepository: LtiOnePointOneConsumerRepository

    @Autowired
    protected lateinit var integrationDocumentRepository: MongoIntegrationDocumentRepository

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
    fun clearClientFixtures() {
        FakeClientsConfig.FakeVideoClientFactory.clear()
        FakeClientsConfig.FakeCollectionsClientFactory.clear()
    }
}
