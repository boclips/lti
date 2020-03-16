package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.v1p1.domain.model.LaunchParams
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpSession
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@ExtendWith(MockitoExtension::class)
class InitializeLtiSessionTest {
    @Test
    fun `sets custom logo if one is provided in the request`(@Mock request: HttpServletRequest) {
        val logoUri = "https://imagez.net/custom/logo.png"
        whenever(request.getParameter(LaunchParams.Custom.LOGO)).thenReturn(logoUri)

        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(SessionKeys.customLogo)).isEqualTo(logoUri)
    }

    @Test
    fun `does not set custom logo if empty string is provided`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(LaunchParams.Custom.LOGO)).thenReturn("")

        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(SessionKeys.customLogo)).isNull()
    }

    @Test
    fun `does not set custom logo if whitespace string is provided`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(LaunchParams.Custom.LOGO)).thenReturn("       ")

        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(SessionKeys.customLogo)).isNull()
    }

    @Test
    fun `does not set custom logo if custom_logo parameter is not provided`(@Mock request: HttpServletRequest) {
        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(SessionKeys.customLogo)).isNull()
    }

    @Test
    fun `sets user id on the session`(@Mock request: HttpServletRequest) {
        val userId = "abc123"
        whenever(request.getParameter(any())).thenReturn(userId)

        initializeLtiSession.handleUserId(request, session)

        assertThat(session.getAttribute(SessionKeys.userId)).isEqualTo(userId)
    }

    @Test
    fun `does not set user id on the session if it's not in the request`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(any())).thenReturn(null)

        initializeLtiSession.handleUserId(request, session)

        assertThat(session.getAttribute(SessionKeys.userId)).isEqualTo(null)
    }

    @Test
    fun `sets the integrationId on the session`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(eq("oauth_consumer_key"))).thenReturn("test-consumer-key")

        initializeLtiSession.handleConsumerKey(request, session)

        assertThat(session.getAttribute(SessionKeys.integrationId)).isEqualTo("test-consumer-key")
    }

    private val initializeLtiSession = InitializeLtiSession()

    private lateinit var session: HttpSession

    @BeforeEach
    fun setUp() {
        session = MockHttpSession()
    }
}
