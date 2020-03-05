package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.application.model.SessionKeys.authenticationState
import com.boclips.lti.v1p1.domain.model.LaunchParams
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Service
class InitializeLtiSession {
    operator fun invoke(request: HttpServletRequest, session: HttpSession) {
        markAsAuthenticated(session)
        handleLogo(request, session)
        handleUserId(request, session)
        handleConsumerKey(request, session)
    }

    fun markAsAuthenticated(session: HttpSession) {
        session.setAttribute(authenticationState, true)
    }

    fun handleLogo(request: HttpServletRequest, session: HttpSession) {
        val customLogo: String = request.getParameter(LaunchParams.Custom.LOGO).orEmpty()
        if (customLogo.isNotBlank()) {
            session.setAttribute(SessionKeys.customLogo, customLogo)
        }
    }

    fun handleUserId(request: HttpServletRequest, session: HttpSession) {
        val userId: String = request.getParameter(LaunchParams.Custom.USER_ID).orEmpty()
        if (userId.isNotBlank()) {
            session.setAttribute(SessionKeys.userId, userId)
        }
    }

    fun handleConsumerKey(request: HttpServletRequest, session: HttpSession) {
        val consumerKey: String = request.getParameter(LaunchParams.Lti.CONSUMER_KEY).orEmpty()
        session.setAttribute(SessionKeys.consumerKey, consumerKey)
    }
}
