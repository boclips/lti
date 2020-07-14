package com.boclips.lti.v1p3.configuration.properties

import com.boclips.lti.testsupport.factories.RsaKeyPairPropertiesFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.validation.Validation

class RsaKeyPairPropertiesTest {
    @Test
    fun `does not return validation errors when given an instance with values`() {
        val keyPair = RsaKeyPairPropertiesFactory.sample()

        assertThat(validator.validate(keyPair)).isEmpty()
    }

    @Test
    fun `returns validation errors when given negative number as generation timestamp`() {
        val keyPair = RsaKeyPairPropertiesFactory.sample(generationTimestamp = -1)

        val result = validator.validate(keyPair)

        assertThat(result).hasSize(1)
        assertThat(result.first().message).contains("must be greater than or equal to 0")
    }

    @Test
    fun `returns validation errors when given a blank private key`() {
        val keyPair = RsaKeyPairPropertiesFactory.sample(privateKey = "")

        val result = validator.validate(keyPair)

        assertThat(result).hasSize(1)
        assertThat(result.first().message).contains("must not be blank")
    }

    @Test
    fun `returns validation errors when given a blank public key`() {
        val keyPair = RsaKeyPairPropertiesFactory.sample(publicKey = "")

        val result = validator.validate(keyPair)

        assertThat(result).hasSize(1)
        assertThat(result.first().message).contains("must not be blank")
    }

    private val validator = Validation.buildDefaultValidatorFactory().validator
}
