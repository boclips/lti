package com.boclips.lti.v1p3.application.command

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.exception.LtiMessageClaimValidationException
import com.boclips.lti.v1p3.domain.model.mapStateToTargetLinkUri
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import java.util.UUID

class HandleDeepLinkingRequestIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `throws when given a token with invalid claims`() {
        val invalidToken = DecodedJwtTokenFactory.sample(deepLinkingSettingsClaim = null)

        assertThatThrownBy { handleDeepLinkingRequest(invalidToken, session, state) }
            .isInstanceOf(LtiMessageClaimValidationException::class.java)
    }

    @Test
    fun `does not throw when given a valid token`() {
        val issuer = "https://lms.com"
        insertPlatform(issuer)

        val validDeepLinkingToken = DecodedJwtTokenFactory.sampleDeepLinkingToken(issuerClaim = issuer)

        assertDoesNotThrow { handleDeepLinkingRequest(validDeepLinkingToken, session, state) }
    }

    private val session = MockHttpSession()
    private val state = UUID.randomUUID().toString()

    @BeforeEach
    fun mapStateToDeepLinkUrl() {
        session.mapStateToTargetLinkUri(state, resourceLinkService.getDeepLinkingLink().toString())
    }

    @Autowired
    private lateinit var handleDeepLinkingRequest: HandleDeepLinkingRequest
}
