package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.model.IntegrationDocument
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
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
    private lateinit var videoServiceProperties: VideoServiceProperties
    private lateinit var factory: MongoBackedCollectionsClientFactory

    @BeforeEach
    fun setupCollectionsClientFactory(@Mock integrationDocumentRepository: MongoIntegrationDocumentRepository) {
        preconfiguredCollectionsClient = CollectionsClientFake()
        this.integrationDocumentRepository = integrationDocumentRepository
        videoServiceProperties = VideoServiceProperties().apply {
            baseUrl = "https://api.com/"
        }

        factory = MongoBackedCollectionsClientFactory(
            videoServiceProperties = videoServiceProperties,
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
