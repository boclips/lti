package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.LtiMessageClaimValidationException
import com.boclips.lti.v1p3.domain.exception.TargetLinkUriMismatchException
import com.boclips.lti.v1p3.domain.model.mapStateToTargetLinkUri
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import java.util.UUID

class DeepLinkingRequestValidatorTest : AbstractSpringIntegrationTest() {
    @Test
    fun `does not throw when given a valid token mapped to state`() {
        val token = DecodedJwtTokenFactory.sample(
            deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(
                deepLinkReturnUrl = "https://platform.com/link-here-plz",
                acceptTypes = listOf("ltiResourceLink"),
                acceptPresentationDocumentTargets = emptyList()
            )
        )

        assertDoesNotThrow {
            validator.assertIsValid(token, session, state)
        }
    }

    @Nested
    inner class ClaimsValidation {
        @Test
        fun `throws when deep linking settings are not provided`() {
            val token = DecodedJwtTokenFactory.sample(deepLinkingSettingsClaim = null)

            assertThatThrownBy { validator.assertIsValid(token, session, state) }
                .isInstanceOf(LtiMessageClaimValidationException::class.java)
                .asString().contains("deep_linking_settings")
        }

        @Test
        fun `throws when deep_link_return_url is not provided`() {
            val token = DecodedJwtTokenFactory.sample(
                deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(deepLinkReturnUrl = null)
            )

            assertThatThrownBy { validator.assertIsValid(token, session, state) }
                .isInstanceOf(LtiMessageClaimValidationException::class.java)
                .asString().contains("deep_link_return_url")
        }

        @Test
        fun `throws when deep_link_return_url is not a valid URL`() {
            val token = DecodedJwtTokenFactory.sample(
                deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(deepLinkReturnUrl = "trolling you, trolling me")
            )

            assertThatThrownBy { validator.assertIsValid(token, session, state) }
                .isInstanceOf(LtiMessageClaimValidationException::class.java)
                .asString().contains("deep_link_return_url")
        }

        @Test
        fun `throws when accept_types is not provided`() {
            val token = DecodedJwtTokenFactory.sample(
                deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(acceptTypes = null)
            )

            assertThatThrownBy { validator.assertIsValid(token, session, state) }
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

            assertThatThrownBy { validator.assertIsValid(token, session, state) }
                .isInstanceOf(LtiMessageClaimValidationException::class.java)
                .asString().contains("accept_types")
        }

        @Test
        fun `throws when accept_presentation_document_targets is not provided`() {
            val token = DecodedJwtTokenFactory.sample(
                deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(acceptPresentationDocumentTargets = null)
            )

            assertThatThrownBy { validator.assertIsValid(token, session, state) }
                .isInstanceOf(LtiMessageClaimValidationException::class.java)
                .asString().contains("accept_presentation_document_targets")
        }
    }

    @Nested
    inner class StateVerification {
        @Test
        fun `throws when state is not mapped to deep linking in the session`() {
            val token = DecodedJwtTokenFactory.sample(
                deepLinkingSettingsClaim = DecodedJwtTokenFactory.sampleDeepLinkingSettingsClaim(
                    deepLinkReturnUrl = "https://platform.com/link-here-plz",
                    acceptTypes = listOf("ltiResourceLink"),
                    acceptPresentationDocumentTargets = emptyList()
                )
            )

            session.mapStateToTargetLinkUri(state, "gibberish")

            assertThatThrownBy { validator.assertIsValid(token, session, state) }
                .isInstanceOf(TargetLinkUriMismatchException::class.java)
        }
    }

    @Autowired
    private lateinit var validator: DeepLinkingRequestValidator

    private val session = MockHttpSession()
    private val state = UUID.randomUUID().toString()

    @BeforeEach
    fun mapStateToDeepLinkUrl() {
        session.mapStateToTargetLinkUri(state, resourceLinkService.getDeepLinkingLink().toString())
    }
}
