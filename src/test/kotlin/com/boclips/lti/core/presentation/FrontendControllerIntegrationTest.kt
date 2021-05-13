package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.service.ApiAccessTokenProviderTest
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class FrontendControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `passes frontend url and user id to the search view`() {

        val session = LtiTestSessionFactory.authenticated(
            integrationId = "blah",
            sessionAttributes = mapOf("boclipsUserId" to "user-123")
        )

        mvc.perform(get("/search").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(view().name("search"))
            .andExpect(
                model().attribute(
                    "ltiBaseUrl",
                    "http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}"
                )
            )
            .andExpect(
                model().attribute(
                    "apiBaseUrl",
                    "http://api.base.url"
                )
            ).andExpect(
                model().attribute(
                    "initialiseDevelopmentSession",
                    false
                )
            ).andExpect(
                model().attribute(
                    "developmentSessionUrl",
                    ""
                )
            ).andExpect(
                model().attribute(
                    "userId",
                    "user-123"
                )
            )
    }

    @Test
    fun `passes frontend url and user id to the search and embed view`() {

        val session = LtiTestSessionFactory.authenticated(
            integrationId = "blah",
            sessionAttributes = mapOf("boclipsUserId" to "user-123")
        )

        mvc.perform(get("/search-and-embed").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(view().name("search-and-embed"))
            .andExpect(
                model().attribute(
                    "ltiBaseUrl",
                    "http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}"
                )
            )
            .andExpect(
                model().attribute(
                    "apiBaseUrl",
                    "http://api.base.url"
                )
            ).andExpect(
                model().attribute(
                    "initialiseDevelopmentSession",
                    false
                )
            ).andExpect(
                model().attribute(
                    "developmentSessionUrl",
                    ""
                )
            ).andExpect(
                model().attribute(
                    "userId",
                    "user-123"
                )
            )
    }

    @Test
    fun `is allowed to retrieve static js anc css resources`() {
        // These assets are compiled and copied into resources/static at build time
        mvc.perform(get("/main.js"))
            .andExpect(status().isNotFound)
        mvc.perform(get("/main.js.gz"))
            .andExpect(status().isNotFound)
        mvc.perform(get("/main.js.map"))
            .andExpect(status().isNotFound)
        mvc.perform(get("/main.css"))
            .andExpect(status().isNotFound)
        mvc.perform(get("/main.css.gz"))
            .andExpect(status().isNotFound)
    }
}
