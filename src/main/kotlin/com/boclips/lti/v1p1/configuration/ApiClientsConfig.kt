package com.boclips.lti.v1p1.configuration

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.v1p1.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.v1p1.infrastructure.service.VideosClientFactory
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class ApiClientsConfig {
    @Bean
    fun videosClientFactory(
        properties: VideoServiceProperties,
        ltiProperties: LtiProperties,
        videoServiceProperties: VideoServiceProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): VideosClientFactory {
        return VideosClientFactory(
            preconfiguredVideosClient = VideosClient.create(
                apiUrl = properties.baseUrl,
                tokenFactory = ServiceAccountTokenFactory(
                    ServiceAccountCredentials(
                        // TODO Use token URI?
                        properties.baseUrl,
                        properties.clientId,
                        properties.clientSecret
                    )
                )
            ),
            ltiProperties = ltiProperties,
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }

    @Bean
    fun collectionsClientFactory(
        properties: VideoServiceProperties,
        ltiProperties: LtiProperties,
        videoServiceProperties: VideoServiceProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): CollectionsClientFactory {
        return CollectionsClientFactory(
            preconfiguredCollectionsClient = CollectionsClient.create(
                apiUrl = properties.baseUrl,
                tokenFactory = ServiceAccountTokenFactory(
                    ServiceAccountCredentials(
                        // TODO Use token URI?
                        properties.baseUrl,
                        properties.clientId,
                        properties.clientSecret
                    )
                )
            ),
            ltiProperties = ltiProperties,
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }
}
