package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import com.boclips.lti.v1p1.infrastructure.repository.LtiOnePointOneConsumerRepository
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CompositeLtiKeySecretServiceIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    private lateinit var composite: CompositeLtiKeySecretService

    @Autowired
    lateinit var ltiOnePointOneConsumerRepository: LtiOnePointOneConsumerRepository

    @Test
    fun `returns the mongo secret if one is found`() {
        ltiOnePointOneConsumerRepository.insert(
            LtiOnePointOneConsumerDocument(
                id = ObjectId(),
                key = ltiProperties.consumer.key,
                secret = "this-is-a-mongo-secret"
            )
        )

        val secret = composite.getSecretForKey(ltiProperties.consumer.key)

        assertThat(secret).isEqualTo("this-is-a-mongo-secret")
    }

    @Test
    fun `returns preconfigured secret if none is found in mongo`() {
        val secret = composite.getSecretForKey(ltiProperties.consumer.key)

        assertThat(secret).isEqualTo(ltiProperties.consumer.secret)
    }

    @Test
    fun `returns null when key is not found in both`() {
        val secret = composite.getSecretForKey("this-wont-be-found")

        assertThat(secret).isNull()
    }
}
