package com.boclips.lti.v1p3.presentation

import com.boclips.lti.v1p3.application.command.HandlePlatformRequest
import com.boclips.lti.v1p3.application.command.PerformSecurityChecks
import com.boclips.lti.v1p3.application.command.SetUpSession
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.model.getBoclipsUserId
import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpSession
import javax.validation.constraints.NotBlank

@Validated
@Controller
class AuthenticationResponseController(
    private val handlePlatformRequest: HandlePlatformRequest,
) {
    companion object : KLogging()

    @PostMapping("/v1p3/authentication-response")
    fun handleAuthenticationResponse(
        state: String?,
        @RequestParam(name = "id_token")
        idToken: String?,
        httpSession: HttpSession,
        @RequestAttribute("decodedToken")
        decodedToken: DecodedJwtToken
    ): ResponseEntity<Nothing> {
        logger.info { "LTI 1.3 Authentication Response from iss: '${decodedToken.issuerClaim}' for '${decodedToken.targetLinkUriClaim}' }" }

        val resourceUrl = handlePlatformRequest(decodedToken, httpSession, state!!)

        return seeOtherRedirect(resourceUrl)
    }
}
