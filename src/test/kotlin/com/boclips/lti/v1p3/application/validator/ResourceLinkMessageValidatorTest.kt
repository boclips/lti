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

    @Test
    fun `throws a validation error when deploymentId is not provided`() {
        val token = DecodedJwtTokenFactory.sample(deploymentIdClaim = null)

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }

    @Test
    fun `throws a validation error when deploymentId is blank`() {
        val token = DecodedJwtTokenFactory.sample(deploymentIdClaim = " ")

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }

    @Test
    fun `throws a validation error when target_link_uri is not provided`() {
        val token = DecodedJwtTokenFactory.sample(targetLinkUriClaim = null)

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }

    @Test
    fun `throws a validation error when target_link_uri is not a URL`() {
        val token = DecodedJwtTokenFactory.sample(targetLinkUriClaim = "well hello there")

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }

    @Test
    fun `throws a validation error when resource link is not provided`() {
        val token = DecodedJwtTokenFactory.sample(resourceLinkClaim = null)

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }

    @Test
    fun `throws a validation error when resource link id is not provided`() {
        val token =
            DecodedJwtTokenFactory.sample(resourceLinkClaim = DecodedJwtTokenFactory.sampleResourceLinkClaim(id = null))

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }

    @Test
    fun `throws a validation error when resource link id is blank`() {
        val token =
            DecodedJwtTokenFactory.sample(resourceLinkClaim = DecodedJwtTokenFactory.sampleResourceLinkClaim(id = ""))

        assertThatThrownBy { ResourceLinkMessageValidator.assertContainsAllRequiredClaims(token) }
            .isInstanceOf(TokenClaimValidationException::class.java)
    }
}
