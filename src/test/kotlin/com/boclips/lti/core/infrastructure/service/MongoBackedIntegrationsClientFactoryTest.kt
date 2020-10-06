package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.exception.ClientNotFoundException
import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.users.api.httpclient.IntegrationsClient
import com.boclips.users.api.httpclient.test.fakes.IntegrationsClientFake
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MongoBackedIntegrationsClientFactoryTest {
    private lateinit var preconfiguredIntegrationsClient: IntegrationsClient
    private lateinit var integrationDocumentRepository: MongoIntegrationDocumentRepository
    private lateinit var boclipsApiProperties: BoclipsApiProperties
    private lateinit var factory: MongoBackedIntegrationsClientFactory

    @BeforeEach
    fun setupUsersClientFactory(@Mock integrationDocumentRepository: MongoIntegrationDocumentRepository) {
        preconfiguredIntegrationsClient = IntegrationsClientFake()
        this.integrationDocumentRepository = integrationDocumentRepository
        boclipsApiProperties = BoclipsApiProperties()
            .apply {
                baseUrl = "https://api.com/"
            }

        factory = MongoBackedIntegrationsClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }

    @Test
    fun `configures the client through a database`() {
        val integrationDocument = IntegrationDocument(
            id = ObjectId(),
            integrationId = "miso",
            clientId = "super",
            clientSecret = "secret"
        )
        whenever(integrationDocumentRepository.findOneByIntegrationId("miso")).thenReturn(integrationDocument)

        val returnedIntegrationsClient = factory.getClient("miso")

        assertThat(returnedIntegrationsClient)
            .isNotNull()
            .isNotEqualTo(preconfiguredIntegrationsClient)
    }

    @Test
    fun `throws an exception when the consumer key does not match`() {
        assertThrows<ClientNotFoundException> { factory.getClient("ramen") }
    }
}
