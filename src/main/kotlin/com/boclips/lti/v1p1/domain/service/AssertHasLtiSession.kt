package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.application.exception.UnauthorizedException
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.authenticationStateHolder
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSession

@Service
class AssertHasLtiSession {
    operator fun invoke(session: HttpSession?) {
        val isAuthenticated: Boolean = session?.getAttribute(authenticationStateHolder)?.let {
            (it as Boolean)
        } ?: false

        if (!isAuthenticated) {
            throw UnauthorizedException("Accessing videos requires a valid session")
        }
    }
}
