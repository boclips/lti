package com.boclips.lti.v1p1.configuration.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class VideoServicePropertiesTest {
  @Test
  fun `returns validation errors when video service token uri is not blank`() {
    properties.accessTokenUri = ""

    assertThat(validator.validate(properties)).hasSize(1)
  }

  @Test
  fun `returns validation errors when video service client id is blank`() {
    properties.clientId = ""

    assertThat(validator.validate(properties)).hasSize(1)
  }

  @Test
  fun `returns validation errors when video service secret is blank`() {
    properties.clientSecret = ""

    assertThat(validator.validate(properties)).hasSize(1)
  }

  @Test
  fun `does not return validation errors when given valid object`() {
    assertThat(validator.validate(properties)).isEmpty()
  }

  lateinit var properties: VideoServiceProperties
  lateinit var validator: Validator

  @BeforeEach
  fun setup() {
    val factory = Validation.buildDefaultValidatorFactory()
    validator = factory.getValidator()

    properties = VideoServiceProperties()
    properties.accessTokenUri = "http://localhost/auth/token"
    properties.clientId = "test-id"
    properties.clientSecret = "test-secret"
  }
}
