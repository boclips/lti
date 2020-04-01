package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.TokenClaimValidationException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ResourceLinkMessageValidatorTest {
    @Test
    fun `does not throw when token contains all required claims`() {
        val validToken = DecodedJwtTokenFactory.sample()

        assertDoesNotThrow { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(validToken) }
    }

    @Test
    fun `throws a validation error when LTI version does not equal '1 3 0'`() {
        val token = DecodedJwtTokenFactory.sample(ltiVersionClaim = "woogie-boogie")

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }
}
