package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.v1p3.application.service.LtiOnePointThreeSession
import com.boclips.lti.v1p3.domain.exception.StatesDoNotMatchException

class VerifyCrossSiteRequestForgeryProtection {
    operator fun invoke(state: String, ltiSession: LtiOnePointThreeSession) {
        if (ltiSession.getState() != state) throw StatesDoNotMatchException()
    }
}
