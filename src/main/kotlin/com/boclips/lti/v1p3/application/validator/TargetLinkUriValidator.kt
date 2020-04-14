package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.model.getTargetLinkUri
import com.boclips.lti.v1p3.domain.exception.TargetLinkUriMismatchException
import javax.servlet.http.HttpSession

object TargetLinkUriValidator {
    fun assertTargetLinkUriMatchesSessionState(decodedJwtToken: DecodedJwtToken, session: HttpSession, state: String) {
        if (decodedJwtToken.targetLinkUriClaim != session.getTargetLinkUri(state)) throw TargetLinkUriMismatchException()
    }
}
