package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.application.exceptions.UnauthorizedException
import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.domain.service.IsLaunchRequestValid
import org.imsglobal.aspect.Lti
import org.imsglobal.lti.launch.LtiVerificationResult
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/lti/v1p1")
class LtiOnePointOneController(
    val isLaunchRequestValid: IsLaunchRequestValid,
    val ltiProperties: LtiProperties
) {
    @Lti
    @PostMapping("", "/")
    fun handleLtiLaunchRequest(request: HttpServletRequest, result: LtiVerificationResult, session: HttpSession): ResponseEntity<Unit> {
        val responseHeaders = HttpHeaders()
        if (isLaunchRequestValid(result)) {
            session.setAttribute("resource-link-id", result.ltiLaunchResult.resourceLinkId)
            responseHeaders.location = URI(ltiProperties.landingPage)
        } else {
            responseHeaders.location = URI(ltiProperties.errorPage)
        }
        return ResponseEntity(responseHeaders, HttpStatus.SEE_OTHER)
    }

    @GetMapping("/video")
    fun getVideo(session: HttpSession): ModelAndView {
        if (session.isNew) {
            throw UnauthorizedException("Accessing videos requires a valid session")
        }
        return ModelAndView("video", mapOf("resource-link-id" to session.getAttribute("resource-link-id")))
    }
}
