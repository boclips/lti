package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.v1p1.domain.model.LaunchParams
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

class InitializeLtiSession {
    operator fun invoke(request: HttpServletRequest, session: HttpSession) {
        handleLogo(request, session)
        handleUserId(request, session)
        handleConsumerKey(request, session)
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
        session.setAttribute(SessionKeys.integrationId, consumerKey)
    }
}
