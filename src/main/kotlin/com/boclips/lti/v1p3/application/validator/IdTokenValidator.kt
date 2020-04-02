package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.exception.TokenClaimValidationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken

object IdTokenValidator {
    fun assertHasNonce(token: DecodedJwtToken) {
        token.nonceClaim ?: throw TokenClaimValidationException("Nonce was not provided")
    }
}
