package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.domain.model.CustomLaunchParams
import mu.KLogging
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Service
class InitializeLtiSession {
    companion object : KLogging() {
        const val authenticationStateHolder = "isAuthenticated"
        const val customLogoHolder = "customLogo"
    }

    operator fun invoke(request: HttpServletRequest, session: HttpSession) {
        markAsAuthenticated(session)
        handleLogo(request, session)
    }

    private fun markAsAuthenticated(session: HttpSession) {
        session.setAttribute(authenticationStateHolder, true)
    }

    private fun handleLogo(request: HttpServletRequest, session: HttpSession) {
        val customLogo: String = request.getParameter(CustomLaunchParams.LOGO).orEmpty()
        if (customLogo.isNotBlank()) {
            logger.info { "custom_logo = $customLogo" }
            session.setAttribute(customLogoHolder, customLogo)
        }
    }
}
