package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.application.exception.MissingSessionAttributeException
import com.boclips.lti.v1p3.domain.model.SessionKeys
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.SessionScope
import javax.servlet.http.HttpSession
import com.boclips.lti.core.application.model.SessionKeys as CoreSessionKeys

@Component
@SessionScope
class LtiOnePointThreeSession(private val httpSession: HttpSession) {
    fun getState() =
        httpSession.getAttribute(SessionKeys.state) as? String ?: throw MissingSessionAttributeException("state")

    fun getTargetLinkUri() = httpSession.getAttribute(SessionKeys.targetLinkUri) as? String
        ?: throw MissingSessionAttributeException("targetLinkUri")

    fun setIntegrationId(value: String) = httpSession.setAttribute(CoreSessionKeys.integrationId, value)
}
