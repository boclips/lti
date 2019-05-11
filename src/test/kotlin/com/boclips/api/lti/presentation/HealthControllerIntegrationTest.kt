package com.boclips.api.lti.presentation

import com.boclips.api.lti.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class HealthControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `health endpoint is available`() {
        val responseEntity = restTemplate.getForEntity("/actuator/health", Map::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }
}
