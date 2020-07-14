package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwk.UrlJwkProvider
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import ru.lanwen.wiremock.ext.WiremockResolver
import ru.lanwen.wiremock.ext.WiremockUriResolver
import java.math.BigInteger
import java.net.URL
import java.util.Base64

@ExtendWith(
    value = [
        WiremockResolver::class,
        WiremockUriResolver::class
    ]
)
class Auth0JwksKeyProviderIntegrationTest : AbstractSpringIntegrationTest() {
    val publicKeyId = "Y0lfRMa8h0UZBHgktq-j3oVCMRcn5JloGAAd6KDWe-Q"
    val encodedModulus =
        "oBa8zIfIJAmcnmsirEwINi1qD5w5YT6aEONW9bwnQtQJMe5n-SIEo8hpbQAfjbsKC1NnZVcJLLqOVnEzQGY0u0m3AbVsQA5cQg7fDoN8QYQBvTcvMMwg9u-4142TU9vYbzLdfve6aVFZwSzPr3tdPlGAXHsZYrPh86lMOH572QPEKMSFzeiv4_q7c1tJfeQ_hjCXwRAcGBSjabBZV9js94ZPOjb7UN0IekVyuRwuGAS35NOzvKo8_eTVsGqJCs1e_P14tLX-rMRbwYyp3LAEsMeZn88MsmVrkfMEBSiFSfD9AQBZXIUOIX_Vrl4iVPi__LmcCIIGz4_25Ze2Z_hSVQ"
    val encodedExponent = "AQAB"

    @Test
    fun `returns a public key from configured JWKS endpoint`(
        @WiremockResolver.Wiremock server: WireMockServer,
        @WiremockUriResolver.WiremockUri uri: String
    ) {
        stubJwksResponse(server, publicKeyId, encodedModulus, encodedExponent)

        val keyProvider = Auth0JwksKeyProvider(UrlJwkProvider(URL("$uri/jwks")), retrier)

        val publicKey = keyProvider.getPublicKeyById(publicKeyId)

        val modulusValue = BigInteger(1, Base64.getUrlDecoder().decode(encodedModulus))
        val exponentValue = BigInteger(1, Base64.getUrlDecoder().decode(encodedExponent))

        assertThat(publicKey.modulus).isEqualTo(modulusValue)
        assertThat(publicKey.publicExponent).isEqualTo(exponentValue)
    }

    @Test
    fun `throws UnsupportedOperationException when retrieving a private key`() {
        val keyProvider = Auth0JwksKeyProvider(UrlJwkProvider(URL("https://idp.com/jwks")), retrier)

        assertThrows<UnsupportedOperationException> { keyProvider.privateKey }
    }

    @Test
    fun `throws UnsupportedOperationException when retrieving a private key id `() {
        val keyProvider = Auth0JwksKeyProvider(UrlJwkProvider(URL("https://idp.com/jwks")), retrier)

        assertThrows<UnsupportedOperationException> { keyProvider.privateKeyId }
    }
}
