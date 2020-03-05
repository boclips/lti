package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class MongoLtiKeySecretServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var mongoLtiKeySecretService: MongoLtiKeySecretService

    @Test
    fun `returns a secret corresponding to the provided key`() {
        ltiOnePointOneConsumerRepository.insert(
            LtiOnePointOneConsumerDocument(
                id = ObjectId(),
                key = "test-key",
                secret = "test-secret"
            )
        )

        val secret = mongoLtiKeySecretService.getSecretForKey("test-key")

        assertThat(secret).isEqualTo("test-secret")
    }

    @Test
    fun `returns a null when no consumer is found for a given key`() {
        val secret = mongoLtiKeySecretService.getSecretForKey("this-consumer-does-not-exist")

        assertThat(secret).isNull()
    }

    @Test
    fun `returns null when key is null`() {
        val secret = mongoLtiKeySecretService.getSecretForKey(null)

        assertThat(secret).isNull()
    }
}
