package com.boclips.lti.presentation

import com.boclips.lti.configuration.LtiContext
import org.imsglobal.aspect.Lti
import org.imsglobal.lti.launch.LtiVerificationResult
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v1/lti")
class LtiController {
    @Lti
    @PostMapping("", "/")
    fun handleLtiLaunchRequest(request: HttpServletRequest, result: LtiVerificationResult): ResponseEntity<Unit> {
        val responseHeaders = HttpHeaders()
        if (result.success) {
            // TODO Establish a user session
            responseHeaders.location = URI(LtiContext.LANDING_PAGE)
        } else {
            responseHeaders.location = URI(LtiContext.ERROR_PAGE)
        }
        return ResponseEntity(responseHeaders, HttpStatus.SEE_OTHER)
    }
}
