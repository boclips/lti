package com.boclips.lti.core.presentation

import com.boclips.lti.core.configuration.properties.DevSupportProperties
import com.boclips.lti.core.configuration.properties.FrontendProperties
import com.boclips.lti.v1p3.domain.model.getUserId
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
class FrontendController(
    private val frontendProperties: FrontendProperties,
    private val devSupportProperties: DevSupportProperties
) {
    @GetMapping("/search")
    fun getSearch(session: HttpSession): ModelAndView {
        /*
         * Search here points to search.html in the front end repo
         * Gradle will compile frontend files and move them /resources/static in the jar at build time
         */
        return ModelAndView(
            "search", mapOf(
                "ltiBaseUrl" to frontendProperties.ltiBaseUrl,
                "userId" to session.getUserId(),
                "apiBaseUrl" to frontendProperties.apiBaseUrl,
                "initialiseDevelopmentSession" to devSupportProperties.initialiseDevelopmentSession,
                "developmentSessionUrl" to devSupportProperties.developmentSessionUrl
            )
        )
    }

    @GetMapping("/search-and-embed")
    fun getSearchAndEmbed(session: HttpSession): ModelAndView {
        /*
         * search-and-embed here points to search-and-embed.html in the front end repo
         * Gradle will compile frontend files and move them /resources/static in the jar at build time
         */
        return ModelAndView(
            "search-and-embed", mapOf(
                "ltiBaseUrl" to frontendProperties.ltiBaseUrl,
                "apiBaseUrl" to frontendProperties.apiBaseUrl,
                "userId" to session.getUserId(),
                "initialiseDevelopmentSession" to devSupportProperties.initialiseDevelopmentSession,
                "developmentSessionUrl" to devSupportProperties.developmentSessionUrl
            )
        )
    }
}
