package com.boclips.lti.v1p3.configuration.properties

import com.boclips.lti.testsupport.factories.RsaKeyPairPropertiesFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.validation.Validation

class SigningKeysPropertiesTest {
    @Test
    fun `does not return validation errors when given valid properties`() {
        val properties = SigningKeysProperties().apply {
            signingKeys = listOf(RsaKeyPairPropertiesFactory.sample())
        }

        assertThat(validator.validate(properties)).isEmpty()
    }

    @Test
    fun `returns validation errors when given an empty list`() {
        val properties = SigningKeysProperties().apply { signingKeys = emptyList() }

        val validationResult = validator.validate(properties)

        assertThat(validationResult).hasSize(1)
        assertThat(validationResult.first().message).contains("must not be empty")
    }

    @Test
    fun `returns validation errors when given a list with invalid object`() {
        val properties = SigningKeysProperties().apply {
            signingKeys = listOf(RsaKeyPairPropertiesFactory.sample(publicKey = ""))
        }

        val validationResult = validator.validate(properties)

        assertThat(validationResult).hasSize(1)
        assertThat(validationResult.first().message).contains("must not be blank")
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator
}
