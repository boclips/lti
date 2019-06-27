package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.domain.model.CustomLaunchParams
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Service
class InitializeLtiSession {
    companion object {
        const val authenticationStateHolder = "isAuthenticated"
        const val customLogoHolder = "customLogo"
    }

    operator fun invoke(request: HttpServletRequest, session: HttpSession) {
        session.setAttribute(authenticationStateHolder, true)

        val customLogo: String = request.getParameter(CustomLaunchParams.LOGO).orEmpty()
        if (customLogo.isNotBlank()) {
            session.setAttribute(customLogoHolder, customLogo)
        }
    }
}
