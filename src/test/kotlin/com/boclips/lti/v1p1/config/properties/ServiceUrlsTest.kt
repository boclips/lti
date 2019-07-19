package com.boclips.lti.v1p1.config.properties

import com.boclips.lti.v1p1.configuration.properties.ServiceUrls
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class ServiceUrlsTest {
  @Test
  fun `validation fails if given null value`() {
    val validationResult = validator.validate(serviceUrls)

    assertThat(validationResult)
      .hasSize(1)
      .extracting("message")
      .containsExactly("must not be null")
  }

  @Test
  fun `validation fails if given non-URL value`() {
    serviceUrls.apiUrl = "gibberish"

    val validationResult = validator.validate(serviceUrls)

    assertThat(validationResult)
      .hasSize(1)
      .extracting("message")
      .containsExactly("must be a valid URL")
  }

  private lateinit var validator: Validator
  private lateinit var serviceUrls: ServiceUrls

  @BeforeEach
  fun setup() {
    validator = Validation.buildDefaultValidatorFactory().validator
    serviceUrls = ServiceUrls()
  }
}
