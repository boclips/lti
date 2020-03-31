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
    fun getState(): String =
        (httpSession.getAttribute(SessionKeys.state) ?: throw MissingSessionAttributeException("state")) as String

    fun setTargetLinkUri(value: String) = httpSession.setAttribute(SessionKeys.targetLinkUri, value)

    fun getTargetLinkUri(): String = (httpSession.getAttribute(SessionKeys.targetLinkUri)
        ?: throw MissingSessionAttributeException("targetLinkUri")) as String

    fun setIntegrationId(value: String) = httpSession.setAttribute(CoreSessionKeys.integrationId, value)

    fun getIntegrationId(): String = (httpSession.getAttribute(CoreSessionKeys.integrationId)
        ?: throw MissingSessionAttributeException("integrationId")) as String
}
