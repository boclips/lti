package com.boclips.lti.v1p1.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class MetricsIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `exposes prometheus metrics`() {
        mvc
            .perform(MockMvcRequestBuilders.get("/actuator/prometheus"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
