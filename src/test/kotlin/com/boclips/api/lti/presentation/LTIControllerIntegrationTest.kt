package com.boclips.api.lti.presentation

import com.boclips.api.lti.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LTIControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `endpoint responds with Hello, World!`() {
        val responseEntity = restTemplate.postForEntity("/v1", null, String::class.java)
        assertThat(responseEntity.body).isEqualTo("Hello, World!")
    }
}
