package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.exception.TokenClaimValidationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken

object ResourceLinkMessageValidator {
    fun assertContainsAllRequiredClaims(token: DecodedJwtToken) {
        if (token.ltiVersionClaim != "1.3.0") throw TokenClaimValidationException("LTI version should be 1.3.0")
    }
}
