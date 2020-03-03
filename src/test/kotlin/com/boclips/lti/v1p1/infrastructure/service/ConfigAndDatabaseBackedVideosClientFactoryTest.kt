package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.model.IntegrationDocument
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
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
class ConfigAndDatabaseBackedVideosClientFactoryTest {
    private lateinit var preconfiguredVideosClient: VideosClient
    private lateinit var mongoIntegrationDocumentRepository: MongoIntegrationDocumentRepository
    private lateinit var ltiProperties: LtiProperties
    private lateinit var videoServiceProperties: VideoServiceProperties
    private lateinit var factory: ConfigAndDatabaseBackedVideosClientFactory

    @BeforeEach
    fun setupVideosClientFactory(@Mock mongoIntegrationDocumentRepository: MongoIntegrationDocumentRepository) {
        preconfiguredVideosClient = VideosClientFake()
        ltiProperties = LtiProperties().apply {
            consumer.key = "pho"
            consumer.secret = "a secret ingredient"
        }
        videoServiceProperties = VideoServiceProperties().apply {
            baseUrl = "https://api.com/"
        }
        this.mongoIntegrationDocumentRepository = mongoIntegrationDocumentRepository

        factory = ConfigAndDatabaseBackedVideosClientFactory(
            preconfiguredVideosClient = preconfiguredVideosClient,
            ltiProperties = ltiProperties,
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = this.mongoIntegrationDocumentRepository
        )
    }

    @Test
    fun `returns the preconfigured VideosClient for the preconfigured consumer key`() {
        val returnedVideosClient = factory.getClient(ltiProperties.consumer.key)

        assertThat(returnedVideosClient).isEqualTo(preconfiguredVideosClient)
    }

    @Test
    fun `looks the client configuration up in the database when preconfigured key does not match`() {
        val integrationDocument = IntegrationDocument(
            id = ObjectId(),
            integrationId = "miso",
            clientId = "super",
            clientSecret = "secret"
        )
        whenever(mongoIntegrationDocumentRepository.findOneByIntegrationId("miso")).thenReturn(integrationDocument)

        val returnedVideosClient = factory.getClient("miso")

        assertThat(returnedVideosClient)
            .isNotNull()
            .isNotEqualTo(preconfiguredVideosClient)
    }

    @Test
    fun `throws an exception when the consumer key does not match`() {
        assertThrows<ClientNotFoundException> { factory.getClient("ramen") }
    }
}
