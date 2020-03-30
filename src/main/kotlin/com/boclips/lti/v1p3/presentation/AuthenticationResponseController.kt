package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.JWT
import com.boclips.lti.v1p3.application.service.LtiOnePointThreeSession
import com.boclips.lti.v1p3.domain.exception.ResourceDoesNotMatchException
import com.boclips.lti.v1p3.domain.service.JwtVerificationService
import com.boclips.lti.v1p3.domain.service.VerifyCrossSiteRequestForgeryProtection
import com.boclips.lti.v1p3.presentation.exception.InvalidSignatureException
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
    private val jwtVerificationService: JwtVerificationService
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

        val decodedToken = JWT.decode(idToken)
        if (!jwtVerificationService.verifySignature(idToken!!)) {
            throw InvalidSignatureException()
        }

        val targetLinkUri =
            decodedToken.getClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri").asString()!!

        if (ltiSession.getTargetLinkUri() != targetLinkUri) throw ResourceDoesNotMatchException()

        logger.info { "LTI 1.3 Authentication Response Token ${decodedToken.token}" }

        ltiSession.setIntegrationId(decodedToken.issuer)

        return "redirect:${ltiSession.getTargetLinkUri()}"
    }
}
