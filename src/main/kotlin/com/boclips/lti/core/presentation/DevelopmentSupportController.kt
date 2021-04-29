package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.configuration.properties.DevSupportProperties
import mu.KLogging
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/dev-support")
@Profile(value = ["local", "test"])
class DevelopmentSupportController(private val devSupportProperties: DevSupportProperties) {
    companion object : KLogging()

    @CrossOrigin(originPatterns = ["*"], allowCredentials = "true")
    @PostMapping("/initialise-session")
    fun handleLtiLaunchRequest(
        request: HttpServletRequest,
        session: HttpSession
    ) {
        session.setAttribute(SessionKeys.integrationId, devSupportProperties.integrationId)
    }
}
