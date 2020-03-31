package com.boclips.lti.v1p3.application.service

class SecurityService {
    fun doesCsrfStateMatch(state: String, ltiSession: LtiOnePointThreeSession): Boolean {
        return ltiSession.getState() == state
    }
}
