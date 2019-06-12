package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.application.exceptions.UnauthorizedException
import com.boclips.lti.v1p1.application.service.VideoUrlFor
import com.boclips.lti.v1p1.domain.service.AssertLaunchRequestIsValid
import mu.KLogging
import org.imsglobal.aspect.Lti
import org.imsglobal.lti.launch.LtiVerificationResult
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/v1p1")
class LtiOnePointOneController(
    val assertLaunchRequestIsValid: AssertLaunchRequestIsValid,
    val videoUrlFor: VideoUrlFor
) {
    companion object : KLogging()

    private val authenticationStateHolder = "isAuthenticated"

    @Lti
    @PostMapping("", "/")
    fun handleLtiLaunchRequest(
        request: HttpServletRequest,
        result: LtiVerificationResult,
        session: HttpSession
    ): ResponseEntity<Unit> {
        logger.info { "Received request: ${request.method} ${request.requestURL}" }

        assertLaunchRequestIsValid(result)

        val responseHeaders = HttpHeaders()
        session.setAttribute(authenticationStateHolder, true)
        responseHeaders.location = URI("${request.requestURI}/videos/${result.ltiLaunchResult.resourceLinkId}")
        return ResponseEntity(responseHeaders, HttpStatus.SEE_OTHER)
    }

    @GetMapping("/videos/{videoId}")
    fun getVideo(session: HttpSession, @PathVariable("videoId") videoId: String): ModelAndView {
        val isAuthenticated: Boolean = session.getAttribute(authenticationStateHolder)?.let {
            (it as Boolean)
        } ?: false

        if (!isAuthenticated) {
            throw UnauthorizedException("Accessing videos requires a valid session")
        }

        return ModelAndView(
            "video", mapOf(
                "videoUrl" to videoUrlFor(videoId)
            )
        )
    }
}
