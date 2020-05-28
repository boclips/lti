package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.service.ApiAccessTokenProviderTest
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class IndexControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `passes frontend url to the view`() {
        mvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(
                model().attribute(
                    "ltiTokenUrl",
                    "http://localhost:${ApiAccessTokenProviderTest.API_SERVER_PORT}/"
                )
            )
            .andExpect(
                model().attribute(
                    "apiBaseUrl",
                    "http://api.base.url"
                )
            )
    }
}
