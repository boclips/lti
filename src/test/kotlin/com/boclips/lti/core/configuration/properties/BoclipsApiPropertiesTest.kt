package com.boclips.lti.core.configuration.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class BoclipsApiPropertiesTest {
    @Test
    fun `returns validation errors when base url is blank`() {
        val properties = BoclipsApiProperties()
            .apply {
            baseUrl = ""
            tokenUrl = "http://localhost/v1/token"
        }

        assertThat(validator.validate(properties)).hasSize(1)
    }

    @Test
    fun `returns validation errors when token url is blank`() {
        val properties = BoclipsApiProperties()
            .apply {
            baseUrl = "http://localhost/v1"
            tokenUrl = ""
        }

        assertThat(validator.validate(properties)).hasSize(1)
    }

    @Test
    fun `does not return validation errors when given valid object`() {
        val properties = BoclipsApiProperties()
            .apply {
            baseUrl = "http://localhost/v1"
            tokenUrl = "http://localhost/v1/token"
        }

        assertThat(validator.validate(properties)).isEmpty()
    }

    private lateinit var validator: Validator

    @BeforeEach
    fun setup() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }
}
