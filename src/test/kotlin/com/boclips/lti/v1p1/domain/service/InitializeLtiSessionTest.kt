package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.domain.model.LaunchParams
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.authenticationStateHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.consumerKeyHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.customLogoHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.userIdHolder
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
    fun `marks the session as authenticated`(@Mock request: HttpServletRequest) {
        initializeLtiSession.markAsAuthenticated(session)

        assertThat(session.getAttribute(authenticationStateHolder)).isEqualTo(true)
    }

    @Test
    fun `sets custom logo if one is provided in the request`(@Mock request: HttpServletRequest) {
        val logoUri = "https://imagez.net/custom/logo.png"
        whenever(request.getParameter(LaunchParams.Custom.LOGO)).thenReturn(logoUri)

        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(customLogoHolder)).isEqualTo(logoUri)
    }

    @Test
    fun `does not set custom logo if empty string is provided`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(LaunchParams.Custom.LOGO)).thenReturn("")

        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(customLogoHolder)).isNull()
    }

    @Test
    fun `does not set custom logo if whitespace string is provided`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(LaunchParams.Custom.LOGO)).thenReturn("       ")

        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(customLogoHolder)).isNull()
    }

    @Test
    fun `does not set custom logo if custom_logo parameter is not provided`(@Mock request: HttpServletRequest) {
        initializeLtiSession.handleLogo(request, session)

        assertThat(session.getAttribute(customLogoHolder)).isNull()
    }

    @Test
    fun `sets user id on the session`(@Mock request: HttpServletRequest) {
        val userId = "abc123"
        whenever(request.getParameter(any())).thenReturn(userId)

        initializeLtiSession.handleUserId(request, session)

        assertThat(session.getAttribute(userIdHolder)).isEqualTo(userId)
    }

    @Test
    fun `does not set user id on the session if it's not in the request`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(any())).thenReturn(null)

        initializeLtiSession.handleUserId(request, session)

        assertThat(session.getAttribute(userIdHolder)).isEqualTo(null)
    }

    @Test
    fun `sets the consumerKey on the session`(@Mock request: HttpServletRequest) {
        whenever(request.getParameter(eq("oauth_consumer_key"))).thenReturn("test-consumer-key")

        initializeLtiSession.handleConsumerKey(request, session)

        assertThat(session.getAttribute(consumerKeyHolder)).isEqualTo("test-consumer-key")
    }

    val initializeLtiSession = InitializeLtiSession()

    lateinit var session: HttpSession

    @BeforeEach
    fun setUp() {
        session = MockHttpSession()
    }
}
