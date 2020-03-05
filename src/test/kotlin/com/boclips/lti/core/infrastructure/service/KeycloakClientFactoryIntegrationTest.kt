package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.core.infrastructure.exception.IntegrationNotFoundException
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class KeycloakClientFactoryIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var factory: KeycloakClientFactory

    @Test
    fun `creates a keycloak client for existing integration`() {
        val integrationId = "test-integration"
        integrationDocumentRepository.insert(
            IntegrationDocument(
                id = ObjectId(),
                integrationId = integrationId,
                clientId = "id",
                clientSecret = "secret"
            )
        )

        val keycloakClient = factory.getKeycloakClient(integrationId)

        assertThat(keycloakClient).isNotNull()
    }

    @Test
    fun `throws an exception when integration is not found`() {
        assertThrows<IntegrationNotFoundException> { factory.getKeycloakClient("this does not exist") }
    }
}
