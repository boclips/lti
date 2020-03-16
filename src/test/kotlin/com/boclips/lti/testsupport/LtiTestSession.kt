package com.boclips.lti.testsupport

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import org.springframework.mock.web.MockHttpSession
import javax.servlet.http.HttpSession

object LtiTestSession {
    const val TEST_INTEGRATION = "test-integration"

    fun authenticated(integrationId: String = TEST_INTEGRATION, sessionAttributes: Map<String, Any> = emptyMap()) =
        sessionWithAuthenticationState(
            integration = integrationId,
            authenticationState = true,
            sessionAttributes = sessionAttributes
        )

    fun unauthenticated() = sessionWithAuthenticationState(
        authenticationState = false,
        sessionAttributes = emptyMap()
    )

    private fun sessionWithAuthenticationState(
        integration: String? = null,
        authenticationState: Boolean,
        sessionAttributes: Map<String, Any>
    ): HttpSession {
        val session = MockHttpSession()
        session.setAttribute(SessionKeys.authenticationState, authenticationState)
        session.setAttribute(consumerKey, integration)

        sessionAttributes.entries.forEach { entry ->
            session.setAttribute(entry.key, entry.value)
        }

        return session
    }
}
