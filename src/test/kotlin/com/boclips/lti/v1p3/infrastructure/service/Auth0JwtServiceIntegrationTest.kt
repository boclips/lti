package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DeepLinkingSelectionFactory
import com.boclips.lti.testsupport.factories.JwtTokenFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.testsupport.factories.PlatformFactory
import com.boclips.lti.testsupport.factories.SelectedVideoFactory
import com.boclips.lti.v1p3.application.exception.UnsupportedSigningAlgorithmException
import com.boclips.lti.v1p3.application.model.SelectedVideo
import com.boclips.lti.v1p3.application.service.JwtService
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import ru.lanwen.wiremock.ext.WiremockResolver
import ru.lanwen.wiremock.ext.WiremockUriResolver
import java.net.URL
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit

@ExtendWith(
    value = [
        WiremockResolver::class,
        WiremockUriResolver::class
    ]
)
class Auth0JwtServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    private lateinit var service: JwtService

    @Nested
    inner class SignatureVerification {
        @Test
        fun `throws an exception if the token is not signed using RS256`() {
            val token =
                JwtTokenFactory.sampleResourceLinkRequestJwt(signatureAlgorithm = Algorithm.HMAC256("super-secret"))

            assertThrows<UnsupportedSigningAlgorithmException> { service.isSignatureValid(token) }
        }

        @Test
        fun `returns true if the signature on id_token is correct`(
            @WiremockResolver.Wiremock server: WireMockServer,
            @WiremockUriResolver.WiremockUri uri: String
        ) {
            val tokenSigningSetup = setupTokenSigning(server, uri)
            val issuer = "https://example.com/"
            mongoPlatformDocumentRepository.save(
                PlatformDocumentFactory.sample(
                    issuer = issuer,
                    jwksUrl = tokenSigningSetup.jwksUrl
                )
            )

            val token = JwtTokenFactory.sampleResourceLinkRequestJwt(
                issuer = issuer,
                signatureAlgorithm = Algorithm.RSA256(tokenSigningSetup.keyPair.first, tokenSigningSetup.keyPair.second)
            )

            assertThat(service.isSignatureValid(token)).isEqualTo(true)
        }

        @Test
        fun `returns false if the token is signed with different private key`(
            @WiremockResolver.Wiremock server: WireMockServer,
            @WiremockUriResolver.WiremockUri uri: String
        ) {
            val tokenSigningSetup = setupTokenSigning(server, uri)

            val issuer = "https://example.com/"
            mongoPlatformDocumentRepository.save(
                PlatformDocumentFactory.sample(
                    issuer = issuer,
                    jwksUrl = tokenSigningSetup.jwksUrl
                )
            )

            val keyPair = KeyPairGenerator.getInstance("RSA").genKeyPair()
            val token = JwtTokenFactory.sampleResourceLinkRequestJwt(
                issuer = issuer,
                signatureAlgorithm = Algorithm.RSA256(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey)
            )

            assertThat(service.isSignatureValid(token)).isEqualTo(false)
        }
    }

    @Nested
    inner class Decoding {
/*
A key set used to generate JWTs for these tests in https://jwt.io/

-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEAqsJsXXRs3jyBNZmDNABER0J/ELpKduXui9aM4RBSdV69jb3i
pWma5f80Ffdm0IHq1C01mSf4ko78csQSSxksRZMu9r9u9qwq/Nb2JW1go/9rB6cU
DxmWd8Gw6jDwRJHg3wrzQOI+A/a9YBJeiYVTD9SMzmpEWlNcHoBDZnNKANwxo7rl
nGmkcqfhHHk2oJfZYq+Y6anxizCSL+famwDNwOGcp5V/vggoN+kQu6digm5SAudf
fs+fnEOhh9z5ibwcPBblbuJmGw6ORCe1N6K6NBQxPJJze2A6QNjO79Eyct58O4/3
Vfu8VvILpKjK6fWLjhCeD3SjTBd/sSfrL2sYPwIDAQABAoIBAHYLS5BbYLtNBmGA
SwQSvyCn5mEw9zM0hnCjE+auOuKqaM1onBqPY6ZlgqNA0RGJDkY5Lpk+2YavU/Me
eiryLvm14rxu4A2kPV9mVujOsQUOF/ZFWQeLP8pv2pY62I3+bn6h+kE7ZXWmB4o3
iKT2xtHltKUpLzFl+QOY1X8oadsnOyXY8iTNU8x2lRqHBXDsKv6i1ScoIbXrxbn2
FOnND0/muSNG6Ss9LuJnme95QYaw6oK1fXhcuZARTZTP/e6hnZZtdkb5nsCRNJ1k
50PfDn6jhyHmEGpEJaPQaE6QXYyyGTmu0FlgQnI+nZfynELUfX85HBBIjg9Wo/Vl
FRJpSgECgYEA4n4IJEh3xILRtAR/49oTfdyBvJh0lLLlNbB11r6cZAoDSg2ooKre
gipR+5ZWdyXS5pmwZ6oRUQgNzNl0UJTC09205oLOZkyaulmtHYabwtVERKuTGKXM
JxeIv4MYS3GjMgDhwYzJFg4LWfUldv28Ezxp2uRqO8IA5XYqJERmUwECgYEAwQGX
KGKCZvQIaY9b4B6g/sPhAKI1Qv+xdr2dP5ryfxRVrJklLU/kq9Mdd/rVeI8am4oI
iULYEvXzBSsY4AYo5iN5oNN53S23S3kv8Fm9oA2yplDbRLRoAaUPdkbjRRTF+Qxl
c/Juswp8ACQPmM5+E9657XR1/tLBrwex7S/Lqz8CgYBRJ+3M6naX4HQ5t98Hiu2v
DKMkK9CiM5Pz7/3Za8VoBzp0f35ry64dIALuTkk/Ojtey7UtAABic6tWOpj2Asfo
2Kloa3h/qXVLzrQ8Py5y2q4ymPQFQetsGn+yd8vtxJNAaJZ8HvEyyOeCXVgGUhbh
IaWgfHYgYXymu/ePtT6hAQKBgQCQUc0isI6Lx1xm+oCb1KtT9UpQ+/nRsHqVrHFh
903uwljR5+4JtNzrssAtUA3ByVf/CDcb1DTX8LsYmUJPeaupcstm+9r7DU0rDWJQ
WfpWLQfyHzHmbPP8jt1B9v3IUSE7+g3+pkAqduOEA3L6MyXJW9kxes0kj5mudJWS
IxQrZwKBgAs7OC8leMubpjm6khML2BG7xgnlDmkUjCNwLNDUv1O4goyr6YnDLSz/
/Ve+JijUyxI6JbG4pU8xfMSswrnz0VGDN1gMDA7bHT/cVJ7ahwJVwo8LFReVD/s2
1rWLQ7UQ69X8Qtun70Efu3P7t9eWaTJsZDZtWay2+crcX4oqChDw
-----END RSA PRIVATE KEY-----

-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqsJsXXRs3jyBNZmDNABE
R0J/ELpKduXui9aM4RBSdV69jb3ipWma5f80Ffdm0IHq1C01mSf4ko78csQSSxks
RZMu9r9u9qwq/Nb2JW1go/9rB6cUDxmWd8Gw6jDwRJHg3wrzQOI+A/a9YBJeiYVT
D9SMzmpEWlNcHoBDZnNKANwxo7rlnGmkcqfhHHk2oJfZYq+Y6anxizCSL+famwDN
wOGcp5V/vggoN+kQu6digm5SAudffs+fnEOhh9z5ibwcPBblbuJmGw6ORCe1N6K6
NBQxPJJze2A6QNjO79Eyct58O4/3Vfu8VvILpKjK6fWLjhCeD3SjTBd/sSfrL2sY
PwIDAQAB
-----END PUBLIC KEY-----

*/

        @Test
        fun `can decode an resource link id_token JWT string with expected data`() {
            /*
            Payload is:

            {
              "iss": "https://lms.com",
              "sub": "user-id-1234",
              "aud": ["boclips"],
              "azp": "boclips",
              "iat": 1300819380,
              "exp": 1300819399,
              "nonce": "woogie-boogie",
              "https://purl.imsglobal.org/spec/lti/claim/deployment_id": "test-deployment-id",
              "https://purl.imsglobal.org/spec/lti/claim/target_link_uri": "https://tool.net/super/resource",
              "https://purl.imsglobal.org/spec/lti/claim/message_type": "LtiResourceLinkRequest",
              "https://purl.imsglobal.org/spec/lti/claim/version": "1.3.0",
              "https://purl.imsglobal.org/spec/lti/claim/resource_link": {
                "id": "test-resource-link-id"
              }
            }

            */
            val encodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2xtcy5jb20iLCJzdWIiOiJ1c2VyLWlkLTEyMzQiLCJhdWQiOlsiYm9jbGlwcyJdLCJhenAiOiJib2NsaXBzIiwiaWF0IjoxMzAwODE5MzgwLCJleHAiOjEzMDA4MTkzOTksIm5vbmNlIjoid29vZ2llLWJvb2dpZSIsImh0dHBzOi8vcHVybC5pbXNnbG9iYWwub3JnL3NwZWMvbHRpL2NsYWltL2RlcGxveW1lbnRfaWQiOiJ0ZXN0LWRlcGxveW1lbnQtaWQiLCJodHRwczovL3B1cmwuaW1zZ2xvYmFsLm9yZy9zcGVjL2x0aS9jbGFpbS90YXJnZXRfbGlua191cmkiOiJodHRwczovL3Rvb2wubmV0L3N1cGVyL3Jlc291cmNlIiwiaHR0cHM6Ly9wdXJsLmltc2dsb2JhbC5vcmcvc3BlYy9sdGkvY2xhaW0vbWVzc2FnZV90eXBlIjoiTHRpUmVzb3VyY2VMaW5rUmVxdWVzdCIsImh0dHBzOi8vcHVybC5pbXNnbG9iYWwub3JnL3NwZWMvbHRpL2NsYWltL3ZlcnNpb24iOiIxLjMuMCIsImh0dHBzOi8vcHVybC5pbXNnbG9iYWwub3JnL3NwZWMvbHRpL2NsYWltL3Jlc291cmNlX2xpbmsiOnsiaWQiOiJ0ZXN0LXJlc291cmNlLWxpbmstaWQifX0.KldwZsAbiLftmlAyoY84z3WyBNAY_0UC8drF8W3ix60"
            val decodedToken = service.decode(encodedToken)

            assertThat(decodedToken.issuerClaim).isEqualTo("https://lms.com")
            assertThat(decodedToken.audienceClaim).isEqualTo(listOf("boclips"))
            assertThat(decodedToken.authorizedPartyClaim).isEqualTo("boclips")
            assertThat(decodedToken.issuedAtClaim).isEqualTo(1300819380L)
            assertThat(decodedToken.expClaim).isEqualTo(1300819399L)
            assertThat(decodedToken.nonceClaim).isEqualTo("woogie-boogie")
            assertThat(decodedToken.deploymentIdClaim).isEqualTo("test-deployment-id")
            assertThat(decodedToken.targetLinkUriClaim).isEqualTo("https://tool.net/super/resource")
            assertThat(decodedToken.messageTypeClaim).isEqualTo("LtiResourceLinkRequest")
            assertThat(decodedToken.ltiVersionClaim).isEqualTo("1.3.0")
            assertThat(decodedToken.resourceLinkClaim?.id).isEqualTo("test-resource-link-id")
            assertThat(decodedToken.subjectClaim).isEqualTo("user-id-1234")
        }

        @Test
        fun `can decode an deep linking id_token JWT string with expected data`() {
            /*
            Payload is:

            {
              "iss": "https://lms.com",
              "aud": ["boclips"],
              "azp": "boclips",
              "iat": 1300819380,
              "exp": 1300819399,
              "nonce": "woogie-boogie",
              "https://purl.imsglobal.org/spec/lti/claim/deployment_id": "test-deployment-id",
              "https://purl.imsglobal.org/spec/lti/claim/message_type": "LtiDeepLinkingRequest",
              "https://purl.imsglobal.org/spec/lti/claim/version": "1.3.0",
              "https://purl.imsglobal.org/spec/lti-dl/claim/deep_linking_settings": {
                "deep_link_return_url": "https://lms.com/return-here",
                "accept_types": ["ltiResourceLink", "link"],
                "accept_presentation_document_targets": ["iframe", "window", "embed"],
                "data": "opaque-value-to-be-passed-back-on-linking"
              }
            }

            */
            val encodedToken =
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2xtcy5jb20iLCJhdWQiOlsiYm9jbGlwcyJdLCJhenAiOiJib2NsaXBzIiwiaWF0IjoxMzAwODE5MzgwLCJleHAiOjEzMDA4MTkzOTksIm5vbmNlIjoid29vZ2llLWJvb2dpZSIsImh0dHBzOi8vcHVybC5pbXNnbG9iYWwub3JnL3NwZWMvbHRpL2NsYWltL2RlcGxveW1lbnRfaWQiOiJ0ZXN0LWRlcGxveW1lbnQtaWQiLCJodHRwczovL3B1cmwuaW1zZ2xvYmFsLm9yZy9zcGVjL2x0aS9jbGFpbS9tZXNzYWdlX3R5cGUiOiJMdGlEZWVwTGlua2luZ1JlcXVlc3QiLCJodHRwczovL3B1cmwuaW1zZ2xvYmFsLm9yZy9zcGVjL2x0aS9jbGFpbS92ZXJzaW9uIjoiMS4zLjAiLCJodHRwczovL3B1cmwuaW1zZ2xvYmFsLm9yZy9zcGVjL2x0aS1kbC9jbGFpbS9kZWVwX2xpbmtpbmdfc2V0dGluZ3MiOnsiZGVlcF9saW5rX3JldHVybl91cmwiOiJodHRwczovL2xtcy5jb20vcmV0dXJuLWhlcmUiLCJhY2NlcHRfdHlwZXMiOlsibHRpUmVzb3VyY2VMaW5rIiwibGluayJdLCJhY2NlcHRfcHJlc2VudGF0aW9uX2RvY3VtZW50X3RhcmdldHMiOlsiaWZyYW1lIiwid2luZG93IiwiZW1iZWQiXSwiZGF0YSI6Im9wYXF1ZS12YWx1ZS10by1iZS1wYXNzZWQtYmFjay1vbi1saW5raW5nIn19.L96sdRhUrzegxnH_nmaPvWGokrP1YQ_lBlPwrd2AjR_g5VJJRQ0tVWKWarrgUoabN98pUxKO2ueJPgWU80sPX_8HLT5Ar15XYL2XMSWd8y4c0THldrNrrzEisuE5Iij6HKRSSoFtz50SjN3GBcFecaEAFLPzt9Xef9QySOSfuNqt4u0yV91m6Ucf_jaDnlJ7JIcc8kCyGpplaJdt-k2SrdmxaoLTIZzEVbAmIPSfYC0N4rWm5vp3Hi2aDsRQti8so6ImvkD88xyY11pIc4qjUM69aILnvlGTLeUW-09GxsKdh0_eIXncfbEedS7tnaxbuOE4DEINV9ywFdPOphLcRA"

            val decodedToken = service.decode(encodedToken)

            assertThat(decodedToken.issuerClaim).isEqualTo("https://lms.com")
            assertThat(decodedToken.audienceClaim).isEqualTo(listOf("boclips"))
            assertThat(decodedToken.authorizedPartyClaim).isEqualTo("boclips")
            assertThat(decodedToken.issuedAtClaim).isEqualTo(1300819380L)
            assertThat(decodedToken.expClaim).isEqualTo(1300819399L)
            assertThat(decodedToken.nonceClaim).isEqualTo("woogie-boogie")
            assertThat(decodedToken.deploymentIdClaim).isEqualTo("test-deployment-id")
            assertThat(decodedToken.messageTypeClaim).isEqualTo("LtiDeepLinkingRequest")
            assertThat(decodedToken.ltiVersionClaim).isEqualTo("1.3.0")
            assertThat(decodedToken.deepLinkingSettingsClaim?.deepLinkReturnUrl).isEqualTo("https://lms.com/return-here")
            assertThat(decodedToken.deepLinkingSettingsClaim?.acceptTypes).containsExactlyInAnyOrder(
                "ltiResourceLink",
                "link"
            )
            assertThat(decodedToken.deepLinkingSettingsClaim?.acceptPresentationDocumentTargets).containsExactlyInAnyOrder(
                "iframe",
                "window",
                "embed"
            )
            assertThat(decodedToken.deepLinkingSettingsClaim?.data).isEqualTo("opaque-value-to-be-passed-back-on-linking")
        }

        @Test
        fun `can decode aud claim if it's a special case of a string instead of an array`() {
            /*
            Payload is:

            {
              "aud": "boclips"
            }

            */
            val encodedToken =
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJib2NsaXBzIn0.K0xEAWCQ-StRkAvEortwfcACPmnJbCi3nHjwCR0cR3DJ8RotDu8t5e-I3vbIaQgCw2VBHxiCIVMw1fB_YSdgolgOxV19vrnP2VlT50xxQd6Sj_Ns0Wd-6_ofxLC3dXIDnbM0zaZamBu4oP1vqLXF1Ec9pDGItQ3JFhERvkswkpEXwCLxEBU4iF9zdRj1vnc1zdLp5Shi7AUyLxjYRL1PgGkNJxJikr4fBODayMHsVVCn-HVQMDkM3PjSsZ-gvoAvzHlhdt3CaBtBGUxJzEqQNm6BY3o6vp_dx9eLxMej8F6cMAprrAwSjr5GEUyT62Xs148Ub_aorYCGZAO7hh5zfg"

            val decodedToken = service.decode(encodedToken)

            assertThat(decodedToken.audienceClaim).isEqualTo(listOf("boclips"))
        }

        @Test
        fun `can decode an id_token JWT string with missing fields, setting values to null`() {
            /*
            Payload is:

            {

            }

            */
            val encodedToken =
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.e30.hiK-vhXM0dc8OaDwosY03GsTjcEiYzrdZPXn16-sV-9m8-KfprDTI6SRcw_4JVER0VHOdnHLufKmDHWgQBu0D1lBdP56RhZ0gSjgvRgICM1k6-KBmeWCsCzr_asnfP0LwPXzOzqOOeeyCtUAUanU0PHvB-KU2Bglx6e2hoS1Eu5PUIIl0RB0EGMzSOTXPh-nppKbKcf6QZaJfT_R0NHO_Hi2IPLVAA_2PtdaEIyxrcI5_OBVTFIMnROGtWwdbc1aaTKc4i0zHy1xTWDZKfTgy6O2ty07n6SlnShlV1Sy3ykpS_pmkbsAOx-dfAkjdBJlU3XOT_MjbZtetFwNUSEAJQ"

            val decodedToken = service.decode(encodedToken)

            assertThat(decodedToken.issuerClaim).isNull()
            assertThat(decodedToken.nonceClaim).isNull()
            assertThat(decodedToken.authorizedPartyClaim).isNull()
            assertThat(decodedToken.expClaim).isNull()
            assertThat(decodedToken.issuedAtClaim).isNull()
            assertThat(decodedToken.deploymentIdClaim).isNull()
            assertThat(decodedToken.subjectClaim).isNull()
            assertThat(decodedToken.targetLinkUriClaim).isNull()
            assertThat(decodedToken.messageTypeClaim).isNull()
            assertThat(decodedToken.ltiVersionClaim).isNull()
            assertThat(decodedToken.resourceLinkClaim).isNull()
            assertThat(decodedToken.nonceClaim).isNull()
            assertThat(decodedToken.deepLinkingSettingsClaim).isNull()
        }
    }

    @Nested
    inner class CreatingDeepLinkingResponseToken {
        private val platformIdentifier = "https://platform.com"
        private val platform = PlatformFactory.sample(issuer = URL(platformIdentifier))

        @Test
        fun `creates a token with selected LTI links`() {
            val selection = listOf(
                SelectedVideoFactory.sample(
                    url = "https://tool.com/videos/123",
                    title = "fantabulous title",
                    text = "fantabulous description"
                )
            )

            val token = service.createDeepLinkingResponseToken(
                platform,
                DeepLinkingSelectionFactory.sample(selectedVideos = selection)
            )

            val contentItemsClaim =
                JWT.decode(token).getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/content_items")
                    .asArray(HashMap::class.java)

            assertThat(contentItemsClaim).hasSize(1)
            val item = contentItemsClaim[0]
            assertThat(item["url"]).isEqualTo("https://tool.com/videos/123")
            assertThat(item["type"]).isEqualTo("ltiResourceLink")
            assertThat(item["title"]).isEqualTo("fantabulous title")
            assertThat(item["text"]).isEqualTo("fantabulous description")
        }

        @Test
        fun `creates a token with empty selection`() {
            val selection = emptyList<SelectedVideo>()

            val token = service.createDeepLinkingResponseToken(
                platform,
                DeepLinkingSelectionFactory.sample(selectedVideos = selection)
            )

            val contentItemsClaim =
                JWT.decode(token).getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/content_items")
                    .asArray(HashMap::class.java)

            assertThat(contentItemsClaim).isEmpty()
        }

        @Test
        fun `sets all JWT claims required by LTI specification`() {
            val deepLinkingSelection = DeepLinkingSelectionFactory.sample(deploymentId = "123", data = "some data")
            val clientId = "i-am-a-client"
            val platform = PlatformFactory.sample(issuer = URL(platformIdentifier), clientId = clientId)
            val token = service.createDeepLinkingResponseToken(platform, deepLinkingSelection)

            val decodedToken = JWT.decode(token)

            assertThat(decodedToken.audience).contains(platformIdentifier)
            assertThat(decodedToken.issuer).isEqualTo(clientId)
            assertThat(decodedToken.expiresAt.toInstant()).isAfter(Instant.now())
            assertThat(decodedToken.issuedAt.toInstant()).isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS))
            assertThat(decodedToken.getClaim("nonce").asString()).isNotBlank()
            assertThat(
                decodedToken.getClaim("https://purl.imsglobal.org/spec/lti/claim/message_type").asString()
            ).isEqualTo("LtiDeepLinkingResponse")
            assertThat(decodedToken.getClaim("https://purl.imsglobal.org/spec/lti/claim/version").asString()).isEqualTo(
                "1.3.0"
            )
            assertThat(
                decodedToken.getClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id").asString()
            ).isEqualTo("123")
            assertThat(decodedToken.getClaim("https://purl.imsglobal.org/spec/lti-dl/claim/data").asString()).isEqualTo(
                "some data"
            )
        }
    }
}
