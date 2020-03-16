package com.boclips.lti.testsupport

import com.boclips.lti.core.application.model.SessionKeys
import org.springframework.mock.web.MockHttpSession
import javax.servlet.http.HttpSession

object LtiTestSession {
    fun authenticated(integrationId: String, sessionAttributes: Map<String, Any> = emptyMap()) =
        sessionWithAuthenticationState(
            integrationId = integrationId,
            sessionAttributes = sessionAttributes
        )

    fun unauthenticated() = sessionWithAuthenticationState(
        sessionAttributes = emptyMap()
    )

    private fun sessionWithAuthenticationState(
        integrationId: String? = null,
        sessionAttributes: Map<String, Any>
    ): HttpSession {
        val session = MockHttpSession()
        session.setAttribute(SessionKeys.integrationId, integrationId)

        sessionAttributes.entries.forEach { entry ->
            session.setAttribute(entry.key, entry.value)
        }

        return session
    }
}
