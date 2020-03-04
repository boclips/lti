package com.boclips.lti.v1p1.configuration

import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.v1p1.infrastructure.service.CollectionsClientFactory
import com.boclips.lti.v1p1.infrastructure.service.MongoBackedCollectionsClientFactory
import com.boclips.lti.v1p1.infrastructure.service.MongoBackedVideosClientFactory
import com.boclips.lti.v1p1.infrastructure.service.VideosClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class ApiClientsConfig {
    @Bean
    fun videosClientFactory(
        properties: VideoServiceProperties,
        videoServiceProperties: VideoServiceProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): VideosClientFactory {
        return MongoBackedVideosClientFactory(
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }

    @Bean
    fun collectionsClientFactory(
        properties: VideoServiceProperties,
        videoServiceProperties: VideoServiceProperties,
        integrationDocumentRepository: MongoIntegrationDocumentRepository
    ): CollectionsClientFactory {
        return MongoBackedCollectionsClientFactory(
            videoServiceProperties = videoServiceProperties,
            integrationDocumentRepository = integrationDocumentRepository
        )
    }
}
