package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.exception.InvalidJwtTokenSignatureException
import com.boclips.lti.v1p3.application.exception.NonceReusedException
import com.boclips.lti.v1p3.application.exception.StatesDoNotMatchException
import com.boclips.lti.v1p3.application.service.CsrfService
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.application.service.NonceService
import com.boclips.lti.v1p3.application.validator.IdTokenValidator
import com.boclips.lti.v1p3.domain.model.SessionKeys
import com.boclips.lti.v1p3.domain.model.getMapAttribute
import mu.KLogging
import javax.servlet.http.HttpSession

class PerformSecurityChecks(
    private val csrfService: CsrfService,
    private val jwtService: JwtService,
    private val nonceService: NonceService,
    private val idTokenValidator: IdTokenValidator
) {
    companion object : KLogging()

    operator fun invoke(state: String, idToken: String, httpSession: HttpSession) {
        logger.info { httpSession.getMapAttribute(SessionKeys.statesToTargetLinkUris) }

        if (!csrfService.doesCsrfStateMatch(state, httpSession)) throw StatesDoNotMatchException(state)
        if (!jwtService.isSignatureValid(idToken)) throw InvalidJwtTokenSignatureException()

        val decodedToken = jwtService.decode(idToken)

        idTokenValidator.assertHasValidClaims(decodedToken)
            .run {
                if (nonceService.hasNonceBeenUsedAlready(decodedToken.nonceClaim!!)) throw NonceReusedException(
                    decodedToken.nonceClaim
                )
            }
            .run { nonceService.storeNonce(decodedToken.nonceClaim!!) }
    }
}
