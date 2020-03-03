package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
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
class ConfigAndDatabaseBackedCollectionsClientFactoryTest {
    private lateinit var preconfiguredCollectionsClient: CollectionsClient
    private lateinit var integrationDocumentRepository: MongoIntegrationDocumentRepository
    private lateinit var ltiProperties: LtiProperties
    private lateinit var videoServiceProperties: VideoServiceProperties
    private lateinit var factory: ConfigAndDatabaseBackedCollectionsClientFactory

    @BeforeEach
    fun setupCollectionsClientFactory(@Mock integrationDocumentRepository: MongoIntegrationDocumentRepository) {
        preconfiguredCollectionsClient = CollectionsClientFake()
        this.integrationDocumentRepository = integrationDocumentRepository
        ltiProperties = LtiProperties().apply {
            consumer.key = "pho"
            consumer.secret = "a secret ingredient"
        }
        videoServiceProperties = VideoServiceProperties().apply {
            baseUrl = "https://api.com/"
        }

        factory = ConfigAndDatabaseBackedCollectionsClientFactory(
            preconfiguredCollectionsClient = preconfiguredCollectionsClient,
            ltiProperties = ltiProperties,
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }

    @Test
    fun `returns the preconfigured client if it matches the preconfigured consumer`() {
        val returnedCollectionsClient = factory.getClient(ltiProperties.consumer.key)

        assertThat(returnedCollectionsClient).isEqualTo(preconfiguredCollectionsClient)
    }

    @Test
    fun `configures the client through a database when preconfigured key does not match`() {
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
