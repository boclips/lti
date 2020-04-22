package com.boclips.lti.v1p3.application.command

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.testsupport.factories.NonceDocumentFactory
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.exception.InvalidJwtTokenSignatureException
import com.boclips.lti.v1p3.application.exception.JwtClaimValidationException
import com.boclips.lti.v1p3.application.exception.NonceReusedException
import com.boclips.lti.v1p3.application.exception.StatesDoNotMatchException
import com.boclips.lti.v1p3.application.model.mapStateToTargetLinkUri
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.exception.TargetLinkUriMismatchException
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockHttpSession
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import javax.servlet.http.HttpSession

class PerformSecurityChecksIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `does not throw and stores a nonce for future reference when checks pass`() {
        val state = "state"
        val nonceValue = "nonce"
        val resourceUri = "https://tool.com/resource/123"

        session.mapStateToTargetLinkUri(state, resourceUri)
        whenever(jwtService.isSignatureValid(token)).thenReturn(true)
        whenever(jwtService.decode(token)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                nonceClaim = nonceValue,
                targetLinkUriClaim = resourceUri

            )
        )

        assertDoesNotThrow { performSecurityChecks(state, token, session) }

        val nonce = mongoNonceDocumentRepository.findOneByValue(nonceValue)!!
        assertThat(nonce.value).isEqualTo(nonceValue)
        assertThat(nonce.createdAt).isCloseTo(now(), within(10, ChronoUnit.SECONDS))
    }

    @Test
    fun `throws an exception when states do not match`() {
        session.mapStateToTargetLinkUri("reality", "https://this-could-be-anythig.com")

        assertThrows<StatesDoNotMatchException> { performSecurityChecks("expectation", "token", session) }
    }

    @Test
    fun `throws an exception when token signature does not match`() {
        val state = "state"
        val resourceUri = "https://tool.com/resource/123"

        session.mapStateToTargetLinkUri(state, resourceUri)
        whenever(jwtService.isSignatureValid(token)).thenReturn(false)

        assertThrows<InvalidJwtTokenSignatureException> { performSecurityChecks(state, token, session) }
    }

    @Test
    fun `throws an exception when nonce has already been used`() {
        val state = "state"
        val resourceUri = "https://tool.com/resource/123"

        session.mapStateToTargetLinkUri(state, resourceUri)
        whenever(jwtService.isSignatureValid(token)).thenReturn(true)
        whenever(jwtService.decode(token)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                nonceClaim = "nonce",
                targetLinkUriClaim = resourceUri
            )
        )

        mongoNonceDocumentRepository.insert(NonceDocumentFactory.sample(value = "nonce"))

        assertThrows<NonceReusedException> { performSecurityChecks(state, token, session) }
    }

    @Test
    fun `throws an exception when nonce is not provided on the token`() {
        val state = "state"
        val resourceUri = "https://tool.com/resource/123"

        session.mapStateToTargetLinkUri(state, resourceUri)
        whenever(jwtService.isSignatureValid(token)).thenReturn(true)
        whenever(jwtService.decode(token)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                nonceClaim = null,
                targetLinkUriClaim = resourceUri
            )
        )

        assertThrows<JwtClaimValidationException> { performSecurityChecks(state, token, session) }
    }

    @Test
    fun `throws an exception when target link URI does not match what's mapped to state`() {
        val state = "state"

        session.mapStateToTargetLinkUri(state, "https://tool.com/reality")
        whenever(jwtService.isSignatureValid(token)).thenReturn(true)
        whenever(jwtService.decode(token)).thenReturn(
            DecodedJwtTokenFactory.sample(
                issuerClaim = testIssuer,
                audienceClaim = listOf(testClientId),
                targetLinkUriClaim = "https://tool.com/expectation"
            )
        )

        assertThrows<TargetLinkUriMismatchException> { performSecurityChecks(state, token, session) }
    }

    private val token = "this is a token"

    @BeforeEach
    fun initialiseVariables() {
        session = MockHttpSession()
    }

    private val testIssuer = "https://platform.com"
    private val testClientId = "tool-client-id"

    @BeforeEach
    fun insertPlatform() {
        mongoPlatformDocumentRepository.insert(
            PlatformDocumentFactory.sample(
                issuer = testIssuer,
                authenticationEndpoint = "https://platform.com/auth",
                clientId = testClientId
            )
        )
    }

    private lateinit var session: HttpSession

    @Autowired
    private lateinit var performSecurityChecks: PerformSecurityChecks

    @MockBean
    private lateinit var jwtService: JwtService
}
