package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.domain.service.AssertLaunchRequestIsValid
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession
import com.boclips.lti.v1p1.domain.service.RedirectToRequestedResource
import mu.KLogging
import org.imsglobal.aspect.Lti
import org.imsglobal.lti.launch.LtiVerificationResult
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/v1p1/**")
class LtiOnePointOneController(
    private val assertLaunchRequestIsValid: AssertLaunchRequestIsValid,
    private val initializeLtiSession: InitializeLtiSession,
    private val redirectToRequestedResource: RedirectToRequestedResource
) {
    companion object : KLogging()

    @Lti
    @PostMapping
    fun handleLtiLaunchRequest(
        request: HttpServletRequest,
        result: LtiVerificationResult,
        session: HttpSession
    ): ResponseEntity<Unit> {
        logger.info { "LTI 1.1 Launch Request: ${request.method} ${request.requestURL}" }

        assertLaunchRequestIsValid(result)
        initializeLtiSession(request, session)

        return redirectToRequestedResource(request)
    }
}
