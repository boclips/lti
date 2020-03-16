package com.boclips.lti.core.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.LtiTestSession
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class WebAssetsIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `can retrieve the stylesheets`() {
        val session = LtiTestSession.authenticated(
            integrationId = "test-integration"
        )

        mvc.perform(get("/styles/main.css").session(session as MockHttpSession))
            .andExpect(status().isOk)
        mvc.perform(get("/styles/fonts.css").session(session))
            .andExpect(status().isOk)
        mvc.perform(get("/styles/normalize.css").session(session))
            .andExpect(status().isOk)
    }
}
