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
        const val userIdHolder = "userId"
    }

    operator fun invoke(request: HttpServletRequest, session: HttpSession) {
        markAsAuthenticated(session)
        handleLogo(request, session)
        handleUserId(request, session)
    }

    private fun markAsAuthenticated(session: HttpSession) {
        session.setAttribute(authenticationStateHolder, true)
    }

    private fun handleLogo(request: HttpServletRequest, session: HttpSession) {
        val customLogo: String = request.getParameter(CustomLaunchParams.LOGO).orEmpty()
        if (customLogo.isNotBlank()) {
            session.setAttribute(customLogoHolder, customLogo)
        }
    }

    private fun handleUserId(request: HttpServletRequest, session: HttpSession) {
        val userId: String = request.getParameter(CustomLaunchParams.USER_ID).orEmpty()
        if (userId.isNotBlank()) {
            session.setAttribute(userIdHolder, userId)
        }
    }
}
