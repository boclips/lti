package com.boclips.lti.core.presentation

import com.boclips.lti.core.configuration.properties.DevSupportProperties
import com.boclips.lti.core.configuration.properties.FrontendProperties
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class IndexController(
    private val frontendProperties: FrontendProperties,
    private val devSupportProperties: DevSupportProperties
) {
    @GetMapping("/search")
    fun getIndex(): ModelAndView {
        /*
         * Index here points to index.html in the front end repo
         * Gradle will compile frontend files and move them /resources/static in the jar at build time
         */
        return ModelAndView(
            "index", mapOf(
                "ltiTokenUrl" to frontendProperties.ltiTokenUrl,
                "apiBaseUrl" to frontendProperties.apiBaseUrl,
                "initialiseDevelopmentSession" to devSupportProperties.initialiseDevelopmentSession,
                "developmentSessionUrl" to devSupportProperties.developmentSessionUrl
            )
        )
    }
}
