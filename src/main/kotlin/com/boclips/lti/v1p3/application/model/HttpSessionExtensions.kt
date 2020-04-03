package com.boclips.lti.v1p3.application.model

import com.boclips.lti.v1p3.application.exception.MissingSessionAttributeException
import com.boclips.lti.v1p3.domain.model.SessionKeys
import javax.servlet.http.HttpSession

fun HttpSession.getState(): String =
    (this.getAttribute(SessionKeys.state) ?: throw MissingSessionAttributeException("state")) as String

fun HttpSession.setTargetLinkUri(value: String) = this.setAttribute(SessionKeys.targetLinkUri, value)

fun HttpSession.getTargetLinkUri(): String =
    (this.getAttribute(SessionKeys.targetLinkUri) ?: throw MissingSessionAttributeException("targetLinkUri")) as String

fun HttpSession.setIntegrationId(value: String) =
    this.setAttribute(com.boclips.lti.core.application.model.SessionKeys.integrationId, value)

fun HttpSession.getIntegrationId(): String =
    (this.getAttribute(com.boclips.lti.core.application.model.SessionKeys.integrationId)
        ?: throw MissingSessionAttributeException("integrationId")) as String
