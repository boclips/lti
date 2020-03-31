package com.boclips.lti.v1p3.application.service

import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SecurityServiceTest {
    private val securityService = SecurityService()

    @Test
    fun `returns false when given state does not match session state`(@Mock ltiSession: LtiOnePointThreeSession) {
        whenever(ltiSession.getState()).thenReturn("expectation")

        val result = securityService.doesCsrfStateMatch(state = "reality", ltiSession = ltiSession)

        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `returns true when states match`(@Mock ltiSession: LtiOnePointThreeSession) {
        whenever(ltiSession.getState()).thenReturn("state")

        val result = securityService.doesCsrfStateMatch(state = "state", ltiSession = ltiSession)

        assertThat(result).isEqualTo(true)
    }
}
