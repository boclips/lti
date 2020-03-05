package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import org.springframework.mock.web.MockHttpSession
import javax.servlet.http.HttpSession

object LtiTestSession {
    const val TEST_CONSUMER_KEY = "test-consumer-key"

    fun getValid() = sessionWithAuthenticationState(true)

    fun getInvalid() = sessionWithAuthenticationState(false)

    private fun sessionWithAuthenticationState(authenticationState: Boolean?): HttpSession {
        val session = MockHttpSession()
        session.setAttribute(SessionKeys.authenticationState, authenticationState)
        session.setAttribute(consumerKey, TEST_CONSUMER_KEY)
        return session
    }
}
