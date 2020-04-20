package com.boclips.lti.v1p3.presentation

import com.boclips.lti.v1p3.application.command.AssembleLoginRequestUrl
import mu.KLogging
import org.hibernate.validator.constraints.URL
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpSession
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Controller
class InitiateLoginController(
    private val assembleLoginRequestUrl: AssembleLoginRequestUrl
) {
    companion object : KLogging()

    @RequestMapping(
        method = [RequestMethod.GET, RequestMethod.POST],
        path = ["/v1p3/initiate-login"]
    )
    fun initiateLogin(
        @NotNull(message = "'iss' parameter must not be blank")
        @URL(protocol = "https", message = "'iss' parameter must be a valid HTTPS URL")
        @RequestParam("iss")
        issuer: String?,
        @NotBlank(message = "'login_hint' parameter must not be blank")
        @RequestParam("login_hint")
        loginHint: String?,
        @NotNull(message = "'target_link_uri' parameter must not be blank")
        @URL(message = "'target_link_uri' must be a valid URL")
        @RequestParam("target_link_uri")
        targetLinkUri: String?,
        @RequestParam("lti_message_hint")
        ltiMessageHint: String?,
        session: HttpSession
    ): String {
        logger.info { "LTI 1.3 Login Initiation Request from $issuer for $targetLinkUri" }

        val authenticationRequestUrl = assembleLoginRequestUrl(
            issuer = issuer!!,
            loginHint = loginHint!!,
            targetLinkUri = targetLinkUri!!,
            ltiMessageHint = ltiMessageHint,
            session = session
        )

        return "redirect:$authenticationRequestUrl"
    }
}
