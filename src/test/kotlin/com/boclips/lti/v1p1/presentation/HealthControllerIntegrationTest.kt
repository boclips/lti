package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
class HealthControllerIntegrationTest : AbstractSpringIntegrationTest() {

    @Test
    fun `health endpoint is available`() {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk)
    }
}
