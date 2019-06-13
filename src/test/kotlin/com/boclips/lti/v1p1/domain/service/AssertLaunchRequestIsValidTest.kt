package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.domain.exception.LaunchRequestInvalidException
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.imsglobal.lti.launch.LtiLaunch
import org.imsglobal.lti.launch.LtiVerificationResult
import org.junit.jupiter.api.Test

internal class AssertLaunchRequestIsValidTest {
    private val assertLaunchRequestIsValid: AssertLaunchRequestIsValid = AssertLaunchRequestIsValid()

    @Test
    fun `should return false when verification result is not successful`() {
        val result = mock<LtiVerificationResult> {
            on { success } doReturn false
        }
        assertThatThrownBy { assertLaunchRequestIsValid(result) }
            .isInstanceOf(LaunchRequestInvalidException::class.java)
            .hasMessageContaining("LTI launch verification failed")
    }

    @Test
    fun `should return false when verification result is successful, but resource_link_id is null`() {
        val launch = mock<LtiLaunch> {
            on { resourceLinkId } doReturn null
        }
        val result = mock<LtiVerificationResult> {
            on { success } doReturn true
            on { ltiLaunchResult } doReturn launch
        }

        assertThatThrownBy { assertLaunchRequestIsValid(result) }
            .isInstanceOf(LaunchRequestInvalidException::class.java)
            .hasMessageContaining("LTI resource link id was not provided")
    }

    @Test
    fun `should throw exception when verification result is successful, but resource_link_id is empty`() {
        val launch = mock<LtiLaunch> {
            on { resourceLinkId } doReturn ""
        }
        val result = mock<LtiVerificationResult> {
            on { success } doReturn true
            on { ltiLaunchResult } doReturn launch
        }

        assertThatThrownBy { assertLaunchRequestIsValid(result) }
            .isInstanceOf(LaunchRequestInvalidException::class.java)
            .hasMessageContaining("LTI resource link id was not provided")
    }

    @Test
    fun `should not throw when verification result is successful and resource_link_id is non-empty`() {
        val launch = mock<LtiLaunch> {
            on { resourceLinkId } doReturn "a resource link identifier"
        }
        val result = mock<LtiVerificationResult> {
            on { success } doReturn true
            on { ltiLaunchResult } doReturn launch
        }

        assertThatCode { assertLaunchRequestIsValid(result) }.doesNotThrowAnyException()
    }
}
