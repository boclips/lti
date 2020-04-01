package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.exception.TokenClaimValidationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import java.net.URL

object ResourceLinkMessageValidator {
    fun assertContainsAllRequiredClaims(token: DecodedJwtToken) {
        if (token.ltiVersionClaim != "1.3.0") throw TokenClaimValidationException("LTI version should be 1.3.0")
        if (token.deploymentIdClaim.isNullOrBlank()) throw TokenClaimValidationException("Deployment ID was not provided")
        if (token.targetLinkUriClaim == null || isNotValidUrl(token.targetLinkUriClaim)) throw TokenClaimValidationException(
            "Target Link URI should be a valid URI"
        )
        if (token.resourceLinkClaim?.id.isNullOrBlank()) throw TokenClaimValidationException("Resource Link ID was not provided")
    }

    private fun isNotValidUrl(string: String): Boolean {
        return try {
            URL(string)
            false
        } catch (_: Exception) {
            return true
        }
    }
}
