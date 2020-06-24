package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.LtiMessageClaimValidationException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class DeepLinkingRequestValidatorTest {
    @Test
    fun `does not throw when given a token with valid deep linking claims`() {
        val token = DecodedJwtTokenFactory.sample(
            deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(
                deepLinkReturnUrl = "https://platform.com/link-here-plz",
                acceptTypes = listOf("ltiResourceLink"),
                acceptPresentationDocumentTargets = emptyList()
            )
        )

        assertDoesNotThrow {
            DeepLinkingRequestValidator.assertIsValid(token)
        }
    }

    @Test
    fun `throws when deep linking settings are not provided`() {
        val token = DecodedJwtTokenFactory.sample(deepLinkingSettingsClaim = null)

        assertThatThrownBy { DeepLinkingRequestValidator.assertIsValid(token) }
            .isInstanceOf(LtiMessageClaimValidationException::class.java)
            .asString().contains("deep_linking_settings")
    }

    @Test
    fun `throws when deep_link_return_url is not provided`() {
        val token = DecodedJwtTokenFactory.sample(
            deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(deepLinkReturnUrl = null)
        )

        assertThatThrownBy { DeepLinkingRequestValidator.assertIsValid(token) }
            .isInstanceOf(LtiMessageClaimValidationException::class.java)
            .asString().contains("deep_link_return_url")
    }

    @Test
    fun `throws when deep_link_return_url is not a valid URL`() {
        val token = DecodedJwtTokenFactory.sample(
            deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(deepLinkReturnUrl = "trolling you, trolling me")
        )

        assertThatThrownBy { DeepLinkingRequestValidator.assertIsValid(token) }
            .isInstanceOf(LtiMessageClaimValidationException::class.java)
            .asString().contains("deep_link_return_url")
    }

    @Test
    fun `throws when accept_types is not provided`() {
        val token = DecodedJwtTokenFactory.sample(
            deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(acceptTypes = null)
        )

        assertThatThrownBy { DeepLinkingRequestValidator.assertIsValid(token) }
            .isInstanceOf(LtiMessageClaimValidationException::class.java)
            .asString().contains("accept_types")
    }

    @Test
    fun `throws when accept_types does not contain ltiResourceLink`() {
        val token = DecodedJwtTokenFactory.sample(
            deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(
                acceptTypes = listOf(
                    "optimus prime",
                    "megatron"
                )
            )
        )

        assertThatThrownBy { DeepLinkingRequestValidator.assertIsValid(token) }
            .isInstanceOf(LtiMessageClaimValidationException::class.java)
            .asString().contains("accept_types")
    }

    @Test
    fun `throws when accept_presentation_document_targets is not provided`() {
        val token = DecodedJwtTokenFactory.sample(
            deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(acceptPresentationDocumentTargets = null)
        )

        assertThatThrownBy { DeepLinkingRequestValidator.assertIsValid(token) }
            .isInstanceOf(LtiMessageClaimValidationException::class.java)
            .asString().contains("accept_presentation_document_targets")
    }
}
