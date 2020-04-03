package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.JwtClaimValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.Instant.now

class IdTokenValidatorTest {
    @Test
    fun `does not throw when nonce claim is provided`() {
        val validToken = DecodedJwtTokenFactory.sample()
        assertDoesNotThrow { IdTokenValidator.assertHasValidClaims(validToken) }
    }

    @Test
    fun `throws an error when the issuer is not provided`() {
        val token = DecodedJwtTokenFactory.sample(issuerClaim = null)
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws an error when the issuer is empty`() {
        val token = DecodedJwtTokenFactory.sample(issuerClaim = "")
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws an error when the issuer is blank`() {
        val token = DecodedJwtTokenFactory.sample(issuerClaim = "   ")
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws a validation error when nonce is not provided on the token`() {
        val token = DecodedJwtTokenFactory.sample(nonceClaim = null)
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws a validation error when nonce is empty`() {
        val token = DecodedJwtTokenFactory.sample(nonceClaim = "")
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws a validation error when nonce is blank`() {
        val token = DecodedJwtTokenFactory.sample(nonceClaim = "  ")
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws a validation error when audience is not provided on the token`() {
        val token = DecodedJwtTokenFactory.sample(audienceClaim = null)
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throw an error when audience array is empty`() {
        val token = DecodedJwtTokenFactory.sample(audienceClaim = emptyList())
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throw an error when audience array does not contain 'boclips'`() {
        val token = DecodedJwtTokenFactory.sample(audienceClaim = listOf("hello", "hi"))
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws an error if aud contains multiple values and azp does not equal 'boclips'`() {
        val token = DecodedJwtTokenFactory.sample(
            audienceClaim = listOf("boclips", "boclaps"),
            authorizedPartyClaim = "hehehe"
        )
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `does not throw if aud contains multiple values and azp equals 'boclips'`() {
        val token = DecodedJwtTokenFactory.sample(
            audienceClaim = listOf("boclips", "boclaps"),
            authorizedPartyClaim = "boclips"
        )
        assertDoesNotThrow { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws an error if aud has a single value and azp is present, but does not equal 'boclips'`() {
        val token = DecodedJwtTokenFactory.sample(
            audienceClaim = listOf("boclips"),
            authorizedPartyClaim = "hehehe"
        )
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }

    @Test
    fun `throws an error if current time is after the value defined by exp claim`() {
        val token = DecodedJwtTokenFactory.sample(expClaim = now().minusSeconds(10).epochSecond)
        assertThrows<JwtClaimValidationException> { IdTokenValidator.assertHasValidClaims(token) }
    }
}
