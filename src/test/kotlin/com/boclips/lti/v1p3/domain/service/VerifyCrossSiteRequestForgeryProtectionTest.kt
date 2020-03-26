package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.v1p3.application.service.LtiOnePointThreeSession
import com.boclips.lti.v1p3.domain.exception.StatesDoNotMatchException
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class VerifyCrossSiteRequestForgeryProtectionTest {
    val verifyCrossSiteRequestForgeryProtection = VerifyCrossSiteRequestForgeryProtection()

    @Test
    fun `throws an exception when given state does not match session state`(@Mock ltiSession: LtiOnePointThreeSession) {
        whenever(ltiSession.getState()).thenReturn("expectation")

        assertThrows<StatesDoNotMatchException> {
            verifyCrossSiteRequestForgeryProtection(
                state = "reality",
                ltiSession = ltiSession
            )
        }
    }

    @Test
    fun `does not throw when states match`(@Mock ltiSession: LtiOnePointThreeSession) {
        whenever(ltiSession.getState()).thenReturn("state")

        assertDoesNotThrow {
            verifyCrossSiteRequestForgeryProtection(
                state = "state",
                ltiSession = ltiSession
            )
        }
    }
}
