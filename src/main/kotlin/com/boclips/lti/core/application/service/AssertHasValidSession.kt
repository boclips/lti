package com.boclips.lti.core.application.service

import com.boclips.lti.core.application.exception.UnauthorizedException
import com.boclips.lti.core.application.model.SessionKeys.authenticationState
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSession

@Service
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
