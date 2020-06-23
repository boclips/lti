package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.domain.model.containsMappingForState
import javax.servlet.http.HttpSession

class CsrfService {
    fun doesCsrfStateMatch(state: String, session: HttpSession): Boolean {
        return session.containsMappingForState(state)
    }
}
