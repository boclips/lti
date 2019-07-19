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
        session.setAttribute(authenticationStateHolder, true)

        logger.info { "Launch parameters received:" }
        request.parameterMap.entries.forEach { logger.info { "${it.key} = ${it.value?.contentToString()}" } }

        val customLogo: String = request.getParameter(CustomLaunchParams.LOGO).orEmpty()
        if (customLogo.isNotBlank()) {
            session.setAttribute(customLogoHolder, customLogo)
        }
    }
}
