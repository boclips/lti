package com.boclips.lti.v1p3.presentation

import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.application.service.LtiOnePointThreeSession
import com.boclips.lti.v1p3.domain.exception.InvalidIdTokenSignatureException
import com.boclips.lti.v1p3.domain.exception.ResourceDoesNotMatchException
import com.boclips.lti.v1p3.domain.exception.UnsupportedMessageTypeException
import com.boclips.lti.v1p3.domain.service.VerifyCrossSiteRequestForgeryProtection
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.constraints.NotBlank

@Validated
@Controller
class AuthenticationResponseController(
    private val verifyCrossSiteRequestForgeryProtection: VerifyCrossSiteRequestForgeryProtection,
    private val ltiSession: LtiOnePointThreeSession,
    private val jwtService: JwtService
) {
    companion object : KLogging()

    @PostMapping("/v1p3/authentication-response")
    fun handleAuthenticationResponse(
        @NotBlank(message = "'state' parameter must not be blank")
        state: String?,
        @NotBlank(message = "'id_token' parameter must not be blank")
        @RequestParam(name = "id_token")
        idToken: String?
    ): String {
        verifyCrossSiteRequestForgeryProtection(state!!, ltiSession)

        if (!jwtService.isSignatureValid(idToken!!)) throw InvalidIdTokenSignatureException()
        val decodedToken = jwtService.decode(idToken)

        if (decodedToken.messageType != "LtiResourceLinkRequest") throw UnsupportedMessageTypeException(decodedToken.messageType!!)
        if (ltiSession.getTargetLinkUri() != decodedToken.targetLinkUri) throw ResourceDoesNotMatchException()

        logger.info { "LTI 1.3 Authentication Response Token $decodedToken" }

        ltiSession.setIntegrationId(decodedToken.issuer!!)

        return "redirect:${decodedToken.targetLinkUri}"
    }
}
