package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.domain.model.LaunchParams
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Service
class InitializeLtiSession {
    companion object {
        const val authenticationStateHolder = "isAuthenticated"
        const val customLogoHolder = "customLogo"
        const val userIdHolder = "userId"
        const val consumerKeyHolder = "consumerKey"
    }

    operator fun invoke(request: HttpServletRequest, session: HttpSession) {
        markAsAuthenticated(session)
        handleLogo(request, session)
        handleUserId(request, session)
        handleConsumerKey(request, session)
    }

    fun markAsAuthenticated(session: HttpSession) {
        session.setAttribute(authenticationStateHolder, true)
    }

    fun handleLogo(request: HttpServletRequest, session: HttpSession) {
        val customLogo: String = request.getParameter(LaunchParams.Custom.LOGO).orEmpty()
        if (customLogo.isNotBlank()) {
            session.setAttribute(customLogoHolder, customLogo)
        }
    }

    fun handleUserId(request: HttpServletRequest, session: HttpSession) {
        val userId: String = request.getParameter(LaunchParams.Custom.USER_ID).orEmpty()
        if (userId.isNotBlank()) {
            session.setAttribute(userIdHolder, userId)
        }
    }

    fun handleConsumerKey(request: HttpServletRequest, session: HttpSession) {
        val consumerKey: String = request.getParameter(LaunchParams.Lti.CONSUMER_KEY).orEmpty()
        session.setAttribute(consumerKeyHolder, consumerKey)
    }
}
