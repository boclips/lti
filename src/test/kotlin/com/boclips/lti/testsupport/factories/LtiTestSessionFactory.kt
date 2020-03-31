package com.boclips.lti.testsupport.factories

import org.springframework.mock.web.MockHttpSession
import javax.servlet.http.HttpSession

object LtiTestSessionFactory {
    fun authenticated(integrationId: String, sessionAttributes: Map<String, Any> = emptyMap()) =
        sessionWithAuthenticationState(
            sessionAttributes = sessionAttributes + ("integrationId" to integrationId)
        )

    fun unauthenticated(sessionAttributes: Map<String, Any> = emptyMap()) =
        sessionWithAuthenticationState(
            sessionAttributes = sessionAttributes
        )

    private fun sessionWithAuthenticationState(
        sessionAttributes: Map<String, Any>
    ): HttpSession {
        val session = MockHttpSession()

        sessionAttributes.entries.forEach { entry ->
            session.setAttribute(entry.key, entry.value)
        }

        return session
    }
}
