package com.boclips.lti.v1p3.application.command

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.testsupport.factories.NonceDocumentFactory
import com.boclips.lti.v1p3.application.exception.InvalidJwtTokenSignatureException
import com.boclips.lti.v1p3.application.exception.NonceReusedException
import com.boclips.lti.v1p3.application.exception.StatesDoNotMatchException
import com.boclips.lti.v1p3.application.exception.TokenClaimValidationException
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.model.SessionKeys
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
        val token = "this is a token"
        val nonceValue = "nonce"

        session.setAttribute(SessionKeys.state, state)
        whenever(jwtService.isSignatureValid(token)).thenReturn(true)
        whenever(jwtService.decode(token)).thenReturn(
            DecodedJwtTokenFactory.sample(
                nonceClaim = nonceValue
            )
        )

        assertDoesNotThrow { performSecurityChecks(state, token, session) }

        val nonce = mongoNonceDocumentRepository.findOneByValue(nonceValue)!!
        assertThat(nonce.value).isEqualTo(nonceValue)
        assertThat(nonce.createdAt).isCloseTo(now(), within(10, ChronoUnit.SECONDS))
    }

    @Test
    fun `throws an exception when states do not match`() {
        session.setAttribute(SessionKeys.state, "reality")

        assertThrows<StatesDoNotMatchException> { performSecurityChecks("expectation", "token", session) }
    }

    @Test
    fun `throws an exception when token signature does not match`() {
        val state = "state"
        val token = "this is a token"

        session.setAttribute(SessionKeys.state, state)
        whenever(jwtService.isSignatureValid(token)).thenReturn(false)

        assertThrows<InvalidJwtTokenSignatureException> { performSecurityChecks(state, token, session) }
    }

    @Test
    fun `throws an exception when nonce has already been used`() {
        val state = "state"
        val token = "this is a token"

        session.setAttribute(SessionKeys.state, state)
        whenever(jwtService.isSignatureValid(token)).thenReturn(true)
        whenever(jwtService.decode(token)).thenReturn(
            DecodedJwtTokenFactory.sample(
                nonceClaim = "nonce"
            )
        )

        mongoNonceDocumentRepository.insert(NonceDocumentFactory.sample(value = "nonce"))

        assertThrows<NonceReusedException> { performSecurityChecks(state, token, session) }
    }

    @Test
    fun `throws an exception when nonce is not provided on the token`() {
        val state = "state"
        val token = "this is a token"

        session.setAttribute(SessionKeys.state, state)
        whenever(jwtService.isSignatureValid(token)).thenReturn(true)
        whenever(jwtService.decode(token)).thenReturn(
            DecodedJwtTokenFactory.sample(
                nonceClaim = null
            )
        )

        assertThrows<TokenClaimValidationException> { performSecurityChecks(state, token, session) }
    }

    @BeforeEach
    fun initialiseVariables() {
        session = MockHttpSession()
    }

    private lateinit var session: HttpSession

    @Autowired
    private lateinit var performSecurityChecks: PerformSecurityChecks

    @MockBean
    private lateinit var jwtService: JwtService
}
