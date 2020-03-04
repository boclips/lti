package com.boclips.lti.v1p1.testsupport

import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.authenticationStateHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.consumerKeyHolder
import org.springframework.mock.web.MockHttpSession
import javax.servlet.http.HttpSession

object LtiTestSession {
    const val TEST_CONSUMER_KEY = "test-consumer-key"

    fun getValid() = sessionWithAuthenticationState(true)

    fun getInvalid() = sessionWithAuthenticationState(false)

    private fun sessionWithAuthenticationState(authenticationState: Boolean?): HttpSession {
        val session = MockHttpSession()
        session.setAttribute(authenticationStateHolder, authenticationState)
        session.setAttribute(consumerKeyHolder, TEST_CONSUMER_KEY)
        return session
    }
}
