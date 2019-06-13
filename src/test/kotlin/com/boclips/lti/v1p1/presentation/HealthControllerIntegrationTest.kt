package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class HealthControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `health endpoint is available`() {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk)
    }
}
