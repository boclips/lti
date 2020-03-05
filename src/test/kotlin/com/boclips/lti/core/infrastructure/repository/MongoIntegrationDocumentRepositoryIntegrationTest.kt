package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

class MongoIntegrationDocumentRepositoryIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `returns an integration when it's found by integrationId`() {
        val newIntegrationDocument = IntegrationDocument(
            id = ObjectId(),
            integrationId = "a-splendid-lti-integration",
            clientId = "id",
            clientSecret = "secret"
        )
        integrationDocumentRepository.insert(
            newIntegrationDocument
        )

        val retrievedDocument = integrationDocumentRepository.findOneByIntegrationId("a-splendid-lti-integration")

        assertThat(retrievedDocument).isEqualTo(newIntegrationDocument)
    }

    @Test
    fun `returns null if the integration is not found`() {
        val document = integrationDocumentRepository.findOneByIntegrationId("this won't be there")

        assertThat(document).isNull()
    }
}
