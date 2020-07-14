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
        "boclips.frontend.ltiBaseUrl=http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}",
        "boclips.frontend.apiBaseUrl=http://api.base.url",
        "boclips.lti.v1p3.signingKeys[0].generationTimestamp=123456",
        "boclips.lti.v1p3.signingKeys[0].privateKey=-----BEGIN PRIVATE KEY-----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-----END PRIVATE KEY-----",
        "boclips.lti.v1p3.signingKeys[0].publicKey=-----BEGIN PUBLIC KEY-----MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA6mFxLbioZLCDNO5euRPaytm8UdEoNacw6s1DNZZ+CqHy32Eaj8S+z2XlKRO4Evj8y1XTTp2+xCaUJuW7efFPYysx7OPXqKuIJeqV0b5qlX2RtSlLAFq/xFeVobZmyFBt23c60iov2iSxcXy30ikEiaSGuYjQ96LJ6J1XAY7Tt9uj6R9MAHJb6DoWEbbYsx0hwz+1PcNSyuhPbpfODt67pFU1NKIRC2ikOc7fJKlJBCjo8N0o0Ca7DkNJUc2sFasR1Ep1b64/t3nrdnmu/o9+un5Y/Y++/RQr1Mv1xmdMKtDVhXyon1HnY8soom+cYvnh8Mw5K46sglA4ZvAho+46FYoh4gFNYBXllAlMZzuJE8x4TbYH8DOA7v27GotgfIs7GM9ATTTp8GlXc8psrxaPDxxbR/bR13v+dizEh8lLORK6yy1/4cFh5EgKYzSvpSU+BZTS37nIz5nDfFDkcLIwFQQlmTmpmgClybn2KnEPXga4etvhGDDzGKAUd1Edi2Tt6ZVAbFEZ+cBVV5XdF+yZC5R50R2rdP01aldmTuu8BBcTkY6zMfkgD6ejdpRJI/5cQ+qMF8+zGjXaDj+oXiNg8/e+2unCpR/Bk+BlQRq3u89psJy0e1/4N+bcNiLtlr0D3vXI1D+P5YFzmPhWZ3fhRuDQH9KPBuDQKIWcCxdEsy8CAwEAAQ==-----END PUBLIC KEY-----",
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
