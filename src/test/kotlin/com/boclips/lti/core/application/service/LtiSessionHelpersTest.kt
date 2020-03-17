package com.boclips.lti.core.application.service

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.application.service.LtiSessionHelpers.getIntegrationId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession

class LtiSessionHelpersTest {
    @Test
    fun `returns integration id by integrationId key`() {
        val session = MockHttpSession()

        session.setAttribute(SessionKeys.integrationId, "test-id")

        assertThat(getIntegrationId(session)).isEqualTo("test-id")
    }
}
