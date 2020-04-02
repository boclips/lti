package com.boclips.lti.v1p3.application.service

class CsrfService {
    fun doesCsrfStateMatch(state: String, ltiSession: LtiOnePointThreeSession): Boolean {
        return ltiSession.getState() == state
    }
}
