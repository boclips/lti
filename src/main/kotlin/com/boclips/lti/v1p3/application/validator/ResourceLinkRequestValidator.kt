package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.exception.LtiMessageClaimValidationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.domain.exception.TargetLinkUriMismatchException
import com.boclips.lti.v1p3.domain.model.getTargetLinkUri
import javax.servlet.http.HttpSession

object ResourceLinkRequestValidator {
    fun assertIsValid(state: String, token: DecodedJwtToken, session: HttpSession) {
        assertContainsAllRequiredClaims(token)
        assertTargetLinkUriMatchesSessionState(
            session = session,
            state = state,
            decodedJwtToken = token
        )
    }

    private fun assertContainsAllRequiredClaims(token: DecodedJwtToken) {
        if (token.ltiVersionClaim != "1.3.0") throw LtiMessageClaimValidationException("LTI version should be 1.3.0")
        if (token.targetLinkUriClaim == null || !token.targetLinkUriClaim.isURLString()) throw LtiMessageClaimValidationException(
            "Target Link URI should be a valid URI"
        )
        if (token.resourceLinkClaim?.id.isNullOrBlank()) throw LtiMessageClaimValidationException("Resource Link ID was not provided")
    }

    private fun assertTargetLinkUriMatchesSessionState(
        decodedJwtToken: DecodedJwtToken,
        session: HttpSession,
        state: String
    ) {
        if (decodedJwtToken.targetLinkUriClaim != session.getTargetLinkUri(state)) throw TargetLinkUriMismatchException()
    }
}
