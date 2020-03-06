package com.boclips.lti.core.application.service

import com.boclips.lti.core.application.exception.UnauthorizedException
import com.boclips.lti.core.application.model.SessionKeys.authenticationState
import javax.servlet.http.HttpSession

class AssertHasValidSession {
    operator fun invoke(session: HttpSession?) {
        val isAuthenticated: Boolean = session?.getAttribute(authenticationState)?.let {
            (it as Boolean)
        } ?: false

        if (!isAuthenticated) {
            throw UnauthorizedException("A valid session is required")
        }
    }
}
