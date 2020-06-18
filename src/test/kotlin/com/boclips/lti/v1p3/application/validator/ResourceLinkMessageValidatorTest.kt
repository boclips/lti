package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.LtiMessageClaimValidationException
import com.boclips.lti.v1p3.application.model.mapStateToTargetLinkUri
import com.boclips.lti.v1p3.domain.exception.TargetLinkUriMismatchException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockHttpSession

class ResourceLinkMessageValidatorTest {
    @Test
    fun `does not throw when token contains all required claims`() {
        val validToken = DecodedJwtTokenFactory.sample(targetLinkUriClaim = "https://tool.com/resource/1")
        session.mapStateToTargetLinkUri(state, validToken.targetLinkUriClaim!!)

        assertDoesNotThrow {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = validToken,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when LTI version does not equal '1 3 0'`() {
        val token = DecodedJwtTokenFactory.sample(
            ltiVersionClaim = "woogie-boogie",
            targetLinkUriClaim = "https://tool.com/resource/1"
        )
        session.mapStateToTargetLinkUri(state, token.targetLinkUriClaim!!)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when deploymentId is not provided`() {
        val token =
            DecodedJwtTokenFactory.sample(deploymentIdClaim = null, targetLinkUriClaim = "https://tool.com/resource/1")
        session.mapStateToTargetLinkUri(state, token.targetLinkUriClaim!!)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when deploymentId is blank`() {
        val token =
            DecodedJwtTokenFactory.sample(deploymentIdClaim = " ", targetLinkUriClaim = "https://tool.com/resource/1")
        session.mapStateToTargetLinkUri(state, token.targetLinkUriClaim!!)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when target_link_uri is not provided`() {
        val token = DecodedJwtTokenFactory.sample(targetLinkUriClaim = null)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when target_link_uri is not a URL`() {
        val token = DecodedJwtTokenFactory.sample(targetLinkUriClaim = "well hello there")
        session.mapStateToTargetLinkUri(state, token.targetLinkUriClaim!!)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when resource link is not provided`() {
        val token =
            DecodedJwtTokenFactory.sample(resourceLinkClaim = null, targetLinkUriClaim = "https://tool.com/resource/1")
        session.mapStateToTargetLinkUri(state, token.targetLinkUriClaim!!)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when resource link id is not provided`() {
        val token =
            DecodedJwtTokenFactory.sample(
                resourceLinkClaim = DecodedJwtTokenFactory.sampleResourceLinkClaim(id = null),
                targetLinkUriClaim = "https://tool.com/resource/1"
            )
        session.mapStateToTargetLinkUri(state, token.targetLinkUriClaim!!)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws a validation error when resource link id is blank`() {
        val token =
            DecodedJwtTokenFactory.sample(
                resourceLinkClaim = DecodedJwtTokenFactory.sampleResourceLinkClaim(id = ""),
                targetLinkUriClaim = "https://tool.com/resource/1"
            )
        session.mapStateToTargetLinkUri(state, token.targetLinkUriClaim!!)

        assertThrows<LtiMessageClaimValidationException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    @Test
    fun `throws an exception when target link URI does not match what's mapped to state`() {
        val token = DecodedJwtTokenFactory.sample(
            issuerClaim = "https://platform.com",
            audienceClaim = listOf("tool-client-id"),
            targetLinkUriClaim = "https://tool.com/expectation"
        )
        session.mapStateToTargetLinkUri(state, "https://tool.com/reality")

        assertThrows<TargetLinkUriMismatchException> {
            ResourceLinkMessageValidator.assertIsValid(
                state = state,
                token = token,
                session = session
            )
        }
    }

    private val state = "state"

    private val session = MockHttpSession()
}
