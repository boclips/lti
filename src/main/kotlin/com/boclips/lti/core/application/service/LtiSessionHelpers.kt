package com.boclips.lti.core.application.service

import com.boclips.lti.core.application.model.SessionKeys
import javax.servlet.http.HttpSession

object LtiSessionHelpers {
    fun getIntegrationId(session: HttpSession): String {
        return session.getAttribute(SessionKeys.integrationId)
            .let { it as String }
    }
}
