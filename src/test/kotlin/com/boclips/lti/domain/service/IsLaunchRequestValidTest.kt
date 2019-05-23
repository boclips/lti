package com.boclips.lti.domain.service

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.imsglobal.lti.launch.LtiLaunch
import org.imsglobal.lti.launch.LtiVerificationResult
import org.junit.jupiter.api.Test

internal class IsLaunchRequestValidTest {
    private val isLaunchRequestValid: IsLaunchRequestValid = IsLaunchRequestValid()

    @Test
    fun `should return false when verification result is not successful`() {
        val result = mock<LtiVerificationResult> {
            on { success } doReturn false
        }
        assertThat(isLaunchRequestValid(result)).isEqualTo(false)
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

        assertThat(isLaunchRequestValid(result)).isEqualTo(false)
    }

    @Test
    fun `should return false when verification result is successful, but resource_link_id is empty`() {
        val launch = mock<LtiLaunch> {
            on { resourceLinkId } doReturn ""
        }
        val result = mock<LtiVerificationResult> {
            on { success } doReturn true
            on { ltiLaunchResult } doReturn launch
        }

        assertThat(isLaunchRequestValid(result)).isEqualTo(false)
    }

    @Test
    fun `should return true when verification result is successful and resource_link_id is non-empty`() {
        val launch = mock<LtiLaunch> {
            on { resourceLinkId } doReturn "a resource link identifier"
        }
        val result = mock<LtiVerificationResult> {
            on { success } doReturn true
            on { ltiLaunchResult } doReturn launch
        }

        assertThat(isLaunchRequestValid(result)).isEqualTo(true)
    }
}
