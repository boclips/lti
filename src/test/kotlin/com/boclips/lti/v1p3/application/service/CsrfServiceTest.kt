package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.domain.model.mapStateToTargetLinkUri
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession

class CsrfServiceTest {
    private val securityService = CsrfService()

    @Test
    fun `returns false when given state does not match session state`() {
        val session = MockHttpSession()
        session.mapStateToTargetLinkUri("expectation", "https://tool.com/")

        val result = securityService.doesCsrfStateMatch(state = "reality", session = session)

        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `returns true when states match`() {
        val session = MockHttpSession()
        session.mapStateToTargetLinkUri("state", "https://tool.com/")

        val result = securityService.doesCsrfStateMatch(state = "state", session = session)

        assertThat(result).isEqualTo(true)
    }
}
