package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class HealthControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `health endpoint is available`() {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk)
    }
}
