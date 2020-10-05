package com.boclips.lti.v1p3.presentation

import com.boclips.lti.v1p3.application.command.HandlePlatformRequest
import com.boclips.lti.v1p3.application.command.PerformSecurityChecks
import com.boclips.lti.v1p3.application.command.SynchroniseUser
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.model.getBoclipsUserId
import com.boclips.lti.v1p3.domain.model.setBoclipsUserId
import mu.KLogging
import org.springframework.http.ResponseEntity
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
    private val handlePlatformRequest: HandlePlatformRequest,
    private val performSecurityChecks: PerformSecurityChecks,
    private val synchroniseUser: SynchroniseUser
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
    ): ResponseEntity<Nothing> {
        if (httpSession.getBoclipsUserId().isNullOrBlank()) {
            performSecurityChecks(state!!, idToken!!, httpSession)
        }
        val decodedToken = jwtService.decode(idToken!!)
        logger.info { "LTI 1.3 Authentication Response from iss: '${decodedToken.issuerClaim}' for '${decodedToken.targetLinkUriClaim}' }" }

        httpSession.setBoclipsUserId(synchroniseUser(
            integrationId = decodedToken.issuerClaim!!,
            username = decodedToken.subjectClaim!!,
            deploymentId = decodedToken.deploymentIdClaim!!
        ))

        val resourceUrl = handlePlatformRequest(decodedToken, httpSession, state!!)

        return seeOtherRedirect(resourceUrl)
    }
}
