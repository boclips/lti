package com.boclips.lti.v1p3.presentation

import com.boclips.lti.v1p3.application.command.HandlePlatformMessage
import com.boclips.lti.v1p3.application.command.PerformSecurityChecks
import com.boclips.lti.v1p3.application.service.JwtService
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpSession
import javax.validation.constraints.NotBlank

@Validated
@Controller
class AuthenticationResponseController(
    private val jwtService: JwtService,
    private val handlePlatformMessage: HandlePlatformMessage,
    private val performSecurityChecks: PerformSecurityChecks
) {
    companion object : KLogging()

    @PostMapping("/v1p3/authentication-response")
    fun handleAuthenticationResponse(
        @NotBlank(message = "'state' parameter must not be blank")
        state: String?,
        @NotBlank(message = "'id_token' parameter must not be blank")
        @RequestParam(name = "id_token")
        idToken: String?,
        httpSession: HttpSession
    ): String {
        performSecurityChecks(state!!, idToken!!, httpSession)

        val decodedToken = jwtService.decode(idToken)

        val resourceUrl = handlePlatformMessage(decodedToken, httpSession)

        return "redirect:${resourceUrl}"
    }
}
