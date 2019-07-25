package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.authenticationStateHolder
import org.springframework.mock.web.MockHttpSession
import javax.servlet.http.HttpSession

object LtiTestSession {
    fun getValid() = sessionWithAuthenticationState(true)

    fun getInvalid() = sessionWithAuthenticationState(false)

    private fun sessionWithAuthenticationState(authenticationState: Boolean?): HttpSession {
        val session = MockHttpSession()
        session.setAttribute(authenticationStateHolder, authenticationState)
        return session
    }
}
