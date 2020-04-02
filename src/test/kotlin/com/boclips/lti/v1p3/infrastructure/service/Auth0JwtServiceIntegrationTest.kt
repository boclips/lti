package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.service.JwtService
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import ru.lanwen.wiremock.ext.WiremockResolver
import ru.lanwen.wiremock.ext.WiremockUriResolver
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

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

            val token = JWT.create()
                .withIssuer(issuer)
                .sign(Algorithm.RSA256(tokenSigningSetup.keyPair.first, tokenSigningSetup.keyPair.second))

            assertThat(service.isSignatureValid(token)).isTrue()
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
            val token = JWT.create()
                .withIssuer(issuer)
                .sign(Algorithm.RSA256(keyPair.public as RSAPublicKey, keyPair.private as RSAPrivateKey))

            assertThat(service.isSignatureValid(token)).isFalse()
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
        fun `can decode an id_token JWT string with expected data`() {
            /*
            Payload is:

            {
              "iss": "super-issuer",
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
            val encodedToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBlci1pc3N1ZXIiLCJub25jZSI6Indvb2dpZS1ib29naWUiLCJodHRwczovL3B1cmwuaW1zZ2xvYmFsLm9yZy9zcGVjL2x0aS9jbGFpbS9kZXBsb3ltZW50X2lkIjoidGVzdC1kZXBsb3ltZW50LWlkIiwiaHR0cHM6Ly9wdXJsLmltc2dsb2JhbC5vcmcvc3BlYy9sdGkvY2xhaW0vdGFyZ2V0X2xpbmtfdXJpIjoiaHR0cHM6Ly90b29sLm5ldC9zdXBlci9yZXNvdXJjZSIsImh0dHBzOi8vcHVybC5pbXNnbG9iYWwub3JnL3NwZWMvbHRpL2NsYWltL21lc3NhZ2VfdHlwZSI6Ikx0aVJlc291cmNlTGlua1JlcXVlc3QiLCJodHRwczovL3B1cmwuaW1zZ2xvYmFsLm9yZy9zcGVjL2x0aS9jbGFpbS92ZXJzaW9uIjoiMS4zLjAiLCJodHRwczovL3B1cmwuaW1zZ2xvYmFsLm9yZy9zcGVjL2x0aS9jbGFpbS9yZXNvdXJjZV9saW5rIjp7ImlkIjoidGVzdC1yZXNvdXJjZS1saW5rLWlkIn19.QA-MKQ9TdXfgbsJmB5SHDU3TdUUBjfNDvXrUTtvp8FKy6t6wunY4mVoRF2m1PZISArnLNjFx5N1mzgo93-QQTniMJ22y2RL9_wQsQZnC0hrA0kskbGdNbHcs5ilnuX6P_i3BtTgyrtGuxCuo4Nb743M2FEbYXNyq0DKhiDnCku0RNNUrUijxdUUNVqQcp2hCfIIDmM96rIdXlG8YtHfFuyldDBpAWE17xAGN1N5X3KO50IWPyq_JfqWurr3eI3An-14pl3nH_yoMRmsUaOsPn39eGPQi5IudK0Tm42aqd049pmdCI22ee_7ofrZfi9LPJYD5u6Eom3Q-MQVvQ7LOjQ"

            val decodedToken = service.decode(encodedToken)

            assertThat(decodedToken.issuerClaim).isEqualTo("super-issuer")
            assertThat(decodedToken.nonceClaim).isEqualTo("woogie-boogie")
            assertThat(decodedToken.deploymentIdClaim).isEqualTo("test-deployment-id")
            assertThat(decodedToken.targetLinkUriClaim).isEqualTo("https://tool.net/super/resource")
            assertThat(decodedToken.messageTypeClaim).isEqualTo("LtiResourceLinkRequest")
            assertThat(decodedToken.ltiVersionClaim).isEqualTo("1.3.0")
            assertThat(decodedToken.resourceLinkClaim?.id).isEqualTo("test-resource-link-id")
        }

        @Test
        fun `can decode an id_token JWT string with missing fields, setting values to null`() {
            /*
            Payload is:

            {

            }

            */
            val encodedToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.e30.hiK-vhXM0dc8OaDwosY03GsTjcEiYzrdZPXn16-sV-9m8-KfprDTI6SRcw_4JVER0VHOdnHLufKmDHWgQBu0D1lBdP56RhZ0gSjgvRgICM1k6-KBmeWCsCzr_asnfP0LwPXzOzqOOeeyCtUAUanU0PHvB-KU2Bglx6e2hoS1Eu5PUIIl0RB0EGMzSOTXPh-nppKbKcf6QZaJfT_R0NHO_Hi2IPLVAA_2PtdaEIyxrcI5_OBVTFIMnROGtWwdbc1aaTKc4i0zHy1xTWDZKfTgy6O2ty07n6SlnShlV1Sy3ykpS_pmkbsAOx-dfAkjdBJlU3XOT_MjbZtetFwNUSEAJQ"

            val decodedToken = service.decode(encodedToken)

            assertThat(decodedToken.issuerClaim).isNull()
            assertThat(decodedToken.deploymentIdClaim).isNull()
            assertThat(decodedToken.targetLinkUriClaim).isNull()
            assertThat(decodedToken.messageTypeClaim).isNull()
            assertThat(decodedToken.ltiVersionClaim).isNull()
            assertThat(decodedToken.resourceLinkClaim).isNull()
            assertThat(decodedToken.nonceClaim).isNull()
        }
    }
}
