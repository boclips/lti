package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.domain.model.SessionKeys
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession

class CsrfServiceTest {
    private val securityService = CsrfService()

    @Test
    fun `returns false when given state does not match session state`() {
        val session = MockHttpSession()
        session.setAttribute(SessionKeys.state, "expectation")

        val result = securityService.doesCsrfStateMatch(state = "reality", session = session)

        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `returns true when states match`() {
        val session = MockHttpSession()
        session.setAttribute(SessionKeys.state, "state")

        val result = securityService.doesCsrfStateMatch(state = "state", session = session)

        assertThat(result).isEqualTo(true)
    }
}
