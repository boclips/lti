package com.boclips.lti.v1p3.domain.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
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
class JwtVerificationServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    private lateinit var service: JwtVerificationService

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
