package com.boclips.lti.v1p3.presentation

import com.boclips.lti.v1p3.application.command.HandlePlatformMessage
import com.boclips.lti.v1p3.application.exception.InvalidJwtTokenSignatureException
import com.boclips.lti.v1p3.application.exception.StatesDoNotMatchException
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.application.service.LtiOnePointThreeSession
import com.boclips.lti.v1p3.application.service.SecurityService
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.constraints.NotBlank

@Validated
@Controller
class AuthenticationResponseController(
    private val securityService: SecurityService,
    private val ltiSession: LtiOnePointThreeSession,
    private val jwtService: JwtService,
    private val handlePlatformMessage: HandlePlatformMessage
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
        if (!securityService.doesCsrfStateMatch(state!!, ltiSession)) throw StatesDoNotMatchException()
        if (!jwtService.isSignatureValid(idToken!!)) throw InvalidJwtTokenSignatureException()

        val decodedToken = jwtService.decode(idToken)

        val resourceUrl = handlePlatformMessage(decodedToken)

        return "redirect:${resourceUrl}"
    }
}
