package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.TokenClaimValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class IdTokenValidatorTest {
    @Test
    fun `throws a validation error when nonce is not provided on the token`() {
        val token = DecodedJwtTokenFactory.sample(nonceClaim = null)
        assertThrows<TokenClaimValidationException> { IdTokenValidator.assertHasNonce(token) }
    }

    @Test
    fun `does not throw when nonce claim is provided`() {
        val validToken = DecodedJwtTokenFactory.sample()
        assertDoesNotThrow { IdTokenValidator.assertHasNonce(validToken) }
    }
}
