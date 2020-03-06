package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.core.infrastructure.exception.ClientNotFoundException
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
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
class MongoBackedCollectionsClientFactoryTest {
    private lateinit var preconfiguredCollectionsClient: CollectionsClient
    private lateinit var integrationDocumentRepository: MongoIntegrationDocumentRepository
    private lateinit var boclipsApiProperties: BoclipsApiProperties
    private lateinit var factory: MongoBackedCollectionsClientFactory

    @BeforeEach
    fun setupCollectionsClientFactory(@Mock integrationDocumentRepository: MongoIntegrationDocumentRepository) {
        preconfiguredCollectionsClient = CollectionsClientFake()
        this.integrationDocumentRepository = integrationDocumentRepository
        boclipsApiProperties = BoclipsApiProperties()
            .apply {
            baseUrl = "https://api.com/"
        }

        factory = MongoBackedCollectionsClientFactory(
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

        val returnedCollectionsClient = factory.getClient("miso")

        assertThat(returnedCollectionsClient)
            .isNotNull()
            .isNotEqualTo(preconfiguredCollectionsClient)
    }

    @Test
    fun `throws an exception when the consumer key does not match`() {
        assertThrows<ClientNotFoundException> { factory.getClient("ramen") }
    }
}
