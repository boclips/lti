package com.boclips.lti.core.application.service

import com.boclips.lti.core.application.exception.UnauthorizedException
import com.boclips.lti.core.application.model.SessionKeys
import javax.servlet.http.HttpSession

class AssertHasValidSession {
    operator fun invoke(session: HttpSession?) {
        val hasIntegrationIdSet =
            session
                ?.getAttribute(SessionKeys.integrationId)
                ?.let { (it as String).isNotBlank() }
                ?: false

        if (!hasIntegrationIdSet) {
            throw UnauthorizedException("A valid session is required")
        }
    }
}
