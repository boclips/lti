package com.boclips.lti.testsupport

import com.boclips.lti.core.application.service.ApiAccessTokenProviderTest
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.core.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.core.infrastructure.service.VideosClientFactory
import com.boclips.lti.core.presentation.service.ToVideoViewModel
import com.boclips.lti.testsupport.configuration.FakeClientsConfig
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p1.infrastructure.repository.LtiOnePointOneConsumerRepository
import com.boclips.lti.v1p3.infrastructure.repository.MongoNonceDocumentRepository
import com.boclips.lti.v1p3.infrastructure.repository.MongoPlatformDocumentRepository
import com.boclips.lti.v1p3.infrastructure.service.Auth0UrlJwkProviderRetrier
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import com.boclips.videos.api.response.video.VideoResource
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
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
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.Base64
import java.util.UUID

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
    properties = [
        "boclips.api.baseUrl=http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}/v1",
        "boclips.api.tokenUrl=http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}/v1/token",
        "boclips.frontend.ltiTokenUrl=http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}/",
        "boclips.frontend.apiBaseUrl=http://api.base.url",
        "boclips.dev-support.integrationId=test-integration-id",
        "boclips.dev-support.initialiseDevelopmentSession=false"
    ]
)
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
    protected lateinit var resourceLinkService: ResourceLinkService

    @Autowired
    protected lateinit var mongoClient: MongoClient

    @Autowired
    protected lateinit var ltiOnePointOneConsumerRepository: LtiOnePointOneConsumerRepository

    @Autowired
    protected lateinit var integrationDocumentRepository: MongoIntegrationDocumentRepository

    @Autowired
    protected lateinit var mongoPlatformDocumentRepository: MongoPlatformDocumentRepository

    @Autowired
    protected lateinit var mongoNonceDocumentRepository: MongoNonceDocumentRepository

    @Autowired
    protected lateinit var retrier: Auth0UrlJwkProviderRetrier

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

    protected fun stubJwksResponse(
        server: WireMockServer,
        publicKeyId: String,
        encodedModulus: String,
        encodedExponent: String
    ) {
        server
            .stubFor(
                WireMock.get(WireMock.urlEqualTo("/.well-known/jwks.json"))
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(
                                """
                                {
                                  "keys": [
                                    {
                                      "kid": "$publicKeyId",
                                      "kty": "RSA",
                                      "alg": "RS256",
                                      "use": "sig",
                                      "n": "$encodedModulus",
                                      "e": "$encodedExponent",
                                      "x5c": [
                                        "MIICnTCCAYUCBgFxG80tMzANBgkqhkiG9w0BAQsFADASMRAwDgYDVQQDDAdib2NsaXBzMB4XDTIwMDMyNzExNDEwM1oXDTMwMDMyNzExNDI0M1owEjEQMA4GA1UEAwwHYm9jbGlwczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKAWvMyHyCQJnJ5rIqxMCDYtag+cOWE+mhDjVvW8J0LUCTHuZ/kiBKPIaW0AH427CgtTZ2VXCSy6jlZxM0BmNLtJtwG1bEAOXEIO3w6DfEGEAb03LzDMIPbvuNeNk1Pb2G8y3X73umlRWcEsz697XT5RgFx7GWKz4fOpTDh+e9kDxCjEhc3or+P6u3NbSX3kP4Ywl8EQHBgUo2mwWVfY7PeGTzo2+1DdCHpFcrkcLhgEt+TTs7yqPP3k1bBqiQrNXvz9eLS1/qzEW8GMqdywBLDHmZ/PDLJla5HzBAUohUnw/QEAWVyFDiF/1a5eIlT4v/y5nAiCBs+P9uWXtmf4UlUCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAL3sB4eM8/lnwEQ3IK53uLVRkbGThNZEesAEowIX+XSp8r4x5gFlqE72kKa5SFX1B9w9N1f5msOYZoO5MraDxyeCHNlGvl5udniub5mrFJfQF3/+vGqi/03ckE+Salj1P3piWGMw+YLZ/igXMneRxFzErH1izg0lqaB9sgdX7NCjtkAUesSbznV5/9/jCtNs7UvoSR6f16p4lvH9IKZcJzRaYZz6MXkVzkt2ViKcl9vExOAUzwL306RVtFTginkElLwvxBNKmo9MspTSKdoZICB+8CJRFxzlqYWgtmCscWzCQG0SHcVXzUJu5gnB0ARkzpe6e4bHoSKrtTXCeKuBVFA=="
                                      ],
                                      "x5t": "9nrzejjjfL8wbj77uco0sf5YLwY",
                                      "x5t#S256": "fmOamYW3tK_fgc-D5BltvRqaaZC0FdhzlxPwXijZqHA"
                                    }
                                  ]
                                }
                            """.trimIndent()
                            )
                    )
            )
    }

    protected fun setupTokenSigning(server: WireMockServer, uri: String): TokenSigningSetup {
        val publicKeyId = UUID.randomUUID().toString()
        val keyPair = KeyPairGenerator.getInstance("RSA").genKeyPair()
        val rsaPublicKey = keyPair.public as RSAPublicKey

        stubJwksResponse(
            server,
            publicKeyId,
            Base64.getUrlEncoder().encodeToString(rsaPublicKey.modulus.toByteArray()),
            Base64.getUrlEncoder().encodeToString(rsaPublicKey.publicExponent.toByteArray())
        )

        return TokenSigningSetup(
            keyPair = rsaPublicKey to keyPair.private as RSAPrivateKey,
            jwksUrl = "$uri/.well-known/jwks.json"
        )
    }

    data class TokenSigningSetup(
        val keyPair: Pair<RSAPublicKey, RSAPrivateKey>,
        val jwksUrl: String
    )

    protected fun extractStateFromUrl(url: URL): String {
        return UriComponentsBuilder.fromUri(url.toURI()).build()
            .queryParams["state"]!!
            .first()
    }

    protected fun extractStateFromLocationHeader(response: MockHttpServletResponse): String {
        return UriComponentsBuilder.fromUriString(response.getHeader("Location")!!).build()
            .queryParams["state"]!!
            .first()
    }

    protected fun insertPlatform(issuer: String) {
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer))
    }

    protected fun saveVideo(video: VideoResource, integrationId: String): VideoResource {
        return (videosClientFactory.getClient(integrationId) as VideosClientFake).add(video)
    }
}
