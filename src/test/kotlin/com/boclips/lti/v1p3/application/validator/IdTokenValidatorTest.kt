package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.JwtClaimValidationException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.Instant
import java.time.Instant.now

class IdTokenValidatorTest {
    @Test
    fun `does not throw when valid token is provided`() {
        val validToken = DecodedJwtTokenFactory.sample()
        assertDoesNotThrow { validator.assertHasValidClaims(validToken) }
    }

    @Nested
    inner class Nonce {
        @Test
        fun `throws a validation error when nonce is empty`() {
            val token = DecodedJwtTokenFactory.sample(nonceClaim = "")
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("nonce")
        }

        @Test
        fun `throws a validation error when nonce is blank`() {
            val token = DecodedJwtTokenFactory.sample(nonceClaim = "  ")
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("nonce")
        }

        @Test
        fun `throws a validation error when nonce is not provided on the token`() {
            val token = DecodedJwtTokenFactory.sample(nonceClaim = null)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("nonce")
        }
    }

    @Nested
    inner class Issuer {
        @Test
        fun `throws an error when the issuer is not provided`() {
            val token = DecodedJwtTokenFactory.sample(issuerClaim = null)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iss")
        }

        @Test
        fun `throws an error when the issuer is empty`() {
            val token = DecodedJwtTokenFactory.sample(issuerClaim = "")
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iss")
        }

        @Test
        fun `throws an error when the issuer is blank`() {
            val token = DecodedJwtTokenFactory.sample(issuerClaim = "   ")
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iss")
        }
    }

    @Nested
    inner class AudienceAndAuthorizedParty {
        @Test
        fun `throws a validation error when audience is not provided on the token`() {
            val token = DecodedJwtTokenFactory.sample(audienceClaim = null)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("aud")
        }

        @Test
        fun `throw an error when audience array is empty`() {
            val token = DecodedJwtTokenFactory.sample(audienceClaim = emptyList())
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("aud")
        }

        @Test
        fun `throw an error when audience array does not contain 'boclips'`() {
            val token = DecodedJwtTokenFactory.sample(audienceClaim = listOf("hello", "hi"))
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("aud")
        }

        @Test
        fun `throws an error if aud contains multiple values and azp does not equal 'boclips'`() {
            val token = DecodedJwtTokenFactory.sample(
                audienceClaim = listOf("boclips", "boclaps"),
                authorizedPartyClaim = "hehehe"
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("azp")
        }

        @Test
        fun `does not throw if aud contains multiple values and azp equals 'boclips'`() {
            val token = DecodedJwtTokenFactory.sample(
                audienceClaim = listOf("boclips", "boclaps"),
                authorizedPartyClaim = "boclips"
            )
            assertDoesNotThrow { validator.assertHasValidClaims(token) }
        }

        @Test
        fun `throws an error if aud has a single value and azp is present, but does not equal 'boclips'`() {
            val token = DecodedJwtTokenFactory.sample(
                audienceClaim = listOf("boclips"),
                authorizedPartyClaim = "hehehe"
            )
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("azp")
        }
    }

    @Nested
    inner class TimeConstraints {
        @Test
        fun `throws an error if exp claim is not available`() {
            val token = DecodedJwtTokenFactory.sample(expClaim = null)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("exp")
        }

        @Test
        fun `throws an error if current time is after the value defined by exp claim`() {
            val token = DecodedJwtTokenFactory.sample(expClaim = now().minusSeconds(10).epochSecond)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("exp")
        }

        @Test
        fun `throws an error if the iat claim is null`() {
            val token = DecodedJwtTokenFactory.sample(issuedAtClaim = null)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iat")
        }

        @Test
        fun `throws an error if the iat claim is in the future`() {
            val token = DecodedJwtTokenFactory.sample(issuedAtClaim = now().plusSeconds(10).epochSecond)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iat")
        }

        @Test
        fun `throws an error if iat is too far in the past`() {
            validator = IdTokenValidator(
                maxTokenAgeInSeconds = 60,
                currentTime = { Instant.ofEpochSecond(1500000061) }

            )

            val token = DecodedJwtTokenFactory.sample(issuedAtClaim = 1500000000)
            assertThatThrownBy { validator.assertHasValidClaims(token) }
                .isInstanceOf(JwtClaimValidationException::class.java)
                .asString().contains("iat")
        }
    }

    private lateinit var validator: IdTokenValidator

    @BeforeEach
    fun initialise() {
        validator = IdTokenValidator(maxTokenAgeInSeconds = 60)
    }
}
